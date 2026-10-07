package com.maan.eway.claimintimation.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "document_info")
@Getter
@Setter
@NoArgsConstructor
public class DocumentInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "INTIMATION_NO")
	private String intimationNo;

	@Column(name = "REQUEST_REFERENCE_NO")
	private String requestReferenceNo;

	@Column(name = "DOCUMENT")
	private String document;

	@Column(name = "FORMAT")
	private String format;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DOCUMENT_TYPE")
	private String documentType;

	@Column(name = "ENTRY_DATE")
	private Date entryDate;
}
