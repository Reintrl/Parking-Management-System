# Parking Management System

Backend is an application for managing the parking system: users, transportation, parking, locations, fares, reservations and parking sessions.  
  The project is implemented on **Spring Boot** using **JWT authentication** and **PostgreSQL**.
  
  ---

## Functionality

## Technology Stack


- User and Role Management
- JWT-based authentication and authorization
- User vehicle Management
- Parking and parking space management
- Work with tariffs (price, billing step, free minutes)
- Booking parking spaces
- Managing parking sessions (opening/closing)
- Checking business logic and entity statuses
- API documentation via Swagger (OpenAPI)

---

  - Java 21
  - Spring Boot
  - Spring Security + JWT (jjwt)
  - Spring Data JPA / Hibernate
  - PostgreSQL
  - Maven
  - Lombok
  - Swagger / OpenAPI

---

## Requirements
  
  - JDK 21+
  - Maven 3.8+
  - PostgreSQL 14+

---

## Application Configuration
  
  The application uses the following environment settings:
  
  | Variable | Assignment |
|----------|-----------|
  | `DB_URL` | JDBC database URL |
  | `DB_USER` | PostgreSQL user |
| `DB_PASS` | PostgreSQL Password |
| `JWT_SECRET` | Secret key for JWT |
| `JWT_EXPIRATION_SECONDS` | JWT lifetime in seconds |
  
  ### Example of `application.properties`
  
  ```properties
  spring.datasource.url=${DB_URL}
  spring.datasource.username=${DB_USER}
  spring.datasource.password=${DB_PASS}
  
  jwt.secret=${JWT_SECRET}
  jwt.expiration-seconds=${JWT_EXPIRATION_SECONDS}
  
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=false
  ```
  # Launching the app
  
  ### Via Maven
  
  ```bash
  mvn clean spring-boot:run
  ```
  
  ### Via JAR file
  
  ```bash
  mvn clean package
  java -jar target/parking-management-system.jar
  ```
  
  ### Upon successful launch, the application will be available by default on port 8080.

  # API Documentation (Swagger)

  ### Swagger UI is automatically connected to the project.
  ### After launching the application, the documentation is available at:
  
  ```bash
  http://localhost:8080/swagger-ui/index.html
  ```
  
  ### Swagger allows you to:
  - view all available endpoints,
  - check query and response models,
  - execute requests directly from the browser.
  
  # Authentication and authorization
  The project uses JWT (JSON Web Token) for authentication and access control.
  ## Getting a JWT
  
  ## The # JWT token is issued upon successful user authentication.
  Example:
  ```bash
  POST /auth/login
  ```
  The response returns the JWT that must be used to access the protected endpoints.
  
  # Using JWT
  ### The token is transmitted in the HTTP header of each request:
```bash
Authorization: Bearer <JWT_TOKEN>
  ```
  
  # The main entities of the system
  
  User — the user of the system
  
  Security — user credentials (login, password, role)

  Vehicle — user's vehicle 
  
  ParkingLot — parking area
  
  Spot — parking space
  
  Tariff — parking rate
  
  Reservation — reservation of a parking space
  
  ParkingSession — active or completed parking session
  
  # Project architecture
  The project is built according to the classical layered architecture
  
  ## Layer description
  controller — REST controllers, HTTP request processing

  service — business logic and transactions

  repository — database access (Spring Data JPA)
  
  model — JPA-entities

  dto — data transfer objects

  security — JWT, filters, Spring configuration Security

  exception — custom exceptions and error handling

# Business rules and checks
  
  ## The following checks have been implemented in the project:
  - uniqueness of the vehicle number;
  - control of user statuses, rates and parking spaces;
  - prohibition of overlapping bookings of one parking space;
  - checking the correctness of booking time intervals;
  - restrictions on the creation and completion of parking sessions.

  # API Endpoints
  
  ## Authentication and authorization
  *All authentication endpoints are available without authorization*
  
### `POST /auth/login`
  - **Description**: User authentication and receipt of the JWT token
  - **Request body parameters**: username, password

### `POST /auth/register`
  - **Description**: Registration of a new user
  - **Request body parameters**: username, password, email, and other user data

## User endpoints
  
  ### Profile of the current user (`/me')

#### `GET /me`
  - **Description**: Getting information about the currently authenticated user
  - **Required roles**: USER, OPERATOR, ADMIN

