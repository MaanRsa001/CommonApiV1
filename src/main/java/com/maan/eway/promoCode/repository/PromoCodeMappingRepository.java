package com.maan.eway.promoCode.repository;

import com.maan.eway.promoCode.entity.PromoCodeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromoCodeMappingRepository extends JpaRepository<PromoCodeMapping, Long> {

	// Find the latest active mapping for a master constraint
	@Query("SELECT m FROM PromoCodeMapping m WHERE m.productId = :productId "
			+ "AND m.sectionId = :sectionId AND m.typeOfBusiness = :typeOfBusiness "
			+ "AND m.status = 'Y' ORDER BY m.amendId DESC")
	Optional<PromoCodeMapping> findActiveMapping(@Param("productId") Integer productId,
			@Param("sectionId") Integer sectionId, @Param("typeOfBusiness") String typeOfBusiness);

	// Find the maximum AMEND_ID for a mapping constraint
	@Query("""
			SELECT COALESCE(MAX(m.amendId), -1)
			FROM PromoCodeMapping m
			WHERE m.productId = :productId
			  AND m.sectionId = :sectionId
			  AND m.typeOfBusiness = :typeOfBusiness
			  AND m.companyId = :companyId
			""")
	Integer findMaxAmendId(@Param("productId") Integer productId, @Param("sectionId") Integer sectionId,
			@Param("typeOfBusiness") String typeOfBusiness, @Param("companyId") Integer companyId);

	// Deactivate all active mappings under a given promoId
	@Modifying
	@Query("""
			UPDATE PromoCodeMapping m
			SET m.status = 'N',
			    m.effectiveEndDate = CURRENT_TIMESTAMP,
			    m.updatedDate = CURRENT_TIMESTAMP
			WHERE m.productId = :productId
			  AND m.sectionId = :sectionId
			  AND m.typeOfBusiness = :typeOfBusiness
			  AND m.companyId = :companyId
			  AND m.status = 'Y'
			""")
	void deactivateActiveMapping(@Param("productId") Integer productId, @Param("sectionId") Integer sectionId,
			@Param("typeOfBusiness") String typeOfBusiness, @Param("companyId") Integer companyId);

	// Find all active mappings for a given promoId (used during amendment to
	// replicate children)
	List<PromoCodeMapping> findByPromoIdAndStatus(Long promoId, String status);

	// Check if a specific amend level already exists (duplicate-guard)
	boolean existsByProductIdAndSectionIdAndTypeOfBusinessAndAmendId(Integer productId, Integer sectionId,
			String typeOfBusiness, Integer amendId);

	@Query("""
		    SELECT m
		    FROM PromoCodeMapping m
		    JOIN TblPromoCodeHeader h ON m.promoId = h.promoId
		    WHERE m.promoCode = :promoCode
		      AND h.companyId = :companyId
		      AND m.channelType = :channelType
		      AND m.amendId = (
		          SELECT MAX(m2.amendId)
		          FROM PromoCodeMapping m2
		          JOIN TblPromoCodeHeader h2 ON m2.promoId = h2.promoId
		          WHERE m2.promoCode = m.promoCode
		            AND h2.companyId = h.companyId
		            AND m2.productId = m.productId
		            AND m2.channelType = :channelType
		      )
		    ORDER BY m.productId ASC
		    """)
		List<PromoCodeMapping> findLatestByPromoCodeAndCompanyIdAndChannelType(
		        @Param("promoCode") String promoCode,
		        @Param("companyId") Integer companyId,
		        @Param("channelType") String channelType);
}