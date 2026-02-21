package com.lifelink.hospital_registration.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {
    public static final String COLLECTION_NAME = "hospitals";

    private String id;
    private String institutionName;
    private String registrationId;
    private String institutionType;
    private String contactPerson;
    private String officialEmail;
    private String contactNumber;
    private String physicalAddress;
    private String password;
    private Facilities facilities;
    private String verificationDocUrl;
    private String verificationDocName;
    private Long registrationDate;
    private Long lastUpdated;
    private String status;
    private String createdBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Facilities {
        private boolean bloodCollection;
        private boolean organHarvesting;
        private boolean icuFacilities;
        private boolean emergencyCare;
        private boolean labAnalysis;
    }
}