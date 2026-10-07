package com.maan.eway.realpay.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maan.eway.bean.*;
import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.integration.controller.IntegrationController;
import com.maan.eway.master.req.EmiTransactionDetailsUpdateReq;
import com.maan.eway.master.service.EmiTransactionDetailsService;
import com.maan.eway.realpay.dto.InstalmentJsonBuilder;
import com.maan.eway.realpay.util.TokenGenerator;
import com.maan.eway.reinsurance.req.ReInsuranceQuoteReq;
import com.maan.eway.reinsurance.service.ReinsuranceService;
import com.maan.eway.repository.*;
import com.maan.eway.res.SuccessRes;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Contract;
import com.maan.eway.realpay.model.Instalment;
import com.maan.eway.realpay.repository.ClientAccountDetailsRepository;
import com.maan.eway.realpay.repository.ContractRepository;
import com.maan.eway.realpay.repository.InstalmentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.Data;

@Service
@Transactional
public class InstalmentStatusService {

	private static final Logger logger = LoggerFactory.getLogger(InstalmentStatusService.class);

	static final class Status {
		static final String FUTURE = "A";
		static final String PROCESSING = "W";
		static final String SUCCESSFUL = "S";
		static final String FAILED = "F";
		static final String DISPUTED = "D";
		static final String ERROR = "E";
		static final String CANCELLED = "I";
		static final String Retry = "R";
		static final String HOLD = "H";
		static final String SUSPENDED = "U";
		static final String EFT_PENDING = "EFT_PENDING";
		static final String EFT_SUCCESS = "EFT_SUCCESS";
		static final String DOUBLE_DEBIT_PENDING = "DOUBLE_DEBIT_PENDING";
		static final String DOUBLE_DEBIT_SUCCESS = "DOUBLE_DEBIT_SUCCESS";
	}

	private static final class Country {
		static final String NAMIBIA = "Namibia";
		static final String ESWATINI = "Eswatini";
	}

	private static final class Product {
		static final String NAMPAY = "NAMPAY";
		static final String FNB = "FNB";
	}

	private static final class ResponseGroups {
		static final Set<String> GROUP1_INSUFFICIENT_FUNDS_NAMIBIA = Set.of("TR76", "00002", "AM04", "RR10");
		static final Set<String> GROUP2_PERMANENT_CLOSURE_NAMIBIA = Set.of("MD07", "UN26", "00012", "AC05", "AC06",
				"UN03", "UN06", "UN08", "UN10", "RR15", "RR06");
		static final Set<String> GROUP3_NEW_MANDATE_NAMIBIA = Set.of("NA04", "NA28", "NA30", "NA32", "NA34", "NA36",
				"MD05");
		static final Set<String> GROUP4_DISPUTE_NAMIBIA = Set.of("RR16");
		static final Set<String> GROUP5_TECHNICAL_ERRORS_NAMIBIA = Set.of("AC02", "AC03", "AC08", "AC12", "AC13",
				"AG08", "AM09", "AM10", "AM11", "AM12", "AM13", "AM14", "AM16", "AM19", "BE08", "BE09", "BE10", "BE11",
				"BE18", "BE22", "CH04", "CL03", "CURR", "DT01", "DT03", "DU01", "DU03", "DU04", "ED06", "FF01", "FF04",
				"FF05", "FF06", "FF08", "FF10", "RC06", "RC07", "RR02", "RR07", "RR09", "SL13", "TR25", "TR26", "TR27",
				"TR28", "TR29", "TR30", "TR31", "TR32", "TR33", "TR34", "TR35", "TR36", "TR37", "TR51", "TR52", "TR64",
				"TR66", "TR74", "TR75", "ACTC", "ACSC", "ACSP", "ACWC", "ACCP", "PDNG", "PART", "RJCT", "MD01", "MD02");
		static final Set<String> GROUP1_INSUFFICIENT_FUNDS_ESWATINI = Set.of("02");
		static final Set<String> GROUP2_PERMANENT_CLOSURE_ESWATINI = Set.of("45", "12", "26", "06", "50");
		static final Set<String> GROUP3_NEW_MANDATE_ESWATINI = Set.of();
		static final Set<String> GROUP4_DISPUTE_ESWATINI = Set.of();
		static final Set<String> GROUP5_TECHNICAL_ERRORS_ESWATINI = Set.of("39", "72", "04", "6", "34", "36", "41",
				"44", "46", "47", "48", "55", "60", "61", "95", "64", "65", "67", "71", "38", "40", "42", "43", "49",
				"54", "57", "58", "59", "62", "68", "70", "30", "31", "33", "74", "75", "76", "77");
	}

	public enum PaymentPreference {
		EFT, DOUBLE_DEBIT_ORDER_ARREAR
	}

	public enum ContractType {
		ORIGINAL, ARREAR
	}

	@Autowired
	private PaymentDetailRepository paymentDetailRepo;

	@Autowired
	private ClientAccountDetailsRepository clientAccountDetailsRepository;

	@Autowired
	private InstalmentRepository instalmentRepository;

	@Autowired
	private ContractRepository contractRepository;

	@Autowired
	private EmiTransactionDetailsRepository emiTransactionDetailsRepository;

	@Autowired
	private EntityManager em;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private EmiTransactionDetailsService emiTransactionService;

	@Autowired
	private PaymentInfoRepository paymentInfoRepo;

	@Autowired
	private PaymentInfoRepository paymentinforepo;

	@Autowired
	private HomePositionMasterRepository homerepo;

	@Autowired
	private PaymentRefnoRepository seqRefNorepo;

	@Autowired
	private RealpayInstallmentReminderService realpayInstallmentReminderService;

	@Autowired
	private LoginUserInfoRepository loginUserInfoRepository;

	@Lazy
	@Autowired
	private ReinsuranceService reinsuranceService;

	@Autowired
	private ReceiptEntryService receiptEntryService;

	@Autowired
	private IntegrationController integrationController;

	@Autowired
	@Lazy
	private ClientAccountDetailsService clientAccountDetailsService;

	@Autowired
	private PremiaPushIntegration premiaPushIntegration;
	@Autowired
	private TokenGenerator tokenGenerator;


	private final Map<String, LocalDateTime> trackingStartTime = new ConcurrentHashMap<>();
	private final Map<String, Integer> trackingDaysMap = new ConcurrentHashMap<>();

	@Data
	public static class StatusProcessingResult {
		private boolean success;
		private String message;
		private String errorCode;
		private String instalmentReferenceNumber;
		private String statusProcessed;
		private LocalDateTime processedAt;
		private String responseCodeGroup;
		private String countryProcessed;
		private String productProcessed;

		public static StatusProcessingResult success(String instalmentRef, String status, String message) {
			StatusProcessingResult result = new StatusProcessingResult();
			result.setSuccess(true);
			result.setInstalmentReferenceNumber(instalmentRef);
			result.setStatusProcessed(status);
			result.setMessage(message);
			result.setProcessedAt(LocalDateTime.now());
			return result;
		}

		public static StatusProcessingResult success(String instalmentRef, String status, String message,
		                                             String responseGroup, String country, String product) {
			StatusProcessingResult result = success(instalmentRef, status, message);
			result.setResponseCodeGroup(responseGroup);
			result.setCountryProcessed(country);
			result.setProductProcessed(product);
			return result;
		}

		public static StatusProcessingResult error(String instalmentRef, String errorCode, String message) {
			StatusProcessingResult result = new StatusProcessingResult();
			result.setSuccess(false);
			result.setInstalmentReferenceNumber(instalmentRef);
			result.setErrorCode(errorCode);
			result.setMessage(message);
			result.setProcessedAt(LocalDateTime.now());
			return result;
		}
	}

	@Async
	public void processInstalmentChangesAsync(JsonNode instalmentChangesReportList, String token, String product) {
		logger.info("Starting async processing for product: {}", product);
		processInstalmentChanges(instalmentChangesReportList, token, product);
	}

