package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.ModuleMaster;

@Repository
public interface ModuleMasterRepo extends JpaRepository<ModuleMaster, Integer>{

	List<ModuleMaster> findByCompanyIdAndStatus(String companyId, String string);

	List<ModuleMaster> findByCompanyIdAndStatusAndModuleIdIn(String companyId, String string, List<String> moduleIds);

	ModuleMaster findByCompanyIdAndModuleId(String string, Integer moduleId);

}
