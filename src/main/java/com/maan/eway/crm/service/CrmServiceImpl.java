package com.maan.eway.crm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.maan.eway.bean.*;
import com.maan.eway.crm.bean.*;
import com.maan.eway.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.admin.req.BrokerLoginGridReq;
import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.common.res.CommonRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class CrmServiceImpl implements CrmService {
    private Logger log = LogManager.getLogger(CrmServiceImpl.class);

    @PersistenceContext
    private EntityManager em;
    @Value(value = "${crm.changePassword}")
    private String changePassword;

    @Autowired
    private LoginBranchMasterRepository loginBranchMasterRepo;

    @Autowired
    private InsuranceCompanyMasterRepository companyRepo;

    @Autowired
    private LoginBranchMasterRepository loginBranchRepo;

    @Autowired
    private SessionMasterRepository sessionRep;
    @Autowired
    private LoginMasterRepository loginRepo;

    @Autowired
    private HomePositionMasterRepository homePositionMatserRepo;

    @Autowired
    private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepository;

    @Autowired
    private LoginUserInfoRepository loginUserInfoRepository;

    @Autowired
    private PersonalInfoRepository personalInfoRepo;
    @Value(value = "${crm.enquiry}")
    private String enquiry;

    @Value(value = "${crm.updateEnquiry}")
    private String updateEnquiry;

    @Autowired
    private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepo;

    @Autowired
    private CompanyProductMasterRepository companyProductMasterRepo;


    @Autowired
    private ProductSectionMasterRepository productSectionMasterRepo;

    @Autowired
    private BranchMasterRepository branchMasterRepo;

    @Override
    public UserLoginResponseData validateTokenForCRM(String token) {
        UserLoginResponseData resp = new UserLoginResponseData();
        try {
            SessionMaster session = sessionRep.findByTempTokenid(token);

            if (session == null || "DE-ACTIVE".equalsIgnoreCase(session.getStatus())) {
                resp.setValidateToken(false);
                return resp;
            }

            String loginId = session.getLoginId();
            String userType = session.getUserType();
            String subUserType = session.getSubUserType();

            LoginMaster loginData = loginRepo.findByLoginId(loginId);

            if (loginData == null) {
                resp.setValidateToken(false);
                return resp;
            }

            LoginUserInfo loginInfo = loginUserInfoRepository.findByLoginId(loginId);
            String companyId = loginData.getCompanyId();
            String loginAccess = loginInfo.getLoginAccess();
            String referralAccess = loginInfo.getReferralAccess();
            String reportAccess = loginInfo.getReportAccess();
            String logincoreappacode = loginInfo.getCoreAppBrokerCode();
            resp.setMenuAccessYN(loginAccess != null ? loginAccess : "N");
            resp.setReferalAccessYN(referralAccess != null ? referralAccess : "N");
            resp.setReportAccessYN(reportAccess != null ? reportAccess : "N");
            resp.setValidateToken(true);
            resp.setUserType(userType);
            resp.setSubUserType(subUserType);
            resp.setCompanyId(companyId);
            resp.setLoginId(loginId);
            resp.setLoginUserCoreAppCode(logincoreappacode);
            List<LoginBranchMaster> loginBranchDatas = loginBranchMasterRepo.findByLoginId(loginId);
            String branchCodes = loginBranchDatas.stream().map(a -> a.getBranchCode()).collect(Collectors.joining(","));
            resp.setBranchCode(branchCodes);
            /***
             * branch Branch code, name, companyId, PremiaCode,
             *
             */
            List<LoginBranchMaster> loginBranchData = loginBranchMasterRepo.findByLoginId(loginId);

            String branchCode = loginBranchData.stream().map(LoginBranchMaster::getBranchCode)
                    .collect(Collectors.joining(","));

            resp.setBranchCode(branchCode);

            // Attached Branch Map
            Map<String, AttachedBranchRes> attachedBranchMap = new HashMap<>();

            for (LoginBranchMaster loginBranch : loginBranchData) {

                String attachedBranch = loginBranch.getAttachedBranch();

                if (attachedBranch != null && !attachedBranch.isBlank()) {

                    BranchMaster branch = branchMasterRepo.findLatestBranch(companyId, attachedBranch);

                    if (branch != null) {

                        AttachedBranchRes branchDetails = new AttachedBranchRes();
                        branchDetails.setBarnchName(branch.getBranchName());
                        branchDetails.setBranchCoreAppCode(branch.getCoreAppCode());

                        attachedBranchMap.put(attachedBranch, branchDetails);
                    }
                }
            }

            resp.setAttachedBranchDetails(attachedBranchMap);

            resp.setAttachedBranchDetails(attachedBranchMap);
        } catch (Exception e) {
            e.printStackTrace();
            resp.setValidateToken(false);
        }
        return resp;
    }

    @Override
    public List<ProductDropDownRes> getProductDetailByLoginId(String loginId, String companyId) {
        List<LoginProductMaster> loginproduct = new ArrayList<>();

        Date today = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 1);
        today = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 1);
        Date todayEnd = cal.getTime();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
        Root<LoginProductMaster> c = query.from(LoginProductMaster.class);
        query.select(c);

        List<Order> orderList = new ArrayList<>();
        orderList.add(cb.asc(c.get("productName")));

        Subquery<Date> effectiveDate = query.subquery(Date.class);
        Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
        effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
        effectiveDate.where(
                cb.equal(c.get("productId"), ocpm1.get("productId")),
                cb.equal(c.get("companyId"), ocpm1.get("companyId")),
                cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today),
                cb.equal(c.get("loginId"), ocpm1.get("loginId"))
        );

        Subquery<Date> effectiveDate2 = query.subquery(Date.class);
        Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
        effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
        effectiveDate2.where(
                cb.equal(c.get("productId"), ocpm2.get("productId")),
                cb.equal(c.get("companyId"), ocpm2.get("companyId")),
                cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd),
                cb.equal(c.get("loginId"), ocpm2.get("loginId"))
        );

        Subquery<Long> productIds = query.subquery(Long.class);
        Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);

        Subquery<Date> effectiveDate3 = query.subquery(Date.class);
        Root<CompanyProductMaster> ocpm4 = effectiveDate3.from(CompanyProductMaster.class);
        effectiveDate3.select(cb.greatest(ocpm4.get("effectiveDateStart").as(Date.class)));
        effectiveDate3.where(
                cb.equal(cm.get("productId"), ocpm4.get("productId")),
                cb.equal(cm.get("companyId"), ocpm4.get("companyId")),
                cb.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today)
        );

        Subquery<Date> effectiveDate4 = query.subquery(Date.class);
        Root<CompanyProductMaster> ocpm5 = effectiveDate4.from(CompanyProductMaster.class);
        effectiveDate4.select(cb.greatest(ocpm5.get("effectiveDateEnd").as(Date.class)));
        effectiveDate4.where(
                cb.equal(cm.get("productId"), ocpm5.get("productId")),
                cb.equal(cm.get("companyId"), ocpm5.get("companyId")),
                cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd)
        );

        productIds.select(cm.get("productId"));
        productIds.where(
                cb.equal(cm.get("companyId"), companyId),
                cb.equal(cm.get("status"), "Y"),
                cb.equal(cm.get("effectiveDateStart"), effectiveDate3),
                cb.equal(cm.get("effectiveDateEnd"), effectiveDate4)
        );

        Predicate n1 = cb.equal(c.get("status"), "Y");
        Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
        Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
        Predicate n4 = cb.equal(c.get("companyId"), companyId);
        Predicate n5 = cb.equal(c.get("loginId"), loginId);
        Predicate n6 = c.get("productId").in(productIds);

        query.where(cb.and(n1, n2, n3, n4, n5, n6)).orderBy(orderList);

        TypedQuery<LoginProductMaster> result = em.createQuery(query);
        loginproduct = result.getResultList();

        List<ProductDropDownRes> resList = new ArrayList<>();
        for (LoginProductMaster products : loginproduct) {
            Integer productId = products.getProductId();
            List<CompanyProductMaster> product = getCompanyProductMaster(products.getCompanyId(), productId);

            if (!product.isEmpty()) {
                CompanyProductMaster prod = product.get(0);
                ProductDropDownRes res = new ProductDropDownRes();
                res.setOldProductName(products.getProductName());
                res.setNewProductName(prod.getProductName());
                res.setProductIconId(prod.getProductIconId() != null ? prod.getProductIconId().toString() : null);
                res.setProductIconName(prod.getProductIconName());
                res.setProductId(productId.toString());
                res.setPackageYn(prod.getPackageYn());
                res.setDisplayOrder(prod.getDisplayOrder() == null ? 999 : prod.getDisplayOrder());
                resList.add(res);
            }
        }

        resList.sort(Comparator.comparing(ProductDropDownRes::getDisplayOrder));
        return resList;
    }


    public List<CompanyProductMaster> getCompanyProductMaster(String companyId, Integer productId) {
        List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
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

            // Find All
            Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
            //Select
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
            Predicate n7 = cb.equal(c.get("productId"), productId);
            Predicate n5 = cb.equal(c.get("status"), "R");
            Predicate n6 = cb.or(n1, n5);
            query.where(n6, n2, n3, n4, n7).orderBy(orderList);
            // Get Result
            TypedQuery<CompanyProductMaster> result = em.createQuery(query);
            list = result.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception is --->" + e.getMessage());
            return null;
        }
        return list;
    }

    @Override
    public void updatePassword(ChangePasswordReq req, String url) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // headers.set("X-AUTH-TOKEN", actualToken);
        headers.set("Authorization", "");
        HttpEntity<ChangePasswordReq> requestEntity = new HttpEntity<>(req, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,
                String.class);
        System.out.println("Response: " + response.getBody());
    }

    @Override
    public String getEnqiryDetail(Long enquiryId, String token) {
        String rcmApi = enquiry + "/" + enquiryId;

        RestTemplate restTemplate = new RestTemplate();

        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        actualToken = actualToken.split(",")[0];
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-AUTH-TOKEN", actualToken);
        headers.set("Authorization", "Bearer " + actualToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(rcmApi, HttpMethod.GET, requestEntity, String.class);

        return response.getBody();
    }


    @Override
    public List<EserviceCustomerDetails> getCustomerDetailByLeadseqNo(Long leadSeqNo, String companyId, String token) {
        return eserviceCustomerDetailsRepo
                .findByCompanyIdAndLeadSeqNo(companyId, leadSeqNo);
    }

    @Override
    public ResponseEntity<CommonRes> updateEnquiryQuotestatus(QuoteReq quoteReq) {
        CommonRes response = new CommonRes();

        try {
            HomePositionMaster quotedata = homePositionMatserRepo.findByQuoteNo(quoteReq.getQuoteNo());

            if (quotedata != null) {
                quotedata.setLeadSeqNo(quoteReq.getLeadId());
                quotedata.setQuotestatus(quoteReq.getQuoteStatus());
                homePositionMatserRepo.save(quotedata);

                response.setMessage("Quote status updated successfully.");
                response.setIsError(false);
                response.setErroCode(200);
                response.setCommonResponse(null);
            } else {
                response.setMessage("Invalid Lead ID or Quote No.");
                response.setIsError(true);
                response.setErroCode(400);
            }

        } catch (Exception e) {
            response.setMessage("Something went wrong while updating quote status.");
            response.setIsError(true);
            response.setErroCode(500);

        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CommonRes> updateCRMEnquiryQuotestatus(CustomerDetail req, String token) {
        Long enqSeqNo = req.getEnqSeqNo();
        String enqStatus = req.getEnqStatus();
        Long leadSeqNo = req.getLeadSeqNo();
        String rcmApi = updateEnquiry;

        RestTemplate restTemplate = new RestTemplate();
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        actualToken = actualToken.split(",")[0];

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-AUTH-TOKEN", actualToken);
        headers.set("Authorization", "Bearer " + actualToken);

        Map<String, Object> body = new HashMap<>();
        body.put("enqSeqNo", enqSeqNo);
        body.put("enqStatus", enqStatus);
        body.put("leadSeqNo", leadSeqNo);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseStr = restTemplate.exchange(
                rcmApi,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        CommonRes commonRes = new CommonRes();

        try {
            JsonNode root = mapper.readTree(responseStr.getBody());
            JsonNode dataNode = root.path("data");

            commonRes.setMessage("Success");
            commonRes.setIsError(false);
            commonRes.setErroCode(responseStr.getStatusCodeValue());
            commonRes.setCommonResponse(dataNode);

        } catch (Exception e) {
            commonRes.setMessage("Error parsing response");
            commonRes.setIsError(true);
            commonRes.setErroCode(500);
        }

        return ResponseEntity.status(responseStr.getStatusCode()).body(commonRes);
    }

    @Override
    public ResponseEntity<CommonRes> fetchQuoteDetailByLeqdSeqNo(Long leadSeqNo, String companyId) {
        CommonRes response = new CommonRes();

        try {
            List<HomePositionMaster> quotedata = homePositionMatserRepo.findAllByLeadSeqNoAndCompanyId(leadSeqNo,
                    companyId);
            String customerId = quotedata.get(0).getCustomerId();
            List<PersonalInfo> personalList = personalInfoRepo.findByCustomerIdAndCompanyId(customerId, companyId);
            String clientName = personalList.get(0).getClientName();
            if (quotedata != null && !quotedata.isEmpty()) {
                quotedata.get(0).setCustomerName(clientName);
                response.setMessage("Quote details fetched successfully.");
                response.setIsError(false);
                response.setErroCode(200);
                response.setCommonResponse(quotedata);
            } else {
                response.setMessage("No quote details found for the given Lead ID.");
                response.setIsError(true);
                response.setErroCode(404);
            }

        } catch (Exception e) {
            response.setMessage("Something went wrong while fetching quote details.");
            response.setIsError(true);
            response.setErroCode(500);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CommonRes> updateCustomerProductId(CustomerDetail customerDetailReq) {
        CommonRes response = new CommonRes();

        try {
            String companyId = customerDetailReq.getCompanyId();
            String customerReferenceNo = customerDetailReq.getCustomerReferenceNo();
            Integer productId;

            try {
                productId = Integer.valueOf(customerDetailReq.getProductId());
            } catch (NumberFormatException e) {
                response.setMessage("Invalid Product ID format.");
                response.setIsError(true);
                response.setErroCode(400);
                return ResponseEntity.badRequest().body(response);
            }

            Optional<EserviceCustomerDetails> customerDetail = eserviceCustomerDetailsRepository
                    .findByCustomerReferenceNoAndCompanyId(customerReferenceNo, companyId);
            if (customerDetail.isPresent()) {
                EserviceCustomerDetails eserviceCustomerDetails = customerDetail.get();
                eserviceCustomerDetails.setProductId(productId);
                eserviceCustomerDetailsRepository.save(eserviceCustomerDetails);
            } else {
                response.setMessage("Customer details not found for given reference no and company id.");
                response.setIsError(true);
                response.setErroCode(404);
                return ResponseEntity.ok(response);
            }

            /***Optional<HomePositionMaster> homePos = homePositionMatserRepo
             .findAllByCompanyIdAndCustomerId(companyId, customerReferenceNo);
             if (homePos.isPresent()) {
             HomePositionMaster homePositionMaster = homePos.get();
             homePositionMaster.setProductId(productId);
             homePositionMatserRepo.save(homePositionMaster);
             } else {
             response.setMessage("Home position not found for given company id and customer id.");
             response.setIsError(true);
             response.setErroCode(404);
             return ResponseEntity.ok(response);
             }**/

            // Success
            response.setMessage("Product ID updated successfully.");
            response.setIsError(false);
            response.setErroCode(200);
            response.setCommonResponse(null);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("An error occurred while updating Product ID.");
            response.setIsError(true);
            response.setErroCode(500);
            return ResponseEntity.status(500).body(response);
        }
    }

    @Override
    public ResponseEntity<CommonRes> getApproverDropDownByClientId(BrokerLoginGridReq req) {
        CommonRes response = new CommonRes();

        try {
            List<LoginMaster> userList = loginRepo.findByCompanyIdAndUserTypeAndSubUserType(req.getCompanyId(),
                    req.getUserType(), "both");

            List<CustomerDetail> customerDetails = userList == null ? Collections.emptyList()
                    : userList.stream().map(LoginMaster::getLoginId).filter(Objects::nonNull).map(CustomerDetail::new)
                    .collect(Collectors.toList());

            ApproverDropDownResponse approverData = new ApproverDropDownResponse();
            approverData.setCustomerDetails(customerDetails);

            response.setCommonResponse(approverData);

            response.setIsError(false);
            response.setMessage("Success");
            response.setErroCode(200);
            response.setErrorMessage(null);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.setIsError(true);
            response.setMessage("Failed to fetch approvers");
            response.setErroCode(500);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<CommonRes> getProductDetailByCompanyId(String companyId) {

        CommonRes response = new CommonRes();

        try {
            List<CompanyProductMaster> products = companyProductMasterRepo.findByCompanyIdAndStatus(companyId, "Y");

            List<CompanyProductRes> productResList = products.stream().map(product -> {
                CompanyProductRes res = new CompanyProductRes();
                res.setCompanyId(product.getCompanyId());
                res.setProductId(product.getProductId());
                res.setProductName(product.getProductName());
                res.setStatus(product.getStatus());
                return res;
            }).collect(Collectors.toList());

            response.setCommonResponse(productResList);
            response.setIsError(false);
            response.setMessage("Success");
            response.setErroCode(200);
            response.setErrorMessage(null);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.setIsError(true);
            response.setMessage("Failed to fetch Data");
            response.setErroCode(500);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<CommonRes> getsectionDetailByProductIdandCompanyId(String companyId, Integer productId) {

        CommonRes response = new CommonRes();

        try {
            List<ProductSectionMaster> productsecList = productSectionMasterRepo.findByProductIdAndCompanyIdOrderByAmendIdDesc(productId, companyId);

            List<CompanyProductRes> productResList = productsecList.stream()
                    .filter(product -> "Y".equalsIgnoreCase(product.getStatus())).map(product -> {
                        CompanyProductRes res = new CompanyProductRes();
                        res.setCompanyId(product.getCompanyId());
                        res.setProductId(product.getProductId());
                        res.setSectionId(String.valueOf(product.getSectionId()));
                        res.setSectionName(product.getSectionName());
                        res.setStatus(product.getStatus());
                        return res;
                    }).collect(Collectors.toList());

            response.setCommonResponse(productResList);
            response.setIsError(false);
            response.setMessage("Success");
            response.setErroCode(200);
            response.setErrorMessage(null);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            response.setIsError(true);
            response.setMessage("Failed to fetch Data");
            response.setErroCode(500);

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
