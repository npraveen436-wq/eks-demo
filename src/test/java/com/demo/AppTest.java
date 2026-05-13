package com.demo;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testGreetWithName() {
        assertEquals("Hello, Alice", new App().greet("Alice"));
    }

    @Test
    public void testGreetWithNull() {
        assertEquals("Hello, stranger", new App().greet(null));
    }

    @Test
    public void testGreetWithBlank() {
        assertEquals("Hello, stranger", new App().greet("  "));
    }
}