package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.SupplierDAO;
import com.possystem.sajilopos.model.Supplier;

import java.util.List;

public class SupplierService {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Get all suppliers for the current company
     */
    public List<Supplier> getAllSuppliers() {
        int companyId = sessionManager.getCurrentCompanyId();
        System.out.println("SupplierService.getAllSuppliers() - Company ID: " + companyId);
        
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        List<Supplier> suppliers = supplierDAO.getAllSuppliers(companyId);
        System.out.println("SupplierService - Retrieved " + suppliers.size() + " suppliers from DAO");
        return suppliers;
    }

    /**
     * Get supplier by ID
     */
    public Supplier getSupplierById(int supplierId) {
        return supplierDAO.getSupplierById(supplierId);
    }

    /**
     * Add new supplier
     */
    public boolean addSupplier(String supplierName, String phone, String address) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        if (supplierName == null || supplierName.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be empty");
        }

        Supplier supplier = new Supplier(companyId, supplierName.trim(), phone, address);
        return supplierDAO.addSupplier(supplier);
    }

    /**
     * Update supplier
     */
    public boolean updateSupplier(int supplierId, String supplierName, String phone, String address) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        if (supplierName == null || supplierName.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be empty");
        }

        Supplier supplier = new Supplier(supplierId, companyId, supplierName.trim(), phone, address, null);
        return supplierDAO.updateSupplier(supplier);
    }

    /**
     * Delete supplier
     */
    public boolean deleteSupplier(int supplierId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        return supplierDAO.deleteSupplier(supplierId, companyId);
    }

    /**
     * Search suppliers by name
     */
    public List<Supplier> searchSuppliers(String name) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        if (name == null || name.trim().isEmpty()) {
            return getAllSuppliers();
        }

        return supplierDAO.searchSuppliers(name.trim(), companyId);
    }
}
