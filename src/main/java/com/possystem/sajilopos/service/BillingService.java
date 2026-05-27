package com.possystem.sajilopos.service;

import com.possystem.sajilopos.config.SessionManager;
import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.dao.SaleDAO;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;
import com.possystem.sajilopos.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Billing Service
 * Handles bill generation, item management, and sale processing
 */
public class BillingService {

    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final List<SaleItem> currentItems = new ArrayList<>();

    /**
     * Add item to current bill
     * 
     * @param productId Product ID to add
     * @param quantity  Quantity of the product
     * @return Message indicating success or error
     */
    public String addItem(int productId, int quantity) {
        if (quantity <= 0) {
            return "Quantity must be greater than 0.";
        }

        Product product = productDAO.getProductById(productId);

        if (product == null) {
            return "Product not found.";
        }

        if (product.getStock() < quantity) {
            return "Insufficient stock. Available: " + product.getStock();
        }

        // Check if product already exists in bill, if yes update quantity
        for (SaleItem item : currentItems) {
            if (item.getProduct().getId() == productId) {
                int newQuantity = item.getQuantity() + quantity;
                if (product.getStock() < newQuantity) {
                    return "Insufficient stock for additional quantity. Available: " + product.getStock();
                }
                // Update quantity (we need to recreate the item to update subtotal)
                currentItems.remove(item);
                currentItems.add(new SaleItem(product, newQuantity));
                return "Item quantity updated.";
            }
        }

        currentItems.add(new SaleItem(product, quantity));
        return "Item added to bill.";
    }

    /**
     * Remove item from current bill
     * 
     * @param index Index of the item to remove
     */
    public void removeItem(int index) {
        if (index >= 0 && index < currentItems.size()) {
            SaleItem removed = currentItems.remove(index);
            System.out.println("Item removed: " + removed.getProduct().getName());
        }
    }

    /**
     * Get current bill items
     */
    public List<SaleItem> getCurrentItems() {
        return new ArrayList<>(currentItems);
    }

    /**
     * Calculate total before discount
     */
    public double getTotal() {
        return currentItems.stream().mapToDouble(SaleItem::getSubtotal).sum();
    }

    /**
     * Get number of items in the current bill
     */
    public int getItemCount() {
        return currentItems.size();
    }

    /**
     * Calculate total after discount
     */
    public double getFinalAmount(double discount) {
        double total = getTotal();
        return Math.max(0, total - discount); // Discount cannot make total negative
    }

    /**
     * Process and save the sale
     * 
     * @param discount Discount amount to apply
     * @return true if sale was successfully saved, false otherwise
     */
    public boolean processSale(double discount) {
        if (currentItems.isEmpty()) {
            System.err.println("Cannot process empty bill.");
            return false;
        }

        if (!sessionManager.isLoggedIn()) {
            System.err.println("No user logged in. Cannot process sale.");
            return false;
        }

        try {
            User currentUser = sessionManager.getCurrentUser();
            Sale sale = new Sale(0, new ArrayList<>(currentItems), discount, currentUser.getUserId());

            boolean saved = saleDAO.saveSale(sale);

            if (saved) {
                // Update stock for each item
                for (SaleItem item : currentItems) {
                    int newStock = item.getProduct().getStock() - item.getQuantity();
                    boolean updated = productDAO.updateStock(item.getProduct().getId(), newStock);
                    if (!updated) {
                        System.err
                                .println("Warning: Failed to update stock for product: " + item.getProduct().getName());
                    }
                }

                System.out.println("Sale processed successfully by " + currentUser.getFullName());
                currentItems.clear(); // Clear bill after successful sale
                return true;
            } else {
                System.err.println("Failed to save sale to database.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error processing sale: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clear current bill without saving
     */
    public void clearBill() {
        if (!currentItems.isEmpty()) {
            System.out.println("Bill cleared. " + currentItems.size() + " items discarded.");
        }
        currentItems.clear();
    }

    /**
     * Get bill summary as string
     */
    public String getBillSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("========== BILL SUMMARY ==========\n");

        for (SaleItem item : currentItems) {
            sb.append(String.format("%s x%d @ %.2f = %.2f\n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice(),
                    item.getSubtotal()));
        }

        sb.append("---------------------------------\n");
        sb.append(String.format("Total: %.2f\n", getTotal()));

        return sb.toString();
    }
}