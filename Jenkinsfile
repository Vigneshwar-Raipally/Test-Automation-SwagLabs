pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git'
            }
        }

        stage('Build & Test') {
            steps {
                // Run TestNG suite
                bat 'mvn clean test -DsuiteXmlFile=testng.xml'
            }
            post {
                always {
                    // Archive screenshots
                    archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true
                    
                    // Publish Extent report
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'reports/extent-reports',
                        reportFiles: 'ExecutionReport.html',
                        reportName: 'Extent Execution Report'
                    ])

                    // Collect TestNG/JUnit results
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
    }
}
