package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.maan.eway.common.req.CoverNoteDetailsSaveReq;
import com.maan.eway.common.req.CustomerCommonSaveReq;
import com.maan.eway.common.service.CustomerSaveValidationService;
import com.maan.eway.error.Error;

@Service
public class CustomerSaveValidationServiceImpl implements CustomerSaveValidationService {

	private Logger log=LogManager.getLogger(CustomerSaveValidationServiceImpl.class);

	
	
	public List<Error> customerCommonSaveValidation(CustomerCommonSaveReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			
			if(StringUtils.isBlank(req.getCustomerId())) {
				errors.add(new Error("01","CustomerId","Please Enter Customer Id"));
			}
			if(StringUtils.isBlank(req.getBranchCode())) {
				errors.add(new Error("02","BranchCode","Please Enter BranchCode"));
			}
			if(StringUtils.isBlank(req.getCityCode())) {
				errors.add(new Error("03","CityCode","Please Enter CityCode"));
			}
			if(StringUtils.isBlank(req.getCountryCode())) {
				errors.add(new Error("04","CountryCode","Please Enter CountryCode"));
			}
			if(StringUtils.isBlank(req.getCreatedBy())) {
				errors.add(new Error("05","CreatedBy","Please Enter CreatedBy"));
			}
			if(StringUtils.isBlank(req.getEmailAddress())) {
				errors.add(new Error("06","EmailAddress","Please Enter EmailAddress"));
			}
			if(StringUtils.isBlank(req.getGenderId())) {
				errors.add(new Error("07","GenderId","Please Enter GenderId"));
			}
			if(StringUtils.isBlank(req.getGstNo())) {
				errors.add(new Error("08","GstNo","Please Enter GstNo"));
			}
			if(StringUtils.isBlank(req.getNameTitleId())) {
				errors.add(new Error("08","NameTitleId","Please Enter NameTitleId"));
			}
			if(StringUtils.isBlank(req.getPolicyHolderFax())) {
				errors.add(new Error("09","PolicyHolderFax","Please Enter PolicyHolderFax"));
			}
			if(StringUtils.isBlank(req.getPolicyHolderIdNumber())) {
				errors.add(new Error("10","PolicyHolderIdNumber","Please Enter PolicyHolderIdNumber"));
			}
			if(StringUtils.isBlank(req.getPolicyHolderIdTypeId())) {
				errors.add(new Error("11","PolicyHolderIdTypeId","Please Enter PolicyHolderIdTypeId"));
			}
			if(StringUtils.isBlank(req.getPolicyHolderMobileNumber())) {
				errors.add(new Error("12","PolicyHolderMobileNumber","Please Enter PolicyHolderMobileNumber"));
			}
			if(StringUtils.isBlank(req.getPolicyHolderName())) {
				errors.add(new Error("13","PolicyHolderName","Please Enter PolicyHolderName"));
			}
			if(StringUtils.isBlank(req.getPolicyHolderTypeId())) {
				errors.add(new Error("14","PolicyHolderTypeId","Please Enter PolicyHolderTypeId"));
			}
			if(StringUtils.isBlank(req.getPostalAddress())) {
				errors.add(new Error("15","PostalAddress","Please Enter PostalAddress"));
			}
			if(StringUtils.isBlank(req.getRegionCode())) {
				errors.add(new Error("16","RegionCode","Please Enter RegionCode"));
			}
			if(StringUtils.isBlank(req.getStateCode())) {
				errors.add(new Error("17","StateCode","Please Enter StateCode"));
			}
			if(StringUtils.isBlank(req.getPolicyHolderBirthDate().toString())) {
				errors.add(new Error("18","PolicyHolderBirthDate","Please Enter PolicyHolderBirthDate"));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage() ));
		}
		return errors;
	}
	public List<Error> coverNoteDetailsSaveReqValidation(CoverNoteDetailsSaveReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			
			if(StringUtils.isBlank(req.getCallBackUrl())) {
				errors.add(new Error("01","CallBackUrl","Please Enter CallBackUrl"));
			}
			if(StringUtils.isBlank(req.getCommissionPaid())) {
				errors.add(new Error("02","CommissionPaid","Please Enter CommissionPaid"));
			}
			if(StringUtils.isBlank(req.getCommissionRate())) {
				errors.add(new Error("03","CommissionRate","Please Enter CommissionRate"));
			}
			if(StringUtils.isBlank(req.getCompanyCode())) {
				errors.add(new Error("04","CompanyCode","Please Enter CompanyCode"));
			}
			if(StringUtils.isBlank(req.getCoverNoteDesc())) {
				errors.add(new Error("05","CoverNoteDesc","Please Enter CoverNoteDesc"));
			}
			if(StringUtils.isBlank(req.getCoverNoteNumber())) {
				errors.add(new Error("06","CoverNoteNumber","Please Enter CoverNoteNumber"));
			}
			if(StringUtils.isBlank(req.getCoverNoteNumber())) {
				errors.add(new Error("07","CoverNoteNumber","Please Enter CoverNoteNumber"));
			}
			
			if(StringUtils.isBlank(req.getCoverNoteTypeId())) {
				errors.add(new Error("08","CoverNoteTypeId","Please Enter CoverNoteTypeId"));
			}
			if(StringUtils.isBlank(req.getCurrenyCode())) {
				errors.add(new Error("09","CurrenyCode","Please Enter CurrenyCode"));
			}
			if(StringUtils.isBlank(req.getEndoresementPremiumEarned())) {
				errors.add(new Error("10","EndoresementPremiumEarned","Please Enter EndoresementPremiumEarned"));
			}
			if(StringUtils.isBlank(req.getEndorsementReason())) {
				errors.add(new Error("11","EndorsementReason","Please Enter EndorsementReason"));
			}
			if(StringUtils.isBlank(req.getEndorsementType())) {
				errors.add(new Error("12","EndorsementType","Please Enter EndorsementType"));
			}
			if(StringUtils.isBlank(req.getExchangeRate())) {
				errors.add(new Error("13","ExchangeRate","Please Enter ExchangeRate"));
			}
			if(StringUtils.isBlank(req.getInsuranceCompanyCode())) {
				errors.add(new Error("14","InsuranceCompanyCode","Please Enter InsuranceCompanyCode"));
			}
			if(StringUtils.isBlank(req.getOfficerName())) {
				errors.add(new Error("15","OfficerName","Please Enter OfficerName"));
			}
			if(StringUtils.isBlank(req.getOfficerTitle())) {
				errors.add(new Error("16","OfficerTitle","Please Enter OfficerTitle"));
			}
			if(StringUtils.isBlank(req.getOperativeClause())) {
				errors.add(new Error("17","OperativeClause","Please Enter OperativeClause"));
			}
			if(StringUtils.isBlank(req.getPaymentMode())) {
				errors.add(new Error("18","PaymentMode","Please Enter PaymentMode"));
			}
			if(StringUtils.isBlank(req.getPrevConverNoteReferenceNumber())) {
				errors.add(new Error("19","PrevConverNoteReferenceNumber","Please Enter PrevConverNoteReferenceNumber"));
			}
			if(StringUtils.isBlank(req.getProductCode())) {
				errors.add(new Error("20","ProductCode","Please Enter ProductCode"));
			}
			if(StringUtils.isBlank(req.getRequestId())) {
				errors.add(new Error("21","RequestId","Please Enter RequestId"));
			}
			if(StringUtils.isBlank(req.getSalePointCode())) {
				errors.add(new Error("22","SalePointCode","Please Enter SalePointCode"));
			}
			if(StringUtils.isBlank(req.getSystemCode())) {
				errors.add(new Error("23","SystemCode","Please Enter SystemCode"));
			}
			if(StringUtils.isBlank(req.getTotalPremiumExcludingTax())) {
				errors.add(new Error("24","TotalPremiumExcludingTax","Please Enter TotalPremiumExcludingTax"));
			}
			if(StringUtils.isBlank(req.getTotalPremiumIncludingTax())) {
				errors.add(new Error("25","TotalPremiumIncludingTax","Please Enter TotalPremiumIncludingTax"));
			}
			if(StringUtils.isBlank(req.getTranCompanyCode())) {
				errors.add(new Error("26","TranCompanyCode","Please Enter TranCompanyCode"));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage() ));
		}
		return errors;
	}


	
}
