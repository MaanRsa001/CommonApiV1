package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.DocumentUniqueDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SectionMaster;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.res.AccessoriesRes;
import com.maan.eway.common.res.AccessoriesSumInsureDropDownRes;
import com.maan.eway.common.res.AdminViewQuoteAllRiskRes;
import com.maan.eway.common.res.AdminViewQuoteBurglaryRes;
import com.maan.eway.common.res.AdminViewQuoteBusinessInterruptionRes;
import com.maan.eway.common.res.AdminViewQuoteBusinessRiskRes;
import com.maan.eway.common.res.AdminViewQuoteCommonRes;
import com.maan.eway.common.res.AdminViewQuoteContentRes;
import com.maan.eway.common.res.AdminViewQuoteElecEquipmentRes;
import com.maan.eway.common.res.AdminViewQuoteEmpLiabilityRes;
import com.maan.eway.common.res.AdminViewQuoteFidelityRes;
import com.maan.eway.common.res.AdminViewQuoteFirePerilsRes;
import com.maan.eway.common.res.AdminViewQuoteGoodsInTransitRes;
import com.maan.eway.common.res.AdminViewQuoteMachineryBreakDownRes;
import com.maan.eway.common.res.AdminViewQuoteMoneyRes;
import com.maan.eway.common.res.AdminViewQuotePersonalAccidentRes;
import com.maan.eway.common.res.AdminViewQuotePlateGlassRes;
import com.maan.eway.common.res.AdminViewQuotePubLiabilityRes;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.BuildingSearchRes;
import com.maan.eway.common.res.DocumentDetailsRes;
import com.maan.eway.common.res.DocumentRes;
import com.maan.eway.common.res.PaymentCashRes;
import com.maan.eway.common.res.PaymentChequeRes;
import com.maan.eway.common.res.PaymentCreditRes;
import com.maan.eway.common.res.PaymentOnlineRes;
import com.maan.eway.common.res.PersonalAccidentRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchDriverDetailsRes;
import com.maan.eway.common.res.SearchPaymentInfoRes;
import com.maan.eway.common.res.SearchPremiumCoverDetailsRes;
import com.maan.eway.common.res.SearchPremiumDetailsRes;
import com.maan.eway.common.res.SearchROPDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleRes;
import com.maan.eway.common.res.SearchRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.common.service.BuildingSearchService;
import com.maan.eway.common.service.CommonSearchService;
import com.maan.eway.common.service.MotorSearchService;
import com.maan.eway.common.service.SearchService;
import com.maan.eway.common.service.TravelSearchService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.MotorVehicleInfoRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PremiaCustomerDetailsRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SubCoverRes;

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
@Transactional
public class SearchServiceImpl implements SearchService {

	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EmiTransactionDetailsRepository emirepo;

	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;

	@Autowired
	private MotorSearchService motService;
	@Autowired
	private BuildingSearchService buiService;

	@Autowired
	private PaymentInfoRepository paymentrepo;

	@Autowired
	private PremiaCustomerDetailsRepository premiaRepo;

	@Autowired
	private PersonalInfoRepository perRepo;
	
	@Autowired
	private PaymentDetailRepository paymentRepo;
	@Autowired
	private DocumentTransactionDetailsRepository coverdocumentuploaddetailsrepository;

	@Autowired
	private DocumentUniqueDetailsRepository docUniqueDetailsRepo;
	@Autowired
	private MotorVehicleInfoRepository motVehInfoRepo;

	@Autowired
	private CommonSearchService commonSearch;
	@Autowired
	private TravelSearchService travelSearch;
	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private MotorDriverDetailsRepository driverRepo;

	@Autowired
	private MotorDataDetailsRepository motorRepo;

	@Autowired
	private CoverDetailsRepository coverRepo;

	@Autowired
	BuildingDetailsRepository buildingrepo;
	
	@Autowired
   private	EserviceBuildingDetailsRepository eservicebuildingrepo;

	@Autowired
	ProductEmployeesDetailsRepository personalRepository;
	
	@Autowired
	private	EserviceTravelDetailsRepository eTravelrepo;
	
	@Autowired
	private	EserviceCommonDetailsRepository eCommRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eBuildingRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository eCommonRepo;
	
	@Autowired
	private SectionDataDetailsRepository sectiondata;

	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(SearchServiceImpl.class);

	//Dropdown
	//CopyQuote Dropdown 
	

