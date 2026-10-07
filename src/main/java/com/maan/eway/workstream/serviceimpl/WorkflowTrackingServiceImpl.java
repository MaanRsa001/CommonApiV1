/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.maan.eway.error.Error;
import com.maan.eway.workstream.entity.QuoteProposal;
import com.maan.eway.workstream.entity.WorkflowTracking;
import com.maan.eway.workstream.repository.QuoteProposalRepository;
import com.maan.eway.workstream.repository.WorkflowTrackingRepository;
import com.maan.eway.workstream.request.QuoteProposalActionReq;
import com.maan.eway.workstream.request.WorkflowGetByLevelReq;
import com.maan.eway.workstream.request.WorkflowGetReq;
import com.maan.eway.workstream.response.ApproverRes;
import com.maan.eway.workstream.response.QuoteProposalRes;
import com.maan.eway.workstream.response.WorkflowTrackingRes;
import com.maan.eway.workstream.service.WorkflowTrackingService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


@Service
public class WorkflowTrackingServiceImpl implements WorkflowTrackingService {
	private static final Logger log = LogManager.getLogger(WorkflowTrackingServiceImpl.class);	
	
	private static final String ACTION_APPROVED = "AP";
	private static final String ACTION_ESCALATED = "ES";
	private static final String ACTION_REVOKED = "RV";	
	private static final String NEW_QUOTE = "NEWQUOTE";
	
	private WorkflowTrackingRepository workflowRepo;
//	private QuoteProposalServiceImpl proposalService;
	private ApproverServiceImpl approverService;
	private EntityManager entityManager;
	private ModelMapper mapper;
	private QuoteProposalRepository proposalRepo;

	@Lazy
	@Autowired
	public WorkflowTrackingServiceImpl(WorkflowTrackingRepository workflowRepo,
			 ApproverServiceImpl approverService,
			EntityManager entityManager, ModelMapper mapper) {
		this.workflowRepo = workflowRepo;
//		this.proposalService = proposalService;
		this.approverService = approverService;
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	public List<Error> validateParamatersOfWorkflowGetByLevelReq(WorkflowGetByLevelReq req){
		List<Error> errors = new ArrayList<>();
		
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}
		if(req.getProposalId() == null) {
			errors.add(new Error("13", "ProposalId", "ProposalId Should Not Be Null"));
		}
		if(req.getHierarchyValue() == null) {
			errors.add(new Error("14", "HierarchyValue", "HierarchyValue Should Not Be Null"));
		}
				
		return errors;
	}


	public List<Error> validateParamatersOfWorkflowGetReq(WorkflowGetReq req){
		List<Error> errors = new ArrayList<>();
		
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}
		if(req.getProposalId() == null) {
			errors.add(new Error("13", "ProposalId", "ProposalId Should Not Be Null"));
		}
		
