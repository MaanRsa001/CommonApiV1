package com.maan.eway.excelupload.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.excelupload.bean.TemplateEntity;
import com.maan.eway.excelupload.dto.AdditionalInformationRequest;
import com.maan.eway.excelupload.dto.ColumnDef;
import com.maan.eway.excelupload.dto.ContentItem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExcelAdditionalInfoService {

    private final ObjectMapper objectMapper;

    public AdditionalInformationRequest buildRequestFromExcel(MultipartFile file,
                                                              TemplateEntity template,
                                                              String quoteNo,
                                                              String sectionId,
                                                              String coverId) throws Exception {

        List<ColumnDef> cols = objectMapper.readValue(
                template.getColumnsJson(),
                new TypeReference<List<ColumnDef>>() {}
        );

        List<ContentItem> items = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             ReadableWorkbook wb = new ReadableWorkbook(is)) {

            Sheet sheet = wb.getFirstSheet();

            try (Stream<Row> rows = sheet.openStream()) {
                Iterator<Row> it = rows.iterator();
                if (!it.hasNext()) {
                    return AdditionalInformationRequest.builder()
                            .quoteNo(quoteNo)
                            .contentItems(Collections.emptyList())
                            .build();
                }

                // header row
                Row header = it.next();
                int headerCells = header.getCellCount();

                Map<Integer, ColumnDef> indexToCol = new HashMap<>();

                for (int i = 0; i < headerCells; i++) {
                    String headerName = header.getCellAsString(i).orElse(null);
                    if (headerName == null) continue;
                    String key = headerName.trim();
                    for (ColumnDef cd : cols) {
                        if (cd.getHeaderName() != null &&
                                cd.getHeaderName().trim().equalsIgnoreCase(key)) {
                            indexToCol.put(i, cd);
                            break;
                        }
                    }
                }

                while (it.hasNext()) {
                    Row r = it.next();

                    ContentItem.ContentItemBuilder builder = ContentItem.builder()
                            .sectionId(sectionId)
                            .coverId(coverId);

                    boolean allEmpty = true;

                    for (Map.Entry<Integer, ColumnDef> e : indexToCol.entrySet()) {
                        int idx = e.getKey();
                        ColumnDef cd = e.getValue();

                        // ⭐ NEW: read value as String based on dataType
                        String raw = getCellValueAsString(r, cd, idx);

                        if (raw != null && !raw.trim().isEmpty()) {
                            allEmpty = false;
                        }

                        String valueToSet = raw;
                        if (cd.isNumericOnly() && raw != null) {
                            valueToSet = raw.replaceAll("[^0-9.]", "");
                            if (valueToSet.isEmpty()) {
                                valueToSet = null;
                            }
                        }

                        if (valueToSet == null || valueToSet.trim().isEmpty()) {
                            continue;
                        }

                        String target = cd.getTargetField() == null
                                ? ""
                                : cd.getTargetField().toUpperCase();

                        switch (target) {
                            case "VALUE":
                                builder.value(valueToSet);
                                break;
                            case "PARAM1":
                                builder.param1(valueToSet);
                                break;
                            case "PARAM3":
                                builder.param3(valueToSet);
                                break;
                            case "PARAM4":
                                builder.param4(valueToSet);
                                break;
                            case "PARAM5":
                                builder.param5(valueToSet);
                                break;
                            case "PARAM6":
                                builder.param6(valueToSet);
                                break;
                            case "PARAM7":
                                builder.param7(valueToSet);
                                break;
                            case "PARAM8":
                                builder.param8(valueToSet);
                                break;
                            case "PARAM9":
                                builder.param9(valueToSet);
                                break;
                            case "PARAM10":
                                builder.param10(valueToSet);
                                break;
                            default:
                                // ignore unknown
                                break;
                        }
                    }

                    if (allEmpty) {
                        continue;
                    }

                    items.add(builder.build());
                }
            }
        }

        return AdditionalInformationRequest.builder()
                .quoteNo(quoteNo)
                .contentItems(items)
                .build();
    }

    // ⭐ helper: safely read a cell as String based on ColumnDef.dataType
    private String getCellValueAsString(Row row, ColumnDef cd, int idx) {
        if (idx < 0) return null;

        String type = (cd.getDataType() == null ? "STRING" : cd.getDataType().toUpperCase());

        try {
            switch (type) {
                case "NUMBER": {
                    var numOpt = row.getCellAsNumber(idx);
                    if (!numOpt.isPresent()) return null;

                    BigDecimal bd = numOpt.get();          // Fastexcel returns BigDecimal
                    double d = bd.doubleValue();           // convert to double

                    // Remove ".0" if whole number
                    if (Math.floor(d) == d) {
                        return String.valueOf((long) d);
                    } else {
                        return String.valueOf(d);
                    }
                }

                case "BOOLEAN": {
                    var bOpt = row.getCellAsBoolean(idx);
                    return bOpt.map(String::valueOf).orElse(null);
                }

                case "DATE": {
                    var dateOpt = row.getCellAsDate(idx);
                    return dateOpt.map(Object::toString).orElse(null);
                }

                case "STRING":
                default: {
                    return row.getCellAsString(idx).orElse(null);
                }
            }
        } catch (Exception ex) {
            // fallback: try string
            try {
                return row.getCellAsString(idx).orElse(null);
            } catch (Exception ignore) {
                return null;
            }
        }
    }

}
