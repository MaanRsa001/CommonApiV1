package com.maan.eway.promoCode.repository;

import com.maan.eway.promoCode.entity.TblPromoCodeHeader;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromoCodeHeaderRepository extends JpaRepository<TblPromoCodeHeader, Long> {

	// Find the latest active record for a given master constraint
	@Query("SELECT h FROM TblPromoCodeHeader h WHERE h.companyId = :companyId "
			+ "AND h.productId = :productId AND h.promoCode = :promoCode "
			+ "AND h.status = 'Y' ORDER BY h.amendId DESC")
	Optional<TblPromoCodeHeader> findActiveHeader(@Param("companyId") Integer companyId,
			@Param("productId") Integer productId, @Param("promoCode") String promoCode);

	// Find the maximum AMEND_ID for a master constraint
	@Query("SELECT COALESCE(MAX(h.amendId), -1) FROM TblPromoCodeHeader h "
			+ "WHERE h.companyId = :companyId AND h.productId = :productId " + "AND h.promoCode = :promoCode")
	Integer findMaxAmendId(@Param("companyId") Integer companyId, @Param("productId") Integer productId,
			@Param("promoCode") String promoCode);

	// Deactivate the existing active record (set STATUS = 'N' and end date to
	// today)
	@Modifying
	@Transactional
	@Query("""
	    UPDATE TblPromoCodeHeader h
	       SET h.status = 'N',
	           h.effectiveEndDate = CURRENT_TIMESTAMP,
	           h.updatedDate = CURRENT_TIMESTAMP
	     WHERE h.companyId = :companyId
	       AND h.productId = :productId
	       AND h.promoCode = :promoCode
	       AND h.status = 'Y'
	""")
	void deactivateActiveHeader(
	        @Param("companyId") Integer companyId,
	        @Param("productId") Integer productId,
	        @Param("promoCode") String promoCode
	);

	// Check if a specific amend level already exists (duplicate-guard)
	boolean existsByCompanyIdAndProductIdAndPromoCodeAndAmendId(Integer companyId, Integer productId, String promoCode,
			Integer amendId);

	// Find all records for a master constraint (history view)
	List<TblPromoCodeHeader> findByCompanyIdAndProductIdAndPromoCodeOrderByAmendIdAsc(Integer companyId,
			Integer productId, String promoCode);
	
	@Query("""
		    SELECT h
		    FROM TblPromoCodeHeader h
		    WHERE h.companyId = :companyId
		      AND h.amendId = (
		          SELECT MAX(h2.amendId)
		          FROM TblPromoCodeHeader h2
		          WHERE h2.companyId = h.companyId
		            AND h2.productId = h.productId
		            AND h2.promoCode = h.promoCode
		      )
		    ORDER BY h.promoCode ASC
		    """)
		List<TblPromoCodeHeader> findLatestByCompanyId(
		        @Param("companyId") Integer companyId);
}