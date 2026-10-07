package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PremiaCustomerDetails;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.common.service.BuildingSearchService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PremiaCustomerDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.EserviceBuildingsDetailsRes;
import com.maan.eway.res.SectionDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;


@Service
@Transactional
public class BuildingSearchServiceImpl implements BuildingSearchService {
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(BuildingSearchServiceImpl.class);
	@Autowired
	private EserviceCustomerDetailsRepository cusrepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository repo;

	@Autowired
	private PremiaCustomerDetailsRepository premiaRepo;

	@Autowired
	private PersonalInfoRepository perRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo  ;
	
	@Autowired
	private BuildingRiskDetailsRepository buildRiskRepo  ;
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo ;
	@Override
	public List<Tuple> searchBuilding(SearchReq req, List<String> branches) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Tuple> searchQuote = new ArrayList<Tuple>();
		try {
			// Search
			String searchKey = req.getSearchKey();
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();
			String loginId = req.getLoginId();
			String userType = req.getUserType();
			String productId=req.getProductId();
			if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerName".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("MobileNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("PolicyNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return searchQuote;
	}

	@Override
	public List<Tuple> searchBuildingDetails(SearchReq req,String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) {
		// TODO Auto-generated method stub
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceBuildingDetails> c = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
//			query.multiselect(c.alias("c"),
//					cus.get("clientName").alias("clientName"),
//					cb.count(c).alias("idsCount"),cus.get("mobileNo1").alias("mobileNumber"));

			query.multiselect(cb.max(cus.get("clientName")).alias("clientName"), cb.count(c).alias("idsCount"),

					cus.get("customerReferenceNo").alias("customerReferenceNo"),
					cus.get("mobileNo1").alias("mobileNumber"),
					cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"),
					cb.max(c.get("riskId")).alias("riskId"), 
				//	cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
					cb.max(c.get("productId")).alias("productId"),
					cb.max(c.get("companyId")).alias("companyId"),
					cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
					.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),

					cb.selectCase().when(cb.max(c.get("customerId")).isNotNull(), cb.max(c.get("customerId")))
					.otherwise(cb.max(c.get("customerId"))).alias("customerId"),
					cb.max(c.get("policyNo")).alias("policyNo"),
					cb.max(c.get("branchCode")).alias("branchCode"), 
					cb.max(c.get("inbuildConstructType")).alias("inbuildConstructType"),
					cb.max(c.get("buildingFloors")).alias("buildingFloors"),
					cb.max(c.get("outbuildConstructType")).alias("outbuildConstructType"),

					cb.max(c.get("buildingUsageYn")).alias("buildingUsageYn"),
					//cb.max(c.get("buildingPurpose")).alias("buildingPurpose"),
					//cb.max(c.get("buildingPurposeId")).alias("buildingPurposeId"),
					cb.max(c.get("buildingUsageId")).alias("buildingUsageId"), 
					cb.max(c.get("buildingUsageDesc")).alias("buildingUsageDesc"),
					cb.max(c.get("buildingType")).alias("buildingType"),
					cb.max(c.get("buildingOwnerYn")).alias("buildingOwnerYn"),
					cb.max(c.get("buildingOccupationType")).alias("buildingOccupationType"),
					cb.max(c.get("apartmentOrBorder")).alias("apartmentOrBorder"),
					cb.max(c.get("withoutInhabitantDays")).alias("withoutInhabitantDays"),
					cb.max(c.get("buildingCondition")).alias("buildingCondition"),
					cb.max(c.get("buildingBuildYear")).alias("buildingBuildYear"),
					cb.max(c.get("buildingAge")).alias("buildingAge"),
					cb.max(c.get("buildingAreaSqm")).alias("buildingAreaSqm"),
					cb.max(c.get("buildingSuminsured")).alias("buildingSuminsured"),
					cb.max(c.get("allriskSuminsured")).alias("allriskSuminsured"), 
					//cb.max(c.get("paDeathSuminsured")).alias("paDeathSuminsured"),
					//cb.max(c.get("paPermanentdisablementSuminsured")).alias("paPermanentdisablementSuminsured"),
					//cb.max(c.get("paTotaldisabilitySumInsured")).alias("paTotaldisabilitySumInsured"), 
					//cb.max(c.get("PaMedicalSuminsured")).alias("PaMedicalSuminsured"),
					//cb.max(c.get("personalIntSuminsured")).alias("personalIntSuminsured"),
					cb.max(c.get("contentSuminsured")).alias("contentSuminsured"), 
					//cb.max(c.get("workmenCompSuminsured")).alias("workmenCompSuminsured"),
					cb.max(c.get("entryDate")).alias("entryDate"),
					cb.max(c.get("createdBy")).alias("createdBy"),
					cb.max(c.get("status")).alias("status"),
					cb.max(c.get("updatedDate")).alias("updatedDate"),
					cb.max(c.get("updatedBy")).alias("updatedBy"),

					cb.max(c.get("acExecutiveId")).alias("acExecutiveId"),
					cb.max(c.get("applicationId")).alias("applicationId"),
					cb.max(c.get("brokerCode")).alias("brokerCode"),
					cb.max(c.get("subUserType")).alias("subUserType"),
					cb.max(c.get("loginId")).alias("loginId"),
					cb.max(c.get("agencyCode")).alias("agencyCode"),
					cb.max(c.get("policyStartDate")).alias("policyStartDate"),
					cb.max(c.get("policyEndDate")).alias("policyEndDate"),
					cb.max(c.get("policyPeriord")).alias("policyPeriord"),
					cb.max(c.get("currency")).alias("currency"),
					cb.max(c.get("exchangeRate")).alias("exchangeRate"),
					cb.max(c.get("adminLoginId")).alias("adminLoginId"),
					cb.max(c.get("adminRemarks")).alias("adminRemarks"),
					cb.max(c.get("rejectReason")).alias("rejectReason"),
					cb.max(c.get("referalRemarks")).alias("referalRemarks"),
					cb.max(c.get("productDesc")).alias("productDesc"),
					cb.max(c.get("sectionId")).alias("sectionId"),
					cb.max(c.get("sectionDesc")).alias("sectionDesc"),
					cb.max(c.get("branchName")).alias("branchName"),
					cb.max(c.get("companyName")).alias("companyName"),
					cb.max(c.get("oldReqRefNo")).alias("oldReqRefNo"),
					cb.max(c.get("actualPremiumFc")).alias("actualPremiumFc"),
					cb.max(c.get("actualPremiumLc")).alias("actualPremiumLc"),
					cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.max(c.get("overallPremiumFc")).alias("overallPremiumFc"),
					cb.max(c.get("brokerBranchCode")).alias("brokerBranchCode"),
					cb.max(c.get("brokerBranchName")).alias("brokerBranchName"),
					cb.max(c.get("insuranceType")).alias("insuranceType"),
					cb.max(c.get("commissionType")).alias("commissionType"),
					cb.max(c.get("commissionTypeDesc")).alias("commissionTypeDesc"),
					cb.max(c.get("havepromocode")).alias("havepromocode"),
					cb.max(c.get("promocode")).alias("promocode"),
					cb.max(c.get("occupationType")).alias("occupationType"),
					cb.max(c.get("occupationTypeDesc")).alias("occupationTypeDesc"),
					cb.max(c.get("domesticPackageYn")).alias("domesticPackageYn"), 
					cb.max(c.get("categoryId")).alias("categoryId"),
					cb.max(c.get("categoryDesc")).alias("categoryDesc"),
					cb.max(c.get("bankCode")).alias("bankCode"),
					cb.max(c.get("sourceType")).alias("sourceType"),
					cb.max(c.get("customerCode")).alias("customerCode"),

					cb.max(c.get("bdmCode")).alias("bdmCode"),
					cb.max(c.get("manualReferalYn")).alias("manualReferalYn"),
					cb.max(c.get("elecEquipSuminsured")).alias("elecEquipSuminsured"),
					//cb.max(c.get("moneySinglecarrySuminsured")).alias("moneySinglecarrySuminsured"),
					//cb.max(c.get("moneyAnnualcarrySuminsured")).alias("moneyAnnualcarrySuminsured"),
					////cb.max(c.get("moneyInsafeSuminsured")).alias("moneyInsafeSuminsured"),
					//cb.max(c.get("fidelityAnyoccuSuminsured")).alias("fidelityAnyoccuSuminsured"),
					//cb.max(c.get("fidelityAnnualSuminsured")).alias("fidelityAnnualSuminsured"),
					//cb.max(c.get("tpliabilityAnyoccuSuminsured")).alias("tpliabilityAnyoccuSuminsured"),
					//cb.max(c.get("empliabilityAnnualSuminsured")).alias("empliabilityAnnualSuminsured"),
					//cb.max(c.get("empliabilityExcessSuminsured")).alias("empliabilityExcessSuminsured"),
					cb.max(c.get("goodsSinglecarrySuminsured")).alias("goodsSinglecarrySuminsured"),
					cb.max(c.get("goodsTurnoverSuminsured")).alias("goodsTurnoverSuminsured"), 
					cb.max(c.get("industryId")).alias("industryId"),
					cb.max(c.get("industryDesc")).alias("industryDesc"),
					cb.max(c.get("endorsementType")).alias("endorsementType"),
					cb.max(c.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
					cb.max(c.get("endorsementDate")).alias("endorsementDate"),

					cb.max(c.get("endorsementRemarks")).alias("endorsementRemarks"),
				
					cb.max(c.get("endorsementEffdate")).alias("endorsementEffdate"),
					cb.max(c.get("originalPolicyNo")).alias("originalPolicyNo"),
					cb.max(c.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
					cb.max(c.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
					cb.max(c.get("endtCount")).alias("endtCount"),
					cb.max(c.get("endtStatus")).alias("endtStatus"),
					cb.max(c.get("isFinaceYn")).alias("isFinaceYn"),
					cb.max(c.get("endtCategDesc")).alias("endtCategDesc"),
					cb.max(c.get("endtPremium")).alias("endtPremium"),
					//cb.max(c.get("liabilityOccupationId")).alias("liabilityOccupationId"), 
					//cb.max(c.get("liabilityOccupationDesc")).alias("liabilityOccupationDesc"),
					cb.max(c.get("wallType")).alias("wallType"),
					cb.max(c.get("wallTypeDesc")).alias("wallTypeDesc"),
					cb.max(c.get("roofType")).alias("roofType"),
					cb.max(c.get("roofTypeDesc")).alias("roofTypeDesc"),
					cb.max(c.get("natureOfTradeId")).alias("natureOfTradeId"),
					cb.max(c.get("natureOfTradeDesc")).alias("natureOfTradeDesc"),
					cb.max(c.get("insuranceForId")).alias("insuranceForId"),
					cb.max(c.get("insuranceForDesc")).alias("insuranceForDesc"),
					cb.max(c.get("internalWallType")).alias("internalWallType"),
					cb.max(c.get("internalWallDesc")).alias("internalWallDesc"),

					cb.max(c.get("ceilingType")).alias("ceilingType"),
					cb.max(c.get("ceilingTypeDesc")).alias("ceilingTypeDesc"),
					cb.max(c.get("stockInTradeSi")).alias("stockInTradeSi"),
					cb.max(c.get("goodsSi")).alias("goodsSi"),
					cb.max(c.get("furnitureSi")).alias("furnitureSi"),
					cb.max(c.get("applianceSi")).alias("applianceSi"),
					cb.max(c.get("cashValueablesSi")).alias("cashValueablesSi"),
					cb.max(c.get("stockLossPercent")).alias("stockLossPercent"),
					cb.max(c.get("goodsLossPercent")).alias("goodsLossPercent"),

					cb.max(c.get("furnitureLossPercent")).alias("furnitureLossPercent"),
					cb.max(c.get("applianceLossPercent")).alias("applianceLossPercent"),
					cb.max(c.get("cashValueablesLossPercent")).alias("cashValueablesLossPercent"),
					cb.max(c.get("address")).alias("address"),
					cb.max(c.get("regionCode")).alias("regionCode"),
					cb.max(c.get("regionDesc")).alias("regionDesc"),
					cb.max(c.get("districtCode")).alias("districtCode"),
					cb.max(c.get("districtDesc")).alias("districtDesc"),
					cb.max(c.get("occupiedYear")).alias("occupiedYear"),
					cb.max(c.get("showWindow")).alias("showWindow"),

					cb.max(c.get("frontDoors")).alias("frontDoors"),
					cb.max(c.get("backDoors")).alias("backDoors"),
					cb.max(c.get("windowsMaterialId")).alias("windowsMaterialId"),
					cb.max(c.get("windowsMaterialDesc")).alias("windowsMaterialDesc"),
					cb.max(c.get("doorsMaterialId")).alias("doorsMaterialId"),
					cb.max(c.get("doorsMaterialDesc")).alias("doorsMaterialDesc"),
					cb.max(c.get("nightLeftDoor")).alias("nightLeftDoor"),
					cb.max(c.get("nightLeftDoorDesc")).alias("nightLeftDoorDesc"),
					cb.max(c.get("buildingOccupied")).alias("buildingOccupied"),
					cb.max(c.get("buildingOccupiedDesc")).alias("buildingOccupiedDesc"),
					cb.max(c.get("watchmanGuardHours")).alias("watchmanGuardHours"),
					cb.max(c.get("accessibleWindows")).alias("accessibleWindows"),
					cb.max(c.get("trapDoors")).alias("trapDoors"),
					//cb.max(c.get("cashInHandDirectors")).alias("cashInHandDirectors"),
					//cb.max(c.get("cashInTransit")).alias("cashInTransit"),
					//cb.max(c.get("cashInHandEmployees")).alias("cashInHandEmployees"),
					//cb.max(c.get("cashInSafe")).alias("cashInSafe"),
					//cb.max(c.get("cashInPremises")).alias("cashInPremises"),
					cb.max(c.get("revenueFromStamps")).alias("revenueFromStamps"),
					//cb.max(c.get("moneyInSafeBusiness")).alias("moneyInSafeBusiness"),
					//cb.max(c.get("moneyOutSafeBusiness")).alias("moneyOutSafeBusiness"),
					//cb.max(c.get("moneyInPremises")).alias("moneyInPremises"),
					//cb.max(c.get("moneyInLocker")).alias("moneyInLocker"),
					cb.max(c.get("machineEquipSi")).alias("machineEquipSi"),
					cb.max(c.get("plateGlassSi")).alias("plateGlassSi"),
					cb.max(c.get("firstLossPercentId")).alias("firstLossPercentId"),
					cb.max(c.get("firstLossPercent")).alias("firstLossPercent"),
					//cb.max(c.get("accDamageSi")).alias("accDamageSi"),
					//cb.max(c.get("burglarySi")).alias("burglarySi"),
					cb.max(c.get("powerPlantSi")).alias("powerPlantSi"),
					cb.max(c.get("elecMachinesSi")).alias("elecMachinesSi"),
					cb.max(c.get("equipmentSi")).alias("equipmentSi"),
					cb.max(c.get("generalMachineSi")).alias("generalMachineSi"),
					cb.max(c.get("manuUnitsSi")).alias("manuUnitsSi"),
					cb.max(c.get("boilerPlantsSi")).alias("boilerPlantsSi"),
					cb.max(c.get("tiraCoverNoteNo")).alias("tiraCoverNoteNo"),
					cb.max(c.get("indemityPeriod")).alias("indemityPeriod"),
					cb.max(c.get("indemityPeriodDesc")).alias("indemityPeriodDesc"),
					cb.max(c.get("makutiYn")).alias("makutiYn"),
					cb.max(c.get("plateGlassType")).alias("plateGlassType"),
					cb.max(c.get("plateGlassDesc")).alias("plateGlassDesc"),
					cb.max(c.get("emiYn")).alias("emiYn"),
					cb.max(c.get("installmentPeriod")).alias("installmentPeriod"),
					cb.max(c.get("noOfInstallment")).alias("noOfInstallment"),
					cb.max(c.get("emiPremium")).alias("emiPremium"));
			

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));

			Predicate n1 = null;
			Predicate n3 = null;
			Predicate n4 = null;
			Predicate n5 = null;
			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
			} else if (searchKey.equalsIgnoreCase("CustomerReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("customerReferenceNo")), searchValue);
			
			} else if (searchKey.equalsIgnoreCase("QuoteNumber")) {
				n1 = cb.equal(c.get("quoteNo"), searchValue);
				
			}
			 else if (searchKey.equalsIgnoreCase("MobileNumber")) {
					n1 = cb.equal(cb.lower(cus.get("mobileNo1")), searchValue);
			 }
			
			else if (searchKey.equalsIgnoreCase("CustomerName")) {
				n1 = cb.like(cb.lower(cus.get("clientName")), "%" + searchValue + "%");
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			}
			else if (searchKey.equalsIgnoreCase("PolicyNumber")) {
				n1 = cb.equal(cb.lower(c.get("policyNo")), searchValue);
			}

			Predicate n2 = cb.equal(c.get("companyId"), companyId);
			Predicate n6 = cb.equal(c.get("productId"), productId);

			

			if ("issuer".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("applicationId"), req.getApplicationId());
				Expression<String> e0 = c.get("branchCode");
				n4 = e0.in(branches);
			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("loginId"), loginId);
				Expression<String> e0 = c.get("brokerBranchCode");
				n4 = e0.in(branches);
			}
			if (searchKey.equalsIgnoreCase("CustomerName")) {
				if ("issuer".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("branchCode");
					n4 = e0.in(branches);
				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("brokerBranchCode");
					n4 = e0.in(branches);
				}
			}
			n5 = cb.equal(cus.get("customerReferenceNo"), c.get("customerReferenceNo"));
		//	Predicate n6 = cb.isNull(c.get("endtTypeId"));
			 if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
			query.where(n1,n2,n3,n4,n5,n6)
			.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),cus.get("mobileNo1"),
					c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
					c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
