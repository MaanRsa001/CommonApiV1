package com.maan.eway.common.service;

import java.util.List;

import com.maan.eway.common.req.EserviceCustomerSaveReq;
import com.maan.eway.common.req.GetCustomerDetailsReq;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.res.SuccessRes;

public interface EserviceCustomerHandler {
    List<String> validateCustomerDetails(EserviceCustomerSaveReq req);
    CustomerDetailsGetRes getCustomerDetails(GetCustomerDetailsReq req);
    SuccessRes saveCustomerDetails(EserviceCustomerSaveReq req);
}
