# MasonSaver

> A cloud-deployed full-stack textbook price comparison platform built with **Spring Boot**, **PostgreSQL**, and **Microsoft Azure**.

MasonSaver helps George Mason University students compare textbook prices before purchasing. This project demonstrates secure authentication, REST API development, cloud deployment, CI/CD automation, and Azure infrastructure.

---

# 🚀 Live Demo

### Frontend (Azure Static Web App)

https://nice-sky-082512f0f.7.azurestaticapps.net

### Backend API Health Check

https://masonsaver-backend-raheem-eqctbph9bycpfhf8.eastus2-01.azurewebsites.net/api/health

Expected response:

```json
{
  "application": "MasonSaver API",
  "status": "UP"
}
```

---

# 📸 Application

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
<td align="center">
<b>Dashboard</b><br><br>
<img src="https://github.com/user-attachments/assets/c79ee436-4d5c-4b86-a69f-6b07f311d49c" width="430">
</td>

<td align="center">
<b>GitHub Actions CI/CD</b><br><br>
<img src="https://github.com/user-attachments/assets/40d52e2f-720e-4c4a-b6f7-f7c9dd889233" width="430">
</td>
</tr>
</table>

> **Note:** Textbook search currently displays demo data. Live eBay Browse API integration will replace these results once API access is approved.

---

# ☁ Azure Deployment

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

# ✨ Features

- Secure user registration
- Secure login authentication
- BCrypt password hashing
- Java Spring Boot REST API
- PostgreSQL database integration
- Azure Static Web Apps deployment
- Azure App Service deployment
- GitHub Actions CI/CD
- Responsive frontend
- Cloud-hosted full-stack architecture

---

# 🛠 Tech Stack

| Category | Technologies |
|-----------|--------------|
| Frontend | HTML, CSS, JavaScript |
| Backend | Java, Spring Boot |
| Database | PostgreSQL |
| Cloud | Azure Static Web Apps, Azure App Service |
| DevOps | Git, GitHub, GitHub Actions, Maven |

---

# 🏗 Architecture

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
Azure PostgreSQL Database
```

---

# 🔐 Authentication Flow

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

Passwords are encrypted using **BCrypt** before being stored in PostgreSQL.

---

# 🌐 REST API

### Register

```
POST /api/auth/register
```

### Login

```
POST /api/auth/login
```

### Health Check

```
GET /api/health
```

---

# 📈 Current Status

## ✅ Completed

- Full-stack Azure deployment
- Spring Boot backend
- PostgreSQL integration
- Secure authentication
- BCrypt password encryption
- GitHub Actions CI/CD
- Responsive UI

## 🚧 In Progress

- Live eBay Browse API integration
- Real textbook price comparison
- Amazon Marketplace integration
- Saved searches
- Favorites
- Price alerts

---

# 📚 Skills Demonstrated

- Microsoft Azure
- Cloud Deployment
- REST API Development
- Java Spring Boot
- PostgreSQL
- Authentication & Authorization
- BCrypt Password Encryption
- GitHub Actions CI/CD
- Full-Stack Development
- Git Version Control

---

# 👨‍💻 Author

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

MIT License
