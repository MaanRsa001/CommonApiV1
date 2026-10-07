package com.maan.eway.master.service.impl;

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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.ExchangeMaster;
import com.maan.eway.master.req.ExchangeChangeStatusReq;
import com.maan.eway.master.req.ExchangeMasterGetReq;
import com.maan.eway.master.req.ExchangeMasterGetallReq;
import com.maan.eway.master.req.ExchangeMasterSaveReq;
import com.maan.eway.master.res.ExchangeMasterGetRes;
import com.maan.eway.master.service.ExchangeMasterService;
import com.maan.eway.repository.CurrencyMasterRepository;
import com.maan.eway.repository.ExchangeMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class ExchangeMasterServiceImpl implements ExchangeMasterService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ExchangeMasterRepository repo;

	@Autowired
	private CurrencyMasterRepository currencyrepo;
	
	Gson json = new Gson();

	private Logger log = LogManager.getLogger(ExchangeMasterServiceImpl.class);

	@Transactional
	@Override
	public List<String> validateInsertExchangeMaster(ExchangeMasterSaveReq req) {
		List<String> errorList = new ArrayList<String>();

		try {

			if (StringUtils.isBlank(req.getRemarks())) {
			//	errorList.add(new Error("03", "Remark", "Please Select Remark "));
				errorList.add("2032");
			} else if (req.getRemarks().length() > 100) {
			//	errorList.add(new Error("03", "Remark", "Please Enter Remark within 100 Characters"));
				errorList.add("2033");
			}
			if (StringUtils.isBlank(req.getCurrencyId())) {
			//	errorList.add(new Error("07", "CurrencyId", "Please Enter CurrencyId"));
				errorList.add("1297");
			}
			else if (req.getCurrencyId().length() > 20) {
			//	errorList.add(new Error("07", "CurrencyId", "Please Enter CurrencyId within 20 Characters"));
				errorList.add("1298");
			}

			ExchangeMaster currencyId =   getCurrencyNameRes(req.getCurrencyId(),req.getCompanyId());
			
			if(StringUtils.isBlank(req.getExchangeId()) &&  currencyId !=null ) {
			//	errorList.add(new Error("08", "Currency", "This Currency Id Already Exist"));
				errorList.add("1299");
			} 
			else if( currencyId !=null  && StringUtils.isNotBlank(req.getExchangeId()) ) 
			{
				if(! currencyId.getCurrencyId().equalsIgnoreCase(req.getCurrencyId()) ) {
				//	errorList.add(new Error("08", "Currency", "This Currency Id Already Exist"));	
					errorList.add("1299");
			}	
			}
				// Date Validation
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				//	errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start"));
					errorList.add("2034");

				} else if (req.getEffectiveDateStart().before(today)) {
				//	errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
					errorList.add("2035");
				}
				// Status Validation
				if (StringUtils.isBlank(req.getStatus())) {
				//	errorList.add(new Error("05", "Status", "Please Enter Status"));
					errorList.add("2036");
				} else if (req.getStatus().length() > 1) {
				//	errorList.add(new Error("05", "Status", "Enter Status in One Character Only"));
					errorList.add("2037");
				} else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				//	errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
					errorList.add("2038");
				}
				if (StringUtils.isBlank(req.getExchangeRate())) {
				//	errorList.add(new Error("06", "ExchangeRate", "Please Enter ExchangeRate"));
					errorList.add("1300");
				}

				if (StringUtils.isBlank(req.getCompanyId())) {
				//	errorList.add(new Error("08", "CompanyId", "Please Enter CompanyId"));
					errorList.add("2101");
				}
				else if (StringUtils.isBlank(req.getCoreAppCode())) {
				//	errorList.add(new Error("02", "CoreAppCode", "Please Enter getCoreAppCode"));
					errorList.add("2124");
				} else if (req.getCoreAppCode().length() > 20) {
				//	errorList.add(new Error("02", "CoreAppCode", "getCoreAppCode under 20 Characters only allowed"));
					errorList.add("2125");
				}else if (req.getCoreAppCode().equalsIgnoreCase("99999")||   StringUtils.isBlank(req.getExchangeId())||req.getExchangeId()==null) {
					List<ExchangeMaster> CompanyList = getCoreAppCodeExistDetails(req.getCoreAppCode() , req.getEffectiveDateStart() , req.getEffectiveDateEnd());
					if (CompanyList.size()>0 ) {
					//	errorList.add(new Error("02", "Core App Code", "This Core App Code Already Exist "));
						errorList.add("1301");
					}
				}else  {
					List<ExchangeMaster> CompanyList =  getCoreAppCodeExistDetails(req.getCoreAppCode()  , req.getEffectiveDateStart() , req.getEffectiveDateEnd());
					if (CompanyList.size()>0 &&  (! req.getExchangeId().equalsIgnoreCase(CompanyList.get(0).getExchangeId().toString())) ) {
					//	errorList.add(new Error("02", "Core App Code", "This Core App Code Already Exist "));
						errorList.add("1301");
					}

				}

			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	private List<ExchangeMaster> getCoreAppCodeExistDetails(String coreAppCode , Date effStartDate , Date effEndDate) {
		List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
		try {
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(effStartDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			effStartDate   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			effEndDate = cal.getTime() ;

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

			// Find All
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.equal(ocpm1.get("coreAppCode"), b.get("coreAppCode"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), effStartDate );
			effectiveDate.where(a1,a2,a3);


			// Effective Date Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ExchangeMaster> ocpm2 = effectiveDate2.from(ExchangeMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(ocpm2.get("exchangeId"), b.get("exchangeId"));
			Predicate a5 = cb.equal(ocpm2.get("coreAppCode"), b.get("coreAppCode"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), effEndDate );
			effectiveDate2.where(a4,a5,a6);

			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n3 = cb.equal(b.get("coreAppCode"), coreAppCode );	
			//		Predicate n4 = cb.equal(b.get("exchangeId"), exchangeId);
			query.where(n1,n2,n3);
			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();		

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	@Transactional
	@Override
	public SuccessRes insertExchangeMaster(ExchangeMasterSaveReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		ExchangeMaster saveData = new ExchangeMaster();
		List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			 List<CurrencyMaster>   currencyList = getByCurrencyId(req.getCompanyId() ,req.getCurrencyId() );
			String  currencyname =currencyList.size()>0  ? currencyList.get(0).getCurrencyName() : "";// currencyrepo.findByCurrencyId(req.getCurrencyId());
			String  currencyNameLocal = currencyList.size()>0 ? currencyList.get(0).getCurrencyNameLocal(): "";
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
				Integer exchangeId = 0;
			if (StringUtils.isBlank(req.getExchangeId()) || req.getExchangeId()==null) {
				// Save
				Integer totalCount = getMasterTableCount( req.getCompanyId());
				exchangeId =  totalCount+1 ;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(exchangeId.toString());
			} else {
				// Update
				exchangeId = Integer.valueOf(req.getExchangeId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
				// Find all
				Root<ExchangeMaster> b = query.from(ExchangeMaster.class);
				//Select 
				query.select(b);
//				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				Predicate n2 = cb.equal(b.get("exchangeId"), req.getExchangeId());
				Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId());
				
				query.where(n2,n3).orderBy(orderList);
				
				// Get Result
				TypedQuery<ExchangeMaster> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();
				if (list.size() > 0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
					
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						ExchangeMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							ExchangeMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(exchangeId.toString());
			}
			dozerMapper.map(req, saveData);
			saveData.setExchangeId(exchangeId);
			saveData.setEffectiveDateStart(req.getEffectiveDateStart());
			saveData.setEffectiveDateEnd(endDate);
			saveData.setStatus(req.getStatus());
			saveData.setEntryDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setSNo(exchangeId);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCreatedBy(createdBy);
			saveData.setCurrencyName(currencyname);
			saveData.setCurrencyNameLocal(req.getCodeDescLocal());
			repo.saveAndFlush(saveData);
			log.info("Saved Details is --> " + json.toJson(saveData));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	public List<CurrencyMaster>  getByCurrencyId(String companyId , String currencyId) {
		List<CurrencyMaster> list = new ArrayList<CurrencyMaster>();
		ModelMapper mapper = new ModelMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CurrencyMaster> query = cb.createQuery(CurrencyMaster.class);
		
			
			// Find All
			Root<CurrencyMaster>    c = query.from(CurrencyMaster.class);		
			
			// Select
			query.select(c );
			
			// amendId Max Filter
			Subquery<Long>amendId = query.subquery(Long.class);
			Root<CurrencyMaster> ocpm1 = amendId.from(CurrencyMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("currencyId"),ocpm1.get("currencyId") );
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));

			amendId.where(a1,a2);
			
		
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("companyId")));
			
		    // Where	
		
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("amendId"), amendId);		
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("currencyId"),currencyId) ;
			Predicate n3 = cb.equal(c.get("companyId"), companyId);
			Predicate n4 = cb.equal(c.get("companyId"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<CurrencyMaster> result = em.createQuery(query);			
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getCurrencyId()))).collect(Collectors.toList());
			
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	public Integer getMasterTableCount(String companyId ) {
		Integer data = 0;
		try {
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
			// Find all
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);
			// Select
			query.select(b);
			//Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			effectiveDate.where(a1,a2);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("exchangeId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n4 = cb.or(n2,n3);
			query.where(n1,n2,n4).orderBy(orderList);
			
			// Get Result
		TypedQuery<ExchangeMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ?  list.get(0).getExchangeId() : 0 ;
	} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}

	@Override
	public ExchangeMasterGetRes getExchangeMaster(ExchangeMasterGetReq req) {
		ExchangeMasterGetRes res = new ExchangeMasterGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

			// Find All
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);

			// Select
			query.select(b);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = amendId.from(ExchangeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));

			amendId.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("companyId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n4 = cb.equal(b.get("exchangeId"), req.getExchangeId());
			Predicate n6 = cb.equal(b.get("companyId"), "99999");
			Predicate n7 = cb.or(n2,n6);
			query.where(n1,n4,n7).orderBy(orderList);

			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getExchangeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ExchangeMaster :: getExchangeRate ));
			res = mapper.map(list.get(0), ExchangeMasterGetRes.class);
			res.setExchangeId(list.get(0).getExchangeId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setCoreAppCode(list.get(0).getCoreAppCode());
			res.setCodeDescLocal(list.get(0).getCurrencyNameLocal());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	@Override
	public List<ExchangeMasterGetRes> getallExchangeMaster(ExchangeMasterGetallReq req) {
		List<ExchangeMasterGetRes> resList = new ArrayList<ExchangeMasterGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

			// Find All
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);

			// Select
			query.select(b);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = amendId.from(ExchangeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));

			amendId.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("companyId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n5 = cb.or(n3,n2);
			query.where(n1,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getExchangeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ExchangeMaster :: getExchangeId ));
			// Map
			for (ExchangeMaster data : list) {
				ExchangeMasterGetRes res = new ExchangeMasterGetRes();

				res = mapper.map(data, ExchangeMasterGetRes.class);
				res.setExchangeId(data.getExchangeId().toString());
				res.setCompanyId(data.getCompanyId());
				res.setCurrencyId(data.getCurrencyId());
				res.setExchangeRate(data.getExchangeRate().toString());
				res.setCodeDescLocal(data.getCurrencyNameLocal());;
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}

	@Override
	public List<ExchangeMasterGetRes> getActiveExchange(ExchangeMasterGetallReq req) {
		List<ExchangeMasterGetRes> resList = new ArrayList<ExchangeMasterGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

			// Find All
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);

			// Select
			query.select(b);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = amendId.from(ExchangeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));

			amendId.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("companyId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("companyId"), "99999");
			Predicate n5 = cb.or(n3,n2);
			Predicate n4 = cb.equal(b.get("status"), "Y");

			query.where(n1,n5,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getExchangeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ExchangeMaster :: getExchangeId ));
			// Map
			for (ExchangeMaster data : list) {
				ExchangeMasterGetRes res = new ExchangeMasterGetRes();

				res = mapper.map(data, ExchangeMasterGetRes.class);
				res.setExchangeId(data.getExchangeId().toString());
				res.setCompanyId(data.getCompanyId());
				res.setCurrencyId(data.getCurrencyId());
				res.setExchangeRate(data.getExchangeRate().toString());
				
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}
/*
	@Override
	public List<DropDownRes> getExchangeMasterDropdown() {
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
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Find All
			Root<ExchangeMaster> c = query.from(ExchangeMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("exchangeId")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("exchangeId"),ocpm1.get("exchangeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm2 = effectiveDate2.from(ExchangeMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("exchangeId"),ocpm2.get("exchangeId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			query.where(n1,n2,n3).orderBy(orderList);
	// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (ExchangeMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getExchangeId().toString());
				res.setCodeDesc(data.getCurrencyId());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details -->" + e.getMessage());
			return null;
		}
		return resList;
	}
*/
	@Override
	public SuccessRes changeStatusOfExchange(ExchangeChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
			// Find all
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);
			// Select
			query.select(b);
			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = amendId.from(ExchangeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			
			amendId.where(a1, a2);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("companyId")));
			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("exchangeId"), Integer.valueOf(req.getExchangeId()));
			Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n4 = cb.equal(b.get("companyId"), "99999");
			Predicate n5 = cb.or(n3,n4);
			
			query.where(n1,n2,n5).orderBy(orderList);
			
			// Get Result 
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();
			ExchangeMaster updateRecord = list.get(0);
			if(  req.getCompanyId().equalsIgnoreCase(updateRecord.getCompanyId())) {
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else {
				ExchangeMaster saveNew = new ExchangeMaster();
				dozerMapper.map(updateRecord,saveNew);
				saveNew.setCompanyId(req.getCompanyId());
				saveNew.setStatus(req.getStatus());
				repo.save(saveNew);
			}
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getExchangeId());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details -->" + e.getMessage());
			return null;
		}
		return res;
	}
	@Override
	public List<DropDownRes> getExchangeMasterDropdown() {
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
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Find All
			Root<ExchangeMaster> c = query.from(ExchangeMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("exchangeId")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("exchangeId"),ocpm1.get("exchangeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ExchangeMaster> ocpm2 = effectiveDate2.from(ExchangeMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("exchangeId"),ocpm2.get("exchangeId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"N");
			Predicate n8 = cb.equal(c.get("status"),"R");
			Predicate n9 = cb.or(n1,n8);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			query.where(n9,n2,n3).orderBy(orderList);
			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (ExchangeMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getExchangeId().toString());
				res.setCodeDesc(data.getCurrencyId());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details -->" + e.getMessage());
			return null;
		}
		return resList;
	}
	

