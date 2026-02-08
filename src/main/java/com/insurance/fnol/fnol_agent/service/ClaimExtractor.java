package com.insurance.fnol.fnol_agent.service;

import com.insurance.fnol.fnol_agent.model.ClaimData;

public interface ClaimExtractor {
    ClaimData extract(String rawText);
}