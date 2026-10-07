package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.SeqPolicynoMadison;

public interface SeqPolicynoMadisonRepository extends JpaRepository<SeqPolicynoMadison,Long > , JpaSpecificationExecutor<SeqPolicynoMadison> {
  
	
	
}
