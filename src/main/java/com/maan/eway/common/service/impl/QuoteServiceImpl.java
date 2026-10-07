package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceLifeDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceNomineeDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.EwaySharePercentage;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataIndividuals;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.TravelPassengerHistory;
import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.bean.UWRefferralHistory;
import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.req.ChangeFinalyzereq;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.DeleteOldQuoteReq;
import com.maan.eway.common.req.EmployeeCountGetReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.SectionSumInsuredGetReq;
import com.maan.eway.common.req.TracesRemovedReq;
import com.maan.eway.common.req.UpdatePolicyStartEndDateReq;
import com.maan.eway.common.req.UpdateQuoteStatusReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsRes;
import com.maan.eway.common.res.DocumentDetails;
import com.maan.eway.common.res.DriverDetailsRes;
import com.maan.eway.common.res.EndorsementEmiDetailsRes;
import com.maan.eway.common.res.EserviceCommonGetRes;
import com.maan.eway.common.res.EserviceMotorDetailsRes;
import com.maan.eway.common.res.EserviceTravelGetRes;
import com.maan.eway.common.res.LocationDetailsRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.OverallPremiumRes;
import com.maan.eway.common.res.PaccGetRes;
import com.maan.eway.common.res.QuoteDetailsRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.SectionDetailsRes;
import com.maan.eway.common.res.TaxDetailsRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.endorsment.request.EndorsementEmiReq;
import com.maan.eway.endorsment.service.EndorsementEmiService;
import com.maan.eway.error.Error;
import com.maan.eway.master.controller.ProductGroupDropDownReq;
import com.maan.eway.master.req.CoInsuranceSaveReq;
import com.maan.eway.master.req.TrackingDetailsSaveReq;
import com.maan.eway.master.res.ProductGroupMasterDropDownRes;
import com.maan.eway.master.service.ProductGroupMasterService;
import com.maan.eway.master.service.TrackingDetailsService;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceLifeDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.EwaySharePercentageRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.ProductSectionMasterRepository;
import com.maan.eway.repository.SectionCoverMasterRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;
import com.maan.eway.repository.UWReferralDetailsRepository;
import com.maan.eway.repository.UWReferralHistoryRepository;
import com.maan.eway.res.BuildingSumInsuredDetails;
import com.maan.eway.res.CommonSumInsuredDetails;
import com.maan.eway.res.CoverRes;
import com.maan.eway.res.EndorsmentRes;
import com.maan.eway.res.EserviceBuildingsDetailsRes;
import com.maan.eway.res.GetEmployeeCountRes;
import com.maan.eway.res.GroupSuminsuredDetailsRes;
import com.maan.eway.res.MotorSuminsuredDetails;
import com.maan.eway.res.OccupationReqClass;
import com.maan.eway.res.PassengerSectionDetails;
import com.maan.eway.res.SectionDetails;
import com.maan.eway.res.SectionWiseSumInsuredRes;
import com.maan.eway.res.SubCoverRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;


@Service 
@Transactional
public class QuoteServiceImpl implements QuoteService {
 
	private static final int ArrayList = 0;


	@Value(value = "${travel.productId}")
	private String travelProductId;

	 
	@PersistenceContext
	private EntityManager em;

	@Lazy
	@Autowired
	private QuoteThreadService otSer ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private PersonalInfoRepository custRepo ;
	
	@Autowired
	private SectionCoverMasterRepository sectioncoverrepo ;
	
	@Autowired
	private MotorDriverDetailsRepository driverRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	
	@Autowired
	private FactorRateRequestDetailsRepository eserCovRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private CoverDetailsRepository coverRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo;
	
	@Autowired
	private EserviceTravelGroupDetailsRepository eserGroupRepo;
	
	@Autowired
	private TravelPassengerDetailsRepository traPassRepo  ;
	
	@Autowired
	private TravelPassengerHistoryRepository traPassHisRepo  ;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo  ;
	
	@Autowired
	private SectionDataDetailsRepository secDataRepo  ;
	
	@Autowired
	private BuildingRiskDetailsRepository buildRiskRepo  ;
	
	
	@Autowired
	private EmiTransactionDetailsRepository emiRepo ;
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	
	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserviceTravelRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerDetailsRepo;
	
	@Autowired
	private BuildingDetailsRepository BuildingRepo;
	
	@Autowired
	private ProductGroupMasterService groupService;
		
	
	@Autowired
	private ProductEmployeesDetailsRepository personalRepo;


	private ProductMasterRepository productRepo;
	
	@Autowired
	private TrackingDetailsService trackingService;
	
	@Autowired
	private FactorRateRequestDetailsRepository facRateRepo ;
	
	@Autowired
	private ProductEmployeesDetailsRepository empRepo ;
	
	@Autowired
	private ContentAndRiskRepository contentRepo ;
	
	@Autowired
	private DocumentTransactionDetailsRepository docRepo ;

	@Autowired
	private UWReferralHistoryRepository uwReferralHistRepo ;
	
	@Autowired
	private UWReferralDetailsRepository uwReferralRepo ;
	
	@Autowired
	private PaymentDetailRepository paymentRepo ;
	
	@Autowired
	private EserviceLifeDetailsRepository lifeRepo;
	
	@Autowired
	private ProductSectionMasterRepository productSectionMasterRepo;
	
	@Autowired
	private EwaySharePercentageRepository coInsRepo;
	
	@Autowired
	private EndorsementEmiService endtEmiService;
	
	@Autowired
	private ReferralStatusUpdateService referralStatusUpdateService;
	
	@Autowired
	@Qualifier("customTaskExecutor")
	private Executor taskExecutor;
	
	private Logger log = LogManager.getLogger(QuoteServiceImpl.class);
	
	@Override
	public CommonRes generateNewQuote(NewQuoteReq req) {
			CommonRes	res = otSer.call_OT_Insert(req);
			return res ;
			
	}

	@Override
	public ViewQuoteRes viewQuoteDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		
		try {
			// Quote Details
			HomePositionMaster homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());
			QuoteDetailsRes quoteRes = new QuoteDetailsRes();
			DozerBeanMapper dozerMappper = new DozerBeanMapper();
			quoteRes = dozerMappper.map(homeData, QuoteDetailsRes.class);
			quoteRes.setOverAllPremiumFc(homeData.getOverallPremiumFc()==null?"":homeData.getOverallPremiumFc().toPlainString() );
			quoteRes.setOverAllPremiumLc(homeData.getOverallPremiumLc()==null?"":homeData.getOverallPremiumLc().toPlainString());
			quoteRes.setPremiumFc(homeData.getPremiumFc()==null?"":homeData.getPremiumFc().toPlainString() );
			quoteRes.setPremiumLc(homeData.getPremiumLc()==null?"":homeData.getPremiumLc().toPlainString());
			quoteRes.setCommissionPercentage(homeData.getCommissionPercentage()==null?"":homeData.getCommissionPercentage().toPlainString());
			quoteRes.setVatCommission(homeData.getVatCommission()==null?"":homeData.getVatCommission().toPlainString());
			if (Integer.valueOf(4).equals(homeData.getProductId())) {
				EserviceTravelDetails byQuoteNo = eserviceTravelRepo.findByQuoteNo(homeData.getQuoteNo());
				String lcRate = (byQuoteNo.getLcExchangeRate() != null)
						 
                        ? byQuoteNo.getLcExchangeRate().toPlainString()

                        : null;
				quoteRes.setLcExchangeRate(lcRate);
			}		
			quoteRes.setAdminRemarks(homeData.getAdminRemarks());
			quoteRes.setReferalRemarks(homeData.getReferralDescription());
			quoteRes.setBrokerBranchCode(homeData.getBrokerBranchCode());
			quoteRes.setBrokerCode(homeData.getBrokerCode());
			quoteRes.setHavepromocode(homeData.getHavepromoYn());
			quoteRes.setPromocode(homeData.getPromocode());
			quoteRes.setBdmCode(homeData.getBdmCode());
			quoteRes.setSourceType(homeData.getSourceType());
			quoteRes.setUserType(homeData.getUserType());
			quoteRes.setSubUserType(homeData.getSubUserType());	
			quoteRes.setProductName(homeData.getProductName());
			quoteRes.setCompanyName(homeData.getCompanyName());
			quoteRes.setCustomerCode(homeData.getCustomerCode());
			quoteRes.setBranchName(homeData.getBranchName());
			quoteRes.setBrokerBranchName(homeData.getBrokerBranchName());		
			//quoteRes.setEmiYn("N");
			quoteRes.setEndtTypeId(homeData.getEndtTypeId()==null?null:homeData.getEndtTypeId());
			quoteRes.setEndtTypeDesc(homeData.getEndtTypeDesc()==null?"":homeData.getEndtTypeDesc());
			quoteRes.setEndtCategDesc(homeData.getEndtCategDesc()==null?null:homeData.getEndtCategDesc());
			quoteRes.setEndorsementRemarks(homeData.getEndorsementRemarks()==null?null:homeData.getEndorsementRemarks());
			quoteRes.setEndorsementEffdate(homeData.getEndorsementEffdate()==null?null:homeData.getEndorsementEffdate());
			quoteRes.setEndtPrevPolicyNo(homeData.getEndtPrevPolicyNo()==null?null:homeData.getEndtPrevPolicyNo());
			quoteRes.setEndtPrevQuoteNo(homeData.getEndtPrevQuoteNo()==null?null:homeData.getEndtPrevQuoteNo());
			if(homeData.getEndtPrevQuoteNo()!=null) {
				List<PaymentDetail> paymentDetails=paymentRepo.findByQuoteNo(homeData.getEndtPrevQuoteNo());
				if(paymentDetails.size()>0 && paymentDetails!=null) {
				quoteRes.setPrevPaymentType(paymentDetails.get(0).getPaymentType());
				quoteRes.setPrevPaymentTypeDesc(paymentDetails.get(0).getPaymentTypedesc());
				}
			}
			quoteRes.setEndtCount(homeData.getEndtCount()==null?0:homeData.getEndtCount().intValue());
			quoteRes.setIsChargeOrRefund(homeData.getIsChargRefund()==null?"":homeData.getIsChargRefund());
			quoteRes.setPolicyNo(homeData.getPolicyNo()==null?"":homeData.getPolicyNo());
			quoteRes.setOriginalPolicyNo(homeData.getOriginalPolicyNo()==null?"":homeData.getOriginalPolicyNo());
			quoteRes.setEndtPremium(homeData.getEndtPremium()==null?BigDecimal.ZERO:homeData.getEndtPremium());
			quoteRes.setEndtTypeId(homeData.getEndtTypeId()==null?null:homeData.getEndtTypeId());
			quoteRes.setEndtPremiumTax(homeData.getEndtPremiumTax()==null?BigDecimal.ZERO:homeData.getEndtPremiumTax());
			quoteRes.setTotalEndtPremium(quoteRes.getEndtPremium().add(quoteRes.getEndtPremiumTax()));
			// Emi Details 
			
			quoteRes.setFinalizeYn(homeData.getFinalizeYn());
			quoteRes.setCertificateNumber(homeData.getActualCNo() != null ? homeData.getActualCNo(): null );
			//PaymentDetails
			if(StringUtils.isNotBlank(homeData.getPolicyNo())){
				quoteRes.setCreditNo(homeData.getCreditNo()==null?"":homeData.getCreditNo()	);		
				quoteRes.setDebitNoteNo(homeData.getDebitNoteNo()==null?"":homeData.getDebitNoteNo());
				quoteRes.setStickerNumber(homeData.getStickerNumber()==null?"":homeData.getStickerNumber());
				List<PaymentDetail> payment=paymentRepo.findByQuoteNoAndPaymentStatusOrderByEntryDateDesc(homeData.getQuoteNo(),"ACCEPTED");
				if(payment.size()>0 && payment!=null) {
					quoteRes.setMerchantReference(payment.get(0).getMerchantReference()==null?"":payment.get(0).getMerchantReference());
				}
				
			}
//			quoteRes.setEmiYn(homeData.getEmiYn()==null?"N":homeData.getEmiYn());
//			quoteRes.setInstallmentPeriod(homeData.getInstallmentPeriod()==null?"":homeData.getInstallmentPeriod());
//			quoteRes.setInstallmentMonth(homeData.getNoOfInstallment()==null?"":homeData.getNoOfInstallment());
//			quoteRes.setDueAmount(homeData.getEmiPremium()==null?null:homeData.getEmiPremium().toString());
//			List<EmiTransactionDetails> emiDetails = emiRepo.findByQuoteNoAndCompanyIdAndProductIdOrderByInstalmentAsc(homeData.getQuoteNo() ,homeData.getCompanyId() , homeData.getProductId().toString());
//			if (emiDetails.size()>0 ) {
//				List<EmiTransactionDetails> filterEmi =  emiDetails.stream().filter( o -> (!o.getPaymentStatus().equalsIgnoreCase("Paid")) &&  ( o.getInstalment().equalsIgnoreCase("0") || o.getInstalment()!=null ) ).collect(Collectors.toList());
//				if(filterEmi.size()>0   ) {
//					quoteRes.setEmiYn("Y");
//					quoteRes.setInstallmentPeriod(filterEmi.get(0).getInstallmentPeriod());
//					quoteRes.setInstallmentMonth(filterEmi.get(0).getInstalment() );
//					quoteRes.setDueAmount(filterEmi.get(0).getDueAmount()==null?"":new BigDecimal(filterEmi.get(0).getDueAmount()).toPlainString());
//				}
//			}
			
			List<EmiTransactionDetails> emiDetails = emiRepo.findTop1ByQuoteNoAndPaymentStatusOrderByDueDateAsc(homeData.getQuoteNo(), "Pending");
			if (emiDetails.size()>0 ) {
					quoteRes.setEmiYn("Y");
					quoteRes.setInstallmentPeriod(emiDetails.get(0).getInstallmentPeriod());
					quoteRes.setInstallmentMonth(emiDetails.get(0).getInstalment() );
					if(emiDetails.get(0).getDueAmount()!=null) {
						Double due = emiDetails.get(0).getDueAmount();
						quoteRes.setDueAmount(due == null ? "" : BigDecimal.valueOf(due).setScale(2, RoundingMode.HALF_UP).toPlainString()); 
					}else {
					quoteRes.setDueAmount(emiDetails.get(0).getDueAmount()==null?"":new BigDecimal(emiDetails.get(0).getDueAmount()).toPlainString());
					}
					
				}
				
			// ValuationYN 100020 kenya
			quoteRes.setValuationYN("N"); // Default
			if("100020".equalsIgnoreCase(homeData.getCompanyId())) {
				List<MotorDataDetails> motorData = motorRepo.findByQuoteNo(homeData.getQuoteNo());
				if(motorData!=null && motorData.size()>0) {
					String motor_usage = motorData.get(0).getMotorUsage()==null?"":motorData.get(0).getMotorUsage();
					if("4".equalsIgnoreCase(motor_usage)) {
						Double sum_insured = motorData.get(0).getSumInsured()==null?0.0:motorData.get(0).getSumInsured();
						if(sum_insured >= 500000) {
							quoteRes.setValuationYN("Y");
						}
					}
				}
			}
			
