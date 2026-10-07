package com.maan.eway.whatsapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class BrokerWhatsappServiceImpl implements BrokerWhatsappService{

	@Autowired
	private BrokerWhatsappTableRepository brokerWhatsappRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserinfoRepo;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public CommonRes brokerCheckWhatsapp(BrokerCheckReq req) {
		CommonRes res = new CommonRes();
		try {
			BrokerWhatsappTable brokerwhatsapp = brokerWhatsappRepo.findByWhatsappNoAndStatus(req.getWhatsappNo(),"Y");
			
			if(brokerwhatsapp != null) {
				Map<String,Object> resultSet = new HashMap<>();
				resultSet.put("LoginId", brokerwhatsapp.getLoginId());
				resultSet.put("WhatsappNo", brokerwhatsapp.getWhatsappNo());
				resultSet.put("BrokerLoginId", brokerwhatsapp.getBrokerLoginId());
				resultSet.put("UserName", brokerwhatsapp.getUserName());
				resultSet.put("BrokerName", brokerwhatsapp.getBrokerName());
				resultSet.put("OaCode", brokerwhatsapp.getOaCode());
				resultSet.put("AgencyCode", brokerwhatsapp.getAgencyCode());
				resultSet.put("CustomerCode", brokerwhatsapp.getCustomerCode());
				resultSet.put("BrokerCoreAppCode", brokerwhatsapp.getBrokerCoreAppCode());
				resultSet.put("SourceType", brokerwhatsapp.getSourceType());
				resultSet.put("SubUserType", brokerwhatsapp.getSubUserType());
				resultSet.put("BranchCode", brokerwhatsapp.getBranchCode());
				resultSet.put("BrokerBranchCode", brokerwhatsapp.getBrokerBranchCode());
				
				res.setCommonResponse(resultSet);
				res.setErroCode(0);
				res.setErrorMessage(null);
				res.setIsError(false);
				res.setMessage("Success");
			}else {
				res.setCommonResponse(null);
				res.setErroCode(0);
				res.setErrorMessage(null);
				res.setIsError(false);
				res.setMessage("Success");
			}
		}catch(Exception e){
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setErroCode(0);
			res.setErrorMessage(null);
			res.setIsError(true);
			res.setMessage(e.getMessage());
		}
		return res;
	}

	@Override
	public CommonRes saveBrokerWhatsapp(BrokerCheckReq req) {
		CommonRes res = new CommonRes();
		
		try {
			List<Error> validation = brokerWhatsAppValidation(req);
			
			if(validation.isEmpty()) {
				
				BrokerWhatsappTable brokerWhatsapp = new BrokerWhatsappTable();
				
				CriteriaBuilder cb = entityManager.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
				
				Root<LoginMaster> lm = query.from(LoginMaster.class);
				Root<LoginUserInfo> lui = query.from(LoginUserInfo.class);
				Root<LoginBranchMaster> lbm = query.from(LoginBranchMaster.class);
				
				query.multiselect(lm.get("loginId").alias("loginId"),lm.get("userType").alias("userType"),
						lm.get("subUserType").alias("subUserType"),lui.get("oaCode").alias("oaCode"),
						lui.get("agencyCode").alias("agencyCode"),lui.get("userName").alias("userName"),
						lui.get("customerCode").alias("customerCode"),lbm.get("branchCode").alias("branchCode"),
						lbm.get("brokerBranchCode").alias("brokerBranchCode"),lbm.get("salePointCode").alias("salePointCode"));
				
				
				 Predicate n1 = cb.equal(lm.get("loginId"), req.getLoginId()); 
				 Predicate n2 = cb.equal(lm.get("loginId"), lui.get("loginId"));
				 Predicate n3 = cb.equal(lm.get("loginId"), lbm.get("loginId"));
				 Predicate n4 = cb.equal(lui.get("oaCode"), lm.get("oaCode").as(String.class)); 
				 Predicate n5 = cb.equal(lm.get("agencyCode"), lui.get("agencyCode"));
				 Predicate n6 = cb.equal(lbm.get("branchCode"), req.getBranchCode());
				 
				 query.where(n1,n2,n3,n4,n5,n6);
				 
				 List<Tuple> resultList = entityManager.createQuery(query).getResultList();
				 
				 if(!resultList.isEmpty()) {
					 for(WhatsappEmployeeReq empReq : req.getEmployeeDetails()) {
						 
						 Long sNo = brokerWhatsappRepo.count();
						 sNo = sNo+1;
						 brokerWhatsapp.setSno(sNo);
						 brokerWhatsapp.setLoginId(resultList.get(0).get("loginId") == null ? "" : resultList.get(0).get("loginId").toString());
						 brokerWhatsapp.setBrokerLoginId(resultList.get(0).get("loginId") == null ? "" : resultList.get(0).get("loginId").toString());
						 brokerWhatsapp.setBrokerName(resultList.get(0).get("userName") == null ? "" : resultList.get(0).get("userName").toString());
						 brokerWhatsapp.setUserName(empReq.getUserName() == null ? null : empReq.getUserName());
						 brokerWhatsapp.setOaCode(resultList.get(0).get("oaCode") == null ? "" : resultList.get(0).get("oaCode").toString());
						 brokerWhatsapp.setAgencyCode(resultList.get(0).get("agencyCode") == null ? "" : resultList.get(0).get("agencyCode").toString());
						 brokerWhatsapp.setCustomerCode(resultList.get(0).get("customerCode") == null ? "" : resultList.get(0).get("customerCode").toString());
						 brokerWhatsapp.setBranchCode(resultList.get(0).get("branchCode") == null ? "" : resultList.get(0).get("branchCode").toString());
						 brokerWhatsapp.setBrokerBranchCode(resultList.get(0).get("brokerBranchCode") == null ? "" : resultList.get(0).get("brokerBranchCode").toString());
						 brokerWhatsapp.setBrokerCoreAppCode(resultList.get(0).get("salePointCode") == null ? "" : resultList.get(0).get("salePointCode").toString());
						 brokerWhatsapp.setSourceType(resultList.get(0).get("userType") == null ? "" : resultList.get(0).get("userType").toString());
						 brokerWhatsapp.setSubUserType(resultList.get(0).get("subUserType") == null ? "" : resultList.get(0).get("subUserType").toString());
						 brokerWhatsapp.setWhatsappNo(empReq.getWhatsappCode()+empReq.getWhatsappNo());
						 brokerWhatsapp.setStatus(empReq.getStatus());
						 brokerWhatsapp.setEntryDate(req.getEntryDate());
						 brokerWhatsapp.setMobileCode(empReq.getWhatsappCode());
						 brokerWhatsapp.setMobileNo(empReq.getWhatsappNo());
						 
						 brokerWhatsappRepo.saveAndFlush(brokerWhatsapp); 
					 }
					 
					 res.setErrorMessage(Collections.EMPTY_LIST);
					 res.setCommonResponse("Successfully Saved");
					 res.setIsError(false);
					 res.setMessage("Success");
				//	 BrokerWhatsappTable checkBroker = brokerWhatsappRepo.findByLoginId(req.getLoginId());
					 
				/*	 if(checkBroker != null) {
						 checkBroker.setWhatsappNo(req.getWhatsappCode()+req.getWhatsappNo());
						 checkBroker.setStatus(req.getStatus());
						 checkBroker.setUpdatedDate(new Date());
						 checkBroker.setMobileCode(req.getWhatsappCode());
						 checkBroker.setMobileNo(req.getWhatsappNo());
						 
						 brokerWhatsappRepo.saveAndFlush(checkBroker);
						 
						 res.setErrorMessage(Collections.EMPTY_LIST);
						 res.setCommonResponse("Successfully Updated");
						 res.setIsError(false);
						 res.setMessage("Success");
					 }else { */
						 
						 
						 
						 
					 
					 
				 }
			/*	LoginUserInfo userInfo = loginUserinfoRepo.findByLoginId(req.getLoginId());
				if(userInfo != null) {
					brokerWhatsapp.setLoginId(userInfo.getLoginId());
					brokerWhatsapp.setUserName(userInfo.getUserName());
					brokerWhatsapp.setOaCode(userInfo.getOaCode());
					brokerWhatsapp.setAgencyCode(userInfo.getAgencyCode());
					brokerWhatsapp.setCustomerCode(userInfo.getCustomerCode());
				}
				
				LoginBranchMaster loginBranch
				brokerWhatsapp.setLoginId(req.getLoginId());
				*/
			}else {
				res.setErrorMessage(validation);
				res.setCommonResponse(null);
				res.setIsError(true);
				res.setMessage("Failed");
			}
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setMessage(e.getMessage());
		}
		return res;
	}

	private List<Error> brokerWhatsAppValidation(BrokerCheckReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {
			
			if(StringUtils.isBlank(req.getLoginId())) {
				errorList.add(new Error("01", "LoginId", "Please Enter the LoginId"));
			}
			
			if(StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("04", "BranchCode", "Please Enter the Branch Code"));
			}else {
				if(StringUtils.isNotBlank(req.getLoginId())) {
					LoginBranchMaster lbm = loginBranchRepo.findByLoginIdAndBranchCode(req.getLoginId(), req.getBranchCode());
					
					if(lbm == null) {
						errorList.add(new Error("04", "BranchCode", "This Branch Code is not available for this broker"));
					}
				}
			}
			
            for(WhatsappEmployeeReq empReq : req.getEmployeeDetails()) {
            	
            	if(StringUtils.isBlank(empReq.getWhatsappCode())) {
    				errorList.add(new Error("02", "WhatsappCode", "Please Enter the WhatsAppCode"));
    			}
    			
    			if(StringUtils.isBlank(empReq.getWhatsappNo())) {
    				errorList.add(new Error("03", "WhatsappNo", "Please Enter the WhatsApp Number"));
    			}else if (empReq.getWhatsappNo().length() > 10 || empReq.getWhatsappNo().length() < 8) {
    				errorList.add(new Error("03", "WhatsappNo", "Please Enter Valid WhatsApp Number"));
    			}else if (!empReq.getWhatsappNo().matches("[0-9]+")) {
    				errorList.add(new Error("03", "WhatsappNo", "Please Enter WhatsApp Number only in numbers"));
    			}else if (empReq.getWhatsappNo().matches("[0-9]+") && Double.valueOf(empReq.getWhatsappNo()) <= 0) {
    				errorList.add(new Error("03", "WhatsappNo", "Please Enter Valid WhatsApp Number"));
    			}
    			
    			if(StringUtils.isNotBlank(empReq.getWhatsappNo())) {
    				 BrokerWhatsappTable checkNo =	brokerWhatsappRepo.findByMobileNo(empReq.getWhatsappNo());
    				 
    				 if(checkNo != null ) { //&& "SAVE".equalsIgnoreCase(req.getAction())
    					 errorList.add(new Error("03", "WhatsappNo", "The WhatsApp Number "+empReq.getWhatsappNo()+" already availble with another User, Please try with new Number"));
    				 }
    			}
    			
			}
			
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public CommonRes editBrokerWhatsapp(BrokerCheckReq req) {
		CommonRes res = new CommonRes();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			BrokerWhatsappTable broWhatsapp = brokerWhatsappRepo.findBySnoAndLoginId(req.getSNo(), req.getLoginId());
			BrokerCheckRes checkRes = new BrokerCheckRes();
			if(broWhatsapp != null) {
				checkRes.setLoginId(broWhatsapp.getLoginId() == null ? "" : broWhatsapp.getLoginId());
				checkRes.setBranchCode(broWhatsapp.getBranchCode() == null ? "" : broWhatsapp.getBranchCode());
				checkRes.setWhatsappCode(broWhatsapp.getMobileCode() == null ? "" : broWhatsapp.getMobileCode());
				checkRes.setWhatsappNo(broWhatsapp.getMobileNo() == null ? "" : broWhatsapp.getMobileNo());
				checkRes.setStatus(broWhatsapp.getStatus() == null ? "" : broWhatsapp.getStatus());
				checkRes.setEntryDate(broWhatsapp.getEntryDate() == null ? null : sdf.format(broWhatsapp.getEntryDate()));
				checkRes.setUpdatedDate(broWhatsapp.getUpdatedDate() == null ? null : sdf.format(broWhatsapp.getUpdatedDate()));
				
				res.setCommonResponse(checkRes);
				res.setErroCode(0);
				res.setErrorMessage(Collections.EMPTY_LIST);
				res.setIsError(false);
				res.setMessage("Success");
				
			}else {
				res.setCommonResponse(null);
				res.setErroCode(0);
				res.setErrorMessage(Collections.EMPTY_LIST);
				res.setIsError(false);
				res.setMessage("Success");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setErroCode(1);
			res.setErrorMessage(Collections.EMPTY_LIST);
			res.setIsError(true);
			res.setMessage("Failed");
		}
		return res;
	}

	@Override
	public CommonRes gritBrokerWhatsapp(BrokerCheckReq req) {
		CommonRes res = new CommonRes();
		try {
			List<BrokerWhatsappTable> broWhatsapp = brokerWhatsappRepo.findByLoginId(req.getLoginId());
			List<BrokerWhatsappEmployeeGritRes> empResList = new ArrayList<>();
			if(!broWhatsapp.isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				BrokerWhatsAppGritRes gritRes = new BrokerWhatsAppGritRes();
				gritRes.setLoginId(broWhatsapp.get(0).getLoginId() == null ? req.getLoginId() : broWhatsapp.get(0).getLoginId());
				gritRes.setBranchCode(broWhatsapp.get(0).getBranchCode() == null ? "" : broWhatsapp.get(0).getBranchCode());
				gritRes.setEntryDate(broWhatsapp.get(0).getEntryDate() == null ? null : sdf.format(broWhatsapp.get(0).getEntryDate()));
				for(BrokerWhatsappTable gritwhat : broWhatsapp) {
					BrokerWhatsappEmployeeGritRes empRes = new BrokerWhatsappEmployeeGritRes();
					
					
					empRes.setSNo(gritwhat.getSno() == null ? 0l : gritwhat.getSno());
					empRes.setUserName(gritwhat.getUserName() == null ? "" : gritwhat.getUserName());
					empRes.setWhatsappCode(gritwhat.getMobileCode() == null ? "" : gritwhat.getMobileCode());
					empRes.setWhatsappNo(gritwhat.getMobileNo() == null ? "" : gritwhat.getMobileNo());
					empRes.setStatus(gritwhat.getStatus() == null ? "" : gritwhat.getStatus());
					empRes.setUpdatedDate(gritwhat.getUpdatedDate() == null ? null : sdf.format(gritwhat.getUpdatedDate()));
					
					empResList.add(empRes);
				}
				
				gritRes.setEmployeeDetails(empResList);
				
				res.setCommonResponse(gritRes);
				res.setErroCode(0);
				res.setErrorMessage(Collections.EMPTY_LIST);
				res.setIsError(false);
				res.setMessage("Success");
			}else {
				res.setCommonResponse(null);
				res.setErroCode(0);
				res.setErrorMessage(Collections.EMPTY_LIST);
				res.setIsError(false);
				res.setMessage("Success");
			}
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setErroCode(0);
			res.setErrorMessage(Collections.EMPTY_LIST);
			res.setIsError(false);
			res.setMessage(e.getMessage());
		}
		return res;
	}

	@Override
	public CommonRes UpdateEmployee(UpdateWhatsappEmployeeReq req) {
		CommonRes res = new CommonRes();
		
		try {
			
			List<Error> validation = updateBrokerWhatsAppValidation(req);
			
			if(validation.isEmpty()) {
				BrokerWhatsappTable broWhatsapp = brokerWhatsappRepo.findBySnoAndLoginId(req.getSNo(), req.getLoginId());
				
				if(broWhatsapp != null) {
					broWhatsapp.setUserName(req.getUserName());
					broWhatsapp.setWhatsappNo(req.getWhatsappCode()+req.getWhatsappNo());
					broWhatsapp.setMobileCode(req.getWhatsappCode());
					broWhatsapp.setMobileNo(req.getWhatsappNo());
					broWhatsapp.setStatus(req.getStatus());
					broWhatsapp.setBranchCode(req.getBranchCode());
					broWhatsapp.setUpdatedDate(new Date());
					
					brokerWhatsappRepo.saveAndFlush(broWhatsapp);
					
					res.setCommonResponse("Updated Successfully");
					res.setErroCode(0);
					res.setErrorMessage(Collections.EMPTY_LIST);
					res.setIsError(false);
					res.setMessage("Success");
				}else {
					res.setCommonResponse("Record Not Found");
					res.setErroCode(0);
					res.setErrorMessage(Collections.EMPTY_LIST);
					res.setIsError(false);
					res.setMessage("Success");
				}
			}else {
				res.setCommonResponse(null);
				res.setErroCode(0);
				res.setErrorMessage(validation);
				res.setIsError(false);
				res.setMessage("Success");
			}
			
		}catch(Exception e) {
			res.setCommonResponse(null);
			res.setErroCode(1);
			res.setErrorMessage(Collections.EMPTY_LIST);
			res.setIsError(true);
			res.setMessage(e.getMessage());
		}
		return res;
	}

	private List<Error> updateBrokerWhatsAppValidation(UpdateWhatsappEmployeeReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getLoginId())) {
				errorList.add(new Error("01", "LoginId", "Please Enter the LoginId"));
			}
			
			if(StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("04", "BranchCode", "Please Enter the Branch Code"));
			}else {
				if(StringUtils.isNotBlank(req.getLoginId())) {
					LoginBranchMaster lbm = loginBranchRepo.findByLoginIdAndBranchCode(req.getLoginId(), req.getBranchCode());
					
					if(lbm == null) {
						errorList.add(new Error("04", "BranchCode", "This Branch Code is not available for this broker"));
					}
				}
			}
			
			if(StringUtils.isBlank(req.getWhatsappCode())) {
				errorList.add(new Error("02", "WhatsappCode", "Please Enter the WhatsAppCode"));
			}
			
			if(StringUtils.isBlank(req.getWhatsappNo())) {
				errorList.add(new Error("03", "WhatsappNo", "Please Enter the WhatsApp Number"));
			}else if (req.getWhatsappNo().length() > 10 || req.getWhatsappNo().length() < 8) {
				errorList.add(new Error("03", "WhatsappNo", "Please Enter Valid WhatsApp Number"));
			}else if (!req.getWhatsappNo().matches("[0-9]+")) {
				errorList.add(new Error("03", "WhatsappNo", "Please Enter WhatsApp Number only in numbers"));
			}else if (req.getWhatsappNo().matches("[0-9]+") && Double.valueOf(req.getWhatsappNo()) <= 0) {
				errorList.add(new Error("03", "WhatsappNo", "Please Enter Valid WhatsApp Number"));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return errorList;
	}

}
