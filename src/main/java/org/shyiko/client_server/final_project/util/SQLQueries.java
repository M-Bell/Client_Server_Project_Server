package org.shyiko.client_server.final_project.util;

public class SQLQueries {
    public static final String DELETE_PRODUCT_STORAGE_TABLE = "DROP TABLE product_storage";
    public static final String CREATE_PRODUCT_STORAGE_TABLE = "CREATE TABLE if NOT EXISTS product_storage (group_id INTEGER PRIMARY KEY AUTOINCREMENT, group_name text UNIQUE, description text );";
    public static final String DELETE_PRODUCT_TABLE = "DROP TABLE product";
    public static final String CREATE_PRODUCT_TABLE = "CREATE TABLE if NOT EXISTS product " + "(id INTEGER PRIMARY KEY AUTOINCREMENT, product_name text UNIQUE, description text, manufacturer text, amount INTEGER DEFAULT 0, price DOUBLE DEFAULT 0.00, group_id INTEGER NOT NULL, FOREIGN KEY (group_id) REFERENCES product_storage (id));";
    public static final String GET_GROUP_NAME_BY_GROUP_ID = "SELECT group_name FROM product_storage WHERE group_id = ?";
    public static final String INSERT_PRODUCT = "INSERT INTO product (product_name, description, manufacturer, price, amount, group_id) VALUES (?, ?, ?, ?, ?, ?)";
    //public static final String READ_PRODUCT_BY_NAME = "SELECT * FROM product WHERE product_name = ?";
    public static final String READ_PRODUCT_BY_ID = "SELECT * FROM product WHERE id = ?";
    public static final String UPDATE_PRODUCT = "UPDATE product SET product_name = ?, description = ?, manufacturer = ?, price = ?, amount = ?, group_id = ? WHERE id = ? ";
    public static final String UPDATE_GROUP = "UPDATE product_storage SET group_name = ?, description = ? WHERE group_id = ? ";
    public static final String DELETE_PRODUCT = "DELETE FROM product WHERE id = ?";
    public static final String INSERT_GROUP = "INSERT INTO product_storage (group_name, description) VALUES (?, ?)";
    public static final String READ_GROUP = "SELECT * FROM product_storage WHERE group_id = ?";
    public static final String DELETE_GROUP = "DELETE FROM product_storage WHERE group_id = ?";
}
