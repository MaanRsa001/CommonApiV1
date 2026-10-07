package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.RegionMaster;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.RegionMasterRepository;
import com.maan.eway.repository.StateMasterRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
@Service
@Transactional
public class OmanEserviceCustomerDetails  {

	private Logger log = LogManager.getLogger(TanzaniaEserviceCustomerDetails.class);

	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService;

	@Autowired
	private EserviceCustomerDetailsRepository repository;

	@Autowired
	private RegionMasterRepository regionMasterRepo;

	@Autowired
	private StateMasterRepository stateMasterRepo;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;

//	@Autowired
//	private EserviceCustomerDetailsServiceImpl eCustDetailsServiceImpl;

	@Autowired
	private ListItemValueRepository listRepo;

	@Autowired
	private PersonalInfoRepository personalInforepo;

	@PersistenceContext
	private EntityManager em;
	
	public List<String> validateCustomerDetails(EserviceCustomerSaveReq req) {

	    List<String> errorList = new ArrayList<>();

	    try {

	        // Sponsor Name (Beneficiary) - CLIENT_NAME
	        if (StringUtils.isBlank(req.getClientName())) {
	            errorList.add("1092");
	        } else if (req.getClientName().length() > 100) {
	            errorList.add("1093");
	        } else if (!req.getClientName().matches("^[A-Za-z0-9&.\\-'(), ]+$")) {
	            errorList.add("1094");
	        }

	        // I.D No - ID_NUMBER
	        if (StringUtils.isBlank(req.getIdNumber())) {
	            errorList.add("1095");
	        }

	       //  Expiry Date - EXPIRY_DATE
	        Date today = new Date();

	        if (req.getExpiryDate() == null) {
	            errorList.add("1096");
	        } else if (req.getExpiryDate().before(today)) {
	            errorList.add("1097");
	        }

	        // City - CITY_NAME
	        if (StringUtils.isBlank(req.getCityName())) {
	            errorList.add("1098");
	        }

	        // Governorate - REGION_CODE
	        if (StringUtils.isBlank(req.getRegionCode())) {
	            errorList.add("1099");
	        }

	        // Mobile No - MOBILE_NO_1
	        if (StringUtils.isBlank(req.getMobileNo1())) {
	            errorList.add("1100");
	        } 
//	        else if (!req.getMobileNo1().matches("^[79]\\d{7}$")) {
//	            errorList.add("1101");
//	        }

	        // Email - EMAIL_1
	        if (StringUtils.isBlank(req.getEmail1())) {
	            errorList.add("1102");
	        } else if (!isValidMail(req.getEmail1())) {
	            errorList.add("1103");
	        }

	        // P.O Box - PIN_CODE (Optional)
	        if (StringUtils.isNotBlank(req.getPinCode()) && !req.getPinCode().matches("^\\d{1,6}$")) {
	            errorList.add("1104");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        log.info("Exception is ---> " + e.getMessage());
	        errorList.add("01");
	    }

	    return errorList;
	}
	public static boolean isValidMail(String mail) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.matches();

	}

	public static boolean checkIsValidMail(String mail) {
		String regex = "^[a-zA-Z0-9._%+-àâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]+@[a-zA-Z0-9.-àâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]+\\.[a-zA-ZàâäéèêëîïôöùûüÿçÀÂÄÉÈÊËÎÏÔÖÙÛÜŸÇ]{2,}$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mail);
		return m.matches();
	}

