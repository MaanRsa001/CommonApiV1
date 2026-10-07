package com.maan.eway.chartaccount;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartAccountChildMasterRepository extends JpaRepository<ChartAccountChildMaster, ChartAccountChildMasterId> {

	List<ChartAccountChildMaster> findByIdCompanyIdAndIdProductIdAndIdSectionIdAndIdChartId(Integer companyId,
			Integer productId, Integer sectionId, Integer chartId);

	List<ChartAccountChildMaster> findByIdCompanyIdAndIdProductIdAndIdSectionIdAndIdChartIdAndIdCoverId(Integer valueOf,
			Integer valueOf2, Integer valueOf3, Integer valueOf4, Integer valueOf5);



}
