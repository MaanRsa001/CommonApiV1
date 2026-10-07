package com.maan.eway.json.serviceImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.json.dto.AdminControllersRes;
import com.maan.eway.json.dto.AdminControllersRes1;
import com.maan.eway.json.dto.EwayFieldRequest;
import com.maan.eway.json.dto.EwayScreenFieldOptionResponse;
import com.maan.eway.json.dto.EwayScreenSection;
import com.maan.eway.json.dto.EwayScreenSectionFieldResponse;
import com.maan.eway.json.entity.EwayScreenFieldOption;
import com.maan.eway.json.entity.EwayScreenSectionField;
import com.maan.eway.json.entity.EwayScreenSectionFieldId;
import com.maan.eway.json.repo.EwayScreenSectionFieldRepository;
import com.maan.eway.json.servicee.EwayScreenSectionFieldService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class EwayScreenSectionFieldServiceImpl implements EwayScreenSectionFieldService {

	@Autowired
	private EwayScreenSectionFieldRepository repository;

	@Override
	public EwayScreenSectionFieldResponse create(EwayFieldRequest request) {
		try {
		EwayScreenSectionField entity = mapToEntity(request);
		entity.setEntryDate(LocalDateTime.now());
		EwayScreenSectionField saved = repository.save(entity);
        return mapToResponse(saved);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public EwayScreenSectionFieldResponse update(Integer sno, Integer screenId, EwayFieldRequest request) {
		EwayScreenSectionFieldId id = new EwayScreenSectionFieldId(sno, screenId);
		EwayScreenSectionField existing = repository.findById(sno).orElseThrow(
				() -> new EntityNotFoundException("Field not found with sno: " + sno + " and screenId: " + screenId));

		EwayScreenSectionField updated = mapToEntity(request);
		updated.setSno(sno);
		updated.setScreenId(screenId);
		updated.setEntryDate(existing.getEntryDate());
		updated.setUpdatedDate(LocalDateTime.now());

		  EwayScreenSectionField saved = repository.save(updated);
	        return mapToResponse(saved);
	}

	@Override
	public EwayScreenSectionFieldResponse getById(Integer sno, EwayScreenSection req) {
		EwayScreenSectionField entity = repository.findBySnoAndCompanyIdAndProductIdAndSectionId(sno,
				req.getCompanyId(), req.getProductId(), req.getSectionId())
				.orElseThrow(() -> new EntityNotFoundException("Field not found"));

		// Trigger lazy load
		entity.getOptions().size();

		 return mapToResponse(entity);
	}

	  @Override
	    public List<EwayScreenSectionFieldResponse> getAll(EwayScreenSection req) {
	        return repository.findByCompanyIdAndProductIdAndSectionId(
	                        req.getCompanyId(), req.getProductId(), req.getSectionId())
	                .stream().map(this::mapToResponse).collect(Collectors.toList());
	    }

	@Override
	public void delete(Integer sno, Integer screenId) {
		EwayScreenSectionFieldId id = new EwayScreenSectionFieldId(sno, screenId);
		if (!repository.existsById(sno)) {
			throw new EntityNotFoundException("Field not found with sno: " + sno + " and screenId: " + screenId);
		}
		repository.deleteById(sno);
	}

    private EwayScreenSectionField mapToEntity(EwayFieldRequest request) {
        EwayScreenSectionField field = EwayScreenSectionField.builder()
                .ScreenId(request.getScreenId())
                .type(request.getType())
                .label(request.getLabel())
                .name(request.getName())
                .id(request.getId())
                .placeholder(request.getPlaceholder())
                .pattern(request.getPattern())
                .validation(request.getValidation())
                .minLength(request.getMinlength())
                .maxLength(request.getMaxlength())
                .className(request.getClassName())
                .disabled(request.getDisabled())
                .hide(request.getHide())
                .defaultValue(request.getDefaultValue())
                .rows(request.getRows())
                .cols(request.getCols())
                .companyId(request.getCompanyId())
                .productId(request.getProductId())
                .sectionId(request.getSectionId())
                .createdBy(request.getCreatedBy())
                .updatedBy(request.getUpdatedBy())
                .status(request.getStatus())
                .hooks(request.getHooks())
                .required(request.getRequired())
                .coverId(request.getCoverId())
                .coverName(request.getCoverName())
                .ApiKey(request.getApiKey())
                .displayOrder(request.getDisplayOrder())
                .key((request.getApiKey() != null ? request.getApiKey() : "") + 
                        (request.getCoverId() != null ? request.getCoverId() : ""))
                .build();

        if (request.getOptions() != null) {
            List<EwayScreenFieldOption> options = request.getOptions().stream().map(opt ->
                    EwayScreenFieldOption.builder()
                            .codeDesc(opt.getCodeDesc())
                            .field(field)
                            .build()).collect(Collectors.toList());
            field.setOptions(options);
        }
        return field;
    }

    private EwayScreenSectionFieldResponse mapToResponse(EwayScreenSectionField entity) {
        List<EwayScreenFieldOptionResponse> optionResponses = entity.getOptions().stream().map(opt ->
                EwayScreenFieldOptionResponse.builder()
                        .code(opt.getCode())
                        .codeDesc(opt.getCodeDesc())
                        .build()).collect(Collectors.toList());

        return EwayScreenSectionFieldResponse.builder()
        		.sno(entity.getSno())
                .screenId(entity.getScreenId())
                .type(entity.getType())
                .label(entity.getLabel())
                .name(entity.getName())
                .id(entity.getId())
                .placeholder(entity.getPlaceholder())
                .pattern(entity.getPattern())
                .validation(entity.getValidation())
                .minLength(entity.getMinLength())
                .maxLength(entity.getMaxLength())
                .className(entity.getClassName())
                .disabled(entity.getDisabled())
                .hide(entity.getHide())
                .defaultValue(entity.getDefaultValue())
                .rows(entity.getRows())
                .cols(entity.getCols())
                .companyId(entity.getCompanyId())
                .productId(entity.getProductId())
                .sectionId(entity.getSectionId())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .entryDate(entity.getEntryDate())
                .updatedDate(entity.getUpdatedDate())
                .status(entity.getStatus())
                .hooks(entity.getHooks())
                .required(entity.getRequired())
                .coverId(entity.getCoverId())
                .coverName(entity.getCoverName())
                .apiKey(entity.getApiKey())
                .displayOrder(entity.getDisplayOrder())
                .options(optionResponses)
                .key((entity.getApiKey() != null ? entity.getApiKey() : "") + 
                        (entity.getCoverId() != null ? entity.getCoverId() : ""))
                .build();
    }

	@Override
	public AdminControllersRes getAllControllers() {
		AdminControllersRes response = new AdminControllersRes();
		List<AdminControllersRes1> resList = new ArrayList<AdminControllersRes1>();
		try {
			String basePackage = "com.maan.eway";
			
			Reflections reflections = new Reflections(new ConfigurationBuilder()
					.forPackages(basePackage)
					.setScanners(new SubTypesScanner(false),
							new TypeAnnotationsScanner()));
			
			Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(RestController.class);
			if(!controllers.isEmpty()) {
				 System.out.println("Controller classes found:");
				 String projectName = Paths.get("").toAbsolutePath().getFileName().toString();
		            for (Class<?> controller : controllers) {
		                String classMapping = projectName+getClassMapping(controller);
		                Method[] methods = controller.getDeclaredMethods();
		                for (Method method : methods) {
		                    Annotation[] annotations = method.getAnnotations();
		                    for (Annotation annotation : annotations) {
		                    	AdminControllersRes1 m = new AdminControllersRes1();
		                        if (annotation instanceof GetMapping) {
		                            GetMapping getMapping = (GetMapping) annotation;
		                            String[] urls = getMapping.value();
		                            for (String url : urls) {
		                            	m.setPackageName(controller.getPackageName());
		                            	m.setControllerClass(controller.getSimpleName());
		                            	m.setMethodName(method.getName());
		                            	m.setProjectName(projectName);
		                            	m.setMethodType("GET");
		                            	m.setApiURL(classMapping+url);
		                            	resList.add(m);
		                            }
		                        }
		                        if (annotation instanceof PostMapping) {
		                            PostMapping postMapping = (PostMapping) annotation;
		                            String[] urls = postMapping.value();
		                            for (String url : urls) {
		                            	m.setPackageName(controller.getPackageName());
		                            	m.setControllerClass(controller.getSimpleName());
		                            	m.setMethodName(method.getName());
		                            	m.setProjectName(projectName);
		                            	m.setMethodType("POST");
		                            	m.setApiURL(classMapping+url);
		                            	resList.add(m);
		                            }
		                        }
		                    }
		                }
		            }
		            response.setCommonResponse(resList);
		            response.setIsError(false);
		            response.setErrorMessage(Collections.emptyList());
		            response.setMessage("Success");
			}else {
				response.setCommonResponse(Collections.emptyList());
	            response.setIsError(true);
	            response.setMessage("Failed");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	private static String getClassMapping(Class<?> controllerClass) {
        RequestMapping classRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
        if (classRequestMapping != null) {
            String[] classUrls = classRequestMapping.value();
            return classUrls.length > 0 ? classUrls[0] : "";
        }
        return "";
    }
}
