package com.maan.eway.notification.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.bean.SmsDataDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.bean.MailDataDetails;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.MailDataDetailsRepository;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.DirectMailSentReq;
import com.maan.eway.notification.req.DirectSmsSentReq;
import com.maan.eway.notification.req.NotifGetByIdReq;
import com.maan.eway.notification.req.NotifGetByQuoteNoReq;
import com.maan.eway.notification.req.NotifGetReq;
import com.maan.eway.notification.req.NotifTemplateGetReq;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.NotificationFrameReq;
import com.maan.eway.notification.req.TemplatesDropDownReq;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.res.MailNotifGetRes;
import com.maan.eway.notification.res.MailTemplateRes;
import com.maan.eway.notification.res.NofiByQuoteNoRes;
import com.maan.eway.notification.res.SmsNofiGetRes;
import com.maan.eway.notification.res.SmsTemplateRes;
import com.maan.eway.notification.service.NotifTemplateService;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.notification.service.NotificationValidation;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MailMasterRepository;
import com.maan.eway.repository.SmsConfigMasterRepository;
import com.maan.eway.repository.SmsDataDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.upgrade.criteria.CriteriaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class NotifTemplateServiceImpl implements  NotifTemplateService {
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(NotifTemplateServiceImpl.class);

	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	
	@Autowired
	protected CriteriaService crservice;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserviceTravelRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerDetailsRepo;
	
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	
	@Autowired 
	private MailMasterRepository mailRepo;
	
	@Autowired
	private SmsConfigMasterRepository smsRepo;
	
	@Autowired 
	private MailDataDetailsRepository mailDataRepo;
	
	@Autowired
	private SmsDataDetailsRepository smsDataRepo;
	
	@Autowired
	private NotifTransactionDetailsRepository notifTrans ; 
	
	@Autowired
	private InsuranceCompanyMasterRepository companyRepo;
	
	@Autowired
	private NotificationValidation vad;
	
	
	@Override
	public List<DropDownRes> getTemplatesDropDown(TemplatesDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<NotifTemplateMaster> query = cb.createQuery(NotifTemplateMaster.class);
			List<NotifTemplateMaster> list = new ArrayList<NotifTemplateMaster>();

			// Find All
			Root<NotifTemplateMaster> c = query.from(NotifTemplateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("notifTemplatename")));
    
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("notifTemplateCode"), ocpm1.get("notifTemplateCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			effectiveDate.where(a1, a2, a3, a4);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<NotifTemplateMaster> ocpm2 = effectiveDate2.from(NotifTemplateMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a6 = cb.equal(c.get("notifTemplateCode"), ocpm2.get("notifTemplateCode"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a9 = cb.greaterThanOrEqualTo(c.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6, a7, a8 ,a9 );

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n6 = null ;
			if (req.getNotifApplicable().equalsIgnoreCase("Mail")  ) {
				n6 = cb.equal(c.get("mailRequired"), "Y");
				
			} else if(req.getNotifApplicable().equalsIgnoreCase("Sms")  ) {
				n6 = cb.equal(c.get("smsRequired"), "Y");
				
			}
			query.where(n1, n2, n3,n4, n5, n6).orderBy(orderList);

			// Get Result
			TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
			list = result.getResultList();

			for (NotifTemplateMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getNotifTemplateCode().toString());
				res.setCodeDesc(data.getNotifTemplatename());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}
	
	public NotifTemplateMaster getTemplateDetails(NotifTemplateGetReq req) {
		NotifTemplateMaster data = new NotifTemplateMaster();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<NotifTemplateMaster> query = cb.createQuery(NotifTemplateMaster.class);
			

			// Find All
			Root<NotifTemplateMaster> c = query.from(NotifTemplateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("notifTemplatename")));
    
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("notifTemplateCode"), ocpm1.get("notifTemplateCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			effectiveDate.where(a1, a2, a3, a4);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<NotifTemplateMaster> ocpm2 = effectiveDate2.from(NotifTemplateMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a6 = cb.equal(c.get("notifTemplateCode"), ocpm2.get("notifTemplateCode"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a9 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"), todayEnd);
			effectiveDate2.where(a6, a7, a8 ,a9);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n6 = cb.equal(c.get("notifTemplateCode"), req.getNotifTemplateCode() );
			
			query.where(n1, n2, n3,n4, n5, n6).orderBy(orderList);

			// Get Result
			TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
			data = result.getResultList().get(0) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return data;
	}

	@Override
	public CommonRes getMailTemplate(NotifTemplateGetReq req) {
		CommonRes res = new CommonRes();
		MailTemplateRes mailTemplateRes = new MailTemplateRes();
		try {
			NotifTemplateMaster template = getTemplateDetails(req) ;
			
			NotificationFrameReq nf = new NotificationFrameReq();
			nf.setInsuranceId(req.getInsuranceId());
			nf.setNotifTemplateCode(template.getNotifTemplateCode().toString());
			nf.setNotifTemplateName(template.getNotifTemplatename());
			nf.setProductId(req.getProductId() );
			nf.setRequestReferenceNo(req.getRequestReferenceNo());;
		//	nf.setRemarks(req.getRemarks());
			nf.setCreatedBy(req.getCreatedBy());
	/*		CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

			if(product.getMotorYn().equalsIgnoreCase("M") ) {
				res =	motorPushNotification(nf);

			} else if(product.getMotorYn().equalsIgnoreCase("H")  &&  req.getProductId().equalsIgnoreCase(travelProductId)) {
				res =  travelPushNotification(nf);
				
			} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
				res = buildingPushNotification(nf);
			}  else {
			
				res = commonPushNotification(nf);
			} 
			if(res.getIsError()!=null && res.getIsError() == true ) {
				return res ;
			}
			
			
			NotifTransactionDetails ne = (NotifTransactionDetails) res.getCommonResponse() ;
		    Tuple t =  loadNotificationPending(ne.getNotifNo()).get(0);
			*/
			String mailBody=template.getMailBody();
			String mailSubject=template.getMailSubject();
			String mailRegards=template.getMailRegards();
			
			if(req.getAdditionalInfo()!=null && !req.getAdditionalInfo().isEmpty()) {
				mailBody=(String) getContentFrame(req.getAdditionalInfo(), template.getMailBody());
				mailSubject=(String) getContentFrame(req.getAdditionalInfo(), template.getMailSubject());
				mailRegards=(String) getContentFrame(req.getAdditionalInfo(), template.getMailRegards());
			}
			mailTemplateRes.setMailBody(mailBody);
			mailTemplateRes.setMailSubject(mailSubject);
			mailTemplateRes.setMailRegards(mailRegards);
			//mailTemplateRes.setNotificationNo(ne.getNotifNo()==null?"":String.valueOf(ne.getNotifNo()));
			mailTemplateRes.setNotifTemplateCode(req.getNotifTemplateCode());
			res.setCommonResponse(mailTemplateRes);
			res.setIsError(false);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			
			return res;
		}
		return res;
	}
	

	

	@Override
	public CommonRes getSmsTemplate(NotifTemplateGetReq req) {
		CommonRes res = new CommonRes();
		SmsTemplateRes smsTemplateRes = new SmsTemplateRes();
		try {
			NotifTemplateMaster template = getTemplateDetails(req) ;
			
			NotificationFrameReq nf = new NotificationFrameReq();
			nf.setInsuranceId(req.getInsuranceId());
			nf.setNotifTemplateCode(template.getNotifTemplateCode().toString());
			nf.setNotifTemplateName(template.getNotifTemplatename());
			nf.setProductId(req.getProductId() );
			nf.setRequestReferenceNo(req.getRequestReferenceNo());
			nf.setCreatedBy(req.getCreatedBy());
	//		nf.setRemarks(req.getRemarks());
		/*	CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

			if(product.getMotorYn().equalsIgnoreCase("M") ) {
				res = 	motorPushNotification(nf);

			} else if(product.getMotorYn().equalsIgnoreCase("H")  && req.getProductId().equalsIgnoreCase(travelProductId)) {
				res = travelPushNotification(nf);
				
			} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
				res = buildingPushNotification(nf);
			}  else {
			
				res = commonPushNotification(nf);
			} 
			NotifTransactionDetails ne = (NotifTransactionDetails) res.getCommonResponse() ;
			Tuple t =  loadNotificationPending(ne.getNotifNo()).get(0);*/
			String smsBody=template.getSmsBodyEn();
			String smsSubject=template.getSmsSubject();
			String smsRegards=template.getSmsRegards();
			
			if(req.getAdditionalInfo()!=null && !req.getAdditionalInfo().isEmpty()) {
				smsBody=(String) getContentFrame(req.getAdditionalInfo(), template.getSmsBodyEn());
				smsSubject=(String) getContentFrame(req.getAdditionalInfo(),template.getSmsSubject());
				smsRegards=(String) getContentFrame(req.getAdditionalInfo(),template.getSmsRegards());
			}			
			
			smsTemplateRes.setSmsBody(smsBody);
			smsTemplateRes.setSmsSubject(smsSubject);
			smsTemplateRes.setSmsRegards(smsRegards);
			//smsTemplateRes.setNotificationNo(ne.getNotifNo()==null?"":String.valueOf(ne.getNotifNo()));
			smsTemplateRes.setNotifTemplateCode(req.getNotifTemplateCode());
			res.setCommonResponse(smsTemplateRes);
			res.setIsError(false);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			return res ;
		}
		return res;
	}
	
	public List<Tuple> loadNotificationPending(Long notifNo) {
		 List<Tuple> list = new ArrayList<Tuple>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			// Find All
			Root<NotifTransactionDetails> b = query.from(NotifTransactionDetails.class);
			query.multiselect( b.get("notifNo").alias("notifNo") ,
   			    b.get("customerName").alias("customerName") ,
			    b.get("customerMailid").alias("customerMailid"),
			    b.get("customerPhoneNo").alias("customerPhoneNo"),
			    b.get("customerPhoneCode").alias("customerPhoneCode"),
			    b.get("customerMessengerCode").alias("customerMessengerCode"),
			    b.get("customerMessengerPhone").alias("customerMessengerPhone"),
			    b.get("brokerName").alias("brokerName"),
			    b.get("brokerCompanyName").alias("brokerCompanyName"),
			    b.get("brokerMailId").alias("brokerMailId"),
			    b.get("brokerPhoneNo").alias("brokerPhoneNo"),
			    b.get("brokerPhoneCode").alias("brokerPhoneCode"),
			    b.get("brokerMessengerCode").alias("brokerMessengerCode"),
			    b.get("brokerMessengerPhone").alias("brokerMessengerPhone"),
			    b.get("uwName").alias("uwName"),
			    b.get("uwMailid").alias("uwMailid"),
			    b.get("uwPhonecode").alias("uwPhonecode"),
			    b.get("uwPhoneNo").alias("uwPhoneNo"),
			    b.get("uwMessengerCode").alias("uwMessengerCode"),
			    b.get("uwMessengerPhone").alias("uwMessengerPhone"),
			    b.get("companyName").alias("companyName"),
			    b.get("productName").alias("productName"),
			    b.get("sectionName").alias("sectionName"),
			    b.get("statusMessage").alias("statusMessage"),
			    b.get("otp").alias("otp"),
			    b.get("policyNo").alias("policyNo"),
			    b.get("quoteNo").alias("quoteNo"),
			    b.get("notifDescription").alias("notifDescription"),
			    b.get("notifTemplatename").alias("notifTemplatename"),
			    b.get("entryDate").alias("entryDate"),
			    b.get("notifcationPushDate").alias("notifcationPushDate"),
			    b.get("notifcationEndDate").alias("notifcationEndDate"),
			    b.get("notifPushedStatus").alias("notifPushedStatus"),
			    b.get("notifPriority").alias("notifPriority"),
			    b.get("tinyUrl").alias("tinyUrl"),
			    b.get("companyid").alias("companyid"),
			    b.get("productid").alias("productid"),
			    b.get("companyAddress").alias("companyAddress"),
			    b.get("companyLogo").alias("companyLogo"),
			    b.get("attachFilePath").alias("attachFilePath"),
			    b.get("pushedBy").alias("pushedBy") );
			
			// Where
			Predicate n1 = cb.equal(b.get("notifNo"), notifNo);
			
			query.where(n1);

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return list;
		
	}
	
	// --------------------------------------MOTOR UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
	private CommonRes motorPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
		try {
			List<EserviceMotorDetails> cusRefNo = eserMotRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

			String loginId = "";
			if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
				loginId = cusRefNo.get(0).getLoginId();
			} else {
				loginId = cusRefNo.get(0).getApplicationId();
			}
			Notification n = new Notification();
			//Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			}
			// Customer Info
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
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
	
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
		
			//Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductName());
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.setPushedBy(req.getCreatedBy());
			n.getTinyUrl();

			// Calling pushNotification
			res= notiService.pushNotification(n);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	
	}
	

	// --------------------------------------TRAVEL UPDATE REFERRAL STATUS----------------------------------------------------------------------//
	private CommonRes travelPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
		try {
			
			List<EserviceTravelDetails> cusRefNo = eserviceTravelRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

			String loginId = "";
			if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
				loginId = cusRefNo.get(0).getLoginId();
			} else {
				loginId = cusRefNo.get(0).getApplicationId();
			}
			Notification n = new Notification();
			// Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			}
			// Customer Info
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
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
			List<Tuple> underWriterList = getUnderWriterDetails(cusRefNo.get(0).getProductId(),
					cusRefNo.get(0).getCompanyId(), cusRefNo.get(0).getBranchCode(), cusRefNo.get(0).getLoginId());
			List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
			if (underWriterList != null) {
				for (Tuple underWriterData : underWriterList) {
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "" : underWriterData.get("userMail").toString());
					underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc") == null ? null: Integer.valueOf(underWriterData.get("whatsappCodeDesc").toString()));
					underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo") == null ? BigDecimal.ZERO: new BigDecimal(underWriterData.get("whatsappNo").toString()));
					underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc") == null ? null: Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
					underWriterReq.setUwPhoneNo(underWriterData.get("userMobile") == null ? BigDecimal.ZERO: new BigDecimal(underWriterData.get("userMobile").toString()));
					underWriterReq.setUwName(underWriterData.get("userName") == null ? "" : underWriterData.get("userName").toString());
					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			// Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());

			// Common Info
			
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductName());
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.getTinyUrl();
			
			n.setPushedBy(req.getCreatedBy());
			// Calling pushNotification
			res = notiService.pushNotification(n);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	//----------------------------------------BUILDING UPDATE REFERRAL STATUS ------------------------------------------------------------------//
	private CommonRes buildingPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
		try {
			List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

			String loginId = "";
			if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
				loginId = cusRefNo.get(0).getLoginId();
			} else {
				loginId = cusRefNo.get(0).getApplicationId();
			}
			Notification n = new Notification();

			// Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			}
			// Customer Info
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
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
			List<Tuple> underWriterList = getUnderWriterDetails(cusRefNo.get(0).getProductId(),
					cusRefNo.get(0).getCompanyId(), cusRefNo.get(0).getBranchCode(), cusRefNo.get(0).getLoginId());
			List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
			if (underWriterList != null) {
				for (Tuple underWriterData : underWriterList) {
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(
							underWriterData.get("userMail") == null ? "" : underWriterData.get("userMail").toString());
					underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc") == null ? null
							: Integer.valueOf(underWriterData.get("whatsappCodeDesc").toString()));
					underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo") == null ? BigDecimal.ZERO
							: new BigDecimal(underWriterData.get("whatsappNo").toString()));
					underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc") == null ? null
							: Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
					underWriterReq.setUwPhoneNo(underWriterData.get("userMobile") == null ? BigDecimal.ZERO
							: new BigDecimal(underWriterData.get("userMobile").toString()));
					underWriterReq.setUwName(
							underWriterData.get("userName") == null ? "" : underWriterData.get("userName").toString());
					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			// Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());

			// Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductDesc());
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
			n.setSectionName(cusRefNo.get(0).getSectionDesc());
			n.setPushedBy(req.getCreatedBy());
			n.getTinyUrl();

			// Calling pushNotification
			res = notiService.pushNotification(n);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	// -------------------------------------- COMMON UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
	private CommonRes commonPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
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
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
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
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductDesc());
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.setPushedBy(req.getCreatedBy());
			// Referral Noti , referral app,recj
			n.getTinyUrl();

			// Calling pushNotification
			res= notiService.pushNotification(n);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
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

	private Object getContentFrame(Map<String, Object> t ,String messageTemplate) {
		try {
			 
			if(StringUtils.isNotBlank(messageTemplate)) {
				StringBuffer b=new StringBuffer(messageTemplate);
				while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
					String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
					b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(t.get(tx)));
				} 
				return b.toString();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	/*
	private String getTemplateFrame(Tuple t ,NotifTemplateMaster m , String mailBody , 	String mailSubject,	String mailRegards) {
		try {
			 String baseTemplate="<div style=\"margin: 0px auto;width: 700px;max-width: 90%;padding-top: 20px;background-color: rgb(255,255,255);\">\r\n"
			 		+ "        <div style=\"text-align: center; margin-bottom: 20px;\"> <img height=\"20px\"> </div>\r\n"
			 		+ "<div class=\"adM\" style=\"text-align: center;\"><img src=\"{xCompanyLogox}\">\r\n"
			 		+ "        \r\n"
			 		+ "        </div>"
			 		+ "        <div style=\"margin: 0px auto; width: 100%; line-height: 1.3;\"> <img width=\"100%\">\r\n"
			 		+ "          <div style=\"padding: 20px 30px;\">\r\n"
			 		+ "            <p style=\"text-transform: capitalize;\">Hi {xCustomerx},</p>\r\n"
			 		+ "            <p style=\"\r\n"
			 		+ "    font-size: 17px;\r\n"
			 		+ "\">{xsubjectx}</p>\r\n"
			 		+ "            <div style=\"border: 1px solid rgb(221, 221, 221); padding: 20px;\">\r\n"
			 		+ "              {xmailBodyx}"
			 		+ "                \r\n"
			 		+ "            </div>\r\n"
			 		+ "            <div>\r\n"
			 		+ "              <p style=\"font-weight: bold;\">Why {companyName}?</p>\r\n"
			 		+ "              <ul>\r\n"
			 		+ "                <li>Sample Text -1.</li>\r\n"
			 		+ "                <li>Sample Text -2.</li>\r\n"
			 		+ "                <li>Sample Text -3.</li>\r\n"
			 		+ "              </ul>\r\n"
			 		+ "              <p style=\"font-weight: bold;\">What’s next?</p>\r\n"
			 		+ "              <p>Paragraph Text</p>\r\n"
			 		+ "              <ul>\r\n"
			 		+ "                <li>Sample Text</li>\r\n"
			 		+ "                <li>Sample Text</li>\r\n"
			 		+ "              </ul>\r\n"
			 		+ "              <p style=\"font-size: 1.3em; text-align: center; font-weight: bold;\">That's all, it’s that simple.</p>\r\n"
			 		+ "            </div>\r\n"
			 		+ "             \r\n"
			 		+ "             \r\n"
			 		+ "            <p style=\"margin-top: 30px;\">We care,</p>\r\n"
			 		+ "            <p>{xregardsx} Team</p>\r\n"
			 		+ "            <div style=\"font-size: 0.8em; color: rgba(0, 0, 0, 0.4); margin-top: 30px;\">\r\n"
			 		+ "              <p>Your premium may need to be adjusted if the provided\r\n"
			 		+ "                information is incorrect.</p>\r\n"
			 		+ "            </div>\r\n"
			 		+ "            <div style=\"color: rgba(0, 0, 0, 0.4); font-size: 0.8em; border-top: 1px solid; margin-top: 30px; line-height: 0.9em; text-align: center; padding: 30px 0px;\">\r\n"
			 		+ "              <p><a href='#'/>\r\n"
			 		+ "               {xCompanyAddressx}</p>\r\n"
			 		+ "              <div>\r\n"
			 		+ "                <p style=\"font-weight: bold; color: rgb(0, 0, 0); margin-top: 20px;\">Connect with us</p>\r\n"
			 		+ "                </div>\r\n"
			 		+ "            </div>\r\n"
			 		+ "          </div>\r\n"
			 		+ "        </div>\r\n"
			 		+ "      </div>";
				String xCustomerx="Team";
				if("customerMailid".equals(m.getToEmail())) {
					xCustomerx=(t.get("customerName")==null || StringUtils.isBlank(t.get("customerName").toString()))?"Team":t.get("customerName").toString();
				}else if("brokerMailId".equals(m.getToEmail())) {
					xCustomerx=(t.get("brokerName")==null || StringUtils.isBlank(t.get("brokerName").toString()))?"Team":t.get("brokerName").toString();
				}else if("uwMailid".equals(m.getToEmail())) {
					xCustomerx=(t.get("uwName")==null || StringUtils.isBlank(t.get("uwName").toString()))?"Team":t.get("uwName").toString();
				}
				
				Map<String,String> hmap=new HashMap<String,String>();
				hmap.put("xregardsx", mailRegards);
				hmap.put("xmailBodyx", mailBody);
				hmap.put("xCustomerx", xCustomerx);
				hmap.put("xsubjectx", mailSubject);
				hmap.put("xCompanyLogox", String.valueOf(t.get("companyLogo")));
				hmap.put("xCompanyAddressx", String.valueOf(t.get("companyAddress")));
				
			StringBuffer b=new StringBuffer(baseTemplate);
			while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
				 String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
				 b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(hmap.get(tx)==null?t.get(tx):hmap.get(tx)));
			} 
			return b.toString(); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/

	@Override
	public CommonRes sentDirectMail(DirectMailSentReq req) {
		CommonRes res = new CommonRes();
		//SuccessRes response = new SuccessRes();
		try {
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(req.getCustomerReferenceNo());
			LoginUserInfo brokerDetail = loginUserRepo.findByLoginId(req.getCreatedBy());
			Customer customer = new Customer();
			Broker broker = new Broker();
			if(customerData != null) {
				
				customer.setCustomerName(customerData.getClientName());
				customer.setCustomerMailid(customerData.getEmail1());
				customer.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				customer.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCode1()));
				customer.setCustomerMessengerPhone(new BigDecimal(customerData.getMobileNo1()));
				customer.setCustomerMessengerCode(Integer.valueOf(customerData.getMobileCode1()));
				customer.setCustomerRefno(customerData.getCustomerReferenceNo());		
				
			}
			if(brokerDetail != null) {
				broker.setBrokerName(brokerDetail.getUserName());
				broker.setBrokerCompanyName(brokerDetail.getCompanyName());
				broker.setBrokerMailId(brokerDetail.getUserMail());
				broker.setBrokerPhoneNo(new BigDecimal(brokerDetail.getUserMobile()));
				broker.setBrokerPhoneCode(Integer.valueOf(brokerDetail.getMobileCodeDesc()));
				broker.setBrokerMessengerCode(Integer.valueOf(brokerDetail.getMobileCodeDesc()));
				broker.setBrokerMessengerPhone(new BigDecimal(brokerDetail.getUserMobile()));
				broker.setBrokerLoginId(brokerDetail.getLoginId());
				//broker.setBrokeruserType(brokerDetail.get);
				//broker.setBrokersubuserType(travelProductId);
			}
			
 			Notification n=Notification.builder()
					.attachments(req.getFileAttachment())
					.branchCode(null)
					.broker(broker)
					.companyid(req.getInsuranceId())
					.companyName(null)
					.customer(customer)
					.notifcationDate(new Date())
					.notifDescription(req.getMailBody())
					.notifPriority(1)
					.notifPushedStatus(NotificationStatus.PENDING)
					.notifTemplatename(req.getNotifTemplateDesc())
					.otp(null)
					.policyNo(null)
					.productid(Integer.parseInt(req.getProductId()))
					.productName(null)
					.pushedBy(req.getCreatedBy())
					.quoteNo(req.getRequestReferenceNo())
					.refNo(req.getRequestReferenceNo())
					.sectionName(null)
					.tinyUrl(null)					
					.build();
			 res = notiService.pushNotification(n); 
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			return res ;
		}
		return res;
	}
	
	private Object getValue(Tuple t, String fieldNameString) {
		 try {
			Object o=(Object) t.get(fieldNameString);
			if (o instanceof BigDecimal) {
				return o.toString();
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	@Override
	public CommonRes sentDirectSms(DirectSmsSentReq req) {
		CommonRes res = new CommonRes();
		SuccessRes response = new SuccessRes();
		try {
			String smsBody= req.getSmsBody();
			
				EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(req.getCustomerReferenceNo());
				LoginUserInfo brokerDetail = loginUserRepo.findByLoginId(req.getCreatedBy());
				Customer customer = new Customer();
				Broker broker = new Broker();
				if(customerData != null) {
					
					customer.setCustomerName(customerData.getClientName());
					customer.setCustomerMailid(customerData.getEmail1());
					customer.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
					customer.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCode1()));
					customer.setCustomerMessengerPhone(new BigDecimal(customerData.getMobileNo1()));
					customer.setCustomerMessengerCode(Integer.valueOf(customerData.getMobileCode1()));
					customer.setCustomerRefno(customerData.getCustomerReferenceNo());		
					
				}
				if(brokerDetail != null) {
					broker.setBrokerName(brokerDetail.getUserName());
					broker.setBrokerCompanyName(brokerDetail.getCompanyName());
					broker.setBrokerMailId(brokerDetail.getUserMail());
					broker.setBrokerPhoneNo(new BigDecimal(brokerDetail.getUserMobile()));
					broker.setBrokerPhoneCode(Integer.valueOf(brokerDetail.getMobileCodeDesc()));
					broker.setBrokerMessengerCode(Integer.valueOf(brokerDetail.getMobileCodeDesc()));
					broker.setBrokerMessengerPhone(new BigDecimal(brokerDetail.getUserMobile()));
					broker.setBrokerLoginId(brokerDetail.getLoginId());
					//broker.setBrokeruserType(brokerDetail.get);
					//broker.setBrokersubuserType(travelProductId);
				}
				
	 			Notification n=Notification.builder()
						.attachments(null)
						.branchCode(null)
						.broker(broker)
						.companyid(req.getInsuranceId())
						.companyName(null)
						.customer(customer)
						.notifcationDate(new Date())
						.notifDescription(req.getSmsBody())
						.notifPriority(1)
						.notifPushedStatus(NotificationStatus.PENDING)
						.notifTemplatename(req.getNotifTemplateDesc())
						.otp(null)
						.policyNo(null)
						.productid(Integer.parseInt(req.getProductId()))
						.productName(null)
						.pushedBy(req.getCreatedBy())
						.quoteNo(req.getRequestReferenceNo())
						.refNo(req.getRequestReferenceNo())
						.sectionName(null)
						.tinyUrl(null)					
						.build();
				 res = notiService.pushNotification(n); 
				 Long notifNo = Long.valueOf(9999);
				if(res != null) {
					Object resResult = res.getCommonResponse();
					if (resResult instanceof NotifTransactionDetails) {
				        NotifTransactionDetails details = (NotifTransactionDetails) resResult;
				         notifNo = details.getNotifNo()== null ? 99999 :Long.valueOf(details.getNotifNo().toString());   // or String based on your field type
				        System.out.println("notifNo = " + notifNo);
				    }
					System.out.println(resResult);
					}
				 SmsConfigMaster smsc = smsRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(req.getInsuranceId(),"99999","Y").get(0);
			
				// Save Sms Data Details
					SmsDataDetails savedata = new SmsDataDetails();

					Long sno = smsDataRepo.findMaxSNo();
					sno=sno+1;
					savedata.setMobileNo(customer.getCustomerPhoneNo().toString());
					savedata.setSmsFrom(smsc.getSenderId());		
					savedata.setSmsType(req.getSmsSubject());
					savedata.setSmsContent(smsBody);
					savedata.setEntryDate(new Date());
					savedata.setSNo(sno.toString());
					savedata.setResMessage("Pending");
					savedata.setResStatus("P");
					savedata.setReqTime(new Date());
					savedata.setResTime(new Date());
					savedata.setNotifNo(notifNo);
					savedata.setPushedBy(req.getCreatedBy());
					savedata.setSmsRegards(req.getSmsRegards());
					smsDataRepo.saveAndFlush(savedata);
/*		String smsSubject= req.getSmsSubject();
			String smsRegards=req.getSmsRegards();
		
			NotifTemplateGetReq ntr = new NotifTemplateGetReq();
			ntr.setNotifTemplateCode(req.getNotifTemplateCode());
			ntr.setInsuranceId(req.getInsuranceId());
			ntr.setProductId(req.getProductId());
			
			NotifTemplateMaster template = getTemplateDetails(ntr) ;
			Tuple t =  loadNotificationPending(Integer.valueOf( req.getNotificationNo())).get(0);
			SmsConfigMaster smsc = smsRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(req.getInsuranceId(),"99999","Y").get(0);													
			
			Sms m=Sms.builder()
					.smsBody(smsBody)
					.smsRegards(smsRegards)
					.smsSubject(smsSubject)
					.smsTo(t.get("customerPhoneNo").toString() )	
					.smsFrom(smsc.getSenderId())
					.credential(JobCredentials.builder().host(smsc.getSmsPartyUrl()).isSSL(true).password(smsc.getSmsUserPass()).username(smsc.getSmsUserName()).build())
					.smsToCode(t.get("customerPhoneCode")==null?"255" : t.get("customerPhoneCode").toString())
					.notifNo(Integer.parseInt(t.get("notifNo").toString()))
					.build();
			
			// Save Sms Data Details
			SmsDataDetails savedata = new SmsDataDetails();

			Long sno = smsDataRepo.count();
			sno=sno+1;
			savedata.setMobileNo(m.getSmsTo());
			savedata.setSmsFrom(m.getSmsFrom());		
			savedata.setSmsType(smsSubject);
			savedata.setSmsContent(smsBody);
			savedata.setEntryDate(new Date());
			savedata.setSNo(sno.toString());
			savedata.setResMessage("Pending");
			savedata.setResStatus("P");
			savedata.setReqTime(new Date());
			savedata.setResTime(new Date());
			savedata.setNotifNo(m.getNotifNo());
			savedata.setPushedBy(req.getCreatedBy());
			savedata.setSmsRegards(smsRegards);
			smsDataRepo.saveAndFlush(savedata);
			
			ExecutorService service = Executors.newFixedThreadPool(4);
		    service.submit(new Runnable() {
		        public void run() {
		        	pushSms(m , savedata);
		        }
		    });
			
			
			response.setResponse("Sms Sent Successfully");	
			response.setSuccessId(req.getNotificationNo());
			res.setCommonResponse(response);
			res.setIsError(false);
			
	*/
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			return res ;
		}
		return res;
	}
	
	 

