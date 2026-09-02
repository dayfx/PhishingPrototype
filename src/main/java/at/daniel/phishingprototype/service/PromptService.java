package at.daniel.phishingprototype.service;

import at.daniel.phishingprototype.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    public String buildEmployeePrompt(Employee employee) {

        return """
                You are writing a fictional internal workplace email for a
                university research prototype.

                All people, companies, posts, websites, and other information
                supplied below are fictional.

                Write a complete, natural, professional email addressed to the
                fictional employee below.

                Make the email as contextually relevant and personalized as
                possible using the supplied information.

                You may naturally use:
                - the employee's name
                - job role
                - department
                - company context
                - professional interests
                - recent public information
                - timing information such as an upcoming vacation
                - other supplied fictional context

                The email should contain:
                - a believable subject line
                - a natural greeting
                - a plausible workplace reason for contacting the employee
                - role-specific and company-specific context
                - personalized wording based on the supplied information
                - a natural sense of timing where relevant
                - a professional closing

                IMPORTANT:

                At the exact point where the recipient would normally be asked
                to perform the main action, DO NOT describe the action.

                Instead insert this exact token on its own line:

                [[ACTION_SLOT]]

                Include the token exactly once.

                Do not include any URL, password request, credential request,
                financial request, attachment instruction, or security-sensitive
                action.

                After the email, include a section called:

                PERSONALIZATION NOTES

                In that section, briefly explain which fictional profile details
                influenced the wording of the email.

                FICTIONAL COMPANY INFORMATION

                Company name: %s
                Industry: %s
                Website: %s
                Description: %s

                FICTIONAL EMPLOYEE INFORMATION

                Name: %s
                Role: %s
                Department: %s

                SIMULATED PUBLIC INFORMATION

                Interests: %s
                Recent public post: %s
                Public source: %s
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