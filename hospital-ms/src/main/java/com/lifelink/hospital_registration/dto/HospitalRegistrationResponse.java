package com.lifelink.hospital_registration.dto;
import com.lifelink.hospital_registration.model.Hospital;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRegistrationResponse {
    private boolean success;
    private String message;
    private Hospital data;
    private String errorCode;
}