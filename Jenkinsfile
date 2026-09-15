pipeline {

    agent any

    stages {

        stage('Start Selenium') {
            steps {
                sh 'docker compose up -d'
                sh 'docker compose ps'
            }
        }

        stage('Tests') {

            agent {
    docker {
        image 'maven:3.8.3-openjdk-17'
        args "--entrypoint='' --shm-size=2g --network=orangehrm_default"
        reuseNode true
    }
}

            steps {
                sh 'cd demo && mvn clean test'
            }
        }
    }

    // post {
    //     always {
    //         allure([
    //             results: [[path: 'target/allure-results']]
    //         ])

    //         sh 'docker compose down || true'
    //     }
    // }
}