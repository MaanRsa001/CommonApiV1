package com.maan.eway.preinspection;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;

@Entity
@Table(name = "PREINSPECTION_API_TXN")
@Data
public class PreinspectionApiTxn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TXN_ID")
    private Long txnId;

    @Column(name = "QUOTE_NO", nullable = false)
    private String quoteNo;
    
    @Column(name = "API_URL")
    private String apiUrl;

    @Lob
    @Column(name = "REQUEST_PAYLOAD")
    private String requestPayload;

    @Lob
    @Column(name = "RESPONSE_PAYLOAD")
    private String responsePayload;

    @Column(name = "HTTP_STATUS")
    private String httpStatus;

    @Column(name = "API_STATUS")
    private String apiStatus; // SUCCESS / FAILED / ERROR

    @Column(name = "CALLED_YN")
    private String calledYn;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATE")
    private Date updatedDate;
}
