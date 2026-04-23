package com.stoneridge.backend.service;

import com.dwolla.Dwolla;
import com.dwolla.http.Headers;
import com.dwolla.http.JsonBody;
import com.dwolla.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoneridge.backend.exception.BadRequestException;
import kotlin.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DwollaService {

    private static final Logger log = LoggerFactory.getLogger(DwollaService.class);
    private final Dwolla dwollaClient;
    private final ObjectMapper objectMapper;

    public DwollaService(Dwolla dwollaClient, ObjectMapper objectMapper) {
        this.dwollaClient = dwollaClient;
        this.objectMapper = objectMapper;
    }

    @SuppressWarnings("unchecked")
    private Pair<String, Object>[] toKotlinPairs(Map<String, Object> javaMap) {
        if (javaMap == null) return new Pair[0];
        return javaMap.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList())
                .toArray(new Pair[0]);
    }

    @SuppressWarnings("unchecked")
    private Pair<String, String>[] toKotlinStringPairs(Map<String, String> javaMap) {
        if (javaMap == null) return new Pair[0];
        return javaMap.entrySet().stream()
                .map(entry -> new Pair<>(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList())
                .toArray(new Pair[0]);
    }

    public String createDwollaCustomer(Map<String, Object> newCustomer) throws Exception {
        Response<JsonBody> response = dwollaClient.post(JsonBody.class, "customers", new JsonBody(toKotlinPairs(newCustomer)), new Headers(toKotlinStringPairs(new HashMap<>())));
        if (response.statusCode == 201) {
            return response.headers.get("location");
        } else {
            log.error("Failed to create Dwolla customer. Status: {}, Body: {}", response.statusCode, response.body);
            throw new BadRequestException("Failed to create Dwolla customer.");
        }
    }

    public String createFundingSource(String customerId, String fundingSourceName, String plaidToken) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", fundingSourceName);
        requestBody.put("plaidToken", plaidToken);

        Response<JsonBody> response = dwollaClient.post(JsonBody.class, String.format("customers/%s/funding-sources", customerId), new JsonBody(toKotlinPairs(requestBody)), new Headers(toKotlinStringPairs(new HashMap<>())));
        if (response.statusCode == 201) {
            return response.headers.get("location");
        } else {
            log.error("Failed to create funding source. Status: {}, Body: {}", response.statusCode, response.body);
            throw new BadRequestException("Failed to create funding source.");
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> createOnDemandAuthorization() throws Exception {
        Response<JsonBody> response = dwollaClient.post(JsonBody.class, "on-demand-authorizations", new JsonBody(new Pair[0]), new Headers(toKotlinStringPairs(new HashMap<>())));
        if (response.statusCode == 201) {
            String responseBodyString = response.body.toString();
            Map<String, Object> bodyMap = objectMapper.readValue(responseBodyString, Map.class);
            return (Map<String, Object>) bodyMap.get("_links");
        } else {
            log.error("Failed to create on-demand authorization. Status: {}, Body: {}", response.statusCode, response.body);
            throw new BadRequestException("Failed to create on-demand authorization.");
        }
    }

    public String createTransfer(String sourceFundingSourceUrl, String destinationFundingSourceUrl, double amount) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> source = new HashMap<>();
        source.put("href", sourceFundingSourceUrl);
        Map<String, String> destination = new HashMap<>();
        destination.put("href", destinationFundingSourceUrl);

        Map<String, Map<String, String>> links = new HashMap<>();
        links.put("source", source);
        links.put("destination", destination);
        requestBody.put("_links", links);

        Map<String, String> amountMap = new HashMap<>();
        amountMap.put("currency", "USD");
        amountMap.put("value", String.format("%.2f", amount));
        requestBody.put("amount", amountMap);

        Response<JsonBody> response = dwollaClient.post(JsonBody.class, "transfers", new JsonBody(toKotlinPairs(requestBody)), new Headers(toKotlinStringPairs(new HashMap<>())));
        if (response.statusCode == 201) {
            return response.headers.get("location");
        } else {
            log.error("Failed to create transfer. Status: {}, Body: {}", response.statusCode, response.body);
            throw new BadRequestException("Failed to create transfer.");
        }
    }

    public String addFundingSource(String dwollaCustomerId, String processorToken, String bankName) throws Exception {
        createOnDemandAuthorization();
        return createFundingSource(dwollaCustomerId, bankName, processorToken);
    }
}
