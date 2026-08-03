# MasonSaver
### Cloud-Deployed Full-Stack Textbook Price Comparison Platform

MasonSaver is a cloud-hosted web application that helps George Mason University students compare textbook prices before purchasing.

The application features secure user authentication, a Java Spring Boot REST API, a PostgreSQL database, automated Azure deployments through GitHub Actions, and a responsive frontend hosted on Microsoft Azure.

---

## Live Demo

### Frontend (Azure Static Web App)

https://nice-sky-082512f0f.7.azurestaticapps.net

### Backend API (Health Endpoint)

https://masonsaver-backend-raheem-eqctbph9bycpfhf8.eastus2-01.azurewebsites.net/

The backend health endpoint returns:

```json
{
  "application": "MasonSaver API",
  "status": "UP"
}
```

---

# Project Screenshots

## Landing Page

<img width="1164" height="2034" alt="01-homepage" src="https://github.com/user-attachments/assets/587f665d-1014-408e-9973-087cca3a1787" />
Landing page introducing MasonSaver and allowing students to create an account or sign in.

---

## Create Account

<img width="1164" height="1179" alt="02-signup" src="https://github.com/user-attachments/assets/08ad2f75-b4ea-4ff4-ba97-f5deca58cc30" />
User registration with client-side validation and Azure-hosted backend integration.

---

## Login

<img width="1164" height="942" alt="03-login" src="https://github.com/user-attachments/assets/9e2e1df9-c975-4568-b97c-7bbaf7f13c61" />
Secure login page authenticating users against the Azure-hosted Spring Boot API.

---

## Dashboard

<img width="1164" height="1139" alt="04-dashboard" src="https://github.com/user-attachments/assets/c79ee436-4d5c-4b86-a69f-6b07f311d49c" />
Authenticated dashboard where students can search for textbooks.

---

## Search

<img width="1164" height="1508" alt="05-search" src="https://github.com/user-attachments/assets/2daa3a84-3e99-413d-b130-150ecf98083e" />
Demo textbook price comparison results. Live eBay API integration will replace demo data after API approval.

---

## Azure Static Web App

<img width="943" height="368" alt="06-azure-static-web-app" src="https://github.com/user-attachments/assets/b3572ade-449c-443b-ac14-cb1aad6f2959" />
Frontend deployed globally using Azure Static Web Apps.

---

## Azure App Service

<img width="949" height="371" alt="07-azure-app-service" src="https://github.com/user-attachments/assets/278c3b39-6335-4395-a42f-67a5288044c0" />
Spring Boot backend deployed to Azure App Service.

---

## GitHub Actions

<img width="1059" height="5753" alt="08-github-actions" src="https://github.com/user-attachments/assets/40d52e2f-720e-4c4a-b6f7-f7c9dd889233" />
CI/CD pipelines automatically deploy the frontend and backend after every push to the main branch.

---

# Features

- Responsive landing page
- Secure account registration
- Secure user login
- BCrypt password hashing
- Spring Boot REST API
- PostgreSQL database
- Azure Static Web Apps deployment
- Azure App Service deployment
- GitHub Actions CI/CD
- Cloud-hosted frontend and backend
- Health monitoring endpoint

---

# Tech Stack

## Frontend

- HTML5
- CSS3
- JavaScript

## Backend

- Java
- Spring Boot
- Spring Data JPA
- Spring Security (BCrypt)

## Database

- PostgreSQL

## Cloud

- Microsoft Azure Static Web Apps
- Microsoft Azure App Service
- Azure Database for PostgreSQL

## DevOps

- Git
- GitHub
- GitHub Actions
- Maven

---

# Architecture

```
User
   │
   ▼
Azure Static Web App
   │
   ▼
Spring Boot REST API
(Azure App Service)
   │
   ▼
Azure PostgreSQL Database
```

---

# Authentication Flow

```
Landing Page
      │
      ▼
Create Account
      │
      ▼
Login
      │
      ▼
Dashboard
```

Passwords are encrypted using BCrypt before being stored in PostgreSQL.

---

# REST API

## Register User

```
POST /api/auth/register
```

Example Request

```json
{
  "fullName": "Jane Smith",
  "email": "jane@example.com",
  "password": "Password123!"
}
```

---

## Login

```
POST /api/auth/login
```

Example Request

```json
{
  "email": "jane@example.com",
  "password": "Password123!"
}
```

---

## Health Check

```
GET /api/health
```

Response

```json
{
  "application": "MasonSaver API",
  "status": "UP"
}
```

---

# Current Status

## Completed

- Responsive frontend
- User registration
- User login
- BCrypt password encryption
- PostgreSQL integration
- Spring Boot REST API
- Azure Static Web App deployment
- Azure App Service deployment
- GitHub Actions CI/CD
- Cloud deployment

## In Progress

- Live textbook price search
- eBay Browse API integration
- Amazon integration
- GMU Bookstore comparison
- Search history
- Favorites
- User profiles

---

# What I Learned

This project strengthened my experience with:

- Deploying full-stack applications to Microsoft Azure
- Hosting frontend applications with Azure Static Web Apps
- Hosting Java Spring Boot applications with Azure App Service
- Building REST APIs
- PostgreSQL database integration
- Password hashing using BCrypt
- GitHub Actions CI/CD automation
- Cloud application architecture
- Authentication workflows
- Full-stack software development

---

# Future Improvements

- Live eBay Browse API integration
- Real textbook pricing
- Amazon Marketplace integration
- ISBN barcode scanner
- Search history
- Saved favorite textbooks
- Price alerts
- Email notifications
- Docker containerization
- Terraform Infrastructure as Code
- Azure Key Vault
- Azure Application Insights
- Custom domain

---

# Author

**Raheem**

Computer Science Student

George Mason University

Interested in:

- Cloud Engineering
- Azure
- Backend Development
- DevOps
- Infrastructure Automation

---

## License

This project is licensed under the MIT License.
