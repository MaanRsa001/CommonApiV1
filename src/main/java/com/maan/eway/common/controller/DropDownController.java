package com.maan.eway.common.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.CertificateDetailsReq;
import com.maan.eway.common.req.GetMachineryContentReq;
import com.maan.eway.common.req.GetOccupationsReq;
import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.common.res.CertificateTypeRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.common.res.GetMachineryContentRes;
import com.maan.eway.common.res.SimplePaymentTypeRes;
import com.maan.eway.common.service.DropDownService;
import com.maan.eway.integration.req.QueryKeyReq;
import com.maan.eway.master.req.BodyTypeDropDownReq;
import com.maan.eway.master.req.BrokerSumInsuredRefReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.req.LovPolicyDropDownReq;
import com.maan.eway.master.req.MotDropdownReq;
import com.maan.eway.master.req.PlanTypeReq;
import com.maan.eway.master.req.RelationDropDownReq;
import com.maan.eway.master.req.UsageDropDownReq;
import com.maan.eway.master.service.MotorBodyTypeMasterService;
import com.maan.eway.master.service.impl.MotorVehicleUsageMasterServiceImpl;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.MotorWithAccessoriesRes;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/dropdown")
@Api(tags = "MASTER : Drop Down Controller", description = "API's")

public class DropDownController {

	@Autowired
	private DropDownService dropDownService;
	
	
	@Autowired
	private MotorVehicleUsageMasterServiceImpl  entityService;
	
	

