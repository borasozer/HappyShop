package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;

import java.io.IOException;
import java.sql.SQLException;

public class CustomerController {
    public CustomerModel cusModel;

    public void doAction(String action) throws SQLException, IOException {
        // Week 5: Handle sorting and item-level control actions
        if (action.startsWith("SORT_")) {
            handleSortAction(action);
        } else if (action.startsWith("CHANGE_QTY:") || action.startsWith("SET_QTY:") || action.startsWith("REMOVE_ITEM:")) {
            handleItemAction(action);
        } else {
            handleStandardAction(action);
        }
    }
    
    // Week 5: Separate method for sorting actions
    private void handleSortAction(String action) {
        switch (action) {
            case "SORT_ID":
                cusModel.sortTrolleyById();
                break;
            case "SORT_PRICE":
                cusModel.sortTrolleyByPrice();
                break;
            case "SORT_PRICE_DESC":
                cusModel.sortTrolleyByPriceDescending();
                break;
            case "SORT_NAME":
                cusModel.sortTrolleyByName();
                break;
            case "SORT_TOTAL":
                cusModel.sortTrolleyByTotalValue();
                break;
        }
    }
    
    // Week 5: Separate method for item-level control actions
    private void handleItemAction(String action) {
        String[] parts = action.split(":");
        String command = parts[0];
        String productId = parts[1];
        
        switch (command) {
            case "CHANGE_QTY":
                int delta = Integer.parseInt(parts[2]);
                cusModel.changeQuantity(productId, delta);
                break;
            case "SET_QTY":
                int newQty = Integer.parseInt(parts[2]);
                cusModel.setQuantity(productId, newQty);
                break;
            case "REMOVE_ITEM":
                cusModel.removeItem(productId);
                break;
        }
    }
    
    /**
     * Week 7: Handle product selection from search results list
     */
    public void selectProduct(Product product) {
        cusModel.selectProduct(product);
    }
    
    // Week 5: Original action handling
    private void handleStandardAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Search":
                cusModel.search();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Cancel":
                cusModel.cancel();
                break;
            case "Check Out":
                cusModel.checkOut();
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
        }
    }

}
