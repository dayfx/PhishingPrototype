package at.daniel.phishingprototype.controller;

import at.daniel.phishingprototype.entity.GenerationRecord;
import at.daniel.phishingprototype.repository.GenerationRecordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class HistoryController {

    private final GenerationRecordRepository
            generationRecordRepository;


    public HistoryController(
            GenerationRecordRepository generationRecordRepository) {

            this.generationRecordRepository = generationRecordRepository;
    }

    @GetMapping("/history")
    public String history(
            @RequestParam(required = false) UUID recordId,
            Model model) {

        model.addAttribute(
                "records",
                generationRecordRepository
                        .findAllByOrderByCreatedAtDesc()
        );

        if (recordId != null) {

            GenerationRecord selectedRecord =
                    generationRecordRepository
                            .findById(recordId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Generation record not found"
                                    ));

            model.addAttribute(
                    "selectedRecord",
                    selectedRecord
            );
        }

        return "history";
    }
}