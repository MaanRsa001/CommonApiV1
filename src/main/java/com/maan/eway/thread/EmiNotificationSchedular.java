package com.maan.eway.thread;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.master.service.EmiTransactionDetailsService;
import com.maan.eway.renewal.req.EmiDataRequest;

@Component
public class EmiNotificationSchedular {

	@Autowired
	private EmiTransactionDetailsService service;
	
	public final static int THREAD_INSERT_SIZE =10; 

	
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
	

	public void Emischedule() {
		boolean threadStatus =!isProcessExist("EMI_THREAD_Job");
		boolean xgenWsdlStatus= false;		

		if(threadStatus){						
			xgenWsdlStatus=true;
		}		
		System.out.println("Thread (EMI_THREAD_Job) Status="+threadStatus);		
		//&& xgenWsdlStatus
		if(threadStatus && xgenWsdlStatus ){
			//Allocate the request
			Emiallocate();
		}		 		
	}

	private void Emiallocate() {

		//----------------- Getting data from Renew_quote_policy and framing request-------------	
		List<EmiDataRequest> List =service.getEmiNotificationRequestList();
		System.out.println(new Date() +"    Emi (SMS/EMAIL) Request COUNT:  "+List.size());
		//----------------------- Insert Notification SMS Next Date next entry------------------	
		//service.InsertNotificationSmsNext(List);	
		try{
			if(List!=null && List.size()>0){
				int splitValue =0;
				if(List.size()>10){
					splitValue =(int) Math.round(List.size()*(0.10));}
				else{
					splitValue =List.size();}
				
				for(int i=0;i<List.size();){
					 List<EmiDataRequest> res=null;
					res=List.subList(i,(i+splitValue) > List.size()?List.size():(i+splitValue));
					i=i+splitValue;
					//Run each request in THREAD
					EmiNotificationThreadExecutor job=new EmiNotificationThreadExecutor(res,service);
					Thread thread = new Thread(job); 
					thread.start();
					
				}
				
			}		
		}catch(Exception e){e.printStackTrace();}		
	}
	
}
