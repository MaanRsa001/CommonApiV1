package com.maan.eway.yara.api;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.eway.bean.AgricultureMaster;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.error.Error;
import com.maan.eway.repository.AgriCultureMasterRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.CountryMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.RegionMasterRepository;
import com.maan.eway.repository.SectionCoverMasterRepository;
import com.maan.eway.repository.StateMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


@Service
//@PropertySource("classpath:com/maan/eway/yara/api/YaraApi.properties")
public class YaraServiceImpl implements YaraService{
	
	@Autowired
	private Gson objectPrint;

	@Autowired
	private ObjectMapper mapper;
	
	private Logger log = LogManager.getLogger(YaraServiceImpl.class);

	@Value("${login.token.api}")
	private String tokenApi;
	
	@Value("${yara.broker.login.id}")
	private String brokerLoginId;
	
	@Value("${yara.broker.password}")
	private String brokerPassword;
	
	@Value("${customer.save.api}")
	private String customerSaveApi;

	@Value("${nonMotor.save.api}")
	private String nonMotorSaveApi;
	
	@Value("${calculator.api}")
	private String calculatorApi;
	
	@Value("${view.calc.api}")
	private String viewCalcApi;
	
	@Value("${update.factor.rate.api}")
	private String factorRateAPi;
	
	@Value("${buypolicy.api}")
	private String buyPolicyApi;
	
	@Value("${make.payment.api}")
	private String makePaymentApi;

	@Value("${insert.payment.api}")
	private String insertPaymentApi;
	
	@Value("${policy.file.path}")
	private String path;
	
	@Value("${yara.document.download}")
	private String documentDownloadURL;
	
	@Autowired
	private LoginMasterRepository loginMasterRepo;
	
	@Autowired
	private LoginBranchMasterRepository branchMasterRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserInfoRepo;
	
	@Autowired
	private CompanyProductMasterRepository productMasterRepo;
	
	@Autowired
	private ProductSectionMasterRepository sectionMasterRepo;
	
	@Autowired
	private SectionCoverMasterRepository coverMasterRepo;
	
	@Autowired
	private RegionMasterRepository regionMasterRepo;
	
	@Autowired
	private StateMasterRepository stateMasterRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerDetailsRepo;
	
	@Autowired
	private CountryMasterRepository countryMasterRepo;
	
	@Autowired
	private AgriCultureMasterRepository agriRepo;
	
	@Autowired
	private EntityManager em;

