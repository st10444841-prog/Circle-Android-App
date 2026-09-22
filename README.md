# Circle – Android Social Networking Application

## 1. Project Overview

Circle is an Android-based social networking application developed using Kotlin and Android Studio. The application provides users with a simple platform where they can create and manage posts, interact with other users, communicate with friends and manage their personal profiles.

The main purpose of Circle is to demonstrate the development of a functional mobile social networking application while applying Android development principles, user interface design, data persistence, validation, navigation and software development practices.

The application was developed using Kotlin with XML-based Android Views. Android Studio was used as the primary development environment.

---

## 2. Purpose of the Application

The purpose of Circle is to provide users with a mobile social networking environment where they can:

* Register and log into an account.
* Create text-based posts.
* Create posts containing photographs.
* View posts through a Home Feed.
* Like and unlike posts.
* Add comments to posts.
* Edit their own posts.
* Delete their own posts.
* Search for other users.
* Follow and unfollow users.
* View their friends.
* Communicate through individual chat conversations.
* Receive notifications for social interactions.
* Manage application settings.
* Enable or disable dark mode.
* Log out of the application.

The application was designed to demonstrate how common social networking functionality can be implemented within an Android mobile application.

---

## 3. Main Features

### User Authentication

Circle provides registration and login functionality. During registration, users provide their full name, username, email address and password. Input validation is performed to ensure that required information is entered correctly.

The login system checks the entered credentials against the registered user information before allowing access to the Home Feed.

### Home Feed

The Home Feed displays the user's posts. Posts can contain text and photographs. Users can interact with posts using likes and comments.

### Post Management

Users can:

* Create posts.
* Attach photographs to posts.
* Edit posts.
* Delete posts.
* View posts after navigating between screens and restarting the application.

### Social Interaction

The Discover section allows users to search for other users and follow or unfollow them.

The Friends section provides access to conversations with other users through the application's chat functionality.

### Notifications

Circle generates notifications for activities such as:

* Likes.
* Comments.
* Following another user.

Users can view their notifications and clear them when required.

### Profile Management

The Profile section displays the user's:

* Name.
* Username.
* Number of posts.
* Number of friends.
* Number of users being followed.

Users can also access profile editing, settings and logout functionality.

### Settings

The Settings section provides:

* Notification settings.
* Privacy settings.
* Password management.
* Dark mode.
* Application information.

---

## 4. Design Considerations

The Circle application was designed with simplicity, usability and consistency in mind.

### User Interface

The interface uses a clean layout with clear headings, buttons and input fields. Screens were designed to make navigation straightforward for users.

The application uses consistent spacing, typography and card-based layouts to improve readability and organisation.

### Usability

The application provides clear validation messages when users enter incomplete or invalid information. This helps users understand what needs to be corrected.

Navigation buttons are provided for major application areas including:

* Home Feed.
* Discover.
* Create Post.
* Friends.
* Profile.

### Visual Design

The application uses a clean modern visual style with rounded cards, borders, spacing and clear text hierarchy. The design was intended to provide a simple social networking experience without unnecessary interface complexity.

### Accessibility and Readability

Text sizes and colour contrasts were selected to improve readability. Buttons are clearly labelled so that users can understand their purpose.

### Dark Mode

A dark mode option was included to provide an alternative visual appearance and improve user comfort in darker environments.

---

## 5. Data Persistence

Circle uses Android SharedPreferences to store application information locally on the device.

The application uses separate storage classes for different types of information, including:

* User information.
* Posts.
* Likes.
* Comments.
* Notifications.
* Following information.
* Chat messages.
* Profile information.

JSON structures are used in several storage classes to store lists such as posts, comments, notifications and chat messages.

This allows information to remain available after navigating between screens and restarting the application.

---

## 6. Application Structure

The project follows a structured Android application design consisting of Activities, XML layouts and utility/storage classes.

Examples of Activities include:

* LoginActivity
* RegisterActivity
* HomeActivity
* DiscoverActivity
* CreatePostActivity
* FriendsActivity
* ChatActivity
* ProfileActivity
* EditProfileActivity
* SettingsActivity
* NotificationsActivity
* PrivacyActivity
* ChangePasswordActivity
* AboutActivity

Utility and storage classes are used to separate data-handling responsibilities from the user interface.

---

## 7. Technologies Used

| Technology        | Purpose                                |
| ----------------- | -------------------------------------- |
| Kotlin            | Main programming language              |
| Android Studio    | Application development environment    |
| XML               | User interface layouts                 |
| Android SDK       | Mobile application development         |
| SharedPreferences | Local data persistence                 |
| JSON              | Storage of structured collections      |
| Git               | Version control                        |
| GitHub            | Source-code repository                 |
| GitHub Actions    | Automated build/continuous integration |

