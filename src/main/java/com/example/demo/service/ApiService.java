package com.example.demo.service;

import java.time.Duration;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.model.CsvRow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiService {

    private final WebClient webClient;

    public Mono<Integer> callApi(CsvRow row) {

        return webClient.post()
                .uri("https://postman-echo.com/post") // test endpoint
                .header("X-Source", "CSV-UPLOAD")
                .bodyValue(Map.of(
                        "field1", row.getCol1(),
                        "field2", row.getCol2()
                ))
                .exchangeToMono(res -> {
                    int status = res.statusCode().value();
                    log.info("Row {} -> {}", row.getCol1(), status);
                    return Mono.just(status);
                })

                .timeout(Duration.ofSeconds(5))

                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .doBeforeRetry(sig ->
                                log.warn("Retry {} for {}",
                                        sig.totalRetries(), row.getCol1()))
                )

                .onErrorResume(ex -> {
                    log.error("Failed for {}", row.getCol1(), ex);
                    return Mono.just(500);
                });
    }
}