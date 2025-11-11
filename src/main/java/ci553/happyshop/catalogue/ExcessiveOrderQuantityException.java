package ci553.happyshop.catalogue;

import java.util.ArrayList;
import java.util.List;

/**
 * Week 6: Custom checked exception for quantity limit validation
 * Thrown when any product in the order has quantity exceeding maximum allowed (50)
 * 
 * This is a checked exception (extends Exception) because:
 * - It represents a recoverable business rule violation
 * - Caller must explicitly handle it (try-catch or throws)
 * - System can automatically adjust quantities to maximum
 * 
 * Reference: Week 6 - Exception Handling, Custom Exceptions
 */
public class ExcessiveOrderQuantityException extends Exception {
    private final List<Product> excessiveProducts;
    private final int maximumAllowed;
    
    /**
     * Week 6: Constructor with list of products exceeding quantity limit
     * @param excessiveProducts Products with quantity > maximum
     * @param maximumAllowed Maximum allowed quantity per product
     */
    public ExcessiveOrderQuantityException(List<Product> excessiveProducts, int maximumAllowed) {
        super(String.format("%d product(s) exceed maximum quantity of %d", 
                          excessiveProducts.size(), maximumAllowed));
        this.excessiveProducts = new ArrayList<>(excessiveProducts);
        this.maximumAllowed = maximumAllowed;
    }
    
    public List<Product> getExcessiveProducts() {
        return new ArrayList<>(excessiveProducts);
    }
    
    public int getMaximumAllowed() {
        return maximumAllowed;
    }
    
    /**
     * Week 6: User-friendly message for UI display
     */
    public String getUserMessage() {
        StringBuilder message = new StringBuilder();
        message.append("⚠️ The following items exceed the maximum quantity limit:\n\n");
        
        for (Product p : excessiveProducts) {
            message.append(String.format("• %s - %s\n", 
                                       p.getProductId(), 
                                       p.getProductDescription()));
            message.append(String.format("  Requested: %d (Max allowed: %d)\n", 
                                       p.getOrderedQuantity(), 
                                       maximumAllowed));
            message.append(String.format("  → Reduced to maximum: %d\n\n", 
                                       maximumAllowed));
        }
        
        return message.toString();
    }
}

