# Spring AI Doctor Search

A Spring Boot API that uses OpenAI embeddings and MongoDB Atlas Vector Search to match a patient's plain-language symptoms with relevant doctor profiles and medical specialties.

> This project suggests where a patient might seek care. It does not diagnose conditions or replace advice from a qualified medical professional. Emergency symptoms require local emergency services.

## Architecture

```mermaid
flowchart LR
    Client --> API[Validated REST API]
    API --> Service[Doctor search service]
    Service --> Port[Search repository port]
    Port --> Atlas[MongoDB Atlas Vector Search]
    Atlas --> Embeddings[OpenAI embeddings]
```

The HTTP layer validates the public contract, the service owns search behavior, and the repository adapter centralizes access to Spring AI's `VectorStore`. Unit and MVC tests use mocks, so the normal build never calls OpenAI or MongoDB.

## Requirements

- Java 21
- A MongoDB Atlas cluster with Vector Search enabled
- An OpenAI project API key

The application uses Spring Boot 3.5 and the stable Spring AI 1.0 release line. API keys and database credentials are read only from environment variables. OpenAI's official guidance likewise recommends loading API keys from a server-side environment variable or key-management service.

## Configuration

Copy the example values into your shell or secret manager; do not commit a populated `.env` file.

```bash
export OPENAI_API_KEY="your-project-key"
export MONGODB_URI="mongodb+srv://username:password@cluster.example.mongodb.net/doctor_db"
export MONGODB_DATABASE="doctor_db"
```

Optional settings are documented in [`.env.example`](.env.example). The configured Atlas collection is `vector_store`, the index is `vector_index`, and `specialty` and `city` are filterable metadata fields.

## Run and verify

```bash
./mvnw verify
./mvnw spring-boot:run
```

The API listens on `http://localhost:8083` by default.

## API

Index doctor profiles:

```bash
curl --request POST http://localhost:8083/api/v1/doctors/documents \
  --header 'Content-Type: application/json' \
  --data '[{"content":"Cardiologist experienced in heart rhythm disorders","metadata":{"specialty":"cardiology","city":"Dhaka"}}]'
```

Search by symptoms, optionally constrained to a specialty:

```bash
curl 'http://localhost:8083/api/v1/doctors/search?query=irregular%20heartbeat&topK=5&similarityThreshold=0.7&specialty=cardiology'
```

Search parameters are bounded: `topK` is 1–50 and `similarityThreshold` is 0–1. Invalid requests return HTTP 400 using Spring's problem-details response format.

## Testing strategy

- Service tests characterize filtering and semantic-query construction.
- MVC slice tests verify JSON contracts, defaults, and validation without loading AI or database infrastructure.
- Repository adapter tests verify delegation to Spring AI's vector-store boundary.
- Live OpenAI/Atlas tests will be kept in a separate opt-in integration profile so pull requests remain deterministic and free of API charges.

## Security

- Never put OpenAI or MongoDB credentials in source-controlled configuration.
- Rotate a credential immediately if it has ever been committed; removing the visible line does not invalidate it.
- Use a least-privilege MongoDB database user and restrict the Atlas network access list.
- Keep symptom data minimal and define a retention policy before using real patient information.
