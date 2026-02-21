package com.lifelink.donorms.controller;

import com.lifelink.donorms.model.Donor;
import com.lifelink.donorms.service.DonorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/donor")   // 🔥 changed from donors → donor
@CrossOrigin(origins = "http://localhost:3000")
public class DonorController {

    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    // ================= REGISTER =================
    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<String> register(
            @RequestPart("donor") Donor donor,
            @RequestPart("medicalReport") MultipartFile medicalReport
    ) throws Exception {

        String donorId = donorService.registerDonor(donor, medicalReport);
        return ResponseEntity.ok(donorId);
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Donor loginRequest) {

        String result = donorService.login(loginRequest);

        if (result.equals("Login successful")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    // ================= PROFILE =================
    @GetMapping("/profile")
    public ResponseEntity<Donor> getProfile(@RequestParam String email) {

        Donor donor = donorService.getDonorProfile(email);

        if (donor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(donor);
    }
}