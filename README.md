# MasonSaver

A full-stack textbook price comparison application built with Java Spring Boot, PostgreSQL, and Microsoft Azure.

MasonSaver helps George Mason University students compare textbook prices before purchasing. The application includes secure user authentication, a REST API, a PostgreSQL database, Azure cloud deployment, and automated CI/CD using GitHub Actions.

---

## Live Demo

### Frontend

https://nice-sky-082512f0f.7.azurestaticapps.net

### Backend API

https://masonsaver-backend-raheem-eqctbph9bycpfhf8.eastus2-01.azurewebsites.net/api/health

Expected response:

```json
{
  "application": "MasonSaver API",
  "status": "UP"
}
```

---

---

# Application

<table>
<tr>
<td align="center">
<b>Landing Page</b><br><br>
<img src="LANDING_PAGE_IMAGE" width="430">
</td>

<td align="center">
<b>Create Account</b><br><br>
<img src="CREATE_ACCOUNT_IMAGE" width="430">
</td>
</tr>

<tr><td height="25"></td></tr>

<tr>
<td align="center">
<b>Login</b><br><br>
<img src="LOGIN_IMAGE" width="430">
</td>

<td align="center">
<b>Textbook Search (Demo)</b><br><br>
<img src="TEXTBOOK_SEARCH_IMAGE" width="430">
</td>
</tr>

<tr><td height="25"></td></tr>

<tr>
<td align="center" colspan="2">
<b>Student Dashboard</b><br><br>
<img src="DASHBOARD_IMAGE" width="860">
</td>
</tr>
</table>

> **Note:** Textbook search currently displays demo pricing. Live textbook listings will be powered by the eBay Browse API once API access is approved.

---

# Cloud Deployment

<table>
<tr>
<td align="center">
<b>Azure Static Web App</b><br><br>
<img src="AZURE_STATIC_WEB_APP_IMAGE" width="430">
</td>

<td align="center">
<b>Azure App Service</b><br><br>
<img src="AZURE_APP_SERVICE_IMAGE" width="430">
</td>
</tr>
</table>

The frontend is hosted using **Azure Static Web Apps**, while the Java Spring Boot backend is deployed to **Azure App Service**.

---

# Continuous Integration / Continuous Deployment

<table>
<tr>
<td align="center">
<b>Frontend Deployment Pipeline</b><br><br>
<img src="https://github.com/user-attachments/assets/2830137f-c449-4086-a87c-cfb9c288df3f" width="430">
</td>

<td align="center">
<b>Backend Deployment Pipeline</b><br><br>
<img src="https://github.com/user-attachments/assets/2d97e951-cb13-4855-a90a-ae9961b5823b" width="430">
</td>
</tr>
</table>
Every push to the **main** branch automatically triggers GitHub Actions workflows that build and deploy the frontend and backend to Microsoft Azure.

---

**Note:** The textbook search page currently displays sample pricing data. Live results will be added after eBay Browse API access is approved.

---

## Azure Deployment

<table>
<tr>
<td align="center">
<b>Azure Static Web App</b><br><br>
<img src="https://github.com/user-attachments/assets/b3572ade-449c-443b-ac14-cb1aad6f2959" width="420">
</td>

<td align="center">
<b>Azure App Service</b><br><br>
<img src="https://github.com/user-attachments/assets/278c3b39-6335-4395-a42f-67a5288044c0" width="420">
</td>
</tr>
</table>

---

## Features

- User registration
- User authentication
- BCrypt password hashing
- Spring Boot REST API
- PostgreSQL database
- Azure Static Web Apps deployment
- Azure App Service deployment
- GitHub Actions CI/CD
- Responsive frontend

---

## Tech Stack

| Category | Technologies |
|----------|--------------|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java, Spring Boot, Spring Data JPA |
| Database | PostgreSQL |
| Cloud | Azure Static Web Apps, Azure App Service |
| DevOps | Git, GitHub, GitHub Actions, Maven |

---

## Architecture

```text
Browser
    │
    ▼
Azure Static Web Apps
    │
    ▼
Spring Boot REST API
    │
    ▼
PostgreSQL Database
```

---

## Authentication Flow

```text
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

Passwords are hashed with BCrypt before being stored in PostgreSQL.

---

## REST API

### Register

```http
POST /api/auth/register
```

### Login

```http
POST /api/auth/login
```

### Health Check

```http
GET /api/health
```

---

## Project Status

### Completed

- Azure deployment
- Spring Boot backend
- PostgreSQL integration
- User authentication
- BCrypt password hashing
- GitHub Actions CI/CD
- Responsive frontend

### In Progress

- eBay Browse API integration
- Live textbook price comparison
- Amazon marketplace comparison
- Saved searches
- Favorite textbooks
- Price alerts

---

## Skills Demonstrated

- Java
- Spring Boot
- REST API Development
- PostgreSQL
- Microsoft Azure
- Cloud Deployment
- Authentication
- BCrypt Password Hashing
- GitHub Actions
- CI/CD
- Git
- Full-Stack Development

---

## Author

**Raheem**

Computer Science Student  
George Mason University

Interested in Cloud Engineering, Backend Development, DevOps, and Microsoft Azure.

---

## License

MIT License
