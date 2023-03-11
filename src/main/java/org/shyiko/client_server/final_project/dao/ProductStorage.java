package org.shyiko.client_server.final_project.dao;

import lombok.Data;
import org.shyiko.client_server.final_project.util.Criteria;
import org.shyiko.client_server.final_project.util.SQLQueries;
import org.shyiko.client_server.final_project.util.SortingType;
import org.shyiko.client_server.final_project.models.Product;
import org.shyiko.client_server.final_project.models.ProductGroup;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class ProductStorage implements IRepository {
    private static Connection connection;
    private static volatile ProductStorage instance;
    private static final Object mutex = new Object();

    private ProductStorage() {
        try {
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void openConnect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:storage");
    }

    private static void closeConnect() throws SQLException {
        connection.close();
    }

    public void resetDB() throws SQLException {
        openConnect();
        PreparedStatement st = connection.prepareStatement(SQLQueries.DELETE_PRODUCT_STORAGE_TABLE);
        st.executeUpdate();
        st = connection.prepareStatement(SQLQueries.DELETE_PRODUCT_TABLE);
        st.executeUpdate();
        closeConnect();
        init();
    }

    private void init() throws SQLException {
        openConnect();
        PreparedStatement st = connection.prepareStatement(SQLQueries.CREATE_PRODUCT_STORAGE_TABLE);
        st.executeUpdate();
        st = connection.prepareStatement(SQLQueries.CREATE_PRODUCT_TABLE);
        st.executeUpdate();
        closeConnect();
    }

    public static ProductStorage getInstance() {
        ProductStorage result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) instance = result = new ProductStorage();
            }
        }
        return result;
    }

    Set<ProductGroup> allProductGroups = new HashSet<>();


    @Override
    public int createProduct(Product product) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getManufacturer());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getAmount());
            statement.setInt(6, product.getGroupID());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                //returns id of inserted row
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public Product readProduct(int id) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.READ_PRODUCT_BY_ID);

            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new Product(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getInt(5),
                        result.getDouble(6),
                        result.getInt(7));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void updateProduct(Product product) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.UPDATE_PRODUCT);

            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getManufacturer());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getAmount());
            statement.setInt(6, product.getGroupID());
            statement.setInt(7, product.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteProduct(int id) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.DELETE_PRODUCT);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int createGroup(ProductGroup group) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.INSERT_GROUP, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            statement.executeUpdate();
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                //returns id of inserted row
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public ProductGroup readGroup(int id) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.READ_GROUP);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new ProductGroup(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void updateGroup(ProductGroup group) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.UPDATE_GROUP);

            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            statement.setInt(3, group.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deleteGroup(int id) {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement(SQLQueries.DELETE_GROUP);
            statement.setInt(1, id);
            statement.executeUpdate();
            statement = connection.prepareStatement("DELETE FROM product WHERE group_id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<ProductGroup> listGroups(Criteria criteria, String value, SortingType type) {
        String columnCriteria;
        String sqlQuery = "SELECT * FROM product_storage";
        try {
            openConnect();


            if (!value.isEmpty() && !value.isBlank()) {
                if (criteria == Criteria.NAME) {
                    columnCriteria = "group_name LIKE '%" + value + "%'";
                } else {
                    columnCriteria = "group_id = " + value;
                }
                sqlQuery += " WHERE " + columnCriteria;
            }
            sqlQuery += " ORDER BY group_id ASC";
            List<ProductGroup> resultList = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                resultList.add(new ProductGroup(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3)));
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Product> listProducts(Criteria criteria, String value, SortingType type) {
        String columnCriteria;
        String sqlQuery = "SELECT * FROM product";
        try {
            openConnect();


            if (!value.isEmpty() && !value.isBlank()) {

                switch (criteria) {
                    case NAME -> columnCriteria = "product_name LIKE '%" + value + "%'";
                    case PRICE -> columnCriteria = "price = " + value;
                    case GROUP -> columnCriteria = "group_id = " + value;
                    case AMOUNT -> columnCriteria = "amount = " + value;
                    default -> columnCriteria = "id = " + value;
                }
                sqlQuery += " WHERE " + columnCriteria;
            }
            sqlQuery += " ORDER BY id ASC";
            List<Product> resultList = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                resultList.add(new Product(
                        result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getInt(5),
                        result.getDouble(6),
                        result.getInt(7)));
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public String totalInStorage() {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement("SELECT SUM(price * amount) FROM product");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "Something went wrong";
    }

    @Override
    public List<ProductGroup> totalInGroups() {
        try {
            openConnect();
            PreparedStatement statement = connection.prepareStatement("SELECT prod_group.group_name," +
                    " SUM(prod.price * prod.amount) FROM product prod ,product_storage prod_group WHERE prod.group_id = prod_group.group_id" +
                    " GROUP BY prod_group.group_name");
            ResultSet result = statement.executeQuery();
            List<ProductGroup> resultList = new ArrayList<>();
            while (result.next()) {
                resultList.add(new ProductGroup(result.getString(1), result.getInt(2)));
            }
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnect();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}
