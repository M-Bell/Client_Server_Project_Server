package org.shyiko.client_server.final_project.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ProductGroup implements Serializable {
    private int id = 0;
    private String name = "";
    private String description = "";
    private int total = 0;

    public ProductGroup(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public ProductGroup(String name, int total) {
        this.name = name;
        this.total = total;
    }
}
