# Hybrid-Cloud SCM Hub

A comprehensive solution for bridging On-Premise warehouse operations with Cloud analytics.

## Tech Stack
- **Backend:** Java 21, Spring Boot 3.4, Hibernate, Spring Integration.
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
    docker-compose up -d
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

## Key Features
- **Real-time Inventory Tracking:** Live updates via WebSockets (simulated).
- **Hybrid Sync Engine:** Automated data push from local to cloud instances.
- **Hexagonal Design:** Decoupled domain logic for high maintainability.
- **Optimistic Locking:** Robust concurrency handling for stock management.

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
