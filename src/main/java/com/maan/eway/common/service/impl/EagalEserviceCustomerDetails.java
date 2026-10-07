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
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
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
import com.maan.eway.workflow.dto.WorkEngine;
import com.maan.eway.workflow.service.JsonMapperFromDB;

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
public class EagalEserviceCustomerDetails {
	
	private Logger log = LogManager.getLogger(EagalEserviceCustomerDetails.class);
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ;
	
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
	
	@Autowired
	private JsonMapperFromDB jsonMapper;
	
	public List<String> validateCustomerDetails(EserviceCustomerSaveReq req) {
	List<String> errorList = new ArrayList<String>();
	try {
		if (req.getSaveOrSubmit().equalsIgnoreCase("Submit")) {
			
			if (StringUtils.isBlank(req.getTitle()))  {
				errorList.add("1047");
			}
			if (StringUtils.isBlank(req.getClientName()) ) {
				errorList.add("1001");
			} else if (req.getClientName().length() > 250) {
			   errorList.add("1002");
			} 
			else if (StringUtils.isNotBlank(req.getClientName())&& !req.getClientName().matches("[a-zA-Z.&() ]+") && !req.getClientName().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")){
				errorList.add("1003");		
			}
			if("1".equalsIgnoreCase(req.getPolicyHolderType())) {
				if(StringUtils.isBlank(req.getGender())) {
					errorList.add("1087");
				}
			}
			if (StringUtils.isBlank(req.getOccupation()) ) {
				errorList.add("1022");
			} else if(req.getOccupation().equalsIgnoreCase("99999")){
				if (StringUtils.isBlank(req.getOtherOccupation()) ) {
					errorList.add("1023");
				}else if (req.getOtherOccupation().length() > 100){
					errorList.add("1024"); 
				}else if(!req.getOtherOccupation().matches("[a-zA-Z\\s]+")){
					errorList.add("1025");
				}
			}
			if("2".equalsIgnoreCase(req.getPolicyHolderType())) {
				if ( StringUtils.isBlank(req.getEmail1()) ) {
					errorList.add("1440");
				}else if ( StringUtils.isNotBlank(req.getEmail1()) ) {
					if( req.getEmail1().length() > 100 ) {
						errorList.add("1032");
					} else if(StringUtils.isNotBlank(req.getEmail1())) {
						boolean b = isValidMail(req.getEmail1());
						if (b == false && (!req.getEmail1().matches("^[a-zA-ZÀ-ÿ\\s'-]+$") || !req.getEmail1().matches("^[.@]+$"))) {
							errorList.add("1033");
						}
					}
				}
			}
			if (StringUtils.isBlank(req.getMobileCode1())) {
				errorList.add("1062");
			}
		/**
		 * Updated validation for mobile number
		 * mobile number should not be blank, exactly 8 digits, contains only numbers, starts with 5, all zeros not accepted
		 */
			if(StringUtils.isBlank(req.getMobileNo1())) {
				errorList.add("1026");
			}			
			else if(req.getMobileNo1().length() != 8) {
				errorList.add("1027");
			}
			else if(!req.getMobileNo1().startsWith("5")) {
				errorList.add("10027");
			}
			else if(!req.getMobileNo1().matches("^5\\d{7}$")) {
				errorList.add("1028");				
			}
			else if(Long.valueOf(req.getMobileNo1()).longValue() == 0L) {
				errorList.add("1029");
			}
		
			
		
			//if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
			if (StringUtils.isBlank(req.getIdType())) {
				errorList.add("1011");
			}
			if (StringUtils.isBlank(req.getPolicyHolderTypeid())) {
				errorList.add("1012");
			}
			
			if (StringUtils.isBlank(req.getIdNumber())) {
				errorList.add("1013");
			}else {
				if ("1".equalsIgnoreCase(req.getPolicyHolderType())) {
					if("7".equals(req.getPolicyHolderTypeid())) {
						if (!req.getIdNumber().matches("^[A-Za-z0-9]{14}$")) {
							errorList.add("1014");
						} else if(!req.getIdNumber().matches("^[A-Za-z][A-Za-z0-9]*$")) {	
							errorList.add("1015");
						}
					}else if("3".equals(req.getPolicyHolderTypeid())) {
						 if(!req.getIdNumber().matches("^[A-Za-z0-9]+$")) {	
							errorList.add("1015");
						}
					}
				}else {
					if("8".equals(req.getPolicyHolderTypeid())) {
						 if(!req.getIdNumber().matches("^[A-Za-z][A-Za-z0-9]*$")) {	
							errorList.add("1015");
						}
					}else if("9".equals(req.getPolicyHolderTypeid())) {
						 if(!req.getIdNumber().matches("^F[A-Za-z0-9]$")) {	
							errorList.add("1015");
						}
					}
				}
			}
			if(StringUtils.isNotBlank(req.getIdNumber())) {
				List<EserviceCustomerDetails>list=repository.findByCompanyIdAndIdNumberAndClientStatus(req.getCompanyId(), req.getIdNumber(),"Y");
				list=list.stream().filter(i ->!(i.getCustomerReferenceNo().equals(req.getCustomerReferenceNo()))).collect(Collectors.toList());
				if(!CollectionUtils.isEmpty(list)) {
					errorList.add("3312");
				}
			}
		/**
		 * Validation the National Identity Card (NIC) ID number   
		 * IdNumber should not be blank, exactly 14 characters long, starts with alphabets
		 */
			if("NIC".equalsIgnoreCase(req.getPolicyHolderTypeid()) && StringUtils.isNotBlank(req.getIdNumber())) {				
				if (req.getIdNumber().length() != 14) {	
					errorList.add("2348");	
				}
				else if (!req.getIdNumber().matches("^[A-Za-z][A-Za-z0-9]{13}+$")) {	
					errorList.add("2349");	
				}								
			}
			
			if (StringUtils.isBlank(req.getPreferredNotification())) {
				errorList.add("1049");
			}
			if ("2".equalsIgnoreCase(req.getPolicyHolderType())) {
				if(StringUtils.isBlank(req.getStreet()) ) {
					errorList.add("3310");
				}else if (req.getStreet().length() > 100) {
					errorList.add("3311");
				}
				if (StringUtils.isBlank(req.getCountry())) {
					errorList.add("1048");
				}
				if (StringUtils.isBlank(req.getCityName())) {
					errorList.add("1082");
				} else if (req.getCityName().length() > 100) {
					errorList.add("1083");
				}
			}
			if (StringUtils.isBlank(req.getRegionCode())) {
				errorList.add("1053");
			} else if (req.getRegionCode().length() > 20) {
				errorList.add("1054");
			}
			
			if (StringUtils.isBlank(req.getClientStatus())) {
				errorList.add("1010");
			}
			if (StringUtils.isNotBlank(req.getPinCode())) {
				if (req.getPinCode().length() > 20) {
					errorList.add("1016");
				}
			} 
			
			if (StringUtils.isNotBlank(req.getFax()) && req.getFax().length() > 20) {
				errorList.add("1017");
			}
			
			if (StringUtils.isNotBlank(req.getTelephoneNo2()) && req.getTelephoneNo2().length() > 20) {
				errorList.add("1018");
			} else if (StringUtils.isNotBlank(req.getTelephoneNo2()) && !req.getTelephoneNo2().matches("\\d+")) {
				errorList.add("1019");	
			}

			if (StringUtils.isNotBlank(req.getTelephoneNo3()) && req.getTelephoneNo3().length() > 20) {
				errorList.add("1020");
			} else if (StringUtils.isNotBlank(req.getTelephoneNo3()) && !req.getTelephoneNo3().matches("\\d+")) {
				errorList.add("1021");
			}
			
			if (StringUtils.isNotBlank(req.getMobileNo3()) &&( req.getMobileNo3().length() > 10||req.getMobileNo3().length() < 10)) {
				errorList.add("1030");
			} else if (StringUtils.isNotBlank(req.getMobileNo3()) && !req.getMobileNo3().matches("\\d+")) {
				errorList.add("1031");
			}

			 
			
			if (StringUtils.isNotBlank(req.getEmail2()) && req.getEmail2().length() > 20) {
				errorList.add("1034");
			} else if (StringUtils.isNotBlank(req.getEmail2())) {
				boolean b = isValidMail(req.getEmail2());

				if (b == false) {
					errorList.add("1035");
				}
			}
			if (StringUtils.isNotBlank(req.getEmail3()) && req.getEmail3().length() > 20) {
				errorList.add("1036");
			} else if (StringUtils.isNotBlank(req.getEmail3())) {
				boolean b = isValidMail(req.getEmail3());
				if (b == false) {
					errorList.add("1037");
				}
			}
			if (StringUtils.isBlank(req.getLanguage())) {
				errorList.add("1038");
			}

			if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail2()) && req.getEmail1().equalsIgnoreCase(req.getEmail2())) {
				errorList.add("1039");
			}
			if (StringUtils.isNotBlank(req.getEmail1()) && StringUtils.isNotBlank(req.getEmail3()) && req.getEmail1().equalsIgnoreCase(req.getEmail3())) {
				errorList.add("1040");
			}
			if (StringUtils.isNotBlank(req.getEmail2()) && StringUtils.isNotBlank(req.getEmail3()) && req.getEmail2().equalsIgnoreCase(req.getEmail3())) {
				errorList.add("1041");
			}

			if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo2()) && req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo2())) {
				errorList.add("1042");
			}
			if (StringUtils.isNotBlank(req.getTelephoneNo1()) && StringUtils.isNotBlank(req.getTelephoneNo3()) && req.getTelephoneNo1().equalsIgnoreCase(req.getTelephoneNo3())) {
				errorList.add("1042");
			}
			if (StringUtils.isNotBlank(req.getTelephoneNo2()) && StringUtils.isNotBlank(req.getTelephoneNo3())&& req.getTelephoneNo2().equalsIgnoreCase(req.getTelephoneNo3())) {
				errorList.add("1043");
			}

			if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo2())&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo2())) {
				errorList.add("1044");
			}
			if (StringUtils.isNotBlank(req.getMobileNo1()) && StringUtils.isNotBlank(req.getMobileNo3())&& req.getMobileNo1().equalsIgnoreCase(req.getMobileNo3())) {
				errorList.add("1045");
			}
			if (StringUtils.isNotBlank(req.getMobileNo2()) && StringUtils.isNotBlank(req.getMobileNo3())&& req.getMobileNo2().equalsIgnoreCase(req.getMobileNo3())) {
				errorList.add("1046");
			}
			

			if (StringUtils.isNotBlank(req.getPolicyHolderType())) {

				if (req.getPolicyHolderType().equalsIgnoreCase("2")) {
					if (StringUtils.isBlank(req.getBusinessType())) {
						//errorList.add("1050");
					}
				}
			}
			
			
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add("1058");
			} else if (req.getStatus().length() > 1) {
				errorList.add("1059");
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus())|| "P".equals(req.getStatus()))) {
				errorList.add("1060");
			}
			if (StringUtils.isBlank(req.getStateCode())) {
				errorList.add("1061");
			}
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add("1063");
			} else if (req.getCreatedBy().length() > 100) {
				errorList.add("1064");
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add("1074");
			} else if (req.getBranchCode().length() > 20) {
				errorList.add("1075");
			}
			
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add("1076");
			} else if (req.getProductId().length() > 20) {
				errorList.add("1077");
			}
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add("1078");
			} else if (req.getCompanyId().length() > 20) {
				errorList.add("1079");
			}
			
			//Check Marital Status only for Individual Customer (IdType =1)
			if("1".equals(req.getIdType()) && StringUtils.isBlank(req.getMaritalStatus())) {
				errorList.add("3313");
			}
			
			if(req.getRiskAssessmentDate()==null || req.getRiskAssessmentDate().equals(""))
			{
				errorList.add("3314");
			}
			if(req.getPhoneNoCode()==null )
			{
				errorList.add("3315");
			}
			
		/**
		 * Validation for phone number code and phone number (phone number is passed through mobile no.2 field)
		 * Phone no code should not be blank
		 * Phone no should not be blank, exactly 7 digits, contains only numbers, all zeros not accepted
		 */
			if(req.getPhoneNoCode() == null || req.getPhoneNoCode().isBlank()) {
				errorList.add("3315");
			}			
			if(req.getMobileNo2() == null || req.getMobileNo2().isBlank()) {
				errorList.add("2350");
			}
			else if(req.getMobileNo2().length() != 7) {
					errorList.add("2351");
			}
			else if(!req.getMobileNo2().matches("^\\d{7}$")) {
				errorList.add("2352");
			}
			else if (Long.valueOf(req.getMobileNo2()).longValue() == 0L){
				errorList.add("2353");
			}
			
						
			List<EserviceCustomerDetails> list = new ArrayList<EserviceCustomerDetails>();
			if ((StringUtils.isNotBlank(req.getAddress1())) 
				//	&& (StringUtils.isNotBlank(req.getAddress2()))
					&& (StringUtils.isNotBlank(req.getBranchCode()))
				//	&& (StringUtils.isNotBlank(req.getBusinessType()))
					&& (StringUtils.isNotBlank(req.getCityCode())) && (StringUtils.isNotBlank(req.getCityName()))
					&& (StringUtils.isNotBlank(req.getClientName()))
					&& (StringUtils.isNotBlank(req.getClientStatus()))
					&& (StringUtils.isNotBlank(req.getCompanyId())) && (StringUtils.isNotBlank(req.getCreatedBy()))
					// && (StringUtils.isNotBlank(req.getCustomerReferenceNo()))
					&& (StringUtils.isNotBlank(req.getEmail1())) 
				//	&& (StringUtils.isNotBlank(req.getEmail2()))
				//	&& (StringUtils.isNotBlank(req.getEmail3())) && (StringUtils.isNotBlank(req.getFax()))
					&& (StringUtils.isNotBlank(req.getGender())) && (StringUtils.isNotBlank(req.getIdNumber()))
					&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
					&& (StringUtils.isNotBlank(req.getLanguage()))
				//	&& (StringUtils.isNotBlank(req.getLanguageDesc()))
					&& (StringUtils.isNotBlank(req.getMobileNo1()))
				//	&& (StringUtils.isNotBlank(req.getMobileNo2()))
				//	&& (StringUtils.isNotBlank(req.getMobileNo3()))
					&& (StringUtils.isNotBlank(req.getNationality()))
					&& (StringUtils.isNotBlank(req.getOccupation()))
					&& (StringUtils.isNotBlank(req.getPlaceOfBirth()))
					&& (StringUtils.isNotBlank(req.getPolicyHolderType()))
					&& (StringUtils.isNotBlank(req.getPolicyHolderTypeid()))
					&& (StringUtils.isNotBlank(req.getProductId())) && (StringUtils.isNotBlank(req.getRegionCode()))
					&& (StringUtils.isNotBlank(req.getStateCode())) && (StringUtils.isNotBlank(req.getStateName()))
					&& (StringUtils.isNotBlank(req.getStatus()))
				//	&& (StringUtils.isNotBlank(req.getStreet()))
				//	&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
				//	&& (StringUtils.isNotBlank(req.getTelephoneNo1()))
				//	&& (StringUtils.isNotBlank(req.getTelephoneNo2()))
				//  && (StringUtils.isNotBlank(req.getTelephoneNo3())) && (StringUtils.isNotBlank(req.getTitle()))
					&& (req.getDobOrRegDate()!=null)
					&& (StringUtils.isNotBlank(req.getIsTaxExempted()))
				//	&& (StringUtils.isNotBlank(req.getTaxExemptedId()))
					&& (StringUtils.isNotBlank(req.getPreferredNotification()))
				//	&& (req.getAppointmentDate()!=null)
					
					){

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EserviceCustomerDetails> query = cb.createQuery(EserviceCustomerDetails.class);
				// Find all
				Root<EserviceCustomerDetails> b = query.from(EserviceCustomerDetails.class);
				// Select
				query.select(b);
				// Where

				Predicate n1 = (cb.like(cb.lower(b.get("address1")), req.getAddress1().toLowerCase()));
			//	Predicate n2 = (cb.like(cb.lower(b.get("address2")), req.getAddress2().toLowerCase()));
				Predicate n3 = (cb.like(cb.lower(b.get("branchCode")), req.getBranchCode().toLowerCase()));
			//	Predicate n4 = (cb.like(cb.lower(b.get("businessType")), req.getBusinessType().toLowerCase()));
			//	Predicate n5 = (cb.like(cb.lower(b.get("cityCode")), req.getCityCode().toLowerCase()));
				Predicate n5 =	(cb.equal(b.get("cityCode") ,  null != req.getCityCode() && 
						req.getCityCode().matches("[0-9]+") ? Integer.valueOf(req.getCityCode()) : 0 ));
				Predicate n6 = (cb.like(cb.lower(b.get("cityName")), req.getCityName().toLowerCase()));
				Predicate n7 = (cb.like(cb.lower(b.get("clientName")), req.getClientName().toLowerCase()));
				Predicate n8 = (cb.like(cb.lower(b.get("clientStatus")), req.getClientStatus().toLowerCase()));
				Predicate n9 = (cb.like(cb.lower(b.get("companyId")), req.getCompanyId().toLowerCase()));
				Predicate n10 = (cb.like(cb.lower(b.get("createdBy")), req.getCreatedBy().toLowerCase()));
				// Predicate n11 =
				// (cb.like(cb.lower(b.get("customerReferenceNo")),req.getCustomerReferenceNo().toLowerCase()));
				Predicate n12 = (cb.equal(b.get("dobOrRegDate"), req.getDobOrRegDate()));
				Predicate n13 = (cb.like(cb.lower(b.get("email1")), req.getEmail1().toLowerCase()));
			//	Predicate n14 = (cb.like(cb.lower(b.get("email2")), req.getEmail2().toLowerCase()));
			//	Predicate n15 = (cb.like(cb.lower(b.get("email3")), req.getEmail3().toLowerCase()));
			//	Predicate n16 = (cb.equal(b.get("fax"), req.getFax().toLowerCase()));
				Predicate n17 = (cb.like(cb.lower(b.get("gender")), req.getGender().toLowerCase()));
				Predicate n18 = (cb.like(cb.lower(b.get("idNumber")), req.getIdNumber().toLowerCase()));
				Predicate n19 = (cb.like(cb.lower(b.get("isTaxExempted")), req.getIsTaxExempted().toLowerCase()));
				Predicate n20 = (cb.like(cb.lower(b.get("language")), req.getLanguage().toLowerCase()));
			//	Predicate n21 = (cb.like(cb.lower(b.get("languageDesc")), req.getLanguageDesc().toLowerCase()));
				Predicate n22 = (cb.equal(b.get("mobileNo1"), req.getMobileNo1()));
			//	Predicate n23 = (cb.equal(b.get("mobileNo2"), req.getMobileNo2()));
			//	Predicate n24 = (cb.equal(b.get("mobileNo3"), req.getMobileNo3()));
				Predicate n25 = (cb.like(cb.lower(b.get("nationality")), req.getNationality().toLowerCase()));
				Predicate n26 = (cb.like(cb.lower(b.get("occupation")), req.getOccupation().toLowerCase()));
				Predicate n27 = (cb.like(cb.lower(b.get("placeOfBirth")), req.getPlaceOfBirth().toLowerCase()));
				Predicate n28 = (cb.like(cb.lower(b.get("policyHolderType")),
						req.getPolicyHolderType().toLowerCase()));
			//	Predicate n29 = (cb.like(cb.lower(b.get("policyHolderTypeId")),
			//			req.getPolicyHolderTypeid().toLowerCase()));
			//	Predicate n30 = (cb.like(cb.lower(b.get("productId")), req.getProductId()));
				
				Predicate n30 = (cb.equal(b.get("productId") ,  null != req.getProductId() && 
						req.getProductId().matches("[0-9]+") ? Integer.valueOf(req.getProductId()) : 0 ));
				Predicate n31 = (cb.like(cb.lower(b.get("regionCode")), req.getRegionCode().toLowerCase()));
			//	Predicate n32 = (cb.like(cb.lower(b.get("stateCode")), req.getStateCode().toLowerCase()));
				Predicate n33 = (cb.like(cb.lower(b.get("stateName")), req.getStateName().toLowerCase()));
				Predicate n34 = (cb.like(cb.lower(b.get("status")), req.getStatus().toLowerCase()));
			//	Predicate n35 = (cb.like(cb.lower(b.get("street")), req.getStreet().toLowerCase()));
			//	Predicate n36 = (cb.like(cb.lower(b.get("taxExemptedId")), req.getTaxExemptedId().toLowerCase()));
			//	Predicate n37 = (cb.equal(b.get("telephoneNo1"), req.getTelephoneNo1()));
			//	Predicate n38 = (cb.equal(b.get("telephoneNo2"), req.getTelephoneNo2()));
			//	Predicate n39 = (cb.equal(b.get("telephoneNo3"), req.getTelephoneNo3()));
			//	Predicate n40 = (cb.like(cb.lower(b.get("title")), req.getTitle().toLowerCase()));
			//	Predicate n41 = (cb.equal(b.get("appointmentDate"), req.getAppointmentDate()));
				Predicate n42 = (cb.like(cb.lower(b.get("preferredNotification")), req.getPreferredNotification().toLowerCase()));

				query.where(n1,  n3,  n6, n7, n8, n9, n10,
						// n11,
						n12, n13,  n17, n18, n19, n20,  n22, n25, n26, n27, n28,
						n30, n31,  n33, n34, /*n35,*/  n42);
				// Get Result 
				TypedQuery<EserviceCustomerDetails> result = em.createQuery(query);
				list = result.getResultList();
				if (list.size() > 0 && req.getCustomerReferenceNo()==null) {
					errorList.add("1511");

				}
			}
		}

		else if (req.getSaveOrSubmit().equalsIgnoreCase("Save")) {
			if (StringUtils.isBlank(req.getClientName())) {
				errorList.add("1084");
			} else if (req.getClientName().length() > 100) {
				errorList.add("1085");
			}
			if (StringUtils.isBlank(req.getPolicyHolderType())) {
			 errorList.add("1086");
			}
			
		/**
		 * Validation the National Identity Card (NIC) ID number   
		 * IdNumber should not be blank, exactly 14 characters long, starts with alphabets
		 */
			if("NIC".equalsIgnoreCase(req.getPolicyHolderTypeid()) && StringUtils.isBlank(req.getIdNumber())){
				errorList.add("1013");
			}
			
			if("NIC".equalsIgnoreCase(req.getPolicyHolderTypeid()) && StringUtils.isNotBlank(req.getIdNumber())) {				
				if (req.getIdNumber().length() != 14) {	
					errorList.add("2348");	
				}
				else if (!req.getIdNumber().matches("^[A-Za-z][A-Za-z0-9]{13}+$")) {	
					errorList.add("2349");	
				}								
			}
							
		}
		
		}catch (Exception e) {
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
				productId=Integer.valueOf(req.getProductId());
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(req.getCompanyId());  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("1");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
	 		 	custRefNo =  genSeqNoService.generateSeqCall(generateSeqReq);
				res.setResponse("Saved Successfully");
				res.setSuccessId(custRefNo);
			} else {
				// Update
				custRefNo = req.getCustomerReferenceNo();
				EserviceCustomerDetails findData = repository.findByCustomerReferenceNo(req.getCustomerReferenceNo());
				entryDate = findData.getEntryDate();
				createdBy = findData.getCreatedBy();
				productId=findData.getProductId();
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
			
			saveData.setZone(StringUtils.isBlank(req.getZone())? 0:Integer.valueOf(req.getZone()));
			
			
			
			saveData.setBrokerBranchCode(req.getBrokerBranchCode());
			
			saveData.setTitle(req.getTitle());
			saveData.setFirstName(req.getClientName());
			saveData.setLastName(StringUtils.isBlank(req.getLastName())?null:req.getLastName());
			saveData.setBusinessType(req.getBusinessType());
			saveData.setGender(StringUtils.isBlank(req.getGender()) ? "M" : req.getGender());
			saveData.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
			saveData.setOtherOccupation(req.getOtherOccupation());
			saveData.setEmail1(req.getEmail1());
			saveData.setWhatsappCode(req.getWhatsappCode());
			saveData.setMobileCode1(req.getMobileCode1());
			saveData.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
			saveData.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
			saveData.setMobileNo1(req.getMobileNo1());
			saveData.setMobileNo2(req.getMobileNo2());
			saveData.setMobileNo3(req.getMobileNo3());
			saveData.setActivities(req.getActivities());
			saveData.setIdType(req.getIdType());
			saveData.setIdNumber(req.getIdNumber());
			saveData.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted())?"0":req.getIsTaxExempted());
			saveData.setPreferredNotification(req.getPreferredNotification());
			saveData.setStatus(req.getStatus());
			
			saveData.setCountry(req.getCountry());
			saveData.setCountryName(req.getCountryName());
			saveData.setCityCode(StringUtils.isBlank(req.getCityCode())?null :Integer.valueOf(req.getCityCode()));
			saveData.setCityName(req.getCityName());
			saveData.setPinCode(req.getPinCode());
			saveData.setRegionCode(req.getRegionCode());
			
			saveData.setPolicyHolderTypeid(req.getPolicyHolderTypeid());
			saveData.setVrTinNo(req.getVrTinNo());
			//saveData.setVrnGst(req.getVrTinNo());
			
			// Age Calculation
			int age = 0 ;
			Date dob = null;
			if (req.getDobOrRegDate() !=null) {
				dob = req.getDobOrRegDate();
				Date today = new Date();
				age = today.getYear() - dob.getYear();
			}
			// From List Item Value
			
			Map<String,String> title = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"NAME_TITLE",req.getTitle());//listRepo.findByItemTypeAndItemCode("NAME_TITLE", req.getTitle());
			Map<String,String> gender = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"GENDER",req.getGender());// listRepo.findByItemTypeAndItemCode("GENDER", saveData.getGender());
			Map<String,String> language = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"LANGUAGE",req.getLanguage());//listRepo.findByItemTypeAndItemCode("LANGUAGE", req.getLanguage());
			Map<String,String> policyHolderType = getListItemLocal ("99999" , req.getBranchCode() ,"POLICY_HOLDER_TYPE",req.getPolicyHolderType());//listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_TYPE",	req.getPolicyHolderType());
			Map<String,String> policyHolderTypeId = getListItemLocal (req.getCompanyId(), req.getBranchCode() ,"POLICY_HOLDER_ID_TYPE",req.getPolicyHolderTypeid());// listRepo.findByItemTypeAndItemCode("POLICY_HOLDER_ID_TYPE", req.getPolicyHolderTypeid());
			
			String genderDesc = Optional.ofNullable(gender).map(map -> map.get("itemDesc")).orElse("");
			String titleDesc = Optional.ofNullable(title).map(map -> map.get("itemDesc")).orElse("");
			String languageDesc = Optional.ofNullable(language).map(map -> map.get("itemDesc")).orElse("");
			String policyHolderTypeDesc = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDesc")).orElse("");
			String policyHolderTypeIdDesc = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDesc")).orElse("");
			
			// From List Item Value (Local)
			String genderLocal = Optional.ofNullable(gender).map(map -> map.get("itemDescLocal")).orElse("");
			String titleLocal = Optional.ofNullable(title).map(map -> map.get("itemDescLocal")).orElse("");
			String languageLocal = Optional.ofNullable(language).map(map -> map.get("itemDescLocal")).orElse("");
			String PolicyHolderTypeLocal = Optional.ofNullable(policyHolderType).map(map -> map.get("itemDescLocal")).orElse("");
			String policyHolderTypeIdLocal = Optional.ofNullable(policyHolderTypeId).map(map -> map.get("itemDescLocal")).orElse("");
			
			// From Region_mater for state name local
			String stateNameLocal = "";
			List<RegionMaster> rgMaster = regionMasterRepo.findByCountryIdAndRegionCode(req.getCountry(),req.getStateCode());
			if(rgMaster!= null  && rgMaster.size()>0) {
				stateNameLocal = rgMaster.get(0).getRegionNameLocal();
			}
			// From State_master for city name local
			String cityNameLocal = "";
			List<StateMaster> stMaster = stateMasterRepo.findByStateIdAndCountryIdAndRegionCode(Integer.valueOf(StringUtils.isNotBlank(req.getCityCode())?req.getCityCode():"0"),req.getCountry(),req.getStateCode());
			if(stMaster!= null && stMaster.size()>0) {
				cityNameLocal = stMaster.get(0).getStateNameLocal();
			}
			
			if(StringUtils.isNotBlank(req.getMobileCode1())){		        
				Map<String,String> mobileCode1Desc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode1());
				String mobileCode1 = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDesc")).orElse("");
				//String mobileCode1Local = Optional.ofNullable(mobileCode1Desc).map(map -> map.get("itemDescLocal")).orElse("");		
			saveData.setMobileCodeDesc1(mobileCode1);

			}
	        if(StringUtils.isNotBlank(req.getMobileCode2())){
	        	Map<String,String> mobileCode2Desc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode2());
	        	String mobileCode2 = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDesc")).orElse("");
				//String mobileCode2Local = Optional.ofNullable(mobileCode2Desc).map(map -> map.get("itemDescLocal")).orElse("");		
			saveData.setMobileCodeDesc2(mobileCode2);

	        }
	       
	        
	        if(StringUtils.isNotBlank(req.getMobileCode3())){		        
	        	Map<String,String> mobileCode3Desc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getMobileCode3());
	        	String mobileCode3 = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDesc")).orElse("");
				//String mobileCode3Local = Optional.ofNullable(mobileCode3Desc).map(map -> map.get("itemDescLocal")).orElse("");
			saveData.setMobileCodeDesc3(mobileCode3);

	        }
	        if(StringUtils.isNotBlank(req.getWhatsappCode())){		        
	        	Map<String,String> whatsappCodeDesc = getListItemLocal (req.getCompanyId() , req.getBranchCode() ,"MOBILE_CODE",req.getWhatsappCode());
	        	String whatsappCode = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDesc")).orElse("");
				//String whatsappCodeLocal = Optional.ofNullable(whatsappCodeDesc).map(map -> map.get("itemDescLocal")).orElse("");
			saveData.setWhatsappCodeDesc(whatsappCode);

	        }			
	        String businessTypeLocal = "";
			if (StringUtils.isNotBlank(req.getBusinessType())) {
				Map<String,String> businessTypeDesc =  getListItemLocal ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
				String businessType = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDesc")).orElse("");
				businessTypeLocal = Optional.ofNullable(businessTypeDesc).map(map -> map.get("itemDescLocal")).orElse("");
				saveData.setBusinessTypeDesc(businessType);
			}
			String occupationDesc="",occupationDescLocal="";
			if (StringUtils.isNotBlank(req.getOccupation())) {
				 Map<String,String> occupation = getByOccupationIdDesc(req.getOccupation(), req.getCompanyId(),req.getProductId() , req.getBranchCode());
				 occupationDesc = Optional.ofNullable(occupation).map(map -> map.get("occupationName")).orElse("");
				 occupationDescLocal = Optional.ofNullable(occupation).map(map -> map.get("occupationNameLocal")).orElse("");
			}
			
			//Desc
			saveData.setTitleDesc(titleDesc);
			saveData.setGenderDesc(genderDesc);
			saveData.setLanguageDesc(languageDesc);
			saveData.setOccupationDesc(occupationDesc);
			saveData.setPolicyHolderTypeDesc(policyHolderTypeDesc);
			saveData.setPolicyHolderTypeIdDesc(policyHolderTypeIdDesc);
			saveData.setIdTypeDesc(policyHolderTypeIdDesc);
			saveData.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
			saveData.setAge(age);
			
			
			//local desc feilds
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
			
			
			// Kenya Rating Fields

			if (req.getLicenseIssuedDate()!=null ) {
				saveData.setLicenseIssuedDate(req.getLicenseIssuedDate());
				Date licenceIssued = req.getDobOrRegDate();
				Date today = new Date();
				int licenseDuration = today.getYear() - licenceIssued.getYear();
				saveData.setLicenseDuration(licenseDuration);
				
			} else {
				saveData.setLicenseIssuedDate(new Date());
				saveData.setLicenseDuration(20);
			}

			
			
			saveData.setVipFlag(req.getVipFlag());
			saveData.setPhoneNoCode(req.getPhoneNoCode());
			saveData.setRiskAssessmentDate(req.getRiskAssessmentDate());

			repository.save(saveData);
			repository.flush();

			if("100028".equals(req.getCompanyId())) {
				WorkEngine e=new WorkEngine();
				e.setCompanyId(req.getCompanyId());
				e.setProductId(req.getProductId().toString());
				e.setQuoteNo("");
				e.setRequestReferenceNo(custRefNo);
				e.setIntegType("CUST_CREATE");
				try {
					List<Map<String, Object>> quotation = jsonMapper.createQuotation(e);
					Map<String, Object> response = (Map<String, Object>)  quotation.get(0).get("Response");
					Map<String, Object> dataq = (Map<String, Object>) response.get("data");	
					String	customerId=(String) dataq.get("customerId"); 
					saveData.setPolCustCode(customerId);
					repository.save(saveData);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					saveData.setPolCustCode("Exception");
					repository.save(saveData);
				}
			}

			//Personal Info Update
			
			//Endorsement flow and B2C Flow
			//Type=B2C
			if(StringUtils.isNotBlank(req.getEndtCategDesc())) {
			if("Non Financial".equalsIgnoreCase(req.getEndtCategDesc().toString())) {
				PersonalInfo savePersonalInfo=new PersonalInfo();
				HomePositionMaster homedata=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			//	PersonalInfo personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
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
					String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
					savePersonalInfo.setBusinessTypeDesc(businessType);
						
				}
				
				
				savePersonalInfo.setRegionCode(req.getRegionCode());
				savePersonalInfo.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted())?"0":req.getIsTaxExempted());
				savePersonalInfo.setCityCode(req.getCityCode());
				savePersonalInfo.setCityName(req.getCityName());
				savePersonalInfo.setClientName(req.getClientName());
				savePersonalInfo.setClientStatus(req.getClientStatus());
				savePersonalInfo.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
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
				savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
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
				savePersonalInfo.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
				savePersonalInfo.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
				savePersonalInfo.setMobileNo1(req.getMobileNo1());
				savePersonalInfo.setMobileNo2(req.getMobileNo2());
				savePersonalInfo.setMobileNo3(req.getMobileNo3());
				savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
				if (StringUtils.isNotBlank(req.getMobileCode1())) {
 					ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode1(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getMobileCode2())) {
					ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode2(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getMobileCode3())) {
					ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode3(),req.getCompanyId());
					savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

				}
				if (StringUtils.isNotBlank(req.getWhatsappCode())) {
					ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
							req.getWhatsappCode(),req.getCompanyId());
					savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

				}
				savePersonalInfo.setRegionCode(req.getRegionCode());
				savePersonalInfo.setStateCode(req.getStateCode());
				savePersonalInfo.setStateName(req.getStateName());
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
				savePersonalInfo.setVipFlag(req.getVipFlag());
				savePersonalInfo.setPhoneNoCode(req.getPhoneNoCode());
				savePersonalInfo.setRiskAssessmentDate(req.getRiskAssessmentDate());
	
				personalInforepo.save(savePersonalInfo);
			}
			}else if(StringUtils.isNotBlank(req.getType())) {
				HomePositionMaster homedata=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				if("b2c".equalsIgnoreCase(req.getType().toString()) && homedata !=null ) {
					PersonalInfo savePersonalInfo=new PersonalInfo();
					
				//	PersonalInfo personalInfodata=personalInforepo.findByCustomerId(homedata.getCustomerId());
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
						String businessType =  getListItem ("99999" , req.getBranchCode() ,"BUSINESS_TYPE",req.getBusinessType());//listRepo.findByItemTypeAndItemCode("BUSINESS_TYPE", req.getBusinessType());
						savePersonalInfo.setBusinessTypeDesc(businessType);
					}
					
					savePersonalInfo.setIsTaxExempted(StringUtils.isBlank(req.getIsTaxExempted())?"0":req.getIsTaxExempted());
					savePersonalInfo.setCityCode(req.getCityCode());
					savePersonalInfo.setCityName(req.getCityName());
					savePersonalInfo.setClientName(req.getClientName());
					savePersonalInfo.setClientStatus(req.getClientStatus());
					savePersonalInfo.setClientStatusDesc(req.getClientStatus().equalsIgnoreCase("N") ? "DeActive" : "Active");
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
					savePersonalInfo.setOccupation(StringUtils.isBlank(req.getOccupation()) ? "2" : req.getOccupation());
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
					savePersonalInfo.setMobileCode2(req.getMobileCode2()==null?"":req.getMobileCode2());
					savePersonalInfo.setMobileCode3(req.getMobileCode3()==null?"":req.getMobileCode3());
					savePersonalInfo.setMobileNo1(req.getMobileNo1());
					savePersonalInfo.setMobileNo2(req.getMobileNo2());
					savePersonalInfo.setMobileNo3(req.getMobileNo3());
					savePersonalInfo.setWhatsappCode(req.getWhatsappCode());
					if (StringUtils.isNotBlank(req.getMobileCode1())) {
						ListItemValue mobiledesc1 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode1(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc1(mobiledesc1.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode2())) {
						ListItemValue mobiledesc2 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode2(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc2(mobiledesc2.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getMobileCode3())) {
						ListItemValue mobiledesc3 = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE", req.getMobileCode3(),req.getCompanyId());
						savePersonalInfo.setMobileCodeDesc3(mobiledesc3.getItemValue());

					}
					if (StringUtils.isNotBlank(req.getWhatsappCode())) {
						ListItemValue whatsappCode = listRepo.findByItemTypeAndItemCodeAndCompanyId("MOBILE_CODE",
								req.getWhatsappCode(),req.getCompanyId());
						savePersonalInfo.setWhatsappcodeDesc(whatsappCode.getItemValue());

					}
					savePersonalInfo.setRegionCode(req.getRegionCode());
					savePersonalInfo.setStateCode(req.getStateCode());
					savePersonalInfo.setStateName(req.getStateName());
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
					savePersonalInfo.setVipFlag(req.getVipFlag());
					savePersonalInfo.setPhoneNoCode(req.getPhoneNoCode());
					savePersonalInfo.setRiskAssessmentDate(req.getRiskAssessmentDate());
					personalInforepo.save(savePersonalInfo);
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
			List<EserviceCustomerDetails> data = repository.findByCustomerReferenceNoOrderByEntryDateDesc(req.getCustomerReferenceNo());
			if(data!=null && data.size()>0) {
				EserviceCustomerDetails cdate=data.get(0);
				res = dozerMapper.map(cdate, CustomerDetailsGetRes.class);
				
				res.setTitle(cdate.getTitle()==null?"":cdate.getTitle());
				res.setFirstName(cdate.getClientName()==null?"":cdate.getClientName());
				res.setBusinessType(cdate.getBusinessType()==null?"":cdate.getBusinessType());
				res.setGender(cdate.getGender()==null?"":cdate.getGender());
				res.setOccupation(cdate.getOccupation()==null?"":cdate.getOccupation());
				res.setOtherOccupation(cdate.getOtherOccupation()==null?"":cdate.getOtherOccupation());
				res.setEmail1(cdate.getEmail1()==null?"":cdate.getEmail1());
				res.setWhatsappCode(cdate.getWhatsappCode()==null?"":cdate.getWhatsappCode());
				res.setMobileCode1(cdate.getMobileCode1()==null?"":cdate.getMobileCode1());
				res.setMobileCode2(cdate.getMobileCode2()==null?"":cdate.getMobileCode2());
				res.setMobileCode3(cdate.getMobileCode3()==null?"":cdate.getMobileCode3());
				res.setMobileCodeDesc1(cdate.getMobileCodeDesc1()==null?"":cdate.getMobileCodeDesc1());
				res.setMobileCodeDesc2(cdate.getMobileCodeDesc2()==null?"":cdate.getMobileCodeDesc2());
				res.setMobileCodeDesc3(cdate.getMobileCodeDesc3()==null?"":cdate.getMobileCodeDesc3());
				res.setMobileNo1(cdate.getMobileNo1()==null?"":cdate.getMobileNo1());
				res.setMobileNo2(cdate.getMobileNo2()==null?"":cdate.getMobileNo2());
				res.setMobileNo3(cdate.getMobileNo3()==null?"":cdate.getMobileNo3());
				res.setActivities(cdate.getActivities()==null?"":cdate.getActivities());
				res.setIdType(cdate.getIdType()==null?"":cdate.getIdType());
				res.setIdNumber(cdate.getIdNumber()==null?"":cdate.getIdNumber());
				res.setIsTaxExempted(cdate.getIsTaxExempted()==null?"":cdate.getIsTaxExempted());
				res.setPreferredNotification(cdate.getPreferredNotification()==null?"":cdate.getPreferredNotification());
				res.setStatus(cdate.getStatus()==null?"":cdate.getStatus());
				
				res.setCountry(cdate.getCountry()==null?"":cdate.getCountry());
				res.setCityCode(cdate.getCityCode()==null?"":cdate.getCityCode().toString());
				res.setCityName(cdate.getCityName()==null?"":cdate.getCityName());
				res.setStateName(cdate.getStateName()==null?"":cdate.getStateName());
				res.setPinCode(cdate.getPinCode()==null?"":cdate.getPinCode());
				res.setRegionCode(cdate.getRegionCode()==null?"":cdate.getRegionCode());
				res.setPolicyHolderTypeid(cdate.getPolicyHolderTypeid()==null?"":cdate.getPolicyHolderTypeid());
				res.setVrTinNo(cdate.getVrTinNo()==null?"":cdate.getVrTinNo());
				res.setVipFlag(cdate.getVipFlag()==null?"":cdate.getVipFlag());
				res.setRiskAssessmentDate(cdate.getRiskAssessmentDate());
				res.setPhoneNoCode(cdate.getPhoneNoCode()==null?"":cdate.getPhoneNoCode());
				res.setMaritalStatus(cdate.getMaritalStatus()==null?"":cdate.getMaritalStatus());
			}
			
					
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
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
