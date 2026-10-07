package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.ClaimHistoryInfo;
import com.maan.eway.bean.ClaimHistoryInfoId;

public interface ClaimHistoryInfoRepository extends JpaRepository<ClaimHistoryInfo, ClaimHistoryInfoId>{

	List<ClaimHistoryInfo> findAllByCompanyIdAndProductIdAndQuoteNoAndRequestReferenceNo(Integer companyId, Integer productId, String quoteNo, String requestReferenceNo);
}
