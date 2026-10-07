package com.maan.eway.bean;

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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(EwayLobMasterId.class)
@Table(name = "eway_line_of_buisness")
public class EwayLobMaster {

	@Id
	@Column(name = "company_id")
	private String compnayId;

	@Column(name = "company_code")
	private String companyCode;

	@Column(name = "company_name")
	private String companyNmae;

	@Column(name = "product_id")
	private String productId;

	@Id
	@Column(name = "lob_id")
	private String lobId;

	@Id
	@Column(name = "lob_code")
	private String lobCode;

	@Column(name = "lob_name")
	private String lobName;

	@Column(name = "created_user")
	private String CreatedUser;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date cretedDate;

	@Column(name = "updated_user")
	private String updatedUser;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private Date updatedDate;

}
