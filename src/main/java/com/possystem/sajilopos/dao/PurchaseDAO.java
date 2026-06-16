package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Purchase;
import com.possystem.sajilopos.model.PurchaseItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

    /**
     * Save a complete purchase with all items (transaction)
     */
    public int savePurchase(Purchase purchase) {
        String purchaseQuery = "INSERT INTO purchases (company_id, supplier_id, invoice_no, total_amount) " +
                              "VALUES (?, ?, ?, ?)";
        String itemQuery = "INSERT INTO purchase_items (purchase_id, product_id, purchase_price, quantity, total) " +
                          "VALUES (?, ?, ?, ?, ?)";
        String stockQuery = "UPDATE products SET stock = stock + ? WHERE product_id = ? AND company_id = ?";
        String historyQuery = "INSERT INTO inventory_history (company_id, product_id, action_type, quantity, remarks) " +
                             "VALUES (?, ?, 'PURCHASE', ?, ?)";

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert purchase
            try (PreparedStatement statement = connection.prepareStatement(purchaseQuery, 
                    Statement.RETURN_GENERATED_KEYS)) {
                
                statement.setInt(1, purchase.getCompanyId());
                statement.setInt(2, purchase.getSupplierId());
                statement.setString(3, purchase.getInvoiceNo());
                statement.setDouble(4, purchase.getTotalAmount());
                
                statement.executeUpdate();
                
                // Get generated purchase ID
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return -1;
                    }
                    int purchaseId = generatedKeys.getInt(1);

                    // Insert all purchase items
                    for (PurchaseItem item : purchase.getItems()) {
                        // Insert purchase item
                        try (PreparedStatement itemStatement = connection.prepareStatement(itemQuery)) {
                            itemStatement.setInt(1, purchaseId);
                            itemStatement.setInt(2, item.getProductId());
                            itemStatement.setDouble(3, item.getPurchasePrice());
                            itemStatement.setInt(4, item.getQuantity());
                            itemStatement.setDouble(5, item.getTotal());
                            
                            itemStatement.executeUpdate();
                        }

                        // Update product stock
                        try (PreparedStatement stockStatement = connection.prepareStatement(stockQuery)) {
                            stockStatement.setInt(1, item.getQuantity());
                            stockStatement.setInt(2, item.getProductId());
                            stockStatement.setInt(3, purchase.getCompanyId());
                            
                            stockStatement.executeUpdate();
                        }

                        // Record inventory history
                        try (PreparedStatement historyStatement = connection.prepareStatement(historyQuery)) {
                            historyStatement.setInt(1, purchase.getCompanyId());
                            historyStatement.setInt(2, item.getProductId());
                            historyStatement.setInt(3, item.getQuantity());
                            historyStatement.setString(4, "Purchase from supplier");
                            
                            historyStatement.executeUpdate();
                        }
                    }

                    connection.commit(); // Commit transaction
                    return purchaseId;
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    System.err.println("Error during rollback: " + rollbackException.getMessage());
                }
            }
            System.err.println("Error saving purchase: " + e.getMessage());
            return -1;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting autocommit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Get purchase by ID with all items
     */
    public Purchase getPurchaseById(int purchaseId) {
        String query = "SELECT * FROM purchases WHERE purchase_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, purchaseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Purchase purchase = mapResultSetToPurchase(resultSet);
                    purchase.setItems(getPurchaseItems(purchaseId));
                    return purchase;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching purchase: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get all purchases for a company
     */
    public List<Purchase> getPurchasesByCompany(int companyId) {
        List<Purchase> purchases = new ArrayList<>();
        String query = "SELECT * FROM purchases WHERE company_id = ? ORDER BY purchase_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, companyId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Purchase purchase = mapResultSetToPurchase(resultSet);
                    purchase.setItems(getPurchaseItems(purchase.getPurchaseId()));
                    purchases.add(purchase);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching purchases: " + e.getMessage());
        }

        return purchases;
    }

    /**
     * Get purchase items for a specific purchase
     */
    public List<PurchaseItem> getPurchaseItems(int purchaseId) {
        List<PurchaseItem> items = new ArrayList<>();
        String query = "SELECT * FROM purchase_items WHERE purchase_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, purchaseId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PurchaseItem item = mapResultSetToPurchaseItem(resultSet);
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching purchase items: " + e.getMessage());
        }

        return items;
    }

    /**
     * Delete a purchase and its items
     */
    public boolean deletePurchase(int purchaseId, int companyId) {
        String deleteItemsQuery = "DELETE FROM purchase_items WHERE purchase_id = ?";
        String deletePurchaseQuery = "DELETE FROM purchases WHERE purchase_id = ? AND company_id = ?";

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Delete items first
            try (PreparedStatement itemStatement = connection.prepareStatement(deleteItemsQuery)) {
                itemStatement.setInt(1, purchaseId);
                itemStatement.executeUpdate();
            }

            // Delete purchase
            try (PreparedStatement purchaseStatement = connection.prepareStatement(deletePurchaseQuery)) {
                purchaseStatement.setInt(1, purchaseId);
                purchaseStatement.setInt(2, companyId);
                int rowsAffected = purchaseStatement.executeUpdate();
                
                if (rowsAffected > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    System.err.println("Error during rollback: " + rollbackException.getMessage());
                }
            }
            System.err.println("Error deleting purchase: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting autocommit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Map ResultSet to Purchase object
     */
    private Purchase mapResultSetToPurchase(ResultSet resultSet) throws SQLException {
        return new Purchase(
            resultSet.getInt("purchase_id"),
            resultSet.getInt("company_id"),
            resultSet.getInt("supplier_id"),
            resultSet.getString("invoice_no"),
            resultSet.getDouble("total_amount"),
            resultSet.getTimestamp("purchase_date")
        );
    }

    /**
     * Map ResultSet to PurchaseItem object
     */
    private PurchaseItem mapResultSetToPurchaseItem(ResultSet resultSet) throws SQLException {
        return new PurchaseItem(
            resultSet.getInt("purchase_item_id"),
            resultSet.getInt("purchase_id"),
            resultSet.getInt("product_id"),
            "", // Product name will be fetched separately
            resultSet.getDouble("purchase_price"),
            resultSet.getInt("quantity"),
            resultSet.getDouble("total")
        );
    }
}
