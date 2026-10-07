package com.maan.eway.payment.service;

import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentVendorMaster;

public interface MpesaPaymentService {

	JsonObject payment(PaymentDetail payment, PaymentVendorMaster vendor);
	
	JsonObject orderStatus(PaymentDetail payment, PaymentVendorMaster vendor);

	JsonObject mtnPayment(PaymentDetail payment, PaymentVendorMaster vendor);

	JsonObject mtnOrderStatus(PaymentDetail payment, PaymentVendorMaster vendor);

}