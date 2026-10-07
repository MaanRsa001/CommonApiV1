package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.YiPolicyApproval;

public interface YiPolicyApprovalRepository extends JpaRepository<YiPolicyApproval,String > , JpaSpecificationExecutor<YiPolicyApproval> {


	List<com.maan.eway.bean.YiPolicyApproval> findByQuotationPolicyNo(String policyNo);

	List<YiPolicyApproval> findByRequestreferenceno(String reqRefNo);

}
