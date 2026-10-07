package com.maan.eway.document.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.maan.eway.document.ai.bean.ImageDetails;

public interface ImageDetailsRepository extends JpaRepository<ImageDetails, Long>{

	@Query(value = "SELECT MAX(transaction_id) FROM image_details", nativeQuery = true)
	Long findMaxTransactionId();

	ImageDetails getByUniqueId(Integer valueOf);

	//List<ImageDetails> findByQuoteNo(String quoteNo);

}
