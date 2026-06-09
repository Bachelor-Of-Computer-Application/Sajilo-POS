package com.possystem.sajilopos.controller.product;

import com.possystem.sajilopos.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductController {

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private int idCounter = 1;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());

        colName.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));

        colPrice.setCellValueFactory(d ->
                new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrice()).asObject());

        colStock.setCellValueFactory(d ->
                new javafx.beans.property.SimpleIntegerProperty(d.getValue().getStock()).asObject());

        productTable.setItems(products);
    }

    @FXML
    private void handleAdd() {
        Product p = new Product(
                idCounter++,
                nameField.getText(),
                Double.parseDouble(priceField.getText()),
                Integer.parseInt(stockField.getText())
        );

        products.add(p);
        clear();
    }

    @FXML
    private void handleUpdate() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setPrice(Double.parseDouble(priceField.getText()));
            selected.setStock(Integer.parseInt(stockField.getText()));
            productTable.refresh();
        }
    }

    @FXML
    private void handleDelete() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            products.remove(selected);
        }
    }

    private void clear() {
        nameField.clear();
        priceField.clear();
        stockField.clear();
    }
}