# Terrakube Development Container

This directory contains configuration for a development container that provides a consistent development environment for working with Terrakube. The devcontainer includes all the necessary tools and dependencies to develop both the Java backend and JavaScript/TypeScript frontend components.

## Features

- Java 21 (Temurin/AdoptOpenJDK)
- Maven 3.9.0
- Node.js 20.x with Yarn
- Docker CLI and Docker Compose
- VS Code extensions for Java, JavaScript/TypeScript, and Docker development
- GitHub CLI

## Getting Started

### Prerequisites

- [Visual Studio Code](https://code.visualstudio.com/)
- [Docker](https://www.docker.com/products/docker-desktop)
- [VS Code Remote - Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

### Opening the Project in a Dev Container

1. Clone the Terrakube repository:
   ```bash
   git clone https://github.com/AzBuilder/terrakube.git
   cd terrakube
   ```

2. Open the project in VS Code:
   ```bash
   code .
   ```

3. When prompted to "Reopen in Container", click "Reopen in Container". Alternatively, you can:
   - Press F1 or Ctrl+Shift+P
   - Type "Remote-Containers: Reopen in Container" and press Enter

4. Wait for the container to build and start. This may take a few minutes the first time.

## Development Workflow

### Java Backend

The Java backend is a Maven project. You can build and run it using the following commands:

```bash
# Build the project
mvn clean install

# Run the API server
cd api
mvn spring-boot:run
```

### JavaScript/TypeScript Frontend

The frontend is a React application with TypeScript. You can start it using:

```bash
# Navigate to the UI directory
cd ui

# Install dependencies
yarn install

# Start the development server
yarn start
```

## Using Docker Compose

The project includes Docker Compose configurations for running the complete application stack. You can use:

```bash
# Navigate to the docker-compose directory
cd docker-compose

# Start the services
docker-compose up -d
```

## Ports

The devcontainer forwards the following ports:
- 8080: Java API server
- 3000: React development server

## Customization

You can customize the devcontainer by modifying:
- `.devcontainer/devcontainer.json`: VS Code settings and extensions
- `.devcontainer/Dockerfile`: Container image configuration