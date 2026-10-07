package com.maan.eway.notification.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.notification.req.statealgo.NotificationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
	@JsonProperty("Companyname")
	private String     companyName  ;
	@JsonProperty("Productname")
	private String     productName  ;
	@JsonProperty("Sectionname")
	private String     sectionName  ;
	@JsonProperty("Statusmessage")
	private String     statusMessage ;
	@JsonProperty("Otp")
	private Integer    otp;
	@JsonProperty("Policyno")
	private String policyNo     ;
	@JsonProperty("Quoteno")
	private String quoteNo      ;
	@JsonProperty("Notifdescription")
	private String     notifDescription ;
	//@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@JsonProperty("Notifcationdate")
	private Date       notifcationDate ;	
	@JsonProperty("Notifpriority")
	private Integer    notifPriority ;
	@JsonProperty("Tinyurl")
	private String     tinyUrl      ;
	@JsonProperty("Notiftemplatename")
    private String     notifTemplatename ;
	@JsonProperty("Notifpushedstatus")
    private NotificationStatus notifPushedStatus ;
	
	@JsonProperty("Customer")
	private Customer customer;
	@JsonProperty("Broker")
	private Broker broker;
	@JsonProperty("UnderWriter")
	private List<UnderWriter> underwriters;
	@JsonProperty("CompanyId")
	private String     companyid;
	@JsonProperty("ProductId")
	private Integer    productid;
	
	@JsonProperty("Attachments")
	private List<String> attachments;
	
	@JsonProperty("PushedBy")
	private String pushedBy;
	
	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("RequestReferenceNo")
	private String refNo;
	
	@JsonProperty("RegistrationNo")
	private String regNo;
	
	@JsonProperty("InsuranceClass")
	private String insuranceClass;
	
	@JsonProperty("PolicyStartDate")
	private String startDate;
	
	@JsonProperty("PolicyEndDate")
	private String endDate;
	
	@JsonProperty("PremiumAmount")
	private String premiumAmount;
	
	@JsonProperty("StickerNumber")
	private String stickerNo;
	

}

