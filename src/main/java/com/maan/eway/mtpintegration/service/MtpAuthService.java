package com.maan.eway.mtpintegration.service;





import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.maan.eway.mtpintegration.config.MtpProperties;
import com.maan.eway.mtpintegration.dto.TokenResponse;
import com.maan.eway.mtpintegration.entity.MtpOauthToken;
import com.maan.eway.mtpintegration.repository.MtpOauthTokenRepository;
import com.maan.eway.service.impl.ApiIntegrationService;

@Service
public class MtpAuthService {

    private static final Logger log = LoggerFactory.getLogger(MtpAuthService.class);
    private static final Long TOKEN_ROW_ID = 1L;

    private final RestClient restClient;
    private final MtpProperties properties;
    private final MtpOauthTokenRepository tokenRepository;
    private final MtpErrorLogService errorLogService;
    
    @Autowired
    private ApiIntegrationService apiIntegrationService;

    private static final String COMPANY_ID = "100019";
    private static final Integer PRODUCT_ID = 125;

    public MtpAuthService(RestClient restClient,
                          MtpProperties properties,
                          MtpOauthTokenRepository tokenRepository,
                          MtpErrorLogService errorLogService) {
        this.restClient = restClient;
        this.properties = properties;
        this.tokenRepository = tokenRepository;
        this.errorLogService = errorLogService;
    }

    public TokenResponse getToken() {
    	 return fetchNewToken();
    }
    
    private TokenResponse fetchNewToken() {
        try {

            log.info("========== MTP TOKEN START ==========");

            validateConfig();

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "client_credentials");
            form.add("client_id", properties.getClientId());
            form.add("client_secret", properties.getClientSecret());

            String tokenUrl = apiIntegrationService.getApiUrl(
                    COMPANY_ID,
                    PRODUCT_ID,
                    "TOKEN"
            );

            log.info("MTP Token URL: {}", tokenUrl);

            TokenResponse response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response == null) {
                throw new IllegalStateException(
                        "MTP Token API returned null response"
                );
            }

            if (!hasText(response.getAccessToken())) {
                throw new IllegalStateException(
                        "MTP Token API returned empty access_token"
                );
            }

            log.info("MTP token received successfully");
            log.info("Expires in: {}", response.getExpiresIn());

            log.info("========== MTP TOKEN END ==========");

            return response;

        } catch (RestClientResponseException ex) {

            log.error(
                    "MTP token HTTP error. status={}, body={}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex
            );

            try {
                errorLogService.save("TOKEN", ex);
            } catch (Exception logEx) {
                log.error("Failed to save MTP token error log", logEx);
            }

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "MTP token failed. type={}, message={}",
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            try {
                errorLogService.save("TOKEN", ex);
            } catch (Exception logEx) {
                log.error("Failed to save MTP token error log", logEx);
            }

            throw ex;
        }
    }

    public String getValidAccessToken() {
        TokenResponse response = getValidTokenResponse();
        return response == null ? null : response.getAccessToken();
    }

    public void invalidateStoredToken() {
        tokenRepository.deleteById(TOKEN_ROW_ID);
    }

    private TokenResponse getValidTokenResponse() {

        LocalDateTime refreshTime = LocalDateTime.now()
                .plusSeconds(properties.getTokenRefreshBeforeSeconds());

        MtpOauthToken token =
                tokenRepository.findById(TOKEN_ROW_ID).orElse(null);

        if (token != null
                && hasText(token.getAccessToken())
                && token.getExpiresAt() != null
                && token.getExpiresAt().isAfter(refreshTime)) {

            log.info(
                    "Using valid MTP OAuth token from DB. expiresAt={}",
                    token.getExpiresAt()
            );

            return toTokenResponse(token);
        }

        log.info("No valid MTP OAuth token found. Generating new token.");

        return fetchAndSaveNewToken();
    }

    private TokenResponse fetchAndSaveNewToken() {

        try {

            log.info("========== MTP TOKEN START ==========");

            validateConfig();

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "client_credentials");
            form.add("client_id", properties.getClientId());
            form.add("client_secret", properties.getClientSecret());

            String tokenUrl = apiIntegrationService.getApiUrl(
                    COMPANY_ID,
                    PRODUCT_ID,
                    "TOKEN"
            );

            log.info("MTP Token URL: {}", tokenUrl);

            TokenResponse response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);

            log.info("MTP Token API response received: {}", response);

            if (response == null) {
                throw new IllegalStateException(
                        "MTP Token API returned null response"
                );
            }

            log.info("Access token received: {}", hasText(response.getAccessToken()));
            log.info("Expires in: {}", response.getExpiresIn());

            if (!hasText(response.getAccessToken())) {
                throw new IllegalStateException(
                        "MTP Token API returned empty access_token"
                );
            }

            if (response.getExpiresIn() == null) {
                throw new IllegalStateException(
                        "MTP Token API returned null expires_in"
                );
            }

            LocalDateTime now = LocalDateTime.now();

            MtpOauthToken token = new MtpOauthToken();

            token.setId(TOKEN_ROW_ID);
            token.setAccessToken(response.getAccessToken());
            token.setTokenType(response.getTokenType());
            token.setExpiresIn(response.getExpiresIn());
            token.setScope(response.getScope());
            token.setCreatedAt(now);

            long refreshBefore = properties.getTokenRefreshBeforeSeconds();

            long effectiveExpiry = Math.max(
                    response.getExpiresIn() - refreshBefore,
                    30L
            );

            token.setExpiresAt(
                    now.plusSeconds(effectiveExpiry)
            );

            log.info("Before saving token. expiresAt={}", token.getExpiresAt());

            MtpOauthToken savedToken = tokenRepository.save(token);

            log.info(
                    "MTP OAuth token SAVED successfully. id={}, expiresAt={}",
                    savedToken.getId(),
                    savedToken.getExpiresAt()
            );

            TokenResponse savedResponse = toTokenResponse(savedToken);

            log.info("Returning newly generated MTP token");

            log.info("========== MTP TOKEN END ==========");

            return savedResponse;

        } catch (RestClientResponseException ex) {

            log.error(
                    "MTP token HTTP error. status={}, body={}",
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex
            );

            try {
                errorLogService.save("TOKEN", ex);
            } catch (Exception logEx) {
                log.error("Failed to save MTP token error log", logEx);
            }

            throw ex;

        } catch (Exception ex) {

            log.error(
                    "MTP token failed. type={}, message={}",
                    ex.getClass().getName(),
                    ex.getMessage(),
                    ex
            );

            try {
                errorLogService.save("TOKEN", ex);
            } catch (Exception logEx) {
                log.error("Failed to save MTP token error log", logEx);
            }

            throw ex;
        }
    }

    private void validateConfig() {
        if (!hasText(properties.getBaseUrl())) {
            throw new IllegalStateException("mtp.base-url missing in application.properties");
        }
        if (!hasText(properties.getClientId())) {
            throw new IllegalStateException("mtp.client-id missing in application.properties");
        }
        if (!hasText(properties.getClientSecret())) {
            throw new IllegalStateException("mtp.client-secret missing in application.properties");
        }
    }

    private TokenResponse toTokenResponse(MtpOauthToken token) {
        TokenResponse response = new TokenResponse();
        response.setAccessToken(token.getAccessToken());
        response.setTokenType(token.getTokenType());
        response.setExpiresIn(token.getExpiresIn());
        response.setScope(token.getScope());
        return response;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}



