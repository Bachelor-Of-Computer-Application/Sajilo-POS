package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;

import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import java.sql.*;

public class SaleDAO {

    public boolean saveSale(Sale sale, Integer customerId) {
        String saleSql = "INSERT INTO sales (company_id, user_id, customer_id, total_amount, discount, final_amount, sale_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO sale_items (sale_id, product_id, quantity, subtotal) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Failed to establish database connection");
                return false;
            }

            conn.setAutoCommit(false);

            try (PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setInt(1, sale.getCompanyId());
                saleStmt.setInt(2, sale.getUserId());
                
                // Set customer_id (can be null)
                if (customerId != null) {
                    saleStmt.setInt(3, customerId);
                } else {
                    saleStmt.setNull(3, Types.INTEGER);
                }
                
                saleStmt.setDouble(4, sale.getTotalAmount());
                saleStmt.setDouble(5, sale.getDiscount());
                saleStmt.setDouble(6, sale.getFinalAmount());
                saleStmt.setTimestamp(7, Timestamp.valueOf(sale.getSaleDate()));
                saleStmt.executeUpdate();

                try (ResultSet keys = saleStmt.getGeneratedKeys()) {
                    int saleId = 0;
                    if (keys.next()) {
                        saleId = keys.getInt(1);
                    }

                    try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                        for (SaleItem item : sale.getItems()) {
                            itemStmt.setInt(1, saleId);
                            itemStmt.setInt(2, item.getProduct().getProductId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.setDouble(4, item.getSubtotal());
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
                    }
                    
                    // Calculate and add loyalty points if customer is associated
                    if (customerId != null) {
                        int loyaltyPoints = calculateLoyaltyPoints(sale.getFinalAmount());
                        addLoyaltyPointsToCustomer(conn, customerId, loyaltyPoints);
                        System.out.println("Added " + loyaltyPoints + " loyalty points to customer " + customerId);
                    }
                }
            }

            conn.commit();
            System.out.println("Sale saved successfully. Sale items: " + sale.getItems().size());
            return true;

        } catch (SQLException e) {
            System.err.println("Error saving sale: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Overload for backward compatibility
    public boolean saveSale(Sale sale) {
        return saveSale(sale, null);
    }
    
    /**
     * Calculate loyalty points based on purchase amount
     * Rule: 1 point for every Rs. 100 spent
     */
    private int calculateLoyaltyPoints(double finalAmount) {
        return (int) (finalAmount / 100);
    }
    
    /**
     * Add loyalty points to customer within the same transaction
     */
    private void addLoyaltyPointsToCustomer(Connection conn, int customerId, int points) throws SQLException {
        String sql = "UPDATE customers SET loyalty_points = loyalty_points + ? WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setInt(2, customerId);
            stmt.executeUpdate();
        }
    }

    public List<Sale> getSalesHistory() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, company_id, user_id, total_amount, discount, final_amount, sale_date " +
                "FROM sales ORDER BY sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Sale sale = new Sale(
                        rs.getInt("sale_id"),
                        new ArrayList<>(),
                        rs.getDouble("discount"),
                        rs.getInt("user_id"));
                sales.add(sale);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sales history: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

    public List<Sale> getSalesByDateRange(LocalDate from, LocalDate to) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, company_id, user_id, total_amount, discount, final_amount, sale_date " +
                "FROM sales WHERE DATE(sale_date) BETWEEN ? AND ? ORDER BY sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Sale sale = new Sale(
                        rs.getInt("sale_id"),
                        new ArrayList<>(),
                        rs.getDouble("discount"),
                        rs.getInt("user_id"));
                sales.add(sale);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sales by date: " + e.getMessage());
            e.printStackTrace();
        }
        return sales;
    }

}
