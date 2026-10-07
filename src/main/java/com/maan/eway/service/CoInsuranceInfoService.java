package com.maan.eway.service;


import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.req.CoInsuranceDetails;

public interface CoInsuranceInfoService  {
	
	CommonRes CoInsuranceInfosaveupdate(@RequestBody CoInsuranceDetails req);

	List<Error> validatecoinsurancedetails(CoInsuranceDetails req);

	CommonRes CoInsuranceInfodelete(String QUOTENO);
    CommonRes getAllByQuoteNo(String QUOTENO);





	
  















	

	
}
