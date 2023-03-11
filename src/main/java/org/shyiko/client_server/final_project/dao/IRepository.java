package org.shyiko.client_server.final_project.dao;

import org.shyiko.client_server.final_project.util.Criteria;
import org.shyiko.client_server.final_project.util.SortingType;
import org.shyiko.client_server.final_project.models.Product;
import org.shyiko.client_server.final_project.models.ProductGroup;

import java.util.List;

public interface IRepository {

    int createProduct(Product product);

    Product readProduct(int id);

    void updateProduct(Product product);

    void deleteProduct(int id);

    int createGroup(ProductGroup group);

    ProductGroup readGroup(int id);

    void updateGroup(ProductGroup group);

    void deleteGroup(int id);

    List<ProductGroup> listGroups(Criteria criteria, String value, SortingType type);


    List<Product> listProducts(Criteria criteria, String value, SortingType type);

    String totalInStorage();

    List<ProductGroup> totalInGroups();

}
