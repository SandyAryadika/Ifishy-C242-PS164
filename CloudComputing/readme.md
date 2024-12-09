# Architecture Cloud Design
<img src="https://github.com/SandyAryadika/Ifishy-C242-PS164/blob/main/CloudComputing/Ifishy%20Architecture.png" alt="Ifishy architecture">
The architecture of IFISHY involves:

- Cloud Run: Handles both the backend and Python-based prediction API.
- Cloud SQL: Manages relational database operations.
- Cloud Storage: Stores user-uploaded files like profile photos and posts.
- Artifact Registry: Manages Docker container images.
- Logging: allowing for centralized monitoring of the system’s behavior and performance.

# [Google Cloud Price Outlook Preview](https://cloud.google.com/products/calculator/estimate-preview/CiQwZjU5NWZkNS00OTY4LTRjYTEtYmYwZi1iYjdkYWZjNjVjNTAQAg==?hl=en)
The Google Cloud Estimated Price Preview outlines the services utilized for this project. Artifact Registry is employed for managing container images, specifically for inter-region egress and storage. Cloud Run is used for deploying both the Backend API and FastAPI, leveraging scalable CPU and memory allocation to handle API requests efficiently.

Cloud SQL (MySQL) serves as the primary database, operating regionally in Jakarta with a micro-instance and zonal standard storage to manage user and application data. Cloud Storage is utilized to store application assets, such as images and related files, in the Jakarta region.

# [Ifishy API Endpoints Documentation](https://docs.google.com/spreadsheets/d/1AnR8ifyktgZRyspRoGS_CgjIDOoaJQElb6PeBWj_5q8/edit?gid=0#gid=0)
This API supports various features for user management, community interaction, article access, and fish disease detection. Endpoints like `/users`, `/dashboard/:email`, and `/profile/:email` provide access to user data, including profiles and dashboards. Users can update their information through `/update` or delete their accounts with `/delete`. Profile photos are managed via `/upload-photo` and `/update-photo-profile`.

Community features include creating, updating, and interacting with posts through endpoints such as `/community/posts` for creating or accessing post lists, and `/community/posts/:postId` for post details or updates. Comments and replies can be added or viewed via `/community/posts/:postId/comments` and `/comments/:commentId/reply`. Users can also `upvote/downvote` posts and check their voting status through relevant endpoints.

Articles can be accessed using `/articles` for a list or `/articles/:id` for details. The bookmark feature allows saving favorite articles or posts through `/bookmark` and `/bookmarks`.

For fish disease detection, the `/scan-history` endpoint allows saving scan results, while `/scan-history/:userId` retrieves the scan history for a specific user ID.

The `/predict` endpoint is designed to handle fish disease detection. Users can upload an image file, and the API processes the image to provide a prediction result, identifying the disease and its confidence level. This endpoint serves as the core of the application’s fish health analysis feature, leveraging machine learning for accurate predictions.

# Technology Stack
IFISHY utilizes a robust and modern tech stack for its development and deployment:

### Backend
- Node.js: The core runtime environment for backend development.
- Express.js: A lightweight framework for routing and middleware.
- Nodemon: For automatic server reloading during development.
- Sequelize: ORM for MySQL database management.
- Axios: For HTTP requests and API integration.
- Puppeteer & Cheerio: For web scraping and HTML parsing.
- Multer: To handle file uploads.
- Form-data: Used for working with `multipart/form-data` in HTTP requests.
- Cheerio: For scraping web pages and extracting data.
- Puppeteer: Headless browser used for scraping and interacting with web pages.

### Google Cloud Platform (GCP)
- Cloud Run: To deploy containerized backend APIs.
- Cloud Storage: To store images and other user data.
- Cloud SQL (MySQL): For relational database operations.
- Cloud SQL Connector: To securely connect with MySQL instances.

### Additional Libraries
- jsonwebtoken: For secure authentication with JWT.
- bcrypt: To hash user passwords.
- dotenv: To manage environment variables securely.

# Deployment Process
Below are the steps to deploy the IFISHY application using Google Cloud Platform:

### Step 1: Prepare the Environment
1. Install Dependencies

    Run `npm install` to install all required libraries listed in the `package.json`.

2. Configure Environment Variables

    Create a `.env` file in the root folder with the following structure:
```
    JWT_SECRET= <YOUR_JWT_SECRET>
    DB_NAME=<DATABASE_NAME>
    DB_USER=<DATABASE_USER>
    DB_PASS=<DATABASE_PASSWORD>
    DB_HOST=<DATABASE_HOST>
    DB_PORT=<DATABASE_PORT>
    DB_SOCKET_PATH=/cloudsql/<INSTANCE_CONNECTION_NAME>
    PORT=8080
    GCP_BUCKET_NAME=<YOUR_BUCKET_NAME>
    GCP_CREDENTIALS_PATH=service-account-key.json
```

### Step 2: Create a Docker Image
1. Dockerfile: Use the following configuration:
```
FROM node:18
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 8080
CMD ["npm", "run", "start"]
```
2. Build the Image:

Run `docker build -t ifishy-backend .`

### Step 3: Deploy to Cloud Run
1. Push the Image to Artifact Registry:

    Tag the image and push it:
```
docker tag ifishy-backend REGION-docker.pkg.dev/PROJECT-ID/REPOSITORY/ifishy-backend
docker push REGION-docker.pkg.dev/PROJECT-ID/REPOSITORY/ifishy-backend
```
2. Deploy the Image:
```
gcloud run deploy ifishy-backend --image REGION-docker.pkg.dev/PROJECT-ID/REPOSITORY/ifishy-backend --platform managed --region asia-southeast2 --allow-unauthenticated
```

### Step 4: Set Up the Database
1. Cloud SQL Instance: Create a MySQL instance in the `asia-southeast2` region.
2. Migrate Database: Use Sequelize CLI or other tools to apply migrations.

### Step 5: Test and Monitor
1. Access the application via the Cloud Run URL.
2. Monitor logs and performance using **Google Cloud Logging** and **Monitoring**.

# Environment Variables Explanation

The `.env` file contains sensitive information required for the application to function. Here's a breakdown:

- JWT_SECRET: A secret key used to generate JSON Web Tokens.
- DB_NAME, DB_USER, DB_PASS, DB_HOST, DB_PORT: Database credentials for MySQL.
- DB_SOCKET_PATH: Connection path for Cloud SQL via Unix sockets.
- PORT: Application port for server hosting.
- GCP_BUCKET_NAME: Name of the Cloud Storage bucket for storing images.
- GCP_CREDENTIALS_PATH: Path to the service account key JSON file.
