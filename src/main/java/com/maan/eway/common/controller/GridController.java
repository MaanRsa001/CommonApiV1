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

import com.maan.eway.admin.res.GetallPortfolioActiveRes;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetApproverListReq;
import com.maan.eway.common.req.GetExistingBrokerListReq;
import com.maan.eway.common.req.GetPaymentStatusReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.GetallReferralPendingDetailsRes;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.req.PortFolioGridReq;
import com.maan.eway.common.req.PortfolioSearchReq;
import com.maan.eway.common.req.RegSearchReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SearchBrokerPolicyReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.AdminPendingGridRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.GetApproverListRes;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetPaymentStatusRes;
import com.maan.eway.common.res.GetallExistingRejectedLapsedRes;
import com.maan.eway.common.res.GetallPolicyReportsRes;
import com.maan.eway.common.res.GetallPortfolioPendingRes;
import com.maan.eway.common.res.GetallReferralApprovedDetailsRes;
import com.maan.eway.common.res.GetallReferralDetailsCommonRes;
import com.maan.eway.common.res.GetallReferralRejectedDetailsRes;
import com.maan.eway.common.res.PortFolioDashBoardRes;
import com.maan.eway.common.res.PortfolioByRegNoRes;
import com.maan.eway.common.res.PortfolioGridRes;
import com.maan.eway.common.res.RegNumberRes;
import com.maan.eway.common.res.RevertGridRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.res.ViewLoginDetailsRes;
import com.maan.eway.common.service.GridService;
import com.maan.eway.common.service.impl.PortFolioSearchGridRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/api")
@Api(tags = "GRID DETAILS", description = "API's")
public class GridController {

	@Autowired
	private PrintReqService reqPrinter;

	@Autowired
	private GridService entityService;

