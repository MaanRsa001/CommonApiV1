package com.maan.eway.notification.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.notification.req.FollowupDetailsGetReq;
import com.maan.eway.notification.req.FollowupDetailsGetallReq;
import com.maan.eway.notification.req.FollowupDetailsSaveReq;
import com.maan.eway.notification.res.FollowUpDetailsPageRes;
import com.maan.eway.notification.res.FollowUpDetailsRes;
import com.maan.eway.res.SuccessRes;

public interface FollowupDetailsService {

	List<Error> validateFollowupDetails(FollowupDetailsSaveReq req);

	SuccessRes saveFollowupDetails(FollowupDetailsSaveReq req);

	FollowUpDetailsRes getclientdetailsid(FollowupDetailsGetReq req);

	FollowUpDetailsPageRes getfollowupDetails(FollowupDetailsGetallReq req);


}
