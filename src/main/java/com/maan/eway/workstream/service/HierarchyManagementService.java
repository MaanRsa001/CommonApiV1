/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.workstream.request.HierarchyManagementGetReq;
import com.maan.eway.workstream.request.HierarchyManagementSaveReq;
import com.maan.eway.workstream.response.HierarchyRes;

public interface HierarchyManagementService {
	
	public List<Error> validateParametersForHierarchySaveReq(HierarchyManagementSaveReq req);
	
	public List<Error> validateParametersForHierarchyGetReq(HierarchyManagementGetReq req);
	
	public SuccessRes saveAllHierarchyManagement(HierarchyManagementSaveReq req);
	
	public List<HierarchyRes> getAllHierarchyManagement(Integer companyId, Integer productId);
	
	public List<Integer> retrieveAllLevelsForProduct(Integer companyId, Integer productId);
	
}
