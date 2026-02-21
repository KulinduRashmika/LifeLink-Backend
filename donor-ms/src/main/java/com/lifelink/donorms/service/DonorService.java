package com.lifelink.donorms.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.lifelink.donorms.model.Donor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.lifelink.donorms.model.Donation;
import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class DonorService {

    private static final String COLLECTION_NAME = "donors";

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==========================
    // REGISTER DONOR
    // ==========================
    public String registerDonor(Donor donor, MultipartFile medicalReport) throws Exception {

        Firestore db = FirestoreClient.getFirestore();

        String donorId = UUID.randomUUID().toString();
        donor.setId(donorId);

        String encryptedPassword = passwordEncoder.encode(donor.getPassword());
        donor.setPassword(encryptedPassword);

        String fileName = donorId + "_" + medicalReport.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(
                medicalReport.getInputStream(),
                uploadPath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING
        );

        donor.setMedicalReportPath(fileName);

        db.collection(COLLECTION_NAME)
                .document(donorId)
                .set(donor);

        return donorId;
    }

    // ==========================
    // LOGIN
    // ==========================
    public String login(Donor loginRequest) {

        Donor existing = findByEmail(loginRequest.getEmail());

        if (existing == null) {
            return "User not found";
        }

        if (passwordEncoder.matches(
                loginRequest.getPassword(),
                existing.getPassword()
        )) {
            return "Login successful";
        }

        return "Invalid credentials";
    }

    // ==========================
    // FIND BY EMAIL (Firestore)
    // ==========================
    public Donor findByEmail(String email) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            if (documents.isEmpty()) {
                return null;
            }

            return documents.get(0).toObject(Donor.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==========================
    // GET PROFILE (Wrapper)
    // ==========================
    public Donor getDonorProfile(String email) {

        Donor donor = findByEmail(email);

        if (donor != null) {
            donor.setPassword(null); // remove password here ONLY
        }

        return donor;
    }
    // ==========================
// GET DONATIONS BY EMAIL
// ==========================
    public List<Donation> getDonationsByEmail(String email) throws Exception {

        List<Donation> donationList = new ArrayList<>();

        Firestore db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future =
                db.collection("donations")
                        .whereEqualTo("donorEmail", email)
                        .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            Donation donation = document.toObject(Donation.class);
            donation.setId(document.getId());
            donationList.add(donation);
        }

        return donationList;
    }
    // ==========================
// SAVE DONATION
// ==========================
    public String saveDonation(Donation donation) throws Exception {

        Firestore db = FirestoreClient.getFirestore();

        String donationId = UUID.randomUUID().toString();
        donation.setId(donationId);

        db.collection("donations")
                .document(donationId)
                .set(donation);

        // 🔥 Update donor lastDonationDate
        if ("Completed".equalsIgnoreCase(donation.getStatus())) {

            ApiFuture<QuerySnapshot> future = db.collection(COLLECTION_NAME)
                    .whereEqualTo("email", donation.getDonorEmail())
                    .get();

            List<QueryDocumentSnapshot> docs = future.get().getDocuments();

            if (!docs.isEmpty()) {
                db.collection(COLLECTION_NAME)
                        .document(docs.get(0).getId())
                        .update("lastDonationDate", donation.getDate());
            }
        }

        return donationId;
    }
}