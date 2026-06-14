package com.possystem.sajilopos.controller.suppliers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class suppliersController {

    // 🔍 SEARCH FIELD
    @FXML
    private TextField searchField;


    @FXML
    private TableView<?> supplierTable;

    @FXML
    private TableColumn<?, ?> idColumn;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> phoneColumn;

    @FXML
    private TableColumn<?, ?> addressColumn;


    @FXML
    private void searchAction() {
        System.out.println("Searching suppliers: " + searchField.getText());
    }


    @FXML
    private void addAction() {
        System.out.println("Add supplier clicked");
    }

    @FXML
    private void editAction() {
        System.out.println("Edit supplier clicked");
    }


    @FXML
    private void deleteAction() {
        System.out.println("Delete supplier clicked");
    }
}