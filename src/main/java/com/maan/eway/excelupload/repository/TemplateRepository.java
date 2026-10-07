package com.maan.eway.excelupload.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.maan.eway.excelupload.bean.TemplateEntity;


@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
	
	List<TemplateEntity> findByCompanyIdAndProductIdAndSectionIdAndCoverId(String companyId,
			String productId, String sectionId, String coverId);
	
	
	 @Query("SELECT t FROM TemplateEntity t " +
	           "WHERE t.companyId = :companyId " +
	           "AND   t.productId = :productId " +
	           "AND   t.sectionId = :sectionId " +
	           "AND   t.coverId   = :coverId " +
	           "AND   t.statusTf  = 'Y' " +
	           "ORDER BY t.amendId DESC")
	    List<TemplateEntity> findActiveByKeys(
	            @Param("companyId") String companyId,
	            @Param("productId") String productId,
	            @Param("sectionId") String sectionId,
	            @Param("coverId")   String coverId);

	    // ── All versions history by business keys ─────────────────────────
	    @Query("SELECT t FROM TemplateEntity t " +
	           "WHERE t.companyId = :companyId " +
	           "AND   t.productId = :productId " +
	           "AND   t.sectionId = :sectionId " +
	           "AND   t.coverId   = :coverId " +
	           "ORDER BY t.amendId DESC")
	    List<TemplateEntity> findAllVersionsByKeys(
	            @Param("companyId") String companyId,
	            @Param("productId") String productId,
	            @Param("sectionId") String sectionId,
	            @Param("coverId")   String coverId);

	    // ── Active record — Integer sectionId/coverId (from PolicyCoverData)
	    @Query("SELECT t FROM TemplateEntity t " +
	           "WHERE t.companyId = :companyId " +
	           "AND   t.productId = :productId " +
	           "AND   t.sectionId = CAST(:sectionId AS string) " +
	           "AND   t.coverId   = CAST(:coverId   AS string) " +
	           "AND   t.statusTf  = 'Y' " +
	           "ORDER BY t.amendId DESC")
	    List<TemplateEntity> findByKey(
	            @Param("companyId") String companyId,
	            @Param("productId") String productId,
	            @Param("sectionId") Integer sectionId,
	            @Param("coverId")   Integer coverId);

	    // ── Same as findActiveByKeys — used by getTemplateByKeys service ──
	    @Query("SELECT t FROM TemplateEntity t " +
	           "WHERE t.companyId = :companyId " +
	           "AND   t.productId = :productId " +
	           "AND   t.sectionId = :sectionId " +
	           "AND   t.coverId   = :coverId " +
	           "AND   t.statusTf  = 'Y' " +
	           "ORDER BY t.amendId DESC")
	    List<TemplateEntity> findByKeys(
	            @Param("companyId") String companyId,
	            @Param("productId") String productId,
	            @Param("sectionId") String sectionId,
	            @Param("coverId")   String coverId);

	    // ── All active templates for a product ────────────────────────────
	    @Query("SELECT t FROM TemplateEntity t " +
	           "WHERE t.companyId = :companyId " +
	           "AND   t.productId = :productId " +
	           "AND   t.statusTf  = 'Y' " +
	           "ORDER BY t.sectionId, t.coverId, t.amendId DESC")
	    List<TemplateEntity> findActiveByProduct(
	            @Param("companyId") String companyId,
	            @Param("productId") String productId);

	    // ── All active templates — admin list ─────────────────────────────
	    @Query("SELECT t FROM TemplateEntity t " +
	           "WHERE t.statusTf = 'Y' " +
	           "ORDER BY t.companyId ASC, t.productId ASC")
	    List<TemplateEntity> findAllActive();
	    
	    @Query("SELECT COUNT(t) FROM TemplateEntity t " +
	    	       "WHERE t.companyId = :companyId " +
	    	       "AND   t.productId = :productId " +
	    	       "AND   t.coverId   IN :coverIds " +
	    	       "AND   t.statusTf  = 'Y'")
	    	long countTemplatesForCovers(
	    	        @Param("companyId") String       companyId,
	    	        @Param("productId") String       productId,  
	    	        @Param("coverIds")  List<String> coverIds);
	    
	    TemplateEntity findByCompanyIdAndProductIdAndSectionIdAndCoverIdAndStatusTf(String companyId,
				String productId, String sectionId, String coverId, String active);
    
    
}
