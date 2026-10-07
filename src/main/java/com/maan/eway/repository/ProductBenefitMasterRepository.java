package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ProductBenefitMaster;
import com.maan.eway.bean.ProductBenefitMasterId;


public interface ProductBenefitMasterRepository extends JpaRepository<ProductBenefitMaster, ProductBenefitMasterId>, JpaSpecificationExecutor<ProductBenefitMaster>{

	
	
}
