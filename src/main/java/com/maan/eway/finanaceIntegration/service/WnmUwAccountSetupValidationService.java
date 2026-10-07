package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.req.CoverReq;
import com.maan.eway.finanaceIntegration.req.SectionReq;
import com.maan.eway.finanaceIntegration.res.CompanyMasterRes;
import com.maan.eway.finanaceIntegration.res.WnmUwAccountSetupRes;

import java.util.List;

public interface WnmUwAccountSetupValidationService {

    List<String> validateCreate(WnmUwAccountSetupRes req);

    List<String> validateUpdate(WnmUwAccountSetupRes req);

    List<String> validateDelete(Long pasSysId);

    List<String> validateSectionRequest(SectionReq request);

    List<String> validateCoverRequest(CoverReq request);

    List<String> validateGetCompany(CompanyMasterRes req);

    List<String> validateGetBranches(CompanyMasterRes req);

    List<String> validateGetDepartments(CompanyMasterRes req);

    List<String> validateGetNarrationDetails(WnmUwAccountSetupRes req);
}
