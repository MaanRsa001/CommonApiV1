package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.req.TinyUrlYnDetailsSaveReq;

import lombok.Data;

@Data
public class TinyUrlMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("Sno")
	private String sno;

	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("AppUrl")
	private String appUrl;
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("ProductId")
	private String productId;
		
	@JsonProperty("BranchCode")
	private String branchCode;
		
	@JsonProperty("Status")
	private String status;

	@JsonProperty("Remarks")
	private String remarks;
	
	@JsonProperty("CreatedBy")
	private String createdBy;


	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;		
		
	@JsonProperty("UpdatedBy")
	private String updatedBy;
	
	@JsonProperty("NotifYn")
	private String notifYn;

	@JsonProperty("NotifDesc")
	private String notifDesc;

	@JsonProperty("RequestYn")
	private String requestYn;
	
	@JsonProperty("TinyUrlYnDetails")
	private List<TinyUrlYnDetailsSaveReq> tinyUrlYnDetails;
}
