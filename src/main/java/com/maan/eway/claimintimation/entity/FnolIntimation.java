package com.maan.eway.claimintimation.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fnol_intimation")
@Getter
@Setter
@NoArgsConstructor
public class FnolIntimation {

    @Id
    @Column(name = "INTIMATION_NO")
    private String intimationNo;

    @Column(name = "REQUEST_REFERENCE_NO")
    private String requestReferenceNo;

    @Column(name = "CLAIM_TYPE")
    private String claimType;

    @Column(name = "CLAIM_CATEGORY")
    private String claimCategory;

    @Column(name = "PARTY_TYPE")
    private String partyType;

    @Column(name = "AT_FAULT")
    private String atFault;

    @Column(name = "LOSS_DATE")
    private Date lossDate;

    @Column(name = "LOSS_LOCATION")
    private String lossLocation;

    @Column(name = "LOSS_DESC")
    private String lossDesc;

    @Column(name = "CODE")
    private String code;

    @Column(name = "CONTACT_PERSON_MOBILE_NO")
    private Long contactPersonMobileNo;

    @Column(name = "POLICE_STATION")
    private String policeStation;

    @Column(name = "POLICE_REPORT_NO")
    private String policeReportNo;

    @Column(name = "ACCIDENT_NO")
    private Integer accidentNo;

    @Column(name = "THIRD_PARTY_INVOLVED")
    private String thirdPartyInvolved;

    @Column(name = "ENTRY_DATE")
    private Date entryDate;
    
    @Column(name="POLICY_NO")
    private String policyNo;

    // Unidirectional OneToMany
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "INTIMATION_NO") // This adds FK in child tables
    private List<ThirdPartyInfo> thirdParties = new ArrayList<ThirdPartyInfo>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "INTIMATION_NO") // This adds FK in child tables
    private List<DocumentInfo> documents = new ArrayList<DocumentInfo>();
}
