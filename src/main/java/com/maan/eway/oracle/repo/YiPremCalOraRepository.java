package com.maan.eway.oracle.repo;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.maan.eway.oracle.YiPremCalIdOra;
import com.maan.eway.oracle.YiPremCalOra;

public interface YiPremCalOraRepository extends JpaRepositoryImplementation<YiPremCalOra, YiPremCalIdOra>{

	List<YiPremCalOra> findByQuotationPolicyNo(String quotationPolicyNo);

}
