package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "test_accounting_entry_mapping")
public class WnmAccountingEntryMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aem_sys_id")
    private Long aemSysId;

    @Column(name = "acnt_type", length = 30)
    private String acntType;

    @Column(name = "acnt_type_desc", length = 240)
    private String acntTypeDesc;

    @Column(name = "acnt_sub_type", length = 30)
    private String acntSubType;

    @Column(name = "acnt_sub_type_desc", length = 240)
    private String acntSubTypeDesc;

    @Column(name = "acnt_table", length = 240)
    private String acntTable;

    @Column(name = "acnt_column", length = 240)
    private String acntColumn;

    @Column(name = "acnt_column_type", length = 30)
    private String acntColumnType;

    @Column(name = "acnt_column_desc", length = 240)
    private String acntColumnDesc;

    @Column(name = "acnt_value_type", length = 240)
    private String acntValueType;

    @Column(name = "acnt_sql_stmt", columnDefinition = "TEXT")
    private String acntSqlStmt;

    @Column(name = "acnt_cr_uid", length = 100)
    private String acntCrUid;

    @Temporal(TemporalType.DATE)
    @Column(name = "acnt_cr_dt")
    private Date acntCrDt;

    @Column(name = "acnt_upd_uid", length = 100)
    private String acntUpdUid;

    @Temporal(TemporalType.DATE)
    @Column(name = "acnt_upd_dt")
    private Date acntUpdDt;

    @Column(name = "company_id", length = 100)
    private String companyId;

    /* =======================
       AUDIT AUTO POPULATION
       ======================= */

    @PrePersist
    protected void onCreate() {
        this.acntCrDt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.acntUpdDt = new Date();
    }
}