			// Customer Details
			PersonalInfo custData = custRepo.findByCustomerId(homeData.getCustomerId());
			CustomerDetailsRes  custRes = new CustomerDetailsRes();
			List<LoginBranchMaster> brokerBranchList= null;
			if(custData!=null) {
				custRes  = dozerMappper.map(custData, CustomerDetailsRes.class);
			
			brokerBranchList=loginBranchRepo.findByLoginIdAndBranchCodeOrderByBranchCodeAsc(custData.getCreatedBy(),custData.getBranchCode());
			}
			if( brokerBranchList!=null && brokerBranchList.size()>0) {
			custRes.setBrokerBranchCode(brokerBranchList.get(0).getBrokerBranchCode());	
			}
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , homeData.getProductId().toString());

			 if(product.getMotorYn().equalsIgnoreCase("H") &&  homeData.getProductId().equals(Integer.valueOf(travelProductId))) {
					// Travel Product Details
					viewRes =	getTravelProductDetails( req);
					//
			 } else if(product.getMotorYn().equalsIgnoreCase("M") ) {
				// Motor Product Details
				viewRes =  getMotorProductDetails( req);
				
			} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
				// Asset Product Details
				viewRes =	getBuildingProductDetails( req);
				
			} else {
				// Human Product Details
				viewRes =	getCommonProductDetails( req);
				
			}
			 
			 viewRes =	getOverallPremiumDetails(req, viewRes);
			 
			 
			 
			 // endorsement emi 
			 EndorsementEmiDetailsRes endtEmiRes = new EndorsementEmiDetailsRes();
			 if(homeData.getEndtPrevQuoteNo()!=null) {
					HomePositionMaster prevHomeData = homeRepo.findByQuoteNo(homeData.getEndtPrevQuoteNo());
					if("Y".equalsIgnoreCase(prevHomeData.getEmiYn())) {
						CommonRes data = new CommonRes();
						EndorsementEmiReq request = new EndorsementEmiReq();
						request.setCompanyId(homeData.getCompanyId());
						request.setProductId(homeData.getProductId().toString());
						request.setQuoteNo(homeData.getQuoteNo());
						data = endtEmiService.copyEmiDetails(request);
						 endtEmiRes = (EndorsementEmiDetailsRes) data.getCommonResponse();
					}
				}
			
			viewRes.setCustomerDetails(custRes);
			viewRes.setQuoteDetails(quoteRes);
			viewRes.setEndorsementEmiDetails(endtEmiRes);
			viewRes.setPreinspectionYN(homeData.getSummaryRemarks());	
			} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}


	private List<BrokerCommissionDetails> getPolicyName( String companyId, String productId, String loginId, String agencyCode, String policyType) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			
			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"),loginId);
			Predicate n6 = cb.equal(b.get("agencyCode"),agencyCode);
			
			query.where(n1,n2,n3,n4,n5,n6);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}

	public ViewQuoteRes getMotorProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			Double totalSumInsure=0.0;
			List<MotorDataDetails> motorDatas =  motorRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(req.getQuoteNo(),"D");
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(req.getQuoteNo(),"D");
			List<LocationDetailsRes>  loctionList = new ArrayList<LocationDetailsRes>();
			List<MotorDriverDetails> driverList = driverRepo.findByQuoteNo(req.getQuoteNo() );
			List<EserviceMotorDetailsRes>   motorResList = new ArrayList<EserviceMotorDetailsRes>();
			
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();			
			for (MotorDataDetails mot :  motorDatas) {
				EserviceMotorDetailsRes vehicleDetails = new  EserviceMotorDetailsRes()  ;
				
				 String premiumFc = mot.getOverallPremiumFc().toString();
				 String vatPremiumFc =	mot.getOverallPremiumFc().toString();
				 BigDecimal commission=	new BigDecimal(premiumFc)
			 				.multiply(mot.getCommissionPercentage()==null ?  new BigDecimal("0") : mot.getCommissionPercentage())
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);
	
				// Mot
				dozerMapper.map(mot, vehicleDetails);
				vehicleDetails.setOverAllPremiumFc(mot.getOverallPremiumFc()==null?0: mot.getOverallPremiumFc() );
				vehicleDetails.setOverAllPremiumLc(mot.getOverallPremiumLc()==null?0:mot.getOverallPremiumLc());
				vehicleDetails.setPremiumFc(mot.getActualPremiumFc()==null?0:mot.getActualPremiumFc() );
				vehicleDetails.setPremiumLc(mot.getActualPremiumLc()==null?0:mot.getActualPremiumLc());
				vehicleDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
				vehicleDetails.setCommissionPercentage(mot.getCommissionPercentage()==null?"" : mot.getCommissionPercentage().toPlainString());
				vehicleDetails.setVatCommission(mot.getVatCommission()==null?"" : mot.getVatCommission().toPlainString());	
				vehicleDetails.setFinalizeYn(mot.getFinalizeYn());
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<CoverRes>  coverListRes = getCoverDetails(groupByCover, covers);
				BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscount()!=null ).map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscountLC()!=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscount()!=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscountLC()!=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTax = (coverListRes.stream().filter(o -> o.getPremiumExcluedTax()!=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumExcluedTaxLC()!=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTax = (coverListRes.stream().filter(o -> o.getPremiumIncludedTax()!=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumIncludedTaxLC()!=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
				
				// Driver Details
				List<DriverDetailsRes>   driverResList = new ArrayList<DriverDetailsRes>();
				List<MotorDriverDetails> filterDriverList = driverList.stream().filter( o -> o.getRiskId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				for (MotorDriverDetails dri :  filterDriverList) {
					DriverDetailsRes driverRes  = new DriverDetailsRes();  
					dozerMapper.map(dri, driverRes);
					driverRes.setLicenseNo(dri.getIdNumber());
					driverRes.setClaimExperience(dri.getClaimExperience()==null?0:dri.getClaimExperience());				driverResList.add(driverRes);
					
				}
				vehicleDetails.setRiskId(mot.getVehicleId());
				driverResList.sort(Comparator.comparing(DriverDetailsRes :: getDriverId  ));
				vehicleDetails.setDriverDetails(driverResList);
				vehicleDetails.setDocumentsTitle(mot.getSectionName());			
				vehicleDetails.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
				
				//get Section name Local from session master 
				List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(mot.getSectionName()!=null ? mot.getSectionName().toString() : " ");
			
				// Section Details
				SectionDetails sec = new SectionDetails(); 
				sec.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
				sec.setSectionName( mot.getSectionName());
				sec.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
				sec.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
				sec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
				sec.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
				sec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
				sec.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
				sec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
				sec.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
				sec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());

				sec.setCovers(coverListRes);
				
				List<SectionDetails>  sectionList = new ArrayList<SectionDetails>();
				sectionList.add(sec);
				vehicleDetails.setSectionDetails(sectionList);
				
				
				if (null != sectionList && !sectionList.isEmpty()) {

					sectionList.stream().forEach(a -> {

						if (null != a && null != a.getSectionId() && !a.getSectionId().isEmpty()
								&& null != vehicleDetails && null != vehicleDetails.getSectionId()
								&& !vehicleDetails.getSectionId().isEmpty()
								&& a.getSectionId().equals(vehicleDetails.getSectionId())) {

							vehicleDetails.setSectionName(a.getSectionName() != null ? a.getSectionName() : "");
						}

					});
				}
				
				// Document 
				DocumentDetails  document = new DocumentDetails();
				document.setDocumentTitle(mot.getChassisNumber() + "~" + mot.getVehicleMakeDesc() + "~" + mot.getVehcileModelDesc()) ;
				document.setRiskId(mot.getVehicleId());
				document.setSectionId(mot.getSectionId().toString());
				documentDetails.add(document);
				
				
				
				vehicleDetails.setAcccessoriesSumInsured(mot.getAcccessoriesSumInsured()==null?0.0:mot.getAcccessoriesSumInsured());
				totalSumInsure = totalSumInsure + vehicleDetails.getAcccessoriesSumInsured();		
				// Response
				motorResList.add(vehicleDetails);		
			}
			
			Set<Integer> findlocationid = motorDatas.stream().map(MotorDataDetails::getLocationId).distinct()
					.collect(Collectors.toSet());
			LocationDetailsRes locRes = null;
			SectionDetailsRes secRes = null;
			for (Integer d : findlocationid) {
				List<SectionDetailsRes>  sectionList = new ArrayList<SectionDetailsRes>();
				 locRes = new LocationDetailsRes();
				List<MotorDataDetails> filter = motorDatas.stream().filter(o -> o.getLocationId().equals(d))
						.collect(Collectors.toList());
				locRes.setLocationId((filter.get(0).getLocationId()==null|| StringUtils.isBlank(filter.get(0).getLocationId().toString()))?"1":filter.get(0).getLocationId().toString());
				locRes.setLocationName("");

				for (MotorDataDetails mot : filter) {
					secRes = new SectionDetailsRes();
					System.out.println("Section :"+mot+"\nSection Id :"+mot.getSectionId()+"\n Location :"+d);
					
					secRes.setRiskId(mot.getVehicleId().toString());
					secRes.setSectionId(mot.getSectionId().toString());
					secRes.setSectionName(mot.getSectionName());

					 String premiumFc = mot.getOverallPremiumFc().toString();
					 String vatPremiumFc =	mot.getOverallPremiumFc().toString();
					 BigDecimal commission=	new BigDecimal(premiumFc)
				 				.multiply(mot.getCommissionPercentage()==null ?  new BigDecimal("0") : mot.getCommissionPercentage())
		 						.divide(BigDecimal.valueOf(100D))
		 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
		 						.getPrecision(),RoundingMode.HALF_UP);
		
					// Mots
					dozerMapper.map(mot, secRes);
					secRes.setOverAllPremiumFc(mot.getOverallPremiumFc()==null?0: mot.getOverallPremiumFc() );
					secRes.setOverAllPremiumLc(mot.getOverallPremiumLc()==null?0:mot.getOverallPremiumLc());
					secRes.setPremiumFc(mot.getActualPremiumFc()==null?0:mot.getActualPremiumFc() );
					secRes.setPremiumLc(mot.getActualPremiumLc()==null?0:mot.getActualPremiumLc());
					secRes.setCommissionAmount(commission.toString()==null?"":commission.toString());
					secRes.setCommissionPercentage(mot.getCommissionPercentage()==null?"" : mot.getCommissionPercentage().toPlainString());
					secRes.setVatCommission(mot.getVatCommission()==null?"" : mot.getVatCommission().toPlainString());	
					secRes.setFinalizeYn(mot.getFinalizeYn());
					// Cover Details
					List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
					
					Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
					
					List<CoverRes>  coverListRes = getCoverDetails(groupByCover, covers);
					BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscount()!=null ).map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscountLC()!=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscount()!=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscountLC()!=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumExcluedTax = (coverListRes.stream().filter(o -> o.getPremiumExcluedTax()!=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumExcluedTaxLC()!=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumIncludedTax = (coverListRes.stream().filter(o -> o.getPremiumIncludedTax()!=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumIncludedTaxLC()!=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
					
					// Driver Details
					List<DriverDetailsRes>   driverResList = new ArrayList<DriverDetailsRes>();
					List<MotorDriverDetails> filterDriverList = driverList.stream().filter( o -> o.getRiskId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
					for (MotorDriverDetails dri :  filterDriverList) {
						DriverDetailsRes driverRes  = new DriverDetailsRes();  
						dozerMapper.map(dri, driverRes);
						driverRes.setLicenseNo(dri.getIdNumber());
						
						driverResList.add(driverRes);
						
					}
					secRes.setRiskId(mot.getVehicleId());
					driverResList.sort(Comparator.comparing(DriverDetailsRes :: getDriverId  ));
					secRes.setDriverDetails(driverResList);
					secRes.setDocumentsTitle(mot.getSectionName());			
					secRes.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
					
					//get Section name Local from session master 
					List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(mot.getSectionName()!=null ? mot.getSectionName().toString() : " ");
				
					// Section Details
//					SectionDetails sec = new SectionDetails(); 
					secRes.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
					secRes.setSectionName( mot.getSectionName());
					secRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
					secRes.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
					secRes.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
					secRes.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
					secRes.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
					secRes.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
					secRes.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
					secRes.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
					secRes.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());

					secRes.setCovers(coverListRes);
					
//					if (null != sectionList && !sectionList.isEmpty()) {
//
//						sectionList.stream().forEach(a -> {
//
//							if (null != a && null != a.getSectionId() && !a.getSectionId().isEmpty()
//									&& null != secRes && null != secRes.getSectionId()
//									&& !secRes.getSectionId().isEmpty()
//									&& a.getSectionId().equals(secRes.getSectionId())) {
//
//								secRes.setSectionName(a.getSectionName() != null ? a.getSectionName() : "");
//							}
//
//						});
//					}
					secRes.setAcccessoriesSumInsured(mot.getAcccessoriesSumInsured()==null?0.0:mot.getAcccessoriesSumInsured());
					totalSumInsure = totalSumInsure + secRes.getAcccessoriesSumInsured();		
					// Response
					sectionList.add(secRes);	
				
				}
				locRes.setSectionDetails(sectionList);
				loctionList.add(locRes);
			}
			
	
			
			viewRes.setRiskDetails(motorResList);
			viewRes.setDocumentDetails(documentDetails);
			viewRes.setLocationDetails(loctionList);
			List<PolicyCoverData>  accCovers = covers.stream().filter( o -> o.getCoverId().equals(55)  ).collect(Collectors.toList());
			if(accCovers.size()> 0 )  {
				viewRes.setTotalAccessoriesSumInsured(totalSumInsure);	
			} else {
				viewRes.setTotalAccessoriesSumInsured(0D);
			}
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	public ViewQuoteRes getOverallPremiumDetails(ViewQuoteReq req, ViewQuoteRes viewRes) {

	   // ViewQuoteRes viewRes = new ViewQuoteRes();
	    OverallPremiumRes overall = new OverallPremiumRes();

	    final Integer OVERALL_VEHICLE_ID = 99999;

	    // Fetch all rows for the overall record
	    List<PolicyCoverData> allRows =
	            coverRepo.findByQuoteNoAndVehicleIdAndStatusNot(
	                    req.getQuoteNo(),
	                    OVERALL_VEHICLE_ID,
	                    "D"
	            );

	    if (allRows == null || allRows.isEmpty()) {
	        viewRes.setOverallPremiumDetails(overall);
	        return viewRes;
	    }

	    // --- Split by coverage type ---
	    List<PolicyCoverData> baseRows = allRows.stream()
	            .filter(r -> "B".equalsIgnoreCase(r.getCoverageType()))
	            .collect(Collectors.toList());

	    List<PolicyCoverData> taxRows = allRows.stream()
	            .filter(r -> "T".equalsIgnoreCase(r.getCoverageType()))
	            .collect(Collectors.toList());

	    List<PolicyCoverData> discountRows = allRows.stream()
	            .filter(r -> "D".equalsIgnoreCase(r.getCoverageType()))
	            .collect(Collectors.toList());

	    List<PolicyCoverData> loadingRows = allRows.stream()
	            .filter(r -> "L".equalsIgnoreCase(r.getCoverageType()))
	            .collect(Collectors.toList());

	    // Fallback: if no 'B' row, use all non-tax rows as base
	    if (baseRows.isEmpty()) {
	        baseRows = allRows.stream()
	                .filter(r -> !"T".equalsIgnoreCase(r.getCoverageType()))
	                .collect(Collectors.toList());
	    }

	    BiFunction<List<PolicyCoverData>, Function<PolicyCoverData, BigDecimal>,
	            BigDecimal> sum = (list, getter) ->
	            list.stream()
	                .map(getter)
	                .filter(Objects::nonNull)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);


	    // B row already has correct stage values
	    BigDecimal premBeforeDiscFc = sum.apply(baseRows, PolicyCoverData::getPremiumBeforeDiscountFc);
	    BigDecimal premBeforeDiscLc = sum.apply(baseRows, PolicyCoverData::getPremiumBeforeDiscountLc);

	    BigDecimal premAfterDiscFc  = sum.apply(baseRows, PolicyCoverData::getPremiumAfterDiscountFc);
	    BigDecimal premAfterDiscLc  = sum.apply(baseRows, PolicyCoverData::getPremiumAfterDiscountLc);

	    BigDecimal premExclTaxFc    = sum.apply(baseRows, PolicyCoverData::getPremiumExcludedTaxFc);
	    BigDecimal premExclTaxLc    = sum.apply(baseRows, PolicyCoverData::getPremiumExcludedTaxLc);

	    BigDecimal premInclTaxFc    = sum.apply(baseRows, PolicyCoverData::getPremiumIncludedTaxFc);
	    BigDecimal premInclTaxLc    = sum.apply(baseRows, PolicyCoverData::getPremiumIncludedTaxLc);

	    // Overall premium = included tax totals
	    BigDecimal overallPremFc = premInclTaxFc;
	    BigDecimal overallPremLc = premInclTaxLc;


	    BigDecimal loadingRateAbs = sum.apply(loadingRows, PolicyCoverData::getRate);
	    BigDecimal loadingAmtFcAbs = sum.apply(loadingRows, PolicyCoverData::getPremiumAfterDiscountFc);
	    BigDecimal loadingAmtLcAbs = sum.apply(loadingRows, PolicyCoverData::getPremiumAfterDiscountLc);

	    // Discount: from 'D' rows – rate & amount from row, then NEGATIVE in response
	    BigDecimal discountRateAbs = sum.apply(discountRows, PolicyCoverData::getRate);
	    BigDecimal discountAmtFcAbs = sum.apply(discountRows, PolicyCoverData::getPremiumAfterDiscountFc);
	    BigDecimal discountAmtLcAbs = sum.apply(discountRows, PolicyCoverData::getPremiumAfterDiscountLc);

	    BigDecimal loadingRate   = loadingRateAbs;      
	    BigDecimal loadingAmtFc  = loadingAmtFcAbs;     
	    BigDecimal loadingAmtLc  = loadingAmtLcAbs;     

	    BigDecimal discountRate  = discountRateAbs.compareTo(BigDecimal.ZERO) > 0
	            ? discountRateAbs
	            : BigDecimal.ZERO;

	    BigDecimal discountAmtFc = discountAmtFcAbs.compareTo(BigDecimal.ZERO) > 0
	            ? discountAmtFcAbs
	            : BigDecimal.ZERO;

	    BigDecimal discountAmtLc = discountAmtLcAbs.compareTo(BigDecimal.ZERO) > 0
	            ? discountAmtLcAbs
	            : BigDecimal.ZERO;

	    BigDecimal premAfterLoadFc = premBeforeDiscFc;                    // after loading, before discount
	    BigDecimal premAfterLoadLc = premBeforeDiscLc;

	    BigDecimal premBeforeLoadFc = premAfterLoadFc.subtract(loadingAmtFcAbs != null ? loadingAmtFcAbs : BigDecimal.ZERO);
	    BigDecimal premBeforeLoadLc = premAfterLoadLc.subtract(loadingAmtLcAbs != null ? loadingAmtLcAbs : BigDecimal.ZERO);


	    Map<Integer, List<PolicyCoverData>> taxGroups = taxRows.stream()
	            .collect(Collectors.groupingBy(PolicyCoverData::getTaxId));

	    List<TaxDetailsRes> taxList = new ArrayList<>();

	    for (Map.Entry<Integer, List<PolicyCoverData>> e : taxGroups.entrySet()) {

	        Integer taxId = e.getKey();
	        List<PolicyCoverData> rows = e.getValue();
	        if (rows == null || rows.isEmpty()) continue;

	        PolicyCoverData first = rows.get(0);

	        BigDecimal totalTaxFc = sum.apply(rows, PolicyCoverData::getTaxAmount);
	        BigDecimal totalTaxLc = sum.apply(rows, PolicyCoverData::getTaxAmountLc);

	        TaxDetailsRes tx = new TaxDetailsRes();
	        tx.setTaxId(taxId);
	        tx.setTaxDesc(first.getTaxDesc());
	        tx.setTaxDescLocal(first.getTaxDescLocal());
	        tx.setTaxRate(first.getTaxRate());
	        tx.setTaxAmountFc(totalTaxFc);
	        tx.setTaxAmountLc(totalTaxLc);

	        taxList.add(tx);
	    }

	    // ========================
	    // 5) SET INTO OverallPremiumRes
	    // ========================

	    overall.setPremiumBeforeDiscountFc(premBeforeDiscFc);
	    overall.setPremiumBeforeDiscountLc(premBeforeDiscLc);

	    overall.setPremiumAfterDiscountFc(premAfterDiscFc);
	    overall.setPremiumAfterDiscountLc(premAfterDiscLc);

//	    overall.setPremiumBeforeLoadingFc(premBeforeLoadFc);
//	    overall.setPremiumBeforeLoadingLc(premBeforeLoadLc);
//
//	    overall.setPremiumAfterLoadingFc(premAfterLoadFc);
//	    overall.setPremiumAfterLoadingLc(premAfterLoadLc);

	    overall.setPremiumExcludedTaxFc(premExclTaxFc);
	    overall.setPremiumExcludedTaxLc(premExclTaxLc);

	    overall.setPremiumIncludedTaxFc(premInclTaxFc);
	    overall.setPremiumIncludedTaxLc(premInclTaxLc);

	    overall.setOverallPremiumFc(overallPremFc);
	    overall.setOverallPremiumLc(overallPremLc);

	    overall.setTaxList(taxList);

	   
	    overall.setLoadingRate(loadingRate);
	    overall.setLoadingAmountFc(loadingAmtFc);
	    overall.setLoadingAmountLc(loadingAmtLc);

	    overall.setDiscountRate(discountRate);
	    overall.setDiscountAmountFc(discountAmtFc);
	    overall.setDiscountAmountLc(discountAmtLc);
	    

	    viewRes.setOverallPremiumDetails(overall);
	    return viewRes;
	}






	public ViewQuoteRes getBuildingProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			
		//	List<BuildingRiskDetails> buildings = buildRiskRepo.findByQuoteNo(req.getQuoteNo());
			List<SectionDataDetails> secDatas =  secDataRepo.findByQuoteNo(req.getQuoteNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			Integer endorsementType = secDatas.get(0).getEndorsementType();
			String endt = endorsementType != null ? endorsementType.toString() : null;
			if(endt != null  && "852".equalsIgnoreCase(secDatas.get(0).getEndorsementType().toString()))
					{
				  viewRes = setRemovalcoverEndt(req,secDatas.get(0).getRequestReferenceNo(),secDatas,covers);	
				  return viewRes;
						}
			List<LocationDetailsRes>  loctionList = new ArrayList<LocationDetailsRes>();
			List<SectionDetailsRes>  sectionList = new ArrayList<SectionDetailsRes>();
//			String productId=buildings.get(0).getProductId();
			String locationId="";
			String locationName="";
			String sumInsured="0.0";
			String contentType="";
			String contentDesc="";
			String sectionId="";
			// Building Details 
			// Build
			// Section Details
			// Document
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();			
			List<Object> totalList =new ArrayList<Object>(); 
			List<PaccGetRes> paccGetResList = new ArrayList<PaccGetRes>(); 
			List<EserviceBuildingsDetailsRes>   buildList = new ArrayList<EserviceBuildingsDetailsRes>();
			
			
		
			Map<Integer, List<SectionDataDetails>> riskGroup =null;
			Map<Integer, List<SectionDataDetails>> locationGroup =null;
			locationGroup = secDatas.stream().filter( o -> o.getLocationId()!=null  ).collect( Collectors.groupingBy(SectionDataDetails :: getLocationId )) ;
		/*	for (Integer locId :  locationGroup.keySet()) {
			riskGroup = secDatas.stream().filter( o -> o.getRiskId()!=null && locId.equals(o.getLocationId())  ).collect( Collectors.groupingBy(SectionDataDetails :: getRiskId )) ;
			for (Integer risk :  riskGroup.keySet()) {
			List<SectionDataDetails> filterData = secDatas.stream().filter( o -> o.getRiskId().equals(risk) && o.getLocationId().equals(locId)).collect(Collectors.toList());
			List<SectionDetails>  buildingSectionList = new ArrayList<SectionDetails>();
			EserviceBuildingsDetailsRes buildingRes = new  EserviceBuildingsDetailsRes()  ;
			for (SectionDataDetails sec :  filterData) {
				sectionId=sec.getSectionId()==null?"":sec.getSectionId().toString();
				if( sec.getProductType().equalsIgnoreCase("H") ) {
					List<SectionDetails>  pacSectionList = new ArrayList<SectionDetails>();
					List<CommonDataDetails> accData =  	commonDataRepo.findByQuoteNoAndSectionIdOrderByLocationIdAsc(req.getQuoteNo() , sec.getSectionId());
					for (CommonDataDetails	 acc : accData ) {
						
						List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(acc.getRiskId()) &&
								 o.getSectionId().toString().equals(acc.getSectionId()) &&  o.getLocationId().equals(acc.getLocationId()) ).collect(Collectors.toList());
						SectionDetails buildSec = new SectionDetails(); 
						buildSec.setSectionId(acc.getSectionId()==null?"":acc.getSectionId().toString());
						buildSec.setSectionName( acc.getSectionDesc());
					if( filterCovers.size() > 0 ) {
						Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
						
							List<CoverRes>  coverListRes = getCoverDetails(groupByCover);

							BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscount()!=null ) .map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
							BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscountLC()!=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
							BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscount()!=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
							BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscountLC()!=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
							BigDecimal PremiumExcluedTax = (coverListRes.stream().filter(o -> o.getPremiumExcluedTax()!=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
							BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumExcluedTaxLC()!=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
							BigDecimal PremiumIncludedTax = (coverListRes.stream().filter(o -> o.getPremiumIncludedTax()!=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
							BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumIncludedTaxLC()!=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
							buildSec.setPremiumAfterDiscount(PremiumAfterDiscount==null?"":PremiumAfterDiscount.toString());
							buildSec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc==null?"":PremiumAfterDiscountLc.toString());
							buildSec.setPremiumBeforeDiscount(PremiumBeforeDiscount==null?"":PremiumBeforeDiscount.toString());
							buildSec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc==null?"":PremiumBeforeDiscountLc.toString());
							buildSec.setPremiumExcluedTax(PremiumExcluedTax==null?"":PremiumExcluedTax.toString());
							buildSec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc==null?"":PremiumExcluedTaxLc.toString());
							buildSec.setPremiumIncludedTax(PremiumIncludedTax==null?"":PremiumIncludedTax.toString());
							buildSec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc==null?"":PremiumIncludedTaxLc.toString());

							buildSec.setCovers(coverListRes);
						}
						
						// Accident
						PaccGetRes pacRes = new  PaccGetRes()  ;
						dozerMapper.map(acc, pacRes);		
						pacSectionList.add(buildSec);
						pacRes.setSectionDetails(pacSectionList);	
						buildingSectionList.add(buildSec);
						pacRes.setDocumentsTitle(StringUtils.isNotBlank(sec.getSectionDesc() ) ? sec.getSectionDesc() :   sec.getProductDesc());
						
						BuildingDetails buildingList=BuildingRepo.findByRequestReferenceNoAndRiskIdAndSectionId(acc.getRequestReferenceNo(),risk,"1");
						String LocationName="";
						if(buildingList!=null) {
							LocationName=buildingList.getLocationName();
						}
						pacRes.setLocationName(LocationName);
						pacRes.setRiskId(acc.getRiskId().toString());
						pacRes.setLocationId(acc.getLocationId().toString());
						pacRes.setLocationName(acc.getLocationName());
						pacRes.setSuminsured(acc.getSumInsured()==null?"" : acc.getSumInsured().toPlainString());
						pacRes.setSectionId(StringUtils.isNotBlank(acc.getSectionId() ) ?  acc.getSectionId() :  "99999"  );
						paccGetResList.add(pacRes);
						
						
					}
					
				} else {
					List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(sec.getRiskId())) &&
							o.getCompanyId().equals(sec.getCompanyId()) && o.getProductId().toString().equals(sec.getProductId()) && o.getSectionId().toString().equals(sec.getSectionId()) &&  o.getLocationId().equals(sec.getLocationId())).collect(Collectors.toList());
				
					Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
					
					List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
					// Build
					SectionDetails buildSec = new SectionDetails(); 
					BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumExcluedTax = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumIncludedTax = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
					
					//get Section name Local from session master 
					List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(sec.getSectionDesc()!=null ? sec.getSectionDesc().toString() : " ");
					
					buildSec.setSectionId(sec.getSectionId()==null?"":sec.getSectionId().toString());
					buildingRes.setSectionId(StringUtils.isBlank(buildingRes.getSectionId() ) ? sec.getSectionId()==null?"":sec.getSectionId().toString() :buildingRes.getSectionId()  );
					buildSec.setSectionName( sec.getSectionDesc());
					buildSec.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
					buildSec.setCovers(coverListRes);
					buildSec.setPremiumAfterDiscount(PremiumAfterDiscount==null?"":PremiumAfterDiscount.toString());
					buildSec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc==null?"":PremiumAfterDiscountLc.toString());
					buildSec.setPremiumBeforeDiscount(PremiumBeforeDiscount==null?"":PremiumBeforeDiscount.toString());
					buildSec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc==null?"":PremiumBeforeDiscountLc.toString());
					buildSec.setPremiumExcluedTax(PremiumExcluedTax==null?"":PremiumExcluedTax.toString());
					buildSec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc==null?"":PremiumExcluedTaxLc.toString());
					buildSec.setPremiumIncludedTax(PremiumIncludedTax==null?"":PremiumIncludedTax.toString());
					buildSec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc==null?"":PremiumIncludedTaxLc.toString());
					locationId=sec.getLocationId().toString();
					locationName=sec.getLocationName();
					buildSec.setLocationId(StringUtils.isBlank(locationId)?"":locationId);
					buildSec.setLocationName(StringUtils.isBlank(locationName)?"":locationName);
				
					buildingSectionList.add(buildSec);
				
			} 
				
			
		}
			buildingRes.setSectionDetails(buildingSectionList);
			buildingRes.setLocationId(risk.toString());
			BuildingDetails buildingList=null;
			buildingList=BuildingRepo.findByRequestReferenceNoAndRiskIdAndSectionId(buildings.get(0).getRequestReferenceNo(),risk,"1");
			String LocationName="";
			if(buildingList!=null) {
				LocationName=buildingList.getLocationName();
			}
	
			buildingRes.setLocationName(LocationName);
			buildingRes.setRiskId(risk.toString());
		// Default Entry
		List<BuildingRiskDetails> filterDefaultBuilding = buildings.stream().filter( o -> "1".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk) ).collect(Collectors.toList());
		if(filterDefaultBuilding.size() > 0 ) {
			BuildingRiskDetails buildData = filterDefaultBuilding.get(0);
			dozerMapper.map(buildData, buildingRes);
			buildingRes.setDocumentsTitle(buildData.getProductDesc());
			String premiumFc = String.valueOf(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum()) ;
			 String vatPremiumFc =	String.valueOf(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum()) ;
			 BigDecimal commission=	new BigDecimal(premiumFc)
		 				.multiply(buildData.getCommissionPercentage()==null ? new BigDecimal("0") : buildData.getCommissionPercentage())
 						.divide(BigDecimal.valueOf(100D))
 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
 						.getPrecision(),RoundingMode.HALF_UP);
			 buildingRes.setOverAllPremiumFc(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum() );
			 buildingRes.setOverAllPremiumLc(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum() );
			 buildingRes.setPremiumFc(buildings.stream().filter( o -> o.getActualPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getActualPremiumFc().toPlainString() ) ).sum() );
			 buildingRes.setPremiumLc(buildings.stream().filter( o -> o.getActualPremiumLc() != null ).mapToDouble(o -> Double.valueOf(o.getActualPremiumLc().toPlainString() ) ).sum() );
			 buildingRes.setCommissionAmount(commission==null?"":commission.toString());
			 buildingRes.setCommissionPercentage(buildData.getCommissionPercentage()==null?"0" : buildData.getCommissionPercentage().toPlainString());
			 buildingRes.setVatCommission(String.valueOf(buildings.stream().filter( o -> o.getVatCommission() != null ).mapToDouble(o -> Double.valueOf(o.getVatCommission().toPlainString() ) ).sum()) );	
			
			buildingRes.setDocumentsTitle(StringUtils.isNotBlank(buildData.getSectionDesc() ) ? buildData.getSectionDesc() :   buildData.getProductDesc());
			buildingRes.setLocationId(buildData.getRiskId().toString());
			buildingList=BuildingRepo.findByRequestReferenceNoAndRiskIdAndSectionId(buildData.getRequestReferenceNo(),risk,"1");
			LocationName="";
			if(buildingList!=null) {
				LocationName=buildingList.getLocationName();
			}
			buildingRes.setLocationName(LocationName);
			
//			buildingRes.setLocationName(StringUtils.isNotBlank(buildData.getSectionDesc() ) ? buildData.getSectionDesc() :   buildData.getProductDesc());
			buildingRes.setRiskId(buildData.getRiskId().toString());
//			buildingRes.setSuminsured(buildData.getBuildingSuminsured()==null?"" : buildData.getBuildingSuminsured().toPlainString());
			buildingRes.setSuminsured(buildData.getSumInsured()==null?"" : buildData.getSumInsured().toPlainString());
			buildingRes.setSectionId(StringUtils.isNotBlank(buildData.getSectionId() ) ?  buildData.getSectionId() :  "99999"  );
		
			List<EserviceSectionDetails>   buildSections = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(buildData.getRequestReferenceNo());
			List<String> sectionIds = buildSections.stream().filter( o -> o.getRiskId().equals(1)).map(EserviceSectionDetails :: getSectionId ).collect(Collectors.toList());
			buildingRes.setRiskId(buildData.getRiskId().toString());
			locationId=buildData.getLocationId().toString();
			locationName=buildData.getLocationName();
		c
			
			// (i) Asset Related Sections
			
			// Building
			List<BuildingRiskDetails> filterBuilding = buildings.stream().filter( o -> "1".equalsIgnoreCase(o.getSectionId())).collect(Collectors.toList());
			if(filterBuilding.size() > 0 ) {
				BuildingRiskDetails build = filterBuilding.get(0);
//				buildingRes.setBuildingSuminsured(build.getBuildingSuminsured() == null?"0" :build.getBuildingSuminsured().toPlainString());
				buildingRes.setBuildingSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
				buildingRes.setBuidingAreaSqm(build.getBuildingAreaSqm() == null ? "0" :build.getBuildingAreaSqm().toPlainString());
				buildingRes.setBuildingOwnerYn(StringUtils.isBlank(build.getBuildingOwnerYn())  ? "N":build.getBuildingOwnerYn());
				buildingRes.setBuildingBuildYear(build.getBuildingBuildYear()==null ?"": build.getBuildingBuildYear().toString());
				buildingRes.setWallType(build.getWallType());
				buildingRes.setRoofType(build.getRoofType());
				buildingRes.setInternalWallType(build.getInternalWallType()==null ?"": build.getInternalWallType().toString());
				buildingRes.setBuildingUsageYn(build.getBuildingUsageYn());
				buildingRes.setBuildingUsageId(build.getBuildingUsageId());
				buildingRes.setBuildingUsageDesc(build.getBuildingUsageDesc());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
				sumInsured=build.getSumInsured().toString();
				} 
			
			// Content
			List<BuildingRiskDetails> filterContent = buildings.stream().filter( o -> "47".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk) ).collect(Collectors.toList());
			if(filterContent.size() > 0 ) {
				BuildingRiskDetails build = filterContent.get(0);
//				buildingRes.setContentSuminsured(build.getContentSuminsured() == null?"0" :build.getContentSuminsured().toPlainString());
				buildingRes.setSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
				sumInsured=build.getSumInsured().toString();
				contentType=StringUtil.isBlank(build.getContentId())?"":build.getContentId();
				contentDesc=StringUtil.isBlank(build.getContentDesc())?"":build.getContentDesc();
				
				
			} 
			
			
			// All Risk , Plant All Risk , Business All Risk
			List<BuildingRiskDetails> filterAllRisk = buildings.stream().filter( o -> "3".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk) ).collect(Collectors.toList());
			if(filterAllRisk.size() > 0 ) {
				BuildingRiskDetails build = filterAllRisk.get(0);
				buildingRes.setAllriskSuminsured(build.getAllriskSuminsured() == null?"0" :build.getAllriskSuminsured().toPlainString());
				buildingRes.setMiningPlantSi(build.getMiningPlantSi()== null?"0" :build.getMiningPlantSi().toPlainString());
				buildingRes.setNonminingPlantSi(build.getNonminingPlantSi() == null?"0" :build.getNonminingPlantSi().toPlainString());
				buildingRes.setGensetsSi(build.getGensetsSi() == null?"0" :build.getGensetsSi().toPlainString());
				buildingRes.setEquipmentSi(build.getEquipmentSi() == null?BigDecimal.ZERO :build.getEquipmentSi());
			//	Double MiningPlantSi = build.getMiningPlantSi() == null?0D :Double.valueOf(build.getMiningPlantSi().toPlainString()) ;
			//	Double NonminingPlantSi = build.getNonminingPlantSi() == null?0D :Double.valueOf(build.getNonminingPlantSi().toPlainString()) ;
			//	Double GensetsSi = build.getGensetsSi() == null?0D :Double.valueOf(build.getGensetsSi().toPlainString()) ;
			//	Double plantAllRiskSi = MiningPlantSi +NonminingPlantSi  + GensetsSi ;
				//res.setPlantAllriskSi( plantAllRiskSi==null ? "" :plantAllRiskSi.toString());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
				if(build.getAllriskSuminsured()!=null && Double.valueOf(build.getAllriskSuminsured().toString())>0.0) {
					sumInsured=build.getAllriskSuminsured()==null?null:build.getAllriskSuminsured().toString();
				}else if(build.getMiningPlantSi()!=null && Double.valueOf(build.getMiningPlantSi().toString())>0.0) {
					sumInsured=build.getMiningPlantSi()==null?null:build.getMiningPlantSi().toString();	
				}
				else if(build.getNonminingPlantSi()!=null && Double.valueOf(build.getNonminingPlantSi().toString())>0.0) {
					sumInsured=build.getNonminingPlantSi()==null?null:build.getNonminingPlantSi().toString();	
				}else if(build.getGensetsSi()!=null && Double.valueOf(build.getGensetsSi().toString())>0.0) {
					sumInsured=build.getGensetsSi()==null?null:build.getGensetsSi().toString();	
				}else if(build.getEquipmentSi()!=null && Double.valueOf(build.getEquipmentSi().toString())>0.0) {
					sumInsured=build.getEquipmentSi()==null?null:build.getEquipmentSi().toString();	
				}
				sumInsured=build.getSumInsured()==null?"" : build.getSumInsured().toPlainString();
			} 
			
			// Accidental Damage
			List<BuildingRiskDetails> filterAccidental = buildings.stream().filter( o -> "56".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk) ).collect(Collectors.toList());
			if(filterAccidental.size() > 0 ) {
				BuildingRiskDetails build = filterAccidental.get(0);
				//res.setContentSuminsured(build.getContentSuminsured() == null?"0" :build.getContentSuminsured().toPlainString());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
			}
			
			// Burgalry
			List<BuildingRiskDetails> filterBurglary = buildings.stream().filter( o -> "52".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk) ).collect(Collectors.toList());
			if(filterBurglary.size() > 0 ) {
				BuildingRiskDetails build = filterBurglary.get(0);
				buildingRes.setStockInTradeSi(build.getStockInTradeSi()== null?"0" :build.getStockInTradeSi().toPlainString());
				buildingRes.setGoodsSi(build.getGoodsSi() == null?"0" :build.getGoodsSi().toPlainString());	
				buildingRes.setFurnitureSi(build.getFurnitureSi() == null?"0" :build.getFurnitureSi().toPlainString());
				buildingRes.setCashValueablesSi(build.getCashValueablesSi() == null?"0" :build.getCashValueablesSi().toPlainString());
				buildingRes.setApplianceSi(build.getApplianceSi() == null?"0" :build.getApplianceSi().toPlainString());
			//	res.setGoodsSinglecarrySuminsured(build.getGoodsSinglecarrySuminsured() == null?"0" :build.getGoodsSinglecarrySuminsured().toPlainString());
			//	res.setGoodsTurnoverSuminsured(build.getGoodsTurnoverSuminsured() == null?"0" :build.getGoodsTurnoverSuminsured().toPlainString());
				 buildingRes.setInsuranceForId(build.getInsuranceForId()!=null ? Arrays.asList(build.getInsuranceForId().split(",")) : null )  ;
				 buildingRes.setStockLossPercent(build.getStockLossPercent()==null ? null : build.getStockLossPercent().toString());
				 buildingRes.setGoodsLossPercent(build.getGoodsLossPercent()==null ? null : build.getGoodsLossPercent().toString());
				 buildingRes.setFurnitureLossPercent(build.getFurnitureLossPercent()==null ? null : build.getFurnitureLossPercent().toString());
				 buildingRes.setApplianceLossPercent(build.getApplianceLossPercent()==null ? null : build.getApplianceLossPercent().toString());
				 buildingRes.setCashValueablesLossPercent(build.getCashValueablesLossPercent()==null ? null : build.getCashValueablesLossPercent().toString());
				 buildingRes.setStockInTradeSi(build.getStockInTradeSi()==null?"" : build.getStockInTradeSi().toPlainString());
				 buildingRes.setGoodsSi(build.getGoodsSi()==null?"" : build.getGoodsSi().toPlainString());
				 buildingRes.setFurnitureSi(build.getFurnitureSi()==null?"" : build.getFurnitureSi().toPlainString());
				 buildingRes.setApplianceSi(build.getApplianceSi()==null?"" : build.getApplianceSi().toPlainString());
				 buildingRes.setCashValueablesSi(build.getCashValueablesSi()==null?"" : build.getCashValueablesSi().toPlainString());
				 buildingRes.setBuildingOwnerYn(build.getBuildingOwnerYn()==null?"" : build.getBuildingOwnerYn());
				 buildingRes.setAccessibleWindows(build.getAccessibleWindows()==null ? "" : build.getAccessibleWindows().toString());	
				 buildingRes.setAddress(build.getAddress()==null ? "" : (build.getAddress()));
				 buildingRes.setBackDoors(build.getBackDoors()==null ? "" : build.getBackDoors().toString());
				 buildingRes.setBuildingOccupied(build.getBuildingOccupied()==null ? "": build.getBuildingOccupied().toString());
				 buildingRes.setCeilingType(build.getCeilingType()==null ? "": build.getCeilingType().toString());
				 buildingRes.setDistrictCode(build.getDistrictCode()==null ? "": build.getDistrictCode().toString());
				 buildingRes.setDoorsMaterialId(build.getDoorsMaterialId()==null? "": build.getDoorsMaterialId().toString());
				 buildingRes.setFrontDoors(build.getFrontDoors()==null ? "" :build.getFrontDoors().toString());
				 buildingRes.setInsuranceForId(build.getInsuranceForId()!= null ? Arrays.asList(build.getInsuranceForId().split(",")) : null )  ;
				 buildingRes.setNatureOfTradeId(build.getNatureOfTradeId()==null ? "" : build.getNatureOfTradeId().toString());				
				 buildingRes.setInternalWallType(build.getInternalWallType()==null? "" : build.getInternalWallType().toString());
				 buildingRes.setNightLeftDoor(build.getNightLeftDoor()==null? "" : build.getNightLeftDoor().toString());
				 buildingRes.setOccupiedYear(build.getOccupiedYear()==null ? "" : build.getOccupiedYear().toString());
				 buildingRes.setShowWindow(build.getShowWindow()==null ? "" : build.getShowWindow().toString());
				 buildingRes.setTrapDoors(build.getTrapDoors()==null  ? "" : build.getTrapDoors().toString());
				 buildingRes.setWatchmanGuardHours(build.getWatchmanGuardHours()==null ? "" :build.getWatchmanGuardHours().toString());
				 buildingRes.setWindowsMaterialId(build.getWindowsMaterialId()==null  ? "" :build.getWindowsMaterialId().toString());
				 buildingRes.setBuildingBuildYear(build.getBuildingBuildYear()==null  ? "" :build.getBuildingBuildYear().toString());
				 buildingRes.setRoofType(build.getRoofType()==null  ? "" :build.getRoofType().toString());
				 buildingRes.setWallType(build.getWallType()==null  ? "" :build.getWallType().toString());				
				 buildingRes.setRegionCode(build.getRegionCode()==null  ? "" :build.getRegionCode().toString());
				 buildingRes.setFinalizeYn(build.getFinalizeYn());
				 locationId=build.getLocationId().toString();
					locationName=build.getLocationName();
			}
			
			// Fire And Material Damage
			List<BuildingRiskDetails> filterFire = buildings.stream().filter( o -> "40".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk)).collect(Collectors.toList());
			if(filterFire.size() > 0 ) {
				BuildingRiskDetails build = filterFire.get(0);
				buildingRes.setStockInTradeSi(build.getStockInTradeSi()== null?"0" :build.getStockInTradeSi().toPlainString());
				buildingRes.setBuildingSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());	
				buildingRes.setFireEquipSi(build.getEquipmentSi() == null?"0" :build.getEquipmentSi().toPlainString());
				buildingRes.setFirePlantSi(build.getFirePlantSi() == null?"0" :build.getFirePlantSi().toPlainString());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
			}
			
			// Electronic Equipment
			List<BuildingRiskDetails> filterElecEquip = buildings.stream().filter( o -> "39".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk)  ).collect(Collectors.toList());
			if(filterElecEquip.size() > 0 ) {
				BuildingRiskDetails build = filterElecEquip.get(0);
				buildingRes.setElecEquipSuminsured(build.getElecEquipSuminsured() == null?BigDecimal.ZERO :build.getElecEquipSuminsured());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
				sumInsured=build.getElecEquipSuminsured()==null?null:build.getElecEquipSuminsured().toString();
				contentType=StringUtil.isBlank(build.getContentId())?"":build.getContentId();
				contentDesc=StringUtil.isBlank(build.getContentDesc())?"":build.getContentDesc();
						
			}
			//or Electronic Equiment in domestic
			List<BuildingRiskDetails> filterElecEquip2 = buildings.stream().filter( o -> "76".equalsIgnoreCase(o.getSectionId()) && o.getRiskId().equals(risk)  ).collect(Collectors.toList());
			if (filterElecEquip2.size() > 0) {
				BuildingRiskDetails build = filterElecEquip2.get(0);
				contentType = StringUtil.isBlank(build.getContentId()) ? "" : build.getContentId();
				contentDesc = StringUtil.isBlank(build.getContentDesc()) ? "" : build.getContentDesc();

			}

			// Bond
			if(StringUtils.isNotBlank(sectionId) && "61".equals(productId) ) {
				String sec=sectionId;
				List<BuildingRiskDetails> filterBond = buildings.stream().filter( o -> sec.equalsIgnoreCase(o.getSectionId())
//					|| "118".equalsIgnoreCase(o.getSectionId()) 
//					|| "119".equalsIgnoreCase(o.getSectionId()) 
//					|| "120".equalsIgnoreCase(o.getSectionId()))
				&& o.getRiskId().equals(risk)  ).collect(Collectors.toList());
			if(filterBond.size() > 0 ) {
				BuildingRiskDetails build = filterBond.get(0);
				buildingRes.setBondSuminsured(build.getBondSuminsured() == null?BigDecimal.ZERO :build.getBondSuminsured());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
				sumInsured=build.getBondSuminsured()==null?null:build.getBondSuminsured().toString();
						
			}
			}
			// Money
			List<BuildingRiskDetails> filterMoney = buildings.stream().filter( o -> "42".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
			if(filterMoney.size() > 0 ) {
				BuildingRiskDetails build = filterMoney.get(0);
				buildingRes.setMoneyAnnualEstimate(build.getMoneyAnnualEstimate()== null?"0" : build.getMoneyAnnualEstimate().toPlainString());
				buildingRes.setMoneyCollector(build.getMoneyCollector()== null?"0" : build.getMoneyCollector().toPlainString() );
				buildingRes.setMoneyDirectorResidence(build.getMoneyDirectorResidence()== null?"0" : build.getMoneyDirectorResidence().toPlainString() );
				buildingRes.setMoneyOutofSafe(build.getMoneyOutofSafe()== null?"0" : build.getMoneyOutofSafe().toPlainString() );
				buildingRes.setMoneySafeLimit(build.getMoneySafeLimit()== null?"0" : build.getMoneySafeLimit().toPlainString() );
				buildingRes.setMoneyMajorLoss(build.getMoneyMajorLoss() == null?"0" : build.getMoneyMajorLoss().toPlainString() );
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
			}
			
			// Machinery
			List<BuildingRiskDetails> filterMachienry = buildings.stream().filter( o -> "41".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
			if(filterMachienry.size() > 0 ) {
				BuildingRiskDetails build = filterMachienry.get(0);
				Double ElecMachinesSi = build.getElecMachinesSi() == null?0D :Double.valueOf(build.getElecMachinesSi().toPlainString());
				Double BoilerPlantsSi = build.getBoilerPlantsSi() == null?0D :Double.valueOf(build.getBoilerPlantsSi().toPlainString()) ;
				Double EquipmentSi = build.getEquipmentSi() == null?0D :Double.valueOf(build.getEquipmentSi().toPlainString()) ;
				Double GeneralMachineSi = build.getGeneralMachineSi() == null?0D :Double.valueOf(build.getGeneralMachineSi().toPlainString()) ;
				Double MachineEquipSi = build.getMachineEquipSi() == null?0D :Double.valueOf(build.getMachineEquipSi().toPlainString()) ;
				Double ManuUnitsSi = build.getManuUnitsSi() == null?0D :Double.valueOf(build.getManuUnitsSi().toPlainString()) ;
				Double plantSi = build.getPowerPlantSi() == null?0D :Double.valueOf(build.getPowerPlantSi().toPlainString()) ;
				Double machinerySi = ElecMachinesSi + BoilerPlantsSi + EquipmentSi + GeneralMachineSi + MachineEquipSi + ManuUnitsSi + plantSi ;
						
				buildingRes.setMachinerySi( machinerySi==null ? "" :machinerySi.toString());
				locationId=build.getLocationId().toString();
				locationName=build.getLocationName();
			}
			buildingRes.setLocationId(locationId);
			buildingRes.setLocationName(locationName);
			buildList.add(buildingRes);
			}
		}
			totalList.addAll(buildList);*/
//			totalList.addAll(paccGetResList);
			// Location Wise Details
		//	List<BuildingLocationDetails> buildLocList = new ArrayList<BuildingLocationDetails>();
//		List<SectionDataDetails> secDatas2 = secDataRepo.findByQuoteNoAndStatusNot(req.getQuoteNo(), "D");

			//List<SectionDataDetails> secDatas2 = secDataRepo.findByQuoteNo(req.getQuoteNo());
		Set<Integer> findlocationid = secDatas.stream().map(SectionDataDetails::getLocationId).distinct()
				.collect(Collectors.toSet());
		LocationDetailsRes locRes = null;
		SectionDetailsRes secRes = null;
		for (Integer d : findlocationid) {
			List<SectionDetailsRes>  buildingSectionList = new ArrayList<SectionDetailsRes>();
			 locRes = new LocationDetailsRes();
			List<SectionDataDetails> filter = secDatas.stream().filter(o -> o.getLocationId().equals(d))
					.collect(Collectors.toList());
			locRes.setLocationId(filter.get(0).getLocationId().toString());
			locRes.setLocationName(filter.get(0).getLocationName());

			for (SectionDataDetails sec : filter) {
				secRes = new SectionDetailsRes();
				System.out.println("Section :"+sec+"\nSection Id :"+sec.getSectionId()+"\n Location :"+d);
				
				secRes.setRiskId(sec.getRiskId().toString());
				secRes.setSectionId(sec.getSectionId().toString());
				secRes.setSectionName(sec.getSectionDesc());

				sectionId=sec.getSectionId()==null?"":sec.getSectionId().toString();
				if( sec.getProductType().equalsIgnoreCase("H") ) {
//					List<SectionDetails>  pacSectionList = new ArrayList<SectionDetails>();
					List<CommonDataDetails> accData =  	commonDataRepo.findByQuoteNoAndSectionIdAndLocationIdAndCoverIdAndRiskIdOrderByLocationIdAsc(req.getQuoteNo() , sec.getSectionId(),d,sec.getCoverId(),sec.getRiskId());
					for (CommonDataDetails	 acc : accData ) {
						
				//		List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(acc.getRiskId()) &&
				//				 o.getSectionId().toString().equals(acc.getSectionId()) &&  o.getLocationId().equals(acc.getLocationId()) ).collect(Collectors.toList());
//						SectionDetails buildSec = new SectionDetails(); 
						List<PolicyCoverData> filterCovers=	covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(acc.getRiskId())) &&
								o.getCompanyId().equals(acc.getCompanyId()) 
								&& o.getProductId().toString().equals(acc.getProductId()) && o.getSectionId().toString().equals(acc.getSectionId())
								&& String.valueOf(o.getCoverId()).equals(String.valueOf(acc.getCoverId()))
								&&  o.getLocationId().equals(acc.getLocationId())).collect(Collectors.toList());
						
						secRes.setSectionId(acc.getSectionId()==null?"":acc.getSectionId().toString());
						secRes.setSectionName( acc.getSectionDesc());
						if (filterCovers != null && !filterCovers.isEmpty()) {
							if (filterCovers.size() > 0) {
								Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
										.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

								List<CoverRes> coverListRes = getCoverDetails(groupByCover, covers);

								BigDecimal PremiumAfterDiscount = (coverListRes.stream()
										.filter(o -> o.getPremiumAfterDiscount() != null)
										.map(CoverRes::getPremiumAfterDiscount).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumAfterDiscountLc = (coverListRes.stream()
										.filter(o -> o.getPremiumAfterDiscountLC() != null)
										.map(CoverRes::getPremiumAfterDiscountLC).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumBeforeDiscount = (coverListRes.stream()
										.filter(o -> o.getPremiumBeforeDiscount() != null)
										.map(CoverRes::getPremiumBeforeDiscount).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream()
										.filter(o -> o.getPremiumBeforeDiscountLC() != null)
										.map(CoverRes::getPremiumBeforeDiscountLC).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumExcluedTax = (coverListRes.stream()
										.filter(o -> o.getPremiumExcluedTax() != null)
										.map(CoverRes::getPremiumExcluedTax).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumExcluedTaxLc = (coverListRes.stream()
										.filter(o -> o.getPremiumExcluedTaxLC() != null)
										.map(CoverRes::getPremiumExcluedTaxLC).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumIncludedTax = (coverListRes.stream()
										.filter(o -> o.getPremiumIncludedTax() != null)
										.map(CoverRes::getPremiumIncludedTax).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumIncludedTaxLc = (coverListRes.stream()
										.filter(o -> o.getPremiumIncludedTaxLC() != null)
										.map(CoverRes::getPremiumIncludedTaxLC).reduce((x, y) -> x.add(y)).get());
								secRes.setPremiumAfterDiscount(
										PremiumAfterDiscount == null ? "" : PremiumAfterDiscount.toString());
								secRes.setPremiumAfterDiscountLc(
										PremiumAfterDiscountLc == null ? "" : PremiumAfterDiscountLc.toString());
								secRes.setPremiumBeforeDiscount(
										PremiumBeforeDiscount == null ? "" : PremiumBeforeDiscount.toString());
								secRes.setPremiumBeforeDiscountLc(
										PremiumBeforeDiscountLc == null ? "" : PremiumBeforeDiscountLc.toString());
								secRes.setPremiumExcluedTax(
										PremiumExcluedTax == null ? "" : PremiumExcluedTax.toString());
								secRes.setPremiumExcluedTaxLc(
										PremiumExcluedTaxLc == null ? "" : PremiumExcluedTaxLc.toString());
								secRes.setPremiumIncludedTax(
										PremiumIncludedTax == null ? "" : PremiumIncludedTax.toString());
								secRes.setPremiumIncludedTaxLc(
										PremiumIncludedTaxLc == null ? "" : PremiumIncludedTaxLc.toString());

								secRes.setCovers(coverListRes);
							}
						}
					secRes.setRiskId(acc.getRiskId().toString());
					secRes.setLocationId(acc.getLocationId().toString());
					secRes.setLocationName(acc.getLocationName());
					secRes.setSumInsured(acc.getSumInsured()==null?"" : acc.getSumInsured().toPlainString());
					secRes.setCoverid(acc.getCoverId());
					buildingSectionList.add(secRes);
					locRes.setSectionDetails(buildingSectionList);
						
					}
					
				}else {
					List<BuildingRiskDetails> bulData =  	buildRiskRepo.findByQuoteNoAndSectionIdAndLocationIdAndRiskIdAndCoverIdOrderByLocationIdAsc(req.getQuoteNo() , sec.getSectionId(),d,sec.getRiskId(),sec.getCoverId());
					for (BuildingRiskDetails	 bul : bulData ) {
					List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(bul.getRiskId())) &&
							o.getCompanyId().equals(bul.getCompanyId()) 
							&& o.getProductId().toString().equals(bul.getProductId()) && o.getSectionId().toString().equals(bul.getSectionId())
							&& String.valueOf(o.getCoverId()).equals(String.valueOf(bul.getCoverId()))
							&&  o.getLocationId().equals(bul.getLocationId())).collect(Collectors.toList());
					
					if (filterCovers != null && !filterCovers.isEmpty()) {
						List<PolicyCoverData> filteredList = covers.stream()
								.filter(f -> filterCovers.stream()
										.anyMatch(c -> Objects.equals(c.getCoverId(), f.getDependentCoverId())))
								.collect(Collectors.toList());
						if (!filteredList.isEmpty()) {
							filterCovers.addAll(filteredList);
						}

						Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
								.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

						contentType = StringUtil.isBlank(bulData.get(0).getContentId())
								|| bulData.get(0).getContentId() == null ? "" : bulData.get(0).getContentId();
						contentDesc = StringUtil.isBlank(bulData.get(0).getContentDesc())
								|| bulData.get(0).getContentDesc() == null ? "" : bulData.get(0).getContentDesc();
						List<CoverRes> coverListRes = getCoverDetails(groupByCover, covers);
						// Build
//				/	SectionDetails buildSec = new SectionDetails(); 
						BigDecimal PremiumAfterDiscount = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumAfterDiscount)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumAfterDiscountLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null)
								.map(CoverRes::getPremiumAfterDiscountLC).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumBeforeDiscount = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null)
								.map(CoverRes::getPremiumBeforeDiscount).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null)
								.map(CoverRes::getPremiumBeforeDiscountLC).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumExcluedTax = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumExcluedTax)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumExcluedTaxLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumExcluedTaxLC)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumIncludedTax = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumIncludedTax)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumIncludedTaxLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumIncludedTaxLC)
								.reduce((x, y) -> x.add(y)).get());

						// get Section name Local from session master
						List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(
								bul.getSectionDesc() != null ? bul.getSectionDesc().toString() : " ");

						secRes.setSectionId(bul.getSectionId() == null ? "" : bul.getSectionId().toString());
						secRes.setSectionName(bul.getSectionDesc());
						secRes.setCodeDescLocal(
								(PSM != null && PSM.size() > 0) ? PSM.get(0).getSectionNameLocal() : " ");
						secRes.setCovers(coverListRes);
						secRes.setPremiumAfterDiscount(
								PremiumAfterDiscount == null ? "" : PremiumAfterDiscount.toString());
						secRes.setPremiumAfterDiscountLc(
								PremiumAfterDiscountLc == null ? "" : PremiumAfterDiscountLc.toString());
						secRes.setPremiumBeforeDiscount(
								PremiumBeforeDiscount == null ? "" : PremiumBeforeDiscount.toString());
						secRes.setPremiumBeforeDiscountLc(
								PremiumBeforeDiscountLc == null ? "" : PremiumBeforeDiscountLc.toString());
						secRes.setPremiumExcluedTax(PremiumExcluedTax == null ? "" : PremiumExcluedTax.toString());
						secRes.setPremiumExcluedTaxLc(
								PremiumExcluedTaxLc == null ? "" : PremiumExcluedTaxLc.toString());
						secRes.setPremiumIncludedTax(PremiumIncludedTax == null ? "" : PremiumIncludedTax.toString());
						secRes.setPremiumIncludedTaxLc(
								PremiumIncludedTaxLc == null ? "" : PremiumIncludedTaxLc.toString());
						locationId = bul.getLocationId().toString();
						locationName = bul.getLocationName();
						secRes.setLocationId(StringUtils.isBlank(locationId) ? "" : locationId);
						secRes.setLocationName(StringUtils.isBlank(locationName) ? "" : locationName);
						secRes.setSumInsured(bul.getSumInsured() == null ? "" : bul.getSumInsured().toPlainString());
//						secRes.setMoneyAnnualEstimate(bul.getMoneyAnnualEstimate() == null ? "0"
//								: bul.getMoneyAnnualEstimate().toPlainString());
//						secRes.setMoneyCollector(
//								bul.getMoneyCollector() == null ? "0" : bul.getMoneyCollector().toPlainString());
//						secRes.setMoneyDirectorResidence(bul.getMoneyDirectorResidence() == null ? "0"
//								: bul.getMoneyDirectorResidence().toPlainString());
//						secRes.setMoneyOutofSafe(
//								bul.getMoneyOutofSafe() == null ? "0" : bul.getMoneyOutofSafe().toPlainString());
//						secRes.setMoneySafeLimit(
//								bul.getMoneySafeLimit() == null ? "0" : bul.getMoneySafeLimit().toPlainString());
//						secRes.setMoneyMajorLoss(
//								bul.getMoneyMajorLoss() == null ? "0" : bul.getMoneyMajorLoss().toPlainString());
						secRes.setContentType(contentType);
						secRes.setContentDesc(contentDesc);
						secRes.setWallType(bul.getWallType() == null ? "" : bul.getWallType());
						secRes.setWallTypeDesc(bul.getWallTypeDesc() == null ? "" : bul.getWallTypeDesc());
						secRes.setRoofType(bul.getRoofType() == null ? "" : bul.getRoofType());
						secRes.setRoofTypeDesc(bul.getRoofTypeDesc() == null ? "" : bul.getRoofTypeDesc());
						secRes.setCoverid(bul.getCoverId());
						buildingSectionList.add(secRes);
						locRes.setSectionDetails(buildingSectionList);
					}
					}
				
			}
