package com.maan.eway.form.fields;


import java.util.List;

import com.maan.eway.common.res.CommonRes;

/**
 * FormFieldConfigService
 *
 * Defines the contract for all form field configuration operations.
 * Business logic is implemented in FormFieldConfigServiceImpl.
 */
public interface FormFieldConfigService {

    /**
     * Retrieve all active form field configurations.
     * Results are ordered by section key then sort order.
     *
     * @return list of all active field config responses
     */
    List<FormFieldConfigResponse> getAllConfigs();

    /**
     * Retrieve a single active form field configuration by its ID.
     *
     * @param id the field config ID
     * @return the matching field config response
     */
    FormFieldConfigResponse getConfigById(Long id);

    /**
     * Retrieve all active form field configurations for a given section.
     *
     * @param sectionKey the section identifier e.g. "customer", "fire", "par"
     * @return list of field config responses in that section
     */
    List<FormFieldConfigResponse> getConfigsBySection(String sectionKey);

    /**
     * Save a new form field configuration.
     * Validates that dropdown types carry apiUrl and requestKeys.
     *
     * @param request the incoming field config request payload
     * @return the saved field config response
     */
    FormFieldConfigResponse saveConfig(FormFieldConfigRequest request);

    /**
     * Update an existing form field configuration by ID.
     * Validates that dropdown types carry apiUrl and requestKeys.
     *
     * @param id      the field config ID to update
     * @param request the updated field config request payload
     * @return the updated field config response
     */
    FormFieldConfigResponse updateConfig(Long id, FormFieldConfigRequest request);

    /**
     * Soft-delete a form field configuration (sets IS_ACTIVE = false).
     *
     * @param id the field config ID to delete
     */
    void deleteConfig(Long id);

    /**
     * Evaluate which sections should be visible based on current count values.
     * Each key in the request map is a form-field key configured as a count trigger.
     * Each value is the live count from the form.
     *
     * @param request the section visibility request containing count values map
     * @return the visibility result for each affected section
     */
    SectionVisibilityResponse evaluateSectionVisibility(SectionVisibilityRequest request);

	CommonRes getSectionCount(FormFieldConfigRequest req);

	CommonRes getSectionList(FormFieldConfigRequest req);
}
