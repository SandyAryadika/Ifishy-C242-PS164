# Architecture Cloud Design
<img src="https://github.com/SandyAryadika/Ifishy-C242-PS164/blob/main/CloudComputing/Ifishy%20Architecture.png" alt="Ifishy architecture">

## [Google Cloud Price Outlook Preview](https://cloud.google.com/products/calculator/estimate-preview/CiQwZjU5NWZkNS00OTY4LTRjYTEtYmYwZi1iYjdkYWZjNjVjNTAQAg==?hl=en)
The Google Cloud Estimated Price Preview outlines the services utilized for this project. Artifact Registry is employed for managing container images, specifically for inter-region egress and storage. Cloud Run is used for deploying both the Backend API and FastAPI, leveraging scalable CPU and memory allocation to handle API requests efficiently.

Cloud SQL (MySQL) serves as the primary database, operating regionally in Jakarta with a micro-instance and zonal standard storage to manage user and application data. Cloud Storage is utilized to store application assets, such as images and related files, in the Jakarta region.

## [Ifishy API Endpoints Documentation](https://docs.google.com/spreadsheets/d/1AnR8ifyktgZRyspRoGS_CgjIDOoaJQElb6PeBWj_5q8/edit?gid=1400970446#gid=1400970446)
This API supports various features for user management, community interaction, article access, and fish disease detection. Endpoints like `/users`, `/dashboard/:email`, and `/profile/:email` provide access to user data, including profiles and dashboards. Users can update their information through `/update` or delete their accounts with `/delete`. Profile photos are managed via `/upload-photo` and `/update-photo-profile`.

Community features include creating, updating, and interacting with posts through endpoints such as `/community/posts` for creating or accessing post lists, and `/community/posts/:postId` for post details or updates. Comments and replies can be added or viewed via `/community/posts/:postId/comments` and `/comments/:commentId/reply`. Users can also `upvote/downvote` posts and check their voting status through relevant endpoints.

Articles can be accessed using `/articles` for a list or `/articles/:id` for details. The bookmark feature allows saving favorite articles or posts through `/bookmark` and `/bookmarks`.

For fish disease detection, the `/scan-history` endpoint allows saving scan results, while `/scan-history/:userId` retrieves the scan history for a specific user ID.

The `/predict` endpoint is designed to handle fish disease detection. Users can upload an image file, and the API processes the image to provide a prediction result, identifying the disease and its confidence level. This endpoint serves as the core of the application’s fish health analysis feature, leveraging machine learning for accurate predictions.