@Override
public List<MailNotifGetRes> getSentMailList(NotifGetReq req) {
	List<MailNotifGetRes> resList = new ArrayList<MailNotifGetRes>();
	ModelMapper dozerMapper = new ModelMapper();
	try {
		// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>();

			// Find All
			Root<NotifTransactionDetails> n = query.from(NotifTransactionDetails.class);
			Root<MailDataDetails> m = query.from(MailDataDetails.class);

			// Select
			query.multiselect( m.alias("mail") , n.alias("notif")   );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("pushedEntryDate")));

			// Where
			//Predicate n1 = cb.equal(m.get("pushedBy"), req.getCreatedBy() );
			Predicate n2 = cb.equal(n.get("pushedBy"), req.getCreatedBy());
			Predicate n3 = cb.equal(n.get("companyid"), req.getInsuranceId());
			Predicate n4 = cb.equal(n.get("productid"), req.getProductId());
			Predicate n5 = cb.equal(n.get("notifNo"), m.get("notifNo"));
			query.where(n2,n3,n4,n5).orderBy(orderList);
			int limit =StringUtils.isBlank(req.getLimit())? 0 :Integer.valueOf(req.getLimit()) ;
			int offset =StringUtils.isBlank(req.getOffset())? 100 :Integer.valueOf(req.getOffset()) ;
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
		for (Tuple t : list) {
			MailDataDetails data = (MailDataDetails) t.get("mail") ;
			NotifTransactionDetails notif = (NotifTransactionDetails) t.get("notif") ;
			// Response
			MailNotifGetRes res = new MailNotifGetRes();
			res.setFromMail(data.getFromEmail());
			res.setMailBody(data.getMailBody());
			res.setMailRegards(data.getMailRegards());
			res.setMailResponse(data.getMailResponse());
			res.setMailSubject(data.getMailSubject());		
			res.setMailTranId(data.getMailTranId()==null?"" : data.getMailTranId().toString() );
			res.setNotificationNo(data.getNotifNo()==null?"" : data.getNotifNo().toString() );
			res.setPushedBy(data.getPushedBy());
			res.setPushedEntryDate(data.getPushedEntryDate());
			res.setStatus(data.getStatus());
			res.setToMail(data.getToEmail());
			res.setCustomerName(notif.getCustomerName());
			resList.add(res);
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return resList;
}

