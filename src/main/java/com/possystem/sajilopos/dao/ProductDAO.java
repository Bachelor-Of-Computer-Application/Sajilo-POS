package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    /**
     * Get all active products for a specific company
     */
    public List<Product> getAllProducts(int companyId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, company_id, product_name, price, stock, description, " +
                     "active, created_at, updated_at FROM products WHERE company_id = ? AND active = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("company_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description"),
                        rs.getBoolean("active"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Get a product by its ID
     */
    public Product getProductById(int productId) {
        String sql = "SELECT product_id, company_id, product_name, price, stock, description, " +
                     "active, created_at, updated_at FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getInt("company_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description"),
                        rs.getBoolean("active"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a new product
     */
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (company_id, product_name, price, stock, description, active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, product.getCompanyId());
            stmt.setString(2, product.getProductName());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.setString(5, product.getDescription());
            stmt.setBoolean(6, product.isActive());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update an existing product
     */
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET product_name = ?, price = ?, stock = ?, description = ? " +
                     "WHERE product_id = ? AND company_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getProductName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setString(4, product.getDescription());
            stmt.setInt(5, product.getProductId());
            stmt.setInt(6, product.getCompanyId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Soft delete a product (set active = 0)
     */
    public boolean deleteProduct(int productId, int companyId) {
        String sql = "UPDATE products SET active = 0 WHERE product_id = ? AND company_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            stmt.setInt(2, companyId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hard delete a product (permanently remove from database)
     */
    public boolean hardDeleteProduct(int productId, int companyId) {
        String sql = "DELETE FROM products WHERE product_id = ? AND company_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            stmt.setInt(2, companyId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error hard deleting product: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Search products by name
     */
    public List<Product> searchProductByName(String name, int companyId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, company_id, product_name, price, stock, description, " +
                     "active, created_at, updated_at FROM products " +
                     "WHERE product_name LIKE ? AND company_id = ? AND active = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            stmt.setInt(2, companyId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getInt("company_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getString("description"),
                        rs.getBoolean("active"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
            e.printStackTrace();
        }
        return products;
    }

    /**
     * Update stock quantity for a product
     */
    public boolean updateStock(int productId, int newStock, int companyId) {
        String sql = "UPDATE products SET stock = ? WHERE product_id = ? AND company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            stmt.setInt(3, companyId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