	@Override
	public List<BuildingSearchRes> adminSearchBuildingDeatails(SearchReq req) {
		List<BuildingSearchRes> builLisRes=new ArrayList<BuildingSearchRes>();
		try {
			
			
			BuildingSearchRes bulRes=new BuildingSearchRes();
			List<BuildingDetails> buldingListDt = new ArrayList<BuildingDetails>();
			
			buldingListDt=buildingrepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			
			
            if(buldingListDt!=null && buldingListDt.size()>0)

            {
            	for(BuildingDetails data:buldingListDt)
            	{
            		bulRes = new DozerBeanMapper().map(data, BuildingSearchRes.class);
            		builLisRes.add(bulRes);

            	}
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		
		return builLisRes;
	}
	
	@Override
	public List<PersonalAccidentRes> viewPersonalAccidentDetails(SearchReq req) {
		
		List<PersonalAccidentRes> preslist = new ArrayList<PersonalAccidentRes>();

		try {

			PersonalAccidentRes pres = new PersonalAccidentRes();

			List<ProductEmployeeDetails> personalList = new ArrayList<ProductEmployeeDetails>();

			
			 if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				 personalList = personalRepository.findByRequestReferenceNo(req.getRequestReferenceNo());
			}
			
            if(personalList!=null && personalList.size()>0)
            {
			for (ProductEmployeeDetails data : personalList) {

				pres = new DozerBeanMapper().map(data, PersonalAccidentRes.class);
				preslist.add(pres);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return preslist;
	}	

	
	@Override
	public List<DropDownRes> searchDropdown(CopyQuoteDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

			List<ListItemValue> getList = new ArrayList<ListItemValue>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				getList = motService.searchDropdownMotor(req);
			} else if (product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
				getList = travelSearch.searchDropdownTravel(req);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				getList = buiService.searchDropdownBuilding(req);
			} else {
				getList = commonSearch.searchDropdownCommon(req);
			}

			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setCodeDescLocal(data.getItemValueLocal());
				res.setStatus(data.getStatus());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<SearchRes> adminSearchOrderByEntryDate(SearchReq req) {
		List<SearchRes> reslist = new ArrayList<SearchRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		try {
			Date effectiveDate = null;
			List<BranchMaster> branchlist = getByBranchCode(req.getBranchCode());
			String branchName = branchlist.get(0).getBranchName();
			String loginId = "";
			List<String> branches = new ArrayList<String>();
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res

			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

			branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
					.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
			if (branches.size() <= 0) {
				branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

			}

			branches.add(req.getBranchCode());
			List<Tuple> list = null;
			CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M") ) {
				list = motService.adminSearchMotorQuote(req, branches);
			}

			else if (product.getMotorYn().equalsIgnoreCase("H")  && req.getProductId().equalsIgnoreCase(travelProductId)) {
				list = travelSearch.searchTravel(req, branches);
			} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
				list = buiService.searchBuilding(req, branches);
			} else {
				list = commonSearch.searchCommon(req, branches);
			}

			for (Tuple data : list) {
				SearchRes res = new SearchRes();
				if (product.getMotorYn().equalsIgnoreCase("M") ) {
				res = dozermapper.map(data.get(0), SearchRes.class);
				res.setCustomerReferenceNo(data.get("customerReferenceNo")==null?null:data.get("customerReferenceNo").toString());
				res.setClientName(data.get("clientName")==null?null:data.get("clientName").toString());
				res.setMobileNo1(data.get("mobileNumber").toString());
				res.setBranchName(branchName);
				res.setLoginId(data.get("maxLoginId")==null?null:data.get("maxLoginId").toString());
				
				res.setEffectiveDate(effectiveDate);
				res.setCurrency(data.get("maxCurrency")==null?null:data.get("maxCurrency").toString());
				//res.setCurrency(data.get("currency").toString());
				String entryDate = data.get("maxEntryDate") == null ? null: dateFormat.format(data.get("maxEntryDate"));
				res.setEntryDate(entryDate);
				res.setExchangeRate(data.get("maxExchangeRate")==null?null:data.get("maxExchangeRate").toString());
				res.setGpsTrackingInstalled(data.get("maxGpsTracking")==null?null:data.get("maxGpsTracking").toString());
				res.setOverallPremiumLc(data.get("maxOverallPremium")==null?null:data.get("maxOverallPremium").toString());
				String policyStartDate = data.get("maxPolicyStartDate") == null ? null
						: dateFormat.format(data.get("maxPolicyStartDate"));
				res.setPolicyStartDate(policyStartDate);
				String policyEndDate = data.get("maxPolicyEndDate") == null ? null
						: dateFormat.format(data.get("maxPolicyEndDate"));
				res.setPolicyEndDate(policyEndDate);
				res.setPolicyNo(data.get("maxPolicyNo") == null ? null :data.get("maxPolicyNo").toString());
				res.setPolicyTypeDesc(data.get("maxPolicyType") == null ? null :data.get("maxPolicyType").toString());
				res.setVehicleTypeDesc(data.get("maxVehicleType") == null ? null :data.get("maxVehicleType").toString());
				res.setQuoteNo(data.get("maxQuoteNo") == null ? null :data.get("maxQuoteNo").toString());
				res.setRequestReferenceNo(data.get("maxRequestReferenceNo") == null ? null :data.get("maxRequestReferenceNo").toString());
				res.setStatus(data.get("maxStatus") == null ? null :data.get("maxStatus").toString());
				res.setWindScreenCoverRequired(data.get("maxWindScreenCover") == null ? null :data.get("maxWindScreenCover").toString());
				res.setProductName(data.get("maxProductName")==null?null:data.get("maxProductName").toString());	
				res.setEmiYn(data.get("maxEmi")==null?null:data.get("maxEmi").toString());
				res.setEmiPremium(data.get("maxEmiPremium")==null?null:data.get("maxEmiPremium").toString());
				res.setInstallmentPeriod(data.get("maxInstallmentPeriod")==null?null:data.get("maxInstallmentPeriod").toString());
				res.setNoOfInstallment(data.get("maxNoOfInstallments")==null?null:data.get("maxNoOfInstallments").toString());
				// res.setIdsCount(data.get("idsCount")==null?"":data.get("idsCount").toString());
				}	
				else if (product.getMotorYn().equalsIgnoreCase("H")  && req.getProductId().equalsIgnoreCase(travelProductId)) {
					res.setClientName(data.get("clientName")==null?null:data.get("clientName").toString());
					res.setMobileNo1(data.get("mobileNumber").toString());
					res.setBranchName(branchName);
					//res.setLoginId(data.get("loginId")==null?null:data.get("loginId").toString());
					res.setEffectiveDate(effectiveDate);
					res.setCurrency(data.get("currency").toString());
					String entryDate = data.get("entryDate") == null ? null
							: dateFormat.format(data.get("entryDate"));
					res.setEntryDate(entryDate);
					res.setExchangeRate(data.get("exchangeRate")==null?null:data.get("exchangeRate").toString());
					res.setOverallPremiumLc(data.get("overallPremiumLc")==null?null:data.get("overallPremiumLc").toString());
					String policyStartDate = data.get("policyStartDate") == null ? null
							: dateFormat.format(data.get("policyStartDate"));
					res.setPolicyStartDate(policyStartDate);
					String policyEndDate = data.get("policyEndDate") == null ? null
							: dateFormat.format(data.get("policyEndDate"));
					res.setPolicyEndDate(policyEndDate);
					res.setPolicyNo(data.get("policyNo") == null ? null :data.get("policyNo").toString());
					//res.setPolicyTypeDesc(data.get("policyTypeDesc") == null ? null :data.get("policyTypeDesc").toString());
					res.setQuoteNo(data.get("quoteNo") == null ? null :data.get("quoteNo").toString());
					res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null :data.get("requestReferenceNo").toString());
					res.setStatus(data.get("status") == null ? null :data.get("status").toString());
					res.setProductName(data.get("productName")==null?null:data.get("productName").toString());	
					res.setEmiYn(data.get("emiYn")==null?null:data.get("emiYn").toString());
					res.setEmiPremium(data.get("emiPremium")==null?null:data.get("emiPremium").toString());
					res.setInstallmentPeriod(data.get("installmentPeriod")==null?null:data.get("installmentPeriod").toString());
					res.setNoOfInstallment(data.get("noOfInstallment")==null?null:data.get("noOfInstallment").toString());

					
				}else if (product.getMotorYn().equalsIgnoreCase("A") ) {
					res.setClientName(data.get("clientName")==null?null:data.get("clientName").toString());
					res.setMobileNo1(data.get("mobileNumber").toString());
					res.setBranchName(branchName);
					//res.setLoginId(data.get("loginId")==null?null:data.get("loginId").toString());
					res.setEffectiveDate(effectiveDate);
					res.setCurrency(data.get("currency").toString());
					String entryDate = data.get("entryDate") == null ? null
							: dateFormat.format(data.get("entryDate"));
					res.setEntryDate(entryDate);
					res.setExchangeRate(data.get("exchangeRate")==null?null:data.get("exchangeRate").toString());
					res.setOverallPremiumLc(data.get("overallPremiumLc")==null?null:data.get("overallPremiumLc").toString());
					String policyStartDate = data.get("policyStartDate") == null ? null
							: dateFormat.format(data.get("policyStartDate"));
					res.setPolicyStartDate(policyStartDate);
					String policyEndDate = data.get("policyEndDate") == null ? null
							: dateFormat.format(data.get("policyEndDate"));
					res.setPolicyEndDate(policyEndDate);
					res.setPolicyNo(data.get("policyNo") == null ? null :data.get("policyNo").toString());
					//res.setPolicyTypeDesc(data.get("policyTypeDesc") == null ? null :data.get("policyTypeDesc").toString());
					res.setQuoteNo(data.get("quoteNo") == null ? null :data.get("quoteNo").toString());
					res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null :data.get("requestReferenceNo").toString());
					res.setStatus(data.get("status") == null ? null :data.get("status").toString());
					res.setProductName(data.get("productDesc")==null?null:data.get("productDesc").toString());		
					res.setEmiYn(data.get("emiYn")==null?null:data.get("emiYn").toString());
					res.setEmiPremium(data.get("emiPremium")==null?null:data.get("emiPremium").toString());
					res.setInstallmentPeriod(data.get("installmentPeriod")==null?null:data.get("installmentPeriod").toString());
					res.setNoOfInstallment(data.get("noOfInstallment")==null?null:data.get("noOfInstallment").toString());

				}else {
					res.setClientName(data.get("clientName")==null?null:data.get("clientName").toString());
					res.setMobileNo1(data.get("mobileNumber").toString());
					res.setBranchName(branchName);
				//	res.setLoginId(data.get("loginId")==null?null:data.get("loginId").toString());
					res.setEffectiveDate(effectiveDate);
					res.setCurrency(data.get("currency").toString());
					String entryDate = data.get("entryDate") == null ? null
							: dateFormat.format(data.get("entryDate"));
					res.setEntryDate(entryDate);
					res.setExchangeRate(data.get("exchangeRate")==null?null:data.get("exchangeRate").toString());
					res.setOverallPremiumLc(data.get("overallPremiumLc")==null?null:data.get("overallPremiumLc").toString());
					String policyStartDate = data.get("policyStartDate") == null ? null
							: dateFormat.format(data.get("policyStartDate"));
					res.setPolicyStartDate(policyStartDate);
					String policyEndDate = data.get("policyEndDate") == null ? null
							: dateFormat.format(data.get("policyEndDate"));
					res.setPolicyEndDate(policyEndDate);
					
					res.setPolicyNo(data.get("policyNo") == null ? null :data.get("policyNo").toString());
					res.setQuoteNo(data.get("quoteNo") == null ? null :data.get("quoteNo").toString());
					res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null :data.get("requestReferenceNo").toString());
					res.setStatus(data.get("status") == null ? null :data.get("status").toString());
					res.setProductName(data.get("productDesc")==null?null:data.get("productDesc").toString());
					res.setEmiYn(data.get("emiYn")==null?null:data.get("emiYn").toString());
					res.setEmiPremium(data.get("emiPremium")==null?null:data.get("emiPremium").toString());
					res.setInstallmentPeriod(data.get("installmentPeriod")==null?null:data.get("installmentPeriod").toString());
					res.setNoOfInstallment(data.get("noOfInstallment")==null?null:data.get("noOfInstallment").toString());
				}
				
				reslist.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}

	//BranchName
	public List<BranchMaster> getByBranchCode(String branchCode) {
		//BranchMaster res = new BranchMasterRes();
		List<BranchMaster> list = new ArrayList<BranchMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BranchMaster> query = cb.createQuery(BranchMaster.class);
			
			
			// Find All
			Root<BranchMaster> c = query.from(BranchMaster.class);		
			
			// Select
			query.select(c );
			
			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BranchMaster> ocpm1 = amendId.from(BranchMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode") );
			
			amendId.where(a1);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			// Where
			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n4 = cb.equal(c.get("branchCode"), branchCode);
			query.where(n1,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<BranchMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBranchCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BranchMaster :: getBranchName ));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}	


	@Override
	public AdminViewQuoteRes adminViewQuoteDetails(SearchReq req) {
		AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
		try {
//			HomePositionMaster homeData =null;
//			if(StringUtils.isNotBlank(req.getQuoteNo())){
//				homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());	
//			}
			// Motor Product Details
			CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

			if(product.getMotorYn().equalsIgnoreCase("M") ) {
				viewRes =motService.getMotorProductDetails( req);
				
			}
			else if(product.getMotorYn().equalsIgnoreCase("H")  && req.getProductId().equals(Integer.valueOf(travelProductId))) {
				// Travel Product Details
				viewRes =travelSearch.getTravelProductDetails( req);
				
			} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
				// Building Product Details
				viewRes =buiService.getBuildingProductDetails( req);
				
			} else {
				
				// Travel Product Details
				viewRes =commonSearch.getCommonProductDetails( req);
				
			}
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}

	
	@Override
	public AdminViewQuoteCommonRes adminViewQuoteRiskDetails(SearchReq req) {
		
		EserviceBuildingDetails build = new EserviceBuildingDetails();
		
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		EserviceCommonDetails common = new EserviceCommonDetails();
		
		AdminViewQuoteCommonRes res = new AdminViewQuoteCommonRes();
		
		try
		{
			
		List<EserviceBuildingDetails> building = eservicebuildingrepo.findByRequestReferenceNo(req.getRequestReferenceNo());
		List<EserviceCommonDetails> commondata = eCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
		
		for(EserviceBuildingDetails id : building)
		{	
	    if(id.getSectionId().equalsIgnoreCase("47")) 
	    {
	    	if(building.size() > 0) 
	    	{
	    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
	    		
	    		AdminViewQuoteContentRes contentres = new AdminViewQuoteContentRes();	    		
	    		
	    			dozerMapper.map(contentres, data);
//		    	    contentres.setContentSumInsured(id.getContentSuminsured());
	    			contentres.setContentSumInsured(id.getSumInsured());
		    	    res.setContentRisk(contentres);
	    		
	    	 }
	    }
	    if(id.getSectionId().equalsIgnoreCase("3")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteAllRiskRes allriskres = new AdminViewQuoteAllRiskRes();	    		
		    	    
		    	    
		    			dozerMapper.map(allriskres, data);
//		    			allriskres.setAllRiskSumInsured(id.getSumInsured());
		    			allriskres.setAllRiskSumInsured(id.getSumInsured());
			    	    res.setAllRisk(allriskres);		    		}
		    	
		  }
	    if(id.getSectionId().equalsIgnoreCase("41")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteMachineryBreakDownRes machineryBreakres = new AdminViewQuoteMachineryBreakDownRes();	    		
		    	    
		    	    
		    		
		    			dozerMapper.map(machineryBreakres, data);
//		    			machineryBreakres.setBoilerPlantsSi(id.getBoilerPlantsSi());
//		    			machineryBreakres.setElecMachinesSi(id.getElecMachinesSi());
//		    			machineryBreakres.setEquipmentSi(id.getEquipmentSi());
//		    			machineryBreakres.setGeneralMachineSi(id.getGeneralMachineSi());
//		    			machineryBreakres.setMachineEquipSi(id.getMachineEquipSi());	 
//		    			machineryBreakres.setManuUnitsSi(id.getManuUnitsSi());
//		    			machineryBreakres.setPowerPlantSi(id.getPowerPlantSi());  
//		    			machineryBreakres.setMachinerySi(id.getMachinerySi());
		    			machineryBreakres.setSumInsured(id.getSumInsured());
		    			res.setMachineryBreakDownRisk(machineryBreakres);   		
			    	
		    	 }
		  }
		
	    if(id.getSectionId().equalsIgnoreCase("42")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteMoneyRes moneyRes = new AdminViewQuoteMoneyRes();	    		
		    	    
		    	    
		    		
		    			dozerMapper.map(moneyRes, data);
//		    			moneyRes.setMoneyAnnualEstimate(id.getMoneyAnnualEstimate());
//		    			moneyRes.setMoneyCollector(id.getMoneyCollector());
//		    			moneyRes.setMoneyDirectorResidence(id.getMoneyDirectorResidence());
//		    			moneyRes.setMoneyMajorLoss(id.getMoneyMajorLoss());	
//		    			moneyRes.setMoneyOutofSafe(id.getMoneyOutofSafe());
//		    			moneyRes.setMoneySafeLimit(id.getMoneySafeLimit());
		    			res.setMoneyRisk(moneyRes); 		
			    	 
		    	 }
		  }
		
	     if(id.getSectionId().equalsIgnoreCase("52")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		 List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		 AdminViewQuoteBurglaryRes burglaryres = new AdminViewQuoteBurglaryRes();	    		
		    	    
		    			dozerMapper.map(burglaryres, data);
		    		//	burglaryres.setAccessibleWindows(id.getAccessibleWindows());		    		
		    			burglaryres.setAddress(id.getAddress());
		    		//	burglaryres.setApplianceLossPercent(id.getApplianceLossPercent());
		    		//	burglaryres.setApplianceSi(id.getApplianceSi());
		    		//	burglaryres.setBackDoors(id.getBackDoors());
		    		//	burglaryres.setBuildingBuildYear(id.getBuildingBuildYear());
		    			burglaryres.setBuildingBuildYearDesc(id.getBuildingOccupationType());
		    		//	burglaryres.setBuildingOccupied(id.getBuildingOccupied());
		    		//	burglaryres.setCashValueablesLossPercent(id.getCashValueablesLossPercent());
		    		//	burglaryres.setCashValueablesSi(id.getCashValueablesSi());	
		    			burglaryres.setCeilingType(id.getCeilingType());
		    			burglaryres.setCeilingTypeDesc(id.getCeilingTypeDesc());
		    			burglaryres.setDistrictCode(id.getDistrictCode());
		    	//		burglaryres.setDoorsMaterialId(id.getDoorsMaterialDesc());	 
		    		//	burglaryres.setFrontDoors(id.getFrontDoors());
		    		//	burglaryres.setFurnitureLossPercent(id.getFurnitureLossPercent());
		    		//	burglaryres.setFurnitureSi(id.getFurnitureSi());
		    		//	burglaryres.setGoodsLossPercent(id.getGoodsLossPercent());
		    		//	burglaryres.setGoodsSi(id.getGoodsSi());
		    			burglaryres.setInsuranceForId(id.getInsuranceForId());
		    			burglaryres.setInternalWallType(id.getInternalWallType());
		    			burglaryres.setInternalWallTypeDesc(id.getInternalWallDesc());
		    			burglaryres.setNatureOfTradeId(id.getNatureOfTradeId());
		    			burglaryres.setNatureOfTradeDesc(id.getNatureOfTradeDesc());	    			
		    		//	burglaryres.setStockInTradeSi(id.getStockInTradeSi());	
		    		//	burglaryres.setNightLeftDoor(id.getNightLeftDoor());
		    			burglaryres.setOccupiedYear(id.getOccupiedYear());
		    			burglaryres.setOccupiedYearDesc(id.getOccupationTypeDesc());
		    			burglaryres.setRegionCode(id.getRegionCode());
		    			burglaryres.setRoofType(id.getRoofType());
//		    			burglaryres.setShowWindow(id.getShowWindow());
		    		//	burglaryres.setStockLossPercent(id.getStockLossPercent());
		    			burglaryres.setOccupion(id.getOccupationType());
		    			burglaryres.setOccupionDesc(id.getOccupationTypeDesc());
		    			burglaryres.setWallType(id.getWallType());
		    			burglaryres.setRoofTypeDesc(id.getRoofTypeDesc());
		    		//	burglaryres.setWatchmanGuardHours(id.getWatchmanGuardHours());
		    			burglaryres.setWallTypeDesc(id.getWallTypeDesc());;
		    		//	burglaryres.setWindowsMaterialId(id.getWindowsMaterialId());	 
//		    			burglaryres.setBurglarySi(id.getBurglarySi());
		    			burglaryres.setBurglarySi(id.getSumInsured());
		    		//	burglaryres.setWindowsMaterialDesc(id.getWindowsMaterialDesc());	
		    		//	burglaryres.setDoorsMaterialIdDesc(id.getDoorsMaterialDesc());
		    		//	burglaryres.setNightLeftDoorDesc(id.getNightLeftDoorDesc());
		    			res.setBurglaryRisk(burglaryres);
		    	}
		    	
		  }
		
