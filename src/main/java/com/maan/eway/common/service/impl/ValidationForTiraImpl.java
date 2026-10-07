package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.req.TiraFrameReqCall;
import com.maan.eway.common.service.ValidationForTira;
import com.maan.eway.repository.HomePositionMasterRepository;

@Service
public class ValidationForTiraImpl implements ValidationForTira {

	@Autowired
	private HomePositionMasterRepository homerepo;

	private Logger log = LogManager.getLogger(ValidationForTiraImpl.class);

	@Override
	public List<String> validationForTira(TiraFrameReqCall req) {
		List<String> error = new ArrayList<String>();
		try {
			HomePositionMaster home = homerepo.findByQuoteNo(req.getQuoteNo());
			req.setCompanyid(home.getCompanyId());
			if (!"ACCEPTED".equalsIgnoreCase(home.getPaymentStatus())) {
				error.add("10001");
			} else if (StringUtils.isBlank(home.getPolicyNo())) {
				error.add("10002");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> validationForTira " + e.getMessage());
			return null;
		}
		return error;
	}

}
