package com.maan.eway.excelupload.bean;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "uploaded_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String companyId;
	private String productId;
	private String sectionId;
	private String coverId;
	private String requestRefNo;
	
	
	@Lob
	@Column(columnDefinition = "TEXT")
	private String dataJson; // store the extracted values as JSON
	
	
	private Instant createdAt;
}
