package com.maan.eway.oracle.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.maan.eway.oracle.YiSectionDetailIdOra;
import com.maan.eway.oracle.YiSectionDetailOra;

public interface YiSectionDetailOraRepository
		extends JpaRepositoryImplementation<YiSectionDetailOra, YiSectionDetailIdOra>,
		JpaSpecificationExecutor<YiSectionDetailOra> {

	List<YiSectionDetailOra> findByQuotationPolicyNo(String quotationPolicyNo);

}
