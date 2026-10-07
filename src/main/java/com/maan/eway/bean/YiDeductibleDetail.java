package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;
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
@IdClass(YiDeductableDetailId.class)
@Table(name="yi_deductible_detail")
public class YiDeductibleDetail implements Serializable {
	
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
    @Column(name = "DED_SR_NO")
    private Integer dedSrNo;

    @Column(name = "DED_CODE", length = 36)
    private String dedCode;

    @Column(name = "DED_DESC", columnDefinition = "TEXT")
    private String dedDesc;
    @Id
    @Column(name = "DED_TYPE", length = 36)
    private String dedType;

    @Column(name = "DED_PERC", precision = 12, scale = 2)
    private BigDecimal dedPerc;

    @Column(name = "DED_AMT_FC", precision = 17, scale = 2)
    private BigDecimal dedAmtFc;

    @Column(name = "DED_AMT_LC", precision = 17, scale = 2)
    private BigDecimal dedAmtLc;
    
    @Id
    @Column(name = "REQUESTREFERENCENO", length = 135)
    private String requestReferenceNo;

    @Column(name = "P_WS_RESPONSE_TYPE", length = 450)
    private String pWsResponseType;

    @Column(name = "P_WS_ERROR", length = 3000)
    private String pWsError;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="REQUEST_TIME")
    private Date       requestTime ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="RESPONSE_TIME")
    private Date       responseTime ;

    @Column(name = "STATUS", length = 90)
    private String status;

}
