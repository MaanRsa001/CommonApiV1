package com.maan.eway.bean;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maan.eway.workflow.util.LocalDateTimeTypeAdapter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="premia_transaction_log")

public class PremiaTransactionLog {

	@Override
	public String toString() {
		 Gson gson = new GsonBuilder() .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter()) .create();
		return gson.toJson(this);
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sno;

    @Column(nullable = false)
    private LocalDateTime requestTime;

    @Column(nullable = false)
    private LocalDateTime responseTime;

    @Column(nullable = false)
    private Date entryDate;

    @Column(nullable = false)
    private String endpoint;

    @Lob
    private String request;

    @Lob
    private String response;

    @Column(nullable = false)
    private String status;

    @Lob
    private String errorMessage;

    @Column(nullable = false)
    private String QuoteNo;

    @Lob
    private String generateReq;
    
}
