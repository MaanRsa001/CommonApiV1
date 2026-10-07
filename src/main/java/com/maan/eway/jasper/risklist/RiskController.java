package com.maan.eway.jasper.risklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/risk")
public class RiskController {

    @Autowired
    private RiskDetailsService service;

    @GetMapping("/details/{quoteNo}")
    public ResponseEntity<RiskDataResponse> getDetails(@PathVariable String quoteNo) {
        return ResponseEntity.ok(service.getDetailsByQuoteNo(quoteNo));
    }
}

