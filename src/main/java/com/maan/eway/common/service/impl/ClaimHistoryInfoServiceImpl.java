package com.maan.eway.common.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ClaimHistoryInfo;
import com.maan.eway.common.req.ClaimHistoryInfoDetailsReq;
import com.maan.eway.common.req.ClaimHistoryInfoGetReq;
import com.maan.eway.common.req.ClaimHistoryInfoSaveReq;
import com.maan.eway.common.res.ClaimHistoryInfoDetailsRes;
import com.maan.eway.common.res.ClaimHistoryInfoRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.ClaimHistoryInfoService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.ClaimHistoryInfoRepository;

@Service
public class ClaimHistoryInfoServiceImpl implements ClaimHistoryInfoService{
	private Logger log = LogManager.getLogger(ClaimHistoryInfoServiceImpl.class);
	@Autowired
	private ClaimHistoryInfoRepository claimHistoryRepository;
	
	public List<Error> validateClaimHistoryInfo(ClaimHistoryInfoSaveReq req){
		List<Error> errorList = new ArrayList<>();
		
		if(req.getCompanyId() == null) {
			errorList.add(new Error("01","CompanyId","Company Id Should Not Be Null"));
		}
		if(req.getProductId() == null) {
			errorList.add(new Error("02","ProductId","Product Id Should Not Be Null"));
		}
		if(StringUtils.isBlank(req.getRequestReferenceNo())) {
			errorList.add(new Error("03","RequestReferenceNo","Request Reference No. Should Not Be Blank"));
		}
//		if(StringUtils.isBlank(req.getQuoteNo())) {
//			errorList.add(new Error("04","QuoteNo","Quote No. Should Not Be Blank"));
//		}
//		
		List<ClaimHistoryInfoDetailsReq> historyInfo = req.getClaimHistoryInfo();
		if(historyInfo == null || historyInfo.isEmpty()) {
			errorList.add(new Error("05","ClaimHistoryInfo","Claim History Info Should Not Be Null Or Empty"));
		}
		else {
			int rowNum=1;
			for(ClaimHistoryInfoDetailsReq info : historyInfo) {
				
				if(info.getClhSlNo() == null) {
					errorList.add(new Error("11","CLHSlNo","Please Provide CLH SlNo."+"For Row"+rowNum));
				}
				if(info.getClhDateOfLoss() == null) {
					errorList.add(new Error("12","CLHDateOfLoss","Please Provide CLH Date Of Loss"+" For Row"+rowNum));
				}
				if(StringUtils.isBlank(info.getClhNatureOfLoss())) {
					errorList.add(new Error("13","CLHNatureOfLoss","Please Provide CLH Nature Of Loss"+" For Row"+rowNum));
				}
				if(info.getClhClaimedAmount() == null) {
					errorList.add(new Error("14","CLHClaimedAmount","Please Provide CLH Claimed Amount"+" For Row"+rowNum));
				}
				if(info.getClhClaimYear() == null) {
					errorList.add(new Error("15","CLHClaimYear","Please Provide CLH Claim Year"+" For Row"+rowNum));
				}
				if(StringUtils.isBlank(info.getClhRemarks())) {
					errorList.add(new Error("16","CLHRemarks","Please Provide CLH Remarks"+" For Row"+rowNum));
				}
				rowNum++;
			}
		}		
		return errorList;		
	}
	