	@Override
	public Object getYaraApi(YaraReq req) {
		YaraImportantKeys keysSetup = otherKeysSetup(req);
		
		String customerRefNo = "";
		String response = "";
		Double premium=0D;
		Double vatTax =0d;
		Double vatPercentage=0D;
		BigDecimal totalPremium=null;
		
		
		//==============================CUSTOMER SAVE BLOCK START=============================================
		
        log.info("CUSTOMER SAVE BLOCK START : " + new Date());
        
        //check if existing customer 
        List<EserviceCustomerDetails> cusDetails = customerDetailsRepo.findByCompanyIdAndIdNumberAndClientStatus(
        		keysSetup.getCompanyId(), req.getCustomerDetails().getIdNumber(), "Y");
        cusDetails = cusDetails.stream().filter(cus -> (cus.getMobileNo1().equals(req.getCustomerDetails().getPhoneNumber()))).collect(Collectors.toList());
        
        if(!cusDetails.isEmpty()) {
        	customerRefNo = cusDetails.get(0).getCustomerReferenceNo();
        }else {
        	Map<String,Object> cusSave = new HashMap<>();
    		cusSave.put("BrokerBranchCode", keysSetup.getBrokerBranchCode());
    		cusSave.put("CustomerReferenceNo",null);
    		cusSave.put("InsuranceId", keysSetup.getCompanyId());
    		cusSave.put("BranchCode",keysSetup.getBranchCode());
    		cusSave.put("ProductId", keysSetup.getProductId());
    		cusSave.put("AppointmentDate", "");
    		cusSave.put("Address1", req.getCustomerDetails().getPostalAddress());
    		cusSave.put("Address2", "");
    		cusSave.put("BusinessType", null);
    		cusSave.put("CityCode",keysSetup.getCityCode());
    		cusSave.put("CityName", req.getCustomerDetails().getDistrict());
    		cusSave.put("ClientName", req.getCustomerDetails().getCustomerName());
    		cusSave.put("Clientstatus", "Y");
    		cusSave.put("CreatedBy", keysSetup.getLoginId());
    		cusSave.put("DobOrRegDate", req.getCustomerDetails().getDateOfBirth());
    		cusSave.put("ExpiryDate", null);
    		cusSave.put("Email1", req.getCustomerDetails().getEmailAddress());
    		cusSave.put("Email2", null);
    		cusSave.put("Email3", null);
    		cusSave.put("Fax", null);
    		cusSave.put("Gender", req.getCustomerDetails().getGender());
    		cusSave.put("IdNumber", req.getCustomerDetails().getIdNumber());
    		cusSave.put("IdType", req.getCustomerDetails().getIdType());
    		cusSave.put("IsTaxExempted", "N");
    		cusSave.put("Language", "1");
    		cusSave.put("MobileNo1", req.getCustomerDetails().getPhoneNumber());
    		cusSave.put("MobileNo3", null);
    		cusSave.put("Nationality", keysSetup.getCountryCode());
    		cusSave.put("NationalityName", keysSetup.getCountryName());
    		cusSave.put("Country", keysSetup.getCountryCode());
    		cusSave.put("CountryName", keysSetup.getCountryName());
    		cusSave.put("Occupation", keysSetup.getOccupationId());
    		cusSave.put("OtherOccupation", "");
    		cusSave.put("Placeofbirth", "Chennai");
    		cusSave.put("PolicyHolderType", "1");
    		cusSave.put("PolicyHolderTypeid", req.getCustomerDetails().getIdType());
    		cusSave.put("PreferredNotification", "Sms");
    		cusSave.put("RegionCode", keysSetup.getRegionCode());
    		cusSave.put("MobileCode1", keysSetup.getMobileCode());
    		cusSave.put("MobileCodeDesc1", keysSetup.getMobileCode());
    		cusSave.put("WhatsappDesc", keysSetup.getMobileCode());
    		cusSave.put("StateCode", keysSetup.getCityCode());
    		cusSave.put("StateName", keysSetup.getCityName());
    		cusSave.put("Status", "Y");
    		cusSave.put("Street", "");
    		cusSave.put("Type", null);
    		cusSave.put("TaxExemptedId", null);
    		cusSave.put("TelephoneNo1", "");
    		cusSave.put("PinCode", null);
    		cusSave.put("TelephoneNo2", null);
    		cusSave.put("TelephoneNo3", null);
    		cusSave.put("Title", "1");
    		cusSave.put("VrTinNo", null);
    		cusSave.put("SaveOrSubmit", "Submit");
    		cusSave.put("Zone", "1");
    		cusSave.put("SocioProfessionalCategory", null);
    		cusSave.put("Activities", "");
    		cusSave.put("CustomerAsInsurer", "N");
    		cusSave.put("RiskAssessmentDate", null);
    		
    		String customerSaveReq = objectPrint.toJson(cusSave);
    	//	System.out.println("Customer Save Request" + customerSaveReq);
    		log.info("Customer Save Request : " + customerSaveReq);

    		response = this.callEwayApi(customerSaveApi, customerSaveReq);
    	//	System.out.println("Customer Save Response" + response);
    		log.info("Customer Save Response : " + response);
    		Map<String, Object> cusSaveResult = null;
    		
    		try {
    			Map<String, Object> cusSaveRes = (Map) this.mapper.readValue(response, Map.class);
    			cusSaveResult = cusSaveRes.get("Result") == null ? null
    					: (Map) this.mapper.readValue(this.mapper.writeValueAsString(cusSaveRes.get("Result")),
    							Map.class);
    			if (cusSaveResult == null) {
    				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
    				return cusSaveRes;
    			}else {
    				customerRefNo =cusSaveResult.get("SuccessId")==null?"":cusSaveResult.get("SuccessId").toString();
    			}
    	
    		} catch (Exception e) {
    			e.printStackTrace();
    			// exception=e.getMessage();
    			log.info(e.getMessage());
    		}

        }
		
		log.info("CUSTOMER SAVE BLOCK END : " + new Date());
		
		//==============================CUSTOMER SAVE BLOCK END=============================================
		
		//==============================NON MOTOR BLOCK START===============================================
		log.info("NONMOTOR SAVE BLOCK START : " + new Date());
		//PolicyDetails
		Map<String,Object> policyDetails = new HashMap<>();
		policyDetails.put("SaveOrSubmit", "Submit");
		policyDetails.put("AcexecutiveId", "");
		policyDetails.put("ProductType", null);
		policyDetails.put("TiraCoverNoteNo", null);
		policyDetails.put("CustomerReferenceNo", customerRefNo);
		policyDetails.put("RequestReferenceNo", null);
		policyDetails.put("QuoteNo", null);
		policyDetails.put("BuildingOwnerYn", "N");
		policyDetails.put("Createdby", keysSetup.getLoginId());
		policyDetails.put("Currency", keysSetup.getCurrencyCode());
		policyDetails.put("ExchangeRate", keysSetup.getExchangeRate());
		policyDetails.put("Havepromocode", "N");
		policyDetails.put("PolicyEndDate", keysSetup.getPolicyEndDate());
		policyDetails.put("PolicyStartDate", keysSetup.getPolicyStartDate());
		policyDetails.put("IndustryId", "99999");
		policyDetails.put("InsuranceId", keysSetup.getCompanyId());
		policyDetails.put("ProductId", keysSetup.getProductId());
		policyDetails.put("BranchCode", keysSetup.getBranchCode());
		policyDetails.put("Status", "Y");
		
		//BrokerDetails
		Map<String,Object> brokerDetails = new HashMap<>();
		brokerDetails.put("CustomerCode", keysSetup.getCustomerCode());
		brokerDetails.put("CustomerName", keysSetup.getLoginId());
		brokerDetails.put("BdmCode", keysSetup.getCustomerCode());
		brokerDetails.put("BrokerCode", keysSetup.getAgencyCode());
		brokerDetails.put("LoginId", keysSetup.getLoginId());
		brokerDetails.put("ApplicationId", "1");
		brokerDetails.put("AgencyCode", keysSetup.getAgencyCode());
		brokerDetails.put("BrokerBranchCode", keysSetup.getBrokerBranchCode());
		brokerDetails.put("SourceTypeId", keysSetup.getBrokerBranchCode());
		brokerDetails.put("UserType", keysSetup.getUserType());
		
		//LocationList
		Map<String,Object> locationList = new HashMap<>();
		locationList.put("LocationId","1");
		locationList.put("LocationName",keysSetup.getRegionName());
		locationList.put("CoversRequired","BC");
		locationList.put("BuildingOwnerYn","Y");
		locationList.put("Address",keysSetup.getCityName());
		
		Map<String,Object> section = new HashMap<>();
		section.put("SectionId", keysSetup.getSectionId());
		section.put("SectionName", keysSetup.getSectionName());
		section.put("CoverId", keysSetup.getCoverId());
		section.put("ContentDesc", req.getRiskDetails().getDistributorName());
		section.put("CategoryId", "3");
		section.put("RegionCode", keysSetup.getRegionCode());
		section.put("DistrictCode", keysSetup.getCityCode());
		section.put("WallType", req.getRiskDetails().getYaraPackageYn());
		section.put("BuildingUsageId", "1");
		section.put("BuildingFloors", req.getRiskDetails().getNoOfAcre());
		//section.put("WallType", "Y");
		section.put("ContentId", req.getRiskDetails().getDistributorId());
		if(req.getRiskDetails().getYaraPackageYn().equalsIgnoreCase("Y")) {
			//Integer basicAmount = 2400000;
			List<AgricultureMaster> agri = agriRepo.findByProvinceIdAndDistrictIdAndProductIdAndCompanyId(Integer.valueOf(keysSetup.getRegionCode()),
					Integer.valueOf(keysSetup.getCityCode()),Integer.valueOf(keysSetup.getProductId()),Integer.valueOf(keysSetup.getCompanyId()));
			BigDecimal basicAmount = agri.get(0).getPerHACost();
			BigDecimal coveragePercentage = new BigDecimal(req.getRiskDetails().getCoveragePercentage());
			coveragePercentage = (coveragePercentage.divide(new BigDecimal(100)));
			Integer acre = Integer.parseInt(req.getRiskDetails().getNoOfAcre());
			BigDecimal sumInsAndAcre = basicAmount.multiply(new BigDecimal(acre));
			BigDecimal totalSumInsyred = coveragePercentage.multiply(sumInsAndAcre);
			section.put("SumInsured", totalSumInsyred);
		}else if(req.getRiskDetails().getYaraPackageYn().equalsIgnoreCase("N")) {
			section.put("SumInsured", req.getRiskDetails().getSumInsured());
		}
		
		
		locationList.put("SectionList", Arrays.asList(section));
	
		//EndorsementDetails
		Map<String,Object> endorsment = new HashMap<>();
		endorsment.put("EndorsementDate", null);
		endorsment.put("EndorsementEffectiveDate", null);
		endorsment.put("EndorsementRemarks", null);
		endorsment.put("EndorsementType", null);
		endorsment.put("EndorsementTypeDesc", null);
		endorsment.put("EndtCategoryDesc", null);
		endorsment.put("EndtCount", null);
		endorsment.put("EndtPrevPolicyNo", null);
		endorsment.put("EndtPrevQuoteNo", null);
		endorsment.put("EndtStatus", null);
		endorsment.put("IsFinanceEndt", null);
		endorsment.put("OrginalPolicyNo", null);
		endorsment.put("PolicyNo", null);
		
		//nonMotorSave
		Map<String,Object> nonMotorSave = new HashMap<>();
		nonMotorSave.put("PolicyDetails", policyDetails);
		nonMotorSave.put("BrokerDetails", brokerDetails);
		nonMotorSave.put("LocationList", Arrays.asList(locationList));
		nonMotorSave.put("EndorsementDetails", endorsment);
		
		String nonMotorSaveReq = objectPrint.toJson(nonMotorSave);
	//	System.out.println("NonMotor Save Request" + nonMotorSaveReq);
		log.info("NonMotor Save Request : " + nonMotorSaveReq);
		
		response = this.callEwayApi(nonMotorSaveApi, nonMotorSaveReq);
	//	System.out.println("NonMotor Save Response" + response);
		log.info("NonMotor Save Response : " + response);
		List<Map<String, Object>> nonMotorSaveResult = null;
		String reqRefNo = "";
		try {
			Map<String, Object> nonMotorSaveRes = (Map) this.mapper.readValue(response, Map.class);
			nonMotorSaveResult = nonMotorSaveRes.get("Result") == null ? null
					: this.mapper.convertValue(nonMotorSaveRes.get("Result"), new TypeReference<List<Map<String, Object>>>() {});
			if (nonMotorSaveResult == null) {
				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
				return nonMotorSaveRes;
			}else {
				reqRefNo = nonMotorSaveResult.get(0).get("RequestReferenceNo") == null ? "" : nonMotorSaveResult.get(0).get("RequestReferenceNo").toString();
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			// exception=e.getMessage();
			log.info(e.getMessage());
		}
		log.info("NONMOTOR SAVE BLOCK END : " + new Date());
		
		//==============================NON MOTOR BLOCK END===============================================
		
		//==============================CALCULATOR BLOCK START===============================================
		
		log.info("CALCULATOR BLOCK START : " + new Date());
		Map<String,Object> calcMap = new HashMap<>();
		calcMap.put("CoverModification", "N");
		calcMap.put("EffectiveDate", keysSetup.getPolicyStartDate());
		calcMap.put("PolicyEndDate", keysSetup.getPolicyEndDate());
		calcMap.put("RequestReferenceNo", reqRefNo);
		
		String calculatorReq = objectPrint.toJson(calcMap);
	//	System.out.println("NonMotor Save Request" + calculatorReq);
		log.info("NonMotor Save Request : " + calculatorReq);
		
		response = this.callEwayApi(calculatorApi, calculatorReq);
	//	System.out.println("calculator Response" + response);
		log.info("calculator Response : " + response);
		List<Map<String, Object>> calculorResult = null;
		//String reqRefNo = "";
		try {
			List<Map<String, Object>> calculatorRes = (List<Map<String, Object>>) this.mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
			calculorResult = calculatorRes.get(0).get("CoverList") == null ? null
					: (List<Map<String, Object>>) this.mapper.readValue(this.mapper.writeValueAsString(calculatorRes.get(0).get("CoverList")),
							new TypeReference<List<Map<String, Object>>>() {});
			if (calculorResult == null) {
				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
				return calculatorRes;
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			// exception=e.getMessage();
			log.info(e.getMessage());
		}
		
		log.info("CALCULATOR BLOCK END : " + new Date());
		
		//==============================CALCULATOR BLOCK END===============================================
		
		//==============================VIEW CALC BLOCK START===============================================
		String coverId ="";
		log.info("VIEW CALC BLOCK START : " + new Date());
		Map<String,Object> viewCalcMap = new HashMap<>();
		viewCalcMap.put("InsuranceId", keysSetup.getCompanyId());
		viewCalcMap.put("ProductId", keysSetup.getProductId());
		viewCalcMap.put("RequestReferenceNo", reqRefNo);
		
		String viewCalcReq = objectPrint.toJson(viewCalcMap);
	//	System.out.println("View Calc Request" + viewCalcReq);
		log.info("View Calc Request : " + viewCalcReq);
		
		response = this.callEwayApi(calculatorApi, viewCalcReq);
	//	System.out.println("View Calc Response" + response);
		log.info("View Calc Response : " + response);
		List<Map<String, Object>> viewCalcResult = null;
		Map<String,Object> coverList = null;
		List<Map<String,Object>> taxList = null;
		Map<String,Object> taxes = null;
		Map<String,Object> riskList = null;
		
		//String reqRefNo = "";
		try {
			List<Map<String, Object>> viewCalcRes = (List<Map<String, Object>>) this.mapper.readValue(response, new TypeReference<List<Map<String, Object>>>() {});
			viewCalcResult = viewCalcRes.get(0).get("CoverList") == null ? null
					: (List<Map<String, Object>>) this.mapper.readValue(this.mapper.writeValueAsString(viewCalcRes.get(0).get("CoverList")),
							new TypeReference<List<Map<String, Object>>>() {});
			if (viewCalcResult == null) {
				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
				return viewCalcRes;
			}else {
				coverList = viewCalcResult.get(0);
				taxList = viewCalcResult.get(0).get("Taxes") == null ? null :
				    this.mapper.readValue(this.mapper.writeValueAsString(viewCalcResult.get(0).get("Taxes")), new TypeReference<List<Map<String, Object>>>() {});
				taxes = taxList.get(0);
				
			//	Map<String,Object> result = mapper.readValue(response, Map.class);
				//result = response.get("RiskDetails") == null ? null
						//: (Map<String, Object>) this.mapper.readValue(this.mapper.writeValueAsString(viewCalcRes.get(0).get("RiskDetails")),
							//	new TypeReference<List<Map<String, Object>>>() {});
			//	riskList = result.get("RiskDetails") == null ? null : (Map<String, Object>) result.get("RiskDetails");	
				//		coverList.get("RiskDetails") == null ? null :
				//	this.mapper.readValue(this.mapper.writeValueAsString(coverList.get("RiskDetails")), Map.class);
				
				//coverList3= (List<Map<String, Object>>) viewCalcResult.get(0);
			}
			
			Map<String,Object> cover_list = null;
			
			String referalRemarks =viewCalcResult.stream()
					.filter(p -> p.get("CoverageType").equals("B"))
					.map(p ->p.get("ReferalDescription")==null?"":p.get("ReferalDescription").toString())
					.collect(Collectors.joining());
			System.out.println(referalRemarks);
			
			
			
			if(StringUtils.isNotBlank(referalRemarks))	{
				
				return "*QUOTATION HAS BEEN REFERRAL ("+reqRefNo+") || CONTACT ADMIN..!*";
			}
			
			 cover_list=viewCalcResult.stream().filter(p ->p.get("CoverageType").equals("B"))
					.map(p ->p).findFirst().orElse(null);
					
					coverId=cover_list.get("CoverId")==null?"":cover_list.get("CoverId").toString();
					
					Map<String,Object> tax =null;
					try {
					List<Map<String,Object>> yaraTaxList =cover_list.get("Taxes")==null?null: 
									mapper.readValue(mapper.writeValueAsString(cover_list.get("Taxes")), List.class);
					tax=yaraTaxList.stream().filter(p ->p.get("TaxId").equals("1")).findFirst().orElse(null);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
					
				premium =cover_list.get("PremiumExcluedTax")==null?0D:Double.valueOf(cover_list.get("PremiumExcluedTax").toString());
				vatTax =tax.get("TaxAmount")==null?0D:Double.valueOf(tax.get("TaxAmount").toString());
				vatPercentage =tax.get("TaxRate")==null?0L:Double.valueOf(tax.get("TaxRate").toString());
				coverId=cover_list.get("CoverId")==null?"572":cover_list.get("CoverId").toString();
					
				
				totalPremium =new BigDecimal(premium).add(new BigDecimal(vatTax));
				totalPremium = totalPremium.setScale(3,RoundingMode.HALF_UP);
				
				if(totalPremium.compareTo(new BigDecimal(req.getRiskDetails().getPremiumAmount())) != 0)  {
					return "The Premium Amount is mismatched, Please check the Premium Amount... The portal Premium Amount is :"+totalPremium;
				}
	
		} catch (Exception e) {
			e.printStackTrace();
			// exception=e.getMessage();
			log.info(e.getMessage());
		}
		
		log.info("VIEW CALC BLOCK END : " + new Date());
		
		//==============================VIEW CALC BLOCK END===============================================
		
		//==============================UPDATE FACTOR RATE BLOCK START===============================================
		
		log.info("UPDATE FACTOR RATE BLOCK START : " + new Date());
		Map<String,Object> factorRate = new HashMap<>();
		factorRate.put("RequestReferenceNo", reqRefNo);
		factorRate.put("ProductId", keysSetup.getProductId());
		factorRate.put("AdminLoginId", keysSetup.getLoginId());
		factorRate.put("InsuranceId", keysSetup.getCompanyId());
		
		//Location
		Map<String,Object> locationMap = new HashMap<>();
		locationMap.put("LocationId", "1");
		
		//SectionDetails
		Map<String,Object> sectionMap = new HashMap<>();
		sectionMap.put("SectionId", keysSetup.getSectionId());
		sectionMap.put("RiskId", "1");
		
		//Covers
		Map<String,Object> coversMap = new HashMap<>();
		coversMap.put("CoverId", coverList.get("CoverId"));
		coversMap.put("CalcType", coverList.get("CalcType"));
		coversMap.put("CoverName", coverList.get("CoverName"));
		coversMap.put("CoverDesc", coverList.get("CoverDesc"));
		coversMap.put("MinimumPremium", coverList.get("MinimumPremium"));
		coversMap.put("CoverToolTip", coverList.get("CoverToolTip"));
		coversMap.put("IsSubCover", coverList.get("IsSubCover"));
		coversMap.put("SumInsured", coverList.get("SumInsured"));
		coversMap.put("SumInsuredLc", coverList.get("SumInsuredLc"));
		coversMap.put("Rate", coverList.get("Rate"));
		coversMap.put("SubCoverId", coverList.get("SubCoverId"));
		coversMap.put("SubCoverDesc", coverList.get("SubCoverDesc"));
		coversMap.put("SubCoverName", coverList.get("SubCoverName"));
		coversMap.put("SectionId", coverList.get("SectionId"));
		coversMap.put("Discounts", coverList.get("Discounts"));
		coversMap.put("SubCovers", coverList.get("SubCovers"));
		coversMap.put("FactorTypeId", coverList.get("FactorTypeId"));
		coversMap.put("DependentCoverYN", coverList.get("DependentCoverYN"));
		coversMap.put("DependentCoverId", coverList.get("DependentCoverId")== null?"": coverList.get("DependentCoverId"));
		coversMap.put("Exception", coverList.get("Exception"));
		coversMap.put("Loadings", coverList.get("Loadings"));
		coversMap.put("CoverageType", coverList.get("CoverageType"));
		coversMap.put("isSelected", coverList.get("isSelected"));
		coversMap.put("Notsutable", coverList.get("Notsutable"));
		coversMap.put("PremiumBeforeDiscountLC", coverList.get("PremiumBeforeDiscountLC"));
		coversMap.put("PremiumAfterDiscountLC", coverList.get("PremiumAfterDiscountLC"));
		coversMap.put("PremiumExcluedTaxLC", coverList.get("PremiumExcluedTaxLC"));
		coversMap.put("PremiumIncludedTaxLC", coverList.get("PremiumIncludedTaxLC"));
		coversMap.put("PremiumBeforeDiscount", coverList.get("PremiumBeforeDiscount"));
		coversMap.put("PremiumAfterDiscount", coverList.get("PremiumAfterDiscount"));
		coversMap.put("PremiumExcluedTax", coverList.get("PremiumExcluedTax"));
		coversMap.put("PremiumIncludedTax", coverList.get("PremiumIncludedTax"));
		coversMap.put("ExchangeRate", coverList.get("ExchangeRate"));
		coversMap.put("Currency", coverList.get("Currency"));
		coversMap.put("isReferal", coverList.get("isReferal"));
		coversMap.put("ReferalDescription", coverList.get("ReferalDescription")== null?"": coverList.get("ReferalDescription"));
		coversMap.put("ProRata", coverList.get("ProRata")== null?"": coverList.get("ProRata"));
		coversMap.put("RegulatorSumInsured", coverList.get("RegulatorSumInsured"));
		coversMap.put("RegulatorRate", coverList.get("RegulatorRate"));
		coversMap.put("UserOpt", coverList.get("UserOpt"));
		coversMap.put("CoverBasedOn", coverList.get("CoverBasedOn"));
		coversMap.put("RegulatoryCode", coverList.get("RegulatoryCode"));
		coversMap.put("InsuranceId", coverList.get("InsuranceId"));
		coversMap.put("BranchCode", coverList.get("BranchCode"));
		coversMap.put("AgencyCode", coverList.get("AgencyCode"));
		coversMap.put("ProductId", coverList.get("ProductId"));
		coversMap.put("MSRefNo", coverList.get("MSRefNo"));
		coversMap.put("VehicleId", coverList.get("VehicleId"));
		coversMap.put("CdRefNo", coverList.get("CdRefNo"));
		coversMap.put("VdRefNo", coverList.get("VdRefNo"));
		coversMap.put("CreatedBy", coverList.get("CreatedBy"));
		coversMap.put("RequestReferenceNo", coverList.get("RequestReferenceNo"));
		coversMap.put("MultiSelectYn", coverList.get("MultiSelectYn"));
		coversMap.put("SectionName", coverList.get("SectionName"));
		coversMap.put("ExcessPercent", coverList.get("ExcessPercent"));
		coversMap.put("ExcessAmount", coverList.get("ExcessAmount"));
		coversMap.put("ExcessDesc", coverList.get("ExcessDesc"));
		coversMap.put("MinimumPremiumYn", coverList.get("MinimumPremiumYn"));
		coversMap.put("ProRataApplicable", coverList.get("ProRataApplicable"));
		coversMap.put("Endorsements", coverList.get("Endorsements"));
		coversMap.put("EndtCount", coverList.get("EndtCount"));
		coversMap.put("EffectiveDate", coverList.get("EffectiveDate"));
		coversMap.put("PolicyEndDate", coverList.get("PolicyEndDate"));
		coversMap.put("Status", coverList.get("Status"));
		coversMap.put("DiffPremiumIncludedTax", coverList.get("DiffPremiumIncludedTax"));
		coversMap.put("DiffPremiumIncludedTaxLC", coverList.get("DiffPremiumIncludedTaxLC"));
		coversMap.put("CoverageLimit", coverList.get("CoverageLimit"));
		coversMap.put("MinSumInsured", coverList.get("MinSumInsured"));
		coversMap.put("PolicyPeriod", coverList.get("PolicyPeriod"));
		coversMap.put("IsTaxExcempted", coverList.get("IsTaxExcempted"));
		coversMap.put("FreeCoverLimit", coverList.get("FreeCoverLimit"));
		coversMap.put("CoverNameLocal", coverList.get("CoverNameLocal")== null?"": coverList.get("CoverNameLocal"));
		coversMap.put("CoverDescLocal", coverList.get("CoverDescLocal")== null?"": coverList.get("CoverDescLocal"));
		coversMap.put("SubCoverDescLocal", coverList.get("SubCoverDescLocal"));
		coversMap.put("SubCoverNameLocal", coverList.get("SubCoverNameLocal"));
		coversMap.put("LocationId", coverList.get("LocationId"));
		coversMap.put("MinRate", coverList.get("MinRate"));
		coversMap.put("MinimumRateYn", coverList.get("MinimumRateYn"));
		coversMap.put("ActualRate", coverList.get("ActualRate"));
		coversMap.put("ContentDesc", coverList.get("ContentDesc"));
		coversMap.put("RiskId", "1");
		coversMap.put("selected", true);
		
		
		//Taxes
		Map<String,Object> taxesMap = new HashMap<>();
		taxesMap.put("isTaxExempted", taxes.get("isTaxExempted"));
		taxesMap.put("TaxId", taxes.get("TaxId"));
		taxesMap.put("TaxRate", taxes.get("TaxRate"));
		taxesMap.put("TaxAmount", taxes.get("TaxAmount"));
		taxesMap.put("TaxDesc", taxes.get("TaxDesc"));
		taxesMap.put("TaxExemptType", taxes.get("TaxExemptType"));
		taxesMap.put("TaxExemptCode", taxes.get("TaxExemptCode")== null?"":taxes.get("TaxExemptCode"));
		taxesMap.put("TaxCalcType", taxes.get("TaxCalcType"));
		taxesMap.put("RegulatoryCode", taxes.get("RegulatoryCode"));
		taxesMap.put("EndtTypeId", taxes.get("EndtTypeId"));
		taxesMap.put("EndtTypeCount", taxes.get("EndtTypeCount"));
		taxesMap.put("DependentYN", taxes.get("DependentYN"));
		taxesMap.put("TaxExemptedAllowed", taxes.get("TaxExemptedAllowed"));
		taxesMap.put("MinimumTaxAmount", taxes.get("MinimumTaxAmount"));
		taxesMap.put("MinimumTaxAmountLC", taxes.get("MinimumTaxAmountLC"));
		taxesMap.put("TaxAmountLc", taxes.get("TaxAmountLc"));
		taxesMap.put("TaxFor", taxes.get("TaxFor"));
		taxesMap.put("extend_Cust_tax", taxes.get("extend_Cust_tax"));
		
		coversMap.put("Taxes", Arrays.asList(taxesMap));
		
		//RiskDetail
		Map<String,Object> riskMap = new HashMap<>();
		riskMap.put("RiskId", "1");
		riskMap.put("DocumentsTitle", null);
		riskMap.put("InbuildConstructType", null);
		riskMap.put("BuildingFloors", req.getRiskDetails().getNoOfAcre());
		riskMap.put("OutbuildConstructType", null);
		riskMap.put("BuildingUsageYn", null);
		riskMap.put("BuildingPurpose", null);
		riskMap.put("BuildingUsageDesc", "Building");
		riskMap.put("BuildingPurposeId", null);
		riskMap.put("BuildingUsageId", req.getRiskDetails().getDistributorId());
		riskMap.put("PaDeathSuminsured", null);
		riskMap.put("PaPermanentdisablementSuminsured", null);
		riskMap.put("PaTotaldisabilitySumInsured", null);
		riskMap.put("PaMedicalSuminsured", null);
		riskMap.put("BuildingType", null);
		riskMap.put("BuildingOwnerYn", "Y");
		riskMap.put("PersonalIntermediarySuminsured", null);
		riskMap.put("BuildingOccupationType", null);
		riskMap.put("WithoutInhabitantDays", null);
		riskMap.put("BuildingCondition", null);
		riskMap.put("BuildingBuildYear", null);
		riskMap.put("BuidingAreaSqm", null);
		riskMap.put("BuildingSuminsured", null);
		riskMap.put("AllriskSumInsured", null);
		riskMap.put("ContentSuminsured", null);
		riskMap.put("DomesticPackageYn", "Y");
		riskMap.put("EndorsementYn", null);
		riskMap.put("SectionId", coverList.get("SectionId"));
		riskMap.put("Suminsured", null);
		riskMap.put("OccupationType", null);
		riskMap.put("OccupationTypeDesc", null);
		riskMap.put("CategoryId", "2");
		riskMap.put("SectionDetails", null);
		riskMap.put("PremiumLc", null);
		riskMap.put("PremiumFc", null);
		riskMap.put("OverAllPremiumFc", null);
		riskMap.put("OverAllPremiumLc", null);
		riskMap.put("CommissionAmount", null);
		riskMap.put("CommissionPercentage", "12.5");
		riskMap.put("WallType", req.getRiskDetails().getYaraPackageYn());
		riskMap.put("WallTypeDesc", null);
		riskMap.put("RoofType", null);
		riskMap.put("RoofTypeDesc", null);
		riskMap.put("NatureOfTradeId", null);
		riskMap.put("NatureOfTradeDesc", null);
		riskMap.put("InsuranceForId", null);
		riskMap.put("InsuranceForDesc", null);
		riskMap.put("InternalWallType", "0");
		riskMap.put("InternalWallDesc", "");
		riskMap.put("CeilingType", null);
		riskMap.put("CeilingTypeDesc", null);
		riskMap.put("StockInTradeSi", null);
		riskMap.put("GoodsSi", null);
		riskMap.put("FurnitureSi", null);
		riskMap.put("ApplianceSi", null);
		riskMap.put("CashValueablesSi", null);
		riskMap.put("Address", req.getCustomerDetails().getPostalAddress());
		riskMap.put("RegionCode", keysSetup.getRegionCode());
		riskMap.put("RegionDesc", req.getCustomerDetails().getRegion());
		riskMap.put("DistrictCode", keysSetup.getCityCode());
		riskMap.put("DistrictDesc", req.getCustomerDetails().getDistrict());
		riskMap.put("OccupiedYear", null);
		riskMap.put("showWindow", null);
		riskMap.put("FrontDoors", null);
		riskMap.put("BackDoors", null);
		riskMap.put("WindowsMaterialId", null);
		riskMap.put("WindowsMaterialDesc", null);
		riskMap.put("DoorsMaterialId", null);
		riskMap.put("DoorsMaterialDesc", null);
		riskMap.put("NightLeftDoor", null);
		riskMap.put("NightLeftDoorDesc", null);
		riskMap.put("BuildingOccupied", null);
		riskMap.put("BuildingOccupiedDesc", null);
		riskMap.put("WatchmanGuardHours", null);
		riskMap.put("AccessibleWindows", null);
		riskMap.put("TrapDoors", null);
		riskMap.put("MachineEquipSi", null);
		riskMap.put("PlateGlassSi", null);
		riskMap.put("FirstLossPercentId", null);
		riskMap.put("FirstLossPercent", null);
		riskMap.put("AccDamageSi", null);
		riskMap.put("BurglarySi", null);
		riskMap.put("PowerPlantSi", null);
		riskMap.put("ElecMachinesSi", null);
		riskMap.put("EquipmentSi", null);
		riskMap.put("GeneralMachineSi", null);
		riskMap.put("ManuUnitsSi", null);
		riskMap.put("BoilerPlantsSi", null);
		riskMap.put("TiraCoverNoteNo", null);
		riskMap.put("IndemityPeriod", "0");
		riskMap.put("IndemityPeriodDesc", null);
		riskMap.put("MakutiYn", null);
		riskMap.put("PlateGlassType", null);
		riskMap.put("PlateGlassDesc", null);
		riskMap.put("StockLossPercent", null);
		riskMap.put("GoodsLossPercent", null);
		riskMap.put("FurnitureLossPercent", null);
		riskMap.put("ApplianceLossPercent", null);
		riskMap.put("CashValueablesLossPercent", null);
		riskMap.put("LocationId", "1");
		riskMap.put("LocationName", "chennai");
		riskMap.put("MiningPlantSi", null);
		riskMap.put("NonminingPlantSi", null);
		riskMap.put("GensetsSi", null);
		riskMap.put("VatCommission", null);
		riskMap.put("PolicyNo", null);
		riskMap.put("MoneySafeLimit", "0.0");
		riskMap.put("MoneyOutofSafe", "0.0");
		riskMap.put("MoneyDirectorResidence", "0.0");
		riskMap.put("MoneyCollector", "0.0");
		riskMap.put("MoneyAnnualEstimate", "0.0");
		riskMap.put("MoneyMajorLoss", "0.0");
		riskMap.put("ElecEquipSuminsured", null);
		riskMap.put("MachinerySi", null);
		riskMap.put("FireEquipSi", null);
		riskMap.put("FirePlantSi", null);
		riskMap.put("BondSuminsured", null);
		riskMap.put("FinalyzeYn", null);
		riskMap.put("CategoryDesc", null);
		
		coversMap.put("RiskDetails", riskMap);
		sectionMap.put("Covers", Arrays.asList(coversMap));
		locationMap.put("SectionDetails", Arrays.asList(sectionMap));
		factorRate.put("LocationDetails", Arrays.asList(locationMap));
		
		String factorRateReq = objectPrint.toJson(factorRate);
		System.out.println("Factor Rate Request" + factorRateReq);
		log.info("Factor Rate Request : " + factorRateReq);
		
		response = this.callEwayApi(factorRateAPi, factorRateReq);
		System.out.println("Factor Rate Response" + response);
		log.info("Factor Rate Response : " + response);
		Map<String, Object> factorRateResult = null;
		//String reqRefNo = "";
		try {
			Map<String, Object> factorRateRes = (Map) this.mapper.readValue(response, Map.class);
			factorRateResult = factorRateRes.get("Result") == null ? null
					: (Map) this.mapper.readValue(this.mapper.writeValueAsString(factorRateRes.get("Result")),
							Map.class);
			if (factorRateResult == null) {
				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
				return factorRateRes;
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			// exception=e.getMessage();
			log.info(e.getMessage());
		}
		
		log.info("UPDATE FACTOR RATE BLOCK END : " + new Date());
		
		//==============================UPDATE FACTOR RATE BLOCK END==============================================
   
		
		//==============================BUYPOLICY BLOCK END========================================================
		
		log.info("BUYPOLICY BLOCK START : " + new Date());
		Map<String,Object> buyPolicyMap = new HashMap<>();
		buyPolicyMap.put("RequestReferenceNo", reqRefNo);
		buyPolicyMap.put("CreatedBy", keysSetup.getLoginId());
		buyPolicyMap.put("ProductId", keysSetup.getProductId());
		buyPolicyMap.put("ManualReferralYn", "N");
		buyPolicyMap.put("EmiYn", "N");
		buyPolicyMap.put("ReferralRemarks", null);
		
		Map<String,Object> vehicleMap = new HashMap<>();
		vehicleMap.put("LocationId", "1");
		vehicleMap.put("Id", "1");
		vehicleMap.put("SectionId", keysSetup.getSectionId());
		
		Map<String,Object> coverMap = new HashMap<>();
		coverMap.put("CoverId", keysSetup.getCoverId());
		coverMap.put("SubCoverId", null);
		coverMap.put("SubCoverYn", "N");
		
		vehicleMap.put("Covers", Arrays.asList(coverMap));
		buyPolicyMap.put("Vehicles", Arrays.asList(vehicleMap));
		
		String buyPolicyReq = objectPrint.toJson(buyPolicyMap);
	//	System.out.println("Buy Policy Request" + buyPolicyReq);
		log.info("Buy Policy Request : " + buyPolicyReq);
		
		response = this.callEwayApi(buyPolicyApi, buyPolicyReq);
	//	System.out.println("Buy Policy Response" + response);
		log.info("Buy Policy Response : " + response);
		Map<String, Object> buyPolicyResult = null;
		//String reqRefNo = "";
		try {
			Map<String, Object> buyPolicyRes = (Map) this.mapper.readValue(response, Map.class);
			buyPolicyResult = buyPolicyRes.get("Result") == null ? null
					: (Map) this.mapper.readValue(this.mapper.writeValueAsString(buyPolicyRes.get("Result")),
							Map.class);
			if (buyPolicyResult == null) {
				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
				return buyPolicyRes;
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			// exception=e.getMessage();
			log.info(e.getMessage());
		}
		
		log.info("BUYPOLICY BLOCK END : " + new Date());
		
		//==============================BUYPOLICY BLOCK END===============================================
		
		//==============================MAKE PAYEMENT BLOCK START===============================================
		
		log.info("MAKE PAYEMENT BLOCK START : " + new Date());
		
		Map<String,Object> makePaymentMap = new HashMap<>();
		makePaymentMap.put("CreatedBy", keysSetup.getLoginId());
		makePaymentMap.put("EmiYn", "N");
		makePaymentMap.put("InstallmentMonth", "");
		makePaymentMap.put("InstallmentPeriod", "");
		makePaymentMap.put("InsuranceId", keysSetup.getCompanyId());
		makePaymentMap.put("Premium", totalPremium);
		makePaymentMap.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
		makePaymentMap.put("Remarks", "None");
		makePaymentMap.put("SubUserType", keysSetup.getSubUserType());
		makePaymentMap.put("UserType", keysSetup.getUserType());
		
		String makePayementReq = objectPrint.toJson(makePaymentMap);
	//	System.out.println("Make Payment Request" + makePayementReq);
		log.info("Make Payment Request : " + makePayementReq);
		
		response = this.callEwayApi(makePaymentApi, makePayementReq);
		System.out.println("Make Payment Response" + response);
		log.info("Make Payment Response : " + response);
		Map<String, Object> makePaymentResult = null;
		//String reqRefNo = "";
		try {
			Map<String, Object> makePaymentRes = (Map) this.mapper.readValue(response, Map.class);
			makePaymentResult = makePaymentRes.get("Result") == null ? null
					: (Map) this.mapper.readValue(this.mapper.writeValueAsString(makePaymentRes.get("Result")),
							Map.class);
			if (makePaymentResult == null) {
				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
				return makePaymentRes;
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			// exception=e.getMessage();
			log.info(e.getMessage());
		}
		
		log.info("MAKE PAYMENT BLOCK END : " + new Date());
		
		//==============================BUYPOLICY BLOCK END===============================================
		
		//==============================INSERT PAYMENT BLOCK START===============================================
		
		log.info("INSERT PAYMENT BLOCK START : " + new Date());
		
		Map<String,Object> insertPaymentMap = new HashMap<>();
		insertPaymentMap.put("CreatedBy", keysSetup.getLoginId());
		insertPaymentMap.put("InsuranceId", keysSetup.getCompanyId());
		insertPaymentMap.put("EmiYn", "N");
		insertPaymentMap.put("Premium", totalPremium);
		insertPaymentMap.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
		insertPaymentMap.put("Remarks", "None");
		insertPaymentMap.put("PayeeName", req.getCustomerDetails().getCustomerName());
		insertPaymentMap.put("SubUserType", keysSetup.getSubUserType());
		insertPaymentMap.put("UserType", keysSetup.getUserType());
		insertPaymentMap.put("PaymentId", makePaymentResult.get("PaymentId"));
		insertPaymentMap.put("PaymentType", req.getPolicyDetails().getPaymentMode()==null?"1":req.getPolicyDetails().getPaymentMode());
		
		String insertPaymentReq = objectPrint.toJson(insertPaymentMap);
	//	System.out.println("Insert Payment Request" + insertPaymentReq);
		log.info("Insert Payment Request : " + insertPaymentReq);
		
		response = this.callEwayApi(insertPaymentApi, insertPaymentReq);
	//	System.out.println("Insert Payment Response" + response);
		log.info("Insert Payment Response : " + response);
		Map<String, Object> insertPaymentResult = null;
		//String reqRefNo = "";
		try {
			Map<String, Object> insertPaymentRes = (Map) this.mapper.readValue(response, Map.class);
			insertPaymentResult = insertPaymentRes.get("Result") == null ? null
					: (Map) this.mapper.readValue(this.mapper.writeValueAsString(insertPaymentRes.get("Result")),
							Map.class);
			if (insertPaymentResult == null) {
				// errorList.add(makePaymentRes.get("ErrorMessage").toString());
				return insertPaymentRes;
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			// exception=e.getMessage();
			log.info(e.getMessage());
		}
		
		log.info("MAKE PAYMENT BLOCK END : " + new Date());
		
		//==============================BUYPOLICY BLOCK END==============================================

		//==============================RESULT BLOCK START===============================================
		
		log.info("RESULT BLOCK START : " + new Date());
		
		Map<String,Object> resultMap = new HashMap<>();
		resultMap.put("RequestReferenceNo", reqRefNo);
		resultMap.put("QuoteNo", insertPaymentResult.get("QuoteNo") == null ? null : insertPaymentResult.get("QuoteNo"));
		resultMap.put("Response", insertPaymentResult.get("Response") == null ? null : insertPaymentResult.get("Response"));
		resultMap.put("PolicyNo", insertPaymentResult.get("PolicyNo") == null ? null : insertPaymentResult.get("PolicyNo"));
		resultMap.put("DebitNoteNo", insertPaymentResult.get("DebitNoteNo") == null ? null : insertPaymentResult.get("DebitNoteNo"));
		resultMap.put("CreditNoteNo", insertPaymentResult.get("CreditNoteNo") == null ? null : insertPaymentResult.get("CreditNoteNo"));
		
		
		log.info("RESULT BLOCK END : " + new Date());
		return resultMap;
		
		//==============================RESULT BLOCK END==============================================
	}

	

	private String callEwayApi(String url, String request) {
		String apiReponse = null;
		try {
			Map<String, Object> tokReq = new HashMap();
			tokReq.put("LoginId", brokerLoginId);
			tokReq.put("Password", brokerPassword);
			tokReq.put("ReLoginKey", "Y");
		//	System.out.println("Token Api URL ==> " + tokenApi);
			String jsonTokenRequest = (new Gson()).toJson(tokReq);
			CloseableHttpClient httpClient = createHttpClientWithTimeouts();
			HttpPost postRequest = new HttpPost(tokenApi);
			postRequest.setHeader("Content-Type", "application/json");
			postRequest.setEntity(new StringEntity(jsonTokenRequest));
		//	System.out.println("Token Api Req ==> " + jsonTokenRequest);
			CloseableHttpResponse response = httpClient.execute(postRequest);
		//	System.out.println("Token Api Res ==> " + String.valueOf(response));
			String responseString = null;
			String token = "";

			Map<String, Object> tokenObj;
			try {
				HttpEntity entity = response.getEntity();
				responseString = EntityUtils.toString(entity);
			//	System.out.println("Token Api Res ==> " + responseString);
				Map<String, Object> tokenRes = (Map) (new Gson()).fromJson(responseString, Map.class);
				tokenObj = tokenRes.get("Result") == null ? null : (Map) tokenRes.get("Result");
				token = tokenObj.get("Token") == null ? "" : tokenObj.get("Token").toString();
				log.info("Token : " + token);
			//	System.out.println("Token Api Resp ==> " + token);
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
	
	private static CloseableHttpClient createHttpClientWithTimeouts() {
		try {
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(60000)
					.build();
			return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			// log.info(e);
		}
		return null;
	}

	private YaraImportantKeys otherKeysSetup(YaraReq req) {
		
		YaraImportantKeys keys = new YaraImportantKeys();
		
		try {
			//GetBroker Details
			LoginMaster loginMasterDetails = loginMasterRepo.findByLoginId(brokerLoginId);
			keys.setLoginId(loginMasterDetails.getLoginId());
			keys.setSubUserType(loginMasterDetails.getSubUserType());
			keys.setUserType(loginMasterDetails.getUserType());
			keys.setCompanyId(loginMasterDetails.getCompanyId());
			keys.setAgencyCode(loginMasterDetails.getAgencyCode());
			
			//GetBranchDetails
			List<LoginBranchMaster> branchDetails = branchMasterRepo.findByLoginId(keys.getLoginId());
			LoginBranchMaster branches = branchDetails.get(0);
			keys.setBranchCode(branches.getBranchCode());
			keys.setBranchName(branches.getBranchName());
			keys.setBrokerBranchCode(branches.getBrokerBranchCode());
			keys.setBrokerBranchName(branches.getBrokerBranchName());
			
			//GetBrokerInfo
			LoginUserInfo userDetails = loginUserInfoRepo.findByLoginId(keys.getLoginId());
			keys.setBrokerName(userDetails.getUserName());
			keys.setCompanyName(userDetails.getCompanyName());
			keys.setCountryCode(userDetails.getCountryCode());
			keys.setCustomerCode(userDetails.getCustomerCode());
			keys.setMobileCode(userDetails.getMobileCode());
			
			//product
			List<CompanyProductMaster> productDetails = productMasterRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(keys.getCompanyId(),86);
			CompanyProductMaster details = productDetails.get(0);
			keys.setProductId(String.valueOf(details.getProductId()));
			keys.setProductName(details.getProductName());
			
			//CountryName
			List<CountryMaster> country = countryMasterRepo.findByCompanyId(keys.getCompanyId());
			if(country != null) {
				keys.setCountryName(country.get(0).getCountryName());
			}
			
			//customerDetails
			keys.setOccupationId("420");
			keys.setOccupationDesc("Others");
			
			//IdTypeDesc
			if(StringUtils.isNotBlank(req.getCustomerDetails().getIdType())) {
				if(req.getCustomerDetails().getIdType().equals("1")) {
					keys.setIdTypeDesc("National Identification Number(NIDA)");
				}else if(req.getCustomerDetails().getIdType().equals("2")) {
					keys.setIdTypeDesc("Voters Registration Number");
				}else if(req.getCustomerDetails().getIdType().equals("3")) {
					keys.setIdTypeDesc("Passport Number");
				}else if(req.getCustomerDetails().getIdType().equals("4")) {
					keys.setIdTypeDesc("Driving License");
				}else if(req.getCustomerDetails().getIdType().equals("5")) {
					keys.setIdTypeDesc("Zanzibar Resident Id (ZANID)");
				}else if(req.getCustomerDetails().getIdType().equals("6")) {
					keys.setIdTypeDesc("Tax Identification Number (TIN)");
				}else {
					keys.setIdTypeDesc("Passport Number");
				}
			}
			
			keys.setExchangeRate("1.0");
			keys.setCurrencyCode("TZS");
			
			//getSectionDetails
			List<ProductSectionMaster> sectionDetails = sectionMasterRepo.findByProductIdAndCompanyId(Integer.parseInt(keys.getProductId()),
					keys.getCompanyId());
			ProductSectionMaster section = sectionDetails.get(0);
			keys.setSectionId(section.getSectionId().toString());
			keys.setSectionName(section.getSectionName());
			
			//getCoverDetails
			List<SectionCoverMaster> coverDetails = coverMasterRepo.findByCompanyIdAndProductIdAndSectionIdAndStatusOrderByAmendIdDesc(
					keys.getCompanyId(), Integer.parseInt(keys.getProductId()),Integer.parseInt(keys.getSectionId()) ,"Y");
			SectionCoverMaster covers = coverDetails.get(0);
			keys.setCoverId(covers.getCoverId().toString());
			keys.setCoverName(covers.getCoverName());
			
			//RegionCode
			List<AgricultureMaster> state = null; 
			List<AgricultureMaster> region = agriRepo.findByCompanyIdAndProvinceDesc(Integer.valueOf(keys.getCompanyId()), req.getCustomerDetails().getRegion());
			if(region != null) {
				keys.setRegionCode(region.get(0).getProvinceId().toString());
				keys.setRegionName(region.get(0).getProvinceDesc());
				state = agriRepo.findByCompanyIdAndProvinceIdAndDistrictDesc(Integer.valueOf(keys.getCompanyId()),Integer.valueOf(keys.getRegionCode()),req.getCustomerDetails().getDistrict());
					
			}if(!state.isEmpty()) {
				keys.setCityCode(state.get(0).getDistrictId().toString());
				keys.setCityName(state.get(0).getDistrictDesc());
			}
			else {
				keys.setRegionCode("51000");
				keys.setRegionName("Iringa");
				keys.setCityCode("51100");
				keys.setCityName("Iringa CBD");
				
			}
			
			//Get Dates 
			LocalDate today = LocalDate.now();
			LocalDate endDate = today.plusDays(364);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			try {
				keys.setPolicyStartDate(today.format(formatter));
				keys.setPolicyEndDate(endDate.format(formatter));
			}catch(Exception e) {
				e.printStackTrace();
			}

		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
				
		return keys;
	}

	@Override
	public List<Error> validationYara(YaraReq req) {
    List<Error> errorList = new ArrayList<Error>();
		
		//Customer Details validation
    List<String> coveragePercentage = Arrays.asList("100","80","60");
		if(StringUtils.isBlank(req.getCustomerDetails().getCustomerName())) {
			errorList.add(new Error("01","CustomerName","Please Enter the Customer Name"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getDateOfBirth())) {
			errorList.add(new Error("01","DateOfBirth","Please Enter the Date of Birth"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getIdNumber())) {
			errorList.add(new Error("01","IdNumber","Please Enter the Id Number"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getIdType())) {
			errorList.add(new Error("01","IdType","Please Enter the Id Type"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getGender())) {
			errorList.add(new Error("01","Gender","Please Enter the Gender.(Eg: M for Male, F for Female and O for Others"));
		}
	//	if(StringUtils.isBlank(req.getCustomerDetails().getCountryCode())) {
	//		errorList.add(new Error("01","CountryCode","Please Enter the Country Code"));
	//	}
		if(StringUtils.isBlank(req.getCustomerDetails().getRegion())) {
			errorList.add(new Error("01","Region","Please Enter the Region name"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getDistrict())) {
			errorList.add(new Error("01","District","Please Enter the District name"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getPhoneNumber())) {
			errorList.add(new Error("01","PhoneNumber","Please Enter the Phone Number"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getPostalAddress())) {
			errorList.add(new Error("01","PostalAddress","Please Enter the Adress Details"));
		}
		if(StringUtils.isBlank(req.getCustomerDetails().getEmailAddress())) {
			errorList.add(new Error("01","EmailAddress","Please Enter the Email Address"));
		}
		if (req.getRiskDetails().getYaraPackageYn().equalsIgnoreCase("Y")) {
			if (StringUtils.isBlank(req.getRiskDetails().getCoveragePercentage())) {
				errorList.add(new Error("01", "CoveragePercentage", "Please Enter the Coverage Percentage"));
			} else if (StringUtils.isNotBlank(req.getRiskDetails().getCoveragePercentage())) {
				if (!coveragePercentage.contains(req.getRiskDetails().getCoveragePercentage())) {
					errorList.add(new Error("02", "CoveragePercentage",
							"Please Enter Valid Coverage Percentage, 100,80 and 60 Percantages only allowed"));
				}

			}

		}
		//Policy Details validation
//		if(StringUtils.isBlank(req.getPolicyDetails().getPolicyStartDate())) {
//			errorList.add(new Error("02","PolicyStartDate","Please Enter the Policy Start Date"));
//		}
//		if(StringUtils.isBlank(req.getPolicyDetails().getPolicyEndDate())) {
//			errorList.add(new Error("02","PolicyEndDate","Please Enter the Policy End Date"));
//		}
//		if(StringUtils.isBlank(req.getPolicyDetails().getCurrency())) {
//			errorList.add(new Error("02","Currency","Please Enter the Currency code"));
//		}
//		if(StringUtils.isBlank(req.getPolicyDetails().getPaymentMode())) {
//			errorList.add(new Error("02","PaymentMode","Please Enter the Payment Mode"));
//		}
		
		//Risk Details validation
		if(StringUtils.isBlank(req.getRiskDetails().getDistributorId())) {
			errorList.add(new Error("03","DistributorId","Please Enter the Distributor Id"));
		}
		if(StringUtils.isBlank(req.getRiskDetails().getDistributorName())) {
			errorList.add(new Error("03","DistributorName","Please Enter the Distributor Name"));
		}
		if(StringUtils.isBlank(req.getRiskDetails().getYaraPackageYn())) {
			errorList.add(new Error("03","YaraPackageYn","Please Enter the Package (Eg: Y for yes and N for no"));
		}
		//if(StringUtils.isBlank(req.getRiskDetails().getCoverage())) {
		//	errorList.add(new Error("03","Coverage","Please Enter the Coverage"));
		//}
		if(StringUtils.isBlank(req.getRiskDetails().getNoOfAcre())) {
			errorList.add(new Error("03","NoOfAcre","Please Enter Acres number"));
		}
		if(StringUtils.isBlank(req.getRiskDetails().getSumInsured())) {
			errorList.add(new Error("03","SumInsured","Please Enter SumInsured"));
		}
		if(StringUtils.isBlank(req.getRiskDetails().getPremiumAmount())) {
			errorList.add(new Error("03","PremiumAmount","Please Enter Premium Amount"));
		}
		
		return errorList;

	}



	@Override
	public List<YaraDocumentResp> documentDetails(YaraDocumentReq req) {
		
		//YaraDocumentResp resp = new YaraDocumentResp();
		List<YaraDocumentResp> resultList = new ArrayList<>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<YaraDocumentResp> query = cb.createQuery(YaraDocumentResp.class);
			
			Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
			Root<EserviceBuildingDetails> ebd = query.from(EserviceBuildingDetails.class);
			Root<PersonalInfo> pi = query.from(PersonalInfo.class);
			
			query.multiselect(hpm.get("policyNo").alias("policyNo"),pi.get("firstName").alias("firstName"),pi.get("middleName").alias("middleName"),
					pi.get("lastName").alias("lastName"),hpm.get("companyName").alias("comapanyName"),hpm.get("inceptionDate").alias("policyStartDate"),
					hpm.get("expiryDate").alias("policyEndDate"),ebd.get("sumInsured").as(String.class).alias("sumInsured"),ebd.get("sumInsuredLc").as(String.class).alias("limitsOfLiability"),
					hpm.get("vatPercent").as(String.class).alias("rate"),hpm.get("overallPremiumLc").as(String.class).alias("premiumAmount"),hpm.get("currency").as(String.class).alias("premiumCurrency"),
					pi.get("titleDesc").alias("titleDesc"),hpm.get("effectiveDate").alias("effectiveDate"),
					ebd.get("regionDesc").alias("region"),ebd.get("districtDesc").alias("district"),ebd.get("buildingFloors").alias("noOfAcres"),
					ebd.get("locationName").alias("locationName"),ebd.get("address").alias("locationAddress"),ebd.get("contentId").alias("distributorId"),
					ebd.get("contentDesc").alias("distributorDesc"),ebd.get("firstLossPercentId").alias("coverage"));
			
			Predicate n1 = cb.equal(hpm.get("quoteNo"), req.getQuoteNo());
			Predicate n2 = cb.equal(hpm.get("status"), "P");
			Predicate n3 = cb.equal(hpm.get("requestReferenceNo"), ebd.get("requestReferenceNo"));
			Predicate n4 = cb.equal(hpm.get("customerId"), pi.get("customerId"));
			Predicate n5 = cb.equal(hpm.get("quoteNo"), ebd.get("quoteNo"));
			
			query.where(n1,n2,n3,n4,n5);
			
			 resultList = em.createQuery(query).getResultList();
			
			//resp = resultList.get(0);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}



	@Override
	public Object uploadDocument(MultipartFile file, String quoteNo) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//String quoteNo = this.quoteNo;
			if(StringUtils.isNoneBlank(quoteNo)) {
				String fileExt="."+FilenameUtils.getExtension(file.getOriginalFilename());
				String fileBaseName= quoteNo+"- FormCare";
				String currentDateTime = sdf.format(new Date()).replaceAll("[^0-9]", "");
				String fileName = fileBaseName+fileExt;
				
				  File dir = new File(path);
		            if (!dir.exists()) {
		                dir.mkdirs();
		            }
				
				//save File
				 Path filePath = Paths.get(path + File.separator + fileName);
				 file.transferTo(filePath.toFile());
				 
				 String downloadUrl = documentDownloadURL + URLEncoder.encode(fileName, StandardCharsets.UTF_8);
				 
			//	 File savedFile = file.transferTo(filePath.toFile());
				 Map<String,Object> respMap = new HashMap<>();
				 if(filePath.toFile().exists() && filePath.toFile().length() > 0) {
					 respMap.put("FileName", fileName);
					 respMap.put("DownloadUrl", downloadUrl);
					 respMap.put("Message", "Saved Successfully");
					 
					 return respMap;
					 
				 }else {
					 respMap = null;
					 return respMap;
				 }			 
			}
		
			
		}catch(Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}

}
