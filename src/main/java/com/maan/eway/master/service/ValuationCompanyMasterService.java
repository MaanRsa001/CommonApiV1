package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.master.req.ValuationCompanyChangeStatusReq;
import com.maan.eway.master.req.ValuationCompanyMasterDropdownReq;
import com.maan.eway.master.req.ValuationCompanyMasterGetReq;
import com.maan.eway.master.req.ValuationCompanyMasterGetallReq;
import com.maan.eway.master.req.ValuationCompanyMasterSaveReq;
import com.maan.eway.master.res.ValuationCompanyMasterRes;
import com.maan.eway.res.DropDownRes;



public interface ValuationCompanyMasterService {

	List<String> validateValuationCompany(ValuationCompanyMasterSaveReq req);

	SuccessRes saveValuationCompany(ValuationCompanyMasterSaveReq req);

	List<ValuationCompanyMasterRes> getActiveValuationCompany(ValuationCompanyMasterGetallReq req);

	ValuationCompanyMasterRes getByValuationCompanyCode(ValuationCompanyMasterGetReq req);

	SuccessRes changeStatusOfCompany(ValuationCompanyChangeStatusReq req);

	List<DropDownRes> getValuationCompanyMasterDropdown(ValuationCompanyMasterDropdownReq req);

	
}