//					c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
			.orderBy(orderList);
			 }else {
				 query.where(n1,n2,n4,n5,n6)
					.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),cus.get("mobileNo1"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
//							c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
					.orderBy(orderList);
			 }
			if (searchKey.equalsIgnoreCase("CustomerName")) {
				query.where(n1, n2,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),cus.get("mobileNo1"),
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
//						c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
				.orderBy(orderList);
			}
			

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			customerDetailsList = result.getResultList();
			customerDetailsList = customerDetailsList.stream().filter(o -> !o.get("idsCount").equals(0L))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return customerDetailsList;
	}

	
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}	
	
	@Override
	public List<ListItemValue> searchDropdownBuilding(CopyQuoteDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		List<ListItemValue> list = new ArrayList<ListItemValue>();

		try {
            String itemType = "ADMIN_SEARCH_BUILDING";
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
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			query.where(n1, n2, n3, n8, n9, n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode())))
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue::getItemValue));
			
//			for (ListItemValue data : list) {
//				DropDownRes res = new DropDownRes();
//				res.setCode(data.getItemCode());
//				res.setCodeDesc(data.getItemValue());
//				res.setStatus(data.getStatus());
//				resList.add(res);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
		}
	
		// Risk details
		@Override
		public AdminViewQuoteRes getBuildingProductDetails(SearchReq req) {
			AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				// Find Motor Data

				List<BuildingRiskDetails> buildings = buildRiskRepo.findByQuoteNo(req.getQuoteNo());
				List<EserviceSectionDetails> secDatas = eserSecRepo.findByQuoteNo(req.getQuoteNo());

				// Building Details
				// Section Details

				List<EserviceBuildingsDetailsRes> buildList = new ArrayList<EserviceBuildingsDetailsRes>();
				EserviceBuildingsDetailsRes buildingRes = new EserviceBuildingsDetailsRes();
				
				// Broker Commission
				
				List<SectionDetails> buildingSectionList = new ArrayList<SectionDetails>();
				for (EserviceSectionDetails sec : secDatas) {
					if (sec.getProductType().equalsIgnoreCase("H")) {
						List<CommonDataDetails> accData = commonDataRepo.findByQuoteNoAndSectionIdOrderByRiskIdAsc(req.getQuoteNo(),sec.getSectionId());
						for (CommonDataDetails acc : accData) {
							SectionDetails buildSec = new SectionDetails();
							buildSec.setSectionId(acc.getSectionId() == null ? "" : acc.getSectionId().toString());
							buildSec.setSectionName(acc.getSectionDesc());
							buildingSectionList.add(buildSec);

						}

					} else {
						// Build
						SectionDetails buildSec = new SectionDetails();
						buildSec.setSectionId(sec.getSectionId() == null ? "" : sec.getSectionId().toString());
						buildingRes.setSectionId(StringUtils.isBlank(buildingRes.getSectionId())
								? sec.getSectionId() == null ? "" : sec.getSectionId().toString()
								: buildingRes.getSectionId());
						buildSec.setSectionName(sec.getSectionName());
						buildingSectionList.add(buildSec);

					}

				}
				
				// Default Entry
				List<BuildingRiskDetails> filterDefaultBuilding = buildings.stream().filter( o -> "0".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
				if(filterDefaultBuilding.size() > 0 ) {
					BuildingRiskDetails buildData = filterDefaultBuilding.get(0);
					dozerMapper.map(buildData, buildingRes);
					buildingRes.setDocumentsTitle(buildData.getProductDesc());

					
					List<BrokerCommissionDetails> policylist = getPolicyName(buildData.getCompanyId(),
							buildData.getProductId().toString(), buildData.getCreatedBy(), buildData.getAgencyCode(),
							"99999");
					Double commissionPercent = 0.0;
					if (policylist.size() > 0 && policylist != null) {
						commissionPercent = policylist.get(0).getCommissionPercentage().toString() == null ? 0
								: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());
					} else {
						commissionPercent = 5.0;
					}
					
					buildingRes.setDocumentsTitle(buildData.getProductDesc());
					String premiumFc = String.valueOf(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum()) ;
					 String vatPremiumFc =	String.valueOf(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum()) ;
					 BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent)).divide(BigDecimal.valueOf(100D));
						buildingRes.setCommissionAmount(commission.toString() == null ? "" : commission.toString());
						buildingRes.setCommissionPercentage(commissionPercent.toString() == null ? "" : commissionPercent.toString());

					 buildingRes.setOverAllPremiumFc(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum() );
					 buildingRes.setOverAllPremiumLc(buildings.stream().filter( o -> o.getOverallPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getOverallPremiumFc().toPlainString() ) ).sum() );
					 buildingRes.setPremiumFc(buildings.stream().filter( o -> o.getActualPremiumFc() != null ).mapToDouble(o -> Double.valueOf(o.getActualPremiumFc().toPlainString() ) ).sum() );
					 buildingRes.setPremiumLc(buildings.stream().filter( o -> o.getActualPremiumLc() != null ).mapToDouble(o -> Double.valueOf(o.getActualPremiumLc().toPlainString() ) ).sum() );
					 buildingRes.setCommissionAmount(commission==null?"":commission.toString());
					 buildingRes.setVatCommission(String.valueOf(buildings.stream().filter( o -> o.getVatCommission() != null ).mapToDouble(o -> Double.valueOf(o.getVatCommission().toPlainString() ) ).sum()) );	
					
					buildingRes.setDocumentsTitle(StringUtils.isNotBlank(buildData.getSectionDesc() ) ? buildData.getSectionDesc() :   buildData.getProductDesc());
					buildingRes.setLocationId(buildData.getRiskId().toString());
					buildingRes.setLocationName(StringUtils.isNotBlank(buildData.getSectionDesc() ) ? buildData.getSectionDesc() :   buildData.getProductDesc());
					buildingRes.setRiskId(buildData.getRiskId().toString());