	@Transactional
	public void processInstalmentChanges(JsonNode instalmentChangesReportList, String token, String product) {
		long startTime = System.currentTimeMillis();
		logger.info("Starting batch processing for product: {}", product);

		try {
			validateInput(instalmentChangesReportList, product);

			int recordCount = instalmentChangesReportList.size();
			logger.info("Processing {} records for product: {}", recordCount, product);

			long processedCount = StreamSupport.stream(instalmentChangesReportList.spliterator(), false)
					.map(instalment -> processSingleInstalment(instalment, token, product)).filter(Objects::nonNull)
					.count();

			long duration = System.currentTimeMillis() - startTime;

			logger.info("Completed batch processing - Product: {}, Processed: {}/{}, Duration: {}ms", product,
					processedCount, recordCount, duration);

		} catch (Exception e) {
			logger.error("CRITICAL_ERROR processing instalment changes for product {}: {}", product, e.getMessage(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public StatusProcessingResult processSingleInstalmentFromWebhook(JsonNode instalment, String token,
	                                                                 String product) {
		String instalmentReferenceNumber = "UNKNOWN";

		try {
			validateInstalmentNode(instalment);
			instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
			String instalmentStatus = getFieldAsText(instalment, "InstalmentStatus");
			String responseCode = getFieldAsText(instalment, "ResponseCode");

			logger.info("WEBHOOK_PROCESSING - Ref: {}, Status: {}, Code: {}", instalmentReferenceNumber,
					instalmentStatus, responseCode);
			/// Need to un commenet this method for using scheduler ...
//			if (isAlreadyProcessedByWebhook(instalmentReferenceNumber, instalmentStatus, responseCode)) {
//				logger.info("WEBHOOK_SKIP - Already processed: Ref={}, Status={}, Code={}", instalmentReferenceNumber,
//						instalmentStatus, responseCode);
//				return StatusProcessingResult.success(instalmentReferenceNumber, "SKIPPED",
//						"Already processed by webhook - duplicate webhook skipped");
//			}

			StatusProcessingResult result = processSingleInstalment(instalment, token, product);

			if (result != null && result.isSuccess()) {
				markInstalmentAsWebhookProcessed(instalmentReferenceNumber, instalmentStatus, responseCode);
				logger.info("WEBHOOK_FLAG_SET - Ref: {}", instalmentReferenceNumber);
			}

			return result;

		} catch (Exception e) {
			logger.error("WEBHOOK_PROCESSING_ERROR for instalment {}: {}", instalmentReferenceNumber, e.getMessage(),
					e);
			return StatusProcessingResult.error(instalmentReferenceNumber, "WEBHOOK_PROCESSING_ERROR", e.getMessage());
		}
	}

	@Transactional
	private void markInstalmentAsWebhookProcessed(String instalmentReferenceNumber, String status,
	                                              String responseCode) {
		try {
			Instalment instalment = instalmentRepository.findByInstalmentReferenceNumber(instalmentReferenceNumber);

			if (instalment != null) {
				instalment.setWebhookUpdated("Y");
				instalment.setWebhookUpdatedDate(LocalDateTime.now());
				instalment.setLastProcessedStatus(status);
				instalment.setLastProcessedResponseCode(responseCode);
				instalmentRepository.save(instalment);

				logger.debug("Webhook flag set for instalment: {} (Status: {}, Code: {})", instalmentReferenceNumber,
						status, responseCode);
			}

		} catch (Exception e) {
			logger.error("Error setting webhook flag for {}: {}", instalmentReferenceNumber, e.getMessage(), e);
		}
	}

	private boolean isAlreadyProcessedByWebhook(String instalmentReferenceNumber, String newStatus,
	                                            String newResponseCode) {
		try {
			Instalment instalment = instalmentRepository.findByInstalmentReferenceNumber(instalmentReferenceNumber);

			if (instalment != null && "Y".equals(instalment.getWebhookUpdated())) {

				String oldStatus = instalment.getLastProcessedStatus();
				String oldResponseCode = instalment.getLastProcessedResponseCode();

				String ns = newStatus == null ? "" : newStatus.trim();
				String os = oldStatus == null ? "" : oldStatus.trim();
				String nr = newResponseCode == null ? "" : newResponseCode.trim();
				String orc = oldResponseCode == null ? "" : oldResponseCode.trim();

				boolean isNewStatusChange = !ns.equalsIgnoreCase(os) || !nr.equalsIgnoreCase(orc);

				if (!isNewStatusChange) {
					logger.info("DUPLICATE_SKIP - Already processed Ref: {}, Status: {}, Code: {}",
							instalmentReferenceNumber, ns, nr);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			logger.error("Error checking webhook flag for {}: {}", instalmentReferenceNumber, e.getMessage(), e);
			return false;
		}
	}

	private boolean isAlreadyProcessedByWebhook(String instalmentReferenceNumber) {
		try {
			Instalment instalment = instalmentRepository.findByInstalmentReferenceNumber(instalmentReferenceNumber);
			return instalment != null && "Y".equals(instalment.getWebhookUpdated());
		} catch (Exception e) {
			logger.error("Error checking webhook flag for {}: {}", instalmentReferenceNumber, e.getMessage(), e);
			return false;
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public StatusProcessingResult processSingleInstalment(JsonNode instalment, String token, String product) {
		String instalmentReferenceNumber = "UNKNOWN";

		try {
			validateInstalmentNode(instalment);

			instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");

			/// Need to un commenet this method for using scheduler ...
//			if (isAlreadyProcessedByWebhook(instalmentReferenceNumber)) {
//				logger.info("SCHEDULER_SKIP - Already processed by webhook: {}", instalmentReferenceNumber);
//				return StatusProcessingResult.success(instalmentReferenceNumber, "SKIPPED",
//						"Already processed by webhook - scheduler update skipped");
//			}

			String instalmentStatus = getFieldAsText(instalment, "InstalmentStatus");
			String responseCode = getFieldAsText(instalment, "ResponseCode");

			validateRequiredFields(instalmentStatus, instalmentReferenceNumber);

			String country = getCountryFromProduct(product);
			String complianceProduct = getComplianceProduct(product);

			logger.debug("Processing instalment - Ref: {}, Status: {}, Country: {}, Product: {}",
					instalmentReferenceNumber, instalmentStatus, country, complianceProduct);

			StatusProcessingResult result = routeToStatusHandler(instalment, instalmentStatus, responseCode, country,
					complianceProduct, token);
			return result;

		} catch (ValidationException e) {
			logger.warn("Validation failed for instalment: {} - {}", instalmentReferenceNumber, e.getMessage());
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "VALIDATION_ERROR",
					e.getMessage());
			return result;
		} catch (Exception e) {
			logger.error("Processing error for instalment {}: {}", instalmentReferenceNumber, e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "PROCESSING_ERROR",
					"Error processing instalment: " + e.getMessage());
			return result;
		}
	}

	private StatusProcessingResult routeToStatusHandler(JsonNode instalment, String instalmentStatus,
	                                                    String responseCode, String country, String complianceProduct, String token) {

		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		String contractNumber = getRequiredField(instalment, "ContractNumber");

		logger.debug("Routing instalment - Ref: {}, Status: {}, Contract: {}", instalmentReferenceNumber,
				instalmentStatus, contractNumber);

		StatusProcessingResult result;

		switch (instalmentStatus) {
			case Status.FUTURE:
				result = processFutureStatus(instalment, token);
				break;

			case Status.SUCCESSFUL:
				result = processSuccessfulStatus(instalment, responseCode, token, "NO");
				break;

			case Status.PROCESSING:
				result = processProcessingStatus(instalment, responseCode, token);
				break;

			case Status.FAILED:
				ContractType contractTypeF = determineContractType(instalmentReferenceNumber, contractNumber);
				result = processFailedStatus(instalment, responseCode, country, complianceProduct, token,
						contractTypeF);
				break;

			case Status.DISPUTED:
				ContractType contractTypeD = determineContractType(instalmentReferenceNumber, contractNumber);
				result = processDisputedStatus(instalment, responseCode, country, complianceProduct, token,
						contractTypeD);
				break;

			case Status.ERROR:
				ContractType contractTypeE = determineContractType(instalmentReferenceNumber, contractNumber);
				result = processErrorStatus(instalment, responseCode, country, complianceProduct, token, contractTypeE);
				break;

			case Status.CANCELLED:
				result = processCancelledStatus(instalment, responseCode, token);
				break;

			case Status.HOLD:
				result = processHoldStatus(instalment, responseCode, token);
				break;

			case Status.SUSPENDED:
				result = processSuspendedStatus(instalment, responseCode, token);
				break;

			default:
				result = processUnknownStatus(instalment, instalmentStatus, responseCode, token);
				break;
		}
		return result;
	}

	@Transactional
	protected StatusProcessingResult processFailedStatus(JsonNode instalment, String responseCode, String country,
	                                                     String complianceProduct, String token, ContractType contractType) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		String clientNumber = getRequiredField(instalment, "ClientNumber");
		String contractNumber = getRequiredField(instalment, "ContractNumber");

		try {
			logger.info("Processing FAILED status for instalment: {}", instalmentReferenceNumber);

			clearTrackingData(instalmentReferenceNumber);
			validateInstalmentAmount(instalment);

			BigDecimal instalmentAmount = new BigDecimal(getRequiredField(instalment, "InstalmentAmount"));
			String responseGroup = analyzeResponseCodeGroup(responseCode, country);
			String groupDescription = getResponseCodeGroupDescription(responseCode, country, responseGroup);

			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalment, Status.FAILED, "FAILED_ENHANCED_PROCESSING", responseCode,
					"Failed with enhanced processing - " + groupDescription);

			logger.debug("Contract type determined: {} for contract: {}", contractType, contractNumber);

			switch (contractType) {
				case ORIGINAL:
					handleOriginalContractFailure(instalment, responseCode, country, instalmentAmount, clientNumber,
							contractNumber, responseGroup);
					break;

				case ARREAR:
					handleArrearContractFailure(instalment, responseCode, country, instalmentAmount, clientNumber,
							contractNumber, responseGroup);
					break;

				default:
					logger.error("Unknown contract type: {} for instalment: {}", contractType,
							instalmentReferenceNumber);
					throw new IllegalStateException("Unknown contract type: " + contractType);
			}

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.FAILED,
					"Failed status processed with enhanced rules", responseGroup, country, complianceProduct);
			return result;

		} catch (Exception e) {
			logger.error("Error processing FAILED status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_F_ERROR",
					"Error processing FAILED status: " + e.getMessage());
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processSuccessfulStatus(JsonNode instalment, String responseCode,
															 String token, String isFirstInstalmentPaid) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");

		try {
			logger.info("Processing SUCCESSFUL status for instalment: {}", instalmentReferenceNumber);

			clearTrackingData(instalmentReferenceNumber);
			processSuccessfulInstalment(instalment, responseCode, token,isFirstInstalmentPaid);

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.SUCCESSFUL,
					"Payment successfully processed");
			return result;

		} catch (Exception e) {
			logger.error("Error processing SUCCESSFUL status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_S_ERROR",
					"Error processing SUCCESSFUL status: " + e.getMessage());
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processFutureStatus(JsonNode instalment, String token) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		try {
			logger.info("Processing FUTURE status for instalment: {}", instalmentReferenceNumber);

			// PASSED FULL JSONNODE for Upsert
			updateInstalmentStatus(instalment, Status.FUTURE, "FUTURE_CONFIRMED", null,
					"Instalment scheduled for future collection");

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.FUTURE,
					"Future instalment confirmed");
			return result;
		} catch (Exception e) {
			logger.error("Error processing FUTURE status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_A_ERROR",
					"Error processing FUTURE status");
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processProcessingStatus(JsonNode instalment, String responseCode, String token) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		try {
			logger.info("Processing PROCESSING status for instalment: {}", instalmentReferenceNumber);

			trackingStartTime.putIfAbsent(instalmentReferenceNumber, LocalDateTime.now());

			// PASSED FULL JSONNODE for Upsert
			updateInstalmentStatus(instalment, Status.PROCESSING, "PROCESSING_CONFIRMED", responseCode,
					"Instalment in processing status");

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.PROCESSING,
					"Processing status confirmed");
			return result;

		} catch (Exception e) {
			logger.error("Error processing PROCESSING status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_W_ERROR",
					"Error processing PROCESSING status");
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processDisputedStatus(JsonNode instalment, String responseCode, String country,
	                                                       String complianceProduct, String token, ContractType contractType) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		String clientNumber = getRequiredField(instalment, "ClientNumber");
		String contractNumber = getRequiredField(instalment, "ContractNumber");

		try {
			logger.info("Processing DISPUTED status for instalment: {}", instalmentReferenceNumber);

			clearTrackingData(instalmentReferenceNumber);
			validateInstalmentAmount(instalment);

			BigDecimal instalmentAmount = new BigDecimal(getRequiredField(instalment, "InstalmentAmount"));
			String responseGroup = analyzeResponseCodeGroup(responseCode, country);

			switch (contractType) {
				case ORIGINAL:
					processCompleteDispute(instalment, responseCode, clientNumber, instalmentAmount, "Original",
							contractNumber);
					break;
				case ARREAR:
					processCompleteDispute(instalment, responseCode, clientNumber, instalmentAmount, "Arrear",
							contractNumber);
					break;
				default:
					processCompleteDispute(instalment, responseCode, clientNumber, instalmentAmount,
							"DISPUTED_UNKNOWN_TYPE", contractNumber);
					break;
			}

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.DISPUTED,
					"Disputed status processed", responseGroup, country, complianceProduct);
			return result;

		} catch (Exception e) {
			logger.error("Error processing DISPUTED status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_D_ERROR",
					"Error processing DISPUTED status");
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processErrorStatus(JsonNode instalment, String responseCode, String country,
	                                                    String complianceProduct, String token, ContractType contractType) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");

		try {
			logger.info("Processing ERROR status for instalment: {}", instalmentReferenceNumber);

			clearTrackingData(instalmentReferenceNumber);
			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalment, Status.ERROR, "ERROR_STATUS_RECEIVED", responseCode,
					"Error status received from RealPay - Manual review required");

			logger.warn("STATUS_E_REQUIRES_MANUAL_REVIEW - Ref: {}, Code: {}", instalmentReferenceNumber, responseCode);

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.ERROR,
					"Error status received - Manual review required", "ERROR", country, complianceProduct);
			return result;

		} catch (Exception e) {
			logger.error("Error processing ERROR status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_E_ERROR",
					"Error processing ERROR status");
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processCancelledStatus(JsonNode instalment, String responseCode, String token) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		try {
			logger.info("Processing CANCELLED status for instalment: {}", instalmentReferenceNumber);

			clearTrackingData(instalmentReferenceNumber);
			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalment, Status.EFT_PENDING, "CANCELLED_STATUS_CONFIRMED", responseCode,
					"Instalment cancelled - Confirmed by RealPay");

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.CANCELLED,
					"Cancellation confirmed");
			return result;

		} catch (Exception e) {
			logger.error("Error processing CANCELLED status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_C_ERROR",
					"Error processing CANCELLED status");
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processHoldStatus(JsonNode instalment, String responseCode, String token) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		try {
			logger.info("Processing HOLD status for instalment: {}", instalmentReferenceNumber);

			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalment, Status.HOLD, "HOLD_STATUS_RECEIVED", responseCode,
					"Instalment on hold - Manual review required");

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.HOLD,
					"Hold status confirmed");
			return result;

		} catch (Exception e) {
			logger.error("Error processing HOLD status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_H_ERROR",
					"Error processing HOLD status");
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processSuspendedStatus(JsonNode instalment, String responseCode, String token) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		try {
			logger.info("Processing SUSPENDED status for instalment: {}", instalmentReferenceNumber);

			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalment, Status.SUSPENDED, "SUSPENDED_STATUS_RECEIVED", responseCode,
					"Instalment suspended - Manual review required");

			StatusProcessingResult result = StatusProcessingResult.success(instalmentReferenceNumber, Status.SUSPENDED,
					"Suspended status confirmed");
			return result;

		} catch (Exception e) {
			logger.error("Error processing SUSPENDED status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "STATUS_U_ERROR",
					"Error processing SUSPENDED status");
			return result;
		}
	}

	@Transactional
	protected StatusProcessingResult processUnknownStatus(JsonNode instalment, String instalmentStatus,
	                                                      String responseCode, String token) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		try {
			logger.warn("Processing UNKNOWN status for instalment: {}, Status: {}", instalmentReferenceNumber,
					instalmentStatus);

			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalment, instalmentStatus, "UNKNOWN_STATUS_RECEIVED", responseCode,
					"Unknown status received: " + instalmentStatus + " - Manual review required");

			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber, "UNKNOWN_STATUS",
					"Unknown status received: " + instalmentStatus);
			return result;

		} catch (Exception e) {
			logger.error("Error processing UNKNOWN status for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			StatusProcessingResult result = StatusProcessingResult.error(instalmentReferenceNumber,
					"UNKNOWN_STATUS_ERROR", "Error processing unknown status: " + e.getMessage());
			return result;
		}
	}

	private void handleOriginalContractFailure(JsonNode instalment, String responseCode, String country,
	                                           BigDecimal instalmentAmount, String clientNumber, String contractNumber, String responseGroup) {
		String originalContractNumber = getOriginalContractNumber(contractNumber, ContractType.ORIGINAL);

		logger.debug("Processing original contract failure - Contract: {}", originalContractNumber);

		if (hasTwoConsecutiveFailures(originalContractNumber)) {
			logger.info("RULE_1: Two consecutive failures detected - Cancelling contracts for: {}",
					originalContractNumber);
			cancelContractsWithAPI(originalContractNumber, "RULE_1_TWO_CONSECUTIVE_FAILURES_" + responseGroup);
			return;
		}

		if (hasThreeTotalFailures(originalContractNumber)) {
			logger.info("RULE_2: Three total failures detected - Cancelling contracts for: {}", originalContractNumber);
			cancelContractsWithAPI(originalContractNumber, "RULE_2_THREE_TOTAL_FAILURES_" + responseGroup);
			return;
		}

		logger.debug("No cancellation rules triggered - Processing by response group: {}", responseGroup);
		handleOriginalContractByResponseGroup(instalment, responseCode, country, instalmentAmount, clientNumber,
				contractNumber, responseGroup);
	}

	private void handleArrearContractFailure(JsonNode instalment, String responseCode, String country,
	                                         BigDecimal instalmentAmount, String clientNumber, String contractNumber, String responseGroup) {
		String originalContractNumber = getOriginalContractNumber(contractNumber, ContractType.ARREAR);

		logger.info("RULE_3: Arrear contract failure - Cancelling contracts for original: {}", originalContractNumber);
		cancelContractsWithAPI(originalContractNumber, "RULE_3_ARREAR_FAILURE_" + responseGroup);
	}

	@Transactional
	private void cancelContractsWithAPI(String originalContractNumber, String reason) {
		try {
			logger.info("Starting unified cancellation flow for contract: {}", originalContractNumber);

			List<Instalment> installmentsToCancel = findAllFutureInstallments(originalContractNumber);

			if (installmentsToCancel.isEmpty()) {
				logger.info("No future installments found to cancel for contract: {}", originalContractNumber);
				return;
			}

			logger.info("Found {} installments to cancel for contract: {}", installmentsToCancel.size(),
					originalContractNumber);

			cancelInstallmentsLocally(installmentsToCancel, reason);

			boolean allApiCallsSuccessful = callRealPayCancellationAPI(installmentsToCancel, reason);

			if (allApiCallsSuccessful) {
				cancelPolicyForContract(originalContractNumber, reason);
			} else {
				logger.warn("Skipping policy cancellation due to RealPay API failures for contract: {}",
						originalContractNumber);
			}

			logger.info("Completed unified cancellation flow for contract: {}", originalContractNumber);

		} catch (Exception e) {
			logger.error("Error cancelling contracts for {}: {}", originalContractNumber, e.getMessage(), e);
		}
	}

	private List<Instalment> findAllFutureInstallments(String originalContractNumber) {
		List<Instalment> allInstallments = new ArrayList<>();

		List<Instalment> originalFuture = instalmentRepository.findByContractNumberAndInstalmentStatusIn(
				originalContractNumber, Arrays.asList(Status.FUTURE, Status.PROCESSING));
		allInstallments.addAll(originalFuture);

		List<Contract> arrearContracts = contractRepository
				.findArrearContractsByOriginalContractNumber(originalContractNumber);

		for (Contract arrearContract : arrearContracts) {
			if (StringUtils.isBlank(arrearContract.getContractSequence())
					|| "0".equals(arrearContract.getContractSequence())) {
				logger.warn("Skipping arrear contract with invalid sequence: {}", arrearContract.getContractNumber());
				continue;
			}

			List<Instalment> arrearFuture = instalmentRepository.findByContractNumberAndInstalmentStatusIn(
					arrearContract.getContractNumber(), Arrays.asList(Status.FUTURE, Status.PROCESSING));
			allInstallments.addAll(arrearFuture);
		}
		return allInstallments;
	}

	private void cancelInstallmentsLocally(List<Instalment> installments, String reason) {
		for (Instalment instalment : installments) {
			instalment.setInstalmentStatus(reason.equals("CANCEL POLICY BY ENDORSEMENT") ? Status.CANCELLED : Status.EFT_PENDING );
			instalment.setStatus("CANCELLED_BY_UNIFIED_RULES");
			instalment.setMessage("Cancelled per unified cancellation rules: " + reason);
			instalment.setLastUpdateDate(new Date());
			instalment.setSyncStatus("P");
			instalment.setUpdatedDate(LocalDateTime.now());
		}
		instalmentRepository.saveAll(installments);

		logger.info("Cancelled {} installments locally", installments.size());
	}

	private boolean callRealPayCancellationAPI(List<Instalment> installments, String reason) {
		boolean allSuccessful = true;
		int successCount = 0;
		int failureCount = 0;

		for (Instalment instalment : installments) {
			try {
				String contractSequence = getContractSequenceFromInstallment(instalment);
				String instalmentSequence = getInstalmentSequenceFromInstallment(instalment);

				logger.info("Cancelling installment: {} with contractSeq: {}, instalmentSeq: {}",
						instalment.getInstalmentReferenceNumber(), contractSequence, instalmentSequence);

				JsonNode apiResponse = clientService.cancelInstalment(
						determineProductFromContract(instalment.getQuoteNo()), instalment.getClientNumber(),
						instalment.getContractNumber(), contractSequence, instalmentSequence, false);

				if (isRealPayCancellationSuccessful(apiResponse)) {
					instalment.setSyncStatus("S");
					instalment.setMessage("Cancelled in RealPay: " + reason);
					successCount++;
					logger.info("SUCCESS: RealPay cancellation for: {}", instalment.getInstalmentReferenceNumber());
				} else {
					instalment.setSyncStatus("F");
					instalment.setMessage("RealPay cancellation failed: " + extractFailureReason(apiResponse));
					instalment.setInstalmentStatus(Status.FAILED);
					allSuccessful = false;
					failureCount++;
					logger.error("FAILED: RealPay cancellation for: {} - Reason: {}",
							instalment.getInstalmentReferenceNumber(), extractFailureReason(apiResponse));
				}

			} catch (Exception e) {
				logger.error("RealPay API call exception for {}: {}", instalment.getInstalmentReferenceNumber(),
						e.getMessage());
				instalment.setSyncStatus("E");
				instalment.setMessage("RealPay API error: " + e.getMessage());
				instalment.setInstalmentStatus(Status.FAILED);
				instalmentRepository.save(instalment);
				allSuccessful = false;
				failureCount++;
			}

			instalmentRepository.save(instalment);
		}

		logger.info("RealPay API Cancellation Summary - Success: {}, Failed: {}", successCount, failureCount);
		return allSuccessful;
	}

	public String getContractSequenceFromInstallment(Instalment instalment) {
		try {
			if (instalment.getContract() != null
					&& StringUtils.isNotBlank(instalment.getContract().getContractSequence())) {
				return instalment.getContract().getContractSequence();
			}

			if (StringUtils.isNotBlank(instalment.getInstalmentReferenceNumber())) {
				String ref = instalment.getInstalmentReferenceNumber();
				if (ref.length() >= 10) {
					return ref.substring(0, 10);
				}
			}

			logger.warn("Could not extract contract sequence for installment: {}",
					instalment.getInstalmentReferenceNumber());
			return "1";

		} catch (Exception e) {
			logger.error("Error extracting contract sequence: {}", e.getMessage());
			return "1";
		}
	}

	public String getInstalmentSequenceFromInstallment(Instalment instalment) {
		try {
			if (StringUtils.isNotBlank(instalment.getInstalmentReferenceNumber())) {
				String ref = instalment.getInstalmentReferenceNumber();
				if (ref.length() > 10) {
					String seq = ref.substring(10);
					return String.valueOf(Integer.parseInt(seq));
				}
			}

			if (instalment.getNoOfInstalment() != null) {
				return instalment.getNoOfInstalment().toString();
			}

			logger.warn("Could not extract installment sequence for: {}", instalment.getInstalmentReferenceNumber());
			return "1";

		} catch (Exception e) {
			logger.error("Error extracting installment sequence: {}", e.getMessage());
			return "1";
		}
	}

	public boolean isRealPayCancellationSuccessful(JsonNode response) {
		try {
			if (response == null) {
				logger.error("Null response from RealPay cancellation API");
				return false;
			}

			logger.debug("RealPay Cancellation Response: {}", response.toString());

			if (response.has("InstalmentDeleteResponse")) {
				JsonNode deleteResponse = response.get("InstalmentDeleteResponse");
				if (deleteResponse.isArray() && deleteResponse.size() > 0) {
					JsonNode firstResponse = deleteResponse.get(0);

					if (firstResponse.has("Failed") && firstResponse.get("Failed").size() > 0) {
						logger.error("FAILED: RealPay returned failures in response: {}",
								firstResponse.get("Failed").toString());
						return false;
					}

					if (firstResponse.has("Successful") && firstResponse.get("Successful").size() > 0) {
						JsonNode successfulItem = firstResponse.get("Successful").get(0);
						if (successfulItem.has("InstalmentStatus")) {
							String status = successfulItem.get("InstalmentStatus").asText();
							if ("I".equals(status)) {
								logger.info("SUCCESS: RealPay cancellation confirmed with status: I");
								return true;
							}
						}
						return true;
					}
				}
			}

			if (response.has("APIResponse")) {
				JsonNode apiResponse = response.get("APIResponse");
				if (apiResponse.has("Status")) {
					String status = apiResponse.get("Status").asText();
					if ("SUCCESS".equalsIgnoreCase(status)) {
						logger.info("SUCCESS: RealPay APIResponse status: SUCCESS");
						return true;
					}
				}
			}

			logger.error("FAILED: RealPay cancellation not confirmed in response");
			return false;

		} catch (Exception e) {
			logger.error("Error parsing RealPay cancellation response: {}", e.getMessage());
			return false;
		}
	}

	public String extractFailureReason(JsonNode response) {
		try {
			if (response.has("InstalmentDeleteResponse")) {
				JsonNode deleteResponse = response.get("InstalmentDeleteResponse");
				if (deleteResponse.isArray() && deleteResponse.size() > 0) {
					JsonNode firstResponse = deleteResponse.get(0);
					if (firstResponse.has("Failed") && firstResponse.get("Failed").size() > 0) {
						JsonNode failed = firstResponse.get("Failed").get(0);
						if (failed.has("Failures") && failed.get("Failures").size() > 0) {
							JsonNode firstFailure = failed.get("Failures").get(0);
							String failureCode = firstFailure.has("FailureCode")
									? firstFailure.get("FailureCode").asText()
									: "UNKNOWN_CODE";
							String failureDesc = firstFailure.has("FailureDescription")
									? firstFailure.get("FailureDescription").asText()
									: "Unknown failure";
							return failureCode + ": " + failureDesc;
						}
						return failed.toString();
					}
				}
			}

			if (response.has("APIResponse")) {
				JsonNode apiResponse = response.get("APIResponse");
				if (apiResponse.has("Errors") && apiResponse.get("Errors").size() > 0) {
					JsonNode firstError = apiResponse.get("Errors").get(0);
					return firstError.asText();
				}
			}

		} catch (Exception e) {
			logger.error("Error extracting failure reason", e);
		}
		return "Unknown failure - check RealPay response";
	}

	private void cancelPolicyForContract(String contractNumber, String cancellationReason) {
		try {
			Optional<Contract> contractOpt = contractRepository.findByContractNumber(contractNumber);
			if (!contractOpt.isPresent()) {
				logger.warn("Contract not found for policy cancellation: {}", contractNumber);
				return;
			}

			Contract contract = contractOpt.get();
			String quoteNo = contract.getQuoteNo();

			if (StringUtils.isBlank(quoteNo)) {
				logger.warn("QuoteNo not found for contract: {}", contractNumber);
				return;
			}

			logger.info("POLICY_CANCELLED_SUCCESSFULLY - Quote: {}, Reason: {}", quoteNo, cancellationReason);

		} catch (Exception e) {
			logger.error("Error cancelling policy for contract {}: {}", contractNumber, e.getMessage(), e);
		}
	}

	private void handleOriginalContractByResponseGroup(JsonNode instalment, String responseCode, String country,
	                                                   BigDecimal instalmentAmount, String clientNumber, String contractNumber, String responseGroup) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		logger.debug("Processing response group {} for instalment: {}", responseGroup, instalmentReferenceNumber);

		switch (responseGroup) {
			case "GROUP1_INSUFFICIENT_FUNDS":
				handleInsufficientFunds(instalment, responseCode, country, instalmentAmount, clientNumber,
						contractNumber);
				break;

			case "GROUP2_PERMANENT_CLOSURE":
				handlePermanentClosure(instalment, responseCode, country, instalmentAmount, clientNumber,
						contractNumber);
				break;

			case "GROUP3_NEW_MANDATE":
				handleNewMandate(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber);
				break;

			case "GROUP4_DISPUTE":
				handleDispute(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber);
				break;

			case "GROUP5_TECHNICAL_ERRORS":
				handleTechnicalError(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber);
				break;

			default:
				logger.warn("Unknown response group: {} - Defaulting to EFT", responseGroup);
				handleEFTPayment(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber,
						responseGroup);
				break;
		}
	}

	private void handleInsufficientFunds(JsonNode instalment, String responseCode, String country,
	                                     BigDecimal instalmentAmount, String clientNumber, String contractNumber) {
		PaymentPreference preference = getCustomerPaymentPreference(clientNumber);
		logger.debug("Insufficient funds - Client: {}, Preference: {}", clientNumber, preference);

		switch (preference) {
			case EFT:
				handleEFTPayment(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber,
						"GROUP1");
				break;

			case DOUBLE_DEBIT_ORDER_ARREAR:
				handleDoubleDebitPayment(instalment, responseCode, country, instalmentAmount, clientNumber,
						contractNumber,
						"GROUP1");
				break;

			default:
				logger.warn("Unknown payment preference: {} - Defaulting to EFT", preference);
				handleEFTPayment(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber,
						"GROUP1");
				break;
		}
	}

	private void handleTechnicalError(JsonNode instalment, String responseCode, String country,
	                                  BigDecimal instalmentAmount, String clientNumber, String contractNumber) {
		PaymentPreference preference = getCustomerPaymentPreference(clientNumber);
		logger.debug("Technical error - Client: {}, Preference: {}", clientNumber, preference);

		switch (preference) {
			case EFT:
				handleEFTPayment(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber,
						"GROUP5");
				break;

			case DOUBLE_DEBIT_ORDER_ARREAR:
				handleDoubleDebitPayment(instalment, responseCode, country, instalmentAmount, clientNumber,
						contractNumber,
						"GROUP5");
				break;

			default:
				logger.warn("Unknown payment preference: {} - Defaulting to EFT", preference);
				handleEFTPayment(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber,
						"GROUP5");
				break;
		}
	}

	private void handlePermanentClosure(JsonNode instalment, String responseCode, String country,
	                                    BigDecimal instalmentAmount, String clientNumber, String contractNumber) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		logger.warn("Processing permanent closure for instalment: {}", instalmentReferenceNumber);

		// PASSED FULL JSONNODE
		updateInstalmentStatus(instalment, Status.CANCELLED, "PERMANENT_CLOSURE", responseCode,
				"Account permanently closed - Group 2: " + getResponseCodeDescription(responseCode, country));

		cancelContractsWithAPI(contractNumber, "PERMANENT_CLOSURE_" + country + ": " + responseCode);

		logger.warn("Permanent closure processed for client: {}, contract: {}", clientNumber, contractNumber);
	}

	private void handleNewMandate(JsonNode instalment, String responseCode, String country, BigDecimal instalmentAmount,
	                              String clientNumber, String contractNumber) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		logger.warn("Processing new mandate requirement for instalment: {}", instalmentReferenceNumber);

		// PASSED FULL JSONNODE
		updateInstalmentStatus(instalment, Status.CANCELLED, "NEW_MANDATE_REQUIRED", responseCode,
				"New mandate required - Group 3: " + getResponseCodeDescription(responseCode, country));

		cancelContractsWithAPI(contractNumber, "NEW_MANDATE_REQUIRED_" + country + ": " + responseCode);
	}

	private void handleDispute(JsonNode instalment, String responseCode, String country, BigDecimal instalmentAmount,
	                           String clientNumber, String contractNumber) {
		String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
		logger.warn("Processing dispute for instalment: {}", instalmentReferenceNumber);

		processCompleteDispute(instalment, responseCode, clientNumber, instalmentAmount, "DISPUTE_PROCESSED",
				contractNumber);
	}

	private void handleEFTPayment(JsonNode instalment, String responseCode, String country, BigDecimal instalmentAmount,
	                              String clientNumber, String contractNumber, String group) {
		String ref = getRequiredField(instalment, "InstalmentReferenceNumber");
		logger.debug("Processing EFT payment for instalment: {}", ref);

		Instalment instalmentRec = instalmentRepository.findByInstalmentReferenceNumber(ref);
		HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(instalmentRec.getQuoteNo());

		Optional<ClientAccountDetails> clientAccountDetails = clientAccountDetailsRepository
				.findByQuoteNo(instalmentRec.getQuoteNo());

		LoginUserInfo loginUserInfo = loginUserInfoRepository.findByLoginId(homePositionMaster.getCustomerCode());

		/// for creating record in realpay_installment_remainder for sending
		/// notificaiton for 15 days once ...

		logger.info("Creating record in realpay_installment_remainder for installmentReference No: " + ref);

		realpayInstallmentReminderService.createReminder(instalmentRec.getClientNumber(),
				homePositionMaster.getPolicyNo(), instalmentRec.getQuoteNo(),
				instalmentRec.getNoOfInstalment().toString(), instalmentAmount, LocalDateTime.now(),
				clientAccountDetails.get().getClientName(), loginUserInfo.getLoginId(), loginUserInfo.getCustomerName(),
				clientAccountDetails.get().getEmail(), loginUserInfo.getUserMail(), homePositionMaster.getCompanyId(),
				instalmentRec.getInstalmentReferenceNumber(),"PHOENIX_PREMIUM_INSTALLMENT_PAYMENT_FAILED");

		updateInstalmentStatus(instalment, Status.EFT_PENDING, "EFT_PAYMENT_PREFERENCE_" + group, responseCode,
				"Customer chose EFT; Premia to capture receipt and sync to WeCore. [" + group + ": "
						+ getResponseCodeDescription(responseCode, country) + "]");
	}

	private void handleDoubleDebitPayment(JsonNode instalment, String responseCode, String country,
	                                      BigDecimal instalmentAmount, String clientNumber, String contractNumber, String group) {
		String ref = getRequiredField(instalment, "InstalmentReferenceNumber");
		logger.debug("Processing double debit payment for instalment: {}", ref);

		try {
			Contract original = contractRepository.findByContractNumber(contractNumber).orElse(null);
			if (original == null) {
				logger.error("Original contract not found: {}", contractNumber);
				return;
			}

			Optional<ClientAccountDetails> clientOpt = clientAccountDetailsRepository
					.findLatestByClientNumber(clientNumber);
			if (clientOpt.isEmpty()) {
				logger.error("Client not found: {}", clientNumber);
				return;
			}

			/// updating double debit pending status ...
			Instalment failedInst = instalmentRepository.findByInstalmentReferenceNumber(ref);
			failedInst.setInstalmentStatus(Status.DOUBLE_DEBIT_PENDING);
			instalmentRepository.save(failedInst);

			createArrearContractForFailedInstallment(failedInst, instalmentAmount, "DOUBLE_DEBIT_ARREAR_" + group + "_"
							+ country + ": " + responseCode + "[" + getResponseCodeDescription(responseCode, country) + "]",
					original, clientOpt.get());

		} catch (Exception e) {
			logger.error("Error creating arrear contract - Fallback to EFT: {}", e.getMessage(), e);
			handleEFTPayment(instalment, responseCode, country, instalmentAmount, clientNumber, contractNumber, group);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	protected Contract createArrearContractForFailedInstallment(Instalment failedInstalment,
	                                                            BigDecimal instalmentAmount, String reason, Contract originalContract, ClientAccountDetails client) {
		try {
			logger.info("Creating arrear contract for failed installment: {}",
					failedInstalment != null ? failedInstalment.getInstalmentReferenceNumber() : "null");

			String arrearContractNumber = generateArrearContractNumber(originalContract.getContractNumber());

			if (arrearContractNumber.length() > 20) {
				logger.error("Generated contract number exceeds 20 character limit: {}", arrearContractNumber);
				throw new RuntimeException("Generated contract number exceeds 20 character limit");
			}

			logger.debug("Generated arrear contract number: {} (length: {})", arrearContractNumber,
					arrearContractNumber.length());

			Optional<ClientAccountDetails> freshClientOpt = clientAccountDetailsRepository.findById(client.getId());
			if (freshClientOpt.isEmpty()) {
				logger.error("Client not found in new transaction: {}", client.getClientNumber());
				throw new RuntimeException("Client not found: " + client.getClientNumber());
			}

			ClientAccountDetails freshClient = freshClientOpt.get();

			Contract arrearContract = new Contract();
			arrearContract.setClientAccount(freshClient);
			arrearContract.setClientNumber(freshClient.getClientNumber());
			arrearContract.setContractNumber(arrearContractNumber);
			arrearContract.setOriginalContractNumber(originalContract.getContractNumber());
			arrearContract.setContractType("ARREAR");
			arrearContract.setFrequencyCode(originalContract.getFrequencyCode());
			arrearContract.setCollectionDay(originalContract.getCollectionDay());
			arrearContract.setTrackingCode(originalContract.getTrackingCode());
			arrearContract.setDebitSequenceType(originalContract.getDebitSequenceType());
			arrearContract.setInstalmentAmount(instalmentAmount);
			arrearContract.setNumberOfInstalments(1);
			arrearContract.setFirstCollectionAmount(instalmentAmount);
			arrearContract.setCtcPercentage(originalContract.getCtcPercentage());
			arrearContract.setQuoteNo(
					failedInstalment != null ? failedInstalment.getQuoteNo() : originalContract.getQuoteNo());

			Date arrearCollectionDate = calculateArrearCollectionDate(
					failedInstalment != null ? failedInstalment.getInstalmentActionDate() : new Date());
			logger.debug("Arrear collection date calculated: {} (original: {})", arrearCollectionDate,
					failedInstalment != null ? failedInstalment.getInstalmentActionDate() : "N/A");

			arrearContract.setFirstCollectionDate(arrearCollectionDate);
			arrearContract.setInstalmentStartDate(arrearCollectionDate);
			arrearContract.setStatus("PENDING");
			arrearContract.setMessage("Arrear contract created for failed installment: " + reason);
			arrearContract.setCreatedDate(LocalDateTime.now());
			arrearContract.setUpdatedDate(LocalDateTime.now());

			Contract savedArrearContract = contractRepository.save(arrearContract);
			logger.info("Arrear contract created successfully - ID: {}, Number: {}", savedArrearContract.getId(),
					savedArrearContract.getContractNumber());

			createSingleArrearInstallment(savedArrearContract, freshClient, failedInstalment, instalmentAmount,
					arrearCollectionDate);

			createArrearContractInRealPay(savedArrearContract, reason,failedInstalment);

			logger.info("Arrear contract creation complete: {}", savedArrearContract.getContractNumber());
			return savedArrearContract;

		} catch (Exception e) {
			logger.error("Error creating arrear contract: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to create arrear contract", e);
		}
	}

	private String generateArrearContractNumber(String originalContractNumber) {
		try {
			String sequenceSuffix = "01";
			List<Contract> existingArrears = contractRepository.findByOriginalContractNumber(originalContractNumber);

			if (!existingArrears.isEmpty()) {
				long arrearCount = existingArrears.stream().filter(c -> "ARREAR".equals(c.getContractType())).count();
				sequenceSuffix = String.format("%02d", arrearCount + 1);
			}

			long timestamp = System.currentTimeMillis() / 1000;
			return String.format("ARR-%d-%s", timestamp, sequenceSuffix);

		} catch (Exception e) {
			logger.error("Error generating arrear contract number: {}", e.getMessage(), e);
			long randomSuffix = System.currentTimeMillis() % 100000;
			return String.format("ARR-%d", randomSuffix);
		}
	}

	private Date calculateArrearCollectionDate(Date originalDate) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(originalDate);

			int originalDay = cal.get(Calendar.DAY_OF_MONTH);
			cal.add(Calendar.MONTH, 1);

			int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			if (originalDay > maxDay) {
				cal.set(Calendar.DAY_OF_MONTH, maxDay);
			} else {
				cal.set(Calendar.DAY_OF_MONTH, originalDay);
			}

			return cal.getTime();
		} catch (Exception e) {
			logger.error("Error calculating arrear collection date: {}", e.getMessage(), e);
			return new Date();
		}
	}

	private void createSingleArrearInstallment(Contract arrearContract, ClientAccountDetails client,
	                                           Instalment failedInstalment, BigDecimal instalmentAmount, Date collectionDate) {
		try {
			Instalment arrearInstalment = new Instalment();
			arrearInstalment.setClientAccount(client);
			arrearInstalment.setContract(arrearContract);
			arrearInstalment.setQuoteNo(failedInstalment != null ? failedInstalment.getQuoteNo() : null);
			arrearInstalment.setProductId(failedInstalment != null ? failedInstalment.getProductId() : null);
			arrearInstalment.setNoOfInstalment(1L);
			arrearInstalment.setInstalmentActionDate(collectionDate);
			arrearInstalment.setInstalmentAmount(instalmentAmount);
			arrearInstalment.setClientNumber(client.getClientNumber());
			arrearInstalment.setContractNumber(arrearContract.getContractNumber());
			arrearInstalment.setTrackingCode(client.getTrackingCode());
			arrearInstalment.setDebitSequenceType(client.getDebitSequenceType());
			arrearInstalment.setCtcAmount(BigDecimal.ZERO);
			arrearInstalment.setStatus(Status.FUTURE);
			arrearInstalment.setInstalmentStatus("A");
			arrearInstalment.setMessage("Arrear installment created for failed original installment: "
					+ (failedInstalment != null ? failedInstalment.getInstalmentReferenceNumber() : "N/A"));
			arrearInstalment.setCreatedDate(LocalDateTime.now());
			arrearInstalment.setUpdatedDate(LocalDateTime.now());
			arrearInstalment.setArrearMonth(failedInstalment.getNoOfInstalment());
			Instalment saved = instalmentRepository.save(arrearInstalment);
			instalmentRepository.save(saved);

			logger.info("Arrear installment created successfully for contract: {}", arrearContract.getContractNumber());

		} catch (Exception e) {
			logger.error("Error creating arrear installment: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to create arrear installment", e);
		}
	}

	private void createArrearContractInRealPay(Contract arrearContract, String reason, Instalment failedInstalment) {
		try {
			logger.info("Creating arrear contract in RealPay: {}", arrearContract.getContractNumber());

			JsonNode contractRequest = createArrearContractAPIRequest(arrearContract);
			String product = determineProductFromContract(arrearContract.getQuoteNo());

			JsonNode response = clientService.callClient(product, contractRequest, "post", "contract",
					arrearContract.getClientAccount().getBeneficiaryUser());
			logger.debug("RealPay arrear contract creation response: {}", response);
			JsonNode contractPostResponse = response.path("ContractPostResponse");

			long instalmentReferenceNumber = contractPostResponse
					.get(0)
					.path("Successful")
					.get(0)
					.path("ContractInstalments")
					.get(0)
					.path("InstalmentReferenceNumber")
					.asLong();

			logger.info("Arrear instalment reference number {}",instalmentReferenceNumber);

			Instalment arrearInstalment = instalmentRepository.findByQuoteNoAndArrearMonth(arrearContract.getQuoteNo(), failedInstalment.getNoOfInstalment());
			if(arrearInstalment!=null){
				arrearInstalment.setInstalmentReferenceNumber(String.valueOf(instalmentReferenceNumber));
				instalmentRepository.save(arrearInstalment);
				logger.info("Updated in Instalment Table for {} - {}",arrearContract.getQuoteNo(),instalmentReferenceNumber);
			}else{
				logger.warn("No record found in Instalment Table for {} - {}",arrearContract.getQuoteNo(),instalmentReferenceNumber );
			}

			HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(arrearContract.getQuoteNo());

			EmiTransactionDetails emiTransactionDetails = emiTransactionDetailsRepository.findByQuoteNoAndInstalmentAndCompanyIdAndProductId(
					arrearContract.getQuoteNo(),String.valueOf(failedInstalment.getNoOfInstalment()),
					homePositionMaster.getCompanyId(),String.valueOf(homePositionMaster.getProductId())
			);
			if(emiTransactionDetails!=null){
				emiTransactionDetails.setInstalmentReferenceNumber(String.valueOf(instalmentReferenceNumber));
				emiTransactionDetailsRepository.save(emiTransactionDetails);
				logger.info("Updated in Emi transaction detail for {} - {}",arrearContract.getQuoteNo(),instalmentReferenceNumber);
			}else{
				logger.warn("No record found in EMI Transaction Detail for {} - {} - {} - {}",arrearContract.getQuoteNo(),failedInstalment.getNoOfInstalment(),
						homePositionMaster.getCompanyId(),homePositionMaster.getProductId());
			}
			logger.info("Arrear contract create response : "+response);

			if (response != null && isRealPayAPIUpdateSuccessful(response)) {
				logger.info("Arrear contract created successfully in RealPay: {}", arrearContract.getContractNumber());
			} else {
				logger.error("Failed to create arrear contract in RealPay: {}", arrearContract.getContractNumber());
			}

		} catch (Exception e) {
			logger.error("Error creating arrear contract in RealPay {}: {}", arrearContract.getContractNumber(),
					e.getMessage(), e);
		}
	}

	private JsonNode createArrearContractAPIRequest(Contract arrearContract) {
		try {
			Map<String, Object> request = new HashMap<>();
			request.put("ClientNumber", arrearContract.getClientNumber());
			request.put("ContractNumber", arrearContract.getContractNumber());
			request.put("FrequencyCode", arrearContract.getFrequencyCode());
			request.put("CollectionDay", arrearContract.getCollectionDay());
			request.put("TrackingCode", arrearContract.getTrackingCode());

			if (arrearContract.getInstalmentStartDate() != null) {
				String formattedDate = new SimpleDateFormat("yyyyMMdd").format(arrearContract.getInstalmentStartDate());
				request.put("InstalmentStartDate", formattedDate);
			}

			request.put("InstalmentAmount", arrearContract.getInstalmentAmount().doubleValue());
			request.put("NumberOfInstalments", 1);
			request.put("CTCPercentage",
					arrearContract.getCtcPercentage() != null ? arrearContract.getCtcPercentage() : 1);

			logger.debug("Arrear contract API request created: {}", request);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.valueToTree(request);

		} catch (Exception e) {
			logger.error("Error creating arrear contract API request: {}", e.getMessage(), e);
			return null;
		}
	}

	private void validateInput(JsonNode instalmentChangesReportList, String product) {
		if (instalmentChangesReportList == null || !instalmentChangesReportList.isArray()) {
			throw new ValidationException(
					"Invalid instalment changes report - null or not array for product: " + product);
		}
	}

	private void validateInstalmentNode(JsonNode instalment) {
		if (instalment == null) {
			throw new ValidationException("Instalment node is null");
		}
	}

	private void validateRequiredFields(String instalmentStatus, String instalmentReferenceNumber) {
		if (instalmentStatus == null) {
			throw new ValidationException("Instalment status is missing for reference: " + instalmentReferenceNumber);
		}
	}

	private void validateInstalmentAmount(JsonNode instalment) {
		if (!instalment.has("InstalmentAmount") || instalment.get("InstalmentAmount").isNull()) {
			throw new ValidationException("InstalmentAmount is missing for failed instalment");
		}
	}

	private String getRequiredField(JsonNode node, String fieldName) {
		if (!node.has(fieldName) || node.get(fieldName).isNull()) {
			throw new ValidationException("Required field '" + fieldName + "' is missing or null");
		}
		return node.get(fieldName).asText();
	}

	private String getFieldAsText(JsonNode node, String fieldName) {
		return node.has(fieldName) && !node.get(fieldName).isNull() ? node.get(fieldName).asText() : null;
	}

	private void clearTrackingData(String instalmentReferenceNumber) {
		trackingStartTime.remove(instalmentReferenceNumber);
		trackingDaysMap.remove(instalmentReferenceNumber);
	}

	private ContractType determineContractType(String instalmentReferenceNumber, String contractNumber) {
		try {
			logger.debug("Determining contract type for instalment: {}, contract: {}", instalmentReferenceNumber,
					contractNumber);

			Instalment instalment = instalmentRepository.findByInstalmentReferenceNumber(instalmentReferenceNumber);
			if (instalment != null && instalment.getContract() != null && isArrearContract(instalment.getContract())) {
				return ContractType.ARREAR;
			}

			Optional<Contract> contractOpt = contractRepository.findByContractNumber(contractNumber);
			if (contractOpt.isPresent() && isArrearContract(contractOpt.get())) {
				return ContractType.ARREAR;
			}

			if (contractNumber != null && contractNumber.contains("-ARR-")) {
				return ContractType.ARREAR;
			}

			return ContractType.ORIGINAL;

		} catch (Exception e) {
			logger.error("Error determining contract type for instalment {}: {}", instalmentReferenceNumber,
					e.getMessage(), e);
			return ContractType.ORIGINAL;
		}
	}

	private boolean isArrearContract(Contract contract) {
		if (contract == null) {
			return false;
		}

		return "ARREAR".equalsIgnoreCase(contract.getContractType())
				|| (StringUtils.isNotBlank(contract.getOriginalContractNumber())
				&& !contract.getOriginalContractNumber().equals(contract.getContractNumber()))
				|| (contract.getContractNumber() != null && contract.getContractNumber().contains("-ARR-"));
	}

	private PaymentPreference getCustomerPaymentPreference(String clientNumber) {
		try {
			Optional<ClientAccountDetails> clientOpt = clientAccountDetailsRepository
					.findLatestByClientNumber(clientNumber);

			if (clientOpt.isPresent()) {
				String preference = clientOpt.get().getPaymentPreference();
				logger.debug("Payment preference for client {}: {}", clientNumber, preference);

				if ("DOUBLE_DEBIT_ORDER_ARREAR".equalsIgnoreCase(preference) || "ARREAR".equalsIgnoreCase(preference)) {
					return PaymentPreference.DOUBLE_DEBIT_ORDER_ARREAR;
				}
				if ("EFT".equalsIgnoreCase(preference)) {
					return PaymentPreference.EFT;
				}
			}

			logger.debug("Defaulting to EFT for client: {}", clientNumber);
			return PaymentPreference.EFT;

		} catch (Exception e) {
			logger.error("Error getting payment preference for client {}: {}", clientNumber, e.getMessage(), e);
			return PaymentPreference.EFT;
		}
	}

	private String analyzeResponseCodeGroup(String responseCode, String country) {
		if (StringUtils.isBlank(responseCode)) {
			return "UNKNOWN_GROUP";
		}

		boolean isNamibia = Country.NAMIBIA.equalsIgnoreCase(country);
		boolean isEswatini = Country.ESWATINI.equalsIgnoreCase(country);

		if (isNamibia && ResponseGroups.GROUP1_INSUFFICIENT_FUNDS_NAMIBIA.contains(responseCode)) {
			return "GROUP1_INSUFFICIENT_FUNDS";
		}
		if (isEswatini && ResponseGroups.GROUP1_INSUFFICIENT_FUNDS_ESWATINI.contains(responseCode)) {
			return "GROUP1_INSUFFICIENT_FUNDS";
		}

		if (isNamibia && ResponseGroups.GROUP2_PERMANENT_CLOSURE_NAMIBIA.contains(responseCode)) {
			return "GROUP2_PERMANENT_CLOSURE";
		}
		if (isEswatini && ResponseGroups.GROUP2_PERMANENT_CLOSURE_ESWATINI.contains(responseCode)) {
			return "GROUP2_PERMANENT_CLOSURE";
		}

		if (isNamibia && ResponseGroups.GROUP3_NEW_MANDATE_NAMIBIA.contains(responseCode)) {
			return "GROUP3_NEW_MANDATE";
		}
		if (isEswatini && ResponseGroups.GROUP3_NEW_MANDATE_ESWATINI.contains(responseCode)) {
			return "GROUP3_NEW_MANDATE";
		}

		if (isNamibia && ResponseGroups.GROUP4_DISPUTE_NAMIBIA.contains(responseCode)) {
			return "GROUP4_DISPUTE";
		}
		if (isEswatini && ResponseGroups.GROUP4_DISPUTE_ESWATINI.contains(responseCode)) {
			return "GROUP4_DISPUTE";
		}

		if (isNamibia && ResponseGroups.GROUP5_TECHNICAL_ERRORS_NAMIBIA.contains(responseCode)) {
			return "GROUP5_TECHNICAL_ERRORS";
		}
		if (isEswatini && ResponseGroups.GROUP5_TECHNICAL_ERRORS_ESWATINI.contains(responseCode)) {
			return "GROUP5_TECHNICAL_ERRORS";
		}

		return "UNKNOWN_GROUP";
	}

	private String getResponseCodeGroupDescription(String responseCode, String country, String group) {
		Map<String, String> descriptions = getResponseCodeDescriptions();
		String baseDescription = descriptions.getOrDefault(responseCode, "Unknown code: " + responseCode);
		return String.format("[%s] %s - %s - %s", group, country, responseCode, baseDescription);
	}

	private String getResponseCodeDescription(String responseCode, String country) {
		if (StringUtils.isBlank(responseCode)) {
			return "No response code";
		}
		Map<String, String> descriptions = getResponseCodeDescriptions();
		return descriptions.getOrDefault(responseCode, "Response Code: " + responseCode + " - " + country);
	}

	private boolean hasTwoConsecutiveFailures(String originalContractNumber) {
		try {
			List<Instalment> failedInstallments = instalmentRepository
					.findFailedInstallmentsByOriginalContractAndDirect(originalContractNumber);

			logger.debug("Checking consecutive failures - Total failed installments: {}", failedInstallments.size());

			if (failedInstallments.size() < 2) {
				return false;
			}

			failedInstallments.sort((a, b) -> Long.compare(a.getNoOfInstalment(), b.getNoOfInstalment()));

			for (int i = 0; i < failedInstallments.size() - 1; i++) {
				if (failedInstallments.get(i + 1).getNoOfInstalment() == failedInstallments.get(i).getNoOfInstalment()
						+ 1) {
					logger.info("Consecutive failures found - Installments {} and {} in contract: {}",
							failedInstallments.get(i).getNoOfInstalment(),
							failedInstallments.get(i + 1).getNoOfInstalment(), originalContractNumber);
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			logger.error("Error checking consecutive failures for contract {}: {}", originalContractNumber,
					e.getMessage(), e);
			return false;
		}
	}

	private boolean hasThreeTotalFailures(String originalContractNumber) {
		try {
			List<Instalment> failedInstallments = instalmentRepository
					.findFailedInstallmentsByOriginalContractAndDirect(originalContractNumber);
			boolean hasThree = failedInstallments.size() >= 3;

			if (hasThree) {
				List<Long> failedNumbers = failedInstallments.stream().map(Instalment::getNoOfInstalment).sorted()
						.toList();
				logger.info("Total failures found - Count: {}, Numbers: {} in contract: {}", failedInstallments.size(),
						failedNumbers, originalContractNumber);
			}

			return hasThree;
		} catch (Exception e) {
			logger.error("Error checking total failures for contract {}: {}", originalContractNumber, e.getMessage(),
					e);
			return false;
		}
	}

	private String getOriginalContractNumber(String contractNumber, ContractType contractType) {
		if (contractType == ContractType.ORIGINAL) {
			return contractNumber;
		}

		try {
			Optional<Contract> contractOpt = contractRepository.findByContractNumber(contractNumber);
			if (contractOpt.isPresent() && StringUtils.isNotBlank(contractOpt.get().getOriginalContractNumber())) {
				return contractOpt.get().getOriginalContractNumber();
			}
		} catch (Exception e) {
			logger.error("Error getting original contract number for {}: {}", contractNumber, e.getMessage(), e);
		}

		return contractNumber;
	}

	private void processCompleteDispute(JsonNode instalmentNode, String responseCode, String clientNumber,
	                                    BigDecimal amount, String disputeType, String contractNumber) {
		try {
			String instalmentRef = getRequiredField(instalmentNode, "InstalmentReferenceNumber");

			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalmentNode, Status.DISPUTED, disputeType, responseCode,
					"Disputed - " + disputeType + ": " + getResponseCodeDescription(responseCode, "Unknown"));

			processPaymentReversal(instalmentRef, clientNumber, amount, disputeType + "_PAYMENT");
			cancelContractsWithAPI(contractNumber, disputeType + "_PAYMENT");

			logger.warn("Dispute processed - Type: {}, Payment reversed, future EMIs cancelled", disputeType);
		} catch (Exception e) {
			logger.error("Error processing dispute: {}", e.getMessage(), e);
		}
	}

	private void processPaymentReversal(String instalmentRef, String clientNumber, BigDecimal amount, String reason) {
		try {
			logger.debug("Processing payment reversal - Amount: {}", amount);

			List<PaymentDetail> paymentDetails = paymentDetailRepo.findByInstalmentReferenceNumber(instalmentRef);

			if (paymentDetails.isEmpty()) {
				logger.warn("Payment detail not found for reversal - Ref: {} - MANUAL_REVIEW_REQUIRED", instalmentRef);
				return;
			}

			for (PaymentDetail paymentDetail : paymentDetails) {
				paymentDetail.setPaymentStatus("REVERSED");
				paymentDetail.setUpdatedDate(new Date());
				paymentDetail.setUpdatedBy("Realpay System");
				paymentDetailRepo.save(paymentDetail);
			}

			logger.info("Payment reversal completed for instalment: {}", instalmentRef);
		} catch (Exception e) {
			logger.error("Error processing payment reversal: {}", e.getMessage(), e);
		}
	}

	private boolean isRealPayAPIUpdateSuccessful(JsonNode response) {
		try {
			if (response == null) {
				return false;
			}

			if (response.has("APIResponse")) {
				JsonNode apiResponse = response.get("APIResponse");
				if (apiResponse.has("Status")) {
					return "SUCCESS".equals(apiResponse.get("Status").asText());
				}
			}

			return false;
		} catch (Exception e) {
			logger.error("Error checking API update success: {}", e.getMessage(), e);
			return false;
		}
	}

	public String determineProductFromContract(String quoteNo) {
		try {
			Optional<ClientAccountDetails> clientAccountDetails = clientAccountDetailsRepository.findByQuoteNo(quoteNo);
			return clientAccountDetailsService.getProviderByCompanyId(clientAccountDetails.get().getCompanyId());
		} catch (Exception e) {
			e.printStackTrace();
			return "FNBENDO";
		}
	}

	@Transactional
	protected void processSuccessfulInstalment(JsonNode instalment, String responseCode, String token, String isFirstInstalmentPaid) {
		try {
			String clientNumber = getRequiredField(instalment, "ClientNumber");
			String contractNumber = getRequiredField(instalment, "ContractNumber");
			String instalmentReferenceNumber = getRequiredField(instalment, "InstalmentReferenceNumber");
			String currentInstallment = getRequiredField(instalment, "InstalmentSequence");

			logger.info("Processing successful instalment: {}", instalmentReferenceNumber);

			Instalment instalmentEntity = instalmentRepository
					.findByInstalmentReferenceNumber(instalmentReferenceNumber);

			Contract contract = instalmentEntity.getContract();

			Optional<ClientAccountDetails> clientAccountOpt = clientAccountDetailsRepository
					.findLatestByClientNumber(clientNumber);
			if (!clientAccountOpt.isPresent()) {
				logger.warn("Client account not found: {}", clientNumber);
				return;
			}

			ClientAccountDetails clientAccount = clientAccountOpt.get();
			String quoteNo = contract.getQuoteNo();
			HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(quoteNo);

			if (StringUtils.isBlank(quoteNo)) {
				logger.warn("Quote number not found for client: {}", clientNumber);
				return;
			}

			// PASSED FULL JSONNODE
			updateInstalmentStatus(instalment, contract.getContractType().equals("ORIGINAL") ? Status.SUCCESSFUL :
					Status.DOUBLE_DEBIT_SUCCESS, "SUCCESS", responseCode, "Payment successfully processed");

			/// isFirstInstalmentPaid.equals("YES") -> Scenario for RealPay First Instalment Paid On Spot
			/// will be handled in UI...
			if (instalmentEntity != null && isFirstInstalmentPaid!=null &&
					!isFirstInstalmentPaid.equals("YES")) {

				/// for updating in we core tables - payment info, emi transaction, payment
				/// detail ...

				String paymentId = creatingRecordsInWeCoreSystems(instalmentEntity, contract, clientAccount,
						currentInstallment);

				/// Changing the status to PAID / ACCEPTED in 3 we core tables ...
				updatePaymentStatus(quoteNo, paymentId, instalmentReferenceNumber);
				updateSuccessInEmiTransactionTable(quoteNo, clientAccount, instalmentEntity, paymentId);

				/// for saving paymentId in installment table for current installment ...
				Instalment instalmentRec = instalmentRepository
						.findByInstalmentReferenceNumber(instalmentReferenceNumber);
				instalmentRec.setPaymentId(paymentId);
				instalmentRepository.save(instalmentRec);


				boolean isOriginalContract = false;
				boolean isFirstInstallment = false;

				if (contract != null) {
					String contractType = contract.getContractType();
					isOriginalContract = "ORIGINAL".equalsIgnoreCase(contractType) || StringUtils.isBlank(contractType)
							|| contract.getOriginalContractNumber() == null
							|| contract.getOriginalContractNumber().equals(contract.getContractNumber());

					logger.debug("Contract type check - Contract: {}, Type: {}, IsOriginal: {}", contractNumber,
							contractType, isOriginalContract);
				}

				Long instalmentSequence = instalmentEntity.getNoOfInstalment();
				isFirstInstallment = instalmentSequence != null && instalmentSequence == 1L;

				logger.debug("Installment sequence check - Ref: {}, Sequence: {}, IsFirst: {}",
						instalmentReferenceNumber, instalmentSequence, isFirstInstallment);


				if (isOriginalContract && isFirstInstallment && !isFirstInstalmentPaid.equals("YES")) {
					logger.info("Policy generation criteria met - Contract: ORIGINAL, Installment: 1, Quote: {}",
							quoteNo);

					Optional<List<PaymentDetail>> paymentDetailOpt = paymentDetailRepo
							.findLatestByQuoteNoAndPaymentId(quoteNo, paymentId);

					if (paymentDetailOpt.isPresent()) {

						/// Need to un commenet this method for using scheduler ...
//						if (!isAlreadyProcessedByWebhook(instalmentReferenceNumber)) {
//							logger.info("Generating policy - Not processed by webhook: {}", instalmentReferenceNumber);
//							generatePolicyForPaymentWithNewTransaction(paymentDetailOpt.get().get(0), token);
//
//							/// if re-insurance exist, update call ...
//							checkAndUpdateForReInsurance(quoteNo);
//						} else {
//							logger.info("Policy generation skipped - Already processed by webhook: {}",
//									instalmentReferenceNumber);
//						}

						/// Need to commend this if you are running an scheduler...
						logger.info("Generating policy - Not processed by webhook: {}", instalmentReferenceNumber);
						if (homePositionMaster.getPolicyNo() == null || homePositionMaster.getPolicyNo().isEmpty()) {
							logger.info("Going for calling generate Policy ...");
							generatePolicyForPaymentWithNewTransaction(paymentDetailOpt.get().get(0), token);
						}else{
							logger.warn("Policy number is not null so skipping policy generation {}",homePositionMaster.getPolicyNo());
						}

						/// if re-insurance exist, update call ...
						checkAndUpdateForReInsurance(quoteNo);
					} else {
						logger.warn("PaymentDetail not found for policy generation - Quote: {}, PaymentId: {}", quoteNo,
								paymentId);

					}
				} else {
					logger.info("Policy generation skipped - Contract: {}, Installment: {}, Quote: {}, First Instalment Paid on Spot: {}",
							isOriginalContract ? "ORIGINAL" : "ARREAR", instalmentSequence, quoteNo,isFirstInstalmentPaid);
				}

			} else {
				if(instalmentEntity==null)logger.warn("Installment entity not found - Cannot verify contract type and sequence");
				else logger.info("Wecore table inserts skipped because its first installment paid on spot for {} and isPaid {}",instalmentEntity.getQuoteNo(),isFirstInstalmentPaid);
			}

		} catch (Exception e) {
			logger.error("Error processing successful instalment: {}", e.getMessage(), e);
		}
	}

	/// for updating sucess in EMI transactionTable ...

	private void updateSuccessInEmiTransactionTable(String quoteNo, ClientAccountDetails clientAccount,
	                                                Instalment instalmentEntity, String paymentId) {
		Integer result = null;
		HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(quoteNo);
		String refShortCode = getListItem(clientAccount.getCompanyId(), homePositionMaster.getBranchCode(),
				"PAYMENT_REF_SHORTCODE", "1");
		PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(instalmentEntity.getQuoteNo(), paymentId);
		String refno = refShortCode + generateMerchantReferenceNo();

		if (paymentInfo.getEmiYn().equalsIgnoreCase("Y")) {
			List<EmiTransactionDetails> emiDetails = emiTransactionDetailsRepository
					.findByQuoteNoOrderByBalanceAmountDesc(instalmentEntity.getQuoteNo());
			if (!emiDetails.isEmpty()) {
				result = calculateInstallments(emiDetails);
			}

			if (result != null && !(result < 0)) {
				EmiTransactionDetails saveDate = new EmiTransactionDetails();
				final String finalResult = String.valueOf(result);
				Optional<EmiTransactionDetails> first = emiDetails.stream()
						.filter(a -> a.getInstalment().equalsIgnoreCase(String.valueOf(finalResult))
								&& a.getQuoteNo().equalsIgnoreCase(instalmentEntity.getQuoteNo()))
						.findFirst();
				EmiTransactionDetails data1 = first.get();
				DozerBeanMapper dozermapper = new DozerBeanMapper();
				saveDate = dozermapper.map(data1, EmiTransactionDetails.class);
				saveDate.setPaymentStatus("Paid");
				saveDate.setPaymentDetails("RealPay");
//				saveDate.setStatus("P");
				saveDate.setPaymentDate(new Date());
				saveDate.setPaymentId(paymentId);
				saveDate.setMerchantReference(refno);
				emiTransactionDetailsRepository.saveAndFlush(saveDate);
			}
		}
	}

	public synchronized String generateMerchantReferenceNo() {
		try {
			PaymentRefno entity;
			entity = seqRefNorepo.save(new PaymentRefno());
			return String.format("%05d", entity.getPaymentReferenceNo());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception is ---> " + e.getMessage());
			return null;
		}

	}

	public Integer calculateInstallments(List<EmiTransactionDetails> emiDetails) {
		Integer numberOfInstallments = 0;
		Optional<EmiTransactionDetails> first = emiDetails.stream()
				.filter(a -> !"Paid".equalsIgnoreCase(a.getPaymentStatus())).findFirst();
		numberOfInstallments = Integer.parseInt(first.get().getInstalment());
		return numberOfInstallments;
	}

	private String creatingRecordsInWeCoreSystems(Instalment instalmentEntity, Contract contract,
	                                              ClientAccountDetails clientAccount, String currentInstallment) {

		/// for calling makePayment

		MakePaymentSaveReq makePaymentSaveReq = constructMakePaymentReq(instalmentEntity, contract,
				clientAccount.getCompanyId(), currentInstallment);
		MakePaymentRes makePaymentRes = paymentService.savemakepayment(makePaymentSaveReq);
		logger.info("Updated in PaymentInfo table.");

		if (!makePaymentRes.getPaymentId().isEmpty()) {

			List<PaymentDetail> paymentDetails =  paymentDetailRepo.findByQuoteNoAndPaymentId(makePaymentRes.getQuoteNo(),
					makePaymentRes.getPaymentId());
			if(paymentDetails.isEmpty()){
				PaymentDetailsSaveReq paymentDetailSaveReq = constructInsertPaymentReq(instalmentEntity,
						clientAccount.getCompanyId(), makePaymentRes.getPaymentId());
				PaymentDetailsSaveRes paymentDetailSaveRes = paymentService.savePaymentDetails(paymentDetailSaveReq, "");
				logger.info("Inserted in Payment Detail table. Res - " + paymentDetailSaveRes.toString());
			}
			/// for updating in EMI transaction table...

			List<EmiTransactionDetailsUpdateReq> reqList = new ArrayList<>();
			reqList.add(constructUpdateEmiTransactionReq(instalmentEntity, contract, clientAccount.getCompanyId(),
					currentInstallment));
			SuccessRes res = emiTransactionService.updateEmiTransactionDetails(reqList);
			logger.info("Updated in EMI Transaction Table. Res -> " + res.toString());

		}

		return makePaymentRes.getPaymentId();
	}

	private EmiTransactionDetailsUpdateReq constructUpdateEmiTransactionReq(Instalment instalmentEntity,
	                                                                        Contract contract, String companyId, String currentInstallment) {
		return EmiTransactionDetailsUpdateReq.builder().quoteNo(instalmentEntity.getQuoteNo())
				.noOfInstallment(currentInstallment).companyId(companyId).productId(instalmentEntity.getProductId())
				.installmentPeriod(contract.getNumberOfInstalments().toString()).createdBy("RealPay").paymentStatus("Paid")
				.remarks("").paymentDetails("RealPay").selectedYn("Y").build();
	}

	private MakePaymentSaveReq constructMakePaymentReq(Instalment instalmentEntity, Contract contract, String companyId,
	                                                   String currentInstallment) {
		return MakePaymentSaveReq.builder().createdBy("RealPay").insuranceId(companyId).emiYn("Y")
				.premium(String.valueOf(instalmentEntity.getInstalmentAmount())).quoteNo(instalmentEntity.getQuoteNo())
				.subUserType("both").userType("Issuer").remarks("None").installmentMonth(currentInstallment)
				.installmentPeriod(String.valueOf(contract.getNumberOfInstalments())).build();
	}

	private PaymentDetailsSaveReq constructInsertPaymentReq(Instalment instalmentEntity, String companyId,
	                                                        String paymentId) {
		return PaymentDetailsSaveReq.builder().createdBy("RealPay").insuranceId(companyId).emiYn("Y")
				.premium(instalmentEntity.getInstalmentAmount()).quoteNo(instalmentEntity.getQuoteNo())
				.payeeName("RealPay").subUserType("both").userType("Issuer").paymentId(paymentId).paymentType("7")
				.build();
	}

	@Transactional
	protected void updatePaymentStatus(String quoteNo, String paymentId, String instalmentReferenceNumber) {
		try {
			logger.info("Updating payment status to Accepted for quote: {}, paymentId: {}", quoteNo, paymentId);

			PaymentInfo paymentInfo = paymentInfoRepo.findByQuoteNoAndPaymentId(quoteNo, paymentId);
			if (paymentInfo != null) {
				paymentInfo.setPaymentStatus("ACCEPTED");
				paymentInfo.setUpdatedDate(new Date());
				paymentInfo.setUpdatedBy("SYSTEM");
				paymentInfoRepo.save(paymentInfo);
				logger.info("payment_info updated successfully - Quote: {}, PaymentId: {}", quoteNo, paymentId);
			} else {
				logger.warn("payment_info not found for Quote: {}, PaymentId: {}", quoteNo, paymentId);
			}

			List<PaymentDetail> paymentDetails = paymentDetailRepo.findByQuoteNoAndPaymentId(quoteNo, paymentId);
			if (!paymentDetails.isEmpty()) {
				for (PaymentDetail paymentDetail : paymentDetails) {
					paymentDetail.setPaymentStatus("ACCEPTED");
					paymentDetail.setUpdatedDate(new Date());
					paymentDetail.setUpdatedBy("SYSTEM");
					if (StringUtils.isBlank(paymentDetail.getInstalmentReferenceNumber())) {
						paymentDetail.setInstalmentReferenceNumber(instalmentReferenceNumber);
					}
					paymentDetailRepo.save(paymentDetail);
				}
				logger.info("payment_detail updated successfully - Quote: {}, PaymentId: {}, Records: {}", quoteNo,
						paymentId, paymentDetails.size());
			} else {
				logger.warn("payment_detail not found for Quote: {}, PaymentId: {}", quoteNo, paymentId);
			}

		} catch (Exception e) {
			logger.error("Error updating payment status for Quote: {}, PaymentId: {} - Error: {}", quoteNo, paymentId,
					e.getMessage(), e);
		}
	}

	@Transactional
	protected void updateEmiTransactionDetails(String quoteNo, String paymentId, JsonNode instalmentData) {
		try {
			EmiTransactionDetails emiDetail = emiTransactionDetailsRepository.findByQuoteNoAndPaymentId(quoteNo,
					paymentId);
			if (emiDetail != null) {
				emiDetail.setPaymentStatus("Paid");
				emiDetail.setPaymentDate(new Date());
				if (instalmentData.has("InstalmentReferenceNumber")) {
					String instalmentRef = instalmentData.get("InstalmentReferenceNumber").asText();
					emiDetail.setInstalmentReferenceNumber(instalmentRef);
				}
				emiTransactionDetailsRepository.save(emiDetail);
				logger.info("EMI transaction details updated - Quote: {}, PaymentId: {}", quoteNo, paymentId);
			} else {
				logger.warn("EMI transaction details not found - Quote: {}, PaymentId: {}", quoteNo, paymentId);
			}
		} catch (Exception e) {
			logger.error("Error updating EMI transaction details: {}", e.getMessage(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void generatePolicyForPaymentWithNewTransaction(PaymentDetail paymentDetail, String token) {
		try {
			PaymentInfo paymentInfo = paymentInfoRepo.findByQuoteNoAndPaymentId(paymentDetail.getQuoteNo(),
					paymentDetail.getPaymentId());

			/// checking weather instalment No is 1 if not set to 1. If not It will not generate policy...
			HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(paymentDetail.getQuoteNo());
			if(!homePositionMaster.getNoOfInstallment().equals("1")){
				homePositionMaster.setNoOfInstallment("1");
				homerepo.save(homePositionMaster);
			}
			if (paymentInfo != null) {
				PaymentDetailsSaveReq req = new PaymentDetailsSaveReq();
				req.setQuoteNo(paymentDetail.getQuoteNo());
				req.setCreatedBy(paymentDetail.getUpdatedBy());
				req.setPaymentType(paymentDetail.getPaymentType());

				logger.info("Generating policy for payment - Quote: {}", paymentDetail.getQuoteNo());
				List<PolicyDrcrDetail> resp = paymentService.generatePolicy(paymentInfo, req, paymentDetail, token);
				logger.info("Generate Policy resp : {}",resp);
			}
		} catch (Exception e) {
			logger.error("Error generating policy - Quote: {}, Error: {}", paymentDetail.getQuoteNo(), e.getMessage(),
					e);
			logger.error("Manual policy generation required - Quote: {}", paymentDetail.getQuoteNo());
		}
	}

	/// for generating policy...
	public void generatePolicy(String quoteNo, String paymentId){

		try {
			PaymentDetail paymentDetail = paymentDetailRepo.findByPaymentId(paymentId);//paymentId
			PaymentInfo paymentInfo = paymentInfoRepo.findByQuoteNoAndPaymentId(paymentDetail.getQuoteNo(),
					paymentDetail.getPaymentId());

			if (paymentInfo != null) {
				PaymentDetailsSaveReq req = new PaymentDetailsSaveReq();
				req.setQuoteNo(paymentDetail.getQuoteNo());
				req.setCreatedBy(paymentDetail.getUpdatedBy());
				req.setPaymentType(paymentDetail.getPaymentType());

				logger.info("Generating policy for payment - Quote: {}", paymentDetail.getQuoteNo());
				List<PolicyDrcrDetail> resp = paymentService.generatePolicy(paymentInfo, req, paymentDetail, tokenGenerator.getToken());
				logger.info("Generate Policy resp : {}",resp);
			}
		} catch (Exception e) {
			logger.error("Error generating policy - Quote: {}, Error: {}", quoteNo, e.getMessage(),
					e);
			logger.error("Manual policy generation required - Quote: {}", quoteNo);
		}
	}

	private String getCountryFromProduct(String urlProduct) {
		if (urlProduct == null) {
			logger.warn("Null URL product - defaulting to Namibia");
			return Country.NAMIBIA;
		}

		String upperProduct = urlProduct.toUpperCase();
		if (upperProduct.contains("FNBENDO") || upperProduct.contains("FNBENDPAY")
				|| upperProduct.contains("FNBENDOSD")) {
			return Country.NAMIBIA;
		} else if (upperProduct.contains("REALTIME") || upperProduct.contains("FNBNDOSW")) {
			return Country.ESWATINI;
		} else {
			return Country.NAMIBIA;
		}
	}

	private String getComplianceProduct(String urlProduct) {
		if (urlProduct == null) {
			logger.warn("Null URL product - defaulting to NAMPAY");
			return Product.NAMPAY;
		}

		String upperProduct = urlProduct.toUpperCase();
		if (upperProduct.contains("FNBENDO") || upperProduct.contains("FNBENDPAY")
				|| upperProduct.contains("FNBENDOSD")) {
			return Product.NAMPAY;
		} else if (upperProduct.contains("REALTIME") || upperProduct.contains("FNBNDOSW")) {
			return Product.FNB;
		} else {
			return Product.NAMPAY;
		}
	}

	/**
	 * UPDATED: Now handles UPSERT (Update if exists, Insert if new). Accepts
	 * JsonNode to populate fields for new records.
	 */
	@Transactional
	protected void updateInstalmentStatus(JsonNode instalmentNode, String newStatus, String statusReason,
	                                      String responseCode, String message) {

		String instalmentReferenceNumber = "UNKNOWN";
		try {
			if (instalmentNode == null) {
				logger.error("Cannot update instalment status: JsonNode is null");
				return;
			}

			if (instalmentNode.has("InstalmentReferenceNumber")) {
				instalmentReferenceNumber = instalmentNode.get("InstalmentReferenceNumber").asText();
			} else {
				logger.error("Missing InstalmentReferenceNumber in update request");
				return;
			}

			Instalment instalment = instalmentRepository.findByInstalmentReferenceNumber(instalmentReferenceNumber);

			if (instalment == null) {
				logger.info("Instalment not found. Creating NEW record for: {}", instalmentReferenceNumber);
				instalment = new Instalment();
				instalment.setInstalmentReferenceNumber(instalmentReferenceNumber);
				instalment.setCreatedDate(LocalDateTime.now());

				// --- Map Fields for NEW Record ---
				if (instalmentNode.has("ContractNumber")) {
					String contractNo = instalmentNode.get("ContractNumber").asText();
					instalment.setContractNumber(contractNo);

					// Attempt to link contract
					Optional<Contract> contract = contractRepository.findByContractNumber(contractNo);
					contract.ifPresent(instalment::setContract);
				}

				if (instalmentNode.has("ClientNumber")) {
					String clientNo = instalmentNode.get("ClientNumber").asText();
					instalment.setClientNumber(clientNo);

					// Attempt to link client account
					Optional<ClientAccountDetails> clientAccount = clientAccountDetailsRepository
							.findLatestByClientNumber(clientNo);
					clientAccount.ifPresent(instalment::setClientAccount);

					// If Contract not found above, try to link via Contract + Client if logical...
					// (Simplified linking logic here)
				}

				if (instalmentNode.has("InstalmentAmount") && !instalmentNode.get("InstalmentAmount").isNull()) {
					instalment.setInstalmentAmount(new BigDecimal(instalmentNode.get("InstalmentAmount").asText()));
				}

				if (instalmentNode.has("InstalmentSequence") && !instalmentNode.get("InstalmentSequence").isNull()) {
					instalment.setNoOfInstalment(instalmentNode.get("InstalmentSequence").asLong());
				}

				if (instalmentNode.has("InstalmentActionDate")
						&& !instalmentNode.get("InstalmentActionDate").isNull()) {
					String dateStr = instalmentNode.get("InstalmentActionDate").asText();
					try {
						// Expected format: "2026-01-20 00:00"
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						Date actionDate = sdf.parse(dateStr);
						instalment.setInstalmentActionDate(actionDate);
					} catch (Exception e) {
						logger.warn("Date parsing failed for new instalment {}: {}", instalmentReferenceNumber,
								e.getMessage());
						// Fallback or leave null
						instalment.setInstalmentActionDate(new Date());
					}
				}
			}

			String paymentStatus = (instalment.getIsPaidCash() != null && instalment.getIsPaidCash().equals("YES"))
					? "CASH"
					: newStatus;

			// --- Common Updates (Existing OR New) ---
			instalment.setInstalmentStatus(paymentStatus);
			instalment.setStatus(statusReason);
			instalment.setResponseCode(responseCode);
			instalment.setMessage(message);
			instalment.setLastUpdateDate(new Date());
			instalment.setUpdatedDate(LocalDateTime.now());

			instalmentRepository.save(instalment);

			/// updating the values in the original instalment for future referece...
			if(newStatus.equals("DOUBLE_DEBIT_SUCCESS")){
				Instalment instalmentOriginal = instalmentRepository.findByQuoteNoAndNoOfInstalmentAndContractSequenceIsNotNull(instalment.getQuoteNo(), Math.toIntExact(instalment.getArrearMonth()));
				if(instalmentOriginal!=null){
					instalmentOriginal.setInstalmentStatus("DOUBLE_DEBIT_SUCCESS");
					instalmentOriginal.setResponseCode("00");
					instalmentOriginal.setLastUpdateDate(new Date());
					instalmentOriginal.setStatus("Original Failed But Success Via Double Debit");
					instalmentRepository.save(instalmentOriginal);
					logger.info("Updated in Original Instalment for {} - Arrear Month {}",instalment.getQuoteNo(),instalment.getArrearMonth());
				}else{
					logger.warn("Original Instalment Not found for {} - Arrear Month {}",instalment.getQuoteNo(),instalment.getArrearMonth());
				}
			}

			logger.info("Successfully Saved/Updated instalment - Ref: {}, Status: {}, Reason: {}",
					instalmentReferenceNumber, newStatus, statusReason);

		} catch (Exception e) {
			logger.error("Error updating instalment status for {}: {}", instalmentReferenceNumber, e.getMessage(), e);
		}
	}

	private void setValidityDate(PaymentDetail paymentDetail, HomePositionMaster data) {
		try {
			String hourStr = getListItem(data.getCompanyId(), data.getBranchCode(), "PAYMENT_VALIDATE_HOUR");
			Integer validateHour = StringUtils.isNotBlank(hourStr) ? Integer.valueOf(hourStr) : 0;
			String minStr = getListItem(data.getCompanyId(), data.getBranchCode(), "PAYMENT_VALIDATE_MINUTES");
			Integer validateMinutes = StringUtils.isNotBlank(minStr) ? Integer.valueOf(minStr) : 0;

			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, validateHour);
			cal.set(Calendar.MINUTE, validateMinutes);
			Date validateDate = cal.getTime();

			paymentDetail.setValidityDate(validateDate);
		} catch (Exception e) {
			logger.error("Error setting validity date: {}", e.getMessage(), e);
			paymentDetail.setValidityDate(new Date());
		}
	}

	private String getTinyUrl(String quoteNo) {
		return "";
	}

	private String getBankName(Integer bankCode) {
		if (bankCode == null) {
			return "";
		}
		return "";
	}

	private String getInscompanyMasterDropdown(String companyId) {
		return "";
	}

	private String getCompanyBranchMasterDropdown(String companyId, String branchCode) {
		return "";
	}

	public synchronized String getListItem(String insuranceId, String branchCode, String itemType, String itemCode) {
		String itemDesc = "";
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			Root<ListItemValue> c = query.from(ListItemValue.class);

			query.select(c);
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			effectiveDate.where(a1, a2, b1, b2);

			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a5 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate a6 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			effectiveDate2.where(a3, a4, a5, a6);

			Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n3 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n4 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(c.get("status"), "Y");
			Predicate n6 = cb.equal(c.get("itemType"), itemType);
			Predicate n7 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n1, n2, n3, n4, n5, n6, n7).orderBy(orderList);

			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
		} catch (Exception e) {
			logger.error("Error in get list item: {}", e.getMessage(), e);
		}
		return itemDesc;
	}

	public synchronized String getListItem(String insuranceId, String branchCode, String itemType) {
		String itemDesc = "";
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			Root<ListItemValue> c = query.from(ListItemValue.class);

			query.select(c);
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			effectiveDate.where(a1, a2, b1, b2);

			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a5 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate a6 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			effectiveDate2.where(a3, a4, a5, a6);

			Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n3 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n4 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(c.get("status"), "Y");
			Predicate n6 = cb.equal(c.get("itemType"), itemType);
			query.where(n1, n2, n3, n4, n5, n6).orderBy(orderList);

			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
		} catch (Exception e) {
			logger.error("Error in get list item: {}", e.getMessage(), e);
		}
		return itemDesc;
	}

	private Map<String, String> getResponseCodeDescriptions() {
		Map<String, String> descriptions = new HashMap<>();

		descriptions.put("TR76", "Insufficient funds");
		descriptions.put("00002", "Insufficient funds");
		descriptions.put("AM04", "Insufficient funds");
		descriptions.put("RR10", "Insufficient funds");
		descriptions.put("02", "Insufficient funds");
		descriptions.put("MD07", "Account closed");
		descriptions.put("UN26", "Account closed");
		descriptions.put("00012", "Invalid account");
		descriptions.put("AC05", "Closed account");
		descriptions.put("AC06", "Blocked account");
		descriptions.put("UN03", "Invalid account");
		descriptions.put("UN06", "Blocked account");
		descriptions.put("UN08", "Closed account");
		descriptions.put("UN10", "Invalid account");
		descriptions.put("RR15", "Account closed");
		descriptions.put("RR06", "Account blocked");
		descriptions.put("45", "Account closed");
		descriptions.put("12", "Invalid account");
		descriptions.put("26", "Invalid account");
		descriptions.put("06", "Account error");
		descriptions.put("50", "Account does not exist");
		descriptions.put("NA04", "Invalid mandate");
		descriptions.put("NA28", "Mandate expired");
		descriptions.put("NA30", "Mandate suspended");
		descriptions.put("NA32", "Mandate cancelled");
		descriptions.put("NA34", "Mandate not found");
		descriptions.put("NA36", "Mandate dispute");
		descriptions.put("MD05", "Mandate cancelled");
		descriptions.put("RR16", "Payment disputed by client");
		descriptions.put("AC02", "Invalid debtor account");
		descriptions.put("AC03", "Invalid creditor account");
		descriptions.put("AC08", "Account type invalid");
		descriptions.put("AC12", "Invalid account number");
		descriptions.put("AC13", "Invalid account type");
		descriptions.put("AG08", "Agent suspended");
		descriptions.put("AM09", "Wrong amount");
		descriptions.put("AM10", "Invalid amount");
		descriptions.put("AM11", "Invalid currency");
		descriptions.put("AM12", "Invalid transaction amount");
		descriptions.put("AM13", "Amount exceeded");
		descriptions.put("AM14", "Amount below minimum");
		descriptions.put("AM16", "Invalid exchange rate");
		descriptions.put("AM19", "Amount mismatch");
		descriptions.put("BE08", "Invalid execution date");
		descriptions.put("BE09", "Missing execution date");
		descriptions.put("BE10", "Invalid due date");
		descriptions.put("BE11", "Invalid processing date");
		descriptions.put("BE18", "Invalid service level");
		descriptions.put("BE22", "Invalid frequency");
		descriptions.put("CH04", "Insufficient channel capacity");
		descriptions.put("CL03", "Clearing timeout");
		descriptions.put("CURR", "Currency error");
		descriptions.put("DT01", "Invalid date");
		descriptions.put("DT03", "Invalid cut-off date");
		descriptions.put("DU01", "Duplicate payment");
		descriptions.put("DU03", "Duplicate reference");
		descriptions.put("DU04", "Duplicate transaction");
		descriptions.put("ED06", "Return of debit transfer");
		descriptions.put("FF01", "Invalid file format");
		descriptions.put("FF04", "Invalid characters");
		descriptions.put("FF05", "Invalid field length");
		descriptions.put("FF06", "Missing field");
		descriptions.put("FF08", "Invalid structure");
		descriptions.put("FF10", "Invalid XML");
		descriptions.put("RC06", "Returned by customer");
		descriptions.put("RC07", "Creditor bank not reachable");
		descriptions.put("RR02", "Regulatory reason");
		descriptions.put("RR07", "Missing regulatory information");
		descriptions.put("RR09", "Regulatory suspension");
		descriptions.put("SL13", "Service level not supported");
		descriptions.put("TR25", "Transaction not supported");
		descriptions.put("TR26", "Invalid message type");
		descriptions.put("TR27", "Transaction timeout");
		descriptions.put("TR28", "Invalid transaction");
		descriptions.put("TR29", "Transaction rejected");
		descriptions.put("TR30", "Bank processing error");
		descriptions.put("TR31", "System malfunction");
		descriptions.put("TR32", "Technical error");
		descriptions.put("TR33", "Communication error");
		descriptions.put("TR34", "Network error");
		descriptions.put("TR35", "Processing timeout");
		descriptions.put("TR36", "Service unavailable");
		descriptions.put("TR37", "System busy");
		descriptions.put("TR51", "Validation error");
		descriptions.put("TR52", "Authentication error");
		descriptions.put("TR64", "Settlement failure");
		descriptions.put("TR66", "Clearing failure");
		descriptions.put("TR74", "Batch processing error");
		descriptions.put("TR75", "Queue error");
		descriptions.put("ACTC", "Transaction accepted");
		descriptions.put("ACSC", "Transaction accepted with change");
		descriptions.put("ACSP", "Transaction accepted, settlement pending");
		descriptions.put("ACWC", "Transaction accepted with conditions");
		descriptions.put("ACCP", "Transaction accepted");
		descriptions.put("PDNG", "Transaction pending");
		descriptions.put("PART", "Partially accepted");
		descriptions.put("RJCT", "Transaction rejected");
		descriptions.put("MD01", "No mandate");
		descriptions.put("MD02", "Missing mandate information");
		descriptions.put("39", "Invalid account");
		descriptions.put("72", "Account error");
		descriptions.put("04", "Capture card");
		descriptions.put("6", "Generic error");
		descriptions.put("34", "Suspected fraud");
		descriptions.put("36", "Restricted card");
		descriptions.put("41", "Lost card");
		descriptions.put("44", "No investment account");
		descriptions.put("46", "Allowable number of PIN exceeded");
		descriptions.put("47", "Duplicate transmission detected");
		descriptions.put("48", "Control field error");
		descriptions.put("55", "Incorrect PIN");
		descriptions.put("60", "Card acceptor contact acquirer");
		descriptions.put("61", "Exceeds withdrawal amount limit");
		descriptions.put("95", "Reconciliation cutover in process");
		descriptions.put("64", "Original amount incorrect");
		descriptions.put("65", "Exceeds withdrawal frequency limit");
		descriptions.put("67", "Hard capture");
		descriptions.put("71", "Function not supported");
		descriptions.put("38", "Allowable PIN tries exceeded");
		descriptions.put("40", "Requested function not supported");
		descriptions.put("42", "No universal account");
		descriptions.put("43", "Stolen card");
		descriptions.put("49", "Suspected manipulation of card number");
		descriptions.put("54", "Expired card");
		descriptions.put("57", "Transaction not permitted to cardholder");
		descriptions.put("58", "Transaction not permitted to terminal");
		descriptions.put("59", "Suspected fraud");
		descriptions.put("62", "Restricted card");
		descriptions.put("68", "Response received too late");
		descriptions.put("70", "Contact card issuer");
		descriptions.put("30", "Format error");
		descriptions.put("31", "Bank not supported by switch");
		descriptions.put("33", "Expired card");
		descriptions.put("74", "PIN validation not possible");
		descriptions.put("75", "Allowable number of PIN tries exceeded");
		descriptions.put("76", "Invalid/nonexistent 'To Account'");
		descriptions.put("77", "Invalid/nonexistent 'From Account'");

		return descriptions;
	}

	public static class ValidationException extends RuntimeException {
		public ValidationException(String message) {
			super(message);
		}
	}

	/// for updating EFT SUCCESS in installment, PaymentInfo, PaymentDetail, EMI
	/// transaction ...
	public ResponseEntity<?> handleEFTSuccess(String quoteNo, String companyId, String currentInstallment, BigDecimal amount) {
		CommonRes commonRes = new CommonRes();
		try {

			Instalment instalmentEntity = instalmentRepository
					.findByQuoteNoAndNoOfInstalmentAndContractSequenceIsNotNull(quoteNo, Integer.valueOf(currentInstallment));

			Optional<ClientAccountDetails> clientAccountDetails = clientAccountDetailsRepository.findByQuoteNo(quoteNo);

			if(instalmentEntity.getNoOfInstalment()==1L){
				logger.info("Going to call push integration and receipt generation because its 1st instalment");
				ObjectNode node = InstalmentJsonBuilder.buildInstalmentNode(instalmentEntity);
				/// calling to update in the all table saying amount is received ...
				InstalmentStatusService.StatusProcessingResult statusProcessingResult = processSuccessfulStatus(node, "00", getAuthToken(), "NO");
				logger.info("Updated in process Successful instalment Flow ... Res {}",statusProcessingResult);
			}
			/// to call premia push integration and receipt generation...
			premiaPushIntegration.callPremiaPushIntegration(instalmentEntity.getInstalmentReferenceNumber(),
					true);

			EmiTransactionDetails emiTransactionDetails = emiTransactionDetailsRepository.findByQuoteNoAndInstalmentAndCompanyIdAndProductId(
					quoteNo, currentInstallment, clientAccountDetails.get().getCompanyId(), instalmentEntity.getProductId()
			);
			emiTransactionDetails.setPaymentStatus("Paid");
//			emiTransactionDetails.setStatus("P");
			emiTransactionDetailsRepository.save(emiTransactionDetails);


			/// for saving paymentId in installment table for current installment ...
			updateEFTSuccessInInstallment(emiTransactionDetails.getPaymentId(), instalmentEntity.getInstalmentReferenceNumber(), amount);

			/// for updating paid in remainder table ...
			realpayInstallmentReminderService.markAsPaid(instalmentEntity.getInstalmentReferenceNumber());
			commonRes.setIsError(false);
			commonRes.setCommonResponse("Successfully Updated Payment Status");
			commonRes.setMessage("Successfully Updated Payment Status");
			commonRes.setErroCode(200);
			return ResponseEntity.ok(commonRes);
		} catch (Exception e) {
			e.printStackTrace();
			commonRes.setIsError(true);
			commonRes.setCommonResponse("Error in updating PaymentStatus: " + e);
			commonRes.setMessage("Error in updating PaymentStatus: " + e);
			commonRes.setErroCode(409);
			return new ResponseEntity<>(commonRes, HttpStatus.CONFLICT);
		}
	}

	public ResponseEntity<?> checkEFTEnabled(String quoteNo, String currentInstallment) {
		CommonRes commonRes = new CommonRes();
		try {
//			HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(quoteNo);
////			EndorsementCheckDto endorsementCheckDto = clientAccountDetailsService.checkAndReturnOriginalQuoteNo(quoteNo, homePositionMaster.getCompanyId());

			Instalment instalmentEntity = instalmentRepository
					.findByQuoteNoAndNoOfInstalmentAndContractSequenceIsNotNull(quoteNo, Integer.valueOf(currentInstallment));
			if (instalmentEntity == null) { /// quote is not in realpay
				commonRes.setErroCode(204);
				commonRes.setMessage("No RealPay Available");
				commonRes.setCommonResponse("No RealPay Available");
			} else {
				commonRes.setErroCode(200);
				commonRes.setMessage(instalmentEntity.getInstalmentStatus());
				commonRes.setCommonResponse(instalmentEntity.getInstalmentStatus());
			}
			return ResponseEntity.ok(commonRes);
		} catch (Exception e) {
			e.printStackTrace();
			commonRes.setErroCode(409);
			commonRes.setMessage("Error in fetching instalment status");
			commonRes.setIsError(true);
			commonRes.setCommonResponse(e.getMessage());
			return new ResponseEntity<>(commonRes, HttpStatus.CONFLICT);
		}
	}

	public void updateEFTSuccessInInstallment(String paymentId, String instalmentReferenceNumber, BigDecimal amount) {
		Instalment instalmentRec = instalmentRepository.findByInstalmentReferenceNumber(instalmentReferenceNumber);

		Instalment updatedInstalment = instalmentRec.toBuilder()
				.paymentId(paymentId).instalmentStatus(Status.EFT_SUCCESS).instalmentAmount(amount)
				.responseCode("00").lastUpdateDate(new Date()).message("PAID via EFT")
				.webhookUpdated("Y").webhookUpdatedDate(LocalDateTime.now())
				.lastProcessedStatus(Status.EFT_SUCCESS).lastProcessedResponseCode("00")
				.build();

		instalmentRepository.save(updatedInstalment);
	}

	public void checkAndUpdateForReInsurance(String quoteId) {
		try {
			HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(quoteId);
			if (homePositionMaster.getRiStatus() != null) {
				logger.info("Reinsurance is enabled for this quote! calling /reinsurance/push/ReInsuranceDetails for {}", quoteId);
				ReInsuranceQuoteReq reInsuranceQuoteReq = new ReInsuranceQuoteReq();
				reInsuranceQuoteReq.setQuoteNo(quoteId);
				reInsuranceQuoteReq.setCreatedBy("RealPay");
				reInsuranceQuoteReq.setUserType(homePositionMaster.getUserType());
				CommonRes commonRes = reinsuranceService.updatePolicyNo(reInsuranceQuoteReq);
				logger.info("Updated Status for Reinsurance {}", commonRes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public String getAuthToken() {
		try {
			String token = tokenGenerator.getToken();
			if (token != null && !token.isEmpty()) {
				if (!token.startsWith("Bearer ")) {
					token = "Bearer " + token;
				}
				return token;
			}
			return null;
		} catch (Exception e) {
			logger.error("Failed to generate authentication token: {}", e.getMessage(), e);
			return null;
		}
	}

	public CommonRes cancelFutureInstalment(String quoteNo){
		CommonRes commonRes = new CommonRes();
		try{
			HomePositionMaster homePositionMaster =  homerepo.findByQuoteNo(quoteNo);
			List<String> quoteNos = homerepo.findQuoteNoByOriginalPolicyNo(homePositionMaster.getOriginalPolicyNo());
			logger.info("Quote Nos from original Policy for {} - {}",quoteNo,quoteNos);
			List<Contract> contracts = contractRepository.findByQuoteNos(quoteNos);
			if(contracts!=null&& !contracts.isEmpty()){
				contracts.forEach(eachContract->{
					cancelContractsWithAPI(eachContract.getContractNumber(), "CANCEL POLICY BY ENDORSEMENT");
				});
				commonRes.setMessage("Successfully cancelled for {}"+quoteNo);
				commonRes.setCommonResponse("Successfully cancelled for {}"+quoteNo);
				commonRes.setErroCode(200);
			}else{
				commonRes.setMessage("No contracts found for "+quoteNo);
				commonRes.setCommonResponse("No contracts find for "+quoteNo);
				commonRes.setErroCode(204);
			}
			commonRes.setIsError(false);

		}catch(Exception e){
			logger.error("Exception in canceling instalments for {}",quoteNo);
			e.printStackTrace();
			commonRes.setMessage("Exception in canceling instalments for"+quoteNo);
			commonRes.setCommonResponse("Exception in canceling instalments for"+quoteNo);
			commonRes.setIsError(true);
			commonRes.setErroCode(500);
		}
		return commonRes;
	}

	public void createEntryForSuccessEmail(String ref){
		try{
			Instalment instalmentRec = instalmentRepository.findByInstalmentReferenceNumber(ref);
			HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(instalmentRec.getQuoteNo());

			Optional<ClientAccountDetails> clientAccountDetails = clientAccountDetailsRepository
					.findByQuoteNo(instalmentRec.getQuoteNo());

			LoginUserInfo loginUserInfo = loginUserInfoRepository.findByLoginId(homePositionMaster.getCustomerCode());

			realpayInstallmentReminderService.createReminder(instalmentRec.getClientNumber(),
					homePositionMaster.getPolicyNo(), instalmentRec.getQuoteNo(),
					instalmentRec.getNoOfInstalment().toString(), instalmentRec.getInstalmentAmount(), LocalDateTime.now(),
					clientAccountDetails.get().getClientName(), loginUserInfo.getLoginId(), loginUserInfo.getCustomerName(),
					clientAccountDetails.get().getEmail(), loginUserInfo.getUserMail(), homePositionMaster.getCompanyId(), instalmentRec.getInstalmentReferenceNumber(),
					"PHOENIX_PREMIUM_INSTALLMENT_PAYMENT_SUCCESS");

			logger.info("Created Entry For Sending Successful Email {}",ref);
		}catch (Exception e){
			logger.error("Error in Creating Entry for Sending Email {}",ref);
			e.printStackTrace();
		}


	}

}