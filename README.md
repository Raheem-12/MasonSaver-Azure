# MasonSaver

A cloud-deployed full-stack textbook price comparison platform built with Spring Boot, PostgreSQL, and Microsoft Azure.

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

## Application

### User Interface

<table>
<tr>
<td align="center">
<b>Landing Page</b><br><br>
<img src="https://github.com/user-attachments/assets/587f665d-1014-408e-9973-087cca3a1787" width="430">
</td>

<td align="center">
<b>Create Account</b><br><br>
<img src="https://github.com/user-attachments/assets/08ad2f75-b4ea-4ff4-ba97-f5deca58cc30" width="430">
</td>
</tr>

<tr><td height="25"></td></tr>

<tr>
<td align="center">
<b>Login</b><br><br>
<img src="https://github.com/user-attachments/assets/9e2e1df9-c975-4568-b97c-7bbaf7f13c61" width="430">
</td>

<td align="center">
<b>Textbook Search (Demo)</b><br><br>
<img src="https://github.com/user-attachments/assets/2daa3a84-3e99-413d-b130-150ecf98083e" width="430">
</td>
</tr>

<tr><td height="25"></td></tr>

<tr>
<td align="center" colspan="2">
<b>Dashboard</b><br><br>
<img src="https://github.com/user-attachments/assets/c79ee436-4d5c-4b86-a69f-6b07f311d49c" width="430">
</td>
</tr>
</table>

**Note:** The textbook search page currently displays sample pricing data. Live results will be available after eBay Browse API access is approved.

---

### Cloud Deployment

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

<tr><td height="25"></td></tr>

<tr>
<td align="center">
<b>Azure Static Web App</b><br><br>
<img src="https://github.com/user-attachments/assets/b3572ade-449c-443b-ac14-cb1aad6f2959" width="430">
</td>

<td align="center">
<b>Azure App Service</b><br><br>
<img src="https://github.com/user-attachments/assets/278c3b39-6335-4395-a42f-67a5288044c0" width="430">
</td>
</tr>
</table>
**Note:** The textbook search page currently displays sample pricing data. Live results will be available after eBay Browse API access is approved.

---

## Features

- Responsive web interface
- User registration
- Secure user authentication
- BCrypt password hashing
- Spring Boot REST API
- PostgreSQL database integration
- Azure Static Web Apps deployment
- Azure App Service deployment
- Automated GitHub Actions CI/CD

---

## Tech Stack

| Category | Technologies |
|----------|--------------|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java, Spring Boot |
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

Passwords are hashed using BCrypt before being stored in PostgreSQL.

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

- Full-stack Azure deployment
- Spring Boot backend
- PostgreSQL integration
- Secure user authentication
- BCrypt password hashing
- Automated GitHub Actions CI/CD
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

- Microsoft Azure
- Cloud Deployment
- Java
- Spring Boot
- REST API Development
- PostgreSQL
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

Interested in Cloud Engineering, DevOps, Backend Development, and Infrastructure Automation.

---

## License

MIT License
