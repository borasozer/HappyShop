package ci553.happyshop.catalogue;

import ci553.happyshop.orderManagement.OrderState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Week 11: Unit tests for Order class
 * Tests order creation, state management, and customer type handling
 * 
 * Test Design Strategies Applied:
 * - Equivalence Partitioning: Valid/invalid order data
 * - Boundary Value Analysis: Edge cases for order IDs and quantities
 * - Error Guessing: Common mistakes in order creation
 * 
 * Test Level: Unit Testing (testing individual Order class)
 */
@DisplayName("Week 11: Order Class Unit Tests")
public class OrderTest {

    private ArrayList<Product> testProductList;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Week 11: Set up test data before each test
        // Equivalence Partitioning: Valid product data
        product1 = new Product("0001", "40 inch TV", "0001.jpg", 269.00, 10);
        product1.setOrderedQuantity(2);
        
        product2 = new Product("0002", "DAB Radio", "0002.jpg", 29.99, 5);
        product2.setOrderedQuantity(1);
        
        testProductList = new ArrayList<>();
        testProductList.add(product1);
        testProductList.add(product2);
    }

    @Test
    @DisplayName("Week 11: Test Order Creation with Standard Customer")
    void testOrderCreationStandard() {
        // Week 11: Test standard order creation
        // Equivalence Partitioning: Valid order parameters
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", testProductList, "Standard");
        
        // Week 11: Verify order attributes
        assertEquals(1, order.getOrderId());
        assertEquals(OrderState.Ordered, order.getState());
        assertEquals("2025-01-01 10:00:00", order.getOrderedDateTime());
        assertEquals(2, order.getProductList().size());
        assertEquals("Standard", order.getCustomerType());
    }

    @Test
    @DisplayName("Week 11: Test Order Creation with VIP Customer")
    void testOrderCreationVIP() {
        // Week 11: Test VIP customer order (Week 10 feature)
        // Equivalence Partitioning: Valid VIP customer type
        Order order = new Order(2, OrderState.Ordered, "2025-01-01 11:00:00", testProductList, "VIP");
        
        assertEquals("VIP", order.getCustomerType());
        assertEquals(2, order.getOrderId());
    }

    @Test
    @DisplayName("Week 11: Test Order Creation with Prime Customer")
    void testOrderCreationPrime() {
        // Week 11: Test Prime customer order (Week 10 feature)
        // Equivalence Partitioning: Valid Prime customer type
        Order order = new Order(3, OrderState.Ordered, "2025-01-01 12:00:00", testProductList, "Prime");
        
        assertEquals("Prime", order.getCustomerType());
        assertEquals(3, order.getOrderId());
    }

    @Test
    @DisplayName("Week 11: Test Order ID Boundary Values")
    void testOrderIdBoundaryValues() {
        // Week 11: Boundary Value Analysis for order IDs
        
        // Test minimum valid order ID
        Order order1 = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", testProductList, "Standard");
        assertEquals(1, order1.getOrderId());
        
        // Test large order ID (system should handle large numbers)
        Order order2 = new Order(99999, OrderState.Ordered, "2025-01-01 10:00:00", testProductList, "Standard");
        assertEquals(99999, order2.getOrderId());
    }

    @Test
    @DisplayName("Week 11: Test Order State Changes")
    void testOrderStateChanges() {
        // Week 11: Test state transitions (Ordered → Progressing → Ready → Collected)
        // Path Testing: Different order states
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", testProductList, "Standard");
        
        assertEquals(OrderState.Ordered, order.getState());
        
        // Week 11: Test state change to Progressing
        order.setState(OrderState.Progressing);
        assertEquals(OrderState.Progressing, order.getState());
        
        // Week 11: Test state change to Ready
        order.setState(OrderState.Ready);
        assertEquals(OrderState.Ready, order.getState());
        
        // Week 11: Test state change to Collected
        order.setState(OrderState.Collected);
        assertEquals(OrderState.Collected, order.getState());
    }

    @Test
    @DisplayName("Week 11: Test Order Product List Integrity")
    void testProductListIntegrity() {
        // Week 11: Test that product list is correctly stored and retrieved
        // Integration Testing: Order + Product relationship
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", testProductList, "Standard");
        
        ArrayList<Product> retrievedList = order.getProductList();
        
        // Week 11: Verify list size
        assertEquals(2, retrievedList.size());
        
        // Week 11: Verify first product
        assertEquals("0001", retrievedList.get(0).getProductId());
        assertEquals(2, retrievedList.get(0).getOrderedQuantity());
        
        // Week 11: Verify second product
        assertEquals("0002", retrievedList.get(1).getProductId());
        assertEquals(1, retrievedList.get(1).getOrderedQuantity());
    }

    @Test
    @DisplayName("Week 11: Test Empty Product List")
    void testEmptyProductList() {
        // Week 11: Boundary Value Analysis - empty product list
        // Error Guessing: What if order has no products?
        ArrayList<Product> emptyList = new ArrayList<>();
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", emptyList, "Standard");
        
        assertNotNull(order.getProductList());
        assertEquals(0, order.getProductList().size());
    }

    @Test
    @DisplayName("Week 11: Test Order Details Format")
    void testOrderDetailsFormat() {
        // Week 11: Test orderDetails() method output format
        // Integration Testing: Order formatting logic
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", testProductList, "Prime");
        
        String details = order.orderDetails();
        
        // Week 11: Verify essential information is present
        assertTrue(details.contains("Order ID: 1"));
        assertTrue(details.contains("State: Ordered"));
        assertTrue(details.contains("CustomerType: Prime")); // Week 10 feature
        assertTrue(details.contains("OrderedDateTime: 2025-01-01 10:00:00"));
        assertTrue(details.contains("Items:"));
    }

    @Test
    @DisplayName("Week 11: Test Payment Method (Week 3 Feature)")
    void testPaymentMethod() {
        // Week 11: Test payment method enum (Week 3 feature)
        // Equivalence Partitioning: Different payment methods
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", testProductList, "Standard");
        
        // Week 11: Test default payment method
        assertEquals(PaymentMethod.CASH, order.getPaymentMethod());
        
        // Week 11: Test setting payment method
        order.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        assertEquals(PaymentMethod.CREDIT_CARD, order.getPaymentMethod());
    }

    @Test
    @DisplayName("Week 11: Test Order with Single Product")
    void testOrderWithSingleProduct() {
        // Week 11: Boundary Value Analysis - single product order
        ArrayList<Product> singleProductList = new ArrayList<>();
        singleProductList.add(product1);
        
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", singleProductList, "Standard");
        
        assertEquals(1, order.getProductList().size());
        assertEquals("0001", order.getProductList().get(0).getProductId());
    }

    @Test
    @DisplayName("Week 11: Test Order with Large Product List")
    void testOrderWithLargeProductList() {
        // Week 11: Boundary Value Analysis - many products
        // Performance consideration: Can system handle large orders?
        ArrayList<Product> largeList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Product p = new Product(String.format("%04d", i), "Product " + i, "image.jpg", 10.0, 100);
            p.setOrderedQuantity(1);
            largeList.add(p);
        }
        
        Order order = new Order(1, OrderState.Ordered, "2025-01-01 10:00:00", largeList, "Standard");
        
        assertEquals(50, order.getProductList().size());
    }
}

