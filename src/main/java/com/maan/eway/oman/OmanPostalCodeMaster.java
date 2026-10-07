package com.maan.eway.oman;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@IdClass(OmanPostalCodeMasterId.class)
@Table(name="oman_postalcode_master")
public class OmanPostalCodeMaster implements Serializable{
	
	private static final long serialVersionUID = 1L;
	 
    //--- ENTITY PRIMARY KEY 
    @Id
    @Column(name="Country_id")
    private String     countryId ;
    
    @Id
    @Column(name="Governorate_id")
    private Long     governorateId ;
    
    @Id
    @Column(name="Wilayat_id")
    private Long     wilayatId ;
    
    @Id
    @Column(name="Location_id")
    private Long     locationId ;
    
    @Id
    @Column(name="Company_id")
    private Long     companyId ;
    
  //--- ENTITY DATA FIELDS 
    @Column(name="Governorate")
    private String     governorate ;

    @Column(name="Wilayat")
    private String     wilayat ;
    
    @Column(name="Location")
    private String     location ;
    
    @Column(name="Postal Code")
    private Long     postalCode ;
    
    @Column(name="Status")
    private String     status ;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="Entry_Date")
    private Date       entryDate ;

}
