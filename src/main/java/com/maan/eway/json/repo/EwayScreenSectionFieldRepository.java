package com.maan.eway.json.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.json.entity.EwayScreenSectionField;


@Repository
public interface EwayScreenSectionFieldRepository extends JpaRepository<EwayScreenSectionField, Integer> {

	Optional<EwayScreenSectionField> findBySnoAndCompanyIdAndProductIdAndSectionId(Integer sno, String companyId,
			String productId, String sectionId);

	List<EwayScreenSectionField> findByCompanyIdAndProductIdAndSectionId(String companyId, String productId,
			String sectionId);
}
