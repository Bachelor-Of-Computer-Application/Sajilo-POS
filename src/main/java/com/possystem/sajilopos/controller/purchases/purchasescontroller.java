package com.possystem.sajilopos.controller.purchases;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.stream.Collectors;

public class purchasescontroller {

    @FXML private TextField searchField;
    @FXML private TableView<Purchase> purchasesTable;

    @FXML private TableColumn<Purchase, String> colId;
    @FXML private TableColumn<Purchase, String> colItem;
    @FXML private TableColumn<Purchase, String> colSupplier;
    @FXML private TableColumn<Purchase, Integer> colQty;
    @FXML private TableColumn<Purchase, Double> colCost;
    @FXML private TableColumn<Purchase, String> colDate;

    private final ObservableList<Purchase> purchaseList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(data -> data.getValue().idProperty());
        colItem.setCellValueFactory(data -> data.getValue().itemProperty());
        colSupplier.setCellValueFactory(data -> data.getValue().supplierProperty());
        colQty.setCellValueFactory(data -> data.getValue().qtyProperty().asObject());
        colCost.setCellValueFactory(data -> data.getValue().costProperty().asObject());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());

        loadMockData();
        purchasesTable.setItems(purchaseList);
    }

    private void loadMockData() {
        purchaseList.addAll(
                new Purchase("P1001", "Rice 10kg", "ABC Traders", 10, 12000, "2026-06-01"),
                new Purchase("P1002", "Sugar 1kg", "XYZ Suppliers", 50, 6000, "2026-06-02"),
                new Purchase("P1003", "Cooking Oil", "FreshMart", 20, 15000, "2026-06-03"),
                new Purchase("P1004", "Soap Pack", "CleanCo", 100, 9000, "2026-06-04")
        );
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().toLowerCase();

        if (keyword.isEmpty()) {
            purchasesTable.setItems(purchaseList);
            return;
        }

        ObservableList<Purchase> filtered = FXCollections.observableArrayList(
                purchaseList.stream()
                        .filter(p ->
                                p.getId().toLowerCase().contains(keyword) ||
                                        p.getItem().toLowerCase().contains(keyword) ||
                                        p.getSupplier().toLowerCase().contains(keyword)
                        )
                        .collect(Collectors.toList())
        );

        purchasesTable.setItems(filtered);
    }

    @FXML
    private void onAdd() {
        purchaseList.add(new Purchase(
                "P9999",
                "New Item",
                "New Supplier",
                1,
                1000,
                "2026-06-14"
        ));
    }

    @FXML
    private void onEdit() {
        Purchase selected = purchasesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setItem(selected.getItem() + " (Updated)");
            purchasesTable.refresh();
        }
    }

    @FXML
    private void onDelete() {
        Purchase selected = purchasesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            purchaseList.remove(selected);
        }
    }

    public static class Purchase {

        private final javafx.beans.property.SimpleStringProperty id;
        private final javafx.beans.property.SimpleStringProperty item;
        private final javafx.beans.property.SimpleStringProperty supplier;
        private final javafx.beans.property.SimpleIntegerProperty qty;
        private final javafx.beans.property.SimpleDoubleProperty cost;
        private final javafx.beans.property.SimpleStringProperty date;

        public Purchase(String id, String item, String supplier, int qty, double cost, String date) {
            this.id = new javafx.beans.property.SimpleStringProperty(id);
            this.item = new javafx.beans.property.SimpleStringProperty(item);
            this.supplier = new javafx.beans.property.SimpleStringProperty(supplier);
            this.qty = new javafx.beans.property.SimpleIntegerProperty(qty);
            this.cost = new javafx.beans.property.SimpleDoubleProperty(cost);
            this.date = new javafx.beans.property.SimpleStringProperty(date);
        }

        public javafx.beans.property.SimpleStringProperty idProperty() { return id; }
        public javafx.beans.property.SimpleStringProperty itemProperty() { return item; }
        public javafx.beans.property.SimpleStringProperty supplierProperty() { return supplier; }
        public javafx.beans.property.SimpleIntegerProperty qtyProperty() { return qty; }
        public javafx.beans.property.SimpleDoubleProperty costProperty() { return cost; }
        public javafx.beans.property.SimpleStringProperty dateProperty() { return date; }

        public String getId() { return id.get(); }
        public String getItem() { return item.get(); }
        public String getSupplier() { return supplier.get(); }

        public void setItem(String value) { item.set(value); }
    }
}