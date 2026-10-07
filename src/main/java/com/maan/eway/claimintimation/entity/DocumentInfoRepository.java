package com.maan.eway.claimintimation.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentInfoRepository extends JpaRepository<DocumentInfo, Long> {

   List<DocumentInfo> findByIntimationNo(String intimationNo);
}
