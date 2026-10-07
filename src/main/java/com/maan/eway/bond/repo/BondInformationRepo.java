package com.maan.eway.bond.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bond.entity.BondInformation;
@Repository
public interface BondInformationRepo extends JpaRepository<BondInformation, String>{

	BondInformation findByCompanyIdAndQuoteNoAndProductId(String companyId, String quoteNo, String productId);

	Optional<BondInformation> findByQuoteNo(String quoteNo);

}
