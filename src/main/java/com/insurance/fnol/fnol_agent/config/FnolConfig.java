package com.insurance.fnol.fnol_agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "fnol")
@Data
public class FnolConfig {

    private Rules rules;
    private Upload upload;

    @Data
    public static class Rules {
        private Double autoApprovalThreshold;
        private List<String> investigationKeywords;
    }

    @Data
    public static class Upload {
        private String maxFileSize;
    }
}