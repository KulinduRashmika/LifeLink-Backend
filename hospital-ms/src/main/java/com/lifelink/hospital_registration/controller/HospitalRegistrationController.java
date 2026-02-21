package com.lifelink.hospital_registration.controller;

import com.lifelink.hospital_registration.dto.HospitalRegistrationRequest;
import com.lifelink.hospital_registration.dto.HospitalRegistrationResponse;
import com.lifelink.hospital_registration.service.HospitalRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RequiredArgsConstructor
public class HospitalRegistrationController {

    private final HospitalRegistrationService registrationService;

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<HospitalRegistrationResponse> registerHospital(
            @Valid @ModelAttribute HospitalRegistrationRequest request,
            BindingResult bindingResult) {

        log.info("Received hospital registration request for: {}", request.getInstitutionName());

        // ✅ Show exact validation errors
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream()
                    .map(err -> {
                        if (err instanceof FieldError fe) {
                            return fe.getField() + ": " + fe.getDefaultMessage();
                        }
                        return err.getDefaultMessage();
                    })
                    .reduce((a, b) -> a + " | " + b)
                    .orElse("Validation failed");

            HospitalRegistrationResponse response = new HospitalRegistrationResponse();
            response.setSuccess(false);
            response.setMessage(msg);
            response.setErrorCode("VALIDATION_ERROR");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            HospitalRegistrationResponse response = new HospitalRegistrationResponse();
            response.setSuccess(false);
            response.setMessage("Passwords do not match");
            response.setErrorCode("PASSWORD_MISMATCH");
            return ResponseEntity.badRequest().body(response);
        }

        HospitalRegistrationResponse response = registrationService.registerHospital(request);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

    // ✅ New GET method to fetch hospital data by UID
    @GetMapping("/{uid}")
    public ResponseEntity<?> getHospitalProfile(@PathVariable String uid) {
        log.info("Fetching data for hospital UID: {}", uid);

        // This calls the service to get data from Firebase
        Object hospitalData = registrationService.getHospitalByUid(uid);

        if (hospitalData != null) {
            return ResponseEntity.ok(hospitalData);
        } else {
            return ResponseEntity.status(404).body("Hospital record not found in Firebase");
        }
    }
}