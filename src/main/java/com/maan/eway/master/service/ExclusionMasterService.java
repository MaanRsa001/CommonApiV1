package com.maan.eway.master.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.maan.eway.common.req.ExclusionMasterDropdownReq;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ExclusionChangeStatusReq;
import com.maan.eway.master.req.ExclusionMasterGetReq;
import com.maan.eway.master.req.ExclusionMasterGetallReq;
import com.maan.eway.master.req.ExclusionMasterReq;
import com.maan.eway.master.req.ExclusionMasterSaveReq;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.res.ExclusionMasterRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface ExclusionMasterService {

	List<String> validateExclusion(ExclusionMasterSaveReq req);

	SuccessRes saveExclusion(ExclusionMasterSaveReq req, MultipartFile documentFile);

	List<ExclusionMasterRes> getallExclusion(ExclusionMasterGetallReq req);

	List<ExclusionMasterRes> getActiveExclusion(ExclusionMasterGetallReq req);

	ExclusionMasterRes getByExclusionId(ExclusionMasterGetReq req);

	SuccessRes changeStatusOfExclusion(ExclusionChangeStatusReq req);
	List<DropDownRes> getExclusionMasterDropdown(ExclusionMasterDropdownReq req);

	List<ExclusionMasterRes> getallNonSelectedExclusion(NonSelectedClausesGetAllReq req);

	List<Error> validateExclusionList(List<ExclusionMasterReq> req);

	SuccessRes saveExclusionList(List<ExclusionMasterReq> req);

}