		return errors;
	}

	
	/**
	 * Creates a new entry in the workflow tracking system based on the given proposal action request.
	 *
	 * @param req the {@link QuoteProposalActionReq} object containing details of the proposal action request.
	 * @return the newly created {@link WorkflowTracking} object, or {@code null} if an exception occurs during execution.
	 *
	 * @throws NoSuchElementException if the approver or proposal associated with the request cannot be found.
	 *
	 * <p>
	 * This method performs the following steps:
	 * <ul>
	 *   <li>Retrieves the approver information using the company ID, product ID, and login ID from the request.</li>
	 *   <li>Retrieves the proposal information using the company ID, product ID, and proposal ID from the request.</li>
	 *   <li>Generates a new workflow ID and sets hierarchical and proposal-specific details in the new workflow entry.</li>
	 *   <li>Saves the newly created workflow entry in the repository.</li>
	 * </ul>
	 * If any step fails, an appropriate exception is logged, and {@code null} is returned.
	 * </p>
	 *
	 * @see QuoteProposalActionReq
	 * @see WorkflowTracking
	 */
	public WorkflowTracking createNewEntryInWorkflow(QuoteProposalActionReq req) {
	    try {
	        // Retrieve the approver information
	        Optional<ApproverRes> optApprover = approverService.getApprover(
	        		req.getCompanyId(), req.getProductId(), req.getLoginId());
	        if (optApprover.isEmpty()) {
	            throw new NoSuchElementException("Approver not found for the provided details.");
	        }

	        // Retrieve the proposal information
	        Optional<QuoteProposalRes> optProposal = getProposal(
	        		req.getCompanyId(), req.getProductId(), req.getProposalId());
	        if (optProposal.isEmpty()) {
	            throw new NoSuchElementException("Proposal not found for the provided details.");
	        }

	        // Create a new WorkflowForward Entry
	        WorkflowTracking newWorkflow = mapper.map(req, WorkflowTracking.class);
	        newWorkflow.setWorkflowId(
	        		workflowIdGenerator(req.getCompanyId(), req.getProductId(), req.getProposalId())
	        		);  // Generate a new workflow ID
	        newWorkflow.setHierarchyValue(optApprover.get().getHierarchyValue());
	        newWorkflow.setHierarchyLevel(optApprover.get().getHierarchyLevel());
	        
	        newWorkflow.setCustomerReferenceNo(optProposal.get().getCustomerReferenceNo());
	        newWorkflow.setRequestReferenceNo(optProposal.get().getRequestReferenceNo());
	        newWorkflow.setQuoteNo(optProposal.get().getQuoteNo());
	        
	        newWorkflow.setActionTakenOn(LocalDateTime.now());
	        return workflowRepo.saveAndFlush(newWorkflow);
	    } catch (Exception e) {
	        log.error("Error creating new workflow entry for proposal ID {}: {}", req.getProposalId(), e.getMessage(), e);
	        return null;
	    }
	}

	
	/**
	 * Creates the first entry in the workflow tracking system for a given proposal.
	 * 
	 * <p>
	 * This method generates a new workflow entry based on the provided proposal and assigns it
	 * a unique workflow ID. The workflow action is set to "NEW_QUOTE" and the action time is 
	 * set to the current timestamp. The entry is then saved to the database.
	 * </p>
	 *
	 * @param req the {@link QuoteProposal} object containing details of the proposal to be processed.
	 * @return the newly created {@link WorkflowTracking} object, or {@code null} if an exception occurs during execution.
	 * 
	 * @throws Exception if there is an error during the creation or saving of the workflow entry.
	 * 
	 * <p>
	 * This method performs the following actions:
	 * <ul>
	 *   <li>Maps the provided {@link QuoteProposal} object to a new {@link WorkflowTracking} entry.</li>
	 *   <li>Generates a unique workflow ID for the proposal using the company, product, and proposal IDs.</li>
	 *   <li>Sets the action time and action type ("NEW_QUOTE") for the workflow entry.</li>
	 *   <li>Saves the new workflow entry to the repository and returns the saved entity.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see QuoteProposal
	 * @see WorkflowTracking
	 */
	public WorkflowTracking firstTimeEntryInWorkflow(QuoteProposal req) {
		try {
			WorkflowTracking firstTimeWorkflow = mapper.map(req, WorkflowTracking.class);
			firstTimeWorkflow.setWorkflowId(
					workflowIdGenerator(req.getCompanyId(), req.getProductId(), req.getProposalId())
					);
			firstTimeWorkflow.setActionTakenOn(LocalDateTime.now());
			firstTimeWorkflow.setActionTaken(NEW_QUOTE);
			
			return workflowRepo.saveAndFlush(firstTimeWorkflow);	
		} catch (Exception e) {
			log.error("Error creating new workflow entry : {}", e.getMessage(), e);
			return null;
		}
	}

	
	/**
	 * Retrieves all workflows associated with a proposal for a specific hierarchy level.
	 * 
	 * <p>
	 * This method queries the database to fetch all workflow entries related to the given company, product,
	 * proposal, and hierarchy value. The workflows are ordered by their workflow ID and mapped into 
	 * {@link WorkflowTrackingRes} objects. Additionally, the method assigns a sequential order to each workflow 
	 * in the list based on the sorted order.
	 * </p>
	 *
	 * @param companyId the ID of the company associated with the workflows.
	 * @param productId the ID of the product associated with the workflows.
	 * @param proposalId the ID of the proposal for which the workflows are being fetched.
	 * @param hierarchyValue the hierarchy level value to filter the workflows by.
	 * @return a list of {@link WorkflowTrackingRes} objects representing the workflows associated with the 
	 *         specified company, product, proposal, and hierarchy level, or {@code null} if an error occurs.
	 * 
	 * @throws Exception if an error occurs while retrieving the workflow data (though not explicitly thrown in this implementation).
	 * 
	 * <p>
	 * The following actions are performed by this method:
	 * <ul>
	 *   <li>Queries the database for all workflows that match the provided company, product, proposal, and hierarchy level values.</li>
	 *   <li>Sorts the workflows by their workflow ID.</li>
	 *   <li>Maps the retrieved workflows to {@link WorkflowTrackingRes} objects.</li>
	 *   <li>Sets a workflow order sequentially for each workflow in the list.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see WorkflowTracking
	 * @see WorkflowTrackingRes
	 */
	public List<WorkflowTrackingRes> getAllWorkflowByProposalIdAndLevel(
			Integer companyId, Integer productId, Long proposalId, Integer hierarchyValue) {
		try {
			List<WorkflowTracking> allWorkflows = workflowRepo.findAllByCompanyIdAndProductIdAndProposalId(
					companyId, productId, proposalId);
		
			
			List<WorkflowTrackingRes> workflowList = allWorkflows.stream()
						.sorted(Comparator.comparing(WorkflowTracking::getWorkflowId))
						.map(wf -> mapper.map(wf, WorkflowTrackingRes.class))
						.toList();
			
			//Filtering upto specific hierarchy value
			int index = -1;
			for(int i=0; i<workflowList.size(); i++) {
				if(hierarchyValue.equals(workflowList.get(i).getHierarchyValue()))	{index = i;}
			}

			if(index != -1) {
				return workflowList.subList(0, index+1);
			}
			else {	return workflowList; }			
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}		
	}
	
	
	/**
	 * Retrieves all workflow entries for a given proposal and returns them as a list of response objects.
	 * 
	 * <p>
	 * This method queries the database for all workflow tracking records associated with the specified company, 
	 * product, and proposal IDs. The retrieved workflows are then sorted by their workflow ID and mapped to 
	 * a list of {@link WorkflowTrackingRes} response objects. The workflow order is also set sequentially for each entry.
	 * </p>
	 *
	 * @param companyId the ID of the company associated with the workflows.
	 * @param productId the ID of the product associated with the workflows.
	 * @param proposalId the ID of the proposal for which the workflows are being retrieved.
	 * @return a list of {@link WorkflowTrackingRes} objects representing the workflow entries for the given proposal,
	 *         or {@code null} if an exception occurs during the retrieval or processing.
	 * 
	 * @throws Exception if an error occurs while retrieving or processing the workflow data.
	 * 
	 * <p>
	 * This method performs the following actions:
	 * <ul>
	 *   <li>Fetches all workflows from the repository that match the provided company ID, product ID, and proposal ID.</li>
	 *   <li>Sorts the workflows by their workflow ID in ascending order.</li>
	 *   <li>Maps the sorted workflows to {@link WorkflowTrackingRes} objects using the {@link Mapper}.</li>
	 *   <li>Assigns a sequential workflow order to each mapped workflow.</li>
	 * </ul>
	 * </p>
	 *
	 * @see WorkflowTracking
	 * @see WorkflowTrackingRes
	 */
	public List<WorkflowTrackingRes> getAllWorkflowByProposalId(Integer companyId, Integer productId, Long proposalId){
		try {
			List<WorkflowTracking> allWorkflows = workflowRepo.findAllByCompanyIdAndProductIdAndProposalId(
					companyId, productId, proposalId);
			
			List<WorkflowTrackingRes> workflowList = allWorkflows.stream()
						.sorted(Comparator.comparing(WorkflowTracking::getWorkflowId))
						.map(wf -> mapper.map(wf, WorkflowTrackingRes.class))
						.toList();
					
			return workflowList;
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}		
	}
	
	
	/**
	 * Retrieves the most recent workflow update for a given proposal.
	 * 
	 * <p>
	 * This method queries the database to fetch the most recent workflow entry associated with the specified 
	 * company, product, and proposal IDs. The retrieval is done by ordering the workflows based on the 
	 * action timestamp in descending order, ensuring the latest update is returned. The result is mapped to 
	 * a {@link WorkflowTrackingRes} response object.
	 * </p>
	 *
	 * @param companyId the ID of the company associated with the workflow.
	 * @param productId the ID of the product associated with the workflow.
	 * @param proposalId the ID of the proposal for which the latest workflow update is being fetched.
	 * @return a {@link WorkflowTrackingRes} object representing the most recent workflow update for the given proposal,
	 *         or {@code null} if no workflow entry is found for the provided IDs.
	 * 
	 * @throws Exception if an error occurs while retrieving the workflow data (though not explicitly thrown in this implementation).
	 * 
	 * <p>
	 * This method performs the following actions:
	 * <ul>
	 *   <li>Queries the database for the most recent workflow entry using the provided company, product, and proposal IDs.</li>
	 *   <li>Orders the results by the action timestamp in descending order to get the latest update.</li>
	 *   <li>Maps the retrieved workflow entry to a {@link WorkflowTrackingRes} response object.</li>
	 * </ul>
	 * </p>
	 *
	 * @see WorkflowTracking
	 * @see WorkflowTrackingRes
	 */	
	public WorkflowTrackingRes latestUpdateOnProposal(
			Integer companyId, Integer productId, Long proposalId) {
		
		WorkflowTracking workflowTracking = workflowRepo.findTopByCompanyIdAndProductIdAndProposalIdOrderByActionTakenOnDesc(
				companyId, productId, proposalId);
		if(workflowTracking == null) {return null;}
	
		return mapper.map(workflowTracking, WorkflowTrackingRes.class);
	}
	
	/**
	 * Retrieves a mapping of proposal IDs to the action times for workflow that match specific criteria 
	 * and are marked with a non-incoming action.
	 *
	 * @param companyId the ID of the company used to filter workflows.
	 * @param productId the ID of the product used to filter workflows.
	 * @param loginId the login ID used to filter workflows by user.
	 * @param action the action type used to filter workflows.
	 * @return a map where the key is the proposal ID, and the value is the time when the action was taken.
	 *         Returns {@code null} if an exception occurs during execution.
	 *
	 * @throws Exception if an error occurs during query execution or data processing.
	 *
	 * <p>
	 * This method identifies workflows that meet the following criteria:
	 * <ul>
	 *   <li>Belong to the specified company and product.</li>
	 *   <li>Are associated with the specified login ID.</li>
	 *   <li>Are marked with the specified action type.</li>
	 * </ul>
	 * The result is returned as a map, with the proposal IDs as keys and their corresponding action times as values.
	 * </p>
	 */
	public Map<Long, LocalDateTime> findActionTimeOfNonIncoming(
			Integer companyId, Integer productId, String loginId, String action) {
		try {
			List<WorkflowTracking> workflowTrackings = findWorkflowsByActionTaken(companyId, productId, loginId, action);
			
			Map<Long, LocalDateTime> map = new HashMap<>();
			workflowTrackings.forEach(
					workflow ->	map.put(workflow.getProposalId(), workflow.getActionTakenOn())
					);
			
			return map;
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
		    return null;
		}		
	}
	
	
	/**
	 * Retrieves a mapping of proposal IDs to the action times of escalated workflow that have become incoming.
	 *
	 * @param companyId the ID of the company used to filter workflows.
	 * @param productId the ID of the product used to filter workflows.
	 * @return a map where the key is the proposal ID, and the value is the time when the action was taken.
	 *         Returns {@code null} if an exception occurs during execution.
	 * 
	 * @throws Exception if an error occurs during query execution or data processing.
	 * 
	 * <p>
	 * This method identifies workflows that meet the following criteria:
	 * <ul>
	 *   <li>Belong to the specified company and product.</li>
	 *   <li>Have an action marked as "escalated" that causes them to become incoming workflows.</li>
	 * </ul>
	 * The result is returned as a map, with the proposal IDs as keys and their corresponding action times as values.
	 * </p>
	 */
	public Map<Long, LocalDateTime> findActionTimeOfEscalatedWorkflowBecomeIncoming(
			Integer companyId, Integer productId) {
		try {
			List<WorkflowTracking> workflowTrackings = findEscalatedWorkflowBecomesIncoming(companyId, productId);
			
			Map<Long, LocalDateTime> map = new HashMap<>();
			workflowTrackings.forEach(
					workflow ->	map.put(workflow.getProposalId(), workflow.getActionTakenOn())
					);
			
			return map;
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
		    return null;
		}
	}
	
	
	/**
	 * Retrieves a mapping of proposal IDs to the action time of revoked workflow that become incoming at the same hierarchy level.
	 *
	 * @param companyId the ID of the company used to filter workflows.
	 * @param productId the ID of the product used to filter workflows.
	 * @param hierarchyLevel the hierarchy level used to filter workflows. This method processes workflows at the specified level.
	 * @return a map where the key is the proposal ID, and the value is the time when the action was taken.
	 *         Returns {@code null} if an exception occurs.
	 * 
	 * @throws Exception if an error occurs during the query or data processing.
	 * 
	 * <p>
	 * This method identifies workflows that meet the following criteria:
	 * <ul>
	 *   <li>Belong to the specified company and product.</li>
	 *   <li>Have a "revoked" action at the same hierarchy level as the specified level.</li>
	 * </ul>
	 * The result is returned as a map, with the proposal IDs as keys and their corresponding action times as values.
	 * </p>
	 */
	public Map<Long, LocalDateTime> findActionTimeOfRevokedWorkflowBecomeIncoming(
				Integer companyId, Integer productId, Integer hierarchyLevel) {
		try {
			Integer sameLevelValue = hierarchyLevel;
			List<WorkflowTracking> workflowTrackings = findRevokedWorkflowBecomingIncoming(companyId, productId, sameLevelValue);
			
			Map<Long, LocalDateTime> map = new HashMap<>();
			workflowTrackings.forEach(
					workflow ->	map.put(workflow.getProposalId(), workflow.getActionTakenOn())
					);
			
			return map;
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
		    return null;
		}
	}
	
	
	/**
	 * Retrieves a mapping of proposal IDs to the action time of approved workflow that become incoming for the next hierarchy level.
	 *
	 * @param companyId the ID of the company to filter workflows.
	 * @param productId the ID of the product to filter workflows.
	 * @param hierarchyLevel the current hierarchy level. The method calculates the previous hierarchy level for filtering.
	 * @return a map where the key is the proposal ID, and the value is the time when the action was taken.
	 *         Returns {@code null} if an exception occurs.
	 * 
	 * @throws Exception if an error occurs during the query or data processing.
	 * 
	 * <p>
	 * This method retrieves workflows that meet the following criteria:
	 * <ul>
	 *   <li>Belong to the specified company and product.</li>
	 *   <li>Have an approved action at the previous hierarchy level.</li>
	 * </ul>
	 * The result is a map of proposal IDs to their corresponding action times.
	 * </p>
	 */
	public Map<Long, LocalDateTime> findActionTimeOfApprovedWorkflowBecomeIncoming(
			Integer companyId, Integer productId, Integer hierarchyLevel) {
		try {
			Integer prevLevelValue = hierarchyLevel -1;
			List<WorkflowTracking> workflowTrackings = findApprovedWorkflowBecomingIncomingForNextLevel(
					companyId, productId, prevLevelValue);
			
			Map<Long, LocalDateTime> map = new HashMap<>();
			workflowTrackings.forEach(
					workflow ->	map.put(workflow.getProposalId(), workflow.getActionTakenOn())
					);
			
			return map;
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
		    return null;
		}
	}	
	
	
	/**
	 * Generates a new workflow ID based on the maximum workflow ID found for the specified company, product, and proposal.
	 * 
	 * <p>
	 * This method queries the database to find the workflow with the highest ID for the given company, product, 
	 * and proposal. It then increments the ID by 1 to generate a new workflow ID. If no workflow is found for the 
	 * specified company, product, and proposal, the method returns 1 as the new workflow ID.
	 * </p>
	 *
	 * @param companyId the ID of the company for which the workflow ID is being generated.
	 * @param productId the ID of the product for which the workflow ID is being generated.
	 * @param proposalId the ID of the proposal for which the workflow ID is being generated.
	 * @return the generated workflow ID, which is one greater than the highest existing workflow ID, or 1 if no 
	 *         workflows exist for the specified company, product, and proposal.
	 * 
	 * @throws Exception if an error occurs during the process of generating the workflow ID (though not explicitly 
	 *                   thrown in this implementation).
	 * 
	 * <p>
	 * The following actions are performed by this method:
	 * <ul>
	 *   <li>Fetches the highest workflow ID for the specified company, product, and proposal from the database.</li>
	 *   <li>If a workflow ID exists, it increments the value by 1 to generate a new workflow ID.</li>
	 *   <li>If no workflow ID exists, it returns 1 as the starting workflow ID.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see WorkflowTracking
	 */
	private Long workflowIdGenerator(Integer companyId, Integer productId, Long proposalId) {
	    WorkflowTracking workflowTracking = workflowRepo.findTopByCompanyIdAndProductIdAndProposalIdOrderByWorkflowIdDesc(
	    		companyId, productId, proposalId);
	
	    if(workflowTracking == null) {return 1L;}        
	    return workflowTracking.getWorkflowId() + 1L;		
	}
	
	
	/**
	 * Retrieves a list of workflow based on the specified action and filters.
	 *
	 * @param companyId the ID of the company to filter workflows.
	 * @param productId the ID of the product to filter workflows.
	 * @param loginId the login ID of the user to filter workflows.
	 * @param action the specific action taken to filter workflows (e.g., "approved", "rejected").
	 * @return a list of {@link WorkflowTracking} objects that match the specified criteria.
	 * @throws Exception if an error occurs during query execution.
	 */
	private List<WorkflowTracking> findWorkflowsByActionTaken(
			Integer companyId, Integer productId, String loginId, String action) throws Exception{
		// Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<WorkflowTracking> query = cb.createQuery(WorkflowTracking.class);
	    Root<WorkflowTracking> workflowRoot = query.from(WorkflowTracking.class);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(workflowRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(workflowRoot.get("productId"), productId);	    	        
	    Predicate loginIdFilter = cb.equal(workflowRoot.get("loginId"), loginId);
	    Predicate actionFilter = cb.equal(workflowRoot.get("actionTaken"), action);

	        
	    // Combine all filters (predicates)
	    query.select(workflowRoot)
	    	.where(cb.and( companyFilter, productFilter, loginIdFilter, actionFilter));
	       
	    // Execute query and return the result list
	    return entityManager.createQuery(query).getResultList();
	}
	
	
	/**
	 * Finds all escalated workflow that have become incoming based on the specified criteria.
	 * 
	 * @param companyId the ID of the company to filter the workflows.
	 * @param productId the ID of the product to filter the workflows.
	 * @return a list of {@link WorkflowTracking} objects that match the specified criteria.
	 * @throws Exception if an error occurs during query execution.
	 */	
	private List<WorkflowTracking> findEscalatedWorkflowBecomesIncoming(Integer companyId, Integer productId) throws Exception{
	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<WorkflowTracking> query = cb.createQuery(WorkflowTracking.class);
	    Root<WorkflowTracking> workflowRoot = query.from(WorkflowTracking.class);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(workflowRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(workflowRoot.get("productId"), productId);
	    Predicate actionFilter = cb.equal(workflowRoot.get("actionTaken"), ACTION_ESCALATED);
	     // Combine all filters (predicates)

	    query.select(workflowRoot)
	    	.where(cb.and(companyFilter, productFilter, actionFilter));

	    return entityManager.createQuery(query).getResultList();		
	}
	
	
	/**
	 * Finds all revoked workflow that are becoming incoming based on the specified criteria.
	 * 
	 * @param companyId      the ID of the company to filter the workflows.
	 * @param productId      the ID of the product to filter the workflows.
	 * @param sameLevelValue the hierarchy level value to filter the workflows.
	 * @return a list of {@link WorkflowTracking} objects that match the criteria.
	 * @throws Exception if an error occurs during query execution.
	 */
	private List<WorkflowTracking> findRevokedWorkflowBecomingIncoming(
			Integer companyId, Integer productId, Integer sameLevelValue) throws Exception {
		
	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<WorkflowTracking> query = cb.createQuery(WorkflowTracking.class);
	    Root<WorkflowTracking> workflowRoot = query.from(WorkflowTracking.class);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(workflowRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(workflowRoot.get("productId"), productId);
	    Predicate hierarchyFilter = cb.equal(workflowRoot.get("hierarchyValue"), sameLevelValue);
	    Predicate actionFilter = cb.equal(workflowRoot.get("actionTaken"), ACTION_REVOKED);
	     
	    // Combine all filters (predicates)
	    query.select(workflowRoot)
	    	.where(cb.and(companyFilter, productFilter, hierarchyFilter, actionFilter));

	    return entityManager.createQuery(query).getResultList();			
	}
	
	
	/**
	 * Retrieves a list of approved workflow tracking records that are becoming incoming
	 * for the next level in the hierarchy.
	 *
	 * @param companyId       the ID of the company to filter by (not null).
	 * @param productId       the ID of the product to filter by (not null).
	 * @param prevLevelValue  the value representing the previous level in the hierarchy (not null).
	 * @return a list of {@link WorkflowTracking} entities that match the specified criteria.
	 * @throws Exception if there is an error during the query execution or database interaction.
	 */
	private List<WorkflowTracking> findApprovedWorkflowBecomingIncomingForNextLevel(
			Integer companyId, Integer productId, Integer prevLevelValue) throws Exception{
		
	    // Initialize CriteriaBuilder and CriteriaQuery
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	    CriteriaQuery<WorkflowTracking> query = cb.createQuery(WorkflowTracking.class);
	    Root<WorkflowTracking> workflowRoot = query.from(WorkflowTracking.class);

	    // Define filters (predicates)
	    Predicate companyFilter = cb.equal(workflowRoot.get("companyId"), companyId);
	    Predicate productFilter = cb.equal(workflowRoot.get("productId"), productId);
	    Predicate hierarchyFilter = cb.equal(workflowRoot.get("hierarchyValue"), prevLevelValue);
	    Predicate actionFilter = cb.equal(workflowRoot.get("actionTaken"), ACTION_APPROVED);
	     
	    // Combine all filters (predicates)
	    query.select(workflowRoot)
	    	.where(cb.and(companyFilter, productFilter, hierarchyFilter, actionFilter));

	    return entityManager.createQuery(query).getResultList();	
	}
	
	public Optional<QuoteProposalRes> getProposal(Integer companyId, Integer productId, Long proposalId) {
		Optional<QuoteProposal> optProposal = proposalRepo.findByCompanyIdAndProductIdAndProposalId(
				companyId, productId, proposalId);
		
		return optProposal.map(pro -> mapper.map(pro, QuoteProposalRes.class));
	}
	
}
