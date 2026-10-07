package com.maan.eway.endorsment.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteDetailsRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.request.SingleCanceleDto;
import com.maan.eway.endorsment.service.EndorsementService;
import com.maan.eway.endorsment.service.SingleApiForCancellationService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.service.impl.CalculatorEngineService;
@Service
public class SingleApiForCancellationServiceImpl implements SingleApiForCancellationService {
	
	private Logger log = LogManager.getLogger(SingleApiForCancellationServiceImpl.class);
	
	@Autowired
	private EndorsementService eservice;
	
	@Autowired
	private CalculatorEngineService calccall;
	
	@Autowired
	private MotorDataDetailsRepository motordataRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Override
	public CommonRes singleCancelPolicy(Endorsment request, String string) {
		try {
			CommonRes endorsment = eservice.createEndorsment(request);
			String requestRefNo = endorsment.getRequestRefNo();
			CalcEngine calc = new CalcEngine();
			
			calc.setRequestReferenceNo(requestRefNo);
			calc.setCoverModification("Y");
			calc.setEffectiveDate(request.getEndtEffectiveDate());
			calccall.getCalc(calc, string);
			request.setRequestReferenceNo(requestRefNo);		
			CommonRes cancelPolicy = eservice.cancelPolicy(request);
			return cancelPolicy;
		}
		catch(Exception e)
		{
			log.info("SingleApiForCancellationServiceImpl Exception is ---> " + e.getMessage());
			return null;
		}
		
	}

	@Override
	public CommonRes waCancellationALL(Endorsment request, String string) {
		try {
			CommonRes res = new CommonRes();
			List<MotorDataDetails> motor = motordataRepo.findByRegistrationNumber(request.getRegistrationNo());
			if(motor!=null && !motor.isEmpty())
			{
				String quoteNo = motor.get(0).getQuoteNo();
				HomePositionMaster homedata = homeRepo.findByQuoteNo(quoteNo);
				request.setPolicyNo(homedata.getOriginalPolicyNo());
				request.setCompanyId(homedata.getCompanyId());
				request.setProductId(new BigDecimal(homedata.getProductId()));
				request.setBranchCode(homedata.getBranchCode());
				request.setEndtType("842");
				request.setEndtRemarks("Cancellation");
				request.setApplicationId("commonapprover");
				request.setUserType("Issuer");
				request.setSubUserType("low");
				CommonRes endorsment = eservice.createEndorsment(request);
				String requestRefNo = endorsment.getRequestRefNo();
				CalcEngine calc = new CalcEngine();
				calc.setRequestReferenceNo(requestRefNo);
				calc.setCoverModification("Y");
				calc.setEffectiveDate(request.getEndtEffectiveDate());
				calccall.getCalc(calc, string);
				request.setRequestReferenceNo(requestRefNo);		
				CommonRes cancelPolicy = eservice.cancelPolicy(request);
				ViewQuoteRes viewQuoteDetails= (ViewQuoteRes) cancelPolicy.getCommonResponse();
				QuoteDetailsRes quoteDetails = viewQuoteDetails.getQuoteDetails();
				BigDecimal endtPremium = quoteDetails.getEndtPremium();
				BigDecimal endtPremiumTax = quoteDetails.getEndtPremiumTax();
				BigDecimal totalEndtPremium = quoteDetails.getTotalEndtPremium();
				DecimalFormat df = new DecimalFormat("#,##0.00");
				SingleCanceleDto dto = new SingleCanceleDto();
				dto.setPolicyNo(homedata.getOriginalPolicyNo());
				dto.setRegistrationNumber(request.getRegistrationNo());
				dto.setEndtPremium(df.format(endtPremium));
				dto.setEndtPremiumTax(df.format(endtPremiumTax));
				dto.setRefund(df.format(totalEndtPremium)); // RefundAmount = TotalEndtPremium
				res.setCommonResponse(dto);
				return res;
			}
			return null;
		}
		catch(Exception e)
		{
			log.info("SingleApiForCancellationServiceImpl Exception is ---> " + e.getMessage());
			return null;
		}
	}

	@Override
	public List<Error> validateEndtDetails(Endorsment request) {
		List<Error> error = new ArrayList<Error>();

		try {
			List<MotorDataDetails> motor = motordataRepo.findByRegistrationNumber(request.getRegistrationNo());
			if(motor==null || motor.isEmpty())
			{
				error.add(new Error("01", "No data Found", "Nodata Found for this RegistrationNumber"));
				return error;
			}
			HomePositionMaster policy = homeRepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(motor.get(0).getPolicyNo(),"P", motor.get(0).getCompanyId(), Integer.valueOf(motor.get(0).getProductId().intValue()));
			if(request.getEndtEffectiveDate() ==null ) {
				error.add(new Error("01", "EndtEffectiveDate", "Please Select Endoresment Effective Date"));
				
			} else if (request.getEndtEffectiveDate().before(policy.getInceptionDate())) {
			    error.add(new Error("02", "EndtEffectiveDate", "Endorsement Effective Date cannot be before Policy Inception Date"));
			}
			
			}
		catch (Exception e) {
			e.printStackTrace();
			error.add(new Error("01", "CommonError", e.getMessage() ));
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return error;
	}

}