//				sectionList.add(secRes);
								
			}
			
			loctionList.add(locRes);
		}
			
	
			viewRes.setRiskDetails(totalList);
			viewRes.setDocumentDetails(documentDetails);
			viewRes.setLocationDetails(loctionList);	
					
					
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	private ViewQuoteRes setRemovalcoverEndt(ViewQuoteReq req, String requestReferenceNo,
			List<SectionDataDetails> secDatas1, List<PolicyCoverData> covers) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<EserviceSectionDetails> secDatas = eserSecRepo.findByRequestReferenceNo(requestReferenceNo);
			List<LocationDetailsRes>  loctionList = new ArrayList<LocationDetailsRes>();
			List<SectionDetailsRes>  sectionList = new ArrayList<SectionDetailsRes>();

			String locationId="";
			String locationName="";
			String sumInsured="0.0";
			String contentType="";
			String contentDesc="";
			String sectionId="";
			// Building Details 
			// Build
			// Section Details
			// Document
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();			
			List<Object> totalList =new ArrayList<Object>(); 
			List<PaccGetRes> paccGetResList = new ArrayList<PaccGetRes>(); 
			List<EserviceBuildingsDetailsRes>   buildList = new ArrayList<EserviceBuildingsDetailsRes>();
			
			
		
			Map<Integer, List<SectionDataDetails>> riskGroup =null;
			Map<Integer, List<SectionDataDetails>> locationGroup =null;
	//		locationGroup = secDatas.stream().filter( o -> o.getLocationId()!=null  ).collect( Collectors.groupingBy(SectionDataDetails :: getLocationId )) ;
		
		 Set<Integer> findlocationid = secDatas.stream().map(EserviceSectionDetails::getLocationId).distinct()
				.collect(Collectors.toSet());
		LocationDetailsRes locRes = null;
		SectionDetailsRes secRes = null;
		for (Integer d : findlocationid) {
			List<SectionDetailsRes>  buildingSectionList = new ArrayList<SectionDetailsRes>();
			 locRes = new LocationDetailsRes();
			List<EserviceSectionDetails> filter = secDatas.stream().filter(o -> o.getLocationId().equals(d)).collect(Collectors.toList());
			locRes.setLocationId(filter.get(0).getLocationId().toString());
			locRes.setLocationName(filter.get(0).getLocationName());

			for (EserviceSectionDetails sec : filter) {
				secRes = new SectionDetailsRes();
				System.out.println("Section :"+sec+"\nSection Id :"+sec.getSectionId()+"\n Location :"+d);
				
				secRes.setRiskId(sec.getRiskId().toString());
				secRes.setSectionId(sec.getSectionId().toString());
				secRes.setSectionName(sec.getSectionName());

				sectionId=sec.getSectionId()==null?"":sec.getSectionId().toString();
				if( sec.getProductType().equalsIgnoreCase("H") ) {
//					List<SectionDetails>  pacSectionList = new ArrayList<SectionDetails>();
					List<CommonDataDetails> accData =  	commonDataRepo.findByQuoteNoAndSectionIdAndLocationIdAndCoverIdAndRiskIdOrderByLocationIdAsc(req.getQuoteNo() , sec.getSectionId(),d,sec.getCoverId(),sec.getRiskId());
					for (CommonDataDetails	 acc : accData ) {
						
				//		List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(acc.getRiskId()) &&
				//				 o.getSectionId().toString().equals(acc.getSectionId()) &&  o.getLocationId().equals(acc.getLocationId()) ).collect(Collectors.toList());
//						SectionDetails buildSec = new SectionDetails(); 
						List<PolicyCoverData> filterCovers=	covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(acc.getRiskId())) &&
								o.getCompanyId().equals(acc.getCompanyId()) 
								&& o.getProductId().toString().equals(acc.getProductId()) && o.getSectionId().toString().equals(acc.getSectionId())
								&& String.valueOf(o.getCoverId()).equals(String.valueOf(acc.getCoverId()))
								&&  o.getLocationId().equals(acc.getLocationId())).collect(Collectors.toList());
						
						secRes.setSectionId(acc.getSectionId()==null?"":acc.getSectionId().toString());
						secRes.setSectionName( acc.getSectionDesc());
						if (filterCovers != null && !filterCovers.isEmpty()) {
							if (filterCovers.size() > 0) {
								Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
										.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

								List<CoverRes> coverListRes = getCoverDetails(groupByCover, covers);

								BigDecimal PremiumAfterDiscount = (coverListRes.stream()
										.filter(o -> o.getPremiumAfterDiscount() != null)
										.map(CoverRes::getPremiumAfterDiscount).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumAfterDiscountLc = (coverListRes.stream()
										.filter(o -> o.getPremiumAfterDiscountLC() != null)
										.map(CoverRes::getPremiumAfterDiscountLC).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumBeforeDiscount = (coverListRes.stream()
										.filter(o -> o.getPremiumBeforeDiscount() != null)
										.map(CoverRes::getPremiumBeforeDiscount).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream()
										.filter(o -> o.getPremiumBeforeDiscountLC() != null)
										.map(CoverRes::getPremiumBeforeDiscountLC).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumExcluedTax = (coverListRes.stream()
										.filter(o -> o.getPremiumExcluedTax() != null)
										.map(CoverRes::getPremiumExcluedTax).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumExcluedTaxLc = (coverListRes.stream()
										.filter(o -> o.getPremiumExcluedTaxLC() != null)
										.map(CoverRes::getPremiumExcluedTaxLC).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumIncludedTax = (coverListRes.stream()
										.filter(o -> o.getPremiumIncludedTax() != null)
										.map(CoverRes::getPremiumIncludedTax).reduce((x, y) -> x.add(y)).get());
								BigDecimal PremiumIncludedTaxLc = (coverListRes.stream()
										.filter(o -> o.getPremiumIncludedTaxLC() != null)
										.map(CoverRes::getPremiumIncludedTaxLC).reduce((x, y) -> x.add(y)).get());
								secRes.setPremiumAfterDiscount(
										PremiumAfterDiscount == null ? "" : PremiumAfterDiscount.toString());
								secRes.setPremiumAfterDiscountLc(
										PremiumAfterDiscountLc == null ? "" : PremiumAfterDiscountLc.toString());
								secRes.setPremiumBeforeDiscount(
										PremiumBeforeDiscount == null ? "" : PremiumBeforeDiscount.toString());
								secRes.setPremiumBeforeDiscountLc(
										PremiumBeforeDiscountLc == null ? "" : PremiumBeforeDiscountLc.toString());
								secRes.setPremiumExcluedTax(
										PremiumExcluedTax == null ? "" : PremiumExcluedTax.toString());
								secRes.setPremiumExcluedTaxLc(
										PremiumExcluedTaxLc == null ? "" : PremiumExcluedTaxLc.toString());
								secRes.setPremiumIncludedTax(
										PremiumIncludedTax == null ? "" : PremiumIncludedTax.toString());
								secRes.setPremiumIncludedTaxLc(
										PremiumIncludedTaxLc == null ? "" : PremiumIncludedTaxLc.toString());

								secRes.setCovers(coverListRes);
							}
						}
					secRes.setRiskId(acc.getRiskId().toString());
					secRes.setLocationId(acc.getLocationId().toString());
					secRes.setLocationName(acc.getLocationName());
					secRes.setSumInsured(acc.getSumInsured()==null?"" : acc.getSumInsured().toPlainString());
					secRes.setCoverid(acc.getCoverId());
					buildingSectionList.add(secRes);
					locRes.setSectionDetails(buildingSectionList);
						
					}
					
				}else {
					List<BuildingRiskDetails> bulData =  	buildRiskRepo.findByQuoteNoAndSectionIdAndLocationIdAndRiskIdAndCoverIdOrderByLocationIdAsc(req.getQuoteNo() , sec.getSectionId(),d,sec.getRiskId(),sec.getCoverId());
					for (BuildingRiskDetails	 bul : bulData ) {
					List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(bul.getRiskId())) &&
							o.getCompanyId().equals(bul.getCompanyId()) 
							&& o.getProductId().toString().equals(bul.getProductId()) && o.getSectionId().toString().equals(bul.getSectionId())
							&& String.valueOf(o.getCoverId()).equals(String.valueOf(bul.getCoverId()))
							&&  o.getLocationId().equals(bul.getLocationId())).collect(Collectors.toList());
					
					if (filterCovers != null && !filterCovers.isEmpty()) {
						List<PolicyCoverData> filteredList = covers.stream()
								.filter(f -> filterCovers.stream()
										.anyMatch(c -> Objects.equals(c.getCoverId(), f.getDependentCoverId())))
								.collect(Collectors.toList());
						if (!filteredList.isEmpty()) {
							filterCovers.addAll(filteredList);
						}

						Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
								.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

						contentType = StringUtil.isBlank(bulData.get(0).getContentId())
								|| bulData.get(0).getContentId() == null ? "" : bulData.get(0).getContentId();
						contentDesc = StringUtil.isBlank(bulData.get(0).getContentDesc())
								|| bulData.get(0).getContentDesc() == null ? "" : bulData.get(0).getContentDesc();
						List<CoverRes> coverListRes = getCoverDetails(groupByCover, covers);
						// Build
//				/	SectionDetails buildSec = new SectionDetails(); 
						BigDecimal PremiumAfterDiscount = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumAfterDiscount)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumAfterDiscountLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null)
								.map(CoverRes::getPremiumAfterDiscountLC).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumBeforeDiscount = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null)
								.map(CoverRes::getPremiumBeforeDiscount).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null)
								.map(CoverRes::getPremiumBeforeDiscountLC).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumExcluedTax = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumExcluedTax)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumExcluedTaxLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumExcluedTaxLC)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumIncludedTax = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumIncludedTax)
								.reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumIncludedTaxLc = (coverListRes.stream()
								.filter(o -> o.getPremiumAfterDiscount() != null).map(CoverRes::getPremiumIncludedTaxLC)
								.reduce((x, y) -> x.add(y)).get());

						// get Section name Local from session master
						List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(
								bul.getSectionDesc() != null ? bul.getSectionDesc().toString() : " ");

						secRes.setSectionId(bul.getSectionId() == null ? "" : bul.getSectionId().toString());
						secRes.setSectionName(bul.getSectionDesc());
						secRes.setCodeDescLocal(
								(PSM != null && PSM.size() > 0) ? PSM.get(0).getSectionNameLocal() : " ");
						secRes.setCovers(coverListRes);
						secRes.setPremiumAfterDiscount(
								PremiumAfterDiscount == null ? "" : PremiumAfterDiscount.toString());
						secRes.setPremiumAfterDiscountLc(
								PremiumAfterDiscountLc == null ? "" : PremiumAfterDiscountLc.toString());
						secRes.setPremiumBeforeDiscount(
								PremiumBeforeDiscount == null ? "" : PremiumBeforeDiscount.toString());
						secRes.setPremiumBeforeDiscountLc(
								PremiumBeforeDiscountLc == null ? "" : PremiumBeforeDiscountLc.toString());
						secRes.setPremiumExcluedTax(PremiumExcluedTax == null ? "" : PremiumExcluedTax.toString());
						secRes.setPremiumExcluedTaxLc(
								PremiumExcluedTaxLc == null ? "" : PremiumExcluedTaxLc.toString());
						secRes.setPremiumIncludedTax(PremiumIncludedTax == null ? "" : PremiumIncludedTax.toString());
						secRes.setPremiumIncludedTaxLc(
								PremiumIncludedTaxLc == null ? "" : PremiumIncludedTaxLc.toString());
						locationId = bul.getLocationId().toString();
						locationName = bul.getLocationName();
						secRes.setLocationId(StringUtils.isBlank(locationId) ? "" : locationId);
						secRes.setLocationName(StringUtils.isBlank(locationName) ? "" : locationName);
						secRes.setSumInsured(bul.getSumInsured() == null ? "" : bul.getSumInsured().toPlainString());
