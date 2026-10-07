package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="COUNTRY_MASTER")
@Entity
public class CountryMasters {

	@EmbeddedId
	private CountryMastersId countrypk;
	@Column(name="SNO__")
	private Long sno;
	@Column(name="COUNTRY_NAME")
	private String countryname;
	@Column(name="COUNTRY_SHORT_CODE")
	private String countryshortname;
	@Column(name="STATUS")
	private String status;
	@Column(name="RSACODE")
	private String rsacode;
	@Column(name="NATIONALITY_NAME")
	private String nationalityname;
	@Column(name="EFFECTIVE_DATE")
	private Date effectivedate;
	@Column(name="GEO_RATE")
	private Double georate;
	@Column(name="REMARKS")
	private String remarks;
	@Column(name="INT_PORT_CODE")
	private String intportcode;
	@Column(name="AIR_CORE_APP_CODE")
	private String airCoreAppCode;
	@Column(name="SEA_CORE_APP_CODE")
	private String seaCoreAppCode;

}
