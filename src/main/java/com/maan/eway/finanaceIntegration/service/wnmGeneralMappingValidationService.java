package com.maan.eway.finanaceIntegration.service;

import com.maan.eway.finanaceIntegration.req.ObjectMappingRequest;
import com.maan.eway.finanaceIntegration.res.WnmGeneralMappingRes;

import java.util.List;

public interface wnmGeneralMappingValidationService {

    List<String> validateCreate(WnmGeneralMappingRes req);

    List<String> validateUpdate(WnmGeneralMappingRes req);

    List<String> validateDelete(Long gmSysId);

    List<String> validateGetObjectMappings(ObjectMappingRequest req);


}