//						secRes.setMoneyAnnualEstimate(bul.getMoneyAnnualEstimate() == null ? "0"
//								: bul.getMoneyAnnualEstimate().toPlainString());
//						secRes.setMoneyCollector(
//								bul.getMoneyCollector() == null ? "0" : bul.getMoneyCollector().toPlainString());
//						secRes.setMoneyDirectorResidence(bul.getMoneyDirectorResidence() == null ? "0"
//								: bul.getMoneyDirectorResidence().toPlainString());
//						secRes.setMoneyOutofSafe(
//								bul.getMoneyOutofSafe() == null ? "0" : bul.getMoneyOutofSafe().toPlainString());
//						secRes.setMoneySafeLimit(
//								bul.getMoneySafeLimit() == null ? "0" : bul.getMoneySafeLimit().toPlainString());
//						secRes.setMoneyMajorLoss(
//								bul.getMoneyMajorLoss() == null ? "0" : bul.getMoneyMajorLoss().toPlainString());
						secRes.setContentType(contentType);
						secRes.setContentDesc(contentDesc);
						secRes.setWallType(bul.getWallType() == null ? "" : bul.getWallType());
						secRes.setWallTypeDesc(bul.getWallTypeDesc() == null ? "" : bul.getWallTypeDesc());
						secRes.setRoofType(bul.getRoofType() == null ? "" : bul.getRoofType());
						secRes.setRoofTypeDesc(bul.getRoofTypeDesc() == null ? "" : bul.getRoofTypeDesc());
						secRes.setCoverid(bul.getCoverId());
						buildingSectionList.add(secRes);
						locRes.setSectionDetails(buildingSectionList);
					}
					}
				
			}
//				sectionList.add(secRes);
								
			}
			
			loctionList.add(locRes);
		}
			
	
			viewRes.setRiskDetails(totalList);
			viewRes.setDocumentDetails(documentDetails);
			viewRes.setLocationDetails(loctionList);	
					
					
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}

	public List<CoverRes> getCoverDetails(Map<Integer,List<PolicyCoverData>> groupByCover, List<PolicyCoverData> covers ) {
		List<CoverRes>  coverListRes = new ArrayList<CoverRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			for ( Integer coverId : groupByCover.keySet() ) {
				List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
				CoverRes coverRes = new CoverRes();
				
				
				if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
					// Get Covers
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes = dozerMapper.map(filterCover.get(0), CoverRes.class);
					List<SectionCoverMaster> coverDetails = sectioncoverrepo
							.findByCompanyIdAndSectionIdAndCoverIdOrderByAmendIdDesc(filterCover.get(0).getCompanyId(),
									Integer.valueOf(filterCover.get(0).getSectionId()
											), Integer.valueOf(coverId));
					coverRes.setCoverNameLocal((coverDetails != null && !coverDetails.isEmpty()
							&& coverDetails.get(0).getCoverNameLocal() != null)
									? coverDetails.get(0).getCoverNameLocal()
									: "");
					coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
					coverRes.setIsselected(filterCover.get(0).getIsSelected());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
					coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setRegulatoryCode(filterCover.get(0).getRegulatoryCode());
					coverRes.setCoverageType(filterCover.get(0).getCoverageType());
					coverRes.setCalcType(filterCover.get(0).getCalcType());
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
					coverRes.setCoverageLimit(filterCover.get(0).getCoverageLimit()==null ? "" :filterCover.get(0).getCoverageLimit().toPlainString());
					
					List<PolicyCoverData> endCoverData = covers.stream()
							.filter(a -> a.getCoverId().equals(coverId)
									&& a.getSectionId().equals(coverGroups.get(0).getSectionId())  
									&& a.getVehicleId() == filterCover.get(0).getVehicleId()
									&& a.getCoverageType().equalsIgnoreCase("E")
									&& a.getLocationId().equals(filterCover.get(0).getLocationId()) 
							        ) //&& Objects.equals(a.getDiscLoadId(), 842)
							.collect(Collectors.toList());
					if (endCoverData != null && !endCoverData.isEmpty()){
						EndorsmentRes endorsmentRes = new EndorsmentRes();
						endorsmentRes = dozerMapper.map(endCoverData.get(0), EndorsmentRes.class);
						endorsmentRes.setPremiumAfterDiscount(endCoverData.get(0).getPremiumAfterDiscountFc());
						endorsmentRes.setPremiumBeforeDiscount(endCoverData.get(0).getPremiumBeforeDiscountFc());
						endorsmentRes.setPremiumExcluedTax(endCoverData.get(0).getPremiumExcludedTaxFc());
						endorsmentRes.setPremiumIncludedTax(endCoverData.get(0).getPremiumIncludedTaxFc());
						endorsmentRes.setPremiumAfterDiscountLC(endCoverData.get(0).getPremiumAfterDiscountLc());
						endorsmentRes.setPremiumBeforeDiscountLC(endCoverData.get(0).getPremiumBeforeDiscountLc());
						endorsmentRes.setPremiumExcluedTaxLC(endCoverData.get(0).getPremiumExcludedTaxLc());
						endorsmentRes.setPremiumIncludedTaxLC(endCoverData.get(0).getPremiumIncludedTaxLc());
						endorsmentRes.setCoverageType(endCoverData.get(0).getCoverageType());
						endorsmentRes.setCalcType(endCoverData.get(0).getCalcType());
						endorsmentRes.setExcessAmount(endCoverData.get(0).getExcessAmount() == null ? ""
								: endCoverData.get(0).getExcessAmount().toPlainString());
						endorsmentRes.setExcessPercent(endCoverData.get(0).getExcessPercent() == null ? ""
								: endCoverData.get(0).getExcessPercent().toPlainString());
						endorsmentRes.setCoverageLimit(endCoverData.get(0).getCoverageLimit() == null ? ""
								: endCoverData.get(0).getCoverageLimit().toPlainString());
						//endorsmentRes.setPolicyPeriod(endCoverData.get(0).getNoOfDays()+);
						coverRes.setEndorsmentRes(endorsmentRes);
					}
//					// Discount Covers Or Promo Covers
//					List<PolicyCoverData> filterDiscountCover = coverGroups.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
//					
//					if ( filterDiscountCover.size() > 0 ) {
//						 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
//						 coverRes.setDiscounts(discounts);	
//					}
//					
//					// Tax Covers
//					List<PolicyCoverData> filterTaxCover = coverGroups.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
//					
//					if( filterTaxCover.size() > 0 ) {
//						 List<Tax> taxes = getTaxRates(filterTaxCover) ;
//						 coverRes.setTaxes(taxes);	
//					}
//					
//					// Loginds Covers
//					List<PolicyCoverData> filterLodingCover = coverGroups.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
//					
//					if( filterLodingCover.size() > 0 ) {
//						 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
//						 coverRes.setLoadings(lodings);	
//					}
										
				} else {
					
					// Get Sub Covers
			
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					List<SectionCoverMaster> coverdetails=new ArrayList<>();
					/*if(filterCover!=null ||! filterCover.isEmpty() ||filterCover.size()>=0)
					{
						coverdetails= sectioncoverrepo.findByCompanyIdAndSectionIdAndCoverIdOrderByAmendIdDesc(filterCover.get(0).getCompanyId(), filterCover.get(0).getSectionId(), coverId);
					    if(!coverdetails.isEmpty() )
					    coverRes.setCoverNameLocal(coverdetails.get(0).getCoverNameLocal());
					      
					}*/
					List<SectionCoverMaster> coverDetails = sectioncoverrepo
							.findByCompanyIdAndSectionIdAndCoverIdOrderByAmendIdDesc(filterCover.get(0).getCompanyId(),
									Integer.valueOf(filterCover.get(0).getSectionId()), Integer.valueOf(coverId));
					coverRes.setCoverNameLocal((coverDetails != null && !coverDetails.isEmpty()
							&& coverDetails.get(0).getCoverNameLocal() != null)
									? coverDetails.get(0).getCoverNameLocal()
									: "");
					coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
					 coverRes.setCoverName(filterCover.get(0).getCoverName());

					 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
					 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
					 coverRes.setSumInsuredLc(filterCover.get(0).getSumInsuredLc()==null ? null : new BigDecimal(filterCover.get(0).getSumInsuredLc().toString()));
					 coverRes.setRate(filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
					coverRes.setCoverageLimit(filterCover.get(0).getCoverageLimit()==null ? "" :filterCover.get(0).getCoverageLimit().toPlainString());
					
					List<SubCoverRes>  subCoverListRes = new ArrayList<SubCoverRes>();
					List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getPremiumExcludedTaxLc()!=null ).collect(Collectors.toList());
					for ( PolicyCoverData subCovers : filterSubCover) {
						SubCoverRes subCoverRes = new SubCoverRes();
						List<SectionCoverMaster> subcoverdetails=new ArrayList<>();
						
					/*	subcoverdetails= sectioncoverrepo.findByCompanyIdAndSectionIdAndCoverIdOrderByAmendIdDesc(subCovers.getCompanyId(), subCovers.getSectionId(), coverId);
						    if(subcoverdetails!=null || subcoverdetails.size()>=0) {
						    	subCoverRes.setSubCoverNameLocal(subcoverdetails.get(0).getCoverNameLocal());
						    }
						*/
						subCoverRes = dozerMapper.map(subCovers, SubCoverRes.class);
						subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
						subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
						subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
						subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
						subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
						subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
						subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
						subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
						subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
						subCoverRes.setRegulatoryCode(filterCover.get(0).getRegulatoryCode());
						subCoverRes.setCoverageLimit(filterSubCover.get(0).getCoverageLimit()==null ? "" :filterSubCover.get(0).getCoverageLimit().toPlainString());
						coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
						coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
						coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
						coverRes.setCoverageLimit(filterCover.get(0).getCoverageLimit()==null ? "" :filterCover.get(0).getCoverageLimit().toPlainString());
						
//						// Discount Covers Or Promo Covers
//						List<PolicyCoverData> filterDiscountCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") )  ).collect(Collectors.toList());
//						
//						if ( filterDiscountCover.size() > 0 ) {
//							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
//							 subCoverRes.setDiscounts(discounts);	
//						}
//						
//						// Tax Covers
//						List<PolicyCoverData> filterTaxCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
//						
//						if( filterTaxCover.size() > 0 ) {
//							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
//							 subCoverRes.setTaxes(taxes);	
//						}
//						
//						// Loginds Covers
//						List<PolicyCoverData> filterLodingCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
//						
//						if( filterLodingCover.size() > 0 ) {
//							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
//							 subCoverRes.setLoadings(lodings);	
//						}
						subCoverListRes.add(subCoverRes);
					}
					coverRes.setSubcovers(subCoverListRes);
				}
				coverListRes.add(coverRes);
			}
	
			coverListRes.sort(Comparator.comparing(CoverRes :: getCoverId));;
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return coverListRes;
	}
	
	public synchronized List<ListItemValue> getFirstLossDropDown(String insuranceId, String branchCode, String itemType) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n12 = cb.equal(c.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
	//		Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
		//	Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			query.where(n13, n2, n3, n4, n9, n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	public ViewQuoteRes getTravelProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<LocationDetailsRes>  loctionList = new ArrayList<LocationDetailsRes>();
			List<EserviceTravelGetRes>   travelResList = new ArrayList<EserviceTravelGetRes>();
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();
			// Find Travel Data
			List<TravelPassengerDetails> travelDatas =  traPassRepo.findByQuoteNo(req.getQuoteNo());
			List<TravelPassengerDetails> adultDatas  = travelDatas.stream().filter( o -> o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> otherDatas  =  travelDatas.stream().filter( o -> ! o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> totalDatas  = new ArrayList<TravelPassengerDetails>();	
			totalDatas.addAll(adultDatas);
			totalDatas.addAll(otherDatas);
			if(travelDatas.size() > 0 ) {
				ProductGroupDropDownReq groupReq = new ProductGroupDropDownReq();
				groupReq.setBranchCode(travelDatas.get(0).getBranchCode());
				groupReq.setInsuranceId(travelDatas.get(0).getCompanyId());		
				groupReq.setProductId(travelDatas.get(0).getProductId().toString());	
				
				List<ProductGroupMasterDropDownRes> groupRes =	groupService.getProductGroupMasterDropdown(groupReq);
				
				List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
				
				
				for (TravelPassengerDetails tra :  totalDatas) {
					EserviceTravelGetRes travelDetails = new  EserviceTravelGetRes()  ;
					dozerMapper.map(tra, travelDetails);
					travelDetails.setRiskId(tra.getPassengerId().toString());
					travelDetails.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
					travelDetails.setPassengerId(tra.getPassengerId().toString());
					travelDetails.setPassengerName(tra.getPassengerName());
					
					List<PassengerSectionDetails>  SectionList = new ArrayList<PassengerSectionDetails>();	
//					 List<BrokerCommissionDetails> policylist = getPolicyName(tra.getCompanyId() , tra.getProductId().toString(), tra.getCreatedBy(),tra.getBrokerCode(), tra.getSectionId().toString());
//					 Double commissionPercent =0.0;
//					 if(policylist.size()>0 && policylist!=null) {
//					 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//					 }
//					 else {
//					 commissionPercent =5.0;
//					 }
					 String premiumFc = tra.getOverallPremiumFc().toString();
					 String vatPremiumFc =	tra.getOverallPremiumFc().toString();
					 BigDecimal commission=	new BigDecimal(premiumFc)
				 				.multiply(tra.getCommissionPercentage()==null?BigDecimal.ZERO : tra.getCommissionPercentage() )
				 				.divide(BigDecimal.valueOf(100D))
		 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
		 						.getPrecision(),RoundingMode.HALF_UP);
		
					 travelDetails.setOverAllPremiumFc(tra.getOverallPremiumFc()==null?0: tra.getOverallPremiumFc() );
					 travelDetails.setOverAllPremiumLc(tra.getOverallPremiumLc()==null?0:tra.getOverallPremiumLc());
					 travelDetails.setPremiumFc(tra.getActualPremiumFc()==null?0:tra.getActualPremiumFc() );
					 travelDetails.setPremiumLc(tra.getActualPremiumLc()==null?0:tra.getActualPremiumLc());
					 travelDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
					 travelDetails.setCommissionPercentage(tra.getCommissionPercentage()==null?"" : tra.getCommissionPercentage().toPlainString());
					 travelDetails.setVatCommission(tra.getVatCommission()==null?"" : tra.getVatCommission().toPlainString());				
					 			 
					 // Cover Details
					List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(tra.getGroupId()))).collect(Collectors.toList());
					
					
					List<CoverRes>  coverListRes = new ArrayList<CoverRes>();
					BigDecimal PremiumAfterDiscount = new BigDecimal(0);
					BigDecimal PremiumAfterDiscountLc = new BigDecimal(0);
					BigDecimal PremiumBeforeDiscount = new BigDecimal(0);
					BigDecimal PremiumBeforeDiscountLc = new BigDecimal(0);
					BigDecimal PremiumExcluedTax = new BigDecimal(0);
					BigDecimal PremiumExcluedTaxLc = new BigDecimal(0);
					BigDecimal PremiumIncludedTax = new BigDecimal(0);
					BigDecimal PremiumIncludedTaxLc = new BigDecimal(0);
					
					if( filterCovers.size() > 0 ) {
						Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));
						coverListRes = getCoverDetails(groupByCover, covers);
						PremiumAfterDiscount = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
						PremiumAfterDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
						PremiumBeforeDiscount = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
						PremiumBeforeDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
						PremiumExcluedTax = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
						PremiumExcluedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
						PremiumIncludedTax = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
						PremiumIncludedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
						
					}
					
					
//					// Response
//					List<PassengerSectionDetails> secList = new ArrayList<PassengerSectionDetails>();
//					// Passenger
//					PassengerSectionDetails traSec = new PassengerSectionDetails(); 
//					
//					traSec.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
//					traSec.setSectionName( tra.getSectionName());
//					traSec.setCovers(coverListRes);
//					traSec.setPassengerId(tra.getPassengerId().toString() );
//					traSec.setPassengerName(tra.getPassengerName());
//					traSec.setGroupDesc(groupRes.stream().filter( o -> o.getCode().equalsIgnoreCase(tra.getGroupId().toString()) ).collect(Collectors.toList()).get(0).getCodeDesc()) ;		
//					traSec.setGroupId(tra.getGroupId().toString());
//					secList.add(traSec);
//					travelDetails.setSectionDetails(secList);
					
					// Document 
					DocumentDetails  document = new DocumentDetails();
					document.setDocumentTitle(tra.getPassengerName());
					document.setRiskId(tra.getPassengerId().toString());
					document.setSectionId(tra.getSectionId().toString());
					documentDetails.add(document);
					
					//get Section name Local from session master 
					List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(tra.getSectionName()!=null ? tra.getSectionName().toString() : " ");
					
					PassengerSectionDetails sec = new PassengerSectionDetails();
					sec.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
					sec.setSectionName( tra.getSectionName());
					sec.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
					sec.setPassengerId(tra.getPassengerId().toString() );
					sec.setPassengerName(tra.getPassengerName());
					sec.setCovers(coverListRes);
					sec.setGroupDesc(groupRes.stream().filter( o -> o.getCode().equalsIgnoreCase(tra.getGroupId().toString()) ).collect(Collectors.toList()).get(0).getCodeDesc()) ;		
					sec.setGroupId(tra.getGroupId().toString());
					sec.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
					sec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
					sec.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
					sec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
					sec.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
					sec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
					sec.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
					sec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());
				        
					
					SectionList.add(sec);
					travelDetails.setSectionDetails(SectionList);	
					travelResList.add(travelDetails);
				}
				viewRes.setRiskDetails(travelResList);	
				viewRes.setDocumentDetails(documentDetails);
			}
			
			Set<Integer> findlocationid = travelDatas.stream().map(TravelPassengerDetails::getLocationId).distinct()
					.collect(Collectors.toSet());
			LocationDetailsRes locRes = null;
			SectionDetailsRes secRes = null;
			List<TravelPassengerDetails> travelDatas1 =  traPassRepo.findByQuoteNo(req.getQuoteNo());
			List<TravelPassengerDetails> adultDatas1  = travelDatas1.stream().filter( o -> o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> otherDatas1  =  travelDatas1.stream().filter( o -> ! o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> totalDatas1  = new ArrayList<TravelPassengerDetails>();	
			totalDatas1.addAll(adultDatas1);
			totalDatas1.addAll(otherDatas1);
			if(travelDatas1.size() > 0 ) {
			ProductGroupDropDownReq groupReq = new ProductGroupDropDownReq();
			groupReq.setBranchCode(travelDatas1.get(0).getBranchCode());
			groupReq.setInsuranceId(travelDatas1.get(0).getCompanyId());
			groupReq.setProductId(travelDatas1.get(0).getProductId().toString());

			List<ProductGroupMasterDropDownRes> groupRes = groupService.getProductGroupMasterDropdown(groupReq);

			List<PolicyCoverData> covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			for (Integer d : findlocationid) {
				List<SectionDetailsRes> sectionList = new ArrayList<SectionDetailsRes>();
				locRes = new LocationDetailsRes();
				List<TravelPassengerDetails> filter = travelDatas1.stream().filter(o -> o.getLocationId().equals(d))
						.collect(Collectors.toList());
				locRes.setLocationId((filter.get(0).getLocationId() == null
						|| StringUtils.isBlank(filter.get(0).getLocationId().toString())) ? "1"
								: filter.get(0).getLocationId().toString());
				locRes.setLocationName("");

				for (TravelPassengerDetails tra : totalDatas1) {
					secRes = new SectionDetailsRes();
					System.out.println("Section :" + tra + "\nSection Id :" + tra.getSectionId() + "\n Location :" + d);

//					EserviceTravelGetRes travelDetails = new  EserviceTravelGetRes()  ;
//					dozerMapper.map(tra, travelDetails);
					secRes.setRiskId(tra.getPassengerId().toString());
					secRes.setSectionId(tra.getSectionId() == null ? "" : tra.getSectionId().toString());
					secRes.setPassengerId(tra.getPassengerId().toString());
					secRes.setPassengerName(tra.getPassengerName());

					List<PassengerSectionDetails> SectionList = new ArrayList<PassengerSectionDetails>();
					String premiumFc = tra.getOverallPremiumFc().toString();
					String vatPremiumFc = tra.getOverallPremiumFc().toString();
					BigDecimal commission = new BigDecimal(premiumFc)
							.multiply(tra.getCommissionPercentage() == null ? BigDecimal.ZERO
									: tra.getCommissionPercentage())
							.divide(BigDecimal.valueOf(100D))
							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);

					secRes
							.setOverAllPremiumFc(tra.getOverallPremiumFc() == null ? 0 : tra.getOverallPremiumFc());
					secRes
							.setOverAllPremiumLc(tra.getOverallPremiumLc() == null ? 0 : tra.getOverallPremiumLc());
					secRes.setPremiumFc(tra.getActualPremiumFc() == null ? 0 : tra.getActualPremiumFc());
					secRes.setPremiumLc(tra.getActualPremiumLc() == null ? 0 : tra.getActualPremiumLc());
					secRes.setCommissionAmount(commission.toString() == null ? "" : commission.toString());
					secRes.setCommissionPercentage(
							tra.getCommissionPercentage() == null ? "" : tra.getCommissionPercentage().toPlainString());
					secRes.setVatCommission(
							tra.getVatCommission() == null ? "" : tra.getVatCommission().toPlainString());

					// Cover Details
					List<PolicyCoverData> filterCovers = covers.stream()
							.filter(o -> o.getVehicleId().equals(Integer.valueOf(tra.getGroupId())))
							.collect(Collectors.toList());

					List<CoverRes> coverListRes = new ArrayList<CoverRes>();
					BigDecimal PremiumAfterDiscount = new BigDecimal(0);
					BigDecimal PremiumAfterDiscountLc = new BigDecimal(0);
					BigDecimal PremiumBeforeDiscount = new BigDecimal(0);
					BigDecimal PremiumBeforeDiscountLc = new BigDecimal(0);
					BigDecimal PremiumExcluedTax = new BigDecimal(0);
					BigDecimal PremiumExcluedTaxLc = new BigDecimal(0);
					BigDecimal PremiumIncludedTax = new BigDecimal(0);
					BigDecimal PremiumIncludedTaxLc = new BigDecimal(0);

					if (filterCovers.size() > 0) {
						Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
								.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));
						coverListRes = getCoverDetails(groupByCover, covers);
						PremiumAfterDiscount = (coverListRes.stream().map(CoverRes::getPremiumAfterDiscount)
								.reduce((x, y) -> x.add(y)).get());
						PremiumAfterDiscountLc = (coverListRes.stream().map(CoverRes::getPremiumAfterDiscountLC)
								.reduce((x, y) -> x.add(y)).get());
						PremiumBeforeDiscount = (coverListRes.stream().map(CoverRes::getPremiumBeforeDiscount)
								.reduce((x, y) -> x.add(y)).get());
						PremiumBeforeDiscountLc = (coverListRes.stream().map(CoverRes::getPremiumBeforeDiscountLC)
								.reduce((x, y) -> x.add(y)).get());
						PremiumExcluedTax = (coverListRes.stream().map(CoverRes::getPremiumExcluedTax)
								.reduce((x, y) -> x.add(y)).get());
						PremiumExcluedTaxLc = (coverListRes.stream().map(CoverRes::getPremiumExcluedTaxLC)
								.reduce((x, y) -> x.add(y)).get());
						PremiumIncludedTax = (coverListRes.stream().map(CoverRes::getPremiumIncludedTax)
								.reduce((x, y) -> x.add(y)).get());
						PremiumIncludedTaxLc = (coverListRes.stream().map(CoverRes::getPremiumIncludedTaxLC)
								.reduce((x, y) -> x.add(y)).get());

					}

					// get Section name Local from session master
					List<ProductSectionMaster> PSM = productSectionMasterRepo
							.findBySectionName(tra.getSectionName() != null ? tra.getSectionName().toString() : " ");

//					PassengerSectionDetails sec = new PassengerSectionDetails();
					secRes.setSectionId(tra.getSectionId() == null ? "" : tra.getSectionId().toString());
					secRes.setSectionName(tra.getSectionName());
					secRes.setCodeDescLocal((PSM != null && PSM.size() > 0) ? PSM.get(0).getSectionNameLocal() : " ");
					secRes.setPassengerId(tra.getPassengerId().toString());
					secRes.setPassengerName(tra.getPassengerName());
					secRes.setCovers(coverListRes);
					secRes.setGroupDesc(
							groupRes.stream().filter(o -> o.getCode().equalsIgnoreCase(tra.getGroupId().toString()))
									.collect(Collectors.toList()).get(0).getCodeDesc());
					secRes.setGroupId(tra.getGroupId().toString());
					secRes.setPremiumAfterDiscount(
							PremiumAfterDiscount.toString() == null ? "" : PremiumAfterDiscount.toString());
					secRes.setPremiumAfterDiscountLc(
							PremiumAfterDiscountLc.toString() == null ? "" : PremiumAfterDiscountLc.toString());
					secRes.setPremiumBeforeDiscount(
							PremiumBeforeDiscount.toString() == null ? "" : PremiumBeforeDiscount.toString());
					secRes.setPremiumBeforeDiscountLc(
							PremiumBeforeDiscountLc.toString() == null ? "" : PremiumBeforeDiscountLc.toString());
					secRes.setPremiumExcluedTax(PremiumExcluedTax.toString() == null ? "" : PremiumExcluedTax.toString());
					secRes.setPremiumExcluedTaxLc(
							PremiumExcluedTaxLc.toString() == null ? "" : PremiumExcluedTaxLc.toString());
					secRes.setPremiumIncludedTax(
							PremiumIncludedTax.toString() == null ? "" : PremiumIncludedTax.toString());
					secRes.setPremiumIncludedTaxLc(
							PremiumIncludedTaxLc.toString() == null ? "" : PremiumIncludedTaxLc.toString());

					sectionList.add(secRes);
				}

				locRes.setSectionDetails(sectionList);
				loctionList.add(locRes);
			}
		}

			viewRes.setLocationDetails(loctionList);		
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	
	
	public ViewQuoteRes getCommonProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<CommonDataDetails> commonDatas =  commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<EserviceCommonGetRes>   commonResList = new ArrayList<EserviceCommonGetRes>();
			List<LocationDetailsRes>  loctionList = new ArrayList<LocationDetailsRes>();
			
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();
			for (CommonDataDetails com :  commonDatas) {
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(com.getRiskId())) 
						&& Objects.equals(o.getCoverId(),com.getCoverId())).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<CoverRes>  coverListRes = getCoverDetails(groupByCover, covers);
				BigDecimal PremiumAfterDiscount = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscount = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTax = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTax = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());

				// Response
				// Mot
//				 List<BrokerCommissionDetails> policylist = getPolicyName(com.getCompanyId() , com.getProductId().toString(), com.getCreatedBy(),com.getAgencyCode(),"99999");
//			
//				 Double commissionPercent = 0.0;
//					if(policylist.size()>0 && policylist!=null) {
//					
//				 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//					}
//					else {
//						commissionPercent =5.0;
//					}
				 String premiumFc = com.getOverallPremiumFc().toString();
				 String vatPremiumFc =	com.getOverallPremiumFc().toString();
				 BigDecimal commission=	new BigDecimal(premiumFc)
			 				.multiply(com.getCommissionPercentage()==null ?  new BigDecimal("0") : com.getCommissionPercentage())
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);

				
				EserviceCommonGetRes commonDetails = new  EserviceCommonGetRes()  ;
				dozerMapper.map(com, commonDetails);
				commonDetails.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
				commonDetails.setOverAllPremiumFc(com.getOverallPremiumFc()==null?0D:Double.valueOf(com.getOverallPremiumFc().toString()));
				commonDetails.setOverAllPremiumLc(com.getOverallPremiumLc()==null?0D:Double.valueOf(com.getOverallPremiumLc().toString()));
				commonDetails.setPremiumFc(com.getActualPremiumFc()==null?0D:Double.valueOf(com.getActualPremiumFc().toString()));
				commonDetails.setPremiumLc(com.getActualPremiumLc()==null?0D:Double.valueOf(com.getActualPremiumLc().toString()));
				commonDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
				commonDetails.setCommissionPercentage(com.getCommissionPercentage()==null?"" : com.getCommissionPercentage().toPlainString());
				commonDetails.setVatCommission(com.getVatCommission()==null?"" : com.getVatCommission().toPlainString());				
				commonDetails.setFinalizeYn(com.getFinalizeYn());
				commonDetails.setLocationId(com.getLocationId().toString());
				commonDetails.setLocationName(com.getLocationName());
				commonDetails.setOccupationTypeDesc(com.getOccupationDesc());				
				
				//get Section name Local from session master 
				List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(com.getSectionDesc()!=null ? com.getSectionDesc().toString() : " ");
				
				// Section Details
				SectionDetails sec = new SectionDetails(); 
				sec.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
				sec.setSectionName( com.getSectionDesc());
				sec.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
				sec.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
				sec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
				sec.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
				sec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
				sec.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
				sec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
				sec.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
				sec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());

				sec.setCovers(coverListRes);
			
				List<SectionDetails>  sectionList = new ArrayList<SectionDetails>();
				sectionList.add(sec);
				commonDetails.setSectionDetails(sectionList);
				commonResList.add(commonDetails);
				
				
				}
