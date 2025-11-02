# Jenkins Setup Guide

## 🎯 Purpose
This guide documents the complete Jenkins setup for the PSOFT Library Management System CI/CD pipeline.

## 📋 Prerequisites
- Jenkins installed and running (http://localhost:8080)
- Docker Desktop installed and running
- Git installed
- Java 17 installed
- DockerHub account created

## 🔧 Jenkins Configuration Steps

### 1. Configure Git Tool

**Path**: `Manage Jenkins` → `Tools` → `Git installations`

```
Name: Default
Path to Git executable: C:\Program Files\Git\bin\git.exe
☐ Install automatically
```

### 2. Configure JDK

**Path**: `Manage Jenkins` → `Tools` → `JDK installations`

```
Name: JDK-17
JAVA_HOME: C:\Users\ljsil\.jdks\jbr-17.0.12
☐ Install automatically
```

**Alternative**: Set global environment variable
- `Manage Jenkins` → `System` → `Global properties`
- ☑ Environment variables
- Add: `JAVA_HOME` = `C:\Users\ljsil\.jdks\jbr-17.0.12`

### 3. Configure Maven

**Path**: `Manage Jenkins` → `Tools` → `Maven installations`

```
Name: Maven-3.9
☑ Install automatically
Version: 3.9.x (latest)
```

### 4. Configure DockerHub Credentials

**Path**: `Manage Jenkins` → `Credentials` → `System` → `Global credentials` → `Add Credentials`

```
Kind: Username with password
Scope: Global
Username: 1201198luissilva
Password: <your-docker-hub-token>  # Use your DockerHub Personal Access Token
ID: DOCKERHUB_CREDENTIALS  ⚠️ MUST match exactly!
Description: Docker Hub Personal Access Token
```

### 5. Configure SSH Credentials (Optional - for remote deployment)

**Path**: `Manage Jenkins` → `Credentials` → `Add Credentials`

```
Kind: SSH Username with private key
Scope: Global
ID: STAGING_SSH
Username: ljsil
Private Key: [Enter directly]
  - Paste content from: C:\Users\ljsil\.ssh\jenkins_staging
Description: SSH key for staging deployment
```

### 6. Configure GitHub API Rate Limiting

**Path**: `Manage Jenkins` → `System` → `GitHub API usage`

```
☉ Throttle at/near rate limit
```

This prevents the 4+ minute waits you experienced!

### 7. Configure GitHub Server (Optional)

**Path**: `Manage Jenkins` → `System` → `GitHub` → `Add GitHub Server`

```
Name: GitHub
API URL: https://api.github.com
Credentials: - none - (public repo)
☑ Manage hooks (optional)
```

## 🚀 Create Multibranch Pipeline

### Step 1: Create New Item
1. Click **"New Item"**
2. **Item name**: `psoft-25-26`
3. **Type**: Select **"Multibranch Pipeline"**
4. Click **"OK"**

### Step 2: Configure Branch Sources
1. **Add source** → **Git**
2. **Project Repository**: `https://github.com/1201198luissilva/psoft-project-2025-26-gx.git`
3. **Credentials**: `- none -` (public repo)

### Step 3: Configure Behaviors
- ☑ Discover branches
- ☑ Discover pull requests from origin
- ☐ Discover tags (optional)

### Step 4: Build Configuration
- **Mode**: by Jenkinsfile
- **Script Path**: `Jenkinsfile`

### Step 5: Scan Triggers
- ☑ Periodically if not otherwise run
  - **Interval**: 1 minute (for testing) or 15 minutes (production)

### Step 6: Save
Click **"Save"** - Jenkins will automatically scan repository and detect branches

## ✅ Verification Checklist

### Tools Configuration
- [ ] Git tool configured (`C:\Program Files\Git\bin\git.exe`)
- [ ] JDK-17 configured (`C:\Users\ljsil\.jdks\jbr-17.0.12`)
- [ ] Maven-3.9 configured (auto-install enabled)

### Credentials
- [ ] DOCKERHUB_CREDENTIALS created with correct ID
- [ ] STAGING_SSH created (optional for local deployment)

### Pipeline
- [ ] Multibranch pipeline `psoft-25-26` created
- [ ] Git repository URL correct
- [ ] Jenkinsfile detected in repository
- [ ] Staging branch discovered

### System Configuration
- [ ] GitHub API rate limiting set to "Throttle at/near rate limit"
- [ ] GitHub server configured (optional)

## 🐛 Common Issues and Solutions

### Issue 1: "git.exe" not recognized
**Error**: `Cannot run program "git.exe"`

**Solution**:
1. Go to `Manage Jenkins` → `Tools` → `Git installations`
2. Add Git with path: `C:\Program Files\Git\bin\git.exe`

### Issue 2: JAVA_HOME not found
**Error**: `Error: JAVA_HOME not found in your environment`

**Solution**:
1. Go to `Manage Jenkins` → `Tools` → `JDK installations`
2. Add JDK-17 with path: `C:\Users\ljsil\.jdks\jbr-17.0.12`
3. OR set global environment variable `JAVA_HOME`

### Issue 3: Maven wrapper fails
**Error**: `'powershell' is not recognized` or `ClassNotFoundException: MavenWrapperMain`

**Solution**:
Already fixed in Jenkinsfile - uses Maven tool instead of wrapper

### Issue 4: Docker login fails
**Error**: `unauthorized: incorrect username or password`

**Solution**:
1. Update DOCKERHUB_CREDENTIALS in Jenkins
2. Use Personal Access Token (not password)
3. Test locally first: `docker login -u 1201198luissilva`

### Issue 5: GitHub API rate limit
**Warning**: `Sleeping for 4 min 20 sec`

**Solution**:
1. Go to `Manage Jenkins` → `System` → `GitHub API usage`
2. Change to: `Throttle at/near rate limit`

### Issue 6: Credentials ID mismatch
**Error**: `could not find credentials matching DOCKERHUB_CREDENTIALS`

**Solution**:
Credential ID must match EXACTLY in both places:
- Jenkins credential ID: `DOCKERHUB_CREDENTIALS`
- Jenkinsfile line 22: `DOCKERHUB_CREDENTIALS = 'DOCKERHUB_CREDENTIALS'`

## 📊 Monitoring Your Pipeline

### View Build Status
1. Go to Jenkins Dashboard
2. Click on `psoft-25-26`
3. Click on `staging` branch
4. View build history and console output

### Check Console Output
1. Click on build number (e.g., #9)
2. Click **"Console Output"**
3. Review all stages and errors

### View Test Results
After successful build:
- Click on build number
- Click **"Test Result"**
- View JUnit test report

## 🎯 Expected Pipeline Output

### Successful Build
```
✅ Prepare - Determine environment and image tag
✅ Checkout - Clone repository
✅ Build - Maven package (skip tests)
✅ Test - Run JUnit tests
✅ Archive - Save JAR artifact
✅ Docker Build & Push - Create and push image
✅ Deploy - Start container locally
```

### Verification Steps After Build
1. **Check DockerHub**: https://hub.docker.com/r/1201198luissilva/psoft-25-26-1201198
2. **Check Docker Desktop**: Container `psoft-staging` running
3. **Check Application**: http://localhost:8080/swagger-ui
4. **Check Health**: http://localhost:8080/actuator/health

## 📝 Pipeline Environment Variables

Configured in Jenkinsfile:
```groovy
IMAGE_NAME = '1201198luissilva/psoft-25-26-1201198'
DOCKERHUB_CREDENTIALS = 'DOCKERHUB_CREDENTIALS'
STAGING_SSH = 'STAGING_SSH'
STAGING_HOST = '192.168.3.14'
STAGING_USER = 'ljsil'
```

## 🔄 Typical Build Timeline

1. **GitHub Push**: Push to staging branch
2. **Jenkins Detection**: 1 minute (or immediate with webhook)
3. **Build Duration**: 3-5 minutes
   - Checkout: 10 seconds
   - Build: 1-2 minutes
   - Test: 1 minute
   - Docker Build: 30 seconds
   - Push: 30 seconds
   - Deploy: 30 seconds
4. **Total**: ~4-6 minutes from push to deployed

## 🎓 Best Practices

1. **Always test locally first**: Run `mvn clean test` before pushing
2. **Check Jenkins console**: Review output for warnings
3. **Monitor DockerHub**: Verify images are pushed
4. **Use semantic versioning**: Tag releases properly
5. **Clean up old images**: Remove unused Docker images periodically

## 📚 References

- Jenkins Documentation: https://www.jenkins.io/doc/
- Docker Documentation: https://docs.docker.com/
- Maven Documentation: https://maven.apache.org/
- Spring Boot Docker: https://spring.io/guides/gs/spring-boot-docker/

---

**Setup Completed**: November 2, 2025  
**Jenkins Version**: 2.x  
**Docker Version**: 27.3.1  
**Maven Version**: 3.9.x (auto-installed)
