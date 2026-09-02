package at.daniel.phishingprototype.service;

import at.daniel.phishingprototype.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    public String buildEmployeePrompt(Employee employee) {

        return """
                You are generating a fictional cybersecurity-awareness simulation
                for an educational university research prototype.

                The person, company, and information below are entirely fictional.

                Your task is to create a simulated phishing-style email that demonstrates
                how publicly available information could be used to personalize a message.

                Safety requirements:
                - Clearly label the generated message as an educational simulation.
                - Do not include real URLs.
                - Do not request real passwords, credentials, payment, or sensitive information.
                - Do not include malware, attachments, or instructions for deploying an attack.
                - Use only the fictional information supplied below.
                - After the simulated email, explain which pieces of information were used
                  for personalization.

                FICTIONAL COMPANY INFORMATION

                Company name: %s
                Industry: %s
                Website: %s
                Company description: %s

                FICTIONAL EMPLOYEE INFORMATION

                Name: %s
                Role: %s
                Department: %s

                SIMULATED PUBLIC INFORMATION

                Public interests: %s
                Recent public post: %s
                Public source: %s

                OUTPUT FORMAT

                1. Simulated email subject
                2. Simulated email body
                3. Personalization explanation
                """.formatted(
                safe(employee.getCompany().getName()),
                safe(employee.getCompany().getIndustry()),
                safe(employee.getCompany().getWebsite()),
                safe(employee.getCompany().getDescription()),
                safe(employee.getName()),
                safe(employee.getRole()),
                safe(employee.getDepartment()),
                safe(employee.getPublicInterests()),
                safe(employee.getRecentPost()),
                safe(employee.getVisibilitySource())
        );
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "Not provided";
        }

        return value;
    }
}