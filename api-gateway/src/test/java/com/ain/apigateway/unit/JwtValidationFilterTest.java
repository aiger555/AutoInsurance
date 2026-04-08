package com.ain.apigateway.unit;

import com.ain.apigateway.filter.JwtValidationGatewayFilterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtValidationFilterTest {

    private JwtValidationGatewayFilterFactory filterFactory;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private GatewayFilterChain chain;

    private JwtValidationGatewayFilterFactory.Config config;

    @BeforeEach
    void setUp() {
        filterFactory = new JwtValidationGatewayFilterFactory(webClientBuilder);
        config = new JwtValidationGatewayFilterFactory.Config();
    }

    @Test
    void apply_FilterWithValidToken_ShouldContinueChain() {
        when(webClientBuilder.baseUrl("http://auth-service:4005")).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/validate")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(HttpHeaders.AUTHORIZATION, "Bearer valid_token")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
        when(chain.filter(any())).thenReturn(Mono.empty());

        GatewayFilter filter = filterFactory.apply(config);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid_token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void apply_FilterWithNoAuthHeader_ShouldReturnUnauthorized() {
        GatewayFilter filter = filterFactory.apply(config);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/clients")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void apply_FilterWithInvalidAuthHeaderFormat_ShouldReturnUnauthorized() {
        GatewayFilter filter = filterFactory.apply(config);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/clients")
                .header(HttpHeaders.AUTHORIZATION, "NotBearer token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void apply_FilterWithMissingBearerPrefix_ShouldReturnUnauthorized() {
        GatewayFilter filter = filterFactory.apply(config);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/clients")
                .header(HttpHeaders.AUTHORIZATION, "token_without_bearer")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        filter.filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}