# RideLink - Backend Microservices for Ride-Sharing Platform
**IT3130 - Application Development | Group Assignment**

RideLink is a high-performance backend microservice architecture for a ride-sharing platform built with **Spring Boot 3 (Java 17/21)**, **MongoDB**, **Spring Security (JWT)**, and **OpenAPI (Swagger UI)**.

---

## 🏗️ Architecture & Database Boundary Summary

This project enforces strict microservice boundaries and independent data ownership. Each service connects to its own dedicated **MongoDB** database instance running on `localhost:27017` (viewable via **MongoDB Compass**):

| # | Microservice Name | Primary Member | Service Port | Database Name (MongoDB Compass) | Key Responsibilities |
|---|---|---|---|---|---|
| **1** | **Account Service** | Member 1 | `8081` | `ridelink_account_db` | User & driver registration, authentication, JWT tokens, profile & status management. |
| **2** | **Driver & Vehicle Service** | Member 2 | `8082` | `ridelink_driver_db` | Driver profiles, vehicle details, availability status (`AVAILABLE`, `OFF_DUTY`, `BUSY`), location tracking, available driver discovery. |
| **3** | **Ride Management Service** | Member 3 | `8083` | `ridelink_ride_db` | Ride requests, fare estimation integration, driver assignment, ride lifecycle state machine, cancellation. |
| **4** | **Fare & Payment Service** | Member 4 | `8084` | `ridelink_payment_db` | Fare calculation rule implementation, simulated payment processing, receipt generation & retrieval. |

---

## 🛠️ Prerequisites

- **Java Development Kit (JDK)**: Java 17 or Java 21
- **MongoDB**: Installed locally running on `mongodb://localhost:27017`
- **MongoDB Compass**: Installed for inspecting collections (`ridelink_account_db`, `ridelink_driver_db`, `ridelink_ride_db`, `ridelink_payment_db`)
- **Maven Wrapper**: Standard `./mvnw.cmd` included in each microservice folder.

---

## 🚀 How to Run the Microservices

You can start each service individually using Maven wrapper:

### 1. Start Account Service (Port 8081)
```bash
cd account-service/account-service
.\mvnw.cmd spring-boot:run
```

### 2. Start Driver & Vehicle Service (Port 8082)
```bash
cd driver-service/driver-service
.\mvnw.cmd spring-boot:run
```

### 3. Start Fare & Payment Service (Port 8084)
```bash
cd payment-service/payment-service
.\mvnw.cmd spring-boot:run
```

### 4. Start Ride Management Service (Port 8083)
```bash
cd ride-service/ride-service
.\mvnw.cmd spring-boot:run
```

---

## 📜 Swagger UI / OpenAPI Documentation

Each microservice generates interactive Swagger UI documentation at runtime:

- **Account Service**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Driver & Vehicle Service**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **Ride Management Service**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **Fare & Payment Service**: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)

---

## 🧮 Fare Calculation Formula Rule

The **Fare & Payment Service** calculates estimated fares based on the following formula:

$$\text{Subtotal} = \text{BaseFare (\$2.50)} + (\text{DistanceKm} \times \$1.20) + (\text{DurationMinutes} \times \$0.30)$$
$$\text{Estimated Fare} = \text{Subtotal} \times \text{VehicleType Multiplier}$$

**Vehicle Multipliers**:
- `BIKE`: `0.8`
- `TUKTUK`: `0.7`
- `SEDAN`: `1.0`
- `SUV`: `1.5`

---

## 🔄 Ride Lifecycle State Machine

1. `REQUESTED`: Passenger creates ride request.
2. `ASSIGNED`: Available driver assigned (Driver availability status set to `BUSY`).
3. `ACCEPTED`: Driver accepts assignment.
4. `IN_PROGRESS`: Driver starts trip.
5. `COMPLETED`: Driver completes trip, payment processed, receipt generated, driver status updated to `AVAILABLE`.
6. `CANCELLED`: Ride cancelled by passenger/driver (frees assigned driver back to `AVAILABLE`).

---

## 🧪 Testing with Postman

Import the included files into Postman:
- Collection: [`RideLink_Postman_Collection.json`](file:///c:/Users/Janit/Downloads/AD/Ridelink/IT3130_Group_RideLink/RideLink_Postman_Collection.json)
- Environment: [`RideLink_Environment.json`](file:///c:/Users/Janit/Downloads/AD/Ridelink/IT3130_Group_RideLink/RideLink_Environment.json)

---

## 🛡️ Note on Version Control
*This project is configured locally and will **NOT** be pushed to Git repositories per assignment project instructions.*
