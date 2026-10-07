package com.maan.eway.realpay.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PaymentRefno;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.realpay.dto.ClientAccountDetailsDTO;
import com.maan.eway.realpay.dto.UnifiedClientCreationResponseDTO;
import com.maan.eway.realpay.service.ClientAccountDetailsService;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PaymentRefnoRepository;
import com.maan.eway.repository.PersonalInfoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@RestController
@RequestMapping("/realpay")
public class ClientAccountDetailsController {

    @Autowired
    private ClientAccountDetailsService service;

    @Autowired
    private HomePositionMasterRepository homerepo;

    @Autowired
    private PersonalInfoRepository personalrepo;

    @Autowired
    private PaymentRefnoRepository seqRefNorepo;

    @Autowired
    private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepository;

    @PersistenceContext
    private EntityManager em;

    @GetMapping("/{id}")
    public ResponseEntity<ClientAccountDetailsDTO> getClientById(@PathVariable Long id) {
        ClientAccountDetailsDTO dto = service.getClientById(id);
        if (dto == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/wecore2realpaycreation")
    public ResponseEntity<UnifiedClientCreationResponseDTO> createClient(@RequestBody ClientAccountDetailsDTO dto,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser) {

        if (beneficiaryUser != null) {
            dto.setBeneficiaryUser(beneficiaryUser);
        }

        HomePositionMaster data = homerepo.findByQuoteNo(dto.getQuoteNo());
        if (data == null) {
            return ResponseEntity.notFound().build();
        }

        PersonalInfo personaldata = personalrepo.findByCustomerId(data.getCustomerId());
        if (personaldata == null) {
            return ResponseEntity.notFound().build();
        }

        EserviceCustomerDetails idnum = eserviceCustomerDetailsRepository.findByCustomerReferenceNo(
                personaldata.getCustomerReferenceNo());
        if (idnum == null) {
            return ResponseEntity.notFound().build();
        }

        dto.setClientNumber(personaldata.getCustomerReferenceNo());
        dto.setClientName(personaldata.getClientName());
        dto.setIdType("I");
        dto.setIdNumber(idnum.getIdNumber());
        dto.setCellphoneNumber(personaldata.getMobileNo1());
        dto.setEmail(personaldata.getEmail1());
        dto.setCompanyId(data.getCompanyId());

        String refShortCode = getListItem(data.getCompanyId(), data.getBranchCode(), "PAYMENT_REF_SHORTCODE", "1");
        String refno = refShortCode + generateMerchantReferenceNo();
        dto.setMerchantReference(refno);

        String paymentMode = getListItem(data.getCompanyId(), data.getBranchCode(), "PAYMENT_MODE",
                dto.getPaymentType());
        dto.setPaymentTypeDesc(paymentMode);

        try {
            UnifiedClientCreationResponseDTO response = service.createClientWithFullResponse(dto);

            if(dto.getIsFirstInstalmentPaid()!=null){
                /// create policy instantly and change payment status if not paymnet is paid...
                String updatedResponse = service.updateFirstInstalmentPaid(dto);
                response.setFirstInstalmentPaid(updatedResponse);
            }
            if(response.getClientDetails()!=null) response.getClientDetails().setIsFirstInstalmentPaid("YES");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Create error response
            UnifiedClientCreationResponseDTO errorResponse = new UnifiedClientCreationResponseDTO();
            UnifiedClientCreationResponseDTO.ClientCreationStatus status = new UnifiedClientCreationResponseDTO.ClientCreationStatus();
            status.setClientStatus("FAILED");
            status.setContractStatus("NOT_CREATED");
            status.setInstallmentsStatus("NOT_CREATED");
            errorResponse.setStatus(status);
            errorResponse.setMessage("Error creating client: " + e.getMessage());
            errorResponse.setTimestamp(java.time.LocalDateTime.now());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    public synchronized String getListItem(String insuranceId, String branchCode, String itemType, String itemCode) {
        String itemDesc = "";
        List<ListItemValue> list = new ArrayList<ListItemValue>();
        try {
            Date today = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);
            today = cal.getTime();
            Date todayEnd = cal.getTime();

            // Criteria
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
            // Find All
            Root<ListItemValue> c = query.from(ListItemValue.class);

            // Select
            query.select(c);
            // Order By
            List<Order> orderList = new ArrayList<Order>();
            orderList.add(cb.asc(c.get("branchCode")));

            // Effective Date Start Max Filter
            Subquery<Date> effectiveDate = query.subquery(Date.class);
            Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
            effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
            Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
            Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
            Predicate b1 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
            Predicate b2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
            effectiveDate.where(a1, a2, b1, b2);

            // Effective Date End Max Filter
            Subquery<Date> effectiveDate2 = query.subquery(Date.class);
            Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
            effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
            Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
            Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
            Predicate b3 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
            Predicate b4 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
            effectiveDate2.where(a3, a4, b3, b4);

            // Where
            Predicate n1 = cb.equal(c.get("status"), "Y");
            Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
            Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
            Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
            Predicate n5 = cb.equal(c.get("companyId"), "99999");
            Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
            Predicate n7 = cb.equal(c.get("branchCode"), "99999");
            Predicate n8 = cb.or(n4, n5);
            Predicate n9 = cb.or(n6, n7);
            Predicate n10 = cb.equal(c.get("itemType"), itemType);
            Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
            query.where(n1, n2, n3, n8, n9, n10, n11).orderBy(orderList);

            // Get Result
            TypedQuery<ListItemValue> result = em.createQuery(query);
            list = result.getResultList();

            itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return itemDesc;
    }

    public synchronized String generateMerchantReferenceNo() {
        try {
            PaymentRefno entity = new PaymentRefno();
            entity = seqRefNorepo.save(entity);
            return String.format("%05d", entity.getPaymentReferenceNo());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientAccountDetailsDTO> updateClient(@PathVariable Long id,
            @RequestBody ClientAccountDetailsDTO dto) {
        ClientAccountDetailsDTO updated = service.updateClient(id, dto);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        service.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fetchClientInfo")
    public ResponseEntity<?> fetchClientInfo(@RequestParam String quoteId){
        return service.fetchClientInfoForEndorsement(quoteId);
    }
}