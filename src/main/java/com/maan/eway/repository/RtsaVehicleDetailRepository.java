package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.RtsaVehicleDetail;

public interface RtsaVehicleDetailRepository extends JpaRepository<RtsaVehicleDetail, String>{

	List<RtsaVehicleDetail> findByCodeAndMessageIsNull(String code);

	List<RtsaVehicleDetail> findByRegistrationNoAndCodeAndMessageIsNull(String regNo, String string);

}
