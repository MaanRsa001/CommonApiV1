package com.maan.eway.excelupload.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Static mirror of the relation-type lookup you get from the "relation type for Male/Female"
 * API responses you pasted. Keeping this static avoids an extra API call per template
 * generation / upload validation. If that master data can change, swap these maps for a
 * cached call to that endpoint instead — the rest of the code doesn't care where the map
 * comes from.
 */
public final class RelationshipMaster {

    // Code -> Desc, for GenderId = "M"
    public static final Map<String, String> MALE_RELATIONS = new LinkedHashMap<>();
    // Code -> Desc, for GenderId = "F"
    public static final Map<String, String> FEMALE_RELATIONS = new LinkedHashMap<>();

    // Desc -> Code (reverse lookups used while parsing the uploaded excel)
    public static final Map<String, String> MALE_DESC_TO_CODE = new LinkedHashMap<>();
    public static final Map<String, String> FEMALE_DESC_TO_CODE = new LinkedHashMap<>();

    public static final String MALE_SELF_CODE = "009";
    public static final String FEMALE_SELF_CODE = "010";

    static {

        MALE_RELATIONS.put("009", "Self");
        MALE_RELATIONS.put("002", "Husband");
        MALE_RELATIONS.put("003", "Father");
        MALE_RELATIONS.put("005", "Son");
        MALE_RELATIONS.put("007", "Brother");
        MALE_RELATIONS.put("012", "Others");

        FEMALE_RELATIONS.put("010", "Self");
        FEMALE_RELATIONS.put("002", "Wife");
        FEMALE_RELATIONS.put("004", "Mother");
        FEMALE_RELATIONS.put("006", "Daughter");
        FEMALE_RELATIONS.put("008", "Sister");
        FEMALE_RELATIONS.put("011", "Others");

        
        MALE_RELATIONS.forEach((code, desc) ->
            MALE_DESC_TO_CODE.put(desc.toUpperCase(), code)
        );

        FEMALE_RELATIONS.forEach((code, desc) ->
            FEMALE_DESC_TO_CODE.put(desc.toUpperCase(), code)
        );
    }

    private RelationshipMaster() {}

    public static String[] descArray(Map<String, String> codeToDesc) {
        return codeToDesc.values().toArray(new String[0]);
    }

    public static String codeFor(String genderId, String relationDesc) {
        if (relationDesc == null) return null;
        String key = relationDesc.trim().toUpperCase();
        if ("M".equalsIgnoreCase(genderId)) {
            return MALE_DESC_TO_CODE.get(key);
        } else if ("F".equalsIgnoreCase(genderId)) {
            return FEMALE_DESC_TO_CODE.get(key);
        }
        return null;
    }

    public static String selfCodeFor(String genderId) {
        return "M".equalsIgnoreCase(genderId) ? MALE_SELF_CODE : FEMALE_SELF_CODE;
    }

    public static String selfDescFor(String genderId) {
        return "Self";
    }
}
