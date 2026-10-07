package com.maan.eway.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.payment.service.impl.SelcomPaymentImpl;

@RestController
@RequestMapping("/api/rsta")
public class RSTAController {

    @Autowired
    private SelcomPaymentImpl selcomPaymentImpl;

    @PostMapping("/push")
    public ResponseEntity<?> callRSTAIntegration(
            @RequestParam("quoteNo") String quoteNo) {

        try {
            selcomPaymentImpl.callRSTAIntegeration(quoteNo);

            return ResponseEntity.ok(
                    "RSTA integration completed successfully for quoteNo: " + quoteNo );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("RSTA integration failed: " + e.getMessage());
        }
    }
}