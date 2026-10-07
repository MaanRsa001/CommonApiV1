package com.maan.eway.excelupload.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.excelupload.bean.UploadedRecord;

@Repository
public interface UploadedRecordRepository extends JpaRepository<UploadedRecord, Long> {
	List<UploadedRecord> findByRequestRefNo(String requestRefNo);
}
