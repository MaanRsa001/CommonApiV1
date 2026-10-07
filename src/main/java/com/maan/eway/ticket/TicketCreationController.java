package com.maan.eway.ticket;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.service.PrintReqService;
import com.maan.eway.thread.TicketCreateService;

@RestController
@RequestMapping("/api")
public class TicketCreationController {

	@Autowired
	private  PrintReqService reqPrinter;
	
	@Autowired
	private TicketCreateService ticketCreateService;
	
	@PostMapping("/create/ticket")
	public ResponseEntity<CommonRes> createTicket(@RequestBody TicketCreateReq req){
		reqPrinter.reqPrint("Printer Request --->" + req);
		CommonRes data = new CommonRes();

		
		CommonRes res = ticketCreateService.createTicket(req);
		if (res.getIsError() == false ) {
			data.setCommonResponse(res.getCommonResponse() );
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			
		} else {
			data.setCommonResponse(res.getCommonResponse());
			data.setIsError(true);
			data.setErrorMessage(res.getErrorMessage() );
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
	}
	
	@PostMapping("/ticket/dropoff")
	public ResponseEntity<CommonRes> dropoffTicket(@RequestBody TicketCreateReq req){
		reqPrinter.reqPrint("Printer Request --->" + req);
		CommonRes data = new CommonRes();
		
		CommonRes res = ticketCreateService.createDropOffTicket(req);
		if (res.getIsError() == false ) {
			data.setCommonResponse(res.getCommonResponse() );
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			
		} else {
			data.setCommonResponse(res.getCommonResponse());
			data.setIsError(true);
			data.setErrorMessage(res.getErrorMessage() );
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
	}
	
	@PostMapping("/customer/tracker")
	public ResponseEntity<CommonRes> customerTracker(@RequestBody CustomerTrackerReq req){
		reqPrinter.reqPrint("Printer Request --->" + req);
		CommonRes data = new CommonRes();
		
		CommonRes res = ticketCreateService.customerJobTracking(req);
		if (res.getIsError() == false ) {
			data.setCommonResponse(res.getCommonResponse() );
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			
		} else {
			data.setCommonResponse(res.getCommonResponse());
			data.setIsError(true);
			data.setErrorMessage(res.getErrorMessage() );
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
	}
}
