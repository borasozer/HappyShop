package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.catalogue.MinimumPaymentException;
import ci553.happyshop.catalogue.ExcessiveOrderQuantityException;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
    private ArrayList<Product> searchResults = new ArrayList<>(); // Week 7: Multiple search results for flexible search

    // Week 10: Customer type for different business rules (Standard, VIP, Prime)
    private String customerType = "Standard";

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "Please type Product ID or Name"; // Week 7: Updated prompt for flexible search
    private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";                                // Text area content showing receipt after checkout (Receipt Page)

    /**
     * Week 10: Constructor Injection (DIP - Dependency Inversion Principle)
     * Dependencies are explicitly passed through constructor, making them clear and testable.
     * 
     * Benefits:
     * - Explicit dependencies: All required dependencies visible in constructor signature
     * - Immutability: Dependencies set once at construction time
     * - Testability: Easy to inject mock objects for unit testing
     * - Loose coupling: Depends on DatabaseRW interface, not concrete implementation
     * 
     * @param databaseRW Database access interface (not concrete implementation)
     * @param notifier Notification system for stock shortage alerts
     */
    public CustomerModel(DatabaseRW databaseRW, RemoveProductNotifier notifier) {
        this.databaseRW = databaseRW;
        this.removeProductNotifier = notifier;
        System.out.println("Week 10: CustomerModel created with constructor injection (DIP)");
    }

    /**
     * Week 10: Default constructor for backward compatibility
     * Allows existing code to continue working while demonstrating constructor injection
     */
    public CustomerModel() {
        System.out.println("Week 10: CustomerModel created with default constructor (setter injection will be used)");
    }

    /**
     * Week 7: Flexible search - accepts both Product ID and Product Name
     * Uses DatabaseRW.searchProduct() which searches by ID first, then by name if not found
     */
    void search() throws SQLException {
        String keyword = cusView.tfId.getText().trim();
        if(!keyword.isEmpty()){
            // Week 7: Search by ID or name using unified search method
            searchResults = databaseRW.searchProduct(keyword);
            
            if(!searchResults.isEmpty()){ 
                // Week 7: If multiple results found, show list; if single result, show details
                if(searchResults.size() == 1){
                    theProduct = searchResults.get(0);
                    if(theProduct.getStockQuantity() > 0){
                        double unitPrice = theProduct.getUnitPrice();
                        String description = theProduct.getProductDescription();
                        int stock = theProduct.getStockQuantity();
                        String productId = theProduct.getProductId(); // Week 7: Use actual product ID, not search keyword

                        String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", productId, description, unitPrice);
                        String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                        displayLaSearchResult = baseInfo + quantityInfo;
                        System.out.println("Product " + productId + " found (searched by: " + keyword + ")");
                    } else {
                        theProduct = null;
                        displayLaSearchResult = "Product found but out of stock";
                    }
                } else {
                    // Week 7: Multiple results - user will select from list
                    theProduct = null;
                    displayLaSearchResult = searchResults.size() + " products found. Select one to view details.";
                    System.out.println(searchResults.size() + " products found for keyword: " + keyword);
                }
            }else{
                // Week 7: No results found
                theProduct = null;
                searchResults.clear();
                displayLaSearchResult = "No product found with ID or Name: " + keyword;
                System.out.println("No product found for keyword: " + keyword);
            }
        }else{
            theProduct = null;
            searchResults.clear();
            displayLaSearchResult = "Please type Product ID or Name";
            System.out.println("Please type Product ID or Name.");
        }
        updateView();
    }

    /**
     * Week 7: Getter for search results to display in ListView
     */
    public ArrayList<Product> getSearchResults() {
        return searchResults;
    }

    /**
     * Week 7: Selects a product from search results and displays its details
     * Called when user clicks on a product in the search results list
     */
    void selectProduct(Product selectedProduct) {
        if(selectedProduct != null){
            theProduct = selectedProduct;
            if(theProduct.getStockQuantity() > 0){
                double unitPrice = theProduct.getUnitPrice();
                String description = theProduct.getProductDescription();
                int stock = theProduct.getStockQuantity();
                String productId = theProduct.getProductId();

                String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", productId, description, unitPrice);
                String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                displayLaSearchResult = baseInfo + quantityInfo;
                System.out.println("Product " + productId + " selected.");
            } else {
                theProduct = null;
                displayLaSearchResult = "Product found but out of stock";
            }
            updateView();
        }
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

    /**
     * Week 6: Exception propagation through call stack demonstration
     * 
     * Call stack for checkout process:
     * 1. CustomerView (button click) 
     * 2. → CustomerController.doAction("Check Out")
     * 3. → CustomerModel.checkOut() ← YOU ARE HERE
     * 4. → validateTrolley() ← throws exceptions
     * 5. Exception propagates back up: validateTrolley → checkOut → catch block
     * 
     * checkOut() declares 'throws IOException, SQLException' for database/file errors
     * but HANDLES MinimumPaymentException & ExcessiveOrderQuantityException with try-catch
     * 
     * @throws IOException if database/file operations fail
     * @throws SQLException if database operations fail
     */
    void checkOut() throws IOException, SQLException {
        if(!trolley.isEmpty()){
            // Week 6: Validate trolley before processing payment
            // Try-catch-finally block handles custom exceptions for business rule violations
            try {
                validateTrolley(trolley);
                // Week 6: If we reach here, validation passed - proceed to stock check
            } catch (MinimumPaymentException e) {
                // Week 6: Handle minimum payment violation - keep trolley, inform user
                System.out.println("MinimumPaymentException caught: " + e.getMessage());
                removeProductNotifier.showRemovalMsg(e.getUserMessage());
                return; // Week 6: Early return - don't proceed with checkout
            } catch (ExcessiveOrderQuantityException e) {
                // Week 6: Handle excessive quantity - adjust quantities, inform user, WAIT for re-checkout
                System.out.println("ExcessiveOrderQuantityException caught: " + e.getMessage());
                handleExcessiveQuantities(e.getExcessiveProducts(), e.getMaximumAllowed());
                removeProductNotifier.showRemovalMsg(e.getUserMessage());
                return; // Week 6: Early return - user must click checkout again with adjusted quantities
            } finally {
                // Week 6: Finally block always executes (whether exception thrown or not)
                // Used for cleanup or logging - executes even with early return
                System.out.println("Validation phase completed.");
            }
            
            // Week 6: Validation successful - proceed with stock checking and payment
            System.out.println("All validations passed. Proceeding to stock verification...");
            
            // Group the products in the trolley by productId to optimize stock checking
            // Check the database for sufficient stock for all products in the trolley.
            // If any products are insufficient, the update will be rolled back.
            // If all products are sufficient, the database will be updated, and insufficientProducts will be empty.
            // Note: If the trolley is already organized (merged and sorted), grouping is unnecessary.
            ArrayList<Product> groupedTrolley= groupProductsById(trolley);
            ArrayList<Product> insufficientProducts= databaseRW.purchaseStocks(groupedTrolley);

            if(insufficientProducts.isEmpty()){ // If stock is sufficient for all products
                // Week 9: Calculate total for payment dialog
                double totalAmount = 0.0;
                for (Product p : trolley) {
                    totalAmount += p.getUnitPrice() * p.getOrderedQuantity();
                }
                
                // Week 9: Show payment dialog before finalizing order
                // Week 10: Pass customer type to show Prime discount in payment dialog
                PaymentDialog paymentDialog = new PaymentDialog();
                PaymentResult paymentResult = paymentDialog.show(totalAmount, customerType);
                
                // Week 9: Check if payment was confirmed
                if (!paymentResult.isConfirmed()) {
                    System.out.println("Week 9: Payment cancelled by user. Checkout aborted.");
                    displayTaTrolley = ProductListFormatter.buildString(trolley); // Week 9: Keep trolley intact
                    updateView();
                    return; // Week 9: Exit checkout without creating order
                }
                
                // Week 9: Payment confirmed - log payment method
                System.out.println("Week 9: Payment confirmed via " + paymentResult.getPaymentMethod());
                
                //get OrderHub and tell it to make a new Order
                // Week 6 debug: Log before creating order
                System.out.println("Week 6 Debug: CustomerModel requesting OrderHub to create new order...");
                
                OrderHub orderHub =OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(trolley, customerType); // Week 10: Pass customer type to order
                trolley.clear();
                displayTaTrolley ="";
                
                // Week 10: Build receipt with customer type benefits
                StringBuilder receiptBuilder = new StringBuilder();
                receiptBuilder.append(String.format("Order_ID: %s\nOrdered_Date_Time: %s\n",
                        theOrder.getOrderId(),
                        theOrder.getOrderedDateTime()));
                receiptBuilder.append(ProductListFormatter.buildString(theOrder.getProductList()));
                
                // Week 10: Add customer type benefits to receipt
                if (customerType.equals("VIP")) {
                    receiptBuilder.append("\n🌟 VIP Member Benefits:\n");
                    receiptBuilder.append("   • No minimum order requirement\n");
                    receiptBuilder.append("   • Fast delivery from warehouse\n"); // Week 10: In-store pickup context
                } else if (customerType.equals("Prime")) {
                    receiptBuilder.append("\n⭐ Prime Member Benefits:\n");
                    receiptBuilder.append("   • No minimum order requirement\n");
                    receiptBuilder.append("   • Express delivery from warehouse\n"); // Week 10: In-store pickup context
                    receiptBuilder.append("   • 10% discount applied\n");
                    // Week 10: Calculate and display discounted total for Prime members
                    double originalTotal = totalAmount;
                    double discountedTotal = originalTotal * 0.9; // 10% discount
                    receiptBuilder.append(String.format("   Original Total: £%.2f\n", originalTotal));
                    receiptBuilder.append(String.format("   Final Total: £%.2f (saved £%.2f)\n", 
                            discountedTotal, originalTotal - discountedTotal));
                }
                
                displayTaReceipt = receiptBuilder.toString();
                
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
     * Week 6: Validates trolley contents before checkout
     * 
     * Uses 'throws' keyword to declare checked exceptions that may be thrown:
     * - MinimumPaymentException if total < £5
     * - ExcessiveOrderQuantityException if any product quantity > 50
     * 
     * 'throws' in method signature means:
     * - This method does NOT handle these exceptions itself
     * - Caller MUST handle them (try-catch) or declare throws
     * - Compiler enforces this for checked exceptions
     * - Exception propagates up the call stack to caller
     * 
     * Call stack: validateTrolley() → checkOut() → CustomerController
     * 
     * @param trolley List of products to validate
     * @throws MinimumPaymentException if payment below minimum (checked exception)
     * @throws ExcessiveOrderQuantityException if any quantity exceeds maximum (checked exception)
     * Reference: Week 6 - throws keyword, Exception Propagation
     */
    private void validateTrolley(ArrayList<Product> trolley) 
            throws MinimumPaymentException, ExcessiveOrderQuantityException {
        
        // Week 6: Constant for business rules
        final double MINIMUM_PAYMENT = 5.0;
        final int MAXIMUM_QUANTITY = 50;
        
        // Week 6: Calculate total payment
        double totalPayment = 0.0;
        for (Product p : trolley) {
            totalPayment += p.getUnitPrice() * p.getOrderedQuantity();
        }
        
        // Week 10: VIP and Prime customers are exempt from minimum payment requirement (OCP)
        if (!customerType.equals("VIP") && !customerType.equals("Prime")) {
            // Week 6: Check minimum payment rule for Standard customers only
            if (totalPayment < MINIMUM_PAYMENT) {
                // Week 6: 'throw' keyword creates and throws exception object
                // Execution stops here and exception propagates to caller (checkOut)
                // new MinimumPaymentException(...) creates exception instance
                throw new MinimumPaymentException(totalPayment, MINIMUM_PAYMENT);
            }
        }
        
        // Week 6: Check excessive quantity rule
        ArrayList<Product> excessiveProducts = new ArrayList<>();
        for (Product p : trolley) {
            if (p.getOrderedQuantity() > MAXIMUM_QUANTITY) {
                excessiveProducts.add(p);
            }
        }
        
        // Week 6: 'throw' keyword throws exception if validation fails
        // Difference: 'throw' (singular) = throw an exception object
        //             'throws' (plural) = declare that method may throw exceptions
        if (!excessiveProducts.isEmpty()) {
            throw new ExcessiveOrderQuantityException(excessiveProducts, MAXIMUM_QUANTITY);
        }
    }
    
    /**
     * Week 6: Handles excessive quantities by reducing them to maximum allowed
     * Automatically adjusts trolley contents when quantities exceed limit
     * 
     * @param excessiveProducts Products with quantities > maximum
     * @param maximumAllowed Maximum allowed quantity per product
     */
    private void handleExcessiveQuantities(List<Product> excessiveProducts, int maximumAllowed) {
        // Week 6: Reduce quantities to maximum for each excessive product
        for (Product excessiveProd : excessiveProducts) {
            // Week 6: Find matching product in trolley and adjust quantity
            for (Product trolleyProd : trolley) {
                if (trolleyProd.getProductId().equals(excessiveProd.getProductId())) {
                    trolleyProd.setOrderedQuantity(maximumAllowed);
                    System.out.println("Reduced " + trolleyProd.getProductId() + 
                                     " quantity to maximum: " + maximumAllowed);
                    break;
                }
            }
        }
        
        // Week 6: Update trolley display after quantity adjustment
        displayTaTrolley = ProductListFormatter.buildString(trolley);
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
    
    /**
     * Week 6: Demonstrates try-with-resources for automatic resource management
     * Saves receipt to file - resources (BufferedWriter, FileWriter) auto-closed
     * 
     * Try-with-resources ensures proper cleanup even if exception occurs:
     * - Resources declared in try() parentheses
     * - Automatically closed in reverse order of creation
     * - No explicit finally block needed for cleanup
     * 
     * @param orderId Order ID for filename
     * @param receiptContent Receipt text to save
     * @throws IOException if file writing fails
     * Reference: Week 6 - Try-with-resources
     */
    void saveReceiptToFile(int orderId, String receiptContent) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "receipt_" + orderId + "_" + timestamp + ".txt";
        
        // Week 6: try-with-resources - BufferedWriter and FileWriter auto-closed
        // Both resources implement AutoCloseable interface
        // close() called automatically at end of try block (even if exception thrown)
        try (FileWriter fileWriter = new FileWriter(filename);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            
            bufferedWriter.write("=".repeat(50));
            bufferedWriter.newLine();
            bufferedWriter.write("HAPPYSHOP RECEIPT");
            bufferedWriter.newLine();
            bufferedWriter.write("=".repeat(50));
            bufferedWriter.newLine();
            bufferedWriter.write(receiptContent);
            bufferedWriter.newLine();
            bufferedWriter.write("=".repeat(50));
            
            System.out.println("Receipt saved to: " + filename);
            
        } // Week 6: Resources automatically closed here (reverse order: bufferedWriter, fileWriter)
        // No need for explicit finally block for resource cleanup
        // If IOException occurs, resources still closed before exception propagates
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
        
        // Week 10: Add Prime discount information to trolley display
        String trolleyDisplay = displayTaTrolley;
        if (customerType.equals("Prime") && !trolley.isEmpty() && !displayTaTrolley.isEmpty()) {
            trolleyDisplay = addPrimeDiscountToTrolley(displayTaTrolley);
        }
        
        cusView.update(imageName, displayLaSearchResult, trolleyDisplay, displayTaReceipt);
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

    // Week 10: Setter for customer type (OCP principle - extensible behavior)
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
        System.out.println("Week 10: Customer type changed to: " + customerType);
        updateView(); // Week 10: Update view to reflect discount changes for Prime customers
    }
    
    /**
     * Week 10: Adds Prime discount information to trolley display
     * Calculates 10% discount and appends it to the trolley string
     * @param trolleyString The original trolley display string
     * @return Modified trolley string with discount information
     */
    private String addPrimeDiscountToTrolley(String trolleyString) {
        // Week 10: Calculate original total from trolley
        double originalTotal = 0.0;
        for (Product p : trolley) {
            originalTotal += p.getUnitPrice() * p.getOrderedQuantity();
        }
        
        // Week 10: Calculate discounted total (10% off)
        double discountedTotal = originalTotal * 0.9;
        double savings = originalTotal - discountedTotal;
        
        // Week 10: Find the "Total" line and replace it with discount info
        String[] lines = trolleyString.split("\n");
        StringBuilder modifiedTrolley = new StringBuilder();
        
        for (String line : lines) {
            if (line.trim().startsWith("Total")) {
                // Week 10: Replace total line with Prime discount information
                modifiedTrolley.append(String.format(" Original Total                      £%7.2f\n", originalTotal));
                modifiedTrolley.append(" ⭐ Prime Discount (10%)             £ -").append(String.format("%6.2f", savings)).append("\n");
                modifiedTrolley.append("--------------------------------------------\n");
                modifiedTrolley.append(String.format(" Final Total                         £%7.2f", discountedTotal));
            } else {
                modifiedTrolley.append(line).append("\n");
            }
        }
        
        return modifiedTrolley.toString().trim();
    }

    // Week 10: Getter for customer type
    public String getCustomerType() {
        return customerType;
    }

    //for test only
    public ArrayList<Product> getTrolley() {
        return trolley;
    }
}
