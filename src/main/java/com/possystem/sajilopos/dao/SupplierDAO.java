package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    /**
     * Get all suppliers for a company
     */
    public List<Supplier> getAllSuppliers(int companyId) {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM suppliers WHERE company_id = ? ORDER BY supplier_name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, companyId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Supplier supplier = mapResultSetToSupplier(resultSet);
                    suppliers.add(supplier);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching suppliers: " + e.getMessage());
        }

        return suppliers;
    }

    /**
     * Get supplier by ID
     */
    public Supplier getSupplierById(int supplierId) {
        String query = "SELECT * FROM suppliers WHERE supplier_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, supplierId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToSupplier(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching supplier: " + e.getMessage());
        }

        return null;
    }

    /**
     * Add new supplier
     */
    public boolean addSupplier(Supplier supplier) {
        String query = "INSERT INTO suppliers (company_id, supplier_name, phone, address) " +
                      "VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, supplier.getCompanyId());
            statement.setString(2, supplier.getSupplierName());
            statement.setString(3, supplier.getPhone());
            statement.setString(4, supplier.getAddress());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update supplier
     */
    public boolean updateSupplier(Supplier supplier) {
        String query = "UPDATE suppliers SET supplier_name = ?, phone = ?, address = ? " +
                      "WHERE supplier_id = ? AND company_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, supplier.getSupplierName());
            statement.setString(2, supplier.getPhone());
            statement.setString(3, supplier.getAddress());
            statement.setInt(4, supplier.getSupplierId());
            statement.setInt(5, supplier.getCompanyId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete supplier
     */
    public boolean deleteSupplier(int supplierId, int companyId) {
        String query = "DELETE FROM suppliers WHERE supplier_id = ? AND company_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, supplierId);
            statement.setInt(2, companyId);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }

    /**
     * Search suppliers by name
     */
    public List<Supplier> searchSuppliers(String name, int companyId) {
        List<Supplier> suppliers = new ArrayList<>();
        String query = "SELECT * FROM suppliers WHERE company_id = ? AND supplier_name LIKE ? ORDER BY supplier_name";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, companyId);
            statement.setString(2, "%" + name + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Supplier supplier = mapResultSetToSupplier(resultSet);
                    suppliers.add(supplier);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching suppliers: " + e.getMessage());
        }

        return suppliers;
    }

    /**
     * Map ResultSet to Supplier object
     */
    private Supplier mapResultSetToSupplier(ResultSet resultSet) throws SQLException {
        return new Supplier(
            resultSet.getInt("supplier_id"),
            resultSet.getInt("company_id"),
            resultSet.getString("supplier_name"),
            resultSet.getString("phone"),
            resultSet.getString("address"),
            resultSet.getTimestamp("created_at")
        );
    }
}
