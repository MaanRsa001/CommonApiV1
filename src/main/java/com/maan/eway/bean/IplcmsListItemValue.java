package com.maan.eway.bean;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "iplcms_list_item_value")
public class IplcmsListItemValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "core_app_code", length = 300)
    private String coreAppCode;

    @Column(name = "item_code", length = 60)
    private String itemCode;

    @Column(name = "item_desc", length = 12000)
    private String itemDesc;

    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_type", length = 150)
    private String itemType;

    @Column(name = "item_value", length = 300)
    private String itemValue;

    @Column(name = "param1", length = 300)
    private String param1;

    @Column(name = "param2", length = 300)
    private String param2;

    @Column(name = "status", length = 15)
    private String status;
}