@Override
public List<SmsNofiGetRes> getSmsSentList(NotifGetReq req) {
	List<SmsNofiGetRes> resList = new ArrayList<SmsNofiGetRes>();
	try {
		// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>();

			// Find All
			Root<NotifTransactionDetails> n = query.from(NotifTransactionDetails.class);
			Root<SmsDataDetails> s = query.from(SmsDataDetails.class);

			// Select
			query.multiselect( s.alias("sms") , n.alias("notif")   );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(s.get("entryDate")));

			// Where
			Predicate n1 = cb.equal(s.get("pushedBy"), req.getCreatedBy() );
			Predicate n2 = cb.equal(n.get("pushedBy"), req.getCreatedBy());
			Predicate n3 = cb.equal(n.get("companyid"), req.getInsuranceId());
			Predicate n4 = cb.equal(n.get("productid"), req.getProductId());
			Predicate n5 = cb.equal(n.get("notifNo"), s.get("notifNo"));
			query.where(n1, n2,n3,n4, n5 ).distinct(true).orderBy(orderList);
			int limit =StringUtils.isBlank(req.getLimit())? 0 :Integer.valueOf(req.getLimit()) ;
			int offset =StringUtils.isBlank(req.getOffset())? 100 :Integer.valueOf(req.getOffset()) ;
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
		for (Tuple t : list) {
			SmsDataDetails data = (SmsDataDetails) t.get("sms") ;
			NotifTransactionDetails notif = (NotifTransactionDetails) t.get("notif") ;
			// Response
			SmsNofiGetRes res = new SmsNofiGetRes();
			res.setEntryDate(data.getEntryDate());
			res.setMobileNo(data.getMobileNo());
			res.setNotificationNo(data.getNotifNo()==null?"":data.getNotifNo().toString());		
			res.setPushedBy(data.getPushedBy());
			res.setReqTime(data.getReqTime());
			res.setResTime(data.getResTime());
			res.setResMessage(data.getResMessage());		
			res.setResStatus(data.getResStatus());
			res.setSmsContent(data.getSmsContent());		
			res.setSmsFrom(data.getSmsFrom());
			res.setSmsType(data.getSmsType());
			res.setSno(data.getSNo());
			res.setSmsRegards(data.getSmsRegards());
			res.setCustomerName(notif.getCustomerName());
			resList.add(res);
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return resList;
}

@Override
public MailNotifGetRes viewSentMail(NotifGetByIdReq req) {
	MailNotifGetRes res = new MailNotifGetRes();
	try {
		List<MailDataDetails> list = mailDataRepo.findByNotifNoOrderByPushedEntryDateDesc(Integer.valueOf(req.getNotificationNo()));
		List<NotifTransactionDetails> notiflist = notifTrans.findByNotifNoOrderByEntryDateDesc(Integer.valueOf(req.getNotificationNo()));

		MailDataDetails data = list.get(0) ;
		// Response
		res.setFromMail(data.getFromEmail());
		res.setMailBody(data.getMailBody());
		res.setMailRegards(data.getMailRegards());
		res.setMailResponse(data.getMailResponse());
		res.setMailSubject(data.getMailSubject());		
		res.setMailTranId(data.getMailTranId()==null?"" : data.getMailTranId().toString() );
		res.setNotificationNo(data.getNotifNo()==null?"" : data.getNotifNo().toString() );
		res.setPushedBy(data.getPushedBy());
		res.setPushedEntryDate(data.getPushedEntryDate());
		res.setStatus(data.getStatus());
		res.setToMail(data.getToEmail());
		res.setCustomerName(notiflist.get(0).getCustomerName());
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res ;
}
	

@Override
public SmsNofiGetRes viewSmsSent(NotifGetByIdReq req) {
	SmsNofiGetRes res = new SmsNofiGetRes();
	try {
		List<SmsDataDetails> list = smsDataRepo.findByNotifNoOrderByEntryDateDesc(Integer.valueOf(req.getNotificationNo()));
		List<NotifTransactionDetails> notiflist = notifTrans.findByNotifNoOrderByEntryDateDesc(Integer.valueOf(req.getNotificationNo()));
			
		SmsDataDetails data = list.get(0);
		// Response
		res.setEntryDate(data.getEntryDate());
		res.setMobileNo(data.getMobileNo());
		res.setNotificationNo(data.getNotifNo()==null?"":data.getNotifNo().toString());		
		res.setPushedBy(data.getPushedBy());
		res.setReqTime(data.getReqTime());
		res.setResTime(data.getResTime());
		res.setResMessage(data.getResMessage());		
		res.setResStatus(data.getResStatus());
		res.setSmsContent(data.getSmsContent());		
		res.setSmsFrom(data.getSmsFrom());
		res.setSmsType(data.getSmsType());
		res.setSno(data.getSNo());
		res.setSmsRegards(data.getSmsRegards());
		res.setCustomerName(notiflist.get(0).getCustomerName());;
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}

@Override
public List<NofiByQuoteNoRes> viewNotificationSentToQuoteNo(NotifGetByQuoteNoReq req) {
	List<NofiByQuoteNoRes> resList=new ArrayList<NofiByQuoteNoRes>();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	//DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	try {

		// Get Datas
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

		// Find All
		Root<NotifTransactionDetails> td = query.from(NotifTransactionDetails.class);
		Root<MailDataDetails> mail = query.from(MailDataDetails.class);
		Root<SmsDataDetails> sms = query.from(SmsDataDetails.class);
		
		
		// Join with mail_data_details
		Predicate mailJoin = cb.equal(td.get("notifNo"), mail.get("notifNo"));
		query.where(mailJoin);

		// Join with eway_sms_data_details
		Predicate smsJoin = cb.equal(td.get("notifNo"), sms.get("notifNo"));
		query.where(smsJoin);

		// Select
		query.multiselect(// cb.literal(Long.parseLong("1")).alias("idsCount"),
				// Notification Info
				
				cb.selectCase().when(cb.max(td.get("notifNo")).in(cb.max(mail.get("notifNo"))), "Y").otherwise("N")
						.alias("mailyn"),
				cb.selectCase().when(cb.max(td.get("notifNo")).in(cb.max(sms.get("notifNo"))), "Y").otherwise("N")
						.alias("smsyn"),
				cb.max(td.get("notifNo")).alias("notifNo"), 
				cb.max(td.get("customerName")).alias("customerName"),
				cb.max(td.get("customerMailid")).alias("customerMailid"),
				cb.max(td.get("customerPhoneNo")).alias("customerPhoneNo"),
				cb.max(td.get("customerPhoneCode")).alias("customerPhoneCode"),
				cb.max(td.get("customerMessengerCode")).alias("customerMessengerCode"),
				cb.max(td.get("customerMessengerPhone")).alias("customerMessengerPhone"),
				cb.max(td.get("brokerName")).alias("brokerName"),
				cb.max(td.get("brokerCompanyName")).alias("brokerCompanyName"),
				cb.max(td.get("brokerMailId")).alias("brokerMailId"), 
				cb.max(td.get("brokerPhoneNo")).alias("brokerPhoneNo"),
				cb.max(td.get("brokerPhoneCode")).alias("brokerPhoneCode"),
				cb.max(td.get("brokerMessengerCode")).alias("brokerMessengerCode"),
				cb.max(td.get("brokerMessengerPhone")).alias("brokerMessengerPhone"),
				cb.max(td.get("uwName")).alias("uwName"),
				cb.max(td.get("uwMailid")).alias("uwMailid"), 
				cb.max(td.get("uwPhonecode")).alias("uwPhonecode"),
				cb.max(td.get("uwPhoneNo")).alias("uwPhoneNo"), 
				cb.max(td.get("uwMessengerCode")).alias("uwMessengerCode"),
				cb.max(td.get("uwMessengerPhone")).alias("uwMessengerPhone"),
				cb.max(td.get("companyName")).alias("companyName"), 
				cb.max(td.get("productName")).alias("productName"),
				cb.max(td.get("sectionName")).alias("sectionName"),
				cb.max(td.get("statusMessage")).alias("statusMessage"),
				cb.max(td.get("otp")).alias("otp"), 
				cb.max(td.get("policyNo")).alias("policyNo"),
				cb.max(td.get("quoteNo")).alias("quoteNo"), 
				cb.max(td.get("notifDescription")).alias("notifDescription"),
				cb.max(td.get("notifTemplatename")).alias("notifTemplatename"), 
				cb.max(td.get("entryDate")).alias("entryDate"),
				cb.max(td.get("notifcationPushDate")).alias("notifcationPushDate"),
				cb.max(td.get("notifPushedStatus")).alias("notifPushedStatus"),
				
				cb.selectCase()
		        .when(cb.equal(td.get("notifPushedStatus"), "C"), "COMPLETED")
		        .when(cb.equal(td.get("notifPushedStatus"), "P"), "PENDING")
		        .otherwise("FAILED")
		        .alias("notifPushedDesc"),
		        cb.max(td.get("notifPriority")).alias("notifPriority"),
		        cb.max(td.get("tinyUrl")).alias("tinyUrl"),
		        cb.max(td.get("productid")).alias("productid"),
		        cb.max(td.get("companyid")).alias("companyid"),
		        cb.max(td.get("notifcationEndDate")).alias("notifcationEndDate"),
		        cb.max(td.get("companyAddress")).alias("companyAddress"),
		        cb.max(td.get("companyLogo")).alias("companyLogo"),
		        cb.max(td.get("attachFilePath")).alias("attachFilePath"),
		        cb.max(td.get("pushedBy")).alias("notiPushedBy"),
		      //Mail Data Details
		        cb.max(mail.get("mailSubject")).alias("mailSubject"),
		        cb.max(mail.get("mailBody")).alias("mailBody"),
		        cb.max(mail.get("mailRegards")).alias("mailRegards"),
		        cb.max(mail.get("pushedEntryDate")).alias("pushedEntryDate"),
		        cb.max(mail.get("toEmail")).alias("toEmail"),
		        cb.max(mail.get("fromEmail")).alias("fromEmail"),
		        cb.max(mail.get("mailTranId")).alias("mailTranId"),
		        cb.max(mail.get("status")).alias("status"),
		        cb.max(mail.get("mailResponse")).alias("mailResponse"),
		        cb.max(mail.get("notifNo")).alias("notifNoInMail"),
		        cb.max(mail.get("pushedBy")).alias("mailPushedby"),
		        //sms data details
		       cb.max(sms.get("sNo")).alias("sNo"),
		       cb.max(sms.get("smsFrom")).alias("smsFrom"),
		       cb.max(sms.get("mobileNo")).alias("mobileNo"),
		       cb.max(sms.get("smsType")).alias("smsType"),
		       cb.max(sms.get("smsContent")).alias("smsContent"),
		       cb.max(sms.get("reqTime")).alias("reqTime"),
		       cb.max(sms.get("resTime")).alias("resTime"),
		       cb.max(sms.get("resStatus")).alias("resStatus"),
		       cb.max(sms.get("resMessage")).alias("resMessage"),
		       cb.max(sms.get("entryDate")).alias("smsEntryDate"),
		       cb.max(sms.get("notifNo")).alias("notifNoInSms"),
		       cb.max(sms.get("pushedBy")).alias("smsPushedBy"),
		       cb.max(sms.get("smsRegards")).alias("smsRegards")
		);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(cb.greatest(td.get("entryDate").as(Date.class))));


		// Where
//		Predicate n1 = cb.equal(mail.get("notifNo"), td.get("notifNo"));
//		Predicate n2 = cb.equal(sms.get("notifNo"), td.get("notifNo"));
		Predicate n3 = cb.equal(td.get("quoteNo"), req.getQuoteNo());
		query.where(n3).groupBy(td.get("notifNo")).orderBy(orderList);

		// Get Result
		TypedQuery<Tuple> result = em.createQuery(query);
		List<Tuple> list = result.getResultList();
		for (Tuple t : list) {
			NofiByQuoteNoRes res = new NofiByQuoteNoRes();
			// res=dozerMapper.map(t.get(0), NofiByQuoteNoRes.class);
		
			res.setNotifNo(t.get("notifNo")==null?"":t.get("notifNo").toString());
			res.setMailyn(t.get("mailyn").toString());
			res.setSmsyn(t.get("smsyn").toString());
			res.setCustomerName(t.get("customerName")==null?"":t.get("customerName").toString());
			res.setCustomerMailId(t.get("customerMailid")==null?"":t.get("customerMailid").toString());
			res.setCustomerPhoneNo(t.get("customerPhoneNo")==null?"":t.get("customerPhoneNo").toString());
			res.setCustomerPhoneCode(t.get("customerPhoneCode")==null?"":t.get("customerPhoneCode").toString());
			res.setCustomerMessengerCode(t.get("customerMessengerCode")==null?"":t.get("customerMessengerCode").toString());
			res.setCustomerMessengerPhone(t.get("customerMessengerPhone")==null?"":t.get("customerMessengerPhone").toString());
			res.setBrokerName(t.get("brokerName")==null?"":t.get("brokerName").toString());
			res.setBrokerCompanyName(t.get("brokerCompanyName")==null?"":t.get("brokerCompanyName").toString());
			res.setBrokerMailId(t.get("brokerMailId")==null?"":t.get("brokerMailId").toString());
			res.setBrokerPhoneNo(t.get("brokerPhoneNo")==null?"":t.get("brokerPhoneNo").toString());
			res.setBrokerPhoneNo(t.get("brokerPhoneCode")==null?"":t.get("brokerPhoneCode").toString());
			res.setBrokerMessengerCode(t.get("brokerMessengerCode")==null?"":t.get("brokerMessengerCode").toString());
			res.setBrokerMessengerPhone(t.get("brokerMessengerPhone")==null?"":t.get("brokerMessengerPhone").toString());
			res.setUwName(t.get("uwName")==null?"":t.get("uwName").toString());
			res.setUwMailid(t.get("uwMailid")==null?"":t.get("uwMailid").toString());
			res.setUwPhonecode(t.get("uwPhonecode")==null?"":t.get("uwPhonecode").toString());
			res.setUwPhoneNo(t.get("uwPhoneNo")==null?"":t.get("uwPhoneNo").toString());
			res.setUwMessengerCode(t.get("uwMessengerCode")==null?"":t.get("uwMessengerCode").toString());
			res.setUwMessengerPhone(t.get("uwMessengerPhone")==null?"":t.get("uwMessengerPhone").toString());
			res.setCompanyName(t.get("companyName")==null?"":t.get("companyName").toString());
			res.setProductName(t.get("productName")==null?"":t.get("productName").toString());
			res.setSectionName(t.get("sectionName")==null?"":t.get("sectionName").toString());
			res.setStatusmessage(t.get("statusMessage")==null?"":t.get("statusMessage").toString());
			res.setOtp(t.get("otp")==null?"":t.get("otp").toString());
			res.setPolicyNo(t.get("policyNo")==null?"":t.get("policyNo").toString());
			res.setQuoteNo(t.get("quoteNo")==null?"":t.get("quoteNo").toString());
			res.setNotifDescription(t.get("notifDescription")==null?"":t.get("notifDescription").toString());
			res.setNotiftemplatename(t.get("notifTemplatename")==null?"":t.get("notifTemplatename").toString());
			
			String entryDate=t.get("entryDate") == null ? null :dateFormat.format(t.get("entryDate"));
			res.setEntryDate(entryDate);
			String notifcationPushdate=t.get("notifcationPushDate") == null ? null :dateFormat.format(t.get("notifcationPushDate"));
			res.setNotifcationPushdate(notifcationPushdate);
			
			res.setNotifpushedStatus(t.get("notifPushedStatus")==null?"":t.get("notifPushedStatus").toString());
			res.setNotifpusheddesc(t.get("notifPushedDesc")==null?"":t.get("notifPushedDesc").toString());
			res.setNotifPriority(t.get("notifPriority")==null?"":t.get("notifPriority").toString());
			res.setTinyURL(t.get("tinyUrl")==null?"":t.get("tinyUrl").toString());
			res.setProductid(t.get("productid")==null?"":t.get("productid").toString());
			res.setCompanyid(t.get("companyid")==null?"":t.get("companyid").toString());
			
			String NotifcationEnddate=t.get("notifcationEndDate") == null ? null :dateFormat.format(t.get("notifcationEndDate"));
			res.setNotifcationEnddate(NotifcationEnddate);
			
			res.setCompanyAddress(t.get("companyAddress")==null?"":t.get("companyAddress").toString());
			res.setCompanyLogo(t.get("companyLogo")==null?"":t.get("companyLogo").toString());
			res.setAttachfilepath(t.get("attachFilePath")==null?"":t.get("attachFilePath").toString());
			res.setNotiPushedBy(t.get("notiPushedBy")==null?"":t.get("notiPushedBy").toString());
			res.setMailSubject(t.get("mailSubject")==null?"":t.get("mailSubject").toString());
			res.setMailBody(t.get("mailBody")==null?"":t.get("mailBody").toString());
			res.setMailRegards(t.get("mailRegards")==null?"":t.get("mailRegards").toString());
			
			String pushedEntryDate=t.get("pushedEntryDate") == null ? null :dateFormat.format(t.get("pushedEntryDate"));
			res.setPushedEntryDate(pushedEntryDate);
			
			res.setToEmail(t.get("toEmail")==null?"":t.get("toEmail").toString());
			res.setFromeMail(t.get("fromEmail")==null?"":t.get("fromEmail").toString());
			res.setMailTranId(t.get("mailTranId")==null?"":t.get("mailTranId").toString());
			res.setStatus(t.get("status")==null?"":t.get("status").toString());
			res.setMailResponse(t.get("mailResponse")==null?"":t.get("mailResponse").toString());
			res.setNotifNoInMail(t.get("notifNoInMail")==null?"":t.get("notifNoInMail").toString());
			res.setMailPushedby(t.get("mailPushedby")==null?"":t.get("mailPushedby").toString());
			res.setSNo(t.get("sNo")==null?"":t.get("sNo").toString());
			res.setSmsFrom(t.get("smsFrom")==null?"":t.get("smsFrom").toString());
			res.setMobileNo(t.get("mobileNo")==null?"":t.get("mobileNo").toString());
			res.setSmsType(t.get("smsType")==null?"":t.get("smsType").toString());
			res.setSmsContent(t.get("smsContent")==null?"":t.get("smsContent").toString());
			
			String reqTime=t.get("reqTime") == null ? null :dateFormat.format(t.get("reqTime"));
			res.setReqTime(reqTime);
			
			String resTime=t.get("resTime") == null ? null :dateFormat.format(t.get("resTime"));
			res.setResTime(resTime);
			
			res.setResStatus(t.get("resStatus")==null?"":t.get("resStatus").toString());
			res.setResMessage(t.get("resMessage")==null?"":t.get("resMessage").toString());
		
			String smsEntryDate=t.get("smsEntryDate") == null ? null :dateFormat.format(t.get("smsEntryDate"));
			res.setSmsEntryDate(smsEntryDate);

			res.setNotifNoInSms(t.get("notifNoInSms")==null?"":t.get("notifNoInSms").toString());
			res.setSmsPushedBy(t.get("smsPushedBy")==null?"":t.get("smsPushedBy").toString());
			res.setSmsRegards(t.get("smsRegards")==null?"":t.get("smsRegards").toString());
			resList.add(res);
		}

		return resList;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

//Get Active Templete Name
@Override
public List<DropDownRes> getActiveTemplatesDropDown(TemplatesDropDownReq req) {
	List<DropDownRes> resList = new ArrayList<DropDownRes>();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		Date todayEnd = cal.getTime();

		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<NotifTemplateMaster> query = cb.createQuery(NotifTemplateMaster.class);
		List<NotifTemplateMaster> list = new ArrayList<NotifTemplateMaster>();

		// Find All
		Root<NotifTemplateMaster> c = query.from(NotifTemplateMaster.class);

		// Select
		query.select(c);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(c.get("notifTemplatename")));

		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(c.get("notifTemplateCode"), ocpm1.get("notifTemplateCode"));
		Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
		Predicate a4 = cb.equal(c.get("productId"), ocpm1.get("productId"));
		effectiveDate.where(a1, a2, a3, a4);
		
		// Effective Date End Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<NotifTemplateMaster> ocpm2 = effectiveDate2.from(NotifTemplateMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a6 = cb.equal(c.get("notifTemplateCode"), ocpm2.get("notifTemplateCode"));
		Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
		Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));
		Predicate a9 = cb.greaterThanOrEqualTo(c.get("effectiveDateEnd"), todayEnd);
		effectiveDate2.where(a6, a7, a8 ,a9 );

		// Where
		Predicate n1 = cb.equal(c.get("status"), "Y");
		Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
		Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
		Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
		Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
	
		query.where(n1, n2, n3,n4, n5).orderBy(orderList);

		// Get Result
		TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
		list = result.getResultList();

		for (NotifTemplateMaster data : list) {
			// Response
			DropDownRes res = new DropDownRes();
			res.setCode(data.getNotifTemplateCode().toString());
			res.setCodeDesc(data.getNotifTemplatename());
			res.setStatus(data.getStatus());
			resList.add(res);
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return resList;
}

@Override
public List<Error> validateQuotoNo(NotifGetByQuoteNoReq req) {
	List<Error> error = new ArrayList<Error>();

	try {

		if (StringUtils.isBlank(req.getQuoteNo())) {
			error.add(new Error("01", "QuoteNo", "Please Enter QuoteNo "));
		}else{
		List<NotifTransactionDetails> data = notifTrans.findByQuoteNo(req.getQuoteNo());
		if (data == null || data.size() <= 0) {
			error.add(new Error("02", "List", "No Data Found "));
		}
		}

	} catch (Exception e) {
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}
	return error;
}


public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
	CompanyProductMaster product = new CompanyProductMaster();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		;
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		Date todayEnd = cal.getTime();

		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
		List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
		// Find All
		Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
		// Select
		query.select(c);
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(c.get("productName")));

		// Effective Date Start Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
		Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
		Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		effectiveDate.where(a1, a2, a3);
		// Effective Date End Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
		Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
		Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		effectiveDate2.where(a4, a5, a6);

		// Where
		Predicate n1 = cb.equal(c.get("status"), "Y");
		Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
		Predicate n4 = cb.equal(c.get("companyId"), companyId);
		Predicate n5 = cb.equal(c.get("productId"), productId);
		query.where(n1, n2, n3, n4, n5).orderBy(orderList);
		// Get Result
		TypedQuery<CompanyProductMaster> result = em.createQuery(query);
		list = result.getResultList();
		product = list.size() > 0 ? list.get(0) :null;
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return product;
}

}

