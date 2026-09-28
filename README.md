# Phishing Prototype – Setup

Required installations:
- Java
- Maven
- Docker
- Gemini API key

Clone repository to local:
git clone https://github.com/dayfx/PhishingPrototype.git

Start Docker Container with Database:
docker compose up -d

Configure Database and API Key in application.properties

Build and Run the Project:
mvn clean install
mvn spring-boot:run

Application can be found on http://localhost:8080
