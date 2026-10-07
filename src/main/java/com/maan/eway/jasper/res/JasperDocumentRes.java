package com.maan.eway.jasper.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.maan.eway.error.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JasperDocumentRes {

	@JsonProperty("PdfOutFilePath")
	private String pdfoutfilepath;
	
	@JsonProperty("PdfOutFile")
	private String pdfoutfile;
	
	@JsonProperty("ErrorMessage")
	private List<Error> errorMessage;
	
	@JsonProperty("JsonString")
	private JsonNode jsonString;

	
}
