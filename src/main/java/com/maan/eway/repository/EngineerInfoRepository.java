package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.EngineerInfo;
import com.maan.eway.bean.EngineerInfoId;

public interface EngineerInfoRepository extends JpaRepository<EngineerInfo, EngineerInfoId>, JpaSpecificationExecutor<EngineerInfo> {

	List<EngineerInfo> findByQuoteNo(String quoteNo);
    List<EngineerInfo> findByRequestReferenceNoAndProductid(String requestReferenceNo, Integer valueOf);
	List<EngineerInfo> findByRequestReferenceNo(String requestReferenceNo);
	EngineerInfo findByRequestReferenceNoAndSectionIdAndLocationId(String requestReferenceNo, String sectionId, Integer i);


}
