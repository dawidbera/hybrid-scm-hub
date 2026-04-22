# Hybrid-Cloud SCM Hub

A comprehensive solution for bridging On-Premise warehouse operations with Cloud analytics.

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.4, Hibernate, Spring Integration, **Spring Boot Actuator**.
- **Frontend:** Angular 16, NgRx, SCSS, RxJS.
- **Database:** 2x PostgreSQL (On-Prem & Cloud simulation).
- **Architecture:** Hexagonal (Ports & Adapters).

## Project Structure
- `backend/`: Spring Boot application.
- `frontend/`: Angular application.
- `docker-compose.yml`: Infrastructure (Postgres).

## How to Run
1.  **Start Databases:**
    ```bash
    docker compose up -d
    ```
2.  **Run Backend:**
    ```bash
    cd backend
    mvn spring-boot:run
    ```
3.  **Run Frontend:**
    ```bash
    cd frontend
    npm install
    npm start
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
- **Real-time Inventory Tracking:** Live updates via WebSockets (simulated).
- **Hybrid Sync Engine:** Automated data push from local to cloud instances.
- **Hexagonal Design:** Decoupled domain logic for high maintainability.
- **Optimistic Locking:** Robust concurrency handling for stock management.
- **Health Monitoring:** Dedicated Actuator endpoints for tracking On-Prem and Cloud database connectivity.

## Monitoring & Health Checks
The application uses Spring Boot Actuator to provide production-ready monitoring.
- **Health Endpoint:** `GET /actuator/health`
- **Details:** The health check includes a custom `DatabaseHealthIndicator` that verifies connectivity to both the On-Premise and Cloud databases independently.

## System Architecture & Request Flow

The system follows the **Hexagonal Architecture** (Ports & Adapters) to ensure business logic remains decoupled from external infrastructure. While technically a modular monolith, the services are designed with microservice principles in mind (Inventory, Order, Sync Log).

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
            InventorySvc[Inventory Service]
            OrderSvc[Order Service]
            SyncSvc[Sync Log Service]
            AuthSvc[Auth Service]
            Domain[Domain Models]
        end
        
        Persist[Persistence Adapter]
        Sync[Sync Engine - Spring Integration]
    end

    subgraph "Infrastructure"
        DB_Local[(PostgreSQL On-Prem)]
        DB_Cloud[(PostgreSQL Cloud Simulation)]
    end

    %% Flow
    UI -->|REST API| REST
    REST --> AuthSvc
    REST --> InventorySvc
    REST --> OrderSvc
    REST --> SyncSvc
    
    OrderSvc -->|Reduce Stock| InventorySvc
    
    InventorySvc --> Domain
    OrderSvc --> Domain
    SyncSvc --> Domain
    
    InventorySvc --> Persist
    OrderSvc --> Persist
    SyncSvc --> Persist
    
    Persist --> DB_Local
    
    DB_Local -.->|Change Capture| Sync
    Sync -->|Push| DB_Cloud
    
    InventorySvc -.->|Updates| WS_Server
    WS_Server -.->|Push Notifications| WS_Client
    WS_Client --> Store
    Store --> UI
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
        int retryCount
    }
```

### Request Flow Overview:
1.  **User Interaction:** The user performs an action in the Angular UI (e.g., login, placing an order, or checking inventory).
2.  **API Request:** The Frontend sends a REST request to the Backend through the `REST Controller Adapter`.
3.  **Domain Processing:**
    *   **Authentication:** Handled by the `Auth Service` using JWT.
    *   **Inventory Requests:** Handled directly by the `Inventory Service`.
    *   **Order Requests:** Handled by the `Order Service`, which orchestrates with the `Inventory Service` to ensure stock availability and reduction.
4.  **Persistence:** The `Persistence Adapter` saves the state to the **On-Premise Database** (PostgreSQL).
5.  **Synchronization:** The **Sync Engine** (via Spring Integration) detects local DB changes and asynchronously synchronizes them to the **Cloud Database**.
6.  **Real-time Updates:** Successful inventory changes trigger `WebSocket` notifications via the `WebSocket Server Adapter`, allowing the UI to reflect updates across all connected clients instantly.
