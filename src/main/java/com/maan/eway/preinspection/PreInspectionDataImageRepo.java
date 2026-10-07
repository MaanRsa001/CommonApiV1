package com.maan.eway.preinspection;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.PreinspectionImageDetail;

public interface PreInspectionDataImageRepo extends JpaRepository<PreinspectionImageDetail, Long>{

	List<PreinspectionImageDetail> findByTranId(Long tranId);

	List<PreinspectionImageDetail> findByTranIdAndQuoteNoAndStatus(Long valueOf,String quoteNo, String string);

	List<PreinspectionImageDetail> findByTranIdAndImageName(Long valueOf, String imageName);

	List<PreinspectionImageDetail> findByQuoteNoAndStatus(String quoteNo, String string);

}
