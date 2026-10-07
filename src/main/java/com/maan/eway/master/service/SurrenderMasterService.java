package com.maan.eway.master.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetallSurrenderDetailsReq;
import com.maan.eway.master.req.GetoneSurrenderDetailsReq;
import com.maan.eway.master.req.InsertSurrenderReq;
import com.maan.eway.master.res.GetoneSurrenderDetailsRes;
import com.maan.eway.res.SuccessRes;

public interface SurrenderMasterService {

	List<Error> validateSurrender(InsertSurrenderReq req);

	SuccessRes insertSurrender(InsertSurrenderReq req);

	InsertSurrenderReq getallSurrenderDetails(GetallSurrenderDetailsReq req);

	GetoneSurrenderDetailsRes getoneSurrenderDetails(GetoneSurrenderDetailsReq req);

}
