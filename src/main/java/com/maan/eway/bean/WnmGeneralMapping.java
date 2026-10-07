package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "test_general_mapping")
public class WnmGeneralMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gm_sys_id")
    private Long gmSysId;

    @Column(name = "gm_map_type", length = 30)
    private String gmMapType;

    @Column(name = "gm_map_type_desc", length = 240)
    private String gmMapTypeDesc;

    @Column(name = "gm_table", length = 240)
    private String gmTable;

    @Column(name = "gm_column", length = 240)
    private String gmColumn;

    @Column(name = "gm_column_type", length = 30)
    private String gmColumnType;

    @Column(name = "gm_column_desc", length = 240)
    private String gmColumnDesc;

    @Column(name = "gm_value_type", length = 240)
    private String gmValueType;

    @Column(name = "gm_sql_stmt", columnDefinition = "TEXT")
    private String gmSqlStmt;

    @Column(name = "gm_cr_uid", length = 100)
    private String gmCrUid;

    @Temporal(TemporalType.DATE)
    @Column(name = "gm_cr_dt")
    private Date gmCrDt;

    @Column(name = "gm_upd_uid", length = 100)
    private String gmUpdUid;

    @Temporal(TemporalType.DATE)
    @Column(name = "gm_upd_dt")
    private Date gmUpdDt;

    @Column(name = "company_id", length = 150)
    private String companyId;

    /* =====================
       AUDIT AUTO HANDLING
       ===================== */

    @PrePersist
    protected void onCreate() {
        this.gmCrDt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.gmUpdDt = new Date();
    }
}
