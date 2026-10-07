package com.maan.eway.bond.serviceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bond.Dto.BondInformationRequest;
import com.maan.eway.bond.Dto.BondInformationRes;
import com.maan.eway.bond.Dto.GetAllReq;
import com.maan.eway.bond.entity.BondInformation;
import com.maan.eway.bond.repo.BondInformationRepo;
import com.maan.eway.bond.service.BondInformationService;

@Service
public class BondInformationServiceImpl implements BondInformationService {

	@Autowired
	public BondInformationRepo repo;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public BondInformationRes save(BondInformationRequest req) {
		try {
			 BondInformation entity;
			 Optional<BondInformation> optional = repo.findByQuoteNo(req.getQuoteNo());

			    if (optional.isPresent()) {
			     
			        entity = optional.get();
			        updateEntity(entity, req);
			    } else {
			  
			        entity = mapToEntity(req);
			    }

			    repo.save(entity);
			    return new BondInformationRes("Bond Information Saved/Updated Successfully");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new BondInformationRes("Bond Information Failed");
	}

	@Override
	public BondInformationRequest getAllByFilters(GetAllReq req) {
		BondInformation entity = repo.findByCompanyIdAndQuoteNoAndProductId(
				req.getCompanyId(), req.getQuoteNo(), req.getProductId());
		if(entity != null ) {
			return mapToResponse(entity);
		}

		return null;
	}

	private BondInformation mapToEntity(BondInformationRequest req) throws NumberFormatException, ParseException {
		return BondInformation.builder()
                .quoteNo(req.getQuoteNo())
                .productId(req.getProductId())
                .companyId(req.getCompanyId())
                .locationId(req.getLocationId())
                .principalName(req.getPrincipalName())
                .businessRegnNo(req.getBusinessRegnNo())
                .typeOfBusiness(req.getTypeOfBusiness())
                .industry(req.getIndustry())
                .yearsBusiness(Integer.parseInt(req.getYearsBusiness()))
                .projectName(req.getProjectName())
                .projectDesc(req.getProjectDesc())
                .projectAddress(req.getProjectAddress())
                .projectLocation(req.getProjectLocation())
                .nameOfObligee(req.getNameOfObligee())
                .projectOwnerType(req.getProjectOwnerType())
                .projectOwnerTypeDesc(req.getProjectOwnerTypeDesc())
                .contractNo(req.getContractNo())
                .projectStartDate(sdf.parse(req.getProjectStartDate()))
                .projectEndDate(sdf.parse(req.getProjectEndDate()))
                .totalContractValue(Integer.parseInt(req.getTotalContractValue()))
                .liquidateDamagesClause(req.getLiquidateDamagesClause())
                .liquidateDamagesClauDesc(req.getLiquidateDamagesClauDesc())
                .advancePaymentClause(req.getAdvancePaymentClause())
                .advancePaymentClauseDesc(req.getAdvancePaymentClauseDesc())
                .advancePaymentValue(Integer.parseInt(req.getAdvancePaymentValue()))
                .bondDuration(req.getBondDuration())
                .formOfBond(req.getFormOfBond())
                .formOfBondDesc(req.getFormOfBondDesc())
                .wordingProvider(req.getWordingProvider())
                .wordingProviderDesc(req.getWordingProviderDesc())
                .jurdication(req.getJurdication())
                .governingLaw(req.getGoverningLaw())
                .collateralFixedDepost(Integer.parseInt(req.getCollateralFixedDepost()))
                .collateralProperty(Integer.parseInt(req.getCollateralProperty()))
                .collateralCorporate(Integer.parseInt(req.getCollateralCorporate()))
                .collateralPersonal(Integer.parseInt(req.getCollateralPersonal()))
                .endorsementYn(req.getEndorsementYn())
                .endorsementDate(StringUtils.isBlank(req.getEndorsementDate())?null:sdf.parse(req.getEndorsementDate()))
                .endorsementEffectiveDate(StringUtils.isBlank(req.getEndorsementEffectiveDate())?null:sdf.parse(req.getEndorsementEffectiveDate()))
                .endorsementRemarks(req.getEndorsementRemarks())
                .endorsementType(req.getEndorsementType())
                .endorsementTypeDesc(req.getEndorsementTypeDesc())
                .endtCategoryDesc(req.getEndtCategoryDesc())
                .endtCount(req.getEndtCount())
                .endtPrevPolicyNo(req.getEndtPrevPolicyNo())
                .endtPrevQuoteNo(req.getEndtPrevQuoteNo())
                .endtStatus(req.getEndtStatus())
                .isFinanceEndt(req.getIsFinanceEndt())
                .orginalPolicyNo(req.getOrginalPolicyNo())
                .entryDate(new Date())
                .loginId(req.getLoginId())
                .status(req.getStatus())
                .remarks(req.getRemarks())
                .build();
    }
	
	 private BondInformationRequest mapToResponse(BondInformation entity) {
	        return BondInformationRequest.builder()
	                .requestReferenceNo(entity.getRequestReferenceNo())
	                .quoteNo(entity.getQuoteNo())
	                .productId(entity.getProductId())
	                .companyId(entity.getCompanyId())
	                .locationId(entity.getLocationId())
	                .principalName(entity.getPrincipalName())
	                .businessRegnNo(entity.getBusinessRegnNo())
	                .typeOfBusiness(entity.getTypeOfBusiness())
	                .industry(entity.getIndustry())
	                .yearsBusiness(entity.getYearsBusiness()==null?"":entity.getYearsBusiness().toString())
	                .projectName(entity.getProjectName())
	                .projectDesc(entity.getProjectDesc())
	                .projectAddress(entity.getProjectAddress())
	                .projectLocation(entity.getProjectLocation())
	                .nameOfObligee(entity.getNameOfObligee())
	                .projectOwnerType(entity.getProjectOwnerType())
	                .projectOwnerTypeDesc(entity.getProjectOwnerTypeDesc())
	                .contractNo(entity.getContractNo())
	                .projectStartDate(sdf.format(entity.getProjectStartDate()))
	                .projectEndDate(entity.getProjectEndDate()==null?null:sdf.format(entity.getProjectEndDate()))
	                .totalContractValue(entity.getTotalContractValue()==null?"":entity.getTotalContractValue().toString())
	                .liquidateDamagesClause(entity.getLiquidateDamagesClause())
	                .liquidateDamagesClauDesc(entity.getLiquidateDamagesClauDesc())
	                .advancePaymentClause(entity.getAdvancePaymentClause())
	                .advancePaymentClauseDesc(entity.getAdvancePaymentClauseDesc())
	                .advancePaymentValue(entity.getAdvancePaymentValue()==null?"":entity.getAdvancePaymentValue().toString())
	                .bondDuration(entity.getBondDuration())
	                .formOfBond(entity.getFormOfBond())
	                .formOfBondDesc(entity.getFormOfBondDesc())
	                .wordingProvider(entity.getWordingProvider())
	                .wordingProviderDesc(entity.getWordingProviderDesc())
	                .jurdication(entity.getJurdication())
	                .governingLaw(entity.getGoverningLaw())
	                .collateralFixedDepost(entity.getCollateralFixedDepost()==null?"":entity.getCollateralFixedDepost().toString())
	                .collateralProperty(entity.getCollateralProperty()==null?"":entity.getCollateralProperty().toString())
	                .collateralCorporate(entity.getCollateralCorporate()==null?"":entity.getCollateralCorporate().toString())
	                .collateralPersonal(entity.getCollateralPersonal()==null?"":entity.getCollateralPersonal().toString())
	                .endorsementYn(entity.getEndorsementYn())
	                .endorsementDate(entity.getEndorsementDate()==null?null:sdf.format(entity.getEndorsementDate()))
	                .endorsementEffectiveDate(entity.getEndorsementEffectiveDate()==null?null:sdf.format(entity.getEndorsementEffectiveDate()))
	                .endorsementRemarks(entity.getEndorsementRemarks())
	                .endorsementType(entity.getEndorsementType())
	                .endorsementTypeDesc(entity.getEndorsementTypeDesc())
	                .endtCategoryDesc(entity.getEndtCategoryDesc())
	                .endtCount(entity.getEndtCount())
	                .endtPrevPolicyNo(entity.getEndtPrevPolicyNo())
	                .endtPrevQuoteNo(entity.getEndtPrevQuoteNo())
	                .endtStatus(entity.getEndtStatus())
	                .isFinanceEndt(entity.getIsFinanceEndt())
	                .orginalPolicyNo(entity.getOrginalPolicyNo())
	                .loginId(entity.getLoginId())
	                .status(entity.getStatus())
	                .remarks(entity.getRemarks())
	                .build();
	    }
	 
	 private void updateEntity(BondInformation entity, BondInformationRequest req) throws ParseException {
		    entity.setProductId(req.getProductId());
		    entity.setCompanyId(req.getCompanyId());
		    entity.setLocationId(req.getLocationId());
		    entity.setPrincipalName(req.getPrincipalName());
		    entity.setBusinessRegnNo(req.getBusinessRegnNo());
		    entity.setTypeOfBusiness(req.getTypeOfBusiness());
		    entity.setIndustry(req.getIndustry());
		    entity.setYearsBusiness(Integer.parseInt(req.getYearsBusiness()));
		    entity.setProjectName(req.getProjectName());
		    entity.setProjectDesc(req.getProjectDesc());
		    entity.setProjectAddress(req.getProjectAddress());
		    entity.setProjectLocation(req.getProjectLocation());
		    entity.setNameOfObligee(req.getNameOfObligee());
		    entity.setProjectOwnerType(req.getProjectOwnerType());
		    entity.setProjectOwnerTypeDesc(req.getProjectOwnerTypeDesc());
		    entity.setContractNo(req.getContractNo());
		    entity.setProjectStartDate(sdf.parse(req.getProjectStartDate()));
		    entity.setProjectEndDate(sdf.parse(req.getProjectEndDate()));
		    entity.setTotalContractValue(Integer.parseInt(req.getTotalContractValue()));
		    entity.setLiquidateDamagesClause(req.getLiquidateDamagesClause());
		    entity.setLiquidateDamagesClauDesc(req.getLiquidateDamagesClauDesc());
		    entity.setAdvancePaymentClause(req.getAdvancePaymentClause());
		    entity.setAdvancePaymentClauseDesc(req.getAdvancePaymentClauseDesc());
		    entity.setAdvancePaymentValue(Integer.parseInt(req.getAdvancePaymentValue()));
		    entity.setBondDuration(req.getBondDuration());
		    entity.setFormOfBond(req.getFormOfBond());
		    entity.setFormOfBondDesc(req.getFormOfBondDesc());
		    entity.setWordingProvider(req.getWordingProvider());
		    entity.setWordingProviderDesc(req.getWordingProviderDesc());
		    entity.setJurdication(req.getJurdication());
		    entity.setGoverningLaw(req.getGoverningLaw());
		    entity.setCollateralFixedDepost(Integer.parseInt(req.getCollateralFixedDepost()));
		    entity.setCollateralProperty(Integer.parseInt(req.getCollateralProperty()));
		    entity.setCollateralCorporate(Integer.parseInt(req.getCollateralCorporate()));
		    entity.setCollateralPersonal(Integer.parseInt(req.getCollateralPersonal()));
		    entity.setEndorsementYn(req.getEndorsementYn());
		    entity.setEndorsementDate(StringUtils.isBlank(req.getEndorsementDate()) ? null : sdf.parse(req.getEndorsementDate()));
		    entity.setEndorsementEffectiveDate(StringUtils.isBlank(req.getEndorsementEffectiveDate()) ? null : sdf.parse(req.getEndorsementEffectiveDate()));
		    entity.setEndorsementRemarks(req.getEndorsementRemarks());
		    entity.setEndorsementType(req.getEndorsementType());
		    entity.setEndorsementTypeDesc(req.getEndorsementTypeDesc());
		    entity.setEndtCategoryDesc(req.getEndtCategoryDesc());
		    entity.setEndtCount(req.getEndtCount());
		    entity.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
		    entity.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
		    entity.setEndtStatus(req.getEndtStatus());
		    entity.setIsFinanceEndt(req.getIsFinanceEndt());
		    entity.setOrginalPolicyNo(req.getOrginalPolicyNo());
		    entity.setEntryDate(new Date());  
		    entity.setLoginId(req.getLoginId());
		    entity.setStatus(req.getStatus());
		    entity.setRemarks(req.getRemarks());
		}

	 
	 

	

}

