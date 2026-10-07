pipeline {
    agent any

    options {
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        IMAGE = 'seojunhayo/yeonjulog'
    }

    stages {
        stage('Init') {
            steps {
                script {
                    // git 커밋 해시의 앞 7자리를 추출하여 이미지 태그(TAG)로 사용
                    env.TAG = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
                echo "TAG=${env.TAG}"
            }
        }

        stage('Test') {
            steps {
                // 테스트 실행 (실패하면 이후 단계 실행 안 함)
                sh './gradlew clean test'
            }
            post {
                always {
                    // JUnit 테스트 결과 리포트를 젠킨스 웹 화면에 시각화
                    junit 'build/test-results/test/*.xml'
                }
            }
        }

        // SonarQube 정적 분석 (판정은 다음 Quality Gate 단계)
        stage('SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh './gradlew sonar'
                }
            }
        }

        // 품질 게이트 미달이면 파이프라인 중단
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage('Build Jar') {
            steps { 
                // 테스트는 이미 통과했으므로 -x test로 건너뛰고 순수 jar만 빠르게 생성
                sh './gradlew bootJar -x test'
            }
        }

        stage('Docker Build & Push') {
            steps {
                // Jenkins Credentials(dockerhub)에서 Docker Hub 계정·토큰 주입
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DH_USER', passwordVariable: 'DH_TOKEN')]) {
                    sh '''
                        echo "$DH_TOKEN" | docker login -u "$DH_USER" --password-stdin
                        docker build -t $IMAGE:$TAG .
                        docker push $IMAGE:$TAG
                        docker logout
                    '''
                }
            }
        }
        // CD 스테이지
        stage('Deploy') {
            steps {
                // Jenkins Credentials(app-ssh)의 SSH 키를 임시 파일로 주입
                withCredentials([sshUserPrivateKey(credentialsId: 'app-ssh', keyFileVariable: 'SSH_KEY')]) {
                    sh '''
                        ansible-playbook -i ansible/inventory.ini ansible/k8s-deploy.yml \
                          --private-key "$SSH_KEY" \
                          -e image_tag=$TAG
                    '''
                }
            }
        }
    }
}
