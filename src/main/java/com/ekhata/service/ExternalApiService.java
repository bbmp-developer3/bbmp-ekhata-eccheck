package com.ekhata.service;

import com.ekhata.dao.DeedData;
import com.ekhata.dao.EcDeedDetail;
import com.ekhata.model.APIResponse;
import com.ekhata.model.EcResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
public class ExternalApiService {

    private final WebClient webClient;
    private final RestTemplate restTemplate;


    public ExternalApiService(WebClient.Builder webClientBuilder, RestTemplateBuilder restTemplateBuilder) {
        this.webClient = webClientBuilder.build();
        this.restTemplate = restTemplateBuilder.build();
    }

    public DeedData callExternalApiWithJson(String url, String jsonBody) {
        Mono<APIResponse> deedResponseMono = webClient.post()
                .uri(url)
                .bodyValue(jsonBody)
                .retrieve()
                .bodyToMono(APIResponse.class);
// Subscribing to the Mono to get the response
        deedResponseMono.subscribe(
                apiResponse -> {
                    // Handle the actual response here
                    System.out.println("Response received: " + apiResponse);
                },
                error -> {
                    // Handle any errors here
                    System.err.println("Error occurred: " + error.getMessage());
                }
        );

        return convertJsonToJava(deedResponseMono.toString());
    }

    public DeedData makeDeedRequest(String url, String requestBody) {

        // Set up headers if necessary
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        // Create an HttpEntity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Process the response
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        APIResponse apiResponse = convertJsonToAPIResponse(response.getBody());
        return convertJsonToJava(apiResponse.getJson());
    }

    public APIResponse convertJsonToAPIResponse(String jsonString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            APIResponse response = mapper.readValue(jsonString, APIResponse.class);
            System.out.println(response.getResponseMessage());
            return response;
        } catch (JsonMappingException e) {
            System.err.println("JSON Mapping Error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.err.println("JSON Processing Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("General Error: " + e.getMessage());
        }
        return null;
    }


    public DeedData convertJsonToJava(String jsonString) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            DeedData response = mapper.readValue(jsonString, DeedData.class);
            System.out.println(response.getApplicationnumber());
            return response;
        } catch (JsonMappingException e) {
            System.err.println("JSON Mapping Error: " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.err.println("JSON Processing Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("General Error: " + e.getMessage());
        }
        return null;
    }

    public EcResponse makeEcDocRequest(String url, String requestBody) {

        // Set up headers if necessary
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        // Create an HttpEntity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Make the POST request
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Process the response
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        APIResponse apiResponse = convertJsonToAPIResponse(response.getBody());
        return convertToEcDetails(apiResponse.getJson());
    }

    private EcResponse convertToEcDetails(String json) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Step 2: Deserialize the nested JSON string (response.json) into a list of DeedDetail objects
            List<EcDeedDetail> deedDetails = objectMapper.readValue(json, new TypeReference<List<EcDeedDetail>>() {});

            // Print the result
            System.out.println("Deed Details: " + deedDetails);
            return EcResponse.builder().deedDetails(deedDetails).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
