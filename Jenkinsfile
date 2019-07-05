#!/usr/bin/env groovy

pipeline {

    agent any

    environment {
        JDK_VERSION = 'jdk-8-oracle'
    }

    options {
        ansiColor('xterm')
        buildDiscarder(logRotator(artifactNumToKeepStr: '1'))
        parallelsAlwaysFailFast()
        retry(1)
        skipStagesAfterUnstable()
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
    }

    triggers {
        cron(env.BRANCH_NAME == 'develop' ? '@midnight' : '')
    }

    tools {
        maven 'M3'
        jdk "${JDK_VERSION}"
    }

    stages {
        stage('Clean workspace') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3') {
                    sh 'mvn clean'
                }
            }
        }
        stage('Compile') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3') {
                    sh 'mvn compile test-compile'
                }
            }
        }
        stage('Run Tests') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            environment {
                SONAR_BRANCH_TARGET= sh (returnStdout: true, script: '[ $BRANCH_NAME = master ] && echo master || echo develop').trim()
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3') {
                    withSonarQubeEnv('Sonar') {
                        sh 'mvn --activate-profiles test test verify org.jacoco:jacoco-maven-plugin:prepare-agent org.jacoco:jacoco-maven-plugin:report sonar:sonar -DgsExec="${gsExec}" -DcompareExec="${compareExec}" -Dmaven.test.skip=false -Dmaven.test.failure.ignore=false -Dmaven.javadoc.skip=true -Dsonar.branch.name="${BRANCH_NAME}" -Dsonar.branch.target="${SONAR_BRANCH_TARGET}"'
                    }
                }
            }
        }
        stage('Static Code Analysis') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3') {
                    sh 'mvn --activate-profiles qa verify -Dpmd.analysisCache=true'
                }
            }
        }
        stage("Quality Gate") {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
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
                    branch "7.0"
                    branch "7.0-master"
                }
            }
            steps {
                script {
                    def server = Artifactory.server('itext-artifactory')
                    def rtMaven = Artifactory.newMavenBuild()
                    rtMaven.deployer server: server, releaseRepo: 'releases', snapshotRepo: 'snapshot'
                    rtMaven.tool = 'M3'
                    def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'install -Dmaven.test.skip=true -Dspotbugs.skip=true -Dmaven.javadoc.failOnError=false'
                    server.publishBuildInfo buildInfo
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
