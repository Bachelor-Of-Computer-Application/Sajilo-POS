package com.possystem.sajilopos.service;

import com.possystem.sajilopos.dao.ProductDAO;
import com.possystem.sajilopos.dao.SaleDAO;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;

import java.util.ArrayList;
import java.util.List;

public class BillingService {

    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final List<SaleItem> currentItems = new ArrayList<>();

    // Add item to current bill
    public String addItem(int productId, int quantity) {
        Product product = productDAO.getProductById(productId);

        if (product == null) return "Product not found.";
        if (product.getStock() < quantity) return "Insufficient stock.";

        currentItems.add(new SaleItem(product, quantity));
        return "Item added.";
    }

    // Remove item from current bill
    public void removeItem(int index) {
        if (index >= 0 && index < currentItems.size()) {
            currentItems.remove(index);
        }
    }

    // Get current bill items
    public List<SaleItem> getCurrentItems() {
        return currentItems;
    }

    // Calculate total before discount
    public double getTotal() {
        return currentItems.stream().mapToDouble(SaleItem::getSubtotal).sum();
    }

    // Process and save the sale
    public boolean processSale(double discount) {
        if (currentItems.isEmpty()) return false;

        Sale sale = new Sale(0, new ArrayList<>(currentItems), discount);
        boolean saved = saleDAO.saveSale(sale);

        if (saved) {
            // Update stock for each item
            for (SaleItem item : currentItems) {
                int newStock = item.getProduct().getStock() - item.getQuantity();
                productDAO.updateStock(item.getProduct().getId(), newStock);
            }
            currentItems.clear(); // Clear bill after successful sale
        }

        return saved;
    }

    // Clear current bill
    public void clearBill() {
        currentItems.clear();
    }
}