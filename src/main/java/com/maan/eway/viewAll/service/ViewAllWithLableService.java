package com.maan.eway.viewAll.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.viewAll.dto.DropDownReq;
import com.maan.eway.viewAll.dto.FieldQueryTableQueryDto;
import com.maan.eway.viewAll.dto.GetALLCoverDto;
import com.maan.eway.viewAll.dto.OverAllResForView;
import com.maan.eway.viewAll.dto.RiskFieldFlowDto;
import com.maan.eway.viewAll.dto.viewAllReq;

public interface ViewAllWithLableService {

	OverAllResForView viewAllinKeyAndValue(viewAllReq req);
	CommonRes insertcoverForRFL(RiskFieldFlowDto req);
	CommonRes getAllcoverForRFL(GetALLCoverDto req);
	CommonRes getQueryDropDown();
	CommonRes getQueryInsert(FieldQueryTableQueryDto req);
	CommonRes getFieldDropDown(DropDownReq req);
	CommonRes deleteRecord(viewAllReq req);
	CommonRes omanRecordForall(viewAllReq req);

}
