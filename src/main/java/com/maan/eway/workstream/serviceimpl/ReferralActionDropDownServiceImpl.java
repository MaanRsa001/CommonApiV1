/**
 * @author : Ashok Kumar S 
 * @since  : 28-12-2024
 */
package com.maan.eway.workstream.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.error.Error;
import com.maan.eway.workstream.request.ReferralActionDropDownGetReq;
import com.maan.eway.workstream.response.ApproverRes;
import com.maan.eway.workstream.response.ReferralActionDropDownRes;
import com.maan.eway.workstream.service.ReferralActionDropDownService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class ReferralActionDropDownServiceImpl implements ReferralActionDropDownService{
	private static final Logger log = LogManager.getLogger(ReferralActionDropDownServiceImpl.class);
	private static final String ITEM_TYPE = "REFERRAL_ACTION";
	
	private EntityManager entityManager;
	private ModelMapper mapper;
	private ApproverServiceImpl approverService;

	@Autowired
	public ReferralActionDropDownServiceImpl(EntityManager entityManager, ModelMapper mapper,
			ApproverServiceImpl approverService) {
		this.entityManager = entityManager;
		this.mapper = mapper;
		this.approverService = approverService;
	}

	public List<Error> validateReferralActionDropDownGetReq(
			ReferralActionDropDownGetReq req) {		
		List<Error> errors = new ArrayList<>();
		
		if(StringUtils.isBlank(req.getCompanyId())) {
			errors.add(new Error("1", "CompanyId", "CompanyId Should Not Be Blank"));
		}
		if(StringUtils.isBlank(req.getBranchCode())) {
			errors.add(new Error("2", "BranchCode", "BranchCode Should Not Be Blank"));
		}
		if(StringUtils.isBlank(req.getLoginId())) {
			errors.add(new Error("3", "LoginId", "LoginId Should Not Be Blank"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("4", "ProductId", "ProductId Should Not Be Null"));
		}
		return errors;		
	}
	
	
	/**
	 * Retrieves the referral action dropdown list based on the provided request parameters. 
	 * This method first fetches the dropdown values, then filters out the "Assigned" option from the list.
	 * Additionally, if the user has permission to escalate (determined via the approver service), 
	 * all options are returned. If escalation is not allowed, the "Escalation" option is filtered out.
	 *
	 * <p>
	 * The method performs the following:
	 * <ul>
	 *   <li>Fetches the list of predefined dropdown items filtered by the maximum amend ID.</li>
	 *   <li>Filters out the "Assigned" option from the dropdown list.</li>
	 *   <li>Checks if the user has the permission to escalate and returns the full list if allowed.</li>
	 *   <li>If the user cannot escalate, filters out the "Escalation" option from the dropdown list.</li>
	 * </ul>
	 * </p>
	 *
	 * @param req the {@link ReferralActionDropDownGetReq} request object containing the company ID, branch code, product ID, and login ID.
	 * @return a list of {@link ReferralActionDropDownRes} containing the filtered referral action dropdown items.
	 *         If an error occurs, returns {@code null}.
	 * 
	 * @see ReferralActionDropDownGetReq
	 * @see ReferralActionDropDownRes
	 */
	public List<ReferralActionDropDownRes> getReferralActionDropDown(ReferralActionDropDownGetReq req) {
		try {
			List<ListItemValue> allDropDown = findingActionDropDownByMaxAmendId(
					req.getCompanyId(), req.getBranchCode());
			
			//Assigned shown in popup
			List<ReferralActionDropDownRes> dropdownList = allDropDown.stream()
					.filter(dropdown -> !dropdown.getItemCode().equals("AS"))
					.map(dropdown -> mapper.map(dropdown, ReferralActionDropDownRes.class))
					.collect(Collectors.toList());	
			
			//Escalation eglible only get all
			Optional<ApproverRes> optApprover = approverService.getApprover(
					Integer.valueOf(req.getCompanyId()), req.getProductId(), req.getLoginId());
			if(optApprover.isPresent()) {
				if(Boolean.TRUE.equals(optApprover.get().getCanEscalate())) {
					return dropdownList;
				}
			}
			
			return dropdownList.stream()
					.filter(drop -> !drop.getItemCode().equals("ES"))
					.toList();

		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}
	}
	
	
	/**
	 * Retrieves a list of {@link ListItemValue} entities based on the provided company ID, branch code, and item type.
	 * It fetches the records with the maximum amend ID for each item ID, company ID, and branch code combination.
	 * 
	 * <p>
	 * This method uses the Criteria API to build a query that retrieves the predefined values from the database. 
	 * It performs the following:
	 * <ul>
	 *   <li>Subquery to fetch the maximum amend ID for each combination of company ID, branch code, and item ID.</li>
	 *   <li>Filters the records based on the provided company ID, branch code, and item type.</li>
	 *   <li>Returns a list of {@link ListItemValue} entities with the matching conditions.</li>
	 * </ul>
	 * </p>
	 *
	 * @param companyId the company ID to filter the predefined values.
	 * @param branchCode the branch code to filter the predefined values.
	 * @return a list of {@link ListItemValue} entities matching the given filters and the maximum amend ID for each item ID.
	 * @throws Exception if any error occurs during the query execution or result retrieval.
	 * 
	 * @see ListItemValue
	 */
	private List<ListItemValue> findingActionDropDownByMaxAmendId(
			String companyId, String branchCode) throws Exception {
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
		Root<ListItemValue> root = query.from(ListItemValue.class);
		
		Subquery<Integer> subquery = query.subquery(Integer.class);
		Root<ListItemValue> subroot = subquery.from(ListItemValue.class);
		
		subquery.select(cb.max(subroot.get("amendId")));
		subquery.where(
				cb.equal(subroot.get("companyId"), root.get("companyId")),
				cb.equal(subroot.get("branchCode"), root.get("branchCode")),
				cb.equal(subroot.get("itemId"), root.get("itemId"))
				);
		
		Predicate companyfilter = cb.equal(root.get("companyId"), companyId);
		Predicate branchfilter = cb.equal(root.get("branchCode"), branchCode);
		Predicate itemTypeFilter = cb.equal(root.get("itemType"), ITEM_TYPE);
		Predicate amendIdFilter = cb.equal(root.get("amendId"), subquery);
		
		query.select(root)
		.where(cb.and(companyfilter, branchfilter, itemTypeFilter, amendIdFilter));
		
		return entityManager.createQuery(query).getResultList();		
	}
}
