package com.maan.eway.thirdparty.Mapfre.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.thirdparty.Mapfre.bean.MapfreIntegrationLog;
import com.maan.eway.thirdparty.Mapfre.bean.MapfreIntegrationLogId;

public interface MapfreIntegrationLogRepository extends JpaRepository<MapfreIntegrationLog, MapfreIntegrationLogId>,JpaSpecificationExecutor<MapfreIntegrationLog>  {
    
}

