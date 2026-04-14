# 🚀 Secure File Upload & Download Service

A production-ready backend service built using Spring Boot that enables secure file upload and download with strong validation and efficient handling.

---

## 📌 Features

- 📤 Upload files (Image / PDF)
- 🔐 Content-based validation using Apache Tika
- 🛡 Prevent file spoofing (extension vs actual type check)
- 📏 File size validation
- 🧠 Path traversal protection
- 🆔 UUID-based file storage
- ⚡ Streaming file download (memory efficient)
- 🗄 Metadata stored in database (MySQL)
- 🚨 Global exception handling
- 📊 Swagger API documentation

---

## 🛠 Tech Stack

- Java 17  
- Spring Boot  
- Spring Data JPA  
- MySQL  
- Apache Tika  
- SpringDoc OpenAPI (Swagger)  

---

## 📂 Project Structure

    src/
     ├── config/        # Configuration classes
     ├── controller/    # REST APIs
     ├── service/       # Business logic
     ├── serviceImpl/   # Implementation
     ├── entity/        # JPA entities
     ├── repository/    # Database layer
     ├── exception/     # Global error handling

---

## 🔐 Security Highlights

This project focuses on real-world security practices:

- Validates file content using Apache Tika (not just extension)
- Ensures extension matches actual MIME type
- Prevents malicious file uploads (e.g., renamed .exe as .pdf)
- Blocks path traversal attacks (../)
- Uses streaming to avoid memory overflow

---

## 🚀 API Endpoints

### 📤 Upload File

POST /api/files/upload

Request:
- Multipart form-data  
- Key: file  

---

### 📥 Download File

GET /api/files/download/{id}

---

## 📊 Swagger Documentation

http://localhost:8080/swagger-ui/index.html

---

## ⚙️ Configuration

file.upload-dir=uploads  
file.max-size=5242880  

---

## ▶️ Run the Project

mvn clean install  
mvn spring-boot:run  

---

## 🧪 Testing

- Postman  
- Swagger UI  

---

## 🔥 Key Learning

Solved a real-world dependency conflict between Spring Boot and Swagger causing runtime errors, demonstrating the importance of version compatibility.

---

## 📩 Email

neerajkumarsharma345@gmail.com


---

## ⭐ If you like this project, consider giving it a star!
