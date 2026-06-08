package com.possystem.sajilopos.service;

import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.model.Product;

import java.util.List;

public class InventoryService {

    private final ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> searchProducts(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllProducts();
        }
        return productDAO.searchProductByName(name.trim());
    }

    public String addProduct(String name, double price, int stock) {
        if (name == null || name.trim().isEmpty())
            return "Product name cannot be empty.";
        if (price <= 0)
            return "Price must be greater than 0.";
        if (stock < 0)
            return "Stock cannot be negative.";

        Product product = new Product(0, name.trim(), price, stock);
        boolean success = productDAO.addProduct(product);
        return success ? "Product added successfully." : "Failed to add product.";
    }

    public String updateStock(int productId, int newStock) {
        if (newStock < 0)
            return "Stock cannot be negative.";

        Product product = productDAO.getProductById(productId);
        if (product == null)
            return "Product not found.";

        boolean success = productDAO.updateStock(productId, newStock);
        return success ? "Stock updated successfully." : "Failed to update stock.";
    }

    public String deleteProduct(int productId) {
        Product product = productDAO.getProductById(productId);
        if (product == null)
            return "Product not found.";

        boolean success = productDAO.deleteProduct(productId);
        return success ? "Product deleted successfully." : "Failed to delete product.";
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productDAO.getAllProducts().stream()
                .filter(p -> p.getStock() <= threshold)
                .toList();
    }
}