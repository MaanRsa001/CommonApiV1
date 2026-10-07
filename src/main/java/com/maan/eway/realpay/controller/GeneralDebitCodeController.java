package com.maan.eway.realpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.realpay.service.GeneralDebitCodeService;

@RestController
@RequestMapping("/")
public class GeneralDebitCodeController {

    @Autowired
    public GeneralDebitCodeService generalDebitCodeService;

    @GetMapping("debitSequenceTypes")
    public ResponseEntity<?> getDebitSequenceTypes(@RequestParam String product) {
        return ResponseEntity.ok().body(generalDebitCodeService.getDebitSequenceTypes(product));
    }

    @GetMapping("mandateStatuses")
    public ResponseEntity<?> getMandateStatuses(@RequestParam String product) {
        return ResponseEntity.ok().body(generalDebitCodeService.getMandateStatuses(product));
    }

    @GetMapping("adjustmentCategories")
    public ResponseEntity<?> getAdjustmentCategories(@RequestParam String product) {
        return ResponseEntity.ok().body(generalDebitCodeService.getAdjustmentCategories(product));
    }

    @GetMapping("mandateReasonCodes")
    public ResponseEntity<?> getMandateReasonCodes(@RequestParam String product) {
        return ResponseEntity.ok().body(generalDebitCodeService.getMandateReasonCodes(product));
    }

    @GetMapping("mandateHistoryActions")
    public ResponseEntity<?> getMandateHistoryActions(@RequestParam String product) {
        return ResponseEntity.ok().body(generalDebitCodeService.getMandateHistoryActions(product));
    }

    @GetMapping("payoutCancelCodes")
    public ResponseEntity<?> getPayoutCancelCodes(@RequestParam String product) {
        return ResponseEntity.ok().body(generalDebitCodeService.getPayoutCancelCodes(product));
    }
}
