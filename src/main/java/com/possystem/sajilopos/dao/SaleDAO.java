package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    /**
     * Save sale and its items to database
     */
    public int saveSale(Sale sale) {
        String saleSql = "INSERT INTO sales (company_id, customer_id, invoice_no, total_amount, " +
                "discount_amount, final_amount, payment_method, amount_paid, change_amount, sold_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, sale.getCompanyId());
            
            // Handle walk-in customer (customer_id = 0) - set to NULL
            if (sale.getCustomerId() <= 0) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, sale.getCustomerId());
            }
            
            stmt.setString(3, sale.getInvoiceNo());
            stmt.setDouble(4, sale.getTotalAmount());
            stmt.setDouble(5, sale.getDiscountAmount());
            stmt.setDouble(6, sale.getFinalAmount());
            stmt.setString(7, sale.getPaymentMethod());
            stmt.setDouble(8, sale.getAmountPaid());
            stmt.setDouble(9, sale.getChangeAmount());
            stmt.setInt(10, sale.getSoldBy());

            stmt.executeUpdate();

            // Get generated sale ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int saleId = rs.getInt(1);
                    saveSaleItems(saleId, sale.getItems());
                    return saleId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving sale: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Save sale items
     */
    private void saveSaleItems(int saleId, List<SaleItem> items) {
        String itemSql = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(itemSql)) {

            for (SaleItem item : items) {
                stmt.setInt(1, saleId);
                stmt.setInt(2, item.getProductId());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getUnitPrice());
                stmt.setDouble(5, item.getSubtotal());
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Saved " + items.size() + " sale items");
        } catch (SQLException e) {
            System.err.println("Error saving sale items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get all sales for a company
     */
    public List<Sale> getSalesByCompany(int companyId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE company_id = ? ORDER BY sale_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sale sale = new Sale(
                        rs.getInt("sale_id"),
                        rs.getInt("company_id"),
                        rs.getInt("customer_id"),
                        rs.getString("invoice_no"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount_amount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_method"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("change_amount"),
                        rs.getInt("sold_by"),
                        rs.getTimestamp("sale_date")
                    );
                    
                    // Load sale items
                    loadSaleItems(sale);
                    sales.add(sale);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting sales: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

    /**
     * Get sale by ID
     */
    public Sale getSaleById(int saleId) {
        String sql = "SELECT * FROM sales WHERE sale_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Sale sale = new Sale(
                        rs.getInt("sale_id"),
                        rs.getInt("company_id"),
                        rs.getInt("customer_id"),
                        rs.getString("invoice_no"),
                        rs.getDouble("total_amount"),
                        rs.getDouble("discount_amount"),
                        rs.getDouble("final_amount"),
                        rs.getString("payment_method"),
                        rs.getDouble("amount_paid"),
                        rs.getDouble("change_amount"),
                        rs.getInt("sold_by"),
                        rs.getTimestamp("sale_date")
                    );
                    
                    loadSaleItems(sale);
                    return sale;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting sale by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load sale items for a sale
     */
    private void loadSaleItems(Sale sale) {
        String sql = "SELECT * FROM sale_items WHERE sale_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sale.getSaleId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SaleItem item = new SaleItem(
                        rs.getInt("sale_item_id"),
                        rs.getInt("sale_id"),
                        rs.getInt("product_id"),
                        getProductName(rs.getInt("product_id")),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getDouble("subtotal")
                    );
                    sale.addItem(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading sale items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get product name by ID
     */
    private String getProductName(int productId) {
        String sql = "SELECT product_name FROM products WHERE product_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("product_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting product name: " + e.getMessage());
        }
        return "Unknown";
    }

    /**
     * Delete sale
     */
    public boolean deleteSale(int saleId, int companyId) {
        String sql = "DELETE FROM sales WHERE sale_id = ? AND company_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            stmt.setInt(2, companyId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting sale: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
