package com.maan.eway.realpay.controller;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.realpay.dto.EftTransactionDetailsDTO;
import com.maan.eway.realpay.service.EftTransactionDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/realpay/eftTransactions")
public class EftTransactionDetailsController {

    @Autowired
    private EftTransactionDetailsService service;

    @PostMapping
    public ResponseEntity<CommonRes> create(@RequestBody EftTransactionDetailsDTO dto) {
        CommonRes res = service.create(dto);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonRes> update(@PathVariable Long id, @RequestBody EftTransactionDetailsDTO dto) {
        CommonRes res = service.update(id, dto);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonRes> getById(@PathVariable Long id) {
        CommonRes res = service.getById(id);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<CommonRes> getAll() {
        CommonRes res = service.getAll();
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonRes> delete(@PathVariable Long id) {
        CommonRes res = service.delete(id);
        return ResponseEntity.ok(res);
    }
}
