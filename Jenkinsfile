pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git'
            }
        }

        stage('Run Cucumber Tests') {
            steps {
                // Run cucumber tests (adjust your runner class if needed)
                bat 'mvn clean test -Dcucumber.options="--plugin pretty"'
            }
        }

        stage('Run TestNG Tests') {
            steps {
                // Run your TestNG suite (root-level testng.xml)
                bat 'mvn test -DsuiteXmlFile=testng.xml'
            }
        }

        stage('Publish Reports') {
            steps {
                script {
                    // Publish Cucumber Report
                    publishHTML(target: [
                        reportDir: 'reports/cucumber-reports',
                        reportFiles: 'cucumber-report.html',
                        reportName: 'Cucumber Report',
                        keepAll: true
                    ])

                    // Publish Extent Report
                    publishHTML(target: [
                        reportDir: 'reports/extent-reports',
                        reportFiles: 'index.html',
                        reportName: 'Extent Report',
                        keepAll: true
                    ])
                }
            }
        }
    }

    post {
        always {
            // Archive screenshots
            archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true

            // Capture TestNG/JUnit XML results
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
