package com.maan.eway.notification.controller;

import java.util.Map;

//import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.notification.req.MailDataDetailsDto;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.service.NotificationService;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "NOTIFIACTION : Notifiaction ", description = "API's")
@RequestMapping("/post/notification")
public class NotificationController {

	
	@Autowired
	private NotificationService notificationservice;
	
	@PostMapping("/pushnotification")
	public ResponseEntity<CommonRes> pushNotification(@RequestBody Notification request) {
	 	CommonRes data = notificationservice.pushNotification(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/tinyurl")
	public ResponseEntity<Map<String,String>> createTinyUrl(){
		Map<String, String> data = notificationservice.createTinyUrl();
		return new ResponseEntity<>(data, HttpStatus.CREATED);
	}
	
	
	/*@Autowired
	private JobScheduler jobScheduler;
	
	@Autowired
	private JobRunrService ourservice;
	
	@PostMapping("/startsched")
	public void x() { 
	 //jobs.schedule(Instant.now().plusSeconds(60), 
		//	 ourservice -> o);
			
	 //jobs.schedule<JobRunrService>(Instant.now().plusSeconds(60),()-> x.jobProcess());
	 
	 jobScheduler.schedule(Instant.now(), () ->ourservice.jobProcess());
  
 }
*/
	
	@PostMapping("/ack/mail")
	public ResponseEntity<CommonRes> pushMailStatus(@RequestBody MailDataDetailsDto request) {
	 	CommonRes data = notificationservice.pushMailStatus(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}	
	
}
