package com.maan.eway.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.service.ValuationService;

public class ValuationThreadExecutor implements Runnable {


	private final List<ValuationStatusReq> list;
	private final ValuationService service;
	public final static int THREAD_INSERT_SIZE =10; 
	
	public ValuationThreadExecutor(List<ValuationStatusReq> res, ValuationService service) {
		this.list=res;
		this.service=service;
	}

	public void run() {
		ExecutorService executor=null;
		try{
			
			executor = Executors.newFixedThreadPool(THREAD_INSERT_SIZE);
			if(list!=null && list.size()>0){
				executor.execute(new Runnable(){
					public void run() {
						final String orgName = Thread.currentThread().getName();
				        Thread.currentThread().setName("Najm_Job "+orgName);
				        for (final ValuationStatusReq res: list) {	
				        	service.getStatus(res);
						}}});
				}
		}catch(Exception e){e.printStackTrace();	
		}finally{
			if(executor!=null){
				executor.shutdown();
				try {
					executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
				} catch (InterruptedException e) {e.printStackTrace();}
			}}
	}

}
