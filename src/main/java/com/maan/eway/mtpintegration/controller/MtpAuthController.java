package com.maan.eway.mtpintegration.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.mtpintegration.dto.TokenResponse;
import com.maan.eway.mtpintegration.service.MtpAuthService;

import lombok.RequiredArgsConstructor;
/*
@RestController
@RequestMapping("/api/mtp")
@RequiredArgsConstructor
public class MtpAuthController {

	private final MtpAuthService authService;
	
	@GetMapping("/token")
	public ResponseEntity<TokenResponse> token() {
		return ResponseEntity.ok(authService.getToken());
	}
}
*/
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.mtpintegration.dto.TokenResponse;
import com.maan.eway.mtpintegration.service.MtpAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mtp")
//@RequiredArgsConstructor
public class MtpAuthController {

	private final MtpAuthService authService;
	
	public MtpAuthController(MtpAuthService mtpAuthService) {
		this.authService=mtpAuthService;
	}

	@GetMapping("/token")
	public ResponseEntity<TokenResponse> token() {
		return ResponseEntity.ok(authService.getToken());
	}
}