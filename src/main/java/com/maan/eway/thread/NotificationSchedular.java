package com.maan.eway.thread;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.renewal.req.RenewDataRequest;
import com.maan.eway.renewal.service.RenewalService;

@Component
public class NotificationSchedular {

	@Autowired
	private RenewalService service;
	
	public final static int THREAD_INSERT_SIZE =10; 

	public void schedule(){
		boolean threadStatus =!isProcessExist("RENEW_THREAD_Job");
		boolean xgenWsdlStatus= false;		

		if(threadStatus){						
			xgenWsdlStatus=true;
		}		
		System.out.println("Thread (RENEW_THREAD_Job) Status="+threadStatus);		
		//&& xgenWsdlStatus
		if(threadStatus && xgenWsdlStatus ){
			//Allocate the request
			allocate();
		}		 		
	}
	
	//Check existing thread
	private boolean isProcessExist(String processName){
		
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet(); 
		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]); 
		if(threadArray.length>0){
			for(Thread th : threadArray)  { 
				if(th.getName().contains(processName) && th.isAlive()){
					return true;}}}
		return false;
	}
	
	//Allocate the request
	public void allocate() {

		//----------------- Getting data from Renew_quote_policy and framing request-------------	
		List<RenewDataRequest> List =service.getNotificationRequestList();
		System.out.println(new Date() +"    Motor-Renewal (SMS/EMAIL) Request COUNT:  "+List.size());
		//----------------------- Insert Notification SMS Next Date next entry------------------	
		service.InsertNotificationSmsNext(List);	
		try{
			if(List!=null && List.size()>0){
				int splitValue =0;
				if(List.size()>10){
					splitValue =(int) Math.round(List.size()*(0.10));}
				else{
					splitValue =List.size();}
				
				for(int i=0;i<List.size();){
					 List<RenewDataRequest> res=null;
					res=List.subList(i,(i+splitValue) > List.size()?List.size():(i+splitValue));
					i=i+splitValue;
					//Run each request in THREAD
					NotificationThreadExecutor job=new NotificationThreadExecutor(res,service);
					Thread thread = new Thread(job); 
					thread.start();
					
				}
				
			}		
		}catch(Exception e){e.printStackTrace();}			
	}
	
}
