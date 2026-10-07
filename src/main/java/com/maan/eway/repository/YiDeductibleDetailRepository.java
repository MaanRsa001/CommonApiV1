package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.YiDeductableDetailId;
import com.maan.eway.bean.YiDeductibleDetail;

public interface YiDeductibleDetailRepository extends JpaRepository<YiDeductibleDetail,YiDeductableDetailId > , JpaSpecificationExecutor<YiDeductibleDetail> {

	List<YiDeductibleDetail> findByQuotationPolicyNo(String policyNo);


	

}