	// __________________________________________EXISTING
	// QUOTE__________________________________________
	// EXISTINGQUOTE GRIDS
	// ********************
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/?")//original existingquotedetails
	public ResponseEntity<CommonRes> getallExistingQuoteDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getallExistingQuoteDetails(req);
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
	@PostMapping("/existingquotedetails")
	public ResponseEntity<CommonRes> getallExistingHPM(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getallExistfromHPM(req);
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
	
//	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/existingquotedetailsdashBoard")
	public ResponseEntity<CommonRes> getallExistingHPMdashBoard(@RequestBody ExistingQuoteReq req) {
		req.setStatus("Y");
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getDashBoardallExistfromHPM(req);
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
	
	@PostMapping("/existingquotedetailsdashBoard/referral")
	public ResponseEntity<CommonRes> getallExistingreferraldashBoard(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		req.setStatus("R");
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getDashBoardallExistfromHPM(req);
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
	


	// EXISTING DROPDOWN
	// *****************
	// Broker-->User1,User2... List Of User
	// User1-->User1
	// Issuer-->List of Source Type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/brokeruserdropdown")
	public ResponseEntity<CommonRes> getBrokerUserList(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getBrokerUserList(req);
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

	// _________________________________________________________________________________________________
	// LAPSED QUOTE
	// ************
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/lapsedquotedetails")
	public ResponseEntity<CommonRes> getallLapsedQuoteDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getallLapsedQuoteDetails(req);
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

	// _________________________________________________________________________________________________
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/rejectedquotedetails")
	public ResponseEntity<CommonRes> getallRejectedQuoteDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getallRejectedQuoteDetails(req);
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

	// _________________________________________________________________________________________________
	// Referral Grids
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referralpending")
	public ResponseEntity<CommonRes> getallReferralPendingDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallReferralPendingDetailsRes res = entityService.getallReferralPendingDetails(req);
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
	@PostMapping("/referralapproved")
	public ResponseEntity<CommonRes> getallReferralApprovedDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallReferralApprovedDetailsRes res = entityService.getallReferralApprovedDetails(req);
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
	@PostMapping("/referralrejected")
	public ResponseEntity<CommonRes> getallReferralRejectedDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallReferralRejectedDetailsRes res = entityService.getallReferralRejectedDetails(req);
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
	@PostMapping("/referralrequote")
	public ResponseEntity<CommonRes> getallReferralRequoteDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallReferralDetailsCommonRes res = entityService.getallReferralRequoteDetails(req);
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

	// Admin Referrral Grids
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminreferralpending")
	public ResponseEntity<CommonRes> getallAdminReferralPendings(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallReferralDetailsCommonRes res = entityService.getallAdminReferralPendings(req);
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
	@PostMapping("/adminreferralapproved")
	public ResponseEntity<CommonRes> getallAdminReferralApproved(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallReferralDetailsCommonRes res = entityService.getallAdminReferralApproved(req);
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
	@PostMapping("/adminreferralrejected")
	public ResponseEntity<CommonRes> getallAdminReferralRejecteds(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallReferralDetailsCommonRes res = entityService.getallAdminReferralRejected(req);
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
	@PostMapping("/adminreferralrequote")
	public ResponseEntity<CommonRes> getallAdminReferralRequote(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		GetallReferralDetailsCommonRes res = entityService.getallAdminReferralRequote(req);
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
	@PostMapping("/copyquote")
	public ResponseEntity<CommonRes> copyQuote(@RequestBody CopyQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation = entityService.validateQuotoNo(req);
		// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			CopyQuoteSuccessRes res = entityService.copyQuote(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/searchmotordata")
	public ResponseEntity<CommonRes> getbyReqRefNo(@RequestBody CopyQuoteReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<GetAllMotorDetailsRes> res = entityService.getbyReqRefNo(req);
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
	@PostMapping("/dropdown/copyquoteby")
	public ResponseEntity<CommonRes> copyQuoteByDropdown(@RequestBody CopyQuoteDropDownReq req) {
		CommonRes data = new CommonRes();
		List<DropDownRes> res = entityService.copyQuoteByDropdown(req);
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
	@PostMapping("/updatelapsedquote")
	public ResponseEntity<CommonRes> updateLapsedQuoteDetails(@RequestBody UpdateLapsedQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		UpdateLapsedQuoteRes res = entityService.updateLapsedQuoteDetails(req);
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

	// Portfolio
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/portfolio/active")
	public ResponseEntity<CommonRes> getallPortfolioActive(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallPortfolioActiveRes res = entityService.getallPortfolioActive(req);
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
	@PostMapping("/portfolio/pending")
	public ResponseEntity<CommonRes> getallPortfolioPending(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallPortfolioPendingRes res = entityService.getallPortfolioPending(req);
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
	@PostMapping("/portfolio/cancelled")
	public ResponseEntity<CommonRes> getallPortfolioCancelled(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallPortfolioActiveRes res = entityService.getallPortfolioCancelled(req);

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

	// Quote Grids
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/dropdown/issuerquotedetails")
	public ResponseEntity<CommonRes> getallIssuerQuoteDetails(@RequestBody IssuerQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<DropDownRes> res = entityService.getallIssuerQuoteDetails(req);
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

	// Reports grid
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getall/policyreports")
	public ResponseEntity<CommonRes> getallPolicyReports(@RequestBody GetallPolicyReportsReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetallPolicyReportsRes> res = entityService.getallPolicyReports(req);
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

	// Reports grid
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/admin/portfoliodashboard")
	public ResponseEntity<CommonRes> getAllAdminPortfolio(@RequestBody PortFolioDashBoardReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<PortFolioDashBoardRes> res = entityService.getAllAdminPortfolio(req);
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
	
	// Reports grid
		@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
		@PostMapping("/admin/portfoliob2cdashboard")
		public ResponseEntity<CommonRes> getB2cAdminPortfolio(@RequestBody PortFolioDashBoardReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();
			List<PortFolioDashBoardRes> res = entityService.getB2cAdminPortfolio(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/admin/portfoliopendings")
	public ResponseEntity<CommonRes> getAllPolicyPendingDashboard(@RequestBody PortFolioDashBoardReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<PortFolioDashBoardRes> res = entityService.getAllPolicyPendingDashboard(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/admin/portfoliogrid")
	public ResponseEntity<CommonRes> getAllPolicyGrid(@RequestBody PortFolioGridReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<PortfolioGridRes> res = entityService.getAllPolicyGrid(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/admin/portfoliogridbyregno")
	public ResponseEntity<CommonRes> getAllPorfolioByRegNo(@RequestBody PortfolioSearchReq req){
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<PortfolioByRegNoRes> res = entityService.getAllPorfolioByRegNo(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.EMPTY_LIST);
		data.setMessage("Success");
		if(res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}
		else{
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/admin/portfoliob2cgrid")
	public ResponseEntity<CommonRes> getAllPolicyB2cGrid(@RequestBody PortFolioGridReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<PortfolioGridRes> res = entityService.getAllPolicyB2cGrid(req);
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


	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/getapproverlist")
	public ResponseEntity<CommonRes> getApproverList(@RequestBody GetApproverListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<GetApproverListRes> res = entityService.getApproverList(req);
		List<Error> validation = new ArrayList<Error>();

		if (res.size() > 0) {
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		} else {
			Error err = new Error();
			err.setCode("0");
			err.setField("No UnderWritter");
			err.setMessage("There Is No UnderWritter For This Product");
			validation.add(err);
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		}
	}

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/uwpendinggrid")
	public ResponseEntity<CommonRes> getUwPendingGrid(@RequestBody RevertGridReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		RevertGridRes res = entityService.getUwPendingGrid(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/superadminreferralpending")
	public ResponseEntity<CommonRes> getReAllotUwPendingGrid(@RequestBody RevertGridReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		AdminPendingGridRes res = entityService.getReAllotUwPendingGrid(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/updateuwreferraldetails")
	public ResponseEntity<CommonRes> updateUwReferralDetails(@RequestBody List<RevertGridReq> req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		SuccessRes res = entityService.updateUwReferralDetails(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/searchbrokerpolicies")
	public ResponseEntity<CommonRes> searchBrokerPolicies(@RequestBody SearchBrokerPolicyReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		PortFolioSearchGridRes res = entityService.searchBrokerPolicies(req);
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

//-----------------------------------------CONTENT_TYPE_DROPDOWN_ISSUER-----------------------------------------------------------

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/brokerdropdown") // getExistingBrokerList for that particular issuer
	public ResponseEntity<CommonRes> getExistingBrokerList(@RequestBody GetExistingBrokerListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getExistingBrokerList(req);
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

//_____________________________________________PORTFOLIO DROPDOWN___________________________________________
	
	//Report 
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")

	@PostMapping("/reportbrokerdropdown") 
	public ResponseEntity<CommonRes> getReportBrokerUserList(@RequestBody ExistingBrokerUserListReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getReportBrokerUserList(req);
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
	
	
	//Active
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")

	@PostMapping("/portfoliobrokerdropdown") // Broker-->User1,User2...,Issuer--> Broker,direct,.... List Of User and
												// List of others
	public ResponseEntity<CommonRes> getPortfolioBrokerUserList(@RequestBody ExistingBrokerUserListReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getPortfolioBrokerUserList(req);
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

	//Pending
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/portfoliopendingdropdown")
	public ResponseEntity<CommonRes> getPortfolioPendingDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getPortfolioPendingDropdown(req);
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

	//Cancel
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/cancelpolicyportfoliodropdown") // Broker-->User1,User2...,Issuer--> Broker,direct,.... List Of User
													// and List of others
	public ResponseEntity<CommonRes> getCancelPolicyIssuerDropdownList(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getCancelPolicyIssuerDropdownList(req);
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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/brokeruserdropdownlapsed")
	public ResponseEntity<CommonRes> getBrokerUserListLapsed(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getBrokerUserListLapsed(req);

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

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN','ROLE_USER')")
	@PostMapping("/brokeruserdropdownrejected")
	public ResponseEntity<CommonRes> getBrokerUserListRejected(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getBrokerUserListRejected(req);
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
//__________________________________________REFERRAL___________________________________________________
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referralpendingsdropdown")
	public ResponseEntity<CommonRes> getReferralPendingDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getReferralPendingDropdown(req);
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
	@PostMapping("/referralapproveddropdown")
	public ResponseEntity<CommonRes> getReferralApprovedDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getReferralApprovedDropdown(req);
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
	@PostMapping("/referralrejectdropdown")
	public ResponseEntity<CommonRes> getReferralRejectDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getReferralRejectDropdown(req);
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
	@PostMapping("/referralrequotedropdown")
	public ResponseEntity<CommonRes> getReferralRequoteDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getReferralRequoteDropdown(req);
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
	
	//_______________________________________ADMIN REFERRAL DROPDOWN_________________________________________
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/adminreferralpendingsdropdown")
	public ResponseEntity<CommonRes> getAdminReferralPendingDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getAdminReferralPendingDropdown(req);
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
	@PostMapping("/adminreferralapprovedropdown")
	public ResponseEntity<CommonRes> getAdminReferralApproveDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getAdminReferralApproveDropdown(req);
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
	@PostMapping("/adminreferralrejectropdown")
	public ResponseEntity<CommonRes> getAdminReferralRejectDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getAdminReferralRejectDropdown(req);
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
	@PostMapping("/adminreferralrequoteropdown")
	public ResponseEntity<CommonRes> getAdminReferralReQuoteDropdown(@RequestBody ExistingBrokerUserListReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<GetExistingBrokerListRes> res = entityService.getAdminReferralReQuoteDropdown(req);
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
	@PostMapping("/regnumberquotes")
	public ResponseEntity<CommonRes> getRegNumberQuotes(@RequestBody RegSearchReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		RegNumberRes res = entityService.getRegNumberQuotes(req);
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
	@PostMapping("/paymentpendingstatus")
	public ResponseEntity<CommonRes> getPaymentStatus(@RequestBody GetPaymentStatusReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetPaymentStatusRes res = entityService.getPaymentStatus(req);
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
	@PostMapping("/paymentfailedstatus")
	public ResponseEntity<CommonRes> getPaymentFailedStatus(@RequestBody GetPaymentStatusReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetPaymentStatusRes res = entityService.getPaymentFailedStatus(req);
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
	@PostMapping("/paymentsucessstatus")
	public ResponseEntity<CommonRes> getPaymentSucessStatus(@RequestBody GetPaymentStatusReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetPaymentStatusRes res = entityService.getPaymentSucessStatus(req);
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
	
	//********************************************QUOTE REGISTER FOR SQ-Short Quote*********************************
	//Existing quote

	//@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/sqexistingquotedetails")
	public ResponseEntity<CommonRes> getallExistingQuoteSQ(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getallExistingQuoteSQ(req);
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
	@PostMapping("/sqlapsedquotedetails")
	public ResponseEntity<CommonRes> getallLapsedQuoteSQ(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getallLapsedQuoteDetailSQ(req);
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
	@PostMapping("/sqrejectedquotedetails")
	public ResponseEntity<CommonRes> getallRejectedQuoteSQ(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		GetallExistingRejectedLapsedRes res = entityService.getallRejectedQuoteSQ(req);
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
	@PostMapping("/viewlogindetails")
	public ResponseEntity<CommonRes> viewLoginDetails(@RequestBody ExistingQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		ViewLoginDetailsRes res = entityService.viewLoginDetails(req);
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

