package com.maan.eway.repository;

import com.maan.eway.bean.WnmUwAccountSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WnmUwAccountSetupRepository extends
        JpaRepository<WnmUwAccountSetup, Long>,
        JpaSpecificationExecutor<WnmUwAccountSetup> {

    /**
     * Find all records with ALL same field values
     * EXCEPT: pasSysId, createdBy, createdDate, updatedBy, updatedDate, effectiveFm, effectiveTo
     * Returns list so we can check if any are active (not expired)
     */
    List<WnmUwAccountSetup> findByAcntTypeAndAcntSubTypeAndAdCustRvPvYnAndAdAutomatchYnAndCompanyCodeFmAndCompanyCodeToAndDivisionCodeFmAndDivisionCodeToAndDepartmentCodeFmAndDepartmentCodeToAndBussinessTypeFmAndBussinessTypeToAndSrcTypeFmAndSrcTypeToAndProductCodeFmAndProductCodeToAndSectionCodeFmAndSectionCodeToAndCoverCodeFmAndCoverCodeToAndChargeCodeFmAndChargeCodeToAndVatCodeFmAndVatCodeToAndSrcCodeFmAndSrcCodeToAndCustCatgAndInstruCodeFmAndInstruCodeToAndMainAcntCodeAndSubAcntCodeAndAcntCompCodeAndAcntDivnCodeAndAcntDeptCodeAndAcntNarrationAndReplaceText1AndReplaceText2AndReplaceText3AndReplaceText4AndReplaceText5AndReplaceText6AndReplaceText7AndReplaceText8AndReplaceText9AndReplaceText10AndDrcrFlagAndTransactionCodeAndCompEntryYnAndPostOnlineYnAndActiveStatusYnAndCompanyId(
            String acntType,
            String acntSubType,
            String adCustRvPvYn,
            String adAutomatchYn,
            String companyCodeFm,
            String companyCodeTo,
            String divisionCodeFm,
            String divisionCodeTo,
            String departmentCodeFm,
            String departmentCodeTo,
            String bussinessTypeFm,
            String bussinessTypeTo,
            String srcTypeFm,
            String srcTypeTo,
            String productCodeFm,
            String productCodeTo,
            String sectionCodeFm,
            String sectionCodeTo,
            String coverCodeFm,
            String coverCodeTo,
            String chargeCodeFm,
            String chargeCodeTo,
            String vatCodeFm,
            String vatCodeTo,
            String srcCodeFm,
            String srcCodeTo,
            String custCatg,
            String instruCodeFm,
            String instruCodeTo,
            String mainAcntCode,
            String subAcntCode,
            String acntCompCode,
            String acntDivnCode,
            String acntDeptCode,
            String acntNarration,
            String replaceText1,
            String replaceText2,
            String replaceText3,
            String replaceText4,
            String replaceText5,
            String replaceText6,
            String replaceText7,
            String replaceText8,
            String replaceText9,
            String replaceText10,
            String drcrFlag,
            String transactionCode,
            Integer compEntryYn,
            Integer postOnlineYn,
            String activeStatusYn,
            String companyId
    );

    /**
     * Find all records with ALL same field values excluding current record
     * EXCEPT: pasSysId, createdBy, createdDate, updatedBy, updatedDate, effectiveFm, effectiveTo
     * Returns list so we can check if any are active (not expired)
     */
    List<WnmUwAccountSetup> findByAcntTypeAndAcntSubTypeAndAdCustRvPvYnAndAdAutomatchYnAndCompanyCodeFmAndCompanyCodeToAndDivisionCodeFmAndDivisionCodeToAndDepartmentCodeFmAndDepartmentCodeToAndBussinessTypeFmAndBussinessTypeToAndSrcTypeFmAndSrcTypeToAndProductCodeFmAndProductCodeToAndSectionCodeFmAndSectionCodeToAndCoverCodeFmAndCoverCodeToAndChargeCodeFmAndChargeCodeToAndVatCodeFmAndVatCodeToAndSrcCodeFmAndSrcCodeToAndCustCatgAndInstruCodeFmAndInstruCodeToAndMainAcntCodeAndSubAcntCodeAndAcntCompCodeAndAcntDivnCodeAndAcntDeptCodeAndAcntNarrationAndReplaceText1AndReplaceText2AndReplaceText3AndReplaceText4AndReplaceText5AndReplaceText6AndReplaceText7AndReplaceText8AndReplaceText9AndReplaceText10AndDrcrFlagAndTransactionCodeAndCompEntryYnAndPostOnlineYnAndActiveStatusYnAndCompanyIdAndPasSysIdNot(
            String acntType,
            String acntSubType,
            String adCustRvPvYn,
            String adAutomatchYn,
            String companyCodeFm,
            String companyCodeTo,
            String divisionCodeFm,
            String divisionCodeTo,
            String departmentCodeFm,
            String departmentCodeTo,
            String bussinessTypeFm,
            String bussinessTypeTo,
            String srcTypeFm,
            String srcTypeTo,
            String productCodeFm,
            String productCodeTo,
            String sectionCodeFm,
            String sectionCodeTo,
            String coverCodeFm,
            String coverCodeTo,
            String chargeCodeFm,
            String chargeCodeTo,
            String vatCodeFm,
            String vatCodeTo,
            String srcCodeFm,
            String srcCodeTo,
            String custCatg,
            String instruCodeFm,
            String instruCodeTo,
            String mainAcntCode,
            String subAcntCode,
            String acntCompCode,
            String acntDivnCode,
            String acntDeptCode,
            String acntNarration,
            String replaceText1,
            String replaceText2,
            String replaceText3,
            String replaceText4,
            String replaceText5,
            String replaceText6,
            String replaceText7,
            String replaceText8,
            String replaceText9,
            String replaceText10,
            String drcrFlag,
            String transactionCode,
            Integer compEntryYn,
            Integer postOnlineYn,
            String activeStatusYn,
            String companyId,
            Long pasSysId
    );


    @Query(value = "SELECT DISTINCT COVER_ID, COVER_NAME " +
            "FROM section_cover_master WHERE " +
            "product_id >= :minProductId AND product_id <= :maxProductId AND " +
            "company_id = :companyId AND " +
            "CURDATE() BETWEEN effective_date_start AND effective_date_end",
            nativeQuery = true)
    List<Object[]> findDistinctCoversByProductRangeAndCompany(
            @Param("minProductId") Long minProductId,
            @Param("maxProductId") Long maxProductId,
            @Param("companyId") Long companyId
    );


    @Query(value = "SELECT DISTINCT section_id, section_name " +
            "FROM product_section_master WHERE " +
            "product_id >= :minProductId AND product_id <= :maxProductId AND " +
            "company_id = :companyId AND " +
            "CURDATE() BETWEEN effective_date_start AND effective_date_end",
            nativeQuery = true)
    List<Object[]> findDistinctSectionsByProductRangeAndCompany(
            @Param("minProductId") Long minProductId,
            @Param("maxProductId") Long maxProductId,
            @Param("companyId") Long companyId
    );

    @Query(value = """
                SELECT 
                    narration_desc,
                    narration_replace1,
                    narration_replace2,
                    narration_replace3,
                    narration_replace4,
                    narration_replace5,
                    narration_replace6,
                    narration_replace7,
                    narration_replace8,
                    narration_replace9,
                    narration_replace10
                FROM wnm_accounting_narration
                WHERE narration_acnt_type = :acntType
                  AND narration_acnt_sub_type = :acntsubType
            """, nativeQuery = true)
    List<Object[]> getNarrationDetails(
            @Param("acntType") String acntType,
            @Param("acntsubType") String acntsubType
    );

}