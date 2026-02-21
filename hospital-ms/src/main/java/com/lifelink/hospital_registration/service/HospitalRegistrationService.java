package com.lifelink.hospital_registration.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.lifelink.hospital_registration.dto.HospitalRegistrationRequest;
import com.lifelink.hospital_registration.dto.HospitalRegistrationResponse;
import com.lifelink.hospital_registration.model.Hospital;
import com.lifelink.hospital_registration.util.PasswordEncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class HospitalRegistrationService {

    private final Firestore firestore;
    private final LocalStorageService localStorageService; // ✅ changed
    private final PasswordEncryptionUtil passwordEncryptionUtil;

    private static final String COLLECTION_NAME = Hospital.COLLECTION_NAME;

    public HospitalRegistrationResponse registerHospital(HospitalRegistrationRequest request) {
        HospitalRegistrationResponse response = new HospitalRegistrationResponse();

        try {
            // Check email uniqueness
            if (isEmailAlreadyRegistered(request.getOfficialEmail())) {
                response.setSuccess(false);
                response.setMessage("Email is already registered");
                response.setErrorCode("EMAIL_EXISTS");
                return response;
            }

            // Check registration ID uniqueness
            if (isRegistrationIdAlreadyRegistered(request.getRegistrationId())) {
                response.setSuccess(false);
                response.setMessage("Registration ID already exists");
                response.setErrorCode("REGISTRATION_ID_EXISTS");
                return response;
            }

            // ✅ Save document locally instead of Firebase Storage
            String documentUrl = localStorageService.save(
                    request.getVerificationDoc(),
                    request.getInstitutionName()
            );

            // Create hospital record
            Hospital hospital = new Hospital();
            hospital.setInstitutionName(request.getInstitutionName());
            hospital.setRegistrationId(request.getRegistrationId());
            hospital.setInstitutionType(request.getInstitutionType());
            hospital.setContactPerson(request.getContactPerson());
            hospital.setOfficialEmail(request.getOfficialEmail());
            hospital.setContactNumber(request.getContactNumber());
            hospital.setPhysicalAddress(request.getPhysicalAddress());
            hospital.setPassword(passwordEncryptionUtil.encryptPassword(request.getPassword()));
            hospital.setFacilities(request.getFacilities());
            hospital.setVerificationDocUrl(documentUrl);
            hospital.setVerificationDocName(
                    request.getVerificationDoc() != null ? request.getVerificationDoc().getOriginalFilename() : null
            );
            hospital.setRegistrationDate(System.currentTimeMillis());
            hospital.setLastUpdated(System.currentTimeMillis());
            hospital.setStatus("PENDING");
            hospital.setCreatedBy("SYSTEM");

            // Save to Firestore
            String documentId = UUID.randomUUID().toString();
            hospital.setId(documentId);

            Map<String, Object> hospitalData = convertToMap(hospital);
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(documentId);
            docRef.set(hospitalData).get();

            response.setSuccess(true);
            response.setMessage("Hospital registered successfully");
            response.setData(hospital);

            log.info("Hospital registered successfully with ID: {}", documentId);

        } catch (Exception e) {
            log.error("Error registering hospital", e);
            response.setSuccess(false);
            response.setMessage("Registration failed: " + e.getMessage());
            response.setErrorCode("INTERNAL_ERROR");
        }

        return response;
    }

    private boolean isEmailAlreadyRegistered(String email) throws ExecutionException, InterruptedException {
        CollectionReference hospitals = firestore.collection(COLLECTION_NAME);
        Query query = hospitals.whereEqualTo("officialEmail", email);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return !querySnapshot.get().isEmpty();
    }

    private boolean isRegistrationIdAlreadyRegistered(String registrationId)
            throws ExecutionException, InterruptedException {
        CollectionReference hospitals = firestore.collection(COLLECTION_NAME);
        Query query = hospitals.whereEqualTo("registrationId", registrationId);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return !querySnapshot.get().isEmpty();
    }

    private Map<String, Object> convertToMap(Hospital hospital) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", hospital.getId());
        map.put("institutionName", hospital.getInstitutionName());
        map.put("registrationId", hospital.getRegistrationId());
        map.put("institutionType", hospital.getInstitutionType());
        map.put("contactPerson", hospital.getContactPerson());
        map.put("officialEmail", hospital.getOfficialEmail());
        map.put("contactNumber", hospital.getContactNumber());
        map.put("physicalAddress", hospital.getPhysicalAddress());
        map.put("password", hospital.getPassword());

        Map<String, Boolean> facilitiesMap = new HashMap<>();
        if (hospital.getFacilities() != null) {
            facilitiesMap.put("bloodCollection", hospital.getFacilities().isBloodCollection());
            facilitiesMap.put("organHarvesting", hospital.getFacilities().isOrganHarvesting());
            facilitiesMap.put("icuFacilities", hospital.getFacilities().isIcuFacilities());
            facilitiesMap.put("emergencyCare", hospital.getFacilities().isEmergencyCare());
            facilitiesMap.put("labAnalysis", hospital.getFacilities().isLabAnalysis());
        }
        map.put("facilities", facilitiesMap);

        map.put("verificationDocUrl", hospital.getVerificationDocUrl());
        map.put("verificationDocName", hospital.getVerificationDocName());
        map.put("registrationDate", hospital.getRegistrationDate());
        map.put("lastUpdated", hospital.getLastUpdated());
        map.put("status", hospital.getStatus());
        map.put("createdBy", hospital.getCreatedBy());

        return map;
    }
}