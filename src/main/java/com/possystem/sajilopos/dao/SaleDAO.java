package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;
import com.possystem.sajilopos.util.DBConnection;

import java.sql.*;

public class SaleDAO {

    public boolean saveSale(Sale sale) {
        String saleSql = "INSERT INTO sales (total_amount, discount, final_amount, sale_date) VALUES (?, ?, ?, ?)";
        String itemSql = "INSERT INTO sale_items (sale_id, product_id, quantity, subtotal) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Save the sale
            PreparedStatement saleStmt = conn.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS);
            saleStmt.setDouble(1, sale.getTotalAmount());
            saleStmt.setDouble(2, sale.getDiscount());
            saleStmt.setDouble(3, sale.getFinalAmount());
            saleStmt.setTimestamp(4, Timestamp.valueOf(sale.getSaleDate()));
            saleStmt.executeUpdate();

            // Get generated sale ID
            ResultSet keys = saleStmt.getGeneratedKeys();
            int saleId = 0;
            if (keys.next()) saleId = keys.getInt(1);

            // Save each sale item
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            for (SaleItem item : sale.getItems()) {
                itemStmt.setInt(1, saleId);
                itemStmt.setInt(2, item.getProduct().getId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getSubtotal());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
