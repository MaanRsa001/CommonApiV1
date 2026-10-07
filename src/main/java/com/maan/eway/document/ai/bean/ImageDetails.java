package com.maan.eway.document.ai.bean;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "image_details")
public class ImageDetails {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageAngle;
    private String imageName;
    private String imageType;
    private String imagePath;
   
    @Column(name = "quote_no")
    private String quoteNo;
    
    @Column(name="unique_id")
    private Integer uniqueId;

    @Column(name = "damage_status")
    private String damageStatus;
    @OneToOne(mappedBy = "vehicleImage", cascade = CascadeType.ALL)
    private VehicleDetails details;
}
