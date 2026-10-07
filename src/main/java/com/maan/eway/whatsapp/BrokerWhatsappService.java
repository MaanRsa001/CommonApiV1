package com.maan.eway.whatsapp;

import com.maan.eway.common.res.CommonRes;
import java.util.List;

public interface BrokerWhatsappService {

	CommonRes brokerCheckWhatsapp(BrokerCheckReq req);

	CommonRes saveBrokerWhatsapp(BrokerCheckReq req);

	CommonRes editBrokerWhatsapp(BrokerCheckReq req);

	CommonRes gritBrokerWhatsapp(BrokerCheckReq req);

	CommonRes UpdateEmployee(UpdateWhatsappEmployeeReq req);

}