//					buildingRes.setSuminsured(buildData.getBuildingSuminsured()==null?"" : buildData.getBuildingSuminsured().toPlainString());
					buildingRes.setSuminsured(buildData.getSumInsured()==null?"" : buildData.getSumInsured().toPlainString());
					buildingRes.setSectionId(StringUtils.isNotBlank(buildData.getSectionId() ) ?  buildData.getSectionId() :  "99999"  );
				
					List<EserviceSectionDetails>   buildSections = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(buildData.getRequestReferenceNo());
					List<String> sectionIds = buildSections.stream().filter( o -> o.getRiskId().equals(1)).map(EserviceSectionDetails :: getSectionId ).collect(Collectors.toList());
					buildingRes.setRiskId(buildData.getRiskId().toString());
					
					
					}
					
					// (i) Asset Related Sections
					
					// Building
					List<BuildingRiskDetails> filterBuilding = buildings.stream().filter( o -> "1".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterBuilding.size() > 0 ) {
						BuildingRiskDetails build = filterBuilding.get(0);
//						buildingRes.setBuildingSuminsured(build.getBuildingSuminsured() == null?"0" :build.getBuildingSuminsured().toPlainString());
//						buildingRes.setBuildingSuminsured(build.getBuildingSuminsured() == null?"0" :build.getBuildingSuminsured().toPlainString());
						buildingRes.setSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
					} 
					
					// Content
					List<BuildingRiskDetails> filterContent = buildings.stream().filter( o -> "47".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterContent.size() > 0 ) {
						BuildingRiskDetails build = filterContent.get(0);
//						buildingRes.setContentSuminsured(build.getContentSuminsured() == null?"0" :build.getContentSuminsured().toPlainString());
						buildingRes.setSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
						
					} 
					
					
					// All Risk , Plant All Risk , Business All Risk
					List<BuildingRiskDetails> filterAllRisk = buildings.stream().filter( o -> "3".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterAllRisk.size() > 0 ) {
						BuildingRiskDetails build = filterAllRisk.get(0);
//						buildingRes.setAllriskSuminsured(build.getAllriskSuminsured() == null?"0" :build.getAllriskSuminsured().toPlainString());
//						buildingRes.setMiningPlantSi(build.getMiningPlantSi()== null?"0" :build.getMiningPlantSi().toPlainString());
//						buildingRes.setNonminingPlantSi(build.getNonminingPlantSi() == null?"0" :build.getNonminingPlantSi().toPlainString());
//						buildingRes.setGensetsSi(build.getGensetsSi() == null?"0" :build.getGensetsSi().toPlainString());
//						buildingRes.setEquipmentSi(build.getEquipmentSi() == null?BigDecimal.ZERO :build.getEquipmentSi());
					//	Double MiningPlantSi = build.getMiningPlantSi() == null?0D :Double.valueOf(build.getMiningPlantSi().toPlainString()) ;
					//	Double NonminingPlantSi = build.getNonminingPlantSi() == null?0D :Double.valueOf(build.getNonminingPlantSi().toPlainString()) ;
					//	Double GensetsSi = build.getGensetsSi() == null?0D :Double.valueOf(build.getGensetsSi().toPlainString()) ;
					//	Double plantAllRiskSi = MiningPlantSi +NonminingPlantSi  + GensetsSi ;
						//res.setPlantAllriskSi( plantAllRiskSi==null ? "" :plantAllRiskSi.toString());
						buildingRes.setSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
					} 
					
					// Accidental Damage
					List<BuildingRiskDetails> filterAccidental = buildings.stream().filter( o -> "56".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterAccidental.size() > 0 ) {
						BuildingRiskDetails build = filterAccidental.get(0);
						//res.setContentSuminsured(build.getContentSuminsured() == null?"0" :build.getContentSuminsured().toPlainString());
						
					}
					
					// Burgalry
					List<BuildingRiskDetails> filterBurglary = buildings.stream().filter( o -> "52".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterBurglary.size() > 0 ) {
						BuildingRiskDetails build = filterBurglary.get(0);
					//	buildingRes.setStockInTradeSi(build.getStockInTradeSi()== null?"0" :build.getStockInTradeSi().toPlainString());
					//	buildingRes.setGoodsSi(build.getGoodsSi() == null?"0" :build.getGoodsSi().toPlainString());	
					//	buildingRes.setFurnitureSi(build.getFurnitureSi() == null?"0" :build.getFurnitureSi().toPlainString());
					//	buildingRes.setCashValueablesSi(build.getCashValueablesSi() == null?"0" :build.getCashValueablesSi().toPlainString());
					//	buildingRes.setApplianceSi(build.getApplianceSi() == null?"0" :build.getApplianceSi().toPlainString());
					//	res.setGoodsSinglecarrySuminsured(build.getGoodsSinglecarrySuminsured() == null?"0" :build.getGoodsSinglecarrySuminsured().toPlainString());
					//	res.setGoodsTurnoverSuminsured(build.getGoodsTurnoverSuminsured() == null?"0" :build.getGoodsTurnoverSuminsured().toPlainString());
						 buildingRes.setInsuranceForId(build.getInsuranceForId()!=null ? Arrays.asList(build.getInsuranceForId().split(",")) : null )  ;
					//	 buildingRes.setStockLossPercent(build.getStockLossPercent()==null ? null : build.getStockLossPercent().toString());
					//	 buildingRes.setGoodsLossPercent(build.getGoodsLossPercent()==null ? null : build.getGoodsLossPercent().toString());
					//	 buildingRes.setFurnitureLossPercent(build.getFurnitureLossPercent()==null ? null : build.getFurnitureLossPercent().toString());
					//	 buildingRes.setApplianceLossPercent(build.getApplianceLossPercent()==null ? null : build.getApplianceLossPercent().toString());
					//	 buildingRes.setCashValueablesLossPercent(build.getCashValueablesLossPercent()==null ? null : build.getCashValueablesLossPercent().toString());
					//	 buildingRes.setStockInTradeSi(build.getStockInTradeSi()==null?"" : build.getStockInTradeSi().toPlainString());
					//buildingRes.setGoodsSi(build.getGoodsSi()==null?"" : build.getGoodsSi().toPlainString());
					//	 buildingRes.setFurnitureSi(build.getFurnitureSi()==null?"" : build.getFurnitureSi().toPlainString());
					//	 buildingRes.setApplianceSi(build.getApplianceSi()==null?"" : build.getApplianceSi().toPlainString());
					//	 buildingRes.setCashValueablesSi(build.getCashValueablesSi()==null?"" : build.getCashValueablesSi().toPlainString());
						 buildingRes.setBuildingOwnerYn(build.getBuildingOwnerYn()==null?"" : build.getBuildingOwnerYn());
					//	 buildingRes.setAccessibleWindows(build.getAccessibleWindows()==null ? "" : build.getAccessibleWindows().toString());	
						 buildingRes.setAddress(build.getAddress()==null ? "" : (build.getAddress()));
					//	 buildingRes.setBackDoors(build.getBackDoors()==null ? "" : build.getBackDoors().toString());
					//	 buildingRes.setBuildingOccupied(build.getBuildingOccupied()==null ? "": build.getBuildingOccupied().toString());
						 buildingRes.setCeilingType(build.getCeilingType()==null ? "": build.getCeilingType().toString());
						 buildingRes.setDistrictCode(build.getDistrictCode()==null ? "": build.getDistrictCode().toString());
						// buildingRes.setDoorsMaterialId(build.getDoorsMaterialId()==null? "": build.getDoorsMaterialId().toString());
						// buildingRes.setFrontDoors(build.getFrontDoors()==null ? "" :build.getFrontDoors().toString());
						 buildingRes.setInsuranceForId(build.getInsuranceForId()!= null ? Arrays.asList(build.getInsuranceForId().split(",")) : null )  ;
						 buildingRes.setNatureOfTradeId(build.getNatureOfTradeId()==null ? "" : build.getNatureOfTradeId().toString());				
						 buildingRes.setInternalWallType(build.getInternalWallType()==null? "" : build.getInternalWallType().toString());
						// buildingRes.setNightLeftDoor(build.getNightLeftDoor()==null? "" : build.getNightLeftDoor().toString());
						 buildingRes.setOccupiedYear(build.getOccupiedYear()==null ? "" : build.getOccupiedYear().toString());
						// buildingRes.setShowWindow(build.getShowWindow()==null ? "" : build.getShowWindow().toString());
						// buildingRes.setTrapDoors(build.getTrapDoors()==null  ? "" : build.getTrapDoors().toString());
						// buildingRes.setWatchmanGuardHours(build.getWatchmanGuardHours()==null ? "" :build.getWatchmanGuardHours().toString());
						// buildingRes.setWindowsMaterialId(build.getWindowsMaterialId()==null  ? "" :build.getWindowsMaterialId().toString());
						// buildingRes.setBuildingBuildYear(build.getBuildingBuildYear()==null  ? "" :build.getBuildingBuildYear().toString());
						 buildingRes.setRoofType(build.getRoofType()==null  ? "" :build.getRoofType().toString());
						 buildingRes.setWallType(build.getWallType()==null  ? "" :build.getWallType().toString());				
						 buildingRes.setRegionCode(build.getRegionCode()==null  ? "" :build.getRegionCode().toString());
								
					}
					
					// Fire And Material Damage
					List<BuildingRiskDetails> filterFire = buildings.stream().filter( o -> "40".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterFire.size() > 0 ) {
						BuildingRiskDetails build = filterFire.get(0);
				//		buildingRes.setStockInTradeSi(build.getStockInTradeSi()== null?"0" :build.getStockInTradeSi().toPlainString());
//						buildingRes.setBuildingSuminsured(build.getBuildingSuminsured() == null?"0" :build.getBuildingSuminsured().toPlainString());	
//						buildingRes.setFireEquipSi(build.getEquipmentSi() == null?"0" :build.getEquipmentSi().toPlainString());
//						buildingRes.setFirePlantSi(build.getFirePlantSi() == null?"0" :build.getFirePlantSi().toPlainString());
						buildingRes.setSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
					}
					
					// Electronic Equipment
					List<BuildingRiskDetails> filterElecEquip = buildings.stream().filter( o -> "39".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterElecEquip.size() > 0 ) {
						BuildingRiskDetails build = filterElecEquip.get(0);
//						buildingRes.setElecEquipSuminsured(build.getElecEquipSuminsured() == null?BigDecimal.ZERO :build.getElecEquipSuminsured());
						buildingRes.setSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
					}
					
					// Money
					List<BuildingRiskDetails> filterMoney = buildings.stream().filter( o -> "42".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
//					if(filterMoney.size() > 0 ) {
//						BuildingRiskDetails build = filterMoney.get(0);
//						buildingRes.setMoneyAnnualEstimate(build.getMoneyAnnualEstimate()== null?"0" : build.getMoneyAnnualEstimate().toPlainString());
//						buildingRes.setMoneyCollector(build.getMoneyCollector()== null?"0" : build.getMoneyCollector().toPlainString() );
//						buildingRes.setMoneyDirectorResidence(build.getMoneyDirectorResidence()== null?"0" : build.getMoneyDirectorResidence().toPlainString() );
//						buildingRes.setMoneyOutofSafe(build.getMoneyOutofSafe()== null?"0" : build.getMoneyOutofSafe().toPlainString() );
//						buildingRes.setMoneySafeLimit(build.getMoneySafeLimit()== null?"0" : build.getMoneySafeLimit().toPlainString() );
//						buildingRes.setMoneyMajorLoss(build.getMoneyMajorLoss() == null?"0" : build.getMoneyMajorLoss().toPlainString() );
//						
//					}
//					
					// Machinery
					List<BuildingRiskDetails> filterMachienry = buildings.stream().filter( o -> "41".equalsIgnoreCase(o.getSectionId()) ).collect(Collectors.toList());
					if(filterMachienry.size() > 0 ) {
						BuildingRiskDetails build = filterMachienry.get(0);
//						Double ElecMachinesSi = build.getElecMachinesSi() == null?0D :Double.valueOf(build.getElecMachinesSi().toPlainString());
//						Double BoilerPlantsSi = build.getBoilerPlantsSi() == null?0D :Double.valueOf(build.getBoilerPlantsSi().toPlainString()) ;
//						Double EquipmentSi = build.getEquipmentSi() == null?0D :Double.valueOf(build.getEquipmentSi().toPlainString()) ;
//						Double GeneralMachineSi = build.getGeneralMachineSi() == null?0D :Double.valueOf(build.getGeneralMachineSi().toPlainString()) ;
//						Double MachineEquipSi = build.getMachineEquipSi() == null?0D :Double.valueOf(build.getMachineEquipSi().toPlainString()) ;
//						Double ManuUnitsSi = build.getManuUnitsSi() == null?0D :Double.valueOf(build.getManuUnitsSi().toPlainString()) ;
//						Double plantSi = build.getPowerPlantSi() == null?0D :Double.valueOf(build.getPowerPlantSi().toPlainString()) ;
//						Double machinerySi = ElecMachinesSi + BoilerPlantsSi + EquipmentSi + GeneralMachineSi + MachineEquipSi + ManuUnitsSi + plantSi ;
//								
//						buildingRes.setMachinerySi( machinerySi==null ? "" :machinerySi.toString());
						buildingRes.setSuminsured(build.getSumInsured() == null?"0" :build.getSumInsured().toPlainString());
					}
				
				buildingRes.setSectionDetails(buildingSectionList);

				buildList.add(buildingRes);
				List<Object> totalList = new ArrayList<Object>();
				totalList.addAll(buildList);

				viewRes.setRiskDetails(totalList);

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return viewRes;
		}

	private List<BrokerCommissionDetails> getPolicyName( String companyId, String productId, String loginId, String agencyCode, String policyType) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			
			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"),loginId);
			Predicate n6 = cb.equal(b.get("agencyCode"),agencyCode);
			
			query.where(n1,n2,n3,n4,n5,n6);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}