#### `PUT /me`
  - **Description**: Updating the profile of the current user
  - **Required roles**: USER, OPERATOR, ADMIN

### User Management (`/user`)
  
#### `GET /user`
  - **Description**: Getting a list of all users
  - **Required roles**: OPERATOR, ADMIN

#### `GET /user/{id}`
  - **Description**: Getting a user by ID
  - **Required roles**: OPERATOR, ADMIN

#### `POST /user`
  - **Description**: Creating a new user
  - **Required roles**: OPERATOR, ADMIN

#### `PUT /user/{id}`
  - **Description**: Full User Update
  - **Required roles**: ADMIN

#### `PATCH /user/{id}/status`
  - **Description**: User status change
  - **Required roles**: OPERATOR, ADMIN

#### `PATCH /user/soft/{id}`
  - **Description**: Soft User Removal
  - **Required roles**: USER, ADMIN

#### `DELETE /user/{id}`
  - **Description**: Deleting a user
  - **Required Roles**: ADMIN

## Security Role Management (`/security`)
  
  *All endpoints require the ADMIN role*
  
  #### `GET /security/{id}`
  - **Description**: Getting security credentials by ID

#### `GET /security/role/{role}`
  - **Description**: Getting all users by role
 
#### `POST /security/{id}/admin`
  - **Description**: Assigning the ADMIN role to a user
  
#### `POST /security/{id}/operator`
  - **Description**: Assigning the OPERATOR role to the user
  
  ## Vehicles (`/vehicle`)

#### `GET /vehicle`
  - **Description**: Getting a list of all vehicles
  - **Required roles**: OPERATOR, ADMIN

#### `GET /vehicle/{id}`
  - **Description**: Getting a vehicle by ID
  - **Required roles**: USER, OPERATOR, ADMIN

#### `POST/vehicle`
  - **Description**: Creating a new vehicle
  - **Required roles**: USER, OPERATOR, ADMIN

#### `PUT /vehicle/{id}`
  - **Description**: Vehicle Upgrade
  - **Required roles**: USER, OPERATOR, ADMIN

#### `DELETE /vehicle/{id}`
  - **Description**: Vehicle Removal
  - **Required roles**: USER, ADMIN

#### `GET/vehicle/user/{userId}`
  - **Description**: Getting user's vehicles
  - **Required roles**: USER, OPERATOR, ADMIN

## Parking areas (`/ParkingLot`)

#### `GET /ParkingLot`
  - **Description**: Getting a list of all parking areas
  - **Required roles**: USER, OPERATOR, ADMIN

#### `GET /ParkingLot/{id}`
  - **Description**: Getting a parking area by ID
  - **Required roles**: USER, OPERATOR, ADMIN

#### `POST /ParkingLot`
  - **Description**: Creating a new parking area
  - **Required roles**: OPERATOR, ADMIN

#### `POST /ParkingLot/with-spots`
  - **Description**: Creating a parking area with spaces
  - **Required roles**: OPERATOR, ADMIN

#### `PUT /ParkingLot/{id}`
  - **Description**: Updating the parking area
  - **Required roles**: OPERATOR, ADMIN

#### `PATCH /ParkingLot/{id}/status`
  - **Description**: Changing the status of the parking area
  - **Required roles**: OPERATOR, ADMIN

#### `DELETE /ParkingLot/{id}`
  - **Description**: Removing a parking area
  - **Required roles**: ADMIN

#### `GET /ParkingLot/{id}/spots/available`
  - **Description**: Getting available parking spaces
  - **Required roles**: USER, OPERATOR, ADMIN
  
#### `GET /parkingLot/{id}/dashboard`
  - **Description**: Getting a parking dashboard
  - **Required Roles**: OPERATOR, ADMIN

## Parking Spaces (`/spot`)

#### `GET /spot`
  - **Description**: Getting a list of all parking spaces
- **Required roles**: USER, OPERATOR, ADMIN

#### `GET/spot/{id}`
  - **Description**: Getting a parking space by ID
  - **Required roles**: USER, OPERATOR, ADMIN

#### `POST /spot`
  - **Description**: Creating a new parking space
  - **Required roles**: OPERATOR, ADMIN

#### `PUT /spot/{id}`
  - **Description**: Parking Space Upgrade
  - **Required roles**: OPERATOR, ADMIN

