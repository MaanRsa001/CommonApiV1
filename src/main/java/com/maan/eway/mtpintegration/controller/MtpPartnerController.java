
package com.maan.eway.mtpintegration.controller;
/*
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.mtpintegration.dto.InitiatePaymentRequest;
import com.maan.eway.mtpintegration.dto.InitiatePaymentResponse;
import com.maan.eway.mtpintegration.dto.PaymentStatusRequest;
import com.maan.eway.mtpintegration.dto.PaymentStatusResponse;
import com.maan.eway.mtpintegration.dto.ValidateVrnRequest;
import com.maan.eway.mtpintegration.dto.VehicleValidationResponse;
import com.maan.eway.mtpintegration.service.MtpPartnerService;
import org.springframework.web.bind.annotation.RequestBody;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mtp")
public class MtpPartnerController {

    private final MtpPartnerService partnerService;

    public MtpPartnerController(MtpPartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostMapping("/validate-vrn")
    public ResponseEntity<VehicleValidationResponse> validateVrn(
            @Valid @RequestBody ValidateVrnRequest request) {
        return ResponseEntity.ok(partnerService.validateVrn(request));
    }

    @PostMapping("/initiate-payment")
    public ResponseEntity<InitiatePaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.ok(partnerService.initiatePayment(request));
    }

    @PostMapping("/payment-status")
    public ResponseEntity<PaymentStatusResponse> paymentStatus(
            @Valid @RequestBody PaymentStatusRequest request) {
        return ResponseEntity.ok(partnerService.paymentStatus(request));
    }
}*/import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.mtpintegration.dto.InitiatePaymentRequest;
import com.maan.eway.mtpintegration.dto.InitiatePaymentResponse;
import com.maan.eway.mtpintegration.dto.MtpPolicyReq;
import com.maan.eway.mtpintegration.dto.PaymentStatusRequest;
import com.maan.eway.mtpintegration.dto.PaymentStatusResponse;
import com.maan.eway.mtpintegration.dto.PolicyResponse;
import com.maan.eway.mtpintegration.dto.ValidateVrnRequest;
import com.maan.eway.mtpintegration.service.MtpPartnerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mtp")
public class MtpPartnerController {

    private final MtpPartnerService partnerService;

    public MtpPartnerController(MtpPartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostMapping("/validate-vrn")
    public ResponseEntity<CommonRes> validateVrn(@Valid @RequestBody ValidateVrnRequest request) {
        return ResponseEntity.ok(partnerService.validateVrn(request));
    }

    
    @PostMapping("/policy-details")
    public ResponseEntity<PolicyResponse> policydetails(@Valid @RequestBody MtpPolicyReq request) {
        return ResponseEntity.ok(partnerService.policyDetails(request));
    }
    
    @PostMapping("/initiate-payment")
    public ResponseEntity<InitiatePaymentResponse> initiatePayment(@Valid @RequestBody InitiatePaymentRequest request) {
        return ResponseEntity.ok(partnerService.initiatePayment(request));
    }

    @PostMapping("/payment-status")
    public ResponseEntity<PaymentStatusResponse> paymentStatus(@Valid @RequestBody PaymentStatusRequest request) {
        return ResponseEntity.ok(partnerService.paymentStatus(request));
    }
}
