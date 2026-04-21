# AGENTS.md — Project Yomu

Guidelines for AI coding agents (GitHub Copilot, Cursor, Claude, etc.) working in this repository.

---

## Project Overview

**Project Yomu** is an Information Literacy Gamification platform built with Spring Boot 4 and Java 21.

| Module | Package suffix | Owner |
|---|---|---|
| Authentication & Authorization | `auth` | Muhammad Adra Prakoso |
| Learning & Quiz | `learning` | Radithya Naufal Mulia |
| Achievement | `achievement` | Muhammad Vegard Fathul Islam |
| Social Interaction, Clan & League | `social` | Arisa Raezzura Zahra |
| Discussion Forum | `forum` | Herdayani Elision Sitio |

---

## Build, Test & Run

```bash
# Build
./gradlew build

# Run all tests
./gradlew test

# Run the application
./gradlew bootRun
```

On Windows use `gradlew.bat` instead of `./gradlew`.

---

## Package Structure

All source code lives under the root package:

```
id.ac.ui.cs.advprog.yomu.<module>.<layer>
```

Each module follows a strict layered structure:

```
<module>/
  controller/   — REST controllers (@RestController)
  service/      — Service interface + Impl class
  repository/   — Spring Data JPA repositories
  model/        — JPA entities
  dto/          — Request/Response data transfer objects
  config/       — Module-specific Spring configuration
```

Example (auth module):
```
id.ac.ui.cs.advprog.yomu.auth.controller.AuthController
id.ac.ui.cs.advprog.yomu.auth.service.AuthService          ← interface
id.ac.ui.cs.advprog.yomu.auth.service.AuthServiceImpl      ← implementation
id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository
id.ac.ui.cs.advprog.yomu.auth.model.User
id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest
```

---

## Coding Conventions

### General
- **Java 21**, **Spring Boot 4**.
- Use **Lombok** (`@RequiredArgsConstructor`, `@Getter`, `@Setter`, etc.) — never write boilerplate constructors or getters manually.
- Use **Jakarta Validation** (`@Valid`, `@NotBlank`, `@Email`, etc.) on all incoming DTOs.
- Inject dependencies via **constructor injection** only (`@RequiredArgsConstructor`). Never use field injection (`@Autowired`).

### Controllers
- Annotate with `@RestController` and `@RequestMapping("/api/<module>")`.
- Keep controllers thin — delegate all business logic to the service layer.
- Return `ResponseEntity<T>` with explicit HTTP status codes.

### Services
- Every service must have an **interface** and a separate **Impl** class.
  - Interface: `<Name>Service`
  - Implementation: `<Name>ServiceImpl` (annotated `@Service`)
- All business logic belongs here, not in controllers or repositories.

### Repositories
- Extend `JpaRepository<Entity, IdType>`.
- Add custom query methods as needed using Spring Data method naming or `@Query`.

### DTOs
- Suffix request DTOs with `Request` and response DTOs with `Response`.
- Validate request DTOs with Jakarta Bean Validation annotations.

### Models / Entities
- Annotate with `@Entity` and `@Table(name = "...")`.
- Use `@Id` with `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Use Lombok `@Getter`/`@Setter` or `@Data` (avoid `@Data` on entities with relationships).

### Configuration
- Place module-specific Spring `@Configuration` classes in `<module>/config/`.
- Project-wide security config lives in `auth/config/SecurityConfig.java` — do not duplicate it in other modules.

---

## SOLID Principles

All code written by agents must adhere to the SOLID principles:

- **Single Responsibility:** Every class has one reason to change. Controllers handle HTTP, services handle business logic, repositories handle persistence — never mix these concerns.
- **Open/Closed:** Classes should be open for extension but closed for modification. Prefer adding new service implementations or decorators over editing existing ones.
- **Liskov Substitution:** All `ServiceImpl` classes must be fully substitutable for their `Service` interface — do not throw unexpected exceptions or change observable behavior.
- **Interface Segregation:** Keep service interfaces focused. If an interface grows too large, split it into smaller, role-specific interfaces rather than forcing implementors to stub unused methods.
- **Dependency Inversion:** Depend on abstractions (interfaces), not concrete classes. Always inject the `Service` interface, never `ServiceImpl` directly.

---

## Design Patterns

Use established design patterns where they naturally fit. Do not force patterns onto simple logic.

| Pattern | When to use in this project |
|---|---|
| **Strategy** | Interchangeable algorithms (e.g., different scoring strategies in the quiz module, different auth providers). |
| **Factory / Factory Method** | Creating complex objects (e.g., building different `Achievement` types, constructing quiz questions). |
| **Builder** | Constructing DTOs or entities with many optional fields. Prefer Lombok `@Builder` on the class. |
| **Observer / Event** | Cross-module side effects (e.g., publish a Spring `ApplicationEvent` when a quiz is completed so the achievement module can react, rather than direct coupling). |
| **Decorator** | Layering behaviour on a service without modifying it (e.g., adding caching or audit logging around an existing `ServiceImpl`). |
| **Repository** | Already enforced via Spring Data JPA — never bypass the repository layer to query the DB directly from a service or controller. |
| **Template Method** | Shared workflows with varying steps (e.g., a base class for different leaderboard calculation flows). |

### Rules
- Prefer composition over inheritance.
- Do not introduce a pattern solely to add abstraction — it must solve a concrete problem present in the code.
- Document the pattern name in a comment when it may not be obvious to a reader (e.g., `// Strategy: scoring algorithm`).

---

## Testing

### Requirements
- **Unit tests are required** for all service and controller classes.
- Test files mirror the source tree under `src/test/java/`.
- Use **JUnit 5** (`@ExtendWith(MockitoExtension.class)` for unit, `@SpringBootTest` for integration).
- Use **Spring Security Test** (`@WithMockUser`, `MockMvc`) for controller tests.
- Mock all dependencies with Mockito (`@Mock`, `@InjectMocks`).

### Naming conventions
- Test class: `<ClassName>Test` (e.g., `AuthServiceImplTest`, `AuthControllerTest`)
- Test method: `test<MethodName>_<scenario>` (e.g., `testRegister_success`, `testLogin_invalidPassword`)

### What to test
- **Service tests:** cover the happy path and common failure cases (invalid input, entity not found, duplicate, etc.).
- **Controller tests:** verify HTTP status codes, request validation rejection, and delegated service calls.

---

## Database

- The application uses an **H2 in-memory database** for development and CI.
- Schema is managed by Hibernate (`ddl-auto=create-drop`) — do not write manual migration scripts unless switching to a persistent DB.
- H2 console is available at `/h2-console` during local development.

---

## Security

- Spring Security is configured in `auth/config/SecurityConfig.java`. All route-level security rules live there.
- Do not disable CSRF or authentication on endpoints without updating `SecurityConfig` and leaving a comment explaining why.
- Never log or expose raw passwords, tokens, or secrets in any layer.

---

## What Agents Should NOT Do

- Do not modify `YomuApplication.java` unless bootstrapping a new module requires it.
- Do not move or restructure the package hierarchy without team consensus.
- Do not add new external dependencies to `build.gradle.kts` without confirming they are needed.
- Do not write business logic inside controllers or repositories.
- Do not create a new `SecurityConfig` — extend or update the existing one in `auth/config/`.
