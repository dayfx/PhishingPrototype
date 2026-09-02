package at.daniel.phishingprototype.controller;

import at.daniel.phishingprototype.entity.Employee;
import at.daniel.phishingprototype.repository.EmployeeRepository;
import at.daniel.phishingprototype.service.LlmService;
import at.daniel.phishingprototype.service.PromptService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Controller
public class GenerateController {

    private final EmployeeRepository employeeRepository;
    private final PromptService promptService;
    private final LlmService llmService;


    public GenerateController(
            EmployeeRepository employeeRepository,
            PromptService promptService,
            LlmService llmService) {

        this.employeeRepository = employeeRepository;
        this.promptService = promptService;
        this.llmService = llmService;
    }


    @GetMapping("/generate")
    public String generatePage(
            @RequestParam(required = false) UUID employeeId,
            Model model) {

        model.addAttribute(
                "employees",
                employeeRepository.findAll()
        );

        if (employeeId != null) {

            Employee selectedEmployee =
                    getEmployee(employeeId);

            String prompt =
                    promptService.buildEmployeePrompt(
                            selectedEmployee
                    );

            model.addAttribute(
                    "selectedEmployee",
                    selectedEmployee
            );

            model.addAttribute(
                    "promptPreview",
                    prompt
            );
        }

        return "generate";
    }


    @PostMapping("/generate/run")
    public String runGeneration(
            @RequestParam UUID employeeId,
            Model model) {

        model.addAttribute(
                "employees",
                employeeRepository.findAll()
        );

        Employee selectedEmployee =
                getEmployee(employeeId);

        String prompt =
                promptService.buildEmployeePrompt(
                        selectedEmployee
                );

        model.addAttribute(
                "selectedEmployee",
                selectedEmployee
        );

        model.addAttribute(
                "promptPreview",
                prompt
        );

        try {

            String generatedResult =
                    llmService.generate(prompt);

            if (generatedResult.contains("[[ACTION_SLOT]]")) {

                generatedResult =
                        generatedResult.replace(
                                "[[ACTION_SLOT]]",
                                "[SIMULATED ACTION REQUIRED HERE — NO REAL LINK]"
                        );
            }

            String[] resultParts =
                    splitGeneratedResult(generatedResult);

            model.addAttribute(
                    "generatedEmail",
                    resultParts[0]
            );

            model.addAttribute(
                    "personalizationNotes",
                    resultParts[1]
            );

        } catch (RestClientResponseException e) {

            model.addAttribute(
                    "generationError",
                    buildApiErrorMessage(e)
            );

        } catch (Exception e) {

            model.addAttribute(
                    "generationError",
                    "An unexpected error occurred while generating the simulation. " +
                            "Please try again."
            );
        }

        return "generate";
    }


    private Employee getEmployee(UUID employeeId) {

        return employeeRepository
                .findById(employeeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found"
                        ));
    }


    private String[] splitGeneratedResult(
            String generatedResult) {

        String marker =
                "PERSONALIZATION NOTES";

        int markerIndex =
                generatedResult
                        .toUpperCase()
                        .indexOf(marker);

        if (markerIndex == -1) {

            return new String[]{
                    generatedResult.trim(),
                    "No separate personalization analysis was returned by the model."
            };
        }

        String email =
                generatedResult
                        .substring(0, markerIndex)
                        .replace("***", "")
                        .trim();

        String notes =
                generatedResult
                        .substring(
                                markerIndex + marker.length()
                        )
                        .replaceFirst("^\\s*#+\\s*", "")
                        .trim();

        return new String[]{
                email,
                notes
        };
    }


    private String buildApiErrorMessage(
            RestClientResponseException exception) {

        int statusCode =
                exception.getStatusCode().value();

        return switch (statusCode) {

            case 429 ->
                    "The language model API rate limit has been reached. " +
                            "Please wait a moment and try again.";

            case 503 ->
                    "The language model is currently experiencing high demand " +
                            "and is temporarily unavailable. Please try again shortly.";

            case 401, 403 ->
                    "The language model API rejected the request. " +
                            "Please check the API key and configuration.";

            case 404 ->
                    "The configured language model could not be found. " +
                            "Please check the model configuration.";

            default ->
                    "The language model API returned an error (HTTP "
                            + statusCode
                            + "). Please try again.";
        };
    }
}