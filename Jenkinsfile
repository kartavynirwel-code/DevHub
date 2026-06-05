pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "kartavyanirwel/devhub-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${DOCKER_IMAGE}:${IMAGE_TAG} .
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh """
                        echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin
                        docker push ${DOCKER_IMAGE}:${IMAGE_TAG}
                    """
                }
            }
        }

        stage('Update Kubernetes Manifest') {
            steps {
                sh """
                    sed -i 's|image: .*|image: ${DOCKER_IMAGE}:${IMAGE_TAG}|g' k8s/manifests/Deployment.yaml

                    git config user.email "jenkins@devhub.com"
                    git config user.name "Jenkins"

                    git add k8s/manifests/Deployment.yaml

                    git commit -m "Update image to ${IMAGE_TAG}" || true
                """
            }
        }

        stage('Push Manifest Changes') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'github-token',
                        usernameVariable: 'GIT_USER',
                        passwordVariable: 'GIT_TOKEN'
                    )
                ]) {

                    sh """
                        git remote set-url origin https://\$GIT_USER:\$GIT_TOKEN@github.com/kartavynirwel-code/DevHub.git
                        git push origin main
                    """
                }
            }
        }

        stage('Verify') {
            steps {
                echo 'Manifest updated successfully. ArgoCD will deploy automatically.'
            }
        }
    }

    post {
        success {
            echo 'Build Successful 🚀'
        }

        failure {
            echo 'Build Failed ❌'
        }
    }
}
