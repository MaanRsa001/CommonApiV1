package com.maan.eway.notification.service;

import com.maan.eway.notification.bean.NotifTransactionDetails;

public interface NotificationState {
	
	public void setNotifTransactionDetails(NotifTransactionDetails d);
	public void doAction();

}
