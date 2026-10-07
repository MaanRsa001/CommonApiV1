package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TiraResList {

	@JsonProperty("RequestId")
	private String requestId;

	@JsonProperty("QuoteNo")
	private String policyNo;

	@JsonProperty("TiraTrackingId")
	private BigDecimal tiraTrackingId;

	@JsonProperty("ResponseId")
	private String responseId;

	@JsonProperty("MethodName")
	private String methodName;

	@JsonProperty("HitCount")
	private Integer hitCount;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("AcknowledgementId")
	private String acknowledgementId;

	@JsonProperty("StatusCode")
	private String statusCode;

	@JsonProperty("StatusDesc")
	private String statusDesc;

	@JsonFormat( pattern = "dd/MM/yyyy HH:mm:ss")
	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonProperty("ChassisNo")
	private String chassisNo;

	@JsonProperty("RequestFilePath")
	private String requestFilePath;

	@JsonProperty("ResponseFilePath")
	private String responseFilePath;
    
	@JsonProperty("StickerNo")
	private String stickerNo;	
	
	@JsonProperty("CoverNoteNo")
	private String coverNoteNo;	
	
	@JsonProperty("VehicleId")
	private String vehicleId;	
}
