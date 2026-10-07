package com.maan.eway.bond.vali;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.maan.eway.bond.Dto.BondInformationRequest;
import com.maan.eway.error.Error;

@Component
public class BondInformationValidation {

	public List<Error> saveBondInformationRequest(BondInformationRequest req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getRequestReferenceNo())) {
				errors.add(new Error("1", "RequestReferenceNo", "Please Enter RequestReferenceNo"));
			}
			if(StringUtils.isBlank(req.getQuoteNo())) {
				errors.add(new Error("2", "QuoteNo", "Please Enter QuoteNo"));	
			}
			if(StringUtils.isBlank(req.getProductId())) {
				errors.add(new Error("3", "ProductId", "Please Enter ProductId"));
			}
			if(StringUtils.isBlank(req.getCompanyId())) {
				errors.add(new Error("4", "CompanyId", "Please Enter CompanyId"));		
			}
			if(StringUtils.isBlank(req.getLocationId())) {
				errors.add(new Error("5", "LocationId", "Please Enter LocationId"));
			}
			if(StringUtils.isBlank(req.getPrincipalName())) {
				errors.add(new Error("6", "PrincipalName", "Please Enter PrincipalName"));
			}
			if(StringUtils.isBlank(req.getBusinessRegnNo())) {
				errors.add(new Error("7", "BusinessRegnNo", "Please Enter BusinessRegnNo"));
			}
			if(StringUtils.isBlank(req.getTypeOfBusiness())) {
				errors.add(new Error("8", "TypeOfBusiness", "Please Enter TypeOfBusiness"));
			}
			if(StringUtils.isBlank(req.getIndustry())) {
				errors.add(new Error("9", "Industry", "Please Enter Industry"));		
			}
			if(StringUtils.isBlank(req.getYearsBusiness())) {
				errors.add(new Error("10", "YearsBusiness", "Please Enter YearsBusiness"));
			}
			if(StringUtils.isBlank(req.getProjectName())) {
				errors.add(new Error("11", "ProjectName", "Please Enter ProjectName"));		
			}
			if(StringUtils.isBlank(req.getProjectDesc())) {
				// errors.add(new Error("12", "ProjectDesc", "Please Enter ProjectDesc"));
			}
			if(StringUtils.isBlank(req.getProjectAddress())) {
				// errors.add(new Error("13", "ProjectAddress", "Please Enter ProjectAddress"));
			}
			if(StringUtils.isBlank(req.getProjectLocation())) {
				errors.add(new Error("14", "ProjectLocation", "Please Enter ProjectLocation"));	
			}
			if(StringUtils.isBlank(req.getNameOfObligee())) {
				errors.add(new Error("15", "NameOfObligee", "Please Enter NameOfObligee"));
			}
			if(StringUtils.isBlank(req.getProjectOwnerType())) {
				errors.add(new Error("16", "ProjectOwnerType", "Please Enter ProjectOwnerType"));
			}
			if(StringUtils.isBlank(req.getProjectOwnerTypeDesc())) {
				// errors.add(new Error("17", "ProjectOwnerTypeDesc", "Please Enter ProjectOwnerTypeDesc"));
			}
			if(StringUtils.isBlank(req.getContractNo())) {
				errors.add(new Error("18", "ContractNo", "Please Enter ContractNo"));
			}
			if(StringUtils.isBlank(req.getProjectStartDate())) {
				errors.add(new Error("19", "ProjectStartDate", "Please Enter ProjectStartDate"));		
			}
			if(StringUtils.isBlank(req.getProjectEndDate())) {
				errors.add(new Error("20", "ProjectEndDate", "Please Enter ProjectEndDate"));		
			}
			if (StringUtils.isNotBlank(req.getProjectStartDate()) && StringUtils.isNotBlank(req.getProjectEndDate())) {
				try {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					LocalDate startDate = LocalDate.parse(req.getProjectStartDate(),formatter);
					LocalDate endDate = LocalDate.parse(req.getProjectEndDate(),formatter);
					if(endDate.isBefore(startDate)) {
						errors.add(new Error("21", "ProjectEndDate", "Project End Date cannot be before Start Date"));
					}
				}catch(Exception e) {
			        errors.add(new Error("21", "ProjectDates", "Invalid date format for ProjectStartDate or ProjectEndDate"));
				}
			}
			if(StringUtils.isBlank(req.getTotalContractValue())) {
				errors.add(new Error("22", "TotalContractValue", "Please Enter TotalContractValue"));		
			}
			if(StringUtils.isBlank(req.getLiquidateDamagesClause())) {
				errors.add(new Error("23", "LiquidateDamagesClause", "Please Enter LiquidateDamagesClause"));		
			}
			if(StringUtils.isBlank(req.getLiquidateDamagesClauDesc())) {
				// errors.add(new Error("24", "LiquidateDamagesClauDesc", "Please Enter LiquidateDamagesClauDesc"));		
			}
			if(StringUtils.isBlank(req.getAdvancePaymentClause())) {
				errors.add(new Error("25", "AdvancePaymentClause", "Please Enter AdvancePaymentClause"));	
			}
			if(StringUtils.isBlank(req.getAdvancePaymentClauseDesc())) {
				// errors.add(new Error("26", "AdvancePaymentClauseDesc", "Please Enter AdvancePaymentClauseDesc"));	
			}
			if(StringUtils.isBlank(req.getAdvancePaymentValue())) {
				errors.add(new Error("27", "AdvancePaymentValue", "Please Enter AdvancePaymentValue"));		
			}
			if(StringUtils.isBlank(req.getBondDuration())) {
				errors.add(new Error("28", "BondDuration", "Please Enter BondDuration"));		
			}
			if(StringUtils.isBlank(req.getFormOfBond())) {
				errors.add(new Error("29", "FormOfBond", "Please Enter FormOfBond"));	
			}
			if(StringUtils.isBlank(req.getFormOfBondDesc())) {
				// errors.add(new Error("30", "FormOfBondDesc", "Please Enter FormOfBondDesc"));		
			}
			if(StringUtils.isBlank(req.getWordingProvider())) {
				errors.add(new Error("31", "WordingProvider", "Please Enter WordingProvider"));	
			}
			if(StringUtils.isBlank(req.getWordingProviderDesc())) {
				// errors.add(new Error("32", "WordingProviderDesc", "Please Enter WordingProviderDesc"));	
			}
			if(StringUtils.isBlank(req.getJurdication())) {
				errors.add(new Error("33", "Jurdication", "Please Enter Jurdication"));	
			}
			if(StringUtils.isBlank(req.getGoverningLaw())) {
				errors.add(new Error("34", "GoverningLaw", "Please Enter GoverningLaw"));		
			}
			
			if(StringUtils.isBlank(req.getCollateralFixedDepost())) {
				errors.add(new Error("35", "CollateralFixedDepost", "Please Enter CollateralFixedDepost"));		
			}else if(Integer.parseInt(req.getCollateralFixedDepost())<0) {
				errors.add(new Error("351", "CollateralFixedDepost", "Negative Value is Not Allowed for CollateralFixedDepost"));
			}
			
			if(StringUtils.isBlank(req.getCollateralProperty())) {
				errors.add(new Error("36", "CollateralProperty", "Please Enter CollateralProperty"));
			}else if(Integer.parseInt(req.getCollateralProperty())<0) {
				errors.add(new Error("352", "CollateralProperty", "Negative Value is Not Allowed for CollateralProperty"));
			}
			
			if(StringUtils.isBlank(req.getCollateralCorporate())) {
				errors.add(new Error("37", "CollateralCorporate", "Please Enter CollateralCorporate"));
			}else if(Integer.parseInt(req.getCollateralCorporate())<0) {
				errors.add(new Error("353", "CollateralCorporate", "Negative Value is Not Allowed for CollateralCorporate"));
			}
			
			if(StringUtils.isBlank(req.getCollateralPersonal())) {
				errors.add(new Error("38", "CollateralPersonal", "Please Enter CollateralPersonal"));
			}else if(Integer.parseInt(req.getCollateralFixedDepost())<0) {
				errors.add(new Error("354", "CollateralFixedDepost", "Negative Value is Not Allowed for CollateralFixedDepost"));
			}
			
			if(StringUtils.isBlank(req.getEndorsementYn())) {
				errors.add(new Error("39", "EndorsementYn", "Please Enter EndorsementYn"));
			}else {
				if("Y".equalsIgnoreCase(req.getEndorsementYn())) {
					if(StringUtils.isBlank(req.getEndorsementDate())) {
						errors.add(new Error("40", "EndorsementDate", "Please Enter EndorsementDate"));
					}
					if(StringUtils.isBlank(req.getEndorsementEffectiveDate())) {
						errors.add(new Error("41", "EndorsementEffectiveDate", "Please Enter EndorsementEffectiveDate"));
					}
					if(StringUtils.isBlank(req.getEndorsementRemarks())) {
						errors.add(new Error("42", "EndorsementRemarks", "Please Enter EndorsementRemarks"));
					}
					if(StringUtils.isBlank(req.getEndorsementType())) {
						errors.add(new Error("43", "EndorsementType", "Please Enter EndorsementType"));
					}
					if(StringUtils.isBlank(req.getEndorsementTypeDesc())) {
						errors.add(new Error("44", "EndorsementTypeDesc", "Please Enter EndorsementTypeDesc"));
					}
					if(StringUtils.isBlank(req.getEndtCategoryDesc())) {
						errors.add(new Error("45", "EndtCategoryDesc", "Please Enter EndtCategoryDesc"));
					}
					if(StringUtils.isBlank(req.getEndtCount())) {
						errors.add(new Error("46", "EndtCount", "Please Enter EndtCount"));
					}
					if(StringUtils.isBlank(req.getEndtPrevPolicyNo())) {
						errors.add(new Error("47", "EndtPrevPolicyNo", "Please Enter EndtPrevPolicyNo"));
					}
					if(StringUtils.isBlank(req.getEndtPrevQuoteNo())) {
						errors.add(new Error("48", "EndtPrevQuoteNo", "Please Enter EndtPrevQuoteNo"));
					}
					if(StringUtils.isBlank(req.getEndtStatus())) {
						errors.add(new Error("49", "EndtStatus", "Please Enter EndtStatus"));
					}
					if(StringUtils.isBlank(req.getIsFinanceEndt())) {
						errors.add(new Error("50", "IsFinanceEndt", "Please Enter IsFinanceEndt"));
					}
				}
			}
			if(StringUtils.isBlank(req.getOrginalPolicyNo())) {
				// errors.add(new Error("51", "OrginalPolicyNo", "Please Enter OrginalPolicyNo"));
			}
			if(StringUtils.isBlank(req.getLoginId())) {
				errors.add(new Error("52", "LoginId", "Please Enter LoginId"));
			}
			if(StringUtils.isBlank(req.getStatus())) {
				errors.add(new Error("53", "Status", "Please Enter Status"));
			}
			if(StringUtils.isBlank(req.getRemarks())) {
				// errors.add(new Error("54", "Remarks", "Please Enter Remarks"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return errors;
	}

	
	
}
