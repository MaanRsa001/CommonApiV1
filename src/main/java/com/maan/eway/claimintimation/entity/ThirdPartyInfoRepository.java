package com.maan.eway.claimintimation.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdPartyInfoRepository extends JpaRepository<ThirdPartyInfo, Long> {

	List<ThirdPartyInfo> findByIntimationNo(String intimationNo);
}
