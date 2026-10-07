package com.maan.eway.document.ai.bean;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "damage_details")
public class DamageDetails {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String DamagePart;
    private String materialType;
    private String damageType;
    private Long damagePercentage;
    private String recommendation;
    private Long repairCostUsd;
    private Long repairCostMin;
    private Long repairCostMax;
    @Column(name="quote_no")
    private String quoteNo;
    @Column(name="damage_side")
    private String damageSide;

    @ManyToOne
    @JoinColumn(name = "vehicle_details_id") // foreign key column
    private VehicleDetails vehicleDetails;
}
