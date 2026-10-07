package com.maan.eway.realpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.maan.eway.realpay.service.MandateService;

@RestController
@RequestMapping("/")
public class MandateController {
    @Autowired
    public MandateService mandateService;

    @PostMapping("mandates")
    public ResponseEntity<?> createClient(@RequestParam String product, @RequestBody JsonNode requestWrapper) {
        return ResponseEntity.ok().body(mandateService.callMandate(product,requestWrapper,"post").get("ClientPostResponse"));
    }

    @PutMapping("mandates")
    public ResponseEntity<?> updateClient(@RequestParam String product, @RequestBody JsonNode requestWrapper) {
        return ResponseEntity.ok().body(mandateService.callMandate(product,requestWrapper,"put").get("ClientPutResponse"));
    }

    @GetMapping("mandates")
    public ResponseEntity<?> getFrequency(@RequestParam String product) {
        return ResponseEntity.ok().body(mandateService.getMandates(product));
    }
}
