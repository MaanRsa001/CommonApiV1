package com.maan.eway.integration.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.admin.res.GetMotorProtfolioActiveRes;
import com.maan.eway.admin.res.GetallPortfolioActiveRes;
import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.bean.CreditLimitDetail;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotCommDiscountDetail;
import com.maan.eway.bean.MotDriverDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PersonalInfoMarine;
import com.maan.eway.bean.PgithPolRiskAddlInfo;
import com.maan.eway.bean.PositionMaster;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.YiChargeDetail;
import com.maan.eway.bean.YiCoverDetail;
import com.maan.eway.bean.YiPolicyApproval;
import com.maan.eway.bean.YiPolicyDetail;
import com.maan.eway.bean.YiPremCal;
import com.maan.eway.bean.YiSectionDetail;
import com.maan.eway.bean.YiVatDetail;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.integration.req.GetAllPolicy;
import com.maan.eway.integration.req.IntegrationStateByPolicyReq;
import com.maan.eway.integration.req.PremiaGetReq;
import com.maan.eway.integration.res.CreditLimitDetailGetRes;
import com.maan.eway.integration.res.IntegrationStatgingRes;
import com.maan.eway.integration.res.MotCommDiscountDetailGetRes;
import com.maan.eway.integration.res.MotDriverDetailGetRes;
import com.maan.eway.integration.res.PgithPolRiskAddlInfoGetRes;
import com.maan.eway.integration.res.YiChargeDetailsGetRes;
import com.maan.eway.integration.res.YiCoverDetailsGetRes;
import com.maan.eway.integration.res.YiPolicyApprovalGetRes;
import com.maan.eway.integration.res.YiPolicyDetailsGetRes;
import com.maan.eway.integration.res.YiPremCalGetRes;
import com.maan.eway.integration.res.YiSectionDetailGetRes;
import com.maan.eway.integration.res.YiVatDetailGetRes;
import com.maan.eway.integration.service.IntegrationGetService;
import com.maan.eway.repository.CreditLimitDetailRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotDriverDetailRepository;
import com.maan.eway.repository.MotcommDiscountDetailRepository;
import com.maan.eway.repository.PgitPolRiskAddlInfoRepository;
import com.maan.eway.repository.YiChargeDetailRepository;
import com.maan.eway.repository.YiCoverDetailRepository;
import com.maan.eway.repository.YiPolicyApprovalRepository;
import com.maan.eway.repository.YiPolicyDetailRepository;
import com.maan.eway.repository.YiPremCalRepository;
import com.maan.eway.repository.YiSectionDetailRepository;
import com.maan.eway.repository.YiVatDetailRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class IntegrationGetServiceImpl implements IntegrationGetService {
	
	@Autowired
	private YiPolicyDetailRepository yiPolicyDetailsRepo;

	@Autowired
	private YiChargeDetailRepository yiChargeDetailsRepo;
	
	@Autowired
	private YiCoverDetailRepository yiCoverDetailsRepo;
	
	@Autowired
	private YiPolicyApprovalRepository yiPolicyApprovalRepo;
	
	@Autowired
	private YiPremCalRepository yiPremCalRepo;
	
	@Autowired
	private YiSectionDetailRepository yiSectionDetailsRepo;
	
	@Autowired
	private YiVatDetailRepository yiVatDetailsRepo;
	
	@Autowired
	private MotDriverDetailRepository motDriverDetailRepo;
	
	@Autowired
	private CreditLimitDetailRepository creditLimitDetailRepo;
	
	@Autowired
	private MotcommDiscountDetailRepository motCommDiscountDetailRepo;
	
	@Autowired
	private PgitPolRiskAddlInfoRepository pgitPolRiskAddlInfoRepo;
	
	@Autowired
	private HomePositionMasterRepository homerepo;
	

@PersistenceContext
private EntityManager em;

Gson json = new Gson(); 
private Logger log=LogManager.getLogger(IntegrationGetServiceImpl.class);


@Override
public GetallPortfolioActiveRes getAllPolicyDetails(GetAllPolicy req) {
	GetallPortfolioActiveRes resp = new GetallPortfolioActiveRes();
	List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
	

		int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
		int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

		List<PortfolioGridCriteriaRes> portfolioActiveList = new ArrayList<PortfolioGridCriteriaRes>();

		GetMotorProtfolioActiveRes response = getAllPolicy(req, limit, offset);
																													// "p"
																													// policy
		portfolioActiveList = response.getPortfolioList();
		resp.setCount(response.getCount());

		for (PortfolioGridCriteriaRes data : portfolioActiveList) {
			PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
			res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
			res.setClientName(data.getClientName());
			custRes.add(res);
		}
		resp.setCustRes(custRes);

	} catch (Exception e) {
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}
	return resp;
}
public synchronized GetMotorProtfolioActiveRes getAllPolicy(GetAllPolicy req,int limit,int offset) {
	List<PortfolioGridCriteriaRes> portfolio = new ArrayList<PortfolioGridCriteriaRes>();
	GetMotorProtfolioActiveRes resp = new GetMotorProtfolioActiveRes();
	try {
		resp.setCount(0l);
		Calendar cal = new GregorianCalendar();

		Date startDate = req.getStartDate();
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, 1);
		startDate = cal.getTime();

		Date endDate = req.getEndDate();
		cal.setTime(endDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		endDate = cal.getTime();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PortfolioGridCriteriaRes> query = cb.createQuery(PortfolioGridCriteriaRes.class);
		
		if(req.getProductId().equals("3") || req.getProductId().equals("11") ){
			
			// Find All
			Root<PositionMaster> m = query.from(PositionMaster.class);
			Root<PersonalInfoMarine> c = query.from(PersonalInfoMarine.class);
			Root<ProductMaster> pm = query.from(ProductMaster.class);
			
			// Select
			query.multiselect(
					// Customer Info
					c.get("customerid").as(String.class).alias("customerReferenceNo"),c.get("vatregno").alias("idNumber"),
					c.get("firstname").alias("clientName"), c.get("mobile").alias("mobileNo1"),
				//	c.get("isTaxExempted").alias("isTaxExempted"), c.get("taxExemptedId").alias("taxExemptedId"),
					// Vehicle Info
					m.get("companyid").as(String.class).alias("companyId"), m.get("productid").as(Integer.class).alias("productId"),
					pm.get("productName").alias("productName"), m.get("branchCode").alias("branchCode"),
					m.get("applicationno").as(String.class).alias("requestReferenceNo"), m.get("quoteno").as(String.class).alias("quoteNo"),
					m.get("customerid").as(String.class).alias("customerId"), m.get("inceptiondate").alias("inceptionDate"),
					m.get("expirydate").alias("expiryDate"), m.get("premium").as(BigDecimal.class).alias("overallPremiumLc"),
					m.get("premiumFc").as(BigDecimal.class).alias("overallPremiumFc"), m.get("policyno").alias("policyNo"),
					m.get("debitCustId").alias("debitAcNo"), m.get("debitTo").alias("debitTo"),
					m.get("debitToId").alias("debitToId"), m.get("debitNoteNo").alias("debitNoteNo"),
					m.get("debitnoteDate").alias("debitNoteDate"), m.get("creditTo").alias("creditTo"),
					m.get("crediToId").alias("creditToId"), m.get("creditNoteNo").alias("creditNo"),
					m.get("creditNoteDate").alias("creditDate"),// m.get("emiYn").alias("emiYn"),
				//	m.get("installmentPeriod").alias("installmentPeriod"),	m.get("noOfInstallment").alias("noOfInstallment"),
				//	m.get("paymentStatus").alias("paymentStatus"),m.get("emiPremium").alias("emiPremium"),
					m.get("policyEffectiveDate").alias("effectiveDate"),// m.get("currency").alias("currency"),
					m.get("originalPolicyNo").alias("originalPolicyNo"),
					m.get("coreintgstatus").alias("coreIntgStatus")

			);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entrydate")));
			
			// Where
			Predicate n1 = cb.equal(c.get("customerid"), m.get("customerid"));
			Predicate n2 = cb.equal(m.get("companyid"), req.getCompanyId());
			Predicate n3 = cb.equal(m.get("productid"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "P");
			Predicate n9 = cb.equal(m.get("integrationstatus"), "S"); 
			Predicate n7 = (cb.greaterThanOrEqualTo(m.get("entrydate"), startDate));
			Predicate n8 = (cb.lessThanOrEqualTo(m.get("entrydate"), endDate));
//			Predicate n10 = cb.equal(m.get("endtCount"), endtCount); 
			Predicate n11 = cb.notEqual(m.get("endttype"), "842"); 
			Predicate n12 = cb.isNull(m.get("endttype"));
			Predicate n13 = cb.or(n11, n12);
			Predicate n14 = cb.equal(pm.get("productId"), req.getProductId());
			Predicate n15 = cb.equal(pm.get("status"), "Y");
			query.where(n1, n2, n3, n4, n7,n8,n9,n13,n14,n15);

		}else {
			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);
					
			// Select
			query.multiselect(
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"), c.get("mobileNo1").alias("mobileNo1"),
					c.get("isTaxExempted").alias("isTaxExempted"), c.get("taxExemptedId").alias("taxExemptedId"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("productName").alias("productName"), m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo"), m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"), m.get("inceptionDate").alias("inceptionDate"),
					m.get("expiryDate").alias("expiryDate"), m.get("overallPremiumLc").alias("overallPremiumLc"),
					m.get("overallPremiumFc").alias("overallPremiumFc"), m.get("policyNo").alias("policyNo"),
					m.get("debitAcNo").alias("debitAcNo"), m.get("debitTo").alias("debitTo"),
					m.get("debitToId").alias("debitToId"), m.get("debitNoteNo").alias("debitNoteNo"),
					m.get("debitNoteDate").alias("debitNoteDate"), m.get("creditTo").alias("creditTo"),
					m.get("creditToId").alias("creditToId"), m.get("creditNo").alias("creditNo"),
					m.get("creditDate").alias("creditDate"), m.get("emiYn").alias("emiYn"),
					m.get("installmentPeriod").alias("installmentPeriod"),
					m.get("noOfInstallment").alias("noOfInstallment"),m.get("paymentStatus").alias("paymentStatus"),
					m.get("emiPremium").alias("emiPremium"),
					m.get("effectiveDate").alias("effectiveDate"), m.get("currency").alias("currency"),
					m.get("originalPolicyNo").alias("originalPolicyNo"),
					m.get("coreIntgStatus").alias("coreIntgStatus")

			);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));

//			// Endt Count Max Filter
//			Subquery<Long> endtCount = query.subquery(Long.class);
//			Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
//			endtCount.select(cb.min(ocpm1.get("endtCount")));
//			Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
//			Predicate a2 = cb.equal(ocpm1.get("status"), m.get("status"));
//			endtCount.where(a1, a2);

			// Where
			Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "P");
			Predicate n9 = cb.equal(m.get("integrationStatus"), "S"); 
			Predicate n7 = (cb.greaterThanOrEqualTo(m.get("entryDate"), startDate));
			Predicate n8 = (cb.lessThanOrEqualTo(m.get("entryDate"), endDate));
