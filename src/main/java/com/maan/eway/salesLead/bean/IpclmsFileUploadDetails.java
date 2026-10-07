package com.maan.eway.salesLead.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ipclms_fileupload_details")
@Builder

public class IpclmsFileUploadDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "FILE_ID", nullable = false)
	private BigDecimal fileId;
	
	@Column(name = "ENQUIRY_ID", nullable = false)
	private String enquiryId;
	
	@Column(name = "QUOTE_NO")
	private String quoteNo;
	
	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "LOGIN_ID", nullable = false)
	private String loginId;
	
	@Column(name = "STATUS")
	private String status;	

}
