pipeline {
    agent any

    environment {
        AWS_REGION    = 'us-east-1'
        ECR_REGISTRY  = '416806138376.dkr.ecr.us-east-1.amazonaws.com'
        IMAGE_NAME    = 'eks-demo'
        EKS_CLUSTER   = 'eks-lab'
        BUILD_VERSION = "1.0.${BUILD_NUMBER}"
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Build & Unit Tests') {
            steps { sh 'mvn -B clean verify' }
            post {
                always { junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml' }
            }
        }

        stage('Package JAR') {
            steps { sh 'mvn -B package -DskipTests' }
        }

        stage('Docker Build') {
            steps {
                sh """
                  docker build \
                    -t ${ECR_REGISTRY}/${IMAGE_NAME}:${BUILD_VERSION} \
                    -t ${ECR_REGISTRY}/${IMAGE_NAME}:latest .
                """
            }
        }

        stage('Push to ECR') {
            steps {
                sh """
                  aws ecr get-login-password --region ${AWS_REGION} | \
                    docker login --username AWS --password-stdin ${ECR_REGISTRY}
                  docker push ${ECR_REGISTRY}/${IMAGE_NAME}:${BUILD_VERSION}
                  docker push ${ECR_REGISTRY}/${IMAGE_NAME}:latest
                """
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh """
                  aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER}

                  helm upgrade --install eks-demo ./helm/eks-demo \
                    --set image.repository=${ECR_REGISTRY}/${IMAGE_NAME} \
                    --set image.tag=${BUILD_VERSION} \
                    --wait --timeout 5m
                """
            }
        }

        stage('Smoke Test') {
            steps {
                sh '''
                  echo "Waiting for LoadBalancer hostname..."
                  for i in $(seq 1 30); do
                    LB=$(kubectl get svc eks-demo -o jsonpath='{.status.loadBalancer.ingress[0].hostname}')
                    if [ -n "$LB" ]; then
                      echo "LoadBalancer: $LB"
                      break
                    fi
                    sleep 10
                  done

                  echo "Waiting for LB to respond..."
                  for i in $(seq 1 30); do
                    if curl -fs "http://$LB/health" > /dev/null 2>&1; then
                      echo "Health check OK"
                      echo "Response from /greet?name=Jenkins:"
                      curl "http://$LB/greet?name=Jenkins"
                      exit 0
                    fi
                    sleep 10
                  done
                  echo "Smoke test failed - LoadBalancer did not respond"
                  exit 1
                '''
            }
        }
    }

    post {
        success {
            echo "Build #${BUILD_NUMBER} (v${BUILD_VERSION}) deployed"
            sh "kubectl get svc eks-demo || true"
        }
        failure { echo "Build #${BUILD_NUMBER} failed" }
    }
}
