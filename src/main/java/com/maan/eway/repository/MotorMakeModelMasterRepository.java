package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.MotorMakeModelMasterId;

public interface MotorMakeModelMasterRepository extends JpaRepository<MotorMakeModelMaster, MotorMakeModelMasterId>,
		JpaSpecificationExecutor<MotorMakeModelMaster> {
	
}
