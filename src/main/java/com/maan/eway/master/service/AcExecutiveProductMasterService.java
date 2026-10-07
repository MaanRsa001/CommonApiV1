package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.AcExecutiveGetReq;
import com.maan.eway.master.req.AcExecutiveGetallReq;
import com.maan.eway.master.req.AcExecutiveNonSelectedReq;
import com.maan.eway.master.req.AcExecutiveProductDropDownReq;
import com.maan.eway.master.req.AcExecutiveSaveReq;
import com.maan.eway.master.req.AcExecutiveUpdateReq;
import com.maan.eway.master.res.AcExecutiveGetRes;
import com.maan.eway.master.res.AcExecutiveProductDropdownRes;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.res.SuccessRes;

public interface AcExecutiveProductMasterService {

	List<Error> validateacexecutive(AcExecutiveSaveReq req);

	SuccessRes saveacexecutive(AcExecutiveSaveReq req);

	SuccessRes updateacexecutive(AcExecutiveUpdateReq req);

	AcExecutiveGetRes getacexecutive(AcExecutiveGetReq req);

	List<AcExecutiveGetRes> getallacexecutive(AcExecutiveGetallReq req);

	List<CompanyProductMasterRes> getallNonSelectedProducts(AcExecutiveNonSelectedReq req);

	List<Error> validateupdateacexecutive(AcExecutiveUpdateReq req);

	List<AcExecutiveProductDropdownRes> dropdownacexecutive(AcExecutiveProductDropDownReq req);

}
