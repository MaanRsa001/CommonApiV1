package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@IdClass(YiConditionDetailId.class)
@Table(name="yi_condition_detail")
public class YiConditionDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 
	    @Column(name = "SERVICE_ID", length = 360)
	    private String serviceId;

	    @Column(name = "QUOTATION_POLICY_NO", length = 360)
	    private String quotationPolicyNo;

	    @Column(name = "SERVICE_ACTION", length = 540)
	    private String serviceAction;

	    @Column(name = "PROD_CODE", length = 108)
	    private String prodCode;

	    @Column(name = "SEC_CODE", length = 108)
	    private String secCode;

	    @Column(name = "RISK_ID", length = 45)
	    private String riskId;

	    @Column(name = "IDX_NO")
	    private Integer idxNo;
	    @Id
	    @Column(name = "COND_SR_NO")
	    private Integer condSrNo;

	    @Column(name = "COND_CODE", length = 36)
	    private String condCode;

	    @Column(name = "COND_DESC", columnDefinition = "TEXT")
	    private String condDesc;
	    @Id
	    @Column(name = "COND_TYPE", length = 36)
	    private String condType;
	    
	    @Id
	    @Column(name = "REQUESTREFERENCENO", length = 135)
	    private String requestReferenceNo;

	    @Column(name = "P_WS_RESPONSE_TYPE", length = 450)
	    private String PWsResponseType;

	    @Column(name = "P_WS_ERROR", length = 3000)
	    private String PWsError;

	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="REQUEST_TIME")
	    private Date       requestTime ;

	    @Temporal(TemporalType.TIMESTAMP)
	    @Column(name="RESPONSE_TIME")
	    private Date       responseTime ;

	    @Column(name = "STATUS", length = 90)
	    private String status;

}
