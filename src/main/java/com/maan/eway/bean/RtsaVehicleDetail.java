package com.maan.eway.bean;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name="rtsa_vehicle_detail")
public class RtsaVehicleDetail implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	 
	    //--- ENTITY PRIMARY KEY 
	    @Id
	    @Column(name="Registration_No", nullable=false)
	    private String     registrationNo ;
	    
	    @Column(name="CODE")
	    private String code;
	    
	    @Column(name="MESSAGE")
	    private String message;
	    
	    @Column(name="RESPONSE")
	    private String response;
	    
	    @Column(name="RESPONSE_TIME")
	    private String responseTime;
	    
	    @Column(name="MAKE_NAME")
	    private String makeName;
	    
	    @Column(name="MODEL_NAME")
	    private String modelName;
	    
	    @Column(name="ENGINE_NO")
	    private String engineNo;
	    
	    @Column(name="CHASSIS_NO")
	    private String chassisNo;
	    
	    @Column(name="YEAR_MAKE")
	    private String yearMake;
	    
	    @Column(name="GVM")
	    private String gvm;
	    
	    @Column(name="BODY_TYPE")
	    private String bodyType;
	    
	    @Column(name="COLOR")
	    private String color;
	    
	    @Column(name="NUMBER_OF_SEATS")
	    private String numberOfSeats;
	    
	    @Column(name="FIRST_REG_DATE")
	    private String firstRegDate;
	    
	    @Column(name="CURRENT_LICENSE_EXPDATE")
	    private String currentLinenseExpDate;
	    
	    @Column(name="ROAD_WORTH_EXPDATE")
	    private String roadWortExpDate;
	    
	    @Column(name="REG_STATUS")
	    private String regStatus;
	 

}
