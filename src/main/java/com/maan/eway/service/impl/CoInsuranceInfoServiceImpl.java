package com.maan.eway.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CoInsuranceInfo;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CoInsuranceInfoRepository;
import com.maan.eway.req.CoInsuranceDetails;
import com.maan.eway.req.CoInsuranceInfoReq;
import com.maan.eway.res.CoInsurance;
import com.maan.eway.service.CoInsuranceInfoService;

@Service
public   class CoInsuranceInfoServiceImpl  implements CoInsuranceInfoService  {
	

	private Logger log=LogManager.getLogger(CoInsuranceInfoServiceImpl.class);
	@Autowired
	private CoInsuranceInfoRepository GetdataReq;

	
	

	@Override
	@Transactional  
	public CommonRes CoInsuranceInfosaveupdate(CoInsuranceDetails req) {
	    CommonRes response = new CommonRes();
	    
	    try {
	    	System.out.println("CoInsurance Save for Quote No: "+req.getQuoteno());
	         GetdataReq.deleteByQuoteno(req.getQuoteno()); 
	        
	         
             List<CoInsuranceInfo> saveList = new ArrayList<>();
	         for (CoInsuranceInfoReq r : req.getCoinsreq()) {
	         CoInsuranceInfo entity = new CoInsuranceInfo();
	         ModelMapper mapper = new ModelMapper();
	          mapper.map(r, entity);
	         entity.setQuoteno(req.getQuoteno());
	         entity.setRequestreferenceno(req.getRequestreferenceno());
	         entity.setProductid(req.getProductid());
	         entity.setStatus("Y");
	         entity.setEffectivedatestart(convertToDateViaSqlDate(LocalDate.now()));
	         entity.setEffectivedateend(convertToDateViaSqlDate(LocalDate.now().plusYears(15)));
	         saveList.add(entity);  
	        }

	        if (!saveList.isEmpty()) {
	           
	            GetdataReq.saveAll(saveList);  
	            response.setErrorMessage(Collections.EMPTY_LIST);
				response.setMessage("Success");
			}else {
				response.setCommonResponse(null);
				response.setErrorMessage(Collections.EMPTY_LIST);
				response.setMessage("Failed");
				response.setIsError(true);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return response;
	}


	

private Date convertToDateViaSqlDate(LocalDate localDate) {
    return java.sql.Date.valueOf(localDate);
}
	   
@Override
@Transactional
public CommonRes CoInsuranceInfodelete(String QUOTENO) {
	
	CommonRes response = new CommonRes();
    try {
    	GetdataReq.deleteByQuoteno(QUOTENO);
    	response.setCommonResponse(null);
		response.setErrorMessage(Collections.EMPTY_LIST);
		response.setMessage("Success");
	}catch (Exception e) {
		e.printStackTrace();
		log.error(e);
	}
	return response;
}
   









@Override
public CommonRes getAllByQuoteNo(String QUOTENO) {
    CommonRes response = new CommonRes();

    List<CoInsuranceInfo> resList = new ArrayList<>();
    CoInsurance ds=new CoInsurance();
    
    List<CoInsuranceInfo> d = GetdataReq.findByQuoteno(QUOTENO);   
    ModelMapper mapper = new ModelMapper();
  List<CoInsurance> result = new ArrayList<>();
  for(CoInsuranceInfo info : d) {
	  CoInsurance map = mapper.map(info, CoInsurance.class);
	  result.add(map);
  }
    if (d != null) {
    	resList.addAll(d);
        response.setCommonResponse(result);
        response.setErrorMessage(Collections.EMPTY_LIST);
        response.setMessage("Success");
    } else {
        response.setCommonResponse(null);
        response.setErrorMessage(Collections.EMPTY_LIST);
        response.setMessage("Failed");
        response.setIsError(true);
    }
 
    return response;
}

public List<Error> validatecoinsurancedetails(CoInsuranceDetails req) {
    List<Error> errors = new ArrayList<>();
    List<CoInsuranceInfoReq> historyInfo = req.getCoinsreq();
    try {
        int row_no = 1; 
        
       
        if (req.getCoinsreq() == null || req.getCoinsreq().isEmpty()) {
            errors.add(new Error("00", "CoInsurerList", "Please provide at least one CoInsurer"));
            return errors;
        }
        
        for (CoInsuranceInfoReq ra :historyInfo) {
          
            if (ra.getSno() == 0) {
            	errors.add(new Error("01","SNo","Please Enter SNo"));
            }
            if (ra.getInsurancecompanyid() == 0) {
            	 errors.add(new Error("02","InsuranceCompanyID","Please Enter Insurance Company ID"));
         }
           
           if (StringUtils.isBlank(ra.getInsurancecompanyname())) {
        	   errors.add(new Error("03","Insurance Company Name","Please Enter Insurance Company Name"));
            }

          
            if (ra.getSharedpercentage() == null ) {
              	 errors.add(new Error("04","Shared Percentage","Please Enter Shared Percentage"));
            }else if(ra.getLeaderparticipant().equalsIgnoreCase("L") && ra.getSharedpercentage().compareTo(BigDecimal.valueOf(100)) != 0) {
            	errors.add(new Error("04","Shared Percentage","For Leader Shared Percentage should be 100 percentage"));
            }
            else if(ra.getLeaderparticipant().equalsIgnoreCase("P") && ra.getSharedpercentage().compareTo(BigDecimal.valueOf(100))>=0) {
            	errors.add(new Error("04","Shared Percentage","For Participant Shared Percentage shoud be less than 100 percentage"));
            }
         
            if (StringUtils.isBlank(ra.getLeaderparticipant())) {
            	 errors.add(new Error("05","Leader Participant","Please Enter Leader Participant"));
            }
          
        

           
            row_no++;
        }
    } catch (Exception e) {
        e.printStackTrace();
        log.info("Exception occurred: " + e.getMessage());
        errors.add(new Error("99", "General", "An error occurred during validation"));
    }

    return errors;
}

}
