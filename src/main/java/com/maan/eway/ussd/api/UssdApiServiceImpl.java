package com.maan.eway.ussd.api;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.repository.EwayVehicleMakemodelMasterDetailRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorBodyTypeMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorMakeMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;



@Service
public class UssdApiServiceImpl implements UssdApiService {
	
	@Autowired
	private ObjectMapper mapper;
	@Autowired
	private Gson objectPrint;
	@Autowired
	private SectionDataDetailsRepository sectionRepo;
	@Autowired
	private MotorDataDetailsRepository motorDataRepo;
	@Autowired
	private PersonalInfoRepository personalInfoRepo;
	@Autowired
	private PolicyCoverDataRepository policyCoverDataRepo;
	@Autowired
	private HomePositionMasterRepository homeRepo;
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService;
	private Logger log = LogManager.getLogger(UssdApiServiceImpl.class);
	
	@Autowired
	private MotorBodyTypeMasterRepository bodyTypeRepo;
	
	@Autowired
	private MotorMakeMasterRepository makeRepo;
	
	@Autowired
	private EwayVehicleMakemodelMasterDetailRepository modelRepo;
	
	@Autowired
	private PolicyDrcrDetailRepository policyDrCrRepo;
	
	@Value("${tokenApi}")
	private String tokenApi;
	
	@Value("${makePaymentApi}")
	private String makePaymentApi;
	
	@Value("${insertPaymentApi}")
	private String insertPaymentApi;
	
	

