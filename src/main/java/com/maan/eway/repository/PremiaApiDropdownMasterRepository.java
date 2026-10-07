package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.PremiaApiDropdownMaster;
import com.maan.eway.bean.PremiaApiDropdownMasterId;

public interface PremiaApiDropdownMasterRepository  extends JpaRepository<PremiaApiDropdownMaster,PremiaApiDropdownMasterId > , JpaSpecificationExecutor<PremiaApiDropdownMaster> {
 
	//EaglePremiaIntegration findBycompanyIdAnditemTypeAnditemId(Integer company,String itemtype,Integer itemid);
	PremiaApiDropdownMaster findByCompanyIdAndItemTypeAndItemId(Integer companyId, String itemType, Integer itemId);

	List<PremiaApiDropdownMaster> findByCompanyIdAndItemType(Integer companyId, String string);

	List<PremiaApiDropdownMaster> findByCompanyIdAndItemTypeOrderByItemId(Integer companyId, String string);

}