	@Autowired
	private MotorBodyTypeMasterService service;


//	@PostMapping("/insurancetype")
//	@ApiOperation(value = "This method is to Cover Note Drop Down")
//	public ResponseEntity<CommonRes> insuranceType(@RequestBody LovDropDownReq req) {
//		CommonRes data = new CommonRes();
//
//		List<DropDownRes> res = dropDownService.insuranceType(req);
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	@PostMapping("/gettabledetails")
//	@ApiOperation(value = "This method is to Table Details Drop Down")
//	public ResponseEntity<CommonRes> getTableDetails(@RequestBody LovDropDownReq req) {
//		CommonRes data = new CommonRes();
//
//		List<ColummnDropRes> res = dropDownService.getTableDetails(req);
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//
//	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/citylimit")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> cityLimit(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.cityLimit(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/borrowertype")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> borrowerType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.borrowerType(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/title")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> title(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.title(req);
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
	
//	@PostMapping("/insuranceclass")
//	@ApiOperation(value = "This method is to Cover Note Drop Down")
//	public ResponseEntity<CommonRes> insuranceClass(@RequestBody LovDropDownReq req) {
//		CommonRes data = new CommonRes();
//
//		List<DropDownRes> res = dropDownService.insuranceClass();
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//
//	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/contructtype")
	@ApiOperation(value = "This method is to Construct Type Drop Down")
	public ResponseEntity<CommonRes> constructType(@RequestBody LovDropDownReq req ) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.constructType(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/consecutivedays")
	@ApiOperation(value = "This method is to Construct Type Drop Down")
	public ResponseEntity<CommonRes> consecutiveDays(@RequestBody LovDropDownReq req ) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.consecutiveDays(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/buildingtype")
	@ApiOperation(value = "This method is to Construct Type Drop Down")
	public ResponseEntity<CommonRes> buildingType(@RequestBody LovDropDownReq req ) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.buildingType(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/covernotetype")
	@ApiOperation(value = "This method is to Cover Note Drop Down")
	public ResponseEntity<CommonRes> coverNoteType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.coverNoteType(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/paymentmode")
	@ApiOperation(value = "This method is to Payment Mode Drop Down")
	public ResponseEntity<CommonRes> paymentmode(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.paymentmode(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/endorsementtype")
	@ApiOperation(value = "This method is to Endorsement Type Drop Down")
	public ResponseEntity<CommonRes> endorsementtype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.endorsementtype(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/discounttypeoffered")
	@ApiOperation(value = "This method is to Discount Type Offered Drop Down")
	public ResponseEntity<CommonRes> discounttypeoffered(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.discounttypeoffered(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/istaxexcempted")
	@ApiOperation(value = "This method is to Tax Excepmted Drop Down")
	public ResponseEntity<CommonRes> taxexcempted(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.taxexcempted(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/taxexcemptiontype")
	@ApiOperation(value = "This method is to Tax Excepmtion Type Drop Down")
	public ResponseEntity<CommonRes> taxexcemptiontype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.taxexcemptiontype(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/policyholdertype")
	@ApiOperation(value = "This method is to Policy Holder Type Drop Down")
	public ResponseEntity<CommonRes> policyholdertype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholdertype(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/policyholderidtype")
	@ApiOperation(value = "This method is to Policy Holder ID Type Drop Down")
	public ResponseEntity<CommonRes> policyholderidtype(@RequestBody LovPolicyDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholderidtype(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/policyholdergender")
	@ApiOperation(value = "This method is to Policy Holder Gender Drop Down")
	public ResponseEntity<CommonRes> policyholdergender(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.policyholdergender(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/nametitle")
	@ApiOperation(value = "This method is to Name Title Drop Down")
	public ResponseEntity<CommonRes> nametitle(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.nametitle(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/notificationtype")
	@ApiOperation(value = "This method is to Notification Type Drop Down")
	public ResponseEntity<CommonRes> notificationtype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.notificationtype(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/motorcategory")
	@ApiOperation(value = "This method is get all Motor Category Drop Down")
	public ResponseEntity<CommonRes> getMotorCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMotorCategory(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/motortype")
	@ApiOperation(value = "This method is get all Motor Type Drop Down")
	public ResponseEntity<CommonRes> getMotorType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.getMotorType(req);
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

//	@PostMapping("/motorusage")
//	@ApiOperation(value = "This method is get all Motor Usage Drop Down")
//	public ResponseEntity<CommonRes> getMotorUsage(@RequestBody LovDropDownReq req) {
//
//		CommonRes data = new CommonRes();
//
//		// Save
//		List<DropDownRes> res = dropDownService.getMotorUsage(req);
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/ownercategory")
	@ApiOperation(value = "This method is get all Owner Category Drop Down")
	public ResponseEntity<CommonRes> ownerCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.ownerCategory(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/fleettype")
	@ApiOperation(value = "This method is get all Fleet Type Drop Down")
	public ResponseEntity<CommonRes> fleetType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.fleetType(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/reinsurancecategory")
	@ApiOperation(value = "This method is get all Reinsurance Category Drop Down")
	public ResponseEntity<CommonRes> reinsuranceCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceCategory(req);
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
	
	@PostMapping("/participanttype")
	@ApiOperation(value = "This method is get all Participant Type Drop Down")
	public ResponseEntity<CommonRes> participantType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.participantType(req);
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
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/reinsuranceform")
	@ApiOperation(value = "This method is get all Reinsurance Form Drop Down")
	public ResponseEntity<CommonRes> reinsuranceForm(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceForm(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/reinsurancetype")
	@ApiOperation(value = "This method is get all Reinsurance Type Drop Down")
	public ResponseEntity<CommonRes> reinsuranceType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.reinsuranceType(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/claimformdullyfilled")
	@ApiOperation(value = "This method is get all Claim Form Dully Filled Drop Down")
	public ResponseEntity<CommonRes> claimformdullyfilled(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimformdullyfilled(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/lostassessmentoption")
	@ApiOperation(value = "This method is get all Lost Assessment Option Drop Down")
	public ResponseEntity<CommonRes> lostassessmentoption(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.lostassessmentoption(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/assessoridtype")
	@ApiOperation(value = "This method is get all Assessor Id Type Drop Down")
	public ResponseEntity<CommonRes> assessoridtype(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.assessoridtype(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/claimantcategory")
	@ApiOperation(value = "This method is get all Calimant Category Drop Down")
	public ResponseEntity<CommonRes> claimantCategory(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantCategory(req);
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

	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/claimanttype")
	@ApiOperation(value = "This method is get all Calimant Type Drop Down")
	public ResponseEntity<CommonRes> claimantType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantType(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/claimantidtype")
	@ApiOperation(value = "This method is get all Calimant Id Type Drop Down")
	public ResponseEntity<CommonRes> claimantIdType(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimantIdType(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/isreassessment")
	@ApiOperation(value = "This method is get all Is Reassessment Drop Down")
	public ResponseEntity<CommonRes> isreassessment(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.isreassessment(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/offeraccepted")
	@ApiOperation(value = "This method is get all Offer Accepted Drop Down")
	public ResponseEntity<CommonRes> offerAccepted(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.offerAccepted(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/partiesnotified")
	@ApiOperation(value = "This method is get all Parties Notified Drop Down")
	public ResponseEntity<CommonRes> partiesNotified(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.partiesNotified(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/claimresultedlitigation")
	@ApiOperation(value = "This method is get all Claim Resulted Litigation Drop Down")
	public ResponseEntity<CommonRes> claimResultedLitigation(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.claimResultedLitigation(req);
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
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/tonnage")
	@ApiOperation(value = "This method is get all Tonnage Drop Down")
	public ResponseEntity<CommonRes> tonnage(@RequestBody LovDropDownReq req) {

		CommonRes data = new CommonRes();

		// Save
		List<DropDownRes> res = dropDownService.tonnage(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getncddetails")
	public ResponseEntity<CommonRes> getNcdDetails(@RequestBody NcdDetailsGetReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getNcdDetails(req);
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
	@PostMapping("/language")
	public ResponseEntity<CommonRes> getLanguage(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getLanguage(req);
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
	@PostMapping(value="/fueltype",produces = "application/json")
	public ResponseEntity<CommonRes> getFuelType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getFuelType(req);
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
	@PostMapping("/plantype")
	public ResponseEntity<CommonRes> getPlanType(@RequestBody PlanTypeReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPlanType(req);
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
	@PostMapping("/relationtype")
	public ResponseEntity<CommonRes> getRelationType(@RequestBody RelationDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getRelationType(req);
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
	
	@PostMapping("/buildingpurpose")
	public ResponseEntity<CommonRes> getBuildingPurpose(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getBuildingPurpose(req);
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
	@PostMapping("/buildingusage")
	public ResponseEntity<CommonRes> getBuildingUsage(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getBuildingUsage(req);
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
	@PostMapping("/paymenttype")
	public ResponseEntity<CommonRes> getPaymentType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPaymentType(req);
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
	@PostMapping("/content")
	public ResponseEntity<CommonRes> getContent(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getContent(req);
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
	@PostMapping("/personal")
	public ResponseEntity<CommonRes> personal(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPersonal(req);
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
	@PostMapping("/contentrisk")
	public ResponseEntity<CommonRes> contentrisk(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getcontentrisk(req);
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
	@PostMapping("/allrisk")
	public ResponseEntity<CommonRes> getallrisk(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getallrisk(req);
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
	@PostMapping("/electronicitems")
	public ResponseEntity<CommonRes> getallelectronicItems(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getallelectronicItems(req);
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
	@PostMapping("/datatype")
	public ResponseEntity<CommonRes> datatype(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.datatype(req);
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
	@PostMapping("/termsandcondition")
	public ResponseEntity<CommonRes> termsandcondition(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.termsandcondition(req);
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
	@PostMapping("/categoryid")
	public ResponseEntity<CommonRes> categoryid(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.categoryid(req);
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
	@PostMapping("/benefitcovermonth")
	public ResponseEntity<CommonRes> benefitcovermonth(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.benefitcovermonth(req);
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
	@PostMapping("/quotestatus")
	public ResponseEntity<CommonRes> quoteStatus(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.quoteStatus(req);
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
	@PostMapping("/termstype")
	public ResponseEntity<CommonRes> termsType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.termsType(req);
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
	@PostMapping("/drivertype")
	public ResponseEntity<CommonRes> driverType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.driverType(req);
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
	
	@PostMapping("/querykeycolumns")
	public ResponseEntity<CommonRes> getQueryKeyColumns(@RequestBody QueryKeyReq req){
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getQueryKeyColumns(req);
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
	@PostMapping("/industrycategory")
	public ResponseEntity<CommonRes> industryCategory(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.industryCategory(req);
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
	@PostMapping("/followupDetailsStatus")
	public ResponseEntity<CommonRes> followupDetailsStatus(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.followupDetailsStatus(req);
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
	@PostMapping("/endtdependantfields")
	public ResponseEntity<CommonRes> endtDependantFields(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.endtDependantFields(req);
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
	@PostMapping("/benefittypes")
	public ResponseEntity<CommonRes> productBenefitsTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.productBenefitsTypes(req);
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
	@PostMapping("/walltypes")
	public ResponseEntity<CommonRes> getWallTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getWallTypes(req);
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
	@PostMapping("/rooftypes")
	public ResponseEntity<CommonRes> getRoofTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getRoofTypes(req);
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

	/*@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/suminsuredreferral") */

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value = "/suminsuredreferral",produces = "application/json")
	public ResponseEntity<CommonRes> brokerSumInsured(@RequestBody BrokerSumInsuredRefReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.brokerSumInsuredRefrral(req);
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
	@PostMapping(value = "/issuerSumInsuredRefrral",produces = "application/json") 
	public ResponseEntity<CommonRes> issuerSumInsured(@RequestBody BrokerSumInsuredRefReq req) { 
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.issuerSumInsuredRefrral(req);
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
	@PostMapping(value = "/nonMotorSumInsuredRefrral",produces = "application/json")
	public ResponseEntity<CommonRes> nonMotorSumInsured(@RequestBody BrokerSumInsuredRefReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.nonMotorSumInsuredRefrral(req); 
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
	@PostMapping("/auditenttype")
	public ResponseEntity<CommonRes> getAuditentType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getAuditentType(req);
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
	@PostMapping("/fidelityEmployeeCount")
	public ResponseEntity<CommonRes> getfidelityEmployeeCount(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getFidelityEmployeeCount(req);
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
		
	@PostMapping("/natureoftrade")
	public ResponseEntity<CommonRes> getNatureOfTrade(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getNatureOfTrade(req);
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
	@PostMapping("/fidelitySuminsured")
	public ResponseEntity<CommonRes> getfidelitySuminsured(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getFidelitySuminsured(req);
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
	@PostMapping("/burglaryinsurancefor")
	public ResponseEntity<CommonRes> getBurglaryInsuranceFor(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getBurglaryInsuranceFor(req);
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
	@PostMapping("/ceilingtype")
	public ResponseEntity<CommonRes> getCeilingType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getCeilingType(req);
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
	@PostMapping("/windowsmaterials")
	public ResponseEntity<CommonRes> getWindowsMaterials(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getWindowsMaterials(req);
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
	@PostMapping("/doorsmaterials")
	public ResponseEntity<CommonRes> getDoorsMaterials(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getDoorsMaterials(req);
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
	@PostMapping("/nightleftdoor")
	public ResponseEntity<CommonRes> getNightLeftDoor(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getNightLeftDoor(req);
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
	@PostMapping("/buildingoccupied")
	public ResponseEntity<CommonRes> getBuildingOccupied(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getBuildingOccupied(req);
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
	@PostMapping("/openoption")
	public ResponseEntity<CommonRes> getOpenoption(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getOpenoption(req);
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
	@PostMapping("/firstlosspercent")
	public ResponseEntity<CommonRes> getFirstLossPercent(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getFirstLossPercent(req);
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
	@PostMapping("/indemity")
	public ResponseEntity<CommonRes> getIndemity(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getIndemity(req);
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
	@PostMapping("/plateglass")
	public ResponseEntity<CommonRes> getPlateGlass(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPlateGlass(req);
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
	@PostMapping("/occupations")
	public ResponseEntity<CommonRes> getOccupations(@RequestBody GetOccupationsReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getOccupations(req);
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
	@PostMapping("/motorcontent")
	public ResponseEntity<CommonRes> getMotorContent(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getMotorContent(req);
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
	@PostMapping("/machinerycontent")
	public ResponseEntity<CommonRes> getMachineryContent(@RequestBody GetMachineryContentReq req) {
		CommonRes data = new CommonRes();
		GetMachineryContentRes res = dropDownService.getMachineryContent(req);
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
	@PostMapping("/sectionmodifytype")
	public ResponseEntity<CommonRes> getSectionModifyType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getSectionModifyType(req);

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
	@PostMapping("/taxfor")
	public ResponseEntity<CommonRes> getTaxFor(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getTaxFor(req);

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
	@PostMapping("/taxpaymenttype")
	public ResponseEntity<CommonRes> getPaymentFor(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPaymentFor(req);

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
	@PostMapping("/adminportfoliotypes")
	public ResponseEntity<CommonRes> getAdminPortFolioTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getAdminPortFolioTypes(req);

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
	@PostMapping("/reportbusinesstypes")
	public ResponseEntity<CommonRes> getReportBuissnessTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getReportBuissnessTypes(req);

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
	@PostMapping("/plantallrisk")
	public ResponseEntity<CommonRes> getPlantAllRisk(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPlantAllRisk(req);

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
	@PostMapping("/businessallrisk")
	public ResponseEntity<CommonRes> getBusinessAllRisk(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getBusinessAllRisk(req);

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
	@PostMapping("/planbenefits")
	public ResponseEntity<CommonRes> getPlanBenefits(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPlanBenefits(req);
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
	@PostMapping("/cyberinsurancetypes")
	public ResponseEntity<CommonRes> getCyberInsuranceTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getCyberInsuranceTypes(req);
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
	@PostMapping("/cybercontents")
	public ResponseEntity<CommonRes> getCyberContents(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getCyberContents(req);
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
	@PostMapping("/taxfordesc")
	public ResponseEntity<CommonRes> getTaxForDesc(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getTaxForDesc(req);
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
	@PostMapping("/motorWithaccessories")
	public ResponseEntity<CommonRes> getTaxForDesc(@RequestBody MotDropdownReq req) {
		CommonRes data = new CommonRes();
		List<MotorWithAccessoriesRes> res = dropDownService.getMotAccDropdown(req);
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
	@PostMapping("/premiasourcetypes")
	public ResponseEntity<CommonRes> getPremiaSourceTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getPremiaSourceTypes(req);
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
	@PostMapping("/getsourcetype")
	public ResponseEntity<CommonRes> getSourceTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.getSourceType(req);
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
	@PostMapping("/life/paymentType")
	public ResponseEntity<CommonRes> lifePaymentType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.lifePaymentTerms(req);
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
	@PostMapping("/life/policyterms")
	public ResponseEntity<CommonRes> lifePolicyTerms(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.lifePolicyTerms(req);
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
	@PostMapping("/buildingpropertytypes")
	public ResponseEntity<CommonRes> buildingPropertyTypes(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.buildingPropertyTypes(req);
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
	@PostMapping("/modeoftransport")
	public ResponseEntity<CommonRes> modeOfTransPort(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.modeOfTransPort(req);
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
	@PostMapping("/geographicalcoverage")
	public ResponseEntity<CommonRes> geographicalCoverage(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.geographicalCoverage(req);
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
	@PostMapping("/transportedby")
	public ResponseEntity<CommonRes> transportedBy(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.transportedBy(req);
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
	@PostMapping("/errormodules")
	public ResponseEntity<CommonRes> errormodules(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.errormodules(req);
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
	@PostMapping("/vehicleclasses")
	public ResponseEntity<CommonRes> vehicleClasses(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.vehicleClasses(req);
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
	@PostMapping("/maritalstatus")
	public ResponseEntity<CommonRes> maritalStatus(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.maritalStatus(req);
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
	@PostMapping("/quoteperiod")
	public ResponseEntity<CommonRes> quotePeriod(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.quotePeriod(req);
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
	@PostMapping("/ratingrelationtypes")
	public ResponseEntity<CommonRes> ratingRelationType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.ratingRelationType(req);
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
	@PostMapping("/CharacterType")
	public ResponseEntity<CommonRes> characterType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.characterType(req);
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
	@PostMapping("/professionaltype")
	public ResponseEntity<CommonRes> professionalType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.professionalType(req);
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
	@PostMapping("/indemnitytype")
	public ResponseEntity<CommonRes> indemnityType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.indemnityType(req);
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
	@PostMapping("/claimtype")
	public ResponseEntity<CommonRes> claimType(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.claimType(req);
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
	@GetMapping("/brokerlist/{companyId}")
	public ResponseEntity<CommonRes> brokerlist(@PathVariable ("companyId") String companyId){
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.brokerlist(companyId);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@GetMapping("/policyEndDate")
	public ResponseEntity<?> policyEndDateList(@RequestParam ("policyStartDate") String policyStartDate){
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.policyEndDateList(policyStartDate);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res != null) {
			return new ResponseEntity<CommonRes>(data,HttpStatus.ACCEPTED);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/socioProfessional")
	@ApiOperation(value = "This method is to Socio Professional Category Drop Down")
	public ResponseEntity<CommonRes> socioProfessionalCategory(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.socioProfessionalCategory(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(null);
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/municipalityTraffic")
	@ApiOperation(value = "This method is to Municipality Traffic Drop Down")
	public ResponseEntity<CommonRes> municipalityTraffic(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.municipalityTraffic(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(null);
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/aggregatedValue")
	@ApiOperation(value = "This method is to Aggregated Value Drop Down")
	public ResponseEntity<CommonRes> aggregatedValue(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();

		List<DropDownRes> res = dropDownService.aggregatedValue(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(null);
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value = "/policytypereferral",produces = "application/json")
	public ResponseEntity<CommonRes> policyTypeReferral(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.policyTypeReferral(req);
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
	@PostMapping(value = "/VehicleAgereferral",produces = "application/json")
	public ResponseEntity<CommonRes> VehcileAgeReferral(@RequestBody LovDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = dropDownService.VehicleAgeReferral(req);
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
	@PostMapping(value = "/getCertificateType",produces = "application/json")
	public ResponseEntity<CertificateTypeRes> getcertificateType(@RequestBody CertificateDetailsReq req) {
		//CommonRes data = new CommonRes()
		CertificateTypeRes res = dropDownService.getcertificateType(req);
		
		if (res != null) {
			return new ResponseEntity<CertificateTypeRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value = "/getBookId",produces = "application/json")
	public ResponseEntity<CertificateTypeRes> getBookId() {
		//CommonRes data = new CommonRes()
		CertificateTypeRes res = dropDownService.getBookIds();
		
		if (res != null) {
			return new ResponseEntity<CertificateTypeRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value = "/getCertificateNo",produces = "application/json")
	public ResponseEntity<CertificateTypeRes> getcertificateNo(@RequestBody CertificateDetailsReq req) {
		//CommonRes data = new CommonRes()
		CertificateTypeRes res = dropDownService.getcertificateNo(req);
		
		if (res != null) {
			return new ResponseEntity<CertificateTypeRes>(res, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value="/vehicleBodyType",produces = "application/json")
	@ApiOperation(value = "This method is get Motor Vehicle Body Type Drop Down")

	public ResponseEntity<DropdownCommonRes> getInduvidualVehicleBodyType(@RequestBody UsageDropDownReq req) {

		DropdownCommonRes data = new DropdownCommonRes();

		// Save
		List<DropDownRes> res = entityService.getInduvidualVehicleUsageDropdown(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/vehicleusage")
	@ApiOperation(value = "This method is get Body Type Drop Down")

	public ResponseEntity<DropdownCommonRes> getVehicleUsage(@RequestBody BodyTypeDropDownReq req) {

		DropdownCommonRes data = new DropdownCommonRes();

		// Save
		List<DropDownRes> res = service.getInduvidualBodyTypeMasterDropdown(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

	}
	
	@GetMapping("/lipilapaymenttype")
	@ApiOperation(value = "Simple Lipila payment types")
	public ResponseEntity<List<SimplePaymentTypeRes>> getSimpleLipilaPaymentType() {
	    List<SimplePaymentTypeRes> list = Arrays.asList(
	        new SimplePaymentTypeRes("card", "Card Payment in Lipila"),
	        new SimplePaymentTypeRes("mobile-money", "Mobile Money Payment in Lipila")
	    );
	    return new ResponseEntity<>(list, HttpStatus.OK);
	}
	


}
