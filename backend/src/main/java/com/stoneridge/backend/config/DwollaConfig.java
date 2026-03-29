package com.stoneridge.backend.config;

import com.dwolla.Dwolla;
import com.dwolla.DwollaEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DwollaConfig {

    @Value("${dwolla.key}")
    private String dwollaKey;

    @Value("${dwolla.secret}")
    private String dwollaSecret;

    @Value("${dwolla.env}")
    private String dwollaEnv;

    @Bean
    public Dwolla dwollaClient() {
        DwollaEnvironment environment;
        switch (dwollaEnv.toLowerCase()) {
            case "sandbox":
                environment = DwollaEnvironment.SANDBOX;
                break;
            case "production":
                environment = DwollaEnvironment.PRODUCTION;
                break;
            default:
                throw new IllegalArgumentException("Invalid Dwolla environment: " + dwollaEnv);
        }
        return new Dwolla(dwollaKey, dwollaSecret, environment);
    }
}
