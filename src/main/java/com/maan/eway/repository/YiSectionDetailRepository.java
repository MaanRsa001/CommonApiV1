package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.maan.eway.bean.YiSectionDetail;
import com.maan.eway.bean.YiSectionDetailId;

public interface YiSectionDetailRepository extends JpaRepositoryImplementation<YiSectionDetail, YiSectionDetailId>, JpaSpecificationExecutor<YiSectionDetail> {

	List<YiSectionDetail> findByQuotationPolicyNo(String policyNo);

	List<YiSectionDetail> findByRequestreferenceno(String reqRefNo);

}
