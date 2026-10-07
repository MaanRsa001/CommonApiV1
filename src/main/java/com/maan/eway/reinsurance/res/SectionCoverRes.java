package com.maan.eway.reinsurance.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SectionCoverRes {
    private Long productId;
    private String productName;
    private Long sectionId;
    private String sectionName;
    private Long coverId;
    private String coverName;
    private Date effectiveDateStart;
    private Date effectiveDateEnd;

}
