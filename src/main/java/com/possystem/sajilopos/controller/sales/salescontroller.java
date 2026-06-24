package com.possystem.sajilopos.controller.sales;

import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.model.Customer;
import com.possystem.sajilopos.service.SalesService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class SalesController {

    @FXML
    private ComboBox<Customer> customerCombo;

    @FXML
    private Label loyaltyPointsLabel;

    @FXML
    private TextField productSearchField;

    @FXML
    private ComboBox<Product> productCombo;

    @FXML
    private TextField quantityField;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private TextField discountField;

    @FXML
    private Label finalTotalLabel;

    @FXML
    private ComboBox<String> paymentMethodCombo;

    @FXML
    private TextField amountPaidField;

    @FXML
    private Label changeLabel;

    @FXML
    private TableView<SaleItem> itemsTable;

    @FXML
    private TableColumn<SaleItem, String> colProduct;

    @FXML
    private TableColumn<SaleItem, Double> colPrice;

    @FXML
    private TableColumn<SaleItem, Integer> colQty;

    @FXML
    private TableColumn<SaleItem, Double> colSubtotal;

    @FXML
    private TableColumn<SaleItem, Void> colAction;

    private final SalesService salesService = new SalesService();
    private Sale currentSale;
    private ObservableList<SaleItem> itemsObservable;
    private List<Product> allProducts;
    private List<Customer> allCustomers;

    @FXML
    public void initialize() {
        setupTableColumns();
        
        // Setup payment methods FIRST
        ObservableList<String> paymentMethods = FXCollections.observableArrayList("Cash", "Card", "Cheque", "Digital");
        paymentMethodCombo.setItems(paymentMethods);
        paymentMethodCombo.getSelectionModel().selectFirst(); // Set default to Cash
        
        loadCustomers();
        loadProducts();
        setupProductSearch();
        setupPaymentCalculations();
        itemsObservable = FXCollections.observableArrayList();
        itemsTable.setItems(itemsObservable);

        // Add Walk-in Customer option
        addWalkInCustomerOption();
    }

    /**
     * Setup table columns
     */
    private void setupTableColumns() {
        colProduct.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getProductName()));

        colPrice.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getUnitPrice()).asObject());

        colQty.setCellValueFactory(data ->
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());

        colSubtotal.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getSubtotal()).asObject());

        // Add delete button to action column
        colAction.setCellFactory(param -> new TableCell<SaleItem, Void>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-padding: 5px 10px;");
                deleteBtn.setOnAction(event -> {
                    int selectedIndex = getTableRow().getIndex();
                    handleRemoveItemFromTable(selectedIndex);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }

    /**
     * Load customers into combo box
     */
    private void loadCustomers() {
        try {
            allCustomers = salesService.getAllCustomers();
            ObservableList<Customer> customerList = FXCollections.observableArrayList(allCustomers);
            customerCombo.setItems(customerList);

            // Set custom display to show customer name only
            customerCombo.setCellFactory(param -> new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getCustomerName() + " (Pts: " + item.getLoyaltyPoints() + ")");
                }
            });

            customerCombo.setButtonCell(new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.getCustomerName());
                }
            });

            // When customer is selected
            customerCombo.setOnAction(event -> {
                Customer selected = customerCombo.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    currentSale = salesService.createNewSale(selected.getCustomerId());
                    loyaltyPointsLabel.setText(String.valueOf(selected.getLoyaltyPoints()));
                    itemsObservable.clear();
                    updateTotals();
                }
            });
        } catch (Exception e) {
            showError("Error loading customers: " + e.getMessage());
        }
    }

    /**
     * Add Walk-in Customer option
     */
    private void addWalkInCustomerOption() {
        // Create a customer object with ID -1 for walk-in customer
        Customer walkIn = new Customer("Walk-in Customer", "", "", 0);
        walkIn.setCustomerId(0);  // Use 0 as placeholder
        
        List<Customer> customers = new ArrayList<>(customerCombo.getItems());
        customers.add(0, walkIn);
        customerCombo.setItems(FXCollections.observableArrayList(customers));
    }

    /**
     * Load products into memory for search
     */
    private void loadProducts() {
        try {
            allProducts = salesService.getAllProducts();
        } catch (Exception e) {
            showError("Error loading products: " + e.getMessage());
        }
    }

    /**
     * Setup product search with filtering
     */
    private void setupProductSearch() {
        productSearchField.setOnKeyReleased(event -> {
            String searchText = productSearchField.getText().trim().toLowerCase();
            
            if (searchText.isEmpty()) {
                productCombo.setItems(FXCollections.observableArrayList(allProducts));
            } else {
                // Filter products by name
                List<Product> filtered = new ArrayList<>();
                for (Product product : allProducts) {
                    if (product.getProductName().toLowerCase().contains(searchText) && !product.isOutOfStock()) {
                        filtered.add(product);
                    }
                }
                productCombo.setItems(FXCollections.observableArrayList(filtered));
            }

            // Set custom display for product combo
            productCombo.setCellFactory(param -> new ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("");
                    } else {
                        setText(item.getProductName() + " - Rs. " + item.getPrice() + " (Stock: " + item.getStock() + ")");
                    }
                }
            });

            productCombo.setButtonCell(new ListCell<Product>() {
                @Override
                protected void updateItem(Product item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getProductName());
                }
            });
        });
    }

    /**
     * Setup automatic calculation for payment
     */
    private void setupPaymentCalculations() {
        amountPaidField.setOnKeyReleased(event -> calculateChange());
        discountField.setOnKeyReleased(event -> {
            try {
                double manualDiscount = discountField.getText().trim().isEmpty() ? 0 : 
                    Double.parseDouble(discountField.getText().trim());
                
                if (currentSale != null) {
                    // If manual discount is entered, use it; otherwise use tiered discount
                    if (manualDiscount > 0) {
                        salesService.applyDiscount(currentSale, manualDiscount);
                    } else if (currentSale.getCustomerId() > 0) {
                        // Auto-apply tiered discount for registered customers
                        salesService.applyTieredDiscountBySale(currentSale, currentSale.getCustomerId());
                    } else {
                        // No discount for walk-in customers
                        currentSale.setDiscountAmount(0);
                        currentSale.recalculateTotals();
                    }
                    updateTotals();
                }
                calculateChange();
            } catch (NumberFormatException e) {
                // Invalid number, ignore
            }
        });
    }

    /**
     * Calculate change amount
     */
    private void calculateChange() {
        try {
            if (currentSale == null) {
                changeLabel.setText("Rs. 0.00");
                return;
            }

            double amountPaid = amountPaidField.getText().trim().isEmpty() ? 0 :
                Double.parseDouble(amountPaidField.getText().trim());

            double change = amountPaid - currentSale.getFinalAmount();
            changeLabel.setText(String.format("Rs. %.2f", change));

            // Color feedback
            if (change < 0) {
                changeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");
            } else {
                changeLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #059669;");
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("Rs. 0.00");
        }
    }

    /**
     * Handle adding product to sale
     */
    @FXML
    private void handleAddProduct() {
        if (currentSale == null) {
            showError("Please select a customer first");
            return;
        }

        Product selectedProduct = productCombo.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showError("Please select a product");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (quantity <= 0) {
                showError("Quantity must be greater than 0");
                return;
            }

            salesService.addItemToSale(currentSale, selectedProduct.getProductId(), quantity);

            itemsObservable.setAll(currentSale.getItems());
            updateTotals();

            // Clear input fields
            productSearchField.clear();
            productCombo.getSelectionModel().clearSelection();
            quantityField.clear();

            showInfo("Product added successfully");
        } catch (NumberFormatException e) {
            showError("Invalid quantity format");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /**
     * Handle removing product from table
     */
    private void handleRemoveItemFromTable(int index) {
        if (index >= 0 && index < currentSale.getItems().size()) {
            salesService.removeItemFromSale(currentSale, index);
            itemsObservable.setAll(currentSale.getItems());
            updateTotals();
            showInfo("Product removed successfully");
        }
    }

    /**
     * Handle removing selected product
     */
    @FXML
    private void handleRemoveProduct() {
        int selectedIndex = itemsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            handleRemoveItemFromTable(selectedIndex);
        } else {
            showError("Please select a product to remove");
        }
    }

    /**
     * Handle completing sale
     */
    @FXML
    private void handleCompleteSale() {
        if (currentSale == null) {
            showError("Please create a sale first");
            return;
        }

        if (currentSale.getItems().isEmpty()) {
            showError("Cannot complete sale without items");
            return;
        }

        String paymentMethod = paymentMethodCombo.getSelectionModel().getSelectedItem();
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            showError("Please select a payment method");
            return;
        }

        try {
            String amountPaidStr = amountPaidField.getText().trim();
            if (amountPaidStr.isEmpty()) {
                showError("Please enter amount paid");
                return;
            }

            double amountPaid = Double.parseDouble(amountPaidStr);

            if (amountPaid < currentSale.getFinalAmount()) {
                showError("Amount paid is less than final amount. Final: Rs. " + 
                         String.format("%.2f", currentSale.getFinalAmount()));
                return;
            }

            // Process payment
            salesService.processPayment(currentSale, paymentMethod, amountPaid);

            System.out.println("Saving sale with details:");
            System.out.println("- Company ID: " + currentSale.getCompanyId());
            System.out.println("- Customer ID: " + currentSale.getCustomerId());
            System.out.println("- Invoice No: " + currentSale.getInvoiceNo());
            System.out.println("- Final Amount: " + currentSale.getFinalAmount());
            System.out.println("- Payment Method: " + currentSale.getPaymentMethod());
            System.out.println("- Sold By: " + currentSale.getSoldBy());
            System.out.println("- Items Count: " + currentSale.getItems().size());

            // Save sale (reduces stock and updates loyalty points)
            int saleId = salesService.saveSale(currentSale);

            if (saleId > 0) {
                showInfo("Sale completed successfully!\n\nInvoice: " + currentSale.getInvoiceNo() + 
                        "\nChange: Rs. " + String.format("%.2f", currentSale.getChangeAmount()));
                handleClear();
            } else {
                showError("Failed to save sale. Check console for details.");
            }
        } catch (NumberFormatException e) {
            showError("Invalid amount paid format. Please enter a valid number.");
        } catch (Exception e) {
            System.err.println("Exception during sale completion: " + e.getMessage());
            e.printStackTrace();
            showError("Error completing sale: " + e.getMessage());
        }
    }

    /**
     * Handle clearing form
     */
    @FXML
    private void handleClear() {
        customerCombo.getSelectionModel().clearSelection();
        productSearchField.clear();
        productCombo.getSelectionModel().clearSelection();
        quantityField.clear();
        discountField.clear();
        amountPaidField.clear();
        paymentMethodCombo.getSelectionModel().clearSelection();
        itemsObservable.clear();
        currentSale = null;
        loyaltyPointsLabel.setText("0");
        updateTotals();
    }

    /**
     * Update totals display
     */
    private void updateTotals() {
        if (currentSale == null) {
            totalItemsLabel.setText("0");
            subtotalLabel.setText("Rs. 0.00");
            finalTotalLabel.setText("Rs. 0.00");
            changeLabel.setText("Rs. 0.00");
            discountField.clear();
            return;
        }

        int totalItems = currentSale.getItems().stream()
                .mapToInt(SaleItem::getQuantity)
                .sum();

        totalItemsLabel.setText(String.valueOf(totalItems));
        subtotalLabel.setText(String.format("Rs. %.2f", currentSale.getTotalAmount()));

        // Auto-apply tiered discount for registered customers if no manual discount set
        if (currentSale.getCustomerId() > 0 && discountField.getText().trim().isEmpty()) {
            Customer customer = salesService.getAllCustomers().stream()
                    .filter(c -> c.getCustomerId() == currentSale.getCustomerId())
                    .findFirst()
                    .orElse(null);
            
            if (customer != null) {
                double autoDiscount = salesService.calculateTieredDiscount(customer.getLoyaltyPoints(), currentSale.getTotalAmount());
                currentSale.setDiscountAmount(autoDiscount);
                currentSale.recalculateTotals();
                // Show the auto-applied discount
                if (autoDiscount > 0) {
                    discountField.setText(String.format("%.2f", autoDiscount));
                    discountField.setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                }
            }
        }

        finalTotalLabel.setText(String.format("Rs. %.2f", currentSale.getFinalAmount()));

        calculateChange();
    }

    /**
     * Show error dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info dialog
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
