package at.daniel.phishingprototype.service;

import at.daniel.phishingprototype.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class PromptService {

    public String buildEmployeePrompt(Employee employee) {

        return """
                Write a complete, natural and professional internal workplace email
                addressed to the employee described below.

                Use the supplied information to make the email feel highly relevant
                and personally appropriate to this specific employee.

                The email should sound like a believable message that could naturally
                occur within their workplace.

                PERSONALIZATION

                Where appropriate, naturally incorporate information such as:

                - the employee's name
                - their role and responsibilities
                - their department
                - their company
                - relevant professional interests
                - relevant recent information
                - relevant timing information
                - organizational context

                Do not force every detail into the email. Only use information where
                it fits naturally and improves the relevance of the message.

                EMAIL PURPOSE

                Create a plausible internal workplace reason for contacting this
                employee.

                Examples of suitable contexts include:

                - reviewing a project document
                - confirming project information
                - coordinating work before an absence
                - reviewing planning information
                - confirming attendance or availability
                - providing feedback on an internal document
                - completing a normal administrative task

                The context should be specifically relevant to the employee's role,
                department and available information.

                ACTION PLACEHOLDER

                At the point where the employee would normally be asked to perform
                the primary action, place this exact token on its own line:

                [[ACTION_SLOT]]

                Include [[ACTION_SLOT]] exactly once.

                Do not replace it with an actual action.

                Do not include:
                - URLs
                - login instructions
                - passwords
                - credential requests
                - payment requests
                - banking information
                - authentication codes
                - executable files
                - software installation instructions

                WRITING STYLE

                The email should:

                - use natural professional language
                - have a realistic subject line
                - include a natural greeting
                - have a coherent reason for contacting the employee
                - make good use of relevant contextual information
                - sound individually written rather than like a generic template
                - have a professional closing
                - be concise enough to resemble a normal workplace email

                OUTPUT FORMAT

                SUBJECT:
                <subject line>

                EMAIL:

                <complete email>

                PERSONALIZATION NOTES:

                Briefly explain which supplied details influenced the email and
                where they were used.


                COMPANY INFORMATION

                Company name: %s
                Industry: %s
                Website: %s
                Company description: %s


                EMPLOYEE INFORMATION

                Name: %s
                Role: %s
                Department: %s


                ADDITIONAL CONTEXT

                Interests: %s
                Recent public information: %s
                Information source: %s
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