package com.maan.eway.viewAll.entity;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FieldQueryTableQueryRepository extends JpaRepository<FieldQueryTableQuery, BigDecimal> {

	FieldQueryTableQuery findByQueryId(BigDecimal queryId);
	
	@Query("SELECT MAX(f.queryId) FROM FieldQueryTableQuery f")
	BigDecimal findMaxQueryId();

	FieldQueryTableQuery findByProductTypeAndPdfYn(String motorYn, String string);

}
