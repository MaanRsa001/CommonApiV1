package com.maan.eway.preinspection;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.maan.eway.bean.PreinspectionDataDetail;

public interface PreInspectionDataDetailRepo extends JpaRepository<PreinspectionDataDetail, Long>{

	PreinspectionDataDetail findByRegistrationNoAndChassisNo(String registrationNo, String chassisNo);
	

	List<PreinspectionDataDetail> findByRegistrationNo(String registrationNo);

	List<PreinspectionDataDetail> findByChassisNo(String chassisNo);

}
