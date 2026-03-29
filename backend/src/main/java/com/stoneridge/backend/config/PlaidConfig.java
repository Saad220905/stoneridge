package com.stoneridge.backend.config;

import com.plaid.client.ApiClient;
import com.plaid.client.request.PlaidApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PlaidConfig {

    @Value("${plaid.client_id}")
    private String plaidClientId;

    @Value("${plaid.secret}")
    private String plaidSecret;

    @Value("${plaid.env}")
    private String plaidEnv;

    @Bean
    public PlaidApi plaidApi() {
        Map<String, String> apiKeys = new HashMap<>();
        apiKeys.put("clientId", plaidClientId);
        apiKeys.put("secret", plaidSecret);

        ApiClient apiClient = new ApiClient(apiKeys);
        switch (plaidEnv.toLowerCase()) {
            case "sandbox":
                apiClient.setPlaidAdapter(ApiClient.Sandbox);
                break;
            case "development":
                apiClient.setPlaidAdapter(ApiClient.Development);
                break;
            case "production":
                apiClient.setPlaidAdapter(ApiClient.Production);
                break;
            default:
                throw new IllegalArgumentException("Invalid Plaid environment: " + plaidEnv);
        }
        return apiClient.createService(PlaidApi.class);
    }
}
