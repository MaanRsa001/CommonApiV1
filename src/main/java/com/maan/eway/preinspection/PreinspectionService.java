package com.maan.eway.preinspection;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PolicyDrcrResWithPolicyNo;
import com.maan.eway.common.service.impl.PaymentServiceImpl;
import com.maan.eway.common.service.impl.TiraIntegerationServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.ApiIntegMasterRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.res.SuccessRes;

import io.micrometer.common.util.StringUtils;

@Service
public class PreinspectionService {

    @Autowired
    private PaymentServiceImpl paymentService;

    @Autowired
    private PaymentDetailRepository paymentDetailRepo;

    @Autowired
    private PaymentInfoRepository paymentInfoRepo;

    @Autowired
    private HomePositionMasterRepository homerepo;
    
    @Autowired
	private  TiraIntegerationServiceImpl tiraService;
    
    @Autowired
	private EserviceCustomerDetailsRepository eserviceCustomer;
    
    @Autowired
	private NotificationService notiService;

	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private InsuranceCompanyMasterRepository insuranceRepo;
	
	@Autowired
	private CompanyProductMasterRepository cpmRepo;
	
	@Autowired
	private PersonalInfoRepository perso;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo ;
	
	@Autowired
	private PreinspectionApiTxnRepository preinspectionTxnRepo;
	
	@Autowired
	private ApiIntegMasterRepository apiIntegRepo;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
    
	@Transactional
	public PolicyIssueRes generatePolicyPushTiraAndNotify(
	        String quoteNo,
	        String token) {
		
		System.out.println("Entering generatePolicyPushTiraAndNotify");

	    String policyNo = null;
	    String stickerNo = null;
	    String debitNo = null;
	    String creditNo = null;

	    String finalStatus = "POLICY_FAILED";

	    try {
	        /* =========================
	         * 1. PAYMENT VALIDATION
	         * ========================= */
	        PaymentInfo paymentInfo =
	                paymentInfoRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

	        PaymentDetail paymentDetail =
	                paymentDetailRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

	        if (paymentInfo == null
	                || !"ACCEPTED".equalsIgnoreCase(paymentInfo.getPaymentStatus())
	                || paymentDetail == null
	                || !"ACCEPTED".equalsIgnoreCase(paymentDetail.getPaymentStatus())) {
	        	
	        	System.out.println("Payment not done");

	            return buildAndNotify(
	                    quoteNo, null, null, null, null, "POLICY_FAILED"
	            );
	        }

	        /* =========================
	         * 2. CHECK EXISTING POLICY
	         * ========================= */
	        HomePositionMaster home =
	                homerepo.findByQuoteNo(quoteNo);

	        if (home != null && StringUtils.isNotBlank(home.getPolicyNo())) {

	            policyNo = home.getPolicyNo();
	            System.out.println("policy number is already there "+ policyNo );

	        } else {

	            /* =========================
	             * 3. GENERATE POLICY
	             * ========================= */
	            PaymentDetailsSaveReq req = new PaymentDetailsSaveReq();
	            req.setQuoteNo(quoteNo);
	            req.setCreatedBy(paymentDetail.getCreatedBy());

	          
	                    PolicyDrcrResWithPolicyNo policyNew = paymentService.generatePolicyNew(
	                            paymentInfo, req, paymentDetail, token
	                    );
	                    List<PolicyDrcrDetail> policyDetails = policyNew.getPolicyDetails();
	                    if (CollectionUtils.isEmpty(policyDetails)) {
	            	System.out.println("policy number is failed");
	                return buildAndNotify(
	                        quoteNo, null, null, null, null, "POLICY_FAILED"
	                );
	                
	            }

	            System.out.println("policy number  "+ policyNo );
	            PolicyDrcrDetail drcr = policyDetails.get(0);
	            policyNo = drcr.getPolicyNo();
//	            debitNo = drcr.getDebitNoteNo();
//	            creditNo = drcr.getCreditNoteNo();
	        }

	        /* =========================
	         * 4. CHECK EXISTING STICKER
	         * ========================= */
	        stickerNo =
	                sectionDataRepo.findStickerNumberByQuoteNo(quoteNo);

	        /* =========================
	         * 5. PUSH TIRA ONLY IF NEEDED
	         * ========================= */
	        if (StringUtils.isBlank(stickerNo)) {

	            try {
	                TiraFrameReqCall tiraReq = new TiraFrameReqCall();
	                tiraReq.setQuoteNo(quoteNo);
	                tiraReq.setLocationId(home.getNoOfVehicles() != null ? String.valueOf(home.getNoOfVehicles()) : "1");
	                tiraReq.setRiskId(home.getNoOfVehicles() != null ? String.valueOf(home.getNoOfVehicles()) : "1");  
	                tiraReq.setSectionId(home.getSectionId() != null ? String.valueOf(home.getSectionId()) : null);   
	                //	                SuccessRes tiraRes =null;
	                
	                SuccessRes tiraRes =
	                        tiraService.callTiraIntegeration(tiraReq, token);

	                if (tiraRes != null && StringUtils.isNotBlank(tiraRes.getStickerNo())) {
	                    stickerNo = tiraRes.getStickerNo();
	                    finalStatus = "SUCCESS";
	                    System.out.println("Sticker number is success "+ stickerNo );
	                } else {
	                    finalStatus = "STICKER_FAILED";
	                    System.out.println("Sticker number is Failed");
	                }

	            } catch (Exception e) {
	                finalStatus = "STICKER_FAILED";
	                System.out.println("Sticker number is Failed" + e.getMessage());
	            }

	        } else {
	            finalStatus = "SUCCESS";
	        }

	        return buildAndNotify(
	                quoteNo,
	                policyNo,
	                debitNo,
	                creditNo,
	                stickerNo,
	                finalStatus
	        );

	    } catch (Exception ex) {
	        return buildAndNotify(
	                quoteNo, null, null, null, null, "POLICY_FAILED"
	        );
	    }
	}


	
	private PolicyIssueRes buildAndNotify(
	        String quoteNo,
	        String policyNo,
	        String debitNo,
	        String creditNo,
	        String stickerNo,
	        String status) {
		System.out.println("Send sms block sendSmsNotification");

	    sendSmsNotification(
	            quoteNo,
	            policyNo,
	            stickerNo,
	            status
	    );

	    PolicyIssueRes res = new PolicyIssueRes();
	    res.setPolicyNo(policyNo);
	    res.setDebitNoteNo(debitNo);
	    res.setCreditNoteNo(creditNo);
	    res.setStickerNo(stickerNo);
	    res.setStatus(status);

	    return res;
	}


    
    
