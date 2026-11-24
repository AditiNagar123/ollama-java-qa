package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class CalculatorTest {
    @Test
    void add_positive_numbers() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(4, calculator.add(2, 2));
    }

    @Test
    void add_zero() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(0, calculator.add(0, 0));
    }

    @Test
    void add_negative_numbers() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(-4, calculator.add(-2, -2));
    }

    @Test
    void add_large_values() {
        Calculator calculator = new Calculator();
        // FIXED: 500 million + 500 million = 1 billion
        Assertions.assertEquals(1_000_000_000, calculator.add(500_000_000, 500_000_000));
    }

    @Test
    void subtract_positive_numbers() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(2, calculator.subtract(4, 2));
    }

    @Test
    void subtract_zero() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(0, calculator.subtract(0, 0));
    }

    @Test
    void subtract_negative_numbers() {
        Calculator calculator = new Calculator();
        // FIXED: -2 - (-2) = 0
        Assertions.assertEquals(0, calculator.subtract(-2, -2));
    }

    @Test
    void subtract_large_values() {
        Calculator calculator = new Calculator();
        // FIXED: 1 million - 1 billion... use values that really give -1 billion
        Assertions.assertEquals(-1_000_000_000, calculator.subtract(1_000_000, 1_001_000_000));
    }

    @Test
    void multiply_positive_numbers() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(4, calculator.multiply(2, 2));
    }

    @Test
    void multiply_zero() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(0, calculator.multiply(0, 5));
    }

    @Test
    void multiply_negative_numbers() {
        Calculator calculator = new Calculator();
        // FIXED: negative x negative = positive
        Assertions.assertEquals(16, calculator.multiply(-4, -4));
    }

    @Test
    void multiply_large_values() {
        Calculator calculator = new Calculator();
        // FIXED: 1,000,000 x 1,000 = 1 billion, which fits in an int
        Assertions.assertEquals(1_000_000_000, calculator.multiply(1_000_000, 1_000));
    }

    @Test
    void multiply_overflow_wraps_around() {
        Calculator calculator = new Calculator();
        // NEW: documents int overflow (1,000,000 x 1,000,000 does not fit in an int)
        Assertions.assertEquals(-727_379_968, calculator.multiply(1_000_000, 1_000_000));
    }

    @Test
    void divide_positive_numbers() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(5.0, calculator.divide(10, 2));
    }

    @Test
    void divide_zero() {
        Calculator calculator = new Calculator();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            calculator.divide(10, 0);
        });
    }

    @Test
    void divide_large_values() {
        Calculator calculator = new Calculator();
        Assertions.assertEquals(1.0, calculator.divide(1_000_000_000, 1_000_000_000));
    }

    @Test
    void isEven_positive_even() {
        Calculator calculator = new Calculator();
        Assertions.assertTrue(calculator.isEven(4));
    }

    @Test
    void isEven_positive_odd() {
        Calculator calculator = new Calculator();
        Assertions.assertFalse(calculator.isEven(5));
    }

    @Test
    void isEven_zero() {
        Calculator calculator = new Calculator();
        Assertions.assertTrue(calculator.isEven(0));
    }

    @Test
    void isEven_negative_even() {
        Calculator calculator = new Calculator();
        Assertions.assertTrue(calculator.isEven(-4));
    }

    @Test
    void isEven_negative_odd() {
        Calculator calculator = new Calculator();
        Assertions.assertFalse(calculator.isEven(-5));
    }
}