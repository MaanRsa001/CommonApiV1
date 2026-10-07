package com.maan.eway.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.EserviceCustomerLocation;
@Repository
public interface EserviceCustomerLocationRepository extends JpaRepository<EserviceCustomerLocation,Long> {
	List<EserviceCustomerLocation> findByCustomerReferenceNo(String customerRefNo);
 
    Optional<EserviceCustomerLocation> findByLocationName(String locationName);
}