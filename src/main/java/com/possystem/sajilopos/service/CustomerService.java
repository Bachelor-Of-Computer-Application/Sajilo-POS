package com.possystem.sajilopos.service;

import com.possystem.sajilopos.dao.CustomerDAO;
import com.possystem.sajilopos.model.Customer;

import java.util.List;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    public List<Customer> searchCustomers(String name) {
        if (name == null || name.trim().isEmpty()) return getAllCustomers();
        return customerDAO.searchCustomerByName(name.trim());
    }

    public String addCustomer(String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) return "Customer name cannot be empty.";
        boolean success = customerDAO.addCustomer(new Customer(0, name.trim(), phone, email));
        return success ? "Customer added successfully." : "Failed to add customer.";
    }

    public String updateCustomer(int id, String name, String phone, String email) {
        if (name == null || name.trim().isEmpty()) return "Customer name cannot be empty.";
        boolean success = customerDAO.updateCustomer(new Customer(id, name.trim(), phone, email));
        return success ? "Customer updated successfully." : "Failed to update customer.";
    }

    public String deleteCustomer(int id) {
        boolean success = customerDAO.deleteCustomer(id);
        return success ? "Customer deleted successfully." : "Failed to delete customer.";
    }
}