//			//Common Document 
//			DocumentDetails  document = new DocumentDetails();
//			document.setDocumentTitle("Common Documents");
//			document.setRiskId("1");
//			document.setSectionId("99999");
//			documentDetails.add(document);
			
			// Induvidual Document
			List<ProductEmployeeDetails> empList = empRepo.findByQuoteNo(req.getQuoteNo());
			if(empList.size()  > 0) {
				for(ProductEmployeeDetails data : empList) {
					// Document 
					DocumentDetails  document2 = new DocumentDetails();
					document2.setDocumentTitle(   " Emp Id :" +  data.getEmployeeId() + " ~ Emp Name : " + data.getEmployeeName() );
					document2.setRiskId(data.getEmployeeId().toString());
					document2.setSectionId(  "99999"  )  ;
					documentDetails.add(document2);
				}
			} 
			String SunInsured="0";
			Set<Integer> findlocationid = commonDatas.stream().map(CommonDataDetails::getLocationId).distinct()
					.collect(Collectors.toSet());
			for (Integer d : findlocationid) {
				List<SectionDetailsRes>  sList = new ArrayList<SectionDetailsRes>();
				LocationDetailsRes locRes = new LocationDetailsRes();
				List<CommonDataDetails> filter = commonDatas.stream().filter(o -> o.getLocationId().equals(d))
						.collect(Collectors.toList());
				locRes.setLocationId(filter.get(0).getLocationId().toString());
				locRes.setLocationName(filter.get(0).getLocationName());

				for (CommonDataDetails com : filter) {
					SectionDetailsRes secRes = new SectionDetailsRes();

					secRes.setRiskId(com.getRiskId().toString());
					secRes.setSectionId(com.getSectionId().toString());
					secRes.setSectionName(com.getSectionDesc());
//					secRes.setCount((com.getTotalNoOfEmployees()==null ||com.getTotalNoOfEmployees()==0)?com.getFidEmpCount().toString():com.getTotalNoOfEmployees().toString());
					secRes.setCount(com.getCount()==null ?"0":com.getCount().toString());
					secRes.setOccupationId(com.getOccupationType());
					secRes.setOccupationDesc(com.getOccupationDesc());
					secRes.setSumInsured(com.getSumInsured()==null?null:com.getSumInsured().toString());
						
						// Cover Details
						List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(com.getRiskId()))&&
								 o.getSectionId().toString().equals(com.getSectionId()) &&  o.getLocationId().equals(com.getLocationId()) && Objects.equals(o.getCoverId(),com.getCoverId()) ).collect(Collectors.toList());
						
						Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
						
						List<CoverRes>  coverListRes = getCoverDetails(groupByCover, covers);
						BigDecimal PremiumAfterDiscount = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumBeforeDiscount = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumExcluedTax = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumIncludedTax = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
						BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());

						// Response
						// Mot
//						 List<BrokerCommissionDetails> policylist = getPolicyName(com.getCompanyId() , com.getProductId().toString(), com.getCreatedBy(),com.getAgencyCode(),"99999");
//					
//						 Double commissionPercent = 0.0;
//							if(policylist.size()>0 && policylist!=null) {
//							
//						 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//							}
//							else {
//								commissionPercent =5.0;
//							}
						 String premiumFc = com.getOverallPremiumFc().toString();
						 String vatPremiumFc =	com.getOverallPremiumFc().toString();
						 BigDecimal commission=	new BigDecimal(premiumFc)
					 				.multiply(com.getCommissionPercentage()==null ?  new BigDecimal("0") : com.getCommissionPercentage())
			 						.divide(BigDecimal.valueOf(100D))
			 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
			 						.getPrecision(),RoundingMode.HALF_UP);

						
						dozerMapper.map(com, secRes);
						secRes.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
						secRes.setOverAllPremiumFc(com.getOverallPremiumFc()==null?0D:Double.valueOf(com.getOverallPremiumFc().toString()));
						secRes.setOverAllPremiumLc(com.getOverallPremiumLc()==null?0D:Double.valueOf(com.getOverallPremiumLc().toString()));
						secRes.setPremiumFc(com.getActualPremiumFc()==null?0D:Double.valueOf(com.getActualPremiumFc().toString()));
						secRes.setPremiumLc(com.getActualPremiumLc()==null?0D:Double.valueOf(com.getActualPremiumLc().toString()));
						secRes.setCommissionAmount(commission.toString()==null?"":commission.toString());
						secRes.setCommissionPercentage(com.getCommissionPercentage()==null?"" : com.getCommissionPercentage().toPlainString());
						secRes.setVatCommission(com.getVatCommission()==null?"" : com.getVatCommission().toPlainString());				
						secRes.setFinalizeYn(com.getFinalizeYn());
						secRes.setLocationId(com.getLocationId().toString());
						secRes.setLocationName(com.getLocationName());
						secRes.setOccupationTypeDesc(com.getOccupationDesc());				
						
						//get Section name Local from session master 
						List<ProductSectionMaster> PSM = productSectionMasterRepo.findBySectionName(com.getSectionDesc()!=null ? com.getSectionDesc().toString() : " ");
						
					 
						secRes.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
						secRes.setSectionName( com.getSectionDesc());
						secRes.setCodeDescLocal((PSM!=null && PSM.size()>0) ? PSM.get(0).getSectionNameLocal() : " ");
						secRes.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
						secRes.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
						secRes.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
						secRes.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
						secRes.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
						secRes.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
						secRes.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
						secRes.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());
						secRes.setCovers(coverListRes);
						sList.add(secRes);
				}
				locRes.setSectionDetails(sList);
				loctionList.add(locRes);
			}
			
			viewRes.setRiskDetails(commonResList);	
			viewRes.setDocumentDetails(documentDetails);
			viewRes.setLocationDetails(loctionList);			
		}catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	public List<Discount> getDiscountRates(List<PolicyCoverData> filterDiscountCover) {
		List<Discount> DiscountList = new  ArrayList<Discount>();
		try {
			for (PolicyCoverData disc :  filterDiscountCover ) {
				Discount discount = new Discount();
				discount.setDiscountAmount(disc.getPremiumIncludedTaxFc());
				discount.setDiscountCalcType(disc.getCalcType());
				discount.setDiscountId(disc.getDiscLoadId().toString());
				discount.setDiscountDesc(disc.getCoverName());	
				discount.setDiscountRate(disc.getRate()==null?"0.0" :disc.getRate().toString());
				discount.setFactorTypeId(disc.getFactorTypeId()==null?"" : disc.getFactorTypeId().toString());
				discount.setMaxAmount(disc.getMinimumPremium());
				discount.setSubCoverId(disc.getSubCoverId().toString());
				discount.setDiscountforId(disc.getDependentCoverId()==null?null:disc.getDependentCoverId().toString());
				
				DiscountList.add(discount);
				
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return DiscountList;
	}
	
	
	public List<Loading> getLodingCovers(List<PolicyCoverData> filterLodingCover) {
		List<Loading> LodingList = new  ArrayList<Loading>();
		try {
			for (PolicyCoverData lod :  filterLodingCover ) {
				Loading loding = new Loading();
				loding.setFactorTypeId(lod.getFactorTypeId()==null?null:lod.getFactorTypeId().toString());
				loding.setLoadingAmount(lod.getMinimumPremium());
				loding.setLoadingCalcType(lod.getCalcType());
				loding.setLoadingDesc(lod.getCoverName());
				loding.setLoadingforId(lod.getDependentCoverId()==null?null:lod.getDependentCoverId().toString());
				loding.setLoadingId(lod.getDiscLoadId()==null?null:lod.getDiscLoadId().toString());
				loding.setLoadingRate(lod.getRate()==null?null:lod.getRate().toString());
				loding.setMaxAmount(lod.getPremiumIncludedTaxFc());
				//loding.setSubCoverId(lod.getLodingSubcoverId()==null?null:lod.getLodingSubcoverId().toString());	
				LodingList.add(loding);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return LodingList;
	}
	
	public List<Tax> getTaxRates(List<PolicyCoverData> filterTaxCover) {
		List<Tax> TaxList = new  ArrayList<Tax>();
		try {
			for (PolicyCoverData tax :  filterTaxCover ) {
				Tax taxes = new Tax();
				taxes.setCalcType(tax.getCalcType());
				taxes.setIsTaxExempted(tax.getIsTaxExtempted());
				taxes.setTaxAmount(tax.getTaxAmount());
				taxes.setTaxDesc(tax.getTaxDesc());
				taxes.setTaxExemptCode(tax.getTaxExemptCode());
				taxes.setTaxExemptType(tax.getTaxExemptType());
				taxes.setTaxId(tax.getTaxId()==null?null:tax.getTaxId().toString()) ;
				taxes.setTaxRate(tax.getTaxRate()==null?null : Double.valueOf(tax.getTaxRate().toString()));
				TaxList.add(taxes);
			
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return TaxList;
	}


	@Override
	public List<Error> validateReferralStatus(AdminReferalStatusReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getAdminLoginId())) {
				errors.add(new Error("01","AdminLoginID","Please Enter Admin LoginId"));
			}
			
			if(StringUtils.isBlank(req.getStatus())) {
				errors.add(new Error("02","ReferralStatus","Please Select Referral Status"));
			} else if ( !(req.getStatus().equalsIgnoreCase("RP") || req.getStatus().equalsIgnoreCase("RA") || req.getStatus().equalsIgnoreCase("RR") ||  req.getStatus().equalsIgnoreCase("RE")||  req.getStatus().equalsIgnoreCase("REV"))) {
				errors.add(new Error("02","ReferralStatus","Please Select Valid Referral Status Accept/Reject/Pending/Re-Quote"));
			} else if ( req.getStatus().equalsIgnoreCase("RR")  ) {
				if(StringUtils.isBlank(req.getRejectReason())) {
					errors.add(new Error("03","Reject Reason","Please Enter Reject Reason"));
				}
				
			} 
			
			if(StringUtils.isNotBlank(req.getStatus()) && (req.getStatus().equalsIgnoreCase("RA") || req.getStatus().equalsIgnoreCase("RR") || req.getStatus().equalsIgnoreCase("RE")) &&  StringUtils.isBlank(req.getAdminRemarks())) {
				errors.add(new Error("03","Admin Remarks","Please Enter Admin Remarks"));
			}
			
			if(StringUtils.isNotBlank(req.getCommissionModifyYn()) && "Y".equalsIgnoreCase(req.getCommissionModifyYn()) ) {
				if(StringUtils.isBlank(req.getCommissionPercent() )) {
					errors.add(new Error("03","CommissionPercent","Please Enter Commission Percent"));
				} else if(! req.getCommissionPercent().matches("[0-9.]+") ) {
					errors.add(new Error("03","CommissionPercent","Please Enter Valid Commission Percent"));
				} else if(Double.valueOf(req.getCommissionPercent()) <1   ) {
					errors.add(new Error("03","CommissionPercent","Commission Percent Zero Not Allowed"));
				}
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return errors;
	}

	@Override
	public QuoteUpdateRes updateReferralStatus(AdminReferalStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		
		try {
			List<EserviceMotorDetails>    motorDatas = eserMotRepo.findByRequestReferenceNoOrderBySectionNameAsc(req.getRequestReferenceNo());
			List<EserviceTravelGroupDetails>    travelDatas = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(req.getRequestReferenceNo());
			List<EserviceBuildingDetails> buildDatas = eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			List<EserviceCommonDetails> findDatas = eserCommonRepo.findByRequestReferenceNoOrderBySectionNameAsc(req.getRequestReferenceNo());
			
			String companyId = motorDatas.size() > 0 ? motorDatas.get(0).getCompanyId() :	 travelDatas.size() > 0 ? travelDatas.get(0).getCompanyId()  
					 :  buildDatas.size() > 0 ? buildDatas.get(0).getCompanyId() :  findDatas.size() > 0 ? findDatas.get(0).getCompanyId() : "" ;
			CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , req.getProductId().toString());
		
			if (!"REV".equalsIgnoreCase(req.getStatus())) {		
				if(product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
				updateRes = travelReferalUpdate(req);
				//Mail Push Notification
				travelPushNotification(req);
				// Tracking Details
				trackingDetails(req, product.getMotorYn());
				//UWReferral History Table
				if ("RA".equalsIgnoreCase(req.getStatus())) {
					uwHisTable(req);
				}
				
			} else if(product.getMotorYn().equalsIgnoreCase("M") ) {
				updateRes = motorReferalUpdate(req);
				//Mail Push Notification
				motorPushNotification(req);
				//Tracking Details
				trackingDetails(req,product.getMotorYn() );
				//UWReferral History Table
				if ("RA".equalsIgnoreCase(req.getStatus())) {
					uwHisTable(req);
				}
			} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
					updateRes = buildingReferalUpdate(req);
					//Mail Push Notification
					buildingPushNotification(req);
					//Tracking Details
					
					//UWReferral History Table
				//	updateRes = commonReferalUpdate(req);
					commonPushNotification(req);

					//UWReferral History Table
					if ("RA".equalsIgnoreCase(req.getStatus())) {
						uwHisTable(req);
					}
					//Tracking Details
					trackingDetails(req,product.getMotorYn() );

				if ("RA".equalsIgnoreCase(req.getStatus())) {
					uwHisTable(req);
				}
			}  else {
				updateRes = commonReferalUpdate(req);
				commonPushNotification(req);
				//Tracking Details
				trackingDetails(req,product.getMotorYn() );
				//UWReferral History Table
				if ("RA".equalsIgnoreCase(req.getStatus())) {
					uwHisTable(req);
				}
			} 
			}else {
				if(StringUtils.isNotBlank(req.getAdminLoginId())) {
					List<UWReferralDetails> uwList = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
					
					uwList.forEach( o -> {
						if( o.getRequestReferenceNo().equals(req.getRequestReferenceNo()) )
							 o.setUwStatus("Y");
	
						
					});
				}
				updateRes.setResponse("Referal Reverted");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			}
		
		
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	//Save In UwReferral Histrory table
	private QuoteUpdateRes uwHisTable(AdminReferalStatusReq req) {
		QuoteUpdateRes res=new QuoteUpdateRes();
		DozerBeanMapper dozerMapper=new DozerBeanMapper();
	try {
		// Delete Old
		List<UWReferralDetails> uwReferral = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
		if(uwReferral.size()>0 && uwReferral !=null) {
			// Delete 
			Long uwReferralCount = uwReferralRepo.countByRequestReferenceNo(req.getRequestReferenceNo());
			if(uwReferralCount > 0 ) {
				uwReferralRepo.deleteByRequestReferenceNo(req.getRequestReferenceNo());
			}
			for(UWReferralDetails oldData:uwReferral) {
				// Save In History Table
				UWRefferralHistory uwRefHistorySave = new UWRefferralHistory(); 
				dozerMapper.map(oldData, uwRefHistorySave);
				uwRefHistorySave.setEntryDate(new Date());
				uwReferralHistRepo.saveAndFlush(uwRefHistorySave);
			}
		}
	} catch ( Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
	
	//Tracking Details
	private QuoteUpdateRes trackingDetails(AdminReferalStatusReq req , String motorYn) {
		QuoteUpdateRes res=new QuoteUpdateRes();
	try {
	
		List<TrackingDetailsSaveReq> trackingReq1 = new ArrayList<TrackingDetailsSaveReq>();
		if(motorYn.equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
			List<EserviceTravelDetails> cusRefNo = eserTraRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());
			for(EserviceTravelDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
		} else if(motorYn.equalsIgnoreCase("M") ) {
			List<EserviceMotorDetails> cusRefNo = eserMotRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			
			for(EserviceMotorDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
				
			} else if(motorYn.equalsIgnoreCase("A") ) {
			List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

			for(EserviceBuildingDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
		} else {
			List<EserviceCommonDetails> cusRefNo = eserCommonRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

			for(EserviceCommonDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
		}			
		
		trackingService.insertTrackingDetails(trackingReq1);
	} catch ( Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
	// --------------------------------------MOTOR UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
	private QuoteUpdateRes motorPushNotification(AdminReferalStatusReq req) {

		QuoteUpdateRes updateRes = new QuoteUpdateRes();
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
			cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());
			}

			// UnderWriter Info
			List<UWReferralDetails> uwReferral = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			List<String> loginIds=uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase()).collect(Collectors.toList());
					
			List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),loginIds);
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
					underWriterReq.setUwLoginId(underWriterData.get("uwloginId")==null?"":underWriterData.get("uwloginId").toString());
					underWriterReq.setUwuserType(underWriterData.get("uwuserType")==null?"":underWriterData.get("uwuserType").toString());
					underWriterReq.setUwsubuserType(underWriterData.get("uwsubuserType")==null?"":underWriterData.get("uwsubuserType").toString());
					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			//Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());
	
			
			if("RA".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Approved");
				n.setStatusMessage(req.getAdminRemarks());
			}else if("RP".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Pending");
				n.setStatusMessage(req.getAdminRemarks());				
			}else if("RR".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Rejected");
				n.setStatusMessage(StringUtils.isBlank(req.getAdminRemarks())?req.getRejectReason():req.getAdminRemarks());
			}else if("RE".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("ReQuote");
				n.setStatusMessage(req.getAdminRemarks());
			}
			//Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName("Motor");
			n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionName());
			
			n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
 
			// Calling pushNotification
			CommonRes res=notiService.pushNotification(n);
			if (res.getIsError()==null) {
				updateRes.setResponse("Pushed Successfuly");
				updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
				updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	
	}
	

	// --------------------------------------TRAVEL UPDATE REFERRAL STATUS----------------------------------------------------------------------//
	private QuoteUpdateRes travelPushNotification(AdminReferalStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
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
			cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

			}
			// UnderWriter Info
			List<UWReferralDetails> uwReferral = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			List<String> loginIds=uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase()).collect(Collectors.toList());
			List<Tuple> underWriterList = getUnderWriterDetails(cusRefNo.get(0).getProductId(),
					cusRefNo.get(0).getCompanyId(), cusRefNo.get(0).getBranchCode(), loginIds);
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
					underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());
					underWriterReq.setUwLoginId(underWriterData.get("uwloginId")==null?"":underWriterData.get("uwloginId").toString());
					underWriterReq.setUwuserType(underWriterData.get("uwuserType")==null?"":underWriterData.get("uwuserType").toString());
					underWriterReq.setUwsubuserType(underWriterData.get("uwsubuserType")==null?"":underWriterData.get("uwsubuserType").toString());

					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			// Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());

			if("RA".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Approved");
				n.setStatusMessage(req.getAdminRemarks());
			}else if("RP".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Pending");
				n.setStatusMessage(req.getAdminRemarks());				
			}else if("RR".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Rejected");
				n.setStatusMessage(StringUtils.isBlank(req.getAdminRemarks())?req.getRejectReason():req.getAdminRemarks());
			}else if("RE".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("ReQuote");
				n.setStatusMessage(req.getAdminRemarks());
			}
			// Common Info
			
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(5);
			n.setProductName("Travel");
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.setStatusMessage("");
			n.getTinyUrl();
			n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

			// Calling pushNotification
			CommonRes res = notiService.pushNotification(n);
			if (res.getIsError() == null) {
				updateRes.setResponse("Pushed Successfuly");
				updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
				updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	//----------------------------------------BUILDING UPDATE REFERRAL STATUS ------------------------------------------------------------------//
	private QuoteUpdateRes buildingPushNotification(AdminReferalStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());
			if (cusRefNo != null && !cusRefNo.isEmpty()) {
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
				if (loginInfo != null) {
					brokerReq.setBrokerCompanyName(
							loginInfo.getCompanyName() == null ? null : loginInfo.getCompanyName());
					brokerReq.setBrokerMailId(loginInfo.getUserMail() == null ? "" : loginInfo.getUserMail());
					brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc() == null ? null
							: Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
					brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo() == null ? BigDecimal.ZERO
							: new BigDecimal(loginInfo.getWhatsappNo().toString()));
					brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc() == null ? null
							: Integer.valueOf((loginInfo.getMobileCodeDesc())));
					brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile() == null ? BigDecimal.ZERO
							: new BigDecimal(loginInfo.getUserMobile()));
					brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = customerDetailsRepo
						.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if (customerData != null) {
					cusReq.setCustomerMailid(customerData.getEmail1());
					cusReq.setCustomerName(customerData.getClientName());
					cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
					cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
					cusReq.setCustomerMessengerCode(customerData.getWhatsappCodeDesc() != null ? Integer.valueOf(customerData.getWhatsappCodeDesc()): null);
					cusReq.setCustomerMessengerPhone(customerData.getWhatsappNo() != null ? new BigDecimal(customerData.getWhatsappNo()) : null);
					cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<UWReferralDetails> uwReferral = uwReferralRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				List<String> loginIds = uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase())
						.collect(Collectors.toList());

				List<Tuple> underWriterList = getUnderWriterDetails(cusRefNo.get(0).getProductId(),
						cusRefNo.get(0).getCompanyId(), cusRefNo.get(0).getBranchCode(), loginIds);
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? ""
								: underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc") == null ? null
								: Integer.valueOf(underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo") == null ? BigDecimal.ZERO
								: new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc") == null ? null
								: Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile") == null ? BigDecimal.ZERO
								: new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName") == null ? ""
								: underWriterData.get("userName").toString());
						underWriterReq.setUwLoginId(underWriterData.get("loginId") == null ? ""
								: underWriterData.get("loginId").toString());
						underWriterReq.setUwLoginId(underWriterData.get("uwloginId") == null ? ""
								: underWriterData.get("uwloginId").toString());
						underWriterReq.setUwuserType(underWriterData.get("uwuserType") == null ? ""
								: underWriterData.get("uwuserType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("uwsubuserType") == null ? ""
								: underWriterData.get("uwsubuserType").toString());

						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				// Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());

				if ("RA".equalsIgnoreCase(req.getStatus())) {
					n.setNotifTemplatename("Referral Approved");
					n.setStatusMessage(req.getAdminRemarks());
				} else if ("RP".equalsIgnoreCase(req.getStatus())) {
					n.setNotifTemplatename("Referral Pending");
					n.setStatusMessage(req.getAdminRemarks());
				} else if ("RR".equalsIgnoreCase(req.getStatus())) {
					n.setNotifTemplatename("Referral Rejected");
					n.setStatusMessage(
							StringUtils.isBlank(req.getAdminRemarks()) ? req.getRejectReason() : req.getAdminRemarks());
				} else if ("RE".equalsIgnoreCase(req.getStatus())) {
					n.setNotifTemplatename("ReQuote");
					n.setStatusMessage(req.getAdminRemarks());
				}

				// Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(cusRefNo.get(0).getProductId()));
				n.setProductName(cusRefNo.get(0).getProductDesc());
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionDesc());
				n.setStatusMessage("");
				n.setRefNo(req.getRequestReferenceNo());
				n.setBranchCode(cusRefNo.get(0).getBranchCode());
				n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res = notiService.pushNotification(n);
				if (res.getIsError() == null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	// -------------------------------------- COMMON UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
			private QuoteUpdateRes commonPushNotification(AdminReferalStatusReq req) {
				QuoteUpdateRes updateRes = new QuoteUpdateRes();
				try {
					List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
					
					if (cusRefNo != null && !cusRefNo.isEmpty()) {
						cusRefNo = cusRefNo.stream()
								.filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
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
						if (loginInfo != null) {
							brokerReq.setBrokerCompanyName(loginInfo.getCompanyName() == null ? loginInfo.getUserName()
									: loginInfo.getCompanyName());
							brokerReq.setBrokerMailId(loginInfo.getUserMail() == null ? "" : loginInfo.getUserMail());
							brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc() == null ? null
									: Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
							brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo() == null ? BigDecimal.ZERO
									: new BigDecimal(loginInfo.getWhatsappNo().toString()));
							brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc() == null ? null
									: Integer.valueOf((loginInfo.getMobileCodeDesc())));
							brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile() == null ? BigDecimal.ZERO
									: new BigDecimal(loginInfo.getUserMobile()));
							brokerReq.setBrokerName(loginInfo.getUserName());
						}
						// Customer Info
						EserviceCustomerDetails customerData = customerDetailsRepo
								.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
						Customer cusReq = new Customer();
						if (customerData != null) {
							cusReq.setCustomerMailid(customerData.getEmail1());
							cusReq.setCustomerName(customerData.getClientName());
							cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
							cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
							/**cusReq.setCustomerMessengerCode(customerData.getWhatsappCodeDesc() != null
									? Integer.valueOf(customerData.getWhatsappCodeDesc())
									: null);
							cusReq.setCustomerMessengerPhone(
									customerData.getWhatsappNo() != null ? new BigDecimal(customerData.getWhatsappNo())
											: null);**/
							
							
							String phone = customerData.getWhatsappNo();
							if (phone != null && phone.matches("\\d+")) {
								cusReq.setCustomerMessengerPhone(new BigDecimal(phone));
							} else {
								cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getMobileNo1()));
							}
							cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

						}

						// UnderWriter Info
						List<UWReferralDetails> uwReferral = uwReferralRepo
								.findByRequestReferenceNo(req.getRequestReferenceNo());
						List<String> loginIds = uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase())
								.collect(Collectors.toList());

						List<Tuple> underWriterList = getUnderWriterDetails(cusRefNo.get(0).getProductId(),
								cusRefNo.get(0).getCompanyId(), cusRefNo.get(0).getBranchCode(), loginIds);
						List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
						if (underWriterList != null) {
							for (Tuple underWriterData : underWriterList) {
								UnderWriter underWriterReq = new UnderWriter();
								underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? ""
										: underWriterData.get("userMail").toString());
								underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc") == null ? null
										: Integer.valueOf(underWriterData.get("whatsappCodeDesc").toString()));
								underWriterReq
										.setUwMessengerPhone(underWriterData.get("whatsappNo") == null ? BigDecimal.ZERO
												: new BigDecimal(underWriterData.get("whatsappNo").toString()));
								underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc") == null ? null
										: Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
								underWriterReq.setUwPhoneNo(underWriterData.get("userMobile") == null ? BigDecimal.ZERO
										: new BigDecimal(underWriterData.get("userMobile").toString()));
								underWriterReq.setUwName(underWriterData.get("userName") == null ? ""
										: underWriterData.get("userName").toString());
								underWriterReq.setUwLoginId(underWriterData.get("loginId") == null ? ""
										: underWriterData.get("loginId").toString());
								underWriterReq.setUwuserType(underWriterData.get("userType") == null ? ""
										: underWriterData.get("userType").toString());
								underWriterReq.setUwsubuserType(underWriterData.get("subUserType") == null ? ""
										: underWriterData.get("subUserType").toString());

								underWrite.add(underWriterReq);
							}
						}
						n.setUnderwriters(underWrite);
						// Company Info
						n.setCompanyid(cusRefNo.get(0).getCompanyId());
						n.setCompanyName(cusRefNo.get(0).getCompanyName());

						if ("RA".equalsIgnoreCase(req.getStatus())) {
							n.setNotifTemplatename("Referral Approved");
							n.setStatusMessage(req.getAdminRemarks());
						} else if ("RP".equalsIgnoreCase(req.getStatus())) {
							n.setNotifTemplatename("Referral Pending");
							n.setStatusMessage(req.getAdminRemarks());
						} else if ("RR".equalsIgnoreCase(req.getStatus())) {
							n.setNotifTemplatename("Referral Rejected");
							n.setStatusMessage(StringUtils.isBlank(req.getAdminRemarks()) ? req.getRejectReason()
									: req.getAdminRemarks());
						} else if ("RE".equalsIgnoreCase(req.getStatus())) {
							n.setNotifTemplatename("ReQuote");
							n.setStatusMessage(req.getAdminRemarks());
						}

						// Common Info
						n.setBroker(brokerReq);
						n.setCustomer(cusReq);
						n.setNotifcationDate(new Date());
						n.setNotifDescription("");
						n.setNotifPriority(0);
						n.setNotifPushedStatus(NotificationStatus.PENDING);
						n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
						n.setProductid(Integer.valueOf(req.getProductId()));
						// ProductMaster productData=
						// getByProductCode(Integer.valueOf(req.getProductId())) ;
						n.setProductName(cusRefNo.get(0).getProductDesc());
						n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())
								? cusRefNo.get(0).getRequestReferenceNo()
								: cusRefNo.get(0).getQuoteNo().toString());
						n.setSectionName(cusRefNo.get(0).getSectionName());
						// Referral Noti , referral app,recj
						n.setRefNo(req.getRequestReferenceNo());
						n.setBranchCode(cusRefNo.get(0).getBranchCode());
						n.setCompanyid(cusRefNo.get(0).getCompanyId());

						n.getTinyUrl();

						// Calling pushNotification
						CommonRes res = notiService.pushNotification(n);
//					if (res.getIsError()==null) {
//						updateRes.setResponse("Pushed Successfuly");
//						updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//						updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//						updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
						//
//					}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return updateRes;
			}

	
	private List<Tuple> getUnderWriterDetails(String productId,String companyId,String branchCode,List<String> loginId) {
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
					   u.get("whatsappCodeDesc").alias("whatsappCodeDesc"),u.get("whatsappNo").alias("whatsappNo"),
					   l.get("userType").alias("userType"),l.get("subUserType").alias("subUserType"));
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
			Predicate n10 =cb.lower(p.get("loginId")).in(loginId);
			/*Predicate n12 = cb.greaterThanOrEqualTo(p.get("sumInsuredEnd"), sumInsured) ;
			Predicate n10 = cb.lessThanOrEqualTo(p.get("sumInsuredStart"), sumInsured) ;
			Predicate n11 = cb.and(n12, n10);
			*/
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			Predicate n9 = cb.between(cb.literal(today),p.get("effectiveDateStart"), p.get("effectiveDateEnd"));
			query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10);
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
}
	//----------------------------------------MOTOR REFFERAL UPDATE ------------------------------------------------------------------//	
	public QuoteUpdateRes motorReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(req.getRequestReferenceNo() , "D");
			
			// Referal Approve & Create New Quote
			if (req.getStatus().equalsIgnoreCase("RA") ) {
				List<FactorRateRequestDetails> coverDatas = eserCovRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(req.getRequestReferenceNo(), 0 ,0,"Y");
				Integer riskId = motorDatas.get(0).getRiskId();	
				NewQuoteReq req2 = new NewQuoteReq();
				List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();
				
				for(EserviceMotorDetails mot : motorDatas ) {
					VehicleIdsReq vehDeh = new VehicleIdsReq();
					List<CoverIdsReq>  coverList = new ArrayList<CoverIdsReq>();
					List<FactorRateRequestDetails> filterCover = coverDatas.stream().filter( o -> o.getVehicleId().equals(mot.getRiskId()) ).collect(Collectors.toList());
					
					for (FactorRateRequestDetails cov :  filterCover ) {
						CoverIdsReq coverReq = new CoverIdsReq();
						if (cov.getCoverId().equals(cov.getSubCoverId())) {
							coverReq.setSubCoverId(null);
						} else {
							coverReq.setSubCoverId(cov.getSubCoverId().toString());
						}
						coverReq.setIsReferal(cov.getIsReferral());
						coverReq.setCoverId(cov.getCoverId());
						coverReq.setSubCoverYn(cov.getSubCoverYn());
						coverList.add(coverReq);
						
					}
					vehDeh.setCoverIdList(coverList);
					vehDeh.setVehicleId(mot.getRiskId());
					vehDeh.setSectionId(mot.getSectionId());
					vehDeh.setLocationId(mot.getLocationId());	
					vehicleIdsList.add(vehDeh);
				}
				
				req2.setAdminLoginId(req.getAdminLoginId());
				req2.setCreatedBy(req.getAdminLoginId());	
				req2.setProductId(req.getProductId());
				req2.setRequestReferenceNo(req.getRequestReferenceNo());
				req2.setVehicleIdsList(vehicleIdsList);
				req2.setManualReferralYn("N");
				req2.setReferralRemarks("");
				req2.setCommissionModifyYn(req.getCommissionModifyYn());
				req2.setCommissionPercent(req.getCommissionPercent());
				
				motorDatas.forEach(m -> {

					String key = m.getRequestReferenceNo() + "_" + m.getRiskId();

					req2.getMotorDetailsMap().computeIfAbsent(key, k -> new ArrayList<>()).add(m);
				});
				
				referralStatusUpdateService.updateMotorStatus(req.getRequestReferenceNo(), req);

				CommonRes	res = otSer.call_OT_Insert(req2);
				NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
				updateRes.setResponse("Referal Approved");
				updateRes.setQuoteNo(response.getQuoteNo());
				updateRes.setCustomerId(response.getCustomerId());
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				
				
			// Referal Pending
			} else if (req.getStatus().equalsIgnoreCase("RP")  ) {
				updateRes.setResponse("Referal Pending");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			// Referal Reject
			} else if (req.getStatus().equalsIgnoreCase("RR") ) {
				updateRes.setResponse("Referal Rejected");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			}  else if (req.getStatus().equalsIgnoreCase("RE") ) {
				updateRes.setResponse("Referal Re-Quote");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			} 
			