    private void sendSmsNotification(
            String quoteNo,
            String policyNo,
            String stickerNo,
            String status) {

        Notification n = new Notification();
        
        HomePositionMaster home =
                homerepo.findByQuoteNo(quoteNo);

        if (home == null || StringUtils.isBlank(home.getCustomerId())) {
            throw new RuntimeException("Customer not found for quote : " + quoteNo);
        }
        
        PersonalInfo customerDetails = perso.findByCustomerId(home.getCustomerId() );
        
        if (customerDetails == null) {
            throw new RuntimeException("Customer personal info not found for customerId : "
                    + home.getCustomerId());
        }

        Broker brokerReq = new Broker();
		LoginUserInfo loginInfo = loginUserRepo.findByLoginId(home.getLoginId());
		brokerReq.setBrokerCompanyName(loginInfo.getCompanyName() == null ? null : loginInfo.getCompanyName());
		brokerReq.setBrokerMailId(customerDetails.getEmail1() == null ? "" : "test@gmail.com");
		brokerReq.setBrokerMessengerCode(
				customerDetails.getMobileCode1() == null && customerDetails.getMobileCode1().isEmpty() ? null : Integer.valueOf((255)));
		brokerReq.setBrokerMessengerPhone(
				customerDetails.getMobileNo1() == null  && customerDetails.getMobileNo1().isEmpty() ? BigDecimal.ZERO : new BigDecimal(99999999));
		brokerReq.setBrokerPhoneCode(
				customerDetails.getMobileCode1() == null && customerDetails.getMobileCode1().isEmpty() ? null : Integer.valueOf((255)));
		brokerReq.setBrokerPhoneNo(
				customerDetails.getMobileNo1() == null  && customerDetails.getMobileNo1().isEmpty() ? BigDecimal.ZERO : new BigDecimal(99999999));
		brokerReq.setBrokerName(customerDetails.getMobileNo1());
		
		
		Customer cusReq = new Customer();
		
		cusReq.setCustomerMailid(customerDetails.getEmail1() != null && !customerDetails.getEmail1().isEmpty() ? customerDetails.getEmail1() : null);
		cusReq.setCustomerName("Customer");
		cusReq.setCustomerPhoneCode(
				customerDetails.getMobileCode1()!= null && !customerDetails.getMobileCode1().isEmpty() ? Integer.valueOf(customerDetails.getMobileCode1())
						: null);
		cusReq.setCustomerPhoneNo(
				customerDetails.getMobileNo1() != null && !customerDetails.getMobileNo1().isEmpty() ? new BigDecimal(customerDetails.getMobileNo1())
						: null);
		cusReq.setCustomerMessengerCode(
				customerDetails.getMobileCode1()!= null && !customerDetails.getMobileCode1().isEmpty() ? Integer.valueOf(customerDetails.getMobileCode1())
						: null);
		cusReq.setCustomerMessengerPhone(
				customerDetails.getMobileNo1() != null && !customerDetails.getMobileNo1().isEmpty() ? new BigDecimal(customerDetails.getMobileNo1())
						: null);
		cusReq.setCustomerRefno(customerDetails.getCustomerId() != null && !customerDetails.getCustomerId().isEmpty() ? customerDetails.getCustomerId() : null);

		List<InsuranceCompanyMaster> company = insuranceRepo.findByCompanyIdOrderByAmendIdDesc(home.getCompanyId());

		
		n.setUnderwriters(null);
		//Company Info
		n.setCompanyid(home.getCompanyId());
		n.setCompanyName(company.get(0).getCompanyName());
		n.setStatusMessage(null);
		n.setNotifPriority(0);
		n.setPolicyNo(policyNo);
		List<CompanyProductMaster> products = cpmRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(home.getCompanyId(), home.getProductId().intValue());
		n.setProductid(home.getProductId().intValue());
		n.setProductName(products.get(0).getProductName());
		n.setQuoteNo(null);
		n.setSectionName(null);
		n.setOtp(Integer.parseInt("0"));
		n.setRefNo(home.getRequestReferenceNo());
		n.setBranchCode(null);
		
		
		n.setBroker(brokerReq);
        n.setCustomer(cusReq);
        n.setQuoteNo(quoteNo);
        n.setPolicyNo(policyNo);

        if ("POLICY_FAILED".equals(status)) {
            n.setNotifTemplatename("POLICY_GENERATION_FAILED");
        }
        else if ("STICKER_FAILED".equals(status)) {
            n.setNotifTemplatename("POLICY_SUCCESS_STICKER_FAILED");
            n.setPolicyNo(policyNo);
        }
        else if ("SUCCESS".equals(status)) {
            n.setNotifTemplatename("POLICY_AND_STICKER_SUCCESS");
            n.setPolicyNo(policyNo);
            n.setOtp(Integer.valueOf(stickerNo));
        
        }

        n.setNotifcationDate(new Date());
        n.setNotifPushedStatus(NotificationStatus.PENDING);
        
        System.out.println("Entering push notification in generate policy method for preinspection");
        notiService.pushNotification(n);
    }
    
    
    @Transactional
    public boolean pushPreinspectionDetailsByQuote(
            String quoteNo,
            CommonRes res) {

        PaymentDetail paymentDetail =
                paymentDetailRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

        if (paymentDetail == null) {
            res.setIsError(true);
            res.setErrorMessage(
                    buildError(
                            "PAYMENT_NOT_FOUND",
                            "QUOTE_NO",
                            "Payment details not found for quote " + quoteNo
                    )
            );
            res.setMessage("Failed");
            return false;
        }

        MotorDataDetails motor =
                motorRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

        if (motor == null) {
            res.setIsError(true);
            res.setErrorMessage(
                    buildError(
                            "MOTOR_DATA_NOT_FOUND",
                            "QUOTE_NO",
                            "Motor data not found for quote " + quoteNo
                    )
            );
            res.setMessage("Failed");
            return false;
        }

        HomePositionMaster home =
                homerepo.findByQuoteNo(quoteNo);

        if (home == null || StringUtils.isBlank(home.getCustomerId())) {
            res.setIsError(true);
            res.setErrorMessage(
                    buildError(
                            "CUSTOMER_NOT_FOUND",
                            "CUSTOMER_ID",
                            "Customer not found for quote " + quoteNo
                    )
            );
            res.setMessage("Failed");
            return false;
        }

        PersonalInfo customer =
        		perso.findByCustomerId(home.getCustomerId());

        if (customer == null) {
            res.setIsError(true);
            res.setErrorMessage(
                    buildError(
                            "CUSTOMER_PERSONAL_NOT_FOUND",
                            "CUSTOMER_ID",
                            "Customer personal info not found"
                    )
            );
            res.setMessage("Failed");
            return false;
        }

        // All validations passed → push API
        pushPreinspectionDetails(quoteNo, paymentDetail);

        return true;
    }



    
    private void pushPreinspectionDetails(String quoteNo, PaymentDetail paymentDetail) {
    	
    	List<Error> error = new ArrayList<Error>();

        MotorDataDetails motor =
                motorRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

        HomePositionMaster home = homerepo.findByQuoteNo(quoteNo);
        PersonalInfo customer =
        		perso.findByCustomerId(home.getCustomerId());

        PreinspectionReq req = new PreinspectionReq();

        req.setChassisNo(motor.getChassisNumber());
        req.setRegistrationNo(motor.getRegistrationNumber());
        req.setQuoteNo(quoteNo);
        req.setCustomerName(customer.getClientName());
        req.setMobileNo(customer.getMobileNo1());

        req.setPolicyStartDate(formatDate(motor.getPolicyStartDate()));
        req.setPolicyEndDate(formatDate(motor.getPolicyEndDate()));

        req.setProductId(String.valueOf(motor.getProductId()));
        req.setProductName(motor.getProductName());
        req.setSectionId(String.valueOf(motor.getSectionId()));
        req.setSectionName(motor.getSectionName());
        req.setCompanyId(motor.getCompanyId());

        req.setEntryDate(formatDate(new Date()));
        req.setPremium(paymentDetail.getPremiumLc().toPlainString());

        callPreinspectionApi(req,motor.getCompanyId(),motor.getProductId(), error);
    }

private void callPreinspectionApi(
        PreinspectionReq req,
        String companyId,
        Integer productId,
        List<Error> errors) {

    String apiUrl = getPreinspectionApiUrl(companyId, productId, errors);
    if (apiUrl == null) {
        return;
    }

    PreinspectionApiTxn txn = new PreinspectionApiTxn();
    txn.setQuoteNo(req.getQuoteNo());
    System.out.println(apiUrl);
    txn.setApiUrl(apiUrl);
    txn.setRequestPayload(toJson(req));
    txn.setCalledYn("Y");
    txn.setCreatedDate(new Date());

    txn = preinspectionTxnRepo.save(txn);

    try {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("whatsappchatapi", "whatsappchatapi@123#");

        HttpEntity<PreinspectionReq> entity =
                new HttpEntity<>(req, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        txn.setResponsePayload(response.getBody());
        txn.setHttpStatus(response.getStatusCode().toString());
        txn.setApiStatus("SUCCESS");

    } catch (Exception ex) {

        txn.setApiStatus("FAILED");
        txn.setErrorMessage(ex.getMessage());

        errors.add(new Error(
                "PREINSPECTION_API_CALL_FAILED",
                "PREINSPECTION_API",
                ex.getMessage()
        ));
    }

    txn.setUpdatedDate(new Date());
    preinspectionTxnRepo.save(txn);
}

	
	private String toJson(Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        return null;
	    }
	}

	private String formatDate(Date date) {
	    return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS").format(date);
	}
	
	private List<Error> buildError(String code, String field, String message) {

	    Error err = new Error();
	    err.setCode(code);
	    err.setField(field);
	    err.setMessage(message);

	    // Optional – if you support localization later
	    err.setFieldLocal(field);
	    err.setMessageLocal(message);

	    return Collections.singletonList(err);
	}


	
	private String getPreinspectionApiUrl(
	        String companyId,
	        Integer productId,
	        List<Error> errors) {

	    List<ApiIntegMaster> apis =
	            apiIntegRepo.findAllByCompanyIdAndProductId(companyId, productId);

	    return apis.stream()
	            .filter(a -> "PREINSPECTION".equalsIgnoreCase(a.getApiType()))
	            .filter(a -> "Y".equalsIgnoreCase(a.getStatus()))
	            .map(ApiIntegMaster::getApiUrl)
	            .findFirst()
	            .orElseGet(() -> {
	                errors.add(new Error(
	                        "PREINSPECTION_API_NOT_FOUND",
	                        "API_INTEG_MASTER",
	                        "Preinspection API URL not configured"
	                ));
	                return null;
	            });
	}


}
