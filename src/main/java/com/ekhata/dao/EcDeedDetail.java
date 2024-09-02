package com.ekhata.dao;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcDeedDetail {

    private List<String> description;

    @JsonProperty("documentvaluation")
    private String documentValuation;

    @JsonProperty("executiondate")
    private String executionDate;

    private List<String> executants;
    private List<String> claimants;
    private String volume;
    private String book;

    @JsonProperty("docsummary")
    private String docSummary;

    @JsonProperty("crossreference")
    private String crossReference;

    @JsonProperty("CorrectionNote")
    private String correctionNote;

    @JsonProperty("liabilitynote")
    private String liabilityNote;
}
