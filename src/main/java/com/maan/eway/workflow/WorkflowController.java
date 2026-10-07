package com.maan.eway.workflow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.workflow.dto.WorkEngine;
import com.maan.eway.workflow.service.JsonMapperFromDB;

import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/workflow")
public class WorkflowController {

	@Autowired
	private JsonMapperFromDB jsonMapper;
	
	
	@PostMapping("/create/request")
	@ApiOperation(value = "This method is Create Json")
	public ResponseEntity<Map<String,Object>> createRequest(@RequestBody WorkEngine engine) {
		Map<String,Object> data=jsonMapper.createRequest(engine);
		if (data != null) {
			return new ResponseEntity<Map<String,Object>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/create/dynamicquery")
	@ApiOperation(value = "This method is Create Json")
	public ResponseEntity<Map<String, List<Map<String, Object>>>> dynamicQuery() {
		List<BigDecimal> list=new ArrayList<BigDecimal>() ;
		list.add(new BigDecimal("1001"));
		Map<String, List<Map<String, Object>>> data=jsonMapper.dynamicQuery(list,null,null);
		if (data != null) {
			return new ResponseEntity<Map<String, List<Map<String, Object>>>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/createquotation")
	@ApiOperation(value = "This method is Create Json")
	public ResponseEntity<List<Map<String,Object>>> createquotation(@RequestBody WorkEngine engine) {
		
		List<Map<String,Object>> data=jsonMapper.createQuotation(engine); 
		if (data != null) {
			return new ResponseEntity<List<Map<String,Object>>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
}
