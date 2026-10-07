package com.maan.eway.overalldiscount;

import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;

public interface EserviceSaveFleetService {

	EserviceMotorDetailsSaveRes updateFleetDetails(FleetDetailsSaveReq req,String tokens);
}