//			// Update Mot Status 
//			for ( EserviceMotorDetails mot : motorDatas ) {
//				mot.setStatus(req.getStatus());
//				mot.setAdminLoginId(req.getAdminLoginId());
//				mot.setAdminRemarks(req.getAdminRemarks());
//				mot.setRejectReason(req.getRejectReason());
//				mot.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent())?BigDecimal.ZERO :new BigDecimal( req.getCommissionPercent())) ;
//				mot.setUpdatedDate(new Date());
//				eserMotRepo.saveAndFlush(mot);
//				
//			}
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}

// Common Referal Update
	public QuoteUpdateRes commonReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			List<EserviceCommonDetails> commonDatas = eserCommonRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			if (commonDatas != null && !commonDatas.isEmpty()) { // Referal Approve & Create New Quote
				if (req.getStatus().equalsIgnoreCase("RA")) {
					List<FactorRateRequestDetails> coverDatas = eserCovRepo
							.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(
									req.getRequestReferenceNo(), 0, 0, "Y");

					NewQuoteReq req2 = new NewQuoteReq();
					List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();

					for (EserviceCommonDetails com : commonDatas) {
						VehicleIdsReq vehDeh = new VehicleIdsReq();
						List<CoverIdsReq> coverList = new ArrayList<CoverIdsReq>();
						List<FactorRateRequestDetails> filterCover = coverDatas.stream()
								.filter(o -> o.getVehicleId().equals(com.getRiskId())).collect(Collectors.toList());

						for (FactorRateRequestDetails cov : filterCover) {
							CoverIdsReq coverReq = new CoverIdsReq();
							if (cov.getCoverId().equals(cov.getSubCoverId())) {
								coverReq.setSubCoverId(null);
							} else {
								coverReq.setSubCoverId(cov.getSubCoverId().toString());
							}
							coverReq.setIsReferal(cov.getIsReferral());
							coverReq.setCoverId(cov.getCoverId());
							coverReq.setSubCoverYn(cov.getSubCoverYn());
							coverList.add(coverReq);

						}
						vehDeh.setCoverIdList(coverList);
						vehDeh.setVehicleId(com.getRiskId());
						vehDeh.setLocationId(com.getLocationId());
						vehDeh.setSectionId(com.getSectionId());
						vehicleIdsList.add(vehDeh);
					}

					req2.setAdminLoginId(req.getAdminLoginId());
					req2.setCreatedBy(req.getAdminLoginId());
					req2.setProductId(req.getProductId());
					req2.setRequestReferenceNo(req.getRequestReferenceNo());
					req2.setVehicleIdsList(vehicleIdsList);
					req2.setManualReferralYn("N");
					req2.setReferralRemarks("");
					req2.setCommissionModifyYn(req.getCommissionModifyYn());
					req2.setCommissionPercent(req.getCommissionPercent());

					referralStatusUpdateService.updateCommonStatus(req.getRequestReferenceNo(), req);

					CommonRes res = otSer.call_OT_Insert(req2);
					NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
					updateRes.setResponse("Referal Approved");
					updateRes.setQuoteNo(response.getQuoteNo());
					updateRes.setCustomerId(response.getCustomerId());
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());

					// Referal Pending
				} else if (req.getStatus().equalsIgnoreCase("RP")) {
					updateRes.setResponse("Referal Pending");
					updateRes.setQuoteNo("");
					updateRes.setCustomerId("");
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
					// Referal Reject
				} else if (req.getStatus().equalsIgnoreCase("RR")) {
					updateRes.setResponse("Referal Rejected");
					updateRes.setQuoteNo("");
					updateRes.setCustomerId("");
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				} else if (req.getStatus().equalsIgnoreCase("RE")) {
					updateRes.setResponse("Referal Re-Quote");
					updateRes.setQuoteNo("");
					updateRes.setCustomerId("");
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				}

//				// Update Mot Status
//				for (EserviceCommonDetails com : commonDatas) {
//					com.setStatus(req.getStatus());
//					com.setAdminLoginId(req.getAdminLoginId());
//					com.setAdminRemarks(req.getAdminRemarks());
//					com.setRejectReason(req.getRejectReason());
//					com.setUpdatedDate(new Date());
//					com.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent()) ? BigDecimal.ZERO
//							: new BigDecimal(req.getCommissionPercent()));
//					eserCommonRepo.saveAndFlush(com);
//
//				}
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	
//----------------------------------------- TRAVEL REFERRAL FLOW ------------------------------------------------------------------//
	public QuoteUpdateRes travelReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			EserviceTravelDetails travelData  = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			
			// Referal Approve & Create New Quote
			if (req.getStatus().equalsIgnoreCase("RA") ) {
				List<EserviceTravelGroupDetails> groupDatas = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(req.getRequestReferenceNo());
				List<FactorRateRequestDetails> coverDatas = eserCovRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(req.getRequestReferenceNo(), 0 ,0,"Y");
				
				
				NewQuoteReq req2 = new NewQuoteReq();
				List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();
				
				for(EserviceTravelGroupDetails tra : groupDatas ) {
					VehicleIdsReq vehDeh = new VehicleIdsReq();
					List<CoverIdsReq>  coverList = new ArrayList<CoverIdsReq>();
					List<FactorRateRequestDetails> filterCover = coverDatas.stream().filter( o -> o.getVehicleId().equals(tra.getGroupId())
							&& o.getProductId().toString().equals(travelData.getProductId())  && o.getSectionId().toString().equals(travelData.getSectionId())
							).collect(Collectors.toList());
					for (FactorRateRequestDetails cov :  filterCover ) {
						CoverIdsReq coverReq = new CoverIdsReq();
						if (cov.getCoverId().equals(cov.getSubCoverId())) {
							coverReq.setSubCoverId(null);
						} else {
							coverReq.setSubCoverId(cov.getSubCoverId().toString());
						}
						coverReq.setIsReferal(cov.getIsReferral());
						coverReq.setCoverId(cov.getCoverId());
						coverReq.setSubCoverYn(cov.getSubCoverYn());
						coverList.add(coverReq);
						
					}
					vehDeh.setCoverIdList(coverList);
					vehDeh.setVehicleId(tra.getGroupId());
					vehDeh.setSectionId(travelData.getSectionId() );
					vehicleIdsList.add(vehDeh);
				}
				
				req2.setAdminLoginId(req.getAdminLoginId());
				req2.setCreatedBy(req.getAdminLoginId());	
				req2.setProductId(req.getProductId());
				req2.setRequestReferenceNo(req.getRequestReferenceNo());
				req2.setVehicleIdsList(vehicleIdsList);
				req2.setManualReferralYn("N");
				req2.setReferralRemarks("");
				req2.setCommissionModifyYn(req.getCommissionModifyYn());
				req2.setCommissionPercent(req.getCommissionPercent());
				
				referralStatusUpdateService.updateTravelStatus(req.getRequestReferenceNo(), req);
				
				CommonRes	res = otSer.call_OT_Insert(req2);
				NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
				updateRes.setResponse("Referal Approved");
				updateRes.setQuoteNo(response.getQuoteNo());
				updateRes.setCustomerId(response.getCustomerId());
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				
				
			// Referal Pending
			} else if (req.getStatus().equalsIgnoreCase("RP")  ) {
				updateRes.setResponse("Referal Pending");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			// Referal Reject
			} else if (req.getStatus().equalsIgnoreCase("RR") ) {
				updateRes.setResponse("Referal Rejected");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			}  else if (req.getStatus().equalsIgnoreCase("RE") ) {
				updateRes.setResponse("Referal Re-Quote");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			} 
			
//			// Update Travel Status 
//			
//			travelData.setStatus(req.getStatus());
//			travelData.setAdminLoginId(req.getAdminLoginId());
//			travelData.setAdminRemarks(req.getAdminRemarks());
//			travelData.setRejectReason(req.getRejectReason());
//			travelData.setUpdatedDate(new Date());
//			travelData.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent())?BigDecimal.ZERO :new BigDecimal( req.getCommissionPercent())) ;
//			eserTraRepo.saveAndFlush(travelData);
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}

	public QuoteUpdateRes buildingReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			List<EserviceBuildingDetails> buildingDatas = eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			List<EserviceCommonDetails> commonDatasList = eserCommonRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			if (buildingDatas != null && !buildingDatas.isEmpty() || commonDatasList != null && !commonDatasList.isEmpty()) {
				List<EserviceSectionDetails> secDatas = eserSecRepo
						.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
				// Referal Approve & Create New Quote
				if (req.getStatus().equalsIgnoreCase("RA")) {
					List<FactorRateRequestDetails> coverDatas = eserCovRepo
							.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(
									req.getRequestReferenceNo(), 0, 0, "Y");

					NewQuoteReq req2 = new NewQuoteReq();
					List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();

					for (EserviceSectionDetails sec : secDatas) {

						if (sec.getProductType().equalsIgnoreCase("H")) {
							List<EserviceCommonDetails> commonDatas = eserCommonRepo
									.findByRequestReferenceNoAndSectionId(req.getRequestReferenceNo(),
											sec.getSectionId());
							VehicleIdsReq vehDeh = new VehicleIdsReq();
							List<CoverIdsReq> coverList = new ArrayList<CoverIdsReq>();

							for (EserviceCommonDetails com : commonDatas) {
								List<FactorRateRequestDetails> filterCover = coverDatas.stream()
										.filter(o -> o.getSectionId().equals(Integer.valueOf(sec.getSectionId()))
												&& o.getVehicleId().equals(com.getRiskId()))
										.collect(Collectors.toList());
								for (FactorRateRequestDetails cov : filterCover) {
									CoverIdsReq coverReq = new CoverIdsReq();
									if (cov.getCoverId().equals(cov.getSubCoverId())) {
										coverReq.setSubCoverId(null);
									} else {
										coverReq.setSubCoverId(cov.getSubCoverId().toString());
									}
									coverReq.setIsReferal(cov.getIsReferral());
									coverReq.setCoverId(cov.getCoverId());
									coverReq.setSubCoverYn(cov.getSubCoverYn());
									coverList.add(coverReq);

								}
								vehDeh.setCoverIdList(coverList);
								vehDeh.setVehicleId(com.getRiskId());
								vehDeh.setSectionId(sec.getSectionId());
								vehDeh.setLocationId(sec.getLocationId());
								vehicleIdsList.add(vehDeh);
							}

						}

						else {
							VehicleIdsReq vehDeh = new VehicleIdsReq();
							List<CoverIdsReq> coverList = new ArrayList<CoverIdsReq>();
							List<FactorRateRequestDetails> filterCover = coverDatas.stream()
									.filter(o -> o.getSectionId().equals(Integer.valueOf(sec.getSectionId()))
											&& o.getVehicleId().equals(sec.getRiskId()))
									.collect(Collectors.toList());
							for (FactorRateRequestDetails cov : filterCover) {
								CoverIdsReq coverReq = new CoverIdsReq();
								if (cov.getCoverId().equals(cov.getSubCoverId())) {
									coverReq.setSubCoverId(null);
								} else {
									coverReq.setSubCoverId(cov.getSubCoverId().toString());
								}
								coverReq.setIsReferal(cov.getIsReferral());
								coverReq.setCoverId(cov.getCoverId());
								coverReq.setSubCoverYn(cov.getSubCoverYn());
								coverList.add(coverReq);

							}
							vehDeh.setCoverIdList(coverList);
							vehDeh.setVehicleId(sec.getRiskId());
							vehDeh.setSectionId(sec.getSectionId());
							vehDeh.setLocationId(sec.getLocationId());
							vehicleIdsList.add(vehDeh);
						}

					}

					req2.setAdminLoginId(req.getAdminLoginId());
					req2.setCreatedBy(req.getAdminLoginId());
					req2.setProductId(req.getProductId());
					req2.setRequestReferenceNo(req.getRequestReferenceNo());
					List<VehicleIdsReq> uniqueVehicleList = vehicleIdsList.stream()
							.filter(distinctByKey(
									v -> Arrays.asList(v.getVehicleId(), v.getSectionId(), v.getLocationId())))
							.collect(Collectors.toList());
					req2.setVehicleIdsList(uniqueVehicleList);
					req2.setManualReferralYn("N");
					req2.setReferralRemarks("");
					req2.setCommissionModifyYn(req.getCommissionModifyYn());
					req2.setCommissionPercent(req.getCommissionPercent());
					
					referralStatusUpdateService.updateBuildingStatus(req.getRequestReferenceNo(), req);
					referralStatusUpdateService.updateCommonStatus(req.getRequestReferenceNo(), req);

					CommonRes res = otSer.call_OT_Insert(req2);
					NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
					updateRes.setResponse("Referal Approved");
					updateRes.setQuoteNo(response.getQuoteNo());
					updateRes.setCustomerId(response.getCustomerId());
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());

					// Referal Pending
				} else if (req.getStatus().equalsIgnoreCase("RP")) {
					updateRes.setResponse("Referal Pending");
					updateRes.setQuoteNo("");
					updateRes.setCustomerId("");
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
					// Referal Reject
				} else if (req.getStatus().equalsIgnoreCase("RR")) {
					updateRes.setResponse("Referal Rejected");
					updateRes.setQuoteNo("");
					updateRes.setCustomerId("");
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				} else if (req.getStatus().equalsIgnoreCase("RE")) {
					updateRes.setResponse("Referal Re-Quote");
					updateRes.setQuoteNo("");
					updateRes.setCustomerId("");
					updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				}

//				// Update Mot Status
//				if(buildingDatas != null && !buildingDatas.isEmpty() ) {
//				for (EserviceBuildingDetails build : buildingDatas) {
//					build.setStatus(req.getStatus());
//					build.setAdminLoginId(req.getAdminLoginId());
//					build.setAdminRemarks(req.getAdminRemarks());
//					build.setRejectReason(req.getRejectReason());
//					build.setUpdatedDate(new Date());
//					build.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent()) ? BigDecimal.ZERO
//							: new BigDecimal(req.getCommissionPercent()));
//					eserBuildRepo.saveAndFlush(build);
//
//				}
//				}
//				if(commonDatasList != null && !commonDatasList.isEmpty() )
//				{
//					for (EserviceCommonDetails com : commonDatasList) {
//						com.setStatus(req.getStatus());
//						com.setAdminLoginId(req.getAdminLoginId());
//						com.setAdminRemarks(req.getAdminRemarks());
//						com.setRejectReason(req.getRejectReason());
//						com.setUpdatedDate(new Date());
//						com.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent()) ? BigDecimal.ZERO
//								: new BigDecimal(req.getCommissionPercent()));
//						eserCommonRepo.saveAndFlush(com);
//
//					}
//				}
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}

	
	@Override
	public SuccessRes deleteOldQuoteRecord(DeleteOldQuoteReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String subUserType = "" ;
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , req.getProductId().toString());

			if(product.getMotorYn().equalsIgnoreCase("M") ) {
				
				Long motorInfo =  motorRepo.countByQuoteNo(req.getQuoteNo());
				if (motorInfo > 0  ) {
					motorRepo.deleteByQuoteNo(req.getQuoteNo());
				}
				
			} else if(product.getMotorYn().equalsIgnoreCase("H") &&  req.getProductId().equalsIgnoreCase(travelProductId) ) {
			//  // Delete Old Record
				Long travelInfo =  traPassRepo.countByQuoteNo(req.getQuoteNo());
				if (travelInfo > 0  ) {
					//Delete data
					List<TravelPassengerDetails> oldPassDatas = 	traPassRepo.findByQuoteNo(req.getQuoteNo());
					subUserType = oldPassDatas.get(0).getSubUserType();
					if (! subUserType.equalsIgnoreCase("b2c") ) {
						traPassRepo.deleteByQuoteNo(req.getQuoteNo());
						
						// Find History
						for (TravelPassengerDetails passData :  oldPassDatas) {
							Long travelHisInfo =  traPassHisRepo.countByQuoteNoAndPassengerId(req.getQuoteNo() ,passData.getPassengerId());
							if (travelHisInfo > 0 ) {
								//Delete data
								traPassHisRepo.deleteByQuoteNoAndPassengerId(req.getQuoteNo(),passData.getPassengerId());
								
							}
							// Save New 
							TravelPassengerHistory traHistorySave = new TravelPassengerHistory(); 
							dozerMapper.map(passData, traHistorySave);
							traHistorySave.setEntryDate(new Date());
							traPassHisRepo.saveAndFlush(traHistorySave);
						}
					}
				
				}	
			}
			
			// Remove Covers
			if (! subUserType.equalsIgnoreCase("b2c") ) { 
				Long coverInfo =  coverRepo.countByQuoteNo(req.getQuoteNo());
	 			if (coverInfo >0 ) {
	 				//Delete data
	 				coverRepo.deleteByQuoteNo(req.getQuoteNo() );
	 				
	 			}
			}
 			
			res.setResponse("Old Record Removed ");
			res.setSuccessId(req.getQuoteNo());
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public SectionWiseSumInsuredRes sectionWiseSuminsuredDetails(SectionSumInsuredGetReq req) {
		SectionWiseSumInsuredRes res = new SectionWiseSumInsuredRes();
		try {
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , req.getProductId().toString());

		
				 if(product.getMotorYn().equalsIgnoreCase("M")) {
					 List<MotorDataDetails> motList = motorRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(req.getQuoteNo(),"D");
					 Double accSuminsured = 0D;
					 for (MotorDataDetails o : motList ) {
						 accSuminsured = accSuminsured +  (o.getAcccessoriesSumInsured()==null ? 0D : o.getAcccessoriesSumInsured())  ;
					 }
					 MotorSuminsuredDetails motSum = new MotorSuminsuredDetails(); 
					 motSum.setAccessoriesSuminsured(accSuminsured.toString() );
					 motSum.setQuoteNo(motList.get(0).getQuoteNo());
					motSum.setCurrency(motList.get(0).getCurrency());
					 res.setProductSuminsuredDetails(motSum);
				}else  if(product.getMotorYn().equalsIgnoreCase("A") ) {
					BuildingSumInsuredDetails builSum  = buildingSuminsuredDetails(req);
					
					 if(req.getProductId().equalsIgnoreCase("19")  ) {
//						if( req.getSectionIds().contains("43")	||req.getSectionIds().contains("45") ) {
							CommonSumInsuredDetails Sum  = commonSuminsuredDetails(req);
							if(Sum!=null) {
								builSum.setEmpLiabilitySi(Sum.getEmpLiabilitySi());
								builSum.setFidEmpSi(Sum.getFidEmpSi());
							}
					//	}
					 }
						 
					 res.setProductSuminsuredDetails(builSum);	
				
				} else {
					
					CommonSumInsuredDetails Sum  = commonSuminsuredDetails(req);
					res.setProductSuminsuredDetails(Sum);
				}
			
			res.setQuoteNo(req.getQuoteNo());
			
			res.setRequestReferenceNo(req.getRequestReferenceNo());
			res.setProductId(req.getProductId());
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	public BuildingSumInsuredDetails buildingSuminsuredDetails(SectionSumInsuredGetReq req) {
		BuildingSumInsuredDetails res = new BuildingSumInsuredDetails();
		try {
			List<BuildingRiskDetails> buildings  = buildRiskRepo.findByQuoteNo(req.getQuoteNo());
			
			if(buildings.size()> 0 ) {
				// Default Entry
//				List<BuildingRiskDetails> filterDefaultBuilding = buildings.stream().filter( o -> "0".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
				if(buildings.size() > 0 ) {
					BuildingRiskDetails build = buildings.get(0);
					List<SectionDataDetails>   buildSections = secDataRepo.findByQuoteNoAndStatusNot(build.getQuoteNo(), "D");
					List<String> sectionIds = buildSections.stream().filter( o -> o.getRiskId().equals(1)).map(SectionDataDetails :: getSectionId ).collect(Collectors.toList());
					res.setCurrencyId(build.getCurrency());
					res.setRiskId(build.getRiskId().toString());
					res.setSectionId(sectionIds);
				}
				
				// (i) Asset Related Sections
				
				// Building
				List<BuildingRiskDetails> filterBuilding = buildings.stream().filter( o -> "1".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus())  ).collect(Collectors.toList());
				if(filterBuilding.size() > 0 ) {
					BuildingRiskDetails build = filterBuilding.get(0);
					res.setSumInsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
//					res.setWaterTankSi(build.getWaterTankSi() == null?"0" :build.getWaterTankSi().toPlainString());
//					res.setLossOfRentSi(build.getLossOfRentSi()== null?"0" :build.getLossOfRentSi().toPlainString());
//					res.setArchitectsSi(build.getArchitectsSi() == null?"0" :build.getArchitectsSi().toPlainString());
					
				} 
				
				// Content
				List<BuildingRiskDetails> filterContent = buildings.stream().filter( o -> "47".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus())  ).collect(Collectors.toList());
				if(filterContent.size() > 0 ) {
					BuildingRiskDetails build = filterContent.get(0);
					res.setSumInsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
//					res.setContentSuminsured(build.getContentSuminsured() == null?"0" :build.getContentSuminsured().toPlainString());
//					res.setEquipmentSi(build.getEquipmentSi() == null?"0" :build.getEquipmentSi().toPlainString());
//					res.setJewellerySi(build.getJewellerySi() == null?"0" :build.getJewellerySi().toPlainString());
//					res.setPaitingsSi(build.getPaitingsSi() == null?"0" :build.getPaitingsSi().toPlainString());
//					res.setCarpetsSi(build.getCarpetsSi() == null?"0" :build.getCarpetsSi().toPlainString());
//				    if(build.getContentSuminsured() == null || build.getContentSuminsured().compareTo(BigDecimal.ZERO) ==0 ) {
//				    	BigDecimal Si1 = build.getEquipmentSi() == null? BigDecimal.ZERO :build.getEquipmentSi();
//				    	BigDecimal Si2 = build.getJewellerySi() == null? BigDecimal.ZERO :build.getJewellerySi();
//				    	BigDecimal Si3 = build.getPaitingsSi() == null? BigDecimal.ZERO :build.getPaitingsSi();
//				    	BigDecimal Si4 = build.getCarpetsSi() == null? BigDecimal.ZERO :build.getCarpetsSi();
//				    	
//				    	BigDecimal totalContentSi = Si1.add(Si2).add(Si3).add(Si4);
//				    	res.setContentSuminsured(totalContentSi.toPlainString());
//				    }
					
				} 
				
				
				// All Risk , Plant All Risk , Business All Risk
				List<BuildingRiskDetails> filterAllRisk = buildings.stream().filter( o -> "3".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus()) ).collect(Collectors.toList());
				if(filterAllRisk.size() > 0 ) {
					BuildingRiskDetails build = filterAllRisk.get(0);
//					res.setAllriskSuminsured(build.getAllriskSuminsured() == null?"0" :build.getAllriskSuminsured().toPlainString());
//					res.setMiningPlantSi(build.getMiningPlantSi()== null?"0" :build.getMiningPlantSi().toPlainString());
//					res.setNonminingPlantSi(build.getNonminingPlantSi() == null?"0" :build.getNonminingPlantSi().toPlainString());
//					res.setGensetsSi(build.getGensetsSi() == null?"0" :build.getGensetsSi().toPlainString());
//					res.setEquipmentSi(build.getEquipmentSi() == null?"0" :build.getEquipmentSi().toPlainString());
//					Double MiningPlantSi = build.getMiningPlantSi() == null?0D :Double.valueOf(build.getMiningPlantSi().toPlainString()) ;
//					Double NonminingPlantSi = build.getNonminingPlantSi() == null?0D :Double.valueOf(build.getNonminingPlantSi().toPlainString()) ;
//					Double GensetsSi = build.getGensetsSi() == null?0D :Double.valueOf(build.getGensetsSi().toPlainString()) ;
//					Double plantAllRiskSi = MiningPlantSi +NonminingPlantSi  + GensetsSi ;
//					res.setPlantAllriskSi( plantAllRiskSi==null ? "" :plantAllRiskSi.toString());
					res.setSumInsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
				} 
				
				// Accidental Damage
				List<BuildingRiskDetails> filterAccidental = buildings.stream().filter( o -> "56".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus()) ).collect(Collectors.toList());
				if(filterAccidental.size() > 0 ) {
					BuildingRiskDetails build = filterAccidental.get(0);
					//res.setContentSuminsured(build.getContentSuminsured() == null?"0" :build.getContentSuminsured().toPlainString());
					
				}
				
				// Burgalry
				List<BuildingRiskDetails> filterBurglary = buildings.stream().filter( o -> "52".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus()) ).collect(Collectors.toList());
				if(filterBurglary.size() > 0 ) {
					BuildingRiskDetails build = filterBurglary.get(0);
				//	res.setStockInTradeSi(build.getStockInTradeSi()== null?"0" :build.getStockInTradeSi().toPlainString());;
				//	res.setGoodsSi(build.getGoodsSi() == null?"0" :build.getGoodsSi().toPlainString());	
				//	res.setFurnitureSi(build.getFurnitureSi() == null?"0" :build.getFurnitureSi().toPlainString());
				//	res.setCashValueablesSi(build.getCashValueablesSi() == null?"0" :build.getCashValueablesSi().toPlainString());
				//	res.setApplianceSi(build.getApplianceSi() == null?"0" :build.getApplianceSi().toPlainString());;
				//	res.setGoodsSinglecarrySuminsured(build.getGoodsSinglecarrySuminsured() == null?"0" :build.getGoodsSinglecarrySuminsured().toPlainString());
				//	res.setGoodsTurnoverSuminsured(build.getGoodsTurnoverSuminsured() == null?"0" :build.getGoodsTurnoverSuminsured().toPlainString());
					
				}
				
				// Fire And Material Damage
				List<BuildingRiskDetails> filterFire = buildings.stream().filter( o -> "40".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus()) ).collect(Collectors.toList());
				if(filterFire.size() > 0 ) {
					BuildingRiskDetails build = filterFire.get(0);
//					res.setStockInTradeSi(build.getStockInTradeSi()== null?"0" :build.getStockInTradeSi().toPlainString());
//					res.setBuildingSuminsured(build.getBuildingSuminsured() == null?"0" :build.getBuildingSuminsured().toPlainString());	
//					res.setFireEquipSi(build.getEquipmentSi() == null?"0" :build.getEquipmentSi().toPlainString());
//					res.setFirePlantSi(build.getFirePlantSi() == null?"0" :build.getFirePlantSi().toPlainString());
					res.setSumInsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
					
				}
				
				// Electronic Equipment
				List<BuildingRiskDetails> filterElecEquip = buildings.stream().filter( o -> "39".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus()) ).collect(Collectors.toList());
				if(filterElecEquip.size() > 0 ) {
					BuildingRiskDetails build = filterElecEquip.get(0);
//					res.setElecEquipSuminsured(build.getElecEquipSuminsured() == null?"0" :build.getElecEquipSuminsured().toPlainString());
					res.setSumInsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
				}
				
				// Money
