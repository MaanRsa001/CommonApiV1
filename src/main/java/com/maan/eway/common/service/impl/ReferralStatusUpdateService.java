package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;


@Service
public class ReferralStatusUpdateService {

	
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateMotorStatus(String requestReferenceNo, AdminReferalStatusReq req) {
	    List<EserviceMotorDetails> motorDatas =
	        eserMotRepo.findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(requestReferenceNo, "D");
	    for (EserviceMotorDetails mot : motorDatas) {
	        mot.setStatus(req.getStatus());
	        mot.setAdminLoginId(req.getAdminLoginId());
	        mot.setAdminRemarks(req.getAdminRemarks());
	        mot.setRejectReason(req.getRejectReason());
	        mot.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent())
	                ? BigDecimal.ZERO : new BigDecimal(req.getCommissionPercent()));
	        mot.setUpdatedDate(new Date());
	        eserMotRepo.saveAndFlush(mot);
	    }
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateCommonStatus(String requestReferenceNo, AdminReferalStatusReq req) {
	    List<EserviceCommonDetails> commonDatas =
	        eserCommonRepo.findByRequestReferenceNoOrderByRiskIdAsc(requestReferenceNo);
	    for (EserviceCommonDetails com : commonDatas) {
	        com.setStatus(req.getStatus());
	        com.setAdminLoginId(req.getAdminLoginId());
	        com.setAdminRemarks(req.getAdminRemarks());
	        com.setRejectReason(req.getRejectReason());
	        com.setUpdatedDate(new Date());
	        com.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent())
	                ? BigDecimal.ZERO : new BigDecimal(req.getCommissionPercent()));
	        eserCommonRepo.saveAndFlush(com);
	    }
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateBuildingStatus(String requestReferenceNo, AdminReferalStatusReq req) {
	    List<EserviceBuildingDetails> buildingDatas =
	        eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(requestReferenceNo);
	    for (EserviceBuildingDetails build : buildingDatas) {
	        build.setStatus(req.getStatus());
	        build.setAdminLoginId(req.getAdminLoginId());
	        build.setAdminRemarks(req.getAdminRemarks());
	        build.setRejectReason(req.getRejectReason());
	        build.setUpdatedDate(new Date());
	        build.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent())
	                ? BigDecimal.ZERO : new BigDecimal(req.getCommissionPercent()));
	        eserBuildRepo.saveAndFlush(build);
	    }
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateTravelStatus(String requestReferenceNo, AdminReferalStatusReq req) {
	    EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(requestReferenceNo);
	    travelData.setStatus(req.getStatus());
	    travelData.setAdminLoginId(req.getAdminLoginId());
	    travelData.setAdminRemarks(req.getAdminRemarks());
	    travelData.setRejectReason(req.getRejectReason());
	    travelData.setUpdatedDate(new Date());
	    travelData.setCommissionPercentage(StringUtils.isBlank(req.getCommissionPercent())
	            ? BigDecimal.ZERO : new BigDecimal(req.getCommissionPercent()));
	    eserTraRepo.saveAndFlush(travelData);
	}
}