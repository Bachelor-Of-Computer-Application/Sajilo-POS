package com.possystem.sajilopos.controller.sales;

import com.possystem.sajilopos.model.Customer;
import com.possystem.sajilopos.model.Sale;
import com.possystem.sajilopos.model.SaleItem;
import com.possystem.sajilopos.model.User;
import com.possystem.sajilopos.config.SessionManager;
import javafx.fxml.FXML;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class InvoiceController {

    @FXML private VBox  receiptPane;

    @FXML private Label businessNameLabel;
    @FXML private Label businessAddressLabel;

    @FXML private Label invoiceNoLabel;
    @FXML private Label saleDateLabel;
    @FXML private Label customerLabel;
    @FXML private Label cashierLabel;

    @FXML private VBox  itemsContainer;

    @FXML private Label subtotalLabel;
    @FXML private Label discountLabel;
    @FXML private Label finalTotalLabel;
    @FXML private Label paymentMethodLabel;
    @FXML private Label amountPaidLabel;
    @FXML private Label changeLabel;

    @FXML private HBox  pointsRow;
    @FXML private Label pointsEarnedLabel;

    private Stage stage;

    // ── Public API — called from SalesController ──────────────────────────

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Populate the invoice with sale data.
     *
     * @param sale         completed sale
     * @param customer     customer object (null = walk-in)
     * @param pointsEarned loyalty points earned this sale
     */
    public void populate(Sale sale, Customer customer, int pointsEarned) {
        // Business info
        businessNameLabel.setText("SAJILO POS");
        businessAddressLabel.setText("");  // add address if stored in Company later

        // Invoice meta
        invoiceNoLabel.setText(sale.getInvoiceNo());

        String dateStr = sale.getSaleDate() != null
                ? sale.getSaleDate().toLocalDateTime()
                      .format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"))
                : "";
        saleDateLabel.setText(dateStr);

        customerLabel.setText(customer != null ? customer.getCustomerName() : "Walk-in Customer");

        User cashier = SessionManager.getInstance().getCurrentUser();
        cashierLabel.setText(cashier != null ? cashier.getUsername() : "");

        // Line items
        itemsContainer.getChildren().clear();
        for (SaleItem item : sale.getItems()) {
            HBox row = new HBox();
            Label nameLabel = new Label(item.getProductName());
            nameLabel.setStyle("-fx-font-size: 11px;");
            HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

            Label qtyLabel  = new Label(String.valueOf(item.getQuantity()));
            qtyLabel.setPrefWidth(36);
            qtyLabel.setStyle("-fx-font-size: 11px; -fx-alignment: CENTER;");

            Label priceLabel = new Label(String.format("%.0f", item.getUnitPrice()));
            priceLabel.setPrefWidth(60);
            priceLabel.setStyle("-fx-font-size: 11px; -fx-alignment: CENTER_RIGHT;");

            Label totalLabel = new Label(String.format("%.0f", item.getSubtotal()));
            totalLabel.setPrefWidth(70);
            totalLabel.setStyle("-fx-font-size: 11px; -fx-alignment: CENTER_RIGHT;");

            row.getChildren().addAll(nameLabel, qtyLabel, priceLabel, totalLabel);
            itemsContainer.getChildren().add(row);
        }

        // Totals
        subtotalLabel.setText(String.format("Rs. %.2f", sale.getTotalAmount()));
        discountLabel.setText(String.format("- Rs. %.2f", sale.getDiscountAmount()));
        finalTotalLabel.setText(String.format("Rs. %.2f", sale.getFinalAmount()));
        paymentMethodLabel.setText(sale.getPaymentMethod());
        amountPaidLabel.setText(String.format("Rs. %.2f", sale.getAmountPaid()));
        changeLabel.setText(String.format("Rs. %.2f", sale.getChangeAmount()));

        // Loyalty points
        if (pointsEarned > 0 && customer != null) {
            pointsEarnedLabel.setText("Points earned: " + pointsEarned
                    + "  |  Total: " + (customer.getLoyaltyPoints() + pointsEarned));
            pointsRow.setVisible(true);
            pointsRow.setManaged(true);
        } else {
            pointsRow.setVisible(false);
            pointsRow.setManaged(false);
        }
    }

    // ── Handlers ─────────────────────────────────────────────────────────

    @FXML
    private void handlePrint() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            showAlert("No printer found.");
            return;
        }

        if (!job.showPrintDialog(stage)) return;

        PageLayout layout = job.getJobSettings().getPageLayout();
        double scaleX = layout.getPrintableWidth()  / receiptPane.getBoundsInParent().getWidth();
        double scaleY = layout.getPrintableHeight() / receiptPane.getBoundsInParent().getHeight();
        double scale  = Math.min(scaleX, scaleY);

        Scale scaleTransform = new Scale(scale, scale);
        receiptPane.getTransforms().add(scaleTransform);

        boolean printed = job.printPage(receiptPane);

        receiptPane.getTransforms().remove(scaleTransform);

        if (printed) {
            job.endJob();
        } else {
            showAlert("Print failed. Please try again.");
        }
    }

    @FXML
    private void handleClose() {
        if (stage != null) stage.close();
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Print");
        a.setContentText(msg);
        a.showAndWait();
    }
}
