package com.maan.eway.bond.service;


import com.maan.eway.bond.Dto.BondInformationRequest;
import com.maan.eway.bond.Dto.BondInformationRes;
import com.maan.eway.bond.Dto.GetAllReq;

public interface BondInformationService {

	BondInformationRes save(BondInformationRequest req);

	BondInformationRequest getAllByFilters(GetAllReq req);

	

}
