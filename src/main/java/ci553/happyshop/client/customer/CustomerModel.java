package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * You can either directly modify the CustomerModel class to implement the required tasks,
 * or create a subclass of CustomerModel and override specific methods where appropriate.
 */
/**
 * Week 3: Uses DatabaseRW interface for database operations (not concrete implementation)
 * This demonstrates polymorphism and dependency injection, allowing flexible database implementations
 */
public class CustomerModel {
    public CustomerView cusView;
    // Week 3: Interface reference for loose coupling and polymorphic behavior
    public DatabaseRW databaseRW; //Interface type, not specific implementation
                                  //Benefits: Flexibility: Easily change the database implementation.
    public RemoveProductNotifier removeProductNotifier; // Week 3: Notifier for stock shortage alerts

    private Product theProduct =null; // product found from search
    private ArrayList<Product> trolley =  new ArrayList<>(); // a list of products in trolley

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "No Product was searched yet"; // Label showing search result message (Search Page)
    private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";                                // Text area content showing receipt after checkout (Receipt Page)

    //SELECT productID, description, image, unitPrice,inStock quantity
    void search() throws SQLException {
        String productId = cusView.tfId.getText().trim();
        if(!productId.isEmpty()){
            theProduct = databaseRW.searchByProductId(productId); //search database
            if(theProduct != null && theProduct.getStockQuantity()>0){
                double unitPrice = theProduct.getUnitPrice();
                String description = theProduct.getProductDescription();
                int stock = theProduct.getStockQuantity();

                String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", productId, description, unitPrice);
                String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                displayLaSearchResult = baseInfo + quantityInfo;
                System.out.println(displayLaSearchResult);
            }
            else{
                theProduct=null;
                displayLaSearchResult = "No Product was found with ID " + productId;
                System.out.println("No Product was found with ID " + productId);
            }
        }else{
            theProduct=null;
            displayLaSearchResult = "Please type ProductID";
            System.out.println("Please type ProductID.");
        }
        updateView();
    }

