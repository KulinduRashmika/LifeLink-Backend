package com.lifelink.patientms.service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.lifelink.patientms.dto.LoginRequest;
import com.lifelink.patientms.dto.PatientRegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PatientService {

    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public PatientService(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String register(PatientRegisterRequest req, MultipartFile medicalReport) throws Exception {

        Firestore db = FirestoreClient.getFirestore();

        String patientId = UUID.randomUUID().toString();

        // ✅ Encrypt password
        String encryptedPassword = passwordEncoder.encode(req.getPassword());

        // ✅ Save file locally (if present)
        String fileUrl = null;
        if (medicalReport != null && !medicalReport.isEmpty()) {
            fileUrl = saveFileLocally(patientId, medicalReport);
        }

        Map<String, Object> doc = new HashMap<>();
        doc.put("patientId", patientId);
        doc.put("fullName", req.getFullName());
        doc.put("nicHospitalId", req.getNicHospitalId());
        doc.put("age", req.getAge());
        doc.put("gender", req.getGender());
        doc.put("contactNumber", req.getContactNumber());
        doc.put("email", req.getEmail());
        doc.put("location", req.getLocation());
        doc.put("requiredBloodGroupOrgan", req.getRequiredBloodGroupOrgan());
        doc.put("urgencyLevel", req.getUrgencyLevel());
        doc.put("hospitalName", req.getHospitalName());
        doc.put("consent", req.isConsent());

        // ✅ store encrypted password only
        doc.put("password", encryptedPassword);

        doc.put("medicalReportUrl", fileUrl);
        doc.put("status", "PENDING_VERIFICATION");
        doc.put("createdAt", Instant.now().toString());

        db.collection("patients").document(patientId).set(doc).get();

        return patientId;
    }

    public boolean login(LoginRequest request) throws Exception {

        Firestore db = FirestoreClient.getFirestore();

        var querySnapshot = db.collection("patients")
                .whereEqualTo("email", request.getEmail())
                .limit(1)
                .get()
                .get();

        if (querySnapshot.isEmpty()) return false;

        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

        String storedPassword = doc.getString("password");
        if (storedPassword == null) return false;

        return passwordEncoder.matches(request.getPassword(), storedPassword);
    }

    private String saveFileLocally(String patientId, MultipartFile file) throws Exception {
        String ext = getExt(file.getOriginalFilename());
        String safeExt = (ext == null) ? "" : ("." + ext);

        Path folder = Paths.get(uploadDir, "medicalReports", patientId)
                .toAbsolutePath().normalize();

        Files.createDirectories(folder);

        String fileName = UUID.randomUUID() + safeExt;
        Path filePath = folder.resolve(fileName);

        Files.write(filePath, file.getBytes());

        return baseUrl + "/files/medicalReports/" + patientId + "/" + fileName;
    }

    private String getExt(String name) {
        if (name == null) return null;
        int i = name.lastIndexOf('.');
        if (i < 0) return null;
        return name.substring(i + 1).toLowerCase();
    }
}