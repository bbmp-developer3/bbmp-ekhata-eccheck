package com.ekhata.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertySchedule {
    private String propertyschedulespropertyid;
    private String scheduletype;
    private double totalarea;
    private String scheduledescription;
    private String scheduleparties;
    private String bhoomisellerparty;
    private String name;
    private String eastboundary;
    private String westboundary;
    private String northboundary;
    private String southboundary;
}
