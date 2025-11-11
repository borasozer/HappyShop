package ci553.happyshop.client.picker;

import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.orderManagement.OrderState;

import java.io.IOException;
import java.util.TreeMap;

/**
 * PickerModel represents the logic order picker.
 * PickerModel handles two main responsibilities:
 * 1. Observing OrderHub.
 * 2. Notifying PickerView to Updates user interface.
 *
 * 1. Observing OrderHub.
 * PickerModel is an observer of  OrderHub, receiving orderMap from OrderHub.
 * When a picker claims a task, PickerModel:
 * - Retrieves the first unlocked order from the orderMap.
 * - Locks the selected order to prevent other pickers from accessing it.
 * - Notifies OrderHub to update the orderMap, and begin preparation of the order.
 *
 * Once the order is collected by the customer, PickerModel:
 * - Unlocks the order.
 * - Notifies OrderHub to update the orderMap.
 * - Begins the next task if available.
 *
 * All changes in order state are centralized through OrderHub to ensure synchronization.
 * No picker directly changes the display before OrderHub updates the shared orderMap;
 * instead, each PickerModel waits for OrderHub's notification to refresh its state.
 *
 * Imagine the interaction flow:
 * PickerModel: "Hey OrderHub, I found an order that needs to be prepared. Please update the orderMap."
 * OrderHub: "Got it. I'll update the orderMap first."
 * OrderHub (after updating): "Attention all pickers: the orderMap has changed. Please refresh your views."
 *
 * This ensures that all PickerModels stay in sync by only updating their local state
 * in response to centralized changes made by the OrderHub.
 */

public class PickerModel {
    public PickerView pickerView;
    private OrderHub orderHub = OrderHub.getOrderHub();

    // Week 6: Removed displayTa fields - now passing orderMap directly to view

    // TreeMap (orderID,state) holding order IDs and their corresponding states.
    private static TreeMap<Integer, OrderState> orderMap = new TreeMap<>();
    
    // Week 6: Track current order being modified
    private int theOrderId = 0;
    private OrderState theOrderState;

    /**
     * Week 6: Changes order state for specific order ID
     * This replaces the old doProgressing() and doCollected() pattern
     */
    public void changeOrderState(int orderId, OrderState newState) throws IOException {
        // Week 6 debug: Log state change request
        System.out.println("Week 6 Debug: Changing order " + orderId + " to state: " + newState);
        
        theOrderId = orderId;
        theOrderState = newState;
        notifyOrderHub();
        updatePickerView();
    }

    // Week 6: Removed lock mechanism - now using individual state buttons per order

    // Registers this PickerModel instance with the OrderHub
    //so it can receive updates about orderMap changes.
    public void registerWithOrderHub(){
        // Week 6 debug: Log registration attempt
        System.out.println("Week 6 Debug: PickerModel registering with OrderHub...");
        
        OrderHub orderHub = OrderHub.getOrderHub();
        orderHub.registerPickerModel(this);
        
        System.out.println("Week 6 Debug: PickerModel registration complete");
    }

    /**
     * Notifies the OrderHub of a change in the order state.
     * Week 6: Simplified - no longer reads order details here (handled by popup)
     */
    private void notifyOrderHub() throws IOException {
        orderHub.changeOrderStateMoveFile(theOrderId, theOrderState);
    }

    /**
     * Sets the order map with new data and refreshes the display.
     * Week 6: Now passes TreeMap directly to view for ListView rendering
     */
    public void setOrderMap(TreeMap<Integer,OrderState> om) {
        // Week 6 debug: Log received order map update
        System.out.println("Week 6 Debug: PickerModel.setOrderMap() called with " + om.size() + " orders");
        
        orderMap.clear();
        orderMap.putAll(om);
        
        updatePickerView();
    }

    /**
     * Week 6: Updates picker view with current order map
     * Passes map directly instead of building string
     */
    private void updatePickerView() {
        pickerView.update(orderMap);
    }
}
