package com.maan.eway.mtpintegration.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.mtpintegration.dto.MtpPolicyReq;
import com.maan.eway.mtpintegration.dto.PolicyObject;
import com.maan.eway.mtpintegration.dto.PolicyResponse;
import com.maan.eway.mtpintegration.entity.MtpPolicyDetails;
import com.maan.eway.mtpintegration.repository.MtpPolicyDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MtpPolicyDetailsService {

	private static final Logger log = LoggerFactory.getLogger(MtpPolicyDetailsService.class);
	private final MtpPolicyDetailsRepository repository;

	public void savePolicyDetails(MtpPolicyReq request, PolicyResponse response) {
	    try {
	        MtpPolicyDetails entity = repository.findByRegno(request.getVrn()).orElse(new MtpPolicyDetails());
	        entity.setRegno(request.getVrn());
	        entity.setReturnCode(response.getReturnCode());
	        entity.setReturnMessage(response.getReturnMessage());

	        if (response.getReturnObject() != null) {
	            PolicyObject obj = response.getReturnObject();
	            entity.setPolicyNumber(obj.getPolicyNumber());
	            entity.setPolicyStatus(obj.getPolicyStatus());
	            entity.setPolicyHolderName(obj.getPolicyHolderName());
	            entity.setInsuranceCompanyName(obj.getInsuranceCompanyName());
	            entity.setAggregatorTransactionId(obj.getAggregatorTransactionId());
	            entity.setAgentName(obj.getAgentName());
	            entity.setAgentPhone(obj.getAgentPhone());
	            entity.setPayerName(obj.getPayerName());
	            entity.setPayerMobile(obj.getPayerMobile());
	            entity.setPaymentReference(obj.getPaymentReference());
	            entity.setAssessmentType(obj.getAssessmentType());
	            entity.setStickerType(obj.getStickerType());
	            entity.setCoverDescription(obj.getCoverDescription());
	            entity.setStartDate(parseDate(obj.getStartDate()));
	            entity.setEndDate(parseDate(obj.getEndDate()));
	            entity.setDateCreated(parseDate(safeSubstring(obj.getDateCreated())));
	            entity.setPaymentReceivedDate(parseDate(safeSubstring(obj.getPaymentReceivedDate())));
	            entity.setAssessedVat(obj.getAssessedVat());
	            entity.setAssessedTrainingLevy(obj.getAssessedTrainingLevy());
	            entity.setAssessedPremium(obj.getAssessedPremium());
	            entity.setAssessedStampDuty(obj.getAssessedStampDuty());
	            entity.setAssessedStickerFees(obj.getAssessedStickerFees());
	            entity.setTotalAssessmentAmount(obj.getTotalAssessmentAmount());
	            entity.setAmount(obj.getAmount());
	            entity.setShortTermDuration(obj.getShortTermDuration());
	            entity.setRunning(String.valueOf(obj.getRunning()));
	            entity.setExpired(String.valueOf(obj.getExpired()));
	            entity.setShortTerm(String.valueOf(obj.getShortTerm()));
	            entity.setProrated(String.valueOf(obj.getProrated()));
	            entity.setFuture(String.valueOf(obj.getFuture()));
	         // Missing fields — chassis, engine, seating capacity, sticker vehicle type
	            entity.setChassisNumber(obj.getChassisNumber());
	            entity.setEngineNumber(obj.getEngineNumber());
	            entity.setSeatingCapacity(obj.getSeatingCapacity());
	            entity.setStickerVehicleType(obj.getStickerVehicleType());
	        } else {
	            // "Not Found" or any response without a returnObject — clear stale policy fields
	            entity.setPolicyNumber(null);
	            entity.setPolicyStatus(null);
	            entity.setPolicyHolderName(null);
	            entity.setInsuranceCompanyName(null);
	            entity.setAggregatorTransactionId(null);
	            entity.setAgentName(null);
	            entity.setAgentPhone(null);
	            entity.setPayerName(null);
	            entity.setPayerMobile(null);
	            entity.setPaymentReference(null);
	            entity.setAssessmentType(null);
	            entity.setStickerType(null);
	            entity.setCoverDescription(null);
	            entity.setStartDate(null);
	            entity.setEndDate(null);
	            entity.setDateCreated(null);
	            entity.setPaymentReceivedDate(null);
	            entity.setAssessedVat(null);
	            entity.setAssessedTrainingLevy(null);
	            entity.setAssessedPremium(null);
	            entity.setAssessedStampDuty(null);
	            entity.setAssessedStickerFees(null);
	            entity.setTotalAssessmentAmount(null);
	            entity.setAmount(null);
	            entity.setShortTermDuration(null);
	            entity.setRunning(null);
	            entity.setExpired(null);
	            entity.setShortTerm(null);
	            entity.setProrated(null);
	            entity.setFuture(null);
	            entity.setChassisNumber(null);
	            entity.setEngineNumber(null);
	            entity.setSeatingCapacity(null);
	            entity.setStickerVehicleType(null);
	        }

	        ObjectMapper mapper = new ObjectMapper();
	        entity.setRequestJson(mapper.writeValueAsString(request));
	        entity.setResponseJson(mapper.writeValueAsString(response));
	        entity.setEntryDate(new java.util.Date());

	        repository.save(entity);
	    } catch (Exception ex) {
	        log.error("Unable to save MTP Policy Details. error={}", ex.getMessage(), ex);
	    }
	}

	private Date parseDate(String value) {
	    if (value == null || value.isBlank()) {
	        return null;
	    }
	    try {
	        return java.sql.Date.valueOf(value.length() >= 10 ? value.substring(0, 10) : value);
	    } catch (Exception e) {
	        log.warn("Unable to parse date value='{}'", value);
	        return null;
	    }
	}

	private String safeSubstring(String value) {
	    return (value != null && value.length() >= 10) ? value.substring(0, 10) : value;
	}

}