//				List<BuildingRiskDetails> filterMoney = buildings.stream().filter( o -> "42".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus()) ).collect(Collectors.toList());
//				if(filterMoney.size() > 0 ) {
//					BuildingRiskDetails build = filterMoney.get(0);
//					res.setMoneyAnnualEstimate(build.getMoneyAnnualEstimate()== null?"0" : build.getMoneyAnnualEstimate().toPlainString());
//					res.setMoneyCollector(build.getMoneyCollector()== null?"0" : build.getMoneyCollector().toPlainString() );
//					res.setMoneyDirectorResidence(build.getMoneyDirectorResidence()== null?"0" : build.getMoneyDirectorResidence().toPlainString() );
//					res.setMoneyOutofSafe(build.getMoneyOutofSafe()== null?"0" : build.getMoneyOutofSafe().toPlainString() );
//					res.setMoneySafeLimit(build.getMoneySafeLimit()== null?"0" : build.getMoneySafeLimit().toPlainString() );
//					res.setMoneyMajorLoss(build.getMoneyMajorLoss() == null?"0" : build.getMoneyMajorLoss().toPlainString() );
//					
//				}
				
				// Machinery
				List<BuildingRiskDetails> filterMachienry = buildings.stream().filter( o -> "41".equalsIgnoreCase(o.getSectionId()) && ! "D".equalsIgnoreCase(o.getStatus())  ).collect(Collectors.toList());
				if(filterMachienry.size() > 0 ) {
					BuildingRiskDetails build = filterMachienry.get(0);
//					Double ElecMachinesSi = build.getElecMachinesSi() == null?0D :Double.valueOf(build.getElecMachinesSi().toPlainString());
//					Double BoilerPlantsSi = build.getBoilerPlantsSi() == null?0D :Double.valueOf(build.getBoilerPlantsSi().toPlainString()) ;
//					Double EquipmentSi = build.getEquipmentSi() == null?0D :Double.valueOf(build.getEquipmentSi().toPlainString()) ;
//					Double GeneralMachineSi = build.getGeneralMachineSi() == null?0D :Double.valueOf(build.getGeneralMachineSi().toPlainString()) ;
//					Double MachineEquipSi = build.getMachineEquipSi() == null?0D :Double.valueOf(build.getMachineEquipSi().toPlainString()) ;
//					Double ManuUnitsSi = build.getManuUnitsSi() == null?0D :Double.valueOf(build.getManuUnitsSi().toPlainString()) ;
//					Double plantSi = build.getPowerPlantSi() == null?0D :Double.valueOf(build.getPowerPlantSi().toPlainString()) ;
//					Double machineSi = build.getMachinerySi() == null?0D :Double.valueOf(build.getMachinerySi().toPlainString()) ;
//					Double machinerySi = ElecMachinesSi + BoilerPlantsSi + EquipmentSi + GeneralMachineSi + MachineEquipSi + ManuUnitsSi + plantSi + machineSi ;
//							
//					res.setMachinerySi( machinerySi==null ? "" :machinerySi.toString());
					res.setSumInsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
				}
				
				// (ii)  Human Related Sections
				List<CommonDataDetails> humanDatas = commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
				
				// Personal Accident
				List<CommonDataDetails> filterPacc = humanDatas.stream().filter( o -> ! "D".equalsIgnoreCase(o.getStatus()) 
						&& o.getSectionId().equals("35") ).collect(Collectors.toList());
				if( filterPacc.size() > 0 ) {
					CommonDataDetails pacc = filterPacc.get(0);
					res.setOccupationType( pacc.getOccupationType());
					res.setOccupationTypeDesc(pacc.getOccupationDesc() );
					res.setPersonalAccSuminsured(pacc.getSumInsured()==null ? "" : pacc.getSumInsured().toPlainString() );
					res.setCount( pacc.getCount()==null ?  "" :pacc.getCount().toString());
					Double sumInsured = filterPacc.stream().filter( o -> o.getSumInsured() != null ).mapToDouble(o -> Double.valueOf(o.getSumInsured().toPlainString() ) ).sum() ;
					res.setSumInsured(sumInsured==null ? "" : sumInsured.toString());
					
				}
				
				// Personal Libaility
				List<CommonDataDetails> filterlialbity = humanDatas.stream().filter( o -> ! "D".equalsIgnoreCase(o.getStatus()) 
						&& o.getSectionId().equals("36") ).collect(Collectors.toList());
				if( filterlialbity.size() > 0 ) {
					CommonDataDetails liability = filterlialbity.get(0);
//					res.setLiabilityOccupationId(liability.getOccupationType());
					res.setLiabilityOccupationDesc(liability.getOccupationDesc());
					res.setPersonalIntermediarySuminsured(liability.getSumInsured()==null ? "" : liability.getSumInsured().toPlainString() );
					
				}
				
				// Employer Liablity
//				Double empliabiltiySi = humanDatas.stream().filter( o ->  o.getStatus().equalsIgnoreCase("D") &&  o.getSectionId().equalsIgnoreCase("45") && o.getEmpLiabilitySi() != null ).mapToDouble(o -> Double.valueOf(o.getEmpLiabilitySi().toPlainString() ) ).sum() ;
//				res.setEmpLiabilitySi(empliabiltiySi==null ? "" : empliabiltiySi.toString());
				
				// Fideltiy
//				Double fidEmpSi = humanDatas.stream().filter( o ->  o.getStatus().equalsIgnoreCase("D") && o.getSectionId().equalsIgnoreCase("43") &&  o.getFidEmpSi() != null ).mapToDouble(o -> Double.valueOf(o.getFidEmpSi().toPlainString() ) ).sum() ;
//				res.setFidEmpSi(fidEmpSi==null ? "" :fidEmpSi.toString());
				
				// Public Liability
