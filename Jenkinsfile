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
                        /* Change treshold to Default or remove treshold to find more bugs */
                        sh 'mvn -B findbugs:check -Dfindbugs.threshold="High"'
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
