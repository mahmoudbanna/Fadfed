# FadFed - Social Venting Application

## Overview
FadFed is an Android social networking application designed to allow users to share their thoughts, secrets, and feelings ("FadFada") publicly. It connects users through a feed of shared posts, enabling interactions via comments, likes, and private messaging. The platform focuses on providing a space for expression and community interaction.

## Key Features
*   **User Authentication:** 
    *   Secure Login and Registration system.
    *   Support for profile creation with details like gender, country, and birth date.
*   **Public Feed:** 
    *   Browse a stream of posts from other users.
    *   Interact with posts through comments and reviews.
*   **Posting:** 
    *   Share text-based posts to the community.
*   **Real-time Messaging:** 
    *   Private chat functionality between users.
    *   Inbox management for ongoing conversations.
*   **Push Notifications:** 
    *   Instant alerts for new messages, interactions, and posts using Google Cloud Messaging (GCM).
    *   Background services to handle token refreshes and incoming data.
*   **User Profiles:** 
    *   View user details, history, and specific post reviews.
*   **Social Integration:** 
    *   Facebook SDK integration for social features.
*   **Analytics:** 
    *   Integrated Google Analytics for tracking user behavior and screen views.

## Technical Stack
*   **Platform:** Android (Java)
*   **Minimum SDK:** API 14 (Android 4.0)
*   **Architecture:** MVC (Model-View-Controller) pattern using Activities and Fragments.
*   **Networking:**
    *   Custom Network Handlers using `HttpURLConnection` and Volley.
    *   Backend API: PHP-based RESTful services.
    *   Data Format: JSON.
*   **Third-Party Libraries:**
    *   Google Play Services (GCM, Analytics)
    *   Facebook Android SDK
    *   Volley (Networking)

## Project Structure
The source code is organized into the following packages:
*   `com.optimalsolutions.fadfed` - Main Activities (Home, Login, Registration, AppController).
*   `com.optimalsolutions.fadfed.fragments` - UI Fragments for different tabs (Home, Chats, Notifications, User Profile, PostReview).
*   `com.optimalsolutions.fadfed.network` - Network communication logic, API handlers, and JSON parsers.
*   `com.optimalsolutions.fadfed.GCM` - Services for handling Google Cloud Messaging tokens and downstream messages.
*   `com.optimalsolutions.fadfed.model` - Data models representing users, posts, and messages.
*   `com.optimalsolutions.fadfed.view` - Custom UI components (e.g., ActionBar).
*   `com.optimalsolutions.fadfed.utils` - Utility classes for alerts and common functions.

## Setup & Installation
1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Open in Android Studio:**
    *   Select "Open an existing Android Studio project".
    *   Navigate to the cloned directory.
3.  **Configuration:**
    *   Ensure `local.properties` points to your Android SDK.
    *   Sync Gradle files.
4.  **Build & Run:**
    *   Connect an Android device or start an emulator.
    *   Run the `app` configuration.

## Permissions
The app requires the following permissions to function correctly:
*   `INTERNET`: For network communication.
*   `ACCESS_NETWORK_STATE`: To check connectivity.
*   `READ_PHONE_STATE`: For device identification during registration.
*   `WRITE_EXTERNAL_STORAGE`: For caching or saving data.
*   `WAKE_LOCK` & `VIBRATE`: For handling push notifications effectively.
*   `GET_ACCOUNTS`: For GCM integration.

---
*Developed by Optimal Solutions*
