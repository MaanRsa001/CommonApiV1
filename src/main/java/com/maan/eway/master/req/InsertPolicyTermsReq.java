package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InsertPolicyTermsReq  implements Serializable {

    private static final long serialVersionUID = 1L;

	@JsonProperty("PolicyTerm")
    private Integer     policyTerm;  //number
	
	@JsonProperty("PolicyTermDesc")
    private String     policyTermDesc;
	
	@JsonProperty("ProductId")
    private String     productId;
	
	@JsonProperty("SectionId")
    private String     sectionId;
	
	@JsonProperty("InsuranceId")
    private String     companyId;
		
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("Status")
    private String     status ;

	@JsonProperty("CoreAppCode")
	private String coreAppCode;

	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
		
	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	
	@JsonProperty("Type")
	private String type;

}
