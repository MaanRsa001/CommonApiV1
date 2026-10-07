package com.maan.eway.bean;


import java.io.Serializable;
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




/**
* Domain class for entity "RenewalTransactionDetails"
*
* @author Telosys Tools Generator
*
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name="renewal_transaction_details")


public class RenewalTransactionDetails implements Serializable {
 
private static final long serialVersionUID = 1L;
 
	@Id
	@Column(name="TRAN_ID", nullable=false)   //--- ENTITY PRIMARY KEY 
	private String    tranId ;
	
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="REQUEST_TIME", nullable=false)
    private Date     requestTime ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RESPONSE_TIME", nullable=false)
    private Date       responseTime ;
 
    @Column(name="STATUS", length=100)
    private String     status ;

    @Column(name="REMARKS")
    private String    remarks ;


}

