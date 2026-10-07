package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.WnmGeneralMapping;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface wnmGeneralMappingRepository extends JpaRepository<WnmGeneralMapping, Long>,
        JpaSpecificationExecutor<WnmGeneralMapping> {

    boolean existsByCompanyIdAndGmMapTypeAndGmTableAndGmColumn(
            String companyId,
            String gmMapType,
            String gmTable,
            String gmColumn
    );

    boolean existsByCompanyIdAndGmMapTypeAndGmTableAndGmColumnAndGmSysIdNot(
            String companyId,
            String gmMapType,
            String gmTable,
            String gmColumn,
            Long gmSysId
    );

    @Query(value = """
                SELECT object_name, input_name, input_type
                FROM wnm_ri_tab_column
                WHERE master_table_name = :tableName AND company_id =:companyId
            """, nativeQuery = true)
    List<Object[]> getColumnDetails(@Param("tableName") String tableName,
                                    @Param("companyId") String companyId);

}
