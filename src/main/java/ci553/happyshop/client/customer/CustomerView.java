package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import ci553.happyshop.utility.WindowBounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The CustomerView is separated into two sections by a line :
 *
 * 1. Search Page – Always visible, allowing customers to browse and search for products.
 * 2. the second page – display either the Trolley Page or the Receipt Page
 *    depending on the current context. Only one of these is shown at a time.
 * 
 * Week 5 Enhancements:
 * - All button event handlers now use lambda expressions instead of single method reference
 * - Added ComboBox for trolley sorting with lambda event handler
 * - Lambda expressions demonstrate modern JavaFX event handling patterns
 */

public class CustomerView  {
    public CustomerController cusController;

    private final int WIDTH = UIStyle.customerWinWidth;
    private final int HEIGHT = UIStyle.customerWinHeight;
    private final int COLUMN_WIDTH = WIDTH / 2 - 10;

    private HBox hbRoot; // Top-level layout manager
    private VBox vbTrolleyPage;  //vbTrolleyPage and vbReceiptPage will swap with each other when need
    private VBox vbReceiptPage;

    TextField tfId; //for user input on the search page. Made accessible so it can be accessed or modified by CustomerModel
    TextField tfName; //for user input on the search page. Made accessible so it can be accessed by CustomerModel
    TextField tfQuantity; // Week 11: Quantity input field for adding products to trolley

    //four controllers needs updating when program going on
    private ImageView ivProduct; //image area in searchPage
    private Label lbProductInfo;//product text info in searchPage
    private ListView<Product> lvSearchResults; // Week 7: List view for multiple search results
    private ListView<Product> lvTrolley; // Week 11: Interactive trolley with item-level controls
    private Label lbTrolleyTotal; // Week 11: Total price label for trolley
    private TextArea taReceipt;//in receipt page

    // Holds a reference to this CustomerView window for future access and management
    // (e.g., positioning the removeProductNotifier when needed).
    private Stage viewWindow;

    public void start(Stage window) {
        VBox vbSearchPage = createSearchPage();
        vbTrolleyPage = CreateTrolleyPage();
        vbReceiptPage = createReceiptPage();

        // Create a divider line
        Line line = new Line(0, 0, 0, HEIGHT);
        line.setStrokeWidth(4);
        line.setStroke(Color.PINK);
        VBox lineContainer = new VBox(line);
        lineContainer.setPrefWidth(4); // Give it some space
        lineContainer.setAlignment(Pos.CENTER);

        hbRoot = new HBox(10, vbSearchPage, lineContainer, vbTrolleyPage); //initialize to show trolleyPage
        hbRoot.setAlignment(Pos.CENTER);
        hbRoot.setStyle(UIStyle.rootStyle);

        Scene scene = new Scene(hbRoot, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("🛒 HappyShop Customer Client");
        WinPosManager.registerWindow(window,WIDTH,HEIGHT); //calculate position x and y for this window
        window.show();
        viewWindow=window;// Sets viewWindow to this window for future reference and management.
    }

    // Week 10: Customer type selection for different business rules and benefits
    private ComboBox<String> customerTypeCombo;
    
    private VBox createSearchPage() {
        Label laPageTitle = new Label("Search by Product ID/Name");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        // Week 10: Customer type selection (Standard, VIP, Prime)
        Label laCustomerType = new Label("Customer Type:");
        laCustomerType.setStyle(UIStyle.labelStyle);
        customerTypeCombo = new ComboBox<>();
        customerTypeCombo.getItems().addAll("Standard", "VIP", "Prime");
        customerTypeCombo.setValue("Standard"); // Week 10: Default to Standard
        customerTypeCombo.setStyle(UIStyle.textFiledStyle);
        // Week 10: Lambda expression to update customer type in model
        customerTypeCombo.setOnAction(event -> {
            String selectedType = customerTypeCombo.getValue();
            if (selectedType != null) {
                try {
                    cusController.doAction("CHANGE_CUSTOMER_TYPE:" + selectedType);
                    
                    // Week 10: Show information dialog about customer type benefits
                    CustomerTypeInfoDialog infoDialog = new CustomerTypeInfoDialog();
                    infoDialog.show(selectedType);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        HBox hbCustomerType = new HBox(10, laCustomerType, customerTypeCombo);

        Label laId = new Label("Search:");
        laId.setStyle(UIStyle.labelStyle);
        tfId = new TextField();
        tfId.setPromptText("ID (eg. 0001) or Name (eg. TV)"); // Week 7: Updated prompt for flexible search
        tfId.setStyle(UIStyle.textFiledStyle);
        HBox hbId = new HBox(10, laId, tfId);

        // Week 7: Name field hidden as unified search now accepts both ID and name in single field
        Label laName = new Label("Name:");
        laName.setStyle(UIStyle.labelStyle);
        tfName = new TextField();
        tfName.setPromptText("implement it if you want");
        tfName.setStyle(UIStyle.textFiledStyle);
        HBox hbName = new HBox(10, laName, tfName);
        hbName.setVisible(false); // Week 7: Hidden - unified search field replaces separate name field
        hbName.setManaged(false); // Week 7: Remove from layout calculations

        // Week 11: Item-level control - Quantity input with +/- buttons
        Label laQuantity = new Label("Quantity:");
        laQuantity.setStyle(UIStyle.labelStyle);
        
        Button btnDecrease = new Button("-");
        btnDecrease.setStyle(UIStyle.buttonStyle);
        btnDecrease.setPrefWidth(30);
        btnDecrease.setOnAction(event -> {
            try {
                int currentQty = Integer.parseInt(tfQuantity.getText());
                if (currentQty > 1) {
                    tfQuantity.setText(String.valueOf(currentQty - 1));
                }
            } catch (NumberFormatException e) {
                tfQuantity.setText("1");
            }
        });
        
        tfQuantity = new TextField("1");
        tfQuantity.setStyle(UIStyle.textFiledStyle);
        tfQuantity.setPrefWidth(50);
        // Week 11: Validate numeric input only - allow empty temporarily
        tfQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfQuantity.setText(oldValue);
            }
        });
        // Week 11: Validate on focus lost - ensure valid quantity when user leaves field
        tfQuantity.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Focus lost
                String text = tfQuantity.getText().trim();
                if (text.isEmpty() || text.equals("0")) {
                    tfQuantity.setText("1");
                }
            }
        });
        
