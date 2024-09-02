package com.ekhata.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartyInfo {
    private String applicationnumber;
    private String partyid;
    private String partyname;
    private String age;
    private String address;
    private String profession;
    private String phonenumber;
    private String epic;
    private int partytypeid;
    private String partytypename;
    private boolean isexecutor;
    private boolean ispresenter;
    private String admissiondate;
    private boolean section88exemption;
    private String idprooftypedesc;
    private String idproofnumber;
    private String relationship;
    private String relativename;
    private boolean isorganization;
    private String tanno;
    private String auname;
    private String auaddress;
    private String salutation;
    private String poaname;
    private String minorguardianname;
    private int sex;
}
