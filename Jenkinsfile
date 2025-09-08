pipeline {
    agent any

    environment {
        GIT_CREDENTIALS_ID = 'your-jenkins-git-credentials-id'  // Replace with Jenkins credentials ID
        GIT_BRANCH = 'main'
        GIT_REPO = 'https://github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${GIT_BRANCH}", url: "${GIT_REPO}"
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

        stage('Push Reports Back to GitHub') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${GIT_CREDENTIALS_ID}", usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    bat """
                        git config user.email "jenkins@example.com"
                        git config user.name "Jenkins CI"

                        git pull origin ${GIT_BRANCH}

                        git add reports/*
                        git commit -m "Jenkins: Update reports [ci skip]" || echo "No changes to commit"
                        git push https://${GIT_USER}:${GIT_PASS}@github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git ${GIT_BRANCH}
                    """
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
