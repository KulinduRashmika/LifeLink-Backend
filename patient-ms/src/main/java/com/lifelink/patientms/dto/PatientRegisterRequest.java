package com.lifelink.patientms.dto;

import jakarta.validation.constraints.*;

public class PatientRegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "NIC / Hospital ID is required")
    private String nicHospitalId;

    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be realistic")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9+]{7,15}$", message = "Invalid contact number format")
    private String contactNumber;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Required blood group or organ is required")
    private String requiredBloodGroupOrgan;

    @NotBlank(message = "Urgency level is required")
    private String urgencyLevel;

    @NotBlank(message = "Hospital name is required")
    private String hospitalName;

    @AssertTrue(message = "You must give consent")
    private boolean consent;

    // 🔐 Password Field
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // ================= GETTERS & SETTERS =================

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNicHospitalId() { return nicHospitalId; }
    public void setNicHospitalId(String nicHospitalId) { this.nicHospitalId = nicHospitalId; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRequiredBloodGroupOrgan() { return requiredBloodGroupOrgan; }
    public void setRequiredBloodGroupOrgan(String requiredBloodGroupOrgan) { this.requiredBloodGroupOrgan = requiredBloodGroupOrgan; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public boolean isConsent() { return consent; }
    public void setConsent(boolean consent) { this.consent = consent; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}