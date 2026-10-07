package com.maan.eway.notification.req;

import java.io.Serializable;

import lombok.Builder;

@Builder
public class Messenger implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
			MessengerTo
		 	MessengerSubject
			MessengerBody
			MessengerRegards
			*/
	
	private String messengerTo;
	private String messengerSubject;
	private String messengerBody;
	private String messengerRegards;
	
}
