package com.maan.eway.thread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.maan.eway.master.service.EmiTransactionDetailsService;
import com.maan.eway.renewal.req.EmiDataRequest;

public class EmiNotificationThreadExecutor implements Runnable {


	private final List<EmiDataRequest> list;
	private final EmiTransactionDetailsService service;
	public final static int THREAD_INSERT_SIZE =10; 
	
	public EmiNotificationThreadExecutor(List<EmiDataRequest> res, EmiTransactionDetailsService service) {
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
				        for (final EmiDataRequest res: list) {	
				        	service.sendSmsEmail(res);
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
