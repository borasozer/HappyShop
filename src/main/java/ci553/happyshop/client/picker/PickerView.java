package ci553.happyshop.client.picker;

import ci553.happyshop.orderManagement.OrderState;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Week 6: Redesigned Order Picker window with modern ListView-based UI.
 * Each order is displayed as a separate row with:
 * - Order ID
 * - Details button (shows popup with full order information)
 * - State buttons: Ordered, Progressing, Ready, Collected
 * - Active state is highlighted, others are dimmed
 * 
 * This improves UX by allowing pickers to:
 * - See all orders at once
 * - Change order states individually
 * - View detailed order information without switching views
 */

public class PickerView  {
    public PickerController pickerController;

    private final int WIDTH = UIStyle.pickerWinWidth;
    private final int HEIGHT = UIStyle.pickerWinHeight;

    // Week 6: ListView replaces TextArea for better order management
    private ListView<OrderEntry> lvOrders;
    private TreeMap<Integer, OrderState> orderMap = new TreeMap<>();

    /**
     * Week 6: Initialize and display the picker window with ListView-based UI
     */
    public void start(Stage window) {
        Label laTitle = new Label("📦 Order Management");
        laTitle.setStyle(UIStyle.labelTitleStyle);

        // Week 6: Initialize ListView for orders with better sizing
        lvOrders = new ListView<>();
        lvOrders.setPrefHeight(HEIGHT - 80);
        lvOrders.setStyle("-fx-font-size: 12px;");
        
        // Week 6: Custom ListCell for each order row
        lvOrders.setCellFactory(param -> new OrderListCell());
        
        VBox root = new VBox(10, laTitle, lvOrders);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(15));
        root.setStyle(UIStyle.rootStyleYellow);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("🛒 HappyShop Order Picker");
        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
    }

    /**
     * Week 6: Updates picker UI with new order data using ListView
     * Platform.runLater ensures UI updates happen on JavaFX Application Thread
     */
    void update(TreeMap<Integer, OrderState> newOrderMap) {
        Platform.runLater(() -> {
            orderMap.clear();
            orderMap.putAll(newOrderMap);
            
            // Week 6: Convert order map to observable list for ListView
            lvOrders.getItems().clear();
            for (Map.Entry<Integer, OrderState> entry : orderMap.entrySet()) {
                lvOrders.getItems().add(new OrderEntry(entry.getKey(), entry.getValue()));
            }
        });
    }

    /**
     * Week 6: Data class to represent an order entry in the ListView
     */
    private static class OrderEntry {
        private final int orderId;
        private OrderState state;

        public OrderEntry(int orderId, OrderState state) {
            this.orderId = orderId;
            this.state = state;
        }

        public int getOrderId() {
            return orderId;
        }

        public OrderState getState() {
            return state;
        }

        public void setState(OrderState state) {
            this.state = state;
        }
    }

    /**
     * Week 6: Custom ListCell for rendering each order with buttons
     * Demonstrates custom UI components and lambda expressions for event handling
     */
    private class OrderListCell extends ListCell<OrderEntry> {
        private final HBox cellLayout = new HBox(10);
        private final Label laOrderId = new Label();
        private final Button btnDetails = new Button("Details");
        private final Button btnOrdered = new Button("Ordered");
        private final Button btnProgressing = new Button("Progressing");
        private final Button btnReady = new Button("Ready");
        private final Button btnCollected = new Button("Collected");

        public OrderListCell() {
            // Week 6: Style components
            laOrderId.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            laOrderId.setPrefWidth(100);
            
            btnDetails.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 11px;");
            btnDetails.setPrefWidth(70);
            
            // Week 6: Set fixed width for state buttons for consistent layout
            btnOrdered.setPrefWidth(85);
            btnProgressing.setPrefWidth(100);
            btnReady.setPrefWidth(70);
            btnCollected.setPrefWidth(90);

            // Week 6: Lambda expressions for button actions
            btnDetails.setOnAction(e -> {
                OrderEntry order = getItem();
                if (order != null) {
                    OrderDetailPopup popup = new OrderDetailPopup();
                    popup.showOrderDetails(order.getOrderId());
                }
            });

            // Week 6: State button event handlers
            btnOrdered.setOnAction(e -> changeOrderState(OrderState.Ordered));
            btnProgressing.setOnAction(e -> changeOrderState(OrderState.Progressing));
            btnReady.setOnAction(e -> changeOrderState(OrderState.Ready));
            btnCollected.setOnAction(e -> changeOrderState(OrderState.Collected));

            // Week 6: Layout with spacer
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Week 6: Layout with proper spacing and padding for better visibility
            cellLayout.getChildren().addAll(
                laOrderId, btnDetails, spacer,
                btnOrdered, btnProgressing, btnReady, btnCollected
            );
            cellLayout.setAlignment(Pos.CENTER_LEFT);
            cellLayout.setPadding(new Insets(8, 10, 8, 10)); // top, right, bottom, left
        }

        @Override
        protected void updateItem(OrderEntry order, boolean empty) {
            super.updateItem(order, empty);

            if (empty || order == null) {
                setText(null);
                setGraphic(null);
            } else {
                laOrderId.setText("Order #" + order.getOrderId());
                
                // Week 6: Update button styles based on current state
                updateStateButtons(order.getState());
                
                setGraphic(cellLayout);
            }
        }

        /**
         * Week 6: Updates state button appearance based on active state
         * Active button is highlighted, others are dimmed
         */
        private void updateStateButtons(OrderState currentState) {
            String activeStyle = "-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;";
            String inactiveStyle = "-fx-background-color: #CCCCCC; -fx-text-fill: #666666; -fx-font-size: 11px;";

            btnOrdered.setStyle(currentState == OrderState.Ordered ? activeStyle : inactiveStyle);
            btnProgressing.setStyle(currentState == OrderState.Progressing ? activeStyle : inactiveStyle);
            btnReady.setStyle(currentState == OrderState.Ready ? activeStyle : inactiveStyle);
            btnCollected.setStyle(currentState == OrderState.Collected ? activeStyle : inactiveStyle);
        }

        /**
         * Week 6: Changes order state and notifies controller
         */
        private void changeOrderState(OrderState newState) {
            OrderEntry order = getItem();
            if (order != null) {
                try {
                    pickerController.changeOrderState(order.getOrderId(), newState);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
