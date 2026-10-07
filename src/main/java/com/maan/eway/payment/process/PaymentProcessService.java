package com.maan.eway.payment.process;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.payment.process.req.SavePaymentProcessReq;
import com.maan.eway.payment.process.req.StatusListReq;

public interface PaymentProcessService {

	CommonRes savePaymentProcess(SavePaymentProcessReq req);

	CommonRes getStatusList(String type, String status, StatusListReq req);

}
