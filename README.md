# Grid Management Application  

## Overview  
This application is a dynamic and interactive grid management system designed for collaboration and efficiency.
It provides users with an interface to manage data grids, apply filters, set permissions, and work collaboratively.  

## Key Features  
- **Grid Management**:  
  - Create, view, and edit data grids with customizable columns and rows.  
  - Dynamic cell styling with options for text color and background color.  
  - Support for filtering, sorting, and range selection directly in the UI.  

- **Collaboration**:  
  - **Chat Feature**: A real-time chat system for users to communicate and collaborate while working on the same grids.  

- **Permissions System**:  
  - Three permission levels: Owner, Writer, and Reader.  
  - Owners can grant or deny access to other users based on their roles.  

- **Server-Side Functionality**:  
  - Server built on **Apache Tomcat** for robust and reliable backend operations.  
  - Support for multi-user collaboration and session management.  

- **Themes and Animations**:  
  - Switch between dark and light themes.  
  - Toggle animations for a better user experience.  

- **File Upload and Parsing**:  
  - Upload XML files containing grid data, which are parsed and managed as sheets on the server.  
  - Duplicate sheet name detection ensures every sheet has a unique identifier.  

## Technical Details  
- **Frontend**: JavaFX with JFoenix controls for a modern, responsive UI.  
- **Backend**: Engine module for data processing and Tomcat server for client-server communication.  
- **Database**: Manages user accounts, sheet permissions, and chat logs.  
- **Deployment**: WAR file for server deployment and standalone JAR for local testing.  
