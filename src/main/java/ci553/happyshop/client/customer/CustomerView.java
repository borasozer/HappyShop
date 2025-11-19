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

    //four controllers needs updating when program going on
    private ImageView ivProduct; //image area in searchPage
    private Label lbProductInfo;//product text info in searchPage
    private ListView<Product> lvSearchResults; // Week 7: List view for multiple search results
    private TextArea taTrolley; //in trolley Page
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

    private VBox createSearchPage() {
        Label laPageTitle = new Label("Search by Product ID/Name");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

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

        VBox vbSearchPage = new VBox(15, laPageTitle, hbId, hbName, hbBtns, hbSearchResult, lvSearchResults);
        vbSearchPage.setPrefWidth(COLUMN_WIDTH);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);
        vbSearchPage.setStyle("-fx-padding: 15px;");

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

        taTrolley = new TextArea();
        taTrolley.setEditable(false);
        taTrolley.setPrefSize(WIDTH/2, HEIGHT-90); // Adjusted for sorting controls

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

        HBox hbBtns = new HBox(10, btnCancel,btnCheckout);
        hbBtns.setStyle("-fx-padding: 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        // Week 5: Added sorting controls to trolley page
        vbTrolleyPage = new VBox(15, laPageTitle, hbSortOptions, taTrolley, hbBtns);
        vbTrolleyPage.setPrefWidth(COLUMN_WIDTH);
        vbTrolleyPage.setAlignment(Pos.TOP_CENTER);
        vbTrolleyPage.setStyle("-fx-padding: 15px;");
        return vbTrolleyPage;
    }

    private VBox createReceiptPage() {
        Label laPageTitle = new Label("Receipt");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setPrefSize(WIDTH/2, HEIGHT-50);

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

        vbReceiptPage = new VBox(15, laPageTitle, taReceipt, btnCloseReceipt);
        vbReceiptPage.setPrefWidth(COLUMN_WIDTH);
        vbReceiptPage.setAlignment(Pos.TOP_CENTER);
        vbReceiptPage.setStyle(UIStyle.rootStyleYellow);
        return vbReceiptPage;
    }


    // Week 5: Replaced buttonClicked() method with individual lambda expressions
    // Each button now has its own lambda, making event handling more explicit and maintainable


    public void update(String imageName, String searchResult, String trolley, String receipt) {

        ivProduct.setImage(new Image(imageName));
        lbProductInfo.setText(searchResult);
        taTrolley.setText(trolley);
        
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
}
