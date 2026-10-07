package com.maan.eway.master.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.CompanyMasterValidateReq;
import com.maan.eway.master.req.GlobalCommonValidationReq;
import com.maan.eway.master.req.NormalMasterValidateReq;
import com.maan.eway.master.service.MasterCommonValidationService;

@Transactional
@Service
public class MasterCommonValidationServiceImpl implements  MasterCommonValidationService {

	private Logger log=LogManager.getLogger(MasterCommonValidationServiceImpl.class);
	
	@Override
	public List<Error> validateGlobalMaster(GlobalCommonValidationReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {
				
			if (StringUtils.isBlank(req.getRemarks()) ) {
				errorList.add(new Error("03", "Remark", "Please Select Remark "));
			}else if (req.getRemarks().length() > 100){
				errorList.add(new Error("03","Remark", "Please Enter Remark within 100 Characters")); 
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null ) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
	
			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			} else if (req.getEffectiveDateEnd() == null ) {
				errorList.add(new Error("04", "EffectiveDateEnd", "Please Enter Effective Date End "));
	
			} else if (req.getEffectiveDateEnd().before(req.getEffectiveDateStart()) || req.getEffectiveDateEnd().equals(req.getEffectiveDateStart())) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date End  is After Effective Date Start"));
			} 
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Enter Status 1 Character Only"));
			}else if(!("Y".equals(req.getStatus())||"N".equals(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Enter Status Y or N Only"));
			}
			
	
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("08", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 50) {
				errorList.add(new Error("08", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			} 
				
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode"));
			}else if (req.getRegulatoryCode().length() > 20) {
				errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters"));
			}
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			errorList.add(new Error("10", "Common Error", e.getMessage()));
		}
		return errorList;
	}
	
	
	@Override
	public List<Error> validateCompanyMaster(CompanyMasterValidateReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {
			
			if (StringUtils.isBlank(req.getRemarks()) ) {
				errorList.add(new Error("03", "Remark", "Please Select Remark "));
			}else if (req.getRemarks().length() > 100){
				errorList.add(new Error("03","Remark", "Please Enter Remark within 100 Characters")); 
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null ) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
	
			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			} else if (req.getEffectiveDateEnd() == null ) {
				errorList.add(new Error("04", "EffectiveDateEnd", "Please Enter Effective Date End "));
	
			} else if (req.getEffectiveDateEnd().before(req.getEffectiveDateStart()) || req.getEffectiveDateEnd().equals(req.getEffectiveDateStart())) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date End  is After Effective Date Start"));
			} 
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Enter Status 1 Character Only"));
			}else if(!("Y".equals(req.getStatus())||"N".equals(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Enter Status Y or N Only"));
			}
		
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 50) {
				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			} 
			
			if (StringUtils.isBlank(req.getCoreAppCode())) {
				errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode"));
			}else if (req.getCoreAppCode().length() > 20) {
				errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode within 20 Characters"));
			}
			
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode"));
			}else if (req.getRegulatoryCode().length() > 20) {
				errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters"));
			}
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			errorList.add(new Error("12", "Common Error", e.getMessage()));
		}
		return errorList;
	}
	
	
	@Override
	public List<Error> validateNoramlMaster(NormalMasterValidateReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {
			
			if (StringUtils.isBlank(req.getRemarks()) ) {
				errorList.add(new Error("03", "Remark", "Please Select Remark "));
			}else if (req.getRemarks().length() > 100){
				errorList.add(new Error("03","Remark", "Please Enter Remark within 100 Characters")); 
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null ) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
	
			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			} else if (req.getEffectiveDateEnd() == null ) {
				errorList.add(new Error("04", "EffectiveDateEnd", "Please Enter Effective Date End "));
	
			} else if (req.getEffectiveDateEnd().before(req.getEffectiveDateStart()) || req.getEffectiveDateEnd().equals(req.getEffectiveDateStart())) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date End  is After Effective Date Start"));
			} 
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Enter Status 1 Character Only"));
			}else if(!("Y".equals(req.getStatus())||"N".equals(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Enter Status Y or N Only"));
			}
		
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 50) {
				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			} 
			
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode"));
			}else if (req.getRegulatoryCode().length() > 20) {
				errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters"));
			}
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			errorList.add(new Error("12", "Common Error", e.getMessage()));
		}
		return errorList;
	}
}
