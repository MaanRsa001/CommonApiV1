package com.maan.eway.ticket;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.maan.eway.bean.BuildingDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name="ticket_job_execution_tracker")
public class TicketJobExecutionTracker {
	
	@Id
    @Column(name = "JOB_NAME")
    private String jobName;

    @Column(name = "LAST_RUN_TIME")
    private LocalDateTime lastRunTime;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}
