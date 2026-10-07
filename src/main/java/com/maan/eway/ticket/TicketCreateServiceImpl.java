package com.maan.eway.ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.PremiaTransactionLog;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PremiaTransactionLogRepository;
import com.maan.eway.thread.TicketCreateService;

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
public class TicketCreateServiceImpl implements TicketCreateService{
	
	private Logger log = LogManager.getLogger(TicketCreateServiceImpl.class);

	@Value("${organisationIdforTicket}") 
    private String organisationId;
	
	@Value("${categoryIdforTicket}") 
    private String categoryId;
	
	@Value("${urlforTicket}") 
    private String url;
	
	@Value("${dropoffCategoryIdforTicket}") 
    private String dropOffcategoryId;
	
	@Value("${ticketCreation.boolean}") 
    private boolean ticketFlagCheck;
	
	@Autowired
	private TicketJobExecutionTrackerRepository trackerRepo;
	
	@Autowired
	private PremiaTransactionLogRepository logRepo;
	
	@Autowired
	private PersonalInfoRepository personalInfoRepo;
	
	@Autowired
	private CustomerJobTrackerRepository jobTrackRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private FactorRateRequestDetailsRepository facRateRepo ;
	
	@Autowired
	private CustomerJobTrackServiceImpl jobTrackerservice;
	
	@Override
	public CommonRes createTicket(TicketCreateReq req) {
		CommonRes res = new CommonRes();
		ResponseEntity<String> response = null;
		PremiaTransactionLog tranLog = new PremiaTransactionLog();
		try {
			Map<String,Object> requestMap = new HashMap<>();
			
			requestMap.put("subject", "Payment Failed");
			requestMap.put("description", "Payment transaction failed during product purchase in the portal. The payment gateway did not return a successful response, resulting in an incomplete order. Admin team to investigate and confirm transaction status.");
			requestMap.put("contact_name", req.getCustomerName());
			requestMap.put("contact_email", req.getCustomerMail());
			requestMap.put("contact_mobile", req.getCustomerMobile());
			requestMap.put("organization_id", organisationId);
			requestMap.put("category_id", categoryId);
			
			tranLog.setRequestTime(LocalDateTime.now());
            tranLog.setEntryDate(new Date());
            tranLog.setEndpoint(url);
            tranLog.setGenerateReq("PaymentFailed");
            tranLog.setQuoteNo(req.getCustomerMobile());
			
			ObjectMapper mapper = new ObjectMapper();
			String ticketRequest = mapper.writeValueAsString(requestMap);
			System.out.println("Create Ticket Request: " + ticketRequest);
			RestTemplate restTemp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<?> requestent = new HttpEntity<>(ticketRequest, header);
			
			tranLog.setRequest(ticketRequest);

			 response = restTemp.exchange(url, HttpMethod.POST, requestent, String.class);

			System.out.println("Create Ticket Response:  " + response.getBody());
			Map<String,Object> resp = null;
			if(response.getBody() != null && !response.getBody().isEmpty()) {
				resp = mapper.readValue(response.getBody(), Map.class);
			}
			tranLog.setResponseTime(LocalDateTime.now());
			tranLog.setResponse(response.getBody());
			tranLog.setStatus(response.getBody()!=null ?"Y":"F");
			 
			res.setCommonResponse(resp);
			res.setIsError(false);
		}catch(Exception e) {
			tranLog.setResponseTime(LocalDateTime.now());
			tranLog.setStatus("F");
			tranLog.setErrorMessage(e.getLocalizedMessage());
			e.printStackTrace();
			res.setCommonResponse(response.getBody());
			res.setIsError(true);	
		}finally {
			logRepo.saveAndFlush(tranLog);
		}
		
		return res;
	}

