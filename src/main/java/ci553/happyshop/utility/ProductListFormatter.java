package ci553.happyshop.utility;

import ci553.happyshop.catalogue.Product;

import java.util.ArrayList;

/**
 * This class builds a formatted, receipt-like summary from a list of products.
 * It is used by:
 * 1. CustomerModel – to display the trolley and receipt
 * 2. The Order class – to generate a summary for writing to an order's file
 * 
 * Week 3: Utility class with static methods and constants for consistent formatting
 */

public class ProductListFormatter {
    // Week 3: Static final constants for consistent formatting across the application
    private static final String CURRENCY_SYMBOL = "£";
    private static final int SEPARATOR_LENGTH = 44;
    private static final String LINE_SEPARATOR = "-".repeat(SEPARATOR_LENGTH) + "\n";
    
    /**
     * Builds a formatted string showing each product's ID, description,
     * quantity ordered, and total price. Also includes a total price at the end.
     * @param proList a List of products
     * @return A nicely formatted string representation of the product list with totals
     */
    public static String buildString(ArrayList<Product> proList) {
        StringBuilder sb = new StringBuilder();
        double totalPrice=0;
        for (Product pr : proList) {
            int orderedQuantity = pr.getOrderedQuantity();
            //%-18.18s, format the argument as a String,
            // -18 → Left-align the string in 18-character wide space.
            //.18 → Truncate the string to at most 18 characters
            // Week 3: Uses static final CURRENCY_SYMBOL constant for consistent formatting
            String aProduct=String.format(" %-7s %-18.18s (%2d) %s%7.2f\n",
                    pr.getProductId(),
                    pr.getProductDescription(),
                    pr.getOrderedQuantity(),
                    CURRENCY_SYMBOL,
                    pr.getUnitPrice() * orderedQuantity);

            sb.append(aProduct);
            totalPrice = totalPrice + pr.getUnitPrice() * orderedQuantity;
        }

        // Week 3: Uses static final constants for separator and currency
        String total = String.format(" %-35s %s%7.2f\n", "Total", CURRENCY_SYMBOL, totalPrice);

        sb.append(LINE_SEPARATOR);
        sb.append(total);
        return sb.toString();
    }
}
