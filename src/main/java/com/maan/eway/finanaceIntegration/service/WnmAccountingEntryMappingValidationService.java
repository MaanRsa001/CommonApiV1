package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.res.WnmAccountingEntryMappingRes;

import java.util.List;

public interface WnmAccountingEntryMappingValidationService{

    List<String> validateCreate(WnmAccountingEntryMappingRes req);

    List<String> validateUpdate(WnmAccountingEntryMappingRes req);

    List<String> validateDelete(Long aemSysId);
}
