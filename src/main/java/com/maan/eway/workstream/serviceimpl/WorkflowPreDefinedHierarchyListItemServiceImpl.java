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

import com.maan.eway.bean.ListItemValue;
import com.maan.eway.error.Error;
import com.maan.eway.workstream.request.PreDefinedHierarchyGetReq;
import com.maan.eway.workstream.response.ListItemValueRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class WorkflowPreDefinedHierarchyListItemServiceImpl {
	private static final Logger log = LogManager.getLogger(WorkflowFactorRateRequestDetailServiceImpl.class);
	private static final String ITEM_TYPE = "WORKFLOW_PREDEFINED_HIERARCHY";
	
	private EntityManager entityManager;
	private ModelMapper mapper;
	
	@Autowired
	public WorkflowPreDefinedHierarchyListItemServiceImpl(EntityManager entityManager, ModelMapper mapper) {
		this.entityManager = entityManager;
		this.mapper = mapper;
	}

	public List<Error> validateParametersOfPreDefinedHierarchyReq(PreDefinedHierarchyGetReq req) {		
		List<Error> errors = new ArrayList<>();
		
		if(req.getCompanyId() == null || req.getCompanyId().isBlank()) {
			errors.add(new Error("1", "CompanyId", "CompanyId Should Not Be Blank"));
		}
		if(req.getBranchCode() == null || req.getBranchCode().isBlank()) {
			errors.add(new Error("2", "BranchCode", "BranchCode Should Not Be Blank"));
		}
		return errors;		
	}
	
	
	/**
	 * Retrieves a list of predefined hierarchy items based on the provided company ID and branch code.
	 * It first fetches the items by calling the {@link #findingPreDefinedHierarchyByMaxAmendId(String, String)} 
	 * method and then maps the result to a list of {@link ListItemValueRes} objects.
	 *
	 * <p>
	 * This method handles the mapping of {@link ListItemValue} entities to {@link ListItemValueRes} 
	 * objects, using the provided request parameters (company ID and branch code).
	 * </p>
	 *
	 * @param req the request object containing the company ID and branch code to filter the predefined hierarchy items.
	 * @return a list of {@link ListItemValueRes} objects corresponding to the predefined hierarchy items, 
	 *         or {@code null} if an error occurs during the process.
	 * @throws Exception if an error occurs during the retrieval or mapping process.
	 * 
	 * <p>
	 * The following actions are performed by this method:
	 * <ul>
	 *   <li>Fetches the predefined items by calling {@link #findingPreDefinedHierarchyByMaxAmendId(String, String)}.</li>
	 *   <li>Maps the retrieved {@link ListItemValue} entities to {@link ListItemValueRes} objects.</li>
	 *   <li>Returns the list of mapped {@link ListItemValueRes} objects.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see ListItemValueRes
	 * @see ListItemValue
	 */
	public List<ListItemValueRes> getPreDefinedHierarchy(PreDefinedHierarchyGetReq req) {
		try {
			List<ListItemValue> allPredefinedItems = findingPreDefinedHierarchyByMaxAmendId(
					req.getCompanyId(), req.getBranchCode());
			
			return allPredefinedItems.stream()
					.map(item -> mapper.map(item, ListItemValueRes.class))
					.toList();
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}
	}
	
	
	/**
	 * Retrieves a list of {@link ListItemValue} entities that match the given company ID and branch code, 
	 * and have the maximum amendment ID for each item ID within the provided company and branch.
	 *
	 * <p>
	 * This method uses a subquery to find the maximum amendment ID for each item, then filters the results 
	 * based on the company ID, branch code, item type, and the maximum amendment ID.
	 * </p>
	 *
	 * @param companyId the ID of the company to filter the results by.
	 * @param branchCode the branch code to filter the results by.
	 * @return a list of {@link ListItemValue} entities that match the specified company ID, branch code, 
	 *         and have the maximum amendment ID for each item, or an empty list if no matching records are found.
	 * @throws Exception if an error occurs during the query execution or entity retrieval.
	 * 
	 * <p>
	 * The following actions are performed by this method:
	 * <ul>
	 *   <li>Creates a subquery to find the maximum amendment ID for each item ID.</li>
	 *   <li>Filters the results based on the provided company ID, branch code, and item type.</li>
	 *   <li>Applies the maximum amendment ID condition to the main query.</li>
	 *   <li>Returns a list of {@link ListItemValue} entities matching the criteria.</li>
	 * </ul>
	 * </p>
	 * 
	 * @see ListItemValue
	 */
	private List<ListItemValue> findingPreDefinedHierarchyByMaxAmendId(
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
