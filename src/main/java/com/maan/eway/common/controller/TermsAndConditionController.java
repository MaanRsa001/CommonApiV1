package com.maan.eway.common.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.AutoInsertTermsReq;
import com.maan.eway.common.req.AutoInsertTermsReq;
import com.maan.eway.common.req.TermsAndConditionGetBySubIdReq;
import com.maan.eway.common.req.TermsAndConditionGetReq;
import com.maan.eway.common.req.TermsAndConditionInsertReq;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.TermsAndConditionGetBySubIdRes;
import com.maan.eway.common.res.TermsAndConditionGetRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.common.service.TermsAndConditionService;
import com.maan.eway.error.Error;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Terms And Condition", description="API's")
@RequestMapping("/api")
public class TermsAndConditionController {

@Autowired
private TermsAndConditionService service;

@Autowired
private PrintReqService reqPrinter;


//  View Terms And Condition
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/viewtermsandcondition")
	@ApiOperation("This method is View Terms And Condition")
	public ResponseEntity<CommonRes> viewTermsAndCondition(@RequestBody TermsAndConditionReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		TermsAndConditionRes res = service.viewTermsAndCondition(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}


	@RequestMapping("/viewtermsbasedonsection")
	@ApiOperation(value = "This method for view the terms and conditions based on sec and cover ids")
	public ResponseEntity<CommonRes> fetchTermsAndCondition(@RequestBody TermsAndConditionReq req) {

		return service.fetchTermsAndCondition(req);
	}
	
	@RequestMapping("/sectionlistbasedonriskid")
	@ApiOperation(value = "Fetch section Based On Risk Ids" , notes = "Section drop down to Terms and conditions" )
	public ResponseEntity<CommonRes> fetchSectionsBasedOnRisk(@RequestParam(value = "requestReferenceNo")  String requestReferenceNo ,
			@RequestParam(value = "riskId" , required = true ) Integer  riskId ){
		
		
		return service.fetchSectionsBasedOnRisk(requestReferenceNo , riskId);
		
		
	}
	
	
	
	// Insert Terms And Condition
	

	@PostMapping("/inserttermsandcondition")
	@ApiOperation("This method is Insert Terms And Condition")
	public ResponseEntity<CommonRes> insertTermsAndCondition(@RequestBody TermsAndConditionInsertReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		

		List<Error> validation = null;
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
		SuccessRes res = service.insertTermsAndConditionWithLocation(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}
	
	}
	
	

//  Get Terms And Condition
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/gettermsandcondition")
	@ApiOperation("This method is Get Terms And Condition")
	public ResponseEntity<CommonRes> getTermsAndCondition(@RequestBody TermsAndConditionGetReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		TermsAndConditionGetRes res = service.getTermsAndCondition(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}


//  Get Terms And Condition By Subid
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/gettermsandconditionbysubid")
	@ApiOperation("This method is Get Terms And Condition by Sub Id")
	public ResponseEntity<CommonRes> getTermsAndConditionSubId(@RequestBody TermsAndConditionGetBySubIdReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		TermsAndConditionGetBySubIdRes res = service.getTermsAndConditionSubId(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PostMapping("/getMastersData")
	public ResponseEntity<CommonRes> getMastersData(@RequestBody TermsAndConditionReq req)
	{
		
		
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		TermsAndConditionRes res = service.getMasterData(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!= null) {
			return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
		}
		
		
	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/autoinsert")
    public ResponseEntity<CommonRes> autoInsertTerms(
            @RequestBody AutoInsertTermsReq req) {

        CommonRes res = new CommonRes();

        try {

            reqPrinter.reqPrint(req);

            List<Error> validationErrors = service.validateTermsAndConditionAutoInsert(req);

            if (validationErrors != null && !validationErrors.isEmpty()) {
                res.setIsError(true);
                res.setErrorMessage(validationErrors);
                res.setMessage("Validation Failed");
                return ResponseEntity.badRequest().body(res);
            }

            
            service.autoInsertDefaultTermsAfterBuyPolicy(req); 

            res.setIsError(false);
            res.setMessage("Terms & Conditions Inserted Successfully");
            res.setCommonResponse("SUCCESS");

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            res.setIsError(true);
            res.setMessage("Something went wrong");
            res.setErrorMessage(Collections.singletonList(
                    new Error("500", "Exception", e.getMessage())
            ));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }
	
}
