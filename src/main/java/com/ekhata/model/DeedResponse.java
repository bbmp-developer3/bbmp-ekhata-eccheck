package com.ekhata.model;

import com.ekhata.dao.DeedData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeedResponse {
    private DeedData deedData;
}
