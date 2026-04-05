package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.CsvRow;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final ApiService apiService;

    public Flux<CsvRow> process(List<CsvRow> rows) {

        return Flux.fromIterable(rows)
                .flatMap(row ->
                        apiService.callApi(row)
                                .map(status -> {
                                    row.setStatus(status);
                                    return row;
                                }),
                        20 // concurrency limit
                );
    }
}