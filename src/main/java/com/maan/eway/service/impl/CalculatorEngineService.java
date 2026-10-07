package com.maan.eway.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.maan.eway.admin.service.RestTemplateApiService;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.ChartOfAccount;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.MsDriverDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsLifeDetails;
import com.maan.eway.bean.MsPolicyDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataEndt;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TaxRemover;
import com.maan.eway.calculator.util.AdminCoverCalculator;
import com.maan.eway.calculator.util.CoverCalculator;
import com.maan.eway.calculator.util.CoverFromFactor;
import com.maan.eway.calculator.util.CreateMinimumPremium;
import com.maan.eway.calculator.util.DiscountFromFactor;
import com.maan.eway.calculator.util.EndtCoverCalculator;
import com.maan.eway.calculator.util.EndtFromFactor;
import com.maan.eway.calculator.util.LoadingFromFactor;
import com.maan.eway.calculator.util.PolicyCoverCalculator;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.calculator.util.SplitDiscountUtils;
import com.maan.eway.calculator.util.SplitLoadingUtils;
import com.maan.eway.calculator.util.SplitSubCoverUtil;
import com.maan.eway.calculator.util.SubCoverCreationUtil;
import com.maan.eway.calculator.util.TaxFromFactor;
import com.maan.eway.calculator.util.TaxUtils;
import com.maan.eway.calculator.util.TupleWrapper;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.EndtUpdatePremiumRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.config.ThreadMonitorService;
import com.maan.eway.endorsment.util.AddCreateEndorsment;
import com.maan.eway.endorsment.util.CoverFromPolicy;
import com.maan.eway.endorsment.util.CreateEndorsment;
import com.maan.eway.endorsment.util.DiscountFromPolicy;
import com.maan.eway.endorsment.util.LoadingFromPolicy;
import com.maan.eway.overalldiscount.MapBackedTuple;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MsHumanDetailsRepository;
import com.maan.eway.repository.MsVehicleDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataEndtRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.req.CalcEngineBatch;
import com.maan.eway.req.UnderwriterAdjustmentReq;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.calcengine.ReferralApi;
import com.maan.eway.res.calc.AdminReferral;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.res.calc.UWReferrals;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.service.CalculatorEngine;

import com.maan.eway.service.impl.referal.ReferalServiceImpl;
import com.maan.eway.thirdparty.TravelApiIntegration;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.JoinCriteria;
import com.maan.eway.upgrade.criteria.SpecCriteria;
import com.maan.eway.workflow.dto.WorkEngine;
import com.maan.eway.workflow.service.JsonMapperFromDB;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Scope("prototype")
public class CalculatorEngineService implements CalculatorEngine {

	// 1.Section
	// 2.Cover

	@Autowired
	private CriteriaService crservice;

	@Autowired
	private RatingFactorsUtil ratingutil;

	@Autowired
	private CoverDetailsRepository coverRepo;

	@Autowired
	private EmiTransactionDetailsRepository emiRepo;
	/*
	 * 
	 * @Autowired private CoverCalculator calc;
	 */

	@Autowired
	RestTemplateApiService restTemplateApiService;

	@Autowired
	private MsHumanDetailsRepository msHumanRepo;
	
	@Autowired
	private EserviceTravelGroupDetailsRepository  eserviceTravelGroupRepo;

	@Autowired
	LoginMasterRepository loginMasterRepository;

	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Value(value = "${calEngine}")
	private String calEngine;

	protected List<Tuple> commontbl = null;
	protected List<Tuple> vehicles = null;
	protected List<Tuple> customers = null;
	protected List<Cover> calculatedcover = null;
	protected List<Tuple> prorata = null;
	protected BigDecimal minimumPremium = BigDecimal.ZERO;
	protected List<Tuple> policytbl = null;
	protected List<Tuple> drivers = null;
	protected List<Tuple> customerChoiceTaxes;

	@Autowired
	private FactorRatePersistenceService fservice;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private FactorRateRequestDetailsRepository repository;

	@Autowired
	private ReferalServiceImpl referal;


//	@Autowired
//	@Lazy
//	private QuoteService quoteservice;

	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private GenerateSeqNoServiceImpl genNo;

	DecimalFormat decimalFormat = null;
	@Autowired
	private PolicyCoverDataRepository coverDataRepo;

	@Autowired
	private TravelPassengerDetailsRepository travelRepo;

	@Autowired
	private BuildingRiskDetailsRepository buildingRepo;

	@Autowired
	private CommonDataDetailsRepository commonRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private PersonalInfoRepository piRepo;

	@Autowired
	private EServiceSectionDetailsRepository esSecRepo;

	@Autowired
	private EserviceBuildingDetailsRepository eservicebuildingRepo;

	@Autowired
	private EserviceCommonDetailsRepository eservicecommonRepo;

	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo;

	@Autowired
	private EServiceMotorDetailsRepository eservicemotorRepo;
	
	@Autowired
	private CompanyProductMasterRepository companyProductMasterRepo;

	
	private Boolean isPolicyPeriod = Boolean.FALSE;
	@Autowired
	private MsVehicleDetailsRepository msVehicleRepo;

	private String oldPolicyPeriod;

	private String currentPolicyPeriod;

	private Date prevPolicyEndDate;
	/*
	 * public void LoadSection(CalcEngine engine) {
	 * 
	 * try { String todayInString = DD_MM_YYYY.format(new Date());
	 * 
	 * String search="companyId:"+ engine.getInsuranceId()
	 * +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+
	 * ";status=Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
	 * List<Tuple> result=null; SpecCriteria criteria =
	 * crservice.createCriteria(ProductSectionMaster.class, search, "coverId");
	 * result=crservice.getResult(criteria, 0, 50);
	 * 
	 * System.out.println("result"+result.size()); }catch(Exception e) {
	 * e.printStackTrace(); } }
	 */
	private final List<String> NORMAL_TAX_LIST = Arrays.asList("NB");
	private final List<String> ENDT_TAX_LIST = Arrays.asList("EC", "ER");
	@Autowired
	private PolicyCoverDataEndtRepository policyCoverEndtRepo;

	@Autowired
	private JsonMapperFromDB jsonMapper;

	@Autowired
	private TravelApiIntegration travelInteg;

	@Autowired
	private ThreadMonitorService threadMonitorService;

	private static final AtomicInteger activeThreads = new AtomicInteger(0);

	public List<Tuple> LoadCover(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			/*
			 * String search1 = "companyId:" + engine.getInsuranceId() + ";productId:" +
			 * engine.getProductId() + ";sectionId:" + engine.getSectionId() +
			 * ";status:{Y,R};" + todayInString + "~effectiveDateStart&effectiveDateEnd;" +
			 * "agencyCode:" + engine.getAgencyCode() + ";branchCode:" +
			 * engine.getBranchCode() + ";";
			 */

String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;";
			List<Tuple> commonResult = null;
			if (!"0".equals(engine.getCoverId())) {

				String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
						+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
						+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;coverId:"
						+ engine.getCoverId() + ";";

				SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
				commonResult = crservice.getResult(commonCriteria, 0, 50);

				search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId() + ";sectionId:"
						+ engine.getSectionId() + ";status:{Y,R};" + todayInString
						+ "~effectiveDateStart&effectiveDateEnd;"
						+ "agencyCode:99999;branchCode:99999;dependentCoverId:" + engine.getCoverId() + ";";

				commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
				List<Tuple> commonResult1 = crservice.getResult(commonCriteria, 0, 50);

				search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId() + ";sectionId:"
						+ engine.getSectionId() + ";status:{Y,R};" + todayInString
						+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;discountCoverId:"
						+ engine.getCoverId() + ";";

				commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
				List<Tuple> commonResult2 = crservice.getResult(commonCriteria, 0, 50);

				commonResult.addAll(commonResult1);
				commonResult.addAll(commonResult2);

			} else {

				String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
						+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
						+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;";

				SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
				commonResult = crservice.getResult(commonCriteria, 0, 50);
			}

			SpecCriteria criteria = null;

			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
			List<Long> count = crservice.getCount(criteria, 0, 50);
			if (!count.isEmpty()) {
				Long countrec = count.get(0);
				if (countrec > 0) {
					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
					for (Tuple t : specific) {
						commonResult.removeIf(c -> c.get("coverId").toString().equals(t.get("coverId").toString()));
						commonResult.add(t);
					}
				}

			}

			return commonResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, BigDecimal> loadFixedValue(CalcEngine engine) {
		try {
			List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);

			List<Tuple> totalcoverstuple = LoadCoverFixedValue(engine);
			if (totalcoverstuple != null && totalcoverstuple.size() > 0) {
				List<Tuple> covers = totalcoverstuple.parallelStream()
						.filter(t -> "N".equals(t.get("dependentCoverYn").toString())).collect(Collectors.toList());
				List<Discount> discounts = null;
				List<Loading> loadings = null;
				if (covers != null && covers.size() > 0) {
					SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
							engine.getPolicyEndDate(), "");
					discounts = covers.parallelStream().map(discountUtil).filter(d -> d != null)
							.collect(Collectors.toList());
					discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
					SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
							engine.getPolicyEndDate());
					loadings = covers.parallelStream().map(loadingtuils).filter(d -> d != null)
							.collect(Collectors.toList());
				}

				SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
						engine.getPolicyEndDate());
				Map<String, List<Cover>> nonSubcovers = covers.parallelStream().map(splitsub).filter(d -> d != null)
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!nonSubcovers.isEmpty()) {
					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							// .collect(Collectors.toUnmodifiableList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							c.setDiscounts(ds);

						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							// .collect(Collectors.toUnmodifiableList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							c.setLoadings(ds);

						}
					}

				}

				List<Cover> totalcovers = new ArrayList<Cover>();
				if (!nonSubcovers.isEmpty()) {
					totalcovers.addAll(nonSubcovers.get("N"));
				}
				CoverCalculator calc = new CoverCalculator();
				calc.setEngine(engine, totalcovers, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
						drivers, customerChoiceTaxes);
				totalcovers.parallelStream().forEach(calc);
				// 286,319,287,324
				BigDecimal premiumLLD = totalcovers.stream()
						.sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed())
						.filter(c -> "324".equals(c.getCoverId())).map(x -> x.getPremiumExcluedTax())
						.reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);
				BigDecimal premiumTPL = totalcovers.stream()
						.sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed())
						.map(x -> x.getPremiumExcluedTax()).reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);

				MsVehicleDetails details = msVehicleRepo.findByVdRefno(Long.parseLong(engine.getVdRefNo()));
				details.setPremiumLLD(premiumLLD);
				details.setPremiumTPL(premiumTPL);
				msVehicleRepo.save(details);
				Map<String, BigDecimal> hap = new HashMap<String, BigDecimal>();
				hap.put("PremiumLLD", premiumLLD);
				hap.put("PremiumTPL", premiumTPL);
				return hap;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<Tuple> LoadCoverFixedValue(CalcEngine engine) {

		try {
			String todayInString = DD_MM_YYYY.format(new Date());

			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;coverId:324";

			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;coverId:324";

			SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);

			SpecCriteria criteria = null;
			// 286,319,287,324
			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
			List<Long> count = crservice.getCount(criteria, 0, 50);
			if (!count.isEmpty()) {
				Long countrec = count.get(0);
				if (countrec > 0) {
					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
					for (Tuple t : specific) {
						commonResult.removeIf(c -> c.get("coverId").toString().equals(t.get("coverId").toString()));
						commonResult.add(t);
					}
				}

			}

			return commonResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public synchronized EserviceMotorDetailsSaveRes calculator(CalcEngine engine, String token) {
//
//		if ("100040".equals(engine.getInsuranceId()) || "100027".equals(engine.getInsuranceId())) {
//
//			WorkEngine work = new WorkEngine();
//			work.setCompanyId(engine.getInsuranceId());
//			work.setProductId(engine.getProductId());
//			work.setQuoteNo("");
//			work.setRequestReferenceNo(engine.getRequestReferenceNo());
//			work.setIntegType("QUOT_INTEG");
//			work.setSectionId(engine.getSectionId());
//			work.setMsrefno(engine.getMsrefno());
//			work.setVdRefNo(engine.getVdRefNo());
//			work.setCdRefNo(engine.getCdRefNo());
//			work.setVdRefNo(engine.getVdRefNo());
//			work.setLocationId(engine.getLocationId());
//			work.setCreatedBy(engine.getCreatedBy());
//			work.setVehicleId(engine.getVehicleId());
//			jsonMapper.createQuotation(work);
//			return null;
//		} else if ("4".equals(engine.getProductId()) && ("100046".equals(engine.getInsuranceId())
//				|| "100047".equals(engine.getInsuranceId()) || "100048".equals(engine.getInsuranceId())
//				|| "100049".equals(engine.getInsuranceId()) || "100050".equals(engine.getInsuranceId()))) {
//			loadOnetimetable(engine);
//			travelInteg = new TravelApiIntegration(fservice, ratingutil, commontbl, vehicles, msHumanRepo,eserTraRepo);
//			EserviceMotorDetailsSaveRes response = travelInteg.pushZeus_GetAvailablePlansOTAWithRiders(engine);
//			return response;
//		} else {
//
//			LoginMaster loginMaster = loginMasterRepository.findByLoginId(engine.getCreatedBy()); // loginId
//
//			List<UWReferrals> referr = null;
//
//			List<MasterReferal> masterreferral = null;
//
//			// Referal Checking.
//			BigDecimal endtCount = BigDecimal.ZERO;
//
//			if (!(loginMaster.getUserType().equalsIgnoreCase("issuer")
//					&& loginMaster.getSubUserType().equalsIgnoreCase("superadmin"))) {
//				referr = referal.underwriterReferral(engine);
//
//				try {
//					masterreferral = referal.masterreferral(engine, token);
//				} catch (ClassNotFoundException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//			String isEndt = null;
//
//			List<Cover> retc = new ArrayList<Cover>();
//			try {
//
//				loadOnetimetable(engine);
//				if ("100040".equals(engine.getInsuranceId())) {
//					Map<String, BigDecimal> fixedValue = loadFixedValue(engine);
//					loadOnetimetable(engine);
//				}
//				if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
//						|| (customers == null || customers.size() == 0)) {
//					System.out.println("::: Exception :: ");
//					throw new Exception();
//
//					/*
//					 * throw
//					 * CoverException.builder().message("Exception :: onetime table not inserted")
//					 * .isError(true).build();
//					 */
//				}
//
//				String promocode = vehicles.get(0).get("promocode") == null ? "": vehicles.get(0).get("promocode").toString();
//				List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
//
//				List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
//
//				TaxUtils tzx = new TaxUtils(endtCount, "");
//				List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
//
//				TaxRemover taxRemov = new TaxRemover(excludedTaxes, null);
//
//				List<String> dependedcovers = new ArrayList<String>();
//				dependedcovers.add("N");
//				dependedcovers.add("Y");
//
//				List<Tuple> totalcoverstuple = LoadCover(engine);
//				// effectiveDateStart&effectiveDateEnd
//
//				for (String dependcover : dependedcovers) {
//					List<Cover> totalcovers = new ArrayList<Cover>();
//					// CopyOnWriteArrayList<Cover> totalcovers=new CopyOnWriteArrayList<Cover>();
//					List<Tuple> covers = totalcoverstuple.parallelStream()
//							.filter(t -> dependcover.equals(t.get("dependentCoverYn").toString()))
//							.collect(Collectors.toList());
//					List<Discount> discounts = null;
//					List<Loading> loadings = null;
//					if (covers != null && covers.size() > 0) {
//						SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
//								engine.getPolicyEndDate(), promocode);
//						discounts = covers.parallelStream().map(discountUtil).filter(d -> d != null)
//								.collect(Collectors.toList());
//						discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
//						SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
//								engine.getPolicyEndDate());
//						loadings = covers.parallelStream().map(loadingtuils).filter(d -> d != null)
//								.collect(Collectors.toList());
//					}
//
//					SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
//							engine.getPolicyEndDate());
//					Map<String, List<Cover>> nonSubcovers = covers.parallelStream().map(splitsub).filter(d -> d != null)
//							.collect(Collectors.groupingBy(Cover::getIsSubCover));
//					if (!nonSubcovers.isEmpty()) {
//						List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
//						if (!discounts.isEmpty() && !noncovers.isEmpty()) {
//							for (Cover c : noncovers) {
//								List<Discount> ds = discounts.stream()
//										.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//										.collect(Collectors.toList());
//								// .collect(Collectors.toUnmodifiableList());
//								ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//								// List<Tax> taxey =
//								// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//								c.setDiscounts(ds);
//								// c.setTaxes(taxey);
//							}
//						}
//
//						if (!loadings.isEmpty() && !noncovers.isEmpty()) {
//							for (Cover c : noncovers) {
//								List<Loading> ds = loadings.stream()
//										.filter(d -> d.getLoadingforId().equals(c.getCoverId()))
//										.collect(Collectors.toList());
//								// .collect(Collectors.toUnmodifiableList());
//								ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//								// List<Tax> taxey =
//								// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//								c.setLoadings(ds);
//								// c.setTaxes(taxey);
//							}
//						}
//
//						if (!noncovers.isEmpty()) {
//							for (Cover c : noncovers) {
//								if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
//									List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
//											.collect(Collectors.toList());
//									// .collect(Collectors.toUnmodifiableList());
//									c.setTaxes(taxey);
//								}
//							}
//						}
//					}
//
//					splitsub = new SplitSubCoverUtil("Y", engine.getEffectiveDate(), engine.getPolicyEndDate());
//					Map<String, List<Cover>> subcovers = covers.parallelStream().map(splitsub)
//
//							.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
//							.collect(Collectors.groupingBy(Cover::getIsSubCover));
//					if (!subcovers.isEmpty()) {
//						List<Cover> noncovers = subcovers.get("Y"); // noncovers
//						if (!discounts.isEmpty() && !noncovers.isEmpty()) {
//							for (Cover c : noncovers) {
//								List<Discount> ds = discounts.stream()
//										.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//										.collect(Collectors.toList());
//								ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//
//								List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
//										.collect(Collectors.toList());
//								// .collect(Collectors.toUnmodifiableList()) ;
//								// List<Tax> taxez =
//								// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//								c.setDiscounts(dss);
//								// c.setTaxes(taxez);
//							}
//						}
//
//						if (!loadings.isEmpty() && !noncovers.isEmpty()) {
//							for (Cover c : noncovers) {
//								List<Loading> ds = loadings.stream()
//										.filter(d -> d.getLoadingforId().equals(c.getCoverId()))
//										.collect(Collectors.toList());
//								ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//
//								List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
//										.collect(Collectors.toList());
//								// .collect(Collectors.toUnmodifiableList());
//								c.setLoadings(dss);
//							}
//						}
//						if (!noncovers.isEmpty()) {
//							for (Cover c : noncovers) {
//								if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
//									List<Tax> taxey = taxes.parallelStream().map(tzx).filter(d -> d != null)
//											.collect(Collectors.toList());
//									// .collect(Collectors.toUnmodifiableList());
//									c.setTaxes(taxey);
//								}
//							}
//						}
//
//						List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
//								.collect(Collectors.toList());
//						List<Cover> subcov = new ArrayList<Cover>();
//						// CopyOnWriteArrayList<Cover> subcov=new CopyOnWriteArrayList<Cover>();
//						for (Cover cover : d) {
//							List<Cover> subcover = noncovers.stream()
//									.filter(cv -> cv.getCoverId().equals(cover.getCoverId()))
//									.collect(Collectors.toList());
//							subcover.stream().forEach(s -> s.setIsSubCover("N"));
//							subcover.stream().forEach(taxRemov);
//							// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
//							Cover newcover = SerializationUtils.clone(cover);
//							newcover.setSubcovers(subcover);
//							newcover.setIsSubCover("Y");
//							newcover.setSubCoverId(null);
//							newcover.setSubCoverDesc(null);
//							newcover.setSubCoverName(null);
//							newcover.setDiscounts(null);
//							newcover.setLoadings(null);
//							newcover.setTaxes(null);
//							subcov.add(newcover);
//						}
//						subcovers.put("Y", subcov);
//					}
//
//					if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
//						// totalcovers =
//						List<Cover> list = subcovers.get("Y");
//						totalcovers.addAll(list);
//						totalcovers.addAll(nonSubcovers.get("N"));
//					} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
//						totalcovers.addAll(nonSubcovers.get("N"));
//					} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
//						totalcovers.addAll(subcovers.get("Y"));
//					}
//
//					totalcovers.stream().forEach(taxRemov);
//					/*
//					 * if(StringUtils.isNotBlank(engine.getVdRefNo()) &&
//					 * StringUtils.isNotBlank(engine.getCdRefNo())) { //calc.setEngine(engine,
//					 * retc);
//					 * 
//					 * 
//					 * }
//					 */
//
//					CoverCalculator calc = new CoverCalculator();
//					calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
//							drivers, customerChoiceTaxes);
//
//					totalcovers.parallelStream().forEach(calc);
//					// remove error records
//					totalcovers.removeIf(ll -> (ll.isNotsutable()));
//					retc.addAll(totalcovers);
//					Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
//					retc.sort(comp);
//				}
//				// if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
//				BigDecimal totalPremium = retc.stream()
//						.filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId())
//								&& x.getPremiumExcluedTaxLC() != null))
//						.map(x -> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO, BigDecimal::add);
//				if (totalPremium.compareTo(minimumPremium) < 0) {
//					List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null).collect(Collectors.toList());
//					BigDecimal difference = minimumPremium.subtract(totalPremium, MathContext.DECIMAL32);
//					CreateMinimumPremium min = new CreateMinimumPremium(difference, engine, endtCount, taxey);
//					Cover mini = min.create();
//					List<Cover> minies = new ArrayList<Cover>(1);
//					minies.add(mini);
//					CoverCalculator calc = new CoverCalculator();
//					calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
//							drivers, customerChoiceTaxes);
//					minies.stream().forEach(calc);
//					retc.add(mini);
//
//				} else {
//					retc.removeIf(t -> "945".equals(t.getCoverId()));// .stream().filter(t->
//																		// "945".equals(t.getCoverId()).de
//				}
//
//				try {
//
//					String endtTypeId = vehicles.get(0).get("endtTypeId") == null ? ""
//							: vehicles.get(0).get("endtTypeId").toString();
//					if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
//						String requestRefercenNo = engine.getRequestReferenceNo();
//						String rawtable = ratingutil.getProductIdBasedRawTable(engine);
//						String search = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
//								+ ";sectionId:" + engine.getSectionId() + ";riskId:" + engine.getVehicleId()
//								+ ";status:{E,D,RP};requestReferenceNo:" + requestRefercenNo + ";locationId:"
//								+ (StringUtils.isBlank(engine.getLocationId()) ? "1" : engine.getLocationId());
//						SpecCriteria criteria = crservice.createCriteria(Class.forName(rawtable), search,
//								"requestReferenceNo");
//						List<Long> count = crservice.getCount(criteria, 0, 2);
//						if (!count.isEmpty() && count.get(0) <= 0) {
//
//							String riskid = engine.getVehicleId();
//							search = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
//									+ ";riskId:" + riskid + ";status:{E,D,RP};requestReferenceNo:" + requestRefercenNo
//									+ ";locationId:"
//									+ (StringUtils.isBlank(engine.getLocationId()) ? "1" : engine.getLocationId());
//						}
//
//						List<Tuple> result = null;
//						criteria = crservice.createCriteria(Class.forName(rawtable), search, "requestReferenceNo");
//						result = crservice.getResult(criteria, 0, 50);
//						endtCount = new BigDecimal(result.get(0).get("endtCount").toString());
//						isEndt = "admin";
//						loadAndRemoveCoversForEndt(engine, retc, result);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			} /*
//				 * catch(CoverException e) { e.printStackTrace(); }
//				 */catch (Exception e) {
//				e.printStackTrace();
//			}
//			System.out.println("FactorRateRequestDetails Save-----------------------------");
//			try {
//				EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
//				response.setCoverList(retc);
//				response.setResponse("Saved Successfully");
//				response.setRequestReferenceNo(engine.getRequestReferenceNo());
//				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
//				response.setVehicleId(engine.getVehicleId());
//				response.setVdRefNo(engine.getVdRefNo());
//				response.setCdRefNo(engine.getCdRefNo());
//				response.setInsuranceId(engine.getInsuranceId());
//				response.setSectionId(engine.getSectionId());
//				response.setCreatedBy(engine.getCreatedBy());
//				response.setProductId(engine.getProductId());
//				response.setLocationId(engine.getLocationId());
//				response.setMsrefno(engine.getMsrefno());
//				response.setUpdateas(isEndt);
//				response.setUwList(referr);
//				response.setReferals(masterreferral);
//				response.setCoverId(engine.getCoverId());
//				fservice.saveFactorRateRequestDetails(response);
//
//				// Update Premium,referral
//
//				/// Endoresment calculation
//				try {
//					String endtTypeId = vehicles.get(0).get("endtTypeId") == null ? ""
//							: vehicles.get(0).get("endtTypeId").toString();
//					if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
//						// referalCalculator = referalCalculator(engine);
//						return endorsementCalculator(engine, endtCount, endtTypeId, isPolicyPeriod);
//
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				return response;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return null;
//		}
//	}

	// @Transactional(propagation = Propagation.REQUIRED, rollbackFor =
	// Exception.class)
	public EserviceMotorDetailsSaveRes calculator(CalcEngine engine, String token) {
		// --- Thread monitoring start ---
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
		        + " <---- Save calculator api start  :---->"+engine.getRequestReferenceNo());
		long startTime = System.currentTimeMillis();
		threadMonitorService.monitorThreads("START", "CalculatorEngineService");

		try {
			String insuranceId = engine.getInsuranceId();
			String productId = engine.getProductId();

			// --- 1. Direct Quotation Case ---
			if (List.of("100040", "100027").contains(insuranceId)) {
				return createQuotation(engine);
			}

			// --- 2. Travel Product Case ---
			if ("4".equals(productId)
					&& List.of("100046", "100047", "100048", "100049", "100050").contains(insuranceId)) {
				return handleTravelIntegration(engine);
			}

			// --- 3. Motor Product Calculation ---
			return handleMotorCalculation(engine, token);
		 } finally {
	            // Monitor at end and cleanup
	            long endTime = System.currentTimeMillis();
	            System.out.printf("Calculation completed in %d ms%n", (endTime - startTime));
	            System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
	    		        + " <---- Save calculator api end  :---->"+engine.getRequestReferenceNo());
	            threadMonitorService.monitorThreads("END", "CalculatorEngineService");
	            threadMonitorService.performCleanup();
	            
	            System.out.println("Cleaning up CalculatorEngineService instance: " + this.hashCode());
	        }
	}

	private EserviceMotorDetailsSaveRes createQuotation(CalcEngine engine) {
		WorkEngine work = new WorkEngine();
		work.setCompanyId(engine.getInsuranceId());
		work.setProductId(engine.getProductId());
		work.setQuoteNo("");
		work.setRequestReferenceNo(engine.getRequestReferenceNo());
		work.setIntegType("QUOT_INTEG");
		work.setSectionId(engine.getSectionId());
		work.setMsrefno(engine.getMsrefno());
		work.setVdRefNo(engine.getVdRefNo());
		work.setCdRefNo(engine.getCdRefNo());
		work.setLocationId(engine.getLocationId());
		work.setCreatedBy(engine.getCreatedBy());
		work.setVehicleId(engine.getVehicleId());

		jsonMapper.createQuotation(work);
		return null;
	}

	private EserviceMotorDetailsSaveRes handleTravelIntegration(CalcEngine engine) {
		loadOnetimetable(engine);
		travelInteg = new TravelApiIntegration(fservice, ratingutil, commontbl, vehicles, msHumanRepo, eserTraRepo);
		return travelInteg.pushZeus_GetAvailablePlansOTAWithRiders(engine);
	}

	private EserviceMotorDetailsSaveRes handleMotorCalculation(CalcEngine engine, String token) {
		
		String login=engine.getCreatedBy();	
		System.out.println(login);;
		LoginMaster loginMaster = loginMasterRepository.findByLoginId(engine.getCreatedBy());
		List<UWReferrals> referrals = null;
		List<MasterReferal> masterReferrals = null;

		// --- Referral Handling ---
		if (!("issuer".equalsIgnoreCase(loginMaster.getUserType())
				&& "superadmin".equalsIgnoreCase(loginMaster.getSubUserType()))) {
			referrals = referal.underwriterReferral(engine);
			try {
				masterReferrals = referal.masterreferral(engine, token);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		if ("100053".equals(engine.getInsuranceId()) && "117".equals(engine.getProductId())) {
	        return handlePropertyMultiLocationCalculation(engine, token);
	    }
		
		if ("100019".equals(engine.getInsuranceId()) && "125".equals(engine.getProductId())) {
		    return handleProduct125Calculation(engine, token);
		}

		List<Cover> resultCovers = new ArrayList<>();
		BigDecimal endtCount = BigDecimal.ZERO;
		String isEndt = null;

		try {
			loadOnetimetable(engine);
			if ("100040".equals(engine.getInsuranceId()))
				loadFixedValue(engine);

			validateData();

			String promocode = Optional.ofNullable(vehicles.get(0).get("promocode")).map(Object::toString).orElse("");
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- LoadTax Rate Block  start :---->");
//			List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
//			List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
//			List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
//			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
//			        + " <---- LoadTax Rate Block  end :---->");
//			TaxUtils taxUtils = new TaxUtils(endtCount, "");
//			TaxRemover taxRemover = new TaxRemover(excludedTaxes, null);
			
			// EXISTING — keep as-is
			List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);

			long policyDays = ratingutil.calculatePolicyDays(engine);
			List<Tuple> resolvedTaxes = ratingutil.resolveTaxByPolicyDays(taxes, policyDays);

			List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
			List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);

			TaxUtils taxUtils = new TaxUtils(endtCount, "");
			TaxRemover taxRemover = new TaxRemover(excludedTaxes, null);

			List<String> dependentCovers = List.of("N", "Y");
			List<Tuple> totalCoversTuple = LoadCover(engine);
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- processCoverDependents Block Start:---->");
//			dependentCovers.forEach(depend -> processCoverDependents(engine, depend, totalCoversTuple, taxes,
//					customerChoiceTaxes, promocode, taxUtils, taxRemover, resultCovers));
			
			resultCovers.clear();
			dependentCovers.forEach(depend -> processCoverDependents(
				    engine, depend, totalCoversTuple,
				    resolvedTaxes,        
				    customerChoiceTaxes,
				    promocode, taxUtils, taxRemover, resultCovers
				));
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- processCoverDependents Block end :---->");
			
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
	                + " <---- loadBenefitCovers Block start :---->");
	        loadBenefitCovers(engine, resultCovers);
	        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
	                + " <---- loadBenefitCovers Block end :---->");

			// --- Minimum Premium Check ---
			BigDecimal totalPremium = resultCovers.stream()
					.filter(x -> !"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId())
							&& x.getPremiumExcluedTaxLC() != null)
					.map(Cover::getPremiumExcluedTaxLC).reduce(BigDecimal.ZERO, BigDecimal::add);
			

			if (totalPremium.compareTo(minimumPremium) < 0) {
				adjustMinimumPremium(engine, endtCount, resolvedTaxes, taxUtils, resultCovers);
			} else {
				resultCovers.removeIf(t -> "945".equals(t.getCoverId()));
			}
