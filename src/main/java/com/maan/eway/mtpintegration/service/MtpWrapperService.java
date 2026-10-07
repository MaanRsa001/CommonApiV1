package com.maan.eway.mtpintegration.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.error.Error;
import com.maan.eway.mtpintegration.dto.MtpWrapperReq;
import com.maan.eway.mtpintegration.dto.MtpWrapperRes;
import com.maan.eway.mtpintegration.mtppayment.service.MtpPaymentStatusPollingService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MtpWrapperService {

    @PersistenceContext
    private EntityManager em;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${eway.common.api.base-url}")
    private String commonApiBase;   

    @Value("${eway.motor.api.base-url}")
    private String motorApiBase;  
    
    @Autowired
    private MtpPaymentStatusPollingService pollingService;

    private static final String INSURANCE_ID = "100019";
    private static final String BRANCH_CODE = "55";
    private static final String BROKER_BRANCH_CODE = "1";
    private static final String PRODUCT_ID = "125";
    private static final String AGENCY_CODE = "13781";
    private static final String VEHICLE_ID = "1";
    private static final String ASSESSMENT_TYPE = "M";
    private static final String INSURANCE_TYPE = "M";
    private static final String MOBILE_CODE = "256";
    private static final String CURRENCY = "UGX";
    private static final String EXCHANGE_RATE = "1";
    private static final String SECTION_ID = "160";
    private static final String LOCATION_ID = "1";

    private static final DateTimeFormatter API_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter VRN_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ConcurrentHashMap<String, Boolean> processingRegs = new ConcurrentHashMap<>();

    private static class MakeModelIds {
        private final String makeId;
        private final String modelId;

        MakeModelIds(String makeId, String modelId) {
            this.makeId = makeId;
            this.modelId = modelId;
        }

        String getMakeId() { return makeId; }
        String getModelId() { return modelId; }
    }

    public MtpWrapperRes process(MtpWrapperReq req, String token, String rawToken) {

        MtpWrapperRes wrapperRes = new MtpWrapperRes();

        if (req.getRegistrationNumber() == null) {
            return fail(wrapperRes, "registrationNumber is mandatory");
        }

        if (processingRegs.putIfAbsent(req.getRegistrationNumber(), Boolean.TRUE) != null) {
            return fail(wrapperRes, "Request for " + req.getRegistrationNumber() + " is already being processed");
        }

        try {
            HttpHeaders headers = buildHeaders(token);

            // ---------- STEP 1: validate-vrn -> API CALL ----------
            Map<String, Object> vrnReqBody = new LinkedHashMap<>();
            vrnReqBody.put("numberPlate", req.getRegistrationNumber());
            vrnReqBody.put("assessmentType", ASSESSMENT_TYPE);

            Map<String, Object> vrnRes = postForMap(commonApiBase + "/api/mtp/validate-vrn", vrnReqBody, headers);

            if (vrnRes == null) {
                return fail(wrapperRes, "VRN validation failed - no response");
            }

            boolean vrnIsError = ("true").equals(vrnRes.get("IsError"));

         
            if ("SEARCH".equalsIgnoreCase(req.getMode())) {
                wrapperRes.setError(vrnIsError);
                wrapperRes.setMessage(String.valueOf(vrnRes.get("Message")));
                wrapperRes.setResult(vrnRes.get("Result"));

                List<Error> errors = toErrorList(vrnRes.get("ErrorMessage"));

                if (errors.isEmpty() && vrnIsError && vrnRes.get("Result") instanceof Map) {
                    Map<String, Object> resultMap = (Map<String, Object>) vrnRes.get("Result");
                    Object rawMsg = resultMap.get("rawReturnMessage");
                    Object returnCode = resultMap.get("returnCode");
                    if (rawMsg != null) {
                        Error synthesized = new Error(
                                returnCode != null ? String.valueOf(returnCode) : "UNKNOWN",
                                null,
                                String.valueOf(rawMsg));
                        errors = Collections.singletonList(synthesized);
                    }
                }

                wrapperRes.setErrorMessage(errors);

                Object erroCode = vrnRes.get("ErroCode");
                if (erroCode instanceof Number) {
                    wrapperRes.setErrorCode(((Number) erroCode).intValue());
                } else if (vrnRes.get("Result") instanceof Map) {
                    // fallback: pull returnCode from Result if top-level ErroCode is absent/0
                    Object returnCode = ((Map<String, Object>) vrnRes.get("Result")).get("returnCode");
                    if (returnCode != null) {
                        try {
                            wrapperRes.setErrorCode(Integer.parseInt(String.valueOf(returnCode)));
                        } catch (NumberFormatException ignored) {
                            // leave as default
                        }
                    }
                }
                return wrapperRes;
            }

            // ---------- SAVE mode: still needs Result to proceed, fail hard on error ----------
            if (vrnIsError || vrnRes.get("Result") == null) {
                return fail(wrapperRes, vrnRes, "VRN validation failed");
            }

            Map<String, Object> vrn = (Map<String, Object>) vrnRes.get("Result");

            if (req.getIdType() == null || req.getIdNumber() == null || req.getMobileNumber() == null) {
                return fail(wrapperRes, "idType, idNumber, mobileNumber are mandatory for SAVE");
            }

            String make = (String) vrn.get("make");
            String model = (String) vrn.get("model");

            // STEP 2: resolve make/model IDs via direct DB query
            MakeModelIds ids = resolveMakeModelIds(make, model);
            if (ids.getMakeId() == null || ids.getModelId() == null) {
                return fail(wrapperRes, "Unable to resolve make/model for " + make + " / " + model);
            }

            // STEP 3: savecustomerdetails -> API CALL
            Map<String, Object> custReqBody = buildCustomerReqMap(req, vrn);
            Map<String, Object> custRes = postForMap(commonApiBase + "/api/savecustomerdetails", custReqBody, headers);

            if (custRes == null) {
                return fail(wrapperRes, "Customer save failed - no response");
            }
            if (Boolean.TRUE.equals(custRes.get("IsError")) || !(custRes.get("Result") instanceof Map)) {
                return fail(wrapperRes, custRes, "Customer save validation failed");
            }

            Map<String, Object> custResult = (Map<String, Object>) custRes.get("Result");
            String customerReferenceNo = (String) custResult.get("SuccessId");

            if (customerReferenceNo == null) {
                return fail(wrapperRes, custRes, "Customer save did not return a reference number");
            }

            // STEP 4: savemotordetails -> API CALL
            Map<String, Object> motorReqBody = buildMotorReqMap(req, vrn, ids, customerReferenceNo);
            Map<String, Object> motorRes = postForMap(motorApiBase + "/api/savemotordetails", motorReqBody, headers);

            if (motorRes == null) {
                return fail(wrapperRes, "Save motor details failed - no response");
            }
            if (Boolean.TRUE.equals(motorRes.get("IsError"))) {
                return fail(wrapperRes, motorRes, "Save motor details validation failed");
            }

            Object resultListObj = motorRes.get("Result");
            if (!(resultListObj instanceof List) || ((List<?>) resultListObj).isEmpty()) {
                return fail(wrapperRes, motorRes, "Save motor details returned empty result");
            }
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultListObj;
            Map<String, Object> motorResult = resultList.get(0);

            // STEP 5: calc -> API CALL
            Map<String, Object> calcReqBody = buildCalcReqMap(motorResult, "WhatsApp_Uganda_Broker");
            Map<String, Object> calcRes = postForMap(commonApiBase + "/calculator/calc", calcReqBody, headers);

            if (calcRes == null) {
                return fail(wrapperRes, "Calc failed - no response");
            }
            if (calcRes.get("CoverList") == null) {
                return fail(wrapperRes, calcRes, "Calc did not return cover list");
            }

            // STEP 6: buypolicy -> API CALL, built using calc response covers
            Map<String, Object> buyReqBody = buildBuyPolicyReqMap(motorResult, calcRes, "WhatsApp_Uganda_Broker");
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            System.out.println("Buy Policy Request JSON:");
            System.out.println(gson.toJson(buyReqBody));
            
            Map<String, Object> buyRes = postForMap(commonApiBase + "/quote/buypolicy", buyReqBody, headers);

            
            if (buyRes == null) {
                return fail(wrapperRes, "Buy policy failed - no response");
            }
            if (Boolean.TRUE.equals(buyRes.get("IsError"))) {
                return fail(wrapperRes, buyRes, "Buy policy validation failed");
            }

            Object buyResultObj = buyRes.get("Result");
            if (!(buyResultObj instanceof Map)) {
                return fail(wrapperRes, buyRes, "Buy policy returned unexpected result format");
            }
            Map<String, Object> buyResult = (Map<String, Object>) buyResultObj;
            String quoteNo = (String) buyResult.get("QuoteNo"); // TODO: confirm actual field name in buypolicy Result

            if (quoteNo == null) {
                return fail(wrapperRes, buyRes, "Buy policy did not return a quote number");
            }

            // Premium taken from calc's OverallPremium
            String premium = extractPremiumIncludedTax(calcRes);

            if (premium == null) {
                return fail(wrapperRes, "Calc response did not contain PremiumIncludedTax");
            }

            // STEP 7: makepayment -> API CALL
            Map<String, Object> makePaymentReqBody = buildMakePaymentReqMap(req, quoteNo, premium);
            Map<String, Object> makePaymentRes = postForMap(commonApiBase + "/payment/makepayment", makePaymentReqBody, headers);

            if (makePaymentRes == null) {
                return fail(wrapperRes, "Make payment failed - no response");
            }
            if (Boolean.TRUE.equals(makePaymentRes.get("IsError"))) {
                return fail(wrapperRes, makePaymentRes, "Make payment validation failed");
            }

            Object makePaymentResultObj = makePaymentRes.get("Result");
            if (!(makePaymentResultObj instanceof Map)) {
                return fail(wrapperRes, makePaymentRes, "Make payment returned unexpected result format");
            }
            Map<String, Object> makePaymentResult = (Map<String, Object>) makePaymentResultObj;
            String paymentId = (String) makePaymentResult.get("PaymentId");

            if (paymentId == null) {
                return fail(wrapperRes, makePaymentRes, "Make payment did not return a payment id");
            }

            // STEP 8: insertpaymentdetails -> API CALL
            Map<String, Object> insertPaymentReqBody = buildInsertPaymentReqMap(req, quoteNo, premium, paymentId);
            Map<String, Object> insertPaymentRes = postForMap(commonApiBase + "/payment/insertpaymentdetails", insertPaymentReqBody, headers);

            if (insertPaymentRes == null) {
                return fail(wrapperRes, "Insert payment details failed - no response");
            }
            if (Boolean.TRUE.equals(insertPaymentRes.get("IsError"))) {
                return fail(wrapperRes, insertPaymentRes, "Insert payment details validation failed");
            }

            Object insertPaymentResultObj = insertPaymentRes.get("Result");
            if (!(insertPaymentResultObj instanceof Map)) {
                return fail(wrapperRes, insertPaymentRes, "Insert payment details returned unexpected result format");
            }
            Map<String, Object> insertPaymentResult = (Map<String, Object>) insertPaymentResultObj;
            String merchantReference = (String) insertPaymentResult.get("MerchantReference");

            if (merchantReference == null) {
                return fail(wrapperRes, insertPaymentRes, "Insert payment details did not return a merchant reference");
            }

            // STEP 9: create-order-minim -> API CALL
            Map<String, Object> checkoutReqBody = new LinkedHashMap<>();
            checkoutReqBody.put("InsuranceId", INSURANCE_ID);

            Map<String, Object> checkoutRes = postForMapIgnoringContentType(
                    commonApiBase + "/selcom/v1/checkout/create-order-minim/" + merchantReference,
                    checkoutReqBody, headers);

            if (checkoutRes == null) {
                return fail(wrapperRes, "Checkout order creation failed - no response");
            }
            if (!"SUCCESS".equalsIgnoreCase(String.valueOf(checkoutRes.get("result")))) {
                return fail(wrapperRes, "Checkout order creation failed: " + checkoutRes.get("message"));
            }
            
            pollingService.startPolling(INSURANCE_ID, quoteNo, token);

            wrapperRes.setError(false);
            wrapperRes.setMessage("Success");
            wrapperRes.setResult(checkoutRes);
            return wrapperRes;

        } finally {
            processingRegs.remove(req.getRegistrationNumber());
        }
    }
    
    /**
     * Extracts PremiumIncludedTax from calc response. Handles two observed shapes:
     * 1) { "CoverList": [ { "PremiumIncludedTax": ... } ] }               (List form)
     * 2) { "CoverList": { "CoverList": { "PremiumIncludedTax": ... } } }  (nested Map form)
     */
    private String extractPremiumIncludedTax(Map<String, Object> calcRes) {
        Object coverListObj = calcRes.get("CoverList");

        Map<String, Object> coverMap = null;

        if (coverListObj instanceof List) {
            List<?> list = (List<?>) coverListObj;
            if (!list.isEmpty() && list.get(0) instanceof Map) {
                coverMap = (Map<String, Object>) list.get(0);
            }
        } else if (coverListObj instanceof Map) {
            Map<String, Object> outer = (Map<String, Object>) coverListObj;
            Object innerCoverList = outer.get("CoverList");
            if (innerCoverList instanceof Map) {
                coverMap = (Map<String, Object>) innerCoverList;
            } else if (innerCoverList instanceof List) {
                List<?> innerList = (List<?>) innerCoverList;
                if (!innerList.isEmpty() && innerList.get(0) instanceof Map) {
                    coverMap = (Map<String, Object>) innerList.get(0);
                }
            } else {
                // outer map might itself be the cover (no further nesting)
                coverMap = outer;
            }
        }

        if (coverMap == null) {
            log.warn("Unable to locate cover data in calc response to extract PremiumIncludedTax");
            return null;
        }

        Object premium = coverMap.get("PremiumIncludedTax");
        return premium != null ? String.valueOf(premium) : null;
    }

    private HttpHeaders buildHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private List<Error> toErrorList(Object rawErrorMessage) {
        if (!(rawErrorMessage instanceof List)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.convertValue(rawErrorMessage,
                    new TypeReference<List<com.maan.eway.error.Error>>() {});
        } catch (Exception e) {
            log.warn("Failed to convert ErrorMessage to List<Error>: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private MtpWrapperRes fail(MtpWrapperRes res, String msg) {
        res.setError(true);
        res.setMessage(msg);
        return res;
    }

    private MtpWrapperRes fail(MtpWrapperRes res, Map<String, Object> upstreamRes, String fallbackMsg) {
        res.setError(true);
        if (upstreamRes != null) {
            res.setMessage(upstreamRes.get("Message") != null ? String.valueOf(upstreamRes.get("Message")) : fallbackMsg);
            res.setErrorMessage(toErrorList(upstreamRes.get("ErrorMessage")));
            res.setResult(upstreamRes.get("Result"));
            Object erroCode = upstreamRes.get("ErroCode");
            if (erroCode instanceof Number) {
                res.setErrorCode(((Number) erroCode).intValue());
            }
        } else {
            res.setMessage(fallbackMsg);
        }
        return res;
    }
    private Map<String, Object> postForMap(String url, Map<String, Object> body, HttpHeaders headers) {
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            return resp.getBody();
        } catch (Exception e) {
            log.error("API call failed for url={} : {}", url, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Same as postForMap but forces String-based fetch + manual JSON parse,
     * bypassing Content-Type based converter selection. Needed specifically
     * for endpoints (like create-order-minim) that return JSON body but with
     * an incorrect "application/xml" Content-Type header, which makes
     * RestTemplate's default Map converter throw.
     */
    private Map<String, Object> postForMapIgnoringContentType(String url, Map<String, Object> body, HttpHeaders headers) {
        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String rawBody = resp.getBody();
            if (rawBody == null || rawBody.isBlank()) {
                return null;
            }

            return objectMapper.readValue(rawBody, new TypeReference<Map<String, Object>>() {});

        } catch (Exception e) {
            log.error("API call failed for url={} : {}", url, e.getMessage(), e);
            return null;
        }
    }

    

    private MakeModelIds resolveMakeModelIds(String makeName, String modelName) {
        String makeId = findMakeId(makeName);
        if (makeId == null) {
            return new MakeModelIds(null, null);
        }
        String modelId = findModelId(makeId, modelName);
        return new MakeModelIds(makeId, modelId);
    }

    private String findMakeId(String makeName) {
        try {
            Date today = new Date();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);
            Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);
            query.select(c);

            Subquery<Date> effectiveDate = query.subquery(Date.class);
            Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
            effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
            effectiveDate.where(
                    cb.equal(c.get("makeId"), ocpm1.get("makeId")),
                    cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today),
                    cb.equal(c.get("companyId"), ocpm1.get("companyId")),
                    cb.equal(c.get("branchCode"), ocpm1.get("branchCode")));

            Subquery<Date> effectiveDate2 = query.subquery(Date.class);
            Root<MotorMakeMaster> ocpm2 = effectiveDate2.from(MotorMakeMaster.class);
            effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
            effectiveDate2.where(
                    cb.equal(c.get("makeId"), ocpm2.get("makeId")),
                    cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), today),
                    cb.equal(c.get("companyId"), ocpm2.get("companyId")),
                    cb.equal(c.get("branchCode"), ocpm2.get("branchCode")));

            Predicate statusPred = cb.or(cb.equal(c.get("status"), "Y"), cb.equal(c.get("status"), "R"));
            Predicate branchPred = cb.or(cb.equal(c.get("branchCode"), BRANCH_CODE), cb.equal(c.get("branchCode"), "99999"));
            Predicate nameMatch = cb.equal(cb.upper(c.get("makeNameEn")), makeName.toUpperCase());

            query.where(statusPred,
                    cb.equal(c.get("effectiveDateStart"), effectiveDate),
                    cb.equal(c.get("effectiveDateEnd"), effectiveDate2),
                    cb.equal(c.get("companyId"), INSURANCE_ID),
                    branchPred, nameMatch);

            List<MotorMakeMaster> list = em.createQuery(query).getResultList();
            return list.isEmpty() ? null : list.get(0).getMakeId().toString();
        } catch (Exception e) {
            log.info("Exception while resolving makeId ---> " + e.getMessage());
            return null;
        }
    }

    private String findModelId(String makeId, String modelName) {
        try {
            Date today = new Date();
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
            Root<MotorMakeModelMaster> c = query.from(MotorMakeModelMaster.class);
            query.select(c);

            Subquery<Date> effectiveDate = query.subquery(Date.class);
            Root<MotorMakeModelMaster> ocpm1 = effectiveDate.from(MotorMakeModelMaster.class);
            effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
            effectiveDate.where(
                    cb.equal(c.get("makeId"), ocpm1.get("makeId")),
                    cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today),
                    cb.equal(c.get("companyId"), ocpm1.get("companyId")),
                    cb.equal(c.get("branchCode"), ocpm1.get("branchCode")),
                    cb.equal(c.get("modelId"), ocpm1.get("modelId")));

            Subquery<Date> effectiveDate2 = query.subquery(Date.class);
            Root<MotorMakeModelMaster> ocpm2 = effectiveDate2.from(MotorMakeModelMaster.class);
            effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
            effectiveDate2.where(
                    cb.equal(c.get("makeId"), ocpm2.get("makeId")),
                    cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), today),
                    cb.equal(c.get("companyId"), ocpm2.get("companyId")),
                    cb.equal(c.get("branchCode"), ocpm2.get("branchCode")),
                    cb.equal(c.get("modelId"), ocpm2.get("modelId")));

            Predicate statusPred = cb.or(cb.equal(c.get("status"), "Y"), cb.equal(c.get("status"), "R"));
            Predicate branchPred = cb.or(cb.equal(c.get("branchCode"), BRANCH_CODE), cb.equal(c.get("branchCode"), "99999"));
            Predicate makeMatch = cb.equal(c.get("makeId"), Long.valueOf(makeId));
            Predicate modelMatch = cb.equal(cb.upper(c.get("modelNameEn")), modelName.toUpperCase());

            query.where(statusPred,
                    cb.equal(c.get("effectiveDateStart"), effectiveDate),
                    cb.equal(c.get("effectiveDateEnd"), effectiveDate2),
                    cb.equal(c.get("companyId"), INSURANCE_ID),
                    branchPred, makeMatch, modelMatch);

            List<MotorMakeModelMaster> list = em.createQuery(query).getResultList();
            return list.isEmpty() ? null : list.get(0).getModelId().toString();
        } catch (Exception e) {
            log.info("Exception while resolving modelId ---> " + e.getMessage());
            return null;
        }
    }

    // ---- savecustomerdetails: plain Map<String,Object> ----

    private Map<String, Object> buildCustomerReqMap(MtpWrapperReq req, Map<String, Object> vrn) {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("BrokerBranchCode", BROKER_BRANCH_CODE);
        c.put("CustomerReferenceNo", null);
        c.put("InsuranceId", INSURANCE_ID);
        c.put("BranchCode", BRANCH_CODE);
        c.put("ProductId", PRODUCT_ID);
        c.put("AppointmentDate", null);
        c.put("Address1", "address");
        c.put("Address2", null);
        c.put("BusinessType", null);
        c.put("CityCode", "2");
        c.put("CityName", "EAST MOYO");
        c.put("ClientName", vrn.get("name"));
        c.put("Clientstatus", "Y");
        c.put("CreatedBy", "WhatsApp_Uganda_Broker");
        c.put("DobOrRegDate", "05/07/2008");
        c.put("ExpiryDate", null);
        c.put("Email1", "hgfcds@gmail.com");
        c.put("Email2", null);
        c.put("Email3", null);
        c.put("Fax", null);
        c.put("Gender", req.getGender());
        c.put("IdNumber", req.getIdNumber());
        c.put("IdType", req.getIdType());
        c.put("IsTaxExempted", "N");
        c.put("Language", "1");
        c.put("MobileNo1", req.getMobileNumber());
        c.put("MobileNo2", null);
        c.put("MobileNo3", null);
        c.put("Nationality", null);
        c.put("NationalityName", null);
        c.put("Country", CURRENCY);
        c.put("CountryName", "Uganda");
        c.put("Occupation", "20");
        c.put("OtherOccupation", null);
        c.put("Placeofbirth", "Chennai");
        c.put("PolicyHolderType", "1");
        c.put("PolicyHolderTypeid", "4");
        c.put("PreferredNotification", "Mail");
        c.put("RegionCode", "1");
        c.put("MobileCode1", MOBILE_CODE);
        c.put("WhatsappCode", null);
        c.put("MobileCodeDesc1", "1");
        c.put("WhatsappDesc", "1");
        c.put("WhatsappNo", null);
        c.put("StateCode", "1");
        c.put("StateName", "ABIM");
        c.put("Status", "Y");
        c.put("Street", null);
        c.put("Type", null);
        c.put("TaxExemptedId", null);
        c.put("TelephoneNo1", null);
        c.put("PinCode", null);
        c.put("TelephoneNo2", null);
        c.put("TelephoneNo3", null);
        c.put("Title", req.getTitle());
        c.put("VrTinNo", null);
        c.put("SaveOrSubmit", "Submit");
        c.put("MiddleName", null);
        c.put("LastName", null);
        c.put("Zone", "1");
        c.put("SocioProfessionalCategory", null);
        c.put("Activities", null);
        c.put("CustomerAsInsurer", null);
        c.put("MaritalStatus", "Single");
        c.put("VipFlag", null);
        c.put("RiskAssessmentDate", null);
        c.put("PhoneNoCode", null);
        c.put("CustomerType", "1");
        c.put("IndustryType", null);
        c.put("IndustryTypeId", null);
        c.put("WealthSource", null);
        c.put("WealthSourceId", null);
        c.put("ComplianceStatus", null);
        c.put("ComplianceStatusId", null);
        c.put("LegalStructure", null);
        c.put("LegalStructureId", null);
        c.put("Owners", null);
        c.put("OwnersId", null);
        return c;
        
    }

    private Map<String, Object> buildMotorReqMap(MtpWrapperReq req, Map<String, Object> vrn,
                                                  MakeModelIds ids, String customerReferenceNo) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("CustomerName", vrn.get("name"));
        m.put("LoginId", "WhatsApp_Uganda_Broker");
        m.put("SubUserType", "broker");
        m.put("UserType", "Broker");
        m.put("ApplicationId", "1");
        m.put("CustomerReferenceNo", customerReferenceNo);
        m.put("RequestReferenceNo", null);
        m.put("VehicleId", VEHICLE_ID);
        m.put("CreatedBy", "WhatsApp_Uganda_Broker");
        m.put("InsuranceId", INSURANCE_ID);
        m.put("BranchCode", BRANCH_CODE);
        m.put("BrokerBranchCode", BROKER_BRANCH_CODE);
        m.put("AgencyCode", "12860");
        m.put("ProductId", PRODUCT_ID);
        m.put("SavedFrom", "SQ");
        m.put("SumInsured", "1");
        m.put("MobileCode", MOBILE_CODE);
        m.put("MobileNumber", req.getMobileNumber());
        m.put("Chassisnumber", req.getRegistrationNumber());
        m.put("Insurancetype", INSURANCE_TYPE);
        m.put("NcdYn", "N");
        m.put("InsuranceClass", INSURANCE_TYPE);
        m.put("Vehiclemake", vrn.get("make"));
        m.put("VehiclemakeId", ids.getMakeId());
        m.put("VehicleModel", vrn.get("model"));
        m.put("VehcilemodelId", ids.getModelId());
        m.put("VehicleType", "Saloons");
        m.put("VehicleTypeId", "7");
        m.put("Registrationnumber", req.getRegistrationNumber());
        m.put("EngineCapacity", vrn.get("engineSize"));

        Object startDateObj = vrn.get("policyStartDate");
        Object endDateObj = vrn.get("policyEndDate");

        String vrnStartDate = toNonEmptyString(startDateObj);
        String vrnEndDate = toNonEmptyString(endDateObj);

        boolean isImport = (vrnStartDate == null && vrnEndDate == null);
        m.put("ImportyYN", isImport ? "Y" : "N");

        String startDate = calcPolicyStartDate(vrnEndDate);
        String endDate = calcPolicyEndDate(startDate);
        m.put("PolicyStartDate", startDate);
        m.put("PolicyEndDate", endDate);
        m.put("PolicyStartDate", startDate);
        m.put("PolicyEndDate", endDate);

        m.put("ExchangeRate", EXCHANGE_RATE);
        m.put("Currency", CURRENCY);
        m.put("HavePromoCode", "N");
        m.put("SearchFromApi", false);
        m.put("Gpstrackinginstalled", "N");
        m.put("SectionId", Collections.singletonList(SECTION_ID));
        m.put("ManufactureYear", String.valueOf(LocalDate.now().getYear()));
        m.put("LoanAmount", extractDigits((String) vrn.get("amount")));
        return m;
    }

    // ---- calc: plain Map<String,Object> ----

    private Map<String, Object> buildCalcReqMap(Map<String, Object> motorResult, String loginId) {
        Map<String, Object> c = new LinkedHashMap<>();
        c.put("InsuranceId", INSURANCE_ID);
        c.put("BranchCode", BRANCH_CODE);
        c.put("AgencyCode", "12860");
        c.put("SectionId", SECTION_ID);
        c.put("ProductId", PRODUCT_ID);
        c.put("MSRefNo", motorResult.get("MSRefNo"));
        c.put("VehicleId", motorResult.get("VehicleId"));
        c.put("LocationId", LOCATION_ID);
        c.put("CdRefNo", motorResult.get("CdRefNo"));
        c.put("DdRefNo", motorResult.get("DdRefNo"));
        c.put("VdRefNo", motorResult.get("VdRefNo"));
        c.put("CreatedBy", "WhatsApp_Uganda_Broker");
        c.put("productId", PRODUCT_ID);
        c.put("sectionId", SECTION_ID);
        c.put("RequestReferenceNo", motorResult.get("RequestReferenceNo"));
        c.put("EffectiveDate", LocalDate.now().format(API_FMT));
        c.put("PolicyEndDate", LocalDate.now().plusYears(1).format(API_FMT));
        c.put("CoverModification", "N");
        return c;
    }

    // ---- buypolicy: plain Map<String,Object>, Vehicles/Covers pulled from calc response ----