	public SuccessRes saveCustomerDetails(EserviceCustomerSaveReq req) {
		SuccessRes res = new SuccessRes();

		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			EserviceCustomerDetails saveData = new EserviceCustomerDetails();
			Date entryDate = null;
			String createdBy = "";
			String custRefNo = "";
			Integer productId;
			if (StringUtils.isBlank(req.getCustomerReferenceNo())) {
				// Save
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				productId = Integer.valueOf(req.getProductId());
				SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
				generateSeqReq.setInsuranceId(req.getCompanyId());
				generateSeqReq.setProductId(req.getProductId());
				generateSeqReq.setType("1");
				generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
				custRefNo = genSeqNoService.generateSeqCall(generateSeqReq);
				res.setResponse("Saved Successfully");
				res.setSuccessId(custRefNo);
			} else {
				// Update
				custRefNo = req.getCustomerReferenceNo();
				EserviceCustomerDetails findData = repository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
				entryDate = findData.getEntryDate();
				createdBy = findData.getCreatedBy();
				productId = findData.getProductId();
				res.setResponse("Updated Successfully");
				res.setSuccessId(custRefNo);
			}

			dozerMapper.map(req, saveData);
			saveData.setProductId(productId);
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(createdBy);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCustomerReferenceNo(custRefNo);

			saveData.setZone(StringUtils.isBlank(req.getZone()) ? 0 : Integer.valueOf(req.getZone()));

			saveData.setBrokerBranchCode(req.getBrokerBranchCode());

			saveData.setTitle(req.getTitle());
			saveData.setFirstName(req.getClientName());
			saveData.setBusinessType(req.getBusinessType());
			saveData.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
			saveData.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
			saveData.setOtherOccupation(req.getOtherOccupation());
			saveData.setEmail1(req.getEmail1());
			saveData.setWhatsappCode(req.getWhatsappCode());
			saveData.setMobileCode1(req.getMobileCode1());
			saveData.setMobileCode2(req.getMobileCode2() == null ? "" : req.getMobileCode2());
			saveData.setMobileCode3(req.getMobileCode3() == null ? "" : req.getMobileCode3());
			saveData.setMobileNo1(req.getMobileNo1());
			saveData.setMobileNo2(req.getMobileNo2());
			saveData.setMobileNo3(req.getMobileNo3());
			saveData.setActivities(req.getActivities());

			saveData.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted()) ? "0" : req.getIsTaxExempted());
			saveData.setPreferredNotification(req.getPreferredNotification());
			saveData.setStatus(req.getStatus());

			saveData.setCountry(req.getCountry());
			saveData.setCountryName(req.getCountryName());
			saveData.setCityCode(StringUtils.isBlank(req.getCityCode()) ? null : Integer.valueOf(req.getCityCode()));
			saveData.setCityName(determineCityName(req));
			saveData.setPinCode(req.getPinCode());
			saveData.setRegionCode(req.getRegionCode());
			saveData.setStateName(determineStateName(req));
			saveData.setHouseNo(req.getHouseNo());
			saveData.setCode(req.getCode());
			saveData.setAreaLocality(req.getAreaLocality());
			saveData.setVrTinNo(req.getVrTinNo());
			// saveData.setVrnGst(req.getVrTinNo());

			// Age Calculation
			int age = 0;
			Date dob = null;
			if (req.getDobOrRegDate() != null) {
				dob = req.getDobOrRegDate();
				Date today = new Date();
				age = today.getYear() - dob.getYear();
			}
			// From List Item Value

			Map<String, String> title = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "NAME_TITLE", req.getTitle());// listRepo.findByItemTypeAndItemCode("NAME_TITLE",
																		// req.getTitle());
			Map<String, String> gender = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "GENDER", req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER",
																	// saveData.getGender());
			Map<String, String> language = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "LANGUAGE", req.getLanguage());// listRepo.findByItemTypeAndItemCode("LANGUAGE",
																		// req.getLanguage());
			Map<String, String> policyHolderType = getListItemLocal("99999",
					req.getBranchCode(), "POLICY_HOLDER_TYPE", req.getPolicyHolderType());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",
																							// req.getPolicyHolderType());
			Map<String, String> policyHolderTypeId = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE",
																								// req.getPolicyHolderTypeid());

			String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
			String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
			String languageDesc = Optional.ofNullable(language).map(map -> map.get("itemDesc")).orElse("");
			String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc"))
					.orElse("");
			String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc"))
					.orElse("");

			// From List Item Value (Local)
			String genderLocal = Optional.ofNullable(gender).map(map -> map.get("itemDescLocal")).orElse("");
			String titleLocal = Optional.ofNullable(title).map(map -> map.get("itemDescLocal")).orElse("");
			String languageLocal = Optional.ofNullable(language).map(map -> map.get("itemDescLocal")).orElse("");
			String PolicyHolderTypeLocal = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDescLocal"))
					.orElse("");
			String policyHolderTypeIdLocal = Optional.ofNullable(policyHolderTypeId)
					.map(map -> map.get("itemDescLocal")).orElse("");

			// From Region_mater for state name local
			String stateNameLocal = "";
			List<RegionMaster> rgMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getCountry(),
					req.getStateCode());
			if (rgMaster != null && rgMaster.size() > 0) {
				stateNameLocal = rgMaster.get(0).getRegionNameLocal();
			}
			// From State_master for city name local
			String cityNameLocal = "";
			List<StateMaster> stMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(
					Integer.valueOf(StringUtils.isNotBlank(req.getCityCode()) ? req.getCityCode() : "0"),
					req.getCountry(), req.getStateCode());
			if (stMaster != null && stMaster.size() > 0) {
				cityNameLocal = stMaster.get(0).getStateNameLocal();
			}

			if (StringUtils.isNotBlank(req.getMobileCode1())) {
				Map<String, String> mobileCode1Desc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode1());
				String mobileCode1 = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode1Local = Optional.ofNullable(mobileCode1Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				saveData.setMobileCodeDesc1(mobileCode1);

			}
			if (StringUtils.isNotBlank(req.getMobileCode2())) {
				Map<String, String> mobileCode2Desc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode2());
				String mobileCode2 = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode2Local = Optional.ofNullable(mobileCode2Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				saveData.setMobileCodeDesc2(mobileCode2);

			}

			if (StringUtils.isNotBlank(req.getMobileCode3())) {
				Map<String, String> mobileCode3Desc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode3());
				String mobileCode3 = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode3Local = Optional.ofNullable(mobileCode3Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				saveData.setMobileCodeDesc3(mobileCode3);

			}
			if (StringUtils.isNotBlank(req.getWhatsappCode())) {
				Map<String, String> whatsappCodeDesc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getWhatsappCode());
				String whatsappCode = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDesc")).orElse("");
				// String whatsappCodeLocal = Optional.ofNullable(whatsappCodeDesc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				saveData.setWhatsappCodeDesc(whatsappCode);

			}
			String businessTypeLocal = "";
			if (StringUtils.isNotBlank(req.getBusinessType())) {
				Map<String, String> businessTypeDesc = getListItemLocal("99999",
						req.getBranchCode(), "BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																						// req.getBusinessType());
				String businessType = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDesc")).orElse("");
				businessTypeLocal = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDescLocal"))
						.orElse("");
				saveData.setBusinessTypeDesc(businessType);
			}
			String occupationDesc = "", occupationDescLocal = "";
			if (StringUtils.isNotBlank(req.getOccupation())) {
				Map<String, String> occupation = getByOccupationIdDesc(req.getOccupation(),
						req.getCompanyId(), req.getProductId(), req.getBranchCode());
				occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
				occupationDescLocal = Optional.ofNullable(occupation).map(map -> map.get("occupationNameLocal"))
						.orElse("");
			}

			/**
			 * Solution:- The issue ID Type blank for some customer to push TIRA ID Type &
			 * Policy Holder ID Type both are same represent Individual / Corporate Customer
			 * if ID Type is blank is take value from Policy Holder Type
			 */
			saveData.setPolicyHolderType(req.getPolicyHolderType());
			saveData.setPolicyHolderTypeid(req.getPolicyHolderTypeid());

			String idType = StringUtils.isBlank(req.getIdType()) && StringUtils.isNotBlank(req.getPolicyHolderType())
					? req.getPolicyHolderType()
					: req.getIdType();

			saveData.setIdType(idType);
			saveData.setIdNumber(req.getIdNumber());

			// Desc
			saveData.setTitleDesc(titleDesc);
			saveData.setGenderDesc(genderDesc);
			saveData.setLanguageDesc(languageDesc);
			saveData.setOccupationDesc(occupationDesc);
			saveData.setPolicyHolderTypeDesc(policyHolderTypeDesc);
			saveData.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
			saveData.setIdTypeDesc(policyHolderTypeIdDesc);
			saveData.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
			saveData.setAge(age);

			// local desc feilds
			saveData.setGenderDescLocal(genderLocal);
			saveData.setTitleDescLocal(titleLocal);
			saveData.setLanguageDescLocal(languageLocal);
			saveData.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
			saveData.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
			saveData.setOccupationDescLocal(occupationDescLocal);
			saveData.setStateNameLocal(stateNameLocal);
			saveData.setCityNameLocal(cityNameLocal);
			saveData.setMobileCodeDesc1Local(req.getMobileCode1());
			saveData.setMobileCodeDesc2Local(req.getMobileCode2());
			saveData.setMobileCodeDesc3Local(req.getMobileCode3());
			saveData.setWhatsappCodeDescLocal(req.getWhatsappCode());
			saveData.setIdTypeDescLocal(policyHolderTypeIdLocal);
			
			saveData.setCustomerType(req.getCustomerType() != null ? req.getCustomerType() : "");
			saveData.setComplianceStatus(req.getComplianceStatus() != null ? req.getComplianceStatus() : "");
			saveData.setComplianceStatusId(req.getComplianceStatusId() != null ? req.getComplianceStatusId() : "");
			saveData.setWealthSource(req.getWealthSource() != null ? req.getWealthSource() : "");
			saveData.setWealthSourceId(req.getWealthSourceId() != null ? req.getWealthSourceId() : "");
			saveData.setIndustryType(req.getIndustryType() != null ? req.getIndustryType() : "");
			saveData.setIndustryTypeId(req.getIndustryTypeId() != null ? req.getIndustryTypeId() : "");
			saveData.setOwners(req.getOwners() != null ? req.getOwners() : "");
			saveData.setOwnersId(req.getOwnersId() != null ? req.getOwnersId() : "");
			saveData.setLegalStructure(req.getLegalStructure() != null ? req.getLegalStructure() : "");
			saveData.setLegalStructureId(req.getLegalStructureId() != null ? req.getLegalStructureId() : "");
			

			// Kenya Rating Fields
			saveData.setMaritalStatus(StringUtils.isBlank(req.getMaritalStatus()) ? "Single" : req.getMaritalStatus());
			if (req.getLicenseIssuedDate() != null) {
				saveData.setLicenseIssuedDate(req.getLicenseIssuedDate());
				Date licenceIssued = req.getDobOrRegDate();
				Date today = new Date();
				int licenseDuration = today.getYear() - licenceIssued.getYear();
				saveData.setLicenseDuration(licenseDuration);

			} else {
				saveData.setLicenseIssuedDate(new Date());
				saveData.setLicenseDuration(20);
			}
			saveData.setLeadSeqNo(req.getLeadSeqNo()!=null ? req.getLeadSeqNo():0);
			saveData.setCustomerType(req.getCustomerType() != null ? req.getCustomerType() : "");
			repository.save(saveData);

			// Personal Info Update

			// Endorsement flow and B2C Flow
			// Type=B2C
			if (StringUtils.isNotBlank(req.getEndtCategDesc())) {
				if ("Non Financial".equalsIgnoreCase(req.getEndtCategDesc().toString())) {
					PersonalInfo savePersonalInfo = new PersonalInfo();
					HomePositionMaster homedata = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
					// PersonalInfo
					// personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
					dozerMapper.map(req, saveData);
					savePersonalInfo.setPinCode(req.getPinCode());
					savePersonalInfo.setCustomerId(homedata.getCustomerId());
					savePersonalInfo.setIdNumber(req.getIdNumber());
					savePersonalInfo.setCreatedBy(createdBy);
					savePersonalInfo.setUpdatedDate(new Date());
					savePersonalInfo.setUpdatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(custRefNo);
					savePersonalInfo.setAddress1(req.getAddress1());
					savePersonalInfo.setAddress2(req.getAddress2());
					savePersonalInfo.setAge(age);
					savePersonalInfo.setBranchCode(req.getBranchCode());
					savePersonalInfo.setBusinessType(req.getBusinessType());
					savePersonalInfo.setOtherOccupation(req.getOtherOccupation());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType = getListItem("99999", req.getBranchCode(),
								"BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																		// req.getBusinessType());
						savePersonalInfo.setBusinessTypeDesc(businessType);

					}

					savePersonalInfo.setRegionCode(req.getRegionCode());
					savePersonalInfo.setIsTaxExempted(
							StringUtils.isBlank(req.getIsTaxExempted()) ? "0" : req.getIsTaxExempted());
					savePersonalInfo.setCityCode(req.getCityCode());
					savePersonalInfo.setCityName(determineCityName(req));
					savePersonalInfo.setClientName(req.getClientName());
					savePersonalInfo.setClientStatus(req.getClientStatus());
					savePersonalInfo
							.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
					savePersonalInfo.setCompanyId(req.getCompanyId());
					savePersonalInfo.setCreatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
					savePersonalInfo.setDobOrRegDate(dob);
					savePersonalInfo.setEmail1(req.getEmail1());
					savePersonalInfo.setEmail2(req.getEmail2());
					savePersonalInfo.setEmail3(req.getEmail3());
					savePersonalInfo.setEndorsementDate(req.getEndorsementDate());
					savePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
					savePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
					savePersonalInfo.setEndorsementType(req.getEndorsementType());
					savePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
					savePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
					savePersonalInfo.setEndtCount(req.getEndtCount());
					savePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					savePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					savePersonalInfo.setEndtStatus(req.getEndtStatus());
					savePersonalInfo.setEntryDate(new Date());
					savePersonalInfo.setFax(req.getFax());
					savePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
					savePersonalInfo
							.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
					savePersonalInfo.setGenderDesc(genderDesc);
					savePersonalInfo.setTitleDesc(titleDesc);
					savePersonalInfo.setLanguageDesc(languageDesc);
					savePersonalInfo.setOccupationDesc(occupationDesc);

					// Induvidual / Corporate
					savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
					savePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);

					// Possport or etc
					savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
					savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
					savePersonalInfo.setIdTypeDesc(policyHolderTypeIdDesc);

					savePersonalInfo.setMobileCode1(req.getMobileCode1());
					savePersonalInfo.setMobileCode2(req.getMobileCode2() == null ? "" : req.getMobileCode2());
					savePersonalInfo.setMobileCode3(req.getMobileCode3() == null ? "" : req.getMobileCode3());
					savePersonalInfo.setMobileNo1(req.getMobileNo1());
					savePersonalInfo.setMobileNo2(req.getMobileNo2());
					savePersonalInfo.setMobileNo3(req.getMobileNo3());
					savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode1(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode2(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode3(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(), req.getCompanyId());
						savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					savePersonalInfo.setRegionCode(req.getRegionCode());
					savePersonalInfo.setStateCode(req.getStateCode());
					savePersonalInfo.setStateName(determineStateName(req));
					savePersonalInfo.setStatus(req.getStatus());
					savePersonalInfo.setNationality(req.getNationality());
					savePersonalInfo.setVrTinNo(req.getVrTinNo());
					savePersonalInfo.setVrnGst(req.getVrTinNo());

					// local desc
					savePersonalInfo.setTitleDescLocal(titleLocal);
					savePersonalInfo.setGenderDescLocal(genderLocal);
					savePersonalInfo.setOccupationDescLocal(occupationDescLocal);
					savePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
					savePersonalInfo.setStateNameLocal(stateNameLocal);
					savePersonalInfo.setCityNameLocal(cityNameLocal);
					savePersonalInfo.setIdTypeDescLocal(policyHolderTypeIdLocal);
					savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setLanguageDescLocal(languageLocal);
					savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
					savePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
					savePersonalInfo.setActivities(req.getActivities());
					savePersonalInfo.setCustomerAsInsurer(req.getCustomerAsInsurer());
					savePersonalInfo.setHouseNo(req.getHouseNo());
					savePersonalInfo.setCode(req.getCode());
					personalInforepo.save(savePersonalInfo);
				}
			} else if (StringUtils.isNotBlank(req.getType())) {
				if("b2c".equalsIgnoreCase(req.getType().toString()) && req.getQuoteNo() != null && !req.getQuoteNo().isEmpty()) {
				HomePositionMaster homedata = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				if ("b2c".equalsIgnoreCase(req.getType().toString()) && homedata != null) {
					PersonalInfo savePersonalInfo = new PersonalInfo();

					// PersonalInfo
					// personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
					dozerMapper.map(req, saveData);
					savePersonalInfo.setPinCode(req.getPinCode());
					savePersonalInfo.setCustomerId(homedata.getCustomerId());
					savePersonalInfo.setIdNumber(req.getIdNumber());
					savePersonalInfo.setCreatedBy(createdBy);
					savePersonalInfo.setUpdatedDate(new Date());
					savePersonalInfo.setUpdatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(custRefNo);
					savePersonalInfo.setAddress1(req.getAddress1());
					savePersonalInfo.setAddress2(req.getAddress2());
					savePersonalInfo.setAge(age);
					savePersonalInfo.setBranchCode(req.getBranchCode());
					savePersonalInfo.setBusinessType(req.getBusinessType());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType = getListItem("99999", req.getBranchCode(),
								"BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																		// req.getBusinessType());
						savePersonalInfo.setBusinessTypeDesc(businessType);
					}

					savePersonalInfo.setIsTaxExempted(
							StringUtils.isBlank(req.getIsTaxExempted()) ? "0" : req.getIsTaxExempted());
					savePersonalInfo.setCityCode(req.getCityCode());
					savePersonalInfo.setCityName(determineCityName(req));
					savePersonalInfo.setClientName(req.getClientName());
					savePersonalInfo.setClientStatus(req.getClientStatus());
					savePersonalInfo
							.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
					savePersonalInfo.setCompanyId(req.getCompanyId());
					savePersonalInfo.setCreatedBy(req.getCreatedBy());
					savePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
					savePersonalInfo.setDobOrRegDate(req.getDobOrRegDate());
					savePersonalInfo.setEmail1(req.getEmail1());
					savePersonalInfo.setEmail2(req.getEmail2());
					savePersonalInfo.setEmail3(req.getEmail3());
					savePersonalInfo.setEndorsementDate(req.getEndorsementDate());
					savePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
					savePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
					savePersonalInfo.setEndorsementType(req.getEndorsementType());
					savePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
					savePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
					savePersonalInfo.setEndtCount(req.getEndtCount());
					savePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					savePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					savePersonalInfo.setEndtStatus(req.getEndtStatus());
					savePersonalInfo.setEntryDate(new Date());
					savePersonalInfo.setFax(req.getFax());
					savePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
					savePersonalInfo
							.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
					savePersonalInfo.setGenderDesc(genderDesc);
					savePersonalInfo.setTitle(req.getTitle());
					savePersonalInfo.setTitleDesc(titleDesc);
					savePersonalInfo.setLanguageDesc(languageDesc);
					savePersonalInfo.setOccupationDesc(occupationDesc);
					savePersonalInfo.setIdType(req.getIdType());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);

					// Induvidual / Corporateipconfi
					savePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
					savePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);

					// Possport or etc
					savePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
					savePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
					savePersonalInfo.setIdType(req.getPolicyHolderTypeid());
					savePersonalInfo.setIdTypeDesc(policyHolderTypeDesc);

					savePersonalInfo.setMobileCode1(req.getMobileCode1());
					savePersonalInfo.setMobileCode2(req.getMobileCode2() == null ? "" : req.getMobileCode2());
					savePersonalInfo.setMobileCode3(req.getMobileCode3() == null ? "" : req.getMobileCode3());
					savePersonalInfo.setMobileNo1(req.getMobileNo1());
					savePersonalInfo.setMobileNo2(req.getMobileNo2());
					savePersonalInfo.setMobileNo3(req.getMobileNo3());
					savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode1(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode2(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode3(), req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(), req.getCompanyId());
						savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					savePersonalInfo.setRegionCode(req.getRegionCode());
					savePersonalInfo.setStateCode(req.getStateCode());
					savePersonalInfo.setStateName(determineStateName(req));
					savePersonalInfo.setStatus(req.getStatus());
					savePersonalInfo.setNationality(req.getNationality());
					savePersonalInfo.setVrTinNo(req.getVrTinNo());
					savePersonalInfo.setVrnGst(req.getVrTinNo());

					// local desc
					savePersonalInfo.setTitleDescLocal(titleLocal);
					savePersonalInfo.setGenderDescLocal(genderLocal);
					savePersonalInfo.setOccupationDescLocal(occupationDescLocal);
					savePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
					savePersonalInfo.setStateNameLocal(stateNameLocal);
					savePersonalInfo.setCityNameLocal(cityNameLocal);
					savePersonalInfo.setIdTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setLanguageDescLocal(languageLocal);
					savePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					savePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
					savePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
					savePersonalInfo.setActivities(req.getActivities());
					savePersonalInfo.setCustomerAsInsurer(req.getCustomerAsInsurer());
					savePersonalInfo.setHouseNo(req.getHouseNo());
					savePersonalInfo.setCode(req.getCode());

					personalInforepo.save(savePersonalInfo);
				}
				}
			}
			// Response

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

	}

	public CustomerDetailsGetRes getCustomerDetails(GetCustomerDetailsReq req) {
		CustomerDetailsGetRes res = new CustomerDetailsGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceCustomerDetails> data = repository
					.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo());
			if (data != null && data.size() > 0) {
				EserviceCustomerDetails cdate = data.get(0);
				res = dozerMapper.map(cdate, CustomerDetailsGetRes.class);

				res.setTitle(cdate.getTitle() == null ? "" : cdate.getTitle());
				res.setFirstName(cdate.getClientName() == null ? "" : cdate.getClientName());
				res.setBusinessType(cdate.getBusinessType() == null ? "" : cdate.getBusinessType());
				res.setGender(cdate.getGender() == null ? "" : cdate.getGender());
				res.setOccupation(cdate.getOccupation() == null ? "" : cdate.getOccupation());
				res.setOtherOccupation(cdate.getOtherOccupation() == null ? "" : cdate.getOtherOccupation());
				res.setEmail1(cdate.getEmail1() == null ? "" : cdate.getEmail1());
				res.setWhatsappCode(cdate.getWhatsappCode() == null ? "" : cdate.getWhatsappCode());
				res.setMobileCode1(cdate.getMobileCode1() == null ? "" : cdate.getMobileCode1());
				res.setMobileCode2(cdate.getMobileCode2() == null ? "" : cdate.getMobileCode2());
				res.setMobileCode3(cdate.getMobileCode3() == null ? "" : cdate.getMobileCode3());
				res.setMobileCodeDesc1(cdate.getMobileCodeDesc1() == null ? "" : cdate.getMobileCodeDesc1());
				res.setMobileCodeDesc2(cdate.getMobileCodeDesc2() == null ? "" : cdate.getMobileCodeDesc2());
				res.setMobileCodeDesc3(cdate.getMobileCodeDesc3() == null ? "" : cdate.getMobileCodeDesc3());
				res.setMobileNo1(cdate.getMobileNo1() == null ? "" : cdate.getMobileNo1());
				res.setMobileNo2(cdate.getMobileNo2() == null ? "" : cdate.getMobileNo2());
				res.setMobileNo3(cdate.getMobileNo3() == null ? "" : cdate.getMobileNo3());
				res.setActivities(cdate.getActivities() == null ? "" : cdate.getActivities());
				res.setIdType(cdate.getIdType() == null ? "" : cdate.getIdType());
				res.setIdNumber(cdate.getIdNumber() == null ? "" : cdate.getIdNumber());
				res.setIsTaxExempted(cdate.getIsTaxExempted() == null ? "" : cdate.getIsTaxExempted());
				res.setPreferredNotification(
						cdate.getPreferredNotification() == null ? "" : cdate.getPreferredNotification());
				res.setStatus(cdate.getStatus() == null ? "" : cdate.getStatus());

				res.setCountry(cdate.getCountry() == null ? "" : cdate.getCountry());
				res.setCityCode(cdate.getCityCode() == null ? "" : cdate.getCityCode().toString());
				res.setCityName(cdate.getCityName() == null ? "" : cdate.getCityName());
				res.setStateName(cdate.getStateName() == null ? "" : cdate.getStateName());
				res.setPinCode(cdate.getPinCode() == null ? "" : cdate.getPinCode());
				res.setRegionCode(cdate.getRegionCode() == null ? "" : cdate.getRegionCode());
				res.setPolicyHolderTypeid(cdate.getPolicyHolderTypeid() == null ? "" : cdate.getPolicyHolderTypeid());
				res.setVrTinNo(cdate.getVrTinNo() == null ? "" : cdate.getVrTinNo());
				res.setAreaLocality(cdate.getAreaLocality());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	/**
	 * This method returns the state name if it's provided. If not, it tries to find
	 * the state name using the country, and region code.
	 */
	private String determineStateName(EserviceCustomerSaveReq req) {

		if (StringUtils.isNotBlank(req.getStateName())) {
			return req.getStateName();
		}

		else if (StringUtils.isBlank(req.getStateName()) && StringUtils.isNotBlank(req.getStateCode())) {
			List<RegionMaster> regionMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getCountry(),
					req.getRegionCode());
			if (regionMaster.size() == 1) {
				return regionMaster.get(0).getRegionName();
			}
		}

		return null;
	}

	/**
	 * This method returns the city name if it's provided. If not, it looks up the
	 * city name using the city code, country, and state code.
	 */
	private String determineCityName(EserviceCustomerSaveReq req) {
		if (StringUtils.isNotBlank(req.getCityName())) {
			return req.getCityName();
		}

		else if (StringUtils.isBlank(req.getCityName()) && StringUtils.isNotBlank(req.getCityCode())) {
			List<StateMaster> stateMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(
					Integer.valueOf(req.getCityCode()), req.getCountry(), req.getStateCode());

			if (stateMaster.size() == 1) {
				return stateMaster.get(0).getStateName();
			}
		}

		return null;
	}

	public SuccessRes updateCustomerDetail(EserviceCustomerSaveReq req) {
		SuccessRes res = new SuccessRes();
		String custRefNo = "";
		Integer productId;
		Date entryDate = null;
		String createdBy = req.getCreatedBy();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		if (StringUtils.isNoneBlank(req.getCustomerReferenceNo())) {
			custRefNo = req.getCustomerReferenceNo();
			EserviceCustomerDetails updateCustomerData = repository
					.findByCustomerReferenceNo(req.getCustomerReferenceNo());
			dozerMapper.map(req, updateCustomerData);
			productId = Integer.valueOf(req.getProductId());
			updateCustomerData.setProductId(productId);
			updateCustomerData.setEntryDate(entryDate);
			updateCustomerData.setCreatedBy(createdBy);
			updateCustomerData.setUpdatedDate(new Date());
			updateCustomerData.setUpdatedBy(req.getCreatedBy());
			updateCustomerData.setCustomerReferenceNo(custRefNo);
			updateCustomerData.setPolCustCode(updateCustomerData.getPolCustCode());
//			updateCustomerData.setCcCustCatgCode(updateCustomerData.getCcCustCatgCode());
//			
//				updateCustomerData.setCcCustType(updateCustomerData.getCcCustType());

			updateCustomerData.setZone(StringUtils.isBlank(req.getZone()) ? 0 : Integer.valueOf(req.getZone()));

			updateCustomerData.setBrokerBranchCode(req.getBrokerBranchCode());

			updateCustomerData.setTitle(req.getTitle());
			updateCustomerData.setFirstName(req.getClientName());
			updateCustomerData.setBusinessType(req.getBusinessType());
			updateCustomerData.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
			updateCustomerData.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
			updateCustomerData.setOtherOccupation(req.getOtherOccupation());
			updateCustomerData.setEmail1(req.getEmail1());
			updateCustomerData.setWhatsappCode(req.getWhatsappCode());
			updateCustomerData.setMobileCode1(req.getMobileCode1());
			updateCustomerData.setMobileCode2(req.getMobileCode2() == null ? "" : req.getMobileCode2());
			updateCustomerData.setMobileCode3(req.getMobileCode3() == null ? "" : req.getMobileCode3());
			updateCustomerData.setMobileNo1(req.getMobileNo1());
			updateCustomerData.setMobileNo2(req.getMobileNo2());
			updateCustomerData.setMobileNo3(req.getMobileNo3());
			updateCustomerData.setActivities(req.getActivities());

			updateCustomerData
					.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted()) ? "0" : req.getIsTaxExempted());
			updateCustomerData.setPreferredNotification(req.getPreferredNotification());
			updateCustomerData.setStatus(req.getStatus());

			updateCustomerData.setCountry(req.getCountry());
			updateCustomerData.setCountryName(req.getCountryName());
			updateCustomerData
					.setCityCode(StringUtils.isBlank(req.getCityCode()) ? null : Integer.valueOf(req.getCityCode()));
			updateCustomerData.setCityName(determineCityName(req));
			updateCustomerData.setPinCode(req.getPinCode());
			updateCustomerData.setRegionCode(req.getRegionCode());
			updateCustomerData.setStateName(determineStateName(req));

			updateCustomerData.setVrTinNo(req.getVrTinNo());
			// saveData.setVrnGst(req.getVrTinNo());

			// Age Calculation
			int age = 0;
			Date dob = null;
			if (req.getDobOrRegDate() != null) {
				dob = req.getDobOrRegDate();
				Date today = new Date();
				age = today.getYear() - dob.getYear();
			}
			// From List Item Value

			Map<String, String> title = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "NAME_TITLE", req.getTitle());// listRepo.findByItemTypeAndItemCode("NAME_TITLE",
																		// req.getTitle());
			Map<String, String> gender = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "GENDER", req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER",
																	// saveData.getGender());
			Map<String, String> language = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "LANGUAGE", req.getLanguage());// listRepo.findByItemTypeAndItemCode("LANGUAGE",
																		// req.getLanguage());
			Map<String, String> policyHolderType = getListItemLocal("99999",
					req.getBranchCode(), "POLICY_HOLDER_TYPE", req.getPolicyHolderType());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",
																							// req.getPolicyHolderType());
			Map<String, String> policyHolderTypeId = getListItemLocal(req.getCompanyId(),
					req.getBranchCode(), "POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE",
																								// req.getPolicyHolderTypeid());

			String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
			String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
			String languageDesc = Optional.ofNullable(language).map(map -> map.get("itemDesc")).orElse("");
			String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc"))
					.orElse("");
			String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc"))
					.orElse("");

			// From List Item Value (Local)
			String genderLocal = Optional.ofNullable(gender).map(map -> map.get("itemDescLocal")).orElse("");
			String titleLocal = Optional.ofNullable(title).map(map -> map.get("itemDescLocal")).orElse("");
			String languageLocal = Optional.ofNullable(language).map(map -> map.get("itemDescLocal")).orElse("");
			String PolicyHolderTypeLocal = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDescLocal"))
					.orElse("");
			String policyHolderTypeIdLocal = Optional.ofNullable(policyHolderTypeId)
					.map(map -> map.get("itemDescLocal")).orElse("");

			// From Region_mater for state name local
			String stateNameLocal = "";
			List<RegionMaster> rgMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getCountry(),
					req.getStateCode());
			if (rgMaster != null && rgMaster.size() > 0) {
				stateNameLocal = rgMaster.get(0).getRegionNameLocal();
			}
			// From State_master for city name local
			String cityNameLocal = "";
			List<StateMaster> stMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(
					Integer.valueOf(StringUtils.isNotBlank(req.getCityCode()) ? req.getCityCode() : "0"),
					req.getCountry(), req.getStateCode());
			if (stMaster != null && stMaster.size() > 0) {
				cityNameLocal = stMaster.get(0).getStateNameLocal();
			}

			if (StringUtils.isNotBlank(req.getMobileCode1())) {
				Map<String, String> mobileCode1Desc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode1());
				String mobileCode1 = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode1Local = Optional.ofNullable(mobileCode1Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				updateCustomerData.setMobileCodeDesc1(mobileCode1);

			}
			if (StringUtils.isNotBlank(req.getMobileCode2())) {
				Map<String, String> mobileCode2Desc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode2());
				String mobileCode2 = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode2Local = Optional.ofNullable(mobileCode2Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				updateCustomerData.setMobileCodeDesc2(mobileCode2);

			}

			if (StringUtils.isNotBlank(req.getMobileCode3())) {
				Map<String, String> mobileCode3Desc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getMobileCode3());
				String mobileCode3 = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDesc")).orElse("");
				// String mobileCode3Local = Optional.ofNullable(mobileCode3Desc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				updateCustomerData.setMobileCodeDesc3(mobileCode3);

			}
			if (StringUtils.isNotBlank(req.getWhatsappCode())) {
				Map<String, String> whatsappCodeDesc = getListItemLocal(req.getCompanyId(),
						req.getBranchCode(), "MOBILE_CODE", req.getWhatsappCode());
				String whatsappCode = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDesc")).orElse("");
				// String whatsappCodeLocal = Optional.ofNullable(whatsappCodeDesc).map(map ->
				// map.get("itemDescLocal")).orElse("");
				updateCustomerData.setWhatsappCodeDesc(whatsappCode);

			}
			String businessTypeLocal = "";
			if (StringUtils.isNotBlank(req.getBusinessType())) {
				Map<String, String> businessTypeDesc = getListItemLocal("99999",
						req.getBranchCode(), "BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																						// req.getBusinessType());
				String businessType = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDesc")).orElse("");
				businessTypeLocal = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDescLocal"))
						.orElse("");
				updateCustomerData.setBusinessTypeDesc(businessType);
			}
			String occupationDesc = "", occupationDescLocal = "";
			if (StringUtils.isNotBlank(req.getOccupation())) {
				Map<String, String> occupation = getByOccupationIdDesc(req.getOccupation(),
						req.getCompanyId(), req.getProductId(), req.getBranchCode());
				occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
				occupationDescLocal = Optional.ofNullable(occupation).map(map -> map.get("occupationNameLocal"))
						.orElse("");
			}

			/**
			 * Solution:- The issue ID Type blank for some customer to push TIRA ID Type &
			 * Policy Holder ID Type both are same represent Individual / Corporate Customer
			 * if ID Type is blank is take value from Policy Holder Type
			 */
			updateCustomerData.setPolicyHolderType(req.getPolicyHolderType());
			updateCustomerData.setPolicyHolderTypeid(req.getPolicyHolderTypeid());

			String idType = StringUtils.isBlank(req.getIdType()) && StringUtils.isNotBlank(req.getPolicyHolderType())
					? req.getPolicyHolderType()
					: req.getIdType();

			updateCustomerData.setIdType(idType);
			updateCustomerData.setIdNumber(req.getIdNumber());

			// Desc
			updateCustomerData.setTitleDesc(titleDesc);
			updateCustomerData.setGenderDesc(genderDesc);
			updateCustomerData.setLanguageDesc(languageDesc);
			updateCustomerData.setOccupationDesc(occupationDesc);
			updateCustomerData.setPolicyHolderTypeDesc(policyHolderTypeDesc);
			updateCustomerData.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
			updateCustomerData.setIdTypeDesc(policyHolderTypeIdDesc);
			updateCustomerData.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
			updateCustomerData.setAge(age);

			// local desc feilds
			updateCustomerData.setGenderDescLocal(genderLocal);
			updateCustomerData.setTitleDescLocal(titleLocal);
			updateCustomerData.setLanguageDescLocal(languageLocal);
			updateCustomerData.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
			updateCustomerData.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
			updateCustomerData.setOccupationDescLocal(occupationDescLocal);
			updateCustomerData.setStateNameLocal(stateNameLocal);
			updateCustomerData.setCityNameLocal(cityNameLocal);
			updateCustomerData.setMobileCodeDesc1Local(req.getMobileCode1());
			updateCustomerData.setMobileCodeDesc2Local(req.getMobileCode2());
			updateCustomerData.setMobileCodeDesc3Local(req.getMobileCode3());
			updateCustomerData.setWhatsappCodeDescLocal(req.getWhatsappCode());
			updateCustomerData.setIdTypeDescLocal(policyHolderTypeIdLocal);

			// Kenya Rating Fields
			updateCustomerData
					.setMaritalStatus(StringUtils.isBlank(req.getMaritalStatus()) ? "Single" : req.getMaritalStatus());
			if (req.getLicenseIssuedDate() != null) {
				updateCustomerData.setLicenseIssuedDate(req.getLicenseIssuedDate());
				Date licenceIssued = req.getDobOrRegDate();
				Date today = new Date();
				int licenseDuration = today.getYear() - licenceIssued.getYear();
				updateCustomerData.setLicenseDuration(licenseDuration);

			} else {
				updateCustomerData.setLicenseIssuedDate(new Date());
				updateCustomerData.setLicenseDuration(20);
			}
			updateCustomerData.setLeadSeqNo(req.getLeadSeqNo());
			repository.save(updateCustomerData);

			// Personal Info Update

			// Endorsement flow and B2C Flow
			// Type=B2C
			if (StringUtils.isNotBlank(req.getEndtCategDesc())) {
				if ("Non Financial".equalsIgnoreCase(req.getEndtCategDesc().toString())) {
					List<PersonalInfo> personalInfoList = personalInforepo.findByCustomerReferenceNo(custRefNo);
					PersonalInfo updatePersonalInfo = personalInfoList.get(0);
					HomePositionMaster homedata = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
					// PersonalInfo
					// personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
					dozerMapper.map(req, updateCustomerData);
					updatePersonalInfo.setPinCode(req.getPinCode());
					updatePersonalInfo.setCustomerId(homedata.getCustomerId());
					updatePersonalInfo.setIdNumber(req.getIdNumber());
					updatePersonalInfo.setCreatedBy(createdBy);
					updatePersonalInfo.setUpdatedDate(new Date());
					updatePersonalInfo.setUpdatedBy(req.getCreatedBy());
					updatePersonalInfo.setCustomerReferenceNo(custRefNo);
					updatePersonalInfo.setAddress1(req.getAddress1());
					updatePersonalInfo.setAddress2(req.getAddress2());
					updatePersonalInfo.setAge(age);
					updatePersonalInfo.setBranchCode(req.getBranchCode());
					updatePersonalInfo.setBusinessType(req.getBusinessType());
					updatePersonalInfo.setOtherOccupation(req.getOtherOccupation());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType = getListItem("99999", req.getBranchCode(),
								"BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																		// req.getBusinessType());
						updatePersonalInfo.setBusinessTypeDesc(businessType);

					}

					updatePersonalInfo.setRegionCode(req.getRegionCode());
					updatePersonalInfo.setIsTaxExempted(
							StringUtils.isBlank(req.getIsTaxExempted()) ? "0" : req.getIsTaxExempted());
					updatePersonalInfo.setCityCode(req.getCityCode());
					updatePersonalInfo.setCityName(determineCityName(req));
					updatePersonalInfo.setClientName(req.getClientName());
					updatePersonalInfo.setClientStatus(req.getClientStatus());
					updatePersonalInfo
							.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
					updatePersonalInfo.setCompanyId(req.getCompanyId());
					updatePersonalInfo.setCreatedBy(req.getCreatedBy());
					updatePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
					updatePersonalInfo.setDobOrRegDate(dob);
					updatePersonalInfo.setEmail1(req.getEmail1());
					updatePersonalInfo.setEmail2(req.getEmail2());
					updatePersonalInfo.setEmail3(req.getEmail3());
					updatePersonalInfo.setEndorsementDate(req.getEndorsementDate());
					updatePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
					updatePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
					updatePersonalInfo.setEndorsementType(req.getEndorsementType());
					updatePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
					updatePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
					updatePersonalInfo.setEndtCount(req.getEndtCount());
					updatePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					updatePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					updatePersonalInfo.setEndtStatus(req.getEndtStatus());
					updatePersonalInfo.setEntryDate(new Date());
					updatePersonalInfo.setFax(req.getFax());
					updatePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
					updatePersonalInfo
							.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
					updatePersonalInfo.setGenderDesc(genderDesc);
					updatePersonalInfo.setTitleDesc(titleDesc);
					updatePersonalInfo.setLanguageDesc(languageDesc);
					updatePersonalInfo.setOccupationDesc(occupationDesc);

					// Induvidual / Corporate
					updatePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
					updatePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);

					// Possport or etc
					updatePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
					updatePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
					updatePersonalInfo.setIdType(req.getPolicyHolderTypeid());
					updatePersonalInfo.setIdTypeDesc(policyHolderTypeIdDesc);

					updatePersonalInfo.setMobileCode1(req.getMobileCode1());
					updatePersonalInfo.setMobileCode2(req.getMobileCode2() == null ? "" : req.getMobileCode2());
					updatePersonalInfo.setMobileCode3(req.getMobileCode3() == null ? "" : req.getMobileCode3());
					updatePersonalInfo.setMobileNo1(req.getMobileNo1());
					updatePersonalInfo.setMobileNo2(req.getMobileNo2());
					updatePersonalInfo.setMobileNo3(req.getMobileNo3());
					updatePersonalInfo.setWhatsappCode(req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode1(), req.getCompanyId());
						updatePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode2(), req.getCompanyId());
						updatePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode3(), req.getCompanyId());
						updatePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(), req.getCompanyId());
						updatePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					updatePersonalInfo.setRegionCode(req.getRegionCode());
					updatePersonalInfo.setStateCode(req.getStateCode());
					updatePersonalInfo.setStateName(determineStateName(req));
					updatePersonalInfo.setStatus(req.getStatus());
					updatePersonalInfo.setNationality(req.getNationality());
					updatePersonalInfo.setVrTinNo(req.getVrTinNo());
					updatePersonalInfo.setVrnGst(req.getVrTinNo());

					// local desc
					updatePersonalInfo.setTitleDescLocal(titleLocal);
					updatePersonalInfo.setGenderDescLocal(genderLocal);
					updatePersonalInfo.setOccupationDescLocal(occupationDescLocal);
					updatePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
					updatePersonalInfo.setStateNameLocal(stateNameLocal);
					updatePersonalInfo.setCityNameLocal(cityNameLocal);
					updatePersonalInfo.setIdTypeDescLocal(policyHolderTypeIdLocal);
					updatePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					updatePersonalInfo.setLanguageDescLocal(languageLocal);
					updatePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					updatePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
					updatePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
					updatePersonalInfo.setActivities(req.getActivities());
					updatePersonalInfo.setCustomerAsInsurer(req.getCustomerAsInsurer());

					personalInforepo.save(updatePersonalInfo);
				}
			} else if (StringUtils.isNotBlank(req.getType())) {
				HomePositionMaster homedata = homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				if ("b2c".equalsIgnoreCase(req.getType().toString()) && homedata != null) {
					List<PersonalInfo> personalInfoList = personalInforepo.findByCustomerReferenceNo(custRefNo);
					PersonalInfo updatePersonalInfo = personalInfoList.get(0);
					// PersonalInfo
					// personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
					dozerMapper.map(req, updateCustomerData);
					updatePersonalInfo.setPinCode(req.getPinCode());
					updatePersonalInfo.setCustomerId(homedata.getCustomerId());
					updatePersonalInfo.setIdNumber(req.getIdNumber());
					updatePersonalInfo.setCreatedBy(createdBy);
					updatePersonalInfo.setUpdatedDate(new Date());
					updatePersonalInfo.setUpdatedBy(req.getCreatedBy());
					updatePersonalInfo.setCustomerReferenceNo(custRefNo);
					updatePersonalInfo.setAddress1(req.getAddress1());
					updatePersonalInfo.setAddress2(req.getAddress2());
					updatePersonalInfo.setAge(age);
					updatePersonalInfo.setBranchCode(req.getBranchCode());
					updatePersonalInfo.setBusinessType(req.getBusinessType());
					if (StringUtils.isNotBlank(req.getBusinessType())) {
						String businessType = getListItem("99999", req.getBranchCode(),
								"BUSINESS_TYPE", req.getBusinessType());// listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE",
																		// req.getBusinessType());
						updatePersonalInfo.setBusinessTypeDesc(businessType);
					}

					updatePersonalInfo.setIsTaxExempted(
							StringUtils.isBlank(req.getIsTaxExempted()) ? "0" : req.getIsTaxExempted());
					updatePersonalInfo.setCityCode(req.getCityCode());
					updatePersonalInfo.setCityName(determineCityName(req));
					updatePersonalInfo.setClientName(req.getClientName());
					updatePersonalInfo.setClientStatus(req.getClientStatus());
					updatePersonalInfo
							.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
					updatePersonalInfo.setCompanyId(req.getCompanyId());
					updatePersonalInfo.setCreatedBy(req.getCreatedBy());
					updatePersonalInfo.setCustomerReferenceNo(req.getCustomerReferenceNo());
					updatePersonalInfo.setDobOrRegDate(req.getDobOrRegDate());
					updatePersonalInfo.setEmail1(req.getEmail1());
					updatePersonalInfo.setEmail2(req.getEmail2());
					updatePersonalInfo.setEmail3(req.getEmail3());
					updatePersonalInfo.setEndorsementDate(req.getEndorsementDate());
					updatePersonalInfo.setEndorsementEffdate(req.getEndorsementEffdate());
					updatePersonalInfo.setEndorsementRemarks(req.getEndorsementRemarks());
					updatePersonalInfo.setEndorsementType(req.getEndorsementType());
					updatePersonalInfo.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
					updatePersonalInfo.setEndtCategDesc(req.getEndtCategDesc());
					updatePersonalInfo.setEndtCount(req.getEndtCount());
					updatePersonalInfo.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
					updatePersonalInfo.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
					updatePersonalInfo.setEndtStatus(req.getEndtStatus());
					updatePersonalInfo.setEntryDate(new Date());
					updatePersonalInfo.setFax(req.getFax());
					updatePersonalInfo.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
					updatePersonalInfo
							.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
					updatePersonalInfo.setGenderDesc(genderDesc);
					updatePersonalInfo.setTitle(req.getTitle());
					updatePersonalInfo.setTitleDesc(titleDesc);
					updatePersonalInfo.setLanguageDesc(languageDesc);
					updatePersonalInfo.setOccupationDesc(occupationDesc);
					updatePersonalInfo.setIdType(req.getIdType());
					updatePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);

					// Induvidual / Corporateipconfi
					updatePersonalInfo.setPolicyHolderType(req.getPolicyHolderType());
					updatePersonalInfo.setPolicyHolderTypeDesc(policyHolderTypeDesc);

					// Possport or etc
					updatePersonalInfo.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
					updatePersonalInfo.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
					updatePersonalInfo.setIdType(req.getPolicyHolderTypeid());
					updatePersonalInfo.setIdTypeDesc(policyHolderTypeDesc);

					updatePersonalInfo.setMobileCode1(req.getMobileCode1());
					updatePersonalInfo.setMobileCode2(req.getMobileCode2() == null ? "" : req.getMobileCode2());
					updatePersonalInfo.setMobileCode3(req.getMobileCode3() == null ? "" : req.getMobileCode3());
					updatePersonalInfo.setMobileNo1(req.getMobileNo1());
					updatePersonalInfo.setMobileNo2(req.getMobileNo2());
					updatePersonalInfo.setMobileNo3(req.getMobileNo3());
					updatePersonalInfo.setWhatsappCode(req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode1(), req.getCompanyId());
						updatePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode2(), req.getCompanyId());
						updatePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getMobileCode3(), req.getCompanyId());
						updatePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(), req.getCompanyId());
						updatePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					updatePersonalInfo.setRegionCode(req.getRegionCode());
					updatePersonalInfo.setStateCode(req.getStateCode());
					updatePersonalInfo.setStateName(determineStateName(req));
					updatePersonalInfo.setStatus(req.getStatus());
					updatePersonalInfo.setNationality(req.getNationality());
					updatePersonalInfo.setVrTinNo(req.getVrTinNo());
					updatePersonalInfo.setVrnGst(req.getVrTinNo());

					// local desc
					updatePersonalInfo.setTitleDescLocal(titleLocal);
					updatePersonalInfo.setGenderDescLocal(genderLocal);
					updatePersonalInfo.setOccupationDescLocal(occupationDescLocal);
					updatePersonalInfo.setBusinessTypeDescLocal(businessTypeLocal);
					updatePersonalInfo.setStateNameLocal(stateNameLocal);
					updatePersonalInfo.setCityNameLocal(cityNameLocal);
					updatePersonalInfo.setIdTypeDescLocal(PolicyHolderTypeLocal);
					updatePersonalInfo.setLanguageDescLocal(languageLocal);
					updatePersonalInfo.setPolicyHolderTypeDescLocal(PolicyHolderTypeLocal);
					updatePersonalInfo.setPolicyHolderTypeIdDescLocal(policyHolderTypeIdLocal);
					updatePersonalInfo.setSocioProfessionalCategory(req.getSocioProfessionalCategory());
					updatePersonalInfo.setActivities(req.getActivities());
					updatePersonalInfo.setCustomerAsInsurer(req.getCustomerAsInsurer());

					personalInforepo.save(updatePersonalInfo);
				}
			}
			res.setResponse("Customer already Exist Updated Successfully");
			res.setSuccessId(custRefNo);
		}
		return res;
	}
	
	public synchronized Map<String,String> getListItemLocal(String insuranceId , String branchCode, String itemType, String itemCode) {
		Map<String,String> itemDesc = new HashMap<String,String>() ;
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));


			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			//Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n1,n2,n3,n4,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemDesc.put("itemDesc",list.size() > 0 ? list.get(0).getItemValue() : "" );
			itemDesc.put("itemDescLocal",list.size() > 0 ? list.get(0).getItemValueLocal() : "" );
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc ;
	}
	
	public Map<String,String> getByOccupationIdDesc(String occupationId, String insuranceId, String productId , String branchCode) {
		Map<String,String> occupationDesc = new HashMap<String,String>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<OccupationMaster> query=  cb.createQuery(OccupationMaster.class);
			List<OccupationMaster> list = new ArrayList<OccupationMaster>();

			// Find All
			Root<OccupationMaster> c = query.from(OccupationMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<OccupationMaster> ocpm1 = effectiveDate.from(OccupationMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("occupationId"),ocpm1.get("occupationId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a9 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a5,a6,a9);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<OccupationMaster> ocpm2 = effectiveDate2.from(OccupationMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("occupationId"),ocpm2.get("occupationId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate a10 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			effectiveDate2.where(a3,a4,a7,a8,a10);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			Predicate n5 = cb.equal(c.get("branchCode"),branchCode);
			Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n8 = cb.equal(c.get("occupationId"),occupationId);
			Predicate n9 = cb.equal(c.get("productId"),productId );
			Predicate n10 = cb.equal(c.get("productId"),"99999" );
			Predicate n11 =  cb.or(n9, n10);
			query.where(n1,n2,n3,n4,n7,n8,n11).orderBy(orderList);
			TypedQuery<OccupationMaster> result = em.createQuery(query);
			list = result.getResultList();

			if(list.size()>0) {
				list = result.getResultList();
				list.sort(Comparator.comparing(OccupationMaster::getOccupationName));
				occupationDesc.put("occupationName" , list.size() > 0 ? list.get(0).getOccupationName() : "");
				occupationDesc.put("occupationNameLocal" , list.size() > 0 ? list.get(0).getOccupationNameLocal() : "");
			}
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
		}
			return occupationDesc;
		}
	
	public synchronized String getListItem(String insuranceId , String branchCode, String itemType, String itemCode) {
		String itemDesc = "" ;
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));


			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			//Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			//Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n1,n2,n3,n4,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "" ;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc ;
	}
}
