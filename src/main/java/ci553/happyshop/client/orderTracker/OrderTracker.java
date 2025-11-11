package ci553.happyshop.client.orderTracker;

import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.orderManagement.OrderState;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;
import java.util.TreeMap;

/**
 * OrderTracker class is for tracking orders and their states.
 * Week 6: Redesigned with ListView and color-coded states for better visibility
 * The ordersMap data is received from the OrderHub.
 */

public class OrderTracker {
    private final int WIDTH = UIStyle.trackerWinWidth;
    private final int HEIGHT = UIStyle.trackerWinHeight;

    // TreeMap (orderID,state) holding order IDs and their corresponding states.
    private static final TreeMap<Integer, OrderState> ordersMap = new TreeMap<>();
    private final ListView<OrderEntry> lvOrders; // Week 6: ListView replaces TextArea

    /**
     * Week 6: Constructor initializes the UI with color-coded ListView
     */
    public OrderTracker() {
        Label laTitle = new Label("📦 Order Tracker");
        laTitle.setStyle(UIStyle.labelTitleStyle);

        // Week 6: Initialize ListView with custom cell factory for colored rows
        lvOrders = new ListView<>();
        lvOrders.setPrefHeight(HEIGHT - 60);
        lvOrders.setPrefWidth(WIDTH - 30); // Week 6: Set width to prevent horizontal scrollbar
        lvOrders.setStyle("-fx-fixed-cell-size: 40;"); // Week 6: Reduced cell height (was default ~48px)
        lvOrders.setCellFactory(param -> new ColoredOrderCell());

        // Week 6: Compact layout with minimal padding
        VBox vbox = new VBox(8, laTitle, lvOrders);
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(8, 5, 8, 5)); // Week 6: Reduced padding for compact fit
        vbox.setStyle(UIStyle.rootStyleGray);

        Scene scene = new Scene(vbox, WIDTH, HEIGHT);
        Stage window = new Stage();
        window.setScene(scene);
        window.setTitle("🛒 Order Tracker");

        // Registers the window's position with WinPosManager.
        WinPosManager.registerWindow(window, WIDTH, HEIGHT);
        window.show();
    }

    /**
     * Registers this OrderTracker instance with the OrderHub.
     * This allows the OrderTracker to receive updates on order state changes.
     */
    public void registerWithOrderHub(){
        OrderHub orderHub = OrderHub.getOrderHub();
        orderHub.registerOrderTracker(this);
    }

    /**
     * Sets the order map with new data and refreshes the display.
     * Week 6: Updates ListView with color-coded entries
     */
    public void setOrderMap(TreeMap<Integer, OrderState> om) {
        ordersMap.clear();
        ordersMap.putAll(om);
        displayOrderMap();
    }

    /**
     * Week 6: Displays the current order map in the ListView with colored backgrounds
     * Ensures UI updates happen on JavaFX Application Thread
     */
    private void displayOrderMap() {
        // Week 6: Platform.runLater ensures UI update on JavaFX thread (thread-safe)
        Platform.runLater(() -> {
            lvOrders.getItems().clear();
            for (Map.Entry<Integer, OrderState> entry : ordersMap.entrySet()) {
                lvOrders.getItems().add(new OrderEntry(entry.getKey(), entry.getValue()));
            }
        });
    }

    /**
     * Week 6: Data class to represent an order entry
     */
    private static class OrderEntry {
        private final int orderId;
        private final OrderState state;

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
    }

    /**
     * Week 6: Custom ListCell with color-coded backgrounds based on order state
     * Color scheme:
     * - Ordered: Orange/Yellow (new order, needs attention)
     * - Progressing: Light Blue (being prepared)
     * - Ready: Light Green (ready for pickup)
     * - Collected: Light Gray (completed)
     */
    private static class ColoredOrderCell extends ListCell<OrderEntry> {
        private final HBox cellLayout = new HBox(10);
        private final Label laOrderId = new Label();
        private final Label laState = new Label();

        public ColoredOrderCell() {
            // Week 6: Style labels with compact sizing
            laOrderId.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            laOrderId.setPrefWidth(85); // Week 6: Reduced from 100px

            laState.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
            laState.setPrefWidth(100); // Week 6: Reduced from 120px

            // Week 6: Add spacer for better layout
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            cellLayout.getChildren().addAll(laOrderId, spacer, laState);
            cellLayout.setAlignment(Pos.CENTER_LEFT);
            cellLayout.setPadding(new Insets(5, 8, 5, 8)); // Week 6: Reduced padding (was 8, 10, 8, 10)
            cellLayout.setMaxWidth(Double.MAX_VALUE); // Week 6: Allow full width expansion
        }

        @Override
        protected void updateItem(OrderEntry entry, boolean empty) {
            super.updateItem(entry, empty);

            if (empty || entry == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                laOrderId.setText("Order #" + entry.getOrderId());
                laState.setText(entry.getState().toString());

                // Week 6: Apply color based on order state with compact styling
                String backgroundColor = getBackgroundColor(entry.getState());
                setStyle("-fx-background-color: " + backgroundColor + "; " +
                        "-fx-background-radius: 3px; " +
                        "-fx-border-radius: 3px; " +
                        "-fx-border-color: #CCCCCC; " +
                        "-fx-border-width: 1px; " +
                        "-fx-padding: 0;"); // Week 6: Remove extra cell padding

                setGraphic(cellLayout);
            }
        }

        /**
         * Week 6: Returns background color based on order state
         */
        private String getBackgroundColor(OrderState state) {
            switch (state) {
                case OrderState.Ordered:
                    return "#FFE5B4"; // Peach/Orange - needs attention
                case OrderState.Progressing:
                    return "#AED6F1"; // Light Blue - in progress
                case OrderState.Ready:
                    return "#A9DFBF"; // Light Green - ready
                case OrderState.Collected:
                    return "#D5D8DC"; // Light Gray - completed
                default:
                    return "#FFFFFF"; // White fallback
            }
        }
    }

}
