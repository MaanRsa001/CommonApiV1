package com.maan.eway.oman;

import java.util.List;

import com.maan.eway.master.req.StateMasterDropDownReq;

public interface OmanPostalCodeMasterService {

	List<OmanDropDownRes> getOmanPostalCodeMasterDropdown(StateMasterDropDownReq req);

}
