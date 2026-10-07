package com.maan.eway.excelupload.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.excelupload.bean.TemplateEntity;
import com.maan.eway.excelupload.dto.ColumnDef;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelTemplateGenerator {
    private final ObjectMapper objectMapper;

    public byte[] generateXlsx(TemplateEntity template) throws IOException {
        List<ColumnDef> cols = objectMapper.readValue(
            template.getColumnsJson(),
            new TypeReference<List<ColumnDef>>(){}
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Workbook wb = new Workbook(out, "template", "1.0")) {
            Worksheet ws = wb.newWorksheet("Template");
            int r = 0;
            int c = 0;
            for (ColumnDef cd : cols) {
                ws.value(r, c++, cd.getHeaderName());
            }
            wb.finish();
        }
        return out.toByteArray();
    }
}

