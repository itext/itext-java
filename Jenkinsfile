#!/usr/bin/env groovy

pipeline {

    agent any

    options {
        ansiColor('xterm')
        buildDiscarder(logRotator(artifactNumToKeepStr: '1'))
        compressBuildLog()
        parallelsAlwaysFailFast()
        retry(1)
        skipStagesAfterUnstable()
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
    }

    tools {
        maven 'M3'
        jdk '1.8'
    }

    stages {
        stage('Clean workspace') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: '1.8', maven: 'M3') {
                    sh 'mvn clean'
                }
            }
        }
        stage('Compile') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: '1.8', maven: 'M3') {
                    sh 'mvn compile test-compile'
                }
            }
        }
        stage('Run Tests') {
            stages {
                stage('Surefire (Unit Tests)') {
                    options {
                        timeout(time: 30, unit: 'MINUTES')
                    }
                    steps {
                        withMaven(jdk: '1.8', maven: 'M3') {
                            sh 'mvn --activate-profiles test test -DgsExec="${gsExec}" -DcompareExec="${compareExec}" -Dmaven.test.skip=false'
                        }
                    }
                }
                stage('Failsafe (Integration Tests)') {
                    options {
                        timeout(time: 30, unit: 'MINUTES')
                    }
                    steps {
                        withMaven(jdk: '1.8', maven: 'M3') {
                            sh 'mvn --activate-profiles test test -DgsExec="${gsExec}" -DcompareExec="${compareExec}" -Dmaven.test.skip=false'
                        }
                    }
                }
            }
        }
        stage('Package') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: '1.8', maven: 'M3') {
                    sh 'mvn package -Dmaven.test.skip=true'
                }
            }
        }
        stage('Static Code Analysis') {
            parallel {
                stage('Checkstyle') {
                    options {
                        timeout(time: 5, unit: 'MINUTES')
                    }
                    steps {
                        withMaven(jdk: '1.8', maven: 'M3') {
                            sh 'mvn --activate-profiles qa checkstyle:checkstyle'
                        }
                    }
                }
                stage('Findbugs') {
                    options {
                        timeout(time: 10, unit: 'MINUTES')
                    }
                    steps {
                        /* Change treshold to Default or remove treshold to find more bugs */
                        withMaven(jdk: '1.8', maven: 'M3') {
                            sh 'mvn --activate-profiles qa findbugs:check -Dfindbugs.threshold="High"'
                        }
                    }
                }
                stage('PMD') {
                    options {
                        timeout(time: 5, unit: 'MINUTES')
                    }
                    steps {
                        withMaven(jdk: '1.8', maven: 'M3') {
                            sh 'mvn --activate-profiles qa pmd:pmd -Dpmd.analysisCache=true'
                        }
                    }
                }
            }
        }
        stage('Archive Artifacts') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.jar'
            }
        }
        stage('Artifactory Deploy') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
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
                    def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'package'
                    server.publishBuildInfo buildInfo
                }
            }
        }
    }

    post {
        always {
            echo 'One way or another, I have finished \uD83E\uDD16'
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
