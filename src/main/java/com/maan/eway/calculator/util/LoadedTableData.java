package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import jakarta.persistence.Tuple;

public class LoadedTableData {

    public List<Tuple> vehicles;
    public List<Tuple> customers;
    public List<Tuple> commontbl;
    public List<Tuple> prorata;
    public List<Tuple> drivers;
    public DecimalFormat decimalFormat;
    public BigDecimal minimumPremium;

    public LoadedTableData(
            List<Tuple> vehicles,
            List<Tuple> customers,
            List<Tuple> commontbl,
            List<Tuple> prorata,
            List<Tuple> drivers,
            DecimalFormat decimalFormat,
            BigDecimal minimumPremium) {

        this.vehicles = vehicles;
        this.customers = customers;
        this.commontbl = commontbl;
        this.prorata = prorata;
        this.drivers = drivers;
        this.decimalFormat = decimalFormat;
        this.minimumPremium = minimumPremium;
    }
    
    public LoadedTableData() {

        
    }
}