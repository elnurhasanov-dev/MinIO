# MinIO File Management System

**Overview**

This project provides a file management system that integrates with MinIO for file storage and PostgreSQL for metadata storage. It allows users to upload, list, retrieve, and manipulate files stored in MinIO. The system is built using Spring Boot and provides RESTful APIs for file operations.

## Features

* 📂 Upload files to MinIO with a specified folder.
* 📜 List files in a given folder.
* 🔗 Retrieve the URL of a stored file.
* 📥 Download individual files.
* 📦 Download an entire folder as a ZIP file.
* 📁 Create folder structures in MinIO.

## Project Structure

📌 **Controller**
 
```FileController```
 
Handles incoming HTTP requests related to file operations and interacts with the FileService to perform the required actions.

***Endpoints:***

* **POST** ```/api/files/upload``` - 📤 Uploads a file to MinIO under a specific folder.
* **GET** ```/api/files/list/{prefix}``` - 📄 Lists all files under a given folder prefix.
* **GET** ```/api/files/url/{prefix}/{fileName}``` - 🔗 Retrieves a pre-signed URL for accessing a file.
* **GET** ```/api/files/download/{folder}/{fileName}``` - 📥 Downloads a specific file from MinIO.
* **GET** ```/api/files/download-folder/{folder}``` - 📦 Downloads an entire folder as a ZIP file.
* **POST** ```/api/files/create-folder``` - 📁 Creates a new folder in MinIO.

🛠️ **Services**

```FileService```

* This service handles business logic for file operations. It interacts with MinioService for storage-related actions and FileRepository for database persistence.
* Calls MinioService for uploading, downloading, and listing files.
* Stores file metadata in PostgreSQL.
* Converts and returns data in FileDto format.

```MinioService```

Handles direct interactions with MinIO, including file uploads, downloads, folder creation, and retrieving URLs.

##  Running the Application

**Prerequisites**

* Docker & Docker Compose installed
* Java 17+
* PostgreSQL (configured in application.yml)

**Setup & Execution**

Clone the repository:

```bash
git clone <repository-url>
cd <project-folder>
``` 

Start the services using Docker Compose:
```bash
docker-compose up -d
```

Run the application:
```bash
./mvnw spring-boot:run
```

## 🛠️ Technologies Used

* **Spring Boot** - Backend framework
* **MinIO** - Object storage service
* **PostgreSQL** - Database for storing file metadata
* **Docker & Docker Compose** - Containerization and service orchestration