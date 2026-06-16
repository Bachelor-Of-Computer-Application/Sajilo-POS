package com.possystem.sajilopos.dao;

import com.possystem.sajilopos.config.DBConnection;
import com.possystem.sajilopos.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, customer_name, phone, address, loyalty_points, " +
                     "created_at, updated_at FROM customers ORDER BY customer_name";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("loyalty_points"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Get a customer by ID
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT customer_id, customer_name, phone, address, loyalty_points, " +
                     "created_at, updated_at FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("loyalty_points"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customer by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get customer by phone number
     */
    public Customer getCustomerByPhone(String phone) {
        String sql = "SELECT customer_id, customer_name, phone, address, loyalty_points, " +
                     "created_at, updated_at FROM customers WHERE phone = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("loyalty_points"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customer by phone: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add a new customer
     */
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_name, phone, address, loyalty_points) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getAddress());
            stmt.setInt(4, customer.getLoyaltyPoints());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update an existing customer
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET customer_name = ?, phone = ?, address = ?, loyalty_points = ? " +
                     "WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getPhone());
            stmt.setString(3, customer.getAddress());
            stmt.setInt(4, customer.getLoyaltyPoints());
            stmt.setInt(5, customer.getCustomerId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a customer
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Search customers by name or phone
     */
    public List<Customer> searchCustomers(String searchText) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, customer_name, phone, address, loyalty_points, " +
                     "created_at, updated_at FROM customers " +
                     "WHERE customer_name LIKE ? OR phone LIKE ? " +
                     "ORDER BY customer_name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + searchText + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getInt("loyalty_points"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * Update loyalty points for a customer
     */
    public boolean updateLoyaltyPoints(int customerId, int points) {
        String sql = "UPDATE customers SET loyalty_points = ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, points);
            stmt.setInt(2, customerId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating loyalty points: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Add loyalty points to a customer
     */
    public boolean addLoyaltyPoints(int customerId, int pointsToAdd) {
        String sql = "UPDATE customers SET loyalty_points = loyalty_points + ? WHERE customer_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pointsToAdd);
            stmt.setInt(2, customerId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding loyalty points: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
