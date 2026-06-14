package com.possystem.sajilopos.controller.sales;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class salescontroller {

    @FXML private TableView<?> cartTable;
    @FXML private TableColumn<?, ?> productCol;
    @FXML private TableColumn<?, ?> qtyCol;
    @FXML private TableColumn<?, ?> priceCol;

    @FXML private TextField barcodeField;
    @FXML private Label totalLabel;

    @FXML
    private void initialize() {
        totalLabel.setText("Total: 0");
    }

    @FXML
    private void addItem() {
        System.out.println("Add item: " + barcodeField.getText());
        barcodeField.clear();
    }

    @FXML
    private void checkout() {
        System.out.println("Checkout clicked");
    }
}