        Button btnIncrease = new Button("+");
        btnIncrease.setStyle(UIStyle.buttonStyle);
        btnIncrease.setPrefWidth(30);
        btnIncrease.setOnAction(event -> {
            try {
                int currentQty = Integer.parseInt(tfQuantity.getText());
                tfQuantity.setText(String.valueOf(currentQty + 1));
            } catch (NumberFormatException e) {
                tfQuantity.setText("1");
            }
        });
        
        HBox hbQuantity = new HBox(5, laQuantity, btnDecrease, tfQuantity, btnIncrease);
        hbQuantity.setAlignment(Pos.CENTER_LEFT);
        
        Label laPlaceHolder = new Label(  " ".repeat(15)); //create left-side spacing so that this HBox aligns with others in the layout.
        Button btnSearch = new Button("Search");
        btnSearch.setStyle(UIStyle.buttonStyle);
        // Week 5: Lambda expression for event handling - cleaner than method reference for simple actions
        btnSearch.setOnAction(event -> {
            try {
                cusController.doAction("Search");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        
        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);
        // Week 5: Lambda expression ensures trolley page is shown before adding
        btnAddToTrolley.setOnAction(event -> {
            try {
                showTrolleyOrReceiptPage(vbTrolleyPage); // Ensure trolley page shows
                cusController.doAction("Add to Trolley");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        HBox hbBtns = new HBox(10, laPlaceHolder,btnSearch, btnAddToTrolley);

        ivProduct = new ImageView("imageHolder.jpg");
        ivProduct.setFitHeight(60);
        ivProduct.setFitWidth(60);
        ivProduct.setPreserveRatio(true); // Image keeps its original shape and fits inside 60×60
        ivProduct.setSmooth(true); //make it smooth and nice-looking

        lbProductInfo = new Label("Thank you for shopping with us.");
        lbProductInfo.setWrapText(true);
        lbProductInfo.setMinHeight(Label.USE_PREF_SIZE);  // Allow auto-resize
        lbProductInfo.setStyle(UIStyle.labelMulLineStyle);
        HBox hbSearchResult = new HBox(5, ivProduct, lbProductInfo);
        hbSearchResult.setAlignment(Pos.CENTER_LEFT);

        // Week 7: ListView for displaying multiple search results
        lvSearchResults = new ListView<>();
        lvSearchResults.setPrefHeight(80);
        lvSearchResults.setVisible(false); // Week 7: Hidden by default, shown when multiple results
        lvSearchResults.setStyle("-fx-font-size: 11px;");
        // Week 7: Lambda expression for handling product selection from list
        lvSearchResults.setOnMouseClicked(event -> {
            Product selectedProduct = lvSearchResults.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                cusController.selectProduct(selectedProduct);
            }
        });
        // Week 7: Custom cell factory for compact product display
        lvSearchResults.setCellFactory(param -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setText(null);
                } else {
                    // Week 7: Display product ID and description in list
                    setText(product.getProductId() + " - " + product.getProductDescription());
                }
            }
        });

        VBox vbSearchPage = new VBox(5, laPageTitle, hbCustomerType, hbId, hbName, hbQuantity, hbBtns, hbSearchResult, lvSearchResults); // Week 10: Added customer type selection, Week 11: Added quantity controls and reduced spacing for compact layout
        vbSearchPage.setPrefWidth(COLUMN_WIDTH);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);
        vbSearchPage.setStyle("-fx-padding: 5px;"); // Week 11: Reduced padding for compact layout

