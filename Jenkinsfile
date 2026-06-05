pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "kartavyanirwel/devhub-app"
        IMAGE_TAG = "v1.0"
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
                    kubectl apply -f k8s/manifests/Service.yaml
                '''
            }
        }

        stage('Restart Deployment') {
            steps {
                sh '''
                    kubectl rollout restart deployment/devhub-deployment
                    kubectl rollout restart deployment/devhub-mysql
                '''
            }
        }

        stage('Wait For Rollout') {
            steps {
                sh '''
                    kubectl rollout status deployment/devhub-mysql --timeout=300s
                    kubectl rollout status deployment/devhub-deployment --timeout=300s
                '''
            }
        }

        stage('Verify') {
            steps {
                sh '''
                    kubectl get pods
                    kubectl get svc
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
