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
                // 테스트 실행 (실패하면 파이프라인 즉시 중단!)
                sh './gradlew clean test'
            }
            post {
                always {
                    // JUnit 테스트 결과 리포트를 젠킨스 웹 화면에 시각화
                    junit 'build/test-results/test/*.xml'
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
                // 5-3에서 등록한 'dockerhub' 자격증명에서 안전하게 아이디/토큰을 꺼내옴
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
    }
}
