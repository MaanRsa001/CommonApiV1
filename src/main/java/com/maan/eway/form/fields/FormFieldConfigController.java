package com.maan.eway.form.fields;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.SuccessRes;

import java.util.Collections;
import java.util.List;

/**
 * FormFieldConfigController
 *
 * REST API endpoints for managing form field configurations.
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * GET    /api/form-configs                          → Get all active configs
 * GET    /api/form-configs/{id}                     → Get config by ID
 * GET    /api/form-configs/section/{sectionKey}     → Get configs by section
 * POST   /api/form-configs                          → Create new config
 * PUT    /api/form-configs/{id}                     → Update existing config
 * DELETE /api/form-configs/{id}                     → Soft-delete config
 * POST   /api/form-configs/section-visibility       → Count-based section display
 * ─────────────────────────────────────────────────────────────────────────────
 *
 * All responses are wrapped in ApiResponse<T>.
 * Controller delegates all logic to FormFieldConfigService.
 */
@RestController
@RequestMapping("/api/form-configs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FormFieldConfigController {

    private final FormFieldConfigService formFieldConfigService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/form-configs
    // Returns all active field configurations ordered by section + sort order.
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<List<FormFieldConfigResponse>>> getAllConfigs() {
        try {
            List<FormFieldConfigResponse> responseList = formFieldConfigService.getAllConfigs();
            return ResponseEntity.ok(
                    ApiResponse.success("Fetched all form field configurations", responseList));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/form-configs/{id}
    // Returns a single field configuration by ID.
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FormFieldConfigResponse>> getConfigById(
            @PathVariable Long id) {
        try {
            FormFieldConfigResponse response = formFieldConfigService.getConfigById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Fetched form field configuration", response));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/form-configs/section/{sectionKey}
    // Returns all active fields for a specific section e.g. "fire".
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/section/{sectionKey}")
    public ResponseEntity<ApiResponse<List<FormFieldConfigResponse>>> getConfigsBySection(
            @PathVariable String sectionKey) {
        try {
            List<FormFieldConfigResponse> responseList =
                    formFieldConfigService.getConfigsBySection(sectionKey);
            return ResponseEntity.ok(
                    ApiResponse.success("Fetched configurations for section: " + sectionKey, responseList));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/form-configs
    // Save a new field configuration.
    //
    // Validation rules enforced in service:
    //   - fieldKey, fieldType, label, sectionKey are required
    //   - Dropdown types (primeng-select, p-select, radio) must have:
    //       apiUrl         → required
    //       requestKeys[]  → at least one entry required
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<FormFieldConfigResponse>> saveConfig(
            @Valid @RequestBody FormFieldConfigRequest request) {
        try {
            FormFieldConfigResponse response = formFieldConfigService.saveConfig(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Form field configuration created successfully", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/form-configs/{id}
    // Update an existing field configuration.
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FormFieldConfigResponse>> updateConfig(
            @PathVariable Long id,
            @Valid @RequestBody FormFieldConfigRequest request) {
        try {
            FormFieldConfigResponse response = formFieldConfigService.updateConfig(id, request);
            return ResponseEntity.ok(
                    ApiResponse.success("Form field configuration updated successfully", response));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/form-configs/{id}
    // Soft-delete a field configuration (sets IS_ACTIVE = false).
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteConfig(@PathVariable Long id) {
        try {
            formFieldConfigService.deleteConfig(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Form field configuration deleted successfully", null));
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/form-configs/section-visibility
    //
    // Count-Based Section Display API.
    //
    // Request:
    //   {
    //     "countValues": {
    //       "itemCount":    3,
    //       "parItemCount": 0
    //     }
    //   }
    //
    // Response:
    //   {
    //     "status":  "SUCCESS",
    //     "data": {
    //       "sectionVisibilities": [
    //         { "sectionKey": "fire", "conditionField": "itemCount",    "currentValue": 3, "isVisible": true  },
    //         { "sectionKey": "par",  "conditionField": "parItemCount", "currentValue": 0, "isVisible": false }
    //       ]
    //     }
    //   }
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/section-visibility")
    public ResponseEntity<ApiResponse<SectionVisibilityResponse>> getSectionVisibility(
            @Valid @RequestBody SectionVisibilityRequest request) {
        try {
            SectionVisibilityResponse response =
                    formFieldConfigService.evaluateSectionVisibility(request);
            return ResponseEntity.ok(
                    ApiResponse.success("Section visibility evaluated successfully", response));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }
    
    @PostMapping("/get/section-count")
    public ResponseEntity<CommonRes> getSectionCount(@RequestBody FormFieldConfigRequest req){
    	CommonRes data = new CommonRes();
    	
    	data = formFieldConfigService.getSectionCount(req);
    	
    	if(data.getIsError() == false) {
    		data.setErrorMessage(Collections.emptyList());
    		data.setMessage("Success");
    		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
    	}else {
    		data.setCommonResponse(null);
    		return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
    	}
    }
    
    @PostMapping("/get/section-list")
    public ResponseEntity<CommonRes> getSectionList(@RequestBody FormFieldConfigRequest req){
    	CommonRes data = new CommonRes();
    	
    	data = formFieldConfigService.getSectionList(req);
    	
    	if(data.getIsError() == false) {
    		data.setErrorMessage(Collections.emptyList());
    		data.setMessage("Success");
    		return new ResponseEntity<CommonRes>(data,HttpStatus.CREATED);
    	}else {
    		data.setCommonResponse(null);
    		return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
    	}
    }
}
