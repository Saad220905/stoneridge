# Stoneridge

<div align="center">
  <h1 align="center">StoneRidge</h1>
  <p align="center">
    <b>A Modern Banking Platform for the Next Generation</b>
  </p>
  <p align="center">
    <a href="https://nextjs.org/">
      <img src="https://img.shields.io/badge/Next.js-15-black?style=flat-square&logo=next.js" alt="Next.js">
    </a>
    <a href="https://www.typescriptlang.org/">
      <img src="https://img.shields.io/badge/TypeScript-5-blue?style=flat-square&logo=typescript" alt="TypeScript">
    </a>
    <a href="https://spring.io/projects/spring-boot">
      <img src="https://img.shields.io/badge/Spring%20Boot-3-green?style=flat-square&logo=springboot" alt="Spring Boot">
    </a>
    <a href="https://www.postgresql.org/">
      <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=flat-square&logo=postgresql" alt="PostgreSQL">
    </a>
    <a href="https://tailwindcss.com/">
      <img src="https://img.shields.io/badge/Tailwind_CSS-3-38B2AC?style=flat-square&logo=tailwind-css" alt="Tailwind CSS">
    </a>
  </p>
</div>

---

## 🚀 Introduction

**Stoneridge** is a comprehensive financial SaaS platform designed to connect multiple bank accounts, monitor real-time transactions, and facilitate secure fund transfers. Built with a robust **Java Spring Boot** backend and a dynamic **Next.js** frontend, Stoneridge bridges the gap between traditional banking and modern user experience.

Developed by **Saad Kashif**, this project showcases advanced full-stack capabilities, integrating third-party financial APIs like **Plaid** and **Dwolla** to deliver a production-ready fintech solution.

## ✨ Key Features

- **🔐 Secure Authentication**: Robust user registration and login system secured with JWT and Spring Security.
- **🏦 Multi-Bank Integration**: Seamlessly link and manage multiple bank accounts using **Plaid**.
- **💸 Fund Transfers**: Securely transfer funds between accounts with **Dwolla** integration.
- **📊 Real-Time Dashboard**: Visualize your total balance, recent transactions, and spending categories with interactive charts.
- **📜 Transaction History**: Detailed logs of all your financial activities, categorized for better insights.
- **📱 Responsive Design**: A fully responsive UI built with **Tailwind CSS** and **Shadcn UI**, ensuring a perfect experience on desktop and mobile.

## 🛠️ Tech Stack

### Frontend
- **Framework**: [Next.js 15](https://nextjs.org/) (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS, Shadcn UI, Class Variance Authority
- **State Management**: React Hooks, Query String
- **Forms**: React Hook Form, Zod
- **Visuals**: Chart.js, Lucide React

### Backend
- **Framework**: [Spring Boot 3.2.4](https://spring.io/projects/spring-boot)
- **Language**: Java 21
- **Database**: PostgreSQL
- **Security**: Spring Security, JWT (JJWT)
- **Build Tool**: Maven

### Integrations
- **Banking Data**: [Plaid](https://plaid.com/)
- **Payments**: [Dwolla](https://www.dwolla.com/)
- **Monitoring**: [Sentry](https://sentry.io/)

## 📸 Screenshots

<div align="center">
  <img src="public/auth-image.jpg" alt="Authentication Page" width="800">
  <p><i>Secure and Elegant Authentication</i></p>
</div>

## ⚙️ Getting Started

Follow these instructions to set up the project locally.

### Prerequisites

- **Node.js** (v18+)
- **Java JDK** (v21)
- **Maven**
- **Docker** (optional, for PostgreSQL) or a running PostgreSQL instance
- API Keys for **Plaid** and **Dwolla**

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/stoneridge.git
cd stoneridge
```

### 2. Backend Setup

Navigate to the backend directory:

```bash
cd backend
```

Configure your environment variables in `src/main/resources/application.properties`. You will need to set up your database connection, API keys, and JWT secret:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/stoneridge_db
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

# Plaid Configuration
plaid.client_id=your_plaid_client_id
plaid.secret=your_plaid_secret
plaid.env=sandbox

# Dwolla Configuration
dwolla.key=your_dwolla_key
dwolla.secret=your_dwolla_secret
dwolla.env=sandbox

# JWT Secret
jwt.secret=your_very_secure_and_long_secret_key_at_least_32_bytes
```

Build and run the backend:

```bash
mvn spring-boot:run
```

The backend server will start at `http://localhost:8000` (or your configured port).

### 3. Frontend Setup

Navigate to the project root (frontend):

```bash
cd ..
```

Install dependencies:

```bash
npm install
# or
yarn install
```

Create a `.env.local` file in the root directory and configure the backend URL:

```bash
NEXT_PUBLIC_BACKEND_URL=http://localhost:8000
```

Run the development server:

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser to see the application.

## 🤝 Contributing

Contributions are welcome! Please fork the repository and create a pull request with your changes.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <p>Built with ❤️ by <b>Saad Kashif</b></p>
  <p>
    <a href="https://github.com/saadkashif">GitHub</a> •
    <a href="https://linkedin.com/in/saadkashif">LinkedIn</a>
  </p>
</div>
