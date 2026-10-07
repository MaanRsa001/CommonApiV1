package com.maan.eway.crm.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachedBranchRes {
    @JsonProperty("BranchName")
    private String barnchName;
    @JsonProperty("BranchCoreAppCode")
    private String branchCoreAppCode;
}