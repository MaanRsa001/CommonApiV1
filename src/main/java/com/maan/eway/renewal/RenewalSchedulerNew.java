package com.maan.eway.renewal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.PullrenewalReq;
import com.maan.eway.renewal.service.RenewalService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RenewalSchedulerNew {

	@Autowired
	private RenewalService renewalservice;

	@Scheduled(cron = "0 0 1 * * ?")
	public void scheduledPullRenewal() {
		try {
			PullrenewalReq req = new PullrenewalReq();
			req.setDays(30);
			CommonRes res = renewalservice.pullrenewal(req);
			log.info("Renewal pull result: " + (res != null ? res.getMessage() : "null response"));
		} catch (Exception e) {
			log.error("Scheduled renewal pull failed", e);
		}
	}
}