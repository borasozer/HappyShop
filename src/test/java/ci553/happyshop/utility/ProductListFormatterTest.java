package ci553.happyshop.utility;

import ci553.happyshop.catalogue.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Week 11: Integration tests for ProductListFormatter
 * Tests formatting logic for product lists (trolley, receipt display)
 * 
 * Test Design Strategies Applied:
 * - Equivalence Partitioning: Empty/single/multiple products
 * - Boundary Value Analysis: Edge cases for formatting
 * - Integration Testing: Product + Formatter interaction
 * 
 * Test Level: Integration Testing (ProductListFormatter + Product)
 */
@DisplayName("Week 11: ProductListFormatter Integration Tests")
public class ProductListFormatterTest {

    private ArrayList<Product> productList;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Week 11: Prepare test data
        product1 = new Product("0001", "40 inch TV", "0001.jpg", 269.00, 10);
        product1.setOrderedQuantity(2);
        
        product2 = new Product("0002", "DAB Radio", "0002.jpg", 29.99, 5);
        product2.setOrderedQuantity(1);
        
        productList = new ArrayList<>();
    }

    @Test
    @DisplayName("Week 11: Test Empty Product List Formatting")
    void testEmptyProductListFormatting() {
        // Week 11: Boundary Value Analysis - empty list
        // Error Guessing: What if trolley is empty?
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Should return empty string or appropriate message
        assertNotNull(result);
    }

    @Test
    @DisplayName("Week 11: Test Single Product Formatting")
    void testSingleProductFormatting() {
        // Week 11: Equivalence Partitioning - single product
        productList.add(product1);
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Verify formatted string contains product information
        assertNotNull(result);
        assertTrue(result.contains("0001"));
        assertTrue(result.contains("40 inch TV"));
        assertTrue(result.contains("538.00")); // Week 11: 269.00 * 2 = 538.00
        assertTrue(result.contains("( 2)")); // Week 11: Quantity format with space for single digit
    }

    @Test
    @DisplayName("Week 11: Test Multiple Products Formatting")
    void testMultipleProductsFormatting() {
        // Week 11: Integration Testing - multiple products
        productList.add(product1);
        productList.add(product2);
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Verify both products are in output
        assertTrue(result.contains("0001"));
        assertTrue(result.contains("40 inch TV"));
        assertTrue(result.contains("0002"));
        assertTrue(result.contains("DAB Radio"));
    }

    @Test
    @DisplayName("Week 11: Test Total Calculation in Formatting")
    void testTotalCalculation() {
        // Week 11: Business Logic Testing - total calculation
        productList.add(product1); // 269.00 * 2 = 538.00
        productList.add(product2); // 29.99 * 1 = 29.99
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Verify total is present and correct
        assertTrue(result.contains("Total"));
        assertTrue(result.contains("567.99")); // Week 11: Expected total
    }

    @Test
    @DisplayName("Week 11: Test Formatting with Large Quantities")
    void testFormattingWithLargeQuantities() {
        // Week 11: Boundary Value Analysis - large quantity
        product1.setOrderedQuantity(50); // Week 11: Maximum allowed (Week 6 rule)
        productList.add(product1);
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Verify large quantity is formatted correctly
        assertTrue(result.contains("(50)")); // Week 11: 2-digit format, no space
        assertTrue(result.contains("13450.00")); // Week 11: 269.00 * 50
    }

    @Test
    @DisplayName("Week 11: Test Formatting with Decimal Prices")
    void testFormattingWithDecimalPrices() {
        // Week 11: Test proper decimal formatting
        Product product = new Product("0003", "USB Drive", "0003.jpg", 6.99, 20);
        product.setOrderedQuantity(3);
        productList.add(product);
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Verify decimal formatting (formatter shows total, not unit price)
        assertTrue(result.contains("0003"));
        assertTrue(result.contains("USB Drive"));
        assertTrue(result.contains("20.97")); // Week 11: 6.99 * 3 = 20.97 (total price shown)
    }

    @Test
    @DisplayName("Week 11: Test Separator Lines in Formatting")
    void testSeparatorLines() {
        // Week 11: UI Testing - visual formatting
        productList.add(product1);
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Verify separator lines are present (Week 3 constants)
        assertTrue(result.contains("----")); // Week 11: Check for separator
    }

    @Test
    @DisplayName("Week 11: Test Product Description Truncation")
    void testProductDescriptionHandling() {
        // Week 11: Error Guessing - long product descriptions
        Product longNameProduct = new Product("0099", 
            "Very Long Product Description That Might Need Special Handling", 
            "0099.jpg", 99.99, 5);
        longNameProduct.setOrderedQuantity(1);
        productList.add(longNameProduct);
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Should handle long descriptions gracefully
        assertNotNull(result);
        assertTrue(result.contains("0099"));
    }

    @Test
    @DisplayName("Week 11: Test Formatting Consistency Across Multiple Calls")
    void testFormattingConsistency() {
        // Week 11: Test that formatter produces consistent results
        productList.add(product1);
        productList.add(product2);
        
        String result1 = ProductListFormatter.buildString(productList);
        String result2 = ProductListFormatter.buildString(productList);
        
        // Week 11: Results should be identical for same input
        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("Week 11: Test Zero Quantity Product")
    void testZeroQuantityProduct() {
        // Week 11: Boundary Value Analysis - zero quantity
        // Error Guessing: What if quantity is 0?
        product1.setOrderedQuantity(0);
        productList.add(product1);
        
        String result = ProductListFormatter.buildString(productList);
        
        // Week 11: Should handle zero quantity (edge case)
        assertNotNull(result);
        assertTrue(result.contains("( 0)"));
    }
}

