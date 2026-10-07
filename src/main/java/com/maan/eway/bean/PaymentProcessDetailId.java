package com.maan.eway.bean;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentProcessDetailId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String quoteNo;
	
	private String paymentId;

}
