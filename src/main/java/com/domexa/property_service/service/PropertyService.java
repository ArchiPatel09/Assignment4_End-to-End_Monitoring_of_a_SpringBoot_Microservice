package com.domexa.property_service.service;

import com.domexa.property_service.model.Property;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PropertyService {

    private final Map<String, Property> properties = new HashMap<>();

    public PropertyService()
    {
        properties.put("P1", new Property("P1", "330 Bluevale Street", "Archi", 1900.0, true, "Town House"));
        properties.put("P2", new Property("P2", "110 Pinnacle Dr", "Nishant", 1800.0, false, "Apartment"));
        properties.put("P3", new Property("P3", "49 George Brier Dr", "Viveka", 2900.0, true, "Condo"));
        properties.put("P4", new Property("P4", "308 King Street N", "Nishva", 3900.0, true, "House"));
    }

    public List<Property> getAll() {
        simulateLatency(10, 80);
        return new ArrayList<>(properties.values());
    }

    public Optional<Property> getById(String id) {
        simulateLatency(5, 50);

        if (ThreadLocalRandom.current().nextInt(10) == 0) {
            simulateLatency(500, 1000);
        }

        return Optional.ofNullable(properties.get(id));
    }

    public Property create(Property property) {
        if (property.getId() == null || property.getId().isBlank()) {
            property.setId("P" + (properties.size() + 1));
        }
        properties.put(property.getId(), property);
        return property;
    }

    public boolean delete(String id) {
        return properties.remove(id) != null;
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
