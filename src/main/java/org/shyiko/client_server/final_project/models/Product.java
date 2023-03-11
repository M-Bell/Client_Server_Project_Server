package org.shyiko.client_server.final_project.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Product implements Serializable {
    private int id;
    private String name;
    private String description;
    private String manufacturer;
    private int amount;
    private double price;
    private int groupID;
}
