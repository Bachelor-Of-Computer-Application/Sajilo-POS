package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.model.Product;

import java.util.List;

public class InventoryService {

    private final ProductDAO productDAO = new ProductDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    public List<Product> getAllProducts() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return productDAO.getAllProducts(companyId);
    }

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

    public String addProduct(String name, double price, int stock, String description) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            return "No company ID found in session";
        }
        
        if (name == null || name.trim().isEmpty())
            return "Product name cannot be empty.";
        if (price <= 0)
            return "Price must be greater than 0.";
        if (stock < 0)
            return "Stock cannot be negative.";

        Product product = new Product(companyId, name.trim(), price, stock, description);
        boolean success = productDAO.addProduct(product);
        return success ? "Product added successfully." : "Failed to add product.";
    }

    public String updateStock(int productId, int newStock) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            return "No company ID found in session";
        }
        
        if (newStock < 0)
            return "Stock cannot be negative.";

        Product product = productDAO.getProductById(productId);
        if (product == null)
            return "Product not found.";

        boolean success = productDAO.updateStock(productId, newStock, companyId);
        return success ? "Stock updated successfully." : "Failed to update stock.";
    }

    public String deleteProduct(int productId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            return "No company ID found in session";
        }
        
        Product product = productDAO.getProductById(productId);
        if (product == null)
            return "Product not found.";

        boolean success = productDAO.deleteProduct(productId, companyId);
        return success ? "Product deleted successfully." : "Failed to delete product.";
    }

    public List<Product> getLowStockProducts(int threshold) {
        return getAllProducts().stream()
                .filter(p -> p.getStock() <= threshold)
                .toList();
    }
}