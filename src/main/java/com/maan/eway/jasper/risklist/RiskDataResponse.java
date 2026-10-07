package com.maan.eway.jasper.risklist;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskDataResponse {
	private List<SectionDetailsDTO> sectionDetails;
	private List<SectionDetailsDTO> commonSectionDetails;
}

