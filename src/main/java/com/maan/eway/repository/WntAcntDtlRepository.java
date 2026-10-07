package com.maan.eway.repository;


import com.maan.eway.bean.WntAcntDtl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WntAcntDtlRepository extends
        JpaRepository<WntAcntDtl, Long>,
        JpaSpecificationExecutor<WntAcntDtl> {

    @Query(value = "SELECT * FROM wnt_acnt_dtl " +
            "WHERE (ad_pol_no = :policyNumber OR ad_end_no = :policyNumber) " +
            "AND ad_acnt_sub_type = :subType " +
            "AND ad_acnt_type = :accountType " +
            "AND ad_inst_no = :installmentNo " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<WntAcntDtl> findCustomerPremiumNative(
            @Param("policyNumber") String policyNumber,
            @Param("subType") String subType,
            @Param("accountType") String accountType,
            @Param("installmentNo") Integer installmentNo);

    @Query(value = "SELECT * FROM wnt_acnt_dtl " +
            "WHERE (ad_pol_no = :policyNumber OR ad_end_no = :policyNumber) " +
            "AND ad_acnt_type = :accountType " +
            "AND ad_inst_no = :installmentNo " +
            "ORDER BY ad_sys_id DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<WntAcntDtl> findReceiptAccount(
            @Param("policyNumber") String policyNumber,
            @Param("accountType") String accountType,
            @Param("installmentNo") Integer installmentNo);

}

