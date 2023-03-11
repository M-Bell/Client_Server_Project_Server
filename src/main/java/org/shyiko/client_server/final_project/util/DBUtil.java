package org.shyiko.client_server.final_project.util;

import org.shyiko.client_server.final_project.dao.ProductStorage;
import org.shyiko.client_server.final_project.models.Product;
import org.shyiko.client_server.final_project.models.ProductGroup;

import java.sql.SQLException;

public class DBUtil {
    private static ProductStorage storage = ProductStorage.getInstance();

    public static void setDBForTest() {
        try {
            storage.resetDB();
            storage.createGroup(new ProductGroup(1, "Group 1", ""));
            storage.createProduct(new Product(1, "Prod 1.1", "My product", "Test", 1, 500, 1));
            storage.createProduct(new Product(2, "Prod 1.2", "Desc", "Test 2", 1, 11, 1));
            storage.createProduct(new Product(3, "Prod 1.3", "Blank", "", 11, 23, 1));
            storage.createGroup(new ProductGroup(2, "Group 2", "Sec group"));
            storage.createProduct(new Product(4, "Prod 2.1", "", "", 431, 11.99, 2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
