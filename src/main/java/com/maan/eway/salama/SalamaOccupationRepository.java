package com.maan.eway.salama;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.SalamaOccupation;

@Repository
public interface SalamaOccupationRepository extends JpaRepository<SalamaOccupation, Integer> {

	@Query("""
            SELECT s
            FROM SalamaOccupation s
            WHERE s.companyId = :companyId
            AND s.industryId = :industryId
            AND s.effectiveDateStart <= CURRENT_DATE
			AND s.effectiveDateEnd >= CURRENT_DATE
            AND s.amendId = (
                    SELECT MAX(x.amendId)
                    FROM SalamaOccupation x
                    WHERE x.companyId = s.companyId
                    AND x.industryId = s.industryId
                    AND x.sNo = s.sNo
            )
            """)
    List<SalamaOccupation> getOccupationList(Integer companyId, Integer industryId);


}
