package com.maan.eway.thirdparty.Mapfre.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.thirdparty.Mapfre.bean.TravelIntegrationLog;


public interface TravelIntegrationLogRepository extends JpaRepository<TravelIntegrationLog, Long>,JpaSpecificationExecutor<TravelIntegrationLog> {

}
