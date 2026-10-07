package com.maan.eway.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eservice_customer_location")
public class EserviceCustomerLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Column(name = "LOCATION_NAME", nullable = false, length = 100)
    private String locationName;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "ACTIVE", nullable = false, length = 1)
    private String active;   // expected values: 'Y' or 'N'

    @Column(name = "COMPANY_ID", nullable = false, length = 50)
    private String companyId;

    @Column(name = "CUSTOMER_REFERENCE_NO", nullable = false, length = 50)
    private String customerReferenceNo;
}