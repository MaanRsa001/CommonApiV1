package com.maan.eway.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "push_integration_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushIntegrationStatus {

	   @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "push_id")
	    private Long pushId;

	    @Column(name = "quotation_policy_no", length = 255)
	    private String quotationPolicyNo;

	    @Column(name = "status", length = 200)
	    private String status;

	    @Column(name = "error_message", columnDefinition = "TEXT")
	    private String errorMessage;
}