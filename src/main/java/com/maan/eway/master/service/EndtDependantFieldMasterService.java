package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.EndtDependantFieldChangeStatusReq;
import com.maan.eway.master.req.EndtDependantFieldMasterSaveReq;
import com.maan.eway.master.req.EndtDependantFieldsGetallReq;
import com.maan.eway.master.req.EndtDependantMasterGetReq;
import com.maan.eway.master.res.EndtDependantFieldsGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface EndtDependantFieldMasterService {

	List<String> validateDependantField(EndtDependantFieldMasterSaveReq req);

	SuccessRes saveDependantField(EndtDependantFieldMasterSaveReq req);

	List<EndtDependantFieldsGetRes> getallDependantField(EndtDependantFieldsGetallReq req);

	List<EndtDependantFieldsGetRes> getActiveDependantField(EndtDependantFieldsGetallReq req);

	EndtDependantFieldsGetRes getByDependantFieldId(EndtDependantMasterGetReq req);

	SuccessRes changeStatusOfDependantField(EndtDependantFieldChangeStatusReq req);

	List<DropDownRes> getDependantMasterDropdown(EndtDependantFieldsGetallReq req);

}
