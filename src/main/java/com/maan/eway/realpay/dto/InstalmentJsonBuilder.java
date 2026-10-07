package com.maan.eway.realpay.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maan.eway.realpay.model.Instalment;
import java.time.LocalDateTime;

public class InstalmentJsonBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode buildInstalmentNode(
            Instalment instalment
    ) {
        ObjectNode node = mapper.createObjectNode();

        node.put("ClientNumber", instalment.getClientNumber());
        node.put("ContractNumber", instalment.getContractNumber());
        node.put("ContractSequence", instalment.getContractSequence());
        node.put("InstalmentSequence", instalment.getNoOfInstalment());
        node.put("InstalmentReferenceNumber", instalment.getInstalmentReferenceNumber());
        node.put("InstalmentActionDate", String.valueOf(LocalDateTime.now()));
        node.put("TrackingCode", "01");

        // Financial values → safer handling
        node.putPOJO("InstalmentAmount", instalment.getInstalmentAmount());
        node.putPOJO("CTCAmount", 0);

        node.put("InstalmentStatus", "S");
        node.put("ResponseCode", "00");
        node.put("LastUpdateDate", String.valueOf(LocalDateTime.now()));

        return node;
    }
}
