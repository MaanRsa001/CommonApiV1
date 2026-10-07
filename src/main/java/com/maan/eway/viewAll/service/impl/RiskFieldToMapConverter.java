//package com.maan.eway.viewAll.service.impl;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//import com.maan.eway.workflow.dto.JsonField;
//
///**
// * Converts a JsonField tree into a recursive {key, value} structure:
// *
// *   Leaf   →  { "key": "Location Name",  "value": "Chennai" }
// *   Parent →  { "key": "Location Details","value": [ {key,value}, {key,value}, ... ] }
// *
// * Every node — whether a leaf or a parent — becomes a single {key, value} map.
// * Parent nodes carry a List<Map<String,Object>> as their value,
// * where each list element is itself a {key, value} map (recursive).
// */
//public class RiskFieldToMapConverter implements Function<JsonField, Map<String, Object>> {
//
//    private final Map<String, List<Map<String, Object>>> dynamicQuery;
//    private final int index;
//
//    public RiskFieldToMapConverter(Map<String, List<Map<String, Object>>> dynamicQuery) {
//        this.dynamicQuery = dynamicQuery;
//        this.index = 0;
//    }
//
//    public RiskFieldToMapConverter(Map<String, List<Map<String, Object>>> dynamicQuery, int index) {
//        this.dynamicQuery = dynamicQuery;
//        this.index = index;
//    }
//
//    /**
//     * Builds a single  { "key": <jsonKey>, "value": <leafValue | List<{key,value}>> }
//     */
//    @Override
//    public Map<String, Object> apply(JsonField field) {
//        try {
//            if (field.getChildField() != null && !field.getChildField().isEmpty()) {
//
//                // ── PARENT node ───────────────────────────────────────────────────
//                List<Map<String, Object>> childKVList;
//
//                if ("Yes".equals(field.getIsarray()) && dynamicQuery != null && !dynamicQuery.isEmpty()) {
//
//                    // Array parent: iterate over each DB row and build one {key,value}
//                    // list per row, then merge all rows together
//                    List<Map<String, Object>> dbRows =
//                            dynamicQuery.get(field.getQueryId().toPlainString());
//
//                    if (dbRows == null || dbRows.isEmpty()) {
//                        return buildKV(field.getJsonKey(), new ArrayList<>());
//                    }
//
//                    childKVList = new ArrayList<>();
//                    for (int i = 0; i < dbRows.size(); i++) {
//                        RiskFieldToMapConverter rowConverter =
//                                new RiskFieldToMapConverter(dynamicQuery, i);
//
//                        // Each child of this array parent → one {key,value} entry
//                        List<Map<String, Object>> rowChildren = field.getChildField().stream()
//                                .map(rowConverter)
//                                .filter(Objects::nonNull)
//                                .collect(Collectors.toList());
//
//                        childKVList.addAll(rowChildren);
//                    }
//
//                } else {
//                    // Non-array parent: recurse with same index
//                    childKVList = field.getChildField().stream()
//                            .map(this)
//                            .filter(Objects::nonNull)
//                            .collect(Collectors.toList());
//                }
//
//                return buildKV(field.getJsonKey(), childKVList);
//
//            } else {
//                // ── LEAF node ─────────────────────────────────────────────────────
//                Object value = resolveLeafValue(field);
//                System.out.println("\t" + field.getJsonKey() + " = " + value);
//                return buildKV(field.getJsonKey(), value);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // ── Helpers ──────────────────────────────────────────────────────────────────
//
//    /** Wraps key + value into the standard {key, value} map. */
//    private Map<String, Object> buildKV(String key, Object value) {
//        Map<String, Object> kv = new HashMap<>();
//        kv.put("Key",   key);
//        kv.put("Value", value);
//        return kv;
//    }
//
//    /** Reads the leaf value from the dynamic-query result for this.index row. */
//    private Object resolveLeafValue(JsonField field) {
//        if (dynamicQuery == null || dynamicQuery.isEmpty()) {
//            return "";
//        }
//
//        List<Map<String, Object>> rows = dynamicQuery.get(field.getQueryId().toPlainString());
//        if (rows == null || rows.isEmpty()) {
//            return "";
//        }
//
//        Map<String, Object> row = rows.get(this.index);
//
//        // Default + Boolean  (must come BEFORE plain defaultYn check)
//        if ("Y".equals(field.getDefaultYn()) && "Boolean".equals(field.getDatatype())) {
//            return Boolean.valueOf(field.getDefaultValue().toString());
//        }
//
//        // Any other default
//        if ("Y".equals(field.getDefaultYn())) {
//            return field.getDefaultValue().toString();
//        }
//
//        // Boolean from DB
//        if ("Boolean".equals(field.getDatatype())) {
//            return Boolean.valueOf(
//                    row.get(field.getQueryAlias()) == null ? ""
//                            : row.get(field.getQueryAlias()).toString());
//        }
//
//        // Date
//        if ("Date".equals(field.getDatatype())) {
//            return row.get(field.getQueryAlias()) == null ? ""
//                    : row.get(field.getQueryAlias()).toString();
//        }
//
//        // Integer / BigDecimal
//        if ("Integer".equals(field.getDatatype())) {
//            try {
//                return row.get(field.getQueryAlias()) == null ? BigDecimal.ZERO
//                        : new BigDecimal(row.get(field.getQueryAlias()).toString());
//            } catch (Exception e) {
//                return BigDecimal.ZERO;
//            }
//        }
//
//        // Default: String
//        return row.get(field.getQueryAlias()) == null ? ""
//                : row.get(field.getQueryAlias()).toString();
//    }
//}


