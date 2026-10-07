package com.maan.eway.renewal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.RenewPremiaPolicy;
import com.maan.eway.bean.RenewVehicleInfo;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.RenewalTrackAgentResByProduct2;
import com.maan.eway.renewal.req.RenewalTrackReq;
import com.maan.eway.renewal.req.RenewalVehicleReq;
import com.maan.eway.renewal.req.UpdateRenewalPremiaPolicyReq;
import com.maan.eway.renewal.res.BranchForRenewalTrack;
import com.maan.eway.renewal.res.DivisionDetails;
import com.maan.eway.renewal.res.PolicyDet;
import com.maan.eway.renewal.res.ProductByBranch;
import com.maan.eway.renewal.res.ProductDetails;
import com.maan.eway.renewal.res.ProductsBySourceRes;
import com.maan.eway.renewal.res.UpdateRenewalPremiaRes;
import com.maan.eway.renewal.service.RenewalTrackingService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.RenewPremiaPolicyRepository;
import com.maan.eway.repository.RenewQuotePolicyRepository;
import com.maan.eway.repository.RenewVehicleInfoRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class RenewalTrackingServiceImpl implements RenewalTrackingService {
	@Autowired
	private HomePositionMasterRepository homePositionMasterRepo;

	@Autowired
	private RenewQuotePolicyRepository renewQuotePolicyRepo;

	@Autowired
	private LoginBranchMasterRepository branchRepo;

	@Autowired
	private LoginUserInfoRepository userInfo;

	@Autowired
	private RenewPremiaPolicyRepository rppRepo;

	@Autowired
	private LoginBranchMasterRepository lbmRepo;

	@Autowired
	private EntityManager em;
	
	@Autowired
	RenewVehicleInfoRepo vehiclerepo;

	public BigDecimal rate(Long count, Long totalCount) {
		if (count == null || totalCount == null || totalCount == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal countBD = BigDecimal.valueOf(count);
		BigDecimal totalCountBD = BigDecimal.valueOf(totalCount);

		return countBD.multiply(BigDecimal.valueOf(100)).divide(totalCountBD, 2, RoundingMode.HALF_UP); // returns
																										// percentage

	}

	private Integer policyCountBySourceAndDiv(List<ProductDetails> list) {
		int totalPolicyCount = list.stream().map(ProductDetails::getProductCount) // get the string
				.filter(Objects::nonNull) // avoid nulls
				.map(String::trim) // clean strings
				.filter(s -> !s.isEmpty()) // avoid empty strings
				.mapToInt(Integer::parseInt) // convert to int
				.filter(i -> i >= 0) // only non-negative
				.sum();
		return totalPolicyCount;
	}

//            From division <--> products <--> Agents <--> policy details 

//  Get Divisions From companyId
	@Override
	public BranchForRenewalTrack RenewalTrackGetBranch(RenewalTrackReq req) {
		BranchForRenewalTrack res = new BranchForRenewalTrack();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DivisionDetails> cq = cb.createQuery(DivisionDetails.class);
			Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

			Expression<Long> successCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RS"), 1L).otherwise(0L));
			Expression<Long> pendingCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RP"), 1L).otherwise(0L));
			Expression<Long> lostCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RR"), 1L).otherwise(0L));

			// Select DISTINCT division_code
			cq.multiselect(root.get("divisionCode").alias("divisionCode"),
					root.get("divisionName").alias("divisionName"),
					cb.count(root).as(String.class).alias("totalPolicycount"),
					cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
					successCount.as(String.class).alias("successCount"),
					pendingCount.as(String.class).alias("pendingCount"), lostCount.as(String.class).alias("lostCount"));

			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			Predicate between = cb.between(root.get("expiryDate"), startTimestamp, endTimestamp);
			// WHERE company_id = '100020'
			cq.where(cb.equal(root.get("companyId"), req.getCompanyId()), between);

			cq.groupBy(root.get("divisionCode"), root.get("divisionName"));

			// Execute query
			List<DivisionDetails> result = em.createQuery(cq).getResultList();
			res.setCompanyId(req.getCompanyId());
			if (!CollectionUtils.isEmpty(result)) {
				result.parallelStream().forEach(division -> {
					Long success = Long.parseLong(division.getSuccessCount());
					Long totCount = Long.parseLong(division.getTotalPolicycount());
					BigDecimal rate = rate(success, totCount);
					division.setSuccessRate(rate.toPlainString());
				});
				res.setDivisionDetails(result);
				res.setNoOfDivisions(String.valueOf(result.size()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

// get product details from division
	public List<ProductByBranch> GetRenewalDetailsByDivsion2(RenewalTrackReq req) {
		List<ProductByBranch> list = getBranchByProduct(req);
		return list;
	}

	private List<ProductByBranch> getBranchByProduct(RenewalTrackReq req) {
		List<ProductByBranch> res=new ArrayList<ProductByBranch>();
		try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductByBranch> cq = cb.createQuery(ProductByBranch.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);
		
		Expression<Long> successCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RS"), 1L)
			        .otherwise(0L)
			);
			Expression<Long> pendingCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RP"), 1L)
			        .otherwise(0L)
			);
			Expression<Long> lostCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RR"), 1L)
			        .otherwise(0L)
			);


		cq.select(cb.construct(ProductByBranch.class, root.get("departmentCode").alias("productCode"),
				root.get("departmentName").alias("productName"), cb.count(root).as(String.class).alias("productCount"),
				cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
				successCount.as(String.class).alias("successCount"),
				pendingCount.as(String.class).alias("pendingCount"),
				lostCount.as(String.class).alias("lostCount")
				));

		// Convert String to Timestamp
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
		LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

		// Convert LocalDate to Timestamp (start of day and end of day)
		Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
		Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

		// Add condition to criteria
		Predicate between = cb.between(root.get("expiryDate"), startTimestamp, endTimestamp);
		
		List<Predicate> pre=new ArrayList<Predicate>();
		pre.add(cb.equal(root.get("companyId"), req.getCompanyId()));
		pre.add(between);
		
		pre.add(cb.equal(root.get("divisionCode"), req.getDivisionCode()));
		if(StringUtils.isNotBlank(req.getSourceCode()))
		{
			pre.add(cb.equal(root.get("polSrcCode"), req.getSourceCode()));
		}
		cq.where(pre.toArray(new Predicate[0]));
		
		
		
		cq.groupBy(root.get("departmentCode"), root.get("departmentName"));
		
		res=em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(res)) {
		res.parallelStream().forEach(product-> { 
			Long success=Long.parseLong(product.getSuccessCount());
			Long totCount=Long.parseLong(product.getProductCount());
			BigDecimal rate = rate(success,totCount);
			product.setSuccessRate(rate.toPlainString());

		});
		}
		}catch(Exception e) {
			e.printStackTrace();
		}

		return res;
	}


	// get brokers from product
	@Override
	public List<RenewalTrackAgentResByProduct2> RenewalTrackAgentRes2(RenewalTrackReq req) {
		List<RenewalTrackAgentResByProduct2> res = new ArrayList<RenewalTrackAgentResByProduct2>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalTrackAgentResByProduct2> cq = cb.createQuery(RenewalTrackAgentResByProduct2.class);
			Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

			Expression<Long> successCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RS"), 1L).otherwise(0L));
			Expression<Long> pendingCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RP"), 1L).otherwise(0L));
			Expression<Long> lostCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RR"), 1L).otherwise(0L));

			cq.select(cb.construct(RenewalTrackAgentResByProduct2.class, root.get("polSrcCode").alias("sourceCode"),
					root.get("polSrcName").alias("sourceName"), cb.count(root).as(String.class).alias("sourceCount"),
					cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
					successCount.as(String.class).alias("successCount"),
					pendingCount.as(String.class).alias("pendingCount"),
					lostCount.as(String.class).alias("lostCount")));

			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			Predicate between = cb.between(root.get("expiryDate"), startTimestamp, endTimestamp);

			cq.where(cb.equal(root.get("companyId"), req.getCompanyId()),
					cb.equal(root.get("divisionCode"), req.getDivisionCode()),
					cb.equal(root.get("departmentCode"), req.getProductCode()), between);

			cq.groupBy(root.get("polSrcCode"), root.get("polSrcName"));

			res = em.createQuery(cq).getResultList();
			if (!CollectionUtils.isEmpty(res)) {
				res.parallelStream().forEach(agent -> {
					Long success = Long.parseLong(agent.getSuccessCount());
					Long totCount = Long.parseLong(agent.getSourceCount());
					BigDecimal rate = rate(success, totCount);
					agent.setSuccessRate(rate.toPlainString());
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	// Get product list by a source in a division & company
	@Override
	public ProductsBySourceRes getProductsBySource(RenewalTrackReq req) {
		ProductsBySourceRes res = new ProductsBySourceRes();
		try {
			List<ProductDetails> productList = getProductsBySourceIn(req);
			res.setSourceCode(req.getSourceCode());
			res.setTotalPolicyCount(policyCountBySourceAndDiv(productList).toString());
			if (!CollectionUtils.isEmpty(productList)) {

				productList.parallelStream().forEach(product -> {
					Long success = Long.parseLong(product.getSuccessCount());
					Long totCount = Long.parseLong(product.getProductCount());
					BigDecimal rate = rate(success, totCount);
					product.setSuccessRate(rate.toPlainString());
				});
				res.setProductDetails(productList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private List<ProductDetails> getProductsBySourceIn(RenewalTrackReq req) {
		List<ProductDetails> resList = new ArrayList<ProductDetails>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductDetails> cq = cb.createQuery(ProductDetails.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			Expression<Long> successCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(r.get("currentStatus"), "RS"), 1L).otherwise(0L));
			Expression<Long> pendingCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(r.get("currentStatus"), "RP"), 1L).otherwise(0L));
			Expression<Long> lostCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(r.get("currentStatus"), "RR"), 1L).otherwise(0L));
			cq.select(cb.construct(ProductDetails.class, r.get("departmentCode").alias("productCode"),
					r.get("departmentName").alias("productName"), cb.count(r).as(String.class).alias("productCount"),
					cb.sum(r.get("totalPremium")).as(String.class).alias("totalPremium"),
					successCount.as(String.class).alias("successCount"),
					pendingCount.as(String.class).alias("pendingCount"),
					lostCount.as(String.class).alias("lostCount")));
			cq.where(cb.equal(r.get("companyId"), req.getCompanyId()),
					cb.equal(r.get("divisionCode"), req.getDivisionCode()),
					cb.equal(r.get("polSrcCode"), req.getSourceCode()));
			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			Predicate between = cb.between(r.get("expiryDate"), startTimestamp, endTimestamp);

			cq.where(cb.equal(r.get("companyId"), req.getCompanyId()),
					cb.equal(r.get("divisionCode"), req.getDivisionCode()),
					cb.equal(r.get("polSrcCode"), req.getSourceCode()), between);

			cq.groupBy(r.get("departmentCode"), r.get("departmentName"));
			resList = em.createQuery(cq).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

	@Override
	public List<PolicyDet> RenewalTrackPolicyDetailsBySource(RenewalTrackReq req) {
		List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(PolicyDet.class, r.get("transactionId").alias("transactionId"),
					r.get("policyNumber").alias("policyNumber"),
					r.get("expiryDate").as(String.class).alias("expiryDate"),

					r.get("companyId").alias("companyId"), r.get("companyCode").alias("companyCode"),
					r.get("companyName").alias("companyName"),

					r.get("classCode").alias("classCode"), r.get("className").alias("className"),

			//		r.get("productCode").alias("productCode"), r.get("productName").alias("productName"),

					r.get("divisionCode").alias("divisionCode"), r.get("divisionName").alias("divisionName"),

					r.get("departmentCode").alias("productCode"), r.get("departmentName").alias("productName"),

					r.get("businessType").alias("businessType"), r.get("businessName").alias("businessName"),

					r.get("endorsementNumber").alias("endorsementNumber"),
					r.get("fromDate").as(String.class).alias("fromDate"),
					r.get("renewalDate").as(String.class).alias("renewalDate"),

			//		r.get("customerCode").alias("customerCode"), r.get("customerName").alias("customerName"),
					r.get("insuredCivilId").alias("insuredCivilId"), r.get("insuredMobile").alias("insuredMobile"),
					r.get("insuredEmailId").alias("insuredEmailId"),

					r.get("polAssrCode").alias("customerCode"), r.get("polAssrName").alias("customerName"),

					r.get("polSrcType").alias("polSrcType"), r.get("polSrcCode").alias("polSrcCode"),
					r.get("polSrcName").alias("polSrcName"),

					r.get("policySi").alias("policySi"), r.get("grossPremium").alias("grossPremium"),
					r.get("coverPremium").alias("coverPremium"), r.get("discountPremium").alias("discountPremium"),
					r.get("loadingPremium").alias("loadingPremium"),

					r.get("pvtCoverYn").alias("pvtCoverYn"), r.get("pvtCoverSi").alias("pvtCoverSi"),
					r.get("pvtCoverPremium").alias("pvtCoverPremium"),

					r.get("chargeAmount").alias("chargeAmount"), r.get("totalPremium").alias("totalPremium"),
					r.get("agBrokCommission").alias("agBrokCommission"),

					r.get("currentStatus").alias("currentStatus"), r.get("newPolicyNumber").alias("newPolicyNumber"),
					r.get("lossReason").alias("lossReason"), r.get("lossRemarks").alias("lossRemarks"),
					r.get("competitor").alias("competitor"),


					r.get("entryDate").as(String.class).alias("entryDate"),
					r.get("paymentType").alias("paymentType")));

					
//			cq.where(cb.equal(r.get("companyId"), req.getCompanyId()),
//					cb.equal(r.get("divisionCode"), req.getDivisionCode()),
//					cb.equal(r.get("polSrcCode"), req.getSourceCode()),
//					cb.equal(r.get("departmentCode"), req.getProductCode()));
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			Predicate pro=cb.equal(r.get("departmentCode"),req.getProductCode());
			Predicate between = cb.between(r.get("expiryDate"), startTimestamp, endTimestamp);

			cq.where(cb.equal(r.get("companyId"), req.getCompanyId()),
					cb.equal(r.get("divisionCode"), req.getDivisionCode()),
					cb.equal(r.get("polSrcCode"), req.getSourceCode()),pro,
					 between);

			policyDetails = em.createQuery(cq).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return policyDetails;
	}

	// Get Approver's Division code
	public BranchForRenewalTrack RenewalTrackGetBranchByApprover(RenewalTrackReq req) {
		BranchForRenewalTrack res = new BranchForRenewalTrack();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DivisionDetails> cq = cb.createQuery(DivisionDetails.class);
			Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

			Expression<Long> successCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RS"), 1L).otherwise(0L));
			Expression<Long> pendingCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RP"), 1L).otherwise(0L));
			Expression<Long> lostCount = cb
					.sum(cb.<Long>selectCase().when(cb.equal(root.get("currentStatus"), "RF"), 1L).otherwise(0L));

			// Select DISTINCT division_code
			cq.multiselect(root.get("divisionCode").alias("divisionCode"),
					root.get("divisionName").alias("divisionName"),
					cb.count(root).as(String.class).alias("totalPolicycount"),
					cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
					successCount.as(String.class).alias("successCount"),
					pendingCount.as(String.class).alias("pendingCount"), lostCount.as(String.class).alias("lostCount"));

			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			Predicate between = cb.between(root.get("expiryDate"), startTimestamp, endTimestamp);

			// WHERE company_id = '100020'
			cq.where(cb.equal(root.get("companyId"), req.getCompanyId()));

			cq.groupBy(root.get("divisionCode"), root.get("divisionName"));

			// Execute query
			List<DivisionDetails> result = em.createQuery(cq).getResultList();
			res.setCompanyId(req.getCompanyId());
			if (!CollectionUtils.isEmpty(result)) {
				result.parallelStream().forEach(division -> {
					Long success = Long.parseLong(division.getSuccessCount());
					Long totCount = Long.parseLong(division.getTotalPolicycount());
					BigDecimal rate = rate(success, totCount);
					division.setSuccessRate(rate.toPlainString());
				});
				res.setDivisionDetails(result);
				res.setNoOfDivisions(String.valueOf(result.size()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public List<PolicyDet> getTop10CustomerDetails(RenewalTrackReq  req) {
		List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(
				    PolicyDet.class,
				    r.get("transactionId").alias("transactionId"),
				    r.get("policyNumber").alias("policyNumber"),
				    r.get("expiryDate").as(String.class).alias("expiryDate"),

				    r.get("companyId").alias("companyId"),
				    r.get("companyCode").alias("companyCode"),
				    r.get("companyName").alias("companyName"),

				    r.get("classCode").alias("classCode"),
				    r.get("className").alias("className"),

//				    r.get("productCode").alias("productCode"),
//				    r.get("productName").alias("productName"),

				    r.get("divisionCode").alias("divisionCode"),
				    r.get("divisionName").alias("divisionName"),

				    r.get("departmentCode").alias("productCode"),
				    r.get("departmentName").alias("productName"),

				    r.get("businessType").alias("businessType"),
				    r.get("businessName").alias("businessName"),

				    r.get("endorsementNumber").alias("endorsementNumber"),
				    r.get("fromDate").as(String.class).alias("fromDate"),
				    r.get("renewalDate").as(String.class).alias("renewalDate"),

//				    r.get("customerCode").alias("customerCode"),
//				    r.get("customerName").alias("customerName"),
				    r.get("insuredCivilId").alias("insuredCivilId"),
				    r.get("insuredMobile").alias("insuredMobile"),
				    r.get("insuredEmailId").alias("insuredEmailId"),

				    r.get("polAssrCode").alias("customerCode"),
				    r.get("polAssrName").alias("customerName"),

				    r.get("polSrcType").alias("polSrcType"),
				    r.get("polSrcCode").alias("polSrcCode"),
				    r.get("polSrcName").alias("polSrcName"),

				    r.get("policySi").alias("policySi"),
				    r.get("grossPremium").alias("grossPremium"),
				    r.get("coverPremium").alias("coverPremium"),
				    r.get("discountPremium").alias("discountPremium"),
				    r.get("loadingPremium").alias("loadingPremium"),

				    r.get("pvtCoverYn").alias("pvtCoverYn"),
				    r.get("pvtCoverSi").alias("pvtCoverSi"),
				    r.get("pvtCoverPremium").alias("pvtCoverPremium"),

				    r.get("chargeAmount").alias("chargeAmount"),
				    r.get("totalPremium").alias("totalPremium"),
				    r.get("agBrokCommission").alias("agBrokCommission"),

				    r.get("currentStatus").alias("currentStatus"),
				    r.get("newPolicyNumber").alias("newPolicyNumber"),
				    r.get("lossReason").alias("lossReason"),
				    r.get("lossRemarks").alias("lossRemarks"),
				    r.get("competitor").alias("competitor"),

				    r.get("entryDate").as(String.class).alias("entryDate"),
				    r.get("paymentType").alias("paymentType")
				));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay()); // 00:00:00
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX)); // 23:59:59.999999999

			Predicate between = cb.between(r.get("expiryDate"), startTimestamp, endTimestamp);
			Predicate divisionCodePredicate = cb.equal(r.get("divisionCode"), req.getDivisionCode());
			cq.where(cb.and(between, divisionCodePredicate));
			cq.orderBy(cb.desc(r.get("totalPremium")));
			policyDetails = em.createQuery(cq).setMaxResults(10).getResultList();

		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return policyDetails;
	}
	
	@Override
	public List<PolicyDet> getExpiryPolicyDetails(String div) {
		List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(
				    PolicyDet.class,
				    r.get("transactionId").alias("transactionId"),
				    r.get("policyNumber").alias("policyNumber"),
				    r.get("expiryDate").as(String.class).alias("expiryDate"),

				    r.get("companyId").alias("companyId"),
				    r.get("companyCode").alias("companyCode"),
				    r.get("companyName").alias("companyName"),

				    r.get("classCode").alias("classCode"),
				    r.get("className").alias("className"),

//				    r.get("productCode").alias("productCode"),
//				    r.get("productName").alias("productName"),

				    r.get("divisionCode").alias("divisionCode"),
				    r.get("divisionName").alias("divisionName"),

				    r.get("departmentCode").alias("productCode"),
				    r.get("departmentName").alias("productName"),

				    r.get("businessType").alias("businessType"),
				    r.get("businessName").alias("businessName"),

				    r.get("endorsementNumber").alias("endorsementNumber"),
				    r.get("fromDate").as(String.class).alias("fromDate"),
				    r.get("renewalDate").as(String.class).alias("renewalDate"),

//				    r.get("customerCode").alias("customerCode"),
//				    r.get("customerName").alias("customerName"),
				    r.get("insuredCivilId").alias("insuredCivilId"),
				    r.get("insuredMobile").alias("insuredMobile"),
				    r.get("insuredEmailId").alias("insuredEmailId"),

				    r.get("polAssrCode").alias("customerCode"),
				    r.get("polAssrName").alias("customerName"),

				    r.get("polSrcType").alias("polSrcType"),
				    r.get("polSrcCode").alias("polSrcCode"),
				    r.get("polSrcName").alias("polSrcName"),

				    r.get("policySi").alias("policySi"),
				    r.get("grossPremium").alias("grossPremium"),
				    r.get("coverPremium").alias("coverPremium"),
				    r.get("discountPremium").alias("discountPremium"),
				    r.get("loadingPremium").alias("loadingPremium"),

				    r.get("pvtCoverYn").alias("pvtCoverYn"),
				    r.get("pvtCoverSi").alias("pvtCoverSi"),
				    r.get("pvtCoverPremium").alias("pvtCoverPremium"),

				    r.get("chargeAmount").alias("chargeAmount"),
				    r.get("totalPremium").alias("totalPremium"),
				    r.get("agBrokCommission").alias("agBrokCommission"),

				    r.get("currentStatus").alias("currentStatus"),
				    r.get("newPolicyNumber").alias("newPolicyNumber"),
				    r.get("lossReason").alias("lossReason"),
				    r.get("lossRemarks").alias("lossRemarks"),
				    r.get("competitor").alias("competitor"),

				    r.get("entryDate").as(String.class).alias("entryDate"),
				    r.get("paymentType").alias("paymentType")
				));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			LocalDate start = LocalDate.now();
			LocalDate end = start.plusDays(3);

			start.format(formatter);
			end.format(formatter);

			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));
			Predicate expiryRangePredicate = cb.between( r.get("expiryDate"),startTimestamp, endTimestamp);
			Predicate divisionCodePredicate = cb.equal(r.get("divisionCode"), div);
			cq.where(expiryRangePredicate,divisionCodePredicate);
			policyDetails = em.createQuery(cq).getResultList();

		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return policyDetails;
	}
	public List<PolicyDet> getPolicyStatusList(RenewalTrackReq req) {
		List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(
				    PolicyDet.class,
				    r.get("transactionId").alias("transactionId"),
				    r.get("policyNumber").alias("policyNumber"),
				    r.get("expiryDate").as(String.class).alias("expiryDate"),

				    r.get("companyId").alias("companyId"),
				    r.get("companyCode").alias("companyCode"),
				    r.get("companyName").alias("companyName"),

				    r.get("classCode").alias("classCode"),
				    r.get("className").alias("className"),

//				    r.get("productCode").alias("productCode"),
//				    r.get("productName").alias("productName"),

				    r.get("divisionCode").alias("divisionCode"),
				    r.get("divisionName").alias("divisionName"),

				    r.get("departmentCode").alias("productCode"),
				    r.get("departmentName").alias("productName"),

				    r.get("businessType").alias("businessType"),
				    r.get("businessName").alias("businessName"),

				    r.get("endorsementNumber").alias("endorsementNumber"),
				    r.get("fromDate").as(String.class).alias("fromDate"),
				    r.get("renewalDate").as(String.class).alias("renewalDate"),

//				    r.get("customerCode").alias("customerCode"),
//				    r.get("customerName").alias("customerName"),
				    r.get("insuredCivilId").alias("insuredCivilId"),
				    r.get("insuredMobile").alias("insuredMobile"),
				    r.get("insuredEmailId").alias("insuredEmailId"),

				    r.get("polAssrCode").alias("customerCode"),
				    r.get("polAssrName").alias("customerName"),

				    r.get("polSrcType").alias("polSrcType"),
				    r.get("polSrcCode").alias("polSrcCode"),
				    r.get("polSrcName").alias("polSrcName"),

				    r.get("policySi").alias("policySi"),
				    r.get("grossPremium").alias("grossPremium"),
				    r.get("coverPremium").alias("coverPremium"),
				    r.get("discountPremium").alias("discountPremium"),
				    r.get("loadingPremium").alias("loadingPremium"),

				    r.get("pvtCoverYn").alias("pvtCoverYn"),
				    r.get("pvtCoverSi").alias("pvtCoverSi"),
				    r.get("pvtCoverPremium").alias("pvtCoverPremium"),

				    r.get("chargeAmount").alias("chargeAmount"),
				    r.get("totalPremium").alias("totalPremium"),
				    r.get("agBrokCommission").alias("agBrokCommission"),

				    r.get("currentStatus").alias("currentStatus"),
				    r.get("newPolicyNumber").alias("newPolicyNumber"),
				    r.get("lossReason").alias("lossReason"),
				    r.get("lossRemarks").alias("lossRemarks"),
				    r.get("competitor").alias("competitor"),

				    r.get("entryDate").as(String.class).alias("entryDate"),
				    r.get("paymentType").alias("paymentType")
				));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay()); // 00:00:00
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX)); // 23:59:59.999999999
			
			Predicate between = cb.between(r.get("expiryDate"), startTimestamp, endTimestamp);
			Predicate status =cb.equal(r.get("currentStatus"),req.getStatus());
			Predicate divisionCodePredicate = cb.equal(r.get("divisionCode"), req.getDivisionCode());
			cq.where(between,status,divisionCodePredicate);
			policyDetails = em.createQuery(cq).getResultList();

		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return policyDetails;
	}
	
	
	
	

	// Update Renew_Premia_Policy
	@Override
	public CommonRes updateRenewalPremiaPolicy(UpdateRenewalPremiaPolicyReq req) {
		CommonRes res = new CommonRes();
		RenewPremiaPolicy rpp = new RenewPremiaPolicy();
		UpdateRenewalPremiaRes updateRes = new UpdateRenewalPremiaRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<RenewPremiaPolicy> renewalDetails = rppRepo.findByPolicyNumber(req.getPolicyNumber());
			if (!CollectionUtils.isEmpty(renewalDetails)) {
				RenewPremiaPolicy renew = renewalDetails.get(0);
				if (!StringUtils.isBlank(req.getTransactionId()))
					renew.setTransactionId(req.getTransactionId());
				if (!StringUtils.isBlank(req.getPolicyNumber()))
					renew.setPolicyNumber(req.getPolicyNumber());
				if (!StringUtils.isBlank(req.getExpiryDate()))
					renew.setExpiryDate(Timestamp.valueOf(req.getExpiryDate()));

				if (!StringUtils.isBlank(req.getCompanyId()))
					renew.setCompanyId(req.getCompanyId());
				if (!StringUtils.isBlank(req.getCompanyCode()))
					renew.setCompanyCode(req.getCompanyCode());
				if (!StringUtils.isBlank(req.getCompanyName()))
					renew.setCompanyName(req.getCompanyName());
				if (!StringUtils.isBlank(req.getClassCode()))
					renew.setClassCode(req.getClassCode());
				if (!StringUtils.isBlank(req.getClassName()))
					renew.setClassName(req.getClassName());
				if (!StringUtils.isBlank(req.getProductCode()))
					renew.setProductCode(req.getProductCode());
				if (!StringUtils.isBlank(req.getProductName()))
					renew.setProductName(req.getProductName());
				if (!StringUtils.isBlank(req.getDivisionCode()))
					renew.setDivisionCode(req.getDivisionCode());
				if (!StringUtils.isBlank(req.getDivisionName()))
					renew.setDivisionName(req.getDivisionName());
				if (!StringUtils.isBlank(req.getDepartmentCode()))
					renew.setDepartmentCode(req.getDepartmentCode());
				if (!StringUtils.isBlank(req.getDepartmentName()))
					renew.setDepartmentName(req.getDepartmentName());
				if (!StringUtils.isBlank(req.getBusinessType()))
					renew.setBusinessType(req.getBusinessType());
				if (!StringUtils.isBlank(req.getBusinessName()))
					renew.setBusinessName(req.getBusinessName());
				if (!StringUtils.isBlank(req.getEndorsementNumber()))
					renew.setEndorsementNumber(req.getEndorsementNumber());

				if (!StringUtils.isBlank(req.getFromDate()))
					renew.setFromDate(Timestamp.valueOf(req.getFromDate()));
				if (!StringUtils.isBlank(req.getRenewalDate())) {
				    LocalDate date = LocalDate.parse(req.getRenewalDate()); // parse "2025-05-22"
				    Timestamp timestamp = Timestamp.valueOf(date.atStartOfDay()); // convert to "2025-05-22 00:00:00"
				    renew.setRenewalDate(timestamp);
				}

				if (!StringUtils.isBlank(req.getCustomerCode()))
					renew.setCustomerCode(req.getCustomerCode());
				if (!StringUtils.isBlank(req.getCustomerName()))
					renew.setCustomerName(req.getCustomerName());
				if (!StringUtils.isBlank(req.getInsuredCivilId()))
					renew.setInsuredCivilId(req.getInsuredCivilId());
				if (!StringUtils.isBlank(req.getInsuredMobile()))
					renew.setInsuredMobile(req.getInsuredMobile());
				if (!StringUtils.isBlank(req.getInsuredEmailId()))
					renew.setInsuredEmailId(req.getInsuredEmailId());
				if (!StringUtils.isBlank(req.getPolAssrCode()))
					renew.setPolAssrCode(req.getPolAssrCode());
				if (!StringUtils.isBlank(req.getPolAssrName()))
					renew.setPolAssrName(req.getPolAssrName());
				if (!StringUtils.isBlank(req.getPolSrcType()))
					renew.setPolSrcType(req.getPolSrcType());
				if (!StringUtils.isBlank(req.getPolSrcCode()))
					renew.setPolSrcCode(req.getPolSrcCode());
				if (!StringUtils.isBlank(req.getPolSrcName()))
					renew.setPolSrcName(req.getPolSrcName());
				if (!StringUtils.isBlank(req.getLossReason()))
					renew.setLossReason(req.getLossReason());
				if (!StringUtils.isBlank(req.getLossRemarks()))
					renew.setLossRemarks(req.getLossRemarks());
				if (!StringUtils.isBlank(req.getCompetitor()))
					renew.setCompetitor(req.getCompetitor());
				if (!StringUtils.isBlank(req.getCurrentStatus()))
					renew.setCurrentStatus(req.getCurrentStatus());
				if (!StringUtils.isBlank(req.getPaymentType()))
					renew.setPaymentType(req.getPaymentType());
				rppRepo.saveAndFlush(renew);
				res.setMessage("UpdatedSuccessfully");
				res.setCommonResponse(renew);
				res.setIsError(false);
			} else {
				res.setMessage("No data for this Policy Number");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setMessage("Exception Occurs");
			res.setIsError(true);
		}
		return res;
	}
	
	@Override
	public CommonRes getTopPremiumCustomerDetails(RenewalTrackReq req){
		CommonRes res = new CommonRes();
		List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();
		try{
			if(!StringUtils.isEmpty(req.getCompanyId()) &&  StringUtils.isEmpty(req.getDivisionCode()) &&  StringUtils.isEmpty(req.getProductCode()) &&  StringUtils.isEmpty(req.getSourceCode()) &&
				    !StringUtils.isEmpty(req.getStartDate()) && !StringUtils.isEmpty(req.getEndDate()) && StringUtils.isEmpty(req.getLoginId())) {
				policyDetails=getTopPremiumCustomerDetailsByCompany( req);
			}else if(!StringUtils.isEmpty(req.getCompanyId()) &&  !StringUtils.isEmpty(req.getDivisionCode()) &&  StringUtils.isEmpty(req.getProductCode()) &&  StringUtils.isEmpty(req.getSourceCode()) &&
				    !StringUtils.isEmpty(req.getStartDate()) && !StringUtils.isEmpty(req.getEndDate()) && StringUtils.isEmpty(req.getLoginId())) {
				policyDetails=getTopPremiumCustomerDetailsByDivision( req);
			}else if(!StringUtils.isEmpty(req.getCompanyId()) &&  !StringUtils.isEmpty(req.getDivisionCode()) &&  !StringUtils.isEmpty(req.getProductCode()) &&  StringUtils.isEmpty(req.getSourceCode()) &&
				    !StringUtils.isEmpty(req.getStartDate()) && !StringUtils.isEmpty(req.getEndDate()) && StringUtils.isEmpty(req.getLoginId())) {
				policyDetails=getTopPremiumCustomerDetailsByProductwise( req);
			}
			if(policyDetails.isEmpty()) {
				res.setCommonResponse(policyDetails);
				res.setMessage("No Data");
				res.setIsError(false);
			}else {
				res.setCommonResponse(policyDetails);
				res.setMessage("Success");
				res.setIsError(false);		
				}
		}catch(Exception e) {
			e.printStackTrace();
			res.setMessage("Failed");
			res.setIsError(true);
			return res;
		}
		return res;
	}

	
	// Get Top 10 Premium Customers based on division in a company
	public List<PolicyDet> getTopPremiumCustomerDetailsByDivision(RenewalTrackReq req) {

		List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();

		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(PolicyDet.class, r.get("transactionId").alias("transactionId"),
					r.get("policyNumber").alias("policyNumber"),
					r.get("expiryDate").as(String.class).alias("expiryDate"),

					r.get("companyId").alias("companyId"), r.get("companyCode").alias("companyCode"),
					r.get("companyName").alias("companyName"),

					r.get("classCode").alias("classCode"), r.get("className").alias("className"),

//					r.get("productCode").alias("productCode"), r.get("productName").alias("productName"),

					r.get("divisionCode").alias("divisionCode"), r.get("divisionName").alias("divisionName"),

					r.get("departmentCode").alias("productCode"), r.get("departmentName").alias("productName"),

					r.get("businessType").alias("businessType"), r.get("businessName").alias("businessName"),

					r.get("endorsementNumber").alias("endorsementNumber"),
					r.get("fromDate").as(String.class).alias("fromDate"),
					r.get("renewalDate").as(String.class).alias("renewalDate"),

//					r.get("customerCode").alias("customerCode"), r.get("customerName").alias("customerName"),
					r.get("insuredCivilId").alias("insuredCivilId"), r.get("insuredMobile").alias("insuredMobile"),
					r.get("insuredEmailId").alias("insuredEmailId"),

					r.get("polAssrCode").alias("customerCode"), r.get("polAssrName").alias("customerName"),

					r.get("polSrcType").alias("polSrcType"), r.get("polSrcCode").alias("polSrcCode"),
					r.get("polSrcName").alias("polSrcName"),

					r.get("policySi").alias("policySi"), r.get("grossPremium").alias("grossPremium"),
					r.get("coverPremium").alias("coverPremium"), r.get("discountPremium").alias("discountPremium"),
					r.get("loadingPremium").alias("loadingPremium"),

					r.get("pvtCoverYn").alias("pvtCoverYn"), r.get("pvtCoverSi").alias("pvtCoverSi"),
					r.get("pvtCoverPremium").alias("pvtCoverPremium"),

					r.get("chargeAmount").alias("chargeAmount"), r.get("totalPremium").alias("totalPremium"),
					r.get("agBrokCommission").alias("agBrokCommission"),

					r.get("currentStatus").alias("currentStatus"), r.get("newPolicyNumber").alias("newPolicyNumber"),
					r.get("lossReason").alias("lossReason"), r.get("lossRemarks").alias("lossRemarks"),
					r.get("competitor").alias("competitor"),

					r.get("entryDate").as(String.class).alias("entryDate"),r.get("paymentType").alias("paymentType")));

			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			Predicate between = cb.between(r.get("expiryDate"), startTimestamp, endTimestamp);

			cq.where(cb.equal(r.get("companyId"), req.getCompanyId()),
					cb.equal(r.get("divisionCode"), req.getDivisionCode()),
					between);
			
			cq.orderBy(cb.desc(r.get("totalPremium")));

			policyDetails = em.createQuery(cq).setMaxResults(10).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return policyDetails;
	

	}
	
	
	// Get Top 10 Premium Customers Based on the product wise in a division
		public List<PolicyDet> getTopPremiumCustomerDetailsByProductwise(RenewalTrackReq req) {

			List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();

			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
				Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

				cq.select(cb.construct(PolicyDet.class, r.get("transactionId").alias("transactionId"),
						r.get("policyNumber").alias("policyNumber"),
						r.get("expiryDate").as(String.class).alias("expiryDate"),

						r.get("companyId").alias("companyId"), r.get("companyCode").alias("companyCode"),
						r.get("companyName").alias("companyName"),

						r.get("classCode").alias("classCode"), r.get("className").alias("className"),

//						r.get("productCode").alias("productCode"), r.get("productName").alias("productName"),

						r.get("divisionCode").alias("divisionCode"), r.get("divisionName").alias("divisionName"),

						r.get("departmentCode").alias("productCode"), r.get("departmentName").alias("productName"),

						r.get("businessType").alias("businessType"), r.get("businessName").alias("businessName"),

						r.get("endorsementNumber").alias("endorsementNumber"),
						r.get("fromDate").as(String.class).alias("fromDate"),
						r.get("renewalDate").as(String.class).alias("renewalDate"),

//						r.get("customerCode").alias("customerCode"), r.get("customerName").alias("customerName"),
						r.get("insuredCivilId").alias("insuredCivilId"), r.get("insuredMobile").alias("insuredMobile"),
						r.get("insuredEmailId").alias("insuredEmailId"),

						r.get("polAssrCode").alias("customerCode"), r.get("polAssrName").alias("customerName"),

						r.get("polSrcType").alias("polSrcType"), r.get("polSrcCode").alias("polSrcCode"),
						r.get("polSrcName").alias("polSrcName"),

						r.get("policySi").alias("policySi"), r.get("grossPremium").alias("grossPremium"),
						r.get("coverPremium").alias("coverPremium"), r.get("discountPremium").alias("discountPremium"),
						r.get("loadingPremium").alias("loadingPremium"),

						r.get("pvtCoverYn").alias("pvtCoverYn"), r.get("pvtCoverSi").alias("pvtCoverSi"),
						r.get("pvtCoverPremium").alias("pvtCoverPremium"),

						r.get("chargeAmount").alias("chargeAmount"), r.get("totalPremium").alias("totalPremium"),
						r.get("agBrokCommission").alias("agBrokCommission"),

						r.get("currentStatus").alias("currentStatus"), r.get("newPolicyNumber").alias("newPolicyNumber"),
						r.get("lossReason").alias("lossReason"), r.get("lossRemarks").alias("lossRemarks"),
						r.get("competitor").alias("competitor"),

						r.get("entryDate").as(String.class).alias("entryDate"),r.get("paymentType").alias("paymentType")));

				// Convert String to Timestamp
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
				LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

				// Convert LocalDate to Timestamp (start of day and end of day)
				Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
				Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

				// Add condition to criteria
				Predicate between = cb.between(r.get("expiryDate"), startTimestamp, endTimestamp);

				cq.where(cb.equal(r.get("companyId"), req.getCompanyId()),
						cb.equal(r.get("divisionCode"), req.getDivisionCode()),
						cb.equal(r.get("polSrcCode"), req.getSourceCode()), between);
				
				cq.orderBy(cb.desc(r.get("totalPremium")));

				policyDetails = em.createQuery(cq).setMaxResults(10).getResultList();

			} catch (Exception e) {
				e.printStackTrace();
			}

			return policyDetails;
		}
	
		// Get Top 10 Premium Customers Based on the Company
				public List<PolicyDet> getTopPremiumCustomerDetailsByCompany(RenewalTrackReq req) {

					List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();

					try {
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
						Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

						cq.select(cb.construct(PolicyDet.class, r.get("transactionId").alias("transactionId"),
								r.get("policyNumber").alias("policyNumber"),
								r.get("expiryDate").as(String.class).alias("expiryDate"),

								r.get("companyId").alias("companyId"), r.get("companyCode").alias("companyCode"),
								r.get("companyName").alias("companyName"),

								r.get("classCode").alias("classCode"), r.get("className").alias("className"),

//								r.get("productCode").alias("productCode"), r.get("productName").alias("productName"),

								r.get("divisionCode").alias("divisionCode"), r.get("divisionName").alias("divisionName"),

								r.get("departmentCode").alias("productCode"), r.get("departmentName").alias("productName"),

								r.get("businessType").alias("businessType"), r.get("businessName").alias("businessName"),

								r.get("endorsementNumber").alias("endorsementNumber"),
								r.get("fromDate").as(String.class).alias("fromDate"),
								r.get("renewalDate").as(String.class).alias("renewalDate"),

//								r.get("customerCode").alias("customerCode"), r.get("customerName").alias("customerName"),
								r.get("insuredCivilId").alias("insuredCivilId"), r.get("insuredMobile").alias("insuredMobile"),
								r.get("insuredEmailId").alias("insuredEmailId"),

								r.get("polAssrCode").alias("customerCode"), r.get("polAssrName").alias("customerName"),

								r.get("polSrcType").alias("polSrcType"), r.get("polSrcCode").alias("polSrcCode"),
								r.get("polSrcName").alias("polSrcName"),

								r.get("policySi").alias("policySi"), r.get("grossPremium").alias("grossPremium"),
								r.get("coverPremium").alias("coverPremium"), r.get("discountPremium").alias("discountPremium"),
								r.get("loadingPremium").alias("loadingPremium"),

								r.get("pvtCoverYn").alias("pvtCoverYn"), r.get("pvtCoverSi").alias("pvtCoverSi"),
								r.get("pvtCoverPremium").alias("pvtCoverPremium"),

								r.get("chargeAmount").alias("chargeAmount"), r.get("totalPremium").alias("totalPremium"),
								r.get("agBrokCommission").alias("agBrokCommission"),

								r.get("currentStatus").alias("currentStatus"), r.get("newPolicyNumber").alias("newPolicyNumber"),
								r.get("lossReason").alias("lossReason"), r.get("lossRemarks").alias("lossRemarks"),
								r.get("competitor").alias("competitor"),

								r.get("entryDate").as(String.class).alias("entryDate"),r.get("paymentType").alias("paymentType")));

						// Convert String to Timestamp
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
						LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

						// Convert LocalDate to Timestamp (start of day and end of day)
						Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
						Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

						// Add condition to criteria
						Predicate between = cb.between(r.get("expiryDate"), startTimestamp, endTimestamp);

						cq.where(cb.equal(r.get("companyId"), req.getCompanyId()), between);
						
						cq.orderBy(cb.desc(r.get("totalPremium")));

						policyDetails = em.createQuery(cq).setMaxResults(10).getResultList();

					} catch (Exception e) {
						e.printStackTrace();
					}

					return policyDetails;
				}

