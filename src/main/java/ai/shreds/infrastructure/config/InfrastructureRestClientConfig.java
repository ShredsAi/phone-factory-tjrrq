package ai.shreds.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for REST client components.
 * Configures RestTemplate with appropriate timeouts, connection settings, and circuit breakers.
 */
@Configuration
public class InfrastructureRestClientConfig {

    /**
     * Configures RestTemplate with optimal settings for external service calls
     * @param builder RestTemplate builder
     * @return configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .requestFactory(this::clientHttpRequestFactory)
                .messageConverters(getMessageConverters())
                .interceptors(getClientInterceptors())
                .build();
    }

    /**
     * Configures HTTP client request factory with connection pooling
     * @return ClientHttpRequestFactory
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds
        factory.setReadTimeout(30000);    // 30 seconds
        factory.setConnectionRequestTimeout(5000); // 5 seconds
        return factory;
    }

    /**
     * Configure message converters for JSON and text responses
     * @return list of message converters
     */
    private List<org.springframework.http.converter.HttpMessageConverter<?>> getMessageConverters() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON
        ));

        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setSupportedMediaTypes(Arrays.asList(
                MediaType.TEXT_PLAIN,
                MediaType.TEXT_HTML,
                MediaType.APPLICATION_XML
        ));

        return Arrays.asList(jsonConverter, stringConverter);
    }

    /**
     * Configure request interceptors for logging and headers
     * @return list of client interceptors
     */
    private List<ClientHttpRequestInterceptor> getClientInterceptors() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        
        // Add a logging interceptor
        interceptors.add((request, body, execution) -> {
            // Log request details if needed
            return execution.execute(request, body);
        });
        
        // Add common headers interceptor
        interceptors.add((request, body, execution) -> {
            request.getHeaders().set("X-Application-Name", "procurement-workflow");
            return execution.execute(request, body);
        });
        
        return interceptors;
    }

    /**
     * Configure circuit breaker factory with default configurations
     */
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCircuitBreakerFactoryCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(30)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50) // 50% failure rate to open circuit
                        .waitDurationInOpenState(Duration.ofSeconds(60)) // Wait 1 minute in open state before trying again
                        .permittedNumberOfCallsInHalfOpenState(5) // Allow 5 calls in half-open state
                        .slidingWindowSize(10) // Consider last 10 calls for failure rate
                        .minimumNumberOfCalls(5) // Minimum calls before calculating failure rate
                        .build())
                .build());
    }

    /**
     * RestTemplate builder with custom configurations
     * @return RestTemplateBuilder
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .detectRequestFactory(false);
    }
}