package com.maan.eway.admin.service;

import java.util.List;

import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.res.DropDownRes;

public interface AdminDropDownService {

	List<DropDownRes> getgender(LovDropDownReq req);

	
	List<DropDownRes> getConstMaterial(LovDropDownReq req);

	List<DropDownRes> getOutbuildingConst(LovDropDownReq req);

	List<DropDownRes> getAboutBuilding(LovDropDownReq req);

	List<DropDownRes> getStateExtent(LovDropDownReq req);

	List<DropDownRes> getContentName(LovDropDownReq req);

	List<DropDownRes> getPropertyName(LovDropDownReq req);
	
	
	List<DropDownRes> getMobileCodes( LovDropDownReq req);

	List<DropDownRes> getBusinessType(LovDropDownReq req);


	List<DropDownRes> getSourceType(LovDropDownReq req);


	List<DropDownRes> getCommissionType(LovDropDownReq req);


	List<DropDownRes> getProRataType(LovDropDownReq req);


	


}
