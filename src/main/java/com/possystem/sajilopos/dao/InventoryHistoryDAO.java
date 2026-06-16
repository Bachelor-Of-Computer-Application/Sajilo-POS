package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.InventoryHistory;
import com.possystem.sajilopos.model.InventoryHistory.ActionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryHistoryDAO {

    /**
     * Add inventory history record
     */
    public boolean addHistory(InventoryHistory history) {
        String sql = "INSERT INTO inventory_history (company_id, product_id, action_type, quantity, remarks) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, history.getCompanyId());
            stmt.setInt(2, history.getProductId());
            stmt.setString(3, history.getActionType().name());
            stmt.setInt(4, history.getQuantity());
            stmt.setString(5, history.getRemarks());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding inventory history: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get recent inventory history for a company (with product names)
     */
    public List<InventoryHistory> getRecentHistory(int companyId, int limit) {
        List<InventoryHistory> history = new ArrayList<>();
        String sql = "SELECT ih.history_id, ih.company_id, ih.product_id, p.product_name, " +
                     "ih.action_type, ih.quantity, ih.remarks, ih.created_at " +
                     "FROM inventory_history ih " +
                     "JOIN products p ON ih.product_id = p.product_id " +
                     "WHERE ih.company_id = ? " +
                     "ORDER BY ih.created_at DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(new InventoryHistory(
                        rs.getInt("history_id"),
                        rs.getInt("company_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        ActionType.valueOf(rs.getString("action_type")),
                        rs.getInt("quantity"),
                        rs.getString("remarks"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching inventory history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Get history for a specific product
     */
    public List<InventoryHistory> getProductHistory(int productId, int companyId) {
        List<InventoryHistory> history = new ArrayList<>();
        String sql = "SELECT ih.history_id, ih.company_id, ih.product_id, p.product_name, " +
                     "ih.action_type, ih.quantity, ih.remarks, ih.created_at " +
                     "FROM inventory_history ih " +
                     "JOIN products p ON ih.product_id = p.product_id " +
                     "WHERE ih.product_id = ? AND ih.company_id = ? " +
                     "ORDER BY ih.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(new InventoryHistory(
                        rs.getInt("history_id"),
                        rs.getInt("company_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        ActionType.valueOf(rs.getString("action_type")),
                        rs.getInt("quantity"),
                        rs.getString("remarks"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Get today's history
     */
    public List<InventoryHistory> getTodayHistory(int companyId) {
        List<InventoryHistory> history = new ArrayList<>();
        String sql = "SELECT ih.history_id, ih.company_id, ih.product_id, p.product_name, " +
                     "ih.action_type, ih.quantity, ih.remarks, ih.created_at " +
                     "FROM inventory_history ih " +
                     "JOIN products p ON ih.product_id = p.product_id " +
                     "WHERE ih.company_id = ? AND DATE(ih.created_at) = CURDATE() " +
                     "ORDER BY ih.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                history.add(new InventoryHistory(
                        rs.getInt("history_id"),
                        rs.getInt("company_id"),
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        ActionType.valueOf(rs.getString("action_type")),
                        rs.getInt("quantity"),
                        rs.getString("remarks"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching today's history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }
}
