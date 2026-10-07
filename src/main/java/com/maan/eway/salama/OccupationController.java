package com.maan.eway.salama;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/occupation")
public class OccupationController {

    @Autowired
    private OccupationService service;

    @PostMapping("/list")
    public ResponseEntity<List<OccupationRes>> getOccupationList(@RequestBody OccupationReq req) {

        List<OccupationRes> response = service.getOccupationList(req);

        return ResponseEntity.ok(response);
    }
}
