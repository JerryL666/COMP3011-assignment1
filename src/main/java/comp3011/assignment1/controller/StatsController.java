package comp3011.assignment1.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import comp3011.assignment1.service.StatisticsService;

@RestController
public class StatsController {

    private final StatisticsService statisticsService;

    public StatsController(
            StatisticsService statisticsService) {

        this.statisticsService = statisticsService;
    }

    @GetMapping("/api/v1/global/stats")
    public Map<String, Long> getGlobalStats() {

        Map<String, Long> response = new HashMap<>();

        response.put("inputTokens", statisticsService.getInputTokens());
        response.put("outputTokens", statisticsService.getOutputTokens());

        return response;
    }
}