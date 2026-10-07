package com.maan.eway.Rtsa;

import com.maan.eway.Rtsa.Req.GetRegDetailsReq;
import com.maan.eway.common.res.CommonRes;

public interface RSTAService {

	CommonRes getToken();

	CommonRes GetRegDetails(GetRegDetailsReq req);

	CommonRes getRtsaList(String regNo);

}
