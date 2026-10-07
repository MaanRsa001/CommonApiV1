package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.req.calcengine.ReferralApi;
import com.maan.eway.res.calc.AdminReferral;
import com.maan.eway.service.CalculatorEngine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class NotificationThreadCall implements Callable<Object>  {
 
	private Logger log = LogManager.getLogger(getClass());
	private EServiceMotorDetailsRepository eserMotRepo;
	private LoginUserInfoRepository loginUserRepo;
	private EserviceCustomerDetailsRepository eserCustRepo;
	private CalculatorEngine calcEngine;
	private NotificationService notiService;
	private EserviceTravelDetailsRepository eserTraRepo;
	private EserviceBuildingDetailsRepository eserBuildRepo;
	private EserviceCommonDetailsRepository eserCommonRepo;
	
	Gson json = new Gson();
	private String type;
	private NewQuoteReq request ;
	private EntityManager em;
	
	public NotificationThreadCall(String type , NewQuoteReq request , EntityManager em,EServiceMotorDetailsRepository eserMotRepo,
			LoginUserInfoRepository loginUserRepo,EserviceCustomerDetailsRepository eserCustRepo,
			CalculatorEngine calcEngine,NotificationService notiService,EserviceTravelDetailsRepository eserTraRepo,
			EserviceBuildingDetailsRepository eserBuildRepo,EserviceCommonDetailsRepository eserCommonRepo) {
		this.type = type;
		this.request = request;
		this.em=em;
		this.eserMotRepo=eserMotRepo;
		this.loginUserRepo=loginUserRepo;
		this.eserCustRepo=eserCustRepo;
		this.calcEngine=calcEngine;
		this.notiService=notiService;
		this.eserTraRepo=eserTraRepo;
		this.eserBuildRepo=eserBuildRepo;
		this.eserCommonRepo=eserCommonRepo;
	}
	@Override
	public  Map<String, Object>  call() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			type = StringUtils.isBlank(type) ? "" : type;

			log.info("Thread_OneTime--> type: " + type);

			if (type.equalsIgnoreCase("motorPushNotification")) {

				map.put("motorPushNotification", motorPushNotification(request));

			} else if (type.equalsIgnoreCase("travelPushNotification")) {

				map.put("travelPushNotification", travelPushNotification(request));

			} else if (type.equalsIgnoreCase("assetPushNotification")) {

				map.put("assetPushNotification", buildingPushNotification(request));

			} else if (type.equalsIgnoreCase("commonPushNotification")) {

				map.put("commonPushNotification", commonPushNotification(request));

			}
			
			

		} catch (Exception e) {
			log.error(e);
		}
		return map;
	}
	
	// --------------------------------------MOTOR UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
	private QuoteUpdateRes motorPushNotification(NewQuoteReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			List<EserviceMotorDetails> cusRefNo = eserMotRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

			String loginId = "";
			if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
				loginId = cusRefNo.get(0).getLoginId();
			} else {
				loginId = cusRefNo.get(0).getApplicationId();
			}
			Notification n = new Notification();
			//Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			}
			// Customer Info
			EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
			Customer cusReq = new Customer();
			if(customerData!=null) {
				cusReq.setCustomerMailid(customerData.getEmail1());
				cusReq.setCustomerName(customerData.getClientName());
				cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
				cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
				cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());
			}

			// UnderWriter Info Old Setup.
			/*List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
			List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
			if (underWriterList != null) {
				for (Tuple underWriterData : underWriterList) {
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
					underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
					underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
					underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
					underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
					underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
					underWrite.add(underWriterReq);
				}
			}*/
			ReferralApi r=ReferralApi.builder()
							.branchCode(cusRefNo.get(0).getBranchCode())
							.insuranceId(cusRefNo.get(0).getCompanyId())
							.productId(cusRefNo.get(0).getProductId())
							.suminsured(cusRefNo.get(0).getSumInsured())
							
							.build();
			List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
			List<AdminReferral> referalList = calcEngine.getReferalList(r);
			for(AdminReferral ref:referalList) {
				UnderWriter underWriterReq = new UnderWriter();
				underWriterReq.setUwMailid(ref.getMailId());
				underWriterReq.setUwMessengerCode(Integer.parseInt(ref.getMobileCode()));
				underWriterReq.setUwMessengerPhone(new BigDecimal(ref.getMobileNo()));
				underWriterReq.setUwPhonecode(Integer.parseInt(ref.getMobileCode()));
				underWriterReq.setUwPhoneNo(new BigDecimal(ref.getMobileNo()));
				underWriterReq.setUwName(ref.getInsuranceId());
				underWriterReq.setUwLoginId(ref.getLoginId());
				underWriterReq.setUwuserType(ref.getUwuserType());
				underWriterReq.setUwsubuserType(ref.getUwsubuserType());
				underWrite.add(underWriterReq);
			}				 
			n.setUnderwriters(underWrite);
			//Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());
			
			//Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setNotifTemplatename("Referral Pending");
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName("Motor");
			n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
			n.getTinyUrl();
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setRefNo(cusRefNo.get(0).getRequestReferenceNo());

			// Calling pushNotification
			CommonRes res=notiService.pushNotification(n);
