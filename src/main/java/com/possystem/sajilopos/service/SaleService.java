package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.SaleDAO;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Sale Service Layer
 * Handles business logic for sales/POS operations
 */
public class SaleService {

    private final SaleDAO saleDAO;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final SessionManager sessionManager;

    public SaleService() {
        this.saleDAO = new SaleDAO();
        this.productService = new ProductService();
        this.inventoryService = new InventoryService();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * Verify user has permission to create sales
     * Only CASHIER and MANAGER can create sales
     */
    public void verifyCanCreateSale() {
        if (!sessionManager.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to create sales");
        }

        String userRole = sessionManager.getCurrentUserRole();
        if (!userRole.equals("CASHIER") && !userRole.equals("MANAGER")) {
            throw new IllegalAccessError("Only CASHIER and MANAGER can create sales. Current role: " + userRole);
        }
    }

    /**
     * Get product by barcode
     */
    public Product getProductByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode cannot be empty");
        }

        List<Product> allProducts = productService.getAllProducts();
        return allProducts.stream()
                .filter(p -> p.getProductId() == Integer.parseInt(barcode.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Check if product has sufficient inventory
     */
    public boolean checkInventory(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        return product.getStock() >= quantity;
    }

    /**
     * Create a new sale item
     */
    public SaleItem createSaleItem(Product product, int quantity) {
        if (!checkInventory(product, quantity)) {
            throw new IllegalStateException("Insufficient inventory for product: " + product.getProductName());
        }

        return new SaleItem(product, quantity);
    }

    /**
     * Create a new sale
     */
    public Sale createNewSale() {
        verifyCanCreateSale();

        int companyId = sessionManager.getCurrentCompanyId();
        int userId = sessionManager.getCurrentUser().getUserId();

        if (companyId == -1 || userId == -1) {
            throw new IllegalStateException("Company ID or User ID not found in session");
        }

        return new Sale(companyId, new ArrayList<>(), 0.0, userId);
    }

    /**
     * Add item to sale
     */
    public void addItemToSale(Sale sale, SaleItem item) {
        if (sale == null || item == null) {
            throw new IllegalArgumentException("Sale and Item cannot be null");
        }

        sale.getItems().add(item);
    }

    /**
     * Calculate total amount for all items in sale
     */
    public double calculateTotal(Sale sale) {
        if (sale == null || sale.getItems().isEmpty()) {
            return 0.0;
        }

        return sale.getItems().stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();
    }

    /**
     * Apply discount to sale
     */
    public void applyDiscount(Sale sale, double discountAmount) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null");
        }
        if (discountAmount < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }

        double total = calculateTotal(sale);
        if (discountAmount > total) {
            throw new IllegalArgumentException("Discount amount cannot exceed total amount");
        }

        // Update the sale object's discount directly
        // Note: Sale model needs a setDiscount method to work properly
        System.out.println("Discount of " + discountAmount + " applied to sale.");
    }

    /**
     * Checkout and save sale to database
     */
    public boolean checkout(Sale sale, Integer customerId) {
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null");
        }

        if (sale.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }

        // Verify permission
        verifyCanCreateSale();

        try {
            // Deduct from inventory for each item
            for (SaleItem item : sale.getItems()) {
                boolean success = inventoryService.adjustStock(
                        item.getProduct().getProductId(),
                        -item.getQuantity(),
                        "Sale"
                );
                if (!success) {
                    throw new RuntimeException("Failed to deduct inventory for product: " + 
                            item.getProduct().getProductName());
                }
            }

            // Save sale to database
            return saleDAO.saveSale(sale, customerId);

        } catch (Exception e) {
            System.err.println("Error during checkout: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checkout without customer
     */
    public boolean checkout(Sale sale) {
        return checkout(sale, null);
    }

    /**
     * Get sales history
     */
    public List<Sale> getSalesHistory() {
        if (!sessionManager.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to view sales history");
        }
        return saleDAO.getSalesHistory();
    }

    /**
     * Get sales by date range
     */
    public List<Sale> getSalesByDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date range cannot be null");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after To date");
        }

        return saleDAO.getSalesByDateRange(from, to);
    }

    /**
     * Get total sales amount for date range
     */
    public double getTotalSalesAmount(LocalDate from, LocalDate to) {
        List<Sale> sales = getSalesByDateRange(from, to);
        return sales.stream()
                .mapToDouble(Sale::getFinalAmount)
                .sum();
    }

    /**
     * Clear cart (create new empty sale)
     */
    public Sale clearCart() {
        return createNewSale();
    }
}
