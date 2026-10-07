package com.maan.eway.common.service;

import com.maan.eway.common.req.GetSectionReq;
import com.maan.eway.common.res.GetSectionRes;

public interface CompanyProductSectionCoverService {

	GetSectionRes getOptedAndUnoptedSection(GetSectionReq req);

	GetSectionRes getOptedAndUnoptedSectionCover(GetSectionReq req);

	GetSectionRes getOptedLocationId(GetSectionReq req);
}
