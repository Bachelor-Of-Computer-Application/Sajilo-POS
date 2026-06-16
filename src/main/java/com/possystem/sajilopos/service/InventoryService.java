package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.InventoryHistoryDAO;
import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.model.InventoryHistory;
import com.possystem.sajilopos.model.InventoryHistory.ActionType;
import com.possystem.sajilopos.model.Product;

import java.util.List;

public class InventoryService {

    private final ProductDAO productDAO = new ProductDAO();
    private final InventoryHistoryDAO historyDAO = new InventoryHistoryDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Get all products with inventory information
     */
    public List<Product> getAllProducts() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return productDAO.getAllProducts(companyId);
    }

    /**
     * Search products
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
     * Get low stock products
     */
    public List<Product> getLowStockProducts() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return productDAO.getLowStockProducts(companyId);
    }

    /**
     * Get out of stock products
     */
    public List<Product> getOutOfStockProducts() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return productDAO.getOutOfStockProducts(companyId);
    }

    /**
     * Adjust stock (manual stock adjustment)
     */
    public boolean adjustStock(int productId, int quantityChange, String remarks) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            return false;
        }

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            return false;
        }

        int newStock = product.getStock() + quantityChange;
        if (newStock < 0) {
            throw new IllegalArgumentException("Adjustment would result in negative stock");
        }

        boolean updated = productDAO.updateStock(productId, newStock, companyId);
        
        if (updated) {
            // Record history
            InventoryHistory history = new InventoryHistory(
                companyId, productId, ActionType.ADJUSTMENT, 
                Math.abs(quantityChange), remarks
            );
            historyDAO.addHistory(history);
        }

        return updated;
    }

    /**
     * Record purchase (stock in)
     */
    public boolean recordPurchase(int productId, int quantity, String remarks) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            return false;
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            return false;
        }

        int newStock = product.getStock() + quantity;
        boolean updated = productDAO.updateStock(productId, newStock, companyId);
        
        if (updated) {
            // Record history
            InventoryHistory history = new InventoryHistory(
                companyId, productId, ActionType.PURCHASE, quantity, remarks
            );
            historyDAO.addHistory(history);
        }

        return updated;
    }

    /**
     * Get recent inventory history
     */
    public List<InventoryHistory> getRecentHistory(int limit) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return historyDAO.getRecentHistory(companyId, limit);
    }

    /**
     * Get today's history
     */
    public List<InventoryHistory> getTodayHistory() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return historyDAO.getTodayHistory(companyId);
    }

    /**
     * Get history for a specific product
     */
    public List<InventoryHistory> getProductHistory(int productId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return historyDAO.getProductHistory(productId, companyId);
    }
}