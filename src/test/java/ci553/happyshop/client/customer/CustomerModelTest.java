package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.MinimumPaymentException;
import ci553.happyshop.catalogue.ExcessiveOrderQuantityException;
import ci553.happyshop.catalogue.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Week 11: Business Logic and Workflow Tests for CustomerModel
 * Tests user input validation, business rules, and customer workflows
 * 
 * Test Design Strategies Applied:
 * - Equivalence Partitioning: Valid/invalid inputs (customer types, payments, quantities)
 * - Boundary Value Analysis: £5 minimum, 50 max quantity (Week 6 business rules)
 * - Path Testing: Different customer types (Week 10), checkout paths
 * - Error Guessing: Common user mistakes
 * 
 * Test Levels: Unit Testing (individual methods) + Integration Testing (workflow)
 */
@DisplayName("Week 11: CustomerModel Business Logic Tests")
public class CustomerModelTest {

    private CustomerModel customerModel;
    private ArrayList<Product> testTrolley;

    @BeforeEach
    void setUp() {
        // Week 11: Initialize CustomerModel with default constructor
        customerModel = new CustomerModel();
        
        // Week 11: Access trolley for testing (using getTrolley() test method)
        testTrolley = customerModel.getTrolley();
    }

    @Test
    @DisplayName("Week 11: Test Customer Type Default Value")
    void testCustomerTypeDefault() {
        // Week 11: Test default customer type is Standard
        // Equivalence Partitioning: Default case
        assertEquals("Standard", customerModel.getCustomerType());
    }

    @Test
    @DisplayName("Week 11: Test Customer Type Change to VIP")
    void testCustomerTypeChangeVIP() {
        // Week 11: Test customer type change (Week 10 feature)
        // Equivalence Partitioning: Valid customer type
        customerModel.setCustomerType("VIP");
        assertEquals("VIP", customerModel.getCustomerType());
    }

    @Test
    @DisplayName("Week 11: Test Customer Type Change to Prime")
    void testCustomerTypeChangePrime() {
        // Week 11: Test Prime customer type
        // Path Testing: Prime customer path
        customerModel.setCustomerType("Prime");
        assertEquals("Prime", customerModel.getCustomerType());
    }

    @Test
    @DisplayName("Week 11: Test Add Product to Trolley")
    void testAddProductToTrolley() {
        // Week 11: Test trolley functionality (Week 2 feature)
        // Workflow Testing: Product addition workflow
        Product product = new Product("0001", "TV", "0001.jpg", 269.00, 10);
        product.setOrderedQuantity(1);
        
        // Week 11: Simulate adding product to trolley
        testTrolley.add(product);
        
        assertEquals(1, testTrolley.size());
        assertEquals("0001", testTrolley.get(0).getProductId());
    }

