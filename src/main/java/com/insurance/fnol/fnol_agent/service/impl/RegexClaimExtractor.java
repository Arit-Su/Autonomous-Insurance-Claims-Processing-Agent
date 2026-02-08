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

        String policy = findMatch("POLICY NUMBER[:\\s]+(\\S+)", rawText);

        String name = findMatch("NAME OF INSURED[:\\s]+(.*)", rawText);

        String date = findMatch("DATE.*?(\\d{2}/\\d{2}/\\d{4})", rawText);

        String desc = findMatch("DESCRIPTION OF ACCIDENT[:\\s]*(.*?)(?=ESTIMATE|INJURY|$)", rawText);

        String damageStr = findMatch("ESTIMATE.*?\\$?([\\d,]+\\.?\\d*)", rawText);
        Double damageValue = parseMoney(damageStr);

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
        if ((lower.contains("injury") || lower.contains("injured"))
                && !lower.contains("injury: no") && !lower.contains("injury:no")) {
            return ClaimType.INJURY;
        }
        return ClaimType.PROPERTY_DAMAGE;
    }

    private String findMatch(String regex, String text) {

        Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
        if (m.find()) {

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