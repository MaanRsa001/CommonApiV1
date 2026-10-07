package com.maan.eway.ticket;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.PreinspectionUploadDetails;
import com.maan.eway.bean.PremiaTransactionLog;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.repository.PremiaTransactionLogRepository;

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
public class CustomerJobTrackServiceImpl implements CustomerJobTrackerService{
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private TicketJobExecutionTrackerRepository trackerRepo;
	
	@Autowired
	private PremiaTransactionLogRepository logRepo;
	
	@Autowired
	private CustomerJobTrackerRepository cusJobRepo;

	@Override
	public CommonRes customerTrackingDashboad(CustomerTrackDashboadReq req) {
		CommonRes res = new CommonRes();
		List<CustomerJobTracker> listJob = new ArrayList<>();
		try {
			List<Map<String,Object>> lictRes = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date entryDate1 = sdf.parse(req.getSearchDate());
			Date entryDate2 = sdf.parse(req.getSearchDate());
			Calendar cal = new GregorianCalendar();
			cal.setTime(entryDate1);
			cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime() ;
			cal.setTime(entryDate2);
			cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
			Date endDate = cal.getTime() ;
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<CustomerJobTracker> h = query.from(CustomerJobTracker.class);
			
			query.multiselect(cb.count(h).alias("count"),h.get("currentStatus").alias("currentStatus"));
			
			//List<Order> orderList = new ArrayList<Order>();
			//orderList.add(cb.asc(h.get("requestReferenceNo")));
			
			Predicate n1 = cb.equal(h.get("companyId"), req.getCompanyId());
			Predicate n2=cb.between(h.get("entryDate"), startDate, endDate);
			
			query.where(n1,n2).groupBy(h.get("currentStatus"));
			TypedQuery<Tuple> result = em.createQuery(query);
			List<Tuple> tub= result.getResultList();
			if(!tub.isEmpty()) {
			for(Tuple tuble : tub) {
				Map<String,Object> mapRes = new HashMap<>();
				
				mapRes.put("Count", tuble.get("count"));
				mapRes.put("StatusCode", tuble.get("currentStatus"));
				
				lictRes.add(mapRes);
			}
			res.setCommonResponse(lictRes);
			res.setIsError(false);
			res.setMessage("Success");
			}else {
				res.setCommonResponse(null);
				res.setIsError(false);
				res.setMessage("Success");
			}
		}catch(Exception e) {
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setMessage("Failed");
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public CommonRes customerDashboad(CustomerTrackDashboadReq req) {
		CommonRes res = new CommonRes();
		List<CustomerJobTracker> listcus = new ArrayList<>();
		List<CustomerTrackDashboadRes> resList = new ArrayList<>();
		try {
			List<Map<String,Object>> lictRes = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date entryDate1 = sdf.parse(req.getSearchDate());
			Date entryDate2 = sdf.parse(req.getSearchDate());
			Calendar cal = new GregorianCalendar();
			cal.setTime(entryDate1);
			cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime() ;
			cal.setTime(entryDate2);
			cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
			Date endDate = cal.getTime() ;
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CustomerJobTracker> query = cb.createQuery(CustomerJobTracker.class);

			// Find All
			Root<CustomerJobTracker> h = query.from(CustomerJobTracker.class);
			
           query.select(h);
			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("requestReferenceNo")));
			
			Predicate n1 = cb.equal(h.get("companyId"), req.getCompanyId());
			Predicate n2=cb.between(h.get("entryDate"), startDate, endDate);
			Predicate n3 = cb.equal(h.get("currentStatus"), req.getStatusCode());
			
			query.where(n1,n2,n3).orderBy(orderList);
			
			TypedQuery<CustomerJobTracker> result = em.createQuery(query);
			listcus = result.getResultList();
			
			for(CustomerJobTracker li : listcus) {
				CustomerTrackDashboadRes setRes = new CustomerTrackDashboadRes();
				setRes.setRequestReferenceNo(li.getRequestReferenceNo() == null ? null : li.getRequestReferenceNo());
				setRes.setCompanyId(li.getCompanyId() == null ? null : li.getCompanyId());
				setRes.setQuoteNo(li.getQuoteNo() == null ? null : li.getQuoteNo());
				setRes.setCustomerName(li.getClientName() == null ? null : li.getClientName());
				setRes.setBranchCode(li.getBranchCode() == null ? null : li.getBranchCode());
				setRes.setBranchName(li.getBranchName() == null ? null : li.getBranchName());
				setRes.setProductId(li.getProductId() == null ? null : li.getProductId());
				setRes.setProductName(li.getProductName() == null ? null : li.getProductName());
				setRes.setSectionId(li.getSectionId() == null ? null : li.getSectionId());
				setRes.setSectionName(li.getSectionName() == null ? null : li.getSectionName());
				setRes.setEntryDate(li.getEntryDate() == null ? null : sdf.format(li.getEntryDate()));
				setRes.setEmail(li.getEmail1() == null ? null : li.getEmail1());
				setRes.setMobileNo(li.getMobileNo() == null ? null : li.getMobileNo());
				setRes.setCurrentStatus(li.getCurrentStatus() == null ? null : li.getCurrentStatus());
				setRes.setRemarks(li.getRemarks() == null ? null : li.getRemarks());
				setRes.setSourceType(li.getSourceType() == null ? null : li.getSourceType());
				setRes.setSubUserType(li.getSubUserType() == null ? null : li.getSubUserType());
				setRes.setLoginId(li.getLoginId() == null ? null : li.getLoginId());
				
				resList.add(setRes);
			}
			res.setCommonResponse(resList);
			res.setIsError(false);
			res.setMessage("Success");
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setIsError(true);
			res.setMessage("Failed");
		}
		return res;
	}

