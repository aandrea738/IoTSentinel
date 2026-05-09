package it.unibas.tav.iotsentinel.filter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class SensorDataFilter {
    private final List<SensorData> data;

    public SensorDataFilter(List<SensorData> data) {
        this.data = data;
    }

    public List<SensorData> filterByTimeRange(Instant start, Instant end) {
        return data.stream()
                .filter(item -> !item.timestamp().isBefore(start) && !item.timestamp().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<SensorData> filterBySensorType(String type) {
        return data.stream()
                .filter(item -> item.sensorType().equals(type))
                .collect(Collectors.toList());
    }

    public List<SensorData> filterByThreshold(double threshold, String metric) {
        return data.stream()
                .filter(item -> {
                    switch (metric.toLowerCase()) {
                        case "temperature":
                            return item.temperature() > threshold;
                        case "pressure":
                            return item.pressure() > threshold;
                        case "vibration":
                            return item.vibration() > threshold;
                        default:
                            return false;
                    }
                })
                .collect(Collectors.toList());
    }
}

record SensorData(
        Instant timestamp,
        double temperature,
        double pressure,
        double vibration,
        String sensorType) {
}
