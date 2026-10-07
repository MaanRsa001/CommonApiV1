package com.maan.eway.finanaceIntegration.service.impl;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.EwayDivisionDepartment;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.WnmUwAccountSetup;
import com.maan.eway.finanaceIntegration.service.FilterSpecificationsBuilder;
import com.maan.eway.finanaceIntegration.service.WnmUwAccountSetupService;
import com.maan.eway.finanaceIntegration.res.*;
import com.maan.eway.repository.WnmUwAccountSetupRepository;
import com.maan.eway.finanaceIntegration.req.CoverReq;
import com.maan.eway.finanaceIntegration.req.SectionReq;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.EwayDivisionDepartmentRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ProductMasterRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WnmUwAccountSetupServiceImpl implements WnmUwAccountSetupService {

    @Autowired
    private WnmUwAccountSetupRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProductMasterRepository productMasterRepository;

    @Autowired
    private InsuranceCompanyMasterRepository insuranceCompanyMasterRepository;

    @Autowired
    BranchMasterRepository branchMasterRepository;

    @Autowired
    EwayDivisionDepartmentRepository ewayDivisionDepartmentRepository;

    @Override
    public List<WnmUwAccountSetupRes> filterData(WnmUwAccountSetupRes request) {
        List<WnmUwAccountSetupRes> resList = new ArrayList<WnmUwAccountSetupRes>();

        try {
            Specification<WnmUwAccountSetup> spec =
                    new FilterSpecificationsBuilder<WnmUwAccountSetup>()
                            .with(request)
                            .build();

            List<WnmUwAccountSetup> datas = repository.findAll(spec);

            if(datas.isEmpty()) {
                return Collections.emptyList();
            } else {
                for (WnmUwAccountSetup data : datas) {
                    WnmUwAccountSetupRes res = new WnmUwAccountSetupRes();
                    res = modelMapper.map(data, WnmUwAccountSetupRes.class);
                    resList.add(res);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        return resList;
    }

    @Override
    public WnmUwAccountSetupRes create(WnmUwAccountSetupRes request) {
        WnmUwAccountSetup entity = modelMapper.map(request, WnmUwAccountSetup.class);

        // Set default values if not provided
        if (entity.getAdCustRvPvYn() == null) {
            entity.setAdCustRvPvYn("N");
        }
        if (entity.getAdAutomatchYn() == null) {
            entity.setAdAutomatchYn("N");
        }

        // @PrePersist will handle createdDate automatically
        WnmUwAccountSetup saved = repository.save(entity);
        return modelMapper.map(saved, WnmUwAccountSetupRes.class);
    }

    @Override
    public WnmUwAccountSetupRes update(WnmUwAccountSetupRes request) {
        Optional<WnmUwAccountSetup> existingOpt = repository.findById(request.getPasSysId());

        if (existingOpt.isPresent()) {
            WnmUwAccountSetup existing = existingOpt.get();

            // Preserve creation info (these should not be changed during update)
            String originalCreator = existing.getCreatedBy();
            java.util.Date originalCreateDate = existing.getCreatedDate();

            // Update entity with new values (Partial update/PATCH behavior)
            modelMapper.map(request, existing);

            // Restore creation info to prevent overwriting
            existing.setCreatedBy(originalCreator);
            existing.setCreatedDate(originalCreateDate);

            // @PreUpdate will handle updatedDate automatically
            WnmUwAccountSetup updated = repository.save(existing);
            return modelMapper.map(updated, WnmUwAccountSetupRes.class);
        }

        return null; // Record not found
    }

    @Override
    public boolean delete(Long pasSysId) {
        if (repository.existsById(pasSysId)) {
            repository.deleteById(pasSysId);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductDTO> getActiveProducts() {
        Date currentDate = new Date();

        List<ProductMaster> activeProducts = productMasterRepository
                .findByEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
                        currentDate, currentDate);

        return activeProducts.stream()
                .map(product -> new ProductDTO(
                        product.getProductId(),
                        product.getProductName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SectionDTO> getSectionsByProductRangeAndCompany(SectionReq request) {
        Long minProductId = request.getMinProductId() != null ? request.getMinProductId() : 1L;
        Long maxProductId = request.getMaxProductId() != null ? request.getMaxProductId() : 9999999L;

        List<Object[]> results = repository.findDistinctSectionsByProductRangeAndCompany(
                minProductId,
                maxProductId,
                request.getCompanyId()
        );

        return results.stream()
                .map(row -> new SectionDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CoverDTO> getCoversByProductRangeAndCompany(CoverReq request) {
        Long minProductId = request.getMinProductId() != null ? request.getMinProductId() : 1L;
        Long maxProductId = request.getMaxProductId() != null ? request.getMaxProductId() : 9999999L;

        List<Object[]> results = repository.findDistinctCoversByProductRangeAndCompany(
                minProductId,
                maxProductId,
                request.getCompanyId()
        );

        return results.stream()
                .map(row -> new CoverDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyMasterRes> getCompany(CompanyMasterRes request) {
        Date currentDate = new Date();

        List<InsuranceCompanyMaster> results = insuranceCompanyMasterRepository.findByCompanyIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
                request.getCompanyId(), currentDate, currentDate
        );

        return results.stream()
                .map(row -> new CompanyMasterRes(
                        row.getCompanyId(),
                        row.getCompanyName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<BranchMasterAccRes> getBranches(CompanyMasterRes request) {

        Date currentDate = new Date();

        List<BranchMaster> results = branchMasterRepository
                .findDistinctByCompanyIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
                        request.getCompanyId(),
                        currentDate,
                        currentDate
                );

        return results.stream()
                .map(row -> new BranchMasterAccRes(
                        row.getBranchCode(),
                        row.getBranchName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentRes> getDepartments(CompanyMasterRes request) {

        List<EwayDivisionDepartment> results = ewayDivisionDepartmentRepository
                .findDistinctByCompnayId(request.getCompanyId());

        return results.stream()
                .map(row -> new DepartmentRes(
                        row.getDepartmentCode(),
                        row.getDepartmentName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<NarrationResponse> getNarrationDetails(
            String narrationAcntType,
            String narrationAcntSubType) {

        List<Object[]> rows = repository.getNarrationDetails(narrationAcntType, narrationAcntSubType);

        List<NarrationResponse> responseList = new ArrayList<>();

        for (Object[] row : rows) {

            Map<String, String> replaceMap = new LinkedHashMap<>();

            replaceMap.put("replaceText1", (String) row[1]);
            replaceMap.put("replaceText2", (String) row[2]);
            replaceMap.put("replaceText3", (String) row[3]);
            replaceMap.put("replaceText4", (String) row[4]);
            replaceMap.put("replaceText5", (String) row[5]);
            replaceMap.put("replaceText6", (String) row[6]);
            replaceMap.put("replaceText7", (String) row[7]);
            replaceMap.put("replaceText8", (String) row[8]);
            replaceMap.put("replaceText9", (String) row[9]);
            replaceMap.put("replaceText10", (String) row[10]);

            NarrationResponse res = new NarrationResponse();
            res.setNarrationDesc((String) row[0]);
            res.setReplaceText(replaceMap);

            responseList.add(res);
        }

        return responseList;
    }

}