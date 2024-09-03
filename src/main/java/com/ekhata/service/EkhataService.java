package com.ekhata.service;

import com.ekhata.dao.DeedData;
import com.ekhata.dao.EcDeedDetail;
import com.ekhata.model.EcResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
        return false;
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
    public String processDeedData(String deed_number, String ec_number) {
        DeedData deedData = callDeedApi(deed_number);
        List<EcDeedDetail> ecDeedDetails = callEcDocApi(ec_number);
        return continueProcess(ecDeedDetails, null, deedData);
    }

    public String continueProcess(List<EcDeedDetail> ecDetails, EcDeedDetail ecDeedDetail, DeedData deedResponse)  {
        if(ecDeedDetail != null) {
            ecDetails.remove(ecDeedDetail);
        }
        Optional<EcDeedDetail> latestEcDeedDetailOptional = findLatestEcDeedDetail(ecDetails);

        if(latestEcDeedDetailOptional.isPresent()) {
            EcDeedDetail latestEcDeedDetail = latestEcDeedDetailOptional.get();
            //call deed API to fetch EcDetails for maxExeDate.getDocSummary()
            DeedData newDeedData = callDeedApi(latestEcDeedDetail.getDocSummary());

            if(isEcDateIsAfterDeedDate(latestEcDeedDetail.getExecutionDate(), deedResponse.getExecutedate())) {
                return continueProcessFurther(newDeedData, ecDetails, latestEcDeedDetail, deedResponse);
            }
            //check if both dates are equal
            else if(isEcDaterDeedDateEqual(latestEcDeedDetail.getExecutionDate(), deedResponse.getExecutedate())) {
                //check if document number is same in both doc
                return compareDeedData(newDeedData, deedResponse, ecDetails, latestEcDeedDetail);
            } else {
                return "No Further Transaction as per EC given by citizen";
            }
        } else {
            throw new RuntimeException("No Data Matched");
        }
    }

    private String compareDeedData(DeedData newDeedData, DeedData deedResponse, List<EcDeedDetail> ecDetails, EcDeedDetail maxEcDeedDetail) {
        if (newDeedData.getFinalregistrationnumber().equalsIgnoreCase(deedResponse.getFinalregistrationnumber())) {
            return "No Further Transaction as per EC given by citizen";
        } else {
            return continueProcessFurther(newDeedData, ecDetails, maxEcDeedDetail, deedResponse);
        }
    }

    private Optional<EcDeedDetail> findLatestEcDeedDetail(List<EcDeedDetail> ecDetails) {
        return ecDetails.stream()
                .max(Comparator.comparing(deed -> parseDate(deed.getExecutionDate())));
    }

    private String continueProcessFurther(DeedData newDeedData, List<EcDeedDetail> ecDetails, EcDeedDetail latestEcDeedDetail, DeedData deedResponse) {
        //check the type of deed article
        String article = newDeedData.getPropertyinfo().get(0).getStamparticle();
        //call API to check deed article - Is it of type "USe" or "Not Use"
        boolean articleInUse = callApiToCheckDeedArticle(article);
        //if type is "use"
        //compare deeddata woth newDeedData
        if (articleInUse) {
            return evaluateDeedData(newDeedData, deedResponse, ecDetails, latestEcDeedDetail);
        } else {
            //if type is "not use"
            //continue to next latest date of List<ecdeeddata>
            return continueProcess(ecDetails, latestEcDeedDetail, deedResponse);
        }
    }

    private String evaluateDeedData(DeedData newDeedData, DeedData deedResponse, List<EcDeedDetail> ecDetails, EcDeedDetail maxEcDeedDetail) {
        if (compareFirstLevelOfData(deedResponse, newDeedData)) {
            if (compareSecondLevelOfData(deedResponse, newDeedData)) {
                return "EC Success";
            } else {
                return "EC shows property further transacted, cannot process your application. Meet jurisdictional ARO.";
            }
        } else {
            return "Enter proper EC as property in EC doesn't match property in registered deed.";
        }
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


