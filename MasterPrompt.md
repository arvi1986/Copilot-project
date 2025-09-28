# Spring Boot Microservice Master Requirements

## Context & Technology Stack
- Spring Boot 3.2.2
- Java 17
- Lombok
- SLF4J
- JUnit 5 with Mockito
- Maven 3

## Build Configuration
- Build tool: Maven 3
- JDK: 17
- Spring Boot version: 3.2.2
- Dependencies must be compatible with Java 17 and Spring Boot 3.2.2

## Global API Requirements
### Authentication & Tracing
- JWT token authentication via "Authorization" header
- Distributed tracing using "X-Correlation-ID" header
  - Use provided ID or generate UUID
  - Include in MDC for logging

## Functional Requirements
### Storage Service Endpoints

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

### Business Rules
- Authorization required for file operations
- File validation for size and type
- Unique metadata keys per file
- Cascading delete for file metadata
- Paginated file listing with filters
- Admin-only bucket creation
- Mandatory bucket assignment for files

## Validation Requirements
- Non-null POST request DTO
- Valid, non-blank folderpath URL
- Non-null, non-empty emails list

## Error Handling
- Global exception handling via `@RestControllerAdvice`
- HTTP 400 for client errors
- HTTP 500 for system errors

## Performance & Resilience
- High TPS design
- Retry mechanism for dependencies
- Circuit breaker implementation

## Database Configuration
### PostgreSQL Setup
- Environment variables:
  - `DB_HOST`
  - `DB_PORT`
  - `DB_NAME`
  - `DB_USERNAME`
  - `DB_PASSWORD`
- Driver: `org.postgresql.Driver`

## Architecture & Code Style
- Constructor-based dependency injection with `@RequiredArgsConstructor`
- Layered architecture (Controller, Service)
- SOLID principles
- Specific class imports (no wildcards)
- Lombok for boilerplate reduction
- Builder pattern for DTOs

## Testing Requirements
- Controller tests: 200 OK and 400 Bad Request scenarios
- Service tests: successful and failure scenarios
- Mockito for mocking
- JUnit 5 for assertions
- 80% minimum code coverage
- Tests required for all new code
- create a postman collections for each controller including all endpoints with examples

## Code Generation Guidelines
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
