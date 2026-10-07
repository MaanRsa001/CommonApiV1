package com.maan.eway.form.fields;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PortFolioAdminTupleRes;
import com.maan.eway.error.Error;
import com.maan.eway.form.fields.FormFieldConfig.WrapperType;
import com.maan.eway.form.fields.SectionVisibilityResponse.SectionVisibilityItem;
import com.maan.eway.repository.ProductSectionMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

/**
 * FormFieldConfigServiceImpl
 *
 * Implements all business logic for form field configuration management.
 *
 * Flow:
 *   Request → validate → map to DTO → map to Entity → persist
 *   Entity  → map to DTO → map to Response → return
 *
 * All service methods work exclusively with DTO and Request/Response objects.
 * The entity is never exposed outside this class.
 */
@Service
@RequiredArgsConstructor
public class FormFieldConfigServiceImpl implements FormFieldConfigService {

    private static final Set<String> DROPDOWN_TYPES =
            Set.of("primeng-select", "p-select", "radio");

    private final FormFieldConfigRepository repository;
    
    @PersistenceContext
	private EntityManager em;
    
    @Autowired
    private ProductSectionMasterRepository sectionRepo;

    // ── Get All ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<FormFieldConfigResponse> getAllConfigs() {
        List<FormFieldConfig> entities =
                repository.findByIsActiveTrueOrderBySectionKeyAscSortOrderAsc();

