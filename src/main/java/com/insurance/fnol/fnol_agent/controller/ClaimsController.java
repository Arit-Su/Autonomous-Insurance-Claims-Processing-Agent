package com.insurance.fnol.fnol_agent.controller;

import com.insurance.fnol.fnol_agent.model.ClaimData;
import com.insurance.fnol.fnol_agent.model.ProcessedClaimResult;
import com.insurance.fnol.fnol_agent.service.ClaimExtractor;
import com.insurance.fnol.fnol_agent.service.RulesEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/claims")
@CrossOrigin(origins = "*")
public class ClaimsController {

    private final ClaimExtractor extractor;
    private final RulesEngineService rulesEngine;

    public ClaimsController(ClaimExtractor extractor, RulesEngineService rulesEngine) {
        this.extractor = extractor;
        this.rulesEngine = rulesEngine;
    }


//      Processes a PDF FNOL document.

    @Operation(summary = "Upload and process an FNOL PDF document")
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProcessedClaimResult> processClaim(
            @RequestPart("file") MultipartFile file // Using @RequestPart is more robust for file uploads
    ) throws IOException {

        // file validation
        if (file.isEmpty() || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Invalid file. Please upload a valid PDF document.");
        }

        //  Extract Text
        String rawText = parsePdf(file);

        ClaimData claimData = extractor.extract(rawText);

        ProcessedClaimResult result = rulesEngine.process(claimData);

        return ResponseEntity.ok(result);
    }

    private String parsePdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }
}