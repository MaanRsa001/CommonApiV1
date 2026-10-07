package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.AviationInfo;
import com.maan.eway.bean.AviationInfoId;

public interface AviationInfoRepository extends JpaRepository<AviationInfo,AviationInfoId > , JpaSpecificationExecutor<AviationInfo> {

	void deleteByProductIdAndRequestReferenceNoAndLocationIdAndSectionId(Integer productId, String requestReferenceNo,
			Integer locationId, Integer sectionId);

	List<AviationInfo> findByRequestReferenceNo(String requestRefNo);


}
