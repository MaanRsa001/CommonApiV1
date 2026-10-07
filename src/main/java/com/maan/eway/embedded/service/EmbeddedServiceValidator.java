package com.maan.eway.embedded.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.maan.eway.embedded.request.Inalipa;

@Service
public class EmbeddedServiceValidator {

	public List<String> validateRequest(Inalipa request) {
		List<String> list=new ArrayList<String>();
		if(StringUtils.isBlank(request.getInsurerName())) {
			list.add("Insured Name is Mandatory");			
		}
		if(StringUtils.isBlank(request.getMobileCode())) {
			list.add("Mobile Code is Mandatory");			
		}
		if(StringUtils.isBlank(request.getMobileNumber())) {
			list.add("Mobile Number is Mandatory");			
		}
		if(StringUtils.isBlank(request.getPlanOpted())) {
			list.add("Plan Opted is Mandatory");			
		}else if( !("97".equals(request.getPlanOpted()) || "98".equals(request.getPlanOpted())  || "99".equals(request.getPlanOpted())) ) {
			list.add("Plan Opted is Invalid");
		}
		
		if(StringUtils.isBlank(request.getTransactionNo())) {
			list.add("Transaction No  is Mandatory");			
		}
	/*	if(request.getOrderValue()==null) {
			list.add("OrderValue is Mandatory");			
		}else if(request.getOrderValue().doubleValue()<=0D) {
			list.add("OrderValue is Not Accept Negative Value");
		}
	*/	
		return list;
	}

}
