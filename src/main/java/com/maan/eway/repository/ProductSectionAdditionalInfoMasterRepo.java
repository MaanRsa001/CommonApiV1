package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.ProductSectionAdditionalInfoMaster;
import com.maan.eway.bean.ProductSectionAdditionalInfoMasterId;

public interface ProductSectionAdditionalInfoMasterRepo extends JpaRepository< ProductSectionAdditionalInfoMaster,  ProductSectionAdditionalInfoMasterId>{

	List<ProductSectionAdditionalInfoMaster> findByProductIdAndSectionIdAndCompanyId(Integer valueOf, Integer valueOf2,
			String companyId);

	List<ProductSectionAdditionalInfoMaster> findByProductIdAndCompanyId(Integer valueOf, String companyId);

	List<ProductSectionAdditionalInfoMaster> findByProductIdAndSectionIdAndCompanyIdOrderByAmendIdDesc(Integer valueOf,
			Integer valueOf2, String companyId);

}
