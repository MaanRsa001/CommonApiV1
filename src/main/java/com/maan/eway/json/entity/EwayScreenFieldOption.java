package com.maan.eway.json.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "eway_screen_field_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EwayScreenFieldOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="code")
    private Long code;

    @Column(name = "code_desc")
    private String codeDesc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "field_sno", referencedColumnName = "sno"),
        @JoinColumn(name = "field_screen_id", referencedColumnName = "screen_id")
    })
    @JsonBackReference
    private EwayScreenSectionField field;
}
