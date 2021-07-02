/*
 * (c) Copyright IBM Corp. 2021
 * (c) Copyright Instana Inc.
 */
package com.instana.prometheus;

import io.prometheus.client.CollectorRegistry;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Instana {

    public static final Set<CollectorRegistry> registries = new CopyOnWriteArraySet<CollectorRegistry>();

    public static void add(CollectorRegistry registry) {
        registries.add(registry);
    }
}
