package ci553.happyshop.client.customer;

import ci553.happyshop.utility.UIStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Week 10: CustomerTypeInfoDialog displays information about customer tier benefits
 * Shows advantages when customer changes their type (Standard/VIP/Prime)
 */
public class CustomerTypeInfoDialog {

    private final int WIDTH = 380;
    private final int HEIGHT = 280;

    /**
     * Week 10: Shows information dialog for the selected customer type
     * @param customerType The selected customer type (Standard/VIP/Prime)
     */
    public void show(String customerType) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Week 10: Modal dialog
        dialogStage.setTitle("Customer Type Information");

        // Week 10: Title with emoji based on customer type
        String titleEmoji = customerType.equals("VIP") ? "🌟" : 
                           customerType.equals("Prime") ? "⭐" : "👤";
        Label laTitle = new Label(titleEmoji + " " + customerType + " Customer");
        laTitle.setStyle(UIStyle.labelTitleStyle + "-fx-font-size: 18px;");

        // Week 10: Benefits description based on customer type
        String benefitsText = getBenefitsText(customerType);
        Label laBenefits = new Label(benefitsText);
        laBenefits.setWrapText(true);
        laBenefits.setMaxWidth(WIDTH - 40);
        laBenefits.setStyle("-fx-font-size: 13px; -fx-line-spacing: 3px;");

        // Week 10: OK button to close
        Button btnOk = new Button("✓ Got it!");
        btnOk.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; " +
                      "-fx-padding: 10 30; -fx-background-radius: 5; -fx-cursor: hand;");
        btnOk.setPrefWidth(150);
        btnOk.setOnAction(event -> dialogStage.close());

        // Week 10: Layout assembly
        VBox root = new VBox(20, laTitle, laBenefits, btnOk);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F9F9F9; -fx-border-color: #4CAF50; " +
                     "-fx-border-width: 2; -fx-border-radius: 5;");

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        dialogStage.setScene(scene);
        dialogStage.setResizable(false);
        dialogStage.showAndWait(); // Week 10: Wait for user to close
    }

    /**
     * Week 10: Returns benefits text for each customer type
     * @param customerType The customer type
     * @return Formatted benefits text
     */
    private String getBenefitsText(String customerType) {
        switch (customerType) {
            case "VIP":
                return """
                       Welcome to VIP membership!
                       
                       Your Benefits:
                       ✓ No minimum order requirement
                       ✓ Fast delivery from warehouse
                       ✓ Staff will prepare and deliver your order quickly
                       ✓ Priority customer support
                       
                       Enjoy your enhanced shopping experience!
                       """;
            
            case "Prime":
                return """
                       Welcome to Prime membership!
                       
                       Your Exclusive Benefits:
                       ⭐ No minimum order requirement
                       ⭐ Express delivery from warehouse
                       ⭐ 10% discount on all orders
                       ⭐ Staff will prioritize and deliver your order immediately
                       ⭐ Dedicated customer support
                       
                       Enjoy premium shopping with great savings!
                       """;
            
            default: // Standard
                return """
                       Standard Customer Account
                       
                       Features:
                       • Minimum order: £5.00
                       • Standard delivery from warehouse
                       • Regular customer support
                       
                       Upgrade to VIP or Prime for exclusive benefits
                       and faster service!
                       """;
        }
    }
}

