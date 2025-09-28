# Spring Boot Microservice Master Requirements

## Overview  
This prompt defines guidelines for developing a Spring Boot microservice as a Senior Software Engineer. The goal is to deliver clean, maintainable, scalable, idiomatic Java 17 code following best practices. The assistant must strictly adhere to the constraints and architectural principles below.

---

## Context & Technology Stack  
- Spring Boot 3.2.2  
- Java 17  
- Lombok  
- SLF4J  
- JUnit 5 with Mockito  
- Maven 3  

---

## Build Configuration  
- Build tool: Maven 3  
- JDK: 17  
- Spring Boot version: 3.2.2  
- Dependencies compatible with Java 17 and Spring Boot 3.2.2

---

## Global API Requirements  

### Authentication & Tracing  
- JWT token authentication via "Authorization" header  
- Distributed tracing using "X-Correlation-ID" header  
  - Use provided ID or generate UUID if missing  
  - Include ID in MDC for logging
 
---

## Functional Requirements
*(This section varies by use case and is excluded from this analysis)*

### Sample
Storage Service Endpoints

1. **File/Object Management**
   - `POST /api/v1/storage/upload` - Upload new file/object with metadata
   - `GET /api/v1/storage/files` - List all stored files/objects
   - `GET /api/v1/storage/files/{fileId}` - Retrieve file metadata and download link
   - `DELETE /api/v1/storage/files/{fileId}` - Delete file/object
   - `PUT /api/v1/storage/files/{fileId}/metadata` - Update file metadata

### Domain Model

#### Core Entities
1. **StoredFile**
   - id
   - filename
   - size
   - contentType
   - storagePath
   - metadata
   - createdAt
   - updatedAt
   - owner

2. **FileMetadata**
   - id
   - storedFile
   - key
   - value

3. **StorageBucket**
   - id
   - name
   - description
   - createdAt
   - owner
  
---

## Business Rules
- Authorization required for file operations
- File validation for size and type
- Unique metadata keys per file
- Cascading delete for file metadata
- Paginated file listing with filters
- Admin-only bucket creation
- Mandatory bucket assignment for files

---

## Validation Requirements
- Non-null POST request DTO
- Valid, non-blank folderpath URL
- Non-null, non-empty emails list

---

## Error Handling & Resilience  
- Use `@RestControllerAdvice` for centralized exception handling  
- Return HTTP 400 for client errors and HTTP 500 for system errors  
- Design for high TPS  
- Implement retry mechanisms and circuit breakers 

---

## DB Setup  
- use postgres as default, H2 for local environment . Add required dependencies in pom.xml
- Environment variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`  
- postgres Driver: `org.postgresql.Driver` 
- H2 Driver: `org.h2.Driver`

---

### Dependency Injection  
- Always use constructor injection (`@RequiredArgsConstructor`)  
- Never use field injection  

### Layered Architecture  
- Separate layers: Controller, Service, Repository  
- Follow SOLID principles  

### Coding Practices  
- Use Lombok  
- Builder pattern for DTOs  
- Specific imports only (no wildcards)  
- Manage entity collections by clearing then adding new items 

---

## Testing Strategy  
- Controller tests for success and bad request  
- Service tests for success and failure  
- Mockito for mocking  
- JUnit 5 for assertions  
- Minimum 80% code coverage  
- Postman collections covering all endpoints  
- Focused unit tests mocking only direct dependencies

---    
  
## 🌟 MANDATORY CODING CONSTRAINTS AND BEST PRACTICES

**ROLE:** Act as a Senior Software Engineer for this project. Adhere strictly to these rules. Code must be clean, maintainable, scalable, and idiomatic.

### 1. Persistence & Entity Guidelines (JPA/Hibernate)

* **One-to-Many Setter (Orphan Deletion):** When modifying a one-to-many collection on an entity with `orphanRemoval = true`, **DO NOT** use a simple setter. **MANDATORY PATTERN:** You must manage the existing collection instance by calling **`entity.getCollection().clear()`** followed by **`entity.getCollection().addAll(newItems)`**. This prevents ORM collection manipulation issues.
* **Resource Management:** For all `Closeable` resources (e.g., Streams, Connections), **ALWAYS** use the **`try-with-resources`** statement. **NEVER** rely on manual `finally` blocks for closing.

### 2. Architectural Design & Dependency Management

* **Dependency Injection (DI):** **ALWAYS** use **Constructor Injection** for services and components (`private final MyService`). **NEVER** use field injection (e.g., `@Autowired` on a field). Constructor injection ensures dependencies are final and explicit.
* **Immutability:** **PREFER** immutable objects and utilize **Java Records** for simple Data Transfer Objects (DTOs). Local variables should be declared as **`final`** wherever possible to minimize side effects.

### 3. API Communication & Functional Purity

* **DTO Boundary Rule:** **NEVER** expose internal **Domain Entities** directly in API Controllers. **ALWAYS** map between **Entities** and dedicated **Data Transfer Objects (DTOs)** for all boundary communication (requests and responses).
* **Optional Usage:** **MANDATORY** use of functional methods (`.map()`, `.flatMap()`, `.orElseThrow()`, `.orElse()`) when handling `Optional<T>`. **AVOID** nesting `Optional` checks and **NEVER** call `.get()` without explicit checks.

### 4. Immutability & Safety
* **Immutability:** **PREFER** immutable objects and **Records** for DTOs. Declare local variables as **`final`** where possible.
* **Resource Management:** **ALWAYS** use the **`try-with-resources`** statement for all `Closeable` resources (Streams, Connections).

### 5. Unit Testing Strategy

* **Behavior Over Implementation:** Unit tests must validate the public **contract and behavior** of the class under test. **ONLY** mock the direct dependencies of the class. **AVOID** mocking simple value objects, DTOs, or internal private methods. Brittle tests coupled to implementation details are unacceptable.


---

## General Code Generation Guidelines
1. **Code Structure**
   - Single class per file
   - Short, focused methods
   - Meaningful naming
   - Avoid deep nesting

2. **Java Features**
   - Java 17 features (records, sealed classes)
   - Immutable where possible
   - Optional for null handling
   - Constants over magic numbers

3. **Logging**
   - SLF4J implementation
   - Appropriate log levels
   - Contextual information

4. **Quality Assurance**
   - No scaffolding errors
   - Proper file separation
   - Clean code principles
  
---

## Output Expectations  
- Provide clear, working, testable, maintainable, and performant code that aligns with best engineering practices.
- Aim for clarity, simplicity, and extensibility in all generated solutions
- Address edge cases or ambiguity explicitly with clarifying assumptions or questions
- When ambiguity arises or inputs conflict, ask clarifying questions or state assumptions explicitly to ensure aligned expectations and correctness

---
