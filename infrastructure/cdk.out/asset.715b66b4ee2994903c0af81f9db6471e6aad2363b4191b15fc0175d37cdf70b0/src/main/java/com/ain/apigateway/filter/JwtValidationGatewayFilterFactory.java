package com.ain.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtValidationGatewayFilterFactory.Config> {

    private final WebClient webClient;

    public JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        // ИСПРАВЬ ЗДЕСЬ: используй имя сервиса Docker вместо localhost
        this.webClient = webClientBuilder.baseUrl("http://auth-service:4005").build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            System.out.println("🔐 JWT FILTER: Processing request to " + exchange.getRequest().getURI());
            System.out.println("🔐 JWT FILTER: Auth Header = " + authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("❌ JWT FILTER: No Bearer token found");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            System.out.println("✅ JWT FILTER: Bearer token found, validating...");

            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> {
                                System.out.println("❌ JWT FILTER: Token validation failed with status: " + response.statusCode());
                                return Mono.error(new RuntimeException("Token validation failed"));
                            }
                    )
                    .bodyToMono(Void.class)
                    .then(chain.filter(exchange))
                    .onErrorResume(e -> {
                        System.out.println("❌ JWT FILTER: Error: " + e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    public static class Config {
        // Конфигурация
    }
}