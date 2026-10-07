package com.maan.eway.excelupload.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.excelupload.bean.TemplateEntity;
import com.maan.eway.excelupload.dto.ColumnDef;
import com.maan.eway.excelupload.dto.PreviewRow;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FastexcelParser {

    private final ObjectMapper objectMapper;

    public PreviewResponseWrapper preview(MultipartFile file,
                                          TemplateEntity template,
                                          int previewRows) throws Exception {

        // 1) Load template columns
        List<ColumnDef> cols = objectMapper.readValue(
                template.getColumnsJson(),
                new TypeReference<List<ColumnDef>>() {}
        );

        Map<Integer, ColumnDef> indexToCol = new HashMap<>();

        List<PreviewRow> preview = new ArrayList<>();
        int total = 0;
        int valid = 0;
        int invalid = 0;

        try (InputStream is = file.getInputStream();
             ReadableWorkbook wb = new ReadableWorkbook(is)) {

            Sheet sheet = wb.getFirstSheet();

            try (Stream<Row> rows = sheet.openStream()) {
                Iterator<Row> it = rows.iterator();
                if (!it.hasNext()) {
                    return new PreviewResponseWrapper(0, preview, valid, invalid);
                }

                // 2) Header row
                Row header = it.next();
                int headerCells = header.getCellCount();

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

                int rowNum = 1;
                // 3) Data rows
                while (it.hasNext()) {
                    Row r = it.next();
                    total++;

                    Map<String, Object> map = new LinkedHashMap<>();
                    boolean rowValid = true;
                    StringBuilder rowError = new StringBuilder();

                    for (Map.Entry<Integer, ColumnDef> e : indexToCol.entrySet()) {
                        int idx = e.getKey();
                        ColumnDef cd = e.getValue();

                        String raw = r.getCellAsString(idx).orElse(null);
                        String valueToSet = raw;

                        // numericOnly => keep only digits and dot
                        if (cd.isNumericOnly() && raw != null) {
                            valueToSet = raw.replaceAll("[^0-9.]", "");
                            if (valueToSet.isEmpty()) {
                                valueToSet = null;
                            }
                        }

                        if (cd.isRequired() && (valueToSet == null || valueToSet.trim().isEmpty())) {
                            rowValid = false;
                            rowError.append(cd.getHeaderName()).append(" is required. ");
                        }

                        // For preview, key by headerName (what user sees)
                        map.put(cd.getHeaderName(), valueToSet);
                    }

                    PreviewRow pr = PreviewRow.builder()
                            .rowNumber(rowNum++)
                            .values(map)
                            .valid(rowValid)
                            .errorMessage(rowValid ? null : rowError.toString())
                            .build();

                    if (preview.size() < previewRows) {
                        preview.add(pr);
                    }

                    if (rowValid) valid++;
                    else invalid++;
                }
            }
        }

        return new PreviewResponseWrapper(total, preview, valid, invalid);
    }

    public static class PreviewResponseWrapper {
        public final int totalRows;
        public final List<PreviewRow> previewRows;
        public final int validRows;
        public final int invalidRows;

        public PreviewResponseWrapper(int totalRows,
                                      List<PreviewRow> previewRows,
                                      int validRows,
                                      int invalidRows) {
            this.totalRows = totalRows;
            this.previewRows = previewRows;
            this.validRows = validRows;
            this.invalidRows = invalidRows;
        }
    }
}
