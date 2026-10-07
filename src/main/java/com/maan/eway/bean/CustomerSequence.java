package com.maan.eway.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customersequence")
@IdClass(CustomerSequenceId.class)
public class CustomerSequence {

    @Id
    @Column(name = "sequence_no")
    private String sequenceNo;

    @Id
    @Column(name = "company_id")
    private String companyid;

    public CustomerSequence() {
    }

    public CustomerSequence(String sequenceNo, String companyid) {
        this.sequenceNo = sequenceNo;
        this.companyid = companyid;
    }
}