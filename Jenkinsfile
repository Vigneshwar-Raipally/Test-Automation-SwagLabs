pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git'
            }
        }

        stage('Run TestNG Tests') {
            steps {
                // Run TestNG suite (your testng.xml is at root)
                bat 'mvn clean test -DsuiteXmlFile=testng.xml'
            }
        }

        stage('Publish Reports') {
            steps {
                script {
                    // Publish Extent Report (HTML)
                    publishHTML (target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: 'TestNG HTML Report'
                    ])
                }
            }
        }
    }

    post {
        always {
            // Archive screenshots (if generated)
            archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true

            // Collect TestNG/JUnit results
            junit '**/target/surefire-reports/*.xml'
        }
    }
}