//			if (res.getIsError()==null) {
//				updateRes.setResponse("Pushed Successfuly");
//				updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//				updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//				updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
//
//			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	// --------------------------------------TRAVEL UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
			private QuoteUpdateRes travelPushNotification(NewQuoteReq req) {
				QuoteUpdateRes updateRes = new QuoteUpdateRes();
				try {
					List<EserviceTravelDetails> cusRefNo = eserTraRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
					
					cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
							.collect(Collectors.toList());

					String loginId = "";
					if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
						loginId = cusRefNo.get(0).getLoginId();
					} else {
						loginId = cusRefNo.get(0).getApplicationId();
					}
					Notification n = new Notification();
					//Broker Info
					LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
					Broker brokerReq = new Broker();
					if(loginInfo!=null) {
					brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
					brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
					brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
					brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
					brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
					brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
					brokerReq.setBrokerName(loginInfo.getUserName());
					}
					// Customer Info
					EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
					Customer cusReq = new Customer();
					if(customerData!=null) {
						cusReq.setCustomerMailid(customerData.getEmail1());
						cusReq.setCustomerName(customerData.getClientName());
						cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
						cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
						cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
						cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
					}

					// UnderWriter Info
					List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
					List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
					if (underWriterList != null) {
						for (Tuple underWriterData : underWriterList) {
							UnderWriter underWriterReq = new UnderWriter();
							underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
							underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
							underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
							underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
							underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
							underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
							underWrite.add(underWriterReq);
						}
					}
					n.setUnderwriters(underWrite);
					//Company Info
					n.setCompanyid(cusRefNo.get(0).getCompanyId());
					n.setCompanyName(cusRefNo.get(0).getCompanyName());
					
					//Common Info
					n.setBroker(brokerReq);
					n.setCustomer(cusReq);
					n.setNotifcationDate(new Date());
					n.setNotifDescription("");
					n.setNotifPriority(0);
					n.setNotifPushedStatus(NotificationStatus.PENDING);
					n.setNotifTemplatename("Referral Penidng");
					n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
					n.setProductid(Integer.valueOf(req.getProductId()));
					n.setProductName("Travel");
					n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
					n.setSectionName(cusRefNo.get(0).getSectionName());
					n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
					n.getTinyUrl();

					// Calling pushNotification
					CommonRes res=notiService.pushNotification(n);
//					if (res.getIsError()==null) {
//						updateRes.setResponse("Pushed Successfuly");
//						updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//						updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//						updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
	//
//					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return updateRes;
			}
			// --------------------------------------BUILDING UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
			private QuoteUpdateRes buildingPushNotification(NewQuoteReq req) {
				QuoteUpdateRes updateRes = new QuoteUpdateRes();
				try {
					List<EserviceBuildingDetails> cusRefNo = eserBuildRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
					
					cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
							.collect(Collectors.toList());

					String loginId = "";
					if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
						loginId = cusRefNo.get(0).getLoginId();
					} else {
						loginId = cusRefNo.get(0).getApplicationId();
					}
					Notification n = new Notification();
					//Broker Info
					LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
					Broker brokerReq = new Broker();
					if(loginInfo!=null) {
					brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
					brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
					brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
					brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
					brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
					brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
					brokerReq.setBrokerName(loginInfo.getUserName());
					}
					// Customer Info
					EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
					Customer cusReq = new Customer();
					if(customerData!=null) {
						cusReq.setCustomerMailid(customerData.getEmail1());
						cusReq.setCustomerName(customerData.getClientName());
						cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
						cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
						cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
						cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
					}

					// UnderWriter Info
					List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
					List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
					if (underWriterList != null) {
						for (Tuple underWriterData : underWriterList) {
							UnderWriter underWriterReq = new UnderWriter();
							underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
							underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
							underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
							underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
							underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
							underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
							underWrite.add(underWriterReq);
						}
					}
					n.setUnderwriters(underWrite);
					//Company Info
					n.setCompanyid(cusRefNo.get(0).getCompanyId());
					n.setCompanyName(cusRefNo.get(0).getCompanyName());
					
					//Common Info
					n.setBroker(brokerReq);
					n.setCustomer(cusReq);
					n.setNotifcationDate(new Date());
					n.setNotifDescription("");
					n.setNotifPriority(0);
					n.setNotifPushedStatus(NotificationStatus.PENDING);
					n.setNotifTemplatename("Referral Pending");
					n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
					n.setProductid(Integer.valueOf(req.getProductId()));
					n.setProductName("Buliding");
					n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
					n.setSectionName(cusRefNo.get(0).getSectionDesc());
					n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
					n.getTinyUrl();

					// Calling pushNotification
					CommonRes res=notiService.pushNotification(n);
//					if (res.getIsError()==null) {
//						updateRes.setResponse("Pushed Successfuly");
//						updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//						updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//						updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
	//
//					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return updateRes;
			}

			// -------------------------------------- COMMON UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
			private QuoteUpdateRes commonPushNotification(NewQuoteReq req) {
				QuoteUpdateRes updateRes = new QuoteUpdateRes();
				try {
					List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
					
					cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
							.collect(Collectors.toList());

					String loginId = "";
					if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
						loginId = cusRefNo.get(0).getLoginId();
					} else {
						loginId = cusRefNo.get(0).getApplicationId();
					}
					Notification n = new Notification();
					//Broker Info
					LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
					Broker brokerReq = new Broker();
					if(loginInfo!=null) {
					brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
					brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
					brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
					brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
					brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
					brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
					brokerReq.setBrokerName(loginInfo.getUserName());
					}
					// Customer Info
					EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
					Customer cusReq = new Customer();
					if(customerData!=null) {
						cusReq.setCustomerMailid(customerData.getEmail1());
						cusReq.setCustomerName(customerData.getClientName());
						cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
						cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
						cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
						cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
					}

					// UnderWriter Info
					List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
					List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
					if (underWriterList != null) {
						for (Tuple underWriterData : underWriterList) {
							UnderWriter underWriterReq = new UnderWriter();
							underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
							underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
							underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
							underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
							underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
							underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
							underWrite.add(underWriterReq);
						}
					}
					n.setUnderwriters(underWrite);
					//Company Info
					n.setCompanyid(cusRefNo.get(0).getCompanyId());
					n.setCompanyName(cusRefNo.get(0).getCompanyName());
					
					//Common Info
					n.setBroker(brokerReq);
					n.setCustomer(cusReq);
					n.setNotifcationDate(new Date());
					n.setNotifDescription("");
					n.setNotifPriority(0);
					n.setNotifPushedStatus(NotificationStatus.PENDING);
					n.setNotifTemplatename("Referral Pending");
					n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
					n.setProductid(Integer.valueOf(req.getProductId()));
				//	 ProductMaster productData= getByProductCode(Integer.valueOf(req.getProductId())) ;
					n.setProductName(cusRefNo.get(0).getProductDesc());
					n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
					n.setSectionName(cusRefNo.get(0).getSectionName());
					n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
					n.getTinyUrl();

					// Calling pushNotification
					CommonRes res=notiService.pushNotification(n);
//					if (res.getIsError()==null) {
//						updateRes.setResponse("Pushed Successfuly");
//						updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//						updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//						updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
	//
//					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return updateRes;
			}
			private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
			    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
			    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
			}
			private List<Tuple> getUnderWriterDetails(String productId,String companyId,String branchCode,String loginId) {
				List<Tuple> list = new ArrayList<Tuple>();
				try {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
					
					Root<LoginMaster> l = query.from(LoginMaster.class);
					Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);
					Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
					Root<LoginProductMaster> p = query.from(LoginProductMaster.class);
					query.multiselect( u.get("loginId").alias("loginId"), u.get("oaCode").alias("oaCode"), u.get("acExecutiveId").alias("acExecutiveId"),u.get("address1").alias("address1"), 
							   u.get("address2").alias("address2"),u.get("address3").alias("address3"), 
							   u.get("agencyCode").alias("agencyCode"),u.get("approvedPreparedBy").alias("approvedPreparedBy"), 
							   u.get("branchCode").alias("branchCode"),u.get("checkerYn").alias("checker"), 
							   u.get("cityCode").alias("cityCode"),u.get("cityName").alias("cityName"), 
							   u.get("commissionVatYn").alias("commissionVatYn"),u.get("companyName").alias("companyName"), 
							   u.get("contactPersonName").alias("contactPersonName"),u.get("coreAppBrokerCode").alias("coreAppBrokerCode"), 
							   u.get("countryCode").alias("countryCode"),u.get("countryName").alias("countryName"), 
							   u.get("createdBy").alias("createdBy"),u.get("custConfirmYn").alias("custConfirmYn"), 
							   u.get("customerId").alias("customerId"),u.get("designation").alias("designation"), 
							   u.get("effectiveDateStart").alias("effectiveDateStart"), 
							   u.get("entryDate").alias("entryDate"),u.get("fax").alias("fax"), 
							   u.get("makerYn").alias("makerYn"),u.get("missippiId").alias("missippiId"), 
							   u.get("mobileCode").alias("mobileCode"),u.get("mobileCodeDesc").alias("mobileCodeDesc"), 
							   u.get("pobox").alias("pobox"),u.get("remarks").alias("remarks"), 
							   u.get("stateCode").alias("stateCode"),u.get("stateName").alias("stateName"), 
							   u.get("status").alias("status"),u.get("updatedBy").alias("updatedBy"), 
							   u.get("updatedDate").alias("updatedDate"),u.get("userMail").alias("userMail"), 
							   u.get("userMobile").alias("userMobile"),u.get("userName").alias("userName"), 
							   u.get("vatRegNo").alias("vatRegNo"),u.get("whatsappCode").alias("whatsappCode"),
							   u.get("whatsappCodeDesc").alias("whatsappCodeDesc"),u.get("whatsappNo").alias("whatsappNo"));			
					List<String> subUserType = new ArrayList<String>(); 
					subUserType.add("high");
					subUserType.add("both");
					//In 
					Expression<String>e0=cb.lower(l.get("subUserType"));
					//Where
					Predicate n1 = cb.equal(cb.lower(l.get("userType")), "issuer");
					Predicate n2 = e0.in(subUserType);
					Predicate n3 = cb.equal(l.get("companyId"),companyId);
					Predicate n4 = cb.equal(l.get("loginId"),u.get("loginId"));
					Predicate n5 = cb.equal(b.get("loginId"),(l.get("loginId")));
					Predicate n6 = cb.equal(p.get("loginId"),(l.get("loginId")));
					Predicate n7 = cb.equal(b.get("branchCode"),branchCode);
					Predicate n8 = cb.equal(p.get("productId"),productId);
					Calendar cal = new GregorianCalendar();
					Date today = new Date();
					cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
					today = cal.getTime();
					Predicate n9 = cb.between(cb.literal(today),p.get("effectiveDateStart"), p.get("effectiveDateEnd"));
					query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9);
					TypedQuery<Tuple> result = em.createQuery(query);
					list = result.getResultList();
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return list;
			}
}