package com.maan.eway.viewAll.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.maan.eway.workflow.dto.JsonField;

/**
 * Converts a JsonField tree into a recursive {Key, Value} structure:
 *
 *   Leaf        →  { "Key": "Location Name",  "Value": "Chennai" }
 *   Amount Leaf →  { "Key": "Sum Insured LC", "Value": "800,000.00", "Type": "Amount" }
 *   Parent      →  { "Key": "Location Details","Value": [ {Key,Value}, {Key,Value}, ... ] }
 *
 * Every node — whether a leaf or a parent — becomes a single {Key, Value} map.
 * Parent nodes carry a List<Map<String,Object>> as their value,
 * where each list element is itself a {Key, Value} map (recursive).
 */
public class RiskFieldToMapConverter implements Function<JsonField, Map<String, Object>> {

    private final Map<String, List<Map<String, Object>>> dynamicQuery;
    private final int index;

    public RiskFieldToMapConverter(Map<String, List<Map<String, Object>>> dynamicQuery) {
        this.dynamicQuery = dynamicQuery;
        this.index = 0;
    }

    public RiskFieldToMapConverter(Map<String, List<Map<String, Object>>> dynamicQuery, int index) {
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

                    // Array parent: iterate over each DB row and build one {Key,Value}
                    // list per row, then merge all rows together
                    List<Map<String, Object>> dbRows =
                            dynamicQuery.get(field.getQueryId().toPlainString());

                    if (dbRows == null || dbRows.isEmpty()) {
                        return buildKV(field.getJsonKey(), new ArrayList<>(), null);
                    }

                    childKVList = new ArrayList<>();
                    for (int i = 0; i < dbRows.size(); i++) {
                        RiskFieldToMapConverter rowConverter =
                                new RiskFieldToMapConverter(dynamicQuery, i);

                        List<Map<String, Object>> rowChildren = field.getChildField().stream()
                                .map(rowConverter)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        childKVList.addAll(rowChildren);
                    }

                } else {
                    // Non-array parent: recurse with same index
                    childKVList = field.getChildField().stream()
                            .map(this)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }

                return buildKV(field.getJsonKey(), childKVList, null);

			} else {

				// ── LEAF node ─────────────────────────────────────────────────────
				Object value = resolveLeafValue(field);
				if (!"RATE".equalsIgnoreCase(field.getJsonKey())) {
					if ("AMOUNT".equalsIgnoreCase(field.getAmount())) {
						// Format as comma-separated with 2 decimal places and add Type = "Amount"
						String formatted = formatAmount(value);
						System.out.println("\t" + field.getJsonKey() + " = " + formatted + " [Amount]");
						return buildKV(field.getJsonKey(), formatted, "A");
					}
				} else {
					if("AMOUNT".equalsIgnoreCase(field.getAmount())) {
						return buildKV(field.getJsonKey(), value, "A");
					}
				}

				System.out.println("\t" + field.getJsonKey() + " = " + value);
				return buildKV(field.getJsonKey(), value, null);
			}

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────

    /**
     * Wraps key + value (+ optional type) into the standard map.
     * If type is null or blank, the "Type" key is omitted entirely.
     */
    private Map<String, Object> buildKV(String key, Object value, String type) {
    	
    	 Object resolvedValue;
    	    if (value == null || value.toString().trim().isEmpty()) {
    	        resolvedValue = "N/A";
    	    } else {
    	        resolvedValue = value;
    	    }
    	
        Map<String, Object> kv = new HashMap<>();
        kv.put("Key",   key);
        kv.put("Value", resolvedValue);
        if (type != null && !type.trim().isEmpty()) {
            kv.put("Type", type);
        }
        return kv;
    }

    /**
     * Formats a numeric value as a comma-grouped 2-decimal string.
     * e.g.  800000  →  "800,000.00"
     *       1234567.5 →  "1,234,567.50"
     * Returns "0.00" for null / blank / unparseable input.
     */
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

    /**
     * Reads the leaf value from the dynamic-query result for this.index row.
     */
    private Object resolveLeafValue(JsonField field) {
        if (dynamicQuery == null || dynamicQuery.isEmpty()) {
            return "";
        }

        List<Map<String, Object>> rows = dynamicQuery.get(field.getQueryId().toPlainString());
        if (rows == null || rows.isEmpty()) {
            return "";
        }

        Map<String, Object> row = rows.get(this.index);

        // Default + Boolean  (must come BEFORE plain defaultYn check)
        if ("Y".equals(field.getDefaultYn()) && "Boolean".equals(field.getDatatype())) {
            return Boolean.valueOf(field.getDefaultValue().toString());
        }

        // Any other default
        if ("Y".equals(field.getDefaultYn())) {
            return field.getDefaultValue() == null ? "" : field.getDefaultValue().toString();
        }

        // Boolean from DB
        if ("Boolean".equals(field.getDatatype())) {
            return Boolean.valueOf(
                    row.get(field.getQueryAlias()) == null ? ""
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