#### `PATCH /spot/{id}/status`
  - **Description**: Changing the status of a parking space
  - **Required roles**: OPERATOR, ADMIN

#### `DELETE /spot/{id}`
  - **Description**: Removing a parking space
  - **Required roles**: ADMIN

#### `GET /spot/ParkingLot/{parkingLotId}`
  - **Description**: Getting places of a certain parking area
  - **Required roles**: USER, OPERATOR, ADMIN

## Tariffs (`/tariff`)

#### `GET /tariff`
  - **Description**: Getting a list of all tariffs
  - **Required roles**: USER, OPERATOR, ADMIN

#### `GET /tariff/{id}`
  - **Description**: Getting a tariff by ID
  - **Required roles**: USER, OPERATOR, ADMIN

#### `POST/tariff`
  - **Description**: Creating a new tariff
  - **Required roles**: OPERATOR, ADMIN

#### `PUT /tariff/{id}`
  - **Description**: Tariff update
  - **Required roles**: OPERATOR, ADMIN

#### `DELETE /tariff/{id}`
  - **Description**: Tariff Removal
  - **Required roles**: ADMIN

## Reservations (`/reservation`)

#### `GET /reservation`
  - **Description**: Getting a list of all bookings
  - **Required roles**: OPERATOR, ADMIN

#### `GET /reservation/{id}`
  - **Description**: Getting a reservation by ID
  - **Required roles**: USER, OPERATOR, ADMIN

#### `POST /reservation`
  - **Description**: Creating a new reservation
  - **Required roles**: USER, OPERATOR, ADMIN

#### `PUT /reservation/{id}`
  - **Description**: Booking Update
  - **Required roles**: USER, OPERATOR, ADMIN

#### `PATCH/reservation/{id}/status`
  - **Description**: Reservation status change
  - **Required roles**: OPERATOR, ADMIN

#### `DELETE /reservation/{id}`
  - **Description**: Deleting a reservation
  - **Required roles**: ADMIN

#### `POST /reservation/{id}/cancel`
  - **Description**: Cancellation of reservations
  - **Required roles**: USER, OPERATOR, ADMIN

#### `GET/reservation/vehicle/{VehicleId}`
  - **Description**: Receiving vehicle bookings
  - **Required roles**: USER, OPERATOR, ADMIN

#### `GET /reservation/spot/{spotId}`
  - **Description**: Receiving parking reservations
  - **Required roles**: OPERATOR, ADMIN

## Parking sessions (`/parkingSession`)

#### `GET /parkingSession`
  - **Description**: Getting a list of all parking sessions
  - **Required roles**: OPERATOR, ADMIN

#### `GET /parkingSession/{id}`
  - **Description**: Getting a parking session by ID
  - **Required roles**: USER, OPERATOR, ADMIN

#### `POST /parkingSession`
  - **Description**: Creating a new parking session
  - **Required roles**: OPERATOR, ADMIN

#### `POST /parkingSession/from-reservation/{ReservationId}`
  - **Description**: Creating a parking session from a reservation
  - **Required roles**: OPERATOR, ADMIN

#### `POST /parkingSession/{id}/finish`
  - **Description**: End of parking session
  - **Required roles**: OPERATOR, ADMIN

#### `DELETE /parkingSession/{id}`
  - **Description**: Deleting a parking session
  - **Required roles**: ADMIN

#### `GET /parkingSession/spot/{spotId}`
  - **Description**: Getting parking sessions of a place
  - **Required roles**: OPERATOR, ADMIN

#### `GET/parkingSession/vehicle/{VehicleId}`
  - **Description**: Getting vehicle parking sessions
  - **Required roles**: USER, OPERATOR, ADMIN

  ## Response codes
  Code Description
  - 200 OK - The request was completed successfully
  - 201 Created - The resource was created successfully
  - 204 No Content - The request was completed, but the response body is missing
  - 400 Bad Request - Invalid request parameters
  - 401 Unauthorized - Authentication required
  - 403 Forbidden - Access denied (insufficient rights)
  - 404 Not Found - Resource not found
  - 409 Conflict - Data conflict (e.g. duplication)
  - 500 Internal Server Error - Internal Server Error

  # Possible development directions
  - implementation of refresh tokens;
  - pagination and filtering of data;
  - containerization of the application using Docker Compose;
