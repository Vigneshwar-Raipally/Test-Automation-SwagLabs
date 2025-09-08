pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml'
            }
        }

        stage('Publish Cucumber Report') {
            steps {
                publishHTML(target: [
                    reportDir: 'reports/cucumber-reports',
                    reportFiles: 'cucumber-report.html',
                    reportName: 'Cucumber Report',
                    keepAll: true
                ])
            }
        }

        stage('Publish Extent Report') {
            steps {
                publishHTML(target: [
                    reportDir: 'reports/extent-reports',
                    reportFiles: 'index.html',
                    reportName: 'Extent Report',
                    keepAll: true
                ])
            }
        }

        stage('Push Changes to GitHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-cred', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    bat '''
                        git config user.email "jenkins@example.com"
                        git config user.name "Jenkins CI"
                        git add reports/*
                        git commit -m "Automated report update from Jenkins"
                        git push https://%GIT_USER%:%GIT_PASS%@github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git HEAD:main
                    '''
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true
        }
    }
}
