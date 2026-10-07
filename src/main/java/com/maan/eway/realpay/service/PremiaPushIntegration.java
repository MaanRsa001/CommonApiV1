package com.maan.eway.realpay.service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.integration.controller.IntegrationController;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.realpay.model.Instalment;
import com.maan.eway.realpay.repository.InstalmentRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class PremiaPushIntegration {

    private static final Logger logger =
            LoggerFactory.getLogger(PremiaPushIntegration.class);

    @Autowired
    private IntegrationController integrationController;

    @Autowired
    private InstalmentRepository instalmentRepository;
    @Autowired
    private HomePositionMasterRepository homePositionMasterRepository;
    @Autowired
    private ReceiptGenerator receiptGenerator;

    @Async
    public void callPremiaPushIntegration(String instalmentReferenceNumber,Boolean receiptGenerate) throws InterruptedException {
        /// Push Generation ...
        String quoteNo = "";

        try {

            Instalment instalment = instalmentRepository
                    .findByInstalmentReferenceNumber(instalmentReferenceNumber);

            if(instalment.getNoOfInstalment() == 1){
                Thread.sleep(5 * 1000L);
                HomePositionMaster homePositionMaster =
                        homePositionMasterRepository.findByQuoteNo(instalment.getQuoteNo());

                quoteNo = homePositionMaster.getQuoteNo();
                PremiaRequest premiaRequest = new PremiaRequest();
                premiaRequest.setQuoteNo(quoteNo);

                ResponseEntity<CommonRes> response =
                        integrationController.pushPremiaIntegeration(premiaRequest);

                if (response == null) {
                    logger.error("Push Integration returned null for quoteNo={}", quoteNo);
                    throw new IllegalStateException("Push Integration response is null");
                }

                if (!response.getStatusCode().is2xxSuccessful()) {
                    logger.error("Push Integration failed. Status={}, Body={}, quoteNo={}",
                            response.getStatusCode(),
                            response.getBody(),
                            quoteNo);
                    throw new IllegalStateException("Push Integration failed with non-2xx status");
                }
                logger.info("Push Integration successful for quoteNo={}, response={}", quoteNo, response.getBody());
            }else{
                logger.info("Push Integration Skipped for installmentReference Number {}. Coz its not first Installment. Current instalment is {}",instalmentReferenceNumber,instalment.getNoOfInstalment());
            }

            if(receiptGenerate &&
                    (instalment.getInstalmentStatus().equals("S")
                            || instalment.getInstalmentStatus().equals("DOUBLE_DEBIT_SUCCESS")
                            || instalment.getInstalmentStatus().equals("EFT_SUCCESS")
                            )){
                /// this will trigger receipt generator ...
                Thread.sleep(30 * 1000L);
                receiptGenerator.callPushIntegrationAndReciptGenerationAPI(instalmentReferenceNumber);
            }

        } catch (Exception e) {
            logger.error("Error during Premia + Receipt flow for instalmentReferenceNumber = {}", instalmentReferenceNumber, e);
            throw new IllegalStateException("Failed to process integration flow", e);
        }
    }
}