//				@Scheduled(cron = "0 0 15 * * ?")  	
//				@Transactional
//				public void expireOldPolicies() {
//					Timestamp oneMonthAgo = Timestamp.valueOf(LocalDateTime.now().minusMonths(1));
//				    int updatedCount = rppRepo.updateExpiredPolicies(oneMonthAgo);
//				    System.out.println("Updated " + updatedCount + " expired policies.");
//				}

				

/*				    @Scheduled(cron = "0 0 0 * * ?")
				    public void updatePolicyStatus() {
				        Timestamp oneMonthAgo = Timestamp.valueOf(LocalDateTime.now().minusMonths(1));
				        List<RenewPremiaPolicy> expiredPolicies = rppRepo.findExpiredPolicies(oneMonthAgo);

				        if (expiredPolicies == null || expiredPolicies.isEmpty()) {
				            System.out.println("No expired policies to update.");
				            return;
				        }

				        // Create a new list to store updated policies
				        List<RenewPremiaPolicy> updatedPolicies = new ArrayList<>();

				        for (RenewPremiaPolicy policy : expiredPolicies) {
				            if (policy != null && !"RR".equals(policy.getCurrentStatus())) {
				                policy.setCurrentStatus("RR");
				                updatedPolicies.add(policy);
				            }
				        }

				        if (!updatedPolicies.isEmpty()) {
				        	rppRepo.saveAll(updatedPolicies);
				            System.out.println("Updated " + updatedPolicies.size() + " policies to RR.");
				        } else {
				            System.out.println("No policy needed status update.");
				        }
				    }*/
				
