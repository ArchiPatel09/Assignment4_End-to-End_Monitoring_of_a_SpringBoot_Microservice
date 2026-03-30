package com.domexa.property_service.controller;

import com.domexa.property_service.metrics.PropertyMetrics;
import com.domexa.property_service.model.Property;
import com.domexa.property_service.service.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyController {
    private final PropertyService service;
    private final PropertyMetrics metrics;

    public PropertyController(PropertyService service, PropertyMetrics metrics) {
        this.service = service;
        this.metrics = metrics;
    }

    @GetMapping
    public List<Property> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getById(@PathVariable String id) {
        return metrics.getPropertyLookupTimer().record(() ->
                service.getById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build())
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Property create(@RequestBody Property property) {
        metrics.incrementCreated();
        return service.create(property);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (service.delete(id)) {
            metrics.incrementDeleted();
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/slow")
    public String slowEndpoint() throws InterruptedException {
        Thread.sleep(2000);
        metrics.incrementSlow();
        return "This response was intentionally delayed by 2 seconds.";
    }

    @GetMapping("/error")
    public String errorEndpoint() {
        throw new RuntimeException("Simulated error for observability demo!");
    }
}
