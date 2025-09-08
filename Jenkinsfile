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
                bat 'mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml'
            }
        }

        stage('Archive Cucumber Report') {
            steps {
                // Instead of publishHTML, just archive the report
                archiveArtifacts artifacts: 'reports/cucumber-reports/cucumber-report.html', allowEmptyArchive: true
            }
        }

        stage('Archive Extent Report') {
            steps {
                // Archive Extent report similarly
                archiveArtifacts artifacts: 'reports/extent-reports/index.html', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            // Archive screenshots
            archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true, allowEmptyArchive: true
        }
    }
}
