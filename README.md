# Hybrid-Cloud SCM Hub

A comprehensive solution for bridging On-Premise warehouse operations with Cloud analytics.

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.4, Hibernate, **Spring Integration**, **Spring Cloud AWS (S3, SQS)**, Spring Boot Actuator.
- **Frontend:** Angular 16, NgRx, SCSS, RxJS.
- **Database:** 2x PostgreSQL (On-Prem & Cloud simulation).
- **Cloud Simulation:** **LocalStack** (S3 for documents, SQS for event-driven sync).
- **Architecture:** Hexagonal (Ports & Adapters) + Event-Driven.

## Application Preview

### Dashboard & Analytics
![Dashboard](docs/images/dashboard.png)

### Inventory Management
![Inventory Top](docs/images/inventory-top.png)
![Inventory Bottom](docs/images/inventory-bottom.png)

### Order Management
![Orders](docs/images/order.png)

### Synchronization Audit Trail
![Audit Trail](docs/images/audit-trail.png)

## Project Structure
- `backend/`: Spring Boot application.
- `frontend/`: Angular application.
- `docker-compose.yml`: Infrastructure (Postgres, LocalStack).
- `localstack-init/`: AWS resource initialization scripts.

## How to Run

### Option A: Full Application via Docker (Recommended)
This is the fastest way to run the entire stack (Databases, LocalStack, Backend, and Frontend).
```bash
docker compose up --build -d
```
The Frontend will be available at `http://localhost:80`, and the Backend API at `http://localhost:8080`.

### Option B: Manual Setup (Development Mode)

#### 1. Hybrid Infrastructure Setup
The project uses two separate PostgreSQL instances and LocalStack for AWS simulation. Ensure Docker is running and execute:
```bash
docker compose up postgres-onprem postgres-cloud localstack -d
```
**Service Port Mapping:**
- **On-Premise DB:** `localhost:5432` (Database: `scm_onprem`)
- **Cloud Simulation DB:** `localhost:5433` (Database: `scm_cloud`)
- **LocalStack (AWS):** `localhost:4566` (S3, SQS)

#### 2. Backend Service
Requires Java 21 and Maven.
```bash
cd backend
mvn spring-boot:run
```
*Note: On first run, Hibernate will automatically create the schema in both databases (`ddl-auto: update`).*

#### 3. Frontend Application
Requires Node.js and npm.
```bash
cd frontend
npm install
npm start
```
The UI will be available at `http://localhost:4200`.

## Verifying the Hybrid Setup

### 1. Connection Health
Verify that both database connections are active using the Spring Boot Actuator endpoint:
`GET http://localhost:8080/actuator/health`

Look for the `db` section in the JSON response to ensure both `onprem` and `cloud` status are `UP`.

### 2. Manual Sync Validation
To verify that data is correctly synchronizing from On-Premise to Cloud:
1. Create an order or update stock in the Frontend UI.
2. Check the **Audit Trail** tab in the UI for a `SUCCESS` status.
3. (Optional) Verify via CLI that the record exists in the Cloud instance:
```bash
# Query the Cloud instance (Port 5433)
psql -h localhost -p 5433 -U scm_user -d scm_cloud -c "SELECT * FROM products;"
```

## Testing

### 1. Backend Tests
Unit and integration tests (using Testcontainers). Requires Docker for integration tests.
```bash
cd backend
mvn test
```

### 2. Frontend Unit Tests
Karma and Jasmine tests.
```bash
cd frontend
# Run in watch mode
npm test
# Run one-time (headless)
npm test -- --watch=false --browsers=ChromeHeadless
```

### 3. Frontend E2E Tests (Cypress)
Requires both Backend and Frontend servers to be running.
```bash
# In separate terminal windows:
# 1. Start Backend: cd backend && mvn spring-boot:run
# 2. Start Frontend: cd frontend && npm start

# Then run Cypress:
cd frontend
npx cypress run
```

## Key Features
- **Real-time Inventory Tracking:** Live updates via WebSockets.
- **Event-Driven Cloud Sync:** Asynchronous order synchronization using **Amazon SQS** for high resilience and decoupling.
- **Cloud Document Storage:** Automated generation and upload of order documents to **Amazon S3**.
- **Message-Driven Engine:** Robust data synchronization using Spring Integration.
- **Resilience & Reliability:** Automatic retries with **Exponential Backoff** to handle transient failures.
- **Hexagonal Design:** Decoupled domain logic for high maintainability.
- **Optimistic Locking:** Robust concurrency handling for stock management.

## Technical Lessons Learned (Gotchas)

- **SQL Initialization vs Hibernate DDL-Auto:** In Spring Boot 3.x, `data.sql` runs *before* Hibernate's schema generation by default. Setting `spring.sql.init.mode: always` while using `ddl-auto: update` can lead to startup failures (e.g., "Table not found") if the schema isn't already present. We keep it as `never` to ensure tests run smoothly with Testcontainers and only enable it explicitly via Docker Compose or manual override when seeding a fresh environment.
- **Testcontainers Performance:** Initially, each integration test class started its own set of Docker containers, leading to port conflicts and slow execution. We implemented the **Singleton Container pattern** in `AbstractIntegrationTest`, where containers are started once in a static block and shared across the entire test suite.
- **JPA Lazy Loading in Tests:** When verifying synchronization in the Cloud database using `EntityManager`, we encountered `LazyInitializationException` because the test session was different from the one used by the sync engine. We resolved this by using explicit `JOIN FETCH` queries in tests to eagerly load related entities (like `OrderItem`).
- **Data Type Consistency:** Notice that `ProductEntity` uses `BigDecimal` for precision in the catalog, while `OrderItemEntity` uses `Double` for historical snapshots. Our `CloudSyncProcessor` handles these mappings via direct JDBC to ensure high-performance upserts without Hibernate overhead.

