package com.gateway.sever;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }


    /**
     *
     * @param locatorBuilder
     * @return  it is provide a custom route for api gateway and also counfigure resilance4j library in api gateway
     *
     * in circuitebreaker provide fallback controller after open circute
     *
     */
    @Bean
    public RouteLocator sbiBankLocator(RouteLocatorBuilder locatorBuilder) {
        return locatorBuilder.routes().route(
                        p -> p.path("/sbibank/account/**")
                                .filters(f -> f.rewritePath("/sbibank/account/(?<segment>.*)", "/${segment}").addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                        .circuitBreaker(config -> config.setName("accountsCircuitBreaker")
                                                .setFallbackUri("forward:/contactSupport"))
                                )
                                .uri("lb://ACCOUNT"))
                .route(p -> p
                        .path("/sbibank/loan/**")
                        .filters(f -> f.rewritePath("/sbibank/loan/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .retry(retryConfig -> retryConfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))
                        )
                        .uri("lb://LOAN"))
                .route(p -> p
                        .path("/sbibank/cards/**")
                        .filters(f -> f.rewritePath("/sbibank/cards/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://cards")).build();


    }


    /**
     *
     *
     * @return ==>method i return timeout duration for the response if server is not response in time throw a timeout exception
     */

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
    }


    /**
     *   this methode put redis Rate Limiter Configration
     * @return
     */
    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }

    /**
     * this methode provide header for ratelimiter
     * @return
     */
    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
                .defaultIfEmpty("anonymous");
    }
}
