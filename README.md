# Domexa Rentals — Observability Assignment (PROG3360)
# Team #01   Property Service   Spring Boot + Prometheus + Grafana + Zipkin
# Project Overview: This project implements full observability for the Domexa Rentals Property Service — a Spring Boot microservice that manages rental property listings. The observability stack includes:

# "Our service is the Domexa Rentals Property Service — a Spring Boot REST API that manages property listings. We've built a full observability stack around it. The service exposes metrics through Actuator, which Prometheus scrapes every 10 seconds. Grafana queries Prometheus to visualize those metrics as dashboards. For distributed tracing, we use Micrometer Tracing with a Zipkin exporter — every HTTP request automatically gets a trace ID that we can look up in Zipkin."

* Spring Boot Property Service - The microservice being monitored - 8081
* Spring Boot Actuator - Exposes health, metrics, info endpoints - 8081/actuator
* Micrometer + Prometheus Registry - Formats metrics for Prometheus - 8081/actuator/prometheus
* Prometheus - Scrapes and stores time-series metrics - 9090
* Grafana - Visualizes metrics as dashboards - 3000
* Micrometer Tracing + Zipkin - Distributed tracing - 9411

### Step 1: Clean build and start the service
- \mvnw clean spring-boot:run

### Step 2: In another terminal — Start Prometheus, Grafana, and Zipkin
- docker compose up -d
- docker compose ps

Tabs to open before presentation
- Tab 1: http://localhost:8081/actuator/health
- Tab 2: http://localhost:8081/actuator/info
- Tab 3: http://localhost:8081/actuator/prometheus
- Tab 4: http://localhost:9090/targets (Prometheus Targets)
- Tab 5: http://localhost:3000 (Grafana — navigate to Service Overview dashboard)
- Tab 6: http://localhost:9411 (Zipkin)

### Step 3: Verify each service is reachable and working
- Invoke-RestMethod http://localhost:8081/actuator/health
- Invoke-RestMethod http://localhost:8081/actuator/prometheus
- Start-Process "http://localhost:9090"
- Start-Process "http://localhost:3000"
- Start-Process "http://localhost:9411"

### Step 4: Spring Boot Microservice

Get all properties (seeded data)
- Invoke-RestMethod http://localhost:8081/properties

Get a single property
- Invoke-RestMethod http://localhost:8081/properties/P1

Create a new property
- body = '"id":"P5","address":"10 Main St","owner":"Eve","rent":1900,"available":true,"type":"Condo"'
- Invoke-RestMethod -Uri http://localhost:8081/properties -Method POST -Body body -ContentType "application/json"

Delete a property
- Invoke-RestMethod -Uri http://localhost:8081/properties/P5 -Method DELETE

Trigger the slow endpoint (2-second delay — used for latency demos)
- Invoke-RestMethod http://localhost:8081/properties/slow

Trigger an error (used for error rate demos)
- try { Invoke-RestMethod http://localhost:8081/properties/error } catch { .Exception.Message }

### Step 5: Spring Boot Actuator

Health endpoint — shows UP
- Invoke-RestMethod http://localhost:8081/actuator/health

Info endpoint — shows your custom metadata (team name, version, etc.)
- Invoke-RestMethod http://localhost:8081/actuator/info

Metrics endpoint — lists all available metrics
- Invoke-RestMethod http://localhost:8081/actuator/metrics

Prometheus endpoint — the raw text Prometheus scrapes
- Invoke-RestMethod http://localhost:8081/actuator/prometheus

See your custom metric specifically
- Invoke-RestMethod "http://localhost:8081/actuator/metrics/domexa.property.created.count"

Open this in the browser:
- http://localhost:8081/actuator/health
- http://localhost:8081/actuator/info
- http://localhost:8081/actuator/prometheus

### Step 6 — Prometheus Integration

- pen http://localhost:9090
- Run the following queries:

JVM heap memory used
- jvmmemoryusedbytesarea="heap"

HTTP request count rate (requests per second)
- rate(httpserverrequestssecondscount[1m])

Your custom business metric
- domexapropertycreatedcounttotal

Active properties gauge
- domexapropertyactivecount

### Step 7: Grafana Dashboards

- http://localhost:3000
- Login with admin / admin
- Show connections and all 4 dashboards

### Step 8: Zipkin Distributed Tracing

Generate traces by hitting your endpoints:

- Invoke-RestMethod http://localhost:8081/properties
- Invoke-RestMethod http://localhost:8081/properties/P1
- Invoke-RestMethod http://localhost:8081/properties/P2

- body = '{"id":"P6","address":"5 Park Blvd","owner":"Frank","rent":2500,"available":true,"type":"House"}'
- Invoke-RestMethod -Uri http://localhost:8081/properties -Method POST -Body body -ContentType "application/json"

- Invoke-RestMethod http://localhost:8081/properties/P6
- Invoke-RestMethod -Uri http://localhost:8081/properties/P6 -Method DELETE

- Invoke-RestMethod http://localhost:8081/properties/slow

- try  Invoke-RestMethod http://localhost:8081/properties/error  catch {}

- Invoke-RestMethod http://localhost:8081/properties
- Invoke-RestMethod http://localhost:8081/properties/P1
- Invoke-RestMethod http://localhost:8081/properties/P3

# View traces in Zipkin:
Open http://localhost:9411
Click Run Query

### Step 9: Generate traffic so dashboards have data to show:

Loop that hits your service every second for 3 minutes
for (i = 0; i -lt 180; i++) {
try {
Invoke-RestMethod http://localhost:8081/properties   Out-Null
Invoke-RestMethod http://localhost:8081/properties/P1   Out-Null
if (i % 5 -eq 0) {
body = "{"id":"LOADi","address":"i Test St","owner":"Test","rent":1500,"available":true,"type":"Condo`"}"
Invoke-RestMethod -Uri http://localhost:8081/properties -Method POST -Body body -ContentType "application/json"   Out-Null
}
if (i % 10 -eq 0) {
try { Invoke-RestMethod http://localhost:8081/properties/slow   Out-Null } catch {}
}
} catch {}
Start-Sleep -Seconds 1
Write-Host "Request i of 180 sent"
}

### Step 10: Generating Zipkin Traces

- Generate 10 traces quickly right before your Zipkin demo
1..10   ForEach-Object {
Invoke-RestMethod http://localhost:8081/properties   Out-Null
Invoke-RestMethod "http://localhost:8081/properties/P(Get-Random -Minimum 1 -Maximum 4)"   Out-Null
Write-Host "Trace $ sent"
Start-Sleep -Milliseconds 500
}

- Invoke-RestMethod http://localhost:8081/properties/slow

### What Metrics Revealed
* Slow Endpoint Impact
* Memory Stability
* Business Patterns
* Error Correlation

### How Tracing Helped Diagnose Issues
* Latency Breakdown
* Trace Visibility
* Faster Debugging


### Team Reflection

- Archi Patel -	Service implementation, endpoints, latency/errors
- Sahil & Annlin: Prometheus, Grafana, Zipkin, custom metrics
- Carlos: Load testing, verification, coverage

### Challenges & Solutions
- Zipkin no traces:	Added micrometer-tracing-bridge-brave dependency

### Lessons Learned
- Observability is layered 
    - Each component (Actuator → Prometheus → Grafana) adds unique value

- Start simple — Build service first, then add monitoring layer by layer

- One missing endpoint can break the entire chain (small endpoint can break everything)

- Document everything — Verification steps helped reproduce environment consistently
