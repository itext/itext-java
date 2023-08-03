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
        stage('Package') {
            steps {
                sh 'mvn package -Dmaven.test.skip=true'
            }
        }
        stage('SonarQube analysis') {
            when {
                branch "develop"
            }
            steps {
                withSonarQubeEnv('Sonar') {
                    sh 'mvn clean install -P test jacoco:prepare-agent ' +
                            '-DgsExec=$(which gs) ' +
                            '-DcompareExec=$(which compare) ' +
                            '-Dmaven.test.failure.ignore=true ' +
                            '$SONAR_MAVEN_GOAL ' +
                            '-Dsonar.host.url=$SONAR_HOST_URL ' +
                            '-Dsonar.login=$SONAR_AUTH_TOKEN ' +
                            '-Dsonar.jacoco.reportPaths=$WORKSPACE/target/jacoco-integration.exec ' +
                            '-Dmaven.javadoc.skip=true ' +
                            '-Dsonar.projectName=svg ' +
                            '-Dsonar.projectVersion=1.0 ' +
                            '-Dsonar.projectKey=svg_' + env.BRANCH_NAME + ' ' +
                            '-Dsonar.sources=. ' +
                            '-Dsonar.sourceEncoding=UTF-8 ' +
                            '-Dsonar.language=java ' +
                            '-Dsonar.java.coveragePlugin=jacoco ' +
                            '-Dsonar.exclusions=/src/test/** ' +
                            '-Dsonar.verbose=true '
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
        stage('Artifactory Deploy') {
            when {
                anyOf {
                    branch "master"
                    branch "develop"
                }
            }
            steps {
                script {
                    def server = Artifactory.server('itext-artifactory')
                    def rtMaven = Artifactory.newMavenBuild()
                    rtMaven.deployer server: server, releaseRepo: 'releases', snapshotRepo: 'snapshot'
                    rtMaven.tool = 'M3'
                    def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'install'
                    server.publishBuildInfo buildInfo
                }
            }
        }
    }

    post {
        always {
            echo 'One way or another, I have finished \uD83E\uDD16'
            deleteDir() /* clean up our workspace */
            echo sh(script: 'env|sort', returnStdout: true)
        }
        success {
            echo 'I succeeeded! \u263A'
            slackSend (color: 'good', message: "Build Stable: <${env.BUILD_URL}|Job: ${env.JOB_NAME} - Branch: ${env.BRANCH_NAME} [${env.BUILD_NUMBER}] - Commit: ${env.GIT_COMMIT}> \u263A", channel: '#ci')
        }
        unstable {
            echo 'I am unstable \uD83D\uDE2E'
            slackSend (color: 'warning', message: "Build Unstable: <${env.BUILD_URL}|Job: ${env.JOB_NAME} - Branch: ${env.BRANCH_NAME} [${env.BUILD_NUMBER}] - Commit: ${env.GIT_COMMIT}> \uD83D\uDE2E", channel: '#ci')
        }
        failure {
            echo 'I failed \uD83D\uDD25'
            slackSend (color: 'danger', message: "Build failed: <${env.BUILD_URL}|Job: ${env.JOB_NAME} - Branch: ${env.BRANCH_NAME} [${env.BUILD_NUMBER}] - Commit: ${env.GIT_COMMIT}> \uD83D\uDD25", channel: '#ci')
        }
        changed {
            echo 'Things were different before... \uD83E\uDD14'
        }
    }

}
