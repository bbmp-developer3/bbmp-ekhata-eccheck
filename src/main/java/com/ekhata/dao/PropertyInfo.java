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
public class PropertyInfo {
    private String applicationnumber;
    private String propertyid;
    private String documentid;
    private String villagenamee;
    private int propertytypeid;
    private String propertytype;
    private String sroname;
    private String northboundary;
    private String southboundary;
    private String eastboundary;
    private String westboundary;
    private String landmark;
    private int consideration;
    private int marketvalue;
    private int sroconsideration;
    private int sromarketvalue;
    private int cessduty;
    private int govtduty;
    private int additionalduty;
    private int stampduty;
    private int duplicatecopies;
    private int duplicatefee;
    private int duplicatestampduty;
    private int duplicateregistrationfee;
    private String valuationreport;
    private int sronoofscanpages;
    private String hobli;
    private String stamparticle;
    private String denodescription;
    private String estampdescription;
    private String adjudescription;
    private String zonenamee;
    private List<PropertySchedule> propertyschedules;
    private List<PropertyNumberDetail> propertynumberdetails;
}
