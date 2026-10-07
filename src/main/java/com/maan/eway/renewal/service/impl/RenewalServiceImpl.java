package com.maan.eway.renewal.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorVehicleUsageMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.bean.RenewDriverDetails;
import com.maan.eway.bean.RenewPremiaPolicy;
import com.maan.eway.bean.RenewPremiaPolicyRaw;
import com.maan.eway.bean.RenewQuotePolicy;
import com.maan.eway.bean.RenewStatusMaster;
import com.maan.eway.bean.RenewVehicleDetails;
import com.maan.eway.bean.RenewalNotificationMaster;
import com.maan.eway.bean.RenewalTransactionDetails;
import com.maan.eway.common.req.NonMotorBrokerReq;
import com.maan.eway.common.req.NonMotorPolicyReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.renewal.req.PullrenewalReq;
import com.maan.eway.renewal.req.RenewDataRequest;
import com.maan.eway.renewal.req.RenewalCopyQuoteReq;
import com.maan.eway.renewal.req.RenewalPendingRequest;
import com.maan.eway.renewal.req.RenewalSearchReq;
import com.maan.eway.renewal.req.RenewalStatusDetailReq;
import com.maan.eway.renewal.req.RenewalTransDetailReq;
import com.maan.eway.renewal.req.RenewalTransactionReq;
import com.maan.eway.renewal.res.RenewPremiaPolicyRes;
import com.maan.eway.renewal.res.RenewQuotePolicyResponse;
import com.maan.eway.renewal.res.RenewalDetailRes;
import com.maan.eway.renewal.res.RenewalPendingResponse;
import com.maan.eway.renewal.res.RenewalPolicyDetailsRes;
import com.maan.eway.renewal.res.RenewalStatusListRes;
import com.maan.eway.renewal.res.RenewalTransactionDetailsRes;
import com.maan.eway.renewal.service.RenewalService;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorBodyTypeMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.MotorMakeMasterRepository;
import com.maan.eway.repository.MotorVehicleUsageMasterRepository;
import com.maan.eway.repository.PolicyTypeMasterRepository;
import com.maan.eway.repository.RenewDriverDetailsRepository;
import com.maan.eway.repository.RenewPremiaPolicyRawRepository;
import com.maan.eway.repository.RenewPremiaPolicyRepository;
import com.maan.eway.repository.RenewQuotePolicyRepository;
import com.maan.eway.repository.RenewVehicleDetailsRepository;
import com.maan.eway.repository.RenewalNotificationMasterRepository;
import com.maan.eway.repository.RenewalTransactionDetailsRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

@Service
public class RenewalServiceImpl implements RenewalService{
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private RenewQuotePolicyRepository renewQuotePolicyRepo;
	
	@Autowired
	private RenewQuotePolicyRepository renewQuotePolicyRepository;
	
	@Autowired
	private RenewalTransactionDetailsRepository renewalTransactionDetailsRepository;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepository;
	
	@Autowired
	private RenewVehicleDetailsRepository renewVehicleDetailsRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private RenewalNotificationMasterRepository renewalNotificationMasterRepository;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EServiceSectionDetailsRepository sectionRepo;
	
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerRepo;
	
	@Autowired
	private RenewDriverDetailsRepository renewalDriverRepo;
	
	@Autowired
	private MotorDriverDetailsRepository driverRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private LoginBranchMasterRepository lbranchRepo;
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private RenewPremiaPolicyRawRepository rqprRepo;
	
	@Autowired
	private RenewPremiaPolicyRepository rppRepo;
	
	@Autowired
	private MotorBodyTypeMasterRepository mbtRepo;
	
	@Autowired
	private MotorVehicleUsageMasterRepository mvuRepo;
	
	@Autowired
	private PolicyTypeMasterRepository ptrepo;
	
	@Autowired
	private MotorMakeMasterRepository  mmrepo;
	
	@Autowired
	private BranchMasterRepository branchRepo;
	

	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;
	
	private Logger log=LogManager.getLogger(RenewalServiceImpl.class);
	@Value(value = "${spring.jpa.database}")
	private String dataBaseType;
	
	DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

