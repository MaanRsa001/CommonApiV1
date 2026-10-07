package com.maan.eway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.EwayUserMapping;

@Repository
public interface EwayUserMappingRepository extends JpaRepository<EwayUserMapping, Long> {
	Optional<EwayUserMapping> findByApproverId(String userName);
}