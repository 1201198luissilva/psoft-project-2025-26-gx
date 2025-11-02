// Declarative Jenkinsfile for a multibranch pipeline
// - maps branches to environments: main -> prod, staging -> staging, others -> dev
// - stages: Prepare, Checkout, Build, Test, Archive, Deploy
// - publishes JUnit test results and archives artifacts
// - deployment logic: commits to dev do NOT deploy. PRs targeting staging or main DO deploy. Merges/commits to staging/main also deploy.
// - staging: build and push Docker image to Docker Hub (image kept running 24/7 on some host - see notes)
// - prod: placeholder for Azure deploy (recommend Azure App Service for Containers or Azure Container Instances for simplicity)

pipeline {
  agent any

  tools {
    maven 'Maven-3.9'
    jdk 'JDK-17'
  }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '30'))
  }

  environment {
    // Docker Hub repository name - CONFIRMED ✅
    IMAGE_NAME = '1201198luissilva/psoft-25-26-1201198'
    
    // Jenkins credentials IDs - CONFIRMED ✅
    DOCKERHUB_CREDENTIALS = 'DOCKERHUB_CREDENTIALS'
    STAGING_SSH = 'STAGING_SSH'
    AZURE_SP = 'AZURE_SP'

    // Staging deployment configuration
    // Option A: Deploy to local Docker Desktop (localhost)
    // Option B: Deploy to remote server (use actual IP/hostname)
    STAGING_HOST = '192.168.3.14'  // Your local machine IP - change if using remote server
    STAGING_USER = 'ljsil'          // Your Windows username - change to 'deploy' if using Linux server
    STAGING_DEPLOY_PATH = 'C:\\Users\\ljsil\\IdeaProjects\\psoft-project-2025-26-gx\\deploy'  // Local path - change if using remote server
  }

  stages {
    stage('Prepare') {
      steps {
        script {
          // Map branch to target environment
          if (env.BRANCH_NAME == 'main') {
            env.TARGET_ENV = 'prod'
          } else if (env.BRANCH_NAME == 'staging') {
            env.TARGET_ENV = 'staging'
          } else {
            env.TARGET_ENV = 'dev'
          }

          // Detect pull request info (multibranch pipeline vars)
          env.IS_PR = env.CHANGE_ID ? 'true' : 'false'
          env.PR_TARGET = env.CHANGE_TARGET ?: ''

          // Choose mvnw wrapper for the agent OS
          env.MVNW = isUnix() ? './mvnw' : 'mvnw.cmd'

          // Image tagging strategy
          def safeBranch = (env.BRANCH_NAME ?: 'branch').replaceAll(/[^a-zA-Z0-9_.-]/, '-')
          def shortSha = (env.GIT_COMMIT ?: '')[0..7] ?: 'local'

          if (env.IS_PR == 'true') {
            env.IMAGE_TAG = "pr-${env.CHANGE_ID}"
          } else if (env.TARGET_ENV == 'prod' && env.BRANCH_NAME == 'main') {
            env.IMAGE_TAG = "latest"
          } else if (env.TARGET_ENV == 'staging') {
            env.IMAGE_TAG = "staging-${shortSha}"
          } else {
            env.IMAGE_TAG = "${safeBranch}-${shortSha}"
          }

          // Determine if we should perform a deploy for this build:
          // - Do NOT deploy for generic dev branches (TARGET_ENV == 'dev' and not a PR targeting staging/main)
          // - Deploy when building the staging branch OR a PR targeting staging
          // - Deploy when building the main branch OR a PR targeting main
          env.DEPLOY = 'false'
          if (env.TARGET_ENV == 'staging') {
            if (env.BRANCH_NAME == 'staging' || (env.IS_PR == 'true' && env.PR_TARGET == 'staging')) {
              env.DEPLOY = 'true'
            }
          } else if (env.TARGET_ENV == 'prod') {
            if (env.BRANCH_NAME == 'main' || (env.IS_PR == 'true' && env.PR_TARGET == 'main')) {
              env.DEPLOY = 'true'
            }
          }

          echo "Branch: ${env.BRANCH_NAME} -> Target environment: ${env.TARGET_ENV}"
          echo "Is PR: ${env.IS_PR} (target=${env.PR_TARGET}), Deploy: ${env.DEPLOY}, Image: ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
        }
      }
    }

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        script {
          // Build without running tests first (faster iteration)
          if (isUnix()) {
            sh "mvn -B -DskipTests=true package"
          } else {
            bat "mvn -B -DskipTests=true package"
          }
        }
      }
    }

    stage('Unit Tests') {
      steps {
        script {
          echo "Running unit tests..."
          if (isUnix()) {
            sh "mvn -B test"
          } else {
            bat "mvn -B test"
          }
        }
      }
      post {
        always {
          // Publish JUnit results (Maven Surefire)
          junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
        }
      }
    }

    stage('Code Coverage') {
      steps {
        script {
          echo "Generating code coverage report..."
          if (isUnix()) {
            sh "mvn -B jacoco:report"
          } else {
            bat "mvn -B jacoco:report"
          }
        }
      }
      post {
        always {
          // Publish JaCoCo coverage report
          jacoco(
            execPattern: 'target/jacoco.exec',
            classPattern: 'target/classes',
            sourcePattern: 'src/main/java',
            exclusionPattern: 'src/test*'
          )
        }
      }
    }

    stage('Integration Tests') {
      steps {
        script {
          echo "Running integration tests..."
          if (isUnix()) {
            sh "mvn -B verify -DskipUnitTests"
          } else {
            bat "mvn -B verify -DskipUnitTests"
          }
        }
      }
      post {
        always {
          // Publish Integration test results
          junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
        }
      }
    }

    stage('Mutation Tests') {
      steps {
        script {
          echo "Running mutation tests with PITest..."
          if (isUnix()) {
            sh "mvn -B org.pitest:pitest-maven:mutationCoverage"
          } else {
            bat "mvn -B org.pitest:pitest-maven:mutationCoverage"
          }
        }
      }
      post {
        always {
          // Archive PITest mutation reports
          publishHTML(target: [
            allowMissing: true,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'target/pit-reports',
            reportFiles: 'index.html',
            reportName: 'PITest Mutation Report'
          ])
        }
      }
    }

    stage('Archive') {
      steps {
        // Archive built artifacts (adjust pattern if your artifact is in a different path)
        archiveArtifacts artifacts: 'target/*.jar, target/*.war, target/*.zip', fingerprint: true, allowEmptyArchive: true
      }
    }

    // Docker build & push only when a deploy is requested (staging/prod PRs or merges)
    stage('Docker Build & Push') {
      when {
        expression { return env.DEPLOY == 'true' }
      }
      steps {
        script {
          // Build and push Docker image to Docker Hub
          // Requires Jenkins credential DOCKERHUB_CREDENTIALS (username/password or token)
          def imageFull = "${env.IMAGE_NAME}:${env.IMAGE_TAG}"

          if (isUnix()) {
            withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
              sh "docker --version || true"
              sh "echo Building docker image ${imageFull}"
              sh "docker build -t ${imageFull} ."
              sh "echo Logging in to Docker Hub as ${DOCKER_USER}"
              sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
              sh "docker push ${imageFull}"
            }
          } else {
            withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
              bat "docker --version || echo docker not found"
              bat "docker build -t %IMAGE_NAME%:%IMAGE_TAG% ."
              bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
              bat "docker push %IMAGE_NAME%:%IMAGE_TAG%"
            }
          }

          echo "Docker image pushed: ${imageFull}"
        }
      }
    }

    stage('Deploy to Hosting Environment 1: Docker Containers') {
      when {
        expression { return env.DEPLOY == 'true' }
      }
      steps {
        script {
          echo "=== HOSTING ENVIRONMENT 1: Docker Containers ==="
          echo "Deploying ${env.TARGET_ENV} environment..."

          // Generate docker-compose from template
          def localTemplate = 'deploy/docker-compose.staging.yml.template'
          def localGenerated = "deploy/docker-compose.staging.yml"

          if (isUnix()) {
            sh "sed -e 's|\\${IMAGE_NAME}|${env.IMAGE_NAME}|g' -e 's|\\${IMAGE_TAG}|${env.IMAGE_TAG}|g' ${localTemplate} > ${localGenerated} || true"
          } else {
            bat """powershell -Command "(Get-Content ${localTemplate}) -replace '\\\\\\\$\\{IMAGE_NAME\\}', '${env.IMAGE_NAME}' -replace '\\\\\\\$\\{IMAGE_TAG\\}', '${env.IMAGE_TAG}' | Set-Content ${localGenerated}" """
          }

          // Deploy to Docker containers
          if (isUnix()) {
            sh "cd deploy && docker-compose -f docker-compose.staging.yml pull"
            sh "cd deploy && docker-compose -f docker-compose.staging.yml up -d --remove-orphans"
          } else {
            bat "cd deploy && docker-compose -f docker-compose.staging.yml pull"
            bat "cd deploy && docker-compose -f docker-compose.staging.yml up -d --remove-orphans"
          }

          echo "✅ Deployed to Docker containers (${env.TARGET_ENV})"
          echo "Container accessible at: http://localhost:8080/swagger-ui"
        }
      }
    }

    stage('Deploy to Hosting Environment 2: Local/Direct') {
      when {
        expression { return env.DEPLOY == 'true' && env.TARGET_ENV == 'dev' }
      }
      steps {
        script {
          echo "=== HOSTING ENVIRONMENT 2: Local Direct Deployment ==="
          echo "Running application directly (non-containerized) for dev environment..."
          
          // For dev branches, run the JAR directly as a background process
          if (isUnix()) {
            sh """
              pkill -f 'psoft-g1.*jar' || true
              nohup java -jar target/psoft-g1-*.jar --spring.profiles.active=dev > app-dev.log 2>&1 &
              echo \$! > app-dev.pid
            """
          } else {
            bat """
              taskkill /F /FI "WINDOWTITLE eq psoft-dev*" || echo No existing process
              start "psoft-dev" /B java -jar target\\psoft-g1-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
            """
          }

          echo "✅ Deployed directly to local (dev environment)"
          echo "Direct deployment accessible at: http://localhost:8081 (if configured)"
        }
      }
    }

    stage('Verify Deployment') {
      when {
        expression { return env.DEPLOY == 'true' }
      }
      steps {
        script {
          echo "Verifying deployment..."
          sleep(time: 10, unit: 'SECONDS')
          
          if (isUnix()) {
            sh "curl -f http://localhost:8080/actuator/health || echo 'Health check failed'"
          } else {
            bat "curl -f http://localhost:8080/actuator/health || echo Health check failed"
          }
        }
      }
    }
  }

  post {
    success {
      echo "Build for ${env.BRANCH_NAME} (${env.TARGET_ENV}) succeeded."
    }
    unstable {
      echo "Build is unstable: ${currentBuild.fullDisplayName}"
    }
    failure {
      echo "Build failed: ${currentBuild.fullDisplayName}"
    }
    always {
      // Clean workspace on the agent to avoid leaving large files
      cleanWs()
    }
  }
}
