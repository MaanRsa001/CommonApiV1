package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PremiaConfigMasterSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("PremiaId")
    private String premiaId ;
    
	@JsonProperty("PremiaTableName")
    private String premiaTableName  ;
    

	@JsonProperty("EntityName")
    private String entityName  ;
    
	
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("QueryKey")
	private String queryKey;
	
	@JsonProperty("SourceTableName")
	private List<String> sourceTableName;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	
}
