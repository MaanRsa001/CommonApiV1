package com.maan.eway.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AuthToken2 {

	@JsonProperty("Token")
    private String token;
}
