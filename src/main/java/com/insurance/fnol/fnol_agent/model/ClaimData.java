package com.insurance.fnol.fnol_agent.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ClaimData {

    private String policyNumber;
    private String policyHolderName;
    private String effectiveDates;


    private String date;
    private String time;
    private String location;
    private String description;


    private String claimant;
    private String thirdParties;
    private String contactDetails;


    private String assetType;
    private String assetId;
    private Double estimatedDamage;


    private ClaimType claimType;
    private List<String> attachments;
    private Double initialEstimate;
}