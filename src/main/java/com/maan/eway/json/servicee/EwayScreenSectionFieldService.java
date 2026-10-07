package com.maan.eway.json.servicee;

import java.util.List;

import com.maan.eway.json.dto.AdminControllersRes;
import com.maan.eway.json.dto.EwayFieldRequest;
import com.maan.eway.json.dto.EwayScreenSection;
import com.maan.eway.json.dto.EwayScreenSectionFieldResponse;

public interface EwayScreenSectionFieldService {
	EwayScreenSectionFieldResponse create(EwayFieldRequest request);
	EwayScreenSectionFieldResponse update(Integer sno, Integer screenId, EwayFieldRequest request);
	EwayScreenSectionFieldResponse getById(Integer sno, EwayScreenSection req);
    List<EwayScreenSectionFieldResponse> getAll(EwayScreenSection req);
    void delete(Integer sno, Integer screenId);
    AdminControllersRes getAllControllers();
}
