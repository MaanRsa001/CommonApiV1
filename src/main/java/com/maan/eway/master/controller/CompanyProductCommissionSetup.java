//package com.maan.eway.master.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.maan.eway.master.service.CompanyProductCommissionService;
//import com.maan.eway.service.PrintReqService;
//
//import io.swagger.annotations.Api;
//
//@RestController
//@Api(tags="MASTER : Company Broker Commission Details Master", description="API's")
//@RequestMapping("/master")
//public class CompanyProductCommissionSetup {
//	
//
//	@Autowired
//	private  CompanyProductCommissionService entityService;
//	
//	@Autowired
//	private PrintReqService reqPrinter;
////
////	//Save
////	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
////	@PostMapping("/insertbrokercommission")
////	@ApiOperation(value = "This Method is to save Broker Commission")
////	public ResponseEntity<CommonRes> saveBrokerCommission(@RequestBody BrokerCommissionDetailsMasterSaveReq req) {
////		CommonRes data = new CommonRes();
////		reqPrinter.reqPrint(req);
////
////		List<Error> validation = entityService.validateBrokerCommission(req);
////		//validation
////		if (validation != null && validation.size() != 0) {
////			data.setCommonResponse(null);
////			data.setIsError(true);
////			data.setErrorMessage(validation);
////			data.setMessage("Failed");
////			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
////		} else {
////			// save
////			SuccessRes res = entityService.saveBrokerCommission(req);
////			data.setCommonResponse(res);
////			data.setIsError(false);
////			data.setErrorMessage(Collections.emptyList());
////			data.setMessage("Success");
////
////			if (res != null) {
////				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
////			} else {
////				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
////			}
////		}
////	}
////	
//	
//}
