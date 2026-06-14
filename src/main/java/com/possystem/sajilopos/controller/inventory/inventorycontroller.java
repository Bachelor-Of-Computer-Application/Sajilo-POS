package com.possystem.sajilopos.controller.inventory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class inventorycontroller {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Item> inventoryTable;

    @FXML
    private TableColumn<Item, String> colId;

    @FXML
    private TableColumn<Item, String> colName;

    @FXML
    private TableColumn<Item, String> colCategory;

    @FXML
    private TableColumn<Item, Integer> colStock;

    @FXML
    private TableColumn<Item, Double> colPrice;

    @FXML
    private TableColumn<Item, String> colSupplier;

    private final ObservableList<Item> inventoryList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colCategory.setCellValueFactory(data -> data.getValue().categoryProperty());
        colStock.setCellValueFactory(data -> data.getValue().stockProperty().asObject());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        colSupplier.setCellValueFactory(data -> data.getValue().supplierProperty());

        loadMockData();

        inventoryTable.setItems(inventoryList);
    }

    private void loadMockData() {

        inventoryList.addAll(
                new Item("P001", "Rice 10kg", "Grocery", 50, 1200.0, "ABC Suppliers"),
                new Item("P002", "Sugar 1kg", "Grocery", 200, 120.0, "XYZ Traders"),
                new Item("P003", "Milk Pack", "Dairy", 80, 60.0, "Dairy Co."),
                new Item("P004", "Soap", "Hygiene", 150, 45.0, "CleanMart")
        );
    }

    @FXML
    private void onSearch() {

        String keyword = searchField.getText().trim().toLowerCase();

        if (keyword.isEmpty()) {
            inventoryTable.setItems(inventoryList);
            return;
        }

        ObservableList<Item> filtered = FXCollections.observableArrayList(
                inventoryList.stream()
                        .filter(i ->
                                i.getName().toLowerCase().contains(keyword)
                                        || i.getCategory().toLowerCase().contains(keyword)
                                        || i.getId().toLowerCase().contains(keyword))
                        .collect(Collectors.toList())
        );

        inventoryTable.setItems(filtered);
    }

    @FXML
    private void onAdd() {

        inventoryList.add(
                new Item("P999", "New Item", "Category", 10, 0.0, "Supplier")
        );
    }

    @FXML
    private void onEdit() {

        Item selected = inventoryTable.getSelectionModel().getSelectedItem();

        if (selected != null) {

            selected.setName(
                    selected.getName() + " (Edited)"
            );

            inventoryTable.refresh();
        }
    }

    @FXML
    private void onDelete() {

        Item selected = inventoryTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            inventoryList.remove(selected);
        }
    }

    public static class Item {

        private final javafx.beans.property.SimpleStringProperty id;
        private final javafx.beans.property.SimpleStringProperty name;
        private final javafx.beans.property.SimpleStringProperty category;
        private final javafx.beans.property.SimpleIntegerProperty stock;
        private final javafx.beans.property.SimpleDoubleProperty price;
        private final javafx.beans.property.SimpleStringProperty supplier;

        public Item(
                String id,
                String name,
                String category,
                int stock,
                double price,
                String supplier
        ) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.name = new javafx.beans.property.SimpleStringProperty(name);
            this.category = new javafx.beans.property.SimpleStringProperty(category);
            this.stock = new javafx.beans.property.SimpleIntegerProperty(stock);
            this.price = new javafx.beans.property.SimpleDoubleProperty(price);
            this.supplier = new javafx.beans.property.SimpleStringProperty(supplier);
        }

        // Properties

        public javafx.beans.property.SimpleStringProperty idProperty() {
            return id;
        }

        public javafx.beans.property.SimpleStringProperty nameProperty() {
            return name;
        }

        public javafx.beans.property.SimpleStringProperty categoryProperty() {
            return category;
        }

        public javafx.beans.property.SimpleIntegerProperty stockProperty() {
            return stock;
        }

        public javafx.beans.property.SimpleDoubleProperty priceProperty() {
            return price;
        }

        public javafx.beans.property.SimpleStringProperty supplierProperty() {
            return supplier;
        }

        // Getters

        public String getId() {
            return id.get();
        }

        public String getName() {
            return name.get();
        }

        public String getCategory() {
            return category.get();
        }

        public int getStock() {
            return stock.get();
        }

        public double getPrice() {
            return price.get();
        }

        public String getSupplier() {
            return supplier.get();
        }

        // Setters

        public void setId(String id) {
            this.id.set(id);
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public void setCategory(String category) {
            this.category.set(category);
        }

        public void setStock(int stock) {
            this.stock.set(stock);
        }

        public void setPrice(double price) {
            this.price.set(price);
        }

        public void setSupplier(String supplier) {
            this.supplier.set(supplier);
        }
    }
}