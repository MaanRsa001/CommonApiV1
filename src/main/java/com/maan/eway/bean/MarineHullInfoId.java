package com.maan.eway.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarineHullInfoId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer productId;
    private Integer sectionId;
    private String requestReferenceNo;
    private Integer coverId;
    
}
