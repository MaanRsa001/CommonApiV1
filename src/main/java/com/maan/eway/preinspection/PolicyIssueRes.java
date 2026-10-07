package com.maan.eway.preinspection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyIssueRes {

    private String policyNo;
    private String debitNoteNo;
    private String creditNoteNo;
    private String stickerNo;
    private String status; 
}