        return vbSearchPage;
    }

    private VBox CreateTrolleyPage() {
        Label laPageTitle = new Label("🛒🛒  Trolley 🛒🛒");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);
        
        // Week 5: Add sorting options UI
        // Reference: Week 5 - Anonymous Classes & Lambda Expressions in event handling
        Label laSortLabel = new Label("Sort:");
        laSortLabel.setStyle(UIStyle.labelStyle);
        
        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll(
            "Sort by ID", 
            "Sort by Price (Low to High)", 
            "Sort by Price (High to Low)",
            "Sort by Name",
            "Sort by Total Value"
        );
        sortOptions.setPromptText("Sort Options");
        sortOptions.setStyle(UIStyle.comboBoxStyle);
        
        // Week 5: Anonymous class example - ChangeListener for debugging/logging
        // Demonstrates traditional anonymous class approach before lambdas
        sortOptions.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, 
                              String oldValue, String newValue) {
                // Week 5: Anonymous class with explicit override - more verbose than lambda
                System.out.println("Sort option changed from '" + oldValue + "' to '" + newValue + "'");
            }
        });
        
        // Week 5: Lambda expression for event handling (modern JavaFX style)
        // Lambda implements EventHandler<ActionEvent> functional interface
        // Compare: Lambda vs Anonymous class above - lambda is more concise
        // Equivalent to: new EventHandler<ActionEvent>() { public void handle(ActionEvent event) {...} }
        sortOptions.setOnAction(event -> {
            String selected = sortOptions.getValue();
            if (selected != null) {
                try {
                    // Week 5: Switch expression (modern Java feature)
                    switch (selected) {
                        case "Sort by ID" -> cusController.doAction("SORT_ID");
                        case "Sort by Price (Low to High)" -> cusController.doAction("SORT_PRICE");
                        case "Sort by Price (High to Low)" -> cusController.doAction("SORT_PRICE_DESC");
                        case "Sort by Name" -> cusController.doAction("SORT_NAME");
                        case "Sort by Total Value" -> cusController.doAction("SORT_TOTAL");
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        HBox hbSortOptions = new HBox(10, laSortLabel, sortOptions);
        hbSortOptions.setAlignment(Pos.CENTER_LEFT);
        hbSortOptions.setStyle("-fx-padding: 5px;");

        // Week 11: Interactive ListView for trolley with custom cell factory
        lvTrolley = new ListView<>();
        lvTrolley.setPrefSize(WIDTH/2, HEIGHT-150);
        lvTrolley.setStyle("-fx-font-size: 11px;");
        lvTrolley.setCellFactory(param -> new TrolleyItemCell());
        
        // Week 11: Total price label
        lbTrolleyTotal = new Label("Total: £0.00");
        lbTrolleyTotal.setStyle(UIStyle.labelStyle + "-fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle(UIStyle.buttonStyle);
        // Week 5: Lambda expression for cancel action
        btnCancel.setOnAction(event -> {
            try {
                cusController.doAction("Cancel");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        Button btnCheckout = new Button("Check Out");
        btnCheckout.setStyle(UIStyle.buttonStyle);
        // Week 5: Lambda expression for checkout action
        btnCheckout.setOnAction(event -> {
            try {
                cusController.doAction("Check Out");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        HBox hbBtns = new HBox(10, btnCancel, btnCheckout);
        hbBtns.setStyle("-fx-padding: 2px;"); // Week 11: Reduced padding for compact layout
        hbBtns.setAlignment(Pos.CENTER);

        // Week 5: Added sorting controls to trolley page, Week 11: Added interactive ListView
        vbTrolleyPage = new VBox(5, laPageTitle, hbSortOptions, lvTrolley, lbTrolleyTotal, hbBtns);
        vbTrolleyPage.setPrefWidth(COLUMN_WIDTH);
        vbTrolleyPage.setAlignment(Pos.TOP_CENTER);
        vbTrolleyPage.setStyle("-fx-padding: 5px;"); // Week 11: Reduced padding for compact layout
        return vbTrolleyPage;
    }

    private VBox createReceiptPage() {
        Label laPageTitle = new Label("Receipt");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setPrefSize(WIDTH/2, HEIGHT-80); // Week 11: Reduced height for compact layout (240px instead of 270px)

        Button btnCloseReceipt = new Button("OK & Close"); //btn for closing receipt and showing trolley page
        btnCloseReceipt.setStyle(UIStyle.buttonStyle);
        // Week 5: Lambda expression for closing receipt and returning to trolley
        btnCloseReceipt.setOnAction(event -> {
            try {
                showTrolleyOrReceiptPage(vbTrolleyPage);
                cusController.doAction("OK & Close");
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        vbReceiptPage = new VBox(10, laPageTitle, taReceipt, btnCloseReceipt); // Week 11: Reduced spacing for compact layout
        vbReceiptPage.setPrefWidth(COLUMN_WIDTH);
        vbReceiptPage.setAlignment(Pos.TOP_CENTER);
        vbReceiptPage.setStyle(UIStyle.rootStyleYellow);
        return vbReceiptPage;
    }


    // Week 5: Replaced buttonClicked() method with individual lambda expressions
    // Each button now has its own lambda, making event handling more explicit and maintainable


    public void update(String imageName, String searchResult, ArrayList<Product> trolleyProducts, String receipt) {

        ivProduct.setImage(new Image(imageName));
        lbProductInfo.setText(searchResult);
        
        // Week 11: Update trolley ListView with products
        lvTrolley.getItems().clear();
        if (trolleyProducts != null && !trolleyProducts.isEmpty()) {
            lvTrolley.getItems().addAll(trolleyProducts);
            // Calculate and display total
            double total = trolleyProducts.stream()
                .mapToDouble(p -> p.getUnitPrice() * p.getOrderedQuantity())
                .sum();
            
            // Week 10: Apply Prime discount if applicable
            String customerType = cusController.cusModel.getCustomerType();
            if ("Prime".equals(customerType)) {
                double discountedTotal = total * 0.9;
                lbTrolleyTotal.setText(String.format("Total: £%.2f (Prime -10%% = £%.2f)", total, discountedTotal));
            } else {
                lbTrolleyTotal.setText(String.format("Total: £%.2f", total));
            }
        } else {
            lbTrolleyTotal.setText("Total: £0.00");
        }
        
        // Week 7: Update search results list visibility based on number of results
        updateSearchResults();
        
        if (!receipt.equals("")) {
            showTrolleyOrReceiptPage(vbReceiptPage);
            taReceipt.setText(receipt);
        }
    }

    /**
     * Week 7: Updates search results ListView based on search outcome
     * Shows ListView for multiple results, hides it for single or no results
     */
    private void updateSearchResults() {
        if (cusController.cusModel.getSearchResults().size() > 1) {
            // Week 7: Multiple results - show ListView
            lvSearchResults.getItems().clear();
            lvSearchResults.getItems().addAll(cusController.cusModel.getSearchResults());
            lvSearchResults.setVisible(true);
            lbProductInfo.setVisible(true); // Week 7: Keep label visible for instructions
        } else {
            // Week 7: Single or no results - hide ListView
            lvSearchResults.setVisible(false);
            lbProductInfo.setVisible(true);
        }
    }

    // Replaces the last child of hbRoot with the specified page.
    // the last child is either vbTrolleyPage or vbReceiptPage.
    private void showTrolleyOrReceiptPage(Node pageToShow) {
        int lastIndex = hbRoot.getChildren().size() - 1;
        if (lastIndex >= 0) {
            hbRoot.getChildren().set(lastIndex, pageToShow);
        }
    }

    WindowBounds getWindowBounds() {
        return new WindowBounds(viewWindow.getX(), viewWindow.getY(),
                  viewWindow.getWidth(), viewWindow.getHeight());
    }

    // Week 10: Getter for selected customer type
    public String getSelectedCustomerType() {
        return customerTypeCombo.getValue();
    }
    
    // Week 11: Getter for selected quantity
    public int getSelectedQuantity() {
        try {
            String text = tfQuantity.getText().trim();
            if (text.isEmpty()) {
                tfQuantity.setText("1");
                return 1;
            }
            int qty = Integer.parseInt(text);
            if (qty <= 0) {
                tfQuantity.setText("1");
                return 1;
            }
            return qty;
        } catch (NumberFormatException e) {
            tfQuantity.setText("1");
            return 1;
        }
    }
    
    // Week 11: Reset quantity to default after adding to trolley
    public void resetQuantity() {
        tfQuantity.setText("1");
    }
    
    /**
     * Week 11: Custom ListCell for interactive trolley items
     * Each item displays product info, quantity selector, and remove button
     */
    private class TrolleyItemCell extends ListCell<Product> {
        private HBox hbCell;
        private Label lbProductInfo;
        private ComboBox<Integer> cbQuantity;
        private Button btnRemove;
        
        public TrolleyItemCell() {
            super();
            
            // Product info label
            lbProductInfo = new Label();
            lbProductInfo.setStyle("-fx-font-size: 11px;");
            lbProductInfo.setPrefWidth(150); // Week 11: Reduced from 240 to fit all controls on screen
            lbProductInfo.setMaxWidth(150);
            
            // Quantity selector (1-50)
            cbQuantity = new ComboBox<>();
            for (int i = 1; i <= 50; i++) {
                cbQuantity.getItems().add(i);
            }
            cbQuantity.setStyle("-fx-font-size: 11px;");
            cbQuantity.setPrefWidth(55); // Week 11: Slightly smaller for compact layout
            
            // Remove button
            btnRemove = new Button("✖");
            btnRemove.setStyle("-fx-font-size: 12px; -fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
            btnRemove.setPrefWidth(25); // Week 11: Slightly smaller for compact layout
            
            hbCell = new HBox(3, lbProductInfo, cbQuantity, btnRemove); // Week 11: Reduced spacing from 5 to 3
            hbCell.setAlignment(Pos.CENTER_LEFT);
        }
        
        @Override
        protected void updateItem(Product product, boolean empty) {
            super.updateItem(product, empty);
            
            if (empty || product == null) {
                setGraphic(null);
                setText(null);
            } else {
                // Format product info - Week 11: Compact format for narrow client windows
                String description = product.getProductDescription();
                // Week 11: Truncate to 12 chars for compact display
                if (description.length() > 12) {
                    description = description.substring(0, 12) + "..";
                }
                String info = String.format("%s %s £%.2f", 
                    product.getProductId(),
                    description,
                    product.getUnitPrice());
                lbProductInfo.setText(info);
                
                // Set quantity selector
                cbQuantity.setValue(product.getOrderedQuantity());
                
                // Quantity change handler
                cbQuantity.setOnAction(event -> {
                    Integer newQty = cbQuantity.getValue();
                    if (newQty != null && newQty != product.getOrderedQuantity()) {
                        try {
                            cusController.doAction("SET_QTY:" + product.getProductId() + ":" + newQty);
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                
                // Remove button handler
                btnRemove.setOnAction(event -> {
                    try {
                        cusController.doAction("REMOVE_ITEM:" + product.getProductId());
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                    }
                });
                
                setGraphic(hbCell);
                setText(null);
            }
        }
    }
}
