package ai.shreds.infrastructure.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database configuration for the procurement workflow application.
 * Configures JPA repositories, entity scanning, and transaction management.
 */
@Configuration
@EnableJpaRepositories(basePackages = "ai.shreds.infrastructure.repositories")
@EntityScan(basePackages = "ai.shreds.infrastructure.repositories")
@EnableTransactionManagement
@EnableJpaAuditing
public class InfrastructureDatabaseConfig {
    // Spring Boot auto-configuration handles most of the database setup based on application.yml properties
    // This class primarily serves to define scan packages for repositories and entities
    // Additional custom bean definitions or database configuration can be added here if needed
}