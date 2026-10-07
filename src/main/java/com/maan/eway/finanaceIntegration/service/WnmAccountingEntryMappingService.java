package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.res.WnmAccountingEntryMappingRes;

import java.util.List;

public interface WnmAccountingEntryMappingService {

    List<WnmAccountingEntryMappingRes> filterData(WnmAccountingEntryMappingRes request);

    WnmAccountingEntryMappingRes create(WnmAccountingEntryMappingRes request);

    WnmAccountingEntryMappingRes update(WnmAccountingEntryMappingRes request);

    boolean delete(Long aemSysId);
}