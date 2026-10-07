package com.maan.eway.document.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilePathNameRes {


	@JsonProperty("ClientId")
	private String clientId ;

	@JsonProperty("DocumentReferenceNumber")
	private String documentRef;
	
	@JsonProperty("DocDesc")
	private String docTypeDescription;
	
	@JsonProperty("UploadedTime")
	private String uploadedTime;
	
	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("DocTypeId")
	private Integer docTypeId;
	
	@Column(name = "FILE_PATH_ORGINAL")
	private String filePathOrginal;
	
	@JsonProperty("CreatedBy")
	private String createdby;
	
	@JsonProperty("status")
	private String status;
	
	@JsonFormat(pattern ="dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
}
