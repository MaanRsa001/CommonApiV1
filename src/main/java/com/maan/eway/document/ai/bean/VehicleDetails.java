package com.maan.eway.document.ai.bean;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "vehicle_details")
public class VehicleDetails {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String VehicleType;
    private String vehicleNumber;
    private String VehicleMake;
    private String VehicleModel;
    private String VehicleColor;
    private Long TotalCostInUsd;
    private Long TotalMinCosINR;
    private Long TotalMaxCostINR;
    @Column(name="quote_no")
    private String quoteNo;
    @Column(name="vehicle_reg_no")
    private String vegicleRegNo;
    @Column(name="company_id")
    private String companyId;
    @Column(name = "analysis_scope", length = 1000)
    private String analysisScope;

    @OneToOne
    @JoinColumn(name = "vehicle_image_id")
    private ImageDetails vehicleImage;


    @OneToMany(mappedBy = "vehicleDetails", cascade = CascadeType.ALL)
    private List<DamageDetails> damages;
}
