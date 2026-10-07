package com.maan.eway.endorsment.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.endorsment.request.RemoveLocationReq;

public interface RemoveLocationService {

	CommonRes cancelPolicyLocation(RemoveLocationReq request, String tokens);

}
