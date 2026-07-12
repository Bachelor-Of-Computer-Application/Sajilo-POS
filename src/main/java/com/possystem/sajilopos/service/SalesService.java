package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.SaleDAO;
import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.dao.CustomerDAO;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.Customer;
import com.possystem.sajilopos.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesService {

    private final SaleDAO saleDAO = new SaleDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();

    /**
     * Create a new sale
     */
    public Sale createNewSale(int customerId) {
        int companyId = sessionManager.getCurrentCompanyId();
        User currentUser = sessionManager.getCurrentUser();
        
        if (companyId == -1 || currentUser == null) {
            throw new IllegalStateException("No company or user found in session");
        }

        return new Sale(companyId, customerId, generateInvoiceNumber(), currentUser.getUserId());
    }

    /**
     * Add item to sale
     */
    public void addItemToSale(Sale sale, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = productDAO.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        if (product.isOutOfStock()) {
            throw new IllegalArgumentException("Product is out of stock");
        }

        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock());
        }

        // Check if product already exists in sale
        for (SaleItem item : sale.getItems()) {
            if (item.getProductId() == productId) {
                throw new IllegalArgumentException("Product already added to this sale");
            }
        }

        SaleItem item = new SaleItem(productId, product.getProductName(), quantity, product.getPrice());
        sale.addItem(item);
    }

    /**
     * Remove item from sale
     */
    public void removeItemFromSale(Sale sale, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= sale.getItems().size()) {
            throw new IllegalArgumentException("Invalid item index");
        }

        sale.removeItemAt(itemIndex);
    }

    /**
     * Update item quantity in sale
     */
    public void updateItemQuantity(Sale sale, int itemIndex, int quantity) {
        if (itemIndex < 0 || itemIndex >= sale.getItems().size()) {
            throw new IllegalArgumentException("Invalid item index");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        SaleItem item = sale.getItems().get(itemIndex);
        Product product = productDAO.getProductById(item.getProductId());

        if (quantity > product.getStock()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock());
        }

        item.setQuantity(quantity);
        sale.recalculateTotals();
    }

    /**
     * Apply discount to sale
     */
    public void applyDiscount(Sale sale, double discountAmount) {
        if (discountAmount < 0) {
            throw new IllegalArgumentException("Discount cannot be negative");
        }

        if (discountAmount > sale.getTotalAmount()) {
            throw new IllegalArgumentException("Discount cannot be greater than total amount");
        }

        sale.setDiscountAmount(discountAmount);
        sale.recalculateTotals();
    }

    /**
     * Apply automatic tiered discount based on loyalty points
     * 50-99 points = 5% off
     * 100-199 points = 10% off
     * 200+ points = 15% off
     */
    public double calculateTieredDiscount(int loyaltyPoints, double subtotal) {
        if (loyaltyPoints < 50) {
            return 0.0; // No discount
        } else if (loyaltyPoints < 100) {
            return subtotal * 0.05; // 5% discount
        } else if (loyaltyPoints < 200) {
            return subtotal * 0.10; // 10% discount
        } else {
            return subtotal * 0.15; // 15% discount
        }
    }

    /**
     * Apply tiered discount to sale based on customer loyalty points
     */
    public void applyTieredDiscountBySale(Sale sale, int customerId) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer != null) {
            double discountAmount = calculateTieredDiscount(customer.getLoyaltyPoints(), sale.getTotalAmount());
            sale.setDiscountAmount(discountAmount);
            sale.recalculateTotals();
        }
    }

    /**
     * Process payment
     */
    public void processPayment(Sale sale, String paymentMethod, double amountPaid) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        if (amountPaid < sale.getFinalAmount()) {
            throw new IllegalArgumentException("Amount paid is less than final amount");
        }

        sale.setPaymentMethod(paymentMethod);
        sale.setAmountPaid(amountPaid);
        sale.calculateChange();
    }

    /**
     * Save sale and reduce stock
     */
    public int saveSale(Sale sale) {
        if (sale.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot save sale without items");
        }

        if (sale.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        if (sale.getInvoiceNo() == null || sale.getInvoiceNo().trim().isEmpty()) {
            sale.setInvoiceNo(generateInvoiceNumber());
        }

        // Reduce stock for each item
        for (SaleItem item : sale.getItems()) {
            reduceProductStock(item.getProductId(), item.getQuantity());
        }

        // Update customer loyalty points (only for registered customers)
        if (sale.getCustomerId() > 0) {
            updateCustomerLoyaltyPoints(sale.getCustomerId(), sale.getFinalAmount());
        }

        // Save sale to database
        return saleDAO.saveSale(sale);
    }

    /**
     * Reduce product stock
     */
    private void reduceProductStock(int productId, int quantity) {
        Product product = productDAO.getProductById(productId);
        if (product != null) {
            int newStock = product.getStock() - quantity;
            product.setStock(newStock);
            productDAO.updateProduct(product);
        }
    }

    /**
     * Update customer loyalty points (1 point per Rs. 20 spent)
     */
    private void updateCustomerLoyaltyPoints(int customerId, double amount) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer != null) {
            // Calculate points: 1 point for every Rs. 20 spent
            int pointsToAdd = (int) (amount / 20);
            customer.setLoyaltyPoints(customer.getLoyaltyPoints() + pointsToAdd);
            customerDAO.updateCustomer(customer);
        }
    }

    /**
     * Get all customers
     */
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        return productDAO.getAllProducts(companyId);
    }

    /**
     * Get sale history
     */
    public List<Sale> getSaleHistory() {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            throw new IllegalStateException("No company ID found in session");
        }

        return saleDAO.getSalesByCompany(companyId);
    }

    /**
     * Get specific sale
     */
    public Sale getSaleById(int saleId) {
        return saleDAO.getSaleById(saleId);
    }

    /**
     * Delete sale
     */
    public boolean deleteSale(int saleId) {
        int companyId = sessionManager.getCurrentCompanyId();
        if (companyId == -1) {
            return false;
        }

        return saleDAO.deleteSale(saleId, companyId);
    }

    /**
     * Generate unique invoice number
     */
    private String generateInvoiceNumber() {
        int companyId = sessionManager.getCurrentCompanyId();
        int todayCount = saleDAO.getTodaySaleCount(companyId);
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return "INV-" + datePart + "-" + String.format("%03d", todayCount + 1);
    }

    /**
     * Calculate grand total for a sale
     */
    public double calculateGrandTotal(Sale sale) {
        sale.recalculateTotals();
        return sale.getFinalAmount();
    }
}