	@Override
	public CommonRes createDropOffTicket(TicketCreateReq req) {
		CommonRes res = new CommonRes();
		ResponseEntity<String> response = null;
		PremiaTransactionLog tranLog = new PremiaTransactionLog();
		try {
            Map<String,Object> requestMap = new HashMap<>();
            
			requestMap.put("subject", "Customer DropOff");
			requestMap.put("description", "The customer completed quote creation but exited the flow without completing policy purchase. As payment was not done, the policy was not issued. Please review and assist with the next steps.");
			requestMap.put("contact_name", req.getCustomerName());
			requestMap.put("contact_email", req.getCustomerMail());
			requestMap.put("contact_mobile", req.getCustomerMobile());
			requestMap.put("organization_id", organisationId);
			requestMap.put("category_id", dropOffcategoryId);
			
			ObjectMapper mapper = new ObjectMapper();
			String ticketRequest = mapper.writeValueAsString(requestMap);
			System.out.println("Create Ticket Request: " + ticketRequest);
			RestTemplate restTemp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<?> requestent = new HttpEntity<>(ticketRequest, header);
			
			
            tranLog.setRequestTime(LocalDateTime.now());
            tranLog.setEntryDate(new Date());
            tranLog.setEndpoint(url);
            tranLog.setGenerateReq("CustomerDropoff");
            tranLog.setRequest(ticketRequest);
            tranLog.setQuoteNo(req.getCustomerMobile());

			response = restTemp.exchange(url, HttpMethod.POST, requestent, String.class);
			 
			 tranLog.setResponseTime(LocalDateTime.now());
			 tranLog.setResponse(response.getBody());
			 tranLog.setStatus(response.getBody()!=null ?"Y":"F");

			System.out.println("Create Ticket Response:  " + response.getBody());
			Map<String,Object> resp = null;
			if(response.getBody() != null && !response.getBody().isEmpty()) {
				resp = mapper.readValue(response.getBody(), Map.class);
			}
			
			res.setCommonResponse(resp);
			res.setIsError(false);
			
		}catch(Exception e) {
			tranLog.setResponseTime(LocalDateTime.now());
			tranLog.setStatus("F");
			tranLog.setErrorMessage(e.getLocalizedMessage());
			e.printStackTrace();
			res.setCommonResponse(response.getBody());
			res.setIsError(true);
			
		}finally {
			logRepo.saveAndFlush(tranLog);
		}
		return res;
	}
	
