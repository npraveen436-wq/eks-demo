package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App {

    // Plain method kept so the existing unit tests still work unchanged.
    public String greet(String name) {
        if (name == null || name.isBlank()) {
            return "Hello, stranger";
        }
        return "Hello, " + name;
    }

    // GET /greet?name=Alice  ->  "Hello, Alice"
    @GetMapping("/greet")
    public String greetEndpoint(@RequestParam(name = "name", required = false) String name) {
        return greet(name);
    }

    // GET /health  ->  "OK"  (used by the k8s liveness/readiness probes and smoke test)
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
