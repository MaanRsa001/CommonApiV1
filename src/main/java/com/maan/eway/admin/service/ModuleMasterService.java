package com.maan.eway.admin.service;

import com.maan.eway.admin.req.ModuleIdsInsReq;
import com.maan.eway.admin.req.ModuleIdsReq;
import com.maan.eway.common.res.CommonRes;

public interface ModuleMasterService {

	CommonRes getModuleIdsList(ModuleIdsReq req);

	CommonRes insertModuleIds(ModuleIdsInsReq req);

}
