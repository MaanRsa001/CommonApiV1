package com.maan.eway.json.dto;

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
public class EwayFieldRequest {

    private Integer screenId;
    private String type;
    private String label;
    private String name;
    private String id;
    private String placeholder;
    private String pattern;
    private String validation;
    private Integer minlength;
    private Integer maxlength;
    private String className;
    private Boolean disabled;
    private Boolean hide;
    private String defaultValue;
    private Integer rows;
    private Integer cols;
    private String companyId;
    private String productId;
    private String sectionId;
    private String createdBy;
    private String updatedBy;
    private String status;
    private String hooks;
    private String key;
    private Boolean required;
    private String coverId;
    private String coverName;
    private String apiKey;
    private Integer displayOrder;
   
    private List<OptionRequest> options;
}


