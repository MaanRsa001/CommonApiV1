package com.maan.eway.preinspection;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPreInspectinUploadList {
	@JsonProperty("ImageName")
	private String imageName;
	
	@JsonProperty("ImageFilePath")
	private String imageFilePath;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entry_date;
	
	@JsonProperty("IMG_URL")
	private String imgUrl;
	
	@JsonProperty("ReferenceNo")
	private String referenceNo;
	
	@JsonProperty("OriginalFileName")
	private String originalFileName;
	
	@JsonProperty("AdminStatus")
	private String adminStatus;
	
	@JsonProperty("Sno")
	private String sNo;
}
