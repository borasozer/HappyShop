package ci553.happyshop.catalogue;

/**
 * Week 3: Enum for type-safe payment method constants
 * Provides compile-time type safety and encapsulates payment method properties
 */
public enum PaymentMethod {
    CASH(5.0),           // Minimum £5 for cash payments
    CREDIT_CARD(0.0),    // No minimum
    DEBIT_CARD(0.0);     // No minimum

    private final double minAmount;

    // Week 3: Enum constructor for initializing minimum payment amounts
    PaymentMethod(double minAmount) {
        this.minAmount = minAmount;
    }

    public double getMinAmount() {
        return minAmount;
    }

    /**
     * Validates if the given amount meets the minimum requirement for this payment method
     * @param amount The payment amount to validate
     * @return true if amount is valid for this payment method
     */
    public boolean isValidAmount(double amount) {
        return amount >= minAmount;
    }
}

