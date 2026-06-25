package com.notifyflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// =============================================================================
// EDUCATIONAL NOTES — NotifyFlowApplication
// =============================================================================
//
// WHAT IS THIS FILE?
// This is the entry point of the entire Spring Boot application.
// Every Java program starts from a main() method — Spring Boot is no different.
// This class bootstraps the whole framework from a single line.
//
// -----------------------------------------------------------------------------
// @SpringBootApplication
// -----------------------------------------------------------------------------
// This is a composed annotation — it combines three annotations into one:
//
//   1. @Configuration
//      Marks this class as a source of Spring bean definitions.
//      Beans are objects managed by the Spring IoC container.
//
//   2. @EnableAutoConfiguration
//      Tells Spring Boot to automatically configure beans based on what JARs
//      are present on the classpath.
//      Example: if the PostgreSQL driver JAR is present, Spring will
//      automatically configure a DataSource without you writing any config.
//      This is the "convention over configuration" principle in action.
//
//   3. @ComponentScan
//      Tells Spring to scan this package (com.notifyflow) and ALL sub-packages
//      for classes annotated with @Component, @Service, @Repository,
//      @Controller, @RestController etc. and register them as beans.
//      This is why you never manually instantiate your services or repositories.
//
// WHY IS AUTO-CONFIGURATION POWERFUL?
// Without it, you would need hundreds of lines of XML or Java config to wire up
// a web server, database connection pool, JPA, JSON serialization, etc.
// Spring Boot detects the classpath and applies sensible defaults.
// You only override what you need in application.properties.
//
// -----------------------------------------------------------------------------
// @EnableScheduling
// -----------------------------------------------------------------------------
// Activates Spring's scheduled task execution infrastructure.
// Without this annotation, any method annotated with @Scheduled anywhere in
// the application will be silently ignored — no error, no background thread.
//
// This is intentional opt-in design: Spring does not start background threads
// unless you explicitly ask for it. This prevents unexpected CPU/memory usage
// in applications that don't need scheduling.
//
// In NotifyFlow, we need scheduling because the NotificationScheduler polls
// the database every 5 seconds to find and deliver due notifications.
//
// -----------------------------------------------------------------------------
// INVERSION OF CONTROL (IoC) AND DEPENDENCY INJECTION (DI)
// -----------------------------------------------------------------------------
// Traditional approach — you create your own dependencies:
//
//   NotificationRepository repo = new NotificationRepository();
//   NotificationService service = new NotificationService(repo);
//
// Problem: your class is tightly coupled to concrete implementations.
// Testing is hard because you can't easily swap in a mock repository.
//
// IoC approach — Spring creates and injects dependencies for you:
//
//   @Service
//   public class NotificationService {
//       public NotificationService(NotificationRepository repo) { ... }
//   }
//
// Spring sees that NotificationService needs a NotificationRepository,
// finds the bean registered for that type, and injects it automatically.
// Your class only declares WHAT it needs, not HOW to create it.
//
// Benefits:
//   - Loose coupling between components
//   - Easy to swap implementations (e.g. mock in tests)
//   - Spring manages object lifecycle (creation, destruction, scope)
//
// -----------------------------------------------------------------------------
// WHAT SpringApplication.run() DOES STEP BY STEP
// -----------------------------------------------------------------------------
//   1. Creates the Spring ApplicationContext (the IoC container)
//   2. Scans all packages and registers beans found by @ComponentScan
//   3. Runs all auto-configuration classes
//   4. Starts the embedded Tomcat HTTP server on port 8080
//   5. Publishes ApplicationReadyEvent — application is live
//
// -----------------------------------------------------------------------------
// SINGLE RESPONSIBILITY PRINCIPLE (SRP)
// -----------------------------------------------------------------------------
// This class has exactly one job: start the application.
// It contains zero business logic, zero configuration details, zero HTTP code.
// All other concerns live in their own dedicated classes.
// This is the S in SOLID — each class should have one reason to change.
//
// =============================================================================

@EnableScheduling
@SpringBootApplication
public class NotifyFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifyFlowApplication.class, args);
	}

}
