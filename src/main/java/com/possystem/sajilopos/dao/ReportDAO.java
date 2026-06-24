package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportDAO {

    
    public double getTotalSales(LocalDate from, LocalDate to) {
        String sql = "SELECT SUM(final_amount) FROM sales WHERE DATE(sale_date) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    
    public int getTotalTransactions(LocalDate from, LocalDate to) {
        String sql = "SELECT COUNT(*) FROM sales WHERE DATE(sale_date) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getTopSellingProducts(LocalDate from, LocalDate to, int limit) {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT p.product_name, SUM(si.quantity) AS total_qty " +
                     "FROM sale_items si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "JOIN sales s ON si.sale_id = s.sale_id " +
                     "WHERE DATE(s.sale_date) BETWEEN ? AND ? " +
                     "GROUP BY p.product_name ORDER BY total_qty DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("product_name"), rs.getInt("total_qty"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Double> getDailySalesSummary(LocalDate from, LocalDate to) {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT DATE(sale_date) AS sale_day, SUM(final_amount) AS daily_total " +
                     "FROM sales WHERE DATE(sale_date) BETWEEN ? AND ? " +
                     "GROUP BY DATE(sale_date) ORDER BY sale_day";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("sale_day"), rs.getDouble("daily_total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
