package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.chartaccount.ChartAccountRequest;
import com.maan.eway.chartaccount.ChartAccountServiceImpl;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentTracking;
import com.maan.eway.mtpintegration.mtppayment.service.MtpPaymentStickerService;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.thirdparty.TravelApiIntegration;
import com.maan.eway.thirdparty.Mapfre.service.MapfreClientIntegration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class GeneratePolicyEwayServiceImpl {
	@Autowired
	private PaymentDetailRepository paymentdetailrepo;
	
	@Autowired
	private EmiTransactionDetailsRepository emiRepo;

	@Autowired
	private GenerateSeqNoServiceImpl genNo;
	
	@Autowired
	private RatingFactorsUtil ratingutil;
	
//	@Lazy
//	@Autowired
//	private CalculatorEngine calcService;

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private MapfreClientIntegration mapfreClientIntegration;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${ticketCreation.boolean}")
	private boolean isTicketCreation;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo ;
	
//	@Lazy
//	@Autowired
//	private PaymentService paymentService ;
	
//	@Autowired
//	private SelcomPaymentService selcomService;
	
	@Autowired
	private ChartAccountServiceImpl accountServiceImpl;
	
	@Autowired
	private TravelApiIntegration travelApiIntegration;
	
	@Autowired
	private MtpPaymentStickerService mtpStickerService;

	@Autowired private PaymentDbService paymentDbService;
	
	@Autowired
	private SectionDataDetailsRepository sectionRepo;
	
	@Autowired
	private PersonalInfoRepository piRepo;
	
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);
	
public List<PolicyDrcrDetail> generatePolicy(PaymentInfo paymentInfo, PaymentDetailsSaveReq req, PaymentDetail paymentDetail, String token) {
		
		return  generatePolicyNew(paymentInfo,req,paymentDetail,token);
		 
	}

