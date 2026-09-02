package at.daniel.phishingprototype.controller;

import at.daniel.phishingprototype.entity.Employee;
import at.daniel.phishingprototype.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class GenerateController {

    private final EmployeeRepository employeeRepository;

    public GenerateController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/generate")
    public String generatePage(
            @RequestParam(required = false) UUID employeeId,
            Model model) {

        model.addAttribute("employees", employeeRepository.findAll());

        if (employeeId != null) {
            Employee selectedEmployee = employeeRepository
                    .findById(employeeId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Employee not found"));

            model.addAttribute("selectedEmployee", selectedEmployee);
        }

        return "generate";
    }
}