---

## 8. Testing

The Circle application was tested across the major application functions.

Testing included:

1. Registration and Login.
2. Main Navigation.
3. Text and Photo Posts.
4. Likes and Comments.
5. Edit and Delete Post.
6. Discover and Search.
7. Follow and Unfollow.
8. Friends and Chat.
9. Profile and Account Management.
10. Validation and Error Handling.
11. Notifications.
12. Settings.
13. Dark Mode.
14. Logout.

The application was also tested for data persistence, including posts, photos, comments, likes, follows and chat messages.

---

## 9. GitHub Utilisation

GitHub is used as the version-control and source-code hosting platform for the Circle project.

The complete Android Studio project is uploaded to the GitHub repository so that the source code, XML layouts, resources, Gradle configuration and supporting files can be accessed.

GitHub provides version control for the project and maintains the history of changes made during development.

The repository provides a central location from which the lecturer or assessor can review the complete source code.

---

## 10. GitHub Actions

GitHub Actions is used to automate the Android project build process.

A workflow is stored inside:

`.github/workflows/`

The workflow is configured to run when changes are pushed to the repository.

The workflow checks out the source code, configures the required Java environment and executes the Gradle build command.

This provides an automated check that the Android project can be built successfully after changes are committed.

The use of GitHub Actions demonstrates the application of continuous integration principles because the project can automatically be checked after source-code changes are uploaded.

---

## 11. Project Repository

The complete Circle Android source code is available on GitHub.

**GitHub Repository:**
`https://github.com/st10444841-prog/Circle-Android-App`

---

## 12. APK

A debug APK was generated from the Android Studio project for demonstration and testing purposes.

**APK file:** `app-debug.apk`

The APK is provided separately from the GitHub source-code repository where required by the assessment.

---

## 13. Screenshots and Demonstration

The project documentation includes screenshots demonstrating:

* Login.
* Registration.
* Home Feed.
* Create Post.
* Photo Post.
* Discover and Search.
* Following.
* Friends.
* Chat.
* Profile.
* Settings.
* Notifications.
* Dark Mode.

A functionality demonstration video is also included as part of the final submission.
## 13. Screenshots and Demonstration

The Circle application was tested across its main features, including user registration and login, post creation, photo posts, likes, comments, editing and deleting posts, discovering and following users, friends and chat functionality, profile management, notifications, settings, and dark mode.

### Application Screenshots

The project screenshots are included in the `Screenshots` folder.

### Application Demonstration Video

The complete demonstration of the Circle Android application is available on YouTube:

[Watch the Circle Application Demonstration](https://www.youtube.com/watch?v=sMlbe_hPK3g)
---

## 14. AI Tool Usage

AI tools were used as supplementary development and learning resources during the completion of the Circle Android application. The primary AI tool used was OpenAI ChatGPT.

The AI tool was used to support the development process in several areas. These included explaining Kotlin and Android Studio concepts, suggesting XML layouts, assisting with Kotlin code structures, explaining Android navigation, helping implement input validation and providing guidance on local data persistence using SharedPreferences and JSON.

AI assistance was also used during debugging. Error messages were provided to the AI tool to help identify possible causes and corrective steps. Examples included resolving duplicate Activity declarations, fixing resource and ID references, correcting package and import issues and improving authentication, post, notification and navigation functionality.

AI tools were also used to assist with documentation and project organisation, including preparation of the README documentation, GitHub guidance, testing documentation and explanations of GitHub Actions.

Where AI-generated code or suggestions were used, the code was reviewed and adapted to the requirements of the Circle application. The resulting application was built and tested in Android Studio using an Android emulator. Functionality such as registration, login, posting, photo uploads, likes, comments, editing, deletion, searching, following, chat, notifications, profile management, settings, dark mode and logout was manually tested.

AI-generated suggestions were therefore treated as development assistance rather than automatically accepted solutions. The final implementation, testing and verification of the application were performed by the student.

Examples of AI usage evidence can include relevant prompts, screenshots of AI-assisted debugging, code suggestions and explanations that contributed to the development process.

### AI Tool Reference

OpenAI (2026) *ChatGPT*. Available at: https://chatgpt.com/ (Accessed: 22 September 2026).

---

## 15. Conclusion

Circle demonstrates the development of a functional Android social networking application using Kotlin and Android Studio. The application combines authentication, post management, social interaction, search, following, chat, notifications, profile management and application settings.

The use of GitHub provides source-code management and version control, while GitHub Actions provides automated build verification. Together, these technologies support a structured software development workflow and provide a complete record of the application's source code and development process.