//    private static final String COVER_ID = "204";
//    private static final String SUB_COVER_ID = null;
//    private static final String SUB_COVER_YN = "N";
//
//    private Map<String, Object> buildBuyPolicyReqMap(Map<String, Object> motorResult, String loginId) {
//        Map<String, Object> req = new LinkedHashMap<>();
//        req.put("RequestReferenceNo", motorResult.get("RequestReferenceNo"));
//        req.put("CreatedBy", loginId);
//        req.put("ProductId", PRODUCT_ID);
//        req.put("ManualReferralYn", "N");
//        req.put("EmiYn", "N");
//        req.put("ReferralRemarks", null);
//        req.put("Vehicles", buildStaticVehicles());
//
//        return req;
//    }
//
//    private List<Map<String, Object>> buildStaticVehicles() {
//        Map<String, Object> cover = new LinkedHashMap<>();
//        cover.put("CoverId", COVER_ID);
//        cover.put("SubCoverId", SUB_COVER_ID);
//        cover.put("SubCoverYn", SUB_COVER_YN);
//
//        Map<String, Object> vehicle = new LinkedHashMap<>();
//        vehicle.put("Covers", Collections.singletonList(cover));
//        vehicle.put("LocationId", LOCATION_ID);
//        vehicle.put("Id", VEHICLE_ID);
//        vehicle.put("SectionId", SECTION_ID);
//
//        return Collections.singletonList(vehicle);
//    }
    
    private Map<String, Object> buildBuyPolicyReqMap(Map<String, Object> motorResult, Map<String, Object> calcRes,
            String loginId) {
        Map<String, Object> req = new LinkedHashMap<>();
        req.put("RequestReferenceNo", motorResult.get("RequestReferenceNo"));
        req.put("CreatedBy", loginId);
        req.put("ProductId", PRODUCT_ID);
        req.put("ManualReferralYn", "N");
        req.put("EmiYn", "N");
        req.put("ReferralRemarks", null);

        req.put("Vehicles", buildVehiclesFromCalc(calcRes));

        return req;
    }

    /**
     * Builds the minimal Vehicles/Covers structure buypolicy expects.
     * calc's response body itself (no "Result" wrapper) is the single vehicle
     * object, with "CoverList" nested inside it directly.
     */
    private List<Map<String, Object>> buildVehiclesFromCalc(Map<String, Object> calcRes) {

        List<Map<String, Object>> vehicles = new ArrayList<>();
        Map<String, Object> vehicle = new LinkedHashMap<>();

        List<Map<String, Object>> covers = new ArrayList<>();

        for (Map<String, Object> coverData : extractCoverList(calcRes)) {
            Map<String, Object> minimalCover = new LinkedHashMap<>();
            minimalCover.put("CoverId", coverData.get("CoverId"));
            minimalCover.put("SubCoverId", coverData.get("SubCoverId"));
            minimalCover.put("SubCoverYn", "N"); // not present in calc response, always static
            covers.add(minimalCover);
        }

        vehicle.put("Covers", covers);
        vehicle.put("LocationId", calcRes.get("LocationId") != null ? calcRes.get("LocationId") : LOCATION_ID);
        vehicle.put("Id", calcRes.get("VehicleId") != null ? calcRes.get("VehicleId") : VEHICLE_ID);
        vehicle.put("SectionId", calcRes.get("SectionId") != null ? calcRes.get("SectionId") : SECTION_ID);

        vehicles.add(vehicle);
        return vehicles;
    }

    /**
     * Normalizes calc's CoverList into a List<Map> regardless of shape:
     * 1) { "CoverList": [ {...cover...} ] }                          (List form)
     * 2) { "CoverList": { "CoverList": {...cover...} } }              (nested single-Map form)
     * 3) { "CoverList": { "CoverList": [ {...cover...} ] } }          (nested List form)
     */
    private List<Map<String, Object>> extractCoverList(Map<String, Object> calcRes) {
        Object coverListObj = calcRes.get("CoverList");
        List<Map<String, Object>> result = new ArrayList<>();

        if (coverListObj instanceof List) {
            for (Object item : (List<?>) coverListObj) {
                if (item instanceof Map) {
                    result.add((Map<String, Object>) item);
                }
            }
            return result;
        }

        if (coverListObj instanceof Map) {
            Map<String, Object> outer = (Map<String, Object>) coverListObj;
            Object inner = outer.get("CoverList");

            if (inner instanceof List) {
                for (Object item : (List<?>) inner) {
                    if (item instanceof Map) {
                        result.add((Map<String, Object>) item);
                    }
                }
            } else if (inner instanceof Map) {
                result.add((Map<String, Object>) inner);
            } else if (outer.get("CoverId") != null) {
                // outer itself is the cover object (no further nesting)
                result.add(outer);
            }
        }

        return result;
    }
    
    private static final String PAYMENT_REMARKS = "None";
    private static final String SUB_USER_TYPE = "Broker";
    private static final String USER_TYPE = "Broker";
    private static final String EMI_YN = "N";
    private static final String PAYMENT_TYPE = "5";

    private Map<String, Object> buildMakePaymentReqMap(MtpWrapperReq req, String quoteNo, String premium) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("CreatedBy", "WhatsApp_Uganda_Broker");
        m.put("EmiYn", EMI_YN);
        m.put("InstallmentMonth", null);
        m.put("InstallmentPeriod", null);
        m.put("InsuranceId", INSURANCE_ID);
        m.put("Premium", premium);
        m.put("QuoteNo", quoteNo);
        m.put("Remarks", PAYMENT_REMARKS);
        m.put("SubUserType", SUB_USER_TYPE);
        m.put("UserType", USER_TYPE);
        return m;
    }

    private Map<String, Object> buildInsertPaymentReqMap(MtpWrapperReq req, String quoteNo, String premium, String paymentId) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("CreatedBy", "WhatsApp_Uganda_Broker");
        m.put("InsuranceId", INSURANCE_ID);
        m.put("EmiYn", EMI_YN);
        m.put("Premium", premium);
        m.put("QuoteNo", quoteNo);
        m.put("Remarks", PAYMENT_REMARKS);
        m.put("PayeeName", null);
        m.put("SubUserType", SUB_USER_TYPE);
        m.put("UserType", USER_TYPE);
        m.put("MICRNo", null);
        m.put("BankName", null);
        m.put("ChequeNo", null);
        m.put("ChequeDate", "");
        m.put("PaymentType", PAYMENT_TYPE);
        m.put("Payments", "");
        m.put("PaymentId", paymentId);
        m.put("AccountNumber", null);
        m.put("IbanNumber", null);
        m.put("WhatsappNo", null);
        m.put("WhatsappCode", null);
        m.put("MobileCode1", MOBILE_CODE);
        m.put("MobileNo1", req.getMobileNumber());
        m.put("InsurancePayename", null);
        m.put("InsurancePayenumber", null);
        return m;
    }
    

    private String toNonEmptyString(Object obj) {
        if (obj == null) return null;
        String s = String.valueOf(obj).trim();
        return s.isEmpty() ? null : s;
    }
    
    

    private String calcPolicyStartDate(String vrnEndDate) {
        
        if (vrnEndDate == null) {
            return LocalDate.now().format(API_FMT);
        }
        return LocalDate.parse(vrnEndDate, VRN_FMT).plusDays(1).format(API_FMT);
    }

    private String calcPolicyEndDate(String startDateStr) {
        return LocalDate.parse(startDateStr, API_FMT).plusYears(1).format(API_FMT);
    }

    private String extractDigits(String amountText) {
        return amountText == null ? null : amountText.replaceAll("[^0-9]", "");
    }
}