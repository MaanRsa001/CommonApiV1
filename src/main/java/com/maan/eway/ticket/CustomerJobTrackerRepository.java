package com.maan.eway.ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJobTrackerRepository extends JpaRepository<CustomerJobTracker, CustomerJobTrackerId>{

	CustomerJobTracker findByRequestReferenceNo(String requestreferenceno);

}
