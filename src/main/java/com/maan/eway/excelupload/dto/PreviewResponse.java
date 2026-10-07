package com.maan.eway.excelupload.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviewResponse {
	private int totalRows;
	private List<PreviewRow> previewRows;
	private int validRows;
	private int invalidRows;
}
