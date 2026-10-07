package com.maan.eway.common.service;

import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.SequenceGenerateRes;

public interface SequenceGenerateService {

	SequenceGenerateRes generateSequence(SequenceGenerateReq req);

	

}
