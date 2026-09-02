package at.daniel.phishingprototype.controller;

import at.daniel.phishingprototype.entity.Employee;
import at.daniel.phishingprototype.repository.EmployeeRepository;
import at.daniel.phishingprototype.service.LlmService;
import at.daniel.phishingprototype.service.PromptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Controller
public class GenerateController {

    private static final Logger logger =
            LoggerFactory.getLogger(GenerateController.class);

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

        // always reloads the employee list so the dropdown is still available after generation
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

        // keeps target and prompt visible whether the API request succeeds or fails
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

            if (!generatedResult.contains("[[ACTION_SLOT]]")) {
                throw new IllegalStateException(
                        "The model did not include the required action placeholder."
                );
            }

            generatedResult = generatedResult.replace(
                    "[[ACTION_SLOT]]",
                    "[SIMULATED ACTION REQUIRED HERE — NO REAL LINK]"
            );

            model.addAttribute(
                    "generatedResult",
                    generatedResult
            );

        } catch (RestClientResponseException e) {

            logger.error(
                    "LLM API request failed with HTTP status {}: {}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            String errorMessage =
                    buildApiErrorMessage(e);

            model.addAttribute(
                    "generationError",
                    errorMessage
            );

        } catch (Exception e) {

            logger.error(
                    "Unexpected error while generating simulation",
                    e
            );

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


    private String buildApiErrorMessage(
            RestClientResponseException exception) {

        int statusCode =
                exception.getStatusCode().value();

        return switch (statusCode) {

            case 429 ->
                    "The language model API rate limit has been reached. " + "Please wait a moment and try again.";

            case 503 ->
                    "The language model is currently experiencing high demand " + "and is temporarily unavailable. Please try again in a moment.";

            case 401, 403 ->
                    "The language model API rejected the request. " + "Check the API key and configuration.";

            case 404 ->
                    "The configured language model could not be found. " + "Check the model name in application.properties.";

            default ->
                    "The language model API returned an error (HTTP " + statusCode + ").";
        };
    }
}