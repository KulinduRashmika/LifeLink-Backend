package com.lifelink.patientms.controller;

import com.lifelink.patientms.dto.LoginRequest;
import com.lifelink.patientms.dto.PatientRegisterRequest;
import com.lifelink.patientms.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:3000")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // ✅ Test endpoint
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "patient-ms is running"
        ));
    }

    // ✅ Register (multipart: JSON + optional file)
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestPart("data") PatientRegisterRequest data,
            @RequestPart(value = "medicalReport", required = false) MultipartFile medicalReport
    ) throws Exception {

        String id = patientService.register(data, medicalReport);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Patient registered successfully",
                "patientId", id
        ));
    }

    // ✅ Login (JSON)
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) throws Exception {

        boolean success = patientService.login(request);

        if (!success) {
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "Invalid email or password"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful"
        ));
    }
}