//			Predicate n10 = cb.equal(m.get("endtCount"), endtCount); 
			Predicate n11 = cb.notEqual(m.get("endtTypeId"), "842"); 
			Predicate n12 = cb.isNull(m.get("endtTypeId"));
			Predicate n13 = cb.or(n11, n12);
			query.where(n1, n2, n3, n4, n7,n8,n9,n13);

		}
				
				
		// Get Result
		TypedQuery<PortfolioGridCriteriaRes> result = em.createQuery(query);
//		result.setFirstResult(limit * offset);
//		result.setMaxResults(offset);
		portfolio = result.getResultList();

		resp.setPortfolioList(portfolio);
		resp.setCount(totalcountpolicy(req,startDate,endDate));
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}
	return resp;
}
private Long totalcountpolicy(GetAllPolicy req,Date startDate,Date endDate) {
	Long count = 0l;
	try {	
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = cb.createQuery(Long.class);
	
		// Find All
		Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
		Root<PersonalInfo> c = query.from(PersonalInfo.class);
	
		// Select
		query.multiselect(cb.count(m));
	
	
		// Where
		Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
		Predicate n2 = cb.equal(m.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
		Predicate n4 = cb.equal(m.get("status"), "P");
		Predicate n9 = cb.equal(m.get("integrationStatus"), "S"); 
		Predicate n7 = (cb.greaterThanOrEqualTo(m.get("entryDate"), startDate));
		Predicate n8 = (cb.lessThanOrEqualTo(m.get("entryDate"), endDate));
		Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");   
		Predicate n12 = cb.isNull(m.get("endtTypeId"));     
		Predicate n13 = cb.or(n11,n12);
			
		query.where(n1, n2, n3, n4,n7,n8,n9,n13);

		TypedQuery<Long> result = em.createQuery(query);
		List<Long> val = result.getResultList();
			
				if(val.size()>0)
					count = val.get(0);


} catch (Exception e) {
	e.printStackTrace();
	log.info("Log Details" + e.getMessage());
	return null;
}
return count;
}




//YiPolicyDetails Get
@Override
public List<YiPolicyDetailsGetRes> getYiPolicyDetails(PremiaGetReq req) {
	List<YiPolicyDetailsGetRes> resList = new ArrayList<YiPolicyDetailsGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
			policyNo=homeData.getPolicyNo();
		}
		List<YiPolicyDetail> yipolicyList=yiPolicyDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yipolicyList!=null && yipolicyList.size()>0) {
		for (YiPolicyDetail data : yipolicyList) {
			YiPolicyDetailsGetRes res = new YiPolicyDetailsGetRes();

		res = mapper.map(data, YiPolicyDetailsGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiChargeDetailsGetRes> getYiChargeDetails(PremiaGetReq req) {
	List<YiChargeDetailsGetRes> resList = new  ArrayList<YiChargeDetailsGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
				try {
					
					Date today = new Date();
					Calendar cal = new GregorianCalendar();
					cal.setTime(today);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 1);
					today = cal.getTime();
					
					
					String policyNo="";
					HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
					if(homeData!=null) {
					policyNo=homeData.getPolicyNo();
					}
					List<YiChargeDetail>  yichargeList=yiChargeDetailsRepo.findByQuotationPolicyNo(policyNo);
					if(yichargeList!=null && yichargeList.size()>0) {
					for (YiChargeDetail data : yichargeList) {
						YiChargeDetailsGetRes res = new  YiChargeDetailsGetRes();

					res = mapper.map(data, YiChargeDetailsGetRes.class); 
					res.setRequestTime(data.getRequestTime());
					res.setResponseTime(data.getResponseTime());
					
					resList.add(res);
					}
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is --> " + e.getMessage());
					return null;
				}
				return resList;

}


@Override
public List<YiCoverDetailsGetRes> getYiCoverDetails(PremiaGetReq req) {
	List<YiCoverDetailsGetRes> resList = new ArrayList<YiCoverDetailsGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiCoverDetail> yicoverList=yiCoverDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yicoverList!=null && yicoverList.size()>0) {
		for (YiCoverDetail data : yicoverList) {
			YiCoverDetailsGetRes res = new YiCoverDetailsGetRes();

		res = mapper.map(data, YiCoverDetailsGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiPolicyApprovalGetRes> getYiPolicyApproval(PremiaGetReq req) {
	List<YiPolicyApprovalGetRes> resList = new ArrayList<YiPolicyApprovalGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiPolicyApproval> yipolicyapprovalList=yiPolicyApprovalRepo.findByQuotationPolicyNo(policyNo);
		if(yipolicyapprovalList!=null && yipolicyapprovalList.size()>0) {
		for (YiPolicyApproval data : yipolicyapprovalList) {
			YiPolicyApprovalGetRes res = new YiPolicyApprovalGetRes();

		res = mapper.map(data, YiPolicyApprovalGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiPremCalGetRes> getYiPremCal(PremiaGetReq req) {
	List<YiPremCalGetRes> resList = new ArrayList<YiPremCalGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiPremCal> yipremcalList=yiPremCalRepo.findByQuotationPolicyNo(policyNo);
		if(yipremcalList!=null && yipremcalList.size()>0) {
		for (YiPremCal data : yipremcalList) {
			YiPremCalGetRes res = new YiPremCalGetRes();

		res = mapper.map(data, YiPremCalGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiSectionDetailGetRes> getYiSectionDetail(PremiaGetReq req) {
	List<YiSectionDetailGetRes> resList = new ArrayList<YiSectionDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiSectionDetail> yisectiondetailList=yiSectionDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yisectiondetailList!=null && yisectiondetailList.size()>0) {
		for (YiSectionDetail data : yisectiondetailList) {
			YiSectionDetailGetRes res = new YiSectionDetailGetRes();

		res = mapper.map(data, YiSectionDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<YiVatDetailGetRes> getVatDetail(PremiaGetReq req) {
	List<YiVatDetailGetRes> resList = new ArrayList<YiVatDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<YiVatDetail> yivatdetailList=yiVatDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(yivatdetailList!=null && yivatdetailList.size()>0) {
		for (YiVatDetail data : yivatdetailList) {
			YiVatDetailGetRes res = new YiVatDetailGetRes();

		res = mapper.map(data, YiVatDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<MotDriverDetailGetRes> getMotDriverDetail(PremiaGetReq req) {
	List<MotDriverDetailGetRes> resList = new ArrayList<MotDriverDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		}
		List<MotDriverDetail> motdriverdetailList=motDriverDetailRepo.findByQuotationPolicyNo(policyNo);
		if(motdriverdetailList!=null && motdriverdetailList.size()>0) {
		for (MotDriverDetail data : motdriverdetailList) {
			MotDriverDetailGetRes res = new MotDriverDetailGetRes();

		res = mapper.map(data, MotDriverDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<CreditLimitDetailGetRes> getCreditLimitDetail(PremiaGetReq req) {
	List<CreditLimitDetailGetRes> resList = new ArrayList<CreditLimitDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String customerId="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
			customerId=homeData.getCustomerId();
		}
		List<CreditLimitDetail> creditlimitdetailList=creditLimitDetailRepo.findByRequestreferenceno(customerId);
		if(creditlimitdetailList!=null && creditlimitdetailList.size()>0) {
		for (CreditLimitDetail data : creditlimitdetailList) {
			CreditLimitDetailGetRes res = new CreditLimitDetailGetRes();

		res = mapper.map(data, CreditLimitDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<MotCommDiscountDetailGetRes> getMotCommDiscountDetail(PremiaGetReq req) {
	List<MotCommDiscountDetailGetRes> resList = new ArrayList<MotCommDiscountDetailGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		
		}
		List<MotCommDiscountDetail> motcommdiscountdetailList=motCommDiscountDetailRepo.findByQuotationPolicyNo(policyNo);
		if(motcommdiscountdetailList!=null && motcommdiscountdetailList.size()>0) {
		for (MotCommDiscountDetail data : motcommdiscountdetailList) {
			MotCommDiscountDetailGetRes res = new MotCommDiscountDetailGetRes();

		res = mapper.map(data, MotCommDiscountDetailGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
}


@Override
public List<PgithPolRiskAddlInfoGetRes> getPgithPolRiskAddlInfo(PremiaGetReq req) {
	List<PgithPolRiskAddlInfoGetRes> resList = new ArrayList<PgithPolRiskAddlInfoGetRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		
		String policyNo="";
		HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
		if(homeData!=null) {
		policyNo=homeData.getPolicyNo();
		
		}
		List<PgithPolRiskAddlInfo> pgithpolriskaddlinfoList=pgitPolRiskAddlInfoRepo.findByQuotationPolicyNo(policyNo);
		if(pgithpolriskaddlinfoList!=null && pgithpolriskaddlinfoList.size()>0) {
		for (PgithPolRiskAddlInfo data : pgithpolriskaddlinfoList) {
			PgithPolRiskAddlInfoGetRes res = new PgithPolRiskAddlInfoGetRes();

		res = mapper.map(data, PgithPolRiskAddlInfoGetRes.class);
		res.setRequestTime(data.getRequestTime());
		res.setResponseTime(data.getResponseTime());
		
		resList.add(res);
		}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return resList;
	
}
@Override
public List<IntegrationStatgingRes> getIntegrationStageDetails(IntegrationStateByPolicyReq req) {
	List<IntegrationStatgingRes> res=new ArrayList<IntegrationStatgingRes>();
		List<Tuple>portfolio=null;
		if("Date".equalsIgnoreCase(req.getSearchType())) {
			portfolio=getPolicyDetails(req);
			for (Tuple tuple : portfolio) {
				IntegrationStatgingRes resp=new IntegrationStatgingRes();
				String policyNo=tuple.get("policyNo")==null?"":tuple.get("policyNo").toString();
				resp=GetIntegrationStatusSearch(policyNo);
				res.add(resp);
			}
		}else {
			IntegrationStatgingRes resp=new IntegrationStatgingRes();
			resp=GetIntegrationStatusSearch(req.getPolicyNo());
			res.add(resp);
		}
	return res;
}
private IntegrationStatgingRes GetIntegrationStatusSearch(String policyNo) {
	IntegrationStatgingRes resp=new IntegrationStatgingRes();
		resp.setQuotationPolicyNo(policyNo);
		List<YiPolicyDetail>policyList=yiPolicyDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(policyList)) {
			resp.setPolicyStatus("Success");
		}else {
			resp.setPolicyStatus("Failed");
		}
		
		List<YiSectionDetail>sectionList=yiSectionDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(sectionList)) {
			resp.setSectionStatus("Success");
		}else {
			resp.setSectionStatus("Failed");
		}
		
		List<PgithPolRiskAddlInfo>policRiskList=pgitPolRiskAddlInfoRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(policRiskList)) {
			resp.setPolicyRiskStatus("Success");
		}else {
			resp.setPolicyRiskStatus("Failed");
		}
		
		List<MotDriverDetail>driverList=motDriverDetailRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(driverList)) {
			resp.setDriverStatus("Success");
		}else {
			resp.setDriverStatus("Failed");
		}
		
		List<YiCoverDetail>coverList=yiCoverDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(coverList)) {
			resp.setCoverStatus("Success");
		}else {
			resp.setCoverStatus("Failed");
		}
		
		List<MotCommDiscountDetail>discountList=motCommDiscountDetailRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(discountList)) {
			resp.setDiscountStatus("Success");
		}else {
			resp.setDiscountStatus("Failed");
		}
		List<YiChargeDetail>chargeList=yiChargeDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(chargeList)) {
			resp.setChargeStatus("Success");
		}else {
			resp.setChargeStatus("Failed");
		}
		List<YiVatDetail>vatList=yiVatDetailsRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(vatList)) {
			resp.setVatStatus("Success");
		}else {
			resp.setVatStatus("Failed");
		}
		List<YiPremCal>premiumList=yiPremCalRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(premiumList)) {
			resp.setPremiumStatus("Success");
		}else {
			resp.setPremiumStatus("Failed");
		}
		List<YiPremCal>policyApproveList=yiPremCalRepo.findByQuotationPolicyNo(policyNo);
		if(!CollectionUtils.isEmpty(policyApproveList)) {
			resp.setPolicyApproveStatus("Success");
		}else {
			resp.setPolicyApproveStatus("Failed");
		}
		
	return resp;
}

public List<Tuple>getPolicyDetails(IntegrationStateByPolicyReq req){
	List<Tuple>portfolio=null;
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
	
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
	
		// Find All
		Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
	
		// Select
		query.multiselect(m.get("companyId").alias("companyId"), m.get("policyNo").alias("policyNo"));
	
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(m.get("entryDate")));
	
		// Where
		Predicate n1 = cb.equal(m.get("companyId"), req.getCompanyId());
		Predicate n2 = cb.equal(m.get("status"), "P");
		Predicate n3 = (cb.greaterThanOrEqualTo(m.get("entryDate"), startDate));
		Predicate n4 = (cb.lessThanOrEqualTo(m.get("entryDate"), endDate));
		
		query.where(n1, n2, n3, n4);
		
		// Get Result
		TypedQuery<Tuple> result = em.createQuery(query);
		portfolio = result.getResultList();
	}catch (Exception e) {
		e.printStackTrace();;
	}
	return portfolio;
}
}