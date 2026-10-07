package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.MotorColorMaster;
import com.maan.eway.bean.MotorColorMasterId;

public interface MotorColorMasterRepository
		extends JpaRepository<MotorColorMaster, MotorColorMasterId>,
		JpaSpecificationExecutor<MotorColorMaster> {

	MotorColorMaster findByCompanyIdAndBranchCodeAndColorCode(String insuranceId, String branchCode, String string);


}
