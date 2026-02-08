package com.insurance.fnol.fnol_agent.service;

import com.insurance.fnol.fnol_agent.model.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RulesEngineService {

    public ProcessedClaimResult process(ClaimData data) {
        List<String> missingFields = identifyMissingMandatoryFields(data);


        if (!missingFields.isEmpty()) {
            return buildResult(data, missingFields, "Manual Review",
                    "Mandatory fields are missing: " + String.join(", ", missingFields));
        }


        if (containsFraudKeywords(data.getDescription())) {
            return buildResult(data, missingFields, "Investigation Flag",
                    "Description contains words suggesting fraud or inconsistency.");
        }


        if (data.getClaimType() == ClaimType.INJURY) {
            return buildResult(data, missingFields, "Specialist Queue",
                    "Claim involves injury; routing to bodily injury specialists.");
        }


        if (data.getEstimatedDamage() != null && data.getEstimatedDamage() < 25000) {
            return buildResult(data, missingFields, "Fast-track",
                    "Low-value property damage claim eligible for automated processing.");
        }

        return buildResult(data, missingFields, "Standard Workflow", "Standard property damage processing.");
    }

    private List<String> identifyMissingMandatoryFields(ClaimData data) {
        List<String> missing = new ArrayList<>();
        if (isNullOrEmpty(data.getPolicyNumber())) missing.add("Policy Number");
        if (isNullOrEmpty(data.getPolicyHolderName())) missing.add("Policyholder Name");
        if (isNullOrEmpty(data.getDate())) missing.add("Date");
        if (isNullOrEmpty(data.getDescription())) missing.add("Description");
        if (data.getEstimatedDamage() == null) missing.add("Estimated Damage");
        if (data.getClaimType() == null || data.getClaimType() == ClaimType.UNKNOWN) missing.add("Claim Type");
        return missing;
    }

    private boolean containsFraudKeywords(String desc) {
        if (desc == null) return false;
        String s = desc.toLowerCase();
        return s.contains("fraud") || s.contains("inconsistent") || s.contains("staged");
    }

    private boolean isNullOrEmpty(String s) { return s == null || s.trim().isEmpty(); }

    private ProcessedClaimResult buildResult(ClaimData d, List<String> m, String route, String reason) {
        return ProcessedClaimResult.builder()
                .extractedFields(d).missingFields(m)
                .recommendedRoute(route).reasoning(reason).build();
    }
}