package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.ProductPerformanceDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Product Performance Reports
 * Handles database queries for product-level sales and purchase analysis
 */
public class ProductReportDAO {

    /**
     * Get product performance metrics for a date range
     * Includes purchase and sales data for each product
     * Profit = Total Sales Amount for Product - Total Purchase Amount for Product (NOT whole purchase)
     */
    public List<ProductPerformanceDTO> getProductPerformanceReport(LocalDate from, LocalDate to) {
        List<ProductPerformanceDTO> products = new ArrayList<>();
        
        String sql = "SELECT " +
                "p.product_id, " +
                "p.product_name, " +
                "COALESCE(SUM(CASE WHEN pi.product_id IS NOT NULL THEN pi.quantity ELSE 0 END), 0) AS qty_purchased, " +
                "COALESCE(SUM(CASE WHEN pi.product_id IS NOT NULL THEN pi.total ELSE 0 END), 0) AS purchase_amount, " +
                "COALESCE(SUM(CASE WHEN si.product_id IS NOT NULL THEN si.quantity ELSE 0 END), 0) AS qty_sold, " +
                "COALESCE(SUM(CASE WHEN si.product_id IS NOT NULL THEN si.subtotal ELSE 0 END), 0) AS sales_amount " +
                "FROM products p " +
                "LEFT JOIN purchase_items pi ON p.product_id = pi.product_id " +
                "LEFT JOIN purchases pur ON pi.purchase_id = pur.purchase_id AND DATE(pur.purchase_date) BETWEEN ? AND ? " +
                "LEFT JOIN sale_items si ON p.product_id = si.product_id " +
                "LEFT JOIN sales s ON si.sale_id = s.sale_id AND DATE(s.sale_date) BETWEEN ? AND ? " +
                "GROUP BY p.product_id, p.product_name " +
                "HAVING qty_purchased > 0 OR qty_sold > 0 " +
                "ORDER BY sales_amount DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            stmt.setDate(3, Date.valueOf(from));
            stmt.setDate(4, Date.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double purchaseAmount = rs.getDouble("purchase_amount");
                    double salesAmount = rs.getDouble("sales_amount");
                    
                    // Profit = Sales Amount for Product - Purchase Amount for Product
                    ProductPerformanceDTO dto = new ProductPerformanceDTO(
                        rs.getString("product_name"),
                        rs.getInt("qty_purchased"),
                        purchaseAmount,  // Only THIS product's purchase amount
                        rs.getInt("qty_sold"),
                        salesAmount      // Only THIS product's sales amount
                    );
                    products.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product performance report: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Get total profit for all products in a date range
     * Profit = Sum of (Each Product's Sales Amount - Each Product's Purchase Amount)
     * NOT (Total Sales - Total Purchases)
     */
    public double getTotalProductProfit(LocalDate from, LocalDate to) {
        String sql = "SELECT " +
                "COALESCE(SUM(CASE WHEN si.subtotal IS NOT NULL THEN si.subtotal ELSE 0 END), 0) - " +
                "COALESCE(SUM(CASE WHEN pi.total IS NOT NULL THEN pi.total ELSE 0 END), 0) AS total_profit " +
                "FROM products p " +
                "LEFT JOIN purchase_items pi ON p.product_id = pi.product_id " +
                "LEFT JOIN purchases pur ON pi.purchase_id = pur.purchase_id AND DATE(pur.purchase_date) BETWEEN ? AND ? " +
                "LEFT JOIN sale_items si ON p.product_id = si.product_id " +
                "LEFT JOIN sales s ON si.sale_id = s.sale_id AND DATE(s.sale_date) BETWEEN ? AND ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            stmt.setDate(3, Date.valueOf(from));
            stmt.setDate(4, Date.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double profit = rs.getDouble("total_profit");
                    return profit > 0 ? profit : 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total product profit: " + e.getMessage());
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Get top performing products by profit
     * Profit per product = Product's Sales Amount - Product's Purchase Amount
     */
    public List<ProductPerformanceDTO> getTopProductsByProfit(LocalDate from, LocalDate to, int limit) {
        List<ProductPerformanceDTO> products = new ArrayList<>();
        
        String sql = "SELECT " +
                "p.product_id, " +
                "p.product_name, " +
                "COALESCE(SUM(CASE WHEN pi.product_id IS NOT NULL THEN pi.quantity ELSE 0 END), 0) AS qty_purchased, " +
                "COALESCE(SUM(CASE WHEN pi.product_id IS NOT NULL THEN pi.total ELSE 0 END), 0) AS purchase_amount, " +
                "COALESCE(SUM(CASE WHEN si.product_id IS NOT NULL THEN si.quantity ELSE 0 END), 0) AS qty_sold, " +
                "COALESCE(SUM(CASE WHEN si.product_id IS NOT NULL THEN si.subtotal ELSE 0 END), 0) AS sales_amount, " +
                "(COALESCE(SUM(CASE WHEN si.product_id IS NOT NULL THEN si.subtotal ELSE 0 END), 0) - " +
                "COALESCE(SUM(CASE WHEN pi.product_id IS NOT NULL THEN pi.total ELSE 0 END), 0)) AS profit " +
                "FROM products p " +
                "LEFT JOIN purchase_items pi ON p.product_id = pi.product_id " +
                "LEFT JOIN purchases pur ON pi.purchase_id = pur.purchase_id AND DATE(pur.purchase_date) BETWEEN ? AND ? " +
                "LEFT JOIN sale_items si ON p.product_id = si.product_id " +
                "LEFT JOIN sales s ON si.sale_id = s.sale_id AND DATE(s.sale_date) BETWEEN ? AND ? " +
                "GROUP BY p.product_id, p.product_name " +
                "ORDER BY profit DESC " +
                "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            stmt.setDate(3, Date.valueOf(from));
            stmt.setDate(4, Date.valueOf(to));
            stmt.setInt(5, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double purchaseAmount = rs.getDouble("purchase_amount");
                    double salesAmount = rs.getDouble("sales_amount");
                    
                    ProductPerformanceDTO dto = new ProductPerformanceDTO(
                        rs.getString("product_name"),
                        rs.getInt("qty_purchased"),
                        purchaseAmount,  // This product's purchase amount only
                        rs.getInt("qty_sold"),
                        salesAmount      // This product's sales amount only
                    );
                    products.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top products by profit: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }

    /**
     * Get top performing products by quantity sold
     */
    public List<ProductPerformanceDTO> getTopProductsByQuantity(LocalDate from, LocalDate to, int limit) {
        List<ProductPerformanceDTO> products = new ArrayList<>();
        
        String sql = "SELECT " +
                "p.product_id, " +
                "p.product_name, " +
                "COALESCE(SUM(CASE WHEN pi.product_id IS NOT NULL THEN pi.quantity ELSE 0 END), 0) AS qty_purchased, " +
                "COALESCE(SUM(CASE WHEN pi.product_id IS NOT NULL THEN pi.total ELSE 0 END), 0) AS purchase_amount, " +
                "COALESCE(SUM(CASE WHEN si.product_id IS NOT NULL THEN si.quantity ELSE 0 END), 0) AS qty_sold, " +
                "COALESCE(SUM(CASE WHEN si.product_id IS NOT NULL THEN si.subtotal ELSE 0 END), 0) AS sales_amount " +
                "FROM products p " +
                "LEFT JOIN purchase_items pi ON p.product_id = pi.product_id " +
                "LEFT JOIN purchases pur ON pi.purchase_id = pur.purchase_id AND DATE(pur.purchase_date) BETWEEN ? AND ? " +
                "LEFT JOIN sale_items si ON p.product_id = si.product_id " +
                "LEFT JOIN sales s ON si.sale_id = s.sale_id AND DATE(s.sale_date) BETWEEN ? AND ? " +
                "GROUP BY p.product_id, p.product_name " +
                "ORDER BY qty_sold DESC " +
                "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));
            stmt.setDate(3, Date.valueOf(from));
            stmt.setDate(4, Date.valueOf(to));
            stmt.setInt(5, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double purchaseAmount = rs.getDouble("purchase_amount");
                    double salesAmount = rs.getDouble("sales_amount");
                    
                    ProductPerformanceDTO dto = new ProductPerformanceDTO(
                        rs.getString("product_name"),
                        rs.getInt("qty_purchased"),
                        purchaseAmount,  // This product's purchase amount only
                        rs.getInt("qty_sold"),
                        salesAmount      // This product's sales amount only
                    );
                    products.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top products by quantity: " + e.getMessage());
            e.printStackTrace();
        }

        return products;
    }
}
