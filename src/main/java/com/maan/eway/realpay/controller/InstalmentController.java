	package com.maan.eway.realpay.controller;

import java.util.List;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.realpay.service.InstalmentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maan.eway.realpay.dto.InstalmentDTO;
import com.maan.eway.realpay.service.InstalmentService;

@RestController
@RequestMapping("/instalments")
public class InstalmentController {

    @Autowired
    private InstalmentService service;
    @Autowired
    private InstalmentStatusService instalmentStatusService;

    @GetMapping
    public ResponseEntity<List<InstalmentDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstalmentDTO> getById(@PathVariable Long id) {
        InstalmentDTO dto = service.getById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<InstalmentDTO> create(@RequestBody InstalmentDTO dto) {
        InstalmentDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstalmentDTO> update(@PathVariable Long id, @RequestBody InstalmentDTO dto) {
        InstalmentDTO updated = service.update(id, dto);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cancelFutureInstalments")
    public CommonRes cancelFutureInstalments(@RequestParam String quoteNo){
        return instalmentStatusService.cancelFutureInstalment(quoteNo);
    }

    @PostMapping("/generatePolicy")
    public void generatePolicy(@RequestParam String quoteNo, @RequestParam String paymentId){
         instalmentStatusService.generatePolicy(quoteNo, paymentId);
    }
}
