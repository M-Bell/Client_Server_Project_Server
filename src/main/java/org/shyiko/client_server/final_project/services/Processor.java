package org.shyiko.client_server.final_project.services;

import org.json.simple.parser.ParseException;
import org.shyiko.client_server.final_project.dao.ProductStorage;
import org.shyiko.client_server.final_project.models.Product;
import org.shyiko.client_server.final_project.models.ProductGroup;
import org.shyiko.client_server.final_project.protocol.Message;
import org.shyiko.client_server.final_project.util.Command;
import org.shyiko.client_server.final_project.util.Criteria;
import org.shyiko.client_server.final_project.util.ModelParser;
import org.shyiko.client_server.final_project.util.SortingType;

import java.util.List;

public class Processor implements IProcessor {

    private final ProductStorage storage = ProductStorage.getInstance();
    private static final String DEFAULT_RESPONSE = "OK";
    private String commandMessage;

    @Override
    public String process(Message message) {
        synchronized (storage) {
            Command currentCommand = Command.getCommandByCode(message.getCType());
            commandMessage = new String(message.getMessage());
            try {
                switch (currentCommand) {
                    case CREATE_PRODUCT -> {
                        return createProduct();
                    }
                    case READ_PRODUCT -> {
                        return readProduct();
                    }
                    case UPDATE_PRODUCT -> {
                        return updateProduct();
                    }
                    case DELETE_PRODUCT -> {
                        return deleteProduct();
                    }
                    case LIST_PRODUCT -> {
                        return listProduct();
                    }
                    case CREATE_GROUP -> {
                        return createGroup();
                    }
                    case READ_GROUP -> {
                        return readGroup();
                    }
                    case UPDATE_GROUP -> {
                        return updateGroup();
                    }
                    case DELETE_GROUP -> {
                        return deleteGroup();
                    }
                    case LIST_GROUP -> {
                        return listGroup();
                    }
                    case INCREMENT_PRODUCT -> {
                        return incrementProduct();
                    }
                    case DECREMENT_PRODUCT -> {
                        return decrementProduct();
                    }
                    case TOTAL_IN_GROUPS -> {
                        return totalInGroups();
                    }
                    case TOTAL_IN_STORAGE -> {
                        return totalInStorage();
                    }
                }
            } catch (NumberFormatException | ClassCastException e) {
                return "INVALID MESSAGE";
            }
        }
        return DEFAULT_RESPONSE;
    }

    private String totalInStorage() {
        return storage.totalInStorage();
    }

    private String totalInGroups() {
        StringBuilder response = new StringBuilder();
        List<ProductGroup> result = storage.totalInGroups();
        if (result.size() < 1) return "Error 404: Not found";
        for (ProductGroup group : result) {
            response.append(ModelParser.parseGroupToJSON(group)).append("\n");
        }
        return response.toString();
    }

    private String decrementProduct() {
        try {
            String[] message = commandMessage.split("\\+");
            int id = Integer.parseInt(message[0]);
            int amount = Integer.parseInt(message[1]);
            Product updatedProduct = storage.readProduct(id);
            if (updatedProduct == null) return "No product";
            if (updatedProduct.getAmount() - amount < 0) return "Invalid message";
            updatedProduct.setAmount(updatedProduct.getAmount() - amount);
            storage.updateProduct(updatedProduct);
            return "Product decremented";
        } catch (NumberFormatException e) {
            return "Invalid message";
        }
    }

    private String incrementProduct() {
        try {
            String[] message = commandMessage.split("\\+");
            int id = Integer.parseInt(message[0]);
            int amount = Integer.parseInt(message[1]);
            Product updatedProduct = storage.readProduct(id);
            if (updatedProduct == null) return "No product";
            updatedProduct.setAmount(updatedProduct.getAmount() + amount);
            storage.updateProduct(updatedProduct);
            return "Product incremented";
        } catch (NumberFormatException e) {
            return "Invalid message";
        }
    }

    private String createProduct() throws ClassCastException {
        try {
            Product product = ModelParser.parseJSONToProduct(commandMessage);
            if (validateUniqueProduct(product) && validateProduct(product)) {
                int id = storage.createProduct(product);
                return "Product created with id: " + id;
            }
            return "Invalid message";
        } catch (ParseException e) {
            return "Invalid message";
        }
    }

    private boolean validateProduct(Product product) {
        if (product.getName().isEmpty()) return false;
        if (product.getAmount() < 0) return false;
        if (product.getPrice() < 0) return false;
        return storage.readGroup(product.getGroupID()) != null;
    }

