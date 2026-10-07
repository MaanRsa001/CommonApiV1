package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
@Table(name="rsta_push_details")
public class RSTAPushDetails implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "SNO",nullable = false)
	private BigDecimal sno;
	
	@Column(name = "QUOTE_NO")
	private String quoteNo;
	
	@Column(name = "POLICY_NO")
	private String policyNo;
	
	@Column(name = "RSTA_REQUEST")
	private String rstaRequest;
	
	@Column(name = "RSTA_RESPONSE")
	private String rstaResponse;
	
	@Column(name = "RSTA_RESPONSE_CODE")
	private String rstaResponseCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUEST_TIME")
	private Date requestTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "RESPONSE_TIME")
	private Date responseTime	;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;

}
