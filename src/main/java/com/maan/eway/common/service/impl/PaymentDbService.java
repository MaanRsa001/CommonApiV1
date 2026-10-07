package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.DepositcbcMaster;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataIndividuals;
import com.maan.eway.bean.RenewQuotePolicy;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpConfigMaster;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentLog;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentTracking;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpConfigMasterRepository;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentLogRepository;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentTrackingRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.DepositcbcMasterRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.RenewQuotePolicyRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.service.CalculatorEngine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class PaymentDbService {

    @Autowired private HomePositionMasterRepository homerepo;
    @Autowired private PersonalInfoRepository personalrepo;
    @Autowired private PaymentInfoRepository paymentinforepo;
    @Autowired private PaymentDetailRepository paymentdetailrepo;
    @Autowired private EmiTransactionDetailsRepository emiRepo;
    @Autowired private LoginUserInfoRepository loginUserRepo;
    @Autowired private MotorDataDetailsRepository motorRepo;
    @Autowired private DepositcbcMasterRepository depositcbcRepo;
    @Autowired private MtpPaymentTrackingRepository trackingRepo;
    @Autowired private MtpPaymentLogRepository logRepo;
    @Autowired private MtpConfigMasterRepository mtpConfigRepo;
	@Autowired
	private EServiceSectionDetailsRepository sectionRepo ;
	
	@Autowired
	private TravelPassengerDetailsRepository passengerRepo ;
	
	@Autowired
	private CommonDataDetailsRepository commonRepo ;
    @Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	@Autowired
	private RenewQuotePolicyRepository renewQuotePolicyRepo;
	
	@Lazy
	@Autowired
	private CalculatorEngine calcService;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	@Autowired
	private BuildingRiskDetailsRepository buildingRiskRepo;
	@PersistenceContext
	private EntityManager em;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;

	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildingRepo;
	

	@Autowired
	private  SectionDataDetailsRepository sddRepo;

    private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);

    @Transactional(readOnly = true)
    public HomePositionMaster findHome(String quoteNo) {
        return homerepo.findByQuoteNo(quoteNo);
    }

    @Transactional(readOnly = true)
    public PersonalInfo findPersonalInfo(String customerId) {
        return personalrepo.findByCustomerId(customerId);
    }

    @Transactional(readOnly = true)
    public PaymentInfo findPaymentInfo(PaymentDetailsSaveReq req, HomePositionMaster data) {
        if (data != null && data.getEndtTypeId() != null && req.getEmiYn().equalsIgnoreCase("Y")) {
            return paymentinforepo.findLatestByQuoteNo(req.getQuoteNo()).get();
        } else {
            return paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
        }
    }

    @Transactional(readOnly = true)
    public java.util.List<EmiTransactionDetails> findEmiDetails(String quoteNo) {
        return emiRepo.findByQuoteNoOrderByBalanceAmountDesc(quoteNo);
    }

    @Transactional(readOnly = true)
    public LoginUserInfo findLoginUser(String loginId) {
        return loginUserRepo.findByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public MotorDataDetails findMotorData(String quoteNo, String vehicleId) {
        return motorRepo.findByQuoteNoAndVehicleId(quoteNo, vehicleId);
    }

    @Transactional(readOnly = true)
    public java.util.List<DepositcbcMaster> findDepositCbc(String brokerId) {
        return depositcbcRepo.findByBrokerId(brokerId);
    }

    // ---------- WRITES: initial save (paymentDetail + paymentInfo + home) ----------

    @Transactional
    public void persistPaymentAndInfo(PaymentDetail paymentDetail, PaymentInfo paymentInfo, HomePositionMaster data) {
        paymentdetailrepo.saveAndFlush(paymentDetail);
        paymentinforepo.saveAndFlush(paymentInfo);
        homerepo.saveAndFlush(data);
    }

    @Transactional
    public void saveEmiTransaction(EmiTransactionDetails data1) {
        emiRepo.saveAndFlush(data1);
    }
    
    @Transactional(readOnly = true)
    public HomePositionMaster findHomeByPolicyNo(String policyNo) {
        return homerepo.findByPolicyNo(policyNo);
    }
    @Transactional(readOnly = true)
    public MotorDataDetails findMotorTopByQuoteNo(String quoteNo) {
        return motorRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);
    }
    
    @Transactional(readOnly = true)
    public Optional<MtpPaymentTracking> findMtpTracking(String quoteNo) {
        return trackingRepo.findByQuoteNoAndStatusIn(quoteNo, Arrays.asList("PENDING", "SUCCESS"));
    }

    @Transactional
    public void persistPolicyResult(HomePositionMaster data, PaymentInfo paymentInfo, PaymentDetailsSaveReq req,
                                     PaymentDetail paymentDetail, String policyNo,
                                     String debitNo, Date debitDate, String debitTo,
                                     String creditNo, Date creditDate, String creditTo) {
        data.setDebitNoteNo(debitNo);
        data.setDebitNoteDate(debitDate);
        data.setDebitTo(debitTo);
        data.setCreditNo(creditNo);
        data.setCreditDate(creditDate);
        data.setCreditTo(creditTo);
        data.setPaymentMode(req.getPaymentType());
        data.setPaymentType(paymentDetail.getPaymentTypedesc());
        data.setPaymentStatus(paymentInfo.getPaymentStatus());
        data.setPolicyNo(policyNo);
        data.setStatus(org.apache.commons.lang3.StringUtils.isNotBlank(data.getEndtTypeId())
                && "842".equalsIgnoreCase(data.getEndtTypeId()) ? "D" : "P");
        data.setIntegrationStatus("S");
        data.setEmiYn(paymentInfo.getEmiYn());
        data.setInstallmentPeriod(paymentInfo.getInstallmentPeriod());
        if (org.apache.commons.lang3.StringUtils.isNotBlank(data.getEndtTypeId())) {
            data.setEndtStatus("C");
        } else {
            data.setOriginalPolicyNo(policyNo);
        }
        homerepo.saveAndFlush(data);
    }

    // ---------- WRITES: preinspection flag update ----------

    @Transactional
    public HomePositionMaster markPreinspectionPending(String quoteNo) {
        HomePositionMaster data = homerepo.findByQuoteNo(quoteNo);
        data.setSummaryRemarks("Y");
        data.setRemarks("PREINSPECTION_PENDING");
        homerepo.save(data);
        return data;
    }

    // ---------- WRITES: home status update after final response is known ----------

    @Transactional
    public void updateHomeAfterPayment(HomePositionMaster data, PaymentDetailsSaveReq req,
                                        PaymentDetail paymentDetail, PaymentInfo paymentInfo) {
        data.setPaymentMode(req.getPaymentType());
        data.setPaymentType(paymentDetail.getPaymentTypedesc());
        data.setPaymentStatus(paymentInfo.getEmiYn().equalsIgnoreCase("Y") ? paymentInfo.getPaymentStatus() : "Pending");
        data.setEffectiveDate(new Date());
        data.setPolicyCovertedDate(new Date());
        homerepo.saveAndFlush(data);
    }
    
    @Transactional
    public MtpPaymentTracking persistMtpSuccessState(PaymentInfo originalPaymentInfo, PaymentDetail employeePaymentDetail,
            String quoteNo, String policyNo, String companyId, PaymentDetailsSaveReq req,
            String paymentRequestId, String paymentChannel, String returnMessage, String mtpResponseJson) {

        String mtpPaymentId = insertMtpPaymentInfo(originalPaymentInfo); // move this helper here too, or call via injected bean
        String merchantRef = "MTP-" + quoteNo + "-A1-" + System.currentTimeMillis();
        employeePaymentDetail.setPaymentId(mtpPaymentId);
        employeePaymentDetail.setMerchantReference(merchantRef);
        employeePaymentDetail.setReference(paymentRequestId);
        employeePaymentDetail.setChannel(paymentChannel);
        employeePaymentDetail.setResponseMessage(returnMessage);
        employeePaymentDetail.setPayments("MTP");
        paymentdetailrepo.saveAndFlush(employeePaymentDetail);

        MtpPaymentTracking tracking = createMtpTrackingRow(quoteNo, policyNo, companyId,
                req.getInsurancePayename(), req.getInsurancePayenumber(), 1); // move this helper here too

        MtpPaymentLog mtpLog = new MtpPaymentLog();
        mtpLog.setTrackingId(tracking.getId());
        mtpLog.setQuoteNo(quoteNo);
        mtpLog.setAttemptNo(1);
        mtpLog.setSource("INITIAL");
        mtpLog.setPaymentRequestId(paymentRequestId);
        mtpLog.setPaymentChannel(paymentChannel);
        mtpLog.setRequestJson(employeePaymentDetail.toString());
        mtpLog.setResponseJson(mtpResponseJson);
        mtpLog.setRequestTime(new Date());
        mtpLog.setResponseTime(new Date());
        mtpLog.setStatus("INITIATED");
        mtpLog.setEntryDate(new Date());
        logRepo.save(mtpLog);

        return tracking;
    }
    
    private String insertMtpPaymentInfo(PaymentInfo originalPaymentInfo) {
		String mtpPaymentId = "MTP" + System.currentTimeMillis();
		PaymentInfo mtpInfo = new PaymentInfo();
		mtpInfo.setQuoteNo(originalPaymentInfo.getQuoteNo());
		mtpInfo.setPaymentId(mtpPaymentId);
		mtpInfo.setMerchantReference("MTP-" + originalPaymentInfo.getMerchantReference());
		mtpInfo.setCustomerName(originalPaymentInfo.getCustomerName());
		mtpInfo.setMobileNo(originalPaymentInfo.getMobileNo());
		mtpInfo.setEmailId(originalPaymentInfo.getEmailId());
		mtpInfo.setPaymentStatus("PENDING");
		mtpInfo.setCompanyId(originalPaymentInfo.getCompanyId());
		mtpInfo.setCompanyName(originalPaymentInfo.getCompanyName());
		mtpInfo.setProductId(originalPaymentInfo.getProductId());
		mtpInfo.setProductDesc(originalPaymentInfo.getProductDesc());
		mtpInfo.setPolicyStartDate(originalPaymentInfo.getPolicyStartDate());
		mtpInfo.setPolicyEndDate(originalPaymentInfo.getPolicyEndDate());
		mtpInfo.setPremium(originalPaymentInfo.getPremium());
		mtpInfo.setPremiumFc(originalPaymentInfo.getPremiumFc());
		mtpInfo.setPremiumLc(originalPaymentInfo.getPremiumLc());
		mtpInfo.setExchangeRate(originalPaymentInfo.getExchangeRate());
		mtpInfo.setCurrencyId(originalPaymentInfo.getCurrencyId());
		mtpInfo.setLoginId(originalPaymentInfo.getLoginId());
		mtpInfo.setEmiYn("N");
		mtpInfo.setBranchCode(originalPaymentInfo.getBranchCode());
		mtpInfo.setBranchName(originalPaymentInfo.getBranchName());
		mtpInfo.setStatus("Y");
		mtpInfo.setPayments("MTP");
		mtpInfo.setEntryDate(new Date());
		mtpInfo.setCreatedBy("SYSTEM");
		mtpInfo.setUpdatedDate(new Date());
		mtpInfo.setUpdatedBy("SYSTEM");
		paymentinforepo.save(mtpInfo);
		log.info("MTP PaymentInfo inserted for quoteNo {} paymentId {}", originalPaymentInfo.getQuoteNo(),
				mtpPaymentId);
		return mtpPaymentId;
	}
    
    private MtpPaymentTracking createMtpTrackingRow(String quoteNo, String policyNo, String companyId,
	        String employeeName, String employeeMobile, int currentAttempt) {

	    // ── Use findFirst to avoid IncorrectResultSizeDataAccessException ─────
	    MtpConfigMaster config = mtpConfigRepo
	        .findFirstByCompanyIdAndStatusOrderByIdDesc(companyId, "Y")
	        .orElse(null);

	    int maxAttempts     = config != null ? config.getMaxAttempts()     : 3;
	    int intervalMinutes = config != null ? config.getIntervalMinutes() : 2;

	    Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.MINUTE, intervalMinutes);

	    MtpPaymentTracking tracking = new MtpPaymentTracking();
	    tracking.setQuoteNo(quoteNo);
	    tracking.setPolicyNo(policyNo);
	    tracking.setCompanyId(companyId);
	    tracking.setEmployeeName(employeeName);
	    tracking.setEmployeeMobile(employeeMobile);
	    tracking.setCurrentAttempt(currentAttempt);
	    tracking.setMaxAttempts(maxAttempts);
	    tracking.setIntervalMinutes(intervalMinutes);
	    tracking.setNextAttemptDue(cal.getTime());
	    tracking.setStatus("PENDING");
	    tracking.setEntryDate(new Date());
	    tracking.setUpdatedDate(new Date());
	    trackingRepo.save(tracking);
	    return tracking;
	}
    
    @Transactional
    public void createMtpTrackingRowOnFailure(String quoteNo, String policyNo, String companyId,
            String payeeName, String payeeNumber, int attemptNo) {
        createMtpTrackingRow(quoteNo, policyNo, companyId, payeeName, payeeNumber, attemptNo); // same helper moved here
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
	 public  String updateProductWisePolicyNo(String productId , String policyNo , String quoteNo,String endttypeId, String motorYn ,BigDecimal commissionPercent ) {
		 String res = "" ;
		 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		 try {
			 
	    	   if(motorYn.equalsIgnoreCase("M") ) {
	    		   
	    		   // Update Main Motor
	    		   List<MotorDataDetails> motorList =  motorRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   motorList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				 //  o.setCommissionPercentage(commissionPercent);
	    				   
	    			   });
					} else {
					   motorList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							  // o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
			    			  
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			  // o.setCommissionPercentage(commissionPercent);
			    		    
		    		   });
					}
	    		   motorRepo.saveAllAndFlush(motorList);
	    		  
	    		// Update Eservice Motor
	    		  List<EserviceMotorDetails> eserMotorsList =  eserMotRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    		  List<EserviceMotorDetails> updateEserList = new ArrayList<EserviceMotorDetails>(); 
	    		  eserMotorsList.forEach( o -> {
	    			  
   			  List<MotorDataDetails> filterMotor = motorList.stream().filter( e -> e.getVehicleId().equals(o.getRiskId().toString())
   					  && e.getSectionId().equals(Integer.valueOf(o.getSectionId())) ).collect(Collectors.toList());
   			  
   			  if( filterMotor.size()> 0 ) {
   				  EserviceMotorDetails updateEser = o ; 
   				  dozerMapper.map(filterMotor.get(0) , updateEser);
   				  updateEserList.add(updateEser);
   			   }
	    		 List<RenewQuotePolicy>rlist=renewQuotePolicyRepo.findByOldpolicyNo(o.getOldPolicyNumber());
	    		 rlist.forEach(i -> {
	    			 RenewQuotePolicy rdata=i;
	    			 rdata.setNewpolicyNumber(policyNo);
	    			 rdata.setCurrentStageCode("C");
	    			 rdata.setCurrentStatus("CONVERT-SUCCESS");
	    			 rdata.setCurrentStatusCode("CS");
	    			 renewQuotePolicyRepo.saveAndFlush(rdata);
	    			 }) ;   		 
	    		  });	    		   
	    		  eserMotRepo.saveAllAndFlush(updateEserList);
	    		  
	    	   } else  if(motorYn.equalsIgnoreCase("H")  && productId.equalsIgnoreCase(travelProductId) ) {
	    		   
	    		   // Update Main Travel
	    		   List<TravelPassengerDetails> passengerList =  passengerRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   passengerList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				//   o.setCommissionPercentage(commissionPercent);
	    			   });
					} else {
						passengerList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							 //  o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			 //  o.setCommissionPercentage(commissionPercent);
			    		    
		    		   });
					}
	    		   passengerRepo.saveAllAndFlush(passengerList);
	    		  
	    		// Update Eservice Travel
	    		  EserviceTravelDetails eserTravel =  eserTraRepo.findByQuoteNo(quoteNo);
	    		  eserTravel.setPolicyNo(policyNo);
	    		  eserTravel.setStatus(StringUtils.isNotBlank(endttypeId) && "842".equalsIgnoreCase(endttypeId) ? "D" : "P");
	    		  eserTravel.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
	    		//  eserTravel.setCommissionPercentage(commissionPercent);
	    		  eserTraRepo.saveAndFlush(eserTravel);
	    		  
	    	   } else  if(motorYn.equalsIgnoreCase("A") ) {
	    		   
	    		// Update Main Asset
	    		   List<BuildingRiskDetails> buildingList =  buildingRiskRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   buildingList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    			   });
					} else {
						buildingList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							//   o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
			    			   
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			 //  o.setCommissionPercentage(commissionPercent);
			    		    
		    		   });
					}
	    		   buildingRiskRepo.saveAllAndFlush(buildingList);
	    		  
	    		// Update Eservice Asset
	    		   List<EserviceBuildingDetails> updateEserList = new ArrayList<EserviceBuildingDetails>();
	    		   List<EserviceBuildingDetails> eserBuildingList =  eserBuildingRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
		    	   eserBuildingList.forEach( o -> {
		    			  
	    			  List<BuildingRiskDetails> filterAsset = buildingList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
	    					  && e.getSectionId().equals(o.getSectionId())
	    					  && String.valueOf(e.getCoverId()).equals(String.valueOf(o.getCoverId()))
	    					  && e.getLocationId().equals(o.getLocationId()) ).collect(Collectors.toList());
	    			  
	    			  if( filterAsset.size()> 0 ) {
	    				  EserviceBuildingDetails updateEser = o; 
	    				  dozerMapper.map(filterAsset.get(0) , updateEser);
	    				  updateEserList.add(updateEser);
	    			   }
	    			  
	    			  if (StringUtils.isNotBlank(o.getOriginalPolicyNo())) {
	    					List<RenewQuotePolicy> rlist = renewQuotePolicyRepo.findByOldpolicyNo(o.getOriginalPolicyNo());
	    					rlist.forEach(i -> {
	    						RenewQuotePolicy rdata = i;
	    						rdata.setNewpolicyNumber(policyNo);
	    						rdata.setCurrentStageCode("C");
	    						rdata.setCurrentStatus("CONVERT-SUCCESS");
	    						rdata.setCurrentStatusCode("CS");
	    						renewQuotePolicyRepo.saveAndFlush(rdata);
	    					});
	    				}
		    		 
		    		  });	    		   
	    		   eserBuildingRepo.saveAllAndFlush(updateEserList);
		    		  
	    		
	    		 
	    		// Update Main Human
	    		   List<CommonDataDetails> humanList =  commonRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   humanList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				//   o.setCommissionPercentage(commissionPercent);
	    			   });
					} else {
						humanList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							 //  o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			//   o.setCommissionPercentage(commissionPercent);
		    			    
		    		   });
					}
	    		   commonRepo.saveAllAndFlush(humanList);
	    		  
	    		   
	    		// Update Eservice Asset
	    		   List<EserviceCommonDetails> updateHumanEserList = new ArrayList<EserviceCommonDetails>();
	    		   List<EserviceCommonDetails> eserHumanList =  eserCommonRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    		   eserHumanList.forEach( o -> {
		    			  
	    			  List<CommonDataDetails> filterHuman = humanList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
	    					  && String.valueOf(e.getCoverId()).equals(String.valueOf(o.getCoverId()))
	    					  && e.getSectionId().equals(o.getSectionId())&& e.getLocationId().equals(o.getLocationId()) ).collect(Collectors.toList());
	    			  
	    			  if( filterHuman.size()> 0 ) {
	    				  EserviceCommonDetails updateEser = o ; 
	    				  dozerMapper.map(filterHuman.get(0) , updateEser);
	    				  updateHumanEserList.add(updateEser);
	    			   }
	    			  
	    			  if (StringUtils.isNotBlank(o.getOriginalPolicyNo())) {
	    					List<RenewQuotePolicy> rlist = renewQuotePolicyRepo.findByOldpolicyNo(o.getOriginalPolicyNo());
	    					rlist.forEach(i -> {
	    						RenewQuotePolicy rdata = i;
	    						rdata.setNewpolicyNumber(policyNo);
	    						rdata.setCurrentStageCode("C");
	    						rdata.setCurrentStatus("CONVERT-SUCCESS");
	    						rdata.setCurrentStatusCode("CS");
	    						renewQuotePolicyRepo.saveAndFlush(rdata);
	    					});
	    				}
		    		 
		    		  });	    		   
	    		   eserCommonRepo.saveAllAndFlush(updateHumanEserList);
		    		  
	    		
	    	
	    		  
	    	   } else {
	    		// Update Main Human
	    		   List<CommonDataDetails> humanList =  commonRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   humanList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				  // o.setCommissionPercentage(commissionPercent);
	    			   });
					} else {
						humanList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
			    			   o.setStatus("P");
						   }   
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			//   o.setCommissionPercentage(commissionPercent);
			    		   
		    			    
		    		   });
					}
	    		   commonRepo.saveAllAndFlush(humanList);
	    		  
	    		// Update Eservice Asset
	    		   List<EserviceCommonDetails> updateHumanEserList = new ArrayList<EserviceCommonDetails>();
	    		   List<EserviceCommonDetails> eserHumanList =  eserCommonRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    		   eserHumanList.forEach( o -> {
		    			  
	    			  List<CommonDataDetails> filterHuman = humanList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
	    					  && e.getSectionId().equals(o.getSectionId()) && e.getLocationId().equals(o.getLocationId()) 
	    					  && String.valueOf(e.getCoverId()).equals(String.valueOf(o.getCoverId()))).collect(Collectors.toList());
	    			  
	    			  if( filterHuman.size()> 0 ) {
	    				  EserviceCommonDetails updateEser = o; 
	    				  dozerMapper.map(filterHuman.get(0) , updateEser);
	    				  updateHumanEserList.add(updateEser);
	    			   }
		    		 
		    		  });	    		   
	    		   eserCommonRepo.saveAllAndFlush(updateHumanEserList);
	    	   }
	    	   
	    	   // Policy Cover Data
	    	   {
	    		   CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<PolicyCoverData> update = cb.createCriteriaUpdate(PolicyCoverData.class);
					// set the root class
					Root<PolicyCoverData> m = update.from(PolicyCoverData.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					// Cancellation Condition
					if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
						update.where(n1);
					} else {
						Predicate n2 = cb.notEqual(m.get("status"),"D" );
						update.where(n1,n2);
					}
					// perform update
					em.createQuery(update).executeUpdate();
	    	   }
	    	   
	    	   // Policy Cover Data Induviduals
	    	   {
	    		   CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<PolicyCoverDataIndividuals> update = cb.createCriteriaUpdate(PolicyCoverDataIndividuals.class);
					// set the root class
					Root<PolicyCoverDataIndividuals> m = update.from(PolicyCoverDataIndividuals.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					// Cancellation Condition
					if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
						update.where(n1);
					} else {
						Predicate n2 = cb.notEqual(m.get("status"),"D" );
						update.where(n1,n2);
					}
					// perform update
					em.createQuery(update).executeUpdate();  
	    	   }
	    	   
	    	   // Section Update
	    	   List<SectionDataDetails> secList =  sddRepo.findByQuoteNo(quoteNo);
   		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
   			   secList.forEach( o -> {
   				   o.setPolicyNo(policyNo);
   				   o.setStatus("D");
   				   o.setEndtStatus("C");
   			   });
				} else {
					secList.forEach( o -> {
					   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
						   o.setPolicyNo(policyNo);
		    			   o.setStatus("P");
					   }
		    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
		    		    
	    		   });
				}
   		   sddRepo.saveAllAndFlush(secList);
   		  
   		// Update Eservice Asset
   		   List<EserviceSectionDetails> updateEserList = new ArrayList<EserviceSectionDetails>();
   		   List<EserviceSectionDetails> eserBuildingList =  sectionRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    	   eserBuildingList.forEach( o -> {
	    			  
   			  List<SectionDataDetails> filterAsset = secList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
   					  && e.getSectionId().equals(o.getSectionId()) 
   					  && String.valueOf(e.getCoverId()).equals(String.valueOf(o.getCoverId()))
   					  &&e.getLocationId().equals(o.getLocationId())).collect(Collectors.toList());
   			  
   			  if( filterAsset.size()> 0 ) {
   				  EserviceSectionDetails updateEser = o; 
   				  dozerMapper.map(filterAsset.get(0) , updateEser);
   				  updateEserList.add(updateEser);
   			   }
	    		 
	    		  });	    		   
	    	   sectionRepo.saveAllAndFlush(updateEserList);

	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
    
    
    
}
