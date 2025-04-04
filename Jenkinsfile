pipeline {
    agent any
    
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk'
        PATH = "${JAVA_HOME}/bin:${PATH}"
        DISPLAY = ':0'  // Required for JavaFX GUI tests
        JAVA_OPTS = "-Djava.awt.headless=false -Dprism.order=sw -Dprism.verbose=true -Dprism.subpixeltext=true"
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building JavaFX application...'
                // Choose either Maven or Gradle commands
                sh '''
                    mvn clean package \
                        -DskipTests \
                        -Djavafx.verbose=true \
                        -Dprism.forceGPU=true
                '''
                // OR
                // sh './gradlew clean assemble -x test'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests with JavaFX...'
                sh 'mvn test -Dtestfx.headless=true'
                // OR
                // sh './gradlew test -Dtestfx.headless=true'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
                // Add deployment commands here
                sh 'echo "Deployment logic would go here"'
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed - cleaning up'
            cleanWs()
        }
    }
}

