package com.maan.eway.common.service.impl;

import com.maan.eway.common.req.NewQuoteReq;

public class NotificatinThread implements Runnable{

	private NotificationThreadServiceImpl notificatinThread;
	private NewQuoteReq request;
	private String type;
	
	public NotificatinThread(NotificationThreadServiceImpl notificatinThread, NewQuoteReq request, String type) {
		this.notificatinThread=notificatinThread;
		this.request=request;
		this.type=type;
	}

	@Override
	public void run() {
		
		if(type.equalsIgnoreCase("REFERRAL")) {
			notificatinThread.getUpdateReferral(request);
		} 
	}

}
