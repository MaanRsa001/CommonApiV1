package com.maan.eway.thread;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.renewal.req.PullrenewalReq;
import com.maan.eway.renewal.req.RenewDataRequest;
import com.maan.eway.renewal.service.RenewalService;

@Component
public class RenewSchedular {

	@Autowired
	private RenewalService service;

	private Logger log = LogManager.getLogger(RenewSchedular.class);

	public final static int THREAD_INSERT_SIZE = 10;

	public void schedule() {
		boolean threadStatus = !isProcessExist("RENEW_THREAD_Job");
		boolean xgenWsdlStatus = false;

		if (threadStatus) {
			xgenWsdlStatus = true;
		}
		System.out.println("Thread (RENEW_THREAD_Job) Status=" + threadStatus);
		// && xgenWsdlStatus
		if (threadStatus && xgenWsdlStatus) {
			// Allocate the request
			allocate();
		}
	}

	// Check existing thread
	private boolean isProcessExist(String processName) {

		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
		if (threadArray.length > 0) {
			for (Thread th : threadArray) {
				if (th.getName().contains(processName) && th.isAlive()) {
					return true;
				}
			}
		}
		return false;
	}

	// Allocate the request
	public void allocate() {

		// ----------------- Insert data from view to motor_renewal_detail-------------
		System.out.println("***allocate insertMotRenDetStart****");
		//for(int j=35;j>=0;j--) {
			
			
		PullrenewalReq req=new PullrenewalReq();
		req.setInsuranceId("100002");
		req.setDays(30);
		CommonRes resp=service.pullrenewal(req);
		String tranId=resp.getCommonResponse()==null?"":resp.getCommonResponse().toString();
		System.out.println("***allocate insertMotRenDetFromView End****");
		// ----------------- Getting data from motor_renewal_detail and framing request-------------
		List<RenewDataRequest> List = service.getRenewPolicyList(tranId);
		System.out.println(new Date() + "    Motor-Renewal Request COUNT:  " + List.size());
		// ----------------- Insert data to ttrn_renew_request and Notification_master table (tran_id)-------------
		//String tranId = service.saveTransactionTable(List);
		try {
			if (List != null && List.size() > 0) {
				int splitValue = 0;
				if (List.size() > 10) {
					splitValue = (int) Math.round(List.size() * (0.10));
				} else {
					splitValue = List.size();
				}

				for (int i = 0; i < List.size();) {
					List<RenewDataRequest> res = null;
					res = List.subList(i, (i + splitValue) > List.size() ? List.size() : (i + splitValue));
					i = i + splitValue;
					// Run each request in THREAD
					RenewThreadExecuter job = new RenewThreadExecuter(res, service);
					Thread thread = new Thread(job);
					thread.start();

				}
				
			}
		} catch (Exception e) {
			log.error(e);
		}
		//}
	}

	public void startPremiaRenewSchedular() {
		boolean threadStatus = !isProcessExist("PREMIA_RENEW_THREAD_Job");
		boolean xgenWsdlStatus = false;

		if (threadStatus) {
			xgenWsdlStatus = true;
		}
		System.out.println("Thread (PREMIA_RENEW_THREAD_Job) Status=" + threadStatus);
		// && xgenWsdlStatus
		if (threadStatus && xgenWsdlStatus) {
			// Allocate the request
			Premiaallocate();
		}
	}

	private void Premiaallocate() {
		// ----------------- Insert data from view to motor_renewal_detail-------------
				System.out.println("***allocate insertMotRenDetFromView Start****");
				service.insertMotRenDetFromView();
				System.out.println("***allocate insertMotRenDetFromView End****");
				// ----------------- Getting data from motor_renewal_detail and framing request-------------
				List<RenewDataRequest> List = service.getPolicyRequestList();
				System.out.println(new Date() + "    Motor-Renewal Request COUNT:  " + List.size());
				// ----------------- Insert data to ttrn_renew_request and Notification_master table (tran_id)-------------
				///String tranId = service.saveTransactionTable(List);
				try {
					if (List != null && List.size() > 0) {
						int splitValue = 0;
						if (List.size() > 10) {
							splitValue = (int) Math.round(List.size() * (0.10));
						} else {
							splitValue = List.size();
						}

						for (int i = 0; i < List.size();) {
							List<RenewDataRequest> res = null;
							res = List.subList(i, (i + splitValue) > List.size() ? List.size() : (i + splitValue));
							i = i + splitValue;
							// Run each request in THREAD
							RenewThreadExecuter job = new RenewThreadExecuter(res, service);
							Thread thread = new Thread(job);
							thread.start();

						}
						
					}
				} catch (Exception e) {
					log.error(e);
				}
	}

}
