package com.insurance.fnol.fnol_agent.service.impl;

import com.insurance.fnol.fnol_agent.model.*;
import com.insurance.fnol.fnol_agent.service.ClaimExtractor;
import org.springframework.stereotype.Service;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RegexClaimExtractor implements ClaimExtractor {

    @Override
    public ClaimData extract(String rawText) {

        String description = findMatch("DESCRIPTION OF ACCIDENT[:\\s]*(.*?)(?=ESTIMATE|INJURY|$)", rawText);
        Double damage = parseMoney(findMatch("ESTIMATE AMOUNT[:\\s]*\\$?([\\d,]+\\.?\\d*)", rawText));

        return ClaimData.builder()
                .policyNumber(findMatch("POLICY NUMBER[:\\s]+(\\S+)", rawText))
                .policyHolderName(findMatch("NAME OF INSURED[:\\s]+(.*)", rawText))
                .date(findMatch("DATE OF LOSS[:\\s]+(\\d{2}/\\d{2}/\\d{4})", rawText))
                .description(description)
                .estimatedDamage(damage)
                .initialEstimate(damage)
                .claimType(determineClaimType(rawText))
                .assetType("Vehicle")
                .assetId(findMatch("VIN[:\\s]+(\\S+)", rawText))
                .build();
    }

    private ClaimType determineClaimType(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("injury") && !lower.contains("injury: no")) {
            return ClaimType.INJURY;
        }
        return ClaimType.PROPERTY_DAMAGE;
    }

    private String findMatch(String regex, String text) {
        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
        return m.find() ? m.group(1).trim() : "";
    }

    private Double parseMoney(String val) {
        try { return Double.parseDouble(val.replace(",", "")); } catch (Exception e) { return null; }
    }
}