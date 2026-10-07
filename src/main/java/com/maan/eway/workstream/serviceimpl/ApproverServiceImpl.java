/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.error.Error;
import com.maan.eway.workstream.entity.Approver;
import com.maan.eway.workstream.repository.ApproverRepository;
import com.maan.eway.workstream.request.ApproverGetReq;
import com.maan.eway.workstream.response.ApproverRes;
import com.maan.eway.workstream.service.ApproverService;

@Service
public class ApproverServiceImpl implements ApproverService {
	
	private ApproverRepository approverRepo;
	private ModelMapper mapper;
	
	@Autowired
	public ApproverServiceImpl(ApproverRepository approverRepo, ModelMapper mapper) {
		this.approverRepo = approverRepo;
		this.mapper = mapper;
	}	

	public List<Error> validateParametersOfApproverGetReq(ApproverGetReq req) {
		List<Error> errors = new ArrayList<>();
		
		if(req.getCompanyId() == null) {
			errors.add(new Error("1", "CompanyId", "Company Id Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("2", "ProductId", "Product Id Should Not Be Null"));
		}
		if(req.getLoginId() == null || req.getLoginId().isBlank()) {
			errors.add(new Error("3", "LoginId", "LoginId Should Not Be Blank"));
		}
		
		return errors;		
	}
	
	
	/**
	 * Retrieves the most recent approver record for a given company, product, and login ID.
	 *
	 * This method fetches the latest approver record (based on the amendment ID) 
	 * for the specified company, product, and user. If no approver is found, an 
	 * empty {@code Optional} is returned. If an approver is found, it is mapped 
	 * to an {@code ApproverRes} object and returned within an {@code Optional}.
	 *
	 * @param companyId the ID of the company to search for
	 * @param productId the ID of the product to search for
	 * @param loginId the login ID of the user to search for
	 * @return an {@code Optional} containing the mapped {@code ApproverRes} object if found,
	 *         or an empty {@code Optional} if no approver record exists
	 */	
	public Optional<ApproverRes> getApprover(Integer companyId, Integer productId, String loginId) {
		Approver approver = approverRepo.findTopByCompanyIdAndProductIdAndLoginIdOrderByAmendIdDesc(
				companyId, productId, loginId);
		if(approver == null) {
			return Optional.empty();
		}		
		return Optional.of(mapper.map(approver, ApproverRes.class));
	}
	
	
	/**
	 * Verifies whether the user has the authority to finalize an action for a given company and product.
	 *
	 * This method checks if the user, identified by the login ID, has finalization permissions 
	 * for the specified company and product. It retrieves the most recent approver record 
	 * (based on the amendment ID) for the provided identifiers and determines if the 
	 * "can finalize" flag is enabled.
	 *
	 * @param companyId the ID of the company to check
	 * @param productId the ID of the product to check
	 * @param loginId the login ID of the user whose permissions are to be verified
	 * @return {@code true} if the user has the authority to finalize; {@code false} otherwise
	 */	
	public boolean verifyAuthorityToFinalize(Integer companyId, Integer productId, String loginId) {
		Approver approver = approverRepo.findTopByCompanyIdAndProductIdAndLoginIdOrderByAmendIdDesc(
				companyId, productId, loginId);
		if(approver != null) {
			return Boolean.TRUE.equals(approver.getCanFinalize());
		}
		return false;			
	}
	
	
	/**
	 * Verifies whether the user has the authority to escalate an action for a given company and product.
	 *
	 * This method checks if the user, identified by the login ID, has escalation permissions 
	 * for the specified company and product. It retrieves the most recent approver record 
	 * (based on the amendment ID) for the provided identifiers and determines if the 
	 * "can escalate" flag is enabled.
	 *
	 * @param companyId the ID of the company to check
	 * @param productId the ID of the product to check
	 * @param loginId the login ID of the user whose permissions are to be verified
	 * @return {@code true} if the user has the authority to escalate; {@code false} otherwise
	 */
	public boolean verifyAuthorityToEscalate(Integer companyId, Integer productId, String loginId) {		
		Approver approver = approverRepo.findTopByCompanyIdAndProductIdAndLoginIdOrderByAmendIdDesc(
				companyId, productId, loginId);
		if(approver != null) {
			return Boolean.TRUE.equals(approver.getCanEscalate());
		}
		return false;	
	}
	
	
}
