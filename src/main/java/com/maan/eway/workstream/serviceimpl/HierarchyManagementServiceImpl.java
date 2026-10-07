/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
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
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.workstream.entity.HierarchyManagement;
import com.maan.eway.workstream.repository.HierarchyManagementRepository;
import com.maan.eway.workstream.request.Hierarchy;
import com.maan.eway.workstream.request.HierarchyManagementGetReq;
import com.maan.eway.workstream.request.HierarchyManagementSaveReq;
import com.maan.eway.workstream.response.HierarchyRes;
import com.maan.eway.workstream.service.HierarchyManagementService;

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
public class HierarchyManagementServiceImpl implements HierarchyManagementService {
	private static final Logger log = LogManager.getLogger(HierarchyManagementServiceImpl.class);
		
	private HierarchyManagementRepository hierarchyRepo;
	private ModelMapper mapper;	
	
	@Autowired
	public HierarchyManagementServiceImpl(HierarchyManagementRepository hierarchyRepo, ModelMapper mapper) {
		this.hierarchyRepo = hierarchyRepo;
		this.mapper = mapper;
	}
	
	@PersistenceContext
	private EntityManager em;



	public List<Error> validateParametersForHierarchySaveReq(HierarchyManagementSaveReq req) {
		List<Error> errors = new ArrayList<>();
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}
		if(req.getHierarchies() == null || req.getHierarchies().isEmpty()) {
			errors.add(new Error("14", "Hierarchies", "Hierarchies Should Not Be Null or Emplty"));
		}
		
		int rowNum = 1;
		for(Hierarchy hierarchy : req.getHierarchies()) {
			
			if(hierarchy.getHierarchyLevel() == null || hierarchy.getHierarchyLevel().isBlank()) {
				errors.add(new Error("51", "HierarchyLevel", "Hierarchy Level Should Not Be Blank for Row "+rowNum));
			}
			if(hierarchy.getHierarchyValue() == null) {
				errors.add(new Error("52", "HierarchyValue", "Hierarchy Value Should Not Be Null for Row "+rowNum));
			}
			rowNum++;
		}

		return errors;
	}

	public List<Error> validateParametersForHierarchyGetReq(HierarchyManagementGetReq req) {
		List<Error> errors = new ArrayList<>();
		if(req.getCompanyId() == null) {
			errors.add(new Error("11", "CompanyId", "CompanyId Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errors.add(new Error("12", "ProductId", "ProductId Should Not Be Null"));
		}

		return errors;
	}
	
	
	public SuccessRes saveAllHierarchyManagement(HierarchyManagementSaveReq req) {	
		SuccessRes res = new SuccessRes();
		try {
			List<HierarchyManagement> allHierarchy = hierarchyRepo.findAllByCompanyIdAndProductId(
					req.getCompanyId(), req.getProductId());
			
			if("Y".equalsIgnoreCase(req.getHierarchyYN())) {
							
				if(allHierarchy.isEmpty()) {
					List<HierarchyManagement> list = new ArrayList<>();
					for(Hierarchy hierarchy : req.getHierarchies()) {
						
						HierarchyManagement hierarchyManagement = HierarchyManagement.builder()
								.companyId(req.getCompanyId())
								.productId(req.getProductId())
								.hierarchyLevel(hierarchy.getHierarchyLevel())
								.hierarchyValue(hierarchy.getHierarchyValue())
								.canFinalize(hierarchy.isCanFinalize())
								.canEscalate(hierarchy.isCanEscalate())
								.build();
					
						list.add(hierarchyManagement);
					}
					hierarchyRepo.saveAllAndFlush(list);
					res.setResponse("Saved Successfully");
				}else {
					
					List<HierarchyManagement> list = new ArrayList<>();
					for(Hierarchy hierarchy : req.getHierarchies()) {
						
						HierarchyManagement hierarchyManagement = HierarchyManagement.builder()
								.companyId(req.getCompanyId())
								.productId(req.getProductId())
								.hierarchyLevel(hierarchy.getHierarchyLevel())
								.hierarchyValue(hierarchy.getHierarchyValue())
								.canFinalize(hierarchy.isCanFinalize())
								.canEscalate(hierarchy.isCanEscalate())
								.build();
					
						list.add(hierarchyManagement);
					}
					hierarchyRepo.saveAllAndFlush(list);
					res.setResponse("Update Successfully");
				}
			}else if("N".equalsIgnoreCase(req.getHierarchyYN())) {
				if(!allHierarchy.isEmpty()) {
					hierarchyRepo.deleteAll(allHierarchy);
					//removeHeirarchyLevels(req.getCompanyId(), req.getProductId());
					//int deletedRows = hierarchyRepo.deleteByCompanyIdAndProductId(req.getCompanyId(), req.getProductId());
					res.setResponse("Deleted Successfully");
				}
			}
			
			
		} catch (Exception e) {
			log.error("Exception occurred: {}", e.getMessage(), e);
			return null;
		}
		return res;
	}
	
	
	public List<HierarchyRes> getAllHierarchyManagement(Integer companyId, Integer productId) {
		List<HierarchyManagement> allHierarchy = hierarchyRepo.findAllByCompanyIdAndProductId(companyId, productId);
		return allHierarchy.stream()
				.map(level -> mapper.map(level, HierarchyRes.class))
				.toList();		
		}
	

	public List<Integer> retrieveAllLevelsForProduct(Integer companyId, Integer productId) {
		List<HierarchyManagement> allHierarchy = hierarchyRepo.findAllByCompanyIdAndProductId(companyId, productId);
	    
	    if (allHierarchy.isEmpty()) {
	        return List.of();
	    }

	    return allHierarchy.stream()
	            .map(HierarchyManagement::getHierarchyValue)
	            .sorted() 
	            .toList();
	}


	public List<DropDownRes> getHierarchyLevelDropdown(String companyId, String itemType) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			Root<ListItemValue> c = query.from(ListItemValue.class);
			query.select(c);
			
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ListItemValue> ocpm = amendId.from(ListItemValue.class);
			amendId.select(cb.max(ocpm.get("amendId")));
			Predicate a1 = cb.equal(ocpm.get("companyId"), companyId);
			Predicate a2 = cb.equal(ocpm.get("itemType"), itemType);
			Predicate a3 = cb.equal(ocpm.get("status"), "Y");
			Predicate a4 = cb.equal(ocpm.get("branchCode"), "99999");
			amendId.where(a1,a2,a3,a4);
			
			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("itemCode")));
			
			Predicate n1 = cb.equal(c.get("companyId"), companyId);
			Predicate n2 = cb.equal(c.get("itemType"), itemType);
			Predicate n3 = cb.equal(c.get("status"), "Y");
			Predicate n4 = cb.equal(c.get("branchCode"), "99999");
			Predicate n5 = cb.equal(c.get("amendId"), amendId);
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			for(ListItemValue values : list) {
				DropDownRes res = new DropDownRes();
				
				res.setCode(values.getItemCode());
				res.setCodeDesc(values.getItemValue());
				res.setTitletype(values.getItemType());
				res.setStatus(values.getStatus());
				
				resList.add(res);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	
		return resList;
	}
	
	
}
