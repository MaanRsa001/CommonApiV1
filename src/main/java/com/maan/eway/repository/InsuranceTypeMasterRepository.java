package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.InsuranceTypeMaster;
import com.maan.eway.bean.InsuranceTypeMasterId;

public interface InsuranceTypeMasterRepository   extends JpaRepository<InsuranceTypeMaster,InsuranceTypeMasterId > , JpaSpecificationExecutor<InsuranceTypeMaster> {


	InsuranceTypeMaster findByIndsutryTypeIdAndSectionId(String indsutryid,Integer Section_Id);
	
    List<InsuranceTypeMaster> findByCompanyIdAndProductId(String CompanyId,Integer Product_id);

    List<InsuranceTypeMaster> findByIndsutryTypeIdAndStatusAndCompanyIdAndProductId(String indsutryid,String status,String CompanyId,Integer Product_id);
    List<InsuranceTypeMaster> findByCompanyIdAndProductIdAndStatus(String CompanyId,Integer Product_id,String status);

    List<InsuranceTypeMaster> findByIndsutryTypeIdAndStatus(String indsutryid,String status);
}
