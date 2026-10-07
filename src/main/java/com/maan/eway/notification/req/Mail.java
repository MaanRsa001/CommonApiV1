package com.maan.eway.notification.req;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class Mail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*	
	private String mailTo;
	private String mailSubject;
	private String mailBody;
	private String mailRegards;
	private List<String> mailcc;
	private JobCredentials credential;
	private String attachments;
	  private Long    notifNo ; 
	*/ 
	 	private List<String> tomails;
	private List<String> ccmails;
	private List<String> files;
	private String subject;
	private String mailbody;
	private String mailbodyContenttype;
	  private Long    notifNo ; 
	private JobCredentials master;
}
