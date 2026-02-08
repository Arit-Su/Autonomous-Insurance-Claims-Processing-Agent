package com.insurance.fnol.fnol_agent;

import com.insurance.fnol.fnol_agent.config.FnolConfig; // Import the new config
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FnolConfig.class)
public class FnolAgentApplication {
	public static void main(String[] args) {
		SpringApplication.run(FnolAgentApplication.class, args);
	}
}