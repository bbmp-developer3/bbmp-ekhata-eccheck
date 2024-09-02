package com.ekhata.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WitnessInfo {
    private String witnessid;
    private String name;
    private String houseno;
    private String address;
    private String pincode;
    private String age;
    private String profession;
    private int sex;
    private String relation;
    private String relativename;
}
