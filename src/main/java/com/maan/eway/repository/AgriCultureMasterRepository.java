package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.AgricultureMaster;
import com.maan.eway.bean.AgricultureMasterId;

public interface AgriCultureMasterRepository extends JpaRepository<AgricultureMaster, AgricultureMasterId>{

	List<AgricultureMaster> findByProvinceIdAndProductIdAndCompanyId(Integer provinceId, Integer productId, Integer companyId);

	List<AgricultureMaster> findByProvinceIdAndProductIdAndCompanyIdOrderByDistrictId(Integer valueOf, Integer valueOf2,
			Integer valueOf3);

	List<AgricultureMaster> findByCompanyId(Integer valueOf);

	List<AgricultureMaster> findByProvinceIdAndDistrictIdAndProductIdAndCompanyId(Integer valueOf, Integer valueOf2, Integer integer, Integer integer2);

	List<AgricultureMaster> findByCompanyIdAndProvinceDesc(Integer countryCode, String region);

	List<AgricultureMaster> findByCompanyIdAndProvinceIdAndDistrictDesc(Integer valueOf, Integer valueOf2,
			String district);

}
