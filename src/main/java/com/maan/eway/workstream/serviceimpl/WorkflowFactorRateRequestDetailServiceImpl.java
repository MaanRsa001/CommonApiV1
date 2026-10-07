/**
 * @author : Ashok Kumar S 
 * @since  : 09-01-2025
 */
package com.maan.eway.workstream.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.workstream.entity.WorkflowFactorRateRequestDetail;
import com.maan.eway.workstream.repository.WorkflowFactorRateRequestDetailRepository;

@Service
public class WorkflowFactorRateRequestDetailServiceImpl {
	private static final Logger log = LogManager
					.getLogger(WorkflowFactorRateRequestDetailServiceImpl.class);
	
	private WorkflowFactorRateRequestDetailRepository workflowFactorRateRepo;
	private FactorRateRequestDetailsRepository factorRateRepo;
	private ModelMapper mapper;
	
	@Autowired	
	public WorkflowFactorRateRequestDetailServiceImpl(WorkflowFactorRateRequestDetailRepository workflowFactorRateRepo,
			FactorRateRequestDetailsRepository factorRateRepo, ModelMapper mapper) {
		this.workflowFactorRateRepo = workflowFactorRateRepo;
		this.factorRateRepo = factorRateRepo;
		this.mapper = mapper;
	}

	/**
	 * Creates a new entry in the WorkflowFactorRateRequestDetail table for each factor rate request associated 
	 * with the given request reference number.
	 * 
	 * <p>
	 * This method fetches all factor rate request details based on the provided request reference number, 
	 * then maps each factor rate detail to a WorkflowFactorRateRequestDetail entity. The resulting list of 
	 * WorkflowFactorRateRequestDetail entities is then saved in the database. Each entry is associated with 
	 * the given proposal ID and workflow ID.
	 * </p>
	 *
	 * @param requestReferenceNo the reference number of the request to identify the factor rate request details.
	 * @param proposalId the ID of the proposal associated with the workflow.
	 * @param workflowId the ID of the workflow to associate with each factor rate request detail.
	 * @return a list of saved WorkflowFactorRateRequestDetail entities, or {@code null} if an error occurs 
	 *         during the process.
	 * 
	 * @throws Exception if an error occurs during the process of fetching the factor rate request details, 
	 *                   mapping them, or saving the entities to the database.
	 * 
	 * <p>
	 * The following actions are performed by this method:
	 * <ul>
	 *   <li>Fetches the factor rate request details based on the provided request reference number.</li>
	 *   <li>Maps each factor rate request detail to a WorkflowFactorRateRequestDetail entity.</li>
	 *   <li>Sets the proposal ID and workflow ID for each mapped entity.</li>
	 *   <li>Saves all the mapped entities to the database and returns the saved list.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see FactorRateRequestDetails
	 * @see WorkflowFactorRateRequestDetail
	 */
	public List<WorkflowFactorRateRequestDetail> createEntryInWorkflowFactorRateForEachTaken(
			String requestReferenceNo, Long proposalId, Long workflowId){
		try {
			List<FactorRateRequestDetails> factorRateList = factorRateRepo
					.findAllByRequestReferenceNo(requestReferenceNo);
			
			List<WorkflowFactorRateRequestDetail> workflowFactorRateList = new ArrayList<>();
			for(FactorRateRequestDetails factorRate : factorRateList) {
				
				WorkflowFactorRateRequestDetail workflowFactorRate = mapper
						.map(factorRate, WorkflowFactorRateRequestDetail.class);
				
				workflowFactorRate.setProposalId(proposalId);
				workflowFactorRate.setWorkflowId(workflowId);
				
				workflowFactorRateList.add(workflowFactorRate);
			}
			return workflowFactorRateRepo.saveAllAndFlush(workflowFactorRateList);
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}		
	}
	
	

}
