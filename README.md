# Banking Platform API

Backend service for the Store Bank banking platform. This project is a Spring Boot 3.4 API that exposes the banking domain used by the frontend application.

## Stack

- Java 17
- Spring Boot 3.4.3
- Spring Web
- Spring Data JPA
- Spring Validation
- MySQL 8.0
- springdoc-openapi
- Lombok

## Project layout

- `src/main/java/com/uab/taller/store` application source code.
- `src/main/java/com/uab/taller/store/controller` REST controllers.
- `src/main/java/com/uab/taller/store/domain` domain entities and DTOs.
- `src/main/java/com/uab/taller/store/repository` persistence interfaces.
- `src/main/java/com/uab/taller/store/service` service layer.
- `src/main/java/com/uab/taller/store/usecase` business use cases.
- `src/main/resources/application.properties` runtime configuration.
- `src/test/java/com/uab/taller/store` automated tests.
- `*.rest` request files at the repository root for manual API testing.

## Run locally

### Prerequisites

- Java 17
- Maven wrapper (`mvnw`)
- MySQL running on `localhost:3306`

### Database

The default local configuration uses this database connection:

- Host: `localhost`
- Port: `3306`
- Database: `mydb`
- User: `root`
- Password: `password`

You can start a compatible MySQL container with:

```bash
docker compose up -d
```

The included `docker-compose.yml` creates a MySQL 8.0 container and persists data in `./dbdata`.

### Start the API

From this folder:

```bash
./mvnw spring-boot:run
```

On Windows, use:

```bash
mvnw.cmd spring-boot:run
```

The application starts on `http://localhost:8080`.

## API documentation

Swagger/OpenAPI is available at:

- `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Base path

All REST endpoints are exposed under `/api/v1`.

## Main endpoints

### Users

Base path: `/api/v1/users`

- `GET /api/v1/users` list active users.
- `GET /api/v1/users/{id}` get a user by id.
- `POST /api/v1/users` create a user.
- `PUT /api/v1/users/{id}` update a user.
- `DELETE /api/v1/users/{id}` soft delete a user.
- `DELETE /api/v1/users/{id}/force` hard delete a user.
- `POST /api/v1/users/search/email` search by email.
- `POST /api/v1/users/login` authenticate a user.
- `GET /api/v1/users/admin/all` list all users, including deleted ones.

### Accounts

Base path: `/api/v1/account`

- `GET /api/v1/account` list accounts.
- `GET /api/v1/account/{id}` get an account by id.
- `GET /api/v1/account/number/{accountNumber}` get an account by number.
- `POST /api/v1/account` create an account.
- `PUT /api/v1/account` update an account.
- `DELETE /api/v1/account/{id}` soft delete an account.
- `DELETE /api/v1/account/{id}/force` hard delete an account.
- `GET /api/v1/account/user/{userId}` list accounts for a user.
- `GET /api/v1/account/user/{userId}/active` list active accounts for a user.
- `GET /api/v1/account/{id}/summary` account summary.
- `GET /api/v1/account/{id}/balance` current balance.
- `POST /api/v1/account/{id}/freeze` freeze an account.
- `POST /api/v1/account/{id}/unfreeze` unfreeze an account.
- `GET /api/v1/account/{id}/frozen` check whether an account is frozen.
- `POST /api/v1/account/{id}/balance/add` add balance.
- `POST /api/v1/account/{id}/balance/subtract` subtract balance.
- `GET /api/v1/account/{id}/balance/sufficient` check balance sufficiency.
- `POST /api/v1/account/validate-creation` validate account creation.
- `POST /api/v1/account/{id}/validate-update` validate account update.
- `POST /api/v1/account/{id}/validate-deletion` validate account deletion.

### Beneficiaries

Base path: `/api/v1/beneficiary`

- `GET /api/v1/beneficiary` list beneficiaries.
- `GET /api/v1/beneficiary/{id}` get a beneficiary by id.
- `POST /api/v1/beneficiary` create a beneficiary.
- `PUT /api/v1/beneficiary` update a beneficiary.
- `DELETE /api/v1/beneficiary/{id}` soft delete a beneficiary.
- `DELETE /api/v1/beneficiary/{id}/force` hard delete a beneficiary.
- `GET /api/v1/beneficiary/user/{userId}` list beneficiaries for a user.
- `GET /api/v1/beneficiary/user/{userId}/active` list active beneficiaries for a user.
- `POST /api/v1/beneficiary/validate-creation` validate beneficiary creation.
- `POST /api/v1/beneficiary/{id}/validate-update` validate beneficiary update.
- `POST /api/v1/beneficiary/{id}/validate-deletion` validate beneficiary deletion.

### Profiles

Base path: `/api/v1/profile`

- `GET /api/v1/profile` list profiles.
- `GET /api/v1/profile/{id}` get a profile by id.
- `GET /api/v1/profile/{id}/summary` profile summary.
- `POST /api/v1/profile` create a profile.
- `PUT /api/v1/profile/{id}` update a profile.
- `DELETE /api/v1/profile/{id}` soft delete a profile.
- `DELETE /api/v1/profile/{id}/force` hard delete a profile.

### Roles

Base path: `/api/v1/rol`

- `GET /api/v1/rol` list roles.
- `GET /api/v1/rol/{id}` get a role by id.
- `GET /api/v1/rol/name/{name}` get a role by name.
- `POST /api/v1/rol` create a role.
- `PUT /api/v1/rol/{id}` update a role.
- `DELETE /api/v1/rol/{id}` soft delete a role.
- `DELETE /api/v1/rol/{id}/force` hard delete a role.

### Transactions

Base path: `/api/v1/transaction`

- `GET /api/v1/transaction` list transactions.
- `GET /api/v1/transaction/{id}` get a transaction by id.
- `GET /api/v1/transaction/account/{accountId}` list transactions for an account.
- `GET /api/v1/transaction/date-range` filter by date range.
- `GET /api/v1/transaction/type/{type}` filter by type.
- `POST /api/v1/transaction` create a transaction.
- `POST /api/v1/transaction/legacy` legacy create endpoint.
- `POST /api/v1/transaction/transfer` process a transfer.
- `POST /api/v1/transaction/deposit` process a deposit.
- `POST /api/v1/transaction/withdrawal` process a withdrawal.
- `PUT /api/v1/transaction/{id}` update a transaction.
- `DELETE /api/v1/transaction/{id}` delete a transaction.
- `POST /api/v1/transaction/{id}/reverse` reverse a transaction.
- `GET /api/v1/transaction/statistics/account/{accountId}` transaction statistics by account.
- `GET /api/v1/transaction/statistics/date-range` transaction statistics by date range.
- `GET /api/v1/transaction/statistics/general` general transaction statistics.
- `GET /api/v1/transaction/{id}/history` transaction history.
- `POST /api/v1/transaction/search` transaction search.
- `GET /api/v1/transaction/metrics` transaction metrics.

## Testing

The repository includes a basic Spring Boot test suite under `src/test/java/com/uab/taller/store`.

## Manual requests

Use the `.rest` files in the repository root to try common requests:

- `user.rest`
- `profile.rest`
- `rol.rest`
- `transaction.rest`

## Notes

- CORS is enabled on the controllers for local frontend access.
- The API uses `hibernate.ddl-auto=update`, so the schema is updated automatically during development.
- If you change the database credentials or port, update `src/main/resources/application.properties`.
