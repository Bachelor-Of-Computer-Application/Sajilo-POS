package com.possystem.sajilopos.controller.inventory;

import com.possystem.sajilopos.model.InventoryHistory;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.service.InventoryService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;

public class inventorycontroller {

    @FXML private TextField searchField;
    @FXML private TableView<Product> inventoryTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colProduct;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colStatus;

    @FXML private TableView<InventoryHistory> historyTable;
    @FXML private TableColumn<InventoryHistory, String> colHistoryProduct;
    @FXML private TableColumn<InventoryHistory, String> colAction;
    @FXML private TableColumn<InventoryHistory, Integer> colQty;
    @FXML private TableColumn<InventoryHistory, String> colDate;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<InventoryHistory> history = FXCollections.observableArrayList();
    private final InventoryService inventoryService = new InventoryService();

    @FXML
    public void initialize() {
        // Setup inventory table columns
        colId.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getProductId()).asObject());
        
        colProduct.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProductName()));
        
        colPrice.setCellValueFactory(data -> 
            new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        
        colStock.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getStock()).asObject());
        
        colStatus.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStockStatus()));

        // Apply row coloring based on stock status
        inventoryTable.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setStyle("");
                } else {
                    if (product.isOutOfStock()) {
                        setStyle("-fx-background-color: #fee2e2;"); // Light red
                    } else if (product.isLowStock()) {
                        setStyle("-fx-background-color: #fef3c7;"); // Light yellow
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Setup history table columns
        colHistoryProduct.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getProductName()));
        
        colAction.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getActionType().name()));
        
        colQty.setCellValueFactory(data -> 
            new SimpleIntegerProperty(data.getValue().getQuantity()).asObject());
        
        colDate.setCellValueFactory(data -> {
            if (data.getValue().getCreatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return new SimpleStringProperty(
                    data.getValue().getCreatedAt().toLocalDateTime().format(formatter)
                );
            }
            return new SimpleStringProperty("");
        });

        inventoryTable.setItems(products);
        historyTable.setItems(history);

        // Load data
        loadInventory();
        loadRecentHistory();
    }

    private void loadInventory() {
        try {
            products.clear();
            products.addAll(inventoryService.getAllProducts());
            System.out.println("Loaded " + products.size() + " products");
        } catch (Exception e) {
            showError("Error Loading Inventory", e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRecentHistory() {
        try {
            history.clear();
            history.addAll(inventoryService.getTodayHistory());
            System.out.println("Loaded " + history.size() + " history records");
        } catch (Exception e) {
            showError("Error Loading History", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String searchText = searchField.getText();
            products.clear();
            products.addAll(inventoryService.searchProducts(searchText));
        } catch (Exception e) {
            showError("Search Error", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadInventory();
        loadRecentHistory();
    }

    @FXML
    private void handleAdjustStock() {
        Product selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a product to adjust stock");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Adjust Stock");
        dialog.setHeaderText("Adjust stock for: " + selected.getProductName());

        ButtonType okButton = new ButtonType("Adjust", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField qtyField = new TextField();
        qtyField.setPromptText("Enter quantity (+/-)");
        TextArea remarksArea = new TextArea();
        remarksArea.setPromptText("Remarks...");
        remarksArea.setPrefRowCount(3);

        grid.add(new Label("Current Stock:"), 0, 0);
        grid.add(new Label(String.valueOf(selected.getStock())), 1, 0);
        grid.add(new Label("Adjustment:"), 0, 1);
        grid.add(qtyField, 1, 1);
        grid.add(new Label("Remarks:"), 0, 2);
        grid.add(remarksArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                try {
                    int adjustment = Integer.parseInt(qtyField.getText());
                    String remarks = remarksArea.getText();
                    
                    boolean success = inventoryService.adjustStock(
                        selected.getProductId(), adjustment, remarks
                    );
                    
                    if (success) {
                        showInfo("Success", "Stock adjusted successfully");
                        loadInventory();
                        loadRecentHistory();
                    } else {
                        showError("Error", "Failed to adjust stock");
                    }
                } catch (NumberFormatException e) {
                    showWarning("Invalid Input", "Please enter a valid number");
                } catch (IllegalArgumentException e) {
                    showWarning("Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRecordPurchase() {
        Product selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a product to record purchase");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Record Purchase");
        dialog.setHeaderText("Record purchase for: " + selected.getProductName());

        ButtonType okButton = new ButtonType("Record", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField qtyField = new TextField();
        qtyField.setPromptText("Enter quantity");
        TextArea remarksArea = new TextArea();
        remarksArea.setPromptText("Remarks (supplier, invoice, etc.)");
        remarksArea.setPrefRowCount(3);

        grid.add(new Label("Current Stock:"), 0, 0);
        grid.add(new Label(String.valueOf(selected.getStock())), 1, 0);
        grid.add(new Label("Purchase Qty:"), 0, 1);
        grid.add(qtyField, 1, 1);
        grid.add(new Label("Remarks:"), 0, 2);
        grid.add(remarksArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == okButton) {
                try {
                    int quantity = Integer.parseInt(qtyField.getText());
                    String remarks = remarksArea.getText();
                    
                    boolean success = inventoryService.recordPurchase(
                        selected.getProductId(), quantity, remarks
                    );
                    
                    if (success) {
                        showInfo("Success", "Purchase recorded successfully");
                        loadInventory();
                        loadRecentHistory();
                    } else {
                        showError("Error", "Failed to record purchase");
                    }
                } catch (NumberFormatException e) {
                    showWarning("Invalid Input", "Please enter a valid number");
                } catch (IllegalArgumentException e) {
                    showWarning("Error", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleShowLowStock() {
        try {
            products.clear();
            products.addAll(inventoryService.getLowStockProducts());
            showInfo("Low Stock", "Showing " + products.size() + " low stock products");
        } catch (Exception e) {
            showError("Error", e.getMessage());
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
