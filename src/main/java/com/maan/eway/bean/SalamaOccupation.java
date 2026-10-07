package com.maan.eway.bean;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "salama_occupation")
public class SalamaOccupation {

    @Id
    @Column(name = "S_no")
    private Integer sNo;

    @Column(name = "Trade_Code")
    private String tradeCodeInternal;

    @Column(name = "Occupation_Description")
    private String occupationDescription;

    @Column(name = "Type")
    private String type;

    @Column(name = "Type_id")
    private String typeId;

    @Column(name = "Fire_Load")
    private String fireLoad;

    @Column(name = "SALAMA_internal")
    private String ragexSalamaInternal;

    @Column(name = "UW_Category")
    private String uwCategory;

    @Column(name = "Base_Rate")
    private Double baseRate;

    @Column(name = "Search_Flag")
    private String searchFlag;

    @Column(name = "Amend_id")
    private Integer amendId;
    
    @Column(name = "Industry_id")
    private Integer industryId;

    @Column(name = "Company_id")
    private Integer companyId;

    @Column(name = "Effective_date_start")
    @Temporal(TemporalType.DATE)
    private Date effectiveDateStart;

    @Column(name = "Effective_date_end")
    @Temporal(TemporalType.DATE)
    private Date effectiveDateEnd;

    @Column(name = "Entry_date")
    @Temporal(TemporalType.DATE)
    private Date entryDate;
}

