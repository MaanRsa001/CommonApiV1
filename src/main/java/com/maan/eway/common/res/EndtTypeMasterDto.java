package com.maan.eway.common.res;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EndtTypeMasterDto implements Serializable  {
    private static final long serialVersionUID = 1L;

    //----------------------------------------------------------------------
    // ENTITY PRIMARY KEY 
    //----------------------------------------------------------------------
	@JsonProperty("Endttypeid")
    private Integer    endtTypeId   ;
	@JsonProperty("Endttypecategoryid")
    private Integer    endtTypeCategoryId ;
	@JsonProperty("Productid")
    private Integer    productId    ;
	@JsonProperty("Companyid")
    private String     companyId    ;

    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
	@JsonProperty("Endttype")
    private String     endtType     ;
	@JsonProperty("Endttypedesc")
    private String     endtTypeDesc ;
	@JsonProperty("Endttypecategory")
    private String     endtTypeCategory ;
	@JsonProperty("Status")
    private String     status       ;
	@JsonProperty("Priority")
    private Integer    priority     ;
	@JsonProperty("Endtdependantids")
    private String     endtDependantIds ;
	@JsonProperty("Endtdependantfields")
    private String     endtDependantFields ;
	@JsonProperty("Coreappcode")
    private BigDecimal coreAppCode  ;
	@JsonProperty("Regulatorycode")
    private String     regulatoryCode ;
	@JsonProperty("Calctypeid")
    private String     calcTypeId   ;
	@JsonProperty("Endtfeeyn")
    private String     endtFeeYn    ;
	@JsonProperty("Endtfeepercent")
    private String     endtFeePercent ;
	@JsonProperty("Remarks")
    private String     remarks      ;
	@JsonProperty("Entrydate")
    private Date       entryDate    ;
	@JsonProperty("Effectivedatestart")
    private Date       effectiveDateStart ;
	@JsonProperty("Effectivedateend")
    private Date       effectiveDateEnd ;
	@JsonProperty("Createdby")
    private String     createdBy    ;
	@JsonProperty("Updatedby")
    private String     updatedBy    ;
	@JsonProperty("Updateddate")
    private Date       updatedDate  ;
	@JsonProperty("Amendid")
    private Integer    amendId      ;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("Endorsementeffdate")
    private Date    endorsementeffdate;
	
	@JsonProperty("endorsementPolicyNo")
    private String    endorsementPolicyNo      ;
	
	@JsonProperty("PolicyNo")
    private String    policyNo;

    //----------------------------------------------------------------------
    // ENTITY LINKS ( RELATIONSHIP )
    //----------------------------------------------------------------------

      
	  }
