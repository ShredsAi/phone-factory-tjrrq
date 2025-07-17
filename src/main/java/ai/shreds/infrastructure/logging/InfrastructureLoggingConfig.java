package ai.shreds.infrastructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Configuration class for logging in the infrastructure layer.
 * Provides beans for creating loggers and configuring logging behavior.
 */
@Configuration
public class InfrastructureLoggingConfig {

    /**
     * Creates a logger instance for each bean that needs one.
     * The logger name will be based on the class into which the logger is injected.
     *
     * @param injectionPoint the point where the logger will be injected
     * @return a logger for the class requesting it
     */
    @Bean
    @Scope("prototype")
    public Logger logger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(
                injectionPoint.getMethodParameter() != null
                        ? injectionPoint.getMethodParameter().getContainingClass()
                        : injectionPoint.getField().getDeclaringClass());
    }
    
    /**
     * Creates a logger for infrastructure repository operations.
     * This allows specialized logging for database operations.
     *
     * @return a logger for repository operations
     */
    @Bean(name = "repositoryLogger")
    public Logger repositoryLogger() {
        return LoggerFactory.getLogger("ai.shreds.infrastructure.repositories");
    }
    
    /**
     * Creates a logger for external service communications.
     * This allows specialized logging for interactions with external systems.
     *
     * @return a logger for external service operations
     */
    @Bean(name = "externalServiceLogger")
    public Logger externalServiceLogger() {
        return LoggerFactory.getLogger("ai.shreds.infrastructure.external_services");
    }
    
    /**
     * Creates a logger for Kafka operations.
     * This allows specialized logging for Kafka producer and consumer operations.
     *
     * @return a logger for Kafka operations
     */
    @Bean(name = "kafkaLogger")
    public Logger kafkaLogger() {
        return LoggerFactory.getLogger("ai.shreds.infrastructure.kafka");
    }
    
    /**
     * Creates a logger for security operations.
     * This allows specialized logging for authentication and authorization operations.
     *
     * @return a logger for security operations
     */
    @Bean(name = "securityLogger")
    public Logger securityLogger() {
        return LoggerFactory.getLogger("ai.shreds.infrastructure.security");
    }
    
    /**
     * Creates a logger for mapping operations between domain and persistence models.
     * This allows specialized logging for entity mapping operations.
     *
     * @return a logger for entity mapping operations
     */
    @Bean(name = "mapperLogger")
    public Logger mapperLogger() {
        return LoggerFactory.getLogger("ai.shreds.infrastructure.mapper");
    }
}