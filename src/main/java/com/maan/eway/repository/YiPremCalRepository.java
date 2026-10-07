package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.maan.eway.bean.YiPremCal;
import com.maan.eway.bean.YiPremCalId;

public interface YiPremCalRepository extends JpaRepositoryImplementation<YiPremCal, YiPremCalId>{

	List<com.maan.eway.bean.YiPremCal> findByQuotationPolicyNo(String policyNo);

	List<YiPremCal> findByRequestreferenceno(String reqRefNo);

}
