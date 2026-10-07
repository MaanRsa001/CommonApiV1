package com.maan.eway.renewal.res;

import java.util.List;

import lombok.Data;

@Data
public class BranchForRenewalTrack {
private String companyId;
private String noOfDivisions;
private  List<DivisionDetails> divisionDetails;
	
}
