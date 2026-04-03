# Finance Dashboard Backend

A backend system for a finance dashboard built with **Java Spring Boot** and **SQLite**. It supports role-based access control, financial record management, and dashboard analytics.

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Security** (JWT-based authentication)
- **Spring Data JPA** (Hibernate)
- **SQLite** (via `sqlite-jdbc` + Hibernate Community Dialects)
- **Maven**

---

## Project Structure

```
src/main/java/com/finance/dashboard/
├── config/
│   └── SecurityConfig.java           # JWT filter chain, role-based access
├── controller/
│   ├── AuthController.java           # Register, Login
│   ├── UserController.java           # User management (Admin only)
│   ├── FinancialRecordController.java # CRUD for records
│   └── DashboardController.java      # Summary and analytics
├── dto/
│   ├── request/                      # Input objects (validated)
│   └── response/                     # Output objects (safe, no raw entities)
├── exception/
│   ├── GlobalExceptionHandler.java   # Centralized error handling
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedAccessException.java
├── model/
│   ├── User.java                     # Users table
│   ├── FinancialRecord.java          # Financial records table
│   ├── Role.java                     # VIEWER, ANALYST, ADMIN
│   └── RecordType.java               # INCOME, EXPENSE
├── repository/
│   ├── UserRepository.java
│   └── FinancialRecordRepository.java
├── security/
│   ├── JwtUtil.java                  # Token generation and validation
│   ├── JwtAuthFilter.java            # Request filter
│   └── CustomUserDetailsService.java # Loads user from DB for Spring Security
└── service/
    ├── AuthService.java
    ├── UserService.java
    ├── FinancialRecordService.java
    └── DashboardService.java
```

---

## Setup and Running

### Prerequisites
- Java 17+
- Maven

### Steps

```bash
# 1. Clone the repository
git clone <my repi url>
cd dashboard

# 2. Run the application
./mvnw spring-boot:run
```

That's it. No database setup needed — SQLite creates a `finance.db` file automatically in the project root on first run.

The app starts at: `http://localhost:8080`

---

## Environment Configuration

All configuration is in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlite:finance.db
spring.jpa.hibernate.ddl-auto=update
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
```

> In a production environment, the JWT secret should be stored in environment variables, not in source code.

---

## Authentication

The API uses **JWT (JSON Web Token)** based authentication.

1. Register or login to receive a token
2. Include the token in all subsequent requests:

```
Authorization: Bearer <your_token_here>
```

Tokens expire after **24 hours**.

---

## Roles and Permissions

| Action                        | VIEWER | ANALYST | ADMIN |
|-------------------------------|--------|---------|-------|
| Register / Login              | ✅     | ✅      | ✅    |
| View financial records        | ✅     | ✅      | ✅    |
| View recent activity          | ✅     | ✅      | ✅    |
| View dashboard summary        | ❌     | ✅      | ✅    |
| View category totals          | ❌     | ✅      | ✅    |
| View monthly trends           | ❌     | ✅      | ✅    |
| Create financial records      | ❌     | ❌      | ✅    |
| Update financial records      | ❌     | ❌      | ✅    |
| Delete financial records      | ❌     | ❌      | ✅    |
| Manage users (role, status)   | ❌     | ❌      | ✅    |

Role enforcement is implemented using Spring Security's `@PreAuthorize` annotations on each controller method.

---

## API Reference

### Auth

| Method | Endpoint              | Access | Description         |
|--------|-----------------------|--------|---------------------|
| POST   | /api/auth/register    | Public | Register a new user |
| POST   | /api/auth/login       | Public | Login and get token |

**Register Request:**
```json
{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "secret123",
    "role": "ADMIN"
}
```

**Login Request:**
```json
{
    "email": "john@example.com",
    "password": "secret123"
}
```

**Response (both):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "email": "john@example.com",
    "role": "ADMIN"
}
```

---

### Users (Admin only)

| Method | Endpoint                  | Description              |
|--------|---------------------------|--------------------------|
| GET    | /api/users                | Get all users            |
| GET    | /api/users/{id}           | Get user by ID           |
| PATCH  | /api/users/{id}/role      | Update user role         |
| PATCH  | /api/users/{id}/status    | Activate or deactivate   |

**Update Role Request:**
```json
{ "role": "ANALYST" }
```

**Update Status Request:**
```json
{ "isActive": false }
```

---

### Financial Records

