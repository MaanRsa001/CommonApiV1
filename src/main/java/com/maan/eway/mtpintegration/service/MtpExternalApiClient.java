package com.maan.eway.mtpintegration.service;





/*import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.maan.eway.mtpintegration.config.MtpProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
//@RequiredArgsConstructor
//@Slf4j
public class MtpExternalApiClient {

    private static final int MAX_ATTEMPTS = 3;

    private final RestClient restClient;
    private final MtpProperties properties;
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(MtpExternalApiClient.class);

    
    
    public MtpExternalApiClient(
            RestClient restClient,
            MtpProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public String getToken() {
    	return "";
    }
  
    
    
    public <T> T postWithToken(String path, String token, Object request, Class<T> responseType) {
        return executeWithRetry(() -> restClient.post()
                .uri("https://uiapaymentstest.servicecops.com/test/Rest/partners/validate-vrn")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(responseType), path);
    }

    public ResponseEntity<byte[]> getFileWithoutToken(String path) {
        return executeWithRetry(() -> restClient.get()
                .uri(properties.getBaseUrl() + path)
                .retrieve()
                .toEntity(byte[].class), path);
    }
    private <T> T executeWithRetry(ApiCall<T> apiCall, String path) {
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return apiCall.execute();
            } catch (RestClientResponseException ex) {
                lastException = ex;
                if (!isRetryableStatus(ex.getRawStatusCode()) || attempt == MAX_ATTEMPTS) {
                    throw ex;
                }
                log.warn("MTP external API retry. path={}, status={}, attempt={}", path, ex.getRawStatusCode(), attempt);
                sleepBeforeRetry(attempt);
            } catch (ResourceAccessException ex) {
                lastException = ex;
                if (attempt == MAX_ATTEMPTS) {
                    throw ex;
                }
                log.warn("MTP external API connection retry. path={}, attempt={}", path, attempt);
                sleepBeforeRetry(attempt);
            }
        }

        throw lastException == null ? new IllegalStateException("MTP external API failed") : lastException;
    }

    private boolean isRetryableStatus(int statusCode) {
        return statusCode == 429 || statusCode >= 500;
    }

    private void sleepBeforeRetry(int attempt) {
        try {
            Thread.sleep(500L * attempt);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface ApiCall<T> {
        T execute();
    }

}*/  





import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.maan.eway.mtpintegration.config.MtpProperties;

@Component
public class MtpExternalApiClient {

    private static final int MAX_ATTEMPTS = 3;
    private static final Logger log = LoggerFactory.getLogger(MtpExternalApiClient.class);

    private final RestClient restClient;
    private final MtpProperties properties;

    public MtpExternalApiClient(RestClient restClient, MtpProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public <T> T postWithToken(String url, String token, Object request, Class<T> responseType) {
        return executeWithRetry(() -> restClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(responseType), url);
    }
    
    public <T> T postPolicyToken(String path, String token, Object request, Class<T> responseType) {
        return executeWithRetry(() -> restClient.post()
                .uri("https://uiapaymentstest.servicecops.com/preprod/Rest/partner/policy-details")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(responseType), path);
    }

    public ResponseEntity<byte[]> getFileWithoutToken(String path) {
        return executeWithRetry(() -> restClient.get()
                .uri(properties.getBaseUrl() + path)
                .retrieve()
                .toEntity(byte[].class), path);
    }

    private <T> T executeWithRetry(ApiCall<T> apiCall, String path) {
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return apiCall.execute();
            } catch (RestClientResponseException ex) {
                lastException = ex;
                if (!isRetryableStatus(ex.getRawStatusCode()) || attempt == MAX_ATTEMPTS) {
                    throw ex;
                }
                log.warn("MTP API retry. path={}, status={}, attempt={}",
                        path, ex.getRawStatusCode(), attempt);
                sleepBeforeRetry(attempt);
            } catch (ResourceAccessException ex) {
                lastException = ex;
                if (attempt == MAX_ATTEMPTS) {
                    throw ex;
                }
                log.warn("MTP API connection retry. path={}, attempt={}", path, attempt);
                sleepBeforeRetry(attempt);
            }
        }

        throw lastException == null ? new IllegalStateException("MTP external API failed") : lastException;
    }

    private boolean isRetryableStatus(int statusCode) {
        return statusCode == 429 || statusCode >= 500;
    }

    private void sleepBeforeRetry(int attempt) {
        try {
            Thread.sleep(500L * attempt);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface ApiCall<T> {
        T execute();
    }

}
