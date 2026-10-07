package com.maan.eway.thirdparty.Mapfre.bean;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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

@IdClass(MapfreIntegrationLogId.class)
@Table(name = "mapfre_integration_log")
public class MapfreIntegrationLog {
	
	@Id
	@Column(name = "QUOTE_NO", nullable = false, length = 20)
    private String quoteNo;
	
	@Id
	@Column(name = "REQUEST_REFERENCE_NO", nullable = false, length = 20)
    private String requestRefNo;

	@Column(name = "MAPFRE_REQUEST")
    private String mapfreRequest;
	
	@Column(name = "MAPFRE_RESPONSE")
    private String mapfreResponse;
	
	@Column(name = "REQUEST_TIME")
    private LocalDateTime requestTime;
	
	@Column(name = "RESPONSE_TIME")
    private LocalDateTime responseTime;

    @Enumerated(EnumType.STRING)
    private ResponseStatus status; // SUCCESS, FAILED, PENDING
    
    @Column(name = "POLICY_NO")
    private String policyNo;
    
    @Column(name = "PDF_LINK")
    private String pdfLink;

    public enum ResponseStatus {
        SUCCESS, FAILED, PENDING
    }

}
