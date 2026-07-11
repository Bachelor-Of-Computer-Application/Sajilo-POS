package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.model.Product;

import java.util.List;

/**
 * Product Service Layer
 * Handles business logic for product operations
 */
public class ProductService {

    private final ProductDAO productDAO;
    private final SessionManager sessionManager;

    public ProductService() {
        this.productDAO = new ProductDAO();
        this.sessionManager = SessionManager.getInstance();
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
     * Get a product by ID
     */
    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }

    /**
     * Add a new product
     */
    public boolean addProduct(String productName, double price, int stock, String description) {
        return addProduct(productName, price, stock, description, 0);
    }

    /**
     * Add a new product with category
     */
    public boolean addProduct(String productName, double price, int stock, String description, int categoryId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) throw new IllegalStateException("No company ID found in session");

        if (productName == null || productName.trim().isEmpty())
            throw new IllegalArgumentException("Product name cannot be empty");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");

        Product product = new Product(companyId, productName.trim(), price, stock, description, 10, categoryId);
        return productDAO.addProduct(product);
    }

    /**
     * Update an existing product
     */
    public boolean updateProduct(int productId, String productName, double price, int stock, String description) {
        return updateProduct(productId, productName, price, stock, description, 0);
    }

    /**
     * Update an existing product with category
     */
    public boolean updateProduct(int productId, String productName, double price, int stock, String description, int categoryId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) throw new IllegalStateException("No company ID found in session");

        if (productName == null || productName.trim().isEmpty())
            throw new IllegalArgumentException("Product name cannot be empty");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");

        Product product = new Product(productId, companyId, productName.trim(), price, stock,
                                      description, true, null, null, 10, categoryId);
        return productDAO.updateProduct(product);
    }

    /**
     * Delete a product (soft delete)
     */
    public boolean deleteProduct(int productId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        return productDAO.deleteProduct(productId, companyId);
    }

    /**
     * Search products by name
     */
    public List<Product> searchProducts(String searchText) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllProducts();
        }
        
        return productDAO.searchProductByName(searchText.trim(), companyId);
    }

    /**
     * Update product stock
     */
    public boolean updateStock(int productId, int newStock) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        
        return productDAO.updateStock(productId, newStock, companyId);
    }
}
