package com.lifelink.hospital_registration.controller;

import com.lifelink.hospital_registration.dto.HospitalLoginRequest;
import com.lifelink.hospital_registration.dto.HospitalLoginResponse;
import com.lifelink.hospital_registration.service.HospitalAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospitals")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RequiredArgsConstructor
public class HospitalAuthController {

    private final HospitalAuthService hospitalAuthService;

    @PostMapping("/login")
    public ResponseEntity<HospitalLoginResponse> login(
            @Valid @RequestBody HospitalLoginRequest request,
            BindingResult bindingResult) {

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

            return ResponseEntity.badRequest().body(
                    new HospitalLoginResponse(false, msg, "VALIDATION_ERROR",
                            null, null, null, null)
            );
        }

        HospitalLoginResponse response = hospitalAuthService.login(request);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(401).body(response);
    }
}