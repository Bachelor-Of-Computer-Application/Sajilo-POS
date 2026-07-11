package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.DashboardActivity;
import com.possystem.sajilopos.model.DashboardLowStockItem;
import com.possystem.sajilopos.model.DashboardTopProduct;
import com.possystem.sajilopos.model.DashboardTransaction;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    public double getTodayRevenue(int companyId) {
        String sql = "SELECT COALESCE(SUM(final_amount), 0) AS total_revenue " +
                "FROM sales WHERE company_id = ? AND DATE(sale_date) = CURDATE()";
        return getSingleDouble(sql, companyId);
    }

    public double getYesterdayRevenue(int companyId) {
        String sql = "SELECT COALESCE(SUM(final_amount), 0) AS total_revenue " +
                "FROM sales WHERE company_id = ? AND DATE(sale_date) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
        return getSingleDouble(sql, companyId);
    }

    public double getTodayProfit(int companyId) {
        String sql = "SELECT COALESCE(SUM(si.subtotal) - SUM(si.quantity * p.cost_price), 0) AS profit " +
                "FROM sale_items si " +
                "JOIN sales s ON si.sale_id = s.sale_id " +
                "JOIN products p ON si.product_id = p.product_id " +
                "WHERE s.company_id = ? AND DATE(s.sale_date) = CURDATE()";
        return getSingleDouble(sql, companyId);
    }

    public int getTransactionsToday(int companyId) {
        String sql = "SELECT COUNT(*) FROM sales WHERE company_id = ? AND DATE(sale_date) = CURDATE()";
        return getSingleInt(sql, companyId);
    }

    public int getProductsSoldToday(int companyId) {
        String sql = "SELECT COALESCE(SUM(si.quantity), 0) " +
                "FROM sale_items si JOIN sales s ON si.sale_id = s.sale_id " +
                "WHERE s.company_id = ? AND DATE(s.sale_date) = CURDATE()";
        return getSingleInt(sql, companyId);
    }

    public int getActiveCustomersToday(int companyId) {
        String sql = "SELECT COUNT(DISTINCT customer_id) FROM sales " +
                "WHERE company_id = ? AND customer_id IS NOT NULL AND DATE(sale_date) = CURDATE()";
        return getSingleInt(sql, companyId);
    }

    public int getLowStockCount(int companyId) {
        String sql = "SELECT COUNT(*) FROM products " +
                "WHERE company_id = ? AND active = 1 AND stock <= minimum_stock";
        return getSingleInt(sql, companyId);
    }

    public double getInventoryValue(int companyId) {
        String sql = "SELECT COALESCE(SUM(stock * price), 0) FROM products " +
                "WHERE company_id = ? AND active = 1";
        return getSingleDouble(sql, companyId);
    }

    public List<DashboardLowStockItem> getLowStockProducts(int companyId, int limit) {
        List<DashboardLowStockItem> items = new ArrayList<>();
        String sql = "SELECT product_name, stock, minimum_stock FROM products " +
                "WHERE company_id = ? AND active = 1 AND stock <= minimum_stock " +
                "ORDER BY stock ASC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new DashboardLowStockItem(
                            rs.getString("product_name"),
                            rs.getInt("stock"),
                            rs.getInt("minimum_stock")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching low stock products: " + e.getMessage());
        }
        return items;
    }

    public List<DashboardTopProduct> getTopSellingProducts(int companyId, LocalDate from, LocalDate to, int limit) {
        List<DashboardTopProduct> items = new ArrayList<>();
        String sql = "SELECT p.product_name, SUM(si.quantity) AS qty_sold, " +
                "COALESCE(SUM(si.subtotal), 0) AS revenue " +
                "FROM sale_items si " +
                "JOIN sales s ON si.sale_id = s.sale_id " +
                "JOIN products p ON si.product_id = p.product_id " +
                "WHERE s.company_id = ? AND DATE(s.sale_date) BETWEEN ? AND ? " +
                "GROUP BY p.product_id, p.product_name " +
                "ORDER BY qty_sold DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            stmt.setInt(4, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new DashboardTopProduct(
                            rs.getString("product_name"),
                            rs.getInt("qty_sold"),
                            rs.getDouble("revenue")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top selling products: " + e.getMessage());
        }
        return items;
    }

    public Map<LocalDate, Double> getSalesAnalytics(int companyId, LocalDate from, LocalDate to) {
        Map<LocalDate, Double> result = new LinkedHashMap<>();
        String sql = "SELECT DATE(sale_date) AS sale_day, COALESCE(SUM(final_amount), 0) AS daily_total " +
                "FROM sales WHERE company_id = ? AND DATE(sale_date) BETWEEN ? AND ? " +
                "GROUP BY DATE(sale_date) ORDER BY sale_day";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getDate("sale_day").toLocalDate(), rs.getDouble("daily_total"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sales analytics: " + e.getMessage());
        }
        return result;
    }

    public Map<String, Double> getSalesDistribution(int companyId, LocalDate from, LocalDate to) {
        Map<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT COALESCE(payment_method, 'Other') AS method, " +
                "COALESCE(SUM(final_amount), 0) AS total_amount " +
                "FROM sales WHERE company_id = ? AND DATE(sale_date) BETWEEN ? AND ? " +
                "GROUP BY COALESCE(payment_method, 'Other') ORDER BY total_amount DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("method"), rs.getDouble("total_amount"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sales distribution: " + e.getMessage());
        }
        return result;
    }

    public List<DashboardTransaction> getRecentTransactions(int companyId, int limit) {
        List<DashboardTransaction> result = new ArrayList<>();
        String sql = "SELECT s.invoice_no, COALESCE(c.customer_name, 'Walk-in') AS customer_name, " +
                "s.final_amount, s.payment_method, s.sale_date " +
                "FROM sales s " +
                "LEFT JOIN customers c ON s.customer_id = c.customer_id " +
                "WHERE s.company_id = ? ORDER BY s.sale_date DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new DashboardTransaction(
                            rs.getString("invoice_no"),
                            rs.getString("customer_name"),
                            rs.getDouble("final_amount"),
                            rs.getString("payment_method"),
                            rs.getTimestamp("sale_date"),
                            "Completed"
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recent transactions: " + e.getMessage());
        }
        return result;
    }

    public List<DashboardActivity> getRecentActivities(int companyId, int limit) {
        List<DashboardActivity> result = new ArrayList<>();
        String sql = "SELECT icon, description, activity_time FROM (" +
                "SELECT '🧾' AS icon, CONCAT('Sale completed: ', invoice_no) AS description, sale_date AS activity_time " +
                "FROM sales WHERE company_id = ? " +
                "UNION ALL " +
                "SELECT '📦' AS icon, CONCAT('Purchase added: ', invoice_no) AS description, purchase_date AS activity_time " +
                "FROM purchases WHERE company_id = ? " +
                "UNION ALL " +
                "SELECT '🔧' AS icon, CONCAT('Stock updated: ', product_name) AS description, created_at AS activity_time " +
                "FROM inventory_history ih JOIN products p ON ih.product_id = p.product_id " +
                "WHERE ih.company_id = ? " +
                "UNION ALL " +
                "SELECT '🆕' AS icon, CONCAT('Product added: ', product_name) AS description, created_at AS activity_time " +
                "FROM products WHERE company_id = ? " +
                "UNION ALL " +
                "SELECT '👤' AS icon, CONCAT('User logged in: ', username) AS description, created_at AS activity_time " +
                "FROM users WHERE company_id = ? " +
                ") activities " +
                "ORDER BY activity_time DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            stmt.setInt(2, companyId);
            stmt.setInt(3, companyId);
            stmt.setInt(4, companyId);
            stmt.setInt(5, companyId);
            stmt.setInt(6, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new DashboardActivity(
                            rs.getString("icon"),
                            rs.getString("description"),
                            rs.getTimestamp("activity_time")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recent activities: " + e.getMessage());
        }
        return result;
    }

    public int getTotalProducts(int companyId) {
        String sql = "SELECT COUNT(*) FROM products WHERE company_id = ? AND active = 1";
        return getSingleInt(sql, companyId);
    }

    public int getTotalCustomers(int companyId) {
        String sql = "SELECT COUNT(*) FROM customers";
        return getSingleInt(sql);
    }

    public int getTotalSuppliers(int companyId) {
        String sql = "SELECT COUNT(*) FROM suppliers WHERE company_id = ?";
        return getSingleInt(sql, companyId);
    }

    private double getSingleDouble(String sql, int companyId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Dashboard query error: " + e.getMessage());
        }
        return 0.0;
    }

    private int getSingleInt(String sql, int companyId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Dashboard query error: " + e.getMessage());
        }
        return 0;
    }

    private int getSingleInt(String sql) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Dashboard query error: " + e.getMessage());
        }
        return 0;
    }
}
