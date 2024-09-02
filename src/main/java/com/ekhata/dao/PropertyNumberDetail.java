package com.ekhata.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyNumberDetail {
    private String propertynumberpropertyid;
    private int currentpropertytypeid;
    private String propertynumbertype;
    private String currentnumber;
    private String hissa_no;
    private int survey_no;
    private String propertynumberdescription;
}
