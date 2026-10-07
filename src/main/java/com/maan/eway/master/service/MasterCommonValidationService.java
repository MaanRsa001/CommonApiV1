package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.CompanyMasterValidateReq;
import com.maan.eway.master.req.GlobalCommonValidationReq;
import com.maan.eway.master.req.NormalMasterValidateReq;

public interface MasterCommonValidationService {

	List<Error> validateGlobalMaster(GlobalCommonValidationReq req);

	List<Error> validateCompanyMaster(CompanyMasterValidateReq req);

	List<Error> validateNoramlMaster(NormalMasterValidateReq req);
}
