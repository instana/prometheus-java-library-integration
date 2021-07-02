Instana Integration for the Prometheus Java Library
===================================================

[Instana](https://instana.com) comes with [built-in support](https://www.instana.com/docs/) for the [Prometheus Java Client library](https://github.com/prometheus/client_java).
If your application uses the Prometheus Java Client library, you will see your Prometheus metrics on Instana's JVM dashboard.

Internally, the Prometheus Java Client library maintains a `CollectorRegistry` that keeps references to all metrics.
In order to find your metrics, Instana needs to find your `CollectorRegistry` instance.

If you use the Prometheus Java Client library's *default registry* you're good: Instana will discover the *default registry* automatically.

If you use a custom registry, you will need to include this little project here to give Instana a hint where to find your custom registry.

Background: Default Registry vs. Custom Registry
------------------------------------------------

The Prometheus Java Client library gives you two options how to register your metrics with the `CollectorRegistry`:

### Option 1: The default registry

If you just call `register()` to register your metrics, the Prometheus Java Client library will use the default registry.

```java
Counter.build()
    .name("counter")
    .help("example counter")
    .register();           // <-- registers your metric with the default CollectorRegistry
```

If you use the default registry, you don't need to do anything. Instana will detect the default registry automatically.

### Option 2: Custom registry

Alternatively, you can create a custom `CollectorRegistry` instance and pass it to the `register()` call:

```java
CollectorRegistry registry = ...;

Counter.build()
    .name("counter")
    .help("example counter")
    .register(registry);   // <-- registers your metric with your custom CollectorRegistry
```

If you use a custom registry, you need to give Instana a hint where to find your `CollectorRegistry` instance. This is the purpose of this library.

Integrating Your Custom Registry With Instana
---------------------------------------------

In order to integrate your custom `CollectorRegistry` with Instana, you first need to add this library as a Maven dependency:

```xml
<dependency>
  <groupId>com.instana</groupId>
  <artifactId>prometheus-java-library-integration</artifactId>
  <version>1.0.0</version>
</dependency>
```

Then, you need to call the following once at some point during your application initialization:

```java
Instana.add(registry);
```

That's it, now Instana will find your registry.

Integration with Micrometer's Prometheus Meter Registry
-------------------------------------------------------

If you use a framework like [Spring Boot](https://spring.io/projects/spring-boot) or [Quarkus](https://quarkus.io/) you might not use the Prometheus Java Client library directly.
Instead, you might use [Micrometer](https://micrometer.io) and Micrometer's [Prometheus Meter Registry](https://micrometer.io/docs/registry/prometheus) for exporting your Prometheus metrics.

Instana provides [built-in support for Micrometer](https://www.instana.com/docs/ecosystem/micrometer/) out-of-the-box. Micrometer metrics will automatically be included on your JVM dashboard.

However, if you want to see Prometheus metrics in exactly the same format and semantics that Micrometer's Prometheus Meter Registry produces, you can use this project.

### Example 1: Spring Boot with the Micrometer Prometheus Registry

Step 0: Add the `prometheus-java-library-integration` as a dependency:

```xml
<dependency>
    <groupId>com.instana</groupId>
    <artifactId>prometheus-java-library-integration</artifactId>
    <version>1.0.0</version>
</dependency>
```

Step 1: Change the scope of the `micrometer-registry-prometheus` dependency:

If you use Spring Boot with the Micrometer Prometheus registry, you already have the following dependency in your `pom.xml`:

```xml
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
  <scope>runtime</scope>
</dependency>
```

You have to remove the `runtime` scope to make this dependency available at compile time:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

Step 2: Initialize the Instana integration:

Spring Boot will provide a `PrometheusMeterRegistry` object via dependency injection. Use this to initialize the Instana integration:

```java
@Service
public class InstanaInitializer {

    private final Optional<PrometheusMeterRegistry> registry;

    public InstanaInitializer(Optional<PrometheusMeterRegistry> registry) {
        this.registry = registry;
    }

    @PostConstruct
    void init() {
        registry.map(PrometheusMeterRegistry::getPrometheusRegistry).ifPresent(Instana::add);
    }
}
```

### Example 2: Quarkus with the Micrometer Prometheus Registry

Step 0: Add the `prometheus-java-library-integration` as a dependency:

```xml
<dependency>
    <groupId>com.instana</groupId>
    <artifactId>prometheus-java-library-integration</artifactId>
    <version>1.0.0</version>
</dependency>
```

Step 1: Initialize the Instana integration

Just like Spring Boot, Quarkus provides Micrometer's `PrometheusMeterRegistry` via dependency injection.
Use this registry to initialize Instana:

```java
@ApplicationScoped
public class InstanaInitializer {

  @Inject
  PrometheusMeterRegistry registry;

  void onContainerInitialized(@Observes StartupEvent event) {
    Instana.add(registry.getPrometheusRegistry());
  }
}
```