@Override
public CommonRes insertVehicleInfo(RenewalVehicleReq req) {
		CommonRes res = new CommonRes();
		RenewVehicleInfo entity = null;
		try{
			if(StringUtils.isBlank(req.getRiskId()))
			{
			entity=vehiclerepo.findByPolicyNo(req.getPolicyNo());
			if(entity!=null)
			{
				String riskIdStr = entity.getRiskId();
				if (riskIdStr != null && !riskIdStr.trim().isEmpty()) {
				    int result = Integer.parseInt(riskIdStr.trim()) + 1;
				    entity.setRiskId(String.valueOf(result));
				} else {
				    entity.setRiskId("1"); 
				}
			}
			}
			else if(StringUtils.isNotBlank(req.getPolicyNo()) && StringUtils.isNotBlank(req.getRiskId()))
			{
			entity=vehiclerepo.findByPolicyNoAndRiskId(req.getPolicyNo(), req.getRiskId());	
			 if (entity != null) {
			        entity.setRiskId(req.getRiskId());
			    }
			}
			if(entity==null)
			{
				entity=new RenewVehicleInfo();
				entity.setRiskId(StringUtils.isBlank(req.getRiskId())?"1" : req.getRiskId());
			}
			entity.setPolicyNo((StringUtils.isBlank(req.getPolicyNo()))?"0": req.getPolicyNo());
			entity.setMake(StringUtils.isBlank(req.getMake())?" " :req.getMake());
			entity.setModel(StringUtils.isBlank(req.getModel())?" " :req.getModel());
			entity.setBodyType(StringUtils.isBlank(req.getBodyType())?" " :req.getBodyType());
			entity.setVehicleUsage(StringUtils.isBlank(req.getVehicleUsage())?" " :req.getVehicleUsage());
			entity.setPolicyType(StringUtils.isBlank(req.getPolicyType())?" " :req.getPolicyType());
			entity.setSumInsured(StringUtils.isBlank(req.getSumInsured())? 0.0 :Double.parseDouble(req.getSumInsured()));
			entity.setCreatedBy(StringUtils.isBlank(req.getCreatedBy())?" " :req.getCreatedBy());
			entity.setEntryDate(new Date());
			vehiclerepo.saveAndFlush(entity);
			res.setCommonResponse(entity);
			res.setErroCode(0);
			res.setMessage("Success");
			res.setIsError(false);
		}catch(Exception e){
				e.printStackTrace();
				res.setErroCode(1);
				res.setMessage("failed");
				res.setIsError(true);
		}
		return res;
	}
@Override
public CommonRes getRenewVehicl(String policyNo) {
	CommonRes res = new CommonRes();
	RenewVehicleInfo entity=null;
	try
	{ 	 entity = vehiclerepo.findByPolicyNo(policyNo);
		 if (entity!=null)
		 {
			 res.setCommonResponse(entity);
				res.setErroCode(0);
				res.setMessage("Success");
				res.setIsError(false);
		 }
		 else
		 {
			    res.setErroCode(1);
				res.setMessage("enter correct policyNo and RiskId");
				res.setIsError(true);
		 }
	}catch(Exception e){
		e.printStackTrace();
		res.setErroCode(1);
		res.setMessage("failed");
		res.setIsError(true);

}
	return res;
}
				
				
}
