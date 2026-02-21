package com.lifelink.hospital_registration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lifelink.hospital_registration.model.Hospital;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class HospitalRegistrationRequest {

    @NotBlank(message = "Institution name is required")
    private String institutionName;

    @NotBlank(message = "Registration ID is required")
    private String registrationId;

    @NotBlank(message = "Institution type is required")
    private String institutionType;

    @NotBlank(message = "Contact person is required")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String officialEmail;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotBlank(message = "Physical address is required")
    private String physicalAddress;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @JsonProperty("facilities")
    private Hospital.Facilities facilities;

    @NotNull(message = "Verification document is required")
    private MultipartFile verificationDoc;
}