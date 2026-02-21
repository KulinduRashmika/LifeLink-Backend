package com.lifelink.hospital_registration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalLoginResponse {
    private boolean success;
    private String message;
    private String errorCode;

    // minimal info to return
    private String hospitalId;
    private String institutionName;
    private String institutionType;
    private String status;
}