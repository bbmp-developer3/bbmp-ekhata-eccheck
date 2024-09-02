package com.ekhata.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeedData {
    private String applicationnumber;
    private String executedate;
    private String pendingdocumentnumber;
    private String finalregistrationnumber;
    private String registrationdatetime;
    private int pagecount;
    private String stamparticlename;
    private String naturedeed;
    private String book;
    private List<PropertyInfo> propertyinfo;
    private List<PartyInfo> partyinfo;
    private List<WitnessInfo> witnessinfo;
    private String applicationType;
    private int applicationTypeId;
    private String presentdate;
}
