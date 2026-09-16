package comp3011.assignment1.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    // Store the time when this controller is created.
    private final Instant serverStart = Instant.now();

    @GetMapping("/api/v1/admin/uptime")
    public Map<String, Object> getUptime() {

        Instant now = Instant.now();

        double uptimeSeconds =
                Duration.between(serverStart, now).toMillis() / 1000.0;

        Map<String, Object> response = new HashMap<>();

        response.put("utcServerStart", serverStart.toString());
        response.put("utcNow", now.toString());
        response.put("serverUptimeSeconds", uptimeSeconds);

        return response;
    }
}