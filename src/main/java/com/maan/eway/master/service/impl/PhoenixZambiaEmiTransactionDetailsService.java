package com.maan.eway.master.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EmiMaster;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceLifeDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.ExchangeMaster;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.EmiInstallmentDetailsReq;
import com.maan.eway.master.req.EmiTransactionDetailsGetReq;
import com.maan.eway.master.req.EmiTransactionDetailsNextReq;
import com.maan.eway.master.req.EmiTransactionDetailsSaveReq;
import com.maan.eway.master.req.EmiTransactionDetailsUpdateReq;
import com.maan.eway.master.req.TaxSummary;
import com.maan.eway.master.req.UserCustomizedEmiReq;
import com.maan.eway.master.req.UserCustomizedInstallmentReq;
import com.maan.eway.master.res.EmiCompanyInfoListRes;
import com.maan.eway.master.res.EmiDisplayListRes;
import com.maan.eway.master.res.EmiDisplayRes;
import com.maan.eway.master.res.EmiInfoListRes;
import com.maan.eway.master.res.EmiTransactionDetailsRes;
import com.maan.eway.master.service.EmiTransactionDetailsService;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.notification.service.impl.JasperNotificationInterImpl;
import com.maan.eway.renewal.req.EmiDataRequest;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EmiMasterRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceLifeDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.ExchangeMasterRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Primary
@Transactional
public class PhoenixZambiaEmiTransactionDetailsService implements EmiTransactionDetailsService {

	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Value(value = "${spring.jpa.database}")
	private String dataBaseType;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private EmiTransactionDetailsRepository repo;

	@Autowired
	private ExchangeMasterRepository exchangeMasterRepo;

	@Autowired
	private HomePositionMasterRepository homerepo;

	@Autowired
	private PaymentDetailRepository paymentdetailrepo;

	@Autowired
	private EServiceMotorDetailsRepository motorRepo;

	@Autowired
	private EserviceTravelDetailsRepository travelRepo;

	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;

	@Autowired
	private EserviceCommonDetailsRepository commonRepo;

	@Autowired
	private EserviceLifeDetailsRepository lifeRepo;

	@Autowired
	private NotifTransactionDetailsRepository notifTrans;

	@Autowired
	private JasperNotificationInterImpl notificationService;

	@Autowired
	private CompanyProductMasterRepository companyProductMasterRepo;

	@Autowired
	private EmiMasterRepository emiMasterRepos;
	
	@Autowired
	private HomePositionMasterRepository homePositionMasterRepos;
	
	@Autowired
	private FactorRateRequestDetailsRepository factorRateRepo;
	
	@Autowired
	private PolicyCoverDataRepository policyCDRepo;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(EmiTransactionDetailsServiceImpl.class);

	// Insert Validation

