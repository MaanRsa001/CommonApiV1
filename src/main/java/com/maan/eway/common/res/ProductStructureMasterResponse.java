package com.maan.eway.common.res;

import java.sql.Date;
import java.util.List;

//import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProductStructureMasterResponse {

		@JsonProperty("CompanyId")
		private String companyid;

		@JsonProperty("ProductId")
		private String productId;
		
		@JsonProperty("SectionId")
		private String sectionId;
		
	    @JsonProperty("SectionName")
		private String sectionName;
	    
	    @JsonProperty("SectionNameLocal")
	   	private String sectionNameLocal;
	    
	    @JsonProperty("IndustryTypeId")
	   	private String industryTypeId;
	    
	    @JsonProperty("IndustryTypeDesc")
	   	private String industryTypeDesc;
	    
	    @JsonProperty("IndustryTypeLocalDesc")
	   	private String industryTypeLocalDesc;
	    
	    @JsonProperty("Status")
	   	private String Status;
	    
	    @JsonProperty("DisplayOrder")
	    private String displayOrder;
	    
//	    @JsonFormat(pattern = "dd/MM/yyyy")
	    @JsonProperty("EntryDate")
	    private Date entryDate;
	    
	    @JsonProperty("CreatedBy")
	    private String CreatedBy;
	    
	    @JsonProperty("amendId")
	    private String amendId;
	    
	    @JsonProperty("remarks")
	    private String remarks;
	    
	    @JsonProperty("BodyTypeId")
		private List<String> bodyTypeIds;
	    
	    @JsonProperty("CoreAppCode")
	    private String coreAppCode;


}
