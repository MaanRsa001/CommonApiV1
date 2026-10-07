package com.maan.eway.mtpintegration.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

@Component
public class MotorMasterDataWriter {

    @PersistenceContext
    private EntityManager em;

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(MotorMasterDataWriter.class);

    @Transactional
    public String insertMotorMakeWithMaxId(String insuranceId, String makeDesc) {
        log.error("AUTO MAKE INSERT TRIGGERED | insuranceId={} | makeDesc='{}'", insuranceId, makeDesc);

        try {
            Integer nextMakeId = getNextMakeId();
            log.error("AUTO MAKE INSERT | nextMakeId={}", nextMakeId);

            MotorMakeMaster make = new MotorMakeMaster();
            make.setMakeId(nextMakeId);
            make.setCompanyId(insuranceId);
            make.setBranchCode("99999");
            make.setMakeNameEn(makeDesc.trim());
            make.setMakeNameLocal(makeDesc.trim());
            make.setStatus("Y");
            make.setRemarks("AUTO INSERTED FROM TIRA");
            make.setCreatedBy("SYSTEM_TIRA");
            make.setUpdatedBy("SYSTEM_TIRA");
            make.setRegulatoryCode("30873");
            make.setCoreAppCode("30873");
            make.setAmendId(0);

            Date now = new Date();
            make.setEntryDate(now);
            make.setUpdatedDate(now);
            make.setEffectiveDateStart(now);

            Calendar cal = Calendar.getInstance();
            cal.set(2049, Calendar.DECEMBER, 31);
            make.setEffectiveDateEnd(cal.getTime());

            em.persist(make);
            em.flush();
            log.error("AUTO MAKE INSERT | flush() SUCCESS | makeId={}", nextMakeId);

            return nextMakeId.toString();
        } catch (Exception e) {
            log.error("AUTO MAKE INSERT FAILED | insuranceId={} | makeDesc='{}'", insuranceId, makeDesc, e);
            return null;
        }
    }

    @Transactional
    public String insertMotorModelWithMaxId(String insuranceId, String makeId, String modelDesc, String makeNameEn) {
        log.error("AUTO MODEL INSERT TRIGGERED | insuranceId={} | makeId={} | modelDesc='{}'",
                insuranceId, makeId, modelDesc);

        try {
            Integer nextModelId = getNextModelId();

            MotorMakeModelMaster model = new MotorMakeModelMaster();
            model.setMakeId(Integer.valueOf(makeId));
            model.setMakeNameEn(makeNameEn);
            model.setModelId(nextModelId);
            model.setBodyId(0); // placeholder, confirm this is safe in your data
            model.setCompanyId(insuranceId);
            model.setBranchCode("99999");
            model.setAmendId(0);
            model.setModelNameEn(modelDesc.trim());
            model.setModelNameLocal(modelDesc.trim());
            model.setStatus("Y");
            model.setRemarks("AUTO INSERTED FROM TIRA");
            model.setCreatedBy("SYSTEM_TIRA");
            model.setUpdatedBy("SYSTEM_TIRA");

            Date now = new Date();
            model.setEntryDate(now);
            model.setUpdatedDate(now);
            model.setEffectiveDateStart(now);

            Calendar cal = Calendar.getInstance();
            cal.set(2049, Calendar.DECEMBER, 31);
            model.setEffectiveDateEnd(cal.getTime());

            em.persist(model);
            em.flush();
            log.error("AUTO MODEL INSERT | flush() SUCCESS | modelId={}", nextModelId);

            return nextModelId.toString();
        } catch (Exception e) {
            log.error("AUTO MODEL INSERT FAILED | insuranceId={} | makeId={} | modelDesc='{}'",
                    insuranceId, makeId, modelDesc, e);
            return null;
        }
    }

    private Integer getNextMakeId() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<MotorMakeMaster> root = cq.from(MotorMakeMaster.class);
        Expression<Long> maxExpr = cb.max(root.get("makeId").as(Long.class));
        cq.select(cb.coalesce(maxExpr, 0L));
        Long maxId = em.createQuery(cq).getSingleResult();
        return maxId.intValue() + 1;
    }

    private Integer getNextModelId() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<MotorMakeModelMaster> root = cq.from(MotorMakeModelMaster.class);
        Expression<Long> maxExpr = cb.max(root.get("modelId").as(Long.class));
        cq.select(cb.coalesce(maxExpr, 0L));
        Long maxId = em.createQuery(cq).getSingleResult();
        return maxId.intValue() + 1;
    }
}
