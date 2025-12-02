package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.PaymentMethod;

/**
 * Week 9: PaymentResult encapsulates the result of a payment dialog interaction.
 * Used to communicate user's payment choice back to the checkout process.
 */
public class PaymentResult {
    private final boolean confirmed;
    private final PaymentMethod paymentMethod;

    public PaymentResult(boolean confirmed, PaymentMethod paymentMethod) {
        this.confirmed = confirmed;
        this.paymentMethod = paymentMethod;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    // Week 9: Factory methods for cleaner code
    public static PaymentResult cancelled() {
        return new PaymentResult(false, null);
    }

    public static PaymentResult confirmed(PaymentMethod method) {
        return new PaymentResult(true, method);
    }
}

