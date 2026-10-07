package com.maan.eway.realpay.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class MerchantUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(MerchantUtil.class);

    public static Map<String, String> parseProductMerchantMapping(String mappingConfig) {
        Map<String, String> mapping = new HashMap<>();
        
        if (mappingConfig == null || mappingConfig.trim().isEmpty()) {
            logger.warn("Product-merchant mapping configuration is empty");
            return mapping;
        }
        
        try {
            String[] pairs = mappingConfig.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String product = keyValue[0].trim();
                    String merchant = keyValue[1].trim();
                    mapping.put(product, merchant);
                    logger.debug("Mapped product '{}' to merchant '{}'", product, merchant);
                }
            }
            logger.info("Successfully parsed {} product-merchant mappings", mapping.size());
        } catch (Exception e) {
            logger.error("Error parsing product-merchant mapping configuration: {}", mappingConfig, e);
        }
        
        return mapping;
    }
}