	@Override
	public Object ussdApi(UssdApiReq req) {

		List<String> errorList = new ArrayList<>();
		
		//validationBlock
		errorList = ussdValidation(req);
		if(errorList.isEmpty()) {
			
	      String response = "";
	      String exception = "";
	      String companyId = "100002";
	      String productId = "5";
	      String reqId = req.getRequestId() == null ? "" : req.getRequestId();
	      String coverNoteType = req.getCoverNoteType() == null ? "0" : req.getCoverNoteType();
	      String coverNoteNumber = req.getCoverNoteNumber() == null ? "" : req.getCoverNoteNumber();
	      String prevCoverNoteRefNo = req.getPrevCoverNoteReferenceNumber() == null ? "" : req.getPrevCoverNoteReferenceNumber();
	      String coverNoteStartDate = req.getCoverNoteStartDate() == null ? null :req.getCoverNoteStartDate();
	      String coverNoteEndDate = req.getCoverNoteEndDate() == null ? null :req.getCoverNoteEndDate();
          String coverNoteDesc = req.getCoverNoteDesc() == null ? "" :req.getCoverNoteDesc();
	      String operativeClause = req.getOperativeClause() == null ? "" :req.getOperativeClause();
	      String paymentMode = req.getPaymentMode() == null ? "" :req.getPaymentMode();
	      String currencyCode = req.getCurrencyCode() == null ? "" : req.getCurrencyCode();
	      String exchangeRate = req.getExchangeRate() == null ? "0" : req.getExchangeRate();
	      Double totalPremiumExclTax = req.getTotalPremiumExcludingTax() == null ? Double.valueOf(0) : Double.valueOf(req.getTotalPremiumExcludingTax());
	      Double totalPremiumIncludingTax = req.getTotalPremiumIncludingTax() == null ? Double.valueOf(0)  : Double.valueOf(req.getTotalPremiumIncludingTax());
	      String paidCommission = req.getCommissionPaid() == null ? "0" : req.getCommissionPaid();
	      String commissionRate = req.getCommissionRate() == null ? "0" : req.getCommissionRate();
	      String officerName = req.getOfficerName() == null ? "" :req.getOfficerName();
	      String officerTitle = req.getOfficerTitle() == null ? "" :req.getOfficerTitle();
	      String productCode = req.getProductCode() == null ? "" : req.getProductCode();
	      String endorsementType = req.getEndorsementType() == null ? "0" : req.getEndorsementType();
	      String endorsementReason = req.getEndorsementReason() == null ? "0" : req.getEndorsementReason();
	      String endorsementPremiumEarned = req.getEndorsementPremiumEarned() == null ? "0" : req.getEndorsementPremiumEarned();
	      String riskCode = req.getRiskCode() == null ? "" : req.getRiskCode();
	      Double sumInsured = req.getSumInsured() == null ? Double.valueOf(0) : Double.valueOf(req.getSumInsured());
	      Double sumInsuredEquivalent = req.getSumInsuredEquivalent() == null ? Double.valueOf(0) : Double.valueOf(req.getSumInsuredEquivalent());
	      Double premiumRate = req.getPremiumRate() == null ? Double.valueOf(0) : Double.valueOf(req.getPremiumRate());
	      Double premiumBeforeDiscount = req.getPremiumBeforeDiscount() == null ? Double.valueOf(0) : Double.valueOf(req.getPremiumBeforeDiscount());
	      Double premiumAfterDiscount = req.getPremiumAfterDiscount() == null ? Double.valueOf(0) : Double.valueOf(req.getPremiumAfterDiscount());
	      Double premiumExcludingTaxEquivalent = req.getPremiumExcludingTaxEquivalent() == null ? Double.valueOf(0) : Double.valueOf(req.getPremiumExcludingTaxEquivalent());
	      Double premiumIncludingTax = req.getPremiumIncludingTax() == null ? Double.valueOf(0) : Double.valueOf(req.getPremiumIncludingTax());
	      String discountType = req.getDiscountType() == null ? "" : req.getPremiumIncludingTax();
	      Double discountRate = req.getDiscountRate() == null ? Double.valueOf(0) : Double.valueOf(req.getDiscountRate());
	      Double discountAmount = req.getDiscountAmount() == null ? Double.valueOf(0) : Double.valueOf(req.getDiscountAmount());
	      String taxCode = req.getTaxCode() == null ? null : req.getTaxCode();
	      String isTaxExempted = req.getIsTaxExempted() == null ? "" : req.getIsTaxExempted();
	      String taxExemptionType = req.getTaxExemptionType() == null ? "" : req.getTaxExemptionType();
	      String taxExemptionReference = req.getTaxExemptionReference() == null ? "" : req.getTaxExemptionReference();
	      Double taxRate = req.getTaxRate() == null ? 0.0D : Double.valueOf(req.getTaxRate());
	      Double taxAmount = req.getTaxAmount() == null ? 0.0D : Double.valueOf(req.getTaxAmount());
	      String subjectMatterReference = req.getSubjectMatterReference() == null ? "" : req.getSubjectMatterReference();
	      String subjectMatterDesc = req.getSubjectMatterDesc() == null ? "" : req.getSubjectMatterDesc();	     
	      String policyHolderName = req.getPolicyHolderName() == null ? "" : req.getPolicyHolderName();
	      String policyHoldeDateOfBirth = req.getPolicyHoldeDateOfBirth() == null ? null : req.getPolicyHoldeDateOfBirth();
	      String policyHolderType = req.getPolicyHolderType() == null ? "0" : req.getPolicyHolderIdType();
	      String policyHolderNumber = req.getPolicyHolderNumber() == null ? "" : req.getPolicyHolderNumber();
	      String policyHolderIdType = req.getPolicyHolderIdType() == null ? "0" : req.getPolicyHolderIdType();
	      String gender = req.getGender() == null ? "" : req.getGender();
	      String countryCode = req.getCountryCode() == null ? "" : req.getCountryCode();
	      String cityCode = req.getCityCode() == null ? "" : req.getCityCode();	      
	      String region = req.getRegion() == null ? "" : req.getRegion();
	      String district = req.getDistrict() == null ? "" : req.getDistrict();
	      String street = req.getStreet() == null ? "" : req.getStreet();
	      String policyHolderPhoneNumber = req.getPolicyHolderPhoneNumber() == null ? "" : req.getPolicyHolderPhoneNumber();
	      String policyHolderFax = req.getPolicyHolderFax() == null ? "" : req.getPolicyHolderFax();
	      String postalAddress = req.getPostalAddress() == null ? "Address1" : req.getPostalAddress();
	      String emailAddress = req.getEmailAddress() == null ? "" : req.getEmailAddress();
	      String motorCategory = req.getMotorCategory() == null ? "0" : req.getMotorCategory();
	      String motorType = req.getMotorType() == null ? "0" : req.getMotorType();
	      String regNo = req.getRegistrationNumber() == null ? "" : req.getRegistrationNumber();
	      String chassisNumber = req.getChassisNumber() == null ? "" : req.getChassisNumber();
	      String make = req.getMake() == null ? "" : req.getMake();
	      String model = req.getModel() == null ? "" : req.getModel();
	      String modelNo = req.getModelNumber() == null ? "" : req.getModelNumber();
	      String bodyType = req.getBodyType() == null ? "" : req.getBodyType();
	      String color = req.getColor() == null ? "" : req.getColor();
	      String engineNo = req.getEngineNumber() == null ? "" : req.getEngineNumber();
	      String engineCapacity = req.getEngineCapacity() == null ? "" : req.getEngineCapacity();
	      String fuel = req.getFuelUsed() == null ? "" : req.getFuelUsed();
	      String noOfAxles = req.getNumberOfAxles() == null ? "0" : req.getNumberOfAxles();
	      String axleDistance = req.getAxleDistance() == null ? "0" : req.getAxleDistance();
	      String sittingCapacity = req.getSittingCapacity() == null ? "0" : req.getSittingCapacity();
	      String yearOfManufacture = req.getYearOfManufacture() == null ? null : req.getYearOfManufacture();
	      String tareWeight = req.getTareWeight() == null ? "0" : req.getTareWeight();
	      String grossWeight = req.getGrossWeight() == null ? "0" : req.getGrossWeight();
	      String motorUsage = req.getMotorUsage() == null ? null : req.getMotorUsage();
	      String ownerName = req.getOwnerName() == null ? "" : req.getOwnerName();
	      String ownerCategory = req.getOwnerCategory() == null ? null : req.getOwnerCategory();
	      String ownerAddress = req.getOwnerAddress() == null ? null : req.getOwnerAddress();
	      String coverNoteReferenceNumber = req.getPrevCoverNoteReferenceNumber() == null ? "" : req.getPrevCoverNoteReferenceNumber();
	      String stickerNumber = req.getStickerNumber() == null ? "" : req.getStickerNumber();
	      String transactionId = req.getTransactionId() == null ? "" : req.getTransactionId();
	      String transactionAmount = req.getTransactionAmount() == null ? "" : req.getTransactionAmount();
	      

	      String coverNoteTypeDesc = "";
	      String endorsementTypeDesc = "";
	      String policyHolderTypeDesc = "";
	      String policyHolderIdTypeDesc = "";
	      String motorCategoryDesc = "";
	      String motorTypeDesc = "";
	      String motorUsageDesc = "";
	      String ownerCategoryDesc = "";
	      String motorUsageId = "";
	      
	      //name validation
	      policyHolderName = policyHolderName.replaceAll("[^a-zA-Z0-9 ]", "");
	      ownerName = ownerName.replaceAll("[^a-zA-Z0-9 ]", "");
	      
	      log.info("POLICY HOLDER NAME : " + policyHolderName);
	      
	      if (coverNoteType.equalsIgnoreCase("1")) {
	         coverNoteTypeDesc = "New";
	      } else if (coverNoteType.equalsIgnoreCase("2")) {
	         coverNoteTypeDesc = "Renew";
	      } else if (coverNoteType.equalsIgnoreCase("3")) {
	         coverNoteTypeDesc = "Endorsement";
	      }

	      if (endorsementType.equalsIgnoreCase("1")) {
	         endorsementTypeDesc = "Increasing Premium Charged";
	      } else if (endorsementType.equalsIgnoreCase("2")) {
	         endorsementTypeDesc = "Decreasing Premium Charged";
	      } else if (endorsementType.equalsIgnoreCase("3")) {
	         endorsementTypeDesc = "Cover Details Change";
	      } else if (endorsementType.equalsIgnoreCase("4")) {
	         endorsementTypeDesc = "Cancellation";
	      }

	      if (policyHolderType.equalsIgnoreCase("1")) {
	         policyHolderTypeDesc = "Individual";
	      } else if (policyHolderType.equalsIgnoreCase("2")) {
	         policyHolderTypeDesc = "Cooperate";
	      }

	      if (policyHolderIdType.equalsIgnoreCase("1")) {
	         policyHolderIdTypeDesc = "National Identification Number(NIN)";
	      } else if (policyHolderIdType.equalsIgnoreCase("2")) {
	         policyHolderIdTypeDesc = "Voters Registration Number";
	      } else if (policyHolderIdType.equalsIgnoreCase("3")) {
	         policyHolderIdTypeDesc = "Passport Number";
	      } else if (policyHolderIdType.equalsIgnoreCase("4")) {
	         policyHolderIdTypeDesc = "Driving License";
	      } else if (policyHolderIdType.equalsIgnoreCase("5")) {
	         policyHolderIdTypeDesc = "Zanzibar Resident Id(ZANID)";
	      } else if (policyHolderIdType.equalsIgnoreCase("6")) {
	         policyHolderIdTypeDesc = "Tax Identification Number(TIN)";
	      } else if (policyHolderIdType.equalsIgnoreCase("7")) {
	         policyHolderIdTypeDesc = "Company Incorporation Certificate Number";
	      }

	      if (motorCategory.equalsIgnoreCase("1")) {
	         motorCategoryDesc = "Motor vehicle";
	      } else if (motorCategory.equalsIgnoreCase("2")) {
	         motorCategoryDesc = "Motor cycle";
	      }

	      if (motorType.equalsIgnoreCase("1")) {
	         motorTypeDesc = "Registered";
	      } else if (motorType.equalsIgnoreCase("2")) {
	         motorTypeDesc = "In transit";
	      }

	      if (motorUsage.equalsIgnoreCase("1")) {
	         motorUsageDesc = "Private Vehicle";
	         motorUsageId = "14";
	      } else if (motorUsage.equalsIgnoreCase("2")) {
	         motorUsageDesc = "Commercial";
	         motorUsageId = "4";
	      }

	      if (ownerCategory.equalsIgnoreCase("1")) {
	         ownerCategoryDesc = "Sole Propriator";
	      } else if (ownerCategory.equalsIgnoreCase("2")) {
	         ownerCategoryDesc = "Corporate";
	      }

	      String productName = "";
	      if (productCode.equalsIgnoreCase("5")) {
	         productName = "Motor";
	      } else if (productCode.equalsIgnoreCase("46")) {
	         productName = "Short Term Policy";
	      }

	      String insurancePeriod = "";
	      if (productName.equalsIgnoreCase("Short Term Policy")) {
	         insurancePeriod = "30";
	      } else {
	         insurancePeriod = "366";
	      }

	      Integer locationId = 1;
	      String branch = "Dar Es Salaam";
	      String branchCode = "02";
	      String vehicleId = "1";
	      Integer sectionId = 10;
	      String sectionName = "MOTOR Private Vehicles";
	      String companyName = "Alliance Insurance Corporation Limited";
	      String agencyCode = "13231";
	      String custRefNo = "";
	      String quoteNo = "";
	      String reqRefNo = "";
	      String customerId = "";
	      
	      Long commissionPercentage = Long.valueOf(commissionRate);
	      Long exchange = Long.valueOf(exchangeRate);
	      Long commissionAmount = Long.valueOf(paidCommission);
	      
	      
	      
	      //Vehicle Age
	      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	      int manufactureYear = Integer.valueOf(yearOfManufacture);
	      int currentYear = LocalDate.now().getYear();
	      int vehAge = currentYear - manufactureYear;
	      
	      //Customer Age
	      String cusDob = req.getPolicyHoldeDateOfBirth();
	      String[] dateParts = cusDob.split("/");
	      int birthYear = Integer.parseInt(dateParts[2]);
	      int cusAge = currentYear - birthYear;
	      
	      String genderDesc = "";
	      if (gender.equalsIgnoreCase("M")) {
	         genderDesc = "Male";
	      } else if (gender.equalsIgnoreCase("F")) {
	         genderDesc = "Female";
	      }
	      
	      //Motor Ids 
	     List<Integer> bodyTypeValue = bodyTypeRepo.findBodyIdWithMaxAmendId(bodyType);
	     String bodyTypeId="",makeId="",modelId="";
	     if(bodyTypeValue.isEmpty()) {
	    	 bodyTypeId = "99999"; 
	     }else {
	    	 bodyTypeId = bodyTypeValue.get(0).toString();
	     }
	     
	     
	     List<Integer> makeIdValue = makeRepo.findMakeIdWithMaxAmendId(make);
	     
	     if(makeIdValue.isEmpty()) {
	    	 makeId = "99999"; 
	     }else {
	    	 makeId = makeIdValue.get(0).toString();
	     }
	    //  makeId = makeIdValue == null ? "99999" : String.valueOf(makeIdValue.get(0));
	     
	     List<Integer> modelIdValue = modelRepo.findModelIdWithMaxAmendId(model);
	     
	     if(modelIdValue.isEmpty()) {
	    	 modelId = "99999"; 
	     }else {
	    	 modelId = modelIdValue.get(0).toString();
	     }
	     // modelId = modelIdValue == null ? "99999" : String.valueOf(modelIdValue.get(0));
	     
	     
	     //String modelId = modelRepo

	      LocalDate currentDate = LocalDate.now();
	      LocalTime constantTime = LocalTime.of(5, 30);
	      LocalDateTime dateTimeWithConstantTime = LocalDateTime.of(currentDate, constantTime);
	      Timestamp inceptionDate = Timestamp.valueOf(dateTimeWithConstantTime);
	      LocalDate oneYr = currentDate.plusDays(364L);
	      LocalDateTime dateTimeWithConstantTimeExp = LocalDateTime.of(oneYr, constantTime);
	      Timestamp expireDate = Timestamp.valueOf(dateTimeWithConstantTimeExp);
	      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	      
	      Date effectiveStartDate = null,effectiveEndDate= null;
		try {
			effectiveStartDate = sdf.parse(coverNoteStartDate);
		    effectiveEndDate = sdf.parse(coverNoteEndDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	      
	      //checking
	      List<MotorDataDetails> motorList = motorDataRepo.findByRegistrationNumber(regNo);
	      
	      List<String> qouteList = motorList.stream().map(mot -> mot.getQuoteNo()).collect(Collectors.toList());
	      
	      List<HomePositionMaster> positionList = homeRepo.findAllByQuoteNoIn(qouteList);
	      
	      positionList = positionList.stream().filter(pos -> (StringUtils.isNotBlank(pos.getStatus()) && (pos.getStatus().equalsIgnoreCase("P"))
	    		 && (StringUtils.isNotBlank(pos.getPolicyNo())) && (pos.getCoverNoteReferenceNo().equals(coverNoteNumber)) &&
	    		 (pos.getStickerNumber().equals(stickerNumber)))).collect(Collectors.toList());
	      
	    		  
	   //   positionList = positionList.stream().filter(pos -> (pos.getInceptionDate().before(effectiveStartDate)) || (pos.getInceptionDate().equals(effectiveStartDate)))
	   // 		  .collect(Collectors.toList());
	      
	   //   positionList = positionList.stream().filter(pos -> (pos.getExpiryDate().before(effectiveEndDate)) || (pos.getExpiryDate().equals(effectiveEndDate)))
	   // 		  .collect(Collectors.toList());
	      
	     // positionList = positionList.stream().filter(pos -> (pos.getCoverNoteReferenceNo().equals(coverNoteNumber))
    	//		  && (pos.getStickerNumber().equals(stickerNumber)) && (pos.getStickerNumber().equals(stickerNumber))).collect(Collectors.toList());
	    		  
	      
	      if(positionList.isEmpty()) {
	    	  
	    		//Customer Reference Sequence
		 	      SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
		 	      generateSeqReq.setInsuranceId(companyId);
		 	      generateSeqReq.setProductId(productId);
		 	      generateSeqReq.setType("1");
		 	      generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");	      
		 	      custRefNo = genSeqNoService.generateSeqCall(generateSeqReq);
		 	      
		 	      //Req Reference Sequence
		 	      generateSeqReq.setType("2");
		 	      generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
		 	      reqRefNo = genSeqNoService.generateSeqCall(generateSeqReq);
		 	      
		 	      //Customer Id Sequence
		 	      generateSeqReq.setType("3");
		 	      generateSeqReq.setTypeDesc("CUSTOMER_ID");
		 	      customerId = genSeqNoService.generateSeqCall(generateSeqReq);
		 	      
		 	      //Quote No Sequence
		 	      generateSeqReq.setType("4");
		 	      generateSeqReq.setTypeDesc("QUOTE_NO");
		 	      quoteNo = genSeqNoService.generateSeqCall(generateSeqReq);
		 	      
		 	      //save motordata
		 	      MotorDataDetails saveMotorData = new MotorDataDetails();
		 	      saveMotorData.setQuoteNo(quoteNo);
		 	      saveMotorData.setRequestReferenceNo(reqRefNo);
		 	      saveMotorData.setLocationId(locationId);
		 	      saveMotorData.setBranchNameLocal(branch);
		 	      saveMotorData.setVehicleId(vehicleId);
		 	      saveMotorData.setProductId(Integer.valueOf(productId));
		 	      saveMotorData.setProductName("Motor");
		 	      saveMotorData.setSectionId(sectionId);
		 	      saveMotorData.setSectionName(sectionName);
		 	      saveMotorData.setCompanyId(companyId);
		 	      saveMotorData.setCompanyName(companyName);
		 	      saveMotorData.setBranchCode(branchCode);
		 	      saveMotorData.setCustomerId(customerId);
		 	      saveMotorData.setVdRefno("0000");
		 	      saveMotorData.setCdRefno("0000");
		 	      saveMotorData.setMsRefno("0000");
		 	      saveMotorData.setIdNumber(policyHolderNumber);
		 	      saveMotorData.setAccident("N");
		 	      saveMotorData.setGpsTrackingInstalled("N");
		 	      saveMotorData.setWindScreenCoverRequired("N");
		 	      saveMotorData.setInsuranceType(sectionId.toString());
		 	      saveMotorData.setInsuranceTypeDesc(sectionName);
		 	      saveMotorData.setMotorCategory(motorCategory);
		 	      saveMotorData.setMotorCategoryDesc(motorCategoryDesc);
		 	      saveMotorData.setMotorUsage(motorUsageId);
		 	      saveMotorData.setRegistrationNumber(regNo);
		 	      saveMotorData.setChassisNumber(chassisNumber);
		 	      saveMotorData.setVehicleMake(makeId);
		 	      saveMotorData.setVehicleMakeDesc(make);
		 	      saveMotorData.setVehcileModel(modelId);
		 	      saveMotorData.setVehcileModelDesc(model);
		 	      saveMotorData.setVehicleType(bodyTypeId);
		 	      saveMotorData.setVehicleTypeDesc(bodyType);
		 	      saveMotorData.setModelNumber(modelNo);
		 	      saveMotorData.setEngineNumber(engineNo);
		 	      saveMotorData.setFuelType(fuel);
		 	      saveMotorData.setFuelTypeDesc(fuel);
		 	      saveMotorData.setSeatingCapacity(Double.valueOf(sittingCapacity));
		 	      saveMotorData.setCubicCapacity(Double.valueOf(engineCapacity));
		 	      saveMotorData.setColor(color);
		 	      saveMotorData.setColorDesc(color);
		 	      saveMotorData.setGrossWeight(Double.valueOf(grossWeight));
		 	      saveMotorData.setTareWeight(Double.valueOf(tareWeight));
		 	      saveMotorData.setActualPremiumFc(totalPremiumExclTax);
		 	      saveMotorData.setActualPremiumLc(totalPremiumExclTax);
		 	      saveMotorData.setOverallPremiumFc(totalPremiumIncludingTax);
		 	      saveMotorData.setOverallPremiumLc(totalPremiumIncludingTax);
		 	      saveMotorData.setPeriodOfInsurance(insurancePeriod);
		 	      saveMotorData.setWindScreenSumInsured(0.0D);
		 	      saveMotorData.setAcccessoriesSumInsured(0.0D);
		 	      saveMotorData.setNumberOfAxels(Integer.valueOf(noOfAxles));
		 	      saveMotorData.setAxelDistance(Double.valueOf(axleDistance));
		 	      saveMotorData.setSumInsured(sumInsured);
		 	      saveMotorData.setPolicyType("3");
		 	      saveMotorData.setPolicyTypeDesc("TPO");
		 	      saveMotorData.setAgencyCode(agencyCode);
		 	      saveMotorData.setInsuranceClass("3");
		 	      saveMotorData.setInsuranceClassDesc("Third Party Vehicle");
		 	      saveMotorData.setOwnerCategory(ownerCategory);
		 	      saveMotorData.setManufactureAge(vehAge);
		 	      saveMotorData.setNcdYn("N");
		 	      saveMotorData.setManufactureYear(manufactureYear);
		 	      saveMotorData.setUpdatedDate(new Date());
		 	      saveMotorData.setCreatedBy("UssdBroker");
		 	      saveMotorData.setUpdatedBy("UssdBroker");
		 	      saveMotorData.setEntryDate(new Date());
		 	      saveMotorData.setPolicyStartDate(effectiveStartDate);
		 	      saveMotorData.setPolicyEndDate(effectiveEndDate);
		 	      saveMotorData.setCurrency(currencyCode);
		 	      saveMotorData.setExchangeRate(Double.valueOf(exchangeRate));
		 	      saveMotorData.setFleetOwnerYn("Y");
		 	      saveMotorData.setNoOfVehicles(1);
		 	      saveMotorData.setNoOfCompehensives(0);
		 	      saveMotorData.setClaimRatio(0.0D);
		 	      saveMotorData.setCollateralYn("N");
		 	      saveMotorData.setApplicationId("1");
		 	      saveMotorData.setBrokerCode(agencyCode);
		 	      saveMotorData.setSubUserType("USSD");
		 	      saveMotorData.setLoginId("UssdBroker");
		 	      saveMotorData.setSavedFrom("WEB");
		 	      saveMotorData.setBrokerBranchCode("1");
		 	      saveMotorData.setHavepromocode("N");
		 	      saveMotorData.setEndorsementYn("N");
		 	      saveMotorData.setCommissionPercentage(BigDecimal.valueOf(commissionPercentage));
		 	      saveMotorData.setCustomerName("UssdBroker");
		 	      saveMotorData.setSourceType("USSD");
		 	      saveMotorData.setBdmCode("1010217630");//UAT 5423156  Live 1010217630
		 	      saveMotorData.setBranchName(branch);
		 	      saveMotorData.setCustomerCode("1010217630");//UAT 5423156  Live 1010217630
		 	      saveMotorData.setMotorUsageDesc(motorUsageDesc);
		 	      saveMotorData.setTiraBodyType(bodyType);
		 	      saveMotorData.setTiraMotorUsage(motorUsageDesc);
		 	      saveMotorData.setModelNumber(modelNo);
		 	      saveMotorData.setFinalizeYn("N");
		 	      saveMotorData.setBrokerTiraCode("99999");
		 	      saveMotorData.setSourceTypeId("2");
		 	      saveMotorData.setOwnerName(ownerName);
		 	      saveMotorData.setOwnerCategoryId(ownerCategory);
		 	      saveMotorData.setClaimNum12m0m(0);
		 	      saveMotorData.setClaimNum24m12m(0);
		 	      saveMotorData.setClaimNum36m24m(0);
		 	      saveMotorData.setCarAlarmYn("N");
		 	      saveMotorData.setPaymentFrequency(12);
		 	      saveMotorData.setNoOfPassengers(Integer.valueOf(sittingCapacity));
		 	      saveMotorData.setFuelTypeDescLocal(fuel);
		 	      saveMotorData.setColorDescLocal(color);
		 	      saveMotorData.setVehicleMakeId(makeId);
		 	      saveMotorData.setVehicleModelId(modelId);
		 	      saveMotorData.setSumInsuredLc(BigDecimal.valueOf(sumInsured));
		 	      saveMotorData.setEndtCount(BigDecimal.valueOf(0));
		 	      
		 	      motorDataRepo.saveAndFlush(saveMotorData);
		 	      
		 	      //save Sectiondata
		 	      SectionDataDetails sectionDataSave = new SectionDataDetails();
		 	      sectionDataSave.setRequestReferenceNo(reqRefNo);
		 	      sectionDataSave.setQuoteNo(quoteNo);
		 	      sectionDataSave.setLocationId(locationId);
		 	      sectionDataSave.setLocationName(branch);
		 	      sectionDataSave.setRiskId(Integer.valueOf(1));
		 	      sectionDataSave.setCustomerReferenceNo(custRefNo);
		 	      sectionDataSave.setProductId(productId);
		 	      sectionDataSave.setProductDesc("Motor");
		 	      sectionDataSave.setSectionId(String.valueOf(sectionId));
		 	      sectionDataSave.setSectionDesc(sectionName);
		 	      sectionDataSave.setCompanyId(companyId);
		 	      sectionDataSave.setCompanyName(companyName);
		 	      sectionDataSave.setCustomerId(customerId);
		 	      sectionDataSave.setEntryDate(new Date());
		 	      sectionDataSave.setCreatedBy("UssdBroker");
		 	      sectionDataSave.setUpdatedDate(new Date());
		 	      sectionDataSave.setUpdatedBy("UssdBroker");
		 	      sectionDataSave.setCurrencyId(currencyCode);
		 	      sectionDataSave.setExchageRate(BigDecimal.valueOf(exchange));
		 	      sectionDataSave.setProductType("M");
		 	      sectionDataSave.setSectionEndtModification("none");
		 	      sectionDataSave.setCoverNoteReferenceNo(coverNoteReferenceNumber);
		 	      sectionDataSave.setStickerNumber(stickerNumber);
		 	      sectionDataSave.setPrevCovernoteRefno(prevCoverNoteRefNo);
		 	      sectionDataSave.setCommsissionPercentage(BigDecimal.valueOf(commissionPercentage));
		 	      sectionDataSave.setCommissionAmount(BigDecimal.valueOf(commissionAmount));
		 	      sectionDataSave.setCoverId(0);
		 	      	      
		 	      sectionRepo.saveAndFlush(sectionDataSave);
		 	      
		 	      //save Personalinfo
		 	      PersonalInfo personalInfoSave = new PersonalInfo();
		 	      personalInfoSave.setCustomerId(customerId);
		 	      personalInfoSave.setCompanyId(companyId);
		 	      personalInfoSave.setBranchCode(branchCode);
		 	      personalInfoSave.setCustomerReferenceNo(custRefNo);
		 	      personalInfoSave.setClientName(policyHolderName);
		 	      personalInfoSave.setAddress1(postalAddress);
		 	      personalInfoSave.setTitle("1");
		 	      personalInfoSave.setTitleDesc("Mr");
		 	      personalInfoSave.setClientStatus("Y");
		 	      personalInfoSave.setClientStatusDesc("Active");
		 	      personalInfoSave.setPolicyHolderType(policyHolderType);
		 	      personalInfoSave.setPolicyHolderTypeid(policyHolderIdType);
		 	      personalInfoSave.setIdNumber(policyHolderNumber);
		 	      SimpleDateFormat dobFormat = new SimpleDateFormat("dd/MM/yyyy");
		 	      Date cusDateOfBirth = null;

		 	      try {
		 	         cusDateOfBirth = dobFormat.parse(policyHoldeDateOfBirth);
		 	      } catch (Exception e) {
		 	         e.printStackTrace();
		 	      }

		 	      personalInfoSave.setDobOrRegDate(cusDateOfBirth);
		 	      personalInfoSave.setAge(cusAge);
		 	      personalInfoSave.setNationality(countryCode);
		 	      personalInfoSave.setGender(gender);
		 	      personalInfoSave.setGenderDesc(genderDesc);
		 	      personalInfoSave.setStateName(region);
		 	      personalInfoSave.setOccupation("23");
		 	      personalInfoSave.setOccupationDesc("Officers");
		 	      personalInfoSave.setCityName(district);
		 	      personalInfoSave.setStreet(street);
		 	      personalInfoSave.setFax(policyHolderFax);
		 	      personalInfoSave.setMobileNo1(policyHolderPhoneNumber);
		 	      personalInfoSave.setWhatsappNo(policyHolderPhoneNumber);
		 	      personalInfoSave.setEmail1(emailAddress);
		 	      personalInfoSave.setLanguage("1");
		 	      personalInfoSave.setLanguageDesc("Swahili");
		 	      personalInfoSave.setIsTaxExempted(isTaxExempted);
		 	      personalInfoSave.setEntryDate(new Date());
		 	      personalInfoSave.setStatus("Y");
		 	      personalInfoSave.setCreatedBy("UssdBroker");
		 	      personalInfoSave.setIdType(policyHolderIdType);
		 	      personalInfoSave.setIdTypeDesc(policyHolderIdTypeDesc);
		 	      personalInfoSave.setPolicyHoderTypeDesc(policyHolderTypeDesc);
		 	      
		 	      personalInfoRepo.saveAndFlush(personalInfoSave);
		 	      
		 	      //save PolicyCoverData
		 	      PolicyCoverData policyCoverDataSave = new PolicyCoverData();
		 	      policyCoverDataSave.setQuoteNo(quoteNo);
		 	      policyCoverDataSave.setRequestReferenceNo(reqRefNo);
		 	      policyCoverDataSave.setVehicleId(Integer.valueOf(vehicleId));
		 	      policyCoverDataSave.setLocationId(locationId);
		 	      policyCoverDataSave.setCdRefno("0000");
		 	      policyCoverDataSave.setVdRefno("0000");
		 	      policyCoverDataSave.setMsRefno("0000");
		 	      policyCoverDataSave.setCompanyId(companyId);
		 	      policyCoverDataSave.setProductId(Integer.valueOf(productId));
		 	      policyCoverDataSave.setSectionId(sectionId);
		 	      policyCoverDataSave.setCoverId(5);
		 	      policyCoverDataSave.setSubCoverYn("N");
		 	      policyCoverDataSave.setSubCoverId(0);
		 	      policyCoverDataSave.setDiscLoadId(0);
		 	      policyCoverDataSave.setCoverName("Base Cover");
		 	      policyCoverDataSave.setCoverDesc("Base Cover - Motor");
		 	      policyCoverDataSave.setCalcType("P");
		 	      policyCoverDataSave.setSumInsured(BigDecimal.valueOf(sumInsured));
		 	      policyCoverDataSave.setRate(BigDecimal.valueOf(premiumRate));
		 	      policyCoverDataSave.setCurrency(currencyCode);
		 	      policyCoverDataSave.setExchangeRate(BigDecimal.valueOf(exchange));
		 	      policyCoverDataSave.setPremiumBeforeDiscountFc(BigDecimal.valueOf(premiumBeforeDiscount));
		 	      policyCoverDataSave.setPremiumBeforeDiscountLc(BigDecimal.valueOf(premiumBeforeDiscount));
		 	      policyCoverDataSave.setPremiumAfterDiscountFc(BigDecimal.valueOf(premiumAfterDiscount));
		 	      policyCoverDataSave.setPremiumAfterDiscountLc(BigDecimal.valueOf(premiumAfterDiscount));
		 	      policyCoverDataSave.setPremiumExcludedTaxFc(BigDecimal.valueOf(totalPremiumExclTax));
		 	      policyCoverDataSave.setPremiumExcludedTaxLc(BigDecimal.valueOf(totalPremiumExclTax));
		 	      policyCoverDataSave.setPremiumIncludedTaxFc(BigDecimal.valueOf(totalPremiumIncludingTax));
		 	      policyCoverDataSave.setPremiumIncludedTaxLc(BigDecimal.valueOf(totalPremiumIncludingTax));
		 	      policyCoverDataSave.setDependentCoverYn("N");
		 	      policyCoverDataSave.setEntryDate(new Date());
		 	      policyCoverDataSave.setCoverPeriodFrom(effectiveStartDate);
		 	      policyCoverDataSave.setCoverPeriodTo(effectiveEndDate);
		 	      policyCoverDataSave.setNoOfDays(BigDecimal.valueOf(366L));
		 	      policyCoverDataSave.setStatus("Y");
		 	      policyCoverDataSave.setCreatedBy("UssdBroker");
		 	      policyCoverDataSave.setTaxId(Integer.valueOf(0));
		 	      policyCoverDataSave.setTaxRate(BigDecimal.valueOf(taxRate));
		 	      policyCoverDataSave.setTaxAmount(BigDecimal.valueOf(taxAmount));
		 	      policyCoverDataSave.setIsTaxExtempted(isTaxExempted);
		 	      policyCoverDataSave.setTaxExemptType(taxExemptionType);
		 	      policyCoverDataSave.setIsReferral("N");
		 	      policyCoverDataSave.setRegulatoryCode("SP014001000000");
		 	      policyCoverDataSave.setExcessAmount(BigDecimal.valueOf(0L));
		 	      policyCoverDataSave.setMultiSelectYn("N");
		 	      policyCoverDataSave.setMinimumPremiumYn("N");
		 	      policyCoverDataSave.setDiscountCoverId(0);
		 	      policyCoverDataSave.setEndtCount(BigDecimal.valueOf(0L));
		 	      policyCoverDataSave.setSumInsuredLc(BigDecimal.valueOf(sumInsured));
		 	      policyCoverDataSave.setTaxAmountLc(BigDecimal.valueOf(taxAmount));
		 	      policyCoverDataSave.setIndividualId(1);
		 	      policyCoverDataSave.setCoverageLimit(BigDecimal.valueOf(999999999L));
		 	      policyCoverDataSave.setCoverageType("B");
		 	      
		 	      policyCoverDataRepo.saveAndFlush(policyCoverDataSave);
		 	      
		 	      //save HomePosition
		 	      HomePositionMaster homePositionSave = new HomePositionMaster();
		 	      homePositionSave.setQuoteNo(quoteNo);
		 	      homePositionSave.setRequestReferenceNo(reqRefNo);
		 	      homePositionSave.setNoOfVehicles(1);
		 	      homePositionSave.setCompanyId(companyId);
		 	      homePositionSave.setAmendId(0);
		 	      homePositionSave.setProductId(Integer.valueOf(productId));
		 	      homePositionSave.setSectionId(sectionId);
		 	      homePositionSave.setCustomerName("UssdBroker");
		 	      homePositionSave.setAgencyCode(Integer.valueOf(agencyCode));
		 	      homePositionSave.setApplicationNo(0L);
		 	      homePositionSave.setCustomerId(customerId);
		 	      homePositionSave.setLoginId("UssdBroker");
		 	      homePositionSave.setApplicationId("1");
		 	      homePositionSave.setBrokerCode(agencyCode);
		 	      homePositionSave.setEmiYn("N");
		 	      homePositionSave.setManualReferalYn("N");
		 	      homePositionSave.setPolicyTerm("366");
		 	      homePositionSave.setQuoteCreatedDate(new Date());
		 	      homePositionSave.setEntryDate(new Date());
		 	      homePositionSave.setInceptionDate(inceptionDate);
		 	      homePositionSave.setExpiryDate(expireDate);
		 	      homePositionSave.setEffectiveDate(effectiveStartDate);
		 	      homePositionSave.setCurrency(currencyCode);
		 	      homePositionSave.setExchangeRate(BigDecimal.valueOf(exchange));
		 	      homePositionSave.setPremiumFc(BigDecimal.valueOf(totalPremiumExclTax));
		 	      homePositionSave.setPremiumLc(BigDecimal.valueOf(totalPremiumExclTax));
		 	      homePositionSave.setVatPercent(BigDecimal.valueOf(taxRate));
		 	      homePositionSave.setVatPremiumFc(BigDecimal.valueOf(taxAmount));
		 	      homePositionSave.setVatPremiumLc(BigDecimal.valueOf(taxAmount));
		 	      homePositionSave.setOverallPremiumFc(BigDecimal.valueOf(totalPremiumIncludingTax));
		 	      homePositionSave.setOverallPremiumLc(BigDecimal.valueOf(totalPremiumIncludingTax));
		 	      homePositionSave.setCommissionPercentage(BigDecimal.valueOf(commissionPercentage));
		 	      homePositionSave.setCommission(BigDecimal.valueOf(commissionAmount));
		 	      homePositionSave.setBranchCode(branchCode);
		 	      homePositionSave.setBranchName(branch);
		 	      homePositionSave.setFinalizeYn("N");
		 	      homePositionSave.setEndtBy(null);
		 	      homePositionSave.setVehicleNo(1);
		 	      homePositionSave.setHavepromoYn("N");
		 	      homePositionSave.setSourceType("USSD");
		 	      homePositionSave.setCustomerCode("1010195707");//UAT 5423156  Live 1010217630(old)
		 	      homePositionSave.setBrokerBranchName(branch);
		 	      homePositionSave.setCompanyName(companyName);
		 	      homePositionSave.setProductName(productName);
		 	      homePositionSave.setUserType("Broker");
		 	      homePositionSave.setSubUserType("USSD");
		 	     // homePositionSave.setCoverNoteNumber(BigDecimal.valueOf(Long.parseLong(coverNoteNumber)));
		 	      homePositionSave.setTiraRequestId(reqId);
		 	      homePositionSave.setCoverNoteReferenceNo(coverNoteNumber);
		 	      homePositionSave.setStickerNumber(stickerNumber);
		 	      homePositionSave.setPrevCoverNoteRefNo(prevCoverNoteRefNo);
		 	      homePositionSave.setCommissionModifyYn("N");
		 	      homePositionSave.setEndtCount(0);
		 	      
		 	      homeRepo.saveAndFlush(homePositionSave);
		 	      
		 	     
		 	      
		 	      //Make Payment Api
		 	      
		 	      log.info("MAKE PAYMENT BLOCK START : " + new Date());
		 	      
		 	      Map<String, Object> makePaymentMap = new HashMap<>();
		 	      makePaymentMap.put("CreatedBy", "UssdBroker");
		 	      makePaymentMap.put("EmiYn", "N");
		 	      makePaymentMap.put("InstallmentMonth", "");
		 	      makePaymentMap.put("InstallmentPeriod", "");
		 	      makePaymentMap.put("InsuranceId", "100002");
		 	      makePaymentMap.put("Premium", totalPremiumIncludingTax);
		 	      makePaymentMap.put("QuoteNo", quoteNo);
		 	      makePaymentMap.put("Remarks", "None");
		 	      makePaymentMap.put("SubUserType", "USSD");
		 	      makePaymentMap.put("UserType", "Broker");
		 	      String makePaymentReq = objectPrint.toJson(makePaymentMap);
		 	      System.out.println("makePaymentReq" + makePaymentReq);
		 	      log.info("makePaymentReq : " + makePaymentReq);
		 	  //    String makePaymentApi = "http://localhost:8086/EwayCommonApi/payment/makepayment";
		 	   //  String makePaymentApi = "http://192.168.1.42:8086/payment/makepayment";
		 	  //    String makePaymentApi = "http://localhost:8080/EwayCommonApiLive/payment/makepayment";
		 	      response = this.callEwayApi(makePaymentApi, makePaymentReq);
		 	      System.out.println("makePaymentRes" + response);
		 	      log.info("makePaymentRes : " + response);
		 	      Map<String, Object> makePaymentResult = null;

		 	      try {
		 	         Map<String, Object> makePaymentRes = (Map)this.mapper.readValue(response, Map.class);
		 	         makePaymentResult = makePaymentRes.get("Result") == null ? null : (Map)this.mapper.readValue(this.mapper.writeValueAsString(makePaymentRes.get("Result")), Map.class);
		 	         if(makePaymentResult.isEmpty() || makePaymentResult == null) {
		 	        	//errorList.add(makePaymentRes.get("ErrorMessage").toString());
		 	        	return makePaymentRes;
		 	         }
		 	      } catch (Exception e) {
		 	         e.printStackTrace();
		 	         exception=e.getMessage();
		 	         log.info(exception);
		 	      }
		 	      
		 	      //InsertPayment

		 	      Map<String, Object> insertPayment = new HashMap();
		 	      insertPayment.put("CreatedBy", "UssdBroker");
		 	      insertPayment.put("InsuranceId", "100002");
		 	      insertPayment.put("EmiYn", "N");
		 	      insertPayment.put("Premium", totalPremiumIncludingTax);
		 	      insertPayment.put("QuoteNo", quoteNo);
		 	      insertPayment.put("Remarks", "None");
		 	      insertPayment.put("PayeeName", policyHolderName);
		 	      insertPayment.put("SubUserType", "USSD");
		 	      insertPayment.put("UserType", "Broker");
		 	      insertPayment.put("PaymentId", makePaymentResult.get("PaymentId"));
		 	      insertPayment.put("PaymentType", paymentMode);
		 	      insertPayment.put("WhatsappCode", "256");
		 	      insertPayment.put("WhatsappNo", policyHolderPhoneNumber);
		 	      insertPayment.put("MobileCode1", "256");
		 	      insertPayment.put("MobileNo1", policyHolderPhoneNumber);
		 	      String insertPaymentReq = this.objectPrint.toJson(insertPayment);
		 	      System.out.println("insertPaymentReq" + insertPaymentReq);
		 	      log.info("insertPaymentReq : " + insertPaymentReq);
		 	      
		 	  //  String insertPaymentApi = "http://localhost:8086/EwayCommonApi/payment/insertpaymentdetails";
		 	  //   String insertPaymentApi = "http://192.168.1.42:8086/payment/insertpaymentdetails";
		 	 //    String insertPaymentApi = "http://localhost:8080/EwayCommonApiLive/payment/insertpaymentdetails";
		 	      response = this.callEwayApi(insertPaymentApi, insertPaymentReq);
		 	      System.out.println("insertPaymentRes" + response);
		 	      log.info("insertPaymentRes : " + response);
		 	      Map<String, Object> insertPaymentResult = null;

		 	      try {
		 	         Map<String, Object> insertPaymentRes = (Map)this.mapper.readValue(response, Map.class);
		 	         insertPaymentResult = insertPaymentRes.get("Result") == null ? null : (Map)this.mapper.readValue(this.mapper.writeValueAsString(insertPaymentRes.get("Result")), Map.class);
		 	        if(insertPaymentResult == null) {
		 	        //	errorList.add(response);
		 	        	return insertPaymentRes;
		 	         }
		 	      } catch (Exception e) {
		 	         e.printStackTrace();
		 	         exception=e.getMessage();
		 	         log.info(exception);
		 	      }
		 	      if(StringUtils.isNotBlank(exception)) {
		 				errorList.add(exception);
		 			}
		 			if(errorList.size()>0) {
		 				return errorList.toString();
		 			}

		 	      String policyNo = insertPaymentResult.get("PolicyNo") == null ? "" : insertPaymentResult.get("PolicyNo").toString();
		 	      String quoteNum = insertPaymentResult.get("QuoteNo") == null ? "" : insertPaymentResult.get("QuoteNo").toString();
		 	      
		 	//      HomePositionMaster homeDetails = homeRepo.findByPolicyNoAndStatus(policyNo, "P");
		 	     PolicyDrcrDetail details = new PolicyDrcrDetail();
		 	      if(StringUtils.isNotBlank(policyNo)) {
		 	    	List<PolicyDrcrDetail> policyDetails = policyDrCrRepo.findByPolicyNo(policyNo);
		 	    	policyDetails = policyDetails.stream().filter(p -> (p.getChargeCode().equals(BigDecimal.valueOf(1002))) &&
		 	    			(p.getChgId().equals(BigDecimal.valueOf(2)))).collect(Collectors.toList());
		 	    	
		 	    	details = policyDetails.get(0);
		 	    	details.setAmountLc(BigDecimal.valueOf(taxAmount));
		 	    	details.setAmountFc(BigDecimal.valueOf(taxAmount));
		 	    	details.setNarration("VAT @ 18.0%");
		 	    	
		 	    	policyDrCrRepo.saveAndFlush(details);
		 	    	
		 	    	SectionDataDetails sectionData = sectionRepo.findByQuoteNoAndSectionIdAndRiskId(quoteNum, sectionId.toString(), Integer.valueOf(1));
		 	    	sectionData.setResponseStatusCode("TIRA001");
		 	    	sectionData.setResponseStatusDesc("Successful");
		 	    	
		 	    	sectionRepo.saveAndFlush(sectionData);
		 	    	
		 	    	}
		 	      
		 	      String debitNo = insertPaymentResult.get("DebitNoteNo") == null ? "" : insertPaymentResult.get("DebitNoteNo").toString();
		 	      String creditNo = insertPaymentResult.get("CreditNoteNo") == null ? "" : insertPaymentResult.get("CreditNoteNo").toString();
		 	      String quoteCreatedDate = effectiveStartDate.toString();
		 	      Map<String, Object> endResponse = new HashMap();
		 	      endResponse.put("ReferenceNumber", reqRefNo);
		 	      endResponse.put("PolicyNo", policyNo);
		 	      endResponse.put("QuoteNo", quoteNum);
		 	      endResponse.put("DebitNoteNumber", debitNo);
		 	      endResponse.put("CreditNoteNumber", creditNo);
		 	      
		 	      log.info("MAKE PAYMENT BLOCK END : " + new Date()); 
		 	      return endResponse;

	    	}else {
	    		// positionList = positionList.stream().filter(pos -> (pos.getCoverNoteReferenceNo().equals(coverNoteNumber))
		    	//		  && (pos.getStickerNumber().equals(stickerNumber)) && (pos.getStickerNumber().equals(stickerNumber))).collect(Collectors.toList());
		    	 
		    	//  if(!positionList.isEmpty()) {
	    		String activePolicy = positionList.get(0).getPolicyNo();
	    		System.out.println("The registration number already has an active policy. Please check your registration number.The Active Policy No is "+activePolicy);
		    		  return "The registration number already has an active policy. Please check your registration number.The Active Policy No is "+activePolicy;
		    				  
		    	 // }
	    	}
	}else {
		//Map<String,Object> resp = new HashMap<>();
		//resp.put(makePaymentApi, resp)
		return errorList;
	}
	    
	   }

	   private List<String> ussdValidation(UssdApiReq req) {
		   List<String> errorList = new ArrayList<>();
		try {
			if(StringUtils.isBlank(req.getPolicyHolderName())){
				errorList.add("policy Holder Name does not allowed null value");
			}
			if(StringUtils.isBlank(req.getStickerNumber())) {
				errorList.add("Sticker Number does not allowed null value");
			}
			if(StringUtils.isBlank(req.getCoverNoteNumber())) {
				errorList.add("Cover Note Number does not allowed null value");
			}
			if(StringUtils.isBlank(req.getCoverNoteStartDate())) {
				errorList.add("Cover Note Start Date does not allowed null value");
			}
			if(StringUtils.isBlank(req.getCoverNoteEndDate())) {
				errorList.add("Cover Note End Date does not allowed null value");
			}
			if(StringUtils.isBlank(req.getCoverNoteDesc())) {
				errorList.add("Cover Note Description does not allowed null value");
			}
			if(StringUtils.isBlank(req.getPaymentMode())) {
				errorList.add("Payment mode does not allowed null value");
			}
			if(StringUtils.isBlank(req.getCurrencyCode())) {
				errorList.add("Currency code does not allowed null value");
			}
			if(StringUtils.isBlank(req.getTotalPremiumExcludingTax())) {
				errorList.add("Total Premium does not allowed null value");
			}
			if(StringUtils.isBlank(req.getTotalPremiumIncludingTax())) {
				errorList.add("Total Premium does not allowed null value");
			}
			if(StringUtils.isBlank(req.getOfficerName())) {
				errorList.add("Officer Name does not allowed null value");
			}
			if(StringUtils.isBlank(req.getOfficerTitle())) {
				errorList.add("Officer Title does not allowed null value");
			}
			if(StringUtils.isBlank(req.getTaxRate())) {
				errorList.add("Tax Rate does not allowed null value");
			}
			if(StringUtils.isBlank(req.getTaxAmount())) {
				errorList.add("Tax Amount does not allowed null value");
			}
			if(StringUtils.isBlank(req.getPolicyHolderIdType())) {
				errorList.add("Policy Holder Type does not allowed null value");
			}
			if(StringUtils.isBlank(req.getPolicyHolderIdType())) {
				errorList.add("Policy Holder Id Type does not allowed null value");
			}
			if(StringUtils.isBlank(req.getPolicyHolderType())) {
				errorList.add("Policy Holder Type does not allowed null value");
			}
			if(StringUtils.isBlank(req.getPolicyHolderNumber())) {
				errorList.add("Policy Holder Number does not allowed null value");
			}if(StringUtils.isBlank(req.getCountryCode())) {
				errorList.add("Country Code does not allowed null value");
			}
			if(StringUtils.isBlank(req.getPolicyHolderPhoneNumber())) {
				errorList.add("Policy Holder Phone Number does not allowed null value");
			}
			if(StringUtils.isBlank(req.getCoverNoteReferenceNumber())) {
				errorList.add("Cover Note Reference Number does not allowed null value");
			}
			if(StringUtils.isBlank(req.getOwnerName())) {
				errorList.add("Owner Name does not allowed null value");
			}
			if(StringUtils.isBlank(req.getRegistrationNumber())) {
				errorList.add("Registration Number does not allowed null value");
			}
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return errorList;
	}

	private String callEwayApi(String url, String request) {
	      String apiReponse = null;

	      try {
	        // System.out.println("Call Eway Block");
	         Map<String, Object> tokReq = new HashMap();
	         tokReq.put("LoginId", "UssdBroker");
	         tokReq.put("Password", "Admin@10");
	         tokReq.put("ReLoginKey", "Y");
	          //   String tokenApi = "http://localhost:8086/EwayCommonApi/authentication/login";
	           //    String tokenApi = "http://192.168.1.42:8086/authentication/login";
	       //  String tokenApi = "http://localhost:8080/EwayCommonApiLive/authentication/login";
	         System.out.println("Token Api URL ==> " + tokenApi);
	         String jsonTokenRequest = (new Gson()).toJson(tokReq);
	         CloseableHttpClient httpClient = createHttpClientWithTimeouts();
	         HttpPost postRequest = new HttpPost(tokenApi);
	         postRequest.setHeader("Content-Type", "application/json");
	         postRequest.setEntity(new StringEntity(jsonTokenRequest));
	        System.out.println("Token Api Req ==> " + jsonTokenRequest);
	         CloseableHttpResponse response = httpClient.execute(postRequest);
	         System.out.println("Token Api Res ==> " + String.valueOf(response));
	         String responseString = null;
	         String token = "";

	         Map<String, Object> tokenObj;
	         try {
	            HttpEntity entity = response.getEntity();
	            responseString = EntityUtils.toString(entity);
	            System.out.println("Token Api Res ==> " + responseString);
	            Map<String, Object> tokenRes = (Map)(new Gson()).fromJson(responseString, Map.class);
	            tokenObj = tokenRes.get("Result") == null ? null : (Map)tokenRes.get("Result");
	            token = tokenObj.get("Token") == null ? "" : tokenObj.get("Token").toString();
	            log.info("Token : " + token);
	            System.out.println("Token Api Resp ==> " + token);
	         } catch (Exception e) {
	            e.printStackTrace();
	            log.info(e);
	         }

	         HttpPost postRequest2 = new HttpPost(url);
	         postRequest2.setHeader("Authorization", "Bearer " + token);
	         postRequest2.setHeader("Content-Type", "application/json");
	         postRequest2.setEntity(new StringEntity(request));
	         CloseableHttpResponse response2 = httpClient.execute(postRequest2);
	         tokenObj = null;

	         String responseString2;
	         try {
	            HttpEntity entity = response2.getEntity();
	            responseString2 = EntityUtils.toString(entity);
	         } finally {
	            response.close();
	            httpClient.close();
	         }

	         apiReponse = responseString2;
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.out.println(e.getLocalizedMessage());
	         log.info(e);
	      }

	      return apiReponse;
	   }

	   private static CloseableHttpClient createHttpClientWithTimeouts()  {
		   System.out.println("ssl");
		   try {
			/*   SSLContext sslContext = SSLContext.getInstance("TLS");
			    sslContext.init(null, new TrustManager[]{
			        new X509TrustManager() {
			            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
			            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
			            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
			        }
			    }, new SecureRandom());

			    SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
			        sslContext, NoopHostnameVerifier.INSTANCE);

			    return HttpClients.custom()
			            .setSSLSocketFactory(sslSocketFactory)
			            .build(); */
		      RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(60000).build();
		      return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		   }catch(Exception e) {
			   e.printStackTrace();
		         System.out.println(e.getLocalizedMessage());
		       //  log.info(e); 
		   }
		   return null;
	   }

	   private Map<String, String> findPolicyDates(String coverNoteStartDate, String coverNoteEndDate) {
	      Map<String, String> response = new HashMap();
	      String PolicyEndDate = coverNoteEndDate.toString();
	      LocalDate previousPolicyEndDate = LocalDate.parse(PolicyEndDate.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	      LocalDate currentDate = LocalDate.now();
	      LocalDate policyStartDate = null;
	      if (previousPolicyEndDate.isBefore(currentDate)) {
	         policyStartDate = LocalDate.now();
	      } else if (previousPolicyEndDate.isEqual(currentDate)) {
	         policyStartDate = LocalDate.now().plusDays(1L);
	      } else if (previousPolicyEndDate.isAfter(currentDate)) {
	         policyStartDate = previousPolicyEndDate.plusDays(1L);
	      }

	      LocalDate policyEndDate = policyStartDate.plusDays(364L);
	      DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	      response.put("PolicyStartDate", formatters.format(policyStartDate));
	      response.put("PolicyEndDate", formatters.format(policyEndDate));
	      return response;
	   }
		
		
	}


