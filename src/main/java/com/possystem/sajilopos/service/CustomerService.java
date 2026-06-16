package com.possystem.sajilopos.service;

import com.possystem.sajilopos.dao.CustomerDAO;
import com.possystem.sajilopos.model.Customer;

import java.util.List;

/**
 * Customer Service Layer
 * Handles business logic for customer operations
 */
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    /**
     * Get a customer by ID
     */
    public Customer getCustomerById(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }

    /**
     * Get customer by phone number
     */
    public Customer getCustomerByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        return customerDAO.getCustomerByPhone(phone.trim());
    }

    /**
     * Add a new customer
     */
    public boolean addCustomer(String customerName, String phone, String address) {
        // Validation
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Check if phone already exists
        Customer existing = customerDAO.getCustomerByPhone(phone.trim());
        if (existing != null) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Always start with 0 loyalty points
        Customer customer = new Customer(customerName.trim(), phone.trim(), address, 0);
        return customerDAO.addCustomer(customer);
    }

    /**
     * Update an existing customer (loyalty points cannot be manually changed)
     */
    public boolean updateCustomer(int customerId, String customerName, String phone, String address) {
        // Validation
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Check if phone already exists for another customer
        Customer existing = customerDAO.getCustomerByPhone(phone.trim());
        if (existing != null && existing.getCustomerId() != customerId) {
            throw new IllegalArgumentException("Phone number already exists for another customer");
        }

        // Get current loyalty points to preserve them
        Customer currentCustomer = customerDAO.getCustomerById(customerId);
        if (currentCustomer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        Customer customer = new Customer(customerId, customerName.trim(), phone.trim(), 
                                        address, currentCustomer.getLoyaltyPoints(), null, null);
        return customerDAO.updateCustomer(customer);
    }

    /**
     * Delete a customer
     */
    public boolean deleteCustomer(int customerId) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        return customerDAO.deleteCustomer(customerId);
    }

    /**
     * Search customers by name or phone
     */
    public List<Customer> searchCustomers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDAO.searchCustomers(searchText.trim());
    }

    /**
     * Update loyalty points
     */
    public boolean updateLoyaltyPoints(int customerId, int points) {
        if (points < 0) {
            throw new IllegalArgumentException("Loyalty points cannot be negative");
        }
        return customerDAO.updateLoyaltyPoints(customerId, points);
    }

    /**
     * Add loyalty points to existing balance
     */
    public boolean addLoyaltyPoints(int customerId, int pointsToAdd) {
        if (pointsToAdd < 0) {
            throw new IllegalArgumentException("Points to add cannot be negative");
        }
        return customerDAO.addLoyaltyPoints(customerId, pointsToAdd);
    }
}
