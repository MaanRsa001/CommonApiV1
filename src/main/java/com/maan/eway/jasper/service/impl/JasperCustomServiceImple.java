package com.maan.eway.jasper.service.impl;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
import com.maan.eway.bean.ApiDocDownloadDetail;
import com.maan.eway.bean.AviationInfo;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.DocumentUniqueDetails;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EngineerInfo;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExcessTransactionDetails;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.FirstLossPayee;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MarineHullInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.bean.ProductGroupMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.TravelPolicyType;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.req.PortFolioGridReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PortFolioAdminTupleRes;
import com.maan.eway.common.res.PortfolioGridRes;
import com.maan.eway.common.service.impl.GridServiceImpl;
import com.maan.eway.jasper.req.JasperScheduleReq;
import com.maan.eway.jasper.req.PremiumReportReq;
import com.maan.eway.jasper.req.TravelPolicyTypeGetReq;
import com.maan.eway.jasper.res.ApiDocListRes;
import com.maan.eway.jasper.res.AttachMentRes;
import com.maan.eway.jasper.res.CollertalRes;
import com.maan.eway.jasper.res.CoverDetailsRes;
import com.maan.eway.jasper.res.CreditDataSetOne;
import com.maan.eway.jasper.res.CreditDataSetTwo;
import com.maan.eway.jasper.res.CreditNoteRes;
import com.maan.eway.jasper.res.InstallmentDetailRes;
import com.maan.eway.jasper.res.MotorCoverNoteRes;
import com.maan.eway.jasper.res.MotorPrivateAccessoriesDetails;
import com.maan.eway.jasper.res.MotorPrivateDriverDetails;
import com.maan.eway.jasper.res.MotorPrivateRes;
import com.maan.eway.jasper.res.MotorPrivateVehicleDetails;
import com.maan.eway.jasper.res.PremiumDetailsWithOuttax;
import com.maan.eway.jasper.res.PremiumReportRes;
import com.maan.eway.jasper.res.TaxDataSetOneRes;
import com.maan.eway.jasper.res.TaxInvoicePremiumDetails;
import com.maan.eway.jasper.res.TaxInvoiceRes;
import com.maan.eway.jasper.res.TearmsAndCondition;
import com.maan.eway.jasper.res.TravelCoverageDetailsRes;
import com.maan.eway.jasper.res.TravelDataSetOneRes;
import com.maan.eway.jasper.res.TravelDataSetTwoRes;
import com.maan.eway.jasper.res.TravelReportRes;
import com.maan.eway.jasper.res.VBasedPremium;
import com.maan.eway.jasper.res.getEmiDetailsListRes;
import com.maan.eway.master.req.EmiInstallmentDetailsReq;
import com.maan.eway.master.req.LovGetReq;
import com.maan.eway.master.res.EmiDisplayListRes;
import com.maan.eway.master.res.EmiDisplayRes;
import com.maan.eway.master.service.EmiTransactionDetailsService;
import com.maan.eway.master.service.impl.KenyaEmiTransactionDetails;
import com.maan.eway.master.service.impl.PhoenixZambiaEmiTransactionDetails;
import com.maan.eway.master.service.impl.PhoenixZambiaEmiTransactionDetailsService;
import com.maan.eway.repository.AviationInfoRepository;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EngineerInfoRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.ExcessMasterRepository;
import com.maan.eway.repository.ExcessTransactionDetailsRepository;
import com.maan.eway.repository.ExclusionMasterRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.FirstLossPayeeRepository;
import com.maan.eway.repository.GroupMedicalDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MarineHullInfoRepo;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorVehicleUsageMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.PolicyDrcrDetailRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TermsAndConditionRepository;
import com.maan.eway.res.DropDownRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Component
public class JasperCustomServiceImple {

	Logger log = LogManager.getLogger(JasperCustomServiceImple.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private MotorDataDetailsRepository motorRepo;

	@Autowired
	private InsuranceCompanyMasterRepository ewayInsuranceCompanyMasterRepo;

	@Autowired
	private EServiceMotorDetailsRepository eservicemotorRepo;

	@Autowired
	private ContentAndRiskRepository conAndRiskRepo;

	@Autowired
	private BuildingDetailsRepository buildingDetRepo;

	@Autowired
	private BuildingRiskDetailsRepository buildingRiskDetailsRepo;

	@Autowired
	private ProductEmployeesDetailsRepository productEmpDetRepo;

	@Autowired
	private PaymentDetailRepository paymentDetailRepo;

	@Autowired
	private ListItemValueRepository listItemValueRepo;

	@Autowired
	private GroupMedicalDetailsRepository groupMedicalDetRepo;

	@Autowired
	private PolicyDrcrDetailRepository drcrdetail;

	@Autowired
	private ExclusionMasterRepository exclusionMasterRepo;

	@Autowired
	private InsuranceCompanyMasterRepository insuranceComMasRepo;

	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingDetailsRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private ListItemValueRepository ewayListItemValueRepo;

	@Autowired
	private PolicyCoverDataRepository coverDataRepository;

	@Autowired
	private FactorRateRequestDetailsRepository factorRateRequestDetailsRepo;

	@Autowired
	private FirstLossPayeeRepository firstLossPayeeRepo;

	@Autowired
	private CommonDataDetailsRepository commonDataDetailsRepo;

	@Autowired
	private ClausesMasterRepository clausesMasterRepo;

	@Autowired
	private ExcessMasterRepository excessRepo;

	@Autowired
	private EngineerInfoRepository engineerRepo;

	@Autowired
	private MarineHullInfoRepo marineHullInfoRepo;

	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingRiskDetailsRepo;

//	@Lazy
//	@Autowired
//	private EmiTransactionDetailsService emitransactionDetails;

	@Autowired
	private MotorVehicleUsageMasterRepository motorVehicleUsageRepo;

	@Autowired
	private AviationInfoRepository aviationInfoRepo;

	@Autowired
	private TermsAndConditionRepository termsConRepo;

	@Autowired
	private ClausesMasterRepository clausesRepo;
	
	@Autowired
	private EmiTransactionDetailsRepository emiRepo;
	
	@Autowired
	private LoginUserInfoRepository logrepo;

	// @Autowired
	// private MultiplePolicyDrCrDetailRepository multiPolicyDrCrDtlRepo;

//	@Autowired
//	private JasperServiceImpl jasperServiceImpl;

	@Autowired
	private SectionDataDetailsRepository sectionDataDetailsRepo;
	
	@Autowired
	private EServiceMotorDetailsRepository esMotorRepo;
	
	@Autowired
	private PersonalInfoRepository piRepo;
	
	@Autowired
	private ExcessTransactionDetailsRepository extransactionRepo;
	
	@Value(value = "${report.image.path}")
	private String externalImagePath;
	
	@Value(value = "${report.file.path}")
    private String policyReportPath;
	
	 @Autowired
	 private Gson gson;
	 
	 @Autowired
	 private GridServiceImpl gridServiceImpl;
	 
		@Autowired
		private KenyaEmiTransactionDetails kenyaEmiTransactionDetails;
		
		@Autowired
		private PhoenixZambiaEmiTransactionDetailsService phoenixZambiaEmiTransactionDetailsService;
		
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private String RenewalDate(String Input) {
		DateTimeFormatter inputformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(Input.substring(0, 19), inputformatter);
		return dateTime.toLocalDate().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	public Object callReport(JasperScheduleReq req) {
		log.info("Enter into callReport");
		Object response = null;
		try {
			HomePositionMaster hpm = homeRepo.findByQuoteNo(req.getQuoteNo());
			if ("1".equalsIgnoreCase(req.getReportId())) { // SCHEDULE
				if (hpm.getProductId() == 5) {
					if ("100004".equalsIgnoreCase(hpm.getCompanyId())) {
						List<Map<String, Object>> resportRes = getMadisonMotorSchedule(hpm.getPolicyNo(), "",hpm);
						response = resportRes;
					} else {
						MotorPrivateRes reportRes = getMotorPrivate(hpm.getPolicyNo(), req.getQuoteNo(), "");
						response = reportRes;
					}
				} else if (hpm.getProductId() == 4) {
					TravelReportRes reportRes = getTravelReport(hpm.getPolicyNo());
					response = reportRes;
				} else if (hpm.getProductId() == 42) {
					Map<String, Object> reportRes = getCyberInsurance(hpm.getPolicyNo());
					response = reportRes;
				} else {
					Map<String, Object> reportRes = getEwaySchedule(req.getQuoteNo());
					response = reportRes;
				}
			} else if ("2".equalsIgnoreCase(req.getReportId())) { // CREDIT NOTE
				CreditNoteRes creditNoteRes = getCreditNoteRes(hpm.getPolicyNo(),hpm);
				response = creditNoteRes;
			} else if ("3".equalsIgnoreCase(req.getReportId())) { // DEBIT NOTE
				TaxInvoiceRes invoiceRes = getTaxInvoiceRes(hpm.getPolicyNo(),hpm);
				response = invoiceRes;
			} else if ("4".equalsIgnoreCase(req.getReportId())) { // BROKER QUOTATION
				Map<String, Object> map = getMotorBrokerQuotation(hpm.getQuoteNo(), req.getTaxShowYn());
				response = map;
			} else if ("5".equalsIgnoreCase(req.getReportId())) { // PREMIUM REGISTER
				PremiumReportRes reportRes = (PremiumReportRes) getPremiumReport(req.getPremiumRegisterReq()).getCommonResponse();
				response = reportRes;
			} else if ("6".equalsIgnoreCase(req.getReportId())) { // ENDORSEMENT PDF
				Map<String, Object> map = getMotorEndorsementSchedule(hpm.getPolicyNo());
				response = map;
			} else if ("7".equalsIgnoreCase(req.getReportId())) { // STICKER PDF
				List<MotorPrivateVehicleDetails> motPrivateRes = getMotorPrivate(hpm.getPolicyNo(), req.getQuoteNo(),
						"").getVehicleDetails();
				response = motPrivateRes;
			} else if ("8".equalsIgnoreCase(req.getReportId())) { // ILLESTRATION PDF
				Map<String, Object> map = getInalipaSchedule(hpm.getPolicyNo());
				response = map;
			}
			log.info("callReport Response ==> " + new Gson().toJson(response));
		} catch (Exception e) {
			log.info("Error in callReport ==> " + e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@SuppressWarnings("rawtypes")
	public List<MotorCoverNoteRes> getEwayMotorCoverNote(String policyNo, String vehicleId, String quoteNo) {
		log.info("Enter into getEwayMotorCoverNote.\nArgument ==> PolicyNo :" + policyNo + ",QuoteNo : " + quoteNo
				+ ",VehicleId : " + vehicleId);
		List<MotorCoverNoteRes> response = new ArrayList<MotorCoverNoteRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<MotorDataDetails> mddRoot = cq.from(MotorDataDetails.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);

			Subquery<String> insureName = cq.subquery(String.class);
			Root<LoginUserInfo> SubluiRoot = insureName.from(LoginUserInfo.class);
			insureName.select(cb.upper(SubluiRoot.get("userName")))
					.where(cb.equal(SubluiRoot.get("loginId"), hpmRoot.get("loginId")));

			// MAKE MASTER TYPE
			Subquery<Integer> makeTypeAmd = cq.subquery(Integer.class);
			Root<MotorMakeMaster> makeAmd = makeTypeAmd.from(MotorMakeMaster.class);
			makeTypeAmd.select(cb.max(makeAmd.get("amendId"))).where(
					cb.equal(makeAmd.get("makeId").as(String.class), mddRoot.get("vehicleMake")),
					cb.equal(makeAmd.get("status"), "Y"), cb.equal(makeAmd.get("companyId"), hpmRoot.get("companyId")));

			/*
			 * Subquery<String> makeType = cq.subquery(String.class); Root<MotorMakeMaster>
			 * makeRoot = makeType.from(MotorMakeMaster.class);
			 * makeType.select(makeRoot.get("makeNameEn")).where(cb.equal(makeRoot.get(
			 * "makeId"), mddRoot.get("vehicleMake")), cb.equal(makeRoot.get("companyId"),
			 * hpmRoot.get("companyId")),cb.equal(makeRoot.get("status"), "Y"),
			 * cb.equal(makeRoot.get("amendId"), makeTypeAmd));
			 */

			// MODEL MASTER TYPE
			Subquery<Integer> modelTypeAmd = cq.subquery(Integer.class);
			Root<MotorMakeModelMaster> SubmmAmd = modelTypeAmd.from(MotorMakeModelMaster.class);
			modelTypeAmd.select(cb.max(SubmmAmd.get("amendId"))).where(
					cb.equal(SubmmAmd.get("vehiclemodelcode").as(String.class), mddRoot.get("vehcileModel")),
					cb.equal(SubmmAmd.get("status"), "Y"),
					cb.equal(SubmmAmd.get("companyId"), hpmRoot.get("companyId")));

			Subquery<String> modelType = cq.subquery(String.class);
			Root<MotorMakeModelMaster> Submm = modelType.from(MotorMakeModelMaster.class);
			modelType.select(Submm.get("modelNameEn")).where(
					cb.equal(Submm.get("vehiclemodelcode").as(String.class), mddRoot.get("vehcileModel")),
					cb.equal(Submm.get("companyId"), hpmRoot.get("companyId")), cb.equal(Submm.get("status"), "Y"),
					cb.equal(Submm.get("amendId"), modelTypeAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> SubicmAmd = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(SubicmAmd.get("amendId")))
					.where(cb.equal(SubicmAmd.get("companyId"), icmRoot.get("companyId")));

			List<Selection> selectionList = Arrays.asList(mddRoot.get("vehicleId").alias("vehicleId"),
					cb.concat(piRoot.get("titleDesc"),
							cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
									.when(cb.equal(piRoot.get("titleDesc"), ""), "").otherwise(".").as(String.class),
									piRoot.get("clientName")))
							.alias("customerName"),
					cb.selectCase()
							.when(cb.in(hpmRoot.get("sourceType"))
									.value(Arrays.asList("Premia Broker", "Premia Direct", "Premia Agent")),
									hpmRoot.get("customerName"))
							.otherwise(insureName).alias("insurerName"),
					hpmRoot.get("policyCovertedDate").alias("paymentDate"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"), hpmRoot.get("expiryDate").alias("expiryDate"),
					mddRoot.get("registrationNumber").alias("registrationNumber"),
					mddRoot.get("requestReferenceNo").alias("requestReferenceNo"),
					mddRoot.get("vehicleTypeDesc").alias("vehicleTypeDesc"),
					cb.selectCase().when(cb.isNotNull(mddRoot.get("vehcileModelDesc")), mddRoot.get("vehcileModelDesc"))
							.otherwise(modelType).alias("modelType"),
					mddRoot.get("colorDesc").alias("colorDesc"), mddRoot.get("cubicCapacity").alias("cubicCapacity"),
					mddRoot.get("vehicleMakeDesc").alias("vehicleMakeDesc"),
					mddRoot.get("chassisNumber").alias("chassisNumber"),
					mddRoot.get("seatingCapacity").alias("seatingCapacity"),
					mddRoot.get("engineNumber").alias("engineNumber"), mddRoot.get("fuelTypeDesc").alias("fuelType"),
					mddRoot.get("policyTypeDesc").alias("policyTypeDesc"),
					mddRoot.get("manufactureYear").alias("manufactureYear"),
					cb.selectCase()
							.when(cb.in(hpmRoot.get("sourceType"))
									.value(Arrays.asList("Premia Broker", "Premia Direct", "Premia Agent")),
									piRoot.get("mobileNo1"))
							.otherwise(luiRoot.get("userMobile")).alias("agentMobile"),
					mddRoot.get("motorUsageDesc").alias("motorUsageDesc"),
					hpmRoot.get("companyName").alias("companyName"), hpmRoot.get("branchName").alias("branchName"),
					hpmRoot.get("currency").alias("currency"), mddRoot.get("sectionName").alias("sectionName"),
					mddRoot.get("vehcileModel").alias("vehcileModel"),
					sddRoot.get("coverNoteReferenceNo").alias("covernoteNo"),
					sddRoot.get("stickerNumber").alias("stickerNumber"));
			List<Selection> selections = selectionList.stream().collect(Collectors.toList());
			// if(StringUtils.isNotBlank(vehicleId)) {
			selections.add(mddRoot.get("sumInsuredLc").alias("sumInsured"));
			selections
					.add(cb.selectCase()
							.when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")),
									mddRoot.get("actualPremiumLc"))
							.otherwise(mddRoot.get("actualPremiumFc")).alias("premium"));
			selections.add(mddRoot.get("vatPremium").alias("vatPremium"));
			selections.add(cb.selectCase()
					.when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")),
							mddRoot.get("overallPremiumLc"))
					.otherwise(mddRoot.get("overallPremiumFc")).alias("overallPremium"));
			/*
			 * }else {
			 * selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(
			 * icmRoot.get("currencyId")),hpmRoot.get("premiumLc"))
			 * .otherwise(hpmRoot.get("vatPremiumFc")).alias("premium"));
			 * selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(
			 * icmRoot.get("currencyId")), hpmRoot.get("vatPremiumLc"))
			 * .otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"));
			 * selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(
			 * icmRoot.get("currencyId")), hpmRoot.get("overallPremiumLc"))
			 * .otherwise(hpmRoot.get("overallPremiumFc")).alias("overallPremium")); }
			 */

			Selection[] selectionArray = new Selection[selections.size()];
			selections.toArray(selectionArray);

			cq.multiselect(selectionArray).where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),
					cb.equal(mddRoot.get("quoteNo"), hpmRoot.get("quoteNo")),
					cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),
					cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId")),
					cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(icmRoot.get("amendId"), icmAmd),
					cb.equal(hpmRoot.get("productId"), StringUtils.isBlank(policyNo) ? "5" : "46"),
					cb.equal(hpmRoot.get("status"), StringUtils.isBlank(policyNo) ? "Y" : "P"),
					cb.equal(sddRoot.get("quoteNo"), mddRoot.get("quoteNo")),
					cb.equal(sddRoot.get("riskId").as(String.class), mddRoot.get("vehicleId")),
					StringUtils.isBlank(policyNo) ? cb.equal(hpmRoot.get("quoteNo"), quoteNo)
							: cb.equal(hpmRoot.get("policyNo"), policyNo),
					StringUtils.isNotBlank(vehicleId) ? cb.equal(mddRoot.get("vehicleId"), vehicleId)
							: cb.conjunction());
			List<Tuple> list = em.createQuery(cq).getResultList();
			if (!CollectionUtils.isEmpty(list)) {
				list.forEach(map -> {
					MotorCoverNoteRes m = MotorCoverNoteRes.builder()
							.vehicleId(map.get("vehicleId") == null ? "" : map.get("vehicleId").toString())
							.customerName(map.get("customerName") == null ? "" : map.get("customerName").toString())
							.insurerName(map.get("insurerName") == null ? "" : map.get("insurerName").toString())
							.paymentDate(map.get("paymentDate") == null ? "" : map.get("paymentDate").toString())
							.dateofIssue(map.get("paymentDate") == null ? "" : map.get("paymentDate").toString())
							.startDate(map.get("inceptionDate") == null ? "" : map.get("inceptionDate").toString())
							.endDate(map.get("expiryDate") == null ? "" : map.get("expiryDate").toString())
							.covernoteNo(map.get("covernoteNo") == null ? "" : map.get("covernoteNo").toString())
							.stickerNumber(map.get("stickerNumber") == null ? "" : map.get("stickerNumber").toString())
							.registrationNumber(map.get("registrationNumber") == null ? ""
									: map.get("registrationNumber").toString())
							.requestReferenceNo(map.get("requestReferenceNo") == null ? ""
									: map.get("requestReferenceNo").toString())
							.vehicleTypeDesc(
									map.get("vehicleTypeDesc") == null ? "" : map.get("vehicleTypeDesc").toString())
							.modelType(map.get("modelType") == null ? "" : map.get("modelType").toString())
							.colorDesc(map.get("colorDesc") == null ? "" : map.get("colorDesc").toString())
							.cubicCapacity(map.get("cubicCapacity") == null ? "" : map.get("cubicCapacity").toString())
							.vehicleMakeDesc(
									map.get("vehicleMakeDesc") == null ? "" : map.get("vehicleMakeDesc").toString())
							.chassisNumber(map.get("chassisNumber") == null ? "" : map.get("chassisNumber").toString())
							.seatingCapacity(
									map.get("seatingCapacity") == null ? "" : map.get("seatingCapacity").toString())
							.engineNumber(map.get("engineNumber") == null ? "" : map.get("engineNumber").toString())
							.fuelType(map.get("fuelType") == null ? "" : map.get("fuelType").toString())
							.policyTypeDesc(
									map.get("policyTypeDesc") == null ? "" : map.get("policyTypeDesc").toString())
							.manufactureYear(
									map.get("manufactureYear") == null ? "" : map.get("manufactureYear").toString())
							.agentMobile(map.get("agentMobile") == null ? "" : map.get("agentMobile").toString())
							.motorUsageDesc(
									map.get("motorUsageDesc") == null ? "" : map.get("motorUsageDesc").toString())
							.companyName(map.get("companyName") == null ? "" : map.get("companyName").toString())
							.branchName(map.get("branchName") == null ? "" : map.get("branchName").toString())
							.currency(map.get("currency") == null ? "" : map.get("currency").toString())
							.sectionName(map.get("sectionName") == null ? "" : map.get("sectionName").toString())
							.modelNumber(map.get("vehcileModel") == null ? "" : map.get("vehcileModel").toString())
							.premium(map.get("premium") == null ? "" : map.get("premium").toString())
							.sumInsured(map.get("sumInsured") == null ? "" : map.get("sumInsured").toString())
							.vatPremium(map.get("vatPremium") == null ? "" : map.get("vatPremium").toString())
							.overallPremium(
									map.get("overallPremium") == null ? "" : map.get("overallPremium").toString())
							.build();
					response.add(m);
				});
			}
		} catch (Exception e) {
			log.info("Error in getEwayMotorCoverNote ==> " + e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getEwayMotorCoverNote");
		return response;
	}

	public TaxInvoiceRes getTaxInvoiceRes(String policyNo ,HomePositionMaster home) {
		log.info("Enter into getTaxInvoiceRes.\nArgument ==> PolicyNo :" + policyNo);
		TaxInvoiceRes response = new TaxInvoiceRes();
		Double OverAllPremium = 0.0;
		try {
			List<TaxDataSetOneRes> dataset1Res = new ArrayList<TaxDataSetOneRes>();
			List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<PaymentDetail> pdRoot = cq.from(PaymentDetail.class);

			Subquery<Integer> SubcnAmd = cq.subquery(Integer.class);
			Root<CountryMaster> cnAmd = SubcnAmd.from(CountryMaster.class);
			SubcnAmd.select(cb.max(cnAmd.get("amendId"))).where(
					cb.equal(cnAmd.get("countryId"), piRoot.get("nationality")),
					cb.equal(cnAmd.get("companyId"), hpmRoot.get("companyId")), cb.equal(cnAmd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> Subcn = countryName.from(CountryMaster.class);
			countryName.select(Subcn.get("countryName")).where(
					cb.equal(Subcn.get("countryId"), piRoot.get("nationality")),
					cb.equal(Subcn.get("companyId"), hpmRoot.get("companyId")), cb.equal(Subcn.get("status"), "Y"),
					cb.equal(Subcn.get("amendId"), SubcnAmd));

			Subquery<String> vrnNumber = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> Subvrn = vrnNumber.from(InsuranceCompanyMaster.class);
			vrnNumber.select(Subvrn.get("vrnNumber")).where(cb.equal(Subvrn.get("companyId"), hpmRoot.get("companyId")),
					cb.between(cb.literal(new Date()), Subvrn.get("effectiveDateStart"),
							Subvrn.get("effectiveDateEnd")));

			Subquery<String> tinNumber = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> Subtin = tinNumber.from(InsuranceCompanyMaster.class);
			tinNumber.select(Subtin.get("tinNumber")).where(cb.equal(Subtin.get("companyId"), hpmRoot.get("companyId")),
					cb.between(cb.literal(new Date()), Subtin.get("effectiveDateStart"),
							Subtin.get("effectiveDateEnd")));

			Subquery<String> brokerName = cq.subquery(String.class);
			Root<LoginUserInfo> SubBn = brokerName.from(LoginUserInfo.class);
			brokerName.select(SubBn.get("userName")).where(cb.equal(SubBn.get("loginId"), hpmRoot.get("loginId")));

			Subquery<String> currencyId = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> SubCi = currencyId.from(InsuranceCompanyMaster.class);
			currencyId.select(SubCi.get("currencyId"))
					.where(cb.equal(hpmRoot.get("companyId"), SubCi.get("companyId")));

			Subquery<BigDecimal> sumInsured = cq.subquery(BigDecimal.class);
			Root<PolicyCoverData> SubSi = sumInsured.from(PolicyCoverData.class);
			sumInsured.select(cb.sum(SubSi.get("sumInsured"))).where(
					cb.equal(SubSi.get("quoteNo"), hpmRoot.get("quoteNo")), cb.equal(SubSi.get("discLoadId"), "0"),
					cb.equal(SubSi.get("taxId"), "0"), cb.equal(SubSi.get("dependentCoverYn"), "N"),
					cb.or(cb.equal(SubSi.get("coverageType"), "B"),
							cb.and(cb.equal(SubSi.get("coverageType"), "O"), cb.equal(SubSi.get("isSelected"), "Y"))));

			/*
			 * Subquery<String> companyName = cq.subquery(String.class);
			 * Root<InsuranceCompanyMaster> companyNameRoot =
			 * companyName.from(InsuranceCompanyMaster.class); //AMD MAX Subquery<Integer>
			 * companyNameAmd = cq.subquery(Integer.class); Root<InsuranceCompanyMaster>
			 * companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			 * companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.
			 * equal(companyNameAmdRoot.get("companyId"),
			 * companyNameRoot.get("companyId")));
			 * companyName.select(companyNameRoot.get("companyName")).where(cb.equal(
			 * companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
			 * cb.equal(companyNameRoot.get("amendId"), companyNameAmd));
			 * 
			 * Subquery<String> imageURL = cq.subquery(String.class);
			 * Root<InsuranceCompanyMaster> imageURLRoot =
			 * imageURL.from(InsuranceCompanyMaster.class); //AMD MAX Subquery<Integer>
			 * imageURLAmd = cq.subquery(Integer.class); Root<InsuranceCompanyMaster>
			 * imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			 * imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(
			 * imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			 * imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.
			 * get("companyId"), hpmRoot.get("companyId")),
			 * cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
			 */

			cq.multiselect(luiRoot.get("userName").alias("userName"), hpmRoot.get("approvedBy").alias("approvedBy"),
					hpmRoot.get("emiYn").alias("emiYn"), hpmRoot.get("agencyCode").alias("agencyCode"),
					luiRoot.get("coreAppBrokerCode").alias("coreAppBrokerCode"),
					hpmRoot.get("effectiveDate").alias("effectiveDate"),
					cb.concat(piRoot.get("titleDesc"),
							cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
									.when(cb.equal(piRoot.get("titleDesc"), ""), "").otherwise(".").as(String.class),
									piRoot.get("clientName")))
							.alias("customerName"),
					piRoot.get("address1").alias("address"), piRoot.get("pinCode").alias("pinCode"),
					countryName.alias("countryName"), piRoot.get("stateName").alias("stateName"),
					piRoot.get("cityName").alias("cityName"), piRoot.get("vrTinNo").alias("vrTinNo"),
					piRoot.get("idTypeDesc").alias("identificationName"),
					piRoot.get("idNumber").alias("identificationNo"),
					hpmRoot.get("brokerCode").alias("intermediaryRefNo"), hpmRoot.get("policyNo").alias("policyNo"),
					hpmRoot.get("quoteNo").alias("quoteNo"), hpmRoot.get("inceptionDate").alias("inceptionDate"),
					hpmRoot.get("expiryDate").alias("expiryDate"), hpmRoot.get("currency").alias("currency"),
					hpmRoot.get("debitNoteNo").alias("debitNoteNo"), vrnNumber.alias("vrnNumber"),
					tinNumber.alias("tinNumber"),
					cb.selectCase().when(cb.equal(hpmRoot.get("subUserType"), "b2c"), "DIRECT")
							.when(cb.in(hpmRoot.get("sourceType"))
									.value(Arrays.asList("Premia Broker", "Premia Direct", "Premia Agent")),
									hpmRoot.get("customerName"))
							.otherwise(brokerName).alias("brokerName"),
					hpmRoot.get("productId").alias("productId"),
					// cb.selectCase().when(cb.isNull(hpmRoot.get("endtTypeId")),
					// cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId),
					// hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")))
					// .otherwise(hpmRoot.get("endtPremium")).alias("premium"),
					// cb.selectCase().when(cb.isNull(hpmRoot.get("endtTypeId")),
					// cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId),
					// hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")))
					// .otherwise(cb.quot(cb.prod(hpmRoot.get("endtPremium"),
					// hpmRoot.get("vatPercent")), 100)).alias("vatPremium"),
					// cb.selectCase().when(cb.isNull(hpmRoot.get("endtTypeId")),
					// cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId),
					// hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")))
					// .otherwise(cb.sum(hpmRoot.get("endtPremium"),cb.quot(cb.prod(hpmRoot.get("endtPremium"),
					// hpmRoot.get("vatPercent")), 100))).alias("overAllPremium"),
					hpmRoot.get("vatPercent").alias("vatPercent"), pdRoot.get("bankName").alias("bankName"),
					pdRoot.get("accountNumber").alias("accountNumber"), sumInsured.alias("totSumInsured"),
					hpmRoot.get("companyId").alias("companyId"), hpmRoot.get("branchCode").alias("branchCode"),
					hpmRoot.get("branchName").alias("branchName"), hpmRoot.get("bdmName").alias("bdmName"),
					hpmRoot.get("orangeCardurl").alias("orangeCardurl"))// ,companyName.alias("companyName"),imageURL.alias("companyLogo"))
					.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),
							cb.equal(pdRoot.get("quoteNo"), hpmRoot.get("quoteNo")),
							cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
							cb.equal(hpmRoot.get("policyNo"), policyNo));

			List<Tuple> list = em.createQuery(cq).getResultList();
			if (!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				CriteriaBuilder cb1 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
				Root<MotorDataDetails> mddRoot = cq1.from(MotorDataDetails.class);
				cq1.multiselect(mddRoot.get("registrationNumber").alias("registrationNumber"),
						mddRoot.get("motorCategoryDesc").alias("motorCategoryDesc"),
						mddRoot.get("policyTypeDesc").alias("policyTypeDesc"))
						.where(cb.equal(mddRoot.get("quoteNo"), map.get("quoteNo")));
				List<Tuple> dataset1 = em.createQuery(cq1).getResultList();
				dataset1.forEach(k -> {
					TaxDataSetOneRes p = TaxDataSetOneRes.builder()
							.registrationNumber(
									k.get("registrationNumber") == null ? "" : k.get("registrationNumber").toString())
							.motorCategoryDesc(
									k.get("motorCategoryDesc") == null ? "" : k.get("motorCategoryDesc").toString())
							.build();
					dataset1Res.add(p);
				});
				String companyId = map.get("companyId") == null ? "" : map.get("companyId").toString();
				String branchCode = map.get("branchCode") == null ? "" : map.get("branchCode").toString();
				CriteriaQuery<Tuple> bankdetails = cb.createQuery(Tuple.class);
				Root<ListItemValue> lRoot = bankdetails.from(ListItemValue.class);
				Subquery<Integer> bankAmd = bankdetails.subquery(Integer.class);
				Root<ListItemValue> bankAmdRoot = bankAmd.from(ListItemValue.class);
				Predicate ba1 = cb.equal(bankAmdRoot.get("itemType"), lRoot.get("itemType"));
				Predicate ba2 = cb.equal(bankAmdRoot.get("companyId"), lRoot.get("companyId"));
				Predicate ba3 = cb.equal(bankAmdRoot.get("branchCode"), lRoot.get("branchCode"));
				Predicate ba4 = cb.equal(bankAmdRoot.get("status"), lRoot.get("status"));
				bankAmd.select(cb.max(bankAmdRoot.get("amendId"))).where(ba1, ba2, ba3, ba4);

				Predicate b1 = cb.equal(lRoot.get("itemType"), "BANK_DETAILS");
				Predicate b2 = cb.equal(lRoot.get("companyId"), companyId);
				Predicate b3 = cb.equal(lRoot.get("status"), "Y");
				Predicate b4 = cb.equal(lRoot.get("amendId"), bankAmd);
				bankdetails.multiselect(lRoot.get("itemCode").alias("itemCode"),
						lRoot.get("itemValue").alias("itemValue"));
				if ("100019".equalsIgnoreCase(companyId)) {
					Predicate b5 = cb.equal(lRoot.get("branchCode"), branchCode);
					bankdetails.where(b1, b2, b3, b4, b5);
				} else {
					bankdetails.where(b1, b2, b3, b4);
				}
				List<Tuple> bankDetailsList = em.createQuery(bankdetails).getResultList();

				List<Map<String, Object>> bankList = new ArrayList<>();
				bankDetailsList.forEach(b -> {
					Map<String, Object> Bmap = new HashMap<>();
					Bmap.put(b.get("itemCode").toString(), b.get("itemValue"));
					bankList.add(Bmap);
				});

				for (Map<String, Object> entry : bankList) {
					if (entry.containsKey("ACCOUNT_NUMBER"))
						response.setBankaccountNumber(
								entry.get("ACCOUNT_NUMBER") == null ? "" : entry.get("ACCOUNT_NUMBER").toString());
					else if (entry.containsKey("ACCOUNT_NAME"))
						response.setBankaccountName(
								entry.get("ACCOUNT_NAME") == null ? "" : entry.get("ACCOUNT_NAME").toString());
					else if (entry.containsKey("ADDRESS"))
						response.setBankaddress(entry.get("ADDRESS") == null ? "" : entry.get("ADDRESS").toString());
					else if (entry.containsKey("SWIFT CODE"))
						response.setBankswiftCode(
								entry.get("SWIFT CODE") == null ? "" : entry.get("SWIFT CODE").toString());
					else if (entry.containsKey("ACCOUNT_NUMBER_USD"))
						response.setBankaccountUSD(entry.get("ACCOUNT_NUMBER_USD") == null ? ""
								: entry.get("ACCOUNT_NUMBER_USD").toString());

					else if (entry.containsKey("BANK_NAME"))
						response.setBankName(entry.get("BANK_NAME") == null ? "" : entry.get("BANK_NAME").toString());
					// else if (entry.containsKey("CURRENCY"))
					// response.setCurrency(entry.get("CURRENCY") == null ? "" :
					// entry.get("CURRENCY").toString());
					else if (entry.containsKey("BRANCH_NAME"))
						response.setBankBranchName(
								entry.get("BRANCH_NAME") == null ? "" : entry.get("BRANCH_NAME").toString());
					else if (entry.containsKey("BRANCH_CODE"))
						response.setBankBranchCode(
								entry.get("BRANCH_CODE") == null ? "" : entry.get("BRANCH_CODE").toString());
				}

				Double taxAmount = 0.0;
				// if(Arrays.asList(5,46).contains(map.get("productId"))) {
				List<PolicyDrcrDetail> drcrDetails = drcrdetail.findByQuoteNoAndStatusIn(
						map.get("quoteNo") == null ? "" : map.get("quoteNo").toString(), Arrays.asList("Y", "CV"));
				List<PolicyDrcrDetail> listByRiskId = drcrDetails.stream()
						.filter(r -> r.getDrcrFlag().equalsIgnoreCase("DR"))
						.sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder)).collect(Collectors.toList());
				listByRiskId.forEach(h -> {
					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
							.amount(h.getAmountFc() == null ? ""
									: new BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
							.narration(h.getNarration() == null ? ""
									: h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
							.status(h.getStatus()).build();
					premiumDetailsRes.add(u);
				});
				List<PolicyCoverData> coverData = coverDataRepository
						.findByQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				Double taxRate = coverData.stream()
						.filter(f -> f.getTaxId() != 0 && f.getCoverageType().equalsIgnoreCase("T"))
						.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue).findAny().orElse(0.0);

				taxAmount = coverData.stream()
						.filter(f -> f.getTaxId() != 0 && f.getCoverageType().equalsIgnoreCase("T")
								&& f.getSectionId() != 99999)
						.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));

				response.setVatPercent(taxRate.toString());
				response.setVatAmount(taxAmount.toString());

				/*
				 * }else { List<PolicyCoverData> coverData =
				 * coverDataRepository.findByQuoteNo(map.get("quoteNo")==null?"":map.get(
				 * "quoteNo").toString()); if(coverData!=null && !coverData.isEmpty()) { Double
				 * taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 &&
				 * f.getCoverageType().equalsIgnoreCase("T")) .map(m ->
				 * m.getTaxRate()).map(BigDecimal::doubleValue) .findAny().orElse(0.0);
				 * 
				 * taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 &&
				 * f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999) .map(i
				 * ->
				 * i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));
				 * 
				 * response.setVatPercent(taxRate.toString());
				 * response.setVatAmount(taxAmount.toString());
				 * 
				 * List<Map<String,Object>> sectionPremium = coverData.stream().filter(f
				 * ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999)
				 * .collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b
				 * -> b.getCoverDesc(),Collectors.reducing( BigDecimal.ZERO,
				 * PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
				 * .entrySet().stream() .flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s )
				 * -> { Integer sectionId = s.getKey(); return s.getValue().entrySet().stream()
				 * .map((Map.Entry<String,BigDecimal> g )-> { String coverDesc = g.getKey();
				 * BigDecimal totPremium = g.getValue(); Map<String,Object> secMap = new
				 * HashMap<String,Object>(); secMap.put("SectionId", sectionId);
				 * secMap.put("CoverDesc", coverDesc); secMap.put("TotPremium", totPremium);
				 * return secMap; }); }).sorted(Comparator.comparing(p -> (String)
				 * p.get("CoverDesc"))) .collect(Collectors.toList()); sectionPremium.forEach(k
				 * -> { TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
				 * .amount(new
				 * BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
				 * .narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().
				 * replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")) .build();
				 * premiumDetailsRes.add(u); }); } }
				 */

				OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount()))
						.collect(Collectors.summingDouble(BigDecimal::doubleValue));
				String amtInWords = "";
				if (OverAllPremium != null) {
					amtInWords = motorRepo.getAmountByWords(OverAllPremium);
				}

				List<Map<String, Object>> companyDetails = insuranceComMasRepo
						.getCompanyDetailsById(map.get("companyId") == null ? "" : map.get("companyId").toString());
				if (!companyDetails.isEmpty()) {
					response.setCompanyName(companyDetails.get(0).get("COMPANY_NAME") == null ? ""
							: companyDetails.get(0).get("COMPANY_NAME").toString());

					String productName = "";
					try {
						String compId = map.get("companyId") == null ? "" : map.get("companyId").toString();
						String productId = map.get("productId") == null ? "" : map.get("productId").toString();

						if (!compId.isEmpty() && !productId.isEmpty()) {
							CriteriaBuilder cb2 = em.getCriteriaBuilder();
							CriteriaQuery<Tuple> cq2 = cb2.createQuery(Tuple.class);
							Root<CompanyProductMaster> cpmRoot = cq2.from(CompanyProductMaster.class);

							Subquery<Integer> productAmd = cq2.subquery(Integer.class);
							Root<CompanyProductMaster> cpmAmdRoot = productAmd.from(CompanyProductMaster.class);
							productAmd.select(cb2.max(cpmAmdRoot.get("amendId"))).where(
									cb2.equal(cpmAmdRoot.get("companyId"), cpmRoot.get("companyId")),
									cb2.equal(cpmAmdRoot.get("productId"), cpmRoot.get("productId")));

							cq2.multiselect(cpmRoot.get("productName").alias("productName")).where(
									cb2.equal(cpmRoot.get("companyId"), compId),
									cb2.equal(cpmRoot.get("productId"), productId),
									cb2.equal(cpmRoot.get("amendId"), productAmd));

							List<Tuple> productResult = em.createQuery(cq2).getResultList();
							if (!productResult.isEmpty()) {
								productName = productResult.get(0).get("productName") == null ? ""
										: productResult.get(0).get("productName").toString();
							}
						}

						response.setProductName(productName);
					} catch (Exception e) {
						log.error("Error fetching product name from CompanyProductMaster: " + e.getMessage());
						response.setProductName("");
					}

					try {
						// String companyId = map.get("companyId") == null ? "" :
						// map.get("companyId").toString();
						String quoteNo = map.get("quoteNo") == null ? "" : map.get("quoteNo").toString();
						String productId = map.get("productId") == null ? "" : map.get("productId").toString();

						if (!companyId.isEmpty() && !quoteNo.isEmpty() && !productId.isEmpty()) {

							CriteriaBuilder cb3 = em.getCriteriaBuilder();
							CriteriaQuery<Tuple> cq3 = cb3.createQuery(Tuple.class);
							Root<EmiTransactionDetails> emiRoot = cq3.from(EmiTransactionDetails.class);

							cq3.multiselect(emiRoot.get("dueDate").alias("dueDate"),
									emiRoot.get("percentage").alias("percentage"),
									emiRoot.get("currency").alias("currency"),
									emiRoot.get("dueAmount").alias("dueAmount"),
									emiRoot.get("paymentStatus").alias("paymentStatus"))
									.where(cb3.equal(emiRoot.get("companyId"), companyId),
											cb3.equal(emiRoot.get("quoteNo"), quoteNo),
											cb3.equal(emiRoot.get("productId"), productId))

									.orderBy(cb3.asc(emiRoot.get("dueDate")));

							List<Tuple> emiResult = em.createQuery(cq3).getResultList();

							AtomicInteger counter = new AtomicInteger(1);
							List<InstallmentDetailRes> emiList = emiResult.stream().map(t -> {
								InstallmentDetailRes i = new InstallmentDetailRes();
								i.setSrNo(String.valueOf(counter.getAndIncrement()));
								i.setDueDate(t.get("dueDate") == null ? "" : t.get("dueDate").toString());
								i.setPercentage(t.get("percentage") == null ? "" : t.get("percentage").toString());
								i.setCurrency(t.get("currency") == null ? "" : t.get("currency").toString());
								i.setDueAmount(t.get("dueAmount") == null ? "" : t.get("dueAmount").toString());
								i.setPaymentStatus(
										t.get("paymentStatus") == null ? "" : t.get("paymentStatus").toString());
								return i;
							}).collect(Collectors.toList());

							response.setInstallmentDetail(emiList);
						}

					} catch (Exception e) {
						log.error("Error fetching EMI installment details: " + e.getMessage(), e);
						response.setInstallmentDetail(Collections.emptyList());
					}

					response.setCompanyLogo(companyDetails.get(0).get("COMPANY_LOGO") == null ? ""
							: companyDetails.get(0).get("COMPANY_LOGO").toString());
					response.setCompanyWebsite(companyDetails.get(0).get("COMPANY_WEBSITE") == null ? ""
							: companyDetails.get(0).get("COMPANY_WEBSITE").toString());
					response.setCompanyMail(companyDetails.get(0).get("COMPANY_EMAIL") == null ? ""
							: companyDetails.get(0).get("COMPANY_EMAIL").toString());
					response.setCompanyPhone(companyDetails.get(0).get("COMPANY_PHONE") == null ? ""
							: companyDetails.get(0).get("COMPANY_PHONE").toString());
					response.setCompanyAddress(companyDetails.get(0).get("COMPANY_ADDRESS") == null ? ""
							: companyDetails.get(0).get("COMPANY_ADDRESS").toString());
					response.setCompanyPoBox(companyDetails.get(0).get("PO_BOX") == null ? ""
							: companyDetails.get(0).get("PO_BOX").toString());
					response.setSignature(companyDetails.get(0).get("SIGNATURE") == null ? ""
							: companyDetails.get(0).get("SIGNATURE").toString());
				}

				response.setUserName(map.get("userName") == null ? "" : map.get("userName").toString());
				response.setApprovedBy(map.get("approvedBy") == null ? "" : map.get("approvedBy").toString());
				response.setAgencyCode(map.get("agencyCode") == null ? "" : map.get("agencyCode").toString());
				response.setCustomerName(map.get("customerName") == null ? "" : map.get("customerName").toString());
				response.setAddress(StringUtils.join(Arrays
						.asList(map.get("address") == null ? "" : map.get("address").toString(),
								map.get("pinCode") == null ? "" : map.get("pinCode").toString(),
								map.get("stateName") == null ? "" : map.get("stateName").toString(),
								map.get("cityName") == null ? "" : map.get("cityName").toString(),
								map.get("countryName") == null ? "" : map.get("countryName").toString())
						.stream().filter(value -> !value.isEmpty()).collect(Collectors.joining(","))));
				response.setVrTinNo(map.get("vrTinNo") == null ? "" : map.get("vrTinNo").toString());
				response.setIdentificationName(
						map.get("identificationName") == null ? "" : map.get("identificationName").toString());
				response.setIdentificationNo(
						map.get("identificationNo") == null ? "" : map.get("identificationNo").toString());
				response.setPolicyNo(map.get("policyNo") == null ? "" : map.get("policyNo").toString());
				response.setInceptionDate(map.get("inceptionDate") == null ? "" : map.get("inceptionDate").toString());
				response.setQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				response.setExpiryDate(map.get("expiryDate") == null ? "" : map.get("expiryDate").toString());
				response.setCurrency(map.get("currency") == null ? "" : map.get("currency").toString());
				response.setDebitNoteNo(map.get("debitNoteNo") == null ? "" : map.get("debitNoteNo").toString());
				response.setVrnNumber(map.get("vrnNumber") == null ? "" : map.get("vrnNumber").toString());
				response.setTinNumber(map.get("tinNumber") == null ? null : map.get("tinNumber").toString());
				response.setBrokerName(map.get("brokerName") == null ? "" : map.get("brokerName").toString());
				response.setOverAllPremium(new BigDecimal(OverAllPremium).toPlainString());
				response.setTotSumInsured(map.get("totSumInsured") == null ? ""
						: new BigDecimal(Double.valueOf(map.get("totSumInsured").toString())).toString());
				response.setIntermediaryRefNo(
						map.get("intermediaryRefNo") == null ? "" : map.get("intermediaryRefNo").toString());
				response.setBranchCode(map.get("branchCode") == null ? "" : map.get("branchCode").toString());
				response.setBranchName(map.get("branchName") == null ? "" : map.get("branchName").toString());
				response.setPolicyType(!dataset1.isEmpty() ? dataset1.get(0).get("policyTypeDesc") == null ? ""
						: dataset1.get(0).get("policyTypeDesc").toString() : "");
				response.setCoreAppBrokerCode(
						map.get("coreAppBrokerCode") == null ? "" : map.get("coreAppBrokerCode").toString());
				response.setEffectiveDate(map.get("effectiveDate") == null ? "" : map.get("effectiveDate").toString());
				response.setEmiYn(map.get("emiYn") == null ? "" : map.get("emiYn").toString());
				response.setCompanyId(map.get("companyId") == null ? "" : map.get("companyId").toString());
				response.setBdmName(map.get("bdmName") == null ? "" : map.get("bdmName").toString());
				response.setOrangeCardurl(map.get("orangeCardurl") == null ? "" : map.get("orangeCardurl").toString());
				response.setAmountInWords(amtInWords);
				response.setDataset1List(dataset1Res);
				response.setPremiumDetails(premiumDetailsRes);
				if ("1".equalsIgnoreCase(home.getApplicationId())) {
					response.setCreatedBy(home.getLoginId());
					response.setAppBy(home.getLoginId());
					if ("RP".equalsIgnoreCase(home.getAdminReferralStatus())) {
						response.setAppBy(home.getEndtBy());
					}
				} else {
					response.setCreatedBy(home.getApplicationId());
					response.setAppBy(home.getApplicationId());
					if ("RP".equalsIgnoreCase(home.getAdminReferralStatus())) {
						response.setAppBy(home.getEndtBy());
					}
				}
			}
		} catch (Exception e) {
			log.info("Error in getTaxInvoiceRes ==> " + e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getTaxInvoiceRes");
		return response;

	}

	public CreditNoteRes getCreditNoteRes(String policyNo , HomePositionMaster home) {
		log.info("Enter into getCreditNoteRes.\nArgument ==> PolicyNo :" + policyNo);
		CreditNoteRes response = new CreditNoteRes();
		Double OverAllPremium = 0.0;
		try {
			List<CreditDataSetOne> DataSetOneRes = new ArrayList<CreditDataSetOne>();
			List<CreditDataSetTwo> DataSetTwoRes = new ArrayList<CreditDataSetTwo>();
			List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);

			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCnAd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCnAd.get("amendId"))).where(
					cb.equal(SubCnAd.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCnAd.get("companyId"), hpmRoot.get("companyId")), cb.equal(SubCnAd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(
					cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")), cb.equal(SubCm.get("status"), "Y"),
					cb.equal(SubCm.get("amendId"), countryNameAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> SubIcAm = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(SubIcAm.get("amendId")))
					.where(cb.equal(SubIcAm.get("companyId"), icmRoot.get("companyId")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			// AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId")))
					.where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(
					cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			// AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId")))
					.where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(
					cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(
					cb.selectCase()
							.when(cb.in(hpmRoot
									.get("sourceType")).value(Arrays.asList("Premia Broker", "Premia Direct",
											"Premia Agent")),
									hpmRoot.get("customerName"))
							.otherwise(luiRoot.get("userName")).alias("brokerName"),
					cb.concat(piRoot.get("titleDesc"),
							cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
									.when(cb.equal(piRoot.get("titleDesc"), ""), "").otherwise(".").as(String.class),
									piRoot.get("clientName")))
							.alias("customerName"),
					piRoot.get("address1").alias("address"), piRoot.get("pinCode").alias("pinCode"),
					countryName.alias("countryName"), piRoot.get("stateName").alias("stateName"),
					piRoot.get("cityName").alias("cityName"), piRoot.get("vrTinNo").alias("vrTinNo"),
					piRoot.get("idTypeDesc").alias("identificationName"),
					piRoot.get("idNumber").alias("identificationNo"),
					hpmRoot.get("brokerCode").alias("intermediaryRefNo"),

					hpmRoot.get("branchName").alias("branchName"), hpmRoot.get("creditNo").alias("creditNo"),
					hpmRoot.get("currency").alias("currency"), hpmRoot.get("productName").alias("productName"),
					cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), "NEW BUSINESS")
							.otherwise("ENDORSEMENT").alias("business"),
					cb.selectCase().when(cb.isNull(hpmRoot.get("originalPolicyNo")), hpmRoot.get("policyNo"))
							.otherwise(hpmRoot.get("originalPolicyNo")).alias("policyNo"),
					cb.selectCase().when(cb.isNotNull(hpmRoot.get("originalPolicyNo")), hpmRoot.get("policyNo"))
							.alias("endorsementNo"),
					hpmRoot.get("endtTypeId").alias("endtTypeId"), hpmRoot.get("endtTypeDesc").alias("endtTypeDesc"),
					hpmRoot.get("endorsementRemarks").alias("endorsementRemarks"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"), hpmRoot.get("expiryDate").alias("expiryDate"),
					hpmRoot.get("agencyCode").alias("agencyCode"), hpmRoot.get("customerId").alias("customerId"),
					hpmRoot.get("approvedBy").alias("approvedBy"),
					// cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"),
					// hpmRoot.get("commission")).when(cb.isNotNull(hpmRoot.get("creditNo")),
					// hpmRoot.get("commission")).alias("premium"),
					// cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"),
					// cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")),
					// 100)).when(cb.isNotNull(hpmRoot.get("creditNo")),
					// cb.quot(cb.prod(hpmRoot.get("commission"), hpmRoot.get("vatPercent")),
					// 100)).alias("vatPremiumFc"),
					// cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"),
					// cb.sum(hpmRoot.get("commission"), cb.quot(cb.prod(hpmRoot.get("commission"),
					// hpmRoot.get("vatPercent")), 100)))
					// .when(cb.isNotNull(hpmRoot.get("creditNo")),
					// cb.sum(hpmRoot.get("commission"), cb.quot(cb.prod(hpmRoot.get("commission"),
					// hpmRoot.get("vatPercent")), 100))).alias("overAllPremiumFc"),
					hpmRoot.get("vatPercent").alias("vatPercent"), hpmRoot.get("quoteNo").alias("quoteNo"),
					hpmRoot.get("customerCode").alias("customerCode"), companyName.alias("companyName"),
					imageURL.alias("companyLogo"), hpmRoot.get("companyId").alias("companyId"),
					icmRoot.get("companyAddress").alias("companyAddress"), icmRoot.get("signature").alias("signature"),
					icmRoot.get("vrnNumber").alias("vrnNumber"), icmRoot.get("companyEmail").alias("companyEmail"),
					icmRoot.get("companyWebsite").alias("companyWebsite"),
					icmRoot.get("companyPhone").alias("companyPhone"),
					hpmRoot.get("effectiveDate").alias("effectiveDate"), luiRoot.get("userName").alias("userName"),
					luiRoot.get("coreAppBrokerCode").alias("coreAppBrokerCode"),
					hpmRoot.get("bdmName").alias("bdmName"))

					.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),
							cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
							cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
							cb.equal(icmRoot.get("amendId"), icmAmd),
							cb.in(hpmRoot.get("status")).value(Arrays.asList("P", "D")),
							cb.equal(hpmRoot.get("policyNo"), policyNo))
					.orderBy(cb.desc(hpmRoot.get("entryDate")));

			List<Tuple> list = em.createQuery(cq).getResultList();
			if (!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				CriteriaBuilder cb1 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
				cq1.multiselect(sddRoot.get("sectionDesc").alias("sectionDesc"))
						.where(cb.equal(sddRoot.get("quoteNo"), map.get("quoteNo")));
				List<Tuple> SectionList = em.createQuery(cq1).getResultList();
				DataSetOneRes = SectionList.stream()
						.map(k -> CreditDataSetOne.builder()
								.sectionDesc(k.get("sectionDesc") == null ? "" : k.get("sectionDesc").toString())
								.build())
						.distinct().collect(Collectors.toList());

				CriteriaBuilder cb2 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq2 = cb2.createQuery(Tuple.class);
				Root<ProductSectionMaster> psmRoot = cq2.from(ProductSectionMaster.class);
				Root<SectionDataDetails> sddRoot1 = cq2.from(SectionDataDetails.class);

				Subquery<Integer> SubSdAm = cq.subquery(Integer.class);
				Root<ProductSectionMaster> SubpsmRoot = SubSdAm.from(ProductSectionMaster.class);
				SubSdAm.select(cb.max(SubpsmRoot.get("amendId"))).where(
						cb.equal(SubpsmRoot.get("productId"), psmRoot.get("productId")),
						cb.equal(SubpsmRoot.get("companyId"), psmRoot.get("companyId")),
						cb.equal(SubpsmRoot.get("sectionId"), psmRoot.get("sectionId")),
						cb.equal(SubpsmRoot.get("status"), "Y"));

				cq2.multiselect(psmRoot.get("coreAppCode").alias("coreAppCode"))
						.where(cb.equal(psmRoot.get("status"), "Y"),
								cb.equal(psmRoot.get("productId").as(String.class), sddRoot1.get("productId")),
								cb.equal(psmRoot.get("companyId"), sddRoot1.get("companyId")),
								cb.equal(psmRoot.get("sectionId").as(String.class), sddRoot1.get("sectionId")),
								cb.equal(sddRoot1.get("quoteNo"), map.get("quoteNo")),
								cb.equal(psmRoot.get("amendId"), SubSdAm))
						.distinct(true);
				List<Tuple> riskCodeList = em.createQuery(cq2).getResultList();
				riskCodeList.forEach(i -> {
					CreditDataSetTwo q = CreditDataSetTwo.builder()
							.coreAppCode(i.get("coreAppCode") == null ? "" : i.get("coreAppCode").toString()).build();
					DataSetTwoRes.add(q);
				});
				List<PolicyDrcrDetail> drcrDetails = drcrdetail.findByQuoteNoAndStatusIn(
						map.get("quoteNo") == null ? "" : map.get("quoteNo").toString(), Arrays.asList("Y", "CV"));
				if (!drcrDetails.isEmpty()) {
					if ("100019"
							.equalsIgnoreCase(map.get("companyId") == null ? "" : map.get("companyId").toString())) {
						List<PolicyDrcrDetail> drcrList = drcrDetails.stream()
								.filter(f -> f.getDrcrFlag().equalsIgnoreCase("CR")
										&& !f.getChargeCode().equals(new BigDecimal(1005)))
								.sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder))
								.collect(Collectors.toList());
						drcrList.forEach(h -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(h.getAmountFc() == null ? ""
											: new BigDecimal(Double.parseDouble(h.getAmountFc().toString()))
													.toPlainString())
									.narration(h.getNarration() == null ? ""
											: h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
									.status(h.getStatus()).build();
							premiumDetailsRes.add(u);
						});
						Double WHTLevy = drcrList.stream()
								.filter(f -> f.getChargeCode().equals(new BigDecimal("1008"))
										|| f.getChargeCode().equals(new BigDecimal("1009")))
								.map(h -> h.getAmountFc()).collect(Collectors.summingDouble(BigDecimal::doubleValue));
						Double Commission = drcrList.stream()
								.filter(f -> f.getChargeCode().equals(new BigDecimal("1006"))).map(h -> h.getAmountFc())
								.map(BigDecimal::doubleValue).findFirst().get();
						Double VAT = drcrList.stream().filter(f -> f.getChargeCode().equals(new BigDecimal("1007")))
								.map(h -> h.getAmountFc()).map(BigDecimal::doubleValue).findFirst().get();
						OverAllPremium = (Commission - WHTLevy) + VAT;
					} else {
						List<PolicyDrcrDetail> listByRiskId = drcrDetails.stream()
								.filter(r -> r.getDrcrFlag().equalsIgnoreCase("CR")
										&& !r.getChargeCode().equals(new BigDecimal(1007))
										&& !r.getChargeCode().equals(new BigDecimal(1005)))
								.sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder))
								.collect(Collectors.toList());
						listByRiskId.forEach(h -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(h.getAmountFc() == null ? ""
											: new BigDecimal(Double.parseDouble(h.getAmountFc().toString()))
													.toPlainString())
									.narration(h.getNarration() == null ? ""
											: h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
									.build();
							premiumDetailsRes.add(u);
						});
						OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount()))
								.collect(Collectors.summingDouble(BigDecimal::doubleValue));

					}
				}
				String amtInWords = "";
				if (OverAllPremium != null) {
					amtInWords = motorRepo.getAmountByWords(OverAllPremium);
				}

				response.setBrokerName(map.get("brokerName") == null ? "" : map.get("brokerName").toString());
				response.setCustomerName(map.get("customerName") == null ? "" : map.get("customerName").toString());
				response.setAddress(StringUtils.join(Arrays
						.asList(map.get("address") == null ? "" : map.get("address").toString(),
								map.get("pinCode") == null ? "" : map.get("pinCode").toString(),
								map.get("stateName") == null ? "" : map.get("stateName").toString(),
								map.get("cityName") == null ? "" : map.get("cityName").toString(),
								map.get("countryName") == null ? "" : map.get("countryName").toString())
						.stream().filter(value -> !value.isEmpty()).collect(Collectors.joining(","))));
				response.setBranchName(map.get("branchName") == null ? "" : map.get("branchName").toString());
				response.setCreditNo(map.get("creditNo") == null ? "" : map.get("creditNo").toString());
				response.setCurrency(map.get("currency") == null ? "" : map.get("currency").toString());
				response.setProductName(map.get("productName") == null ? "" : map.get("productName").toString());
				response.setBusiness(map.get("business") == null ? "" : map.get("business").toString());
				response.setPolicyNo(map.get("policyNo") == null ? "" : map.get("policyNo").toString());
				response.setEndtTypeId(map.get("endtTypeId") == null ? "" : map.get("endtTypeId").toString());
				response.setEndtTypeDesc(map.get("endtTypeDesc") == null ? "" : map.get("endtTypeDesc").toString());
				response.setEndorsementRemarks(
						map.get("endorsementRemarks") == null ? "" : map.get("endorsementRemarks").toString());
				response.setEndorsementNo(map.get("endorsementNo") == null ? "" : map.get("endorsementNo").toString());
				response.setInceptionDate(map.get("inceptionDate") == null ? "" : map.get("inceptionDate").toString());
				response.setExpiryDate(map.get("expiryDate") == null ? "" : map.get("expiryDate").toString());
				response.setAgencyCode(map.get("agencyCode") == null ? "" : map.get("agencyCode").toString());
				response.setCustomerId(map.get("customerId") == null ? "" : map.get("customerId").toString());
				response.setApprovedBy(map.get("approvedBy") == null ? "" : map.get("approvedBy").toString());
				response.setOverAllPremiumFc(new BigDecimal(OverAllPremium).toPlainString());
				response.setQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				response.setCompanyLogo(map.get("companyLogo") == null ? "" : map.get("companyLogo").toString());
				response.setCompanyName(map.get("companyName") == null ? "" : map.get("companyName").toString());
				response.setCustomerCode(map.get("customerCode") == null ? "" : map.get("customerCode").toString());
				// response.setVatRegNo(map.get("vatRegNo")==null?"":map.get("vatRegNo").toString());
				response.setCompanyAddress(
						map.get("companyAddress") == null ? "" : map.get("companyAddress").toString());
				response.setVrnNumber(map.get("vrnNumber") == null ? "" : map.get("vrnNumber").toString());
				response.setCompanyEmail(map.get("companyEmail") == null ? "" : map.get("companyEmail").toString());
				response.setCompanyWebsite(
						map.get("companyWebsite") == null ? "" : map.get("companyWebsite").toString());
				response.setCompanyPhone(map.get("companyPhone") == null ? "" : map.get("companyPhone").toString());
				response.setAmountInWords(amtInWords);
				response.setSignature(map.get("signature") == null ? "" : map.get("signature").toString());
				response.setCoreAppBrokerCode(
						map.get("coreAppBrokerCode") == null ? "" : map.get("coreAppBrokerCode").toString());
				response.setEffectiveDate(map.get("effectiveDate") == null ? "" : map.get("effectiveDate").toString());
				response.setUserName(map.get("userName") == null ? "" : map.get("userName").toString());
				response.setBdmName(map.get("bdmName") == null ? "" : map.get("bdmName").toString());
				response.setCompanyId(map.get("companyId") == null ? "" : map.get("companyId").toString());
				response.setSectionDescList(DataSetOneRes);
				response.setRiskCodeList(DataSetTwoRes);
				response.setPremiumDetails(premiumDetailsRes);
				if ("1".equalsIgnoreCase(home.getApplicationId())) {
					response.setCreatedBy(home.getLoginId());
					response.setAppBy(home.getLoginId());
					if ("RP".equalsIgnoreCase(home.getAdminReferralStatus())) {
						response.setAppBy(home.getEndtBy());
					}
				} else {
					response.setCreatedBy(home.getApplicationId());
					response.setAppBy(home.getApplicationId());
					if ("RP".equalsIgnoreCase(home.getAdminReferralStatus())) {
						response.setAppBy(home.getEndtBy());
					}
				}
			}
		} catch (Exception e) {
			log.info("Error in getCreditNoteRes ==>" + e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getCreditNoteRes");
		return response;
	}
	
	public List<DropDownRes> getByItemValue(LovGetReq req) {
		
	
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);

			// Find All
			Root<ListItemValue> b = query.from(ListItemValue.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("itemId"), b.get("itemId"));
//			Predicate a2 = cb.equal(ocpm1.get("itemCode"), b.get("itemCode"));
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("branchCode"), ocpm1.get("branchCode"));
			if(StringUtils.isNotBlank(req.getParam1())) {
				Predicate a5 = cb.equal(b.get("param1"), ocpm1.get("param1"));
				amendId.where(a1,a3,a4,a5);
			}else {
				amendId.where(a1,a3,a4);
			}
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(b.get("branchCode"), StringUtils.isBlank(req.getBranchCode()) ?"99999" :req.getBranchCode() );
			Predicate n8 = cb.equal(cb.upper(b.get("itemType")), cb.upper(cb.literal(req.getItemType())));
			Predicate n10 = cb.equal(b.get("itemCode"), req.getItemCode());
			/*
			 * if(!StringUtils.isBlank(req.getTitletype())) { Predicate
			 * n9=cb.equal(b.get("param1"),req.getTitletype());
			 * query.where(n1,n2,n4,n8,n9).orderBy(orderList); }
			 */
			Predicate statusPredicate = cb.equal(b.get("status"), "Y");

			if(StringUtils.isNotBlank(req.getParam1())) {
				Predicate n9 = cb.equal(b.get("param1"), req.getParam1());
				query.where(n1, n2, n4, n8, n9,n10, statusPredicate).orderBy(orderList);
			} else {
				query.where(n1, n2, n4, n8,n10, statusPredicate).orderBy(orderList);
			}

			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemId()))).collect(Collectors.toList());
			if (StringUtils.isNotBlank(req.getItemType()) && ("BOND_YEAR".equalsIgnoreCase(req.getItemType())
					|| "BURGLARY_FIRST_LOSS".equalsIgnoreCase(req.getItemType())
					|| "FIDELITY_SI".equalsIgnoreCase(req.getItemType()))) {
				list = list.stream().sorted((o1, o2)->Long.valueOf(o1.getItemCode()).compareTo(Long.valueOf(o2.getItemCode()))).collect(Collectors.toList());
			}else {
			list.sort(Comparator.comparing(ListItemValue :: getItemValue ));
			}
			// Map
			if(!StringUtils.isBlank(req.getTitletype()))
			{
		
					list = list.stream()
	                .filter(item -> req.getTitletype().equals(item.getParam1()))
	                .collect(Collectors.toList());
			}
			
			
			for (ListItemValue data : list) {
				DropDownRes res = new DropDownRes();
               res.setTitletype(data.getParam1());
			res.setCode(data.getItemCode().toString());
			res.setCodeDesc(data.getItemValue().toString());
			res.setCodeDescLocal(data.getItemValueLocal());
				res.setStatus(data.getStatus()==null?"":data.getStatus().toString());
				res.setParam1(data.getParam1());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}


	@SuppressWarnings("unused")
	public MotorPrivateRes getMotorPrivate(String policyNo, String quoteNo, String vehicleId) {
		log.info("Enter into getMotorPrivate.\nArgument ==> PolicyNo :" + policyNo + " || \t QuoteNo :" + quoteNo
				+ " || \t VehicleId :" + vehicleId);
		MotorPrivateRes response = new MotorPrivateRes();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
		Double OverAllPremium = 0d;
		try {
			List<MotorPrivateVehicleDetails> vehicleDetailsRes = new ArrayList<>();
			List<MotorPrivateDriverDetails> driverDetailsRes = new ArrayList<>();
			List<MotorPrivateAccessoriesDetails> accessoriesDetailsRes = new ArrayList<>();
			List<TearmsAndCondition> tearmsAndConditionRes = new ArrayList<>();
			List<AttachMentRes> attachments = new ArrayList<>();
			List<CoverDetailsRes> coverDetailsRes = new ArrayList<>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<CompanyProductMaster> cpmRoot = cq.from(CompanyProductMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<MotorDataDetails> mddRoot = cq.from(MotorDataDetails.class);
			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCmAmd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCmAmd.get("amendId"))).where(
					cb.equal(SubCmAmd.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCmAmd.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(SubCmAmd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(
					cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")), cb.equal(SubCm.get("status"), "Y"),
					cb.equal(SubCm.get("amendId"), countryNameAmd));

			Subquery<Long> MotorCount = cq.subquery(Long.class);
			Root<MotorDataDetails> SubMCRoot = MotorCount.from(MotorDataDetails.class);
			MotorCount.select(cb.count(SubMCRoot));
			Predicate mc1 = cb.equal(
					StringUtils.isBlank(policyNo) ? SubMCRoot.get("quoteNo") : SubMCRoot.get("policyNo"),
					StringUtils.isBlank(policyNo) ? hpmRoot.get("quoteNo") : hpmRoot.get("policyNo"));
			if (StringUtils.isNotBlank(vehicleId)) {
				Predicate mc2 = cb.equal(SubMCRoot.get("vehicleId"), vehicleId);
				MotorCount.where(mc1, mc2);
			} else {
				MotorCount.where(mc1);
			}

			cq.multiselect(cpmRoot.get("companyId").alias("companyId"),
					hpmRoot.get("policyNo").alias("policyNo"), hpmRoot.get("quoteNo").alias("quoteNo"),
					hpmRoot.get("effectiveDate").alias("effectiveDateHome"),
					hpmRoot.get("entryDate").alias("entryDatehome"),
					hpmRoot.get("emiYn").alias("emiYn"),
					cb.concat(piRoot.get("titleDesc"),cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "").when(cb.equal(piRoot.get("titleDesc"), ""), "").otherwise(".").as(String.class),piRoot.get("clientName"))).alias("customerName"),
					hpmRoot.get("debitNoteNo").alias("debitNoteNo"), piRoot.get("address1").alias("address"),
					piRoot.get("mobileNo1").alias("mobileNo1"), piRoot.get("email1").alias("email1"),
					piRoot.get("pinCode").alias("pinCode"), piRoot.get("cityName").alias("cityName"),
					piRoot.get("stateName").alias("stateName"), countryName.alias("countryName"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"), hpmRoot.get("expiryDate").alias("expiryDate"),
					hpmRoot.get("currency").alias("currency"),
					mddRoot.get("insuranceTypeDesc").alias("insuranceTypeDesc"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")),hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")),hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")),hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), "LC").otherwise("FC").alias("vehiclePremiumDesc"),hpmRoot.get("branchName").alias("branchName"), /* hpmRoot.get("approvedBy").alias("approvedBy"), */
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker", "Premia Direct", "Premia Agent")),hpmRoot.get("customerName")).when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("b2c", "Direct")),"System").otherwise(luiRoot.get("userName")).alias("approvedBy"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker", "Premia Direct", "Premia Agent")),hpmRoot.get("customerName")).otherwise(luiRoot.get("userName")).alias("userName"),
					MotorCount.alias("noOfVehicle"), hpmRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),
					piRoot.get("customerId").alias("customerId"),
					cb.selectCase().when(cb.equal(hpmRoot.get("endtCount"), "0"), "NEW BUSINESS").otherwise("ENDORSEMENT").alias("business"),
					luiRoot.get("brokerLogo").alias("brokerLogo"), hpmRoot.get("vatPercent").alias("vatPercent"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")),mddRoot.get("actualPremiumLc")).otherwise(mddRoot.get("actualPremiumFc")).alias("vehiclePremium"),
					mddRoot.get("vatPremium").alias("vehicleVatPremium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")),mddRoot.get("overallPremiumLc")).otherwise(mddRoot.get("overallPremiumFc")).alias("vehicelTotalPremium"),
					hpmRoot.get("subUserType").alias("subUserType"), hpmRoot.get("productId").alias("productId"),hpmRoot.get("bdmName").alias("bdmName"), mddRoot.get("sectionName").alias("sectionName"),
					cb.selectCase().when(cb.equal(hpmRoot.get("applicationId"), "1"), hpmRoot.get("loginId")).otherwise(hpmRoot.get("applicationId")).alias("loginId"))
					.where(StringUtils.isBlank(policyNo) ? cb.equal(mddRoot.get("quoteNo"), hpmRoot.get("quoteNo"))
							: cb.equal(mddRoot.get("policyNo"), hpmRoot.get("policyNo")),
							cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),
							cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
							cb.equal(cpmRoot.get("companyId"), hpmRoot.get("companyId")),
							cb.equal(cpmRoot.get("status"), "Y"),
							cb.equal(hpmRoot.get("productId"), cpmRoot.get("productId")),
							cb.between(cb.literal(new Date()), cpmRoot.get("effectiveDateStart"),
									cpmRoot.get("effectiveDateEnd")),
							StringUtils.isBlank(policyNo) ? cb.equal(hpmRoot.get("quoteNo"), quoteNo)
									: cb.equal(hpmRoot.get("policyNo"), policyNo))
					.distinct(true);

			List<Tuple> list = em.createQuery(cq).getResultList();
			if (!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);

				List<Map<String, Object>> companyDetails = insuranceComMasRepo
						.getCompanyDetailsById(map.get("companyId") == null ? "" : map.get("companyId").toString());
				if (!companyDetails.isEmpty()) {
					response.setCompanyName(companyDetails.get(0).get("COMPANY_NAME") == null ? ""
							: companyDetails.get(0).get("COMPANY_NAME").toString());
					response.setCompanylogo(companyDetails.get(0).get("COMPANY_LOGO") == null ? ""
							: companyDetails.get(0).get("COMPANY_LOGO").toString());
					response.setCompanyWebsite(companyDetails.get(0).get("COMPANY_WEBSITE") == null ? ""
							: companyDetails.get(0).get("COMPANY_WEBSITE").toString());
					response.setCompanyMail(companyDetails.get(0).get("COMPANY_EMAIL") == null ? ""
							: companyDetails.get(0).get("COMPANY_EMAIL").toString());
					response.setCompanyPhone(companyDetails.get(0).get("COMPANY_PHONE") == null ? ""
							: companyDetails.get(0).get("COMPANY_PHONE").toString());
					response.setCompanyAddress(companyDetails.get(0).get("COMPANY_ADDRESS") == null ? ""
							: companyDetails.get(0).get("COMPANY_ADDRESS").toString());
					response.setCompanyPoBox(companyDetails.get(0).get("PO_BOX") == null ? ""
							: companyDetails.get(0).get("PO_BOX").toString());
					response.setCompanyVrnNumber(companyDetails.get(0).get("VRN_NUMBER") == null ? ""
							: companyDetails.get(0).get("VRN_NUMBER").toString());
					response.setCompanyremarks(companyDetails.get(0).get("REMARKS") == null ? ""
							: companyDetails.get(0).get("REMARKS").toString());
					response.setCompanySignature(companyDetails.get(0).get("SIGNATURE") == null ? ""
							: companyDetails.get(0).get("SIGNATURE").toString());
				}
				List<EserviceMotorDetails> eserviceMotor= esMotorRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo).stream().filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());
				List<MotorDataDetails> vehicleDetails = motorRepo
						.findByQuoteNoOrderByVehicleIdAsc(map.get("quoteNo").toString()).stream()
						.filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());
				
			
				if (StringUtils.isNotBlank(vehicleId)) {
					vehicleDetails = vehicleDetails.stream()
							.filter(f -> f.getVehicleId() != null && f.getVehicleId().equalsIgnoreCase(vehicleId))
							.collect(Collectors.toList());
				}
				List<PolicyCoverData> cover = coverDataRepository.findByQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				List<VBasedPremium> vIdlist = new ArrayList<>();
				
				String vehiclePremiumDesc = map.get("vehiclePremiumDesc") == null ? ""
						: map.get("vehiclePremiumDesc").toString();
				vehicleDetails.forEach(k -> {
					VBasedPremium vId = new VBasedPremium();
					EserviceMotorDetails eserMotor = eserviceMotor.stream().filter(f -> String.valueOf(f.getRiskId()).equals(k.getVehicleId())).findFirst().orElse(null);
					String all = "";
					LovGetReq listvalue = new LovGetReq();
					listvalue.setItemType("No_Claim_Bonus");
					listvalue.setItemCode(eserMotor.getClaimType());
					listvalue.setInsuranceId(eserMotor.getCompanyId());
					List<DropDownRes> ncbounce = getByItemValue(listvalue);
					listvalue.setItemType("Voluntary discounts");
					listvalue.setItemCode(eserMotor.getExcess());
					listvalue.setInsuranceId(eserMotor.getCompanyId());
					List<DropDownRes> ve = getByItemValue(listvalue);
					listvalue.setItemType("Geographical Extension");
					listvalue.setItemCode(eserMotor.getVehicleClass());
					listvalue.setInsuranceId(eserMotor.getCompanyId());
					List<DropDownRes> geoArea = getByItemValue(listvalue);
					String veDesc =null;
					String ncbDesc =null;
					String geoAreaDesc =null;
					if(ve!=null && !ve.isEmpty())
					{
						veDesc= ve.get(0).getCodeDesc()	;
					}
					if(ncbounce!=null && !ncbounce.isEmpty())
					{
						ncbDesc= ncbounce.get(0).getParam1();
					}
					
					if(geoArea!=null && !geoArea.isEmpty())
					{
						geoAreaDesc= geoArea.get(0).getCodeDesc();
					}
					
					if(eserMotor !=null )
					{
						String gps ="Y".equalsIgnoreCase(eserMotor.getGpsTrackingInstalled()) ? "Yes" : "No" ;
					//	all ="Tracker: " + gps +"," + "NCB: " +eserMotor.getClaimTypeDesc()+","  + "Voluntary Excess: "+eserMotor.getExcessDesc();
						all=gps;
					}
					List<PolicyCoverData> coverData = cover.stream().filter(c-> k.getVehicleId().equalsIgnoreCase(c.getVehicleId().toString())).collect(Collectors.toList());
//					PolicyCoverData gps = coverData.stream().filter(c-> "19".equalsIgnoreCase(c.getDiscLoadId().toString())).findFirst().orElse(null);
//					PolicyCoverData ncb = coverData.stream().filter(c-> "479".equalsIgnoreCase(c.getDiscLoadId().toString())).findFirst().orElse(null);
//					PolicyCoverData vd = coverData.stream().filter(c-> "656".equalsIgnoreCase(c.getDiscLoadId().toString())).findFirst().orElse(null);
					List<PremiumDetailsWithOuttax> coverdetail = getcoverPremiumDetails(coverData);
			//		response.setCoverPremium(coverdetail);
					vId.setList(coverdetail);
					vId.setRegNo(k.getRegistrationNumber() == null ? "" : k.getRegistrationNumber().toString());
				//	vIdlist.add(vId);
					String amount = "";
					String formatted ="";
					if("103".equalsIgnoreCase(k.getSectionId().toString()))
					{
						amount = "1000000";
						formatted = String.format("%,d", Long.parseLong(amount));
					}
					DecimalFormat formatter = new DecimalFormat("#,##0.##");
					MotorPrivateVehicleDetails t = MotorPrivateVehicleDetails.builder()
							.vehicleId(k.getVehicleId() == null ? "" : k.getVehicleId().toString())
							.registrationNumber(k.getRegistrationNumber() == null ? "" : k.getRegistrationNumber().toString())
							.vehicleMake(k.getVehicleMakeDesc() == null ? "" : k.getVehicleMakeDesc().toString())
							.vehcileModel(k.getVehcileModelDesc() == null ? "" : k.getVehcileModelDesc().toString())
							.vehicleTypeDesc(k.getVehicleTypeDesc() == null ? "" : k.getVehicleTypeDesc().toString())
							.cubicCapacity(k.getCubicCapacity() == null ? "" : k.getCubicCapacity().toString())
							.manufactureYear(k.getManufactureYear() == null ? "" : k.getManufactureYear().toString())
							.seatingCapacity(k.getSeatingCapacity() == null? null: (k.getSeatingCapacity() % 1 == 0 ? String.valueOf(k.getSeatingCapacity().intValue()) : String.valueOf(k.getSeatingCapacity())))
							.colorDesc(k.getColorDesc() == null ? "NA" : k.getColorDesc().toString())
							.policyTypeDesc(k.getSectionName() == null ? "" : k.getSectionName())
						//.policyTypeId(k.getPolicyType() == null ? "" : k.getPolicyType())
							.windScreenSumInsuredLc(k.getWindScreenSumInsured() == null ? null : new BigDecimal(Double.parseDouble(k.getWindScreenSumInsured().toString()))
											.toString())
						//	.sumInsured(k.getSumInsured() == null ? null : new BigDecimal(Double.parseDouble(k.getSumInsured().toString())).toString())
							.sumInsured(k.getSumInsured() == null ? null: formatter.format(new BigDecimal(k.getSumInsured().toString())))
							// .stickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString())
							.stickerNumber(getStrickerNo(k.getQuoteNo(), k.getVehicleId()))
							.grossWeight(k.getGrossWeight() == null ? null : k.getGrossWeight().toString())
							.insTypeDesc(k.getInsuranceTypeDesc() == null ? "NA" : k.getInsuranceTypeDesc())
							.engineNumber(k.getEngineNumber() == null ? "NA" : k.getEngineNumber())
							.tPPDIncreaseLimit(k.getTppdIncreaeLimit() == null ? null : new BigDecimal(Double.parseDouble(k.getTppdIncreaeLimit().toString())).toString())
							.chassisNumber(k.getChassisNumber() == null ? "NA" : k.getChassisNumber())
							.fuelType(k.getFuelTypeDesc() == null ? "NA" : k.getFuelTypeDesc())
//							.premium("LC".equalsIgnoreCase(vehiclePremiumDesc) ? k.getOverallPremiumLc() == null ? "" : new BigDecimal(Double.parseDouble(k.getOverallPremiumLc().toString()))
//													.toString(): k.getOverallPremiumFc() == null ? "": new BigDecimal(Double.parseDouble(k.getOverallPremiumFc().toString())).toString())
							
							.premium("LC".equalsIgnoreCase(vehiclePremiumDesc) ? (k.getOverallPremiumLc() == null ? "" : formatter.format(new BigDecimal(k.getOverallPremiumLc().toString()))): (k.getOverallPremiumFc() == null? "" : formatter.format(new BigDecimal(k.getOverallPremiumFc().toString())))
								)
							.inceptionDate(map.get("inceptionDate") == null ? "" : sdf.format(map.get("inceptionDate")))
							.expiryDate(map.get("expiryDate") == null ? "" : sdf.format(map.get("expiryDate")))
							.thirdPartyLiabilityLimit(formatted)
							.tracker(all!=null ? all : "No")
							.ncb(ncbDesc!=null ? ncbDesc: "No")
							.voluntary(veDesc!=null ? veDesc: "No")
							.geographicArea(geoAreaDesc == null ? null : geoAreaDesc)
							.build();
					vehicleDetailsRes.add(t);
				});

				CriteriaBuilder dbuilder = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> driverDtl = dbuilder.createQuery(Tuple.class);
				Root<MotorDriverDetails> dRoot = driverDtl.from(MotorDriverDetails.class);
				Root<MotorDataDetails> dmdRoot = driverDtl.from(MotorDataDetails.class);

				driverDtl.multiselect(dRoot.get("driverId").alias("driverId"),
						dRoot.get("driverName").alias("driverName"),
						dRoot.get("driverTypedesc").alias("driverTypedesc"), dRoot.get("driverDob").alias("driverDob"),
						dRoot.get("idNumber").alias("idNumber"), dmdRoot.get("chassisNumber").alias("chassisNumber"),
						dRoot.get("driverType").alias("driverType"));
				List<Predicate> dPredicate = new ArrayList<Predicate>();
				dPredicate.add(cb.equal(dRoot.get("quoteNo"), map.get("quoteNo").toString()));
				dPredicate.add(cb.equal(dRoot.get("quoteNo"), dmdRoot.get("quoteNo")));
				dPredicate.add(cb.equal(dmdRoot.get("vehicleId"), dRoot.get("riskId").as(String.class)));
				dPredicate.add(cb.equal(dmdRoot.get("companyId"), dRoot.get("companyId")));
				dPredicate.add(cb.equal(dmdRoot.get("productId"), dRoot.get("productId")));
				if (StringUtils.isNotBlank(vehicleId)) {
					dPredicate.add(cb.equal(dRoot.get("riskId"), Integer.parseInt(vehicleId)));
				}
				Predicate dPredicateArray[] = new Predicate[dPredicate.size()];
				dPredicate.toArray(dPredicateArray);
				driverDtl.where(dPredicateArray);
				List<Tuple> driverDetails = em.createQuery(driverDtl).getResultList();
				if (!driverDetails.isEmpty()) {
					driverDetails = driverDetails.stream().filter(
							f -> f.get("driverType") != null && f.get("driverType").toString().equalsIgnoreCase("2"))
							.collect(Collectors.toList());
					driverDetails.forEach(k -> driverDetailsRes.add(MotorPrivateDriverDetails.builder()
							.driverId(k.get("driverId") == null ? "" : k.get("driverId").toString())
							.driverName(k.get("driverName") == null ? "" : k.get("driverName").toString())
							.driverTypeDesc(k.get("driverTypedesc") == null ? "" : k.get("driverTypedesc").toString())
							.driverDOB(k.get("driverDob") == null ? "" : sdf.format(k.get("driverDob")))
							.iDNumber(k.get("idNumber") == null ? "" : k.get("idNumber").toString())
							.chassisNumber(k.get("chassisNumber") == null ? "" : k.get("chassisNumber").toString())
							.build()));
				}

				if (StringUtils.isNotBlank(vehicleId) && StringUtils.isNotBlank(policyNo)) {
					CriteriaBuilder coverdtl = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> pcd = coverdtl.createQuery(Tuple.class);
					Root<PolicyCoverData> pcdRoot = pcd.from(PolicyCoverData.class);
					Root<HomePositionMaster> phpmRoot = pcd.from(HomePositionMaster.class);
					Root<CompanyProductMaster> pcpmRoot = pcd.from(CompanyProductMaster.class);

					pcd.multiselect(pcdRoot.get("coverName").alias("coverName"),
							pcdRoot.get("sumInsured").alias("sumInsured"),
							cb.selectCase()
									.when(cb.in(phpmRoot.get("currency")).value(pcpmRoot.get("currencyIds")),
											pcdRoot.get("premiumAfterDiscountLc"))
									.otherwise(pcdRoot.get("premiumAfterDiscountFc")).alias("premiumAfterDiscount"),
							cb.selectCase()
									.when(cb.in(phpmRoot.get("currency")).value(pcpmRoot.get("currencyIds")),
											pcdRoot.get("premiumIncludedTaxLc"))
									.otherwise(pcdRoot.get("premiumIncludedTaxFc")).alias("premiumIncludedTax"),
							pcdRoot.get("vehicleId").alias("vehicleId"))
							.where(cb.equal(pcpmRoot.get("companyId"), phpmRoot.get("companyId")),
									cb.equal(pcpmRoot.get("status"), "Y"),
									cb.equal(phpmRoot.get("productId"), pcpmRoot.get("productId")),
									cb.between(cb.literal(new Date()), pcpmRoot.get("effectiveDateStart"),
											pcpmRoot.get("effectiveDateEnd")),
									cb.equal(pcdRoot.get("policyNo"), phpmRoot.get("policyNo")),
									cb.equal(pcdRoot.get("taxId"), 0), cb.equal(pcdRoot.get("discLoadId"), 0),
									cb.notEqual(pcdRoot.get("coverageType"), "B"),
									cb.equal(phpmRoot.get("policyNo"), policyNo));

					pcd.where(cb.equal(pcdRoot.get("policyNo"), policyNo),
							cb.equal(pcdRoot.get("vehicleId"), vehicleId),
							cb.equal(pcdRoot.get("companyId"), phpmRoot.get("companyId")),
							cb.equal(phpmRoot.get("policyNo"), policyNo),
							cb.equal(pcpmRoot.get("companyId"), phpmRoot.get("companyId")),
							cb.equal(pcpmRoot.get("productId"), phpmRoot.get("productId")),
							cb.equal(pcpmRoot.get("status"), "Y"),

							// taxId / discLoadId must not filter 0
							cb.or(cb.isNull(pcdRoot.get("taxId")), cb.equal(pcdRoot.get("taxId"), 0)),
							cb.or(cb.isNull(pcdRoot.get("discLoadId")), cb.equal(pcdRoot.get("discLoadId"), 0)));

					List<Tuple> coverDetailsList = em.createQuery(pcd).getResultList();
					if (!coverDetailsList.isEmpty()) {
						coverDetailsList.forEach(k -> {
							CoverDetailsRes m = CoverDetailsRes.builder()
									.coverName(k.get("coverName") == null ? "" : k.get("coverName").toString())
									.sumInsured(k.get("sumInsured") == null ? null : k.get("sumInsured").toString())
									.premiumAfterDiscount(k.get("premiumAfterDiscount") == null ? null
											: k.get("premiumAfterDiscount").toString())
									.premiumIncludedTax(k.get("premiumIncludedTax") == null ? null
											: k.get("premiumIncludedTax").toString())
									.vehicleId(k.get("vehicleId") == null ? "" : k.get("vehicleId").toString()).build();
							coverDetailsRes.add(m);
						});
					}
				} else if ("100019".equalsIgnoreCase(map.get("companyId") == null ? "" : map.get("companyId").toString())) {
					CriteriaBuilder coverdtl = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> pcd = coverdtl.createQuery(Tuple.class);
					Root<PolicyCoverData> pcdRoot = pcd.from(PolicyCoverData.class);
					Root<HomePositionMaster> phpmRoot = pcd.from(HomePositionMaster.class);
					Root<CompanyProductMaster> pcpmRoot = pcd.from(CompanyProductMaster.class);

					pcd.multiselect(pcdRoot.get("coverName").alias("coverName"),
							pcdRoot.get("sumInsured").alias("sumInsured"),
							cb.selectCase()
									.when(cb.in(phpmRoot.get("currency")).value(pcpmRoot.get("currencyIds")),
											pcdRoot.get("premiumAfterDiscountLc"))
									.otherwise(pcdRoot.get("premiumAfterDiscountFc")).alias("premiumAfterDiscount"),
							cb.selectCase()
									.when(cb.in(phpmRoot.get("currency")).value(pcpmRoot.get("currencyIds")),
											pcdRoot.get("premiumIncludedTaxLc"))
									.otherwise(pcdRoot.get("premiumIncludedTaxFc")).alias("premiumIncludedTax"),
							pcdRoot.get("vehicleId").alias("vehicleId"))
							.where(cb.equal(pcpmRoot.get("companyId"), phpmRoot.get("companyId")),
									cb.equal(pcpmRoot.get("status"), "Y"),
									cb.equal(phpmRoot.get("productId"), pcpmRoot.get("productId")),
									cb.between(cb.literal(new Date()), pcpmRoot.get("effectiveDateStart"),
											pcpmRoot.get("effectiveDateEnd")),
									cb.equal(pcdRoot.get("quoteNo"), phpmRoot.get("quoteNo")),
									cb.or(cb.isNull(pcdRoot.get("taxId")), cb.equal(pcdRoot.get("taxId"), 0)),
									cb.or(cb.isNull(pcdRoot.get("discLoadId")), cb.equal(pcdRoot.get("discLoadId"), 0)),
									cb.equal(phpmRoot.get("quoteNo"), quoteNo));

					List<Tuple> coverDetailsList = em.createQuery(pcd).getResultList();
					if (!coverDetailsList.isEmpty()) {
						coverDetailsList.forEach(k -> {
							CoverDetailsRes m = CoverDetailsRes.builder()
									.coverName(k.get("coverName") == null ? "" : k.get("coverName").toString())
									.sumInsured(k.get("sumInsured") == null ? null : k.get("sumInsured").toString())
									.premiumAfterDiscount(k.get("premiumAfterDiscount") == null ? null: k.get("premiumAfterDiscount").toString())
									.premiumIncludedTax(k.get("premiumIncludedTax") == null ? null: k.get("premiumIncludedTax").toString())
									.vehicleId(k.get("vehicleId") == null ? "" : k.get("vehicleId").toString()).build();
							coverDetailsRes.add(m);
						});
					}

				}
//				List<ContentAndRisk> accessoriesDetails = conAndRiskRepo.findByQuoteNoOrderByRiskIdAsc(map.get("quoteNo").toString());
//				accessoriesDetails.forEach(a -> {
//					MotorPrivateAccessoriesDetails t = MotorPrivateAccessoriesDetails.builder()
//							.itemNo(a.getItemId()==null?"":a.getItemId().toString())
//							.itemDesc(a.getItemDesc()==null?"":a.getItemDesc().toString())
//							.sumInsured(a.getSumInsured()==null?"":a.getSumInsured().toString())
//							.serialNoDesc(a.getSerialNoDesc()==null?"":a.getSerialNoDesc())
//							.build();
//					accessoriesDetailsRes.add(t);
//				});

				List<MotorDataDetails> collateralDetails = vehicleDetails.stream()
						.filter(f -> "Y".equalsIgnoreCase(Objects.requireNonNullElse(f.getCollateralYn(), "")))
						.collect(Collectors.toList());
			
				if(collateralDetails!=null && !collateralDetails.isEmpty()) {
					response.setCollateralYn("Y");
					List<CollertalRes> coList = collateralDetails.stream().map(c -> {
					CollertalRes col = new CollertalRes();
					col.setBorrowerType(
						StringUtils.isBlank(c.getBorrowerTypeDesc()) ? "" : c.getBorrowerTypeDesc());
					col.setCollateralName(
							StringUtils.isBlank( c.getCollateralName()) ? "" : c.getCollateralName());
					col.setFirstLossPayee(
							StringUtils.isBlank(c.getFirstLossPayee()) ? "" : c.getFirstLossPayee());
					return col;
					}).collect(Collectors.toList());
					response.setCollater(coList);
				}
				
				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);

				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"), map.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"), sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				Predicate[] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);

				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(
						cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),
						cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),
						cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));

				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),
						sddRoot.get("sectionDesc").alias("sectionDesc"), pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"), pcdRoot.get("coverageType").alias("coverageType"),
						pcdRoot.get("sumInsured").alias("sumInsured"), pcdRoot.get("rate").alias("rate"),
						pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),
						occDesc.alias("occupationDesc"),
						pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),
						pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc")).where(predicateArray)
						.orderBy(cb.asc(sddRoot.get("sectionId")));

				List<Tuple> Slist = em.createQuery(cq1).getResultList();

				List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct()
						.collect(Collectors.toList());
				for (int i = 0; i < sectionIds.size(); i++) {
					String sectionId = sectionIds.get(i).toString();
					// CONDITIONS
					List<Map<String, Object>> conditionList = getConditionList(
							map.get("policyNo") == null ? "" : map.get("policyNo").toString(),
							map.get("quoteNo") == null ? "" : map.get("quoteNo").toString(), sectionId);

					// EXCLUSION
					List<Map<String, Object>> exclusionRes = getExclusionList(
							map.get("policyNo") == null ? "" : map.get("policyNo").toString(),
							map.get("quoteNo") == null ? "" : map.get("quoteNo").toString(), sectionId);
					List<Map<String, Object>> exclusionList = exclusionRes.stream().map(k -> {
						Map<String, Object> eMap = new HashMap<String, Object>();
						eMap.put("conditionTerms", k.get("exclusioTerms"));
						return eMap;
					}).collect(Collectors.toList());

					// WARRANTY
					List<Map<String, Object>> warrantyList = getWarrantyDescription(
							map.get("policyNo") == null ? "" : map.get("policyNo").toString(),
							map.get("quoteNo") == null ? "" : map.get("quoteNo").toString(), sectionId);

					List<Map<String, Object>> termsAndconditions = Stream.of(conditionList, exclusionList, warrantyList)
							.flatMap(Collection::stream).collect(Collectors.toList());
					termsAndconditions.stream().distinct().collect(Collectors.toList()).forEach(g -> {
						TearmsAndCondition tearms = new TearmsAndCondition();
						tearms.setAllConditions(
								g.get("conditionTerms") == null ? "" : g.get("conditionTerms").toString());
						tearmsAndConditionRes.add(tearms);
					});
				}
				if (StringUtils.isNotBlank(vehicleId)) {
					List<LinkedHashMap<String, Object>> vehiclePremiumList = new ArrayList<LinkedHashMap<String, Object>>(
							4);
					vehiclePremiumList.add(new LinkedHashMap<String, Object>() {
						{
							put("Premium", map.get("vehiclePremium") == null ? ""
									: new BigDecimal(Double.parseDouble(map.get("vehiclePremium").toString())));
						}
					});
					vehiclePremiumList.add(new LinkedHashMap<String, Object>() {
						{
							put("Vat Premium @ "
									+ (map.get("vatPercent") == null ? "" : map.get("vatPercent").toString()) + " %",
									map.get("vehicleVatPremium") == null ? ""
											: new BigDecimal(
													Double.parseDouble(map.get("vehicleVatPremium").toString())));
						}
					});
					vehiclePremiumList.add(new LinkedHashMap<String, Object>() {
						{
							put("Total Premium", map.get("vehicelTotalPremium") == null ? ""
									: new BigDecimal(Double.parseDouble(map.get("vehicelTotalPremium").toString())));
						}
					});
					for (LinkedHashMap<String, Object> k : vehiclePremiumList) {
						TaxInvoicePremiumDetails v = TaxInvoicePremiumDetails.builder()
								.amount(k.values().stream().map(m -> {
									return BigDecimal.valueOf(Double.parseDouble(m.toString())).toString();
								}).collect(Collectors.joining(", "))).narration(String.join(", ", k.keySet())).build();
						premiumDetailsRes.add(v);
					}
					/*
					 * List<MultiplePolicyDrCrDetail> vehiclePremiumDetails =
					 * multiPolicyDrCrDtlRepo.findByQuoteNoAndRiskId(map.get("quoteNo")==null?"":map
					 * .get("quoteNo").toString(),Integer.parseInt(vehicleId));
					 * if(!vehiclePremiumDetails.isEmpty()) { vehiclePremiumDetails =
					 * vehiclePremiumDetails.stream().filter(f -> f.getChargeCode().compareTo(new
					 * BigDecimal(1003)) !=0 && f.getChargeCode().compareTo(new BigDecimal(1004))
					 * !=0).sorted(Comparator.comparing(MultiplePolicyDrCrDetail::getDisplayOrder)).
					 * collect(Collectors.toList()); vehiclePremiumDetails.forEach(h ->{
					 * TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
					 * .amount(h.getAmountFc()==null?"":new
					 * BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
					 * .narration(h.getNarration()==null?"":h.getNarration().replaceAll(
					 * "\\n|\\t|\\r|\\r\\n|\\f|", "")) .build(); premiumDetailsRes.add(u); }); }
					 */
				} else if ("100020".equalsIgnoreCase(map.get("companyId") == null ? "" : map.get("companyId").toString())) {
					List<PolicyCoverData> coverData = coverDataRepository
							.findByQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
					if (coverData != null && !coverData.isEmpty()) {

						Double taxAmount = coverData.stream()
								.filter(f -> f.getTaxId() != 0 && f.getCoverageType().equalsIgnoreCase("T")
										&& f.getSectionId() != 99999)
								.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));

						List<Map<String, Object>> sectionPremium = coverData.stream().filter(f -> f.getTaxId() == 0
								&& f.getDiscLoadId() == 0 && f.getSectionId() != 99999
								&& (f.getCoverageType().equals("O")
										&& (f.getIsSelected().equalsIgnoreCase("Y") ? "Y" : "N").equalsIgnoreCase("Y")
										|| !f.getCoverageType().equalsIgnoreCase("O")))
								.collect(Collectors.groupingBy(a -> a.getSectionId(),
										Collectors.groupingBy(b -> b.getCoverName(),
												Collectors.reducing(BigDecimal.ZERO,
														PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
								.entrySet().stream().flatMap((Map.Entry<Integer, Map<String, BigDecimal>> s) -> {
									Integer sectionId = s.getKey();
									return s.getValue().entrySet().stream().map((Map.Entry<String, BigDecimal> g) -> {
										String coverDesc = g.getKey();
										BigDecimal totPremium = g.getValue();
										Map<String, Object> secMap = new HashMap<String, Object>();
										Double sumInsured = coverData.stream()
												.filter(f -> f.getTaxId() == 0 && f.getDiscLoadId() == 0
														&& f.getSectionId() == sectionId)
												.map(m -> m.getSumInsured())
												.collect(Collectors.summingDouble(BigDecimal::doubleValue));
										secMap.put("SectionId", sectionId);
										secMap.put("CoverDesc", coverDesc.toUpperCase());
										secMap.put("TotPremium", totPremium);
										secMap.put("SumInsured", sumInsured);
										return secMap;
									});
								}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());
						sectionPremium.forEach(k -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
									.sumInsured(new BigDecimal(Double.valueOf(k.get("SumInsured").toString())).toString())
									.narration(k.get("CoverDesc") == null ? "": k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
									.build();
							premiumDetailsRes.add(u);
						});

						OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount()))
								.collect(Collectors.summingDouble(BigDecimal::doubleValue)) + taxAmount;
					}

				} else {
					List<PolicyDrcrDetail> drcrDetails = drcrdetail.findByQuoteNoAndStatusIn(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString(), Arrays.asList("Y", "CV"));
					List<PolicyDrcrDetail> listByRiskId = drcrDetails.stream()
							.filter(r -> r.getDrcrFlag().equalsIgnoreCase("DR"))
							.sorted(Comparator.comparing(PolicyDrcrDetail::getDisplayOrder))
							.collect(Collectors.toList());
					String totalAmount = listByRiskId.stream()
					        .map(PolicyDrcrDetail::getAmountFc)
					        .filter(Objects::nonNull)
					        .map(amount -> new BigDecimal(amount.toString()))
					        .reduce(BigDecimal.ZERO, BigDecimal::add)
					        .toPlainString();
					listByRiskId.forEach(h -> {
						TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
								.amount(h.getAmountFc() == null ? "": new BigDecimal(Double.parseDouble(h.getAmountFc().toString())).toPlainString())
								.narration(h.getNarration() == null ? "": h.getNarration().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
								.status(h.getStatus()).build();
						premiumDetailsRes.add(u);
					});
					TaxInvoicePremiumDetails total = TaxInvoicePremiumDetails.builder()
							.amount(totalAmount)
							.narration("Total")
							.status("Y").build();
					premiumDetailsRes.add(total);
				}
				if (!"100020".equalsIgnoreCase(map.get("companyId") == null ? "" : map.get("companyId").toString()))
					OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount()))
							.collect(Collectors.summingDouble(BigDecimal::doubleValue));

				if ("100019".equalsIgnoreCase(map.get("companyId") == null ? "" : map.get("companyId").toString()) || "100049".equalsIgnoreCase(map.get("companyId") == null ? "" : map.get("companyId").toString())) {
					String loginId = map.get("loginId") == null ? "" : map.get("loginId").toString();
					if (StringUtils.isNotBlank(loginId)) {
						CriteriaBuilder cb1 = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> doc = cb1.createQuery(Tuple.class);
						Root<DocumentUniqueDetails> dudRoot = doc.from(DocumentUniqueDetails.class);
						Root<ClausesMaster> cmRoot = doc.from(ClausesMaster.class);
						Root<LoginMaster> lmRoot = doc.from(LoginMaster.class);
						Root<LoginUserInfo> dlui = doc.from(LoginUserInfo.class);

						Subquery<Integer> cmAmd = cq.subquery(Integer.class);
						Root<ClausesMaster> cmAmdRoot = cmAmd.from(ClausesMaster.class);

						cmAmd.select(cb.max(cmAmdRoot.get("amendId"))).where(
								cb.equal(cmAmdRoot.get("brokerCode"), dlui.get("customerCode")),
								cb.equal(cmAmdRoot.get("clausesId"), cmRoot.get("clausesId")),
								cb.equal(cmAmdRoot.get("status"), cmRoot.get("status")));

						doc.multiselect(cmRoot.get("docRefNo").alias("docRefNo"),
								dudRoot.get("filePathOrginal").alias("filePathOrginal"))
								.where(cb.equal(lmRoot.get("loginId"), dlui.get("loginId")),
										cb.equal(dlui.get("customerCode"), cmRoot.get("brokerCode")),
										cb.equal(cmRoot.get("docRefNo").as(String.class),dudRoot.get("uniqueId").as(String.class)),
										cb.equal(lmRoot.get("loginId").as(String.class), loginId),
										cb.equal(cmRoot.get("status"), "Y"), cb.equal(cmRoot.get("amendId"), cmAmd));

						List<Tuple> docList = em.createQuery(doc).getResultList();
						if (!docList.isEmpty()) {
							docList.forEach(e -> {
								AttachMentRes m = AttachMentRes.builder()
										.docRefNo(e.get("docRefNo") == null ? "" : e.get("docRefNo").toString())
										.docloction(e.get("filePathOrginal") == null ? "": e.get("filePathOrginal").toString())
										.build();
								attachments.add(m);
							});
						}
					}
					if (attachments == null || attachments.isEmpty()) {
						if (vehicleDetails != null && "100049".equalsIgnoreCase(
								map.get("companyId") == null ? "" : map.get("companyId").toString())) {
							boolean isComprehensive = vehicleDetails.stream().map(m -> m.getSectionName().toLowerCase())
									.distinct().anyMatch(k -> k.equals("comprehensive"));
							if (isComprehensive) {
								attachments.addAll(getAttachMentList(map.get("companyId") == null ? "" : map.get("companyId").toString(),map.get("productId") == null ? "" : map.get("productId").toString(),"ATTACHMENTS", "CH"));
							}
						}

						if (vehicleDetails != null) {
							vehicleDetails.stream().map(m -> m.getMotorUsage()).distinct().collect(Collectors.toList())
									.forEach(k -> {
										if(k!=null) {
										String bodyType = motorVehicleUsageRepo
												.findByCompanyIdAndVehicleUsageIdOrderByAmendIdDesc(map.get("companyId") == null ? "": map.get("companyId").toString(),
														Integer.parseInt(k))
												.get(0).getBodyType();
										attachments.addAll(getAttachMentList(map.get("companyId") == null ? "" : map.get("companyId").toString(),map.get("productId") == null ? "" : map.get("productId").toString(),
												"ATTACHMENTS", bodyType));
									}
									});
						} else {
							attachments.addAll(getAttachMentList(map.get("companyId") == null ? "" : map.get("companyId").toString(),map.get("productId") == null ? "" : map.get("productId").toString(), "ATTACHMENTS",
									null));
						}
					}
				} else {
					attachments.addAll(getAttachMentList(map.get("companyId") == null ? "" : map.get("companyId").toString(),map.get("productId") == null ? "" : map.get("productId").toString(), "ATTACHMENTS", null));
				}

				String polNo = map.get("policyNo") == null ? "" : map.get("policyNo").toString();
				if (StringUtils.isNotBlank(vehicleId)) {
					polNo += "/" + vehicleId;
				}
				
				
				
				String subUserType = map.get("subUserType") == null ? "" : map.get("subUserType").toString();
				response.setCustomerId(map.get("customerId") == null ? "" : map.get("customerId").toString());
				response.setCompanyId(map.get("companyId") == null ? "" : map.get("companyId").toString());
				response.setEffectiveDateStart(map.get("inceptionDate") == null ? "" : map.get("inceptionDate").toString());
				response.setEffectiveDateEnd(map.get("expiryDate") == null ? "" : map.get("expiryDate").toString());
				response.setEffectiveDate(map.get("effectiveDateHome") == null ? "" : map.get("effectiveDateHome").toString());
				response.setEntryDate(map.get("entryDatehome") == null ? "" : map.get("entryDatehome").toString());
				response.setPolicyNo(polNo);
				response.setQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				response.setCustomerName(map.get("customerName") == null ? "" : map.get("customerName").toString());
				response.setDebitNoteNo(map.get("debitNoteNo") == null ? "" : map.get("debitNoteNo").toString());
				response.setAddress(map.get("address") == null ? "" : map.get("address").toString());
				response.setInceptionDate(map.get("inceptionDate") == null ? "" : map.get("inceptionDate").toString());
				response.setExpiryDate(map.get("expiryDate") == null ? "" : map.get("expiryDate").toString());
				response.setRenewalDate(map.get("expiryDate") == null ? "" : RenewalDate(map.get("expiryDate").toString()));
				response.setCurrency(map.get("currency") == null ? "" : map.get("currency").toString());
				// response.setStickerNumber(map.get("stickerNumber")==null?"":map.get("stickerNumber").toString());
				response.setInsuranceTypeDesc(map.get("insuranceTypeDesc") == null ? "" : map.get("insuranceTypeDesc").toString());
				response.setPremium(map.get("premium") == null ? "": new BigDecimal(Double.parseDouble(map.get("premium").toString())).toString());
				response.setVatPremium(map.get("vatPremium") == null ? "": new BigDecimal(Double.parseDouble(map.get("vatPremium").toString())).toString());
				response.setOverAllPremium(map.get("totalPremium") == null ? "": new BigDecimal(Double.parseDouble(map.get("totalPremium").toString())).toString());
				response.setTotalPremium(new BigDecimal(OverAllPremium).toString());
				response.setBranchName(map.get("branchName") == null ? "" : map.get("branchName").toString());
				response.setApprovedBy(map.get("approvedBy") == null ? "" : map.get("approvedBy").toString());
				response.setUserName(subUserType.toLowerCase().contains("b2c")? map.get("customerName") == null ? "" : map.get("customerName").toString(): map.get("userName") == null ? "" : map.get("userName").toString());
				response.setNoOfVehicle(map.get("noOfVehicle") == null ? "" : map.get("noOfVehicle").toString());
				response.setPostalAddress(StringUtils.join(Arrays.asList(map.get("address") == null ? "" : map.get("address").toString(),
								map.get("pinCode") == null ? "" : map.get("pinCode").toString(),
								map.get("stateName") == null ? "" : map.get("stateName").toString(),
								map.get("cityName") == null ? "" : map.get("cityName").toString(),
								map.get("countryName") == null ? "" : map.get("countryName").toString())
						.stream().filter(value -> !value.isEmpty()).collect(Collectors.joining(","))));
				response.setBorrowerType(collateralDetails.isEmpty() ? "" : collateralDetails.get(0).getBorrowerTypeDesc());
				response.setCollateralName(collateralDetails.isEmpty() ? "" : collateralDetails.get(0).getCollateralName());
				response.setFirstLossPayee(collateralDetails.isEmpty() ? "" : collateralDetails.get(0).getFirstLossPayee());
				response.setCoverNoteReferenceNo(map.get("coverNoteReferenceNo") == null ? "" : map.get("coverNoteReferenceNo").toString());
				response.setBusiness(map.get("business") == null ? "" : map.get("business").toString());
				response.setBrokerLogo(map.get("brokerLogo") == null ? "" : map.get("brokerLogo").toString());
				response.setBdmName(map.get("bdmName") == null ? "" : map.get("bdmName").toString());
				response.setVatPercent(map.get("vatPercent") == null ? "" : map.get("vatPercent").toString());
				response.setMobileNo1(map.get("mobileNo1") == null ? "" : map.get("mobileNo1").toString());
				response.setEmail1(map.get("email1") == null ? "" : map.get("email1").toString());
				response.setLoginId(map.get("loginId") == null ? "" : map.get("loginId").toString());
				response.setSubUserType(subUserType);
				response.setVehicleDetails(vehicleDetailsRes);
				response.setDriverDetails(driverDetailsRes);
				response.setAccessoriesDetails(accessoriesDetailsRes);
				if(!"100049".equalsIgnoreCase(map.get("companyId") == null ? "" : map.get("companyId").toString()))
				{
				response.setTearmsAndConditions(tearmsAndConditionRes);
				}
				if ("Y".equalsIgnoreCase(map.get("emiYn") == null ? "" : map.get("emiYn").toString())) {
					List<EmiTransactionDetails> emiList = emiRepo.findByQuoteNoAndCompanyIdAndProductId(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString(),map.get("companyId") == null ? "" : map.get("companyId").toString(),map.get("productId") == null ? "" : map.get("productId").toString());
					if(!emiList.isEmpty() && emiList!=null)
					{
						emiList.sort(Comparator.comparingInt(e -> Integer.parseInt(e.getInstalment())));
						 DecimalFormat df = new DecimalFormat("#,##0.00");
						TaxInvoicePremiumDetails emi1 = new TaxInvoicePremiumDetails();
						emi1.setAmount(emiList.get(0).getDueAmount() != null ? df.format(emiList.get(0).getDueAmount()) : "0.00");
						emi1.setNarration("1st Month Premium");
						premiumDetailsRes.add(emi1);
						TaxInvoicePremiumDetails emi2 = new TaxInvoicePremiumDetails();
						emi2.setAmount(emiList.get(1).getDueAmount() != null ? df.format(emiList.get(1).getDueAmount()) : "0.00");
						emi2.setNarration(emiList.get(1).getInstallmentTypeDesc());
						premiumDetailsRes.add(emi2);
						List<getEmiDetailsListRes> emiDetails=getEMIdetails(emiList);
						emiDetails.sort(Comparator.comparingInt(e -> Integer.parseInt(e.getInstallmentId())));
					//	response.setEmi(emiDetails);
						response.setEmiYn("Y");
					//	response.setEmiDetails(emiListTax);
					}
				}
				response.setPremiumDetails(premiumDetailsRes);
				response.setAttachmentList(attachments);
				response.setCoverDetailsList(coverDetailsRes);
				response.setMotorType(map.get("sectionName") == null ? "" : map.get("sectionName").toString());
				response.setCoverPremium(vIdlist);
				response.setApprovedBy(map.get("loginId") == null ? "" : map.get("loginId").toString());
				response.setCreatedBy(map.get("loginId") == null ? "" : map.get("loginId").toString());
				List<TaxInvoicePremiumDetails> emiListTax = new ArrayList<>();
				
				
				}
		} catch (Exception e) {
			log.info("Error in getMotorPrivate ==>" + e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getMotorPrivate");
		return response;
	}

	private List<getEmiDetailsListRes> getEMIdetails(List<EmiTransactionDetails> emiList) {
		try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    DecimalFormat df = new DecimalFormat("#,##0.00");

	    return emiList.stream().map(emi -> {
	        getEmiDetailsListRes res = new getEmiDetailsListRes();

	        res.setInstallmentId(emi.getInstalment());
	        res.setDueDate(emi.getDueDate() != null ? sdf.format(emi.getDueDate()) : "");
	        res.setDueAmount(emi.getDueAmount() != null ? df.format(emi.getDueAmount()) : "0.00");

	        return res;
	    }).collect(Collectors.toList());
		}catch (Exception e) {
			log.info("Error in getEMIdetails ==>" + e.getMessage());
			e.printStackTrace();
			 return Collections.emptyList();
		}
	}

	private List<PremiumDetailsWithOuttax> getcoverPremiumDetails(List<PolicyCoverData> coverData) {
		try {

			if (coverData != null && !coverData.isEmpty()) {

				DecimalFormat df = new DecimalFormat("#,##0.##");

				List<PolicyCoverData> withOuttax = coverData.stream()
						.filter(c -> !"T".equalsIgnoreCase(c.getCoverageType())).collect(Collectors.toList());

				List<PremiumDetailsWithOuttax> premiumList = withOuttax.stream()

						.filter(c -> {

							// Exclude section 99999
							if ("99999".equalsIgnoreCase(String.valueOf(c.getSectionId()))) {
								return false;
							}

							// Always include Benefit covers
							if ("A".equalsIgnoreCase(c.getCoverageType())) {
								return true;
							}

							// Include other covers only if premium > 0
							return c.getPremiumIncludedTaxFc() != null
									&& c.getPremiumIncludedTaxFc().compareTo(BigDecimal.ZERO) > 0;
						})

						// Custom Order:
						// Base -> Discount -> Loading -> Optional -> Benefit
						.sorted(Comparator.comparingInt(c -> {
							switch (c.getCoverageType().toUpperCase()) {
							case "B":
								return 1; // Base
							case "D":
								return 2; // Discount
							case "L":
								return 3; // Loading
							case "O":
								return 4; // Optional
							case "A":
								return 5; // Benefit
							default:
								return 6;
							}
						}))

						.map(c -> {

							PremiumDetailsWithOuttax pd = new PremiumDetailsWithOuttax();

							pd.setCoverName(c.getCoverName());

							// Sum Insured / Coverage Limit
							if ("B".equalsIgnoreCase(c.getCoverageType())) {
							//	pd.setRate(c.getRate() != null ? c.getRate().toPlainString() : "");
								pd.setSumInsured(c.getSumInsured() != null ? df.format(c.getSumInsured()) : "");
								pd.setCoverAgeLimite("");
								pd.setPremiumIncludeTax(c.getPremiumBeforeDiscountFc() != null? df.format("D".equalsIgnoreCase(c.getCoverageType())
										? c.getPremiumBeforeDiscountFc().multiply(BigDecimal.valueOf(-1))
										: c.getPremiumBeforeDiscountFc())
								: "");

							}else if ("O".equalsIgnoreCase(c.getCoverageType())) {
								pd.setRate(c.getRate() != null ? c.getRate().toPlainString() : "");
								pd.setSumInsured(c.getSumInsured() != null ? df.format(c.getSumInsured()) : "");
								pd.setCoverAgeLimite("");
								pd.setPremiumIncludeTax(c.getPremiumIncludedTaxFc() != null? df.format("D".equalsIgnoreCase(c.getCoverageType())
										? c.getPremiumIncludedTaxFc().multiply(BigDecimal.valueOf(-1))
										: c.getPremiumIncludedTaxFc())
								: "");

							}
							else if ("A".equalsIgnoreCase(c.getCoverageType())) {
								pd.setRate(c.getRate() != null ? c.getRate().toPlainString() : "");
								pd.setCoverAgeLimite(
										c.getCoverageLimit() != null ? df.format(c.getCoverageLimit()) : "");
								pd.setSumInsured("");
								pd.setPremiumIncludeTax(c.getPremiumIncludedTaxFc() != null? df.format("D".equalsIgnoreCase(c.getCoverageType())
										? c.getPremiumIncludedTaxFc().multiply(BigDecimal.valueOf(-1))
										: c.getPremiumIncludedTaxFc())
								: "");

							} else {
								pd.setRate(c.getRate() != null ? c.getRate().toPlainString() : "");
								pd.setCoverAgeLimite("");
								pd.setSumInsured("");
								pd.setPremiumIncludeTax(c.getPremiumIncludedTaxFc() != null? df.format("D".equalsIgnoreCase(c.getCoverageType())
										? c.getPremiumIncludedTaxFc().multiply(BigDecimal.valueOf(-1))
										: c.getPremiumIncludedTaxFc())
								: "");
							}

						

							String coverAgeType;

							switch (c.getCoverageType()) {
							case "B":
								coverAgeType = "Base";
								break;

							case "D":
								coverAgeType = "Discount";
								break;

							case "L":
								coverAgeType = "Loading";
								break;

							case "O":
								coverAgeType = "Optional";
								break;

							case "A":
								coverAgeType = "Benefit";
								break;

							default:
								coverAgeType = c.getCoverageType();
								break;
							}

							pd.setCoverAgeType(coverAgeType);

							return pd;

						}).collect(Collectors.toList());

				return premiumList;
			}

		} catch (Exception e) {
			log.info("Error in getcoverPremiumDetails ==> {}", e.getMessage(), e);
		}

		return Collections.emptyList();
	}
	

	public String getStrickerNo(String quoteNo,String vehicleId) {
		String result = null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SectionDataDetails> cq = cb.createQuery(SectionDataDetails.class);
			Root<SectionDataDetails> stnoRoot = cq.from(SectionDataDetails.class);
			cq.select(stnoRoot).where(cb.equal(stnoRoot.get("quoteNo"), quoteNo), // stnoRoot.get("stickerNumber")
					cb.equal(stnoRoot.get("riskId"),vehicleId));
			List<SectionDataDetails> data = em.createQuery(cq).getResultList();
			if(data!=null && data.size()>0) {
				result = data.get(0).getStickerNumber();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public SectionDataDetails getStickerDetails(String quoteNo, String vehicleId) {
	    SectionDataDetails result = null;
	    try {
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<SectionDataDetails> cq = cb.createQuery(SectionDataDetails.class);
	        Root<SectionDataDetails> root = cq.from(SectionDataDetails.class);
 
	        cq.select(root).where(
	                cb.equal(root.get("quoteNo"), quoteNo),
	                cb.equal(root.get("riskId"), vehicleId)
	        );
 
	        List<SectionDataDetails> data = em.createQuery(cq).getResultList();
	        if (data != null && !data.isEmpty()) {
	            result = data.get(0);
	        }
 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}

	public TravelReportRes getTravelReport(String policyNo) {
		log.info("Enter into getTravelReport.\nArgument ==> PolicyNo :"+policyNo);
		TravelReportRes response = new TravelReportRes();
		try {
			List<TravelDataSetOneRes> travelDataSetOne = new ArrayList<TravelDataSetOneRes>();
			List<TravelDataSetTwoRes> travelDataSetTwo = new ArrayList<TravelDataSetTwoRes>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<LoginMaster> lmRoot = cq.from(LoginMaster.class);

			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCmAmd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCmAmd.get("amendId"))).where(cb.equal(SubCmAmd.get("countryId"), piRoot.get("nationality")),cb.equal(SubCmAmd.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(SubCmAmd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));

			Subquery<String> currencyId =cq.subquery(String.class);
			Root<InsuranceCompanyMaster> icmRoot = currencyId.from(InsuranceCompanyMaster.class);
			currencyId.select(icmRoot.get("currencyId")).where(cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")));

			Subquery<Double> overAllPremiumLc = cq.subquery(Double.class);
			Root<TravelPassengerDetails> SuboverAllPremiumLcRoot = overAllPremiumLc.from(TravelPassengerDetails.class);
			overAllPremiumLc.select(cb.sum(SuboverAllPremiumLcRoot.get("overallPremiumLc")).as(Double.class))
			.where(cb.equal(SuboverAllPremiumLcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

			Subquery<Double> overAllPremiumFc = cq.subquery(Double.class);
			Root<TravelPassengerDetails> SuboverAllPremiumFcRoot = overAllPremiumFc.from(TravelPassengerDetails.class);
			overAllPremiumFc.select(cb.sum(SuboverAllPremiumFcRoot.get("overallPremiumFc")).as(Double.class))
			.where(cb.equal(SuboverAllPremiumFcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

			Subquery<Double> premiumLc = cq.subquery(Double.class);
			Root<TravelPassengerDetails> SubpremiumLcRoot = premiumLc.from(TravelPassengerDetails.class);
			premiumLc.select(cb.sum(SubpremiumLcRoot.get("actualPremiumLc")).as(Double.class))
			.where(cb.equal(SubpremiumLcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

			Subquery<Double> premiumFc = cq.subquery(Double.class);
			Root<TravelPassengerDetails> SubpremiumFcRoot = premiumFc.from(TravelPassengerDetails.class);
			premiumFc.select(cb.sum(SubpremiumFcRoot.get("actualPremiumFc")).as(Double.class))
			.where(cb.equal(SubpremiumFcRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

			Subquery<Long> noOfPassanger = cq.subquery(Long.class);
			Root<TravelPassengerDetails> SubnoOfPassanger= noOfPassanger.from(TravelPassengerDetails.class);
			noOfPassanger.select(cb.count(SubnoOfPassanger)).where(cb.equal(SubnoOfPassanger.get("quoteNo"), hpmRoot.get("quoteNo")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("policyNo").alias("policyNo"),cb.upper(cb.concat(piRoot.get("titleDesc"),
					cb.concat(cb.selectCase().when(cb.isNotNull(piRoot.get("titleDesc")), ".")
							.when(cb.equal(piRoot.get("titleDesc"),""), "")
							.otherwise("").as(String.class), piRoot.get("clientName")))).alias("customerName"),cb.concat(piRoot.get("address1"), cb.concat(cb.coalesce(piRoot.get("pinCode"), ""),
									cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), 
											cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",", countryName))))))).alias("address"),
					piRoot.get("telephoneNo1").alias("telephoneNo1"),lmRoot.get("agencyCode").alias("agencyCode"),hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),
					hpmRoot.get("currency").alias("currency"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), overAllPremiumLc).otherwise(overAllPremiumFc).alias("overAllPremium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), premiumLc).otherwise(premiumFc).alias("premium"),
					noOfPassanger.alias("noOfPassanger"),companyName.alias("companyName"),imageURL.alias("companylogo"))
			.where(cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(lmRoot.get("loginId"), hpmRoot.get("loginId")),
					cb.equal(hpmRoot.get("productId"), "4"),cb.equal(hpmRoot.get("status"), "P"),cb.equal(hpmRoot.get("policyNo"), policyNo));

			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				CriteriaBuilder cb1 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
				Root<TravelPassengerDetails> tpdRoot1 = cq1.from(TravelPassengerDetails.class);
				cq1.multiselect(cb.upper(tpdRoot1.get("passengerName")).alias("passengerName"),tpdRoot1.get("dob").alias("dob"),tpdRoot1.get("age").alias("age"),
						tpdRoot1.get("relationDesc").alias("relationDesc"),tpdRoot1.get("passportNo").alias("passportNo"),tpdRoot1.get("travelCoverDuration").alias("travelCoverDuration"))
				.where(cb.equal(tpdRoot1.get("quoteNo"), map.get("quoteNo"))).orderBy(cb.asc(tpdRoot1.get("passengerName")));

				List<Tuple> passangerDetails = em.createQuery(cq1).getResultList();

				for(int i=0;i<passangerDetails.size();i++) {
					TravelDataSetOneRes o = new TravelDataSetOneRes();
					o.setSno(String.valueOf(i+1));
					o.setPassengerName(passangerDetails.get(i).get("passengerName")==null?"":passangerDetails.get(i).get("passengerName").toString());
					o.setDob(passangerDetails.get(i).get("dob")==null?"":passangerDetails.get(i).get("dob").toString());
					o.setAge(passangerDetails.get(i).get("age")==null?"":passangerDetails.get(i).get("age").toString());
					o.setRelationDesc(passangerDetails.get(i).get("relationDesc")==null?"":passangerDetails.get(i).get("relationDesc").toString());
					o.setPassportNo(passangerDetails.get(i).get("passportNo")==null?"":passangerDetails.get(i).get("passportNo").toString());
					o.setTravelCoverDuration(passangerDetails.get(i).get("travelCoverDuration")==null?"":passangerDetails.get(i).get("travelCoverDuration").toString());
					travelDataSetOne.add(o);
				}

				CriteriaBuilder cb2 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq2 = cb2.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
				Root<TravelPassengerDetails> tpdRoot2 = cq2.from(TravelPassengerDetails.class);
				Root<ProductGroupMaster> pgmRoot2 = cq2.from(ProductGroupMaster.class);
				Root<PolicyCoverData> pcdRoot2 = cq2.from(PolicyCoverData.class);

				Subquery<Long> sumInsured = cq2.subquery(Long.class);
				Root<PolicyCoverData> SubsumInsured = sumInsured.from(PolicyCoverData.class);
				sumInsured.select(cb.sum(SubsumInsured.get("sumInsured"))).where(cb.equal(SubsumInsured.get("vehicleId"), tpdRoot2.get("travelId")),
						cb.equal(SubsumInsured.get("quoteNo"), hpmRoot2.get("quoteNo")));

				Subquery<String> currencyId2 = cq2.subquery(String.class);
				Root<InsuranceCompanyMaster> icmRoot2 = currencyId2.from(InsuranceCompanyMaster.class);
				currencyId2.select(icmRoot2.get("currencyId")).where(cb.equal(pcdRoot2.get("companyId"), icmRoot2.get("companyId")));

				cq2.multiselect(pgmRoot2.get("bandDesc").alias("bandDesc"),tpdRoot2.get("planTypeDesc").alias("planTypeDesc"),pcdRoot2.get("coverName").alias("coverName"),
						sumInsured.alias("sumInsured"),pcdRoot2.get("rate").alias("rate"),pcdRoot2.get("currency").alias("currency"),pcdRoot2.get("taxRate").alias("taxRate"),
						cb.selectCase().when(cb.in(pcdRoot2.get("currency")).value(currencyId2), pcdRoot2.get("premiumIncludedTaxLc")).otherwise(pcdRoot2.get("premiumIncludedTaxFc")).alias("premium"))
				.where(cb.equal(tpdRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")),cb.equal(pgmRoot2.get("groupId"),tpdRoot2.get("groupId")),
						cb.equal(hpmRoot2.get("productId").as(String.class), "4"),cb.equal(hpmRoot2.get("status"), "P"),cb.equal(hpmRoot2.get("quoteNo"), pcdRoot2.get("quoteNo")),cb.equal(pcdRoot2.get("vehicleId"), tpdRoot2.get("groupId")),
						cb.equal(pcdRoot2.get("discLoadId"), "0"),cb.equal(pcdRoot2.get("taxId"), "0"),cb.equal(hpmRoot2.get("policyNo"),policyNo)).distinct(true);

				List<Tuple> travelSubReport = em.createQuery(cq2).getResultList();
				travelSubReport.forEach(k -> {
					TravelDataSetTwoRes h = TravelDataSetTwoRes.builder()
							.bandDesc(k.get("bandDesc")==null?"":k.get("bandDesc").toString())
							.planTypeDesc(k.get("planTypeDesc")==null?"":k.get("planTypeDesc").toString())
							.coverName(k.get("coverName")==null?"":k.get("coverName").toString())
							.sumInsured(k.get("sumInsured")==null?"":k.get("sumInsured").toString())
							.taxRate(k.get("taxRate")==null?"":k.get("taxRate").toString())
							.rate(k.get("rate")==null?"":k.get("rate").toString())
							.currency(k.get("currency")==null?"":k.get("currency").toString())
							.premium(k.get("premium")==null?"":k.get("premium").toString())
							.build();
					travelDataSetTwo.add(h);
				});
				response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				response.setPolicyNo(map.get("policyNo")==null?"":map.get("policyNo").toString());
				response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
				response.setAddress(map.get("address")==null?"":map.get("address").toString());
				response.setTelephoneNo1(map.get("telephoneNo1")==null?"":map.get("telephoneNo1").toString());
				response.setAgencyCode(map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				response.setInceptionDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				response.setExpiryDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
				response.setOverAllPremium(map.get("overAllPremium")==null?"":map.get("overAllPremium").toString());
				response.setPremium(map.get("premium")==null?"":map.get("premium").toString());
				response.setNoOfPassanger(map.get("noOfPassanger")==null?"":map.get("noOfPassanger").toString());
				response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				response.setCompanylogo(map.get("companylogo")==null?"":map.get("companylogo").toString());
				response.setCompanyName(map.get("companyName")==null?"":map.get("companyName").toString());
				response.setPassangerDetails(travelDataSetOne);
				response.setTravelCoverDetails(travelDataSetTwo);
			}
		}catch(Exception e) {
			log.info("Error in getTravelReport ==>"+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getTravelReport");
		return response;
	}

	public Map<String, Object> getMotorEndorsementSchedule(String policyNo) {
		log.info("Enter into getMotorEndorsementSchedule.\nArgument ==> PolicyNo :"+policyNo);
		Map<String, Object> result = new HashMap<String,Object>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			//Refund or Not
			Subquery<String> payments = cq.subquery(String.class);
			Root<PaymentInfo> paymentsRoot = payments.from(PaymentInfo.class);

			Subquery<Integer> paymentAmd = cq.subquery(Integer.class);
			Root<PaymentInfo> paymentAmdRoot = paymentAmd.from(PaymentInfo.class);
			paymentAmd.select(cb.max(paymentAmdRoot.get("merchantReference"))).where(cb.equal(paymentAmdRoot.get("quoteNo"), paymentsRoot.get("quoteNo")),
					cb.equal(paymentAmdRoot.get("paymentStatus"), paymentsRoot.get("paymentStatus")));

			payments.select(paymentsRoot.get("payments")).where(cb.equal(paymentsRoot.get("quoteNo"), hpmRoot.get("quoteNo")),cb.equal(paymentsRoot.get("paymentStatus"), "ACCEPTED"),
					cb.equal(paymentsRoot.get("merchantReference"), paymentAmd.as(String.class)));

			cq.multiselect(cb.concat(piRoot.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
					.when(cb.equal(piRoot.get("titleDesc"),""), "")
					.otherwise(".").as(String.class), piRoot.get("clientName"))).alias("customerName"),
					hpmRoot.get("policyNo").alias("EndorsementNo"),hpmRoot.get("originalPolicyNo").alias("originalPolicyNo"),hpmRoot.get("effectiveDate").alias("effectiveDate"),
					hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("currency").alias("currency"),
					hpmRoot.get("endtPremium").alias("endtPremium"),hpmRoot.get("endtTypeDesc").alias("endtTypeDesc"),hpmRoot.get("endorsementRemarks").alias("endorsementRemarks"),
					hpmRoot.get("branchName").alias("branchName"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")),hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("quoteNo").alias("quoteNo"),companyName.alias("companyName"),imageURL.alias("companylogo"),payments.alias("payments"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),
					cb.equal(hpmRoot.get("productId"), "5"),cb.in(hpmRoot.get("status")).value(Arrays.asList("P","D","E")),cb.equal(hpmRoot.get("policyNo"), policyNo))
			.orderBy(cb.asc(hpmRoot.get("entryDate")));

			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				CriteriaBuilder cb1 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
				Root<MotorDataDetails> mddRoot = cq1.from(MotorDataDetails.class);

				cq1.multiselect(mddRoot.get("insuranceClassDesc").alias("insuranceClassDesc"),mddRoot.get("registrationNumber").alias("registrationNumber"),
						mddRoot.get("chassisNumber").alias("chassisNumber"),mddRoot.get("borrowerTypeDesc").alias("borrowerTypeDesc"),mddRoot.get("collateralName").alias("collateralName"),
						mddRoot.get("firstLossPayee").alias("firstLossPayee"),mddRoot.get("collateralYn").alias("collateralYn"),mddRoot.get("sumInsured").alias("sumInsured"))
				.where(cb.equal(mddRoot.get("quoteNo"), map.get("quoteNo")));

				List<Tuple> vehicleInfo = em.createQuery(cq1).getResultList();
				List<Map<String,Object>> vehicleList = vehicleInfo.stream().map(k ->{
					LinkedHashMap<String, Object> vMap = new LinkedHashMap<String,Object>();
					vMap.put("InsuranceClassDesc", k.get("insuranceClassDesc")==null?"":k.get("insuranceClassDesc").toString());
					vMap.put("RegistrationNumber", k.get("registrationNumber")==null?"":k.get("registrationNumber").toString());
					vMap.put("ChassisNumber", k.get("chassisNumber")==null?"":k.get("chassisNumber").toString());
					return vMap;
				}).collect(Collectors.toList());

				List<LinkedHashMap<String,Object>> collateralDetails = vehicleInfo.stream().filter(k -> "Y".equals(Objects.requireNonNullElse(k.get("collateralYn"), ""))).map(m ->{
					LinkedHashMap<String,Object> cdMap = new LinkedHashMap<String,Object>();
					cdMap.put("BorrowerType", m.get("borrowerTypeDesc")==null?"":m.get("borrowerTypeDesc").toString());
					cdMap.put("CollateralName", m.get("collateralName")==null?"":m.get("collateralName").toString());
					cdMap.put("CollateralYn", m.get("collateralYn")==null?"":m.get("collateralYn").toString());
					cdMap.put("FirstLossPayee", m.get("firstLossPayee")==null?"":m.get("firstLossPayee").toString());
					return cdMap;
				}).collect(Collectors.toList());

				List<PaymentDetail> paymentDetail = paymentDetailRepo.findByQuoteNo(map.get("quoteNo").toString());
				List<LinkedHashMap<String,Object>> refundPaymentDetail = paymentDetail.stream().filter(f -> "REFUND".equalsIgnoreCase(Objects.requireNonNullElse(f.getPayments(), ""))).map(k ->{
					LinkedHashMap<String,Object> pMap = new LinkedHashMap<String,Object>();
					pMap.put("BankName",k.getBankName());
					pMap.put("AccountNumber",k.getAccountNumber());
					pMap.put("IbanNumber",k.getIbanNumber());
					return pMap;
				}).collect(Collectors.toList());

				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("EndorsementNo", map.get("EndorsementNo")==null?"":map.get("EndorsementNo").toString());
				result.put("originalPolicyNo", map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
				result.put("effectiveDate", map.get("effectiveDate")==null?"":map.get("effectiveDate").toString());
				result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("endtPremium", map.get("endtPremium")==null?"":map.get("endtPremium").toString());
				result.put("endtTypeDesc", map.get("endtTypeDesc")==null?"":map.get("endtTypeDesc").toString());
				result.put("endorsementRemarks", map.get("endorsementRemarks")==null?"":map.get("endorsementRemarks").toString());
				result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
				result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
				result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
				result.put("companylogo", map.get("companylogo")==null?"":map.get("companylogo").toString());
				result.put("payments", map.get("payments")==null?"":map.get("payments").toString());
				result.put("vehicleList", vehicleList);
				result.put("refundPaymentDetail", refundPaymentDetail);
				result.put("collateralDetails", collateralDetails);
				result.put("vehicleSumInsured", vehicleInfo.get(0).get("sumInsured")==null?null:new BigDecimal(Double.parseDouble(vehicleInfo.get(0).get("sumInsured").toString())).toString());
			}
		}catch(Exception e) {
			log.info("Error in getMotorEndorsementSchedule ==>"+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getMotorEndorsementSchedule");
		return result;
	}

	public Map<String, Object> getCyberInsurance(String policyNo) {
		log.info("Enter into getCyberInsurance.\nArgument ==> PolicyNo :"+policyNo);
		Map<String, Object> result = new HashMap<String,Object>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<BranchMaster> bmRoot = cq.from(BranchMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<EserviceBuildingDetails> ebdRoot = cq.from(EserviceBuildingDetails.class);

			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubcountryAmd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubcountryAmd.get("amendId"))).where(cb.equal(SubcountryAmd.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubcountryAmd.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubcountryAmd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> cmRoot = countryName.from(CountryMaster.class);
			countryName.select(cmRoot.get("countryName")).where(cb.equal(cmRoot.get("countryId"), piRoot.get("nationality")),
					cb.equal(cmRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(cmRoot.get("status"), "Y"),cb.equal(cmRoot.get("amendId"), countryNameAmd));

			Subquery<String> currencyId = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> icmRoot = currencyId.from(InsuranceCompanyMaster.class);
			currencyId.select(icmRoot.get("currencyId")).where(cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")));

			Subquery<Integer> bmAmd = cq.subquery(Integer.class);
			Root<BranchMaster> SubbmAnd = bmAmd.from(BranchMaster.class);
			bmAmd.select(cb.max(SubbmAnd.get("amendId"))).where(cb.equal(SubbmAnd.get("branchCode"), hpmRoot.get("branchCode")),cb.equal(SubbmAnd.get("status"), "Y"));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),bmRoot.get("branchName").alias("branchName"),
					hpmRoot.get("entryDate").alias("entryDate"),cb.concat(piRoot.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
							.when(cb.equal(piRoot.get("titleDesc"),""), "")
							.otherwise(".").as(String.class),
							piRoot.get("clientName"))).alias("customerName"),
					cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""), cb.concat(cb.selectCase()
							.when(cb.equal(piRoot.get("pinCode"), ""), "").when(cb.isNull(piRoot.get("pinCode")), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"),
									cb.concat(",", cb.concat(piRoot.get("cityName"), cb.concat(",", countryName)))))))).alias("address"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
					hpmRoot.get("expiryDate").alias("expiryDate"),ebdRoot.get("occupationTypeDesc").alias("occupationTypeDesc"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("overallPremiumLc"))
					.otherwise(hpmRoot.get("overallPremiumFc")).alias("premium"))
			.where(cb.equal(hpmRoot.get("branchCode"), bmRoot.get("branchCode")),cb.equal(hpmRoot.get("companyId"), bmRoot.get("companyId")),
					cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(hpmRoot.get("requestReferenceNo"), ebdRoot.get("requestReferenceNo")),
					cb.equal(bmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), bmRoot.get("effectiveDateStart"), bmRoot.get("effectiveDateEnd")),
					cb.equal(bmRoot.get("amendId"), bmAmd),cb.not(cb.in(ebdRoot.get("sectionId")).value("0")),cb.equal(hpmRoot.get("policyNo"), policyNo));

			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				// SectionList
				CriteriaBuilder cb1 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq1 = cb1.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot1 = cq1.from(HomePositionMaster.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);

				Subquery<BigDecimal> excessAmount = cq1.subquery(BigDecimal.class);
				Root<PolicyCoverData> SubexcessAmd = excessAmount.from(PolicyCoverData.class);
				excessAmount.select(SubexcessAmd.get("excessAmount")).where(cb.equal(SubexcessAmd.get("quoteNo"), hpmRoot1.get("quoteNo")),
						cb.equal(SubexcessAmd.get("discLoadId"), "0"),cb.equal(SubexcessAmd.get("taxId"), "0"),cb.equal(SubexcessAmd.get("coverId"), "5"));

				cq1.multiselect(pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverName").alias("coverName"),pcdRoot.get("coverageLimit").alias("coverageLimit"),
						excessAmount.alias("excessAmount")).where(cb.equal(pcdRoot.get("quoteNo"), hpmRoot1.get("quoteNo")),cb.equal(hpmRoot1.get("policyNo"), map.get("policyNo")),
								cb.equal(pcdRoot.get("discLoadId"), "0"),cb.equal(pcdRoot.get("taxId"), "0"));

				List<Tuple> list1 = em.createQuery(cq1).getResultList();
				List<Map<String,Object>> sectionList = list1.stream().map(k ->{
					LinkedHashMap<String, Object> Smap = new LinkedHashMap<String,Object>();
					Smap.put("coverId", k.get("coverId")==null?"":k.get("coverId").toString());
					Smap.put("coverName", k.get("coverName")==null?"":k.get("coverName").toString());
					Smap.put("coverageLimit", k.get("coverageLimit")==null?"":k.get("coverageLimit").toString());
					Smap.put("excessAmount", k.get("excessAmount")==null?"":k.get("excessAmount").toString());
					return Smap;
				}).collect(Collectors.toList());

				// DeviceList
				List<ContentAndRisk> list2 = conAndRiskRepo.findByQuoteNo(map.get("quoteNo").toString());
				List<Map<String,Object>> deviceList = list2.stream().map(d -> {
					LinkedHashMap<String, Object> Dmap = new LinkedHashMap<String,Object>();
					Dmap.put("itemDesc", d.getItemDesc()==null?"":d.getItemDesc().toString());
					Dmap.put("makeAndModel", d.getMakeAndModel()==null?"":d.getMakeAndModel().toString());
					Dmap.put("manufactureYear", d.getManufactureYear()==null?"":d.getManufactureYear().toString());
					Dmap.put("serialNoDesc", d.getSerialNoDesc()==null?"":d.getSerialNoDesc().toString());
					return Dmap;
				}).collect(Collectors.toList());

				// CONDITIONS
				List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),"");

				// EXCLUSION
				List<Map<String,Object>> exclusionList = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),"");

				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
				result.put("entryDate", map.get("entryDate")==null?"":map.get("entryDate").toString());
				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("address", map.get("address")==null?"":map.get("address").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				result.put("occupationTypeDesc", map.get("occupationTypeDesc")==null?"":map.get("occupationTypeDesc").toString());
				result.put("premium", map.get("premium")==null?"":map.get("premium").toString());
				result.put("sectionList", sectionList);
				result.put("deviceList", deviceList);
				result.put("conditionList", conditionList);
				result.put("exclusionList", exclusionList);
			}
		}catch(Exception e) {
			log.info("Error in getCyberInsurance ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getCyberInsurance");
		return result;
	}

	public Map<String,Object> getMotorBrokerQuotation(String QuoteNo, String taxShowYn){
		log.info("Enter into getMotorBrokerQuotation.\nArgument ==> "+QuoteNo);
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);

			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCnAd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCnAd.get("amendId"))).where(cb.equal(SubCnAd.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCnAd.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCnAd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(icmAmdRoot.get("companyId"), icmRoot.get("companyId")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Agent","Premia Direct")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("agencyCode").alias("agencyCode"),hpmRoot.get("requestReferenceNo").alias("requestReferenceNo"),
					hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("originalPolicyNo").alias("originalPolicyNo"),hpmRoot.get("companyId").alias("companyId"),
					hpmRoot.get("companyName").alias("companyName"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("vatPercent").alias("vatPercent"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("overAllPremium"),
					hpmRoot.get("commissionPercentage").alias("commissionPercentage"),hpmRoot.get("commission").alias("commission"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
					hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("paymentType").alias("paymentType"),cb.concat(piRoot.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
							.when(cb.equal(piRoot.get("titleDesc"),""), "").otherwise(".").as(String.class),
							piRoot.get("clientName"))).alias("customerName"),
					cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""),cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "")
							.otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), cb.concat(",", cb.concat(piRoot.get("cityName"),cb.concat(",", countryName)))))))).alias("address"),
					piRoot.get("vrTinNo").alias("vrTinNo"),piRoot.get("email1").alias("email1"),piRoot.get("mobileNo1").alias("mobileNo1"),imageURL.alias("companyLogo"),luiRoot.get("brokerLogo").alias("brokerLogo"),hpmRoot.get("bdmName").alias("bdmName"),
					cb.selectCase().when(cb.equal(hpmRoot.get("applicationId"),"1"), hpmRoot.get("loginId")).otherwise(hpmRoot.get("applicationId")).alias("loginId"),
					piRoot.get("idNumber").alias("idNumber"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId"))/*,cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId"))*/,cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(luiRoot.get("loginId"), hpmRoot.get("loginId")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.in(hpmRoot.get("productId")).value(Arrays.asList(5,46)),cb.equal(hpmRoot.get("quoteNo"), QuoteNo))
			.orderBy(cb.desc(hpmRoot.get("entryDate")));
			

			List<Tuple> list = em.createQuery(cq).getResultList();
			List<LinkedHashMap<String, Object>> taxSummaryList = getTaxSummaryList(QuoteNo);
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<MotorDataDetails> list1 = motorRepo.findByQuoteNoOrderByVehicleIdAsc(map.get("quoteNo").toString());
				List<Map<String,Object>> vehicleList = list1.stream().map(k -> {
					LinkedHashMap<String,Object> m = new LinkedHashMap<String,Object>();
					m.put("policyTypeDesc", k.getPolicyTypeDesc()==null?"":StringUtils.capitalize(k.getPolicyTypeDesc()));
					m.put("registrationNumber", k.getRegistrationNumber()==null?"":k.getRegistrationNumber());
					m.put("vehicleMakeDesc", k.getVehicleMake()==null?"":StringUtils.capitalize(k.getVehicleMake()));
					m.put("vehicleTypeDesc", k.getVehicleTypeDesc()==null?"":StringUtils.capitalize(k.getVehicleTypeDesc()));
					m.put("chassisNumber", k.getChassisNumber()==null?"":k.getChassisNumber());
					m.put("colorDesc", k.getColorDesc()==null?"":StringUtils.capitalize(k.getColorDesc()));
					m.put("manufactureYear", k.getManufactureYear());
					m.put("engineNumber", k.getEngineNumber()==null?"":k.getEngineNumber());
					m.put("vehcileModelDesc", k.getVehcileModelDesc()==null?"":StringUtils.capitalize(k.getVehcileModelDesc()));
					m.put("sumInsured", k.getSumInsured());
					m.put("insuranceTypeDesc", k.getInsuranceTypeDesc()==null?"":k.getInsuranceTypeDesc());
					return m;
				}).collect(Collectors.toList());

				result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
				result.put("agencyCode", map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				result.put("requestReferenceNo", map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("originalPolicyNo", map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
				result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("vatPercent", map.get("vatPercent")==null?null:map.get("vatPercent").toString());
				result.put("premium", map.get("premium")==null?"":map.get("premium").toString());
				result.put("vatPremium", map.get("vatPremium")==null?null:map.get("vatPremium").toString());
				result.put("overAllPremium", map.get("overAllPremium")==null?"":map.get("overAllPremium").toString());
				result.put("commissionPercentage", map.get("commissionPercentage")==null?null:map.get("commissionPercentage").toString());
				result.put("commission", map.get("commission")==null?null:map.get("commission").toString());
				result.put("branchName", map.get("branchName")==null?null:map.get("branchName").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?null:map.get("inceptionDate").toString());
				result.put("address", map.get("address")==null?"":map.get("address").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("mobileNo1", map.get("mobileNo1")==null?null:map.get("mobileNo1").toString());
				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("vrTinNo", map.get("vrTinNo")==null?null:map.get("vrTinNo").toString());
				result.put("expiryDate", map.get("expiryDate")==null?null:map.get("expiryDate").toString());
				result.put("companyLogo", map.get("companyLogo")==null?"":map.get("companyLogo").toString());
				result.put("brokerLogo", map.get("brokerLogo")==null?"":map.get("brokerLogo").toString());
				result.put("bdmName", map.get("bdmName")==null?"":map.get("bdmName").toString());
				result.put("loginId", map.get("loginId")==null?"":map.get("loginId").toString());
				result.put("idNumber", map.get("idNumber")==null?"":map.get("idNumber").toString());
				result.put("insuranceTypeDesc", vehicleList.get(0).get("insuranceTypeDesc")==null?"":vehicleList.get(0).get("insuranceTypeDesc").toString());
				result.put("vehicleList", vehicleList);
				result.put("taxShowYn", taxShowYn);
				result.put("premiumSummary", taxSummaryList);
			}

		}catch(Exception e) {
			log.info("Error in getMotorBrokerQuotation ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getMotorBrokerQuotation");
		return result;
	}
	private static final Map<Integer, Integer> TAX_DISPLAY_ORDER = new HashMap<>();
	static {
	    TAX_DISPLAY_ORDER.put(10, 1);  // TRAINING LEVY
	    TAX_DISPLAY_ORDER.put(11, 2);  // STICKER FEE
	    TAX_DISPLAY_ORDER.put(9,  3);  // VAT
	    TAX_DISPLAY_ORDER.put(12, 4);  // STAMP DUTY
	    
	}

	public List<LinkedHashMap<String, Object>> getTaxSummaryList(String quoteNo) {
	    try {
	        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.00");
	        
	        List<PolicyCoverData> policies = coverDataRepository.findByQuoteNo(quoteNo);
	        
	        // Premium LC
	        Double premiumLc = policies.stream()
	            .filter(o ->
	                o.getDiscLoadId().equals(0)
	                    && o.getTaxId().equals(0)
	                    && o.getPremiumExcludedTaxLc() != null
	                    && o.getPremiumExcludedTaxLc().doubleValue() > 0D
	                    && !o.getStatus().equalsIgnoreCase("D")
	            )
	            .mapToDouble(o -> o.getPremiumExcludedTaxLc().doubleValue())
	            .sum();

	        // Group tax rows
	        Map<Integer, List<PolicyCoverData>> groupedByTaxId = policies.stream()
	            .filter(p -> p.getCoverageType() != null && "T".equalsIgnoreCase(p.getCoverageType().trim()))
	            .filter(p -> p.getTaxAmount() != null && p.getTaxId() != null)
	            .filter(p -> p.getTaxId() != 0)
	            .sorted(Comparator.comparingInt(p ->
	                TAX_DISPLAY_ORDER.getOrDefault(p.getTaxId(), Integer.MAX_VALUE)
	            ))
	            .collect(Collectors.groupingBy(PolicyCoverData::getTaxId, LinkedHashMap::new, Collectors.toList()));

	        List<LinkedHashMap<String, Object>> result = new ArrayList<>();

	        // 1. First row — Premium
			
			String premiumLcFormat = formatter.format(premiumLc);
	        LinkedHashMap<String, Object> premiumRow = new LinkedHashMap<>();
	        premiumRow.put("narration", "PREMIUM");
	        premiumRow.put("amount", premiumLcFormat);
	        result.add(premiumRow);

	        // 2. Middle rows — Tax entries (in custom order)
	        BigDecimal totalTaxAmount = BigDecimal.ZERO;

	        for (Map.Entry<Integer, List<PolicyCoverData>> entry : groupedByTaxId.entrySet()) {
	            List<PolicyCoverData> group = entry.getValue();
	            PolicyCoverData first = group.get(0);

	            BigDecimal total = group.stream()
	                .map(PolicyCoverData::getTaxAmount)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	            totalTaxAmount = totalTaxAmount.add(total);

	            boolean isAbsolute = "A".equalsIgnoreCase(
	                first.getTaxCalcType() != null ? first.getTaxCalcType().trim() : ""
	            );

	            String key;
	            if (isAbsolute) {
	                key = first.getTaxDesc();
	            } else {
	                String rateStr = (first.getTaxRate() != null
	                    && first.getTaxRate().compareTo(BigDecimal.ZERO) > 0)
	                    ? " " + first.getTaxRate().stripTrailingZeros().toPlainString() + "%"
	                    : "";
	                key = first.getTaxDesc() + rateStr;
	            }

	            LinkedHashMap<String, Object> item = new LinkedHashMap<>();
	            item.put("narration", key);
	            item.put("amount", total);
	            result.add(item);
	        }

	        // 3. Last row — Total Premium (premiumLc + all taxes)
	      //  BigDecimal premiumTotal = BigDecimal.valueOf(premiumLc).add(totalTaxAmount);
	        Double premiumTotal = policies.stream()
				    .filter(o ->
				        o.getDiscLoadId().equals(0)
				            && o.getTaxId().equals(0)
				            && o.getPremiumIncludedTaxLc() != null
				            && o.getPremiumIncludedTaxLc().doubleValue() > 0D
				            && !o.getStatus().equalsIgnoreCase("D"))
				    .mapToDouble(o -> o.getPremiumIncludedTaxLc().doubleValue())
				    .sum();	
			String premiumTotalFormat = formatter.format(premiumTotal);
			LinkedHashMap<String, Object> totalRow = new LinkedHashMap<>();
	        totalRow.put("narration", "TOTAL PREMIUM");
	        totalRow.put("amount", premiumTotalFormat);
	        result.add(totalRow);

	        return result;

	    } catch (Exception e) {
	        log.error("Error in getTaxSummaryList ==> {}", e.getMessage(), e);
	    }
	    return Collections.emptyList();
	}
	
	public List<LinkedHashMap<String, Object>> getTaxSummaryListSection(String quoteNo , Integer sectionId , Integer locationId) {
	    try {
	        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,##0.00");
	        
	        List<PolicyCoverData> policies = coverDataRepository.findByQuoteNoAndSectionIdAndLocationId(quoteNo,sectionId,locationId);
	        
	        // Premium LC
	        Double premiumLc = policies.stream()
	            .filter(o ->
	                o.getDiscLoadId().equals(0)
	                    && o.getTaxId().equals(0)
	                    && o.getPremiumExcludedTaxFc() != null
	                    && o.getPremiumExcludedTaxFc().doubleValue() > 0D
	                    && !o.getStatus().equalsIgnoreCase("D")
	                    && !o.getSectionId().equals(99999)
	            )
	            .mapToDouble(o -> o.getPremiumExcludedTaxFc().doubleValue())
	            .sum();

	        // Group tax rows
	        Map<Integer, List<PolicyCoverData>> groupedByTaxId = policies.stream()
	            .filter(p -> p.getCoverageType() != null && "T".equalsIgnoreCase(p.getCoverageType().trim()))
	            .filter(p -> p.getTaxAmount() != null && p.getTaxId() != null)
	            .filter(p -> p.getTaxId() != 0)
	            .filter(p -> !p.getSectionId().equals(99999))
	            .sorted(Comparator.comparingInt(p ->
	                TAX_DISPLAY_ORDER.getOrDefault(p.getTaxId(), Integer.MAX_VALUE)
	            ))
	            .collect(Collectors.groupingBy(PolicyCoverData::getTaxId, LinkedHashMap::new, Collectors.toList()));

	        List<LinkedHashMap<String, Object>> result = new ArrayList<>();

	        // 1. First row — Premium
			
			String premiumLcFormat = formatter.format(premiumLc);
	        LinkedHashMap<String, Object> premiumRow = new LinkedHashMap<>();
	        premiumRow.put("narration", "Annually");
	        premiumRow.put("amount", premiumLcFormat);
	        result.add(premiumRow);
	        
	        String monthly = formatter.format(premiumLc/12);
	        LinkedHashMap<String, Object> premiumRowMon = new LinkedHashMap<>();
	        premiumRowMon.put("narration", "Monthly");
	        premiumRowMon.put("amount", monthly);
	        result.add(premiumRowMon);

	        // 2. Middle rows — Tax entries (in custom order)
	        BigDecimal totalTaxAmount = BigDecimal.ZERO;

	        for (Map.Entry<Integer, List<PolicyCoverData>> entry : groupedByTaxId.entrySet()) {
	            List<PolicyCoverData> group = entry.getValue();
	            PolicyCoverData first = group.get(0);

	            BigDecimal total = group.stream()
	                .map(PolicyCoverData::getTaxAmount)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	            totalTaxAmount = totalTaxAmount.add(total);

	            boolean isAbsolute = "A".equalsIgnoreCase(
	                first.getTaxCalcType() != null ? first.getTaxCalcType().trim() : ""
	            );

	            String key;
	            if (isAbsolute) {
	                key = first.getTaxDesc();
	            } else {
	                String rateStr = (first.getTaxRate() != null
	                    && first.getTaxRate().compareTo(BigDecimal.ZERO) > 0)
	                    ? " " + first.getTaxRate().stripTrailingZeros().toPlainString() + "%"
	                    : "";
	                key = first.getTaxDesc() + rateStr;
	            }

	            LinkedHashMap<String, Object> item = new LinkedHashMap<>();
	            item.put("narration", key);
	            item.put("amount", total);
	            result.add(item);
	        }

	        // 3. Last row — Total Premium (premiumLc + all taxes)
	      //  BigDecimal premiumTotal = BigDecimal.valueOf(premiumLc).add(totalTaxAmount);
	        Double premiumTotal = policies.stream()
				    .filter(o ->
				        o.getDiscLoadId().equals(0)
				            && o.getTaxId().equals(0)
				            && o.getPremiumIncludedTaxFc() != null
				            && o.getPremiumIncludedTaxFc().doubleValue() > 0D
				            && !o.getStatus().equalsIgnoreCase("D"))
				    .mapToDouble(o -> o.getPremiumIncludedTaxFc().doubleValue())
				    .sum();	
			String premiumTotalFormat = formatter.format(premiumTotal);
			LinkedHashMap<String, Object> totalRow = new LinkedHashMap<>();
	        totalRow.put("narration", "TOTAL PREMIUM");
	        totalRow.put("amount", premiumTotalFormat);
	        result.add(totalRow);

	        return result;

	    } catch (Exception e) {
	        log.error("Error in getTaxSummaryList ==> {}", e.getMessage(), e);
	    }
	    return Collections.emptyList();
	}
	
	public Map<String,Object> getEwayScheduleNam(String QuoteNo){
		log.info("Enter into EwaySchedule.\nArgument ==> "+QuoteNo);
		Map<String,Object> result = new HashMap<String,Object>();
		List<AttachMentRes> attachments = new ArrayList<>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);

			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginBranchMaster> lbmRoot= cq.from(LoginBranchMaster.class);

			Subquery<Integer> cmAmd = cq.subquery(Integer.class);
			Root<CountryMaster> cmAmdRoot = cmAmd.from(CountryMaster.class);
			cmAmd.select(cb.max(cmAmdRoot.get("amendId"))).where(cb.equal(cmAmdRoot.get("countryId"), piRoot.get("nationality")),cb.equal(cmAmdRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(cmAmdRoot.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> ScmRoot = countryName.from(CountryMaster.class);
			countryName.select(ScmRoot.get("countryName")).where(cb.equal(ScmRoot.get("countryId"), piRoot.get("nationality")),cb.equal(ScmRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(ScmRoot.get("status"), "Y"),cb.equal(ScmRoot.get("amendId"), cmAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(hpmRoot.get("companyId"), icmAmdRoot.get("companyId")),cb.equal(icmAmdRoot.get("status"), "Y"),
					cb.between(cb.literal(new Date()), icmAmdRoot.get("effectiveDateStart"), icmAmdRoot.get("effectiveDateEnd")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("requestReferenceNo").alias("requestReferenceNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
					.when(cb.equal(piRoot.get("titleDesc"),""), "").otherwise(".").as(String.class),piRoot.get("clientName"))).alias("customerName"),
					piRoot.get("address1").alias("address1"),piRoot.get("pinCode").alias("pinCode"),countryName.alias("countryName"),hpmRoot.get("entryDate").alias("entryDate"),
					piRoot.get("email1").alias("email1"),piRoot.get("mobileNo1").alias("mobileNo1"),hpmRoot.get("branchCode").alias("branchCode"),luiRoot.get("agencyCode").alias("agencyCode"),piRoot.get("idNumber").alias("identificationNo"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("brokerBranchName").alias("brokerBranchName"),
					hpmRoot.get("productName").alias("productName"),piRoot.get("stateName").alias("stateName"),piRoot.get("cityName").alias("cityName"),cb.concat(piRoot.get("mobileCodeDesc1"), cb.concat("-", piRoot.get("mobileNo1"))).alias("mobileNo"),
					piRoot.get("customerId").alias("customerId"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Agent","Premia Direct","Premia Broker")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("brokerName"),luiRoot.get("coreAppBrokerCode").alias("coreAppBrokerCode"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("vatPercent").alias("vatPercent"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),
					icmRoot.get("signature").alias("signature"),lbmRoot.get("branchName").alias("place"),companyName.alias("companyName"),imageURL.alias("companylogo"),hpmRoot.get("companyId").alias("companyId"),hpmRoot.get("productId").alias("productId"),
					hpmRoot.get("debitNoteNo").alias("debitNoteNo"),luiRoot.get("userMobile").alias("userMobile"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
					.when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("b2c","Direct")), "System").otherwise(luiRoot.get("userName")).alias("approvedBy"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("bdmName").alias("bdmName"),hpmRoot.get("bdmCode").alias("bdmCode"),
					cb.selectCase().when(cb.equal(hpmRoot.get("applicationId"),"1"), hpmRoot.get("loginId")).otherwise(hpmRoot.get("applicationId")).alias("loginId"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(hpmRoot.get("agencyCode").as(String.class), luiRoot.get("agencyCode")),cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(hpmRoot.get("loginId"), lbmRoot.get("loginId")),cb.equal(hpmRoot.get("companyId"), lbmRoot.get("companyId")),cb.equal(hpmRoot.get("branchCode"), lbmRoot.get("branchCode")),cb.equal(lbmRoot.get("status"), "Y"),
					cb.equal(icmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), icmRoot.get("effectiveDateStart"), icmRoot.get("effectiveDateEnd")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("quoteNo"), QuoteNo));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<PolicyCoverData> coverData = coverDataRepository.findByQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());

				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
				//List<EserviceCommonDetails> eserviceCommonList = eserviceCommonDetRepo.findByQuoteNo(map.get("quoteNo").toString());

				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),map.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("locationId"), sddRoot.get("locationId")));
				predicate.add(cb.equal(pcdRoot.get("vehicleId"), sddRoot.get("riskId")));
				predicate.add(cb.equal(pcdRoot.get("coverId"), sddRoot.get("coverId")));
				/*if(!eserviceCommonList.isEmpty()) {
					Root<EserviceCommonDetails> ecdRoot = cq1.from(EserviceCommonDetails.class);
					eserviceQuote = ecdRoot.get("occupationDesc").alias("occupationDesc");
					predicate.add(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")));
					predicate.add(cb.equal(pcdRoot.get("sectionId"), ecdRoot.get("sectionId")));
					predicate.add(cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")));
					predicate.add(cb.equal(pcdRoot.get("productId"), ecdRoot.get("productId")));
					predicate.add(cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));
				}*/
				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);

				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")),cb.equal(pcdRoot.get("locationId"), ecdRoot.get("locationId")),
						cb.equal(pcdRoot.get("coverId"), ecdRoot.get("coverId")));

				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),sddRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),
						pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
						pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"),
						sddRoot.get("locationId").alias("locationId"),sddRoot.get("locationName").alias("locationName"),
						sddRoot.get("productType").alias("productType"),pcdRoot.get("coverageLimit").alias("coverageLimit"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));

				List<Tuple> Slist = em.createQuery(cq1).getResultList();
				List<Map<String,Object>>sectList=new ArrayList<>();
				Double minAdjPrem=0.0,minAdjPremFc=0.0,basePremium=0.0,basePremiumFc=0.0,
						minAdjExPrem=0.0,minAdjExPremFc=0.0,baseExPremium=0.0,baseExPremiumFc=0.0;

				for (int i=0;i<Slist.size();i++) {
					Tuple t=Slist.get(i);
					String coverId = t.get("coverId")==null?"":t.get("coverId").toString();
					String coverageType = t.get("coverageType")==null?"":t.get("coverageType").toString();
					if("945".equals(coverId)){
						minAdjPrem=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						minAdjPremFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						minAdjExPrem=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						minAdjExPremFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if("B".equals(coverageType)) {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
						basePremium=basePremium+minAdjPrem;
						basePremiumFc=basePremiumFc+minAdjPremFc;
						baseExPremium=baseExPremium+minAdjExPrem;
						baseExPremiumFc=baseExPremiumFc+minAdjExPremFc;
					}else {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if(!"945".equals(coverId)){
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", t.get("sectionDesc"));
						Smap.put("occupationDesc", t.get("occupationDesc"));
						Smap.put("coverDesc", t.get("coverDesc"));
						Smap.put("sumInsured", t.get("sumInsured"));
						Smap.put("rate", t.get("rate"));
						Smap.put("premiumIncludedTaxLc", basePremium);
						Smap.put("premiumIncludedTaxFc", basePremiumFc);
						Smap.put("premiumExcludedTaxLc", baseExPremium);
						Smap.put("premiumExcludedTaxFc", baseExPremiumFc);
						sectList.add(Smap);
					}
				}
				List<Map<String,Object>> sectionList = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> coverageDetails = new ArrayList<Map<String,Object>>();
				List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
				Double OverAllPremium=0.0;
				String companyId = map.get("companyId")==null?"":map.get("companyId").toString();
				List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
				List<Object> coverIds = Slist.stream().map(k -> k.get("coverId")).distinct().collect(Collectors.toList());
				if("100002".equalsIgnoreCase(companyId) || "100020".equalsIgnoreCase(companyId)) {
					if(coverData!=null && !coverData.isEmpty()) {
						Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
								.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
								.findAny().orElse(0.0);

						Double taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
								.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));


						List<Map<String,Object>> sectionPremium = new ArrayList<Map<String,Object>>();
						for(int x=0;x<sectionIds.size();x++) {
							int s = Integer.parseInt(sectionIds.get(x).toString());
							System.out.println(new Gson().toJson(coverData.stream().filter(f -> f.getSectionId()==s)
									.collect(Collectors.toList())));
							List<Integer> coverids = coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s)
									.map(m -> m.getCoverageType().equalsIgnoreCase("L")?m.getDiscLoadId():m.getCoverId()).distinct()
									.collect(Collectors.toList());
							for(int j=0;j<coverids.size();j++) {
								int c = coverids.get(j);
								Map<String,Object> o = new HashMap<String,Object>();
								o.put("SectionId", s);
								o.put("CoverDesc", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
										&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
										.map(m -> m.getCoverName()).findFirst().orElse("-N-A"));
								o.put("TotPremium", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
										&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
										.map(m -> m.getPremiumExcludedTaxFc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
								sectionPremium.add(o);
							}
						}
						String occupationValue = Slist.stream()
								.filter(t -> t.get("occupationDesc") != null && 
								!t.get("occupationDesc").toString().equals("null") &&
								!t.get("occupationDesc").toString().isEmpty())
								.map(t -> t.get("occupationDesc").toString())
								.findFirst()
								.orElse("Not Specified");

						// Add occupation to result
						result.put("occupation", occupationValue);


						/*						List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && (f.getDiscLoadId()==0 || f.getCoverageType().equalsIgnoreCase("L")) && f.getSectionId()!=99999
								&& (f.getCoverageType().equals("O") && Arrays.asList("Y","D").contains(f.getIsSelected().equalsIgnoreCase("Y")?"Y":f.getIsSelected().equalsIgnoreCase("D")?"D":"N") 
								|| !f.getCoverageType().equalsIgnoreCase("O")))
								.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverId(),Collectors.reducing(
									BigDecimal.ZERO, PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
								.entrySet().stream()
								.flatMap((Map.Entry<Integer,Map<Integer,BigDecimal>> s ) -> {
									Integer sectionId = s.getKey();
									return s.getValue().entrySet().stream()
											.map((Map.Entry<Integer,BigDecimal> g )-> {
												Integer coverId = g.getKey();
												BigDecimal totPremium = g.getValue();
												Map<String,Object> secMap = new HashMap<String,Object>();
												secMap.put("SectionId", sectionId);
												secMap.put("CoverDesc", coverData.stream().filter(f -> f.getTaxId()==0
														&& (f.getDiscLoadId()==0 || f.getCoverageType().equalsIgnoreCase("L")) && f.getSectionId()==sectionId && f.getCoverId()==coverId)
														.map(m -> m.getCoverName()).findFirst().orElse("-N-A"));
												secMap.put("TotPremium", totPremium);
												return secMap;
											});
								}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());*/

						sectionPremium = sectionPremium.stream().sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());
						if("100020".equalsIgnoreCase(companyId)) {
							sectionPremium = sectionPremium.stream().filter(f -> new BigDecimal(f.get("TotPremium").toString()).compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
							List<PolicyCoverData> spData = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
									.collect(Collectors.toList());
							if(spData !=null && spData.size()>0) {
								List<Map<String,Object>> subspData = new ArrayList<Map<String,Object>>();
								spData.forEach(sp -> {
									Map<String,Object> spMap = new HashMap<String,Object>();
									spMap.put("SectionId", sp.getSectionId());
									spMap.put("CoverDesc", sp.getCoverName()+" ("+sp.getTaxRate()+"% )");
									spMap.put("TotPremium", sp.getTaxAmount());
									subspData.add(spMap);
								});
								sectionPremium.addAll(subspData);
							}
						}
						sectionPremium.forEach(k -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
									.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
									.build();
							premiumDetailsRes.add(u);
						});


						OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue))+taxAmount;
						result.put("vatPercent", taxRate.toString());
						result.put("vatAmount", taxAmount.toString());
					}
				}else if("100004".equalsIgnoreCase(companyId)){
					sectList.forEach(k -> {
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", k.get("sectionDesc"));
						Smap.put("coverDesc", k.get("coverDesc"));
						Smap.put("sumInsured", k.get("sumInsured"));
						Smap.put("rate", k.get("rate"));
						Smap.put("premiumIncludedTaxLc", k.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", k.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", k.get("premiumExcludedTaxLc"));
						Smap.put("premiumExcludedTaxFc", k.get("premiumExcludedTaxFc"));
						Smap.put("vehicleId",k.get("vehicleId"));
						sectionList.add(Smap);
						result.put("occupationDesc", sectList.stream().filter(f -> f.get("occupationDesc") != null && !f.get("occupationDesc").toString().equals("null")).map(m -> m.get("occupationDesc"))
								.map(Object::toString).findAny().orElse(null));
					});
				}else {
					Map<Object, List<Map<String,Object>>> sectionRes = sectList.stream().collect(Collectors.groupingBy(g -> g.get("sectionDesc"),Collectors.mapping(v ->{
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("occupationDesc", v.get("occupationDesc"));
						Smap.put("coverDesc", v.get("coverDesc"));
						Smap.put("sumInsured", v.get("sumInsured"));
						Smap.put("rate", v.get("rate"));
						Smap.put("premiumIncludedTaxLc", v.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", v.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", v.get("premiumExcludedTaxLc"));
						Smap.put("premiumExclhitudedTaxFc", v.get("premiumExcludedTaxFc"));
						return Smap;
					}, Collectors.toList())));			
					for(Map.Entry<Object, List<Map<String,Object>>> entry :sectionRes.entrySet()) {
						Map<String, Object> sectionMap = new HashMap<String, Object>();
						sectionMap.put("sectionKey", entry.getKey());
						sectionMap.put("sectionValue", entry.getValue());
						sectionList.add(sectionMap);
					}
				}

				List<Object> locationIds = Slist.stream().map(k -> k.get("locationId")).distinct().collect(Collectors.toList());
				List<Map<String,Object>> coverageList = new ArrayList<Map<String,Object>>();
				for(int i=0;i<coverIds.size();i++) {
					Map<String,Object> coverMap = new HashMap<String,Object>();
					List<Map<String,Object>> buildingDetails = new ArrayList<Map<String,Object>>();

					List<Map<String,Object>> allriskDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> contentDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> electronicDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> ownersDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> excessConDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> personalAccDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> bondDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> exclusionDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> excessDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> clauseDetails = new ArrayList<Map<String,Object>>();
					String coverId = coverIds.get(i).toString() ;
					String sectionId = Slist.stream().filter(f -> f.get("coverId").toString().equalsIgnoreCase(coverId))
							.map(z -> z.get("sectionId").toString()).findFirst().orElse("");
					log.info("Current Lopping SectionId :: "+ sectionId);
					for(int x=0;x<locationIds.size();x++) {
						String locationId = locationIds.get(x).toString();

						String locationName = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))).map(r -> r.get("locationName").toString()).findFirst().get();
						/*String productType = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))
								&& f.get("sectionId").equals(sectionId)).map(t -> t.get("productType")).map(Object::toString).findFirst().orElse("");*/
						List<BuildingRiskDetails> buildingRiskData = buildingRiskDetailsRepo
								.findByRequestReferenceNoAndSectionIdAndLocationIdAndCoverId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId),Integer.parseInt(coverId));
						List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
							LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
							lmap.put("locationName", k.getLocationName());
							lmap.put("buildingAddress", k.getAddress());
							lmap.put("sectionId", sectionId);
							lmap.put("sectiondesc", k.getSectionDesc());
							lmap.put("wallTypeDesc", k.getWallTypeDesc()==null?"":k.getWallTypeDesc());
							lmap.put("roofType", k.getRoofTypeDesc());
							lmap.put("firstlosspayee", k.getFirstLossPercent());
							lmap.put("coveringdetails", k.getCoveringDetails());
							lmap.put("descriptionofrisk", k.getDescriptionOfRisk());
							lmap.put("buildingSumInsured", k.getSumInsured());
							lmap.put("industrydesc", k.getIndustryDesc());
							lmap.put("buildingAge", k.getBuildingAge()==null?"":k.getBuildingAge() );
							lmap.put("wallType", k.getWallType());
							lmap.put("coverId", k.getCoverId()==null?"":k.getCoverId() );


							lmap.put("bondyear", k.getBondYear());
							lmap.put("sumInsured", k.getSumInsured());
							lmap.put("buildingSumInsured", k.getSumInsured());
							lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
							lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
									&& f.getSectionId()==Integer.parseInt(sectionId))
									.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
							//							lmap.put("moneyAnnualEstimate", k.getMoneyAnnualEstimate());
							//							lmap.put("moneyCollector", k.getMoneyCollector());
							//							lmap.put("moneyDirectorResidence", k.getMoneyDirectorResidence());
							//							lmap.put("moneyOutofSafe", k.getMoneyOutofSafe());
							//							lmap.put("moneySafeLimit", k.getMoneySafeLimit());
							//							lmap.put("moneyMajorLoss", k.getMoneyMajorLoss());
							lmap.put("indemityPeriodDesc", k.getIndemityPeriodDesc());
							lmap.put("categoryDesc", k.getCategoryDesc());
							lmap.put("contentDesc", k.getContentDesc());
							lmap.put("firstLossPercent", k.getFirstLossPercent());
							lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("coverId").equals(coverId) && f.get("coverNoteReferenceNo")!=null)
									.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
							return lmap;
						}).collect(Collectors.toList());



						if("105".equalsIgnoreCase(coverId)){ // Building
							buildingDetails.addAll(locationList);
						}else if("45".equalsIgnoreCase(coverId)){ // All Risk
							allriskDetails.addAll(locationList);
						}else if("290".equalsIgnoreCase(coverId)){ //Content
							contentDetails.addAll(locationList);
						}else if("90".equalsIgnoreCase(coverId)){ // Electronic Equipment
							electronicDetails.addAll(locationList);
						}else if("593".equalsIgnoreCase(coverId)) { //Owners liability
							ownersDetails.addAll(locationList);
						}

						//							List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationIdAndCoverId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId),Integer.parseInt(coverId));
						//							List<Map<String,Object>> commonList = comDetails.stream()
						//									.collect(Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(o -> {
						//										LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
						//										empMap.put("locationName", locationName);
						//										empMap.put("occupationDesc", o.getOccupationDesc());
						//										empMap.put("sumInsured", o.getSumInsured());
						//										empMap.put("count", o.getCount()==null?0:o.getCount());
						//										empMap.put("Rate", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
						//												&& f.getTaxId()!=0 && f.getSectionId()==Integer.parseInt(o.getSectionId())
						//												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getRate()).findAny().orElse(BigDecimal.ZERO));
						//										empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
						//												&& f.getSectionId()==Integer.parseInt(sectionId)
						//												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
						//										return empMap;
						//									}, Collectors.toList()))).entrySet()
						//									.stream().map(g -> {
						//										LinkedHashMap<String,Object> eMap = new LinkedHashMap<String,Object>();
						//										eMap.put("occupationDesc", g.getValue().stream().map(t -> String.valueOf(t.get("occupationDesc"))).collect(Collectors.joining("<br>")));
						//										eMap.put("Rate", g.getValue().stream().map(t -> t.get("Rate")).findFirst().get());
						//										eMap.put("count", g.getValue().stream().map(t -> t.get("count")).findFirst().get());
						//										eMap.put("sumInsured", g.getValue().stream().map(j -> (BigDecimal) j.get("sumInsured")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
						//										eMap.put("premium", g.getValue().stream().map(h -> h.get("Premium")).findFirst().get());
						//										eMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
						//												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
						//										eMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
						////										eMap.put("wallTypeDesc", map.get("wallTypeDesc")==null?"":map.get("wallTypeDesc").toString());
						////										eMap.put("buildingAge", map.get("buildingAge")==null?"":map.get("buildingAge").toString()  );
						////										eMap.put("wallType", map.get("wallType")==null?"":map.get("wallType").toString() );
						////										eMap.put("coverId", map.get("coverId")==null?"":map.get("coverId").toString()  );
						//										
						//										eMap.put("locationName", g.getValue().stream().map(j -> j.get("locationName")).findFirst().get());
						//										return eMap;
						//									}).collect(Collectors.toList());
						//							ownersDetails.addAll(commonList);
						else if("13".equalsIgnoreCase(map.get("productId")==null?"":map.get("productId").toString())){
							List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
							if(comDetails !=null && comDetails.size()>0) {
								comDetails.forEach(us -> {
									Map<String,Object> empMap = new HashMap<String,Object>();
									empMap.put("locationName", locationName);
									empMap.put("Quoteoccupation", us.getCategoryDesc());
									empMap.put("sumInsured", us.getSumInsured());
									empMap.put("customerName", us.getNickName());
									empMap.put("dateOfBrith", sdf.format(us.getDob()));
									empMap.put("options", us.getSectionDesc());
									empMap.put("estAnnualEarnings", us.getOtherOccupation());
									empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId)
											&& f.getVehicleId()==us.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									empMap.put("coverLimit", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId) && f.getCoverageType().equalsIgnoreCase("B")
											&& f.getVehicleId()==us.getRiskId()).map(u -> u.getCoverageLimit()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									empMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									personalAccDetails.add(empMap);
								});	


							}
						}

						else if ("246".equalsIgnoreCase(sectionId)) { // Member Section

							Map<String, Object> finalResponse = new LinkedHashMap<>();

							List<ListItemValue> memberList = ewayListItemValueRepo
									.findByItemTypeAndItemCodeOrderByItemCodeAsc("Members", sectionId);

							List<Map<String, Object>> memberDetails = new ArrayList<>();

							String categoryDes = ""; 
							if (memberList != null && !memberList.isEmpty()) {

								Optional<InsuranceCompanyMaster> companyOpt = ewayInsuranceCompanyMasterRepo
										.findTopByCompanyIdOrderByAmendIdAsc(companyId);  

								String companyWebsite = companyOpt.isPresent() && companyOpt.get().getCompanyWebsite() != null
										? companyOpt.get().getCompanyWebsite()
												: "";

								memberDetails = memberList.stream().map(val -> {
									Map<String, Object> m = new LinkedHashMap<>();


									//							        BuildingRiskDetails matchedBuilding = buildingRiskDetailsRepo.findByQuoteNo(QuoteNo);
									//							        	    
									//							        if (matchedBuilding.isPresent()) {
									//							            BuildingRiskDetails brDetails = matchedBuilding.get();
									////							            m.put("MembersValue", brDetails.getMoneyCollector() != null ? brDetails.getMoneyCollector() : "");
									////							            m.put("occupation", brDetails.getCategoryDesc() != null ? brDetails.getCategoryDesc() : "");
									//							            String categoryDesc = brDetails.getCategoryDesc() != null ? brDetails.getCategoryDesc() : "";
									//
									//							            m.put("occupation", categoryDesc);
									//							          
									//							            
									//							            
									//							        } else {
									//							            m.put("MembersValue", "");
									//							            m.put("occupation", "");
									//							        }

									m.put("interestsInsured", val.getItemValue());
									m.put("locationName", locationName);
									m.put("sectionId", sectionId); 
									m.put("companyWebsite", companyWebsite);

									return m;
								}).collect(Collectors.toList());
							}
							HomePositionMaster home1=homeRepo.findByQuoteNo(QuoteNo);
							BuildingRiskDetails matchedBuild = buildingRiskDetailsRepo.findFirstByQuoteNo(QuoteNo);

							if (matchedBuild != null) {
								categoryDes = Optional.ofNullable(matchedBuild.getCategoryDesc()).orElse("");
								String categoryId = Optional.ofNullable(matchedBuild.getCategoryId()).orElse("");

								 if ("P".equalsIgnoreCase(home1.getStatus()) && attachments.isEmpty()) {
									String attachmentType = "MISCELLANEOUS";
									if ("1".equalsIgnoreCase(categoryId)) attachmentType = "ADVOCATES";
									else if ("3".equalsIgnoreCase(categoryId)) attachmentType = "ENGINEERING";
									else if ("2".equalsIgnoreCase(categoryId)) attachmentType = "ACCOUNTANTS";
									
									   

									attachments.addAll(getAttachMentList(companyId, matchedBuild.getProductId(), attachmentType, null));
									 
								}
								result.put("attachMents", attachments);
								
								Map<String, Object> occupationMap = new LinkedHashMap<>();
								occupationMap.put("occupation1", categoryDes);
								result.put("occupationDetails", occupationMap);
							}

							List<BuildingRiskDetails> mem = buildingRiskDetailsRepo
									.findByQuoteNoAndCoverId(QuoteNo, 632);

							List<Map<String, Object>> membersList = new ArrayList<>();

							if (mem != null && !mem.isEmpty()) {

								for (BuildingRiskDetails item : mem) {

									Map<String, Object> membersMap = new LinkedHashMap<>();

									//							        String categoryDesc = Optional.ofNullable(item.getCategoryDesc()).orElse("");
									//							        String categoryId   = Optional.ofNullable(item.getCategoryId()).orElse("");

									String contentId = Optional.ofNullable(item.getContentId()).orElse("");
									String contentDesc = Optional.ofNullable(item.getContentDesc()).orElse("");
									Integer coverId1 = Optional.ofNullable(item.getCoverId()).orElse(0);
									//							        String sectionId = Optional.ofNullable(item.getSectionId()).orElse("");
									//							        String companyId = Optional.ofNullable(item.getCompanyId()).orElse("");
									String productId = Optional.ofNullable(item.getProductId()).orElse("");
									String wallType = Optional.ofNullable(item.getWallType()).orElse("");
									String wallTypeDesc = Optional.ofNullable(item.getWallTypeDesc()).orElse("");
									Integer buildingFloors = Optional.ofNullable(item.getBuildingFloors()).orElse(0);
									BigDecimal sumInsured = Optional.ofNullable(item.getSumInsured()).orElse(BigDecimal.ZERO);
									String indemityPeriod = Optional.ofNullable(item.getIndemityPeriod()).orElse("");

									//							        membersMap.put("categoryDesc", categoryDesc);
									//							        membersMap.put("categoryId", categoryId);


									membersMap.put("contentId", contentId);
									membersMap.put("contentDesc", contentDesc);
									membersMap.put("coverId", coverId1);
									membersMap.put("sectionId", sectionId);
									membersMap.put("companyId", companyId);
									membersMap.put("productId", productId);
									membersMap.put("wallType", wallType);
									membersMap.put("wallTypeDesc", wallTypeDesc);
									membersMap.put("buildingFloors", buildingFloors);
									membersMap.put("sumInsured", sumInsured);
									membersMap.put("indemityPeriod", indemityPeriod);

									membersList.add(membersMap);
								}
							}

							result.put("membersList", membersList);

							BuildingRiskDetails mem1 = buildingRiskDetailsRepo
									.findFirstByQuoteNoAndCoverId(QuoteNo, 632);
							BigDecimal sumInsured = BigDecimal.ZERO;
							String indemityPeriod = "";
							String wallType="";
							String wallTypeDesc="";
							String indemityPeriodDesc="";
							if(mem1 != null) {
								sumInsured = Optional.ofNullable(mem1.getSumInsured()).orElse(BigDecimal.ZERO);
								indemityPeriod = Optional.ofNullable(mem1.getIndemityPeriod()).orElse("");
								wallType = Optional.ofNullable(mem1.getWallType()).orElse("");
								wallTypeDesc = Optional.ofNullable(mem1.getWallTypeDesc()).orElse("");
							    //indemityPeriodDesc = (String)Optional.ofNullable(mem1.getIndemityPeriodDesc()).orElse("");
							}
							List<BuildingRiskDetails> memList = buildingRiskDetailsRepo
							        .findByQuoteNoAndCoverId(QuoteNo, 631);  
							if (memList != null && !memList.isEmpty()) {
							    BuildingRiskDetails firstMem = memList.get(0);  
							    indemityPeriodDesc = Optional.ofNullable(firstMem.getIndemityPeriodDesc()).orElse("");
							}
							result.put("GrossIncome",sumInsured);
							result.put("limitIndemity",indemityPeriod);
							result.put("PerLimitOfIndemity",wallType);
							result.put("PerLimitOfIndemityDesc",wallTypeDesc);
							result.put("indemityPeriodDesc", indemityPeriodDesc);

							finalResponse.put("memberSection", memberDetails);


							HomePositionMaster homeData = homeRepo.findByQuoteNo(QuoteNo);
							List<ExcessMaster> excessList = excessRepo
									.findByProductIdAndCompanyIdAndSectionId(
											homeData.getProductId().toString(), homeData.getCompanyId(), sectionId);

							excessDetails = excessList.stream().map(e -> {
								Map<String, Object> m = new LinkedHashMap<>();
								m.put("coverName", e.getCoverName());
								m.put("excessPercentage", e.getExcessPercentage());
								return m;
							}).collect(Collectors.toList());

							result.put("excessDetails", excessDetails);


							List<ExclusionMaster> exclusionList =  exclusionMasterRepo
									.findExclusionByProductIdAndCompanyIdAndSectionId(
											homeData.getProductId().toString(), homeData.getCompanyId(), sectionId);

							exclusionDetails = exclusionList.stream().map(ex -> {
								Map<String, Object>  m = new LinkedHashMap<>();
								m.put("exclusionDescription", ex.getExclusionDescription());
								return  m;
							}).collect(Collectors.toList());

							result.put("exclusionDetails", exclusionDetails);


							List<ClausesMaster> clauseList = clausesMasterRepo
									.findClausesByProductIdAndCompanyIdAndSectionId(
											homeData.getProductId().toString(), homeData.getCompanyId().toString(), sectionId);

							clauseDetails = clauseList.stream().map(c -> {
								Map<String, Object>  m = new LinkedHashMap<>();
								m.put("clausesDescription", c.getClausesDescription());
								return m;
							}).collect(Collectors.toList());

							result.put("clauseDetails", clauseDetails);


							List<PolicyCoverData> coverList = coverDataRepository.findByQuoteNoAndCoverageTypeAndVehicleId(
									QuoteNo,"T", 99999);

							List<Map<String, Object>> policyCoverDetails = coverList.stream().map(p -> {
								Map<String, Object> m = new LinkedHashMap<>();

								m.put("coverName", p.getCoverName());						    
								m.put("taxAmount", p.getTaxAmount());

								//						         if(p.getCoverageType().equals("T")) {
								//						           m.put("coverName", p.getCoverName());
								//						      m.put("taxAmount", p.getTaxAmount());
								//						         }
								//						        

								return m;
							}).collect(Collectors.toList());

							result.put("policyCoverData", policyCoverDetails);


							Map<String, Object> premiumDetails = new LinkedHashMap<>();
							if (homeData != null) {
								premiumDetails.put("premiumLc", homeData.getPremiumLc());
								premiumDetails.put("renewalDate", homeData.getEffectiveDate());
							} else {
								premiumDetails.put("premiumLc", null);
								premiumDetails.put("renewalDate", null);
							} 
					//						    log.info("Member Section (--) Data => " + finalResponse);
					//						    return ResponseEntity.ok(finalResponse);
						}else if(Arrays.asList("258","256").contains(sectionId)) {
							List<Map<String,Object>> bondList = buildingRiskData.stream().map(k ->{
								LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
								lmap.put("locationName", k.getLocationName());
								lmap.put("buildingAddress", k.getAddress());
								lmap.put("sectionId", sectionId);
								lmap.put("sectiondesc", k.getSectionDesc());
								lmap.put("industrydesc", k.getIndustryDesc());
								lmap.put("bondyear", k.getBondYear());
								lmap.put("bondSumInsured", k.getSumInsured());
								lmap.put("coveringdetails", k.getCoveringDetails());
								lmap.put("descriptionofrisk", k.getDescriptionOfRisk());
								lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
								lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
										&& f.getSectionId()==Integer.parseInt(sectionId))
										.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
								lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("coverId").equals(coverId) && f.get("coverNoteReferenceNo")!=null)
										.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
								return lmap;
							}).collect(Collectors.toList());
							
							bondDetails.addAll(bondList);
						}
						
						// CONDITIONS
						List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						// EXCLUSION
						List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
						List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
							Map<String,Object> eMap = new HashMap<String,Object>();
							eMap.put("conditionTerms", k.get("exclusioTerms"));
							eMap.put("SectionId", k.get("SectionId"));
							return eMap;
						}).collect(Collectors.toList());

						//WARRANTY
						List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList,conditionList,exclusionList).flatMap(Collection::stream)
								.sorted(Comparator.comparing(p -> {
									if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
										return Integer.MAX_VALUE;
									}
									try {
										return Integer.parseInt(p.get("Sno").toString());
									} catch (NumberFormatException e) {
										return Integer.MAX_VALUE;
									}
								}))
								.map(u -> {
									LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
									m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
									return m;
								}).distinct().collect(Collectors.toList());

						int conditionsize = termsAndconditions.size();
						List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
						if(conditionsize>10) {
							int midIndex = conditionsize / 2;
							firstHalf = termsAndconditions.subList(0, midIndex);
							secondHalf = termsAndconditions.subList(midIndex, conditionsize);
						}else {
							firstHalf = termsAndconditions.subList(0, conditionsize);
						}

						//						List<PolicyCoverData> excessCon = coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getCoverageType().equalsIgnoreCase("B") && f.getSectionId()==Integer.parseInt(sectionId)).distinct().collect(Collectors.toList());
						//						if(!excessCon.isEmpty()) {
						//							PolicyCoverData cpd = excessCon.get(0);
						//							Map<String,Object> excessMap = new HashMap<String,Object>();
						//							excessMap.put("excessPercent", cpd.getExcessPercent());
						//							excessMap.put("excessAmount", cpd.getExcessAmount());
						//							excessMap.put("excessDesc", cpd.getExcessDesc());
						//							excessMap.put("currency", cpd.getCurrency());
						//							excessConDetails.add(excessMap);
						//						}
						String productId=map.get("productId").toString();
						List<ExcessMaster> excess1=excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(companyId,productId ,sectionId, coverId);	
						if(!excess1.isEmpty() ) {
							ExcessMaster cpd = excess1.get(0);
							Map<String,Object> excessMap = new HashMap<String,Object>();
							excessMap.put("excessPercent", cpd.getExcessPercentage()==null?0:cpd.getExcessPercentage());
							excessMap.put("excessAmount", cpd.getExcessAmount()==null?0.0:cpd.getExcessAmount());
							excessMap.put("excessDesc", cpd.getExcessDescription()==null?"":cpd.getExcessDescription());
							excessMap.put("currency", cpd.getCurrency()==null?"":cpd.getCurrency());
							excessConDetails.add(excessMap);
						}


						coverMap.put("sectionDesc", Slist.stream().filter(k -> sectionId.equalsIgnoreCase(k.get("sectionId").toString())).map(e -> e.get("sectionDesc").toString()).findFirst().orElse(""));
						coverMap.put("coverDesc", Slist.stream().filter(k -> coverId.equalsIgnoreCase(k.get("coverId").toString())).map(e -> e.get("coverDesc").toString()).findFirst().orElse(""));
						coverMap.put("buildingDetails", buildingDetails);
						coverMap.put("allriskDetails", allriskDetails);
						coverMap.put("contentDetails", contentDetails);
						//						coverMap.put("domesticDetails", domesticDetails);
						coverMap.put("electronicDetails",electronicDetails);
						coverMap.put("ownersDetails", ownersDetails);
						coverMap.put("firstHalfconditions", firstHalf);
						coverMap.put("secondHalfconditions", secondHalf);
						coverMap.put("excessConditions", excessConDetails);
						coverMap.put("personalAccDetails", personalAccDetails);
						coverMap.put("clauseDetails", clauseDetails);
						coverMap.put("excessDetails", excessDetails);
						coverMap.put("exclusionDetails", exclusionDetails);
						coverMap.put("bondDetails", bondDetails);
						coverMap.put("sectionId", sectionId);
						coverageList.add(coverMap);
					}
				}
				
				List<PolicyCoverData> coverList =
				        coverDataRepository.findByQuoteNoAndVehicleId(QuoteNo, 99999);

				
				List<Map<String, Object>> policyCoverDetails = new ArrayList<>();
				
				coverList.stream()
				        .filter(c -> "B".equalsIgnoreCase(c.getCoverageType())
				                || "O".equalsIgnoreCase(c.getCoverageType()))
				        .forEach(c -> {
				            Map<String, Object> entry = new LinkedHashMap<>();
				            entry.put("coverName", c.getCoverName()); // Cover Name

				            BigDecimal premium = c.getPremiumExcludedTaxLc() == null
				                    ? BigDecimal.ZERO
				                    : c.getPremiumExcludedTaxLc();

				            entry.put("taxAmount", premium.toPlainString());
				            policyCoverDetails.add(entry);
				        });


				
				Map<Integer, BigDecimal> taxSummary =
				        coverList.stream()
				                .filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
				                .filter(c -> c.getTaxId() != null)
				                .collect(Collectors.groupingBy(
				                        PolicyCoverData::getTaxId,
				                        LinkedHashMap::new,
				                        Collectors.reducing(
				                                BigDecimal.ZERO,
				                                c -> c.getTaxAmountLc() == null
				                                        ? BigDecimal.ZERO
				                                        : c.getTaxAmountLc(),
				                                BigDecimal::add
				                        )
				                ));


				
				taxSummary.forEach((taxId, totalTax) -> {

				    
				    PolicyCoverData anyTaxRow = coverList.stream()
				            .filter(c -> taxId.equals(c.getTaxId()))
				            .findFirst()
				            .orElse(null);

				    if (anyTaxRow != null) {
				        Map<String, Object> entry = new LinkedHashMap<>();
				        entry.put("coverName", anyTaxRow.getTaxDesc());                
				        entry.put("taxAmount", totalTax.toPlainString());          
				        policyCoverDetails.add(entry);
				    }
				});


				result.put("policyCoverData", policyCoverDetails);
				
				String sectionId = sectionIds.get(0).toString();
				
				// CONDITIONS
				List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

				// EXCLUSION
				List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
				List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
					Map<String,Object> eMap = new HashMap<String,Object>();
					eMap.put("conditionTerms", k.get("exclusioTerms"));
					eMap.put("SectionId", k.get("SectionId"));
					return eMap;
				}).collect(Collectors.toList());

				//WARRANTY
				List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

				List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList,conditionList,exclusionList).flatMap(Collection::stream)
						.sorted(Comparator.comparing(p -> {
							if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
								return Integer.MAX_VALUE;
							}
							try {
								return Integer.parseInt(p.get("Sno").toString());
							} catch (NumberFormatException e) {
								return Integer.MAX_VALUE;
							}
						}))
						.map(u -> {
							LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
							m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
							return m;
						}).distinct().collect(Collectors.toList());

				int conditionsize = termsAndconditions.size();
				List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
				if(conditionsize>10) {
					int midIndex = conditionsize / 2;
					firstHalf = termsAndconditions.subList(0, midIndex);
					secondHalf = termsAndconditions.subList(midIndex, conditionsize);
				}else {
					firstHalf = termsAndconditions.subList(0, conditionsize);
				}
				
				
				result.put("Commonconditions", firstHalf);


//				List<PolicyCoverData> coverList =
//				        coverDataRepository.findByQuoteNoAndVehicleId(QuoteNo, 99999);
//
//				List<Map<String, Object>> policyCoverDetails = coverList.stream().map(p -> {
//					Map<String, Object> m = new LinkedHashMap<>();
//
//					m.put("coverName", p.getCoverName());						    
//					m.put("taxAmount", p.getTaxAmount());
//
//					//			         if(p.getCoverageType().equals("T")) {
//					//			           m.put("coverName", p.getCoverName());
//					//			      m.put("taxAmount", p.getTaxAmount());
//					//			         }
//					//			        
//
//					return m;
//				}).collect(Collectors.toList());
//
//				result.put("policyCoverData", policyCoverDetails);
				Map<Object,List<Map<String,Object>>> groupBycoverageDetails = coverageList.stream()
						.collect(Collectors.groupingBy(k -> k.get("coverDesc"), Collectors.toList()));
				for(Map.Entry<Object, List<Map<String,Object>>> CDEntry : groupBycoverageDetails.entrySet()) {
					LinkedHashMap<String, Object> coverMap = new LinkedHashMap<String, Object>();
					coverMap.put("coverId", Slist.stream().filter(f -> f.get("coverDesc").equals(CDEntry.getKey())).map(m -> m.get("coverId")).findFirst().orElse(""));
					coverMap.put("coverKey", CDEntry.getValue().stream()
							.filter(e -> Arrays.asList(108, 109, 114, 115, 33, 111).contains(Integer.parseInt(e.get("sectionId").toString())))
							.map(e -> "BUSINESS INTERRUPTION (" + CDEntry.getKey().toString() + ")".toUpperCase()+ " " +(map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE"))
							.findFirst()
							.orElse(CDEntry.getKey().toString().toUpperCase() + " " + (map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE")));
					coverMap.put("coverValue", CDEntry.getValue());//CDEntry.getValue()
					coverMap.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
					coverMap.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
					coverMap.put("productId", map.get("productId")==null?"":map.get("productId").toString());
					coverMap.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
					coverMap.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
					coverMap.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
					coverageDetails.add(coverMap);
				}

				List<EserviceBuildingDetails> buildingDtl = eserviceBuildingDetailsRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(QuoteNo, "Y");
				String buildingOwnerYn = buildingDtl.isEmpty()?"":buildingDtl.get(0).getBuildingOwnerYn()==null?"":buildingDtl.get(0).getBuildingOwnerYn();
				List<Map<String,Object>> domesticKeyFactor = listItemValueRepo.getDomesticKeyFactor(buildingOwnerYn.equalsIgnoreCase("Y")?"1":"2",companyId);
				if(!domesticKeyFactor.isEmpty()) {
					String attachmentloc = this.getClass().getClassLoader().getResource("").getPath().replaceAll("%20", "")+"report/attachments/";
					domesticKeyFactor.forEach(k->{
						AttachMentRes a = AttachMentRes.builder()
								.docRefNo(k.get("ITEM_CODE")==null?"":k.get("ITEM_CODE").toString())
								.docloction(k.get("ITEM_VALUE")==null?"":(attachmentloc+k.get("ITEM_VALUE").toString()))
								.build();
						attachments.add(a);
					});
				}

				List<Map<String,Object>> firstLossPayeesList = new ArrayList<Map<String,Object>>();
				List<FirstLossPayee> firstLossPayees = firstLossPayeeRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
				if(!firstLossPayees.isEmpty()) {
					firstLossPayees.forEach(k -> {
						Map<String,Object> custMap = new HashMap<String,Object>();
						custMap.put("firstLossPayee", k.getFirstLossPayeeDesc());
						firstLossPayeesList.add(custMap);
					});
				}

				if(Arrays.asList("100046","100047","100048","100049","100050").contains(map.get("companyId")==null?"":map.get("companyId").toString()) || "100020".equalsIgnoreCase(companyId)) {
					LinkedList<Map<String,Object>> secdetails_f = new LinkedList<Map<String,Object>>();
					Map<Object, List<Tuple>> sectionDetails = Slist.stream().collect(Collectors.groupingBy(k -> k.get("locationName"), Collectors.toList()));
					for(Map.Entry<Object, List<Tuple>> secEntry : sectionDetails.entrySet()) {
						LinkedHashMap<String, Object> sec_map = new LinkedHashMap<String, Object>();
						sec_map.put("locationName", capitalizeFirstLetter(secEntry.getKey()==null?"":secEntry.getKey().toString()));
						Map<Object, List<Tuple>> k = secEntry.getValue().stream()
								.collect(Collectors.groupingBy(j -> j.get("sectionDesc"), Collectors.toList()));
						LinkedList<Map<String,Object>> secdetails = new LinkedList<Map<String,Object>>();
						for(Map.Entry<Object, List<Tuple>> t : k.entrySet()) {
							LinkedList<Map<String,Object>> sec_list = new LinkedList<Map<String,Object>>();
							Map<String,Object> f = new HashMap<String,Object>();
							f.put("SectionName", t.getKey()==null?"":capitalizeFirstLetter(t.getKey().toString()));
							Map<String,Object> sectionConditions = new HashMap<>();

							if (coverageList != null && t != null && t.getKey() != null) {
							    for (Map<String, Object> coverage : coverageList) {
							        if (coverage != null) {
							            Object sectionDesc = coverage.get("sectionDesc");
							            if (sectionDesc != null && sectionDesc.toString().equals(t.getKey().toString())) {
							                sectionConditions.put("firstHalfconditions", coverage.get("firstHalfconditions"));
							                sectionConditions.put("secondHalfconditions", coverage.get("secondHalfconditions"));
							                break;
							            }
							        }
							    }
							}
							if (sectionConditions.isEmpty()) {
							    sectionConditions = Collections.emptyMap();
							}
							for(int i =0;i<t.getValue().size();i++) {
								LinkedList<Map<String,Object>> addcoverdetails = new LinkedList<Map<String,Object>>();
								LinkedList<Map<String,Object>> excessdetails = new LinkedList<Map<String,Object>>();
								Tuple o = t.getValue().get(i);
								LinkedHashMap<String,Object> s = new LinkedHashMap<String,Object>();
								s.put("covername", o.get("coverDesc")==null?"":capitalizeFirstLetter(o.get("coverDesc").toString()));
								s.put("suminsured", o.get("sumInsured")==null?0.00:Double.parseDouble(o.get("sumInsured").toString()));
								s.put("annually", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString()));
								s.put("monthly", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString())/12);


								List<ExcessMaster> excessList = excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
										map.get("companyId")==null?"":map.get("companyId").toString(),
												map.get("productId")==null?"":map.get("productId").toString(),
														o.get("sectionId")==null?"":o.get("sectionId").toString(),
																o.get("coverId")==null?"":o.get("coverId").toString());

								if(excessList!=null && excessList.size()>0) {
									excessList.forEach(e_l -> {
										Map<String,Object> e = new HashMap<String,Object>();
										e.put("excessdesc", e_l.getExcessDescription()==null?"":e_l.getExcessDescription());
										e.put("excessamount", e_l.getExcessAmount()==null?0.00:e_l.getExcessAmount());
										e.put("excessper", e_l.getExcessPercentage()==null?0:e_l.getExcessPercentage());
										e.put("excessName", e_l.getCoverName()==null?"":e_l.getCoverName());
										excessdetails.add(e);
									});
								}

								List<PolicyCoverData> additionalCover = coverData.stream()
										.filter(u -> "A".equalsIgnoreCase(u.getCoverageType())
												&& u.getSectionId() == Integer.parseInt(o.get("sectionId").toString()))
										.collect(Collectors.toList());
								if(additionalCover!=null && additionalCover.size()>0) {
									additionalCover.forEach(p -> {
										Map<String,Object> e = new HashMap<String,Object>();
										e.put("covername", p.getCoverDesc()==null?"":capitalizeFirstLetter(p.getCoverDesc().toString()));
										e.put("coverlimit", p.getCoverageLimit()==null?BigDecimal.ZERO:p.getCoverageLimit());
										addcoverdetails.add(e);
									});
								}
								s.put("addcoverdetails", addcoverdetails);
								s.put("excessdetails", excessdetails);
								sec_list.add(s);
							}



							sec_list.sort(Comparator.comparing(o -> (String) o.get("covername")));
							f.put("sectionList", sec_list);
							f.put("firstHalfconditions", sectionConditions.get("firstHalfconditions"));
							f.put("secondHalfconditions", sectionConditions.get("secondHalfconditions"));
							secdetails.add(f);
						}
						sec_map.put("sectionDetails", secdetails);
						secdetails_f.add(sec_map);
					}

					result.put("sectionDetails", secdetails_f);

					LinkedList<Map<String,Object>> sec_list = new LinkedList<Map<String,Object>>();
					for(Map.Entry<Object, List<Tuple>> secEntry : sectionDetails.entrySet()) {
						LinkedHashMap<String, Object> sec_map = new LinkedHashMap<String, Object>();
						sec_map.put("locationName", secEntry.getKey());
						Map<Object, List<Tuple>> k = secEntry.getValue().stream()
								.collect(Collectors.groupingBy(j -> j.get("sectionDesc"), Collectors.toList()));
						List<Map<String,Object>> j_list = new ArrayList<Map<String,Object>>();
						for(Map.Entry<Object, List<Tuple>> t : k.entrySet()) {
							Map<String,Object> j = new HashMap<String,Object>();
							j.put("sectionDesc", t.getKey()==null?"":capitalizeFirstLetter(t.getKey().toString()));
							Double premium_section = t.getValue().stream().map(w -> (BigDecimal) w.get("premiumExcludedTaxFc"))
									.collect(Collectors.summingDouble(BigDecimal::doubleValue));
							Double suminsured_section = t.getValue().stream().map(w -> (BigDecimal) w.get("sumInsured"))
									.collect(Collectors.summingDouble(BigDecimal::doubleValue));
							
							j.put("suminsured", suminsured_section);
							j.put("annually", premium_section);
							j.put("monthly", premium_section/12);
						//	j.put("SectionPremiumList",getTaxSummaryListSection(QuoteNo,Integer.valueOf(t.getValue().get(0).get("sectionId").toString()),Integer.valueOf(t.getValue().get(0).get("locationId").toString())));
							j_list.add(j);
						}
						j_list.sort(Comparator.comparing(o -> (String) o.get("sectionDesc")));
						sec_map.put("sectionList", j_list);
						sec_list.add(sec_map);
					}

					result.put("sectionList", sec_list);

					result.put("phoenixVatPercent", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxRate()).findAny().orElse(BigDecimal.ZERO));
					result.put("phoenixVatAmount", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
				}

				List<Map<String,Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(map.get("companyId")==null?"":map.get("companyId").toString());
				if(!companyDetails.isEmpty()) {
					result.put("companyName", companyDetails.get(0).get("COMPANY_NAME")==null?"":companyDetails.get(0).get("COMPANY_NAME").toString());
					result.put("companylogo", companyDetails.get(0).get("COMPANY_LOGO")==null?"":companyDetails.get(0).get("COMPANY_LOGO").toString());
					result.put("companyWebsite", companyDetails.get(0).get("COMPANY_WEBSITE")==null?"":companyDetails.get(0).get("COMPANY_WEBSITE").toString());
					result.put("companyMail", companyDetails.get(0).get("COMPANY_EMAIL")==null?"":companyDetails.get(0).get("COMPANY_EMAIL").toString());
					result.put("companyPhone", companyDetails.get(0).get("COMPANY_PHONE")==null?"":companyDetails.get(0).get("COMPANY_PHONE").toString());
					result.put("companyAddress", companyDetails.get(0).get("COMPANY_ADDRESS")==null?"":companyDetails.get(0).get("COMPANY_ADDRESS").toString());
					result.put("companyPoBox", companyDetails.get(0).get("PO_BOX")==null?"":companyDetails.get(0).get("PO_BOX").toString());
					result.put("companyVrnNumber", companyDetails.get(0).get("VRN_NUMBER")==null?"":companyDetails.get(0).get("VRN_NUMBER").toString());
					result.put("companyremarks", companyDetails.get(0).get("REMARKS")==null?"":companyDetails.get(0).get("REMARKS").toString());
				}

				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("branchCode", map.get("branchCode")==null?"":map.get("branchCode").toString());
				result.put("agencyCode", map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("address", map.get("address1") == null ? "" : map.get("address1").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				result.put("entryDate", map.get("entryDate")==null?"":map.get("entryDate").toString());
				result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
				result.put("brokerBranchName", map.get("brokerBranchName")==null?"":map.get("brokerBranchName").toString());
				result.put("productName", map.get("productName")==null?"":map.get("productName").toString().toUpperCase()+" "+(map.get("policyNo")==null?"\nQuote Schedule":"\nPolicy Schedule"));
				result.put("stateName", map.get("stateName")==null?"":map.get("stateName").toString());
				result.put("cityName", map.get("cityName")==null?"":map.get("cityName").toString());
				result.put("mobileNo", map.get("mobileNo")==null?"":map.get("mobileNo").toString());
				result.put("customerId", map.get("customerId")==null?"":map.get("customerId").toString());
				result.put("brokerName", map.get("brokerName")==null?"":map.get("brokerName").toString());
				result.put("coreAppBrokerCode", map.get("coreAppBrokerCode")==null?"":map.get("coreAppBrokerCode").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("debitNoteNo", map.get("debitNoteNo")==null?"":map.get("debitNoteNo").toString());
				result.put("premium", map.get("premium")==null?"":new BigDecimal(Double.parseDouble(map.get("premium").toString())).toString());
				result.put("vatPremium", map.get("vatPremium")==null?"":Double.parseDouble(map.get("vatPremium").toString()));
				result.put("vatPercent", map.get("vatPercent")==null?"":Double.parseDouble(map.get("vatPercent").toString()));
				result.put("totalPremium", map.get("totalPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("totalPremium").toString())).toString());
				result.put("signature", map.get("signature")==null?"":map.get("signature").toString());
				result.put("place", map.get("place")==null?"":map.get("place").toString());
				result.put("productId", map.get("productId")==null?"":map.get("productId").toString());
				result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				result.put("taxName", map.get("companyId")==null?"":map.get("companyId").toString().equalsIgnoreCase("100004")?"Premium":"Vat");
				result.put("userMobile", map.get("userMobile")==null?"":map.get("userMobile").toString());
				result.put("identificationNo", map.get("identificationNo")==null?"":map.get("identificationNo").toString());
				result.put("postalAddress", StringUtils.join(
						Arrays.asList(
								map.get("address1") == null ? "" : map.get("address1").toString(),
										map.get("pinCode") == null ? "" : map.get("pinCode").toString(),
												map.get("stateName") == null ? "" : map.get("stateName").toString(),
														map.get("cityName") == null ? "" : map.get("cityName").toString(),
																map.get("countryName") == null ? "" : map.get("countryName").toString()
								).stream()
						.filter(value -> !value.isEmpty())
						.collect(Collectors.joining(","))
						));
				result.put("mobileNo1", map.get("mobileNo1")==null?"":map.get("mobileNo1").toString());
				result.put("approvedBy", map.get("approvedBy")==null?"":map.get("approvedBy").toString());
				result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
				result.put("renewalDate", map.get("expiryDate")==null?"":RenewalDate(map.get("expiryDate").toString()));
				result.put("loginId", map.get("loginId")==null?"":map.get("loginId").toString());
				result.put("bdmName", map.get("bdmName")==null?"":map.get("bdmName").toString());
				result.put("bdmCode", map.get("bdmCode")==null?"":map.get("bdmCode").toString());
				result.put("overAllPremium", OverAllPremium);
				result.put("premiumDetails", premiumDetailsRes);
				//result.put("sectionDetails", sectionList);
				//result.put("locationDetails", locationDetails);
				result.put("firstLossPayeesList", firstLossPayeesList);
				result.put("coverageDetails",  coverageDetails.stream()
						.sorted(Comparator.comparing(o -> (Integer) o.get("coverId")))
						.collect(Collectors.toList()));
				

				// Existing Result
			
				// Existing Result
				result.put("bdmName", map.get("bdmName") == null ? "" : map.get("bdmName").toString());
				result.put("bdmCode", map.get("bdmCode") == null ? "" : map.get("bdmCode").toString());
				result.put("overAllPremium", OverAllPremium);
				result.put("premiumDetails", premiumDetailsRes);
				
				
			}
		}catch(Exception e) {
			log.info("Error in EwaySchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into EwaySchedule");
		return result;
	}
	
	public Map<String,Object> getEwaySchedule(String QuoteNo){
		log.info("Enter into EwaySchedule.\nArgument ==> "+QuoteNo);
		Map<String,Object> result = new HashMap<String,Object>();
		List<AttachMentRes> attachments = new ArrayList<>();
		try {
			List<BuildingRiskDetails> buildList = buildingRiskDetailsRepo.findByQuoteNoOrderByRiskIdAsc(QuoteNo);
		    List<CommonDataDetails> commonList = commonDataDetailsRepo.findByQuoteNoOrderByRiskIdAsc(QuoteNo);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);

			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginBranchMaster> lbmRoot= cq.from(LoginBranchMaster.class);

			Subquery<Integer> cmAmd = cq.subquery(Integer.class);
			Root<CountryMaster> cmAmdRoot = cmAmd.from(CountryMaster.class);
			cmAmd.select(cb.max(cmAmdRoot.get("amendId"))).where(cb.equal(cmAmdRoot.get("countryId"), piRoot.get("nationality")),cb.equal(cmAmdRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(cmAmdRoot.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> ScmRoot = countryName.from(CountryMaster.class);
			countryName.select(ScmRoot.get("countryName")).where(cb.equal(ScmRoot.get("countryId"), piRoot.get("nationality")),cb.equal(ScmRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(ScmRoot.get("status"), "Y"),cb.equal(ScmRoot.get("amendId"), cmAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(hpmRoot.get("companyId"), icmAmdRoot.get("companyId")),cb.equal(icmAmdRoot.get("status"), "Y"),
					cb.between(cb.literal(new Date()), icmAmdRoot.get("effectiveDateStart"), icmAmdRoot.get("effectiveDateEnd")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("requestReferenceNo").alias("requestReferenceNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
					.when(cb.equal(piRoot.get("titleDesc"),""), "").otherwise(".").as(String.class),piRoot.get("clientName"))).alias("customerName"),
					piRoot.get("address1").alias("address1"),piRoot.get("pinCode").alias("pinCode"),countryName.alias("countryName"),hpmRoot.get("entryDate").alias("entryDate"),
					piRoot.get("email1").alias("email1"),piRoot.get("mobileNo1").alias("mobileNo1"),hpmRoot.get("branchCode").alias("branchCode"),luiRoot.get("agencyCode").alias("agencyCode"),piRoot.get("idNumber").alias("identificationNo"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("brokerBranchName").alias("brokerBranchName"),
					hpmRoot.get("productName").alias("productName"),piRoot.get("stateName").alias("stateName"),piRoot.get("cityName").alias("cityName"),cb.concat(piRoot.get("mobileCodeDesc1"), cb.concat("-", piRoot.get("mobileNo1"))).alias("mobileNo"),
					piRoot.get("customerId").alias("customerId"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Agent","Premia Direct","Premia Broker")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("brokerName"),luiRoot.get("coreAppBrokerCode").alias("coreAppBrokerCode"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("vatPercent").alias("vatPercent"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),
					icmRoot.get("signature").alias("signature"),lbmRoot.get("branchName").alias("place"),companyName.alias("companyName"),imageURL.alias("companylogo"),hpmRoot.get("companyId").alias("companyId"),hpmRoot.get("productId").alias("productId"),
					hpmRoot.get("debitNoteNo").alias("debitNoteNo"),luiRoot.get("userMobile").alias("userMobile"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
					.when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("b2c","Direct")), "System").otherwise(luiRoot.get("userName")).alias("approvedBy"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("bdmName").alias("bdmName"),hpmRoot.get("bdmCode").alias("bdmCode"),
					cb.selectCase().when(cb.equal(hpmRoot.get("applicationId"),"1"), hpmRoot.get("loginId")).otherwise(hpmRoot.get("applicationId")).alias("loginId"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(hpmRoot.get("agencyCode").as(String.class), luiRoot.get("agencyCode")),cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(hpmRoot.get("loginId"), lbmRoot.get("loginId")),cb.equal(hpmRoot.get("companyId"), lbmRoot.get("companyId")),cb.equal(hpmRoot.get("branchCode"), lbmRoot.get("branchCode")),cb.equal(lbmRoot.get("status"), "Y"),
					cb.equal(icmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), icmRoot.get("effectiveDateStart"), icmRoot.get("effectiveDateEnd")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("quoteNo"), QuoteNo));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<PolicyCoverData> coverData = coverDataRepository.findByQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());

				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);
				//List<EserviceCommonDetails> eserviceCommonList = eserviceCommonDetRepo.findByQuoteNo(map.get("quoteNo").toString());

				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),map.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("locationId"), sddRoot.get("locationId")));
				predicate.add(cb.equal(pcdRoot.get("vehicleId"), sddRoot.get("riskId")));
				predicate.add(cb.equal(pcdRoot.get("coverId"), sddRoot.get("coverId")));
				/*if(!eserviceCommonList.isEmpty()) {
					Root<EserviceCommonDetails> ecdRoot = cq1.from(EserviceCommonDetails.class);
					eserviceQuote = ecdRoot.get("occupationDesc").alias("occupationDesc");
					predicate.add(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")));
					predicate.add(cb.equal(pcdRoot.get("sectionId"), ecdRoot.get("sectionId")));
					predicate.add(cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")));
					predicate.add(cb.equal(pcdRoot.get("productId"), ecdRoot.get("productId")));
					predicate.add(cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));
				}*/
				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);

				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")),cb.equal(pcdRoot.get("locationId"), ecdRoot.get("locationId")),
						cb.equal(pcdRoot.get("coverId"), ecdRoot.get("coverId")));

				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),sddRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),
						pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
						pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"),
						sddRoot.get("locationId").alias("locationId"),sddRoot.get("locationName").alias("locationName"),
						sddRoot.get("productType").alias("productType"),pcdRoot.get("coverageLimit").alias("coverageLimit"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));

				List<Tuple> Slist = em.createQuery(cq1).getResultList();
				List<Map<String,Object>>sectList=new ArrayList<>();
				Double minAdjPrem=0.0,minAdjPremFc=0.0,basePremium=0.0,basePremiumFc=0.0,
						minAdjExPrem=0.0,minAdjExPremFc=0.0,baseExPremium=0.0,baseExPremiumFc=0.0;

				for (int i=0;i<Slist.size();i++) {
					Tuple t=Slist.get(i);
					String coverId = t.get("coverId")==null?"":t.get("coverId").toString();
					String coverageType = t.get("coverageType")==null?"":t.get("coverageType").toString();
					if("945".equals(coverId)){
						minAdjPrem=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						minAdjPremFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						minAdjExPrem=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						minAdjExPremFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if("B".equals(coverageType)) {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
						basePremium=basePremium+minAdjPrem;
						basePremiumFc=basePremiumFc+minAdjPremFc;
						baseExPremium=baseExPremium+minAdjExPrem;
						baseExPremiumFc=baseExPremiumFc+minAdjExPremFc;
					}else {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if(!"945".equals(coverId)){
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", t.get("sectionDesc"));
						Smap.put("occupationDesc", t.get("occupationDesc"));
						Smap.put("coverDesc", t.get("coverDesc"));
						Smap.put("sumInsured", t.get("sumInsured"));
						Smap.put("rate", t.get("rate"));
						Smap.put("premiumIncludedTaxLc", basePremium);
						Smap.put("premiumIncludedTaxFc", basePremiumFc);
						Smap.put("premiumExcludedTaxLc", baseExPremium);
						Smap.put("premiumExcludedTaxFc", baseExPremiumFc);
						sectList.add(Smap);
					}
				}
				List<Map<String,Object>> sectionList = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> coverageDetails = new ArrayList<Map<String,Object>>();
				List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
				Double OverAllPremium=0.0;
				String companyId = map.get("companyId")==null?"":map.get("companyId").toString();
				List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
				List<Object> coverIds = Slist.stream().map(k -> k.get("coverId")).distinct().collect(Collectors.toList());
				if("100002".equalsIgnoreCase(companyId) || "100020".equalsIgnoreCase(companyId)) {
					if(coverData!=null && !coverData.isEmpty()) {
						Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
								.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
								.findAny().orElse(0.0);

						Double taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
								.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));


						List<Map<String,Object>> sectionPremium = new ArrayList<Map<String,Object>>();
						for(int x=0;x<sectionIds.size();x++) {
							int s = Integer.parseInt(sectionIds.get(x).toString());
							System.out.println(new Gson().toJson(coverData.stream().filter(f -> f.getSectionId()==s)
									.collect(Collectors.toList())));
							List<Integer> coverids = coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s)
									.map(m -> m.getCoverageType().equalsIgnoreCase("L")?m.getDiscLoadId():m.getCoverId()).distinct()
									.collect(Collectors.toList());
							for(int j=0;j<coverids.size();j++) {
								int c = coverids.get(j);
								Map<String,Object> o = new HashMap<String,Object>();
								o.put("SectionId", s);
								o.put("CoverDesc", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
										&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
										.map(m -> m.getCoverName()).findFirst().orElse("-N-A"));
								o.put("TotPremium", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
										&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
										.map(m -> m.getPremiumExcludedTaxFc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
								sectionPremium.add(o);
							}
						}
						String occupationValue = Slist.stream()
								.filter(t -> t.get("occupationDesc") != null && 
								!t.get("occupationDesc").toString().equals("null") &&
								!t.get("occupationDesc").toString().isEmpty())
								.map(t -> t.get("occupationDesc").toString())
								.findFirst()
								.orElse("Not Specified");

						// Add occupation to result
						result.put("occupation", occupationValue);


						/*						List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && (f.getDiscLoadId()==0 || f.getCoverageType().equalsIgnoreCase("L")) && f.getSectionId()!=99999
								&& (f.getCoverageType().equals("O") && Arrays.asList("Y","D").contains(f.getIsSelected().equalsIgnoreCase("Y")?"Y":f.getIsSelected().equalsIgnoreCase("D")?"D":"N") 
								|| !f.getCoverageType().equalsIgnoreCase("O")))
								.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverId(),Collectors.reducing(
									BigDecimal.ZERO, PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
								.entrySet().stream()
								.flatMap((Map.Entry<Integer,Map<Integer,BigDecimal>> s ) -> {
									Integer sectionId = s.getKey();
									return s.getValue().entrySet().stream()
											.map((Map.Entry<Integer,BigDecimal> g )-> {
												Integer coverId = g.getKey();
												BigDecimal totPremium = g.getValue();
												Map<String,Object> secMap = new HashMap<String,Object>();
												secMap.put("SectionId", sectionId);
												secMap.put("CoverDesc", coverData.stream().filter(f -> f.getTaxId()==0
														&& (f.getDiscLoadId()==0 || f.getCoverageType().equalsIgnoreCase("L")) && f.getSectionId()==sectionId && f.getCoverId()==coverId)
														.map(m -> m.getCoverName()).findFirst().orElse("-N-A"));
												secMap.put("TotPremium", totPremium);
												return secMap;
											});
								}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());*/

						sectionPremium = sectionPremium.stream().sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());
						if("100020".equalsIgnoreCase(companyId)) {
							sectionPremium = sectionPremium.stream().filter(f -> new BigDecimal(f.get("TotPremium").toString()).compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
							List<PolicyCoverData> spData = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
									.collect(Collectors.toList());
							if(spData !=null && spData.size()>0) {
								List<Map<String,Object>> subspData = new ArrayList<Map<String,Object>>();
								spData.forEach(sp -> {
									Map<String,Object> spMap = new HashMap<String,Object>();
									spMap.put("SectionId", sp.getSectionId());
									spMap.put("CoverDesc", sp.getCoverName()+" ("+sp.getTaxRate()+"% )");
									spMap.put("TotPremium", sp.getTaxAmount());
									subspData.add(spMap);
								});
								sectionPremium.addAll(subspData);
							}
						}
						sectionPremium.forEach(k -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
									.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
									.build();
							premiumDetailsRes.add(u);
						});


						OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue))+taxAmount;
						result.put("vatPercent", taxRate.toString());
						result.put("vatAmount", taxAmount.toString());
					}
				}else if("100004".equalsIgnoreCase(companyId)){
					sectList.forEach(k -> {
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", k.get("sectionDesc"));
						Smap.put("coverDesc", k.get("coverDesc"));
						Smap.put("sumInsured", k.get("sumInsured"));
						Smap.put("rate", k.get("rate"));
						Smap.put("premiumIncludedTaxLc", k.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", k.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", k.get("premiumExcludedTaxLc"));
						Smap.put("premiumExcludedTaxFc", k.get("premiumExcludedTaxFc"));
						Smap.put("vehicleId",k.get("vehicleId"));
						sectionList.add(Smap);
						result.put("occupationDesc", sectList.stream().filter(f -> f.get("occupationDesc") != null && !f.get("occupationDesc").toString().equals("null")).map(m -> m.get("occupationDesc"))
								.map(Object::toString).findAny().orElse(null));
					});
				}else {
					Map<Object, List<Map<String,Object>>> sectionRes = sectList.stream().collect(Collectors.groupingBy(g -> g.get("sectionDesc"),Collectors.mapping(v ->{
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("occupationDesc", v.get("occupationDesc"));
						Smap.put("coverDesc", v.get("coverDesc"));
						Smap.put("sumInsured", v.get("sumInsured"));
						Smap.put("rate", v.get("rate"));
						Smap.put("premiumIncludedTaxLc", v.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", v.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", v.get("premiumExcludedTaxLc"));
						Smap.put("premiumExclhitudedTaxFc", v.get("premiumExcludedTaxFc"));
						return Smap;
					}, Collectors.toList())));			
					for(Map.Entry<Object, List<Map<String,Object>>> entry :sectionRes.entrySet()) {
						Map<String, Object> sectionMap = new HashMap<String, Object>();
						sectionMap.put("sectionKey", entry.getKey());
						sectionMap.put("sectionValue", entry.getValue());
						sectionList.add(sectionMap);
					}
				}

				List<Object> locationIds = Slist.stream().map(k -> k.get("locationId")).distinct().collect(Collectors.toList());
				List<Map<String,Object>> coverageList = new ArrayList<Map<String,Object>>();
				for(int i=0;i<coverIds.size();i++) {
					Map<String,Object> coverMap = new HashMap<String,Object>();
					List<Map<String,Object>> buildingDetails = new ArrayList<Map<String,Object>>();

					List<Map<String,Object>> allriskDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> contentDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> electronicDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> ownersDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> excessConDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> personalAccDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> bondDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> exclusionDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> excessDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> clauseDetails = new ArrayList<Map<String,Object>>();
					String coverId = coverIds.get(i).toString() ;
					String sectionId = Slist.stream().filter(f -> f.get("coverId").toString().equalsIgnoreCase(coverId))
							.map(z -> z.get("sectionId").toString()).findFirst().orElse("");
					log.info("Current Lopping SectionId :: "+ sectionId);
					for(int x=0;x<locationIds.size();x++) {
						String locationId = locationIds.get(x).toString();

						String locationName = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))).map(r -> r.get("locationName").toString()).findFirst().get();
						/*String productType = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))
								&& f.get("sectionId").equals(sectionId)).map(t -> t.get("productType")).map(Object::toString).findFirst().orElse("");*/
						List<BuildingRiskDetails> buildingRiskData = buildingRiskDetailsRepo
								.findByRequestReferenceNoAndSectionIdAndLocationIdAndCoverId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId),Integer.parseInt(coverId));
						List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
							LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
							lmap.put("locationName", k.getLocationName());
							lmap.put("buildingAddress", k.getAddress());
							lmap.put("sectionId", sectionId);
							lmap.put("sectiondesc", k.getSectionDesc());
							lmap.put("wallTypeDesc", k.getWallTypeDesc()==null?"":k.getWallTypeDesc());
							lmap.put("roofType", k.getRoofTypeDesc());
							lmap.put("firstlosspayee", k.getFirstLossPercent());
							lmap.put("coveringdetails", k.getCoveringDetails());
							lmap.put("descriptionofrisk", k.getDescriptionOfRisk());
							lmap.put("buildingSumInsured", k.getSumInsured());
							lmap.put("industrydesc", k.getIndustryDesc());
							lmap.put("buildingAge", k.getBuildingAge()==null?"":k.getBuildingAge() );
							lmap.put("wallType", k.getWallType());
							lmap.put("coverId", k.getCoverId()==null?"":k.getCoverId() );


							lmap.put("bondyear", k.getBondYear());
							lmap.put("sumInsured", k.getSumInsured());
							lmap.put("buildingSumInsured", k.getSumInsured());
							lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
							lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
									&& f.getSectionId()==Integer.parseInt(sectionId))
									.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
							//							lmap.put("moneyAnnualEstimate", k.getMoneyAnnualEstimate());
							//							lmap.put("moneyCollector", k.getMoneyCollector());
							//							lmap.put("moneyDirectorResidence", k.getMoneyDirectorResidence());
							//							lmap.put("moneyOutofSafe", k.getMoneyOutofSafe());
							//							lmap.put("moneySafeLimit", k.getMoneySafeLimit());
							//							lmap.put("moneyMajorLoss", k.getMoneyMajorLoss());
							lmap.put("indemityPeriodDesc", k.getIndemityPeriodDesc());
							lmap.put("categoryDesc", k.getCategoryDesc());
							lmap.put("contentDesc", k.getContentDesc());
							lmap.put("firstLossPercent", k.getFirstLossPercent());
							lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("coverId").equals(coverId) && f.get("coverNoteReferenceNo")!=null)
									.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
							return lmap;
						}).collect(Collectors.toList());



						if("105".equalsIgnoreCase(coverId)){ // Building
							buildingDetails.addAll(locationList);
						}else if("45".equalsIgnoreCase(coverId)){ // All Risk
							allriskDetails.addAll(locationList);
						}else if("290".equalsIgnoreCase(coverId)){ //Content
							contentDetails.addAll(locationList);
						}else if("90".equalsIgnoreCase(coverId)){ // Electronic Equipment
							electronicDetails.addAll(locationList);
						}else if("593".equalsIgnoreCase(coverId)) { //Owners liability
							ownersDetails.addAll(locationList);
						}

						//							List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationIdAndCoverId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId),Integer.parseInt(coverId));
						//							List<Map<String,Object>> commonList = comDetails.stream()
						//									.collect(Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(o -> {
						//										LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
						//										empMap.put("locationName", locationName);
						//										empMap.put("occupationDesc", o.getOccupationDesc());
						//										empMap.put("sumInsured", o.getSumInsured());
						//										empMap.put("count", o.getCount()==null?0:o.getCount());
						//										empMap.put("Rate", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
						//												&& f.getTaxId()!=0 && f.getSectionId()==Integer.parseInt(o.getSectionId())
						//												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getRate()).findAny().orElse(BigDecimal.ZERO));
						//										empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
						//												&& f.getSectionId()==Integer.parseInt(sectionId)
						//												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
						//										return empMap;
						//									}, Collectors.toList()))).entrySet()
						//									.stream().map(g -> {
						//										LinkedHashMap<String,Object> eMap = new LinkedHashMap<String,Object>();
						//										eMap.put("occupationDesc", g.getValue().stream().map(t -> String.valueOf(t.get("occupationDesc"))).collect(Collectors.joining("<br>")));
						//										eMap.put("Rate", g.getValue().stream().map(t -> t.get("Rate")).findFirst().get());
						//										eMap.put("count", g.getValue().stream().map(t -> t.get("count")).findFirst().get());
						//										eMap.put("sumInsured", g.getValue().stream().map(j -> (BigDecimal) j.get("sumInsured")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
						//										eMap.put("premium", g.getValue().stream().map(h -> h.get("Premium")).findFirst().get());
						//										eMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
						//												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
						//										eMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
						////										eMap.put("wallTypeDesc", map.get("wallTypeDesc")==null?"":map.get("wallTypeDesc").toString());
						////										eMap.put("buildingAge", map.get("buildingAge")==null?"":map.get("buildingAge").toString()  );
						////										eMap.put("wallType", map.get("wallType")==null?"":map.get("wallType").toString() );
						////										eMap.put("coverId", map.get("coverId")==null?"":map.get("coverId").toString()  );
						//										
						//										eMap.put("locationName", g.getValue().stream().map(j -> j.get("locationName")).findFirst().get());
						//										return eMap;
						//									}).collect(Collectors.toList());
						//							ownersDetails.addAll(commonList);
						else if("13".equalsIgnoreCase(map.get("productId")==null?"":map.get("productId").toString())){
							List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
							if(comDetails !=null && comDetails.size()>0) {
								comDetails.forEach(us -> {
									Map<String,Object> empMap = new HashMap<String,Object>();
									empMap.put("locationName", locationName);
									empMap.put("Quoteoccupation", us.getCategoryDesc());
									empMap.put("sumInsured", us.getSumInsured());
									empMap.put("customerName", us.getNickName());
									empMap.put("dateOfBrith", sdf.format(us.getDob()));
									empMap.put("options", us.getSectionDesc());
									empMap.put("estAnnualEarnings", us.getOtherOccupation());
									empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId)
											&& f.getVehicleId()==us.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									empMap.put("coverLimit", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId) && f.getCoverageType().equalsIgnoreCase("B")
											&& f.getVehicleId()==us.getRiskId()).map(u -> u.getCoverageLimit()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									empMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									personalAccDetails.add(empMap);
								});	


							}
						}

						else if ("246".equalsIgnoreCase(sectionId)) { // Member Section

							Map<String, Object> finalResponse = new LinkedHashMap<>();

							List<ListItemValue> memberList = ewayListItemValueRepo
									.findByItemTypeAndItemCodeOrderByItemCodeAsc("Members", sectionId);

							List<Map<String, Object>> memberDetails = new ArrayList<>();

							String categoryDes = ""; 
							if (memberList != null && !memberList.isEmpty()) {

								Optional<InsuranceCompanyMaster> companyOpt = ewayInsuranceCompanyMasterRepo
										.findTopByCompanyIdOrderByAmendIdAsc(companyId);  

								String companyWebsite = companyOpt.isPresent() && companyOpt.get().getCompanyWebsite() != null
										? companyOpt.get().getCompanyWebsite()
												: "";

								memberDetails = memberList.stream().map(val -> {
									Map<String, Object> m = new LinkedHashMap<>();


									//							        BuildingRiskDetails matchedBuilding = buildingRiskDetailsRepo.findByQuoteNo(QuoteNo);
									//							        	    
									//							        if (matchedBuilding.isPresent()) {
									//							            BuildingRiskDetails brDetails = matchedBuilding.get();
									////							            m.put("MembersValue", brDetails.getMoneyCollector() != null ? brDetails.getMoneyCollector() : "");
									////							            m.put("occupation", brDetails.getCategoryDesc() != null ? brDetails.getCategoryDesc() : "");
									//							            String categoryDesc = brDetails.getCategoryDesc() != null ? brDetails.getCategoryDesc() : "";
									//
									//							            m.put("occupation", categoryDesc);
									//							          
									//							            
									//							            
									//							        } else {
									//							            m.put("MembersValue", "");
									//							            m.put("occupation", "");
									//							        }

									m.put("interestsInsured", val.getItemValue());
									m.put("locationName", locationName);
									m.put("sectionId", sectionId); 
									m.put("companyWebsite", companyWebsite);

									return m;
								}).collect(Collectors.toList());
							}
							HomePositionMaster home1=homeRepo.findByQuoteNo(QuoteNo);
							BuildingRiskDetails matchedBuild = buildingRiskDetailsRepo.findFirstByQuoteNo(QuoteNo);

							if (matchedBuild != null) {
								categoryDes = Optional.ofNullable(matchedBuild.getCategoryDesc()).orElse("");
								String categoryId = Optional.ofNullable(matchedBuild.getCategoryId()).orElse("");

								 if ("P".equalsIgnoreCase(home1.getStatus()) && attachments.isEmpty()) {
									String attachmentType = "MISCELLANEOUS";
									if ("1".equalsIgnoreCase(categoryId)) attachmentType = "ADVOCATES";
									else if ("3".equalsIgnoreCase(categoryId)) attachmentType = "ENGINEERING";
									else if ("2".equalsIgnoreCase(categoryId)) attachmentType = "ACCOUNTANTS";
									
									   

									attachments.addAll(getAttachMentList(companyId, matchedBuild.getProductId(), attachmentType, null));
									 
								}
								result.put("attachMents", attachments);
								
								Map<String, Object> occupationMap = new LinkedHashMap<>();
								occupationMap.put("occupation1", categoryDes);
								result.put("occupationDetails", occupationMap);
							}

							List<BuildingRiskDetails> mem = buildingRiskDetailsRepo
									.findByQuoteNoAndCoverId(QuoteNo, 632);

							List<Map<String, Object>> membersList = new ArrayList<>();

							if (mem != null && !mem.isEmpty()) {

								for (BuildingRiskDetails item : mem) {

									Map<String, Object> membersMap = new LinkedHashMap<>();

									//							        String categoryDesc = Optional.ofNullable(item.getCategoryDesc()).orElse("");
									//							        String categoryId   = Optional.ofNullable(item.getCategoryId()).orElse("");

									String contentId = Optional.ofNullable(item.getContentId()).orElse("");
									String contentDesc = Optional.ofNullable(item.getContentDesc()).orElse("");
									Integer coverId1 = Optional.ofNullable(item.getCoverId()).orElse(0);
									//							        String sectionId = Optional.ofNullable(item.getSectionId()).orElse("");
									//							        String companyId = Optional.ofNullable(item.getCompanyId()).orElse("");
									String productId = Optional.ofNullable(item.getProductId()).orElse("");
									String wallType = Optional.ofNullable(item.getWallType()).orElse("");
									String wallTypeDesc = Optional.ofNullable(item.getWallTypeDesc()).orElse("");
									Integer buildingFloors = Optional.ofNullable(item.getBuildingFloors()).orElse(0);
									BigDecimal sumInsured = Optional.ofNullable(item.getSumInsured()).orElse(BigDecimal.ZERO);
									String indemityPeriod = Optional.ofNullable(item.getIndemityPeriod()).orElse("");

									//							        membersMap.put("categoryDesc", categoryDesc);
									//							        membersMap.put("categoryId", categoryId);


									membersMap.put("contentId", contentId);
									membersMap.put("contentDesc", contentDesc);
									membersMap.put("coverId", coverId1);
									membersMap.put("sectionId", sectionId);
									membersMap.put("companyId", companyId);
									membersMap.put("productId", productId);
									membersMap.put("wallType", wallType);
									membersMap.put("wallTypeDesc", wallTypeDesc);
									membersMap.put("buildingFloors", buildingFloors);
									membersMap.put("sumInsured", sumInsured);
									membersMap.put("indemityPeriod", indemityPeriod);

									membersList.add(membersMap);
								}
							}

							result.put("membersList", membersList);

							BuildingRiskDetails mem1 = buildingRiskDetailsRepo
									.findFirstByQuoteNoAndCoverId(QuoteNo, 632);
							BigDecimal sumInsured = BigDecimal.ZERO;
							String indemityPeriod = "";
							String wallType="";
							String wallTypeDesc="";
							String indemityPeriodDesc="";
							if(mem1 != null) {
								sumInsured = Optional.ofNullable(mem1.getSumInsured()).orElse(BigDecimal.ZERO);
								indemityPeriod = Optional.ofNullable(mem1.getIndemityPeriod()).orElse("");
								wallType = Optional.ofNullable(mem1.getWallType()).orElse("");
								wallTypeDesc = Optional.ofNullable(mem1.getWallTypeDesc()).orElse("");
							    //indemityPeriodDesc = (String)Optional.ofNullable(mem1.getIndemityPeriodDesc()).orElse("");
							}
							List<BuildingRiskDetails> memList = buildingRiskDetailsRepo
							        .findByQuoteNoAndCoverId(QuoteNo, 631);  
							if (memList != null && !memList.isEmpty()) {
							    BuildingRiskDetails firstMem = memList.get(0);  
							    indemityPeriodDesc = Optional.ofNullable(firstMem.getIndemityPeriodDesc()).orElse("");
							}
							result.put("GrossIncome",sumInsured);
							result.put("limitIndemity",indemityPeriod);
							result.put("PerLimitOfIndemity",wallType);
							result.put("PerLimitOfIndemityDesc",wallTypeDesc);
							result.put("indemityPeriodDesc", indemityPeriodDesc);

							finalResponse.put("memberSection", memberDetails);


							HomePositionMaster homeData = homeRepo.findByQuoteNo(QuoteNo);
							List<ExcessMaster> excessList = excessRepo
									.findByProductIdAndCompanyIdAndSectionId(
											homeData.getProductId().toString(), homeData.getCompanyId(), sectionId);

							excessDetails = excessList.stream().map(e -> {
								Map<String, Object> m = new LinkedHashMap<>();
								m.put("coverName", e.getCoverName());
								m.put("excessPercentage", e.getExcessPercentage());
								return m;
							}).collect(Collectors.toList());

							result.put("excessDetails", excessDetails);


							List<ExclusionMaster> exclusionList =  exclusionMasterRepo
									.findExclusionByProductIdAndCompanyIdAndSectionId(
											homeData.getProductId().toString(), homeData.getCompanyId(), sectionId);

							exclusionDetails = exclusionList.stream().map(ex -> {
								Map<String, Object>  m = new LinkedHashMap<>();
								m.put("exclusionDescription", ex.getExclusionDescription());
								return  m;
							}).collect(Collectors.toList());

							result.put("exclusionDetails", exclusionDetails);


							List<ClausesMaster> clauseList = clausesMasterRepo
									.findClausesByProductIdAndCompanyIdAndSectionId(
											homeData.getProductId().toString(), homeData.getCompanyId().toString(), sectionId);

							clauseDetails = clauseList.stream().map(c -> {
								Map<String, Object>  m = new LinkedHashMap<>();
								m.put("clausesDescription", c.getClausesDescription());
								return m;
							}).collect(Collectors.toList());

							result.put("clauseDetails", clauseDetails);


							List<PolicyCoverData> coverList = coverDataRepository.findByQuoteNoAndCoverageTypeAndVehicleId(
									QuoteNo,"T", 99999);

							List<Map<String, Object>> policyCoverDetails = coverList.stream().map(p -> {
								Map<String, Object> m = new LinkedHashMap<>();

								m.put("coverName", p.getCoverName());						    
								m.put("taxAmount", p.getTaxAmount());

								//						         if(p.getCoverageType().equals("T")) {
								//						           m.put("coverName", p.getCoverName());
								//						      m.put("taxAmount", p.getTaxAmount());
								//						         }
								//						        

								return m;
							}).collect(Collectors.toList());

							result.put("policyCoverData", policyCoverDetails);


							Map<String, Object> premiumDetails = new LinkedHashMap<>();
							if (homeData != null) {
								premiumDetails.put("premiumLc", homeData.getPremiumLc());
								premiumDetails.put("renewalDate", homeData.getEffectiveDate());
							} else {
								premiumDetails.put("premiumLc", null);
								premiumDetails.put("renewalDate", null);
							} 
					//						    log.info("Member Section (--) Data => " + finalResponse);
					//						    return ResponseEntity.ok(finalResponse);
						}else if(Arrays.asList("258","256").contains(sectionId)) {
							List<Map<String,Object>> bondList = buildingRiskData.stream().map(k ->{
								LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
								lmap.put("locationName", k.getLocationName());
								lmap.put("buildingAddress", k.getAddress());
								lmap.put("sectionId", sectionId);
								lmap.put("sectiondesc", k.getSectionDesc());
								lmap.put("industrydesc", k.getIndustryDesc());
								lmap.put("bondyear", k.getBondYear());
								lmap.put("bondSumInsured", k.getSumInsured());
								lmap.put("coveringdetails", k.getCoveringDetails());
								lmap.put("descriptionofrisk", k.getDescriptionOfRisk());
								lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
								lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
										&& f.getSectionId()==Integer.parseInt(sectionId))
										.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
								lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("coverId").equals(coverId) && f.get("coverNoteReferenceNo")!=null)
										.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
								return lmap;
							}).collect(Collectors.toList());
							
							bondDetails.addAll(bondList);
						}
						
						// CONDITIONS
						List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						// EXCLUSION
						List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
						List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
							Map<String,Object> eMap = new HashMap<String,Object>();
							eMap.put("conditionTerms", k.get("exclusioTerms"));
							eMap.put("SectionId", k.get("SectionId"));
							return eMap;
						}).collect(Collectors.toList());

						//WARRANTY
						List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList,conditionList,exclusionList).flatMap(Collection::stream)
								.sorted(Comparator.comparing(p -> {
									if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
										return Integer.MAX_VALUE;
									}
									try {
										return Integer.parseInt(p.get("Sno").toString());
									} catch (NumberFormatException e) {
										return Integer.MAX_VALUE;
									}
								}))
								.map(u -> {
									LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
									m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
									return m;
								}).distinct().collect(Collectors.toList());

						int conditionsize = termsAndconditions.size();
						List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
						if(conditionsize>10) {
							int midIndex = conditionsize / 2;
							firstHalf = termsAndconditions.subList(0, midIndex);
							secondHalf = termsAndconditions.subList(midIndex, conditionsize);
						}else {
							firstHalf = termsAndconditions.subList(0, conditionsize);
						}

						//						List<PolicyCoverData> excessCon = coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getCoverageType().equalsIgnoreCase("B") && f.getSectionId()==Integer.parseInt(sectionId)).distinct().collect(Collectors.toList());
						//						if(!excessCon.isEmpty()) {
						//							PolicyCoverData cpd = excessCon.get(0);
						//							Map<String,Object> excessMap = new HashMap<String,Object>();
						//							excessMap.put("excessPercent", cpd.getExcessPercent());
						//							excessMap.put("excessAmount", cpd.getExcessAmount());
						//							excessMap.put("excessDesc", cpd.getExcessDesc());
						//							excessMap.put("currency", cpd.getCurrency());
						//							excessConDetails.add(excessMap);
						//						}
						String productId=map.get("productId").toString();
						List<ExcessMaster> excess1=excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(companyId,productId ,sectionId, coverId);	
						if(!excess1.isEmpty() ) {
							ExcessMaster cpd = excess1.get(0);
							Map<String,Object> excessMap = new HashMap<String,Object>();
							excessMap.put("excessPercent", cpd.getExcessPercentage()==null?0:cpd.getExcessPercentage());
							excessMap.put("excessAmount", cpd.getExcessAmount()==null?0.0:cpd.getExcessAmount());
							excessMap.put("excessDesc", cpd.getExcessDescription()==null?"":cpd.getExcessDescription());
							excessMap.put("currency", cpd.getCurrency()==null?"":cpd.getCurrency());
							excessConDetails.add(excessMap);
						}


						coverMap.put("sectionDesc", Slist.stream().filter(k -> sectionId.equalsIgnoreCase(k.get("sectionId").toString())).map(e -> e.get("sectionDesc").toString()).findFirst().orElse(""));
						coverMap.put("coverDesc", Slist.stream().filter(k -> coverId.equalsIgnoreCase(k.get("coverId").toString())).map(e -> e.get("coverDesc").toString()).findFirst().orElse(""));
						coverMap.put("buildingDetails", buildingDetails);
						coverMap.put("allriskDetails", allriskDetails);
						coverMap.put("contentDetails", contentDetails);
						//						coverMap.put("domesticDetails", domesticDetails);
						coverMap.put("electronicDetails",electronicDetails);
						coverMap.put("ownersDetails", ownersDetails);
						coverMap.put("firstHalfconditions", firstHalf);
						coverMap.put("secondHalfconditions", secondHalf);
						coverMap.put("excessConditions", excessConDetails);
						coverMap.put("personalAccDetails", personalAccDetails);
						coverMap.put("clauseDetails", clauseDetails);
						coverMap.put("excessDetails", excessDetails);
						coverMap.put("exclusionDetails", exclusionDetails);
						coverMap.put("bondDetails", bondDetails);
						coverMap.put("sectionId", sectionId);
						coverageList.add(coverMap);
					}
				}
				
				List<PolicyCoverData> coverList =
				        coverDataRepository.findByQuoteNoAndVehicleId(QuoteNo, 99999);

				
				List<Map<String, Object>> policyCoverDetails = new ArrayList<>();
				
				coverList.stream()
				        .filter(c -> "B".equalsIgnoreCase(c.getCoverageType())
				                || "O".equalsIgnoreCase(c.getCoverageType()))
				        .forEach(c -> {
				            Map<String, Object> entry = new LinkedHashMap<>();
				            entry.put("coverName", c.getCoverName()); // Cover Name

				            BigDecimal premium = c.getPremiumExcludedTaxLc() == null
				                    ? BigDecimal.ZERO
				                    : c.getPremiumExcludedTaxLc();

				            entry.put("taxAmount", premium.toPlainString());
				            policyCoverDetails.add(entry);
				        });


				
				Map<Integer, BigDecimal> taxSummary =
				        coverList.stream()
				                .filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
				                .filter(c -> c.getTaxId() != null)
				                .collect(Collectors.groupingBy(
				                        PolicyCoverData::getTaxId,
				                        LinkedHashMap::new,
				                        Collectors.reducing(
				                                BigDecimal.ZERO,
				                                c -> c.getTaxAmountLc() == null
				                                        ? BigDecimal.ZERO
				                                        : c.getTaxAmountLc(),
				                                BigDecimal::add
				                        )
				                ));


				
				taxSummary.forEach((taxId, totalTax) -> {

				    
				    PolicyCoverData anyTaxRow = coverList.stream()
				            .filter(c -> taxId.equals(c.getTaxId()))
				            .findFirst()
				            .orElse(null);

				    if (anyTaxRow != null) {
				        Map<String, Object> entry = new LinkedHashMap<>();
				        entry.put("coverName", anyTaxRow.getTaxDesc());                
				        entry.put("taxAmount", totalTax.toPlainString());          
				        policyCoverDetails.add(entry);
				    }
				});


				result.put("policyCoverData", policyCoverDetails);
				
				String sectionId = sectionIds.get(0).toString();
				
				// CONDITIONS
				List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

				// EXCLUSION
				List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
				List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
					Map<String,Object> eMap = new HashMap<String,Object>();
					eMap.put("conditionTerms", k.get("exclusioTerms"));
					eMap.put("SectionId", k.get("SectionId"));
					return eMap;
				}).collect(Collectors.toList());

				//WARRANTY
				List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

				List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList,conditionList,exclusionList).flatMap(Collection::stream)
						.sorted(Comparator.comparing(p -> {
							if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
								return Integer.MAX_VALUE;
							}
							try {
								return Integer.parseInt(p.get("Sno").toString());
							} catch (NumberFormatException e) {
								return Integer.MAX_VALUE;
							}
						}))
						.map(u -> {
							LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
							m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
							return m;
						}).distinct().collect(Collectors.toList());

				int conditionsize = termsAndconditions.size();
				List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
				if(conditionsize>10) {
					int midIndex = conditionsize / 2;
					firstHalf = termsAndconditions.subList(0, midIndex);
					secondHalf = termsAndconditions.subList(midIndex, conditionsize);
				}else {
					firstHalf = termsAndconditions.subList(0, conditionsize);
				}
				
				
				result.put("Commonconditions", firstHalf);


//				List<PolicyCoverData> coverList =
//				        coverDataRepository.findByQuoteNoAndVehicleId(QuoteNo, 99999);
//
//				List<Map<String, Object>> policyCoverDetails = coverList.stream().map(p -> {
//					Map<String, Object> m = new LinkedHashMap<>();
//
//					m.put("coverName", p.getCoverName());						    
//					m.put("taxAmount", p.getTaxAmount());
//
//					//			         if(p.getCoverageType().equals("T")) {
//					//			           m.put("coverName", p.getCoverName());
//					//			      m.put("taxAmount", p.getTaxAmount());
//					//			         }
//					//			        
//
//					return m;
//				}).collect(Collectors.toList());
//
//				result.put("policyCoverData", policyCoverDetails);
				Map<Object,List<Map<String,Object>>> groupBycoverageDetails = coverageList.stream()
						.collect(Collectors.groupingBy(k -> k.get("coverDesc"), Collectors.toList()));
				for(Map.Entry<Object, List<Map<String,Object>>> CDEntry : groupBycoverageDetails.entrySet()) {
					LinkedHashMap<String, Object> coverMap = new LinkedHashMap<String, Object>();
					coverMap.put("coverId", Slist.stream().filter(f -> f.get("coverDesc").equals(CDEntry.getKey())).map(m -> m.get("coverId")).findFirst().orElse(""));
					coverMap.put("coverKey", CDEntry.getValue().stream()
							.filter(e -> Arrays.asList(108, 109, 114, 115, 33, 111).contains(Integer.parseInt(e.get("sectionId").toString())))
							.map(e -> "BUSINESS INTERRUPTION (" + CDEntry.getKey().toString() + ")".toUpperCase()+ " " +(map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE"))
							.findFirst()
							.orElse(CDEntry.getKey().toString().toUpperCase() + " " + (map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE")));
					coverMap.put("coverValue", CDEntry.getValue());//CDEntry.getValue()
					coverMap.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
					coverMap.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
					coverMap.put("productId", map.get("productId")==null?"":map.get("productId").toString());
					coverMap.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
					coverMap.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
					coverMap.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
					coverageDetails.add(coverMap);
				}

				List<EserviceBuildingDetails> buildingDtl = eserviceBuildingDetailsRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(QuoteNo, "Y");
				String buildingOwnerYn = buildingDtl.isEmpty()?"":buildingDtl.get(0).getBuildingOwnerYn()==null?"":buildingDtl.get(0).getBuildingOwnerYn();
				List<Map<String,Object>> domesticKeyFactor = listItemValueRepo.getDomesticKeyFactor(buildingOwnerYn.equalsIgnoreCase("Y")?"1":"2",companyId);
				if(!domesticKeyFactor.isEmpty()) {
					String attachmentloc = this.getClass().getClassLoader().getResource("").getPath().replaceAll("%20", "")+"report/attachments/";
					domesticKeyFactor.forEach(k->{
						AttachMentRes a = AttachMentRes.builder()
								.docRefNo(k.get("ITEM_CODE")==null?"":k.get("ITEM_CODE").toString())
								.docloction(k.get("ITEM_VALUE")==null?"":(attachmentloc+k.get("ITEM_VALUE").toString()))
								.build();
						attachments.add(a);
					});
				}

				List<Map<String,Object>> firstLossPayeesList = new ArrayList<Map<String,Object>>();
				List<FirstLossPayee> firstLossPayees = firstLossPayeeRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
				if(!firstLossPayees.isEmpty()) {
					firstLossPayees.forEach(k -> {
						Map<String,Object> custMap = new HashMap<String,Object>();
						custMap.put("firstLossPayee", k.getFirstLossPayeeDesc());
						firstLossPayeesList.add(custMap);
					});
				}

				if(Arrays.asList("100046","100047","100048","100049","100050").contains(map.get("companyId")==null?"":map.get("companyId").toString()) || "100020".equalsIgnoreCase(companyId)) {
					LinkedList<Map<String,Object>> secdetails_f = new LinkedList<Map<String,Object>>();
					Map<Object, List<Tuple>> sectionDetails = Slist.stream().collect(Collectors.groupingBy(k -> k.get("locationName"), Collectors.toList()));
					for(Map.Entry<Object, List<Tuple>> secEntry : sectionDetails.entrySet()) {
						LinkedHashMap<String, Object> sec_map = new LinkedHashMap<String, Object>();
						sec_map.put("locationName", capitalizeFirstLetter(secEntry.getKey()==null?"":secEntry.getKey().toString()));
						Tuple tuple = secEntry.getValue().get(0);
						Integer location = tuple.get("locationId", Integer.class);
						sec_map.put("locationId", location);
						String address="";
						List<BuildingRiskDetails> filteredBuildList = buildList.stream()
						        .filter(b -> b.getLocationId() != null
						                && b.getLocationId().equals(location))
						        .collect(Collectors.toList());
						if(!filteredBuildList.isEmpty() && filteredBuildList!=null)
						{
							address = filteredBuildList.get(0).getAddress();
						}
						else
						{
							List<CommonDataDetails> collect = commonList.stream()
					        .filter(b -> b.getLocationId() != null
					                && b.getLocationId().equals(location))
					        .collect(Collectors.toList());
							if(!collect.isEmpty() && collect!=null)
							{
								address = collect.get(0).getAddress();
							}
						}
						sec_map.put("locationAddress", address);
						
						Map<Object, List<Tuple>> k = secEntry.getValue().stream()
								.collect(Collectors.groupingBy(j -> j.get("sectionDesc"), Collectors.toList()));
						LinkedList<Map<String,Object>> secdetails = new LinkedList<Map<String,Object>>();
						for(Map.Entry<Object, List<Tuple>> t : k.entrySet()) {
							LinkedList<Map<String,Object>> sec_list = new LinkedList<Map<String,Object>>();
							Map<String,Object> f = new HashMap<String,Object>();
							f.put("SectionName", t.getKey()==null?"":capitalizeFirstLetter(t.getKey().toString()));
							Map<String,Object> sectionConditions = new HashMap<>();
							
							if (coverageList != null && t != null && t.getKey() != null) {
							    for (Map<String, Object> coverage : coverageList) {
							        if (coverage != null) {
							            Object sectionDesc = coverage.get("sectionDesc");
							            if (sectionDesc != null && sectionDesc.toString().equals(t.getKey().toString())) {
							                sectionConditions.put("firstHalfconditions", coverage.get("firstHalfconditions"));
							                sectionConditions.put("secondHalfconditions", coverage.get("secondHalfconditions"));
							                break;
							            }
							        }
							    }
							}
							if (sectionConditions.isEmpty()) {
							    sectionConditions = Collections.emptyMap();
							}
							List<Tuple> value = t.getValue();
							Integer valueOfsec = Integer.valueOf(value.get(0).get("sectionId").toString());
							Integer valueOfloc = Integer.valueOf(value.get(0).get("locationId").toString());
							LinkedList<Map<String,Object>> addcoverdetails = new LinkedList<Map<String,Object>>();
							for(int i =0;i<t.getValue().size();i++) {
								DecimalFormat df = new DecimalFormat("#,##0.00");
								
								LinkedList<Map<String,Object>> excessdetails = new LinkedList<Map<String,Object>>();
								
								Tuple o = t.getValue().get(i);
								Integer coverId = o.get("coverId") == null ? null : Integer.valueOf(o.get("coverId").toString());
								Integer sectio = o.get("sectionId") == null ? null : Integer.valueOf(o.get("sectionId").toString());
								Integer locationId = o.get("locationId") == null ? null : Integer.valueOf(o.get("locationId").toString());
								BigDecimal vatAmount = coverData.stream()
								        .filter(c -> c.getCoverId() != null
								                && c.getCoverId().equals(coverId)
								                && c.getSectionId() != null
								                && c.getSectionId().equals(sectio)
								                && c.getLocationId() != null
								                && c.getLocationId().equals(locationId)
								                && c.getTaxId() != null
								                && c.getTaxId().equals(41)
								                && "T".equalsIgnoreCase(c.getCoverageType()))
								        .map(c -> c.getTaxAmount() == null ? BigDecimal.ZERO : c.getTaxAmount())
								        .reduce(BigDecimal.ZERO, BigDecimal::add);

								BigDecimal annualPremium = o.get("premiumExcludedTaxFc") == null
										? BigDecimal.ZERO
												: new BigDecimal(o.get("premiumExcludedTaxFc").toString());

								BigDecimal annualWithVat = annualPremium.add(vatAmount);

								BigDecimal monthlyWithVat = annualWithVat.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
								LinkedHashMap<String,Object> s = new LinkedHashMap<String,Object>();
								s.put("covername", o.get("coverDesc")==null?"":capitalizeFirstLetter(o.get("coverDesc").toString()));
								s.put("suminsured", o.get("sumInsured")==null?0.00:Double.parseDouble(o.get("sumInsured").toString()));
						//		s.put("annually", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString()));
						//		s.put("monthly", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString())/12);
								s.put("annually", df.format(annualWithVat));      // String with commas
								s.put("monthly", df.format(monthlyWithVat));      // String with commas

								List<ExcessMaster> excessList = excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
										map.get("companyId")==null?"":map.get("companyId").toString(),map.get("productId")==null?"":map.get("productId").toString(),
														o.get("sectionId")==null?"":o.get("sectionId").toString(),o.get("coverId")==null?"":o.get("coverId").toString());

								if(excessList!=null && excessList.size()>0) {
									excessList.forEach(e_l -> {
										Map<String,Object> e = new HashMap<String,Object>();
										e.put("excessdesc", e_l.getExcessDescription()==null?"":e_l.getExcessDescription());
										e.put("excessamount", e_l.getExcessAmount()==null?0.00:e_l.getExcessAmount());
										e.put("excessper", e_l.getExcessPercentage()==null?0:e_l.getExcessPercentage());
										e.put("excessName", e_l.getCoverName()==null?"":e_l.getCoverName());
										excessdetails.add(e);
									});
								}

//								List<PolicyCoverData> additionalCover = coverData.stream()
//										.filter(u -> "A".equalsIgnoreCase(u.getCoverageType())
//												&& u.getSectionId() == Integer.parseInt(o.get("sectionId").toString()))
//										.collect(Collectors.toList());
//								if(additionalCover!=null && additionalCover.size()>0) {
//									additionalCover.forEach(p -> {
//										Map<String,Object> e = new HashMap<String,Object>();
//										e.put("covername", p.getCoverDesc()==null?"":capitalizeFirstLetter(p.getCoverDesc().toString()));
//										e.put("coverlimit", p.getCoverageLimit()==null?BigDecimal.ZERO:p.getCoverageLimit());
//										addcoverdetails.add(e);
//									});
//								}
//								s.put("addcoverdetails", addcoverdetails);
								s.put("excessdetails", excessdetails);
								sec_list.add(s);
							}

							List<PolicyCoverData> additionalCover = coverData.stream()
									.filter(u -> "A".equalsIgnoreCase(u.getCoverageType())
											&& u.getSectionId().equals(valueOfsec) && u.getLocationId().equals(valueOfloc))									.collect(Collectors.toList());
							if(additionalCover!=null && additionalCover.size()>0) {
								additionalCover.forEach(p -> {
									Map<String,Object> e = new HashMap<String,Object>();
									e.put("covername", p.getCoverDesc()==null?"":capitalizeFirstLetter(p.getCoverDesc().toString()));
									e.put("coverlimit", p.getCoverageLimit()==null?BigDecimal.ZERO:p.getCoverageLimit());
									addcoverdetails.add(e);
								});
							}
							f.put("addcoverdetails", addcoverdetails);

							sec_list.sort(Comparator.comparing(o -> (String) o.get("covername")));
							f.put("sectionList", sec_list);
							f.put("firstHalfconditions", sectionConditions.get("firstHalfconditions"));
							f.put("secondHalfconditions", sectionConditions.get("secondHalfconditions"));
							secdetails.add(f);
						}
						sec_map.put("sectionDetails", secdetails);
						secdetails_f.add(sec_map);
					}

					result.put("sectionDetails", secdetails_f);

					LinkedList<Map<String,Object>> sec_list = new LinkedList<Map<String,Object>>();
					for(Map.Entry<Object, List<Tuple>> secEntry : sectionDetails.entrySet()) {
						LinkedHashMap<String, Object> sec_map = new LinkedHashMap<String, Object>();
						sec_map.put("locationName", secEntry.getKey());
						Tuple tuple = secEntry.getValue().get(0);
						Integer location = tuple.get("locationId", Integer.class);
						sec_map.put("locationId", location);
						String address="";
						List<BuildingRiskDetails> filteredBuildList = buildList.stream()
						        .filter(b -> b.getLocationId() != null
						                && b.getLocationId().equals(location))
						        .collect(Collectors.toList());
						if(!filteredBuildList.isEmpty() && filteredBuildList!=null)
						{
							address = filteredBuildList.get(0).getAddress();
						}
						else
						{
							List<CommonDataDetails> collect = commonList.stream()
					        .filter(b -> b.getLocationId() != null
					                && b.getLocationId().equals(location))
					        .collect(Collectors.toList());
							if(!collect.isEmpty() && collect!=null)
							{
								address = collect.get(0).getAddress();
							}
						}
						sec_map.put("locationAddress", address);
						
						
						Map<Object, List<Tuple>> k = secEntry.getValue().stream().collect(Collectors.groupingBy(j -> j.get("sectionDesc"), Collectors.toList()));
						List<Map<String,Object>> j_list = new ArrayList<Map<String,Object>>();
						for(Map.Entry<Object, List<Tuple>> t : k.entrySet()) {
							Map<String,Object> j = new HashMap<String,Object>();
							j.put("sectionDesc", t.getKey()==null?"":capitalizeFirstLetter(t.getKey().toString()));
//							Double premium_section = t.getValue().stream().map(w -> (BigDecimal) w.get("premiumExcludedTaxFc"))
//									.collect(Collectors.summingDouble(BigDecimal::doubleValue));
							Double suminsured_section = t.getValue().stream().map(w -> (BigDecimal) w.get("sumInsured"))
									.collect(Collectors.summingDouble(BigDecimal::doubleValue));
							Integer secti = Integer.valueOf(t.getValue().get(0).get("sectionId").toString());
							
							BigDecimal premium_sect = coverData.stream()
							        .filter(c -> c.getLocationId() != null && c.getLocationId().equals(location))
							        .filter(c -> c.getSectionId() != null && c.getSectionId().equals(secti))
							        .filter(c -> !"T".equalsIgnoreCase(c.getCoverageType()))
							       // .filter(c -> c.getTaxId() != null && c.getTaxId().equals(41))
							        .map(c -> c.getPremiumExcludedTaxLc() == null ? BigDecimal.ZERO : c.getPremiumExcludedTaxLc())
							        .reduce(BigDecimal.ZERO, BigDecimal::add);
							
							
							BigDecimal vatAmount = coverData.stream()
							        .filter(c -> c.getLocationId() != null && c.getLocationId().equals(location))
							        .filter(c -> c.getSectionId() != null && c.getSectionId().equals(secti))
							        .filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
							        .filter(c -> c.getTaxId() != null && c.getTaxId().equals(41))
							        .map(c -> c.getTaxAmount() == null ? BigDecimal.ZERO : c.getTaxAmount())
							        .reduce(BigDecimal.ZERO, BigDecimal::add);
							Double premium_section = premium_sect.doubleValue();
							premium_section += vatAmount.doubleValue();
							
							j.put("suminsured", suminsured_section);
							j.put("annually", premium_section);
							j.put("monthly", premium_section/12);
							
						//	j.put("SectionPremiumList",getTaxSummaryListSection(QuoteNo,Integer.valueOf(t.getValue().get(0).get("sectionId").toString()),Integer.valueOf(t.getValue().get(0).get("locationId").toString())));
							j_list.add(j);
						}
						j_list.sort(Comparator.comparing(o -> (String) o.get("sectionDesc")));
						sec_map.put("sectionList", j_list);
						sec_list.add(sec_map);
					}

					result.put("sectionList", sec_list);

					result.put("phoenixVatPercent", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxRate()).findAny().orElse(BigDecimal.ZERO));
					result.put("phoenixVatAmount", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
				}

				List<Map<String,Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(map.get("companyId")==null?"":map.get("companyId").toString());
				if(!companyDetails.isEmpty()) {
					result.put("companyName", companyDetails.get(0).get("COMPANY_NAME")==null?"":companyDetails.get(0).get("COMPANY_NAME").toString());
					result.put("companylogo", companyDetails.get(0).get("COMPANY_LOGO")==null?"":companyDetails.get(0).get("COMPANY_LOGO").toString());
					result.put("companyWebsite", companyDetails.get(0).get("COMPANY_WEBSITE")==null?"":companyDetails.get(0).get("COMPANY_WEBSITE").toString());
					result.put("companyMail", companyDetails.get(0).get("COMPANY_EMAIL")==null?"":companyDetails.get(0).get("COMPANY_EMAIL").toString());
					result.put("companyPhone", companyDetails.get(0).get("COMPANY_PHONE")==null?"":companyDetails.get(0).get("COMPANY_PHONE").toString());
					result.put("companyAddress", companyDetails.get(0).get("COMPANY_ADDRESS")==null?"":companyDetails.get(0).get("COMPANY_ADDRESS").toString());
					result.put("companyPoBox", companyDetails.get(0).get("PO_BOX")==null?"":companyDetails.get(0).get("PO_BOX").toString());
					result.put("companyVrnNumber", companyDetails.get(0).get("VRN_NUMBER")==null?"":companyDetails.get(0).get("VRN_NUMBER").toString());
					result.put("companyremarks", companyDetails.get(0).get("REMARKS")==null?"":companyDetails.get(0).get("REMARKS").toString());
				}

				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("branchCode", map.get("branchCode")==null?"":map.get("branchCode").toString());
				result.put("agencyCode", map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("address", map.get("address1") == null ? "" : map.get("address1").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				result.put("entryDate", map.get("entryDate")==null?"":map.get("entryDate").toString());
				result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
				result.put("brokerBranchName", map.get("brokerBranchName")==null?"":map.get("brokerBranchName").toString());
				result.put("productName", map.get("productName")==null?"":map.get("productName").toString().toUpperCase()+" "+(map.get("policyNo")==null?"\nQuote Schedule":"\nPolicy Schedule"));
				result.put("stateName", map.get("stateName")==null?"":map.get("stateName").toString());
				result.put("cityName", map.get("cityName")==null?"":map.get("cityName").toString());
				result.put("mobileNo", map.get("mobileNo")==null?"":map.get("mobileNo").toString());
				result.put("customerId", map.get("customerId")==null?"":map.get("customerId").toString());
				result.put("brokerName", map.get("brokerName")==null?"":map.get("brokerName").toString());
				result.put("coreAppBrokerCode", map.get("coreAppBrokerCode")==null?"":map.get("coreAppBrokerCode").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("debitNoteNo", map.get("debitNoteNo")==null?"":map.get("debitNoteNo").toString());
				result.put("premium", map.get("premium")==null?"":new BigDecimal(Double.parseDouble(map.get("premium").toString())).toString());
				result.put("vatPremium", map.get("vatPremium")==null?"":Double.parseDouble(map.get("vatPremium").toString()));
				result.put("vatPercent", map.get("vatPercent")==null?"":Double.parseDouble(map.get("vatPercent").toString()));
				result.put("totalPremium", map.get("totalPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("totalPremium").toString())).toString());
				result.put("signature", map.get("signature")==null?"":map.get("signature").toString());
				result.put("place", map.get("place")==null?"":map.get("place").toString());
				result.put("productId", map.get("productId")==null?"":map.get("productId").toString());
				result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				result.put("taxName", map.get("companyId")==null?"":map.get("companyId").toString().equalsIgnoreCase("100004")?"Premium":"Vat");
				result.put("userMobile", map.get("userMobile")==null?"":map.get("userMobile").toString());
				result.put("identificationNo", map.get("identificationNo")==null?"":map.get("identificationNo").toString());
				result.put("postalAddress", StringUtils.join(
						Arrays.asList(
								map.get("address1") == null ? "" : map.get("address1").toString(),
										map.get("pinCode") == null ? "" : map.get("pinCode").toString(),
												map.get("stateName") == null ? "" : map.get("stateName").toString(),
														map.get("cityName") == null ? "" : map.get("cityName").toString(),
																map.get("countryName") == null ? "" : map.get("countryName").toString()
								).stream()
						.filter(value -> !value.isEmpty())
						.collect(Collectors.joining(","))
						));
				result.put("mobileNo1", map.get("mobileNo1")==null?"":map.get("mobileNo1").toString());
				result.put("approvedBy", map.get("approvedBy")==null?"":map.get("approvedBy").toString());
				result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
				result.put("renewalDate", map.get("expiryDate")==null?"":RenewalDate(map.get("expiryDate").toString()));
				result.put("loginId", map.get("loginId")==null?"":map.get("loginId").toString());
				result.put("bdmName", map.get("bdmName")==null?"":map.get("bdmName").toString());
				result.put("bdmCode", map.get("bdmCode")==null?"":map.get("bdmCode").toString());
				result.put("overAllPremium", OverAllPremium);
				result.put("premiumDetails", premiumDetailsRes);
				//result.put("sectionDetails", sectionList);
				//result.put("locationDetails", locationDetails);
				result.put("firstLossPayeesList", firstLossPayeesList);
				result.put("coverageDetails",  coverageDetails.stream()
						.sorted(Comparator.comparing(o -> (Integer) o.get("coverId")))
						.collect(Collectors.toList()));
				//result.put("attachMents", attachments);
				//result.put("attachMents", attachments);
		//		DecimalFormat df = new DecimalFormat("#,##0.00");

				// Tax Display Order
				Map<Integer, Integer> taxOrder = new HashMap<>();
				taxOrder.put(43, 1); // Stamp Duty
				taxOrder.put(42, 2); // Namfisa Levy
				taxOrder.put(41, 3); // VAT
				List<PolicyCoverData> taxList;

				// Check whether Overall Premium Section exists
				Map<Integer, PolicyCoverData> taxMap = new HashMap<>();

				for (PolicyCoverData data : coverData) {

				    if (!"T".equalsIgnoreCase(data.getCoverageType())) {
				        continue;
				    }

				    boolean include = false;

				    // Stamp Duty -> Only Overall Premium Section
				    if (data.getTaxId() == 43
				            && data.getSectionId() != null
				            && data.getSectionId() == 99999) {
				        include = true;
				    }

				    // Namfisa Levy & VAT -> Sum Other Sections
				    if ((data.getTaxId() == 42 || data.getTaxId() == 41)
				            && data.getSectionId() != null
				            && data.getSectionId() != 99999) {
				        include = true;
				    }

				    if (!include) {
				        continue;
				    }

				    PolicyCoverData tax = taxMap.get(data.getTaxId());

				    if (tax == null) {
				        tax = new PolicyCoverData();
				        tax.setTaxId(data.getTaxId());
				        tax.setTaxDesc(data.getTaxDesc());
				        tax.setTaxRate(data.getTaxRate());
				        tax.setTaxAmount(BigDecimal.ZERO);
				        taxMap.put(data.getTaxId(), tax);
				    }

				    tax.setTaxAmount(
				            tax.getTaxAmount().add(
				                    data.getTaxAmount() == null
				                            ? BigDecimal.ZERO
				                            : data.getTaxAmount()));
				}

				taxList = taxMap.values().stream()
				        .sorted(Comparator.comparing(c ->
				                taxOrder.getOrDefault(c.getTaxId(), Integer.MAX_VALUE)))
				        .collect(Collectors.toList());
				int index = 1;
				 BigDecimal premiumExclud = coverData.stream()
			                .filter(c -> !"T".equalsIgnoreCase(c.getCoverageType()))
			                .filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
			                .map(c -> c.getPremiumExcludedTaxLc() == null
			                        ? BigDecimal.ZERO
			                        : c.getPremiumExcludedTaxLc())
			                .reduce(BigDecimal.ZERO, BigDecimal::add);

			        
				BigDecimal totalTaxAmount = BigDecimal.ZERO;
				BigDecimal totalTaxAmountMonthly = BigDecimal.ZERO;
				for (PolicyCoverData tax : taxList) {

					BigDecimal amount = tax.getTaxAmount() == null ? BigDecimal.ZERO : tax.getTaxAmount();
					totalTaxAmount = totalTaxAmount.add(amount);
				
					BigDecimal monthlyAmount = amount.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
					totalTaxAmountMonthly = totalTaxAmountMonthly.add(amount);
					String taxName =tax.getTaxDesc() == null ? "" : tax.getTaxDesc();

					if (tax.getTaxRate() != null) {
						taxName += " " + tax.getTaxRate().stripTrailingZeros().toPlainString() + "%";
					}

					result.put("Tax" + index, taxName);
					result.put("TaxAmount" + index, amount);
					result.put("TaxAmountMonthly" + index,monthlyAmount);
					
					if (tax.getTaxId() == 41) {

					    BigDecimal premiumExcludedTaxLc = coverData.stream()
					            .filter(c -> !"T".equalsIgnoreCase(c.getCoverageType()))
					            .filter(c -> c.getSectionId() != null && c.getSectionId() != 99999)
					            .map(c -> c.getPremiumExcludedTaxLc() == null
					                    ? BigDecimal.ZERO
					                    : c.getPremiumExcludedTaxLc())
					            .reduce(BigDecimal.ZERO, BigDecimal::add);

					    BigDecimal premiumExcludedTaxLcVat = premiumExcludedTaxLc.add(amount);

					    result.put("PremiumExcludedTaxLcVat", premiumExcludedTaxLcVat);
					}


					index++;
				}
				HomePositionMaster home = homeRepo.findByQuoteNo(QuoteNo);
				if ("1".equalsIgnoreCase(home.getApplicationId())) {
					result.put("ApprovedByHome" ,home.getLoginId());
					result.put("CreatedByHome" ,home.getLoginId());
					if("A".equalsIgnoreCase(home.getAdminReferralStatus()))
					{
						result.put("ApprovedByHome" ,home.getAdminLoginId());
					}
				}else
				{
					result.put("ApprovedByHome" ,home.getApplicationId());
					result.put("CreatedByHome" ,home.getApplicationId());
					if("A".equalsIgnoreCase(home.getAdminReferralStatus()))
					{
						result.put("ApprovedByHome" ,home.getAdminLoginId());
					}
				}
				
				result.put("withTaxAmount" ,premiumExclud.add(totalTaxAmount));
				
			}
		}catch(Exception e) {
			log.info("Error in EwaySchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into EwaySchedule");
		return result;
	}
	
	
	
	

	public List<Map<String, Object>> getConditionListSectionCover(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> conditionList = new ArrayList<Map<String, Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> conditionRes = new ArrayList<>();

			for (int i = 1; i <= 2; i++) {
				CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<>();
				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot2.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot2.get("quoteNo"), quoteNo));
				}

				if (i == 1 ) {

					Subquery<Tuple> CquoteIn = cq2.subquery(Tuple.class);
					Root<TermsAndCondition> StacRoot = CquoteIn.from(TermsAndCondition.class);

					CquoteIn.select(StacRoot.get("quoteNo")).where(cb.equal(StacRoot.get("quoteNo"), quoteNo),cb.equal(StacRoot.get("id"), "6"));

					Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

						predicates.add(cb.equal(cmRoot2.get("sectionId"), "99999"));

					} else {
						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
					//	predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")),cb.equal(cmRoot2.get("sectionId"), "99999")));
						predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")),cb.equal(cmRoot2.get("sectionId"), "99999")));
					}

					cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"),
							cmRoot2.get("sectionId").alias("sectionId"), cmRoot2.get("clausesId").alias("clausesId"),
							cmRoot2.get("clausesShortDesc").alias("conditionTitle"),
							cmRoot2.get("pdfLocation").alias("pdfLocation"), cmRoot2.get("pdfName").alias("pdfName"));

					predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
					predicates.add(cb.equal(cmRoot2.get("productId").as(String.class),
							hpmRoot2.get("productId").as(String.class)));

					predicates.add(cb.or(cb.equal(cmRoot2.get("branchCode"), hpmRoot2.get("branchCode")),
							cb.equal(cmRoot2.get("branchCode"), "99999")));

					predicates.add(cb.between(cb.literal(new Date()), cmRoot2.get("effectiveDateStart"),
							cmRoot2.get("effectiveDateEnd")));

					predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
					predicates.add(cb.equal(cmRoot2.get("typeId"), "D"));
					predicates.add(cb.not(cb.in(hpmRoot2.get("quoteNo")).value(CquoteIn)));

				} else {

					Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
					if (sectionIds != null && !sectionIds.isEmpty()) {

					//	predicates.add(cb.or(tacRoot2.get("sectionId").in(sectionIds),cb.equal(tacRoot2.get("sectionId"), "99999")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), sddRoot2.get("sectionId")));
					}

					cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"),
							tacRoot2.get("sectionId").alias("sectionId"), tacRoot2.get("sno").alias("clausesId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),
							cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));

					predicates.add(cb.equal(tacRoot2.get("productId").as(String.class),
							hpmRoot2.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot2.get("quoteNo")).value(tacRoot2.get("quoteNo")));

					predicates.add(cb.equal(tacRoot2.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")),
							cb.equal(tacRoot2.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot2.get("id"), "6"));
				}

				Predicate[] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);

				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
			}

			conditionList = conditionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Cmap = new LinkedHashMap<String, Object>();

				Cmap.put("conditionTerms",
						c.get("conditionTerms") == null ? ""
								: c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Cmap.put("title",
						c.get("conditionTitle") == null ? ""
								: c.get("conditionTitle").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Cmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Cmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {
			log.info("Error in getConditionListSectionCover ==> " + e.getMessage());
			e.printStackTrace();
		}

		return conditionList;
	}
	public List<Map<String,Object>> getConditionList(String policyNo,String QuoteNo, String sectionId){
		List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();		
			List<Tuple> conditionRes = new ArrayList<>();
			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<Predicate>();

				if(StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot2.get("policyNo"), policyNo));
				}else {
					predicates.add(cb.equal(hpmRoot2.get("quoteNo"), QuoteNo));
				}
				if(i == 1) {
					Subquery<Tuple> CquoteIn = cq2.subquery(Tuple.class);
					Root<TermsAndCondition> StacRoot = CquoteIn.from(TermsAndCondition.class);
					CquoteIn.select(StacRoot.get("quoteNo")).where(cb.equal(StacRoot.get("quoteNo"), QuoteNo),cb.equal(StacRoot.get("id"), "6"));

					Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sectionId), cb.equal(cmRoot2.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
						predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")), cb.equal(cmRoot2.get("sectionId"), "99999")));
					}
					cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"),cmRoot2.get("sectionId").alias("sectionId"),cmRoot2.get("clausesId").alias("clausesId"),
							cmRoot2.get("clausesShortDesc").alias("conditionTitle"),cmRoot2.get("pdfLocation").alias("pdfLocation"),
							cmRoot2.get("pdfName").alias("pdfName"));
					predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
					predicates.add(cb.equal(cmRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(cmRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(cmRoot2.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), cmRoot2.get("effectiveDateStart"), cmRoot2.get("effectiveDateEnd")));
					predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
					predicates.add(cb.equal(cmRoot2.get("typeId"), "D"));
					predicates.add(cb.not(cb.in(hpmRoot2.get("quoteNo")).value(CquoteIn)));
				}else {
					Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(tacRoot2.get("sectionId"), sectionId), cb.equal(tacRoot2.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), sddRoot2.get("sectionId")));
					}
					cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"),tacRoot2.get("sectionId").alias("sectionId"),tacRoot2.get("sno").alias("clausesId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));
					predicates.add(cb.equal(tacRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
					predicates.add(cb.in(hpmRoot2.get("quoteNo")).value(tacRoot2.get("quoteNo")));
					predicates.add(cb.equal(tacRoot2.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(tacRoot2.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot2.get("id"), "6"));
				}
				Predicate [] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);
				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
			}
			conditionList = conditionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
				Cmap.put("conditionTerms", c.get("conditionTerms")==null?"":c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Cmap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Cmap.put("title", c.get("conditionTitle")==null?"":c.get("conditionTitle").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Cmap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Cmap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Cmap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getConditionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return conditionList;
	}
	
	public List<Map<String,Object>> getConditionListCover(String policyNo,String QuoteNo, String sectionId ,String coverId,Integer loc){
		List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
		try {
			List<String> sect= new ArrayList<>();
			sect.add(sectionId);
			List<String> cover= new ArrayList<>();
			cover.add(coverId);
			List<TermsAndCondition> tClist = termsConRepo.findByQuoteNoAndSectionIdInAndCoverIdInAndIdAndLocationId(QuoteNo,sect,cover,6,loc.toString());
			if(tClist != null && !tClist.isEmpty())
			{
				 conditionList = tClist.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.getSubIdDesc());
					Cmap.put("SectionId", c.getSectionId());
					Cmap.put("title", "Clauses");
					return Cmap;
				}).collect(Collectors.toList());
				 return conditionList;
			}
			else
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();		
				List<Tuple> conditionRes = new ArrayList<>();
				CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();

				predicates.add(cb.equal(hpmRoot2.get("quoteNo"), QuoteNo));
				
				Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);
				if(StringUtils.isNotBlank(sectionId)) {
					predicates.add(cb.equal(cmRoot2.get("sectionId"), Integer.valueOf(sectionId)));
					predicates.add(cb.equal(cmRoot2.get("coverId"), coverId));
				//	predicates.add(cb.equal(cmRoot2.get("coverId"), Integer.valueOf(coverId)));
				//	predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sectionId), cb.equal(cmRoot2.get("sectionId"), "99999")));
					
					predicates.add(cb.or(cb.equal(cmRoot2.get("coverId"), Integer.valueOf(coverId)),cb.equal(cmRoot2.get("coverId"), Integer.valueOf("99999"))));
				}else {
					Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
					predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
					//predicates.add(cb.or(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")), cb.equal(cmRoot2.get("sectionId"), "99999")));
					//predicates.add(cb.or(cb.equal(cmRoot2.get("coverId"), sddRoot2.get("coverId")), cb.equal(cmRoot2.get("coverId"), Integer.valueOf("99999"))));
					predicates.add(cb.equal(cmRoot2.get("coverId"), sddRoot2.get("coverId")));
					predicates.add(cb.equal(cmRoot2.get("sectionId"), sddRoot2.get("sectionId")));
				}
				cq2.multiselect(cmRoot2.get("clausesDescription").alias("conditionTerms"),cmRoot2.get("sectionId").alias("sectionId"),cmRoot2.get("clausesId").alias("clausesId"),
						cmRoot2.get("clausesShortDesc").alias("conditionTitle"),cmRoot2.get("pdfLocation").alias("pdfLocation"),
						cmRoot2.get("pdfName").alias("pdfName"));
				predicates.add(cb.equal(cmRoot2.get("companyId"), hpmRoot2.get("companyId")));
				predicates.add(cb.equal(cmRoot2.get("productId").as(String.class), hpmRoot2.get("productId").as(String.class)));
				predicates.add(cb.or(cb.equal(cmRoot2.get("branchCode"), hpmRoot2.get("branchCode")), cb.equal(cmRoot2.get("branchCode"), "99999")));
				predicates.add(cb.between(cb.literal(new Date()), cmRoot2.get("effectiveDateStart"), cmRoot2.get("effectiveDateEnd")));
				predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
				predicates.add(cb.equal(cmRoot2.get("typeId"), "D"));
				Predicate [] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);
				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
				conditionList = conditionRes.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.get("conditionTerms")==null?"":c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
					Cmap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
					Cmap.put("title", c.get("conditionTitle")==null?"":c.get("conditionTitle").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
					Cmap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
					Cmap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
					return Cmap;
				}).collect(Collectors.toList());
			}
			
		}catch(Exception e) {
			log.info("Error in getConditionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return conditionList;
	}

	public List<Map<String,Object>> getExclusionList(String policyNo,String QuoteNo,String sectionId){
		List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<Predicate>();
				if(StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
				}else {
					predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				}
				if(i == 1) {

					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "7"));

					Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sectionId), cb.equal(emRoot3.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId")), cb.equal(emRoot3.get("sectionId"), "99999")));
					}
					cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"),emRoot3.get("sectionId").alias("sectionId"),emRoot3.get("exclusionId").alias("exclusionId"),
							emRoot3.get("pdfLocation").alias("pdfLocation"),emRoot3.get("pdfName").alias("pdfName"));
					predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(emRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(emRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(emRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), emRoot3.get("effectiveDateStart"), emRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(emRoot3.get("status"), "Y"));
					predicates.add(cb.equal(emRoot3.get("typeId"), "D"));
					predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}else {
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
					}

					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("exclusionId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "7"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			exclusionList = exclusionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("exclusioTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return exclusionList;
	}

	public List<Map<String,Object>> getExclusionListCover(String policyNo,String QuoteNo,String sectionId,String coverId,Integer loc){
		List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				
				if(i == 1) {

					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "7"));

					Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);
					if(StringUtils.isNotBlank(sectionId)) {
						predicates.add(cb.equal(emRoot3.get("sectionId"), Integer.valueOf(sectionId)));
						predicates.add(cb.equal(emRoot3.get("coverId"), Integer.valueOf(coverId)));
//						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sectionId), cb.equal(emRoot3.get("sectionId"), "99999")));
//						predicates.add(cb.or(cb.equal(emRoot3.get("coverId"), coverId), cb.equal(emRoot3.get("coverId"), "99999")));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId"))
								, cb.equal(emRoot3.get("sectionId"), "99999")));
						predicates.add(cb.equal(emRoot3.get("sectionId"), Integer.valueOf(sectionId)));
						//predicates.add(cb.or(cb.equal(emRoot3.get("coverId"), sddRoot3.get("coverId")), cb.equal(emRoot3.get("coverId"), "99999")));
						predicates.add(cb.equal(emRoot3.get("coverId"), sddRoot3.get("coverId")));
					}
					cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"),emRoot3.get("sectionId").alias("sectionId"),emRoot3.get("exclusionId").alias("exclusionId"),
							emRoot3.get("pdfLocation").alias("pdfLocation"),emRoot3.get("pdfName").alias("pdfName"));
					predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(emRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(emRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(emRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), emRoot3.get("effectiveDateStart"), emRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(emRoot3.get("status"), "Y"));
					predicates.add(cb.equal(emRoot3.get("typeId"), "D"));
				//	predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}else {
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					if(StringUtils.isNotBlank(sectionId)) {
//						predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
//						predicates.add(cb.or(cb.equal(tacRoot3.get("coverId"), coverId), cb.equal(tacRoot3.get("coverId"), "99999")));
						
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sectionId));
						predicates.add(cb.equal(tacRoot3.get("coverId"), coverId));
						predicates.add(cb.equal(tacRoot3.get("locationId"), loc.toString()));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
						predicates.add(cb.equal(tacRoot3.get("coverId"), sddRoot3.get("coverId")));
					}

					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("exclusionId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "7"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			exclusionList = exclusionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return exclusionList;
	}
	
	public List<Map<String,Object>> getWarrantyDescription(String policyNo,String QuoteNo, String sectionId){
		List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();

			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				if(StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
				}else {
					predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				}
				if(i == 1) {

					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "4"));
					Root<WarrantyMaster> wmRoot3 = cq3.from(WarrantyMaster.class);
					cq3.multiselect(wmRoot3.get("warrantyDescription").alias("warrantyTerms"),wmRoot3.get("sectionId").alias("sectionId"),wmRoot3.get("warrantyId").alias("warrantyId"),
							wmRoot3.get("pdfLocation").alias("pdfLocation"),wmRoot3.get("pdfName").alias("pdfName"));
					predicates.add(cb.equal(wmRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(wmRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(wmRoot3.get("sectionId"), sectionId), cb.equal(wmRoot3.get("sectionId"), "99999")));
					predicates.add(cb.or(cb.equal(wmRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(wmRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), wmRoot3.get("effectiveDateStart"), wmRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(wmRoot3.get("status"), "Y"));
					predicates.add(cb.equal(wmRoot3.get("typeId"), "D"));
					predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}else {
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					cq3.multiselect(tacRoot3.get("subIdDesc").alias("warrantyTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("warrantyId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "4"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			warrantyList = warrantyRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("Sno", c.get("warrantyId")==null?"":c.get("warrantyId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getWarrantyDescription ==> "+e.getMessage());
			e.printStackTrace();
		}
		return warrantyList;

	}
	
	public List<Map<String,Object>> getWarrantyDescriptionCover(String policyNo,String QuoteNo, String sectionId,String coverId,Integer loc){
		List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();

			for(int i=1;i<=2;i++) {
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				
				if(i == 1) {

					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);
					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);
					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), QuoteNo),cb.equal(SEtacRoot.get("id"), "4"));
					Root<WarrantyMaster> wmRoot3 = cq3.from(WarrantyMaster.class);
					cq3.multiselect(wmRoot3.get("warrantyDescription").alias("warrantyTerms"),wmRoot3.get("sectionId").alias("sectionId"),wmRoot3.get("warrantyId").alias("warrantyId"),
							wmRoot3.get("pdfLocation").alias("pdfLocation"),wmRoot3.get("pdfName").alias("pdfName"));
					predicates.add(cb.equal(wmRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(wmRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
//					predicates.add(cb.or(cb.equal(wmRoot3.get("sectionId"), sectionId), cb.equal(wmRoot3.get("sectionId"), "99999")));
//					predicates.add(cb.or(cb.equal(wmRoot3.get("coverId"), coverId), cb.equal(wmRoot3.get("coverId"), "99999")));
					predicates.add(cb.equal(wmRoot3.get("sectionId"), Integer.valueOf(sectionId)));
					predicates.add(cb.equal(wmRoot3.get("coverId"), Integer.valueOf(coverId)));
					predicates.add(cb.or(cb.equal(wmRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(wmRoot3.get("branchCode"), "99999")));
					predicates.add(cb.between(cb.literal(new Date()), wmRoot3.get("effectiveDateStart"), wmRoot3.get("effectiveDateEnd")));
					predicates.add(cb.equal(wmRoot3.get("status"), "Y"));
					predicates.add(cb.equal(wmRoot3.get("typeId"), "D"));
			//		predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}else {
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					cq3.multiselect(tacRoot3.get("subIdDesc").alias("warrantyTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("warrantyId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
//					predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
//					predicates.add(cb.or(cb.equal(tacRoot3.get("coverId"), coverId), cb.equal(tacRoot3.get("coverId"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("sectionId"), sectionId));
					predicates.add(cb.equal(tacRoot3.get("coverId"),coverId));
					predicates.add(cb.equal(tacRoot3.get("locationId"),loc.toString()));
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "4"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}
			warrantyList = warrantyRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("Sno", c.get("warrantyId")==null?"":c.get("warrantyId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getWarrantyDescription ==> "+e.getMessage());
			e.printStackTrace();
		}
		return warrantyList;

	}


	public Map<String, Object> getInalipaSchedule(String policyNo) {
		Map<String,Object> res = new HashMap<String,Object>();
		try {
			List<Map<String,Object>> list = groupMedicalDetRepo.getInalipaScheduleByPolicyNo(policyNo);
			if(!CollectionUtils.isEmpty(list)) {
				Map<String,Object> map = list.get(0);
				res.put("PolicyNo", map.get("POLICY_NO")==null?"":map.get("POLICY_NO").toString());
				res.put("InsuredName", map.get("CUSTOMER_NAME")==null?"":map.get("CUSTOMER_NAME").toString());
				res.put("MobileCode", map.get("MOBILE_CODE")==null?"":map.get("MOBILE_CODE").toString());
				res.put("MobileNo", map.get("MOBILE_NO")==null?"":map.get("MOBILE_NO").toString());
				res.put("TranscationNo", map.get("CLIENT_TRANSACTION_NO")==null?"":map.get("CLIENT_TRANSACTION_NO").toString());
				res.put("TranscationDate", map.get("ENTRY_DATE")==null?"":sdf.format(map.get("ENTRY_DATE")).toString());
				res.put("LoginId", map.get("LOGIN_ID")==null?"":map.get("LOGIN_ID").toString());
				res.put("StartDate", map.get("INCEPTION_DATE")==null?"":sdf.format(map.get("INCEPTION_DATE")).toString());
				res.put("EndDate", map.get("EXPIRY_DATE")==null?"":sdf.format(map.get("EXPIRY_DATE")).toString());
				res.put("AmountPaid", map.get("AMOUNT_PAID")==null?"":map.get("AMOUNT_PAID").toString());
				res.put("Premium", map.get("PREMIUM")==null?"":map.get("PREMIUM").toString());
				res.put("TaxPercent", map.get("TAX_PERCENTAGE")==null?"":map.get("TAX_PERCENTAGE").toString());
				res.put("TaxPremium", map.get("TAX_PREMIUM")==null?"":map.get("TAX_PREMIUM").toString());
				res.put("OverAllPremium", map.get("OVERALL_PREMIUM")==null?"":map.get("OVERALL_PREMIUM").toString());
				res.put("PlanObtained", map.get("PLAN_OBTAINED")==null?"":map.get("PLAN_OBTAINED").toString());
				res.put("Companylogo", map.get("COMPANY_LOGO")==null?"":map.get("COMPANY_LOGO").toString());
				res.put("CompanyName", map.get("COMPANY_NAME")==null?"":map.get("COMPANY_NAME").toString());
			}
		}catch(Exception e) {
			log.info("Error in jasperCustomServiceImple :: getInalipaSchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		return res;
	}

	public List<Map<String, Object>> getMadisonMotorSchedule(String policyNo,String diskNo,HomePositionMaster hp) {
		log.info("Enter into getMadisonMotorSchedule. \nArguments ==> "+policyNo);
		List<Map<String,Object>> resultList = new ArrayList<>();
		List<TearmsAndCondition> tearmsAndwarrantesRes = new ArrayList<>();
		List<Map<String,Object>> conditionsRes = new ArrayList<>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();	
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<CompanyProductMaster> cpmRoot = cq.from(CompanyProductMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<MotorDataDetails> mddRoot = cq.from(MotorDataDetails.class);

			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCmAmd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCmAmd.get("amendId"))).where(cb.equal(SubCmAmd.get("countryId"), piRoot.get("nationality")),cb.equal(SubCmAmd.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(SubCmAmd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), hpmRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));

			Subquery<Long> MotorCount = cq.subquery(Long.class);
			Root<MotorDataDetails> SubMCRoot = MotorCount.from(MotorDataDetails.class);
			MotorCount.select(cb.count(SubMCRoot)).where(cb.equal(SubMCRoot.get("policyNo"), hpmRoot.get("policyNo")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			Expression<Integer> dateDiff = cb.function(
					"DATEDIFF", Integer.class, 
					hpmRoot.get("expiryDate"), 
					hpmRoot.get("inceptionDate")
					);

			Expression<Integer> result = cb.sum(dateDiff, 1);

			cq.multiselect(cpmRoot.get("companyId").alias("companyId"),cpmRoot.get("effectiveDateStart").alias("effectiveDateStart"),cpmRoot.get("effectiveDateEnd").alias("effectiveDateEnd"),
					hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
							.when(cb.equal(piRoot.get("titleDesc"),""), "").otherwise(".").as(String.class),
							piRoot.get("clientName"))).alias("customerName"),piRoot.get("email1").alias("email1"),
					hpmRoot.get("debitNoteNo").alias("debitNoteNo"),cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(piRoot.get("cityName"),
							cb.concat(" Street", cb.concat(",", cb.concat(piRoot.get("stateName"), cb.concat(",", countryName))))))).alias("address"),
					cb.selectCase().when(cb.isNotNull(piRoot.get("pinCode")), cb.concat("P.O.BOX ", cb.concat(piRoot.get("pinCode"), cb.concat(",", cb.concat(piRoot.get("cityName"),
							cb.concat(" Street",cb.concat(",", cb.concat(piRoot.get("stateName"), cb.concat(",", countryName))))))))).otherwise("").alias("postalAddress"),hpmRoot.get("inceptionDate").alias("inceptionDate"),
					hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("effectiveDate").alias("effectiveDate"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("stickerNumber").alias("stickerNumber"),hpmRoot.get("policyPeriod").alias("policyPeriod"),
					mddRoot.get("insuranceTypeDesc").alias("insuranceTypeDesc"),mddRoot.get("vehicleId").alias("vehicleId"),mddRoot.get("registrationNumber").alias("registrationNumber"),
					mddRoot.get("vehicleMakeDesc").alias("vehicleMake"),mddRoot.get("vehcileModelDesc").alias("vehcileModel"),mddRoot.get("vehicleTypeDesc").alias("vehicleTypeDesc"),
					mddRoot.get("cubicCapacity").alias("cubicCapacity"),mddRoot.get("manufactureYear").alias("manufactureYear"),mddRoot.get("seatingCapacity").alias("seatingCapacity"),
					mddRoot.get("colorDesc").alias("colorDesc"),mddRoot.get("policyTypeDesc").alias("policyTypeDesc"),mddRoot.get("sumInsured").alias("sumInsured"),
					mddRoot.get("engineNumber").alias("engineNumber"),mddRoot.get("chassisNumber").alias("chassisNumber"),mddRoot.get("motorUsageDesc").alias("motorUsageDesc"),
					cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("overallPremiumLc"))
					.otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("premiumLc"))
					.otherwise(hpmRoot.get("premiumFc")).alias("Premium"),cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(cpmRoot.get("currencyIds")), hpmRoot.get("vatPremiumLc"))
					.otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),hpmRoot.get("branchCode").alias("branchCode"),
					hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("approvedBy").alias("approvedBy"),
					cb.selectCase()
				    .when(
				        cb.equal(hpmRoot.get("applicationId"), "1"),
				        hpmRoot.get("loginId")
				    )
				    .otherwise(hpmRoot.get("applicationId"))
				    .alias("loginId"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("userName"),MotorCount.alias("noOfVehicle"),companyName.alias("companyName"),imageURL.alias("companylogo"),
					hpmRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),mddRoot.get("diskNo").alias("diskNo"),
					mddRoot.get("sectionName").alias("sectionName"),result.alias("days"))
			.where(StringUtils.isBlank(policyNo)?cb.equal(mddRoot.get("quoteNo"), hpmRoot.get("quoteNo")):cb.equal(mddRoot.get("policyNo"), hpmRoot.get("policyNo")),
					cb.equal(piRoot.get("customerId"), hpmRoot.get("customerId")),cb.equal(hpmRoot.get("loginId"), luiRoot.get("loginId")),
					cb.equal(cpmRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(cpmRoot.get("status"), "Y"),cb.equal(hpmRoot.get("productId"), cpmRoot.get("productId")),
					cb.between(cb.literal(new Date()), cpmRoot.get("effectiveDateStart"), cpmRoot.get("effectiveDateEnd")),
					cb.equal(hpmRoot.get("policyNo"), policyNo),StringUtils.isNotBlank(diskNo)?cb.equal(mddRoot.get("diskNo"), diskNo):cb.conjunction()).distinct(true).orderBy(cb.asc(mddRoot.get("vehicleId")));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!list.isEmpty()) {
				Tuple m = list.get(0);
				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);

				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),m.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);

				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));

				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),
						pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
						pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));

				List<Tuple> Slist = em.createQuery(cq1).getResultList();

				List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
				for(int i=0;i<sectionIds.size();i++) {
					String sectionId = sectionIds.get(i).toString();
					// CONDITIONS
					List<Map<String,Object>> conditionList = getConditionList(m.get("policyNo")==null?"":m.get("policyNo").toString(), m.get("quoteNo")==null?"":m.get("quoteNo").toString(),sectionId);
					conditionsRes.addAll(conditionList);

					// EXCLUSION
					List<Map<String,Object>> exclusionRes = getExclusionList(m.get("policyNo")==null?"":m.get("policyNo").toString(), m.get("quoteNo")==null?"":m.get("quoteNo").toString(),sectionId);
					List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
						Map<String,Object> eMap = new HashMap<String,Object>();
						eMap.put("conditionTerms", k.get("exclusioTerms"));
						return eMap;
					}).collect(Collectors.toList());

					//WARRANTY
					List<Map<String,Object>> warrantyList = getWarrantyDescription(m.get("policyNo")==null?"":m.get("policyNo").toString(), m.get("quoteNo")==null?"":m.get("quoteNo").toString(),sectionId);

					List<Map<String,Object>> tearmsAndwarrantes = Stream.of(exclusionList,warrantyList).flatMap(Collection::stream).collect(Collectors.toList());
					tearmsAndwarrantes.stream().distinct().collect(Collectors.toList()).forEach(g ->{
						TearmsAndCondition tearms = new TearmsAndCondition();
						tearms.setAllConditions(g.get("conditionTerms")==null?"":g.get("conditionTerms").toString());
						tearmsAndwarrantesRes.add(tearms);
					});
				}

				StringBuffer currentURL = new StringBuffer();
				String curURL = currentRequestURL();
				if(StringUtils.isNotBlank(curURL)) {
					String cur[] = curURL.split("/pdf");
					currentURL.append(cur[0]).append("/pdf/view/Certificate/");

				}
				

				list.forEach(k -> {
					LoginUserInfo login = new LoginUserInfo();
					LoginUserInfo issuer = new LoginUserInfo();
					String agencyName ="",issuerName ="" ,address="" , phnNo ="";
					if ("1".equalsIgnoreCase(hp.getApplicationId())) {
						login = logrepo.findByLoginId(hp.getLoginId());	
						agencyName=login.getUserName();
						issuerName=login.getUserName();
					}
					else
					{
						login = logrepo.findByLoginId(hp.getLoginId());	
						agencyName=login.getUserName();
						issuer = logrepo.findByLoginId(hp.getApplicationId());	
						issuerName=issuer.getUserName();
					}
					List<PersonalInfo> piList = piRepo.findByCustomerIdAndCompanyId(hp.getCustomerId(), hp.getCompanyId());
					if(piList!=null && !piList.isEmpty())
					{
						PersonalInfo pi = piList.get(0);
						 address = pi.getAddress1()
						            + " , "
						            + (StringUtils.isNotBlank(pi.getStateName()) ? pi.getStateName() : "")
						            + (StringUtils.isNotBlank(pi.getCityName()) ? " , " + pi.getCityName() : "")
						            + (StringUtils.isNotBlank(pi.getPinCode()) ? " , " + pi.getPinCode() : "");
						 phnNo= pi.getMobileCodeDesc1()+"-"+pi.getMobileNo1();
					}
					Map<String,Object> map = new HashMap<>();
					map.put("companyId", k.get("companyId")==null?"":k.get("companyId").toString());
					map.put("effectiveDateStart", k.get("effectiveDateStart")==null?"":k.get("effectiveDateStart").toString());
					map.put("effectiveDateEnd", k.get("effectiveDateEnd")==null?"":k.get("effectiveDateEnd").toString());
					map.put("policyNo", k.get("policyNo")==null?"":k.get("policyNo").toString());
					map.put("quoteNo", k.get("quoteNo")==null?"":k.get("quoteNo").toString());
					map.put("sectionName", k.get("sectionName")==null?"":k.get("sectionName").toString());
					map.put("customerName", k.get("customerName")==null?"":k.get("customerName").toString());
					map.put("debitNoteNo", k.get("debitNoteNo")==null?"":k.get("debitNoteNo").toString());
				//	map.put("address", k.get("address")==null?"":k.get("address").toString());
					map.put("address", address);
					map.put("phoneNo",phnNo);
					map.put("postalAddress", k.get("postalAddress")==null?"":k.get("postalAddress").toString());
					map.put("inceptionDate", k.get("inceptionDate")==null?"":k.get("inceptionDate").toString());
					map.put("time", new SimpleDateFormat("HH:mm").format(k.get("inceptionDate")));
					map.put("durationOfCover", "From "+sdf.format(k.get("inceptionDate"))+"  To  "+sdf.format(k.get("expiryDate"))+" 23:59");
					map.put("Quarter", getQuarter(k.get("days")==null?"":k.get("days").toString(),k.get("insuranceTypeDesc")==null?"":k.get("insuranceTypeDesc").toString()));
					map.put("expiryDate", k.get("expiryDate")==null?"":k.get("expiryDate").toString());
					map.put("currency", k.get("currency")==null?"":k.get("currency").toString());
					map.put("stickerNumber", k.get("stickerNumber")==null?"":k.get("stickerNumber").toString());
					map.put("insuranceTypeDesc", k.get("insuranceTypeDesc")==null?"":k.get("insuranceTypeDesc").toString());
					map.put("vehicleId", k.get("vehicleId")==null?"":k.get("vehicleId").toString());
					map.put("registrationNumber", k.get("registrationNumber")==null?"":k.get("registrationNumber").toString());
					map.put("vehicleMake", k.get("vehicleMake")==null?"":k.get("vehicleMake").toString());
					map.put("vehcileModel", k.get("vehcileModel")==null?"":k.get("vehcileModel").toString());
					map.put("vehicleTypeDesc", k.get("vehicleTypeDesc")==null?"":k.get("vehicleTypeDesc").toString());
					map.put("cubicCapacity", k.get("cubicCapacity")==null?"":k.get("cubicCapacity").toString());
					map.put("manufactureYear", k.get("manufactureYear")==null?"":k.get("manufactureYear").toString());
					map.put("seatingCapacity", k.get("seatingCapacity")==null?"":k.get("seatingCapacity").toString());
					map.put("colorDesc", k.get("colorDesc")==null?"":k.get("colorDesc").toString());
					map.put("policyTypeDesc", k.get("policyTypeDesc")==null?"":k.get("policyTypeDesc").toString());
					map.put("sumInsured", k.get("sumInsured")==null?"":k.get("sumInsured").toString());
					map.put("totalPremium", k.get("totalPremium")==null?"":k.get("totalPremium").toString());
					map.put("branchName", k.get("branchName")==null?"":k.get("branchName").toString());
					map.put("approvedBy", k.get("approvedBy")==null?"":k.get("approvedBy").toString());
					map.put("noOfVehicle", k.get("noOfVehicle")==null?"":k.get("noOfVehicle").toString());
					map.put("companyName", k.get("companyName")==null?"":k.get("companyName").toString());
					map.put("companylogo", k.get("companylogo")==null?"":k.get("companylogo").toString());
					map.put("coverNoteReferenceNo", k.get("coverNoteReferenceNo")==null?"":k.get("coverNoteReferenceNo").toString());
					map.put("engineNumber", k.get("engineNumber")==null?"":k.get("engineNumber").toString());
					map.put("chassisNumber", k.get("chassisNumber")==null?"":k.get("chassisNumber").toString());
					map.put("policyPeriod", k.get("policyPeriod")==null?"":k.get("policyPeriod").toString());
					map.put("motorUsageDesc", k.get("motorUsageDesc")==null?"":k.get("motorUsageDesc").toString());
					map.put("email1", k.get("email1")==null?"":k.get("email1").toString());
					map.put("loginId", k.get("loginId")==null?"":k.get("loginId").toString());
					map.put("Premium", k.get("Premium")==null?"":k.get("Premium").toString());
					map.put("vatPremium", k.get("vatPremium")==null?"":k.get("vatPremium").toString());
					map.put("diskNo", k.get("diskNo")==null?"":k.get("diskNo").toString());
					map.put("effectiveDate", k.get("effectiveDate")==null?"":k.get("effectiveDate").toString());
					map.put("currentURL", currentURL.toString());
					map.put("agencyName", agencyName);
					map.put("issuerName", issuerName);

					Map<String,Object> map1 = new HashMap<String,Object>();
					map1.put("tearmsAndwarrantes", tearmsAndwarrantesRes);
					map1.put("conditions", conditionsRes);
					map.put("conditionobj", map1);
					resultList.add(map);
				});
			}
			log.info("Exit into getMadisonMotorSchedule");
		}catch(Exception e) {
			log.info("Error in getMadisonMotorSchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		return resultList;
	}

	private String getQuarter(String days,String type) {
		String result="";
		try {
			if(!"Comprehensive".equalsIgnoreCase(type)) {
				Integer quarter = Integer.parseInt(days);
				if(quarter <= 30) {
					result = "None";
				}else if(quarter > 30 && quarter <=90) {
					result = "1 QTR";
				}else if(quarter > 90 && quarter <=180) {
					result = "2 QTR";
				}else if(quarter > 180 && quarter <=270) {
					result = "3 QTR";
				}else if(quarter > 270 && quarter <=365) {
					result = "4 QTR";
				}
			}else {
				result = "None";
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> GetKenyaMotorScheduleByRequestRefNo(String requestRefNo) {
		log.info("Enter Into GetKenyaMotorScheduleByRequestRefNo \n Argument ==> "+requestRefNo);
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> vehicleList = new ArrayList<Map<String,Object>>();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
		List<TaxInvoicePremiumDetails> premiumDetailsRes_1 = new ArrayList<>();
		List<Map<String,Object>> taxDetails = new ArrayList<Map<String,Object>>();
		List<EmiDisplayListRes> emiDetailsList = new ArrayList<EmiDisplayListRes>();
		Double OverAllPremium=0.0;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EserviceCustomerDetails> ecdRoot = cq.from(EserviceCustomerDetails.class);
			Root<EserviceMotorDetails> emdRoot = cq.from(EserviceMotorDetails.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);

			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCnAd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCnAd.get("amendId"))).where(cb.equal(SubCnAd.get("countryId"), ecdRoot.get("nationality")),
					cb.equal(SubCnAd.get("companyId"), emdRoot.get("companyId")),cb.equal(SubCnAd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), ecdRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), emdRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(icmAmdRoot.get("companyId"), icmRoot.get("companyId")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), emdRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), emdRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(luiRoot.get("userName").alias("userName"),emdRoot.get("requestReferenceNo").alias("requestReferenceNo"),emdRoot.get("companyId").alias("companyId"),
					emdRoot.get("currency").alias("currency"),emdRoot.get("policyStartDate").alias("inceptionDate"),emdRoot.get("branchName").alias("branchName"),emdRoot.get("quoteNo").alias("quoteNo"),
					emdRoot.get("policyEndDate").alias("expiryDate"),emdRoot.get("policyType").alias("policyType"),emdRoot.get("policyTypeDesc").alias("policyTypeDesc"),emdRoot.get("vehicleClass").alias("vehicleClass"),
					(cb.selectCase().when(cb.isNull(ecdRoot.get("titleDesc")), ecdRoot.get("clientName")).otherwise(cb.concat(cb.concat(ecdRoot.get("titleDesc"),"."), ecdRoot.get("clientName")))) .alias("customerName"),companyName.alias("companyName"),
					cb.concat(ecdRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(ecdRoot.get("pinCode"), ""),cb.concat(cb.selectCase().when(cb.isNull(ecdRoot.get("pinCode")), "").when(cb.equal(ecdRoot.get("pinCode"), ""), "")
							.otherwise(",").as(String.class), cb.concat(ecdRoot.get("stateName"), cb.concat(",", cb.concat(ecdRoot.get("cityName"),cb.concat(",", countryName)))))))).alias("address"),
					ecdRoot.get("vrTinNo").alias("vrTinNo"),ecdRoot.get("email1").alias("email1"),ecdRoot.get("mobileNo1").alias("mobileNo1"),imageURL.alias("companyLogo"),luiRoot.get("brokerLogo").alias("brokerLogo"),
					emdRoot.get("productId").alias("productId"))
			.where(cb.equal(emdRoot.get("customerReferenceNo"), ecdRoot.get("customerReferenceNo")),cb.equal(emdRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(luiRoot.get("loginId"), emdRoot.get("loginId")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.in(emdRoot.get("productId")).value(Arrays.asList("5","46")),cb.equal(emdRoot.get("requestReferenceNo"), requestRefNo))
			.orderBy(cb.desc(emdRoot.get("entryDate")));

			List<Tuple> list = em.createQuery(cq).getResultList();
			Tuple map = list.get(0);
			String sql = "SELECT MD.RISK_ID, MD.INSURANCE_TYPE_DESC, MD.POLICY_TYPE ,MD.POLICY_TYPE_DESC, MD.REGISTRATION_NUMBER, MD.VEHICLE_MAKE_DESC, MD.VEHCILE_MODEL_DESC, MD.CHASSIS_NUMBER, MD.VEHICLE_TYPE_DESC, ( SELECT COLOR_DESC FROM MOTOR_COLOR_MASTER WHERE COLOR_ID = MD.COLOR AND COMPANY_ID = MD.COMPANY_ID AND AMEND_ID = ( SELECT MAX(AMEND_ID) FROM MOTOR_COLOR_MASTER WHERE COLOR_ID = MD.COLOR AND COMPANY_ID = MD.COMPANY_ID ) ) AS COLOR_DESC, MD.MANUFACTURE_YEAR, MD.SUM_INSURED,MD.NO_OF_CLAIMS FROM ESERVICE_MOTOR_DETAILS MD WHERE MD.REQUEST_REFERENCE_NO = :requestRefNo";
			Query query = em.createNativeQuery(sql);
			query.setParameter("requestRefNo", requestRefNo);
			query.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<Map<String,Object>> vehicleDetails = query.getResultList();
			if(vehicleDetails!=null && !vehicleDetails.isEmpty()) {
				vehicleDetails.forEach(k -> {
					Map<String,Object> m = new HashMap<String,Object>();
					m.put("vehicleId", k.get("RISK_ID")==null?"":k.get("RISK_ID").toString());
					m.put("InsuranceType", k.get("INSURANCE_TYPE_DESC")==null?"":k.get("INSURANCE_TYPE_DESC").toString());
					m.put("PolicyType", k.get("POLICY_TYPE_DESC")==null?"":k.get("POLICY_TYPE_DESC").toString());
					m.put("RegistrationNumber", k.get("REGISTRATION_NUMBER")==null?"":k.get("REGISTRATION_NUMBER").toString());
					m.put("Make", k.get("VEHICLE_MAKE_DESC")==null?"":k.get("VEHICLE_MAKE_DESC").toString());
					m.put("Model", k.get("VEHCILE_MODEL_DESC")==null?"":k.get("VEHCILE_MODEL_DESC").toString());
					m.put("ChassisNo", k.get("CHASSIS_NUMBER")==null?"":k.get("CHASSIS_NUMBER").toString());
					m.put("BodyType", k.get("VEHICLE_TYPE_DESC")==null?"":k.get("VEHICLE_TYPE_DESC").toString());
					m.put("Color", k.get("COLOR_DESC")==null?"":k.get("COLOR_DESC").toString());
					m.put("Year", k.get("MANUFACTURE_YEAR")==null?"":k.get("MANUFACTURE_YEAR").toString());
					m.put("SumInsured", k.get("SUM_INSURED")==null?null:k.get("SUM_INSURED"));
					vehicleList.add(m);
				});
			}

			String policyTypeDesc=map.get("policyTypeDesc")==null?"":map.get("policyTypeDesc").toString();
			String referenceNo=map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString();
			String quoteNo=map.get("quoteNo")==null?"":map.get("quoteNo").toString();
			List<FactorRateRequestDetails> coverData = factorRateRequestDetailsRepo.findByRequestReferenceNo(referenceNo);
			/*List<PolicyCoverData>coverData=null;
			if(StringUtils.isNotBlank(quoteNo)) {
				coverData=coverDataRepository.findByRequestReferenceNoAndQuoteNo(referenceNo,quoteNo);
			}else {
				coverData=coverDataRepository.findByRequestReferenceNo(referenceNo);
			}*/
			if(coverData!=null && !coverData.isEmpty()) {


				Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
						.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
						.findAny().orElse(0.0);

				Double taxAmount = coverData.stream().filter(f -> (f.getUserOpt().equalsIgnoreCase("Y")  ||f.getIsSelected().equalsIgnoreCase("Y")) && f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
						.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));

				Double overAllPremium = coverData.stream()
						.filter(f -> (
								("Y".equalsIgnoreCase(f.getUserOpt()) || "Y".equalsIgnoreCase(f.getIsSelected()))
								&& f.getTaxId() == 0
								&& f.getSectionId() != 99999
								))
						.map(i -> i.getPremiumExcludedTaxFc() != null ? i.getPremiumExcludedTaxFc() : BigDecimal.ZERO)
						.collect(Collectors.summingDouble(BigDecimal::doubleValue));
				OverAllPremium=overAllPremium+taxAmount;
				
				if(vehicleDetails!=null && !vehicleDetails.isEmpty()) {
					Integer NoofClaims = vehicleDetails.get(0).get("NO_OF_CLAIMS")==null?0:Integer.valueOf(vehicleDetails.get(0).get("NO_OF_CLAIMS").toString());
					if(NoofClaims>0) {
						EmiInstallmentDetailsReq req1 = new EmiInstallmentDetailsReq();
						req1.setPremiumWithTax(OverAllPremium.toString());
						req1.setCompanyId(map.get("companyId")==null?"":map.get("companyId").toString());
						req1.setProductId(map.get("productId")==null?"":map.get("productId").toString());
						req1.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
						req1.setPolicyType(vehicleDetails.get(0).get("POLICY_TYPE")==null?"":vehicleDetails.get(0).get("POLICY_TYPE").toString());
						req1.setRequestReferenceNo(requestRefNo);
						
						List<EmiDisplayRes> emiDetails = viewEmiInstallmentDetails(req1);
						if(!emiDetails.isEmpty()) {
							for(EmiDisplayRes r : emiDetails) {
								if(Integer.valueOf(r.getEmiInfoRes().getNoOfMonth()) == NoofClaims ) {
									if(!r.getEmiPremium().isEmpty()) {
											emiDetailsList.addAll(r.getEmiPremium());										
										}
									}
								}
							}
						}
					}
				
				List<Integer> sectionIds = coverData.stream().map(k -> k.getSectionId()).distinct().collect(Collectors.toList());
				List<Map<String,Object>> sectionPremium = new ArrayList<Map<String,Object>>();
				for(int x=0;x<sectionIds.size();x++) {
					int s = Integer.parseInt(sectionIds.get(x).toString());
					System.out.println(new Gson().toJson(coverData.stream().filter(f -> f.getSectionId()==s)
							.collect(Collectors.toList())));
					List<Integer> coverids = coverData.stream().filter((f) -> {
						return !f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId() == s && f.getUserOpt().equalsIgnoreCase("Y") || f.getFreeCoverLimit().compareTo(BigDecimal.ZERO) > 0;
					}).map((m) -> {
						return m.getSubCoverYn().equalsIgnoreCase("Y") ? m.getSubCoverId() : m.getCoverId();
					}).distinct().collect(Collectors.toList());
					for(int j=0;j<coverids.size();j++) {
						int c = coverids.get(j);
						Map<String,Object> o = new HashMap<String,Object>();
						o.put("SectionId", s);
						o.put("CoverDesc", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
								&& (f.getSubCoverYn().equalsIgnoreCase("Y")?f.getSubCoverId():f.getCoverId())==c)
								.map(m -> m.getSubCoverYn().equalsIgnoreCase("Y")?(m.getSubCoverName()+" "+m.getCoverName()):(m.getCoverageType().equalsIgnoreCase("B")?policyTypeDesc:m.getCoverName())).findFirst().orElse("-N-A"));
						o.put("SumInsured",
							    coverData.stream()
							        .filter(f -> (!f.getCoverageType().equalsIgnoreCase("T"))
							            && f.getSectionId() == s
							            && (f.getSubCoverYn().equalsIgnoreCase("Y")
							                ? f.getSubCoverId()
							                : f.getCoverId()) == c)
							        .map(FactorRateRequestDetails::getSumInsured)
							        .filter(Objects::nonNull)
							        .max(BigDecimal::compareTo)
							        .orElse(BigDecimal.ZERO)
							);

						o.put("Rate", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
								&& (f.getSubCoverYn().equalsIgnoreCase("Y")?f.getSubCoverId():f.getCoverId())==c)
								.map(m -> m.getRate()).findFirst().orElse(BigDecimal.ZERO));
						o.put("TotPremium",
								coverData.stream()
								.filter(f -> 
								f.getCoverageType() != null 
								&& !f.getCoverageType().equalsIgnoreCase("T")
								&& f.getSectionId() == s
								&& (f.getSubCoverYn() != null && f.getUserOpt().equalsIgnoreCase("Y")				                && (f.getSubCoverYn().equalsIgnoreCase("Y") 
										? f.getSubCoverId() 
												: f.getCoverId()) == c)
										)
								.map(m -> m.getPremiumExcludedTaxFc() != null ? m.getPremiumExcludedTaxFc() : BigDecimal.ZERO)
								.collect(Collectors.summingDouble(BigDecimal::doubleValue))
								);
						sectionPremium.add(o);
					}
				}

				sectionPremium = sectionPremium.stream().sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
						.collect(Collectors.toList());
				sectionPremium.forEach(k -> {
					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
							.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
							.sumInsured(new BigDecimal(Double.valueOf(k.get("SumInsured").toString())).toString())
							.rate(new BigDecimal(Double.valueOf(k.get("Rate").toString())).toString())
							.build();
					premiumDetailsRes.add(u);
				});

				List<Integer> sectionIds_1 = coverData.stream().map(k -> k.getSectionId()).distinct().collect(Collectors.toList());
				List<Map<String,Object>> sectionPremium_1 = new ArrayList<Map<String,Object>>();
				for(int x=0;x<sectionIds_1.size();x++) {
					int s = Integer.parseInt(sectionIds_1.get(x).toString());
					List<Integer> coverids_1 = coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T"))
							&& f.getSectionId()==s && (f.getIsSelected().equalsIgnoreCase("Y")) && (f.getFreeCoverLimit().compareTo(BigDecimal.ZERO) == 0))
							.map(m -> m.getSubCoverYn().equalsIgnoreCase("Y")?m.getSubCoverId():m.getCoverId()).distinct()
							.collect(Collectors.toList());
					for(int j=0;j<coverids_1.size();j++) {
						int c = coverids_1.get(j);
						Map<String,Object> o = new HashMap<String,Object>();
						o.put("SectionId", s);
						o.put("CoverDesc", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
								&& (f.getSubCoverYn().equalsIgnoreCase("Y")?f.getSubCoverId():f.getCoverId())==c)
								.map(m -> m.getSubCoverYn().equalsIgnoreCase("Y")?(m.getSubCoverName()+" "+m.getCoverName()):m.getCoverName()).findFirst().orElse("-N-A"));
						o.put("SumInsured",
							    coverData.stream()
							        .filter(f -> (!f.getCoverageType().equalsIgnoreCase("T"))
							            && f.getSectionId() == s
							            && (f.getSubCoverYn().equalsIgnoreCase("Y")
							                ? f.getSubCoverId()
							                : f.getCoverId()) == c)
							        .map(FactorRateRequestDetails::getSumInsured)
							        .filter(Objects::nonNull)
							        .max(BigDecimal::compareTo)
							        .orElse(BigDecimal.ZERO)
							);

						o.put("Rate", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
								&& (f.getSubCoverYn().equalsIgnoreCase("Y")?f.getSubCoverId():f.getCoverId())==c)
								.map(m -> m.getRate()).findFirst().orElse(BigDecimal.ZERO));
						o.put("TotPremium",
								coverData.stream()
								.filter(f -> 
								f.getCoverageType() != null 
								&& !f.getCoverageType().equalsIgnoreCase("T")
								&& f.getSectionId() == s
								&& (f.getSubCoverYn() != null && f.getUserOpt().equalsIgnoreCase("Y")				                && (f.getSubCoverYn().equalsIgnoreCase("Y") 
										? f.getSubCoverId() 
												: f.getCoverId()) == c)
										)
								.map(m -> m.getPremiumExcludedTaxFc() != null ? m.getPremiumExcludedTaxFc() : BigDecimal.ZERO)
								.collect(Collectors.summingDouble(BigDecimal::doubleValue))
								);
						sectionPremium_1.add(o);
					}
				}

				sectionPremium_1 = sectionPremium_1.stream().sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
						.collect(Collectors.toList());
				sectionPremium_1.forEach(k -> {
					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
							.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
							.sumInsured(new BigDecimal(Double.valueOf(k.get("SumInsured").toString())).toString())
							.rate(new BigDecimal(Double.valueOf(k.get("Rate").toString())).toString())
							.build();
					premiumDetailsRes_1.add(u);
				});

				result.put("taxRate", new BigDecimal(Double.valueOf(taxRate.toString())).toString());
				result.put("taxAmount", new BigDecimal(Double.valueOf(taxAmount.toString())).toString());
			}
			List<Tuple>taxlist=getTaxDetails(referenceNo,quoteNo);
			if(!CollectionUtils.isEmpty(taxlist)) {
				for (Tuple tuple : taxlist) {
					Map<String,Object>tax=new HashMap<>();
					tax.put("taxId", tuple.get("taxId")==null?"":tuple.get("taxId").toString());
					tax.put("taxDesc", tuple.get("taxDesc")==null?"":tuple.get("taxDesc").toString());
					tax.put("taxRate", tuple.get("taxRate")==null?"":tuple.get("taxRate").toString());
					tax.put("taxAmount", tuple.get("taxAmount")==null?"":new DecimalFormat("##,##0.00").format(new BigDecimal(tuple.get("taxAmount").toString())));
					taxDetails.add(tax);
				}

			}



			//			if(coverData!=null && !coverData.isEmpty()) {
			//				Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getSectionId()!=99999 && (f.getCoverageType().equals("T")))
			//						.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
			//						.findFirst().orElse(0.0);
			//				
			//				Double taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getSectionId()!=99999 && (f.getCoverageType().equals("T")))
			//						.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));
			//				
			//				
			//				List<Map<String,Object>> sectionPremium1 = coverData.stream().filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999
			//						&& (!f.getCoverageType().equalsIgnoreCase("T")))
			//						.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
			//							BigDecimal.ZERO, FactorRateRequestDetails::getPremiumIncludedTaxFc, BigDecimal::add))))
			//						.entrySet().stream()
			//						.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
			//							Integer sectionId = s.getKey();
			//							return s.getValue().entrySet().stream()
			//									.map((Map.Entry<String,BigDecimal> g )-> {
			//										String coverDesc = g.getKey();
			//										BigDecimal totPremium = g.getValue();
			//										Map<String,Object> secMap = new HashMap<String,Object>();
			//										secMap.put("SectionId", sectionId);
			//										secMap.put("CoverDesc", coverDesc);
			//										secMap.put("SumInsured", coverData.stream()
			//												.filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()==sectionId)
			//												.map(m -> m.getSumInsured()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
			//										secMap.put("TotPremium", totPremium);
			//										return secMap;
			//									});
			//						}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
			//						.collect(Collectors.toList());
			//				sectionPremium.forEach(k -> {
			//					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
			//							.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
			//							.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
			//							.sumInsured(new BigDecimal(Double.valueOf(k.get("SumInsured").toString())).toString())
			//						.build();
			//						premiumDetailsRes.add(u);
			//				});
			//					
			//					result.put("taxRate", new BigDecimal(Double.valueOf(taxRate.toString())).toString());
			//					result.put("taxAmount", new BigDecimal(Double.valueOf(taxAmount.toString())).toString());
			//					OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue))+taxAmount;
			//			}
			/*
			List<FactorRateRequestDetails> excessCon = coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getCoverageType().equalsIgnoreCase("B")).collect(Collectors.toList());
			if(!excessCon.isEmpty()) {
				excessCon.forEach(k -> {
					Map<String,Object> excessMap = new HashMap<String,Object>();
					excessMap.put("excessPercent", k.getExcessPercent());
					excessMap.put("excessAmount", k.getExcessAmount());
					excessMap.put("excessDesc", k.getExcessDesc());
					excessMap.put("currency", k.getCurrency());
					excessConDetails.add(excessMap);
				});
			}*/
			String policyType=map.get("policyType")==null?"":map.get("policyType").toString();
			String vehicelClass=map.get("vehicleClass")==null?"":map.get("vehicleClass").toString();
			List<ListItemValue>exesslist=listItemValueRepo.findByItemTypeAndParam1AndParam2AndStatus("POLICY_VEHICLE_EXCESS",policyType,vehicelClass,"Y");
			if(!CollectionUtils.isEmpty(exesslist)) {
				result.put("excessDesc", exesslist.get(0).getItemValue());
			}
			List<ListItemValue>limit=listItemValueRepo.findByItemTypeAndParam1AndParam2AndStatus("POLICY_LIMIT_LIABILITY","99999","99999","Y");
			if(!CollectionUtils.isEmpty(limit)) {
				result.put("limitDesc", limit.get(0).getItemValue());
			}
			List<ListItemValue>benifitlist=listItemValueRepo.findByItemTypeAndParam1AndParam2AndStatus("POLICY_FREE_BENIFIT",policyType,"99999","Y");
			if(!CollectionUtils.isEmpty(benifitlist)) {
				result.put("benifitDesc", benifitlist.get(0).getItemValue());
			}
			ListItemValue vehicle=listItemValueRepo.findByItemTypeAndItemCode("VEHICLE_CLASSES",vehicelClass);
			if(vehicle!=null) {
				result.put("vehicleClassDesc", vehicle.getItemValue());
			}
			result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
			result.put("requestReferenceNo", map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
			result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
			result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
			result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
			result.put("inceptionDate", map.get("inceptionDate")==null?null:map.get("inceptionDate").toString());
			result.put("expiryDate", map.get("expiryDate")==null?null:map.get("expiryDate").toString());
			result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
			result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
			result.put("address", map.get("address")==null?"":map.get("address").toString());
			result.put("vrTinNo", map.get("vrTinNo")==null?"":map.get("vrTinNo").toString());
			result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
			result.put("mobileNo1", map.get("mobileNo1")==null?"":map.get("mobileNo1").toString());
			result.put("companyLogo", map.get("companyLogo")==null?"":map.get("companyLogo").toString());
			result.put("brokerLogo", map.get("brokerLogo")==null?"":map.get("brokerLogo").toString());
			result.put("vehicleDetails", vehicleList);
			result.put("basePremiumDetails", premiumDetailsRes);
			result.put("addonPremiumDetails", premiumDetailsRes_1);
			result.put("taxDetails", taxDetails);
			result.put("premiumTotal", OverAllPremium);
			result.put("emiDetailsList", emiDetailsList);

		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private List<Tuple> getTaxDetails(String referenceNo, String quoteNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);

		Root<FactorRateRequestDetails> root = cq.from(FactorRateRequestDetails.class);
		//Root<PolicyCoverData> root = cq.from(PolicyCoverData.class);

		Expression<Long> sumTaxAmount = cb.sum(root.get("taxAmount"));
		cq.multiselect(root.get("taxId").alias("taxId"), root.get("taxRate").alias("taxRate"), sumTaxAmount.alias("taxAmount"),root.get("taxDesc").alias("taxDesc") );

		List<Predicate> predicate = new ArrayList<Predicate>();
		predicate.add(cb.equal(root.get("requestReferenceNo"), referenceNo));
		predicate.add(cb.notEqual(root.get("taxId"), 0));
		predicate.add(cb.equal(root.get("coverageType"), "T"));
		predicate.add(cb.or(cb.equal(root.get("isSelected"), "D"),cb.equal(root.get("isSelected"), "Y")));
		//if(StringUtils.isNotBlank(quoteNo))
		//predicate.add(cb.equal(root.get("quoteNo"), quoteNo));

		Predicate [] predicateArray = new Predicate[predicate.size()];
		predicate.toArray(predicateArray);
		cq.where(predicateArray);
		//cq.where(cb.equal(root.get("requestReferenceNo"), referenceNo),cb.notEqual(root.get("taxId"), 0),cb.equal(root.get("coverageType"), "T"),
		//cb.or(cb.equal(root.get("isSelected"), "D"),cb.equal(root.get("isSelected"), "Y")));

		cq.groupBy(root.get("taxId"), root.get("taxRate"),root.get("taxDesc"));

		List<Tuple>list=em.createQuery(cq).getResultList();
		return list;
	}

	public List<Map<String,Object>> getEwayPremiumRegister(PremiumReportReq req){
		log.info("Enter into EwayPremiumRegister || "+req.toString());
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpm = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> pif = cq.from(PersonalInfo.class);

			//BROKER_NAME
			Subquery<String> brokerName = cq.subquery(String.class);
			Root<LoginUserInfo> lui = brokerName.from(LoginUserInfo.class);
			brokerName.select(cb.upper(lui.get("userName"))).where(cb.equal(lui.get("loginId"), hpm.get("loginId")));

			/*	//PAYMENT_ID
			Subquery<Object> paymentIds = cq.subquery(Object.class);
			Root<PaymentInfo> pi = paymentIds.from(PaymentInfo.class);
			paymentIds.select(pi.get("paymentId")).where(cb.equal(pi.get("quoteNo"), hpm.get("quoteNo")),
					cb.equal(pi.get("paymentStatus"), "ACCEPTED"));
			paymentIds.setMaxResults(1); */

			//SECTION_NAME
			Subquery<String> sectionName = cq.subquery(String.class);
			Root<TravelPassengerDetails> tpd = sectionName.from(TravelPassengerDetails.class);
			sectionName.select(tpd.get("sectionName")).where(cb.equal(tpd.get("quoteNo"), hpm.get("quoteNo"))).distinct(true);

			//POLICY_TYPE_DESC
			Subquery<String> policyTypeDesc = cq.subquery(String.class);
			Root<MotorDataDetails> mdd = policyTypeDesc.from(MotorDataDetails.class);
			policyTypeDesc.select(mdd.get("policyTypeDesc")).where(cb.equal(tpd.get("quoteNo"), hpm.get("quoteNo")),
					cb.equal(mdd.get("vehicleId"), "1"));

			//POLICY_TYPE_NAME
			Subquery<String> policyTypeName = cq.subquery(String.class);
			Root<PolicyTypeMaster> ptm = policyTypeName.from(PolicyTypeMaster.class);

			Subquery<Integer> policyTypeAmd = policyTypeName.subquery(Integer.class);
			Root<PolicyTypeMaster> policyTypeAmdRoot = policyTypeAmd.from(PolicyTypeMaster.class);
			policyTypeAmd.select(cb.max(policyTypeAmdRoot.get("amendId"))).where(cb.equal(policyTypeAmdRoot.get("status"), ptm.get("status")),
					cb.equal(policyTypeAmdRoot.get("productId"), ptm.get("productId")),cb.equal(policyTypeAmdRoot.get("companyId"), ptm.get("companyId")));

			policyTypeName.select(ptm.get("policyTypeName")).where(cb.equal(ptm.get("status"), "Y"),
					cb.equal(ptm.get("productId"), hpm.get("productId")),cb.equal(ptm.get("companyId"), hpm.get("companyId")),
					cb.equal(ptm.get("amendId"), policyTypeAmd));

			//SUM_INSURED
			Subquery<BigDecimal> sumInsured = cq.subquery(BigDecimal.class);
			Root<PolicyCoverData> pcd = sumInsured.from(PolicyCoverData.class);
			sumInsured.select(cb.sum(pcd.get("sumInsured"))).where(cb.equal(pcd.get("quoteNo"), hpm.get("quoteNo")),
					cb.equal(pcd.get("taxId"), 0),cb.equal(pcd.get("discLoadId"), 0),cb.equal(pcd.get("coverageType"), "B"));

			//CURRENCY_ID
			Subquery<Tuple> currencyId = cq.subquery(Tuple.class);
			Root<InsuranceCompanyMaster> icm = currencyId.from(InsuranceCompanyMaster.class);
			currencyId.select(icm.get("currencyId")).where(cb.equal(hpm.get("companyId"), icm.get("companyId")));


			cq.multiselect(hpm.get("originalPolicyNo").alias("originalPolicyNo"),hpm.get("sourceType").alias("sourceType"),
					hpm.get("customerCode").alias("customerCode"),hpm.get("loginId").alias("loginId"),hpm.get("quoteNo").alias("quoteNo"),
					hpm.get("policyNo").alias("policyNo"),cb.upper(cb.concat(pif.get("titleDesc"), cb.concat(".", pif.get("clientName")))).alias("customerName"),
					hpm.get("inceptionDate").alias("startDate"),hpm.get("expiryDate").alias("endDate"),hpm.get("entryDate").alias("issueDate"),
					cb.upper(hpm.get("branchName")).alias("branchName"),cb.selectCase().when(cb.in(hpm.get("sourceType")).value(Arrays.asList("Premia Broker",
							"Premia Direct","Premia Agent")), hpm.get("customerName")).otherwise(brokerName).alias("brokerName"),
					hpm.get("userType").alias("userType"),hpm.get("subUserType").alias("subUserType"),hpm.get("currency").alias("currency"),hpm.get("paymentType").alias("paymentType"),
					hpm.get("productName").alias("productName"),cb.selectCase().when(cb.equal(hpm.get("productId"), 4), sectionName).when(cb.equal(hpm.get("productId"), 5), policyTypeDesc)
					.otherwise(policyTypeName).alias("policyTypeDesc"),hpm.get("debitNoteNo").alias("debitNoteNo"),sumInsured.alias("sumInsured"),
					cb.selectCase().when(cb.in(hpm.get("currency")).value(currencyId), cb.selectCase().when(cb.in(hpm.get("productId")).value(Arrays.asList(5,46)),
							icm)));
			log.info("Exit into EwayPremiumRegister");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String capitalizeFirstLetter(String str) {
		if (StringUtils.isNotBlank(str)) {
			return Arrays.stream(str.trim().split("\\s+"))
					.map(m -> Character.toUpperCase(m.charAt(0)) + m.substring(1).toLowerCase())
					.collect(Collectors.joining(" "));
		}
		return null;
	}

	public List<ApiDocListRes> getApiDocList(String quoteNo) {
		List<ApiDocListRes> resList = new ArrayList<>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<ApiDocDownloadDetail> aRoot = cq.from(ApiDocDownloadDetail.class);
			Root<HomePositionMaster> hRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> pRoot = cq.from(PersonalInfo.class);

			cq.multiselect(aRoot.get("sgsId").alias("sgsId"),aRoot.get("docName").alias("docName"),
					aRoot.get("docType").alias("docType"),aRoot.get("filePath").alias("filePath"),
					hRoot.get("policyNo").alias("policyNo"),hRoot.get("quoteNo").alias("quoteNo"),
					pRoot.get("clientName").alias("clientName"),aRoot.get("hasError").alias("hasError"))
			.where(cb.equal(aRoot.get("quoteNo"), hRoot.get("quoteNo")),cb.equal(hRoot.get("customerId"), pRoot.get("customerId")),
					cb.equal(hRoot.get("status"), "P"),cb.equal(hRoot.get("quoteNo"), quoteNo));

			TypedQuery<Tuple> r = em.createQuery(cq);

			List<Tuple> result = r.getResultList();
			if(result!=null && result.size()>0) {
				result.forEach(k -> {
					ApiDocListRes m = ApiDocListRes.builder()
							.customerName(k.get("clientName")==null?"":k.get("clientName").toString())
							.fileCode(k.get("sgsId")==null?"":k.get("sgsId").toString())
							.fileName(k.get("docName")==null?"":k.get("docName").toString())
							.policyNo(k.get("policyNo")==null?"":k.get("policyNo").toString())
							.quoteNo(k.get("quoteNo")==null?"":k.get("quoteNo").toString())
							.fileType(k.get("docType")==null?"":k.get("docType").toString())
							.filePath(k.get("filePath")==null?"":k.get("filePath").toString())
							.hasError(k.get("hasError")==null?"":k.get("hasError").toString())
							.build();
					resList.add(m);
				});
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

	public Map<String,Object> getCorporatePlusSchedule(String QuoteNo){
		log.info("Enter into CorporatePlusSchedule.\nArgument ==> "+QuoteNo);
		Map<String,Object> result = new HashMap<String,Object>();
		List<AttachMentRes> attachments = new ArrayList<>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
			Root<PersonalInfo> piRoot = cq.from(PersonalInfo.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginBranchMaster> lbmRoot= cq.from(LoginBranchMaster.class);

			Subquery<Integer> cmAmd = cq.subquery(Integer.class);
			Root<CountryMaster> cmAmdRoot = cmAmd.from(CountryMaster.class);
			cmAmd.select(cb.max(cmAmdRoot.get("amendId"))).where(cb.equal(cmAmdRoot.get("countryId"), piRoot.get("nationality")),cb.equal(cmAmdRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(cmAmdRoot.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> ScmRoot = countryName.from(CountryMaster.class);
			countryName.select(ScmRoot.get("countryName")).where(cb.equal(ScmRoot.get("countryId"), piRoot.get("nationality")),cb.equal(ScmRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(ScmRoot.get("status"), "Y"),cb.equal(ScmRoot.get("amendId"), cmAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(hpmRoot.get("companyId"), icmAmdRoot.get("companyId")),cb.equal(icmAmdRoot.get("status"), "Y"),
					cb.between(cb.literal(new Date()), icmAmdRoot.get("effectiveDateStart"), icmAmdRoot.get("effectiveDateEnd")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("requestReferenceNo").alias("requestReferenceNo"),cb.concat(piRoot.get("titleDesc"),
					cb.concat(cb.selectCase().when(cb.isNotNull(piRoot.get("titleDesc")), ".").when(cb.equal(piRoot.get("titleDesc"),""), "")
							.otherwise("").as(String.class), piRoot.get("clientName"))).alias("customerName"),
					cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "")
							.when(cb.equal(piRoot.get("pinCode"), ""), "").otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), cb.concat(",", cb.concat(piRoot.get("cityName"),
									cb.concat(",", countryName)))))))).alias("address"),piRoot.get("email1").alias("email1"),hpmRoot.get("branchCode").alias("branchCode"),luiRoot.get("agencyCode").alias("agencyCode"),
					hpmRoot.get("inceptionDate").alias("inceptionDate"),hpmRoot.get("expiryDate").alias("expiryDate"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("brokerBranchName").alias("brokerBranchName"),
					hpmRoot.get("productName").alias("productName"),piRoot.get("stateName").alias("stateName"),piRoot.get("cityName").alias("cityName"),cb.concat(piRoot.get("mobileCodeDesc1"), cb.concat("-", piRoot.get("mobileNo1"))).alias("mobileNo"),
					piRoot.get("customerId").alias("customerId"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Agent","Premia Direct","Premia Broker")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("brokerName"),luiRoot.get("coreAppBrokerCode").alias("coreAppBrokerCode"),hpmRoot.get("currency").alias("currency"),hpmRoot.get("vatPercent").alias("vatPercent"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("premiumLc")).otherwise(hpmRoot.get("premiumFc")).alias("premium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("vatPremiumLc")).otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),
					icmRoot.get("signature").alias("signature"),lbmRoot.get("branchName").alias("place"),companyName.alias("companyName"),imageURL.alias("companylogo"),hpmRoot.get("companyId").alias("companyId"),hpmRoot.get("productId").alias("productId"),
					hpmRoot.get("debitNoteNo").alias("debitNoteNo"),luiRoot.get("userMobile").alias("userMobile"),hpmRoot.get("bdmCode").alias("bdmCode"),hpmRoot.get("bdmName").alias("bdmName"),
					cb.selectCase().when(cb.equal(hpmRoot.get("applicationId"), "1"), hpmRoot.get("loginId")).otherwise(hpmRoot.get("applicationId")).alias("preparedBy"))
			.where(cb.equal(hpmRoot.get("customerId"), piRoot.get("customerId")),cb.equal(hpmRoot.get("agencyCode").as(String.class), luiRoot.get("agencyCode")),cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(hpmRoot.get("loginId"), lbmRoot.get("loginId")),cb.equal(hpmRoot.get("companyId"), lbmRoot.get("companyId")),cb.equal(hpmRoot.get("branchCode"), lbmRoot.get("branchCode")),cb.equal(lbmRoot.get("status"), "Y"),
					cb.equal(icmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), icmRoot.get("effectiveDateStart"), icmRoot.get("effectiveDateEnd")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("quoteNo"), QuoteNo));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<PolicyCoverData> coverData = coverDataRepository.findByQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());

				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<PolicyCoverData> pcdRoot = cq1.from(PolicyCoverData.class);
				Root<SectionDataDetails> sddRoot = cq1.from(SectionDataDetails.class);

				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),map.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("quoteNo"),sddRoot.get("quoteNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("locationId"), sddRoot.get("locationId")));
				predicate.add(cb.equal(pcdRoot.get("coverId"),sddRoot.get("coverId")));

				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);

				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")),cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")),cb.equal(pcdRoot.get("locationId"), ecdRoot.get("locationId")),
						cb.equal(pcdRoot.get("coverId"),ecdRoot.get("coverId")));

				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionDesc").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),sddRoot.get("coverNoteReferenceNo").alias("coverNoteReferenceNo"),
						pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
						pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"),
						sddRoot.get("locationId").alias("locationId"),sddRoot.get("locationName").alias("locationName"),sddRoot.get("productType").alias("productType"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));

				List<Tuple> Slist = em.createQuery(cq1).getResultList();
				List<Map<String,Object>>sectList=new ArrayList<>();
				Double minAdjPrem=0.0,minAdjPremFc=0.0,basePremium=0.0,basePremiumFc=0.0,
						minAdjExPrem=0.0,minAdjExPremFc=0.0,baseExPremium=0.0,baseExPremiumFc=0.0;

				for (int i=0;i<Slist.size();i++) {
					Tuple t=Slist.get(i);
					String coverId = t.get("coverId")==null?"":t.get("coverId").toString();
					String coverageType = t.get("coverageType")==null?"":t.get("coverageType").toString();
					if("945".equals(coverId)){
						minAdjPrem=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						minAdjPremFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						minAdjExPrem=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						minAdjExPremFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if("B".equals(coverageType)) {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
						basePremium=basePremium+minAdjPrem;
						basePremiumFc=basePremiumFc+minAdjPremFc;
						baseExPremium=baseExPremium+minAdjExPrem;
						baseExPremiumFc=baseExPremiumFc+minAdjExPremFc;
					}else {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if(!"945".equals(coverId)){
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", t.get("sectionDesc"));
						Smap.put("occupationDesc", t.get("occupationDesc"));
						Smap.put("coverDesc", t.get("coverDesc"));
						Smap.put("sumInsured", t.get("sumInsured"));
						Smap.put("rate", t.get("rate"));
						Smap.put("premiumIncludedTaxLc", basePremium);
						Smap.put("premiumIncludedTaxFc", basePremiumFc);
						Smap.put("premiumExcludedTaxLc", baseExPremium);
						Smap.put("premiumExcludedTaxFc", baseExPremiumFc);
						sectList.add(Smap);
					}
				}
				List<Map<String,Object>> sectionList = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> coverageDetails = new ArrayList<Map<String,Object>>();
				List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
				Double OverAllPremium=0.0;
				String companyId = map.get("companyId")==null?"":map.get("companyId").toString();
				List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
				if("100002".equalsIgnoreCase(companyId) || Arrays.asList("100050","100049","100048","100047","100046").contains(companyId)) {
					if(coverData!=null && !coverData.isEmpty()) {
						Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
								.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
								.findAny().orElse(0.0);

						Double taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
								.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));

						List<Map<String, Object>> sectionPremium = Slist.stream()
								.filter(g -> !Arrays.asList(217,218).contains(Integer.parseInt(g.get("sectionId").toString())))
								.map(m -> {
									Map<String, Object> u = new HashMap<>();
									u.put("sectionId", m.get("sectionId").toString());
									u.put("sectionDesc", m.get("sectionDesc").toString());
									return u;
								}).distinct()
								.map(l -> {
									Map<String, Object> fMap = new HashMap<>();
									String sectionIdStr = l.get("sectionId")== null?"":l.get("sectionId").toString();
									String sectionDesc = l.get("sectionDesc")== null?"":l.get("sectionDesc").toString();
									fMap.put("SectionId", Integer.parseInt(sectionIdStr));
									fMap.put("CoverDesc", sectionDesc);
									if(Integer.parseInt(sectionIdStr)==40) {
										double totPremium = coverData.stream()
												.filter(f -> f.getTaxId() == 0 && f.getDiscLoadId() == 0 
												&& (Arrays.asList(217,218,Integer.parseInt(sectionIdStr)).contains(f.getSectionId())))
												.mapToDouble(f -> f.getPremiumExcludedTaxFc().doubleValue())
												.sum();
										fMap.put("TotPremium", totPremium);
									}else {
										double totPremium = coverData.stream()
												.filter(f -> f.getTaxId() == 0 && f.getDiscLoadId() == 0 
												&& f.getSectionId() == Integer.parseInt(sectionIdStr))
												.mapToDouble(f -> f.getPremiumExcludedTaxFc().doubleValue())
												.sum();
										fMap.put("TotPremium", totPremium);
									}
									return fMap;
								}).sorted((p1, p2) -> {
									int sectionId1 = (Integer) p1.get("SectionId");
									int sectionId2 = (Integer) p2.get("SectionId");

									if (sectionId1 == 40) {
										return -1;
									}
									if (sectionId2 == 40) {
										return 1;
									}
									if (sectionId1 == 75) {
										return -1;
									}
									if (sectionId2 == 75) {
										return 1;
									}
									return Integer.compare(sectionId1, sectionId2);
								})
								.collect(Collectors.toList());

						double addonPremium = sectionPremium.stream().filter(f -> Arrays.asList(217,218).contains(f.get("SectionId")))
								.map(m -> new BigDecimal(m.get("TotPremium").toString())).collect(Collectors.summingDouble(BigDecimal::doubleValue));

						long count_1 = sectionPremium.stream().anyMatch(f -> f.get("SectionId").toString().equalsIgnoreCase("217"))?1:0;
						if(count_1>0)
							sectionPremium.removeIf(r -> r.get("SectionId").toString().equalsIgnoreCase("218"));

						long count_2 = sectionPremium.stream().anyMatch(f -> f.get("SectionId").toString().equalsIgnoreCase("218"))?1:0;
						if(count_2>0)
							sectionPremium.removeIf(r -> r.get("SectionId").toString().equalsIgnoreCase("217"));


						/*			List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999)
								.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
									BigDecimal.ZERO, PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
								.entrySet().stream()
								.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
									Integer sectionId = s.getKey();
									return s.getValue().entrySet().stream()
											.map((Map.Entry<String,BigDecimal> g )-> {
												String coverDesc = g.getKey();
												BigDecimal totPremium = g.getValue();
												Map<String,Object> secMap = new HashMap<String,Object>();
												secMap.put("SectionId", sectionId);
												secMap.put("CoverDesc", coverDesc);
												secMap.put("TotPremium", totPremium);
												return secMap;
											});
								}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());*/
						sectionPremium.forEach(k -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(Arrays.asList(217,218).contains(k.get("SectionId"))?
											new BigDecimal(addonPremium).toString():
												new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
									.narration(
											Arrays.asList(217,218).contains(k.get("SectionId"))?
													"Add On Covers":
														k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
									.build();
							premiumDetailsRes.add(u);
						});

						OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue))+taxAmount;
						result.put("vatPercent", taxRate.toString());
						result.put("vatAmount", taxAmount.toString());
					}
				}else if("100004".equalsIgnoreCase(companyId)){
					sectList.forEach(k -> {
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", k.get("sectionDesc"));
						Smap.put("coverDesc", k.get("coverDesc"));
						Smap.put("sumInsured", k.get("sumInsured"));
						Smap.put("rate", k.get("rate"));
						Smap.put("premiumIncludedTaxLc", k.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", k.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", k.get("premiumExcludedTaxLc"));
						Smap.put("premiumExcludedTaxFc", k.get("premiumExcludedTaxFc"));
						Smap.put("vehicleId",k.get("vehicleId"));
						sectionList.add(Smap);
						result.put("occupationDesc", sectList.stream().filter(f -> f.get("occupationDesc") != null).map(m -> m.get("occupationDesc"))
								.map(Object::toString).findAny().orElse(null));
					});
				}else {
					Map<Object, List<Map<String,Object>>> sectionRes = sectList.stream().collect(Collectors.groupingBy(g -> g.get("sectionDesc"),Collectors.mapping(v ->{
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("occupationDesc", v.get("occupationDesc"));
						Smap.put("coverDesc", v.get("coverDesc"));
						Smap.put("sumInsured", v.get("sumInsured"));
						Smap.put("rate", v.get("rate"));
						Smap.put("premiumIncludedTaxLc", v.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", v.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", v.get("premiumExcludedTaxLc"));
						Smap.put("premiumExclhitudedTaxFc", v.get("premiumExcludedTaxFc"));
						return Smap;
					}, Collectors.toList())));			
					for(Map.Entry<Object, List<Map<String,Object>>> entry :sectionRes.entrySet()) {
						Map<String, Object> sectionMap = new HashMap<String, Object>();
						sectionMap.put("sectionKey", entry.getKey());
						sectionMap.put("sectionValue", entry.getValue());
						sectionList.add(sectionMap);
					}
				}

				List<Object> locationIds = Slist.stream().map(k -> k.get("locationId")).distinct().collect(Collectors.toList());
				List<Map<String,Object>> coverageList = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> addOnDetails = new ArrayList<>();
				for(int i=0;i<sectionIds.size();i++) {
					Map<String,Object> coverMap = new HashMap<String,Object>();
					List<Map<String,Object>> buildingDetails = new ArrayList<>();
					List<Map<String,Object>> interruptionDetails = new ArrayList<>();
					List<Map<String,Object>> burglaryDetails = new ArrayList<>();
					List<Map<String,Object>> moneyDetails = new ArrayList<>();
					List<Map<String,Object>> officeContDetails = new ArrayList<>();
					List<Map<String,Object>> contentDetails = new ArrayList<>();
					List<Map<String,Object>> occupationDetails = new ArrayList<>();
					List<Map<String,Object>> goodsAndtransitDtls = new ArrayList<>();
					List<Map<String,Object>> categoryDetails= new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> excessConDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> erectionDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> machnieryDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> contractorsDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> alliedPerilsDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> indAccDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> carrierDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> plantAllRiskDetails = new ArrayList<Map<String,Object>>();
					List<MarineHullInfo> marineHullDetails = new ArrayList<MarineHullInfo>();
					List<Map<String,Object>> aviationDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> bondDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> bankersDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> bussinessAllRiskDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> cyberCrimeDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> deteriorationOfStockDetails = new ArrayList<Map<String,Object>>();
					String sectionId = sectionIds.get(i).toString();
					if(!Arrays.asList("218","217").contains(sectionId)) {
						for(int x=0;x<locationIds.size();x++) {
							String locationId = locationIds.get(x).toString();
							String locationName = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))).map(r -> r.get("locationName").toString()).findFirst().get();
							/*String productType = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))
								&& f.get("sectionId").equals(sectionId)).map(t -> t.get("productType")).map(Object::toString).findFirst().orElse("");*/
							List<BuildingRiskDetails> buildingRiskData = buildingRiskDetailsRepo.findByQuoteNoAndSectionIdAndLocationId(map.get("quoteNo").toString(),sectionId,Integer.parseInt(locationId));
							if("40".equalsIgnoreCase(sectionId)) { // building
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("assetType", k.getBuildingUsageDesc());
									lmap.put("wallType", k.getWallTypeDesc());
									lmap.put("roofType", k.getRoofTypeDesc());
									lmap.put("descriptionOfRisk", k.getDescriptionOfRisk());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								buildingDetails.addAll(locationList);

								List<Integer> coverids = coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && Arrays.asList(217,218).contains(f.getSectionId()))
										.map(m -> m.getCoverageType().equalsIgnoreCase("L")?m.getDiscLoadId():m.getCoverId()).distinct()
										.collect(Collectors.toList());
								for(int j=0;j<coverids.size();j++) {
									int c = coverids.get(j);
									Map<String,Object> o = new HashMap<String,Object>();
									o.put("locationName", locationName);
									o.put("sectionId", sectionId);
									o.put("description", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && Arrays.asList(217,218).contains(f.getSectionId())
											&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
											.map(m -> m.getCoverName()).findFirst().orElse("-N-A"));
									o.put("sumInsured", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && Arrays.asList(217,218).contains(f.getSectionId())
											&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
											.map(m -> m.getSumInsured()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									o.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									o.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& Arrays.asList(217,218).contains(f.getSectionId()))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									o.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									addOnDetails.add(o);
								}
							}else if("75".equalsIgnoreCase(sectionId)) { // Business Interruption (Fire & Allied Perils)
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("indemnityPeriod", k.getIndemityPeriodDesc());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								interruptionDetails.addAll(locationList);
							}else if("52".equalsIgnoreCase(sectionId)) { // BURGLARY/THEFT
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("firstlosspercent", k.getFirstLossPercent()==null?"":k.getFirstLossPercent()+"%");
									lmap.put("descriptionOfRisk", k.getDescriptionOfRisk());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								burglaryDetails.addAll(locationList);
							}else if("42".equalsIgnoreCase(sectionId)) { //Money
								List<PolicyCoverData> moneyList = coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
										&& f.getSectionId()==Integer.parseInt(sectionId))
										.collect(Collectors.toList());
								if(moneyList!=null && moneyList.size()>0) {
									moneyList.forEach(k -> {
										LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
										lmap.put("locationName", locationName);	
										lmap.put("coverName", k.getCoverName());
										lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
										lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
										lmap.put("sumInsured", k.getSumInsured());
										lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
												&& f.getSectionId()==Integer.parseInt(sectionId))
												.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										moneyDetails.add(lmap);
									});
								}
							}else if(Arrays.asList("198","56").contains(sectionId)) { //Office Contents
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("contentDesc", k.getContentDesc());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								officeContDetails.addAll(locationList);
							}else if(Arrays.asList("69","53","76").contains(sectionId)) { 
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("contentDesc", k.getContentDesc());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								contentDetails.addAll(locationList);
							}else if(Arrays.asList("43","182").contains(sectionId)) {
								List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
								List<Map<String,Object>> commonList = comDetails.stream()
										.collect(Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(o -> {
											LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
											empMap.put("locationName", locationName);
											empMap.put("occupationDesc", o.getOccupationDesc());
											empMap.put("sumInsured", o.getSumInsured());
											empMap.put("count", o.getCount());
											empMap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
													&& f.getSectionId()==Integer.parseInt(sectionId)
													&& f.getVehicleId()==o.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
											return empMap;
										}, Collectors.toList()))).entrySet()
										.stream().map(g -> {
											LinkedHashMap<String,Object> eMap = new LinkedHashMap<String,Object>();
											eMap.put("occupationDesc", g.getValue().stream().map(t -> String.valueOf(t.get("occupationDesc"))).collect(Collectors.joining("<br>")));
											eMap.put("sumInsured", g.getValue().stream().map(j -> (BigDecimal) j.get("sumInsured")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
											eMap.put("premium", g.getValue().stream().map(h -> h.get("premium")).findFirst().get());
											eMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
													.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
											eMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
											eMap.put("locationName", g.getValue().stream().map(j -> j.get("locationName")).findFirst().get());
											eMap.put("count", g.getValue().stream().map(j -> j.get("count")).findFirst().get());
											return eMap;
										}).collect(Collectors.toList());
								occupationDetails.addAll(commonList);
							}else if("46".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("commodity", k.getOccupationTypeDesc());
									lmap.put("limitPerTrip", k.getContentDesc());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("goodsPackage", k.getCategoryDesc());
									lmap.put("covertypeDesc", k.getBuildingUsageDesc());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								goodsAndtransitDtls.addAll(locationList);
							}else if("233".equalsIgnoreCase(sectionId) || "232".equalsIgnoreCase(sectionId)) {
								List<EngineerInfo> engineeringinfo = engineerRepo.findByQuoteNo(QuoteNo);
								engineeringinfo.forEach(k -> {
									if(locationId.equalsIgnoreCase(k.getLocationId().toString())) {
										LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
										lmap.put("sectionId", sectionId);
										lmap.put("principal", k.getPrincipalOwner());
										lmap.put("constructionDesc", k.getDescription());
										lmap.put("constructionSite", k.getLocationName());
										lmap.put("constructionDate", k.getStartDate()==null?null:sdf.format(k.getStartDate()));
										lmap.put("periodOfActivity", k.getPeriodOfActivity());
										lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
										lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
												&& f.getSectionId()==Integer.parseInt(sectionId))
												.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										lmap.put("sumInsured", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
												&& f.getSectionId()==Integer.parseInt(sectionId))
												.map(u -> u.getSumInsured()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
										erectionDetails.add(lmap);
									}
								});

								if("232".equalsIgnoreCase(sectionId)) { 
									List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
										LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
										lmap.put("sectionId", sectionId);
										lmap.put("locationName", k.getLocationName());
										lmap.put("constructionType", k.getCategoryDesc());
										lmap.put("noofConst", k.getBuildingFloors());
										lmap.put("maintenancePeriod", k.getDescriptionOfRisk());
										lmap.put("noofMonths", k.getBuildingAge());
										lmap.put("sumInsured", k.getSumInsured());
										lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
										lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
												&& f.getSectionId()==Integer.parseInt(sectionId))
												.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
										return lmap;
									}).collect(Collectors.toList());
									contractorsDetails.addAll(locationList);
								}

							}else if(Integer.parseInt(sectionId) >= 252 && Integer.parseInt(sectionId) <= 267) {
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("commodity", k.getCategoryDesc());
									lmap.put("description", k.getDescriptionOfRisk());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("origiantionCountry", k.getGeographicalCoverage());
									lmap.put("destinationCountry", k.getModeOfTransport());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								goodsAndtransitDtls.addAll(locationList);
							}else if("41".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", locationName);
									lmap.put("description", k.getDescriptionOfRisk());
									lmap.put("machinery", k.getCategoryDesc());
									lmap.put("industry", k.getIndustryDesc());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("coveringDetails", k.getCoveringDetails());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								machnieryDetails.addAll(locationList);
							}else if("183".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", locationName);
									lmap.put("description", k.getDescriptionOfRisk());
									lmap.put("businessInterruption", k.getSectionDesc());
									lmap.put("industry", k.getIndustryDesc());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("coveringDetails", k.getCoveringDetails());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								interruptionDetails.addAll(locationList);
							}else if(Arrays.asList("32","33","107","108","109","110","111","112","113","114","115","116").contains(sectionId)) {
								if(Arrays.asList("107","110","113").contains(sectionId)) {
									List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
										LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
										lmap.put("locationName", locationName);
										lmap.put("insuranceType", k.getIndustryDesc());
										lmap.put("occupation", k.getOccupationTypeDesc());
										lmap.put("industryCover", k.getSectionDesc());
										lmap.put("description", k.getDescriptionOfRisk());
										lmap.put("sumInsured", k.getSumInsured());
										lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
										lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
												&& f.getSectionId()==Integer.parseInt(sectionId))
												.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
										return lmap;
									}).collect(Collectors.toList());
									alliedPerilsDetails.addAll(locationList);
								}else {
									List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
										LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
										lmap.put("locationName", locationName);
										lmap.put("businessInterruption", k.getSectionDesc());
										lmap.put("description", k.getDescriptionOfRisk());
										lmap.put("sumInsured", k.getSumInsured());
										lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
										lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
												&& f.getSectionId()==Integer.parseInt(sectionId))
												.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
										lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
										return lmap;
									}).collect(Collectors.toList());
									interruptionDetails.addAll(locationList);
								}
							}else if("35".equalsIgnoreCase(sectionId)) {
								List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
								List<Map<String,Object>> locationList = comDetails.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", locationName);
									lmap.put("coverName", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId) && f.getCoverId()==k.getCoverId())
											.map(q -> q.getCoverName()).findFirst().orElse(""));
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("occupation", k.getOccupationDesc());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								indAccDetails.addAll(locationList);
							}else if("272".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", locationName);
									lmap.put("description", k.getDescriptionOfRisk());
									lmap.put("territorialLimits", k.getGeographicalCoverage());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("coverName", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId) && f.getCoverId().equals(k.getCoverId()))
											.map(q -> q.getCoverName()).findFirst().orElse(""));
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								carrierDetails.addAll(locationList);
							}else if("80".equalsIgnoreCase(sectionId)) { // plant All Risk
								List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", locationName);
									lmap.put("description", k.getDescriptionOfRisk());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								plantAllRiskDetails.addAll(locationList);
							}else if("286".equalsIgnoreCase(sectionId)) {
								List<MarineHullInfo> marineInfo = marineHullInfoRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
								marineHullDetails.addAll(marineInfo.stream().filter(f -> f.getSectionId().toString().equalsIgnoreCase(sectionId)).collect(Collectors.toList()));
							}else if(Arrays.asList("120","118").contains(sectionId)) {
								List<Map<String,Object>> bondList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("address", k.getAddress());
									lmap.put("sectionId", sectionId);
									lmap.put("sectiondesc", k.getSectionDesc());
									lmap.put("industrydesc", k.getIndustryDesc());
									lmap.put("bondyear", k.getBondYear());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("coveringdetails", k.getCoveringDetails());
									lmap.put("descriptionofrisk", k.getDescriptionOfRisk());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								
								bondDetails.addAll(bondList);
							}else if("286".equalsIgnoreCase(sectionId)) {
								List<MarineHullInfo> marineInfo = marineHullInfoRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
								marineHullDetails.addAll(marineInfo.stream().filter(f -> f.getSectionId().toString().equalsIgnoreCase(sectionId)).collect(Collectors.toList()));
							}else if("291".equalsIgnoreCase(sectionId)) {
								List<AviationInfo> aviationInfo = aviationInfoRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
								List<Map<String,Object>> aviationList = aviationInfo.stream()
										.filter(f -> f.getSectionId().toString().equalsIgnoreCase(sectionId)).map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("ownerName", k.getOwnerName());
									lmap.put("aircraftName", k.getAircraftName());
									lmap.put("type", k.getType());
									lmap.put("registrationNumber", k.getRegistrationNumber());
									lmap.put("manufactureYear", k.getManufactureYear());
									lmap.put("make", k.getMake());
									lmap.put("model", k.getModel());
									lmap.put("usageDesc", k.getUsageDesc());
									lmap.put("geographicalLimit", k.getGeographicalLimit());
									lmap.put("engineType", k.getEngineType());
									lmap.put("noOfEngines", k.getNoOfEngines());
									lmap.put("weight", k.getWeight());
									lmap.put("nightFlight", k.getNightFlight());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								aviationDetails.addAll(aviationList);
							}else if("288".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> bankersList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("titleBank", k.getParam1());
									lmap.put("headOffice", k.getParam2());
									lmap.put("establishment", k.getParam3());
									lmap.put("typeBank", k.getParam4());
									lmap.put("noOfDirectors", k.getParam5());
									lmap.put("noOfBankingDuties", k.getParam6());
									lmap.put("noOfNonBankingDuties", k.getParam7());
									lmap.put("noOfLocation", k.getParam8());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								bankersDetails.addAll(bankersList);
							}else if("223".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> bussinessAllriskList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("address", k.getAddress());
									lmap.put("sectiondesc", k.getSectionDesc());
									lmap.put("industrydesc", k.getIndustryDesc());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("descriptionofrisk", k.getDescriptionOfRisk());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								bussinessAllRiskDetails.addAll(bussinessAllriskList);
							}else if("289".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> cyberCrimeList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("address", k.getAddress());
									lmap.put("sectiondesc", k.getSectionDesc());
									lmap.put("industrydesc", k.getOccupationTypeDesc());
									lmap.put("limitPerOccurance", k.getParam1());
									lmap.put("remarks", k.getOccupationTypeDesc());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								cyberCrimeDetails.addAll(cyberCrimeList);
							}else if("226".equalsIgnoreCase(sectionId)) {
								List<Map<String,Object>> deteriorationOfStockList = buildingRiskData.stream().map(k ->{
									LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
									lmap.put("locationName", k.getLocationName());
									lmap.put("address", k.getAddress());
									lmap.put("sectiondesc", k.getSectionDesc());
									lmap.put("industrydesc", k.getIndustryDesc());
									lmap.put("stockType", k.getParam1());
									lmap.put("coverageDescription", k.getDescriptionOfRisk());
									lmap.put("maintainedTemperature", k.getDescriptionOfRisk());
									lmap.put("quantityOfStock", k.getDescriptionOfRisk());
									lmap.put("sumInsured", k.getSumInsured());
									lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId))
											.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
											.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
									return lmap;
								}).collect(Collectors.toList());
								deteriorationOfStockDetails.addAll(deteriorationOfStockList);
							}
							else {
								List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
								List<Map<String,Object>> commonList = comDetails.stream()
										.collect(Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(o -> {
											LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
											empMap.put("locationName", locationName);
											empMap.put("categoryDesc", o.getCategoryDesc());
											empMap.put("sumInsured", o.getSumInsured());
											empMap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
													&& f.getSectionId()==Integer.parseInt(sectionId)
													&& f.getVehicleId()==o.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
											return empMap;
										}, Collectors.toList()))).entrySet()
										.stream().map(g -> {
											LinkedHashMap<String,Object> eMap = new LinkedHashMap<String,Object>();
											eMap.put("categoryDesc", g.getValue().stream().map(t -> String.valueOf(t.get("categoryDesc"))).collect(Collectors.joining("<br>")));
											eMap.put("sumInsured", g.getValue().stream().map(j -> (BigDecimal) j.get("sumInsured")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
											eMap.put("premium", g.getValue().stream().map(h -> h.get("premium")).findFirst().get());
											eMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
													.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
											eMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
											eMap.put("locationName", g.getValue().stream().map(j -> j.get("locationName")).findFirst().get());
											return eMap;
										}).collect(Collectors.toList());
								categoryDetails.addAll(commonList);
							}
						}
						// CONDITIONS
						List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						// EXCLUSION
						List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
						List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
							Map<String,Object> eMap = new HashMap<String,Object>();
							eMap.put("conditionTerms", k.get("exclusioTerms"));
							eMap.put("SectionId", k.get("SectionId"));
							eMap.put("Sno", k.get("Sno"));
							return eMap;
						}).collect(Collectors.toList());

						//WARRANTY
						List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList,conditionList,exclusionList).flatMap(Collection::stream)
								.sorted(Comparator.comparing(p -> {
									if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
										return Integer.MAX_VALUE;
									}
									try {
										return Integer.parseInt(p.get("Sno").toString());
									} catch (NumberFormatException e) {
										return Integer.MAX_VALUE;
									}
								}))
								.map(u -> {
									LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
									m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
									return m;
								}).distinct().collect(Collectors.toList());

						int conditionsize = termsAndconditions.size();
						List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
						if(conditionsize>10) {
							int midIndex = conditionsize / 2;
							firstHalf = termsAndconditions.subList(0, midIndex);
							secondHalf = termsAndconditions.subList(midIndex, conditionsize);
						}else {
							firstHalf = termsAndconditions.subList(0, conditionsize);
						}

						List<PolicyCoverData> excessCon = coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getCoverageType().equalsIgnoreCase("B") && f.getSectionId()==Integer.parseInt(sectionId)).collect(Collectors.toList());
						if(!excessCon.isEmpty()) {
							excessCon.forEach(k -> {
								Map<String,Object> excessMap = new HashMap<String,Object>();
								excessMap.put("excessPercent", k.getExcessPercent());
								excessMap.put("excessAmount", k.getExcessAmount());
								excessMap.put("excessDesc", k.getExcessDesc());
								excessMap.put("currency", k.getCurrency());
								excessConDetails.add(excessMap);
							});
						}

						coverMap.put("sectionDesc", Slist.stream().filter(k -> sectionId.equalsIgnoreCase(k.get("sectionId").toString())).map(e -> e.get("sectionDesc").toString()).findFirst().orElse(""));
						coverMap.put("firstHalfconditions", firstHalf);
						coverMap.put("secondHalfconditions", secondHalf);
						coverMap.put("addOnDetails", addOnDetails);
						coverMap.put("buildingDetails", buildingDetails);
						coverMap.put("burglaryDetails", burglaryDetails);
						coverMap.put("interruptionDetails", interruptionDetails);
						coverMap.put("goodsAndtransitDtls", goodsAndtransitDtls);
						coverMap.put("moneyDetails", moneyDetails);
						coverMap.put("officeContDetails", officeContDetails);
						coverMap.put("contentDetails", contentDetails);
						coverMap.put("occupationDetails", occupationDetails);
						coverMap.put("categoryDetails", categoryDetails);
						coverMap.put("erectionDetails", erectionDetails);
						coverMap.put("machnieryDetails", machnieryDetails);
						coverMap.put("contractorsDetails", contractorsDetails);
						coverMap.put("alliedPerilsDetails", alliedPerilsDetails);
						coverMap.put("indAccDetails", indAccDetails);
						coverMap.put("carrierDetails", carrierDetails);
						coverMap.put("plantAllRiskDetails", plantAllRiskDetails);
						coverMap.put("marineHullDetails", marineHullDetails);
						coverMap.put("bondDetails", bondDetails);
						coverMap.put("aviationDetails", aviationDetails);
						coverMap.put("bankersDetails", bankersDetails);
						coverMap.put("bussinessAllRiskDetails", bussinessAllRiskDetails);
						coverMap.put("cyberCrimeDetails", cyberCrimeDetails);
						coverMap.put("deteriorationOfStockDetails", deteriorationOfStockDetails);
						coverMap.put("sectionId", sectionId);
						coverageList.add(coverMap);
					}
				}

				Map<Object,List<Map<String,Object>>> groupBycoverageDetails = coverageList.stream()
						.collect(Collectors.groupingBy(k -> k.get("sectionDesc"), Collectors.toList()));
				for(Map.Entry<Object, List<Map<String,Object>>> CDEntry : groupBycoverageDetails.entrySet()) {
					LinkedHashMap<String, Object> coverMap = new LinkedHashMap<String, Object>();
					coverMap.put("coverId", Slist.stream().filter(f -> f.get("sectionDesc").equals(CDEntry.getKey())).map(m -> m.get("sectionId")).findFirst().orElse(""));
					coverMap.put("coverKey", CDEntry.getValue().stream()
							.filter(e -> Arrays.asList(108, 109, 114, 115, 33, 111).contains(Integer.parseInt(e.get("sectionId").toString())))
							.map(e -> "BUSINESS INTERRUPTION (" + CDEntry.getKey().toString() + ")".toUpperCase()+ " " +(map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE"))
							.findFirst()
							.orElseGet(() -> CDEntry.getValue().stream()
									.filter(e -> Arrays.asList(217,218).contains(Integer.parseInt(e.get("sectionId").toString())))
									.map(e -> "ADD ON COVERS"+" " +(map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE"))
									.findFirst().orElse(CDEntry.getKey().toString().toUpperCase() + " " + (map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE"))));
					coverMap.put("coverValue", CDEntry.getValue());
					coverMap.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
					coverMap.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
					coverMap.put("productId", map.get("productId")==null?"":map.get("productId").toString());
					coverMap.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
					coverMap.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
					coverMap.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
					coverageDetails.add(coverMap);
				}

				long count_1 = coverageDetails.stream().anyMatch(f -> f.get("coverId").toString().equalsIgnoreCase("217"))?1:0;
				if(count_1>0)
					coverageDetails.removeIf(r -> r.get("coverId").toString().equalsIgnoreCase("218"));

				long count_2 = coverageDetails.stream().anyMatch(f -> f.get("coverId").toString().equalsIgnoreCase("218"))?1:0;
				if(count_2>0)
					coverageDetails.removeIf(r -> r.get("coverId").toString().equalsIgnoreCase("217"));



				List<EserviceBuildingDetails> buildingDtl = eserviceBuildingDetailsRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(QuoteNo, "Y");
				String buildingOwnerYn = buildingDtl.isEmpty()?"":buildingDtl.get(0).getBuildingOwnerYn()==null?"":buildingDtl.get(0).getBuildingOwnerYn();
				List<Map<String,Object>> domesticKeyFactor = listItemValueRepo.getDomesticKeyFactor(buildingOwnerYn.equalsIgnoreCase("Y")?"1":"2",companyId);
				if(!domesticKeyFactor.isEmpty()) {
					String attachmentloc = this.getClass().getClassLoader().getResource("").getPath().replaceAll("%20", "")+"report/attachments/";
					domesticKeyFactor.forEach(k->{
						AttachMentRes a = AttachMentRes.builder()
								.docRefNo(k.get("ITEM_CODE")==null?"":k.get("ITEM_CODE").toString())
								.docloction(k.get("ITEM_VALUE")==null?"":(attachmentloc+k.get("ITEM_VALUE").toString()))
								.build();
						attachments.add(a);
					});
				}

				List<Map<String,Object>> firstLossPayeesList = new ArrayList<Map<String,Object>>();
				List<FirstLossPayee> firstLossPayees = firstLossPayeeRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
				if(!firstLossPayees.isEmpty()) {
					firstLossPayees.forEach(k -> {
						Map<String,Object> custMap = new HashMap<String,Object>();
						custMap.put("firstLossPayee", k.getFirstLossPayeeDesc());
						firstLossPayeesList.add(custMap);
					});
				}

				if("100046".equalsIgnoreCase(map.get("companyId")==null?"":map.get("companyId").toString())) {
					Map<Object, List<Tuple>> sectionDetails = Slist.stream().collect(Collectors.groupingBy(k -> k.get("locationName"), Collectors.toList()));
					LinkedList<Map<String,Object>> secdetails = new LinkedList<Map<String,Object>>();
					for(Map.Entry<Object, List<Tuple>> secEntry : sectionDetails.entrySet()) {
						LinkedHashMap<String, Object> sec_map = new LinkedHashMap<String, Object>();
						LinkedList<Map<String,Object>> sec_list = new LinkedList<Map<String,Object>>();
						sec_map.put("locationName", secEntry.getKey());
						for(int i =0;i<secEntry.getValue().size();i++) {
							Tuple o = secEntry.getValue().get(i);
							LinkedHashMap<String,Object> s = new LinkedHashMap<String,Object>();
							s.put("covername", o.get("sectionDesc")==null?"":o.get("sectionDesc").toString());
							s.put("annually", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString()));
							s.put("monthly", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString())/12);
							sec_list.add(s);
						}
						sec_map.put("sectionList", sec_list);
						secdetails.add(sec_map);
					}
					result.put("sectionDetails", secdetails);
					result.put("phoenixVatPercent", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxRate()).findAny().orElse(BigDecimal.ZERO));
					result.put("phoenixVatAmount", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
				}

				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("branchCode", map.get("branchCode")==null?"":map.get("branchCode").toString());
				result.put("agencyCode", map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("address", map.get("address")==null?"":map.get("address").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
				result.put("brokerBranchName", map.get("brokerBranchName")==null?"":map.get("brokerBranchName").toString());
				result.put("productName", map.get("productName")==null?"":map.get("productName").toString().toUpperCase()+" "+(map.get("policyNo")==null?"QUOTE SCHEDULE":"POLICY SCHEDULE"));
				result.put("stateName", map.get("stateName")==null?"":map.get("stateName").toString());
				result.put("cityName", map.get("cityName")==null?"":map.get("cityName").toString());
				result.put("mobileNo", map.get("mobileNo")==null?"":map.get("mobileNo").toString());
				result.put("customerId", map.get("customerId")==null?"":map.get("customerId").toString());
				result.put("brokerName", map.get("brokerName")==null?"":map.get("brokerName").toString());
				result.put("coreAppBrokerCode", map.get("coreAppBrokerCode")==null?"":map.get("coreAppBrokerCode").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("debitNoteNo", map.get("debitNoteNo")==null?"":map.get("debitNoteNo").toString());
				result.put("premium", map.get("premium")==null?"":new BigDecimal(Double.parseDouble(map.get("premium").toString())).toString());
				result.put("vatPremium", map.get("vatPremium")==null?"":Double.parseDouble(map.get("vatPremium").toString()));
				result.put("vatPercent", map.get("vatPercent")==null?"":Double.parseDouble(map.get("vatPercent").toString()));
				result.put("totalPremium", map.get("totalPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("totalPremium").toString())).toString());
				result.put("signature", map.get("signature")==null?"":map.get("signature").toString());
				result.put("place", map.get("place")==null?"":map.get("place").toString());
				result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
				result.put("companylogo", map.get("companylogo")==null?"":map.get("companylogo").toString());
				result.put("productId", map.get("productId")==null?"":map.get("productId").toString());
				result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				result.put("taxName", map.get("companyId")==null?"":map.get("companyId").toString().equalsIgnoreCase("100004")?"Premium":"VAT");
				result.put("userMobile", map.get("userMobile")==null?"":map.get("userMobile").toString());
				result.put("bdmCode", map.get("bdmCode")==null?"":map.get("bdmCode").toString());
				result.put("bdmName", map.get("bdmName")==null?"":map.get("bdmName").toString());
				result.put("preparedBy", map.get("preparedBy")==null?"":map.get("preparedBy").toString());
				result.put("overAllPremium", OverAllPremium);
				result.put("premiumDetails", premiumDetailsRes);
				result.put("firstLossPayeesList", firstLossPayeesList);
				result.put("coverageDetails",  coverageDetails.stream()
						.sorted((p1, p2) -> {
							int sectionId1 = Integer.parseInt(p1.get("coverId").toString());
							int sectionId2 = Integer.parseInt(p2.get("coverId").toString());

							if (sectionId1 == 40) {
								return -1;
							}
							if (sectionId2 == 40) {
								return 1;
							}
							if (sectionId1 == 75) {
								return -1;
							}
							if (sectionId2 == 75) {
								return 1;
							}
							return Integer.compare(sectionId1, sectionId2);
						})
						.collect(Collectors.toList()));
				result.put("attachMents", attachments);
			}
		}catch(Exception e) {
			log.info("Error in CorporatePlusSchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into CorporatePlusSchedule");
		return result;

	}

	public List<AttachMentRes> getAttachMentList(String companyId,String productId,String itemType,String motorusage){
	    List<AttachMentRes> attachments = new ArrayList<>();
	    try {
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
	        Root<ListItemValue> dudRoot = cq.from(ListItemValue.class);

	        Subquery<Integer> amdMax = cq.subquery(Integer.class);
	        Root<ListItemValue> aSub = amdMax.from(ListItemValue.class);

	        amdMax.select(aSub.get("amendId"))
	        .where(cb.equal(aSub.get("itemType"), dudRoot.get("itemType")),
	                cb.equal(aSub.get("companyId"), dudRoot.get("companyId")),
	                cb.equal(aSub.get("param1"), dudRoot.get("param1")),
	                StringUtils.isNotBlank(motorusage)?cb.like(aSub.get("param2"), "%"+motorusage+"%"):cb.conjunction());

	        cq.multiselect(dudRoot.get("itemCode").alias("docRefNo"),dudRoot.get("itemValue").alias("filePathOrginal"))
	        .where(cb.equal(dudRoot.get("itemType"), itemType),
	                cb.equal(dudRoot.get("companyId"), companyId),
	                cb.equal(dudRoot.get("param1"), productId),
	                cb.equal(dudRoot.get("amendId"), amdMax),
	                StringUtils.isNotBlank(motorusage)?cb.like(dudRoot.get("param2"), "%"+motorusage+"%"):cb.conjunction())
	        .distinct(true);  

	        List<Tuple> docList = em.createQuery(cq).getResultList();
	        if(!docList.isEmpty()) {
	            docList.forEach(e -> {
	                AttachMentRes m = AttachMentRes.builder()
	                        .docRefNo(e.get("docRefNo")==null?"":e.get("docRefNo").toString())
	                        .docloction(e.get("filePathOrginal")==null?"":e.get("filePathOrginal").toString())
	                        .build();
	                attachments.add(m);
	            });
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return attachments;
	}

	public String currentRequestURL() {
		ServletRequestAttributes att = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(att != null) {
			HttpServletRequest request = att.getRequest();
			StringBuffer requestURL = request.getRequestURL();
			String queryString = request.getQueryString();
			if(queryString != null) {
				requestURL.append("?").append(queryString);
			}
			return requestURL.toString();
		}
		return null;
	}

	public List<Map<String,Object>> getEagleMotorComputationSheet(String requestReferenceNo) {
		log.info("Enter into getEagleMotorBrokerQuotation.\nArgument ==> "+requestReferenceNo);
		List<Map<String, Object>> vehicleList = new ArrayList<>();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EserviceCustomerDetails> piRoot = cq.from(EserviceCustomerDetails.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<EserviceMotorDetails> mddRoot = cq.from(EserviceMotorDetails.class);

			Subquery<Integer> countryNameAmd = cq.subquery(Integer.class);
			Root<CountryMaster> SubCnAd = countryNameAmd.from(CountryMaster.class);
			countryNameAmd.select(cb.max(SubCnAd.get("amendId"))).where(cb.equal(SubCnAd.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCnAd.get("companyId"), mddRoot.get("companyId")),cb.equal(SubCnAd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> SubCm = countryName.from(CountryMaster.class);
			countryName.select(SubCm.get("countryName")).where(cb.equal(SubCm.get("countryId"), piRoot.get("nationality")),
					cb.equal(SubCm.get("companyId"), mddRoot.get("companyId")),cb.equal(SubCm.get("status"), "Y"),cb.equal(SubCm.get("amendId"), countryNameAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(icmAmdRoot.get("companyId"), icmRoot.get("companyId")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), mddRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), mddRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(luiRoot.get("userName").alias("userName"),mddRoot.get("agencyCode").alias("agencyCode"),mddRoot.get("requestReferenceNo").alias("requestReferenceNo"),
					mddRoot.get("quoteNo").alias("quoteNo"),mddRoot.get("policyNo").alias("policyNo"),mddRoot.get("originalPolicyNo").alias("originalPolicyNo"),mddRoot.get("companyId").alias("companyId"),
					icmRoot.get("companyName").alias("companyName"),mddRoot.get("currency").alias("currency"),
					mddRoot.get("actualPremiumFc").alias("premium"),mddRoot.get("overallPremiumFc").alias("overAllPremium"),
					mddRoot.get("branchName").alias("branchName"),mddRoot.get("policyStartDate").alias("inceptionDate"),
					mddRoot.get("policyEndDate").alias("expiryDate"), piRoot.get("clientName").alias("customerName"),
					cb.concat(piRoot.get("address1"), cb.concat(",", cb.concat(cb.coalesce(piRoot.get("pinCode"), ""),cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("pinCode")), "").when(cb.equal(piRoot.get("pinCode"), ""), "")
							.otherwise(",").as(String.class), cb.concat(piRoot.get("stateName"), cb.concat(",", cb.concat(piRoot.get("cityName"),cb.concat(",", countryName)))))))).alias("address"),
					piRoot.get("vrTinNo").alias("vrTinNo"),piRoot.get("email1").alias("email1"),piRoot.get("mobileNo1").alias("mobileNo1"),imageURL.alias("companyLogo"),luiRoot.get("brokerLogo").alias("brokerLogo"))
			.where(cb.equal(piRoot.get("customerReferenceNo"), mddRoot.get("customerReferenceNo"))/*,cb.equal(hpmRoot.get("currency"), icmRoot.get("currencyId"))*/,cb.equal(mddRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(luiRoot.get("loginId"), mddRoot.get("loginId")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.in(mddRoot.get("productId")).value(Arrays.asList(5,46)),cb.equal(mddRoot.get("requestReferenceNo"), requestReferenceNo))
			.orderBy(cb.desc(mddRoot.get("entryDate")));

			List<Tuple> list = em.createQuery(cq).getResultList();

			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);

				String referenceNo=map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString();
				List<FactorRateRequestDetails> coverData = factorRateRequestDetailsRepo.findByRequestReferenceNo(referenceNo);

				List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999
						&& (!f.getCoverageType().equalsIgnoreCase("T")) && Arrays.asList("D","Y").contains(f.getIsSelected()))
						.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
								BigDecimal.ZERO, FactorRateRequestDetails::getPremiumExcludedTaxFc, BigDecimal::add))))
						.entrySet().stream()
						.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
							Integer sectionId = s.getKey();
							return s.getValue().entrySet().stream()
									.filter(f -> f.getValue().compareTo(BigDecimal.ZERO) != 0)
									.map((Map.Entry<String,BigDecimal> g )-> {
										String coverDesc = g.getKey();
										BigDecimal totPremium = g.getValue();
										Map<String,Object> secMap = new HashMap<String,Object>();
										secMap.put("SectionId", sectionId);
										secMap.put("CoverDesc", coverDesc);
										secMap.put("Rate", coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
												.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
												.findAny().orElse(0.0));
										secMap.put("TotPremium", totPremium);
										return secMap;
									});
						}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
						.collect(Collectors.toList());

				List<Map<String,Object>> sectionTaxRate = coverData.stream().filter(f -> f.getSectionId()!=99999 && f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T")
						&& Arrays.asList("D","Y").contains(f.getIsSelected()))
						.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
								BigDecimal.ZERO, FactorRateRequestDetails::getTaxAmount, BigDecimal::add))))
						.entrySet().stream()
						.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
							Integer sectionId = s.getKey();
							return s.getValue().entrySet().stream()
									.filter(f -> f.getValue().compareTo(BigDecimal.ZERO) != 0)
									.map((Map.Entry<String,BigDecimal> g )-> {
										String coverDesc = g.getKey();
										BigDecimal totPremium = g.getValue();
										Map<String,Object> secMap = new HashMap<String,Object>();
										secMap.put("SectionId", sectionId);
										secMap.put("CoverDesc", coverDesc);
										secMap.put("Rate", coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
												.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
												.findAny().orElse(0.0));
										secMap.put("TotPremium", totPremium);
										return secMap;
									});
						}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
						.collect(Collectors.toList());

				sectionPremium.addAll(sectionTaxRate);

				sectionPremium.stream().filter(f ->
				!f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("h&rfee") &&
				!f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("policyfee") &&
				!f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("fscfee"))
				.collect(Collectors.toList())
				.forEach(k -> {
					TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
							.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
							.rate(new BigDecimal(Double.valueOf(k.get("Rate").toString())).toString())
							.build();
					premiumDetailsRes.add(u);
				});

				Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
						.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
						.findAny().orElse(0.0);

				List<Map<String,Object>> h_rfees = sectionPremium.stream()
						.filter(f -> f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("h&rfee"))
						.collect(Collectors.toList());
				if(h_rfees !=null && h_rfees.size()>0) {
					Double h_rfee = h_rfees.stream().map(m -> new BigDecimal(m.get("TotPremium").toString())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					TaxInvoicePremiumDetails p = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(h_rfee).toString())
							.narration("H&R Fee")
							.build();
					premiumDetailsRes.add(p);
				}

				List<Map<String,Object>> policyfees = sectionPremium.stream()
						.filter(f -> f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("policyfee"))
						.collect(Collectors.toList());
				if(policyfees !=null && policyfees.size()>0) {
					Double policyfee = policyfees.stream().map(m -> new BigDecimal(m.get("TotPremium").toString())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					TaxInvoicePremiumDetails p = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(policyfee).toString())
							.narration("Policy Fee")
							.build();
					premiumDetailsRes.add(p);
				}

				List<Map<String,Object>> fscfees = sectionPremium.stream()
						.filter(f -> f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("fscfee"))
						.collect(Collectors.toList());
				if(fscfees !=null && fscfees.size()>0) {
					Double fscfee = fscfees.stream().map(m -> new BigDecimal(m.get("TotPremium").toString())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					TaxInvoicePremiumDetails p = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(fscfee).toString())
							.narration("FSC Charges")
							.build();
					premiumDetailsRes.add(p);
				}

				List<EserviceMotorDetails> list1 = eservicemotorRepo.findByRequestReferenceNoAndStatusOrderByRiskIdAsc(map.get("requestReferenceNo").toString(),"Y");
				vehicleList = list1.stream().map(k -> {
					LinkedHashMap<String,Object> m = new LinkedHashMap<String,Object>();
					m.put("policyTypeDesc", k.getPolicyTypeDesc()==null?"":StringUtils.capitalize(k.getPolicyTypeDesc()));
					m.put("registrationNumber", k.getRegistrationNumber()==null?"":k.getRegistrationNumber());
					m.put("vehicleMakeDesc", k.getVehicleMakeDesc()==null?"":StringUtils.capitalize(k.getVehicleMakeDesc()));
					m.put("vehicleTypeDesc", k.getVehicleTypeDesc()==null?"":StringUtils.capitalize(k.getVehicleTypeDesc()));
					m.put("chassisNumber", k.getChassisNumber()==null?"":k.getChassisNumber());
					m.put("colorDesc", k.getColorDesc()==null?"":StringUtils.capitalize(k.getColorDesc()));
					m.put("manufactureYear", k.getManufactureYear());
					m.put("engineNumber", k.getEngineNumber()==null?"":k.getEngineNumber());
					m.put("vehcileModelDesc", k.getVehcileModelDesc()==null?"":StringUtils.capitalize(k.getVehcileModelDesc()));
					m.put("sumInsured", k.getSumInsured());
					m.put("sectionName", k.getSectionName()==null?"":k.getSectionName());
					m.put("cubicCapacity", k.getCubicCapacity()==null?"":k.getCubicCapacity());
					m.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
					m.put("requestReferenceNo", map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
					m.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
					m.put("motorUsageDesc", k.getMotorUsageDesc()==null?"":k.getMotorUsageDesc().toString());
					m.put("inceptionDate", map.get("inceptionDate")==null?null:map.get("inceptionDate").toString());
					m.put("expiryDate", map.get("expiryDate")==null?null:map.get("expiryDate").toString());
					m.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
					m.put("companyLogo", map.get("companyLogo")==null?"":map.get("companyLogo").toString());
					m.put("brokerLogo", map.get("brokerLogo")==null?"":map.get("brokerLogo").toString());
					m.put("premiumDetailsRes", premiumDetailsRes);
					return m;
				}).collect(Collectors.toList());


				/*result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
				result.put("agencyCode", map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				result.put("requestReferenceNo", map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("originalPolicyNo", map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
				result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				result.put("companyName", map.get("companyName")==null?"":map.get("companyName").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("vatPercent", map.get("vatPercent")==null?null:map.get("vatPercent").toString());
				result.put("premium", map.get("premium")==null?"":map.get("premium").toString());
				result.put("vatPremium", map.get("vatPremium")==null?null:map.get("vatPremium").toString());
				result.put("overAllPremium", map.get("overAllPremium")==null?"":map.get("overAllPremium").toString());
				result.put("commissionPercentage", map.get("commissionPercentage")==null?null:map.get("commissionPercentage").toString());
				result.put("commission", map.get("commission")==null?null:map.get("commission").toString());
				result.put("branchName", map.get("branchName")==null?null:map.get("branchName").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?null:map.get("inceptionDate").toString());
				result.put("address", map.get("address")==null?"":map.get("address").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("mobileNo1", map.get("mobileNo1")==null?null:map.get("mobileNo1").toString());
				result.put("customerName", map.get("customerName")==null?"":map.get("customerName").toString());
				result.put("vrTinNo", map.get("vrTinNo")==null?null:map.get("vrTinNo").toString());
				result.put("expiryDate", map.get("expiryDate")==null?null:map.get("expiryDate").toString());
				result.put("insuranceTypeDesc", vehicleList.get(0).get("insuranceTypeDesc")==null?"":vehicleList.get(0).get("insuranceTypeDesc").toString());
				result.put("companyLogo", map.get("companyLogo")==null?"":map.get("companyLogo").toString());
				result.put("brokerLogo", map.get("brokerLogo")==null?"":map.get("brokerLogo").toString());
				result.put("vehicleList", vehicleList);*/

			}

		}catch(Exception e) {
			log.info("Error in getMotorBrokerQuotation ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getMotorBrokerQuotation");
		return vehicleList;
	}

	@SuppressWarnings("rawtypes")
	public List<MotorCoverNoteRes> getEagleMotorQuotation(String requestReferenceNo,String vehicleId) {
		log.info("Enter into getEagleMotorQuotation.\nArgument ==> RequestReferenceNo :"+requestReferenceNo+",VehicleId : "+vehicleId);
		List<MotorCoverNoteRes> response = new  ArrayList<MotorCoverNoteRes>();
		List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EserviceMotorDetails> mddRoot = cq.from(EserviceMotorDetails.class);
			Root<EserviceCustomerDetails> piRoot = cq.from(EserviceCustomerDetails.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<EserviceSectionDetails> sddRoot = cq.from(EserviceSectionDetails.class);

			Subquery<String> insureName = cq.subquery(String.class);
			Root<LoginUserInfo> SubluiRoot = insureName.from(LoginUserInfo.class);
			insureName.select(cb.upper(SubluiRoot.get("userName"))).where(cb.equal(SubluiRoot.get("loginId"), mddRoot.get("loginId")));

			// MAKE MASTER TYPE
			Subquery<Integer> makeTypeAmd = cq.subquery(Integer.class);
			Root<MotorMakeMaster> makeAmd = makeTypeAmd.from(MotorMakeMaster.class);
			makeTypeAmd.select(cb.max(makeAmd.get("amendId"))).where(cb.equal(makeAmd.get("makeId").as(String.class), mddRoot.get("vehicleMake")),
					cb.equal(makeAmd.get("status"), "Y"),cb.equal(makeAmd.get("companyId"), mddRoot.get("companyId")));

			/*Subquery<String> makeType = cq.subquery(String.class);
			Root<MotorMakeMaster> makeRoot = makeType.from(MotorMakeMaster.class);
			makeType.select(makeRoot.get("makeNameEn")).where(cb.equal(makeRoot.get("makeId"), mddRoot.get("vehicleMake")),
					cb.equal(makeRoot.get("companyId"), hpmRoot.get("companyId")),cb.equal(makeRoot.get("status"), "Y"),
					cb.equal(makeRoot.get("amendId"), makeTypeAmd));*/

			// MODEL MASTER TYPE
			Subquery<Integer> modelTypeAmd = cq.subquery(Integer.class);
			Root<MotorMakeModelMaster> SubmmAmd = modelTypeAmd.from(MotorMakeModelMaster.class);
			modelTypeAmd.select(cb.max(SubmmAmd.get("amendId"))).where(cb.equal(SubmmAmd.get("vehiclemodelcode").as(String.class), mddRoot.get("vehcileModel")),
					cb.equal(SubmmAmd.get("status"), "Y"),cb.equal(SubmmAmd.get("companyId"), mddRoot.get("companyId")));

			Subquery<String> modelType = cq.subquery(String.class);
			Root<MotorMakeModelMaster> Submm = modelType.from(MotorMakeModelMaster.class);
			modelType.select(Submm.get("modelNameEn")).where(cb.equal(Submm.get("vehiclemodelcode").as(String.class), mddRoot.get("vehcileModel")),
					cb.equal(Submm.get("companyId"), mddRoot.get("companyId")),cb.equal(Submm.get("status"), "Y"),cb.equal(Submm.get("amendId"), modelTypeAmd));


			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> SubicmAmd = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(SubicmAmd.get("amendId"))).where(cb.equal(SubicmAmd.get("companyId"), icmRoot.get("companyId")));

			Subquery<Integer> SubcnAmd = cq.subquery(Integer.class);
			Root<CountryMaster> cnAmd = SubcnAmd.from(CountryMaster.class);
			SubcnAmd.select(cb.max(cnAmd.get("amendId"))).where(cb.equal(cnAmd.get("countryId"), piRoot.get("nationality")),
					cb.equal(cnAmd.get("companyId"), mddRoot.get("companyId")),
					cb.equal(cnAmd.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> Subcn = countryName.from(CountryMaster.class);
			countryName.select(Subcn.get("countryName")).where(cb.equal(Subcn.get("countryId"), piRoot.get("nationality")),
					cb.equal(Subcn.get("companyId"), mddRoot.get("companyId")),
					cb.equal(Subcn.get("status"), "Y"),
					cb.equal(Subcn.get("amendId"), SubcnAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), mddRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));


			List<Selection> selectionList = 	Arrays.asList(
					mddRoot.get("riskId").alias("vehicleId"),
					piRoot.get("clientName").alias("customerName"),
					insureName.alias("insurerName"),
					piRoot.get("address1").alias("address1"),
					piRoot.get("customerReferenceNo").alias("customerId"),
					mddRoot.get("policyStartDate").alias("inceptionDate"),
					mddRoot.get("policyEndDate").alias("expiryDate"),
					mddRoot.get("quoteNo").alias("quoteNo"),
					imageURL.alias("companyLogo"),
					mddRoot.get("registrationNumber").alias("registrationNumber"),
					mddRoot.get("requestReferenceNo").alias("requestReferenceNo"),
					mddRoot.get("vehicleTypeDesc").alias("vehicleTypeDesc"),
					cb.selectCase().when(cb.isNotNull(mddRoot.get("vehcileModelDesc")), mddRoot.get("vehcileModelDesc"))
					.otherwise(modelType).alias("modelType"),
					mddRoot.get("colorDesc").alias("colorDesc"),
					mddRoot.get("cubicCapacity").alias("cubicCapacity"),
					mddRoot.get("vehicleMakeDesc").alias("vehicleMakeDesc"),
					mddRoot.get("chassisNumber").alias("chassisNumber"),
					mddRoot.get("seatingCapacity").alias("seatingCapacity"),
					mddRoot.get("engineNumber").alias("engineNumber"),
					mddRoot.get("fuelTypeDesc").alias("fuelType"),
					mddRoot.get("policyTypeDesc").alias("policyTypeDesc"),
					mddRoot.get("manufactureYear").alias("manufactureYear"),
					luiRoot.get("userMobile").alias("agentMobile"),
					mddRoot.get("motorUsageDesc").alias("motorUsageDesc"),
					icmRoot.get("companyName").alias("companyName"),
					mddRoot.get("branchName").alias("branchName"),
					mddRoot.get("currency").alias("currency"),
					mddRoot.get("sectionName").alias("sectionName"),
					mddRoot.get("vehcileModel").alias("vehcileModel"));
			List<Selection> selections = selectionList.stream().collect(Collectors.toList());
			//if(StringUtils.isNotBlank(vehicleId)) {
			selections.add(mddRoot.get("sumInsured").alias("sumInsured"));
			/*}else {
				selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")),hpmRoot.get("premiumLc"))
						.otherwise(hpmRoot.get("vatPremiumFc")).alias("premium"));
				selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("vatPremiumLc"))
						.otherwise(hpmRoot.get("vatPremiumFc")).alias("vatPremium"));
				selections.add(cb.selectCase().when(cb.in(hpmRoot.get("currency")).value(icmRoot.get("currencyId")), hpmRoot.get("overallPremiumLc"))
						.otherwise(hpmRoot.get("overallPremiumFc")).alias("overallPremium"));
			}*/

			Selection [] selectionArray = new Selection[selections.size()];
			selections.toArray(selectionArray);

			cq.multiselect(selectionArray)
			.where(cb.equal(piRoot.get("customerReferenceNo"), mddRoot.get("customerReferenceNo")),
					cb.equal(luiRoot.get("loginId"), mddRoot.get("loginId")),
					cb.equal(mddRoot.get("currency"), icmRoot.get("currencyId")),
					cb.equal(mddRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(icmRoot.get("amendId"), icmAmd),
					cb.equal(mddRoot.get("productId"), mddRoot.get("productId")),
					cb.equal(mddRoot.get("status"), mddRoot.get("status")),
					cb.equal(sddRoot.get("requestReferenceNo"), mddRoot.get("requestReferenceNo")),
					cb.equal(sddRoot.get("riskId"), mddRoot.get("riskId")),
					cb.equal(mddRoot.get("requestReferenceNo"), requestReferenceNo),
					StringUtils.isNotBlank(vehicleId)?cb.equal(mddRoot.get("riskId"), vehicleId):
						cb.conjunction());
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {

				String referenceNo=list.get(0).get("requestReferenceNo")==null?"":list.get(0).get("requestReferenceNo").toString();
				List<FactorRateRequestDetails> coverData = factorRateRequestDetailsRepo.findByRequestReferenceNo(referenceNo);

				List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getSectionId()!=99999
						&& (!f.getCoverageType().equalsIgnoreCase("T")) && Arrays.asList("D","Y").contains(f.getIsSelected()))
						.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
								BigDecimal.ZERO, FactorRateRequestDetails::getPremiumExcludedTaxFc, BigDecimal::add))))
						.entrySet().stream()
						.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
							Integer sectionId = s.getKey();
							return s.getValue().entrySet().stream()
									.filter(f -> f.getValue().compareTo(BigDecimal.ZERO) != 0)
									.map((Map.Entry<String,BigDecimal> g )-> {
										String coverDesc = g.getKey();
										BigDecimal totPremium = g.getValue();
										Map<String,Object> secMap = new HashMap<String,Object>();
										secMap.put("SectionId", sectionId);
										secMap.put("CoverDesc", coverDesc);
										secMap.put("Currency", coverData.stream()
												.map(m -> m.getCurrency()).findFirst().orElse(""));
										secMap.put("TotPremium", totPremium);
										return secMap;
									});
						}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
						.collect(Collectors.toList());

				List<Map<String,Object>> sectionTaxRate = coverData.stream().filter(f -> f.getSectionId()!=99999 && f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T")
						&& Arrays.asList("D","Y").contains(f.getIsSelected()))
						.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverDesc(),Collectors.reducing(
								BigDecimal.ZERO, FactorRateRequestDetails::getTaxAmount, BigDecimal::add))))
						.entrySet().stream()
						.flatMap((Map.Entry<Integer,Map<String,BigDecimal>> s ) -> {
							Integer sectionId = s.getKey();
							return s.getValue().entrySet().stream()
									.filter(f -> f.getValue().compareTo(BigDecimal.ZERO) != 0)
									.map((Map.Entry<String,BigDecimal> g )-> {
										String coverDesc = g.getKey();
										BigDecimal totPremium = g.getValue();
										Map<String,Object> secMap = new HashMap<String,Object>();
										secMap.put("SectionId", sectionId);
										secMap.put("CoverDesc", coverDesc);
										secMap.put("Currency", coverData.stream()
												.map(m -> m.getCurrency()).findFirst().orElse(""));
										secMap.put("TotPremium", totPremium);
										return secMap;
									});
						}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
						.collect(Collectors.toList());

				sectionPremium.addAll(sectionTaxRate);

				String currency = sectionPremium.get(0).get("Currency")==null?null:sectionPremium.get(0).get("Currency").toString();
				Double overAllPremium = sectionPremium.stream().filter(f ->
				!f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("h&rfee") &&
				!f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("policyfee") &&
				!f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("fscfee"))
						.map(m -> new BigDecimal(m.get("TotPremium").toString()))
						.collect(Collectors.summingDouble(BigDecimal::doubleValue));

				TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
						.amount(new BigDecimal(overAllPremium).toString())
						.narration("Premium")
						.currency(currency)
						.build();
				premiumDetailsRes.add(u);

				List<Map<String,Object>> h_rfees = sectionPremium.stream()
						.filter(f -> f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("h&rfee"))
						.collect(Collectors.toList());
				if(h_rfees !=null && h_rfees.size()>0) {
					Double h_rfee = h_rfees.stream().map(m -> new BigDecimal(m.get("TotPremium").toString())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					TaxInvoicePremiumDetails p = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(h_rfee).toString())
							.narration("H&R Fee")
							.currency(currency)
							.build();
					premiumDetailsRes.add(p);
				}

				List<Map<String,Object>> policyfees = sectionPremium.stream()
						.filter(f -> f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("policyfee"))
						.collect(Collectors.toList());
				if(policyfees !=null && policyfees.size()>0) {
					Double policyfee = policyfees.stream().map(m -> new BigDecimal(m.get("TotPremium").toString())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					TaxInvoicePremiumDetails p = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(policyfee).toString())
							.narration("Policy Fee")
							.currency(currency)
							.build();
					premiumDetailsRes.add(p);
				}

				List<Map<String,Object>> fscfees = sectionPremium.stream()
						.filter(f -> f.get("CoverDesc").toString().toLowerCase().replace(" ", "").contains("fscfee"))
						.collect(Collectors.toList());
				if(fscfees !=null && fscfees.size()>0) {
					Double fscfee = fscfees.stream().map(m -> new BigDecimal(m.get("TotPremium").toString())).collect(Collectors.summingDouble(BigDecimal::doubleValue));
					TaxInvoicePremiumDetails p = TaxInvoicePremiumDetails.builder()
							.amount(new BigDecimal(fscfee).toString())
							.narration("FSC Charges")
							.currency(currency)
							.build();
					premiumDetailsRes.add(p);
				}

				list.forEach(map -> {
					MotorCoverNoteRes m = MotorCoverNoteRes.builder()
							.vehicleId(map.get("vehicleId")==null?"":map.get("vehicleId").toString())
							.customerName(map.get("customerName")==null?"":map.get("customerName").toString())
							.insurerName(map.get("insurerName")==null?"":map.get("insurerName").toString())
							.quoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString())
							.companyLogo(map.get("companyLogo")==null?"":map.get("companyLogo").toString())
							.startDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString())
							.endDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString())
							.registrationNumber(map.get("registrationNumber")==null?"":map.get("registrationNumber").toString())
							.requestReferenceNo(map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString())
							.vehicleTypeDesc(map.get("vehicleTypeDesc")==null?"":map.get("vehicleTypeDesc").toString())
							.modelType(map.get("modelType")==null?"":map.get("modelType").toString())
							.colorDesc(map.get("colorDesc")==null?"":map.get("colorDesc").toString())
							.cubicCapacity(map.get("cubicCapacity")==null?"":map.get("cubicCapacity").toString())
							.vehicleMakeDesc(map.get("vehicleMakeDesc")==null?"":map.get("vehicleMakeDesc").toString())
							.chassisNumber(map.get("chassisNumber")==null?"":map.get("chassisNumber").toString())
							.seatingCapacity(map.get("seatingCapacity")==null?"":map.get("seatingCapacity").toString())
							.engineNumber(map.get("engineNumber")==null?"":map.get("engineNumber").toString())
							.fuelType(map.get("fuelType")==null?"":map.get("fuelType").toString())
							.policyTypeDesc(map.get("policyTypeDesc")==null?"":map.get("policyTypeDesc").toString())
							.manufactureYear(map.get("manufactureYear")==null?"":map.get("manufactureYear").toString())
							.agentMobile(map.get("agentMobile")==null?"":map.get("agentMobile").toString())
							.motorUsageDesc(map.get("motorUsageDesc")==null?"":map.get("motorUsageDesc").toString())
							.companyName(map.get("companyName")==null?"":map.get("companyName").toString())
							.branchName(map.get("branchName")==null?"":map.get("branchName").toString())
							.currency(map.get("currency")==null?"":map.get("currency").toString())
							.sectionName(map.get("sectionName")==null?"":map.get("sectionName").toString())
							.modelNumber(map.get("vehcileModel")==null?"":map.get("vehcileModel").toString())
							.overallPremium(overAllPremium.toString())
							.premiumDetailsRes(premiumDetailsRes)
							.address(map.get("address1")==null?"":map.get("address1").toString())
							.customerId(map.get("customerId")==null?"":map.get("customerId").toString())
							.build();
					response.add(m);
				});
			}
		}catch(Exception e) {
			log.info("Error in getEagleMotorQuotation ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getEagleMotorQuotation");
		return response;
	}

	public TravelReportRes GetTravelQuotationByRequestRefNo(String requestRefNo) {
		log.info("Enter into GetTravelQuotationByRequestRefNo.\nArgument ==> requestRefNo :"+requestRefNo);
		TravelReportRes response = new TravelReportRes();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<EserviceTravelDetails> hpmRoot = cq.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> ecdRoot = cq.from(EserviceCustomerDetails.class);
			Root<LoginMaster> lmRoot = cq.from(LoginMaster.class);

			Subquery<String> currencyId =cq.subquery(String.class);
			Root<InsuranceCompanyMaster> icmRoot = currencyId.from(InsuranceCompanyMaster.class);
			currencyId.select(icmRoot.get("currencyId")).where(cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")));



			Subquery<Long> noOfPassanger = cq.subquery(Long.class);
			Root<TravelPassengerDetails> SubnoOfPassanger= noOfPassanger.from(TravelPassengerDetails.class);
			noOfPassanger.select(cb.count(SubnoOfPassanger)).where(cb.equal(SubnoOfPassanger.get("quoteNo"), hpmRoot.get("quoteNo")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));
			
			Subquery<Double> overallPremiumSubquery = cq.subquery(Double.class);
			Root<FactorRateRequestDetails> frdRoot = overallPremiumSubquery.from(FactorRateRequestDetails.class);

			overallPremiumSubquery.select(cb.sum(frdRoot.get("premiumIncludedTaxLc")))
			    .where(
			        cb.equal(frdRoot.get("requestReferenceNo"), hpmRoot.get("requestReferenceNo")),
			        cb.equal(frdRoot.get("vehicleId"), 99999),
			        cb.equal(frdRoot.get("coverageType"), "B")
			    );

	        cq.multiselect(
	                hpmRoot.get("sectionId").alias("sectionId"),
	                hpmRoot.get("planTypeId").alias("planTypeId"),
	                hpmRoot.get("requestReferenceNo").alias("quoteNo"),

	                cb.selectCase()
	                        .when(cb.isNull(ecdRoot.get("titleDesc")), ecdRoot.get("clientName"))
	                        .otherwise(cb.concat(cb.concat(ecdRoot.get("titleDesc"), "."), ecdRoot.get("clientName")))
	                        .alias("customerName"),
	                lmRoot.get("agencyCode").alias("agencyCode"),
	                hpmRoot.get("travelStartDate").alias("inceptionDate"),
	                hpmRoot.get("travelEndDate").alias("expiryDate"),
	                hpmRoot.get("currency").alias("currency"),

	                overallPremiumSubquery.alias("overAllPremium"),
	                cb.selectCase()
	                        .when(cb.in(hpmRoot.get("currency")).value(currencyId), hpmRoot.get("actualPremiumLc"))
	                        .otherwise(hpmRoot.get("actualPremiumFc"))
	                        .alias("premium"),

	                hpmRoot.get("companyId").alias("companyId"),
	                hpmRoot.get("travelCoverDuration").alias("travelCoverDuration"),
	                hpmRoot.get("sourceCountryDesc").alias("sourceCountryDesc"),
	                hpmRoot.get("destinationCountryDesc").alias("destinationCountryDesc"),
	                hpmRoot.get("travelCoverDesc").alias("travelCoverDesc"),
	                hpmRoot.get("totalPassengers").alias("noOfPassanger"),
	                companyName.alias("companyName"),
	                imageURL.alias("companylogo")
	        ).where(
	                cb.equal(lmRoot.get("loginId"), hpmRoot.get("loginId")),
	                cb.equal(hpmRoot.get("customerReferenceNo"), ecdRoot.get("customerReferenceNo")),
	                cb.equal(hpmRoot.get("productId"), "4"),
	                cb.equal(hpmRoot.get("requestReferenceNo"), requestRefNo)
	        );
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);


				response.setQuoteNo(map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				response.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
				response.setAgencyCode(map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				response.setInceptionDate(map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				response.setExpiryDate(map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				response.setCurrency(map.get("currency")==null?"":map.get("currency").toString());
				response.setOverAllPremium(map.get("overAllPremium") == null ? "0" : map.get("overAllPremium").toString());
				response.setPremium(map.get("premium")==null?"":map.get("premium").toString());
				response.setNoOfPassanger(map.get("noOfPassanger")==null?"":map.get("noOfPassanger").toString());
				response.setCompanylogo(map.get("companylogo")==null?"":map.get("companylogo").toString());
				response.setCompanyName(map.get("companyName")==null?"":map.get("companyName").toString());
				response.setAgencyName(map.get("companyName")==null?"":map.get("companyName").toString());
				response.setPassportNo("");
				response.setPolicyPeriod(map.get("travelCoverDuration")==null?"":map.get("travelCoverDuration").toString());
				response.setOrginCountry(map.get("sourceCountryDesc")==null?"":map.get("sourceCountryDesc").toString());
				response.setDestCountry(map.get("destinationCountryDesc")==null?"":map.get("destinationCountryDesc").toString());
				response.setPlanDesc(map.get("travelCoverDesc")==null?"":map.get("travelCoverDesc").toString());
				TravelPolicyTypeGetReq re=new TravelPolicyTypeGetReq();
				re.setCompanyId(map.get("companyId")==null?"":map.get("companyId").toString());
				re.setPlanTypeId(map.get("planTypeId")==null?"":map.get("planTypeId").toString());
				re.setPolicyTypeId(map.get("sectionId")==null?"":map.get("sectionId").toString());
				re.setProductId("4");
				response.setTravelCoverageDetails(getTravelPolicyType(re));
			}
		}catch(Exception e) {
			log.info("Error in getTravelReport ==>"+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into getTravelReport");
		return response;
	}

	public List<TravelCoverageDetailsRes> getTravelPolicyType(TravelPolicyTypeGetReq req) {
		log.info("Enter into getTravelPolicyType.\nArgument ==> requestRefNo :"+req);
		List<TravelCoverageDetailsRes> vehicleList = new ArrayList<>();
		try {

			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();


			//get all Active cover list 
			List<TravelPolicyType> list = new ArrayList<TravelPolicyType>();

			//Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelPolicyType> query = cb.createQuery(TravelPolicyType.class);

			//Find All
			Root<TravelPolicyType> b = query.from(TravelPolicyType.class);

			//Select
			query.select(b);

			//		Subquery<Long> maxAmendId = query.subquery(Long.class);
			//		Root<TravelPolicyType> ocpm1 = maxAmendId.from(TravelPolicyType.class);
			//		maxAmendId.select(cb.max(ocpm1.get("amendId")));
			//		Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			//		Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			//		Predicate a3 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
			//		Predicate a4 = cb.equal(ocpm1.get("planTypeId"), b.get("planTypeId"));
			//		Predicate a5 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			//		Predicate a6 = cb.equal(ocpm1.get("coverId"), b.get("coverId"));
			//		Predicate a7 = cb.equal(ocpm1.get("subCoverId"), "0");
			//		Predicate a8 = cb.equal(ocpm1.get("coverStatus"), "Y");
			//		maxAmendId.where(a1,a2,a3,a4,a5,a6, a7, a8);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<TravelPolicyType> ocpm1 = effectiveDate.from(TravelPolicyType.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveStartDate").as(Date.class))); //
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
			Predicate a4 = cb.equal(ocpm1.get("planTypeId"), b.get("planTypeId"));
			Predicate a5 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a6 = cb.equal(ocpm1.get("coverId"), b.get("coverId"));
			//Predicate a7 = cb.equal(ocpm1.get("subCoverId"), "0");
			Predicate a8 = cb.equal(ocpm1.get("coverStatus"), "Y");
			Predicate a9 = cb.lessThanOrEqualTo(ocpm1.get("effectiveStartDate"), today);
			effectiveDate.where(a1,a2,a3,a4,a5,a6,a8,a9);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<TravelPolicyType> ocpm2 = effectiveDate2.from(TravelPolicyType.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveEndDate").as(Date.class)));
			Predicate b1 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate b2 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate b3 = cb.equal(ocpm2.get("policyTypeId"), b.get("policyTypeId"));
			Predicate b4 = cb.equal(ocpm2.get("planTypeId"), b.get("planTypeId"));
			Predicate b5 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));
			Predicate b6 = cb.equal(ocpm2.get("coverId"), b.get("coverId"));
			//Predicate b7 = cb.equal(ocpm2.get("subCoverId"), "0");
			Predicate b8 = cb.equal(ocpm2.get("coverStatus"), "Y");
			Predicate b9 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveEndDate"), todayEnd);
			effectiveDate2.where(b1, b2, b3, b4,b5,b6,  b8, b9);


			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("coverId")));
			orderList.add(cb.asc(b.get("subCoverId")));


			Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(b.get("policyTypeId"), req.getPolicyTypeId());
			Predicate n4 = cb.equal(b.get("planTypeId"), req.getPlanTypeId());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.equal(b.get("effectiveStartDate"),effectiveDate);
			Predicate n8 = cb.equal(b.get("effectiveEndDate"),effectiveDate2);
			Predicate n7 = cb.equal(b.get("coverStatus"),"Y");
			//	Predicate n10 = cb.equal(b.get("subCoverId"), "0");

			query.where(n1, n2,n3,n4,n6, n5,n7, n8).orderBy(orderList) ;

			TypedQuery<TravelPolicyType> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getCoverId()))).collect(Collectors.toList());
			list = list.stream().filter(o -> o.getCoverDesc()!=null).collect(Collectors.toList());


			if(list.size()>0) {
				for(TravelPolicyType data : list) {
					TravelCoverageDetailsRes m =  TravelCoverageDetailsRes.builder()
							.coverId(data.getCoverId()==null?"":data.getCoverId().toString())
							.coverDesc(data.getCoverDesc())
							.coverage("")
							.build();


					vehicleList.add(m);
					List<TravelPolicyType> subcovers = getAllActiveSubCoversList(req,data.getCoverId() );
					if(subcovers.size()>0) {
						for(TravelPolicyType subcover : subcovers) {
							TravelCoverageDetailsRes m1 =  TravelCoverageDetailsRes.builder()
									.coverId(subcover.getSubCoverId()==null?"":subcover.getSubCoverId().toString())
									.coverDesc(subcover.getSubCoverDesc())
									.coverage(subcover.getSumInsured())
									.build();
							vehicleList.add(m1);
						}
					}

				}
			}
			log.info("Response List of getTravelPolicyType ==> "+vehicleList);
			log.info("Exit into getTravelPolicyType");
		}catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return vehicleList;
	}


	private List<TravelPolicyType> getAllActiveSubCoversList(TravelPolicyTypeGetReq req, Integer coverId) {
		List<TravelPolicyType> list = new ArrayList<TravelPolicyType>();
		try {

			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelPolicyType> query = cb.createQuery(TravelPolicyType.class);

			// Find All
			Root<TravelPolicyType> b = query.from(TravelPolicyType.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			//		Subquery<Long> amendId = query.subquery(Long.class);
			//		Root<TravelPolicyType> ocpm1 = amendId.from(TravelPolicyType.class);
			//		amendId.select(cb.max(ocpm1.get("amendId")));
			//		Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			//		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			//		Predicate a3 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
			//		Predicate a4 = cb.equal(ocpm1.get("planTypeId"),b.get("planTypeId"));
			//		Predicate a5 = cb.equal(ocpm1.get("coverId"),b.get("coverId"));
			//		Predicate a7 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			//		Predicate a9 = cb.equal(ocpm1.get("coverId"),  b.get("coverId"));
			//		Predicate a10 = cb.notEqual(ocpm1.get("subCoverId"), "0");
			//		Predicate a11 = cb.equal(ocpm1.get("status"), "Y");
			//
			//		amendId.where(a1,a2,a3,a4,a5,a7,a9,a10, a11);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<TravelPolicyType> ocpm1 = effectiveDate.from(TravelPolicyType.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveStartDate").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
			Predicate a4 = cb.equal(ocpm1.get("planTypeId"),b.get("planTypeId"));
			Predicate a5 = cb.equal(ocpm1.get("coverId"),b.get("coverId"));
			Predicate a6 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a7 = cb.equal(ocpm1.get("coverId"),  b.get("coverId"));
			Predicate a8 = cb.notEqual(ocpm1.get("subCoverId"), "0");
			Predicate a10 = cb.equal(ocpm1.get("status"), "Y");
			Predicate a9 = cb.lessThanOrEqualTo(ocpm1.get("effectiveStartDate"), today);
			effectiveDate.where(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<TravelPolicyType> ocpm2 = effectiveDate2.from(TravelPolicyType.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveEndDate").as(Date.class)));
			Predicate b1 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate b2 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate b3 = cb.equal(ocpm2.get("policyTypeId"),b.get("policyTypeId"));
			Predicate b4 = cb.equal(ocpm2.get("planTypeId"),b.get("planTypeId"));
			Predicate b5 = cb.equal(ocpm2.get("coverId"),b.get("coverId"));
			Predicate b6 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));
			Predicate b7 = cb.equal(ocpm2.get("coverId"),  b.get("coverId"));
			Predicate b8 = cb.notEqual(ocpm2.get("subCoverId"), "0");
			Predicate b9 = cb.equal(ocpm2.get("status"), "Y");
			Predicate b10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveEndDate"), todayEnd);
			effectiveDate2.where(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10);

			Predicate n1 = cb.equal(b.get("effectiveStartDate"),effectiveDate);
			Predicate n8 = cb.equal(b.get("effectiveEndDate"),effectiveDate2);
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId() );	
			Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId() );		
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.equal(b.get("coverId"), coverId);
			Predicate n6 =  cb.equal(b.get("policyTypeId"), req.getPolicyTypeId()); 
			Predicate n7 =  cb.equal(b.get("planTypeId"), req.getPlanTypeId()); 
			Predicate n11 = cb.notEqual(b.get("subCoverId"), "0");
			Predicate n12 = cb.equal(b.get("status"),"Y");


			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("subCoverId")));
			query.where(n1, n2,n3,n4, n5, n6, n7,n8, n11, n12).orderBy(orderList);

			TypedQuery<TravelPolicyType> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getSubCoverId()))).collect(Collectors.toList());
			list = list.stream().filter(o -> o.getSubCoverDesc()!=null).collect(Collectors.toList());

		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}


	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Transactional
	public String getPDFcount(String quoteNo) {
		String reportName="DRAFT";
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<HomePositionMaster> updateQuery = cb.createCriteriaUpdate(HomePositionMaster.class);
			Root<HomePositionMaster>  pmRoot = updateQuery.from(HomePositionMaster.class);
			CriteriaQuery<Tuple> currentcount = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> currentcountRoot = currentcount.from(HomePositionMaster.class);

			currentcount.multiselect(cb.coalesce(currentcountRoot.get("pdfCount"), 0).alias("pdfCount"),
					currentcountRoot.get("status").alias("status")).where(cb.equal(currentcountRoot.get("quoteNo"), quoteNo));
			TypedQuery<Tuple> query = em.createQuery(currentcount);
			List<Tuple> result = query.getResultList();
			if(result != null && result.size()>0) {
				Tuple map = result.get(0);
				if("P".equalsIgnoreCase(map.get("status").toString())) {
					int count = Integer.parseInt(map.get("pdfCount").toString());
					if(count == 0) {
						reportName = "Original Policy";
					}else if(count == 1) {
						reportName = "Policy";
					}else if(count == 2) {
						reportName = "Duplicate Policy";
					}else if(count > 2) {
						reportName = "Copy Policy";
					}
					updateQuery.set(pmRoot.get("pdfCount"), count+1).where(cb.equal(pmRoot.get("quoteNo"), quoteNo));
					int updatecount = em.createQuery(updateQuery).executeUpdate();
					System.out.println(updatecount+" row updated.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportName;
	}
	public List<PortFolioAdminTupleRes> getPortFolioDashBoard(PortFolioDashBoardReq req) {
		List<PortFolioAdminTupleRes> list = new ArrayList<PortFolioAdminTupleRes>();
		try {

			Calendar cal = new GregorianCalendar();

			Date startDate = req.getStartDate();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			startDate = cal.getTime();

			Date endDate = req.getEndDate();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			endDate = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortFolioAdminTupleRes> query = cb.createQuery(PortFolioAdminTupleRes.class);

			// Find All
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);

			// Select
			query.multiselect(cb.count(h).alias("count"), cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc"), h.get("productId").alias("productId"),
					h.get("productName").alias("productName"), l.get("agencyCode").alias("agencyCode"),
					u.get("userName").alias("brokerName"), l.get("userType").alias("userType"),
					/* l.get("subUserType").alias("subUserType"), */l.get("oaCode").alias("oaCode"),
					cb.max(h.get("customerCode")).alias("customerCode"),
					cb.max(h.get("customerName")).alias("customerName"), h.get("sourceType").alias("sourceType"),
					cb.max(h.get("bdmCode")).alias("bdmCode"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("productName")));

			// Broker condition
			Subquery<String> loginId = query.subquery(String.class);
			Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
			loginId.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("loginId"), h.get("loginId"));
			Predicate a3 = cb.equal(ocpm1.get("oaCode").as(String.class), l.get("agencyCode"));
			loginId.where(a1, a2, a3);

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(h.get("loginId"), loginId));

			//			predicate.add(cb.greaterThanOrEqualTo(h.get("effectiveDate"), startDate));
			//			predicate.add(cb.lessThanOrEqualTo(h.get("effectiveDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(h.get("entryDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("entryDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("userType"), "Broker"));
			//			Expression<String> e0 = l.get("subUserType");
			//			predicate.add(e0.in("broker","direct"));
			//			predicate.add(cb.equal(l.get("subUserType"), "Broker"));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			predicate.add(cb.equal(l.get("companyId"), h.get("companyId")));
			if (StringUtils.isNotBlank(req.getLoginId())) {
				predicate.add(cb.equal(l.get("loginId"), req.getLoginId()));
			}

			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();

			if ("N".equalsIgnoreCase(businessType)) {
				predicate.add(cb.equal(h.get("status"), "P"));
				Predicate n1 = cb.isNull(h.get("endtStatus"));
				Predicate n2 = cb.equal(h.get("endtStatus"), "");
				predicate.add(cb.or(n1, n2));

			} else if ("E".equalsIgnoreCase(businessType)) {
				predicate.add(cb.equal(h.get("status"), "P"));
				predicate.add(cb.equal(h.get("endtStatus"), "C"));
				predicate.add(cb.notEqual(h.get("endtTypeId"), "842"));

			} else if ("C".equalsIgnoreCase(businessType)) {
				predicate.add(cb.equal(h.get("status"), "P"));
				predicate.add(cb.equal(h.get("endtStatus"), "C"));
				predicate.add(cb.equal(h.get("endtTypeId"), "842"));
			}

			// Product & Branch Condition
			if (StringUtils.isNotBlank(req.getProductId()))
				predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0]))
			.groupBy(h.get("productId"), h.get("productName"), l.get("agencyCode"), u.get("userName"),
					l.get("userType"), // , l.get("subUserType"),
					l.get("oaCode"), h.get("sourceType"))
			.orderBy(orderList);

			// Get Result
			TypedQuery<PortFolioAdminTupleRes> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	public synchronized List<CompanyProductMaster> getCompanyProductList(String companyId) {
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
			// Predicate n5 = cb.equal(c.get("productId"), productId);
			query.where(n1, n2, n3, n4).orderBy(orderList);
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
	
	
	public Map<String,Object> getEwayScheduleByRequestRef(String requestReferenceNo,String productId){
		log.info("Enter into getEwayScheduleByRequestRef.\nArgument ==> "+requestReferenceNo,productId);
		Map<String,Object> result = new HashMap<String,Object>();
		List<AttachMentRes> attachments = new ArrayList<>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			
			Root<?> hpmRoot = null;
			if("87".equalsIgnoreCase(productId)) {
				hpmRoot = cq.from(EserviceBuildingDetails.class);
			}else {
				hpmRoot = cq.from(EserviceCommonDetails.class);
			}
			 
			Root<EserviceCustomerDetails> piRoot = cq.from(EserviceCustomerDetails.class);
			Root<LoginUserInfo> luiRoot = cq.from(LoginUserInfo.class);
			Root<InsuranceCompanyMaster> icmRoot = cq.from(InsuranceCompanyMaster.class);
			Root<LoginBranchMaster> lbmRoot= cq.from(LoginBranchMaster.class);

			Subquery<Integer> cmAmd = cq.subquery(Integer.class);
			Root<CountryMaster> cmAmdRoot = cmAmd.from(CountryMaster.class);
			cmAmd.select(cb.max(cmAmdRoot.get("amendId"))).where(cb.equal(cmAmdRoot.get("countryId"), piRoot.get("nationality")),cb.equal(cmAmdRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(cmAmdRoot.get("status"), "Y"));

			Subquery<String> countryName = cq.subquery(String.class);
			Root<CountryMaster> ScmRoot = countryName.from(CountryMaster.class);
			countryName.select(ScmRoot.get("countryName")).where(cb.equal(ScmRoot.get("countryId"), piRoot.get("nationality")),cb.equal(ScmRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(ScmRoot.get("status"), "Y"),cb.equal(ScmRoot.get("amendId"), cmAmd));

			Subquery<Integer> icmAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> icmAmdRoot = icmAmd.from(InsuranceCompanyMaster.class);
			icmAmd.select(cb.max(icmAmdRoot.get("amendId"))).where(cb.equal(hpmRoot.get("companyId"), icmAmdRoot.get("companyId")),cb.equal(icmAmdRoot.get("status"), "Y"),
					cb.between(cb.literal(new Date()), icmAmdRoot.get("effectiveDateStart"), icmAmdRoot.get("effectiveDateEnd")));

			Subquery<String> companyName = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> companyNameRoot = companyName.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> companyNameAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> companyNameAmdRoot = companyNameAmd.from(InsuranceCompanyMaster.class);
			companyNameAmd.select(cb.max(companyNameAmdRoot.get("amendId"))).where(cb.equal(companyNameAmdRoot.get("companyId"), companyNameRoot.get("companyId")));
			companyName.select(companyNameRoot.get("companyName")).where(cb.equal(companyNameRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(companyNameRoot.get("amendId"), companyNameAmd));

			Subquery<String> imageURL = cq.subquery(String.class);
			Root<InsuranceCompanyMaster> imageURLRoot = imageURL.from(InsuranceCompanyMaster.class);
			//AMD MAX
			Subquery<Integer> imageURLAmd = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> imageURLAmdRoot = imageURLAmd.from(InsuranceCompanyMaster.class);
			imageURLAmd.select(cb.max(imageURLAmdRoot.get("amendId"))).where(cb.equal(imageURLAmdRoot.get("companyId"), imageURLRoot.get("companyId")));
			imageURL.select(imageURLRoot.get("companyLogo")).where(cb.equal(imageURLRoot.get("companyId"), hpmRoot.get("companyId")),
					cb.equal(imageURLRoot.get("amendId"), imageURLAmd));

			cq.multiselect(hpmRoot.get("policyNo").alias("policyNo"),hpmRoot.get("quoteNo").alias("quoteNo"),hpmRoot.get("requestReferenceNo").alias("requestReferenceNo"),cb.concat(piRoot.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(piRoot.get("titleDesc")), "")
					.when(cb.equal(piRoot.get("titleDesc"),""), "").otherwise(".").as(String.class),piRoot.get("clientName"))).alias("customerName"),piRoot.get("clientName").alias("clientName"),
					piRoot.get("address1").alias("address1"),piRoot.get("pinCode").alias("pinCode"),countryName.alias("countryName"),hpmRoot.get("entryDate").alias("entryDate"),
					piRoot.get("email1").alias("email1"),piRoot.get("mobileNo1").alias("mobileNo1"),hpmRoot.get("branchCode").alias("branchCode"),luiRoot.get("agencyCode").alias("agencyCode"),piRoot.get("idNumber").alias("identificationNo"),
					hpmRoot.get("policyStartDate").alias("inceptionDate"),hpmRoot.get("policyEndDate").alias("expiryDate"),hpmRoot.get("branchName").alias("branchName"),hpmRoot.get("brokerBranchName").alias("brokerBranchName"),
					hpmRoot.get("productDesc").alias("productName"),piRoot.get("stateName").alias("stateName"),piRoot.get("cityName").alias("cityName"),cb.concat(piRoot.get("mobileCodeDesc1"), cb.concat("-", piRoot.get("mobileNo1"))).alias("mobileNo"),
					piRoot.get("customerReferenceNo").alias("customerId"),cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Agent","Premia Direct","Premia Broker")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("brokerName"),luiRoot.get("coreAppBrokerCode").alias("coreAppBrokerCode"),hpmRoot.get("currency").alias("currency"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("actualPremiumLc")).otherwise(hpmRoot.get("actualPremiumFc")).alias("premium"),hpmRoot.get("vatPremium").alias("vatPremium"),
					cb.selectCase().when(cb.equal(icmRoot.get("currencyId"), hpmRoot.get("currency")), hpmRoot.get("overallPremiumLc")).otherwise(hpmRoot.get("overallPremiumFc")).alias("totalPremium"),
					icmRoot.get("signature").alias("signature"),lbmRoot.get("branchName").alias("place"),companyName.alias("companyName"),imageURL.alias("companylogo"),hpmRoot.get("companyId").alias("companyId"),hpmRoot.get("productId").alias("productId"),luiRoot.get("userMobile").alias("userMobile"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
					.when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("b2c","Direct")), "System").otherwise(luiRoot.get("userName")).alias("approvedBy"),
					cb.selectCase().when(cb.in(hpmRoot.get("sourceType")).value(Arrays.asList("Premia Broker","Premia Direct","Premia Agent")), hpmRoot.get("customerName"))
					.otherwise(luiRoot.get("userName")).alias("userName"),hpmRoot.get("bdmName").alias("bdmName"))
			.where(cb.equal(hpmRoot.get("customerReferenceNo"), piRoot.get("customerReferenceNo")),cb.equal(hpmRoot.get("agencyCode").as(String.class), luiRoot.get("agencyCode")),cb.equal(hpmRoot.get("companyId"), icmRoot.get("companyId")),
					cb.equal(hpmRoot.get("loginId"), lbmRoot.get("loginId")),cb.equal(hpmRoot.get("companyId"), lbmRoot.get("companyId")),cb.equal(hpmRoot.get("branchCode"), lbmRoot.get("branchCode")),cb.equal(lbmRoot.get("status"), "Y"),
					cb.equal(icmRoot.get("status"), "Y"),cb.between(cb.literal(new Date()), icmRoot.get("effectiveDateStart"), icmRoot.get("effectiveDateEnd")),cb.equal(icmRoot.get("amendId"), icmAmd),cb.equal(hpmRoot.get("requestReferenceNo"), requestReferenceNo));
			List<Tuple> list = em.createQuery(cq).getResultList();
			if(!CollectionUtils.isEmpty(list)) {
				Tuple map = list.get(0);
				List<FactorRateRequestDetails> coverData = factorRateRequestDetailsRepo.findByRequestReferenceNo(requestReferenceNo);

				CriteriaQuery<Tuple> cq1 = cb.createQuery(Tuple.class);
				Root<FactorRateRequestDetails> pcdRoot = cq1.from(FactorRateRequestDetails.class);
				Root<EserviceSectionDetails> sddRoot = cq1.from(EserviceSectionDetails.class);
				//List<EserviceCommonDetails> eserviceCommonList = eserviceCommonDetRepo.findByQuoteNo(map.get("quoteNo").toString());

				List<Predicate> predicate = new ArrayList<Predicate>();
				predicate.add(cb.equal(pcdRoot.get("requestReferenceNo"),map.get("requestReferenceNo")));
				predicate.add(cb.equal(pcdRoot.get("requestReferenceNo"),sddRoot.get("requestReferenceNo")));
				predicate.add(cb.equal(pcdRoot.get("sectionId").as(String.class), sddRoot.get("sectionId")));
				predicate.add(cb.equal(pcdRoot.get("taxId"),"0"));
				predicate.add(cb.equal(pcdRoot.get("discLoadId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("subCoverId"), "0"));
				predicate.add(cb.equal(pcdRoot.get("locationId"), sddRoot.get("locationId")));
				predicate.add(cb.equal(pcdRoot.get("vehicleId"), sddRoot.get("riskId")));
				predicate.add(cb.equal(pcdRoot.get("coverId"), sddRoot.get("coverId")));
				/*if(!eserviceCommonList.isEmpty()) {
					Root<EserviceCommonDetails> ecdRoot = cq1.from(EserviceCommonDetails.class);
					eserviceQuote = ecdRoot.get("occupationDesc").alias("occupationDesc");
					predicate.add(cb.equal(pcdRoot.get("quoteNo"), ecdRoot.get("quoteNo")));
					predicate.add(cb.equal(pcdRoot.get("sectionId"), ecdRoot.get("sectionId")));
					predicate.add(cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")));
					predicate.add(cb.equal(pcdRoot.get("productId"), ecdRoot.get("productId")));
					predicate.add(cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")));
				}*/
				Predicate [] predicateArray = new Predicate[predicate.size()];
				predicate.toArray(predicateArray);

				Subquery<String> occDesc = cq1.subquery(String.class);
				Root<EserviceCommonDetails> ecdRoot = occDesc.from(EserviceCommonDetails.class);
				occDesc.select(ecdRoot.get("occupationDesc")).where(cb.equal(pcdRoot.get("requestReferenceNo"), ecdRoot.get("requestReferenceNo")),cb.equal(pcdRoot.get("sectionId").as(String.class), ecdRoot.get("sectionId")),
						cb.equal(pcdRoot.get("vehicleId"), ecdRoot.get("riskId")),cb.equal(pcdRoot.get("productId").as(String.class), ecdRoot.get("productId")),
						cb.equal(pcdRoot.get("companyId"), ecdRoot.get("companyId")),cb.equal(pcdRoot.get("locationId"), ecdRoot.get("locationId")),
						cb.equal(pcdRoot.get("coverId"), ecdRoot.get("coverId")));

				cq1.multiselect(sddRoot.get("sectionId").alias("sectionId"),sddRoot.get("sectionName").alias("sectionDesc"),pcdRoot.get("coverDesc").alias("coverDesc"),
						pcdRoot.get("coverId").alias("coverId"),pcdRoot.get("coverageType").alias("coverageType"),
						pcdRoot.get("sumInsured").alias("sumInsured"),pcdRoot.get("rate").alias("rate"),pcdRoot.get("premiumIncludedTaxLc").alias("premiumIncludedTaxLc"),
						pcdRoot.get("premiumIncludedTaxFc").alias("premiumIncludedTaxFc"),occDesc.alias("occupationDesc"),
						pcdRoot.get("premiumExcludedTaxLc").alias("premiumExcludedTaxLc"),pcdRoot.get("premiumExcludedTaxFc").alias("premiumExcludedTaxFc"),
						sddRoot.get("locationId").alias("locationId"),sddRoot.get("locationName").alias("locationName"),
						sddRoot.get("productType").alias("productType"),pcdRoot.get("coverageLimit").alias("coverageLimit"))
				.where(predicateArray).orderBy(cb.asc(sddRoot.get("sectionId")));

				List<Tuple> Slist = em.createQuery(cq1).getResultList();
				List<Map<String,Object>>sectList=new ArrayList<>();
				Double minAdjPrem=0.0,minAdjPremFc=0.0,basePremium=0.0,basePremiumFc=0.0,
						minAdjExPrem=0.0,minAdjExPremFc=0.0,baseExPremium=0.0,baseExPremiumFc=0.0;

				for (int i=0;i<Slist.size();i++) {
					Tuple t=Slist.get(i);
					String coverId = t.get("coverId")==null?"":t.get("coverId").toString();
					String coverageType = t.get("coverageType")==null?"":t.get("coverageType").toString();
					if("945".equals(coverId)){
						minAdjPrem=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						minAdjPremFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						minAdjExPrem=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						minAdjExPremFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if("B".equals(coverageType)) {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
						basePremium=basePremium+minAdjPrem;
						basePremiumFc=basePremiumFc+minAdjPremFc;
						baseExPremium=baseExPremium+minAdjExPrem;
						baseExPremiumFc=baseExPremiumFc+minAdjExPremFc;
					}else {
						basePremium=t.get("premiumIncludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxLc").toString());
						basePremiumFc=t.get("premiumIncludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumIncludedTaxFc").toString());
						baseExPremium=t.get("premiumExcludedTaxLc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxLc").toString());
						baseExPremiumFc=t.get("premiumExcludedTaxFc")==null?0.0:Double.parseDouble(t.get("premiumExcludedTaxFc").toString());
					}
					if(!"945".equals(coverId)){
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", t.get("sectionDesc"));
						Smap.put("occupationDesc", t.get("occupationDesc"));
						Smap.put("coverDesc", t.get("coverDesc"));
						Smap.put("sumInsured", t.get("sumInsured"));
						Smap.put("rate", t.get("rate"));
						Smap.put("premiumIncludedTaxLc", basePremium);
						Smap.put("premiumIncludedTaxFc", basePremiumFc);
						Smap.put("premiumExcludedTaxLc", baseExPremium);
						Smap.put("premiumExcludedTaxFc", baseExPremiumFc);
						sectList.add(Smap);
					}
				}
				List<Map<String,Object>> sectionList = new ArrayList<Map<String,Object>>();
				List<Map<String,Object>> coverageDetails = new ArrayList<Map<String,Object>>();
				List<TaxInvoicePremiumDetails> premiumDetailsRes = new ArrayList<>();
				Double OverAllPremium=0.0;
				String companyId = map.get("companyId")==null?"":map.get("companyId").toString();
				List<Object> sectionIds = Slist.stream().map(k -> k.get("sectionId")).distinct().collect(Collectors.toList());
				List<Object> coverIds = Slist.stream().map(k -> k.get("coverId")).distinct().collect(Collectors.toList());
				if("100002".equalsIgnoreCase(companyId) || "100020".equalsIgnoreCase(companyId)) {
					if(coverData!=null && !coverData.isEmpty()) {
						Double taxRate = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T"))
								.map(m -> m.getTaxRate()).map(BigDecimal::doubleValue)
								.findAny().orElse(0.0);

						Double taxAmount = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
								.map(i -> i.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue));


						List<Map<String,Object>> sectionPremium = new ArrayList<Map<String,Object>>();
						for(int x=0;x<sectionIds.size();x++) {
							int s = Integer.parseInt(sectionIds.get(x).toString());
							System.out.println(new Gson().toJson(coverData.stream().filter(f -> f.getSectionId()==s)
									.collect(Collectors.toList())));
							List<Integer> coverids = coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s)
									.map(m -> m.getCoverageType().equalsIgnoreCase("L")?m.getDiscLoadId():m.getCoverId()).distinct()
									.collect(Collectors.toList());
							for(int j=0;j<coverids.size();j++) {
								int c = coverids.get(j);
								Map<String,Object> o = new HashMap<String,Object>();
								o.put("SectionId", s);
								o.put("CoverDesc", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
										&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
										.map(m -> m.getCoverName()).findFirst().orElse("-N-A"));
								o.put("TotPremium", coverData.stream().filter(f -> (!f.getCoverageType().equalsIgnoreCase("T")) && f.getSectionId()==s
										&& (f.getCoverageType().equalsIgnoreCase("L")?f.getDiscLoadId():f.getCoverId())==c)
										.map(m -> m.getPremiumExcludedTaxFc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
								sectionPremium.add(o);
							}
						}
						String occupationValue = Slist.stream()
								.filter(t -> t.get("occupationDesc") != null && 
								!t.get("occupationDesc").toString().equals("null") &&
								!t.get("occupationDesc").toString().isEmpty())
								.map(t -> t.get("occupationDesc").toString())
								.findFirst()
								.orElse("Not Specified");

						// Add occupation to result
						result.put("occupation", occupationValue);


						/*						List<Map<String,Object>> sectionPremium = coverData.stream().filter(f ->f.getTaxId()==0 && (f.getDiscLoadId()==0 || f.getCoverageType().equalsIgnoreCase("L")) && f.getSectionId()!=99999
								&& (f.getCoverageType().equals("O") && Arrays.asList("Y","D").contains(f.getIsSelected().equalsIgnoreCase("Y")?"Y":f.getIsSelected().equalsIgnoreCase("D")?"D":"N") 
								|| !f.getCoverageType().equalsIgnoreCase("O")))
								.collect(Collectors.groupingBy(a -> a.getSectionId(),Collectors.groupingBy(b -> b.getCoverId(),Collectors.reducing(
									BigDecimal.ZERO, PolicyCoverData::getPremiumExcludedTaxLc, BigDecimal::add))))
								.entrySet().stream()
								.flatMap((Map.Entry<Integer,Map<Integer,BigDecimal>> s ) -> {
									Integer sectionId = s.getKey();
									return s.getValue().entrySet().stream()
											.map((Map.Entry<Integer,BigDecimal> g )-> {
												Integer coverId = g.getKey();
												BigDecimal totPremium = g.getValue();
												Map<String,Object> secMap = new HashMap<String,Object>();
												secMap.put("SectionId", sectionId);
												secMap.put("CoverDesc", coverData.stream().filter(f -> f.getTaxId()==0
														&& (f.getDiscLoadId()==0 || f.getCoverageType().equalsIgnoreCase("L")) && f.getSectionId()==sectionId && f.getCoverId()==coverId)
														.map(m -> m.getCoverName()).findFirst().orElse("-N-A"));
												secMap.put("TotPremium", totPremium);
												return secMap;
											});
								}).sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());*/

						sectionPremium = sectionPremium.stream().sorted(Comparator.comparing(p -> (String) p.get("CoverDesc")))
								.collect(Collectors.toList());
						if("100020".equalsIgnoreCase(companyId)) {
							sectionPremium = sectionPremium.stream().filter(f -> new BigDecimal(f.get("TotPremium").toString()).compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
							List<FactorRateRequestDetails> spData = coverData.stream().filter(f -> f.getTaxId()!=0 && f.getCoverageType().equalsIgnoreCase("T") && f.getSectionId()!=99999)
									.collect(Collectors.toList());
							if(spData !=null && spData.size()>0) {
								List<Map<String,Object>> subspData = new ArrayList<Map<String,Object>>();
								spData.forEach(sp -> {
									Map<String,Object> spMap = new HashMap<String,Object>();
									spMap.put("SectionId", sp.getSectionId());
									spMap.put("CoverDesc", sp.getCoverName()+" ("+sp.getTaxRate()+"% )");
									spMap.put("TotPremium", sp.getTaxAmount());
									subspData.add(spMap);
								});
								sectionPremium.addAll(subspData);
							}
						}
						sectionPremium.forEach(k -> {
							TaxInvoicePremiumDetails u = TaxInvoicePremiumDetails.builder()
									.amount(new BigDecimal(Double.valueOf(k.get("TotPremium").toString())).toString())
									.narration(k.get("CoverDesc")==null?"":k.get("CoverDesc").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", ""))
									.build();
							premiumDetailsRes.add(u);
						});


						OverAllPremium = premiumDetailsRes.stream().map(k -> new BigDecimal(k.getAmount())).collect(Collectors.summingDouble(BigDecimal::doubleValue))+taxAmount;
						result.put("vatPercent", taxRate.toString());
						result.put("vatAmount", taxAmount.toString());
					}
				}else if("100004".equalsIgnoreCase(companyId)){
					sectList.forEach(k -> {
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("sectionDesc", k.get("sectionDesc"));
						Smap.put("coverDesc", k.get("coverDesc"));
						Smap.put("sumInsured", k.get("sumInsured"));
						Smap.put("rate", k.get("rate"));
						Smap.put("premiumIncludedTaxLc", k.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", k.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", k.get("premiumExcludedTaxLc"));
						Smap.put("premiumExcludedTaxFc", k.get("premiumExcludedTaxFc"));
						Smap.put("vehicleId",k.get("vehicleId"));
						sectionList.add(Smap);
						result.put("occupationDesc", sectList.stream().filter(f -> f.get("occupationDesc") != null && !f.get("occupationDesc").toString().equals("null")).map(m -> m.get("occupationDesc"))
								.map(Object::toString).findAny().orElse(null));
					});
				}else {
					Map<Object, List<Map<String,Object>>> sectionRes = sectList.stream().collect(Collectors.groupingBy(g -> g.get("sectionDesc"),Collectors.mapping(v ->{
						Map<String,Object> Smap = new HashMap<String,Object>();
						Smap.put("occupationDesc", v.get("occupationDesc"));
						Smap.put("coverDesc", v.get("coverDesc"));
						Smap.put("sumInsured", v.get("sumInsured"));
						Smap.put("rate", v.get("rate"));
						Smap.put("premiumIncludedTaxLc", v.get("premiumIncludedTaxLc"));
						Smap.put("premiumIncludedTaxFc", v.get("premiumIncludedTaxFc"));
						Smap.put("premiumExcludedTaxLc", v.get("premiumExcludedTaxLc"));
						Smap.put("premiumExclhitudedTaxFc", v.get("premiumExcludedTaxFc"));
						return Smap;
					}, Collectors.toList())));			
					for(Map.Entry<Object, List<Map<String,Object>>> entry :sectionRes.entrySet()) {
						Map<String, Object> sectionMap = new HashMap<String, Object>();
						sectionMap.put("sectionKey", entry.getKey());
						sectionMap.put("sectionValue", entry.getValue());
						sectionList.add(sectionMap);
					}
				}

				List<Object> locationIds = Slist.stream().map(k -> k.get("locationId")).distinct().collect(Collectors.toList());
				List<Map<String,Object>> coverageList = new ArrayList<Map<String,Object>>();
				for(int i=0;i<coverIds.size();i++) {
					Map<String,Object> coverMap = new HashMap<String,Object>();
					List<Map<String,Object>> buildingDetails = new ArrayList<Map<String,Object>>();

					List<Map<String,Object>> allriskDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> contentDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> electronicDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> ownersDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> excessConDetails = new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> personalAccDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> exclusionDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> excessDetails = new ArrayList<Map<String,Object>>();
					List<Map<String, Object>> clauseDetails = new ArrayList<Map<String,Object>>();
					String coverId = coverIds.get(i).toString() ;
					String sectionId = Slist.stream().filter(f -> f.get("coverId").toString().equalsIgnoreCase(coverId))
							.map(z -> z.get("sectionId").toString()).findFirst().orElse("");
					log.info("Current Lopping SectionId :: "+ sectionId);
					for(int x=0;x<locationIds.size();x++) {
						String locationId = locationIds.get(x).toString();

						String locationName = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))).map(r -> r.get("locationName").toString()).findFirst().get();
						/*String productType = Slist.stream().filter(f -> f.get("locationId").equals(Integer.parseInt(locationId))
								&& f.get("sectionId").equals(sectionId)).map(t -> t.get("productType")).map(Object::toString).findFirst().orElse("");*/
						List<EserviceBuildingDetails> buildingRiskData = eserviceBuildingRiskDetailsRepo
								.findByRequestReferenceNoAndSectionIdAndLocationIdAndCoverId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId),Integer.parseInt(coverId));
						List<Map<String,Object>> locationList = buildingRiskData.stream().map(k ->{
							LinkedHashMap<String,Object> lmap = new LinkedHashMap<String,Object>();
							lmap.put("locationName", k.getLocationName());
							lmap.put("buildingAddress", k.getAddress());
							lmap.put("sectionId", sectionId);
							lmap.put("sectiondesc", k.getSectionDesc());
							lmap.put("wallTypeDesc", k.getWallTypeDesc()==null?"":k.getWallTypeDesc());
							lmap.put("roofType", k.getRoofTypeDesc());
							lmap.put("firstlosspayee", k.getFirstLossPercent());
							lmap.put("coveringdetails", k.getCoveringDetails());
							lmap.put("descriptionofrisk", k.getDescriptionOfRisk());
							lmap.put("buildingSumInsured", k.getSumInsured());
							lmap.put("industrydesc", k.getIndustryDesc());
							lmap.put("buildingAge", k.getBuildingAge()==null?"":k.getBuildingAge() );
							lmap.put("wallType", k.getWallType());
							lmap.put("coverId", k.getCoverId()==null?"":k.getCoverId() );


							lmap.put("bondyear", k.getBondYear());
							lmap.put("sumInsured", k.getSumInsured());
							lmap.put("buildingSumInsured", k.getSumInsured());
							lmap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
							lmap.put("premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
									&& f.getSectionId()==Integer.parseInt(sectionId))
									.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
							//							lmap.put("moneyAnnualEstimate", k.getMoneyAnnualEstimate());
							//							lmap.put("moneyCollector", k.getMoneyCollector());
							//							lmap.put("moneyDirectorResidence", k.getMoneyDirectorResidence());
							//							lmap.put("moneyOutofSafe", k.getMoneyOutofSafe());
							//							lmap.put("moneySafeLimit", k.getMoneySafeLimit());
							//							lmap.put("moneyMajorLoss", k.getMoneyMajorLoss());
							lmap.put("indemityPeriodDesc", k.getIndemityPeriodDesc());
							lmap.put("categoryDesc", k.getCategoryDesc());
							lmap.put("contentDesc", k.getContentDesc());
							lmap.put("firstLossPercent", k.getFirstLossPercent());
							lmap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("coverId").equals(coverId) && f.get("coverNoteReferenceNo")!=null)
									.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
							return lmap;
						}).collect(Collectors.toList());



						if("105".equalsIgnoreCase(coverId)){ // Building
							buildingDetails.addAll(locationList);
						}else if("45".equalsIgnoreCase(coverId)){ // All Risk
							allriskDetails.addAll(locationList);
						}else if("290".equalsIgnoreCase(coverId)){ //Content
							contentDetails.addAll(locationList);
						}else if("90".equalsIgnoreCase(coverId)){ // Electronic Equipment
							electronicDetails.addAll(locationList);
						}else if("593".equalsIgnoreCase(coverId)) { //Owners liability
							ownersDetails.addAll(locationList);
						}

						//							List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationIdAndCoverId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId),Integer.parseInt(coverId));
						//							List<Map<String,Object>> commonList = comDetails.stream()
						//									.collect(Collectors.groupingBy(k -> k.getRiskId(), Collectors.mapping(o -> {
						//										LinkedHashMap<String,Object> empMap = new LinkedHashMap<String,Object>();
						//										empMap.put("locationName", locationName);
						//										empMap.put("occupationDesc", o.getOccupationDesc());
						//										empMap.put("sumInsured", o.getSumInsured());
						//										empMap.put("count", o.getCount()==null?0:o.getCount());
						//										empMap.put("Rate", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
						//												&& f.getTaxId()!=0 && f.getSectionId()==Integer.parseInt(o.getSectionId())
						//												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getRate()).findAny().orElse(BigDecimal.ZERO));
						//										empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
						//												&& f.getSectionId()==Integer.parseInt(sectionId)
						//												&& f.getVehicleId()==o.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
						//										return empMap;
						//									}, Collectors.toList()))).entrySet()
						//									.stream().map(g -> {
						//										LinkedHashMap<String,Object> eMap = new LinkedHashMap<String,Object>();
						//										eMap.put("occupationDesc", g.getValue().stream().map(t -> String.valueOf(t.get("occupationDesc"))).collect(Collectors.joining("<br>")));
						//										eMap.put("Rate", g.getValue().stream().map(t -> t.get("Rate")).findFirst().get());
						//										eMap.put("count", g.getValue().stream().map(t -> t.get("count")).findFirst().get());
						//										eMap.put("sumInsured", g.getValue().stream().map(j -> (BigDecimal) j.get("sumInsured")).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
						//										eMap.put("premium", g.getValue().stream().map(h -> h.get("Premium")).findFirst().get());
						//										eMap.put("tiraCoverNo", Slist.stream().filter(f -> f.get("sectionId").equals(sectionId) && f.get("coverNoteReferenceNo")!=null)
						//												.map(b -> b.get("coverNoteReferenceNo")).distinct().findAny().orElse(""));
						//										eMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
						////										eMap.put("wallTypeDesc", map.get("wallTypeDesc")==null?"":map.get("wallTypeDesc").toString());
						////										eMap.put("buildingAge", map.get("buildingAge")==null?"":map.get("buildingAge").toString()  );
						////										eMap.put("wallType", map.get("wallType")==null?"":map.get("wallType").toString() );
						////										eMap.put("coverId", map.get("coverId")==null?"":map.get("coverId").toString()  );
						//										
						//										eMap.put("locationName", g.getValue().stream().map(j -> j.get("locationName")).findFirst().get());
						//										return eMap;
						//									}).collect(Collectors.toList());
						//							ownersDetails.addAll(commonList);
						else if("13".equalsIgnoreCase(map.get("productId")==null?"":map.get("productId").toString())){
							List<CommonDataDetails> comDetails = commonDataDetailsRepo.findByRequestReferenceNoAndSectionIdAndLocationId(map.get("requestReferenceNo").toString(),sectionId,Integer.parseInt(locationId));
							if(comDetails !=null && comDetails.size()>0) {
								comDetails.forEach(us -> {
									Map<String,Object> empMap = new HashMap<String,Object>();
									empMap.put("locationName", locationName);
									empMap.put("Quoteoccupation", us.getCategoryDesc());
									empMap.put("sumInsured", us.getSumInsured());
									empMap.put("customerName", us.getNickName());
									empMap.put("dateOfBrith", sdf.format(us.getDob()));
									empMap.put("options", us.getSectionDesc());
									empMap.put("estAnnualEarnings", us.getOtherOccupation());
									empMap.put("Premium", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId)
											&& f.getVehicleId()==us.getRiskId()).map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									empMap.put("coverLimit", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
											&& f.getSectionId()==Integer.parseInt(sectionId) && f.getCoverageType().equalsIgnoreCase("B")
											&& f.getVehicleId()==us.getRiskId()).map(u -> u.getCoverageLimit()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
									empMap.put("currency", map.get("currency")==null?"":map.get("currency").toString());
									personalAccDetails.add(empMap);
								});	


							}
						}else if ("246".equalsIgnoreCase(sectionId)) { // Member Section

							Map<String, Object> finalResponse = new LinkedHashMap<>();

							List<ListItemValue> memberList = ewayListItemValueRepo
									.findByItemTypeAndItemCodeOrderByItemCodeAsc("Members", sectionId);

							List<Map<String, Object>> memberDetails = new ArrayList<>();

							String categoryDes = ""; 
							if (memberList != null && !memberList.isEmpty()) {

								Optional<InsuranceCompanyMaster> companyOpt = ewayInsuranceCompanyMasterRepo
										.findTopByCompanyIdOrderByAmendIdAsc(companyId);  

								String companyWebsite = companyOpt.isPresent() && companyOpt.get().getCompanyWebsite() != null
										? companyOpt.get().getCompanyWebsite()
												: "";

								memberDetails = memberList.stream().map(val -> {
									Map<String, Object> m = new LinkedHashMap<>();


									//							        BuildingRiskDetails matchedBuilding = buildingRiskDetailsRepo.findByQuoteNo(QuoteNo);
									//							        	    
									//							        if (matchedBuilding.isPresent()) {
									//							            BuildingRiskDetails brDetails = matchedBuilding.get();
									////							            m.put("MembersValue", brDetails.getMoneyCollector() != null ? brDetails.getMoneyCollector() : "");
									////							            m.put("occupation", brDetails.getCategoryDesc() != null ? brDetails.getCategoryDesc() : "");
									//							            String categoryDesc = brDetails.getCategoryDesc() != null ? brDetails.getCategoryDesc() : "";
									//
									//							            m.put("occupation", categoryDesc);
									//							          
									//							            
									//							            
									//							        } else {
									//							            m.put("MembersValue", "");
									//							            m.put("occupation", "");
									//							        }

									m.put("interestsInsured", val.getItemValue());
									m.put("locationName", locationName);
									m.put("sectionId", sectionId); 
									m.put("companyWebsite", companyWebsite);

									return m;
								}).collect(Collectors.toList());
							}
							

							if (buildingRiskData != null) {
								categoryDes = Optional.ofNullable(buildingRiskData.get(0).getCategoryDesc()).orElse("");
								String categoryId = Optional.ofNullable(buildingRiskData.get(0).getCategoryId()).orElse("");

								 if (attachments.isEmpty()) {
									String attachmentType = "MISCELLANEOUS";
									if ("1".equalsIgnoreCase(categoryId)) attachmentType = "ADVOCATES";
									else if ("3".equalsIgnoreCase(categoryId)) attachmentType = "ENGINEERING";
									else if ("2".equalsIgnoreCase(categoryId)) attachmentType = "ACCOUNTANTS";
									
									   

									attachments.addAll(getAttachMentList(companyId, buildingRiskData.get(0).getProductId(), attachmentType, null));
									 
								}
								result.put("attachMents", attachments);
								
								Map<String, Object> occupationMap = new LinkedHashMap<>();
								occupationMap.put("occupation1", categoryDes);
								result.put("occupationDetails", occupationMap);
							}

							List<EserviceBuildingDetails> mem = eserviceBuildingRiskDetailsRepo
									.findByRequestReferenceNoAndCoverId(map.get("requestReferenceNo").toString(), 632);

							List<Map<String, Object>> membersList = new ArrayList<>();

							if (mem != null && !mem.isEmpty()) {

								for (EserviceBuildingDetails item : mem) {

									Map<String, Object> membersMap = new LinkedHashMap<>();

									//							        String categoryDesc = Optional.ofNullable(item.getCategoryDesc()).orElse("");
									//							        String categoryId   = Optional.ofNullable(item.getCategoryId()).orElse("");

									String contentId = Optional.ofNullable(item.getContentId()).orElse("");
									String contentDesc = Optional.ofNullable(item.getContentDesc()).orElse("");
									Integer coverId1 = Optional.ofNullable(item.getCoverId()).orElse(0);
									//							        String sectionId = Optional.ofNullable(item.getSectionId()).orElse("");
									//							        String companyId = Optional.ofNullable(item.getCompanyId()).orElse("");
									String wallType = Optional.ofNullable(item.getWallType()).orElse("");
									String wallTypeDesc = Optional.ofNullable(item.getWallTypeDesc()).orElse("");
									Integer buildingFloors = Optional.ofNullable(item.getBuildingFloors()).orElse(0);
									BigDecimal sumInsured = Optional.ofNullable(item.getSumInsured()).orElse(BigDecimal.ZERO);
									String indemityPeriod = Optional.ofNullable(item.getIndemityPeriod()).orElse("");

									//							        membersMap.put("categoryDesc", categoryDesc);
									//							        membersMap.put("categoryId", categoryId);


									membersMap.put("contentId", contentId);
									membersMap.put("contentDesc", contentDesc);
									membersMap.put("coverId", coverId1);
									membersMap.put("sectionId", sectionId);
									membersMap.put("companyId", companyId);
									membersMap.put("productId", productId);
									membersMap.put("wallType", wallType);
									membersMap.put("wallTypeDesc", wallTypeDesc);
									membersMap.put("buildingFloors", buildingFloors);
									membersMap.put("sumInsured", sumInsured);
									membersMap.put("indemityPeriod", indemityPeriod);

									membersList.add(membersMap);
								}
							}

							result.put("membersList", membersList);

							EserviceBuildingDetails mem1 = eserviceBuildingRiskDetailsRepo
									.findFirstByRequestReferenceNoAndCoverId(map.get("requestReferenceNo").toString(), 632);
							BigDecimal sumInsured = BigDecimal.ZERO;
							String indemityPeriod = "";
							String wallType="";
							String wallTypeDesc="";
							String indemityPeriodDesc="";
							if(mem1 != null) {
								sumInsured = Optional.ofNullable(mem1.getSumInsured()).orElse(BigDecimal.ZERO);
								indemityPeriod = Optional.ofNullable(mem1.getIndemityPeriod()).orElse("");
								wallType = Optional.ofNullable(mem1.getWallType()).orElse("");
								wallTypeDesc = Optional.ofNullable(mem1.getWallTypeDesc()).orElse("");
							    //indemityPeriodDesc = (String)Optional.ofNullable(mem1.getIndemityPeriodDesc()).orElse("");
							}
							List<EserviceBuildingDetails> memList = eserviceBuildingRiskDetailsRepo
									.findByRequestReferenceNoAndCoverId(map.get("requestReferenceNo").toString(), 631);  
							if (memList != null && !memList.isEmpty()) {
							    EserviceBuildingDetails firstMem = memList.get(0);  
							    indemityPeriodDesc = Optional.ofNullable(firstMem.getIndemityPeriodDesc()).orElse("");
							}
							result.put("GrossIncome",sumInsured);
							result.put("limitIndemity",indemityPeriod);
							result.put("PerLimitOfIndemity",wallType);
							result.put("PerLimitOfIndemityDesc",wallTypeDesc);
							result.put("indemityPeriodDesc", indemityPeriodDesc);

							finalResponse.put("memberSection", memberDetails);


							List<ExcessMaster> excessList = excessRepo
									.findByProductIdAndCompanyIdAndSectionId(
											productId, companyId, sectionId);

							excessDetails = excessList.stream().map(e -> {
								Map<String, Object> m = new LinkedHashMap<>();
								m.put("coverName", e.getCoverName());
								m.put("excessPercentage", e.getExcessPercentage());
								return m;
							}).collect(Collectors.toList());

							result.put("excessDetails", excessDetails);


							List<ExclusionMaster> exclusionList =  exclusionMasterRepo
									.findExclusionByProductIdAndCompanyIdAndSectionId(
											productId, companyId, sectionId);

							exclusionDetails = exclusionList.stream().map(ex -> {
								Map<String, Object>  m = new LinkedHashMap<>();
								m.put("exclusionDescription", ex.getExclusionDescription());
								return  m;
							}).collect(Collectors.toList());

							result.put("exclusionDetails", exclusionDetails);


							List<ClausesMaster> clauseList = clausesMasterRepo
									.findClausesByProductIdAndCompanyIdAndSectionId(
											productId, companyId, sectionId);

							clauseDetails = clauseList.stream().map(c -> {
								Map<String, Object>  m = new LinkedHashMap<>();
								m.put("clausesDescription", c.getClausesDescription());
								return m;
							}).collect(Collectors.toList());

							result.put("clauseDetails", clauseDetails);


							List<FactorRateRequestDetails> coverList = factorRateRequestDetailsRepo.findByRequestReferenceNoAndCoverageTypeAndVehicleId(requestReferenceNo,"T", 99999);

							List<Map<String, Object>> policyCoverDetails = coverList.stream().map(p -> {
								Map<String, Object> m = new LinkedHashMap<>();

								m.put("coverName", p.getCoverName());						    
								m.put("taxAmount", p.getTaxAmount());

								//						         if(p.getCoverageType().equals("T")) {
								//						           m.put("coverName", p.getCoverName());
								//						      m.put("taxAmount", p.getTaxAmount());
								//						         }
								//						        

								return m;
							}).collect(Collectors.toList());

							result.put("policyCoverData", policyCoverDetails);


							Map<String, Object> premiumDetails = new LinkedHashMap<>();
							if (coverData != null) {
								premiumDetails.put("premiumLc", coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0
										&& f.getSectionId()==Integer.parseInt(sectionId))
										.map(u -> u.getPremiumExcludedTaxLc()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
								premiumDetails.put("renewalDate", RenewalDate(map.get("expiryDate").toString()));
													} else {
								premiumDetails.put("premiumLc", null);
								premiumDetails.put("renewalDate", null);
							} 
							premiumDetails.put("Quotation Number", map.get("requestReferenceNo").toString());


							//						    log.info("Member Section (--) Data => " + finalResponse);
							//						    return ResponseEntity.ok(finalResponse);
						}
						
						// CONDITIONS
						List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						// EXCLUSION
						List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
						List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
							Map<String,Object> eMap = new HashMap<String,Object>();
							eMap.put("conditionTerms", k.get("exclusioTerms"));
							eMap.put("SectionId", k.get("SectionId"));
							return eMap;
						}).collect(Collectors.toList());

						//WARRANTY
						List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

						List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList,conditionList,exclusionList).flatMap(Collection::stream)
								.sorted(Comparator.comparing(p -> {
									if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
										return Integer.MAX_VALUE;
									}
									try {
										return Integer.parseInt(p.get("Sno").toString());
									} catch (NumberFormatException e) {
										return Integer.MAX_VALUE;
									}
								}))
								.map(u -> {
									LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
									m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
									return m;
								}).distinct().collect(Collectors.toList());

						int conditionsize = termsAndconditions.size();
						List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
						if(conditionsize>10) {
							int midIndex = conditionsize / 2;
							firstHalf = termsAndconditions.subList(0, midIndex);
							secondHalf = termsAndconditions.subList(midIndex, conditionsize);
						}else {
							firstHalf = termsAndconditions.subList(0, conditionsize);
						}

						//						List<PolicyCoverData> excessCon = coverData.stream().filter(f -> f.getTaxId()==0 && f.getDiscLoadId()==0 && f.getCoverageType().equalsIgnoreCase("B") && f.getSectionId()==Integer.parseInt(sectionId)).distinct().collect(Collectors.toList());
						//						if(!excessCon.isEmpty()) {
						//							PolicyCoverData cpd = excessCon.get(0);
						//							Map<String,Object> excessMap = new HashMap<String,Object>();
						//							excessMap.put("excessPercent", cpd.getExcessPercent());
						//							excessMap.put("excessAmount", cpd.getExcessAmount());
						//							excessMap.put("excessDesc", cpd.getExcessDesc());
						//							excessMap.put("currency", cpd.getCurrency());
						//							excessConDetails.add(excessMap);
						//						}
						List<ExcessMaster> excess1=excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(companyId,productId ,sectionId, coverId);	
						if(!excess1.isEmpty() ) {
							ExcessMaster cpd = excess1.get(0);
							Map<String,Object> excessMap = new HashMap<String,Object>();
							excessMap.put("excessPercent", cpd.getExcessPercentage()==null?0:cpd.getExcessPercentage());
							excessMap.put("excessAmount", cpd.getExcessAmount()==null?0.0:cpd.getExcessAmount());
							excessMap.put("excessDesc", cpd.getExcessDescription()==null?"":cpd.getExcessDescription());
							excessMap.put("currency", cpd.getCurrency()==null?"":cpd.getCurrency());
							excessConDetails.add(excessMap);
						}


						coverMap.put("sectionDesc", Slist.stream().filter(k -> sectionId.equalsIgnoreCase(k.get("sectionId").toString())).map(e -> e.get("sectionDesc").toString()).findFirst().orElse(""));
						coverMap.put("coverDesc", Slist.stream().filter(k -> coverId.equalsIgnoreCase(k.get("coverId").toString())).map(e -> e.get("coverDesc").toString()).findFirst().orElse(""));
						coverMap.put("buildingDetails", buildingDetails);
						coverMap.put("allriskDetails", allriskDetails);
						coverMap.put("contentDetails", contentDetails);
						//						coverMap.put("domesticDetails", domesticDetails);
						coverMap.put("electronicDetails",electronicDetails);
						coverMap.put("ownersDetails", ownersDetails);
						coverMap.put("firstHalfconditions", firstHalf);
						coverMap.put("secondHalfconditions", secondHalf);
						coverMap.put("excessConditions", excessConDetails);
						coverMap.put("personalAccDetails", personalAccDetails);
						coverMap.put("clauseDetails", clauseDetails);
						coverMap.put("excessDetails", excessDetails);
						coverMap.put("exclusionDetails", exclusionDetails);
						coverMap.put("sectionId", sectionId);
						coverageList.add(coverMap);
					}
				}
				
				List<FactorRateRequestDetails> coverList = factorRateRequestDetailsRepo.findByRequestReferenceNoAndVehicleIdOrderByCoverIdAsc(requestReferenceNo, 99999);
				
				List<Map<String, Object>> policyCoverDetails = new ArrayList<>();
				
				coverList.stream()
				        .filter(c -> "B".equalsIgnoreCase(c.getCoverageType())
				                || "O".equalsIgnoreCase(c.getCoverageType()))
				        .forEach(c -> {
				            Map<String, Object> entry = new LinkedHashMap<>();
				            entry.put("coverName", c.getCoverName()); // Cover Name

				            BigDecimal premium = c.getPremiumExcludedTaxLc() == null
				                    ? BigDecimal.ZERO
				                    : c.getPremiumExcludedTaxLc();

				            entry.put("taxAmount", premium.toPlainString());
				            policyCoverDetails.add(entry);
				        });


				
				Map<Integer, BigDecimal> taxSummary =
				        coverList.stream()
				                .filter(c -> "T".equalsIgnoreCase(c.getCoverageType()))
				                .filter(c -> c.getTaxId() != null)
				                .collect(Collectors.groupingBy(
				                		FactorRateRequestDetails::getTaxId,
				                        LinkedHashMap::new,
				                        Collectors.reducing(
				                                BigDecimal.ZERO,
				                                c -> c.getTaxAmountLc() == null
				                                        ? BigDecimal.ZERO
				                                        : c.getTaxAmountLc(),
				                                BigDecimal::add
				                        )
				                ));


				
				taxSummary.forEach((taxId, totalTax) -> {

				    
					FactorRateRequestDetails anyTaxRow = coverList.stream()
				            .filter(c -> taxId.equals(c.getTaxId()))
				            .findFirst()
				            .orElse(null);

				    if (anyTaxRow != null) {
				        Map<String, Object> entry = new LinkedHashMap<>();
				        entry.put("coverName", anyTaxRow.getTaxDesc());                
				        entry.put("taxAmount", totalTax.toPlainString());          
				        policyCoverDetails.add(entry);
				    }
				});


				result.put("policyCoverData", policyCoverDetails);
				
				String sectionId = sectionIds.get(0).toString();
				
				// CONDITIONS
				List<Map<String,Object>> conditionList = getConditionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

				// EXCLUSION
				List<Map<String,Object>> exclusionRes = getExclusionList(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);
				List<Map<String,Object>> exclusionList = exclusionRes.stream().map(k -> {
					Map<String,Object> eMap = new HashMap<String,Object>();
					eMap.put("conditionTerms", k.get("exclusioTerms"));
					eMap.put("SectionId", k.get("SectionId"));
					return eMap;
				}).collect(Collectors.toList());

				//WARRANTY
				List<Map<String,Object>> warrantyList = getWarrantyDescription(map.get("policyNo")==null?"":map.get("policyNo").toString(), map.get("quoteNo")==null?"":map.get("quoteNo").toString(),sectionId);

				List<LinkedHashMap<String, Object>> termsAndconditions = Stream.of(warrantyList,conditionList,exclusionList).flatMap(Collection::stream)
						.sorted(Comparator.comparing(p -> {
							if (p.get("Sno") == null || p.get("Sno").toString().isEmpty()) {
								return Integer.MAX_VALUE;
							}
							try {
								return Integer.parseInt(p.get("Sno").toString());
							} catch (NumberFormatException e) {
								return Integer.MAX_VALUE;
							}
						}))
						.map(u -> {
							LinkedHashMap<String,Object> m = new LinkedHashMap<String, Object>();
							m.put("conditionTerms", u.get("conditionTerms")==null?"":u.get("conditionTerms").toString());
							return m;
						}).distinct().collect(Collectors.toList());

				int conditionsize = termsAndconditions.size();
				List<LinkedHashMap<String, Object>> firstHalf,secondHalf = new ArrayList<LinkedHashMap<String, Object>>();
				if(conditionsize>10) {
					int midIndex = conditionsize / 2;
					firstHalf = termsAndconditions.subList(0, midIndex);
					secondHalf = termsAndconditions.subList(midIndex, conditionsize);
				}else {
					firstHalf = termsAndconditions.subList(0, conditionsize);
				}
				
				
				result.put("Commonconditions", firstHalf);


//				List<PolicyCoverData> coverList =
//				        coverDataRepository.findByQuoteNoAndVehicleId(QuoteNo, 99999);
//
//				List<Map<String, Object>> policyCoverDetails = coverList.stream().map(p -> {
//					Map<String, Object> m = new LinkedHashMap<>();
//
//					m.put("coverName", p.getCoverName());						    
//					m.put("taxAmount", p.getTaxAmount());
//
//					//			         if(p.getCoverageType().equals("T")) {
//					//			           m.put("coverName", p.getCoverName());
//					//			      m.put("taxAmount", p.getTaxAmount());
//					//			         }
//					//			        
//
//					return m;
//				}).collect(Collectors.toList());
//
//				result.put("policyCoverData", policyCoverDetails);
				Map<Object,List<Map<String,Object>>> groupBycoverageDetails = coverageList.stream()
						.collect(Collectors.groupingBy(k -> k.get("coverDesc"), Collectors.toList()));
				for(Map.Entry<Object, List<Map<String,Object>>> CDEntry : groupBycoverageDetails.entrySet()) {
					LinkedHashMap<String, Object> coverMap = new LinkedHashMap<String, Object>();
					coverMap.put("coverId", Slist.stream().filter(f -> f.get("coverDesc").equals(CDEntry.getKey())).map(m -> m.get("coverId")).findFirst().orElse(""));
					coverMap.put("coverKey", CDEntry.getValue().stream()
							.filter(e -> Arrays.asList(108, 109, 114, 115, 33, 111).contains(Integer.parseInt(e.get("sectionId").toString())))
							.map(e -> "BUSINESS INTERRUPTION (" + CDEntry.getKey().toString() + ")".toUpperCase()+ " " +(map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE"))
							.findFirst()
							.orElse(CDEntry.getKey().toString().toUpperCase() + " " + (map.get("policyNo") == null ? "QUOTE SCHEDULE" : "POLICY SCHEDULE")));
					coverMap.put("coverValue", CDEntry.getValue());//CDEntry.getValue()
					coverMap.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
					coverMap.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
					coverMap.put("productId", map.get("productId")==null?"":map.get("productId").toString());
					coverMap.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
					coverMap.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
					coverMap.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
					coverageDetails.add(coverMap);
				}

				List<EserviceBuildingDetails> buildingDtl = eserviceBuildingDetailsRepo.findByRequestReferenceNoAndStatusOrderByRiskIdAsc(requestReferenceNo, "Y");
				String buildingOwnerYn = buildingDtl.isEmpty()?"":buildingDtl.get(0).getBuildingOwnerYn()==null?"":buildingDtl.get(0).getBuildingOwnerYn();
				List<Map<String,Object>> domesticKeyFactor = listItemValueRepo.getDomesticKeyFactor(buildingOwnerYn.equalsIgnoreCase("Y")?"1":"2",companyId);
				if(!domesticKeyFactor.isEmpty()) {
					String attachmentloc = this.getClass().getClassLoader().getResource("").getPath().replaceAll("%20", "")+"report/attachments/";
					domesticKeyFactor.forEach(k->{
						AttachMentRes a = AttachMentRes.builder()
								.docRefNo(k.get("ITEM_CODE")==null?"":k.get("ITEM_CODE").toString())
								.docloction(k.get("ITEM_VALUE")==null?"":(attachmentloc+k.get("ITEM_VALUE").toString()))
								.build();
						attachments.add(a);
					});
				}

				List<Map<String,Object>> firstLossPayeesList = new ArrayList<Map<String,Object>>();
				List<FirstLossPayee> firstLossPayees = firstLossPayeeRepo.findByRequestReferenceNo(map.get("requestReferenceNo").toString());
				if(!firstLossPayees.isEmpty()) {
					firstLossPayees.forEach(k -> {
						Map<String,Object> custMap = new HashMap<String,Object>();
						custMap.put("firstLossPayee", k.getFirstLossPayeeDesc());
						firstLossPayeesList.add(custMap);
					});
				}

				if(Arrays.asList("100046","100047","100048","100049","100050").contains(map.get("companyId")==null?"":map.get("companyId").toString()) || "100020".equalsIgnoreCase(companyId)) {
					LinkedList<Map<String,Object>> secdetails_f = new LinkedList<Map<String,Object>>();
					Map<Object, List<Tuple>> sectionDetails = Slist.stream().collect(Collectors.groupingBy(k -> k.get("locationName"), Collectors.toList()));
					for(Map.Entry<Object, List<Tuple>> secEntry : sectionDetails.entrySet()) {
						LinkedHashMap<String, Object> sec_map = new LinkedHashMap<String, Object>();
						sec_map.put("locationName", capitalizeFirstLetter(secEntry.getKey()==null?"":secEntry.getKey().toString()));
						Map<Object, List<Tuple>> k = secEntry.getValue().stream()
								.collect(Collectors.groupingBy(j -> j.get("sectionDesc"), Collectors.toList()));
						LinkedList<Map<String,Object>> secdetails = new LinkedList<Map<String,Object>>();
						for(Map.Entry<Object, List<Tuple>> t : k.entrySet()) {
							LinkedList<Map<String,Object>> sec_list = new LinkedList<Map<String,Object>>();
							Map<String,Object> f = new HashMap<String,Object>();
							f.put("SectionName", t.getKey()==null?"":capitalizeFirstLetter(t.getKey().toString()));
							Map<String,Object> sectionConditions = new HashMap<>();

							if (coverageList != null && t != null && t.getKey() != null) {
							    for (Map<String, Object> coverage : coverageList) {
							        if (coverage != null) {
							            Object sectionDesc = coverage.get("sectionDesc");
							            if (sectionDesc != null && sectionDesc.toString().equals(t.getKey().toString())) {
							                sectionConditions.put("firstHalfconditions", coverage.get("firstHalfconditions"));
							                sectionConditions.put("secondHalfconditions", coverage.get("secondHalfconditions"));
							                break;
							            }
							        }
							    }
							}
							if (sectionConditions.isEmpty()) {
							    sectionConditions = Collections.emptyMap();
							}
							for(int i =0;i<t.getValue().size();i++) {
								LinkedList<Map<String,Object>> addcoverdetails = new LinkedList<Map<String,Object>>();
								LinkedList<Map<String,Object>> excessdetails = new LinkedList<Map<String,Object>>();
								Tuple o = t.getValue().get(i);
								LinkedHashMap<String,Object> s = new LinkedHashMap<String,Object>();
								s.put("covername", o.get("coverDesc")==null?"":capitalizeFirstLetter(o.get("coverDesc").toString()));
								s.put("suminsured", o.get("sumInsured")==null?0.00:Double.parseDouble(o.get("sumInsured").toString()));
								s.put("annually", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString()));
								s.put("monthly", o.get("premiumExcludedTaxFc")==null?0.00:Double.parseDouble(o.get("premiumExcludedTaxFc").toString())/12);


								List<ExcessMaster> excessList = excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
										map.get("companyId")==null?"":map.get("companyId").toString(),
												map.get("productId")==null?"":map.get("productId").toString(),
														o.get("sectionId")==null?"":o.get("sectionId").toString(),
																o.get("coverId")==null?"":o.get("coverId").toString());

								if(excessList!=null && excessList.size()>0) {
									excessList.forEach(e_l -> {
										Map<String,Object> e = new HashMap<String,Object>();
										e.put("excessdesc", e_l.getExcessDescription()==null?"":e_l.getExcessDescription());
										e.put("excessamount", e_l.getExcessAmount()==null?0.00:e_l.getExcessAmount());
										e.put("excessper", e_l.getExcessPercentage()==null?0:e_l.getExcessPercentage());
										e.put("excessName", e_l.getCoverName()==null?"":e_l.getCoverName());
										excessdetails.add(e);
									});
								}

								List<FactorRateRequestDetails> additionalCover = coverData.stream()
										.filter(u -> "A".equalsIgnoreCase(u.getCoverageType())
												&& u.getSectionId() == Integer.parseInt(o.get("sectionId").toString()))
										.collect(Collectors.toList());
								if(additionalCover!=null && additionalCover.size()>0) {
									additionalCover.forEach(p -> {
										Map<String,Object> e = new HashMap<String,Object>();
										e.put("covername", p.getCoverDesc()==null?"":capitalizeFirstLetter(p.getCoverDesc().toString()));
										e.put("coverlimit", p.getCoverageLimit()==null?BigDecimal.ZERO:p.getCoverageLimit());
										addcoverdetails.add(e);
									});
								}
								s.put("addcoverdetails", addcoverdetails);
								s.put("excessdetails", excessdetails);
								sec_list.add(s);
							}



							sec_list.sort(Comparator.comparing(o -> (String) o.get("covername")));
							f.put("sectionList", sec_list);
							f.put("firstHalfconditions", sectionConditions.get("firstHalfconditions"));
							f.put("secondHalfconditions", sectionConditions.get("secondHalfconditions"));
							secdetails.add(f);
						}
						sec_map.put("sectionDetails", secdetails);
						secdetails_f.add(sec_map);
					}

					result.put("sectionDetails", secdetails_f);

					LinkedList<Map<String,Object>> sec_list = new LinkedList<Map<String,Object>>();
					for(Map.Entry<Object, List<Tuple>> secEntry : sectionDetails.entrySet()) {
						LinkedHashMap<String, Object> sec_map = new LinkedHashMap<String, Object>();
						sec_map.put("locationName", secEntry.getKey());
						Map<Object, List<Tuple>> k = secEntry.getValue().stream()
								.collect(Collectors.groupingBy(j -> j.get("sectionDesc"), Collectors.toList()));
						List<Map<String,Object>> j_list = new ArrayList<Map<String,Object>>();
						for(Map.Entry<Object, List<Tuple>> t : k.entrySet()) {
							Map<String,Object> j = new HashMap<String,Object>();
							j.put("sectionDesc", t.getKey()==null?"":capitalizeFirstLetter(t.getKey().toString()));
							Double premium_section = t.getValue().stream().map(w -> (BigDecimal) w.get("premiumExcludedTaxFc"))
									.collect(Collectors.summingDouble(BigDecimal::doubleValue));
							Double suminsured_section = t.getValue().stream().map(w -> (BigDecimal) w.get("sumInsured"))
									.collect(Collectors.summingDouble(BigDecimal::doubleValue));
							j.put("suminsured", suminsured_section);
							j.put("annually", premium_section);
							j.put("monthly", premium_section/12);
							j_list.add(j);
						}
						j_list.sort(Comparator.comparing(o -> (String) o.get("sectionDesc")));
						sec_map.put("sectionList", j_list);
						sec_list.add(sec_map);
					}

					result.put("sectionList", sec_list);

					result.put("phoenixVatPercent", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxRate()).findAny().orElse(BigDecimal.ZERO));
					result.put("phoenixVatAmount", coverData.stream().filter(f -> f.getCoverageType().equalsIgnoreCase("T")
							&& f.getTaxId()!=0).map(u -> u.getTaxAmount()).collect(Collectors.summingDouble(BigDecimal::doubleValue)));
				}

				List<Map<String,Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(map.get("companyId")==null?"":map.get("companyId").toString());
				if(!companyDetails.isEmpty()) {
					result.put("companyName", companyDetails.get(0).get("COMPANY_NAME")==null?"":companyDetails.get(0).get("COMPANY_NAME").toString());
					result.put("companylogo", companyDetails.get(0).get("COMPANY_LOGO")==null?"":companyDetails.get(0).get("COMPANY_LOGO").toString());
					result.put("companyWebsite", companyDetails.get(0).get("COMPANY_WEBSITE")==null?"":companyDetails.get(0).get("COMPANY_WEBSITE").toString());
					result.put("companyMail", companyDetails.get(0).get("COMPANY_EMAIL")==null?"":companyDetails.get(0).get("COMPANY_EMAIL").toString());
					result.put("companyPhone", companyDetails.get(0).get("COMPANY_PHONE")==null?"":companyDetails.get(0).get("COMPANY_PHONE").toString());
					result.put("companyAddress", companyDetails.get(0).get("COMPANY_ADDRESS")==null?"":companyDetails.get(0).get("COMPANY_ADDRESS").toString());
					result.put("companyPoBox", companyDetails.get(0).get("PO_BOX")==null?"":companyDetails.get(0).get("PO_BOX").toString());
					result.put("companyVrnNumber", companyDetails.get(0).get("VRN_NUMBER")==null?"":companyDetails.get(0).get("VRN_NUMBER").toString());
					result.put("companyremarks", companyDetails.get(0).get("REMARKS")==null?"":companyDetails.get(0).get("REMARKS").toString());
				}

				result.put("customerName", map.get("customerName")==null?
                        map.get("clientName")==null?"":map.get("clientName").toString()
                        :map.get("customerName").toString());
				result.put("email1", map.get("email1")==null?"":map.get("email1").toString());
				result.put("branchCode", map.get("branchCode")==null?"":map.get("branchCode").toString());
				result.put("agencyCode", map.get("agencyCode")==null?"":map.get("agencyCode").toString());
				result.put("policyNo", map.get("policyNo")==null?"":map.get("policyNo").toString());
				result.put("quoteNo", map.get("quoteNo")==null?"":map.get("quoteNo").toString());
				result.put("requestReferenceNo", map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				result.put("address", map.get("address1") == null ? "" : map.get("address1").toString());
				result.put("inceptionDate", map.get("inceptionDate")==null?"":map.get("inceptionDate").toString());
				result.put("expiryDate", map.get("expiryDate")==null?"":map.get("expiryDate").toString());
				result.put("entryDate", map.get("entryDate")==null?"":map.get("entryDate").toString());
				result.put("branchName", map.get("branchName")==null?"":map.get("branchName").toString());
				result.put("brokerBranchName", map.get("brokerBranchName")==null?"":map.get("brokerBranchName").toString());
				result.put("productName", map.get("productName")==null?"":map.get("productName").toString().toUpperCase()+" "+(map.get("policyNo")==null?"\nQuote Schedule":"\nPolicy Schedule"));
				result.put("stateName", map.get("stateName")==null?"":map.get("stateName").toString());
				result.put("cityName", map.get("cityName")==null?"":map.get("cityName").toString());
				result.put("mobileNo", map.get("mobileNo")==null?"":map.get("mobileNo").toString());
				result.put("customerId", map.get("customerId")==null?"":map.get("customerId").toString());
				result.put("brokerName", map.get("brokerName")==null?"":map.get("brokerName").toString());
				result.put("coreAppBrokerCode", map.get("coreAppBrokerCode")==null?"":map.get("coreAppBrokerCode").toString());
				result.put("currency", map.get("currency")==null?"":map.get("currency").toString());
				result.put("premium", map.get("premium")==null?"":new BigDecimal(Double.parseDouble(map.get("premium").toString())).toString());
				result.put("vatPremium", map.get("vatPremium")==null?"":Double.parseDouble(map.get("vatPremium").toString()));
				result.put("totalPremium", map.get("totalPremium")==null?"":new BigDecimal(Double.parseDouble(map.get("totalPremium").toString())).toString());
				result.put("signature", map.get("signature")==null?"":map.get("signature").toString());
				result.put("place", map.get("place")==null?"":map.get("place").toString());
				result.put("productId", map.get("productId")==null?"":map.get("productId").toString());
				result.put("companyId", map.get("companyId")==null?"":map.get("companyId").toString());
				result.put("taxName", map.get("companyId")==null?"":map.get("companyId").toString().equalsIgnoreCase("100004")?"Premium":"Vat");
				result.put("userMobile", map.get("userMobile")==null?"":map.get("userMobile").toString());
				result.put("identificationNo", map.get("identificationNo")==null?"":map.get("identificationNo").toString());
				result.put("postalAddress", StringUtils.join(
						Arrays.asList(
								map.get("address1") == null ? "" : map.get("address1").toString(),
										map.get("pinCode") == null ? "" : map.get("pinCode").toString(),
												map.get("stateName") == null ? "" : map.get("stateName").toString(),
														map.get("cityName") == null ? "" : map.get("cityName").toString(),
																map.get("countryName") == null ? "" : map.get("countryName").toString()
								).stream()
						.filter(value -> !value.isEmpty())
						.collect(Collectors.joining(","))
						));
				result.put("mobileNo1", map.get("mobileNo1")==null?"":map.get("mobileNo1").toString());
				result.put("approvedBy", map.get("approvedBy")==null?"":map.get("approvedBy").toString());
				result.put("userName", map.get("userName")==null?"":map.get("userName").toString());
				result.put("renewalDate", map.get("expiryDate")==null?"":RenewalDate(map.get("expiryDate").toString()));
				result.put("bdmName", map.get("bdmName")==null?"":map.get("bdmName").toString());
				result.put("overAllPremium", OverAllPremium);
				result.put("premiumDetails", premiumDetailsRes);
				//result.put("sectionDetails", sectionList);
				//result.put("locationDetails", locationDetails);
				result.put("firstLossPayeesList", firstLossPayeesList);
				result.put("coverageDetails",  coverageDetails.stream()
						.sorted(Comparator.comparing(o -> (Integer) o.get("coverId")))
						.collect(Collectors.toList()));
				//result.put("attachMents", attachments);
				
			}
		}catch(Exception e) {
			log.info("Error in EwaySchedule ==> "+e.getMessage());
			e.printStackTrace();
		}
		log.info("Exit into EwaySchedule");
		return result;
	}
	
	public List<Map<String,Object>> getExclusionListByRequestRef(String requestReferenceNo, String companyId, String productId, String branchCode, String sectionId){
	    List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
	    try {
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        List<Tuple> exclusionRes = new ArrayList<>();

	        for(int i=1;i<=2;i++) {
	            CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
	            
	            if(i == 1) {
	                // For ExclusionMaster
	                Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);
	                cq3.multiselect(
	                    emRoot3.get("exclusionDescription").alias("exclusionTerms"),
	                    emRoot3.get("sectionId").alias("sectionId"),
	                    emRoot3.get("exclusionId").alias("exclusionId")
	                );
	                
	                List<Predicate> predicates = new ArrayList<Predicate>();
	                predicates.add(cb.equal(emRoot3.get("companyId"), companyId));
	                predicates.add(cb.equal(emRoot3.get("productId").as(String.class), productId));
	                predicates.add(cb.or(
	                    cb.equal(emRoot3.get("sectionId"), sectionId), 
	                    cb.equal(emRoot3.get("sectionId"), "99999")
	                ));
	                predicates.add(cb.or(
	                    cb.equal(emRoot3.get("branchCode"), branchCode), 
	                    cb.equal(emRoot3.get("branchCode"), "99999")
	                ));
	                predicates.add(cb.between(
	                    cb.literal(new Date()), 
	                    emRoot3.get("effectiveDateStart"), 
	                    emRoot3.get("effectiveDateEnd")
	                ));
	                predicates.add(cb.equal(emRoot3.get("status"), "Y"));
	                predicates.add(cb.equal(emRoot3.get("typeId"), "D"));
	                
	                Predicate [] predicatArray = new Predicate[predicates.size()];
	                predicates.toArray(predicatArray);
	                exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
	                
	            } else {
	                // For TermsAndCondition
	                Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
	                cq3.multiselect(
	                    tacRoot3.get("subIdDesc").alias("exclusionTerms"),
	                    tacRoot3.get("sectionId").alias("sectionId"),
	                    tacRoot3.get("sno").alias("exclusionId")
	                );
	                
	                List<Predicate> predicates = new ArrayList<Predicate>();
	                predicates.add(cb.equal(tacRoot3.get("companyId"), companyId));
	                predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), productId));
	                predicates.add(cb.or(
	                    cb.equal(tacRoot3.get("sectionId"), sectionId), 
	                    cb.equal(tacRoot3.get("sectionId"), "99999")
	                ));
	                predicates.add(cb.equal(tacRoot3.get("requestReferenceNo"), requestReferenceNo));
	                predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
	                predicates.add(cb.or(
	                    cb.equal(tacRoot3.get("branchCode"), branchCode), 
	                    cb.equal(tacRoot3.get("branchCode"), "99999")
	                ));
	                predicates.add(cb.equal(tacRoot3.get("id"), "7"));
	                
	                Predicate [] predicatArray = new Predicate[predicates.size()];
	                predicates.toArray(predicatArray);
	                exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
	            }
	        }
	        
	        exclusionList = exclusionRes.stream().distinct().map(c ->{
	            LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
	            Emap.put("exclusioTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString()
	                    .replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
	                    .replaceAll("'", "'"));
	            Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
	            return Emap;
	        }).collect(Collectors.toList());
	        
	    }catch(Exception e) {
	        log.info("Error in getExclusionListByRequestRef ==> "+e.getMessage());
	        e.printStackTrace();
	    }
	    return exclusionList;
	}
	
	public List<Map<String,Object>> getWarrantyDescriptionByRequestRef(String requestReferenceNo, String companyId, String productId, String branchCode, String sectionId){
	    List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
	    try {
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        List<Tuple> warrantyRes = new ArrayList<>();

	        for(int i=1;i<=2;i++) {
	            CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
	            
	            if(i == 1) {
	                // For WarrantyMaster
	                Root<WarrantyMaster> wmRoot3 = cq3.from(WarrantyMaster.class);
	                cq3.multiselect(
	                    wmRoot3.get("warrantyDescription").alias("warrantyTerms"),
	                    wmRoot3.get("sectionId").alias("sectionId"),
	                    wmRoot3.get("warrantyId").alias("warrantyId")
	                );
	                
	                List<Predicate> predicates = new ArrayList<Predicate>();
	                predicates.add(cb.equal(wmRoot3.get("companyId"), companyId));
	                predicates.add(cb.equal(wmRoot3.get("productId").as(String.class), productId));
	                predicates.add(cb.or(
	                    cb.equal(wmRoot3.get("sectionId"), sectionId), 
	                    cb.equal(wmRoot3.get("sectionId"), "99999")
	                ));
	                predicates.add(cb.or(
	                    cb.equal(wmRoot3.get("branchCode"), branchCode), 
	                    cb.equal(wmRoot3.get("branchCode"), "99999")
	                ));
	                predicates.add(cb.between(
	                    cb.literal(new Date()), 
	                    wmRoot3.get("effectiveDateStart"), 
	                    wmRoot3.get("effectiveDateEnd")
	                ));
	                predicates.add(cb.equal(wmRoot3.get("status"), "Y"));
	                predicates.add(cb.equal(wmRoot3.get("typeId"), "D"));
	                
	                Predicate [] predicatArray = new Predicate[predicates.size()];
	                predicates.toArray(predicatArray);
	                warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
	                
	            } else {
	                // For TermsAndCondition
	                Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
	                cq3.multiselect(
	                    tacRoot3.get("subIdDesc").alias("warrantyTerms"),
	                    tacRoot3.get("sectionId").alias("sectionId"),
	                    tacRoot3.get("sno").alias("warrantyId")
	                );
	                
	                List<Predicate> predicates = new ArrayList<Predicate>();
	                predicates.add(cb.equal(tacRoot3.get("companyId"), companyId));
	                predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), productId));
	                predicates.add(cb.or(
	                    cb.equal(tacRoot3.get("sectionId"), sectionId), 
	                    cb.equal(tacRoot3.get("sectionId"), "99999")
	                ));
	                predicates.add(cb.equal(tacRoot3.get("requestReferenceNo"), requestReferenceNo));
	                predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
	                predicates.add(cb.or(
	                    cb.equal(tacRoot3.get("branchCode"), branchCode), 
	                    cb.equal(tacRoot3.get("branchCode"), "99999")
	                ));
	                predicates.add(cb.equal(tacRoot3.get("id"), "4"));
	                
	                Predicate [] predicatArray = new Predicate[predicates.size()];
	                predicates.toArray(predicatArray);
	                warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
	            }
	        }
	        
	        warrantyList = warrantyRes.stream().distinct().map(c ->{
	            LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
	            Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString()
	                .replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
	                .replaceAll("'", "'"));
	            Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
	            Emap.put("Sno", c.get("warrantyId")==null?"":c.get("warrantyId").toString());
	            return Emap;
	        }).collect(Collectors.toList());
	        
	    } catch(Exception e) {
	        log.info("Error in getWarrantyDescriptionByRequestRef ==> "+e.getMessage());
	        e.printStackTrace();
	    }
	    return warrantyList;
	}
	
	
	public List<Map<String,Object>> getConditionListByRequestRef(String requestReferenceNo, String companyId, String productId, String branchCode, String sectionId){
	    List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
	    try {
	        CriteriaBuilder cb = em.getCriteriaBuilder();       
	        List<Tuple> conditionRes = new ArrayList<>();
	        
	        for(int i=1;i<=2;i++) {
	            CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
	            
	            if(i == 1) {
	                // For ClausesMaster
	                Root<ClausesMaster> cmRoot2 = cq2.from(ClausesMaster.class);
	                cq2.multiselect(
	                    cmRoot2.get("clausesDescription").alias("conditionTerms"),
	                    cmRoot2.get("sectionId").alias("sectionId"),
	                    cmRoot2.get("clausesId").alias("clausesId"),
	                    cmRoot2.get("clausesShortDesc").alias("conditionTitle")
	                );
	                
	                List<Predicate> predicates = new ArrayList<Predicate>();
	                predicates.add(cb.equal(cmRoot2.get("companyId"), companyId));
	                predicates.add(cb.equal(cmRoot2.get("productId").as(String.class), productId));
	                predicates.add(cb.or(
	                    cb.equal(cmRoot2.get("sectionId"), sectionId), 
	                    cb.equal(cmRoot2.get("sectionId"), "99999")
	                ));
	                predicates.add(cb.or(
	                    cb.equal(cmRoot2.get("branchCode"), branchCode), 
	                    cb.equal(cmRoot2.get("branchCode"), "99999")
	                ));
	                predicates.add(cb.between(
	                    cb.literal(new Date()), 
	                    cmRoot2.get("effectiveDateStart"), 
	                    cmRoot2.get("effectiveDateEnd")
	                ));
	                predicates.add(cb.equal(cmRoot2.get("status"), "Y"));
	                predicates.add(cb.equal(cmRoot2.get("typeId"), "D"));
	                
	                Predicate [] predicatArray = new Predicate[predicates.size()];
	                predicates.toArray(predicatArray);
	                conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
	                
	            } else {
	                // For TermsAndCondition
	                Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
	                cq2.multiselect(
	                    tacRoot2.get("subIdDesc").alias("conditionTerms"),
	                    tacRoot2.get("sectionId").alias("sectionId"),
	                    tacRoot2.get("sno").alias("clausesId")
	                );
	                
	                List<Predicate> predicates = new ArrayList<Predicate>();
	                predicates.add(cb.equal(tacRoot2.get("companyId"), companyId));
	                predicates.add(cb.equal(tacRoot2.get("productId").as(String.class), productId));
	                predicates.add(cb.or(
	                    cb.equal(tacRoot2.get("sectionId"), sectionId), 
	                    cb.equal(tacRoot2.get("sectionId"), "99999")
	                ));
	                predicates.add(cb.equal(tacRoot2.get("requestReferenceNo"), requestReferenceNo));
	                predicates.add(cb.equal(tacRoot2.get("status"), "Y"));
	                predicates.add(cb.or(
	                    cb.equal(tacRoot2.get("branchCode"), branchCode), 
	                    cb.equal(tacRoot2.get("branchCode"), "99999")
	                ));
	                predicates.add(cb.equal(tacRoot2.get("id"), "6"));
	                
	                Predicate [] predicatArray = new Predicate[predicates.size()];
	                predicates.toArray(predicatArray);
	                conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
	            }
	        }
	        
	        conditionList = conditionRes.stream().distinct().map(c ->{
	            LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
	            Cmap.put("conditionTerms", c.get("conditionTerms")==null?"":c.get("conditionTerms").toString()
	                    .replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
	                    .replaceAll("'", "'"));
	            Cmap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
	            Cmap.put("title", c.get("conditionTitle")==null?"":c.get("conditionTitle").toString()
	                    .replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
	                    .replaceAll("'", "'"));
	            return Cmap;
	        }).collect(Collectors.toList());
	        
	    }catch(Exception e) {
	        log.info("Error in getConditionListByRequestRef ==> "+e.getMessage());
	        e.printStackTrace();
	    }
	    return conditionList;
	}

	public List<AttachMentRes> downloadPolicyWorking(String quoteNo) {
		List<SectionDataDetails> secData = sectionDataDetailsRepo.findByQuoteNo(quoteNo);
		List<AttachMentRes> attachments = new ArrayList<AttachMentRes>();
		if(secData != null) {
			SectionDataDetails s = secData.get(0);
			List<String> sectionIds = secData.stream().map(m -> m.getSectionId()).distinct().collect(Collectors.toList());
			for(int i =0;i<sectionIds.size();i++) {
				List<Map<String,Object>> conditionList = getConditionList(s.getPolicyNo(), s.getQuoteNo(),sectionIds.get(i));
				attachments.addAll(
					conditionList.stream().filter(f -> f.get("pdfName") != null && f.get("pdfLocation") != null)
						.map(e -> {
							AttachMentRes m = AttachMentRes.builder()
									.docRefNo(e.get("pdfName")==null?"":e.get("pdfName").toString())
									.docloction(e.get("pdfLocation")==null?"":e.get("pdfLocation").toString())
									.build();
							return m;
						}).collect(Collectors.toList())
					);
				
				List<Map<String,Object>> exclusionList = getExclusionList(s.getPolicyNo(), s.getQuoteNo(),sectionIds.get(i));
				attachments.addAll(
						exclusionList.stream().filter(f -> f.get("pdfName") != null && f.get("pdfLocation") != null)
						.map(e -> {
							AttachMentRes m = AttachMentRes.builder()
									.docRefNo(e.get("pdfName")==null?"":e.get("pdfName").toString())
									.docloction(e.get("pdfLocation")==null?"":e.get("pdfLocation").toString())
									.build();
							return m;
						}).collect(Collectors.toList())
					);
				
				List<Map<String,Object>> warrantyDescription = getWarrantyDescription(s.getPolicyNo(), s.getQuoteNo(),sectionIds.get(i));
				attachments.addAll(
						warrantyDescription.stream().filter(f -> f.get("pdfName") != null && f.get("pdfLocation") != null)
						.map(e -> {
							AttachMentRes m = AttachMentRes.builder()
									.docRefNo(e.get("pdfName")==null?"":e.get("pdfName").toString())
									.docloction(e.get("pdfLocation")==null?"":e.get("pdfLocation").toString())
									.build();
							return m;
						}).collect(Collectors.toList())
					);
			}
		}
		return attachments;
	}

	public List<Map<String, Object>> getWarrantyDescriptionSectionCover(String policyNo,
			String quoteNo, Set<String> sectionIds) {

		List<Map<String, Object>> warrantyList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();

			for (int i = 1; i <= 2; i++) {

				CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<>();

				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot.get("quoteNo"), quoteNo));
				}

				// MASTER WARRANTY
				if (i == 1) {

					Subquery<Tuple> WquoteIn = cq.subquery(Tuple.class);
					Root<TermsAndCondition> StacRoot = WquoteIn.from(TermsAndCondition.class);

					WquoteIn.select(StacRoot.get("quoteNo")).where(cb.equal(StacRoot.get("quoteNo"), quoteNo),
							cb.equal(StacRoot.get("id"), "4"));

					Root<WarrantyMaster> wmRoot = cq.from(WarrantyMaster.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(wmRoot.get("sectionId").in(sectionIds),
//								cb.equal(wmRoot.get("sectionId"), "99999")));
						predicates.add(cb.equal(wmRoot.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

						predicates.add(cb.or(cb.equal(wmRoot.get("sectionId"), sddRoot.get("sectionId")),
								cb.equal(wmRoot.get("sectionId"), "99999")));
					}

					cq.multiselect(wmRoot.get("warrantyDescription").alias("warrantyTerms"),
							wmRoot.get("sectionId").alias("sectionId"), wmRoot.get("warrantyId").alias("warrantyId"),
							wmRoot.get("pdfLocation").alias("pdfLocation"), wmRoot.get("pdfName").alias("pdfName"));

					predicates.add(cb.equal(wmRoot.get("companyId"), hpmRoot.get("companyId")));

					predicates.add(cb.equal(wmRoot.get("productId").as(String.class),
							hpmRoot.get("productId").as(String.class)));

					predicates.add(cb.or(cb.equal(wmRoot.get("branchCode"), hpmRoot.get("branchCode")),
							cb.equal(wmRoot.get("branchCode"), "99999")));

					predicates.add(cb.between(cb.literal(new Date()), wmRoot.get("effectiveDateStart"),
							wmRoot.get("effectiveDateEnd")));

					predicates.add(cb.equal(wmRoot.get("status"), "Y"));
					predicates.add(cb.equal(wmRoot.get("typeId"), "D"));

					predicates.add(cb.not(cb.in(hpmRoot.get("quoteNo")).value(WquoteIn)));

					Predicate[] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);

					warrantyRes.addAll(em.createQuery(cq.where(predicatArray)).getResultList());

				} else {

					// TERMS AND CONDITION WARRANTY
					Root<TermsAndCondition> tacRoot = cq.from(TermsAndCondition.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot.get("sectionId"), "99999")));
						
						predicates.add(cb.equal(tacRoot.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

						predicates.add(cb.equal(tacRoot.get("sectionId"), sddRoot.get("sectionId")));
					}

					cq.multiselect(tacRoot.get("subIdDesc").alias("warrantyTerms"),
							tacRoot.get("sectionId").alias("sectionId"), tacRoot.get("sno").alias("warrantyId"),
							cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot.get("companyId"), hpmRoot.get("companyId")));

					predicates.add(cb.equal(tacRoot.get("productId").as(String.class),
							hpmRoot.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot.get("quoteNo")).value(tacRoot.get("quoteNo")));

					predicates.add(cb.equal(tacRoot.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot.get("branchCode"), hpmRoot.get("branchCode")),
							cb.equal(tacRoot.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot.get("id"), "4"));

					Predicate[] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);

					warrantyRes.addAll(em.createQuery(cq.where(predicatArray)).getResultList());
				}
			}

			warrantyList = warrantyRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Wmap = new LinkedHashMap<>();

				Wmap.put("conditionTerms",
						c.get("warrantyTerms") == null ? ""
								: c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Wmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Wmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Wmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Wmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getWarrantyDescriptionSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return warrantyList;
	}

	public List<Map<String, Object>> getExclusionListSectionCover(String policyNo,String quoteNo, Set<String> sectionIds) {

		List<Map<String, Object>> exclusionList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			for (int i = 1; i <= 2; i++) {

				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<>();

				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot3.get("quoteNo"), quoteNo));
				}

				// EXCLUSION MASTER
				if (i == 1) {

					Subquery<Tuple> EquoteIn = cq3.subquery(Tuple.class);

					Root<TermsAndCondition> SEtacRoot = EquoteIn.from(TermsAndCondition.class);

					EquoteIn.select(SEtacRoot.get("quoteNo")).where(cb.equal(SEtacRoot.get("quoteNo"), quoteNo),
							cb.equal(SEtacRoot.get("id"), "7"));

					Root<ExclusionMaster> emRoot3 = cq3.from(ExclusionMaster.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(emRoot3.get("sectionId").in(sectionIds),
//								cb.equal(emRoot3.get("sectionId"), "99999")));
						predicates.add(cb.equal(emRoot3.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));

						predicates.add(cb.or(cb.equal(emRoot3.get("sectionId"), sddRoot3.get("sectionId")),
								cb.equal(emRoot3.get("sectionId"), "99999")));
					}

					cq3.multiselect(emRoot3.get("exclusionDescription").alias("exclusionTerms"),

							emRoot3.get("sectionId").alias("sectionId"),

							emRoot3.get("exclusionId").alias("exclusionId"),

							emRoot3.get("pdfLocation").alias("pdfLocation"),

							emRoot3.get("pdfName").alias("pdfName"));

					predicates.add(cb.equal(emRoot3.get("companyId"), hpmRoot3.get("companyId")));

					predicates.add(cb.equal(emRoot3.get("productId").as(String.class),
							hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.or(cb.equal(emRoot3.get("branchCode"), hpmRoot3.get("branchCode")),
							cb.equal(emRoot3.get("branchCode"), "99999")));

					predicates.add(cb.between(cb.literal(new Date()), emRoot3.get("effectiveDateStart"),
							emRoot3.get("effectiveDateEnd")));

					predicates.add(cb.equal(emRoot3.get("status"), "Y"));
					predicates.add(cb.equal(emRoot3.get("typeId"), "D"));

					predicates.add(cb.not(cb.in(hpmRoot3.get("quoteNo")).value(EquoteIn)));

					Predicate[] predicatArray = new Predicate[predicates.size()];

					predicates.toArray(predicatArray);

					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());

				} else {

					// TERMS AND CONDITION EXCLUSION
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);

					// SECTION ID IN (...)
					if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot3.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot3.get("sectionId"), "99999")));
						
						predicates.add(cb.equal(tacRoot3.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);

						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));

						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
					}

					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),

							tacRoot3.get("sectionId").alias("sectionId"),

							tacRoot3.get("sno").alias("exclusionId"),

							cb.nullLiteral(String.class).alias("conditionTitle"),

							cb.nullLiteral(String.class).alias("pdfLocation"),

							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));

					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class),
							hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));

					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")),
							cb.equal(tacRoot3.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot3.get("id"), "7"));

					Predicate[] predicatArray = new Predicate[predicates.size()];

					predicates.toArray(predicatArray);

					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				}
			}

			exclusionList = exclusionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Emap = new LinkedHashMap<>();

				Emap.put("conditionTerms",
						c.get("exclusionTerms") == null ? ""
								: c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Emap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Emap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Emap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Emap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getExclusionListSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return exclusionList;
	}

	public List<Map<String, Object>> getConditionListSectionCoverTermsOnly(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> conditionList = new ArrayList<Map<String, Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> conditionRes = new ArrayList<>();
			CriteriaQuery<Tuple> cq2 = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot2 = cq2.from(HomePositionMaster.class);
			List<Predicate> predicates = new ArrayList<>();
				if (StringUtils.isNotBlank(policyNo)) {
					predicates.add(cb.equal(hpmRoot2.get("policyNo"), policyNo));
				} else {
					predicates.add(cb.equal(hpmRoot2.get("quoteNo"), quoteNo));
				}
				Root<TermsAndCondition> tacRoot2 = cq2.from(TermsAndCondition.class);
					if (sectionIds != null && !sectionIds.isEmpty()) {

					//	predicates.add(cb.or(tacRoot2.get("sectionId").in(sectionIds),cb.equal(tacRoot2.get("sectionId"), "99999")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), "99999"));

					} else {

						Root<SectionDataDetails> sddRoot2 = cq2.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot2.get("quoteNo"), hpmRoot2.get("quoteNo")));
						predicates.add(cb.equal(tacRoot2.get("sectionId"), sddRoot2.get("sectionId")));
					}

					cq2.multiselect(tacRoot2.get("subIdDesc").alias("conditionTerms"),
							tacRoot2.get("sectionId").alias("sectionId"), tacRoot2.get("sno").alias("clausesId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),
							cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));

					predicates.add(cb.equal(tacRoot2.get("companyId"), hpmRoot2.get("companyId")));

					predicates.add(cb.equal(tacRoot2.get("productId").as(String.class),
							hpmRoot2.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot2.get("quoteNo")).value(tacRoot2.get("quoteNo")));

					predicates.add(cb.equal(tacRoot2.get("status"), "Y"));

					predicates.add(cb.or(cb.equal(tacRoot2.get("branchCode"), hpmRoot2.get("branchCode")),
							cb.equal(tacRoot2.get("branchCode"), "99999")));

					predicates.add(cb.equal(tacRoot2.get("id"), "6"));
				
				Predicate[] predicatArray = new Predicate[predicates.size()];
				predicates.toArray(predicatArray);

				conditionRes.addAll(em.createQuery(cq2.where(predicatArray)).getResultList());
			

			conditionList = conditionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Cmap = new LinkedHashMap<String, Object>();

				Cmap.put("conditionTerms",
						c.get("conditionTerms") == null ? ""
								: c.get("conditionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Cmap.put("title",
						c.get("conditionTitle") == null ? ""
								: c.get("conditionTitle").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Cmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Cmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Cmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {
			log.info("Error in getConditionListSectionCover ==> " + e.getMessage());
			e.printStackTrace();
		}

		return conditionList;
	}

	public List<Map<String, Object>> getWarrantyDescriptionSectionCoverTermsOnly(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> warrantyList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);

			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.isNotBlank(policyNo)) {
				predicates.add(cb.equal(hpmRoot.get("policyNo"), policyNo));
			} else {
				predicates.add(cb.equal(hpmRoot.get("quoteNo"), quoteNo));
			}

			Root<TermsAndCondition> tacRoot = cq.from(TermsAndCondition.class);

			// SECTION ID IN (...)
			if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot.get("sectionId"), "99999")));

				predicates.add(cb.equal(tacRoot.get("sectionId"), "99999"));

			} else {

				Root<SectionDataDetails> sddRoot = cq.from(SectionDataDetails.class);

				predicates.add(cb.equal(sddRoot.get("quoteNo"), hpmRoot.get("quoteNo")));

				predicates.add(cb.equal(tacRoot.get("sectionId"), sddRoot.get("sectionId")));
			}

			cq.multiselect(tacRoot.get("subIdDesc").alias("warrantyTerms"), tacRoot.get("sectionId").alias("sectionId"),
					tacRoot.get("sno").alias("warrantyId"), cb.nullLiteral(String.class).alias("pdfLocation"),
					cb.nullLiteral(String.class).alias("pdfName"));

			predicates.add(cb.equal(tacRoot.get("companyId"), hpmRoot.get("companyId")));

			predicates.add(
					cb.equal(tacRoot.get("productId").as(String.class), hpmRoot.get("productId").as(String.class)));

			predicates.add(cb.in(hpmRoot.get("quoteNo")).value(tacRoot.get("quoteNo")));

			predicates.add(cb.equal(tacRoot.get("status"), "Y"));

			predicates.add(cb.or(cb.equal(tacRoot.get("branchCode"), hpmRoot.get("branchCode")),
					cb.equal(tacRoot.get("branchCode"), "99999")));

			predicates.add(cb.equal(tacRoot.get("id"), "4"));

			Predicate[] predicatArray = new Predicate[predicates.size()];
			predicates.toArray(predicatArray);

			warrantyRes.addAll(em.createQuery(cq.where(predicatArray)).getResultList());

			warrantyList = warrantyRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Wmap = new LinkedHashMap<>();

				Wmap.put("conditionTerms",
						c.get("warrantyTerms") == null ? ""
								: c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Wmap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Wmap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Wmap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Wmap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getWarrantyDescriptionSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return warrantyList;
	}

	public List<Map<String, Object>> getExclusionListSectionCoverTermsOnly(String policyNo, String quoteNo,
			Set<String> sectionIds) {

		List<Map<String, Object>> exclusionList = new ArrayList<>();

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
			Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

			List<Predicate> predicates = new ArrayList<>();

			if (StringUtils.isNotBlank(policyNo)) {
				predicates.add(cb.equal(hpmRoot3.get("policyNo"), policyNo));
			} else {
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), quoteNo));
			}
			Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
			if (sectionIds != null && !sectionIds.isEmpty()) {

//						predicates.add(cb.or(tacRoot3.get("sectionId").in(sectionIds),
//								cb.equal(tacRoot3.get("sectionId"), "99999")));

				predicates.add(cb.equal(tacRoot3.get("sectionId"), "99999"));

			} else {

				Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);

				predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));

				predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
			}

			cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),

					tacRoot3.get("sectionId").alias("sectionId"),

					tacRoot3.get("sno").alias("exclusionId"),

					cb.nullLiteral(String.class).alias("conditionTitle"),

					cb.nullLiteral(String.class).alias("pdfLocation"),

					cb.nullLiteral(String.class).alias("pdfName"));

			predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));

			predicates.add(
					cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));

			predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));

			predicates.add(cb.equal(tacRoot3.get("status"), "Y"));

			predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")),
					cb.equal(tacRoot3.get("branchCode"), "99999")));

			predicates.add(cb.equal(tacRoot3.get("id"), "7"));

			Predicate[] predicatArray = new Predicate[predicates.size()];

			predicates.toArray(predicatArray);

			exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());

			exclusionList = exclusionRes.stream().distinct().map(c -> {

				LinkedHashMap<String, Object> Emap = new LinkedHashMap<>();

				Emap.put("conditionTerms",
						c.get("exclusionTerms") == null ? ""
								: c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "")
										.replaceAll("’", "'"));

				Emap.put("SectionId", c.get("sectionId") == null ? "" : c.get("sectionId").toString());

				Emap.put("pdfLocation", c.get("pdfLocation") == null ? "" : c.get("pdfLocation").toString());

				Emap.put("pdfName", c.get("pdfName") == null ? "" : c.get("pdfName").toString());

				return Emap;

			}).collect(Collectors.toList());

		} catch (Exception e) {

			log.info("Error in getExclusionListSectionCover ==> " + e.getMessage());

			e.printStackTrace();
		}

		return exclusionList;
	}

	public List<Map<String, Object>> getConditionListCoverTermsOnly(String policyNo,String QuoteNo, String sectionId ,String coverId,Integer loc) {
		List<Map<String,Object>> conditionList = new ArrayList<Map<String,Object>>();
		try {
			List<String> sect= new ArrayList<>();
			sect.add(sectionId);
			List<String> cover= new ArrayList<>();
			cover.add(coverId);
			List<TermsAndCondition> tClist = termsConRepo.findByQuoteNoAndSectionIdInAndCoverIdInAndIdAndLocationId(QuoteNo,sect,cover,6,loc.toString());
			if(tClist != null && !tClist.isEmpty())
			{
				 conditionList = tClist.stream().distinct().map(c ->{
					LinkedHashMap<String,Object> Cmap = new LinkedHashMap<String,Object>();
					Cmap.put("conditionTerms", c.getSubIdDesc());
					Cmap.put("SectionId", c.getSectionId());
					Cmap.put("title", "Clauses");
					return Cmap;
				}).collect(Collectors.toList());
				 return conditionList;
			}
		
		}catch(Exception e) {
			log.info("Error in getConditionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return conditionList;
	}

	public List<Map<String, Object>> getWarrantyDescriptionCoverTermsOnly(String policyNo,String QuoteNo, String sectionId,String coverId,Integer loc) {
		List<Map<String,Object>> warrantyList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> warrantyRes = new ArrayList<>();

			
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
				cq3.multiselect(tacRoot3.get("subIdDesc").alias("warrantyTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("warrantyId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));
					predicates.add(cb.equal(tacRoot3.get("sectionId"), sectionId));
					predicates.add(cb.equal(tacRoot3.get("coverId"),coverId));
					predicates.add(cb.equal(tacRoot3.get("locationId"),loc.toString()));
					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "4"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					warrantyRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
					warrantyList = warrantyRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("warrantyTerms")==null?"":c.get("warrantyTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("Sno", c.get("warrantyId")==null?"":c.get("warrantyId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getWarrantyDescription ==> "+e.getMessage());
			e.printStackTrace();
		}
		return warrantyList;

	}

	public List<Map<String, Object>> getExclusionListCoverTermsOnly(String policyNo,String QuoteNo,String sectionId,String coverId,Integer loc) {
		List<Map<String,Object>> exclusionList = new ArrayList<Map<String,Object>>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			List<Tuple> exclusionRes = new ArrayList<>();

			
				CriteriaQuery<Tuple> cq3 = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> hpmRoot3 = cq3.from(HomePositionMaster.class);

				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(hpmRoot3.get("quoteNo"), QuoteNo));
				
				
					Root<TermsAndCondition> tacRoot3 = cq3.from(TermsAndCondition.class);
					if(StringUtils.isNotBlank(sectionId)) {
//						predicates.add(cb.or(cb.equal(tacRoot3.get("sectionId"), sectionId), cb.equal(tacRoot3.get("sectionId"), "99999")));
//						predicates.add(cb.or(cb.equal(tacRoot3.get("coverId"), coverId), cb.equal(tacRoot3.get("coverId"), "99999")));
						
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sectionId));
						predicates.add(cb.equal(tacRoot3.get("coverId"), coverId));
						predicates.add(cb.equal(tacRoot3.get("locationId"), loc.toString()));
					}else {
						Root<SectionDataDetails> sddRoot3 = cq3.from(SectionDataDetails.class);
						predicates.add(cb.equal(sddRoot3.get("quoteNo"), hpmRoot3.get("quoteNo")));
						predicates.add(cb.equal(tacRoot3.get("sectionId"), sddRoot3.get("sectionId")));
						predicates.add(cb.equal(tacRoot3.get("coverId"), sddRoot3.get("coverId")));
					}

					cq3.multiselect(tacRoot3.get("subIdDesc").alias("exclusionTerms"),tacRoot3.get("sectionId").alias("sectionId"),tacRoot3.get("sno").alias("exclusionId"),
							cb.nullLiteral(String.class).alias("conditionTitle"),cb.nullLiteral(String.class).alias("pdfLocation"),
							cb.nullLiteral(String.class).alias("pdfName"));
					predicates.add(cb.equal(tacRoot3.get("companyId"), hpmRoot3.get("companyId")));
					predicates.add(cb.equal(tacRoot3.get("productId").as(String.class), hpmRoot3.get("productId").as(String.class)));

					predicates.add(cb.in(hpmRoot3.get("quoteNo")).value(tacRoot3.get("quoteNo")));
					predicates.add(cb.equal(tacRoot3.get("status"), "Y"));
					predicates.add(cb.or(cb.equal(tacRoot3.get("branchCode"), hpmRoot3.get("branchCode")), cb.equal(tacRoot3.get("branchCode"), "99999")));
					predicates.add(cb.equal(tacRoot3.get("id"), "7"));
					Predicate [] predicatArray = new Predicate[predicates.size()];
					predicates.toArray(predicatArray);
					exclusionRes.addAll(em.createQuery(cq3.where(predicatArray)).getResultList());
				
			
			exclusionList = exclusionRes.stream().distinct().map(c ->{
				LinkedHashMap<String,Object> Emap = new LinkedHashMap<String,Object>();
				Emap.put("conditionTerms", c.get("exclusionTerms")==null?"":c.get("exclusionTerms").toString().replaceAll("\\n|\\t|\\r|\\r\\n|\\f|", "").replaceAll("’", "'"));
				Emap.put("SectionId", c.get("sectionId")==null?"":c.get("sectionId").toString());
				Emap.put("pdfLocation", c.get("pdfLocation")==null?"":c.get("pdfLocation").toString());
				Emap.put("pdfName", c.get("pdfName")==null?"":c.get("pdfName").toString());
				return Emap;
			}).collect(Collectors.toList());
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return exclusionList;
	}

	public List<Map<String, Object>> getExcessListCover(HomePositionMaster home, String secId, String coverId,Integer locId) {
		List<Map<String,Object>> excessList = new ArrayList<Map<String,Object>>();
		try {
			List<ExcessTransactionDetails> etrList =extransactionRepo.findByRequestReferenceNoAndProductIdAndSectionIdAndCoverId(home.getRequestReferenceNo(),home.getProductId().toString(),secId,coverId);
			if (etrList != null && !etrList.isEmpty()) {
				etrList.forEach(e_l -> {
					Map<String, Object> e = new HashMap<>();
					e.put("excessName", e_l.getCoverName() == null ? "" : e_l.getCoverName());
					e.put("ExcessDescription",
							e_l.getExcessDescription() == null ? "" : e_l.getExcessDescription());
					e.put("ExcessPercentage",
							e_l.getExcessPercentage() == null ? 0 : e_l.getExcessPercentage());
					e.put("ExcessAmount",
							e_l.getExcessAmount() == null ? 0.00 : e_l.getExcessAmount());
					excessList.add(e);
				});
		}
			else {
				List<ExcessMaster> excess = excessRepo.findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(
						home.getCompanyId(), home.getProductId().toString(),
						secId.toString(), coverId.toString());
				if (excess != null && !excess.isEmpty()) {
					excess.forEach(e_l -> {
						Map<String, Object> e = new HashMap<>();
						e.put("excessName", e_l.getCoverName() == null ? "" : e_l.getCoverName());
						e.put("ExcessDescription",
								e_l.getExcessDescription() == null ? "" : e_l.getExcessDescription());
						e.put("ExcessPercentage",
								e_l.getExcessPercentage() == null ? 0 : e_l.getExcessPercentage());
						e.put("ExcessAmount",
								e_l.getExcessAmount() == null ? 0.00 : e_l.getExcessAmount());
						excessList.add(e);
					});
			}
			}
				
		}catch(Exception e) {
			log.info("Error in getExclusionList ==> "+e.getMessage());
			e.printStackTrace();
		}
		return excessList;
	}
	
	 public CommonRes getPremiumReport(PremiumReportReq req) {
	        log.info("Enter into PremiumReport ==> " + gson.toJson(req));
	        ByteArrayOutputStream output = new ByteArrayOutputStream();
	        PremiumReportRes preRes = new PremiumReportRes();
	        CommonRes response = new CommonRes();
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        String fileName = "", prefix = "", companylogo = "";
	        try {
	            PortFolioGridReq req1 = new PortFolioGridReq();
	            req1.setBranchCode(req.getBranchCode());
	            req1.setBusinessType(req.getBusinessType());
	            req1.setEndDate(sdf.parse(req.getEndDate()));
	            req1.setStartDate(sdf.parse(req.getStartDate()));
	            req1.setInsuranceId(req.getInsuranceId());
	            req1.setProductId(req.getProductId());
	            req1.setLoginId(req.getLoginId());
	            req1.setShowAllDataYn("Y");
	            req1.setUserType(req.getUserType());
	            req1.setPortFolioYn(req.getPortFolioYn());

	            List<PortfolioGridRes> result = gridServiceImpl.getAllPolicyGrid(req1);

	            if (!result.isEmpty()) {
	                result.stream().filter(f -> Arrays.asList("5", "46").contains(f.getProductId())).forEach(o -> {
	                    List<MotorDataDetails> vehicleDetails = motorRepo.findByQuoteNoOrderByVehicleIdAsc(o.getQuoteNo())
	                            .stream().filter(f -> !f.getStatus().equalsIgnoreCase("D")).collect(Collectors.toList());

	                    List<MotorPrivateVehicleDetails> vehicleDetailsRes = new ArrayList<MotorPrivateVehicleDetails>();
	                    vehicleDetails.forEach(k -> {
	                        MotorPrivateVehicleDetails t = MotorPrivateVehicleDetails.builder()
	                                .vehicleId(k.getVehicleId() == null ? "" : k.getVehicleId().toString())
	                                .registrationNumber(k.getRegistrationNumber() == null ? "" : k.getRegistrationNumber().toString())
	                                .vehicleMake(k.getVehicleMakeDesc() == null ? "" : k.getVehicleMakeDesc().toString())
	                                .vehcileModel(k.getVehcileModelDesc() == null ? "" : k.getVehcileModelDesc().toString())
	                                .vehicleTypeDesc(k.getVehicleTypeDesc() == null ? "" : k.getVehicleTypeDesc().toString())
	                                .cubicCapacity(k.getCubicCapacity() == null ? "" : k.getCubicCapacity().toString())
	                                .manufactureYear(k.getManufactureYear() == null ? "" : k.getManufactureYear().toString())
	                                .seatingCapacity(k.getSeatingCapacity() == null ? null : k.getSeatingCapacity().toString())
	                                .colorDesc(k.getColorDesc() == null ? "" : k.getColorDesc().toString())
	                                .policyTypeDesc(k.getPolicyTypeDesc() == null ? "" : k.getPolicyTypeDesc().toString())
	                                .policyTypeId(k.getPolicyType() == null ? "" : k.getPolicyType())
	                                .windScreenSumInsuredLc(k.getWindScreenSumInsured() == null ? null : new BigDecimal(Double.parseDouble(k.getWindScreenSumInsured().toString())).toString())
	                                .sumInsured(k.getSumInsured() == null ? null : new BigDecimal(Double.parseDouble(k.getSumInsured().toString())).toString())
	                                .stickerNumber(getStrickerNo(k.getQuoteNo(), k.getVehicleId()))
	                                .grossWeight(k.getGrossWeight() == null ? null : k.getGrossWeight().toString())
	                                .insTypeDesc(k.getInsuranceTypeDesc() == null ? "" : k.getInsuranceTypeDesc())
	                                .engineNumber(k.getEngineNumber() == null ? "" : k.getEngineNumber())
	                                .tPPDIncreaseLimit(k.getTppdIncreaeLimit() == null ? null : new BigDecimal(Double.parseDouble(k.getTppdIncreaeLimit().toString())).toString())
	                                .chassisNumber(k.getChassisNumber() == null ? "" : k.getChassisNumber())
	                                .fuelType(k.getFuelTypeDesc() == null ? "" : k.getFuelTypeDesc())
	                                .premium(new BigDecimal(Double.parseDouble(k.getOverallPremiumFc().toString())).toString())
	                                .build();
	                        vehicleDetailsRes.add(t);
	                    });

	                    o.setVehicleDetails(vehicleDetailsRes);
	                });
	            }

	            List<Map<String, Object>> companyDetails = insuranceComMasRepo.getCompanyDetailsById(req.getInsuranceId());
	            if (!companyDetails.isEmpty()) {
	                companylogo = companyDetails.get(0).get("COMPANY_LOGO") == null ? "" : companyDetails.get(0).get("COMPANY_LOGO").toString();
	            }

	            String classpath = this.getClass().getClassLoader().getResource("").getPath();
	            classpath = classpath.replaceAll("%20", " ");
	            classpath = classpath.substring(1, classpath.length());

	           // String imagepath = classpath + "report/images/" + companylogo; //windows system path

	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("pvImagepath", externalImagePath+companylogo);

				/*result.sort(Comparator
				        .comparing(PortfolioGridRes::getBranchName, String.CASE_INSENSITIVE_ORDER)
				        .thenComparing(PortfolioGridRes::getProductName, String.CASE_INSENSITIVE_ORDER)
				        .thenComparing(PortfolioGridRes::getBrokerName, String.CASE_INSENSITIVE_ORDER)
				);*/

	            result.sort(
	                    Comparator
	                            .comparing(PortfolioGridRes::getBranchName,
	                                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
	                            .thenComparing(PortfolioGridRes::getProductName,
	                                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
	                            .thenComparing(PortfolioGridRes::getBrokerName,
	                                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
	            );

	            String jsonString = gson.toJson(result);
	            log.info("PremiumReport Response ==> " + jsonString);

	            if ("Y".equalsIgnoreCase(req.getExcelYn())) {
	                fileName = "PremiumRegister";
	                prefix = "data:application/vnd.ms-excel;base64,";
	                JsonDataSource dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
	                InputStream inputStream = this.getClass().getResourceAsStream("/report/jasper/EwayPremiumReportSql.jrxml");
	                JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
	                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
	                JRXlsxExporter exporter = new JRXlsxExporter();
	                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(output));

	                SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
	                configuration.setOnePagePerSheet(false);
	                configuration.setDetectCellType(true);
	                configuration.setCollapseRowSpan(false);

	                exporter.setConfiguration(configuration);
	                try {
	                    exporter.exportReport();
	                } catch (Exception e) {
	                    log.info("Error in PremiumReport ==> " + e.getMessage());
	                    e.printStackTrace();

	                }
	                log.info("PremiumReport Report Created");
	            } else {
	                fileName = "PremiumRegister";
	                prefix = "data:application/pdf;base64,";
	                JsonDataSource dataSource = new JsonDataSource(new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8)));
	                InputStream inputStream = this.getClass().getResourceAsStream("/report/jasper/EwayPremiumReportSql.jrxml");
	                JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
	                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
	                JRPdfExporter pdfExporter = new JRPdfExporter();
	                pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
	                pdfExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
	                pdfExporter.exportReport();
	            }
	            String jasperPath = policyReportPath + req.getLoginId() + System.currentTimeMillis() + ("Y".equalsIgnoreCase(req.getExcelYn()) ? ".xlsx" : ".pdf");
	            log.info("PremiumReport byte Part");
	            byte[] bs = output.toByteArray();
	            String encodeToString = Base64.getEncoder().encodeToString(bs);
	            FileOutputStream fs = new FileOutputStream(new File(jasperPath));
	            fs.write(bs);
	            fs.flush();
	            fs.close();

	            preRes = PremiumReportRes.builder()
	                    .base64(prefix + encodeToString)
	                    .fileName(fileName)
	                    .filePath(jasperPath)
	                    .build();

	            log.info("PremiumReport res set");
	            response.setCommonResponse(preRes);
	            response.setIsError(false);
	            response.setErrorMessage(Collections.emptyList());
	            response.setMessage("Success");
	        } catch (Exception e) {
	            log.info("Error in PremiumReport ==> " + e);
	            response.setCommonResponse(null);
	            response.setIsError(true);
	            response.setErrorMessage(Collections.emptyList());
	            response.setMessage("Failed");
	            e.printStackTrace();
	        }
	        return response;
	    }
	 
	 public List<EmiDisplayRes> viewEmiInstallmentDetails(EmiInstallmentDetailsReq req) {
			List<EmiDisplayRes> resList = new ArrayList<EmiDisplayRes>();
			try {
				if(req.getCompanyId().equalsIgnoreCase("100020")) {
					resList=kenyaEmiTransactionDetails.viewEmiInstallmentDetails(req);
				}else { 
					resList =phoenixZambiaEmiTransactionDetailsService.viewEmiInstallmentDetails(req); 
				}
	 		} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}

			return resList;
		}

}