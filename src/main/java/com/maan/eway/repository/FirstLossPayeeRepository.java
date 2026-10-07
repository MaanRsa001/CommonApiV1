package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.FirstLossPayee;
import com.maan.eway.bean.FirstLossPayeeId;

public interface FirstLossPayeeRepository extends JpaRepository<FirstLossPayee,FirstLossPayeeId> {

	List<FirstLossPayee> findByRequestReferenceNo(String requestReferenceNo);

	List<FirstLossPayee> findByRequestReferenceNoAndLocationId(String requestReferenceNo, Integer valueOf);

	  List<FirstLossPayee>  findByRequestReferenceNoAndLocationIdAndSectionId(String requestReferenceNo, Integer loc,
			String sectionId);

}
