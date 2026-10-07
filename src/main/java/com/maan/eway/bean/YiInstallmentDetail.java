package com.maan.eway.bean;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DynamicInsert
@DynamicUpdate
@Builder
@Entity
@Table(name = "yi_installment_detail")
@Data
public class YiInstallmentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inst_sys_Id")
	private Long instsysId;

	@Column(name = "quotation_policy_no")
	private String quotationPolicyNo;

	@Column(name = "uw_sys_id")
	private Long uwSysId;

	@Column(name = "pol_idx")
	private Integer polIdx;

	@Column(name = "end_no")
	private String endNo;

	@Column(name = "prem_curr")
	private String premCurr;

	@Column(name = "installment_no")
	private Integer installmentNo;

	@Column(name = "installment_perc")
	private BigDecimal installmentPerc;

	@Column(name = "due_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dueDate;

	@Column(name = "due_amount")
	private BigDecimal dueAmount;

	@Column(name = "due_amount_lc")
	private BigDecimal dueAmountLc;

	@Column(name = "payment_status")
	private String paymentStatus;

	@Column(name = "paid_amount")
	private BigDecimal paidAmount;

	@Column(name = "paid_amount_lc")
	private Double paidAmountLc;

	@Column(name = "payment_date")
	private Date paymentDate;

	@Column(name = "payment_details")
	private String paymentDetails;

	@Column(name = "created_uid")
	private String createdUid;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_uid")
	private String updatedUid;

	@Column(name = "updated_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedDate;
	
	@Column(name = "total_amount")
	private BigDecimal totalAmount;
	
	@Column(name = "total_amount_lc")
	private BigDecimal totalAmountLc;
	
	@Column(name = "total_amount_without_tax")
	private BigDecimal totalAmountwithoutTax;
	
    @Column(name = "P_WS_RESPONSE_TYPE", length = 450)
    private String PWsResponseType;

    @Column(name = "P_WS_ERROR", length = 3000)
    private String PWsError;
}