    private boolean validateUniqueProduct(Product product) {
        List<Product> res = storage.listProducts(Criteria.NAME, product.getName(), SortingType.ASC);
        for (Product prod : res) {
            if (prod.getName().equals(product.getName()) && prod.getId() != product.getId()) {
                return false;
            }
        }
        return true;
    }

    private String readProduct() throws ClassCastException {
        try {
            int id = Integer.parseInt(commandMessage);
            return ModelParser.parseProductToJSON(storage.readProduct(id));
        } catch (Exception e) {
            return "Invalid message";
        }
    }

    private String updateProduct() throws ClassCastException {
        try {
            Product product = ModelParser.parseJSONToProduct(commandMessage);
            if (validateUniqueProduct(product) && validateProduct(product)) {
                storage.updateProduct(product);
                return "Product updated";
            } else {
                return "Invalid message";
            }
        } catch (ParseException e) {
            return "Invalid message";
        }
    }

    private String deleteProduct() throws ClassCastException {
        try {
            int id = Integer.parseInt(commandMessage);
            if (storage.readProduct(id) != null) {
                storage.deleteProduct(id);
                return "Product deleted";
            } else {
                return "Not found";
            }
        } catch (NumberFormatException e) {
            return "Invalid message";
        }
    }

    private String createGroup() {
        try {
            ProductGroup group = ModelParser.parseJSONToGroup(commandMessage);
            if (validateUniqueGroup(group)) {
                int id = storage.createGroup(group);
                return "Group created with id: " + id;
            } else {
                return "Group already exists";

            }
        } catch (ParseException e) {
            return "Invalid message";
        }
    }

    private boolean validateUniqueGroup(ProductGroup group) {
        List<ProductGroup> res = storage.listGroups(Criteria.NAME, group.getName(), SortingType.ASC);
        for (ProductGroup resultGroup : res) {
            if (resultGroup.getName().equals(group.getName()) && resultGroup.getId() != group.getId()) {
                return false;
            }
        }
        return true;
    }

    private String readGroup() {
        try {
            int id = Integer.parseInt(commandMessage);
            return ModelParser.parseGroupToJSON(storage.readGroup(id));
        } catch (Exception e) {
            return "Invalid id";
        }
    }

    private String updateGroup() {
        try {
            ProductGroup group = ModelParser.parseJSONToGroup(commandMessage);
            if (validateGroup(group)) {
                storage.updateGroup(group);
                return "Group updated";
            } else {
                return "Invalid message";
            }

        } catch (ParseException e) {
            return "Invalid message";
        }
    }

    private boolean validateGroup(ProductGroup group) {
        return validateUniqueGroup(group) && !group.getName().isEmpty();
    }

    private String deleteGroup() {
        try {
            int id = Integer.parseInt(commandMessage);
            if (storage.readGroup(id) != null) {
                storage.deleteGroup(id);
                return "Group deleted";
            } else {
                return "Not found";
            }

        } catch (NumberFormatException e) {
            return "Invalid message";
        }
    }


    private String listGroup() {
        try {
            String[] query = commandMessage.split("\\+");
            Criteria criteria = Criteria.getCommandByCode(Integer.parseInt(query[0]));
            String value = query[1];
            SortingType sortType = Integer.parseInt(query[2]) == 0 ? SortingType.ASC : SortingType.DESC;
            StringBuilder response = new StringBuilder();
            List<ProductGroup> result = storage.listGroups(criteria, value, sortType);
            if (result.size() < 1) return "Error 404: Not found";
            for (ProductGroup group : result) {
                response.append(ModelParser.parseGroupToJSON(group)).append("\n");
            }
            return response.toString();
        } catch (NumberFormatException e) {
            return "Invalid message";
        }
    }


    private String listProduct() throws ClassCastException {
        try {
            String[] query = commandMessage.split("\\+");
            Criteria criteria = Criteria.getCommandByCode(Integer.parseInt(query[0]));
            String value = query[1];
            SortingType sortType = Integer.parseInt(query[2]) == 0 ? SortingType.ASC : SortingType.DESC;
            StringBuilder response = new StringBuilder();
            List<Product> result = storage.listProducts(criteria, value, sortType);
            if (result.size() < 1) return "Error 404: Not found";

            for (Product product : result) {
                response.append(ModelParser.parseProductToJSON(product)).append("\n");
            }
            return response.toString();
        } catch (NumberFormatException e) {
            return "Invalid message";
        }
    }

}