//				Double liabiltiySi = humanDatas.stream().filter( o ->  o.getStatus().equalsIgnoreCase("D") && o.getSectionId().equalsIgnoreCase("54") && o.getLiabilitySi()!= null ).mapToDouble(o -> Double.valueOf(o.getLiabilitySi().toPlainString() ) ).sum() ;
//				res.setLiabilitySi(liabiltiySi==null ? "" : liabiltiySi.toString());
//				
				 List<OccupationReqClass> occupation = new ArrayList<OccupationReqClass>(); 
			}
			
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
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
	public CommonSumInsuredDetails commonSuminsuredDetails(SectionSumInsuredGetReq req) {
		CommonSumInsuredDetails res = new CommonSumInsuredDetails();
		List<CommonDataDetails> paccDatas = new ArrayList<CommonDataDetails>();
		try {
			paccDatas = commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			if( paccDatas.size() > 0 ){
				CommonDataDetails pacc = paccDatas.get(0) ;
				List<EserviceSectionDetails>   sections = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(pacc.getRequestReferenceNo());	
				List<String> sectionIds = sections.stream().map(EserviceSectionDetails :: getSectionId ).collect(Collectors.toList());
				
				res.setCurrencyId(pacc.getCurrency());
				res.setRiskId(pacc.getRiskId().toString());
				// Personal Accident
				List<CommonDataDetails> filterPacc = paccDatas.stream().filter( o -> ! "D".equalsIgnoreCase(o.getStatus()) 
						&& o.getSectionId().equals("35") ).collect(Collectors.toList());
				
				if( filterPacc.size() > 0 ) {
					pacc = filterPacc.get(0);
					res.setOccupationType( pacc.getOccupationType());
					res.setOccupationTypeDesc(pacc.getOccupationDesc() );
					res.setPersonalAccSuminsured(pacc.getSumInsured()==null ? "" : pacc.getSumInsured().toPlainString() );
					res.setCount( pacc.getCount()==null ?  "" :pacc.getCount().toString());
					
					
				}
				
				// Personal Libaility
				List<CommonDataDetails> filterlialbity = paccDatas.stream().filter( o -> ! "D".equalsIgnoreCase(o.getStatus()) 
						&& o.getSectionId().equals("36") ).collect(Collectors.toList());
				if( filterlialbity.size() > 0 ) {
					CommonDataDetails liability = filterlialbity.get(0);
					res.setLiabilityOccupationId(liability.getOccupationType());
					res.setLiabilityOccupationDesc(liability.getOccupationDesc());
					res.setPersonalIntermediarySuminsured(liability.getSumInsured()==null ? "" : liability.getSumInsured().toPlainString() );
					
				}
	
				Double sumInsured = paccDatas.stream().filter( o -> (! o.getStatus().equalsIgnoreCase("D")) &&  o.getSumInsured() != null ).mapToDouble(o -> Double.valueOf(o.getSumInsured().toPlainString() ) ).sum() ;
				res.setSumInsured(sumInsured.toString());
				
//				Double empliabiltiySi = paccDatas.stream().filter( o -> (! o.getStatus().equalsIgnoreCase("D")) &&  o.getEmpLiabilitySi() != null ).mapToDouble(o -> Double.valueOf(o.getEmpLiabilitySi().toPlainString() ) ).sum() ;
//				Double fidEmpSi = paccDatas.stream().filter( o -> (! o.getStatus().equalsIgnoreCase("D")) &&   o.getFidEmpSi() != null ).mapToDouble(o -> Double.valueOf(o.getFidEmpSi().toPlainString() ) ).sum() ;
//				Double liabiltiySi = paccDatas.stream().filter( o -> (! o.getStatus().equalsIgnoreCase("D")) &&   o.getLiabilitySi()!= null ).mapToDouble(o -> Double.valueOf(o.getLiabilitySi().toPlainString() ) ).sum() ;
				
//				res.setEmpLiabilitySi(empliabiltiySi.toString());
//				res.setFidEmpSi(fidEmpSi.toString());
//				res.setLiabilitySi(liabiltiySi.toString());
				
				res.setSectionId(sectionIds);
			}
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public List<SectionCoverMaster>  getSectionCovers(String companyId , String productId ,List<String> sectionIds) {
		List<SectionCoverMaster> list = new ArrayList<SectionCoverMaster>();
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
			CriteriaQuery<SectionCoverMaster> query = cb.createQuery(SectionCoverMaster.class);
			
			// Find All
			Root<SectionCoverMaster> c = query.from(SectionCoverMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("coverName")));

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<SectionCoverMaster> ocpm1 = effectiveDate.from(SectionCoverMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			jakarta.persistence.criteria.Predicate a2 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			jakarta.persistence.criteria.Predicate a4 = cb.equal(c.get("coverId"), ocpm1.get("coverId"));
			jakarta.persistence.criteria.Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4, a5);
			// Effective Date End
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<SectionCoverMaster> ocpm2 = effectiveDate2.from(SectionCoverMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a6 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a7 = cb.equal(c.get("coverId"), ocpm2.get("coverId"));
			Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId") );
			Predicate a9 = cb.equal(c.get("productId"), ocpm2.get("productId") );
			Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6,a7,a8,a9,a10);
					
			//In 
			Expression<String>e0=c.get("sectionId");
			// Where
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"), companyId);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), productId);
			jakarta.persistence.criteria.Predicate n5 =e0.in(sectionIds)	;
			jakarta.persistence.criteria.Predicate n6 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			query.where(n1, n2, n3, n4, n5,n6).orderBy(orderList);

			// Get Result
			TypedQuery<SectionCoverMaster> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public List<Error> validateQuoteStatus(UpdateQuoteStatusReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getLoginId())) {
				errors.add(new Error("01","LoginID","Please Enter LoginId"));
			}
			
			if(StringUtils.isBlank(req.getStatus())) {
				errors.add(new Error("02","QuoteStatus","Please Select Quote Status"));
			}
			
			if(StringUtils.isNotBlank(req.getStatus()) && req.getStatus().equalsIgnoreCase("R") && StringUtils.isBlank(req.getRejectReason())  )  {
				errors.add(new Error("03","Reject Reason","Please Enter Reject Reason"));
			} else if(StringUtils.isNotBlank(req.getRequestReferenceNo()) )  {
				List<HomePositionMaster> homeDatas = homeRepo.findByRequestReferenceNo(req.getRequestReferenceNo());  
				if(homeDatas.size() >0 ) {
					List<HomePositionMaster> filterDatas = homeDatas.stream().filter( o ->   StringUtils.isNotBlank(o.getPolicyNo())).collect(Collectors.toList());
					if(filterDatas.size() >0 ) {
						errors.add(new Error("03","Status Update","Can not  Update Status. Already Policy Converted For This Quote. PolicyNo : " + filterDatas.get(0).getPolicyNo() )) ;	
					}
				}
			}
			
			if(StringUtils.isBlank(req.getRequestReferenceNo()) )  {
				errors.add(new Error("03","RequestReferenceNo","Please Enter RequestReferenceNo"));
			} 
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return errors;
	}

	@Override
	@Transactional
	public QuoteUpdateRes updateQuoteStatus(UpdateQuoteStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			List<EserviceMotorDetails>    motorDatas = eserMotRepo.findByRequestReferenceNoOrderBySectionNameAsc(req.getRequestReferenceNo());
			List<EserviceLifeDetails>    lifeDatas = lifeRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			List<EserviceTravelGroupDetails>    travelDatas = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(req.getRequestReferenceNo());
			List<EserviceBuildingDetails> buildDatas = eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			List<EserviceCommonDetails> findDatas = eserCommonRepo.findByRequestReferenceNoOrderBySectionNameAsc(req.getRequestReferenceNo());
			
			String companyId = motorDatas.size() > 0 ? motorDatas.get(0).getCompanyId() :	 travelDatas.size() > 0 ? travelDatas.get(0).getCompanyId()  
					 :  buildDatas.size() > 0 ? buildDatas.get(0).getCompanyId() :  findDatas.size() > 0 ? findDatas.get(0).getCompanyId() :
						 lifeDatas.size() > 0 ? lifeDatas.get(0).getCompanyId() :  "" ;
			CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , req.getProductId().toString());
		
			if(product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
				updateRes = updateTravelPorductStatus(req);
				// Notification Trigger
				travelNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req,product.getMotorYn());
				
			} else if(product.getMotorYn().equalsIgnoreCase("M") ) {
				updateRes = updateMotorPorductStatus(req) ;
				// Notification Trigger
				motorNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req,product.getMotorYn());
				
			} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
				updateRes = updateBuildingPorductStatus(req);
				// Notification Trigger
				buildingNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req,product.getMotorYn());
				
			} else if(product.getMotorYn().equalsIgnoreCase("L") ) { //Life
				updateRes = updateLifePorductStatus(req) ;
				// Notification Trigger
				//lifeNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req,product.getMotorYn());
				
			}
			else {
				updateRes = updateCommonPorductStatus(req);
				// Notification Trigger
				commonNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req,product.getMotorYn());
			}
			
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes ;
	}
	
	private QuoteUpdateRes updateLifePorductStatus(UpdateQuoteStatusReq req) {

		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Motor Update
   		   {
   		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceLifeDetails> update = cb.createCriteriaUpdate(EserviceLifeDetails.class);
				// set the root class
				Root<EserviceLifeDetails> m = update.from(EserviceLifeDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
   		   }
   		   // Nominee Details Update
   		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<EserviceNomineeDetails> update = cb.createCriteriaUpdate(EserviceNomineeDetails.class);
					// set the root class
					Root<EserviceNomineeDetails> m = update.from(EserviceNomineeDetails.class);
					// set update and where clause
					update.set("rejectReason", req.getRejectReason());
					update.set("status", req.getStatus());
					update.set("updatedDate",  new Date());
					update.set("updatedBy",req.getLoginId() );
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
   		   }
   		   
   		  // Home Position Master Update
   		   {
   			    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
   		   }
   		 
   		   // Resposne 
   		   res.setResponse("Status Updated Successfully");
   		   res.setRequestReferenceNo(req.getRequestReferenceNo());
   		   
   		   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 
	}

		//Tracking Details
		private QuoteUpdateRes trackingDetailsupdateQuoteStatus(UpdateQuoteStatusReq req , String motorYn) {
			QuoteUpdateRes res=new QuoteUpdateRes();
		try {
			
			List<TrackingDetailsSaveReq> trackingReq1 = new ArrayList<TrackingDetailsSaveReq>();
			if(motorYn.equalsIgnoreCase("H") &&  req.getProductId().equalsIgnoreCase(travelProductId)) {
				List<EserviceTravelDetails> cusRefNo = eserTraRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceTravelDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
			} else if(motorYn.equalsIgnoreCase("M")) {
				List<EserviceMotorDetails> cusRefNo = eserMotRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				for(EserviceMotorDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
					
				} else if(motorYn.equalsIgnoreCase("A")) {
				List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceBuildingDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
			} else {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceCommonDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
			}			
			
			trackingService.insertTrackingDetails(trackingReq1);

		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	 public  QuoteUpdateRes updateMotorPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Motor Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceMotorDetails> update = cb.createCriteriaUpdate(EserviceMotorDetails.class);
				// set the root class
				Root<EserviceMotorDetails> m = update.from(EserviceMotorDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
    		   // Motor Data Details Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<MotorDataDetails> update = cb.createCriteriaUpdate(MotorDataDetails.class);
					// set the root class
					Root<MotorDataDetails> m = update.from(MotorDataDetails.class);
					// set update and where clause
					update.set("rejectReason", req.getRejectReason());
					update.set("status", req.getStatus());
					update.set("updatedDate",  new Date());
					update.set("updatedBy",req.getLoginId() );
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		  // Home Position Master Update
    		   {
    			    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		 
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
    		   
    		   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	 
	 private QuoteUpdateRes motorNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
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
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<UWReferralDetails> uwReferral = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
				List<String> loginIds=uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase()).collect(Collectors.toList());
				
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),loginIds);
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
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());

						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}else {
					n.setNotifTemplatename("Quote Action");
					
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName("Motor");
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.setStatusMessage("");
				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}

	 private QuoteUpdateRes travelNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceTravelDetails> cusRefNo = eserTraRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
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
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<UWReferralDetails> uwReferral = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
				List<String> loginIds=uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase()).collect(Collectors.toList());
				
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),loginIds);
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
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName("Travel");
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setStatusMessage("");
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
	 private QuoteUpdateRes buildingNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
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
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<UWReferralDetails> uwReferral = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
				List<String> loginIds=uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase()).collect(Collectors.toList());
								List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),loginIds);
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
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName("Building");
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionDesc());
				n.setStatusMessage("");
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
	 private QuoteUpdateRes commonNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
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
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<UWReferralDetails> uwReferral = uwReferralRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
				List<String> loginIds=uwReferral.stream().map(i -> i.getUwLoginId().toLowerCase()).collect(Collectors.toList());				
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),loginIds);
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
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName(cusRefNo.get(0).getProductDesc());
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setStatusMessage("");
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}


	 public  QuoteUpdateRes updateTravelPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Travel Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceTravelDetails> update = cb.createCriteriaUpdate(EserviceTravelDetails.class);
				// set the root class
				Root<EserviceTravelDetails> m = update.from(EserviceTravelDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
    		   // Travel Data Details Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<TravelPassengerDetails> update = cb.createCriteriaUpdate(TravelPassengerDetails.class);
					// set the root class
					Root<TravelPassengerDetails> m = update.from(TravelPassengerDetails.class);
					// set update and where clause
					update.set("rejectReason", req.getRejectReason());
					update.set("status", req.getStatus());
					update.set("updatedDate",  new Date());
					update.set("updatedBy",req.getLoginId() );
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		  // Home Position Master Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
    		   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	 
	 public  QuoteUpdateRes updateBuildingPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Building Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceBuildingDetails> update = cb.createCriteriaUpdate(EserviceBuildingDetails.class);
				// set the root class
				Root<EserviceBuildingDetails> m = update.from(EserviceBuildingDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
//    		   // Motor Data Details Update
//    		   {
//	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
//					// create update
//					CriteriaUpdate<MotorDataDetails> update = cb.createCriteriaUpdate(MotorDataDetails.class);
//					// set the root class
//					Root<MotorDataDetails> m = update.from(MotorDataDetails.class);
//					// set update and where clause
//					update.set("rejectReason", req.getRejectReason());
//					update.set("status", req.getStatus());
//					update.set("updatedDate", req.getLoginId());
//					update.set("updatedBy", new Date() );
//					
//					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
//					update.where(n1);
//					// perform update
//					em.createQuery(update).executeUpdate();
//					
//    		   }
    		   
    		  // Home Position Master Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
	    	   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	 
	 public  QuoteUpdateRes updateCommonPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Common Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceCommonDetails> update = cb.createCriteriaUpdate(EserviceCommonDetails.class);
				// set the root class
				Root<EserviceCommonDetails> m = update.from(EserviceCommonDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
    		   // Common Data Details Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<CommonDataDetails> update = cb.createCriteriaUpdate(CommonDataDetails.class);
					// set the root class
					Root<CommonDataDetails> m = update.from(CommonDataDetails.class);
					// set update and where clause
					update.set("rejectReason", req.getRejectReason());
					update.set("status", req.getStatus());
					update.set("updatedDate",  new Date());
					update.set("updatedBy",req.getLoginId() );
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		  // Home Position Master Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
    		   
	         } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	 public ProductMaster getByProductCode(Integer productId) {
			ProductMaster res = new ProductMaster();
			try {
				Date today  =new Date();
				Calendar cal = new GregorianCalendar(); 
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today   = cal.getTime();
				
				List<ProductMaster> list = new ArrayList<ProductMaster>();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ProductMaster> query = cb.createQuery(ProductMaster.class);
		
				// Find All
				Root<ProductMaster> b = query.from(ProductMaster.class);
		
				// Select
				query.select(b);
		
				// Amend ID Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<ProductMaster> ocpm1 = amendId.from(ProductMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				amendId.where(a1,a2);
		
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
		
				// Where
				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(b.get("productId"), productId);
		
				query.where(n1,n2).orderBy(orderList);
		
				// Get Result
				TypedQuery<ProductMaster> result = em.createQuery(query);
				list = result.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getProductId()))).collect(Collectors.toList());
				list.sort(Comparator.comparing(ProductMaster :: getProductName ));
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}

	@Override
	public List<String> validateNewQuoteDetails(NewQuoteReq req) {
		List<String> error = new ArrayList<String>();

		try {
			if(StringUtils.isNotBlank(req.getManualReferralYn()) && req.getManualReferralYn().equalsIgnoreCase("Y")) {
				if( StringUtils.isBlank(req.getReferralRemarks()) ) {
					error.add("1147");
//					error.add(new Error("01", "ManualReferralRemarks", "Please Enter Manual Referral Remarks "));
				} else if(req.getReferralRemarks().length() > 1000 ) {
					error.add("1148");
//					error.add(new Error("01", "ManualReferralRemarks", "Manual Referral Remarks Less Then 200 Charecter Only Allowed"));
				}
					
			}	
			
			if(StringUtils.isBlank(req.getRequestReferenceNo() ) ){
				error.add("1149");
//				error.add(new Error("01", "RequestReferenceNo", "Please Enter Request Reference No"));
			}
			if(StringUtils.isBlank(req.getProductId() ) ){
				error.add("1150");
//				error.add(new Error("01", "ProductId", "Please Enter Product Id"));
			}

			if (StringUtils.isNotBlank(req.getRequestReferenceNo()) && StringUtils.isNotBlank(req.getProductId())) {
//				List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNoOrderBySectionNameAsc(req.getRequestReferenceNo());
//				 motorDatas.forEach(m -> {
//			            String key = m.getRequestReferenceNo() + "_" + m.getRiskId();
//			            req.getMotorDetailsMap()
//			                    .computeIfAbsent(key, k -> new ArrayList<>())
//			                    .add(m);
//			        });
//				 
//				EserviceTravelDetails    travelDatas = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
//				List<EserviceBuildingDetails> buildDatas = eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
//				List<EserviceCommonDetails> findDatas = eserCommonRepo.findByRequestReferenceNoOrderBySectionNameAsc(req.getRequestReferenceNo());
				
				// Launch all futures in parallel
			
//				
//				CompletableFuture<EserviceTravelDetails> travelFuture =
//				    CompletableFuture.supplyAsync(() ->
//				        eserTraRepo.findByRequestReferenceNo(
//				            req.getRequestReferenceNo()), taskExecutor);
//
//				CompletableFuture<List<EserviceBuildingDetails>> buildFuture =
//				    CompletableFuture.supplyAsync(() ->
//				        eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(
//				            req.getRequestReferenceNo()), taskExecutor);
//
//				CompletableFuture<List<EserviceCommonDetails>> commonFuture =
//				    CompletableFuture.supplyAsync(() ->
//				        eserCommonRepo.findByRequestReferenceNoOrderBySectionNameAsc(
//				            req.getRequestReferenceNo()), taskExecutor);
//				
//				CompletableFuture<List<EserviceSectionDetails>> sectionFuture =
//					    CompletableFuture.supplyAsync(() ->
//					    eserSecRepo.findByRequestReferenceNo(req.getRequestReferenceNo()), taskExecutor);
//
//				// Wait for all to complete
//				CompletableFuture.allOf(sectionFuture).join();

				// Collect results
				
				//List<EserviceMotorDetails>      motorDatas = motorFuture.join();
			//	EserviceTravelDetails           travelDatas = travelFuture.join();
			//	List<EserviceBuildingDetails>   buildDatas  = buildFuture.join();
			//	List<EserviceCommonDetails>     findDatas   = commonFuture.join();
		//		List<EserviceSectionDetails> sectionDatas = sectionFuture.join();
				List<EserviceSectionDetails> sectionDatas=eserSecRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
				// Motor map grouping
//				motorDatas.forEach(m -> {
//				    String key = m.getRequestReferenceNo() + "_" + m.getRiskId();
//				    req.getMotorDetailsMap()
//				        .computeIfAbsent(key, k -> new ArrayList<>())
//				        .add(m);
//				});
//				String companyId = motorDatas.size() > 0 ? motorDatas.get(0).getCompanyId() :	 travelDatas!=null ? travelDatas.getCompanyId()  
//						 :  buildDatas.size() > 0 ? buildDatas.get(0).getCompanyId() :  findDatas.size() > 0 ? findDatas.get(0).getCompanyId() : "" ;
				String companyId = sectionDatas.get(0).getCompanyId();
				
				req.setCompanyid(companyId);
				req.setGetSectionDatas(sectionDatas);
				CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , req.getProductId().toString());
				req.setProduct(product);
				String  custRefno = "" ;
				 if(product.getMotorYn().equalsIgnoreCase("M")) {
//					List<EserviceMotorDetails> list = eserMotRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
//					custRefno = list.size() > 0 ? list.get(0).getCustomerReferenceNo() :"";
//					 CompletableFuture<List<EserviceMotorDetails>> motorFuture =
//							    CompletableFuture.supplyAsync(() ->
//							        eserMotRepo.findByRequestReferenceNoOrderBySectionNameAsc(
//							            req.getRequestReferenceNo()), taskExecutor);

						List<EserviceMotorDetails>      motorDatas = eserMotRepo.findByRequestReferenceNoOrderBySectionNameAsc(
					            req.getRequestReferenceNo());
						motorDatas.forEach(m -> {
				            String key = m.getRequestReferenceNo() + "_" + m.getRiskId();
				            req.getMotorDetailsMap()
			                    .computeIfAbsent(key, k -> new ArrayList<>())
				                    .add(m);
			        });
						List<EserviceMotorDetails> allMotor = req.getMotorDetailsMap().values().stream()
								.flatMap(List::stream).filter(m -> req.getProductId().equals(m.getProductId()))
								.toList();

					    custRefno = !allMotor.isEmpty() ? allMotor.get(0).getCustomerReferenceNo() : "";
					    
					    
					
				} else  if(product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equals("4") ) {
					EserviceTravelDetails list = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
					custRefno = list!=null ? list.getCustomerReferenceNo() :"";
					req.setTravelDatas(list);					
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
//					List<EserviceBuildingDetails> list = eserBuildRepo
//							.findByRequestReferenceNo(req.getRequestReferenceNo());

					CompletableFuture<List<EserviceBuildingDetails>> buildFuture =
					    CompletableFuture.supplyAsync(() ->
					        eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(
					            req.getRequestReferenceNo()), taskExecutor);

					CompletableFuture<List<EserviceCommonDetails>> commonFuture =
					    CompletableFuture.supplyAsync(() ->
					        eserCommonRepo.findByRequestReferenceNoOrderBySectionNameAsc(
					            req.getRequestReferenceNo()), taskExecutor);
					CompletableFuture.allOf(commonFuture,buildFuture).join();
					
					List<EserviceBuildingDetails>   buildDatas  = buildFuture.join();
					List<EserviceCommonDetails>     findDatas   = commonFuture.join();
					req.setBuildDatas(buildDatas);	
					req.setHumanDatas(findDatas);
					List<EserviceBuildingDetails> list = buildDatas;
					if (list != null && !list.isEmpty()) {
						custRefno = list.size() > 0 ? list.get(0).getCustomerReferenceNo() : "";
					} else {
//						List<EserviceCommonDetails> list1 = eserCommonRepo
//								.findByRequestReferenceNo(req.getRequestReferenceNo());
						List<EserviceCommonDetails> list1 =findDatas;
						custRefno = list1.size() > 0 ? list1.get(0).getCustomerReferenceNo() : "";
					}
					if (StringUtils.isBlank(custRefno)) { // asset product only has human related covers
						List<EserviceSectionDetails> getSectionDatas = sectionDatas;
						
						boolean isHuman = getSectionDatas.stream()
								.anyMatch(e -> "H".equalsIgnoreCase(e.getProductType()));
						if (isHuman) {
							List<EserviceCommonDetails> list1 = findDatas;
							custRefno = list1.size() > 0 ? list1.get(0).getCustomerReferenceNo() : "";
						}
					}
				} else {
//					CompletableFuture<List<EserviceCommonDetails>> commonFuture =
//						    CompletableFuture.supplyAsync(() ->
//						        eserCommonRepo.findByRequestReferenceNoOrderBySectionNameAsc(
//						            req.getRequestReferenceNo()), taskExecutor);
//						CompletableFuture.allOf(commonFuture).join();
						List<EserviceCommonDetails>     findDatas   = eserCommonRepo.findByRequestReferenceNoOrderBySectionNameAsc(
					            req.getRequestReferenceNo());
			//		List<EserviceCommonDetails> list = findDatas;
					custRefno = findDatas.size() > 0 ? findDatas.get(0).getCustomerReferenceNo() :"";
					req.setHumanDatas(findDatas);
				}
				 
				EserviceCustomerDetails custData =  customerDetailsRepo.findByCustomerReferenceNo(custRefno);
				if( custData==null || custData.getStatus()==null || !"Y".equalsIgnoreCase(custData.getStatus())) {
					error.add("1151");
//					error.add(new Error("01", "Customer", "Customer Is Not Active"));
				}
				
			}
			
			
//			if(StringUtils.isNotBlank(req.getRequestReferenceNo() )  &&  StringUtils.isNotBlank(req.getProductId() ) ) {
//				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo()); 
//				List<EserviceSectionDetails>   sections = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());	
				
//				Long row = 0L ;
//				for (VehicleIdsReq id :  req.getVehicleIdsList() ) {
//					row = row + 1 ;
//						if(StringUtils.isBlank(id.getSectionId() ) ){
//							error.add(new Error("01", "SectionId", "Please Enter Section Id In Row No : " + row));
//						}
//						if(id.getVehicleId()==null ){
//							error.add(new Error("01", "RiskId", "Please Enter Risk Id In Row No : " + row));
//						}
						
//					if(StringUtils.isNotBlank(id.getSectionId() )  && id.getVehicleId()!=null  ) {
//						List<EserviceSectionDetails>   filterSection  = sections.stream().filter( o -> o.getSectionId().equalsIgnoreCase(id.getSectionId())).collect(Collectors.toList());
//						String sectionName = filterSection.size() > 0 ? filterSection.get(0).getSectionName() : id.getSectionId() ;
//						
//						// Base Cover Check in Table
//						List<FactorRateRequestDetails> filterBaseCover = covers.stream().filter( o -> o.getVehicleId() !=null && o.getSectionId() !=null && o.getCoverageType() !=null )
//								.filter( o ->  o.getVehicleId().equals(Integer.valueOf(id.getVehicleId())) && o.getSectionId().equals(Integer.valueOf(id.getSectionId()))
//										&& o.getCoverageType().equalsIgnoreCase("B") ).collect(Collectors.toList());
//						
//						
//						if(filterBaseCover.size() <=0 ) {
//							error.add(new Error("01", "BaseCover", "Base Cover Not Available In Risk Id :  " + id.getVehicleId()  + ", Section Id : " + sectionName ));
//						} else {
//							// Base Cover Check in Request
//							FactorRateRequestDetails cov = filterBaseCover.get(0);
//
//							String baseCoverId = cov.getCoverId().toString();
//							List<CoverIdsReq> filterBaseCoverReq = id .getCoverIdList().stream().filter( o -> o.getCoverId() !=null  )
//									.filter( o ->  o.getCoverId().equals(Integer.valueOf(baseCoverId))  ).collect(Collectors.toList());
//							if(filterBaseCoverReq.size() <=0 ) {
//								error.add(new Error("01", "BaseCover", "Base Cover Not Available In Risk Id :  " + id.getVehicleId()  + ", Section Id : " + sectionName ));
//							}
//						}
//						
//					}
					
					
					
	//			}
	//		}
		} catch (Exception e) {
			e.printStackTrace();
			error.add("1152");
//			error.add(new Error("01", "CommonError", e.getMessage() ));
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return error;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public SuccessRes tracesRemoved(TracesRemovedReq req) { //after buypolicy section based only, for all products
		SuccessRes res = new SuccessRes();			//additional infos delete
	
		try {
			//Domestic
			List<SectionDataDetails> secs = secDataRepo.findByQuoteNo(req.getQuoteNo());
			
			List<String> secIds = secs.stream().map(SectionDataDetails :: getSectionId).collect(Collectors.toList());
			
			List<ContentAndRisk> con = contentRepo.findByQuoteNo(req.getQuoteNo());
			List<ProductEmployeeDetails> emp = empRepo.findByQuoteNo(req.getQuoteNo());
			List<DocumentTransactionDetails> doc = docRepo.findByQuoteNo(req.getQuoteNo());
			
			if (secIds.size()>0) {
				
				//unmatched based on sectionid
				
				List<ContentAndRisk> confilter = con.stream().filter(o -> ! secIds.contains(o.getSectionId())).collect(Collectors.toList());	
				contentRepo.deleteAll(confilter);
				
				List<ProductEmployeeDetails> empfilter = emp.stream().filter(o -> ! secIds.contains(o.getSectionId())).collect(Collectors.toList());	
				empRepo.deleteAll(empfilter);
				
				List<DocumentTransactionDetails> docfilter = doc.stream().filter(o -> ! secIds.contains(o.getSectionId().toString())).collect(Collectors.toList());	
				docRepo.deleteAll(docfilter);
			}
			
			
			res.setResponse("Old Traces Removed");
			res.setSuccessId(req.getQuoteNo());
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
			return res;
	}

	@Override
	public GetEmployeeCountRes getProductEmplyee(EmployeeCountGetReq req) {
		GetEmployeeCountRes res = new GetEmployeeCountRes();
		
		try {
			HomePositionMaster homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , homeData.getProductId().toString());

		//	Long actualCount = 
			 if(product.getMotorYn().equalsIgnoreCase("H") &&  homeData.getProductId().equals(Integer.valueOf(travelProductId))) {
				
				 EserviceTravelDetails travelData = eserTraRepo.findByQuoteNoAndSectionIdAndProductIdOrderByRiskIdAsc(req.getQuoteNo(),req.getSectionId().toString(),req.getProductId().toString());
				 Integer  passengerCount=travelData.getTotalPassengers();
				
				List<TravelPassengerDetails> actualCount= traPassRepo.findByQuoteNoAndSectionIdAndProductId(req.getQuoteNo(),req.getSectionId(),req.getProductId());
				
				Integer count=actualCount.size();			
				Integer uploadcount=passengerCount-count;				
				res.setExpectedCount(passengerCount.longValue());
				res.setActualCount(count.longValue());
				res.setUploadCount(uploadcount.longValue());;
							
				}
						
		//	 } else if(product.getMotorYn().equalsIgnoreCase("M") ) {
				// Motor Product Details
//				viewRes =  getMotorProductDetails( req);
			
	//		} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
//				// Travel Product Details
//				viewRes =	getBuildingProductDetails( req);
//				
//			} 			 
			 else {
			 // Human Product Details
//				viewRes =	getCommonProductDetails( req);
				 
				 List<EserviceCommonDetails>  passCount=eserCommonRepo.findByQuoteNoAndSectionIdAndProductIdOrderByRiskIdAsc(req.getQuoteNo(),req.getSectionId().toString(),req.getProductId().toString());
				 List<ProductEmployeeDetails>   actualCount=personalRepo.findByQuoteNoAndSectionIdAndProductIdOrderByRiskIdAsc(req.getQuoteNo(),req.getSectionId().toString(),req.getProductId());
				 Integer actualEmpCount=actualCount.size();
				 Integer uploadedCount=0;
				 Integer count =0;
				 if(passCount.size()>=0)
					{
						for(EserviceCommonDetails data:passCount)
						{
							count=count+data.getCount();
						}		
						uploadedCount=count-actualEmpCount;	
						res.setExpectedCount(count.longValue());
						res.setActualCount(actualEmpCount.longValue());
						res.setUploadCount(uploadedCount.longValue());
					}				 
		}
			 
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
			return res;
	}

	@Override
	public List<GroupSuminsuredDetailsRes> groupSuminsuredDetails(SectionSumInsuredGetReq req) {
		List<GroupSuminsuredDetailsRes> resList = new ArrayList<GroupSuminsuredDetailsRes>();
		try {
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(homeData.getCompanyId() , homeData.getProductId().toString());

			
			 if(product.getProductId().equals(4)) {
				 List<EserviceTravelGroupDetails> travelGroupDatas = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(homeData.getRequestReferenceNo());
				 for (EserviceTravelGroupDetails group :  travelGroupDatas ) {
					 GroupSuminsuredDetailsRes res = new GroupSuminsuredDetailsRes();
					 res.setGroupCount(group.getGrouppMembers() ==null ? "" :group.getGrouppMembers().toString() );	
					 res.setGroupDesc(group.getGroupDesc());
					 res.setGroupId(group.getGroupId()==null?"": group.getGroupId().toString());
					 res.setGroupSuminsured("");
					 resList.add(res);
				}
				 
			} else {
				List<CommonDataDetails> commonList =  commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
				for (CommonDataDetails group :  commonList ) {
					 GroupSuminsuredDetailsRes res = new GroupSuminsuredDetailsRes();
					 res.setGroupCount(group.getCount() ==null ? "" :group.getCount().toString() );	
					 res.setGroupDesc(group.getOccupationDesc());
					 res.setGroupId(group.getRiskId()==null?"": group.getRiskId().toString());
					 res.setGroupSuminsured(group.getSumInsured()==null?"": group.getSumInsured().toPlainString());
					 resList.add(res);
				}
			}
				
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override

	public SuccessRes changefinalyzestatus(ChangeFinalyzereq req) {
		
		SuccessRes res = new SuccessRes();
		 
      try
      {  
    	  CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),req.getProductId());

		  if (product.getMotorYn().equalsIgnoreCase("M")) {
			
			List<EserviceMotorDetails> motors =  eserMotRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			motors.forEach( o -> {
				o.setFinalizeYn(req.getFinalizeYn());
			});			
			eserMotRepo.saveAll(motors);
			
		} else if (product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
				
			List<EserviceTravelDetails> travels = eserTraRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(),req.getProductId());
			travels.forEach( o -> {
				o.setFinalizeYn(req.getFinalizeYn());
			});	
			eserTraRepo.saveAll(travels);
    	
		} else if (product.getMotorYn().equalsIgnoreCase("A")) {
			
			List<EserviceBuildingDetails> building = eserBuildRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			building.forEach( o -> {
				o.setFinalizeYn(req.getFinalizeYn());
			});	
			eserBuildRepo.saveAll(building);
			
		} else {
			
			List<EserviceCommonDetails> common = eserCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			common.forEach( o ->{
				o.setFinalizeYn(req.getFinalizeYn());		
			});
			eserCommonRepo.saveAll(common);
		}
		
		res.setResponse("Success");
		res.setSuccessId(req.getRequestReferenceNo());    
		return res;
      }
      catch(Exception e)
      {
    	  e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;

      }
  }


	public List<Error> validateStartdate(UpdatePolicyStartEndDateReq req) {
		List<Error> error = new ArrayList<Error>();

		try {
			
			if(StringUtils.isBlank(req.getQuoteNo())){
				error.add(new Error("01","Quote No","Please Enter Quote No"));
			}
			
			HomePositionMaster hp = homeRepo.findByQuoteNo(req.getQuoteNo());
			
			if(hp!=null) {
				if (req.getPolicyStartDate()== null) {
					error.add(new Error("13", "PolicyStartDate", "Please Enter PolicyStartDate"));
				} else if ((hp.getEndtTypeId() == null || hp.getEndtTypeId().equalsIgnoreCase("0"))) {
//					int before = getBackDays(hp.getCompanyId(), String.valueOf(hp.getProductId()), hp.getLoginId()); //madinbank
//					int days = before == 0 ? -1 : -before;
//					long MILLS_IN_A_DAY = 1000 * 60 * 60 * 24;
//					long backDays = MILLS_IN_A_DAY * days;
//					Date today = new Date();
//					Date resticDate = new Date(today.getTime() + backDays);
//					long days90 = MILLS_IN_A_DAY * 90;
//					Date after90 = new Date(today.getTime() + days90);
					
					Calendar cal = new GregorianCalendar();
					Date today = new Date();
					cal.setTime(today);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 50);
					today = cal.getTime();
					
					if (req.getPolicyStartDate().before(today)) {
						error.add(new Error("14", "PolicyStartDate", "Policy Start Date Back Days Not Allowed "));
					}
//					else if (req.getPolicyStartDate().after(after90)) {
//						error.add(new Error("14", "PolicyStartDate", "PolicyStartDate  even after 90 days Not Allowed"));
//					}
	
				}
			}
//			
//			if (req.getPolicyEndDate()==null) {
//				error.add(new Error("13", "PolicyEndDate", "Please Enter PolicyEndDate"));
//			} else if ( req.getPolicyEndDate().before(req.getPolicyStartDate() )){
//				error.add(new Error("13", "PolicyEndDate", "Please Enter PolicyEndDate Greater Than PolicyStartDate"));
//			}	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return error;
	}

	@Override
	public SuccessRes updatePolicyStartEndDate(UpdatePolicyStartEndDateReq req) {
		SuccessRes res = new SuccessRes();
		try {
			HomePositionMaster hp = homeRepo.findByQuoteNo(req.getQuoteNo());
			
			CompanyProductMaster product = getCompanyProductMasterDropdown(hp.getCompanyId(),
					hp.getProductId().toString());
			
			//End date
			int period = hp.getPolicyPeriod()==null?0:Integer.valueOf(hp.getPolicyPeriod())	;
				
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(req.getPolicyStartDate());
			
			if(hp.getProductId()==4 )
				calendar.add(Calendar.DAY_OF_MONTH, (period-1));
			else
				calendar.add(Calendar.DAY_OF_MONTH, period);

			Date endDate = calendar.getTime();
			
			
			// main tables update
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<MotorDataDetails> update = cb.createCriteriaUpdate(MotorDataDetails.class);
				Root<MotorDataDetails> m = update.from(MotorDataDetails.class);
				update.set("policyStartDate", req.getPolicyStartDate());
				update.set("policyEndDate", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
				
			} else if (product.getMotorYn().equalsIgnoreCase("H")&& hp.getProductId().toString().equalsIgnoreCase(travelProductId)) {
					

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<TravelPassengerDetails> update = cb.createCriteriaUpdate(TravelPassengerDetails.class);
				Root<TravelPassengerDetails> m = update.from(TravelPassengerDetails.class);
				update.set("travelStartDate", req.getPolicyStartDate());
				update.set("travelEndDate", endDate);
				update.set("effectiveDate", req.getPolicyStartDate());
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
				
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<BuildingRiskDetails> update = cb.createCriteriaUpdate(BuildingRiskDetails.class);
				Root<BuildingRiskDetails> m = update.from(BuildingRiskDetails.class);
				update.set("policyStartDate", req.getPolicyStartDate());
				update.set("policyEndDate", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
				
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<CommonDataDetails> update = cb.createCriteriaUpdate(CommonDataDetails.class);
				Root<CommonDataDetails> m = update.from(CommonDataDetails.class);
				update.set("policyStartDate", req.getPolicyStartDate());
				update.set("policyEndDate", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();

			}
			
			//Row tables update
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<EserviceMotorDetails> update = cb.createCriteriaUpdate(EserviceMotorDetails.class);
				Root<EserviceMotorDetails> m = update.from(EserviceMotorDetails.class);
				update.set("policyStartDate", req.getPolicyStartDate());
				update.set("policyEndDate", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
				
			} else if (product.getMotorYn().equalsIgnoreCase("H")&& hp.getProductId().toString().equalsIgnoreCase(travelProductId)) {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<EserviceTravelDetails> update = cb.createCriteriaUpdate(EserviceTravelDetails.class);
				Root<EserviceTravelDetails> m = update.from(EserviceTravelDetails.class);
				update.set("travelStartDate", req.getPolicyStartDate());
				update.set("travelEndDate", endDate);
				update.set("effectiveDate", req.getPolicyStartDate());
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
				
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<EserviceBuildingDetails> update = cb.createCriteriaUpdate(EserviceBuildingDetails.class);
				Root<EserviceBuildingDetails> m = update.from(EserviceBuildingDetails.class);
				update.set("policyStartDate", req.getPolicyStartDate());
				update.set("policyEndDate", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
				
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<EserviceCommonDetails> update = cb.createCriteriaUpdate(EserviceCommonDetails.class);
				Root<EserviceCommonDetails> m = update.from(EserviceCommonDetails.class);
				update.set("policyStartDate", req.getPolicyStartDate());
				update.set("policyEndDate", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();

			}
			//other tables
//			{
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaUpdate<SectionDataDetails> update = cb.createCriteriaUpdate(SectionDataDetails.class);
//				Root<SectionDataDetails> m = update.from(SectionDataDetails.class);
//				update.set("policyStartDate", req.getPolicyStartDate());
//				update.set("policyEndDate", endDate);
//				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
//				update.where(n1);
//				em.createQuery(update).executeUpdate();
//			}
			
//			{
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaUpdate<EserviceSectionDetails> update = cb.createCriteriaUpdate(EserviceSectionDetails.class);
//				Root<EserviceSectionDetails> m = update.from(EserviceSectionDetails.class);
//				update.set("policyStartDate", req.getPolicyStartDate());
//				update.set("policyEndDate", endDate);
//				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
//				update.where(n1);
//				em.createQuery(update).executeUpdate();
//			}
			
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<PolicyCoverData> update = cb.createCriteriaUpdate(PolicyCoverData.class);
				Root<PolicyCoverData> m = update.from(PolicyCoverData.class);
				update.set("coverPeriodFrom", req.getPolicyStartDate());
				update.set("coverPeriodTo", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
			}
			
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<PolicyCoverDataIndividuals> update = cb.createCriteriaUpdate(PolicyCoverDataIndividuals.class);
				Root<PolicyCoverDataIndividuals> m = update.from(PolicyCoverDataIndividuals.class);
				update.set("coverPeriodFrom", req.getPolicyStartDate());
				update.set("coverPeriodTo", endDate);
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
			}
			
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
				Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
				update.set("inceptionDate", req.getPolicyStartDate());
				update.set("expiryDate", endDate);
				update.set("effectiveDate", req.getPolicyStartDate());
				Predicate n1 = cb.equal(m.get("quoteNo"), req.getQuoteNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
			}
			
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaUpdate<FactorRateRequestDetails> update = cb.createCriteriaUpdate(FactorRateRequestDetails.class);
				Root<FactorRateRequestDetails> m = update.from(FactorRateRequestDetails.class);
				update.set("coverPeriodFrom", req.getPolicyStartDate());
				update.set("coverPeriodTo", endDate);
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), hp.getRequestReferenceNo());
				update.where(n1);
				em.createQuery(update).executeUpdate();
			}

		res.setResponse("Policy Start/End Dates Updated Successfully")	;
		res.setSuccessId(req.getQuoteNo());		
		
		
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	public synchronized CompanyProductMaster getCompanyProductMasterDropdown1(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();

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


	public Integer getBackDays(String companyId , String productId , String loginId ) {
		Integer backDays = 0 ;
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

			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			jakarta.persistence.criteria.Predicate a3 = cb.equal(b.get("loginId"), ocpm1.get("loginId"));
			jakarta.persistence.criteria.Predicate a4 = cb.equal(b.get("productId"), ocpm1.get("productId"));
			jakarta.persistence.criteria.Predicate a11 = cb.equal(b.get("policyType"), ocpm1.get("policyType"));
			jakarta.persistence.criteria.Predicate a12 = cb.equal(b.get("id"), ocpm1.get("id"));
			effectiveDate.where(a1, a2, a3,a4,a11,a12);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm2 = effectiveDate2.from(BrokerCommissionDetails.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			jakarta.persistence.criteria.Predicate a6 = cb.equal(b.get("companyId"), ocpm2.get("companyId"));
			jakarta.persistence.criteria.Predicate a8 = cb.equal(b.get("productId"), ocpm2.get("productId"));
			jakarta.persistence.criteria.Predicate a9 = cb.equal(b.get("loginId"), ocpm2.get("loginId"));
			jakarta.persistence.criteria.Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			jakarta.persistence.criteria.Predicate a13 = cb.equal(b.get("policyType"), ocpm2.get("policyType"));
			jakarta.persistence.criteria.Predicate a14 = cb.equal(b.get("id"), ocpm2.get("id"));
			effectiveDate2.where(a6,  a8, a9, a10,a13,a14);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyType")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n3 = cb.equal(b.get("companyId"), companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"), loginId);
			Predicate n6 = cb.equal(b.get("policyType"),"99999");
			Predicate n7 = cb.equal(b.get("id"),"99999");
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);

			list = result.getResultList();
			backDays = list.size() > 0 ? (list.get(0).getBackDays() !=null ? list.get(0).getBackDays() : 0)  : 0 ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return backDays;
	}

	@Override
	public List<String> validateCoInsurance(List<CoInsuranceSaveReq> reqList) {
		List<String> error = new ArrayList<String>();
		try {
			List<String> yN= new ArrayList<String>();
			List<String> companyId=new ArrayList<String>();
			Double percent=0d;
			if(reqList.size()>0 && reqList!=null) {
//				List<Integer> companyId =  reqList.stream().map( CoInsuranceSaveReq :: getInsuranceCompanyId ) .collect(Collectors.toList());
			for(CoInsuranceSaveReq req: reqList) {
				if(StringUtils.isBlank(req.getQuoteNo()) || req.getQuoteNo()==null ||req.getQuoteNo()=="") {
					error.add("2183");
				}else if(StringUtils.isBlank(req.getCompanyId()) || req.getCompanyId()==null ||req.getCompanyId()=="") {
					error.add("2184");
				}else if(StringUtils.isBlank(req.getInsuranceCompanyId()) || req.getInsuranceCompanyId()==null ||req.getInsuranceCompanyId()=="") {
					error.add("2185");
				}else if (StringUtils.isNotBlank(req.getInsuranceCompanyId())) {
					List<String> coIns =  companyId.stream().filter( o -> o.equalsIgnoreCase(req.getCompanyId())).collect(Collectors.toList()); 
					if(coIns.size()<0  ) {
						error.add("2186");
					} else {
						companyId.add(req.getInsuranceCompanyId());
					}
				}else if(StringUtils.isBlank(req.getLeaderYn()) || req.getLeaderYn()==null ||req.getLeaderYn()=="") {
					error.add("");
				} else if (StringUtils.isNotBlank(req.getLeaderYn())) {
					if (!("Y".equalsIgnoreCase(req.getLeaderYn()) || "N".equalsIgnoreCase(req.getLeaderYn()))) {
						error.add("2187");
					}
					List<String> leaderYn =  yN.stream().filter( o -> o.equalsIgnoreCase(req.getLeaderYn()) ).collect(Collectors.toList()); 
					if(leaderYn.size()>0  ) {
						error.add("2188");
					} else {
						yN.add(req.getLeaderYn());
					}	
				} else if (req.getSharePercentage() == null ||req.getSharePercentage() <= 0.0 || req.getSharePercentage()>100.0 ) {
					error.add("2189");
				}else if(req.getSharePercentage()!=null && req.getSharePercentage() >0.0) {
					percent=req.getSharePercentage() +percent;					
					if(percent>100){
						error.add("2190");
					}else if(percent<100){
						error.add("2191");
					}
					
				}
				
			}
			}else {
				error.add("2182");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			error.add("2181");
			return null;
		}
		return error;
	}
	

	@Override
	public SuccessRes insertCoInsurance(List<CoInsuranceSaveReq> reqList) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			String quoteNo = "";
			String companyId = "";
			String companyName = "";
			List<EwaySharePercentage> saveList = new ArrayList<EwaySharePercentage>();
			if (reqList != null && reqList.size() > 0) {
				quoteNo = reqList.get(0).getQuoteNo();
				companyId = reqList.get(0).getCompanyId();
				List<EwaySharePercentage> list = coInsRepo.findByQuoteNoAndCompanyId(quoteNo, companyId);
				if (list.size() > 0 && list != null) {
					coInsRepo.deleteAll(list);
				}
				for (CoInsuranceSaveReq req : reqList) {
					EwaySharePercentage save = new EwaySharePercentage();
					mapper.map(req, save);
					save.setEntryDate(new Date());
					save.setProductId(Integer.valueOf(req.getProductId()));
					save.setInsuranceCompanyId(Integer.valueOf(req.getInsuranceCompanyId()));
					companyName = getInscompanyMasterDropdown(req.getCompanyId());
					save.setInsuranceCompanyName(companyName);
					saveList.add(save);
				}
				coInsRepo.saveAllAndFlush(saveList);
				res.setResponse("Inserted Successfully");
				res.setSuccessId(quoteNo);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
			
	public String getInscompanyMasterDropdown(String companyId ) {
		String companyName = "" ;
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InsuranceCompanyMaster> query = cb.createQuery(InsuranceCompanyMaster.class);
			List<InsuranceCompanyMaster> list = new ArrayList<InsuranceCompanyMaster>();
			
			// Find All
			Root<InsuranceCompanyMaster>    c = query.from(InsuranceCompanyMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("companyName")));
			
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			
			// Effective Date End
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			jakarta.persistence.criteria.Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			
		    // Where	
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
	
			// Get Result
			TypedQuery<InsuranceCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();
			companyName  = list.size()> 0 ? list.get(0).getCompanyName() : "";	
				
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return companyName;
	}
	
	 
	 
	@Override
	 
		public CommonRes userOptUpdate(NewQuoteReq req) {
	 
		    CommonRes response = new CommonRes();
	 
		    try {
	 
		        int totalUpdated = 0;
	 
		        int totalRecords = 0;
	 
	 
	 
		        if (req.getVehicleIdsList() != null) {
	 
		            for (var vehicle : req.getVehicleIdsList()) {
	 
	 
	 
		        
	 
		                Integer sectionId = null;
	 
		                try {
	 
		                    sectionId = Integer.parseInt(vehicle.getSectionId().trim());
	 
		                } catch (Exception e) {
	 
		                    System.err.println("Invalid sectionId: " + vehicle.getSectionId());
	 
		                    continue;
	 
		                }
	 
	 
	 
		                Integer vehicleId = null;
	 
		                try {
	 
		                    vehicleId = Integer.valueOf(vehicle.getVehicleId());
	 
		                } catch (Exception e) {
	 
		                    System.err.println("Invalid vehicleId: " + vehicle.getVehicleId());
	 
		                    continue;
	 
		                }
	 
		                int resetCount = facRateRepo.resetUserOptToN(req.getRequestReferenceNo(), sectionId, vehicleId);
	 
	 
	 
		                totalUpdated += resetCount;
	 
	 
	 
		                
	 
		                if (vehicle.getCoverIdList() != null) {
	 
		                    for (var cover : vehicle.getCoverIdList()) {
	 
		                        Integer coverId = null;
	 
		                        Integer subCoverId=null;
	 
		                        try {
	 
		                            coverId = Integer.valueOf(cover.getCoverId());
	 
		                            
	 
		                            if(cover.getSubCoverId()!=null) {
	 
		                            subCoverId=Integer.valueOf(cover.getSubCoverId() );
	 
		                            }
	 
		                            
	 
		                        } catch (Exception e) {
	 
		                            System.err.println("Invalid coverId: " + cover.getCoverId());
	 
		                            System.err.println("Invalid coverId: " + cover.getCoverId());
	 
		                            continue;
	 
		                        }
	 
	 
	 
		                        totalRecords++;
	 
		                        int updated = facRateRepo.updateUserOptToY(
	 
		                            req.getRequestReferenceNo(),
	 
		                            sectionId,
	 
		                            coverId,
	 
		                            subCoverId,
	 
		                            vehicleId
	 
		                        );
	 
	 
	 
		                        totalUpdated += updated;
	 
		                    }
	 
		                }
	 
		            }
	 
		        }
	 
	 
	 
		        // Prepare response
	 
		        if (totalUpdated > 0) {
	 
		            response.setIsError(false);
	 
		            response.setMessage("UserOpt updated successfully.");
	 
		            response.setErroCode(200);
	 
		            response.setCommonResponse(
	 
		                String.format("Updated %d records successfully.", totalUpdated)
	 
		            );
	 
		        } else {
	 
		            response.setIsError(true);
	 
		            response.setMessage("No matching records found for update.");
	 
		            response.setErroCode(404);
	 
		        }
	 
	 
	 
		    } catch (Exception e) {
	 
		        e.printStackTrace();
	 
		        response.setIsError(true);
	 
		        response.setMessage("Exception occurred while updating UserOpt.");
	 
		        response.setErroCode(500);
	 
		    }
	 
	 
	 
		    return response;
	 
		}

	@Override
	public NewQuoteReq frameRequest(String refNo, NewQuoteReq req) {
		NewQuoteReq newQuoteReq = new NewQuoteReq();
		try {
			List<FactorRateRequestDetails> factList = eserCovRepo.findByRequestReferenceNoAndUserOptOrderByVehicleIdAsc(refNo, "Y");
			if (factList != null && !factList.isEmpty()) {
				Map<String, List<FactorRateRequestDetails>> groupMap = factList.stream()
						.filter(f -> f.getVehicleId() != 99999 && f.getLocationId() != 99999)
						.filter(f -> !"T".equals(f.getCoverageType()) && !"L".equals(f.getCoverageType()))
						.collect(Collectors
								.groupingBy(f -> f.getVehicleId() + "_" + f.getLocationId() + "_" + f.getSectionId() // composite
								));
				
				boolean hasCoverageTypeE = factList.stream()
				        .anyMatch(f -> "E".equalsIgnoreCase(f.getCoverageType()));
				
				
				
				List<VehicleIdsReq> vehicleList = new ArrayList<>();

				for (Map.Entry<String, List<FactorRateRequestDetails>> entry : groupMap.entrySet()) {
					List<FactorRateRequestDetails> facts = entry.getValue();
					FactorRateRequestDetails first = facts.get(0);

					VehicleIdsReq vehicleReq = new VehicleIdsReq();
					vehicleReq.setVehicleId(first.getVehicleId());
					vehicleReq.setLocationId(first.getLocationId());
					vehicleReq.setSectionId(String.valueOf(first.getSectionId()));

					List<CoverIdsReq> coverList = new ArrayList<>();

					for (FactorRateRequestDetails fact : facts) {
						CoverIdsReq cover = new CoverIdsReq();
						cover.setCoverId(fact.getCoverId());
						cover.setSubCoverYn(fact.getSubCoverYn());
						cover.setSubCoverId((fact.getSubCoverId() != null && fact.getSubCoverId() != 0) ? String.valueOf(fact.getSubCoverId()) 
							        : null
							);
					//	cover.setUserOpt(fact.getUserOpt());
						coverList.add(cover);
					}

					vehicleReq.setCoverIdList(coverList);
					vehicleList.add(vehicleReq);
				}
				newQuoteReq.setRequestReferenceNo(refNo);
				newQuoteReq.setCreatedBy(req.getCreatedBy());
				newQuoteReq.setProductId(req.getProductId());
				newQuoteReq.setManualReferralYn(req.getManualReferralYn());
				newQuoteReq.setEmiYn(req.getEmiYn());
				newQuoteReq.setReferralRemarks(req.getReferralRemarks());
				newQuoteReq.setVehicleIdsList(vehicleList);
				newQuoteReq.setEntd(hasCoverageTypeE);
				  ObjectMapper mapper = new ObjectMapper();
		            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newQuoteReq);
		            System.out.println("==== frameRequest JSON Buypolicy ====");
		            System.out.println(json);
		            System.out.println("===========================");
			}
		} catch (Exception e) {

		}
		return newQuoteReq;
	}
	 
	

}
