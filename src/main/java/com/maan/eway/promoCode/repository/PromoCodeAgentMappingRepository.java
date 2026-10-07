package com.maan.eway.promoCode.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maan.eway.promoCode.entity.TblPromoCodeAgentMapping;

public interface PromoCodeAgentMappingRepository
        extends JpaRepository<TblPromoCodeAgentMapping, Long> {

	 @Query("SELECT a FROM TblPromoCodeAgentMapping a WHERE a.promoCode = :promoCode " +
	           "AND a.agencyCode = :agencyCode AND a.companyId = :companyId " +
	           "AND a.productId = :productId AND a.sectionId = :sectionId " +
	           "AND a.typeOfBusiness = :typeOfBusiness AND a.status = 'Y'")
	    Optional<TblPromoCodeAgentMapping> findActiveAgentMapping(
	            @Param("promoCode") String promoCode,
	            @Param("agencyCode") String agencyCode,
	            @Param("companyId") Integer companyId,
	            @Param("productId") Integer productId,
	            @Param("sectionId") Integer sectionId,
	            @Param("typeOfBusiness") String typeOfBusiness);
	 
	    // Find the maximum AMEND_ID for an agent mapping constraint
	    @Query("SELECT COALESCE(MAX(a.amendId), -1) FROM TblPromoCodeAgentMapping a " +
	           "WHERE a.promoCode = :promoCode AND a.agencyCode = :agencyCode " +
	           "AND a.companyId = :companyId AND a.productId = :productId " +
	           "AND a.sectionId = :sectionId AND a.typeOfBusiness = :typeOfBusiness")
	    Integer findMaxAmendId(
	            @Param("promoCode") String promoCode,
	            @Param("agencyCode") String agencyCode,
	            @Param("companyId") Integer companyId,
	            @Param("productId") Integer productId,
	            @Param("sectionId") Integer sectionId,
	            @Param("typeOfBusiness") String typeOfBusiness);
	 
	    // Deactivate active agent mappings under a given mappingId + agencyCode
	    @Modifying
	    @Query("UPDATE TblPromoCodeAgentMapping a SET a.status = 'N', a.effectiveEndDate = CURRENT_TIMESTAMP, " +
	           "a.updatedDate = CURRENT_TIMESTAMP " +
	           "WHERE a.promoCode = :promoCode AND a.agencyCode = :agencyCode " +
	           "AND a.companyId = :companyId AND a.productId = :productId " +
	           "AND a.sectionId = :sectionId AND a.typeOfBusiness = :typeOfBusiness AND a.status = 'Y'")
	    void deactivateActiveAgentMapping(
	            @Param("promoCode") String promoCode,
	            @Param("agencyCode") String agencyCode,
	            @Param("companyId") Integer companyId,
	            @Param("productId") Integer productId,
	            @Param("sectionId") Integer sectionId,
	            @Param("typeOfBusiness") String typeOfBusiness);
	 
	    // Fetch all active agents under a mappingId (used for history checks)
	    List<TblPromoCodeAgentMapping> findByMappingIdAndStatus(Long mappingId, String status);
	 
	    // Duplicate guard at specific amend level
	    boolean existsByPromoCodeAndAgencyCodeAndCompanyIdAndProductIdAndSectionIdAndTypeOfBusiness(
	            String promoCode, String agencyCode, Integer companyId,
	            Integer productId, Integer sectionId, String typeOfBusiness);
	    
	    @Query("""
	    	    SELECT a
	    	    FROM TblPromoCodeAgentMapping a
	    	    WHERE a.productId = :productId
	    	      AND a.companyId = :companyId
	    	      AND (:sectionId IS NULL OR a.sectionId = :sectionId)
	    	      AND (:promoCode IS NULL OR a.promoCode = :promoCode)
	    	      AND (:type IS NULL OR a.type = :type)
	    		  AND (:discType IS NULL OR a.discType = :discType)
                  AND (:typeOfBusiness IS NULL OR a.typeOfBusiness = :typeOfBusiness)
                  AND (:userType IS NULL OR a.userType = :userType)
	    	      AND a.amendId = (
	    	          SELECT MAX(a2.amendId)
	    	          FROM TblPromoCodeAgentMapping a2
	    	          WHERE a2.productId = a.productId
	    	            AND a2.companyId = a.companyId
	    	            AND (:sectionId IS NULL OR a2.sectionId = :sectionId)
	    	            AND (:promoCode IS NULL OR a2.promoCode = :promoCode)
	    	            AND (:type IS NULL OR a2.type = :type)
                        AND (:discType IS NULL OR a2.discType = :discType)
                        AND (:typeOfBusiness IS NULL OR a2.typeOfBusiness = :typeOfBusiness)
                        AND (:userType IS NULL OR a2.userType = :userType)
	    	      )
	    	    ORDER BY a.promoCode ASC
	    	    """)
	    	List<TblPromoCodeAgentMapping> findLatestByProductIdAndCompanyIdAndSectionIdAndPromoCodeAndTypeAndDiscTypeAndTypeOfBusinessAndUserType(
	    	        @Param("productId") Integer productId,
	    	        @Param("companyId") Integer companyId,
	    	        @Param("sectionId") Integer sectionId,
	    	        @Param("promoCode") String promoCode,
	    	        @Param("type") String type,
	    	        @Param("discType") String discType,
	    	        @Param("typeOfBusiness") String typeOfBusiness,
	    	        @Param("userType") String userType);
	               
}