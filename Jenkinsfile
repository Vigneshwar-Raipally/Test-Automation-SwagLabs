pipeline {
    agent any

    tools {
        maven 'maven-3.9.6'   // Make sure this name matches the Maven install in Jenkins Global Tool Configuration
        jdk 'java-20'         // Make sure this matches the JDK name in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git'
            }
        }
        stage('Build & Test') {
            steps {
                sh 'mvn clean test -DsuiteXmlFile=src/test/resources/testng.xml'
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
    }
    post {
        always {
            archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true
        }
    }
}