## Monitoring & Health Checks
The application uses Spring Boot Actuator to provide production-ready monitoring.
- **Health Endpoint:** `GET /actuator/health`
- **Details:** The health check includes a custom `DatabaseHealthIndicator` that verifies connectivity to both the On-Premise and Cloud databases independently.

## System Architecture & Request Flow

```mermaid
graph TD
    subgraph "Frontend (Angular)"
        UI[Angular Components]
        Store[NgRx State]
        WS_Client[WebSocket Client]
    end

    subgraph "Backend (Spring Boot - Hexagonal)"
        direction TB
        REST[REST Controller Adapter]
        WS_Server[WebSocket Server Adapter]
        
        subgraph "Application & Domain"
            direction LR
            OrderSvc[Order Service]
            InventorySvc[Inventory Service]
            DocSvc[Order Document Service]
            EventPub[Order Event Publisher]
        end
        
        Persist[Persistence Adapter]
    end

    subgraph "Infrastructure & Cloud (LocalStack)"
        DB_Local[(PostgreSQL On-Prem)]
        DB_Cloud[(PostgreSQL Cloud)]
        S3[(Amazon S3 - Documents)]
        SQS{Amazon SQS - Events}
    end

    %% Flow
    UI -->|REST API| REST
    REST --> OrderSvc
    
    OrderSvc -->|1. Save| Persist
    Persist --> DB_Local
    
    OrderSvc -->|2. Document| DocSvc
    DocSvc -->|Upload| S3
    
    OrderSvc -->|3. Notify| EventPub
    EventPub -->|Publish| SQS
    
    SQS -->|Async Sync| DB_Cloud
    
    InventorySvc -.->|Updates| WS_Server
    WS_Server -.->|Push| WS_Client
```

## Database Schema

The following Entity Relationship Diagram (ERD) represents the data model used in the On-Premise database. This schema is mirrored (fully or partially) in the Cloud environment for analytics.

```mermaid
erDiagram
    PRODUCT ||--o{ STOCK : "has"
    WAREHOUSE ||--o{ STOCK : "stores"
    ORDER ||--o{ ORDER_ITEM : "contains"
    PRODUCT ||--o{ ORDER_ITEM : "ordered in"

    PRODUCT {
        uuid id PK
        string sku UK
        string name
        string description
        decimal basePrice
    }

    WAREHOUSE {
        uuid id PK
        string name
        string location
    }

    STOCK {
        uuid id PK
        uuid productId FK
        uuid warehouseId FK
        int quantity
        datetime lastUpdated
        long version
    }

    ORDER {
        uuid id PK
        string customerName
        string status
        datetime createdAt
        datetime updatedAt
    }

    ORDER_ITEM {
        uuid id PK
        uuid orderId FK
        uuid productId FK
        int quantity
        double price
    }

    SYNC_LOG {
        uuid id PK
        string entityName
        uuid entityId
        string status
        string errorMessage
        datetime syncTimestamp
    }
```

## Hybrid Synchronization Strategy

This project demonstrates two distinct patterns for data synchronization, each chosen for its specific strengths:

### 1. Database Polling (Spring Integration)
- **Used for:** Inventory updates and Warehouse metadata.
- **Pattern:** **Transactional Outbox**.
- **Why:** Absolute data integrity. Inventory changes are mission-critical and must be consistent with the local database. The "Outbox" (Sync Log) ensures that no stock update is ever lost, even during system crashes, by handling synchronization in reliable batches.

### 2. Event-Driven (Amazon SQS)
- **Used for:** Order processing and Cloud-Native extensions.
- **Pattern:** **Asynchronous Messaging**.
- **Why:** High decoupling and low latency. Placing an order is a complex business event that triggers multiple downstream actions (S3 documentation, Cloud sync). SQS allows the system to respond instantly to the user while the "heavy lifting" happens asynchronously in the background.

## Detailed Request Flow

1.  **User Interaction:** The user performs an action in the Angular UI (e.g., login, placing an order, or checking inventory).
2.  **API Request:** The Frontend sends a REST request to the Backend through the `REST Controller Adapter`.
3.  **Domain Processing:**
    *   **Authentication:** Handled via JWT and Spring Security.
    *   **Inventory Requests:** Handled by the `Inventory Service`.
    *   **Order Requests:** Handled by the `Order Service`, which orchestrates stock reduction, S3 document generation, and SQS event publishing. The `Order Controller` also provides endpoints for retrieving these documents from S3.
4.  **Persistence:** The `Persistence Adapter` saves the state to the **On-Premise Database** (PostgreSQL). For inventory changes, it also creates a `SyncLog` entry (Transactional Outbox).
5.  **Dual-Path Synchronization:**
    *   **Path A (Inventory):** The **Spring Integration Engine** polls pending logs and synchronizes them to the Cloud DB.
    *   **Path B (Orders):** The **SQS Listener** picks up order events from the queue for near real-time processing and cloud archival.
6.  **Real-time Updates:** Successful changes trigger `WebSocket` notifications, allowing the UI to reflect updates across all connected clients instantly.

