package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.bean.IntegrationMappingDetailsMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterGetAllReq;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterGetReq;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterSaveReq;

public interface IntegrationMappingDetailsMasterService {
	
	public List<String> validateIntegrationMappingDetails(IntegrationMappingDetailsMasterSaveReq req);
	
	public CommonRes saveIntegrationMappingDetails(IntegrationMappingDetailsMasterSaveReq req);
	
	public CommonRes updateIntegrationMappingDetails(IntegrationMappingDetailsMasterSaveReq req);
	
	public CommonRes getIntegrationMappingDetails(IntegrationMappingDetailsMasterGetReq req);
	
	public CommonRes getAllIntegrationMappingDetails(IntegrationMappingDetailsMasterGetAllReq req);
	
	
	public String retriveProductNameFromCompanyProductMaster(Integer companyId, Integer productId);
	
	public String retriveSectionNameFromProductSectionMaster(Integer companyId, Integer productId, Integer sectionId);
	
	public String retrivePolicyTypeNameFromPolicyTypeMaster(Integer companyId, Integer productId, Integer policyTypeId);
	
	public List<IntegrationMappingDetailsMaster> selectTopRecordsOfEachGroup(Integer companyId, Integer productId,  Integer sectionId, Integer policyTypeId);
	
}
