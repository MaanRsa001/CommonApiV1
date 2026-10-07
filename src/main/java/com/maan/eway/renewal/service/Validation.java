package com.maan.eway.renewal.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.maan.eway.error.Error;
import com.maan.eway.renewal.req.RenewalVehicleReq;

@Component
public class Validation {

	public List<Error> validateReq(RenewalVehicleReq req) {
		List<Error> list = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getPolicyNo()))
			{
				list.add(new Error("add PolicyNo" , "", ""));
			}
			if(StringUtils.isBlank(req.getBodyType()))
			{
				list.add(new Error("add bodytype", "", ""));
			}
			if(StringUtils.isBlank(req.getMake()))
			{
				list.add(new Error("add make", "", ""));
			}
			if(StringUtils.isBlank(req.getModel()))
			{
				list.add(new Error("add model", "", ""));
			}
			if(StringUtils.isBlank(req.getBodyType()))
			{
				list.add(new Error("add bodyType", "", ""));
			}
			if(StringUtils.isBlank(req.getVehicleUsage()))
			{
				list.add(new Error("add vehicleUsage", "", ""));
			}
			if(StringUtils.isBlank(req.getPolicyType()))
			{
				list.add(new Error("add policyType", "", ""));
			}
			if(StringUtils.isBlank(req.getSumInsured()))
			{
				list.add(new Error("add sumInsured", "", ""));
			}
			if(StringUtils.isBlank(req.getCreatedBy()))
			{
				list.add(new Error("add createdBy", "", ""));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
		}

}
