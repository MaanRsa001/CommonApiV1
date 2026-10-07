package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.req.CoverReq;
import com.maan.eway.finanaceIntegration.req.SectionReq;
import com.maan.eway.finanaceIntegration.res.*;

import java.util.List;

public interface WnmUwAccountSetupService {

    List<WnmUwAccountSetupRes> filterData(WnmUwAccountSetupRes request);

    WnmUwAccountSetupRes create(WnmUwAccountSetupRes request);

    WnmUwAccountSetupRes update(WnmUwAccountSetupRes request);

    boolean delete(Long pasSysId);

    List<ProductDTO> getActiveProducts();

    List<SectionDTO> getSectionsByProductRangeAndCompany(SectionReq request);

    List<CoverDTO> getCoversByProductRangeAndCompany(CoverReq request);

    List<CompanyMasterRes> getCompany(CompanyMasterRes request);

    List<BranchMasterAccRes> getBranches(CompanyMasterRes request);

    List<DepartmentRes> getDepartments(CompanyMasterRes request);

    List<NarrationResponse> getNarrationDetails(String narrationAcntType, String narrationAcntSubType);
}