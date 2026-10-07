package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.AgricultureCropListReq;
import com.maan.eway.master.res.AgricultureCropListResp;
import com.maan.eway.res.DropDownRes;

public interface AgricultureMasterService {

	List<Error> validationCropList(AgricultureCropListReq req);

	List<AgricultureCropListResp> getCropList(AgricultureCropListReq req);

	List<DropDownRes> getRegionList(AgricultureCropListReq req);

}
