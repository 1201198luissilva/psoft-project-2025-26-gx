# Deployment Guide - Phase 1

## 🎯 Overview

This document describes the CI/CD pipeline and deployment strategy for the PSOFT Library Management System Phase 1 integration.

## 🏗️ Architecture

### Components
- **Jenkins**: CI/CD automation server
- **Docker**: Container platform
- **DockerHub**: Image registry (`1201198luissilva/psoft-25-26-1201198`)
- **Docker Desktop**: Local deployment environment

### Deployment Flow
```
Git Push → Jenkins Pipeline → Maven Build → Docker Build → DockerHub Push → Local Deploy
```

## 🔧 Jenkins Pipeline

### Pipeline Configuration
- **Type**: Multibranch Pipeline
- **Repository**: https://github.com/1201198luissilva/psoft-project-2025-26-gx.git
- **Branch**: `staging`
- **Jenkinsfile**: Root directory

### Pipeline Stages

1. **Prepare**
   - Determines target environment (dev/staging/prod)
   - Sets image tags based on branch
   - Configures deployment flags

2. **Checkout**
   - Clones repository
   - Fetches latest code from staging branch

3. **Build**
   - Runs `mvn package -DskipTests`
   - Compiles Java application
   - Creates executable JAR

4. **Test**
   - Runs `mvn test`
   - Executes JUnit test suite
   - Publishes test results

5. **Archive**
   - Preserves build artifacts
   - Stores JAR file for deployment

6. **Docker Build & Push**
   - Builds Docker image with tag
   - Logs in to DockerHub
   - Pushes image to registry

7. **Deploy**
   - Generates docker-compose.staging.yml from template
   - Pulls latest image
   - Starts containers locally

## 🐳 Docker Configuration

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
- **Template**: `deploy/docker-compose.staging.yml.template`
- **Generated**: `deploy/docker-compose.staging.yml`
- **Environment**: `deploy/.env.staging`

## 🔑 Credentials

### Jenkins Credentials
| ID | Type | Usage |
|----|------|-------|
| `DOCKERHUB_CREDENTIALS` | Username/Password | Push images to DockerHub |
| `STAGING_SSH` | SSH Key | Reserved for remote deployment |

### Environment Variables
```
NINJAS_API_KEY=<configured>
GOOGLE_BOOKS_API_KEY=<configured>
```

## 📦 Docker Hub

### Repository
- **URL**: https://hub.docker.com/r/1201198luissilva/psoft-25-26-1201198
- **Visibility**: Public
- **Tags**:
  - `staging-<git-sha>`: Staging builds
  - `latest`: Production builds (main branch)
  - `pr-<number>`: Pull request builds

## 🚀 Deployment Instructions

### Manual Deployment

1. **Build locally**
   ```bash
   mvn clean package -DskipTests
   docker build -t 1201198luissilva/psoft-25-26-1201198:local .
   ```

2. **Run with docker-compose**
   ```bash
   cd deploy
   docker-compose -f docker-compose.staging.yml up -d
   ```

3. **Verify deployment**
   ```bash
   docker ps
   curl http://localhost:8080/actuator/health
   ```

### Jenkins Deployment

1. **Trigger build**
   - Push to `staging` branch
   - Or click "Build Now" in Jenkins

2. **Monitor pipeline**
   - Check Jenkins console output
   - Verify all stages pass

3. **Verify deployment**
   - Check DockerHub for new image
   - Check Docker Desktop for running container
   - Access http://localhost:8080/swagger-ui

## 🔍 Verification

### Health Checks
- **Application**: http://localhost:8080/actuator/health
- **Swagger UI**: http://localhost:8080/swagger-ui
- **H2 Console**: http://localhost:8080/h2-console

### Container Status
```bash
docker ps
docker logs <container-id>
```

### Image Verification
```bash
docker images | grep psoft-25-26-1201198
```

## 📊 Monitoring

### Jenkins
- Build history in Jenkins UI
- Test results published after each build
- Artifacts archived

### Docker
- Container logs via `docker logs`
- Health check status via `docker ps`
- Resource usage via Docker Desktop

## 🐛 Troubleshooting

### Common Issues

**Jenkins can't find Git**
- Solution: Configure Git tool in `Manage Jenkins` → `Tools`
- Path: `C:\Program Files\Git\bin\git.exe`

**Jenkins can't find Java**
- Solution: Configure JDK in `Manage Jenkins` → `Tools`
- Path: `C:\Users\ljsil\.jdks\jbr-17.0.12`

**Docker login fails**
- Solution: Update `DOCKERHUB_CREDENTIALS` in Jenkins
- Use Personal Access Token instead of password

**Maven wrapper fails**
- Solution: Use Maven tool instead (already configured)
- Jenkinsfile uses `mvn` command directly

### Logs
- **Jenkins**: Check console output in build
- **Docker**: `docker logs psoft-staging`
- **Application**: Check logs in container

## 📝 Notes

- This setup deploys to **local Docker Desktop** (not remote server)
- SSH credentials (`STAGING_SSH`) are configured but not used for local deployment
- For remote deployment, uncomment SSH sections in Jenkinsfile

## ✅ Phase 1 Completion Checklist

- [x] Jenkinsfile created with all stages
- [x] Dockerfile configured
- [x] docker-compose template created
- [x] Environment variables configured
- [x] Jenkins credentials configured
- [x] Git and Java tools configured
- [x] Maven configured in pipeline
- [x] DockerHub repository accessible
- [ ] Successful build completion
- [ ] Image pushed to DockerHub
- [ ] Container running locally
- [ ] Application accessible via browser

---

**Last Updated**: November 2, 2025  
**Deployment Target**: Docker Desktop (Local)  
**Image Registry**: DockerHub (`1201198luissilva/psoft-25-26-1201198`)
