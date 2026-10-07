package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.res.GetBuildingAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.GetMotorAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.GetMotorProtfolioActiveRes;
import com.maan.eway.admin.res.GetTravelAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.GetallPortfolioActiveRes;
import com.maan.eway.admin.res.MotorGridCriteriaAdminRes;
import com.maan.eway.admin.res.MotorGridCriteriaRes;
import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.admin.res.ReferalCommonCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaAdminRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SessionMaster;
import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetApproverListReq;
import com.maan.eway.common.req.GetExistingBrokerListReq;
import com.maan.eway.common.req.GetPaymentStatusReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.GetallReferralPendingDetailsRes;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.req.PortFolioGridReq;
import com.maan.eway.common.req.PortfolioSearchReq;
import com.maan.eway.common.req.RegSearchReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SearchBrokerPolicyReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.AdminPendingGridListRes;
import com.maan.eway.common.res.AdminPendingGridRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CountRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.GetApproverListRes;
import com.maan.eway.common.res.GetCommonReferalDetailsRes;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetExistingBrokerRes;
import com.maan.eway.common.res.GetMotorProtfolioPendingRes;
import com.maan.eway.common.res.GetMotorReferalDetailsRes;
import com.maan.eway.common.res.GetPaymentStatusRes;
import com.maan.eway.common.res.GetRegNumberQuoteRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.GetTravelReferalDetailsRes;
import com.maan.eway.common.res.GetTravelRejectedQuoteDetailsRes;
import com.maan.eway.common.res.GetallExistingRejectedLapsedRes;
import com.maan.eway.common.res.GetallPolicyReportsRes;
import com.maan.eway.common.res.GetallPortfolioPendingRes;
import com.maan.eway.common.res.GetallReferralApprovedDetailsRes;
import com.maan.eway.common.res.GetallReferralDetailsCommonRes;
import com.maan.eway.common.res.GetallReferralRejectedDetailsRes;
import com.maan.eway.common.res.LoginPolicyCountTupleRes;
import com.maan.eway.common.res.LoginQuoteCriteriaResponse;
import com.maan.eway.common.res.PaymentStausRes;
import com.maan.eway.common.res.PortFolioAdminTupleRes;
import com.maan.eway.common.res.PortFolioDashBoardRes;
import com.maan.eway.common.res.PortfolioAdminGridRes;
import com.maan.eway.common.res.PortfolioAdminPendingRes;
import com.maan.eway.common.res.PortfolioBrokerListRes;
import com.maan.eway.common.res.PortfolioByRegNoRes;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.common.res.PortfolioGridRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.PortfolioSearchDataRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.common.res.RegNumberRes;
import com.maan.eway.common.res.RegirsterSearchCriteeriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.res.RevertGridListRes;
import com.maan.eway.common.res.RevertGridRes;
import com.maan.eway.common.res.TravelQuoteCriteriaRes;
import com.maan.eway.common.res.TravelQuoteCriteriaResponse;
import com.maan.eway.common.res.TravelRejectCriteriaRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.res.ViewLoginDetailsRes;
import com.maan.eway.common.service.BuildingGridService;
import com.maan.eway.common.service.CommonGridService;
import com.maan.eway.common.service.GridService;
import com.maan.eway.common.service.LifeGridService;
import com.maan.eway.common.service.MotorGridService;
import com.maan.eway.common.service.TravelGridService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.reinsurance.req.ReInsuranceQuoteReq;
import com.maan.eway.reinsurance.service.ReinsuranceService;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.SessionMasterRepository;
import com.maan.eway.repository.UWReferralDetailsRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.thread.MyTaskList;

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
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class GridServiceImpl implements GridService {

	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Autowired
	private EServiceMotorDetailsRepository repo;

	@Autowired
	private EserviceCustomerDetailsRepository custRepo;

	@Autowired
	private EserviceCommonDetailsRepository commonRepo;

	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;

	@Autowired
	private MotorGridService motService;

	@Autowired
	private TravelGridService traService;

	@Autowired
	private BuildingGridService buiService;

	@Autowired
	private CommonGridService commonService;

	@Autowired
	private EserviceTravelDetailsRepository travelRepo;

	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private LifeGridService lifeService;

	@Autowired
	private LoginMasterRepository loginRepo;

	@Autowired
	private SessionMasterRepository sessionRepo;

	@Autowired
	private PaymentDetailRepository paymentdetailrepo;

	@Autowired
	private PaymentInfoRepository paymentinforepo;

	@Autowired
	private MotorDataDetailsRepository motDataRepo;

	@Autowired
	private UWReferralDetailsRepository uwReferalDetailsRepo;
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ReinsuranceService reinsuranceService;

	private Logger log = LogManager.getLogger(GridServiceImpl.class);

	@Override
	public GetallExistingRejectedLapsedRes getDashBoardallExistfromHPM(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		QuoteCriteriaResponse cres = new QuoteCriteriaResponse();

		try {
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime(); // today
			cal.add(Calendar.DAY_OF_MONTH, -10);
			Date before30 = cal.getTime();
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<QuoteCriteriaRes> extingQuoteList = new ArrayList<QuoteCriteriaRes>();

				cres = motService.getCustomerReferenceNoMapDB(req, req.getInsuranceId(), req.getProductId(), before30,
						today, req.getApplicationId(), req.getBdmCode(), req.getBranchCode(), limit, offset);
				List<QuoteCriteriaRes> extingQuoteListMotor = new ArrayList<QuoteCriteriaRes>(); 
				extingQuoteListMotor = cres.getQuoteRes();
				extingQuoteList.addAll(extingQuoteListMotor);

//				TravelQuoteCriteriaResponse tcres = traService.getTravelExistingQuoteDetails(req, before30, today,limit, offset);
//				List<TravelQuoteCriteriaRes> textingQuoteList = new ArrayList<>();
//				textingQuoteList = tcres.getQuoteRes();
//
//				for (TravelQuoteCriteriaRes data : textingQuoteList) {
//					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
//					dozerMapper.map(data, res);
//					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
//					custRes.add(res);
//				}
//				resp.setCustomerDetailsRes(custRes);
//				resp.setTotalCount(tcres.getTotalCount());
			

//				cres = lifeService.getLifeExistingQuoteDetails(req, before30, today, limit, offset);
//				List<QuoteCriteriaRes> extingQuoteListlife = new ArrayList<QuoteCriteriaRes>(); 
//				 extingQuoteListlife = cres.getQuoteRes();
//				extingQuoteList.addAll(extingQuoteListlife);
				

			
				cres = motService.getNonMotorProductDashBoard(req, before30, today, limit, offset);
				List<QuoteCriteriaRes> extingQuoteListAH = new ArrayList<QuoteCriteriaRes>(); 
				extingQuoteListAH = cres.getQuoteRes();
				extingQuoteList.addAll(extingQuoteListAH);
				
				extingQuoteList.sort(Comparator.comparing(
					    QuoteCriteriaRes::getPolicyStartDate,
					    Comparator.nullsLast(Date::compareTo)
					).reversed());
				if (extingQuoteList != null) {
						for (QuoteCriteriaRes data : extingQuoteList) {
							EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
							dozerMapper.map(data, res);
							res.setQuoteNo(("99999".equalsIgnoreCase(data.getQuoteNo()))?" ":data.getQuoteNo());					
							res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
								    (data.getClientName() == null ? "" : data.getClientName()) + " " +
								    (data.getLastName() == null ? "" : data.getLastName()));
							custRes.add(res);
						}
						resp.setCustomerDetailsRes(custRes);
						resp.setTotalCount(Long.valueOf(extingQuoteList.size()));
					}
				

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}
	
	
	
	@Override
	public GetallExistingRejectedLapsedRes getallExistfromHPM(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		QuoteCriteriaResponse cres = new QuoteCriteriaResponse();
		
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -90);
			Date before30 = cal.getTime();
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<QuoteCriteriaRes> extingQuoteList = new ArrayList<QuoteCriteriaRes>();

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				cres= motService.getCustomerReferenceNoMap(req, req.getInsuranceId(), req.getProductId(), before30, today,
						req.getApplicationId(), req.getBdmCode(), req.getBranchCode(),limit, offset);
					extingQuoteList = cres.getQuoteRes();

			}else if (product.getMotorYn().equalsIgnoreCase("H")&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				TravelQuoteCriteriaResponse tcres = traService.getTravelExistingQuoteDetails(req, before30, today,
						limit, offset);
				List<TravelQuoteCriteriaRes> textingQuoteList = new ArrayList<>();
				textingQuoteList = tcres.getQuoteRes();

				for (TravelQuoteCriteriaRes data : textingQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					dozerMapper.map(data, res);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(tcres.getTotalCount());
			}	
			else if (product.getMotorYn().equalsIgnoreCase("L")) {

				cres = lifeService.getLifeExistingQuoteDetails(req, before30, today, limit, offset);

				extingQuoteList = cres.getQuoteRes();
			
			
		 } else if (product.getMotorYn().equalsIgnoreCase("A")) {
			cres= motService.getNonMotorProduct(req,before30,today,limit,offset);
		    extingQuoteList = cres.getQuoteRes();
		 } else
		 {

				cres= motService.getNonMotorProduct(req,before30,today,limit,offset);
			    extingQuoteList = cres.getQuoteRes();
			 
		 }
				// Common
			
			if (!req.getProductId().equalsIgnoreCase(travelProductId)) {

				if (extingQuoteList != null) {
					for (QuoteCriteriaRes data : extingQuoteList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						dozerMapper.map(data, res);
						res.setQuoteNo(("99999".equalsIgnoreCase(data.getQuoteNo()))?" ":data.getQuoteNo());					
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
					resp.setCustomerDetailsRes(custRes);
					resp.setTotalCount(cres.getTotalCount());
				}
			}
			
		}catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
		return resp;
	}
	@Override
	public GetallExistingRejectedLapsedRes getallExistingQuoteDetails(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		QuoteCriteriaResponse cres = new QuoteCriteriaResponse();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -90);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<QuoteCriteriaRes> extingQuoteList = new ArrayList<QuoteCriteriaRes>();

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) {
			cres= motService.getCustomerReferenceNoMap(req, req.getInsuranceId(), req.getProductId(), before30, today,
					req.getApplicationId(), req.getBdmCode(), req.getBranchCode(),limit, offset);
				extingQuoteList = cres.getQuoteRes();

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				TravelQuoteCriteriaResponse tcres = traService.getTravelExistingQuoteDetails(req, before30, today,
						limit, offset);
				List<TravelQuoteCriteriaRes> textingQuoteList = new ArrayList<>();
				textingQuoteList = tcres.getQuoteRes();

				for (TravelQuoteCriteriaRes data : textingQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					dozerMapper.map(data, res);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(tcres.getTotalCount());

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				//cres = buiService.getBuildingExistingQuoteDetails(req, before30, today, limit, offset);

				//extingQuoteList = cres.getQuoteRes();
				cres= motService.getCustomerReferenceNoMapNonMotor(req, req.getInsuranceId(), req.getProductId(), before30, today,
						req.getApplicationId(), req.getBdmCode(), req.getBranchCode(),limit, offset);
					extingQuoteList = cres.getQuoteRes();
				// Common
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {

				cres = lifeService.getLifeExistingQuoteDetails(req, before30, today, limit, offset);

				extingQuoteList = cres.getQuoteRes();
				// Common
			} else {
				cres = commonService.getCommonExistingQuoteDetails(req, before30, today, limit, offset);
				extingQuoteList = cres.getQuoteRes();
			}

			if (!req.getProductId().equalsIgnoreCase(travelProductId)) {

				if (extingQuoteList != null) {
					for (QuoteCriteriaRes data : extingQuoteList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						dozerMapper.map(data, res);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
					resp.setCustomerDetailsRes(custRes);
					resp.setTotalCount(cres.getTotalCount());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
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

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public GetallExistingRejectedLapsedRes getallLapsedQuoteDetails(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		QuoteCriteriaResponse cres = new QuoteCriteriaResponse();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			List<QuoteCriteriaRes> lapsedQuoteList = new ArrayList<QuoteCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				cres = motService.getMotorLapsedQuoteDetails(req, before30, limit, offset);
				lapsedQuoteList = cres.getQuoteRes();

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				TravelQuoteCriteriaResponse tcres = traService.getTravelLapsedQuoteDetails(req, before30, limit,
						offset);
				List<TravelQuoteCriteriaRes> textingQuoteList = new ArrayList<TravelQuoteCriteriaRes>();
				textingQuoteList = tcres.getQuoteRes();

				for (TravelQuoteCriteriaRes data : textingQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					dozerMapper.map(data, res);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(tcres.getTotalCount());

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {

				cres = buiService.getBuildingLapsedQuoteDetails(req, before30, limit, offset);
				lapsedQuoteList = cres.getQuoteRes();

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				cres = lifeService.getLifeLapsedQuoteDetails(req, before30, limit, offset);
				lapsedQuoteList = cres.getQuoteRes();

			} else {
				cres = commonService.getCommonLapsedQuoteDetails(req, before30, limit, offset);
				lapsedQuoteList = cres.getQuoteRes();
			}

			if (!req.getProductId().equalsIgnoreCase(travelProductId)) {
				for (QuoteCriteriaRes data : lapsedQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(cres.getTotalCount());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public GetallExistingRejectedLapsedRes getallRejectedQuoteDetails(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		GetRejectedQuoteDetailsRes cres = new GetRejectedQuoteDetailsRes();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<RejectCriteriaRes> rejectedQuoteList = new ArrayList<RejectCriteriaRes>();
			List<TravelRejectCriteriaRes> trejectedQuoteList = new ArrayList<TravelRejectCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				cres = motService.getMotorRejectedQuoteDetails(req, before30, today, limit, offset);
				rejectedQuoteList = cres.getQuoteRes();

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				GetTravelRejectedQuoteDetailsRes tcres = traService.getTravelRejectedQuoteDetails(req, before30, today,
						limit, offset);
				trejectedQuoteList = tcres.getQuoteRes();
				for (TravelRejectCriteriaRes data : trejectedQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(tcres.getTotalCount());

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				cres = buiService.getBuildingRejectedQuoteDetails(req, before30, today, limit, offset);
				rejectedQuoteList = cres.getQuoteRes();
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				cres = lifeService.getLifeRejectedQuoteDetails(req, before30, today, limit, offset);
				rejectedQuoteList = cres.getQuoteRes();

			} else {
				cres = commonService.getCommonRejectedQuoteDetails(req, before30, today, limit, offset);
				rejectedQuoteList = cres.getQuoteRes();
			}

			if (!req.getProductId().equalsIgnoreCase(travelProductId)) {

				for (RejectCriteriaRes data : rejectedQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(cres.getTotalCount());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	// Referral Grids
	@Override
	public GetallReferralPendingDetailsRes getallReferralPendingDetails(ExistingQuoteReq req) {
		GetallReferralPendingDetailsRes resp = new GetallReferralPendingDetailsRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> referralPendingList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				GetMotorReferalDetailsRes response = motService.getMotorReferalDetails(req, limit, offset, "RP");
				List<MotorGridCriteriaRes> referralPendingList2 = response.getMotorGridCriteriaResRes();

				resp.setTotalCount(String.valueOf(response.getTotalCount()));

				for (MotorGridCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				GetMotorReferalDetailsRes response = lifeService.getLifeReferalDetails(req, limit, offset, "RP");
				List<MotorGridCriteriaRes> referralPendingList2 = response.getMotorGridCriteriaResRes();

				resp.setTotalCount(String.valueOf(response.getTotalCount()));

				for (MotorGridCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}

			}

			else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				GetTravelReferalDetailsRes response = traService.getTravelReferalDetails(req, limit, offset, "RP");
				referralPendingList = response.getReferalGridCriteriaRes();

				resp.setTotalCount(String.valueOf(response.getTotalCount()));

				for (ReferalGridCriteriaRes data : referralPendingList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
						    (data.getClientName() == null ? "" : data.getClientName()) + " " +
						    (data.getLastName() == null ? "" : data.getLastName()));
					custRes.add(res);
				}

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				GetTravelReferalDetailsRes response = buiService.getBuildingReferalDetails(req, limit, offset, "RP");
				referralPendingList = response.getReferalGridCriteriaRes();

				if (referralPendingList != null) {
					resp.setTotalCount(String.valueOf(response.getTotalCount()));

					for (ReferalGridCriteriaRes data : referralPendingList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				} else {

					GetCommonReferalDetailsRes response1 = commonService.getCommonReferalDetails(req, limit, offset,
							"RP");

					List<ReferalCommonCriteriaRes> referralPendingList2 = response1.getReferalCommonCriteriaRes();

					resp.setTotalCount(String.valueOf(response1.getTotalCount()));

					for (ReferalCommonCriteriaRes data : referralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}

				}

			} else {
				GetCommonReferalDetailsRes response = commonService.getCommonReferalDetails(req, limit, offset, "RP");

				List<ReferalCommonCriteriaRes> referralPendingList2 = response.getReferalCommonCriteriaRes();

				resp.setTotalCount(String.valueOf(response.getTotalCount()));

				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
						    (data.getClientName() == null ? "" : data.getClientName()) + " " +
						    (data.getLastName() == null ? "" : data.getLastName()));
					custRes.add(res);
				}

			}
			resp.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public GetallReferralApprovedDetailsRes getallReferralApprovedDetails(ExistingQuoteReq req) {
		GetallReferralApprovedDetailsRes response = new GetallReferralApprovedDetailsRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
//			List<String> branches = new ArrayList<String>();
//			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {
//
//				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);
//
//				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
//						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
//				if (branches.size() <= 0) {
//					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
//
//				}
//
//			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//				branches.add(req.getBrokerBranchCode());
//			} else {
//				branches.add(req.getBranchCode());
//			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> referralApprovedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {

				GetMotorReferalDetailsRes resp = motService.getMotorReferalDetails(req, limit, offset, "RA");
				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();
				if (List2 != null && !List2.isEmpty()) {
					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {

				GetMotorReferalDetailsRes resp = lifeService.getLifeReferalDetails(req, limit, offset, "RA");
				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();
				if (List2 != null && !List2.isEmpty()) {
					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				GetTravelReferalDetailsRes resp = traService.getTravelReferalDetails(req, limit, offset, "RA");
				referralApprovedList = resp.getReferalGridCriteriaRes();
				if (referralApprovedList != null && !referralApprovedList.isEmpty()) {
					for (ReferalGridCriteriaRes data : referralApprovedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				GetTravelReferalDetailsRes resp = buiService.getBuildingReferalDetails(req, limit, offset, "RA");

				referralApprovedList = resp.getReferalGridCriteriaRes();
				if (referralApprovedList != null && !referralApprovedList.isEmpty()) {
					for (ReferalGridCriteriaRes data : referralApprovedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				GetCommonReferalDetailsRes resp1 = commonService.getCommonReferalDetails(req, limit, offset, "RA");
				List<ReferalCommonCriteriaRes> referralPendingList2 = resp1.getReferalCommonCriteriaRes();
				if (referralPendingList2 != null && !referralPendingList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : referralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount() + resp1.getTotalCount()));

			} else {

				GetCommonReferalDetailsRes resp = commonService.getCommonReferalDetails(req, limit, offset, "RA");
				List<ReferalCommonCriteriaRes> referralPendingList2 = resp.getReferalCommonCriteriaRes();
				if (referralPendingList2 != null && !referralPendingList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : referralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			}
			response.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	@Override
	public GetallReferralRejectedDetailsRes getallReferralRejectedDetails(ExistingQuoteReq req) {
		GetallReferralRejectedDetailsRes response = new GetallReferralRejectedDetailsRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
//			List<String> branches = new ArrayList<String>();
//			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {
//
//				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);
//
//				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
//						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
//				if (branches.size() <= 0) {
//					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
//
//				}
//
//			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//				branches.add(req.getBrokerBranchCode());
//			} else {
//				branches.add(req.getBranchCode());
//			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> referralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				GetMotorReferalDetailsRes resp = motService.getMotorReferalDetails(req, limit, offset, "RR");
				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();
				if (List2 != null && !List2.isEmpty()) {
					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				GetMotorReferalDetailsRes resp = lifeService.getLifeReferalDetails(req, limit, offset, "RR");
				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();
				if (List2 != null && !List2.isEmpty()) {
					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				GetTravelReferalDetailsRes resp = traService.getTravelReferalDetails(req, limit, offset, "RR");

				referralRejectedList = resp.getReferalGridCriteriaRes();
				if (referralRejectedList != null && !referralRejectedList.isEmpty()) {
					for (ReferalGridCriteriaRes data : referralRejectedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				GetTravelReferalDetailsRes resp = buiService.getBuildingReferalDetails(req, limit, offset, "RR");

				referralRejectedList = resp.getReferalGridCriteriaRes();
				if (referralRejectedList != null && !referralRejectedList.isEmpty()) {
					for (ReferalGridCriteriaRes data : referralRejectedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				GetCommonReferalDetailsRes resp1 = commonService.getCommonReferalDetails(req, limit, offset, "RR");
				List<ReferalCommonCriteriaRes> referralPendingList2 = resp1.getReferalCommonCriteriaRes();
				if (referralPendingList2 != null && !referralPendingList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : referralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount() + resp1.getTotalCount()));

			} else {
				GetCommonReferalDetailsRes resp = commonService.getCommonReferalDetails(req, limit, offset, "RR");

				List<ReferalCommonCriteriaRes> referralPendingList2 = resp.getReferalCommonCriteriaRes();
				if (referralPendingList2 != null && !referralPendingList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : referralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			}
			response.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	@Override
	public GetallReferralDetailsCommonRes getallAdminReferralPendings(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		GetallReferralDetailsCommonRes response = new GetallReferralDetailsCommonRes();
		try {
			String sourceType = req.getSourceType();
			if ("Premia Broker".equalsIgnoreCase(sourceType)) {
			    req.setSourceType("Broker");
			} else if ("Premia Direct".equalsIgnoreCase(sourceType)) {
			    req.setSourceType("Direct");
			} else if ("Premia Agent".equalsIgnoreCase(sourceType)) {
			    req.setSourceType("Agent");
			}
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralPendingList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {

				GetMotorReferalDetailsRes resp = motService.getMotorAdminReferalDetails(req, limit, offset, "RP");

				List<MotorGridCriteriaRes> adminReferralPendingList2 = resp.getMotorGridCriteriaResRes();
				if (adminReferralPendingList2 != null && !adminReferralPendingList2.isEmpty()) {
					for (MotorGridCriteriaRes data : adminReferralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {

				GetMotorReferalDetailsRes resp = lifeService.getLifeAdminReferalDetails(req, limit, offset, "RP");

				List<MotorGridCriteriaRes> adminReferralPendingList2 = resp.getMotorGridCriteriaResRes();
				if (adminReferralPendingList2 != null && !adminReferralPendingList2.isEmpty()) {
					for (MotorGridCriteriaRes data : adminReferralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				GetTravelReferalDetailsRes resp = traService.getTravelAdminReferalDetails(req, limit, offset, "RP");
				adminReferralPendingList = resp.getReferalGridCriteriaRes();
				if (adminReferralPendingList != null && !adminReferralPendingList.isEmpty()) {
					for (ReferalGridCriteriaRes data : adminReferralPendingList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {

				GetTravelReferalDetailsRes resp = buiService.getBuildingAdminReferalDetails(req, limit, offset, "RP");

				adminReferralPendingList = resp.getReferalGridCriteriaRes();
				if (adminReferralPendingList != null && !adminReferralPendingList.isEmpty()) {
					for (ReferalGridCriteriaRes data : adminReferralPendingList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}

				GetCommonReferalDetailsRes resp1 = commonService.getCommonAdminReferalDetails(req, limit, offset, "RP");

				List<ReferalCommonCriteriaRes> adminReferralPendingList2 = resp1.getReferalCommonCriteriaRes();
				if (adminReferralPendingList2 != null && !adminReferralPendingList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : adminReferralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount() + resp1.getTotalCount()));

			} else {
				GetCommonReferalDetailsRes resp = commonService.getCommonAdminReferalDetails(req, limit, offset, "RP");

				List<ReferalCommonCriteriaRes> adminReferralPendingList2 = resp.getReferalCommonCriteriaRes();
				if (adminReferralPendingList2 != null && !adminReferralPendingList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : adminReferralPendingList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			}

			response.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	@Override
	public GetallReferralDetailsCommonRes getallAdminReferralApproved(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		GetallReferralDetailsCommonRes response = new GetallReferralDetailsCommonRes();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

//			List<String> branches = new ArrayList<String>();
//			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(req.getApplicationId());
//			branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralApprovedList = new ArrayList<ReferalGridCriteriaRes>();

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				GetMotorReferalDetailsRes resp = motService.getMotorAdminReferalDetails(req, limit, offset, "RA");
				if (resp.getMotorGridCriteriaResRes() != null && !resp.getMotorGridCriteriaResRes().isEmpty()) {
					List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();

					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				GetMotorReferalDetailsRes resp = lifeService.getLifeAdminReferalDetails(req, limit, offset, "RA");
				if (resp.getMotorGridCriteriaResRes() != null && !resp.getMotorGridCriteriaResRes().isEmpty()) {
					List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();

					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				GetTravelReferalDetailsRes resp = traService.getTravelAdminReferalDetails(req, limit, offset, "RA");
				if (resp.getReferalGridCriteriaRes() != null && !resp.getReferalGridCriteriaRes().isEmpty()) {
					adminReferralApprovedList = resp.getReferalGridCriteriaRes();

					for (ReferalGridCriteriaRes data : adminReferralApprovedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {

				GetTravelReferalDetailsRes resp = buiService.getBuildingAdminReferalDetails(req, limit, offset, "RA");
				System.out.println("Approved  A: " +resp);
				if (resp.getReferalGridCriteriaRes() != null && !resp.getReferalGridCriteriaRes().isEmpty()) {
					adminReferralApprovedList = resp.getReferalGridCriteriaRes();

					for (ReferalGridCriteriaRes data : adminReferralApprovedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				GetCommonReferalDetailsRes resp1 = commonService.getCommonAdminReferalDetails(req, limit, offset, "RA");
				System.out.println("Approved  C: " +resp1);
				if (resp1.getReferalCommonCriteriaRes() != null && !resp1.getReferalCommonCriteriaRes().isEmpty()) {
					List<ReferalCommonCriteriaRes> adminReferralApprovedList2 = resp1.getReferalCommonCriteriaRes();

					for (ReferalCommonCriteriaRes data : adminReferralApprovedList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount() + resp1.getTotalCount()));

			} 
			else {
				GetCommonReferalDetailsRes resp = commonService.getCommonAdminReferalDetails(req, limit, offset, "RA");
				
				if (resp.getReferalCommonCriteriaRes() != null && !resp.getReferalCommonCriteriaRes().isEmpty()) {
					List<ReferalCommonCriteriaRes> adminReferralApprovedList2 = resp.getReferalCommonCriteriaRes();

					for (ReferalCommonCriteriaRes data : adminReferralApprovedList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));
			}
			response.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	@Override
	public GetallReferralDetailsCommonRes getallAdminReferralRejected(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		GetallReferralDetailsCommonRes response = new GetallReferralDetailsCommonRes();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(req.getApplicationId());
			branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				GetMotorReferalDetailsRes resp = motService.getMotorAdminReferalDetails(req, limit, offset, "RR");

				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();
				if (List2 != null && !List2.isEmpty()) {
					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				GetMotorReferalDetailsRes resp = lifeService.getLifeAdminReferalDetails(req, limit, offset, "RR");

				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();
				if (List2 != null && !List2.isEmpty()) {
					for (MotorGridCriteriaRes data : List2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				GetTravelReferalDetailsRes resp = traService.getTravelAdminReferalDetails(req, limit, offset, "RR");
				adminReferralRejectedList = resp.getReferalGridCriteriaRes();
				if (adminReferralRejectedList != null && !adminReferralRejectedList.isEmpty()) {
					for (ReferalGridCriteriaRes data : adminReferralRejectedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				GetTravelReferalDetailsRes resp = buiService.getBuildingAdminReferalDetails(req, limit, offset, "RR");

				adminReferralRejectedList = resp.getReferalGridCriteriaRes();
				if (adminReferralRejectedList != null && !adminReferralRejectedList.isEmpty()) {
					for (ReferalGridCriteriaRes data : adminReferralRejectedList) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				GetCommonReferalDetailsRes resp1 = commonService.getCommonAdminReferalDetails(req, limit, offset, "RR");

				List<ReferalCommonCriteriaRes> adminReferralRejectedList2 = resp1.getReferalCommonCriteriaRes();
				if (adminReferralRejectedList2 != null && !adminReferralRejectedList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : adminReferralRejectedList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount() + resp1.getTotalCount()));

			} else {
				GetCommonReferalDetailsRes resp = commonService.getCommonAdminReferalDetails(req, limit, offset, "RR");

				List<ReferalCommonCriteriaRes> adminReferralRejectedList2 = resp.getReferalCommonCriteriaRes();
				if (adminReferralRejectedList2 != null && !adminReferralRejectedList2.isEmpty()) {
					for (ReferalCommonCriteriaRes data : adminReferralRejectedList2) {
						EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
						res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
						// res.setCount(data.getIdsCount() == null ? "" :
						// data.getIdsCount().toString());
						res.setClientName((data.getTitleDesc() == null ? "" : data.getTitleDesc()) + " " +
							    (data.getClientName() == null ? "" : data.getClientName()) + " " +
							    (data.getLastName() == null ? "" : data.getLastName()));
						custRes.add(res);
					}
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			}

			response.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	@Transactional
	@Override
	public CopyQuoteSuccessRes copyQuote(CopyQuoteReq req) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		try {
			ReInsuranceQuoteReq reReq= new ReInsuranceQuoteReq();
			String appId = "";
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<EserviceMotorDetails> motorList = repo.findByRequestReferenceNo(req.getRequestReferenceNo());
				appId = motorList.get(0).getApplicationId();
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				List<EserviceBuildingDetails> buildingList = buildingRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				appId = buildingList.get(0).getApplicationId();
			}
			String loginId = "";
			List<String> branches = new ArrayList<String>();
			if (appId.equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}

			branches.add(req.getBranchCode());
			
			reReq.setQuoteNo(req.getQuoteNo());
			reReq.setUserType(req.getUserType());
			reReq.setCreatedBy(req.getApplicationId());
			
			if (req.getTypeId().equalsIgnoreCase("Endt")) {
				// Product Wise Get
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					res = motService.motorEndt(req, branches, loginId);

				} else if (product.getMotorYn().equalsIgnoreCase("H")
						&& req.getProductId().equalsIgnoreCase(travelProductId)) {
					res = traService.travelEndt(req, branches, loginId);
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
					res = buiService.buildingEndt(req, branches, loginId);

				} else {
					res = commonService.commonEndt(req, branches, loginId);

				}
			} else if (StringUtils.isBlank(req.getTypeId()) || req.getTypeId().equalsIgnoreCase("Normal")) {
				// Product Wise Get
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					res = motService.motorCopyQuote(req, branches, loginId);
				} else if (product.getMotorYn().equalsIgnoreCase("H")
						&& req.getProductId().equalsIgnoreCase(travelProductId)) {
					res = traService.travelCopyQuote(req, branches, loginId);
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
					res = buiService.buildingCopyQuote(req, branches, loginId);
				} else {
					res = commonService.commonCopyQuote(req, branches);
				}
				res.setRequestReferenceNo(res.getRequestReferenceNo());
			}

		} catch (

		Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	// Validation
	@Override
	public List<Error> validateQuotoNo(CopyQuoteReq req) {
		List<Error> error = new ArrayList<Error>();

		try {

			List<Tuple> quoteExist = null;
			if (req.getTypeId().equalsIgnoreCase("Endt")) {
				if (StringUtils.isBlank(req.getQuoteNo())) {
					error.add(new Error("01", "QuoteNo", "Please Enter QuoteNo "));
				} else if (StringUtils.isBlank(req.getEndtTypeId())) {
					error.add(new Error("01", "EndtTypeId", "Please Select EndtTypeId "));
				}
				CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
						req.getProductId().toString());

				if (product.getMotorYn().equalsIgnoreCase("M")) {
					quoteExist = motService.validateMotorEndt(req.getQuoteNo());
				} else {
					quoteExist = commonService.validateCommonEndt(req.getQuoteNo());
				}

				if (quoteExist.size() > 0) {
					for (Tuple data : quoteExist) {
						if (Integer.valueOf(data.get("homeCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						} else if (data.get("policyNo") == " " || data.get("policyNo") == null) {
							error.add(new Error("02", "PolicyNo", "Policy No is Null "));
						} else if (!data.get("status").equals("P")) {
							error.add(new Error("02", "Status", "Status is Null "));
						} else if (Integer.valueOf(data.get("perCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						} else if (Integer.valueOf(data.get("policyCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						} else if (Integer.valueOf(data.get("motorCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						}
					}

				} else {
					error.add(new Error("02", "List", "No Data Found "));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return error;
	}

	@Override
	public List<GetAllMotorDetailsRes> getbyReqRefNo(CopyQuoteReq req) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		List<GetAllMotorDetailsRes> reslist = new ArrayList<GetAllMotorDetailsRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			/*
			 * String loginId = req.getLoginId();
			 * 
			 * // Branch Res List<String> branches = new ArrayList<String>();
			 * 
			 * if (req.getBranchCode().equalsIgnoreCase("99999")) {
			 * 
			 * List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);
			 * 
			 * branches = loginBranch.stream().filter(o ->
			 * !o.getBrokerBranchCode().equalsIgnoreCase("None"))
			 * .map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList()); if
			 * (branches.size() <= 0) { branches =
			 * loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors
			 * .toList());
			 * 
			 * }
			 * 
			 * } else if (req.getUserType().equalsIgnoreCase("Broker") ||
			 * req.getUserType().equalsIgnoreCase("User")) {
			 * branches.add(req.getBranchCode()); } else {
			 * branches.add(req.getBranchCode()); }
			 */
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
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				list = motService.searchMotorQuote(req, branches);

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				list = traService.searchTravelQuote(req, branches);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				list = buiService.searchBuildingQuote(req, branches);

			} else {
				list = commonService.searchCommonQuote(req, branches);
			}

			for (Tuple data : list) {
				GetAllMotorDetailsRes res = new GetAllMotorDetailsRes();

				// Risk
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					if (data != null) {
						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
						res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null
								: data.get("requestReferenceNo").toString());
						res.setIdNumber(data.get("idNumber") == null ? null : data.get("idNumber").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						// res.setRiskId(data.get("riskId")==null?null:data.get("riskId").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("customerId").toString());
						res.setInsuranceType(
								data.get("insuranceType") == null ? null : data.get("insuranceType").toString());
						res.setAccident(data.get("accident") == null ? null : data.get("accident").toString());
						// res.setGpsTrackingInstalled(data.get("gpsTrackingInstalled")==null?null:data.get("gpsTrackingInstalled").toString());
						// res.setGpsTrackingInstalled(data.get("gpsTrackingInstalled")==null?null:data.get("gpsTrackingInstalled").toString());
						// res.setManufactureYear(data.get("manufactureYear")==null?null:data.get("manufactureYear").toString());

						res.setInsuranceTypeDesc(data.get("insuranceTypeDesc") == null ? null
								: data.get("insuranceTypeDesc").toString());
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
						// res.setMotorCategory(data.get("motorCategory")==null?null:data.get("motorCategory").toString());
						// res.setMotorCategoryDesc(data.get("motorCategoryDesc")==null?null:data.get("motorCategoryDesc").toString());
						// res.setMotorUsage(data.get("motorUsage")==null?null:data.get("motorUsage").toString());
						res.setRegistrationNumber(data.get("registrationNumber") == null ? null
								: data.get("registrationNumber").toString());
						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
						res.setChassisNumber(
								data.get("chassisNumber") == null ? null : data.get("chassisNumber").toString());
						res.setActualPremiumFc(
								data.get("actualPremiumFc") == null ? null : data.get("actualPremiumFc").toString());
						res.setVehicleMake(data.get("vehicleMake") == null ? null : data.get("vehicleMake").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setVehicleMakeDesc(
								data.get("vehicleMakeDesc") == null ? null : data.get("vehicleMakeDesc").toString());
						res.setVehicleMakeDesc(
								data.get("vehicleMakeDesc") == null ? null : data.get("vehicleMakeDesc").toString());
						res.setVehcileModel(
								data.get("vehcileModel") == null ? null : data.get("vehcileModel").toString());
						res.setVehcileModelDesc(
								data.get("vehcileModelDesc") == null ? null : data.get("vehcileModelDesc").toString());
						res.setVehicleType(data.get("vehicleType") == null ? null : data.get("vehicleType").toString());
						res.setVehicleTypeDesc(
								data.get("vehicleTypeDesc") == null ? null : data.get("vehicleTypeDesc").toString());
						res.setModelNumber(data.get("modelNumber") == null ? null : data.get("modelNumber").toString());
						// res.setEngineNumber(data.get("engineNumber")==null?null:data.get("engineNumber").toString());
//						res.setFuelType(data.get("fuelType")==null?null:data.get("fuelType").toString()); 
//						res.setFuelTypeDesc(data.get("fuelTypeDesc")==null?null:data.get("fuelTypeDesc").toString());
//						res.setOverridePercentage(data.get("overridePercentage")==null?null:data.get("overridePercentage").toString());
//						res.setRegistrationYear(data.get("registrationYear")==null?null:data.get("registrationYear").toString()); 
//						res.setSeatingCapacity(data.get("seatingCapacity")==null?null:data.get("seatingCapacity").toString());
//						res.setCubicCapacity(data.get("cubicCapacity")==null?null:data.get("cubicCapacity").toString());
//						res.setColor(data.get("color")==null?null:data.get("color").toString());
						// res.setCo(data.get("colorDesc")==null?null:data.get("colorDesc").toString());
						// res.setg//(data.get("grossWeight")==null?null:data.get("grossWeight").toString());
						// res.setT//(data.get("tareWeight")==null?null:data.get("tareWeight").toString());
						// res.setCovernoteNo(data.get("covernoteNo")==null?null:data.get("covernoteNo").toString());
//						res.set(data.get("stickerNo")==null?null:data.get("stickerNo").toString());
//						res.set(data.get("periodOfInsurance")==null?null:data.get("periodOfInsurance").toString());
//						res.setWindScreenSumInsured(data.get("windScreenSumInsured")==null?null:data.get("windScreenSumInsured").toString());
//						res.set(data.get("acccessoriesSumInsured")==null?null:data.get("acccessoriesSumInsured").toString());
//						res.set(data.get("accessoriesInformation")==null?null:data.get("accessoriesInformation").toString());
//						res.set(data.get("numberOfAxels")==null?null:data.get("numberOfAxels").toString());
//						res.set(data.get("axelDistance")==null?null:data.get("axelDistance").toString());
						// res.setSumInsured(data.get("sumInsured")==null?null:data.get("sumInsured").toString());
						res.setEndorsementType(
								data.get("endorsementType") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endorsementTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());
//						res.setTppdFreeLimit(data.get("tppdFreeLimit")==null?null:data.get("tppdFreeLimit").toString());
//						res.setTppdIncreaeLimit(data.get("tppdIncreaeLimit")==null?null:data.get("tppdIncreaeLimit").toString());
//						res.setSpecialTermsOfPremium(data.get("specialTermsOfPremium")==null?null:data.get("specialTermsOfPremium").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setAgencyCode(data.get("agencyCode") == null ? null : data.get("agencyCode").toString());
						res.setInsuranceClass(
								data.get("insuranceClass") == null ? null : data.get("insuranceClass").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						// res.setCompanyName(data.get("companyName")==null?null:data.get("companyName").toString());
//						res.setPolicyType(data.get("policyType")==null?null:data.get("policyType").toString());
//						res.sePolicyTypeDesc(data.get("policyTypeDesc")==null?null:data.get("policyTypeDesc").toString());
						res.setStatus(data.get("status") == null ? null : data.get("status").toString());
						// res.setRoofRack(data.get("roofRack")==null?null:data.get("roofRack").toString());

						String entryDate = data.get("entryDate") == null ? null
								: dateFormat.format(data.get("entryDate"));
						res.setEntryDate(entryDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						// res.set(data.get("trailerDetails")==null?null:data.get("trailerDetails").toString());

						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						res.setUpdatedBy(data.get("updatedBy") == null ? null : data.get("updatedBy").toString());
						// res.setDrivenBy(data.get("drivenBy")==null?null:data.get("drivenBy").toString());

						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));
						res.setPolicyStartDate(policyStartDate);

						res.setDrivenByDesc(
								data.get("drivenByDesc") == null ? null : data.get("drivenByDesc").toString());
						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						// res.setDefectiveVisionOrHearing(data.get("defectiveVisionOrHearing")==null?null:data.get("defectiveVisionOrHearing").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());
//						res.setMotoringOffence(data.get("motoringOffence")==null?null:data.get("motoringOffence").toString());
//						res.setFleetOwnerYn(data.get("fleetOwnerYn")==null?null:data.get("fleetOwnerYn").toString());
//						res.setSuspensionOfLicense(data.get("suspensionOfLicense")==null?null:data.get("suspensionOfLicense").toString());
//						res.setNoOfVehicles(data.get("noOfVehicles")==null?null:data.get("noOfVehicles").toString());
//						res.setIrrespectiveOfBlame(data.get("irrespectiveOfBlame")==null?null:data.get("irrespectiveOfBlame").toString());
//						res.setNoOfCompehensives(data.get("noOfCompehensives")==null?null:data.get("noOfCompehensives").toString());
//						res.setVehicleInterestedCompany(data.get("vehicleInterestedCompany")==null?null:data.get("vehicleInterestedCompany").toString());
//						res.setClaimRatio(data.get("claimRatio")==null?null:data.get("claimRatio").toString()); 
//						res.setInterestedCompanyDetails(data.get("interestedCompanyDetails")==null?null:data.get("interestedCompanyDetails").toString());
//						res.setCollateralYn(data.get("collateralYn")==null?null:data.get("collateralYn").toString());
//						res.setBorrowerType(data.get("borrowerType")==null?null:data.get("borrowerType").toString());
//						res.setOtherVehicle(data.get("otherVehicle")==null?null:data.get("otherVehicle").toString());
//						res.setBorrowerTypeDesc(data.get("borrowerTypeDesc")==null?null:data.get("borrowerTypeDesc").toString());

//						res.setOtherVehicleDetails(data.get("otherVehicleDetails")==null?null:data.get("otherVehicleDetails").toString());
//						res.setOtherInsurance(data.get("collateralName")==null?null:data.get("collateralName").toString());
//						res.setOtherInsurance(data.get("otherInsurance")==null?null:data.get("otherInsurance").toString());
//						res.setFirstLossPayee(data.get("firstLossPayee")==null?null:data.get("firstLossPayee").toString());
//						res.setOtherInsuranceDetails(data.get("otherInsuranceDetails")==null?null:data.get("otherInsuranceDetails").toString());
//						res.setHoldInsurancePolicy(data.get("holdInsurancePolicy")==null?null:data.get("holdInsurancePolicy").toString());
//						res.setNoOfClaims(data.get("noOfClaims")==null?null:data.get("noOfClaims").toString());
//						res.setCityLimit(data.get("cityLimit")==null?null:data.get("cityLimit").toString());
//						res.setAdditionalCircumstances(data.get("additionalCircumstances")==null?null:data.get("additionalCircumstances").toString());
						res.setSavedFrom(data.get("savedFrom") == null ? null : data.get("savedFrom").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setSubUserType(data.get("subUserType") == null ? null : data.get("subUserType").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
						// res.setAdminLoginId(data.get("adminLoginId")==null?null:data.get("adminLoginId").toString());
						res.setAdminRemarks(
								data.get("adminRemarks") == null ? null : data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setBdmCode(data.get("bdmCode") == null ? null : data.get("bdmCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
						res.setCustomerCode(
								data.get("customerCode") == null ? null : data.get("customerCode").toString());
						// res.setBrokerBranchName(data.get("brokerBranchName")==null?null:data.get("brokerBranchName").toString());
						res.setBrokerBranchCode(
								data.get("brokerBranchCode") == null ? null : data.get("brokerBranchCode").toString());

						res.setCommissionType(
								data.get("commissionType") == null ? null : data.get("commissionType").toString());
//						res.setCommissionTypeDesc(data.get("commissionTypeDesc")==null?null:data.get("commissionTypeDesc").toString());
//						res.setOldReqRefNo(data.get("oldReqRefNo")==null?null:data.get("oldReqRefNo").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
//						res.setDriverYn(data.get("driverYn")==null?null:data.get("driverYn").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
//						res.setManualReferalYn(data.get("manualReferalYn")==null?null:data.get("manualReferalYn").toString());
//						res.setTiraCoverNoteNo(data.get("tiraCoverNoteNo")==null?null:data.get("tiraCoverNoteNo").toString());

						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);
						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						String endorsementEffdate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffdate);
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtPrevPolicyNo") == null ? null : data.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtStatus(data.get("endtStatus") == null ? null : data.get("endtStatus").toString());
						res.setIsFinaceYn(data.get("isFinaceYn") == null ? null : data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
						res.setEndtCount(data.get("endtCount") == null ? null : data.get("endtCount").toString());
						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
					}
//					EserviceMotorDetails riskData =data.get("c") ==null?null: (EserviceMotorDetails) data.get("c")   ;
//					if( riskData !=null ) {
//						dozermapper.map(riskData, res);
//						res.setSectionName(riskData.getSectionName());	
//					}

				} else if (product.getMotorYn().equalsIgnoreCase("H")
						&& req.getProductId().equalsIgnoreCase(travelProductId)) {
					EserviceTravelDetails riskData = new EserviceTravelDetails();
					if (data != null) {
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null
								: data.get("requestReferenceNo").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("quoteNo").toString());
						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));
						res.setPolicyStartDate(policyStartDate);

						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);

						// res.setTravelStartDate(data.get("travelStartDate")==null?null:data.get("travelStartDate").toString());
						// res.setTravelEndDat(data.get("travelEndDate")==null?null:data.get("travelEndDate").toString());
						// res.setRejectReason(data.get("rejectReason")==null?null:data.get("rejectReason").toString());
						// res.setSdminRemarks(data.get("adminRemarks")==null?null:data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
//						res.setRiskId(data.get("riskId")==null?null:data.get("riskId").toString());
//						res.setTravelCoverId(data.get("travelCoverId")==null?null:data.get("travelCoverId").toString());
//						res.setTravelCoverDesc(data.get("travelCoverDesc")==null?null:data.get("travelCoverDesc").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
//						res.setSourceCountry(data.get("sourceCountry")==null?null:data.get("sourceCountry").toString());
//						res.setDestinationCountry(data.get("destinationCountry")==null?null:data.get("destinationCountry").toString());
//						res.setSportsCoverYn(data.get("sportsCoverYn")==null?null:data.get("sportsCoverYn").toString());
//						res.setTerrorismCoverYn(data.get("terrorismCoverYn")==null?null:data.get("terrorismCoverYn").toString());
//						res.setPlanTypeId(data.get("planTypeId")==null?null:data.get("planTypeId").toString());
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());
//						res.setPlanTypeDesc(data.get("planTypeDesc")==null?null:data.get("planTypeDesc").toString());
//						res.setTravelCoverDuration(data.get("travelCoverDuration")==null?null:data.get("travelCoverDuration").toString());
//						res.setTotalPassengers(data.get("totalPassengers")==null?null:data.get("totalPassengers").toString());
//						res.setTotalPremium(data.get("totalPremium")==null?null:data.get("totalPremium").toString());
//						res.setAge(data.get("age")==null?null:data.get("age").toString()); 
//						res.setEffectiveDate(data.get("effectiveDate")==null?null:data.get("effectiveDate").toString());
						String entryDate = data.get("entryDate") == null ? null
								: dateFormat.format(data.get("entryDate"));
						res.setEntryDate(entryDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						res.setStatus(data.get("status") == null ? null : data.get("status").toString());
						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						res.setUpdatedBy(data.get("updatedBy") == null ? null : data.get("updatedBy").toString());
//						res.setRemarks(data.get("remarks")==null?null:data.get("remarks").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
//						res.setCovidCoverYn(data.get("covidCoverYn")==null?null:data.get("covidCoverYn").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setSubUserType(data.get("subUserType") == null ? null : data.get("subUserType").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
//						res.setAdminLoginId(data.get("adminLoginId")==null?null:data.get("adminLoginId").toString());
//						res.setBdmCode.set(data.get("bdmCode")==null?null:data.get("bdmCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
//						res.setcustomerCode(data.get("customerCode")==null?null:data.get("customerCode").toString());
//						res.setBrokerBranchName(data.get("brokerBranchName")==null?null:data.get("brokerBranchName").toString());
						res.setBrokerBranchCode(
								data.get("brokerBranchCode") == null ? null : data.get("brokerBranchCode").toString());
//						res.setCompanyName(data.get("companyName")==null?null:data.get("companyName").toString());
//						res.setProductName(data.get("productName")==null?null:data.get("productName").toString());
						// res.setSumInsured(data.get("sumInsured")==null?null:data.get("sumInsured").toString());
						res.setCommissionType(
								data.get("commissionType") == null ? null : data.get("commissionType").toString());
//						res.setCommissionTypeDesc(data.get("commissionTypeDesc")==null?null:data.get("commissionTypeDesc").toString());
//						res.setSourceCountryDesc(data.get("sourceCountryDesc")==null?null:data.get("sourceCountryDesc").toString());
//						res.setDestinationCountryDesc(data.get("destinationCountryDesc")==null?null:data.get("destinationCountryDesc").toString());
						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
//						res.setactualPremiumFc(data.get("actualPremiumFc")==null?null:data.get("actualPremiumFc").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setOverallPremiumFc(
								data.get("overallPremiumFc") == null ? null : data.get("overallPremiumFc").toString());
//						res.setOldReqRefNo(data.get("oldReqRefNo")==null?null:data.get("oldReqRefNo").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
//						res.setManualReferalYn(data.get("manualReferalYn")==null?null:data.get("manualReferalYn").toString());
						res.setEndorsementType(
								data.get("endorsementType") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endorsementTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());
						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);
						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						String endorsementEffdate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffdate);
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtPrevPolicyNo") == null ? null : data.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtStatus(data.get("endtStatus") == null ? null : data.get("endtStatus").toString());
						res.setIsFinaceYn(data.get("isFinaceYn") == null ? null : data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
//						res.setEndtPremium(data.get("endtPremium")==null?null:data.get("endtPremium").toString());

						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
					}
//					//EserviceTravelDetails riskData =data==null?null: (EserviceTravelDetails) data;
//					if( riskData !=null ) {
//						dozermapper.map(riskData==null?null:data.get("quoteNo").toString()); res);
//						res.setSectionName(riskData.getSectionName());	
//					}

				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
//					EserviceBuildingDetails riskData =data.get("c") ==null?null: (EserviceBuildingDetails) data.get("c")   ;
//					
//					if( riskData !=null ) {
//						dozermapper.map(riskData, res);
//						res.setSectionName(riskData.getProductDesc());	
//					}
					if (data != null) {

						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
						res.setBrokerBranchCode(
								data.get("brokerBranchCode") == null ? null : data.get("brokerBranchCode").toString());
						res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null
								: data.get("requestReferenceNo").toString());
						res.setInsuranceType(
								data.get("insuranceType") == null ? null : data.get("insuranceType").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setAdminRemarks(
								data.get("adminRemarks") == null ? null : data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setAgencyCode(data.get("agencyCode") == null ? null : data.get("agencyCode").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						res.setStatus(data.get("status") == null ? null : data.get("status").toString());
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());

						String entryDate = data.get("entryDate") == null ? null
								: dateFormat.format(data.get("entryDate"));
						res.setEntryDate(entryDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						// res.set(data.get("trailerDetails")==null?null:data.get("trailerDetails").toString());

						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));
						res.setPolicyStartDate(policyStartDate);

						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);
						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
						res.setActualPremiumFc(
								data.get("actualPremiumFc") == null ? null : data.get("actualPremiumFc").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setOverallPremiumFc(
								data.get("overallPremiumFc") == null ? null : data.get("overallPremiumFc").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setSubUserType(data.get("subUserType") == null ? null : data.get("subUserType").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						// res.setSumInsured(data.get("sumInsured")==null?null:data.get("sumInsured").toString());
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("customerId").toString());
						res.setBdmCode(data.get("bdmCode") == null ? null : data.get("bdmCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
						res.setCustomerCode(
								data.get("customerCode") == null ? null : data.get("customerCode").toString());
						res.setCommissionType(
								data.get("commissionType") == null ? null : data.get("commissionType").toString());
						res.setEndorsementType(
								data.get("endorsementType") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endorsementTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());

						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);
						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						String endorsementEffdate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffdate);

						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtPrevPolicyNo") == null ? null : data.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtCount(data.get("endtCount") == null ? null : data.get("endtCount").toString());
						res.setEndtStatus(data.get("endtStatus") == null ? null : data.get("endtStatus").toString());
						// res.setIsFinaceYn(data.get("isFinaceYn") == null ? null :
						// data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
						res.setSectionName(data.get("sectionDesc") == null ? null : data.get("sectionDesc").toString());

					}
				} else {

					if (data != null) {
						res.setSumInsured(data.get("sumInsured") == null ? null : data.get("sumInsured").toString());
						res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null
								: data.get("requestReferenceNo").toString());
						res.setBrokerBranchCode(
								data.get("brokerBranchCode") == null ? null : data.get("brokerBranchCode").toString());
						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setAgencyCode(data.get("agencyCode") == null ? null : data.get("agencyCode").toString());

						res.setStatus(data.get("status") == null ? null : data.get("status").toString());

						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						res.setUpdatedBy(data.get("updatedBy") == null ? null : data.get("updatedBy").toString());
						res.setAdminRemarks(
								data.get("adminRemarks") == null ? null : data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("customerId").toString());
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());

						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));

						res.setPolicyStartDate(policyStartDate);

						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);

						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
						res.setActualPremiumFc(
								data.get("actualPremiumFc") == null ? null : data.get("actualPremiumFc").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setOverallPremiumFc(
								data.get("overallPremiumFc") == null ? null : data.get("overallPremiumFc").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
						res.setCustomerCode(
								data.get("customerCode") == null ? null : data.get("customerCode").toString());
						res.setBdmCode(data.get("bdmCode") == null ? null : data.get("bdmCode").toString());
						res.setEndorsementType(
								data.get("endorsementType") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endorsementTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());
						res.setSumInsured(data.get("sumInsured") == null ? null : data.get("sumInsured").toString());
						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);

						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());

						String endorsementEffDate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffDate);
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtPrevPolicyNo") == null ? null : data.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtCount(data.get("endtCount") == null ? null : data.get("endtCount").toString());
						res.setEndtStatus(data.get("endtStatus") == null ? null : data.get("endtStatus").toString());
						// res.setIsFinaceYn(data.get("isFinaceYn") == null ? null :
						// data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
					}
//					EserviceCommonDetails riskData =data.get("c") ==null?null: (EserviceCommonDetails) data.get("c")   ;
//					if( riskData !=null ) {
//						dozermapper.map(riskData, res);
//						res.setSectionName(riskData.getSectionName());	
//					}
				}
				res.setClientName((data.get("clientName").toString()));
				res.setIdsCount(data.get("idsCount") == null ? "" : data.get("idsCount").toString());
				reslist.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}

	@Override
	public List<DropDownRes> copyQuoteByDropdown(CopyQuoteDropDownReq req) {

		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {

			List<ListItemValue> getList = new ArrayList<ListItemValue>();
			String itemType = "";
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				itemType = "COPY_QUOTE_BY_MOTOR";
				getList = motService.geMotorCoptyQuotetListItem(req, itemType);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				itemType = "COPY_QUOTE_BY_TRAVEL";
				getList = traService.getTravelCoptyQuotetListItem(req, itemType);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				itemType = "COPY_QUOTE_BY_BUILDING";
				getList = buiService.geBuildingCoptyQuotetListItem(req, itemType);

			} else {
				itemType = "COPY_QUOTE_BY_COMMON";
				getList = commonService.getCommonCoptyQuotetListItem(req, itemType);
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
	public GetallReferralDetailsCommonRes getallReferralRequoteDetails(ExistingQuoteReq req) {
		GetallReferralDetailsCommonRes response = new GetallReferralDetailsCommonRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
//			List<String> branches = new ArrayList<String>();
//			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {
//
//				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);
//
//				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
//						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
//				if (branches.size() <= 0) {
//					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
//
//				}
//
//			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//				branches.add(req.getBrokerBranchCode());
//			} else {
//				branches.add(req.getBranchCode());
//			}

			List<ReferalGridCriteriaRes> referralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				GetMotorReferalDetailsRes resp = motService.getMotorReferalDetails(req, limit, offset, "RE");
				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();

				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				GetMotorReferalDetailsRes resp = lifeService.getLifeReferalDetails(req, limit, offset, "RE");
				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();

				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);

					custRes.add(res);
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				GetTravelReferalDetailsRes resp = traService.getTravelReferalDetails(req, limit, offset, "RE");

				referralRejectedList = resp.getReferalGridCriteriaRes();
				for (ReferalGridCriteriaRes data : referralRejectedList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);

					custRes.add(res);
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {

				GetTravelReferalDetailsRes resp = buiService.getBuildingReferalDetails(req, limit, offset, "RE");

				referralRejectedList = resp.getReferalGridCriteriaRes();
				for (ReferalGridCriteriaRes data : referralRejectedList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);

					custRes.add(res);
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else {

				GetCommonReferalDetailsRes resp = commonService.getCommonReferalDetails(req, limit, offset, "RE");

				List<ReferalCommonCriteriaRes> referralPendingList2 = resp.getReferalCommonCriteriaRes();

				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			}
			response.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	@Override
	public GetallReferralDetailsCommonRes getallAdminReferralRequote(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		GetallReferralDetailsCommonRes response = new GetallReferralDetailsCommonRes();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

//			List<String> branches = new ArrayList<String>();
//			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(req.getApplicationId());
//			branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				GetMotorReferalDetailsRes resp = motService.getMotorAdminReferalDetails(req, limit, offset, "RE");

				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();

				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				GetMotorReferalDetailsRes resp = lifeService.getLifeAdminReferalDetails(req, limit, offset, "RE");

				List<MotorGridCriteriaRes> List2 = resp.getMotorGridCriteriaResRes();

				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);

					custRes.add(res);
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				GetTravelReferalDetailsRes resp = traService.getTravelAdminReferalDetails(req, limit, offset, "RE");
				adminReferralRejectedList = resp.getReferalGridCriteriaRes();

				for (ReferalGridCriteriaRes data : adminReferralRejectedList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);

					custRes.add(res);
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {

				GetTravelReferalDetailsRes resp = buiService.getBuildingAdminReferalDetails(req, limit, offset, "RE");

				adminReferralRejectedList = resp.getReferalGridCriteriaRes();

				for (ReferalGridCriteriaRes data : adminReferralRejectedList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);

					custRes.add(res);
				}

				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			} else {
				GetCommonReferalDetailsRes resp = commonService.getCommonAdminReferalDetails(req, limit, offset, "RE");

				List<ReferalCommonCriteriaRes> referralPendingList2 = resp.getReferalCommonCriteriaRes();

				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}
				response.setTotalCount(String.valueOf(resp.getTotalCount()));

			}
			response.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return response;
	}

	@Override
	public UpdateLapsedQuoteRes updateLapsedQuoteDetails(UpdateLapsedQuoteReq req) {
		UpdateLapsedQuoteRes res = new UpdateLapsedQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			EserviceMotorDetails motordata = new EserviceMotorDetails();
			EserviceTravelDetails traveldata = new EserviceTravelDetails();
			EserviceBuildingDetails buildingdata = new EserviceBuildingDetails();
			EserviceCommonDetails commondata = new EserviceCommonDetails();
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product = getCompanyProductMasterDropdown(homeData.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				motordata = repo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(req.getRequestReferenceNo(),
						req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(motordata, EserviceMotorDetails.class);
				motordata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(motordata.getRequestReferenceNo());
				res.setQuoteNo(motordata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			}

			else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				traveldata = travelRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(
						req.getRequestReferenceNo(), req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(traveldata, EserviceTravelDetails.class);
				traveldata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(traveldata.getRequestReferenceNo());
				res.setQuoteNo(traveldata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				buildingdata = buildingRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(
						req.getRequestReferenceNo(), req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(buildingdata, EserviceBuildingDetails.class);
				buildingdata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(buildingdata.getRequestReferenceNo());
				res.setQuoteNo(buildingdata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			} else {
				commondata = commonRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(
						req.getRequestReferenceNo(), req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(commondata, EserviceCommonDetails.class);
				commondata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(commondata.getRequestReferenceNo());
				res.setQuoteNo(buildingdata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	// Portfolio

	@Override
	public GetallPortfolioActiveRes getallPortfolioActive(ExistingQuoteReq req) {
		GetallPortfolioActiveRes resp = new GetallPortfolioActiveRes();
		List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<PortfolioGridCriteriaRes> portfolioActiveList = new ArrayList<PortfolioGridCriteriaRes>();

			GetMotorProtfolioActiveRes response = motService.getMotorProtfolioActive(req, today, limit, offset, "P"); // forallproducts
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
			if (!custRes.isEmpty()) {
				for (PortfolioCustomerDetailsRes data : custRes) {
					if (data.getStickerno() == null) {
						data.setStickerno(" ");
					}
				}
			}

			resp.setCustRes(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public GetallPortfolioPendingRes getallPortfolioPending(ExistingQuoteReq req) {
		GetallPortfolioPendingRes resp = new GetallPortfolioPendingRes();
		List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, +365);

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<PortfolioPendingGridCriteriaRes> list = new ArrayList<PortfolioPendingGridCriteriaRes>();
			GetMotorProtfolioPendingRes res = new GetMotorProtfolioPendingRes();
			if (product.getMotorYn().equalsIgnoreCase("M")) {

				res = motService.getMotorProtfolioPending(req, branches, today, limit, offset, "P");
				list = res.getPending();
				resp.setCount(res.getCount());

			} else if (product.getMotorYn().equalsIgnoreCase("L")) { // Life

				res = lifeService.getLifeProtfolioPending(req, branches, today, limit, offset, "P");
				list = res.getPending();
				resp.setCount(res.getCount());

			}

			else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				res = traService.getTravelProtfolioPending(req, branches, today, limit, offset, "P");
				list = res.getPending();
				resp.setCount(res.getCount());

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {

				res = buiService.getBuildingProtfolioPending(req, branches, today, limit, offset, "P");
				list = res.getPending();
				resp.setCount(res.getCount());

			} else {

				res = commonService.getCommonProtfolioPending(req, branches, today, limit, offset, "P");
				list = res.getPending();
				resp.setCount(res.getCount());

			}
			for (PortfolioPendingGridCriteriaRes data : list) {
				PortfolioCustomerDetailsRes res1 = new PortfolioCustomerDetailsRes();
				res1 = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
				// res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				custRes.add(res1);
			}
			resp.setPendingList(custRes);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public GetallPortfolioActiveRes getallPortfolioCancelled(ExistingQuoteReq req) {
		GetallPortfolioActiveRes resp = new GetallPortfolioActiveRes();
		List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			GetMotorProtfolioActiveRes response = motService.getMotorPortfolioCancelled(req, today, limit, offset,
					"842");
			List<PortfolioGridCriteriaRes> list = response.getPortfolioList();

			for (PortfolioGridCriteriaRes data : list) {
				PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
				res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
				custRes.add(res);
			}
			resp.setCount(response.getCount());
			resp.setCustRes(custRes);
			;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public List<DropDownRes> getallIssuerQuoteDetails(IssuerQuoteReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			List<Tuple> List = new ArrayList<Tuple>();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List = motService.getMotorIssuerQuoteDetails(req, before30, today);
			}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
//					extingQuoteList = traService.getTravelExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//				} else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
//					extingQuoteList = buiService.getBuildingExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//					// Common
//				} else { // (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					extingQuoteList = commonService.getCommonExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//				}

			for (Tuple data : List) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.get("loginId").toString());
				res.setCodeDesc(data.get("agencyCode").toString());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetallPolicyReportsRes> getallPolicyReports(GetallPolicyReportsReq req) {
		List<GetallPolicyReportsRes> resList = new ArrayList<GetallPolicyReportsRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		try {

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product wise
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				list = motService.getMotorReportDetails(req);

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {

				list = traService.getTravelReportDetails(req);

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				// list = buiService.getBuildingReportDetails(req);

			} else { // common
				// list = commonService.getCommonReportDetails(req);
			}
			if (list.size() > 0) {

				for (Tuple report : list) {
					GetallPolicyReportsRes res = new GetallPolicyReportsRes();
					res.setBranchName(report.get("branchName") == null ? "" : report.get("branchName").toString());
					res.setBrokerName(report.get("brokerName") == null ? "" : report.get("brokerName").toString());
					res.setCommissionAmount(
							report.get("commissionAmount") == null ? "" : report.get("commissionAmount").toString());
					res.setCommissionPercentage(report.get("commissionPercentage") == null ? ""
							: report.get("commissionPercentage").toString());
					res.setCurrency(report.get("currency") == null ? "" : report.get("currency").toString());
					res.setCustomerName(
							report.get("customerName") == null ? "" : report.get("customerName").toString());
					res.setDebitNoteNo(report.get("debitNoteNo") == null ? "" : report.get("debitNoteNo").toString());
					res.setEndDate(report.get("endDate") == null ? "" : dateFormat.format(report.get("endDate")));
					res.setIssueDate(report.get("issueDate") == null ? "" : dateFormat.format(report.get("issueDate")));
					res.setLoginId(report.get("loginId") == null ? "" : report.get("loginId").toString());
					res.setPaymentId(report.get("paymentId") == null ? "" : report.get("paymentId").toString());
					res.setPaymentType(report.get("paymentType") == null ? "" : report.get("paymentType").toString());
					res.setPolicyNo(report.get("policyNo") == null ? "" : report.get("policyNo").toString());
					res.setPolicyTypeDesc(
							report.get("policyTypeDesc") == null ? "" : report.get("policyTypeDesc").toString());
					res.setPremium(report.get("premium") == null ? "" : report.get("premium").toString());
					res.setQuoteNo(report.get("quoteNo") == null ? "" : report.get("quoteNo").toString());
					res.setStartDate(report.get("startDate") == null ? "" : dateFormat.format(report.get("startDate")));
					res.setSubUserType(report.get("subUserType") == null ? "" : report.get("subUserType").toString());

					if (product.getMotorYn().equalsIgnoreCase("M"))
						res.setSumInsured(report.get("sumInsured") == null ? "" : report.get("sumInsured").toString());
					if (product.getMotorYn().equalsIgnoreCase("H")
							&& req.getProductId().equalsIgnoreCase(travelProductId))
						res.setPassengerCount(
								report.get("passengerCount") == null ? "" : report.get("passengerCount").toString());

					res.setUserType(report.get("userType") == null ? "" : report.get("userType").toString());
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

	@Override
	public List<PortFolioDashBoardRes> getAllAdminPortfolio(PortFolioDashBoardReq req) {
		List<PortFolioDashBoardRes> resList = new ArrayList<PortFolioDashBoardRes>();
		DecimalFormat df = new DecimalFormat("0.##");
		try {
			List<CompanyProductMaster> productList = getCompanyProductList(req.getInsuranceId());
			List<PortFolioAdminTupleRes> list = getPortFolioDashBoard(req);

			// Group By Product Id
			// Map<Integer ,List<PortFolioAdminTupleRes>> groupByProductId =
			// list.stream().collect(Collectors.groupingBy(PortFolioAdminTupleRes ::
			// getProductId )) ;
			for (CompanyProductMaster product : productList) {

				if (StringUtils.isBlank(req.getProductId()) || "99999".equalsIgnoreCase(req.getProductId())
						|| product.getProductId().equals(Integer.valueOf(req.getProductId()))) {

					List<PortFolioAdminTupleRes> filterProduct = list.stream()
							.filter(o -> o.getProductId() != null && o.getProductId().equals(product.getProductId()))
							.collect(Collectors.toList());
					 System.out.println("FILTER PRODUCT LIST: "+filterProduct);
					// Map Broker List
					List<PortfolioBrokerListRes> brokerResList = new ArrayList<PortfolioBrokerListRes>();
					for (PortFolioAdminTupleRes data : filterProduct) {
						PortfolioBrokerListRes brokerRes = new PortfolioBrokerListRes();

//						if(StringUtils.isNotBlank(data.getBdmCode())){
//							brokerRes.setBrokerCode(data.getCustomerCode() == null ? "0" : data.getCustomerCode().toString());
//							brokerRes.setBrokerName(data.getCustomerName());
//						} else {
						brokerRes.setBrokerCode(data.getOaCode() == null ? "0" : data.getOaCode().toString());
						brokerRes.setBrokerName(data.getBrokerName());
//						}
//						brokerRes.setBrokerLoginId(data.getLoginId());
//						brokerRes.setSubUserType(data.getSubUserType());
						brokerRes.setTotalCount(data.getCount() == null ? 0 : data.getCount());
						brokerRes.setTotalPremiumLc(data.getOverallPremiumLc() == null ? "0"
								: df.format(Double.valueOf(data.getOverallPremiumLc().toPlainString())));
						brokerRes.setTotalPremiumFc(data.getOverallPremiumFc() == null ? "0"
								: df.format(Double.valueOf(data.getOverallPremiumFc().toPlainString())));
						brokerRes.setUserType(data.getUserType());
						brokerRes.setSourceType(data.getSourceType());
						brokerRes.setBdmCode(data.getBdmCode());
						brokerResList.add(brokerRes);
						
						System.out.println("BROKER RES LIST: "+brokerResList);
					}
					brokerResList.sort(Comparator.comparing(PortfolioBrokerListRes::getTotalCount).reversed());

					// Response
					PortFolioDashBoardRes res = new PortFolioDashBoardRes();
					res.setBrokerList(brokerResList);
					res.setProductId(product.getProductId().toString());
					res.setProductName(product.getProductName());
					res.setBrokerCount(brokerResList.size() > 0 ? Long.valueOf(brokerResList.size()) : 0);
					
					System.out.println("RES LIST: "+res);
					resList.add(res);
				}

			}
			resList.sort(Comparator.comparing(PortFolioDashBoardRes::getBrokerCount).reversed());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
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
			predicate.add(cb.isNotNull(h.get("quoteNo")));
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
			}else
			{
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("D");
				statusNot.add("E");
				predicate.add(e0.in(statusNot).not());
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
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				result.setHint("javax.persistence.query.timeout", 90000);
			list = result.getResultList();
            System.out.println("PORTFOLIO LIST: "+list);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	public List<PortfolioAdminGridRes> getPortFolioGrid(PortFolioGridReq req) {
		List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
		try {
			LoginMaster logindata = null;
			if(!"Y".equalsIgnoreCase(req.getPortFolioYn())) {
				logindata = loginRepo.findByLoginId(req.getLoginId());
			}
			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();
			
			Calendar cal = new GregorianCalendar();

			Date startDate = req.getStartDate();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			startDate = cal.getTime();

			Date endDate = req.getEndDate();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			endDate = cal.getTime();

			Integer limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			Integer offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminGridRes> query = cb.createQuery(PortfolioAdminGridRes.class);

			// Find All
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<PersonalInfo> p = query.from(PersonalInfo.class);
			
			//Sum Insured
			Subquery<BigDecimal> sumInsured = query.subquery(BigDecimal.class);
			Root<PolicyCoverData> sicd = sumInsured.from(PolicyCoverData.class);
			sumInsured.select(cb.sum(sicd.get("sumInsured")))
			.where(cb.equal(sicd.get("quoteNo"), h.get("quoteNo")),
					cb.equal(sicd.get("taxId"), 0),
					cb.equal(sicd.get("discLoadId"), 0),
					cb.equal(sicd.get("coverageType"), "B"));
			
			List<Selection<?>> selections = new ArrayList<Selection<?>>();
			List<Predicate> predicate = new ArrayList<Predicate>();
			
			if(!"Q".equalsIgnoreCase(businessType)) {
				Root<PaymentDetail> pd = query.from(PaymentDetail.class);
				Root<PaymentInfo> pi = query.from(PaymentInfo.class);
				
				selections.add(pd.get("paymentTypedesc").alias("paymentType"));
				selections.add(pd.get("merchantReference").alias("merchantReference"));
				selections.add(pd.get("authTransRefNo").alias("authTransRefNo"));
				selections.add(pi.get("paymentId").alias("paymentId"));
				
				predicate.add(cb.equal(pi.get("quoteNo"), h.get("quoteNo")));
				predicate.add(cb.equal(pi.get("paymentStatus"), "ACCEPTED"));
				predicate.add(cb.equal(pd.get("paymentStatus"), pi.get("paymentStatus")));
				predicate.add(cb.equal(pd.get("quoteNo"), pi.get("quoteNo")));
				predicate.add(cb.equal(pd.get("paymentId"), pi.get("paymentId")));
			}
			
			selections.add(h.get("creditNo").alias("creditNo"));
			selections.add(h.get("debitNoteNo").alias("debitNoteNo"));
			selections.add(h.get("applicationId").alias("applicationId"));
			selections.add(h.get("noOfVehicles").as(Long.class).alias("count"));
			selections.add(h.get("overallPremiumLc").alias("overallPremiumLc"));
			selections.add(h.get("overallPremiumFc").alias("overallPremiumFc"));
			selections.add(h.get("currency").alias("currencyCode"));
			selections.add(h.get("exchangeRate").alias("exchangeRate"));
			selections.add(h.get("requestReferenceNo").alias("requestReferenceNo"));
			selections.add(h.get("quoteNo").alias("quoteNo"));
			selections.add(h.get("policyNo").alias("policyNo"));
			selections.add(h.get("originalPolicyNo").alias("originalPolicyNo"));
			selections.add(h.get("productId").alias("productId"));
			selections.add(h.get("productName").alias("productName"));
			selections.add(h.get("agencyCode").alias("oaCode"));
			selections.add(h.get("loginId").alias("loginId"));
			selections.add(h.get("referralDescription").alias("referralRemarks"));
			selections.add(h.get("adminRemarks").alias("adminRemarks"));
			selections.add(h.get("adminLoginId").alias("adminLoginId"));
			selections.add(h.get("status").alias("status"));
			selections.add(h.get("endtStatus").alias("endtStatus"));
			selections.add(h.get("inceptionDate").alias("policyStartDate"));
			selections.add(h.get("expiryDate").alias("policyEndDate"));
			selections.add(h.get("branchCode").alias("branchCode"));
			selections.add(h.get("branchName").alias("branchName"));
			selections.add(h.get("brokerBranchCode").alias("brokerBranchCode"));
			selections.add(h.get("brokerBranchName").alias("brokerBranchName"));
			selections.add(u.get("userName").alias("brokerName"));
			selections.add(l.get("userType").alias("userType"));
			selections.add(l.get("subUserType").alias("subUserType"));
			selections.add(h.get("effectiveDate").alias("updatedDate"));
			selections.add(h.get("endorsementRemarks").alias("endorsementRemarks"));
			selections.add(cb.concat(p.get("titleDesc"), cb.concat(cb.selectCase().when(cb.isNull(p.get("titleDesc")), "")
					.when(cb.equal(p.get("titleDesc"),""), "").otherwise(".").as(String.class),
					p.get("clientName"))).alias("customerName"));
			selections.add(sumInsured.alias("sumInsured"));
			selections.add(h.get("commissionPercentage").alias("commissionPercentage"));
			selections.add(h.get("commission").alias("commissionAmount"));
			selections.add(cb.selectCase().when(cb.equal(cb.upper(h.get("subUserType")), "B2C"), "B2C")
					.when(cb.equal(cb.upper(h.get("subUserType")), "WHATSAPP"), "WHATSAPP")
					.otherwise("B2B").alias("sourceType"));
			selections.add(cb.selectCase().when(cb.equal(h.get("userType"), "User"), cb.upper(u.get("userName"))).alias("userName"));
			
			query.multiselect(selections);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(h.get("effectiveDate")));

			// Broker condition
			Subquery<String> oaCode = query.subquery(String.class);
			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
			oaCode.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
			Predicate a2 = null;
			if(StringUtils.isNotBlank(req.getUserType()) && "user".equalsIgnoreCase(req.getUserType().toLowerCase())) {
				if(!"Y".equalsIgnoreCase(req.getPortFolioYn())) {
					a2 = cb.equal(ocpm1.get("agencyCode"), logindata.getAgencyCode());
				}else {
					a2 = cb.equal(ocpm1.get("agencyCode"), req.getLoginId());
				}
			}else {
				if(!"Y".equalsIgnoreCase(req.getPortFolioYn())) {
					a2 = cb.equal(ocpm1.get("oaCode"), logindata.getOaCode());
				}else {
					a2 = cb.equal(ocpm1.get("oaCode"), req.getLoginId());
				}
			}
			
			oaCode.where(a1, a2);

//			Subquery<Long> loginId = query.subquery(Long.class);
//			Root<LoginMaster> ocpm2 = loginId.from(LoginMaster.class);
//			Expression<String> e2 = ocpm2.get("oaCode");
//			loginId.select(ocpm2.get("loginId"));
//			Predicate a6 = e2.in(oaCode);
//			loginId.where(a6);

			Expression<String> e0 = null;
			if(StringUtils.isNotBlank(req.getUserType()) && "approver".equalsIgnoreCase(req.getUserType().toLowerCase())) {
				e0 = h.get("applicationId");
			}else {
				e0 = h.get("loginId");
			}
			if(StringUtils.isNotBlank(req.getLoginId()) && !"99999".equalsIgnoreCase(req.getLoginId())) {
				predicate.add(e0.in(oaCode));
			}
//			predicate.add(cb.greaterThanOrEqualTo(h.get("effectiveDate"), startDate));
//			predicate.add(cb.lessThanOrEqualTo(h.get("effectiveDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(h.get("entryDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("entryDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(u.get("loginId"), h.get("loginId")));
			if(!"99999".equalsIgnoreCase(req.getProductId())) {
				predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			}
			
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			predicate.add(cb.equal(p.get("customerId"), h.get("customerId")));
			
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
			} else if ("Q".equalsIgnoreCase(businessType)) {
				Expression<String> e1 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("E");
				predicate.add(e1.in(statusNot).not());
			}

			// Branch Condition
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);

			// Get Result
			TypedQuery<PortfolioAdminGridRes> result = em.createQuery(query);
			if(!"Y".equalsIgnoreCase(req.getShowAllDataYn())) {
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
			}
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	public List<PortfolioAdminGridRes> getPortFolioTravelGrid(PortFolioGridReq req) {
		List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
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

			Integer limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			Integer offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminGridRes> query = cb.createQuery(PortfolioAdminGridRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceTravelDetails> h = query.from(EserviceTravelDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
			// creditNo
			Subquery<Long> creditNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp = creditNo.from(HomePositionMaster.class);
			creditNo.select(cb.max(hp.get("creditNo")));
			Predicate b1 = cb.equal(h.get("quoteNo"), hp.get("quoteNo"));
			creditNo.where(b1);

			// debitNoteNo
			Subquery<Long> debitNoteNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp1 = debitNoteNo.from(HomePositionMaster.class);
			debitNoteNo.select(cb.max(hp1.get("debitNoteNo")));
			Predicate b2 = cb.equal(h.get("quoteNo"), hp1.get("quoteNo"));
			debitNoteNo.where(b2);

			// Select
			query.multiselect(creditNo.alias("creditNo"), debitNoteNo.alias("debitNoteNo"),
					h.get("applicationId").alias("applicationId"),
					cb.sum(h.get("totalPassengers")).as(Long.class).alias("count"),
					cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc"),
					h.get("currency").alias("currencyCode"), h.get("exchangeRate").alias("exchangeRate"),
					h.get("requestReferenceNo").alias("requestReferenceNo"), h.get("quoteNo").alias("quoteNo"),
					h.get("policyNo").alias("policyNo"), h.get("originalPolicyNo").alias("originalPolicyNo"),
					h.get("productId").as(Integer.class).alias("productId"), h.get("productName").alias("productName"),
					h.get("brokerCode").as(Integer.class).alias("oaCode"), h.get("loginId").alias("loginId"),
					h.get("referalRemarks").alias("referralRemarks"), h.get("adminRemarks").alias("adminRemarks"),
					h.get("adminLoginId").alias("adminLoginId"), h.get("status").alias("status"),
					h.get("endtStatus").alias("endtStatus"), c.get("clientName").alias("customerName"),
					h.get("travelStartDate").alias("policyStartDate"), h.get("travelEndDate").alias("policyEndDate"),
					h.get("branchCode").alias("branchCode"), b.get("branchName").alias("branchName"),
					h.get("brokerBranchCode").alias("brokerBranchCode"),
					h.get("brokerBranchName").alias("brokerBranchName"), u.get("userName").alias("brokerName"),
					l.get("userType").alias("userType"), l.get("subUserType").alias("subUserType"),
					h.get("updatedDate").alias("updatedDate"), h.get("endorsementRemarks").alias("endorsementRemarks"),
					hpm.get("effectiveDate").alias("effectiveDate"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(h.get("updatedDate")));

			// Broker condition
//			Subquery<Long> oaCode = query.subquery(Long.class);
//			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
//			oaCode.select(ocpm1.get("oaCode"));
//			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
//			Predicate a2 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
//			oaCode.where(a1, a2);
//
//			Subquery<Long> loginId = query.subquery(Long.class);
//			Root<LoginMaster> ocpm2 = loginId.from(LoginMaster.class);
//			Expression<String> e2 = ocpm2.get("oaCode");
//			loginId.select(ocpm2.get("loginId"));
//			Predicate a6 = e2.in(oaCode);
//			loginId.where(a6);

			Subquery<String> oaCode = query.subquery(String.class);
			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
			oaCode.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("oaCode"), req.getLoginId());
			oaCode.where(a1, a2);

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			Expression<String> e1 = h.get("loginId");
			Expression<String> e2 = hpm.get("loginId");
	        predicate.add(e1.in(oaCode));
		    predicate.add(e2.in(oaCode));
		    predicate.add(cb.equal(h.get("requestReferenceNo"),hpm.get("requestReferenceNo")));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(u.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			predicate.add(cb.equal(h.get("customerReferenceNo"), c.get("customerReferenceNo")));
			predicate.add(cb.equal(b.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(b.get("branchCode"), h.get("branchCode")));
			predicate.add(cb.equal(b.get("brokerBranchCode"), h.get("brokerBranchCode")));

			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();

			// Pending Quote Condition
			if ("Q".equalsIgnoreCase(businessType)) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
//				statusNot.add("D");
				statusNot.add("E");
				predicate.add(e0.in(statusNot).not());

				// Endt Status Not
//				Expression<String> e3 = h.get("endtStatus");
//				List<String> endtStatusNot = new ArrayList<String>();
//				endtStatusNot.add("C");
//				predicate.add(e3.in(endtStatusNot).not() );
			}

			// Branch Condition
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0]))
					.groupBy(h.get("applicationId"), h.get("currency"), h.get("exchangeRate"),
							h.get("requestReferenceNo"), h.get("quoteNo"), h.get("policyNo"), h.get("originalPolicyNo"),
							h.get("productId"), h.get("productName"), h.get("brokerCode"), h.get("loginId"),
							h.get("referalRemarks"), h.get("adminRemarks"), h.get("adminLoginId"), h.get("status"),
							h.get("endtStatus"), c.get("clientName"), h.get("travelStartDate"), h.get("travelEndDate"),
							h.get("branchCode"), b.get("branchName"), h.get("brokerBranchCode"),
							h.get("brokerBranchName"), u.get("userName"), l.get("userType"), l.get("subUserType"),
							h.get("updatedDate"), h.get("endorsementRemarks"))// ;
					.orderBy(orderList);

			// Get Result
			TypedQuery<PortfolioAdminGridRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	public List<PortfolioAdminGridRes> getPortFolioMotorGrid(PortFolioGridReq req) {
		List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
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

			Integer limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			Integer offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminGridRes> query = cb.createQuery(PortfolioAdminGridRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceMotorDetails> h = query.from(EserviceMotorDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
			// creditNo
			Subquery<Long> creditNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp = creditNo.from(HomePositionMaster.class);
			creditNo.select(cb.max(hp.get("creditNo")));
			Predicate b1 = cb.equal(h.get("quoteNo"), hp.get("quoteNo"));
			creditNo.where(b1);

			// debitNoteNo
			Subquery<Long> debitNoteNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp1 = debitNoteNo.from(HomePositionMaster.class);
			debitNoteNo.select(cb.max(hp1.get("debitNoteNo")));
			Predicate b2 = cb.equal(h.get("quoteNo"), hp1.get("quoteNo"));
			debitNoteNo.where(b2);

			// Select
			query.multiselect(creditNo.alias("creditNo"), debitNoteNo.alias("debitNoteNo"),
					h.get("applicationId").alias("applicationId"), cb.count(h).alias("count"),
					cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc"),
					h.get("currency").alias("currencyCode"), h.get("exchangeRate").alias("exchangeRate"),
					h.get("requestReferenceNo").alias("requestReferenceNo"), cb.max(h.get("quoteNo")).alias("quoteNo"),
					cb.max(h.get("policyNo")).alias("policyNo"), h.get("originalPolicyNo").alias("originalPolicyNo"),
					h.get("productId").as(Integer.class).alias("productId"), h.get("productName").alias("productName"),
					h.get("brokerCode").as(Integer.class).alias("oaCode"), h.get("loginId").alias("loginId"),
					cb.max(h.get("referalRemarks")).alias("referralRemarks"),
					h.get("adminRemarks").alias("adminRemarks"), h.get("adminLoginId").alias("adminLoginId"),
					cb.max(h.get("status")).alias("status"), cb.max(h.get("endtStatus")).alias("endtStatus"),
					c.get("clientName").alias("customerName"), h.get("policyStartDate").alias("policyStartDate"),
					h.get("policyEndDate").alias("policyEndDate"), h.get("branchCode").alias("branchCode"),
					b.get("branchName").alias("branchName"), h.get("brokerBranchCode").alias("brokerBranchCode"),
					h.get("brokerBranchName").alias("brokerBranchName"), u.get("userName").alias("brokerName"),
					l.get("userType").alias("userType"), l.get("subUserType").alias("subUserType"),
					cb.max(h.get("updatedDate")).alias("updatedDate"),
					h.get("endorsementRemarks").alias("endorsementRemarks"),
					hpm.get("effectiveDate").alias("effectiveDate"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(h.get("updatedDate")));

//			// Broker condition
//			Subquery<Long> oaCode = query.subquery(Long.class);
//			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
//			oaCode.select(ocpm1.get("oaCode"));
//			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
//			Predicate a2 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
//			oaCode.where(a1, a2);
//
//			Subquery<Long> loginId = query.subquery(Long.class);
//			Root<LoginMaster> ocpm2 = loginId.from(LoginMaster.class);
//			Expression<String> e2 = ocpm2.get("oaCode");
//			loginId.select(ocpm2.get("loginId"));
//			Predicate a6 = e2.in(oaCode);
//			loginId.where(a6);
			// Broker condition
			Subquery<String> oaCode = query.subquery(String.class);
			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
			oaCode.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("oaCode"), req.getLoginId());
			oaCode.where(a1, a2);

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			Expression<String> e1 = h.get("loginId");
			Expression<String> e2 = hpm.get("loginId");
	        predicate.add(e1.in(oaCode));
		    predicate.add(e2.in(oaCode));
		    predicate.add(cb.equal(h.get("requestReferenceNo"),hpm.get("requestReferenceNo")));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(u.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			predicate.add(cb.equal(h.get("customerReferenceNo"), c.get("customerReferenceNo")));
			predicate.add(cb.equal(b.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(b.get("branchCode"), h.get("branchCode")));
			predicate.add(cb.equal(b.get("brokerBranchCode"), h.get("brokerBranchCode")));

			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();

			// Pending Quote Condition
			if ("Q".equalsIgnoreCase(businessType)) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("D");
				statusNot.add("E");
				predicate.add(e0.in(statusNot).not());

				// Endt Status Not
				Expression<String> e3 = h.get("endtStatus");
//				List<String> endtStatusNot = new ArrayList<String>();
//				endtStatusNot.add("C");
//				predicate.add(e3.in(endtStatusNot).not() );
			}

			// Branch Condition
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0]))
					.groupBy(h.get("applicationId"), h.get("currency"), h.get("exchangeRate"),
							h.get("requestReferenceNo"), h.get("quoteNo"), h.get("policyNo"), h.get("originalPolicyNo"),
							h.get("productId"), h.get("productName"), h.get("brokerCode"), h.get("loginId"),
							h.get("referalRemarks"), h.get("adminRemarks"), h.get("adminLoginId"), h.get("status"),
							h.get("endtStatus"), c.get("clientName"), h.get("policyStartDate"), h.get("policyEndDate"),
							h.get("branchCode"), b.get("branchName"), h.get("brokerBranchCode"),
							h.get("brokerBranchName"), u.get("userName"), l.get("userType"), l.get("subUserType"),
							h.get("updatedDate"), h.get("endorsementRemarks"))// ;
					.orderBy(orderList);

			// Get Result
			TypedQuery<PortfolioAdminGridRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	public List<PortfolioAdminGridRes> getPortFolioBuildingGrid(PortFolioGridReq req) {
		List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
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

			Integer limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			Integer offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminGridRes> query = cb.createQuery(PortfolioAdminGridRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceBuildingDetails> h = query.from(EserviceBuildingDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
			// creditNo
			Subquery<Long> creditNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp = creditNo.from(HomePositionMaster.class);
			creditNo.select(cb.max(hp.get("creditNo")));
			Predicate b1 = cb.equal(h.get("quoteNo"), hp.get("quoteNo"));
			creditNo.where(b1);

			// debitNoteNo
			Subquery<Long> debitNoteNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp1 = debitNoteNo.from(HomePositionMaster.class);
			debitNoteNo.select(cb.max(hp1.get("debitNoteNo")));
			Predicate b2 = cb.equal(h.get("quoteNo"), hp1.get("quoteNo"));
			debitNoteNo.where(b2);

			// Select
			query.multiselect(creditNo.alias("creditNo"), debitNoteNo.alias("debitNoteNo"),

					h.get("applicationId").alias("applicationId"), cb.count(h).alias("count"),
					cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc"),
					h.get("currency").alias("currencyCode"), h.get("exchangeRate").alias("exchangeRate"),
					h.get("requestReferenceNo").alias("requestReferenceNo"), h.get("quoteNo").alias("quoteNo"),
					h.get("policyNo").alias("policyNo"), h.get("originalPolicyNo").alias("originalPolicyNo"),
					h.get("productId").as(Integer.class).alias("productId"), h.get("productDesc").alias("productName"),
					h.get("brokerCode").as(Integer.class).alias("oaCode"), h.get("loginId").alias("loginId"),
					h.get("referalRemarks").alias("referralRemarks"), h.get("adminRemarks").alias("adminRemarks"),
					h.get("adminLoginId").alias("adminLoginId"), h.get("status").alias("status"),
					h.get("endtStatus").alias("endtStatus"), c.get("clientName").alias("customerName"),
					h.get("policyStartDate").alias("policyStartDate"), h.get("policyEndDate").alias("policyEndDate"),
					h.get("branchCode").alias("branchCode"), b.get("branchName").alias("branchName"),
					h.get("brokerBranchCode").alias("brokerBranchCode"),
					h.get("brokerBranchName").alias("brokerBranchName"), u.get("userName").alias("brokerName"),
					l.get("userType").alias("userType"), l.get("subUserType").alias("subUserType"),
					h.get("updatedDate").alias("updatedDate"), h.get("endorsementRemarks").alias("endorsementRemarks"));
				//	hp.get("effectiveDate").alias("effectiveDate"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(h.get("updatedDate")));

//			// Broker condition
//			Subquery<Long> oaCode = query.subquery(Long.class);
//			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
//			oaCode.select(ocpm1.get("oaCode"));
//			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
//			Predicate a2 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
//			oaCode.where(a1, a2);
//
//			Subquery<Long> loginId = query.subquery(Long.class);
//			Root<LoginMaster> ocpm2 = loginId.from(LoginMaster.class);
//			Expression<String> e2 = ocpm2.get("oaCode");
//			loginId.select(ocpm2.get("loginId"));
//			Predicate a6 = e2.in(oaCode);
//			loginId.where(a6);

			// Broker condition
			Subquery<String> oaCode = query.subquery(String.class);
			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
			oaCode.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("oaCode"), req.getLoginId());
			oaCode.where(a1, a2);

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			Expression<String> e1 = h.get("loginId");
			Expression<String> e2 = hpm.get("loginId");
			predicate.add(e1.in(oaCode));
			predicate.add(e2.in(oaCode));
			predicate.add(cb.equal(h.get("requestReferenceNo"), hpm.get("requestReferenceNo")));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(u.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			predicate.add(cb.equal(h.get("customerReferenceNo"), c.get("customerReferenceNo")));
			predicate.add(cb.equal(b.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(b.get("branchCode"), h.get("branchCode")));
			predicate.add(cb.equal(b.get("brokerBranchCode"), h.get("brokerBranchCode")));
			predicate.add(cb.isNotNull(hpm.get("quoteNo")));

			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();

			// Pending Quote Condition
			if ("Q".equalsIgnoreCase(businessType)) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("D");
				statusNot.add("E");
				predicate.add(e0.in(statusNot).not());

//				// Endt Status Not
//				Expression<String> e3 = h.get("endtStatus");
//				List<String> endtStatusNot = new ArrayList<String>();
//				endtStatusNot.add("C");
//				predicate.add(e3.in(endtStatusNot).not() );
			}

			// Branch Condition
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0]))
					.groupBy(h.get("applicationId"), h.get("currency"), h.get("exchangeRate"),
							h.get("requestReferenceNo"), h.get("quoteNo"), h.get("policyNo"), h.get("originalPolicyNo"),
							h.get("productId"), h.get("productDesc"), h.get("brokerCode"), h.get("loginId"),
							h.get("referalRemarks"), h.get("adminRemarks"), h.get("adminLoginId"), h.get("status"),
							h.get("endtStatus"), c.get("clientName"), h.get("policyStartDate"), h.get("policyEndDate"),
							h.get("branchCode"), b.get("branchName"), h.get("brokerBranchCode"),
							h.get("brokerBranchName"), u.get("userName"), l.get("userType"), l.get("subUserType"),
							h.get("updatedDate"), h.get("endorsementRemarks"))// ;
					.orderBy(orderList);

			// Get Result
			TypedQuery<PortfolioAdminGridRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	public List<PortfolioAdminGridRes> getPortFolioHumanGrid(PortFolioGridReq req) {
		List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
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

			Integer limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			Integer offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminGridRes> query = cb.createQuery(PortfolioAdminGridRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceCommonDetails> h = query.from(EserviceCommonDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
			// creditNo
			Subquery<Long> creditNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp = creditNo.from(HomePositionMaster.class);
			creditNo.select(cb.max(hp.get("creditNo")));
			Predicate b1 = cb.equal(h.get("quoteNo"), hp.get("quoteNo"));
			creditNo.where(b1);

			// debitNoteNo
			Subquery<Long> debitNoteNo = query.subquery(Long.class);
			Root<HomePositionMaster> hp1 = debitNoteNo.from(HomePositionMaster.class);
			debitNoteNo.select(cb.max(hp1.get("debitNoteNo")));
			Predicate b2 = cb.equal(h.get("quoteNo"), hp1.get("quoteNo"));
			debitNoteNo.where(b2);

			// Select
			query.multiselect(creditNo.alias("creditNo"), debitNoteNo.alias("debitNoteNo"),

					h.get("applicationId").alias("applicationId"), cb.count(h).alias("count"),
					cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc"),
					h.get("currency").alias("currencyCode"), h.get("exchangeRate").alias("exchangeRate"),
					h.get("requestReferenceNo").alias("requestReferenceNo"), cb.max(h.get("quoteNo")).alias("quoteNo"),
					cb.max(h.get("policyNo")).alias("policyNo"), h.get("originalPolicyNo").alias("originalPolicyNo"),
					h.get("productId").as(Integer.class).alias("productId"), h.get("productDesc").alias("productName"),
					h.get("brokerCode").as(Integer.class).alias("oaCode"), h.get("loginId").alias("loginId"),
					cb.max(h.get("referalRemarks")).alias("referralRemarks"),
					h.get("adminRemarks").alias("adminRemarks"), h.get("adminLoginId").alias("adminLoginId"),
					cb.max(h.get("status")).alias("status"), cb.max(h.get("endtStatus")).alias("endtStatus"),
					c.get("clientName").alias("customerName"), h.get("policyStartDate").alias("policyStartDate"),
					h.get("policyEndDate").alias("policyEndDate"), h.get("branchCode").alias("branchCode"),
					b.get("branchName").alias("branchName"), h.get("brokerBranchCode").alias("brokerBranchCode"),
					h.get("brokerBranchName").alias("brokerBranchName"), u.get("userName").alias("brokerName"),
					l.get("userType").alias("userType"), l.get("subUserType").alias("subUserType"),
					cb.max(h.get("updatedDate")).alias("updatedDate"),
					h.get("endorsementRemarks").alias("endorsementRemarks"),
					hpm.get("effectiveDate").alias("effectiveDate"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(h.get("updatedDate")));

//			// Broker condition
//			Subquery<Long> oaCode = query.subquery(Long.class);
//			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
//			oaCode.select(ocpm1.get("oaCode"));
//			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
//			Predicate a2 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
//			oaCode.where(a1, a2);
//
//			Subquery<Long> loginId = query.subquery(Long.class);
//			Root<LoginMaster> ocpm2 = loginId.from(LoginMaster.class);
//			Expression<String> e2 = ocpm2.get("oaCode");
//			loginId.select(ocpm2.get("loginId"));
//			Predicate a6 = e2.in(oaCode);
//			loginId.where(a6);

			// Broker condition
			Subquery<String> oaCode = query.subquery(String.class);
			Root<LoginMaster> ocpm1 = oaCode.from(LoginMaster.class);
			oaCode.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("oaCode"), req.getLoginId());
			oaCode.where(a1, a2);

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			Expression<String> e1 = h.get("loginId");
			Expression<String> e2 = hpm.get("loginId");
	        predicate.add(e1.in(oaCode));
		    predicate.add(e2.in(oaCode));
		    predicate.add(cb.equal(h.get("requestReferenceNo"),hpm.get("requestReferenceNo")));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(u.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			predicate.add(cb.equal(h.get("customerReferenceNo"), c.get("customerReferenceNo")));
			predicate.add(cb.equal(b.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(b.get("branchCode"), h.get("branchCode")));
			predicate.add(cb.equal(b.get("brokerBranchCode"), h.get("brokerBranchCode")));
			predicate.add(cb.isNotNull(hpm.get("quoteNo")));
			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();

			// Pending Quote Condition
			if ("Q".equalsIgnoreCase(businessType)) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("D");
				statusNot.add("E");
				predicate.add(e0.in(statusNot).not());

//				// Endt Status Not
//							Expression<String> e3 = h.get("endtStatus");
//							List<String> endtStatusNot = new ArrayList<String>();
//							endtStatusNot.add("C");
//							predicate.add(e3.in(endtStatusNot).not() );
			}

			// Branch Condition
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0]))
					.groupBy(h.get("applicationId"), h.get("currency"), h.get("exchangeRate"),
							h.get("requestReferenceNo"), h.get("quoteNo"), h.get("policyNo"), h.get("originalPolicyNo"),
							h.get("productId"), h.get("productDesc"), h.get("brokerCode"), h.get("loginId"),
							h.get("referalRemarks"), h.get("adminRemarks"), h.get("adminLoginId"), h.get("status"),
							h.get("endtStatus"), c.get("clientName"), h.get("policyStartDate"), h.get("policyEndDate"),
							h.get("branchCode"), b.get("branchName"), h.get("brokerBranchCode"),
							h.get("brokerBranchName"), u.get("userName"), l.get("userType"), l.get("subUserType"),
							h.get("updatedDate"), h.get("endorsementRemarks"))// ;
					.orderBy(orderList);

			// Get Result
			TypedQuery<PortfolioAdminGridRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	@Override
	public List<PortFolioDashBoardRes> getAllPolicyPendingDashboard(PortFolioDashBoardReq req) {
		List<PortFolioDashBoardRes> resList = new ArrayList<PortFolioDashBoardRes>();
		DecimalFormat df = new DecimalFormat("0.##");
		try {
			// Thread Call Setup To Fetch List From 4 tables
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			MyTaskList taskList = new MyTaskList(queue);
			PortFolioFetchThreadCall motorPendings = new PortFolioFetchThreadCall("getPortFolioMotorPendings", req, em);
			PortFolioFetchThreadCall travelPendings = new PortFolioFetchThreadCall("getPortFolioTravelPendings", req,
					em);
			PortFolioFetchThreadCall buildingPendings = new PortFolioFetchThreadCall("getPortFolioBuildingPendings",
					req, em);
			PortFolioFetchThreadCall humanPendings = new PortFolioFetchThreadCall("getPortFolioHumanPendings", req, em);

			queue.add(motorPendings);
			queue.add(travelPendings);
			queue.add(buildingPendings);
			queue.add(humanPendings);
			int threadCount = 4;
			int success = 0;
			ForkJoinPool forkjoin = new ForkJoinPool(threadCount);
			ConcurrentLinkedQueue<Future<Object>> invoke = (ConcurrentLinkedQueue<Future<Object>>) forkjoin
					.invoke(taskList);

			List<PortfolioAdminPendingRes> motorList = new ArrayList<PortfolioAdminPendingRes>();
			List<PortfolioAdminPendingRes> travelList = new ArrayList<PortfolioAdminPendingRes>();
			List<PortfolioAdminPendingRes> buildingList = new ArrayList<PortfolioAdminPendingRes>();
			List<PortfolioAdminPendingRes> humanList = new ArrayList<PortfolioAdminPendingRes>();

			for (Future<Object> callable : invoke) {

				log.info(callable.getClass() + "," + callable.isDone());

				if (callable.isDone()) {
					Map<String, Object> map = (Map<String, Object>) callable.get();

					for (Entry<String, Object> future : map.entrySet()) {

						if ("getPortFolioMotorPendings".equalsIgnoreCase(future.getKey())) {
							motorList = (List<PortfolioAdminPendingRes>) future.getValue();

						} else if ("getPortFolioTravelPendings".equalsIgnoreCase(future.getKey())) {
							travelList = (List<PortfolioAdminPendingRes>) future.getValue();

						} else if ("getPortFolioBuildingPendings".equalsIgnoreCase(future.getKey())) {
							buildingList = (List<PortfolioAdminPendingRes>) future.getValue();

						} else if ("getPortFolioHumanPendings".equalsIgnoreCase(future.getKey())) {
							humanList = (List<PortfolioAdminPendingRes>) future.getValue();
						}
					}

					success++;
				}
			}

			List<CompanyProductMaster> productList = getCompanyProductList(req.getInsuranceId());

			// Group By Product Id
			for (CompanyProductMaster product : productList) {

				if (StringUtils.isBlank(req.getProductId()) || "99999".equalsIgnoreCase(req.getProductId())
						|| product.getProductId().equals(Integer.valueOf(req.getProductId()))) {

					String productType = StringUtils.isBlank(product.getMotorYn()) ? "M" : product.getMotorYn();
					List<PortfolioAdminPendingRes> filterProduct = new ArrayList<PortfolioAdminPendingRes>();

					if ("H".equalsIgnoreCase(productType) && product.getProductId().equals(4))
						filterProduct = travelList; // travelList.stream().filter( o -> o.getProductId()!=null &&
													// o.getProductId().equals(product.getProductId() )
													// ).collect(Collectors.toList());

					else if ("M".equalsIgnoreCase(productType))
						filterProduct = motorList.stream().filter(
								o -> o.getProductId() != null && o.getProductId().equals(product.getProductId()))
								.collect(Collectors.toList());

					else if ("A".equalsIgnoreCase(productType))
						filterProduct = buildingList.stream().filter(
								o -> o.getProductId() != null && o.getProductId().equals(product.getProductId()))
								.collect(Collectors.toList());

					else if ("H".equalsIgnoreCase(productType))
						filterProduct = humanList.stream().filter(
								o -> o.getProductId() != null && o.getProductId().equals(product.getProductId()))
								.collect(Collectors.toList());

					// Map Broker List
					List<PortfolioBrokerListRes> brokerResList = new ArrayList<PortfolioBrokerListRes>();

					for (PortfolioAdminPendingRes data : filterProduct) {
						// System.out.println(format.format(price));
						PortfolioBrokerListRes brokerRes = new PortfolioBrokerListRes();

						brokerRes.setBrokerCode(data.getOaCode() == null ? "0" : data.getOaCode().toString());
						brokerRes.setBrokerLoginId(data.getLoginId());
						brokerRes.setBrokerName(data.getBrokerName());
						brokerRes.setSubUserType(data.getSubUserType());
						brokerRes.setTotalCount(data.getCount() == null ? 0 : data.getCount());
						brokerRes.setTotalPremiumLc(data.getOverallPremiumLc() == null ? "0"
								: df.format(Double.valueOf(data.getOverallPremiumLc().toPlainString())));
						brokerRes.setTotalPremiumFc(data.getOverallPremiumFc() == null ? "0"
								: df.format(Double.valueOf(data.getOverallPremiumFc().toPlainString())));
						brokerRes.setUserType(data.getUserType());
						brokerResList.add(brokerRes);
					}
					brokerResList.sort(Comparator.comparing(PortfolioBrokerListRes::getTotalCount).reversed());

					// Response
					PortFolioDashBoardRes res = new PortFolioDashBoardRes();
					res.setBrokerList(brokerResList);
					res.setProductId(product.getProductId().toString());
					res.setProductName(product.getProductName());
					res.setBrokerCount(brokerResList.size() > 0 ? Long.valueOf(brokerResList.size()) : 0);
					resList.add(res);
				}

			}
			resList.sort(Comparator.comparing(PortFolioDashBoardRes::getBrokerCount).reversed());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<PortfolioGridRes> getAllPolicyGrid(PortFolioGridReq req) {
		List<PortfolioGridRes> resList = new ArrayList<PortfolioGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("0.##");
		try {

			// Fetch Data
			List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
			String bsType = StringUtils.isNotBlank(req.getBusinessType()) ? req.getBusinessType() : "N";

			if ("N".equalsIgnoreCase(bsType) || "E".equalsIgnoreCase(bsType) || "C".equalsIgnoreCase(bsType) || "Q".equalsIgnoreCase(bsType)) {
				list = getPortFolioGrid(req);
			} else {
				CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
						req.getProductId());
				String productType = StringUtils.isNotBlank(product.getMotorYn()) ? product.getMotorYn() : "M";

				if ("H".equalsIgnoreCase(productType) && "4".equalsIgnoreCase(req.getProductId())) {
					list = getPortFolioTravelGrid(req);

				} else if ("M".equalsIgnoreCase(productType)) {
					list = getPortFolioMotorGrid(req);

				} else if ("A".equalsIgnoreCase(productType)) {
					list = getPortFolioBuildingGrid(req);

				} else if ("H".equalsIgnoreCase(productType)) {
					list = getPortFolioHumanGrid(req);
				}
			}

			// Sort By Broker Name
//			list.sort(Comparator.comparing(PortfolioAdminGridRes :: getBrokerName  ) 
//					 .thenComparing(PortfolioAdminGridRes :: getUpdatedDate  ).reversed());

			// Sort By Updated Date
			list.sort(Comparator.comparing(PortfolioAdminGridRes::getUpdatedDate).reversed());

			// Map Broker List
			for (PortfolioAdminGridRes data : list) {
				PortfolioGridRes res = new PortfolioGridRes();

				res.setApplicationId(data.getApplicationId() == null ? "" : data.getApplicationId().toString());
				res.setBrokerCode(data.getOaCode() == null ? "" : data.getOaCode().toString());
				res.setBrokerLoginId(data.getLoginId());
				res.setBrokerName(data.getBrokerName());
				res.setUserName(data.getUserName());
				res.setBranchCode(data.getBranchCode());
				res.setBranchName(data.getBranchName());
				res.setBrokerBranchCode(data.getBrokerBranchCode());
				res.setBrokerBranchName(data.getBrokerBranchName());
				res.setCount(data.getCount());
				res.setCurrencyCode(data.getCurrencyCode());
				res.setCustomerName(data.getCustomerName());
				res.setExchangeRate(data.getExchangeRate() == null ? "" : data.getExchangeRate().toPlainString());
				res.setOriginalPolicyNo(StringUtils.isNotBlank(data.getOriginalPolicyNo()) ? data.getOriginalPolicyNo()
						: data.getPolicyNo());
				res.setOverallPremiumFc(data.getOverallPremiumFc() == null ? "0"
						: df.format(Double.valueOf(data.getOverallPremiumFc().toPlainString())));
				res.setOverallPremiumLc(data.getOverallPremiumLc() == null ? "0"
						: df.format(Double.valueOf(data.getOverallPremiumLc().toPlainString())));
				res.setPolicyStartDate(data.getPolicyStartDate() == null ? "" : sdf.format(data.getPolicyStartDate()));
				res.setPolicyEndDate(data.getPolicyEndDate() == null ? "" : sdf.format(data.getPolicyEndDate()));
				res.setCommissionPercentage(data.getCommissionPercentage()== null ? "0": df.format(Double.valueOf(data.getCommissionPercentage().toPlainString())));
				res.setCommissionAmount(data.getCommissionAmount()== null ? "0": df.format(Double.valueOf(data.getCommissionAmount().toPlainString())));
				res.setPolicyNo(data.getPolicyNo());
				res.setQuoteNo(data.getQuoteNo());
				res.setRequestReferenceNo(data.getRequestReferenceNo());
				res.setSubUserType(data.getSubUserType());
				res.setUserType(data.getUserType());
				res.setProductId(data.getProductId() == null ? "" : data.getProductId().toString());
				res.setProductName(data.getProductName());
				res.setAdminRemarks(data.getAdminRemarks());
				res.setReferralRemarks(data.getReferralRemarks());
				res.setAdminLoginId(data.getAdminLoginId());
				res.setStatus(data.getStatus());
				res.setEndtSatus(data.getEndtStatus());
				res.setUpdatedDate(data.getUpdatedDate() == null ? "" : sdf.format(data.getUpdatedDate()));
				res.setEndorsementRemarks(data.getEndorsementRemarks());
				String statusDesc = StringUtils.isBlank(data.getStatus()) ? ""
						: "Y".equalsIgnoreCase(data.getStatus()) ? "Existing Quote"
								: "R".equalsIgnoreCase(data.getStatus()) ? "Quote Rejected"
										: "N".equalsIgnoreCase(data.getStatus()) ? "Quote Deactivated"
												: "D".equalsIgnoreCase(data.getStatus()) ? "Quote Deleted"
														: "RP".equalsIgnoreCase(data.getStatus()) ? "Refferral Pending"
																: "RA".equalsIgnoreCase(data.getStatus())
																		? "Refferral Approved"
																		: "RR".equalsIgnoreCase(data.getStatus())
																				? "Refferral Rejected"
																				: "RA".equalsIgnoreCase(
																						data.getStatus())
																								? "Refferral Request"
																								: "P".equalsIgnoreCase(
																										data.getStatus())
																												? "Policy Converted"
																												: "E".equalsIgnoreCase(
																														data.getStatus())
																																? "Endorsement"
																																: "";

				String endtStatusDesc = StringUtils.isBlank(data.getEndtStatus()) ? ""
						: "P".equalsIgnoreCase(data.getEndtStatus()) ? "Pending"
								: "C".equalsIgnoreCase(data.getEndtStatus()) ? "Completed" : "";

				res.setStatusDesc(statusDesc);
				res.setEndtStatusDesc(endtStatusDesc);
				res.setSumInsured(data.getSumInsured() == null ? "0"
						: df.format(Double.valueOf(data.getSumInsured().toPlainString())));
				res.setPaymentType(data.getPaymentType()==null?"":data.getPaymentType());
				res.setMerchantReference(data.getMerchantReference()==null?"":data.getMerchantReference());
				res.setAuthTransRefNo(data.getAuthTransRefNo()==null?"":data.getAuthTransRefNo());
				res.setPaymentId(data.getPaymentId()==null?"":data.getPaymentId());
				res.setSourceType(data.getSourceType()==null?"":data.getSourceType());
						
				res.setCreditNo(data.getCreditNo());
				res.setDebitNo(data.getDebitNoteNo());

				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetApproverListRes> getApproverList(GetApproverListReq req) {
		List<GetApproverListRes> resList = new ArrayList<GetApproverListRes>();
		try {
			//
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
			List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();

			Root<LoginProductMaster> c = query.from(LoginProductMaster.class);

			query.select(c);

			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("loginId")));

			Subquery<String> loginId = query.subquery(String.class);
			Root<LoginBranchMaster> ocpm2 = loginId.from(LoginBranchMaster.class);
			loginId.select(ocpm2.get("loginId"));
			Predicate a4 = cb.equal(ocpm2.get("branchCode"), req.getBranchCode());
			Predicate a5 = cb.equal(c.get("loginId"), ocpm2.get("loginId"));
			Predicate a3 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			loginId.where(a3, a4, a5);

			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(cb.lower(c.get("userType")), "issuer");
			Predicate n4 = cb.or(cb.equal(cb.lower(c.get("subUserType")), "high"),
					cb.equal(cb.lower(c.get("subUserType")), "both"));
			Predicate n5 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n6 = cb.greaterThanOrEqualTo(c.get("sumInsuredEnd"), req.getSumInsured());
			Predicate n7 = cb.lessThanOrEqualTo(c.get("sumInsuredStart"), req.getSumInsured());
			Predicate n11 = cb.and(n6, n7);

			Predicate n9 = cb.greaterThanOrEqualTo(c.get("effectiveDateEnd"), new Date());
			Predicate n10 = cb.lessThanOrEqualTo(c.get("effectiveDateStart"), new Date());
			Predicate n12 = cb.and(n9, n10);

			Predicate n8 = cb.equal(c.get("loginId"), loginId);

			query.where(n1, n2, n3, n4, n5, n11, n8, n12).orderBy(orderList);

			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getLoginId()))).collect(Collectors.toList());

			if (list.size() > 0) {

				for (LoginProductMaster data : list) {
					GetApproverListRes res = new GetApproverListRes();
					res.setLoginId(data.getLoginId() == null ? "" : data.getLoginId());
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

	@Override
	public RevertGridRes getUwPendingGrid(RevertGridReq req) {
		RevertGridRes res = new RevertGridRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWReferralDetails> query = cb.createQuery(UWReferralDetails.class);
			List<UWReferralDetails> list = new ArrayList<UWReferralDetails>();

			Root<UWReferralDetails> c = query.from(UWReferralDetails.class);

			query.select(c);

			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("entryDate")));

			Predicate n1 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n2 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n3 = cb.equal(c.get("productId"), req.getProductId());
//			Predicate n4 = cb.equal(c.get("uwLoginId"), req.getLoginId());
			Predicate n5 = cb.equal(c.get("requestReferenceNo"), req.getRequestReferenceNo());

			query.where(n1, n2, n3, n5).orderBy(orderList);

			TypedQuery<UWReferralDetails> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getUwLoginId())))
					.collect(Collectors.toList());

			Long resCount = getUwPendingGridCount(req);
			if (list.size() > 0) {
				List<RevertGridListRes> resList = new ArrayList<RevertGridListRes>();
				for (UWReferralDetails data : list) {
					RevertGridListRes res1 = new RevertGridListRes();
					res1.setUnderWriterLoginId(data.getUwLoginId() == null ? "" : data.getUwLoginId());
					res1.setBranchCode(data.getBranchCode() == null ? "" : data.getBranchCode());
					res1.setProductId(data.getProductId() == null ? "" : data.getProductId().toString());
					res1.setStatus(data.getUwStatus() == null ? "" : data.getUwStatus());
					res1.setRequestReferenceNo(
							data.getRequestReferenceNo() == null ? "" : data.getRequestReferenceNo());
					res1.setEntryDate(data.getEntryDate() == null ? null : sdf.format(data.getEntryDate()));
					res1.setUwStatus(data.getUwStatus() == null ? null : data.getUwStatus());
					resList.add(res1);
				}
				res.setCount(resCount);
				res.setPendingList(resList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	public Long getUwPendingGridCount(RevertGridReq req) {
		Long count = 0l;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWReferralDetails> query = cb.createQuery(UWReferralDetails.class);
			List<UWReferralDetails> list = new ArrayList<UWReferralDetails>();

			Root<UWReferralDetails> c = query.from(UWReferralDetails.class);

			query.select(c);

			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("entryDate")));

			Predicate n1 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n2 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n3 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(c.get("requestReferenceNo"), req.getRequestReferenceNo());

			query.where(n1, n2, n3, n5).orderBy(orderList);

			TypedQuery<UWReferralDetails> result = em.createQuery(query);
			list = result.getResultList();
			count = Long.valueOf(list.size());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	}

	@Override
	public AdminPendingGridRes getReAllotUwPendingGrid(RevertGridReq req) {
		AdminPendingGridRes custRes = new AdminPendingGridRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<BranchMaster> branchlist = getByBranchCode(req.getInsuranceId());
			String branchName = "";

			List<AdminPendingGridListRes> resList = new ArrayList<AdminPendingGridListRes>();
			List<ReferalGridCriteriaAdminRes> adminReferralPendingList = new ArrayList<ReferalGridCriteriaAdminRes>();
			List<ReferalGridCriteriaAdminRes> adminReferralPendingListCount = new ArrayList<ReferalGridCriteriaAdminRes>();

			if (product.getMotorYn().equalsIgnoreCase("M")) {

				GetMotorAdminReferalPendingDetailsRes response = motService.getMotorAdminReferalPendingDetails(req,
						limit, offset, "RP");
				List<MotorGridCriteriaAdminRes> adminReferralPendingList2 = response.getMotorGridCriteriaAdminRes();

				for (MotorGridCriteriaAdminRes data : adminReferralPendingList2) {
					AdminPendingGridListRes res = new AdminPendingGridListRes();
					if (data.getBranchCode() != null && StringUtils.isNotBlank(data.getBranchCode())) {
						List<BranchMaster> filterProducts = branchlist.stream()
								.filter(o -> o.getBranchCode().equalsIgnoreCase(data.getBranchCode()))
								.collect(Collectors.toList());
						branchName = filterProducts.get(0).getBranchName();
					}
					res = dozerMapper.map(data, AdminPendingGridListRes.class);
					res.setBranchName(branchName);
					resList.add(res);
				}

				custRes.setCount(response.getCount());
				custRes.setAdminPendingGridListRes(resList);

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				GetTravelAdminReferalPendingDetailsRes response = traService.getTravelAdminReferalPendingDetails(req,
						limit, offset, "RP");

				adminReferralPendingList = response.getReferalGridCriteriaAdminRes();

				for (ReferalGridCriteriaAdminRes data : adminReferralPendingList) {
					AdminPendingGridListRes res = new AdminPendingGridListRes();
					if (data.getBranchCode() != null && StringUtils.isNotBlank(data.getBranchCode())) {
						List<BranchMaster> filterProducts = branchlist.stream()
								.filter(o -> o.getBranchCode().equalsIgnoreCase(data.getBranchCode()))
								.collect(Collectors.toList());
						branchName = filterProducts.get(0).getBranchName();
					}
					res = dozerMapper.map(data, AdminPendingGridListRes.class);
					res.setBranchName(branchName);
					resList.add(res);
				}

				custRes.setCount(response.getCount());
				custRes.setAdminPendingGridListRes(resList);

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				GetBuildingAdminReferalPendingDetailsRes response = buiService
						.getBuildingAdminReferalPendingDetails(req, limit, offset, "RP");
				adminReferralPendingList = response.getReferalGridCriteriaAdminRes();

				for (ReferalGridCriteriaAdminRes data : adminReferralPendingList) {
					AdminPendingGridListRes res = new AdminPendingGridListRes();
					if (data.getBranchCode() != null && StringUtils.isNotBlank(data.getBranchCode())) {
						List<BranchMaster> filterProducts = branchlist.stream()
								.filter(o -> o.getBranchCode().equalsIgnoreCase(data.getBranchCode()))
								.collect(Collectors.toList());
						branchName = filterProducts.get(0).getBranchName();
					}
					res = dozerMapper.map(data, AdminPendingGridListRes.class);
					res.setBranchName(branchName);
					resList.add(res);
				}
				custRes.setCount(response.getCount());
				custRes.setAdminPendingGridListRes(resList);

			} else {
				GetBuildingAdminReferalPendingDetailsRes response = commonService
						.getCommonAdminReferalPendingDetails(req, limit, offset, "RP");

				List<ReferalGridCriteriaAdminRes> adminReferralPendingList2 = response.getReferalGridCriteriaAdminRes();

				for (ReferalGridCriteriaAdminRes data : adminReferralPendingList2) {
					AdminPendingGridListRes res = new AdminPendingGridListRes();

					if (data.getBranchCode() != null && StringUtils.isNotBlank(data.getBranchCode())) {
						List<BranchMaster> filterProducts = branchlist.stream()
								.filter(o -> o.getBranchCode().equalsIgnoreCase(data.getBranchCode()))
								.collect(Collectors.toList());
						branchName = filterProducts.get(0).getBranchName();
					}
					res = dozerMapper.map(data, AdminPendingGridListRes.class);
					res.setBranchName(branchName);
					resList.add(res);
				}
				custRes.setCount(response.getCount());
				custRes.setAdminPendingGridListRes(resList);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	// BranchName
	public List<BranchMaster> getByBranchCode(String companyId) {
		// BranchMaster res = new BranchMasterRes();
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
			query.select(c);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BranchMaster> ocpm1 = amendId.from(BranchMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));

			amendId.where(a1);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			query.where(n1, n4).orderBy(orderList);

			// Get Result
			TypedQuery<BranchMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBranchCode())))
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(BranchMaster::getBranchName));

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public SuccessRes updateUwReferralDetails(List<RevertGridReq> reqList) {
		SuccessRes res = new SuccessRes();
		try {
			List<String> loginIds = reqList.stream().map(RevertGridReq::getLoginId).collect(Collectors.toList());

			List<UWReferralDetails> nonselected = uwReferalDetailsRepo
					.findByRequestReferenceNoAndProductIdAndUwLoginIdNotIn(reqList.get(0).getRequestReferenceNo(),
							Integer.valueOf(reqList.get(0).getProductId()), loginIds);

			nonselected.forEach(o -> {
				o.setUwStatus("N");
			});

			uwReferalDetailsRepo.saveAll(nonselected);
			for (RevertGridReq req : reqList) {
				// Update Referral Details
				if (StringUtils.isNotBlank(req.getLoginId())) {
					List<UWReferralDetails> uwList = uwReferalDetailsRepo
							.findByRequestReferenceNo(req.getRequestReferenceNo());

					uwList.forEach(o -> {
						if (o.getUwLoginId().equals(req.getLoginId()))
							o.setUwStatus("Y");
					});
					uwReferalDetailsRepo.saveAll(uwList);
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(reqList.get(0).getRequestReferenceNo());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public PortFolioSearchGridRes searchBrokerPolicies(SearchBrokerPolicyReq req) {
		PortFolioSearchGridRes res = new PortFolioSearchGridRes();
		try {

			// Thread Call Setup To Fetch List From 4 tables
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			MyTaskList taskList = new MyTaskList(queue);
			PortFolioSearchThreadCall count = new PortFolioSearchThreadCall("getProtfolioSearchDataCount", req, em);
			PortFolioSearchThreadCall list = new PortFolioSearchThreadCall("getProtfolioSearchData", req, em);

			queue.add(count);
			queue.add(list);
			int threadCount = 2;
			int success = 0;
			ForkJoinPool forkjoin = new ForkJoinPool(threadCount);
			ConcurrentLinkedQueue<Future<Object>> invoke = (ConcurrentLinkedQueue<Future<Object>>) forkjoin
					.invoke(taskList);

			Long totalCount = 0L;
			List<PortfolioSearchDataRes> resList = new ArrayList<PortfolioSearchDataRes>();

			for (Future<Object> callable : invoke) {

				log.info(callable.getClass() + "," + callable.isDone());

				if (callable.isDone()) {
					Map<String, Object> map = (Map<String, Object>) callable.get();

					for (Entry<String, Object> future : map.entrySet()) {

						if ("getProtfolioSearchDataCount".equalsIgnoreCase(future.getKey())) {
							totalCount = (Long) future.getValue();

						} else if ("getProtfolioSearchData".equalsIgnoreCase(future.getKey())) {
							resList = (List<PortfolioSearchDataRes>) future.getValue();

						}
					}

					success++;
				}
			}
			// List<PortfolioSearchDataRes> resList = new
			// ArrayList<PortfolioSearchDataRes>();
			// CompanyProductMaster product =
			// getCompanyProductMasterDropdown(req.getInsuranceId(), req.getProductId());

//				Long totalCount = motService.getProtfolioSearchDataCount(req);
//				resList = motService.getProtfolioSearchData(req);

			res.setPortFolioList(resList);
			res.setTotalCount(totalCount);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<GetExistingBrokerListRes> getExistingBrokerList(GetExistingBrokerListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<GetExistingBrokerRes> list = new ArrayList<GetExistingBrokerRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) { // Motor

				list = getExistingBrokerListMotor(req);

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {// Travel

				list = getExistingBrokerListTravel(req);

			} else if (product.getMotorYn().equalsIgnoreCase("A")) { // Asset

				list = getExistingBrokerListAsset(req);
			} else { // Common

				list = getExistingBrokerListCommon(req);
			}

			if (list != null && list.size() > 0) {
				list = list.stream().sorted(Comparator.comparing(GetExistingBrokerRes::getCodeDesc))
						.collect(Collectors.toList());

				for (GetExistingBrokerRes data : list) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					res = dozerMapper.map(data, GetExistingBrokerListRes.class);
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

	private List<GetExistingBrokerRes> getExistingBrokerListMotor(GetExistingBrokerListReq req) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerRes> resList = new ArrayList<GetExistingBrokerRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

			query.multiselect(m.get("customerCode").alias("code"), m.get("customerName").alias("codeDesc"),
					m.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(m.get("applicationId"), req.getLoginId()));
			predics.add(cb.equal(m.get("status"), req.getStatus()));
			predics.add(cb.equal(m.get("productId"), req.getProductId()));
			predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			predics.add(cb.isNotNull(m.get("bdmCode")));

			query.where(predics.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery = em.createQuery(query);
			list = typedQuery.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}

			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

			Root<EserviceMotorDetails> m1 = query1.from(EserviceMotorDetails.class);

			query1.multiselect(m1.get("agencyCode").alias("code"), m1.get("loginId").alias("codeDesc"),
					m1.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics1 = new ArrayList<Predicate>();
			predics1.add(cb1.equal(m1.get("applicationId"), req.getLoginId()));
			predics1.add(cb1.equal(m1.get("status"), req.getStatus()));
			predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			predics1.add(cb1.isNull(m1.get("bdmCode")));

			query1.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			list1 = typedQuery1.getResultList();
			list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
					.collect(Collectors.toList());
			if (list1 != null && list1.size() > 0) {

				for (Tuple data : list1) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
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

	private List<GetExistingBrokerRes> getExistingBrokerListCommon(GetExistingBrokerListReq req) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerRes> resList = new ArrayList<GetExistingBrokerRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

			query.multiselect(m.get("customerCode").alias("code"), m.get("customerName").alias("codeDesc"),
					m.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(m.get("applicationId"), req.getLoginId()));
			predics.add(cb.equal(m.get("status"), req.getStatus()));
			predics.add(cb.equal(m.get("productId"), req.getProductId()));
			predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			predics.add(cb.isNotNull(m.get("bdmCode")));

			query.where(predics.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery = em.createQuery(query);
			list = typedQuery.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}

			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

			Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class);

			query1.multiselect(m1.get("agencyCode").alias("code"), m1.get("loginId").alias("codeDesc"),
					m1.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics1 = new ArrayList<Predicate>();
			predics1.add(cb1.equal(m1.get("applicationId"), req.getLoginId()));
			predics1.add(cb1.equal(m1.get("status"), req.getStatus()));
			predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			predics1.add(cb1.isNull(m1.get("bdmCode")));

			query1.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			list1 = typedQuery1.getResultList();
			list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
					.collect(Collectors.toList());
			if (list1 != null && list1.size() > 0) {

				for (Tuple data : list1) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
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

	private List<GetExistingBrokerRes> getExistingBrokerListAsset(GetExistingBrokerListReq req) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerRes> resList = new ArrayList<GetExistingBrokerRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

			query.multiselect(m.get("customerCode").alias("code"), m.get("customerName").alias("codeDesc"),
					m.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(m.get("applicationId"), req.getLoginId()));
			predics.add(cb.equal(m.get("status"), req.getStatus()));
			predics.add(cb.equal(m.get("productId"), req.getProductId()));
			predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			predics.add(cb.isNotNull(m.get("bdmCode")));

			query.where(predics.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery = em.createQuery(query);
			list = typedQuery.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}

			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

			Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class);

			query1.multiselect(m1.get("agencyCode").alias("code"), m1.get("loginId").alias("codeDesc"),
					m1.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics1 = new ArrayList<Predicate>();
			predics1.add(cb1.equal(m1.get("applicationId"), req.getLoginId()));
			predics1.add(cb1.equal(m1.get("status"), req.getStatus()));
			predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			predics1.add(cb1.isNull(m1.get("bdmCode")));

			query1.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			list1 = typedQuery1.getResultList();
			list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
					.collect(Collectors.toList());
			if (list1 != null && list1.size() > 0) {

				for (Tuple data : list1) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
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

	private List<GetExistingBrokerRes> getExistingBrokerListTravel(GetExistingBrokerListReq req) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerRes> resList = new ArrayList<GetExistingBrokerRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);

			query.multiselect(m.get("customerCode").alias("code"), m.get("customerName").alias("codeDesc"),
					m.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(m.get("applicationId"), req.getLoginId()));
			predics.add(cb.equal(m.get("status"), req.getStatus()));
			predics.add(cb.equal(m.get("productId"), req.getProductId()));
			predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			predics.add(cb.isNotNull(m.get("bdmCode")));

			query.where(predics.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery = em.createQuery(query);
			list = typedQuery.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}

			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

			Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class);

			query1.multiselect(m1.get("agencyCode").alias("code"), m1.get("loginId").alias("codeDesc"),
					m1.get("sourceType").alias("type")).distinct(true);

			List<Predicate> predics1 = new ArrayList<Predicate>();
			predics1.add(cb1.equal(m1.get("applicationId"), req.getLoginId()));
			predics1.add(cb1.equal(m1.get("status"), req.getStatus()));
			predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			predics1.add(cb1.isNull(m1.get("bdmCode")));

			query1.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			list1 = typedQuery1.getResultList();
			list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
					.collect(Collectors.toList());
			if (list1 != null && list1.size() > 0) {

				for (Tuple data : list1) {
					GetExistingBrokerRes res = new GetExistingBrokerRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
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

//__________________________________________________________________________________________________
	@Override
	public List<GetExistingBrokerListRes> getBrokerUserList(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			// Finding Product

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorExistingDropdown(req, today, before30);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelExistingDropdown(req, today, before30);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingExistingDropdown(req, today, before30);
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeExistingDropdown(req, today, before30);
			} else {
				resList = commonService.getCommonExistingDropdown(req, today, before30);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getReportBrokerUserList(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			String subUserType = "";
			LoginMaster logindata = new LoginMaster();
			if ("1".equalsIgnoreCase(req.getApplicationId())) {
				logindata = loginRepo.findByLoginId(req.getLoginId());
				if (logindata != null) {
					subUserType = logindata.getSubUserType();
				}
			} else {
				logindata = loginRepo.findByLoginId(req.getApplicationId());
				if (logindata != null) {
					subUserType = logindata.getSubUserType();
				}
			}
			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
				query.multiselect(/* m.get("agencyCode").alias("code"), */ m.get("loginId").alias("code"),
						us.get("userName").alias("codeDesc"), m.get("sourceType").alias("type"));
				// Find All
				Subquery<String> agencyCode = query.subquery(String.class);
				Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
				agencyCode.select(ocpm1.get("agencyCode"));
				Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				agencyCode.where(a1);

//				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
//				Predicate n1 = cb.equal(m.get("loginId"), req.getLoginId());
				// Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("status"), "P");
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), today);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), today);
				Predicate n9 = cb.notEqual(m.get("endtTypeId"), "842");
				Predicate n10 = cb.isNull(m.get("endtTypeId"));
				Predicate n11 = cb.or(n9, n10);
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode);
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode").as(String.class), agencyCode);
				}
				Predicate n13 = cb.isNotNull(m.get("sourceType"));
				Predicate n14 = cb.isNotNull(m.get("loginId"));
				Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));

				query.where(n3, n4, n5, n6, n7, n8, n11, n12, n13, n14, us1);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
						.collect(Collectors.toList());
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();

						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? ""
								: data.get("type").toString().toLowerCase().replaceAll("premia ", ""));
						resList.add(res);

					}
				}
			} else {
				resList = getReportIssuer(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	private List<GetExistingBrokerListRes> getReportIssuer(ExistingBrokerUserListReq req, Date today) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			LoginMaster logindata = loginRepo.findByLoginId(req.getApplicationId());
			String subUserType = "high";
			if (logindata != null) {
				subUserType = logindata.getSubUserType();
			}
			if ("low".equalsIgnoreCase(subUserType)) {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<HomePositionMaster> m = query.from(HomePositionMaster.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
							m.get("sourceType").alias("type"));
					Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb.isNotNull(m.get("applicationId"));
					Predicate n3 = cb.equal(m.get("status"), "P");
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
					Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), today);
					Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), today);
					Predicate n9 = cb.notEqual(m.get("endtTypeId"), "842");
					Predicate n10 = cb.isNull(m.get("endtTypeId"));
					Predicate n11 = cb.or(n9, n10);
					Predicate n12 = cb.isNotNull(m.get("bdmCode"));
					query.where(n1, n2, n3, n4, n5, n6, n7, n8, n11, n12);

					TypedQuery<Tuple> typedQuery = em.createQuery(query);
					list = typedQuery.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<HomePositionMaster> m1 = query1.from(HomePositionMaster.class);
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);

					query1.multiselect(/* m1.get("brokerCode").alias("code"), */ m1.get("loginId").alias("code"),
							us.get("userName").alias("codeDesc"), m1.get("sourceType").alias("type"));

					Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
					Predicate n3 = cb1.equal(m1.get("status"), "P");
					Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
					Predicate n5 = cb1.equal(m1.get("companyId"), req.getCompanyId());
					Predicate n6 = cb1.equal(m1.get("branchCode"), req.getBranchCode());
					Predicate n7 = cb1.greaterThanOrEqualTo(m1.get("expiryDate"), today);
					Predicate n8 = cb1.lessThanOrEqualTo(m1.get("entryDate"), today);
					Predicate n9 = cb1.notEqual(m1.get("endtTypeId"), "842");
					Predicate n10 = cb1.isNull(m1.get("endtTypeId"));
					Predicate n11 = cb1.or(n9, n10);
					Predicate n12 = cb1.isNull(m1.get("bdmCode"));
					Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
					query1.where(n1, n2, n3, n4, n5, n6, n7, n8, n11, n12, us1);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
					list1 = typedQuery1.getResultList();
					list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list1 != null && list1.size() > 0) {

						for (Tuple data : list1) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
			} else if ("high".equalsIgnoreCase(subUserType) || "both".equalsIgnoreCase(subUserType)) {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<HomePositionMaster> m = query.from(HomePositionMaster.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
							m.get("sourceType").alias("type"));
//					Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb.isNotNull(m.get("applicationId"));
					Predicate n3 = cb.equal(m.get("status"), "P");
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
					Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), today);
					Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), today);
					Predicate n9 = cb.notEqual(m.get("endtTypeId"), "842");
					Predicate n10 = cb.isNull(m.get("endtTypeId"));
					Predicate n11 = cb.or(n9, n10);
					Predicate n12 = cb.isNotNull(m.get("bdmCode"));
					query.where(n2, n3, n4, n5, n6, n7, n8, n11, n12);

					TypedQuery<Tuple> typedQuery = em.createQuery(query);
					list = typedQuery.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<HomePositionMaster> m1 = query1.from(HomePositionMaster.class);
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);

					query1.multiselect(/* m1.get("brokerCode").alias("code"), */ m1.get("loginId").alias("code"),
							us.get("userName").alias("codeDesc"), m1.get("sourceType").alias("type"));

//					Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
					Predicate n3 = cb1.equal(m1.get("status"), "P");
					Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
					Predicate n5 = cb1.equal(m1.get("companyId"), req.getCompanyId());
					Predicate n6 = cb1.equal(m1.get("branchCode"), req.getBranchCode());
					Predicate n7 = cb1.greaterThanOrEqualTo(m1.get("expiryDate"), today);
					Predicate n8 = cb1.lessThanOrEqualTo(m1.get("entryDate"), today);
					Predicate n9 = cb1.notEqual(m1.get("endtTypeId"), "842");
					Predicate n10 = cb1.isNull(m1.get("endtTypeId"));
					Predicate n11 = cb1.or(n9, n10);
					Predicate n12 = cb1.isNull(m1.get("bdmCode"));
					Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
					query1.where(n2, n3, n4, n5, n6, n7, n8, n11, n12, us1);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
					list1 = typedQuery1.getResultList();
					list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list1 != null && list1.size() > 0) {

						for (Tuple data : list1) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getPortfolioBrokerUserList(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
				query.multiselect(/* m.get("agencyCode").alias("code"), */ m.get("loginId").alias("code"),
						us.get("userName").alias("codeDesc"), m.get("sourceType").alias("type"));
				// Find All
				Subquery<String> agencyCode = query.subquery(String.class);
				Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
				agencyCode.select(ocpm1.get("agencyCode"));
				Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				agencyCode.where(a1);

//				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
//				Predicate n1 = cb.equal(m.get("loginId"), req.getLoginId());
				// Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("status"), "P");
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), today);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), today);
				Predicate n9 = cb.notEqual(m.get("endtTypeId"), "842");
				Predicate n10 = cb.isNull(m.get("endtTypeId"));
				Predicate n11 = cb.or(n9, n10);
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode);
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode").as(String.class), agencyCode);
				}
				Predicate n15 =cb.isNotNull(m.get("policyNo"));
				Predicate n13 = cb.isNotNull(m.get("sourceType"));
				Predicate n14 = cb.isNotNull(m.get("loginId"));
				Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));

				query.where(n3, n4, n5, n6, n7, n8, n11, n12, n13, n14,n15, us1);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
						.collect(Collectors.toList());
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();

						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? ""
								: data.get("type").toString().toLowerCase().replaceAll("premia ", ""));
						resList.add(res);

					}
				}
			} else {
				resList = getPortfolioActiveIssuerMotor(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	private List<GetExistingBrokerListRes> getPortfolioActiveIssuerMotor(ExistingBrokerUserListReq req, Date today) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			/*
			 * { CriteriaBuilder cb = em.getCriteriaBuilder(); CriteriaQuery<Tuple> query =
			 * cb.createQuery(Tuple.class);
			 * 
			 * Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			 * 
			 * query.multiselect(m.get("bdmCode").alias("code"),
			 * m.get("customerName").alias("codeDesc"), m.get("sourceType").alias("type"));
			 * Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
			 * Predicate n2 = cb.isNotNull(m.get("applicationId")); Predicate n3 =
			 * cb.equal(m.get("status"), "P"); Predicate n4 = cb.equal(m.get("productId"),
			 * req.getProductId()); Predicate n5 = cb.equal(m.get("companyId"),
			 * req.getCompanyId()); Predicate n6 = cb.equal(m.get("branchCode"),
			 * req.getBranchCode()); Predicate n7 =
			 * cb.greaterThanOrEqualTo(m.get("expiryDate"), today); Predicate n8 =
			 * cb.lessThanOrEqualTo(m.get("entryDate"), today); Predicate n9 =
			 * cb.notEqual(m.get("endtTypeId"), "842"); Predicate n10 =
			 * cb.isNull(m.get("endtTypeId")); Predicate n11 = cb.or(n9, n10); Predicate n12
			 * = cb.isNotNull(m.get("bdmCode")); query.where(n1, n2, n3, n4, n5, n6, n7, n8,
			 * n11, n12);
			 * 
			 * TypedQuery<Tuple> typedQuery = em.createQuery(query); list =
			 * typedQuery.getResultList(); list = list.stream().filter(distinctByKey(o ->
			 * Arrays.asList(o.get("code")))) .collect(Collectors.toList()); if (list !=
			 * null && list.size() > 0) {
			 * 
			 * for (Tuple data : list) { GetExistingBrokerListRes res = new
			 * GetExistingBrokerListRes(); res.setCode(data.get("code") == null ? "" :
			 * data.get("code").toString()); res.setCodeDesc(data.get("codeDesc") == null ?
			 * "" : data.get("codeDesc").toString()); res.setType(data.get("type") == null ?
			 * "" : data.get("type").toString()); String type=data.get("type") == null ? ""
			 * : data.get("type").toString(); type="Premia "+type; res.setType(type);
			 * resList.add(res);
			 * 
			 * } } }
			 */
			{
				CriteriaBuilder cb1 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

				Root<HomePositionMaster> m1 = query1.from(HomePositionMaster.class);
				Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);

				query1.multiselect(/* m1.get("brokerCode").alias("code"), */ m1.get("bdmCode").alias("code"),m1.get("bdmName").alias("codeDesc"), m1.get("sourceType").alias("type"));

				Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
				Predicate n3 = cb1.equal(m1.get("status"), "P");
				Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
				Predicate n5 = cb1.equal(m1.get("companyId"), req.getCompanyId());
				Predicate n6 = cb1.equal(m1.get("branchCode"), req.getBranchCode());
				Predicate n12 = cb1.isNotNull(m1.get("policyNo"));
				Predicate n7 = cb1.greaterThanOrEqualTo(m1.get("expiryDate"), today);
				Predicate n8 = cb1.lessThanOrEqualTo(m1.get("entryDate"), today);
				Predicate n9 = cb1.notEqual(m1.get("endtTypeId"), "842");
				Predicate n10 = cb1.isNull(m1.get("endtTypeId"));
				Predicate n11 = cb1.or(n9, n10);
				// Predicate n12 = cb1.isNull(m1.get("bdmCode"));
				Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
				query1.where(n1, n2, n3, n4, n5, n6, n7, n8, n11, us1,n12);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
				list1 = typedQuery1.getResultList();
				list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
						.collect(Collectors.toList());
				if (list1 != null && list1.size() > 0) {

					for (Tuple data : list1) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? "" : data.get("type").toString());
						resList.add(res);

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	// Portfolio Pending Dropdown
	@Override
	public List<GetExistingBrokerListRes> getPortfolioPendingDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {

			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorProtfolioDropdownPending(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeProtfolioDropdownPending(req, today);
			}

			else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelProtfolioDropdownPending(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingProtfolioDropdownPending(req, today);
			} else {
				resList = commonService.getCommonProtfolioDropdownPending(req, today);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;

	}

	@Override
	public List<GetExistingBrokerListRes> getCancelPolicyIssuerDropdownList(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
				query.multiselect(m.get("loginId").alias("code"), us.get("userName").alias("codeDesc"),
						m.get("sourceType").alias("type"));

				// Find All
				Subquery<String> agencyCode = query.subquery(String.class);
				Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
				agencyCode.select(ocpm1.get("agencyCode"));
				Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				agencyCode.where(a1);

//				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
//				Predicate n1 = cb.equal(m.get("loginId"), req.getLoginId());
				Predicate n3 = cb.equal(m.get("status"), "D");
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
//				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), today);
//				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), today);
				Predicate n9 = cb.equal(m.get("endtTypeId"), "842");
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode);
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode").as(String.class), agencyCode);
				}
				Predicate n13 = cb.isNotNull(m.get("sourceType"));
				Predicate n14 = cb.isNotNull(m.get("loginId"));
				Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
				query.where(n3, n4, n5, n6, n9, n12, n13, n14, us1);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
						.collect(Collectors.toList());
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();

						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? ""
								: data.get("type").toString().toLowerCase().replaceAll("premia ", ""));
						resList.add(res);

					}
				}
			} else {
				resList = getPortfolioCancelIssuerMotor(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	private List<GetExistingBrokerListRes> getPortfolioCancelIssuerMotor(ExistingBrokerUserListReq req, Date today) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);

				query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
						m.get("sourceType").alias("type"));
				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("status"), "D");
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
//				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), today);
//				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), today);
				Predicate n9 = cb.equal(m.get("endtTypeId"), "842");
				Predicate n10 = cb.isNotNull(m.get("bdmCode"));
				query.where(n1, n2, n3, n4, n5, n6, n9, n10);

				TypedQuery<Tuple> typedQuery = em.createQuery(query);
				list = typedQuery.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
						.collect(Collectors.toList());
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? "" : data.get("type").toString());
						String type = data.get("type") == null ? "" : data.get("type").toString();
						type = "Premia " + type;
						res.setType(type);
						resList.add(res);

					}
				}
			}
			{
				CriteriaBuilder cb1 = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

				Root<HomePositionMaster> m1 = query1.from(HomePositionMaster.class);
				Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);

				query1.multiselect(m1.get("loginId").alias("code"), us.get("userName").alias("codeDesc"),
						m1.get("sourceType").alias("type"));

				Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
				Predicate n3 = cb1.equal(m1.get("status"), "D");
				Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
				Predicate n5 = cb1.equal(m1.get("companyId"), req.getCompanyId());
				Predicate n6 = cb1.equal(m1.get("branchCode"), req.getBranchCode());
//				Predicate n7 = cb1.greaterThanOrEqualTo(m1.get("expiryDate"), today);
//				Predicate n8 = cb1.lessThanOrEqualTo(m1.get("entryDate"), today);
				Predicate n9 = cb1.equal(m1.get("endtTypeId"), "842");
				Predicate n10 = cb1.isNull(m1.get("bdmCode"));
				Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
				query1.where(n1, n2, n3, n4, n5, n6, n9, n10, us1);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
				list1 = typedQuery1.getResultList();
				list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
						.collect(Collectors.toList());
				if (list1 != null && list1.size() > 0) {

					for (Tuple data : list1) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? "" : data.get("type").toString());
						resList.add(res);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override // dropdown
	public List<GetExistingBrokerListRes> getBrokerUserListLapsed(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) { // Motor

				resList = motService.getBrokerUserListLapsedMotor(req, today, before30);

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {// Travel

				resList = traService.getBrokerUserListLapsedTravel(req, today, before30);

			} else if (product.getMotorYn().equalsIgnoreCase("A")) { // Asset

				resList = buiService.getBrokerUserListLapsedAsset(req, today, before30);

			} else if (product.getMotorYn().equalsIgnoreCase("L")) { // Anti

				resList = lifeService.getBrokerUserListLapsedLife(req, today, before30);
			}

			else { // Common

				resList = commonService.getBrokerUserListLapsedCommon(req, today, before30);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override // dropdown
	public List<GetExistingBrokerListRes> getBrokerUserListRejected(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) { // Motor

				resList = motService.getBrokerUserListMotorRejected(req, today, before30);

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {// Travel

				resList = traService.getBrokerUserListTravelRejected(req, today, before30);

			} else if (product.getMotorYn().equalsIgnoreCase("A")) { // Asset

				resList = buiService.getBrokerUserListBuildingRejected(req, today, before30);

			} else if (product.getMotorYn().equalsIgnoreCase("L")) { // Life

				resList = lifeService.getBrokerUserListLifeRejected(req, today, before30);

			} else { // Common

				resList = commonService.getBrokerUserListCommonRejected(req, today, before30);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getReferralPendingDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorRPDropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeRPDropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelReferalDropdown(req, today, "RP");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingReferalDropdown(req, today, "RP");
				
			} else {
				resList = commonService.getCommonReferalDropdown(req, today, "RP");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getReferralApprovedDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorRADropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeRADropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelReferalDropdown(req, today, "RA");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingReferalDropdown(req, today, "RA");
			} else {
				resList = commonService.getCommonReferalDropdown(req, today, "RA");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getReferralRejectDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorRRDropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeRRDropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelReferalDropdown(req, today, "RR");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingReferalDropdown(req, today, "RR");
			} else {
				resList = commonService.getCommonReferalDropdown(req, today, "RR");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getReferralRequoteDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorREDropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeREDropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelReferalDropdown(req, today, "RE");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingReferalDropdown(req, today, "RE");
			} else {
				resList = commonService.getCommonReferalDropdown(req, today, "RE");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getAdminReferralPendingDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			// cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getAdminMotorRPropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getAdminLifeRPropdown(req, today);
			}

			else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getAdminTravelRPDropdown(req, today);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getAdminBuildingRPDropdown(req, today);
				
				if(resList== null || resList.isEmpty())
				{
					resList = commonService.getAdminCommonRPDropdown(req, today);
				}
				
			} else {
				resList = commonService.getAdminCommonRPDropdown(req, today);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getAdminReferralApproveDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			// cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorAdminReferalDropdown(req, today, "RA");
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeAdminReferalDropdown(req, today, "RA");
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelAdminReferalDropdown(req, today, "RA");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingAdminReferalDropdown(req, today, "RA");
				if(resList== null || resList.isEmpty())
				{
					resList = commonService.getCommonAdminReferalDropdown(req, today, "RA");
				}
			} else {
				resList = commonService.getCommonAdminReferalDropdown(req, today, "RA");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getAdminReferralRejectDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			// cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorAdminReferalDropdown(req, today, "RR");
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeAdminReferalDropdown(req, today, "RR");
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelAdminReferalDropdown(req, today, "RR");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingAdminReferalDropdown(req, today, "RR");
				if(resList== null || resList.isEmpty())
				{
					resList = commonService.getCommonAdminReferalDropdown(req, today, "RR");
				}
				
			} else {
				resList = commonService.getCommonAdminReferalDropdown(req, today, "RR");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetExistingBrokerListRes> getAdminReferralReQuoteDropdown(ExistingBrokerUserListReq req) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			// cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				resList = motService.getMotorAdminReferalDropdown(req, today, "RE");
			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
				resList = lifeService.getLifeAdminReferalDropdown(req, today, "RE");
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = traService.getTravelAdminReferalDropdown(req, today, "RE");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				resList = buiService.getBuildingAdminReferalDropdown(req, today, "RE");
			} else {
				resList = commonService.getCommonAdminReferalDropdown(req, today, "RE");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public RegNumberRes getRegNumberQuotes(RegSearchReq req) {
		RegNumberRes res2 = new RegNumberRes();
		List<GetRegNumberQuoteRes> reslist = new ArrayList<GetRegNumberQuoteRes>();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RegirsterSearchCriteeriaRes> query = cb.createQuery(RegirsterSearchCriteeriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

			// overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceMotorDetails> ocpm1 = overallPremiumLc.from(EserviceMotorDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);

			// overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceMotorDetails> oc = overallPremiumFc.from(EserviceMotorDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);

			// Select
			query.multiselect(

					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("productName").alias("productName"), m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo"), m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"), m.get("policyStartDate").alias("policyStartDate"),
					m.get("policyEndDate").alias("policyEndDate"),

					overallPremiumLc.alias("overallPremiumLc"), overallPremiumFc.alias("overallPremiumFc"),
					m.get("currency").alias("currency"), m.get("chassisNumber").alias("chassisNumber"),
					m.get("registrationNumber").alias("registerNumber")

			);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), today);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n12 = cb.equal(m.get("registrationNumber"), req.getRegisterNumber());
			Predicate n7 = null;
			Predicate n8 = null;

			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());

			if (loginData.getUserType().equalsIgnoreCase("Broker")
					|| loginData.getUserType().equalsIgnoreCase("User")) {

				n7 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
				n8 = cb.equal(m.get("loginId"), req.getCreatedBy());

			} else {

				n7 = cb.equal(m.get("branchCode"), req.getBranchCode());
				n8 = cb.equal(m.get("applicationId"), req.getCreatedBy());

			}
			// Risk Max Filter
//			Subquery<Long> riskId = query.subquery(Long.class);
//			Root<EserviceMotorDetails> ocp = riskId.from(EserviceMotorDetails.class);
//			riskId.select(cb.max(ocp.get("riskId")));
//			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
//			riskId.where(a3);
//			
//			Predicate n10 = cb.equal(m.get("riskId"),  riskId );

			query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, n12).orderBy(orderList);

			// Get Result
			TypedQuery<RegirsterSearchCriteeriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			List<RegirsterSearchCriteeriaRes> existingQuotes = new ArrayList<RegirsterSearchCriteeriaRes>();

			existingQuotes = result.getResultList();

			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			for (RegirsterSearchCriteeriaRes data : existingQuotes) {
				GetRegNumberQuoteRes res = new GetRegNumberQuoteRes();
				dozerMapper.map(data, res);
				reslist.add(res);
			}

			res2.setRegisterNumberQuotes(reslist);
			res2.setTotalCount(reslist == null ? "0" : String.valueOf(reslist.size()));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res2;
	}

	@Override
	public GetPaymentStatusRes getPaymentStatus(GetPaymentStatusReq req) {
		// List<GetPaymentStatusRes> reslist = new ArrayList<GetPaymentStatusRes>();
		GetPaymentStatusRes res = new GetPaymentStatusRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			if (StringUtils.isNotBlank(req.getLoginId())) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Tuple> list = new ArrayList<Tuple>();

				query.multiselect(p.get("quoteNo").alias("quoteNo"), h.get("loginId").alias("loginId"),
						p.get("paymentId").alias("paymentId"), p.get("branchCode").alias("branchCode"),
						p.get("paymentStatus").alias("paymentStatus"), p.get("branchName").alias("branchName"),
						p.get("paymentTypedesc").alias("paymentTypedesc"), h.get("companyId").alias("companyId"),
						h.get("productId").alias("productId"), p.get("customerName").alias("customerName"),
						h.get("userType").alias("userType"), p.get("entryDate").alias("entryDate"),
						h.get("inceptionDate").alias("inceptionDate"), h.get("expiryDate").alias("expiryDate"),
						h.get("applicationId").alias("applicationId")).distinct(true);
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "PENDING"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				predics.add(cb.equal(h.get("loginId"), req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Tuple> typedQuery = em.createQuery(query);
				typedQuery.setFirstResult(limit * offset);
				typedQuery.setMaxResults(offset);
				list = typedQuery.getResultList();

				// list
				// =list.stream().filter(o->o.get("paymentStatus").equals("PENDING")).collect(Collectors.toList());
				List<PaymentStausRes> statusResList = new ArrayList<PaymentStausRes>();
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						PaymentStausRes res1 = new PaymentStausRes();
						res1.setBranchCode(data.get("branchCode") == null ? "" : data.get("branchCode").toString());
						res1.setBranchCode(data.get("branchName") == null ? "" : data.get("branchName").toString());
						res1.setCompanyId(data.get("companyId") == null ? "" : data.get("companyId").toString());
						res1.setClientName(data.get("customerName") == null ? "" : data.get("customerName").toString());
						res1.setLoginId(data.get("loginId") == null ? "" : data.get("loginId").toString());
						res1.setUserType(data.get("userType") == null ? "" : data.get("userType").toString());
						res1.setQuoteNo(data.get("quoteNo") == null ? "" : data.get("quoteNo").toString());
						res1.setPaymentId(data.get("paymentId") == null ? "" : data.get("paymentId").toString());
						res1.setPaymentStatus(
								data.get("paymentStatus") == null ? "" : data.get("paymentStatus").toString());
						res1.setPaymentTypedesc(
								data.get("paymentTypedesc") == null ? "" : data.get("paymentTypedesc").toString());
						res1.setInceptionDate(data.get("inceptionDate") == null ? null
								: sdf.format(data.get("inceptionDate")).toString());
						res1.setExpiryDate(
								data.get("expiryDate") == null ? null : sdf.format(data.get("expiryDate")).toString());
						res1.setEntryDate(
								data.get("entryDate") == null ? null : sdf.format(data.get("entryDate")).toString());
						res1.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						statusResList.add(res1);

					}

				}
				res.setPaymentStausRes(statusResList);
				res.setTotalCount(pendingCount(req));
				// reslist.add(statusResList);
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Tuple> list = new ArrayList<Tuple>();

				query.multiselect(p.get("quoteNo").alias("quoteNo"), h.get("loginId").alias("loginId"),
						p.get("paymentId").alias("paymentId"), p.get("branchCode").alias("branchCode"),
						p.get("paymentStatus").alias("paymentStatus"), p.get("branchName").alias("branchName"),
						p.get("paymentTypedesc").alias("paymentTypedesc"), h.get("companyId").alias("companyId"),
						h.get("productId").alias("productId"), h.get("userType").alias("userType"),
						p.get("customerName").alias("customerName"), p.get("entryDate").alias("entryDate"),
						h.get("inceptionDate").alias("inceptionDate"), h.get("expiryDate").alias("expiryDate"),
						h.get("applicationId").alias("applicationId")).distinct(true);
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "PENDING"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				// predics.add(cb.equal(h.get("loginId"),req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Tuple> typedQuery = em.createQuery(query);
				typedQuery.setFirstResult(limit * offset);
				typedQuery.setMaxResults(offset);

				list = typedQuery.getResultList();
				// list
				// =list.stream().filter(o->o.get("paymentStatus").equals("PENDING")).collect(Collectors.toList());
				List<PaymentStausRes> statusResList = new ArrayList<PaymentStausRes>();
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						PaymentStausRes res1 = new PaymentStausRes();
						res1.setBranchCode(data.get("branchCode") == null ? "" : data.get("branchCode").toString());
						res1.setBranchCode(data.get("branchName") == null ? "" : data.get("branchName").toString());
						res1.setCompanyId(data.get("companyId") == null ? "" : data.get("companyId").toString());
						res1.setClientName(data.get("customerName") == null ? "" : data.get("customerName").toString());
						res1.setLoginId(data.get("loginId") == null ? "" : data.get("loginId").toString());
						res1.setUserType(data.get("userType") == null ? "" : data.get("userType").toString());
						res1.setQuoteNo(data.get("quoteNo") == null ? "" : data.get("quoteNo").toString());
						res1.setPaymentId(data.get("paymentId") == null ? "" : data.get("paymentId").toString());
						res1.setPaymentStatus(
								data.get("paymentStatus") == null ? "" : data.get("paymentStatus").toString());
						res1.setPaymentTypedesc(
								data.get("paymentTypedesc") == null ? "" : data.get("paymentTypedesc").toString());
						res1.setInceptionDate(data.get("inceptionDate") == null ? null
								: sdf.format(data.get("inceptionDate")).toString());
						res1.setExpiryDate(
								data.get("expiryDate") == null ? null : sdf.format(data.get("expiryDate")).toString());
						res1.setEntryDate(
								data.get("entryDate") == null ? null : sdf.format(data.get("entryDate")).toString());
						res1.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						statusResList.add(res1);

					}

				}
				res.setPaymentStausRes(statusResList);
				res.setTotalCount(pendingCount(req));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public GetPaymentStatusRes getPaymentFailedStatus(GetPaymentStatusReq req) {
		GetPaymentStatusRes res = new GetPaymentStatusRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			if (StringUtils.isNotBlank(req.getLoginId())) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Tuple> list = new ArrayList<Tuple>();

				query.multiselect(p.get("quoteNo").alias("quoteNo"), h.get("loginId").alias("loginId"),
						p.get("paymentId").alias("paymentId"), p.get("branchCode").alias("branchCode"),
						p.get("paymentStatus").alias("paymentStatus"), p.get("branchName").alias("branchName"),
						p.get("paymentTypedesc").alias("paymentTypedesc"), h.get("companyId").alias("companyId"),
						h.get("productId").alias("productId"), p.get("customerName").alias("customerName"),
						h.get("userType").alias("userType"), p.get("entryDate").alias("entryDate"),
						h.get("inceptionDate").alias("inceptionDate"), h.get("expiryDate").alias("expiryDate"),
						h.get("applicationId").alias("applicationId")).distinct(true);
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "FAILED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				predics.add(cb.equal(h.get("loginId"), req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Tuple> typedQuery = em.createQuery(query);
				typedQuery.setFirstResult(limit * offset);
				typedQuery.setMaxResults(offset);
				list = typedQuery.getResultList();

//			list =list.stream().filter(o->o.get("paymentStatus").equals("FAILED")).collect(Collectors.toList());
				List<PaymentStausRes> statusResList = new ArrayList<PaymentStausRes>();
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						PaymentStausRes res1 = new PaymentStausRes();
						res1.setBranchCode(data.get("branchCode") == null ? "" : data.get("branchCode").toString());
						res1.setBranchCode(data.get("branchName") == null ? "" : data.get("branchName").toString());
						res1.setCompanyId(data.get("companyId") == null ? "" : data.get("companyId").toString());
						res1.setClientName(data.get("customerName") == null ? "" : data.get("customerName").toString());
						res1.setLoginId(data.get("loginId") == null ? "" : data.get("loginId").toString());
						res1.setUserType(data.get("userType") == null ? "" : data.get("userType").toString());
						res1.setQuoteNo(data.get("quoteNo") == null ? "" : data.get("quoteNo").toString());
						res1.setPaymentId(data.get("paymentId") == null ? "" : data.get("paymentId").toString());
						res1.setPaymentStatus(
								data.get("paymentStatus") == null ? "" : data.get("paymentStatus").toString());
						res1.setPaymentTypedesc(
								data.get("paymentTypedesc") == null ? "" : data.get("paymentTypedesc").toString());
						res1.setInceptionDate(data.get("inceptionDate") == null ? null
								: sdf.format(data.get("inceptionDate")).toString());
						res1.setExpiryDate(
								data.get("expiryDate") == null ? null : sdf.format(data.get("expiryDate")).toString());
						res1.setEntryDate(
								data.get("entryDate") == null ? null : sdf.format(data.get("entryDate")).toString());
						res1.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						statusResList.add(res1);

					}

				}
				res.setPaymentStausRes(statusResList);
				res.setTotalCount(failureCount(req));
				// reslist.add(statusResList);
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Tuple> list = new ArrayList<Tuple>();

				query.multiselect(p.get("quoteNo").alias("quoteNo"), h.get("loginId").alias("loginId"),
						p.get("paymentId").alias("paymentId"), p.get("branchCode").alias("branchCode"),
						p.get("paymentStatus").alias("paymentStatus"), p.get("branchName").alias("branchName"),
						p.get("paymentTypedesc").alias("paymentTypedesc"), h.get("companyId").alias("companyId"),
						h.get("productId").alias("productId"), p.get("customerName").alias("customerName"),
						h.get("userType").alias("userType"), p.get("entryDate").alias("entryDate"),
						h.get("inceptionDate").alias("inceptionDate"), h.get("expiryDate").alias("expiryDate"),
						h.get("applicationId").alias("applicationId")).distinct(true);
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "FAILED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				// predics.add(cb.equal(h.get("loginId"),req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Tuple> typedQuery = em.createQuery(query);
				typedQuery.setFirstResult(limit * offset);
				typedQuery.setMaxResults(offset);
				list = typedQuery.getResultList();

//				list =list.stream().filter(o->o.get("paymentStatus").equals("FAILED")).collect(Collectors.toList());
				List<PaymentStausRes> statusResList = new ArrayList<PaymentStausRes>();
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						PaymentStausRes res1 = new PaymentStausRes();
						res1.setBranchCode(data.get("branchCode") == null ? "" : data.get("branchCode").toString());
						res1.setBranchCode(data.get("branchName") == null ? "" : data.get("branchName").toString());
						res1.setCompanyId(data.get("companyId") == null ? "" : data.get("companyId").toString());
						res1.setClientName(data.get("customerName") == null ? "" : data.get("customerName").toString());
						res1.setLoginId(data.get("loginId") == null ? "" : data.get("loginId").toString());
						res1.setUserType(data.get("userType") == null ? "" : data.get("userType").toString());
						res1.setQuoteNo(data.get("quoteNo") == null ? "" : data.get("quoteNo").toString());
						res1.setPaymentId(data.get("paymentId") == null ? "" : data.get("paymentId").toString());
						res1.setPaymentStatus(
								data.get("paymentStatus") == null ? "" : data.get("paymentStatus").toString());
						res1.setPaymentTypedesc(
								data.get("paymentTypedesc") == null ? "" : data.get("paymentTypedesc").toString());
						res1.setInceptionDate(data.get("inceptionDate") == null ? null
								: sdf.format(data.get("inceptionDate")).toString());
						res1.setExpiryDate(
								data.get("expiryDate") == null ? null : sdf.format(data.get("expiryDate")).toString());
						res1.setEntryDate(
								data.get("entryDate") == null ? null : sdf.format(data.get("entryDate")).toString());
						res1.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						statusResList.add(res1);

					}

				}
				res.setPaymentStausRes(statusResList);
				res.setTotalCount(failureCount(req));

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public GetPaymentStatusRes getPaymentSucessStatus(GetPaymentStatusReq req) {
		GetPaymentStatusRes res = new GetPaymentStatusRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			if (StringUtils.isNotBlank(req.getLoginId())) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Tuple> list = new ArrayList<Tuple>();

				query.multiselect(p.get("quoteNo").alias("quoteNo"), h.get("loginId").alias("loginId"),
						p.get("paymentId").alias("paymentId"), p.get("branchCode").alias("branchCode"),
						p.get("paymentStatus").alias("paymentStatus"), p.get("branchName").alias("branchName"),
						p.get("paymentTypedesc").alias("paymentTypedesc"), h.get("companyId").alias("companyId"),
						h.get("productId").alias("productId"), p.get("customerName").alias("customerName"),
						h.get("userType").alias("userType"), p.get("entryDate").alias("entryDate"),
						h.get("inceptionDate").alias("inceptionDate"), h.get("expiryDate").alias("expiryDate"),
						h.get("applicationId").alias("applicationId")).distinct(true);
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "ACCEPTED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				predics.add(cb.equal(h.get("loginId"), req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Tuple> typedQuery = em.createQuery(query);
				typedQuery.setFirstResult(limit * offset);
				typedQuery.setMaxResults(offset);
				list = typedQuery.getResultList();

//			list =list.stream().filter(o->o.get("paymentStatus").equals("SUCCESS")).collect(Collectors.toList());
				List<PaymentStausRes> statusResList = new ArrayList<PaymentStausRes>();
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						PaymentStausRes res1 = new PaymentStausRes();
						res1.setBranchCode(data.get("branchCode") == null ? "" : data.get("branchCode").toString());
						res1.setBranchCode(data.get("branchName") == null ? "" : data.get("branchName").toString());
						res1.setCompanyId(data.get("companyId") == null ? "" : data.get("companyId").toString());
						res1.setClientName(data.get("customerName") == null ? "" : data.get("customerName").toString());
						res1.setLoginId(data.get("loginId") == null ? "" : data.get("loginId").toString());
						res1.setUserType(data.get("userType") == null ? "" : data.get("userType").toString());
						res1.setQuoteNo(data.get("quoteNo") == null ? "" : data.get("quoteNo").toString());
						res1.setPaymentId(data.get("paymentId") == null ? "" : data.get("paymentId").toString());
						res1.setPaymentStatus(
								data.get("paymentStatus") == null ? "" : data.get("paymentStatus").toString());
						res1.setPaymentTypedesc(
								data.get("paymentTypedesc") == null ? "" : data.get("paymentTypedesc").toString());
						res1.setInceptionDate(data.get("inceptionDate") == null ? null
								: sdf.format(data.get("inceptionDate")).toString());
						res1.setExpiryDate(
								data.get("expiryDate") == null ? null : sdf.format(data.get("expiryDate")).toString());
						res1.setEntryDate(
								data.get("entryDate") == null ? null : sdf.format(data.get("entryDate")).toString());
						res1.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						statusResList.add(res1);

					}

				}
				res.setPaymentStausRes(statusResList);
				res.setTotalCount(successCount(req));
				// reslist.add(statusResList);
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Tuple> list = new ArrayList<Tuple>();

				query.multiselect(p.get("quoteNo").alias("quoteNo"), h.get("loginId").alias("loginId"),
						p.get("paymentId").alias("paymentId"), p.get("branchCode").alias("branchCode"),
						p.get("paymentStatus").alias("paymentStatus"), p.get("branchName").alias("branchName"),
						p.get("paymentTypedesc").alias("paymentTypedesc"), h.get("companyId").alias("companyId"),
						h.get("productId").alias("productId"), p.get("customerName").alias("customerName"),
						h.get("userType").alias("userType"), p.get("entryDate").alias("entryDate"),
						h.get("inceptionDate").alias("inceptionDate"), h.get("expiryDate").alias("expiryDate"),
						h.get("applicationId").alias("applicationId")).distinct(true);
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "ACCEPTED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				// predics.add(cb.equal(h.get("loginId"),req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Tuple> typedQuery = em.createQuery(query);
				typedQuery.setFirstResult(limit * offset);
				typedQuery.setMaxResults(offset);
				list = typedQuery.getResultList();

//				list =list.stream().filter(o->o.get("paymentStatus").equals("SUCCESS")).collect(Collectors.toList());
				List<PaymentStausRes> statusResList = new ArrayList<PaymentStausRes>();
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						PaymentStausRes res1 = new PaymentStausRes();
						res1.setBranchCode(data.get("branchCode") == null ? "" : data.get("branchCode").toString());
						res1.setBranchCode(data.get("branchName") == null ? "" : data.get("branchName").toString());
						res1.setCompanyId(data.get("companyId") == null ? "" : data.get("companyId").toString());
						res1.setClientName(data.get("customerName") == null ? "" : data.get("customerName").toString());
						res1.setLoginId(data.get("loginId") == null ? "" : data.get("loginId").toString());
						res1.setUserType(data.get("userType") == null ? "" : data.get("userType").toString());
						res1.setQuoteNo(data.get("quoteNo") == null ? "" : data.get("quoteNo").toString());
						res1.setPaymentId(data.get("paymentId") == null ? "" : data.get("paymentId").toString());
						res1.setPaymentStatus(
								data.get("paymentStatus") == null ? "" : data.get("paymentStatus").toString());
						res1.setPaymentTypedesc(
								data.get("paymentTypedesc") == null ? "" : data.get("paymentTypedesc").toString());
						res1.setInceptionDate(data.get("inceptionDate") == null ? null
								: sdf.format(data.get("inceptionDate")).toString());
						res1.setExpiryDate(
								data.get("expiryDate") == null ? null : sdf.format(data.get("expiryDate")).toString());
						res1.setEntryDate(
								data.get("entryDate") == null ? null : sdf.format(data.get("entryDate")).toString());
						res1.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						statusResList.add(res1);

					}

				}
				res.setPaymentStausRes(statusResList);
				res.setTotalCount(successCount(req));

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	private Long successCount(GetPaymentStatusReq req) {
		Long count = 0l;
		try {
			if (StringUtils.isNotBlank(req.getLoginId())) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Long> list = new ArrayList<Long>();

				query.multiselect(cb.count(h));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "ACCEPTED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				predics.add(cb.equal(h.get("loginId"), req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Long> typedQuery = em.createQuery(query);
				list = typedQuery.getResultList();

//				list =list.stream().filter(o->o.get("paymentStatus").equals("SUCCESS")).collect(Collectors.toList());

				count = list.get(0);
				// reslist.add(statusResList);
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();

				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Long> list = new ArrayList<Long>();

				query.multiselect(cb.count(h));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "ACCEPTED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				// predics.add(cb.equal(h.get("loginId"),req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Long> typedQuery = em.createQuery(query);
				list = typedQuery.getResultList();
//					
//					list =list.stream().filter(o->o.get("paymentStatus").equals("SUCCESS")).collect(Collectors.toList());

				count = list.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	}

	private Long failureCount(GetPaymentStatusReq req) {
		Long count = 0l;
		try {
			if (StringUtils.isNotBlank(req.getLoginId())) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Long> list = new ArrayList<Long>();

				query.multiselect(cb.count(h));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "FAILED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				predics.add(cb.equal(h.get("loginId"), req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Long> typedQuery = em.createQuery(query);
				list = typedQuery.getResultList();
				count = list.get(0);
				// list
				// =list.stream().filter(o->o.get("paymentStatus").equals("FAILED")).collect(Collectors.toList());

			} else {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Long> list = new ArrayList<Long>();

				query.multiselect(cb.count(h));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "FAILED"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				// predics.add(cb.equal(h.get("loginId"),req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Long> typedQuery = em.createQuery(query);
				list = typedQuery.getResultList();
				count = list.get(0);
				// list
				// =list.stream().filter(o->o.get("paymentStatus").equals("FAILED")).collect(Collectors.toList());

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	}

	private Long pendingCount(GetPaymentStatusReq req) {
		Long count = 0l;
		try {

			if (StringUtils.isNotBlank(req.getLoginId())) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Long> list = new ArrayList<Long>();

				query.multiselect(cb.count(h));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "PENDING"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				predics.add(cb.equal(h.get("loginId"), req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Long> typedQuery = em.createQuery(query);
				list = typedQuery.getResultList();
				count = list.get(0);
			} else {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				Root<PaymentDetail> p = query.from(PaymentDetail.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				List<Long> list = new ArrayList<Long>();

				query.multiselect(cb.count(h));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(p.get("entryDate")));
				// max of merchantReference
				Subquery<Long> merchantReference = query.subquery(Long.class);
				Root<PaymentDetail> ocpm2 = merchantReference.from(PaymentDetail.class);
				merchantReference.select(cb.max(ocpm2.get("merchantReference")));
				Predicate a3 = cb.equal(p.get("quoteNo"), ocpm2.get("quoteNo"));
				merchantReference.where(a3);
				List<Predicate> predics = new ArrayList<Predicate>();
				predics.add(cb.equal(p.get("quoteNo"), h.get("quoteNo")));
				predics.add(cb.equal(p.get("paymentType"), "4"));
				predics.add(cb.equal(p.get("branchCode"), req.getBranchCode()));
				predics.add(cb.equal(h.get("productId"), req.getProductId()));
				predics.add(cb.equal(p.get("companyId"), req.getCompanyId()));
				predics.add(cb.equal(p.get("paymentStatus"), "PENDING"));
				predics.add(cb.equal(p.get("merchantReference"), merchantReference));
				// predics.add(cb.equal(h.get("loginId"),req.getLoginId()));
				query.where(predics.toArray(new Predicate[0])).orderBy(orderList);

				TypedQuery<Long> typedQuery = em.createQuery(query);
				list = typedQuery.getResultList();
				count = list.get(0);

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	}

	@Override
	public List<PortFolioDashBoardRes> getB2cAdminPortfolio(PortFolioDashBoardReq req) {
		List<PortFolioDashBoardRes> resList = new ArrayList<PortFolioDashBoardRes>();
		DecimalFormat df = new DecimalFormat("0.##");
		try {
			List<CompanyProductMaster> productList = getCompanyProductList(req.getInsuranceId());
			List<PortFolioAdminTupleRes> list = getPortFolioB2cDashBoard(req);

			// Group By Product Id
			// Map<Integer ,List<PortFolioAdminTupleRes>> groupByProductId =
			// list.stream().collect(Collectors.groupingBy(PortFolioAdminTupleRes ::
			// getProductId )) ;
			for (CompanyProductMaster product : productList) {

				if (StringUtils.isBlank(req.getProductId()) || "99999".equalsIgnoreCase(req.getProductId())
						|| product.getProductId().equals(Integer.valueOf(req.getProductId()))) {

					List<PortFolioAdminTupleRes> filterProduct = list.stream()
							.filter(o -> o.getProductId() != null && o.getProductId().equals(product.getProductId()))
							.collect(Collectors.toList());

					// Map Broker List
					List<PortfolioBrokerListRes> brokerResList = new ArrayList<PortfolioBrokerListRes>();
					for (PortFolioAdminTupleRes data : filterProduct) {
						PortfolioBrokerListRes brokerRes = new PortfolioBrokerListRes();

						if (StringUtils.isBlank(data.getBdmCode()) && "b2c".equalsIgnoreCase(data.getSourceType())) {
							brokerRes.setBrokerCode(
									data.getCustomerCode() == null ? "0" : data.getCustomerCode().toString());
							brokerRes.setBrokerName(data.getCustomerName());
						}
//						brokerRes.setBrokerLoginId(data.getLoginId());
//						brokerRes.setSubUserType(data.getSubUserType());
						brokerRes.setTotalCount(data.getCount() == null ? 0 : data.getCount());
						brokerRes.setTotalPremiumLc(data.getOverallPremiumLc() == null ? "0"
								: df.format(Double.valueOf(data.getOverallPremiumLc().toPlainString())));
						brokerRes.setTotalPremiumFc(data.getOverallPremiumFc() == null ? "0"
								: df.format(Double.valueOf(data.getOverallPremiumFc().toPlainString())));
						brokerRes.setUserType(data.getUserType());
						brokerRes.setSourceType(data.getSourceType());
						brokerRes.setBdmCode(data.getBdmCode());
						brokerResList.add(brokerRes);
					}
					brokerResList.sort(Comparator.comparing(PortfolioBrokerListRes::getTotalCount).reversed());

					// Response
					PortFolioDashBoardRes res = new PortFolioDashBoardRes();
					res.setBrokerList(brokerResList);
					res.setProductId(product.getProductId().toString());
					res.setProductName(product.getProductName());
					res.setBrokerCount(brokerResList.size() > 0 ? Long.valueOf(brokerResList.size()) : 0);
					resList.add(res);
				}

			}
			resList.sort(Comparator.comparing(PortFolioDashBoardRes::getBrokerCount).reversed());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	public List<PortFolioAdminTupleRes> getPortFolioB2cDashBoard(PortFolioDashBoardReq req) {
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
					h.get("productName").alias("productName"),
					cb.max(l.get("agencyCode")).as(Integer.class).alias("oaCode"),
					cb.max(u.get("userName")).alias("brokerName"), cb.max(l.get("userType")).alias("userType"),
					cb.max(l.get("subUserType")).alias("subUserType"), cb.max(l.get("loginId")).alias("loginId"),
					cb.max(h.get("customerCode")).alias("customerCode"),
					cb.max(h.get("customerName")).alias("customerName"),
					cb.max(h.get("sourceType")).alias("sourceType"), cb.max(h.get("bdmCode")).alias("bdmCode"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("productName")));

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.greaterThanOrEqualTo(h.get("entryDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("entryDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(h.get("subUserType"), "b2c"));
			predicate.add(cb.equal(h.get("sourceType"), "b2c"));
			predicate.add(cb.equal(l.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(u.get("loginId"), h.get("loginId")));
			// Business Type Condition)
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();

			if ("NB2C".equalsIgnoreCase(businessType)) {
				predicate.add(cb.equal(h.get("status"), "P"));
				Predicate n1 = cb.isNull(h.get("endtStatus"));
				Predicate n2 = cb.equal(h.get("endtStatus"), "");
				predicate.add(cb.or(n1, n2));

			}
//			else if ("E".equalsIgnoreCase(businessType)) {
//				predicate.add(cb.equal(h.get("status"), "P"));
//				predicate.add(cb.equal(h.get("endtStatus"), "C"));
//				predicate.add(cb.notEqual(h.get("endtTypeId"), "842"));
//
//			} else if ("C".equalsIgnoreCase(businessType)) {
//				predicate.add(cb.equal(h.get("status"), "P"));
//				predicate.add(cb.equal(h.get("endtStatus"), "C"));
//				predicate.add(cb.equal(h.get("endtTypeId"), "842"));
//			}

			// Product & Branch Condition
			if (StringUtils.isNotBlank(req.getProductId()))
				predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0])).groupBy(h.get("productId"), h.get("productName"),
//					h.get("sourceType"),
					h.get("loginId")).orderBy(orderList);

			// Get Result
			TypedQuery<PortFolioAdminTupleRes> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	@Override
	public List<PortfolioGridRes> getAllPolicyB2cGrid(PortFolioGridReq req) {
		List<PortfolioGridRes> resList = new ArrayList<PortfolioGridRes>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DecimalFormat df = new DecimalFormat("0.##");
		try {

			// Fetch Data
			List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
			String bsType = StringUtils.isNotBlank(req.getBusinessType()) ? req.getBusinessType() : "N";

			if ("NB2C".equalsIgnoreCase(bsType)) {
				list = getPortFolioB2cGrid(req);
			} /*
				 * else { CompanyProductMaster product =
				 * getCompanyProductMasterDropdown(req.getInsuranceId(), req.getProductId());
				 * String productType = StringUtils.isNotBlank(product.getMotorYn()) ?
				 * product.getMotorYn() : "M";
				 * 
				 * if ("H".equalsIgnoreCase(productType) &&
				 * "4".equalsIgnoreCase(req.getProductId())) { list =
				 * getPortFolioTravelGrid(req);
				 * 
				 * } else if ("M".equalsIgnoreCase(productType)) { list =
				 * getPortFolioMotorGrid(req);
				 * 
				 * } else if ("A".equalsIgnoreCase(productType)) { list =
				 * getPortFolioBuildingGrid(req);
				 * 
				 * } else if ("H".equalsIgnoreCase(productType)) { list =
				 * getPortFolioHumanGrid(req); } }
				 */

			// Sort By Broker Name
//			list.sort(Comparator.comparing(PortfolioAdminGridRes :: getBrokerName  ) 
//					 .thenComparing(PortfolioAdminGridRes :: getUpdatedDate  ).reversed());

			// Sort By Updated Date
			list.sort(Comparator.comparing(PortfolioAdminGridRes::getUpdatedDate).reversed());

			// Map Broker List
			for (PortfolioAdminGridRes data : list) {
				PortfolioGridRes res = new PortfolioGridRes();

				res.setApplicationId(data.getApplicationId() == null ? "" : data.getApplicationId().toString());
				res.setBrokerCode(data.getOaCode() == null ? "" : data.getOaCode().toString());
				res.setBrokerLoginId(data.getLoginId());
				res.setBrokerName(data.getBrokerName());
				res.setBranchCode(data.getBranchCode());
				res.setBranchName(data.getBranchName());
				res.setBrokerBranchCode(data.getBrokerBranchCode());
				res.setBrokerBranchName(data.getBrokerBranchName());
				res.setCount(data.getCount());
				res.setCurrencyCode(data.getCurrencyCode());
				res.setCustomerName(data.getCustomerName());
				res.setExchangeRate(data.getExchangeRate() == null ? "" : data.getExchangeRate().toPlainString());
				res.setOriginalPolicyNo(StringUtils.isNotBlank(data.getOriginalPolicyNo()) ? data.getOriginalPolicyNo()
						: data.getPolicyNo());
				res.setOverallPremiumFc(data.getOverallPremiumFc() == null ? "0"
						: df.format(Double.valueOf(data.getOverallPremiumFc().toPlainString())));
				res.setOverallPremiumLc(data.getOverallPremiumLc() == null ? "0"
						: df.format(Double.valueOf(data.getOverallPremiumLc().toPlainString())));
				res.setPolicyStartDate(data.getPolicyStartDate() == null ? "" : sdf.format(data.getPolicyStartDate()));
				res.setPolicyEndDate(data.getPolicyEndDate() == null ? "" : sdf.format(data.getPolicyEndDate()));
				res.setPolicyNo(data.getPolicyNo());
				res.setQuoteNo(data.getQuoteNo());
				res.setRequestReferenceNo(data.getRequestReferenceNo());
				res.setSubUserType(data.getSubUserType());
				res.setUserType(data.getUserType());
				res.setProductId(data.getProductId() == null ? "" : data.getProductId().toString());
				res.setProductName(data.getProductName());
				res.setAdminRemarks(data.getAdminRemarks());
				res.setReferralRemarks(data.getReferralRemarks());
				res.setAdminLoginId(data.getAdminLoginId());
				res.setStatus(data.getStatus());
				res.setEndtSatus(data.getEndtStatus());
				res.setUpdatedDate(data.getUpdatedDate() == null ? "" : sdf.format(data.getUpdatedDate()));
				res.setEndorsementRemarks(data.getEndorsementRemarks());
				String statusDesc = StringUtils.isBlank(data.getStatus()) ? ""
						: "Y".equalsIgnoreCase(data.getStatus()) ? "Existing Quote"
								: "R".equalsIgnoreCase(data.getStatus()) ? "Quote Rejected"
										: "N".equalsIgnoreCase(data.getStatus()) ? "Quote Deactivated"
												: "D".equalsIgnoreCase(data.getStatus()) ? "Quote Deleted"
														: "RP".equalsIgnoreCase(data.getStatus()) ? "Refferral Pending"
																: "RA".equalsIgnoreCase(data.getStatus())
																		? "Refferral Approved"
																		: "RR".equalsIgnoreCase(data.getStatus())
																				? "Refferral Rejected"
																				: "RA".equalsIgnoreCase(
																						data.getStatus())
																								? "Refferral Request"
																								: "P".equalsIgnoreCase(
																										data.getStatus())
																												? "Policy Converted"
																												: "E".equalsIgnoreCase(
																														data.getStatus())
																																? "Endorsement"
																																: "";

				String endtStatusDesc = StringUtils.isBlank(data.getEndtStatus()) ? ""
						: "P".equalsIgnoreCase(data.getEndtStatus()) ? "Pending"
								: "C".equalsIgnoreCase(data.getEndtStatus()) ? "Completed" : "";

				res.setStatusDesc(statusDesc);
				res.setEndtStatusDesc(endtStatusDesc);

				res.setCreditNo(data.getCreditNo());
				res.setDebitNo(data.getDebitNoteNo());

				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	public List<PortfolioAdminGridRes> getPortFolioB2cGrid(PortFolioGridReq req) {
		List<PortfolioAdminGridRes> list = new ArrayList<PortfolioAdminGridRes>();
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

			Integer limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			Integer offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminGridRes> query = cb.createQuery(PortfolioAdminGridRes.class);

			// Find All
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);

			// Select
			query.multiselect(h.get("creditNo").alias("creditNo"), h.get("debitNoteNo").alias("debitNoteNo"),
					h.get("applicationId").alias("applicationId"), h.get("noOfVehicles").as(Long.class).alias("count"),
					h.get("overallPremiumLc").alias("overallPremiumLc"),
					h.get("overallPremiumFc").alias("overallPremiumFc"), h.get("currency").alias("currencyCode"),
					h.get("exchangeRate").alias("exchangeRate"),
					h.get("requestReferenceNo").alias("requestReferenceNo"), h.get("quoteNo").alias("quoteNo"),
					h.get("policyNo").alias("policyNo"), h.get("originalPolicyNo").alias("originalPolicyNo"),
					h.get("productId").alias("productId"), h.get("productName").alias("productName"),
					h.get("agencyCode").alias("oaCode"), h.get("loginId").alias("loginId"),
					h.get("referralDescription").alias("referralRemarks"), h.get("adminRemarks").alias("adminRemarks"),
					h.get("adminLoginId").alias("adminLoginId"), h.get("status").alias("status"),
					h.get("endtStatus").alias("endtStatus"), h.get("customerName").alias("customerName"),
					h.get("inceptionDate").alias("policyStartDate"), h.get("expiryDate").alias("policyEndDate"),
					h.get("branchCode").alias("branchCode"), h.get("branchName").alias("branchName"),
					h.get("brokerBranchCode").alias("brokerBranchCode"),
					h.get("brokerBranchName").alias("brokerBranchName"), u.get("userName").alias("brokerName"),
					l.get("userType").alias("userType"), l.get("subUserType").alias("subUserType"),
					h.get("effectiveDate").alias("updatedDate"),
					h.get("endorsementRemarks").alias("endorsementRemarks"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(h.get("effectiveDate")));

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.greaterThanOrEqualTo(h.get("effectiveDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("effectiveDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(u.get("loginId"), h.get("loginId")));
			predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			predicate.add(cb.equal(l.get("loginId"), req.getLoginId()));

			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType();

			if ("NB2C".equalsIgnoreCase(businessType)) {
				predicate.add(cb.equal(h.get("status"), "P"));
				Predicate n1 = cb.isNull(h.get("endtStatus"));
				Predicate n2 = cb.equal(h.get("endtStatus"), "");
				predicate.add(cb.or(n1, n2));

			} /*
				 * else if ("E".equalsIgnoreCase(businessType)) {
				 * predicate.add(cb.equal(h.get("status"), "P"));
				 * predicate.add(cb.equal(h.get("endtStatus"), "C"));
				 * predicate.add(cb.notEqual(h.get("endtTypeId"), "842"));
				 * 
				 * } else if ("C".equalsIgnoreCase(businessType)) {
				 * predicate.add(cb.equal(h.get("status"), "P"));
				 * predicate.add(cb.equal(h.get("endtStatus"), "C"));
				 * predicate.add(cb.equal(h.get("endtTypeId"), "842")); }
				 */

			// Branch Condition
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode())))
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));

			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);

			// Get Result
			TypedQuery<PortfolioAdminGridRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	@Override
	public GetallExistingRejectedLapsedRes getallExistingQuoteSQ(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		QuoteCriteriaResponse cres = new QuoteCriteriaResponse();

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -90);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<QuoteCriteriaRes> extingQuoteList = new ArrayList<QuoteCriteriaRes>();

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				cres = motService.getMotorExistingQuoteDetailsSQ(req, before30, today, limit, offset);

				extingQuoteList = cres.getQuoteRes();
			}
//			} else if (product.getMotorYn().equalsIgnoreCase("H")
//					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
//
//				TravelQuoteCriteriaResponse tcres = traService.getTravelExistingQuoteDetails(req, before30, today,
//						limit, offset);
//				List<TravelQuoteCriteriaRes> textingQuoteList = new ArrayList<TravelQuoteCriteriaRes>();
//				textingQuoteList = tcres.getQuoteRes();
//
//				for (TravelQuoteCriteriaRes data : textingQuoteList) {
//					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
//					dozerMapper.map(data, res);
//					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
//					custRes.add(res);
//				}
//				resp.setCustomerDetailsRes(custRes);
//				resp.setTotalCount(tcres.getTotalCount());
//
//			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
//				cres = buiService.getBuildingExistingQuoteDetails(req, before30, today, limit, offset);
//
//				extingQuoteList = cres.getQuoteRes();
//				// Common
//			}else if (product.getMotorYn().equalsIgnoreCase("L")) {
//				
//				cres = lifeService.getLifeExistingQuoteDetails(req, before30, today, limit, offset);
//
//				extingQuoteList = cres.getQuoteRes();
//				// Common
//			}
//			else {
//				cres = commonService.getCommonExistingQuoteDetails(req, before30, today, limit, offset);
//				extingQuoteList = cres.getQuoteRes();
//			}

			if (!req.getProductId().equalsIgnoreCase(travelProductId)) {

				for (QuoteCriteriaRes data : extingQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					dozerMapper.map(data, res);
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(cres.getTotalCount());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public GetallExistingRejectedLapsedRes getallLapsedQuoteDetailSQ(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		QuoteCriteriaResponse cres = new QuoteCriteriaResponse();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			List<QuoteCriteriaRes> lapsedQuoteList = new ArrayList<QuoteCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				cres = motService.getMotorLapsedQuoteDetailsSQ(req, before30, limit, offset);
				lapsedQuoteList = cres.getQuoteRes();

			}
//				else if (product.getMotorYn().equalsIgnoreCase("H")
//					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
//
//				TravelQuoteCriteriaResponse tcres = traService.getTravelLapsedQuoteDetails(req, before30, limit,
//						offset);
//				List<TravelQuoteCriteriaRes> textingQuoteList = new ArrayList<TravelQuoteCriteriaRes>();
//				textingQuoteList = tcres.getQuoteRes();
//
//				for (TravelQuoteCriteriaRes data : textingQuoteList) {
//					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
//					dozerMapper.map(data, res);
//					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
//					custRes.add(res);
//				}
//				resp.setCustomerDetailsRes(custRes);
//				resp.setTotalCount(tcres.getTotalCount());
//
//			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
//
//				cres = buiService.getBuildingLapsedQuoteDetails(req, before30, limit, offset);
//				lapsedQuoteList = cres.getQuoteRes();
//
//			} else if (product.getMotorYn().equalsIgnoreCase("L")) {
//				cres = lifeService.getLifeLapsedQuoteDetails(req, before30, limit, offset);
//				lapsedQuoteList = cres.getQuoteRes();
//
//			}
//			else {
//				cres = commonService.getCommonLapsedQuoteDetails(req, before30, limit, offset);
//				lapsedQuoteList = cres.getQuoteRes();
//			}

			if (!req.getProductId().equalsIgnoreCase(travelProductId)) {
				for (QuoteCriteriaRes data : lapsedQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount() == null ? "" :
					// data.getIdsCount().toString());
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(cres.getTotalCount());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public GetallExistingRejectedLapsedRes getallRejectedQuoteSQ(ExistingQuoteReq req) {
		GetallExistingRejectedLapsedRes resp = new GetallExistingRejectedLapsedRes();
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		GetRejectedQuoteDetailsRes cres = new GetRejectedQuoteDetailsRes();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<RejectCriteriaRes> rejectedQuoteList = new ArrayList<RejectCriteriaRes>();
			List<TravelRejectCriteriaRes> trejectedQuoteList = new ArrayList<TravelRejectCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				cres = motService.getMotorRejectedQuoteSQ(req, before30, today, limit, offset);
				rejectedQuoteList = cres.getQuoteRes();

			}
//			else if (product.getMotorYn().equalsIgnoreCase("H")
//					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
//
//				GetTravelRejectedQuoteDetailsRes tcres = traService.getTravelRejectedQuoteDetails(req, before30, today,
//						limit, offset);
//				trejectedQuoteList = tcres.getQuoteRes();
//				for (TravelRejectCriteriaRes data : trejectedQuoteList) {
//					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
//					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
//					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
//					custRes.add(res);
//				}
//				resp.setCustomerDetailsRes(custRes);
//				resp.setTotalCount(tcres.getTotalCount());
//
//			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
//				cres = buiService.getBuildingRejectedQuoteDetails(req, before30, today, limit, offset);
//				rejectedQuoteList = cres.getQuoteRes();
//			} 
//			else if (product.getMotorYn().equalsIgnoreCase("L")) {
//			
//				cres = lifeService.getLifeRejectedQuoteDetails(req, before30, today, limit, offset);
//				rejectedQuoteList = cres.getQuoteRes();
//
//			} 
//			else {
//				cres = commonService.getCommonRejectedQuoteDetails(req, before30, today, limit, offset);
//				rejectedQuoteList = cres.getQuoteRes();
//			}

			if (!req.getProductId().equalsIgnoreCase(travelProductId)) {

				for (RejectCriteriaRes data : rejectedQuoteList) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					custRes.add(res);
				}
				resp.setCustomerDetailsRes(custRes);
				resp.setTotalCount(cres.getTotalCount());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	@Override
	public ViewLoginDetailsRes viewLoginDetails(ExistingQuoteReq req) {
		ViewLoginDetailsRes resp = new ViewLoginDetailsRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		QuoteCriteriaResponse cres = new QuoteCriteriaResponse();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		List<CountRes> countResList = new ArrayList<CountRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date loginCreatedDate = null;
			Date lastLoginDate = null;

			CountRes count = new CountRes();
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			if (loginData != null) {
				loginCreatedDate = loginData.getEntryDate();
			}
			List<SessionMaster> sessionList = sessionRepo.findByLoginIdOrderByEntryDateDesc(req.getLoginId());
			if (sessionList != null && sessionList.size() > 0) {
				lastLoginDate = sessionList.get(0).getEntryDate();
			}
			System.out.println("Entering into Thread");
			// Thread Call Setup To Fetch List From 4 tables
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			MyTaskList taskList = new MyTaskList(queue);
			ViewLoginExistingQuoteThreadCall motorexisting = new ViewLoginExistingQuoteThreadCall(
					"getMotorExistingQuote", req, em, loginCreatedDate, today);
			ViewLoginExistingQuoteThreadCall travelexisting = new ViewLoginExistingQuoteThreadCall(
					"getTravelExistingQuote", req, em, loginCreatedDate, today);
			ViewLoginExistingQuoteThreadCall buildingexisting = new ViewLoginExistingQuoteThreadCall(
					"getAssetExistingQuote", req, em, loginCreatedDate, today);
			ViewLoginExistingQuoteThreadCall humanexisting = new ViewLoginExistingQuoteThreadCall(
					"getHummanExistingQuote", req, em, loginCreatedDate, today);
			System.out.println("Adding to Queue");
			queue.add(motorexisting);
			queue.add(travelexisting);
			queue.add(buildingexisting);
			queue.add(humanexisting);
			int threadCount = 4;
			int success = 0;
			ForkJoinPool forkjoin = new ForkJoinPool(threadCount);
			ConcurrentLinkedQueue<Future<Object>> invoke = (ConcurrentLinkedQueue<Future<Object>>) forkjoin
					.invoke(taskList);
			System.out.println("Declaring list");
			List<LoginQuoteCriteriaResponse> motorList = new ArrayList<LoginQuoteCriteriaResponse>();
			List<LoginQuoteCriteriaResponse> travelList = new ArrayList<LoginQuoteCriteriaResponse>();
			List<LoginQuoteCriteriaResponse> buildingList = new ArrayList<LoginQuoteCriteriaResponse>();
			List<LoginQuoteCriteriaResponse> humanList = new ArrayList<LoginQuoteCriteriaResponse>();
			List<Date> lastQuoteDate = new ArrayList<Date>();
			List<Date> lastPolicyDate = new ArrayList<Date>();
			List<Double> totalPremium = new ArrayList<Double>();
			List<LoginPolicyCountTupleRes> filterProduct1 = null;
			List<LoginPolicyCountTupleRes> filterProduct2 = null;
			Long poicyCount, endtCount;
			for (Future<Object> callable : invoke) {

				log.info(callable.getClass() + "," + callable.isDone());

				if (callable.isDone()) {
					Map<String, Object> map = (Map<String, Object>) callable.get();

					for (Entry<String, Object> future : map.entrySet()) {

						if ("getMotorExistingQuote".equalsIgnoreCase(future.getKey())) {
							motorList = (List<LoginQuoteCriteriaResponse>) future.getValue();
							if (motorList != null && motorList.size() > 0) {
								System.out.println("MotorList " + motorList);
								lastQuoteDate.add(dateFormat.parse(motorList.get(0).getLastQuoteDate()));
							}
						} else if ("getTravelExistingQuote".equalsIgnoreCase(future.getKey())) {
							travelList = (List<LoginQuoteCriteriaResponse>) future.getValue();
							if (travelList != null && travelList.size() > 0) {
								lastQuoteDate.add(dateFormat.parse(travelList.get(0).getLastQuoteDate()));
							}
						} else if ("getAssetExistingQuote".equalsIgnoreCase(future.getKey())) {
							buildingList = (List<LoginQuoteCriteriaResponse>) future.getValue();
							if (buildingList != null && buildingList.size() > 0) {
								lastQuoteDate.add(dateFormat.parse(buildingList.get(0).getLastQuoteDate()));
							}
						} else if ("getHummanExistingQuote".equalsIgnoreCase(future.getKey())) {
							humanList = (List<LoginQuoteCriteriaResponse>) future.getValue();
							if (humanList != null && humanList.size() > 0) {
								lastQuoteDate.add(dateFormat.parse(humanList.get(0).getLastQuoteDate()));
							}
						}
					}

					success++;
				}
			}

			List<String> companyIds = new ArrayList<String>();
			companyIds.add(req.getInsuranceId());
			List<LoginProductMaster> brokerProduct = getBrokerProducts(req.getLoginId(), companyIds, today);
			brokerProduct.sort(Comparator.comparing(LoginProductMaster::getProductId));
			List<Integer> productIds = brokerProduct.stream().map(LoginProductMaster::getProductId)
					.collect(Collectors.toList());
			List<CompanyProductMaster> productList = getFilterProductList(req.getInsuranceId(), productIds);

			// Policy
			PortFolioDashBoardReq policyReq = new PortFolioDashBoardReq();
			policyReq.setBranchCode("99999");
			policyReq.setLoginId(req.getLoginId());
			policyReq.setInsuranceId(req.getInsuranceId());
			policyReq.setStartDate(loginCreatedDate);
			policyReq.setEndDate(today);
			policyReq.setBusinessType("N");
			List<LoginPolicyCountTupleRes> list = getLoginPolicyCount(policyReq);

			// Endt
			PortFolioDashBoardReq policyReq1 = new PortFolioDashBoardReq();
			policyReq1.setBranchCode("99999");
			policyReq1.setLoginId(req.getLoginId());
			policyReq1.setInsuranceId(req.getInsuranceId());
			policyReq1.setStartDate(loginCreatedDate);
			policyReq1.setEndDate(today);
			policyReq1.setBusinessType("E");
			List<LoginPolicyCountTupleRes> list1 = getLoginPolicyCount(policyReq1);
			// Group By Product Id
			for (CompanyProductMaster product : productList) {
				poicyCount = 0l;
				endtCount = 0l;
				String productId = product.getProductId().toString();
				if (StringUtils.isBlank(req.getProductId()) || "99999".equalsIgnoreCase(req.getProductId())
						|| product.getProductId().equals(Integer.valueOf(req.getProductId()))) {

					String productType = StringUtils.isBlank(product.getMotorYn()) ? "M" : product.getMotorYn();
					List<LoginQuoteCriteriaResponse> filterProduct = new ArrayList<LoginQuoteCriteriaResponse>();
					System.out.println("**********************PolicyCount");
					if (list != null && list.size() > 0) {
						filterProduct1 = list.stream().filter(
								o -> o.getProductId() != null && o.getProductId().equals(Integer.valueOf(productId)))
								.collect(Collectors.toList());
						if (filterProduct1.size() > 0 && filterProduct1 != null) {
							lastPolicyDate.add(filterProduct1.get(0).getEntryDate());
							totalPremium.add(Double.valueOf(filterProduct1.get(0).getOverallPremiumLc().toString()));
							poicyCount = filterProduct1.get(0).getCount();
						}
					}
					System.out.println("**********************EndtCount");
					if (list != null && list.size() > 0) {
						filterProduct2 = list1.stream().filter(
								o -> o.getProductId() != null && o.getProductId().equals(Integer.valueOf(productId)))
								.collect(Collectors.toList());
						if (filterProduct2.size() > 0 && filterProduct2 != null) {
							endtCount = filterProduct2.get(0).getCount();
						}
					}
					if ("H".equalsIgnoreCase(productType) && product.getProductId().equals(4))
						filterProduct = travelList;

					if ("M".equalsIgnoreCase(productType))
						filterProduct = motorList.stream()
								.filter(o -> o.getProductId() != null && o.getProductId().equals(productId))
								.collect(Collectors.toList());

					else if ("A".equalsIgnoreCase(productType))
						filterProduct = buildingList.stream()
								.filter(o -> o.getProductId() != null && o.getProductId().equals(productId))
								.collect(Collectors.toList());

					else if ("H".equalsIgnoreCase(productType))
						filterProduct = humanList.stream()
								.filter(o -> o.getProductId() != null && o.getProductId().equals(productId))
								.collect(Collectors.toList());

					// Map
					if (filterProduct != null && filterProduct.size() > 0) {
						for (LoginQuoteCriteriaResponse data : filterProduct) {
							CountRes countRes = new CountRes();
							countRes.setProductId(data.getProductId());
							countRes.setProductName(data.getProductName());
							countRes.setQuotetotalCount(data.getQuoteCount());
							countRes.setPolicytotalCount(poicyCount);
							countRes.setEndttotalCount(endtCount);
							countResList.add(countRes);
						}
					} else {
						CountRes countRes = new CountRes();
						countRes.setProductId(productId);
						countRes.setProductName(product.getProductName());
						countRes.setQuotetotalCount(0l);
						countRes.setPolicytotalCount(poicyCount);
						countRes.setEndttotalCount(endtCount);
						countResList.add(countRes);
					}
				}

			}
			Double totalPre = totalPremium.stream().mapToDouble(Double::doubleValue).sum();
			// Finding the maximum (latest) date using Collectors.maxBy
			Optional<Date> lastQuote = lastQuoteDate.stream().collect(Collectors.maxBy(Comparator.naturalOrder()));
			Optional<Date> lastPolicy = lastPolicyDate.stream().collect(Collectors.maxBy(Comparator.naturalOrder()));
			DecimalFormat decimalFormatter = new DecimalFormat("#.#");
			resp.setProductDetails(countResList);
			resp.setLastLoginDate(lastLoginDate);
			resp.setLastPolicyDate(lastPolicy.isEmpty() ? null : lastPolicy.get());
			resp.setLastQuoteDate(lastQuote.isEmpty() ? null : lastQuote.get());
			resp.setCollectedPremium(decimalFormatter.format(totalPre).toString());
			resp.setPolicyCommission(null);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	public List<LoginPolicyCountTupleRes> getLoginPolicyCount(PortFolioDashBoardReq req) {
		List<LoginPolicyCountTupleRes> list = new ArrayList<LoginPolicyCountTupleRes>();
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
			CriteriaQuery<LoginPolicyCountTupleRes> query = cb.createQuery(LoginPolicyCountTupleRes.class);

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
					cb.max(h.get("bdmCode")).alias("bdmCode"), cb.max(h.get("entryDate")).alias("entryDate"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("productId")));

			// Broker condition
			Subquery<Long> loginId = query.subquery(Long.class);
			Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
			loginId.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), h.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("loginId"), h.get("loginId"));
			Predicate a3 = cb.equal(ocpm1.get("oaCode"), l.get("agencyCode"));
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
			TypedQuery<LoginPolicyCountTupleRes> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());

		}
		return list;
	}

	public List<LoginProductMaster> getBrokerProducts(String loginId, List<String> companyIds, Date today) {
		List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
		try {
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
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> c = query.from(LoginProductMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = amendId.from(LoginProductMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("loginId"), ocpm1.get("loginId"));
			amendId.where(a1, a2, a3);

			// Filer Product IDs
			Subquery<Long> productIds = query.subquery(Long.class);
			Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);

			Subquery<Date> effectiveDate3 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm4 = effectiveDate3.from(CompanyProductMaster.class);
			effectiveDate3.select(cb.greatest(ocpm4.get("effectiveDateStart").as(Date.class)));
			Predicate a9 = cb.equal(cm.get("productId"), ocpm4.get("productId"));
			Predicate a10 = cb.equal(cm.get("companyId"), ocpm4.get("companyId"));
			Predicate a11 = cb.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today);
			effectiveDate3.where(a9, a10, a11);

			Subquery<Date> effectiveDate4 = query.subquery(Date.class);
			Root<CompanyProductMaster> ocpm5 = effectiveDate4.from(CompanyProductMaster.class);
			effectiveDate4.select(cb.greatest(ocpm5.get("effectiveDateEnd").as(Date.class)));
			Predicate a12 = cb.equal(cm.get("productId"), ocpm5.get("productId"));
			Predicate a13 = cb.equal(cm.get("companyId"), ocpm5.get("companyId"));
			Predicate a14 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			effectiveDate4.where(a12, a13, a14);

			productIds.select(cm.get("productId"));
			Predicate a15 = cb.equal(cm.get("companyId"), companyIds.get(0));
			Predicate a16 = cb.equal(cm.get("status"), "Y");
			Predicate a17 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
			Predicate a18 = cb.equal(cm.get("effectiveDateEnd"), effectiveDate4);
			productIds.where(a15, a16, a17, a18);

			// In
			Expression<String> e0 = c.get("productId");

			// Where
			Predicate n2 = cb.equal(c.get("amendId"), amendId);
			Predicate n4 = cb.equal(c.get("companyId"), companyIds.get(0));
			Predicate n5 = cb.equal(c.get("loginId"), loginId);
			Predicate n6 = e0.in(productIds);
			query.where(n2, n4, n5, n6).orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list;
	}

	public synchronized List<CompanyProductMaster> getFilterProductList(String companyId, List<Integer> peoductIds) {
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
			Expression<String> e0 = c.get("productId");
			Predicate n5 = (e0.in(peoductIds));
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
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

	@Override
	public List<PortfolioByRegNoRes> getAllPorfolioByRegNo(PortfolioSearchReq req) {
		List<PortfolioByRegNoRes> resList = new ArrayList<PortfolioByRegNoRes>();
		List<HomePositionMaster> list = new ArrayList<HomePositionMaster>();
		List<MotorDataDetails> motList = new ArrayList<MotorDataDetails>();
		try {
			if (req.getSearchBy().equalsIgnoreCase("REGISTRATION_NUMBER")) {
				List<MotorDataDetails> motDataList = motDataRepo.findByRegistrationNumber(req.getRegistrationNumber());

				List<String> quoteList = motDataList.stream().map(quote -> quote.getQuoteNo())
						.collect(Collectors.toList());
				// String quoteNo = motDataList.getQuoteNo();

				list = homeRepo.findAllByQuoteNoIn(quoteList);

				list.stream().filter(st -> st.getStatus().equalsIgnoreCase("P")).collect(Collectors.toList());

			} else if (req.getSearchBy().equalsIgnoreCase("POLICY_NO")) {

				List<HomePositionMaster> policyList = homeRepo.findAllByPolicyNo(req.getPolicyNo());

				List<String> quoteList = policyList.stream().map(quote -> quote.getQuoteNo())
						.collect(Collectors.toList());

				list = homeRepo.findAllByQuoteNoIn(quoteList);

				list.stream().filter(st -> st.getStatus().equalsIgnoreCase("P")).collect(Collectors.toList());

			}
			for (HomePositionMaster data : list) {
				PortfolioByRegNoRes res = new PortfolioByRegNoRes();
				res.setAdminLoginId(data.getAdminLoginId() == null ? "" : data.getAdminLoginId());
				res.setAdminRemarks(data.getAdminRemarks() == null ? "" : data.getAdminRemarks());
				res.setApplicationId(data.getApplicationId() == null ? "" : data.getApplicationId());
				res.setBranchCode(data.getBranchCode() == null ? "" : data.getBranchCode());
				res.setBranchName(data.getBranchName() == null ? "" : data.getBranchName());
				res.setBrokerBranchCode(data.getBrokerBranchCode() == null ? "" : data.getBrokerBranchCode());
				res.setBrokerBranchName(data.getBrokerBranchName() == null ? "" : data.getBrokerBranchName());
				res.setBrokerCode(data.getBrokerCode() == null ? "" : data.getBrokerCode());
				res.setBrokerName(data.getCustomerName() == null ? "" : data.getCustomerName());
				res.setLoginId(data.getLoginId() == null ? "" : data.getLoginId());
				res.setCreditNo(data.getCreditNo() == null ? "" : data.getCreditNo());
				res.setCurrency(data.getCurrency() == null ? "" : data.getCurrency());
				res.setCustomerName(data.getCustomerName() == null ? "" : data.getCustomerName());
				res.setDebitNoteNo(data.getDebitNoteNo() == null ? "" : data.getDebitNoteNo());
				res.setEndorsementRemarks(data.getEndorsementRemarks() == null ? "" : data.getEndorsementRemarks());
				res.setEndtStatus(data.getEndtStatus() == null ? "" : data.getEndtStatus());
				res.setExchangeRate(data.getExchangeRate() == null ? null : data.getExchangeRate());
				res.setOriginalPolicyNo(data.getOriginalPolicyNo() == null ? "" : data.getOriginalPolicyNo());
				res.setOverallPremiumLc(data.getOverallPremiumLc() == null ? null : data.getOverallPremiumLc());
				res.setOverallPremiumFc(data.getOverallPremiumFc() == null ? null : data.getOverallPremiumFc());
				res.setExpiryDate(data.getExpiryDate() == null ? null : data.getExpiryDate());
				res.setPolicyNo(data.getPolicyNo() == null ? "" : data.getPolicyNo());
				res.setQuoteCreatedDate(data.getQuoteCreatedDate() == null ? null : data.getQuoteCreatedDate());
				res.setProductId(data.getProductId() == null ? null : data.getProductId());
				res.setProductName(data.getProductName() == null ? "" : data.getProductName());
				res.setQuoteNo(data.getQuoteNo() == null ? "" : data.getQuoteNo());
				res.setRemarks(data.getRemarks() == null ? "" : data.getRemarks());
				res.setReferalRemarks(data.getReferalRemarks() == null ? "" : data.getReferalRemarks());
				res.setRequestReferenceNo(data.getRequestReferenceNo() == null ? "" : data.getRequestReferenceNo());
				res.setStatus(data.getStatus() == null ? "" : data.getStatus());
				res.setSubUserType(data.getSubUserType() == null ? "" : data.getStatus());
				res.setUserType(data.getUserType() == null ? "" : data.getUserType());
				res.setEntryDate(data.getEntryDate() == null ? null : data.getEntryDate());

				String statusDesc = StringUtils.isBlank(data.getStatus()) ? ""
						: "Y".equalsIgnoreCase(data.getStatus()) ? "Existing Quote"
								: "R".equalsIgnoreCase(data.getStatus()) ? "Quote Rejected"
										: "N".equalsIgnoreCase(data.getStatus()) ? "Quote Deactivated"
												: "D".equalsIgnoreCase(data.getStatus()) ? "Quote Deleted"
														: "RP".equalsIgnoreCase(data.getStatus()) ? "Refferral Pending"
																: "RA".equalsIgnoreCase(data.getStatus())
																		? "Refferral Approved"
																		: "RR".equalsIgnoreCase(data.getStatus())
																				? "Refferral Rejected"
																				: "RA".equalsIgnoreCase(
																						data.getStatus())
																								? "Refferral Request"
																								: "P".equalsIgnoreCase(
																										data.getStatus())
																												? "Policy Converted"
																												: "E".equalsIgnoreCase(
																														data.getStatus())
																																? "Endorsement"
																																: "";

				String endtStatusDesc = StringUtils.isBlank(data.getEndtStatus()) ? ""
						: "P".equalsIgnoreCase(data.getEndtStatus()) ? "Pending"
								: "C".equalsIgnoreCase(data.getEndtStatus()) ? "Completed" : "";

				res.setStatusDesc(statusDesc);
				res.setEndStatusDesc(endtStatusDesc);

				resList.add(res);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resList;
	}

	

}
