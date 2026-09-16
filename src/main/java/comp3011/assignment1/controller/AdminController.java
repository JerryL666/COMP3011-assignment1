package comp3011.assignment1.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private final Instant serverStart = Instant.now();

    private final ConfigurableApplicationContext applicationContext;

    private final AtomicBoolean shutdownStarted =
            new AtomicBoolean(false);


    public AdminController(
            ConfigurableApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }


    @GetMapping("/api/v1/admin/uptime")
    public Map<String, Object> getUptime() {

        Instant now = Instant.now();

        double uptimeSeconds =
                Duration.between(serverStart, now).toMillis()
                / 1000.0;

        Map<String, Object> response = new HashMap<>();

        response.put("utcServerStart", serverStart.toString());
        response.put("utcNow", now.toString());
        response.put("serverUptimeSeconds", uptimeSeconds);

        return response;
    }


    @PostMapping("/api/v1/admin/shutdown")
    public ResponseEntity<Map<String, Object>> shutdown() {

        if (!shutdownStarted.compareAndSet(false, true)) {

            Map<String, Object> error = new HashMap<>();

            error.put("timestamp", Instant.now().toString());
            error.put("status", 409);
            error.put("error", "Conflict");
            error.put(
                    "message",
                    "Graceful shutdown is already in progress.");
            error.put(
                    "path",
                    "/api/v1/admin/shutdown");

            return ResponseEntity
                    .status(409)
                    .body(error);
        }

        Map<String, Object> response = new HashMap<>();

        response.put(
                "message",
                "Graceful shutdown requested.");

        startShutdown();

        return ResponseEntity
                .status(202)
                .body(response);
    }


    private void startShutdown() {

        Thread shutdownThread = new Thread(() -> {

            try {
                // Give the HTTP response time to be sent.
                Thread.sleep(200);
            }
            catch (InterruptedException error) {
                Thread.currentThread().interrupt();
            }

            applicationContext.close();
        });

        shutdownThread.start();
    }
}