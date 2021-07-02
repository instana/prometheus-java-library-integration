/*
 * (c) Copyright IBM Corp. 2021
 * (c) Copyright Instana Inc.
 */
package com.instana.prometheus;

import io.prometheus.client.CollectorRegistry;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Global entrypoint for Instana's Prometheus Java library integration.
 */
public class Instana {

    /**
     * Set of custom {@link CollectorRegistry} instances to be monitored by Instana.
     */
    public static final Set<CollectorRegistry> included = new CopyOnWriteArraySet<CollectorRegistry>();

    /**
     * Set of {@link CollectorRegistry} instances that will not be monitored by Instana.
     * This can be used to prevent Instana from monitoring the {@link CollectorRegistry#defaultRegistry defaultRegistry}.
     */
    public static final Set<CollectorRegistry> excluded = new CopyOnWriteArraySet<CollectorRegistry>();

    /**
     * Used by Instana to learn the version of this library.
     */
    public static final int version = 1;

    /**
     * Register a custom {@link CollectorRegistry} for Instana monitoring.
     * <p/>
     * You don't need to call this for the {@link CollectorRegistry#defaultRegistry defaultRegistry},
     * as Instana discovers the {@link CollectorRegistry#defaultRegistry defaultRegistry} automatically.
     * <p/>
     * If you call {@link #include(CollectorRegistry) include()} and {@link #exclude(CollectorRegistry) exclude()}
     * for the same registry, {@link #exclude(CollectorRegistry) exclude()} wins and the registry will not
     * be monitored.
     * @param registry a custom {@link CollectorRegistry}.
     */
    public static void include(CollectorRegistry registry) {
        included.add(registry);
    }

    /**
     * Exclude a {@link CollectorRegistry} from being monitored by Instana.
     * <p/>
     * Instana monitors the {@link CollectorRegistry#defaultRegistry defaultRegistry} automatically.
     * To prevent this, call
     * <pre>
     *     Instana.exclude{CollectorRegistry.defaultRegistry}
     * </pre>
     * If you call {@link #include(CollectorRegistry) include()} and {@link #exclude(CollectorRegistry) exclude()}
     * for the same registry, {@link #exclude(CollectorRegistry) exclude()} wins and the registry will not
     * be monitored.
     * @param registry a {@link CollectorRegistry} that will be excluded from Instana monitoring.
     */
    public static void exclude(CollectorRegistry registry) {
        excluded.add(registry);
    }
}
