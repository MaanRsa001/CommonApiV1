package com.maan.eway.claimintimation.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FnolIntimationRepository extends JpaRepository<FnolIntimation, String> {
	public void deleteAllByIntimationNo(String intimationNo);

	FnolIntimation findByIntimationNo(String intimationNo);

	List<FnolIntimation> findByPolicyNo(String policyNo);

}
