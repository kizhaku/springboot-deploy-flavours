# springboot-deploy-flavours

## About the app

- A further enhancement to the Spring Boot app running with different flavours of deployment. Can be used as a template.
- Uses Prometheus and Loki for health metrics, alerts, and logs collection.
- Uses Grafana for metrics and logs visualization.
- For Kubernetes deployment refer: (/infra/kubernetes/DEPLOYMENT.md)

#### Dependencies

Add the following dependencies for actuator and micrometer-prometheus:

```xml
<!-- Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- Prometheus registry -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

#### Enable actuator for health and prometheus endpoints

Add the following properties:

```properties
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.show-details=always
```

- Health info available at: `http://localhost:8080/actuator/health`
- Prometheus metrics available at: `http://localhost:8080/actuator/prometheus`

### Prometheus

- Go to [https://prometheus.io/download/](https://prometheus.io/download/) to download the correct binaries for your machine and unzip.
- Create `prometheus.yml` with the scrape configuration.
- Within the Prometheus directory, run the executable:

```bash
./prometheus --config.file=<<absolute path to prometheus.yml>>
```

- Prometheus dashboard can be viewed at [http://localhost:9090/](http://localhost:9090/)
    - Within the dashboard you can query using PromQL expressions, for example:

```promql
rate(http_server_requests_seconds_count[5m])
http_server_requests_seconds_count{job="spring-aop-app", status="404"}[10m]
```

- (Second query: Check 404 errors with given job name.)
- Alerts:
    - You can view alerts under [http://localhost:9090/alerts](http://localhost:9090/alerts)
    - Alerts can be set up by adding a `rules.yml` and providing its reference under `rule_files` in `prometheus.yml`.
    - Check `prometheus.yml` and `rules.yml` under the `observability` directory.
    - Alerts can also be configured to send notification emails etc. This can be done by installing the Alertmanager.

### Promtail & Loki

- Promtail will scrape the logs at `logs/app.log` and push to Loki.
- Download and install Loki and Promtail by following instructions at [https://grafana.com/docs/loki/latest/setup/install/local/](https://grafana.com/docs/loki/latest/setup/install/local/)
- Create the `loki.yml` and `promtail.yml`. Refer to the `observability` directory.
- From the Loki executable location run:

```bash
./loki --config.file=<<absolute path to loki-config.yml>>
```

- Loki status can be checked at [http://localhost:3100/ready](http://localhost:3100/ready)
- Create the `promtail.yml`. This will define the scrape job, the logs path, the label, and the Loki endpoint to push to. Refer to the `observability` directory.
- From the Promtail executable location run:

```bash
./promtail --config.file=<<absolute path to promtail.yml>>
```

### Grafana

- Install and run Grafana following the instructions at [https://grafana.com/docs/grafana/latest/setup-grafana/installation/](https://grafana.com/docs/grafana/latest/setup-grafana/installation/). On Mac, Grafana can be installed through Homebrew.
- On Mac, run Grafana as:

```bash
brew services start grafana
```

- Access Grafana at [http://localhost:3000/](http://localhost:3000/)
- Go to Data Sources and add both Prometheus and Loki. Provide the local URL for both services.
- Once added, go to Explore from the side menu:
    - From the dashboard, select Prometheus to view time series metrics, queries, etc.
    - From the dashboard, select Loki. You can provide app name, job name, etc. to pull the logs. Further drill down can be done using queries.

## Links

- Prometheus: [http://localhost:9090/alerts](http://localhost:9090/alerts)
- Actuator: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
- Grafana: [http://localhost:3000/](http://localhost:3000/)
- Loki: [http://localhost:3100/ready](http://localhost:3100/ready)
