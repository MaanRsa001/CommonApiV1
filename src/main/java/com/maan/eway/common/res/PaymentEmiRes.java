package com.maan.eway.common.res;

import jakarta.persistence.Column;
import lombok.Data;

@Data

public class PaymentEmiRes {
    @Column(name="EMI_YN", length=100)
    private String     emiYn ;
    
    @Column(name="INSTALLMENT_MONTH", length=20)
    private String     installmentMonth;
    
    @Column(name="INSTALLMENT_PERIOD", length=20)
    private String     installmentPeriod;

}