        return entities.stream()
                .map(this::mapEntityToDto)
                .map(this::mapDtoToResponse)
                .collect(Collectors.toList());
    }

    // ── Get By ID ─────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public FormFieldConfigResponse getConfigById(Long id) {
        FormFieldConfig entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Form field config not found for ID: " + id));

        FormFieldConfigDto dto = mapEntityToDto(entity);
        return mapDtoToResponse(dto);
    }

    // ── Get By Section ────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<FormFieldConfigResponse> getConfigsBySection(String sectionKey) {
        List<FormFieldConfig> entities =
                repository.findBySectionKeyAndIsActiveTrueOrderBySortOrderAsc(sectionKey);

        return entities.stream()
                .map(this::mapEntityToDto)
                .map(this::mapDtoToResponse)
                .collect(Collectors.toList());
    }

    // ── Save ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FormFieldConfigResponse saveConfig(FormFieldConfigRequest request) {
    	try {
    	//	validateDropdownConfig(request.getFieldType(), request.getApiUrl(), request.getRequestKeys());
        //    validateUniqueFieldKey(request.getFieldKey(), null);


            FormFieldConfigDto dto    = mapRequestToDto(request);
            FormFieldConfig    entity = mapDtoToEntity(dto);
            if(request.getId() != null) {
            	entity.setId(request.getId());
            }else {
            	entity.setId(generateId());
            }
            
            entity.setIsActive(Boolean.TRUE);

            FormFieldConfig saved = repository.save(entity);
            return mapDtoToResponse(mapEntityToDto(saved));
    	}catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
        
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FormFieldConfigResponse updateConfig(Long id, FormFieldConfigRequest request) {
        FormFieldConfig existing = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Form field config not found for ID: " + id));

        validateDropdownConfig(request.getFieldType(), request.getApiUrl(), request.getRequestKeys());
        validateUniqueFieldKey(request.getFieldKey(), id);

        FormFieldConfigDto dto    = mapRequestToDto(request);
        dto.setId(id);
        dto.setCreatedAt(existing.getCreatedAt());
        dto.setIsActive(Boolean.TRUE);

        FormFieldConfig entity  = mapDtoToEntity(dto);
        FormFieldConfig updated = repository.save(entity);
        return mapDtoToResponse(mapEntityToDto(updated));
    }

    // ── Soft Delete ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteConfig(Long id) {
        FormFieldConfig entity = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Form field config not found for ID: " + id));

        entity.setIsActive(Boolean.FALSE);
        repository.save(entity);
    }

    // ── Count-Based Section Visibility ────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public SectionVisibilityResponse evaluateSectionVisibility(SectionVisibilityRequest request) {
        Map<String, Integer> countValues = request.getCountValues();
        List<SectionVisibilityItem> items = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : countValues.entrySet()) {
            String conditionField = entry.getKey();
            int    currentValue   = entry.getValue();

            List<FormFieldConfig> conditionedFields =
                    repository.findByCountConditionField(conditionField);

            // Group by section key; a section is visible if ANY field in it passes its condition
            Map<String, List<FormFieldConfig>> bySection = conditionedFields.stream()
                    .collect(Collectors.groupingBy(FormFieldConfig::getSectionKey));

            bySection.forEach((sectionKey, fields) -> {
                boolean isVisible = fields.stream()
                        .anyMatch(f -> evaluateCondition(
                                currentValue,
                                f.getCountConditionOp(),
                                f.getCountConditionValue()));

                items.add(SectionVisibilityItem.builder()
                        .sectionKey(sectionKey)
                        .conditionField(conditionField)
                        .currentValue(currentValue)
                        .isVisible(isVisible)
                        .build());
            });
        }

        return SectionVisibilityResponse.builder()
                .sectionVisibilities(items)
                .build();
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PRIVATE — Mapping Methods
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Map incoming Request to DTO.
     */
    private FormFieldConfigDto mapRequestToDto(FormFieldConfigRequest request) {
        return FormFieldConfigDto.builder()
                //.id(request.getId())
                .fieldKey(request.getFieldKey())
                .fieldType(request.getFieldType())
                .label(request.getLabel())
                .placeholder(request.getPlaceholder())
                .description(request.getDescription())
                .className(request.getClassName())
                .defaultValue(request.getDefaultValue())
                .inputType(request.getInputType())
                .textareaRows(request.getTextareaRows())
                .isRequired(request.getIsRequired())
                .minLength(request.getMinLength())
                .maxLength(request.getMaxLength())
                .minValue(request.getMinValue())
                .maxValue(request.getMaxValue())
                .pattern(request.getPattern())
                .validators(request.getValidators())
                .validatorParams(request.getValidatorParams())
                .isHidden(request.getIsHidden())
                .isDisabled(request.getIsDisabled())
                .apiUrl(request.getApiUrl())
                .requestKeys(request.getRequestKeys())
                .apiMethod(request.getApiMethod())
                .apiValueKey(request.getApiValueKey())
                .apiLabelKey(request.getApiLabelKey())
                .apiDataPath(request.getApiDataPath())
                .apiDependsOn(request.getApiDependsOn())
                .apiDependsOnParam(request.getApiDependsOnParam())
                .apiHeaders(request.getApiHeaders())
                .apiQueryParams(request.getApiQueryParams())
                .sectionKey(request.getSectionKey())
                .sectionTitle(request.getSectionTitle())
                .sectionSubtitle(request.getSectionSubtitle())
                .sectionIcon(request.getSectionIcon())
                .sectionGradient(request.getSectionGradient())
                .wrapperType(request.getWrapperType())
                .sortOrder(request.getSortOrder())
                .countConditionField(request.getCountConditionField())
                .countConditionOp(request.getCountConditionOp())
                .countConditionValue(request.getCountConditionValue())
                .companyId(request.getCompanyId())
                .productId(request.getProductId())
                .sectionId(request.getSectionId())
                .build();
    }

    /**
     * Map DTO to Entity for persistence.
     */
    private FormFieldConfig mapDtoToEntity(FormFieldConfigDto dto) {
        FormFieldConfig entity = new FormFieldConfig();

        entity.setId(dto.getId());
        entity.setFieldKey(dto.getFieldKey());
        entity.setFieldType(dto.getFieldType());
        entity.setLabel(dto.getLabel());
        entity.setPlaceholder(dto.getPlaceholder());
        entity.setDescription(dto.getDescription());
        entity.setClassName(dto.getClassName());
        entity.setDefaultValue(dto.getDefaultValue());
        entity.setInputType(dto.getInputType() != null ? dto.getInputType() : "text");
        entity.setTextareaRows(dto.getTextareaRows());
        entity.setIsRequired(dto.getIsRequired() != null  ? dto.getIsRequired()  : Boolean.FALSE);
        entity.setMinLength(dto.getMinLength());
        entity.setMaxLength(dto.getMaxLength());
        entity.setMinValue(dto.getMinValue());
        entity.setMaxValue(dto.getMaxValue());
        entity.setPattern(dto.getPattern());
        entity.setValidators(dto.getValidators());
        entity.setValidatorParams(dto.getValidatorParams());
        entity.setIsHidden(dto.getIsHidden()   != null ? dto.getIsHidden()   : Boolean.FALSE);
        entity.setIsDisabled(dto.getIsDisabled()!= null ? dto.getIsDisabled(): Boolean.FALSE);
        entity.setApiUrl(dto.getApiUrl());
        entity.setRequestKeys(dto.getRequestKeys());
        entity.setApiMethod(dto.getApiMethod() != null ? dto.getApiMethod() : "GET");
        entity.setApiValueKey(dto.getApiValueKey());
        entity.setApiLabelKey(dto.getApiLabelKey());
        entity.setApiDataPath(dto.getApiDataPath());
        entity.setApiDependsOn(dto.getApiDependsOn());
        entity.setApiDependsOnParam(dto.getApiDependsOnParam());
        entity.setApiHeaders(dto.getApiHeaders());
        entity.setApiQueryParams(dto.getApiQueryParams());
        entity.setSectionKey(dto.getSectionKey());
        entity.setSectionTitle(dto.getSectionTitle());
        entity.setSectionSubtitle(dto.getSectionSubtitle());
        entity.setSectionIcon(dto.getSectionIcon() != null ? dto.getSectionIcon() : "pi-list");
        entity.setSectionGradient(dto.getSectionGradient());
        entity.setWrapperType(dto.getWrapperType() != null ? dto.getWrapperType() : WrapperType.grid);
        entity.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        entity.setCountConditionField(dto.getCountConditionField());
        entity.setCountConditionOp(dto.getCountConditionOp());
        entity.setCountConditionValue(dto.getCountConditionValue());
        entity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : Boolean.TRUE);
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setCompanyId(dto.getCompanyId());
        entity.setSectionId(dto.getSectionId());
        entity.setProductId(dto.getProductId());

        return entity;
    }

    /**
     * Map Entity to DTO after fetching from DB.
     */
    private FormFieldConfigDto mapEntityToDto(FormFieldConfig entity) {
    	try {
    		return FormFieldConfigDto.builder()
                    .id(entity.getId())
                    .fieldKey(entity.getFieldKey())
                    .fieldType(entity.getFieldType())
                    .label(entity.getLabel())
                    .placeholder(entity.getPlaceholder())
                    .description(entity.getDescription())
                    .className(entity.getClassName())
                    .defaultValue(entity.getDefaultValue())
                    .inputType(entity.getInputType())
                    .textareaRows(entity.getTextareaRows())
                    .isRequired(entity.getIsRequired())
                    .minLength(entity.getMinLength())
                    .maxLength(entity.getMaxLength())
                    .minValue(entity.getMinValue())
                    .maxValue(entity.getMaxValue())
                    .pattern(entity.getPattern())
                    .validators(entity.getValidators())
                    .validatorParams(entity.getValidatorParams())
                    .isHidden(entity.getIsHidden())
                    .isDisabled(entity.getIsDisabled())
                    .apiUrl(entity.getApiUrl())
                    .requestKeys(entity.getRequestKeys())
                    .apiMethod(entity.getApiMethod())
                    .apiValueKey(entity.getApiValueKey())
                    .apiLabelKey(entity.getApiLabelKey())
                    .apiDataPath(entity.getApiDataPath())
                    .apiDependsOn(entity.getApiDependsOn())
                    .apiDependsOnParam(entity.getApiDependsOnParam())
                    .apiHeaders(entity.getApiHeaders())
                    .apiQueryParams(entity.getApiQueryParams())
                    .sectionKey(entity.getSectionKey())
                    .sectionTitle(entity.getSectionTitle())
                    .sectionSubtitle(entity.getSectionSubtitle())
                    .sectionIcon(entity.getSectionIcon())
                    .sectionGradient(entity.getSectionGradient())
                    .wrapperType(entity.getWrapperType())
                    .sortOrder(entity.getSortOrder())
                    .countConditionField(entity.getCountConditionField())
                    .countConditionOp(entity.getCountConditionOp())
                    .countConditionValue(entity.getCountConditionValue())
                    .isActive(entity.getIsActive())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .companyId(entity.getCompanyId())
                    .sectionId(entity.getSectionId())
                    .productId(entity.getProductId())
                    .build();
    	}catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
        
    }

    /**
     * Map DTO to Response for returning to the controller.
     */
    private FormFieldConfigResponse mapDtoToResponse(FormFieldConfigDto dto) {
    	try {
    		return FormFieldConfigResponse.builder()
                    .id(dto.getId())
                    .fieldKey(dto.getFieldKey())
                    .fieldType(dto.getFieldType())
                    .label(dto.getLabel())
                    .placeholder(dto.getPlaceholder())
                    .description(dto.getDescription())
                    .className(dto.getClassName())
                    .defaultValue(dto.getDefaultValue())
                    .inputType(dto.getInputType())
                    .textareaRows(dto.getTextareaRows())
                    .isRequired(dto.getIsRequired())
                    .minLength(dto.getMinLength())
                    .maxLength(dto.getMaxLength())
                    .minValue(dto.getMinValue())
                    .maxValue(dto.getMaxValue())
                    .pattern(dto.getPattern())
                    .validators(dto.getValidators())
                    .validatorParams(dto.getValidatorParams())
                    .isHidden(dto.getIsHidden())
                    .isDisabled(dto.getIsDisabled())
                    .apiUrl(dto.getApiUrl())
                    .requestKeys(dto.getRequestKeys())
                    .apiMethod(dto.getApiMethod())
                    .apiValueKey(dto.getApiValueKey())
                    .apiLabelKey(dto.getApiLabelKey())
                    .apiDataPath(dto.getApiDataPath())
                    .apiDependsOn(dto.getApiDependsOn())
                    .apiDependsOnParam(dto.getApiDependsOnParam())
                    .apiHeaders(dto.getApiHeaders())
                    .apiQueryParams(dto.getApiQueryParams())
                    .sectionKey(dto.getSectionKey())
                    .sectionTitle(dto.getSectionTitle())
                    .sectionSubtitle(dto.getSectionSubtitle())
                    .sectionIcon(dto.getSectionIcon())
                    .sectionGradient(dto.getSectionGradient())
                    .wrapperType(dto.getWrapperType())
                    .sortOrder(dto.getSortOrder())
                    .countConditionField(dto.getCountConditionField())
                    .countConditionOp(dto.getCountConditionOp())
                    .countConditionValue(dto.getCountConditionValue())
                    .createdAt(dto.getCreatedAt())
                    .updatedAt(dto.getUpdatedAt())
                    .companyId(dto.getCompanyId())
                    .sectionId(dto.getSectionId())
                    .productId(dto.getProductId())
                    .build();
    	}catch(Exception e) {
    		e.printStackTrace();
    		return null;
    	}
        
    }

    // ═════════════════════════════════════════════════════════════════════════
    // PRIVATE — Business Validation
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Enforce: dropdown types MUST have apiUrl and at least one requestKey.
     * Static options are not allowed.
     */
    private void validateDropdownConfig(String fieldType, String apiUrl, List<String> requestKeys) {
        if (!DROPDOWN_TYPES.contains(fieldType)) return;

        if (apiUrl == null || apiUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "apiUrl is required for dropdown field type: " + fieldType);
        }
        if (requestKeys == null || requestKeys.isEmpty()) {
            throw new IllegalArgumentException(
                    "requestKeys must contain at least one entry for dropdown field type: " + fieldType);
        }
    }

    /**
     * Enforce unique fieldKey across all active records.
     * Pass excludeId = null on create, pass the existing ID on update.
     */
    private void validateUniqueFieldKey(String fieldKey, Long excludeId) {
        boolean exists = (excludeId == null)
                ? repository.existsByFieldKeyAndIsActiveTrue(fieldKey)
                : repository.existsByFieldKeyAndIsActiveTrueAndIdNot(fieldKey, excludeId);

        if (exists) {
            throw new IllegalArgumentException(
                    "A field with key '" + fieldKey + "' already exists");
        }
    }

    /**
     * Evaluate a count condition.
     *
     * Supported operators:
     *   gt  → currentValue >  threshold
     *   gte → currentValue >= threshold
     *   lt  → currentValue <  threshold
     *   lte → currentValue <= threshold
     *   eq  → currentValue == threshold
     */
    private boolean evaluateCondition(int currentValue, String op, Integer threshold) {
        if (op == null || threshold == null) return true;

        return switch (op.toLowerCase()) {
            case "gt"  -> currentValue >  threshold;
            case "gte" -> currentValue >= threshold;
            case "lt"  -> currentValue <  threshold;
            case "lte" -> currentValue <= threshold;
            case "eq"  -> currentValue == threshold;
            default    -> false;
        };
    }

    /**
     * Generate a simple unique ID using section prefix + UUID fragment.
     */
    private Long generateId() {
    	Long idValue = repository.count();
    	idValue = idValue + 1;
    	
    	return idValue;
    	
    }

	@Override
	public CommonRes getSectionCount(FormFieldConfigRequest req) {
		CommonRes res = new CommonRes();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			
			Root<FormFieldConfig> u = query.from(FormFieldConfig.class);
			
			query.multiselect(cb.count(u).alias("count"),u.get("sectionId").alias("sectionId"),
					u.get("companyId").alias("companyId"),u.get("productId").alias("productId"));
			
			Predicate a1 = cb.equal(u.get("companyId"), req.getCompanyId());
			Predicate a2 = cb.equal(u.get("productId"), req.getProductId());
			
			query.where(a1,a2).groupBy(u.get("sectionId"));
			
			TypedQuery<Tuple> result = em.createQuery(query);
			List<Tuple> tupleList = result.getResultList();
			
			System.out.println("SECTION LIST: "+tupleList);
			
			if(!tupleList.isEmpty()) {
				Map<String,Object> respMap = new HashMap<>();
				respMap.put("companyId", req.getCompanyId());
				respMap.put("productId", req.getProductId());
				List<Map<String,Object>> sectionDetails = new ArrayList();
				
				for(Tuple tuple : tupleList) {
					Map<String,Object> sectionDetail = new HashMap<>();
					String compId = tuple.get("companyId") == null ? null : tuple.get("companyId").toString();
					Integer proId = tuple.get("productId") == null ? null : Integer.valueOf(tuple.get("productId").toString());
					Integer secId = tuple.get("sectionId") == null ? null : Integer.valueOf(tuple.get("sectionId").toString());
					Integer count = tuple.get("count") == null ? null : Integer.valueOf(tuple.get("count").toString());
					List<ProductSectionMaster> sectDetails = sectionRepo.findByProductIdAndSectionIdAndCompanyIdOrderByAmendIdDesc(proId,
							secId,compId);
					
					if(!sectDetails.isEmpty()) {
						sectionDetail.put("count", count);
						sectionDetail.put("sectionId", secId);
						sectionDetail.put("sectionName", sectDetails.get(0).getSectionName());
						
						sectionDetails.add(sectionDetail);
					}else {
						sectionDetail.put("count", count);
						sectionDetail.put("sectionId", secId);
						sectionDetail.put("sectionName", null);
						
						sectionDetails.add(sectionDetail);
					}
					
				}
				
				respMap.put("sectionDetails", sectionDetails);
				
                res.setCommonResponse(respMap);
                res.setIsError(false);	
			}else {
				res.setCommonResponse(null);
                res.setIsError(false);	
			}
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage(e.getMessage());
		}
		return res;
	}

	@Override
	public CommonRes getSectionList(FormFieldConfigRequest req) {
		CommonRes res = new CommonRes();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<FormFieldConfig> query = cb.createQuery(FormFieldConfig.class);
			
			Root<FormFieldConfig> u = query.from(FormFieldConfig.class);
			 query.select(u);
			 
			 Predicate a1 = cb.equal(u.get("companyId"), req.getCompanyId());
			 Predicate a2 = cb.equal(u.get("productId"), req.getProductId());
			 Predicate a3 = cb.equal(u.get("sectionId"), req.getSectionId());
			 
			 List<Order> orderList = new ArrayList<Order>();
			 orderList.add(cb.asc(u.get("sectionId")));
			 
			 query.where(a1,a2,a3).orderBy(orderList);
				
			TypedQuery<FormFieldConfig> result = em.createQuery(query);
			List<FormFieldConfig> sectionList = result.getResultList();
				
			System.out.println("SECTION LIST: "+sectionList);
			
			if(!sectionList.isEmpty()) {
				 res.setCommonResponse(sectionList.stream()
			                .map(this::mapEntityToDto)
			                .map(this::mapDtoToResponse)
			                .collect(Collectors.toList()));
	             res.setIsError(false);		
			}else {
				res.setCommonResponse(null);
                res.setIsError(false);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			res.setCommonResponse(null);
            res.setIsError(true);
            res.setMessage(e.getMessage());
		}
		return res;
	}
}
