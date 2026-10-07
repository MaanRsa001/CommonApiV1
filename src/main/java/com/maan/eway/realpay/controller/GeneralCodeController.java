// GeneralCodeController.java
package com.maan.eway.realpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.realpay.service.GeneralCodeService;

@RestController
@RequestMapping("/dropdown")
public class GeneralCodeController {

    @Autowired
    public GeneralCodeService generalService;

    @GetMapping("/frequenyCodes")
    public ResponseEntity<CommonRes> getFrequency(@RequestParam String product) {
        CommonRes response = generalService.getFrequencyCodes(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/products")
    public ResponseEntity<CommonRes> getProducts(@RequestParam String product) {
        CommonRes response = generalService.getProducts(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/banks")
    public ResponseEntity<CommonRes> getBanks(@RequestParam String product) {
        CommonRes response = generalService.getBanks(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/trackingCodes")
    public ResponseEntity<CommonRes> getTracking(@RequestParam String product) {
        CommonRes response = generalService.getTracking(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/collection")
    public ResponseEntity<CommonRes> getCollection(@RequestParam String product) {
        CommonRes response = generalService.getCollection(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/bankResponses")
    public ResponseEntity<CommonRes> getBankResponses(@RequestParam String product) {
        CommonRes response = generalService.getBankResponses(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/employeeGroups")
    public ResponseEntity<CommonRes> getEmployeeGroups(@RequestParam String product) {
        CommonRes response = generalService.getEmployeeGroups(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/transactionTypes")
    public ResponseEntity<CommonRes> getTransactionTypes(@RequestParam String product) {
        CommonRes response = generalService.getTransactionTypes(product);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/beneficiaryUsers")
    public ResponseEntity<CommonRes> getBeneficiaryUsers() {
        CommonRes response = generalService.getBeneficiaryUsers();
        return ResponseEntity.ok().body(response);
    }
}