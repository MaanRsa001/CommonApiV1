package com.maan.eway.form.fields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

/**
 * FormFieldConfigRepository
 *
 * Data access layer for FormFieldConfig entity.
 * All query methods work only on active records (IS_ACTIVE = true)
 * unless otherwise stated.
 */
@Repository
public interface FormFieldConfigRepository extends JpaRepository<FormFieldConfig, Long> {

    /**
     * Fetch all active field configs ordered by section key then sort order.
     */
    List<FormFieldConfig> findByIsActiveTrueOrderBySectionKeyAscSortOrderAsc();

    /**
     * Fetch a single active field config by its ID.
     */
    Optional<FormFieldConfig> findByIdAndIsActiveTrue(Long id);

    /**
     * Fetch all active fields belonging to a specific section.
     */
    List<FormFieldConfig> findBySectionKeyAndIsActiveTrueOrderBySortOrderAsc(String sectionKey);

    /**
     * Check whether a given fieldKey already exists in any active record
     * (used to prevent duplicate keys on create).
     */
    boolean existsByFieldKeyAndIsActiveTrue(String fieldKey);

    /**
     * Check whether a given fieldKey exists in any active record
     * excluding a specific ID (used during update validation).
     */
    boolean existsByFieldKeyAndIsActiveTrueAndIdNot(String fieldKey, Long id);

    /**
     * Fetch all fields that have a count-based display condition configured,
     * filtered by the condition field name.
     * Used by the section-visibility API.
     */
    @Query("""
            SELECT f FROM FormFieldConfig f
            WHERE f.isActive = true
              AND f.countConditionField = :conditionField
              AND f.countConditionOp    IS NOT NULL
              AND f.countConditionValue IS NOT NULL
            ORDER BY f.sectionKey ASC, f.sortOrder ASC
            """)
    List<FormFieldConfig> findByCountConditionField(@Param("conditionField") String conditionField);

}
