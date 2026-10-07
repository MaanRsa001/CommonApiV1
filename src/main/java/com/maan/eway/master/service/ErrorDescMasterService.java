package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.ErrorDescMasterGetReq;
import com.maan.eway.master.req.ErrorDescMasterSaveReq;
import com.maan.eway.master.req.ErrorMasterGetAllReq;
import com.maan.eway.master.res.ErrorDescMasterRes;
import com.maan.eway.res.SuccessRes;

public interface ErrorDescMasterService {

	List<String> validateErrorDesc(ErrorDescMasterSaveReq req);
	
	SuccessRes inserterrordesc(ErrorDescMasterSaveReq req);
	
	List<ErrorDescMasterRes> getallErrorDetails(ErrorMasterGetAllReq  req);
	
	ErrorDescMasterRes getbyerrorcodeDetails(ErrorDescMasterGetReq req);
	
}
