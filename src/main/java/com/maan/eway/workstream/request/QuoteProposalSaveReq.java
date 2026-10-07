/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QuoteProposalSaveReq {
	
	@JsonProperty("CompanyId")
	@JsonFormat(shape = Shape.STRING)
	private Integer companyId;
	
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;
		
	@JsonProperty("CustomerReferenceNo")
	private String customerReferenceNo;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("PolicyStartDate")
	@JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
	private LocalDate policyStartDate;
	
	@JsonProperty("PolicyEndDate")
	@JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
	private LocalDate policyEndDate;
		
	@JsonProperty("SumInsured")
	@JsonFormat(shape = Shape.STRING)
	private BigDecimal sumInsured;
	
}
