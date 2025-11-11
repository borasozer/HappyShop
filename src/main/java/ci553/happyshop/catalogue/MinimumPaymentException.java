package ci553.happyshop.catalogue;

/**
 * Week 6: Custom checked exception for payment validation
 * Thrown when the total payment amount is below the minimum threshold (£5)
 * 
 * This is a checked exception (extends Exception) because:
 * - It represents a recoverable business rule violation
 * - Caller must explicitly handle it (try-catch or throws)
 * - User can fix the issue by adding more items to trolley
 */
public class MinimumPaymentException extends Exception {
    private final double actualAmount;
    private final double minimumRequired;
    
    /**
     * Week 6: Constructor with detailed payment information
     * @param actualAmount The current total payment amount
     * @param minimumRequired The minimum payment threshold
     */
    public MinimumPaymentException(double actualAmount, double minimumRequired) {
        super(String.format("Payment of £%.2f is below minimum of £%.2f", 
                          actualAmount, minimumRequired));
        this.actualAmount = actualAmount;
        this.minimumRequired = minimumRequired;
    }
    
    public double getActualAmount() {
        return actualAmount;
    }
    
    public double getMinimumRequired() {
        return minimumRequired;
    }
    
    /**
     * Week 6: User-friendly message for UI display
     */
    public String getUserMessage() {
        return String.format("⚠️ Minimum payment is £%.2f\n" +
                           "Current total: £%.2f\n" +
                           "Please add £%.2f more to proceed.",
                           minimumRequired, actualAmount, minimumRequired - actualAmount);
    }
}

