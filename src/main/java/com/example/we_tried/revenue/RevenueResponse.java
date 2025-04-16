package com.example.we_tried.revenue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class RevenueResponse {
    private String date;
    private BigDecimal revenue;


}