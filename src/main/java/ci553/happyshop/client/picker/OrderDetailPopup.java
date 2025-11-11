package ci553.happyshop.client.picker;

import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.UIStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Week 6: Popup window for displaying detailed order information.
 * Shows full order details including all products, quantities, and prices.
 * This improves UX by allowing pickers to view order details without cluttering the main view.
 */
public class OrderDetailPopup {
    
    private Stage popupStage;
    private TextArea taOrderDetail;
    
    /**
     * Week 6: Constructor creates the popup window structure
     * Uses modal window to focus user attention on order details
     */
    public OrderDetailPopup() {
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("📦 Order Details");
        
        // Week 6: Title label for order identification
        Label laTitle = new Label("Order Details");
        laTitle.setStyle(UIStyle.labelTitleStyle);
        
        // Week 6: TextArea for displaying full order information
        taOrderDetail = new TextArea();
        taOrderDetail.setEditable(false);
        taOrderDetail.setWrapText(true);
        taOrderDetail.setPrefSize(500, 400);
        taOrderDetail.setStyle(UIStyle.textFiledStyle);
        
        // Week 6: Close button for dismissing popup
        Button btnClose = new Button("Close");
        btnClose.setStyle(UIStyle.buttonStyle);
        btnClose.setOnAction(e -> popupStage.close());
        
        HBox buttonBox = new HBox(btnClose);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        
        VBox root = new VBox(15, laTitle, taOrderDetail, buttonBox);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle(UIStyle.rootStyleGray);
        
        Scene scene = new Scene(root);
        popupStage.setScene(scene);
    }
    
    /**
     * Week 6: Shows the popup with specific order details
     * Fetches order information from OrderHub and displays it
     * 
     * @param orderId The ID of the order to display
     */
    public void showOrderDetails(int orderId) {
        try {
            // Week 6: Fetch order details from OrderHub
            OrderHub orderHub = OrderHub.getOrderHub();
            String orderDetail = orderHub.getOrderDetailForPicker(orderId);
            
            taOrderDetail.setText(orderDetail);
            popupStage.showAndWait();
            
        } catch (IOException e) {
            taOrderDetail.setText("Error loading order details:\n" + e.getMessage());
            popupStage.showAndWait();
        }
    }
}