	@Scheduled(cron = "0 0 */1 * * ?")
	//@Scheduled(cron = "0 */2 * * * ?")
	public void dropOffCustomer() {
		try {
			
			boolean processed = true;

			LocalDateTime currentTime = LocalDateTime.now();
			
			LocalDateTime lastRunTime = getLastRunTime("CUSTOMERDROPOFF");
			System.out.println("UPDATE TRACKER TIME INSIDE");
			jobTrackerservice.updateTrackerTime(currentTime);
			if(lastRunTime != null) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CustomerJobTracker> query = cb.createQuery(CustomerJobTracker.class);
				List<CustomerJobTracker> list = new ArrayList<>();
				Root<CustomerJobTracker> h = query.from(CustomerJobTracker.class);
				query.select(h);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(h.get("entryDate")));
				
				
				Predicate a1 = cb.lessThanOrEqualTo(h.get("entryDate"), currentTime);
				Predicate a2 = cb.greaterThan(h.get("entryDate"), lastRunTime);
			//	Predicate a3 = cb.isNull(h.get("policyNo"));
				Predicate a4 = cb.equal(h.get("companyId"), "100002");
				Predicate a5 = cb.equal(h.get("currentStatus"), "QTP");
				Predicate a6 = cb.equal(h.get("currentStatus"), "PYP");
				Predicate a7 = cb.equal(h.get("currentStatus"), "PYPE");
				Predicate a8 = cb.or(a5,a6,a7);
				
				query.where(a1, a2, a4, a8).orderBy(orderList);
				
				TypedQuery<CustomerJobTracker> result = em.createQuery(query);
				list = result.getResultList();
				System.out.println("Fetching DropOff Customer");
				processed = true;
				if(ticketFlagCheck && !list.isEmpty()) {
					//if(!list.isEmpty()) {
						System.out.println("Customer List:" +list);
						
						for(CustomerJobTracker l : list) {
							Map<String,Object> requestMap = new HashMap<>();
							
							PremiaTransactionLog tranLog = new PremiaTransactionLog();
							ResponseEntity<String> response = null;
						//	PersonalInfo personal = personalInfoRepo.findByCustomerId(l.getCustomerId());
						//	if(personal != null) {
								
								try {
									requestMap.put("subject", "Customer DropOff");
									requestMap.put("description", "The customer completed quote creation with the Quote Number "+l.getQuoteNo()+". But exited the flow without completing policy purchase. As payment was not done, the policy was not issued. Please review and assist with the next steps.");
									requestMap.put("contact_name", l.getClientName());
									requestMap.put("contact_email", l.getEmail1());
									requestMap.put("contact_mobile", l.getMobileNo());
									requestMap.put("organization_id", organisationId);
									requestMap.put("category_id", dropOffcategoryId);	
									
									ObjectMapper mapper = new ObjectMapper();
									String ticketRequest = mapper.writeValueAsString(requestMap);
									System.out.println("Create Ticket Request: " + ticketRequest);
									
									SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			                        factory.setConnectTimeout(15000);
			                        factory.setReadTimeout(15000);
			                        
									RestTemplate restTemp = new RestTemplate(factory);
									HttpHeaders header = new HttpHeaders();
									header.setContentType(MediaType.APPLICATION_JSON);
									HttpEntity<?> requestent = new HttpEntity<>(ticketRequest, header);
									
									
						            tranLog.setRequestTime(LocalDateTime.now());
						            tranLog.setEntryDate(new Date());
						            tranLog.setEndpoint(url);
						            tranLog.setGenerateReq("CustomerDropoff");
						            tranLog.setRequest(ticketRequest);
						            tranLog.setQuoteNo(l.getQuoteNo());

									response = restTemp.exchange(url, HttpMethod.POST, requestent, String.class);
									 
									 tranLog.setResponseTime(LocalDateTime.now());
									 tranLog.setResponse(response.getBody());
									 tranLog.setStatus(response.getBody()!=null ?"Y":"F");

									System.out.println("Create Ticket Response:  " + response.getBody());
								}catch(Exception e) {
									tranLog.setResponseTime(LocalDateTime.now());
									tranLog.setStatus("F");
									tranLog.setErrorMessage(e.getLocalizedMessage());
									e.printStackTrace();
								}finally {
									//logRepo.saveAndFlush(tranLog);
									jobTrackerservice.saveTransactionLog(tranLog);
								}	
						//	}
						}
					
				}				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public LocalDateTime getLastRunTime(String type) {
			try {
				LocalDateTime time = null;
				TicketJobExecutionTracker tracker = trackerRepo.findByJobName(type);
				if(tracker != null) {
				 time = tracker.getLastRunTime();	
				 System.out.println("CUSTOMERDROPOFF LASTRUN TIME "+ time);
				}
				return time;
			}catch(Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	@Transactional
	@Override
	public CommonRes customerJobTracking(CustomerTrackerReq req) {
		CommonRes res = new CommonRes();
		try {
			log.info("SAVE CUSTOMER TRACKING METHOD START" +  new Date());
		//	CustomerJobTracker trackDetail = jobTrackRepo.findByRequestReferenceNo(req.getRequestreferenceno());
			
		//	if(trackDetail != null) {
		//		jobTrackRepo.delete(trackDetail);
		//		jobTrackRepo.flush(); 
		//	}
				CustomerJobTracker saveTrack = new CustomerJobTracker();
				saveTrack.setRequestReferenceNo(req.getRequestreferenceno());
				saveTrack.setCompanyId(req.getCompanyId());
				saveTrack.setQuoteNo(req.getQuoteno() == null ? null : req.getQuoteno());
				saveTrack.setClientName(req.getCustomerName() == null ? null : req.getCustomerName());
				saveTrack.setBranchCode(req.getBranchCode() == null ? null : req.getBranchCode());
				saveTrack.setBranchName(req.getBranchName() == null ? null : req.getBranchName());
				saveTrack.setProductId(req.getProductId() == null ? null : req.getProductId());
				saveTrack.setProductName(req.getProductName() == null ? null : req.getProductName());
				saveTrack.setSectionId(req.getSectionId() == null ? null : req.getSectionId());
				saveTrack.setSectionName(req.getSectionName() == null ? null : req.getSectionName());
				saveTrack.setEntryDate(new Date());
				saveTrack.setEmail1(req.getEmail() == null ? null : req.getEmail());
				saveTrack.setMobileNo(req.getMobileNo() == null ? null : req.getMobileNo());
				//saveTrack.setRemarks(req.getRemarks() == null ? null : req.getRemarks());
				saveTrack.setSourceType(req.getSourceType() == null ? null : req.getSourceType());
				saveTrack.setSubUserType(req.getSubUserType() == null ? null : req.getSubUserType());
				saveTrack.setLoginId(req.getLoginId() == null ? null : req.getLoginId());
				
				if("PREMIUM_PAGE".equalsIgnoreCase(req.getCurrentStatus())) {
					saveTrack.setCurrentStatus("PRP");
					saveTrack.setRemarks("Customer in Premium Page");
				}else if("QUOTATION_PAGE".equalsIgnoreCase(req.getCurrentStatus())) {
					saveTrack.setCurrentStatus("QTP");
					saveTrack.setRemarks("Customer in Quotation Page");
				}else if("DOCUMENT_PAGE".equalsIgnoreCase(req.getCurrentStatus())) {
					saveTrack.setCurrentStatus("DMP");
					saveTrack.setRemarks("Customer in Document Page");
				}else if("PAYMENT_PAGE".equalsIgnoreCase(req.getCurrentStatus())) {
					saveTrack.setCurrentStatus("PYP");
					saveTrack.setRemarks("Customer in Payment Page");
				}else if("ACCEPTED".equalsIgnoreCase(req.getCurrentStatus())) {
					saveTrack.setCurrentStatus("PYS");
					saveTrack.setRemarks("Customer Complete the Payment");
				}else if("PENDING".equalsIgnoreCase(req.getCurrentStatus())) {
					saveTrack.setCurrentStatus("PYPE");
					saveTrack.setRemarks("Payment still Pending");
				}else if("FAILED".equalsIgnoreCase(req.getCurrentStatus())) {
					saveTrack.setCurrentStatus("PYF");
					saveTrack.setRemarks("Payment Failed");
				}
				jobTrackRepo.save(saveTrack);
				
				res.setCommonResponse("Tracking Details Updated");
				log.info("Tracking Customer Response --> " +  res.getCommonResponse());
				System.out.println("Tracking Customer Response :"+res.getCommonResponse());
				log.info("SAVE CUSTOMER TRACKING METHOD END" +  new Date());
				res.setIsError(false);
		}catch(Exception e) {
			e.printStackTrace();
			res.setIsError(true);
			log.info("Tracking Customer Response --> " +  e.getMessage());
			System.out.println("Tracking Customer Response :"+e.getMessage());
			res.setCommonResponse(null);
		}
		return res;
	}
	
	public void TrackCustomerStatus(String reguestRefNo, String quoteNo, String statusType) {
		try {
			
			log.info("ENTER TO THE CUSTOMER TRACK METHOD" +  new Date());
			List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(reguestRefNo);
			String companyId = covers.size() > 0 ? covers.get(0).getCompanyId()  :"" ;
			String productId = covers.size() > 0 ? covers.get(0).getProductId().toString()  :"" ;
			CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , productId);
			String productYN = product.getMotorYn();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			if("M".equalsIgnoreCase(productYN)) {
				Root<EserviceMotorDetails> emd = query.from(EserviceMotorDetails.class);
				Root<EserviceCustomerDetails> ecd = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(ecd.get("clientName").alias("clientName"),emd.get("branchCode").alias("branchCode"),
						emd.get("branchName").alias("branchName"),emd.get("companyId").alias("companyId"),
						emd.get("productId").alias("productId"),emd.get("productName").alias("productName"),
						emd.get("sectionId").alias("sectionId"),emd.get("sectionName").alias("sectionName"),
						emd.get("sourceType").alias("sourceType"),ecd.get("mobileNo1").alias("mobileNo1"),
						ecd.get("email1").alias("email1"),emd.get("subUserType").alias("subUserType"),
						emd.get("loginId").alias("loginId"));
				
				Predicate n1 = cb.equal(emd.get("requestReferenceNo"), reguestRefNo);
				Predicate n2 = cb.equal(emd.get("customerReferenceNo"), ecd.get("customerReferenceNo"));
				
				query.where(n1,n2);
			}else if("H".equalsIgnoreCase(productYN)) {
				if("4".equalsIgnoreCase(productId)) {
					Root<EserviceTravelDetails> emd = query.from(EserviceTravelDetails.class);
					Root<EserviceCustomerDetails> ecd = query.from(EserviceCustomerDetails.class);
					
					query.multiselect(ecd.get("clientName").alias("clientName"),emd.get("branchCode").alias("branchCode"),
							emd.get("branchName").alias("branchName"),emd.get("companyId").alias("companyId"),
							emd.get("productId").alias("productId"),emd.get("productName").alias("productName"),
							emd.get("sectionId").alias("sectionId"),emd.get("sectionName").alias("sectionName"),
							emd.get("sourceType").alias("sourceType"),ecd.get("mobileNo1").alias("mobileNo1"),
							ecd.get("email1").alias("email1"),emd.get("subUserType").alias("subUserType"),
							emd.get("loginId").alias("loginId"));
					
					Predicate n1 = cb.equal(emd.get("requestReferenceNo"), reguestRefNo);
					Predicate n2 = cb.equal(emd.get("customerReferenceNo"), ecd.get("customerReferenceNo"));
					
					query.where(n1,n2);
				}else {
					Root<EserviceCommonDetails> emd = query.from(EserviceCommonDetails.class);
					Root<EserviceCustomerDetails> ecd = query.from(EserviceCustomerDetails.class);
					
					query.multiselect(ecd.get("clientName").alias("clientName"),emd.get("branchCode").alias("branchCode"),
							emd.get("branchName").alias("branchName"),emd.get("companyId").alias("companyId"),
							emd.get("productId").alias("productId"),emd.get("productDesc").alias("productName"),
							emd.get("sectionId").alias("sectionId"),emd.get("sectionName").alias("sectionName"),
							emd.get("sourceType").alias("sourceType"),ecd.get("mobileNo1").alias("mobileNo1"),
							ecd.get("email1").alias("email1"),emd.get("subUserType").alias("subUserType"),
							emd.get("loginId").alias("loginId"));
					
					Predicate n1 = cb.equal(emd.get("requestReferenceNo"), reguestRefNo);
					Predicate n2 = cb.equal(emd.get("customerReferenceNo"), ecd.get("customerReferenceNo"));
					
					query.where(n1,n2);
				}
				
			}else if("A".equalsIgnoreCase(productYN)) {
				Root<EserviceBuildingDetails> emd = query.from(EserviceBuildingDetails.class);
				Root<EserviceCustomerDetails> ecd = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(ecd.get("clientName").alias("clientName"),emd.get("branchCode").alias("branchCode"),
						emd.get("branchName").alias("branchName"),emd.get("companyId").alias("companyId"),
						emd.get("productId").alias("productId"),emd.get("productDesc").alias("productName"),
						emd.get("sectionId").alias("sectionId"),emd.get("sectionDesc").alias("sectionName"),
						emd.get("sourceType").alias("sourceType"),ecd.get("mobileNo1").alias("mobileNo1"),
						ecd.get("email1").alias("email1"),emd.get("subUserType").alias("subUserType"),
						emd.get("loginId").alias("loginId"));
				
				Predicate n1 = cb.equal(emd.get("requestReferenceNo"), reguestRefNo);
				Predicate n2 = cb.equal(emd.get("customerReferenceNo"), ecd.get("customerReferenceNo"));
				
				query.where(n1,n2);
			}
			
			
			List<Tuple> tupleValue = em.createQuery(query).getResultList();
			
			Tuple tup = tupleValue.get(0);
			
			if(tup != null) {
				CustomerTrackerReq req = new CustomerTrackerReq();
				req.setRequestreferenceno(reguestRefNo);
				req.setQuoteno(quoteNo == null ? null : quoteNo);
				req.setCustomerName(tup.get("clientName") == null ? null : tup.get("clientName").toString());
				req.setBranchCode(tup.get("branchCode") == null ? null : tup.get("branchCode").toString());
				req.setBranchName(tup.get("branchName") == null ? null : tup.get("branchName").toString());
				req.setCompanyId(tup.get("companyId") == null ? null : tup.get("companyId").toString());
				req.setProductId(tup.get("productId") == null ? null : tup.get("productId").toString());
				req.setProductName(tup.get("productName") == null ? null : tup.get("productName").toString());
				req.setSectionId(tup.get("sectionId") == null ? null : tup.get("sectionId").toString());
				req.setSectionName(tup.get("sectionName") == null ? null : tup.get("sectionName").toString());
				req.setSourceType(tup.get("sourceType") == null ? null : tup.get("sourceType").toString());
				req.setMobileNo(tup.get("mobileNo1") == null ? null : tup.get("mobileNo1").toString());
				req.setEmail(tup.get("email1") == null ? null : tup.get("email1").toString());
				req.setSubUserType(tup.get("subUserType") == null ? null : tup.get("subUserType").toString());
				req.setLoginId(tup.get("loginId") == null ? null : tup.get("loginId").toString());
				req.setCurrentStatus(statusType);	
				
				customerJobTracking(req);
				log.info("Exit TO THE CUSTOMER TRACK METHOD" +  new Date());
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			log.info("EXCEPTION -> CUSTOMER TRACK METHOD" +  e.getMessage());
		}
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
			product = list.size() > 0 ? list.get(0) :null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}

}
