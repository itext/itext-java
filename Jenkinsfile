#!/usr/bin/env groovy

pipeline {

    agent any

    options {
        buildDiscarder(logRotator(daysToKeepStr:'7',  numToKeepStr:'20', artifactNumToKeepStr:'3'))
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
        retry(1)
        timeout(time: 30, unit: 'MINUTES')
    }

    tools {
        maven 'M3'
        jdk '1.8'
    }

    stages {
        stage('Compile') {
            steps {
                sh 'mvn -B compile test-compile'
            }
        }
        stage('Static Code Analysis') {
            parallel {
                stage('Checkstyle') {
                    steps {
                        sh 'mvn -B checkstyle:checkstyle'
                    }
                }
                stage('Findbugs') {
                    steps {
                        sh 'mvn -B findbugs:check'
                    }
                }
                stage('PMD') {
                    steps {
                        sh 'mvn -B pmd:pmd -Dpmd.analysisCache=true'
                    }
                }
            }
        }
        stage('Run Tests') {
            parallel {
                stage('Surefire (Unit Tests)') {
                    steps {
                        sh 'mvn -B surefire:test -DgsExec=$(which gs) -DcompareExec=$(which compare) -Dmaven.test.skip=false -Dmaven.javadoc.failOnError=false'
                    }
                }
                stage('Failsafe (Integration Tests)') {
                    steps {
                        sh 'mvn -B failsafe:integration-test failsafe:verify -DgsExec=$(which gs) -DcompareExec=$(which compare) -Dmaven.test.skip=false -Dmaven.javadoc.failOnError=false'
                    }
                }
            }
        }
        stage('Build') {
            steps {
                sh 'mvn compile package -Dmaven.test.skip=true'
            }
        }
        stage('SonarQube analysis') {
            when {
                branch "develop"
            }
            steps {
                withSonarQubeEnv('Sonar') {
                    sh 'mvn -P test ' +
                            '-DgsExec=$(which gs) -DcompareExec=$(which compare) ' +
                            '-Dmaven.test.failure.ignore=true ' +
                            '-Dmaven.javadoc.skip=true ' +
                            'org.jacoco:jacoco-maven-plugin:prepare-agent ' +
                            '-Dsonar.jacoco.reportPaths=$WORKSPACE/target/jacoco-integration.exec ' +
                            '$SONAR_MAVEN_GOAL ' +
                            '-Dsonar.host.url=$SONAR_HOST_URL ' +
                            '-Dsonar.login=$SONAR_AUTH_TOKEN ' +
                            '-f pom.xml ' +
                            '-Dsonar.projectKey=com.itextpdf:svg:develop ' +
                            '-Dsonar.projectName=svg ' +
                            '-Dsvg.sonar.projectName=svg ' +
                            '-Dsonar.projectVersion=1.0 ' +
                            '-Dsonar.sourceEncoding=UTF-8 ' +
                            '-Dsonar.java.coveragePlugin=jacoco ' +
                            '-Dsonar.language=java ' +
                            '-Dsonar.sources=. ' +
                            '-Dsonar.tests=. ' +
                            '-Dsonar.test.inclusions=**/*Test*/** ' +
                            '-Dsonar.exclusions=**/*Test*/**'
                }
            }
        }
        stage("SonarQube Quality Gate") {
            when {
                branch "develop"
            }
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to quality gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**', onlyIfSuccessful: true
            }
        }
    }

    post {
        always {
            echo 'One way or another, I have finished \uD83E\uDD16'
            deleteDir() /* clean up our workspace */
        }
        success {
            echo 'I succeeeded! \u263A'
        }
        unstable {
            echo 'I am unstable \uD83D\uDE2E'
        }
        failure {
            echo 'I failed \uD83D\uDCA9'
        }
        changed {
            echo 'Things were different before... \uD83E\uDD14'
        }
    }

}