	@Override
	public CommonRes pullrenewal(PullrenewalReq req) {
		CommonRes res=new CommonRes();
		try {
		String tranId=InsertTransactionDetails(req);
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<RenewalPolicyDetailsRes>list= getRenewalPolicyDetail(req);
		if(!CollectionUtils.isEmpty(list)) {
			for (RenewalPolicyDetailsRes rdata : list) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(rdata.getOldendDate());
				cal.set(Calendar.DATE, 1);
				Date startDate = cal.getTime();

				Calendar cal1 = new GregorianCalendar();
				cal1.setTime(rdata.getOldendDate());
				cal1.set(Calendar.DATE, 365);
				Date endDate = cal1.getTime();
				
				int daysLeft = (int) ChronoUnit.DAYS.between(
				        LocalDate.now(),
				        rdata.getOldendDate()
				             .toInstant()
				             .atZone(ZoneId.systemDefault())
				             .toLocalDate());

				RenewQuotePolicy data=new RenewQuotePolicy();
				data = dozerMapper.map(rdata, RenewQuotePolicy.class);
				data.setTranId(tranId);
				data.setRequestTime(new Date());
				data.setResponseTime(new Date());
				data.setStatus("Y");
				data.setNewstartDate(startDate);
				data.setNewendDate(endDate);
				data.setCurrentStatus("RENEW_PENDING");
				data.setCurrentStageCode("R");
				data.setCurrentStatusCode("RP");
				data.setPreviousPolicyPremium(rdata.getPreviousPolicyPremium());
				data.setCustomerReferenceNo(rdata.getCustomerReferenceNo());
				data.setDaysLeft(daysLeft);

				renewQuotePolicyRepository.saveAndFlush(data);

				if("5".equals(rdata.getProductCode())) {
					InsertVehicleDetails(rdata.getOldquoteNo());
					InsertDriverDetails(rdata.getOldquoteNo());
				}
			}
			res.setMessage("Renewal Pull Success for "+tranId);
			//InsertRenewalNotification(list,tranId);
			res.setCommonResponse(tranId);
		}else {
			res.setMessage("Renewal Pull failed for "+tranId);
		}
		return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private void InsertDriverDetails(String quoteNo) {
		List<MotorDriverDetails>mddList=driverRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
		if(!CollectionUtils.isEmpty(mddList)) {
			for (MotorDriverDetails mdata : mddList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				RenewDriverDetails rvd=new RenewDriverDetails();
				rvd = dozerMapper.map(mdata, RenewDriverDetails.class);
				rvd.setRiskId(mdata.getRiskId());
				rvd.setOldrequestreferenceNo(mdata.getRequestReferenceNo());
				rvd.setOldquoteNo(quoteNo);
				rvd.setEntryDate(new Date());
				renewalDriverRepo.saveAndFlush(rvd);
			}
		}
	}


	private void InsertVehicleDetails(String quoteNo) {
		List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
		if(!CollectionUtils.isEmpty(mddList)) {
			for (MotorDataDetails mdata : mddList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				RenewVehicleDetails rvd=new RenewVehicleDetails();
				rvd = dozerMapper.map(mdata, RenewVehicleDetails.class);
				rvd.setVehicleId(mdata.getVehicleId());
				rvd.setOldrequestReferenceNo(mdata.getRequestReferenceNo());
				rvd.setOldquoteNo(quoteNo);
				rvd.setEntryDate(new Date());
				rvd.setOldPolicyNumber(mdata.getPolicyNo());
				renewVehicleDetailsRepository.saveAndFlush(rvd);
			}
		}
		
	}

	private String InsertTransactionDetails(PullrenewalReq req) {
		String tranId="";
		try {
			SequenceGenerateReq seqReq=new SequenceGenerateReq();
			seqReq.setInsuranceId(req.getInsuranceId());
			seqReq.setType("9");
			seqReq.setTypeDesc("RENEWAL_TRAN_ID");
			tranId=genSeqNoService.generateSeqCall(seqReq);
			
			RenewalTransactionDetails renewTrans=new RenewalTransactionDetails();
			renewTrans.setTranId(tranId);
			renewTrans.setRequestTime(new Date());
			renewTrans.setResponseTime(new Date());
			renewTrans.setStatus("Y");
			renewalTransactionDetailsRepository.saveAndFlush(renewTrans);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tranId;
	}

	private void InsertRenewalNotification(List<RenewalPolicyDetailsRes> list, String tranId) {
		try {
		if (null != list && list.size() > 0) {
			Long sno=1l;
			List<RenewalNotificationMaster>rlist=renewalNotificationMasterRepository.findAllByOrderBySnoDesc();
			if(!CollectionUtils.isEmpty(rlist)) {
				sno=rlist.get(0).getSno()+1;
			}
			RenewalNotificationMaster sms = new RenewalNotificationMaster();
			sms.setTranId(tranId);
			sms.setNotificationId(1L);
			sms.setSmssentdate(new Date());
			sms.setPolicyexpiryDate(list.get(0).getOldendDate());
			ListItemValue data=listRepo.findByItemTypeAndItemCodeAndStatus("RENEWAL_NOTIFICATION","1", "Y");
			if(data!=null) {
				sms.setNextsmsdate(getNotificationDate(sms.getPolicyexpiryDate(),Integer.parseInt(data.getParam1())));
				sms.setSmsCount(String.valueOf(list.size()));
				sms.setSno(sno);
				renewalNotificationMasterRepository.save(sms);
			}
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  List<RenewalPolicyDetailsRes> getRenewalPolicyDetail(PullrenewalReq req){

		List<RenewalPolicyDetailsRes> portfolio = new ArrayList<RenewalPolicyDetailsRes>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalPolicyDetailsRes> query = cb.createQuery(RenewalPolicyDetailsRes.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);

			// Select
			query.multiselect(
					// Customer Info
					c.get("title").alias("title"),c.get("clientName").alias("customerName"), c.get("gender").alias("gender"),
					c.get("occupation").alias("occupation"),c.get("mobileCode1").alias("mobileCode"),c.get("mobileNo1").alias("mobileNo"),
					c.get("email1").alias("emailId"), c.get("idType").alias("identityType"),c.get("idNumber").alias("identityNumber"),
					c.get("preferredNotification").alias("preferedNotification"), c.get("isTaxExempted").alias("taxExcemption"),
					c.get("street").alias("street"),c.get("nationality").alias("country"),c.get("regionCode").alias("region"),
					c.get("stateCode").alias("district"),c.get("policyHolderType").alias("policyHolderType"),c.get("policyHolderTypeid").alias("policyHolderTypeid"),
					c.get("customerReferenceNo").alias("customerReferenceNo"),

					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productCode"),m.get("sectionId").alias("sectionCode"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("oldrequestreferenceNo"), m.get("quoteNo").alias("oldquoteNo"),
					m.get("policyNo").alias("oldpolicyNo"),cb.literal("Portal").alias("serviceType"),
					m.get("inceptionDate").alias("oldstartDate"),m.get("expiryDate").alias("oldendDate"),
					m.get("noOfVehicles").alias("noofVehicle"),m.get("overallPremiumLc").alias("previousPolicyPremium"),
					m.get("loginId").alias("loginId"),m.get("applicationId").alias("applicationId"),m.get("branchName").alias("branchName")

			);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));

			Subquery<String> policyNo = query.subquery(String.class);
			Root<RenewQuotePolicy> ocpm1 = policyNo.from(RenewQuotePolicy.class);
			policyNo.select(ocpm1.get("oldpolicyNo"));
			policyNo.where(cb.equal(ocpm1.get("oldpolicyNo"), m.get("policyNo")));


			Predicate n5=null;
			if("mysql".equalsIgnoreCase(dataBaseType)) {
				Expression<Integer> dateDiffExpression = cb.function("DATEDIFF",Integer.class, m.get("expiryDate"),cb.currentDate());
				n5 = cb.between(dateDiffExpression, -1, req.getDays());
			}
			else {
				Expression<Long> dateDiffExpression = cb.diff(
				    cb.function("TRUNC", Date.class, m.get("expiryDate")).as(Long.class),
				    cb.function("TRUNC", Date.class, cb.currentDate()).as(Long.class)
				);
				n5 = cb.between(dateDiffExpression, -30L, (long) req.getDays());
			}
			Predicate n1 = cb.equal(m.get("status"), "P");
//			Predicate n2 = cb.equal(m.get("integrationStatus"), "S");
			Predicate n3 = StringUtils.isNotBlank(req.getInsuranceId()) ? cb.equal(m.get("companyId"), req.getInsuranceId()) : cb.conjunction();
			Predicate n4 = cb.equal(m.get("customerId"), c.get("customerId"));
			Predicate n7 = cb.or(
					cb.isNull(m.get("endtTypeId")),
					cb.equal(m.get("endtTypeId"), ""),
					cb.notEqual(m.get("endtTypeId"), 0)
			);
			Predicate n9 = cb.exists(policyNo).not();
			query.where( n1,n3,n4,n5,n7,n9);

			// Get Result
			TypedQuery<RenewalPolicyDetailsRes> result = em.createQuery(query);
			portfolio = result.getResultList();


		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return portfolio;
	}
	@Transactional
	public void sendSmsEmail(RenewDataRequest req) {
		boolean sms=true,mail=true;
		String remarks="",currenctStatus="",currentStatusCode="";
		try {
			
		if(StringUtils.isBlank(req.getMobileno())) {
			remarks="Mobile Not Available";
			currentStatusCode="ACV";
			currenctStatus="ADMIN-CALL-ALLIANCE";
			sms=false;
		}else if(StringUtils.isBlank(req.getMobileno())) {
			remarks="Email Not Available";
			mail=false;
		}
		if(sms || mail) {
			Calendar calend = Calendar.getInstance();
			calend.setTime(new Date()); 
			calend.add(Calendar.DATE, 1); 
			NotifTransactionDetails nt = NotifTransactionDetails.builder()
					.brokerCompanyName(req.getCustomerName())
					.brokerMailId(req.getEmail())					
					.companyName(req.getCompanyName())
					.customerPhoneCode(Integer.parseInt(req.getMobileCode()))
					.customerPhoneNo(req.getMobileno()==null?null:new BigDecimal(req.getMobileno()))
					.customerMailid(req.getEmail())					
					.customerName(req.getCustomerName())
					.entryDate(new Date())
					.notifcationPushDate(new Date())
					.notifcationEndDate(calend.getTime())
					.regNo(req.getRegistrationNo())
					.expiryDate(req.getExpiryDate())
					//.notifDescription(tempPassword)
					.notifNo(Instant.now().toEpochMilli())
					//.notifNo(null)
					.notifPriority(1)
					.notifPushedStatus("P")
					.notifTemplatename("RENEWAL_NOTIFICATION1")											
					.productName("Common")					
					//.tinyUrl(n.getTinyUrl())
					.companyid(req.getCompanyId())
					.productid(99999)
					//.companyLogo(cm.getCompanyLogo())
					//.companyAddress(cm.getCompanyAddress())											
					.tinyUrlActive("N")
					//.tinyGroupId(tinyGroupId)
					.build();
			NotifTransactionDetails sv = notifTrans.save(nt);
			List<NotifTransactionDetails> text=new LinkedList<NotifTransactionDetails>();
			text.add(sv);
			notificationService.jobProcess(text);
			currentStatusCode="ASS";
			currenctStatus="ALLIANCE-SMS-SENT";
		}
		}catch (Exception e) {
			e.printStackTrace();
			currentStatusCode="ASF";
			currenctStatus="ALLIANCE-SMS-FAILED";
		}
		if(sms) {
			updateRenewalStatusAndStage("V",currentStatusCode,currenctStatus,req.getPolicyNo());
			if("Y".equalsIgnoreCase(req.getLastNotifyYN()))
			updateNotifyStatus(req.getLastNotifyYN(),req.getPolicyNo());
		}
		updateCurrentStatus(remarks,req.getPolicyNo());
	}
	@Transactional
	private void updateNotifyStatus(String lastNotifyYN, String policyNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
		Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
		
		update.set("lastNotifyYN", lastNotifyYN);
		update.where(cb.equal(rqp.get("oldpolicyNo"), policyNo) );
		em.createQuery(update).executeUpdate();  
		
	}

	@Transactional
	private void updateCurrentStatus(String remarks, String policyNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
		Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
		
		update.set("remarks", remarks);
		update.where(cb.equal(rqp.get("oldpolicyNo"), policyNo) );
		em.createQuery(update).executeUpdate();  
		
	}
	@Transactional
	private void updateRenewalStatusAndStage(String stageCode, String currentStatusCode, String currenctStatus, String policyNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
		Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
		
		update.set("currentStatus", currenctStatus);
		update.set("currentStageCode", stageCode);
		update.set("currentStatusCode", currentStatusCode);
		update.where(cb.equal(rqp.get("oldpolicyNo"), policyNo) );
		em.createQuery(update).executeUpdate(); 
	}

	@Override
	public boolean getDbStatus() {
		List<ListItemValue> mobileCodes = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("RENEW_SCHEDULER" , "Y");
		if(!CollectionUtils.isEmpty(mobileCodes)) {
			return true;
		}
		return false;
	}

	@Override
	public List<RenewDataRequest> getRenewPolicyList(String tranId) {
		List<RenewDataRequest>res=new ArrayList<>();
		List<RenewQuotePolicy> rqplist=renewQuotePolicyRepository.findByCurrentStatusCodeAndTranId("RP",tranId);
			if(!CollectionUtils.isEmpty(rqplist)) {
				for (RenewQuotePolicy rdata : rqplist) {
				String quoteNo=rdata.getOldquoteNo();
				List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				if(!CollectionUtils.isEmpty(mddList)) {
					for (MotorDataDetails mdata : mddList) {
						RenewDataRequest  rdr=new RenewDataRequest();
						rdr.setCompanyId(rdata.getCompanyId());		
						rdr.setCompanyName(null);
						rdr.setCustomerName(rdata.getCustomerName());
						rdr.setEmail(rdata.getEmailId());
						rdr.setMobileCode(rdata.getMobileCode());
						rdr.setMobileno(rdata.getMobileNo());
						rdr.setPolicyNo(rdata.getOldpolicyNo());
						rdr.setTransId(rdata.getTranId());	
						rdr.setRegistrationNo(mdata.getRegistrationNumber());
						rdr.setExpiryDate(rdata.getOldendDate());
						res.add(rdr);
					}
				}
			}
		}
		
		return res;
	}

	@Override
	public List<RenewDataRequest> getNotificationRequestList() {
		List<RenewDataRequest>res=new ArrayList<>();
		
		List<String>status=new ArrayList<>();
		status.add("ACV");status.add("APG");status.add("ASS");status.add("ASF");status.add("ASC");
		List<String>tranId=getNotificationPendingTrans();
		
		List<RenewQuotePolicy> rqplist=renewQuotePolicyRepository.findByCurrentStatusCodeInAndTranIdIn(status,tranId);
			if(!CollectionUtils.isEmpty(rqplist)) {
				for (RenewQuotePolicy rdata : rqplist) {
				String quoteNo=rdata.getOldquoteNo();
				List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				if(!CollectionUtils.isEmpty(mddList)) {
					for (MotorDataDetails mdata : mddList) {
						RenewDataRequest  rdr=new RenewDataRequest();
						rdr.setCompanyId(rdata.getCompanyId());		
						rdr.setCompanyName(null);
						rdr.setCustomerName(rdata.getCustomerName());
						rdr.setEmail(rdata.getEmailId());
						rdr.setMobileCode(rdata.getMobileCode());
						rdr.setMobileno(rdata.getMobileNo());
						rdr.setPolicyNo(rdata.getOldpolicyNo());
						rdr.setTransId(rdata.getTranId());	
						rdr.setRegistrationNo(mdata.getRegistrationNumber());
						rdr.setExpiryDate(rdata.getOldendDate());
						res.add(rdr);
					}
				}
			}
		}
		
		return res;
	}

	private List<String> getNotificationPendingTrans() {
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> query = cb.createQuery(String.class);

			Root<RenewalNotificationMaster> m = query.from(RenewalNotificationMaster.class);

			// Select
			query.multiselect(m.get("tranId").alias("tranId"));

			Predicate n1=null;
			if("mysql".equalsIgnoreCase(dataBaseType)) {
				Expression<Integer> dateDiffExpression = cb.function("DATEDIFF",Integer.class, m.get("nextsmsdate"),cb.currentDate());
				n1 = cb.equal(dateDiffExpression,0);
			}
			else {
				Expression<Long> dateDiffExpression = cb.diff(
				    cb.function("TRUNC", Date.class, m.get("nextsmsdate")).as(Long.class),
				    cb.function("TRUNC", Date.class, cb.currentDate()).as(Long.class)
				);
				n1 = cb.equal(dateDiffExpression,0);
			}
			
			query.where(n1);
			
			// Get Result
			TypedQuery<String> result = em.createQuery(query);
			return result.getResultList();

		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
	}

	@Override
	public void InsertNotificationSmsNext(List<RenewDataRequest> list) {
		try {
			
			List<String> uniqueTranIdList = list.stream().map(RenewDataRequest::getTransId).distinct().collect(Collectors.toList());
			
			for(String tranId:uniqueTranIdList) {
				try {
					List<RenewalNotificationMaster> nlist = renewalNotificationMasterRepository.findByTranIdOrderByNotificationIdDesc(tranId);
					if(nlist!=null) {
						RenewalNotificationMaster sms=nlist.get(0);
						ListItemValue data=listRepo.findByItemTypeAndItemCodeAndStatus("RENEWAL_NOTIFICATION",String.valueOf(sms.getNotificationId()+1) , "Y");
						if(data!=null) {
							Long sno=1l;
							List<RenewalNotificationMaster>rlist=renewalNotificationMasterRepository.findAllByOrderBySnoDesc();
							if(!CollectionUtils.isEmpty(rlist)) {
								sno=rlist.get(0).getSno()+1;
							}
							sms.setNotificationId(sms.getNotificationId()+1);
							sms.setSmssentdate(sms.getNextsmsdate());
							System.out.println("Notification Id"+sms.getNotificationId());
							if(!"5".equals(String.valueOf(sms.getNotificationId()))) {
								sms.setNextsmsdate(getNotificationDate(sms.getPolicyexpiryDate(),Integer.parseInt(data.getParam1())));
							}
							sms.setSmsCount(String.valueOf(list.size()));
							sms.setSno(sno);
							renewalNotificationMasterRepository.save(sms);
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("******Exception occurs while inserting new Notification next SMS Date******");
		}
	}
	public Date getNotificationDate(Date dateInstance,int noofdays ) {
		Date result=null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateInstance);
			cal.add(Calendar.DATE, -noofdays);
			result = cal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	@Transactional
	public void startExpiredPolicyUpdateData() {
	try {
		updateExpiredPolicyUpdateData();
		List<RenewQuotePolicy>list=getExpiredPolicyList();
		if(!CollectionUtils.isEmpty(list)) {
			for (RenewQuotePolicy rdata : list) {
			String quoteNo=rdata.getOldquoteNo();
			List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
			if(!CollectionUtils.isEmpty(mddList)) {
				for (MotorDataDetails mdata : mddList) {
					RenewDataRequest  rdr=new RenewDataRequest();
					rdr.setCompanyId(rdata.getCompanyId());		
					rdr.setCompanyName(null);
					rdr.setCustomerName(rdata.getCustomerName());
					rdr.setEmail(rdata.getEmailId());
					rdr.setMobileCode(rdata.getMobileCode());
					rdr.setMobileno(rdata.getMobileNo());
					rdr.setPolicyNo(rdata.getOldpolicyNo());
					rdr.setTransId(rdata.getTranId());	
					rdr.setRegistrationNo(mdata.getRegistrationNumber());
					rdr.setExpiryDate(rdata.getOldendDate());
					sendSmsEmail(rdr);
					//res.add(rdr);
				}
			}
			}
		}
	}catch (Exception e) {
		e.printStackTrace();;
	}
	}
	private List<RenewQuotePolicy> getExpiredPolicyList() {
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewQuotePolicy> query = cb.createQuery(RenewQuotePolicy.class);

			Root<RenewQuotePolicy> rqp = query.from(RenewQuotePolicy.class);

			// Select
			query.select(rqp);

			query.where(cb.equal(rqp.get("currentStageCode"), "ASE"),rqp.get("lastNotifyYN").isNull(),
					cb.lessThan(rqp.get("oldendDate"), cb.currentDate()));
			
			// Get Result
			TypedQuery<RenewQuotePolicy> result = em.createQuery(query);
			return result.getResultList();

		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
	}

	@Transactional
	private void updateExpiredPolicyUpdateData() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
			Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
			
			update.set("currentStatus", "V");
			update.set("currentStageCode", "ASE");
			update.set("currentStatusCode", "ALLIANCE-SMS-EXPIRED");
			update.set("remarks", "Quotation Expired");
			List<String>status=new ArrayList<>();
			status.add("PS");status.add("PF");status.add("CS");status.add("CF");status.add("RF");status.add("ASE");
			update.where(rqp.get("currentStageCode").in(status).not(),cb.lessThan(rqp.get("oldendDate"), cb.currentDate()));
			em.createQuery(update).executeUpdate(); 
		}catch (Exception e) {
			e.printStackTrace();;
		}
		
	}

	@Override
	public CopyQuoteSuccessRes renewalCopyQuote(RenewalCopyQuoteReq req) {
		CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
		CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(), req.getProductId().toString());

		if (product.getMotorYn().equalsIgnoreCase("M")) {
			res = MotorRenewalCopyQuote(req);
		} else if (product.getMotorYn().equalsIgnoreCase("A")) {
			res = BuildingRenewalCopyQuote(req);
		} else {
			res = CommonRenewalCopyQuote(req);
		}
		return res;
	}

	private CopyQuoteSuccessRes MotorRenewalCopyQuote(RenewalCopyQuoteReq req) {
		CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceMotorDetails savedata = new EserviceMotorDetails();
		String refNo="",custRefNo="";
		try {
			
			List<RenewQuotePolicy>list=renewQuotePolicyRepository.findByOldpolicyNo(req.getPolicyNo());
			if (list.size() > 0) {

				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(req.getInsuranceId());  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	refNo =  genSeqNoService.generateSeqCall(generateSeqReq);
	 		 	
	 		 	generateSeqReq.setType("1");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
	 		 	custRefNo = genSeqNoService.generateSeqCall(generateSeqReq);
				for (RenewQuotePolicy data : list) {
					String requestNo=data.getOldrequestreferenceNo();
					String quoteNo=data.getOldquoteNo();
					List<RenewVehicleDetails>mddList=renewVehicleDetailsRepository.findByOldquoteNoOrderByVehicleIdAsc(quoteNo);
					if(!CollectionUtils.isEmpty(mddList)) {
							for (RenewVehicleDetails mdata : mddList) {
								savedata=dozerMapper.map(mdata, EserviceMotorDetails.class);
								savedata.setOldPolicyNumber(mdata.getOldPolicyNumber());
								savedata.setEntryDate(new Date());
								savedata.setCreatedBy(req.getLoginId());
								savedata.setUpdatedBy(req.getLoginId());
								savedata.setUpdatedDate(new Date());
								savedata.setRequestReferenceNo(refNo);
								savedata.setCustomerReferenceNo(custRefNo);
								savedata.setPolicyStartDate(data.getNewstartDate());		
								savedata.setPolicyEndDate(data.getNewendDate());
								savedata.setActualPremiumFc(BigDecimal.ZERO);
								savedata.setActualPremiumLc(BigDecimal.ZERO);
								savedata.setOverallPremiumFc(BigDecimal.ZERO);
								savedata.setOverallPremiumLc(BigDecimal.ZERO);
								savedata.setQuoteNo("");
								savedata.setStatus("Y");
								savedata.setSavedFrom("WEB");
								savedata.setRenewalYn("Y");
								savedata.setApplicationId(StringUtils.isBlank(req.getApplicationId()) ? "1" : req.getApplicationId());
								
								LoginUserInfo loginUserData = loginUserRepo.findByLoginId(req.getLoginId());
							
								LoginBranchMaster brokerBranch =lbranchRepo.findByLoginIdAndBrokerBranchCodeAndCompanyIdAndBranchCode( req.getLoginId(), req.getBrokerBranchCode(), req.getInsuranceId(),req.getBranchCode());
								
				                LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());

								savedata.setBrokerCode(loginData.getOaCode().toString());
								savedata.setAgencyCode(loginData.getAgencyCode());
								savedata.setCustomerCode(loginUserData.getCustomerCode());
								savedata.setLoginId(req.getLoginId());
								savedata.setCustomerName(loginUserData.getCustomerName());
								savedata.setBdmCode(null);
								savedata.setBrokerBranchCode(brokerBranch.getBrokerBranchCode());
								savedata.setBrokerBranchName(brokerBranch.getBrokerBranchName());
								savedata.setSalePointCode(brokerBranch.getSalePointCode());
								savedata.setBrokerTiraCode(loginUserData.getRegulatoryCode());

								
								savedata.setProductId(data.getProductCode());
								savedata.setCompanyId(data.getCompanyId());
								savedata.setSectionId(data.getSectionCode());
								savedata.setBranchCode(data.getBranchCode());
								savedata.setBranchName(data.getBranchName());
								savedata.setIdNumber(data.getIdentityNumber());
								savedata.setRiskId(Integer.parseInt(mdata.getVehicleId()));
								savedata.setEndorsementYn("N");
								savedata.setEndorsementDate(null);
								savedata.setEndorsementEffdate(null);
								savedata.setEndorsementRemarks(null);
								savedata.setEndorsementType(null);
								savedata.setEndorsementTypeDesc(null);
								savedata.setEndtCategDesc(null);
								savedata.setEndtCount(null);
								savedata.setEndtPremium(null);
								savedata.setEndtPrevPolicyNo(null);
								savedata.setEndtPrevQuoteNo(null);
								savedata.setEndtStatus(null);
								savedata.setFinalizeYn("N");
								savedata.setEmiYn("N");
								savedata.setEmiPremium(null);
								savedata.setInstallmentPeriod(null);
								savedata.setNoOfInstallment(null);
								savedata.setApplicationId(req.getApplicationId());
								if(StringUtils.isNotBlank(req.getLoginId())) {
								savedata.setLoginId(req.getLoginId());
								}
								savedata.setSubUserType(req.getSubUserType());
								List<ListItemValue> sourcerTypes =genSeqNoService.getSourceTypeDropdown(req.getInsuranceId() , req.getBranchCode() ,"SOURCE_TYPE");
								if("broker".equalsIgnoreCase(req.getUserType())){
									List<ListItemValue> filterSource =  sourcerTypes.stream().filter(o-> o.getItemValue().equalsIgnoreCase(req.getUserType())).collect(Collectors.toList());
									savedata.setSourceType(req.getUserType());
									savedata.setSourceTypeId(filterSource.get(0).getItemCode());
								}
								savedata.setPolicyNo(null);
								savedata.setVatPremium(null);
								savedata.setCdRefno(null);
								savedata.setMsRefno(null);
								savedata.setVdRefNo(null);
								savedata.setSumInsuredLc(null);
								
								// Source Type Condtion
								String sourceType = savedata.getSourceType() ;
								if(StringUtils.isNotBlank(savedata.getApplicationId()) && ! "1".equalsIgnoreCase(savedata.getApplicationId()) ) {
									List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter( o -> "Y".equalsIgnoreCase(o.getStatus()) 
											&& o.getItemValue().contains(sourceType) ).collect(Collectors.toList()); 							
									savedata.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()	: 	savedata.getSourceType());			
									savedata.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()	: 	savedata.getSourceTypeId());
								}
								
								repo.saveAndFlush(savedata);
								
								
								//Section Insert
								EserviceSectionDetails savedata3 = new EserviceSectionDetails();
								
								savedata3.setRequestReferenceNo(refNo);
								savedata3.setCustomerReferenceNo(custRefNo);;
								savedata3.setRiskId(Integer.parseInt(mdata.getVehicleId()));
								savedata3.setLocationId(mdata.getLocationId());
								savedata3.setProductId(data.getProductCode());
								savedata3.setSectionId(String.valueOf(mdata.getSectionId()));
								savedata3.setSectionName(mdata.getSectionName());
								savedata3.setCompanyId(String.valueOf(mdata.getCompanyId()));
								savedata3.setCompanyName(mdata.getCompanyName());
								savedata3.setEntryDate(new Date());
								savedata3.setCreatedBy(req.getLoginId());
								savedata3.setUpdatedBy(req.getLoginId());
								savedata3.setUpdatedDate(new Date());
								savedata3.setQuoteNo("");
								savedata3.setStatus("Y");
								savedata3.setEndorsementDate(null);
								savedata3.setEndorsementEffdate(null);
								savedata3.setEndorsementRemarks(null);
								savedata3.setEndorsementType(null);
								savedata3.setEndorsementTypeDesc(null);
								savedata3.setEndtCategDesc(null);
								savedata3.setEndtCount(null);
								savedata3.setEndtPremium(null);
								savedata3.setEndtPrevPolicyNo(null);
								savedata3.setEndtPrevQuoteNo(null);
								savedata3.setEndtStatus(null);
								savedata3.setPolicyNo(null);
								sectionRepo.saveAndFlush(savedata3);
								
					}
				}
					
					EserviceCustomerDetails customer=new EserviceCustomerDetails();
					customer.setCustomerReferenceNo(custRefNo);
					customer.setPolicyHolderType(data.getPolicyHolderType());
					customer.setClientName(data.getCustomerName());
					customer.setTitle(data.getTitle());
					customer.setGender(data.getGender());
					customer.setOccupation(data.getOccupation());
					customer.setMobileCode1(data.getMobileCode());
					customer.setMobileNo1(data.getMobileNo());
					customer.setEmail1(data.getEmailId());
					customer.setIdType(data.getIdentityType());
					customer.setIdNumber(data.getIdentityNumber());
					customer.setPreferredNotification(data.getPreferedNotification());	
					customer.setIsTaxExempted(data.getTaxExcemption());
					customer.setStreet(data.getStreet());
					customer.setNationality(data.getCountry());
					customer.setRegionCode(data.getRegion());
					customer.setStateCode(Integer.parseInt(data.getDistrict()));
					customer.setPinCode(data.getPobox());
					customer.setAge(9);
					customer.setPolicyHolderTypeid(data.getPolicyHolderTypeid());
					customer.setCompanyId(data.getCompanyId());
					customer.setProductId(Integer.parseInt(data.getProductCode()));
					customer.setStatus("Y");
					customerRepo.saveAndFlush(customer);
					
					//Driver Save
					List<RenewDriverDetails> motorDriverData = renewalDriverRepo.findByOldrequestreferenceNoAndStatusNot(requestNo,"D");
					if (motorDriverData.size() > 0) {
						for (RenewDriverDetails ddata : motorDriverData) {
							MotorDriverDetails savedriver =new MotorDriverDetails();
							savedriver = dozerMapper.map(ddata, MotorDriverDetails.class);
							savedriver.setRequestReferenceNo(refNo);
							savedriver.setQuoteNo("");
							savedriver.setEntryDate(new Date());
							savedriver.setCreatedBy(req.getLoginId());
							savedriver.setEndorsementDate(null);
							savedriver.setEndorsementEffdate(null);
							savedriver.setEndorsementRemarks(null);
							savedriver.setEndorsementType(null);
							savedriver.setEndorsementTypeDesc(null);
							savedriver.setEndtCategDesc(null);
							savedriver.setEndtCount(null);
							savedriver.setEndtPrevPolicyNo(null);
							savedriver.setEndtPrevQuoteNo(null);
							savedriver.setEndtStatus(null);
							savedriver.setIsFinaceYn("N");
							driverRepo.saveAndFlush(savedriver);
						}
					}
					 RenewQuotePolicy rdata=data;
	    			 rdata.setCurrentStageCode("R");
	    			 rdata.setCurrentStatus("RENEW-SUCCESS");
	    			 rdata.setCurrentStatusCode("RS");
	    			 rdata.setNewRequestRefNo(refNo);
	    			 renewQuotePolicyRepo.saveAndFlush(rdata);
				}
				
				
				
				
				
				
			}
			res.setRequestReferenceNo(refNo);
			res.setCustomerReferenceNo(custRefNo);;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		return null;
	}
		return res;
	}

	

	@Override
	public CommonRes getRenewalPending(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			RenewalPendingResponse res = new RenewalPendingResponse();
			List<RenewalDetailRes> renewal = getAllPendingRenewalDetails(req);
			res.setRenewalDetailRes(renewal);
			if(renewal != null ) {
				int count = renewal.size();
				res.setTotalCount(count);
			}
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewalDetailRes> getAllPendingRenewalDetails(RenewalPendingRequest req) {
		List<RenewalDetailRes> res = new ArrayList<RenewalDetailRes>();
		
		try {

	        Date today = new Date();
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(today);
	        calendar.add(Calendar.DAY_OF_MONTH, 30);
	        Date dateAfter30Days = calendar.getTime();
			
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalDetailRes> query = cb.createQuery(RenewalDetailRes.class);

			// Find All
			Root<RenewQuotePolicy> r = query.from(RenewQuotePolicy.class);
						
			// Select
			query.multiselect(
					r.get("oldpolicyNo").alias("oldpolicyNo"),
					r.get("oldquoteNo").alias("oldquoteNo"),
					r.get("customerName").alias("customerName"),
					r.get("oldstartDate").alias("inceptionDate"),
					r.get("oldendDate").alias("expiryDate"),
					r.get("newpolicyNumber").alias("newpolicyNumber"),
					r.get("currentStatus").alias("currentStatus"),
					r.get("newRequestRefNo").alias("newRequestRefNo"),
					r.get("registrationNumber").alias("registrationNo")
					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(r.get("newstartDate")));
			
			List<String>status=new ArrayList<>()	;
			status.add("RP");status.add("ACV");status.add("APG");status.add("ASS");status.add("ASF");status.add("ASC");
			// Where
				
			List<Predicate>	predicate=new ArrayList<Predicate>();
			predicate.add(cb.equal(r.get("companyId"), req.getInsuranceId()));	
			predicate.add(cb.equal(r.get("productCode"), req.getProductId()));
			predicate.add(r.get("currentStatusCode").in(status));
			
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				predicate.add(cb.equal(r.get("loginId"), req.getLoginId()));
				predicate.add(cb.equal(r.get("applicationId"), req.getApplicationId()));
			}
			predicate.add(cb.equal(r.get("branchCode"), req.getBranchCode()));
				
	
			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);


			// Get Result
			TypedQuery<RenewalDetailRes> result = em.createQuery(query);
			res = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public CommonRes getRenewalExpired(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			RenewalPendingResponse res = new RenewalPendingResponse();
			List<RenewalDetailRes> renewal = getAllExpiredRenewalDetails(req);
			res.setRenewalDetailRes(renewal);
			if(renewal != null ) {
				int count = renewal.size();
				res.setTotalCount(count);
			}
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private List<RenewalDetailRes> getAllExpiredRenewalDetails(RenewalPendingRequest req) {
      List<RenewalDetailRes> res = new ArrayList<RenewalDetailRes>();
		
		try {

	        Date today = new Date();
	        
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalDetailRes> query = cb.createQuery(RenewalDetailRes.class);

			// Find All
			Root<RenewQuotePolicy> r = query.from(RenewQuotePolicy.class);
						
			// Select
			query.multiselect(
					r.get("oldpolicyNo").alias("oldpolicyNo"),
					r.get("oldquoteNo").alias("oldquoteNo"),
					r.get("customerName").alias("customerName"),
					r.get("oldstartDate").alias("inceptionDate"),
					r.get("oldendDate").alias("expiryDate"),
					r.get("newpolicyNumber").alias("newpolicyNumber"),
					r.get("currentStatus").alias("currentStatus"),
					r.get("newRequestRefNo").alias("newRequestRefNo"),
					r.get("registrationNumber").alias("registrationNo")

					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(r.get("newendDate")));
					
			// Where
			List<Predicate>	predicate=new ArrayList<Predicate>();
			predicate.add(cb.equal(r.get("companyId"), req.getInsuranceId()));	
			predicate.add(cb.equal(r.get("productCode"), req.getProductId()));
			predicate.add(cb.equal(r.get("currentStatusCode"), "ASE"));
			predicate.add(cb.equal(r.get("applicationId"), req.getApplicationId()));
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				predicate.add(cb.equal(r.get("loginId"), req.getLoginId()));
				predicate.add(cb.equal(r.get("applicationId"), req.getApplicationId()));
			}
			predicate.add(cb.equal(r.get("branchCode"), req.getBranchCode()));
				
	
			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);
			

			// Get Result
			TypedQuery<RenewalDetailRes> result = em.createQuery(query);
			res = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public CommonRes getRenewalCompleted(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			RenewalPendingResponse res = new RenewalPendingResponse();
			List<RenewalDetailRes> renewal = getAllCompletedRenewalDetails(req);
			res.setRenewalDetailRes(renewal);
			if(renewal != null ) {
				int count = renewal.size();
				res.setTotalCount(count);
			}
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewalDetailRes> getAllCompletedRenewalDetails(RenewalPendingRequest req) {
		 List<RenewalDetailRes> res = new ArrayList<RenewalDetailRes>();
			
			try {
		        
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<RenewalDetailRes> query = cb.createQuery(RenewalDetailRes.class);

				// Find All
				Root<RenewQuotePolicy> r = query.from(RenewQuotePolicy.class);
							
				// Select
				query.multiselect(
						r.get("oldpolicyNo").alias("oldpolicyNo"),
						r.get("oldquoteNo").alias("oldquoteNo"),
						r.get("customerName").alias("customerName"),
						r.get("oldstartDate").alias("inceptionDate"),
						r.get("oldendDate").alias("expiryDate"),
						r.get("newpolicyNumber").alias("newpolicyNumber"),
						r.get("currentStatus").alias("currentStatus"),
						r.get("registrationNumber").alias("registrationNo")

						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(r.get("newendDate")));
				
				List<Predicate>	predicate=new ArrayList<Predicate>();
				predicate.add(cb.equal(r.get("companyId"), req.getInsuranceId()));	
				predicate.add(cb.equal(r.get("productCode"), req.getProductId()));
				predicate.add(cb.equal(r.get("currentStatusCode"), "CS"));
				predicate.add(cb.equal(r.get("applicationId"), req.getApplicationId()));
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					predicate.add(cb.equal(r.get("loginId"), req.getLoginId()));
					predicate.add(cb.equal(r.get("applicationId"), req.getApplicationId()));
				}
				predicate.add(cb.equal(r.get("branchCode"), req.getBranchCode()));
					
		
				query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);
				

				// Get Result
				TypedQuery<RenewalDetailRes> result = em.createQuery(query);
				res = result.getResultList();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return res;
	}

	@Override
	public CommonRes getRenewalTransaction(RenewalTransactionReq req) {
		CommonRes data = new CommonRes();
		try {
			List<RenewalTransactionDetailsRes> res = new ArrayList<RenewalTransactionDetailsRes>();
			res = getAllRenewalTransactionDetails(req);

			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewalTransactionDetailsRes> getAllRenewalTransactionDetails(RenewalTransactionReq req) {
		List<RenewalTransactionDetailsRes> res = new ArrayList<RenewalTransactionDetailsRes>();
		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy"); 
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalTransactionDetailsRes> cq = cb.createQuery(RenewalTransactionDetailsRes.class);

			// Root for RenewalTransactionDetails table
			Root<RenewalTransactionDetails> root = cq.from(RenewalTransactionDetails.class);

			

			// Subquery for Converted count
			Subquery<String> convertedCountSubquery = cq.subquery(String.class);
			Root<RenewQuotePolicy> convertedSubqueryRoot = convertedCountSubquery.from(RenewQuotePolicy.class);
			convertedCountSubquery.select(cb.count(convertedSubqueryRoot).as(String.class))
			    .where(
			        cb.equal(convertedSubqueryRoot.get("tranId"), root.get("tranId")),
			        cb.equal(convertedSubqueryRoot.get("currentStatusCode"), "CS"),
			        cb.equal(convertedSubqueryRoot.get("companyId"), req.getInsuranceId()),
			        cb.equal(convertedSubqueryRoot.get("branchCode"), req.getBranchCode())
			    );

			// Subquery for pending count
			Subquery<String> pendingCountSubquery = cq.subquery(String.class);
			Root<RenewQuotePolicy> pendingSubqueryRoot = pendingCountSubquery.from(RenewQuotePolicy.class);
			pendingCountSubquery.select(cb.count(pendingSubqueryRoot).as(String.class))
			    .where(
			        cb.equal(pendingSubqueryRoot.get("tranId"), root.get("tranId")),
			        //cb.notEqual(pendingSubqueryRoot.get("currentStatusCode"), "RS"),
			        cb.notEqual(pendingSubqueryRoot.get("currentStatusCode"), "CS"),
			        cb.equal(pendingSubqueryRoot.get("companyId"), req.getInsuranceId()),
			        cb.equal(pendingSubqueryRoot.get("branchCode"), req.getBranchCode())
			    );
			
			// Subquery for total count
	        Subquery<String> totalCountSubquery = cq.subquery(String.class);
	        Root<RenewQuotePolicy> totalCountSubqueryRoot = totalCountSubquery.from(RenewQuotePolicy.class);
	        totalCountSubquery.select(cb.count(totalCountSubqueryRoot).as(String.class))
	            .where(
	                cb.equal(totalCountSubqueryRoot.get("tranId"), root.get("tranId")),
	                cb.equal(totalCountSubqueryRoot.get("branchCode"), req.getBranchCode())
	            );
	        
	        Subquery<String> notifyId = cq.subquery(String.class);
	        Root<RenewalNotificationMaster> rnm = notifyId.from(RenewalNotificationMaster.class);
	        
	        Subquery<Long> amendSubquery = cq.subquery(Long.class);
	        Root<RenewalNotificationMaster> rnm1 = amendSubquery.from(RenewalNotificationMaster.class);
	        amendSubquery.select(cb.max(rnm1.get("notificationId")))
	            .where(cb.equal(rnm1.get("tranId"), rnm.get("tranId")));

	        
	        
	        notifyId.select(rnm.get("notificationId").as(String.class))
	            .where(
	                cb.equal(rnm.get("tranId"), root.get("tranId")),
	                		cb.equal(rnm.get("notificationId"), amendSubquery)
	            );
	        
	        
	        
			// Define predicates for the query
			//Predicate tranIdPredicate = cb.equal(renewalTransactionDetailsRoot.get("tranId"), req.getTranId());

			// Select fields and subqueries in multiselect
			cq.multiselect(
					root.get("tranId").alias("tranId"),
					notifyId.alias("notificationId"),
					root.get("requestTime").alias("requestTime"),
					root.get("responseTime").alias("responseTime"),
					totalCountSubquery.alias("totalCount"),
					//successCountSubquery.alias("successCount"),
					convertedCountSubquery.alias("convertedCount"),
					pendingCountSubquery.alias("pendingCount")
			);

			// Apply the predicates
			cq.where(cb.between(root.get("requestTime"), sdf.parse(req.getStartDate())  , sdf.parse(req.getEndDate())));
			
			// Apply the order by clause
	        cq.orderBy(cb.asc(root.get("responseTime")));
	        
			// Execute the query
			res =  em.createQuery(cq).getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return res;
	}

	@Override
	public CommonRes getRenewalTransactionSuccess(RenewalTransDetailReq req) {
	    CommonRes data = new CommonRes();
	    try {
	        // Fetch the list of RenewQuotePolicy where status is "RS" (Success)
	    	List<RenewQuotePolicy> list = renewQuotePolicyRepo.findByTranIdAndCompanyIdAndBranchCodeAndCurrentStatusCode(req.getTranId(),req.getInsuranceId(),req.getBranchCode(),"RS");

	        // Map the entities to response objects
	        List<RenewQuotePolicyResponse> res = mapRenewQuotePolicyResponse(list);

	        // Set the response data
	        data.setCommonResponse(res);
	        data.setIsError(false);
	        data.setErrorMessage(Collections.emptyList());
	        data.setMessage("Success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        data.setIsError(true);
	        data.setMessage("Failed");
	    }
	    return data;
	}

	@Override
	public CommonRes getRenewalTransactionConverted(RenewalTransDetailReq req) {
		CommonRes data = new CommonRes();
		try {
			List<RenewQuotePolicyResponse> res = new ArrayList<RenewQuotePolicyResponse>();
			
			List<RenewQuotePolicy> list = renewQuotePolicyRepo.findByTranIdAndCompanyIdAndBranchCodeAndCurrentStatusCode(req.getTranId(),req.getInsuranceId(),req.getBranchCode(),"CS");
			
			res = mapRenewQuotePolicyResponse(list);

			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewQuotePolicyResponse> mapRenewQuotePolicyResponse(List<RenewQuotePolicy> list) {
	    List<RenewQuotePolicyResponse> responseList = new ArrayList<>();
	    try {
			for (RenewQuotePolicy policy : list) {
			    RenewQuotePolicyResponse response = new RenewQuotePolicyResponse();
			    
			 // Mapping fields from RenewQuotePolicy to RenewQuotePolicyResponse
		        response.setTranId(policy.getTranId());
		        response.setOldRequestRefNo(policy.getOldrequestreferenceNo());
		        response.setServiceType(policy.getServiceType());
		        response.setRequestTime(policy.getRequestTime());
		        response.setResponseTime(policy.getResponseTime());
		        response.setStatus(policy.getStatus());
		        response.setOldPolicyNo(policy.getOldpolicyNo());
		        response.setOldQuoteNo(policy.getOldquoteNo());
		        response.setOldStartDate(policy.getOldstartDate());
		        response.setOldEndDate(policy.getOldendDate());
		        response.setTitle(policy.getTitle());
		        response.setCustomerName(policy.getCustomerName());
		        response.setGender(policy.getGender());
		        response.setOccupation(policy.getOccupation());
		        response.setMobileCode(policy.getMobileCode());
		        response.setMobileNo(policy.getMobileNo());
		        response.setEmailId(policy.getEmailId());
		        response.setIdentityType(policy.getIdentityType());
		        response.setIdentityNumber(policy.getIdentityNumber());
		        response.setPreferredNotification(policy.getPreferedNotification());
		        response.setTaxExcemption(policy.getTaxExcemption());
		        response.setStreet(policy.getStreet());
		        response.setCountry(policy.getCountry());
		        response.setRegion(policy.getRegion());
		        response.setDistrict(policy.getDistrict());
		        response.setPobox(policy.getPobox());
		        response.setNoOfVehicle(policy.getNoofVehicle());
		        response.setNewStartDate(policy.getNewstartDate());
		        response.setNewEndDate(policy.getNewendDate());
		        response.setNewPremium(policy.getNewPremium() != null ? policy.getNewPremium().toString() : null);
		        response.setNewPolicyNumber(policy.getNewpolicyNumber());
		        response.setCurrentStatus(policy.getCurrentStatus());
		        response.setCurrentStageCode(policy.getCurrentStageCode());
		        response.setCurrentStatusCode(policy.getCurrentStatusCode());
		        response.setLoginId(policy.getLoginId());
		        response.setApplicationId(policy.getApplicationId());
		        response.setCompanyId(policy.getCompanyId());
		        response.setProductCode(policy.getProductCode());
		        response.setSectionCode(policy.getSectionCode());
		        response.setBranchCode(policy.getBranchCode());
		        response.setBranchName(policy.getBranchName());
		        response.setViewedDate(policy.getViewDate());
		        response.setRemarks(policy.getRemarks());
		        response.setRegistrationNo(policy.getRegistrationNumber());	
		        responseList.add(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return responseList;
	}

	@Override
	public CommonRes getRenewalTransactionPending(RenewalTransDetailReq req) {
	    CommonRes data = new CommonRes();
	    try {
	        // Fetch the list of RenewQuotePolicy where status is neither "RS" nor "CS" (Pending)
	        List<RenewQuotePolicy> list = renewQuotePolicyRepo.findByTranIdAndCompanyIdAndBranchCodeAndCurrentStatusCodeNotIn(req.getTranId(),req.getInsuranceId(),req.getBranchCode(),Arrays.asList("CS"));
	        // Map the entities to response objects
	        List<RenewQuotePolicyResponse> res = mapRenewQuotePolicyResponse(list);

	        // Set the response data
	        data.setCommonResponse(res);
	        data.setIsError(false);
	        data.setErrorMessage(Collections.emptyList());
	        data.setMessage("Success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        data.setIsError(true);
	        data.setMessage("Failed");
	    }
	    return data;
	}

	@Override
	public CommonRes pullPremiarenewal() {
		insertMotRenDetFromView();
		return null;
	}

	@Override
	public void insertMotRenDetFromView() {
		try {
			System.out.println("***insertMotRenDetFromView Start****");
			//List<Map<String,Object>> renewalList=renewQuotePolicyRepo.getRenewalViewList();
			//SaveRanewalRawDetail(renewalList);
			
			System.out.println("***insertMotRenDetFromView End****");
		} catch (Exception e) {
			System.out.println("***Exception occurs while inserting data from Renew View to motor renewal detail****");
			log.error(e);
		}
	}

	private void SaveRanewalRawDetail(List<Map<String, Object>> renewalList) {

		if(renewalList!=null && renewalList.size()>0) {
			System.out.println("***SaveRanewalRawDetail Start****");
			for(int i=0;i<renewalList.size();i++) {
				try {
					Map<String,Object>map=renewalList.get(i);
					RenewPremiaPolicyRaw rqp=new RenewPremiaPolicyRaw();
					
					rqp.setCompanyCode(map.get("COMPANY_CODE")==null?"":map.get("COMPANY_CODE").toString());
					rqp.setCompanyName(map.get("COMPANY_NAME")==null?"":map.get("COMPANY_NAME").toString());
					rqp.setClassCode(map.get("CLASS_CODE")==null?"":map.get("CLASS_CODE").toString());
					rqp.setClassName(map.get("CLASS_NAME")==null?"":map.get("CLASS_NAME").toString());
					rqp.setProductCode(map.get("PRODUCT_CODE")==null?"":map.get("PRODUCT_CODE").toString());
					rqp.setProductName(map.get("PRODUCT_NAME")==null?"":map.get("PRODUCT_NAME").toString());
					
					rqp.setDivisionCode(map.get("DIVISION_CODE")==null?"":map.get("DIVISION_CODE").toString());
					rqp.setDivisionName(map.get("DIVISION_NAME")==null?"":map.get("DIVISION_NAME").toString());
					rqp.setDepartmentCode(map.get("DEPARTMENT_CODE")==null?"":map.get("DEPARTMENT_CODE").toString());
					rqp.setDepartmentName(map.get("DEPARTMENT_NAME")==null?"":map.get("DEPARTMENT_NAME").toString());
					rqp.setBusinessType(map.get("BUSINESS_TYPE")==null?"":map.get("BUSINESS_TYPE").toString());
					rqp.setBusinessName(map.get("BUSINESS_NAME")==null?"":map.get("BUSINESS_NAME").toString());
					
					rqp.setPolicyNumber(map.get("POLICY_NUMBER")==null?"":map.get("POLICY_NUMBER").toString());
					rqp.setEndorsementNumber(map.get("ENDORSEMENT_NUMBER")==null?"":map.get("ENDORSEMENT_NUMBER").toString());
					
					rqp.setFromDate(map.get("FROM_DATE")==null?null:inputFormat.parse(map.get("FROM_DATE").toString()));
					rqp.setExpiryDate(map.get("EXPIRY_DATE")==null?null:inputFormat.parse(map.get("EXPIRY_DATE").toString()));
					rqp.setRenewalDate(map.get("RENEWAL_DATE")==null?null:inputFormat.parse((map.get("RENEWAL_DATE").toString())));
					
					rqp.setCustomerCode(map.get("CUSTOMER_CODE")==null?"":map.get("CUSTOMER_CODE").toString());
					rqp.setCustomerName(map.get("CUSTOMER_NAME")==null?"":map.get("CUSTOMER_NAME").toString());
					rqp.setInsuredCivilId(map.get("INSURED_CIVIL_ID")==null?"":map.get("INSURED_CIVIL_ID").toString());
					rqp.setInsuredMobile(map.get("INSURED_MOBILE")==null?"":map.get("INSURED_MOBILE").toString());
					rqp.setInsuredEmailId(map.get("INSURED_EMAIL_ID")==null?"":map.get("INSURED_EMAIL_ID").toString());
					
					rqp.setPolAssrCode(map.get("POL_ASSR_CODE")==null?"":map.get("POL_ASSR_CODE").toString());
					rqp.setPolAssrName(map.get("POL_ASSR_NAME")==null?"":map.get("POL_ASSR_NAME").toString());
					rqp.setPolSrcType(map.get("POL_SRC_TYPE")==null?"":map.get("POL_SRC_TYPE").toString());
					rqp.setPolSrcCode(map.get("POL_SRC_CODE")==null?null:map.get("POL_SRC_CODE").toString());
					rqp.setPolSrcName(map.get("POL_SRC_NAME")==null?null:(map.get("POL_SRC_NAME").toString()));
					
					
					rqp.setPolicySi(map.get("POLICY_SI")==null?null:Double.valueOf(map.get("POLICY_SI").toString()));
					rqp.setGrossPremium(map.get("GROSS_PREMIUM")==null?null:Double.valueOf(map.get("GROSS_PREMIUM").toString()));
					rqp.setCoverPremium(map.get("COVER_PREMIUM")==null?null:Double.valueOf(map.get("COVER_PREMIUM").toString()));
					rqp.setLoadingPremium(map.get("LOADING_PREMIUM")==null?null:Double.valueOf(map.get("LOADING_PREMIUM").toString()));
					rqp.setDiscountPremium(map.get("DISCOUNT_PREMIUM")==null?null:Double.valueOf(map.get("DISCOUNT_PREMIUM").toString()));
					
					rqp.setPvtCoverYn(map.get("PVT_COVER_YN")==null?null:Integer.parseInt(map.get("PVT_COVER_YN").toString()));
					rqp.setPvtCoverSi(map.get("PVT_COVER_SI")==null?null:Double.valueOf(map.get("PVT_COVER_SI").toString()));
					rqp.setPvtCoverPremium(map.get("PVT_COVER_PREMIUM")==null?null:Double.valueOf(map.get("PVT_COVER_PREMIUM").toString()));
					rqp.setChargeAmount(map.get("CHARGE_AMOUNT")==null?null:Double.valueOf(map.get("CHARGE_AMOUNT").toString()));
					rqp.setTotalPremium(map.get("TOTAL_PREMIUM")==null?null:Double.valueOf(map.get("TOTAL_PREMIUM").toString()));
					rqp.setTotalPremium(map.get("AG_BROK_COMMISSION")==null?null:Double.valueOf(map.get("AG_BROK_COMMISSION").toString()));
					rqp.setEntryDate(new Date());
					rqp.setStatus("RP");
					rqprRepo.save(rqp);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("***SaveRanewalRawDetail Start****");
			}
		}
	
		
	}

	@Override
	public List<RenewDataRequest> getPolicyRequestList() {
		List<RenewDataRequest>res=new ArrayList<>();
		try {
			String tranId=saveRenewPremiaPolicy();
			List<RenewPremiaPolicy> rqplist=rppRepo.findByCurrentStatusAndTransactionId("RP",tranId);
			//InsertPremiaRenewal(rqplist,tranId); 
			log.info("getPolicyRequestList--> transactionId: " + tranId);
			for (RenewPremiaPolicy rdata : rqplist) {
				RenewDataRequest rdr = new RenewDataRequest();
				
				rdr.setCompanyId(rdata.getCompanyId());		
				rdr.setCompanyName(null);
				rdr.setCustomerName(rdata.getCustomerName());
				rdr.setEmail(rdata.getInsuredEmailId());
				//rdr.setMobileCode(rdata.getMobileCode());
				rdr.setMobileno(rdata.getInsuredMobile());
				rdr.setPolicyNo(rdata.getPolicyNumber());
				rdr.setTransId(tranId);	
				//rdr.setRegistrationNo(rdata.getPlateNumber());
				rdr.setExpiryDate(rdata.getExpiryDate());
				
				res.add(rdr);
			}
		} catch (Exception e) {
			log.error(e);
		}
		return res;
	}

	private String saveRenewPremiaPolicy() {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		String tranId="";
		try {
		List<RenewPremiaPolicyRaw> findAll = rqprRepo.findAll();
		if(!CollectionUtils.isEmpty(findAll)) {
			PullrenewalReq req=new PullrenewalReq();
			req.setInsuranceId("100020");
			tranId=InsertTransactionDetails(req);
			log.info("RenewQuotePolicyRaw--> count: " + findAll.size());
			for (RenewPremiaPolicyRaw data : findAll) {
				RenewPremiaPolicy rpp=new RenewPremiaPolicy();
				rpp=dozerMapper.map(data, RenewPremiaPolicy.class);
				rpp.setTransactionId(tranId);
				rpp.setCurrentStatus("RP");
				//rpp.setMobileCode("+254");
				rpp.setCompanyId(req.getInsuranceId());
				rpp.setEntryDate(new Date());		
				rppRepo.saveAndFlush(rpp);
				rqprRepo.delete(data);
			} 
		}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tranId;
	}
	/*public void InsertPremiaRenewal(List<RenewPremiaPolicy> rqplist,String tranId) {
		CommonRes res=new CommonRes();
		List<RenewalPolicyDetailsRes>list=new ArrayList<>();
		//List<RenewPremiaPolicy> rqplist=rppRepo.findByStatusAndTransactionId("RP",tranId);
		if(!CollectionUtils.isEmpty(rqplist)) {
			for (RenewPremiaPolicy rdata : rqplist) {
				RenewalPolicyDetailsRes rd=new RenewalPolicyDetailsRes();
				Calendar cal = new GregorianCalendar();
				cal.setTime(rdata.getPolFmDt());
				cal.set(Calendar.DATE, 1);
				Date startDate = cal.getTime();
				
				Calendar cal1 = new GregorianCalendar();
				cal1.setTime(rdata.getPolFmDt());
				cal1.set(Calendar.DATE, 365);
				Date endDate = cal1.getTime();
				
				RenewQuotePolicy data=new RenewQuotePolicy();
				data.setServiceType("Premia");
				data.setOldrequestreferenceNo("99999");
				data.setCompanyId(rdata.getCompanyId());
				data.setCustomerName(rdata.getPolAssrName());
				data.setEmailId(rdata.getInsuredEmailId() );
				data.setMobileCode(rdata.getMobileCode());
				data.setMobileNo(rdata.getInsuredMobile());
				data.setRegistrationNumber(rdata.getPlateNumber());
				data.setChassisNumber(rdata.getChassNo());
				data.setEngineNumber(rdata.getEngineNumber());
				data.setOldpolicyNo(rdata.getPolNo());
				data.setTranId(tranId);
				data.setRequestTime(new Date());
				data.setResponseTime(new Date());
				data.setStatus("Y");
				data.setOldstartDate(rdata.getPolFmDt());
				data.setOldendDate(rdata.getPolExpDt() );
				data.setNewstartDate(startDate);
				data.setNewendDate(endDate);
				data.setCurrentStageCode("R");
   			 	data.setCurrentStatus("RENEW-SUCCESS");
   			 	data.setCurrentStatusCode("RS");
				data.setDivisionCode(rdata.getDivisionCode()); 
				data.setDivisionName(rdata.getDivisionName());
				data.setPolProductCode(rdata.getPolProdCode());
				data.setProductName(rdata.getProdName());
				data.setPolType(rdata.getPolType());
				data.setCustomerCode(rdata.getCustomerCode());
				data.setPolAssureCode(rdata.getPolAssrCode());
				data.setPolAssureName(rdata.getPolAssrName());
				data.setSourceCode(rdata.getSourceCode());
				data.setSourceName(rdata.getSourceName());
				
				
				List<LoginUserInfo> loginUserData = loginUserRepo.findByCoreAppBrokerCode(rdata.getSourceCode());
   			 	if(!CollectionUtils.isEmpty(loginUserData) ) {
   			 	data.setLoginId(loginUserData.get(0).getLoginId());
   			 	}
   			 	List<BranchMaster> branch=branchRepo.findByCoreAppCodeAndCompanyId(rdata.getDivisionCode(),rdata.getCompanyId());
   			 	if(!CollectionUtils.isEmpty(branch) ) {
   			 	data.setBranchCode(branch.get(0).getBranchCode());
   			 	data.setBranchName(branch.get(0).getBranchName());
   			 	}
   			 
				data.setApplicationId("1"); 
				data.setProductCode("5");
				
				renewQuotePolicyRepository.saveAndFlush(data);
				rd.setOldendDate(endDate);
				list.add(rd);
			}
			res.setMessage("Renewal Pull Success for "+tranId);
			InsertRenewalNotification(list,tranId);
			res.setCommonResponse(tranId);
		}else {
			res.setMessage("Renewal Pull failed for "+tranId);
		}
	}
*/
	@Override
	public List<RenewPremiaPolicyRes> searchRenewPremiaPolicy(RenewalSearchReq req) {
	
		List<RenewPremiaPolicyRes> response = new ArrayList<RenewPremiaPolicyRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<RenewPremiaPolicy> data = new ArrayList<RenewPremiaPolicy>();
		try {
			if(req.getSearchType().equalsIgnoreCase("REGNO")) {
				//data = rppRepo.findAllByPlateNumber(req.getPlateNumber());
			}
			else if(req.getSearchType().equalsIgnoreCase("MOBILENUMBER")) {
				data = rppRepo.findAllByInsuredMobile(req.getInsuredMobile());
			}
			if(!CollectionUtils.isEmpty(data)) {
				for (RenewPremiaPolicy rdata : data) {
					RenewPremiaPolicyRes rppr=new RenewPremiaPolicyRes();
					rppr=dozerMapper.map(rdata,RenewPremiaPolicyRes.class);
					if(StringUtils.isNotBlank(rppr.getBodyType())) {
						List<MotorBodyTypeMaster> bodylist= mbtRepo.findByCoreAppCodeAndBranchCodeAndCompanyIdOrderByAmendIdDesc(rppr.getBodyType() , "99999" , rppr.getCompanyId());
						if(!CollectionUtils.isEmpty(bodylist)) {
							rppr.setBodyTypeLocal(bodylist.get(0).getBodyId()==null?"":bodylist.get(0).getBodyId().toString());
						}
					}
					if(StringUtils.isNotBlank(rppr.getPolProdCode()) ) {
						List<MotorVehicleUsageMaster> usageList=mvuRepo.findByCompanyIdAndCoreAppCodeOrderByAmendIdDesc(rppr.getCompanyId(), rppr.getPolProdCode());
						if(!CollectionUtils.isEmpty(usageList)) {
							rppr.setVehicleUsageLocal(usageList.get(0).getVehicleUsageId()==null?"":usageList.get(0).getVehicleUsageId().toString());
						}
					}
					if(StringUtils.isNotBlank(rppr.getTypeOfCover()) ) {
						List<PolicyTypeMaster> polTypeList=ptrepo.findByCompanyIdAndCoreAppCodeOrderByAmendIdDesc(rppr.getCompanyId(), rppr.getTypeOfCover());
						if(!CollectionUtils.isEmpty(polTypeList)) {
							rppr.setTypeOfCoverLocal(polTypeList.get(0).getPolicyTypeId()==null?"":polTypeList.get(0).getPolicyTypeId().toString());
						}
					}
					
					if(StringUtils.isNotBlank(rppr.getMakeId()) ) {
						List<MotorMakeMaster>makeList=mmrepo.findByCompanyIdAndCoreAppCodeOrderByAmendIdDesc(rppr.getCompanyId(), rppr.getMakeId());
						if(!CollectionUtils.isEmpty(makeList)) {
							rppr.setMakeIdLocal(makeList.get(0).getMakeId()==null?"":makeList.get(0).getMakeId().toString());
						}
					}
					
					response.add(rppr);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public CommonRes getRenewalStatusList(RenewalTransDetailReq req) {
		CommonRes data=new CommonRes();
		List<RenewalStatusListRes> res = new ArrayList<RenewalStatusListRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalStatusListRes> cq = cb.createQuery(RenewalStatusListRes.class);

			Root<RenewQuotePolicy> root = cq.from(RenewQuotePolicy.class);

			Subquery<String> statusDesc = cq.subquery(String.class);
			Root<RenewStatusMaster> statusRoot = statusDesc.from(RenewStatusMaster.class);
			statusDesc.select(statusRoot.get("statusDesc"))
			 	.where(
				        cb.equal(statusRoot.get("currentStatusCode"), root.get("currentStatusCode")),
				        cb.equal(statusRoot.get("status"), "Y")
				    );

			Subquery<Integer> displayOrderSubquery = cq.subquery(Integer.class);
			Root<RenewStatusMaster> displayOrderRoot = displayOrderSubquery.from(RenewStatusMaster.class);
			displayOrderSubquery.select(displayOrderRoot.get("displayOrder"))
			    .where(
			        cb.equal(displayOrderRoot.get("currentStatusCode"), root.get("currentStatusCode")),
			        cb.equal(displayOrderRoot.get("status"), "Y")
			    );
		
	        
			
			cq.multiselect(
					root.get("currentStatusCode").alias("statusCode"),
					statusDesc.alias("statusDescription"),
				    cb.count(root).as(String.class).alias("count"),
				    displayOrderSubquery.as(String.class).alias("displayOrder")
				    );

			// Apply the predicates
			cq.where( cb.equal(root.get("tranId"), req.getTranId()),cb.equal(root.get("branchCode"), req.getBranchCode()));
			
			// Apply the order by clause
			cq.groupBy(root.get("currentStatusCode"));
	        cq.orderBy(cb.asc(displayOrderSubquery));
	        
			// Execute the query
			res =  em.createQuery(cq).getResultList();
			
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return data;
	}

	@Override
	public CommonRes getRenewalStatusDetailList(RenewalStatusDetailReq req) {
		CommonRes data = new CommonRes();
		try {
			List<RenewQuotePolicyResponse> res = new ArrayList<RenewQuotePolicyResponse>();
			
			List<RenewQuotePolicy> list = renewQuotePolicyRepo.findByTranIdAndCompanyIdAndBranchCodeAndCurrentStatusCode(req.getTranId(),req.getInsuranceId(),req.getBranchCode(),req.getStatusCode());
			
			res = mapRenewQuotePolicyResponse(list);

			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
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
			product = list.size() > 0 ? list.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}
	
	private CopyQuoteSuccessRes BuildingRenewalCopyQuote(RenewalCopyQuoteReq req) {
		CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<EserviceBuildingDetails> savedataList = new ArrayList<EserviceBuildingDetails>();
		String refNo="",custRefNo="";
		try {
			List<RenewQuotePolicy> list = renewQuotePolicyRepository.findByOldpolicyNo(req.getPolicyNo());
			if (list.size() > 0) {

				SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
				generateSeqReq.setInsuranceId(req.getInsuranceId());
				generateSeqReq.setProductId(req.getProductId());
				generateSeqReq.setType("2");
				generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
				refNo = genSeqNoService.generateSeqCall(generateSeqReq);

				generateSeqReq.setType("1");
				generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
				custRefNo = genSeqNoService.generateSeqCall(generateSeqReq);

				for (RenewQuotePolicy data : list) {

					// Fetch the OLD building rows (may be multiple, e.g. per cover/risk) and
					// carry every original column forward, only overriding renewal-specific fields
					List<EserviceBuildingDetails> oldBuildingList = buildingRepo.findByRequestReferenceNo(data.getOldrequestreferenceNo());
					if (!CollectionUtils.isEmpty(oldBuildingList)) {
						for (EserviceBuildingDetails oldRow : oldBuildingList) {

							EserviceBuildingDetails savedata = dozerMapper.map(oldRow, EserviceBuildingDetails.class);

							savedata.setEntryDate(new Date());
							savedata.setCreatedBy(req.getLoginId());
							savedata.setUpdatedBy(req.getLoginId());
							savedata.setUpdatedDate(new Date());
							savedata.setRequestReferenceNo(refNo);
							savedata.setCustomerReferenceNo(custRefNo);
							savedata.setOldReqRefNo(data.getOldrequestreferenceNo());
							savedata.setOriginalPolicyNo(data.getOldpolicyNo());
							savedata.setPolicyStartDate(data.getNewstartDate());
							savedata.setPolicyEndDate(data.getNewendDate());
							savedata.setActualPremiumFc(BigDecimal.ZERO);
							savedata.setActualPremiumLc(BigDecimal.ZERO);
							savedata.setOverallPremiumFc(BigDecimal.ZERO);
							savedata.setOverallPremiumLc(BigDecimal.ZERO);
							savedata.setQuoteNo("");
							savedata.setStatus("Y");
							savedata.setFinalizeYn("N");
							savedata.setEmiYn("N");
							savedata.setRenewalYn("Y");
							savedata.setSavedFrom("WEB");
							savedata.setEmiPremium(null);
							savedata.setInstallmentPeriod(null);
							savedata.setNoOfInstallment(null);
							savedata.setApplicationId(StringUtils.isBlank(req.getApplicationId()) ? "1" : req.getApplicationId());
							savedata.setSubUserType(req.getSubUserType());
							if (StringUtils.isNotBlank(req.getLoginId())) {
								savedata.setLoginId(req.getLoginId());
							}
							savedata.setBranchCode(req.getBranchCode());
							savedata.setPolicyNo(null);
							savedata.setVatPremium(null);
							savedata.setCdRefno(null);
							savedata.setMsRefno(null);
							savedata.setVdRefno(null);
							savedata.setEndorsementDate(null);
							savedata.setEndorsementEffdate(null);
							savedata.setEndorsementRemarks(null);
							savedata.setEndorsementType(null);
							savedata.setEndorsementTypeDesc(null);
							savedata.setEndtCategDesc(null);
							savedata.setEndtCount(null);
							savedata.setEndtPremium(null);
							savedata.setEndtPrevPolicyNo(null);
							savedata.setEndtPrevQuoteNo(null);
							savedata.setEndtStatus(null);

							buildingRepo.saveAndFlush(savedata);
							savedataList.add(savedata);
						}
					}

					// Section save — a product may have multiple sections, so fetch all
					// existing sections tied to the OLD request reference no and copy each forward
					List<EserviceSectionDetails> oldSections = sectionRepo.findByRequestReferenceNo(data.getOldrequestreferenceNo());
					if (!CollectionUtils.isEmpty(oldSections)) {
						for (EserviceSectionDetails oldSec : oldSections) {
							EserviceSectionDetails savedata3 = dozerMapper.map(oldSec, EserviceSectionDetails.class);
							savedata3.setRequestReferenceNo(refNo);
							savedata3.setCustomerReferenceNo(custRefNo);
							savedata3.setEntryDate(new Date());
							savedata3.setCreatedBy(req.getLoginId());
							savedata3.setUpdatedBy(req.getLoginId());
							savedata3.setUpdatedDate(new Date());
							savedata3.setQuoteNo("");
							savedata3.setStatus("Y");
							savedata3.setEndorsementDate(null);
							savedata3.setEndorsementEffdate(null);
							savedata3.setEndorsementRemarks(null);
							savedata3.setEndorsementType(null);
							savedata3.setEndorsementTypeDesc(null);
							savedata3.setEndtCategDesc(null);
							savedata3.setEndtCount(null);
							savedata3.setEndtPremium(null);
							savedata3.setEndtPrevPolicyNo(null);
							savedata3.setEndtPrevQuoteNo(null);
							savedata3.setEndtStatus(null);
							savedata3.setPolicyNo(null);
							sectionRepo.saveAndFlush(savedata3);
						}
					}

					EserviceCustomerDetails customer = new EserviceCustomerDetails();
					customer.setCustomerReferenceNo(custRefNo);
					customer.setPolicyHolderType(data.getPolicyHolderType());
					customer.setClientName(data.getCustomerName());
					customer.setTitle(data.getTitle());
					customer.setGender(data.getGender());
					customer.setOccupation(data.getOccupation());
					customer.setMobileCode1(data.getMobileCode());
					customer.setMobileNo1(data.getMobileNo());
					customer.setEmail1(data.getEmailId());
					customer.setIdType(data.getIdentityType());
					customer.setIdNumber(data.getIdentityNumber());
					customer.setPreferredNotification(data.getPreferedNotification());
					customer.setIsTaxExempted(data.getTaxExcemption());
					customer.setStreet(data.getStreet());
					customer.setNationality(data.getCountry());
					customer.setRegionCode(data.getRegion());
					customer.setStateCode(Integer.parseInt(data.getDistrict()));
					customer.setPolicyHolderTypeid(data.getPolicyHolderTypeid());
					customer.setCompanyId(data.getCompanyId());
					customer.setProductId(Integer.parseInt(data.getProductCode()));
					customer.setStatus("Y");
					customerRepo.saveAndFlush(customer);

					data.setCurrentStageCode("R");
					data.setCurrentStatus("RENEW-SUCCESS");
					data.setCurrentStatusCode("RS");
					data.setNewRequestRefNo(refNo);
					renewQuotePolicyRepository.saveAndFlush(data);
				}

				if (savedataList != null && !savedataList.isEmpty()) {
					NonMotorPolicyReq policyReq = new NonMotorPolicyReq();
					policyReq.setCreatedBy(savedataList.get(0).getLoginId());
					policyReq.setCompanyId(savedataList.get(0).getCompanyId());
					policyReq.setProductId(savedataList.get(0).getProductId());
					policyReq.setPolicyStartDate(savedataList.get(0).getPolicyStartDate());
					policyReq.setPolicyEndDate(savedataList.get(0).getPolicyEndDate());
					policyReq.setBranchCode(savedataList.get(0).getBranchCode());
					policyReq.setExchangeRate(savedataList.get(0).getExchangeRate()+"");
					policyReq.setCustomerReferenceNo(savedataList.get(0).getCustomerReferenceNo());
					policyReq.setProductName(savedataList.get(0).getProductDesc());
					NonMotorBrokerReq brokerReq = new NonMotorBrokerReq();
					brokerReq.setApplicationId(savedataList.get(0).getApplicationId());
					brokerReq.setBdmCode(savedataList.get(0).getBdmCode());
					brokerReq.setBrokerCode(savedataList.get(0).getBrokerCode());
					brokerReq.setAgencyCode(savedataList.get(0).getAgencyCode());
					brokerReq.setBrokerBranchCode(savedataList.get(0).getBrokerBranchCode());
					brokerReq.setSourceTypeId(savedataList.get(0).getSourceTypeId());
					brokerReq.setSourceType(savedataList.get(0).getSourceType());
					brokerReq.setBdmName(savedataList.get(0).getBdmName());
					brokerReq.setBdmCode(savedataList.get(0).getBdmCode());
					insertHomePositionMaster(refNo, policyReq, brokerReq, savedataList.size());
				}
			}
			res.setRequestReferenceNo(refNo);
			res.setCustomerReferenceNo(custRefNo);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	} 
	
	private CopyQuoteSuccessRes CommonRenewalCopyQuote(RenewalCopyQuoteReq req) {
		CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<EserviceCommonDetails> saveCommonList = new ArrayList<EserviceCommonDetails>();
		String refNo="",custRefNo="";
		try {
			List<RenewQuotePolicy> list = renewQuotePolicyRepository.findByOldpolicyNo(req.getPolicyNo());
			if (list.size() > 0) {

				SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
				generateSeqReq.setInsuranceId(req.getInsuranceId());
				generateSeqReq.setProductId(req.getProductId());
				generateSeqReq.setType("2");
				generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
				refNo = genSeqNoService.generateSeqCall(generateSeqReq);

				generateSeqReq.setType("1");
				generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
				custRefNo = genSeqNoService.generateSeqCall(generateSeqReq);

				for (RenewQuotePolicy data : list) {

					// Fetch the OLD common rows (may be multiple, e.g. per cover) and
					// carry every original column forward, only overriding renewal-specific fields
					List<EserviceCommonDetails> oldCommonList = eserCommonRepo.findByRequestReferenceNo(data.getOldrequestreferenceNo());
					if (!CollectionUtils.isEmpty(oldCommonList)) {
						for (EserviceCommonDetails oldRow : oldCommonList) {

							EserviceCommonDetails savedata2 = dozerMapper.map(oldRow, EserviceCommonDetails.class);

							savedata2.setEntryDate(new Date());
							savedata2.setCreatedBy(req.getLoginId());
							savedata2.setUpdatedBy(req.getLoginId());
							savedata2.setUpdatedDate(new Date());
							savedata2.setRequestReferenceNo(refNo);
							savedata2.setCustomerReferenceNo(custRefNo);
							savedata2.setOldReqRefNo(data.getOldrequestreferenceNo());
							savedata2.setOriginalPolicyNo(data.getOldpolicyNo());
							savedata2.setPolicyStartDate(data.getNewstartDate());
							savedata2.setPolicyEndDate(data.getNewendDate());
							savedata2.setActualPremiumFc(BigDecimal.ZERO);
							savedata2.setActualPremiumLc(BigDecimal.ZERO);
							savedata2.setOverallPremiumFc(BigDecimal.ZERO);
							savedata2.setOverallPremiumLc(BigDecimal.ZERO);
							savedata2.setQuoteNo("");
							savedata2.setStatus("Y");
							savedata2.setFinalizeYn("N");
							savedata2.setRenewalYn("Y");
							savedata2.setSavedFrom("WEB");
							savedata2.setApplicationId(StringUtils.isBlank(req.getApplicationId()) ? "1" : req.getApplicationId());
							savedata2.setSubUserType(req.getSubUserType());
							if (StringUtils.isNotBlank(req.getLoginId())) {
								savedata2.setLoginId(req.getLoginId());
							}
							savedata2.setBranchCode(req.getBranchCode());
							savedata2.setPolicyNo(null);
							savedata2.setVatPremium(null);
							savedata2.setCdRefno(null);
							savedata2.setMsRefno(null);
							savedata2.setVdRefNo(null);
							savedata2.setSourceTypeId(null);
							savedata2.setEndorsementDate(null);
							savedata2.setEndorsementEffdate(null);
							savedata2.setEndorsementRemarks(null);
							savedata2.setEndorsementType(null);
							savedata2.setEndorsementTypeDesc(null);
							savedata2.setEndtCategDesc(null);
							savedata2.setEndtCount(null);
							savedata2.setEndtPremium(null);
							savedata2.setEndtPrevPolicyNo(null);
							savedata2.setEndtPrevQuoteNo(null);
							savedata2.setEndtStatus(null);
							

							eserCommonRepo.saveAndFlush(savedata2);
							saveCommonList.add(savedata2);
						}
					}

					// Section save — fetch existing sections tied to the OLD request reference no
					List<EserviceSectionDetails> oldSections = sectionRepo.findByRequestReferenceNo(data.getOldrequestreferenceNo());
					if (!CollectionUtils.isEmpty(oldSections)) {
						for (EserviceSectionDetails oldSec : oldSections) {
							EserviceSectionDetails savedata3 = dozerMapper.map(oldSec, EserviceSectionDetails.class);
							savedata3.setRequestReferenceNo(refNo);
							savedata3.setCustomerReferenceNo(custRefNo);
							savedata3.setEntryDate(new Date());
							savedata3.setCreatedBy(req.getLoginId());
							savedata3.setUpdatedBy(req.getLoginId());
							savedata3.setUpdatedDate(new Date());
							savedata3.setQuoteNo("");
							savedata3.setStatus("Y");
							savedata3.setEndorsementDate(null);
							savedata3.setEndorsementEffdate(null);
							savedata3.setEndorsementRemarks(null);
							savedata3.setEndorsementType(null);
							savedata3.setEndorsementTypeDesc(null);
							savedata3.setEndtCategDesc(null);
							savedata3.setEndtCount(null);
							savedata3.setEndtPremium(null);
							savedata3.setEndtPrevPolicyNo(null);
							savedata3.setEndtPrevQuoteNo(null);
							savedata3.setEndtStatus(null);
							savedata3.setPolicyNo(null);
							sectionRepo.saveAndFlush(savedata3);
						}
					}

					EserviceCustomerDetails customer = new EserviceCustomerDetails();
					customer.setCustomerReferenceNo(custRefNo);
					customer.setPolicyHolderType(data.getPolicyHolderType());
					customer.setClientName(data.getCustomerName());
					customer.setTitle(data.getTitle());
					customer.setGender(data.getGender());
					customer.setOccupation(data.getOccupation());
					customer.setMobileCode1(data.getMobileCode());
					customer.setMobileNo1(data.getMobileNo());
					customer.setEmail1(data.getEmailId());
					customer.setIdType(data.getIdentityType());
					customer.setIdNumber(data.getIdentityNumber());
					customer.setPreferredNotification(data.getPreferedNotification());
					customer.setIsTaxExempted(data.getTaxExcemption());
					customer.setStreet(data.getStreet());
					customer.setNationality(data.getCountry());
					customer.setRegionCode(data.getRegion());
					customer.setStateCode(Integer.parseInt(data.getDistrict()));
					customer.setPolicyHolderTypeid(data.getPolicyHolderTypeid());
					customer.setCompanyId(data.getCompanyId());
					customer.setProductId(Integer.parseInt(data.getProductCode()));
					customer.setStatus("Y");
					customerRepo.saveAndFlush(customer);

					data.setCurrentStageCode("R");
					data.setCurrentStatus("RENEW-SUCCESS");
					data.setCurrentStatusCode("RS");
					data.setNewRequestRefNo(refNo);
					renewQuotePolicyRepository.saveAndFlush(data);
				}

				if (saveCommonList != null && !saveCommonList.isEmpty()) {
					NonMotorPolicyReq policyReq = new NonMotorPolicyReq();
					policyReq.setCreatedBy(saveCommonList.get(0).getLoginId());
					policyReq.setCompanyId(saveCommonList.get(0).getCompanyId());
					policyReq.setProductId(saveCommonList.get(0).getProductId());
					policyReq.setPolicyStartDate(saveCommonList.get(0).getPolicyStartDate());
					policyReq.setPolicyEndDate(saveCommonList.get(0).getPolicyEndDate());
					policyReq.setBranchCode(saveCommonList.get(0).getBranchCode());
					policyReq.setExchangeRate(saveCommonList.get(0).getExchangeRate()+"");
					policyReq.setCustomerReferenceNo(saveCommonList.get(0).getCustomerReferenceNo());
					policyReq.setProductName(saveCommonList.get(0).getProductDesc());
					NonMotorBrokerReq brokerReq = new NonMotorBrokerReq();
					brokerReq.setApplicationId(saveCommonList.get(0).getApplicationId());
					brokerReq.setBdmCode(saveCommonList.get(0).getBdmCode());
					brokerReq.setBrokerCode(saveCommonList.get(0).getBrokerCode());
					brokerReq.setAgencyCode(saveCommonList.get(0).getAgencyCode());
					brokerReq.setBrokerBranchCode(saveCommonList.get(0).getBrokerBranchCode());
					brokerReq.setSourceTypeId(saveCommonList.get(0).getSourceTypeId());
					brokerReq.setSourceType(saveCommonList.get(0).getSourceType());
					brokerReq.setBdmName(saveCommonList.get(0).getBdmName());
					insertHomePositionMaster(refNo, policyReq, brokerReq, saveCommonList.size());
				}
			}
			res.setRequestReferenceNo(refNo);
			res.setCustomerReferenceNo(custRefNo);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	private HomePositionMaster insertHomePositionMaster(String requestReferenceNo, NonMotorPolicyReq policyReq,NonMotorBrokerReq brokerReq,int size)
	{
		HomePositionMaster home = null;
	long a=99999;
	boolean isSameDate=false;
	try {
		home = homePosistionRepo.findByRequestReferenceNoOrderByNoOfVehiclesAsc(requestReferenceNo);
		if (home != null) {
			if (StringUtils.isBlank(home.getQuoteNo())) {
				Date existingStart = home.getInceptionDate();
				Date requestStart = policyReq.getPolicyStartDate();

				isSameDate = existingStart != null && requestStart != null
						&& existingStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
								.equals(requestStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			} else {
				isSameDate = true;
			}

		}
		if (!isSameDate) {
			home = new HomePositionMaster();
			home.setNoOfVehicles(size);
			home.setSectionId(0);
			home.setAmendId(0);
			home.setApplicationNo(a);			
			home.setApplicationId(StringUtils.isBlank(brokerReq.getApplicationId()) ? "0" : brokerReq.getApplicationId());
			home.setBdmCode(StringUtils.isBlank(brokerReq.getBdmCode()) ? "0" : brokerReq.getBdmCode());
			home.setBdmName(brokerReq.getBdmName());
			home.setLoginId(StringUtils.isBlank(policyReq.getCreatedBy()) ? null : policyReq.getCreatedBy());
			home.setCustomerId("99999");
			home.setRequestReferenceNo(requestReferenceNo);
			home.setCompanyId(StringUtils.isBlank(policyReq.getCompanyId()) ? null : policyReq.getCompanyId());
			home.setProductId(StringUtils.isBlank(policyReq.getProductId()) ? null: Integer.valueOf(policyReq.getProductId()));
			home.setBrokerCode(StringUtils.isBlank(brokerReq.getBrokerCode()) ? null : brokerReq.getBrokerCode());
			home.setAgencyCode(StringUtils.isBlank(brokerReq.getAgencyCode()) ? null: Integer.valueOf(brokerReq.getAgencyCode()));
			home.setEntryDate(policyReq.getPolicyStartDate() == null ? null : policyReq.getPolicyStartDate());
			home.setInceptionDate(policyReq.getPolicyStartDate() == null ? null : policyReq.getPolicyStartDate());
			home.setEffectiveDate(policyReq.getPolicyStartDate() == null ? null : policyReq.getPolicyStartDate());
			home.setExpiryDate(policyReq.getPolicyEndDate() == null ? null : policyReq.getPolicyEndDate());	
			home.setBranchCode(StringUtils.isBlank(policyReq.getBranchCode()) ? null : policyReq.getBranchCode());
			home.setExchangeRate(StringUtils.isBlank(policyReq.getExchangeRate()) ? null: new BigDecimal(policyReq.getExchangeRate()));
			home.setCustomerCode(StringUtils.isBlank(policyReq.getCustomerReferenceNo()) ? "99999" : policyReq.getCustomerReferenceNo());
			home.setProductName(policyReq.getProductName());
			home.setBrokerBranchCode(StringUtils.isBlank(brokerReq.getBrokerBranchCode()) ? null: brokerReq.getBrokerBranchCode());
			home.setStatus("Y");	
			home.setSourceTypeId(StringUtils.isBlank(brokerReq.getSourceTypeId()) ? null : brokerReq.getSourceTypeId());
			home.setSourceType(StringUtils.isBlank(brokerReq.getSourceType()) ? null : brokerReq.getSourceType());
			
			homePosistionRepo.saveAndFlush(home);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception Is HomePositionMaster ---> " + e.getMessage());
		return null;
	}
	return home;

	}


}
