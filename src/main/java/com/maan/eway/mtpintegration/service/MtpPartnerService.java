package com.maan.eway.mtpintegration.service;


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.MotorVehicleUsageMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.mtpintegration.dto.CommonPartnerResponse;
import com.maan.eway.mtpintegration.dto.InitiatePaymentRequest;
import com.maan.eway.mtpintegration.dto.InitiatePaymentResponse;
import com.maan.eway.mtpintegration.dto.MtpPolicyReq;
import com.maan.eway.mtpintegration.dto.PaymentStatusRequest;
import com.maan.eway.mtpintegration.dto.PaymentStatusResponse;
import com.maan.eway.mtpintegration.dto.PolicyObject;
import com.maan.eway.mtpintegration.dto.PolicyResponse;
import com.maan.eway.mtpintegration.dto.TokenResponse;
import com.maan.eway.mtpintegration.dto.ValidateVrnRequest;
import com.maan.eway.mtpintegration.dto.VehicleValidationResponse;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorVehicleUsageMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.service.impl.ApiIntegrationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MtpPartnerService {

    private static final Logger log = LoggerFactory.getLogger(MtpPartnerService.class);

    private final VehicleValidationDbService vehicleValidationDbService;
    private final MtpAuthService authService;
    private final MtpExternalApiClient externalApiClient;
    private final MtpAuditService auditService;
    private final MtpErrorLogService errorLogService;
    private final MtpPaymentTransactionService paymentTransactionService;
	private final MtpPolicyDetailsService policyDetailsService;
	private final MotorMasterDataWriter motorMasterDataWriter;
	private final EServiceMotorDetailsRepository eserviceMotorDetailsRepository;
	private final PolicyCoverDataRepository policyCoverDataRepository ;
	private final FactorRateRequestDetailsRepository factorRateRequestDetailsRepository ;
	private final MotorDataDetailsRepository motorDataDetailsRepository;
	private final MotorVehicleUsageMasterRepository motorVehicleusageMasterRepository;
	private final PolicyDrcrDetailRepository policyDrcrDetailRepository;
	
	@Autowired
	private ApiIntegrationService apiIntegrationService;
	
	private static final String COMPANY_ID = "100019";
	private static final Integer PRODUCT_ID = 125;

	
    
    @PersistenceContext
    private EntityManager em;

    private static final Set<String> AUTO_INSERT_COMPANIES = Set.of("100002", "100019");
    private static final Integer PLACEHOLDER_BODY_ID = 0; // used when inserting a new model with no body-type match


    public CommonRes validateVrn(ValidateVrnRequest request) {
    	
    	request.setAssessmentType("M");

        // NEW: kick off POLICY_DETAILS in parallel, right before VALIDATE_VRN starts
        CompletableFuture<PolicyResponse> policyFuture = CompletableFuture.supplyAsync(() -> {
            try {
                MtpPolicyReq policyReq = new MtpPolicyReq();
                policyReq.setVrn(request.getNumberPlate());
                policyReq.setPartnerIdentifier("ALLIANCE");
                return policyDetails(policyReq);
            } catch (Exception e) {
                log.error("Policy details enrichment failed for vrn={}", request.getNumberPlate(), e);
                return null;
            }
        });

        // --- VALIDATE_VRN call happens on the main thread, exactly as before ---
        CommonPartnerResponse partnerResponse = callPartnerApi(
                "VALIDATE_VRN",
                request,
                CommonPartnerResponse.class);

        VehicleValidationResponse response = new VehicleValidationResponse();

        if (partnerResponse == null) {
            response.setReturnCode(100);
        } else {
            response.setReturnCode(partnerResponse.getReturnCode());
            response.setRawReturnMessage(partnerResponse.getReturnMessage());

            boolean isSuccess = partnerResponse.getReturnCode() != null && partnerResponse.getReturnCode() == 0;

            if (isSuccess) {
                parseValidationMessage(partnerResponse.getReturnMessage(), response);

                String insuranceId = "100019";
                String branchCode = "99999";

                String makeId = null;
                String makeNameEn=null;
                if (StringUtils.isNotBlank(response.getMake())) {
                    MasterLookupResult makeResult = getMotorMakeId(insuranceId, branchCode, response.getMake());
                    if (makeResult != null) {
                        makeId = makeResult.id;
                        if (StringUtils.isNotBlank(makeResult.name)) {
                            response.setMake(makeResult.name);
                            makeNameEn=makeResult.name;
                        }
                    }
                }

                if (StringUtils.isNotBlank(response.getModel()) && StringUtils.isNotBlank(makeId)) {
                    MasterLookupResult modelResult = getMotorModelId(insuranceId, branchCode, makeId, response.getModel(),makeNameEn);
                    if (modelResult != null && StringUtils.isNotBlank(modelResult.name)) {
                        response.setModel(modelResult.name);
                    }
                }
            }
        }

        try {
            List<EserviceMotorDetails> motorDetailsList =
                    eserviceMotorDetailsRepository.findByRegistrationNumber(request.getNumberPlate());

            if (motorDetailsList != null && !motorDetailsList.isEmpty()) {
                EserviceMotorDetails motorDetails = motorDetailsList.get(0);
                response.setCustomerReferenceNo(motorDetails.getCustomerReferenceNo());
            } else {
                log.warn("No eservice_motor_details record found for numberPlate={}", request.getNumberPlate());
            }
        } catch (Exception e) {
            log.error("Failed to fetch customerReferenceNo from eservice_motor_details for numberPlate={}",
                    request.getNumberPlate(), e);
        }

        // NEW: wait for the policy-details call, which has been running in parallel since the start of this method
        try {
            PolicyResponse policyResponse = policyFuture.orTimeout(15, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        log.error("Policy details future failed for vrn={}", request.getNumberPlate(), ex);
                        return null;
                    }).join();

            if (policyResponse != null && policyResponse.getReturnObject() != null) {
                response.setPolicyStartDate(policyResponse.getReturnObject().getStartDate());
                response.setPolicyEndDate(policyResponse.getReturnObject().getEndDate());
            }
        } catch (Exception e) {
            log.error("Policy details enrichment failed for vrn={}", request.getNumberPlate(), e);
        }

        try {
            vehicleValidationDbService.saveValidation(request, response);
        } catch (Exception e) {
            log.error("Failed to save vehicle validation for numberPlate={}", request.getNumberPlate(), e);
        }

        return wrapResponse(response);
    }

    private CommonRes wrapResponse(VehicleValidationResponse response) {
        CommonRes commonRes = new CommonRes();
        boolean isError = response.getReturnCode() != null && response.getReturnCode() != 0;
        commonRes.setIsError(isError);
        commonRes.setErroCode(response.getReturnCode() == null ? 0 : response.getReturnCode());

        if (isError) {
            commonRes.setMessage("Failed");
            if (StringUtils.isNotBlank(response.getRawReturnMessage())) {
                Error error = new Error();
                error.setCode(String.valueOf(response.getReturnCode()));
                error.setMessage(response.getRawReturnMessage()); 
                commonRes.setErrorMessage(java.util.List.of(error));
            }
        } else {
            commonRes.setMessage("Success");
        }

        commonRes.setCommonResponse(response);
        return commonRes;
    }
    
    public PolicyResponse policyDetails(MtpPolicyReq request) {

        PolicyResponse response = callPolicyApi(
                "POLICY_DETAILS",
                request,
                PolicyResponse.class);

        if (response == null) {
            response = new PolicyResponse();
            response.setReturnCode(100);
            response.setReturnMessage("Not Found from MTP Policy Details API");
        }

        policyDetailsService.savePolicyDetails(request, response);

        return response;
    }
	
    @SuppressWarnings("unchecked")
    private <T> T callPolicyApi(String apiType, Object request, Class<T> responseType) {

        try {

            // Fetch complete URL from DB
            String url = apiIntegrationService.getApiUrl(
                    COMPANY_ID,
                    PRODUCT_ID,
                    apiType);

            log.info("MTP Partner API Started. apiType={}, url={}", apiType, url);

            String token = authService.getValidAccessToken();

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(token);

            HttpEntity<Object> entity = new HttpEntity<>(request, headers);

            log.info("Request URL : {}", url);
            log.info("Request     : {}", request);

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType);

            auditService.save(apiType, request, response.getBody(), "SUCCESS");

            return response.getBody();

        } catch (RestClientResponseException ex) {

            if (ex.getRawStatusCode() == 401) {
                authService.invalidateStoredToken();
            }

            auditService.save(apiType, request, ex.getResponseBodyAsString(), "FAILED");
            errorLogService.save(apiType, ex);

            throw ex;

        } catch (RuntimeException ex) {

            auditService.save(apiType, request, ex.getMessage(), "FAILED");
            errorLogService.save(apiType, ex);

            throw ex;
        }
    }

    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request) {
        InitiatePaymentResponse response = null;
//        );
//
//        if (response == null) {
//            response = new InitiatePaymentResponse();
//            response.setReturnCode(100);
//            response.setReturnMessage("No response from MTP initiate payment API");
//        }
//
//        if (response.getReturnMessage() != null) {
//            response.setReturnMessage(response.getReturnMessage().replace("\\n", System.lineSeparator()));
//        }
//
//        paymentTransactionService.saveInitiated(request, response);
        return response;
    }

    public PaymentStatusResponse paymentStatus(PaymentStatusRequest request) {
        PaymentStatusResponse response = null;
        if (response == null) {
            response = new PaymentStatusResponse();
            response.setReturnCode(0);
            response.setPaymentStatus("PENDING");
            response.setReturnMessage("Payment status not received. Please check again.");
        }

        enrichPaymentStatusResponse(response);
        paymentTransactionService.updateStatus(request, response);
        return response;
    }

    private void enrichPaymentStatusResponse(PaymentStatusResponse response) {
        String status = response.getPaymentStatus() == null ? "" : response.getPaymentStatus().trim().toUpperCase();

        if ("COMPLETED".equals(status)) {
            response.setNextAction("DOWNLOAD_STICKER");
            response.setUserMessage("Payment completed. Sticker is ready to download.");
        } else if ("FAILED".equals(status)) {
            response.setNextAction("STOP");
            response.setUserMessage(firstNonBlank(response.getReturnMessage(), "Payment failed."));
        } else if ("APPROVED".equals(status)) {
            response.setNextAction("CHECK_PAYMENT_STATUS");
            response.setUserMessage("Payment approved. Sticker generation is in progress.");
        } else {
            response.setPaymentStatus(firstNonBlank(response.getPaymentStatus(), "PENDING"));
            response.setNextAction("CHECK_PAYMENT_STATUS");
            response.setUserMessage("Payment is still pending. Please check again after some time.");
        }
    }

    private <T> T callPartnerApi(String apiType, Object request, Class<T> responseType) {
        try {
            log.info("MTP partner API started. apiName={}, path={}", apiType);
            
            String url = apiIntegrationService.getApiUrl(
                    COMPANY_ID,
                    PRODUCT_ID,
                    apiType);
            
            
            TokenResponse tokenResponse = authService.getToken();
            
            if (tokenResponse == null
                    || tokenResponse.getAccessToken() == null
                    || tokenResponse.getAccessToken().isBlank()) {
                throw new IllegalStateException("Failed to get MTP access token");
            }

            String token = tokenResponse.getAccessToken();

            T response = externalApiClient.postWithToken(
                    url,
                    token,
                    request,
                    responseType);
            
            auditService.save(apiType, request, response, "SUCCESS");
            return response;
        } catch (RestClientResponseException ex) {
            if (ex.getRawStatusCode() == 401) {
                authService.invalidateStoredToken();
            }
            auditService.save(apiType, request, ex.getResponseBodyAsString(), "FAILED");
            errorLogService.save(apiType, ex);
            throw ex;
        } catch (RuntimeException ex) {
            auditService.save(apiType, request, ex.getMessage(), "FAILED");
            errorLogService.save(apiType, ex);
            throw ex;
        }
    }

