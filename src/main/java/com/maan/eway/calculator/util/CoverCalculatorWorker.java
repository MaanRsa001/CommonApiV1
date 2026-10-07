package com.maan.eway.calculator.util;
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
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.service.PolicyDrcrDetailService;
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


//@Component
//@Scope("prototype") 
public class CoverCalculatorWorker {

    
//	@Autowired
//	private CriteriaService crservice;
//
//	@Autowired
//	private RatingFactorsUtil ratingutil;
//
//	@Autowired
//	private CoverDetailsRepository coverRepo;
//
//	@Autowired
//	private EmiTransactionDetailsRepository emiRepo;
//	/*
//	 * 
//	 * @Autowired private CoverCalculator calc;
//	 */
//
//	@Autowired
//	RestTemplateApiService restTemplateApiService;
//
//	@Autowired
//	private MsHumanDetailsRepository msHumanRepo;
//	
//	@Autowired
//	private EserviceTravelGroupDetailsRepository  eserviceTravelGroupRepo;
//
//	@Autowired
//	LoginMasterRepository loginMasterRepository;
//
//	@Value(value = "${travel.productId}")
//	private String travelProductId;
//
//	@Value(value = "${calEngine}")
//	private String calEngine;
//
//	protected List<Tuple> commontbl = null;
//	protected List<Tuple> vehicles = null;
//	protected List<Tuple> customers = null;
//	protected List<Cover> calculatedcover = null;
//	protected List<Tuple> prorata = null;
//	protected BigDecimal minimumPremium = BigDecimal.ZERO;
//	protected List<Tuple> policytbl = null;
//	protected List<Tuple> drivers = null;
//	protected List<Tuple> customerChoiceTaxes;
//
//	@Autowired
//	private FactorRateRequestDetailsService fservice;
//
//	@PersistenceContext
//	private EntityManager em;
//
//	@Autowired
//	private FactorRateRequestDetailsRepository repository;
//
//	@Autowired
//	private ReferalServiceImpl referal;
//
//	@Autowired
//	private LoginUserInfoRepository loginUserRepo;
//
//	@Autowired
//	private QuoteService quoteservice;
//
//	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");
//
//	@Autowired
//	private LoginProductMasterRepository loginProductrepo;
//
//	@Autowired
//	private PolicyDrcrDetailService crdrservice;
//
//	@Autowired
//	private GenerateSeqNoServiceImpl genNo;
//
//	DecimalFormat decimalFormat = null;
//	@Autowired
//	private PolicyCoverDataRepository coverDataRepo;
//
//	@Autowired
//	private TravelPassengerDetailsRepository travelRepo;
//
//	@Autowired
//	private BuildingRiskDetailsRepository buildingRepo;
//
//	@Autowired
//	private CommonDataDetailsRepository commonRepo;
//
//	@Autowired
//	private HomePositionMasterRepository homeRepo;
//
//	@Autowired
//	private PersonalInfoRepository piRepo;
//
//	@Autowired
//	private EServiceSectionDetailsRepository esSecRepo;
//
//	@Autowired
//	private EserviceBuildingDetailsRepository eservicebuildingRepo;
//
//	@Autowired
//	private EserviceCommonDetailsRepository eservicecommonRepo;
//
//	@Autowired
//	private EserviceTravelDetailsRepository eserTraRepo;
//
//	@Autowired
//	private EServiceMotorDetailsRepository eservicemotorRepo;
//	
//	@Autowired
//	private CompanyProductMasterRepository companyProductMasterRepo;
//
//	
//	private Boolean isPolicyPeriod = Boolean.FALSE;
//	@Autowired
//	private MsVehicleDetailsRepository msVehicleRepo;
//
//	private String oldPolicyPeriod;
//
//	private String currentPolicyPeriod;
//
//	private Date prevPolicyEndDate;
//	/*
//	 * public void LoadSection(CalcEngine engine) {
//	 * 
//	 * try { String todayInString = DD_MM_YYYY.format(new Date());
//	 * 
//	 * String search="companyId:"+ engine.getInsuranceId()
//	 * +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+
//	 * ";status=Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
//	 * List<Tuple> result=null; SpecCriteria criteria =
//	 * crservice.createCriteria(ProductSectionMaster.class, search, "coverId");
//	 * result=crservice.getResult(criteria, 0, 50);
//	 * 
//	 * System.out.println("result"+result.size()); }catch(Exception e) {
//	 * e.printStackTrace(); } }
//	 */
//	private final List<String> NORMAL_TAX_LIST = Arrays.asList("NB");
//	private final List<String> ENDT_TAX_LIST = Arrays.asList("EC", "ER");
//	@Autowired
//	private PolicyCoverDataEndtRepository policyCoverEndtRepo;
//
//	@Autowired
//	private JsonMapperFromDB jsonMapper;
//
//	@Autowired
//	private TravelApiIntegration travelInteg;
//
//	@Autowired
//	private ThreadMonitorService threadMonitorService;
//
//	private static final AtomicInteger activeThreads = new AtomicInteger(0);
//
//    // ── Entry point called from getCalc() parallel stream ──
//    public EserviceMotorDetailsSaveRes calculator(CalcEngine engine, String token) {
//        System.out.println(LocalTime.now().format(
//            DateTimeFormatter.ofPattern("HH:mm:ss"))
//            + " <---- CoverCalculatorWorker start coverId: "
//            + engine.getCoverId());
//        long start = System.currentTimeMillis();
//
//        try {
//            String insuranceId = engine.getInsuranceId();
//            String productId   = engine.getProductId();
//
//            // Direct quotation case
//            if (List.of("100040", "100027").contains(insuranceId)) {
//                return createQuotation(engine);
//            }
//
//            // Travel case
//            if ("4".equals(productId)
//                    && List.of("100046","100047","100048","100049","100050")
//                        .contains(insuranceId)) {
//                return handleTravelIntegration(engine);
//            }
//
//            // All other products
//            return handleMotorCalculation(engine, token);
//
//        } finally {
//            long end = System.currentTimeMillis();
//            System.out.printf("CoverCalculatorWorker cover %s done in %d ms%n",
//                engine.getCoverId(), (end - start));
//        }
//    }
//
//    // ── Copy of handleMotorCalculation ──
//    // Uses this instance's own fields — no shared state
//    private EserviceMotorDetailsSaveRes handleMotorCalculation(
//            CalcEngine engine, String token) {
//
//        // loginMaster — cached in RatingFactorsUtil so safe from any thread
//        LoginMaster loginMaster =
//            loginMasterRepository.findByLoginId(engine.getCreatedBy());
//
//        List<UWReferrals> referrals      = null;
//        List<MasterReferal> masterReferrals = null;
//
//        if (!("issuer".equalsIgnoreCase(loginMaster.getUserType())
//                && "superadmin".equalsIgnoreCase(loginMaster.getSubUserType()))) {
//            referrals = referal.underwriterReferral(engine);
//            try {
//                masterReferrals = referal.masterreferral(engine, token);
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if ("100053".equals(engine.getInsuranceId())
//                && "117".equals(engine.getProductId())) {
//            return handlePropertyMultiLocationCalculation(engine, token);
//        }
//
//        List<Cover> resultCovers = new ArrayList<>();
//        BigDecimal endtCount     = BigDecimal.ZERO;
//        String isEndt            = null;
//
//        try {
//            // loadOnetimetable uses THIS instance's fields
//            // No synchronized — each worker has its own vehicles/customers
//            loadOnetimetable(engine);
//
//            if ("100040".equals(engine.getInsuranceId()))
//                loadFixedValue(engine);
//
//            validateData();
//
//            String promocode = Optional.ofNullable(
//                vehicles.get(0).get("promocode"))
//                .map(Object::toString).orElse("");
//
//            // These are @Cacheable — safe from multiple threads
//            List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
//            List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
//            List<Tuple> excludedTaxes =
//                ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
//
//            TaxUtils taxUtils   = new TaxUtils(endtCount, "");
//            TaxRemover taxRemover = new TaxRemover(excludedTaxes, null);
//
//            // LoadCover — @Cacheable, safe from multiple threads
//            List<Tuple> totalCoversTuple = LoadCover(engine);
//
//            // Null guard
//            if (totalCoversTuple == null || totalCoversTuple.isEmpty()) {
//                System.out.println("LoadCover empty for sectionId: "
//                    + engine.getSectionId()
//                    + " coverId: " + engine.getCoverId());
//                return null;
//            }
//
//            List<String> dependentCovers = List.of("N", "Y");
//            dependentCovers.forEach(depend ->
//                processCoverDependents(engine, depend, totalCoversTuple,
//                    taxes, customerChoiceTaxes, promocode,
//                    taxUtils, taxRemover, resultCovers));
//
//            loadBenefitCovers(engine, resultCovers);
//
//            BigDecimal totalPremium = resultCovers.stream()
//                .filter(x -> !"N".equals(x.getIsselected())
//                    && !"945".equals(x.getCoverId())
//                    && x.getPremiumExcluedTaxLC() != null)
//                .map(Cover::getPremiumExcluedTaxLC)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            if (totalPremium.compareTo(minimumPremium) < 0) {
//                adjustMinimumPremium(engine, endtCount,
//                    taxes, taxUtils, resultCovers);
//            } else {
//                resultCovers.removeIf(t -> "945".equals(t.getCoverId()));
//            }
//
//            endtCount = handleEndorsement(engine, resultCovers);
//
//            EserviceMotorDetailsSaveRes response =
//                buildResponse(engine, referrals, masterReferrals,
//                    resultCovers, isEndt);
//
//            fservice.saveFactorRateRequestDetails(response);
//
//            String endtTypeId = Optional.ofNullable(
//                vehicles.get(0).get("endtTypeId"))
//                .map(Object::toString).orElse("");
//
//            if (StringUtils.isNotBlank(endtTypeId)
//                    && !"0".equals(endtTypeId)) {
//                return endorsementCalculator(
//                    engine, endtCount, endtTypeId, isPolicyPeriod);
//            }
//
//            return response;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // ── Copy of loadOnetimetable WITHOUT synchronized ──
//    // Safe because each worker instance has its own vehicles/customers fields
//    public void loadOnetimetable(CalcEngine engine) {
//        try {
//            System.out.println(LocalTime.now().format(
//                DateTimeFormatter.ofPattern("HH:mm:ss"))
//                + " <---- loadOnetimetable start coverId: "
//                + engine.getCoverId());
//
//            SpecCriteria criteria = null;
//
//            // collectProductType is @Cacheable — returns same result for all covers
//            List<Tuple> product = ratingutil.collectProductType(engine);
//            String oneProduct = product.get(0).get("motorYn") == null
//                ? "M" : product.get(0).get("motorYn").toString();
//
//            String search = "msRefno:" + engine.getMsrefno() + ";";
//            criteria = crservice.createCriteria(
//                MsCommonDetails.class, search, "msRefno");
//            commontbl = crservice.getResult(criteria, 0, 50);
//
//            if (commontbl != null && commontbl.size() > 0) {
//                Tuple tuple  = commontbl.get(0);
//                String vdRefno = tuple.get("vdRefno").toString();
//                String cdRefno = tuple.get("cdRefno").toString();
//
//                vehicles = null;
//                int counter = 0;
//
//                while ((vehicles == null || vehicles.size() == 0)
//                        && counter < 6) {
//
//                    if (oneProduct.equals("M")) {
//                        search = "vdRefno:" + engine.getVdRefNo()
//                            + ";vehicleId:" + engine.getVehicleId()
//                            + ";locationId:" + (StringUtils.isBlank(
//                                engine.getLocationId()) ? "1"
//                                : engine.getLocationId());
//                        criteria = crservice.createCriteria(
//                            MsVehicleDetails.class, search, "vdRefno");
//                        vehicles = crservice.getResult(criteria, 0, 50);
//
//                        if (StringUtils.isNotBlank(engine.getDdRefno())
//                                && !"0".equals(engine.getDdRefno())) {
//                            search = "ddRefno:" + engine.getDdRefno()
//                                + ";riskId:" + engine.getVehicleId()
//                                + ";driverId:1;locationId:"
//                                + (StringUtils.isBlank(engine.getLocationId())
//                                    ? "1" : engine.getLocationId());
//                            criteria = crservice.createCriteria(
//                                MsDriverDetails.class, search, "ddRefno");
//                            drivers = crservice.getResult(criteria, 0, 50);
//                        }
//
//                    } else if (oneProduct.equals("H")) {
//                        search = "vdRefno:" + engine.getVdRefNo()
//                            + ";humanId:" + engine.getVehicleId()
//                            + ";locationId:" + (StringUtils.isBlank(
//                                engine.getLocationId()) ? "1"
//                                : engine.getLocationId());
//                        criteria = crservice.createCriteria(
//                            MsHumanDetails.class, search, "vdRefno");
//                        vehicles = crservice.getResult(criteria, 0, 50);
//
//                    } else if (oneProduct.equalsIgnoreCase("A")) {
//                        search = "vdRefno:" + engine.getVdRefNo()
//                            + ";riskId:" + engine.getVehicleId()
//                            + ";locationId:" + (StringUtils.isBlank(
//                                engine.getLocationId()) ? "1"
//                                : engine.getLocationId());
//                        criteria = crservice.createCriteria(
//                            MsAssetDetails.class, search, "vdRefno");
//                        vehicles = crservice.getResult(criteria, 0, 50);
//
//                    } else if (oneProduct.equalsIgnoreCase("L")) {
//                        search = "vdRefno:" + engine.getVdRefNo()
//                            + ";riskId:" + engine.getVehicleId()
//                            + ";locationId:" + (StringUtils.isBlank(
//                                engine.getLocationId()) ? "1"
//                                : engine.getLocationId());
//                        criteria = crservice.createCriteria(
//                            MsLifeDetails.class, search, "vdRefno");
//                        vehicles = crservice.getResult(criteria, 0, 50);
//                    }
//
//                    counter++;
//                    System.out.println("Worker vehicle record " + vdRefno
//                        + " = " + ((vehicles == null || vehicles.isEmpty())
//                            ? "empty" : "loaded"));
//                }
//
//                search = "cdRefno:" + cdRefno + ";";
//                criteria = crservice.createCriteria(
//                    MsCustomerDetails.class, search, "cdRefno");
//                customers = crservice.getResult(criteria, 0, 50);
//
//                if (vehicles != null && vehicles.size() > 0) {
//                    String periodOfInsurance =
//                        vehicles.get(0).get("periodOfInsurance") == null
//                            ? "365"
//                            : vehicles.get(0).get("periodOfInsurance").toString();
//                    String policyTypeId =
//                        vehicles.get(0).get("insuranceClass") == null
//                            ? "99999"
//                            : vehicles.get(0).get("insuranceClass").toString();
//
//                    String coverId =
//                        vehicles.get(0).get("coverId") == null
//                            ? engine.getCoverId()
//                            : vehicles.get(0).get("coverId").toString();
//                    engine.setCoverId(coverId);
//
//                    // loadProRataData is @Cacheable — safe from multiple threads
//                    prorata = ratingutil.loadProRataData(
//                        engine, periodOfInsurance, policyTypeId);
//
//                    String currencyId =
//                        vehicles.get(0).get("currency") == null
//                            ? "TTT"
//                            : vehicles.get(0).get("currency").toString();
//                    String decimalDigits = ratingutil.currencyDecimalFormat(
//                        engine.getInsuranceId(), currencyId);
//                    String stringFormat = "%0" + decimalDigits + "d";
//                    String decimalLength = decimalDigits.equals("0")
//                        ? ""
//                        : String.format(stringFormat, 0L);
//                    String pattern = StringUtils.isBlank(decimalLength)
//                        ? "#####0"
//                        : "#####0." + decimalLength;
//                    decimalFormat = new DecimalFormat(pattern);
//
//                    minimumPremium =
//                        product.get(0).get("minPremium") == null
//                            ? BigDecimal.ZERO
//                            : new BigDecimal(
//                                product.get(0).get("minPremium").toString());
//                }
//            }
//
//            System.out.println(LocalTime.now().format(
//                DateTimeFormatter.ofPattern("HH:mm:ss"))
//                + " <---- loadOnetimetable end coverId: "
//                + engine.getCoverId());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // ── Copy all remaining private methods from CalculatorEngineService ──
//    // processCoverDependents()   — copy unchanged
//    // LoadCover()                — copy with null guard fix below
//    // loadBenefitCovers()        — copy unchanged
//    // generateDiscounts()        — copy unchanged
//    // generateLoadings()         — copy unchanged
//    // splitCovers()              — copy unchanged
//    // attachDiscountsLoadingsTaxes() — copy unchanged
//    // adjustMinimumPremium()     — copy unchanged
//    // handleEndorsement()        — copy unchanged
//    // buildResponse()            — copy unchanged
//    // endorsementCalculator()    — copy unchanged
//    // createQuotation()          — copy unchanged
//    // handleTravelIntegration()  — copy unchanged
//    // handlePropertyMultiLocationCalculation() — copy unchanged
//    // getSectionCoverDetails()   — copy unchanged
//    // loadFixedValue()           — copy unchanged
//    // validateData()             — copy unchanged
//
//    // ── LoadCover with null guard ──
//    public List<Tuple> LoadCover(CalcEngine engine) {
//        try {
//            String coverId = engine.getCoverId();
//            if ("null".equalsIgnoreCase(coverId)
//                    || StringUtils.isBlank(coverId)) {
//                coverId = null;
//            }
//
//            StringBuilder search = new StringBuilder();
//            search.append("companyId:").append(engine.getInsuranceId()).append(";");
//            search.append("productId:").append(engine.getProductId()).append(";");
//            search.append("sectionId:").append(engine.getSectionId()).append(";");
//            if (coverId != null) {
//                search.append("coverId:").append(coverId).append(";");
//            }
//
//            SpecCriteria criteria = crservice.createCriteria(
//                SectionCoverMaster.class, search.toString(), "coverId");
//            List<Tuple> result = crservice.getResult(criteria, 0, 50);
//            return result != null ? result : Collections.emptyList();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Collections.emptyList(); // never null
//        }
//    }
//
//	private Map<String, BigDecimal> loadFixedValue(CalcEngine engine) {
//		try {
//			List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
//
//			List<Tuple> totalcoverstuple = LoadCoverFixedValue(engine);
//			if (totalcoverstuple != null && totalcoverstuple.size() > 0) {
//				List<Tuple> covers = totalcoverstuple.parallelStream()
//						.filter(t -> "N".equals(t.get("dependentCoverYn").toString())).collect(Collectors.toList());
//				List<Discount> discounts = null;
//				List<Loading> loadings = null;
//				if (covers != null && covers.size() > 0) {
//					SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
//							engine.getPolicyEndDate(), "");
//					discounts = covers.parallelStream().map(discountUtil).filter(d -> d != null)
//							.collect(Collectors.toList());
//					discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
//					SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
//							engine.getPolicyEndDate());
//					loadings = covers.parallelStream().map(loadingtuils).filter(d -> d != null)
//							.collect(Collectors.toList());
//				}
//
//				SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
//						engine.getPolicyEndDate());
//				Map<String, List<Cover>> nonSubcovers = covers.parallelStream().map(splitsub).filter(d -> d != null)
//						.collect(Collectors.groupingBy(Cover::getIsSubCover));
//				if (!nonSubcovers.isEmpty()) {
//					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
//					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Discount> ds = discounts.stream()
//									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							// .collect(Collectors.toUnmodifiableList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//
//							c.setDiscounts(ds);
//
//						}
//					}
//
//					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							// .collect(Collectors.toUnmodifiableList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//							c.setLoadings(ds);
//
//						}
//					}
//
//				}
//
//				List<Cover> totalcovers = new ArrayList<Cover>();
//				if (!nonSubcovers.isEmpty()) {
//					totalcovers.addAll(nonSubcovers.get("N"));
//				}
//				CoverCalculator calc = new CoverCalculator();
//				calc.setEngine(engine, totalcovers, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
//						drivers, customerChoiceTaxes);
//				totalcovers.parallelStream().forEach(calc);
//				// 286,319,287,324
//				BigDecimal premiumLLD = totalcovers.stream()
//						.sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed())
//						.filter(c -> "324".equals(c.getCoverId())).map(x -> x.getPremiumExcluedTax())
//						.reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);
//				BigDecimal premiumTPL = totalcovers.stream()
//						.sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed())
//						.map(x -> x.getPremiumExcluedTax()).reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);
//
//				MsVehicleDetails details = msVehicleRepo.findByVdRefno(Long.parseLong(engine.getVdRefNo()));
//				details.setPremiumLLD(premiumLLD);
//				details.setPremiumTPL(premiumTPL);
//				msVehicleRepo.save(details);
//				Map<String, BigDecimal> hap = new HashMap<String, BigDecimal>();
//				hap.put("PremiumLLD", premiumLLD);
//				hap.put("PremiumTPL", premiumTPL);
//				return hap;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private List<Tuple> LoadCoverFixedValue(CalcEngine engine) {
//
//		try {
//			String todayInString = DD_MM_YYYY.format(new Date());
//
//			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
//					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
//					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
//					+ ";branchCode:99999;coverId:324";
//
//			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
//					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
//					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;coverId:324";
//
//			SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
//			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);
//
//			SpecCriteria criteria = null;
//			// 286,319,287,324
//			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
//			List<Long> count = crservice.getCount(criteria, 0, 50);
//			if (!count.isEmpty()) {
//				Long countrec = count.get(0);
//				if (countrec > 0) {
//					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
//					for (Tuple t : specific) {
//						commonResult.removeIf(c -> c.get("coverId").toString().equals(t.get("coverId").toString()));
//						commonResult.add(t);
//					}
//				}
//
//			}
//
//			return commonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	private EserviceMotorDetailsSaveRes createQuotation(CalcEngine engine) {
//		WorkEngine work = new WorkEngine();
//		work.setCompanyId(engine.getInsuranceId());
//		work.setProductId(engine.getProductId());
//		work.setQuoteNo("");
//		work.setRequestReferenceNo(engine.getRequestReferenceNo());
//		work.setIntegType("QUOT_INTEG");
//		work.setSectionId(engine.getSectionId());
//		work.setMsrefno(engine.getMsrefno());
//		work.setVdRefNo(engine.getVdRefNo());
//		work.setCdRefNo(engine.getCdRefNo());
//		work.setLocationId(engine.getLocationId());
//		work.setCreatedBy(engine.getCreatedBy());
//		work.setVehicleId(engine.getVehicleId());
//
//		jsonMapper.createQuotation(work);
//		return null;
//	}
//
//	private EserviceMotorDetailsSaveRes handleTravelIntegration(CalcEngine engine) {
//		loadOnetimetable(engine);
//		travelInteg = new TravelApiIntegration(fservice, ratingutil, commontbl, vehicles, msHumanRepo, eserTraRepo);
//		return travelInteg.pushZeus_GetAvailablePlansOTAWithRiders(engine);
//	}
//
//	
//	private void loadBenefitCovers(CalcEngine engine, List<Cover> resultCovers) {
//	    try {
//	        
//	        Map<Integer, List<Integer>> sectionAndCoverIds = resultCovers.stream()
//	            .filter(c -> c.getSectionId() != null && c.getCoverId() != null)
//	            .collect(Collectors.groupingBy(
//	                c -> Integer.valueOf(c.getSectionId()),
//	                Collectors.mapping(c -> Integer.valueOf(c.getCoverId()), Collectors.toList())
//	            ));
//
//	        List<Cover> benefitCovers = new ArrayList<>();
//
//	        sectionAndCoverIds.forEach((sectionId, coverIds) -> {
//	            // Fetch benefit covers from section cover master
//	            List<SectionCoverMaster> sectionCoverMasters = getBySectionCoverId(
//	                engine.getInsuranceId(),
//	                Integer.parseInt(engine.getProductId()),
//	                sectionId,
//	                coverIds
//	            );
//
//	            // Find a reference cover for this section to copy base fields
//	            Cover referenceCover = resultCovers.stream()
//	                .filter(c -> c.getSectionId() != null 
//	                          && c.getSectionId().equals(String.valueOf(sectionId)))
//	                .findFirst()
//	                .orElse(null);
//
//	            if (referenceCover == null) return;
//
//	            sectionCoverMasters.forEach(master -> {
//	                
//	                boolean alreadyPresent = resultCovers.stream()
//	                    .anyMatch(c -> c.getCoverId() != null 
//	                               && c.getCoverId().equals(String.valueOf(master.getCoverId())));
//	                if (alreadyPresent) return;
//
//	                Cover benefitCover = new Cover();
//
//	                benefitCover.setInsuranceId(referenceCover.getInsuranceId());
//	                benefitCover.setProductId(referenceCover.getProductId());
//	                benefitCover.setSectionId(referenceCover.getSectionId());
//	                benefitCover.setSectionName(referenceCover.getSectionName());
//	                benefitCover.setVehicleId(referenceCover.getVehicleId());
//	                benefitCover.setLocationId(referenceCover.getLocationId());
//	                benefitCover.setRequestReferenceNo(referenceCover.getRequestReferenceNo());
//	                benefitCover.setCdRefNo(referenceCover.getCdRefNo());
//	                benefitCover.setVdRefNo(referenceCover.getVdRefNo());
//	                benefitCover.setMsrefno(referenceCover.getMsrefno());
//	                benefitCover.setCreatedBy(referenceCover.getCreatedBy());
//	                benefitCover.setCalcType(referenceCover.getCalcType());
//	                benefitCover.setCurrency(referenceCover.getCurrency());
//	                benefitCover.setExchangeRate(referenceCover.getExchangeRate());
//	                benefitCover.setEffectiveDate(referenceCover.getEffectiveDate());
//	                benefitCover.setPolicyEndDate(referenceCover.getPolicyEndDate());
//	                benefitCover.setProRataYn(referenceCover.getProRataYn());
//	                benefitCover.setProRata(referenceCover.getProRata());
//
//	                // Benefit cover specific fields
//	                benefitCover.setCoverId(String.valueOf(master.getCoverId()));
//	                benefitCover.setCoverName(master.getCoverName());
//	                benefitCover.setCoverDesc(master.getCoverDesc());
//	                benefitCover.setCoverageType("A");
//	                benefitCover.setCoverageLimit(master.getCoverageLimit() == null 
//	                                              ? BigDecimal.ZERO : master.getCoverageLimit());
//	                benefitCover.setIsselected("Y");
//	                benefitCover.setIsSubCover("N");
//	                benefitCover.setDependentCoverId("0");
//	                benefitCover.setDependentCoveryn("N");
//	                benefitCover.setMultiSelectYn("N");
//
//	                // All premiums zero — benefit cover, no premium
//	                benefitCover.setRate(0.0d);
//	                benefitCover.setSumInsured(BigDecimal.ZERO);
//	                benefitCover.setPremiumAfterDiscount(BigDecimal.ZERO);
//	                benefitCover.setPremiumBeforeDiscount(BigDecimal.ZERO);
//	                benefitCover.setPremiumExcluedTax(BigDecimal.ZERO);
//	                benefitCover.setPremiumIncludedTax(BigDecimal.ZERO);
//	                benefitCover.setPremiumAfterDiscountLC(BigDecimal.ZERO);
//	                benefitCover.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
//	                benefitCover.setPremiumExcluedTaxLC(BigDecimal.ZERO);
//	                benefitCover.setPremiumIncludedTaxLC(BigDecimal.ZERO);
//
//	                benefitCovers.add(benefitCover);
//	            });
//	        });
//	        resultCovers.addAll(benefitCovers);
//	        System.out.println("Benefit covers added in calc: " + benefitCovers.size());
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        System.out.println("loadBenefitCovers error: " + e.getMessage());
//	    }
//	}
//	
//	public List<SectionCoverMaster> getBySectionCoverId(String Company_id,Integer product_id,Integer Sectionid,List<Integer> coverIdsToExclude) {
//		List<SectionCoverMaster> list = new ArrayList<SectionCoverMaster>();
//		String pattern = "#####0.00";
//		DecimalFormat df = new DecimalFormat(pattern);
//		
//		String patternn = "#####0.0000";
//		DecimalFormat df1 = new DecimalFormat(patternn);
//		
//		try {
//			Date today  =  new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 50);
//			today = cal.getTime();
//
//			
//			// Find Latest Record
//			CriteriaBuilder cb2 = em.getCriteriaBuilder();
//			CriteriaQuery<SectionCoverMaster> query2 = cb2.createQuery(SectionCoverMaster.class);
//
//			// Find All
//			Root<SectionCoverMaster> b2 = query2.from(SectionCoverMaster.class);
//
//			// Effective Date Max Filter
//			
//			Subquery<Long> amendId = query2.subquery(Long.class);
//			Root<SectionCoverMaster> ocpm2 = amendId.from(SectionCoverMaster.class);
//			amendId.select(cb2.max(ocpm2.get("amendId")));
//			Predicate a7 = cb2.equal(ocpm2.get("coverId"), b2.get("coverId"));
//			Predicate a8 = cb2.equal(ocpm2.get("sectionId"), b2.get("sectionId"));
//			Predicate a9 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
//			Predicate a10 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
//			Predicate a11 = cb2.equal(ocpm2.get("subCoverId"), b2.get("subCoverId"));
//			Predicate a13 = cb2.equal(ocpm2.get("agencyCode"), b2.get("agencyCode"));
//			Predicate a14 = cb2.equal(ocpm2.get("branchCode"), b2.get("branchCode"));
//			amendId.where(a7,a8,a9,a10,a11,a13,a14);
//
//			// Select
//			query2.select(b2);
//
//			// Order By
//			List<Order> orderList2 = new ArrayList<Order>();
//			orderList2.add(cb2.desc(b2.get("effectiveDateEnd")));
//
//			// Where
//			Predicate n5 = cb2.equal(b2.get("amendId"),amendId);
//			Predicate n6 =cb2.equal(b2.get("subCoverId"), "0");
//			Predicate n7 = cb2.equal(b2.get("productId"),product_id);
//			Predicate n14 = cb2.equal(b2.get("companyId"), Company_id);
//			Predicate n15 = cb2.equal(b2.get("sectionId"), Sectionid);
//			Predicate n16 = cb2.equal(b2.get("coverageType"), "A");
//			Predicate n18 = cb2.equal(b2.get("status"), "Y");
//
//			Predicate n17;
//			if (coverIdsToExclude != null && !coverIdsToExclude.isEmpty()) {
//			    n17 = cb2.not(b2.get("coverId").in(coverIdsToExclude));
//			} else {
//			    n17 = cb2.conjunction(); // No exclusion
//			}
//			Predicate[] predicatesArray = new Predicate[] { n5, n6, n7, n14, n15, n16,n17,n18 };
//
//		
//			query2.where(predicatesArray).orderBy(orderList2);
//
//			// Get Result
//			TypedQuery<SectionCoverMaster> result2 = em.createQuery(query2);
//			list = result2.getResultList();
//			list.stream().distinct().collect(Collectors.toList());
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Exception is ---> " + e.getMessage());
//			return null;
//		}
//		return list;
//	}
//
//	private void validateData() throws Exception {
//		if ((commontbl == null || commontbl.isEmpty()) || (vehicles == null || vehicles.isEmpty())
//				|| (customers == null || customers.isEmpty())) {
//			throw new Exception("Exception :: onetime table not inserted");
//		}
//	}
//
//	private void processCoverDependents(CalcEngine engine,
//            String dependCover,
//            List<Tuple> totalCoversTuple,
//            List<Tuple> taxes,
//            List<Tuple> customerChoiceTaxes,
//            String promocode,
//            TaxUtils taxUtils,
//            TaxRemover taxRemover,
//            List<Cover> resultCovers) {
//
//		// Filter covers by dependent flag (N / Y)
//		List<Tuple> covers = totalCoversTuple.parallelStream()
//		.filter(t -> dependCover.equals(t.get("dependentCoverYn").toString()))
//		.toList();
//		
//		if (covers.isEmpty()) {
//		return;
//		}
//		
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- Discount Block start :---->");
//		List<Discount> discounts = generateDiscounts(engine, covers, promocode);
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- Discount Block end :---->");
//		
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- Loading Block start :---->");
//		List<Loading> loadings = generateLoadings(engine, covers);
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- Loading Block end :---->");
//		
//		// ----------  NON-SUB COVERS ("N") – SAME AS BEFORE ----------
//		Map<String, List<Cover>> nonSubCovers = splitCovers(engine, covers, "N");
//		List<Cover> nonSubCoverList = nonSubCovers.get("N");
//		
//		
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- attachDiscountsLoadingsTaxes  Block start :---->");
//		attachDiscountsLoadingsTaxes(nonSubCoverList, discounts, loadings, taxes, taxUtils);
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- attachDiscountsLoadingsTaxes  Block end :---->");
//		
//		// ----------  SUB COVERS ("Y") – CHILDREN → PARENT WITH SUBCOVERS ----------
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- SubCover split Block start :---->");
//		
//		// Step 1: build CHILD subcovers from tuples (no parents yet)
//		SplitSubCoverUtil splitY = new SplitSubCoverUtil("Y",
//		engine.getEffectiveDate(), engine.getPolicyEndDate());
//		
//		List<Cover> childSubCovers = covers.parallelStream()
//		.map(splitY)
//		.filter(Objects::nonNull)
//		.collect(Collectors.toList());
//		
//		// Step 2: attach discounts / loadings / taxes to CHILDREN
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- attachDiscountsLoadingsTaxes 2 Block start :---->");
//		attachDiscountsLoadingsTaxes(childSubCovers, discounts, loadings, taxes, taxUtils);
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- attachDiscountsLoadingsTaxes 2 Block end :---->");
//		
//		// Step 3: group children by coverId and create PARENT covers with subcovers list
//		List<Cover> parentSubCovers = buildParentsWithSubcovers(childSubCovers);
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- SubCover split Block end :---->");
//		
//		// ---------- MERGE & CALCULATE ----------
//		List<Cover> mergedCovers = new ArrayList<>();
//		
//		if (nonSubCoverList != null) {
//		mergedCovers.addAll(nonSubCoverList);
//		}
//		if (parentSubCovers != null) {
//		mergedCovers.addAll(parentSubCovers);
//		}
//		
//		// Remove excluded taxes, etc.
//		mergedCovers = mergedCovers.stream()
//		.peek(taxRemover)
//		.collect(Collectors.toList());
//		
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- calc engine Block start :---->");
//		
//		CoverCalculator calc = new CoverCalculator();
//		calc.setEngine(engine, resultCovers, commontbl, vehicles, customers, prorata,
//		ratingutil, decimalFormat, drivers, customerChoiceTaxes);
//		
//		mergedCovers.parallelStream().forEach(calc);
//		mergedCovers.removeIf(Cover::isNotsutable);
//		
//		resultCovers.addAll(mergedCovers);
//		resultCovers.sort(Comparator.comparing(Cover::getCoverageType));
//		
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//		+ " <---- calc engine Block end :---->");
//}
//	
//	private List<Cover> buildParentsWithSubcovers(List<Cover> childSubCovers) {
//	    if (childSubCovers == null || childSubCovers.isEmpty()) {
//	        return new ArrayList<>();
//	    }
//
//	    // Only rows that actually have subCoverId
//	    List<Cover> validChildren = childSubCovers.stream()
//	            .filter(c -> c.getSubCoverId() != null && !"0".equals(c.getSubCoverId()))
//	            .collect(Collectors.toList());
//
//	    if (validChildren.isEmpty()) {
//	        return new ArrayList<>();
//	    }
//
//	    Map<String, List<Cover>> byCoverId = validChildren.stream()
//	            .collect(Collectors.groupingBy(Cover::getCoverId));
//
//	    List<Cover> parents = new ArrayList<>();
//
//	    byCoverId.forEach((coverId, children) -> {
//	        // children are the actual subcovers; make sure they are treated as such
//	        children.forEach(c -> c.setIsSubCover("N"));
//
//	        // Clone first child as base for parent
//	        Cover parent = (Cover) org.springframework.util.SerializationUtils.clone(children.get(0));
//
//	        parent.setIsSubCover("Y");
//	        parent.setSubcovers(children);
//	        parent.setSubCoverId(null);
//	        parent.setSubCoverDesc(null);
//	        parent.setSubCoverName(null);
//	        parent.setDiscounts(null);
//	        parent.setLoadings(null);
//	        parent.setTaxes(null);
//
//	        parents.add(parent);
//	    });
//
//	    return parents;
//	}
//
//
//
//	private List<Discount> generateDiscounts(CalcEngine engine, List<Tuple> covers, String promocode) {
//		SplitDiscountUtils util = new SplitDiscountUtils(engine.getEffectiveDate(), engine.getPolicyEndDate(),
//				promocode);
//		return covers.parallelStream().map(util).filter(Objects::nonNull)
//				.peek(d -> d.setEffectiveDate(engine.getEffectiveDate())).toList();
//	}
//
//	private List<Loading> generateLoadings(CalcEngine engine, List<Tuple> covers) {
//		SplitLoadingUtils util = new SplitLoadingUtils(engine.getEffectiveDate(), engine.getPolicyEndDate());
//		return covers.parallelStream().map(util).filter(Objects::nonNull).toList();
//	}
//
//	private Map<String, List<Cover>> splitCovers(CalcEngine engine, List<Tuple> covers, String flag) {
//	    SplitSubCoverUtil splitUtil = new SplitSubCoverUtil(
//	            flag,
//	            engine.getEffectiveDate(),
//	            engine.getPolicyEndDate()
//	    );
//
//	    // First: same as before – map tuples → Cover, group by isSubCover
//	    Map<String, List<Cover>> grouped = covers.parallelStream()
//	            .map(splitUtil)
//	            .filter(Objects::nonNull)
//	            .collect(Collectors.groupingBy(Cover::getIsSubCover));
//
//	    // For non-subcovers (“N”), behaviour stays exactly the same
//	    if (!"Y".equalsIgnoreCase(flag)) {
//	        return grouped;
//	    }
//
//	    // For subcovers (“Y”) we must build PARENT covers with subcovers list
//	    List<Cover> rawSubCovers = grouped.get("Y");
//	    if (rawSubCovers == null || rawSubCovers.isEmpty()) {
//	        return grouped;
//	    }
//
//	    // Only those that actually have a subCoverId
//	    List<Cover> usableSubCovers = rawSubCovers.stream()
//	            .filter(c -> c.getSubCoverId() != null && !"0".equals(c.getSubCoverId()))
//	            .toList();
//
//	    if (usableSubCovers.isEmpty()) {
//	        return grouped;
//	    }
//
//	    
//	    List<Cover> distinctParents = usableSubCovers.stream()
//	            .filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
//	            .toList();
//
//	    List<Cover> parentWithSubcovers = new ArrayList<>();
//
//	    for (Cover parentCandidate : distinctParents) {
//
//	        // All children (subcovers) for this coverId
//	       List<Cover> children = usableSubCovers.stream()
//	                .filter(sc -> sc.getCoverId().equals(parentCandidate.getCoverId()))
//	                .toList();
//
//	        // Children are plain subcovers, not “parent” again
//	        children.forEach(c -> c.setIsSubCover("N"));
//
//	        // Build a parent cover with subcovers list
//	        Cover parent = SerializationUtils
//	                .clone(parentCandidate);   // same as your old SerializationUtils.clone
//
//	        parent.setSubcovers(children);
//	        parent.setIsSubCover("Y");
//	        parent.setSubCoverId(null);
//	        parent.setSubCoverDesc(null);
//	        parent.setSubCoverName(null);
//	        parent.setDiscounts(null);
//	        parent.setLoadings(null);
//	        parent.setTaxes(null);
//
//	        parentWithSubcovers.add(parent);
//	    }
//
//	    // Override the "Y" list with properly built parents
//	    grouped.put("Y", parentWithSubcovers);
//
//	    return grouped;
//	}
//
//
//	private void attachDiscountsLoadingsTaxes(List<Cover> covers, List<Discount> discounts, List<Loading> loadings,
//			List<Tuple> taxes, TaxUtils taxUtils) {
//		if (covers == null || covers.isEmpty())
//			return;
//
//		covers.forEach(c -> {
//			List<Discount> ds = discounts.stream().filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//					.peek(d -> d.setSubCoverId(c.getSubCoverId())).toList();
//			c.setDiscounts(ds);
//			
//			
//
//			List<Loading> ls = loadings.stream().filter(l -> l.getLoadingforId().equals(c.getCoverId()))
//					.peek(l -> l.setSubCoverId(c.getSubCoverId())).toList();
//			c.setLoadings(ls);
//
//			if (!"A".equals(c.getCoverageType()) && !"Y".equals(c.getIsTaxExcempted())) {
//				List<Tax> taxList = taxes.stream().map(taxUtils).filter(Objects::nonNull).toList();
//				c.setTaxes(taxList);
//			}
//		});
//	}
//
//	private void adjustMinimumPremium(CalcEngine engine, BigDecimal endtCount, List<Tuple> taxes, TaxUtils taxUtils,
//			List<Cover> covers) {
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
//		        + " <---- adjustMinimumPremium Block  start :---->");
//		List<Tax> taxList = taxes.stream().map(taxUtils).filter(Objects::nonNull).toList();
//		BigDecimal totalPremium = covers.stream().map(Cover::getPremiumExcluedTaxLC).reduce(BigDecimal.ZERO,
//				BigDecimal::add);
//		BigDecimal diff = minimumPremium.subtract(totalPremium, MathContext.DECIMAL32);
//
//		CreateMinimumPremium min = new CreateMinimumPremium(diff, engine, endtCount, taxList);
//		Cover mini = min.create();
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
//		        + " <---- adjustMinimumPremium  calculator Block  start :---->");
//		CoverCalculator calc = new CoverCalculator();
//		calc.setEngine(engine, covers, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat, drivers,
//				taxes);
//		calc.accept(mini);
//
//		covers.add(mini);
//		System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
//		        + " <---- adjustMinimumPremium Block  end :---->");
//	}
//
//	private BigDecimal handleEndorsement(CalcEngine engine, List<Cover> covers) {
//		try {
//			String endtTypeId = Optional.ofNullable(vehicles.get(0).get("endtTypeId")).map(Object::toString).orElse("");
//			if (StringUtils.isBlank(endtTypeId) || "0".equals(endtTypeId))
//				return BigDecimal.ZERO;
//
//			String requestRef = engine.getRequestReferenceNo();
//			String rawTable = ratingutil.getProductIdBasedRawTable(engine);
//			String baseSearch = String.format(
//					"companyId:%s;productId:%s;sectionId:%s;riskId:%s;status:{E,D,RP};requestReferenceNo:%s;locationId:%s",
//					engine.getInsuranceId(), engine.getProductId(), engine.getSectionId(), engine.getVehicleId(),
//					requestRef, StringUtils.defaultIfBlank(engine.getLocationId(), "1"));
//
//			SpecCriteria criteria = crservice.createCriteria(Class.forName(rawTable), baseSearch, "requestReferenceNo");
//			List<Tuple> result = crservice.getResult(criteria, 0, 50);
//
//			BigDecimal endtCount = new BigDecimal(result.get(0).get("endtCount").toString());
//			loadAndRemoveCoversForEndt(engine, covers, result);
//
//			return endtCount;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return BigDecimal.ZERO;
//		}
//	}
//
//	private EserviceMotorDetailsSaveRes buildResponse(CalcEngine engine, List<UWReferrals> uwList,
//			List<MasterReferal> masterList, List<Cover> retc, String isEndt) {
//		EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
//		response.setCoverList(retc);
//		response.setResponse("Saved Successfully");
//		response.setRequestReferenceNo(engine.getRequestReferenceNo());
//		response.setVehicleId(engine.getVehicleId());
//		response.setVdRefNo(engine.getVdRefNo());
//		response.setCdRefNo(engine.getCdRefNo());
//		response.setInsuranceId(engine.getInsuranceId());
//		response.setSectionId(engine.getSectionId());
//		response.setCreatedBy(engine.getCreatedBy());
//		response.setProductId(engine.getProductId());
//		response.setLocationId(engine.getLocationId());
//		response.setMsrefno(engine.getMsrefno());
//		response.setUpdateas(isEndt);
//		response.setUwList(uwList);
//		response.setReferals(masterList);
//		response.setCoverId(engine.getCoverId());
//		return response;
//	}
//
//	/*
//	 * 
//	 * @Autowired private EndtTypeMasterRepository endtTypeRepo;
//	 * 
//	 */
//	private void loadAndRemoveCoversForEndt(CalcEngine engine, List<Cover> retc, List<Tuple> result) {
//		try {
//
//			if (!result.isEmpty()) {
//
//				// String endtPrevPolicyNo=result.get(0).get("endtPrevPolicyNo").toString();
//				String endtPrevQuoteNo = result.get(0).get("endtPrevQuoteNo").toString();
//
//				String endtDesc = result.get(0).get("endorsementTypeDesc").toString();
//				String endtTypeId = result.get(0).get("endorsementType").toString();
//				BigDecimal endtCount = new BigDecimal(result.get(0).get("endtCount").toString());
//				Date date = null;
//				try {
//					date = (Date) result.get(0).get("policyStartDate");
//					currentPolicyPeriod = findNoOfDaysInDate(date, engine.getPolicyEndDate());
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				String originalPolicyNo = result.get(0).get("originalPolicyNo").toString();
//				List<PolicyCoverDataEndt> oldPolicyData = null;
//				if (!"0".equals(engine.getCoverId())) {
//					oldPolicyData = policyCoverEndtRepo
//							.findByPolicyNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByCoverIdAsc(
//									originalPolicyNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
//									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()),
//									Integer.parseInt(engine.getCoverId()));
//				} else {
//					oldPolicyData = policyCoverEndtRepo
//							.findByPolicyNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdOrderByCoverIdAsc(
//									originalPolicyNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
//									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()));
//				}
//				EndtTypeMaster endtmaster = ratingutil.getEndtMasterData(engine.getInsuranceId(), engine.getProductId(),
//						endtTypeId);
//
//				retc.stream().forEach(i -> i.setEndtCount(endtCount));
//				// find Prev Quote Data
//				List<PolicyCoverData> oldPolicyCovers = null;
//				if (!"0".equals(engine.getCoverId())) {
//					oldPolicyCovers = coverDataRepo
//							.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusAndCoverIdOrderByCoverIdAsc(
//									endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
//									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()),
//									"Y", Integer.parseInt(engine.getCoverId()));
//				} else {
//					oldPolicyCovers = coverDataRepo
//							.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
//									endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
//									Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()),
//									"Y");
//				}
//				List<Tuple> taxes = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
//				TaxUtils tzx = new TaxUtils(endtCount, "");
//				TaxUtils tzxEndt = new TaxUtils(endtCount, endtTypeId);
//				List<Tax> taxey = taxes.stream().map(tzx).filter(t -> t != null).collect(Collectors.toList());
//
//				List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
//				TaxRemover taxRemov = new TaxRemover(excludedTaxes, null);
//
//				List<Tax> tzxeyEndt = taxes.stream().map(tzxEndt).filter(t -> t != null).collect(Collectors.toList());
//
//				// CoverFromPolicy
//				
//				List<PolicyCoverData> basecovers = oldPolicyCovers.stream()
//						.filter(d -> !("T".equals(d.getCoverageType()) || "D".equals(d.getCoverageType())
//								|| "L".equals(d.getCoverageType()) || "E".equals(d.getCoverageType())
//								|| "P".equals(d.getCoverageType())
//								|| d.getCoverId().compareTo(Integer.valueOf("945")) == 0))
//						.collect(Collectors.toList());
//
//				List<PolicyCoverData> countPolicy = oldPolicyCovers.stream()
//						.filter(d -> (d.getCoverId().compareTo(Integer.valueOf("945")) == 0))
//						.collect(Collectors.toList());
//				List<Cover> countCover = retc.stream().filter(t -> "945".equals(t.getCoverId()))
//						.collect(Collectors.toList());
//				if (countCover.size() > 0 && countPolicy.size() > 0 && countCover.get(0).getPremiumExcluedTaxLC()
//						.compareTo(countPolicy.get(0).getPremiumExcludedTaxLc()) == 0) {
//
//					retc.removeIf(t -> "945".equals(t.getCoverId()));
//				}
//				Long days=0L;
//				if((basecovers==null || basecovers.isEmpty()) && ("851".equalsIgnoreCase(endtTypeId) || "854".equalsIgnoreCase(endtTypeId) ||  "846".equalsIgnoreCase(endtTypeId) ))
//				{
//					Set<Integer> baseCoverIds=null;
//					boolean contains=false;
//					
//					List<PolicyCoverData> allSection = new ArrayList<PolicyCoverData>();
//					
//					List<Endorsement> endorsements = new ArrayList<Endorsement>();
//
//					if ("854".equalsIgnoreCase(endtTypeId)) {
//						allSection = coverDataRepo
//								.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndStatusOrderByCoverIdAsc(
//										endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()),
//										engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
//										 "Y");
//					
//					}
//					else if("846".equalsIgnoreCase(endtTypeId)){
//						allSection = coverDataRepo.findByQuoteNoAndCompanyIdAndProductIdAndStatusOrderByCoverIdAsc(
//										endtPrevQuoteNo,engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
//										 "Y");
//						
//					}
//					else {
//						allSection = coverDataRepo
//								.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
//										endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()),
//										engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
//										Integer.parseInt(engine.getSectionId()), "Y");
//						baseCoverIds = basecovers.stream()
//						        .map(PolicyCoverData::getCoverId)
//						        .collect(Collectors.toSet());
//						try {
//			                 contains = baseCoverIds.contains(Integer.valueOf(engine.getCoverId()));
//			            } catch (NumberFormatException e) {	
//			              e.printStackTrace();
//			            }
//					}
//					if (!"0".equals(engine.getCoverId()))
//					{
//						
//					isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate()).compareTo(getZeroTimeDate(allSection.get(0).getCoverPeriodTo())) != 0 ? true : false;
//					oldPolicyPeriod = findNoOfDaysInDate(date, allSection.get(0).getCoverPeriodTo());
//					AddCreateEndorsment createEndt = new AddCreateEndorsment(endtmaster, endtCount, tzxeyEndt, retc,
//							engine.getPolicyEndDate(), engine.getCoverId(),days,"N", originalPolicyNo,engine);
//					Endorsement currentEndt = createEndt.create();
//
//					endorsements.add(currentEndt);
//					
//					retc.stream()
//				    .filter(t -> t.getCoverId().equalsIgnoreCase(currentEndt.getEndorsementforId().toString()))
//				    .forEach(t -> {
//				        t.setEndorsements(endorsements);
//				        t.setUserOpt("N");
//				    });
//					}
//					else
//					{
//						List<Cover> ob = retc.stream()
//						.filter(t -> !("T".equals(t.getCoverageType()) || "D".equals(t.getCoverageType())
//								|| "L".equals(t.getCoverageType()) || "E".equals(t.getCoverageType())
//								|| "P".equals(t.getCoverageType())
//								))
//						.collect(Collectors.toList());
//						for(Cover b : ob) 
//						{
//							isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate())
//									.compareTo(getZeroTimeDate(allSection.get(0).getCoverPeriodTo())) != 0 ? true : false;
//							oldPolicyPeriod = findNoOfDaysInDate(date, allSection.get(0).getCoverPeriodTo());
//							prevPolicyEndDate = allSection.get(0).getCoverPeriodTo();
//							AddCreateEndorsment createEndt = new AddCreateEndorsment(endtmaster, endtCount, tzxeyEndt, retc,
//									engine.getPolicyEndDate(), b.getCoverId(),days,"N", originalPolicyNo,engine);
//							Endorsement currentEndt = createEndt.create();
//
//							endorsements.add(currentEndt);
//							retc.stream()
//						    .filter(t -> t.getCoverId().equalsIgnoreCase(currentEndt.getEndorsementforId().toString()))
//						    .forEach(t -> {
//						        t.setEndorsements(endorsements);
//						        t.setUserOpt("N");
//						    });
//						}
//						
//						
//					}
//				}
//				else if("0".equals(engine.getCoverId()) && "851".equalsIgnoreCase(endtTypeId))
//				{
//					Set<Integer> baseCoverIds = basecovers.stream()
//					        .map(PolicyCoverData::getCoverId)
//					        .collect(Collectors.toSet());
//					days = oldPolicyData.stream().filter(o -> o.getDiscLoadId() == 851)
//							.map(PolicyCoverDataEndt::getNoOfDays).filter(Objects::nonNull).findFirst().orElse(null);
//					List<PolicyCoverData> allSection = new ArrayList<PolicyCoverData>();
//					
//					
//					allSection = coverDataRepo
//							.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
//									endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()),
//									engine.getInsuranceId(), Integer.parseInt(engine.getProductId()),
//									Integer.parseInt(engine.getSectionId()), "Y");
//					List<Cover> ob = retc.stream()
//					        .filter(t -> !("T".equals(t.getCoverageType()) 
//					                || "D".equals(t.getCoverageType())
//					                || "L".equals(t.getCoverageType()) 
//					                || "E".equals(t.getCoverageType())
//					                || "P".equals(t.getCoverageType()))).collect(Collectors.toList());
//					for(Cover b : ob) 
//					{ 
//						boolean contains=false;
//					
//					try {
//						contains = baseCoverIds.contains(Integer.parseInt(b.coverId));
//					} catch (NumberFormatException e) {
//						contains = false;
//					}
//
//					String key = contains ? "Y" : "N";
//						List<Endorsement> endorsements = new ArrayList<Endorsement>();
//						isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate())
//								.compareTo(getZeroTimeDate(allSection.get(0).getCoverPeriodTo())) != 0 ? true : false;
//						oldPolicyPeriod = findNoOfDaysInDate(date, allSection.get(0).getCoverPeriodTo());
//						prevPolicyEndDate = allSection.get(0).getCoverPeriodTo();
//						AddCreateEndorsment createEndt1 = new AddCreateEndorsment(endtmaster, endtCount, tzxeyEndt, retc,
//								engine.getPolicyEndDate(), b.getCoverId(),days,key, key,engine);
//						Endorsement currentEndt1 = createEndt1.create();
//
//						endorsements.add(currentEndt1);
//						retc.stream()
//					    .filter(t -> t.getCoverId().equalsIgnoreCase(currentEndt1.getEndorsementforId().toString()))
//					    .forEach(t -> {
//					        t.setEndorsements(endorsements);
//					        t.setUserOpt(key);
//					    });
//				}
//				}
//				if(!("0".equals(engine.getCoverId()) && "851".equalsIgnoreCase(endtTypeId))) {				
//					for (PolicyCoverData d : basecovers) {
//					List<Cover> operatedList = new ArrayList<Cover>();
//					// try {
//					isPolicyPeriod = getZeroTimeDate(engine.getPolicyEndDate())
//							.compareTo(getZeroTimeDate(d.getCoverPeriodTo())) != 0 ? true : false;
//					/*
//					 * }catch (Exception e) { e.printStackTrace(); }
//					 */
//					
//					
//					
//					String opp = d.getNoOfDays() != null ? d.getNoOfDays().toString() : "";
//					Date coverPeriodFrom = d.getCoverPeriodFrom();
//					Date coverPeriodTo = d.getCoverPeriodTo();
//					oldPolicyPeriod = findNoOfDaysInDate(date, coverPeriodTo);
////						oldPolicyPeriod=StringUtils.isNotBlank(opp)?opp:StringUtils.isNotBlank(oldPolicyPeriod)?oldPolicyPeriod:"";
//					prevPolicyEndDate = d.getCoverPeriodTo();
//
//					DiscountFromPolicy discountUtil = new DiscountFromPolicy();
//					List<Discount> discounts = oldPolicyCovers.stream()
//							.filter(r -> Objects.equals(d.getCoverId(), r.getCoverId())).map(discountUtil)
//							.filter(dx -> dx != null).collect(Collectors.toList());
//
//					LoadingFromPolicy loadingUtil = new LoadingFromPolicy();
//					List<Loading> loadings = oldPolicyCovers.stream()
//							.filter(r -> Objects.equals(d.getCoverId(), r.getCoverId())).map(loadingUtil)
//							.filter(dx -> dx != null).collect(Collectors.toList());
//
//					List<Endorsement> endorsements = new ArrayList<Endorsement>();
//					List<PolicyCoverDataEndt> coverData = oldPolicyData.stream()
//							.filter(i -> i.getCoverId().compareTo(d.getCoverId()) == 0).collect(Collectors.toList());
//
//					CreateEndorsment createEndt = new CreateEndorsment(endtmaster, endtCount, tzxeyEndt, coverData, d,
//							engine.getPolicyEndDate(),engine);
//					Endorsement currentEndt = createEndt.create();
//					endorsements.add(currentEndt);
//					String endtProRata="",endtProRataDec="",siorpre="" ;
//					List<Cover> collect = retc.stream().filter(t -> t.getCoverId().equalsIgnoreCase(d.getCoverId().toString())).collect(Collectors.toList());
//					if(collect!=null && !collect.isEmpty())
//					{
//						endtProRata=collect.get(0).getEndtProRataYn();
//						endtProRataDec=collect.get(0).getEndtProRataDesc();
//						siorpre=collect.get(0).getDependentCoveSIorPI();
//					}
//					CoverFromPolicy coverUtil = new CoverFromPolicy("",endtProRata,endtProRataDec,siorpre );
//					List<Cover> covers = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
//							.map(coverUtil).filter(dx -> dx != null).collect(Collectors.toList());
//					/*
//					 * List<Cover> oldTax = covers.stream().filter(c ->
//					 * "T".equals(c.getCoverageType())).collect(Collectors.toList());
//					 * covers.removeAll(oldTax);
//					 */
//
//					covers.stream()
//							.filter(c -> (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")))
//							.forEach(c -> c.setTaxes(taxey));
//					covers.forEach(c -> c.setEndtCount(endtCount));
//					covers.forEach(c -> c.setEndorsements(endorsements));// Existing Endorsement
//					covers.forEach(c -> c.setDiscounts(discounts));
//					covers.forEach(c -> c.setLoadings(loadings));
//					covers.forEach(c -> c.setPolicyEndDate(engine.getPolicyEndDate()));
//					covers.stream().forEach(taxRemov);
//
//					retc.stream().filter(r -> d.getCoverId() == Integer.parseInt(r.getCoverId())).forEach(item -> {
//						operatedList.add(item);
//						covers.stream().forEach(c -> {
//							c.setCoverageLimit(item.getCoverageLimit());
//							c.setEffectiveDate(engine.getEffectiveDate());
////							c.setPolicyEndDate(engine.getPolicyEndDate());
//						});
//					});
//					retc.removeAll(operatedList);
//					retc.addAll(covers);
//				}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//
//	@Transactional
//	public EserviceMotorDetailsSaveRes endorsementCalculator(CalcEngine request, BigDecimal endtCount,
//			String endtTypeId, Boolean isPolicyPeriod) {
//		try {
//			List<Cover> retc = new ArrayList<Cover>();
//
//			List<String> dependedcovers = new ArrayList<String>();
//			EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
//
//			dependedcovers.add("N");
//			dependedcovers.add("Y");
//			List<FactorRateRequestDetails> factors;
//
//			if (!"0".equals(request.getCoverId())) {
//				factors = repository
//						.findByRequestReferenceNoAndLocationIdAndVehicleIdAndProductIdAndSectionIdAndCoverId(
//								request.getRequestReferenceNo(), Integer.valueOf(request.getLocationId()),
//								Integer.valueOf(request.getVehicleId()), Integer.valueOf(request.getProductId()),
//								Integer.valueOf(request.getSectionId()), Integer.valueOf(request.getCoverId()));
//			} else {
//				factors = repository.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
//						request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()),
//						Integer.valueOf(request.getProductId()), Integer.valueOf(request.getSectionId()));
//			}
//
//			// TaxFromFactor tzx=new TaxFromFactor();
//			List<Tuple> taxes = ratingutil.LoadTax(request, NORMAL_TAX_LIST);
//			List<Tuple> taxesEndt = ratingutil.LoadTax(request, ENDT_TAX_LIST);
//
//			List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(request, NORMAL_TAX_LIST);
//			List<Tuple> excludedTaxesEndt = ratingutil.LoadExcludedTax(request, ENDT_TAX_LIST);
//
//			TaxRemover taxRemov = new TaxRemover(excludedTaxes, excludedTaxesEndt);
//			TaxUtils tzx = new TaxUtils(endtCount, "");
//			TaxUtils tzxsa = new TaxUtils(endtCount, endtTypeId);
//
//			for (String dependcover : dependedcovers) {
//				List<Cover> totalcovers = new ArrayList<Cover>();
//				List<FactorRateRequestDetails> covers = factors.stream()
//						.filter(f -> dependcover.equals(f.getDependentCoverYn())).collect(Collectors.toList());
//
//				DiscountFromFactor discountUtil = new DiscountFromFactor();
//				List<Discount> discounts = covers.stream().map(discountUtil).filter(d -> d != null)
//						.collect(Collectors.toList());
//				LoadingFromFactor loadingtuils = new LoadingFromFactor();
//				List<Loading> loadings = covers.stream().map(loadingtuils).filter(d -> d != null)
//						.collect(Collectors.toList());
//				EndtFromFactor endtUtil = new EndtFromFactor();
//				List<Endorsement> endorsements = covers.stream().map(endtUtil).filter(d -> d != null)
//						.collect(Collectors.toList());
//
//				CoverFromFactor splitsub = new CoverFromFactor("N");
//				Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
//						.collect(Collectors.groupingBy(Cover::getIsSubCover));
//				if (!nonSubcovers.isEmpty()) {
//					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
//					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Discount> ds = discounts.stream()
//									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//							// List<Tax> taxey =
//							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//							c.setDiscounts(ds);
//							// c.setTaxes(taxey);
//						}
//					}
//					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//							// List<Tax> taxey =
//							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//							c.setLoadings(ds);
//							// c.setTaxes(taxey);
//						}
//					}
//
//					if (!endorsements.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Endorsement> ds = endorsements.stream()
//									.filter(d -> d.getEndorsementforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//							// List<Tax> taxey =
//							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//
//							// TaxFromFactor endttaxUtil = new TaxFromFactor();
//							if (ds != null && ds.size() > 0) {
//
//								for (Endorsement e : ds) {
//
//									// only for endrose we cannt use cover objs tax cover wontbe list.
//									/*
//									 * List<Tax> txx = factors.stream() .filter(r -> (r.getDiscLoadId() ==
//									 * Integer.parseInt(e.getEndorsementId()) && r.getCoverId() ==
//									 * Integer.parseInt(e.getEndorsementforId()) && r.getEndtCount().intValue() ==
//									 * e.getEndtCount().intValue())) .map(endttaxUtil).filter(dx -> (dx != null &&
//									 * !"0".equals(dx.getTaxId()))) .collect(Collectors.toList());
//									 */
//									List<Tax> taxey = taxesEndt.stream().map(tzxsa).filter(d -> d != null)
//											.collect(Collectors.toList());
//									e.setTaxes(taxey);
//								}
//							}
//
//							c.setEndorsements(ds);
//							// c.setTaxes(taxey);
//						}
//					}
//
//					if (!noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
//								List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
//										.collect(Collectors.toList());
//								c.setTaxes(taxey);
//							}
//						}
//					}
//				}
//
//				splitsub = new CoverFromFactor("Y");
//				Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
//						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
//						.collect(Collectors.groupingBy(Cover::getIsSubCover));
//				if (!subcovers.isEmpty()) {
//					List<Cover> noncovers = subcovers.get("Y"); // noncovers
//					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Discount> ds = discounts.stream()
//									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//
//							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
//									.collect(Collectors.toList());
//							// List<Tax> taxez =
//							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//							c.setDiscounts(dss);
//							// c.setTaxes(taxez);
//						}
//					}
//
//					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//
//							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
//									.collect(Collectors.toList());
//							c.setLoadings(dss);
//						}
//					}
//
//					if (!endorsements.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Endorsement> ds = endorsements.stream()
//									.filter(d -> d.getEndorsementforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//							List<Endorsement> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
//									.collect(Collectors.toList());
//
//							// TaxFromFactor endttaxUtil = new TaxFromFactor();
//							if (dss != null && dss.size() > 0) {
//
//								for (Endorsement e : dss) {
//									/*
//									 * List<Tax> txx = covers.stream() .filter(r -> (r.getDiscLoadId() ==
//									 * Integer.parseInt(e.getEndorsementId()) && r.getCoverId() ==
//									 * Integer.parseInt(e.getEndorsementforId()) && r.getEndtCount().intValue() ==
//									 * e.getEndtCount().intValue())) .map(endttaxUtil).filter(dx -> dx !=
//									 * null).collect(Collectors.toList());
//									 */
//									List<Tax> taxey = taxesEndt.stream().map(tzxsa).filter(d -> d != null)
//											.collect(Collectors.toList());
//									e.setTaxes(taxey);
//								}
//							}
//
//							c.setEndorsements(dss);
//							// c.setTaxes(taxey);
//						}
//					}
//
//					if (!noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
//								List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
//										.collect(Collectors.toList());
//								c.setTaxes(taxey);
//							}
//						}
//					}
//
//					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
//							.collect(Collectors.toList());
//					List<Cover> subcov = new ArrayList<Cover>();
//					for (Cover cover : d) {
//						List<Cover> subcover = noncovers.stream()
//								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
//						subcover.stream().forEach(s -> s.setIsSubCover("N"));
//						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
//						Cover newcover = SerializationUtils.clone(cover);
//						newcover.setSubcovers(subcover);
//						newcover.setIsSubCover("Y");
//						newcover.setSubCoverId(null);
//						newcover.setSubCoverDesc(null);
//						newcover.setSubCoverName(null);
//						newcover.setDiscounts(null);
//						newcover.setLoadings(null);
//						newcover.setTaxes(null);
//						subcov.add(newcover);
//					}
//					subcovers.put("Y", subcov);
//				}
//
//				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
//					totalcovers = subcovers.get("Y");
//					totalcovers.addAll(nonSubcovers.get("N"));
//				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
//					totalcovers = nonSubcovers.get("N");
//				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
//					totalcovers = subcovers.get("Y");
//				}
//
//				totalcovers.stream().forEach(taxRemov);
//				EndtCoverCalculator calc = new EndtCoverCalculator(isPolicyPeriod, oldPolicyPeriod, currentPolicyPeriod,
//						prevPolicyEndDate);
//				List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(request);
//
//				if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
//						|| (customers == null || customers.size() == 0)) {
//					loadOnetimetable(request);
//				}
//				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil,
//						request.getEffectiveDate(), decimalFormat, drivers, customerChoiceTaxes);
//
//				totalcovers.stream().filter(t -> "Y".equals(t.getStatus())).forEach(calc);
//				// remove error records
//				totalcovers.removeIf(ll -> (ll.isNotsutable()));
//				retc.addAll(totalcovers);
//				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
//				retc.sort(comp);
//				response.setCoverList(retc);
//				response.setResponse("Saved Successfully");
//				response.setRequestReferenceNo(request.getRequestReferenceNo());
//				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
//				response.setVehicleId(request.getVehicleId());
//				response.setVdRefNo(request.getVdRefNo());
//				response.setCdRefNo(request.getCdRefNo());
//				response.setInsuranceId(request.getInsuranceId());
//				response.setSectionId(request.getSectionId());
//				response.setCreatedBy(request.getCreatedBy());
//				response.setProductId(request.getProductId());
//
//				response.setLocationId(request.getLocationId());
//				response.setMsrefno(request.getMsrefno());
//				response.setUpdateas("admin");
//				response.setCoverId(request.getCoverId());
//				// response.setUwList(referr);
//
//				fservice.saveFactorRateRequestDetails(response);
//
//			}
//
//			try {
//
//				// Update Premium,referral
//
//				return response;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//
//	public  EserviceMotorDetailsSaveRes referalCalculator(CalcEngine request, String token) {
//		try {
//			List<UWReferrals> referr = referal.underwriterReferral(request);
//
//			List<MasterReferal> masterreferral = null;
//			try {
//				masterreferral = referal.masterreferral(request, token);
//			} catch (ClassNotFoundException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			List<Cover> retc = new ArrayList<Cover>();
//
//			loadOnetimetable(request);
//			
//			
//			if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
//					|| (customers == null || customers.size() == 0)) {
//				System.out.println("::: Exception :: ");
//				System.out.println("commontbl size: " + (commontbl == null ? "NULL" : commontbl.size()));
//				System.out.println("vehicles size: " + (vehicles == null ? "NULL" : vehicles.size()));
//				System.out.println("customers size: " + (customers == null ? "NULL" : customers.size()));
//				throw new Exception();
//
//				/*
//				 * throw
//				 * CoverException.builder().message("Exception :: onetime table not inserted")
//				 * .isError(true).build();
//				 */
//			}
//			List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(request);
//			List<String> dependedcovers = new ArrayList<String>();
//
//			dependedcovers.add("N");
//			dependedcovers.add("Y");
//
//			List<FactorRateRequestDetails> factors = repository
//					.findByRequestReferenceNoAndLocationIdAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
//							request.getRequestReferenceNo(), Integer.valueOf(request.getLocationId()),
//							Integer.valueOf(request.getVehicleId()), Integer.valueOf(request.getProductId()),
//							Integer.valueOf(request.getSectionId()));
//
//			/*
//			 * BigDecimal rate = factors.get(0).getRate();
//			 * System.out.println("=====================================================" +
//			 * rate + "===================================="); BigDecimal rate1 =
//			 * factors.get(1).getRate();
//			 * System.out.println("=====================================================" +
//			 * rate1 + "====================================");
//			 */
//			// TaxFromFactor tzx=new TaxFromFactor();
//			/*
//			 * List<Tuple> taxes = ratingutil.LoadTax(request,NORMAL_TAX_LIST); TaxUtils tzx
//			 * = new TaxUtils(BigDecimal.ZERO ,"");
//			 */
//			TaxFromFactor tzx = new TaxFromFactor();
//			for (String dependcover : dependedcovers) {
//				List<Cover> totalcovers = new ArrayList<Cover>();
//				List<FactorRateRequestDetails> covers = factors.stream()
//						.filter(f -> dependcover.equals(f.getDependentCoverYn())).collect(Collectors.toList());
//
//				DiscountFromFactor discountUtil = new DiscountFromFactor();
//				List<Discount> discounts = covers.stream().map(discountUtil).filter(d -> d != null)
//						.collect(Collectors.toList());
//				LoadingFromFactor loadingtuils = new LoadingFromFactor();
//				List<Loading> loadings = covers.stream().map(loadingtuils).filter(d -> d != null)
//						.collect(Collectors.toList());
//
//				CoverFromFactor splitsub = new CoverFromFactor("N");
//				Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
//						.collect(Collectors.groupingBy(Cover::getIsSubCover));
//				if (!nonSubcovers.isEmpty()) {
//					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
//					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Discount> ds = discounts.stream()
//									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//							// List<Tax> taxey =
//							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//							c.setDiscounts(ds);
//							// c.setTaxes(taxey);
//						}
//					}
//					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//							// List<Tax> taxey =
//							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//							c.setLoadings(ds);
//							// c.setTaxes(taxey);
//						}
//					}
//					if (!noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
//								List<Tax> taxey = factors.stream()
//										.filter(d -> (d.getCoverId().toString().equals(c.getCoverId())
//												&& d.getCoverageType().equalsIgnoreCase("T")))
//										.map(tzx).collect(Collectors.toList());
//								c.setTaxes(taxey);
//							}
//						}
//					}
//				}
//
//				splitsub = new CoverFromFactor("Y");
//				Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
//						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
//						.collect(Collectors.groupingBy(Cover::getIsSubCover));
//				if (!subcovers.isEmpty()) {
//					List<Cover> noncovers = subcovers.get("Y"); // noncovers
//					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Discount> ds = discounts.stream()
//									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//
//							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
//									.collect(Collectors.toList());
//							// List<Tax> taxez =
//							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
//							c.setDiscounts(dss);
//							// c.setTaxes(taxez);
//						}
//					}
//
//					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
//									.collect(Collectors.toList());
//							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
//
//							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
//									.collect(Collectors.toList());
//							c.setLoadings(dss);
//						}
//					}
//					if (!noncovers.isEmpty()) {
//						for (Cover c : noncovers) {
//							if (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
//								/*
//								 * List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
//								 * .collect(Collectors.toList()); c.setTaxes(taxey);
//								 */
//
//								List<Tax> taxey = factors.stream()
//										.filter(d -> (d.getCoverId().toString().equals(c.getCoverId())
//												&& d.getCoverageType().equalsIgnoreCase("T")))
//										.map(tzx).collect(Collectors.toList());
//								c.setTaxes(taxey);
//
//							}
//						}
//					}
//
//					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
//							.collect(Collectors.toList());
//					List<Cover> subcov = new ArrayList<Cover>();
//					for (Cover cover : d) {
//						List<Cover> subcover = noncovers.stream()
//								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
//						subcover.stream().forEach(s -> s.setIsSubCover("N"));
//						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
//						Cover newcover = SerializationUtils.clone(cover);
//						newcover.setSubcovers(subcover);
//						newcover.setIsSubCover("Y");
//						newcover.setSubCoverId(null);
//						newcover.setSubCoverDesc(null);
//						newcover.setSubCoverName(null);
//						newcover.setDiscounts(null);
//						newcover.setLoadings(null);
//						newcover.setTaxes(null);
//						subcov.add(newcover);
//					}
//					subcovers.put("Y", subcov);
//				}
//
//				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
//					totalcovers = subcovers.get("Y");
//					totalcovers.addAll(nonSubcovers.get("N"));
//				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
//					totalcovers = nonSubcovers.get("N");
//				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
//					totalcovers = subcovers.get("Y");
//				}
//
//				// CoverCalculator calc=new CoverCalculator();
//
//				AdminCoverCalculator calc = new AdminCoverCalculator();
//				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
//						drivers, customerChoiceTaxes);
//
//				totalcovers.stream().forEach(calc);
//				// remove error records
//				totalcovers.removeIf(ll -> (ll.isNotsutable()));
//				retc.addAll(totalcovers);
//				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
//				retc.sort(comp);
//			}
//			// if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
//			BigDecimal totalPremium = retc.stream()
//					.filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId())
//							&& x.getPremiumExcluedTaxLC() != null))
//					.map(x -> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO, BigDecimal::add);
//			if (totalPremium.compareTo(minimumPremium) < 0) {
//
//				List<Tuple> taxes = ratingutil.LoadTax(request, NORMAL_TAX_LIST);
//				TaxUtils tzxx = new TaxUtils(BigDecimal.ZERO, "");
//				List<Tax> taxey = taxes.stream().map(tzxx).filter(d -> d != null).collect(Collectors.toList());
//				BigDecimal difference = minimumPremium.subtract(totalPremium, MathContext.DECIMAL32);
//				CreateMinimumPremium min = new CreateMinimumPremium(difference, request, factors.get(0).getEndtCount(),
//						taxey);
//				Cover mini = min.create();
//				List<Cover> minies = new ArrayList<Cover>(1);
//				minies.add(mini);
//				CoverCalculator calc = new CoverCalculator();
//				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,
//						drivers, customerChoiceTaxes);
//				minies.stream().forEach(calc);
//				retc.add(mini);
//
//			} else {
//				retc.removeIf(t -> "945".equals(t.getCoverId()));// .stream().filter(t-> "945".equals(t.getCoverId()).de
//			}
//			try {
//				EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
//				response.setCoverList(retc);
//				response.setResponse("Saved Successfully");
//				response.setRequestReferenceNo(request.getRequestReferenceNo());
//				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
//				response.setVehicleId(request.getVehicleId());
//				response.setVdRefNo(request.getVdRefNo());
//				response.setCdRefNo(request.getCdRefNo());
//				response.setInsuranceId(request.getInsuranceId());
//				response.setSectionId(request.getSectionId());
//				response.setCreatedBy(request.getCreatedBy());
//				response.setProductId(request.getProductId());
//				response.setMsrefno(request.getMsrefno());
//				response.setLocationId(request.getLocationId());
//				response.setUpdateas("admin");
//				response.setUwList(referr);
//				response.setReferals(masterreferral);
//				// response.setUwList(referr);
//				response.setCoverId(request.getCoverId());
//				fservice.saveFactorRateRequestDetails(response);
//
//				// Update Premium,referral
//
//				return response;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	@Autowired
//	private MotorDataDetailsRepository motorRepo;
//	@Autowired
//	private SectionDataDetailsRepository sectionRepo;
//
//	public List<DebitAndCredit> commissionCalc(CalcCommission request) {
//		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
//		String policyNo = "";
//		try {
//
//			resList = getOverAllcommissionCalc(request);
//			// resList = getRiskWisecommissionCalc( )
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return resList;
//	}
//
//	public List<DebitAndCredit> getOverAllcommissionCalc(CalcCommission request) {
//		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
//		try {
//			ViewQuoteReq q = new ViewQuoteReq();
//			q.setQuoteNo(request.getQuoteno());
//			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
//			CompanyProductMaster product = getCompanyProductMasterDropdown(v1.getQuoteDetails().getCompanyId(),
//					v1.getQuoteDetails().getProductId().toString());
//			String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//			String emiYn = v1.getQuoteDetails().getEmiYn();
//			String instalment = v1.getQuoteDetails().getInstallmentMonth();
//			List<BranchMaster> branchCode = ratingutil.collectBranchMaster(v1.getQuoteDetails().getCompanyId(),
//					v1.getQuoteDetails().getBranchCode());
//
//			// Not endt
//			if (StringUtils.isBlank(endttypeid) && (emiYn.equalsIgnoreCase("N") || instalment.equalsIgnoreCase("0"))) {
//				List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
//				List<ProductSectionMaster> coreappcode = ratingutil.collectSectionMaster(
//						v1.getQuoteDetails().getCompanyId(), v1.getQuoteDetails().getProductId().toString(),
//						sections.get(0).getSectionId());
//
//				List<MotorDataDetails> list = motorRepo.findByQuoteNo(request.getQuoteno());
//				HomePositionMaster hpm = homeRepo.findByQuoteNo(request.getQuoteno());
//				PersonalInfo pi = piRepo.findByCustomerId(hpm.getCustomerId());
//				String vehUsageCoreappcode = "";
//				String policyNo = "";
//
//				if (request.getProductId().equalsIgnoreCase("5"))
//					vehUsageCoreappcode = getListItemvalue(request.getInsuranceId(), request.getBranchCode(),
//							"MADISON_MOTOR", list.get(0).getMotorUsage(), pi.getPolicyHolderType());
//
//				if (request.getInsuranceId().equalsIgnoreCase("100004")) {
//
//					String itemvalue = getListItemvalue(request.getInsuranceId(), request.getBranchCode(), "POLICY_NO");
//
//					policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),
//							branchCode.get(0).getCoreAppCode(), request.getInsuranceId(), vehUsageCoreappcode,
//							request.getProductId(), itemvalue);
//
//				} else if (request.getInsuranceId().equalsIgnoreCase("100019")) {
//
//					policyNo = genNo.generateUgandaPolicyNo(coreappcode.get(0).getCoreAppCode(),
//							branchCode.get(0).getCoreAppCode());
//				} else {
//					policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),
//							branchCode.get(0).getCoreAppCode());
//				}
//
//				request.setPolicyNo(policyNo);
//			} else { // endt
//
//				request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//			}
//
//			/*
//			 * HomePositionMaster homeData =
//			 * homeRepo.findByQuoteNo(v1.getQuoteDetails().getQuoteNo());
//			 * List<ChartOfAccount> getChartList = getChartList(homeData.getCompanyId()); //
//			 * Source Type Search Condition List<String> directSource = new
//			 * ArrayList<String>(); directSource.add("1"); directSource.add("2");
//			 * directSource.add("3"); boolean directSourceAvailable =
//			 * homeData.getSourceTypeId()!=null &&
//			 * directSource.contains(homeData.getSourceTypeId()) ? true : false ;
//			 * 
//			 * String policyType = "99999"; if (product.getMotorYn().equalsIgnoreCase("M"))
//			 * { List<MotorDataDetails> motors =
//			 * motorRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteno()); policyType
//			 * = motors.size() > 0 ? motors.get(0).getPolicyType() : "99999" ; }
//			 * HomePositionMaster v = homeData ;
//			 * 
//			 * Double commissionPercent = 0.0;
//			 * //commissionPercent=v.getCommissionPercentage().doubleValue(); String loginId
//			 * = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//			 * List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//			 * v.getProductId().toString(), loginId, v.getBrokerCode(), policyType);
//			 * 
//			 * 
//			 * // Premia Broker , Agent Condition if(directSourceAvailable == true ) {
//			 * //commissionPercent=12.5; String commission = getListItem
//			 * (homeData.getCompanyId() , homeData.getBranchCode()
//			 * ,"COMMISSION_PERCENT",homeData.getSourceType() ); commissionPercent =
//			 * StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//			 * 
//			 * } else if(policylist.size()>0 && policylist!=null) {
//			 * if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) &&
//			 * "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) { commissionPercent
//			 * = homeData.getCommissionPercentage()==null ? 0D :
//			 * Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ; } else {
//			 * commissionPercent = policylist.get(0).getCommissionPercentage().toString() ==
//			 * null ? 0 :
//			 * Double.valueOf(policylist.get(0).getCommissionPercentage().toString()); }
//			 * 
//			 * } else { commissionPercent=0D; }
//			 * 
//			 * String premiumFc = v.getPremiumFc().toString(); String vatPremiumFc =
//			 * v.getVatPremiumFc()==null ?"0" : v.getVatPremiumFc().toPlainString();
//			 * 
//			 * 
//			 * if(StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId()) ) {
//			 * EndtUpdatePremiumRes endtRes = mainTableEndtPremium(v.getQuoteNo() ,
//			 * product.getProductId().toString() , product.getMotorYn() ); premiumFc =
//			 * endtRes.getEndtPremium()==null ? "0" :
//			 * String.valueOf(endtRes.getEndtPremium().toPlainString()); vatPremiumFc =
//			 * endtRes.getEndtVatPremium()==null ? "0" :
//			 * String.valueOf(endtRes.getEndtVatPremium().toPlainString()) ;
//			 * 
//			 * }
//			 * 
//			 * BigDecimal commission = new BigDecimal(premiumFc).multiply(new
//			 * BigDecimal(commissionPercent)) .divide(BigDecimal.valueOf(100D))
//			 * .setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(),
//			 * RoundingMode.HALF_UP); //totalcommission = totalcommission.add(commission);
//			 * 
//			 * 
//			 * List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//			 * 
//			 * // Setup Map<String, Object> setup = new HashMap<String, Object>();
//			 * 
//			 * List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>(); {
//			 * Map<String, Object> subset = new HashMap<String, Object>(); String chargeCode
//			 * = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//			 * 
//			 * List<ChartOfAccount> filterChargeCode = getChartList.stream().filter( o ->
//			 * o.getChartAccountCode().equals(Integer.valueOf(chargeCode))
//			 * ).collect(Collectors.toList()); ChartOfAccount filteredCharge =
//			 * filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ;
//			 * 
//			 * subset.put("CHARGE_CODE", chargeCode); subset.put("CHARGE_CODE_DESC",
//			 * filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :
//			 * "Premium"); subset.put("CHARGE_CODE_VALUE", premiumFc);
//			 * subset.put("DISPLAY_ORDER", filteredCharge!=null ?
//			 * filteredCharge.getDisplayOrder() : "1"); csubsets.add(subset); } {
//			 * Map<String, Object> subset = new HashMap<String, Object>();
//			 * List<ChartOfAccount> filterChargeCode = getChartList.stream().filter( o ->
//			 * o.getChartAccountCode().equals(1009) ).collect(Collectors.toList());
//			 * ChartOfAccount filteredCharge = filterChargeCode.size() > 0 ?
//			 * filterChargeCode.get(0):null ; subset.put("CHARGE_CODE", "1009");
//			 * subset.put("CHARGE_CODE_DESC", filteredCharge!=null ?
//			 * filteredCharge.getChartAccountDesc() : "VAT"); subset.put("NARATION",
//			 * filteredCharge!=null ? filteredCharge.getNaration()+ " " +
//			 * Double.valueOf(homeData.getVatPercent()==null?"0":homeData.getVatPercent().
//			 * toPlainString()) +"%" : "VAT"); subset.put("CHARGE_CODE_VALUE",
//			 * vatPremiumFc); subset.put("DISPLAY_ORDER", filteredCharge!=null ?
//			 * filteredCharge.getDisplayOrder() : "2"); csubsets.add(subset); } String
//			 * crnumber =""; if(commissionPercent.doubleValue()>0D) {
//			 * 
//			 * List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>(); {
//			 * Map<String, Object> subset = new HashMap<String, Object>(); String chargeCode
//			 * = Double.valueOf(premiumFc)<0 ? "1006" : "1005" ; List<ChartOfAccount>
//			 * filterChargeCode = getChartList.stream().filter( o ->
//			 * o.getChartAccountCode().equals(Integer.valueOf(chargeCode))
//			 * ).collect(Collectors.toList()); ChartOfAccount filteredCharge =
//			 * filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ;
//			 * subset.put("CHARGE_CODE", chargeCode); subset.put("CHARGE_CODE_DESC",
//			 * filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Commission");
//			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :
//			 * "Commission"); subset.put("CHARGE_CODE_VALUE", commission);
//			 * subset.put("DISPLAY_ORDER", filteredCharge!=null ?
//			 * filteredCharge.getDisplayOrder() : "3"); bsubsets.add(subset); }
//			 * 
//			 * { Map<String, Object> subset = new HashMap<String, Object>(); String
//			 * chargeCode = "1007" ; List<ChartOfAccount> filterChargeCode =
//			 * getChartList.stream().filter( o ->
//			 * o.getChartAccountCode().equals(Integer.valueOf(chargeCode))
//			 * ).collect(Collectors.toList()); ChartOfAccount filteredCharge =
//			 * filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ;
//			 * subset.put("CHARGE_CODE", chargeCode); subset.put("CHARGE_CODE_DESC",
//			 * filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :
//			 * "Commission%"); subset.put("CHARGE_CODE_VALUE", commissionPercent);
//			 * subset.put("DISPLAY_ORDER", filteredCharge!=null ?
//			 * filteredCharge.getDisplayOrder() : "4"); bsubsets.add(subset); } {// Broker
//			 * Commmission Vat String brokerLoginId = policylist.size() > 0 ?
//			 * policylist.get(0).getLoginId() : ""; LoginUserInfo loginuser =
//			 * loginUserRepo.findByLoginId(brokerLoginId); String brokerVatYn = loginuser
//			 * !=null && loginuser.getTaxExemptedYn()!=null &&
//			 * loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//			 * if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) { String
//			 * brokerVatPercent = homeData.getVatPercent()==null ? "0" :
//			 * homeData.getVatPercent().toPlainString(); BigDecimal brokerVatAmount =
//			 * commission.multiply(new BigDecimal(brokerVatPercent))
//			 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(0,
//			 * RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//			 * 
//			 * Map<String, Object> subset = new HashMap<String, Object>();
//			 * List<ChartOfAccount> filterChargeCode = getChartList.stream().filter( o ->
//			 * o.getChartAccountCode().equals(1009) ).collect(Collectors.toList());
//			 * ChartOfAccount filteredCharge = filterChargeCode.size() > 0 ?
//			 * filterChargeCode.get(0):null ; subset.put("CHARGE_CODE", "1009");
//			 * subset.put("CHARGE_CODE_DESC", filteredCharge!=null ?
//			 * filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//			 * subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+
//			 * " " + Double.valueOf(brokerVatPercent) +"%" : "Vat");
//			 * subset.put("CHARGE_CODE_VALUE", brokerVatAmount); subset.put("DISPLAY_ORDER",
//			 * filteredCharge!=null ? filteredCharge.getDisplayOrder() : "5");
//			 * bsubsets.add(subset); } } setup.put("<BROKER>", bsubsets); crnumber =
//			 * genNo.generateCreditNo(branchCode.get(0).getCoreAppCode()); } /*
//			 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//			 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//			 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//			 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//			 * 
//			 * 
//			 * Map<String,Object> subset=new HashMap<String, Object>();
//			 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//			 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//			 * bsubsets.add(subset); }
//			 * 
//			 */
//			/*
//			 * setup.put("<CUSTOMER>", csubsets);
//			 * 
//			 * 
//			 * // Rule Map<String, Object> rule1 = new HashMap<String, Object>();
//			 * 
//			 * if( v.getEndtPremium()!=null && v.getEndtPremium().compareTo(new
//			 * BigDecimal(0))<=0 ){ rule1.put("DEBIT", "<BROKER>"); rule1.put("CREDIT",
//			 * "<CUSTOMER>"); }else { rule1.put("DEBIT", "<CUSTOMER>"); rule1.put("CREDIT",
//			 * "<BROKER>"); } rules.add(rule1);
//			 * 
//			 * // ThreadLocalRandom.current().ints(1001, //
//			 * 4999).distinct().limit(5).findAny().toString(); String drnumber =
//			 * genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); //
//			 * ThreadLocalRandom.current().ints(4999, //
//			 * 9999).distinct().limit(5).findAny().toString();
//			 * 
//			 * 
//			 * int rownum = 1;
//			 * 
//			 * for (Map<String, Object> map : rules) { for (Entry<String, Object> m :
//			 * map.entrySet()) { List<Map<String, Object>> dd = (List<Map<String, Object>>)
//			 * setup.get(m.getValue()); if(dd!=null){ for (Map<String, Object> s : dd) {
//			 * DebitAndCredit res =new DebitAndCredit(); String doctype =
//			 * m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//			 * 
//			 * res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//			 * if(s.get("CHARGE_CODE").toString().equalsIgnoreCase("1007") ) {
//			 * res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString())); }
//			 * else { String pattern = "#####0" ; DecimalFormat df = new
//			 * DecimalFormat(pattern); res.setAmountLc( new
//			 * BigDecimal(df.format(res.getAmountFc().multiply(v.getExchangeRate()))) );
//			 * 
//			 * }
//			 * 
//			 * res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//			 * res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//			 * res.setNarration(s.get("NARATION").toString());
//			 * res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//			 * res.setBranchCode(request.getBranchCode()); res.setChgId(new
//			 * BigDecimal(rownum++)); res.setCompanyId(request.getInsuranceId());
//			 * res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId() :
//			 * v1.getQuoteDetails().getLoginId()); res.setDocNo(m.getKey().equals("DEBIT") ?
//			 * drnumber : crnumber); res.setDocType(doctype);
//			 * res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//			 * res.setEntryDate(new Date()); res.setPolicyNo(request.getPolicyNo());
//			 * res.setProductId(request.getProductId());
//			 * res.setQuoteNo(request.getQuoteno()); res.setStatus("Y");
//			 * res.setQuoteInfo(v1); resList.add(res); } } } }
//			 * crdrservice.insertDRCR(resList, request.getQuoteno());
//			 */
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		return resList;
//	}
//
//	public String getPolicyNo(CalcCommission request) {
//		String policyNo = "";
//		try {
//			ViewQuoteReq q = new ViewQuoteReq();
//			q.setQuoteNo(request.getQuoteno());
//			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
//			CompanyProductMaster product = getCompanyProductMasterDropdown(v1.getQuoteDetails().getCompanyId(),
//					v1.getQuoteDetails().getProductId().toString());
//			String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//			String emiYn = StringUtils.isBlank(v1.getQuoteDetails().getEmiYn()) ? "N" : v1.getQuoteDetails().getEmiYn();
////			String instalment=v1.getQuoteDetails().getInstallmentMonth();
//			String instalment = "";
//			String noOFIns = "";
//			List<BranchMaster> branchCode = ratingutil.collectBranchMaster(v1.getQuoteDetails().getCompanyId(),
//					v1.getQuoteDetails().getBranchCode());
//			HomePositionMaster hpm = homeRepo.findByQuoteNo(request.getQuoteno());
//			if (emiYn.equalsIgnoreCase("Y") && StringUtils.isBlank(endttypeid)) {
//				List<EmiTransactionDetails> emiDetails = emiRepo
//						.findByQuoteNoOrderByInstalmentAsc(request.getQuoteno());
//				if (emiDetails != null ) {
//					noOFIns = emiDetails.get(0).getInstalment();
//				}
//				instalment = hpm.getNoOfInstallment();
//			}
//			System.out.println(request.getQuoteno() + "EmiYN :" + emiYn + "\n NoOFIns from EmiTransactionDEtails :"
//					+ noOFIns + " \n Installment from HMP :" + instalment);
//			// Not endt
//			if (StringUtils.isBlank(endttypeid)
//					&& (emiYn.equalsIgnoreCase("N") || instalment.equalsIgnoreCase(noOFIns))) {
//				List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
//				List<ProductSectionMaster> coreappcode = ratingutil.collectSectionMaster(
//						v1.getQuoteDetails().getCompanyId(), v1.getQuoteDetails().getProductId().toString(),
//						sections.get(0).getSectionId());
//
//				List<MotorDataDetails> list = motorRepo.findByQuoteNo(request.getQuoteno());
//
//				PersonalInfo pi = piRepo.findByCustomerId(hpm.getCustomerId());
//				String vehUsageCoreappcode = "";
//
////		 	if(request.getProductId().equalsIgnoreCase("5"))		 	
////		 		vehUsageCoreappcode = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "MADISON_MOTOR", list.get(0).getMotorUsage(), pi.getPolicyHolderType());	 	
////		 	
////		 	  if(request.getInsuranceId().equalsIgnoreCase("100004")) {
////		 		  
////		 		 String itemvalue = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "POLICY_NO");
////		 		  
////		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode(), request.getInsuranceId(), vehUsageCoreappcode, request.getProductId(), itemvalue);
////		 		 
////		 	  }else {
////		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
////		 	  }
//				// Generate Policy Seq
//				SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
//				generateSeqReq.setInsuranceId(hpm.getCompanyId());
//				generateSeqReq.setProductId(hpm.getProductId().toString());
//				generateSeqReq.setType("5");
//				generateSeqReq.setTypeDesc("POLICY_NO");
//				List<String> params = new ArrayList<String>();
//				params.add(hpm.getQuoteNo());
//				generateSeqReq.setParams(params);
//				policyNo = genNo.generateSeqCall(generateSeqReq);
//				policyNo = policyNo.replaceAll(" ", "");
//				request.setPolicyNo(policyNo);
//			} else { // endt
//
//				request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//				policyNo = v1.getQuoteDetails().getPolicyNo();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return policyNo;
//	}
//
//	private EndtUpdatePremiumRes updateEndtPremium2(String quoteNo, Date effDate, String prevQuoteNo, Integer riskId,
//			List<PolicyCoverData> covers, Integer productId, Integer sectionId, String endtType) {
//		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();
//		try {
//			List<PolicyCoverData> newCovers = null;
//			List<PolicyCoverData> totalcovers = null;
//			List<PolicyCoverData> oldcovers = null;
//
//			if (riskId.intValue() == 0) {
//				newCovers = covers;
//				totalcovers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
//				oldcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdAndStatusNotOrderByVehicleIdAsc(prevQuoteNo, 0,
//						0, "D");
//			} else {
//				newCovers = covers.stream().filter(i -> i.getVehicleId().doubleValue() == riskId.doubleValue())
//						.collect(Collectors.toList());
//				totalcovers = coverRepo.findByQuoteNoAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(quoteNo,
//						riskId, productId, sectionId);
//				oldcovers = coverRepo
//						.findByQuoteNoAndVehicleIdAndDiscLoadIdAndTaxIdAndStatusNotAndProductIdAndSectionIdOrderByVehicleIdAsc(
//								prevQuoteNo, riskId, 0, 0, "D", productId, sectionId);
//			}
//
//			List<PolicyCoverData> oldcoversf = oldcovers;
//
//			// Premium With Tax
////			Double removedCoverPremium =  (totalcovers.stream().filter( o ->   o.getPremiumIncludedTaxFc()!=null 
////					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType())  ) .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum());			 
////			 
////			Double endtChangePremium=totalcovers.stream().filter( o ->  o.getPremiumIncludedTaxFc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())  )
////			 .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
////			 
////			 newCovers.removeIf(p-> {
////				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
////			 });
////			 Double addedCoverPremium =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
////					 o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null 
////					 && !"D".equals(o.getStatus())
////					 && effDate.compareTo(o.getCoverPeriodFrom())>=0  ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
////				BigDecimal endtPremium= new  BigDecimal(removedCoverPremium+addedCoverPremium+endtChangePremium);
////				
//
//			// Premium Without Tax
//			Double removedCoverPremiumWithoutTax = (totalcovers.stream()
//					.filter(o -> o.getPremiumExcludedTaxFc() != null && "D".equals(o.getStatus())
//							&& "E".equals(o.getCoverageType()))
//					.mapToDouble(o -> o.getPremiumExcludedTaxFc().doubleValue()).sum());
//
//			Double endtChangePremiumWithoutTax = totalcovers.stream()
//					.filter(o -> o.getPremiumExcludedTaxFc() != null && "E".equals(o.getCoverageType())
//							&& !"D".equals(o.getStatus()))
//					.mapToDouble(o -> o.getPremiumExcludedTaxFc().doubleValue()).sum();
//
//			newCovers.removeIf(p -> {
//				return oldcoversf.stream()
//						.anyMatch(x -> (x.getVehicleId() == p.getVehicleId() && x.getSectionId() == p.getSectionId()
//								&& x.getProductId() == p.getProductId() && x.getCoverId() == p.getCoverId()));
//			});
//			Double addedCoverPremiumWithoutTax = newCovers.stream()
//					.filter(o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)
//							&& o.getPremiumExcludedTaxFc() != null && !"D".equals(o.getStatus())
//							&& effDate.compareTo(o.getCoverPeriodFrom()) >= 0)
//					.mapToDouble(o -> o.getPremiumIncludedTaxFc().doubleValue()).sum();
//			BigDecimal endtPremiumWithoutTax = new BigDecimal(
//					removedCoverPremiumWithoutTax + addedCoverPremiumWithoutTax + endtChangePremiumWithoutTax);
//
//			// Tax Amount
//			List<PolicyCoverData> endtTaxCovers = totalcovers.stream()
//					.filter(o -> o.getCoverageType().equalsIgnoreCase("T")
//							&& o.getDiscLoadId().equals(Integer.valueOf(endtType)))
//					.collect(Collectors.toList());
//			Double endtVatPremium = endtTaxCovers.stream()
//					.filter(o -> !o.getDiscLoadId().equals(0) && !o.getTaxId().equals(0) && o.getTaxAmount() != null
//							&& o.getCoverageType().equalsIgnoreCase("T"))
//					.mapToDouble(o -> o.getTaxAmount().doubleValue()).sum();
//
//			String endtChargeOrRefund = "REFUND";
//			if (endtPremiumWithoutTax.doubleValue() >= 0) {
//				endtChargeOrRefund = "CHARGE";
//			} else if (endtPremiumWithoutTax.doubleValue() < 0 && endtVatPremium >= 0) {
//				endtVatPremium = -endtVatPremium;
//			}
//
//			endtRes.setChargeOrRefund(endtChargeOrRefund);
//			endtRes.setEndtPremium(endtPremiumWithoutTax);
//			endtRes.setEndtVatPremium(new BigDecimal(endtVatPremium));
//
//			return endtRes;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return endtRes;
//	}
//
//	private EndtUpdatePremiumRes mainTableEndtPremium(String quoteNo, String productId, String productType) {
//		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();
//		try {
//			Double endtPremiumWithoutTax = 0D;
//			Double endtVatPremium = 0D;
//			if (productType.equalsIgnoreCase("M")) {
//				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
//				for (MotorDataDetails mot : motors) {
//					endtPremiumWithoutTax = endtPremiumWithoutTax
//							+ (mot.getEndtPremium() == null ? 0D : mot.getEndtPremium().doubleValue());
//					endtVatPremium = endtVatPremium
//							+ (mot.getEndtVatPremium() == null ? 0D : mot.getEndtVatPremium().doubleValue());
//
//				}
//
//				// Travel Product
//			} else if (productType.equalsIgnoreCase("H") && productId.equalsIgnoreCase(travelProductId)) {
//				// List<EserviceTravelGetRes> motors = (List<EserviceTravelGetRes>)
//				// v1.getRiskDetails();
//				// EserviceTravelDetails tra =
//				// eserTraRepo.findByRequestReferenceNo(request.getQuoteNo() );
//				HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo);
//				endtPremiumWithoutTax = endtPremiumWithoutTax
//						+ (homeData.getEndtPremium() == null ? 0D : homeData.getEndtPremium().doubleValue());
//				endtVatPremium = endtVatPremium
//						+ (homeData.getEndtPremiumTax() == null ? 0D : homeData.getEndtPremiumTax().doubleValue());
//
//			} else if (productType.equalsIgnoreCase("A")) {
//				List<BuildingRiskDetails> BuildingRisk = buildingRepo
//						.findByQuoteNoAndSectionIdNotOrderByRiskIdAsc(quoteNo, "0");
//
//				// Asset
//				for (BuildingRiskDetails build : BuildingRisk) {
//					endtPremiumWithoutTax = endtPremiumWithoutTax
//							+ (build.getEndtPremium() == null ? 0D : build.getEndtPremium().doubleValue());
//					endtVatPremium = endtVatPremium
//							+ (build.getEndtVatPremium() == null ? 0D : build.getEndtVatPremium().doubleValue());
//
//				}
//
//				// Human Included
//				List<CommonDataDetails> humans = commonRepo.findByQuoteNo(quoteNo);
//
//				for (CommonDataDetails hum : humans) {
//					endtPremiumWithoutTax = endtPremiumWithoutTax
//							+ (hum.getEndtPremium() == null ? 0D : hum.getEndtPremium().doubleValue());
//					endtVatPremium = endtVatPremium
//							+ (hum.getEndtVatPremium() == null ? 0D : hum.getEndtVatPremium().doubleValue());
//				}
//
//				// Human Products
//			} else {
//				List<CommonDataDetails> humans = commonRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
//
//				for (CommonDataDetails hum : humans) {
//					endtPremiumWithoutTax = endtPremiumWithoutTax
//							+ (hum.getEndtPremium() == null ? 0D : hum.getEndtPremium().doubleValue());
//					endtVatPremium = endtVatPremium
//							+ (hum.getEndtVatPremium() == null ? 0D : hum.getEndtVatPremium().doubleValue());
//				}
//
//			}
//
//			String endtChargeOrRefund = "REFUND";
//			if (endtPremiumWithoutTax.doubleValue() >= 0) {
//				endtChargeOrRefund = "CHARGE";
//			}
//
//			endtRes.setChargeOrRefund(endtChargeOrRefund);
//			endtRes.setEndtPremium(new BigDecimal(endtPremiumWithoutTax));
//			endtRes.setEndtVatPremium(new BigDecimal(endtVatPremium));
//
//			return endtRes;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return endtRes;
//	}
//
//	public  String getListItemvalue(String insuranceId, String branchCode, String itemType) {
//		String itemvalue = "";
//		List<ListItemValue> list = new ArrayList<ListItemValue>();
//		try {
//			Date today = new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 1);
//			cal.set(Calendar.MINUTE, 1);
//			Date todayEnd = cal.getTime();
//
//			// Criteria
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
//			// Find All
//			Root<ListItemValue> c = query.from(ListItemValue.class);
//
//			// Select
//			query.select(c);
//
//			// Effective Date Start Max Filter
//			Subquery<Date> effectiveDate = query.subquery(Date.class);
//			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
//			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
//			Predicate b3 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
//			Predicate b4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
//			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//			effectiveDate.where(a1, a2, b3, b4);
//			// Effective Date End Max Filter
//			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
//			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
//			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
//			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
//			Predicate b1 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
//			Predicate b2 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
//			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//			effectiveDate2.where(a3, a4, b1, b2);
//
//			// Where
//			Predicate n1 = cb.equal(c.get("status"), "Y");
//			Predicate n12 = cb.equal(c.get("status"), "R");
//			Predicate n13 = cb.or(n1, n12);
//			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
//			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
//			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
//			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
//			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
//			Predicate n9 = cb.or(n6, n7);
//			Predicate n10 = cb.equal(c.get("itemType"), itemType);
//
//			query.where(n13, n2, n3, n4, n9, n10);
//
//			// Get Result
//			TypedQuery<ListItemValue> result = em.createQuery(query);
//			list = result.getResultList();
//
//			itemvalue = list.size() > 0 ? list.get(0).getItemValue() : "";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		return itemvalue;
//
//	}
//
//	public  String getListItemvalue(String insuranceId, String branchCode, String itemType,
//			String vehUsageId, String cusTypeId) {
//		String coreappcode = "";
//		List<ListItemValue> list = new ArrayList<ListItemValue>();
//		try {
//			Date today = new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 1);
//			cal.set(Calendar.MINUTE, 1);
//			Date todayEnd = cal.getTime();
//
//			// Criteria
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
//			// Find All
//			Root<ListItemValue> c = query.from(ListItemValue.class);
//
//			// Select
//			query.select(c);
//
//			// Effective Date Start Max Filter
//			Subquery<Date> effectiveDate = query.subquery(Date.class);
//			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
//			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
//			Predicate b3 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
//			Predicate b4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
//			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//			effectiveDate.where(a1, a2, b3, b4);
//			// Effective Date End Max Filter
//			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
//			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
//			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
//			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
//			Predicate b1 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
//			Predicate b2 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
//			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//			effectiveDate2.where(a3, a4, b1, b2);
//
//			// Where
//			Predicate n1 = cb.equal(c.get("status"), "Y");
//			Predicate n12 = cb.equal(c.get("status"), "R");
//			Predicate n13 = cb.or(n1, n12);
//			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
//			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
//			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
//			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
//			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
//			Predicate n9 = cb.or(n6, n7);
//			Predicate n10 = cb.equal(c.get("itemType"), itemType);
//			Predicate n11 = cb.equal(c.get("itemCode"), vehUsageId); // Veh USAGE Id (private, commercial, special)
//			Predicate n14 = cb.equal(c.get("param1"), cusTypeId); // Customer TYPE Id (corporate/Indidual)
//
//			query.where(n13, n2, n3, n4, n9, n10, n11, n14);
//
//			// Get Result
//			TypedQuery<ListItemValue> result = em.createQuery(query);
//			list = result.getResultList();
//
//			coreappcode = list.size() > 0 ? list.get(0).getCoreAppCode() : "";
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		return coreappcode;
//
//	}
//
//	
//	public List<AdminReferral> getReferalList(ReferralApi request) {
//		try {
//			String todayInString = DD_MM_YYYY.format(new Date());
//			List<SpecCriteria> criterias;
//			List<String> columns = new ArrayList<String>();
//			columns.add("loginId");
//			columns.add("userName");
//			columns.add("userMobile");
//			columns.add("mobileCodeDesc");
//			columns.add("companyName");
//			columns.add("userMail");
//			columns.add("userName");
//			columns.add("whatsappCodeDesc");
//			columns.add("whatsappNo");
//			columns.add("userType");
//			columns.add("subUserType");
//
//			String s1 = "userType:Issuer;subUserType:{high,both};companyId:" + request.getInsuranceId()
//					+ ";attachedBranches%" + request.getBranchCode() + ";status:Y;";
//			SpecCriteria c1 = crservice.createCriteria(LoginMaster.class, s1, "loginId", columns);
//			JoinCriteria j1 = new JoinCriteria();
//			j1.setColumnName("loginId");
//			j1.setToColumnName("loginId");
//			j1.setToTableName(LoginProductMaster.class);
//			List<JoinCriteria> j1s = new ArrayList<JoinCriteria>();
//			j1s.add(j1);
//			c1.setJoins(j1s);
//
//			String s2 = "productId:" + request.getProductId() + ";status:Y;" + todayInString
//					+ "~effectiveDateStart&effectiveDateEnd;" + request.getSuminsured()
//					+ "~sumInsuredStart&sumInsuredEnd";
//			SpecCriteria c2 = crservice.createCriteria(LoginProductMaster.class, s2, "loginId", columns);
//
//			JoinCriteria j2 = new JoinCriteria();
//			j2.setColumnName("loginId");
//			j2.setToColumnName("loginId");
//			j2.setToTableName(LoginMaster.class);
//
//			JoinCriteria j2_1 = new JoinCriteria();
//			j2_1.setColumnName("loginId");
//			j2_1.setToColumnName("loginId");
//			j2_1.setToTableName(LoginUserInfo.class);
//
//			List<JoinCriteria> j2s = new ArrayList<JoinCriteria>();
//			j2s.add(j2);
//			j2s.add(j2_1);
//			c2.setJoins(j2s);
//
//			String s3 = "status:Y;";
//			SpecCriteria c3 = crservice.createCriteria(LoginUserInfo.class, s3, "loginId", columns);
//
//			JoinCriteria j3 = new JoinCriteria();
//			j3.setColumnName("loginId");
//			j3.setToColumnName("loginId");
//			j3.setToTableName(LoginMaster.class);
//			List<JoinCriteria> j3s = new ArrayList<JoinCriteria>();
//			j3s.add(j3);
//			c3.setJoins(j3s);
//
//			criterias = new ArrayList<SpecCriteria>();
//			criterias.add(c1);
//			criterias.add(c2);
//			criterias.add(c3);
//
//			List<Tuple> joinResult = crservice.getJoinResult(criterias, 0, 0);
//			List<AdminReferral> list = new ArrayList<AdminReferral>();
//			for (Tuple tuple : joinResult) {
//
//				AdminReferral a = AdminReferral.builder().insuranceId((String) tuple.get("companyName"))
//						.loginId((String) tuple.get("loginId")).mailId((String) tuple.get("userMail"))
//						.mobileCode((String) tuple.get("mobileCodeDesc")).mobileNo((String) tuple.get("userMobile"))
//						.userName((String) tuple.get("userName")).whatsappcode((String) tuple.get("whatsappCodeDesc"))
//						.whatsAppNo((String) tuple.get("whatsappNo")).uwuserType((String) tuple.get("userType"))
//						.uwsubuserType((String) tuple.get("subUserType")).build();
//				list.add(a);
//			}
//			return list;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public  CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
//		CompanyProductMaster product = new CompanyProductMaster();
//		try {
//			Date today = new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			;
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.set(Calendar.HOUR_OF_DAY, 1);
//			cal.set(Calendar.MINUTE, 1);
//			Date todayEnd = cal.getTime();
//
//			// Criteria
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
//			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
//			// Find All
//			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
//			// Select
//			query.select(c);
//			// Order By
//			List<Order> orderList = new ArrayList<Order>();
//			orderList.add(cb.asc(c.get("productName")));
//
//			// Effective Date Start Max Filter
//			Subquery<Date> effectiveDate = query.subquery(Date.class);
//			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
//			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
//			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
//			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//			effectiveDate.where(a1, a2, a3);
//			// Effective Date End Max Filter
//			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
//			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
//			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
//			Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
//			Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
//			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//			effectiveDate2.where(a4, a5, a6);
//
//			// Where
//			Predicate n1 = cb.equal(c.get("status"), "Y");
//			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
//			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
//			Predicate n4 = cb.equal(c.get("companyId"), companyId);
//			Predicate n5 = cb.equal(c.get("productId"), productId);
//			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
//			// Get Result
//			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
//			list = result.getResultList();
//			product = list.size() > 0 ? list.get(0) : null;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//		return product;
//	}
//
//	public  String getListItem(String insuranceId, String branchCode, String itemType, String itemCode) {
//		String itemDesc = "";
//		List<ListItemValue> list = new ArrayList<ListItemValue>();
//		try {
//			Date today = new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
//			today = cal.getTime();
//			Date todayEnd = cal.getTime();
//
//			// Criteria
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
//			// Find All
//			Root<ListItemValue> c = query.from(ListItemValue.class);
//
//			// Select
//			query.select(c);
//			// Order By
//			List<Order> orderList = new ArrayList<Order>();
//			orderList.add(cb.asc(c.get("branchCode")));
//
//			// Effective Date Start Max Filter
//			Subquery<Date> effectiveDate = query.subquery(Date.class);
//			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
//			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
//			Predicate b3 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
//			Predicate b4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
//			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//			effectiveDate.where(a1, a2, b3, b4);
//			// Effective Date End Max Filter
//			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
//			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
//			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
//			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
//			Predicate b1 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
//			Predicate b2 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
//			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//			effectiveDate2.where(a3, a4, b1, b2);
//
//			// Where
//			Predicate n1 = cb.equal(c.get("status"), "Y");
//			Predicate n12 = cb.equal(c.get("status"), "R");
//			Predicate n13 = cb.or(n1, n12);
//			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
//			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
//			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
//			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
//			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
//			Predicate n9 = cb.or(n6, n7);
//			Predicate n10 = cb.equal(c.get("itemType"), itemType);
//			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
//
//			query.where(n13, n2, n3, n4, n9, n10, n11).orderBy(orderList);
//
//			// Get Result
//			TypedQuery<ListItemValue> result = em.createQuery(query);
//			list = result.getResultList();
//
//			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
//		} catch (Exception e) {
//			e.printStackTrace();
//			// log.info("Exception is ---> " + e.getMessage());
//			return null;
//		}
//		return itemDesc;
//	}
//
//	public List<ChartOfAccount> getChartList(String companyId) {
//		List<ChartOfAccount> list = new ArrayList<ChartOfAccount>();
//		try {
//			Date today = new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
////			cal.set(Calendar.HOUR_OF_DAY, 1);;
////			cal.set(Calendar.MINUTE, 1);
////			today = cal.getTime();
////			cal.set(Calendar.HOUR_OF_DAY, 23);
////			cal.set(Calendar.MINUTE, 59);
////			Date todayEnd = cal.getTime();
//			today = cal.getTime();
//			Date todayEnd = cal.getTime();
//
//			// Criteria
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<ChartOfAccount> query = cb.createQuery(ChartOfAccount.class);
//
//			// Find All
//			Root<ChartOfAccount> c = query.from(ChartOfAccount.class);
//			// Select
//			query.select(c);
//
//			// Effective Date Start Max Filter
//			Subquery<Date> effectiveDate = query.subquery(Date.class);
//			Root<ChartOfAccount> ocpm1 = effectiveDate.from(ChartOfAccount.class);
//			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//			Predicate a1 = cb.equal(c.get("chartAccountCode"), ocpm1.get("chartAccountCode"));
//			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
//			Predicate a4 = cb.equal(c.get("chartAccountCode"), ocpm1.get("chartAccountCode"));
//			effectiveDate.where(a1, a2, a3, a4);
//			// Effective Date End Max Filter
//			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
//			Root<ChartOfAccount> ocpm2 = effectiveDate2.from(ChartOfAccount.class);
//			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
//			Predicate a7 = cb.equal(c.get("chartAccountCode"), ocpm2.get("chartAccountCode"));
//			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//			Predicate a9 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
//			Predicate a10 = cb.equal(c.get("chartAccountCode"), ocpm2.get("chartAccountCode"));
//			effectiveDate2.where(a7, a8, a9, a10);
//
//			// Order By
//			List<Order> orderList = new ArrayList<Order>();
//			orderList.add(cb.asc(c.get("chartAccountCode")));
//
//			// Where
//			Predicate n1 = cb.equal(c.get("status"), "Y");
//			Predicate n8 = cb.equal(c.get("status"), "R");
//			Predicate n9 = cb.or(n1, n8);
//			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
//			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
//			Predicate n4 = cb.equal(c.get("companyId"), companyId);
//			query.where(n9, n2, n3, n4).orderBy(orderList);
//
//			// Get Result
//
//			TypedQuery<ChartOfAccount> result = em.createQuery(query);
//			list = result.getResultList();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			e.getMessage();
//		}
//		return list;
//	}
//	
//	public BigDecimal getOverallPremiumByRefNo(String requestRefNo) {
//		
//		BigDecimal premium = BigDecimal.ZERO;
//		String sqlQuery =
//                "select sum(fac.premiumExcludedTaxFc) " +
//                "  from FactorRateRequestDetails fac " +
//                " where fac.requestReferenceNo = :requestReferenceNo " +
//                "   and fac.coverageType in (:coverageType) " +
//                "   and (fac.isSelected = :isSelected or fac.userOpt = :userOpt) " +
//                "   and fac.vehicleId not in (:vehicleId)";
//
//        premium = (BigDecimal) em.createQuery(sqlQuery)
//                .setParameter("requestReferenceNo", requestRefNo)
//                .setParameter("coverageType", Arrays.asList("B", "O"))
//                .setParameter("isSelected", "D")
//                .setParameter("userOpt", "Y")
//                .setParameter("vehicleId", 99999)
//                .getSingleResult();
//
//	    return premium == null ? BigDecimal.ZERO : premium;
//	}
//
//
//	private List<Tuple> LoadCoverPolicy(CalcEngine engine) {
//
//		try {
//			String todayInString = DD_MM_YYYY.format(new Date());
//
//			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
//					+ ";sectionId:99999;status:{Y,R};" + todayInString + "~effectiveDateStart&effectiveDateEnd;"
//					+ "agencyCode:" + engine.getAgencyCode() + ";branchCode:99999;";
//
//			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
//					+ ";sectionId:99999;status:{Y,R};" + todayInString
//					+ "~effectiveDateStart&effectiveDateEnd;agencyCode:99999;branchCode:99999;";
//
//			SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
//			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);
//
//			SpecCriteria criteria = null;
//
//			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
//			List<Long> count = crservice.getCount(criteria, 0, 50);
//			if (!count.isEmpty()) {
//				Long countrec = count.get(0);
//				if (countrec > 0) {
//					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
//					for (Tuple t : specific) {
//						commonResult.removeIf(c -> c.get("coverId").toString().equals(t.get("coverId").toString()));
//						commonResult.add(t);
//					}
//				}
//
//			}
//
//			return commonResult;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public void loadOnetimetablePolicy(CalcEngine engine) {
//
//		try {
//			SpecCriteria criteria = null;
//			String search = "pdRefno:" + engine.getPdrefno() + ";";
//			criteria = crservice.createCriteria(MsPolicyDetails.class, search, "pdRefno");
//			policytbl = crservice.getResult(criteria, 0, 50);
//			String cdRefno = policytbl.get(0).get("cdRefno").toString();
//			search = "cdRefno:" + cdRefno + ";";
//			criteria = crservice.createCriteria(MsCustomerDetails.class, search, "cdRefno");
//			customers = crservice.getResult(criteria, 0, 50);
//
//			String todayInString = DD_MM_YYYY.format(new Date());
//
//			search = "companyId:" + engine.getInsuranceId() + ";status:Y;" + todayInString
//					+ "~effectiveDateStart&effectiveDateEnd;productId:" + engine.getProductId() + ";";
//			criteria = crservice.createCriteria(CompanyProductMaster.class, search, "productId");
//			List<Tuple> result = crservice.getResult(criteria, 0, 50);
//			if (result != null && result.size() > 0) {
//				minimumPremium = result.get(0).get("minimumPremium") == null ? BigDecimal.ZERO
//						: new BigDecimal(result.get(0).get("minimumPremium").toString());
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//
//	private Date getZeroTimeDate(Date date) {
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(date);
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);
//		date = calendar.getTime();
//		return date;
//	}
//	
//	private CalcEngine buildEngineFromBuilding(EserviceBuildingDetails bd, CalcEngine request) {
//
//	    CalcEngine engine = new CalcEngine();
//
//	    engine.setLocationId(bd.getLocationId().toString());
//	    engine.setBranchCode(bd.getBranchCode());
//	    engine.setInsuranceId(bd.getCompanyId());
//	    engine.setSectionId(bd.getSectionId());
//	    engine.setProductId(bd.getProductId());
//	    engine.setMsrefno(bd.getMsRefno().toString());
//	    engine.setCdRefNo(bd.getCdRefno().toString());
//	    engine.setVdRefNo(bd.getVdRefno().toString());
//	    engine.setCreatedBy(bd.getCreatedBy());
//	    engine.setRequestReferenceNo(bd.getRequestReferenceNo());
//
//	    engine.setEffectiveDate(
//	            bd.getEndorsementEffdate() == null ? bd.getPolicyStartDate() : bd.getEndorsementEffdate());
//
//	    engine.setPolicyEndDate(
//	            request.getPolicyEndDate() == null ? bd.getPolicyEndDate() : request.getPolicyEndDate());
//
//	    engine.setCoverModification(
//	            StringUtils.isBlank(request.getCoverModification()) ? "N" : request.getCoverModification());
//
//	    engine.setVehicleId(bd.getRiskId().toString());
//	    engine.setCoverId(bd.getCoverId() == null ? "0" : bd.getCoverId().toString());
//	    engine.setAgencyCode(bd.getAgencyCode());
//
//	    return engine;
//	}
//	
//	public List<SectionCoverMaster> getSectionCoverDetails(String product, String section, String company) {
//		List<SectionCoverMaster> resList = new ArrayList<SectionCoverMaster>();
//		try {
//			Date today = new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 50);
//			today = cal.getTime();
//
//			// Find Latest Record
//			CriteriaBuilder cb2 = em.getCriteriaBuilder();
//			CriteriaQuery<SectionCoverMaster> query2 = cb2.createQuery(SectionCoverMaster.class);
//
//			// Find All
//			Root<SectionCoverMaster> b2 = query2.from(SectionCoverMaster.class);
//
//			// Amed Id
//
//			Subquery<Long> amendId2 = query2.subquery(Long.class);
//			Root<SectionCoverMaster> ocpm2 = amendId2.from(SectionCoverMaster.class);
//			amendId2.select(cb2.max(ocpm2.get("amendId")));
//			Predicate a7 = cb2.equal(ocpm2.get("coverId"), b2.get("coverId"));
//			Predicate a12 = cb2.equal(ocpm2.get("subCoverId"), b2.get("subCoverId"));
//			Predicate a8 = cb2.equal(ocpm2.get("sectionId"), b2.get("sectionId"));
//			Predicate a9 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
//			Predicate a10 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
//			Predicate a13 = cb2.equal(ocpm2.get("agencyCode"), b2.get("agencyCode"));
//			Predicate a14 = cb2.equal(ocpm2.get("branchCode"), b2.get("branchCode"));
//			amendId2.where(a7, a8, a9, a10, a12, a13, a14);
//			query2.select(b2);
//
//			// Order By
//			List<Order> orderList2 = new ArrayList<Order>();
//			orderList2.add(cb2.asc(b2.get("coverName")));
//
//			// Where
//			Predicate n4 = cb2.equal(b2.get("amendId"), amendId2);
//			Predicate n6 = cb2.equal(b2.get("subCoverId"), "0");
//			Predicate n7 = cb2.equal(b2.get("productId"), product);
//			Predicate n14 = cb2.equal(b2.get("companyId"), company);
//			Predicate n15 = cb2.equal(b2.get("sectionId"), section);
//			Predicate n9 = cb2.equal(b2.get("agencyCode"), "99999");
//			Predicate n12 = cb2.equal(b2.get("branchCode"), "99999");
//			Predicate n13 = cb2.equal(b2.get("status"), "Y");
//			Predicate n16 = cb2.equal(b2.get("dependentCoverYn"), "Y");
//			query2.where(n4, n6, n7, n14, n15, n9, n12, n13, n16).orderBy(orderList2);
//
//			// Get Result
//			TypedQuery<SectionCoverMaster> result2 = em.createQuery(query2);
//			resList = result2.getResultList();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//
//		}
//		return resList;
//	}
//
//	public String findNoOfDaysInDate(Date policyStartDate, Date policyEndDate) {
//		try {
//			Date periodStart = policyStartDate;
//			Date periodEnd = policyEndDate;
//			Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
//			Long daysBetween = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
//			// Check Leap Year
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			boolean leapYear = LocalDate.parse(sdf.format(periodEnd)).isLeapYear();
//			String diff = String.valueOf(daysBetween == 365 && leapYear == true ? daysBetween + 1 : daysBetween);
//			if (Integer.parseInt(diff) < 0)
//				diff = "0";
//			return diff;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	private EserviceMotorDetailsSaveRes handlePropertyMultiLocationCalculation(
//	        CalcEngine engine, String token) {
//
//	    LoginMaster loginMaster = loginMasterRepository.findByLoginId(engine.getCreatedBy());
//	    List<UWReferrals>   referrals       = null;
//	    List<MasterReferal> masterReferrals = null;
//
//	    if (!("issuer".equalsIgnoreCase(loginMaster.getUserType())
//	            && "superadmin".equalsIgnoreCase(loginMaster.getSubUserType()))) {
//	        referrals = referal.underwriterReferral(engine);
//	        try {
//	            masterReferrals = referal.masterreferral(engine, token);
//	        } catch (ClassNotFoundException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    try {
//	        
//	        loadPropertyOnetimetable(engine);
//	        validateData();
//
//	        List<Tuple> allAssetRows = new ArrayList<>(this.vehicles);
//
//	        if (allAssetRows.isEmpty()) {
//	            System.out.println("No asset rows for: " + engine.getRequestReferenceNo());
//	            return null;
//	        }
//
//	        CoverCalculator rateCalc = new CoverCalculator();
//	        rateCalc.setCriservice(crservice);
//	        rateCalc.setEngine(engine,
//	                new ArrayList<>(), commontbl, allAssetRows, 
//	                customers, prorata, ratingutil, decimalFormat, drivers, new ArrayList<>());
//
//	        double derivedRate = rateCalc.derivePropertyRate(engine);
//
//	        System.out.println("=== Single Shared Rate for ALL locations = " + derivedRate + " ===");
//
//	        List<Tuple> taxes               = ratingutil.LoadTax(engine, NORMAL_TAX_LIST);
//	        List<Tuple> customerChoiceTaxes = ratingutil.customerTaxList(engine);
//	        List<Tuple> excludedTaxes       = ratingutil.LoadExcludedTax(engine, NORMAL_TAX_LIST);
//
//	        engine.setSectionId("293");
//	        List<Tuple> totalCoversTuple = LoadCover(engine);
//
//	        String promocode = Optional.ofNullable(allAssetRows.get(0).get("promocode"))
//	                .map(Object::toString).orElse("");
//
//	        List<Cover> allCovers = Collections.synchronizedList(new ArrayList<>());
//	        List<EserviceMotorDetailsSaveRes> allResponses =
//	                Collections.synchronizedList(new ArrayList<>());
//
//	        final List<UWReferrals>   finalReferrals       = referrals;
//	        final List<MasterReferal> finalMasterReferrals = masterReferrals;
//	        final double              sharedRate           = derivedRate;
//
//	        allAssetRows.parallelStream().forEach(assetRow -> {
//	            try {
//	                String locationId = assetRow.get("locationId") == null
//	                        ? "1" : assetRow.get("locationId").toString();
//	                String coverId    = assetRow.get("coverId") == null
//	                        ? "0" : assetRow.get("coverId").toString();
//	                String vdRefNo    = assetRow.get("vdRefno") == null
//	                        ? "" : assetRow.get("vdRefno").toString();
//	                String vehicleId  = assetRow.get("riskId") == null
//	                        ? "" : assetRow.get("riskId").toString();
//	                String sumInsured = assetRow.get("sumInsured") == null
//	                        ? "0" : assetRow.get("sumInsured").toString();
//
//	                System.out.println("Processing → locationId=" + locationId
//	                        + " | coverId=" + coverId
//	                        + " | vdRefNo=" + vdRefNo
//	                        + " | SI=" + sumInsured
//	                        + " | rate=" + sharedRate);  
//
//	                String periodOfInsurance = Optional.ofNullable(assetRow.get("periodOfInsurance"))
//	                        .map(Object::toString).orElse("365");
//	                String policyTypeId = Optional.ofNullable(assetRow.get("insuranceClass"))
//	                        .map(Object::toString).orElse("99999");
//
//	                CalcEngine rowEngine = cloneEngine(engine);
//	                rowEngine.setLocationId(locationId);
//	                rowEngine.setVdRefNo(vdRefNo);       
//	                rowEngine.setVehicleId(vehicleId);
//	                rowEngine.setCoverId(coverId);
//	                rowEngine.setSectionId("293");
//
//	                List<Tuple> rowProrata = ratingutil.loadProRataData(
//	                        rowEngine, periodOfInsurance, policyTypeId);
//
//	                List<Tuple> singleRowVehicle = List.of(assetRow);
//
//	                List<Cover> rowCovers     = new ArrayList<>();
//	                TaxUtils   rowTaxUtils   = new TaxUtils(BigDecimal.ZERO, "");
//	                TaxRemover rowTaxRemover = new TaxRemover(excludedTaxes, null);
//
//	                List<Tuple> rowCoverTuple = totalCoversTuple.stream()
//	                        .filter(t -> coverId.equals(
//	                                t.get("coverId") == null
//	                                        ? "" : t.get("coverId").toString()))
//	                        .collect(Collectors.toList());
//
//	                List.of("N", "Y").forEach(depend ->
//	                        processCoverDependentsWithRate(
//	                                rowEngine,
//	                                singleRowVehicle,  
//	                                depend,
//	                                rowCoverTuple,
//	                                taxes,
//	                                customerChoiceTaxes,
//	                                promocode,
//	                                rowTaxUtils,
//	                                rowTaxRemover,
//	                                rowCovers,
//	                                sharedRate));      
//
//	                loadBenefitCovers(rowEngine, rowCovers);
//
//	                BigDecimal totalPremium = rowCovers.stream()
//	                        .filter(x -> !"N".equals(x.getIsselected())
//	                                && !"945".equals(x.getCoverId())
//	                                && x.getPremiumExcluedTaxLC() != null)
//	                        .map(Cover::getPremiumExcluedTaxLC)
//	                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//	                if (totalPremium.compareTo(minimumPremium) < 0) {
//	                    adjustMinimumPremium(rowEngine, BigDecimal.ZERO,
//	                            taxes, rowTaxUtils, rowCovers);
//	                } else {
//	                    rowCovers.removeIf(t -> "945".equals(t.getCoverId()));
//	                }
//
//	                EserviceMotorDetailsSaveRes rowResponse =
//	                        buildResponse(rowEngine, finalReferrals, finalMasterReferrals,
//	                                rowCovers, null);
//	                
//	                if ("Y".equalsIgnoreCase(engine.getIsReferral())) {
// 
//	                    rowCovers.forEach(cover -> {
//	                        cover.setIsReferral("Y");
//	                        cover.setReferalDescription("Occupation Referral");
//	                    });
//	                }
//
//	                fservice.saveFactorRateRequestDetails(rowResponse);
//
//	                allCovers.addAll(rowCovers);
//	                allResponses.add(rowResponse);
//
//	                System.out.println("Saved → locationId=" + locationId
//	                        + " | coverId=" + coverId
//	                        + " | SI=" + sumInsured
//	                        + " | rate=" + sharedRate
//	                        + " | premium=" + totalPremium);
//
//	            } catch (Exception e) {
//	                System.err.println("Error → locationId=" + assetRow.get("locationId")
//	                        + " coverId=" + assetRow.get("coverId") + " : " + e.getMessage());
//	                e.printStackTrace();
//	            }
//	        });
//
//	        this.vehicles = allAssetRows;
//
//	        if (!allResponses.isEmpty()) {
//	            EserviceMotorDetailsSaveRes finalResponse =
//	                    allResponses.get(allResponses.size() - 1);
//	            finalResponse.setCoverList(new ArrayList<>(allCovers));
//	            return finalResponse;
//	        }
//
//	        return null;
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return null;
//	    }
//	}
//
//
//	private CalcEngine cloneEngine(CalcEngine src) {
//	    CalcEngine clone = new CalcEngine();
//	    clone.setLocationId(src.getLocationId());
//	    clone.setBranchCode(src.getBranchCode());
//	    clone.setInsuranceId(src.getInsuranceId());
//	    clone.setSectionId(src.getSectionId());
//	    clone.setProductId(src.getProductId());
//	    clone.setMsrefno(src.getMsrefno());
//	    clone.setCdRefNo(src.getCdRefNo());
//	    clone.setVdRefNo(src.getVdRefNo());
//	    clone.setCreatedBy(src.getCreatedBy());
//	    clone.setRequestReferenceNo(src.getRequestReferenceNo());
//	    clone.setEffectiveDate(src.getEffectiveDate());
//	    clone.setPolicyEndDate(src.getPolicyEndDate());
//	    clone.setCoverModification(src.getCoverModification());
//	    clone.setVehicleId(src.getVehicleId());
//	    clone.setCoverId(src.getCoverId());
//	    clone.setAgencyCode(src.getAgencyCode());
//	    clone.setIsReferral(src.getIsReferral());
//	    clone.setReferralRemarks(src.getReferralRemarks());
//	    return clone;
//	}
//	
//	public void loadPropertyOnetimetable(CalcEngine engine) {
//	    try {
//	        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//	                + " <---- loadPropertyOnetimetable start ---->");
//
//	        SpecCriteria criteria = crservice.createCriteria(
//	                MsCommonDetails.class,
//	                "msRefno:" + engine.getMsrefno() + ";",
//	                "msRefno");
//	        commontbl = crservice.getResult(criteria, 0, 50);
//
//	        if (commontbl == null || commontbl.isEmpty()) {
//	            System.out.println("loadPropertyOnetimetable: commontbl empty");
//	            return;
//	        }
//
//	        String cdRefno = commontbl.get(0).get("cdRefno").toString();
//
//	        criteria = crservice.createCriteria(
//	                MsAssetDetails.class,
//	                "requestReferenceNo:" + engine.getRequestReferenceNo() + ";",
//	                "locationId");  
//
//	        List<Tuple> allRows = crservice.getResult(criteria, 0, 500);
//
//	        if (allRows == null || allRows.isEmpty()) {
//	            System.out.println("No assets found for: " + engine.getRequestReferenceNo());
//	            vehicles = new ArrayList<>();
//	            return;
//	        }
//
//	        vehicles = new ArrayList<>(allRows);
//
//	        System.out.println("loadPropertyOnetimetable: loaded "
//	                + vehicles.size() + " asset rows (location+cover combinations)");
//
//	        vehicles.forEach(v -> System.out.println(
//	                "  → locationId=" + v.get("locationId")
//	                + " coverId=" + v.get("coverId")
//	                + " vdRefNo=" + v.get("vdRefno")
//	                + " SI=" + v.get("sumInsured")));
//
//	        criteria = crservice.createCriteria(
//	                MsCustomerDetails.class,
//	                "cdRefno:" + cdRefno + ";",
//	                "cdRefno");
//	        customers = crservice.getResult(criteria, 0, 50);
//
//	        if (!vehicles.isEmpty()) {
//	            List<Tuple> product = ratingutil.collectProductType(engine);
//	            minimumPremium = product.get(0).get("minPremium") == null
//	                    ? BigDecimal.ZERO
//	                    : new BigDecimal(product.get(0).get("minPremium").toString());
//
//	            engine.setCoverId("0");
//
//	            String currencyId = vehicles.get(0).get("currency") == null
//	                    ? "TTT" : vehicles.get(0).get("currency").toString();
//
//	            String decimalDigits = ratingutil.currencyDecimalFormat(
//	                    engine.getInsuranceId(), currencyId);
//	            String stringFormat  = "%0" + decimalDigits + "d";
//	            String decimalLength = decimalDigits.equals("0")
//	                    ? "" : String.format(stringFormat, 0L);
//	            String pattern = StringUtils.isBlank(decimalLength)
//	                    ? "#####0" : "#####0." + decimalLength;
//	            decimalFormat = new DecimalFormat(pattern);
//	        }
//
//	        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
//	                + " <---- loadPropertyOnetimetable end ---->");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}
//	
//	
//	private void processCoverDependentsWithRate(CalcEngine engine,
//	        List<Tuple> currentVehicle,
//	        String dependCover, List<Tuple> totalCoversTuple,
//	        List<Tuple> taxes, List<Tuple> customerChoiceTaxes,
//	        String promocode, TaxUtils taxUtils, TaxRemover taxRemover,
//	        List<Cover> resultCovers, double derivedRate) {
//
//	    List<Tuple> covers = totalCoversTuple.parallelStream()
//	            .filter(t -> dependCover.equals(t.get("dependentCoverYn").toString()))
//	            .toList();
//
//	    if (covers.isEmpty()) return;
//
//	    List<Discount> discounts = generateDiscounts(engine, covers, promocode);
//	    List<Loading> loadings   = generateLoadings(engine, covers);
//
//	    Map<String, List<Cover>> nonSubCovers = splitCovers(engine, covers, "N");
//	    List<Cover> nonSubCoverList = nonSubCovers.get("N");
//	    attachDiscountsLoadingsTaxes(nonSubCoverList, discounts, loadings, taxes, taxUtils);
//
//	    SplitSubCoverUtil splitY = new SplitSubCoverUtil("Y",
//	            engine.getEffectiveDate(), engine.getPolicyEndDate());
//	    List<Cover> childSubCovers = covers.parallelStream()
//	            .map(splitY).filter(Objects::nonNull).collect(Collectors.toList());
//	    attachDiscountsLoadingsTaxes(childSubCovers, discounts, loadings, taxes, taxUtils);
//	    List<Cover> parentSubCovers = buildParentsWithSubcovers(childSubCovers);
//
//	    List<Cover> mergedCovers = new ArrayList<>();
//	    if (nonSubCoverList != null) mergedCovers.addAll(nonSubCoverList);
//	    if (parentSubCovers   != null) mergedCovers.addAll(parentSubCovers);
//
//	    mergedCovers = mergedCovers.stream()
//	            .peek(taxRemover).collect(Collectors.toList());
//
//	    mergedCovers.forEach(cover -> {
//	        if ("Y".equals(cover.getIsSubCover())) {
//	            if (cover.getSubcovers() != null)
//	                cover.getSubcovers().forEach(sub -> sub.setRate(derivedRate));
//	        } else {
//	            cover.setRate(derivedRate);
//	        }
//	    });
//
//	    CoverCalculator calc = new CoverCalculator();
//	    calc.setEngine(engine, resultCovers, commontbl, currentVehicle, customers, prorata,
//	            ratingutil, decimalFormat, drivers, customerChoiceTaxes);
//
//	    mergedCovers.parallelStream().forEach(calc);
//	    mergedCovers.removeIf(Cover::isNotsutable);
//
//	    resultCovers.addAll(mergedCovers);
//	    resultCovers.sort(Comparator.comparing(Cover::getCoverageType));
//	}
//	
//	
//	private List<EserviceMotorDetailsSaveRes> handlePropertyRequest(
//	        CalcEngine request, String token) {
//
//	    List<EserviceMotorDetailsSaveRes> resList = new ArrayList<>();
//
//	    try {
//	        List<EserviceBuildingDetails> allBuildings = eservicebuildingRepo
//	                .findByRequestReferenceNo(request.getRequestReferenceNo());
//
//	        if (allBuildings == null || allBuildings.isEmpty()) {
//	            System.out.println("No building records found for: "
//	                    + request.getRequestReferenceNo());
//	            return resList;
//	        }
//
//	        EserviceBuildingDetails first = allBuildings.get(0);
//
//	        CalcEngine engine = new CalcEngine();
//	        engine.setInsuranceId(first.getCompanyId());           
//	        engine.setProductId(first.getProductId());             
//	        engine.setBranchCode(first.getBranchCode());
//	        engine.setSectionId(first.getSectionId());
//	        engine.setMsrefno(first.getMsRefno().toString());
//	        engine.setCdRefNo(first.getCdRefno().toString());
//	        engine.setVdRefNo(first.getVdRefno().toString());
//	        engine.setCreatedBy(first.getCreatedBy());
//	        engine.setRequestReferenceNo(first.getRequestReferenceNo());
//	        engine.setAgencyCode(first.getAgencyCode());
//	        engine.setLocationId(first.getLocationId().toString());
//	        engine.setVehicleId(first.getRiskId().toString());
//	        engine.setEffectiveDate(
//	                first.getEndorsementEffdate() == null
//	                        ? first.getPolicyStartDate()
//	                        : first.getEndorsementEffdate());
//	        engine.setPolicyEndDate(
//	                request.getPolicyEndDate() == null
//	                        ? first.getPolicyEndDate()
//	                        : request.getPolicyEndDate());
//	        engine.setCoverModification(
//	                StringUtils.isBlank(request.getCoverModification())
//	                        ? "N" : request.getCoverModification());
//
//	        List<SectionCoverMaster> coverMasters = getSectionCoverDetails(
//	                first.getProductId(), first.getSectionId(), first.getCompanyId());
//	        if (CollectionUtils.isEmpty(coverMasters)) {
//	            engine.setCoverId(first.getCoverId() == null
//	                    ? "0" : first.getCoverId().toString());
//	        }
//
//	        System.out.println("100053 Property: single calculator() call for requestRef="
//	                + engine.getRequestReferenceNo()
//	                + " totalBuildings=" + allBuildings.size());
//
//	        EserviceMotorDetailsSaveRes res = calculator(engine, token);
//
//	        if (res != null) {
//	            resList.add(res);
//	        }
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//
//	    return resList;
//	}
//
//private List<EserviceMotorDetailsSaveRes> handleTravelRequest(CalcEngine request, String token) {
//
//	    List<EserviceMotorDetailsSaveRes> resList = new ArrayList<>();
//
//	    try {
//
//	        // Header data
//	        List<EserviceTravelDetails> travelDetails =
//	        		eserTraRepo.findByRequestReferenceNoAndProductId(request.getRequestReferenceNo(),request.getProductId());
//
//	        if (travelDetails.isEmpty()) {
//	            System.out.println("Travel Details not found for ReqRefNo: "
//	                    + request.getRequestReferenceNo());
//	            return resList;
//	        }
//
//	        EserviceTravelDetails travel = travelDetails.get(0);
//
//	        // Groups
//	        List<EserviceTravelGroupDetails> groupList =
//	                eserviceTravelGroupRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
//
//	        if (groupList.isEmpty()) {
//	            System.out.println("Travel Groups not found for ReqRefNo: "
//	                    + request.getRequestReferenceNo());
//	            return resList;
//	        }
//
//	        for (EserviceTravelGroupDetails group : groupList) {
//
//	            CalcEngine engine = new CalcEngine();
//
//	            engine.setLocationId(travel.getLocationId());
//	            engine.setBranchCode(travel.getBranchCode());
//	            engine.setInsuranceId(travel.getCompanyId());
//	            engine.setSectionId(travel.getSectionId());
//	            engine.setProductId(travel.getProductId());
//
//	            engine.setMsrefno(travel.getMsRefno().toString());
//	            engine.setCdRefNo(travel.getCdRefno().toString());
//	            engine.setVdRefNo(travel.getVdRefNo().toString());
//
//	            engine.setCreatedBy(travel.getCreatedBy());
//	            engine.setAgencyCode(travel.getAgencyCode());
//
//	            engine.setRequestReferenceNo(travel.getRequestReferenceNo());
//
//	            engine.setEffectiveDate(
//	                    travel.getEndorsementEffdate() == null
//	                            ? travel.getTravelStartDate()
//	                            : travel.getEndorsementEffdate()
//	            );
//
//	            engine.setPolicyEndDate(
//	                    request.getPolicyEndDate() == null
//	                            ? travel.getTravelEndDate()
//	                            : request.getPolicyEndDate()
//	            );
//
//	            engine.setCoverModification(
//	                    StringUtils.isBlank(request.getCoverModification())
//	                            ? "N"
//	                            : request.getCoverModification()
//	            );
//
//	            engine.setVehicleId(group.getGroupId().toString());
//
//	            ObjectMapper objectMapper = new ObjectMapper();
//	            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
//
//	            System.out.println(
//	                    "Travel Calculator Request → GroupId : "
//	                            + group.getGroupId()
//	                            + "\nRequest → "
//	                            + objectMapper.writeValueAsString(engine)
//	            );
//
//	            EserviceMotorDetailsSaveRes res = calculator(engine, token);
//
//	            resList.add(res);
//	        }
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//
//	    return resList;
//	}
//
//
}
