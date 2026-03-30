package com.domexa.property_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Property {
    private String id;
    private String address;
    private String owner;
    private double rent;
    private boolean available;
    private String type;
}
