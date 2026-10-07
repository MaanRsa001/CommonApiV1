package com.maan.eway.salesLead.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.bean.IplcmsListItemValue;
import com.maan.eway.salesLead.Repository.IplcmsListItemValueRepository;
import com.maan.eway.salesLead.Repository.QuoteInformationRepository;
import com.maan.eway.salesLead.bean.EnquiryDetails;
import com.maan.eway.salesLead.bean.QuoteInformationId;
import com.maan.eway.salesLead.bean.QuoteInformationIpclms;
import com.maan.eway.salesLead.req.GetQuotationDetailsReq;
import com.maan.eway.salesLead.req.QuoteInformationDTO;
import com.maan.eway.salesLead.service.QuoteInformationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class QuoteInformationServiceImpl implements QuoteInformationService {

    private final EncryDecryService encryDecryService;
	
    @Autowired
	private IplcmsListItemValueRepository iplcmsListItemValueRepo;
    
	private Logger log = LogManager.getLogger(QuoteInformationServiceImpl.class);

    @Autowired
    private QuoteInformationRepository repository;
    
    @PersistenceContext
    private EntityManager em;

    QuoteInformationServiceImpl(EncryDecryService encryDecryService) {
        this.encryDecryService = encryDecryService;
    }

    @Override
    public QuoteInformationDTO saveOrUpdate(QuoteInformationDTO dto) {
        try {

        	String statusDesc = null;
			if (StringUtils.isNotBlank(dto.getQuoteStatus())) {
				List<IplcmsListItemValue> getList  = iplcmsListItemValueRepo.findByItemType("SU_STATUS");
				statusDesc = getList.stream()
					    .filter(k -> dto.getQuoteStatus().equalsIgnoreCase(k.getItemCode()))
					    .map(k -> String.valueOf(k.getItemValue()))
					    .findFirst()
					    .orElse(null);
			}
			
			List<EnquiryDetails> enquiryList = new ArrayList<EnquiryDetails>();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EnquiryDetails> cq = cb.createQuery(EnquiryDetails.class);
			Root<EnquiryDetails> edRoot = cq.from(EnquiryDetails.class);
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			cq.select(edRoot);
			
			if(StringUtils.isNotBlank(dto.getEnquiryId())) {
				Subquery<Integer> amdMax = cq.subquery(Integer.class);
				Root<EnquiryDetails> amdRoot = amdMax.from(EnquiryDetails.class);
				
				amdMax.select(cb.max(amdRoot.get("amendId")))
					.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
							cb.equal(amdRoot.get("leadId"), edRoot.get("leadId")));
				
				predicates.add(cb.equal(edRoot.get("enquiryId"), dto.getEnquiryId()));
				predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
				Predicate [] predicateArray = new Predicate[predicates.size()];
				predicates.toArray(predicateArray);
				cq.where(predicateArray);
				enquiryList.add(em.createQuery(cq).getSingleResult());
			}
            QuoteInformationIpclms entity = new QuoteInformationIpclms();
        	entity.setEnquiryId(dto.getEnquiryId());
            entity.setQuoteNo(StringUtils.isBlank(dto.getQuoteNo())?getmaxQuoteNo():dto.getQuoteNo());
        	entity.setAmendId(MaxQuoteInfoAmendId(dto.getEnquiryId(),dto.getQuoteNo()));
            entity.setQuotationDescription(dto.getQuotationDescription());
            entity.setSumInsured(dto.getSumInsured());
            entity.setPremiumRate(dto.getPremiumRate());
            entity.setPremiumAmount(dto.getPremiumAmount());
            entity.setTechnicalDiscount(dto.getTechnicalDiscount());
            entity.setAdditionalDiscount(dto.getAdditionalDiscount());
            entity.setQuoteStatus(dto.getQuoteStatus());
            entity.setQuoteStatusDesc(statusDesc);
            entity.setQuoteRemarks(dto.getQuoteRemarks());
            entity.setSalesRemarks(dto.getSalesRemarks());
            entity.setUwRemarks(dto.getUWRemarks());
            entity.setLoginId(dto.getLoginId());
            entity.setEnquiryStringCreatedBy((enquiryList!=null && enquiryList.size()>0)?enquiryList.get(0).getCreatedBy():null);
            entity.setLeadId((enquiryList!=null && enquiryList.size()>0)?enquiryList.get(0).getLeadId():null);
            repository.save(entity);
            return dto;

        } catch (Exception e) {
            log.error("Error while saving/updating quote: ", e);
            throw new RuntimeException("Error while saving/updating quote", e);
        }
    }

    @Override
    public List<QuoteInformationDTO> getQuotationDetails(GetQuotationDetailsReq req) {
    	List<QuoteInformationIpclms> resultList = new ArrayList<QuoteInformationIpclms>();
    	List<QuoteInformationDTO> resList = new ArrayList<QuoteInformationDTO>();
        try{
        	CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteInformationIpclms> cq = cb.createQuery(QuoteInformationIpclms.class);
			Root<QuoteInformationIpclms> edRoot = cq.from(QuoteInformationIpclms.class);
			List<Predicate> predicates = new ArrayList<Predicate>();
			
			cq.select(edRoot);
			
        if(StringUtils.isNotBlank(req.getEnquiryId()) && StringUtils.isNotBlank(req.getQuoteNo())) {
        	Subquery<Integer> amdMax = cq.subquery(Integer.class);
			Root<QuoteInformationIpclms> amdRoot = amdMax.from(QuoteInformationIpclms.class);
			
			amdMax.select(cb.max(amdRoot.get("amendId")))
				.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
						cb.equal(amdRoot.get("quoteNo"), edRoot.get("quoteNo")));
			
			predicates.add(cb.equal(edRoot.get("enquiryId"), req.getEnquiryId()));
			predicates.add(cb.equal(edRoot.get("quoteNo"), req.getQuoteNo()));
			predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
			Predicate [] predicateArray = new Predicate[predicates.size()];
			predicates.toArray(predicateArray);
			cq.where(predicateArray);
			resultList = em.createQuery(cq).getResultList();
		
        }else if(StringUtils.isNotBlank(req.getEnquiryId())) {
        	Subquery<Integer> amdMax = cq.subquery(Integer.class);
			Root<QuoteInformationIpclms> amdRoot = amdMax.from(QuoteInformationIpclms.class);
			
			amdMax.select(cb.max(amdRoot.get("amendId")))
				.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
						cb.equal(amdRoot.get("quoteNo"), edRoot.get("quoteNo")));
			
			predicates.add(cb.equal(edRoot.get("enquiryId"), req.getEnquiryId()));
			predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
			Predicate [] predicateArray = new Predicate[predicates.size()];
			predicates.toArray(predicateArray);
			cq.where(predicateArray);
			resultList = em.createQuery(cq).getResultList();
        }else if(StringUtils.isNotBlank(req.getStatus()) && (StringUtils.isNotBlank(req.getLoginId()) || StringUtils.isNotBlank(req.getSalesLoginId()))) {
        	Subquery<Integer> amdMax = cq.subquery(Integer.class);
			Root<QuoteInformationIpclms> amdRoot = amdMax.from(QuoteInformationIpclms.class);
			
			amdMax.select(cb.max(amdRoot.get("amendId")))
				.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
						cb.equal(amdRoot.get("quoteNo"), edRoot.get("quoteNo")));
			
			predicates.add(cb.equal(edRoot.get("quoteStatus"), req.getStatus()));
			if(StringUtils.isNotBlank(req.getLoginId()))
				predicates.add(cb.equal(edRoot.get("loginId"), req.getLoginId()));
			else
				predicates.add(cb.equal(edRoot.get("enquiryStringCreatedBy"), req.getSalesLoginId()));
			predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
			Predicate [] predicateArray = new Predicate[predicates.size()];
			predicates.toArray(predicateArray);
			cq.where(predicateArray);
			resultList = em.createQuery(cq).getResultList();
        }else {
        	Subquery<Integer> amdMax = cq.subquery(Integer.class);
			Root<QuoteInformationIpclms> amdRoot = amdMax.from(QuoteInformationIpclms.class);
			
			amdMax.select(cb.max(amdRoot.get("amendId")))
				.where(cb.equal(amdRoot.get("enquiryId"), edRoot.get("enquiryId")),
						cb.equal(amdRoot.get("quoteNo"), edRoot.get("quoteNo")));
			
			predicates.add(cb.equal(edRoot.get("amendId"), amdMax));
			Predicate [] predicateArray = new Predicate[predicates.size()];
			predicates.toArray(predicateArray);
			cq.where(predicateArray);
			resultList = em.createQuery(cq).getResultList();
        }
        
        if(resultList!=null && resultList.size()>0) {
        	resultList.forEach(entity -> {
        		QuoteInformationDTO dto = new QuoteInformationDTO();
                dto.setEnquiryId(entity.getEnquiryId());
                dto.setQuoteNo(entity.getQuoteNo());
                dto.setQuotationDescription(entity.getQuotationDescription());
                dto.setSumInsured(entity.getSumInsured());
                dto.setPremiumRate(entity.getPremiumRate());
                dto.setPremiumAmount(entity.getPremiumAmount());
                dto.setTechnicalDiscount(entity.getTechnicalDiscount());
                dto.setAdditionalDiscount(entity.getAdditionalDiscount());
                dto.setQuoteStatus(entity.getQuoteStatus());
                dto.setQuoteRemarks(entity.getQuoteRemarks());
                dto.setSalesRemarks(entity.getSalesRemarks());
                dto.setUWRemarks(entity.getUwRemarks());
                dto.setLeadId(entity.getLeadId());
                resList.add(dto);
        	});
        }
    }catch (Exception e){
		log.error(e);
		e.printStackTrace();
	}
	return resList;
    }

    @Override
    public void delete(String enquiryId, String quoteNo) {
        QuoteInformationId id = new QuoteInformationId();
        id.setEnquiryId(enquiryId);
        id.setQuoteNo(quoteNo);
        repository.deleteById(id);
    }
    
    
    public String getmaxQuoteNo() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<QuoteInformationIpclms> slRoot = cq.from(QuoteInformationIpclms.class);
		Expression<Integer> startIndex = cb.literal(3);
		cq.multiselect(cb.coalesce(cb.sum(cb.max(
				cb.substring(slRoot.get("quoteNo"), startIndex, cb.length(slRoot.get("quoteNo"))).as(Integer.class)), 1),
				1000));
		TypedQuery<Integer> query = em.createQuery(cq);
		Integer value = query.getSingleResult();
		return "Q-" + value;
	}
    
    
    private Integer MaxQuoteInfoAmendId(String enquiryId, String quoteNo) {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
			Root<QuoteInformationIpclms> edRoot = cq.from(QuoteInformationIpclms.class);
			
			cq.select(cb.coalesce(cb.sum(cb.max(edRoot.get("amendId")),1), 0))
			.where(cb.equal(edRoot.get("enquiryId"), enquiryId),
					cb.equal(edRoot.get("quoteNo"), quoteNo));
			
			return em.createQuery(cq).getSingleResult();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
}
