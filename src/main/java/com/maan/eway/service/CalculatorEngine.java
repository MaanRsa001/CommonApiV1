package com.maan.eway.service;

import java.math.BigDecimal;
import java.util.List;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.calcengine.ReferralApi;
import com.maan.eway.res.calc.AdminReferral;
import com.maan.eway.res.calc.DebitAndCredit;

import jakarta.persistence.Tuple;

public interface CalculatorEngine {
	//void LoadSection(CalcEngine engine) ;
	List<Tuple> LoadCover(CalcEngine engine) ;
	 
	EserviceMotorDetailsSaveRes  calculator(CalcEngine engine,String token);
	EserviceMotorDetailsSaveRes referalCalculator(CalcEngine request,String token);
	 List<DebitAndCredit> commissionCalc(CalcCommission request);
	 void loadOnetimetable(CalcEngine engine) ;
	 List<AdminReferral> getReferalList(ReferralApi request);
	 EserviceMotorDetailsSaveRes endorsementCalculator(CalcEngine request, BigDecimal endtCount,String endtTypeId,Boolean isPolicyDateEndt);

	String getPolicyNo(CalcCommission request, HomePositionMaster hpm);

	EserviceMotorDetailsSaveRes policyCalculator(CalcEngine request, String string);

	EserviceMotorDetailsSaveRes policyReferralCalc(CalcEngine engine);

	List<EserviceMotorDetailsSaveRes> getCalc(CalcEngine request, String string);

}
