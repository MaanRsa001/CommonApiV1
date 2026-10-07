package com.maan.eway.thread;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.service.ValuationService;

@Component
public class ValuationSchedular {

	@Autowired
	private ValuationService service;
	
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
	

	public void Valuationschedule() {
		boolean threadStatus =!isProcessExist("EMI_THREAD_Job");
		boolean xgenWsdlStatus= false;		

		if(threadStatus){						
			xgenWsdlStatus=true;
		}		
		System.out.println("Thread (EMI_THREAD_Job) Status="+threadStatus);		
		//&& xgenWsdlStatus
		if(threadStatus && xgenWsdlStatus ){
			//Allocate the request
			Valuationallocate();
		}		 		
	}

	private void Valuationallocate() {

		List<ValuationStatusReq> List =service.getValuationStatusPendingList();
		System.out.println(new Date() +"    Valuation Request COUNT:  "+List.size());
		try{
			if(List!=null && List.size()>0){
				int splitValue =0;
				if(List.size()>10){
					splitValue =(int) Math.round(List.size()*(0.10));}
				else{
					splitValue =List.size();}
				
				for(int i=0;i<List.size();){
					 List<ValuationStatusReq> res=null;
					res=List.subList(i,(i+splitValue) > List.size()?List.size():(i+splitValue));
					i=i+splitValue;
					//Run each request in THREAD
					ValuationThreadExecutor job=new ValuationThreadExecutor(res,service);
					Thread thread = new Thread(job); 
					thread.start();
					
				}
				
			}		
		}catch(Exception e){e.printStackTrace();}		
	}


	
}