//Rating
	@Override
	public List<SearchEservieMotorDetailsViewRatingRes> buildingRating() {
		// TODO Auto-generated method stub
		return null;
	}

	
	//Customer Search
	@Override
	public List<SearchCustomerDetailsRes> buildingCustSearch(SearchReq req, CompanyProductMaster product) {
			List<SearchCustomerDetailsRes> reslist = new ArrayList<SearchCustomerDetailsRes>();
			DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
			try {
				List<HomePositionMaster> homeData=null;
				if (StringUtils.isNotBlank(req.getQuoteNo())) {
					homeData = homeRepo.findByQuoteNoAndProductId(req.getQuoteNo(),Integer.valueOf(req.getProductId()));
				String customerId=homeData.get(0).getCustomerId();
				String loginId = "";
				String appId = "";
				String userName = "";
				String aproverName = "";
				String sourceType="";
				String coustomerCode="";
				String customerCodeName="";
				String source=""; 
				PersonalInfo list =new PersonalInfo();
				LoginUserInfo loginUserData=new LoginUserInfo();
				List<PremiaCustomerDetails> premiadata =null;
				List<EserviceBuildingDetails> motor = repo.findByCustomerId(customerId);
				if (motor.size() > 0) {
					sourceType = motor.get(0).getSourceType();
					coustomerCode = motor.get(0).getCustomerCode();
					loginId = motor.get(0).getLoginId();
					appId = motor.get(0).getApplicationId();
					source = motor.get(0).getLoginId();
					customerCodeName = motor.get(0).getCustomerName();
//					premiadata = premiaRepo.findByCustomerCode(coustomerCode);
//					if (premiadata.size() > 0) {
//						customerCodeName = premiadata.get(0).getCustomerName();
//					}
				}
				list = perRepo.findByCustomerId(homeData.get(0).getCustomerId());
				if ("Broker".equalsIgnoreCase(sourceType)) {
					loginUserData=loginUserRepo.findByLoginId(loginId);
					userName=loginUserData.getUserName();
					
				}
				if(!"1".equalsIgnoreCase(appId)){
					loginUserData=loginUserRepo.findByLoginId(appId);
					aproverName=loginUserData.getUserName();
				}
				SearchCustomerDetailsRes res = new SearchCustomerDetailsRes();
				res = dozerMapper.map(list,SearchCustomerDetailsRes.class);	
				res.setLoginId(loginId);
				res.setBrokerName(userName==null?"":userName);
				res.setApplicationId(appId);
				res.setApproverName(aproverName==null?"":aproverName);
				res.setCustomerCode(coustomerCode);
				res.setCustomerName(customerCodeName);	
				res.setSourceType(sourceType);
				res.setBranchCode(list.getBranchCode().toString());
				res.setSource(source);
				res.setCompanyId(product.getCompanyId());
				res.setCompanyName(homeData.get(0).getCompanyName());
				res.setProductType(product.getMotorYn());
				reslist.add(res);
			}else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				String customerId="";
				String loginId = "";
				String appId = "";
				String userName = "";
				String aproverName = "";
				String sourceType="";
				String coustomerCode="";
				String customerCodeName="";
				String source=""; 
				LoginUserInfo loginUserData=new LoginUserInfo();
				List<EserviceBuildingDetails> motor = repo.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (motor.size() > 0) {
					sourceType = motor.get(0).getSourceType();
					coustomerCode = motor.get(0).getCustomerCode();
					loginId = motor.get(0).getLoginId();
					appId = motor.get(0).getApplicationId();
					source = motor.get(0).getLoginId();
					customerCodeName = motor.get(0).getCustomerName();
//					premiadata = premiaRepo.findByCustomerCode(coustomerCode);
//					if (premiadata.size() > 0) {
//						customerCodeName = premiadata.get(0).getCustomerName();
//					}
				}
				EserviceCustomerDetails list = cusrepo.findByCustomerReferenceNo(motor.get(0).getCustomerReferenceNo());
				if ("Broker".equalsIgnoreCase(sourceType)) {
					loginUserData=loginUserRepo.findByLoginId(loginId);
					userName=loginUserData.getUserName();
					
				}
				if(!"1".equalsIgnoreCase(appId)){
					loginUserData=loginUserRepo.findByLoginId(appId);
					aproverName=loginUserData.getUserName();
				}
				SearchCustomerDetailsRes res = new SearchCustomerDetailsRes();
				res = dozerMapper.map(list,SearchCustomerDetailsRes.class);	
				res.setLoginId(loginId);
				res.setBrokerName(userName==null?"":userName);
				res.setApplicationId(appId);
				res.setApproverName(aproverName==null?"":aproverName);
				res.setCustomerCode(coustomerCode);
				res.setCustomerName(customerCodeName);	
				res.setSourceType(sourceType);
				res.setBranchCode(list.getBranchCode().toString());
				res.setSource(source);
				res.setCompanyId(product.getCompanyId());
				res.setCompanyName(homeData.get(0).getCompanyName());
				res.setProductType(product.getMotorYn());
				reslist.add(res);	
			}
			}catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return reslist;
		}

	@Override
	public ViewQuoteDetailsRes viewQuoteBuilding(ViewQuoteDetailsReq req) {

		ViewQuoteDetailsRes res = new ViewQuoteDetailsRes();
		try {
		
			List<EserviceBuildingDetails> motor = repo.findByRequestReferenceNo(req.getRequestReferenceNo());

				if(motor.size()>0) {
					EserviceBuildingDetails mot = motor.get(0);
			//		res.setAdminReferralStatus(null);		
					res.setAdminRemarks(mot.getAdminRemarks()==null?"":mot.getAdminRemarks());
					res.setApplicationid(mot.getApplicationId()==null?"":mot.getApplicationId());
					res.setBranchCode(mot.getBranchCode()==null?"":mot.getBranchCode());
			//		res.setCommission(mot.getcomm);
					res.setCommissionPercentage(mot.getCommissionPercentage()==null?"":mot.getCommissionPercentage().toString());
				//	res.setCreditNo(mot.getcre);
					res.setCurrency(mot.getCurrency()==null?"":mot.getCurrency());
					res.setCustomerCode(mot.getCustomerCode()==null?"":mot.getCustomerCode());
					res.setCustomerName(mot.getCustomerName()==null?"":mot.getCustomerName());
				//	res.setDebitNoteNo(mot.getde);
					res.setEndtCount(mot.getEndtCount()==null?"":mot.getEndtCount().toString());
					res.setEndtPremium(mot.getEndtPremium()==null?"":mot.getEndtPremium().toString());
					res.setEndtTypeDesc(mot.getEndorsementTypeDesc()==null?"":mot.getEndorsementTypeDesc().toString());
					res.setEndtTypeId(mot.getEndorsementType()==null?"": mot.getEndorsementType().toString());
					res.setExchangeRate(mot.getExchangeRate()==null?"":mot.getExchangeRate().toString() );
					res.setExpiryDate(mot.getPolicyEndDate()==null?null:mot.getPolicyEndDate() );
					res.setInceptionDate(mot.getPolicyStartDate()==null?null:mot.getPolicyStartDate());
				//	res.setIntegrationStatus(mot.getint);
					res.setLoginid(mot.getLoginId()==null?"":mot.getLoginId());
					res.setOverallPremiumFc(mot.getOverallPremiumFc()==null?"":mot.getOverallPremiumFc().toString() );
					res.setOverallPremiumLc(mot.getOverallPremiumLc()==null?"": mot.getOverallPremiumLc().toString());
			//		res.setPolicyCovertedDate(mot.getdate);
					res.setPolicyPeriod(mot.getPolicyPeriord()==null?"":mot.getPolicyPeriord().toString());
				//	res.setQuoteCreatedDate(mot.getdate);
					res.setReferralDescription(mot.getReferalRemarks()==null?"":mot.getReferalRemarks());
					res.setSourcetype(mot.getSourceType()==null?"":mot.getSourceType().toString());
					res.setVatCommission(mot.getVatCommission()==null?"":mot.getVatCommission().toString());
					res.setEndorsementYn( mot.getEndorsementType()==null?"N":"Y");
				}
			
		}catch(Exception e) {
			e.printStackTrace();
			}
		return res;
	
	}
	}
		
