package com.maan.eway.realpay.service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.WntAcntDtl;
import com.maan.eway.realpay.dto.EndorsementResBean;
import com.maan.eway.realpay.dto.GetPolicyDetailsReqDto;
import com.maan.eway.realpay.dto.ReceiptEntryReqDto;
import com.maan.eway.realpay.model.ClientAccountDetails;
import com.maan.eway.realpay.model.Instalment;
import com.maan.eway.realpay.repository.ClientAccountDetailsRepository;
import com.maan.eway.realpay.repository.InstalmentRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.WntAcntDtlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiptGenerator {

        @Autowired
        private InstalmentRepository instalmentRepository;
        @Autowired
        private HomePositionMasterRepository homePositionMasterRepository;
        @Autowired
        private ReceiptEntryService receiptEntryService;
        @Autowired
        private ClientAccountDetailsRepository clientAccountDetailsRepository;
        @Autowired
        private PolicyDetailsFromFinance policyDetailsFromFinance;
        @Autowired
        WntAcntDtlRepository wntAcntDtlRepository;

        private static final Logger logger =
                LoggerFactory.getLogger(com.maan.eway.realpay.service.ReceiptGenerator.class);


        public void callPushIntegrationAndReciptGenerationAPI(String installmentReferenceNumber) {
            try {

                logger.info("Receipt generation started for {} at {}", installmentReferenceNumber,LocalDateTime.now());

                Instalment instalment = instalmentRepository
                        .findByInstalmentReferenceNumber(installmentReferenceNumber);

                HomePositionMaster homePositionMaster =
                        homePositionMasterRepository.findByQuoteNo(instalment.getQuoteNo());

                List<ClientAccountDetails> clientAccountDetails =
                        clientAccountDetailsRepository.findByClientNumber(instalment.getClientNumber());


                GetPolicyDetailsReqDto getPolicyDetailsReqDto = new GetPolicyDetailsReqDto().constructDto(homePositionMaster.getPolicyNo(),
                        instalment.getQuoteNo(),!clientAccountDetails.isEmpty() ? clientAccountDetails.get(0).getCompanyId() : "100050",
                        String.valueOf(homePositionMaster.getProductId()));

                /// Fetching info from Finance ...
                EndorsementResBean endorsementResBean = policyDetailsFromFinance.callGetPolicyDetails(getPolicyDetailsReqDto);

                Optional<WntAcntDtl> customerPremiums =
                        wntAcntDtlRepository.findCustomerPremiumNative(
                                homePositionMaster.getPolicyNo(),
                                "CUSTOMER_PREMIUM",
                                endorsementResBean.getAccountType(),
                                Math.toIntExact(instalment.getNoOfInstalment())
                        );
                if (customerPremiums.isEmpty()) {
                    logger.error("No record available in finance for policy {} for Receipt generation"
                            ,homePositionMaster.getPolicyNo());
                    return;
                }

                Optional<WntAcntDtl> receiptAccount =
                        wntAcntDtlRepository.findReceiptAccount(
                                homePositionMaster.getPolicyNo(),
                                endorsementResBean.getReceiptAccountType(),
                                Math.toIntExact(instalment.getNoOfInstalment())
                        );

                if (receiptAccount.isPresent()) {
                    logger.info("Receipt is already generated {} for instalment {}", homePositionMaster.getPolicyNo(),
                            Math.toIntExact(instalment.getNoOfInstalment()));
                    return;
                }else{
                    ReceiptEntryReqDto dto = new ReceiptEntryReqDto().constructRecepitEntry(
                            homePositionMaster.getLoginId(),
                            homePositionMaster.getPolicyNo(),
                            instalment.getQuoteNo(),
                            String.valueOf(instalment.getNoOfInstalment()),
                            String.valueOf(instalment.getInstalmentAmount()),
                            instalment.getPaymentId(),
                            !clientAccountDetails.isEmpty() ? clientAccountDetails.get(0).getCompanyId() : "100050",
                            String.valueOf(homePositionMaster.getProductId())
                    );

                    receiptEntryService.callRecieptEntry(dto);
                }

                logger.info("Receipt generation ended for {} at {}", installmentReferenceNumber, LocalDateTime.now());

            } catch (Exception e) {
                logger.error("Error in receipt generation", e);
            }
        }
}
