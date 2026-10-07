package com.maan.eway.repository;

import com.maan.eway.bean.WntAcntHdr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WntAcntHdrRepository
        extends JpaRepository<WntAcntHdr, Long>,
        JpaSpecificationExecutor<WntAcntHdr> {
}
