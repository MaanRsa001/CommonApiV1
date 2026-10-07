package com.maan.eway.upr.service;

import java.util.List;

import com.maan.eway.upr.dto.UprPolicyResponse;

public interface UprService {


	List<UprPolicyResponse> saveUpr(String policyno);

}
