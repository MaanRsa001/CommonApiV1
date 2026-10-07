package com.maan.eway.ticket;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketJobExecutionTrackerRepository extends JpaRepository<TicketJobExecutionTracker, String>{

	TicketJobExecutionTracker findByJobName(String type);

}
