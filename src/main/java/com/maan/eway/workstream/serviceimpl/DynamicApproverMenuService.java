/**
 * @author : Ashok Kumar S 
 * @since  : 26-02-2025
 */
package com.maan.eway.workstream.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.error.Error;
import com.maan.eway.workstream.request.ApproverGetReq;
import com.maan.eway.workstream.response.ApproverRes;
import com.maan.eway.workstream.response.ListItemValueRes;
import com.maan.eway.workstream.service.ApproverService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class DynamicApproverMenuService {
	private static final Logger log = LogManager.getLogger(DynamicApproverMenuService.class);
	
	private static final String ITEM_TYPE = "MULTI_LEVEL_REFERRAL_APPROVER_MENU";
	private static final String DEFAULT_VALUE = "99999";
	private static final String STATUS_ACTIVE = "Y";
	
	private ApproverService approverService;
	private EntityManager entityManager;
	
	
	@Autowired
	public DynamicApproverMenuService(ApproverService approverService, EntityManager entityManager) {
		this.approverService = approverService;
		this.entityManager = entityManager;
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
	 * Retrieves a dynamic approver menu based on the provided request details.
	 * <p>
	 * This method fetches the approver details and determines whether the approver  
	 * can escalate. Based on this, it filters the referral approver menu accordingly.
	 * </p>
	 *
	 * @param req the {@link ApproverGetReq} containing company ID, product ID, and login ID.
	 * @return a {@link List} of {@link ListItemValueRes} containing the filtered approver menu items,  
	 *         or {@code null} if an exception occurs.
	 */
	public List<ListItemValueRes> getDynamicApproverMenu(ApproverGetReq req){
		try {
	        // Fetch approver details
			Optional<ApproverRes> optApprover = approverService.getApprover(
					req.getCompanyId(), req.getProductId(), req.getLoginId());
			
			if(optApprover.isEmpty()) {
	            throw new NoSuchElementException("Approver not found for the provided details.");
			}			
			
			Boolean canEscalate = optApprover.get().getCanEscalate();			
			List<ListItemValue> referralApproverMenu = findingReferralApproverMenu();
			
	        // Filter menu items based on escalation permissions
			if(Boolean.FALSE.equals(canEscalate)) {
				return referralApproverMenu.stream()
						.filter(menu -> ! "ES".equals(menu.getItemCode()))
						.map(menu -> new ListItemValueRes(menu.getItemCode(), menu.getItemValue()))
						.toList();
			}else {
				return referralApproverMenu.stream()
						.map(menu -> new ListItemValueRes(menu.getItemCode(), menu.getItemValue()))
						.toList();
			}
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}
	}
	
	
	/**
	 * Retrieves a list of referral approver menu items based on specific criteria.
	 * <p>
	 * This method constructs a JPA Criteria Query to fetch active items of a specific type,  
	 * ensuring that only the latest amendment ID for each item is retrieved.
	 * </p>
	 *
	 * @return a {@link List} of {@link ListItemValue} containing the filtered referral approver menu items.
	 * @throws Exception if an error occurs while executing the query.
	 */
	private List<ListItemValue> findingReferralApproverMenu() throws Exception {
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
		Root<ListItemValue> root = query.from(ListItemValue.class);
		
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<ListItemValue> subroot = subquery.from(ListItemValue.class);
		
	    // Subquery to fetch the latest amendId per companyId, branchCode, and itemId
		subquery.select(cb.max(subroot.get("amendId")))
				.where(
					cb.equal(subroot.get("companyId"), root.get("companyId")),
					cb.equal(subroot.get("branchCode"), root.get("branchCode")),
					cb.equal(subroot.get("itemId"), root.get("itemId"))
				);
		
	    // Define filter conditions
		Predicate [] filters = new Predicate[] {
				cb.equal(root.get("companyId"), DEFAULT_VALUE),
				cb.equal(root.get("branchCode"), DEFAULT_VALUE),
				cb.equal(root.get("itemType"), ITEM_TYPE),
				cb.equal(root.get("status"), STATUS_ACTIVE),
				cb.equal(root.get("amendId"), subquery)
		};
		
	    // Build query with filters and sorting
		query.select(root)
			.where(cb.and(filters))
			.orderBy(cb.asc(root.get("itemId")));
		
		return entityManager.createQuery(query).getResultList();		
	}

}
