package com.maan.eway.mtpintegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PolicyObject {

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("aggregator_transaction_id")
    private String aggregatorTransactionId;

    @JsonProperty("agent_name")
    private String agentName;

    @JsonProperty("insurance_company_name")
    private String insuranceCompanyName;

    @JsonProperty("policy_status")
    private String policyStatus;

    @JsonProperty("assessed_vat")
    private Double assessedVat;

    @JsonProperty("agent_phone")
    private String agentPhone;

    @JsonProperty("sticker_type")
    private String stickerType;

    @JsonProperty("running")
    private Boolean running;

    @JsonProperty("assessed_training_levy")
    private Double assessedTrainingLevy;

    @JsonProperty("expired")
    private Boolean expired;

    @JsonProperty("policy_number")
    private String policyNumber;

    @JsonProperty("total_assessment_amount")
    private Double totalAssessmentAmount;

    @JsonProperty("short_term")
    private Boolean shortTerm;

    @JsonProperty("payer_mobile")
    private String payerMobile;

    @JsonProperty("short_term_duration")
    private Integer shortTermDuration;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("prorated")
    private Boolean prorated;

    @JsonProperty("assessed_premium")
    private Double assessedPremium;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("payment_reference")
    private String paymentReference;

    @JsonProperty("assessment_type")
    private String assessmentType;

    @JsonProperty("policy_holder_name")
    private String policyHolderName;

    @JsonProperty("vrn")
    private String vrn;

    @JsonProperty("assessed_stamp_duty")
    private Double assessedStampDuty;

    @JsonProperty("future")
    private Boolean future;

    @JsonProperty("assessed_sticker_fees")
    private Double assessedStickerFees;

    @JsonProperty("cover_description")
    private String coverDescription;

    @JsonProperty("payment_received_date")
    private String paymentReceivedDate;

    @JsonProperty("payer_name")
    private String payerName;
    
    @JsonProperty("chassis_number")
    private String chassisNumber;

    @JsonProperty("engine_number")
    private String engineNumber;

    @JsonProperty("seating_capacity")
    private Integer seatingCapacity;

    @JsonProperty("sticker_vehicle_type")
    private String stickerVehicleType;

}
