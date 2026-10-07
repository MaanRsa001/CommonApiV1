package com.maan.eway.repository;

import com.maan.eway.bean.YiSmiDetail;
import com.maan.eway.bean.YiSmiDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface YiSmiDetailRepository extends JpaRepository<YiSmiDetail, YiSmiDetailId>, JpaSpecificationExecutor<YiSmiDetail> {

    List<YiSmiDetail> findByQuotationPolicyNo(String policyNo);

}