package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.PaymentMethod;
import ci553.happyshop.utility.UIStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Week 9: PaymentDialog is a modal window for payment method selection.
 * Demonstrates Adapter-like pattern by bridging user interaction to payment processing.
 * Displays total amount and allows user to choose between cash and card payment.
 */
public class PaymentDialog {

    private final int WIDTH = 400;
    private final int HEIGHT = 280; // Week 9: Increased height to fit all elements comfortably
    
    private PaymentResult result = null; // Week 9: Stores user's choice

    /**
     * Week 9: Shows payment dialog and waits for user input.
     * @param totalAmount The total order amount to display
     * @return PaymentResult containing user's choice
     */
    public PaymentResult show(double totalAmount) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Week 9: Blocks other windows
        dialogStage.setTitle("💳 Payment Required");

        // Week 9: Title label
        Label laTitle = new Label("Complete Your Payment");
        laTitle.setStyle(UIStyle.labelTitleStyle);

        // Week 9: Display total amount prominently
        Label laAmount = new Label(String.format("Total Amount: £%.2f", totalAmount));
        laAmount.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // Week 9: Payment method selection with RadioButtons
        Label laPaymentMethod = new Label("Select Payment Method:");
        laPaymentMethod.setStyle(UIStyle.labelStyle);

        ToggleGroup paymentGroup = new ToggleGroup();

        // Week 9: Card option (default)
        RadioButton rbCard = new RadioButton("💳 Credit/Debit Card");
        rbCard.setToggleGroup(paymentGroup);
        rbCard.setSelected(true); // Week 9: Default selection
        rbCard.setStyle("-fx-font-size: 14px;");

        // Week 9: Cash option
        RadioButton rbCash = new RadioButton("💵 Cash");
        rbCash.setToggleGroup(paymentGroup);
        rbCash.setStyle("-fx-font-size: 14px;");

        VBox vbPaymentOptions = new VBox(10, rbCard, rbCash);
        vbPaymentOptions.setPadding(new Insets(10, 0, 10, 20));

        // Week 9: Action buttons
        Button btnConfirm = new Button("✓ Confirm Payment");
        btnConfirm.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; " +
                           "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnConfirm.setPrefWidth(160);

        Button btnCancel = new Button("✗ Cancel");
        btnCancel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 14px; " +
                          "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnCancel.setPrefWidth(160);

        HBox hbButtons = new HBox(15, btnConfirm, btnCancel);
        hbButtons.setAlignment(Pos.CENTER);

        // Week 9: Lambda expressions for button actions
        btnConfirm.setOnAction(event -> {
            // Week 9: Determine selected payment method
            PaymentMethod selectedMethod = rbCard.isSelected() ? 
                PaymentMethod.CREDIT_CARD : PaymentMethod.CASH;
            
            result = PaymentResult.confirmed(selectedMethod); // Week 9: Store result
            dialogStage.close();
        });

        btnCancel.setOnAction(event -> {
            result = PaymentResult.cancelled(); // Week 9: User cancelled payment
            dialogStage.close();
        });

        // Week 9: Layout assembly
        VBox root = new VBox(20, laTitle, laAmount, laPaymentMethod, vbPaymentOptions, hbButtons);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F5F5F5; -fx-border-color: #4CAF50; " +
                     "-fx-border-width: 2; -fx-border-radius: 5;");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false); // Week 9: Fixed size for consistent UX
        
        dialogStage.showAndWait(); // Week 9: Modal - waits for user action

        // Week 9: Return result (cancelled if window closed without action)
        return result != null ? result : PaymentResult.cancelled();
    }
}

