package com.maan.eway.master.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.AgricultureMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.AgricultureCropListReq;
import com.maan.eway.master.res.AgricultureCropListResp;
import com.maan.eway.master.res.CropListResp;
import com.maan.eway.master.service.AgricultureMasterService;
import com.maan.eway.repository.AgriCultureMasterRepository;
import com.maan.eway.res.DropDownRes;

@Service
public class AgricultureMasterServiceImpl implements AgricultureMasterService{
	
	@Autowired
	private AgriCultureMasterRepository agriCultureMasterRepo;
	
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
	    Set<Object> seen = ConcurrentHashMap.newKeySet();
	    return t -> seen.add(keyExtractor.apply(t));
	}


	@Override
	public List<Error> validationCropList(AgricultureCropListReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if (StringUtils.isBlank(req.getCompanyId())) {
				errors.add(new Error("01", "InsuranceId", "Please Enter the InsuranceId"));
			}
			if (StringUtils.isBlank(req.getRegion())) {
				errors.add(new Error("02", "Region", "Please Enter the Region"));
			}if (StringUtils.isBlank(req.getProductId())) {
				errors.add(new Error("03", "ProductId", "Please Enter the ProductId"));
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return errors;
	}

	@Override
	public List<AgricultureCropListResp> getCropList(AgricultureCropListReq req) {
		List<AgricultureCropListResp> respList = new ArrayList<>();
		 Map<String, AgricultureCropListResp> districtMap = new LinkedHashMap<>();
		try {
			List<AgricultureMaster> masterValues = agriCultureMasterRepo.findByProvinceIdAndProductIdAndCompanyIdOrderByDistrictId(Integer.valueOf(req.getRegion()),
					Integer.valueOf(req.getProductId()),Integer.valueOf(req.getCompanyId()));
			List<Integer> districtIds = masterValues.stream().map(AgricultureMaster :: getDistrictId).distinct().toList();
			System.out.println("DistrictIds :"+districtIds);
			if(!masterValues.isEmpty()) {
				for(AgricultureMaster listvalues : masterValues) {
					String districtKey = listvalues.getDistrictId().toString();
					
					districtMap.computeIfAbsent(districtKey, key -> {
	                    AgricultureCropListResp districtResp = new AgricultureCropListResp();
	                    districtResp.setDistrictId(listvalues.getDistrictId().toString());
	                    districtResp.setDistrictDesc(listvalues.getDistrictDesc());
	                    districtResp.setAez(listvalues.getAez().toString());
	                    districtResp.setCropList(new ArrayList<>());
	                    
	                    return districtResp;
					});
					
					//CropList
					CropListResp cropList = new CropListResp();
					cropList.setCropId(listvalues.getCropId().toString());
					cropList.setCropDesc(listvalues.getCropDesc());
					cropList.setPerHACost(listvalues.getPerHACost().toString());
					
					districtMap.get(districtKey).getCropList().add(cropList);
					/*
					AgricultureCropListResp list = new AgricultureCropListResp();
					list.setDistrictId(listvalues.getDistrictId().toString());
					list.setDistrictDesc(listvalues.getDistrictDesc());
					list.setAez(listvalues.getAez().toString());
					//list.setCropId(listvalues.getCropId().toString());
					//list.setCropDesc(listvalues.getCropDesc());
					//list.setPerHACost(listvalues.getPerHACost().toString());
					*/
					//respList.add(list);
				}
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>(districtMap.values());
	}

	@Override
	public List<DropDownRes> getRegionList(AgricultureCropListReq req) {

		List<DropDownRes> listRegion = new ArrayList<>();
		try {
			List<AgricultureMaster> regionList = agriCultureMasterRepo.findByCompanyId(Integer.valueOf(req.getCompanyId()));
			if(!regionList.isEmpty()) {
				regionList = regionList.stream().filter(reg -> reg.getStatus().equalsIgnoreCase("Y")).toList();
				regionList = regionList.stream().filter(distinctByKey(AgricultureMaster :: getProvinceId)).toList();
				regionList = regionList.stream().sorted(Comparator.comparing(AgricultureMaster :: getProvinceDesc, Comparator.nullsLast(String :: compareToIgnoreCase))).toList();
			}
			for(AgricultureMaster list : regionList) {
				DropDownRes resp = new DropDownRes();
				resp.setCode(list.getProvinceId().toString());
				resp.setCodeDesc(list.getProvinceDesc());
				resp.setStatus(list.getStatus());
				
				listRegion.add(resp);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return listRegion;
	}

}
