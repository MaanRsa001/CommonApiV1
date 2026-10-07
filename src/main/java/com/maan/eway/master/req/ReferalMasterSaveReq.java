package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReferalMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("ReferalId")
	private String referalId;

	@JsonProperty("ReferalName")
	private String referalName;	

	@JsonProperty("ReferalDesc")
	private String referalDesc;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;

	@JsonProperty("AmendId")
	private Integer amendId;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("CreatedBy")
	private String createdBy;

	@JsonProperty("MotorYn")
	private String motorYn;
	
}
