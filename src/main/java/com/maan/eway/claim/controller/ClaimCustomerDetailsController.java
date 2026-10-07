package com.maan.eway.claim.controller;


import com.maan.eway.claim.req.ClaimCustomerSyncReq;
import com.maan.eway.claim.service.ClaimCustomerDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class ClaimCustomerDetailsController {

    @Autowired
    private ClaimCustomerDetailsService claimCustomerDetailsService;

    @PostMapping("/sync-claim")
    public ResponseEntity<String> syncCustomerToClaim(
            @RequestBody ClaimCustomerSyncReq req) {

        String status = claimCustomerDetailsService
                .syncCustomer(req.getCustomerReferenceNo());

        return ResponseEntity.ok(status);
    }
}
