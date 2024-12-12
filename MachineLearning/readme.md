# Architecture Machine Learning
<img src="https://github.com/SandyAryadika/Ifishy-C242-PS164/blob/main/MachineLearning/ifishy%20machine%20learning%20architecture.png">

# Architecture Transfer Learning
<img src="https://github.com/SandyAryadika/Ifishy-C242-PS164/blob/main/MachineLearning/ifishy%20transfer%20learning.png">

# [Ifishy API Endpoints Documentation](https://docs.google.com/spreadsheets/d/1AnR8ifyktgZRyspRoGS_CgjIDOoaJQElb6PeBWj_5q8/edit?gid=1400970446#gid=1400970446)
The `/predict` endpoint is designed to handle fish disease detection. Users can upload an image file, and the API processes the image to provide a prediction result, identifying the disease and its confidence level. This endpoint serves as the core of the application’s fish health analysis feature, leveraging machine learning for accurate predictions.

# Technology Stack
IFISHY uses a modern and scalable technology stack for its development and deployment:

### Backend
- FastAPI: A high-performance web framework for building APIs with Python, utilizing standard Python type hints.
- Uvicorn: An ASGI server for Python, providing fast performance for serving FastAPI applications.
- TensorFlow: Open-source machine learning library for building and deploying machine learning models for fish disease detection.
- Pillow: Image processing library to handle image manipulation and format conversion.
- NumPy: A powerful library for numerical computing in Python, used for handling large, multi-dimensional arrays.
- python-multipart: A library to handle file uploads via `multipart/form-data` encoding.

### Google Cloud Platform (GCP)
- Cloud Run: Fully managed compute platform to deploy containerized applications, including backend APIs and prediction models.
- Cloud Storage: Storage service to handle user-uploaded files, such as images and documents.
- Artifact Registry: Service for storing and managing Docker images in Google Cloud.

# Deployment to Cloud Run via Docker and Artifact Registry
Here are the steps to deploy the IFISHY application to Google Cloud Run using Docker and Artifact Registry.

### Step 1: Prepare the Environment
1. Install Dependencies

   Run the following command to install all required libraries from `requirements.txt`:
```
   pip install -r requirements.txt
```

### Step 2: Create a Docker Image
1. Dockerfile

   Create a `Dockerfile` in the root folder with the following configuration:
```
   FROM python:3.12-slim
   WORKDIR /app
   COPY requirements.txt /app/
   RUN pip install --no-cache-dir -r requirements.txt
   COPY . /app/
   EXPOSE 8080
   CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]
```

2. Build the Docker Image

   Use Docker to build the image:
```
   docker build -t ifishy-api .
```

### Step 3: Push the Docker Image to Artifact Registry
1. Tag the Docker Image

   Tag the Docker image for Artifact Registry in Google Cloud:
```
   docker tag ifishy-api REGION-docker.pkg.dev/PROJECT-ID/REPOSITORY/ifishy-api
```

2. Push the Docker Image

   Push the tagged image to Artifact Registry:
```
   docker push REGION-docker.pkg.dev/PROJECT-ID/REPOSITORY/ifishy-api
```

### Step 4: Deploy the Image to Cloud Run
1. Deploy the Container

   Use the following command to deploy the Docker image to Cloud Run:
```
   gcloud run deploy ifishy-api --image REGION-docker.pkg.dev/PROJECT-ID/REPOSITORY/ifishy-api --platform managed --region asia-southeast2 --allow-unauthenticated
```

2. After the deployment is complete, you will receive a Cloud Run URL which can be used to access the live application.

### Step 5: Monitor and Test
1. Monitor Logs
   Use Google Cloud Logging to monitor the logs and troubleshoot any errors.

# Running the IFISHY Application Locally and Using the URL

### Step 1: Clone the Repository
1. Clone the repository from GitHub:
```
git clone https://github.com/SandyAryadika/Ifishy-C242-PS164.git
cd Ifishy-C242-PS164/MachineLearning
```

### Step 2: Install Dependencies
1. Run the following command to install the necessary dependencies listed in requirements.txt:
```
pip install -r requirements.txt
```

### Step 3: Build and Run the Application Locally Using Docker Desktop
1. Build the Docker Image

   Run the following command to build the Docker image:
```
docker build -t ifishy-api .
```

2. Run the Docker Container

   Use Docker Desktop to run the container:
```
docker run -d -p 8080:8080 ifishy-api
```

3. Verify the Application: Access the application in your browser at `http://localhost:8080`.

### Step 4: Test the Local Application Using Postman
1. You can download Postman [here](https://www.postman.com/downloads/).

2. Ensure that the application is running without any errors. You can verify this by checking the logs or confirming that the Docker container is running correctly with the following command:
```
docker ps
```

3. Open Postman and test the API endpoints locally:
   - Example: Test the `/predict` endpoint to check fish disease prediction functionality.
   - Use the local URL: `http://localhost:8080/predict` for making requests.
   - Tip: If the endpoint requires a file upload (like an image for prediction), ensure that you set the request type to `POST` and select the appropriate file in the request body.

4. Check Logs in Docker Desktop:
   - Open Docker Desktop and go to the "Containers/Apps" tab.
   - Find your running container (`ifishy-api`) and click on it.
   - You should see the logs of the running container displayed there. This is useful for troubleshooting any issues while the app is running locally.

### Step 5: Access the Deployed Application on Cloud Run
1. After successfully deploying your application to Google Cloud Run, you will be given a URL for your application.

2. Access the live application via the Cloud Run URL. The URL should look something like:
```
Service URL: https://<your-app-id>-<random-id>.a.run.app
```

### Step 7: Monitor Logs
1. Use **Google Cloud Logging** to monitor the logs and troubleshoot any issues during deployment or runtime.
