package com.maan.eway.oracle.repo;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.maan.eway.oracle.YiVatDetailOra;

public interface YiVatDetailOraRepository extends JpaRepositoryImplementation<YiVatDetailOra, String>{

	List<YiVatDetailOra> findByQuotationPolicyNo(String quotationPolicyNo);

}