| Method | Endpoint           | Access                      | Description              |
|--------|--------------------|-----------------------------|--------------------------|
| POST   | /api/records       | ADMIN                       | Create a record          |
| GET    | /api/records       | ADMIN, ANALYST, VIEWER      | Get all records          |
| GET    | /api/records/{id}  | ADMIN, ANALYST, VIEWER      | Get record by ID         |
| PUT    | /api/records/{id}  | ADMIN                       | Update a record          |
| DELETE | /api/records/{id}  | ADMIN                       | Soft delete a record     |

**Create / Update Request:**
```json
{
    "amount": 5000.00,
    "type": "INCOME",
    "category": "Salary",
    "date": "2026-04-01",
    "notes": "Monthly salary"
}
```

**Filtering (query params):**
```
GET /api/records?type=INCOME
GET /api/records?category=Salary
GET /api/records?from=2026-01-01&to=2026-04-30
GET /api/records?type=EXPENSE&from=2026-01-01&to=2026-04-30
```

---

### Dashboard (Admin + Analyst)

| Method | Endpoint                     | Access               | Description                      |
|--------|------------------------------|----------------------|----------------------------------|
| GET    | /api/dashboard/summary       | ADMIN, ANALYST       | Total income, expense, balance   |
| GET    | /api/dashboard/by-category   | ADMIN, ANALYST       | Totals grouped by category       |
| GET    | /api/dashboard/trends/monthly| ADMIN, ANALYST       | Monthly income/expense trends    |
| GET    | /api/dashboard/recent        | ADMIN, ANALYST, VIEWER | Last 5 transactions            |

**Summary Response:**
```json
{
    "totalIncome": 5000.00,
    "totalExpense": 1500.00,
    "netBalance": 3500.00,
    "categoryTotals": {
        "Salary": 5000.00,
        "Rent": 1500.00
    },
    "recentActivity": [ ... ]
}
```

---

## Data Models

### User
| Field      | Type      | Notes                        |
|------------|-----------|------------------------------|
| id         | Long      | Auto-generated               |
| name       | String    | Required                     |
| email      | String    | Unique, used as username     |
| password   | String    | BCrypt hashed                |
| role       | Enum      | VIEWER, ANALYST, ADMIN       |
| isActive   | Boolean   | Default true                 |
| createdAt  | Timestamp | Auto-set on creation         |

### Financial Record
| Field      | Type      | Notes                        |
|------------|-----------|------------------------------|
| id         | Long      | Auto-generated               |
| amount     | Decimal   | Must be positive             |
| type       | Enum      | INCOME or EXPENSE            |
| category   | String    | Required                     |
| date       | Date      | Required                     |
| notes      | String    | Optional                     |
| isDeleted  | Boolean   | Soft delete flag             |
| createdBy  | User (FK) | User who created the record  |
| createdAt  | Timestamp | Auto-set on creation         |
| updatedAt  | Timestamp | Auto-set on update           |

---

## Error Handling

All errors return a consistent JSON structure:

```json
{
    "timestamp": "2026-04-02T12:00:00",
    "status": 404,
    "message": "Record not found with id: 5"
}
```

| Status | Meaning                                      |
|--------|----------------------------------------------|
| 400    | Validation failed (missing or invalid fields)|
| 401    | Invalid or missing token                     |
| 403    | Authenticated but insufficient role          |
| 404    | Resource not found                           |
| 500    | Unexpected server error                      |

---

## Assumptions Made

1. **Any user can self-register** with any role including ADMIN. In a real system, admin accounts would be created separately or seeded.
2. **Soft delete** is used for financial records — deleted records are hidden from all APIs but remain in the database for audit purposes.
3. **SQLite** was chosen for zero-setup simplicity. Switching to PostgreSQL or MySQL only requires changing `application.properties` and the Maven dependency — no code changes needed.
4. **JWT tokens** are stateless — there is no logout/token revocation mechanism. Tokens naturally expire after 24 hours.
5. **No pagination** is implemented for record listing. For large datasets this would be a necessary addition.
6. All monetary values use `BigDecimal` for precision — important for financial data.

---

## Tradeoffs Considered

| Decision | Chosen Approach | Alternative |
|----------|----------------|-------------|
| Database | SQLite (zero setup) | PostgreSQL (production-ready) |
| Auth | JWT stateless | Session-based |
| Boilerplate | Manual getters/setters | Lombok (had Eclipse compatibility issues) |
| Delete | Soft delete | Hard delete |
| Role storage | Single role per user | Multiple roles per user |

---

## What Could Be Added

- Pagination for `/api/records`
- Search by notes/description
- Rate limiting
- Unit and integration tests
- Swagger / OpenAPI documentation
- Refresh token mechanism
- Audit log for all record changes
