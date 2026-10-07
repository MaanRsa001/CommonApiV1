package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ViewReportDetailsRes {

	@JsonProperty("CustomerDetails")
	private ReportCustomerInfoRes  customerDetails ;
	
	@JsonProperty("QuotationDetails")
	private ReportQuoteInfoRes  quoteDetails ;
	
	@JsonProperty("CompanyDetails")
	private ReportCompanyInfoRes companydetails;
	
	@JsonProperty("BrokerDetails")
	private ReportBrokerInfoRes brokerDetails;
	
	@JsonProperty("LocationDetails")
	private List<ReportLocationInfoRes> locationDetails;
	
	@JsonProperty("Attachments")
	private List<AttachMentRes> attachments;
	
	@JsonProperty("ProductSectionMaster")
	private List<ReportSectionMasterRes> productSectionMaster;
	
	@JsonProperty("CommonSectionRes")
	private List<CommonSectionRes> commonSectionRes;
	
	
}