	//delete old record create new record for everytime	
	public CommonRes saveUpdateClaimHistoryInfo(ClaimHistoryInfoSaveReq req) {
		try {	
			ModelMapper mapper = new ModelMapper();
			CommonRes response = new CommonRes();
		
			List<ClaimHistoryInfo> allClaimList = claimHistoryRepository.findAllByCompanyIdAndProductIdAndQuoteNoAndRequestReferenceNo(req.getCompanyId(), 
					req.getProductId(), req.getQuoteNo(), req.getRequestReferenceNo());
			
		//delete old record entries
			if(allClaimList != null && !allClaimList.isEmpty()) {
				claimHistoryRepository.deleteAll(allClaimList);	
				response.setCommonResponse(Map.of("Status", "Claim History Info Updated"));	
			}
			else {response.setCommonResponse(Map.of("Status", "Claim History Info Saved"));}
		//create new entries	
			List<ClaimHistoryInfo> saveList = new ArrayList<>();
			
			List<ClaimHistoryInfoDetailsReq> infoList = req.getClaimHistoryInfo();
			if(infoList != null && !infoList.isEmpty()) {			
				for(ClaimHistoryInfoDetailsReq claim : infoList) {
					
				ClaimHistoryInfo saveClaim = mapper.map(claim, ClaimHistoryInfo.class);
				saveClaim.setCompanyId(req.getCompanyId());
				saveClaim.setProductId(req.getProductId());
				saveClaim.setQuoteNo(req.getQuoteNo());
				saveClaim.setRequestReferenceNo(req.getRequestReferenceNo());
				
				saveClaim.setAmendId(0);
				saveClaim.setClhEntryDate(LocalDate.now());
				saveClaim.setEffectiveDateStart(LocalDate.now());
				saveClaim.setEffectiveDateEnd(LocalDate.of(2050, 12, 31));
				saveClaim.setStatus("Y");
				saveList.add(saveClaim);
				}
				claimHistoryRepository.saveAllAndFlush(saveList);
			}
			response.setMessage("Success");
			response.setIsError(false);	
			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	
	public CommonRes getClaimHistoryInfo(ClaimHistoryInfoGetReq req) {
		try {
			ModelMapper mapper = new ModelMapper();
			CommonRes response = new CommonRes();
			List<ClaimHistoryInfo> allClaimList = claimHistoryRepository.findAllByCompanyIdAndProductIdAndQuoteNoAndRequestReferenceNo(req.getCompanyId(), 
					req.getProductId(), req.getQuoteNo(), req.getRequestReferenceNo());
			if(allClaimList != null && !allClaimList.isEmpty()) {
				ClaimHistoryInfoRes getClaim = new ClaimHistoryInfoRes();
			//setting common properties				
				ClaimHistoryInfo claim1 = allClaimList.get(0);
				getClaim.setCompanyId(claim1.getCompanyId());
				getClaim.setProductId(claim1.getProductId());
				getClaim.setQuoteNo(claim1.getQuoteNo());
				getClaim.setRequestReferenceNo(claim1.getRequestReferenceNo());
				
				getClaim.setEffectiveDateStart(claim1.getEffectiveDateStart());
				getClaim.setEffectiveDateEnd(claim1.getEffectiveDateEnd());
				getClaim.setAmendId(claim1.getAmendId());
				getClaim.setStatus(claim1.getStatus());

			//setting inner list properties
				List<ClaimHistoryInfoDetailsRes> infoList = new ArrayList<>();
				for(ClaimHistoryInfo claim :allClaimList) {
					ClaimHistoryInfoDetailsRes infoDetailsRes = mapper.map(claim, ClaimHistoryInfoDetailsRes.class);
					infoList.add(infoDetailsRes);
				}
				getClaim.setClaimHistoryInfo(infoList);
								
				response.setMessage("Success");
				response.setIsError(false);
				response.setCommonResponse(getClaim);
			}
			else {
				ClaimHistoryInfoRes getClaim = new ClaimHistoryInfoRes();
				getClaim.setCompanyId(req.getCompanyId());
				getClaim.setProductId(req.getProductId());
				getClaim.setQuoteNo(req.getQuoteNo());
				getClaim.setRequestReferenceNo(req.getRequestReferenceNo());
				getClaim.setClaimHistoryInfo(List.of());
				
				
				response.setMessage("Success");
				response.setIsError(false);
				response.setCommonResponse(getClaim);
			}
			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	
}
