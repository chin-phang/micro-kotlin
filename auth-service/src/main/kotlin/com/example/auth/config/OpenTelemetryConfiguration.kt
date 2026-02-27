package com.example.auth.config

import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmClassLoadingMeterConventions
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmCpuMeterConventions
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmMemoryMeterConventions
import io.micrometer.core.instrument.binder.jvm.convention.otel.OpenTelemetryJvmThreadMeterConventions
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention


@Configuration(proxyBeanMethods = false)
class OpenTelemetryConfiguration {
    @Bean
    fun openTelemetryServerRequestObservationConvention() : DefaultServerRequestObservationConvention {
        return DefaultServerRequestObservationConvention()
    }

    @Bean
    fun openTelemetryJvmCpuMeterConventions() : OpenTelemetryJvmCpuMeterConventions {
        return OpenTelemetryJvmCpuMeterConventions(Tags.empty())
    }

    @Bean
    fun processorMetrics() : ProcessorMetrics {
        return ProcessorMetrics(emptyList<Tag>(), OpenTelemetryJvmCpuMeterConventions(Tags.empty()))
    }

    @Bean
    fun jvmMemoryMetrics() : JvmMemoryMetrics {
        return JvmMemoryMetrics(emptyList<Tag>(), OpenTelemetryJvmMemoryMeterConventions(Tags.empty()))
    }

    @Bean
    fun jvmThreadMetrics() : JvmThreadMetrics {
        return JvmThreadMetrics(emptyList<Tag>(), OpenTelemetryJvmThreadMeterConventions(Tags.empty()))
    }

    @Bean
    fun classLoaderMetrics() : ClassLoaderMetrics {
        return ClassLoaderMetrics(OpenTelemetryJvmClassLoadingMeterConventions())
    }
}