public ExchangeMaster getCurrencyNameRes(String currencyId, String companyId) {
	ExchangeMaster currencyRes =null;
	try {
		Date today = new Date();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

		// Find All
		Root<ExchangeMaster> s = query.from(ExchangeMaster.class);
		
		// State Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate c1 = cb.equal(ocpm1.get("currencyId"), s.get("currencyId"));
		Predicate c2 = cb.equal(ocpm1.get("status"),s.get("status"));
		Predicate c3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate c5 = cb.equal(ocpm1.get("companyId"),s.get("companyId"));
		
		effectiveDate.where(c1,c2,c3,c5);
		
		Predicate n1 = cb.equal(s.get("effectiveDateStart"), effectiveDate);
		Predicate n2 = cb.equal(s.get("currencyId"), currencyId);
		Predicate n3 = cb.equal(s.get("status"), "Y");
		Predicate n4 = cb.equal(s.get("companyId"), companyId);
		
		// Select
		query.select( s );
		
		query.where(n1,n2,n3,n4);
		// Get Result
		TypedQuery<ExchangeMaster> result = em.createQuery(query);
		List<ExchangeMaster> list = result.getResultList();
		if( list.size()>0) {
			currencyRes = list.get(0);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
		return null;
	}
	return currencyRes;
}
}
