package com.demo;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class App {
    public String greet(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, stranger";
        }
        return "Hello, " + name;
    }

    public static void main(String[] args) throws IOException {
        App app = new App();
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/greet", exchange -> {
            URI uri = exchange.getRequestURI();
            String query = uri.getQuery();
            String name = null;
            if (query != null && query.startsWith("name=")) {
                name = query.substring(5);
            }
            String response = app.greet(name) + "\n";
            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        server.createContext("/health", exchange -> {
            String response = "OK";
            byte[] bytes = response.getBytes();
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });

        server.setExecutor(null);
        System.out.println("Server starting on port 8080");
        server.start();
    }
}