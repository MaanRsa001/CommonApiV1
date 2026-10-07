package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.master.req.ExchangeChangeStatusReq;
import com.maan.eway.master.req.ExchangeMasterGetReq;
import com.maan.eway.master.req.ExchangeMasterGetallReq;
import com.maan.eway.master.req.ExchangeMasterSaveReq;
import com.maan.eway.master.res.ExchangeMasterGetRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

public interface ExchangeMasterService {

	List<String> validateInsertExchangeMaster(ExchangeMasterSaveReq req);

	SuccessRes insertExchangeMaster(ExchangeMasterSaveReq req);

	ExchangeMasterGetRes getExchangeMaster(ExchangeMasterGetReq req);

	List<ExchangeMasterGetRes> getallExchangeMaster(ExchangeMasterGetallReq req);

	List<ExchangeMasterGetRes> getActiveExchange(ExchangeMasterGetallReq req);

	List<DropDownRes> getExchangeMasterDropdown();

	SuccessRes changeStatusOfExchange(ExchangeChangeStatusReq req);

}
