package com.maan.eway.realpay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.maan.eway.realpay.service.ClientAccountDetailsService;
import com.maan.eway.realpay.service.ClientService;
import com.maan.eway.realpay.util.ApiEndPoints;

@RestController
@RequestMapping("/")
public class ClientController {

    @Autowired
    public ClientService clientService;

    @Autowired
    public ClientAccountDetailsService clientAccountDetailsService;

    @PostMapping("clientsNew")
    public ResponseEntity<?> createClient(@RequestParam String product, @RequestBody JsonNode requestWrapper,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser) {
        return ResponseEntity.ok().body(clientService
                .callClient(product, requestWrapper, "post", "client", beneficiaryUser).get("ClientPostResponse"));
    }

    @PutMapping("clientsNew")
    public ResponseEntity<?> updateClient(@RequestParam String product, @RequestBody JsonNode requestWrapper,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser) {
        return ResponseEntity.ok().body(clientService
                .callClient(product, requestWrapper, "put", "client", beneficiaryUser).get("ClientPutResponse"));
    }

    @GetMapping("clientsNew")
    public ResponseEntity<?> getClient(@RequestParam String product,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser)
            throws JsonProcessingException {
        String url = ApiEndPoints.General.client + "/" + product + "?Version=v1&BeneficiaryUser=";
        return ResponseEntity.ok()
                .body(clientService.getClientsNew(url, product, beneficiaryUser).get("ClientGetResponse"));
    }

    @PostMapping("contractsNew")
    public ResponseEntity<?> createContract(@RequestParam String product, @RequestBody JsonNode requestWrapper,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser) {
        return ResponseEntity.ok().body(clientService
                .callClient(product, requestWrapper, "post", "contract", beneficiaryUser).get("ContractPostResponse"));
    }

    @PutMapping("contractsNew")
    public ResponseEntity<?> updateContract(@RequestParam String product, @RequestBody JsonNode requestWrapper,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser) {
        return ResponseEntity.ok().body(clientService
                .callClient(product, requestWrapper, "put", "contract", beneficiaryUser).get("ContractPutResponse"));
    }

    @GetMapping("contractsNew")
    public ResponseEntity<?> getContract(@RequestParam String product,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser)
            throws JsonProcessingException {
        String url = ApiEndPoints.General.contract + "/" + product + "?Version=v1&BeneficiaryUser=";
        return ResponseEntity.ok()
                .body(clientService.getClientsNew(url, product, beneficiaryUser).get("ContractGetResponse"));
    }

    @PostMapping("instalmentsNew")
    public ResponseEntity<?> createInstalment(@RequestParam String product, @RequestBody JsonNode requestWrapper,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser) {
        return ResponseEntity.ok()
                .body(clientService.callClient(product, requestWrapper, "post", "instalment", beneficiaryUser)
                        .get("InstalmentPostResponse"));
    }

    @PutMapping("instalmentsNew")
    public ResponseEntity<?> updateInstalment(@RequestParam String product, @RequestBody JsonNode requestWrapper,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser) {
        return ResponseEntity.ok()
                .body(clientService.callClient(product, requestWrapper, "put", "instalment", beneficiaryUser)
                        .get("InstalmentPutResponse"));
    }

    @GetMapping("instalmentsNew")
    public ResponseEntity<?> getInstalment(@RequestParam String product,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser)
            throws JsonProcessingException {
        String url = ApiEndPoints.General.instalment + "/" + product + "?Version=v1&BeneficiaryUser=";
        return ResponseEntity.ok()
                .body(clientService.getClientsNew(url, product, beneficiaryUser).get("InstalmentGetResponse"));
    }

    @GetMapping("contractChangesReport/{product}")
    public ResponseEntity<?> getContractChangesReport(
            @PathVariable String product,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "v1") String version,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser)
            throws JsonProcessingException {

        String url = String.format("%s/%s?BeneficiaryUser=%s&StartDate=%s&EndDate=%s&Version=%s",
                ApiEndPoints.Reports.contractChangesReport,
                product,
                // clientService.getMerchant(),
                startDate,
                endDate,
                version);

        return ResponseEntity.ok()
                .body(clientService.getClientsNew(url, product, beneficiaryUser).get("ContractChangesGetResponse"));
    }

    @GetMapping("instalmentChangesReport/{product}")
    public ResponseEntity<?> getInstalmentChangesReport(
            @PathVariable String product,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "v1") String version,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser)
            throws JsonProcessingException {

        String url = String.format("%s/%s?BeneficiaryUser=%s&StartDate=%s&EndDate=%s&Version=%s",
                ApiEndPoints.Reports.instalmentChangesReport,
                product,
                // clientService.getMerchant(),
                startDate,
                endDate,
                version);

        // JsonNode response = clientService.getClientsNew(url);

        JsonNode response = clientService.getClientsNew(url, product, beneficiaryUser);
        return ResponseEntity.ok().body(response);
    }

}