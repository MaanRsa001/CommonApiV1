package com.maan.eway.oracle.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.oracle.YiPolicyApprovalOra;

public interface YiPolicyApprovalOraRepository extends JpaRepository<YiPolicyApprovalOra,String > , JpaSpecificationExecutor<YiPolicyApprovalOra> {

	List<YiPolicyApprovalOra> findByQuotationPolicyNo(String quotationPolicyNo);

}
