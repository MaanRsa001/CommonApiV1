package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonFormat;

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

/**
 * Entity class for table "renew_vehicle_info"
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@Table(name = "renew_vehicle_info")
public class RenewVehicleInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "POLICY_NO", nullable = false, length = 100)
    private String policyNo;

    @Column(name = "RISK_ID", length = 20)
    private String riskId;

    @Column(name = "MAKE", length = 200)
    private String make;

    @Column(name = "MODEL", length = 200)
    private String model;

    @Column(name = "BODY_TYPE", length = 200)
    private String bodyType;

    @Column(name = "VEHICLE_USAGE", length = 200)
    private String vehicleUsage;

    @Column(name = "POLICY_TYPE", length = 50)
    private String policyType;

    @Column(name = "SUM_INSURED")
    private Double sumInsured;

    @Column(name = "STATUS", length = 100)
    private String status;

    @Column(name = "CREATED_BY", length = 200)
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENTRY_DATE")
    private Date entryDate;
}
