package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maan.eway.bean.EwayLobMaster;
import com.maan.eway.bean.EwayLobMasterId;

public interface EwayLobMasterRepository extends JpaRepository<EwayLobMaster,EwayLobMasterId > , JpaSpecificationExecutor<EwayLobMaster> {
	
	@Query(value = "SELECT * FROM eway_line_of_buisness e " + "WHERE e.company_id = :companyId "
			+ "AND FIND_IN_SET(:productId, e.product_id)", nativeQuery = true)
	EwayLobMaster findByCompnayIdAndProductId(@Param("companyId") String companyId,
			@Param("productId") String productId);

	List<EwayLobMaster> findByCompnayId(String string);

}