	    if(id.getSectionId().equalsIgnoreCase("69")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteBusinessRiskRes businessres = new AdminViewQuoteBusinessRiskRes();	    		
		    	    
		    	    
		    		dozerMapper.map(businessres, data);
		    			businessres.setAllriskSumInsured(id.getSumInsured());
		    			res.setBusinessRisk(businessres);    	
		    		
		    	 }
		  }
	    
	    if(id.getSectionId().equalsIgnoreCase("39")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteElecEquipmentRes elecequipres = new AdminViewQuoteElecEquipmentRes();	    		
		    	    
		    	    
		    		dozerMapper.map(elecequipres, data);
		    		elecequipres.setElecEquipSumInsured(id.getSumInsured());	
		    		res.setElecEquipRisk(elecequipres);	
		    		
		    	 }
		  }
	    
	    if(id.getSectionId().equalsIgnoreCase("53")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuotePlateGlassRes plateglassres = new AdminViewQuotePlateGlassRes();	    		
		    	    
		    	    
		    		dozerMapper.map(plateglassres, data);
		    		plateglassres.setPlateGlassSi(id.getSumInsured());
		    		plateglassres.setPlateGlassType(id.getPlateGlassType());	    		
		    		res.setPlateGlassRisk(plateglassres);		    		
		    	 }
		  }
	    
	    if(id.getSectionId().equalsIgnoreCase("75")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteBusinessInterruptionRes busiIntter = new AdminViewQuoteBusinessInterruptionRes();	    		
		    	    
		    	    
		    		dozerMapper.map(busiIntter, data);
		    		busiIntter.setGrossProfitSi(id.getSumInsured()); 
		    		busiIntter.setIndemnityPeriodSi(id.getSumInsured());		    		
		    		res.setBusinessInterruptionRisk(busiIntter);		    		
		    	 }
		  }
	    
	    if(id.getSectionId().equalsIgnoreCase("46")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteGoodsInTransitRes goodsintrans = new AdminViewQuoteGoodsInTransitRes();	    		
		    	    
		    	    
		    		dozerMapper.map(goodsintrans, data);
		    		goodsintrans.setTransportedBy(id.getTransportedBy());
		    		goodsintrans.setGeographicalCoverage(id.getGeographicalCoverage());
		    		goodsintrans.setModeOfTransport(id.getModeOfTransport());
//		    		goodsintrans.setSingleRoadSiFc(id.getSingleRoadSiFc());
//		    		goodsintrans.setEstAnnualCarriesSiFc(id.getEstAnnualCarriesSiFc());
		    		res.setGoodsInTransitRisk(goodsintrans);   		
		    	 }
		  }
	    
	    if(id.getSectionId().equalsIgnoreCase("40")) 
		 {
		    	if(building.size() > 0) 
		    	{
		    		List<EserviceBuildingDetails> data = new ArrayList<EserviceBuildingDetails>();
		    		
		    		AdminViewQuoteFirePerilsRes fireperils = new AdminViewQuoteFirePerilsRes();	    		
		    	    
		    	    
		    		dozerMapper.map(fireperils, data);
		    		fireperils.setBuildingSumInsured(id.getSumInsured());
		    		fireperils.setIndemityPeriod(id.getIndemityPeriod());
		    		fireperils.setMakutiYn(id.getMakutiYn());
		    		res.setFirePerilsRisk(fireperils); 		
		    	 }
		  }
	    
	    
		}
	
	    
	    for(EserviceCommonDetails ids : commondata)
		{
	    
	          if(ids.getSectionId().equalsIgnoreCase("43")) 
		      {
		    	if(commondata.size() > 0) 
		    	{
		    		List<EserviceCommonDetails> data = new ArrayList<EserviceCommonDetails>();
		    		
		    		AdminViewQuoteFidelityRes fidelityres = new AdminViewQuoteFidelityRes();	    		
		    	    
		    	    
		    		dozerMapper.map(fidelityres, data);
		    		fidelityres.setFidEmpCount(ids.getFidEmpCount());
//		    		fidelityres.setFidEmpSi(ids.getFidEmpSi());
		    		fidelityres.setSumInsured(ids.getSumInsured());
		    		fidelityres.setOccupationTypeDesc(ids.getOccupationDesc());
		    		res.setFidelityRisk(fidelityres);		    		
		    	 }
		      }
		    	
		        if(ids.getSectionId().equalsIgnoreCase("35")) 
				 {
				    	if(commondata.size() > 0) 
				    	{
				    		List<EserviceCommonDetails> data = new ArrayList<EserviceCommonDetails>();
				    		
				    		AdminViewQuotePersonalAccidentRes pares = new AdminViewQuotePersonalAccidentRes();	    		
				    	    
				    	    
				    		dozerMapper.map(pares, data);
				    		pares.setOccupationType(ids.getOccupationType());
				    		pares.setOccupationTypeDesc(ids.getOccupationDesc());
				    		pares.setSumInsured(ids.getSumInsuredLc ());	    		
				    		res.setPersonalAccident(pares); 	
				    		
				    	 }
				  }
//		        personalLiability
		        if(ids.getSectionId().equalsIgnoreCase("36")) 
				 {
				    	if(commondata.size() > 0) 
				    	{
				    		List<EserviceCommonDetails> data = new ArrayList<EserviceCommonDetails>();
				    		
				    		AdminViewQuotePersonalAccidentRes pares = new AdminViewQuotePersonalAccidentRes();	    		
				    	    
				    	    
				    		dozerMapper.map(pares, data);
				    		pares.setOccupationType(ids.getOccupationType());
				    		pares.setOccupationTypeDesc(ids.getOccupationDesc());
//				    		pares.setSumInsured(ids.getEmpLiabilitySiLc());
				    		pares.setSumInsured(ids.getSumInsured());
				    		res.setPersonalLiability(pares);
				    		
				    	 }
				  }
		        if(ids.getSectionId().equalsIgnoreCase("45")) 
				 {
				    	if(commondata.size() > 0) 
				    	{
				    		List<EserviceCommonDetails> data = new ArrayList<EserviceCommonDetails>();
				    		
				    		AdminViewQuoteEmpLiabilityRes empliability = new AdminViewQuoteEmpLiabilityRes();	    		
				    	    
				    	    
				    		dozerMapper.map(empliability, data);
//				    		empliability.setEmpLiabilitySi(ids.getEmpLiabilitySi());
				    		empliability.setSumInsured(ids.getSumInsured());	
				    		empliability.setTotalNoOfEmployees(ids.getTotalNoOfEmployees());	
				    		empliability.setOccupationTypeDesc(ids.getOccupationDesc());
				    		res.setEmpLiability(empliability);				    		
				    	 }
				  }
		        
		        if(ids.getSectionId().equalsIgnoreCase("54")) 
				 {
				    	if(commondata.size() > 0) 
				    	{
				    		List<EserviceCommonDetails> data = new ArrayList<EserviceCommonDetails>();
				    		
				    		AdminViewQuotePubLiabilityRes publiability = new AdminViewQuotePubLiabilityRes();	    		
				    	    
				    	    
				    		dozerMapper.map(publiability, data);
//				    		publiability.setLiabilitySi(ids.getLiabilitySi());
//				    		publiability.setAggSumInsured(ids.getAggSuminsured());
//				    		publiability.setAooSumInsured(ids.getAooSuminsured());
				    		publiability.setSumInsured(ids.getSumInsured());	
//				    		publiability.setProductTurnoverSi(ids.getProductTurnoverSi());
				    		publiability.setCategory(ids.getCategoryId());
				    		res.setPublicLiabilityRisk(publiability);	    		
				    	 }
				   }
		       
		 
		  }
		  
	    res.setQuoteNo(req.getQuoteNo());
	    res.setRequestReferenceNo(req.getRequestReferenceNo());
	    }
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return res;
	}
	
	public List<SearchPremiumCoverDetailsRes> getCoverDetails(Map<Integer,List<PolicyCoverData>> groupByCover  ) {
		List<SearchPremiumCoverDetailsRes>  coverListRes = new ArrayList<SearchPremiumCoverDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String sectionName="";
			for ( Integer coverId : groupByCover.keySet() ) {
				List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
				SearchPremiumCoverDetailsRes coverRes = new SearchPremiumCoverDetailsRes();
				
				if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
					// Get Covers
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes = dozerMapper.map(filterCover.get(0), SearchPremiumCoverDetailsRes.class);
					coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setCoverageType(filterCover.get(0).getCoverageType());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());	
					if(!StringUtils.isBlank(filterCover.get(0).getSectionId().toString())){
						List<SectionMaster> sectiondata=getBySectionId(filterCover.get(0).getSectionId().toString());
						sectionName=sectiondata.get(0).getSectionName();
					}
					coverRes.setSectionName(sectionName);
										
				} else {
					
					// Get Sub Covers
			
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
					 coverRes.setCoverName(filterCover.get(0).getCoverName());
					 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
					 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
					 coverRes.setRate(filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
						coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
						coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
						coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());	
					List<SubCoverRes>  subCoverListRes = new ArrayList<SubCoverRes>();
					List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
					for ( PolicyCoverData subCovers : filterSubCover) {
						SubCoverRes subCoverRes = new SubCoverRes();
						subCoverRes = dozerMapper.map(subCovers, SubCoverRes.class);
						subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
						subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
						subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
						subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
						subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
						subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
						subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
						subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
						subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
						subCoverRes.setRegulatoryCode(filterCover.get(0).getRegulatoryCode());
						coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
						coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
						coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
						if(!StringUtils.isBlank(filterCover.get(0).getSectionId().toString())){
							List<SectionMaster> sectiondata=getBySectionId(filterCover.get(0).getSectionId().toString());
							sectionName=sectiondata.get(0).getSectionName();
						}
						coverRes.setSectionName(sectionName);
						subCoverListRes.add(subCoverRes);
					}
					coverRes.setSubcovers(subCoverListRes);
				}
				coverListRes.add(coverRes);
			}
	
			coverListRes.sort(Comparator.comparing(SearchPremiumCoverDetailsRes :: getCoverId));;
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return coverListRes;
	}
	
	public List<SectionMaster> getBySectionId(String sectionid) {
		List<SectionMaster> list = new ArrayList<SectionMaster>();
	
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SectionMaster> query = cb.createQuery(SectionMaster.class);
	
			
			// Find All
			Root<SectionMaster>    c = query.from(SectionMaster.class);		
			
			// Select
			query.select(c );
			
			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<SectionMaster> ocpm1 = amendId.from(SectionMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("sectionId"),ocpm1.get("sectionId") );
			amendId.where(a1);
			
			
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("effectiveDateStart")));
			
		    // Where	
		
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("amendId"), amendId);		
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("sectionId"),sectionid) ;
			query.where(n1 ,n2).orderBy(orderList);
			
			// Get Result
			TypedQuery<SectionMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	
	

