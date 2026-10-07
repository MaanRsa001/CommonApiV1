package com.maan.eway.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YiSmiDetailId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String quotationPolicyNo;
    private Double cvrId;

    private Integer prsSmiCode;
    private String prsSmiDesc;
    private String prsSiFc;
    private String prsSiLc1;
    private String prsPremFc;
    private String prsPremLc1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YiSmiDetailId that = (YiSmiDetailId) o;
        return Objects.equals(quotationPolicyNo, that.quotationPolicyNo) &&
                Objects.equals(cvrId, that.cvrId) &&
                Objects.equals(prsSmiCode, that.prsSmiCode) &&
                Objects.equals(prsSmiDesc, that.prsSmiDesc) &&
                Objects.equals(prsSiFc, that.prsSiFc) &&
                Objects.equals(prsSiLc1, that.prsSiLc1) &&
                Objects.equals(prsPremFc, that.prsPremFc) &&
                Objects.equals(prsPremLc1, that.prsPremLc1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                quotationPolicyNo,
                cvrId,
                prsSmiCode,
                prsSmiDesc,
                prsSiFc,
                prsSiLc1,
                prsPremFc,
                prsPremLc1
        );
    }
}