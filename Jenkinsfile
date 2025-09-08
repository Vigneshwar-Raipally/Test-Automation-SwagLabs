pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Vigneshwar-Raipally/Test-Automation-SwagLabs.git'
            }
        }

        stage('Run Cucumber Tests') {
            steps {
                // Run Cucumber scenarios (login feature)
                sh 'mvn clean test -Dcucumber.options="--plugin pretty --plugin html:reports/cucumber-reports/cucumber-report.html"'
            }
        }

        stage('Run TestNG Tests') {
            steps {
                // Run TestNG suite (Products, Cart, Checkout)
                sh 'mvn test -DsuiteXmlFile=testng.xml'
            }
        }

        stage('Publish Reports') {
            steps {
                // Cucumber HTML report
                publishHTML(target: [
                    reportDir: 'reports/cucumber-reports',
                    reportFiles: 'cucumber-report.html',
                    reportName: 'Cucumber Report',
                    keepAll: true
                ])

                // Extent HTML report
                publishHTML(target: [
                    reportDir: 'reports/extent-reports',
                    reportFiles: 'index.html',
                    reportName: 'Extent Report',
                    keepAll: true
                ])

                // TestNG results (requires TestNG plugin in Jenkins)
                publishTestNGResults testNGPattern: '**/test-output/testng-results.xml'
            }
        }
    }

    post {
        always {
            // Archive screenshots captured during failures
            archiveArtifacts artifacts: 'reports/screenshots/*', fingerprint: true

            // JUnit fallback (Surefire reports for Jenkins trends)
            junit '**/target/surefire-reports/*.xml'
        }
    }
}
