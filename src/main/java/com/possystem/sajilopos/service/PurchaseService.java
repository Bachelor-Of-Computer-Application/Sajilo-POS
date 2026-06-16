package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.PurchaseDAO;
import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.dao.SupplierDAO;
import com.possystem.sajilopos.model.Purchase;
import com.possystem.sajilopos.model.PurchaseItem;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.Supplier;

import java.util.List;

public class PurchaseService {

    private final PurchaseDAO purchaseDAO = new PurchaseDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Create a new purchase
     */
    public Purchase createNewPurchase(int supplierId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        Supplier supplier = supplierDAO.getSupplierById(supplierId);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier not found");
        }

        return new Purchase(companyId, supplierId, generateInvoiceNumber());
    }

    /**
     * Add item to purchase
     */
    public void addItemToPurchase(Purchase purchase, int productId, double purchasePrice, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if (purchasePrice < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative");
        }

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        // Check if product already exists in purchase
        for (PurchaseItem item : purchase.getItems()) {
            if (item.getProductId() == productId) {
                throw new IllegalArgumentException("Product already added to this purchase");
            }
        }

        PurchaseItem item = new PurchaseItem(productId, product.getProductName(), purchasePrice, quantity);
        purchase.addItem(item);
    }

    /**
     * Update item in purchase
     */
    public void updateItemInPurchase(Purchase purchase, int itemIndex, double purchasePrice, int quantity) {
        if (itemIndex < 0 || itemIndex >= purchase.getItems().size()) {
            throw new IllegalArgumentException("Invalid item index");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if (purchasePrice < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative");
        }

        PurchaseItem item = purchase.getItems().get(itemIndex);
        item.setPurchasePrice(purchasePrice);
        item.setQuantity(quantity);
        purchase.recalculateTotal();
    }

    /**
     * Remove item from purchase
     */
    public void removeItemFromPurchase(Purchase purchase, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= purchase.getItems().size()) {
            throw new IllegalArgumentException("Invalid item index");
        }

        purchase.removeItemAt(itemIndex);
    }

    /**
     * Save purchase to database
     */
    public int savePurchase(Purchase purchase) {
        if (purchase.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot save purchase without items");
        }

        if (purchase.getInvoiceNo() == null || purchase.getInvoiceNo().trim().isEmpty()) {
            purchase.setInvoiceNo(generateInvoiceNumber());
        }

        return purchaseDAO.savePurchase(purchase);
    }

    /**
     * Get all suppliers for the current company
     */
    public List<Supplier> getAllSuppliers() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        return supplierDAO.getAllSuppliers(companyId);
    }

    /**
     * Get all products for the current company
     */
    public List<Product> getAllProducts() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        return productDAO.getAllProducts(companyId);
    }

    /**
     * Search products by name
     */
    public List<Product> searchProducts(String name) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }

        return productDAO.searchProductByName(name.trim(), companyId);
    }

    /**
     * Get purchase history
     */
    public List<Purchase> getPurchaseHistory() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        return purchaseDAO.getPurchasesByCompany(companyId);
    }

    /**
     * Get specific purchase
     */
    public Purchase getPurchaseById(int purchaseId) {
        return purchaseDAO.getPurchaseById(purchaseId);
    }

    /**
     * Delete purchase
     */
    public boolean deletePurchase(int purchaseId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            return false;
        }

        return purchaseDAO.deletePurchase(purchaseId, companyId);
    }

    /**
     * Generate unique invoice number
     */
    private String generateInvoiceNumber() {
        // Format: INV-YYYYMMDD-HHMMSS
        return "INV-" + System.currentTimeMillis();
    }

    /**
     * Calculate grand total for a purchase
     */
    public double calculateGrandTotal(Purchase purchase) {
        purchase.recalculateTotal();
        return purchase.getTotalAmount();
    }
}
