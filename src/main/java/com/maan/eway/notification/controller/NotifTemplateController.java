package com.maan.eway.notification.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.req.DirectMailSentReq;
import com.maan.eway.notification.req.DirectSmsSentReq;
import com.maan.eway.notification.req.NotifGetByIdReq;
import com.maan.eway.notification.req.NotifGetByQuoteNoReq;
import com.maan.eway.notification.req.NotifGetReq;
import com.maan.eway.notification.req.NotifTemplateGetReq;
import com.maan.eway.notification.req.TemplatesDropDownReq;
import com.maan.eway.notification.res.MailNotifGetRes;
import com.maan.eway.notification.res.NofiByQuoteNoRes;
import com.maan.eway.notification.res.SmsNofiGetRes;
import com.maan.eway.notification.service.NotifTemplateService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Mail & Sms  : Direct Sent ", description = "API's")
@RequestMapping("/notification")
public class NotifTemplateController {

	@Autowired
	private  NotifTemplateService notifTempService;
	
	@Autowired
	private  PrintReqService reqPrinter;
	
	// Client Type
		@PostMapping("/dropdown/templateslist")
		@ApiOperation(value = "This method is to Mail Templates Drop Down")

		public ResponseEntity<CommonRes> getTemplatesDropDown(@RequestBody  TemplatesDropDownReq req ) {

			CommonRes data = new CommonRes();

			// Save
			List<DropDownRes> res = notifTempService.getTemplatesDropDown(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}
		
		
		
		
		@PostMapping("/getmailtemplate")
		@ApiOperation(value = "This method is to Get Framed Mail Template Details")
		public ResponseEntity<CommonRes> getMailTemplate(@RequestBody NotifTemplateGetReq req) {

			reqPrinter.reqPrint("Printer Request --->" + req);
			CommonRes data = new CommonRes();

			// Save
			CommonRes res = notifTempService.getMailTemplate(req);
			if (res.getIsError() == false ) {
				data.setCommonResponse(res.getCommonResponse() );
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(res.getErrorMessage() );
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
		}
		
		@PostMapping("/getsmstemplate")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> getSmsTemplate(@RequestBody NotifTemplateGetReq req) {

			reqPrinter.reqPrint("Printer Request --->" + req);
			CommonRes data = new CommonRes();

			// Save
			CommonRes res = notifTempService.getSmsTemplate(req);
			if (res.getIsError() == false ) {
				data.setCommonResponse(res.getCommonResponse() );
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(res.getErrorMessage() );
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
		}
		
		
		@PostMapping("/sentdirectmail")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> sentDirectMail(@RequestBody DirectMailSentReq req) {

			reqPrinter.reqPrint("Printer Request --->" + req);
			CommonRes data = new CommonRes();

			// Save
			CommonRes res = notifTempService.sentDirectMail(req);
			if (res.getIsError() == false ) {
				data.setCommonResponse(res.getCommonResponse() );
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(res.getErrorMessage() );
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
		}
		
		@PostMapping("/sentdirectsms")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> sentDirectSms(@RequestBody DirectSmsSentReq req) {

			reqPrinter.reqPrint("Printer Request --->" + req);
			CommonRes data = new CommonRes();

			// Save
			CommonRes res = notifTempService.sentDirectSms(req);
			if (res.getIsError() == false ) {
				data.setCommonResponse(res.getCommonResponse() );
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				
			} else {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(res.getErrorMessage() );
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
		}
		
		@PostMapping("/getallsentmail")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> getSentMailList(@RequestBody NotifGetReq req) {

			CommonRes data = new CommonRes();

			// Save
			List<MailNotifGetRes> res = notifTempService.getSentMailList(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}
		
		@PostMapping("/viewsentmail")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> viewSentMail(@RequestBody NotifGetByIdReq req) {

			CommonRes data = new CommonRes();

			// Save
			MailNotifGetRes res = notifTempService.viewSentMail(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}
		
		@PostMapping("/getallsentsms")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> getSmsSentList(@RequestBody NotifGetReq req) {

			CommonRes data = new CommonRes();

			// Save
			List<SmsNofiGetRes> res = notifTempService.getSmsSentList(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}
		
		
		
		@PostMapping("/viewsentsms")
		@ApiOperation(value = "This method is to Get Framed SMS Template Details")
		public ResponseEntity<CommonRes> viewSmsSent(@RequestBody NotifGetByIdReq req) {

			CommonRes data = new CommonRes();

			// Save
			SmsNofiGetRes res = notifTempService.viewSmsSent(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}
		@PostMapping("/viewnoofnotification")
		@ApiOperation(value = "This method is to Get No Of Notification Send By Quote No")
		public ResponseEntity<CommonRes> viewNotificationSentToQuoteNo(@RequestBody NotifGetByQuoteNoReq req) {

			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
			List<Error> validation = notifTempService.validateQuotoNo(req);
			// validation
			if (validation != null && validation.size() != 0) {
				data.setCommonResponse(null);
				data.setIsError(true);
				data.setErrorMessage(validation);
				data.setMessage("Failed");
				return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

			} else {

				// Save
				List<NofiByQuoteNoRes> res = notifTempService.viewNotificationSentToQuoteNo(req);
				data.setCommonResponse(res);
				data.setIsError(false);
				data.setErrorMessage(Collections.emptyList());
				data.setMessage("Success");
				if (res != null) {
					return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
				} else {
					return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
				}
			}
		}
		@PostMapping("/dropdown/activetemplist")
		@ApiOperation(value = "This method is to Mail Templates Drop Down")

		public ResponseEntity<CommonRes> getActiveTemplatesDropDown(@RequestBody  TemplatesDropDownReq req ) {

			CommonRes data = new CommonRes();

			// Save
			List<DropDownRes> res = notifTempService.getActiveTemplatesDropDown(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}
	
}
