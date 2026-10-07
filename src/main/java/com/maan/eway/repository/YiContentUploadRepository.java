package com.maan.eway.repository;

import com.maan.eway.bean.YiContentUpload;
import com.maan.eway.bean.YiSmiDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YiContentUploadRepository
        extends JpaRepository<YiContentUpload, Long> {
    List<YiContentUpload> findByQuotationPolicyNo(String policyNo);
}
