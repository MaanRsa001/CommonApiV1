package com.maan.eway.viewAll.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.viewAll.dto.viewAllReq;

public interface UgandaDebiteJsonService {

	CommonRes jsonFrame(viewAllReq req);
	
	CommonRes generateDebitNoteUganda(viewAllReq req);


}
