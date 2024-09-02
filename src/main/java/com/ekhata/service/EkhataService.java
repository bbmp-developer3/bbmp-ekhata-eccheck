package com.ekhata.service;

import com.ekhata.dao.DeedData;
import com.ekhata.dao.EcDeedDetail;
import com.ekhata.model.DeedResponse;
import com.ekhata.model.EcResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class EkhataService {

    public EkhataService (ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }
    @Value("${deed-url}")
    private String deedUrl;
    @Value("${ec-url}")
    private String ecUrl;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ExternalApiService externalApiService;

    public DeedData callDeedApi(String deed_number) {
        //call deedNUmberAPI to get EC details
        String deedReqBody = "{\"finalRegNumber\": \"" + deed_number  + "\"}";
        DeedData deedResponse = externalApiService.makeDeedRequest(deedUrl, deedReqBody);
        if(deedResponse == null)
            throw new RuntimeException("Response is null");
        System.out.println("DEED DATA >>>>> " + deedResponse.toString());
        return deedResponse;

    }

    public boolean callApiToCheckDeedArticle(String stamparticle) {
        return true;
        //call deedNUmberAPI to get EC details
//        String deedReqBody = "{\"finalRegNumber\": \"" + stamparticle  + "\"}";
//        DeedData deedResponse = externalApiService.makeDeedRequest(deedUrl, deedReqBody);
//        if(deedResponse == null)
//            throw new RuntimeException("Response is null");
//        System.out.println("DEED DATA >>>>> " + deedResponse.toString());
//        return deedResponse;

    }

    private List<EcDeedDetail> callEcDocApi(String ec_number) {
        //call EcDoc API for Ec Details
        String ecReqBody = "{\"finalRegNumber\": \"" + ec_number  + "\"}";
        EcResponse ecResponse = externalApiService.makeEcDocRequest(ecUrl, ecReqBody);

        if(ecResponse == null)
            throw new RuntimeException("Response is null");
        System.out.println("DEED DATA >>>>> " + ecResponse.toString());
        return ecResponse.getDeedDetails();

    }
    public String doIt(String deed_number, String ec_number) {
        DeedData deedData = callDeedApi(deed_number);
        List<EcDeedDetail> ecDeedDetails = callEcDocApi(ec_number);
        return continueProcess(ecDeedDetails, null, deedData);
    }

    public String continueProcess(List<EcDeedDetail> ecDetails, EcDeedDetail ecDeedDetail, DeedData deedResponse)  {
        if(ecDeedDetail != null) {
            ecDetails.remove(ecDeedDetail);
        }
        Optional<EcDeedDetail> max = ecDetails.stream()
                .max(Comparator.comparing(deed -> parseDate(deed.getExecutionDate())));

        if(max.isPresent()) {
            EcDeedDetail maxExeDate = max.get();
            //call deed API to fetch EcDetails for maxExeDate.getDocSummary()
            DeedData newDeedData = callDeedApi(maxExeDate.getDocSummary());

            if(isEcDateIsAfterDeedDate(maxExeDate.getExecutionDate(), deedResponse.getExecutedate())) {
                continueProcessFurther(newDeedData, ecDetails, maxExeDate, deedResponse);
            }
            //check if both dates are equal
            else if(isEcDaterDeedDateEqual(maxExeDate.getExecutionDate(), deedResponse.getExecutedate())) {
                //check if document number is same in both doc
                if(newDeedData.getFinalregistrationnumber().equalsIgnoreCase(deedResponse.getFinalregistrationnumber())) {
                    return "No Further Transaction as per EC given by citizen";
                } else {
                    continueProcessFurther(newDeedData, ecDetails, maxExeDate, deedResponse);
                }

            } else {
                return "No Further Transaction as per EC given by citizen";
            }

        } else {
            throw new RuntimeException("No Data Matched");
        }
        return "No Data Matched";
    }

    private String continueProcessFurther(DeedData newDeedData, List<EcDeedDetail> ecDetails, EcDeedDetail maxExeDate, DeedData deedResponse) {
        //check the type of deed article
        String article = newDeedData.getPropertyinfo().get(0).getStamparticle();
        //call API to check deed article - Is it of type "USe" or "Not Use"
        boolean articleInUse = callApiToCheckDeedArticle(article);
        //if type is "use"
        //compare deeddata woth newDeedData
        if(articleInUse) {
            if(compareFirstLevelOfData(deedResponse, newDeedData)) {
                //property/door/siteno
                //village/ward
                //registration distict
                //if all compare success
                //compare more fields
                //purchaser/receiver name
                if(compareSecondLevelOfData(deedResponse, newDeedData)) {
                    //if matches
                    return "ec success";
                } else {
                    //else
                    return "ec shows property further transacted , so cannot process ur application. u can meet jurisdictional ARO";
                }
            } else {
                return "enter proper ec as property in ec doesnt match property in registered deed";
            }
        }
        //if type is "not use"
        //continue to next latest date of List<ecdeeddata>
        return continueProcess(ecDetails, maxExeDate, deedResponse);
    }

    private static LocalDate parseDate(String dateString) {
        // Extract the date part and parse it
        String datePart = dateString.substring(0, 10); // Extract 'yyyy-MM-dd'
        return LocalDate.parse(datePart, dateFormatter);
    }
    private boolean compareFirstLevelOfData(DeedData deedResponse, DeedData ecDeedDetail) {
        return (deedResponse.getPropertyinfo().get(0).getVillagenamee().equalsIgnoreCase(ecDeedDetail.getPropertyinfo().get(0).getVillagenamee()));
        //property/door/siteno
        //village/ward
        //registration distict
    }

    private boolean isEcDateIsAfterDeedDate(String executionDate, String executedate) {
        // Parse the date strings into LocalDateTime objects
        LocalDate executionDatelocalDate = parseDate(executionDate);
        LocalDate executeDatelocalDate = parseDate(executedate);
        return executionDatelocalDate.isAfter(executeDatelocalDate);
    }

    private boolean isEcDaterDeedDateEqual(String executionDate, String executedate) {
        // Parse the date strings into LocalDateTime objects
        LocalDate executionDatelocalDate = parseDate(executionDate);
        LocalDate executeDatelocalDate = parseDate(executedate);
        return executionDatelocalDate.isEqual(executeDatelocalDate);
    }

    private boolean compareSecondLevelOfData(DeedData deedResponse, DeedData ecDeedDetail) {
        return deedResponse.getPartyinfo().get(0).getPartyname().equalsIgnoreCase(ecDeedDetail.getPartyinfo().get(0).getPartyname());
    }
}


