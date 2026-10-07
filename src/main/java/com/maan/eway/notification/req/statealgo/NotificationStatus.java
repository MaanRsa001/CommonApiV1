package com.maan.eway.notification.req.statealgo;

public enum NotificationStatus {
		PENDING("P"),PUSHED("Y"),COMPLETED("C");
	
		private String value;  
		
		private NotificationStatus(String value){  
			this.value=value;  
		}
		
		public NotificationStatus changeNext() {
			switch (this) {
				case  PENDING:  return PUSHED;
				case PUSHED: return COMPLETED;
				default : return PENDING;
			}
		}
		
		@Override
	    public String toString() {
	        return value;
	    }
}
