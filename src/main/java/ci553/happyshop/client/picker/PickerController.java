package ci553.happyshop.client.picker;

import ci553.happyshop.orderManagement.OrderState;

import java.io.IOException;

/**
 * Week 6: Updated controller for new ListView-based picker UI
 * Simplified to handle individual order state changes
 */
public class PickerController {
    public PickerModel pickerModel;

    /**
     * Week 6: Changes state of specific order
     * Replaces the old doProgressing() and doCollected() methods
     */
    public void changeOrderState(int orderId, OrderState newState) throws IOException {
        pickerModel.changeOrderState(orderId, newState);
    }
}
