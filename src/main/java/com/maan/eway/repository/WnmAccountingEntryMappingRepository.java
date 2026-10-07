package com.maan.eway.repository;

import com.maan.eway.bean.WnmAccountingEntryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WnmAccountingEntryMappingRepository
        extends JpaRepository<WnmAccountingEntryMapping, Long>,
        JpaSpecificationExecutor<WnmAccountingEntryMapping> {
    boolean existsByCompanyIdAndAcntTypeAndAcntSubTypeAndAcntTableAndAcntColumn(String companyId, String acntType, String acntSubType, String acntTable, String acntColumn);

    boolean existsByCompanyIdAndAcntTypeAndAcntSubTypeAndAcntTableAndAcntColumnAndAemSysIdNot(String companyId, String acntType, String acntSubType, String acntTable, String acntColumn, Long aemSysId);
}
