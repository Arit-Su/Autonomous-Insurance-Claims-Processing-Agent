package com.insurance.fnol.fnol_agent.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProcessedClaimResult {
    private ClaimData extractedFields;
    private List<String> missingFields;
    private String recommendedRoute;
    private String reasoning;
}