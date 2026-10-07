package com.maan.eway.viewAll.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "field_query_tablequery")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldQueryTableQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "Query_id", precision = 10, scale = 0)
    private BigDecimal queryId;

    @Column(name = "Query_name", length = 50)
    private String queryName;

    @Column(name = "SQL_QUERY", length = 4000)
    private String sqlQuery;
    
    @Column(name = "Product_type", length = 4000)
    private String productType;
    
    @Column(name = "Pdf_Yn", length = 4000)
    private String pdfYn;
}

