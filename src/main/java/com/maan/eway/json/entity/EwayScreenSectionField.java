package com.maan.eway.json.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "eway_screen_section_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@IdClass(EwayScreenSectionFieldId.class)
@Builder
public class EwayScreenSectionField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Integer sno;
    
    
    @Column(name="screen_id")
    private Integer ScreenId;

    @Column(name = "type")
    private String type;

    @Column(name = "label")
    private String label;

    @Column(name = "name")
    private String name;

    @Column(name = "id")
    private String id;

    @Column(name = "placeholder")
    private String placeholder;

    @Column(name = "pattern")
    private String pattern;

    @Column(name = "validation")
    private String validation;

    @Column(name = "minlength")
    private Integer minLength;

    @Column(name = "maxlength")
    private Integer maxLength;

    @Column(name = "classname")
    private String className;

    @Column(name = "disabled")
    private Boolean disabled;

    @Column(name = "hide")
    private Boolean hide;

    @Column(name = "defaultvalue")
    private String defaultValue;

    @Column(name = "f_rows")
    private Integer rows;

    @Column(name = "cols")
    private Integer cols;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "section_id")
    private String sectionId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "enrty_date")
    private LocalDateTime entryDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "status")
    private String status;

    @Column(name = "hooks")
    private String hooks;
    
    @Column(name="f_key")
    private String key;
    
    @Column(name="required")
    private Boolean required;
    
    @Column(name="cover_id")
    private String coverId;
    
    @Column(name="cover_name")
    private String coverName;
    
    @Column(name="api_key")
    private String ApiKey;
    
    @Column(name="display_order")
    private Integer displayOrder;
    
    @OneToMany(fetch = FetchType.EAGER,mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EwayScreenFieldOption> options;
}
