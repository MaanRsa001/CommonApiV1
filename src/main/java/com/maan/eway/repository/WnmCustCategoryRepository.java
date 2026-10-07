package com.maan.eway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.WnmCustCategory;

public interface WnmCustCategoryRepository extends JpaRepository<WnmCustCategory, String> {

	Optional<WnmCustCategory> findByCcCustCatgCode(String ccCustCatgCode);
}
