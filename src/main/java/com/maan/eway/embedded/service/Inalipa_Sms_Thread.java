package com.maan.eway.embedded.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Inalipa_Sms_Thread implements Runnable{
	
	Logger log = LogManager.getLogger(Inalipa_Sms_Thread.class);
	
	private String type;
	
	private String policyNo;
	
	private EmbeddedService embeddedService;
	
	
	public Inalipa_Sms_Thread(String type,String policyNo,EmbeddedService embeddedService){
		
		this.embeddedService = embeddedService;
		
		this.policyNo = policyNo;
		
		this.type = type;
		
		
	}

	@Override
	public void run() {
		
		if(type.equalsIgnoreCase("INALIPA-SMS")) {
			
			log.info("Inalipa send sms block start : "+policyNo);
			
			embeddedService.sendSms(policyNo);
			
			log.info("Inalipa send sms block end : "+policyNo);

		}
	}  

}