//    private void parseValidationMessage(String returnMessage, VehicleValidationResponse response) {
//        if (returnMessage == null || returnMessage.trim().isEmpty()) {
//            return;
//        }
//        String[] lines = returnMessage.split("\\n");
//        if (lines.length > 0) response.setAmount(lines[0].trim());
//        if (lines.length > 1) response.setVehicleNo(lines[1].replace("Vehicle No:", "").replace(":", "").trim());
//        if (lines.length > 2) response.setName(lines[2].replace("Name:", "").trim());
//        if (lines.length > 3) response.setEngineSize(lines[3].replace("Engine Size:", "").trim());
//        if (lines.length > 4) response.setMake(lines[4].replace("Make:", "").trim());
//        if (lines.length > 5) response.setModel(lines[5].replace("Model:", "").trim());
//        if (lines.length > 6) response.setService(lines[6].replace("Service:", "").trim());
//    }
    
    private void parseValidationMessage(String returnMessage, VehicleValidationResponse response) {
        if (returnMessage == null || returnMessage.trim().isEmpty()) {
            return;
        }
        String[] lines = returnMessage.split("\\n");
        if (lines.length > 0) response.setAmount(extractAmount(lines[0].trim()));
        if (lines.length > 1) response.setVehicleNo(lines[1].replace("Vehicle No:", "").replace(":", "").trim());
        if (lines.length > 2) response.setName(lines[2].replace("Name:", "").trim());
        if (lines.length > 3) response.setEngineSize(lines[3].replace("Engine Size:", "").trim());
        if (lines.length > 4) response.setMake(lines[4].replace("Make:", "").trim());
        if (lines.length > 5) response.setModel(lines[5].replace("Model:", "").trim());
        if (lines.length > 6) response.setService(lines[6].replace("Service:", "").trim());
    }

    private String extractAmount(String line) {
        if (line == null) {
            return null;
        }
        
        java.util.regex.Matcher matcher =
                java.util.regex.Pattern.compile("[\\d,]+(?:\\.\\d+)?").matcher(line);
        if (matcher.find()) {
            return matcher.group().replace(",", "");
        }
        return line; 
    }
    
    

    private String firstNonBlank(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }
    
    private MasterLookupResult getMotorMakeId(String insuranceId, String branchCode, String makeDesc) {
        String makeId = "";
        String canonicalName = makeDesc; 
        try {
            Date today = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);
            today = cal.getTime();
            Date todayEnd = cal.getTime();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);
            Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);
            query.select(c);

            Subquery<Date> effStart = query.subquery(Date.class);
            Root<MotorMakeMaster> s1 = effStart.from(MotorMakeMaster.class);
            effStart.select(cb.greatest(s1.get("effectiveDateStart").as(Date.class)));
            effStart.where(
                    cb.equal(c.get("makeId"), s1.get("makeId")),
                    cb.lessThanOrEqualTo(s1.get("effectiveDateStart"), today),
                    cb.equal(c.get("companyId"), s1.get("companyId")),
                    cb.equal(c.get("branchCode"), s1.get("branchCode"))
            );

            Subquery<Date> effEnd = query.subquery(Date.class);
            Root<MotorMakeMaster> s2 = effEnd.from(MotorMakeMaster.class);
            effEnd.select(cb.greatest(s2.get("effectiveDateEnd").as(Date.class)));
            effEnd.where(
                    cb.equal(c.get("makeId"), s2.get("makeId")),
                    cb.greaterThanOrEqualTo(s2.get("effectiveDateEnd"), todayEnd),
                    cb.equal(c.get("companyId"), s2.get("companyId")),
                    cb.equal(c.get("branchCode"), s2.get("branchCode"))
            );

            Predicate statusOk = cb.or(cb.equal(c.get("status"), "Y"), cb.equal(c.get("status"), "R"));
            Predicate startOk = cb.equal(c.get("effectiveDateStart"), effStart);
            Predicate endOk = cb.equal(c.get("effectiveDateEnd"), effEnd);
            Predicate companyOk = cb.equal(c.get("companyId"), insuranceId);
            Predicate branchOk = cb.or(cb.equal(c.get("branchCode"), branchCode), cb.equal(c.get("branchCode"), "99999"));

            Expression<String> cleanedMakeNameEn = cb.function("REGEXP_REPLACE", String.class,
                    cb.lower(c.get("makeNameEn")), cb.literal("[^a-zA-Z0-9]"), cb.literal(""));
            Predicate nameOk = cb.equal(
                    cb.function("REPLACE", String.class, cleanedMakeNameEn, cb.literal(" "), cb.literal("")),
                    makeDesc.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().replace(" ", "")
            );

            query.where(statusOk, startOk, endOk, companyOk, branchOk, nameOk)
                 .orderBy(cb.asc(c.get("makeNameEn")));

            List<MotorMakeMaster> list = em.createQuery(query).getResultList();
            if (!list.isEmpty()) {
                makeId = list.get(0).getMakeId().toString();
                canonicalName = list.get(0).getMakeNameEn();
            }

            if (StringUtils.isBlank(makeId)
                    && AUTO_INSERT_COMPANIES.contains(insuranceId)
                    && isLikelyName(makeDesc)) {
                makeId = motorMasterDataWriter.insertMotorMakeWithMaxId(insuranceId, makeDesc);
                canonicalName = makeDesc.trim(); // newly inserted row uses the third-party desc as-is
            }
        } catch (Exception e) {
            log.error("MAKE LOOKUP FAILED | insuranceId={} | makeDesc='{}'", insuranceId, makeDesc, e);
            return null;
        }
        return new MasterLookupResult(makeId, canonicalName);
    }

    private boolean isLikelyName(String value) {
        return value != null && value.matches(".*[A-Za-z].*");
    }

    @Transactional
    private String insertMotorMakeWithMaxId(String insuranceId, String makeDesc) {
        if (!AUTO_INSERT_COMPANIES.contains(insuranceId)) {
            return null;
        }

        log.error("AUTO MAKE INSERT TRIGGERED | insuranceId={} | makeDesc='{}'", insuranceId, makeDesc);

        Integer nextMakeId = getNextMakeId();

        MotorMakeMaster make = new MotorMakeMaster();
        make.setMakeId(nextMakeId);
        make.setCompanyId("100019");
        make.setBranchCode("99999");
        make.setMakeNameEn(makeDesc.trim());
        make.setMakeNameLocal(makeDesc.trim());
        make.setStatus("Y");
        make.setRemarks("AUTO INSERTED FROM TIRA");
        make.setCreatedBy("SYSTEM_TIRA");
        make.setUpdatedBy("SYSTEM_TIRA");
        make.setRegulatoryCode("30873");
        make.setCoreAppCode("30873");
        make.setAmendId(0);

        Date now = new Date();
        make.setEntryDate(now);
        make.setUpdatedDate(now);
        make.setEffectiveDateStart(now);

        Calendar cal = Calendar.getInstance();
        cal.set(2049, Calendar.DECEMBER, 31);
        make.setEffectiveDateEnd(cal.getTime());

        em.persist(make);
        em.flush();

        return nextMakeId.toString();
    }

    private Integer getNextMakeId() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<MotorMakeMaster> root = cq.from(MotorMakeMaster.class);
        Expression<Long> maxExpr = cb.max(root.get("makeId").as(Long.class));
        cq.select(cb.coalesce(maxExpr, 0L));
        Long maxId = em.createQuery(cq).getSingleResult();
        return maxId.intValue() + 1;
    }


    private MasterLookupResult getMotorModelId(String insuranceId, String branchCode, String makeId, String modelDesc, String makeNameEn) {
    	String modelId = "";
        String canonicalName = modelDesc;
        try {
            Date today = new Date();
            Date todayEnd = new Date();

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
            Root<MotorMakeModelMaster> c = query.from(MotorMakeModelMaster.class);
            query.select(c);

            Subquery<Date> effStart = query.subquery(Date.class);
            Root<MotorMakeModelMaster> s1 = effStart.from(MotorMakeModelMaster.class);
            effStart.select(cb.greatest(s1.get("effectiveDateStart").as(Date.class)));
            effStart.where(
                    cb.equal(c.get("modelId"), s1.get("modelId")),
                    cb.equal(c.get("makeId"), s1.get("makeId")),
                    cb.lessThanOrEqualTo(s1.get("effectiveDateStart"), today),
                    cb.equal(c.get("companyId"), s1.get("companyId")),
                    cb.equal(c.get("branchCode"), s1.get("branchCode"))
            );

            Subquery<Date> effEnd = query.subquery(Date.class);
            Root<MotorMakeModelMaster> s2 = effEnd.from(MotorMakeModelMaster.class);
            effEnd.select(cb.greatest(s2.get("effectiveDateEnd").as(Date.class)));
            effEnd.where(
                    cb.equal(c.get("modelId"), s2.get("modelId")),
                    cb.equal(c.get("makeId"), s2.get("makeId")),
                    cb.greaterThanOrEqualTo(s2.get("effectiveDateEnd"), todayEnd),
                    cb.equal(c.get("companyId"), s2.get("companyId")),
                    cb.equal(c.get("branchCode"), s2.get("branchCode"))
            );

            Predicate statusOk = cb.or(cb.equal(c.get("status"), "Y"), cb.equal(c.get("status"), "R"));
            Predicate startOk = cb.equal(c.get("effectiveDateStart"), effStart);
            Predicate endOk = cb.equal(c.get("effectiveDateEnd"), effEnd);
            Predicate companyOk = cb.equal(c.get("companyId"), insuranceId);
            Predicate branchOk = cb.or(cb.equal(c.get("branchCode"), branchCode), cb.equal(c.get("branchCode"), "99999"));
            Predicate makeOk = cb.equal(c.get("makeId"), Integer.valueOf(makeId));

            Expression<String> cleanedModelName = cb.function("REGEXP_REPLACE", String.class,
                    cb.lower(c.get("modelNameEn")), cb.literal("[^a-zA-Z0-9]"), cb.literal(""));
            Predicate nameOk = cb.equal(
                    cb.function("REPLACE", String.class, cleanedModelName, cb.literal(" "), cb.literal("")),
                    modelDesc.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase().replace(" ", "")
            );

            query.where(statusOk, startOk, endOk, companyOk, branchOk, makeOk, nameOk)
                 .orderBy(cb.asc(c.get("modelNameEn")));

//            List<MotorMakeModelMaster> list = em.createQuery(query).setMaxResults(1).getResultList();
//            modelId = !list.isEmpty() ? list.get(0).getModelId().toString() : "";
            
            List<MotorMakeModelMaster> list = em.createQuery(query).setMaxResults(1).getResultList();
            if (!list.isEmpty()) {
                modelId = list.get(0).getModelId().toString();
                canonicalName = list.get(0).getModelNameEn(); // <-- canonical name from master table
            }

            if (StringUtils.isBlank(modelId)
                    && AUTO_INSERT_COMPANIES.contains(insuranceId)
                    && isLikelyName(modelDesc)) {
                modelId = motorMasterDataWriter.insertMotorModelWithMaxId(insuranceId, makeId, modelDesc,makeNameEn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception is --->" + e.getMessage());
            return null;
        }
        return new MasterLookupResult(modelId, canonicalName);
    }

    @Transactional
    private String insertMotorModelWithMaxId(String insuranceId, String makeId, String modelDesc,String canonicalName) {
        if (!AUTO_INSERT_COMPANIES.contains(insuranceId)) {
            return null;
        }

        log.error("AUTO MODEL INSERT TRIGGERED | insuranceId={} | makeId={} | modelDesc='{}'",
                insuranceId, makeId, modelDesc);

        Integer nextModelId = getNextModelId();

        MotorMakeModelMaster model = new MotorMakeModelMaster();
        model.setMakeId(Integer.valueOf(makeId));
        model.setMakeNameEn(canonicalName);   // <-- add this
        model.setModelId(nextModelId);
        model.setBodyId(PLACEHOLDER_BODY_ID);
        model.setCompanyId(insuranceId);
        model.setBranchCode("99999");
        model.setAmendId(0);

        model.setModelNameEn(modelDesc.trim());
        model.setModelNameLocal(modelDesc.trim());
        model.setStatus("Y");
        model.setVehManfRegion(1);
        
        model.setRemarks("AUTO INSERTED FROM TIRA");
        model.setCreatedBy("SYSTEM_TIRA");
        model.setUpdatedBy("SYSTEM_TIRA");

        Date now = new Date();
        model.setEntryDate(now);
        model.setUpdatedDate(now);
        model.setEffectiveDateStart(now);

        Calendar cal = Calendar.getInstance();
        cal.set(2049, Calendar.DECEMBER, 31);
        model.setEffectiveDateEnd(cal.getTime());

        em.persist(model);
        em.flush();

        return nextModelId.toString();
    }

    private Integer getNextModelId() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<MotorMakeModelMaster> root = cq.from(MotorMakeModelMaster.class);
        Expression<Long> maxExpr = cb.max(root.get("modelId").as(Long.class));
        cq.select(cb.coalesce(maxExpr, 0L));
        Long maxId = em.createQuery(cq).getSingleResult();
        return maxId.intValue() + 1;
    }
    
    public class MasterLookupResult {
        public final String id;
        public final String name;
        public MasterLookupResult(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    @Transactional
    public void updatePolicyCoverAndFactorRate(String quoteNo, String requestReferenceNo, PolicyObject obj) {
        if (obj == null) {
            log.warn("updatePolicyCoverAndFactorRate skipped — no PolicyObject for quoteNo={}", quoteNo);
            return;
        }

        try {
            updatePolicyCoverData(quoteNo, obj);
        } catch (Exception e) {
            log.error("Failed to update policy_cover_data for quoteNo={}", quoteNo, e);
        }

        try {
            updateFactorRateRequestDetails(requestReferenceNo, obj);
        } catch (Exception e) {
            log.error("Failed to update factor_rate_request_details for requestReferenceNo={}", requestReferenceNo, e);
        }

        try {
            updateMotorDetails(quoteNo, requestReferenceNo, obj);
        } catch (Exception e) {
            log.error("Failed to update motor details for quoteNo={}", quoteNo, e);
        }
        
    }

    private void updatePolicyCoverData(String quoteNo, PolicyObject obj) {
        List<PolicyCoverData> rows = policyCoverDataRepository.findByQuoteNo(quoteNo);
        if (rows.isEmpty()) {
            log.warn("No policy_cover_data rows found for quoteNo={}", quoteNo);
            return;
        }

        BigDecimal assessedPremium = toBigDecimal(obj.getAssessedPremium());
        BigDecimal totalAssessmentAmount = toBigDecimal(obj.getTotalAssessmentAmount());
        BigDecimal assessedVat = toBigDecimal(obj.getAssessedVat());
        BigDecimal assessedTrainingLevy = toBigDecimal(obj.getAssessedTrainingLevy());
        BigDecimal assessedStickerFees = toBigDecimal(obj.getAssessedStickerFees());
        BigDecimal assessedStampDuty = toBigDecimal(obj.getAssessedStampDuty());

        for (PolicyCoverData row : rows) {
            String coverName = row.getCoverName() == null ? "" : row.getCoverName().trim();

            if ("Base cover".equalsIgnoreCase(coverName)) {
            	row.setPremiumBeforeDiscountLc(assessedPremium);
            	row.setPremiumBeforeDiscountLc(assessedPremium);
            	row.setPremiumAfterDiscountFc(assessedPremium);
            	row.setPremiumAfterDiscountLc(assessedPremium);
                row.setPremiumExcludedTaxFc(assessedPremium);
                row.setPremiumExcludedTaxLc(assessedPremium);
                row.setPremiumIncludedTaxFc(totalAssessmentAmount);
                row.setPremiumIncludedTaxLc(totalAssessmentAmount);
            } else if ("Base cover VAT".equalsIgnoreCase(coverName)) {
                row.setTaxAmount(assessedVat);
            } else if ("Base cover TRAINING LEVY".equalsIgnoreCase(coverName)) {
                row.setTaxAmount(assessedTrainingLevy);
            } else if ("Base cover STICKER FEE".equalsIgnoreCase(coverName)) {
                row.setTaxAmount(assessedStickerFees);
            } else if ("Base cover STAMP DUTY".equalsIgnoreCase(coverName)) {
                row.setTaxAmount(assessedStampDuty);
            }
        }

        policyCoverDataRepository.saveAll(rows);
        log.info("Updated {} policy_cover_data rows for quoteNo={}", rows.size(), quoteNo);
    }

    private void updateFactorRateRequestDetails(String requestReferenceNo, PolicyObject obj) {
        List<FactorRateRequestDetails> rows =
                factorRateRequestDetailsRepository.findByRequestReferenceNo(requestReferenceNo);
        if (rows.isEmpty()) {
            log.warn("No factor_rate_request_details rows found for requestReferenceNo={}", requestReferenceNo);
            return;
        }

        BigDecimal assessedPremium = toBigDecimal(obj.getAssessedPremium());
        BigDecimal totalAssessmentAmount = toBigDecimal(obj.getTotalAssessmentAmount());
        BigDecimal assessedVat = toBigDecimal(obj.getAssessedVat());
        BigDecimal assessedTrainingLevy = toBigDecimal(obj.getAssessedTrainingLevy());
        BigDecimal assessedStickerFees = toBigDecimal(obj.getAssessedStickerFees());
        BigDecimal assessedStampDuty = toBigDecimal(obj.getAssessedStampDuty());

        for (FactorRateRequestDetails row : rows) {
            String coverName = row.getCoverName() == null ? "" : row.getCoverName().trim();
            
            Integer taxId= row.getTaxId();

            if ("Base cover".equalsIgnoreCase(coverName)) {
            	row.setPremiumBeforeDiscountLc(assessedPremium);
            	row.setPremiumBeforeDiscountLc(assessedPremium);
            	row.setPremiumAfterDiscountFc(assessedPremium);
            	row.setPremiumAfterDiscountLc(assessedPremium);
                row.setPremiumExcludedTaxFc(assessedPremium);
                row.setPremiumExcludedTaxLc(assessedPremium);
                row.setPremiumIncludedTaxFc(totalAssessmentAmount);
                row.setPremiumIncludedTaxLc(totalAssessmentAmount);
            } else if (taxId.equals(9)) {
                row.setTaxAmount(assessedVat);
            } else if (taxId.equals(10)) {
                row.setTaxAmount(assessedTrainingLevy);
            } else if (taxId.equals(11)) {
                row.setTaxAmount(assessedStickerFees);
            } else if (taxId.equals(12)) {
                row.setTaxAmount(assessedStampDuty);
            }
        }

        factorRateRequestDetailsRepository.saveAll(rows);
        log.info("Updated {} factor_rate_request_details rows for requestReferenceNo={}", rows.size(), requestReferenceNo);
    }
    
    /**
     * Updates chassis number, engine number, seating capacity, and sticker vehicle type
     * into both EserviceMotorDetails and MotorDataDetails after MTP payment confirmation.
     *
     * EserviceMotorDetails — queried by requestReferenceNo + vehicleId + locationId
     * MotorDataDetails     — queried by quoteNo (all non-deleted rows)
     */
    private void updateMotorDetails(String quoteNo, String requestReferenceNo, PolicyObject obj) {

        String chassisNumber      = obj.getChassisNumber();
        String engineNumber       = obj.getEngineNumber();
        Integer seatingCapacity   = obj.getSeatingCapacity();
        String stickerVehicleType = obj.getStickerVehicleType();
        String companyId         = "100019"; // or however companyId is resolved in this context

        // ── Resolve motor_usage / usage_id from motor_vehicleusage_master ─────────
        MotorVehicleUsageMaster usageMaster = resolveVehicleUsage(companyId, stickerVehicleType);
        Integer usageId   = usageMaster != null ? usageMaster.getVehicleUsageId() : null;
        String motorUsage = usageMaster != null ? usageMaster.getVehicleUsageDesc() : null;

        if (usageMaster == null) {
            log.warn("No matching motor_vehicleusage_master row found for stickerVehicleType={}, companyId={}",
                    stickerVehicleType, companyId);
        }

        // ── EserviceMotorDetails ─────────────────────────────────────────────────
        // vehicleId and locationId default to 1 for single-vehicle MTP product
        try {
            EserviceMotorDetails motorDetails = eserviceMotorDetailsRepository
                    .findByRequestReferenceNoAndRiskIdAndLocationId(
                            requestReferenceNo,
                            Integer.valueOf(1),   // vehicleId — MTP is always single vehicle
                            Integer.valueOf(1)    // locationId
                    );

            if (motorDetails != null) {
                motorDetails.setChassisNumber(chassisNumber);
                motorDetails.setEngineNumber(engineNumber);
                motorDetails.setSeatingCapacity(seatingCapacity);
                motorDetails.setUsageId(String.valueOf(usageId));
                motorDetails.setMotorUsage(motorUsage);
                motorDetails.setOtherVehicleDetails(stickerVehicleType);
                eserviceMotorDetailsRepository.save(motorDetails);
                log.info("Updated EserviceMotorDetails for requestReferenceNo={} — chassis={}, engine={}, seats={}, usageId={}, motorUsage={}, stickerType={}",
                        requestReferenceNo, chassisNumber, engineNumber, seatingCapacity, usageId, motorUsage, stickerVehicleType);
            } else {
                log.warn("EserviceMotorDetails not found for requestReferenceNo={}", requestReferenceNo);
            }
        } catch (Exception e) {
            log.error("Failed to update EserviceMotorDetails for requestReferenceNo={}", requestReferenceNo, e);
        }

        // ── MotorDataDetails ─────────────────────────────────────────────────────
        try {
            List<MotorDataDetails> motorDataList = motorDataDetailsRepository
                    .findByQuoteNoAndStatusNotOrderByVehicleIdAsc(quoteNo, "D");

            if (motorDataList == null || motorDataList.isEmpty()) {
                log.warn("MotorDataDetails not found for quoteNo={}", quoteNo);
                return;
            }

            for (MotorDataDetails motorData : motorDataList) {
                motorData.setChassisNumber(chassisNumber);
                motorData.setEngineNumber(engineNumber);
                motorData.setSeatingCapacity(
                        seatingCapacity != null ? seatingCapacity.doubleValue() : null
                );
                motorData.setUsageId(String.valueOf(usageId));
                motorData.setMotorUsage(motorUsage);
                motorData.setOtherVehicleDetails(stickerVehicleType);
            }

            motorDataDetailsRepository.saveAll(motorDataList);
            log.info("Updated {} MotorDataDetails rows for quoteNo={} — chassis={}, seats={}, usageId={}, motorUsage={}, stickerType={}",
                    motorDataList.size(), quoteNo, chassisNumber, seatingCapacity, usageId, motorUsage, stickerVehicleType);

        } catch (Exception e) {
            log.error("Failed to update MotorDataDetails for quoteNo={}", quoteNo, e);
        }
    }

    /**
     * Resolves the vehicle usage master row for the given sticker vehicle type.
     * "Motor bike" is special-cased to search for "Motor Cycle" (the master's actual desc);
     * everything else is matched with a LIKE against VEHICLE_USAGE_DESC.
     */
    private MotorVehicleUsageMaster resolveVehicleUsage(String companyId, String stickerVehicleType) {
        if (stickerVehicleType == null || stickerVehicleType.isBlank()) {
            return null;
        }
        log.warn("Sticker type", stickerVehicleType);
        
        String searchTerm = "motor bike".equalsIgnoreCase(stickerVehicleType.trim())
                ? "Motor Cycle"
                : stickerVehicleType.trim();
        

        try {
            List<MotorVehicleUsageMaster> activeUsages = motorVehicleusageMasterRepository
                    .findByCompanyIdAndStatus(companyId, "Y");

            if (activeUsages == null || activeUsages.isEmpty()) {
                log.warn("No active motor_vehicleusage_master rows for companyId={}", companyId);
                return null;
            }

            String searchLower = searchTerm.toLowerCase();

            List<MotorVehicleUsageMaster> matches = activeUsages.stream()
                    .filter(u -> u.getVehicleUsageDesc() != null)
                    .filter(u -> {
                        String descLower = u.getVehicleUsageDesc().toLowerCase();
  
                        return searchLower.contains(descLower) || descLower.contains(searchLower);
                    })
                    .toList();

            if (matches.isEmpty()) {
                log.warn("No motor_vehicleusage_master match for searchTerm='{}' (original stickerVehicleType='{}'), companyId={}",
                        searchTerm, stickerVehicleType, companyId);
                return null;
            }

            if (matches.size() > 1) {
                // prefer the longest/most specific desc match when multiple hit
                matches = matches.stream()
                        .sorted((a, b) -> b.getVehicleUsageDesc().length() - a.getVehicleUsageDesc().length())
                        .toList();
                log.warn("Multiple motor_vehicleusage_master matches ({}) for searchTerm='{}', companyId={} — using most specific: {}",
                        matches.size(), searchTerm, companyId, matches.get(0).getVehicleUsageDesc());
            }

            return matches.get(0);

        } catch (Exception e) {
            log.error("Failed to resolve vehicle usage for stickerVehicleType={}, companyId={}", stickerVehicleType, companyId, e);
            return null;
        }
    }

    private BigDecimal toBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }
    
//    private void updatePolicyDrcrDetail(String quoteNo, PolicyObject obj) {
//        List<PolicyDrcrDetail> rows = policyDrcrDetailRepository.findByQuoteNo(quoteNo);
//        if (rows == null || rows.isEmpty()) {
//            log.warn("No policy_drcr_detail rows found for quoteNo={}", quoteNo);
//            return;
//        }
//
//        BigDecimal assessedPremium      = toBigDecimal(obj.getAssessedPremium());
//        BigDecimal assessedVat          = toBigDecimal(obj.getAssessedVat());
//        BigDecimal assessedTrainingLevy = toBigDecimal(obj.getAssessedTrainingLevy());
//        BigDecimal assessedStickerFees  = toBigDecimal(obj.getAssessedStickerFees());
//        BigDecimal assessedStampDuty    = toBigDecimal(obj.getAssessedStampDuty());
//
//        int updated = 0;
//        for (PolicyDrcrDetail row : rows) {
//            if (row.getChargeCode() == null) {
//                log.warn("Null charge_code in policy_drcr_detail for quoteNo={}, chgId={}", quoteNo, row.getChgId());
//                continue;
//            }
//
//            BigDecimal amount;
//            switch (row.getChargeCode().intValue()) {
//                case 1001: amount = assessedPremium;      break; // Premium
//                case 1002: amount = assessedVat;          break; // VAT
//                case 1003: amount = assessedTrainingLevy; break; // Insurance Training Levy
//                case 1004: amount = assessedStickerFees;  break; // Sticker Fee
//                case 1005: amount = assessedStampDuty;    break; // Stamp Duty
//                default:
//                    log.warn("Unknown charge_code={} in policy_drcr_detail for quoteNo={}", row.getChargeCode(), quoteNo);
//                    continue;
//            }
//
//            row.setAmountLc(amount);
//            row.setAmountFc(amount);
//            updated++;
//        }
//
//        policyDrcrDetailRepository.saveAll(rows);
//        log.info("Updated {} of {} policy_drcr_detail rows for quoteNo={}", updated, rows.size(), quoteNo);
//    }
   

    
}



