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
        // 1. Policy Number: Matches "POLICY NUMBER" then skips space/colons to get the ID
        String policy = findMatch("POLICY NUMBER[:\\s]+(\\S+)", rawText);

        // 2. Name: Matches "NAME OF INSURED" and grabs the rest of that line
        String name = findMatch("NAME OF INSURED[:\\s]+(.*)", rawText);

        // 3. Date: Matches "DATE" followed by anything until a MM/DD/YYYY pattern
        // This works for both "DATE OF LOSS" and "DATE (MM/DD/YYYY)"
        String date = findMatch("DATE.*?(\\d{2}/\\d{2}/\\d{4})", rawText);

        // 4. Description: Captures everything between Description label and Estimate/Injury labels
        String desc = findMatch("DESCRIPTION OF ACCIDENT[:\\s]*(.*?)(?=ESTIMATE|INJURY|$)", rawText);

        // 5. Estimate: Matches "ESTIMATE" and grabs the number/dollar amount
        String damageStr = findMatch("ESTIMATE.*?\\$?([\\d,]+\\.?\\d*)", rawText);
        Double damageValue = parseMoney(damageStr);

        // 6. VIN: Matches V.I.N. or VIN
        String vin = findMatch("V\\.?I\\.?N\\.?:?[:\\s]+(\\S+)", rawText);

        return ClaimData.builder()
                .policyNumber(policy)
                .policyHolderName(name)
                .date(date)
                .description(desc)
                .estimatedDamage(damageValue)
                .initialEstimate(damageValue)
                .claimType(determineClaimType(rawText))
                .assetType("Vehicle")
                .assetId(vin)
                .build();
    }

    private ClaimType determineClaimType(String text) {
        String lower = text.toLowerCase();
        // Logical check: If the word 'injury' or 'injured' exists
        // AND it's not immediately followed by 'no'.
        if ((lower.contains("injury") || lower.contains("injured"))
                && !lower.contains("injury: no") && !lower.contains("injury:no")) {
            return ClaimType.INJURY;
        }
        return ClaimType.PROPERTY_DAMAGE;
    }

    private String findMatch(String regex, String text) {
        // DOTALL allows the '.' to match newlines for the description field
        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
        if (m.find()) {
            // We split by newline and take the first line to avoid grabbing the whole page
            String result = m.group(1).split("\\n")[0].trim();
            return result;
        }
        return "";
    }

    private Double parseMoney(String val) {
        try {
            return Double.parseDouble(val.replace(",", ""));
        } catch (Exception e) {
            return null;
        }
    }
}