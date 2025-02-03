# Finance Tracker

**Your complete personal finance management solution**

Finance Tracker is a modular microservice-based application that empowers users to manage their financial activities effectively. From tracking budgets, transactions, and assets to providing insightful analytics, this application offers a comprehensive suite of features. Built with Spring Boot, Spring Cloud, Docker, and Angular, Finance Tracker ensures scalability, flexibility, and a user-friendly experience.

---

## Overview

The Finance Tracker application is organized into multiple microservices, each handling a specific domain of financial management. Users benefit from real-time notifications, detailed analytics, and secure authentication to manage their financial data efficiently.

### Key Features

- **User Management with JWT Authentication:** Secure user authentication and role management.
- **Budget Tracking:** Create, monitor, and update budgets for specific categories.
- **Transaction Management:** Record and manage income and expenses.
- **Asset Management:** Track and manage user assets, including cash and investments.
- **Financial Analytics:** Summarize and analyze user financial data to provide actionable insights.
- **Notifications:** Email alerts for critical updates like budget limits and unusual transactions.
- **Load Balancing:** The API Gateway uses Spring Cloud Gateway and Eureka for service discovery and load balancing across multiple service instances.
---

## Architecture

The application follows the microservices architecture pattern. Each service is independent and communicates using REST APIs.

### Services Overview

#### **1. User Service**
Handles user authentication, registration, and management. Implements JWT for secure authentication.

| Method | Path                              | Description                            | Authentication Required | Available from UI |
|--------|-----------------------------------|----------------------------------------|-------------------------|-------------------|
| POST   | `/users/register`                 | Register a new user                    |                         | ✓                 |
| POST   | `/users/login`                    | Authenticate and generate JWT          |                         | ✓                 |
| GET    | `/users/{id}`                     | Retrieve user profile                  | ✓                       | ✓                 |
| PUT    | `/users/{id}`                     | Update user profile                    | ✓                       | ✓                 |

---

#### **2. Transaction Service**
Manages user transactions, categorizing them as income or expenses.

| Method | Path                              | Description                            | Authentication Required | Available from UI |
|--------|-----------------------------------|----------------------------------------|-------------------------|-------------------|
| POST   | `/api/transactions`               | Create a new transaction               | ✓                       | ✓                 |
| GET    | `api/transactions/user/{userId}`  | Retrieve all transactions for a user   | ✓                       | ✓                 |

---

#### **3. Recurrinng Payment Service**
Manages recurring payments, updating and creating transactions based on frequency.

| Method | Path                              | Description                            | Authentication Required | Available from UI |
|--------|-----------------------------------|----------------------------------------|-------------------------|-------------------|
| POST   | `/api/recurring-payments`         | Create a new recurring payment         | ✓                       | ✓                 |
| GET    | `/api/recurring-payments/{id}`    | Get recurring payment by id            | ✓                       | ✓                 |

---

#### **4. Budget Service**
Facilitates the creation and management of budgets.

| Method | Path                              | Description                            | Authentication Required | Available from UI |
|--------|-----------------------------------|----------------------------------------|-------------------------|-------------------|
| POST   | `/budgets`                        | Create a new budget                    | ✓                       | ✓                 |
| GET    | `/budgets/user/{userId}`          | Retrieve budgets for a specific user   | ✓                       | ✓                 |

---

#### **5. Asset Service**
Tracks and updates user assets, including cash and investments.

| Method | Path                              | Description                            | Authentication Required | Available from UI |
|--------|-----------------------------------|----------------------------------------|-------------------------|-------------------|
| GET    | `/assets/cash/{userId}`           | Get cash asset for a user              | ✓                       | ✓                 |
| PUT    | `/assets/{assetId}`               | Update an existing asset               | ✓                       |                   |

---

#### **6. Notification Service**
Sends email notifications based on user activities.

| Method | Path                              | Description                            | Authentication Required | Available from UI |
|--------|-----------------------------------|----------------------------------------|-------------------------|-------------------|
| POST   | `/notifications/email`            | Send a notification email              | ✓                       |                   |
| GET    | `/notifications/{userId}`         | Retrieve notifications for a user      | ✓                       | ✓                 |

---

#### **7. Analytics Service**
Generates summaries and insights from financial data.

| Method | Path                              | Description                            | Authentication Required | Available from UI |
|--------|-----------------------------------|----------------------------------------|-------------------------|-------------------|
| GET    | `/analytics/{userId}`             | Retrieve financial summary for a user  | ✓                       | ✓                 |

---

## Technology Stack

- **Backend:** Java, Spring Boot, Spring Cloud Gateway, Eureka
- **Frontend:** Angular
- **Database:** MySQL
- **Authentication:** JWT Authentication
- **Communication:** REST APIs, WebClient
- **Containerization:** Docker
- **Other Tools:** Postman, Git

---

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/finance-tracker.git
   cd finance-tracker
