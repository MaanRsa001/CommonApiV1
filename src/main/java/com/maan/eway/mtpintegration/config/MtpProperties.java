package com.maan.eway.mtpintegration.config;
/*
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component


@ConfigurationProperties(prefix = "mtp")
public class MtpProperties {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    
    
    @Value("${mtp.token.url}")
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
}
*/



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MtpProperties {

    @Value("${mtp.base-url}")
    private String baseUrl;

    @Value("${mtp.client-id}")
    private String clientId;

    @Value("${mtp.client-secret}")
    private String clientSecret;

    @Value("${mtp.partner-identifier:}")
    private String partnerIdentifier;

    @Value("${mtp.token-refresh-before-seconds:120}")
    private long tokenRefreshBeforeSeconds;

    public String getBaseUrl() {
        return trimTrailingSlash(baseUrl);
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getPartnerIdentifier() {
        return partnerIdentifier;
    }

    public long getTokenRefreshBeforeSeconds() {
        return tokenRefreshBeforeSeconds;
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}

