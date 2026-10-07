package com.maan.eway.update;

import java.util.List;

import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.UpdateCustomerDetailsReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.res.SuccessRes;

public interface UpdateCustomerService {

	SuccessRes updateCustomerDetails(EserviceCustomerSaveReq req);

	CustomerDetailsGetRes getCustomerReferenceNo(GetCustomerReq req);

	SuccessRes updateTiraCustomerDetails(UpdateCustomerDetailsReq req);

	UpdateCustomerDetailsReq getTiraCustomerDetails(UpdateCustomerDetailsReq req);

	List<String> tiraValidateCustomerDetails(UpdateCustomerDetailsReq req);

}
