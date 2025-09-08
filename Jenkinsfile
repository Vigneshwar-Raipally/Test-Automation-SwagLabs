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
                // Run TestNG suite
                bat 'mvn clean test -DsuiteXmlFile=testng.xml'
            }
        }

        stage('Publish Extent Report') {
            steps {
                script {
                    // Publish Extent HTML Report
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'reports/extent-reports',
                        reportFiles: 'ExecutionReport.html',
                        reportName: 'Extent Execution Report'
                    ])
                }
            }
        }
    }

    post {
        always {
            // Archive screenshots
            archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true

            // Collect TestNG/JUnit results
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
