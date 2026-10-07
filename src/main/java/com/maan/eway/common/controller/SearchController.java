package com.maan.eway.common.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.res.AccessoriesSumInsureDropDownRes;
import com.maan.eway.common.res.AdminViewQuoteCommonRes;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.BuildingSearchRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DocumentDetailsRes;
import com.maan.eway.common.res.PersonalAccidentRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchPaymentInfoRes;
import com.maan.eway.common.res.SearchPremiumDetailsRes;
import com.maan.eway.common.res.SearchROPDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.common.service.SearchService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
@Api(tags = "SEARCH DETAILS", description = "API's")
public class SearchController {

	@Autowired
	private PrintReqService reqPrinter;
	
	
	
	@Autowired
	private  SearchService entityService;
	
	
	
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/dropdown/adminsearch")
	public ResponseEntity<CommonRes> searchDropdown(@RequestBody CopyQuoteDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = entityService.searchDropdown(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}


	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminsearchdetails")
	public ResponseEntity<CommonRes> adminSearchOrderByEntryDate(@RequestBody SearchReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<SearchRes> res = entityService.adminSearchOrderByEntryDate(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminviewcustomerdetails")
	public ResponseEntity<CommonRes> adminCustomerSearch(@RequestBody SearchReq req) {
		CommonRes data = new CommonRes();
		List<SearchCustomerDetailsRes> res = entityService.adminCustomerSearch(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminviewriskdetails")
	@ApiOperation(value = "This method is Get Quote Details")
	public ResponseEntity<CommonRes> adminViewQuoteDetails(@RequestBody SearchReq req) {
		CommonRes commonRes = new CommonRes();
		reqPrinter.reqPrint(req);

		// Save
		AdminViewQuoteRes res = entityService.adminViewQuoteDetails(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminviewquoteriskdetails")
	@ApiOperation(value = "This method is Get Quote Details")
	public ResponseEntity<CommonRes> adminViewQuoteRiskDetails(@RequestBody SearchReq req) {
		CommonRes commonRes = new CommonRes();
		reqPrinter.reqPrint(req);

		// Save
		AdminViewQuoteCommonRes res = entityService.adminViewQuoteRiskDetails(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminviewratingdetails")
	@ApiOperation(value = "This method is Get Rating Details")
	public ResponseEntity<CommonRes> adminViewRatingDetails(@RequestBody SearchReq req) {
		CommonRes commonRes = new CommonRes();
		reqPrinter.reqPrint(req);

		// Save
		List<SearchEservieMotorDetailsViewRatingRes> res = entityService.adminViewRatingDetails(req);
		commonRes.setCommonResponse(res);
		commonRes.setIsError(false);
		commonRes.setErrorMessage(null);
		commonRes.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(commonRes, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminviewpremiumdetails")
	public ResponseEntity<CommonRes> adminPremiumSearch(@RequestBody SearchReq req) {
		CommonRes data = new CommonRes();
		SearchPremiumDetailsRes res = entityService.adminPremiumSearch(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	

@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/adminviewropdriverdetails")
public ResponseEntity<CommonRes> adminROPSearch(@RequestBody SearchReq req) {
	CommonRes data = new CommonRes();
	SearchROPDetailsRes res = entityService.adminROPDriverSearch(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if (res != null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}

@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/adminviewropvehicledetails")
public ResponseEntity<CommonRes> adminROPVehicleSearch(@RequestBody SearchReq req) { 	//vehicle info tab in viewquotedetails (motor,short term policy only)
	CommonRes data = new CommonRes();
	SearchROPVehicleDetailsRes res = entityService.adminROPVehicleSearch(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if (res != null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}
@PostMapping("/adminviewpaymentinfo")
public ResponseEntity<CommonRes> viewPaymentInfo(@RequestBody SearchReq req) {
	CommonRes data = new CommonRes();
	List<SearchPaymentInfoRes> paymentgetres = new ArrayList<SearchPaymentInfoRes>();
	paymentgetres = entityService.viewPaymentInfo(req);
	data.setCommonResponse(paymentgetres);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");

	if (paymentgetres != null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}

}
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/viewdocumentdetails")
@ApiOperation(value = "This method is Get Document Details")
public ResponseEntity<CommonRes> viewDocumentDetails(@RequestBody SearchReq req) {
	CommonRes data = new CommonRes();
	DocumentDetailsRes res = entityService.viewDocumentDetails(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if (res != null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}


@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/viewpersonalaccidentdetails")
public ResponseEntity<CommonRes> viewPersonalAccidentDetails(@RequestBody SearchReq req) {
	CommonRes data = new CommonRes();
	List<PersonalAccidentRes> res = entityService.viewPersonalAccidentDetails(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if (res != null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}


@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/adminbuildingsearchdetails")
public ResponseEntity<CommonRes> adminSearchBuildingDeatails(@RequestBody SearchReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	List<BuildingSearchRes> res = entityService.adminSearchBuildingDeatails(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if (res != null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}


@PostMapping("/accessoriessuminsured")
public ResponseEntity<CommonRes> getAccessoriesSuminsuredByQuoteNo(@RequestBody SearchReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	AccessoriesSumInsureDropDownRes res = entityService.getAccessoriesSuminsuredByQuoteNo(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	if (res != null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	} else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}


	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/viewquotedetails")
	public ResponseEntity<CommonRes> viewQuoteDetails(@RequestBody ViewQuoteDetailsReq req) { 	//quote info tab in viewquotedetails (all products)
		CommonRes data = new CommonRes();
		ViewQuoteDetailsRes res = entityService.viewQuoteDetails(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}
