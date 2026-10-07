package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.maan.eway.bean.YiVatDetail;

public interface YiVatDetailRepository extends JpaRepositoryImplementation<YiVatDetail, String>{

	List<com.maan.eway.bean.YiVatDetail> findByQuotationPolicyNo(String policyNo);

	List<YiVatDetail> findByRequestreferenceno(String reqRefNo);

}