@SuppressWarnings("unchecked")
public List<PolicyDrcrDetail>  generatePolicyNew(PaymentInfo paymentInfo, PaymentDetailsSaveReq req, PaymentDetail paymentDetail, String token) {
	List<PolicyDrcrDetail> policydrcr  = new ArrayList<PolicyDrcrDetail>();
	try {
		
		if (paymentDetail.getPaymentStatus().equalsIgnoreCase("ACCEPTED")
				&& paymentDetail.getCompanyId().equals("100046")) {

			callDiscNumberGeneration(req.getQuoteNo());
			
		}

		//HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
		
		HomePositionMaster data = paymentDbService.findHome(req.getQuoteNo());
		//String paymentMode = getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_MODE",req.getPaymentType());
		
		String policyNo ="";
		String pdfLink="";
						
		List<DebitAndCredit> policyDetails = new ArrayList<DebitAndCredit>();
		
		if ("ACCEPTED".equalsIgnoreCase(paymentDetail.getPaymentStatus())
			    && Integer.valueOf(4).equals(data.getProductId())
			    && ("100020".equals(paymentDetail.getCompanyId())
			        || "100046".equals(paymentDetail.getCompanyId())
			        		||"100002".equals(paymentDetail.getCompanyId()))
			) {
			
			if("100020".equals(paymentDetail.getCompanyId()) || "100002".equals(paymentDetail.getCompanyId())){
				Map<String, Object> response = mapfreClientIntegration.call_mapfreIntegration(data.getQuoteNo(), data.getRequestReferenceNo());
		
				 pdfLink = response.get("PdfLink")==null?"":response.get("PdfLink").toString();
				 response = (Map<String,Object>) response.get("Body");
				
				 data.setResponseStatusDesc(pdfLink);
				
				 if (response != null && response.get("numContrato") != null && !response.get("numContrato").toString().isEmpty()) {
				       
					 	policyNo = response.get("numContrato").toString();
					 	log.info(response);
				        log.info("Policy Number (numContrato): " + policyNo);
				        
				    } else {
				        
				    	log.info(response);
				        log.info("Mapfre Integration failed or no policy number returned.");
				    }
			}else if("100046".equals(paymentDetail.getCompanyId())){
				Map<String, Object> response = travelApiIntegration.push_confirmPurchace(
				        data.getQuoteNo(),
				        data.getRequestReferenceNo()
				);

				if (response != null) {
				    Object policyNumberObj = response.get("PolicyNumber");
				    Object policyUrlObj = response.get("PolicyURL");

				    if (policyNumberObj != null && !policyNumberObj.toString().isEmpty()) {
				        policyNo = policyNumberObj.toString();
				        log.info("Policy Number (PolicyNo): {}", policyNo);

				        if (policyUrlObj != null && !policyUrlObj.toString().isEmpty()) {
				            String policyURL = policyUrlObj.toString();
				            log.info("Policy URL Link: {}", policyURL);
				            data.setResponseStatusDesc(policyURL);
				        }
				    } else {
				        log.warn("Tune Protect Integration failed or no policy number returned. Response: {}", response);
				    }
				} else {
				    log.error("No response received from Travel API.");
				}

				
			}
			
			
		}else {
			CalcCommission  policyReq = new CalcCommission();
			policyReq.setAgencyCode("");
			policyReq.setBranchCode(paymentInfo.getBranchCode());
			policyReq.setCreatedBy(req.getCreatedBy());
			policyReq.setInsuranceId(paymentInfo.getCompanyId());
			policyReq.setPolicyNo("");
			policyReq.setProductId(paymentInfo.getProductId().toString());
			policyReq.setQuoteno(req.getQuoteNo());
			policyReq.setSectionId("");
			//policyNo = calcService.getPolicyNo(policyReq);
			policyNo = getPolicyNo(policyReq, data);
		}
		

		if(StringUtils.isNotBlank(policyNo)) {

			ChartAccountRequest request = new ChartAccountRequest();

			request.setQuoteNo(req.getQuoteNo());
			request .setPolicyNo(policyNo);
			request.setDiscountYn("N");
			//CommonRes res =accountServiceImpl.drcrEntry(request);
			
			CommonRes res = accountServiceImpl.drcrEntry(request);

			policydrcr =(List<PolicyDrcrDetail>)res.getCommonResponse();

			List<PolicyDrcrDetail> filterDebit=policydrcr.stream().filter(p->"DR".equalsIgnoreCase(p.getDrcrFlag())).collect(Collectors.toList());
			List<PolicyDrcrDetail> filterCredit=policydrcr.stream().filter(p->"CR".equalsIgnoreCase(p.getDrcrFlag())).collect(Collectors.toList());
			// Debit
			String debitNo = filterDebit.size() > 0 ?  filterDebit.get(0).getDocNo() :"" ;
			Date debitDate = filterDebit.size() > 0 ? filterDebit.get(0).getEntryDate() : null;
			String debitTo = filterDebit.size() > 0 ?  filterDebit.get(0).getDocType()  : "";

			String creditNo ="";
			Date creditDate =null;
			String creditTo = "";
			if(filterCredit!=null && !filterCredit.isEmpty()) {
				// Credit
				creditNo =  filterCredit.get(0).getDocNo();
				creditDate = filterCredit.get(0).getEntryDate();
				creditTo = filterCredit.get(0).getDocType();

			}
			
			paymentDbService.persistPolicyResult(data, paymentInfo, req, paymentDetail, policyNo,
					debitNo, debitDate, debitTo, creditNo, creditDate, creditTo);
			// Update ProductWise
			CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString());
			String msg = paymentDbService.updateProductWisePolicyNo(paymentInfo.getProductId().toString() ,policyNo ,req.getQuoteNo(),data.getEndtTypeId() , product.getMotorYn(),data.getCommissionPercentage());
			
			updateEmiTransactionDetails(req.getQuoteNo());
			if ("100019".equalsIgnoreCase(paymentInfo.getCompanyId())
			        && Integer.valueOf(5).equals(data.getProductId())
			        && !Integer.valueOf(125).equals(data.getProductId())
			        && StringUtils.isNotBlank(data.getPolicyNo())
			        && isMtpStickerEligible(paymentDetail.getQuoteNo())) {

			    triggerMtpEmployeePayment(paymentInfo, data, data.getPolicyNo(), req);
			}
			
			return policydrcr;
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

private void updateEmiTransactionDetails(String quoteNo) {
	DozerBeanMapper dozermapper = new DozerBeanMapper();
	List<PaymentDetail> paymentList = paymentdetailrepo.findByQuoteNoOrderByMerchantReferenceAsc(quoteNo);
	if((!paymentList.isEmpty()) && paymentList.get(0).getEmiYn().equalsIgnoreCase("Y")) {
	for (PaymentDetail m : paymentList) {
		List<EmiTransactionDetails> emiDetails = emiRepo.findByQuoteNo(quoteNo);
		if (!emiDetails.isEmpty()) {
			String paymentStatus = m.getPaymentStatus();
			Date responseTime =m.getResponseTime();
			EmiTransactionDetails saveDate = new EmiTransactionDetails();
			List<EmiTransactionDetails> emiDetails1 = emiDetails.stream()
					.filter(o -> m.getMerchantReference().equalsIgnoreCase(o.getMerchantReference()))
					.collect(Collectors.toList());
			for (EmiTransactionDetails data1 : emiDetails1) {
				saveDate = dozermapper.map(data1, EmiTransactionDetails.class);
				if (paymentStatus.equalsIgnoreCase("ACCEPTED")) {
					saveDate.setPaymentStatus("Paid");
				} else {
					saveDate.setPaymentStatus("Pending");
				}
				if(responseTime==null) {
					saveDate.setPaymentDate(m.getUpdatedDate());
				}
				saveDate.setPaymentDate(responseTime);
				emiRepo.saveAndFlush(saveDate);
			}
		}

	}
}

}
@Transactional
public void callDiscNumberGeneration(String quoteNo) {
	try {

		StoredProcedureQuery disc = em.createStoredProcedureQuery("phoenix_disk_no_generated");
		disc.registerStoredProcedureParameter("quoteno", String.class, ParameterMode.IN);
		disc.setParameter("quoteno", quoteNo);

		disc.execute();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

private void triggerMtpEmployeePayment(PaymentInfo originalPaymentInfo, HomePositionMaster data,
		String policyNo, PaymentDetailsSaveReq req) {
	try {
		String quoteNo = originalPaymentInfo.getQuoteNo();
		String companyId = originalPaymentInfo.getCompanyId();

//		Optional<MtpPaymentTracking> existing = trackingRepo.findByQuoteNoAndStatusIn(quoteNo,Arrays.asList("PENDING", "SUCCESS"));
		Optional<MtpPaymentTracking> existing = paymentDbService.findMtpTracking(quoteNo);
				
		if (existing.isPresent()) {
			log.info("MTP sticker already triggered for quoteNo {}, skipping", quoteNo);
			return;
		}

		PaymentVendorMaster mtpVendor = mtpStickerService.getMtpVendor(companyId);
		if (mtpVendor == null) {
			log.error("MTP vendor not found for companyId {}", companyId);
			return;
		}

		PaymentDetail employeePaymentDetail = new PaymentDetail();
		employeePaymentDetail.setQuoteNo(quoteNo);
		employeePaymentDetail.setCompanyId(companyId);
		employeePaymentDetail.setPaymentType("6");
		employeePaymentDetail.setPaymentTypedesc("MTP Sticker Payment");
		employeePaymentDetail.setPaymentStatus("PENDING");
		employeePaymentDetail.setReqBillToPhone(req.getInsurancePayenumber());
		employeePaymentDetail.setCustomerName("Test");
		employeePaymentDetail.setPremium(originalPaymentInfo.getPremium());
		employeePaymentDetail.setPremiumFc(originalPaymentInfo.getPremiumFc());
		employeePaymentDetail.setPremiumLc(originalPaymentInfo.getPremiumLc());
		employeePaymentDetail.setCurrencyId(originalPaymentInfo.getCurrencyId());
		employeePaymentDetail.setExchangeRate(originalPaymentInfo.getExchangeRate());
		employeePaymentDetail.setPaymentId(originalPaymentInfo.getPaymentId());		
		employeePaymentDetail.setEmiYn("N");
		employeePaymentDetail.setEntryDate(new Date());
		employeePaymentDetail.setUpdatedDate(new Date());
		employeePaymentDetail.setCreatedBy("SYSTEM");
		employeePaymentDetail.setUpdatedBy("SYSTEM");

		JsonObject mtpResponse = mtpStickerService.createOrderForPaymentMTP(employeePaymentDetail);

		if (mtpResponse == null || !"SUCCESS".equalsIgnoreCase(mtpResponse.get("result").getAsString())) {
			log.error("MTP employee payment initiation failed for quoteNo {}: {}", quoteNo, mtpResponse);

			paymentDbService.createMtpTrackingRowOnFailure(quoteNo, policyNo, companyId, req.getInsurancePayename(),
					req.getInsurancePayenumber(), 1);
			
			return;
		}


		JsonArray dataArray = mtpResponse.get("data").getAsJsonArray();
		JsonObject dataObj = dataArray.get(0).getAsJsonObject();
		String paymentRequestId = dataObj.has("paymentRequestId")
				? dataObj.get("paymentRequestId").getAsString()
				: "";
		String paymentChannel = dataObj.has("paymentChannel") ? dataObj.get("paymentChannel").getAsString()
				: "";
		String returnMessage = dataObj.has("returnMessage") ? dataObj.get("returnMessage").getAsString() : "";


		MtpPaymentTracking tracking = paymentDbService.persistMtpSuccessState(
				originalPaymentInfo, employeePaymentDetail, quoteNo, policyNo, companyId, req,
				paymentRequestId, paymentChannel, returnMessage, mtpResponse.toString());

		log.info("MTP employee payment triggered for quoteNo {} requestId {} channel {}", quoteNo,
				paymentRequestId, paymentChannel);
		
		mtpStickerService.pollMtpStatusAndRetry(tracking, mtpVendor);

	} catch (Exception e) {
		log.error("MTP employee payment trigger exception for quoteNo {}: {}", originalPaymentInfo.getQuoteNo(),
				e.getMessage(), e);
	}
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

private boolean isMtpStickerEligible(String quoteNo) {
    List<MotorDataDetails> motors = motorRepo.findByQuoteNo(quoteNo);
    return !CollectionUtils.isEmpty(motors) && motors.stream().anyMatch(m ->
        m.getSectionId().equals(103)
        && m.getSubUserType() != null
    );
}

public String getPolicyNo(CalcCommission request, HomePositionMaster hpm) {
	String policyNo = "";
	try {

//		ViewQuoteReq q = new ViewQuoteReq();
//		q.setQuoteNo(request.getQuoteno());
//		ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
		CompanyProductMaster product = getCompanyProductMasterDropdown(
                hpm.getCompanyId(), hpm.getProductId().toString());
        String endttypeid = hpm.getEndtTypeId();
        String emiYn = StringUtils.isBlank(hpm.getEmiYn()) ? "N" : hpm.getEmiYn();
//		String instalment=v1.getQuoteDetails().getInstallmentMonth();
		String instalment = "";
		String noOFIns = "";
		List<BranchMaster> branchCode = ratingutil.collectBranchMaster(hpm.getCompanyId(), hpm.getBranchCode());

		if (emiYn.equalsIgnoreCase("Y") && StringUtils.isBlank(endttypeid)) {
			List<EmiTransactionDetails> emiDetails = emiRepo
					.findByQuoteNoOrderByInstalmentAsc(request.getQuoteno());
			if (emiDetails != null ) {
				noOFIns = emiDetails.get(0).getInstalment();
			}
			instalment = hpm.getNoOfInstallment();
		}
		System.out.println(request.getQuoteno() + "EmiYN :" + emiYn + "\n NoOFIns from EmiTransactionDEtails :"
				+ noOFIns + " \n Installment from HMP :" + instalment);
		// Not endt
		if (StringUtils.isBlank(endttypeid) && (hpm.getPolicyNo()== null || hpm.getPolicyNo().isEmpty())) {
			List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
            List<ProductSectionMaster> coreappcode = ratingutil.collectSectionMaster(
                    hpm.getCompanyId(), hpm.getProductId().toString(), sections.get(0).getSectionId());

			List<MotorDataDetails> list = motorRepo.findByQuoteNo(request.getQuoteno());

			PersonalInfo pi = piRepo.findByCustomerId(hpm.getCustomerId());
			String vehUsageCoreappcode = "";

//	 	if(request.getProductId().equalsIgnoreCase("5"))		 	
//	 		vehUsageCoreappcode = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "MADISON_MOTOR", list.get(0).getMotorUsage(), pi.getPolicyHolderType());	 	
//	 	
//	 	  if(request.getInsuranceId().equalsIgnoreCase("100004")) {
//	 		  
//	 		 String itemvalue = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "POLICY_NO");
//	 		  
//	 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode(), request.getInsuranceId(), vehUsageCoreappcode, request.getProductId(), itemvalue);
//	 		 
//	 	  }else {
//	 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
//	 	  }
			// Generate Policy Seq
			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
			generateSeqReq.setInsuranceId(hpm.getCompanyId());
			generateSeqReq.setProductId(hpm.getProductId().toString());
			generateSeqReq.setType("5");
			generateSeqReq.setTypeDesc("POLICY_NO");
			List<String> params = new ArrayList<String>();
			params.add(hpm.getQuoteNo());
			generateSeqReq.setParams(params);
			policyNo = genNo.generateSeqCall(generateSeqReq);
			policyNo = policyNo.replaceAll(" ", "");
			request.setPolicyNo(policyNo);
		} else { // endt

			request.setPolicyNo(hpm.getPolicyNo());
            policyNo = hpm.getPolicyNo();
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return policyNo;
}



}
