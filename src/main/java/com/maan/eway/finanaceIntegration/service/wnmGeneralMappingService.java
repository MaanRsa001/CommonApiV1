package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.res.ObjectMappingRes;
import com.maan.eway.finanaceIntegration.res.WnmGeneralMappingRes;

import java.util.List;

public interface wnmGeneralMappingService {


    List<WnmGeneralMappingRes> filterData(WnmGeneralMappingRes request);

    WnmGeneralMappingRes create(WnmGeneralMappingRes request);

    WnmGeneralMappingRes update(WnmGeneralMappingRes request);

    boolean delete(Long gmSysId);

    List<ObjectMappingRes> getObjectMappings(String tableName ,String companyId);
}