	@Transactional
	public void updateTrackerTime(LocalDateTime currentTime) {
		
		System.out.println("CUSTOMERDROPOFF TIME FETCH");
		TicketJobExecutionTracker tracker = trackerRepo.findByJobName("CUSTOMERDROPOFF");
		if(tracker == null){
			System.out.println("Tracker not found in DB");
		}
		System.out.println("CUSTOMERDROPOFF VALUES "+ tracker);
		tracker.setLastRunTime(currentTime);
        tracker.setUpdatedAt(tracker.getLastRunTime());
        System.out.println("CUSTOMERDROPOFF CURRENT TIME SET "+ tracker);
        trackerRepo.saveAndFlush(tracker);	
		
	}

	@Transactional
	public void saveTransactionLog(PremiaTransactionLog tranLog) {
		System.out.println("Saving log object: " + tranLog);

	    logRepo.saveAndFlush(tranLog);

	    System.out.println("Saved successfully");
	}

	@Override
	public CommonRes customerStatusUpdate(CustomerTrackDashboadReq req) {
		CommonRes res = new CommonRes();
		try {
			CustomerJobTracker cusDet = cusJobRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			
			if(cusDet != null) {
				cusDet.setAdminRemarks(req.getAdminRemarks() == null ? null : req.getAdminRemarks());
				cusDet.setAdminStatus(req.getAdminStatus() == null ? null : req.getAdminStatus());
				cusDet.setUpdateDate(new Date());
				cusDet.setUpdatedBy(req.getUpdatedBy() == null ? null : req.getUpdatedBy());
				
				cusJobRepo.saveAndFlush(cusDet);
				
				res.setCommonResponse("Update Successfully");
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
				res.setMessage("Success");
			}else {
				res.setCommonResponse("Update Not Happened");
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
				res.setMessage("Success");
			}
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
			res.setIsError(true);
			//res.setErrorMessage(Collections.emptyList());
			res.setMessage(e.getMessage());
		}
		return res;
	}

}