	public List<Error> validateEmiTransactionDetails(EmiTransactionDetailsSaveReq req) {

		List<Error> errorList = new ArrayList<Error>();

		try {
			/*
			 * if(req.getCompanyId().equalsIgnoreCase("100020")) {
			 * errorList=kenyaEmiTransactionDetails.validateEmiTransactionDetails(req);
			 * return errorList; }
			 */

			if (StringUtils.isBlank(req.getQuoteNo())) {
				errorList.add(new Error("03", "QuoteNo", "Please Enter QuoteNo"));
			} else {
				List<EmiTransactionDetails> quoteNo = repo.findByQuoteNoAndCompanyIdAndProductId(req.getQuoteNo(),
						req.getCompanyId(), req.getProductId());
				if (quoteNo != null) {
					quoteNo = quoteNo.stream().filter(o -> o.getPaymentStatus().equals("Accept"))
							.collect(Collectors.toList());
					if (quoteNo.size() > 0 && StringUtils.isNotBlank(req.getQuoteNo())) {
						// if (quoteNo.get(0).getPaymentStatus().equalsIgnoreCase("Accept")) {
						errorList.add(new Error("08", "QuoteNo", "This QuoteNo  Already Running"));
					}
				}
			}

//				else {
//					List<EmiTransactionDetails> quoteNo = repo.findByQuoteNoAndCompanyIdAndProductId(req.getQuoteNo(),
//							req.getCompanyId(), req.getProductId());
//					quoteNo = quoteNo.stream().filter(o -> o.getQuoteNo() != null)
//							.filter(distinctByKey(o -> o.getQuoteNo())).collect(Collectors.toList());
//					if (quoteNo.size()>0 && StringUtils.isNotBlank(req.getQuoteNo())) {
//						if (quoteNo.get(0).getQuoteNo().equalsIgnoreCase(req.getQuoteNo())) {
//							errorList.add(new Error("08", "QuoteNo", "This QuoteNo  Already Exist"));
//						}
//					}
//				}

			// Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Select Status  "));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
			} else if (!("Y".equalsIgnoreCase(req.getStatus()) || "N".equalsIgnoreCase(req.getStatus())
					|| "R".equalsIgnoreCase(req.getStatus()) || "P".equalsIgnoreCase(req.getStatus()))) {
				errorList.add(new Error("05", "Status",
						"Please Select Valid Status - Active or Deactive or Pending or Referral "));
			}

			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("07", "CreatedBy", "Please Enter CreatedBy"));
			} else if (req.getCreatedBy().length() > 100) {
				errorList.add(new Error("07", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			}

//				if (StringUtils.isBlank(req.getPaymentDetails())) {
//					errorList.add(new Error("08", "PaymentDetails", "Please Enter PaymentDetails "));
//				}
//				
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	// Insert
	@Transactional
	public SuccessRes insertEmiTransactionDetails(EmiTransactionDetailsSaveReq req) {
		SuccessRes res = new SuccessRes();
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			String quoteNo = req.getQuoteNo();
			HomePositionMaster homeData = homePositionMasterRepos.findByQuoteNo(quoteNo);
			Integer stampDuty = 40;
			BigDecimal adv = new BigDecimal(0);
			Integer noOfMonth = 0, instalId = 0;
			if ("N".equalsIgnoreCase(req.getStatus())) {
				List<EmiTransactionDetails> list = repo.findByQuoteNoAndCompanyIdAndProductId(quoteNo,
						req.getCompanyId(), req.getProductId());
				if (list.size() > 0 && StringUtils.isNotBlank(req.getQuoteNo())) {
					repo.deleteAll(list);
				}
				res.setSuccessId("N");
				res.setResponse("No Choose EMI");
			} else if (homeData.getEndtTypeId() != null 
				&& "Y".equalsIgnoreCase(req.getStatus())) {
				res = getEndorsementEmiDetails(req, homeData);
			} else {
				Double temp = 0d, premiumWithTax, interestPercent, advancePercent, interestAmount, totalLoanAmount,
						advanceAmount, balanceAmount = null, installment = 0d;

				if (req.getInstallmentTypeId() != null) {
					instalId = Integer.valueOf(req.getInstallmentTypeId());
				}
				if (req.getInstallmentPeriod() != null) {
					noOfMonth = Integer.valueOf(req.getInstallmentPeriod());
				}

				// Finding Old Record
				List<EmiTransactionDetails> list = repo.findByQuoteNoAndCompanyIdAndProductId(req.getQuoteNo(),
						req.getCompanyId(), req.getProductId());
				if (list.size() > 0 && StringUtils.isNotBlank(req.getQuoteNo())) {
					repo.deleteAll(list);
				}

				List<EmiMaster> emiMasterData = new ArrayList<>();
				if ("Y".equalsIgnoreCase(req.getUserOption())) {
					UserCustomizedEmiReq customEmiReq = req.getUserCustomizedEmiReq();
					EmiMaster data = new EmiMaster();
					if (customEmiReq != null) {
						interestPercent = Double.valueOf(customEmiReq.getInterestPercent());
						advancePercent = Double.valueOf(customEmiReq.getAdvancePercent());

						premiumWithTax = Double.valueOf(homeData.getOverallPremiumLc().toString());

						if (StringUtils.isNotBlank(req.getInstallmentPeriod())) {
							noOfMonth = Integer.parseInt(req.getInstallmentPeriod());
						}
						if ("Y".equalsIgnoreCase(customEmiReq.getMonthGapYn())) {
							instalId = Integer.parseInt(customEmiReq.getMonthGap());
							data.setInstallmentTypeDesc(noOfMonth + " months (Gap Period: " + instalId + ")");
						} else {
							instalId = 1;
							data.setInstallmentTypeDesc(noOfMonth + " months");
						}
						emiMasterData.add(data);
						if ("Y".equalsIgnoreCase(customEmiReq.getCustomizedInstallmentYn())) {
							advanceAmount = insertEmiTransactionDetailsByInstalId4(req, interestPercent, advancePercent,
									premiumWithTax, instalId, noOfMonth, emiMasterData, stampDuty, homeData);
						} else {
							advanceAmount = insertEmiTransactionDetailsByInstalId3(req, interestPercent, advancePercent,
									premiumWithTax, instalId, noOfMonth, emiMasterData, stampDuty, homeData);
						}

						adv = BigDecimal.valueOf(advanceAmount);
					}
				} else {

					// Getting Record from Emi Master
					if (req.getProductId().equalsIgnoreCase("5")) {
						emiMasterData = getEmiMasterData(req.getCompanyId(), req.getProductId(), req.getPolicyType(),
								instalId.toString());
						System.out.println(emiMasterData);
					} else {
						emiMasterData = emiMasterRepos.findByCompanyIdAndProductIdAndStatusAndInstallmentTypeId(
								req.getCompanyId(), Integer.valueOf(req.getProductId()), "Y", instalId.toString());
					}
					interestPercent = Double.valueOf(emiMasterData.get(0).getInterestPercent().toString());
					advancePercent = Double.valueOf(emiMasterData.get(0).getAdvancePercent().toString());

					premiumWithTax = Double.valueOf(homeData.getOverallPremiumLc().toString());
					if (req.getCompanyId().equalsIgnoreCase("100020")) {
						premiumWithTax = premiumWithTax - stampDuty;
					}
					// noOfMonth=Integer.valueOf(emiMasterData.get(0).getInstallmentPeriod());

					if (req.getInstallmentTypeId() != null) {
						instalId = Integer.valueOf(req.getInstallmentTypeId());
						// premiumWithTax = Double.valueOf(req.getPremiumWithTax());
						if (StringUtils.isNotBlank(emiMasterData.get(0).getInstallmentPeriod())) {
							noOfMonth = Integer.parseInt(emiMasterData.get(0).getInstallmentPeriod());
							instalId = 1;
						}
						advanceAmount = insertEmiTransactionDetailsByInstalId3(req, interestPercent, advancePercent,
								premiumWithTax, instalId, noOfMonth, emiMasterData, stampDuty, homeData);
						adv = new BigDecimal(advanceAmount);
					}
				}
				res.setSuccessId(quoteNo);
				res.setResponse("Saved Successful");
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());
			// Update Home Position Master
			if ("Y".equalsIgnoreCase(req.getStatus())) {
				homeData.setInstallmentPeriod(noOfMonth.toString());
				homeData.setEmiYn("Y");
				homeData.setNoOfInstallment("0");
				homeData.setEmiPremium(adv);
				homerepo.save(homeData);
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					EserviceMotorDetails motor = saveMotor("Y", adv, req.getQuoteNo(), noOfMonth.toString(), "0");
				} else if (product.getMotorYn().equalsIgnoreCase("H")
						&& req.getProductId().equalsIgnoreCase(travelProductId)) {
					EserviceTravelDetails travel = saveTravel("Y", adv, req.getQuoteNo(), noOfMonth.toString(), "0");
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
					EserviceBuildingDetails motor = saveBuilding("Y", adv, req.getQuoteNo(), noOfMonth.toString(), "0");
				} else if (product.getMotorYn().equalsIgnoreCase("L")) {
					EserviceLifeDetails motor = saveLife("Y", adv, req.getQuoteNo(), noOfMonth.toString(), "0");
				} else {
					EserviceCommonDetails motor = saveCommon("Y", adv, req.getQuoteNo(), noOfMonth.toString(), "0");
				}

			} else {
				homeData.setInstallmentPeriod("");
				homeData.setEmiYn("N");
				homeData.setNoOfInstallment(null);
				homeData.setEmiPremium(null);
				homerepo.save(homeData);
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					EserviceMotorDetails motor = saveMotor("N", null, req.getQuoteNo(), "", null);
				} else if (product.getMotorYn().equalsIgnoreCase("H")
						&& req.getProductId().equalsIgnoreCase(travelProductId)) {
					EserviceTravelDetails travel = saveTravel("N", null, req.getQuoteNo(), "", null);
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
					EserviceBuildingDetails building = saveBuilding("N", null, req.getQuoteNo(), "", null);
				} else if (product.getMotorYn().equalsIgnoreCase("L")) {
					EserviceLifeDetails life = saveLife("N", null, req.getQuoteNo(), "", null);
				} else {
					EserviceCommonDetails common = saveCommon("N", null, req.getQuoteNo(), "", null);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return res;
	}

	private Double insertEmiTransactionDetailsByInstalId2(EmiTransactionDetailsSaveReq req, Double interestPercent,
			Double advancePercent, Double premiumWithTax, Integer instalId, Integer installmentPeriod,
			List<EmiMaster> emiMasterData, Integer stampDuty) {
		EmiTransactionDetails saveData = new EmiTransactionDetails();
		Double adv = 0d;
		String quoteNo = req.getQuoteNo();
		String insDesc = "";
		Date entryDate = new Date();
		String createdBy = req.getCreatedBy();
		Double balanceAmount = null, temp = 0d, installment = 0d, trackTotLnAmtWithInterest = 0d, trackInsAmt = 0d,
				skipAmount = 0d;
		Integer loop = installmentPeriod / instalId;
		Integer i = 0, in = 0;
		Double totalLoanAmount = premiumWithTax + premiumWithTax * interestPercent / 100;
		if (interestPercent > 0) {
			BigDecimal bd = BigDecimal.valueOf(totalLoanAmount);
			bd = bd.setScale(2, RoundingMode.HALF_UP);

			totalLoanAmount = bd.doubleValue();
		}
		Double interestAmount = premiumWithTax * interestPercent / 100;
		Double advanceAmount = totalLoanAmount * advancePercent / 100;
		advanceAmount = BigDecimal.valueOf(advanceAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
		adv = advanceAmount;
		Integer set_installment = 0;
		Calendar cal = Calendar.getInstance();
		Date dueDate = cal.getTime();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
		for (i = 0; i < loop; i++) {
			if (i == 0 && advanceAmount > 0.0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				advanceAmount = premiumWithTax * advancePercent / 100;
				advanceAmount = BigDecimal.valueOf(advanceAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
				if (req.getCompanyId().equalsIgnoreCase("100020")) {
					advanceAmount = advanceAmount + stampDuty;
				}
				adv = advanceAmount;
				totalLoanAmount = premiumWithTax - advanceAmount;
				totalLoanAmount = totalLoanAmount + totalLoanAmount * interestPercent / 100;
				totalLoanAmount = BigDecimal.valueOf(totalLoanAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				in = loop - 1;
				installment = balanceAmount / in;
				installment = BigDecimal.valueOf(installment).setScale(2, RoundingMode.HALF_UP).doubleValue();
				set_installment = 0;
			} else if (i == 0) {
				cal.add(Calendar.MONTH, i);
				dueDate = cal.getTime();
				advanceAmount = 0d;
				adv = advanceAmount;
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount - advanceAmount;
				in = loop;
				installment = balanceAmount / in;
				installment = BigDecimal.valueOf(installment).setScale(2, RoundingMode.HALF_UP).doubleValue();
				balanceAmount = balanceAmount - installment;
				balanceAmount = BigDecimal.valueOf(balanceAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
				set_installment = 1;
				trackInsAmt += installment;
			} else {
				cal.add(Calendar.MONTH, instalId);
				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
				dueDate = cal.getTime();
				// Increment calendar based on the installment period
				System.out.println("premiumWithTax " + premiumWithTax + " advanceAmount" + advanceAmount
						+ " installment " + installment + " totalLoanAmount" + totalLoanAmount + "");
				if ((trackTotLnAmtWithInterest - trackInsAmt) < installment || i == loop - 1) {
					skipAmount = installment - (trackTotLnAmtWithInterest - trackInsAmt);
					skipAmount = BigDecimal.valueOf(skipAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();
					installment = installment - skipAmount;
					installment = BigDecimal.valueOf(installment).setScale(2, RoundingMode.HALF_UP).doubleValue();
					/**
					 * }else if((balanceAmount-installment)<12 && (balanceAmount-installment)>0) {
					 * skipAmount=(balanceAmount-installment);
					 * skipAmount=BigDecimal.valueOf(skipAmount).setScale(2,
					 * RoundingMode.HALF_UP).doubleValue(); installment = installment+skipAmount;
					 * installment=BigDecimal.valueOf(installment).setScale(2,
					 * RoundingMode.HALF_UP).doubleValue();
					 **/

				} else {
					trackInsAmt += installment;
				}
//				        cal.add(Calendar.MONTH, instalId);
//				        dueDate = cal.getTime();
				temp = balanceAmount;
				temp -= installment;
				temp = BigDecimal.valueOf(temp).setScale(2, RoundingMode.HALF_UP).doubleValue();
				balanceAmount = temp;
			}
			// Save
			saveData.setPremiumWithTax(premiumWithTax);
			saveData.setInstallmentPeriod(installmentPeriod.toString());
			saveData.setInterest(interestPercent);
			saveData.setAdvance(advancePercent.toString());
			saveData.setInterestAmount(interestAmount);
			if (i == 0 && advanceAmount > 0) {
				saveData.setDueAmount(advanceAmount);
				insDesc = "Advance Amount";
				// saveData.setPaymentDate(entryDate);
				saveData.setStatus(req.getStatus());
				saveData.setPaymentDetails(req.getPaymentDetails());
			} else {
				saveData.setDueAmount(installment);
				insDesc = "Installment Amount";
				saveData.setStatus("Y");
				saveData.setPaymentDetails(null);
			}
			saveData.setPaymentDate(null);
			saveData.setPaymentStatus("Pending");
			saveData.setQuoteNo(quoteNo);
			saveData.setProductId(req.getProductId());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setBalanceAmount(balanceAmount);
			saveData.setTotalLoanAmount(totalLoanAmount);
			saveData.setInstallmentDesc(insDesc);
			saveData.setInstalment("0");
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(createdBy);
			saveData.setDueDate(dueDate);
			saveData.setRemarks(req.getRemarks());

			String installmentDesc = emiMasterData.get(0).getInstallmentTypeDesc();
			saveData.setInstallmentTypeId(req.getInstallmentTypeId());
			saveData.setInstallmentTypeDesc(StringUtils.isBlank(installmentDesc) ? "" : installmentDesc);
			set_installment++;

			repo.saveAndFlush(saveData);

		}
		return (double) adv;

	}

	private Double insertEmiTransactionDetailsByInstalId4(EmiTransactionDetailsSaveReq req, Double interestPercent,
			Double advancePercent, Double premiumWithTax, Integer instalId, Integer installmentPeriod,
			List<EmiMaster> emiMasterData, Integer stampDuty, HomePositionMaster homeData) {
		Double adv = 0d;
		String quoteNo = req.getQuoteNo();
		String insDesc = "";
		Date entryDate = new Date();
		String createdBy = req.getCreatedBy();
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal temp = BigDecimal.ZERO;
		BigDecimal installment = BigDecimal.ZERO;
		BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
		BigDecimal trackInsAmt = BigDecimal.ZERO;
		BigDecimal skipAmount = BigDecimal.ZERO;
		Integer loop = installmentPeriod / instalId;
		Integer i = 0, in = 0;
		BigDecimal intPerc = BigDecimal.valueOf(interestPercent);
		BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
		BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
		BigDecimal totalLoanAmount = premWithTax.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100")));
		if (interestPercent > 0) {
			totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
		}
		BigDecimal interestAmount = premWithTax.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);
		BigDecimal advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);
		adv = advanceAmount.doubleValue();
		Integer set_installment = 0;
		Calendar cal = Calendar.getInstance();
		Date dueDate = cal.getTime();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
		UserCustomizedEmiReq customEmiReq = req.getUserCustomizedEmiReq();
		List<UserCustomizedInstallmentReq> customInstallment = customEmiReq.getCustomizedInstallment();
		BigDecimal premWithTaxAndInt = BigDecimal.ZERO;
		for (i = 0; i < 1; i++) {
			EmiTransactionDetails saveData = new EmiTransactionDetails();
			// Advanced Amount
			if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				insDesc = "Advance Amount";

				advanceAmount = premWithTax.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
						RoundingMode.HALF_UP);

				if (req.getCompanyId().equalsIgnoreCase("100020")) {
					advanceAmount = advanceAmount.add(new BigDecimal(stampDuty));
				}
				totalLoanAmount = premWithTax.subtract(advanceAmount);
				interestAmount = totalLoanAmount.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
						RoundingMode.HALF_UP);
				totalLoanAmount = totalLoanAmount.add(interestAmount);
				adv = advanceAmount.doubleValue();
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				saveData.setRequestReferenceNo(req.getRequestReferenceNo());
				saveData.setPremiumWithTax(premiumWithTax);
				saveData.setInstallmentPeriod(installmentPeriod.toString());
				saveData.setInterest(interestPercent);
				saveData.setAdvance(advancePercent.toString());
				saveData.setInterestAmount(interestAmount.doubleValue());
				saveData.setDueAmount(adv);
				// saveData.setPaymentDate(entryDate);
				saveData.setStatus(req.getStatus());
				saveData.setPaymentDetails(req.getPaymentDetails());

				saveData.setPaymentDate(null);
				saveData.setPaymentStatus("Pending");
				saveData.setQuoteNo(quoteNo);
				saveData.setProductId(req.getProductId());
				saveData.setCompanyId(req.getCompanyId());
				saveData.setBalanceAmount(balanceAmount.doubleValue());
				saveData.setTotalLoanAmount(totalLoanAmount.doubleValue());
				saveData.setInstallmentDesc(insDesc);
				saveData.setInstalment(set_installment.toString());
				saveData.setEntryDate(entryDate);
				saveData.setCreatedBy(req.getCreatedBy());
				saveData.setUpdatedDate(new Date());
				saveData.setUpdatedBy(createdBy);
				saveData.setDueDate(dueDate);
				saveData.setRemarks(req.getRemarks());

				String installmentDesc = emiMasterData.get(0).getInstallmentTypeDesc();
				saveData.setInstallmentTypeId(req.getInstallmentTypeId());
				saveData.setInstallmentTypeDesc(StringUtils.isBlank(installmentDesc) ? "" : installmentDesc);

				repo.saveAndFlush(saveData);
			}
			Integer inc = 0;
			int size = customInstallment.size();
			premWithTaxAndInt = premWithTax.add(interestAmount);
			BigDecimal remPerc = new BigDecimal("100").subtract(advPerc);
			for (int x = 0; x < size; x++) {
				EmiTransactionDetails saveData1 = new EmiTransactionDetails();
				UserCustomizedInstallmentReq cus = customInstallment.get(x);
				inc = inc + 1;
				if (x == 0 && advanceAmount.compareTo(BigDecimal.ZERO) == 0) {

					advanceAmount = BigDecimal.ZERO;
					balanceAmount = totalLoanAmount;
					trackTotLnAmtWithInterest = totalLoanAmount;
					adv = advanceAmount.doubleValue();
					cal.add(Calendar.MONTH, 0);
					dueDate = cal.getTime();
					insDesc = "Installment Amount";
					installment = totalLoanAmount.multiply(new BigDecimal(cus.getInstallmentPercent())).divide(remPerc,
							2, RoundingMode.HALF_UP);
					saveData1.setDueAmount(installment.doubleValue());
					saveData1.setInstalment("0");

					trackInsAmt = trackInsAmt.add(installment);
					temp = balanceAmount;
					temp = temp.subtract(installment);
					balanceAmount = temp;

				} else if (x == size - 1) {
					cal.add(Calendar.MONTH, instalId);
					int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
					dueDate = cal.getTime();
					insDesc = "Installment Amount";
					installment = totalLoanAmount.multiply(new BigDecimal(cus.getInstallmentPercent())).divide(remPerc,
							2, RoundingMode.HALF_UP);
					if (installment.compareTo(balanceAmount) == 0) {
						saveData1.setDueAmount(installment.doubleValue());
					} else {
						saveData1.setDueAmount(balanceAmount.doubleValue());
					}
					saveData1.setInstalment(inc.toString());

				} else {
					cal.add(Calendar.MONTH, instalId);
					int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
					dueDate = cal.getTime();
					insDesc = "Installment Amount";
					installment = totalLoanAmount.multiply(new BigDecimal(cus.getInstallmentPercent())).divide(remPerc,
							2, RoundingMode.HALF_UP);
					saveData1.setDueAmount(installment.doubleValue());
					saveData1.setInstalment(inc.toString());

					trackInsAmt = trackInsAmt.add(installment);
					temp = balanceAmount;
					temp = temp.subtract(installment);
					balanceAmount = temp;
				}
				saveData1.setPremiumWithTax(premiumWithTax);
				saveData1.setInstallmentPeriod(installmentPeriod.toString());
				saveData1.setInterest(interestPercent);
				saveData1.setAdvance(advancePercent.toString());
				saveData1.setInterestAmount(interestAmount.doubleValue());
				// saveData.setPaymentDate(entryDate);
				saveData1.setStatus(req.getStatus());
				saveData1.setPaymentDetails(req.getPaymentDetails());

				saveData1.setPaymentDate(null);
				saveData1.setPaymentStatus("Pending");
				saveData1.setQuoteNo(quoteNo);
				saveData1.setProductId(req.getProductId());
				saveData1.setCompanyId(req.getCompanyId());
				saveData1.setBalanceAmount(balanceAmount.doubleValue());
				saveData1.setTotalLoanAmount(totalLoanAmount.doubleValue());
				saveData1.setInstallmentDesc(insDesc);
				saveData1.setEntryDate(entryDate);
				saveData1.setCreatedBy(req.getCreatedBy());
				saveData1.setUpdatedDate(new Date());
				saveData1.setUpdatedBy(createdBy);
				saveData1.setDueDate(dueDate);
				saveData1.setRemarks(req.getRemarks());
				saveData.setCurrency(homeData.getCurrency());
				BigDecimal percentage = installment.divide(premWithTax, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
					    .setScale(2, RoundingMode.HALF_UP);

				saveData.setPercentage(percentage);

				String installmentDesc = emiMasterData.get(0).getInstallmentTypeDesc();
				saveData1.setInstallmentTypeId(req.getInstallmentTypeId());
				saveData1.setInstallmentTypeDesc(StringUtils.isBlank(installmentDesc) ? "" : installmentDesc);

				repo.saveAndFlush(saveData1);
			}

		}
		return (double) adv;

	}
	private Double insertEmiTransactionDetailsByInstalId3(EmiTransactionDetailsSaveReq req, Double interestPercent,
			Double advancePercent, Double premiumWithTax, Integer instalId, Integer installmentPeriod,
			List<EmiMaster> emiMasterData, Integer stampDuty, HomePositionMaster homeData) {
		boolean calculateInterest = "Y".equalsIgnoreCase(emiMasterData.get(0).getIntresetOrProposal());
		Double adv = 0d;
		String quoteNo = req.getQuoteNo();
		String insDesc = "";
		Date entryDate = new Date();
		String createdBy = req.getCreatedBy();
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal temp = BigDecimal.ZERO;
		BigDecimal installment = BigDecimal.ZERO;
		BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
		BigDecimal trackInsAmt = BigDecimal.ZERO;
		BigDecimal skipAmount = BigDecimal.ZERO;
		BigDecimal interestAmount= BigDecimal.ZERO;
		BigDecimal advanceAmount= BigDecimal.ZERO;
		BigDecimal advSetUpAmount  = BigDecimal.ZERO;
		Integer loop = installmentPeriod / instalId;
		Integer i = 0, in = 0;
		Integer emiDay = emiMasterData.get(0).getEmiAllowedDay();
		BigDecimal intPerc = BigDecimal.valueOf(interestPercent);
		BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
		BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
		BigDecimal premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		BigDecimal totalLoanAmount = premWithTax.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100")));
		Integer set_installment = 0;
		Calendar cal = Calendar.getInstance();
		Date dueDate = cal.getTime();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
		if (emiDay != null && emiDay > 0 && emiDay < 32)
			desiredDay = emiDay;
		if(!calculateInterest)
		{
		if (interestPercent > 0) {
			totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
		}
		if (req.getCompanyId().equalsIgnoreCase("100020")) {
			premiumWithTax = premiumWithTax - stampDuty;
			premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		}
		 interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);
		 advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);
		adv = advanceAmount.doubleValue();
		
		}
		else
		{
			List<PolicyCoverData> policyDataList = policyCDRepo.findByQuoteNo(quoteNo);

			List<TaxSummary> taxList = policyDataList.stream()
			        .filter(c -> c.getTaxId() != null)
			        .filter(c -> !c.getStatus().equalsIgnoreCase("D"))
			        .collect(Collectors.groupingBy(PolicyCoverData::getTaxId,
			                Collectors.reducing(BigDecimal.ZERO, 
			                        c -> c.getTaxAmountLc() == null ? BigDecimal.ZERO : c.getTaxAmountLc(), 
			                        BigDecimal::add)
			        ))
			        .entrySet()
			        .stream()
			        .map(e -> new TaxSummary(e.getKey(), e.getValue()))
			        .collect(Collectors.toList());
			BigDecimal totalPremiumwithoutax = policyDataList.stream()
			        .filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
			        .filter(c -> !c.getStatus().equalsIgnoreCase("D"))
			        .filter(c -> "B".equalsIgnoreCase(c.getCoverageType()) || "O".equalsIgnoreCase(c.getCoverageType()))
			        .map(c -> c.getPremiumExcludedTaxLc() == null ? BigDecimal.ZERO : c.getPremiumExcludedTaxLc())
			        .reduce(BigDecimal.ZERO, BigDecimal::add);
			String taxIds = emiMasterData.get(0).getTaxIds();		
			Set<Integer> taxIdSet = StringUtils.isNotBlank(taxIds)
			        ? Arrays.stream(taxIds.split(","))
			                .map(String::trim)
			                .map(Integer::parseInt)
			                .collect(Collectors.toSet())
			        : Collections.emptySet();
			BigDecimal advemiTaxAmount = taxList.stream()
			        .filter(t -> t.getTaxId() != null)
			        .filter(t -> taxIdSet.contains(t.getTaxId()))
			        .map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
			        .reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal emiTaxAmount = taxList.stream()
			        .filter(t -> t.getTaxId() != null)
			        .filter(t -> ! taxIdSet.contains(t.getTaxId()))
			        .map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
			        .reduce(BigDecimal.ZERO, BigDecimal::add);
			
			premWithTax = totalPremiumwithoutax.add(emiTaxAmount);
			premWithTax1 = totalPremiumwithoutax.add(emiTaxAmount);
			interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			advanceAmount = premWithTax1.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			advanceAmount = advanceAmount.add(advemiTaxAmount);
			
			totalLoanAmount =premWithTax1;
			
			if (i == 0 && calculateInterest) {
				 balanceAmount=totalLoanAmount;
				 trackTotLnAmtWithInterest = totalLoanAmount;
				 advSetUpAmount = totalLoanAmount;
				if (calculateInterest && interestPercent > 0) {
					   	in = loop;
					   	advSetUpAmount=advSetUpAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
					   	totalLoanAmount=totalLoanAmount.subtract(advSetUpAmount);
					   	advSetUpAmount = advSetUpAmount.add(advanceAmount);
					    advanceAmount = advSetUpAmount.add(interestAmount);
					    totalLoanAmount =totalLoanAmount.add(advanceAmount);
					    adv = advanceAmount.doubleValue();
					    installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
					    balanceAmount =totalLoanAmount.subtract(advanceAmount);
					}else {
					    interestAmount = BigDecimal.ZERO;
					    totalLoanAmount = totalLoanAmount.add(interestAmount);
					}
					insDesc = "Advance Amount";
					
				}
		}
		HomePositionMaster homePositionMaster = homerepo.findByQuoteNo(req.getQuoteNo());
		homePositionMaster.setOtherFee(interestAmount);
		homerepo.save(homePositionMaster);
		for (i = 0; i < loop; i++) {
			EmiTransactionDetails saveData = new EmiTransactionDetails();
			if(i == 0 && calculateInterest)
			{
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				set_installment = 0;
			}
			else if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0.0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				advanceAmount = premWithTax.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
						RoundingMode.HALF_UP);
				if (req.getCompanyId().equalsIgnoreCase("100020")) {
					advanceAmount = advanceAmount.add(new BigDecimal(stampDuty));
				}
				adv = advanceAmount.doubleValue();
				totalLoanAmount = premWithTax.subtract(advanceAmount);
				totalLoanAmount = totalLoanAmount.add(totalLoanAmount.multiply(intPerc).divide(new BigDecimal("100")))
						.setScale(2, RoundingMode.HALF_UP);
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				in = loop - 1;
				installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
				set_installment = 0;
			} else if (i == 0) {
				cal.add(Calendar.MONTH, i);
				dueDate = cal.getTime();
				advanceAmount = BigDecimal.ZERO;
				adv = advanceAmount.doubleValue();
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				in = loop;
				installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
				balanceAmount = balanceAmount.subtract(installment).setScale(2, RoundingMode.HALF_UP);
				set_installment = 1;
				trackInsAmt = trackInsAmt.add(installment);
			} else {
				cal.add(Calendar.MONTH, instalId);
				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
				dueDate = cal.getTime();
				// Increment calendar based on the installment period
				System.out.println("premiumWithTax " + premiumWithTax + " advanceAmount" + advanceAmount
						+ " installment " + installment + " totalLoanAmount" + totalLoanAmount + "");
				if ((trackTotLnAmtWithInterest.subtract(trackInsAmt).compareTo(installment) < 0 || i == loop - 1) && !calculateInterest) {
					skipAmount = installment.subtract(trackTotLnAmtWithInterest.subtract(trackInsAmt)).setScale(2,
							RoundingMode.HALF_UP);
					installment = installment.subtract(skipAmount).setScale(2, RoundingMode.HALF_UP);
				} else {
					trackInsAmt = trackInsAmt.add(installment);
				}
				if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
					balanceAmount=BigDecimal.ZERO;
				}
				temp = balanceAmount;
				temp = temp.subtract(installment).setScale(2, RoundingMode.HALF_UP);
				balanceAmount = temp;
				if (temp.compareTo(BigDecimal.ZERO) <= 0) {
					balanceAmount=BigDecimal.ZERO;
				}
				
				
			}
			// Save
			saveData.setRequestReferenceNo(req.getRequestReferenceNo());
			saveData.setPremiumWithTax(premiumWithTax);
			saveData.setInstallmentPeriod(installmentPeriod.toString());
			saveData.setInterest(interestPercent);
			saveData.setAdvance(advancePercent.toString());
			saveData.setInterestAmount(interestAmount.doubleValue());
			if(i == 0 && calculateInterest)
			{
				saveData.setDueAmount(adv);
				insDesc = "Advance Amount";
				// saveData.setPaymentDate(entryDate);
				saveData.setStatus(req.getStatus());
				saveData.setPaymentDetails(req.getPaymentDetails());
			}
			else if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				saveData.setDueAmount(adv);
				insDesc = "Advance Amount";
				// saveData.setPaymentDate(entryDate);
				saveData.setStatus(req.getStatus());
				saveData.setPaymentDetails(req.getPaymentDetails());
			} else {
				saveData.setDueAmount(installment.doubleValue());
				insDesc = "Installment Amount";
				saveData.setStatus("Y");
				saveData.setPaymentDetails(null);
				System.out.println("=====> installment:" + installment.doubleValue());
			}
			saveData.setPaymentDate(null);
			saveData.setPaymentStatus("Pending");
			saveData.setQuoteNo(quoteNo);
			saveData.setProductId(req.getProductId());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setBalanceAmount(balanceAmount.doubleValue());
			saveData.setTotalLoanAmount(totalLoanAmount.doubleValue());
			saveData.setInstallmentDesc(insDesc);
			saveData.setInstalment(set_installment.toString());
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(createdBy);
			saveData.setDueDate(dueDate);
			saveData.setRemarks(req.getRemarks());
			saveData.setCurrency(homeData.getCurrency());
			BigDecimal percentage = installment.divide(premWithTax, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
				    .setScale(2, RoundingMode.HALF_UP);

			saveData.setPercentage(percentage);
			String installmentDesc = emiMasterData.get(0).getInstallmentTypeDesc();
			saveData.setInstallmentTypeId(req.getInstallmentTypeId());
			saveData.setInstallmentTypeDesc(StringUtils.isBlank(installmentDesc) ? "" : installmentDesc);
			set_installment++;

			repo.saveAndFlush(saveData);

		}
		return (double) adv;

	}

	private Double insertEmiTransactionDetailsByInstalId3Bk(EmiTransactionDetailsSaveReq req, Double interestPercent,
			Double advancePercent, Double premiumWithTax, Integer instalId, Integer installmentPeriod,
			List<EmiMaster> emiMasterData, Integer stampDuty, HomePositionMaster homeData) {
		
		Double adv = 0d;
		String quoteNo = req.getQuoteNo();
		String insDesc = "";
		Date entryDate = new Date();
		String createdBy = req.getCreatedBy();
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal temp = BigDecimal.ZERO;
		BigDecimal installment = BigDecimal.ZERO;
		BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
		BigDecimal trackInsAmt = BigDecimal.ZERO;
		BigDecimal skipAmount = BigDecimal.ZERO;
		Integer loop = installmentPeriod / instalId;
		Integer i = 0, in = 0;
		Integer emiDay = emiMasterData.get(0).getEmiAllowedDay();
		BigDecimal intPerc = BigDecimal.valueOf(interestPercent);
		BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
		BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
		BigDecimal premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		BigDecimal totalLoanAmount = premWithTax.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100")));
		if (interestPercent > 0) {
			totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
		}
		if (req.getCompanyId().equalsIgnoreCase("100020")) {
			premiumWithTax = premiumWithTax - stampDuty;
			premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		}
		BigDecimal interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);
		BigDecimal advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);
		adv = advanceAmount.doubleValue();
		Integer set_installment = 0;
		Calendar cal = Calendar.getInstance();
		Date dueDate = cal.getTime();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
		if (emiDay != null && emiDay > 0 && emiDay < 32)
			desiredDay = emiDay;
		for (i = 0; i < loop; i++) {
			EmiTransactionDetails saveData = new EmiTransactionDetails();
			if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0.0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				advanceAmount = premWithTax.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
						RoundingMode.HALF_UP);
				if (req.getCompanyId().equalsIgnoreCase("100020")) {
					advanceAmount = advanceAmount.add(new BigDecimal(stampDuty));
				}
				adv = advanceAmount.doubleValue();
				totalLoanAmount = premWithTax.subtract(advanceAmount);
				totalLoanAmount = totalLoanAmount.add(totalLoanAmount.multiply(intPerc).divide(new BigDecimal("100")))
						.setScale(2, RoundingMode.HALF_UP);
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				in = loop - 1;
				installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
				set_installment = 0;
			} else if (i == 0) {
				cal.add(Calendar.MONTH, i);
				dueDate = cal.getTime();
				advanceAmount = BigDecimal.ZERO;
				adv = advanceAmount.doubleValue();
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				in = loop;
				installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
				balanceAmount = balanceAmount.subtract(installment).setScale(2, RoundingMode.HALF_UP);
				set_installment = 1;
				trackInsAmt = trackInsAmt.add(installment);
			} else {
				cal.add(Calendar.MONTH, instalId);
				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
				dueDate = cal.getTime();
				// Increment calendar based on the installment period
				System.out.println("premiumWithTax " + premiumWithTax + " advanceAmount" + advanceAmount
						+ " installment " + installment + " totalLoanAmount" + totalLoanAmount + "");
				if (trackTotLnAmtWithInterest.subtract(trackInsAmt).compareTo(installment) < 0 || i == loop - 1) {
					skipAmount = installment.subtract(trackTotLnAmtWithInterest.subtract(trackInsAmt)).setScale(2,
							RoundingMode.HALF_UP);
					installment = installment.subtract(skipAmount).setScale(2, RoundingMode.HALF_UP);
				} else {
					trackInsAmt = trackInsAmt.add(installment);
				}
				temp = balanceAmount;
				temp = temp.subtract(installment).setScale(2, RoundingMode.HALF_UP);
				balanceAmount = temp;
			}
			// Save
			saveData.setRequestReferenceNo(req.getRequestReferenceNo());
			saveData.setPremiumWithTax(premiumWithTax);
			saveData.setInstallmentPeriod(installmentPeriod.toString());
			saveData.setInterest(interestPercent);
			saveData.setAdvance(advancePercent.toString());
			saveData.setInterestAmount(interestAmount.doubleValue());
			if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				saveData.setDueAmount(adv);
				insDesc = "Advance Amount";
				// saveData.setPaymentDate(entryDate);
				saveData.setStatus(req.getStatus());
				saveData.setPaymentDetails(req.getPaymentDetails());
			} else {
				saveData.setDueAmount(installment.doubleValue());
				insDesc = "Installment Amount";
				saveData.setStatus("Y");
				saveData.setPaymentDetails(null);
			}
			saveData.setPaymentDate(null);
			saveData.setPaymentStatus("Pending");
			saveData.setQuoteNo(quoteNo);
			saveData.setProductId(req.getProductId());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setBalanceAmount(balanceAmount.doubleValue());
			saveData.setTotalLoanAmount(totalLoanAmount.doubleValue());
			saveData.setInstallmentDesc(insDesc);
			saveData.setInstalment(set_installment.toString());
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(createdBy);
			saveData.setDueDate(dueDate);
			saveData.setRemarks(req.getRemarks());
			saveData.setCurrency(homeData.getCurrency());
			BigDecimal percentage = installment.divide(premWithTax, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"))
				    .setScale(2, RoundingMode.HALF_UP);

			saveData.setPercentage(percentage);
			String installmentDesc = emiMasterData.get(0).getInstallmentTypeDesc();
			saveData.setInstallmentTypeId(req.getInstallmentTypeId());
			saveData.setInstallmentTypeDesc(StringUtils.isBlank(installmentDesc) ? "" : installmentDesc);
			set_installment++;

			repo.saveAndFlush(saveData);

		}
		return (double) adv;

	}

	public EserviceMotorDetails saveMotor(String status, BigDecimal adv, String quoteNo, String installmentPeriod,
			String noOFIns) {
		EserviceMotorDetails save = new EserviceMotorDetails();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {

			List<EserviceMotorDetails> list = motorRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
			if (list != null && list.size() > 0) {

				for (EserviceMotorDetails data : list) {
					save = dozermapper.map(data, EserviceMotorDetails.class);
					save.setEmiYn("Y");
					save.setInstallmentPeriod(Integer.valueOf(
							installmentPeriod != null && !installmentPeriod.isEmpty() ? installmentPeriod : "0"));
					save.setNoOfInstallment(Integer.valueOf(noOFIns != null && !noOFIns.isEmpty() ? noOFIns : "0"));
					save.setEmiPremium(adv);
					motorRepo.save(save);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return save;
	}

	public EserviceTravelDetails saveTravel(String status, BigDecimal adv, String quoteNo, String installmentPeriod,
			String noOFIns) {
		EserviceTravelDetails save = new EserviceTravelDetails();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {

			EserviceTravelDetails data = travelRepo.findByQuoteNo(quoteNo);
			if (data != null) {
				save = dozermapper.map(data, EserviceTravelDetails.class);
				save.setEmiYn("Y");
				save.setInstallmentPeriod(Integer.valueOf(installmentPeriod));
				save.setNoOfInstallment(Integer.valueOf(noOFIns));
				save.setEmiPremium(adv);
				travelRepo.save(save);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return save;
	}

	public EserviceBuildingDetails saveBuilding(String status, BigDecimal adv, String quoteNo, String installmentPeriod,
			String noOFIns) {
		EserviceBuildingDetails save = new EserviceBuildingDetails();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {

			List<EserviceBuildingDetails> list = buildingRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
			if (list != null && list.size() > 0) {

				for (EserviceBuildingDetails data : list) {
					save = dozermapper.map(data, EserviceBuildingDetails.class);
					save.setEmiYn(status);
					save.setInstallmentPeriod(
							StringUtils.isBlank(installmentPeriod) ? null : Integer.valueOf(installmentPeriod));
					save.setNoOfInstallment(StringUtils.isBlank(noOFIns) ? null : Integer.valueOf(noOFIns));
					save.setEmiPremium(adv);
					buildingRepo.save(save);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return save;
	}

	public EserviceCommonDetails saveCommon(String status, BigDecimal adv, String quoteNo, String installmentPeriod,
			String noOFIns) {
		EserviceCommonDetails save = new EserviceCommonDetails();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {

			List<EserviceCommonDetails> list = commonRepo.findByQuoteNo(quoteNo);
			if (list != null && list.size() > 0) {

				for (EserviceCommonDetails data : list) {
					save = dozermapper.map(data, EserviceCommonDetails.class);
					save.setEmiYn("Y");
					save.setInstallmentPeriod(
							StringUtils.isBlank(installmentPeriod) ? null : Integer.valueOf(installmentPeriod));
					save.setNoOfInstallment(StringUtils.isBlank(noOFIns) ? null : Integer.valueOf(noOFIns));
					save.setEmiPremium(adv == null ? null : adv);
					commonRepo.save(save);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return save;
	}

	public EserviceLifeDetails saveLife(String status, BigDecimal adv, String quoteNo, String installmentPeriod,
			String noOFIns) {
		EserviceLifeDetails save = new EserviceLifeDetails();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {

			List<EserviceLifeDetails> list = lifeRepo.findByQuoteNo(quoteNo);
			if (list != null && list.size() > 0) {

				for (EserviceLifeDetails data : list) {
					save = dozermapper.map(data, EserviceLifeDetails.class);
					save.setEmiYn("Y");
					save.setInstallmentPeriod(Integer.valueOf(installmentPeriod));
					save.setNoOfInstallment(Integer.valueOf(noOFIns));
					save.setEmiPremium(adv);
					lifeRepo.save(save);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return save;
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

	public List<EmiMaster> getEmiMasterDataByInsPeriod(String companyId, String productId, String policyType,
			String insPeriod) {
		List<EmiMaster> list = new ArrayList<EmiMaster>();

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
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmiMaster> query = cb.createQuery(EmiMaster.class);

			// Find All
			Root<EmiMaster> b = query.from(EmiMaster.class);

			// Select
			query.select(b);

//				// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<EmiMaster> ocpm1 = effectiveDate.from(EmiMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("emiId"), ocpm1.get("emiId"));
			Predicate a2 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(b.get("productId"), ocpm1.get("productId"));
			Predicate a9 = cb.equal(b.get("policyType"), ocpm1.get("policyType"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4, a9);
//				
//				// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<EmiMaster> ocpm2 = effectiveDate2.from(EmiMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a5 = cb.equal(b.get("emiId"), ocpm2.get("emiId"));
			Predicate a6 = cb.equal(b.get("companyId"), ocpm2.get("companyId"));
			Predicate a7 = cb.equal(b.get("productId"), ocpm2.get("productId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a10 = cb.equal(b.get("policyType"), ocpm2.get("policyType"));
			effectiveDate2.where(a5, a6, a7, a8, a10);
//				// amendId Max Filter
//				Subquery<Long> amendId = query.subquery(Long.class);
//				Root<EmiMaster> ocpm2 = amendId.from(EmiMaster.class);
//				amendId.select(cb.max(ocpm2.get("amendId")));
//				Predicate a5 = cb.equal( b.get("emiId"),ocpm2.get("emiId"));
//				Predicate a6 = cb.equal( b.get("companyId"),ocpm2.get("companyId"));
//				Predicate a7 = cb.equal( b.get("productId"),ocpm2.get("productId"));
//				Predicate a10 = cb.equal( b.get("policyType"),ocpm2.get("policyType"));
//				amendId.where(a5, a6, a7,a10);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("companyId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n5 = cb.or(n3, n2);
			Predicate n6 = cb.equal(b.get("productId"), productId);
			Predicate n7 = cb.equal(b.get("policyType"), policyType);
//				Predicate n11 = cb.equal(b.get("policyType"),  "99999");
//				Predicate n12 = cb.or(n7,  n11);
			Predicate n9 = cb.equal(b.get("installmentPeriod"), insPeriod);
//				Predicate n9 = cb.equal(b.get("installmentTypeId"), insPeriod);
			Predicate n10 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n13 = cb.equal(b.get("status"), "Y");
			query.where(n1, n5, n6, n7, n9, n10, n13).orderBy(orderList);

			// Get Result
			TypedQuery<EmiMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(o -> o.getEmiId() != null).filter(distinctByKey(o -> o.getEmiId()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list;
	}
	// Update Validation

	public List<Error> validateUpdateEmiTransactionDetails(List<EmiTransactionDetailsUpdateReq> reqList) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			HomePositionMaster homeData = homerepo.findByQuoteNo(reqList.get(0).getQuoteNo());
			/*
			 * if(reqList.get(0).getCompanyId().equalsIgnoreCase("100020")) {
			 * errorList=kenyaEmiTransactionDetails.validateUpdateEmiTransactionDetails(
			 * reqList); return errorList; }
			 */
//				if (StringUtils.isBlank(req.getPremiumWithTax())) {
//					errorList.add(new Error("01", "PremiumWithTax", "Please Enter PremiumWithTax "));
//				} 
			int row = 0;
			for (EmiTransactionDetailsUpdateReq req : reqList) {
				row = row + 1;
//				if (StringUtils.isBlank(req.getInstallmentTypeId())) {
//					errorList.add(new Error("02", "InstallmentPeriod", "Please Select Installment type"+row));
//				}
				if (StringUtils.isBlank(req.getNoOfInstallment()) && homeData.getEndtTypeId() == null) {
					errorList.add(new Error("02", "No Of Installment", "Please Enter No Of Installment" + row));
				}
				if (row == 1) {
					if (StringUtils.isNotBlank(req.getQuoteNo()) && StringUtils.isNotBlank(req.getCompanyId()) && homeData.getEndtTypeId() == null) {
						List<EmiTransactionDetails> list = repo
								.findTop1ByQuoteNoAndCompanyIdAndPaymentStatusOrderByDueDateAsc(req.getQuoteNo(),
										req.getCompanyId(), "Pending");

						if (list != null) {
							if (!list.get(0).getInstalment().equals(req.getNoOfInstallment())) {
								errorList.add(new Error("08", "No Of Installment", "Please Enter Installment" + row));
							}
						}
					}
				}
				if (StringUtils.isBlank(req.getQuoteNo())) {
					errorList.add(new Error("03", "QuoteNo", "Please Enter QuoteNo" + row));
				}

//				// Status Validation
//				if (StringUtils.isBlank(req.getStatus())) {
//					errorList.add(new Error("05", "Status", "Please Enter Status"));
//				} else if (req.getStatus().length() > 1) {
//					errorList.add(new Error("05", "Status", "Status 1 Character Only"));
//				} 
				// Payment Staus validation
				else if (!("Paid".equals(req.getPaymentStatus()))) {
					errorList.add(new Error("05", "PaymentStatus", "Please Enter PaymentStatus " + row));
				}

				if (StringUtils.isBlank(req.getCreatedBy())) {
					errorList.add(new Error("07", "CreatedBy", "Please Enter CreatedBy" + row));
				} else if (req.getCreatedBy().length() > 100) {
					errorList.add(new Error("07", "CreatedBy", "Please Enter CreatedBy within 100 Characters" + row));
				}
			}

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	// Update

	public SuccessRes updateEmiTransactionDetails(List<EmiTransactionDetailsUpdateReq> reqList) {
		SuccessRes res = new SuccessRes();
		EmiTransactionDetails saveData = new EmiTransactionDetails();
		List<EmiTransactionDetails> list = new ArrayList<EmiTransactionDetails>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			/*
			 * if(reqList.get(0).getCompanyId().equalsIgnoreCase("100020")) {
			 * res=kenyaEmiTransactionDetails.updateEmiTransactionDetails(reqList); return
			 * res; }
			 */
			String productId = reqList.get(0).getProductId();
			String companyId = reqList.get(0).getCompanyId();
			HomePositionMaster homeData = homerepo.findByQuoteNo(reqList.get(0).getQuoteNo());
//			if (homeData.getEndtTypeId() == null) {
			List<EmiTransactionDetails> list1 = repo.findByQuoteNoAndSelectYn(reqList.get(0).getQuoteNo(), "Y");
			if (list1.size() > 0) {
				for (EmiTransactionDetails req : list1) {
					saveData = dozerMapper.map(req, EmiTransactionDetails.class);
					saveData.setSelectYn("N");
					repo.saveAndFlush(saveData);
				}
			}

//				for (EmiTransactionDetailsUpdateReq req : reqList) {
//					Date entryDate = null;
//					String createdBy = "";
//					String quoteNo = req.getQuoteNo();
//
//					// Update
//					productId = req.getProductId();
//					companyId = req.getCompanyId();
//					CriteriaBuilder cb = em.getCriteriaBuilder();
//					CriteriaQuery<EmiTransactionDetails> query = cb.createQuery(EmiTransactionDetails.class);
//					// Find all
//					Root<EmiTransactionDetails> b = query.from(EmiTransactionDetails.class);
//					// Select
//					query.select(b);
//
//					// Order By
//					List<Order> orderList = new ArrayList<Order>();
//					orderList.add(cb.asc(b.get("instalment")));
//
//					// Where
//					Predicate n1 = cb.equal(b.get("instalment"), req.getNoOfInstallment());
//					Predicate n2 = cb.equal(b.get("productId"), productId);
//					Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId());
//					Predicate n4 = cb.equal(b.get("quoteNo"), quoteNo);
//					query.where(n1, n2, n3, n4).orderBy(orderList);
//
//					// Get Result
//					TypedQuery<EmiTransactionDetails> result = em.createQuery(query);
//					int limit = 0, offset = 2;
//					result.setFirstResult(limit * offset);
//					result.setMaxResults(offset);
//					list = result.getResultList();
//					System.out.println(list.size());
//					System.out.println(list);
//
//					if (list.size() > 0) {
//						entryDate = list.get(0).getEntryDate();
//						createdBy = list.get(0).getCreatedBy();
//						saveData = list.get(0);
//						if (list.size() > 1) {
//							EmiTransactionDetails lastRecord = list.get(1);
//							repo.saveAndFlush(lastRecord);
//						}
//
//					}

//				System.out.println("Before dozzer "+list.get(0).getInstallmentPeriod());
//				dozerMapper.map(req, saveData);
//				System.out.println("After dozzer"+list.get(0).getInstallmentPeriod());
//					saveData.setProductId(productId);
//					saveData.setCreatedBy(createdBy);
//					// saveData.setStatus(list.get(0).getStatus());
//					saveData.setCompanyId(req.getCompanyId());
//					saveData.setEntryDate(entryDate);
//					saveData.setPaymentStatus("Pending");
//					saveData.setPaymentDate(null);
//					saveData.setPaymentDetails(req.getPaymentDetails());
//					saveData.setSelectYn(req.getSelectedYn());
//					saveData.setInstallmentPeriod(list.get(0).getInstallmentPeriod());
//					System.out.println(list.get(0).getInstallmentPeriod());
//					repo.saveAndFlush(saveData);
//					log.info("Saved Details is --> " + json.toJson(saveData));
//
//				}
			
			List<EmiTransactionDetails> saveDataList = repo
					.findByQuoteNoAndCompanyIdAndProductId(reqList.get(0).getQuoteNo(), companyId, productId);
			for (EmiTransactionDetails saveData1 : saveDataList) {
				if(!saveData1.getPaymentStatus().equalsIgnoreCase("Paid")) {
				saveData1.setCompanyId(companyId);
				saveData1.setPaymentStatus("Pending");
				saveData1.setPaymentDate(null);
				saveData1.setPaymentDetails(reqList.get(0).getPaymentDetails());
				saveData1.setSelectYn(reqList.get(0).getSelectedYn());
				saveData1.setInstallmentPeriod(reqList.get(0).getInstallmentPeriod());
				list.add(saveData1);
				}
			}
			repo.saveAllAndFlush(list);

			res.setResponse("Updated Successfully");
			res.setSuccessId(homeData.getQuoteNo());
			// Update Home Position Master
			List<EmiTransactionDetails> list2 = repo
					.findByQuoteNoAndSelectYnOrderByInstalmentDesc(reqList.get(0).getQuoteNo(), "Y");
			System.out.println(list2.size());
			Double getData = list2.stream().filter(o -> o.getSelectYn().equalsIgnoreCase("Y"))
					.mapToDouble(o -> o.getDueAmount().doubleValue()).sum();
			BigDecimal adv = new BigDecimal(getData);

			homeData.setInstallmentPeriod(list.get(0).getInstallmentPeriod());
			homeData.setEmiYn("Y");
			homeData.setNoOfInstallment(list2.get(0).getInstalment());
			homeData.setEmiPremium(adv);
			homerepo.save(homeData);
			CompanyProductMaster product = getCompanyProductMasterDropdown(companyId, productId);
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				EserviceMotorDetails motor = saveMotor("Y", adv, reqList.get(0).getQuoteNo(),
						list.get(0).getInstallmentTypeId(), list2.get(0).getInstalment());
			} else if (product.getMotorYn().equalsIgnoreCase("H") && productId.equalsIgnoreCase(travelProductId)) {
				EserviceTravelDetails travel = saveTravel("Y", adv, list.get(0).getQuoteNo(),
						list.get(0).getInstallmentTypeId(), list2.get(0).getInstalment());
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				EserviceBuildingDetails motor = saveBuilding("Y", adv, list.get(0).getQuoteNo(),
						list.get(0).getInstallmentTypeId(), list2.get(0).getInstalment());
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				EserviceLifeDetails motor = saveLife("Y", adv, list.get(0).getQuoteNo(),
						list.get(0).getInstallmentTypeId(), list2.get(0).getInstalment());
			} else {
				EserviceCommonDetails motor = saveCommon("Y", adv, list.get(0).getQuoteNo(),
						list.get(0).getInstallmentTypeId(), list2.get(0).getInstalment());
			}
//			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	// Get All Emi Transaction Details

	public List<EmiTransactionDetailsRes> getEmiDetailsByQuoteNo(EmiTransactionDetailsGetReq req) {
		List<EmiTransactionDetailsRes> resList = new ArrayList<EmiTransactionDetailsRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		DecimalFormat df = new DecimalFormat("0.00");
		try {
			/*
			 * if(req.getCompanyId().equalsIgnoreCase("100020")) {
			 * resList=kenyaEmiTransactionDetails.getEmiDetailsByQuoteNo(req); return
			 * resList; }
			 */
			String quoteNo = req.getQuoteNo();
			String productId = req.getProductId();
			String requestReferenceNo = req.getRequestReferenceNo();
			List<EmiTransactionDetails> list = new ArrayList<EmiTransactionDetails>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmiTransactionDetails> query = cb.createQuery(EmiTransactionDetails.class);
			// Find all
			Root<EmiTransactionDetails> b = query.from(EmiTransactionDetails.class);
			// Select
			query.select(b);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("instalment")));

			// Where
			Predicate n2 = cb.equal(b.get("productId"), productId);
			Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n4 = cb.equal(b.get("quoteNo"), quoteNo);
			Predicate n5 = cb.equal(b.get("requestReferenceNo"), requestReferenceNo);
			Predicate n6 = b.get("status").in("Y", "E");
			query.where(n2, n3, n4,n5,n6).orderBy(orderList);

			// Get Result
			TypedQuery<EmiTransactionDetails> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream()
					.sorted((o1, o2) -> Long.valueOf(o1.getInstalment()).compareTo(Long.valueOf(o2.getInstalment())))
					.collect(Collectors.toList());
			// Map
			List<EmiTransactionDetails> list1 = new ArrayList<EmiTransactionDetails>();
			list1 = repo.findTop1ByQuoteNoAndPaymentStatusOrderByDueDateAsc(quoteNo, "Pending");

			for (EmiTransactionDetails data : list) {
				EmiTransactionDetailsRes res = new EmiTransactionDetailsRes();
				res = mapper.map(data, EmiTransactionDetailsRes.class);
				res.setInstallment(data.getInstalment());
				res.setDueAmount(data.getDueAmount().toString());
				res.setBalanceAmount(data.getBalanceAmount().toString());
				res.setPaymentDetails(data.getPaymentDetails());
				Optional<List<PaymentDetail>> paymentDetailOP= paymentdetailrepo.findLatestByQuoteNoAndPaymentId(quoteNo,data.getPaymentId());
				if (paymentDetailOP.isPresent() && !paymentDetailOP.get().isEmpty()) {
					PaymentDetail paymentData = paymentDetailOP.get().get(0);
					res.setMerchantReference(
							paymentData.getMerchantReference() == null ? "" : paymentData.getMerchantReference());
					res.setBankName(paymentData.getBankName() == null ? "" : paymentData.getBankName());
					res.setChequeNo(paymentData.getChequeNo() == null ? "" : paymentData.getChequeNo());
					res.setChequeDate(paymentData.getChequeDate() == null ? null : paymentData.getChequeDate());
					res.setAccountNumber(paymentData.getAccountNumber() == null ? "" : paymentData.getAccountNumber());
					res.setIbanNumber(paymentData.getIbanNumber() == null ? "" : paymentData.getIbanNumber());
					res.setPayments(StringUtils.isBlank(paymentData.getPayments()) ? "" : paymentData.getPayments());
					res.setPayeeName(paymentData.getPayeeName() == null ? "" : paymentData.getPayeeName());
					res.setMicrNo(paymentData.getMicrNo() == null ? "" : paymentData.getMicrNo());
					res.setCbcNo(paymentData.getCbcNo() == null ? "" : paymentData.getCbcNo());
					res.setPaymentDate(data.getPaymentDate());
				}
				if (list1 != null && list1.size() > 0) {
					List<EmiTransactionDetails> filter = list1.stream()
							.filter(o -> o.getInstalment().equals(data.getInstalment())).collect(Collectors.toList());
					if (filter.size() > 0) {
						res.setSelectYn("Y");
					} else {
						res.setSelectYn("N");
					}
				}
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return resList;
	}

	// EMI Insatallment Details
	// Validation

	public List<Error> validateEmiInstallmentDetails(EmiInstallmentDetailsReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			/*
			 * if(req.getCompanyId().equalsIgnoreCase("100020")) {
			 * errorList=kenyaEmiTransactionDetails.validateEmiInstallmentDetails(req);
			 * return errorList; }
			 */

			if (StringUtils.isBlank(req.getPremiumWithTax())) {
				errorList.add(new Error("01", "PremiumWithTax", "Please Enter PremiumWithTax "));
			} else if (!req.getPremiumWithTax().matches("[0-9.]+")) {
				errorList.add(new Error("01", "PremiumWithTax", "Please Enter Valid Number In PremiumStart"));
			} else if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			} else if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("03", "ProductId", "Please Enter ProductId"));
			}
			if (StringUtils.isBlank(req.getPolicyType())) {
				errorList.add(new Error("04", "PolicyType", "Please Enter PolicyType"));
			}
			List<EmiMaster> list = new ArrayList<>();
			if (req.getProductId().equalsIgnoreCase("5")) {
				list = getEmiMasterData(req.getCompanyId(), req.getProductId(), req.getPolicyType(),
						Double.parseDouble(req.getPremiumWithTax()));
			} else {
				list = emiMasterRepos.findByCompanyIdAndProductIdAndStatus(req.getCompanyId(),
						Integer.valueOf(req.getProductId()), "Y");
			}
			Integer installmentPeriod = null;
			if (StringUtils.isNotBlank(req.getInstallmentPeriod())) {
				installmentPeriod = Integer.parseInt(req.getInstallmentPeriod());
			} else {
				installmentPeriod = getNoOfMonthsBetweenPolicyPeriod(req);
			}
			UserCustomizedEmiReq customReq = req.getUserCustomizedEmiReq();
			if ((list == null || list.isEmpty()) && (customReq == null)) {
				errorList.add(new Error("05", " ", "No Master Setup For This Company"));
			}
			if (customReq != null) {
				if (StringUtils.isBlank(customReq.getAdvancePercent())) {
					errorList.add(new Error("06", "AdvacePercent", "Please Enter AdvacePercent"));
				}
				if (StringUtils.isBlank(customReq.getInterestPercent())) {
					errorList.add(new Error("07", "InterestPercent", "Please Enter InterestPercent"));
				}
				if (StringUtils.isNotBlank(customReq.getMonthGapYn()) && "Y".equalsIgnoreCase(customReq.getMonthGapYn())
						&& StringUtils.isBlank(customReq.getMonthGap())) {
					errorList.add(new Error("08", "MonthGap", "Please Enter MonthGap"));
				}
				if (StringUtils.isNotBlank(customReq.getCustomizedInstallmentYn())
						&& "Y".equalsIgnoreCase(customReq.getCustomizedInstallmentYn())) {
					List<UserCustomizedInstallmentReq> customizedInstallment = customReq.getCustomizedInstallment();
					if (customizedInstallment == null || customizedInstallment.isEmpty()) {
						errorList.add(new Error("09", "CustomizedInstallment",
								"CustomizedInstallment Cannot Be null or Empty"));
					} else {
						int index = 0;
						boolean ex = false;
						Integer percent = 0;
						for (UserCustomizedInstallmentReq r : customizedInstallment) {
							index += 1;
							if (StringUtils.isBlank(r.getInstallmentPercent())) {
								errorList.add(new Error("10", "InstallmentPercent",
										"Please Enter InstallmentPercent at the index of " + index));
								ex = true;
							}
							if (StringUtils.isBlank(r.getNoOfInstallment())) {
								errorList.add(new Error("11", "NoOfInstallment",
										"Please Enter NoOfInstallment at the index of " + index));
								ex = true;
							}
							if (!ex) {
								percent += Integer.parseInt(r.getInstallmentPercent());
							}
						}
						if (!ex && StringUtils.isNotBlank(customReq.getAdvancePercent())) {
							percent += Integer.parseInt(customReq.getAdvancePercent());
							if (percent > 100 || percent < 100) {
								errorList.add(new Error("12", "Distributed Percentage",
										" sum of AdvancedPercent and InstallmentPercent is " + percent
												+ ". It Should be 100"));
							}
						}
						if (StringUtils.isNotBlank(customReq.getMonthGap())
								&& Integer.parseInt(customReq.getMonthGap()) > 0 && installmentPeriod != null) {
							if (StringUtils.isNotBlank(customReq.getAdvancePercent())
									&& Integer.parseInt(customReq.getAdvancePercent()) > 0) {
								int a = (int) customizedInstallment.size() + 1;
								int b = installmentPeriod / Integer.parseInt(customReq.getMonthGap());
								if (a > b) {
									errorList.add(new Error("12", "CustomizedInstallment",
											" Size of CustomizedInstallment should be " + b + " or less than " + b
													+ ", based on installment period and month Gap"));
								}
							} else if (StringUtils.isNotBlank(customReq.getAdvancePercent())
									&& Integer.parseInt(customReq.getAdvancePercent()) == 0) {
								int a = (int) customizedInstallment.size();
								int b = installmentPeriod / Integer.parseInt(customReq.getMonthGap());
								if (a > b) {
									errorList.add(new Error("12", "CustomizedInstallment",
											" Size of CustomizedInstallment should be " + b + " or less than " + b
													+ ", based on installment period period and month Gap"));
								}
							}
						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return errorList;
	}

//NEW TRIAL

	public static Long DaysToMonthDifference(Integer days) {

		LocalDate today = LocalDate.now();
		LocalDate futureDate = today.plusDays(days);

		long monthsBetween = ChronoUnit.MONTHS.between(today, futureDate);

		System.out.println("Today: " + today);
		System.out.println("Future Date after " + days + " days: " + futureDate);
		System.out.println("Month(s) between: " + monthsBetween);
		return monthsBetween;
	}

	/**
	 * Retrieves a list of EMI installment details based on the provided request.
	 * 
	 * @param req The request object containing the necessary parameters to fetch
	 *            EMI installment details. - companyId: The ID of the company for
	 *            which the EMI details are to be retrieved. - productId: The ID of
	 *            the product associated with the EMI. - policyType: The type of
	 *            policy for which the EMI details are to be retrieved. -
	 *            premiumWithTax: The premium amount including tax. - currency: The
	 *            currency in which the premium is specified. - requestReferenceNo:
	 *            The reference number for the request.
	 * @return A list of {@link EmiDisplayRes} objects containing the EMI
	 *         installment details. If no EMI options are available, a single object
	 *         with a message indicating the unavailability is returned.
	 */

	public List<EmiDisplayRes> viewEmiInstallmentDetails(EmiInstallmentDetailsReq req) {
		List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
		// DecimalFormat df = new DecimalFormat("0.0");
		try {
			/*
			 * if(req.getCompanyId().equalsIgnoreCase("100020")) {
			 * resList=kenyaEmiTransactionDetails.viewEmiInstallmentDetails(req); return
			 * resList; }
			 */
			List<FactorRateRequestDetails> factorlist=factorRateRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			List<TaxSummary> taxList = factorlist.stream()
			      //  .filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
			        .filter(c -> c.getTaxId() != null)
			        .collect(Collectors.groupingBy(FactorRateRequestDetails::getTaxId,
			                Collectors.reducing(BigDecimal.ZERO, c -> c.getTaxAmount() == null ? BigDecimal.ZERO : c.getTaxAmount(), BigDecimal::add)
			        ))
			        .entrySet()
			        .stream()
			        .map(e -> new TaxSummary(e.getKey(), e.getValue()))
			        .collect(Collectors.toList());
			BigDecimal totalPremiumwithoutax = factorlist.stream()
	                .filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
	                .filter(c -> "B".equalsIgnoreCase(c.getCoverageType()) || "O".equalsIgnoreCase(c.getCoverageType()))
	                .map(c -> c.getPremiumExcludedTaxLc() == null ? BigDecimal.ZERO : c.getPremiumExcludedTaxLc())
	                .reduce(BigDecimal.ZERO, BigDecimal::add);
			
			Integer stampDuty =40;
			Integer i = 0;
			String insDesc = "";
			Double temp = 0d, premiumWithTax, interestPercent, advancePercent, interestAmount, totalLoanAmount,
					advanceAmount, balanceAmount = null, installment = 0d, exchangeDate = 0d, curPremium = 0d;
			premiumWithTax = Double.valueOf(req.getPremiumWithTax());
			if (!req.getCurrency().equalsIgnoreCase("ZMW")) {
				List<ExchangeMaster> exchangeData = exchangeMasterRepo.findByCurrencyIdAndCompanyIdOrderByAmendIdDesc(req.getCurrency(),req.getCompanyId());
				if (exchangeData.size() > 0)
					exchangeDate = exchangeData.get(0).getExchangeRate();

				curPremium = exchangeDate * premiumWithTax;
				BigDecimal cp = BigDecimal.valueOf(curPremium).setScale(2, RoundingMode.HALF_UP);
				premiumWithTax = cp.doubleValue();

			}
			Long yeardaysBetween = 0l;
			Integer noOfMonth = 0;
			Integer subNoOfMonth = 0;
			Integer policyPeriod = 0;
			List<CompanyProductMaster> cpm = companyProductMasterRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(req.getCompanyId() ,Integer.parseInt(req.getProductId()));

			if (cpm.get(0).getMotorYn().equalsIgnoreCase("A")) {
				List<EserviceBuildingDetails> buildingDetails = buildingRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!buildingDetails.isEmpty()) {
//							 policyPeriod=buildingDetails.get(0).getPolicyPeriord();							 
//							 noOfMonth=DaysToMonthDifference(policyPeriod).intValue();
					LocalDate policyStartDate = buildingDetails.get(0).getPolicyStartDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate policyEndDate = buildingDetails.get(0).getPolicyEndDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					Long monthsBetween = ChronoUnit.MONTHS.between(policyStartDate, policyEndDate);
					yeardaysBetween = ChronoUnit.DAYS.between(policyStartDate, policyEndDate);
					yeardaysBetween += 1L;
					if (policyEndDate.getDayOfMonth() > policyStartDate.getDayOfMonth()) {
						monthsBetween += 1; // Add 1 month since the difference missed the last full month
					} else if (policyEndDate.getDayOfMonth() < policyStartDate.getDayOfMonth()) {
						monthsBetween += 1;
					}
					subNoOfMonth = monthsBetween.intValue();
				}
			} else if (cpm.get(0).getMotorYn().equalsIgnoreCase("H")) {
				List<EserviceCommonDetails> commonDetails = commonRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!commonDetails.isEmpty()) {
//							 policyPeriod=commonDetails.get(0).getPolicyPeriod();
//							 noOfMonth=DaysToMonthDifference(policyPeriod).intValue();
					LocalDate policyStartDate = commonDetails.get(0).getPolicyStartDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate policyEndDate = commonDetails.get(0).getPolicyEndDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					Long monthsBetween = ChronoUnit.MONTHS.between(policyStartDate, policyEndDate);
					 yeardaysBetween = ChronoUnit.DAYS.between(policyStartDate, policyEndDate);
					 yeardaysBetween += 1L;
					if (policyEndDate.getDayOfMonth() > policyStartDate.getDayOfMonth()) {
						monthsBetween += 1; // Add 1 month since the difference missed the last full month
					} else if (policyEndDate.getDayOfMonth() < policyStartDate.getDayOfMonth()) {
						monthsBetween += 1;
					}
					subNoOfMonth = monthsBetween.intValue();
				}
			} else if (cpm.get(0).getMotorYn().equalsIgnoreCase("M")) {
				List<EserviceMotorDetails> motorDetails = motorRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!motorDetails.isEmpty()) {
//							 policyPeriod=Integer.parseInt(motorDetails.get(0).getPeriodOfInsurance());
//							 noOfMonth=DaysToMonthDifference(policyPeriod).intValue();
					LocalDate policyStartDate = motorDetails.get(0).getPolicyStartDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate policyEndDate = motorDetails.get(0).getPolicyEndDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					Long monthsBetween = ChronoUnit.MONTHS.between(policyStartDate, policyEndDate);
					yeardaysBetween = ChronoUnit.DAYS.between(policyStartDate, policyEndDate);
					yeardaysBetween += 1L;
					if (policyEndDate.getDayOfMonth() > policyStartDate.getDayOfMonth()) {
						monthsBetween += 1; // Add 1 month since the difference missed the last full month
					} else if (policyEndDate.getDayOfMonth() < policyStartDate.getDayOfMonth()) {
						monthsBetween += 1;
					}
					subNoOfMonth = monthsBetween.intValue();
				}
			}
			List<EmiMaster> list = new ArrayList<>();
			if (req.getProductId().equalsIgnoreCase("5")) {
				list = getEmiMasterData(req.getCompanyId(), req.getProductId(), req.getPolicyType(),
						Double.parseDouble(req.getPremiumWithTax()));
			} else {
				list = emiMasterRepos.findByCompanyIdAndProductIdAndStatus(req.getCompanyId(),
						Integer.valueOf(req.getProductId()), "Y");
			}
			list = list.stream().sorted(
					Comparator.comparingLong((EmiMaster o) -> Long.parseLong(o.getInstallmentTypeId())).reversed())
					.collect(Collectors.toList());
			EmiDisplayRes res = null;
			if ("Y".equalsIgnoreCase(req.getUserOption())) {

				EmiMaster data = new EmiMaster();
				UserCustomizedEmiReq customReq = req.getUserCustomizedEmiReq();
				noOfMonth = subNoOfMonth;
				interestPercent = Double.valueOf(customReq.getInterestPercent());
				advancePercent = Double.valueOf(customReq.getAdvancePercent());
				Integer instalId;
				if (StringUtils.isNotBlank(req.getInstallmentPeriod()) && yeardaysBetween > 270) {
					noOfMonth = Integer.parseInt(req.getInstallmentPeriod());
				}
				if ("Y".equalsIgnoreCase(customReq.getMonthGapYn())) {
					instalId = Integer.parseInt(customReq.getMonthGap());
					data.setInstallmentTypeDesc(noOfMonth + " months (Gap Period: " + instalId + ")");
				} else {
					instalId = 1;
					data.setInstallmentTypeDesc(noOfMonth + " months");
				}
				data.setInstallmentTypeId(instalId.toString());
				data.setPremiumStart("1");
				data.setPremiumEnd("999999999");
				res = new EmiDisplayRes();
				if ("Y".equalsIgnoreCase(customReq.getCustomizedInstallmentYn())) {

					List<EmiDisplayRes> result = viewEmiInstallmentDetailsByInstalId6(req, interestPercent,
							advancePercent, premiumWithTax, instalId, res, data, noOfMonth, stampDuty);
					if (!result.isEmpty()) {
						resList.add(result.get(0));
					}

				} else if (req.getCompanyId().equalsIgnoreCase("100050")) {
					List<EmiDisplayRes> result = datebasedViewEmiInstallmentDetailsByInstalId(req, interestPercent,
							advancePercent, premiumWithTax, instalId, res, data, noOfMonth, stampDuty, yeardaysBetween);
					if (!result.isEmpty()) {
						resList.add(result.get(0));
					}
				} else {
					List<EmiDisplayRes> result = new ArrayList<>();
					if(yeardaysBetween > 270) {
					result = viewEmiInstallmentDetailsByInstalId5(req, interestPercent,
							advancePercent, premiumWithTax, instalId, res, data, noOfMonth, stampDuty, yeardaysBetween,taxList,totalPremiumwithoutax);
					} else {
					
						result  = viewDynamicEmiInstallmentDetails(req, interestPercent,
							advancePercent, premiumWithTax, instalId, res, data, noOfMonth, stampDuty, yeardaysBetween,taxList,totalPremiumwithoutax);
					}
					if (!result.isEmpty()) {
						resList.add(result.get(0));
					}
				}

			} else {

				if (!list.isEmpty()) {
					for (EmiMaster data : list) {
						noOfMonth = subNoOfMonth;
						interestPercent = Double.valueOf(data.getInterestPercent().toString());
						advancePercent = Double.valueOf(data.getAdvancePercent().toString());
						Integer instalId = Integer.parseInt(data.getInstallmentTypeId());
						if (yeardaysBetween > 364) {
							noOfMonth = Integer.parseInt(data.getInstallmentPeriod());
							instalId = 1;
						}

						res = new EmiDisplayRes();
						if (req.getCompanyId().equalsIgnoreCase("100050")) {
							List<EmiDisplayRes> result = datebasedViewEmiInstallmentDetailsByInstalId(req,
									interestPercent, advancePercent, premiumWithTax, instalId, res, data, noOfMonth,
									stampDuty, yeardaysBetween);
							if (!result.isEmpty()) {
								resList.add(result.get(0));
							}
						} else {
							List<EmiDisplayRes> result = new ArrayList<>();
							if(yeardaysBetween < 364) {
								
								result  = viewDynamicEmiInstallmentDetails(req, interestPercent,
									advancePercent, premiumWithTax, instalId, res, data, noOfMonth, stampDuty, yeardaysBetween,taxList,totalPremiumwithoutax);
							} else {				
								result = viewEmiInstallmentDetailsByInstalId5(req, interestPercent,
										advancePercent, premiumWithTax, instalId, res, data, noOfMonth, stampDuty, yeardaysBetween,taxList,totalPremiumwithoutax);
							}
							if (!result.isEmpty()) {
								resList.add(result.get(0));
							}
						}
					}

				} else if (list.size() == 0) {
					res = new EmiDisplayRes();
					res.setEmiYn("N");
					res.setEmiYnDesc("Emi Option is not Available ");
					resList.add(res);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return resList;
	}

	/**
	 * Calculates and retrieves a list of EMI installment details based on the
	 * provided parameters.
	 * 
	 * @param req               The request object containing the necessary
	 *                          parameters to fetch EMI installment details.
	 * @param interestPercent   The interest percentage applicable to the EMI.
	 * @param advancePercent    The advance percentage applicable to the EMI.
	 * @param premiumWithTax    The premium amount including tax.
	 * @param instalId          The installment type identifier.
	 * @param res               The response object to be populated with EMI
	 *                          details.
	 * @param data              The EMI master data used for calculations.
	 * @param installmentPeriod The total period for which installments are
	 *                          calculated.
	 * @return A list of {@link EmiDisplayRes} objects containing the EMI
	 *         installment details. If the installment period is not valid, an empty
	 *         list is returned.
	 */
	/*
	 * public List<EmiDisplayRes>
	 * viewEmiInstallmentDetailsByInstalId5(EmiInstallmentDetailsReq req, Double
	 * interestPercent, Double advancePercent,Double premiumWithTax, Integer
	 * instalId,EmiDisplayRes res,EmiMaster data, Integer installmentPeriod, Integer
	 * stampDuty) { List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
	 * Double balanceAmount=null, temp=0d; Double
	 * installment=0d,trackTotLnAmtWithInterest=0d, trackInsAmt=0d; Integer i=0;
	 * String insDesc = ""; Integer in=0; Integer loop=installmentPeriod/instalId,
	 * loop2=installmentPeriod/instalId; Double skipAmount=0d; Double
	 * totalLoanAmount=premiumWithTax+premiumWithTax*interestPercent/100;
	 * if(interestPercent>0) { BigDecimal bd = BigDecimal.valueOf(totalLoanAmount);
	 * bd = bd.setScale(2, RoundingMode.HALF_UP);
	 * 
	 * totalLoanAmount = bd.doubleValue(); } Double interestAmount =
	 * premiumWithTax*interestPercent/100; BigDecimal ia =
	 * BigDecimal.valueOf(premiumWithTax).multiply(BigDecimal.valueOf(
	 * interestPercent)).divide(new BigDecimal("100")).setScale(2,
	 * RoundingMode.HALF_UP); Double advanceAmount = totalLoanAmount *
	 * advancePercent / 100;
	 * 
	 * if(!(installmentPeriod/instalId>1) || !(installmentPeriod%instalId==0)) {
	 * return resList; } if(i==0 && advanceAmount>0 ) { advanceAmount =
	 * premiumWithTax * advancePercent / 100; if
	 * (req.getCompanyId().equalsIgnoreCase("100020")) { advanceAmount =
	 * advanceAmount + stampDuty; } totalLoanAmount=premiumWithTax-advanceAmount;
	 * totalLoanAmount=totalLoanAmount+totalLoanAmount*interestPercent/100;
	 * BigDecimal bd = BigDecimal.valueOf(totalLoanAmount); bd = bd.setScale(2,
	 * RoundingMode.HALF_UP);
	 * 
	 * totalLoanAmount = bd.doubleValue();
	 * trackTotLnAmtWithInterest=totalLoanAmount; balanceAmount = totalLoanAmount;
	 * in=loop-1; installment =balanceAmount/in;
	 * installment=BigDecimal.valueOf(installment).setScale(2,RoundingMode.HALF_UP).
	 * doubleValue(); insDesc="Advance Amount";
	 * 
	 * }else if(i==0) { advanceAmount=0d; balanceAmount = totalLoanAmount -
	 * advanceAmount; trackTotLnAmtWithInterest=totalLoanAmount; in=loop;
	 * installment = balanceAmount/in;
	 * installment=BigDecimal.valueOf(installment).setScale(2,RoundingMode.HALF_UP).
	 * doubleValue();
	 * 
	 * insDesc="Installment Amount";
	 * 
	 * }else {
	 * 
	 * temp = balanceAmount; temp -= installment; balanceAmount = temp;
	 * insDesc="Installment Amount";
	 * 
	 * } EmiInfoListRes emiInfoListRes = new EmiInfoListRes();
	 * emiInfoListRes.setPremiumWithTax(premiumWithTax.toString());
	 * emiInfoListRes.setNoOfMonth(installmentPeriod.toString());
	 * emiInfoListRes.setInterestAmount(ia.toString());
	 * emiInfoListRes.setAdvanceAmount(advanceAmount.toString());
	 * emiInfoListRes.setBalanceAmount(balanceAmount.toString());
	 * emiInfoListRes.setTotalLoanAmount(totalLoanAmount.toString());
	 * emiInfoListRes.setInstallment(installment.toString());
	 * emiInfoListRes.setInstallmentTypeId(data.getInstallmentTypeId().equals("0")?
	 * StringUtils.isNotBlank(data.getInstallmentPeriod())?"1"+data.
	 * getInstallmentPeriod():"0":data.getInstallmentTypeId());
	 * emiInfoListRes.setInstallmentTypeDesc(data.getInstallmentTypeDesc());
	 * 
	 * res.setEmiInfoRes(emiInfoListRes);
	 * 
	 * EmiCompanyInfoListRes compInfoRes = new EmiCompanyInfoListRes();
	 * compInfoRes.setPremiumStart(data.getPremiumStart().toString());
	 * compInfoRes.setPremiumEnd(data.getPremiumEnd().toString());
	 * compInfoRes.setInterest(interestPercent.toString());
	 * compInfoRes.setAdvance(advancePercent.toString());
	 * res.setCompanyEmiInfo(compInfoRes);
	 * 
	 * List<EmiDisplayListRes> emiPremiumResList = new
	 * ArrayList<EmiDisplayListRes>(); Calendar cal = Calendar.getInstance(); Date
	 * dueDate = cal.getTime(); int desiredDay = cal.get(Calendar.DAY_OF_MONTH); for
	 * (i = 0; i < loop2; i++) { EmiDisplayListRes emiPremiumRes = new
	 * EmiDisplayListRes(); Integer inc=i; if(i==0 && advanceAmount>0.0 ) {
	 * cal.add(Calendar.MONTH, 0); dueDate = cal.getTime();
	 * insDesc="Advance Amount"; //
	 * if(req.getCompanyId().equalsIgnoreCase("100020")) { // advanceAmount =
	 * advanceAmount + stampDuty; // }
	 * emiPremiumRes.setInstallment(advanceAmount.toString()); }else if (i == 0) {
	 * cal.add(Calendar.MONTH, 0); dueDate = cal.getTime();
	 * insDesc="Installment Amount";
	 * emiPremiumRes.setInstallment(installment.toString()); inc=inc+1;
	 * emiPremiumRes.setNoOfInstallment(inc.toString());
	 * 
	 * trackInsAmt+=installment; temp = balanceAmount; temp -= installment;
	 * balanceAmount = temp;
	 * 
	 * } else { // Increment calendar based on the installment period
	 * cal.add(Calendar.MONTH, instalId); int maxDay =
	 * cal.getActualMaximum(Calendar.DAY_OF_MONTH); cal.set(Calendar.DAY_OF_MONTH,
	 * Math.min(desiredDay, maxDay)); dueDate = cal.getTime();
	 * if((trackTotLnAmtWithInterest-trackInsAmt)<installment || i==loop2-1) {
	 * skipAmount=installment-(trackTotLnAmtWithInterest-trackInsAmt); balanceAmount
	 * = installment-skipAmount; BigDecimal ba =
	 * BigDecimal.valueOf(balanceAmount).setScale(2, RoundingMode.HALF_UP);
	 * emiPremiumRes.setInstallment(ba.toString()); }/*else
	 * if((balanceAmount-installment)<12 && (balanceAmount-installment)>0) {
	 * skipAmount=(balanceAmount-installment);
	 * skipAmount=BigDecimal.valueOf(skipAmount).setScale(2,RoundingMode.HALF_UP).
	 * doubleValue(); balanceAmount = installment+skipAmount;
	 * emiPremiumRes.setInstallment(balanceAmount.toString());
	 * 
	 * }
	 */
	/*
	 * else { trackInsAmt+=installment;
	 * emiPremiumRes.setInstallment(installment.toString()); } temp = balanceAmount;
	 * temp -= installment;
	 * temp=BigDecimal.valueOf(temp).setScale(2,RoundingMode.HALF_UP).doubleValue();
	 * balanceAmount = temp;
	 * 
	 * insDesc="Installment Amount"; inc=advanceAmount>0.0?inc:(inc+1);
	 * emiPremiumRes.setNoOfInstallment(inc.toString());
	 * 
	 * }
	 * 
	 * emiPremiumRes.setNoOfInstallment(inc.toString());
	 * emiPremiumRes.setDueDate(dueDate); emiPremiumRes.setInstallmentDesc(insDesc);
	 * emiPremiumResList.add(emiPremiumRes);
	 * 
	 * } res.setEmiPremium(emiPremiumResList); res.setEmiYn("Y");
	 * res.setEmiYnDesc("Emi Data");
	 * 
	 * if(balanceAmount!=null) { resList.add(res); } return resList; }
	 */

	public List<EmiDisplayRes> viewEmiInstallmentDetailsByInstalId5Bk(EmiInstallmentDetailsReq req,
			Double interestPercent, Double advancePercent, Double premiumWithTax, Integer instalId, EmiDisplayRes res,
			EmiMaster data, Integer installmentPeriod, Integer stampDuty, Long daysBetween,List<TaxSummary> taxList,BigDecimal totalPremiumwithoutax ) {
		List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal temp = BigDecimal.ZERO;
		BigDecimal installment = BigDecimal.ZERO;
		BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
		BigDecimal trackInsAmt = BigDecimal.ZERO;
		String taxIds = data.getTaxIds();
		boolean calculateInterest = "Y".equalsIgnoreCase(data.getIntresetOrProposal());
		Set<Integer> taxIdSet = StringUtils.isNotBlank(taxIds)
		        ? Arrays.stream(taxIds.split(","))
		                .map(String::trim)
		                .map(Integer::parseInt)
		                .collect(Collectors.toSet())
		        : Collections.emptySet();
		
		
		BigDecimal advemiTaxAmount = taxList.stream()
		        .filter(t -> t.getTaxId() != null)
		        .filter(t -> taxIdSet.contains(t.getTaxId()))
		        .map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal emiTaxAmount = taxList.stream()
		        .filter(t -> t.getTaxId() != null)
		        .filter(t -> ! taxIdSet.contains(t.getTaxId()))
		        .map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
		BigDecimal premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		premWithTax = totalPremiumwithoutax.add(emiTaxAmount);
		premWithTax1 = totalPremiumwithoutax.add(emiTaxAmount);
		
		Integer i = 0;
		String insDesc = "";
		Integer in = 0;
		Integer loop = installmentPeriod / instalId, loop2 = installmentPeriod / instalId;
		Integer emiDay = data.getEmiAllowedDay();
		BigDecimal skipAmount = null;
		BigDecimal intPerc = BigDecimal.valueOf(interestPercent);
		BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
		BigDecimal totalLoanAmount = premWithTax;
		if (req.getCompanyId().equalsIgnoreCase("100020")) {
			premiumWithTax = premiumWithTax - stampDuty;
			premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		}
		

		BigDecimal interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
		BigDecimal advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
		
		advanceAmount = advanceAmount.add(advemiTaxAmount);

		if (!(installmentPeriod / instalId > 1) || !(installmentPeriod % instalId == 0)) {
			return resList;
		}
		if (i == 0 && calculateInterest) {
		//	advanceAmount = premWithTax1.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			if (calculateInterest && interestPercent > 0) {
			    interestAmount = totalLoanAmount.multiply(intPerc)
			            .divide(new BigDecimal("100"))
			            .setScale(2, RoundingMode.HALF_UP);
			    balanceAmount=totalLoanAmount;
			    trackTotLnAmtWithInterest = totalLoanAmount;
			    in = loop;
			    totalLoanAmount=totalLoanAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
			    totalLoanAmount = totalLoanAmount.add(advanceAmount);
			    advanceAmount = totalLoanAmount.add(interestAmount);
			}else {
			    interestAmount = BigDecimal.ZERO;
			    totalLoanAmount = totalLoanAmount.add(interestAmount);
			}
			installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
			insDesc = "Advance Amount";
			
		}else if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
		//	totalLoanAmount = premWithTax.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100")));
			totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
		//	advanceAmount = premWithTax1.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			if (req.getCompanyId().equalsIgnoreCase("100020")) {
					advanceAmount = advanceAmount.add(new BigDecimal(stampDuty));
				}
				totalLoanAmount = premWithTax.subtract(advanceAmount);
				interestAmount = totalLoanAmount.multiply(intPerc)
				            .divide(new BigDecimal("100"))
				            .setScale(2, RoundingMode.HALF_UP);

				    totalLoanAmount = totalLoanAmount.add(interestAmount);
			//	interestAmount = totalLoanAmount.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				in = loop - 1;
				installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
				insDesc = "Advance Amount";
		}else if (i == 0) {
			advanceAmount = BigDecimal.ZERO;
			balanceAmount = totalLoanAmount;
			trackTotLnAmtWithInterest = totalLoanAmount;
			in = loop;
			installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
			insDesc = "Installment Amount";

		} else {
			temp = balanceAmount;
			temp = temp.subtract(installment);
			balanceAmount = temp;
			insDesc = "Installment Amount";
		}
		EmiInfoListRes emiInfoListRes = new EmiInfoListRes();
		emiInfoListRes.setPremiumWithTax(premWithTax.toString());
		emiInfoListRes.setNoOfMonth(installmentPeriod.toString());
		emiInfoListRes.setInterestAmount(interestAmount.toString());
		emiInfoListRes.setAdvanceAmount(advanceAmount.toString());
		emiInfoListRes.setBalanceAmount(balanceAmount.toString());
		emiInfoListRes.setTotalLoanAmount(totalLoanAmount.toString());
		emiInfoListRes.setInstallment(installment.toString());
		emiInfoListRes.setInstallmentTypeId(data.getInstallmentTypeId().equals("0")
				? StringUtils.isNotBlank(data.getInstallmentPeriod()) ? "1" + data.getInstallmentPeriod() : "0"
				: data.getInstallmentTypeId());
		emiInfoListRes.setInstallmentTypeDesc(data.getInstallmentTypeDesc());

		res.setEmiInfoRes(emiInfoListRes);

		EmiCompanyInfoListRes compInfoRes = new EmiCompanyInfoListRes();
		compInfoRes.setPremiumStart(data.getPremiumStart().toString());
		compInfoRes.setPremiumEnd(data.getPremiumEnd().toString());
		compInfoRes.setInterest(interestPercent.toString());
		compInfoRes.setAdvance(advancePercent.toString());
		res.setCompanyEmiInfo(compInfoRes);

		List<EmiDisplayListRes> emiPremiumResList = new ArrayList<EmiDisplayListRes>();
		Calendar cal = Calendar.getInstance();
		Date dueDate = cal.getTime();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
		if (emiDay != null && emiDay > 0 && emiDay < 32)
			desiredDay = emiDay;
		for (i = 0; i < loop2; i++) {
			EmiDisplayListRes emiPremiumRes = new EmiDisplayListRes();
			Integer inc = i;
			if (i == 0 && calculateInterest) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				insDesc = "Advance Amount";
				emiPremiumRes.setInstallment(advanceAmount.toString());
			} else if (i == 0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				insDesc = "Installment Amount";
				emiPremiumRes.setInstallment(installment.toString());
				inc = inc + 1;
				emiPremiumRes.setNoOfInstallment(inc.toString());

				trackInsAmt = trackInsAmt.add(installment);
				temp = balanceAmount;
				temp = temp.subtract(installment);
				balanceAmount = temp;

			} else {
				// Increment calendar based on the installment period
				cal.add(Calendar.MONTH, instalId);
				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
				dueDate = cal.getTime();
				if (trackTotLnAmtWithInterest.subtract(trackInsAmt).compareTo(installment) < 0 || i == loop2 - 1) {
					skipAmount = installment.subtract(trackTotLnAmtWithInterest.subtract(trackInsAmt));
					balanceAmount = installment.subtract(skipAmount);
					BigDecimal ba = balanceAmount.setScale(2, RoundingMode.HALF_UP);
					emiPremiumRes.setInstallment(ba.toString());
				} else {
					trackInsAmt = trackInsAmt.add(installment);
					emiPremiumRes.setInstallment(installment.toString());
				}
				temp = balanceAmount;
				temp = temp.subtract(installment);
				temp = temp.setScale(2, RoundingMode.HALF_UP);
				balanceAmount = temp;

				insDesc = "Installment Amount";
				inc = advanceAmount.compareTo(BigDecimal.ZERO) > 0 ? inc : (inc + 1);
				emiPremiumRes.setNoOfInstallment(inc.toString());

			}

			emiPremiumRes.setNoOfInstallment(inc.toString());
			emiPremiumRes.setDueDate(dueDate);
			emiPremiumRes.setInstallmentDesc(insDesc);
			emiPremiumResList.add(emiPremiumRes);

		}
		res.setEmiPremium(emiPremiumResList);
		res.setEmiYn("Y");
		res.setEmiYnDesc("Emi Data");

		if (balanceAmount != null) {
			resList.add(res);
		}
		return resList;
	}

	public List<EmiDisplayRes> viewEmiInstallmentDetailsByInstalId5(EmiInstallmentDetailsReq req,
			Double interestPercent, Double advancePercent, Double premiumWithTax, Integer instalId, EmiDisplayRes res,
			EmiMaster data, Integer installmentPeriod, Integer stampDuty, Long daysBetween,List<TaxSummary> taxList,BigDecimal totalPremiumwithoutax ) {
		List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
		boolean calculateInterest = "Y".equalsIgnoreCase(data.getIntresetOrProposal());
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal temp = BigDecimal.ZERO;
		BigDecimal installment = BigDecimal.ZERO;
		BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
		BigDecimal trackInsAmt = BigDecimal.ZERO;
		BigDecimal interestAmount  = BigDecimal.ZERO;
		BigDecimal advanceAmount  = BigDecimal.ZERO;
		BigDecimal totalLoanAmount  = BigDecimal.ZERO;
		BigDecimal advSetUpAmount  = BigDecimal.ZERO;
		String taxIds = data.getTaxIds();
		Integer i = 0;
		String insDesc = "";
		Integer in = 0;
		Integer loop = installmentPeriod / instalId, loop2 = installmentPeriod / instalId;
		Integer emiDay = data.getEmiAllowedDay();
		BigDecimal skipAmount = null;
		BigDecimal intPerc = BigDecimal.valueOf(interestPercent);
		BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
		BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
		BigDecimal premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		
		if(!calculateInterest)
		{
		if (req.getCompanyId().equalsIgnoreCase("100020")) {
			premiumWithTax = premiumWithTax - stampDuty;
			premWithTax1 = BigDecimal.valueOf(premiumWithTax);
		}
		 totalLoanAmount = premWithTax.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100")));
		if (interestPercent > 0) {
			totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
		}
		 interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
		 advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);

		if (!(installmentPeriod / instalId > 1) || !(installmentPeriod % instalId == 0)) {
			return resList;
		}
		if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
			advanceAmount = premWithTax1.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			if (req.getCompanyId().equalsIgnoreCase("100020")) {
				advanceAmount = advanceAmount.add(new BigDecimal(stampDuty));
			}
			totalLoanAmount = premWithTax.subtract(advanceAmount);
			interestAmount = totalLoanAmount.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			totalLoanAmount = totalLoanAmount.add(interestAmount);

			trackTotLnAmtWithInterest = totalLoanAmount;
			balanceAmount = totalLoanAmount;
			in = loop - 1;
			installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
			insDesc = "Advance Amount";

		} else if (i == 0) {
			advanceAmount = BigDecimal.ZERO;
			balanceAmount = totalLoanAmount;
			trackTotLnAmtWithInterest = totalLoanAmount;
			in = loop;
			installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
			insDesc = "Installment Amount";

		} else {

			temp = balanceAmount;
			temp = temp.subtract(installment);
			balanceAmount = temp;
			insDesc = "Installment Amount";

		}
		}
		else
		{
			Set<Integer> taxIdSet = StringUtils.isNotBlank(taxIds)
			        ? Arrays.stream(taxIds.split(","))
			                .map(String::trim)
			                .map(Integer::parseInt)
			                .collect(Collectors.toSet())
			        : Collections.emptySet();
			BigDecimal advemiTaxAmount = taxList.stream()
			        .filter(t -> t.getTaxId() != null)
			        .filter(t -> taxIdSet.contains(t.getTaxId()))
			        .map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
			        .reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal emiTaxAmount = taxList.stream()
			        .filter(t -> t.getTaxId() != null)
			        .filter(t -> ! taxIdSet.contains(t.getTaxId()))
			        .map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
			        .reduce(BigDecimal.ZERO, BigDecimal::add);
			premWithTax = totalPremiumwithoutax.add(emiTaxAmount);
			premWithTax1 = totalPremiumwithoutax.add(emiTaxAmount);
			interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			advanceAmount = premWithTax1.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,RoundingMode.HALF_UP);
			advanceAmount = advanceAmount.add(advemiTaxAmount);
			totalLoanAmount =premWithTax1;
			if (!(installmentPeriod / instalId > 1) || !(installmentPeriod % instalId == 0)) {
				return resList;
			}
			if (i == 0 && calculateInterest) {
				 balanceAmount=totalLoanAmount;
				 trackTotLnAmtWithInterest = totalLoanAmount;
				 advSetUpAmount = totalLoanAmount;
				if (calculateInterest && interestPercent > 0) {
					   	in = loop;
					   	advSetUpAmount=advSetUpAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
					   	totalLoanAmount=totalLoanAmount.subtract(advSetUpAmount);
					   	advSetUpAmount = advSetUpAmount.add(advanceAmount);
					    advanceAmount = advSetUpAmount.add(interestAmount);
					    totalLoanAmount =totalLoanAmount.add(advanceAmount);
					    installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
					    balanceAmount =totalLoanAmount.subtract(advanceAmount);
					}else {
					    interestAmount = BigDecimal.ZERO;
					    totalLoanAmount = totalLoanAmount.add(interestAmount);
					}
					insDesc = "Advance Amount";
					
				}
		}
		EmiInfoListRes emiInfoListRes = new EmiInfoListRes();
		emiInfoListRes.setPremiumWithTax(premWithTax.toString());
		emiInfoListRes.setNoOfMonth(installmentPeriod.toString());
		emiInfoListRes.setInterestAmount(interestAmount.toString());
		emiInfoListRes.setAdvanceAmount(advanceAmount.toString());
		emiInfoListRes.setBalanceAmount(balanceAmount.toString());
		emiInfoListRes.setTotalLoanAmount(totalLoanAmount.toString());
		emiInfoListRes.setInstallment(installment.toString());
		emiInfoListRes.setInstallmentTypeId(data.getInstallmentTypeId().equals("0")
				? StringUtils.isNotBlank(data.getInstallmentPeriod()) ? "1" + data.getInstallmentPeriod() : "0"
				: data.getInstallmentTypeId());
		emiInfoListRes.setInstallmentTypeDesc(data.getInstallmentTypeDesc());

		res.setEmiInfoRes(emiInfoListRes);

		EmiCompanyInfoListRes compInfoRes = new EmiCompanyInfoListRes();
		compInfoRes.setPremiumStart(data.getPremiumStart().toString());
		compInfoRes.setPremiumEnd(data.getPremiumEnd().toString());
		compInfoRes.setInterest(interestPercent.toString());
		compInfoRes.setAdvance(advancePercent.toString());
		res.setCompanyEmiInfo(compInfoRes);

		List<EmiDisplayListRes> emiPremiumResList = new ArrayList<EmiDisplayListRes>();
		Calendar cal = Calendar.getInstance();
		Date dueDate = cal.getTime();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
		if (emiDay != null && emiDay > 0 && emiDay < 32)
			desiredDay = emiDay;
		for (i = 0; i < loop2; i++) {
			EmiDisplayListRes emiPremiumRes = new EmiDisplayListRes();
			Integer inc = i;
			if (i == 0 && calculateInterest) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				insDesc = "Advance Amount";
				emiPremiumRes.setInstallment(advanceAmount.toString());
			}
			else if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				insDesc = "Advance Amount";
				emiPremiumRes.setInstallment(advanceAmount.toString());
			} else if (i == 0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				insDesc = "Installment Amount";
				emiPremiumRes.setInstallment(installment.toString());
				inc = inc + 1;
				emiPremiumRes.setNoOfInstallment(inc.toString());

				trackInsAmt = trackInsAmt.add(installment);
				temp = balanceAmount;
				temp = temp.subtract(installment);
				balanceAmount = temp;

			} else {
				// Increment calendar based on the installment period
				cal.add(Calendar.MONTH, instalId);
				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
				dueDate = cal.getTime();
				if ((trackTotLnAmtWithInterest.subtract(trackInsAmt).compareTo(installment) < 0 || i == loop2 - 1) && !calculateInterest) {
					skipAmount = installment.subtract(trackTotLnAmtWithInterest.subtract(trackInsAmt));
					balanceAmount = installment.subtract(skipAmount);
					BigDecimal ba = balanceAmount.setScale(2, RoundingMode.HALF_UP);
					emiPremiumRes.setInstallment(ba.toString());
				} else {
					trackInsAmt = trackInsAmt.add(installment);
					emiPremiumRes.setInstallment(installment.toString());
				}
				temp = balanceAmount;
				temp = temp.subtract(installment);
				temp = temp.setScale(2, RoundingMode.HALF_UP);
				balanceAmount = temp;
				if (temp.compareTo(BigDecimal.ZERO) <= 0) {
					balanceAmount=BigDecimal.ZERO;
				}

				insDesc = "Installment Amount";
				inc = advanceAmount.compareTo(BigDecimal.ZERO) > 0 ? inc : (inc + 1);
				emiPremiumRes.setNoOfInstallment(inc.toString());

			}

			emiPremiumRes.setNoOfInstallment(inc.toString());
			emiPremiumRes.setDueDate(dueDate);
			emiPremiumRes.setInstallmentDesc(insDesc);
			emiPremiumResList.add(emiPremiumRes);

		}
		res.setEmiPremium(emiPremiumResList);
		res.setEmiYn("Y");
		res.setEmiYnDesc("Emi Data");

		if (balanceAmount != null) {
			resList.add(res);
		}
		return resList;
	}

	public List<EmiDisplayRes> viewDynamicEmiInstallmentDetails(EmiInstallmentDetailsReq req, Double interestPercent,
	        Double advancePercent, Double premiumWithTax, Integer instalId, EmiDisplayRes res, EmiMaster data,
	        Integer installmentPeriod, Integer stampDuty, Long daysBetween, List<TaxSummary> taxList,
			BigDecimal totalPremiumwithoutax) {

		List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
		boolean calculateInterest = "Y".equalsIgnoreCase(data.getIntresetOrProposal());
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal installment = BigDecimal.ZERO;
		BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
		BigDecimal trackInsAmt = BigDecimal.ZERO;
		BigDecimal interestAmount = BigDecimal.ZERO;
		BigDecimal advanceAmount = BigDecimal.ZERO;
		BigDecimal totalLoanAmount = BigDecimal.ZERO;
		BigDecimal advSetUpAmount = BigDecimal.ZERO;
		String taxIds = data.getTaxIds();
		String insDesc = "";
		Integer in = 0;
		Integer loop = installmentPeriod;
		Integer loop2 = installmentPeriod;
		Integer emiDay = data.getEmiAllowedDay();
		BigDecimal skipAmount = null;
		BigDecimal intPerc = BigDecimal.valueOf(interestPercent).divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(installmentPeriod));
		BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
		BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
		BigDecimal premWithTax1 = BigDecimal.valueOf(premiumWithTax);

		// derive real interval-in-months from installment type, NOT from the typeId
		// literal
		Integer intervalMonths = resolveIntervalMonths(data.getInstallmentTypeDesc(), instalId);

		if (!calculateInterest) {

			if (req.getCompanyId() != null && req.getCompanyId().equalsIgnoreCase("100020")) {
				premiumWithTax = premiumWithTax - stampDuty;
				premWithTax1 = BigDecimal.valueOf(premiumWithTax);
			}
			totalLoanAmount = premWithTax
					.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
			if (interestPercent != null && interestPercent > 0) {
				totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
			}
			interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
					.setScale(2, RoundingMode.HALF_UP);
			advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
					.setScale(2, RoundingMode.HALF_UP);
		} else {

			Set<Integer> taxIdSet = StringUtils.isNotBlank(data.getTaxIds())
					? Arrays.stream(data.getTaxIds().split(",")).map(String::trim).map(Integer::parseInt).collect(
							Collectors.toSet())
					: Collections.emptySet();

			BigDecimal advemiTaxAmount = taxList.stream().filter(t -> t.getTaxId() != null)
					.filter(t -> taxIdSet.contains(t.getTaxId()))
					.map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			BigDecimal emiTaxAmount = taxList.stream().filter(t -> t.getTaxId() != null)
					.filter(t -> !taxIdSet.contains(t.getTaxId()))
					.map(t -> t.getTaxAmount() == null ? BigDecimal.ZERO : t.getTaxAmount())
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			premWithTax = totalPremiumwithoutax.add(emiTaxAmount);
			premWithTax1 = totalPremiumwithoutax.add(emiTaxAmount);
			interestAmount = premWithTax1.multiply(intPerc).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
					.setScale(2, RoundingMode.HALF_UP);
			advanceAmount = premWithTax1.multiply(advPerc).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
					.setScale(2, RoundingMode.HALF_UP);
			advanceAmount = advanceAmount.add(advemiTaxAmount);
			totalLoanAmount = premWithTax1;
		}

		Integer numberOfInstallments = installmentPeriod >= intervalMonths ? installmentPeriod / intervalMonths : 0;

		if (numberOfInstallments <= 1) {
			return resList;
		}

		if (!calculateInterest) {
			if (advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				totalLoanAmount = premWithTax.subtract(advanceAmount);
				interestAmount = totalLoanAmount.multiply(intPerc)
						.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
				totalLoanAmount = totalLoanAmount.add(interestAmount);
				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				int remainingInstallments = numberOfInstallments - 1;
				if (remainingInstallments > 0) {
					installment = balanceAmount.divide(BigDecimal.valueOf(remainingInstallments), 2,
							RoundingMode.HALF_UP);
				}

			} else {
				advanceAmount = BigDecimal.ZERO;
				balanceAmount = totalLoanAmount;
				trackTotLnAmtWithInterest = totalLoanAmount;
				installment = balanceAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
			}

		} else {
			balanceAmount = totalLoanAmount;
			trackTotLnAmtWithInterest = totalLoanAmount;
			advSetUpAmount = totalLoanAmount;
			if (interestPercent != null && interestPercent > 0) {
				advSetUpAmount = advSetUpAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2,
						RoundingMode.HALF_UP);
				totalLoanAmount = totalLoanAmount.subtract(advSetUpAmount);
				advSetUpAmount = advSetUpAmount.add(advanceAmount);
				advanceAmount = advSetUpAmount.add(interestAmount);
				totalLoanAmount = totalLoanAmount.add(advanceAmount);
				installment = balanceAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.HALF_UP);
				balanceAmount = totalLoanAmount.subtract(advanceAmount);

			} else {
				interestAmount = BigDecimal.ZERO;
				totalLoanAmount = totalLoanAmount.add(interestAmount);
			}
		}

		EmiInfoListRes emiInfoListRes = new EmiInfoListRes();
		emiInfoListRes.setPremiumWithTax(premWithTax.toString());
		emiInfoListRes.setNoOfMonth(installmentPeriod.toString());
		emiInfoListRes.setInterestAmount(interestAmount.toString());
		emiInfoListRes.setAdvanceAmount(advanceAmount.toString());
		emiInfoListRes.setBalanceAmount(balanceAmount.toString());
		emiInfoListRes.setTotalLoanAmount(totalLoanAmount.toString());
		emiInfoListRes.setInstallment(installment.toString());
		emiInfoListRes.setInstallmentTypeId(data.getInstallmentTypeId().equals("0")
				? StringUtils.isNotBlank(data.getInstallmentPeriod()) ? "1" + data.getInstallmentPeriod() : "0"
				: data.getInstallmentTypeId());
		emiInfoListRes.setInstallmentTypeDesc(data.getInstallmentTypeDesc());
		res.setEmiInfoRes(emiInfoListRes);
		EmiCompanyInfoListRes compInfoRes = new EmiCompanyInfoListRes();
		compInfoRes.setPremiumStart(data.getPremiumStart().toString());
		compInfoRes.setPremiumEnd(data.getPremiumEnd().toString());
		compInfoRes.setInterest(interestPercent.toString());
		compInfoRes.setAdvance(advancePercent.toString());
		res.setCompanyEmiInfo(compInfoRes);

		List<EmiDisplayListRes> emiPremiumResList = new ArrayList<>();
		BigDecimal allocatedAmount = BigDecimal.ZERO;

		// Due-date calc — same pattern as viewEmiInstallmentDetailsByInstalId5, but
		// stepping by
		// intervalMonths instead of instalId (instalId isn't reliably
		// months-per-installment).
		Calendar cal = Calendar.getInstance();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
		if (emiDay != null && emiDay > 0 && emiDay < 32) {
			desiredDay = emiDay;
		}

		for (int i = 0; i < numberOfInstallments; i++) {

			EmiDisplayListRes emiPremiumRes = new EmiDisplayListRes();
			int installmentNo = i + 1;
			BigDecimal currentAmount;

			if (i == 0) {
				cal.add(Calendar.MONTH, 0); // first installment due immediately
			} else {
				cal.add(Calendar.MONTH, intervalMonths);
				int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
			}
			Date dueDate = cal.getTime();

			if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				currentAmount = advanceAmount;
				emiPremiumRes.setInstallmentDesc("Advance Amount");
			} else {
				int normalInstallmentIndex = advanceAmount.compareTo(BigDecimal.ZERO) > 0 ? i : i + 1;
				if (normalInstallmentIndex == numberOfInstallments) {
					currentAmount = totalLoanAmount.subtract(allocatedAmount);
				} else {
					currentAmount = installment;
					allocatedAmount = allocatedAmount.add(currentAmount);
				}
				emiPremiumRes.setInstallmentDesc("Installment Amount");
			}
			emiPremiumRes.setInstallment(currentAmount.setScale(2, RoundingMode.HALF_UP).toString());
			emiPremiumRes.setNoOfInstallment(String.valueOf(installmentNo));
			emiPremiumRes.setDueDate(dueDate);
			emiPremiumResList.add(emiPremiumRes);
		}
		res.setEmiPremium(emiPremiumResList);
		res.setEmiYn("Y");
		res.setEmiYnDesc("Emi Data");
		resList.add(res);

		return resList;
	}

	private Integer resolveIntervalMonths(String installmentTypeDesc, Integer instalId) {
	    if (StringUtils.isBlank(installmentTypeDesc)) {
	        return instalId; // fallback to whatever was passed in
	    }
	    switch (installmentTypeDesc.trim().toLowerCase()) {
	        case "monthly":
	            return 1;
	        case "quarterly":
	            return 3;
	        case "half yearly":
	        case "halfyearly":
	            return 6;
	        case "yearly":
	        case "annual":
	            return 12;
	        default:
	            return instalId;
	    }
	}
	
	
	public List<EmiDisplayRes> viewEmiInstallmentDetailsByInstalId6(EmiInstallmentDetailsReq req,
			Double interestPercent, Double advancePercent, Double premiumWithTax, Integer instalId, EmiDisplayRes res,
			EmiMaster data, Integer installmentPeriod, Integer stampDuty) {
		List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal temp = BigDecimal.ZERO;
		BigDecimal installment = BigDecimal.ZERO;
		BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
		BigDecimal trackInsAmt = BigDecimal.ZERO;

		Integer i = 0;
		String insDesc = "";
		Integer in = 0;
		Integer loop = installmentPeriod / instalId, loop2 = installmentPeriod / instalId;
		BigDecimal skipAmount = null;
		BigDecimal intPerc = BigDecimal.valueOf(interestPercent);
		BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
		BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
		BigDecimal totalLoanAmount = premWithTax.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100")));
		if (interestPercent > 0) {
			totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
		}
		BigDecimal interestAmount = premWithTax.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);
		BigDecimal advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
				RoundingMode.HALF_UP);

		if (!(installmentPeriod / instalId > 1) || !(installmentPeriod % instalId == 0)) {
			return resList;
		}
		if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
			advanceAmount = premWithTax.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
					RoundingMode.HALF_UP);

			if (req.getCompanyId().equalsIgnoreCase("100020")) {
				advanceAmount = advanceAmount.add(new BigDecimal(stampDuty));
			}
			totalLoanAmount = premWithTax.subtract(advanceAmount);
			interestAmount = totalLoanAmount.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
					RoundingMode.HALF_UP);
			totalLoanAmount = totalLoanAmount.add(interestAmount);

			trackTotLnAmtWithInterest = totalLoanAmount;
			balanceAmount = totalLoanAmount;
			in = loop - 1;
			insDesc = "Advance Amount";

		} else if (i == 0) {
			advanceAmount = BigDecimal.ZERO;
			balanceAmount = totalLoanAmount;
			trackTotLnAmtWithInterest = totalLoanAmount;
			in = loop;

			insDesc = "Installment Amount";

		} else {

			temp = balanceAmount;
			temp = temp.subtract(installment);
			balanceAmount = temp;
			insDesc = "Installment Amount";

		}
		EmiInfoListRes emiInfoListRes = new EmiInfoListRes();
		emiInfoListRes.setPremiumWithTax(premiumWithTax.toString());
		emiInfoListRes.setNoOfMonth(installmentPeriod.toString());
		emiInfoListRes.setInterestAmount(interestAmount.toString());
		emiInfoListRes.setAdvanceAmount(advanceAmount.toString());
		emiInfoListRes.setBalanceAmount(balanceAmount.toString());
		emiInfoListRes.setTotalLoanAmount(totalLoanAmount.toString());
//						emiInfoListRes.setInstallment(installment.toString());
		emiInfoListRes.setInstallmentTypeId(data.getInstallmentTypeId().equals("0")
				? StringUtils.isNotBlank(data.getInstallmentPeriod()) ? "1" + data.getInstallmentPeriod() : "0"
				: data.getInstallmentTypeId());
		emiInfoListRes.setInstallmentTypeDesc(data.getInstallmentTypeDesc());

		res.setEmiInfoRes(emiInfoListRes);

		EmiCompanyInfoListRes compInfoRes = new EmiCompanyInfoListRes();
		compInfoRes.setPremiumStart(data.getPremiumStart().toString());
		compInfoRes.setPremiumEnd(data.getPremiumEnd().toString());
		compInfoRes.setInterest(interestPercent.toString());
		compInfoRes.setAdvance(advancePercent.toString());
		res.setCompanyEmiInfo(compInfoRes);

		List<EmiDisplayListRes> emiPremiumResList = new ArrayList<EmiDisplayListRes>();
		Calendar cal = Calendar.getInstance();
		Date dueDate = cal.getTime();
		int desiredDay = cal.get(Calendar.DAY_OF_MONTH);

		UserCustomizedEmiReq customEmiReq = req.getUserCustomizedEmiReq();
		List<UserCustomizedInstallmentReq> customInstallment = customEmiReq.getCustomizedInstallment();
		BigDecimal premWithTaxAndInt = BigDecimal.ZERO;
		for (i = 0; i < 1; i++) {
			// Advanced Amount
			EmiDisplayListRes emiPremiumRes = new EmiDisplayListRes();
			if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				cal.add(Calendar.MONTH, 0);
				dueDate = cal.getTime();
				insDesc = "Advance Amount";
				emiPremiumRes.setNoOfInstallment("0");
				emiPremiumRes.setInstallment(advanceAmount.toString());
				emiPremiumRes.setDueDate(dueDate);
				emiPremiumRes.setInstallmentDesc(insDesc);
				emiPremiumResList.add(emiPremiumRes);
			}
			Integer inc = 0;
			int size = customInstallment.size();
			premWithTaxAndInt = premWithTax.add(interestAmount);
			BigDecimal remPerc = new BigDecimal("100").subtract(advPerc);

			for (int x = 0; x < size; x++) {
				EmiDisplayListRes emiPremiumRes1 = new EmiDisplayListRes();
				UserCustomizedInstallmentReq cus = customInstallment.get(x);
				inc = inc + 1;
				if (x == 0 && advanceAmount.compareTo(BigDecimal.ZERO) == 0) {

					cal.add(Calendar.MONTH, 0);
					dueDate = cal.getTime();
					insDesc = "Installment Amount";
					installment = totalLoanAmount.multiply(new BigDecimal(cus.getInstallmentPercent())).divide(remPerc,
							2, RoundingMode.HALF_UP);
					emiPremiumRes1.setInstallment(installment.toString());
					emiPremiumRes1.setNoOfInstallment(inc.toString());

					trackInsAmt = trackInsAmt.add(installment);
					temp = balanceAmount;
					temp = temp.subtract(installment);
					balanceAmount = temp;

				} else if (x == size - 1) {
					cal.add(Calendar.MONTH, instalId);
					int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
					dueDate = cal.getTime();
					insDesc = "Installment Amount";
					installment = totalLoanAmount.multiply(new BigDecimal(cus.getInstallmentPercent())).divide(remPerc,
							2, RoundingMode.HALF_UP);
					if (installment.compareTo(balanceAmount) == 0) {
						emiPremiumRes1.setInstallment(installment.toString());
					} else {
						emiPremiumRes1.setInstallment(balanceAmount.toString());
					}
					emiPremiumRes1.setNoOfInstallment(inc.toString());

				} else {
					cal.add(Calendar.MONTH, instalId);
					int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
					dueDate = cal.getTime();
					insDesc = "Installment Amount";
					installment = totalLoanAmount.multiply(new BigDecimal(cus.getInstallmentPercent())).divide(remPerc,
							2, RoundingMode.HALF_UP);
					emiPremiumRes1.setInstallment(installment.toString());
					emiPremiumRes1.setNoOfInstallment(inc.toString());

					trackInsAmt = trackInsAmt.add(installment);
					temp = balanceAmount;
					temp = temp.subtract(installment);
					balanceAmount = temp;
				}

				emiPremiumRes1.setNoOfInstallment(inc.toString());
				emiPremiumRes1.setDueDate(dueDate);
				emiPremiumRes1.setInstallmentDesc(insDesc);
				emiPremiumResList.add(emiPremiumRes1);
			}

		}
		res.setEmiPremium(emiPremiumResList);
		res.setEmiYn("Y");
		res.setEmiYnDesc("Emi Data");

		if (balanceAmount != null) {
			resList.add(res);
		}
		return resList;
	}

//END TRIAL	
	/**
	 * Retrieves a list of EMI master data based on the provided parameters.
	 * 
	 * @param companyId  The ID of the company for which the EMI master data is to
	 *                   be retrieved.
	 * @param productId  The ID of the product associated with the EMI.
	 * @param policyType The type of policy for which the EMI master data is to be
	 *                   retrieved.
	 * @param amt        The premium amount used to filter the EMI master data.
	 * @return A list of {@link EmiMaster} objects containing the EMI master data.
	 *         If no data is found, an empty list is returned.
	 */
	public List<EmiMaster> getEmiMasterData(String companyId, String productId, String policyType, Double amt) {
		List<EmiMaster> list = new ArrayList<EmiMaster>();

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
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmiMaster> query = cb.createQuery(EmiMaster.class);

			// Find All
			Root<EmiMaster> b = query.from(EmiMaster.class);

			// Select
			query.select(b);

//				// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<EmiMaster> ocpm1 = effectiveDate.from(EmiMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("emiId"), ocpm1.get("emiId"));
			Predicate a2 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(b.get("productId"), ocpm1.get("productId"));
			Predicate a9 = cb.equal(b.get("policyType"), ocpm1.get("policyType"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4, a9);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<EmiMaster> ocpm2 = effectiveDate2.from(EmiMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a5 = cb.equal(b.get("emiId"), ocpm2.get("emiId"));
			Predicate a6 = cb.equal(b.get("companyId"), ocpm2.get("companyId"));
			Predicate a7 = cb.equal(b.get("productId"), ocpm2.get("productId"));
			Predicate a10 = cb.equal(b.get("policyType"), ocpm2.get("policyType"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5, a6, a7, a8, a10);
//				// AmendI Max Filter
//				
//				Subquery<Long> amendId = query.subquery(Long.class);
//				Root<EmiMaster> ocpm2 = amendId.from(EmiMaster.class);
//				amendId.select(cb.max(ocpm2.get("amendId")));
//				Predicate a5 = cb.equal( b.get("emiId"),ocpm2.get("emiId"));
//				Predicate a6 = cb.equal( b.get("companyId"),ocpm2.get("companyId"));
//				Predicate a7 = cb.equal( b.get("productId"),ocpm2.get("productId"));
//				Predicate a10 = cb.equal( b.get("policyType"),ocpm2.get("policyType"));
//				amendId.where(a5, a6, a7,a10);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("companyId")));

			// Where
			// Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n5 = cb.or(n3, n2);
			Predicate n6 = cb.equal(b.get("productId"), productId);
			Predicate n7 = cb.equal(b.get("policyType"), policyType);
//				Predicate n11 = cb.equal(b.get("policyType"), "99999");
//				Predicate n12 = cb.or(n7, n11);
			Predicate n9 = cb.between(cb.literal(amt).as(Double.class), b.get("premiumStart").as(Double.class),
					b.get("premiumEnd").as(Double.class));
			Predicate n10 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n13 = cb.equal(b.get("status"), "Y");
			query.where(n1, n5, n6, n7, n9, n13, n10).orderBy(orderList);

			// Get Result
			TypedQuery<EmiMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(o -> o.getEmiId() != null).filter(distinctByKey(o -> o.getEmiId()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list;
	}

	// Get Next Emi Transaction Details
	/**
	 * Retrieves the next EMI transaction details for a given quote number.
	 * 
	 * @param req The request object containing the necessary parameters to fetch
	 *            the next EMI details. - quoteNo: The quote number for which the
	 *            EMI details are to be retrieved. - companyId: The ID of the
	 *            company associated with the EMI.
	 * @return A list of {@link EmiTransactionDetailsRes} objects containing the
	 *         next EMI transaction details. If no pending EMI details are found, an
	 *         empty list is returned.
	 */
	public List<EmiTransactionDetailsRes> getNextEmiDetails(EmiTransactionDetailsNextReq req) {
		List<EmiTransactionDetailsRes> resList = new ArrayList<EmiTransactionDetailsRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			/*
			 * if(req.getCompanyId().equalsIgnoreCase("100020")) {
			 * resList=kenyaEmiTransactionDetails.getNextEmiDetails(req); return resList; }
			 */
			String quoteNo = req.getQuoteNo();
			List<EmiTransactionDetails> list = new ArrayList<EmiTransactionDetails>();
			list = repo.findTop1ByQuoteNoAndPaymentStatusOrderByDueDateAsc(quoteNo, "Pending");

			// Map
			for (EmiTransactionDetails data : list) {
				EmiTransactionDetailsRes res = new EmiTransactionDetailsRes();
				res = mapper.map(data, EmiTransactionDetailsRes.class);
				res.setInstallment(data.getInstalment());
				res.setDueAmount((Double.valueOf(Math.round(data.getDueAmount()))).toString());
				res.setBalanceAmount((Double.valueOf(Math.round(data.getBalanceAmount()))).toString());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return resList;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	public SuccessRes getEndorsementEmiDetails(EmiTransactionDetailsSaveReq req) {
		EmiTransactionDetails saveData = new EmiTransactionDetails();
		SuccessRes res = new SuccessRes();
		try {
			HomePositionMaster homeData = homePositionMasterRepos.findByQuoteNo(req.getQuoteNo());
			if ("Y".equalsIgnoreCase(req.getEmiYn()) && StringUtils.isNotBlank(req.getEndtTypeId())) {
				// Finding Old Record
				List<EmiTransactionDetails> list = repo.findByQuoteNoAndCompanyIdAndProductId(req.getQuoteNo(),
						req.getCompanyId(), req.getProductId());
				if (list.size() > 0 && StringUtils.isNotBlank(req.getQuoteNo())) {
					if (StringUtils.isNotBlank(list.get(0).getEndtTypeId())
							&& (!list.get(0).getEndtTypeId().equals(req.getEndtTypeId()))) {
						repo.deleteAll(list);
					} else {
						repo.deleteAll(list);
					}
				}


				List<EmiTransactionDetails> emiList = repo.findByQuoteNoAndCompanyIdAndProductId(homeData.getQuoteNo(),
						req.getCompanyId(), req.getProductId());
				Long pendingMonth = emiList.stream().filter(e -> e.getPaymentStatus().equalsIgnoreCase("Pending"))
						.mapToLong(i -> Long.valueOf(i.getInstalment())).count();
				Double pendingAmt = emiList.stream().filter(e -> e.getPaymentStatus().equalsIgnoreCase("Pending"))
						.mapToDouble(i -> i.getDueAmount().doubleValue()).sum();
				Double paidAmt = emiList.stream().filter(e -> e.getPaymentStatus().equalsIgnoreCase("Paid"))
						.mapToDouble(i -> i.getDueAmount().doubleValue()).sum();
				Double diffAmt = Double.valueOf(homeData.getEndtPremiumLc().toString());
				List<EmiTransactionDetails> emiList2 = repo
						.findTop1ByQuoteNoAndPaymentStatusOrderByDueDateAsc(homeData.getQuoteNo(), "Pending");
				Integer pendingIns = Integer.valueOf(emiList2.get(0).getInstalment());
				Double premium = null;

				if (diffAmt > 0) {
					premium = Math.abs(pendingAmt + diffAmt);
				} else if (diffAmt < 0) {
					premium = Math.abs(pendingAmt - diffAmt);
				}

				String quoteNo = req.getQuoteNo();
				String insDesc = "";
				Date entryDate = new Date();
				String createdBy = req.getCreatedBy();
				Integer i = 0;
				Double temp = 0d, premiumWithTax, interestPercent, interestAmount, totalLoanAmount,
						balanceAmount = null, installment = 0d;

				Integer noOfMonth = Integer.valueOf(homeData.getInstallmentPeriod().toString());

				// Getting Record from Emi Master
				List<EmiMaster> emiMasterData = getEmiMasterDataByInsPeriod(req.getCompanyId(), req.getProductId(),
						req.getPolicyType(), homeData.getInstallmentPeriod());
				interestPercent = Double.valueOf(emiMasterData.get(0).getInterestPercent().toString());

				// Calculation
				for (i = pendingIns; i <= pendingMonth; i++) {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.MONTH, i);
					Date dueDate = cal.getTime();

					premiumWithTax = Double.valueOf(premium);
					interestAmount = premiumWithTax * interestPercent / 100;
					interestAmount = interestAmount / 12;
					interestAmount = interestAmount * noOfMonth;
					totalLoanAmount = premiumWithTax + interestAmount;

					installment = totalLoanAmount / pendingMonth;
					balanceAmount = totalLoanAmount - installment;
					temp = balanceAmount;
					temp -= installment;
					balanceAmount = temp;
					// Save
					saveData.setPremiumWithTax(premiumWithTax);
					saveData.setInstallmentPeriod(noOfMonth.toString());
					saveData.setInterest(interestPercent);
					saveData.setInterestAmount((Double.valueOf(Math.round(interestAmount))));
					saveData.setDueAmount((Double.valueOf(Math.round(installment))));
					insDesc = "Installment Amount";
					saveData.setStatus("Y");
					saveData.setPaymentDetails(null);
					saveData.setPaymentDate(null);
					saveData.setPaymentStatus("Pending");
					saveData.setQuoteNo(quoteNo);
					saveData.setProductId(req.getProductId());
					saveData.setCompanyId(req.getCompanyId());
					saveData.setBalanceAmount(Double.valueOf(Math.round(balanceAmount)));
					saveData.setTotalLoanAmount(Double.valueOf(Math.round(totalLoanAmount)));
					saveData.setInstallmentDesc(insDesc);
					saveData.setInstalment(i.toString());
					saveData.setEntryDate(entryDate);
					saveData.setCreatedBy(req.getCreatedBy());
					saveData.setUpdatedDate(new Date());
					saveData.setUpdatedBy(createdBy);
					saveData.setDueDate(dueDate);

					// Endorsement Changes
					if (!(req.getEndtTypeId() == null || req.getEndtTypeId().equalsIgnoreCase("0"))) {
						saveData.setOriginalPolicyNo(req.getOriginalPolicyNo());
						saveData.setEndtDate(req.getEndtDate());
						saveData.setEndorsementRemarks(req.getEndorsementRemarks());
						saveData.setEndorsementEffdate(req.getEndorsementEffdate());
						saveData.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
						saveData.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
						saveData.setEndtCount(req.getEndtCount());
						saveData.setEndtStatus(req.getEndtStatus());
						saveData.setIsFinacialEndt(req.getIsFinacialEndt());
						saveData.setEndtCategDesc(req.getEndtCategDesc());
						saveData.setEndtTypeId(req.getEndtTypeId());
						saveData.setEndtTypeDesc(req.getEndtTypeDesc());
						saveData.setEndtPremium(new BigDecimal(req.getEndtPremium()));
						saveData.setEndtPremiumLc(new BigDecimal(req.getEndtPremium()));
					}
					repo.saveAndFlush(saveData);
				}
				res.setSuccessId(quoteNo);
				res.setResponse("Saved Successful");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return res;
	}
	
	public SuccessRes getEndorsementEmiDetails(EmiTransactionDetailsSaveReq req, HomePositionMaster homeData) {
		
		SuccessRes res = new SuccessRes();
		try {
			
			if ("Y".equalsIgnoreCase(req.getStatus()) && StringUtils.isNotBlank(homeData.getEndtTypeId())) {
				// Finding Old Record
				List<EmiTransactionDetails> list = new ArrayList<>();

				List<EmiTransactionDetails> emiList = repo.findByRequestReferenceNoAndCompanyIdAndProductId(req.getRequestReferenceNo(),
						req.getCompanyId(), req.getProductId());
				Long pendingMonth = emiList.stream().filter(e -> e.getPaymentStatus().equalsIgnoreCase("Pending"))
						.mapToLong(i -> Long.valueOf(i.getInstalment())).count();
				Double pendingAmt = emiList.stream().filter(e -> e.getPaymentStatus().equalsIgnoreCase("Pending"))
						.mapToDouble(i -> i.getDueAmount().doubleValue()).sum();
				Double paidAmt = emiList.stream().filter(e -> e.getPaymentStatus().equalsIgnoreCase("Paid"))
						.mapToDouble(i -> i.getDueAmount().doubleValue()).sum();
				Double diffAmt = Double.valueOf(homeData.getEndtPremiumLc().toString()) + Double.valueOf(homeData.getEndtPremiumTax().toString());
				List<EmiTransactionDetails> emiList2 = repo
						.findByRequestReferenceNoAndCompanyIdAndProductIdAndPaymentStatusOrderByInstalmentDesc(
								req.getRequestReferenceNo(), req.getCompanyId(), req.getProductId(), "Pending");
				Integer pendingIns = Integer.valueOf(emiList2.get(0).getInstalment());
				String installmentTypeId = emiList2.get(0).getInstallmentTypeId();
				Double premium = null;
				list = emiList2;

				if (diffAmt > 0) {
					premium = Double.valueOf(homeData.getOverallPremiumLc().toString()) - paidAmt;
					//premium = Math.abs(pendingAmt + diffAmt);
				} else if (diffAmt <= 0) {
					premium = Double.valueOf(homeData.getOverallPremiumLc().toString()) - paidAmt;
				//premium = Math.abs(pendingAmt + diffAmt);
				}

				String quoteNo = req.getQuoteNo();
				String insDesc = "";
				Date entryDate = new Date();
				Double temp = 0d, premiumWithTax, interestPercent, interestAmount, totalLoanAmount,
						balanceAmount = null, installment = 0d;

				Integer noOfMonth = emiList.size();

				// Getting Record from Emi Master
				List<EmiMaster> emiMasterData = null;
				if (req.getProductId().equalsIgnoreCase("5")) {
					emiMasterData = getEmiMasterData(req.getCompanyId(), req.getProductId(), req.getPolicyType(),
							installmentTypeId);
					System.out.println(emiMasterData);
				} else {
					emiMasterData = emiMasterRepos.findByCompanyIdAndProductIdAndStatusAndInstallmentTypeId(
							req.getCompanyId(), Integer.valueOf(req.getProductId()), "Y", installmentTypeId);
				}
				interestPercent = Double.valueOf(emiMasterData.get(0).getInterestPercent().toString());

				// Calculation
				for(EmiTransactionDetails saveData : emiList2) {

					premiumWithTax = Double.valueOf(premium);
					interestAmount = premiumWithTax * interestPercent / 100;
					interestAmount = interestAmount / 12;
					interestAmount = interestAmount * noOfMonth;
					totalLoanAmount = premiumWithTax + interestAmount;

					installment = totalLoanAmount / pendingMonth;
					balanceAmount = totalLoanAmount - installment;
					temp = balanceAmount;
					temp -= installment;
					balanceAmount = temp;
					// Save
					saveData.setPremiumWithTax(premiumWithTax);
					//saveData.setInstallmentPeriod(homeData.getInstallmentPeriod());
					saveData.setInterest(interestPercent);
					saveData.setInterestAmount((Double.valueOf(interestAmount)));
					saveData.setDueAmount((Double.valueOf(installment)));
					insDesc = "Installment Amount";
					saveData.setStatus("E");
					saveData.setPaymentDetails(null);
					saveData.setPaymentDate(null);
					saveData.setPaymentStatus("Pending");
					saveData.setQuoteNo(quoteNo);
					//saveData.setProductId(req.getProductId());
					//saveData.setCompanyId(req.getCompanyId());
					saveData.setBalanceAmount(Double.valueOf(balanceAmount));
					saveData.setTotalLoanAmount(Double.valueOf(totalLoanAmount));
					saveData.setInstallmentDesc(insDesc);
					saveData.setEntryDate(entryDate);
					//saveData.setCreatedBy(req.getCreatedBy());
					saveData.setUpdatedDate(new Date());
					saveData.setUpdatedBy(req.getCreatedBy());
					//saveData.setDueDate(dueDate);
					//saveData.setAdvance(emiMasterData.get(0).getAdvancePercent());
					//saveData.setInstallmentTypeId(installmentTypeId);
					//saveData.setInstallmentTypeDesc(emiMasterData.get(0).getInstallmentTypeDesc());
					//saveData.setRequestReferenceNo(req.getRequestReferenceNo());
					saveData.setPolicyNo(homeData.getPolicyNo());
					saveData.setRemarks(req.getRemarks());
					// Endorsement Changes
					if (!(homeData.getEndtTypeId() == null || homeData.getEndtTypeId().equalsIgnoreCase("0"))) {
						EmiTransactionDetails endPrevemi = repo.findByQuoteNoAndInstalmentAndCompanyIdAndProductId(
								homeData.getEndtPrevQuoteNo(), saveData.getInstalment(), req.getCompanyId(),
								req.getProductId());
						saveData.setOriginalPolicyNo(homeData.getOriginalPolicyNo());
						saveData.setEndtDate(homeData.getEndtDate());
						saveData.setEndorsementRemarks(homeData.getEndorsementRemarks());
						saveData.setEndorsementEffdate(homeData.getEndorsementEffdate());
						saveData.setEndtPrevPolicyNo(homeData.getEndtPrevPolicyNo());
						saveData.setEndtPrevQuoteNo(homeData.getEndtPrevQuoteNo());
						saveData.setEndtCount(homeData.getEndtCount());
						saveData.setEndtStatus(homeData.getEndtStatus());
						saveData.setIsFinacialEndt(req.getIsFinacialEndt());
						saveData.setEndtCategDesc(homeData.getEndtCategDesc());
						saveData.setEndtTypeId(homeData.getEndtTypeId());
						saveData.setEndtTypeDesc(homeData.getEndtTypeDesc());
						saveData.setEndtPremium(homeData.getEndtPremium());
						saveData.setEndtPremiumLc(homeData.getEndtPremium());
						saveData.setEndtBy(req.getCreatedBy());
						if (endPrevemi != null) {
							saveData.setEndtPremiumDiff(installment - endPrevemi.getDueAmount());
						}
					}
					repo.saveAndFlush(saveData);
				}
				List<EmiTransactionDetails> emiPaidList = repo
						.findByRequestReferenceNoAndCompanyIdAndProductIdAndPaymentStatusOrderByInstalmentDesc(
								req.getRequestReferenceNo(), req.getCompanyId(), req.getProductId(), "Paid");
				for(EmiTransactionDetails emi : emiPaidList) {
					emi.setQuoteNo(quoteNo);
					emi.setStatus("E");
					emi.setPolicyNo(homeData.getPolicyNo());
					emi.setRemarks(req.getRemarks());
					if (!(homeData.getEndtTypeId() == null || homeData.getEndtTypeId().equalsIgnoreCase("0"))) {
						emi.setOriginalPolicyNo(homeData.getOriginalPolicyNo());
						emi.setEndtDate(homeData.getEndtDate());
						emi.setEndorsementRemarks(homeData.getEndorsementRemarks());
						emi.setEndorsementEffdate(homeData.getEndorsementEffdate());
						emi.setEndtPrevPolicyNo(homeData.getEndtPrevPolicyNo());
						emi.setEndtPrevQuoteNo(homeData.getEndtPrevQuoteNo());
						emi.setEndtCount(homeData.getEndtCount());
						emi.setEndtStatus(homeData.getEndtStatus());
						emi.setIsFinacialEndt(req.getIsFinacialEndt());
						emi.setEndtCategDesc(homeData.getEndtCategDesc());
						emi.setEndtTypeId(homeData.getEndtTypeId());
						emi.setEndtTypeDesc(homeData.getEndtTypeDesc());
						emi.setEndtPremium(homeData.getEndtPremium());
						emi.setEndtPremiumLc(homeData.getEndtPremium());
						emi.setEndtBy(req.getCreatedBy());
					}
					repo.saveAndFlush(emi);
				}
				res.setSuccessId(quoteNo);
				res.setResponse("Saved Successful");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}

		return res;
	}

	public void sendSmsEmail(EmiDataRequest req) {

		boolean sms = true, mail = true;
		// String remarks="",currenctStatus="",currentStatusCode="";
		try {

			if (StringUtils.isBlank(req.getMobileno())) {
				// remarks="Mobile Not Available";
				// currentStatusCode="ACV";
				// currenctStatus="ADMIN-CALL-ALLIANCE";
				sms = false;
			} else if (StringUtils.isBlank(req.getMobileno())) {
				// remarks="Email Not Available";
				mail = false;
			}
			if (sms || mail) {
				Calendar calend = Calendar.getInstance();
				calend.setTime(new Date());
				calend.add(Calendar.DATE, 1);
				NotifTransactionDetails nt = NotifTransactionDetails.builder().brokerCompanyName(req.getCustomerName())
						.brokerMailId(req.getEmail()).companyName(req.getCompanyName())
						.customerPhoneCode(Integer.parseInt(req.getMobileCode()))
						.customerPhoneNo(req.getMobileno() == null ? null : new BigDecimal(req.getMobileno()))
						.customerMailid(req.getEmail()).customerName(req.getCustomerName()).entryDate(new Date())
						.notifcationPushDate(new Date()).notifcationEndDate(calend.getTime()).regNo(req.getDueAmount())
						.expiryDate(req.getDueDate())
						// .notifDescription(tempPassword)
						.notifNo(Instant.now().toEpochMilli())
						// .notifNo(null)
						.notifPriority(1).notifPushedStatus("P").notifTemplatename("EMI_NOTIFICATION1")
						.productName("Common")
						// .tinyUrl(n.getTinyUrl())
						.companyid(req.getCompanyId()).productid(99999)
						// .companyLogo(cm.getCompanyLogo())
						// .companyAddress(cm.getCompanyAddress())
						.tinyUrlActive("N")
						// .tinyGroupId(tinyGroupId)
						.build();
				NotifTransactionDetails sv = notifTrans.save(nt);
				List<NotifTransactionDetails> text = new LinkedList<NotifTransactionDetails>();
				text.add(sv);
				notificationService.jobProcess(text);
				// currentStatusCode="ASS";
				// currenctStatus="ALLIANCE-SMS-SENT";
			}
		} catch (Exception e) {
			e.printStackTrace();
			// currentStatusCode="ASF";
			// currenctStatus="ALLIANCE-SMS-FAILED";
		}
		/*
		 * if(sms) {
		 * updateRenewalStatusAndStage("V",currentStatusCode,currenctStatus,req.
		 * getPolicyNo()); if("Y".equalsIgnoreCase(req.getLastNotifyYN()))
		 * updateNotifyStatus(req.getLastNotifyYN(),req.getPolicyNo()); }
		 * updateCurrentStatus(remarks,req.getPolicyNo());
		 */

	}

	public List<EmiDataRequest> getEmiNotificationRequestList() {
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmiDataRequest> query = cb.createQuery(EmiDataRequest.class);

			Root<EmiTransactionDetails> m = query.from(EmiTransactionDetails.class);
			Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
			Root<PersonalInfo> pi = query.from(PersonalInfo.class);
			// Select
			query.multiselect(m.get("quoteNo").alias("quoteNo"), pi.get("title").alias("title"),
					pi.get("clientName").alias("customerName"), pi.get("mobileCode1").alias("mobileCode"),
					pi.get("mobileNo1").alias("mobileno"), pi.get("email1").alias("email"),
					hpm.get("companyId").alias("companyId"), hpm.get("productId").as(String.class).alias("productCode"),
					hpm.get("sectionId").as(String.class).alias("sectionCode"),
					hpm.get("branchCode").alias("branchCode"), m.get("instalment").alias("instalment"),
					m.get("dueDate").alias("dueDate"), m.get("dueAmount").as(String.class).alias("dueAmount"));

			Predicate n1 = null;
			if ("mysql".equalsIgnoreCase(dataBaseType)) {
				Expression<Integer> dateDiffExpression = cb.function("DATEDIFF", Integer.class, m.get("dueDate"),
						cb.currentDate());
				n1 = cb.equal(dateDiffExpression, 0);
			} else {
				Expression<Long> dateDiffExpression = cb.diff(
						cb.function("TRUNC", Date.class, m.get("dueDate")).as(Long.class),
						cb.function("TRUNC", Date.class, cb.currentDate()).as(Long.class));
				n1 = cb.equal(dateDiffExpression, 0);
			}
			Predicate n2 = cb.equal(m.get("paymentStatus"), "Pending");
			Predicate n3 = cb.equal(m.get("quoteNo"), hpm.get("quoteNo"));
			Predicate n4 = cb.equal(hpm.get("customerId"), pi.get("customerId"));
			Predicate n5 = cb.isNull(hpm.get("endtTypeId"));

			query.where(n1, n2, n3, n4, n5);

			// Get Result
			TypedQuery<EmiDataRequest> result = em.createQuery(query);
			return result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
	}

	public synchronized List<ListItemValue> getInstallmentTypeDesc(String insuranceId, String branchCode,
			String itemType, String ItemCode) {
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
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			Predicate n11 = cb.equal(c.get("itemCode"), ItemCode);
			query.where(n1, n2, n3, n8, n9, n10, n11).orderBy(orderList);
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

	public List<EmiMaster> getEmiMasterData(String companyId, String productId, String policyType, String instalId) {
		List<EmiMaster> list = new ArrayList<EmiMaster>();

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
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EmiMaster> query = cb.createQuery(EmiMaster.class);

			// Find All
			Root<EmiMaster> b = query.from(EmiMaster.class);

			// Select
			query.select(b);

//				// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<EmiMaster> ocpm1 = effectiveDate.from(EmiMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("emiId"), ocpm1.get("emiId"));
			Predicate a2 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(b.get("productId"), ocpm1.get("productId"));
			Predicate a9 = cb.equal(b.get("policyType"), ocpm1.get("policyType"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4, a9);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<EmiMaster> ocpm2 = effectiveDate2.from(EmiMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a5 = cb.equal(b.get("emiId"), ocpm2.get("emiId"));
			Predicate a6 = cb.equal(b.get("companyId"), ocpm2.get("companyId"));
			Predicate a7 = cb.equal(b.get("productId"), ocpm2.get("productId"));
			Predicate a10 = cb.equal(b.get("policyType"), ocpm2.get("policyType"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5, a6, a7, a8, a10);
//				// AmendI Max Filter
//				
//				Subquery<Long> amendId = query.subquery(Long.class);
//				Root<EmiMaster> ocpm2 = amendId.from(EmiMaster.class);
//				amendId.select(cb.max(ocpm2.get("amendId")));
//				Predicate a5 = cb.equal( b.get("emiId"),ocpm2.get("emiId"));
//				Predicate a6 = cb.equal( b.get("companyId"),ocpm2.get("companyId"));
//				Predicate a7 = cb.equal( b.get("productId"),ocpm2.get("productId"));
//				Predicate a10 = cb.equal( b.get("policyType"),ocpm2.get("policyType"));
//				amendId.where(a5, a6, a7,a10);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("companyId")));

			// Where
			// Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n5 = cb.or(n3, n2);
			Predicate n6 = cb.equal(b.get("productId"), productId);
			Predicate n7 = cb.equal(b.get("policyType"), policyType);
//				Predicate n11 = cb.equal(b.get("policyType"), "99999");
//				Predicate n12 = cb.or(n7, n11);
			// Predicate n9 = cb.between(cb.literal(amt).as(Double.class) ,
			// b.get("premiumStart").as(Double.class),
			// b.get("premiumEnd").as(Double.class));
			Predicate n9 = cb.equal(b.get("installmentTypeId"), instalId);
			Predicate n10 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n13 = cb.equal(b.get("status"), "Y");
			query.where(n1, n5, n6, n7, n9, n13, n10).orderBy(orderList);

			// Get Result
			TypedQuery<EmiMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(o -> o.getEmiId() != null).filter(distinctByKey(o -> o.getEmiId()))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list;
	}

	public int getNoOfMonthsBetweenPolicyPeriod(EmiInstallmentDetailsReq req) {
		Integer subNoOfMonth = 0;
		try {
			List<CompanyProductMaster> cpm = companyProductMasterRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(
					req.getCompanyId(), Integer.parseInt(req.getProductId()));

			if (cpm.get(0).getMotorYn().equalsIgnoreCase("A")) {
				List<EserviceBuildingDetails> buildingDetails = buildingRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!buildingDetails.isEmpty()) {
					LocalDate policyStartDate = buildingDetails.get(0).getPolicyStartDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate policyEndDate = buildingDetails.get(0).getPolicyEndDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					Long monthsBetween = ChronoUnit.MONTHS.between(policyStartDate, policyEndDate);
					if (policyEndDate.getDayOfMonth() > policyStartDate.getDayOfMonth()) {
						monthsBetween += 1; // Add 1 month since the difference missed the last full month
					} else if (policyEndDate.getDayOfMonth() < policyStartDate.getDayOfMonth()) {
						monthsBetween += 1;
					}
					subNoOfMonth = monthsBetween.intValue();
				}
			} else if (cpm.get(0).getMotorYn().equalsIgnoreCase("H")) {
				List<EserviceCommonDetails> commonDetails = commonRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!commonDetails.isEmpty()) {
					LocalDate policyStartDate = commonDetails.get(0).getPolicyStartDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate policyEndDate = commonDetails.get(0).getPolicyEndDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					Long monthsBetween = ChronoUnit.MONTHS.between(policyStartDate, policyEndDate);
					if (policyEndDate.getDayOfMonth() > policyStartDate.getDayOfMonth()) {
						monthsBetween += 1; // Add 1 month since the difference missed the last full month
					} else if (policyEndDate.getDayOfMonth() < policyStartDate.getDayOfMonth()) {
						monthsBetween += 1;
					}
					subNoOfMonth = monthsBetween.intValue();
				}
			} else if (cpm.get(0).getMotorYn().equalsIgnoreCase("M")) {
				List<EserviceMotorDetails> motorDetails = motorRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (!motorDetails.isEmpty()) {
					LocalDate policyStartDate = motorDetails.get(0).getPolicyStartDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate policyEndDate = motorDetails.get(0).getPolicyEndDate().toInstant()
							.atZone(ZoneId.systemDefault()).toLocalDate();
					Long monthsBetween = ChronoUnit.MONTHS.between(policyStartDate, policyEndDate);
					if (policyEndDate.getDayOfMonth() > policyStartDate.getDayOfMonth()) {
						monthsBetween += 1; // Add 1 month since the difference missed the last full month
					} else if (policyEndDate.getDayOfMonth() < policyStartDate.getDayOfMonth()) {
						monthsBetween += 1;
					}
					subNoOfMonth = monthsBetween.intValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return subNoOfMonth;
	}

	public List<EmiDisplayRes> datebasedViewEmiInstallmentDetailsByInstalId(EmiInstallmentDetailsReq req,
			Double interestPercent, Double advancePercent, Double premiumWithTax, Integer instalId, EmiDisplayRes res,
			EmiMaster data, Integer installmentPeriod, Integer stampDuty, Long yeardaysBetween) {
		List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
		try {
			BigDecimal balanceAmount = BigDecimal.ZERO;
			BigDecimal temp = BigDecimal.ZERO;
			BigDecimal installment = BigDecimal.ZERO;
			//BigDecimal oneDayPremium = BigDecimal.ZERO;
			BigDecimal trackTotLnAmtWithInterest = BigDecimal.ZERO;
			BigDecimal trackInsAmt = BigDecimal.ZERO;
			int daysBetween = 0;

			BigDecimal skipAmount = null;

			Calendar cal = Calendar.getInstance();
			Date dueDate = cal.getTime();
			Integer emiDay = data.getEmiAllowedDay();
			int desiredDay = cal.get(Calendar.DAY_OF_MONTH);
			int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			BigDecimal firstInstallment = BigDecimal.ZERO;

			if (emiDay != null && emiDay > 0 && emiDay < 32)
				desiredDay = emiDay;
			if (emiDay != null) {
				Calendar emiCal = (Calendar) cal.clone();
				emiCal.add(Calendar.MONTH, emiDay);
				cal.add(Calendar.DATE, emiDay);
				emiCal.set(Calendar.DAY_OF_MONTH, emiDay);
				Date emiDate = emiCal.getTime();
				long diffMillis = emiDate.getTime() - dueDate.getTime();
				daysBetween = (int) (diffMillis / (1000 * 60 * 60 * 24));
				installmentPeriod = installmentPeriod - 1;
			}

			Integer i = 0;
			String insDesc = "";
			Integer in = 0;
			
			Integer loop = installmentPeriod / instalId, loop2 = installmentPeriod / instalId;
			BigDecimal intPerc = BigDecimal.valueOf(interestPercent);
			BigDecimal advPerc = BigDecimal.valueOf(advancePercent);
			BigDecimal premWithTax = BigDecimal.valueOf(premiumWithTax);
			BigDecimal oneDayPre = premWithTax.divide(new BigDecimal(yeardaysBetween), 2, RoundingMode.HALF_UP);
			BigDecimal totalLoanAmount = premWithTax.add(premWithTax.multiply(intPerc).divide(new BigDecimal("100")));
			if (interestPercent > 0) {
				totalLoanAmount = totalLoanAmount.setScale(2, RoundingMode.HALF_UP);
			}
			BigDecimal interestAmount = premWithTax.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
					RoundingMode.HALF_UP);
			BigDecimal advanceAmount = totalLoanAmount.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
					RoundingMode.HALF_UP);

			if (!(installmentPeriod / instalId > 1) || !(installmentPeriod % instalId == 0)) {
				return resList;
			}
			if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
				advanceAmount = premWithTax.multiply(advPerc).divide(new BigDecimal("100")).setScale(2,
						RoundingMode.HALF_UP);
				if (req.getCompanyId().equalsIgnoreCase("100020")) {
					advanceAmount = advanceAmount.add(new BigDecimal(stampDuty));
				}
				totalLoanAmount = premWithTax.subtract(advanceAmount);
				interestAmount = totalLoanAmount.multiply(intPerc).divide(new BigDecimal("100")).setScale(2,
						RoundingMode.HALF_UP);
				totalLoanAmount = totalLoanAmount.add(interestAmount);

				trackTotLnAmtWithInterest = totalLoanAmount;
				balanceAmount = totalLoanAmount;
				in = loop - 1;
				installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
				insDesc = "Advance Amount";

			} else if (i == 0) {
				advanceAmount = BigDecimal.ZERO;
				balanceAmount = totalLoanAmount;
				trackTotLnAmtWithInterest = totalLoanAmount;
				in = loop;
				installment = balanceAmount.divide(new BigDecimal(in), 2, RoundingMode.HALF_UP);
				//oneDayPremium = installment.divide(new BigDecimal(yeardaysBetween), 2, RoundingMode.HALF_UP);
				insDesc = "Installment Amount";

			} else {

				temp = balanceAmount;
				temp = temp.subtract(installment);
				balanceAmount = temp;
				insDesc = "Installment Amount";

			}
			EmiInfoListRes emiInfoListRes = new EmiInfoListRes();
			emiInfoListRes.setPremiumWithTax(premiumWithTax.toString());
			emiInfoListRes.setNoOfMonth(installmentPeriod.toString());
			emiInfoListRes.setInterestAmount(interestAmount.toString());
			emiInfoListRes.setAdvanceAmount(advanceAmount.toString());
			emiInfoListRes.setBalanceAmount(balanceAmount.toString());
			emiInfoListRes.setTotalLoanAmount(totalLoanAmount.toString());
			emiInfoListRes.setInstallment(installment.toString());
			emiInfoListRes.setInstallmentTypeId(data.getInstallmentTypeId().equals("0")
					? StringUtils.isNotBlank(data.getInstallmentPeriod()) ? "1" + data.getInstallmentPeriod() : "0"
					: data.getInstallmentTypeId());
			emiInfoListRes.setInstallmentTypeDesc(data.getInstallmentTypeDesc());

			res.setEmiInfoRes(emiInfoListRes);

			EmiCompanyInfoListRes compInfoRes = new EmiCompanyInfoListRes();
			compInfoRes.setPremiumStart(data.getPremiumStart().toString());
			compInfoRes.setPremiumEnd(data.getPremiumEnd().toString());
			compInfoRes.setInterest(interestPercent.toString());
			compInfoRes.setAdvance(advancePercent.toString());
			res.setCompanyEmiInfo(compInfoRes);

			List<EmiDisplayListRes> emiPremiumResList = new ArrayList<EmiDisplayListRes>();

			for (i = 0; i < loop2; i++) {
				EmiDisplayListRes emiPremiumRes = new EmiDisplayListRes();
				Integer inc = i;
				if (i == 0 && advanceAmount.compareTo(BigDecimal.ZERO) > 0) {
					cal.add(Calendar.MONTH, 0);
					dueDate = cal.getTime();
					insDesc = "Advance Amount";
					emiPremiumRes.setInstallment(advanceAmount.toString());
				} else if (i == 0) {

					insDesc = "Installment Amount";
					if (emiDay != null && daysBetween != 0) {
						//cal.add(Calendar.MONTH, 0);
						//dueDate = cal.getTime();
						firstInstallment = installment;
						firstInstallment = oneDayPre.multiply(new BigDecimal(daysInMonth)).setScale(2,
								RoundingMode.HALF_UP);
						firstInstallment = firstInstallment.add(installment);
						emiPremiumRes.setInstallment(firstInstallment.toString());
						trackTotLnAmtWithInterest.subtract(firstInstallment);
						inc = inc + 1;
						emiPremiumRes.setNoOfInstallment(inc.toString());

						trackInsAmt = trackInsAmt.add(firstInstallment);
						temp = balanceAmount;
						temp = temp.subtract(firstInstallment);
						balanceAmount = temp;
					} else {
						//cal.add(Calendar.MONTH, 0);
						//dueDate = cal.getTime();
						emiPremiumRes.setInstallment(installment.toString());
						inc = inc + 1;
						emiPremiumRes.setNoOfInstallment(inc.toString());

						trackInsAmt = trackInsAmt.add(installment);
						temp = balanceAmount;
						temp = temp.subtract(installment);
						balanceAmount = temp;
					}

				} else {
					// Increment calendar based on the installment period
					int maxDay = 0;
					if (emiDay != null && daysBetween != 0 && i == 1) {
						cal.add(Calendar.MONTH, instalId + 1);
						 maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
						dueDate = cal.getTime();
					} else {
						cal.add(Calendar.MONTH, instalId);
						maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
						cal.set(Calendar.DAY_OF_MONTH, Math.min(desiredDay, maxDay));
						dueDate = cal.getTime();
					}

					if (trackTotLnAmtWithInterest.subtract(trackInsAmt).compareTo(installment) < 0 || i == loop2 - 1) {
						skipAmount = installment.subtract(trackTotLnAmtWithInterest.subtract(trackInsAmt));
						balanceAmount = installment.subtract(skipAmount);
						BigDecimal ba = balanceAmount.setScale(2, RoundingMode.HALF_UP);
						BigDecimal finalAmount = oneDayPre.multiply(new BigDecimal(maxDay)).setScale(2, RoundingMode.HALF_UP);
						if(ba.compareTo(finalAmount) < 0 && ba.compareTo(balanceAmount) != 0 ) {
							ba = oneDayPre.multiply(new BigDecimal(maxDay)).setScale(2, RoundingMode.HALF_UP);
						}
						
						emiPremiumRes.setInstallment(ba.toString());
					} else {
						trackInsAmt = trackInsAmt.add(installment);
						emiPremiumRes.setInstallment(installment.toString());
					}
					temp = balanceAmount;
					temp = temp.subtract(installment);
					temp = temp.setScale(2, RoundingMode.HALF_UP);
					balanceAmount = temp;

					insDesc = "Installment Amount";
					inc = advanceAmount.compareTo(BigDecimal.ZERO) > 0 ? inc : (inc + 1);
					emiPremiumRes.setNoOfInstallment(inc.toString());

				}
				emiPremiumRes.setNoOfInstallment(inc.toString());
				emiPremiumRes.setDueDate(dueDate);
				emiPremiumRes.setInstallmentDesc(insDesc);
				emiPremiumResList.add(emiPremiumRes);
			}
			res.setEmiPremium(emiPremiumResList);
			res.setEmiYn("Y");
			res.setEmiYnDesc("Emi Data");

			if (balanceAmount != null) {
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return resList;
	}
}
