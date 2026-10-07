package com.maan.eway.admin.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.req.ModuleIdsInsReq;
import com.maan.eway.admin.req.ModuleIdsReq;
import com.maan.eway.admin.res.ModuleIdsRes;
import com.maan.eway.admin.service.ModuleMasterService;
import com.maan.eway.bean.ModuleMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.repository.ModuleMasterRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
@Service
public class ModuleMasterServiceImpl implements ModuleMasterService{

private Logger log = LogManager.getLogger(ModuleMasterServiceImpl.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ModuleMasterRepo repo;
	
	@Override
	public CommonRes getModuleIdsList(ModuleIdsReq req) {
		CommonRes res = new CommonRes();
		List<ModuleIdsRes> reslist = new ArrayList<ModuleIdsRes>();
		List<ModuleMaster> lsit = new ArrayList<ModuleMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<String> moduleIds = req.getModuleIds();
			if (moduleIds == null || moduleIds.isEmpty()) {
				lsit = repo.findByCompanyIdAndStatus("99999", "Y");
			} else {
				lsit = repo.findByCompanyIdAndStatusAndModuleIdIn("99999", "Y", req.getModuleIds());
			}
			if (lsit != null) {
				for (ModuleMaster ms : lsit) {
					ModuleIdsRes msres = new ModuleIdsRes();
					msres = dozerMapper.map(ms, ModuleIdsRes.class);
					msres.setModuleLOGO(ms.getModuleLOGO());
					msres.setModuleURL(ms.getModuleURL());
					reslist.add(msres);
				}
			}
			res.setCommonResponse(reslist);
			res.setMessage("Success");
			res.setErroCode(0);
			res.setIsError(false);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details => getReferralPendinglist" + e.getMessage());
			res.setCommonResponse(null);
			res.setMessage("Error in getModuleIdsList" + e.getMessage());
			res.setErroCode(1);
			res.setIsError(true);
		}

		return res;
	}

	@Override
	public CommonRes insertModuleIds(ModuleIdsInsReq req) {
		CommonRes res = new CommonRes();
		ModuleMaster module = new ModuleMaster();
		try {
			if (req.getModuleId() != null) {
				module = repo.findByCompanyIdAndModuleId("99999", req.getModuleId());
			}
			if (module != null) {
				module.setModuleId(req.getModuleId());
			} else {
				module = new ModuleMaster();
				module.setModuleId(req.getModuleId() == null ? 0 : req.getModuleId());
				module.setEntryDate(new Date());
			}
			module.setCompanyId(StringUtils.isBlank(req.getCompanyId()) ? "99999" : req.getCompanyId());
			module.setBranchCode(StringUtils.isBlank(req.getBranchCode()) ? "99999" : req.getBranchCode());
			module.setCreatedBy(StringUtils.isBlank(req.getCreatedBy()) ? "" : req.getCreatedBy());
			module.setDisplayOrder((req.getDisplayOrder() == null) ? null : req.getDisplayOrder());
			module.setDisplayYn(StringUtils.isBlank(req.getDisplayYn()) ? "" : req.getDisplayYn());
			module.setModuleName(StringUtils.isBlank(req.getModuleName()) ? "" : req.getModuleName());
			module.setModuleNameLocal(StringUtils.isBlank(req.getModuleNameLocal()) ? "" : req.getModuleNameLocal());
			module.setModuleRemarks(StringUtils.isBlank(req.getModuleRemarks()) ? "" : req.getModuleRemarks());
			module.setStatus("Y");
			module.setUsertype(StringUtils.isBlank(req.getUsertype()) ? "" : req.getUsertype());
			module.setModuleLOGO(StringUtils.isBlank(req.getModuleLOGO()) ? "" : req.getModuleLOGO());
			module.setModuleURL(StringUtils.isBlank(req.getModuleURL()) ? "" : req.getModuleURL());

			repo.saveAndFlush(module);
			res.setCommonResponse(module);
			res.setMessage("Success");
			res.setErroCode(0);
			res.setIsError(false);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details => getReferralPendinglist" + e.getMessage());
			res.setCommonResponse(null);
			res.setMessage("Error in getModuleIdsList" + e.getMessage());
			res.setErroCode(1);
			res.setIsError(true);
		}
		return res;
	}

}
