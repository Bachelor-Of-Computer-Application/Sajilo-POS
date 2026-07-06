package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.SummaryReportDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportDAO {

    /**
     * Get summary report for a date range
     * Includes total sales, total purchases, and calculated profit
     */
    public SummaryReportDTO getSummaryReport(LocalDate from, LocalDate to) {
        SummaryReportDTO summary = new SummaryReportDTO();
        
        // Query 1: Get total sales and transaction count
        String salesQuery = "SELECT COALESCE(SUM(final_amount), 0) AS total_sales, COUNT(*) AS total_transactions " +
                "FROM sales WHERE DATE(sale_date) BETWEEN ? AND ?";
        
        // Query 2: Get total purchases
        String purchaseQuery = "SELECT COALESCE(SUM(total_amount), 0) AS total_purchases FROM purchases WHERE DATE(purchase_date) BETWEEN ? AND ?";
        
        // Query 3: Get purchased quantities
        String purchaseQtyQuery = "SELECT COALESCE(SUM(quantity), 0) AS total_qty FROM purchase_items pi " +
                "INNER JOIN purchases p ON pi.purchase_id = p.purchase_id " +
                "WHERE DATE(p.purchase_date) BETWEEN ? AND ?";
        
        // Query 4: Get sold quantities
        String saleQtyQuery = "SELECT COALESCE(SUM(quantity), 0) AS total_qty FROM sale_items si " +
                "INNER JOIN sales s ON si.sale_id = s.sale_id " +
                "WHERE DATE(s.sale_date) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection()) {
            
            // Get sales data
            try (PreparedStatement stmt = conn.prepareStatement(salesQuery)) {
                stmt.setDate(1, Date.valueOf(from));
                stmt.setDate(2, Date.valueOf(to));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        summary.setTotalSales(rs.getDouble("total_sales"));
                        summary.setTotalTransactions(rs.getInt("total_transactions"));
                    }
                }
            }
            
            // Get purchases data
            try (PreparedStatement stmt = conn.prepareStatement(purchaseQuery)) {
                stmt.setDate(1, Date.valueOf(from));
                stmt.setDate(2, Date.valueOf(to));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        summary.setTotalPurchases(rs.getDouble("total_purchases"));
                    }
                }
            }
            
            // Get purchased quantities
            try (PreparedStatement stmt = conn.prepareStatement(purchaseQtyQuery)) {
                stmt.setDate(1, Date.valueOf(from));
                stmt.setDate(2, Date.valueOf(to));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        summary.setTotalProductsPurchased(rs.getInt("total_qty"));
                    }
                }
            }
            
            // Get sold quantities
            try (PreparedStatement stmt = conn.prepareStatement(saleQtyQuery)) {
                stmt.setDate(1, Date.valueOf(from));
                stmt.setDate(2, Date.valueOf(to));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        summary.setTotalProductsSold(rs.getInt("total_qty"));
                    }
                }
            }
            
            // Calculate profit
            summary.setProfit(summary.getTotalSales() - summary.getTotalPurchases());
            
        } catch (SQLException e) {
            System.err.println("Error fetching summary report: " + e.getMessage());
            e.printStackTrace();
        }

        return summary;
    }

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

    /**
     * Get total purchases for a date range
     */
    public double getTotalPurchases(LocalDate from, LocalDate to) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM purchases WHERE DATE(purchase_date) BETWEEN ? AND ?";
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

    /**
     * Get profit for a date range (Sales - Purchases)
     */
    public double getProfit(LocalDate from, LocalDate to) {
        double sales = getTotalSales(from, to);
        double purchases = getTotalPurchases(from, to);
        return sales - purchases;
    }
}
