package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.GetMasterTableIdsReq;
import com.maan.eway.master.req.OriginatingCountryDropdownReq;
import com.maan.eway.master.res.CountryMasterRes;
import com.maan.eway.res.SuccessRes;

public interface MasterTablesDescAndIdService {

	SuccessRes getIdsfromMastersTable(GetMasterTableIdsReq req);

	List<CountryMasterRes> getOriginatingCountryDropdown(OriginatingCountryDropdownReq req);

}
