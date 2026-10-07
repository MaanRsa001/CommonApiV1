package com.maan.eway.document.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class TermsDocRes {


	@JsonProperty("UniqueId")
	private String uniqueId;


	@JsonProperty("FilePathOriginal")
	private String filePathOriginal;

	
	@JsonProperty("DocumentName")
	private String documentName;

	
	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("UploadedTime")
	private Date uploadedTime;
	
	@JsonProperty("IdType")
	private String idType ;

	@JsonProperty("ProductType")
	private String productType;
	
	
	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("OriginalFileName")
	private String originalFileName;

	@JsonProperty("UploadedBy")
	private String uploadedBy;

	@JsonProperty("FilePathName")
	private String filepathname ;
	@JsonProperty("UploadedTime")
	private String uploadedtime;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("FileName")
	private String filename;
	
	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("DocumentDesc")
	private String documentDesc;
	
	@JsonProperty("DocApplicable")
	private String docApplicable;
	@JsonProperty("DocApplicableId")
	private String docApplicableId;
	
	@JsonProperty("CommonFilePath")
	private String commonfilepath ;
	
	@JsonProperty("Errorres")
	private String errorres ;
	


	@JsonProperty("Id")
	private String id;

	@Column(name = "FILE_PATH_ORGINAL")
	private String filePathOrginal;
	
	@JsonProperty("DocDesc")
	private String docDesc;
	
	
	@JsonProperty("DocumentType")
	private String documentType;
	
	@JsonProperty("DocumentTypeDesc")
	private String documentTypeDesc;
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("OrginalFileName")
	private String orginalFileName;
	
	@JsonProperty("ImgUrl")
	private String imgurl;
}
