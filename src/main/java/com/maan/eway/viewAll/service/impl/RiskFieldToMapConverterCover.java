package com.maan.eway.viewAll.service.impl;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.maan.eway.workflow.dto.JsonField;

public class RiskFieldToMapConverterCover implements Function<JsonField, Map<String, Object>> {

    private final Map<String, List<Map<String, Object>>> dynamicQuery;
    private final int index;

    public RiskFieldToMapConverterCover(Map<String, List<Map<String, Object>>> dynamicQuery) {
        this.dynamicQuery = dynamicQuery;
        this.index = 0;
    }

    public RiskFieldToMapConverterCover(Map<String, List<Map<String, Object>>> dynamicQuery, int index) {
        this.dynamicQuery = dynamicQuery;
        this.index = index;
    }

    // ── Main apply ───────────────────────────────────────────────────────────────

    @Override
    public Map<String, Object> apply(JsonField field) {
        try {
            if (field.getChildField() != null && !field.getChildField().isEmpty()) {

                // ── PARENT node ───────────────────────────────────────────────────
                List<Map<String, Object>> childKVList;

                if ("Yes".equals(field.getIsarray()) && dynamicQuery != null && !dynamicQuery.isEmpty()) {

                    List<Map<String, Object>> dbRows =
                            dynamicQuery.get(field.getQueryId().toPlainString());

                    if (dbRows == null || dbRows.isEmpty()) {
                        return buildKV(field.getJsonKey(), new ArrayList<>(), null, field.getOrderBy());
                    }

                    childKVList = new ArrayList<>();
                    for (int i = 0; i < dbRows.size(); i++) {
                    	RiskFieldToMapConverterCover rowConverter =
                                new RiskFieldToMapConverterCover(dynamicQuery, i);

                        List<Map<String, Object>> rowChildren = field.getChildField().stream()
                                .map(rowConverter)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        // Flatten each row's children using orderBy as suffix
                        Map<String, Object> flatRow = flattenByOrderBy(field.getChildField(), rowChildren);
                        childKVList.add(flatRow);
                    }

                } else {
                    // Non-array parent: recurse with same index
                    List<Map<String, Object>> rawChildren = field.getChildField().stream()
                            .map(this)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    // Flatten children using orderBy as suffix
                    Map<String, Object> flatChildren = flattenByOrderBy(field.getChildField(), rawChildren);
                    childKVList = new ArrayList<>();
                    childKVList.add(flatChildren);
                }

                return buildKV(field.getJsonKey(), childKVList, null, field.getOrderBy());

            } else {

                // ── LEAF node ─────────────────────────────────────────────────────
                Object value = resolveLeafValue(field);
                if (!"RATE".equalsIgnoreCase(field.getJsonKey())) {
                    if ("AMOUNT".equalsIgnoreCase(field.getAmount())) {
                        String formatted = formatAmount(value);
                        System.out.println("\t" + field.getJsonKey() + " = " + formatted + " [Amount]");
                        return buildKV(field.getJsonKey(), formatted, "A", field.getOrderBy());
                    }
                } else {
                    if ("AMOUNT".equalsIgnoreCase(field.getAmount())) {
                        return buildKV(field.getJsonKey(), value, "A", field.getOrderBy());
                    }
                }

                System.out.println("\t" + field.getJsonKey() + " = " + value);
                return buildKV(field.getJsonKey(), value, null, field.getOrderBy());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Flatten using orderBy as suffix ──────────────────────────────────────────

    /**
     * Maps each entry using its corresponding JsonField.orderBy as the suffix.
     *
     * fields[0].orderBy = 1  →  Key1, Value1, Type1
     * fields[1].orderBy = 2  →  Key2, Value2, Type2
     * fields[2].orderBy = 3  →  Key3, Value3, Type3
     *
     * If orderBy is null, falls back to list position (i+1).
     */
    public static Map<String, Object> flattenByOrderBy(
            List<JsonField> fields,
            List<Map<String, Object>> kvList) {

        Map<String, Object> result = new LinkedHashMap<>();

        // Sort fields by orderBy first
        List<JsonField> sortedFields = fields.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(f -> f.getOrderBy() != null ? f.getOrderBy() : Integer.MAX_VALUE))
                .collect(Collectors.toList());

        // Build a map from jsonKey -> kvEntry for quick lookup
        Map<String, Map<String, Object>> kvByKey = new LinkedHashMap<>();
        for (Map<String, Object> kv : kvList) {
            if (kv != null && kv.get("Key") != null) {
                kvByKey.put(kv.get("Key").toString(), kv);
            }
        }

        for (int i = 0; i < sortedFields.size(); i++) {
            JsonField f = sortedFields.get(i);
            int suffix = (f.getOrderBy() != null) ? f.getOrderBy() : (i + 1);

            Map<String, Object> entry = kvByKey.get(f.getJsonKey());
            if (entry == null) continue;

            result.put("Key"   + suffix, entry.get("Key"));
            result.put("Value" + suffix, entry.get("Value"));
            Object type = entry.get("Type");
            result.put("Type"  + suffix, type != null ? type : "");
        }

        return result;
    }

    // ── buildKV ──────────────────────────────────────────────────────────────────

    /**
     * Wraps key + value + optional type + orderBy into the standard map.
     * orderBy is stored internally for reference by flattenByOrderBy.
     */
    private Map<String, Object> buildKV(String key, Object value, String type, Integer orderBy) {
    	
    	Map<String, Object> kv = new LinkedHashMap<>();
    	 Object resolvedValue;
    	    if (value == null || value.toString().trim().isEmpty()) {
    	        resolvedValue = "NA";
    	    } else {
    	        resolvedValue = value;
    	    }
        kv.put("Key",     key);
        kv.put("Value",   resolvedValue);
        kv.put("OrderBy", orderBy); // kept for flattenByOrderBy reference
        if (type != null && !type.trim().isEmpty()) {
            kv.put("Type", type);
        }
        return kv;
    }

    // ── formatAmount ─────────────────────────────────────────────────────────────

    private String formatAmount(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return "0";
        }
        try {
            BigDecimal bd = new BigDecimal(value.toString().trim())
                    .setScale(0, RoundingMode.HALF_UP);
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
            return nf.format(bd);
        } catch (Exception e) {
            return value.toString();
        }
    }

    // ── resolveLeafValue ─────────────────────────────────────────────────────────

    private Object resolveLeafValue(JsonField field) {
        if (dynamicQuery == null || dynamicQuery.isEmpty()) {
            return "";
        }

        List<Map<String, Object>> rows = dynamicQuery.get(field.getQueryId().toPlainString());
        if (rows == null || rows.isEmpty()) {
            return "";
        }

        Map<String, Object> row = rows.get(this.index);

        // Default + Boolean (must come BEFORE plain defaultYn check)
        if ("Y".equals(field.getDefaultYn()) && "Boolean".equals(field.getDatatype())) {
            return Boolean.valueOf(field.getDefaultValue().toString());
        }

        // Any other default
        if ("Y".equals(field.getDefaultYn())) {
            return field.getDefaultValue() == null ? "" : field.getDefaultValue().toString();
        }

        // Boolean from DB
        if ("Boolean".equals(field.getDatatype())) {
            return Boolean.valueOf(row.get(field.getQueryAlias()) == null ? ""
                            : row.get(field.getQueryAlias()).toString());
        }

        // Date
        if ("Date".equals(field.getDatatype())) {
            return row.get(field.getQueryAlias()) == null ? ""
                    : row.get(field.getQueryAlias()).toString();
        }

        // Integer / BigDecimal
        if ("Integer".equals(field.getDatatype())) {
            try {
                return row.get(field.getQueryAlias()) == null ? BigDecimal.ZERO
                        : new BigDecimal(row.get(field.getQueryAlias()).toString());
            } catch (Exception e) {
                return BigDecimal.ZERO;
            }
        }

        // Default: String
        return row.get(field.getQueryAlias()) == null ? ""
                : row.get(field.getQueryAlias()).toString();
    }
}