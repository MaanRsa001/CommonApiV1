package com.maan.eway.endorsment.service;

import java.util.List;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.error.Error;

public interface SingleApiForCancellationService {

	CommonRes singleCancelPolicy(Endorsment request, String string);

	CommonRes waCancellationALL(Endorsment request, String string);

	List<Error> validateEndtDetails(Endorsment request);

}
