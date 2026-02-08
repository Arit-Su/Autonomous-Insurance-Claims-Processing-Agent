# Autonomous-Insurance-Claims-Processing-Agent
# Autonomous FNOL Processing Agent

A lightweight Spring Boot agent designed to automate the processing of First Notice of Loss (FNOL) documents (specifically ACORD 2 Automobile Loss Notice forms).

## The Approach

The system follows a three-stage pipeline to ensure reliability and speed:

1.  **Extraction**: Uses Apache PDFBox to parse raw text from PDF uploads. A Regex-based strategy extracts key fields (Policy #, Dates, Estimates) specifically tuned for the ACORD 2 layout.
2.  **Validation**: Identifies missing mandatory fields (like incident description or estimated damage) to determine if a human needs to step in.
3.  **Routing**: A prioritized Rules Engine classifies the claim:
    *   **Investigation**: Flagged if keywords like "fraud" or "staged" appear.
    *   **Specialist**: Routed to Injury teams if medical keywords are detected.
    *   **Fast-track**: Auto-routed if damage is under $25,000 and the file is complete.
    *   **Manual Review**: Default for incomplete or high-value claims.

---

## Prerequisites

*   **Java 21** (Required)
*   **Maven 3.8+**

---

## How to Run

1.  **Clone the repo**:
    ```bash
    git clone https://github.com/Arit-Su/Autonomous-Insurance-Claims-Processing-Agent.git
    cd Autonomous-Insurance-Claims-Processing-Agent
    ```

2.  **Build and Run**:
    ```bash
    mvn spring-boot:run
    ```

3.  **Access the Agent**:
    The server starts on `http://localhost:8080`.

---

## Testing the Agent

The easiest way to test is via the built-in **Swagger UI**:

1.  Open your browser to: `http://localhost:8080/swagger-ui/index.html`
2.  Expand the **POST `/api/v1/claims/process`** section.
3.  Click **"Try it out"**.
4.  Upload an FNOL PDF (or a text-based PDF containing policy and damage info).
5.  Click **"Execute"** to see the routing decision and extracted JSON.

### Example Rules
*   **Under $25k**: Routes to `Fast-track`.
*   **Missing Policy #**: Routes to `Manual Review`.
*   **Contains "suspicious"**: Routes to `Investigation Flag`.

---

## Project Structure

*   `controller/`: API Endpoints and Swagger config.
*   `service/`: Extraction logic and the Rules Engine.
*   `model/`: Strongly typed DTOs for claims and processing results.
*   `config/`: Type-safe configuration for rule thresholds.
