package com.maan.eway.realpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maan.eway.common.res.CommonRes;

import com.maan.eway.realpay.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.realpay.mapper.ClientAccountDetailsMapper;
import com.maan.eway.realpay.mapper.ContractMapper;
import com.maan.eway.realpay.mapper.InstalmentMapper;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Contract;
import com.maan.eway.realpay.model.Instalment;
import com.maan.eway.realpay.repository.ClientAccountDetailsRepository;
import com.maan.eway.realpay.repository.ContractRepository;
import com.maan.eway.realpay.repository.InstalmentRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;

import lombok.Data;

@Service
public class ClientAccountDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(ClientAccountDetailsService.class);

    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAILED_STATUS = "FAILED";
    private static final String PENDING_STATUS = "PENDING";
    private static final String EXISTS_STATUS = "EXISTS";
    private static final String PARTIAL_SUCCESS = "PARTIAL_SUCCESS";

    private static final String API_RESPONSE = "APIResponse";
    private static final String STATUS_KEY = "Status";
    private static final String CLIENT_POST_RESPONSE = "ClientPostResponse";
    private static final String CONTRACT_POST_RESPONSE = "ContractPostResponse";
    private static final String INSTALMENT_POST_RESPONSE = "InstalmentPostResponse";

    private static final String SUCCESSFUL = "Successful";
    private static final String FAILED = "Failed";
    private static final String FAILURES = "Failures";
    private static final String FAILURE_DESCRIPTION = "FailureDescription";

    private static final String CONTRACT_SEQUENCE = "ContractSequence";
    private static final String CONTRACT_INSTALMENTS = "ContractInstalments";
    private static final String INSTALMENT_REFERENCE_NUMBER = "InstalmentReferenceNumber";
    private static final String INSTALMENT_STATUS = "InstalmentStatus";
    private static final String CALL_SEQUENCE = "CallSequence";
    private static final String DUPLICATE_RECORD_MESSAGE = "DUPLICATE RECORD ALREADY EXISTS";

    private static final String API_TYPE_CLIENT = "client";
    private static final String API_TYPE_CONTRACT = "contract";
    private static final String API_TYPE_INSTALMENT = "instalment";

    private static final int MAX_RESPONSE_BODY_SIZE = 65535;

    @Autowired
    private ClientAccountDetailsRepository repository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmiTransactionDetailsRepository emiTransactionDetailsRepository;

    @Autowired
    private InstalmentRepository instalmentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private HomePositionMasterRepository homeRepo;

    @Autowired
    private PersonalInfoRepository personalRepo;

    @Autowired
    private PaymentInfoRepository paymentInfoRepo;

    @Autowired
    private PaymentDetailRepository paymentDetailRepo;

    @Autowired
    private PremiaPushIntegration premiaPushIntegration;

    @Autowired
    private InstalmentStatusService instalmentStatusService;


    @Data
    private static class ContractAndInstallmentsResult {
        private Contract contract;
        private List<Instalment> installments;
        private String installmentsStatus;
        private String contractReference;
        private List<String> installmentReferences;
    }

    @Data
    private static class FrequencyCalculationResult {
        private String frequencyCode;
        private Integer collectionDay;
        private String frequencyDescription;
        private String dayDescription;
    }

    // TODO: FrequencyCode and CollectionDay
    private String mapInstallmentPeriodToFrequencyCode(String installmentPeriod) {
        if (installmentPeriod == null) {
            logger.warn("Installment period is null, defaulting to MNTH");
            return "MNTH";
        }

        switch (installmentPeriod) {
            case "2":
                logger.debug("Mapped installment period 2 to BIMN (Bimonthly)");
                return "BIMN";
            case "3":
                logger.debug("Mapped installment period 3 to QURT (Quarterly)");
                return "QURT";
            case "6":
                logger.debug("Mapped installment period 6 to MIAN (Bi-annually)");
                return "MIAN";
            case "12":
                logger.debug("Mapped installment period 12 to MNTH (Monthly)");
                return "MNTH";
            default:
                logger.warn("Unknown installment period: {}, defaulting to MNTH", installmentPeriod);
                return "MNTH";
        }
    }

    private Integer calculateCollectionDay(Date dueDate, String frequencyCode) {
        if (dueDate == null) {
            logger.warn("Due date is null, defaulting to collection day 1");
            return 1;
        }

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dueDate);

            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

            if ("MNTH".equals(frequencyCode) || "QURT".equals(frequencyCode) ||
                    "MIAN".equals(frequencyCode) || "BIMN".equals(frequencyCode)) {

                int collectionDay = Math.min(dayOfMonth, 31);
                logger.debug("{} frequency - Collection day: {}", frequencyCode, collectionDay);
                return collectionDay;
            }

            int collectionDay = Math.min(dayOfMonth, 31);
            logger.debug("Default frequency {} - Collection day: {}", frequencyCode, collectionDay);
            return collectionDay;

        } catch (Exception e) {
            logger.error("Error calculating collection day from due date: {}", e.getMessage(), e);
            return 1;
        }
    }

    public String getProviderByCompanyId(String companyId) {
        if ("100049".equals(companyId)) {
            // EwayESWATINI
            return "REALTIME";
        } else if ("100050".equals(companyId)) {
            // EwayNAMIBIA
            return "FNBENDO";
        } else {
            logger.warn("Unknown company ID: {}, defaulting to NAMPAY provider", companyId);
            return "FNBENDO";
        }
    }

    @Transactional
    private void calculateAndSetFrequencyDetailsToEmi(String quoteNo, String companyId) {
        try {
            logger.info("Calculating and setting frequency details to EMI table - QuoteNo: {}", quoteNo);

            List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository
                    .findByQuoteNoAndCompanyIdOrderByInstalmentAsc(quoteNo, companyId);

            if (emiDetails.isEmpty()) {
                logger.warn("No EMI details found for quote: {}", quoteNo);
                return;
            }

            EmiTransactionDetails firstEmi = emiDetails.get(0);
            String installmentPeriod = firstEmi.getInstallmentPeriod();

            String frequencyCode = mapInstallmentPeriodToFrequencyCode(installmentPeriod);

            for (EmiTransactionDetails emiDetail : emiDetails) {
                Date dueDate = emiDetail.getDueDate();

                Integer collectionDay = calculateCollectionDay(dueDate, frequencyCode);

                emiDetail.setFrequencyCode(frequencyCode);
                emiDetail.setCollectionDay(collectionDay);

                logger.debug("Updated EMI detail - Instalment: {}, InstallmentPeriod: {}, FrequencyCode: {}, " +
                        "DueDate: {}, CollectionDay: {}",
                        emiDetail.getInstalment(), installmentPeriod, frequencyCode, dueDate, collectionDay);
            }

            emiTransactionDetailsRepository.saveAll(emiDetails);

            logger.info("Successfully calculated and saved frequency details to {} EMI records for quote: {}",
                    emiDetails.size(), quoteNo);

        } catch (Exception e) {
            logger.error("Error calculating and setting frequency details to EMI table: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public UnifiedClientCreationResponseDTO createClientWithFullResponse(ClientAccountDetailsDTO dto) {
        UnifiedClientCreationResponseDTO response = new UnifiedClientCreationResponseDTO();
        response.setTimestamp(LocalDateTime.now());

        UnifiedClientCreationResponseDTO.ClientCreationStatus status = new UnifiedClientCreationResponseDTO.ClientCreationStatus();
        response.setStatus(status);

        ClientAccountDetails entity = null;

        try {

            Optional<ClientAccountDetails> existingExact = repository
                    .findByClientNumberAndQuoteNo(
                            dto.getClientNumber(), dto.getQuoteNo());

            ///  here check logic for endorsement
            EndorsementCheckDto endorsementCheckDto = checkAndReturnOriginalQuoteNo(dto.getQuoteNo(), dto.getCompanyId());
            if(endorsementCheckDto.endorsement) {
                 existingExact = repository
                        .findByClientNumberAndQuoteNo(
                                dto.getClientNumber(), endorsementCheckDto.getOriginalQuoteNo());
                 logger.info("Going into endorsement flow for realpay adjustment for {} ...",dto.getQuoteNo());
               return handleEndorsement(existingExact,dto,response);
            }

            /// New quote flow ...
            logger.info("Starting client creation process for: {}", dto.getClientNumber());
            System.out.println("---UI Request: " + dto);

            validateClientDTO(dto);
            calculateAndSetFrequencyDetailsToEmi(dto.getQuoteNo(), dto.getCompanyId());

            if (existingExact.isPresent()) {
                logger.info("Exact match found - reusing client record for client={}, quote={}, paymentId={}",
                        dto.getClientNumber(), dto.getQuoteNo(), dto.getPaymentId());
                dto.setClientNumber(existingExact.get().getClientNumber());
                entity = createAndSaveClientEntity(dto);
                return handleExistingClientWithFullResponse(existingExact.get(), dto, response);
            }

//            Optional<ClientAccountDetails> latestClientOpt = repository.findLatestByClientNumber(dto.getClientNumber());

            try {
                entity = createAndSaveClientEntity(dto);
                logger.info("NEW client_account_details row created - ID:{}, client:{}, quote:{}, payment:{}",
                        entity.getId(), dto.getClientNumber(), dto.getQuoteNo(), dto.getPaymentId());
            } catch (DataIntegrityViolationException e) {
                logger.warn("DUPLICATE KEY - concurrent creation detected for: {}", dto.getClientNumber());

                Optional<ClientAccountDetails> concurrentExact = repository
                        .findByClientNumberAndQuoteNo(
                                dto.getClientNumber(), dto.getQuoteNo());

                if (concurrentExact.isPresent()) {
                    logger.info("Using concurrent record: ID={}", concurrentExact.get().getId());
                    return handleExistingClientWithFullResponse(concurrentExact.get(), dto, response);
                }
                throw e;
            }

            JsonNode apiResponse = callClientAPI(entity);
            logger.debug("Client API response: {}", apiResponse);
            System.out.println("----ClientResponse: " + apiResponse);

            boolean apiSuccess = processClientResponse(entity, apiResponse);

            if (apiSuccess) {
                entity.setStatus(SUCCESS_STATUS);
                entity.setMessage("Client successfully created in RealPay system with reference: "
                        + entity.getClientReference());
                repository.save(entity);

                ContractAndInstallmentsResult result = createContractAndInstallmentsWithResult(entity, dto);

                response.setClientDetails(ClientAccountDetailsMapper.toDto(entity));
                response.setContractDetails(ContractMapper.toDto(result.getContract()));
                response.setInstallmentDetails(
                        result.getInstallments().stream().map(InstalmentMapper::toDto).collect(Collectors.toList()));

                status.setClientStatus(SUCCESS_STATUS);
                status.setContractStatus(SUCCESS_STATUS);
                status.setInstallmentsStatus(result.getInstallmentsStatus());
                status.setClientReference(entity.getClientReference());
                status.setContractReference(result.getContractReference());
                if (result.getInstallmentReferences() != null && !result.getInstallmentReferences().isEmpty()) {
                    status.setInstallmentReference(String.join(",", result.getInstallmentReferences()));
                }

                response.setMessage("Client, contract, payment detail and installments created successfully");
                logger.info("Client creation completed successfully for ID: {}", entity.getId());
            } else {
                handleClientCreationFailure(entity, response, status);
            }

        } catch (ValidationException e) {
            handleValidationException(response, status, e);
        } catch (Exception e) {
            handleGenericException(response, status, e, entity);
        }

        return response;
    }

    @Transactional
    protected PaymentDetail createPaymentDetailRecord(ClientAccountDetailsDTO dto) {
        System.out.println("---createPaymentDetailRecord");
        System.out.println("---In");

        try {
            String quoteNo = dto.getQuoteNo();
            String merchantReference = dto.getMerchantReference();
            String paymentId = dto.getPaymentId();

            List<PaymentDetail> existingPaymentDetails = paymentDetailRepo.findByQuoteNoAndPaymentId(quoteNo,
                    paymentId);
            if (existingPaymentDetails != null && !existingPaymentDetails.isEmpty()) {
                logger.info("PaymentDetail already exists for quote: {} and paymentId: {}", quoteNo, paymentId);
                System.out.println("---Out (already exists)");
                return existingPaymentDetails.get(0);
            }

            HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
            if (homeData == null) {
                logger.warn("Home position master not found - Quote: {}", quoteNo);
                System.out.println("---Out (no home data)");
                return null;
            }

            PersonalInfo personalData = personalRepo.findByCustomerId(homeData.getCustomerId());
            if (personalData == null) {
                logger.warn("Personal info not found - Customer: {}", homeData.getCustomerId());
                System.out.println("---Out (no personal data)");
                return null;
            }

            PaymentInfo paymentInfo = paymentInfoRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);
            if (paymentInfo==null) {
                logger.warn("Payment info not found - Quote: {}", quoteNo);
                System.out.println("---Out (no payment info)");
                return null;
            }

            List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository
                    .findByQuoteNoAndCompanyIdOrderByInstalmentAsc(quoteNo, dto.getCompanyId());

            if (emiDetails.isEmpty()) {
                logger.warn("No EMI transaction details found for quote: {}", quoteNo);
                System.out.println("---Out (no EMI details)");
                return null;
            }

            EmiTransactionDetails firstEmi = emiDetails.get(0);

            String companyName = getInscompanyMasterDropdown(homeData.getCompanyId());
            String branchName = getCompanyBranchMasterDropdown(homeData.getCompanyId(), homeData.getBranchCode());
            String paymentMode = getListItem(homeData.getCompanyId(), homeData.getBranchCode(), "PAYMENT_MODE",
                    dto.getPaymentType());

            PaymentDetail paymentDetail = new PaymentDetail();
            paymentDetail.setBranchCode(homeData.getBranchCode());
            paymentDetail.setBranchName(branchName);
            paymentDetail.setCreatedBy("SYSTEM");
            paymentDetail.setPaymentType(dto.getPaymentType());
            paymentDetail.setPaymentTypedesc(paymentMode);
            paymentDetail.setCustomerName(personalData.getClientName());
            paymentDetail.setEntryDate(new Date());
            paymentDetail.setMerchantReference(merchantReference);
            paymentDetail.setPaymentStatus("Pending");
            paymentDetail.setQuoteNo(quoteNo);
            paymentDetail.setUpdatedBy("SYSTEM");
            paymentDetail.setUpdatedDate(new Date());
            paymentDetail.setPaymentId(paymentId);
            paymentDetail.setCustomerEmail(personalData.getEmail1());
            paymentDetail.setCustomerId(personalData.getCustomerId());
            paymentDetail.setCompanyId(homeData.getCompanyId());

            paymentDetail.setEmiYn(StringUtils.hasText(paymentInfo.getEmiYn()) ? paymentInfo.getEmiYn() : "N");
            paymentDetail.setInstallmentMonth(paymentInfo.getInstallmentMonth());
            paymentDetail.setInstallmentPeriod(paymentInfo.getInstallmentPeriod());

            paymentDetail.setReqBillToAddressCity(personalData.getCityName());
            paymentDetail.setReqBillToAddressLine1(personalData.getAddress1());
            paymentDetail.setReqBillToAddressLine2(personalData.getAddress2());
            paymentDetail.setReqBillToAddrPostalCode(personalData.getPinCode());
            paymentDetail.setReqBillToEmail(personalData.getEmail1());
            paymentDetail.setReqBillToForename(personalData.getClientName());
            paymentDetail.setReqBillToSurname(personalData.getClientName());
            paymentDetail.setReqBillToCompanyName(companyName);
            paymentDetail.setReqBillToAddressState(personalData.getStateName());
            paymentDetail.setReqBillToCountry(personalData.getNationality());

            String personalInfoMobile = personalData.getMobileCode1() + "" + personalData.getMobileNo1();
            paymentDetail.setReqBillToPhone(personalInfoMobile);
            paymentDetail.setWhatsappCode(personalData.getMobileCode1());
            paymentDetail.setWhatsappNo(personalData.getMobileNo1());

            if (StringUtils.hasText(firstEmi.getInstalmentReferenceNumber())) {
                paymentDetail.setInstalmentReferenceNumber(firstEmi.getInstalmentReferenceNumber());
            }

            // Set premium and amount from first installment
            BigDecimal instalmentAmount = BigDecimal.valueOf(firstEmi.getDueAmount());
            paymentDetail.setPremium(instalmentAmount);
            paymentDetail.setPremiumLc(instalmentAmount);
            paymentDetail.setPremiumFc(paymentInfo.getPremiumFc());
            paymentDetail.setCurrencyId(paymentInfo.getCurrencyId());
            paymentDetail.setExchangeRate(paymentInfo.getExchangeRate());

            if ("5".equals(dto.getPaymentType())) {
                paymentDetail.setAccountNumber(
                        dto.getAccountNumber() != null ? dto.getAccountNumber().toString() : null);
                paymentDetail.setBankCode(
                        dto.getBankCode() != null ? dto.getBankCode().toString() : null);
                paymentDetail.setBankName(getBankName(dto.getBankCode()));
            }

            setValidityDate(paymentDetail, homeData);
            paymentDetail.setShorternUrl(getTinyUrl(quoteNo));

            PaymentDetail savedPaymentDetail = paymentDetailRepo.saveAndFlush(paymentDetail);

            logger.info("Payment detail created successfully - Quote: {}, PaymentId: {}", quoteNo, paymentId);
            System.out.println("#####paymentDetail: " + savedPaymentDetail);
            System.out.println("---Out (success)");
            return savedPaymentDetail;

        } catch (Exception e) {
            logger.error("Error creating payment detail: {}", e.getMessage(), e);
            System.out.println("---Out (error)");
            return null;
        }
    }

    private JsonNode callClientAPI(ClientAccountDetails client) {
        try {
            Map<String, Object> request = createClientRequest(client);
            List<Map<String, Object>> requestList = Collections.singletonList(request);

            String provider = getProviderByCompanyId(client.getCompanyId());

            try {
                String requestBodyJson = objectMapper.writeValueAsString(requestList);

                setResponseBodySafely(requestBodyJson, client::setRequestBody);
                repository.save(client);
                logger.debug("Client request body saved: {}", requestBodyJson);
            } catch (JsonProcessingException e) {
                logger.error("Error serializing client request body: {}", e.getMessage());
            }

            logger.debug("Third-party API request for client: {}", requestList);
            System.out.println("----ClientRequest: " + requestList);

            return callClientAPI(provider, requestList, API_TYPE_CLIENT);
        } catch (Exception e) {
            logger.error("Third-party API call failed: {}", e.getMessage(), e);
            throw new ClientAPIException("Failed to call third-party API", e);
        }
    }

    private boolean processClientResponse(ClientAccountDetails client, JsonNode response) {
        try {
            if (response == null) {
                client.setMessage("No response from third-party API");
                return false;
            }

            setResponseBodySafely(response.toString(), client::setResponseBody);

            if (response.has(API_RESPONSE)) {
                JsonNode apiResponse = response.get(API_RESPONSE);
                if (apiResponse.has(STATUS_KEY)) {
                    String status = apiResponse.get(STATUS_KEY).asText();
                    if (SUCCESS_STATUS.equals(status)) {
                        if (apiResponse.has(CALL_SEQUENCE)) {
                            client.setClientReference(apiResponse.get(CALL_SEQUENCE).asText());
                        }
                        return true;
                    } else {
                        client.setMessage("API responded with status: " + status);
                        return false;
                    }
                }
            }

            if (response.has(CLIENT_POST_RESPONSE)) {
                JsonNode postResponse = response.get(CLIENT_POST_RESPONSE);
                if (postResponse.isArray() && postResponse.size() > 0) {
                    JsonNode responseElement = postResponse.get(0);

                    if (responseElement.has(SUCCESSFUL)) {
                        JsonNode successful = responseElement.get(SUCCESSFUL);
                        if (successful.isArray() && successful.size() > 0) {
                            if (response.has(API_RESPONSE)) {
                                JsonNode apiResponse = response.get(API_RESPONSE);
                                if (apiResponse.has(CALL_SEQUENCE)) {
                                    client.setClientReference(apiResponse.get(CALL_SEQUENCE).asText());
                                }
                            }
                            return true;
                        }
                    }

                    if (responseElement.has(FAILED)) {
                        JsonNode failed = responseElement.get(FAILED);
                        if (failed.isArray() && failed.size() > 0) {
                            JsonNode failedArray = failed.get(0);
                            if (failedArray.isArray() && failedArray.size() > 0) {
                                JsonNode failedClient = failedArray.get(0);

                                if (failedClient.has(FAILURES)) {
                                    JsonNode failures = failedClient.get(FAILURES);
                                    if (failures.isArray() && failures.size() > 0) {
                                        JsonNode failure = failures.get(0);
                                        String failureDesc = failure.has(FAILURE_DESCRIPTION)
                                                ? failure.get(FAILURE_DESCRIPTION).asText()
                                                : "Unknown error";

                                        if (failureDesc.contains(DUPLICATE_RECORD_MESSAGE)) {
                                            client.setMessage("Client already exists in third-party system");
                                            return true;
                                        } else {
                                            client.setMessage("API error: " + failureDesc);
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            client.setMessage("Unexpected response format from third-party API");
            return false;

        } catch (Exception e) {
            logger.error("Error processing third-party response: {}", e.getMessage(), e);
            client.setMessage("Error processing API response: " + e.getMessage());
            return false;
        }
    }

    private void handleClientCreationFailure(ClientAccountDetails entity, UnifiedClientCreationResponseDTO response,
            UnifiedClientCreationResponseDTO.ClientCreationStatus status) {

        updateClientStatusInSeparateTransaction(entity, FAILED_STATUS,
                "Client creation failed in RealPay system - API returned error response");

        response.setClientDetails(ClientAccountDetailsMapper.toDto(entity));
        status.setClientStatus(FAILED_STATUS);
        status.setContractStatus("NOT_CREATED");
        status.setInstallmentsStatus("NOT_CREATED");
        response.setMessage("Client creation failed in third-party system");

        logger.error("Client creation failed in third-party system for: {}", entity.getClientNumber());

        throw new ClientAPIException("Client creation failed in third-party system", null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void updateClientStatusInSeparateTransaction(ClientAccountDetails entity, String status, String message) {
        try {
            entity.setStatus(status);
            entity.setMessage(message);
            entity.setUpdatedDate(LocalDateTime.now());
            repository.save(entity);
            logger.info("CLIENT_STATUS_UPDATED_IN_SEPARATE_TX - ID: {}, Status: {}", entity.getId(), status);
        } catch (Exception e) {
            logger.error("ERROR_UPDATING_CLIENT_STATUS: {}", e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private ContractAndInstallmentsResult createContractAndInstallmentsWithResult(ClientAccountDetails client,
            ClientAccountDetailsDTO dto) {
        ContractAndInstallmentsResult result = new ContractAndInstallmentsResult();

        try {
            Contract contract = createContract(client, dto);
            result.setContract(contract);

            createInstallmentsFromEMITransactions(client, contract, dto.getQuoteNo(), dto.getCompanyId());
            updateContractWithFirstInstallment(contract);

            JsonNode contractResponse = callContractAPI(contract);

            logger.debug("Contract API response: {}", contractResponse);
            System.out.println("----ContractResponse: " + contractResponse);

            boolean contractSuccess = processContractAPIResponse(contract, contractResponse);

            if (contractSuccess) {
                contract.setStatus(SUCCESS_STATUS);
                contract.setMessage("Contract successfully created in RealPay system with sequence: "
                        + contract.getContractSequence());
                contractRepository.save(contract);

                result.setContractReference(contract.getContractSequence());

                updateFirstInstallmentFromContractResponse(contract, contractResponse);

                List<Instalment> allInstallments = instalmentRepository
                        .findByContractOrderByNoOfInstalmentAsc(contract);
                result.setInstallments(allInstallments);

                if (allInstallments.size() > 1) {

                    String installmentsStatus = createRemainingInstallmentsViaAPI(client, contract, allInstallments);
                    result.setInstallmentsStatus(installmentsStatus);
                } else {
                    result.setInstallmentsStatus(SUCCESS_STATUS);
                }

                List<String> installmentRefs = instalmentRepository.findByContractOrderByNoOfInstalmentAsc(contract)
                        .stream().filter(i -> i.getInstalmentReferenceNumber() != null)
                        .map(Instalment::getInstalmentReferenceNumber).collect(Collectors.toList());
                result.setInstallmentReferences(installmentRefs);

                logger.info("Contract creation completed successfully for contract: {}", contract.getContractNumber());
            } else {

                contract.setStatus(FAILED_STATUS);
                contract.setMessage("Contract creation failed in RealPay system - API returned error response");
                contractRepository.save(contract);
                result.setInstallmentsStatus(FAILED_STATUS);

                logger.error("CONTRACT_CREATION_FAILED - Returning failed result without exception");
            }
        } catch (ClientAPIException e) {
            logger.error("Error creating contract and installments: {}", e.getMessage(), e);
            result.setInstallmentsStatus(FAILED_STATUS);
            throw e;
        } catch (Exception e) {
            logger.error("Error creating contract and installments: {}", e.getMessage(), e);
            result.setInstallmentsStatus(FAILED_STATUS);
            throw new ClientAPIException("Failed to create contract and installments", e);
        }

        return result;
    }

    private void createInstallmentsFromEMITransactions(ClientAccountDetails client, Contract contract, String quoteNo,
            String companyId) {
        List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository
                .findByQuoteNoAndCompanyIdOrderByInstalmentAsc(quoteNo, companyId);

        if (emiDetails.isEmpty()) {
            String errorMsg = "No EMI transaction details found for quote: " + quoteNo
                    + " - Cannot create contract without installments";
            logger.error(errorMsg);
            throw new ClientAPIException(errorMsg, null);
        }

        List<Instalment> instalmentsToSave = new ArrayList<>();

        for (EmiTransactionDetails emiDetail : emiDetails) {
            if (!validateEmiDetail(emiDetail)) {
                logger.warn("Skipping invalid EMI detail: {}", emiDetail.getInstalment());
                continue;
            }

            Instalment instalment = createInstalmentFromEmiDetail(client, contract, quoteNo, emiDetail);
            instalmentsToSave.add(instalment);
        }

        if (!instalmentsToSave.isEmpty()) {
            instalmentRepository.saveAll(instalmentsToSave);
            logger.info("Created {} instalments for contract: {}", instalmentsToSave.size(),
                    contract.getContractNumber());
        } else {
            throw new ClientAPIException("No valid EMI transactions found for quote: " + quoteNo, null);
        }
    }

    private String createRemainingInstallmentsViaAPI(ClientAccountDetails client, Contract contract,
            List<Instalment> allInstallments) {
        List<Instalment> remainingInstallments = allInstallments.stream().filter(i -> i.getNoOfInstalment() > 1)
                .collect(Collectors.toList());

        logger.info("Processing {} remaining installments for contract: {}", remainingInstallments.size(),
                contract.getContractNumber());

        int successCount = 0;
        int failureCount = 0;

        for (Instalment instalment : remainingInstallments) {
            try {
                JsonNode response = callInstallmentAPI(instalment);

                logger.debug("Installment API response: {}", response);
                System.out.println("----InstallmentResponse: " + response);

                boolean success = processInstallmentAPIResponse(instalment, response);

                if (success) {
                    instalment.setStatus(SUCCESS_STATUS);
                    instalment.setMessage("Installment successfully created in RealPay system with reference: "
                            + instalment.getInstalmentReferenceNumber());

                    updateEmiTransactionDetailsWithReference(instalment.getQuoteNo(),
                            instalment.getNoOfInstalment().toString(), instalment.getInstalmentReferenceNumber());

                    logger.info("Successfully processed installment {} for contract: {}",
                            instalment.getNoOfInstalment(), contract.getContractNumber());
                    successCount++;
                } else {
                    instalment.setStatus(FAILED_STATUS);
                    instalment
                            .setMessage("Installment creation failed in RealPay system - API returned error response");
                    logger.error("Failed to process installment {} for contract: {}", instalment.getNoOfInstalment(),
                            contract.getContractNumber());
                    failureCount++;
                }

                instalment.setUpdatedDate(LocalDateTime.now());
                instalmentRepository.save(instalment);

            } catch (Exception e) {
                logger.error("Installment API call failed for installment {}: {}", instalment.getNoOfInstalment(),
                        e.getMessage(), e);
                instalment.setStatus(FAILED_STATUS);
                instalment.setMessage("API call error: " + e.getMessage());
                instalment.setUpdatedDate(LocalDateTime.now());
                instalmentRepository.save(instalment);
                failureCount++;
            }
        }

        if (failureCount == 0) {
            logger.info("ALL_INSTALLMENTS_CREATED_SUCCESSFULLY - Count: {}", successCount);
            return SUCCESS_STATUS;
        } else if (successCount > 0) {
            logger.warn("PARTIAL_INSTALLMENT_CREATION - Success: {}, Failed: {}", successCount, failureCount);
            return PARTIAL_SUCCESS;
        } else {
            logger.error("ALL_INSTALLMENTS_FAILED - Count: {}", failureCount);
            return FAILED_STATUS;
        }
    }

    private JsonNode callContractAPI(Contract contract) {
        try {
            Map<String, Object> request = createContractAPIRequest(contract);
            List<Map<String, Object>> requestList = Collections.singletonList(request);

            String provider = getProviderByCompanyId(contract.getClientAccount().getCompanyId());

            try {
                String requestBodyJson = objectMapper.writeValueAsString(requestList);
                setResponseBodySafely(requestBodyJson, contract::setRequestBody);
                contractRepository.save(contract);
                logger.debug("Contract request body saved: {}", requestBodyJson);
            } catch (JsonProcessingException e) {
                logger.error("Error serializing contract request body: {}", e.getMessage());
            }

            logger.debug("Contract API request: {}", requestList);
            System.out.println("----ContractRequest: " + requestList);

            return callClientAPI(provider, requestList, API_TYPE_CONTRACT);
        } catch (Exception e) {
            logger.error("Contract API call failed: {}", e.getMessage(), e);
            throw new ClientAPIException("Failed to call contract API", e);
        }
    }

    private boolean processContractAPIResponse(Contract contract, JsonNode response) {
        try {
            if (response == null) {
                contract.setMessage("No response from contract API");
                return false;
            }

            setResponseBodySafely(response.toString(), contract::setResponseBody);

            if (response.has(API_RESPONSE)) {
                JsonNode apiResponse = response.get(API_RESPONSE);
                if (apiResponse.has(STATUS_KEY)) {
                    String status = apiResponse.get(STATUS_KEY).asText();
                    if (SUCCESS_STATUS.equals(status)) {
                        if (response.has(CONTRACT_POST_RESPONSE)) {
                            JsonNode postResponse = response.get(CONTRACT_POST_RESPONSE);
                            if (postResponse.isArray() && postResponse.size() > 0) {
                                JsonNode responseElement = postResponse.get(0);
                                if (responseElement.has(SUCCESSFUL)) {
                                    JsonNode successful = responseElement.get(SUCCESSFUL);
                                    if (successful.isArray() && successful.size() > 0) {
                                        JsonNode successfulContract = successful.get(0);
                                        if (successfulContract.has(CONTRACT_SEQUENCE)) {
                                            String contractSequence = successfulContract.get(CONTRACT_SEQUENCE)
                                                    .asText();
                                            contract.setContractSequence(contractSequence);
                                            contractRepository.save(contract);

                                            updateInstallmentsContractSequence(contract, contractSequence);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        contract.setMessage("API responded with status: " + status);
                        return false;
                    }
                }
            }

            if (response.has(CONTRACT_POST_RESPONSE)) {
                JsonNode postResponse = response.get(CONTRACT_POST_RESPONSE);
                if (postResponse.isArray() && postResponse.size() > 0) {
                    JsonNode responseElement = postResponse.get(0);

                    if (responseElement.has(SUCCESSFUL)) {
                        JsonNode successful = responseElement.get(SUCCESSFUL);
                        if (successful.isArray() && successful.size() > 0) {
                            JsonNode successfulContract = successful.get(0);

                            if (successfulContract.has(CONTRACT_SEQUENCE)) {
                                String contractSequence = successfulContract.get(CONTRACT_SEQUENCE).asText();
                                contract.setContractSequence(contractSequence);
                                contractRepository.save(contract);

                                updateInstallmentsContractSequence(contract, contractSequence);

                                return true;
                            }
                        }
                    }

                    if (responseElement.has(FAILED)) {
                        JsonNode failed = responseElement.get(FAILED);
                        if (failed.isArray() && failed.size() > 0) {
                            JsonNode failedArray = failed.get(0);
                            if (failedArray.isArray() && failedArray.size() > 0) {
                                JsonNode failedContract = failedArray.get(0);

                                if (failedContract.has(FAILURES)) {
                                    JsonNode failures = failedContract.get(FAILURES);
                                    if (failures.isArray() && failures.size() > 0) {
                                        JsonNode failure = failures.get(0);
                                        String failureDesc = failure.has(FAILURE_DESCRIPTION)
                                                ? failure.get(FAILURE_DESCRIPTION).asText()
                                                : "Unknown error";

                                        contract.setMessage("API error: " + failureDesc);
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            contract.setMessage("Unexpected response format from contract API");
            return false;

        } catch (Exception e) {
            logger.error("Error processing contract API response: {}", e.getMessage(), e);
            contract.setMessage("Error processing API response: " + e.getMessage());
            return false;
        }
    }

    private void updateInstallmentsContractSequence(Contract contract, String contractSequence) {
        try {
            Integer sequence = null;
            try {
                sequence = Integer.parseInt(contractSequence);
            } catch (NumberFormatException e) {
                logger.error("INVALID_CONTRACT_SEQUENCE_FORMAT - Value: {}, Error: {}", contractSequence,
                        e.getMessage());
                return;
            }

            List<Instalment> installments = instalmentRepository.findByContract(contract);
            for (Instalment instalment : installments) {
                instalment.setContractSequence(sequence);
            }
            instalmentRepository.saveAll(installments);
            logger.info("UPDATED_INSTALLMENTS_CONTRACT_SEQUENCE - Count: {}, Sequence: {}", installments.size(),
                    sequence);
        } catch (Exception e) {
            logger.error("ERROR_UPDATING_INSTALLMENTS_CONTRACT_SEQUENCE: {}", e.getMessage(), e);
        }
    }

    private void updateFirstInstallmentFromContractResponse(Contract contract, JsonNode response) {
        try {
            List<Instalment> installments = instalmentRepository.findByContractOrderByNoOfInstalmentAsc(contract);
            if (installments.isEmpty()) {
                logger.warn("No installments found for contract: {}", contract.getContractNumber());
                return;
            }

            Instalment firstInstallment = installments.get(0);

            if (response.has(CONTRACT_POST_RESPONSE)) {
                JsonNode postResponse = response.get(CONTRACT_POST_RESPONSE);
                if (postResponse.isArray() && postResponse.size() > 0) {
                    JsonNode responseElement = postResponse.get(0);
                    if (responseElement.has(SUCCESSFUL)) {
                        JsonNode successful = responseElement.get(SUCCESSFUL);
                        if (successful.isArray() && successful.size() > 0) {
                            JsonNode successfulContract = successful.get(0);
                            if (successfulContract.has(CONTRACT_INSTALMENTS)) {
                                JsonNode contractInstalments = successfulContract.get(CONTRACT_INSTALMENTS);
                                if (contractInstalments.isArray()) {
                                    for (JsonNode instalmentNode : contractInstalments) {
                                        if (instalmentNode.has("InstalmentSequence")
                                                && instalmentNode.get("InstalmentSequence").asInt() == 1) {
                                            if (instalmentNode.has(INSTALMENT_REFERENCE_NUMBER)) {
                                                String referenceNumber = instalmentNode.get(INSTALMENT_REFERENCE_NUMBER)
                                                        .asText();
                                                firstInstallment.setInstalmentReferenceNumber(referenceNumber);

                                                updateEmiTransactionDetailsWithReference(firstInstallment.getQuoteNo(),
                                                        firstInstallment.getNoOfInstalment().toString(),
                                                        referenceNumber);
                                            }
                                            if (instalmentNode.has(INSTALMENT_STATUS)) {
                                                firstInstallment.setInstalmentStatus(
                                                        instalmentNode.get(INSTALMENT_STATUS).asText());
                                            }
                                            firstInstallment.setStatus(SUCCESS_STATUS);
                                            firstInstallment.setMessage(
                                                    "First installment successfully created in RealPay system with reference: "
                                                            + firstInstallment.getInstalmentReferenceNumber());
                                            firstInstallment.setUpdatedDate(LocalDateTime.now());
                                            instalmentRepository.save(firstInstallment);
                                            logger.info("Updated first installment with reference number: {}",
                                                    firstInstallment.getInstalmentReferenceNumber());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error updating first installment from contract response: {}", e.getMessage(), e);
        }
    }

    private JsonNode callInstallmentAPI(Instalment instalment) {
        try {
            Map<String, Object> request = createInstallmentAPIRequest(instalment);
            List<Map<String, Object>> requestList = Collections.singletonList(request);

            String provider = getProviderByCompanyId(instalment.getClientAccount().getCompanyId());

            try {
                String requestBodyJson = objectMapper.writeValueAsString(requestList);
                setResponseBodySafely(requestBodyJson, instalment::setRequestBody);
                instalmentRepository.save(instalment);
                logger.debug("Installment request body saved: {}", requestBodyJson);
            } catch (JsonProcessingException e) {
                logger.error("Error serializing installment request body: {}", e.getMessage());
            }

            logger.debug("Installment API request: {}", requestList);
            System.out.println("----InstallmentRequest: " + requestList);

            return callClientAPI(provider, requestList, API_TYPE_INSTALMENT);
        } catch (Exception e) {
            logger.error("Installment API call failed: {}", e.getMessage(), e);
            throw new ClientAPIException("Failed to call installment API", e);
        }
    }

    private boolean processInstallmentAPIResponse(Instalment instalment, JsonNode response) {
        try {
            if (response == null) {
                instalment.setMessage("No response from installment API");
                return false;
            }

            setResponseBodySafely(response.toString(), instalment::setResponseBody);

            if (response.has(API_RESPONSE)) {
                JsonNode apiResponse = response.get(API_RESPONSE);
                if (apiResponse.has(STATUS_KEY)) {
                    String status = apiResponse.get(STATUS_KEY).asText();
                    if (SUCCESS_STATUS.equals(status)) {
                        if (response.has(INSTALMENT_POST_RESPONSE)) {
                            JsonNode postResponse = response.get(INSTALMENT_POST_RESPONSE);
                            if (postResponse.isArray() && postResponse.size() > 0) {
                                JsonNode responseElement = postResponse.get(0);

                                if (responseElement.has(SUCCESSFUL)) {
                                    JsonNode successful = responseElement.get(SUCCESSFUL);
                                    if (successful.isArray() && successful.size() > 0) {
                                        JsonNode successfulInstalment = successful.get(0);

                                        if (successfulInstalment.has(INSTALMENT_REFERENCE_NUMBER)) {
                                            String refNumber = successfulInstalment.get(INSTALMENT_REFERENCE_NUMBER)
                                                    .asText();
                                            instalment.setInstalmentReferenceNumber(refNumber);

                                            updateEmiTransactionDetailsWithReference(instalment.getQuoteNo(),
                                                    instalment.getNoOfInstalment().toString(), refNumber);
                                        }

                                        if (successfulInstalment.has(INSTALMENT_STATUS)) {
                                            String instalmentStatus = successfulInstalment.get(INSTALMENT_STATUS)
                                                    .asText();
                                            instalment.setInstalmentStatus(instalmentStatus);
                                            logger.debug("Updated instalment {} with status: {}",
                                                    instalment.getNoOfInstalment(), instalmentStatus);
                                        }

                                        return true;
                                    }
                                }
                            }
                        }
                    } else {
                        instalment.setMessage("API responded with status: " + status);
                        return false;
                    }
                }
            }

            instalment.setMessage("Unexpected response format from installment API");
            return false;

        } catch (Exception e) {
            logger.error("Error processing installment API response: {}", e.getMessage(), e);
            instalment.setMessage("Error processing API response: " + e.getMessage());
            return false;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void updateEmiTransactionDetailsWithReference(String quoteNo, String installmentNumber,
            String instalmentReferenceNumber) {
        System.out.println("---updateEmiTransactionDetailsWithReference");
        System.out.println("---In");
        try {
            List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository.findByQuoteNoAndInstalment(quoteNo,
                    installmentNumber);

            if (emiDetails != null && !emiDetails.isEmpty()) {
                for (EmiTransactionDetails emiDetail : emiDetails) {
                    emiDetail.setInstalmentReferenceNumber(instalmentReferenceNumber);
                    // Payment status and date can be updated here if needed
                    // emiDetail.setPaymentStatus("Pending");
                    // emiDetail.setPaymentDate(new Date());
                }

                emiTransactionDetailsRepository.saveAll(emiDetails);

                System.out.println("##########EMI TransactionDetails updated successfully");
                logger.info("EMI transaction details updated - Quote: {}, Installment: {}, Reference: {}", quoteNo,
                        installmentNumber, instalmentReferenceNumber);
                System.out.println("---Out (true)");
            } else {
                logger.warn("EMI transaction details not found - Quote: {}, Installment: {}", quoteNo,
                        installmentNumber);
                System.out.println("---Out (false)");
            }
        } catch (Exception e) {
            logger.error("Error updating EMI transaction details: {}", e.getMessage(), e);
            System.out.println("---Out (error)");
        }
    }

    private UnifiedClientCreationResponseDTO handleExistingClientWithFullResponse(ClientAccountDetails existingClient,
            ClientAccountDetailsDTO dto, UnifiedClientCreationResponseDTO response) {

        try {
            UnifiedClientCreationResponseDTO.ClientCreationStatus status = response.getStatus();

            updateExistingClientWithQuoteInfo(existingClient, dto);

            calculateAndSetFrequencyDetailsToEmi(dto.getQuoteNo(), dto.getCompanyId());

            Optional<Contract> existingContractOpt = contractRepository.findByClientAccountAndQuoteNo(existingClient,
                    dto.getQuoteNo());

            Contract contract;
            List<Instalment> installments;

            if (existingContractOpt.isPresent()) {
                logger.info("Contract already exists for quote: {}", dto.getQuoteNo());
                contract = existingContractOpt.get();
                installments = instalmentRepository.findByContract(contract);

                handleExistingContractInstallments(existingClient, contract, dto);

                status.setClientStatus(EXISTS_STATUS);
                status.setContractStatus(EXISTS_STATUS);
                status.setInstallmentsStatus(EXISTS_STATUS);

                status.setClientReference(existingClient.getClientReference());
                status.setContractReference(contract.getContractSequence());
                List<String> installmentRefs = installments.stream()
                        .filter(i -> i.getInstalmentReferenceNumber() != null)
                        .map(Instalment::getInstalmentReferenceNumber).collect(Collectors.toList());
                if (!installmentRefs.isEmpty()) {
                    status.setInstallmentReference(String.join(",", installmentRefs));
                }

                existingClient.setMessage("Existing client updated with new quote: " + dto.getQuoteNo()
                        + ", contract and installments already exist");
            } else {
                ContractAndInstallmentsResult result = createContractAndInstallmentsWithResult(existingClient, dto);
                contract = result.getContract();
                installments = result.getInstallments();

                status.setClientStatus(EXISTS_STATUS);
                status.setContractStatus(SUCCESS_STATUS);
                status.setInstallmentsStatus(result.getInstallmentsStatus());

                status.setClientReference(existingClient.getClientReference());
                status.setContractReference(result.getContractReference());
                if (result.getInstallmentReferences() != null && !result.getInstallmentReferences().isEmpty()) {
                    status.setInstallmentReference(String.join(",", result.getInstallmentReferences()));
                }

                existingClient.setMessage(
                        "Existing client updated with new contract and installments for quote: " + dto.getQuoteNo());
            }

            repository.save(existingClient);

            response.setClientDetails(ClientAccountDetailsMapper.toDto(existingClient));
            response.setContractDetails(ContractMapper.toDto(contract));
            response.setInstallmentDetails(
                    installments.stream().map(InstalmentMapper::toDto).collect(Collectors.toList()));

            response.setMessage("Client already exists, processed contract and installments");

            return response;
        } catch (Exception e) {
            logger.error("Error handling existing client: {}", e.getMessage(), e);
            response.setMessage("Error handling existing client: " + e.getMessage());
            response.getStatus().setClientStatus(FAILED_STATUS);
            throw new ClientAPIException("Failed to process existing client", e);
        }
    }

    private void handleExistingContractInstallments(ClientAccountDetails client, Contract contract,
            ClientAccountDetailsDTO dto) {
        try {
            List<Instalment> existingInstallments = instalmentRepository.findByClientAccountAndQuoteNo(client,
                    dto.getQuoteNo());

            List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository
                    .findByQuoteNoAndCompanyIdOrderByInstalmentAsc(dto.getQuoteNo(), dto.getCompanyId());

            if (existingInstallments.size() < emiDetails.size()) {
                createMissingInstallments(client, contract, existingInstallments, emiDetails);

                List<Instalment> allInstallments = instalmentRepository
                        .findByContractOrderByNoOfInstalmentAsc(contract);
                createRemainingInstallmentsViaAPI(client, contract, allInstallments);
            }
        } catch (Exception e) {
            logger.error("Error handling existing contract installments: {}", e.getMessage(), e);
            throw new ClientAPIException("Failed to process existing contract installments", e);
        }
    }

    private void createMissingInstallments(ClientAccountDetails client, Contract contract,
            List<Instalment> existingInstallments, List<EmiTransactionDetails> emiDetails) {
        Set<Long> existingInstallmentNumbers = existingInstallments.stream().map(Instalment::getNoOfInstalment)
                .collect(Collectors.toSet());

        List<Instalment> instalmentsToSave = new ArrayList<>();

        for (EmiTransactionDetails emiDetail : emiDetails) {
            Long installmentNumber = Long.parseLong(emiDetail.getInstalment());
            if (!existingInstallmentNumbers.contains(installmentNumber)) {
                Instalment instalment = createInstalmentFromEmiDetail(client, contract, emiDetail.getQuoteNo(),
                        emiDetail);
                instalmentsToSave.add(instalment);
            }
        }

        if (!instalmentsToSave.isEmpty()) {
            instalmentRepository.saveAll(instalmentsToSave);
            logger.info("Created {} missing instalments for contract: {}", instalmentsToSave.size(),
                    contract.getContractNumber());
        }
    }

    private ClientAccountDetails createAndSaveClientEntity(ClientAccountDetailsDTO dto) {
        ClientAccountDetails entity = ClientAccountDetailsMapper.toEntity(dto);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setStatus(PENDING_STATUS);
        entity.setMessage("Client entity created, pending API processing");
        return repository.save(entity);
    }

    private Contract createContract(ClientAccountDetails client, ClientAccountDetailsDTO dto) {
        Contract contract = new Contract();
        contract.setClientAccount(client);
        contract.setClientNumber(client.getClientNumber());
        contract.setContractNumber("C" + client.getId() + "-" + System.currentTimeMillis());
        contract.setQuoteNo(dto.getQuoteNo());
        contract.setPaymentId(dto.getPaymentId()); // ← NEW
        contract.setTrackingCode(dto.getTrackingCode());
        contract.setDebitSequenceType(dto.getDebitSequenceType());

        List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository
                .findByQuoteNoAndCompanyIdOrderByInstalmentAsc(dto.getQuoteNo(), dto.getCompanyId());

        if (!emiDetails.isEmpty()) {
            EmiTransactionDetails firstEmi = emiDetails.get(0);

            String frequencyCode = firstEmi.getFrequencyCode();
            Integer collectionDay = firstEmi.getCollectionDay();

            contract.setFrequencyCode(frequencyCode);
            contract.setCollectionDay(collectionDay);

            logger.info(
                    "Contract using frequency details FROM EMI TABLE - QuoteNo: {}, FrequencyCode: {}, CollectionDay: {}",
                    dto.getQuoteNo(), frequencyCode, collectionDay);
        } else {
            contract.setFrequencyCode("MNTH");
            contract.setCollectionDay(1);
            logger.warn("No EMI details found for quote: {}, using default frequency and collection day",
                    dto.getQuoteNo());
        }

        contract.setStatus(PENDING_STATUS);
        contract.setMessage("Contract entity created, pending API processing");
        contract.setCreatedDate(LocalDateTime.now());
        contract.setUpdatedDate(LocalDateTime.now());

        return contractRepository.save(contract);
    }

    private Instalment createInstalmentFromEmiDetail(ClientAccountDetails client, Contract contract, String quoteNo,
            EmiTransactionDetails emiDetail) {
        Instalment instalment = new Instalment();
        instalment.setClientAccount(client);
        instalment.setContract(contract);
        instalment.setQuoteNo(quoteNo);
        instalment.setPaymentId(contract.getPaymentId());
        instalment.setProductId(emiDetail.getProductId());
        instalment.setNoOfInstalment(Long.parseLong(emiDetail.getInstalment()));
        instalment.setInstalmentActionDate(emiDetail.getDueDate());

        BigDecimal installmentAmount = BigDecimal.valueOf(emiDetail.getDueAmount());
        instalment.setInstalmentAmount(installmentAmount);

        instalment.setClientNumber(client.getClientNumber());
        instalment.setContractNumber(contract.getContractNumber());
        instalment.setContractSequence(null);
        instalment.setTrackingCode(client.getTrackingCode());
        instalment.setDebitSequenceType(client.getDebitSequenceType());
        instalment.setCtcAmount(BigDecimal.ZERO);
        instalment.setStatus(PENDING_STATUS);
        instalment.setMessage("Installment entity created, pending API processing");
        instalment.setCreatedDate(LocalDateTime.now());
        instalment.setUpdatedDate(LocalDateTime.now());

        return instalment;
    }

    private void updateContractWithFirstInstallment(Contract contract) {
        List<Instalment> installments = instalmentRepository.findByContractOrderByNoOfInstalmentAsc(contract);
        if (installments.isEmpty()) {
            logger.warn("No installments found for contract: {}", contract.getContractNumber());
            return;
        }

        Instalment firstInstallment = installments.get(0);
        contract.setFirstCollectionDate(firstInstallment.getInstalmentActionDate());
        contract.setFirstCollectionAmount(firstInstallment.getInstalmentAmount());
        contract.setInstalmentStartDate(firstInstallment.getInstalmentActionDate());
        contract.setInstalmentAmount(firstInstallment.getInstalmentAmount());
        contract.setNumberOfInstalments(installments.size());

        contractRepository.save(contract);
    }

    private Map<String, Object> createClientRequest(ClientAccountDetails client) {
        Map<String, Object> request = new HashMap<>();
        request.put("ClientNumber", client.getClientNumber());
        request.put("ClientName", client.getClientName());
        request.put("IDType", client.getIdType());
        request.put("IDNumber", client.getIdNumber());
        request.put("BankCode", client.getBankCode());
        request.put("BranchCode", client.getBranchCode());
        request.put("AccountType", client.getAccountType());
        request.put("AccountNumber", client.getAccountNumber());
        request.put("AccountHolderName", client.getAccountHolderName());
        request.put("EmployeeGroupCode", client.getEmployeeGroupCode());

        addOptionalField(request, "PopInd", client.getPopInd());
        addOptionalField(request, "POBankCode", client.getPoBankCode());
        addOptionalField(request, "POBranchCode", client.getPoBranchCode());
        addOptionalField(request, "POAccountHolderName", client.getPoAccountHolderName());
        addOptionalField(request, "POAccountNumber", client.getPoAccountNumber());
        addOptionalField(request, "POAccountType", client.getPoAccountType());
        addOptionalField(request, "CardNumber", client.getCardNumber());
        addOptionalField(request, "CardExpiry", client.getCardExpiry());

        return request;
    }

    private void addOptionalField(Map<String, Object> request, String fieldName, Object value) {
        if (value != null) {
            request.put(fieldName, value);
        }
    }

    private Map<String, Object> createContractAPIRequest(Contract contract) {
        Map<String, Object> request = new HashMap<>();

        try {
            request.put("ClientNumber", contract.getClientNumber());
            request.put("ContractNumber", contract.getContractNumber());
            request.put("FrequencyCode", contract.getFrequencyCode());
            request.put("CollectionDay", contract.getCollectionDay());
            request.put("TrackingCode", contract.getTrackingCode());

            if (contract.getInstalmentStartDate() != null) {
                String formattedDate = formatDateForOracleNumeric(contract.getInstalmentStartDate());
                request.put("InstalmentStartDate", formattedDate);
                logger.debug("Contract API - InstalmentStartDate formatted as numeric: {}", formattedDate);
            }

            request.put("InstalmentAmount", contract.getInstalmentAmount().doubleValue());
            request.put("NumberOfInstalments", 1);
            request.put("CTCPercentage", contract.getCtcPercentage() != null ? contract.getCtcPercentage() : 1);

            logger.debug("Contract API request created successfully: {}", request);
            return request;

        } catch (Exception e) {
            logger.error("Error creating contract API request: {}", e.getMessage(), e);
            throw new ClientAPIException("Failed to create contract API request", e);
        }
    }

    private Map<String, Object> createInstallmentAPIRequest(Instalment instalment) {
        Map<String, Object> request = new HashMap<>();

        try {
            request.put("ClientNumber", instalment.getClientNumber());
            request.put("ContractNumber", instalment.getContractNumber());

            if (instalment.getContract().getContractSequence() != null) {
                request.put("ContractSequence", instalment.getContract().getContractSequence());
            }
            request.put("InstalmentSequence", instalment.getNoOfInstalment());

            if (instalment.getInstalmentActionDate() != null) {
                String formattedDate = formatDateForOracleNumeric(instalment.getInstalmentActionDate());
                request.put("InstalmentActionDate", formattedDate);
                logger.debug("Installment API - InstalmentActionDate formatted as numeric: {}", formattedDate);
            }

            request.put("InstalmentAmount", instalment.getInstalmentAmount().doubleValue());
            request.put("TrackingCode", instalment.getTrackingCode());
            request.put("DebitSequenceType", instalment.getDebitSequenceType());
            request.put("CTCAmount", instalment.getCtcAmount().doubleValue());

            logger.debug("Installment API request created successfully: {}", request);
            return request;

        } catch (Exception e) {
            logger.error("Error creating installment API request: {}", e.getMessage(), e);
            throw new ClientAPIException("Failed to create installment API request", e);
        }
    }

    private JsonNode callClientAPI(String provider, List<Map<String, Object>> requestList, String apiType) {
        try {
            return clientService.callClient(provider, objectMapper.valueToTree(requestList), "post", apiType,"21478");
        } catch (Exception e) {
            logger.error("{} API call failed: {}", apiType, e.getMessage(), e);
            throw new ClientAPIException("Failed to call " + apiType + " API", e);
        }
    }

    private void updateExistingClientWithQuoteInfo(ClientAccountDetails existingClient, ClientAccountDetailsDTO dto) {
        if (StringUtils.hasText(dto.getQuoteNo()) && !dto.getQuoteNo().equals(existingClient.getQuoteNo())) {
            existingClient.setQuoteNo(dto.getQuoteNo());
        }
        if (StringUtils.hasText(dto.getMerchantReference())
                && !dto.getMerchantReference().equals(existingClient.getMerchantReference())) {
            existingClient.setMerchantReference(dto.getMerchantReference());
        }
        if (StringUtils.hasText(dto.getPaymentType())
                && !dto.getPaymentType().equals(existingClient.getPaymentType())) {
            existingClient.setPaymentType(dto.getPaymentType());
        }
        if (StringUtils.hasText(dto.getPaymentTypeDesc())
                && !dto.getPaymentTypeDesc().equals(existingClient.getPaymentTypeDesc())) {
            existingClient.setPaymentTypeDesc(dto.getPaymentTypeDesc());
        }
        if (StringUtils.hasText(dto.getCompanyId()) && !dto.getCompanyId().equals(existingClient.getCompanyId())) {
            existingClient.setCompanyId(dto.getCompanyId());
        }
        if (StringUtils.hasText(dto.getClientName()) && !dto.getClientName().equals(existingClient.getClientName())) {
            existingClient.setClientName(dto.getClientName());
        }
        if (StringUtils.hasText(dto.getIdType()) && !dto.getIdType().equals(existingClient.getIdType())) {
            existingClient.setIdType(dto.getIdType());
        }
        if (StringUtils.hasText(dto.getIdNumber()) && !dto.getIdNumber().equals(existingClient.getIdNumber())) {
            existingClient.setIdNumber(dto.getIdNumber());
        }
        if (StringUtils.hasText(dto.getCellphoneNumber())
                && !dto.getCellphoneNumber().equals(existingClient.getCellphoneNumber())) {
            existingClient.setCellphoneNumber(dto.getCellphoneNumber());
        }
        if (StringUtils.hasText(dto.getEmail()) && !dto.getEmail().equals(existingClient.getEmail())) {
            existingClient.setEmail(dto.getEmail());
        }
        if (dto.getBankCode() != null && !dto.getBankCode().equals(existingClient.getBankCode())) {
            existingClient.setBankCode(dto.getBankCode());
        }
        if (dto.getBranchCode() != null && !dto.getBranchCode().equals(existingClient.getBranchCode())) {
            existingClient.setBranchCode(dto.getBranchCode());
        }
        if (dto.getAccountType() != null && !dto.getAccountType().equals(existingClient.getAccountType())) {
            existingClient.setAccountType(dto.getAccountType());
        }
        if (dto.getAccountNumber() != null && !dto.getAccountNumber().equals(existingClient.getAccountNumber())) {
            existingClient.setAccountNumber(dto.getAccountNumber());
        }
        if (StringUtils.hasText(dto.getAccountHolderName())
                && !dto.getAccountHolderName().equals(existingClient.getAccountHolderName())) {
            existingClient.setAccountHolderName(dto.getAccountHolderName());
        }
        if (StringUtils.hasText(dto.getEmployeeGroupCode())
                && !dto.getEmployeeGroupCode().equals(existingClient.getEmployeeGroupCode())) {
            existingClient.setEmployeeGroupCode(dto.getEmployeeGroupCode());
        }
        if (StringUtils.hasText(dto.getPopInd()) && !dto.getPopInd().equals(existingClient.getPopInd())) {
            existingClient.setPopInd(dto.getPopInd());
        }
        if (dto.getPoBankCode() != null && !dto.getPoBankCode().equals(existingClient.getPoBankCode())) {
            existingClient.setPoBankCode(dto.getPoBankCode());
        }
        if (dto.getPoBranchCode() != null && !dto.getPoBranchCode().equals(existingClient.getPoBranchCode())) {
            existingClient.setPoBranchCode(dto.getPoBranchCode());
        }
        if (StringUtils.hasText(dto.getPoAccountHolderName())
                && !dto.getPoAccountHolderName().equals(existingClient.getPoAccountHolderName())) {
            existingClient.setPoAccountHolderName(dto.getPoAccountHolderName());
        }
        if (dto.getPoAccountNumber() != null && !dto.getPoAccountNumber().equals(existingClient.getPoAccountNumber())) {
            existingClient.setPoAccountNumber(dto.getPoAccountNumber());
        }
        if (dto.getPoAccountType() != null && !dto.getPoAccountType().equals(existingClient.getPoAccountType())) {
            existingClient.setPoAccountType(dto.getPoAccountType());
        }
        if (StringUtils.hasText(dto.getCardNumber()) && !dto.getCardNumber().equals(existingClient.getCardNumber())) {
            existingClient.setCardNumber(dto.getCardNumber());
        }
        if (dto.getCardExpiry() != null && !dto.getCardExpiry().equals(existingClient.getCardExpiry())) {
            existingClient.setCardExpiry(dto.getCardExpiry());
        }
        if (StringUtils.hasText(dto.getTrackingCode())
                && !dto.getTrackingCode().equals(existingClient.getTrackingCode())) {
            existingClient.setTrackingCode(dto.getTrackingCode());
        }
        if (StringUtils.hasText(dto.getDebitSequenceType())
                && !dto.getDebitSequenceType().equals(existingClient.getDebitSequenceType())) {
            existingClient.setDebitSequenceType(dto.getDebitSequenceType());
        }
        if (StringUtils.hasText(dto.getFrequencyCode())
                && !dto.getFrequencyCode().equals(existingClient.getFrequencyCode())) {
            existingClient.setFrequencyCode(dto.getFrequencyCode());
        }

        existingClient.setUpdatedDate(LocalDateTime.now());
        repository.save(existingClient);
    }

    private void setResponseBodySafely(String responseBody, Consumer<String> setter) {
        if (responseBody == null) {
            setter.accept(null);
            return;
        }

        if (responseBody.length() > MAX_RESPONSE_BODY_SIZE) {
            logger.warn("RESPONSE_BODY_TOO_LARGE - Truncating from {} to {} characters", responseBody.length(),
                    MAX_RESPONSE_BODY_SIZE);
            setter.accept(responseBody.substring(0, MAX_RESPONSE_BODY_SIZE));
        } else {
            setter.accept(responseBody);
        }
    }

    private String formatDateForOracleNumeric(Date date) {
        if (date == null)
            return null;

        try {
            SimpleDateFormat numericFormat = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = numericFormat.format(date);
            logger.debug("Oracle Numeric Date - Original: {}, Formatted: {}", date, formattedDate);
            return formattedDate;

        } catch (Exception e) {
            logger.error("Error formatting date for Oracle API (numeric format): {}", e.getMessage());

            try {
                SimpleDateFormat fallbackFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fallbackDate = fallbackFormat.format(date);
                logger.debug("Oracle API Date - Fallback format: {}", fallbackDate);
                return fallbackDate;

            } catch (Exception e2) {
                logger.error("Error formatting date for Oracle API (fallback format): {}", e2.getMessage());
                return null;
            }
        }
    }

    private boolean validateEmiDetail(EmiTransactionDetails emiDetail) {
        if (emiDetail == null)
            return false;
        if (!StringUtils.hasText(emiDetail.getInstalment()))
            return false;
        if (emiDetail.getDueDate() == null)
            return false;
        if (emiDetail.getDueAmount() == null || emiDetail.getDueAmount() <= 0)
            return false;
        return true;
    }

    private void validateClientDTO(ClientAccountDetailsDTO dto) {
        List<String> errors = new ArrayList<>();

        if (!StringUtils.hasText(dto.getClientNumber())) {
            errors.add("Client number is required");
        }
        if (!StringUtils.hasText(dto.getClientName())) {
            errors.add("Client name is required");
        }
        if (!StringUtils.hasText(dto.getIdType())) {
            errors.add("ID type is required");
        }
        if (!StringUtils.hasText(dto.getIdNumber())) {
            errors.add("ID number is required");
        }
        if (dto.getBankCode() == null) {
            errors.add("Bank code is required");
        }
        if (dto.getAccountNumber() == null) {
            errors.add("Account number is required");
        }
        if (!StringUtils.hasText(dto.getQuoteNo())) {
            errors.add("Quote number is required");
        }
        if (!StringUtils.hasText(dto.getCompanyId())) {
            errors.add("Company ID is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed: " + String.join(", ", errors));
        }
    }

    private void handleValidationException(UnifiedClientCreationResponseDTO response,
            UnifiedClientCreationResponseDTO.ClientCreationStatus status, ValidationException e) {
        logger.error("Validation error: {}", e.getMessage());
        response.setMessage("Validation error: " + e.getMessage());
        status.setClientStatus(FAILED_STATUS);
        throw e;
    }

    private void handleGenericException(UnifiedClientCreationResponseDTO response,
            UnifiedClientCreationResponseDTO.ClientCreationStatus status, Exception e, ClientAccountDetails entity) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        response.setMessage("Unexpected error: " + e.getMessage());
        status.setClientStatus(FAILED_STATUS);

        if (entity != null) {
            response.setClientDetails(ClientAccountDetailsMapper.toDto(entity));
        }

        throw new ClientAPIException("Failed to create client", e);
    }

    @Transactional
    public ClientAccountDetailsDTO createClient(ClientAccountDetailsDTO dto) {
        UnifiedClientCreationResponseDTO unifiedResponse = createClientWithFullResponse(dto);
        return unifiedResponse.getClientDetails();
    }

    public ClientAccountDetailsDTO getClientById(Long id) {
        Optional<ClientAccountDetails> clientOpt = repository.findById(id);
        return clientOpt.map(ClientAccountDetailsMapper::toDto).orElse(null);
    }

    public ClientAccountDetailsDTO getClientByClientNumber(String clientNumber) {
        List<ClientAccountDetails> clients = repository.findByClientNumber(clientNumber);
        if (clients.isEmpty()) {
            return null;
        }
        return ClientAccountDetailsMapper.toDto(clients.get(0));
    }

    public List<ClientAccountDetailsDTO> getClientsByStatus(String status) {
        List<ClientAccountDetails> clients = repository.findByStatus(status);
        return clients.stream().map(ClientAccountDetailsMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ClientAccountDetailsDTO updateClient(Long id, ClientAccountDetailsDTO dto) {
        return repository.findById(id).map(existing -> {
            ClientAccountDetails updated = ClientAccountDetailsMapper.toEntity(dto);
            updated.setId(id);
            updated.setCreatedDate(existing.getCreatedDate());
            updated.setUpdatedDate(LocalDateTime.now());
            updated.setBeneficiaryUser(dto.getBeneficiaryUser());

            ClientAccountDetails savedDetails = repository.save(updated);
            return ClientAccountDetailsMapper.toDto(savedDetails);
        }).orElseThrow(() -> new ClientAPIException("Client not found: " + id, null));
    }

    @Transactional
    public void deleteClient(Long id) {
        Optional<ClientAccountDetails> clientOpt = repository.findById(id);
        if (clientOpt.isPresent()) {
            ClientAccountDetails client = clientOpt.get();

            List<Contract> contracts = contractRepository.findByClientAccount(client);
            for (Contract contract : contracts) {
                instalmentRepository.deleteByContract(contract);
            }
            contractRepository.deleteByClientAccount(client);

            repository.delete(client);
        } else {
            throw new ClientAPIException("Client not found: " + id, null);
        }
    }

    private String getInscompanyMasterDropdown(String companyId) {
        return "Company Name";
    }

    private String getCompanyBranchMasterDropdown(String companyId, String branchCode) {
        return "Branch Name";
    }

    private String getListItem(String companyId, String branchCode, String itemType, String itemCode) {
        return "Payment Mode Description";
    }

    private String getBankName(Integer bankCode) {
        if (bankCode == null)
            return null;
        return "Bank Name";
    }

    private void setValidityDate(PaymentDetail paymentDetail, HomePositionMaster homeData) {
        // paymentDetail.setValidityFrom(homeData.getPolicyStartDate());
        // paymentDetail.setValidityTo(homeData.getPolicyEndDate());
    }

    private String getTinyUrl(String quoteNo) {
        return "https://short.url/" + quoteNo;
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static class ClientAPIException extends RuntimeException {
        public ClientAPIException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public EndorsementCheckDto checkAndReturnOriginalQuoteNo(String quoteNo, String companyId){

        HomePositionMaster homePositionMasterCurr = homeRepo.findByQuoteNo(quoteNo);
        String originalQuote = quoteNo;
        Boolean endoresement = false;

//        if(!homePositionMasterCurr.getOriginalPolicyNo().isEmpty()){
//            HomePositionMaster homePositionMaster = homeRepo.findByPolicyNo(homePositionMasterCurr.getOriginalPolicyNo());
//            if(homePositionMaster!=null && homePositionMaster.getQuoteNo() != null){
//                endoresement = homePositionMasterCurr.getEndtPrevQuoteNo()!=null;
//                originalQuote = homePositionMaster.getQuoteNo();
//            }
//        }
        if(homePositionMasterCurr.getEndtPrevQuoteNo()!= null &&
                !homePositionMasterCurr.getEndtPrevQuoteNo().isEmpty()){
            endoresement = true;
            originalQuote = homePositionMasterCurr.getEndtPrevQuoteNo();
        }
        return new EndorsementCheckDto().constructEndorsementCheck(endoresement,originalQuote);
    }

    public UnifiedClientCreationResponseDTO handleEndorsement(Optional<ClientAccountDetails> existingExact, ClientAccountDetailsDTO dto,
                                                              UnifiedClientCreationResponseDTO response){
        if(existingExact.isPresent()) {
            ClientAccountDetails clientAccountDetails = existingExact.get();

            HomePositionMaster homePositionMaster =  new HomePositionMaster();
            homePositionMaster =  homeRepo.findByQuoteNo(dto.getQuoteNo());

            /// For updating in Client Account Details ...
            int updatedCountClientRecords = repository.updateClientAccountDetails(clientAccountDetails.getClientNumber(),
                    clientAccountDetails.getQuoteNo(),dto.getQuoteNo(),clientAccountDetails.getQuoteNo(),
                    homePositionMaster.getEndorsementRemarks(),homePositionMaster.getEndtPrevPolicyNo());
            logger.info("Updated Client Account Info for Endorsement count {}, for quote {}",updatedCountClientRecords,dto.getQuoteNo());

            /// For updating in Contracts
            int updatedContractRecords = contractRepository.updateContractsByQuoteNo(
                    clientAccountDetails.getQuoteNo(),dto.getQuoteNo(),clientAccountDetails.getQuoteNo(),
                    homePositionMaster.getEndorsementRemarks(),homePositionMaster.getEndtPrevPolicyNo());
            logger.info("Updated Contract Info for Endorsement count {}, for quote {}",updatedContractRecords,dto.getQuoteNo());

            /// For updating in installment collection
            List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository
                    .findByQuoteNoAndCompanyIdOrderByInstalmentAsc(dto.getQuoteNo(), dto.getCompanyId());
            HomePositionMaster finalHomePositionMaster = homePositionMaster;
            emiDetails.stream().forEach(eachEmi->{
                int updatedInstallmentRecords =  instalmentRepository.updateInstalmentForEndorsement(
                        clientAccountDetails.getQuoteNo(),dto.getQuoteNo(),clientAccountDetails.getQuoteNo(),
                        finalHomePositionMaster.getEndorsementRemarks(), finalHomePositionMaster.getEndtPrevPolicyNo(),
                        new BigDecimal(eachEmi.getDueAmount()), Long.valueOf(eachEmi.getInstalment())
                );
            });
            logger.info("Updated Installments for Endorsement for quote {}",dto.getQuoteNo());

            /// for making API calls to RealPay
            List<Instalment> pendingInstalments = instalmentRepository.findByQuoteNoAndInstalmentStatus(dto.getQuoteNo(),"A");
            logger.info("Pending instalments {}",pendingInstalments);
            List<InstalmentPutRequestItemDTO> instalmentPutRequestItemDTOS = new ArrayList<>();
            pendingInstalments.stream().forEach(eachPendingInstallment->{
                InstalmentPutRequestItemDTO instalmentPutRequestItemDTO = new InstalmentPutRequestItemDTO();
                instalmentPutRequestItemDTO =  instalmentPutRequestItemDTO.constructInstallmentUpdateReq(
                        eachPendingInstallment.getClientNumber(),Long.valueOf(eachPendingInstallment.getContractSequence()),
                        eachPendingInstallment.getContractNumber(),eachPendingInstallment.getArrearMonth()!= null ? eachPendingInstallment.getArrearMonth() : eachPendingInstallment.getNoOfInstalment(),
                        String.valueOf(eachPendingInstallment.getInstalmentActionDate()),eachPendingInstallment.getTrackingCode(),eachPendingInstallment.getInstalmentAmount(),
                        eachPendingInstallment.getInstalmentStatus(),eachPendingInstallment.getDebitSequenceType()
                );
                logger.info("adding record {}",instalmentPutRequestItemDTO);
                instalmentPutRequestItemDTOS.add(instalmentPutRequestItemDTO);
            });

            String paymentType = (dto.getPaymentType()!=null && dto.getPaymentType().equals("8") ) ? "EFT" : "RealPay";
            /// updating in home position master table ...
            homePositionMaster.setPaymentType(paymentType);
            homePositionMaster.setPaymentStatus("ACCEPTED");
            homePositionMaster.setPaymentMode("Real Pay");
            homeRepo.save(homePositionMaster);

            String product = getProviderByCompanyId(clientAccountDetails.getCompanyId());
            String merchantId = clientService.getMerchantByProduct(product);
            logger.info("Updating Body for Real Pay {}",instalmentPutRequestItemDTOS);
            JsonNode jsonNode = clientService.maintainInstalmentsExternal(product,merchantId,instalmentPutRequestItemDTOS);
            logger.info("RealPay API Call Status {}",jsonNode);
            response.setMessage("Client, contract, and installments updated successfully");
        }else{
            logger.info("No clinet found for {}, for client {}",dto.getQuoteNo(),dto.getClientNumber() );
            response.setMessage("Client Not Found");
        }
        return response;
    }
    public ResponseEntity<?> fetchClientInfoForEndorsement(String newQuoteId){
        CommonRes commonRes = new CommonRes();
        try{
            HomePositionMaster homePositionMaster = homeRepo.findByQuoteNo(newQuoteId);
            EndorsementCheckDto endorsementCheckDto = checkAndReturnOriginalQuoteNo(newQuoteId, homePositionMaster.getCompanyId());
            Optional<ClientAccountDetails> clientAccountDetails = repository.findByQuoteNo(endorsementCheckDto.getOriginalQuoteNo());
            if(clientAccountDetails.isPresent()&&clientAccountDetails!=null){
                ClientAccountDetails clientAccountDetails1 = clientAccountDetails.get();
                clientAccountDetails1.setContracts(null);
                clientAccountDetails1.setInstalments(null);
                commonRes.setCommonResponse(clientAccountDetails1);
                commonRes.setMessage("Client Account Info Fetch Successfully !");
                commonRes.setErroCode(200);
            }else{
                commonRes.setCommonResponse("No client info available for quoteNo "+newQuoteId);
                commonRes.setMessage("No client info available for quoteNo "+newQuoteId);
                commonRes.setErroCode(204);
            }
            return new ResponseEntity<>(commonRes, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            commonRes.setCommonResponse("Error in fetching client account info error :"+e.getMessage());
            commonRes.setMessage("Error in fetching client account info ");
            commonRes.setErroCode(409);
            return new ResponseEntity<>(commonRes, HttpStatus.CONFLICT);
        }
    }

    public String updateFirstInstalmentPaid(ClientAccountDetailsDTO clientAccountDetailsDTO){
        try{
            String quoteNo = clientAccountDetailsDTO.getQuoteNo();
            logger.info("Updating the first instalment for {}",quoteNo);
            Contract contract = contractRepository.findOriginalContract(quoteNo);
            if(contract!=null){
                Instalment instalment = instalmentRepository.findByQuoteNoAndNoOfInstalmentAndContractNumber(quoteNo,1L, contract.getContractNumber());
                ObjectNode node = InstalmentJsonBuilder.buildInstalmentNode(instalment);

                /// calling to update in the all table saying amount is received ...
                InstalmentStatusService.StatusProcessingResult statusProcessingResult = instalmentStatusService.processSuccessfulStatus(node, "00",
                        instalmentStatusService.getAuthToken(),clientAccountDetailsDTO.getIsFirstInstalmentPaid());
                logger.info("Updated in process Successful instalment Flow ... Res {}",statusProcessingResult);

                if(clientAccountDetailsDTO.getIsFirstInstalmentPaid()!= null
                        && clientAccountDetailsDTO.getIsFirstInstalmentPaid().equals("YES")){

                    /// to update in instalment table to capture the first instalment payment info...
                    updateInstalmentProcessedInfo(instalment,clientAccountDetailsDTO);

                    /// to cancel the first instalment in Real Pay as amount received via cash ...
                    cancelInstalmentInRealPay(instalment);
                    instalment.setInstalmentStatus("CASH");
                    instalmentRepository.save(instalment);

                    /// to call premia push integration and receipt generation...
                  premiaPushIntegration.callPremiaPushIntegration(instalment.getInstalmentReferenceNumber(),false);

                }else{
                    changeStatusToPending(clientAccountDetailsDTO.getQuoteNo(),"1"
                            ,clientAccountDetailsDTO.getCompanyId(),instalment.getProductId(),clientAccountDetailsDTO.getPaymentId(),instalment.getInstalmentReferenceNumber());
                }
                return "YES";
            }
            return "NO";
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Error in updating first instalment for {}, error is {}",clientAccountDetailsDTO.getQuoteNo(),e.getMessage());
            return "NO";
        }
    }

    /// to change the payment status because payment is not done...
    private void changeStatusToPending(String quoteNo, String instalmentNo, String companyId, String productId, String paymentId, String instalmentReferenceNumber){
        try{
            Optional<List<PaymentDetail>> paymentDetailOpt = paymentDetailRepo
                    .findLatestByQuoteNoAndPaymentId(quoteNo, paymentId);

            HomePositionMaster homePositionMaster = homeRepo.findByQuoteNo(quoteNo);

            if (paymentDetailOpt.isPresent()) {

                if (homePositionMaster.getPolicyNo() == null || homePositionMaster.getPolicyNo().isEmpty()) {
                    logger.info("Generating policy - Not processed by webhook: {}", instalmentReferenceNumber);
                    logger.info("Going for calling generate Policy Again...");
                    instalmentStatusService.generatePolicyForPaymentWithNewTransaction(paymentDetailOpt.get().get(0), instalmentStatusService.getAuthToken());
                }else{
                    logger.warn("Policy number is not null so skipping policy generation {}",homePositionMaster.getPolicyNo());
                }

            } else {
                logger.warn("PaymentDetail not found for policy generation - Quote: {}, PaymentId: {}", quoteNo,
                        paymentId);

            }

            logger.info("Changing payment staus to pending in all tables because payment not done for {}...",quoteNo);

            EmiTransactionDetails emiTransactionDetails = emiTransactionDetailsRepository.findByQuoteNoAndInstalmentAndCompanyIdAndProductId(
                    quoteNo,instalmentNo,companyId,productId);
            if (emiTransactionDetails != null) {
                emiTransactionDetails.setPaymentDate(null);
                emiTransactionDetails.setPaymentDetails(null);
                emiTransactionDetails.setPaymentStatus("Pending");
                emiTransactionDetailsRepository.save(emiTransactionDetails);
                logger.info("Reverted status in the EMI Transaction Detail Table for {} instalment {}",quoteNo,instalmentNo);
            }else{
                logger.warn("No EMI Transaction record is available for {}, instalment {}",quoteNo,instalmentNo);
            }

            Instalment instalment = instalmentRepository.findByInstalmentReferenceNumber(emiTransactionDetails.getInstalmentReferenceNumber());
            if(instalment!=null){
                instalment.setInstalmentStatus("A");
                instalmentRepository.save(instalment);
                logger.info("Reverted status in the Instalment Table for {} instalment {}",quoteNo,instalmentNo);
            }else{
                logger.warn("No Instalment Record found for reference number {}",emiTransactionDetails.getInstalmentReferenceNumber());
            }

            PaymentInfo paymentInfo = paymentInfoRepo.findByQuoteNoAndPaymentId(quoteNo,emiTransactionDetails.getPaymentId());
            if(paymentInfo!=null){
                paymentInfo.setPaymentStatus("PENDING");
                paymentInfoRepo.save(paymentInfo);
                logger.info("Reverted status in the PaymentInfo Table for {} instalment {}",quoteNo,instalmentNo);
            }else{
                logger.warn("No Payment Info Record found for reference number {}",emiTransactionDetails.getInstalmentReferenceNumber());
            }

            PaymentDetail paymentDetail = paymentDetailRepo.findByPaymentId(emiTransactionDetails.getPaymentId());
            if(paymentDetail!=null){
                paymentDetail.setPaymentStatus("PENDING");
                paymentDetailRepo.save(paymentDetail);
                logger.info("Reverted status in the Payment Detail Table for {} instalment {}",quoteNo,instalmentNo);
            }else{
                logger.warn("No Payment Detail Record found for reference number {}",emiTransactionDetails.getInstalmentReferenceNumber());
            }

            /// push integration API ...
            premiaPushIntegration.callPremiaPushIntegration(emiTransactionDetails.getInstalmentReferenceNumber(),false);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateInstalmentProcessedInfo(Instalment instalment,ClientAccountDetailsDTO dto){
        instalment.setIsPaidCash(dto.getIsFirstInstalmentPaid());
        instalment.setPayeeName(dto.getPayeeName());
        instalment.setBankName(dto.getBankName());
        instalment.setMicrNumber(dto.getMicrNumber());
        instalment.setReferenceNumber(dto.getReferenceNumber());
        instalment.setPaidDate(dto.getPaidDate());
        instalmentRepository.save(instalment);
    }

    public void cancelInstalmentInRealPay(Instalment instalment){
        try{
            logger.info("Going to cancel instalment in Real Pay for received in cash for {} - {}",instalment.getQuoteNo(),instalment.getNoOfInstalment());
            String contractSequence = instalmentStatusService.getContractSequenceFromInstallment(instalment);
            String instalmentSequence = instalmentStatusService.getInstalmentSequenceFromInstallment(instalment);

            JsonNode apiResponse = clientService.cancelInstalment(
                    instalmentStatusService.determineProductFromContract(instalment.getQuoteNo()), instalment.getClientNumber(),
                    instalment.getContractNumber(), contractSequence, instalmentSequence,true);

            if (instalmentStatusService.isRealPayCancellationSuccessful(apiResponse)) {
                instalment.setInstalmentStatus("CASH");
                instalment.setSyncStatus("S");
                instalment.setMessage("Cancelled in RealPay: " + "First Instalment Received via Cash");
                logger.info("SUCCESS: RealPay cancellation for: {}", instalment.getInstalmentReferenceNumber());
            } else {
                instalment.setSyncStatus("F");
                instalment.setMessage("RealPay cancellation failed: " + instalmentStatusService.extractFailureReason(apiResponse));
                instalment.setInstalmentStatus(InstalmentStatusService.Status.FAILED);
                logger.error("FAILED: RealPay cancellation for: {} - Reason: {}",
                        instalment.getInstalmentReferenceNumber(), instalmentStatusService.extractFailureReason(apiResponse));
            }
            instalmentRepository.save(instalment);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}