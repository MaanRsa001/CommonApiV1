package com.maan.eway.viewAll.entity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
@Repository
public interface EmployeeInfoRepo  extends JpaRepository<EmployeeInfo,EmployeeInfoId > , JpaSpecificationExecutor<EmployeeInfo> {

	//List<EmployeeInfo> findByPassportNoAndRequestReferenceNo(String passportNo, String requestReferenceNo);

	List<EmployeeInfo> findByPassportNo(String passportNo);

	Optional<EmployeeInfo> findByPassportNoAndCustomerReferenceNoAndVisaNo(String passportNo, String customerReferenceNo,String visaNo);

	List<EmployeeInfo> findByPassportNoAndCustomerReferenceNo(String passportNo, String customerReferenceNo);

	List<EmployeeInfo> findByRequestReferenceNo(String requestReferenceNo);

	List<EmployeeInfo> findByRequestReferenceNoOrderByEmpIdDesc(String requestReferenceNo);
	
	@Query("SELECT MAX(e.empId) FROM EmployeeInfo e")
	Long findMaxEmpId();

}