    void addToTrolley(){
        if(theProduct!= null){
            // Week 2: Check if product already exists in trolley to merge quantities
            boolean productFound = false;
            for (Product existingProduct : trolley) {
                // Week 2: If same product ID found, increment quantity instead of adding duplicate
                if (existingProduct.getProductId().equals(theProduct.getProductId())) {
                    existingProduct.setOrderedQuantity(existingProduct.getOrderedQuantity() + 1);
                    productFound = true;
                    break;
                }
            }
            
            // Week 2: Add new product only if not already in trolley
            if (!productFound) {
                // Week 2: Create a copy to avoid reference issues when theProduct is reused
                Product productCopy = new Product(
                    theProduct.getProductId(),
                    theProduct.getProductDescription(),
                    theProduct.getProductImageName(),
                    theProduct.getUnitPrice(),
                    theProduct.getStockQuantity()
                );
                trolley.add(productCopy);
            }
            
            // Week 2: Sort trolley by product ID to maintain organized display
            Collections.sort(trolley);
            
            displayTaTrolley = ProductListFormatter.buildString(trolley); //build a String for trolley so that we can show it
        }
        else{
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");
        }
        displayTaReceipt=""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    void checkOut() throws IOException, SQLException {
        if(!trolley.isEmpty()){
            // Group the products in the trolley by productId to optimize stock checking
            // Check the database for sufficient stock for all products in the trolley.
            // If any products are insufficient, the update will be rolled back.
            // If all products are sufficient, the database will be updated, and insufficientProducts will be empty.
            // Note: If the trolley is already organized (merged and sorted), grouping is unnecessary.
            ArrayList<Product> groupedTrolley= groupProductsById(trolley);
            ArrayList<Product> insufficientProducts= databaseRW.purchaseStocks(groupedTrolley);

            if(insufficientProducts.isEmpty()){ // If stock is sufficient for all products
                //get OrderHub and tell it to make a new Order
                OrderHub orderHub =OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(trolley);
                trolley.clear();
                displayTaTrolley ="";
                displayTaReceipt = String.format(
                        "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                        theOrder.getOrderId(),
                        theOrder.getOrderedDateTime(),
                        ProductListFormatter.buildString(theOrder.getProductList())
                );
                
                // Week 3: Close notifier window on successful checkout
                removeProductNotifier.closeNotifierWindow();
                
                System.out.println(displayTaReceipt);
            }
            else{ // Some products have insufficient stock — build an error message to inform the customer
                StringBuilder errorMsg = new StringBuilder();
                for(Product p : insufficientProducts){
                    errorMsg.append("\u2022 "+ p.getProductId()).append(", ")
                            .append(p.getProductDescription()).append(" (Only ")
                            .append(p.getStockQuantity()).append(" available, ")
                            .append(p.getOrderedQuantity()).append(" requested)\n");
                }
                theProduct=null;

                // Week 3: Remove products with insufficient stock from trolley by product ID
                // Cannot use removeAll() as Product objects have different references and stock quantities
                for(Product insufficientProd : insufficientProducts){
                    trolley.removeIf(trolleyProd -> trolleyProd.getProductId().equals(insufficientProd.getProductId()));
                }
                
                // Week 3: Update trolley display after removal
                displayTaTrolley = trolley.isEmpty() ? "" : ProductListFormatter.buildString(trolley);
                
                // Week 3: Show notification window with removed products information
                removeProductNotifier.showRemovalMsg(errorMsg.toString());
                
                System.out.println("Insufficient stock: products removed from trolley");
            }
        }
        else{
            displayTaTrolley = "Your trolley is empty";
            System.out.println("Your trolley is empty");
        }
        updateView();
    }

    /**
     * Groups products by their productId to optimize database queries and updates.
     * By grouping products, we can check the stock for a given `productId` once, rather than repeatedly
     */
    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                // Make a shallow copy to avoid modifying the original
                Product newProduct = new Product(p.getProductId(), p.getProductDescription(),
                        p.getProductImageName(), p.getUnitPrice(), p.getStockQuantity());
                // Week 3: Set ordered quantity from original to preserve accurate count
                newProduct.setOrderedQuantity(p.getOrderedQuantity());
                grouped.put(id, newProduct);
            }
        }
        return new ArrayList<>(grouped.values());
    }

    void cancel(){
        trolley.clear();
        displayTaTrolley="";
        theProduct = null; // Week 2: Clear current product reference to prevent stale data
        displayLaSearchResult = "Please type ProductID"; // Week 2: Clear search result display
        
        // Week 3: Close notifier window when trolley is cancelled
        removeProductNotifier.closeNotifierWindow();
        
        updateView();
    }
    
    // Week 5: Multiple sorting methods demonstrating lambda expressions and method references
    // Reference: Week 5 Slides, Slide 31 - "Different ways to sort a List of objects"
    
    /**
     * Sorts trolley by product ID (ascending) using method reference.
     * This is the default/natural ordering already defined in Product.compareTo()
     * 
     * Week 5: Method reference - cleanest approach for simple sorting
     */
    void sortTrolleyById() {
        // Week 5: Method reference - cleanest syntax when getter exists
        // Equivalent lambda: (p1, p2) -> p1.getProductId().compareTo(p2.getProductId())
        trolley.sort(Comparator.comparing(Product::getProductId));
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        updateView();
    }
    
    /**
     * Sorts trolley by product price (ascending) using method reference.
     * 
     * Week 5: Comparator.comparingDouble for numeric sorting
     */
    void sortTrolleyByPrice() {
        // Week 5: Method reference with comparingDouble for primitive optimization
        // Equivalent lambda: (p1, p2) -> Double.compare(p1.getUnitPrice(), p2.getUnitPrice())
        trolley.sort(Comparator.comparingDouble(Product::getUnitPrice));
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        updateView();
    }
    
    /**
     * Sorts trolley by product description (alphabetically) using method reference.
     * 
     * Week 5: String comparison with natural ordering
     */
    void sortTrolleyByName() {
        // Week 5: Method reference for String comparison
        // Equivalent lambda: (p1, p2) -> p1.getProductDescription().compareTo(p2.getProductDescription())
        trolley.sort(Comparator.comparing(Product::getProductDescription));
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        updateView();
    }
    
    /**
     * Sorts trolley by price descending using lambda expression.
     * Demonstrates lambda syntax when reversed() or custom logic is needed.
     * 
     * Week 5: Lambda expressions for custom comparison logic
     */
    void sortTrolleyByPriceDescending() {
        // Week 5: Lambda expression - more explicit than method reference
        // Shows descending order by reversing comparison order
        trolley.sort((p1, p2) -> Double.compare(p2.getUnitPrice(), p1.getUnitPrice()));
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        updateView();
    }
    
    /**
     * Sorts trolley by total price (unitPrice * quantity) descending.
     * Demonstrates lambda expression when calculation is needed.
     * 
     * Week 5: Lambda expressions for complex comparison logic
     */
    void sortTrolleyByTotalValue() {
        // Week 5: Lambda expression with calculation - method reference not suitable here
        // Multi-line lambda body for complex logic
        trolley.sort((p1, p2) -> {
            double total1 = p1.getUnitPrice() * p1.getOrderedQuantity();
            double total2 = p2.getUnitPrice() * p2.getOrderedQuantity();
            return Double.compare(total2, total1); // Descending order
        });
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        updateView();
    }
    
    /**
     * Alternative sorting using anonymous class instead of lambda.
     * Demonstrates traditional anonymous class approach for comparison.
     * 
     * Week 5: Anonymous class implementing Comparator interface
     * Compare this with lambda version above to see syntax difference
     */
    void sortTrolleyByTotalValueAnonymous() {
        // Week 5: Anonymous class - verbose but explicit implementation
        // Equivalent to lambda: (p1, p2) -> { ... }
        trolley.sort(new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                double total1 = p1.getUnitPrice() * p1.getOrderedQuantity();
                double total2 = p2.getUnitPrice() * p2.getOrderedQuantity();
                return Double.compare(total2, total1); // Descending order
            }
        });
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        updateView();
    }
    
    void closeReceipt(){
        displayTaReceipt="";
    }

    void updateView() {
        if(theProduct != null){
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder +imageName; //relative file path, eg images/0001.jpg
            // Get the full absolute path to the image
            Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
            imageName = imageFullPath.toUri().toString(); //get the image full Uri then convert to String
            System.out.println("Image absolute path: " + imageFullPath); // Debugging to ensure path is correct
        }
        else{
            imageName = "imageHolder.jpg";
        }
        cusView.update(imageName, displayLaSearchResult, displayTaTrolley,displayTaReceipt);
    }
     // extra notes:
     //Path.toUri(): Converts a Path object (a file or a directory path) to a URI object.
     //File.toURI(): Converts a File object (a file on the filesystem) to a URI object
    
    // Week 5: Item-level control methods supporting custom ListCell
    // Reference: Week 5 Lab Activities - Item-level Control extension
    
    /**
     * Changes quantity of a specific product by delta amount
     * Week 5: Uses Stream API with lambda expressions for functional approach
     */
    void changeQuantity(String productId, int delta) {
        // Week 5: Lambda expression with filter to find product
        // stream() returns Stream<Product>, filter() takes Predicate<Product> (functional interface)
        trolley.stream()
               .filter(p -> p.getProductId().equals(productId)) // Lambda implementing Predicate
               .findFirst()
               .ifPresent(product -> { // Lambda implementing Consumer
                   int newQty = product.getOrderedQuantity() + delta;
                   if (newQty > 0) {
                       product.setOrderedQuantity(newQty);
                       displayTaTrolley = ProductListFormatter.buildString(trolley);
                       updateView();
                   } else if (newQty == 0) {
                       removeItem(productId);
                   }
               });
    }
    
    /**
     * Sets quantity of a specific product to exact value
     * Week 5: Stream API with lambda for finding and updating
     */
    void setQuantity(String productId, int newQty) {
        // Week 5: Stream API with lambda for finding and updating
        trolley.stream()
               .filter(p -> p.getProductId().equals(productId))
               .findFirst()
               .ifPresent(product -> {
                   if (newQty > 0) {
                       product.setOrderedQuantity(newQty);
                       displayTaTrolley = ProductListFormatter.buildString(trolley);
                       updateView();
                   }
               });
    }
    
    /**
     * Removes a specific product from trolley
     * Week 5: removeIf with lambda expression (functional approach)
     */
    void removeItem(String productId) {
        // Week 5: removeIf with lambda expression
        // removeIf() takes Predicate<Product> (functional interface)
        trolley.removeIf(p -> p.getProductId().equals(productId));
        displayTaTrolley = ProductListFormatter.buildString(trolley);
        updateView();
    }

    //for test only
    public ArrayList<Product> getTrolley() {
        return trolley;
    }
}
