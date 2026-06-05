pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "kartavyanirwel/devhub-app"
        IMAGE_TAG = "v1.0"
        KUBECONFIG = "${HOME}/.kube/config"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
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
                    sh '''
                    echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                    docker push '"${DOCKER_IMAGE}:${IMAGE_TAG}"'
                    '''
                }
            }
        }

        stage('Deploy MySQL') {
            steps {
                sh '''
                kubectl apply -f k8s/manifests/Secrets.yaml
                kubectl apply -f k8s/manifests/Configmap.yaml
                kubectl apply -f k8s/manifests/mysql-deployment.yaml
                '''
            }
        }

        stage('Deploy Application') {
            steps {
                sh '''
                kubectl apply -f k8s/manifests/Deployment.yaml
                '''
            }
        }

        stage('Restart Deployment') {
            steps {
                sh '''
                kubectl rollout restart deployment/devhub-deployment
                kubectl rollout status deployment/devhub-deployment
                '''
            }
        }

        stage('Verify') {
            steps {
                sh '''
                kubectl get pods
                kubectl get svc
                kubectl get deployment
                '''
            }
        }
    }

    post {
        success {
            echo 'Deployment Successful 🚀'
        }

        failure {
            echo 'Deployment Failed ❌'
        }
    }
}