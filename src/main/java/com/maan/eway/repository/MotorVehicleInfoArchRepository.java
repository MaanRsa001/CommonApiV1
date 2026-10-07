package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.MotorVehicleInfoArch;
import com.maan.eway.bean.MotorVehicleInfoArchId;

public interface MotorVehicleInfoArchRepository extends JpaRepository<MotorVehicleInfoArch,MotorVehicleInfoArchId > , JpaSpecificationExecutor<MotorVehicleInfoArch> {

}
