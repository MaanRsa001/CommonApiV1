package com.maan.eway.mtpintegration.entity;
/*
import com.maan.eway.mtpintegration.entity.MtpVehicleValidation;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "mtp_vehicle_validation")
public class MtpVehicleValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number_plate")
    private String numberPlate;

    @Column(name = "assessment_type")
    private String assessmentType;

    @Column(name = "return_code")
    private Integer returnCode;

    @Column(name = "amount")
    private String amount;

    @Column(name = "vehicle_no")
    private String vehicleNo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "engine_size")
    private String engineSize;

    @Column(name = "make_name")
    private String makeName;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(String engineSize) {
        this.engineSize = engineSize;
    }

    public String getMakeName() {
        return makeName;
    }

    public void setMakeName(String makeName) {
        this.makeName = makeName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}*/



import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "mtp_vehicle_validation")
@Data
public class MtpVehicleValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number_plate")
    private String numberPlate;

    @Column(name = "assessment_type")
    private String assessmentType;

    @Column(name = "return_code")
    private Integer returnCode;

    @Column(name = "amount")
    private String amount;

    @Column(name = "vehicle_no")
    private String vehicleNo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "engine_size")
    private String engineSize;

    @Column(name = "make_name")
    private String makeName;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "POLICY_START_DATE")
    private Date policyStartDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "POLICY_END_DATE")
    private Date policyEndDate;

   
}

