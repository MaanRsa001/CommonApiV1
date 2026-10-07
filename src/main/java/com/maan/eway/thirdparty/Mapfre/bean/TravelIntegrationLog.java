package com.maan.eway.thirdparty.Mapfre.bean;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "travel_integration_log")
public class TravelIntegrationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "QUOTE_NO", nullable = false, length = 20)
    private String quoteNo;
    
    @Column(name = "REQUEST_REFERENCE_NO", nullable = false, length = 20)
    private String requestReferenceNo;

    
    @Column(name = "REQUEST_PAYLOAD")
    private String requestPayload;

    @Column(name = "RESPONSE")
    private String responsePayload;

    @Column(name = "REQUEST_TIME")
    private LocalDateTime requestTime;
    
    @Column(name = "RESPONSE_TIME")
    private LocalDateTime responseTime;

    @Enumerated(EnumType.STRING)
    private ResponseStatus status;
    
    @Column(name = "POLICY_NO")
    private String policyNumber;
    
    public enum ResponseStatus {
        SUCCESS, FAILED, PENDING
    }
}
