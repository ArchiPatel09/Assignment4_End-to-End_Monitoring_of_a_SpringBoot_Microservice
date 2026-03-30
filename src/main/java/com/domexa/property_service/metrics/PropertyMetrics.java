package com.domexa.property_service.metrics;

import com.domexa.property_service.service.PropertyService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class PropertyMetrics {

    private final Counter propertyCreatedCounter;
    private final Counter propertyDeletedCounter;
    private final Counter slowRequestCounter;
    private final Timer propertyLookupTimer;

    private final AtomicInteger activePropertyCount = new AtomicInteger();

    public PropertyMetrics(MeterRegistry registry, PropertyService propertyService) {

        this.propertyCreatedCounter = Counter.builder("domexa.property.created.count")
                .description("Total number of properties created")
                .tag("service", "property-service")
                .register(registry);

        this.propertyDeletedCounter = Counter.builder("domexa.property.deleted.count")
                .description("Total number of properties deleted")
                .tag("service", "property-service")
                .register(registry);

        this.slowRequestCounter = Counter.builder("domexa.property.slow.requests")
                .description("Number of requests exceeding 500ms")
                .register(registry);

        this.propertyLookupTimer = Timer.builder("domexa.property.lookup.duration")
                .description("Time taken to look up a property")
                .tag("service", "property-service")
                .publishPercentileHistogram()   // 🔥 ADD THIS
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        this.activePropertyCount.set(propertyService.getAll().size());

        Gauge.builder("domexa.property.active.count", activePropertyCount, AtomicInteger::get)
                .description("Current number of active properties in the system")
                .register(registry);
    }

    public void incrementCreated() {
        propertyCreatedCounter.increment();
        activePropertyCount.incrementAndGet();
    }

    public void incrementDeleted() {
        propertyDeletedCounter.increment();
        activePropertyCount.decrementAndGet();
    }

    public void incrementSlow() {
        slowRequestCounter.increment();
    }

    public Timer getPropertyLookupTimer() {
        return propertyLookupTimer;
    }
}