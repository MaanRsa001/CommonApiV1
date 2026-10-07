package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.MarineHullInfo;
import com.maan.eway.bean.MarineHullInfoId;

@Repository
public interface MarineHullInfoRepo extends JpaRepository<MarineHullInfo, MarineHullInfoId>, JpaSpecificationExecutor<MarineHullInfo>  {


	List<MarineHullInfo> findByRequestReferenceNo(String requestReferenceno);

	void deleteByProductIdAndRequestReferenceNoAndLocationIdAndSectionId(Integer productid, String requestReferenceNo,
			Integer locationId, Integer sectionId);

}

