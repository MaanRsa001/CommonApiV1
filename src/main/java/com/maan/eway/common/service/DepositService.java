package com.maan.eway.common.service;

import com.maan.eway.common.req.GetDepositPaymentReq;
import com.maan.eway.common.req.SaveDepositeMasterReq;
import com.maan.eway.common.req.SavePaymentDepositReq;
import com.maan.eway.common.req.SavePremiumDepositReq;
import com.maan.eway.common.req.SavedepositDetailReq;
import com.maan.eway.common.res.CommonRes;

public interface DepositService {

	CommonRes saveDepositeMaster(SaveDepositeMasterReq req);

	CommonRes savePremiumDeposit(SavePremiumDepositReq req);

	CommonRes savePaymentDeposit(SavePaymentDepositReq req);

	CommonRes CbcbyBrokerId(String loginId);

	CommonRes DepositMasterById(String cbcNo);

	CommonRes GetDepositDetail();

	CommonRes GetDepositDetailById(String cbcNo);

	CommonRes GetDepositPayment(GetDepositPaymentReq req);

	CommonRes savedepositDetail(SavedepositDetailReq req);

}
