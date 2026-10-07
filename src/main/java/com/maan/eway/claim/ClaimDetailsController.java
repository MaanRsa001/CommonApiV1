package com.maan.eway.claim;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.claimintimation.dto.ClaimCoInsurancedetailsData;
import com.maan.eway.claimintimation.dto.ClaimRequest;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.service.PrintReqService;

@RestController
@RequestMapping("/claim")
public class ClaimDetailsController {

	@Autowired
	private PrintReqService reqPrinter;

	@Autowired
	private ClaimDetailsService entityService;

	@PostMapping(value = "/get/policydetails")
	public ResponseEntity<List<PolicyDetailsResponseDto>> policydetailsbyregno(@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);

		List<PolicyDetailsResponseDto> data = entityService.policydetailsbyregno(req);
		if (data != null) {
			return new ResponseEntity<List<PolicyDetailsResponseDto>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/sumInsuredValue")
	public ResponseEntity<List<NetRes>> policydetailsbyregno(@RequestBody NetReq req) {

		List<NetRes> data = entityService.getsumInsured(req);
		if (data != null) {
			return new ResponseEntity<List<NetRes>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/viewQuoteDetails")
	public ResponseEntity<ViewQuoteRes> claimViewQuoteDetails(@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);

		ViewQuoteRes data = entityService.claimViewQuoteDetails(req);
		if (data != null) {
			return new ResponseEntity<ViewQuoteRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/coverdetails")
	public ResponseEntity<List<ClaimCoverdetailsData>> getCoverList(@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);

		List<ClaimCoverdetailsData> data = entityService.getCoverList(req);
		if (data != null) {
			return new ResponseEntity<List<ClaimCoverdetailsData>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/policydetailsbychassissnoApi")
	public ResponseEntity<PolicyDetailsResponseDto> policydetailsbychassissnoApi(@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);

		PolicyDetailsResponseDto data = entityService.policydetails(req);
		if (data != null) {
			return new ResponseEntity<PolicyDetailsResponseDto>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/coinsurancedetails")
	public ResponseEntity<ClaimCoInsurancedetailsData> getcoinsurancedetails(@RequestBody ClaimRequest req) {

		reqPrinter.reqPrint(req);
		ClaimCoInsurancedetailsData data = entityService.getCoinsurancedetailsist(req);

		if (data != null) {
			return new ResponseEntity<ClaimCoInsurancedetailsData>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/customerDetails")
	public ResponseEntity<PolicyInfoDetailsDto> customerDetails(@RequestBody PolicyDetailsReq req) {
		reqPrinter.reqPrint(req);
		PolicyInfoDetailsDto data = entityService.customerDetails(req);
		if (data != null) {
			return new ResponseEntity<PolicyInfoDetailsDto>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/nonMotorDetails")
	public ResponseEntity<NonMotorRes> nonMotorDetails(@RequestBody PolicyDetailsReq req) {
		reqPrinter.reqPrint(req);
		NonMotorRes data = entityService.nonMotorDetails(req);
		if (data != null) {
			return new ResponseEntity<NonMotorRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/nonMotorpolicydetails")
	public ResponseEntity<List<NonMotorPolicyDetailsResponseDto>> nonMotorpolicydetails(
			@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);

		List<NonMotorPolicyDetailsResponseDto> data = entityService.nonMotorpolicydetails(req);
		if (data != null) {
			return new ResponseEntity<List<NonMotorPolicyDetailsResponseDto>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/nonMotorPolicyDetailsList")
	public ResponseEntity<List<NonMotorPolicyDetailsListRes>> nonMotorPolicyDetailsList(
			@RequestBody NonMotorPolicyDetailsListReq req) {
		reqPrinter.reqPrint(req);
		List<NonMotorPolicyDetailsListRes> data = entityService.nonMotorPolicyDetailsList(req);
		if (data != null) {
			return new ResponseEntity<List<NonMotorPolicyDetailsListRes>>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/get/claimPolicydetails")
	public ResponseEntity<CaimPolicydetailsRes> claimPolicydetails(@RequestBody PolicyDetailsReq req) {

		reqPrinter.reqPrint(req);

		CaimPolicydetailsRes data = entityService.caimPolicydetailsRes(req);
		if (data != null) {
			return new ResponseEntity<CaimPolicydetailsRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

}
