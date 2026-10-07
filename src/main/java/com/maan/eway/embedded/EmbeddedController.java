package com.maan.eway.embedded;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.embedded.request.ClaimDetailsReq;
import com.maan.eway.embedded.request.Inalipa;
import com.maan.eway.embedded.response.InalipaDetailsRes;
import com.maan.eway.embedded.response.ResponseForInalipa;
import com.maan.eway.embedded.service.EmbeddedService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/embedded")
public class EmbeddedController {
	
	
	@Autowired
	private EmbeddedService embService;
	
	@PostMapping("/create/{LoginId}/policy")
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<ResponseForInalipa>  createPolicy(@PathVariable("LoginId")  String loginId, @RequestBody Inalipa request) {
		ResponseForInalipa response=embService.createPolicy(loginId,request);
		if(response!=null)
			return new ResponseEntity<>(response,HttpStatus.OK);
		else
			return new ResponseEntity<>(null,HttpStatus.EXPECTATION_FAILED);	
		
	}
	
	@GetMapping("/create/schedule/{LoginId}/{EncodedPolicyNo}")
	@ApiOperation("This Method is to get by id")
	public ResponseEntity<Resource> download(@PathVariable("LoginId") String loginId,@PathVariable("EncodedPolicyNo") String encodedPolicyNo) throws IOException {
      
		byte [] byteArray =Base64.getDecoder().decode(encodedPolicyNo);
		
		String policyNo =new String(byteArray);
		
		String pdfFilepath= embService.createSchedule(loginId, policyNo);
		
		HttpHeaders header = new HttpHeaders();
	    header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+policyNo+".pdf");
	    header.add("Cache-Control", "no-cache, no-store, must-revalidate");
	    header.add("Pragma", "no-cache");
	    header.add("Expires", "0");
		
	    File file = new File(pdfFilepath);
	    
	    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

	    return ResponseEntity.ok()
	            .headers(header)
	            .contentLength(file.length())
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}
	
	@PostMapping("getClaimDetails")
	public InalipaDetailsRes getClaimDetails(@RequestBody ClaimDetailsReq req){
		return embService.getClaimDetails(req);
	}
	
	@GetMapping("/create/policy/schedule/{quoteNo}")
	public ResponseEntity<Resource> download(@PathVariable("quoteNo") String quoteNo) throws IOException {
      
		byte [] byteArray =Base64.getDecoder().decode(quoteNo);
		
		String quote =new String(byteArray);
		
		String pdfFilepath= embService.getPolicySchedule(quote);
		
		HttpHeaders header = new HttpHeaders();
	    header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=PolicyDocument.pdf");
	    header.add("Cache-Control", "no-cache, no-store, must-revalidate");
	    header.add("Pragma", "no-cache");
	    header.add("Expires", "0");
		
	    File file = new File(pdfFilepath);
	    
	    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

	    return ResponseEntity.ok()
	            .headers(header)
	            .contentLength(file.length())
	            .contentType(MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}
	
	
	@GetMapping("/create/send/sms/{policyNo}")
	public CommonRes sendSms(@PathVariable("policyNo") String policyNo) {
		return embService.sendSms(policyNo);
	}
	
}