//			
//			BigDecimal exchangeRate = new BigDecimal(
//			        vehicles.get(0).get("exchangeRate") == null ? "1" : vehicles.get(0).get("exchangeRate").toString());
//
//			CreateOverallPremium overallPremiumBuilder = new CreateOverallPremium(
//			        engine, totalPremium, resolvedTaxes, customers, decimalFormat, exchangeRate);
//
//			List<Cover> overallPremiumCovers = overallPremiumBuilder.create();
//			resultCovers.addAll(overallPremiumCovers);
			
			Object endtType = vehicles.get(0).get("endtTypeId");

			if ((endtType != null  && !"0".equals(endtType) && StringUtils.isNotBlank(endtType.toString())) || !engine.getEndtTypeId().isEmpty()) {

			    endtCount = handleEndorsement(engine, resultCovers);
			}

			// --- Save & Return ---
			EserviceMotorDetailsSaveRes response =buildResponse(engine, referrals, masterReferrals, resultCovers,isEndt);

			fservice.saveFactorRateRequestDetails(response);

			// Check for Endorsement recalculation
			String endtTypeId="";
			if(StringUtils.isNotBlank(engine.getEndtTypeId())) {
			endtTypeId = engine.getEndtTypeId();
			}else
			{
			 endtTypeId = Optional.ofNullable(vehicles.get(0).get("endtTypeId")).map(Object::toString).orElse("");
			}

			if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
			return endorsementCalculator(engine, endtCount, endtTypeId, isPolicyPeriod);
			}
			return response;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void loadBenefitCovers(CalcEngine engine, List<Cover> resultCovers) {
	    try {
	        
	        Map<Integer, List<Integer>> sectionAndCoverIds = resultCovers.stream()
	            .filter(c -> c.getSectionId() != null && c.getCoverId() != null)
	            .collect(Collectors.groupingBy(
	                c -> Integer.valueOf(c.getSectionId()),
	                Collectors.mapping(c -> Integer.valueOf(c.getCoverId()), Collectors.toList())
	            ));

	        List<Cover> benefitCovers = new ArrayList<>();

	        sectionAndCoverIds.forEach((sectionId, coverIds) -> {
	            // Fetch benefit covers from section cover master
	            List<SectionCoverMaster> sectionCoverMasters = getBySectionCoverId(
	                engine.getInsuranceId(),
	                Integer.parseInt(engine.getProductId()),
	                sectionId,
	                coverIds
	            );

	            // Find a reference cover for this section to copy base fields
	            Cover referenceCover = resultCovers.stream()
	                .filter(c -> c.getSectionId() != null 
	                          && c.getSectionId().equals(String.valueOf(sectionId)))
	                .findFirst()
	                .orElse(null);

	            if (referenceCover == null) return;

	            sectionCoverMasters.forEach(master -> {
	                
	                boolean alreadyPresent = resultCovers.stream()
	                    .anyMatch(c -> c.getCoverId() != null 
	                               && c.getCoverId().equals(String.valueOf(master.getCoverId())));
	                if (alreadyPresent) return;

	                Cover benefitCover = new Cover();

	                benefitCover.setInsuranceId(referenceCover.getInsuranceId());
	                benefitCover.setProductId(referenceCover.getProductId());
	                benefitCover.setSectionId(referenceCover.getSectionId());
	                benefitCover.setSectionName(referenceCover.getSectionName());
	                benefitCover.setVehicleId(referenceCover.getVehicleId());
	                benefitCover.setLocationId(referenceCover.getLocationId());
	                benefitCover.setRequestReferenceNo(referenceCover.getRequestReferenceNo());
	                benefitCover.setCdRefNo(referenceCover.getCdRefNo());
	                benefitCover.setVdRefNo(referenceCover.getVdRefNo());
	                benefitCover.setMsrefno(referenceCover.getMsrefno());
	                benefitCover.setCreatedBy(referenceCover.getCreatedBy());
	                benefitCover.setCalcType(referenceCover.getCalcType());
	                benefitCover.setCurrency(referenceCover.getCurrency());
	                benefitCover.setExchangeRate(referenceCover.getExchangeRate());
	                benefitCover.setEffectiveDate(referenceCover.getEffectiveDate());
	                benefitCover.setPolicyEndDate(referenceCover.getPolicyEndDate());
	                benefitCover.setProRataYn(referenceCover.getProRataYn());
	                benefitCover.setProRata(referenceCover.getProRata());
	                benefitCover.setExcessAmount(master.getExcessAmount());
	                benefitCover.setExcessPercent(master.getExcessPercent());
	                benefitCover.setExcessDesc(master.getExcessDesc());
	                // Benefit cover specific fields
	                benefitCover.setCoverId(String.valueOf(master.getCoverId()));
	                benefitCover.setCoverName(master.getCoverName());
	                benefitCover.setCoverDesc(master.getCoverDesc());
	                benefitCover.setCoverageType("A");
	                benefitCover.setCoverageLimit(master.getCoverageLimit() == null 
	                                              ? BigDecimal.ZERO : master.getCoverageLimit());
	                benefitCover.setIsselected("Y");
	                benefitCover.setIsSubCover("N");
	                benefitCover.setDependentCoverId("0");
	                benefitCover.setDependentCoveryn("N");
	                benefitCover.setMultiSelectYn("N");

	                // All premiums zero — benefit cover, no premium
	                benefitCover.setRate(0.0d);
	                benefitCover.setSumInsured(BigDecimal.ZERO);
	                benefitCover.setPremiumAfterDiscount(BigDecimal.ZERO);
	                benefitCover.setPremiumBeforeDiscount(BigDecimal.ZERO);
	                benefitCover.setPremiumExcluedTax(BigDecimal.ZERO);
	                benefitCover.setPremiumIncludedTax(BigDecimal.ZERO);
	                benefitCover.setPremiumAfterDiscountLC(BigDecimal.ZERO);
	                benefitCover.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
	                benefitCover.setPremiumExcluedTaxLC(BigDecimal.ZERO);
	                benefitCover.setPremiumIncludedTaxLC(BigDecimal.ZERO);

	                benefitCovers.add(benefitCover);
	            });
	        });
	        resultCovers.addAll(benefitCovers);
	        System.out.println("Benefit covers added in calc: " + benefitCovers.size());

	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("loadBenefitCovers error: " + e.getMessage());
	    }
	}
	
	public List<SectionCoverMaster> getBySectionCoverId(String Company_id,Integer product_id,Integer Sectionid,List<Integer> coverIdsToExclude) {
		List<SectionCoverMaster> list = new ArrayList<SectionCoverMaster>();
		String pattern = "#####0.00";
		DecimalFormat df = new DecimalFormat(pattern);
		
		String patternn = "#####0.0000";
		DecimalFormat df1 = new DecimalFormat(patternn);
		
		try {
			Date today  =  new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();

			
			// Find Latest Record
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<SectionCoverMaster> query2 = cb2.createQuery(SectionCoverMaster.class);

			// Find All
			Root<SectionCoverMaster> b2 = query2.from(SectionCoverMaster.class);

			// Effective Date Max Filter
			
			Subquery<Long> amendId = query2.subquery(Long.class);
			Root<SectionCoverMaster> ocpm2 = amendId.from(SectionCoverMaster.class);
			amendId.select(cb2.max(ocpm2.get("amendId")));
			Predicate a7 = cb2.equal(ocpm2.get("coverId"), b2.get("coverId"));
			Predicate a8 = cb2.equal(ocpm2.get("sectionId"), b2.get("sectionId"));
			Predicate a9 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
			Predicate a10 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
			Predicate a11 = cb2.equal(ocpm2.get("subCoverId"), b2.get("subCoverId"));
			Predicate a13 = cb2.equal(ocpm2.get("agencyCode"), b2.get("agencyCode"));
			Predicate a14 = cb2.equal(ocpm2.get("branchCode"), b2.get("branchCode"));
			amendId.where(a7,a8,a9,a10,a11,a13,a14);

			// Select
			query2.select(b2);

			// Order By
			List<Order> orderList2 = new ArrayList<Order>();
			orderList2.add(cb2.desc(b2.get("effectiveDateEnd")));

			// Where
			Predicate n5 = cb2.equal(b2.get("amendId"),amendId);
			Predicate n6 =cb2.equal(b2.get("subCoverId"), "0");
			Predicate n7 = cb2.equal(b2.get("productId"),product_id);
			Predicate n14 = cb2.equal(b2.get("companyId"), Company_id);
			Predicate n15 = cb2.equal(b2.get("sectionId"), Sectionid);
			Predicate n16 = cb2.equal(b2.get("coverageType"), "A");
			Predicate n18 = cb2.equal(b2.get("status"), "Y");

			Predicate n17;
			if (coverIdsToExclude != null && !coverIdsToExclude.isEmpty()) {
			    n17 = cb2.not(b2.get("coverId").in(coverIdsToExclude));
			} else {
			    n17 = cb2.conjunction(); // No exclusion
			}
			Predicate[] predicatesArray = new Predicate[] { n5, n6, n7, n14, n15, n16,n17,n18 };

		
			query2.where(predicatesArray).orderBy(orderList2);

			// Get Result
			TypedQuery<SectionCoverMaster> result2 = em.createQuery(query2);
			list = result2.getResultList();
			list.stream().distinct().collect(Collectors.toList());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	private void validateData() throws Exception {
		if ((commontbl == null || commontbl.isEmpty()) || (vehicles == null || vehicles.isEmpty())
				|| (customers == null || customers.isEmpty())) {
			throw new Exception("Exception :: onetime table not inserted");
		}
	}

	private void processCoverDependents(CalcEngine engine,
            String dependCover,
            List<Tuple> totalCoversTuple,
            List<Tuple> taxes,
            List<Tuple> customerChoiceTaxes,
            String promocode,
            TaxUtils taxUtils,
            TaxRemover taxRemover,
            List<Cover> resultCovers) {

		List<Tuple> covers = totalCoversTuple.parallelStream()
		.filter(t -> dependCover.equals(t.get("dependentCoverYn").toString()))
		.toList();
		
		if (covers.isEmpty()) {
		return;
		}
		
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- Discount Block start :---->");
		List<Discount> discounts = generateDiscounts(engine, covers, promocode);
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- Discount Block end :---->");
		
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- Loading Block start :---->");
		List<Loading> loadings = generateLoadings(engine, covers);
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- Loading Block end :---->");
		
		// ----------  NON-SUB COVERS ("N") – SAME AS BEFORE ----------
		Map<String, List<Cover>> nonSubCovers = splitCovers(engine, covers, "N");
		List<Cover> nonSubCoverList = nonSubCovers.get("N");
		
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- attachDiscountsLoadingsTaxes  Block start :---->");
		attachDiscountsLoadingsTaxes(nonSubCoverList, discounts, loadings, taxes, taxUtils);
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- attachDiscountsLoadingsTaxes  Block end :---->");
		
		// ----------  SUB COVERS ("Y") – CHILDREN → PARENT WITH SUBCOVERS ----------
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- SubCover split Block start :---->");
		
		// Step 1: build CHILD subcovers from tuples (no parents yet)
		SplitSubCoverUtil splitY = new SplitSubCoverUtil("Y",
		engine.getEffectiveDate(), engine.getPolicyEndDate());
		
		List<Cover> childSubCovers = covers.parallelStream()
		.map(splitY)
		.filter(Objects::nonNull)
		.collect(Collectors.toList());
		
		// Step 2: attach discounts / loadings / taxes to CHILDREN
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- attachDiscountsLoadingsTaxes 2 Block start :---->");
		attachDiscountsLoadingsTaxes(childSubCovers, discounts, loadings, taxes, taxUtils);
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- attachDiscountsLoadingsTaxes 2 Block end :---->");
		
		// Step 3: group children by coverId and create PARENT covers with subcovers list
		List<Cover> parentSubCovers = buildParentsWithSubcovers(childSubCovers);
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- SubCover split Block end :---->");
		
		// ---------- MERGE & CALCULATE ----------
		List<Cover> mergedCovers = new ArrayList<>();
		
		if (nonSubCoverList != null) {
		mergedCovers.addAll(nonSubCoverList);
		}
		if (parentSubCovers != null) {
		mergedCovers.addAll(parentSubCovers);
		}
		
		// Remove excluded taxes, etc.
		mergedCovers = mergedCovers.stream()
		.peek(taxRemover)
		.collect(Collectors.toList());
		
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- calc engine Block start :---->");
		
		CoverCalculator calc = new CoverCalculator();
		calc.setEngine(engine, resultCovers, commontbl, vehicles, customers, prorata,
		ratingutil, decimalFormat, drivers, customerChoiceTaxes);
		
		mergedCovers.parallelStream().forEach(calc);
		mergedCovers.removeIf(Cover::isNotsutable);
		
		resultCovers.addAll(mergedCovers);
		resultCovers.sort(Comparator.comparing(Cover::getCoverageType));
		
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
		+ " <---- calc engine Block end :---->");
}
	
	private List<Cover> buildParentsWithSubcovers(List<Cover> childSubCovers) {
	    if (childSubCovers == null || childSubCovers.isEmpty()) {
	        return new ArrayList<>();
	    }

	    // Only rows that actually have subCoverId
	    List<Cover> validChildren = childSubCovers.stream()
	            .filter(c -> c.getSubCoverId() != null && !"0".equals(c.getSubCoverId()))
	            .collect(Collectors.toList());

	    if (validChildren.isEmpty()) {
	        return new ArrayList<>();
	    }

	    Map<String, List<Cover>> byCoverId = validChildren.stream()
	            .collect(Collectors.groupingBy(Cover::getCoverId));

	    List<Cover> parents = new ArrayList<>();

	    byCoverId.forEach((coverId, children) -> {
	        // children are the actual subcovers; make sure they are treated as such
	        children.forEach(c -> c.setIsSubCover("N"));

	        // Clone first child as base for parent
	        Cover parent = (Cover) org.springframework.util.SerializationUtils.clone(children.get(0));

	        parent.setIsSubCover("Y");
	        parent.setSubcovers(children);
	        parent.setSubCoverId(null);
	        parent.setSubCoverDesc(null);
	        parent.setSubCoverName(null);
	        parent.setDiscounts(null);
	        parent.setLoadings(null);
	        parent.setTaxes(null);

	        parents.add(parent);
	    });

	    return parents;
	}



	private List<Discount> generateDiscounts(CalcEngine engine, List<Tuple> covers, String promocode) {
		SplitDiscountUtils util = new SplitDiscountUtils(engine.getEffectiveDate(), engine.getPolicyEndDate(),
				promocode);
		return covers.parallelStream().map(util).filter(Objects::nonNull)
				.peek(d -> d.setEffectiveDate(engine.getEffectiveDate())).toList();
	}

	private List<Loading> generateLoadings(CalcEngine engine, List<Tuple> covers) {
		SplitLoadingUtils util = new SplitLoadingUtils(engine.getEffectiveDate(), engine.getPolicyEndDate());
		return covers.parallelStream().map(util).filter(Objects::nonNull).toList();
	}

	private Map<String, List<Cover>> splitCovers(CalcEngine engine, List<Tuple> covers, String flag) {
	    SplitSubCoverUtil splitUtil = new SplitSubCoverUtil(
	            flag,
	            engine.getEffectiveDate(),
	            engine.getPolicyEndDate()
	    );

	    // First: same as before – map tuples → Cover, group by isSubCover
	    Map<String, List<Cover>> grouped = covers.parallelStream()
	            .map(splitUtil)
	            .filter(Objects::nonNull)
	            .collect(Collectors.groupingBy(Cover::getIsSubCover));

	    // For non-subcovers (“N”), behaviour stays exactly the same
	    if (!"Y".equalsIgnoreCase(flag)) {
	        return grouped;
	    }

	    // For subcovers (“Y”) we must build PARENT covers with subcovers list
	    List<Cover> rawSubCovers = grouped.get("Y");
	    if (rawSubCovers == null || rawSubCovers.isEmpty()) {
	        return grouped;
	    }

	    // Only those that actually have a subCoverId
	    List<Cover> usableSubCovers = rawSubCovers.stream()
	            .filter(c -> c.getSubCoverId() != null && !"0".equals(c.getSubCoverId()))
	            .toList();

	    if (usableSubCovers.isEmpty()) {
	        return grouped;
	    }

	    
	    List<Cover> distinctParents = usableSubCovers.stream()
	            .filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
	            .toList();

	    List<Cover> parentWithSubcovers = new ArrayList<>();

	    for (Cover parentCandidate : distinctParents) {

	        // All children (subcovers) for this coverId
	       List<Cover> children = usableSubCovers.stream()
	                .filter(sc -> sc.getCoverId().equals(parentCandidate.getCoverId()))
	                .toList();

	        // Children are plain subcovers, not “parent” again
	        children.forEach(c -> c.setIsSubCover("N"));

	        // Build a parent cover with subcovers list
	        Cover parent = SerializationUtils
	                .clone(parentCandidate);   // same as your old SerializationUtils.clone

	        parent.setSubcovers(children);
	        parent.setIsSubCover("Y");
	        parent.setSubCoverId(null);
	        parent.setSubCoverDesc(null);
	        parent.setSubCoverName(null);
	        parent.setDiscounts(null);
	        parent.setLoadings(null);
	        parent.setTaxes(null);

	        parentWithSubcovers.add(parent);
	    }

	    // Override the "Y" list with properly built parents
	    grouped.put("Y", parentWithSubcovers);

	    return grouped;
	}


	private void attachDiscountsLoadingsTaxes(List<Cover> covers, List<Discount> discounts, List<Loading> loadings,
			List<Tuple> taxes, TaxUtils taxUtils) {
		if (covers == null || covers.isEmpty())
			return;

		    covers.forEach(c -> {
			List<Discount> ds = discounts.stream().filter(d -> d.getDiscountforId().equals(c.getCoverId()))
					.peek(d -> d.setSubCoverId(c.getSubCoverId())).toList();
			c.setDiscounts(ds);
			
			List<Loading> ls = loadings.stream().filter(l -> l.getLoadingforId().equals(c.getCoverId()))
					.peek(l -> l.setSubCoverId(c.getSubCoverId())).toList();
			c.setLoadings(ls); 

			if (!"A".equals(c.getCoverageType()) && !"Y".equals(c.getIsTaxExcempted())) {
				List<Tax> taxList = taxes.stream().map(taxUtils).filter(Objects::nonNull).toList();
				c.setTaxes(taxList);
			}
		});
	}

	private void adjustMinimumPremium(CalcEngine engine, BigDecimal endtCount, List<Tuple> taxes, TaxUtils taxUtils,
			List<Cover> covers) {
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
		        + " <---- adjustMinimumPremium Block  start :---->");
		List<Tax> taxList = taxes.stream().map(taxUtils).filter(Objects::nonNull).toList();
		BigDecimal totalPremium = covers.stream().map(Cover::getPremiumExcluedTaxLC).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		BigDecimal diff = minimumPremium.subtract(totalPremium, MathContext.DECIMAL32);

		CreateMinimumPremium min = new CreateMinimumPremium(diff, engine, endtCount, taxList);
		Cover mini = min.create();
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
		        + " <---- adjustMinimumPremium  calculator Block  start :---->");
		CoverCalculator calc = new CoverCalculator();
		calc.setEngine(engine, covers, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat, drivers,
				taxes);
		calc.accept(mini);

		covers.add(mini);
		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
		        + " <---- adjustMinimumPremium Block  end :---->");
	}

	private BigDecimal handleEndorsement(CalcEngine engine, List<Cover> covers) {
		try {
			String endtTypeId;
			if(StringUtils.isNotBlank(engine.getEndtTypeId())) {
				endtTypeId = engine.getEndtTypeId();
			}else
			{
				endtTypeId = Optional.ofNullable(vehicles.get(0).get("endtTypeId")).map(Object::toString).orElse("");
			}
			
			if (StringUtils.isBlank(endtTypeId) || "0".equals(endtTypeId))
				return BigDecimal.ZERO;

			String requestRef = engine.getRequestReferenceNo();
			String rawTable = ratingutil.getProductIdBasedRawTable(engine);
			String baseSearch = String.format(
					"companyId:%s;productId:%s;sectionId:%s;riskId:%s;status:{E,D,RP};requestReferenceNo:%s;locationId:%s",
					engine.getInsuranceId(), engine.getProductId(), engine.getSectionId(), engine.getVehicleId(),
					requestRef, StringUtils.defaultIfBlank(engine.getLocationId(), "1"));

			SpecCriteria criteria = crservice.createCriteria(Class.forName(rawTable), baseSearch, "requestReferenceNo");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);

			BigDecimal endtCount = (result != null
			        && !result.isEmpty()
			        && result.get(0).get("endtCount") != null)
			        ? new BigDecimal(result.get(0).get("endtCount").toString())
			        : BigDecimal.ZERO;			
			loadAndRemoveCoversForEndt(engine, covers, result);

			return endtCount;
		} catch (Exception e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
	}

	private EserviceMotorDetailsSaveRes buildResponse(CalcEngine engine, List<UWReferrals> uwList,
			List<MasterReferal> masterList, List<Cover> retc, String isEndt) {
		EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
		response.setCoverList(retc);
		response.setResponse("Saved Successfully");
		response.setRequestReferenceNo(engine.getRequestReferenceNo());
		response.setVehicleId(engine.getVehicleId());
		response.setVdRefNo(engine.getVdRefNo());
		response.setCdRefNo(engine.getCdRefNo());
		response.setInsuranceId(engine.getInsuranceId());
		response.setSectionId(engine.getSectionId());
		response.setCreatedBy(engine.getCreatedBy());
		response.setProductId(engine.getProductId());
		response.setLocationId(engine.getLocationId());
		response.setMsrefno(engine.getMsrefno());
		response.setUpdateas(isEndt);
		response.setUwList(uwList);
		response.setReferals(masterList);
		response.setCoverId(engine.getCoverId());
		return response;
	}

	/*
	 * 
	 * @Autowired private EndtTypeMasterRepository endtTypeRepo;
	 * 
	 */
	private void loadAndRemoveCoversForEndt(CalcEngine engine, List<Cover> retc, List<Tuple> result) {
		try {

			if (!result.isEmpty()) {

				// String endtPrevPolicyNo=result.get(0).get("endtPrevPolicyNo").toString();
				String endtPrevQuoteNo = result.get(0).get("endtPrevQuoteNo").toString();

				String endtDesc = result.get(0).get("endorsementTypeDesc").toString();
				String endtTypeId = result.get(0).get("endorsementType").toString();
				BigDecimal endtCount = new BigDecimal(result.get(0).get("endtCount").toString());
				Date date = null;
				try {
					date = (Date) result.get(0).get("policyStartDate");
					currentPolicyPeriod = findNoOfDaysInDate(date, engine.getPolicyEndDate());
				} catch (Exception e) {
					e.printStackTrace();
				}

				String originalPolicyNo = result.get(0).get("originalPolicyNo").toString();
				List<PolicyCoverDataEndt> oldPolicyData = null;
				if (!"0".equals(engine.getCoverId())) {
					oldPolicyData = policyCoverEndtRepo
							.findByPolicyNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndCoverIdAndLocationIdOrderByCoverIdAsc(
									originalPolicyNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()),
									Integer.parseInt(engine.getCoverId()) , Integer.parseInt(engine.getLocationId()));
				} else {
					oldPolicyData = policyCoverEndtRepo
							.findByPolicyNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdOrderByCoverIdAsc(
									originalPolicyNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()));
				}
				EndtTypeMaster endtmaster = ratingutil.getEndtMasterData(engine.getInsuranceId(), engine.getProductId(),
						endtTypeId);

				retc.stream().forEach(i -> i.setEndtCount(endtCount));
				// find Prev Quote Data
				List<PolicyCoverData> oldPolicyCovers = null;
				if (!"0".equals(engine.getCoverId())) {
					oldPolicyCovers = coverDataRepo
							.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusAndCoverIdAndLocationIdOrderByCoverIdAsc(
									endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()),
									"Y", Integer.parseInt(engine.getCoverId()),Integer.parseInt(engine.getLocationId()));
				} else {
					oldPolicyCovers = coverDataRepo
							.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
									endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()),
									"Y");
				}
				List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
				TaxUtils tzx = new TaxUtils(endtCount, "");
				TaxUtils tzxEndt = new TaxUtils(endtCount, endtTypeId);
				List<Tax> taxey = taxes.stream().map(tzx).filter(t -> t != null).collect(Collectors.toList());

				List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
				TaxRemover taxRemov = new TaxRemover(excludedTaxes, null);

				List<Tax> tzxeyEndt = taxes.stream().map(tzxEndt).filter(t -> t != null).collect(Collectors.toList());

				// CoverFromPolicy
				
				List<PolicyCoverData> basecovers = oldPolicyCovers.stream()
						.filter(d -> !("T".equals(d.getCoverageType()) || "D".equals(d.getCoverageType())
								|| "L".equals(d.getCoverageType()) || "E".equals(d.getCoverageType())
								|| "P".equals(d.getCoverageType())
								|| d.getCoverId().compareTo(Integer.valueOf("945")) == 0))
						.collect(Collectors.toList());

				List<PolicyCoverData> countPolicy = oldPolicyCovers.stream()
						.filter(d -> (d.getCoverId().compareTo(Integer.valueOf("945")) == 0))
						.collect(Collectors.toList());
				List<Cover> countCover = retc.stream().filter(t -> "945".equals(t.getCoverId()))
						.collect(Collectors.toList());
				if (countCover.size() > 0 && countPolicy.size() > 0 && countCover.get(0).getPremiumExcluedTaxLC()
						.compareTo(countPolicy.get(0).getPremiumExcludedTaxLc()) == 0) {

					retc.removeIf(t -> "945".equals(t.getCoverId()));
				}
				Long days=0L;
				if((basecovers==null || basecovers.isEmpty()) && ("851".equalsIgnoreCase(endtTypeId) || "854".equalsIgnoreCase(endtTypeId) ||  "846".equalsIgnoreCase(endtTypeId)))
				{
					Set<Integer> baseCoverIds=null;
					boolean contains=false;
					
					List<PolicyCoverData> allSection = new ArrayList<PolicyCoverData>();
					
					List<Endorsement> endorsements = new ArrayList<Endorsement>();

					if ("854".equalsIgnoreCase(endtTypeId)) {
						allSection = coverDataRepo
								.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndStatusOrderByCoverIdAsc(
										endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()),
										engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
										 "Y");
					
					}
					else if("846".equalsIgnoreCase(endtTypeId) || "890".equalsIgnoreCase(endtTypeId)){
						allSection = coverDataRepo.findByQuoteNoAndCompanyIdAndProductIdAndStatusOrderByCoverIdAsc(
										endtPrevQuoteNo,engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
										 "Y");
						
					}
					else {
						allSection = coverDataRepo
								.findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
										endtPrevQuoteNo, 
										engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
										Integer.parseInt(engine.getSectionId()), "Y");
						baseCoverIds = basecovers.stream()
						        .map(PolicyCoverData::getCoverId)
						        .collect(Collectors.toSet());
						try {
			                 contains = baseCoverIds.contains(Integer.valueOf(engine.getCoverId()));
			            } catch (NumberFormatException e) {	
			              e.printStackTrace();
			            }
					}
					if (!"0".equals(engine.getCoverId()))
					{
						
					isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate()).compareTo(getZeroTimeDate(allSection.get(0).getCoverPeriodTo())) != 0 ? true : false;
					oldPolicyPeriod = findNoOfDaysInDate(date, allSection.get(0).getCoverPeriodTo());
					AddCreateEndorsment createEndt = new AddCreateEndorsment(endtmaster, endtCount, tzxeyEndt, retc,
							engine.getPolicyEndDate(), engine.getCoverId(),days,"N","A",engine);
					Endorsement currentEndt = createEndt.create();

					endorsements.add(currentEndt);
					
					retc.stream()
				    .filter(t -> t.getCoverId().equalsIgnoreCase(currentEndt.getEndorsementforId().toString()))
				    .forEach(t -> {
				        t.setEndorsements(endorsements);
				        t.setUserOpt("N");
				    });
					}
					else
					{
						List<Cover> ob = retc.stream()
						.filter(t -> !("T".equals(t.getCoverageType()) || "D".equals(t.getCoverageType())
								|| "L".equals(t.getCoverageType()) || "E".equals(t.getCoverageType())
								|| "P".equals(t.getCoverageType())
								))
						.collect(Collectors.toList());
						for(Cover b : ob) 
						{
							isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate())
									.compareTo(getZeroTimeDate(allSection.get(0).getCoverPeriodTo())) != 0 ? true : false;
							oldPolicyPeriod = findNoOfDaysInDate(date, allSection.get(0).getCoverPeriodTo());
							prevPolicyEndDate = allSection.get(0).getCoverPeriodTo();
							AddCreateEndorsment createEndt = new AddCreateEndorsment(endtmaster, endtCount, tzxeyEndt, retc,
									engine.getPolicyEndDate(), b.getCoverId(),days,"N","A",engine);
							Endorsement currentEndt = createEndt.create();

							endorsements.add(currentEndt);
							retc.stream()
						    .filter(t -> t.getCoverId().equalsIgnoreCase(currentEndt.getEndorsementforId().toString()))
						    .forEach(t -> {
						        t.setEndorsements(endorsements);
						        t.setUserOpt("N");
						    });
						}
						
						
					}
				}
				else if("0".equals(engine.getCoverId()) && "851".equalsIgnoreCase(endtTypeId))
				{
					Set<Integer> baseCoverIds = basecovers.stream()
					        .map(PolicyCoverData::getCoverId)
					        .collect(Collectors.toSet());
					days = oldPolicyData.stream().filter(o -> o.getDiscLoadId() == 851)
							.map(PolicyCoverDataEndt::getNoOfDays).filter(Objects::nonNull).findFirst().orElse(null);
					List<PolicyCoverData> allSection = new ArrayList<PolicyCoverData>();
					
					
					allSection = coverDataRepo
							.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
									endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()),
									engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
									Integer.parseInt(engine.getSectionId()), "Y");
					List<Cover> ob = retc.stream()
					        .filter(t -> !("T".equals(t.getCoverageType()) 
					                || "D".equals(t.getCoverageType())
					                || "L".equals(t.getCoverageType()) 
					                || "E".equals(t.getCoverageType())
					                || "P".equals(t.getCoverageType()))).collect(Collectors.toList());
					for(Cover b : ob) 
					{ 
						boolean contains=false;
					
					try {
						contains = baseCoverIds.contains(Integer.parseInt(b.coverId));
					} catch (NumberFormatException e) {
						contains = false;
					}

					String key = contains ? "Y" : "N";
						List<Endorsement> endorsements = new ArrayList<Endorsement>();
						isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate())
								.compareTo(getZeroTimeDate(allSection.get(0).getCoverPeriodTo())) != 0 ? true : false;
						oldPolicyPeriod = findNoOfDaysInDate(date, allSection.get(0).getCoverPeriodTo());
						prevPolicyEndDate = allSection.get(0).getCoverPeriodTo();
						AddCreateEndorsment createEndt1 = new AddCreateEndorsment(endtmaster, endtCount, tzxeyEndt, retc,
								engine.getPolicyEndDate(), b.getCoverId(),days,key,"A",engine);
						Endorsement currentEndt1 = createEndt1.create();

						endorsements.add(currentEndt1);
						retc.stream()
					    .filter(t -> t.getCoverId().equalsIgnoreCase(currentEndt1.getEndorsementforId().toString()))
					    .forEach(t -> {
					        t.setEndorsements(endorsements);
					        t.setUserOpt(key);
					    });
				}
				}
				else if("890".equalsIgnoreCase(endtTypeId) )
				{
				
					boolean contains=false;
					Set<Integer> baseCoverIds = basecovers.stream()
					        .map(PolicyCoverData::getCoverId)
					        .collect(Collectors.toSet());
					contains = baseCoverIds.contains(Integer.parseInt(engine.getCoverId()));
					
					String key = contains ? "Y" : "N";

					List<PolicyCoverData> allSection = new ArrayList<PolicyCoverData>();

					List<Endorsement> endorsements = new ArrayList<Endorsement>();

					allSection = coverDataRepo.findByQuoteNoAndCompanyIdAndProductIdAndStatusOrderByCoverIdAsc(
							endtPrevQuoteNo, engine.getInsuranceId(), Integer.parseInt(engine.getProductId()), "Y");

					if (!"0".equals(engine.getCoverId())) {

						isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate())
								.compareTo(getZeroTimeDate(allSection.get(0).getCoverPeriodTo())) != 0 ? true : false;
						oldPolicyPeriod = findNoOfDaysInDate(date, allSection.get(0).getCoverPeriodTo());
						AddCreateEndorsment createEndt = new AddCreateEndorsment(endtmaster, endtCount, tzxeyEndt, retc,
								engine.getPolicyEndDate(), engine.getCoverId(), days, key, "A",engine);
						Endorsement currentEndt = createEndt.create();

						endorsements.add(currentEndt);

						retc.stream().filter(
								t -> t.getCoverId().equalsIgnoreCase(currentEndt.getEndorsementforId().toString()))
								.forEach(t -> {
									t.setEndorsements(endorsements);
									t.setUserOpt("N");
								});
					}

				}
				else{				
					for (PolicyCoverData d : basecovers) {
					List<Cover> operatedList = new ArrayList<Cover>();
					
					isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate())
							.compareTo(getZeroTimeDate(d.getCoverPeriodTo())) != 0 ? true : false;
					
					String opp = d.getNoOfDays() != null ? d.getNoOfDays().toString() : "";
					Date coverPeriodFrom = d.getCoverPeriodFrom();
					Date coverPeriodTo = d.getCoverPeriodTo();
					oldPolicyPeriod = findNoOfDaysInDate(date, coverPeriodTo);
//						oldPolicyPeriod=StringUtils.isNotBlank(opp)?opp:StringUtils.isNotBlank(oldPolicyPeriod)?oldPolicyPeriod:"";
					prevPolicyEndDate = d.getCoverPeriodTo();

					DiscountFromPolicy discountUtil = new DiscountFromPolicy();
					List<Discount> discounts = oldPolicyCovers.stream()
							.filter(r -> Objects.equals(d.getCoverId(), r.getCoverId())).map(discountUtil)
							.filter(dx -> dx != null).collect(Collectors.toList());

					LoadingFromPolicy loadingUtil = new LoadingFromPolicy();
					List<Loading> loadings = oldPolicyCovers.stream()
							.filter(r -> Objects.equals(d.getCoverId(), r.getCoverId())).map(loadingUtil)
							.filter(dx -> dx != null).collect(Collectors.toList());

					List<Endorsement> endorsements = new ArrayList<Endorsement>();
					List<PolicyCoverDataEndt> coverData = oldPolicyData.stream()
							.filter(i -> i.getCoverId().compareTo(d.getCoverId()) == 0).collect(Collectors.toList());

					CreateEndorsment createEndt = new CreateEndorsment(endtmaster, endtCount, tzxeyEndt, coverData, d,
							engine.getPolicyEndDate(),engine);
					Endorsement currentEndt = createEndt.create();
					endorsements.add(currentEndt);
					String endtProRata="",endtProRataDec="",siorpre="" ;
					List<Cover> collect = retc.stream().filter(t -> t.getCoverId().equalsIgnoreCase(d.getCoverId().toString())).collect(Collectors.toList());
					if(collect!=null && !collect.isEmpty())
					{
						endtProRata=collect.get(0).getEndtProRataYn();
						endtProRataDec=collect.get(0).getEndtProRataDesc();
						siorpre=collect.get(0).getDependentCoveSIorPI();
					}
					CoverFromPolicy coverUtil = new CoverFromPolicy("",endtProRata,endtProRataDec,siorpre );
					List<Cover> covers = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
							.map(coverUtil).filter(dx -> dx != null).collect(Collectors.toList());
					/*
					 * List<Cover> oldTax = covers.stream().filter(c ->
					 * "T".equals(c.getCoverageType())).collect(Collectors.toList());
					 * covers.removeAll(oldTax);
					 */

					if(!("0".equals(engine.getCoverId())))
					{
						
						List<PolicyCoverData> vechilIdlist = coverDataRepo.findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverIdAndStatusAndCoverageTypeAndLocationIdOrderByCoverIdAsc(
								endtPrevQuoteNo, engine.getInsuranceId(),
								Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()),
								Integer.parseInt(engine.getCoverId()),"Y","B",Integer.parseInt(engine.getLocationId()));
						if((!vechilIdlist.isEmpty())&& vechilIdlist.size()>1)
						{
							covers.forEach(c -> {
								c.setIsselected("N");
							});
							endorsements.forEach(c -> c.setIsselected("N"));
						}
					}
					covers.stream()
							.filter(c -> (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")))
							.forEach(c -> c.setTaxes(taxey));
					covers.forEach(c -> c.setEndtCount(endtCount));
					covers.forEach(c -> c.setEndorsements(endorsements));// Existing Endorsement
					covers.forEach(c -> c.setDiscounts(discounts));
					covers.forEach(c -> c.setLoadings(loadings));
					covers.forEach(c -> c.setPolicyEndDate(engine.getPolicyEndDate()));
					covers.stream().forEach(taxRemov);
					if("851".equalsIgnoreCase(endtTypeId) || "854".equalsIgnoreCase(endtTypeId)){
						engine.setCoverModification("N");
					}
					retc.stream().filter(r -> d.getCoverId() == Integer.parseInt(r.getCoverId())).forEach(item -> {
						operatedList.add(item);
						covers.stream().forEach(c -> {
							c.setCoverageLimit(item.getCoverageLimit());
							c.setEffectiveDate(engine.getEffectiveDate());
//							c.setPolicyEndDate(engine.getPolicyEndDate());
						});
					});
					retc.removeAll(operatedList);
					retc.addAll(covers);
				}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Transactional
	public EserviceMotorDetailsSaveRes endorsementCalculator(CalcEngine request, BigDecimal endtCount,
			String endtTypeId, Boolean isPolicyPeriod) {
		try {
			
			List<Cover> retc = new ArrayList<Cover>();

			List<String> dependedcovers = new ArrayList<String>();
			EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();

			dependedcovers.add("N");
			dependedcovers.add("Y");
			List<FactorRateRequestDetails> factors;

			if (!"0".equals(request.getCoverId())) {
				factors = repository
						.findByRequestReferenceNoAndLocationIdAndVehicleIdAndProductIdAndSectionIdAndCoverId(
								request.getRequestReferenceNo(), Integer.valueOf(request.getLocationId()),
								Integer.valueOf(request.getVehicleId()), Integer.valueOf(request.getProductId()),
								Integer.valueOf(request.getSectionId()), Integer.valueOf(request.getCoverId()));
			} else {
				factors = repository.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
						request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()),
						Integer.valueOf(request.getProductId()), Integer.valueOf(request.getSectionId()));
			}

			// TaxFromFactor tzx=new TaxFromFactor();
			List<Tuple> taxes = ratingutil.LoadTax(request, NORMAL_TAX_LIST);
			List<Tuple> taxesEndt = ratingutil.LoadTax(request, ENDT_TAX_LIST);

			List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(request, NORMAL_TAX_LIST);
			List<Tuple> excludedTaxesEndt = ratingutil.LoadExcludedTax(request, ENDT_TAX_LIST);

			TaxRemover taxRemov = new TaxRemover(excludedTaxes, excludedTaxesEndt);
			TaxUtils tzx = new TaxUtils(endtCount, "");
			TaxUtils tzxsa = new TaxUtils(endtCount, endtTypeId);

			for (String dependcover : dependedcovers) {
				List<Cover> totalcovers = new ArrayList<Cover>();
				List<FactorRateRequestDetails> covers = factors.stream()
						.filter(f -> dependcover.equals(f.getDependentCoverYn())).collect(Collectors.toList());

				DiscountFromFactor discountUtil = new DiscountFromFactor();
				List<Discount> discounts = covers.stream().map(discountUtil).filter(d -> d != null)
						.collect(Collectors.toList());
				LoadingFromFactor loadingtuils = new LoadingFromFactor();
				List<Loading> loadings = covers.stream().map(loadingtuils).filter(d -> d != null)
						.collect(Collectors.toList());
				EndtFromFactor endtUtil = new EndtFromFactor();
				List<Endorsement> endorsements = covers.stream().map(endtUtil).filter(d -> d != null)
						.collect(Collectors.toList());

				CoverFromFactor splitsub = new CoverFromFactor("N");
				Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!nonSubcovers.isEmpty()) {
					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(ds);
							// c.setTaxes(taxey);
						}
					}
					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setLoadings(ds);
							// c.setTaxes(taxey);
						}
					}

					if (!endorsements.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Endorsement> ds = endorsements.stream()
									.filter(d -> d.getEndorsementforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());

							// TaxFromFactor endttaxUtil = new TaxFromFactor();
							if (ds != null && ds.size() > 0) {

								for (Endorsement e : ds) {

									// only for endrose we cannt use cover objs tax cover wontbe list.
									/*
									 * List<Tax> txx = factors.stream() .filter(r -> (r.getDiscLoadId() ==
									 * Integer.parseInt(e.getEndorsementId()) && r.getCoverId() ==
									 * Integer.parseInt(e.getEndorsementforId()) && r.getEndtCount().intValue() ==
									 * e.getEndtCount().intValue())) .map(endttaxUtil).filter(dx -> (dx != null &&
									 * !"0".equals(dx.getTaxId()))) .collect(Collectors.toList());
									 */
									List<Tax> taxey = taxesEndt.stream().map(tzxsa).filter(d -> d != null)
											.collect(Collectors.toList());
									e.setTaxes(taxey);
								}
							}

							c.setEndorsements(ds);
							// c.setTaxes(taxey);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
							}
						}
					}
				}

				splitsub = new CoverFromFactor("Y");
				Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!subcovers.isEmpty()) {
					List<Cover> noncovers = subcovers.get("Y"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							// List<Tax> taxez =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(dss);
							// c.setTaxes(taxez);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							c.setLoadings(dss);
						}
					}

					if (!endorsements.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Endorsement> ds = endorsements.stream()
									.filter(d -> d.getEndorsementforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							List<Endorsement> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());

							// TaxFromFactor endttaxUtil = new TaxFromFactor();
							if (dss != null && dss.size() > 0) {

								for (Endorsement e : dss) {
									/*
									 * List<Tax> txx = covers.stream() .filter(r -> (r.getDiscLoadId() ==
									 * Integer.parseInt(e.getEndorsementId()) && r.getCoverId() ==
									 * Integer.parseInt(e.getEndorsementforId()) && r.getEndtCount().intValue() ==
									 * e.getEndtCount().intValue())) .map(endttaxUtil).filter(dx -> dx !=
									 * null).collect(Collectors.toList());
									 */
									List<Tax> taxey = taxesEndt.stream().map(tzxsa).filter(d -> d != null)
											.collect(Collectors.toList());
									e.setTaxes(taxey);
								}
							}

							c.setEndorsements(dss);
							// c.setTaxes(taxey);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
							}
						}
					}

					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
							.collect(Collectors.toList());
					List<Cover> subcov = new ArrayList<Cover>();
					for (Cover cover : d) {
						List<Cover> subcover = noncovers.stream()
								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
						subcover.stream().forEach(s -> s.setIsSubCover("N"));
						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
						Cover newcover = SerializationUtils.clone(cover);
						newcover.setSubcovers(subcover);
						newcover.setIsSubCover("Y");
						newcover.setSubCoverId(null);
						newcover.setSubCoverDesc(null);
						newcover.setSubCoverName(null);
						newcover.setDiscounts(null);
						newcover.setLoadings(null);
						newcover.setTaxes(null);
						subcov.add(newcover);
					}
					subcovers.put("Y", subcov);
				}

				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
					totalcovers.addAll(nonSubcovers.get("N"));
				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
					totalcovers = nonSubcovers.get("N");
				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
				}
				
				totalcovers.stream().forEach(taxRemov);
				EndtCoverCalculator calc = new EndtCoverCalculator(isPolicyPeriod, oldPolicyPeriod, currentPolicyPeriod,
						prevPolicyEndDate);
				List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(request);

				if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
						|| (customers == null || customers.size() == 0)) {
					loadOnetimetable(request);
				}
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil,
						request.getEffectiveDate(), decimalFormat, drivers, customerChoiceTaxes);

				totalcovers.stream().filter(t -> "Y".equals(t.getStatus())).forEach(calc);
				// remove error records
				totalcovers.removeIf(ll -> (ll.isNotsutable()));
				retc.addAll(totalcovers);
				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
				retc.sort(comp);
				response.setCoverList(retc);
				response.setResponse("Saved Successfully");
				response.setRequestReferenceNo(request.getRequestReferenceNo());
				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
				response.setVehicleId(request.getVehicleId());
				response.setVdRefNo(request.getVdRefNo());
				response.setCdRefNo(request.getCdRefNo());
				response.setInsuranceId(request.getInsuranceId());
				response.setSectionId(request.getSectionId());
				response.setCreatedBy(request.getCreatedBy());
				response.setProductId(request.getProductId());

				response.setLocationId(request.getLocationId());
				response.setMsrefno(request.getMsrefno());
				response.setUpdateas("admin");
				response.setCoverId(request.getCoverId());
				// response.setUwList(referr);

				fservice.saveFactorRateRequestDetails(response);

			}

			try {

				// Update Premium,referral

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void loadOnetimetable(CalcEngine engine) {
		/// One time table record
		try {
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- loadOnetimetable Block  start :---->");
			SpecCriteria criteria = null;
			/*
			 * MsVehicleDetails findByVdRefno =
			 * msvech.findByVdRefno(Long.parseLong(engine.getVdRefNo()));
			 * System.out.println("findByVdRefno"+findByVdRefno.getChassisNumber());
			 */

			List<Tuple> product = ratingutil.collectProductType(engine);
			String oneProduct = product.get(0).get("motorYn") == null ? "M" : product.get(0).get("motorYn").toString();

			/*
			 * vehicles=null; while(vehicles==null) {
			 * 
			 * 
			 * if(oneProduct.equalsIgnoreCase("M")){ String
			 * search="vdRefno:"+engine.getVdRefNo()+";vehicleId:"+engine.getVehicleId();
			 * criteria = crservice.createCriteria(MsVehicleDetails.class, search,
			 * "vdRefno"); vehicles = crservice.getResult(criteria, 0, 50); }else
			 * if(oneProduct.equalsIgnoreCase("H")){ String
			 * search="vdRefno:"+engine.getVdRefNo()+";humanId:"+engine.getVehicleId();
			 * criteria = crservice.createCriteria(MsHumanDetails.class, search, "vdRefno");
			 * vehicles = crservice.getResult(criteria, 0, 50); }else
			 * if(oneProduct.equalsIgnoreCase("A")){ String
			 * search="vdRefno:"+engine.getVdRefNo()+";locationId:"+engine.getVehicleId();
			 * criteria = crservice.createCriteria(MsAssetDetails.class, search, "vdRefno");
			 * vehicles = crservice.getResult(criteria, 0, 50); }
			 * 
			 * System.out.println("Vehicle record "+engine.getVdRefNo()+", vehicles is "+((
			 * vehicles==null || vehicles.isEmpty())?"empty":"Not an empty")); }
			 * 
			 */

			String search = "msRefno:" + engine.getMsrefno() + ";";

			// if(result==null) {
			criteria = crservice.createCriteria(MsCommonDetails.class, search, "msRefno");
			commontbl = crservice.getResult(criteria, 0, 50);
			// }
			if (commontbl != null && commontbl.size() > 0) {
				Tuple tuple = commontbl.get(0);
				String vdRefno = tuple.get("vdRefno").toString();
				String cdRefno = tuple.get("cdRefno").toString();
				vehicles = null;
				int counter = 0;
				while (vehicles == null || vehicles.size() == 0 && counter < 6) {

					if (oneProduct.equals("M")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";vehicleId:" + engine.getVehicleId()
								+ ";locationId:"
								+ (StringUtils.isBlank(engine.getLocationId()) ? "1" : engine.getLocationId());
						criteria = crservice.createCriteria(MsVehicleDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);

						if (StringUtils.isNotBlank(engine.getDdRefno()) && !"0".equals(engine.getDdRefno())) {
							search = "ddRefno:" + engine.getDdRefno() + ";riskId:" + engine.getVehicleId()
									+ ";driverId:1;locationId:"
									+ (StringUtils.isBlank(engine.getLocationId()) ? "1" : engine.getLocationId());
							criteria = crservice.createCriteria(MsDriverDetails.class, search, "ddRefno");
							drivers = crservice.getResult(criteria, 0, 50);
						}

					} else if (oneProduct.equals("H")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";humanId:" + engine.getVehicleId() + ";locationId:"
								+ (StringUtils.isBlank(engine.getLocationId()) ? "1" : engine.getLocationId());
						criteria = crservice.createCriteria(MsHumanDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					} else if (oneProduct.equalsIgnoreCase("A")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";riskId:" + engine.getVehicleId() + ";locationId:"
								+ (StringUtils.isBlank(engine.getLocationId()) ? "1" : engine.getLocationId());
						criteria = crservice.createCriteria(MsAssetDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					} else if (oneProduct.equalsIgnoreCase("L")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";riskId:" + engine.getVehicleId() + ";locationId:"
								+ (StringUtils.isBlank(engine.getLocationId()) ? "1" : engine.getLocationId());
						criteria = crservice.createCriteria(MsLifeDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					}

					counter++;

					System.out.println("Vehicle record " + vdRefno + ", vehicles is "
							+ ((vehicles == null || vehicles.isEmpty()) ? "empty" : "Not an empty"));
				}

				// if(customers==null) {
				search = "cdRefno:" + cdRefno + ";";
				criteria = crservice.createCriteria(MsCustomerDetails.class, search, "cdRefno");
				customers = crservice.getResult(criteria, 0, 50);
				// }

				if (vehicles != null && vehicles.size() > 0) {
					String periodOfInsurance = (vehicles.get(0).get("periodOfInsurance") == null ? "365"
							: vehicles.get(0).get("periodOfInsurance").toString());
					String policyTypeId = (vehicles.get(0).get("insuranceClass") == null ? "99999"
							: vehicles.get(0).get("insuranceClass").toString());

					String coverId = (vehicles.get(0).get("coverId") == null ? "0"
							: vehicles.get(0).get("coverId").toString());
					engine.setCoverId(coverId);

					prorata = ratingutil.loadProRataData(engine, periodOfInsurance, policyTypeId);

					String currencyId = vehicles.get(0).get("currency") == null ? "TTT"
							: vehicles.get(0).get("currency").toString();
					String decimalDigits = ratingutil.currencyDecimalFormat(engine.getInsuranceId(), currencyId);
					String stringFormat = "%0" + decimalDigits + "d";
					String decimalLength = decimalDigits.equals("0") ? "" : String.format(stringFormat, 0L);
					String pattern = StringUtils.isBlank(decimalLength) ? "#####0" : "#####0." + decimalLength;
					decimalFormat = new DecimalFormat(pattern);

					minimumPremium = product.get(0).get("minPremium") == null ? BigDecimal.ZERO
							: new BigDecimal(product.get(0).get("minPremium").toString());

				}

			}
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- loadOnetimetable Block  end :---->");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public  EserviceMotorDetailsSaveRes referalCalculator(CalcEngine request, String token) {
		try {
			List<UWReferrals> referr = referal.underwriterReferral(request);

			List<MasterReferal> masterreferral = null;
			try {
				masterreferral = referal.masterreferral(request, token);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			List<Cover> retc = new ArrayList<Cover>();

			loadOnetimetable(request);
			
			
			if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
					|| (customers == null || customers.size() == 0)) {
				System.out.println("::: Exception :: ");
				System.out.println("commontbl size: " + (commontbl == null ? "NULL" : commontbl.size()));
				System.out.println("vehicles size: " + (vehicles == null ? "NULL" : vehicles.size()));
				System.out.println("customers size: " + (customers == null ? "NULL" : customers.size()));
				throw new Exception();

				/*
				 * throw
				 * CoverException.builder().message("Exception :: onetime table not inserted")
				 * .isError(true).build();
				 */
			}
			List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(request);
			List<String> dependedcovers = new ArrayList<String>();

			dependedcovers.add("N");
			dependedcovers.add("Y");

			List<FactorRateRequestDetails> factors = repository
					.findByRequestReferenceNoAndLocationIdAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
							request.getRequestReferenceNo(), Integer.valueOf(request.getLocationId()),
							Integer.valueOf(request.getVehicleId()), Integer.valueOf(request.getProductId()),
							Integer.valueOf(request.getSectionId()));

			/*
			 * BigDecimal rate = factors.get(0).getRate();
			 * System.out.println("=====================================================" +
			 * rate + "===================================="); BigDecimal rate1 =
			 * factors.get(1).getRate();
			 * System.out.println("=====================================================" +
			 * rate1 + "====================================");
			 */
			// TaxFromFactor tzx=new TaxFromFactor();
			/*
			 * List<Tuple> taxes = ratingutil.LoadTax(request,NORMAL_TAX_LIST); TaxUtils tzx
			 * = new TaxUtils(BigDecimal.ZERO ,"");
			 */
			TaxFromFactor tzx = new TaxFromFactor();
			for (String dependcover : dependedcovers) {
				List<Cover> totalcovers = new ArrayList<Cover>();
				List<FactorRateRequestDetails> covers = factors.stream()
						.filter(f -> dependcover.equals(f.getDependentCoverYn())).collect(Collectors.toList());

				DiscountFromFactor discountUtil = new DiscountFromFactor();
				List<Discount> discounts = covers.stream().map(discountUtil).filter(d -> d != null)
						.collect(Collectors.toList());
				LoadingFromFactor loadingtuils = new LoadingFromFactor();
				List<Loading> loadings = covers.stream().map(loadingtuils).filter(d -> d != null)
						.collect(Collectors.toList());

				CoverFromFactor splitsub = new CoverFromFactor("N");
				Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!nonSubcovers.isEmpty()) {
					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(ds);
							// c.setTaxes(taxey);
						}
					}
					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setLoadings(ds);
							// c.setTaxes(taxey);
						}
					}
					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = factors.stream()
										.filter(d -> (d.getCoverId().toString().equals(c.getCoverId())
												&& d.getCoverageType().equalsIgnoreCase("T")))
										.map(tzx).collect(Collectors.toList());
								c.setTaxes(taxey);
							}
						}
					}
				}

				splitsub = new CoverFromFactor("Y");
				Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!subcovers.isEmpty()) {
					List<Cover> noncovers = subcovers.get("Y"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							// List<Tax> taxez =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(dss);
							// c.setTaxes(taxez);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							c.setLoadings(dss);
						}
					}
					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								/*
								 * List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
								 * .collect(Collectors.toList()); c.setTaxes(taxey);
								 */

								List<Tax> taxey = factors.stream()
									    .filter(d -> (d.getCoverId().toString().equals(c.getCoverId())
									            && d.getCoverageType().equalsIgnoreCase("T")
									            && d.getSubCoverId().toString().equals(
									                (c.getSubCoverId() == null || "0".equals(c.getSubCoverId()))
									                ? "0" : c.getSubCoverId())
									    ))
									    .map(tzx).collect(Collectors.toList());
								c.setTaxes(taxey);

							}
						}
					}

					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
							.collect(Collectors.toList());
					List<Cover> subcov = new ArrayList<Cover>();
					for (Cover cover : d) {
						List<Cover> subcover = noncovers.stream()
								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
						subcover.stream().forEach(s -> s.setIsSubCover("N"));
						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
						Cover newcover = SerializationUtils.clone(cover);
						newcover.setSubcovers(subcover);
						newcover.setIsSubCover("Y");
						newcover.setSubCoverId(null);
						newcover.setSubCoverDesc(null);
						newcover.setSubCoverName(null);
						newcover.setDiscounts(null);
						newcover.setLoadings(null);
						newcover.setTaxes(null);
						subcov.add(newcover);
					}
					subcovers.put("Y", subcov);
				}

				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
					totalcovers.addAll(nonSubcovers.get("N"));
				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
					totalcovers = nonSubcovers.get("N");
				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
				}

				// CoverCalculator calc=new CoverCalculator();

				AdminCoverCalculator calc = new AdminCoverCalculator();
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
						drivers, customerChoiceTaxes);

				totalcovers.stream().forEach(calc);
				// remove error records
				totalcovers.removeIf(ll -> (ll.isNotsutable()));
				retc.addAll(totalcovers);
				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
				retc.sort(comp);
			}
			// if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
			BigDecimal totalPremium = retc.stream()
					.filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId())
							&& x.getPremiumExcluedTaxLC() != null))
					.map(x -> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO, BigDecimal::add);
			if (totalPremium.compareTo(minimumPremium) < 0) {

				List<Tuple> taxes = ratingutil.LoadTax(request, NORMAL_TAX_LIST);
				TaxUtils tzxx = new TaxUtils(BigDecimal.ZERO, "");
				List<Tax> taxey = taxes.stream().map(tzxx).filter(d -> d != null).collect(Collectors.toList());
				BigDecimal difference = minimumPremium.subtract(totalPremium, MathContext.DECIMAL32);
				CreateMinimumPremium min = new CreateMinimumPremium(difference, request, factors.get(0).getEndtCount(),
						taxey);
				Cover mini = min.create();
				List<Cover> minies = new ArrayList<Cover>(1);
				minies.add(mini);
				CoverCalculator calc = new CoverCalculator();
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
						drivers, customerChoiceTaxes);
				minies.stream().forEach(calc);
				retc.add(mini);

			} else {
				retc.removeIf(t -> "945".equals(t.getCoverId()));// .stream().filter(t-> "945".equals(t.getCoverId()).de
			}
			try {
				EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
				response.setCoverList(retc);
				response.setResponse("Saved Successfully");
				response.setRequestReferenceNo(request.getRequestReferenceNo());
				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
				response.setVehicleId(request.getVehicleId());
				response.setVdRefNo(request.getVdRefNo());
				response.setCdRefNo(request.getCdRefNo());
				response.setInsuranceId(request.getInsuranceId());
				response.setSectionId(request.getSectionId());
				response.setCreatedBy(request.getCreatedBy());
				response.setProductId(request.getProductId());
				response.setMsrefno(request.getMsrefno());
				response.setLocationId(request.getLocationId());
				response.setUpdateas("admin");
				response.setUwList(referr);
				response.setReferals(masterreferral);
				// response.setUwList(referr);
				response.setCoverId(request.getCoverId());
				fservice.saveFactorRateRequestDetails(response);

				// Update Premium,referral

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Autowired
	private MotorDataDetailsRepository motorRepo;
	@Autowired
	private SectionDataDetailsRepository sectionRepo;

	@Override
	public List<DebitAndCredit> commissionCalc(CalcCommission request) {
		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
		String policyNo = "";
		try {

			resList = getOverAllcommissionCalc(request);
			// resList = getRiskWisecommissionCalc( )

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

	public List<DebitAndCredit> getOverAllcommissionCalc(CalcCommission request) {
		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
		try {
			
//			ViewQuoteReq q = new ViewQuoteReq();
//			q.setQuoteNo(request.getQuoteno());
			//ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
			HomePositionMaster hpm = homeRepo.findByQuoteNo(request.getQuoteno()); // moved to top

	        CompanyProductMaster product = getCompanyProductMasterDropdown(
	                hpm.getCompanyId(), hpm.getProductId().toString());
	        String endttypeid = hpm.getEndtTypeId();
	        String emiYn = hpm.getEmiYn();
	        String instalment = hpm.getNoOfInstallment(); // substitute — confirm this is correct
	        List<BranchMaster> branchCode = ratingutil.collectBranchMaster(hpm.getCompanyId(), hpm.getBranchCode());


			// Not endt
			if (StringUtils.isBlank(endttypeid) && (emiYn.equalsIgnoreCase("N") || instalment.equalsIgnoreCase("0"))) {
				List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
				List<ProductSectionMaster> coreappcode = ratingutil.collectSectionMaster(
	                    hpm.getCompanyId(), hpm.getProductId().toString(), sections.get(0).getSectionId());

				List<MotorDataDetails> list = motorRepo.findByQuoteNo(request.getQuoteno());
				PersonalInfo pi = piRepo.findByCustomerId(hpm.getCustomerId());
				String vehUsageCoreappcode = "";
				String policyNo = "";

				if (request.getProductId().equalsIgnoreCase("5"))
					vehUsageCoreappcode = getListItemvalue(request.getInsuranceId(), request.getBranchCode(),
							"MADISON_MOTOR", list.get(0).getMotorUsage(), pi.getPolicyHolderType());

				if (request.getInsuranceId().equalsIgnoreCase("100004")) {

					String itemvalue = getListItemvalue(request.getInsuranceId(), request.getBranchCode(), "POLICY_NO");

					policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),
							branchCode.get(0).getCoreAppCode(), request.getInsuranceId(), vehUsageCoreappcode,
							request.getProductId(), itemvalue);

				} else if (request.getInsuranceId().equalsIgnoreCase("100019")) {

					policyNo = genNo.generateUgandaPolicyNo(coreappcode.get(0).getCoreAppCode(),
							branchCode.get(0).getCoreAppCode());
				} else {
					policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),
							branchCode.get(0).getCoreAppCode());
				}

				request.setPolicyNo(policyNo);
			} else { // endt

				request.setPolicyNo(hpm.getPolicyNo());
			}

			/*
			 * HomePositionMaster homeData =
			 * homeRepo.findByQuoteNo(v1.getQuoteDetails().getQuoteNo());
			 * List<ChartOfAccount> getChartList = getChartList(homeData.getCompanyId()); //
			 * Source Type Search Condition List<String> directSource = new
			 * ArrayList<String>(); directSource.add("1"); directSource.add("2");
			 * directSource.add("3"); boolean directSourceAvailable =
			 * homeData.getSourceTypeId()!=null &&
			 * directSource.contains(homeData.getSourceTypeId()) ? true : false ;
			 * 
			 * String policyType = "99999"; if (product.getMotorYn().equalsIgnoreCase("M"))
			 * { List<MotorDataDetails> motors =
			 * motorRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteno()); policyType
			 * = motors.size() > 0 ? motors.get(0).getPolicyType() : "99999" ; }
			 * HomePositionMaster v = homeData ;
			 * 
			 * Double commissionPercent = 0.0;
			 * //commissionPercent=v.getCommissionPercentage().doubleValue(); String loginId
			 * = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
			 * List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
			 * v.getProductId().toString(), loginId, v.getBrokerCode(), policyType);
			 * 
			 * 
			 * // Premia Broker , Agent Condition if(directSourceAvailable == true ) {
			 * //commissionPercent=12.5; String commission = getListItem
			 * (homeData.getCompanyId() , homeData.getBranchCode()
			 * ,"COMMISSION_PERCENT",homeData.getSourceType() ); commissionPercent =
			 * StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
			 * 
			 * } else if(policylist.size()>0 && policylist!=null) {
			 * if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) &&
			 * "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) { commissionPercent
			 * = homeData.getCommissionPercentage()==null ? 0D :
			 * Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ; } else {
			 * commissionPercent = policylist.get(0).getCommissionPercentage().toString() ==
			 * null ? 0 :
			 * Double.valueOf(policylist.get(0).getCommissionPercentage().toString()); }
			 * 
			 * } else { commissionPercent=0D; }
			 * 
			 * String premiumFc = v.getPremiumFc().toString(); String vatPremiumFc =
			 * v.getVatPremiumFc()==null ?"0" : v.getVatPremiumFc().toPlainString();
			 * 
			 * 
			 * if(StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId()) ) {
			 * EndtUpdatePremiumRes endtRes = mainTableEndtPremium(v.getQuoteNo() ,
			 * product.getProductId().toString() , product.getMotorYn() ); premiumFc =
			 * endtRes.getEndtPremium()==null ? "0" :
			 * String.valueOf(endtRes.getEndtPremium().toPlainString()); vatPremiumFc =
			 * endtRes.getEndtVatPremium()==null ? "0" :
			 * String.valueOf(endtRes.getEndtVatPremium().toPlainString()) ;
			 * 
			 * }
			 * 
			 * BigDecimal commission = new BigDecimal(premiumFc).multiply(new
			 * BigDecimal(commissionPercent)) .divide(BigDecimal.valueOf(100D))
			 * .setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(),
			 * RoundingMode.HALF_UP); //totalcommission = totalcommission.add(commission);
			 * 
			 * 
			 * List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
			 * 
			 * // Setup Map<String, Object> setup = new HashMap<String, Object>();
			 * 
			 * List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>(); {
			 * Map<String, Object> subset = new HashMap<String, Object>(); String chargeCode
			 * = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
			 * 
			 * List<ChartOfAccount> filterChargeCode = getChartList.stream().filter( o ->
			 * o.getChartAccountCode().equals(Integer.valueOf(chargeCode))
			 * ).collect(Collectors.toList()); ChartOfAccount filteredCharge =
			 * filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ;
			 * 
			 * subset.put("CHARGE_CODE", chargeCode); subset.put("CHARGE_CODE_DESC",
			 * filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :
			 * "Premium"); subset.put("CHARGE_CODE_VALUE", premiumFc);
			 * subset.put("DISPLAY_ORDER", filteredCharge!=null ?
			 * filteredCharge.getDisplayOrder() : "1"); csubsets.add(subset); } {
			 * Map<String, Object> subset = new HashMap<String, Object>();
			 * List<ChartOfAccount> filterChargeCode = getChartList.stream().filter( o ->
			 * o.getChartAccountCode().equals(1009) ).collect(Collectors.toList());
			 * ChartOfAccount filteredCharge = filterChargeCode.size() > 0 ?
			 * filterChargeCode.get(0):null ; subset.put("CHARGE_CODE", "1009");
			 * subset.put("CHARGE_CODE_DESC", filteredCharge!=null ?
			 * filteredCharge.getChartAccountDesc() : "VAT"); subset.put("NARATION",
			 * filteredCharge!=null ? filteredCharge.getNaration()+ " " +
			 * Double.valueOf(homeData.getVatPercent()==null?"0":homeData.getVatPercent().
			 * toPlainString()) +"%" : "VAT"); subset.put("CHARGE_CODE_VALUE",
			 * vatPremiumFc); subset.put("DISPLAY_ORDER", filteredCharge!=null ?
			 * filteredCharge.getDisplayOrder() : "2"); csubsets.add(subset); } String
			 * crnumber =""; if(commissionPercent.doubleValue()>0D) {
			 * 
			 * List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>(); {
			 * Map<String, Object> subset = new HashMap<String, Object>(); String chargeCode
			 * = Double.valueOf(premiumFc)<0 ? "1006" : "1005" ; List<ChartOfAccount>
			 * filterChargeCode = getChartList.stream().filter( o ->
			 * o.getChartAccountCode().equals(Integer.valueOf(chargeCode))
			 * ).collect(Collectors.toList()); ChartOfAccount filteredCharge =
			 * filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ;
			 * subset.put("CHARGE_CODE", chargeCode); subset.put("CHARGE_CODE_DESC",
			 * filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Commission");
			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :
			 * "Commission"); subset.put("CHARGE_CODE_VALUE", commission);
			 * subset.put("DISPLAY_ORDER", filteredCharge!=null ?
			 * filteredCharge.getDisplayOrder() : "3"); bsubsets.add(subset); }
			 * 
			 * { Map<String, Object> subset = new HashMap<String, Object>(); String
			 * chargeCode = "1007" ; List<ChartOfAccount> filterChargeCode =
			 * getChartList.stream().filter( o ->
			 * o.getChartAccountCode().equals(Integer.valueOf(chargeCode))
			 * ).collect(Collectors.toList()); ChartOfAccount filteredCharge =
			 * filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ;
			 * subset.put("CHARGE_CODE", chargeCode); subset.put("CHARGE_CODE_DESC",
			 * filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :
			 * "Commission%"); subset.put("CHARGE_CODE_VALUE", commissionPercent);
			 * subset.put("DISPLAY_ORDER", filteredCharge!=null ?
			 * filteredCharge.getDisplayOrder() : "4"); bsubsets.add(subset); } {// Broker
			 * Commmission Vat String brokerLoginId = policylist.size() > 0 ?
			 * policylist.get(0).getLoginId() : ""; LoginUserInfo loginuser =
			 * loginUserRepo.findByLoginId(brokerLoginId); String brokerVatYn = loginuser
			 * !=null && loginuser.getTaxExemptedYn()!=null &&
			 * loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
			 * if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) { String
			 * brokerVatPercent = homeData.getVatPercent()==null ? "0" :
			 * homeData.getVatPercent().toPlainString(); BigDecimal brokerVatAmount =
			 * commission.multiply(new BigDecimal(brokerVatPercent))
			 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(0,
			 * RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
			 * 
			 * Map<String, Object> subset = new HashMap<String, Object>();
			 * List<ChartOfAccount> filterChargeCode = getChartList.stream().filter( o ->
			 * o.getChartAccountCode().equals(1009) ).collect(Collectors.toList());
			 * ChartOfAccount filteredCharge = filterChargeCode.size() > 0 ?
			 * filterChargeCode.get(0):null ; subset.put("CHARGE_CODE", "1009");
			 * subset.put("CHARGE_CODE_DESC", filteredCharge!=null ?
			 * filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+
			 * " " + Double.valueOf(brokerVatPercent) +"%" : "Vat");
			 * subset.put("CHARGE_CODE_VALUE", brokerVatAmount); subset.put("DISPLAY_ORDER",
			 * filteredCharge!=null ? filteredCharge.getDisplayOrder() : "5");
			 * bsubsets.add(subset); } } setup.put("<BROKER>", bsubsets); crnumber =
			 * genNo.generateCreditNo(branchCode.get(0).getCoreAppCode()); } /*
			 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
			 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
			 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
			 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
			 * 
			 * 
			 * Map<String,Object> subset=new HashMap<String, Object>();
			 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
			 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
			 * bsubsets.add(subset); }
			 * 
			 */
			/*
			 * setup.put("<CUSTOMER>", csubsets);
			 * 
			 * 
			 * // Rule Map<String, Object> rule1 = new HashMap<String, Object>();
			 * 
			 * if( v.getEndtPremium()!=null && v.getEndtPremium().compareTo(new
			 * BigDecimal(0))<=0 ){ rule1.put("DEBIT", "<BROKER>"); rule1.put("CREDIT",
			 * "<CUSTOMER>"); }else { rule1.put("DEBIT", "<CUSTOMER>"); rule1.put("CREDIT",
			 * "<BROKER>"); } rules.add(rule1);
			 * 
			 * // ThreadLocalRandom.current().ints(1001, //
			 * 4999).distinct().limit(5).findAny().toString(); String drnumber =
			 * genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); //
			 * ThreadLocalRandom.current().ints(4999, //
			 * 9999).distinct().limit(5).findAny().toString();
			 * 
			 * 
			 * int rownum = 1;
			 * 
			 * for (Map<String, Object> map : rules) { for (Entry<String, Object> m :
			 * map.entrySet()) { List<Map<String, Object>> dd = (List<Map<String, Object>>)
			 * setup.get(m.getValue()); if(dd!=null){ for (Map<String, Object> s : dd) {
			 * DebitAndCredit res =new DebitAndCredit(); String doctype =
			 * m.getValue().equals("<CUSTOMER>") ? "C" : "B";
			 * 
			 * res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
			 * if(s.get("CHARGE_CODE").toString().equalsIgnoreCase("1007") ) {
			 * res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString())); }
			 * else { String pattern = "#####0" ; DecimalFormat df = new
			 * DecimalFormat(pattern); res.setAmountLc( new
			 * BigDecimal(df.format(res.getAmountFc().multiply(v.getExchangeRate()))) );
			 * 
			 * }
			 * 
			 * res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
			 * res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
			 * res.setNarration(s.get("NARATION").toString());
			 * res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
			 * res.setBranchCode(request.getBranchCode()); res.setChgId(new
			 * BigDecimal(rownum++)); res.setCompanyId(request.getInsuranceId());
			 * res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId() :
			 * v1.getQuoteDetails().getLoginId()); res.setDocNo(m.getKey().equals("DEBIT") ?
			 * drnumber : crnumber); res.setDocType(doctype);
			 * res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
			 * res.setEntryDate(new Date()); res.setPolicyNo(request.getPolicyNo());
			 * res.setProductId(request.getProductId());
			 * res.setQuoteNo(request.getQuoteno()); res.setStatus("Y");
			 * res.setQuoteInfo(v1); resList.add(res); } } } }
			 * crdrservice.insertDRCR(resList, request.getQuoteno());
			 */
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList;
	}

	@Override
	public String getPolicyNo(CalcCommission request, HomePositionMaster hpm) {
		String policyNo = "";
		try {

//			ViewQuoteReq q = new ViewQuoteReq();
//			q.setQuoteNo(request.getQuoteno());
//			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
			CompanyProductMaster product = getCompanyProductMasterDropdown(
	                hpm.getCompanyId(), hpm.getProductId().toString());
	        String endttypeid = hpm.getEndtTypeId();
	        String emiYn = StringUtils.isBlank(hpm.getEmiYn()) ? "N" : hpm.getEmiYn();
//			String instalment=v1.getQuoteDetails().getInstallmentMonth();
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

//		 	if(request.getProductId().equalsIgnoreCase("5"))		 	
//		 		vehUsageCoreappcode = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "MADISON_MOTOR", list.get(0).getMotorUsage(), pi.getPolicyHolderType());	 	
//		 	
//		 	  if(request.getInsuranceId().equalsIgnoreCase("100004")) {
//		 		  
//		 		 String itemvalue = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "POLICY_NO");
//		 		  
//		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode(), request.getInsuranceId(), vehUsageCoreappcode, request.getProductId(), itemvalue);
//		 		 
//		 	  }else {
//		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
//		 	  }
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

	private EndtUpdatePremiumRes updateEndtPremium2(String quoteNo, Date effDate, String prevQuoteNo, Integer riskId,
			List<PolicyCoverData> covers, Integer productId, Integer sectionId, String endtType) {
		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();
		try {
			List<PolicyCoverData> newCovers = null;
			List<PolicyCoverData> totalcovers = null;
			List<PolicyCoverData> oldcovers = null;

			if (riskId.intValue() == 0) {
				newCovers = covers;
				totalcovers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				oldcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdAndStatusNotOrderByVehicleIdAsc(prevQuoteNo, 0,
						0, "D");
			} else {
				newCovers = covers.stream().filter(i -> i.getVehicleId().doubleValue() == riskId.doubleValue())
						.collect(Collectors.toList());
				totalcovers = coverRepo.findByQuoteNoAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(quoteNo,
						riskId, productId, sectionId);
				oldcovers = coverRepo
						.findByQuoteNoAndVehicleIdAndDiscLoadIdAndTaxIdAndStatusNotAndProductIdAndSectionIdOrderByVehicleIdAsc(
								prevQuoteNo, riskId, 0, 0, "D", productId, sectionId);
			}

			List<PolicyCoverData> oldcoversf = oldcovers;

			// Premium With Tax
//			Double removedCoverPremium =  (totalcovers.stream().filter( o ->   o.getPremiumIncludedTaxFc()!=null 
//					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType())  ) .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum());			 
//			 
//			Double endtChangePremium=totalcovers.stream().filter( o ->  o.getPremiumIncludedTaxFc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())  )
//			 .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
//			 
//			 newCovers.removeIf(p-> {
//				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
//			 });
//			 Double addedCoverPremium =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
//					 o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null 
//					 && !"D".equals(o.getStatus())
//					 && effDate.compareTo(o.getCoverPeriodFrom())>=0  ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
//				BigDecimal endtPremium= new  BigDecimal(removedCoverPremium+addedCoverPremium+endtChangePremium);
//				

			// Premium Without Tax
			Double removedCoverPremiumWithoutTax = (totalcovers.stream()
					.filter(o -> o.getPremiumExcludedTaxFc() != null && "D".equals(o.getStatus())
							&& "E".equals(o.getCoverageType()))
					.mapToDouble(o -> o.getPremiumExcludedTaxFc().doubleValue()).sum());

			Double endtChangePremiumWithoutTax = totalcovers.stream()
					.filter(o -> o.getPremiumExcludedTaxFc() != null && "E".equals(o.getCoverageType())
							&& !"D".equals(o.getStatus()))
					.mapToDouble(o -> o.getPremiumExcludedTaxFc().doubleValue()).sum();

			newCovers.removeIf(p -> {
				return oldcoversf.stream()
						.anyMatch(x -> (x.getVehicleId() == p.getVehicleId() && x.getSectionId() == p.getSectionId()
								&& x.getProductId() == p.getProductId() && x.getCoverId() == p.getCoverId()));
			});
			Double addedCoverPremiumWithoutTax = newCovers.stream()
					.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
							&& o.getPremiumExcludedTaxFc() != null && !"D".equals(o.getStatus())
							&& effDate.compareTo(o.getCoverPeriodFrom()) >= 0)
					.mapToDouble(o -> o.getPremiumIncludedTaxFc().doubleValue()).sum();
			BigDecimal endtPremiumWithoutTax = new BigDecimal(
					removedCoverPremiumWithoutTax + addedCoverPremiumWithoutTax + endtChangePremiumWithoutTax);

			// Tax Amount
			List<PolicyCoverData> endtTaxCovers = totalcovers.stream()
					.filter(o -> o.getCoverageType().equalsIgnoreCase("T")
							&& o.getDiscLoadId().equals(Integer.valueOf(endtType)))
					.collect(Collectors.toList());
			Double endtVatPremium = endtTaxCovers.stream()
					.filter(o -> !o.getDiscLoadId().equals(0) && !o.getTaxId().equals(0) && o.getTaxAmount() != null
							&& o.getCoverageType().equalsIgnoreCase("T"))
					.mapToDouble(o -> o.getTaxAmount().doubleValue()).sum();

			String endtChargeOrRefund = "REFUND";
			if (endtPremiumWithoutTax.doubleValue() >= 0) {
				endtChargeOrRefund = "CHARGE";
			} else if (endtPremiumWithoutTax.doubleValue() < 0 && endtVatPremium >= 0) {
				endtVatPremium = -endtVatPremium;
			}

			endtRes.setChargeOrRefund(endtChargeOrRefund);
			endtRes.setEndtPremium(endtPremiumWithoutTax);
			endtRes.setEndtVatPremium(new BigDecimal(endtVatPremium));

			return endtRes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return endtRes;
	}

	private EndtUpdatePremiumRes mainTableEndtPremium(String quoteNo, String productId, String productType) {
		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();
		try {
			Double endtPremiumWithoutTax = 0D;
			Double endtVatPremium = 0D;
			if (productType.equalsIgnoreCase("M")) {
				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				for (MotorDataDetails mot : motors) {
					endtPremiumWithoutTax = endtPremiumWithoutTax
							+ (mot.getEndtPremium() == null ? 0D : mot.getEndtPremium().doubleValue());
					endtVatPremium = endtVatPremium
							+ (mot.getEndtVatPremium() == null ? 0D : mot.getEndtVatPremium().doubleValue());

				}

				// Travel Product
			} else if (productType.equalsIgnoreCase("H") && productId.equalsIgnoreCase(travelProductId)) {
				// List<EserviceTravelGetRes> motors = (List<EserviceTravelGetRes>)
				// v1.getRiskDetails();
				// EserviceTravelDetails tra =
				// eserTraRepo.findByRequestReferenceNo(request.getQuoteNo() );
				HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
				endtPremiumWithoutTax = endtPremiumWithoutTax
						+ (homeData.getEndtPremium() == null ? 0D : homeData.getEndtPremium().doubleValue());
				endtVatPremium = endtVatPremium
						+ (homeData.getEndtPremiumTax() == null ? 0D : homeData.getEndtPremiumTax().doubleValue());

			} else if (productType.equalsIgnoreCase("A")) {
				List<BuildingRiskDetails> BuildingRisk = buildingRepo
						.findByQuoteNoAndSectionIdNotOrderByRiskIdAsc(quoteNo, "0");

				// Asset
				for (BuildingRiskDetails build : BuildingRisk) {
					endtPremiumWithoutTax = endtPremiumWithoutTax
							+ (build.getEndtPremium() == null ? 0D : build.getEndtPremium().doubleValue());
					endtVatPremium = endtVatPremium
							+ (build.getEndtVatPremium() == null ? 0D : build.getEndtVatPremium().doubleValue());

				}

				// Human Included
				List<CommonDataDetails> humans = commonRepo.findByQuoteNo(quoteNo);

				for (CommonDataDetails hum : humans) {
					endtPremiumWithoutTax = endtPremiumWithoutTax
							+ (hum.getEndtPremium() == null ? 0D : hum.getEndtPremium().doubleValue());
					endtVatPremium = endtVatPremium
							+ (hum.getEndtVatPremium() == null ? 0D : hum.getEndtVatPremium().doubleValue());
				}

				// Human Products
			} else {
				List<CommonDataDetails> humans = commonRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);

				for (CommonDataDetails hum : humans) {
					endtPremiumWithoutTax = endtPremiumWithoutTax
							+ (hum.getEndtPremium() == null ? 0D : hum.getEndtPremium().doubleValue());
					endtVatPremium = endtVatPremium
							+ (hum.getEndtVatPremium() == null ? 0D : hum.getEndtVatPremium().doubleValue());
				}

			}

			String endtChargeOrRefund = "REFUND";
			if (endtPremiumWithoutTax.doubleValue() >= 0) {
				endtChargeOrRefund = "CHARGE";
			}

			endtRes.setChargeOrRefund(endtChargeOrRefund);
			endtRes.setEndtPremium(new BigDecimal(endtPremiumWithoutTax));
			endtRes.setEndtVatPremium(new BigDecimal(endtVatPremium));

			return endtRes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return endtRes;
	}

//	public List<DebitAndCredit> getRiskWisecommissionCalc(CalcCommission request ) {
//		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
//		try {
//			ViewQuoteReq q = new ViewQuoteReq();
//			q.setQuoteNo(request.getQuoteno());
//			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
//			CompanyProductMaster product =  getCompanyProductMasterDropdown(v1.getQuoteDetails().getCompanyId() , v1.getQuoteDetails().getProductId().toString());
//			String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//			String emiYn=v1.getQuoteDetails().getEmiYn();
//			String instalment=v1.getQuoteDetails().getInstallmentMonth();
//			 List<BranchMaster> branchCode=ratingutil.collectBranchMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getBranchCode());
//			if (StringUtils.isBlank(endttypeid)&& ( emiYn.equalsIgnoreCase("N") || instalment.equalsIgnoreCase("0"))) {			 
//				List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
//		 	List<ProductSectionMaster> coreappcode=ratingutil.collectSectionMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getProductId().toString(),sections.get(0).getSectionId());
//		 	String policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
//				request.setPolicyNo(policyNo);
//			} else {
//				request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//			}
//			
//			HomePositionMaster homeData = homeRepo.findByQuoteNo(v1.getQuoteDetails().getQuoteNo());
//			List<ChartOfAccount>  getChartList = getChartList(homeData.getCompanyId());
//			// Source Type Search Condition
//			List<String> directSource = new ArrayList<String>();
//			directSource.add("1");
//			directSource.add("2");
//			directSource.add("3");
//			boolean directSourceAvailable = homeData.getSourceTypeId()!=null && directSource.contains(homeData.getSourceTypeId()) ? true : false ;  
//			
//			
//			if (product.getMotorYn().equalsIgnoreCase("M")) {
//				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteno());
//					//List<EserviceMotorDetailsRes> motors = (List<EserviceMotorDetailsRes>) v1.getRiskDetails();
//				for (MotorDataDetails v : motors) {
//					Double commissionPercent = 0.0;
//					//commissionPercent=v.getCommissionPercentage().doubleValue();
//					String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//					List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//							v.getProductId().toString(), loginId, v.getBrokerCode(), v.getPolicyType());
//					 
//						
//					// Premia Broker , Agent Condition
//					 if(directSourceAvailable == true ) {
//						//commissionPercent=12.5;
//						String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//						commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//						
//					} else if(policylist.size()>0 && policylist!=null) {
//							if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//								commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//							} else {
//								commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//										: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//							}
//							
//					}
//					else {
//						commissionPercent=0D;
//					}
//					
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc = v.getVatPremium()==null  ?"0" : v.getVatPremium().toPlainString();
//				
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//						
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					//totalcommission = totalcommission.add(commission);
//					
//					
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//					
//					if("D".equals(v.getStatus()) || ( v.getEndtPremium()!=null && v.getEndtPremium() <0 ) ){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					rules.add(rule1);
//	
//					 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber =  genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//					
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null){
//								for (Map<String, Object> s : dd) {
//									DebitAndCredit res =new  DebitAndCredit();
//									String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//	
//									res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//									res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//									res.setNarration(s.get("NARATION").toString());
//									res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//									res.setRiskDesc("Vehicle Id -" + v.getVehicleId() ) ;
//									res.setBranchCode(request.getBranchCode());
//									res.setChgId(new BigDecimal(rownum++));
//									res.setCompanyId(request.getInsuranceId());
//									res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//											: v1.getQuoteDetails().getLoginId());
//									res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//									res.setDocType(doctype);
//									res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//									res.setEntryDate(new Date());
//									res.setPolicyNo(request.getPolicyNo());
//									res.setProductId(request.getProductId());
//									res.setQuoteNo(request.getQuoteno());
//									res.setStatus("Y");
//									res.setRiskId(v.getVehicleId());
//									res.setQuoteInfo(v1);
//									res.setSectionId(v.getInsuranceClass());
//									resList.add(res);
//								}
//							}
//						}
//					}
//					crdrservice.insertDRCR(resList, request.getQuoteno());
//				}
//				
//			}
//	
//			// Travel Product
//			else if (product.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
//					//List<EserviceTravelGetRes> motors = (List<EserviceTravelGetRes>) v1.getRiskDetails();
//				List<TravelPassengerDetails> motors = travelRepo.findByQuoteNoOrderByTravelIdAsc(request.getQuoteno());
//	
//					for (TravelPassengerDetails v : motors) {
//						Double commissionPercent = 0.0;
//						// Double commissionPercent = v.getCommissionPercentage().doubleValue();
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//					List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//							v.getProductId().toString(), loginId, v1.getQuoteDetails().getBrokerCode(), "99999");
//							
//					// Premia Broker , Agent Condition
//					 if(directSourceAvailable == true) {
//						//commissionPercent=12.5;
//							String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//							commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//						
//					} else if(policylist.size()>0 && policylist!=null) {
//						
//						if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//							commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//						} else {
//							commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//									: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//						}
//					}
//					else {
//						commissionPercent=0D;
//					}
//					 String premiumFc ="";
//					 String vatPremiumFc = "";
//					 
//					if(! v.getStatus().equalsIgnoreCase("D")) {
//						premiumFc = v.getActualPremiumFc().toString();
//						vatPremiumFc = String.valueOf(  v.getVatPremium());
//						
//					}
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId()) && v.getStatus().equalsIgnoreCase("D") ) {
//						premiumFc = v.getEndtPremium() ==null ? "" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"" :  v.getEndtVatPremium().toPlainString();
//					}
//	
//					
//					
//				//	String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					if(StringUtils.isNotBlank(premiumFc) ) {
//						BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//								.divide(BigDecimal.valueOf(100D))
//								.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//						
//						Map<String, Object> setup = new HashMap<String, Object>();
//	
//						List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//							
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//									
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//							subset.put("CHARGE_CODE_VALUE", premiumFc);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//							csubsets.add(subset);
//						}
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1009");
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//							subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//							csubsets.add(subset);
//						}
//						String crnumber ="";
//						if(commissionPercent.doubleValue()>0D) {
//	
//							List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//							{
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1005");
//								subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", commission);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//								bsubsets.add(subset);
//							}
//	
//							{
//								Map<String, Object> subset = new HashMap<String, Object>();
//								String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", chargeCode);
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//								subset.put("CHARGE_CODE_VALUE", commissionPercent);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//								bsubsets.add(subset);
//							}
//							{// Broker Commmission Vat
//								String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//								LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//								String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//								if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//									String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//									BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//											.divide(BigDecimal.valueOf(100D))
//											.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//											
//									Map<String, Object> subset = new HashMap<String, Object>();
//									List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//									ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//									subset.put("CHARGE_CODE", "1009");
//									subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//									subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//									subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//									subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//									bsubsets.add(subset);
//								}
//							}
//							setup.put("<BROKER>", bsubsets);
//							crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//						}
//						
//						/*
//						 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//						 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//						 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//						 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//						 * 
//						 * 
//						 * Map<String,Object> subset=new HashMap<String, Object>();
//						 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//						 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//						 * bsubsets.add(subset); }
//						 * 
//						 */
//						setup.put("<CUSTOMER>", csubsets);
//						
//	
//						// Rule
//						Map<String, Object> rule1 = new HashMap<String, Object>();
//						
//						if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//							rule1.put("DEBIT", "<BROKER>");
//							rule1.put("CREDIT", "<CUSTOMER>");
//						}else {
//								rule1.put("DEBIT", "<CUSTOMER>");
//								rule1.put("CREDIT", "<BROKER>");
//						}
//						
//						rules.add(rule1);
//	
//						 // ThreadLocalRandom.current().ints(1001,
//																			// 4999).distinct().limit(5).findAny().toString();
//						String drnumber =  genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																			// 9999).distinct().limit(5).findAny().toString();
//	
//						/*if (StringUtils.isBlank(endttypeid)) {
//							String policyNo = genNo.generatePolicyNo();
//							request.setPolicyNo(policyNo);
//						} else {
//							request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//						}*/
//						int rownum = 1;
//						
//						for (Map<String, Object> map : rules) {
//							for (Entry<String, Object> m : map.entrySet()) {
//	
//								List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//								if(dd!=null) {
//									for (Map<String, Object> s : dd) {
//										DebitAndCredit res =new  DebitAndCredit();
//										String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//	
//										res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//										res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//										res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//										res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//										res.setNarration(s.get("NARATION").toString());
//										res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//										res.setRiskDesc("Passenger Id -" + v.getPassengerId() ) ;
//										res.setBranchCode(request.getBranchCode());
//										res.setChgId(new BigDecimal(rownum++));
//										res.setCompanyId(request.getInsuranceId());
//										res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//												: v1.getQuoteDetails().getLoginId());
//										res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//										res.setDocType(doctype);
//										res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//										res.setEntryDate(new Date());
//										res.setPolicyNo(request.getPolicyNo());
//										res.setProductId(request.getProductId());
//										res.setQuoteNo(request.getQuoteno());
//										res.setStatus("Y");
//										res.setQuoteInfo(v1);
//										res.setSectionId(v.getSectionId().toString());
//										res.setRiskId(v.getPassengerId().toString());
//										resList.add(res);
//									}
//								}
//							}
//						}
//						crdrservice.insertDRCR(resList, request.getQuoteno());
//					}
//					
//					
//	
//				
//				}
//				
//			}
//	
//			// Building and SME Product
//			else if (product.getMotorYn().equalsIgnoreCase("A")) {
//	//				List<EserviceBuildingsDetailsRes> motors = (List<EserviceBuildingsDetailsRes>) v1.getRiskDetails();
//				List<BuildingRiskDetails> motors = buildingRepo.findByQuoteNoAndSectionIdNotOrderByRiskIdAsc(request.getQuoteno() ,"0");
//	
//					for (BuildingRiskDetails v : motors) {
//						 Double commissionPercent = 0.0;
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//					List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//							v.getProductId().toString(), loginId, v.getBrokerCode(),"99999");
//					 // Double commissionPercent = v.getCommissionPercentage().doubleValue();
//					
//					// Premia Broker , Agent Condition
//					 if(directSourceAvailable == true) {
//						//commissionPercent=12.5;
//						String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//						commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//						
//					} else if(policylist.size()>0 && policylist!=null) {
//						
//						if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//							commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//						} else {
//							commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//									: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//						}
//					}
//					else {
//						commissionPercent=0D;
//					}
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc = String.valueOf( v.getOverallPremiumFc().subtract( v.getActualPremiumFc()));
//	
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					v1.getQuoteDetails().getVatPercent();
//				 //  String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//	
//					if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					
//					rules.add(rule1);
//	
//				 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber =  genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//					/*if (StringUtils.isBlank(endttypeid)) {
//						String policyNo = genNo.generatePolicyNo();
//						request.setPolicyNo(policyNo);
//					} else {
//						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//					}*/
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//	
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null) {
//								for (Map<String, Object> s : dd) {
//									DebitAndCredit res =new  DebitAndCredit();
//									String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//									res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//									res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//									res.setNarration(s.get("NARATION").toString());
//									res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//									res.setRiskDesc( v.getSectionDesc() ) ;
//									res.setBranchCode(request.getBranchCode());
//									res.setChgId(new BigDecimal(rownum++));
//									res.setCompanyId(request.getInsuranceId());
//									res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//											: v1.getQuoteDetails().getLoginId());
//									res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//									res.setDocType(doctype);
//									res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//									res.setEntryDate(new Date());
//									res.setPolicyNo(request.getPolicyNo());
//									res.setProductId(request.getProductId());
//									res.setQuoteNo(request.getQuoteno());
//									res.setStatus("Y");
//									res.setQuoteInfo(v1);
//									res.setSectionId(v.getSectionId());
//									res.setRiskId(v.getRiskId().toString());
//									resList.add(res);
//								}
//							}
//						}
//					}
//					
//				}
//					
//					// Human Icluded
//					List<CommonDataDetails> humans = commonRepo.findByQuoteNo(request.getQuoteno());
//	
//					for (CommonDataDetails v : humans) {
//						 Double commissionPercent = 0.0;
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//						List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//								v.getProductId().toString(), loginId, v.getBrokerCode(),"99999");
//						 // Double commissionPercent = v.getCommissionPercentage().doubleValue();
//						
//						// Premia Broker , Agent Condition
//						 if(directSourceAvailable == true) {
//							//commissionPercent=12.5;
//							String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//							commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//							
//						} else if(policylist.size()>0 && policylist!=null) {
//							
//							if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//								commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//							} else {
//								commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//										: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//							}
//						}
//						else {
//							commissionPercent=0D;
//						}
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc =String.valueOf( v.getOverallPremiumFc().subtract( v.getActualPremiumFc()));
//	
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//	
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					v1.getQuoteDetails().getVatPercent();
//				
//					//String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//	
//					if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					
//					rules.add(rule1);
//	
//					 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber = genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//				/*	if (StringUtils.isBlank(endttypeid)) {
//						String policyNo = genNo.generatePolicyNo();
//						request.setPolicyNo(policyNo);
//					} else {
//						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//					}*/
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//	
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null) {
//	
//							for (Map<String, Object> s : dd) {
//							 	 DebitAndCredit res =new  DebitAndCredit();
//								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//															res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//								res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//								res.setNarration(s.get("NARATION").toString());
//								res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//								res.setRiskDesc( v.getSectionDesc() +"-" + v.getOccupationDesc() ) ;
//								res.setBranchCode(request.getBranchCode());
//								res.setChgId(new BigDecimal(rownum++));
//								res.setCompanyId(request.getInsuranceId());
//								res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//										: v1.getQuoteDetails().getLoginId());
//								res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//								res.setDocType(doctype);
//								res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//								res.setEntryDate(new Date());
//								res.setPolicyNo(request.getPolicyNo());
//								res.setProductId(request.getProductId());
//								res.setQuoteNo(request.getQuoteno());
//								res.setStatus("Y");
//								res.setQuoteInfo(v1);
//								res.setSectionId(v.getSectionId());
//								res.setRiskId(v.getRiskId().toString());
//								resList.add(res);
//							}
//							}
//						}
//					}
//				}
//					
//					crdrservice.insertDRCR(resList, request.getQuoteno());
//			}
//			
//			// Common Product
//			else {
//	//				List<EserviceCommonGetRes> motors = (List<EserviceCommonGetRes>) v1.getRiskDetails();
//					List<CommonDataDetails> motors = commonRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
//	
//					for (CommonDataDetails v : motors) {
//						 Double commissionPercent = 0.0;
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//						List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//								v.getProductId().toString(), loginId, v.getBrokerCode(),"99999");
//						 // Double commissionPercent = v.getCommissionPercentage().doubleValue();
//						
//						// Premia Broker , Agent Condition
//						 if(directSourceAvailable == true) {
//							//commissionPercent=12.5;
//							String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//							commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//							
//						} else if(policylist.size()>0 && policylist!=null) {
//							
//							if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//								commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//							} else {
//								commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//										: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//							}
//						}
//						else {
//							commissionPercent=0D;
//						}
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc = String.valueOf( v.getOverallPremiumFc().subtract( v.getActualPremiumFc()));
//	
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//	
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					v1.getQuoteDetails().getVatPercent();
//				
//					//String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//	
//					if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					
//					rules.add(rule1);
//	
//					 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber = genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//				/*	if (StringUtils.isBlank(endttypeid)) {
//						String policyNo = genNo.generatePolicyNo();
//						request.setPolicyNo(policyNo);
//					} else {
//						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//					}*/
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//	
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null) {
//	
//							for (Map<String, Object> s : dd) {
//							 	 DebitAndCredit res =new  DebitAndCredit();
//								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//															res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//								res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//								res.setNarration(s.get("NARATION").toString());
//								res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//								res.setRiskDesc(  v.getSectionDesc() +"-" + v.getOccupationDesc() ) ;
//								res.setBranchCode(request.getBranchCode());
//								res.setChgId(new BigDecimal(rownum++));
//								res.setCompanyId(request.getInsuranceId());
//								res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//										: v1.getQuoteDetails().getLoginId());
//								res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//								res.setDocType(doctype);
//								res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//								res.setEntryDate(new Date());
//								res.setPolicyNo(request.getPolicyNo());
//								res.setProductId(request.getProductId());
//								res.setQuoteNo(request.getQuoteno());
//								res.setStatus("Y");
//								res.setQuoteInfo(v1);
//								res.setSectionId(v.getSectionId());
//								res.setRiskId(v.getRiskId().toString());
//								resList.add(res);
//							}
//							}
//						}
//					}
//					crdrservice.insertDRCR(resList, request.getQuoteno());
//				}
//				
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	public  String getListItemvalue(String insuranceId, String branchCode, String itemType) {
		String itemvalue = "";
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate b3 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate b4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, b3, b4);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate b1 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4, b1, b2);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n12 = cb.equal(c.get("status"), "R");
			Predicate n13 = cb.or(n1, n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);

			query.where(n13, n2, n3, n4, n9, n10);

			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemvalue = list.size() > 0 ? list.get(0).getItemValue() : "";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return itemvalue;

	}

	public  String getListItemvalue(String insuranceId, String branchCode, String itemType,
			String vehUsageId, String cusTypeId) {
		String coreappcode = "";
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate b3 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate b4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, b3, b4);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate b1 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4, b1, b2);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n12 = cb.equal(c.get("status"), "R");
			Predicate n13 = cb.or(n1, n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			Predicate n11 = cb.equal(c.get("itemCode"), vehUsageId); // Veh USAGE Id (private, commercial, special)
			Predicate n14 = cb.equal(c.get("param1"), cusTypeId); // Customer TYPE Id (corporate/Indidual)

			query.where(n13, n2, n3, n4, n9, n10, n11, n14);

			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			coreappcode = list.size() > 0 ? list.get(0).getCoreAppCode() : "";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return coreappcode;

	}

	private List<BrokerCommissionDetails> getPolicyName(String companyId, String productId, String loginId,
			String agencyCode, String policyType) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
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
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a2 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a6 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a7 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			effectiveDate.where(a1, a2, a3, a4, a5, a6, a7);

			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm2 = effectiveDate2.from(BrokerCommissionDetails.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(ocpm2.get("id"), b.get("id"));
			Predicate a10 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a11 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate a12 = cb.equal(ocpm2.get("policyType"), b.get("policyType"));
			Predicate a13 = cb.equal(ocpm2.get("loginId"), b.get("loginId"));
			Predicate a14 = cb.equal(ocpm2.get("agencyCode"), b.get("agencyCode"));

			effectiveDate2.where(a8, a9, a10, a11, a12, a13, a14);

			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"), companyId);
			Predicate n4 = cb.equal(b.get("productId"), productId);
			Predicate n5 = cb.equal(b.get("loginId"), loginId);
			// Predicate n6 = cb.like(b.get("agencyCode"), "%" + agencyCode + "%");
			Predicate n7 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n8 = cb.equal(b.get("status"), "Y");

			query.where(n1, n2, n3, n4, n5, n7, n8);

			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}

	@Override
	public List<AdminReferral> getReferalList(ReferralApi request) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			List<SpecCriteria> criterias;
			List<String> columns = new ArrayList<String>();
			columns.add("loginId");
			columns.add("userName");
			columns.add("userMobile");
			columns.add("mobileCodeDesc");
			columns.add("companyName");
			columns.add("userMail");
			columns.add("userName");
			columns.add("whatsappCodeDesc");
			columns.add("whatsappNo");
			columns.add("userType");
			columns.add("subUserType");

			String s1 = "userType:Issuer;subUserType:{high,both};companyId:" + request.getInsuranceId()
					+ ";attachedBranches%" + request.getBranchCode() + ";status:Y;";
			SpecCriteria c1 = crservice.createCriteria(LoginMaster.class, s1, "loginId", columns);
			JoinCriteria j1 = new JoinCriteria();
			j1.setColumnName("loginId");
			j1.setToColumnName("loginId");
			j1.setToTableName(LoginProductMaster.class);
			List<JoinCriteria> j1s = new ArrayList<JoinCriteria>();
			j1s.add(j1);
			c1.setJoins(j1s);

			String s2 = "productId:" + request.getProductId() + ";status:Y;" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + request.getSuminsured()
					+ "~sumInsuredStart&sumInsuredEnd";
			SpecCriteria c2 = crservice.createCriteria(LoginProductMaster.class, s2, "loginId", columns);

			JoinCriteria j2 = new JoinCriteria();
			j2.setColumnName("loginId");
			j2.setToColumnName("loginId");
			j2.setToTableName(LoginMaster.class);

			JoinCriteria j2_1 = new JoinCriteria();
			j2_1.setColumnName("loginId");
			j2_1.setToColumnName("loginId");
			j2_1.setToTableName(LoginUserInfo.class);

			List<JoinCriteria> j2s = new ArrayList<JoinCriteria>();
			j2s.add(j2);
			j2s.add(j2_1);
			c2.setJoins(j2s);

			String s3 = "status:Y;";
			SpecCriteria c3 = crservice.createCriteria(LoginUserInfo.class, s3, "loginId", columns);

			JoinCriteria j3 = new JoinCriteria();
			j3.setColumnName("loginId");
			j3.setToColumnName("loginId");
			j3.setToTableName(LoginMaster.class);
			List<JoinCriteria> j3s = new ArrayList<JoinCriteria>();
			j3s.add(j3);
			c3.setJoins(j3s);

			criterias = new ArrayList<SpecCriteria>();
			criterias.add(c1);
			criterias.add(c2);
			criterias.add(c3);

			List<Tuple> joinResult = crservice.getJoinResult(criterias, 0, 0);
			List<AdminReferral> list = new ArrayList<AdminReferral>();
			for (Tuple tuple : joinResult) {

				AdminReferral a = AdminReferral.builder().insuranceId((String) tuple.get("companyName"))
						.loginId((String) tuple.get("loginId")).mailId((String) tuple.get("userMail"))
						.mobileCode((String) tuple.get("mobileCodeDesc")).mobileNo((String) tuple.get("userMobile"))
						.userName((String) tuple.get("userName")).whatsappcode((String) tuple.get("whatsappCodeDesc"))
						.whatsAppNo((String) tuple.get("whatsappNo")).uwuserType((String) tuple.get("userType"))
						.uwsubuserType((String) tuple.get("subUserType")).build();
				list.add(a);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public  CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
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
			return null;
		}
		return product;
	}

	public  String getListItem(String insuranceId, String branchCode, String itemType, String itemCode) {
		String itemDesc = "";
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
			Predicate b3 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate b4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, b3, b4);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate b1 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4, b1, b2);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n12 = cb.equal(c.get("status"), "R");
			Predicate n13 = cb.or(n1, n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);

			query.where(n13, n2, n3, n4, n9, n10, n11).orderBy(orderList);

			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
		} catch (Exception e) {
			e.printStackTrace();
			// log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc;
	}

	public List<ChartOfAccount> getChartList(String companyId) {
		List<ChartOfAccount> list = new ArrayList<ChartOfAccount>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 1);;
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 59);
//			Date todayEnd = cal.getTime();
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ChartOfAccount> query = cb.createQuery(ChartOfAccount.class);

			// Find All
			Root<ChartOfAccount> c = query.from(ChartOfAccount.class);
			// Select
			query.select(c);

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ChartOfAccount> ocpm1 = effectiveDate.from(ChartOfAccount.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("chartAccountCode"), ocpm1.get("chartAccountCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("chartAccountCode"), ocpm1.get("chartAccountCode"));
			effectiveDate.where(a1, a2, a3, a4);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ChartOfAccount> ocpm2 = effectiveDate2.from(ChartOfAccount.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a7 = cb.equal(c.get("chartAccountCode"), ocpm2.get("chartAccountCode"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a10 = cb.equal(c.get("chartAccountCode"), ocpm2.get("chartAccountCode"));
			effectiveDate2.where(a7, a8, a9, a10);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("chartAccountCode")));

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n8 = cb.equal(c.get("status"), "R");
			Predicate n9 = cb.or(n1, n8);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			query.where(n9, n2, n3, n4).orderBy(orderList);

			// Get Result

			TypedQuery<ChartOfAccount> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
		return list;
	}
	
	public BigDecimal getOverallPremiumByRefNo(String requestRefNo) {
		
		BigDecimal premium = BigDecimal.ZERO;
		String sqlQuery =
                "select sum(fac.premiumExcludedTaxFc) " +
                "  from FactorRateRequestDetails fac " +
                " where fac.requestReferenceNo = :requestReferenceNo " +
                "   and fac.coverageType in (:coverageType) " +
                "   and (fac.isSelected = :isSelected or fac.userOpt = :userOpt) " +
                "   and fac.vehicleId not in (:vehicleId)";

        premium = (BigDecimal) em.createQuery(sqlQuery)
                .setParameter("requestReferenceNo", requestRefNo)
                .setParameter("coverageType", Arrays.asList("B", "O"))
                .setParameter("isSelected", "D")
                .setParameter("userOpt", "Y")
                .setParameter("vehicleId", 99999)
                .getSingleResult();

	    return premium == null ? BigDecimal.ZERO : premium;
	}


	@Override
	public EserviceMotorDetailsSaveRes policyCalculator(CalcEngine engine, String tokens) {
		String isEndt = null;
		List<UWReferrals> referr = null;
		List<MasterReferal> masterreferral = null;
		List<Cover> retc = new ArrayList<Cover>();
		String promocode = "";
		loadOnetimetablePolicy(engine);
		String currencyId = policytbl.get(0).get("currency") == null ? "TTT"
				: policytbl.get(0).get("currency").toString();

		String decimalDigits = ratingutil.currencyDecimalFormat(engine.getInsuranceId(), currencyId);
		String stringFormat = "%0" + decimalDigits + "d";
		String decimalLength = decimalDigits.equals("0") ? "" : String.format(stringFormat, 0L);
		String pattern = StringUtils.isBlank(decimalLength) ? "#####0" : "#####0." + decimalLength;
		decimalFormat = new DecimalFormat(pattern);

		List<Tuple> totalcoverstuple = LoadCoverPolicy(engine);
		
		//minimum premium

	    BigDecimal overallPremium = getOverallPremiumByRefNo(engine.getRequestReferenceNo());

	    BigDecimal productMinPremium = companyProductMasterRepo
	            .findMinPremium(engine.getInsuranceId(),Integer.valueOf(engine.getProductId()), new Date());

	    if (productMinPremium == null) {
	        productMinPremium = BigDecimal.ZERO;
	    }


	    
	    BigDecimal minPremDiff = BigDecimal.ZERO;
	    if (overallPremium.compareTo(productMinPremium) < 0) {
	        minPremDiff = productMinPremium.subtract(overallPremium); 
	    }

		List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
		
		long policyDays = ratingutil.calculatePolicyDays(engine);
		List<Tuple> resolvedTaxes = ratingutil.resolveTaxByPolicyDays(taxes, policyDays);
		TaxUtils tzx = new TaxUtils(BigDecimal.ZERO, "");
		
		List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
		TaxRemover taxRemov = new TaxRemover(excludedTaxes, null);

		List<String> dependedcovers = new ArrayList<String>();
		dependedcovers.add("N");
		dependedcovers.add("Y");
		for (String dependcover : dependedcovers) {
			List<Cover> totalcovers = new ArrayList<Cover>();
			List<Tuple> covers = totalcoverstuple.stream()
					.filter(t -> dependcover.equals(t.get("dependentCoverYn").toString())).collect(Collectors.toList());
			List<Discount> discounts = null;
			List<Loading> loadings = null;
			
			// find one base cover (coverageType = 'B') to attach minimum-premium loading
			String baseCoverId = totalcoverstuple.stream()
			        .filter(t -> "B".equalsIgnoreCase(
			                t.get("coverageType") == null ? "" : t.get("coverageType").toString()))
			        .map(t -> t.get("coverId").toString())
			        .findFirst()
			        .orElse(null);   // if null, we simply won't add the loading

			
			if (totalcoverstuple != null && totalcoverstuple.size() > 0) {
				
				//Merge Underwriter adjustments into tuple data
				List<Map<String, Object>> uwMergedTuples = new ArrayList<>();

				for (Tuple t : totalcoverstuple) {
				    Map<String, Object> map = new HashMap<>();
				    t.getElements().forEach(e -> map.put(e.getAlias(), t.get(e.getAlias())));

				    String coverageType = map.get("coverageType") == null ? "" : map.get("coverageType").toString();

				    // process Discount / Loading covers
				    if (!"D".equalsIgnoreCase(coverageType) && !"L".equalsIgnoreCase(coverageType)) {
				        uwMergedTuples.add(map);
				        continue;
				    }

				    UnderwriterAdjustmentReq uw = null;
				    if (engine.getUnderwriterAdjustments() != null && !engine.getUnderwriterAdjustments().isEmpty()) {
				        uw = engine.getUnderwriterAdjustments().get(0); 
				    }

				    if (uw != null) {
				        // manual Discount overrides
				        if ("D".equalsIgnoreCase(coverageType) && "Y".equalsIgnoreCase(uw.getUwDiscountYn())) {
				            map.put("uwDiscountYn", uw.getUwDiscountYn());
				            map.put("uwDiscountType", uw.getUwDiscountType());
				            map.put("uwDiscountValue", uw.getUwDiscountValue());
				            map.put("uwDiscountDesc", uw.getUwDiscountDesc());
				        }

				        // manual Loading overrides
				        if ("L".equalsIgnoreCase(coverageType) && "Y".equalsIgnoreCase(uw.getUwLoadingYn())) {
				            map.put("uwLoadingYn", uw.getUwLoadingYn());
				            map.put("uwLoadingType", uw.getUwLoadingType());
				            map.put("uwLoadingValue", uw.getUwLoadingValue());
				            map.put("uwLoadingDesc", uw.getUwLoadingDesc());
				        }
				    }

				    uwMergedTuples.add(map);
				}

				List<Tuple> mergedTuples = uwMergedTuples.stream()
					    .map(m -> new MapBackedTuple(m))
					    .collect(Collectors.<Tuple>toList());


				


				SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
				        engine.getPolicyEndDate(), promocode);
				discounts = mergedTuples.stream().map(discountUtil).filter(Objects::nonNull).collect(Collectors.toList());
				discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
				SplitLoadingUtils loadingUtil = new SplitLoadingUtils(engine.getEffectiveDate(),
				        engine.getPolicyEndDate());
				loadings = mergedTuples.stream().map(loadingUtil).filter(Objects::nonNull).collect(Collectors.toList());
			}
			
			

			SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
					engine.getPolicyEndDate());
			Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
					.collect(Collectors.groupingBy(Cover::getIsSubCover));
			if (!nonSubcovers.isEmpty()) {
				List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
				if (!discounts.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Discount> ds = discounts.stream().filter(d -> d.getDiscountforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
						// List<Tax> taxey =
						// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
						c.setDiscounts(ds);
						// c.setTaxes(taxey);
					}
				}

				if (!loadings.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
						// List<Tax> taxey =
						// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
						
						System.out.println("");
						c.setLoadings(ds);
						// c.setTaxes(taxey);
					}
				}

				if (!noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
							List<Tax> taxey = resolvedTaxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
						}
					}
				}
			}

			splitsub = new SplitSubCoverUtil("Y", engine.getEffectiveDate(), engine.getPolicyEndDate());
			Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
					.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
					.collect(Collectors.groupingBy(Cover::getIsSubCover));
			if (!subcovers.isEmpty()) {
				List<Cover> noncovers = subcovers.get("Y"); // noncovers
				if (!discounts.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Discount> ds = discounts.stream().filter(d -> d.getDiscountforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

						List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
								.collect(Collectors.toList());
						// List<Tax> taxez =
						// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
						c.setDiscounts(dss);
						// c.setTaxes(taxez);
					}
				}

				if (!loadings.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

						List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
								.collect(Collectors.toList());
						c.setLoadings(dss);
					}
				}
				if (!noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
							List<Tax> taxey = resolvedTaxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
						}
					}
				}

				List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
						.collect(Collectors.toList());
				List<Cover> subcov = new ArrayList<Cover>();
				for (Cover cover : d) {
					List<Cover> subcover = noncovers.stream().filter(cv -> cv.getCoverId().equals(cover.getCoverId()))
							.collect(Collectors.toList());
					subcover.stream().forEach(s -> s.setIsSubCover("N"));
					// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
					Cover newcover = SerializationUtils.clone(cover);
					newcover.setSubcovers(subcover);
					newcover.setIsSubCover("Y");
					newcover.setSubCoverId(null);
					newcover.setSubCoverDesc(null);
					newcover.setSubCoverName(null);
					newcover.setDiscounts(null);
					newcover.setLoadings(null);
					newcover.setTaxes(null);
					subcov.add(newcover);
				}
				subcovers.put("Y", subcov);
			}

			if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
				totalcovers = subcovers.get("Y");
				totalcovers.addAll(nonSubcovers.get("N"));
			} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
				totalcovers = nonSubcovers.get("N");
			} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
				totalcovers = subcovers.get("Y");
			}

			totalcovers.stream().forEach(taxRemov);
			/*
			 * if(StringUtils.isNotBlank(engine.getVdRefNo()) &&
			 * StringUtils.isNotBlank(engine.getCdRefNo())) { //calc.setEngine(engine,
			 * retc);
			 * 
			 * 
			 * }
			 */

			/*
			 * CoverCalculator calc = new CoverCalculator(); calc.setEngine(engine, retc,
			 * commontbl, vehicles, customers, prorata, ratingutil, decimalFormat);
			 */
			PolicyCoverCalculator calc = new PolicyCoverCalculator(policytbl, ratingutil, engine, decimalFormat,
					customers,vehicles, false);
			retc.stream().forEach(calc);

			totalcovers.stream().forEach(calc);
			// remove error records
			totalcovers.removeIf(ll -> (ll.isNotsutable()));
			retc.addAll(totalcovers);
			Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
			retc.sort(comp);
		}
		// if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
		BigDecimal totalPremium = retc.stream()
				.filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId())
						&& x.getPremiumExcluedTaxLC() != null))
				.map(x -> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (totalPremium.compareTo(minimumPremium) < 0) {
			List<Tax> taxey = taxes.stream()
			        .map(tzx)
			        .filter(d -> d != null)
			        .filter(d -> {

			            // Exclude TAX ID 42 ONLY for company 100020 + product 59
			            if ("100020".equals(engine.getInsuranceId()) 
			                    && "59".equals(engine.getProductId())) {

			                
			                return !"42".equals(d.getTaxId());
			            }

			            return true;  // for all other products/companies include everything
			        })
			        .collect(Collectors.toList());
			
			

			BigDecimal difference = minimumPremium.subtract(totalPremium, MathContext.DECIMAL32);
			CreateMinimumPremium min = new CreateMinimumPremium(difference, engine, BigDecimal.ZERO, taxey);
			Cover mini = min.create();
			List<Cover> minies = new ArrayList<Cover>(1);
			minies.add(mini);
			PolicyCoverCalculator calc = new PolicyCoverCalculator(policytbl, ratingutil, engine, decimalFormat,
					customers,vehicles, false);
			
			System.out.println("DEBUG -> overallPremium: " + overallPremium);
			System.out.println("DEBUG -> productMinPremium: " + productMinPremium);
			System.out.println("DEBUG -> minPremDiff: " + minPremDiff);
			System.out.println("DEBUG -> totalPremium (before min cover): " + totalPremium);
			System.out.println("DEBUG -> engine.insuranceId: " + engine.getInsuranceId() + ", productId: " + engine.getProductId());

			System.out.println("DEBUG -> available taxes: " + taxes.stream().map(tzx).filter(Objects::nonNull)
			    .map(Tax::getTaxId).collect(Collectors.toList()));

			// calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata,
			// ratingutil, decimalFormat,drivers);
			minies.stream().forEach(calc);
			retc.add(mini);

		} else {
			retc.removeIf(t -> "945".equals(t.getCoverId()));// .stream().filter(t-> "945".equals(t.getCoverId()).de
		}

		try {

			String endtTypeId = policytbl.get(0).get("endtTypeId") == null ? ""
					: policytbl.get(0).get("endtTypeId").toString();
			if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
				// loadAndRemoveCoversForEndt(engine, retc, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
			response.setCoverList(retc);
			response.setResponse("Saved Successfully");
			response.setRequestReferenceNo(engine.getRequestReferenceNo());
			// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
			response.setVehicleId(engine.getVehicleId());
			response.setVdRefNo(engine.getPdrefno());
			response.setCdRefNo(engine.getCdRefNo());
			response.setInsuranceId(engine.getInsuranceId());
			response.setSectionId(engine.getSectionId());
			response.setCreatedBy(engine.getCreatedBy());
			response.setProductId(engine.getProductId());
			response.setMsrefno(engine.getMsrefno());
			response.setUpdateas(isEndt);
			response.setUwList(referr);
			response.setReferals(masterreferral);
			response.setLocationId(engine.getLocationId());
			response.setCoverId(StringUtils.isBlank(engine.getCoverId()) ? "99999" : engine.getCoverId());
			fservice.saveFactorRateRequestDetails(response);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<Tuple> LoadCoverPolicy(CalcEngine engine) {

		try {
			String todayInString = DD_MM_YYYY.format(new Date());

			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:99999;status:{Y,R};" + todayInString + "~effectiveDateStart&effectiveDateEnd;"
					+ "agencyCode:" + engine.getAgencyCode() + ";branchCode:99999;";

			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:99999;status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;agencyCode:99999;branchCode:99999;";

			SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);

			SpecCriteria criteria = null;

			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
			List<Long> count = crservice.getCount(criteria, 0, 50);
			if (!count.isEmpty()) {
				Long countrec = count.get(0);
				if (countrec > 0) {
					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
					for (Tuple t : specific) {
						commonResult.removeIf(c -> c.get("coverId").toString().equals(t.get("coverId").toString()));
						commonResult.add(t);
					}
				}

			}

			return commonResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void loadOnetimetablePolicy(CalcEngine engine) {

		try {
			SpecCriteria criteria = null;
			String search = "pdRefno:" + Long.valueOf(engine.getPdrefno()) + ";";
			criteria = crservice.createCriteria(MsPolicyDetails.class, search, "pdRefno");
			policytbl = crservice.getResult(criteria, 0, 50);
			String cdRefno = policytbl.get(0).get("cdRefno").toString();
			search = "cdRefno:" + cdRefno + ";";
			criteria = crservice.createCriteria(MsCustomerDetails.class, search, "cdRefno");
			customers = crservice.getResult(criteria, 0, 50);

			String todayInString = DD_MM_YYYY.format(new Date());

			search = "companyId:" + engine.getInsuranceId() + ";status:Y;" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;productId:" + engine.getProductId() + ";";
			criteria = crservice.createCriteria(CompanyProductMaster.class, search, "productId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);
			if (result != null && result.size() > 0) {
				minimumPremium = result.get(0).get("minimumPremium") == null ? BigDecimal.ZERO
						: new BigDecimal(result.get(0).get("minimumPremium").toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public EserviceMotorDetailsSaveRes policyReferralCalc(CalcEngine engine) {
		try {

			List<FactorRateRequestDetails> factors = repository
					.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
							engine.getRequestReferenceNo(), Integer.valueOf(99999),
							Integer.valueOf(engine.getProductId()), Integer.valueOf(99999));
			String isEndt = null;
			List<UWReferrals> referr = null;
			List<MasterReferal> masterreferral = null;
			List<Cover> retc = new ArrayList<Cover>();
			String promocode = "";
			loadOnetimetablePolicy(engine);
			String currencyId = policytbl.get(0).get("currency") == null ? "TTT"
					: policytbl.get(0).get("currency").toString();

			String decimalDigits = ratingutil.currencyDecimalFormat(engine.getInsuranceId(), currencyId);
			String stringFormat = "%0" + decimalDigits + "d";
			String decimalLength = decimalDigits.equals("0") ? "" : String.format(stringFormat, 0L);
			String pattern = StringUtils.isBlank(decimalLength) ? "#####0" : "#####0." + decimalLength;
			decimalFormat = new DecimalFormat(pattern);

			List<Tuple> totalcoverstuple = LoadCoverPolicy(engine);

			List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
			TaxUtils tzx = new TaxUtils(BigDecimal.ZERO, "");
			long policyDays = ratingutil.calculatePolicyDays(engine);
			List<Tuple> resolvedTaxes = ratingutil.resolveTaxByPolicyDays(taxes, policyDays);
			List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
			TaxRemover taxRemov = new TaxRemover(excludedTaxes, null);

			List<String> dependedcovers = new ArrayList<String>();
			dependedcovers.add("N");
			dependedcovers.add("Y");
			for (String dependcover : dependedcovers) {
				List<Cover> totalcovers = new ArrayList<Cover>();
				List<Tuple> covers = totalcoverstuple.stream()
						.filter(t -> dependcover.equals(t.get("dependentCoverYn").toString()))
						.collect(Collectors.toList());
				List<Discount> discounts = null;
				List<Loading> loadings = null;

				if (totalcoverstuple != null && totalcoverstuple.size() > 0) {
					DiscountFromFactor discountUtil = new DiscountFromFactor();
					discounts = factors.stream().map(discountUtil).filter(d -> d != null).collect(Collectors.toList());
					LoadingFromFactor loadingtuils = new LoadingFromFactor();
					loadings = factors.stream().map(loadingtuils).filter(d -> d != null).collect(Collectors.toList());

//					SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
//							engine.getPolicyEndDate(), promocode);
//					discounts = totalcoverstuple.stream().map(discountUtil).filter(d -> d != null)
//							.collect(Collectors.toList());
//					discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
//					SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
//							engine.getPolicyEndDate());
//					loadings = totalcoverstuple.stream().map(loadingtuils).filter(d -> d != null)
//							.collect(Collectors.toList());
				}

				SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
						engine.getPolicyEndDate());
				Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!nonSubcovers.isEmpty()) {
					List<Cover> noncovers = nonSubcovers.get("N");
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							c.setDiscounts(ds);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							c.setLoadings(ds);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = resolvedTaxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
							}
						}
					}
				}

				splitsub = new SplitSubCoverUtil("Y", engine.getEffectiveDate(), engine.getPolicyEndDate());
				Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!subcovers.isEmpty()) {
					List<Cover> noncovers = subcovers.get("Y"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							c.setDiscounts(dss);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							c.setLoadings(dss);
						}
					}
					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = resolvedTaxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
							}
						}
					}

					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
							.collect(Collectors.toList());
					List<Cover> subcov = new ArrayList<Cover>();
					for (Cover cover : d) {
						List<Cover> subcover = noncovers.stream()
								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
						subcover.stream().forEach(s -> s.setIsSubCover("N"));
						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
						Cover newcover = SerializationUtils.clone(cover);
						newcover.setSubcovers(subcover);
						newcover.setIsSubCover("Y");
						newcover.setSubCoverId(null);
						newcover.setSubCoverDesc(null);
						newcover.setSubCoverName(null);
						newcover.setDiscounts(null);
						newcover.setLoadings(null);
						newcover.setTaxes(null);
						subcov.add(newcover);
					}
					subcovers.put("Y", subcov);
				}

				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
					totalcovers.addAll(nonSubcovers.get("N"));
				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
					totalcovers = nonSubcovers.get("N");
				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
				}

				totalcovers.stream().forEach(taxRemov);

				PolicyCoverCalculator calc = new PolicyCoverCalculator(policytbl, ratingutil, engine, decimalFormat,
						customers,vehicles, false);
				retc.stream().forEach(calc);

				totalcovers.stream().forEach(calc);
				// remove error records
				totalcovers.removeIf(ll -> (ll.isNotsutable()));
				retc.addAll(totalcovers);
				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
				retc.sort(comp);
			}
			BigDecimal totalPremium = retc.stream()
					.filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId())
							&& x.getPremiumExcluedTaxLC() != null))
					.map(x -> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO, BigDecimal::add);
			if (totalPremium.compareTo(minimumPremium) < 0) {
//				List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null).collect(Collectors.toList());
//				BigDecimal difference = minimumPremium.subtract(totalPremium, MathContext.DECIMAL32);
//				CreateMinimumPremium min = new CreateMinimumPremium(difference, engine, BigDecimal.ZERO, taxey);
//				Cover mini = min.create();
//				List<Cover> minies = new ArrayList<Cover>(1);
//				minies.add(mini);
//				PolicyCoverCalculator calc = new PolicyCoverCalculator(policytbl, ratingutil, engine, decimalFormat,
//						customers, false);
//				minies.stream().forEach(calc);
//				retc.add(mini);

			} else {
				retc.removeIf(t -> "945".equals(t.getCoverId()));// .stream().filter(t-> "945".equals(t.getCoverId()).de
			}
			retc.stream().filter(r -> r.getRequestReferenceNo().equalsIgnoreCase(engine.getRequestReferenceNo())
					&& r.getProductId().equalsIgnoreCase(engine.getProductId())
					&& r.getVehicleId().equalsIgnoreCase("99999") && r.getSectionId().equalsIgnoreCase("99999"));

			try {
				EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
				response.setCoverList(retc);
				response.setResponse("Saved Successfully");
				response.setRequestReferenceNo(engine.getRequestReferenceNo());
				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
				response.setVehicleId(engine.getVehicleId());
				response.setVdRefNo(engine.getPdrefno());
				response.setCdRefNo(engine.getCdRefNo());
				response.setInsuranceId(engine.getInsuranceId());
				response.setSectionId(engine.getSectionId());
				response.setCreatedBy(engine.getCreatedBy());
				response.setProductId(engine.getProductId());
				response.setMsrefno(engine.getMsrefno());
				response.setUpdateas(isEndt);
				response.setUwList(referr);
				response.setReferals(masterreferral);
				response.setLocationId(engine.getLocationId());
				response.setCoverId(StringUtils.isBlank(engine.getCoverId()) ? "99999" : engine.getCoverId());
				response.setVdRefNo(engine.getVdRefNo());
				fservice.saveFactorRateRequestDetails(response);
				return response;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Date getZeroTimeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		return date;
	}
	
	@Override
	public List<EserviceMotorDetailsSaveRes> getCalc(CalcEngine request, String token) {
		List<EserviceMotorDetailsSaveRes> resList = new ArrayList<EserviceMotorDetailsSaveRes>();
		try {
			Integer locationId = 0;
			String riskId = "";
			String sectionId = "";

			System.out.println("Calculator Calling Api");

			List<EserviceMotorDetails> motorList = eservicemotorRepo
					.findByRequestReferenceNo(request.getRequestReferenceNo());
			if (!motorList.isEmpty()) {
				for (EserviceMotorDetails data : motorList) {
					CalcEngine engine = new CalcEngine();
					engine.setLocationId(data.getLocationId() == null ? "1" : data.getLocationId().toString());
					engine.setBranchCode(data.getBranchCode());
					engine.setInsuranceId(data.getCompanyId());
					engine.setSectionId(data.getSectionId());
					engine.setProductId(data.getProductId());
					engine.setMsrefno(data.getMsRefno().toString());
					engine.setCdRefNo(data.getCdRefno().toString());
					engine.setVdRefNo(data.getVdRefNo().toString());
					engine.setCreatedBy(data.getCreatedBy());
					engine.setRequestReferenceNo(data.getRequestReferenceNo());
					engine.setEffectiveDate(request.getEffectiveDate() == null ? data.getPolicyStartDate()
							: request.getEffectiveDate());
					engine.setPolicyEndDate(
							request.getPolicyEndDate() == null ? data.getPolicyEndDate() : request.getPolicyEndDate());
					engine.setCoverModification(
							StringUtils.isBlank(request.getCoverModification()) ? "N" : request.getCoverModification());
					engine.setVehicleId(data.getRiskId().toString());
					engine.setCoverId(data.getCoverId() == null ? "0" : data.getCoverId().toString());
					engine.setAgencyCode(data.getAgencyCode());
					engine.setEndtTypeId(String.valueOf(data.getEndorsementType()));
//					System.out.println((new StringBuilder("Json Req==>")).append((new Gson()).toJson(engine)).toString());
					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
					System.out.println("Calculator Request -->Vehicle Id " + data.getRiskId() + " \nCover Id : "
							+ data.getCoverId() + "\nRequest -->  " + objectMapper.writeValueAsString(engine));
					EserviceMotorDetailsSaveRes res = calculator(engine, token);
					resList.add(res);
				}
			} else {

				List<EserviceSectionDetails> secList = new ArrayList<EserviceSectionDetails>();
	         	if(StringUtils.isNotBlank(request.getSectionId()) && StringUtils.isNotBlank(request.getLocationId()))
	         	{
	       		Integer loc =Integer.valueOf(request.getLocationId());
              	secList = esSecRepo.findByRequestReferenceNoAndSectionIdAndLocationId(request.getRequestReferenceNo(),request.getSectionId(),loc);
        		}
              	else
                     	{
                     	 secList = esSecRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
                       	}
				if (secList != null) {
					Set<Integer> findlocationid = secList.stream().map(EserviceSectionDetails::getLocationId).distinct()
							.collect(Collectors.toSet());
					System.out.println("Total Location Ids :" + findlocationid);
			
					if (secList != null && !secList.isEmpty()) {

					    EserviceSectionDetails firstSection = secList.get(0);
					    String companyId = firstSection.getCompanyId();
					    String productId = firstSection.getProductId();

					    System.out.println("Detected companyId=" + companyId + " productId=" + productId);

					    if ("100053".equals(companyId) && "117".equals(productId)) {
					        return handlePropertyRequest(request, token);
					    }

					} else {
					    System.out.println("No Section Details Found for requestReferenceNo : " + request.getRequestReferenceNo());
					}
					if (findlocationid != null) {
						for (Integer data : findlocationid) {
							locationId = data;
							System.out.println("Location Id " + data);
							List<EserviceSectionDetails> secFilter = secList.stream()
									.filter(o -> o.getLocationId().equals(data)).collect(Collectors.toList());
							System.out.println("Section Count: "+secFilter.size());
							for (EserviceSectionDetails s : secFilter) {
								String yn=StringUtils.isBlank(request.getCoverModification()) ? "N": request.getCoverModification();
								if ("A".equalsIgnoreCase(s.getProductType())) {
									List<EserviceBuildingDetails> buildingdata = eservicebuildingRepo.findByRequestReferenceNoAndLocationId(request.getRequestReferenceNo(),data);
									if (!buildingdata.isEmpty() && buildingdata.size() > 0 && buildingdata != null) {
										List<EserviceBuildingDetails> building = buildingdata.stream()
												.filter(o -> o.getLocationId().equals(data)
														&& o.getSectionId().equals(s.getSectionId())
														&& o.getCoverId().toString().equals(s.getCoverId().toString())
														&& o.getRiskId().equals(s.getRiskId()))
												.collect(Collectors.toList());

										
										for (EserviceBuildingDetails bd : building) {
											System.out.println(  "Start -----------> ReqRef===>" + bd.getRequestReferenceNo() +
													"SectionId ==>"+bd.getSectionId()+ "CoverId==>" + bd.getCoverId());
											CalcEngine engine = new CalcEngine();
											
											
											engine.setLocationId(bd.getLocationId().toString());
											engine.setBranchCode(bd.getBranchCode());
											engine.setInsuranceId(bd.getCompanyId());
											engine.setSectionId(bd.getSectionId());
											engine.setProductId(bd.getProductId());
											engine.setMsrefno(bd.getMsRefno().toString());
											engine.setCdRefNo(bd.getCdRefno().toString());
											engine.setVdRefNo(bd.getVdRefno().toString());
											engine.setCreatedBy(bd.getCreatedBy());
											engine.setRequestReferenceNo(bd.getRequestReferenceNo());
											engine.setEffectiveDate(bd.getEndorsementEffdate() == null ? bd.getPolicyStartDate(): bd.getEndorsementEffdate());
											engine.setPolicyEndDate(request.getPolicyEndDate() == null ? bd.getPolicyEndDate(): request.getPolicyEndDate());
											if ("890".equalsIgnoreCase(String.valueOf(bd.getEndorsementType()))) {
												engine.setCoverModification(
														StringUtils.isBlank(s.getEndtLocationyn()) ? "N" :yn);
											} else {
												engine.setCoverModification(yn);
											}
											engine.setEndtOpdt(
												    bd.getEndtoptd() != null
												        ? bd.getEndtoptd().toString()
												        : null
												);

												engine.setEndtTypeId(
												    bd.getEndorsementType() != null
												        ? bd.getEndorsementType().toString()
												        : null
												);
											engine.setVehicleId(bd.getRiskId().toString());
											List<SectionCoverMaster> list = getSectionCoverDetails(bd.getProductId(),
													bd.getSectionId(), bd.getCompanyId());
											if (CollectionUtils.isEmpty(list)) {
												engine.setCoverId(
														bd.getCoverId() == null ? "0" : bd.getCoverId().toString());
											}
											// engine.setCoverId(bd.getCoverId()==null?"0":bd.getCoverId().toString());
											engine.setAgencyCode(bd.getAgencyCode());
//									System.out.println((new StringBuilder("Json Req==>")).append((new Gson()).toJson(engine)).toString());
											ObjectMapper objectMapper = new ObjectMapper();
											objectMapper
													.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
											System.out.println("Calculator Request -->Vehicle Id " + bd.getRiskId()
													+ " \nCover Id : " + bd.getCoverId() + "\nRequest -->  "
													+ objectMapper.writeValueAsString(engine));

											// = calculator( engine, token) ;
											EserviceMotorDetailsSaveRes res = calculator(engine, token);
										//	String url = calEngine;
										//	EserviceMotorDetailsSaveRes res = restTemplateApiService.callEngine(url,
										//			engine, token);


											// new Thread().sleep(10000L);
											 resList.add(res);

										//	new Thread().sleep(7000L);
											resList.add(res);
											
											System.out.println(  "End -----------> ReqRef===>" + bd.getRequestReferenceNo() +
													"SectionId ==>"+bd.getSectionId()+ "CoverId==>" + bd.getCoverId());

										}
									}
								}else if ("H".equalsIgnoreCase(s.getProductType())
								        && s.getProductId().equalsIgnoreCase("4")) {

								    return handleTravelRequest(request, token);
								} 
								
								else if ("H".equalsIgnoreCase(s.getProductType())
										&& (!s.getProductId().equalsIgnoreCase(travelProductId))) {
									List<EserviceCommonDetails> comdata = eservicecommonRepo.findByRequestReferenceNoAndLocationId(request.getRequestReferenceNo(),
													data);
									if (!comdata.isEmpty() && comdata.size() > 0 && comdata != null) {
										List<EserviceCommonDetails> common = comdata.stream()
												.filter(o -> o.getLocationId().equals(data)
														&& o.getSectionId().equals(s.getSectionId())
														&& o.getCoverId().toString().equals(s.getCoverId().toString())
														&& o.getRiskId().equals(s.getRiskId()))
												.collect(Collectors.toList());
										for (EserviceCommonDetails cd : common) {
											CalcEngine engine = new CalcEngine();
											engine.setLocationId(cd.getLocationId().toString());
											engine.setBranchCode(cd.getBranchCode());
											engine.setInsuranceId(cd.getCompanyId());
											engine.setSectionId(cd.getSectionId());
											engine.setProductId(cd.getProductId());
											engine.setMsrefno(cd.getMsRefno().toString());
											engine.setCdRefNo(cd.getCdRefno().toString());
											engine.setVdRefNo(cd.getVdRefNo().toString());
											engine.setCreatedBy(cd.getCreatedBy());
											engine.setRequestReferenceNo(cd.getRequestReferenceNo());
											engine.setEffectiveDate(cd.getEndorsementEffdate() == null ? cd.getPolicyStartDate(): cd.getEndorsementEffdate());
											engine.setPolicyEndDate(request.getPolicyEndDate() == null ? cd.getPolicyEndDate(): request.getPolicyEndDate());
											if ("890".equalsIgnoreCase(String.valueOf(s.getEndorsementType()))) {
												engine.setCoverModification(
														StringUtils.isBlank(s.getEndtLocationyn()) ? "N" :yn);
											} else {
												engine.setCoverModification(yn);
											}
											engine.setVehicleId(cd.getRiskId().toString());
											List<SectionCoverMaster> list = getSectionCoverDetails(cd.getProductId(),
													cd.getSectionId(), cd.getCompanyId());
											if (CollectionUtils.isEmpty(list)) {
												engine.setCoverId(
														cd.getCoverId() == null ? "0" : cd.getCoverId().toString());
											}
											// engine.setCoverId(cd.getCoverId()==null?"0":cd.getCoverId().toString());
											engine.setAgencyCode(cd.getAgencyCode());
//							System.out.println((new StringBuilder("Json Req==>")).append((new Gson()).toJson(engine)).toString());
											ObjectMapper objectMapper = new ObjectMapper();
											objectMapper
													.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
											System.out.println("Calculator Request -->Vehicle Id " + cd.getRiskId()
													+ " \nCover Id : " + cd.getCoverId() + "\nRequest -->  "
													+ objectMapper.writeValueAsString(engine));
//											String url = calEngine;
//											EserviceMotorDetailsSaveRes res = restTemplateApiService.callEngine(url,
//													engine, token);
											EserviceMotorDetailsSaveRes res = calculator(engine, token);
											resList.add(res);
										}
									}
								}

							}
						}
					}

				} else {
					System.out.println("Section List is Empty for this  Request Reference Number : "
							+ request.getRequestReferenceNo());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resList;
	}

	

//	@Override
//	public List<EserviceMotorDetailsSaveRes> getCalc(CalcEngine request, String token) {
//		List<EserviceMotorDetailsSaveRes> resList = new ArrayList<EserviceMotorDetailsSaveRes>();
//		try {
//			Integer locationId = 0;
//			String riskId = "";
//			String sectionId = "";
//
//			System.out.println("Calculator Calling Api");
//
//			List<EserviceMotorDetails> motorList = eservicemotorRepo
//					.findByRequestReferenceNo(request.getRequestReferenceNo());
//			if (!motorList.isEmpty()) {
//				for (EserviceMotorDetails data : motorList) {
//					CalcEngine engine = new CalcEngine();
//					engine.setLocationId(data.getLocationId() == null ? "1" : data.getLocationId().toString());
//					engine.setBranchCode(data.getBranchCode());
//					engine.setInsuranceId(data.getCompanyId());
//					engine.setSectionId(data.getSectionId());
//					engine.setProductId(data.getProductId());
//					engine.setMsrefno(data.getMsRefno().toString());
//					engine.setCdRefNo(data.getCdRefno().toString());
//					engine.setVdRefNo(data.getVdRefNo().toString());
//					engine.setCreatedBy(data.getCreatedBy());
//					engine.setRequestReferenceNo(data.getRequestReferenceNo());
//					engine.setEffectiveDate(request.getEffectiveDate() == null ? data.getPolicyStartDate()
//							: request.getEffectiveDate());
//					engine.setPolicyEndDate(
//							request.getPolicyEndDate() == null ? data.getPolicyEndDate() : request.getPolicyEndDate());
//					engine.setCoverModification(
//							StringUtils.isBlank(request.getCoverModification()) ? "N" : request.getCoverModification());
//					engine.setVehicleId(data.getRiskId().toString());
//					engine.setCoverId(data.getCoverId() == null ? "0" : data.getCoverId().toString());
//					engine.setAgencyCode(data.getAgencyCode());
//					engine.setEndtTypeId(String.valueOf(data.getEndorsementType()));
////					System.out.println((new StringBuilder("Json Req==>")).append((new Gson()).toJson(engine)).toString());
//					ObjectMapper objectMapper = new ObjectMapper();
//					objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
//					System.out.println("Calculator Request -->Vehicle Id " + data.getRiskId() + " \nCover Id : "
//							+ data.getCoverId() + "\nRequest -->  " + objectMapper.writeValueAsString(engine));
//					EserviceMotorDetailsSaveRes res = calculator(engine, token);
//					resList.add(res);
//				}
//			} else {
//
//				List<EserviceSectionDetails> secList = new ArrayList<EserviceSectionDetails>();
//	         	if(StringUtils.isNotBlank(request.getSectionId()) && StringUtils.isNotBlank(request.getLocationId()))
//	         	{
//	       		Integer loc =Integer.valueOf(request.getLocationId());
//              	secList = esSecRepo.findByRequestReferenceNoAndSectionIdAndLocationId(request.getRequestReferenceNo(),request.getSectionId(),loc);
//        		}
//              	else
//                     	{
//                     	 secList = esSecRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
//                       	}
//				if (secList != null) {
//					Set<Integer> findlocationid = secList.stream().map(EserviceSectionDetails::getLocationId).distinct()
//							.collect(Collectors.toSet());
//					System.out.println("Total Location Ids :" + findlocationid);
//			
//					if (secList != null && !secList.isEmpty()) {
//
//					    EserviceSectionDetails firstSection = secList.get(0);
//					    String companyId = firstSection.getCompanyId();
//					    String productId = firstSection.getProductId();
//
//					    System.out.println("Detected companyId=" + companyId + " productId=" + productId);
//
//					    if ("100053".equals(companyId) && "117".equals(productId)) {
//					        return handlePropertyRequest(request, token);
//					    }
//
//					} else {
//					    System.out.println("No Section Details Found for requestReferenceNo : " + request.getRequestReferenceNo());
//					}
//					if (findlocationid != null) {
//						for (Integer data : findlocationid) {
//							locationId = data;
//							System.out.println("Location Id " + data);
//							List<EserviceSectionDetails> secFilter = secList.stream()
//									.filter(o -> o.getLocationId().equals(data)).collect(Collectors.toList());
//							System.out.println("Section Count: "+secFilter.size());
//							Map<String, EserviceSectionDetails> uniqueSections = secFilter.stream()
//							        .collect(Collectors.toMap(
//							                sec -> sec.getSectionId() + "_" + sec.getRiskId(),
//							                sec -> sec,
//							                (existing, replacement) -> existing  // keep first occurrence
//							        ));
//
//							System.out.println("Unique Section+Risk combos: " + uniqueSections.size());
//
//							for (EserviceSectionDetails s : uniqueSections.values()) {
//								
//								String yn=StringUtils.isBlank(request.getCoverModification()) ? "N": request.getCoverModification();
//								if ("A".equalsIgnoreCase(s.getProductType())) {
//
//								    List<EserviceBuildingDetails> buildingdata = eservicebuildingRepo
//								            .findByRequestReferenceNoAndLocationId(request.getRequestReferenceNo(), data);
//
//								    if (buildingdata != null && !buildingdata.isEmpty()) {
//
//								        List<EserviceBuildingDetails> building = buildingdata.stream()
//								                .filter(o -> o.getLocationId().equals(data)
//								                        && o.getSectionId().equals(s.getSectionId())
//								                        
//								                        && o.getRiskId().equals(s.getRiskId()))
//								                .collect(Collectors.toList());
//
//								        if (!building.isEmpty()) {
//								            EserviceBuildingDetails first = building.get(0);
//								            yn = StringUtils.isBlank(request.getCoverModification()) ? "N"
//								                    : request.getCoverModification();
//
//								            
//								            CalcEngineBatch batchEngine = new CalcEngineBatch();
//								            batchEngine.setLocationId(first.getLocationId().toString());
//								            batchEngine.setBranchCode(first.getBranchCode());
//								            batchEngine.setInsuranceId(first.getCompanyId());
//								            batchEngine.setSectionId(first.getSectionId());
//								            batchEngine.setProductId(first.getProductId());
//								            batchEngine.setCreatedBy(first.getCreatedBy());
//								            batchEngine.setRequestReferenceNo(first.getRequestReferenceNo());
//								            batchEngine.setEffectiveDate(first.getEndorsementEffdate() == null
//								                    ? first.getPolicyStartDate() : first.getEndorsementEffdate());
//								            batchEngine.setPolicyEndDate(request.getPolicyEndDate() == null
//								                    ? first.getPolicyEndDate() : request.getPolicyEndDate());
//								            batchEngine.setCoverModification(
//								                    "890".equalsIgnoreCase(String.valueOf(first.getEndorsementType()))
//								                    ? (StringUtils.isBlank(s.getEndtLocationyn()) ? "N" : yn) : yn);
//								            batchEngine.setEndtOpdt(first.getEndtoptd() != null
//								                    ? first.getEndtoptd().toString() : null);
//								            batchEngine.setEndtTypeId(first.getEndorsementType() != null
//								                    ? first.getEndorsementType().toString() : null);
//								            batchEngine.setVehicleId(first.getRiskId().toString());
//								            batchEngine.setAgencyCode(first.getAgencyCode());
//
//								            // Parallel index-aligned lists
//								            List<String> coverIds = new ArrayList<>();
//								            List<String> vdRefNos = new ArrayList<>();
//								            List<String> cdRefNos = new ArrayList<>();
//								            List<String> msRefNos = new ArrayList<>();
//
//								            for (EserviceBuildingDetails bd : building) {
//
//								                coverIds.add(bd.getCoverId() != null
//								                        ? bd.getCoverId().toString()
//								                        : "0");
//
//								                vdRefNos.add(bd.getVdRefno() != null
//								                        ? bd.getVdRefno().toString()
//								                        : "0");
//
//								                cdRefNos.add(bd.getCdRefno() != null
//								                        ? bd.getCdRefno().toString()
//								                        : "0");
//
//								                msRefNos.add(bd.getMsRefno() != null
//								                        ? bd.getMsRefno().toString()
//								                        : "0");
//								            }
//
//								            batchEngine.setCoverIds(coverIds);
//								            batchEngine.setVdRefNos(vdRefNos);
//								            batchEngine.setCdRefNos(cdRefNos);
//								            batchEngine.setMsRefNos(msRefNos);
//
//								            System.out.println("A-type batch: sectionId=" + first.getSectionId()
//								                    + " covers=" + coverIds + " location=" + data);
//
//								            EserviceMotorDetailsSaveRes res = calculatorBatch(batchEngine, token);
//								            resList.add(res);
//								        }
//								    }
//								}else if ("H".equalsIgnoreCase(s.getProductType())
//								        && s.getProductId().equalsIgnoreCase("4")) {
//
//								    return handleTravelRequest(request, token);
//								} 
//								
//								else if ("H".equalsIgnoreCase(s.getProductType())
//								        && (!s.getProductId().equalsIgnoreCase(travelProductId))) {
//
//								    List<EserviceCommonDetails> comdata = eservicecommonRepo
//								            .findByRequestReferenceNoAndLocationId(request.getRequestReferenceNo(), data);
//
//								    if (comdata != null && !comdata.isEmpty()) {
//
//								        List<EserviceCommonDetails> common = comdata.stream()
//								                .filter(o -> o.getLocationId().equals(data)
//								                        && o.getSectionId().equals(s.getSectionId())
//								                        && o.getCoverId().toString().equals(s.getCoverId().toString())
//								                        && o.getRiskId().equals(s.getRiskId()))
//								                .collect(Collectors.toList());
//
//								        if (!common.isEmpty()) {
//								            EserviceCommonDetails first = common.get(0);
//								            yn = StringUtils.isBlank(request.getCoverModification()) ? "N"
//								                    : request.getCoverModification();
//
//								            CalcEngineBatch batchEngine = new CalcEngineBatch();
//								            batchEngine.setLocationId(first.getLocationId().toString());
//								            batchEngine.setBranchCode(first.getBranchCode());
//								            batchEngine.setInsuranceId(first.getCompanyId());
//								            batchEngine.setSectionId(first.getSectionId());
//								            batchEngine.setProductId(first.getProductId());
//								            batchEngine.setCreatedBy(first.getCreatedBy());
//								            batchEngine.setRequestReferenceNo(first.getRequestReferenceNo());
//								            batchEngine.setEffectiveDate(first.getEndorsementEffdate() == null
//								                    ? first.getPolicyStartDate() : first.getEndorsementEffdate());
//								            batchEngine.setPolicyEndDate(request.getPolicyEndDate() == null
//								                    ? first.getPolicyEndDate() : request.getPolicyEndDate());
//								            batchEngine.setCoverModification(
//								                    "890".equalsIgnoreCase(String.valueOf(s.getEndorsementType()))
//								                    ? (StringUtils.isBlank(s.getEndtLocationyn()) ? "N" : yn) : yn);
//								            batchEngine.setVehicleId(first.getRiskId().toString());
//								            batchEngine.setAgencyCode(first.getAgencyCode());
//
//								            List<String> coverIds = new ArrayList<>();
//								            List<String> vdRefNos = new ArrayList<>();
//								            List<String> cdRefNos = new ArrayList<>();
//								            List<String> msRefNos = new ArrayList<>();
//
//								            for (EserviceCommonDetails cd : common) {
//								                List<SectionCoverMaster> scmList = getSectionCoverDetails(
//								                        cd.getProductId(), cd.getSectionId(), cd.getCompanyId());
//								                String resolvedCoverId = CollectionUtils.isEmpty(scmList)
//								                        ? (cd.getCoverId() == null ? "0" : cd.getCoverId().toString())
//								                        : "0";
//
//								                coverIds.add(resolvedCoverId);
//								                vdRefNos.add(cd.getVdRefNo().toString());
//								                cdRefNos.add(cd.getCdRefno().toString());
//								                msRefNos.add(cd.getMsRefno().toString());
//								            }
//
//								            batchEngine.setCoverIds(coverIds);
//								            batchEngine.setVdRefNos(vdRefNos);
//								            batchEngine.setCdRefNos(cdRefNos);
//								            batchEngine.setMsRefNos(msRefNos);
//
//								            System.out.println("H-type batch: sectionId=" + first.getSectionId()
//								                    + " covers=" + coverIds + " location=" + data);
//
//								            EserviceMotorDetailsSaveRes res = calculatorBatch(batchEngine, token);
//								            resList.add(res);
//								        }
//								    }
//								}
//
//							}
//						}
//					}
//
//				} else {
//					System.out.println("Section List is Empty for this  Request Reference Number : "
//							+ request.getRequestReferenceNo());
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return resList;
//	}

	public List<SectionCoverMaster> getSectionCoverDetails(String product, String section, String company) {
		List<SectionCoverMaster> resList = new ArrayList<SectionCoverMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();

			// Find Latest Record
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<SectionCoverMaster> query2 = cb2.createQuery(SectionCoverMaster.class);

			// Find All
			Root<SectionCoverMaster> b2 = query2.from(SectionCoverMaster.class);

			// Amed Id

			Subquery<Long> amendId2 = query2.subquery(Long.class);
			Root<SectionCoverMaster> ocpm2 = amendId2.from(SectionCoverMaster.class);
			amendId2.select(cb2.max(ocpm2.get("amendId")));
			Predicate a7 = cb2.equal(ocpm2.get("coverId"), b2.get("coverId"));
			Predicate a12 = cb2.equal(ocpm2.get("subCoverId"), b2.get("subCoverId"));
			Predicate a8 = cb2.equal(ocpm2.get("sectionId"), b2.get("sectionId"));
			Predicate a9 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
			Predicate a10 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
			Predicate a13 = cb2.equal(ocpm2.get("agencyCode"), b2.get("agencyCode"));
			Predicate a14 = cb2.equal(ocpm2.get("branchCode"), b2.get("branchCode"));
			amendId2.where(a7, a8, a9, a10, a12, a13, a14);
			query2.select(b2);

			// Order By
			List<Order> orderList2 = new ArrayList<Order>();
			orderList2.add(cb2.asc(b2.get("coverName")));

			// Where
			Predicate n4 = cb2.equal(b2.get("amendId"), amendId2);
			Predicate n6 = cb2.equal(b2.get("subCoverId"), "0");
			Predicate n7 = cb2.equal(b2.get("productId"), product);
			Predicate n14 = cb2.equal(b2.get("companyId"), company);
			Predicate n15 = cb2.equal(b2.get("sectionId"), section);
			Predicate n9 = cb2.equal(b2.get("agencyCode"), "99999");
			Predicate n12 = cb2.equal(b2.get("branchCode"), "99999");
			Predicate n13 = cb2.equal(b2.get("status"), "Y");
			Predicate n16 = cb2.equal(b2.get("dependentCoverYn"), "Y");
			query2.where(n4, n6, n7, n14, n15, n9, n12, n13, n16).orderBy(orderList2);

			// Get Result
			TypedQuery<SectionCoverMaster> result2 = em.createQuery(query2);
			resList = result2.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
		return resList;
	}

	public String findNoOfDaysInDate(Date policyStartDate, Date policyEndDate) {
		try {
			Date periodStart = policyStartDate;
			Date periodEnd = policyEndDate;
			Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
			Long daysBetween = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
			// Check Leap Year
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			boolean leapYear = LocalDate.parse(sdf.format(periodEnd)).isLeapYear();
			String diff = String.valueOf(daysBetween == 365 && leapYear == true ? daysBetween + 1 : daysBetween);
			if (Integer.parseInt(diff) < 0)
				diff = "0";
			return diff;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private EserviceMotorDetailsSaveRes handlePropertyMultiLocationCalculation(
	        CalcEngine engine, String token) {

	    LoginMaster loginMaster = loginMasterRepository.findByLoginId(engine.getCreatedBy());
	    List<UWReferrals>   referrals       = null;
	    List<MasterReferal> masterReferrals = null;

	    if (!("issuer".equalsIgnoreCase(loginMaster.getUserType())
	            && "superadmin".equalsIgnoreCase(loginMaster.getSubUserType()))) {
	        referrals = referal.underwriterReferral(engine);
	        try {
	            masterReferrals = referal.masterreferral(engine, token);
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	    }

	    try {
	        
	        loadPropertyOnetimetable(engine);
	        validateData();

	        List<Tuple> allAssetRows = new ArrayList<>(this.vehicles);

	        if (allAssetRows.isEmpty()) {
	            System.out.println("No asset rows for: " + engine.getRequestReferenceNo());
	            return null;
	        }

	        CoverCalculator rateCalc = new CoverCalculator();
	        rateCalc.setCriservice(crservice);
	        rateCalc.setEngine(engine,
	                new ArrayList<>(), commontbl, allAssetRows, 
	                customers, prorata, ratingutil, decimalFormat, drivers, new ArrayList<>());

	        double derivedRate = rateCalc.derivePropertyRate(engine);

	        System.out.println("=== Single Shared Rate for ALL locations = " + derivedRate + " ===");

	        List<Tuple> taxes               = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
	        List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
	        List<Tuple> excludedTaxes       = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);

	        engine.setSectionId("293");
	        List<Tuple> totalCoversTuple = LoadCover(engine);

	        String promocode = Optional.ofNullable(allAssetRows.get(0).get("promocode"))
	                .map(Object::toString).orElse("");

	        List<Cover> allCovers = Collections.synchronizedList(new ArrayList<>());
	        List<EserviceMotorDetailsSaveRes> allResponses =
	                Collections.synchronizedList(new ArrayList<>());

	        final List<UWReferrals>   finalReferrals       = referrals;
	        final List<MasterReferal> finalMasterReferrals = masterReferrals;
	        final double              sharedRate           = derivedRate;

	        allAssetRows.parallelStream().forEach(assetRow -> {
	            try {
	                String locationId = assetRow.get("locationId") == null
	                        ? "1" : assetRow.get("locationId").toString();
	                String coverId    = assetRow.get("coverId") == null
	                        ? "0" : assetRow.get("coverId").toString();
	                String vdRefNo    = assetRow.get("vdRefno") == null
	                        ? "" : assetRow.get("vdRefno").toString();
	                String vehicleId  = assetRow.get("riskId") == null
	                        ? "" : assetRow.get("riskId").toString();
	                String sumInsured = assetRow.get("sumInsured") == null
	                        ? "0" : assetRow.get("sumInsured").toString();

	                System.out.println("Processing → locationId=" + locationId
	                        + " | coverId=" + coverId
	                        + " | vdRefNo=" + vdRefNo
	                        + " | SI=" + sumInsured
	                        + " | rate=" + sharedRate);  

	                String periodOfInsurance = Optional.ofNullable(assetRow.get("periodOfInsurance"))
	                        .map(Object::toString).orElse("365");
	                String policyTypeId = Optional.ofNullable(assetRow.get("insuranceClass"))
	                        .map(Object::toString).orElse("99999");

	                CalcEngine rowEngine = cloneEngine(engine);
	                rowEngine.setLocationId(locationId);
	                rowEngine.setVdRefNo(vdRefNo);       
	                rowEngine.setVehicleId(vehicleId);
	                rowEngine.setCoverId(coverId);
	                rowEngine.setSectionId("293");

	                List<Tuple> rowProrata = ratingutil.loadProRataData(
	                        rowEngine, periodOfInsurance, policyTypeId);

	                List<Tuple> singleRowVehicle = List.of(assetRow);

	                List<Cover> rowCovers     = new ArrayList<>();
	                TaxUtils   rowTaxUtils   = new TaxUtils(BigDecimal.ZERO, "");
	                TaxRemover rowTaxRemover = new TaxRemover(excludedTaxes, null);

	                List<Tuple> rowCoverTuple = totalCoversTuple.stream()
	                        .filter(t -> coverId.equals(
	                                t.get("coverId") == null
	                                        ? "" : t.get("coverId").toString()))
	                        .collect(Collectors.toList());

	                List.of("N", "Y").forEach(depend ->
	                        processCoverDependentsWithRate(
	                                rowEngine,
	                                singleRowVehicle,  
	                                depend,
	                                rowCoverTuple,
	                                taxes,
	                                customerChoiceTaxes,
	                                promocode,
	                                rowTaxUtils,
	                                rowTaxRemover,
	                                rowCovers,
	                                sharedRate));      

	                loadBenefitCovers(rowEngine, rowCovers);

	                BigDecimal totalPremium = rowCovers.stream()
	                        .filter(x -> !"N".equals(x.getIsselected())
	                                && !"945".equals(x.getCoverId())
	                                && x.getPremiumExcluedTaxLC() != null)
	                        .map(Cover::getPremiumExcluedTaxLC)
	                        .reduce(BigDecimal.ZERO, BigDecimal::add);

	                if (totalPremium.compareTo(minimumPremium) < 0) {
	                    adjustMinimumPremium(rowEngine, BigDecimal.ZERO,
	                            taxes, rowTaxUtils, rowCovers);
	                } else {
	                    rowCovers.removeIf(t -> "945".equals(t.getCoverId()));
	                }

	                EserviceMotorDetailsSaveRes rowResponse =
	                        buildResponse(rowEngine, finalReferrals, finalMasterReferrals,
	                                rowCovers, null);
	                
	                if ("Y".equalsIgnoreCase(engine.getIsReferral())) {
 
	                    rowCovers.forEach(cover -> {
	                        cover.setIsReferral("Y");
	                        cover.setReferalDescription("Occupation Referral");
	                    });
	                }

	                fservice.saveFactorRateRequestDetails(rowResponse);

	                allCovers.addAll(rowCovers);
	                allResponses.add(rowResponse);

	                System.out.println("Saved → locationId=" + locationId
	                        + " | coverId=" + coverId
	                        + " | SI=" + sumInsured
	                        + " | rate=" + sharedRate
	                        + " | premium=" + totalPremium);

	            } catch (Exception e) {
	                System.err.println("Error → locationId=" + assetRow.get("locationId")
	                        + " coverId=" + assetRow.get("coverId") + " : " + e.getMessage());
	                e.printStackTrace();
	            }
	        });

	        this.vehicles = allAssetRows;

	        if (!allResponses.isEmpty()) {
	            EserviceMotorDetailsSaveRes finalResponse =
	                    allResponses.get(allResponses.size() - 1);
	            finalResponse.setCoverList(new ArrayList<>(allCovers));
	            return finalResponse;
	        }

	        return null;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	private CalcEngine cloneEngine(CalcEngine src) {
	    CalcEngine clone = new CalcEngine();
	    clone.setLocationId(src.getLocationId());
	    clone.setBranchCode(src.getBranchCode());
	    clone.setInsuranceId(src.getInsuranceId());
	    clone.setSectionId(src.getSectionId());
	    clone.setProductId(src.getProductId());
	    clone.setMsrefno(src.getMsrefno());
	    clone.setCdRefNo(src.getCdRefNo());
	    clone.setVdRefNo(src.getVdRefNo());
	    clone.setCreatedBy(src.getCreatedBy());
	    clone.setRequestReferenceNo(src.getRequestReferenceNo());
	    clone.setEffectiveDate(src.getEffectiveDate());
	    clone.setPolicyEndDate(src.getPolicyEndDate());
	    clone.setCoverModification(src.getCoverModification());
	    clone.setVehicleId(src.getVehicleId());
	    clone.setCoverId(src.getCoverId());
	    clone.setAgencyCode(src.getAgencyCode());
	    clone.setIsReferral(src.getIsReferral());
	    clone.setReferralRemarks(src.getReferralRemarks());
	    return clone;
	}
	
	public void loadPropertyOnetimetable(CalcEngine engine) {
	    try {
	        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
	                + " <---- loadPropertyOnetimetable start ---->");

	        SpecCriteria criteria = crservice.createCriteria(
	                MsCommonDetails.class,
	                "msRefno:" + engine.getMsrefno() + ";",
	                "msRefno");
	        commontbl = crservice.getResult(criteria, 0, 50);

	        if (commontbl == null || commontbl.isEmpty()) {
	            System.out.println("loadPropertyOnetimetable: commontbl empty");
	            return;
	        }

	        String cdRefno = commontbl.get(0).get("cdRefno").toString();

	        criteria = crservice.createCriteria(
	                MsAssetDetails.class,
	                "requestReferenceNo:" + engine.getRequestReferenceNo() + ";",
	                "locationId");  

	        List<Tuple> allRows = crservice.getResult(criteria, 0, 500);

	        if (allRows == null || allRows.isEmpty()) {
	            System.out.println("No assets found for: " + engine.getRequestReferenceNo());
	            vehicles = new ArrayList<>();
	            return;
	        }

	        vehicles = new ArrayList<>(allRows);

	        System.out.println("loadPropertyOnetimetable: loaded "
	                + vehicles.size() + " asset rows (location+cover combinations)");

	        vehicles.forEach(v -> System.out.println(
	                "  → locationId=" + v.get("locationId")
	                + " coverId=" + v.get("coverId")
	                + " vdRefNo=" + v.get("vdRefno")
	                + " SI=" + v.get("sumInsured")));

	        criteria = crservice.createCriteria(
	                MsCustomerDetails.class,
	                "cdRefno:" + cdRefno + ";",
	                "cdRefno");
	        customers = crservice.getResult(criteria, 0, 50);

	        if (!vehicles.isEmpty()) {
	            List<Tuple> product = ratingutil.collectProductType(engine);
	            minimumPremium = product.get(0).get("minPremium") == null
	                    ? BigDecimal.ZERO
	                    : new BigDecimal(product.get(0).get("minPremium").toString());

	            engine.setCoverId("0");

	            String currencyId = vehicles.get(0).get("currency") == null
	                    ? "TTT" : vehicles.get(0).get("currency").toString();

	            String decimalDigits = ratingutil.currencyDecimalFormat(
	                    engine.getInsuranceId(), currencyId);
	            String stringFormat  = "%0" + decimalDigits + "d";
	            String decimalLength = decimalDigits.equals("0")
	                    ? "" : String.format(stringFormat, 0L);
	            String pattern = StringUtils.isBlank(decimalLength)
	                    ? "#####0" : "#####0." + decimalLength;
	            decimalFormat = new DecimalFormat(pattern);
	        }

	        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
	                + " <---- loadPropertyOnetimetable end ---->");

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	private void processCoverDependentsWithRate(CalcEngine engine,
	        List<Tuple> currentVehicle,
	        String dependCover, List<Tuple> totalCoversTuple,
	        List<Tuple> taxes, List<Tuple> customerChoiceTaxes,
	        String promocode, TaxUtils taxUtils, TaxRemover taxRemover,
	        List<Cover> resultCovers, double derivedRate) {

	    List<Tuple> covers = totalCoversTuple.parallelStream()
	            .filter(t -> dependCover.equals(t.get("dependentCoverYn").toString()))
	            .toList();

	    if (covers.isEmpty()) return;

	    List<Discount> discounts = generateDiscounts(engine, covers, promocode);
	    List<Loading> loadings   = generateLoadings(engine, covers);

	    Map<String, List<Cover>> nonSubCovers = splitCovers(engine, covers, "N");
	    List<Cover> nonSubCoverList = nonSubCovers.get("N");
	    attachDiscountsLoadingsTaxes(nonSubCoverList, discounts, loadings, taxes, taxUtils);

	    SplitSubCoverUtil splitY = new SplitSubCoverUtil("Y",
	            engine.getEffectiveDate(), engine.getPolicyEndDate());
	    List<Cover> childSubCovers = covers.parallelStream()
	            .map(splitY).filter(Objects::nonNull).collect(Collectors.toList());
	    attachDiscountsLoadingsTaxes(childSubCovers, discounts, loadings, taxes, taxUtils);
	    List<Cover> parentSubCovers = buildParentsWithSubcovers(childSubCovers);

	    List<Cover> mergedCovers = new ArrayList<>();
	    if (nonSubCoverList != null) mergedCovers.addAll(nonSubCoverList);
	    if (parentSubCovers   != null) mergedCovers.addAll(parentSubCovers);

	    mergedCovers = mergedCovers.stream()
	            .peek(taxRemover).collect(Collectors.toList());

	    mergedCovers.forEach(cover -> {
	        if ("Y".equals(cover.getIsSubCover())) {
	            if (cover.getSubcovers() != null)
	                cover.getSubcovers().forEach(sub -> sub.setRate(derivedRate));
	        } else {
	            cover.setRate(derivedRate);
	        }
	    });

	    CoverCalculator calc = new CoverCalculator();
	    calc.setEngine(engine, resultCovers, commontbl, currentVehicle, customers, prorata,
	            ratingutil, decimalFormat, drivers, customerChoiceTaxes);

	    mergedCovers.parallelStream().forEach(calc);
	    mergedCovers.removeIf(Cover::isNotsutable);

	    resultCovers.addAll(mergedCovers);
	    resultCovers.sort(Comparator.comparing(Cover::getCoverageType));
	}
	
	
	private List<EserviceMotorDetailsSaveRes> handlePropertyRequest(
	        CalcEngine request, String token) {

	    List<EserviceMotorDetailsSaveRes> resList = new ArrayList<>();

	    try {
	        List<EserviceBuildingDetails> allBuildings = eservicebuildingRepo
	                .findByRequestReferenceNo(request.getRequestReferenceNo());

	        if (allBuildings == null || allBuildings.isEmpty()) {
	            System.out.println("No building records found for: "
	                    + request.getRequestReferenceNo());
	            return resList;
	        }

	        EserviceBuildingDetails first = allBuildings.get(0);

	        CalcEngine engine = new CalcEngine();
	        engine.setInsuranceId(first.getCompanyId());           
	        engine.setProductId(first.getProductId());             
	        engine.setBranchCode(first.getBranchCode());
	        engine.setSectionId(first.getSectionId());
	        engine.setMsrefno(first.getMsRefno().toString());
	        engine.setCdRefNo(first.getCdRefno().toString());
	        engine.setVdRefNo(first.getVdRefno().toString());
	        engine.setCreatedBy(first.getCreatedBy());
	        engine.setRequestReferenceNo(first.getRequestReferenceNo());
	        engine.setAgencyCode(first.getAgencyCode());
	        engine.setLocationId(first.getLocationId().toString());
	        engine.setVehicleId(first.getRiskId().toString());
	        engine.setEffectiveDate(
	                first.getEndorsementEffdate() == null
	                        ? first.getPolicyStartDate()
	                        : first.getEndorsementEffdate());
	        engine.setPolicyEndDate(
	                request.getPolicyEndDate() == null
	                        ? first.getPolicyEndDate()
	                        : request.getPolicyEndDate());
	        engine.setCoverModification(
	                StringUtils.isBlank(request.getCoverModification())
	                        ? "N" : request.getCoverModification());

	        List<SectionCoverMaster> coverMasters = getSectionCoverDetails(
	                first.getProductId(), first.getSectionId(), first.getCompanyId());
	        if (CollectionUtils.isEmpty(coverMasters)) {
	            engine.setCoverId(first.getCoverId() == null
	                    ? "0" : first.getCoverId().toString());
	        }

	        System.out.println("100053 Property: single calculator() call for requestRef="
	                + engine.getRequestReferenceNo()
	                + " totalBuildings=" + allBuildings.size());

	        EserviceMotorDetailsSaveRes res = calculator(engine, token);

	        if (res != null) {
	            resList.add(res);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return resList;
	}

private List<EserviceMotorDetailsSaveRes> handleTravelRequest(CalcEngine request, String token) {

	    List<EserviceMotorDetailsSaveRes> resList = new ArrayList<>();

	    try {

	        // Header data
	        List<EserviceTravelDetails> travelDetails =
	        		eserTraRepo.findByRequestReferenceNoAndProductId(request.getRequestReferenceNo(),request.getProductId());

	        if (travelDetails.isEmpty()) {
	            System.out.println("Travel Details not found for ReqRefNo: "
	                    + request.getRequestReferenceNo());
	            return resList;
	        }

	        EserviceTravelDetails travel = travelDetails.get(0);

	        // Groups
	        List<EserviceTravelGroupDetails> groupList =
	                eserviceTravelGroupRepo.findByRequestReferenceNo(request.getRequestReferenceNo());

	        if (groupList.isEmpty()) {
	            System.out.println("Travel Groups not found for ReqRefNo: "
	                    + request.getRequestReferenceNo());
	            return resList;
	        }

	        for (EserviceTravelGroupDetails group : groupList) {

	            CalcEngine engine = new CalcEngine();

	            engine.setLocationId(travel.getLocationId());
	            engine.setBranchCode(travel.getBranchCode());
	            engine.setInsuranceId(travel.getCompanyId());
	            engine.setSectionId(travel.getSectionId());
	            engine.setProductId(travel.getProductId());

	            engine.setMsrefno(travel.getMsRefno().toString());
	            engine.setCdRefNo(travel.getCdRefno().toString());
	            engine.setVdRefNo(travel.getVdRefNo().toString());

	            engine.setCreatedBy(travel.getCreatedBy());
	            engine.setAgencyCode(travel.getAgencyCode());

	            engine.setRequestReferenceNo(travel.getRequestReferenceNo());

	            engine.setEffectiveDate(
	                    travel.getEndorsementEffdate() == null
	                            ? travel.getTravelStartDate()
	                            : travel.getEndorsementEffdate()
	            );

	            engine.setPolicyEndDate(
	                    request.getPolicyEndDate() == null
	                            ? travel.getTravelEndDate()
	                            : request.getPolicyEndDate()
	            );

	            engine.setCoverModification(
	                    StringUtils.isBlank(request.getCoverModification())
	                            ? "N"
	                            : request.getCoverModification()
	            );

	            engine.setVehicleId(group.getGroupId().toString());

	            ObjectMapper objectMapper = new ObjectMapper();
	            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);

	            System.out.println(
	                    "Travel Calculator Request → GroupId : "
	                            + group.getGroupId()
	                            + "\nRequest → "
	                            + objectMapper.writeValueAsString(engine)
	            );

	            EserviceMotorDetailsSaveRes res = calculator(engine, token);

	            resList.add(res);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return resList;
	}

/**
 * Reverse-engineers base premium P from actualPremiumLc using cascading tax formula:
 *
 * T = SD + SF + (1 + TL)(1 + V)P + V × SF
 * Therefore:
 * P = (T - SD - SF - (V × SF)) / ((1 + TL)(1 + V))
 *
 * If importYn = 'Y' → SD is excluded from reverse engineering (treated as 0)
 *
 * @param actualPremiumLc  gross premium from eservice_motor_details
 * @param resolvedTaxes    tax tuples from ratingutil.resolveTaxByPolicyDays()
 * @param importYn         importYn flag from eservice_motor_details
 * @return                 net base premium to set as cover Rate
 */
private BigDecimal reverseEngineerBasePremium(
        BigDecimal actualPremiumLc,
        List<Tuple> resolvedTaxes,
        String importYn) {

    BigDecimal SD = BigDecimal.ZERO; 
    BigDecimal SF = BigDecimal.ZERO; 
    BigDecimal TL = BigDecimal.ZERO; 
    BigDecimal V  = BigDecimal.ZERO; 

    boolean isImport = "Y".equalsIgnoreCase(importYn);

    for (Tuple tax : resolvedTaxes) {
        String taxCode  = tax.get("taxCode")  == null ? "" : tax.get("taxCode").toString().trim();
        String calcType = tax.get("calcType") == null ? "" : tax.get("calcType").toString().trim();
        BigDecimal value = tax.get("value") == null
                ? BigDecimal.ZERO
                : new BigDecimal(tax.get("value").toString());

        switch (taxCode.toUpperCase()) {
            case "STAMP":
                // If importYn=Y, stamp duty excluded from reverse engineering
                SD = isImport ? BigDecimal.ZERO : value;
                break;
            case "STICKER":
                SF = value;
                break;
            case "LEVY":
                // Convert percentage to decimal: 0.5 → 0.005
                TL = value.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                break;
            case "VAT":
                // Convert percentage to decimal: 18 → 0.18
                V = value.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                break;
            default:
                System.out.println("[Product125] Unknown taxCode='" + taxCode + "' — skipped in reverse engineering");
                break;
        }
    }

    // P = (T - SD - SF - (V × SF)) / ((1 + TL)(1 + V))
    BigDecimal numerator = actualPremiumLc
            .subtract(SD)
            .subtract(SF)
            .subtract(V.multiply(SF));

    BigDecimal denominator = BigDecimal.ONE.add(TL)
            .multiply(BigDecimal.ONE.add(V));

    BigDecimal basePremium = numerator.divide(denominator, 10, RoundingMode.HALF_UP);

    System.out.println("[Product125] === Reverse Engineering ===");
    System.out.println("[Product125] actualPremiumLc = " + actualPremiumLc);
    System.out.println("[Product125] importYn        = " + importYn);
    System.out.println("[Product125] SD (StampDuty)  = " + SD);
    System.out.println("[Product125] SF (StickerFee) = " + SF);
    System.out.println("[Product125] TL (TrainLevy)  = " + TL);
    System.out.println("[Product125] V  (VAT)        = " + V);
    System.out.println("[Product125] numerator       = " + numerator);
    System.out.println("[Product125] denominator     = " + denominator);
    System.out.println("[Product125] basePremium     = " + basePremium);

    return basePremium;
}

/**
 * Special handler for companyId=100019, productId=125.
 *
 * Flow:
 *  1. Run normal one-time table setup
 *  2. Load taxes for this product
 *  3. Fetch actualPremiumLc from eservice_motor_details by requestReferenceNo
 *  4. Reverse-engineer base premium by stripping taxes from actualPremiumLc
 *  5. Patch the single cover tuple's "rate" with the derived base
 *  6. Hand off to existing processCoverDependents flow — taxes re-applied naturally
 */
private EserviceMotorDetailsSaveRes handleProduct125Calculation(CalcEngine engine, String token) {

    LoginMaster loginMaster = loginMasterRepository.findByLoginId(engine.getCreatedBy());
    List<UWReferrals> referrals = null;
    List<MasterReferal> masterReferrals = null;

    if (!("issuer".equalsIgnoreCase(loginMaster.getUserType())
            && "superadmin".equalsIgnoreCase(loginMaster.getSubUserType()))) {
        referrals = referal.underwriterReferral(engine);
        try {
            masterReferrals = referal.masterreferral(engine, token);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    List<Cover> resultCovers = new ArrayList<>();
    BigDecimal endtCount = BigDecimal.ZERO;
    String isEndt = null;

    try {
        // Step 1: setup
        loadOnetimetable(engine);
        validateData();

        // Step 2: load taxes
        List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
        long policyDays = ratingutil.calculatePolicyDays(engine);
        List<Tuple> resolvedTaxes = ratingutil.resolveTaxByPolicyDays(taxes, policyDays);

        List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
        List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);

        TaxUtils taxUtils = new TaxUtils(endtCount, "");
        TaxRemover taxRemover = new TaxRemover(excludedTaxes, null);

        // Step 3: fetch actualPremiumLc + importYn from DB
        EserviceMotorDetails motorDetails = eservicemotorRepo
                .findFirstByRequestReferenceNo(engine.getRequestReferenceNo());

        if (motorDetails == null || motorDetails.getLoanAmount() == null) {
            System.err.println("[Product125] actualPremiumLc not found for ref: "
                    + engine.getRequestReferenceNo() + " — falling back to normal calc");
            return handleMotorCalculation(engine, token);
        }

     Double actualPremiumDouble = motorDetails.getLoanAmount();
     BigDecimal basePremium = BigDecimal.valueOf(actualPremiumDouble);

     List<Tuple> rawCoversTuple = LoadCover(engine);

     List<Tuple> patchedCoversTuple = rawCoversTuple.stream()
             .map(tuple -> {
                 String calcType = tuple.get("calcType") == null
                         ? "" : tuple.get("calcType").toString().trim();
                 if ("A".equals(calcType)) {
                     Map<String, Object> overrides = new HashMap<>();
                     overrides.put("baseRate", basePremium.toPlainString());
                     overrides.put("rate", basePremium.toPlainString()); // safety fallback
                     System.out.println("[Product125] Patching coverId="
                             + tuple.get("coverId")
                             + " baseRate → " + basePremium.toPlainString());
                     return (Tuple) new TupleWrapper(tuple, overrides);
                 }
                 return tuple;
             })
             .collect(Collectors.toList());
     
     String promocode = Optional.ofNullable(vehicles.get(0).get("promocode"))
             .map(Object::toString).orElse("");

     List<String> dependentCovers = List.of("N", "Y");

     // Step 6: pass resolvedTaxes directly — TaxCalculator zeros amounts for 100019/125
     resultCovers.clear();
     dependentCovers.forEach(depend -> processCoverDependents(
             engine, depend, patchedCoversTuple,
             resolvedTaxes,        
             customerChoiceTaxes,
             promocode, taxUtils, taxRemover, resultCovers
     ));
        

        loadBenefitCovers(engine, resultCovers);

        // Minimum premium check
        BigDecimal totalPremium = resultCovers.stream()
                .filter(x -> !"N".equals(x.getIsselected())
                        && !"945".equals(x.getCoverId())
                        && x.getPremiumExcluedTaxLC() != null)
                .map(Cover::getPremiumExcluedTaxLC)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPremium.compareTo(minimumPremium) < 0) {
            adjustMinimumPremium(engine, endtCount, resolvedTaxes, taxUtils, resultCovers);
        } else {
            resultCovers.removeIf(t -> "945".equals(t.getCoverId()));
        }

        // Endorsement check
        Object endtType = vehicles.get(0).get("endtTypeId");
        if ((endtType != null && StringUtils.isNotBlank(endtType.toString()))
                || !engine.getEndtTypeId().isEmpty()) {
            endtCount = handleEndorsement(engine, resultCovers);
        }

        // Build and save
        EserviceMotorDetailsSaveRes response = buildResponse(
                engine, referrals, masterReferrals, resultCovers, isEndt);

        fservice.saveFactorRateRequestDetails(response);

        // Endorsement recalculation
        String endtTypeId = "";
        if (StringUtils.isNotBlank(engine.getEndtTypeId())) {
            endtTypeId = engine.getEndtTypeId();
        } else {
            endtTypeId = Optional.ofNullable(vehicles.get(0).get("endtTypeId"))
                    .map(Object::toString).orElse("");
        }

        if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
            return endorsementCalculator(engine, endtCount, endtTypeId, isPolicyPeriod);
        }

        return response;

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

	public List<Tuple> LoadCoverBatch(CalcEngine engine, List<String> coverIds) {
	    try {
	        String todayInString = DD_MM_YYYY.format(new Date());
	        // e.g. "124,156,105"
	        String coverIdCsv = String.join(",", coverIds);
	
	        // Primary covers — all coverIds in one shot
	        String search4 = "companyId:" + engine.getInsuranceId()
	                + ";productId:" + engine.getProductId()
	                + ";sectionId:" + engine.getSectionId()
	                + ";status:{Y,R};"
	                + todayInString + "~effectiveDateStart&effectiveDateEnd;"
	                + "agencyCode:99999;branchCode:99999;"
	                + "coverId:{" + coverIdCsv + "};";
	
	        SpecCriteria criteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
	        List<Tuple> commonResult = crservice.getResult(criteria, 0, 200);
	
	        // Dependent covers
	        String searchDep = "companyId:" + engine.getInsuranceId()
	                + ";productId:" + engine.getProductId()
	                + ";sectionId:" + engine.getSectionId()
	                + ";status:{Y,R};"
	                + todayInString + "~effectiveDateStart&effectiveDateEnd;"
	                + "agencyCode:99999;branchCode:99999;"
	                + "dependentCoverId:{" + coverIdCsv + "};";
	
	        SpecCriteria depCriteria = crservice.createCriteria(SectionCoverMaster.class, searchDep, "coverId");
	        commonResult.addAll(crservice.getResult(depCriteria, 0, 200));
	
	        // Discount covers
	        String searchDisc = "companyId:" + engine.getInsuranceId()
	                + ";productId:" + engine.getProductId()
	                + ";sectionId:" + engine.getSectionId()
	                + ";status:{Y,R};"
	                + todayInString + "~effectiveDateStart&effectiveDateEnd;"
	                + "agencyCode:99999;branchCode:99999;"
	                + "discountCoverId:{" + coverIdCsv + "};";
	
	        SpecCriteria discCriteria = crservice.createCriteria(SectionCoverMaster.class, searchDisc, "coverId");
	        commonResult.addAll(crservice.getResult(discCriteria, 0, 200));
	
	        // Agency-specific overrides — same logic as LoadCover()
	        String search2 = "companyId:" + engine.getInsuranceId()
	                + ";productId:" + engine.getProductId()
	                + ";sectionId:" + engine.getSectionId()
	                + ";status:{Y,R};"
	                + todayInString + "~effectiveDateStart&effectiveDateEnd;"
	                + "agencyCode:" + engine.getAgencyCode() + ";branchCode:99999;";
	
	        SpecCriteria agencyCriteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
	        List<Long> count = crservice.getCount(agencyCriteria, 0, 50);
	        if (!count.isEmpty() && count.get(0) > 0) {
	            List<Tuple> specific = crservice.getResult(agencyCriteria, 0, 200);
	            for (Tuple t : specific) {
	                commonResult.removeIf(c -> c.get("coverId").toString()
	                        .equals(t.get("coverId").toString()));
	                commonResult.add(t);
	            }
	        }
	
	        return commonResult;
	
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return new ArrayList<>();
	}
	
	public EserviceMotorDetailsSaveRes calculatorBatch(CalcEngineBatch batchEngine, String token) {
	    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
	            + " <---- calculatorBatch start: " + batchEngine.getRequestReferenceNo()
	            + " covers: " + batchEngine.getCoverIds());
	    long startTime = System.currentTimeMillis();
	    threadMonitorService.monitorThreads("START", "CalculatorEngineService-Batch");

	    try {
	        List<String> coverIds = batchEngine.getCoverIds();
	        List<String> vdRefNos = batchEngine.getVdRefNos();
	        List<String> cdRefNos = batchEngine.getCdRefNos();
	        List<String> msRefNos = batchEngine.getMsRefNos();

	        // Safety: if somehow called with empty lists, fall back to single calc
	        if (coverIds == null || coverIds.isEmpty()) {
	            return calculator(batchEngine, token);
	        }

	        batchEngine.setMsrefno(msRefNos.get(0));
	        batchEngine.setVdRefNo(vdRefNos.get(0));
	        batchEngine.setCdRefNo(cdRefNos.get(0));
	        batchEngine.setCoverId(coverIds.get(0));
	        loadOnetimetable(batchEngine);

	        
	        List<Tuple> totalCoversTuple = LoadCoverBatch(batchEngine, coverIds);
	        System.out.println("LoadCoverBatch returned " + totalCoversTuple.size()
	                + " rows for covers: " + coverIds);

	        List<Tuple> taxes          = ratingutil.LoadTax(batchEngine, NORMAL_TAX_LIST);
	        long policyDays            = ratingutil.calculatePolicyDays(batchEngine);
	        List<Tuple> resolvedTaxes  = ratingutil.resolveTaxByPolicyDays(taxes, policyDays);
	        List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(batchEngine);
	        List<Tuple> excludedTaxes  = ratingutil.LoadExcludedTax(batchEngine, NORMAL_TAX_LIST);
	        TaxUtils taxUtils          = new TaxUtils(BigDecimal.ZERO, "");
	        TaxRemover taxRemover      = new TaxRemover(excludedTaxes, null);
	        String promocode           = Optional.ofNullable(vehicles.get(0).get("promocode"))
	                                             .map(Object::toString).orElse("");

	        // ── Step 4: Per-cover processing using pre-loaded data ──
	        List<Cover> resultCovers = new ArrayList<>();

	        for (int i = 0; i < coverIds.size(); i++) {
	            final String currentCoverId = coverIds.get(i);

	            // Swap per-cover refs so processCoverDependents reads
	            // the right sumInsured from the correct vd row
	            batchEngine.setCoverId(currentCoverId);
	            batchEngine.setVdRefNo(vdRefNos.get(i));
	            batchEngine.setCdRefNo(cdRefNos.get(i));
	            batchEngine.setMsrefno(msRefNos.get(i));

	            // Reload vehicle/risk data only when vdRefNo actually changes
	            // (each non-motor cover can have its own sumInsured row)
	            if (i > 0 && !vdRefNos.get(i).equals(vdRefNos.get(i - 1))) {
	                loadOnetimetable(batchEngine);
	            }

	            // Filter the batch-loaded tuples to this cover's relevant rows
//	            List<Tuple> coverSubset = totalCoversTuple.stream()
//	                .filter(t -> {
//	                    String cid   = t.get("coverId")        != null ? t.get("coverId").toString()        : "";
//	                    String dep   = t.get("dependentCoverId") != null ? t.get("dependentCoverId").toString() : "";
//	                    String disc  = t.get("discountCoverId") != null ? t.get("discountCoverId").toString()  : "";
//	                    return currentCoverId.equals(cid)
//	                        || currentCoverId.equals(dep)
//	                        || currentCoverId.equals(disc);
//	                })
//	                .collect(Collectors.toList());
	            List<Tuple> coverSubset = totalCoversTuple.stream()
	            	    .filter(t -> {
	            	        String cid = t.get("coverId") != null ? t.get("coverId").toString() : "";
	            	        return currentCoverId.equals(cid);
	            	    })
	            	    .collect(Collectors.toList());

	            System.out.println("Cover " + currentCoverId + " subset size: " + coverSubset.size());

	            List<String> dependentCovers = List.of("N", "Y");
	            dependentCovers.forEach(depend ->
	                processCoverDependents(batchEngine, depend, coverSubset,
	                        resolvedTaxes, customerChoiceTaxes,
	                        promocode, taxUtils, taxRemover, resultCovers)
	            );
	        }

	        // ── Step 5: Benefit covers ONCE ──
	        loadBenefitCovers(batchEngine, resultCovers);

	        // ── Step 6: Minimum premium check ──
	        BigDecimal endtCount = BigDecimal.ZERO;
	        BigDecimal totalPremium = resultCovers.stream()
	                .filter(x -> !"N".equals(x.getIsselected())
	                        && !"945".equals(x.getCoverId())
	                        && x.getPremiumExcluedTaxLC() != null)
	                .map(Cover::getPremiumExcluedTaxLC)
	                .reduce(BigDecimal.ZERO, BigDecimal::add);

	        if (totalPremium.compareTo(minimumPremium) < 0) {
	            adjustMinimumPremium(batchEngine, endtCount, resolvedTaxes, taxUtils, resultCovers);
	        } else {
	            resultCovers.removeIf(t -> "945".equals(t.getCoverId()));
	        }

	        // ── Step 7: Endorsement handling ──
	        Object endtType = vehicles.get(0).get("endtTypeId");
	        if ((endtType != null && !"0".equals(endtType)
	                && StringUtils.isNotBlank(endtType.toString()))
	                || StringUtils.isNotBlank(batchEngine.getEndtTypeId())) {
	            endtCount = handleEndorsement(batchEngine, resultCovers);
	        }

	        // ── Step 8: Referrals ──
	        LoginMaster loginMaster = loginMasterRepository.findByLoginId(batchEngine.getCreatedBy());
	        List<UWReferrals> referrals = null;
	        List<MasterReferal> masterReferrals = null;

	        if (!("issuer".equalsIgnoreCase(loginMaster.getUserType())
	                && "superadmin".equalsIgnoreCase(loginMaster.getSubUserType()))) {
	            referrals = referal.underwriterReferral(batchEngine);
	            try {
	                masterReferrals = referal.masterreferral(batchEngine, token);
	            } catch (ClassNotFoundException e) {
	                e.printStackTrace();
	            }
	        }

	        // ── Step 9: Build & save ONCE ──
	        EserviceMotorDetailsSaveRes response = buildResponse(
	                batchEngine, referrals, masterReferrals, resultCovers, null);
	        fservice.saveFactorRateRequestDetails(response);

	        // ── Step 10: Endorsement recalculation if needed ──
	        String endtTypeId = StringUtils.isNotBlank(batchEngine.getEndtTypeId())
	                ? batchEngine.getEndtTypeId()
	                : Optional.ofNullable(vehicles.get(0).get("endtTypeId"))
	                          .map(Object::toString).orElse("");

	        if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
	            return endorsementCalculator(batchEngine, endtCount, endtTypeId, isPolicyPeriod);
	        }

	        return response;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        long endTime = System.currentTimeMillis();
	        System.out.printf("calculatorBatch completed in %d ms%n", (endTime - startTime));
	        threadMonitorService.monitorThreads("END", "CalculatorEngineService-Batch");
	        threadMonitorService.performCleanup();
	    }
	}


}
