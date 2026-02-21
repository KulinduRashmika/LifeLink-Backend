package com.lifelink.hospital_registration.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.lifelink.hospital_registration.dto.HospitalLoginRequest;
import com.lifelink.hospital_registration.dto.HospitalLoginResponse;
import com.lifelink.hospital_registration.model.Hospital;
import com.lifelink.hospital_registration.util.PasswordEncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HospitalAuthService {

    private final Firestore firestore;
    private final PasswordEncryptionUtil passwordEncryptionUtil;

    public HospitalLoginResponse login(HospitalLoginRequest request) {
        try {
            // Find by email
            Query query = firestore.collection(Hospital.COLLECTION_NAME)
                    .whereEqualTo("officialEmail", request.getEmail())
                    .limit(1);

            ApiFuture<QuerySnapshot> future = query.get();
            QuerySnapshot snapshot = future.get();

            if (snapshot.isEmpty()) {
                return new HospitalLoginResponse(false, "Invalid email or password", "INVALID_CREDENTIALS",
                        null, null, null, null);
            }

            List<QueryDocumentSnapshot> docs = snapshot.getDocuments();
            QueryDocumentSnapshot doc = docs.get(0);

            Map<String, Object> data = doc.getData();

            String encryptedPassword = (String) data.get("password");
            String status = (String) data.get("status");
            String institutionName = (String) data.get("institutionName");
            String institutionType = (String) data.get("institutionType");

            // Password check
            boolean ok = passwordEncryptionUtil.verifyPassword(request.getPassword(), encryptedPassword);
            if (!ok) {
                return new HospitalLoginResponse(false, "Invalid email or password", "INVALID_CREDENTIALS",
                        null, null, null, null);
            }

            // ✅ Only allow verified/approved institutions
            // Choose ONE status string and use it everywhere.
            // I recommend "APPROVED".
            if (status == null || !status.equalsIgnoreCase("APPROVED")) {
                return new HospitalLoginResponse(false,
                        "Your institution is not verified yet. Please wait for approval.",
                        "NOT_VERIFIED",
                        doc.getId(), institutionName, institutionType, status);
            }

            // Success
            return new HospitalLoginResponse(true, "Login successful", null,
                    doc.getId(), institutionName, institutionType, status);

        } catch (Exception e) {
            log.error("Hospital login error", e);
            return new HospitalLoginResponse(false, "Login failed: " + e.getMessage(), "INTERNAL_ERROR",
                    null, null, null, null);
        }
    }
}