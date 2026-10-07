package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EngineerInfo;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceCustomerDetailsId;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MasterReferralDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.AutoInsertTermsReq;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.GetApproverListReq;
import com.maan.eway.common.req.IndividualReferalReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.GetApproverListRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.ProductThreadRes;
import com.maan.eway.common.res.QuoteThreadRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.service.GridService;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.error.Error;
import com.maan.eway.excelupload.repository.TemplateRepository;
import com.maan.eway.master.req.TrackingDetailsSaveReq;
import com.maan.eway.master.service.TrackingDetailsService;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.reinsurance.service.ReinsuranceService;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
import com.maan.eway.repository.EServiceDriverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EngineerInfoRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MasterReferralDetailsRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.MsDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.SectionCoverMasterRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;
import com.maan.eway.repository.UWReferralDetailsRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.req.calcengine.ReferralApi;
import com.maan.eway.res.ReferalResponse;
import com.maan.eway.res.calc.AdminReferral;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.thread.MyTaskList;
import com.maan.eway.ticket.TicketCreateServiceImpl;
import com.maan.eway.workflow.dto.WorkEngine;
import com.maan.eway.workflow.service.JsonMapperFromDB;
import com.maan.eway.workstream.entity.HierarchyManagement;
import com.maan.eway.workstream.repository.HierarchyManagementRepository;
import com.maan.eway.workstream.request.QuoteProposalSaveReq;
import com.maan.eway.workstream.serviceimpl.QuoteProposalWorkFlowEntryServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class QuoteThreadServiceImpl implements QuoteThreadService {

	
	private Logger log = LogManager.getLogger(QuoteThreadServiceImpl.class);
	

	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	Gson json = new Gson();
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EserviceCustomerDetailsRepository eserCustRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo ;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo ;
	
	@Autowired
	private FactorRateRequestDetailsRepository facRateRepo ;
	
	@Autowired
	private SeqCustidRepository custIdRepo ;
	
	@Autowired
	private SeqQuotenoRepository quoteNoRepo ;
	
	@Autowired
	private PersonalInfoRepository perInfoRepo ;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo ;
	
	@Autowired
	private CoverDetailsRepository coverRepo ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@Autowired
	private UwQuestionsDetailsRepository uwRepo ;
	
	@Autowired
	private EserviceTravelDetailsRepository eserRepo ;
	
	@Autowired
	private EserviceTravelGroupDetailsRepository eserGroupRepo ;
	
	@Autowired
	private TravelPassengerDetailsRepository traPassRepo  ;
	
	@Autowired
	private TravelPassengerHistoryRepository traPassHisRepo  ;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo  ;
	
	@Autowired
	private MasterReferralDetailsRepository masReferralRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;

	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	
	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private ProductMasterRepository productRepo;
	
	@Autowired
	private MotorDriverDetailsRepository driverRepo;
	
	@Autowired
	private SectionDataDetailsRepository secRepo ;
	 
	@Autowired
	private BuildingRiskDetailsRepository buildRepo ;
	
	@Autowired
	private DocumentTransactionDetailsRepository docRepo ;
	
	@Autowired
	private BuildingDetailsRepository locRepo ;
	
	@Autowired
	private ContentAndRiskRepository  contentRepo ;
	
	@Autowired
	private ProductEmployeesDetailsRepository pacRepo ; 
	
	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingRepo;
	
	@Autowired
	private TrackingDetailsService trackingService;
	
	@Autowired
	@Lazy
	private CalculatorEngine calcEngine;
	
	@Autowired
	private RatingFactorsUtil ratingutil;
	
	@Autowired
	private DocumentUniqueDetailsRepository docUniqueRepo ;
	
	@Autowired
	private DocumentTransactionDetailsRepository docTranRepo ;
	
	@Autowired
	private GridService gridService;
	
	@Autowired
	private UWReferralDetailsRepository uwReferralRepo;
	
	@Autowired
	private MsDriverDetailsRepository msDriverRepo;
	
	@Autowired
	private NotificationThreadServiceImpl notiThreadService;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ;
	
	@Autowired
	private EServiceDriverDetailsRepository eservicedriverRepo;
	
	@Autowired
	private EServiceMotorDetailsRepository eserviceMotorDetailsRepo;
	
	
	@Autowired
	private TicketCreateServiceImpl ticketserviceImpl;

	@Autowired
	private QuoteProposalWorkFlowEntryServiceImpl quoteProposalService;
	
	@Autowired
	private JsonMapperFromDB jsonMapper;
	
	@Autowired
	private EmiTransactionDetailsRepository emitransrepo;
	
	@Autowired
	private EngineerInfoRepository engRepo;
	
	@Autowired
	private SectionCoverMasterRepository sectionCoverMasterRepository;
	
	@Autowired
	private TemplateRepository templateRepository;
	
	@Autowired
	private TermsAndConditionServiceImpl termsAndConditionServiceImpl ;
	
	@Autowired
	private DozerBeanMapper dozerMapper;
	
	@Autowired
	private ReinsuranceService reinsuranceService;
	
	@Autowired
	@Qualifier("customTaskExecutor")
	private Executor taskExecutor;	
	
	@Autowired
	private CompanyProductMasterRepository companyProductMasterRepo;
	
	@Autowired
	private HierarchyManagementRepository workFlowHierarchyManagementRepository;
	
	

	@Override
	public CommonRes call_OT_Insert(NewQuoteReq req) {
		CommonRes commonRes = new CommonRes();
		NewQuoteRes response = new NewQuoteRes();
		List<Error> errors = new ArrayList<Error>();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		QuoteThreadReq request = new QuoteThreadReq(); 
		Optional<EserviceCustomerDetails> optCustomer=null;
		
		try {
		boolean referal = false ;
//		CompletableFuture<List<FactorRateRequestDetails>> coversFuture =
//		        CompletableFuture.supplyAsync(() ->
//		                facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(
//		                        req.getRequestReferenceNo()
//		                ), taskExecutor);

//		CompletableFuture<List<EserviceSectionDetails>> secsFuture =
//		        CompletableFuture.supplyAsync(() ->
//		                eserSecRepo.findByRequestReferenceNo(req.getRequestReferenceNo()), taskExecutor);

		// wait for completion
		List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
//		List<EserviceSectionDetails> secs = secsFuture.join();

		// set response
		request.setGetSectionDatas(req.getGetSectionDatas());
		request.setCovers(covers);

		// safe companyId extraction
		String companyId = (covers != null && !covers.isEmpty())
		        ? covers.get(0).getCompanyId()
		        : "";

		// likely correction (based on your intent)
		req.setInsuranceId(companyId);
	//	CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , req.getProductId().toString());
		CompanyProductMaster product = new CompanyProductMaster();
		if(req.getProduct()!=null)
		{
			 product =req.getProduct();
		}
		else
		{
		 product =  getCompanyProductMasterDropdown(companyId , req.getProductId().toString());
		}
		
		req.setMotorYn(product.getMotorYn());
		request.setBuildDatas(req.getBuildDatas());
		request.setHumanDatas(req.getHumanDatas());
		request.setTravelDatas(req.getTravelDatas());
		if( StringUtils.isBlank(req.getAdminLoginId())) {
			
			// Refral Checking Method
			commonRes = RefferalChecking(req);
			ReferalResponse res = (ReferalResponse) commonRes.getCommonResponse();
			if(res!=null && StringUtils.isNotBlank( res.getReferral())  && res.getReferral().equalsIgnoreCase("true") ) {
				referal = true ;	
				HomePositionMaster status = homeRepo.findByRequestReferenceNoAndQuoteNoIsNull(req.getRequestReferenceNo());
				if(status!=null)
				{
					status.setStatus("RP");	
					homeRepo.saveAndFlush(status);
				}
				
			}	
		}
		
		// Referal Returning Block
		if( referal == true || (commonRes.getIsError()!=null && commonRes.getIsError()==true) ) {
			 if(commonRes.getIsError()==true ) {
				 return  commonRes ; 
			 }
			 // Notification Trigger
			req.setReferralRemarks(((ReferalResponse) commonRes.getCommonResponse()).getReferalRemarks());
	
			
		//-- Begin of New Quote Proposal	
			/**
			 * Processes the quote proposal request by setting up the proposal details 
			 * and initiating the first workflow entry.			 * 
			 * @param req The incoming request containing details for processing the quote proposal.
			 * @since : 20-12-2024
			 */
			{
			    // Fetch the EserviceSectionDetails based on the request reference number
				List<EserviceSectionDetails> requestReferenceNo = new ArrayList<>();
				if(req.getGetSectionDatas()!= null &&  !req.getGetSectionDatas().isEmpty())
				{
					requestReferenceNo = req.getGetSectionDatas();
				}
				else
				{
					requestReferenceNo = eserSecRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
					req.setGetSectionDatas(requestReferenceNo);			
					}
				EserviceSectionDetails sectionDetails = requestReferenceNo.get(0);
				
				CommonRes frameQuoteReq = setQuoteThreadReq(req );
	            if( frameQuoteReq.getErrorMessage() !=null && frameQuoteReq.getErrorMessage().size()>0 ) {
	            	commonRes = frameQuoteReq ;
	            	return commonRes ; 
	            }
	            
	            // Extract details from the QuoteThreadReq
	            request = (QuoteThreadReq) frameQuoteReq.getCommonResponse() ;
	            LocalDate policyStartDate = request.getPolicyStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	            LocalDate policyEndDate = request.getPolicyEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	            
	            // Prepare the QuoteProposalSaveReq object
				QuoteProposalSaveReq proposal=new QuoteProposalSaveReq();
				proposal.setCompanyId(Integer.valueOf(sectionDetails.getCompanyId()));
				proposal.setProductId(Integer.valueOf(sectionDetails.getProductId()));
				
			    // Retrieve customer details using the composite key
				EserviceCustomerDetailsId customerid = new EserviceCustomerDetailsId(
						sectionDetails.getCustomerReferenceNo(), sectionDetails.getCompanyId());
				
				optCustomer = eserCustRepo.findById(customerid);
				if(optCustomer.isPresent()){
					proposal.setClientName(optCustomer.get().getClientName());
				}

				// Set additional proposal details
				proposal.setCustomerReferenceNo(sectionDetails.getCustomerReferenceNo());
				proposal.setPolicyEndDate(policyEndDate);
				proposal.setPolicyStartDate(policyStartDate);
				//Quote no generated after referral approval process therefore quote no is null
				proposal.setQuoteNo(null);
				proposal.setRequestReferenceNo(sectionDetails.getRequestReferenceNo());
				proposal.setSumInsured(request.getSumInsured());
				proposal.setCreatedBy(req.getCreatedBy());
				quoteProposalService.createQuoteProposalAndFirstWorkflowEntry(proposal);				
			}
		//-- End of New Quote Proposal		
			
			
			// Background Call
			NotificatinThread smwthread=new NotificatinThread(notiThreadService,req,"REFERRAL");
			Thread th=new Thread(smwthread);
			th.start();
			//Tracking Details
			 trackingDetailsBuyPolicy(req);
			 return  commonRes ;
		} else {
			// Thread Call Setup
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			MyTaskList taskList = new MyTaskList(queue);
			
			CommonRes frameQuoteReq = setQuoteThreadReq(req );
            if( frameQuoteReq.getErrorMessage() !=null && frameQuoteReq.getErrorMessage().size()>0 ) {
            	commonRes = frameQuoteReq ;
            	return commonRes ; 
            }
            
            // Frame Request 
            request = (QuoteThreadReq) frameQuoteReq.getCommonResponse() ;
            request.setMotorDetailsList(frameQuoteReq.getMotorDetailsList());

            // Delete Same Quote Old Records
            commonRes =  oldQuoteRecordsDeleteThreadCall(request);
            request.setMotorDetailsList(frameQuoteReq.getMotorDetailsList());

            if( commonRes.getErrorMessage() !=null && commonRes.getErrorMessage().size()>0 ) {
             	return commonRes ; 
             } 
            
            // Customer Save Thread Call
            QuoteThreadCall customerSave = new QuoteThreadCall("CustomerSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
            		, locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
            queue.add(customerSave);
            // Section Save
            QuoteThreadCall sectionSave = new QuoteThreadCall("SectionSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
            		, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
            	     , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
            queue.add(sectionSave);
            
            int threadCount = 2 ;
            int success = 0;
            
           
            
         // Product Wise Thread Call
            commonRes = productWiseThreadCall( req , request ) ;
        	 if( commonRes.getErrorMessage() !=null && commonRes.getErrorMessage().size()>0 ) {
             	commonRes = frameQuoteReq ;
             	return commonRes ; 
             }
        	 
//        	  // Deactivate Old Covers 
//	 			if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
//	 				
//	 				commonRes = deactivateOldCovers(request); 
//	 				if( commonRes.getIsIsError() == true  ) {
//	 					return commonRes ; 
//	 				}
//	 			}
            
        	 ProductThreadRes productThreads = (ProductThreadRes) commonRes.getCommonResponse();
        	 if( productThreads.getThreadCount() !=null) {
        		 threadCount = threadCount + productThreads.getProductQueue().size();
            	 queue.addAll(productThreads.getProductQueue());
            	 	 
        	 }
        	 Map<String,Object> custRes = new HashMap<String,Object>() ;
 			List<Map<String,Object>> motRes =  new ArrayList<Map<String,Object>>();
 			List<Map<String,Object>> covRes =  new ArrayList<Map<String,Object>>() ;
 			List<Map<String,Object>> traRes =  new ArrayList<Map<String,Object>>();
 			Map<String,Object> secRes =  new HashMap<String,Object>();
 			Map<String,Object> insRes = new HashMap<String,Object>() ;
 			// Cover Save 
 			{
 				List<Callable<Object>> queue2 = new ArrayList<Callable<Object>>();
 				
 				MyTaskList taskList2 = new MyTaskList(queue2);
 				if(productThreads.getCoverQueue()!=null && productThreads.getCoverQueue().size() > 0) {
 					threadCount = productThreads.getCoverQueue().size() ;
 	 				queue2.addAll(productThreads.getCoverQueue());
 	 				ForkJoinPool forkjoin = new ForkJoinPool(threadCount); 
 	 	            ConcurrentLinkedQueue<Future<Object>> invoke  = (ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList2) ;
 	 	          //  em.flush();
 	 	            for (Future<Object> callable : invoke) {

 	 					log.info(callable.getClass() + "," + callable.isDone());

 	 					if (callable.isDone()) {
 	 						Map<String, Object> map = (Map<String, Object>) callable.get();

 	 						for (Entry<String, Object> future : map.entrySet()) {
 	 							if ("CoverSave".equalsIgnoreCase(future.getKey())) {
 	 								covRes.add((Map<String,Object>) future.getValue());
 	 							}
 	 						}

 	 						success++;
 	 					}
 	 				}    
 				}
 				
 		
 			}
 			
 			
 			
        	 ForkJoinPool forkjoin = new ForkJoinPool(threadCount); 
             ConcurrentLinkedQueue<Future<Object>> invoke  =(ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList);
             
             
        	
			for (Future<Object> callable : invoke) {

				log.info(callable.getClass() + "," + callable.isDone());

				if (callable.isDone()) {
					Map<String, Object> map = (Map<String, Object>) callable.get();

					for (Entry<String, Object> future : map.entrySet()) {
						
						if ("CustomerSave".equalsIgnoreCase(future.getKey())) {
							custRes = (Map<String,Object>) future.getValue();
						} else if ("MotorSave".equalsIgnoreCase(future.getKey())) {
							motRes.add((Map<String,Object>) future.getValue());
						} else if ("CoverSave".equalsIgnoreCase(future.getKey())) {
							covRes.add((Map<String,Object>) future.getValue());
						} else if ("TravelSave".equalsIgnoreCase(future.getKey())) {
							traRes.add((Map<String,Object>) future.getValue());
						} else if ("SectionSave".equalsIgnoreCase(future.getKey())) {
							secRes= (Map<String,Object>) future.getValue();
						}
					}

					success++;
				}
			}
	
			
		
			// Cust Res
			if( custRes.get("Response")!=null && custRes.get("Response").toString().equals("Failed") ) {
				errors.add(new Error("01","Customer Save",custRes.get("Errors").toString()));
				commonRes.setCommonResponse(null);
				commonRes.setIsError(true);
				commonRes.setErrorMessage(errors);
				commonRes.setMessage("Failed");
				return commonRes ; 
				
			} else if( secRes.get("Response")!=null && secRes.get("Response").toString().equals("Failed") ) {
				errors.add(new Error("01","Section Save",secRes.get("Errors").toString()));
				commonRes.setCommonResponse(null);
				commonRes.setIsError(true);
				commonRes.setErrorMessage(errors);
				commonRes.setMessage("Failed");
				return commonRes ; 
				
			} else {
				
				// Motor Res
				for (Map<String,Object> mot : motRes) {
					if( mot.get("Response")!=null && mot.get("Response").toString().equals("Failed") ) {
						errors.add(new Error("01","Motor Save",mot.get("Errors").toString()));
						commonRes.setCommonResponse(null);
						commonRes.setIsError(true);
						commonRes.setErrorMessage(errors);
						commonRes.setMessage("Failed");
						return commonRes ; 
					}
				}
				
				// Travel Res
				for (Map<String,Object> tra : traRes) {
					if( tra.get("Response")!=null && tra.get("Response").toString().equals("Failed") ) {
						errors.add(new Error("01","Travel Save",tra.get("Errors").toString()));
						commonRes.setCommonResponse(null);
						commonRes.setIsError(true);
						commonRes.setErrorMessage(errors);
						commonRes.setMessage("Failed");
						return commonRes ; 
					}
				}
				
				// Cover Res
				for (Map<String,Object> cov : covRes) {
					if( cov.get("Response")!=null && cov.get("Response").toString().equals("Failed") ) {
						errors.add(new Error("01","Cover Save",cov.get("Errors").toString()));
						commonRes.setCommonResponse(null);
						commonRes.setIsError(true);
						commonRes.setErrorMessage(errors);
						commonRes.setMessage("Failed");
						return commonRes ; 
					}
				}
			}
			
			// Response 
			if ( errors !=null && errors.size()>0 ) {
				commonRes.setCommonResponse(null);
				commonRes.setIsError(true);
				commonRes.setErrorMessage(errors);
				commonRes.setMessage("Failed");
				
			} else {
				
				//Quote Save Thread Call
				List<Callable<Object>> queue2 = new ArrayList<Callable<Object>>();
				MyTaskList taskList2 = new MyTaskList(queue2);
//				request.setVehicleId(req.getVehicleIdsList().get(0).getVehicleId());
				QuoteThreadCall quoteSave = new QuoteThreadCall("QuoteSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo  
						, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
					    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
	            
				queue2.add(quoteSave);
				Thread.sleep(200);
				
				 ForkJoinPool forkjoin2 = new ForkJoinPool(threadCount);
				ConcurrentLinkedQueue<Future<Object>> invoke2 =(ConcurrentLinkedQueue<Future<Object>>)forkjoin2.invoke(taskList2);
				
				QuoteThreadRes quoteRes = new QuoteThreadRes(); 
				
				for (Future<Object> callable : invoke2) {
	
					log.info(callable.getClass() + "," + callable.isDone());
	
					if (callable.isDone()) {
						Map<String, Object> map = (Map<String, Object>) callable.get();
	
						for (Entry<String, Object> future : map.entrySet()) {
							if ("QuoteSave".equalsIgnoreCase(future.getKey())) {
	
								quoteRes = (QuoteThreadRes) future.getValue();
	
							} 
						}
						success++;
					}
				}

//				
//				
//				QuoteThreadRes quoteRes = call_QuoteSave(request);

				updateTermsAndCondition(request);
				updateEngeenirInfo(request);
				response.setQuoteNo(quoteRes.getQuoteNo());
				response.setRequestReferenceNo(quoteRes.getRequestReferenceNo());
				response.setCustomerId(quoteRes.getCustomerId());
				response.setResponse("Saved SuccessFully");
				String branchCode = "99999";

				
				    HomePositionMaster home = homeRepo.findByQuoteNo(quoteRes.getQuoteNo());

				    if (home != null && home.getBranchCode() != null) {
				        branchCode = home.getBranchCode();
				    }

				    else {
				    log.error("Error fetching branchCode from HomePositionMaster");
				}
				
				if (quoteRes != null 
				        && request != null
				        && branchCode!=null
				        && quoteRes.getRequestReferenceNo() != null
				        && quoteRes.getQuoteNo() != null
				        && request.getInsuranceId() != null
				        && request.getProductId() != null
				        && request.getCreatedBy() != null) {

				    AutoInsertTermsReq clauseDTO = AutoInsertTermsReq.builder()
				            .requestReferenceNo(quoteRes.getRequestReferenceNo())
				            .quoteNo(quoteRes.getQuoteNo())
				            .companyId(request.getInsuranceId())
				            .branchCode(branchCode)
				            .productId(String.valueOf(request.getProductId()))
				            .createdBy(request.getCreatedBy())
				            .build();

				    termsAndConditionServiceImpl.autoInsertDefaultTermsAfterBuyPolicy(clauseDTO);

				} else {
				    
				    log.warn("Skipping autoInsertDefaultTermsAfterBuyPolicy due to null/missing data");
				}
				
				String additionalInfoYN = resolveAdditionalInfoYN(req, req.getCompanyid());
				response.setAdditionalInfoYN(additionalInfoYN);
				 
				ticketserviceImpl.TrackCustomerStatus(quoteRes.getRequestReferenceNo(), quoteRes.getQuoteNo(),"QUOTATION_PAGE");
				// Response 
				if ( errors !=null && errors.size()>0 ) {
					commonRes.setCommonResponse(null);
					commonRes.setIsError(true);
					commonRes.setErrorMessage(errors);
					commonRes.setMessage("Failed");
					
				} else {
					commonRes.setCommonResponse(response);
					commonRes.setIsError(false);
					commonRes.setErrorMessage(Collections.emptyList());
					commonRes.setMessage("Success");
				}
			}
			
			if("100040".equals(request.getInsuranceId()) || "100027".equals(request.getInsuranceId())) {
				WorkEngine work=new WorkEngine();
				work.setCompanyId(request.getInsuranceId());
				work.setProductId(request.getProductId());
				work.setQuoteNo(response.getQuoteNo());
				work.setRequestReferenceNo(request.getRequestReferenceNo());
				work.setIntegType("UPD_QUOT_INTEG");
				work.setSectionId(request.getSectionId());
				
				jsonMapper.createQuotation(work);
			}
//			if ("100046".equals(request.getInsuranceId()) || "100046".equals(request.getInsuranceId())
//					|| "100046".equals(request.getInsuranceId()) || "100046".equals(request.getInsuranceId())
//					|| "100027".equals(request.getInsuranceId())) {
//
//				HomePositionMaster home = homeRepo.findByQuoteNo(response.getQuoteNo());
//				if (home != null) {
//					ReInsuranceQuoteReq riReq = new ReInsuranceQuoteReq();
//					riReq.setCreatedBy(req.getCreatedBy());
//					riReq.setQuoteNo(response.getQuoteNo());
//					riReq.setUserType(home.getUserType());
//					riReq.setRequestReferenceNo(req.getRequestReferenceNo());
//					reinsuranceService.insertReinsurance(riReq);
//				} else {
//					home = homeRepo.findByRequestReferenceNoOrderByNoOfVehiclesAsc(req.getRequestReferenceNo());
//					if (home != null) {
//						ReInsuranceQuoteReq riReq = new ReInsuranceQuoteReq();
//						riReq.setCreatedBy(req.getCreatedBy());
//						riReq.setQuoteNo(response.getQuoteNo());
//						riReq.setUserType(home.getUserType());
//						riReq.setRequestReferenceNo(req.getRequestReferenceNo());
//						reinsuranceService.insertReinsurance(riReq);
//					}
//				}
//			}

				notiService.motorQuotationNotification(req);
				trackingDetailsQuote(request);
				
		}	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		
		
		return commonRes ;
	}
	
	private void updateEngeenirInfo(QuoteThreadReq req) {
		try {
		List<EngineerInfo> info=engRepo.findByRequestReferenceNoAndProductid(req.getRequestReferenceNo(),Integer.valueOf(req.getProductId()));
		
		if (info != null && !info.isEmpty()) {
		    info.forEach(in -> in.setQuoteNo(req.getQuoteNo()));
		    engRepo.saveAll(info);
		    }
		}catch (Exception e) {
			log.error("Exception Occurs when update EngeenirInfo  " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	public  void updateSection(NewQuoteReq req) {
		try {

			if (null != req && null != req.getVehicleIdsList() && !req.getVehicleIdsList().isEmpty()) {

				if (null != req.getVehicleIdsList().get(0)) {

					VehicleIdsReq vehicle = req.getVehicleIdsList().get(0);
					if (null != vehicle && null != vehicle.getSectionId() && !vehicle.getSectionId().isEmpty()

							&& null != req.getRequestReferenceNo() && !req.getRequestReferenceNo().isEmpty()) {

//						List<EserviceMotorDetails> motorList = eserviceMotorDetailsRepo
//								.findByRequestReferenceNo(req.getRequestReferenceNo());
						
						   List<EserviceMotorDetails> motorList = req.getMotorDetailsMap()
			                        .values()
			                        .stream()
			                        .flatMap(List::stream)
			                        .filter(m -> req.getRequestReferenceNo().equals(m.getRequestReferenceNo()))
			                        .collect(Collectors.toList());

						if (null != motorList && !motorList.isEmpty() && null != motorList.get(0)) {

							EserviceMotorDetails motor = motorList.get(0);

							if (null != motor) {

								if ("104".equals(vehicle.getSectionId())) {
									motor.setSectionId(vehicle.getSectionId());
									motor.setSectionName("TPL");
								} else if ("103".equals(vehicle.getSectionId())) {
									motor.setSectionId(vehicle.getSectionId());
									motor.setSectionName("Comprehensive");

								}
								eserviceMotorDetailsRepo.saveAndFlush(motor);
							}

						}

					}

				}
			}

		} catch (Exception e) {

			log.error("Exception Occurs when update section id  " + e.getMessage());
			e.printStackTrace();
		}
		
	}

	//-------------------------------------------------------------Refrral Checking Block ---------------------------------------------------------------------//
	@Transactional
	public CommonRes RefferalChecking(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		String companyId = "" ;
		try {
			List<MasterReferralDetails> findMasterRefrals = masReferralRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			List<IndividualReferalReq>  induRefs = new ArrayList<IndividualReferalReq>(); 
			
			boolean referral = false ;
			String referralRemarks = "" ;
			String manualRemarks = "" ;
			String uwRemarks = "" ;
			if(StringUtils.isNotBlank(req.getManualReferralYn()) && req.getManualReferralYn().equalsIgnoreCase("Y") ) {
				referral = true ;
				manualRemarks = req.getReferralRemarks();
				
			}
			
			// ---- Checker-Maker Referral Checking ----
			String checkerMakerRemarks = "";
			CompanyProductMaster companyProductMaster = companyProductMasterRepo
			        .findTopByProductIdAndCompanyIdAndStatusOrderByAmendIdDesc( Integer.valueOf(req.getProductId()),req.getInsuranceId(),"Y");

			if (companyProductMaster != null 
			        && StringUtils.isNotBlank(companyProductMaster.getCheckerYn()) 
			        && companyProductMaster.getCheckerYn().equalsIgnoreCase("Y")) {

			    List<HierarchyManagement> workflowHierarchyList = workFlowHierarchyManagementRepository
			            .findByCompanyIdAndProductId(Integer.valueOf(req.getInsuranceId()), Integer.valueOf(req.getProductId()));

			    if (workflowHierarchyList != null && workflowHierarchyList.size() > 0) {
			        referral = true;
			        checkerMakerRemarks = "Checker Maker Referral";
			    }
			}

			{
				List<FactorRateRequestDetails> userOptCovers = new ArrayList<FactorRateRequestDetails>();
			
				// Covers Referrral Checking
				List<PolicyCoverData> 	alreadyOptCovers = coverRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
				req.setCovers(covers);
				
				
				boolean alreadyOpt = false ; 
				if(alreadyOptCovers.size() >0) {
					alreadyOpt = true ;
				}
				companyId = covers.size() > 0 ? covers.get(0).getCompanyId()  :"" ;
						
				for (VehicleIdsReq veh : req.getVehicleIdsList()) {
					List<PolicyCoverData> alreadyOptVeh = alreadyOptCovers.stream().filter(o -> veh.getVehicleId() !=null && o.getVehicleId().equals(veh.getVehicleId()) &&
					StringUtils.isNotBlank(veh.getSectionId())		&& o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) ).collect(Collectors.toList());	
					
					boolean alreadyOpt2 = false ; 
					if(alreadyOptVeh.size() >0 && alreadyOptVeh.get(0).getIsReferral() !=null && alreadyOptVeh.get(0).getIsReferral().equalsIgnoreCase("Y") ) {
						alreadyOpt2 = true ;
					}
					
					String referrals = "" ;
					// Cover Referal Checking
					List<CoverIdsReq> coverList = veh.getCoverIdList();
					for (CoverIdsReq cov : coverList) {
						
						if(StringUtils.isBlank(cov.getSubCoverYn()) || cov.getSubCoverYn().equalsIgnoreCase("N") ) {
							List<FactorRateRequestDetails> filterCovers = covers.stream().filter( o -> o.getLocationId().compareTo(veh.getLocationId())==0 &&   o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) &&  o.getCoverId().equals(cov.getCoverId()) ).collect(Collectors.toList());	
							userOptCovers.addAll(filterCovers);
							
							List<FactorRateRequestDetails> filterReferalCovers = filterCovers.stream().filter( o ->  o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&  o.getIsReferral()!=null && o.getIsReferral().equalsIgnoreCase("Y") ).collect(Collectors.toList());
							if(filterReferalCovers.size()>0 ) { 
								List<PolicyCoverData> filterAlreadyOpt = alreadyOptCovers.stream().filter(o -> o.getVehicleId().equals(veh.getVehicleId())
										&& o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) ).collect(Collectors.toList());	
								
								if(filterAlreadyOpt.size() <=0 ) {
									referrals = StringUtils.isBlank(referrals)? filterReferalCovers.get(0).getCoverName()  : referrals ;// +"~" +filterReferalCovers.get(0).getCoverName() ;
									referral = true ;
								}
								
							}
						
						} else {
							List<FactorRateRequestDetails> filterSubCovers  = covers.stream().filter( o -> o.getLocationId().compareTo(veh.getLocationId())==0 &&  o.getVehicleId().equals(veh.getVehicleId()) &&  o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))  ).collect(Collectors.toList());
							userOptCovers.addAll(filterSubCovers);
							List<FactorRateRequestDetails> filterReferalSubCovers = filterSubCovers.stream().filter( o ->  o.getVehicleId().equals(veh.getVehicleId()) &&  o.getCoverId().equals(cov.getCoverId()) &&  o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) &&   o.getIsReferral()!=null && o.getIsReferral().equalsIgnoreCase("Y")  ).collect(Collectors.toList());
							if(filterReferalSubCovers.size()>0  ) { 
								List<PolicyCoverData> filterAlreadyOpt = alreadyOptCovers.stream().filter(o -> o.getVehicleId().equals(veh.getVehicleId())
										&& o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))   ).collect(Collectors.toList());	
								
								if(filterAlreadyOpt.size() <=0 ) {
									referrals = StringUtils.isBlank(referrals)? filterReferalSubCovers.get(0).getCoverName() : referrals ;// +"~" +filterReferalSubCovers.get(0).getCoverName() ;
									referral = true ;
								}
							}
						}
					}
					
					// Master Referals 
					List<MasterReferralDetails> filterMasterReferals =  findMasterRefrals.stream().filter(o -> o.getRiskId().equals(veh.getVehicleId()) 
							&& o.getProductId().equals(Integer.valueOf(req.getProductId())) 
							&& o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) ).collect(Collectors.toList());
							
					if (  filterMasterReferals!=null && filterMasterReferals.size()>0 && alreadyOpt2==false) {
						for ( MasterReferralDetails masRef : filterMasterReferals) {
							if(! masRef.getReferralDesc().contains("Exception")) {
								referrals = StringUtils.isBlank(referrals)? masRef.getReferralDesc() : referrals ;// +"~" +masRef.getReferralDesc() ;
								referral = true ;	
							}
							
						}
					}
					IndividualReferalReq indu = new IndividualReferalReq();
					indu.setProductId(req.getProductId());
					indu.setRiskId(veh.getVehicleId());
					indu.setSectionId(req.getSectionId());	
					indu.setReferals(referrals);
					induRefs.add(indu);
					
					
				}
			
				// Under Writter Refral Checking
				List<UwQuestionsDetails>  uwQuestions = uwRepo.findByRequestReferenceNo( req.getRequestReferenceNo());
				List<UwQuestionsDetails>  filterUwQuestions = uwQuestions.stream().filter( o -> o.getIsReferral()!=null && o.getIsReferral().equalsIgnoreCase("Y") ).collect(Collectors.toList());
				if(filterUwQuestions.size()>0 && alreadyOpt==false ) {
					referral = true ;
					
					uwRemarks = filterUwQuestions.get(0).getUwQuestionDesc() ;
					
				}
				List<FactorRateRequestDetails> nonOptCovers  = covers ;
				
				// Update User Opted Covers 
				for (FactorRateRequestDetails uptCover : userOptCovers ) {
					nonOptCovers.remove(uptCover);
					
					uptCover.setUserOpt("Y");
					
				}
				// Update 99999
				List<FactorRateRequestDetails> fleetCovers = covers.stream().filter( o -> o.getVehicleId().equals(99999) && o.getSectionId().equals(99999)  ).collect(Collectors.toList());
				for (FactorRateRequestDetails uptCover : fleetCovers ) {
					nonOptCovers.remove(uptCover);
					uptCover.setUserOpt("Y");
					userOptCovers.add(uptCover);				
				}
				
				facRateRepo.saveAllAndFlush(userOptCovers);
				
				// Update Non Opted Covers
				if (!nonOptCovers.isEmpty()) {
					for (FactorRateRequestDetails cover : nonOptCovers) {

						cover.setUserOpt("N");

					}
				}
				
				facRateRepo.saveAllAndFlush(nonOptCovers);
				em.flush();
			}	
			
			
			
			
			if (  referral == true ) {
				  BigDecimal overAllSuminsured = BigDecimal.ZERO;
				  String branchCode = "" ;
					String otherReferals  =  StringUtils.isBlank(manualRemarks) ? "" : manualRemarks ;
					otherReferals = StringUtils.isBlank(uwRemarks) ? otherReferals : uwRemarks ;//+ ( StringUtils.isNotBlank(otherReferals) ?  "~" +otherReferals :"")  ;
					referralRemarks = otherReferals ;
					Double sumInsured=0.0d;
					if (req.getMotorYn().equalsIgnoreCase("H") &&  req.getProductId().equalsIgnoreCase(travelProductId)) {
						EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
						List<IndividualReferalReq> filterInduRef = induRefs.stream().filter( o -> o.getRiskId().equals(travelData.getRiskId()) ).collect(Collectors.toList()) ;
						String induRefDesc  = filterInduRef.size()> 0 ?  filterInduRef.get(0).getReferals() : "" ;
						String induRefDesc3  = StringUtils.isBlank(otherReferals) ? induRefDesc : otherReferals;// + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"")  ;
						referralRemarks = StringUtils.isBlank(referralRemarks) ? induRefDesc : referralRemarks ;//+ ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"") ;
						
						travelData.setReferalRemarks(induRefDesc3) ;
						travelData.setStatus("RP");
						travelData.setUpdatedDate(new Date());
//						travelData.setQuoteNo("");
//						travelData.setCustomerId("");
						travelData.setManualReferalYn(req.getManualReferralYn());
						eserTraRepo.save(travelData);
						
						overAllSuminsured = new BigDecimal("0");
						branchCode = travelData.getBranchCode();
						
					} else if ( req.getMotorYn().equalsIgnoreCase("M") ) {
						List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
						
						for (EserviceMotorDetails mot : motorDatas ) {
							mot.setStatus("RP");
							List<IndividualReferalReq> filterInduRef = induRefs.stream().filter( o -> o.getRiskId().equals(mot.getRiskId()) ).collect(Collectors.toList()) ;
							String induRefDesc  = filterInduRef.size()> 0 ?  filterInduRef.get(0).getReferals() : "" ;
							String induRefDesc3  = StringUtils.isBlank(otherReferals) ? induRefDesc : otherReferals;// + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"")  ;
							referralRemarks = StringUtils.isBlank(referralRemarks) ? induRefDesc : referralRemarks;// + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"") ;
							//sumInsured=mot.getSumInsuredLc();
							mot.setReferalRemarks(induRefDesc3) ;
							mot.setUpdatedDate(new Date());
//							mot.setQuoteNo("");
//							mot.setCustomerId("");
							mot.setManualReferalYn(req.getManualReferralYn());
							eserMotRepo.save(mot);
							
							// Suminsured 
							if(  ! "D".equalsIgnoreCase(mot.getStatus()) ) {
								overAllSuminsured = mot.getSumInsuredLc()==null ? overAllSuminsured : mot.getSumInsuredLc().add(overAllSuminsured) ;
								branchCode = mot.getBranchCode();
							}
						}
						
						
					}  else if (req.getMotorYn().equalsIgnoreCase("A") ) {
					//	List<EserviceBuildingDetails> buildingDatas = eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
						List<EserviceBuildingDetails> buildingDatas =req.getBuildDatas();		
						for (EserviceBuildingDetails build : buildingDatas ) {
							List<IndividualReferalReq> filterInduRef = induRefs.stream().filter( o -> o.getRiskId().equals(build.getRiskId()) &&   StringUtils.isNotBlank(o.getReferals())   ).collect(Collectors.toList()) ;
							String induRefDesc  = filterInduRef.size()> 0 ?  filterInduRef.get(0).getReferals() : "" ;
							String induRefDesc3  = StringUtils.isBlank(otherReferals) ? induRefDesc : otherReferals;// + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"")  ;
							referralRemarks = StringUtils.isBlank(referralRemarks) ? induRefDesc : referralRemarks;// + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"") ;
							
							build.setReferalRemarks(induRefDesc3) ;
							build.setStatus("RP");
							build.setUpdatedDate(new Date());
//							build.setQuoteNo("");
//							build.setCustomerId("");
							build.setManualReferalYn(req.getManualReferralYn());
							eserBuildRepo.save(build);
							
							branchCode = build.getBranchCode();
							
							// Building Suminsured

							if(build.getSumInsuredLc()!=null ) {
								overAllSuminsured =  build.getSumInsuredLc().add(overAllSuminsured) ;
							}

//							
							// Fire And Material Damage
					    	if( overAllSuminsured.compareTo(new BigDecimal("0"))==0   ) {
//					    		
					    		if(build.getSumInsuredLc()!=null ) {
									overAllSuminsured =  build.getSumInsuredLc().add(overAllSuminsured) ;
								}
								
					    	}
							
					    	// Electronic Equipment
					    	if( overAllSuminsured.compareTo(new BigDecimal("0"))==0   ) {
//					    		if(build.getElecEquipSumInsuredLC()!=null ) {
//									overAllSuminsured = build.getElecEquipSumInsuredLC().add(overAllSuminsured)  ;
//									
//								}
					    		if(build.getSumInsuredLc()!=null ) {
									overAllSuminsured =  build.getSumInsuredLc().add(overAllSuminsured) ;
								}
					    	}
							
					    	if( overAllSuminsured.compareTo(new BigDecimal("0"))==0   ) {

					    		if(build.getSumInsuredLc()!=null ) {
									overAllSuminsured =  build.getSumInsuredLc().add(overAllSuminsured) ;
								}
									
					    	}
							
							
						}
					//	List<EserviceCommonDetails> commonDatas = eserCommonRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
						List<EserviceCommonDetails> commonDatas = req.getHumanDatas();
						if( overAllSuminsured.compareTo(new BigDecimal("0"))==0   ) {
							for (EserviceCommonDetails commonData : commonDatas ) {
								List<IndividualReferalReq> filterInduRef = induRefs.stream().filter( o -> o.getRiskId().equals(commonData.getRiskId()) ).collect(Collectors.toList()) ;
								String induRefDesc  = filterInduRef.size()> 0 ?  filterInduRef.get(0).getReferals() : "" ;
								String induRefDesc3  = StringUtils.isBlank(otherReferals) ? induRefDesc : otherReferals ;//+ ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"")  ;
								referralRemarks = StringUtils.isBlank(referralRemarks) ? induRefDesc : referralRemarks;// + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"") ;
								
								commonData.setStatus("RP");
								commonData.setReferalRemarks(induRefDesc3) ;
								commonData.setUpdatedDate(new Date());
//								commonData.setQuoteNo("");
//								commonData.setCustomerId("");
								commonData.setManualReferalYn(req.getManualReferralYn());
								eserCommonRepo.save(commonData);
								if(! "D".equalsIgnoreCase(commonData.getStatus()) ) {
									overAllSuminsured = commonData.getSumInsuredLc()==null ? overAllSuminsured : commonData.getSumInsuredLc().add(overAllSuminsured) ;
//									overAllSuminsured = commonData.getAooSuminsuredLc()==null ? overAllSuminsured : commonData.getAooSuminsuredLc().add(overAllSuminsured) ;
								//	overAllSuminsured = commonData.getAggSuminsuredLc()==null ? overAllSuminsured : commonData.getAggSuminsuredLc().add(overAllSuminsured) ;
									
								}
								branchCode = commonData.getBranchCode();
								
							}
						}
						
						
					} else  {
						List<EserviceCommonDetails> commonDatas =  req.getHumanDatas();
						for (EserviceCommonDetails commonData : commonDatas ) {
							List<IndividualReferalReq> filterInduRef = induRefs.stream().filter( o -> o.getRiskId().equals(commonData.getRiskId()) ).collect(Collectors.toList()) ;
							String induRefDesc  = filterInduRef.size()> 0 ?  filterInduRef.get(0).getReferals() : "" ;
							String induRefDesc3  = StringUtils.isBlank(otherReferals) ? induRefDesc : otherReferals ;//+ ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"")  ;
							referralRemarks = StringUtils.isBlank(referralRemarks) ? induRefDesc : referralRemarks;// + ( StringUtils.isNotBlank(induRefDesc) ?  "~" +induRefDesc :"") ;
							
							commonData.setStatus("RP");
							commonData.setReferalRemarks(induRefDesc3) ;
							commonData.setUpdatedDate(new Date());
							commonData.setQuoteNo("");
							commonData.setCustomerId("");
							commonData.setManualReferalYn(req.getManualReferralYn());
							eserCommonRepo.save(commonData);
							
							branchCode = commonData.getBranchCode();
							
							if(! "D".equalsIgnoreCase(commonData.getStatus()) ) {
								overAllSuminsured = commonData.getSumInsuredLc()==null ? overAllSuminsured : commonData.getSumInsuredLc().add(overAllSuminsured) ;
//								overAllSuminsured = commonData.getAooSuminsuredLc()==null ? overAllSuminsured : commonData.getAooSuminsuredLc().add(overAllSuminsured) ;
								//overAllSuminsured = commonData.getAggSuminsuredLc()==null ? overAllSuminsured : commonData.getAggSuminsuredLc().add(overAllSuminsured) ;
							}
						}
					}
					
					//Referral Update Based on Sum insured
					GetApproverListReq refReq= new GetApproverListReq(); 
					refReq.setBranchCode(branchCode);
					refReq.setCompanyId(req.getInsuranceId());
					refReq.setProductId(req.getProductId());
					refReq.setSumInsured(overAllSuminsured.toPlainString());
					List<GetApproverListRes> res1 =gridService.getApproverList(refReq);
					
					if(res1 ==null || res1.size() <=0 ) {
						errors.add(new Error("01","UwRefferral", "No Under Writter Available for this Over All Suminsured : " + overAllSuminsured.toPlainString()));
						commonRes.setCommonResponse(null);
						commonRes.setIsError(true);
						commonRes.setErrorMessage(errors);
						commonRes.setMessage("Failed");	
						return commonRes; 
					}
					
					// Delete Old
					Long uwReferralCount = uwReferralRepo.countByRequestReferenceNo(req.getRequestReferenceNo());
					if(uwReferralCount > 0 ) {
						uwReferralRepo.deleteByRequestReferenceNo(req.getRequestReferenceNo());
					}
					
					//Save New
					List<UWReferralDetails> saveUWList =new ArrayList<UWReferralDetails>();
					for(GetApproverListRes uwres : res1) {
						UWReferralDetails saveUWReferral =new UWReferralDetails();
						saveUWReferral.setBranchCode(branchCode);
						saveUWReferral.setCompanyId(req.getInsuranceId());
						saveUWReferral.setEntryDate(new Date());
						saveUWReferral.setProductId(Integer.valueOf(req.getProductId()));
						saveUWReferral.setRequestReferenceNo(req.getRequestReferenceNo());
						saveUWReferral.setStatus("Y");
						saveUWReferral.setSumInsured(overAllSuminsured);
						saveUWReferral.setUwLoginId(uwres.getLoginId());
						saveUWReferral.setUwStatus("Y");
						saveUWList.add(saveUWReferral);
						
					}
					uwReferralRepo.saveAllAndFlush(saveUWList);
					
					// Referral Response 
					ReferalResponse res = new ReferalResponse();
					res.setReferalRemarks(referralRemarks);
					res.setRequestReferenceNo(req.getRequestReferenceNo());
					res.setResponse("Referral Pending");
					res.setStatus("RP");
					res.setQuoteNo(null);
					res.setReferral("true");
					res.setInsuranceId(companyId);
					
					commonRes.setCommonResponse(res);
					commonRes.setIsError(false);
					commonRes.setErrorMessage(Collections.emptyList());
				 	commonRes.setMessage("Success");
				 	
				 	
			} else {
				// Referral Response 
				ReferalResponse res = new ReferalResponse();
				res.setReferalRemarks("");
				res.setRequestReferenceNo(req.getRequestReferenceNo());
				res.setResponse("");
				res.setStatus("");
				res.setQuoteNo(null);
				res.setReferral("false");
				res.setInsuranceId(companyId);
				
				commonRes.setCommonResponse(null);
				commonRes.setIsError(false);
				commonRes.setErrorMessage(Collections.emptyList());
			 	commonRes.setMessage("Success");	
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		return commonRes ;
	}

	private void updateTermsAndCondition(QuoteThreadReq  request) {
		try {
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TermsAndCondition> query = cb.createQuery(TermsAndCondition.class);

			// Find All
			Root<TermsAndCondition> b = query.from(TermsAndCondition.class);

			// Select
			query.select(b);
			
			// Where
			Predicate n1 = cb.equal(b.get("requestReferenceNo"), request.getRequestReferenceNo());
			Predicate n2 = cb.equal(b.get("companyId"),   request.getInsuranceId());
			Predicate n3 = cb.equal(b.get("productId"), request.getProductId());
			
			query.where(n1,n2,n3);

			// Get Result
			TypedQuery<TermsAndCondition> result = em.createQuery(query);
			List<TermsAndCondition> list = result.getResultList();
			
			if(list.size() > 0 ){
				// create update
				CriteriaUpdate<TermsAndCondition> update = cb.createCriteriaUpdate(TermsAndCondition.class);
				// set the root class
				Root<TermsAndCondition> m = update.from(TermsAndCondition.class);
				Predicate a1 = cb.equal(m.get("requestReferenceNo"), request.getRequestReferenceNo());
				Predicate a2 = cb.equal(m.get("companyId"),   request.getInsuranceId());
				Predicate a3 = cb.equal(m.get("productId"), request.getProductId());
					update.set("quoteNo",request.getQuoteNo());
				 
				update.where(a1,a2,a3);
				// perform update
				em.createQuery(update).executeUpdate();
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			
		}
	}
	public CommonRes oldQuoteRecordsDeleteThreadCall( QuoteThreadReq request ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		String res = "" ;
		try {
			int threadCount = 1 ;
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			MyTaskList taskList = new MyTaskList(queue);
			
        	QuoteThreadCall deleteOldRecords = new QuoteThreadCall("DeleteOldRecords" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo  
        			, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
        		    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
          
        	queue.add(deleteOldRecords);
        	res = "Success";
        	int success = 0;
        	 
        	 Map<String,Object> deleteRes = new HashMap<String,Object>() ;
 			
 			 ForkJoinPool forkjoin = new ForkJoinPool(threadCount); 
             ConcurrentLinkedQueue<Future<Object>> invoke  = (ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList) ;
             
 			for (Future<Object> callable : invoke) {

 				log.info(callable.getClass() + "," + callable.isDone());

 				if (callable.isDone()) {
 					Map<String, Object> map = (Map<String, Object>) callable.get();

 					for (Entry<String, Object> future : map.entrySet()) {
 						
 						if ("DeleteOldQuote".equalsIgnoreCase(future.getKey())) {
 							deleteRes = (Map<String,Object>) future.getValue();
 						}
 					}

 					success++;
 				}
 			}
 	
 			// Cust Res
 			if( deleteRes.get("Response")!=null && deleteRes.get("Response").toString().equals("Failed") ) {
 				errors.add(new Error("01","Customer Save",deleteRes.get("Errors").toString()));
 				commonRes.setCommonResponse(null);
 				commonRes.setIsError(true);
 				commonRes.setErrorMessage(errors);
 				commonRes.setMessage("Failed");
 				return commonRes ; 
 				
 			} 
 			
	        commonRes.setCommonResponse(res);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(Collections.emptyList());
		 	commonRes.setMessage("Success");
		 	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		return commonRes ;
	}
	
	
	
	
	//-------------------------------------------------------------Product Wise Multi Thread Call---------------------------------------------------------------------//
	public CommonRes productWiseThreadCall(NewQuoteReq req , QuoteThreadReq request ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		ProductThreadRes ProductThreadRes = new ProductThreadRes();
		
		try {
			// Multiple Travel Thread Call	 
			if (request.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId) ) {
	        	
				ProductThreadRes =  travelProductThreadCall(req , request )  ;
				
			} else if (request.getMotorYn().equalsIgnoreCase("M")) {
	        	ProductThreadRes =  motorProductThreadCall(req , request )  ;
					
			// Multiple Bulding Thread Call	 
			} else if (request.getMotorYn().equalsIgnoreCase("A")) {
				ProductThreadRes =  buildingProductThreadCall(req , request )  ;
					
			
			// Multiple Vehicle Thread Call
			} else  {
				ProductThreadRes =  commonProductThreadCall(req , request )  ;
			}
	        commonRes.setCommonResponse(ProductThreadRes);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(Collections.emptyList());
		 	commonRes.setMessage("Success");
		 	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
			errors.add(new Error("01","Common Error",e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");	
		}
		return commonRes ;
	}
	
	
	public ProductThreadRes motorProductThreadCall(NewQuoteReq req , QuoteThreadReq request ) {
		ProductThreadRes ProductThreadRes = new ProductThreadRes();
		
		try {
			int threadCount = 0 ;
			List<Callable<Object>> productQueue = new ArrayList<Callable<Object>>();
			List<Callable<Object>> coverQueue = new ArrayList<Callable<Object>>();
			
			// Multiple Vehicle Thread Call
			
			List<Integer> vehicleIds = req.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
			List<EserviceMotorDetails> eserMotors = request.getMotorDetailsList().stream()
			        .filter(a -> !"D".equalsIgnoreCase(a.getStatus()))
			        .filter(m -> vehicleIds.contains(m.getRiskId()))
			        .sorted(Comparator.comparing(EserviceMotorDetails::getRiskId))
			        .collect(Collectors.toList());

			List<Integer> activeVehicleIds = eserMotors.stream().map(EserviceMotorDetails::getRiskId)
					.collect(Collectors.toList());
	    	//List<EserviceMotorDetails> activeMotorList =  eserMotRepo.findByRequestReferenceNoAndRiskIdInAndStatusNotOrderByRiskIdAsc(req.getRequestReferenceNo(),vehicleIds ,"D");
	    	//List<Integer> activeVehicleIds = activeMotorList.stream().map(EserviceMotorDetails :: getRiskId  ).collect(Collectors.toList());
	    	
	 
			for (Integer veh :  activeVehicleIds ) {
					threadCount = threadCount +  2 ;
	            	List<String> sectionId = req.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(veh)).map(VehicleIdsReq :: getSectionId   ).collect(Collectors.toList());
	            	
	            	QuoteThreadReq request2 = new QuoteThreadReq();
	            	request2.setCustomerId(request.getCustomerId());
	            	request2.setProductId(request.getProductId());
	            	request2.setQuoteNo(request.getQuoteNo());
	            	request2.setRequestReferenceNo(request.getRequestReferenceNo());
	            	request2.setVehicleIdsList(request.getVehicleIdsList());
	            	request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
	            	request2.setCreatedBy(request.getCreatedBy());
	            	request2.setVehicleId(veh);
	            	request2.setLocationId(1);
	            	request2.setSectionId(sectionId.get(0));
	            	request2.setPolicyStartDate(request.getPolicyStartDate());
	            	request2.setPolicyEndDate(request.getPolicyEndDate());
	            	request2.setEffetiveDate(request.getEffetiveDate());
	            	request2.setNoOfDays(request.getNoOfDays());
	            	request2.setEndtType(request.getEndtType());
	            	request2.setEndtCount(request.getEndtCount());
	            	request2.setEndtFields(request.getEndtFields());
	            	request2.setMotorYn(request.getMotorYn());
	            	request2.setIsFinYn(request.getIsFinYn());
	            	request2.setMotorDetailsList(request.getMotorDetailsList());

	            	QuoteThreadCall motorSave = new QuoteThreadCall("MotorSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo  
	            			, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
	            		    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
		            productQueue.add(motorSave);
					QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
							, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
						    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
					coverQueue.add(coverSave);	
					QuoteThreadCall driverSave = new QuoteThreadCall("DriverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo  
	            			, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
	            		    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
					 productQueue.add(driverSave);
	            }
			
			 // 99999 Covers
	       	 {
	       		threadCount = threadCount + 1;
	       		 QuoteThreadReq request2 = new QuoteThreadReq();
	           	 request2.setVehicleId(99999);
	           	 request2.setCustomerId(request.getCustomerId());
	           	 request2.setProductId(request.getProductId());
	           	 request2.setQuoteNo(request.getQuoteNo());
	           	 request2.setRequestReferenceNo(request.getRequestReferenceNo());
	           	 request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
	           	 request2.setVehicleIdsList(request.getVehicleIdsList());
	           	 request2.setCreatedBy(request.getCreatedBy());
	           	 request2.setGroupId(99999);
	           	request2.setLocationId(1);
	           	 request2.setGroupCount(1);
	           	 request2.setSectionId("99999");
	           	 request2.setPolicyStartDate(request.getPolicyStartDate());
		             request2.setPolicyEndDate(request.getPolicyEndDate());
		             request2.setEffetiveDate(request.getEffetiveDate());
		             request2.setNoOfDays(request.getNoOfDays());
		         	 request2.setEndtType(request.getEndtType());
	           	 request2.setEndtCount(request.getEndtCount());
	           	 request2.setEndtFields(request.getEndtFields());	
	           	 request2.setMotorYn(request.getMotorYn());
	           	 request2.setIsFinYn(request.getIsFinYn());
					request2.setMotorDetailsList(request.getMotorDetailsList());

	           	 QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
								, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
							    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
					 coverQueue.add(coverSave);
	       	 }
	    	 // Response 
	        ProductThreadRes.setProductQueue(productQueue);
	        ProductThreadRes.setCoverQueue(coverQueue);
	        ProductThreadRes.setThreadCount(threadCount);	
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
		}
		return ProductThreadRes ;
	}
	
	public ProductThreadRes travelProductThreadCall(NewQuoteReq req , QuoteThreadReq request ) {
		ProductThreadRes ProductThreadRes = new ProductThreadRes();
		
		try {
			int threadCount = 0 ;
			List<Callable<Object>> productQueue = new ArrayList<Callable<Object>>();
			List<Callable<Object>> coverQueue = new ArrayList<Callable<Object>>();
			
			
			// Multiple Vehicle Thread Call
			List<EserviceTravelGroupDetails> groupData = new ArrayList<EserviceTravelGroupDetails>(); 
			
			
			groupData = eserGroupRepo.findByRequestReferenceNoAndStatusNotOrderByGroupIdAsc(request.getRequestReferenceNo() ,"D" );
			if(groupData.size() > 0 ) {
				Integer passCount = 0;
	        	List<VehicleIdsReq>  filterAdult  = req.getVehicleIdsList().stream().filter( o ->  o.getVehicleId().equals(2) ).collect(Collectors.toList());
	        	List<VehicleIdsReq>  filterOthers =req.getVehicleIdsList().stream().filter( o -> ! o.getVehicleId().equals(2) ).collect(Collectors.toList());
	        	List<VehicleIdsReq>  totalGroup  = new ArrayList<VehicleIdsReq>();
	        	totalGroup.addAll(filterAdult)	;
	        	totalGroup.addAll(filterOthers);
	        	List<Integer> groupIds = totalGroup.stream().map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
	        	
	        	// Filte Count
	        	 for (Integer vehId :  groupIds ) {
					 List<EserviceTravelGroupDetails> filterGroup = groupData.stream().filter( o -> o.getGroupId().equals(vehId) ).collect(Collectors.toList());				 
					 List<String> sectionId = req.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(vehId)).map(VehicleIdsReq :: getSectionId   ).collect(Collectors.toList());	
				//	 for (int i=0 ; i < filterGroup.get(0).getGrouppMembers() ; i++) {
						 passCount = passCount + 1 ;
						 threadCount = threadCount +  2 ;
						
		            	 QuoteThreadReq request2 = new QuoteThreadReq();
		            	 request2.setVehicleId(vehId);
		            	 request2.setCustomerId(request.getCustomerId());
		            	 request2.setProductId(request.getProductId());
		            	 request2.setQuoteNo(request.getQuoteNo());
		            	 request2.setRequestReferenceNo(request.getRequestReferenceNo());
		            	 request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
		            	 request2.setVehicleIdsList(request.getVehicleIdsList());
		            	 request2.setCreatedBy(request.getCreatedBy());
		            	 request2.setGroupId(filterGroup.get(0).getGroupId());
		            	 request2.setGroupCount(filterGroup.get(0).getGrouppMembers());
		            	 request2.setSectionId(sectionId.get(0));
		            	 request2.setPolicyStartDate(request.getPolicyStartDate());
			             request2.setPolicyEndDate(request.getPolicyEndDate());
			             request2.setEffetiveDate(request.getEffetiveDate());
			             request2.setNoOfDays(request.getNoOfDays());
			         	 request2.setEndtType(request.getEndtType());
		            	 request2.setEndtCount(request.getEndtCount());
		            	 request2.setEndtFields(request.getEndtFields());	
		            	 request2.setMotorYn(request.getMotorYn());
		            	 request2.setIndividualId(passCount);
		            	 request2.setIsFinYn(request.getIsFinYn());
		            	 request2.setLocationId(1);
		            	 request2.setMotorDetailsList(request.getMotorDetailsList());
		            	 request.setSectionId(sectionId.get(0));
		            	 request.setGroupId(filterGroup.get(0).getGroupId());
		            	 
						 QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
									, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
								    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
						 coverQueue.add(coverSave);
					// }					 
		         } 
	        	 // 99999 Covers
	        	 {
	        		 threadCount = threadCount + 1;
	        		 QuoteThreadReq request2 = new QuoteThreadReq();
	            	 request2.setVehicleId(99999);
	            	 request2.setCustomerId(request.getCustomerId());
	            	 request2.setProductId(request.getProductId());
	            	 request2.setQuoteNo(request.getQuoteNo());
	            	 request2.setRequestReferenceNo(request.getRequestReferenceNo());
	            	 request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
	            	 request2.setVehicleIdsList(request.getVehicleIdsList());
	            	 request2.setCreatedBy(request.getCreatedBy());
	            	 request2.setGroupId(99999);
	            	 request2.setGroupCount(1);
	            	 request2.setSectionId("99999");
	            	 request2.setPolicyStartDate(request.getPolicyStartDate());
		             request2.setPolicyEndDate(request.getPolicyEndDate());
		             request2.setEffetiveDate(request.getEffetiveDate());
		             request2.setNoOfDays(request.getNoOfDays());
		         	 request2.setEndtType(request.getEndtType());
	            	 request2.setEndtCount(request.getEndtCount());
	            	 request2.setEndtFields(request.getEndtFields());	
	            	 request2.setMotorYn(request.getMotorYn());
	            	 request2.setIndividualId(passCount);
	            	 request2.setIsFinYn(request.getIsFinYn());
	            	 request2.setLocationId(1);
	            	 request2.setMotorDetailsList(request.getMotorDetailsList());
	            	 QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
								, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
							    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
					 coverQueue.add(coverSave);
	        	 }
	        	
	        	 
	        	 QuoteThreadCall travelSave = new QuoteThreadCall("TravelSave" , request , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
	     				, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
	     			    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
	          productQueue.add(travelSave);
	        	 
	        	 EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
	        	 if( travelData.getPlanTypeId().equals(3) ){
	        		// Kids Passenger Details Insert Frame  
	        		 List<EserviceTravelGroupDetails> filterGroup = groupData.stream().filter( o -> o.getGroupId().equals(1) ).collect(Collectors.toList());
	        		 if(filterGroup.size()>0 ) {
	        		//	 for (int i=0 ; i < filterGroup.get(0).getGrouppMembers() ; i++) {
	    					 passCount = passCount + 1 ;
	    					 threadCount = threadCount +  1 ;
	    					
	    	            	 QuoteThreadReq request2 = new QuoteThreadReq();
	    	            	 request2.setVehicleId(filterGroup.get(0).getGroupId());
	    	            	 request2.setCustomerId(request.getCustomerId());
	    	            	 request2.setProductId(request.getProductId());
	    	            	 request2.setQuoteNo(request.getQuoteNo());
	    	            	 request2.setRequestReferenceNo(request.getRequestReferenceNo());
	    	            	 request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
	    	            	 request2.setVehicleIdsList(request.getVehicleIdsList());
	    	            	 request2.setCreatedBy(request.getCreatedBy());
	    	            	 request2.setGroupId(filterGroup.get(0).getGroupId());
	    	            	 request2.setGroupCount(filterGroup.get(0).getGrouppMembers());
	    	            	 request2.setSectionId(travelData.getSectionId());
	    	            	 request2.setPolicyStartDate(request.getPolicyStartDate());
	    		             request2.setPolicyEndDate(request.getPolicyEndDate());
	    		             request2.setEffetiveDate(request.getEffetiveDate());
	    		             request2.setNoOfDays(request.getNoOfDays());
	    		         	 request2.setEndtType(request.getEndtType());
	    	            	 request2.setEndtCount(request.getEndtCount());
	    	            	 request2.setEndtFields(request.getEndtFields());	
	    	            	 request2.setMotorYn(request.getMotorYn());
	    	            	 request2.setIndividualId(passCount);
	    	            	 request2.setIsFinYn(request.getIsFinYn());
	    	            	 request2.setLocationId(1);
	    	            	 request2.setMotorDetailsList(request.getMotorDetailsList());
//	    	            	 QuoteThreadCall travelSave = new QuoteThreadCall("TravelSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
//	    	            				, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
//	    	            			    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo );
//	    		             queue.add(travelSave);
	    					
	    			//	 }	
	        		 }
	        		
	        	 }
	        	 ProductThreadRes.setProductQueue(productQueue);
	        	 ProductThreadRes.setCoverQueue(coverQueue);
	        	 ProductThreadRes.setThreadCount(threadCount);
			}
        	
        	 
        	
 	        
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
		}
		return ProductThreadRes ;
	}
	
	public ProductThreadRes buildingProductThreadCall(NewQuoteReq req , QuoteThreadReq request ) {
		ProductThreadRes ProductThreadRes = new ProductThreadRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<Callable<Object>> productQueue = new ArrayList<Callable<Object>>();
			List<Callable<Object>> coverQueue = new ArrayList<Callable<Object>>();
			List<VehicleIdsReq> vList=new ArrayList<VehicleIdsReq>();
			List<VehicleIdsReq> allCovers = new ArrayList<VehicleIdsReq>();
			List<VehicleIdsReq> optedCovers = new ArrayList<VehicleIdsReq>();	
			Boolean isRemove=false;
			List<Integer> locationyn=new ArrayList<Integer>();
			if((Objects.equals(request.getEndtType(),"852")) || (Objects.equals(request.getEndtType(),"891"))) {
				allCovers.addAll(request.getVehicleIdsList());
				optedCovers.addAll(request.getVehicleIdsList());
				
				List<FactorRateRequestDetails> removedCovers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOpt(request.getRequestReferenceNo(),0,0,"N");
				Set<String> seen = new HashSet<>();
				locationyn = removedCovers.stream()
				        .map(FactorRateRequestDetails::getLocationId)
				        .distinct()
				        .collect(Collectors.toList());
				removedCovers = removedCovers.stream() 
				    .filter(r -> {
				        String key = r.getSectionId() + "-" + r.getVehicleId() + "-" + r.getLocationId() + "-" + r.getCoverId();
				        return seen.add(key);
				    })
				    .collect(Collectors.toList());
				
				removedCovers.forEach(r -> {
				    // Create new instances
				    CoverIdsReq c = new CoverIdsReq();
				    VehicleIdsReq v = new VehicleIdsReq();

				    // Set properties from 'r'
				    v.setVehicleId(r.getVehicleId());
				    v.setLocationId(r.getLocationId());
				    v.setSectionId(String.valueOf(r.getSectionId()));

				    c.setCoverId(r.getCoverId());
				    c.setSubCoverYn(r.getSubCoverYn());
				    
				    // Add cover to vehicle
				    List<CoverIdsReq> coverList = new ArrayList<>();
				    coverList.add(c);
				    v.setCoverIdList(coverList);

				    // Add vehicle to all required lists
				    vList.add(v);
				    req.getVehicleIdsList().add(v);
				    allCovers.add(v);
				});	
				isRemove=true;
			}
			int threadCount = 1 ;
			request.setGroupId(1);
			request.setVehicleId(1);
			List<Integer> locationIds = req.getVehicleIdsList().stream().map(VehicleIdsReq :: getLocationId).collect(Collectors.toList());
			locationIds=locationIds.stream().distinct().collect(Collectors.toList());
			System.out.println("Total Location Id  :"+locationIds);
			for(Integer locId :locationIds) {
				System.out.println("Loop Location Id  :"+locId);
			List<Integer> vehicleIds = req.getVehicleIdsList().stream().filter(l -> l.getLocationId().equals(locId)).map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
			vehicleIds.removeIf ( o -> o.equals(1)  );
			vehicleIds.add(1);
			vehicleIds = vehicleIds.stream().distinct().collect(Collectors.toList());
			List<EserviceSectionDetails> sectionList = new ArrayList<>();
			if(req.getGetSectionDatas()!=null && req.getGetSectionDatas().isEmpty())
			{
				sectionList=req.getGetSectionDatas();
			}
			else
			{
				sectionList = eserSecRepo.findByRequestReferenceNoOrderBySectionIdAsc(request.getRequestReferenceNo());
			}
					
					
		//	if(Objects.equals(request.getEndtType(),"842")) {
		//		sectionList=sectionList.stream().filter(s-> "D".equalsIgnoreCase(s.getStatus())).collect(Collectors.toList());
		//		}
		//		else
		//		{
					sectionList=sectionList.stream().filter(s-> !"D".equalsIgnoreCase(s.getStatus())).collect(Collectors.toList());
		//		}
			//sectionList=sectionList.stream().filter(s-> !"D".equalsIgnoreCase(s.getStatus())).collect(Collectors.toList());
			System.out.println("Total Vehicle Id  :"+vehicleIds);
			
			for (Integer vehId :  vehicleIds ) {
				System.out.println("Loop Vehicle Id  :"+vehId);
	            	List<String> sectionId = req.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(vehId) && o.getLocationId().equals(locId)).map(VehicleIdsReq :: getSectionId   ).collect(Collectors.toList());
	            	sectionId = sectionId.stream().distinct().collect(Collectors.toList());
//	            	if(vehId.equals(1) ) {
//	            		sectionId.add("0");
//	            	}
	            	System.out.println("Total Section Id  :"+sectionId);
	            	for ( String sec : sectionId) {
	            		System.out.println("Loop Section Id  :"+sectionId);
	            		List<EserviceSectionDetails> activeSection = sectionList.stream().filter( o -> o.getSectionId().equals(sec)).collect(Collectors.toList());
		    			if("0".equalsIgnoreCase(sec) ||  activeSection.size() > 0 ) {
		    				EserviceSectionDetails secData = activeSection.size() > 0 ? activeSection.get(0) : new EserviceSectionDetails();
		    				
		    				
		    				QuoteThreadReq request2 = new QuoteThreadReq();
			            	request2.setCustomerId(request.getCustomerId());
			            	request2.setProductId(request.getProductId());
			            	request2.setQuoteNo(request.getQuoteNo());
			            	request2.setRequestReferenceNo(request.getRequestReferenceNo());
			            	request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
			            	request2.setVehicleIdsList(request.getVehicleIdsList());
			            	request2.setCreatedBy(request.getCreatedBy());
			            	request2.setVehicleId(vehId);
			            	request2.setLocationId(locId);
			            	request2.setSectionId(sec);	
			            	request2.setPolicyStartDate(request.getPolicyStartDate());
			            	request2.setPolicyEndDate(request.getPolicyEndDate());
			            	request2.setEffetiveDate(request.getEffetiveDate());
			            	request2.setNoOfDays(request.getNoOfDays());
			            	request2.setEndtType(request.getEndtType());
			            	request2.setEndtCount(request.getEndtCount());
			            	if(Objects.equals(request.getEndtType(),"891"))
			            			{
			            		boolean contains = locationyn.contains(locId);
			            		request2.setEndtFields(!contains?request.getEndtFields():"N");
			            			}else
			            			{
			            				request2.setEndtFields(request.getEndtFields());
			            			}
			            	
			            	request2.setMotorYn(request.getMotorYn());
			            	request2.setIsFinYn(request.getIsFinYn());
							request2.setMotorDetailsList(request.getMotorDetailsList());

			            	if("0".equalsIgnoreCase(sec)  ) {
		            			threadCount = threadCount +  1 ;
			            	
		            		} else {
		            			if(isRemove) {
		            				boolean b= optedCovers.stream().map(s-> s.getSectionId()).anyMatch(s-> sec.equalsIgnoreCase(s));
		            				if(b) {
		            					QuoteThreadReq request3 = new QuoteThreadReq();
			            				mapper.map(request2, request3);
			            				request3.setVehicleIdsList(optedCovers);
			            				QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request3 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
						            			, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
						            		    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
										coverQueue.add(coverSave);
										threadCount = threadCount +  2 ;
		            				}	
		            			}else {

		            			QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
				            			, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
				            		    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
								coverQueue.add(coverSave);
								threadCount = threadCount +  2 ;
		            			}
		            		}
			            	
			            	if((!"0".equalsIgnoreCase(sec)) && "H".equalsIgnoreCase(secData.getProductType()) ) {
			            		if(isRemove) {
		            				request2.setVehicleIdsList(allCovers);
		            			}
			            		QuoteThreadCall humanSave = new QuoteThreadCall("CommonDataSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo  
				    					, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
				    				    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
				                productQueue.add(humanSave);
				                
				        	} else {
				        		if(isRemove) {
		            				request2.setVehicleIdsList(allCovers);
		            			}
		    					QuoteThreadCall buildingSave = new QuoteThreadCall("BuildingSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo  
				    					, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
				    				    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
		    					productQueue.add(buildingSave);
				                
				            }
			            	
		    			}
	            	
	            	}
	            }
			}
				 // 99999 Covers
		       	 {
		       		System.out.println("99999 Covers");
		       		threadCount = threadCount + 1;
		       		 QuoteThreadReq request2 = new QuoteThreadReq();
		           	 request2.setVehicleId(99999);
		           	 request2.setCustomerId(request.getCustomerId());
		           	 request2.setProductId(request.getProductId());
		           	 request2.setQuoteNo(request.getQuoteNo());
		           	 request2.setRequestReferenceNo(request.getRequestReferenceNo());
		           	 request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
		           	 request2.setVehicleIdsList(request.getVehicleIdsList());
		           	 request2.setCreatedBy(request.getCreatedBy());
		           	 request2.setGroupId(99999);
		           	 request2.setGroupCount(1);
		           	 request2.setSectionId("99999");
		           	 request2.setPolicyStartDate(request.getPolicyStartDate());
			             request2.setPolicyEndDate(request.getPolicyEndDate());
			             request2.setEffetiveDate(request.getEffetiveDate());
			             request2.setNoOfDays(request.getNoOfDays());
			         	 request2.setEndtType(request.getEndtType());
		           	 request2.setEndtCount(request.getEndtCount());
		           	 request2.setEndtFields(request.getEndtFields());	
		           	 request2.setMotorYn(request.getMotorYn());
		           	 request2.setIsFinYn(request.getIsFinYn());
		           	request2.setLocationId(1);
		           	 QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
									, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
								    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo,dozerMapper );
						 coverQueue.add(coverSave);
		       	 }
		       	 
			 	ProductThreadRes.setProductQueue(productQueue);
			 	ProductThreadRes.setCoverQueue(coverQueue);
			 	ProductThreadRes.setThreadCount(threadCount);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
		}
		return ProductThreadRes ;
	}
	
	public ProductThreadRes commonProductThreadCall(NewQuoteReq req , QuoteThreadReq request ) {
		ProductThreadRes ProductThreadRes = new ProductThreadRes();
		
		try {
			
			int threadCount = 0 ;
			List<Callable<Object>> productQueue = new ArrayList<Callable<Object>>();
			List<Callable<Object>> coverQueue = new ArrayList<Callable<Object>>();
			List<VehicleIdsReq> vList=new ArrayList<VehicleIdsReq>();
			List<VehicleIdsReq> allCovers = new ArrayList<VehicleIdsReq>();
			List<VehicleIdsReq> optedCovers = new ArrayList<VehicleIdsReq>();	
			Boolean isRemove=false;
			List<Integer> locationyn=new ArrayList<Integer>();
			if(Objects.equals(request.getEndtType(),"852") || Objects.equals(request.getEndtType(),"891")) {
				allCovers.addAll(request.getVehicleIdsList());
				optedCovers.addAll(request.getVehicleIdsList());
				List<FactorRateRequestDetails> removedCovers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOpt(request.getRequestReferenceNo(),0,0,"N");
				Set<String> seen = new HashSet<>();
				locationyn = removedCovers.stream()
				        .map(FactorRateRequestDetails::getLocationId)
				        .distinct()
				        .collect(Collectors.toList());
				removedCovers = removedCovers.stream() 
				    .filter(r -> {
				        String key = r.getSectionId() + "-" + r.getVehicleId() + "-" + r.getLocationId() + "-" + r.getCoverId();
				        return seen.add(key);
				    })
				    .collect(Collectors.toList());
				
				
				removedCovers.forEach(r -> {
				    // Create new instances
				    CoverIdsReq c = new CoverIdsReq();
				    VehicleIdsReq v = new VehicleIdsReq();

				    // Set properties from 'r'
				    v.setVehicleId(r.getVehicleId());
				    v.setLocationId(r.getLocationId());
				    v.setSectionId(String.valueOf(r.getSectionId()));

				    c.setCoverId(r.getCoverId());
				    c.setSubCoverYn(r.getSubCoverYn());

				    // Add cover to vehicle
				    List<CoverIdsReq> coverList = new ArrayList<>();
				    coverList.add(c);
				    v.setCoverIdList(coverList);

				    // Add vehicle to all required lists
				    vList.add(v);
				    req.getVehicleIdsList().add(v);
				    allCovers.add(v);
				});	
				isRemove=true;
			}
			 threadCount = 1 ;
			request.setGroupId(1);
			request.setVehicleId(1);
			List<Integer> locationIds = req.getVehicleIdsList().stream().map(VehicleIdsReq :: getLocationId).collect(Collectors.toList());
			locationIds=locationIds.stream().distinct().collect(Collectors.toList());
			System.out.println("Total Location Id  :"+locationIds);
			
			for(Integer locId :locationIds)  {
			List<Integer> vehicleIds = req.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
//				List<Integer> vehicleIds = req.getVehicleIdsList().stream().filter(l -> l.getLocationId().equals(locId)).map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
				List<EserviceCommonDetails> commonDatas = eserCommonRepo.findByRequestReferenceNoAndStatusNotAndRiskIdInOrderByRiskIdAsc(req.getRequestReferenceNo(),"D",vehicleIds );
			List<Integer> activeVehicleIds = commonDatas.stream().filter(l -> l.getLocationId().equals(locId)).map(EserviceCommonDetails :: getRiskId).collect(Collectors.toList());
			activeVehicleIds = activeVehicleIds.stream().distinct().collect(Collectors.toList());
			for (Integer vehId : activeVehicleIds) {
				threadCount = threadCount + 2;
				List<String> sectionId = req.getVehicleIdsList().stream().filter(o -> o.getVehicleId().equals(vehId)).map(VehicleIdsReq::getSectionId).collect(Collectors.toList());
				sectionId = sectionId.stream().distinct().collect(Collectors.toList());
				for (String sec : sectionId) {
					QuoteThreadReq request2 = new QuoteThreadReq();
					request2.setCustomerId(request.getCustomerId());
					request2.setProductId(request.getProductId());
					request2.setQuoteNo(request.getQuoteNo());
					request2.setRequestReferenceNo(request.getRequestReferenceNo());
					request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
					request2.setVehicleIdsList(request.getVehicleIdsList());
					request2.setCreatedBy(request.getCreatedBy());
					request2.setVehicleId(vehId);
					request2.setLocationId(locId);
					request2.setSectionId(sec);
					request2.setPolicyStartDate(request.getPolicyStartDate());
	            	request2.setPolicyEndDate(request.getPolicyEndDate());
	            	request2.setEffetiveDate(request.getEffetiveDate());
	            	request2.setNoOfDays(request.getNoOfDays());
	            	request2.setEndtType(request.getEndtType());
	            	request2.setEndtCount(request.getEndtCount());
	            	if(Objects.equals(request.getEndtType(),"891"))
        			{
        		boolean contains = locationyn.contains(locId);
        		request2.setEndtFields(!contains?request.getEndtFields():"N");
        			}else
        			{
        				request2.setEndtFields(request.getEndtFields());
        			}
        	
	            	request2.setMotorYn(request.getMotorYn());
	            	request2.setIsFinYn(request.getIsFinYn());
	            	request2.setMotorDetailsList(request.getMotorDetailsList());
					QuoteThreadCall commonDataSave = new QuoteThreadCall("CommonDataSave", request2, em,eserCustRepo, eserMotRepo, facRateRepo, perInfoRepo, motorRepo,driverRepo , coverRepo, homeRepo,eserRepo,
							 eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
						    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
					productQueue.add(commonDataSave);
					QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave", request2, em, eserCustRepo,eserMotRepo, facRateRepo, perInfoRepo, motorRepo,driverRepo , coverRepo, homeRepo, eserRepo,
							 eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
						    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
					coverQueue.add(coverSave);
				}
			}
		}
			
			 // 99999 Covers
	       	 {
	       		threadCount = threadCount + 1;
	       		 QuoteThreadReq request2 = new QuoteThreadReq();
	           	 request2.setVehicleId(99999);
	           	 request2.setCustomerId(request.getCustomerId());
	           	 request2.setProductId(request.getProductId());
	           	 request2.setQuoteNo(request.getQuoteNo());
	           	 request2.setRequestReferenceNo(request.getRequestReferenceNo());
	           	 request2.setEndtPrevQuoteNo(request.getEndtPrevQuoteNo());
	           	 request2.setVehicleIdsList(request.getVehicleIdsList());
	           	 request2.setCreatedBy(request.getCreatedBy());
	           	 request2.setGroupId(99999);
	           	 request2.setGroupCount(1);
	           	request2.setLocationId(1);
	           	 request2.setSectionId("99999");
	           	 request2.setPolicyStartDate(request.getPolicyStartDate());
		             request2.setPolicyEndDate(request.getPolicyEndDate());
		             request2.setEffetiveDate(request.getEffetiveDate());
		             request2.setNoOfDays(request.getNoOfDays());
		         	 request2.setEndtType(request.getEndtType());
	           	 request2.setEndtCount(request.getEndtCount());
	           	 request2.setEndtFields(request.getEndtFields());	
	           	 request2.setMotorYn(request.getMotorYn());
	           	 request2.setIsFinYn(request.getIsFinYn());
	           	request2.setMotorDetailsList(request.getMotorDetailsList());
	           	 QuoteThreadCall coverSave = new QuoteThreadCall("CoverSave" , request2 , em , eserCustRepo ,eserMotRepo  ,facRateRepo  ,perInfoRepo  , motorRepo ,driverRepo ,coverRepo 
								, homeRepo , eserRepo , eserGroupRepo ,traPassRepo ,traPassHisRepo ,travelProductId,eserBuildRepo,eserSecRepo,eserCommonRepo,commonDataRepo,secRepo,buildRepo , docRepo
							    , locRepo , contentRepo , pacRepo , docUniqueRepo , docTranRepo,pacRepo,eservicedriverRepo,msDriverRepo ,dozerMapper);
					 coverQueue.add(coverSave);
	       	 }
	       	 
			ProductThreadRes.setProductQueue(productQueue);
			ProductThreadRes.setCoverQueue(coverQueue);
			ProductThreadRes.setThreadCount(threadCount);	
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " +  e.getMessage());
		}
		return ProductThreadRes ;
	}
	public CommonRes setQuoteThreadReq(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
	//	SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
			// Id Generate
			String customerId = "" ;
			String companyId = "" ;
			String quoteNo  = "" ;
			String subUserType  = "" ;
			String endtPrevQuoteNo  = "" ;
			Date policyStartDate = null ;
			Date policyEndDate = null ;
			Date effectiveDate = null ;
			String noOfDays = "" ;
			String endtType = "" ;
			String endtCount = "" ;
			String endtFields = "" ;
			String originalPolicyNo = "" ;
			String isFinYn = "" ;
			Integer locationId=1;
			BigDecimal sumInsured=BigDecimal.ZERO;
			
			DecimalFormat df = new DecimalFormat("####");
			// Find Old QuoteNo
			 if( req.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
				 EserviceTravelDetails data = new  EserviceTravelDetails (); 
				 if(req.getTravelDatas()!=null) {
					 data =  eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
				 }else
				 {
					 data= req.getTravelDatas();
				 }
					customerId = data.getCustomerId()==null?"":data.getCustomerId();
					companyId    = data.getCompanyId()==null?"":data.getCompanyId();
					quoteNo    = data.getQuoteNo()==null?"":data.getQuoteNo();
					subUserType = data.getSubUserType()==null?"":data.getSubUserType() ;
					endtPrevQuoteNo  = data.getEndtPrevQuoteNo()==null?"":data.getEndtPrevQuoteNo() ;
					policyStartDate  = data.getTravelStartDate()==null?null: data.getTravelStartDate() ;
					policyEndDate    = data.getTravelEndDate()==null?null: data.getTravelEndDate() ;
					effectiveDate    = data.getEndorsementEffdate()==null?null: data.getEndorsementEffdate() ;
					noOfDays		 = data.getTravelCoverDuration()==null?null: data.getTravelCoverDuration().toString();
					endtType		 = data.getEndorsementType()==null?"": data.getEndorsementType().toString() ;
					endtCount		 = data.getEndtCount()==null?"0": df.format(Double.valueOf(data.getEndtCount().toPlainString())) ;
					originalPolicyNo = data.getOriginalPolicyNo();
					isFinYn 	 = data.getIsFinaceYn()==null ? "N" :data.getIsFinaceYn() ;
					
			} else if( req.getMotorYn().equalsIgnoreCase("M") ) {
				
//				Integer vehicleId = req.getVehicleIdsList().get(0).getVehicleId();
//				String key = req.getRequestReferenceNo() + "_" + vehicleId;
//				List<EserviceMotorDetails> motorList = req.getMotorDetailsMap().get(key);
//				EserviceMotorDetails data = (motorList != null && !motorList.isEmpty()) ? motorList.get(0) : null;
//				
//				//EserviceMotorDetails data =  eserMotRepo.findByRequestReferenceNoAndRiskId(req.getRequestReferenceNo() , req.getVehicleIdsList().get(0).getVehicleId());
//				customerId = data.getCustomerId()==null?"":data.getCustomerId();
//				companyId    = data.getCompanyId()==null?"":data.getCompanyId();
//				quoteNo    = data.getQuoteNo()==null?"":data.getQuoteNo();
//				subUserType = data.getSubUserType()==null?"":data.getSubUserType() ;
//				endtPrevQuoteNo  = data.getEndtPrevQuoteNo()==null?"":data.getEndtPrevQuoteNo() ;
//				policyStartDate  = data.getPolicyStartDate()==null?null: data.getPolicyStartDate() ;
//				policyEndDate    = data.getPolicyEndDate()==null?null: data.getPolicyEndDate() ;
//				effectiveDate    = data.getEndorsementEffdate()==null?null: data.getEndorsementEffdate() ;
//				noOfDays		 = data.getPeriodOfInsurance()==null?null: data.getPeriodOfInsurance() ;
//				endtType		 = data.getEndorsementType()==null?"": data.getEndorsementType().toString() ;
//				endtCount		 = data.getEndtCount()==null?"0": df.format(Double.valueOf(data.getEndtCount().toPlainString()))  ;
//				originalPolicyNo = data.getOriginalPolicyNo();
//				isFinYn 	 = data.getIsFinaceYn()==null ? "N" :data.getIsFinaceYn() ;
//				locationId=data.getLocationId()==null?1:	data.getLocationId();	
//				sumInsured=data.getSumInsuredLc();
//				commonRes.setMotorDetailsList(motorList);
				
				List<EserviceMotorDetails> allMotorDetails = new ArrayList<>();

				for (VehicleIdsReq vehicleReq : req.getVehicleIdsList()) {

				    Integer vehicleId = vehicleReq.getVehicleId();
				    String key = req.getRequestReferenceNo() + "_" + vehicleId;

				    List<EserviceMotorDetails> motorList = req.getMotorDetailsMap().get(key);

				    if (motorList == null || motorList.isEmpty()) {
				        continue; 
				    }

				    EserviceMotorDetails data = motorList.get(0);

				    customerId        = data.getCustomerId() == null ? "" : data.getCustomerId();
				    companyId         = data.getCompanyId() == null ? "" : data.getCompanyId();
				    quoteNo           = data.getQuoteNo() == null ? "" : data.getQuoteNo();
				    subUserType       = data.getSubUserType() == null ? "" : data.getSubUserType();
				    endtPrevQuoteNo   = data.getEndtPrevQuoteNo() == null ? "" : data.getEndtPrevQuoteNo();
				    policyStartDate   = data.getPolicyStartDate();
				    policyEndDate     = data.getPolicyEndDate();
				    effectiveDate     = data.getEndorsementEffdate();
				    noOfDays          = data.getPeriodOfInsurance();
				    endtType          = data.getEndorsementType() == null ? "" : data.getEndorsementType().toString();
				    endtCount         = data.getEndtCount() == null ? "0"
				                        : df.format(Double.valueOf(data.getEndtCount().toPlainString()));
				    originalPolicyNo  = data.getOriginalPolicyNo();
				    isFinYn           = data.getIsFinaceYn() == null ? "N" : data.getIsFinaceYn();
				    locationId        = data.getLocationId() == null ? 1 : data.getLocationId();
				    sumInsured        = data.getSumInsuredLc();

				    allMotorDetails.addAll(motorList);
				    commonRes.setMotorDetailsList(allMotorDetails);

				}

			} else if( req.getMotorYn().equalsIgnoreCase("A")) {
				List<EserviceBuildingDetails> datas = new ArrayList<>();
				if(req.getBuildDatas()!=null && !req.getBuildDatas().isEmpty()) {
					 datas =  req.getBuildDatas();
				}
				else
				{
					datas =  eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
					req.setBuildDatas(datas);				
					}
				if(!datas.isEmpty()) {
					List<String> quoteNoList = datas.stream().map(d-> d.getQuoteNo()).filter(Objects::nonNull).distinct().collect(Collectors.toList());
					if(quoteNoList.size()>=1)
						quoteNo=quoteNoList.get(0);
				
				EserviceBuildingDetails data = datas.get(0);
				customerId = data.getCustomerId()==null?"": data.getCustomerId();
				companyId    = data.getCompanyId()==null?"":data.getCompanyId();
				quoteNo    =  StringUtils.isBlank(data.getQuoteNo())?StringUtils.isBlank(quoteNo)?"":quoteNo: data.getQuoteNo();
				subUserType =  data.getSubUserType()==null?"": data.getSubUserType() ;
				endtPrevQuoteNo  = data.getEndtPrevQuoteNo()==null?"":data.getEndtPrevQuoteNo() ;
				policyStartDate  = data.getPolicyStartDate()==null?null: data.getPolicyStartDate() ;
				policyEndDate    = data.getPolicyEndDate()==null?null: data.getPolicyEndDate() ;
				effectiveDate    = data.getEndorsementEffdate()==null?null : data.getEndorsementEffdate() ;
				noOfDays		 = data.getPolicyPeriord()==null?null: data.getPolicyPeriord().toString() ;
				endtType		 = data.getEndorsementType()==null?"": data.getEndorsementType().toString() ;
				endtCount		 = data.getEndtCount()==null?"0": df.format(Double.valueOf(data.getEndtCount().toPlainString()))  ;
				originalPolicyNo = data.getOriginalPolicyNo();
				isFinYn 	 = data.getIsFinyn()==null ? "N" :data.getIsFinyn() ;
				}else {
					List<EserviceCommonDetails> datas1 = new ArrayList<>();
					if(req.getHumanDatas()!=null && !req.getHumanDatas().isEmpty()) {
						datas1 = req.getHumanDatas();
					}
					else
					{
						datas1 =  eserCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
						req.setHumanDatas(datas1);			
						}

			//		List<EserviceCommonDetails> datas1 =  eserCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
					if(!datas1.isEmpty()) {
						List<String> quoteNoList = datas1.stream().map(d-> d.getQuoteNo()).filter(Objects::nonNull).distinct().collect(Collectors.toList());
						if(quoteNoList.size()>=1)
							quoteNo=quoteNoList.get(0);
					
					EserviceCommonDetails data = datas1.get(0) ;
					customerId = data.getCustomerId()==null?"": data.getCustomerId();
					companyId    = data.getCompanyId()==null?"":data.getCompanyId();
					quoteNo    =  StringUtils.isBlank(data.getQuoteNo())?StringUtils.isBlank(quoteNo)?"":quoteNo: data.getQuoteNo();
					LoginMaster loginData = loginRepo.findByLoginId(data.getLoginId());
					subUserType =  loginData.getSubUserType()==null?"": loginData.getSubUserType() ;
					endtPrevQuoteNo  = data.getEndtPrevQuoteNo()==null?"":data.getEndtPrevQuoteNo() ;
					policyStartDate  = data.getPolicyStartDate()==null?null: data.getPolicyStartDate() ;
					policyEndDate    = data.getPolicyEndDate()==null?null: data.getPolicyEndDate() ;
					policyEndDate    = data.getPolicyEndDate()==null?null: data.getPolicyEndDate() ;
					effectiveDate    = data.getEndorsementEffdate()==null?null : data.getEndorsementEffdate() ;
					noOfDays		 = data.getPolicyPeriod()==null?null: data.getPolicyPeriod().toString() ;
					endtType		 = data.getEndorsementType()==null?"": data.getEndorsementType().toString() ;
					endtCount		 = data.getEndtCount()==null?"0": df.format(Double.valueOf(data.getEndtCount().toPlainString()))  ;
					originalPolicyNo = data.getOriginalPolicyNo();
					isFinYn 	 = data.getIsFinaceYn()==null ? "N" :data.getIsFinaceYn() ;
					}
				}
			} else {
				List<EserviceCommonDetails> datas = new ArrayList<>();
				if(req.getHumanDatas()!=null && !req.getHumanDatas().isEmpty()) {
					datas = req.getHumanDatas();
				}
				else
				{
					datas =  eserCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
					req.setHumanDatas(datas);			
					}

	//			List<EserviceCommonDetails> datas =  eserCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
				if(!datas.isEmpty()) {
					List<String> quoteNoList = datas.stream().map(d-> d.getQuoteNo()).filter(Objects::nonNull).distinct().collect(Collectors.toList());
					if(quoteNoList.size()>=1)
						quoteNo=quoteNoList.get(0);
				}
				EserviceCommonDetails data = datas.get(0) ;
				customerId = data.getCustomerId()==null?"": data.getCustomerId();
				companyId    = data.getCompanyId()==null?"":data.getCompanyId();
				quoteNo    =  StringUtils.isBlank(data.getQuoteNo())?StringUtils.isBlank(quoteNo)?"":quoteNo: data.getQuoteNo();
				LoginMaster loginData = loginRepo.findByLoginId(data.getLoginId());
				subUserType =  loginData.getSubUserType()==null?"": loginData.getSubUserType() ;
				endtPrevQuoteNo  = data.getEndtPrevQuoteNo()==null?"":data.getEndtPrevQuoteNo() ;
				policyStartDate  = data.getPolicyStartDate()==null?null: data.getPolicyStartDate() ;
				policyEndDate    = data.getPolicyEndDate()==null?null: data.getPolicyEndDate() ;
				policyEndDate    = data.getPolicyEndDate()==null?null: data.getPolicyEndDate() ;
				effectiveDate    = data.getEndorsementEffdate()==null?null : data.getEndorsementEffdate() ;
				noOfDays		 = data.getPolicyPeriod()==null?null: data.getPolicyPeriod().toString() ;
				endtType		 = data.getEndorsementType()==null?"": data.getEndorsementType().toString() ;
				endtCount		 = data.getEndtCount()==null?"0": df.format(Double.valueOf(data.getEndtCount().toPlainString()))  ;
				originalPolicyNo = data.getOriginalPolicyNo();
				isFinYn 	 = data.getIsFinaceYn()==null ? "N" :data.getIsFinaceYn() ;
				
			}

				if("N".equalsIgnoreCase(req.getEmiYn())) {
							    if(StringUtils.isNotBlank(quoteNo)) {
							    	List<EmiTransactionDetails> list = emitransrepo.findByQuoteNoAndCompanyIdAndProductId(quoteNo,
											req.getInsuranceId(), req.getProductId());
									if (list.size() > 0 && StringUtils.isNotBlank(quoteNo)) {
										emitransrepo.deleteAll(list);
									}
							    }
							}
			
			// Get Endt Fields
			if(StringUtils.isNotBlank(endtType) ) {
				//List<EndtTypeMaster> endtList = 
				EndtTypeMaster endtList = ratingutil.getEndtMasterData(companyId, req.getProductId(), endtType); //getEndtMasterData(companyId ,  req.getProductId() ,  endtType ) ;
				if(endtList!=null ) {
					endtFields = endtList.getIsCoverendt();						
				}
				
			}
			
			// Quote No Generate
			if(StringUtils.isNotBlank( quoteNo) && (subUserType.equalsIgnoreCase("b2c")) ) {
				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(companyId);  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("3");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_ID");
	 		 	customerId = genSeqNoService.generateSeqCall(generateSeqReq); // "C-" + generateCustId();// idf.format(new Date()) + random ;
	 		 	
			 	generateSeqReq.setType("4");
	 		 	generateSeqReq.setTypeDesc("QUOTE_NO");
	 		 	quoteNo  = genSeqNoService.generateSeqCall(generateSeqReq); //"Q"+ generateQuoteNo();// idf.format(new Date()) + random ;
	      //  } else
			} else if (StringUtils.isBlank( quoteNo)  ) {
	       // 	Random rand = new Random();
	       //     int random=rand.nextInt(90)+10; 
				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(companyId);  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("3");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_ID");
	 		 	customerId = genSeqNoService.generateSeqCall(generateSeqReq); // "C-" + generateCustId();// idf.format(new Date()) + random ;
	 		 	
			 	generateSeqReq.setType("4");
	 		 	generateSeqReq.setTypeDesc("QUOTE_NO");
	 		 	quoteNo  = genSeqNoService.generateSeqCall(generateSeqReq); //"Q"+ generateQuoteNo();// idf.format(new Date()) + random ;
	        } 

			QuoteThreadReq request = new QuoteThreadReq();
            request.setCustomerId(customerId);
            request.setQuoteNo(quoteNo);
            request.setInsuranceId(companyId);
            request.setRequestReferenceNo(req.getRequestReferenceNo());
            request.setVehicleIdsList(req.getVehicleIdsList());
            request.setProductId(req.getProductId());
            request.setCreatedBy(req.getCreatedBy());
            request.setEndtPrevQuoteNo(endtPrevQuoteNo);
            request.setPolicyStartDate(policyStartDate);
            request.setPolicyEndDate(policyEndDate);
            request.setEffetiveDate(effectiveDate);
            request.setNoOfDays(noOfDays);
            request.setEndtType(endtType);
            request.setEndtCount(endtCount);
            request.setEndtFields(endtFields);
            request.setMotorYn(req.getMotorYn());
            request.setOriginalPolicyNo(originalPolicyNo);
            request.setCommissionModifyYn(req.getCommissionModifyYn());
            request.setCommissionPercent(req.getCommissionPercent());
            request.setIsFinYn(isFinYn);
            request.setSumInsured(sumInsured);
            request.setWhatsappPolicy(req.getWhatsappPolicy());
            
            commonRes.setCommonResponse(request);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(null);
			commonRes.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> "   );
			errors.add(new Error("01", "Common Error", e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");
		}
		return commonRes ;
}
	
	public CommonRes vehicleMultiThreadCall(NewQuoteReq req ) {
		CommonRes commonRes = new CommonRes();
		List<Error> errors = new ArrayList<Error>();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhmmssSS");
		try {
			// Id Generate
			String customerId = "" ;
			String quoteNo  = "" ;
			String subUserType  = "" ;
			
			// Find Old QuoteNo
			if(req.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
				EserviceTravelDetails findTravel =  eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
				customerId = findTravel.getCustomerId()==null?"":findTravel.getCustomerId();
				quoteNo    = findTravel.getQuoteNo()==null?"":findTravel.getQuoteNo();
				subUserType = findTravel.getSubUserType()==null?"":findTravel.getSubUserType() ;
			
			} else if(req.getMotorYn().equalsIgnoreCase("M")) {
				EserviceMotorDetails findMotor =  eserMotRepo.findByRequestReferenceNoAndRiskId(req.getRequestReferenceNo() , req.getVehicleIdsList().get(0).getVehicleId());
				customerId = findMotor.getCustomerId()==null?"":findMotor.getCustomerId();
				quoteNo    = findMotor.getQuoteNo()==null?"":findMotor.getQuoteNo();
				subUserType = findMotor.getSubUserType()==null?"":findMotor.getSubUserType() ;
			
			} else 
			
			// Quote No Generate
			if(StringUtils.isNotBlank( quoteNo) && (subUserType.equalsIgnoreCase("b2c")) ) {
				Random rand = new Random();
	            int random=rand.nextInt(90)+10; 
	        	customerId = "C-" + idf.format(new Date()) + random ;
	            quoteNo  = "Q"+ idf.format(new Date()) + random ;
	        } else if (StringUtils.isBlank( quoteNo)  ) {
	        	Random rand = new Random();
	            int random=rand.nextInt(90)+10; 
	        	customerId = "C-" + idf.format(new Date()) + random ;
	            quoteNo  = "Q"+ idf.format(new Date()) + random ;
	        } 

			QuoteThreadReq request = new QuoteThreadReq();
            request.setCustomerId(customerId);
            request.setQuoteNo(quoteNo);
            request.setRequestReferenceNo(req.getRequestReferenceNo());
            request.setVehicleIdsList(req.getVehicleIdsList());
            request.setProductId(req.getProductId());
            request.setCreatedBy(req.getCreatedBy());
            
			commonRes.setCommonResponse(request);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(null);
			commonRes.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> "   );
			errors.add(new Error("01", "Common Error", e.getMessage()));
			commonRes.setCommonResponse(null);
			commonRes.setIsError(true);
			commonRes.setErrorMessage(errors);
			commonRes.setMessage("Failed");
		}
		return commonRes ;
}
	
	public List<EndtTypeMaster> getEndtMasterData(String insuranceId, String productId, String endtTypeId ) {
		List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
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
					CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
					
					// Find All
					Root<EndtTypeMaster> c = query.from(EndtTypeMaster.class);

					// Select
					query.select(c);

					// Order By
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(cb.asc(c.get("endtTypeId")));

					// Effective Date Max Filter
					Subquery<Date> effectiveDate = query.subquery(Date.class);
					Root<EndtTypeMaster> ocpm1 = effectiveDate.from(EndtTypeMaster.class);
					effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
					jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("endtTypeId"), ocpm1.get("endtTypeId"));
					jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(cb.function("trunc", Date.class,ocpm1.get("effectiveDateStart")) , today );
					jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));
					jakarta.persistence.criteria.Predicate a4 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));

					effectiveDate.where(a1, a2, a3, a4);
					// Effective Date End Max Filter
					Subquery<Date> effectiveDate2 = query.subquery(Date.class);
					Root<EndtTypeMaster> ocpm2 = effectiveDate2.from(EndtTypeMaster.class);
					effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
					jakarta.persistence.criteria.Predicate a6 = cb.equal(c.get("endtTypeId"), ocpm2.get("endtTypeId"));
					jakarta.persistence.criteria.Predicate a7 = cb.equal(c.get("productId"), ocpm2.get("productId"));
					jakarta.persistence.criteria.Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
					jakarta.persistence.criteria.Predicate a10 = cb.greaterThanOrEqualTo(cb.function("trunc", Date.class,ocpm2.get("effectiveDateEnd")), todayEnd);
					effectiveDate2.where(a6, a7, a8, a10);

					// Where

					Predicate n1 = cb.equal(c.get("status"),"Y");
					Predicate n11 = cb.equal(c.get("status"),"R");
					Predicate n12 = cb.or(n1,n11);
					jakarta.persistence.criteria.Predicate n2 = cb.equal(cb.function("trunc", Date.class,c.get("effectiveDateStart")), effectiveDate);
					jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("endtTypeId"), endtTypeId);
					jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("productId"), productId);
					jakarta.persistence.criteria.Predicate n6 = cb.equal(c.get("companyId"), insuranceId);
					jakarta.persistence.criteria.Predicate n7 = cb.equal(cb.function("trunc", Date.class,c.get("effectiveDateEnd")), effectiveDate2);

					query.where(n12, n2, n3, n5, n6,n7).orderBy(orderList);

					// Get Result
					TypedQuery<EndtTypeMaster> result = em.createQuery(query);
					list = result.getResultList();

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return list;
			}
	
	public synchronized String generateQuoteNo() {
	       try {
	    	   SeqQuoteno entity;
	            entity = quoteNoRepo.save(new SeqQuoteno());          
	            return String.format("%05d",entity.getQuoteNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	
	 public synchronized String generateCustId() {
	       try {
	    	   SeqCustid entity;
	            entity = custIdRepo.save(new SeqCustid());          
	            return String.format("%05d",entity.getCustId()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	 
	/* public Object updateReferralStatus(NewQuoteReq NewQuoteReq , String token ) {
		 	Object PremiaRes = null;
		try {
			// Frame Tira Req

			RestTemplate temp = new RestTemplate();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_JSON);
			// header.setCharset("UTF-8");
			header.setBearerAuth(token);
			String url = referralNotiPushLink ;
			HttpEntity<?> requestent = new HttpEntity<>(NewQuoteReq, header);

			System.out.println(new Date() + " Start " + url);
			ResponseEntity<Object> postEntity = temp.exchange(url, HttpMethod.POST, requestent,new ParameterizedTypeReference<Object>() {}) ;
			
			//if(PremiaRes.getStatusCode()==HttpStatus.ACCEPTED) {
			PremiaRes = postEntity.getBody() ;
			//}		
				System.out.println("Premia Response --> "+PremiaRes);
			System.out.println(new Date() + " End " + url);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return PremiaRes;
	}
	 */
	 
	 
/*	 public QuoteUpdateRes updateReferralStatus(NewQuoteReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
			
				if( req.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
					//Mail Push Notification
					
//					updateRes= travelPushNotification(req);
					
				} else  if( req.getMotorYn().equalsIgnoreCase("M") ) {
					//Mail Push Notification
					
//					updateRes= motorPushNotification(req);
					
				} else if( req.getMotorYn().equalsIgnoreCase("A") ) {
					//Mail Push Notification
					
//					updateRes= buildingPushNotification(req);
				}
//				else if( req.getProductId().equalsIgnoreCase(personalAccidentProductId)) {
//					
//				}
				else  {
					//Mail Push Notification
					
//					commonPushNotification(req);
				}
			
			
			} catch ( Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
		*/
	// --------------------------------------MOTOR UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
		private QuoteUpdateRes motorPushNotification(NewQuoteReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceMotorDetails> cusRefNo = eserMotRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());

				String loginId = "";
				if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
					loginId = cusRefNo.get(0).getLoginId();
				} else {
					loginId = cusRefNo.get(0).getApplicationId();
				}
				Notification n = new Notification();
				//Broker Info
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
				Broker brokerReq = new Broker();
				if(loginInfo!=null) {
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
					cusReq.setCustomerMailid(customerData.getEmail1());
					cusReq.setCustomerName(customerData.getClientName());
					cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
					cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
					cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
					cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
					cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());
				}

				// UnderWriter Info Old Setup.
				/*List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
						underWrite.add(underWriterReq);
					}
				}*/
				ReferralApi r=ReferralApi.builder()
								.branchCode(cusRefNo.get(0).getBranchCode())
								.insuranceId(cusRefNo.get(0).getCompanyId())
								.productId(cusRefNo.get(0).getProductId())
								.suminsured(cusRefNo.get(0).getSumInsured())
								
								.build();
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				List<AdminReferral> referalList = calcEngine.getReferalList(r);
				for(AdminReferral ref:referalList) {
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(ref.getMailId());
					underWriterReq.setUwMessengerCode(Integer.parseInt(ref.getMobileCode()));
					underWriterReq.setUwMessengerPhone(new BigDecimal(ref.getMobileNo()));
					underWriterReq.setUwPhonecode(Integer.parseInt(ref.getMobileCode()));
					underWriterReq.setUwPhoneNo(new BigDecimal(ref.getMobileNo()));
					underWriterReq.setUwName(ref.getInsuranceId());
					underWriterReq.setUwLoginId(ref.getLoginId());
					underWriterReq.setUwuserType(ref.getUwuserType());
					underWriterReq.setUwsubuserType(ref.getUwsubuserType());
					underWrite.add(underWriterReq);
				}				 
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
				
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setNotifTemplatename("Referral Pending");
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName("Motor");
				n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
				n.getTinyUrl();
				n.setBranchCode(cusRefNo.get(0).getBranchCode());
				n.setRefNo(cusRefNo.get(0).getRequestReferenceNo());
 
				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
//				if (res.getIsError()==null) {
//					updateRes.setResponse("Pushed Successfuly");
//					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
//
//				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
		// --------------------------------------TRAVEL UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
				private QuoteUpdateRes travelPushNotification(NewQuoteReq req) {
					QuoteUpdateRes updateRes = new QuoteUpdateRes();
					try {
						List<EserviceTravelDetails> cusRefNo = eserTraRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
						
						cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
								.collect(Collectors.toList());

						String loginId = "";
						if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
							loginId = cusRefNo.get(0).getLoginId();
						} else {
							loginId = cusRefNo.get(0).getApplicationId();
						}
						Notification n = new Notification();
						//Broker Info
						LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
						Broker brokerReq = new Broker();
						if(loginInfo!=null) {
						brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
						brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
						brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
						brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
						brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
						brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
						brokerReq.setBrokerName(loginInfo.getUserName());
						}
						// Customer Info
						EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
						Customer cusReq = new Customer();
						if(customerData!=null) {
							cusReq.setCustomerMailid(customerData.getEmail1());
							cusReq.setCustomerName(customerData.getClientName());
							cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
							cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
							cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
							cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
						}

						// UnderWriter Info
						List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
						List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
						if (underWriterList != null) {
							for (Tuple underWriterData : underWriterList) {
								UnderWriter underWriterReq = new UnderWriter();
								underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
								underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
								underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
								underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
								underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
								underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
								underWrite.add(underWriterReq);
							}
						}
						n.setUnderwriters(underWrite);
						//Company Info
						n.setCompanyid(cusRefNo.get(0).getCompanyId());
						n.setCompanyName(cusRefNo.get(0).getCompanyName());
						
						//Common Info
						n.setBroker(brokerReq);
						n.setCustomer(cusReq);
						n.setNotifcationDate(new Date());
						n.setNotifDescription("");
						n.setNotifPriority(0);
						n.setNotifPushedStatus(NotificationStatus.PENDING);
						n.setNotifTemplatename("Referral Penidng");
						n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
						n.setProductid(Integer.valueOf(req.getProductId()));
						n.setProductName("Travel");
						n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
						n.setSectionName(cusRefNo.get(0).getSectionName());
						n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
						n.getTinyUrl();

						// Calling pushNotification
						CommonRes res=notiService.pushNotification(n);
//						if (res.getIsError()==null) {
//							updateRes.setResponse("Pushed Successfuly");
//							updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//							updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//							updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
		//
//						}
					} catch (Exception e) {
						e.printStackTrace();
						log.info("Exception is ---> " + e.getMessage());
						return null;
					}
					return updateRes;
				}
				// --------------------------------------BUILDING UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
				private QuoteUpdateRes buildingPushNotification(NewQuoteReq req) {
					QuoteUpdateRes updateRes = new QuoteUpdateRes();
					try {
						List<EserviceBuildingDetails> cusRefNo = eserBuildRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
						
						cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
								.collect(Collectors.toList());

						String loginId = "";
						if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
							loginId = cusRefNo.get(0).getLoginId();
						} else {
							loginId = cusRefNo.get(0).getApplicationId();
						}
						Notification n = new Notification();
						//Broker Info
						LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
						Broker brokerReq = new Broker();
						if(loginInfo!=null) {
						brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
						brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
						brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
						brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
						brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
						brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
						brokerReq.setBrokerName(loginInfo.getUserName());
						}
						// Customer Info
						EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
						Customer cusReq = new Customer();
						if(customerData!=null) {
							cusReq.setCustomerMailid(customerData.getEmail1());
							cusReq.setCustomerName(customerData.getClientName());
							cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
							cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
							cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
							cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
						}

						// UnderWriter Info
						List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
						List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
						if (underWriterList != null) {
							for (Tuple underWriterData : underWriterList) {
								UnderWriter underWriterReq = new UnderWriter();
								underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
								underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
								underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
								underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
								underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
								underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
								underWrite.add(underWriterReq);
							}
						}
						n.setUnderwriters(underWrite);
						//Company Info
						n.setCompanyid(cusRefNo.get(0).getCompanyId());
						n.setCompanyName(cusRefNo.get(0).getCompanyName());
						
						//Common Info
						n.setBroker(brokerReq);
						n.setCustomer(cusReq);
						n.setNotifcationDate(new Date());
						n.setNotifDescription("");
						n.setNotifPriority(0);
						n.setNotifPushedStatus(NotificationStatus.PENDING);
						n.setNotifTemplatename("Referral Pending");
						n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
						n.setProductid(Integer.valueOf(req.getProductId()));
						n.setProductName("Buliding");
						n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
						n.setSectionName(cusRefNo.get(0).getSectionDesc());
						n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
						n.getTinyUrl();

						// Calling pushNotification
						CommonRes res=notiService.pushNotification(n);
//						if (res.getIsError()==null) {
//							updateRes.setResponse("Pushed Successfuly");
//							updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//							updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//							updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
		//
//						}
					} catch (Exception e) {
						e.printStackTrace();
						log.info("Exception is ---> " + e.getMessage());
						return null;
					}
					return updateRes;
				}


		// --------------------------------------PERSONAL ACCIDENT UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
		private QuoteUpdateRes personalAccidentPushNotification(NewQuoteReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());

				String loginId = "";
				if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
					loginId = cusRefNo.get(0).getLoginId();
				} else {
					loginId = cusRefNo.get(0).getApplicationId();
				}
				Notification n = new Notification();
				//Broker Info
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
				Broker brokerReq = new Broker();
				if(loginInfo!=null) {
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
					cusReq.setCustomerMailid(customerData.getEmail1());
					cusReq.setCustomerName(customerData.getClientName());
					cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
					cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
					cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
					cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				}

				// UnderWriter Info
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
				
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setNotifTemplatename("Referral Pending");
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				List<ProductMaster> productData=productRepo.findByProductIdOrderByEffectiveDateStartDesc(Integer.valueOf(req.getProductId()));
				n.setProductName(productData.get(0).getProductName());
				n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
//				if (res.getIsError()==null) {
//					updateRes.setResponse("Pushed Successfuly");
//					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
//
//				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
	
		// -------------------------------------- COMMON UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
		private QuoteUpdateRes commonPushNotification(NewQuoteReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());

				String loginId = "";
				if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
					loginId = cusRefNo.get(0).getLoginId();
				} else {
					loginId = cusRefNo.get(0).getApplicationId();
				}
				Notification n = new Notification();
				//Broker Info
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
				Broker brokerReq = new Broker();
				if(loginInfo!=null) {
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
					cusReq.setCustomerMailid(customerData.getEmail1());
					cusReq.setCustomerName(customerData.getClientName());
					cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
					cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
					cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
					cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				}

				// UnderWriter Info
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
				
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setNotifTemplatename("Referral Pending");
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
			//	 ProductMaster productData= getByProductCode(Integer.valueOf(req.getProductId())) ;
				n.setProductName(cusRefNo.get(0).getProductDesc());
				n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setStatusMessage(req.getReferralRemarks());// Referral Noti , referral app,recj
				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
