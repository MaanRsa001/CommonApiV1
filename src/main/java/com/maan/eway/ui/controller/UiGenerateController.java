package com.maan.eway.ui.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.notification.req.Notification;
import com.maan.eway.ui.request.GroupSetup;
import com.maan.eway.ui.request.Ui;
import com.maan.eway.ui.service.UIService;

import io.swagger.annotations.Api;
@RestController
@Api(tags = "UI Generation", description = "API's")
@RequestMapping("/ui")
public class UiGenerateController {
	@PostMapping("/generate/")
	public ResponseEntity<String> pushNotification(@RequestBody Notification request) {
		ClassPathResource staticDataResource = new ClassPathResource("/sampleJson.json");
        String staticDataString;
		try {
			staticDataString = IOUtils.toString(staticDataResource.getInputStream(), StandardCharsets.UTF_8);
			  return new ResponseEntity<>(
		        		staticDataString,
		            HttpStatus.OK
		        );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

      return null;
		
		
	}
	
	@Autowired
	private UIService uiservice;
	
	@PostMapping("/buildui/")
	public ResponseEntity<GroupSetup> createFormly(@RequestBody Ui request ){
		GroupSetup g=uiservice.buildUIFromTable(request);
		  return new ResponseEntity<>(g,  HttpStatus.OK );
	}
}