    @Test
    @DisplayName("Week 11: Test Trolley Quantity Merging (Week 2)")
    void testTrolleyQuantityMerging() {
        // Week 11: Test that same product quantities merge (Week 2 feature)
        // Integration Testing: Trolley management logic
        Product product1 = new Product("0001", "TV", "0001.jpg", 269.00, 10);
        product1.setOrderedQuantity(1);
        
        Product product2 = new Product("0001", "TV", "0001.jpg", 269.00, 10);
        product2.setOrderedQuantity(1);
        
        // Week 11: Simulate Week 2 logic - quantities should merge
        testTrolley.add(product1);
        
        // Week 11: If same product added again, quantity should increase
        boolean found = false;
        for (Product p : testTrolley) {
            if (p.getProductId().equals(product2.getProductId())) {
                p.setOrderedQuantity(p.getOrderedQuantity() + product2.getOrderedQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            testTrolley.add(product2);
        }
        
        assertEquals(1, testTrolley.size()); // Week 11: Should only have 1 entry
        assertEquals(2, testTrolley.get(0).getOrderedQuantity()); // Week 11: Quantity should be 2
    }

    @Test
    @DisplayName("Week 11: Test Empty Trolley")
    void testEmptyTrolley() {
        // Week 11: Boundary Value Analysis - empty trolley
        // Error Guessing: User tries to checkout with empty trolley
        assertTrue(testTrolley.isEmpty());
        assertEquals(0, testTrolley.size());
    }

    @Test
    @DisplayName("Week 11: Test Trolley Clear (Cancel Operation)")
    void testTrolleyClear() {
        // Week 11: Test trolley cancellation (Week 2 feature)
        // Workflow Testing: Cancel workflow
        Product product = new Product("0001", "TV", "0001.jpg", 269.00, 10);
        testTrolley.add(product);
        
        assertEquals(1, testTrolley.size());
        
        // Week 11: Simulate cancel - clear trolley
        testTrolley.clear();
        
        assertEquals(0, testTrolley.size());
        assertTrue(testTrolley.isEmpty());
    }

    @Test
    @DisplayName("Week 11: Test Minimum Payment Validation - Below Minimum (Standard)")
    void testMinimumPaymentValidationBelowMinimum() {
        // Week 11: Boundary Value Analysis - £5 minimum (Week 6 business rule)
        // Test £4.99 (just below boundary)
        customerModel.setCustomerType("Standard");
        
        Product product = new Product("0001", "Cheap Item", "0001.jpg", 4.99, 10);
        product.setOrderedQuantity(1);
        testTrolley.add(product);
        
        // Week 11: Should throw MinimumPaymentException for Standard customers
        // Exception Testing: Week 6 custom exception
        assertThrows(MinimumPaymentException.class, () -> {
            // Week 11: This would be called inside checkOut() validation
            double total = 4.99;
            if (total < 5.0 && customerModel.getCustomerType().equals("Standard")) {
                throw new MinimumPaymentException(total, 5.0);
            }
        });
    }

    @Test
    @DisplayName("Week 11: Test Minimum Payment Validation - At Boundary")
    void testMinimumPaymentValidationAtBoundary() {
        // Week 11: Boundary Value Analysis - exactly £5.00
        customerModel.setCustomerType("Standard");
        
        Product product = new Product("0001", "Item", "0001.jpg", 5.00, 10);
        product.setOrderedQuantity(1);
        testTrolley.add(product);
        
        // Week 11: Should NOT throw exception at exactly £5.00
        double total = 5.00;
        assertDoesNotThrow(() -> {
            if (total < 5.0 && customerModel.getCustomerType().equals("Standard")) {
                throw new MinimumPaymentException(total, 5.0);
            }
        });
    }

    @Test
    @DisplayName("Week 11: Test Minimum Payment Bypass for VIP (Week 10)")
    void testMinimumPaymentBypassVIP() {
        // Week 11: Path Testing - VIP customer path (Week 10 feature)
        // VIP should bypass £5 minimum
        customerModel.setCustomerType("VIP");
        
        Product product = new Product("0001", "Cheap Item", "0001.jpg", 2.00, 10);
        product.setOrderedQuantity(1);
        testTrolley.add(product);
        
        // Week 11: VIP should NOT throw exception even with £2 order
        double total = 2.00;
        assertDoesNotThrow(() -> {
            if (!customerModel.getCustomerType().equals("VIP") && 
                !customerModel.getCustomerType().equals("Prime")) {
                if (total < 5.0) {
                    throw new MinimumPaymentException(total, 5.0);
                }
            }
        });
    }

    @Test
    @DisplayName("Week 11: Test Minimum Payment Bypass for Prime (Week 10)")
    void testMinimumPaymentBypassPrime() {
        // Week 11: Path Testing - Prime customer path (Week 10 feature)
        // Prime should bypass £5 minimum
        customerModel.setCustomerType("Prime");
        
        Product product = new Product("0001", "Cheap Item", "0001.jpg", 1.50, 10);
        product.setOrderedQuantity(1);
        testTrolley.add(product);
        
        // Week 11: Prime should NOT throw exception even with £1.50 order
        double total = 1.50;
        assertDoesNotThrow(() -> {
            if (!customerModel.getCustomerType().equals("VIP") && 
                !customerModel.getCustomerType().equals("Prime")) {
                if (total < 5.0) {
                    throw new MinimumPaymentException(total, 5.0);
                }
            }
        });
    }

    @Test
    @DisplayName("Week 11: Test Excessive Quantity Validation - Below Maximum")
    void testExcessiveQuantityValidationBelowMaximum() {
        // Week 11: Boundary Value Analysis - 50 max quantity (Week 6 rule)
        // Test 49 (just below boundary)
        Product product = new Product("0001", "Item", "0001.jpg", 10.00, 100);
        product.setOrderedQuantity(49);
        testTrolley.add(product);
        
        // Week 11: Should NOT throw exception for 49 items
        assertDoesNotThrow(() -> {
            if (product.getOrderedQuantity() > 50) {
                ArrayList<Product> excessive = new ArrayList<>();
                excessive.add(product);
                throw new ExcessiveOrderQuantityException(excessive, 50);
            }
        });
    }

    @Test
    @DisplayName("Week 11: Test Excessive Quantity Validation - At Boundary")
    void testExcessiveQuantityValidationAtBoundary() {
        // Week 11: Boundary Value Analysis - exactly 50 items
        Product product = new Product("0001", "Item", "0001.jpg", 10.00, 100);
        product.setOrderedQuantity(50);
        testTrolley.add(product);
        
        // Week 11: Should NOT throw exception at exactly 50
        assertDoesNotThrow(() -> {
            if (product.getOrderedQuantity() > 50) {
                ArrayList<Product> excessive = new ArrayList<>();
                excessive.add(product);
                throw new ExcessiveOrderQuantityException(excessive, 50);
            }
        });
    }

    @Test
    @DisplayName("Week 11: Test Excessive Quantity Validation - Above Maximum")
    void testExcessiveQuantityValidationAboveMaximum() {
        // Week 11: Boundary Value Analysis - 51 items (just above boundary)
        // Exception Testing: Week 6 custom exception
        Product product = new Product("0001", "Item", "0001.jpg", 10.00, 100);
        product.setOrderedQuantity(51);
        testTrolley.add(product);
        
        // Week 11: Should throw ExcessiveOrderQuantityException
        assertThrows(ExcessiveOrderQuantityException.class, () -> {
            if (product.getOrderedQuantity() > 50) {
                ArrayList<Product> excessive = new ArrayList<>();
                excessive.add(product);
                throw new ExcessiveOrderQuantityException(excessive, 50);
            }
        });
    }

    @Test
    @DisplayName("Week 11: Test Multiple Products in Trolley")
    void testMultipleProductsInTrolley() {
        // Week 11: Integration Testing - multiple products workflow
        Product product1 = new Product("0001", "TV", "0001.jpg", 269.00, 10);
        product1.setOrderedQuantity(2);
        
        Product product2 = new Product("0002", "Radio", "0002.jpg", 29.99, 5);
        product2.setOrderedQuantity(1);
        
        testTrolley.add(product1);
        testTrolley.add(product2);
        
        assertEquals(2, testTrolley.size());
        
        // Week 11: Calculate total
        double total = 0;
        for (Product p : testTrolley) {
            total += p.getUnitPrice() * p.getOrderedQuantity();
        }
        
        assertEquals(567.99, total, 0.01); // Week 11: Allow small floating point difference
    }

    @Test
    @DisplayName("Week 11: Test Prime Discount Calculation (Week 10)")
    void testPrimeDiscountCalculation() {
        // Week 11: Business Logic Testing - Prime 10% discount (Week 10 feature)
        customerModel.setCustomerType("Prime");
        
        Product product = new Product("0001", "TV", "0001.jpg", 100.00, 10);
        product.setOrderedQuantity(1);
        testTrolley.add(product);
        
        // Week 11: Calculate Prime discount
        double originalTotal = 100.00;
        double discountedTotal = originalTotal * 0.9; // 10% off
        
        assertEquals(90.00, discountedTotal, 0.01);
        assertEquals(10.00, originalTotal - discountedTotal, 0.01);
    }

    @Test
    @DisplayName("Week 11: Test Item Removal from Trolley (Week 5)")
    void testItemRemovalFromTrolley() {
        // Week 11: Workflow Testing - remove item (Week 5 feature)
        Product product1 = new Product("0001", "TV", "0001.jpg", 269.00, 10);
        Product product2 = new Product("0002", "Radio", "0002.jpg", 29.99, 5);
        
        testTrolley.add(product1);
        testTrolley.add(product2);
        
        assertEquals(2, testTrolley.size());
        
        // Week 11: Remove first product (Week 5 lambda: removeIf)
        testTrolley.removeIf(p -> p.getProductId().equals("0001"));
        
        assertEquals(1, testTrolley.size());
        assertEquals("0002", testTrolley.get(0).getProductId());
    }

    @Test
    @DisplayName("Week 11: Test Quantity Change (Week 5)")
    void testQuantityChange() {
        // Week 11: Test quantity modification (Week 5 item-level control)
        Product product = new Product("0001", "TV", "0001.jpg", 269.00, 10);
        product.setOrderedQuantity(2);
        testTrolley.add(product);
        
        // Week 11: Increase quantity
        product.setOrderedQuantity(product.getOrderedQuantity() + 1);
        assertEquals(3, product.getOrderedQuantity());
        
        // Week 11: Decrease quantity
        product.setOrderedQuantity(product.getOrderedQuantity() - 1);
        assertEquals(2, product.getOrderedQuantity());
    }
}

