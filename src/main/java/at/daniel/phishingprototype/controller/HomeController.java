package at.daniel.phishingprototype.controller;

import at.daniel.phishingprototype.entity.Company;
import at.daniel.phishingprototype.entity.Employee;
import at.daniel.phishingprototype.repository.CompanyRepository;
import at.daniel.phishingprototype.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class HomeController {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;

    public HomeController(
            CompanyRepository companyRepository,
            EmployeeRepository employeeRepository) {

        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("company", new Company());
        model.addAttribute("companies", companyRepository.findAll());

        model.addAttribute("employee", new Employee());
        model.addAttribute("employees", employeeRepository.findAll());

        return "index";
    }

    @PostMapping("/companies")
    public String createCompany(@ModelAttribute Company company) {

        companyRepository.save(company);

        return "redirect:/";
    }

    @PostMapping("/employees")
    public String createEmployee(
            @ModelAttribute Employee employee,
            @RequestParam UUID companyId) {

        Company company = companyRepository
                .findById(companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Company not found"));

        employee.setCompany(company);

        employeeRepository.save(employee);

        return "redirect:/";
    }
}