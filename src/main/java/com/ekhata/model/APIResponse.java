package com.ekhata.model;

import com.ekhata.dao.DeedData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class APIResponse {
    private String responseMessage;
    private String responseCode;

    @JsonProperty("Base64")
    private String base64;

    private String json;
}
