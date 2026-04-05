package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.CsvRow;
import com.example.demo.service.CsvService;
import com.example.demo.service.ProcessingService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;
    private final ProcessingService processingService;

    private List<CsvRow> processed;

    @GetMapping("/")
    public String home() {
        return "upload";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         Model model) throws Exception {

        List<CsvRow> rows = csvService.read(file.getInputStream());

        processed = processingService.process(rows).collectList().block();

        model.addAttribute("rows", processed);
        return "result";
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download() throws Exception {

        ByteArrayInputStream csv = csvService.write(processed);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=result.csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(csv));
    }
}