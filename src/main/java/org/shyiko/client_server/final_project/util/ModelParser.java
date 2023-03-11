package org.shyiko.client_server.final_project.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.shyiko.client_server.final_project.models.Product;
import org.shyiko.client_server.final_project.models.ProductGroup;

public class ModelParser {
    public static String parseProductToJSON(Product product) {
        JSONObject object = new JSONObject();
        object.put("ID", product.getId());
        object.put("Name", product.getName());
        object.put("Description", product.getDescription());
        object.put("Manufacturer", product.getManufacturer());
        object.put("Price", product.getPrice());
        object.put("Amount", product.getAmount());
        object.put("Group", product.getGroupID());
        return object.toJSONString();
    }

    public static Product parseJSONToProduct(String json) throws ParseException, ClassCastException, NumberFormatException {
        Object object = new org.json.simple.parser.JSONParser().parse(json);
        int id;
        JSONObject jsonObject = (JSONObject) object;
        if (jsonObject.containsKey("ID")) {
            try {
                id = Integer.parseInt((String) jsonObject.get("ID"));
            } catch (ClassCastException e) {
                id = Integer.parseInt(String.valueOf(jsonObject.get("ID")));

            }
        } else {
            id = 0;
        }
        String productName = (String) jsonObject.get("Name");
        String description = (String) jsonObject.get("Description");
        String manufacturer = (String) jsonObject.get("Manufacturer");
        double price;
        try {
            price = Double.parseDouble((String) jsonObject.get("Price"));
        } catch (ClassCastException e) {
            price = Double.parseDouble(String.valueOf(jsonObject.get("Price")));

        }
        int amount;
        try {
            amount = Integer.parseInt((String) jsonObject.get("Amount"));
        } catch (ClassCastException e) {
            amount = Integer.parseInt(String.valueOf(jsonObject.get("Amount")));

        }

        int group;
        try {
            group = Integer.parseInt((String) jsonObject.get("Group"));
        } catch (ClassCastException e) {
            group = Integer.parseInt(String.valueOf(jsonObject.get("Group")));
        }

        return new Product(id, productName, description, manufacturer, amount, price, group);
    }

    public static String parseGroupToJSON(ProductGroup group) {
        JSONObject object = new JSONObject();
        object.put("ID", group.getId());
        object.put("Name", group.getName());
        object.put("Description", group.getDescription());
        object.put("Total", group.getTotal());
        return object.toJSONString();
    }

    public static ProductGroup parseJSONToGroup(String json) throws ParseException {
        Object object = new org.json.simple.parser.JSONParser().parse(json);
        JSONObject jsonObject = (JSONObject) object;
        int id;
        if (jsonObject.containsKey("ID")) {
            id = Integer.parseInt((String) jsonObject.get("ID"));
        } else {
            id = 0;
        }
        String groupName = (String) jsonObject.get("Name");
        String description = (String) jsonObject.get("Description");
        return new ProductGroup(id, groupName, description);
    }
}