//				if (res.getIsError()==null) {
//					updateRes.setResponse("Pushed Successfuly");
//					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
//
//				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}

		private List<Tuple> getUnderWriterDetails(String productId,String companyId,String branchCode,String loginId) {
			List<Tuple> list = new ArrayList<Tuple>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
				
				Root<LoginMaster> l = query.from(LoginMaster.class);
				Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);
				Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
				Root<LoginProductMaster> p = query.from(LoginProductMaster.class);
				query.multiselect( u.get("loginId").alias("loginId"), u.get("oaCode").alias("oaCode"), u.get("acExecutiveId").alias("acExecutiveId"),u.get("address1").alias("address1"), 
						   u.get("address2").alias("address2"),u.get("address3").alias("address3"), 
						   u.get("agencyCode").alias("agencyCode"),u.get("approvedPreparedBy").alias("approvedPreparedBy"), 
						   u.get("branchCode").alias("branchCode"),u.get("checkerYn").alias("checker"), 
						   u.get("cityCode").alias("cityCode"),u.get("cityName").alias("cityName"), 
						   u.get("commissionVatYn").alias("commissionVatYn"),u.get("companyName").alias("companyName"), 
						   u.get("contactPersonName").alias("contactPersonName"),u.get("coreAppBrokerCode").alias("coreAppBrokerCode"), 
						   u.get("countryCode").alias("countryCode"),u.get("countryName").alias("countryName"), 
						   u.get("createdBy").alias("createdBy"),u.get("custConfirmYn").alias("custConfirmYn"), 
						   u.get("customerId").alias("customerId"),u.get("designation").alias("designation"), 
						   u.get("effectiveDateStart").alias("effectiveDateStart"), 
						   u.get("entryDate").alias("entryDate"),u.get("fax").alias("fax"), 
						   u.get("makerYn").alias("makerYn"),u.get("missippiId").alias("missippiId"), 
						   u.get("mobileCode").alias("mobileCode"),u.get("mobileCodeDesc").alias("mobileCodeDesc"), 
						   u.get("pobox").alias("pobox"),u.get("remarks").alias("remarks"), 
						   u.get("stateCode").alias("stateCode"),u.get("stateName").alias("stateName"), 
						   u.get("status").alias("status"),u.get("updatedBy").alias("updatedBy"), 
						   u.get("updatedDate").alias("updatedDate"),u.get("userMail").alias("userMail"), 
						   u.get("userMobile").alias("userMobile"),u.get("userName").alias("userName"), 
						   u.get("vatRegNo").alias("vatRegNo"),u.get("whatsappCode").alias("whatsappCode"),
						   u.get("whatsappCodeDesc").alias("whatsappCodeDesc"),u.get("whatsappNo").alias("whatsappNo"));			
				List<String> subUserType = new ArrayList<String>(); 
				subUserType.add("high");
				subUserType.add("both");
				//In 
				Expression<String>e0=cb.lower(l.get("subUserType"));
				//Where
				Predicate n1 = cb.equal(cb.lower(l.get("userType")), "issuer");
				Predicate n2 = e0.in(subUserType);
				Predicate n3 = cb.equal(l.get("companyId"),companyId);
				Predicate n4 = cb.equal(l.get("loginId"),u.get("loginId"));
				Predicate n5 = cb.equal(b.get("loginId"),(l.get("loginId")));
				Predicate n6 = cb.equal(p.get("loginId"),(l.get("loginId")));
				Predicate n7 = cb.equal(b.get("branchCode"),branchCode);
				Predicate n8 = cb.equal(p.get("productId"),productId);
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
				today = cal.getTime();
				Predicate n9 = cb.between(cb.literal(today),p.get("effectiveDateStart"), p.get("effectiveDateEnd"));
				query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9);
				TypedQuery<Tuple> result = em.createQuery(query);
				list = result.getResultList();
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
		
		public ProductMaster getByProductCode(Integer productId) {
			ProductMaster res = new ProductMaster();
			try {
				Date today  =new Date();
				Calendar cal = new GregorianCalendar(); 
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today   = cal.getTime();
				
				List<ProductMaster> list = new ArrayList<ProductMaster>();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ProductMaster> query = cb.createQuery(ProductMaster.class);
		
				// Find All
				Root<ProductMaster> b = query.from(ProductMaster.class);
		
				// Select
				query.select(b);
		
				// Amend ID Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<ProductMaster> ocpm1 = amendId.from(ProductMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				amendId.where(a1,a2);
		
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
		
				// Where
				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(b.get("productId"), productId);
		
				query.where(n1,n2).orderBy(orderList);
		
				// Get Result
				TypedQuery<ProductMaster> result = em.createQuery(query);
				list = result.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getProductId()))).collect(Collectors.toList());
				list.sort(Comparator.comparing(ProductMaster :: getProductName ));
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}
		
		//Tracking Details
		private QuoteUpdateRes trackingDetailsBuyPolicy(NewQuoteReq req) {
			QuoteUpdateRes res=new QuoteUpdateRes();
		try {
			List<TrackingDetailsSaveReq> trackingReq1 = new ArrayList<TrackingDetailsSaveReq>();
		
			 if( req.getMotorYn().equalsIgnoreCase("H") &&  req.getProductId().equalsIgnoreCase(travelProductId)) {
					List<EserviceTravelDetails> cusRefNo = eserTraRepo
							.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

					for(EserviceTravelDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus(motor.getStatus().equalsIgnoreCase("D") ? "D" : "RP");
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getCreatedBy());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					}
				} else if( req.getMotorYn().equalsIgnoreCase("M") ) {
				List<EserviceMotorDetails> cusRefNo = eserMotRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				for(EserviceMotorDetails motor:cusRefNo ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus(motor.getStatus().equalsIgnoreCase("D") ? "D" : "RP");
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getCreatedBy());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getReferalRemarks());
					trackingReq1.add(trackingReq);
					}
					
				} else if( req.getMotorYn().equalsIgnoreCase("A") ) {
				List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceBuildingDetails motor:cusRefNo ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus(motor.getStatus().equalsIgnoreCase("D") ? "D" : "RP");
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getCreatedBy());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getReferalRemarks());
					trackingReq1.add(trackingReq);
				}
			} else  {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceCommonDetails motor:cusRefNo ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus(motor.getStatus().equalsIgnoreCase("D") ? "D" : "RP");
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getCreatedBy());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getReferalRemarks());
					trackingReq1.add(trackingReq);
				}
			}			
			
			trackingService.insertTrackingDetails(trackingReq1);		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	// Tracking Details
	private QuoteUpdateRes trackingDetailsQuote(QuoteThreadReq req) {
		QuoteUpdateRes res = new QuoteUpdateRes();
		try {
			
			List<TrackingDetailsSaveReq> trackingReq1 = new ArrayList<TrackingDetailsSaveReq>();
			if( req.getMotorYn().equalsIgnoreCase("M")) {
//				List<EserviceMotorDetails> cusRefNo = eserMotRepo
//						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				List<EserviceMotorDetails> cusRefNo = req.getMotorDetailsList()  
				        .stream()
				        .filter(m -> req.getRequestReferenceNo().equals(m.getRequestReferenceNo()))
				        .filter(m -> req.getProductId().equals(m.getProductId()))  
				        .collect(Collectors.toList());

			   
				for(EserviceMotorDetails motor:cusRefNo ) {
					if(! motor.getStatus().equalsIgnoreCase("D")  ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( "Q");
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(req.getQuoteNo()==null?"":req.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getCreatedBy());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq1.add(trackingReq);
					}
					
				  }
					
				} else if( req.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
				List<EserviceTravelDetails> cusRefNo = eserTraRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceTravelDetails motor:cusRefNo ) {
					if(! motor.getStatus().equalsIgnoreCase("D")  ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( "Q");
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(req.getQuoteNo()==null?"":req.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getCreatedBy());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq1.add(trackingReq);
					}
					}
			} else if( req.getMotorYn().equalsIgnoreCase("A") ) {
				List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceBuildingDetails motor:cusRefNo ) {
					if(! motor.getStatus().equalsIgnoreCase("D")  ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( "Q");
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(req.getQuoteNo()==null?"":req.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getCreatedBy());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq1.add(trackingReq);
					}
					}
			} else {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceCommonDetails motor:cusRefNo ) {
					if(! motor.getStatus().equalsIgnoreCase("D")  ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( "Q");
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(req.getQuoteNo()==null?"":req.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getCreatedBy());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq1.add(trackingReq);
					}
					}
			}			
			
			trackingService.insertTrackingDetails(trackingReq1);
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
	
	
	@Override
	public void QuoteGenerateUwsysIdAndPolIdxID(NewQuoteReq req, String  quoteNo) {
		HomePositionMaster quoteNoData = homeRepo.findByQuoteNo(quoteNo);
		String endtTypeId = quoteNoData.getEndtTypeId();
		String endtPrevQuoteNo = quoteNoData.getEndtPrevQuoteNo();
		
		if(endtPrevQuoteNo==null) {
		System.out.println("Quote Save Success quoteId : " + quoteNo);
		System.out.println("home position data : " + quoteNoData);
		if (quoteNoData.getUwsysId() == null) {
			Long nextUwSysId = homeRepo.getNextUwSysId();
			//Long nextpolIdx = homeRepo.getNextpolIdx();
			quoteNoData.setUwsysId(nextUwSysId);
			quoteNoData.setAhpolIdx(0L);
			homeRepo.save(quoteNoData);
		}
		}else {
			HomePositionMaster home = homeRepo.findByQuoteNo(endtPrevQuoteNo);
			Long uwsysId = home.getUwsysId();
			if(uwsysId != null) {
			quoteNoData.setUwsysId(uwsysId);
			quoteNoData.setAhpolIdx(quoteNoData.getEndtCount()!= null?quoteNoData.getEndtCount():0L);
			homeRepo.save(quoteNoData);
			}

		}
	/**	CalcCommission policyReq = new CalcCommission();
		policyReq.setAgencyCode("");
		policyReq.setBranchCode(req.getBranchCode());
		policyReq.setCreatedBy(req.getCreatedBy());
		policyReq.setInsuranceId(req.getInsuranceId());
		policyReq.setPolicyNo("");
		policyReq.setProductId(req.getProductId().toString());
		policyReq.setQuoteno(quoteNo);
		policyReq.setSectionId("");
		String policyNo = calcService.getPolicyNo(policyReq);
		home.setPolicyNo(policyNo);
		home.setStatus("P");
		
		return policyNo;**/
	}
	
	private String resolveAdditionalInfoYN(NewQuoteReq req, String companyId) {

	    try {
	        if (req.getVehicleIdsList() == null || req.getVehicleIdsList().isEmpty())
	            return "N";

	        Integer productId    = Integer.valueOf(req.getProductId()); 
	        String  productIdStr = req.getProductId();                  

	        
	        List<Integer> allCoverIds = req.getVehicleIdsList().stream()
	                .filter(v -> v.getCoverIdList() != null)
	                .flatMap(v -> v.getCoverIdList().stream())
	                .map(c -> Integer.valueOf(c.getCoverId()))
	                .distinct()
	                .collect(Collectors.toList());

	        if (allCoverIds.isEmpty()) return "N";

	        long coverCount = sectionCoverMasterRepository
	                .countCoversWithAdditionalInfo(
	                        companyId,
	                        productId,        
	                        allCoverIds);     

	        if (coverCount == 0) return "N";  

	        List<String> coverIdStrs = allCoverIds.stream()
	                .map(String::valueOf)
	                .collect(Collectors.toList());

	        long templateCount = templateRepository
	                .countTemplatesForCovers(
	                        companyId,
	                        productIdStr,     
	                        coverIdStrs);     

	        return templateCount > 0 ? "Y" : "N";

	    } catch (Exception e) {
	        log.error("resolveAdditionalInfoYN error: {}", e.getMessage());
	        return "N";
	    }
	}
}
