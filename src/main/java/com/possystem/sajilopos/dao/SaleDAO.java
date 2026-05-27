package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;

import java.sql.*;

/**
 * Sale Data Access Object
 * Handles database operations for sales and sale items
 */
public class SaleDAO {

    /**
     * Save a sale transaction with all its items
     * 
     * @param sale Sale object containing items and details
     * @return true if successfully saved, false otherwise
     */
    public boolean saveSale(Sale sale) {
        String saleSql = "INSERT INTO sales (user_id, total_amount, discount, final_amount, sale_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO sale_items (sale_id, product_id, quantity, subtotal) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                System.err.println("Failed to establish database connection");
                return false;
            }

            conn.setAutoCommit(false);

            // Save the sale
            try (PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                saleStmt.setInt(1, sale.getUserId());
                saleStmt.setDouble(2, sale.getTotalAmount());
                saleStmt.setDouble(3, sale.getDiscount());
                saleStmt.setDouble(4, sale.getFinalAmount());
                saleStmt.setTimestamp(5, Timestamp.valueOf(sale.getSaleDate()));
                saleStmt.executeUpdate();

                // Get generated sale ID
                try (ResultSet keys = saleStmt.getGeneratedKeys()) {
                    int saleId = 0;
                    if (keys.next()) {
                        saleId = keys.getInt(1);
                    }

                    // Save each sale item
                    try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                        for (SaleItem item : sale.getItems()) {
                            itemStmt.setInt(1, saleId);
                            itemStmt.setInt(2, item.getProduct().getId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.setDouble(4, item.getSubtotal());
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
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
}