// Customer Details -Search
	@Override
	public List<SearchCustomerDetailsRes> adminCustomerSearch(SearchReq req) {
		List<SearchCustomerDetailsRes> reslist = new ArrayList<SearchCustomerDetailsRes>();
		try {
			List<HomePositionMaster> homeData = null;
			
			
				CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

				if (product.getMotorYn().equalsIgnoreCase("M") ) {
					reslist = motService.motorCustSearch(req, product);
				} else if (product.getMotorYn().equalsIgnoreCase("H")  && req.getProductId().equalsIgnoreCase(travelProductId)) {
					reslist = travelSearch.travelCustSearch(req, product);
				} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
					reslist = buiService.buildingCustSearch(req, product);
				} else {
					reslist = commonSearch.commonCustSearch(req, product);
				}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}



	//Rating Details
	@Override
	public List<SearchEservieMotorDetailsViewRatingRes> adminViewRatingDetails(SearchReq req) {
		List<SearchEservieMotorDetailsViewRatingRes>  resList = new ArrayList<SearchEservieMotorDetailsViewRatingRes>();
		try {
			CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M") ) {
				resList = motService.motorRating(req);
			} else if (product.getMotorYn().equalsIgnoreCase("H")  && req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = travelSearch.travelRating(req);
			} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
				resList = buiService.buildingRating();
			} else {
				resList = commonSearch.commonRating(req);
			}

		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return resList;
	}

	//Premium Details
	@Override
	public SearchPremiumDetailsRes adminPremiumSearch(SearchReq req) {
		SearchPremiumDetailsRes viewRes = new SearchPremiumDetailsRes();
		try {
			List<MotorDataDetails> motorid=null;
			List<PolicyCoverData> covers=null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				// Find Motor Data
				 motorid = motorRepo
						.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
				 covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			}else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				 motorid = motorRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
					 covers = coverRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
			}
				for (MotorDataDetails mot : motorid) {
					// Cover Details
					List<PolicyCoverData> filterCovers = covers.stream()
							.filter(o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId())))
							.collect(Collectors.toList());

					Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
							.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

					List<SearchPremiumCoverDetailsRes> coverListRes = getCoverDetails(groupByCover);
					viewRes.setSearchPremiumCoverDetailsRes(coverListRes);

				}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	//ROP Driver Details
	@Override
	public SearchROPDetailsRes adminROPDriverSearch(SearchReq req) {
		SearchROPDetailsRes viewRes = new SearchROPDetailsRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<MotorDataDetails> motorid=null;
			List<MotorDriverDetails> driverList =null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				// Find Motor Data
				 motorid = motorRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(req.getQuoteNo(), "D");
				 driverList = driverRepo.findByQuoteNo(req.getQuoteNo() );
			}else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				 motorid = motorRepo.findByRequestReferenceNoAndStatusNotOrderByVehicleIdAsc(req.getRequestReferenceNo(), "D");
				 driverList = driverRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
			}
			for (MotorDataDetails mot : motorid) {
			List<SearchDriverDetailsRes>   driverResList = new ArrayList<SearchDriverDetailsRes>();
			List<MotorDriverDetails> filterDriverList = driverList.stream().filter( o -> o.getRiskId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
			for (MotorDriverDetails dri :  filterDriverList) {
				SearchDriverDetailsRes driverRes  = new SearchDriverDetailsRes();  
				dozerMapper.map(dri, driverRes);
				driverRes.setLicenseNo(dri.getIdNumber());
				
				driverResList.add(driverRes);
				
			}
			driverResList.sort(Comparator.comparing(SearchDriverDetailsRes :: getDriverId  ));
			viewRes.setDriverDetails(driverResList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}


	//ROP Vehicle Details
	@Override
	public SearchROPVehicleDetailsRes adminROPVehicleSearch(SearchReq req) {
		SearchROPVehicleDetailsRes viewRes = new SearchROPVehicleDetailsRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SectionDataDetails ss= new SectionDataDetails();
		try {
			
		List<EserviceMotorDetails> motorid=null;
		String chassisNo="";
		if (StringUtils.isNotBlank(req.getQuoteNo())) {
			// Find Motor Data
			 motorid = repo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			
			 }else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
			 motorid = repo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
		}
		
		List<SearchROPVehicleRes> resList=new ArrayList<SearchROPVehicleRes>();
		for (EserviceMotorDetails data : motorid) { 
			ss=sectiondata.findByQuoteNoAndSectionIdAndRiskId(req.getQuoteNo(),data.getSectionId(),data.getRiskId());
			chassisNo=data.getChassisNumber();
			if(req.getProductId().equalsIgnoreCase("5")){
				MotorDataDetails vehInfo = motorRepo.findByQuoteNoAndVehicleId(req.getQuoteNo(), data.getRiskId().toString());
				if(vehInfo!=null) {
					SearchROPVehicleRes res =new SearchROPVehicleRes();
					res.setResRegNumber(vehInfo.getRegistrationNumber());
					res.setResChassisNumber(vehInfo.getChassisNumber());
					res.setResEngineNumber(vehInfo.getEngineNumber());
					res.setResMake(vehInfo.getVehicleMakeDesc());
					res.setResModel(vehInfo.getVehcileModelDesc());
					res.setResColor(vehInfo.getColorDesc());
					res.setResBodyType(vehInfo.getVehicleTypeDesc());
					res.setResYearOfManufacture(vehInfo.getManufactureYear());
					
					res.setRiskId(data.getRiskId());
					res.setEngineCapacity(vehInfo.getCubicCapacity().toString());
					res.setFuelType(vehInfo.getFuelType());
					res.setGrossWeight(vehInfo.getGrossWeight());
					res.setMotorCategory(data.getMotorCategoryDesc()); 	
					res.setMotorDesc(data.getSectionName());
					res.setSeatingCapacity(new BigDecimal(vehInfo.getSeatingCapacity()).intValue());
					res.setTareWeight(vehInfo.getTareWeight());
					res.setVehicleUsage(vehInfo.getMotorUsageDesc());
					res.setPolicyType(data.getPolicyTypeDesc());
					res.setSumInsured(data.getSumInsured());
					res.setStickerNo(ss!=null && StringUtils.isBlank(ss.getStickerNumber())? "":ss.getStickerNumber());
					res.setCovernoterefno(ss!=null && StringUtils.isBlank(ss.getCoverNoteReferenceNo()) ? "":ss.getCoverNoteReferenceNo());
					res.setResponseStatusCode(ss!=null && StringUtils.isBlank(ss.getResponseStatusCode())? "":ss.getResponseStatusCode());
					res.setResponseStatusDesc(ss!=null && StringUtils.isBlank(ss.getResponseStatusDesc())? "":ss.getResponseStatusDesc());
					resList.add(res);
				}else {
					SearchROPVehicleRes res =new SearchROPVehicleRes();
					res.setResRegNumber(data.getRegistrationNumber());
					res.setResChassisNumber(data.getChassisNumber());
					res.setResEngineNumber(data.getEngineNumber());
					res.setResMake(data.getVehicleMakeDesc());
					res.setResModel(data.getVehcileModelDesc());
					res.setResColor(data.getColorDesc());
					res.setResBodyType(data.getTiraBodyType());
					res.setResYearOfManufacture(data.getManufactureYear()==null?0:Integer.valueOf(data.getManufactureYear()));
					
					res.setRiskId(data.getRiskId());
					res.setEngineCapacity(data.getCubicCapacity()==null?"0":data.getCubicCapacity().toString());
					res.setFuelType(data.getFuelTypeDesc());
					res.setGrossWeight(data.getGrossWeight()==null?0.0:Double.valueOf(data.getGrossWeight().toString()));
					res.setMotorCategory(data.getMotorCategoryDesc()); 	
					res.setMotorDesc(data.getSectionName());
					res.setSeatingCapacity(data.getSeatingCapacity());
					res.setTareWeight(data.getTareWeight()==null?0.0: Double.valueOf(data.getTareWeight().toString()));
					res.setVehicleUsage(data.getMotorUsageDesc());	
					res.setPolicyType(data.getPolicyTypeDesc());
					res.setSumInsured(data.getSumInsured());
					res.setStickerNo(ss!=null && StringUtils.isBlank(ss.getStickerNumber())? "":ss.getStickerNumber());
					res.setCovernoterefno(ss!=null && StringUtils.isBlank(ss.getCoverNoteReferenceNo()) ? "":ss.getCoverNoteReferenceNo());
					res.setResponseStatusCode(ss!=null && StringUtils.isBlank(ss.getResponseStatusCode())? "":ss.getResponseStatusCode());
					res.setResponseStatusDesc(ss!=null && StringUtils.isBlank(ss.getResponseStatusDesc())? "":ss.getResponseStatusDesc());
					
					resList.add(res);
				}
			} else {
				SearchROPVehicleRes res =new SearchROPVehicleRes();
				res.setResRegNumber(data.getRegistrationNumber());
				res.setResChassisNumber(data.getChassisNumber());
				res.setResEngineNumber(data.getEngineNumber());
				res.setResMake(data.getVehicleMakeDesc());
				res.setResModel(data.getVehcileModelDesc());
				res.setResColor(data.getColorDesc());
				res.setResBodyType(data.getTiraBodyType());
				res.setResYearOfManufacture(data.getManufactureYear()==null?0:Integer.valueOf(data.getManufactureYear()));
				
				res.setRiskId(data.getRiskId());
				res.setEngineCapacity(data.getCubicCapacity()==null?"0":data.getCubicCapacity().toString());
				res.setFuelType(data.getFuelTypeDesc());
				res.setGrossWeight(data.getGrossWeight()==null?0.0:Double.valueOf(data.getGrossWeight().toString()));
				res.setMotorCategory(data.getMotorCategoryDesc()); 	
				res.setMotorDesc(data.getSectionName());
				res.setSeatingCapacity(data.getSeatingCapacity());
				res.setTareWeight(data.getTareWeight()==null?0.0: Double.valueOf(data.getTareWeight().toString()));
				res.setVehicleUsage(data.getMotorUsageDesc());	
				res.setPolicyType(data.getPolicyTypeDesc());
				res.setSumInsured(data.getSumInsured());
				res.setStickerNo(ss!=null ?   StringUtils.isBlank(ss.getStickerNumber())? "" :ss.getStickerNumber() : null);
				res.setCovernoterefno(ss!=null  ?  StringUtils.isBlank(ss.getCoverNoteReferenceNo()) ? "":ss.getCoverNoteReferenceNo() : null);
				res.setResponseStatusCode(ss!=null ? StringUtils.isBlank(ss.getResponseStatusCode())? "":ss.getResponseStatusCode(): null );
				res.setResponseStatusDesc(ss!=null ?  StringUtils.isBlank(ss.getResponseStatusDesc())? "":ss.getResponseStatusDesc() : null);
				
				resList.add(res);
			}
		}
		viewRes.setVehDetails(resList);		
		} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return viewRes;
	}


//Payment Info
	@Override
	public List<SearchPaymentInfoRes> viewPaymentInfo(SearchReq req) {
		SearchPaymentInfoRes paymentgetres = new SearchPaymentInfoRes();
		List<SearchPaymentInfoRes> paylist = new ArrayList<SearchPaymentInfoRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		try {
			List<PaymentInfo> paymentinfo = null;
			List<PaymentDetail> pay = null;
			List<PaymentDetail> patmentDetails = null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {

				paymentinfo = paymentrepo.findByQuoteNoAndProductId(req.getQuoteNo(),
						Integer.valueOf(req.getProductId()));
				if (paymentinfo != null && paymentinfo.size() > 0) {
					String paymentId = paymentinfo.get(0).getPaymentId();

					patmentDetails = paymentRepo.findByQuoteNo(req.getQuoteNo());
//					pay = patmentDetails.stream().filter(o -> o.getPaymentId().equals(paymentId))
//							.collect(Collectors.toList());

					for (PaymentDetail pi : patmentDetails) {

						paymentgetres = new DozerBeanMapper().map(pi, SearchPaymentInfoRes.class);
						paymentgetres.setEntryDate(formatter.format(pi.getEntryDate()));
						paymentgetres.setUpdatedDate(formatter.format(pi.getUpdatedDate()));

						paymentgetres.setInstallmentMonth(pi.getInstallmentMonth()==null?null:pi.getInstallmentMonth());
						paymentgetres.setInstallmentPeriod(pi.getInstallmentPeriod()==null?null:pi.getInstallmentPeriod());
						
						PaymentCashRes res1 = new PaymentCashRes();

						// Cash
						if ("1".equalsIgnoreCase(pi.getPaymentType())) {
							res1.setPayeeName(pi.getPayeeName());
							paymentgetres.setPaymentCashRes(res1);
						}

						// Cheque
						PaymentChequeRes res2 = new PaymentChequeRes();
						if ("2".equalsIgnoreCase(pi.getPaymentType())) {
							res2.setBankName(pi.getBankName() == null ? null : pi.getBankName());
							res2.setChequeDate(pi.getChequeDate() == null ? null : pi.getChequeDate());
							res2.setChequeNo(pi.getChequeNo() == null ? null : pi.getChequeNo());
							res2.setMicrNo(pi.getMicrNo() == null ? null : pi.getMicrNo());
							paymentgetres.setPaymentChequeRes(res2);
						}

						// Credit
						PaymentCreditRes res3 = new PaymentCreditRes();
						if ("3".equalsIgnoreCase(pi.getPaymentType())) {
							res3.setCbcNo(pi.getCbcNo() == null ? null : pi.getCbcNo());
							paymentgetres.setPaymentCreditRes(res3);
						}

						// Online
						PaymentOnlineRes res4 = new PaymentOnlineRes();
						if ("4".equalsIgnoreCase(pi.getPaymentType())) {
							res4.setAuthAmount(pi.getAuthAmount() == null ? null : pi.getAuthAmount());
							res4.setAuthResponse(pi.getAuthResponse() == null ? null : pi.getAuthResponse());
							res4.setAuthTransRefNo(pi.getAuthTransRefNo() == null ? null : pi.getAuthTransRefNo());
							res4.setChannel(pi.getChannel() == null ? null : pi.getChannel());
							res4.setMsisdn(pi.getMsisdn() == null ? null : pi.getMsisdn());
							res4.setReference(pi.getReference() == null ? null : pi.getReference());
							res4.setRequestTime(pi.getRequestTime() == null ? null : pi.getRequestTime());
							res4.setResponseMessage(pi.getResponseMessage() == null ? null : pi.getResponseMessage());
							res4.setResponseStatus(pi.getResponseStatus() == null ? null : pi.getResponseStatus());
							res4.setResponseTime(pi.getResponseTime() == null ? null : pi.getResponseTime());
							paymentgetres.setPaymentOnlineRes(res4);
						}

						paylist.add(paymentgetres);

					}
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}

		return paylist;

	}



	@Override
	public DocumentDetailsRes viewDocumentDetails(SearchReq req) {
		DocumentDetailsRes res = new DocumentDetailsRes();
		List<DocumentRes> commonList = new ArrayList<DocumentRes>();
		List<DocumentRes> indiList = new ArrayList<DocumentRes>();
		try {

			List<DocumentTransactionDetails> getList = null;
			
			if (StringUtils.isNotBlank(req.getQuoteNo())) {

				getList = coverdocumentuploaddetailsrepository.findByQuoteNo(req.getQuoteNo());
			}else if(StringUtils.isNotBlank(req.getRequestReferenceNo())){
				getList = coverdocumentuploaddetailsrepository.findByRequestReferenceNo(req.getRequestReferenceNo());
			}

			List<Tuple> list1 = new ArrayList<Tuple>();
			if(getList.size()>0) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query1 = cb.createQuery(Tuple.class);

				Root<DocumentTransactionDetails> td = query1.from(DocumentTransactionDetails.class);
				Root<DocumentUniqueDetails> ud = query1.from(DocumentUniqueDetails.class);

				query1.multiselect(ud.alias("DocumentUniqueDetails"),td.alias("DocumentTransactionDetails")); 
				
				Predicate m1 = cb.equal(td.get("quoteNo"), req.getQuoteNo());
				Predicate m2 = cb.equal(ud.get("uniqueId"), td.get("uniqueId"));
			
				query1.where(m1, m2);

				TypedQuery<Tuple> result1 = em.createQuery(query1);
				list1 = result1.getResultList();
				 
				 for (Tuple cd : list1) {
					 	DocumentUniqueDetails unique = (DocumentUniqueDetails) cd.get("DocumentUniqueDetails");
					 	DocumentTransactionDetails trans = (DocumentTransactionDetails) cd.get("DocumentTransactionDetails");
					 	
					 	if(unique.getDocumentType().equals("1")) { //common
					 		
					 		DocumentRes common = new DocumentRes();
					 		common = new DozerBeanMapper().map(unique, DocumentRes.class);
					 		
					 		common.setSectionId(trans.getSectionId());
					 		common.setSectionName(trans.getSectionName());
					 		common.setLocationId(trans.getLocationId());
					 		common.setLocationName(trans.getLocationName());
					 		commonList.add(common)	;
					 		
					 	} else if (unique.getDocumentType().equals("2")) { //individual
					 		DocumentRes indi = new DocumentRes();
					 		indi = new DozerBeanMapper().map(unique, DocumentRes.class);
					 		
					 		indi.setSectionId(trans.getSectionId());
					 		indi.setSectionName(trans.getSectionName());
					 		indi.setLocationId(trans.getLocationId());
					 		indi.setLocationName(trans.getLocationName());
					 		indiList.add(indi)	;
					 		
					 	}
					 	
					}
				 res.setCommonDocumentRes(commonList);
				 res.setIndividualDocumentRes(indiList);
				 

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();
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
			product = list.size() > 0 ? list.get(0) :null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}

	@Override
	public AccessoriesSumInsureDropDownRes getAccessoriesSuminsuredByQuoteNo(SearchReq req) {
		// TODO Auto-generated method stub
		
		List<AccessoriesRes> resList=new ArrayList<AccessoriesRes>();
		AccessoriesSumInsureDropDownRes totalList=new AccessoriesSumInsureDropDownRes();
		Double totalSumInsure=0.0;

		try {
		List<MotorDataDetails> quoteDetails =motorRepo.findByQuoteNo(req.getQuoteNo());
		
		for(MotorDataDetails  data : quoteDetails)
		{
          if(data.getAcccessoriesSumInsured()!=null && data.getAcccessoriesSumInsured()>0)
          {
			AccessoriesRes motor=new AccessoriesRes();			
			motor.setCode(data.getVehicleId());
			motor.setCodeDesc(data.getChassisNumber());	
			motor.setAccessoriesSumInsured(data.getAcccessoriesSumInsured());
			totalSumInsure = totalSumInsure + data.getAcccessoriesSumInsured();			
			resList.add(motor);
          }			
	  }
		totalList.setTotalAccessoriesSumInsured(totalSumInsure);
		totalList.setAccessoriesRes(resList);	
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return totalList;
}

	@Override
	public ViewQuoteDetailsRes viewQuoteDetails(ViewQuoteDetailsReq req) {
		ViewQuoteDetailsRes res = new ViewQuoteDetailsRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		
		try {
			CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId(), req.getProductId().toString());

			
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				
				List<HomePositionMaster> homeData = homeRepo.findByQuoteNoAndProductId(req.getQuoteNo(),Integer.valueOf(req.getProductId()));
				if(homeData.size()>0) {
					HomePositionMaster home = homeData.get(0);
					mapper.map(home, res);
					res.setEndorsementYn(home.getEndtTypeId()==null?"N":"Y");
					res.setStrickerNo(home.getStickerNumber()==null?"":	home.getStickerNumber());
					res.setCovernoteNo(home.getTiraCoverNoteNo()==null?"":	home.getTiraCoverNoteNo().toString());
					res.setPaymentMode(home.getPaymentType()==null?"":home.getPaymentType());
					res.setPaymentStatus(home.getPaymentStatus()==null?"":home.getPaymentStatus());
					res.setPremiaIntegrationStatus(home.getIntegrationStatus()==null?"": home.getIntegrationStatus());	
					res.setTirraIntegrationStatus(home.getResponseStatusDesc()==null?"": home.getResponseStatusDesc());
					if(StringUtils.isBlank(home.getAdminReferralStatus()))
							res.setAdminReferralStatus("N");
					else if (home.getAdminReferralStatus().equalsIgnoreCase("RP"))
						res.setAdminReferralStatus("Pending");
					else if (home.getAdminReferralStatus().equalsIgnoreCase("RA"))
						res.setAdminReferralStatus("Approved");
					else if (home.getAdminReferralStatus().equalsIgnoreCase("RR"))
						res.setAdminReferralStatus("Rejected");
					else if (home.getAdminReferralStatus().equalsIgnoreCase("RE"))
						res.setAdminReferralStatus("Re-quote");
					else if (home.getAdminReferralStatus().equalsIgnoreCase("REV"))
						res.setAdminReferralStatus("Reverted");
					
					
					String customerId=homeData.get(0).getCustomerId();
					
					if (product.getMotorYn().equalsIgnoreCase("M")) { 	
						
						List<EserviceMotorDetails> motor = repo.findByCustomerId(customerId);
						if (motor.size() > 0) {
							res.setLoginid(motor.get(0).getLoginId()==null?"": motor.get(0).getLoginId());
							res.setApplicationid(motor.get(0).getApplicationId()==null?"":motor.get(0).getApplicationId());
							res.setSourcetype(motor.get(0).getSourceType()==null?"":motor.get(0).getSourceType());
						}
						
					} else if (product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
					
						List<EserviceTravelDetails> motor = eTravelrepo.findByCustomerId(customerId);
						if (motor.size() > 0) {
							res.setLoginid(motor.get(0).getLoginId()==null?"": motor.get(0).getLoginId());
							res.setApplicationid(motor.get(0).getApplicationId()==null?"":motor.get(0).getApplicationId());
							res.setSourcetype(motor.get(0).getSourceType()==null?"":motor.get(0).getSourceType());
					
									
						}
					
					} else if (product.getMotorYn().equalsIgnoreCase("A")) {

						List<EserviceBuildingDetails> motor = eBuildingRepo.findByCustomerId(customerId);
						if (motor.size() > 0) {
							res.setLoginid(motor.get(0).getLoginId()==null?"": motor.get(0).getLoginId());
							res.setApplicationid(motor.get(0).getApplicationId()==null?"":motor.get(0).getApplicationId());
							res.setSourcetype(motor.get(0).getSourceType()==null?"":motor.get(0).getSourceType());
						
						}
						
					} else {

						List<EserviceCommonDetails> motor = eCommRepo.findByCustomerId(customerId);
						if (motor.size() > 0) {
							res.setLoginid(motor.get(0).getLoginId()==null?"": motor.get(0).getLoginId());
							res.setApplicationid(motor.get(0).getApplicationId()==null?"":motor.get(0).getApplicationId());
							res.setSourcetype(motor.get(0).getSourceType()==null?"":motor.get(0).getSourceType());
						
						}
						
					}
					
				}
			}else if(StringUtils.isNotBlank(req.getRequestReferenceNo())){ 	//ReqreferenceNo
				
				if (product.getMotorYn().equalsIgnoreCase("M")) { 	//get from raw tables
					res = motService.viewQuoteMotor(req);
				} else if (product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
					res = travelSearch.viewQuoteTravel(req);
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
					res = buiService.viewQuoteBuilding(req);
				} else {
					res = commonSearch.viewQuoteCommon(req);
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	
}
