package com.maan.eway.SectionCoverMaster;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/master")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping("/SectionCoverDetails")
    public SectionResponseDto getAllSectionCoverDetails(@RequestBody SectionRequestDto request) {
        return sectionService.getAllSectionCoverDetails(request);
    }
}