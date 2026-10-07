package com.maan.eway.ticket;

import com.maan.eway.common.res.CommonRes;

public interface CustomerJobTrackerService {

	CommonRes customerTrackingDashboad(CustomerTrackDashboadReq req);

	CommonRes customerDashboad(CustomerTrackDashboadReq req);

	CommonRes customerStatusUpdate(CustomerTrackDashboadReq req);

}
