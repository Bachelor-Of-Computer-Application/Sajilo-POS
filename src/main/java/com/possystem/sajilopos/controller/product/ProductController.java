package com.possystem.sajilopos.controller.product;

import com.possystem.sajilopos.model.Category;
import com.possystem.sajilopos.model.Product;
import com.possystem.sajilopos.service.CategoryService;
import com.possystem.sajilopos.service.ProductService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class ProductController {

    // ── Form fields ───────────────────────────────────────────────────────
    @FXML private TextField         nameField;
    @FXML private TextField         priceField;
    @FXML private TextField         stockField;
    @FXML private TextArea          descriptionArea;
    @FXML private ComboBox<Category> categoryCombo;  // NEW

    // ── Search ────────────────────────────────────────────────────────────
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> filterCategoryCombo; // filter table by category

    // ── Category management panel ─────────────────────────────────────────
    @FXML private VBox                categoryMgmtPanel;
    @FXML private TextField           newCategoryField;
    @FXML private ListView<Category>  categoryListView;
    @FXML private Label               categoryStatusLabel;

    // ── Table ─────────────────────────────────────────────────────────────
    @FXML private TableView<Product>              productTable;
    @FXML private TableColumn<Product, Integer>   colId;
    @FXML private TableColumn<Product, String>    colName;
    @FXML private TableColumn<Product, String>    colCategory;  // NEW
    @FXML private TableColumn<Product, Double>    colPrice;
    @FXML private TableColumn<Product, Integer>   colStock;
    @FXML private TableColumn<Product, String>    colDescription;

    private final ObservableList<Product>  products         = FXCollections.observableArrayList();
    private final ProductService           productService   = new ProductService();
    private final CategoryService          categoryService  = new CategoryService();
    private final com.possystem.sajilopos.config.SessionManager session =
            com.possystem.sajilopos.config.SessionManager.getInstance();
    private       Product                  selectedProduct  = null;
    private       List<Category>           allCategories;

    @FXML
    public void initialize() {
        // ── Table column bindings ──────────────────────────────────────────
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getProductId()).asObject());

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProductName()));

        colCategory.setCellValueFactory(data -> {
            int catId = data.getValue().getCategoryId();
            String catName = categoryName(catId);
            return new SimpleStringProperty(catName);
        });

        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPrice()).asObject());

        colStock.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        colDescription.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDescription()));

        productTable.setItems(products);

        // ── Load categories into combos ────────────────────────────────────
        loadCategories();

        // ── Load products ──────────────────────────────────────────────────
        loadProducts();

        // ── Row selection → populate form ─────────────────────────────────
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedProduct = selected;
                populateFields(selected);
            }
        });

        // ── Filter by category ─────────────────────────────────────────────
        filterCategoryCombo.setOnAction(e -> applyFilter());

        // ── Category management panel (MANAGER/ADMIN only) ─────────────────
        boolean canManageCategories = session.isAdmin() || session.isManager();
        categoryMgmtPanel.setVisible(canManageCategories);
        categoryMgmtPanel.setManaged(canManageCategories);
        if (canManageCategories) {
            refreshCategoryListView();
        }
    }

    // ── Category helpers ──────────────────────────────────────────────────

    private void loadCategories() {
        try {
            allCategories = categoryService.getAllCategories();

            // Form combo — plain list of categories
            categoryCombo.getItems().clear();
            categoryCombo.getItems().add(new Category(0, 0, "-- No Category --"));
            categoryCombo.getItems().addAll(allCategories);
            categoryCombo.getSelectionModel().selectFirst();

            // Filter combo — "All" option + categories
            filterCategoryCombo.getItems().clear();
            filterCategoryCombo.getItems().add(new Category(0, 0, "All Categories"));
            filterCategoryCombo.getItems().addAll(allCategories);
            filterCategoryCombo.getSelectionModel().selectFirst();

            // Also refresh the management ListView
            refreshCategoryListView();

        } catch (Exception e) {
            System.err.println("Could not load categories: " + e.getMessage());
        }
    }

    /** Refresh the category management ListView with current categories. */
    private void refreshCategoryListView() {
        if (categoryListView == null) return;
        try {
            allCategories = categoryService.getAllCategories();
            categoryListView.setItems(FXCollections.observableArrayList(allCategories));
        } catch (Exception e) {
            System.err.println("Could not refresh category list: " + e.getMessage());
        }
    }

    // ── Category management handlers ──────────────────────────────────────

    @FXML
    private void handleAddCategory() {
        String name = newCategoryField.getText().trim();
        if (name.isEmpty()) {
            setCatStatus("Enter a name for the item type.", true);
            return;
        }
        String result = categoryService.addCategory(name);
        setCatStatus(result, result.startsWith("Failed") || result.contains("empty"));
        if (result.contains("successfully")) {
            newCategoryField.clear();
            loadCategories();
        }
    }

    @FXML
    private void handleRenameCategory() {
        Category selected = categoryListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setCatStatus("Select an item type to rename.", true);
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("Rename Item Type");
        dialog.setHeaderText("Rename: " + selected.getName());
        dialog.setContentText("New name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) return;

        String msg = categoryService.updateCategory(selected.getId(), result.get().trim());
        setCatStatus(msg, msg.startsWith("Failed"));
        if (msg.contains("successfully")) loadCategories();
    }

    @FXML
    private void handleDeleteCategory() {
        Category selected = categoryListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setCatStatus("Select an item type to delete.", true);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Item Type");
        confirm.setHeaderText("Delete '" + selected.getName() + "'?");
        confirm.setContentText("Products in this category will become uncategorised.");
        if (confirm.showAndWait().get() != ButtonType.OK) return;

        String msg = categoryService.deleteCategory(selected.getId());
        setCatStatus(msg, msg.startsWith("Failed"));
        if (msg.contains("successfully")) {
            loadCategories();
            loadProducts(); // refresh table so category column shows "—"
        }
    }

    private void setCatStatus(String message, boolean isError) {
        if (categoryStatusLabel == null) return;
        categoryStatusLabel.setText(message);
        categoryStatusLabel.setStyle(isError
                ? "-fx-text-fill: #dc2626;"
                : "-fx-text-fill: #16a34a;");
    }

    /** Returns the category name for a given id, or "—" if uncategorised. */
    private String categoryName(int categoryId) {
        if (categoryId <= 0 || allCategories == null) return "—";
        return allCategories.stream()
                .filter(c -> c.getId() == categoryId)
                .map(Category::getName)
                .findFirst()
                .orElse("—");
    }

    // ── Data loading ──────────────────────────────────────────────────────

    private void loadProducts() {
        try {
            products.clear();
            products.addAll(productService.getAllProducts());
        } catch (Exception e) {
            showError("Error Loading Products", e.getMessage());
        }
    }

    /** Filter table by selected category. */
    private void applyFilter() {
        Category selected = filterCategoryCombo.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            loadProducts();
            return;
        }
        try {
            products.clear();
            productService.getAllProducts().stream()
                    .filter(p -> p.getCategoryId() == selected.getId())
                    .forEach(products::add);
        } catch (Exception e) {
            showError("Filter Error", e.getMessage());
        }
    }

    /** Populate form fields when a row is selected. */
    private void populateFields(Product product) {
        nameField.setText(product.getProductName());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
        descriptionArea.setText(product.getDescription());

        // Select matching category in combo
        categoryCombo.getItems().stream()
                .filter(c -> c.getId() == product.getCategoryId())
                .findFirst()
                .ifPresentOrElse(
                        c -> categoryCombo.getSelectionModel().select(c),
                        () -> categoryCombo.getSelectionModel().selectFirst()
                );
    }

    private void clearFields() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
        descriptionArea.clear();
        categoryCombo.getSelectionModel().selectFirst();
        selectedProduct = null;
        productTable.getSelectionModel().clearSelection();
    }

    // ── CRUD handlers ─────────────────────────────────────────────────────

    @FXML
    private void handleAdd() {
        try {
            String name        = nameField.getText().trim();
            String priceText   = priceField.getText().trim();
            String stockText   = stockField.getText().trim();
            String description = descriptionArea.getText();
            int    categoryId  = selectedCategoryId();

            if (name.isEmpty()) { showWarning("Validation Error", "Product name is required"); return; }
            if (priceText.isEmpty()) { showWarning("Validation Error", "Price is required"); return; }
            if (stockText.isEmpty()) { showWarning("Validation Error", "Stock quantity is required"); return; }

            double price = Double.parseDouble(priceText);
            int    stock = Integer.parseInt(stockText);

            boolean ok = productService.addProduct(name, price, stock, description, categoryId);
            if (ok) {
                showInfo("Success", "Product added successfully");
                loadProducts();
                clearFields();
            } else {
                showError("Error", "Failed to add product");
            }
        } catch (NumberFormatException e) {
            showWarning("Invalid Input", "Price and Stock must be valid numbers");
        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to add product: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedProduct == null) { showWarning("No Selection", "Please select a product to update"); return; }

        try {
            String name        = nameField.getText().trim();
            String priceText   = priceField.getText().trim();
            String stockText   = stockField.getText().trim();
            String description = descriptionArea.getText();
            int    categoryId  = selectedCategoryId();

            if (name.isEmpty()) { showWarning("Validation Error", "Product name is required"); return; }
            if (priceText.isEmpty()) { showWarning("Validation Error", "Price is required"); return; }
            if (stockText.isEmpty()) { showWarning("Validation Error", "Stock quantity is required"); return; }

            double price = Double.parseDouble(priceText);
            int    stock = Integer.parseInt(stockText);

            boolean ok = productService.updateProduct(
                    selectedProduct.getProductId(), name, price, stock, description, categoryId);
            if (ok) {
                showInfo("Success", "Product updated successfully");
                loadProducts();
                clearFields();
            } else {
                showError("Error", "Failed to update product");
            }
        } catch (NumberFormatException e) {
            showWarning("Invalid Input", "Price and Stock must be valid numbers");
        } catch (IllegalArgumentException e) {
            showWarning("Validation Error", e.getMessage());
        } catch (Exception e) {
            showError("Error", "Failed to update product: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedProduct == null) { showWarning("No Selection", "Please select a product to delete"); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Product");
        confirm.setContentText("Delete '" + selectedProduct.getProductName() + "'?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                boolean ok = productService.deleteProduct(selectedProduct.getProductId());
                if (ok) {
                    showInfo("Success", "Product deleted successfully");
                    loadProducts();
                    clearFields();
                } else {
                    showError("Error", "Failed to delete product");
                }
            } catch (Exception e) {
                showError("Error", "Failed to delete product: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        try {
            products.clear();
            products.addAll(productService.searchProducts(searchField.getText()));
        } catch (Exception e) {
            showError("Search Error", e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        clearFields();
        filterCategoryCombo.getSelectionModel().selectFirst();
        searchField.clear();
    }

    @FXML
    private void handleRefresh() {
        loadProducts();
        loadCategories();
        searchField.clear();
        filterCategoryCombo.getSelectionModel().selectFirst();
        if (categoryMgmtPanel != null && categoryMgmtPanel.isVisible()) {
            refreshCategoryListView();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private int selectedCategoryId() {
        Category selected = categoryCombo.getSelectionModel().getSelectedItem();
        return (selected != null) ? selected.getId() : 0;
    }

    private void showInfo(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }

    private void showError(String title, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(message); a.showAndWait();
    }
}
