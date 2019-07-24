#!/usr/bin/env groovy
@Library('pipeline-library')_

def schedule = env.BRANCH_NAME.contains('master') ? '@monthly' : env.BRANCH_NAME == 'develop' ? '@midnight' : ''
def sonarBranchName = env.BRANCH_NAME.contains('master') ? '-Dsonar.branch.name=master' : '-Dsonar.branch.name=' + env.BRANCH_NAME
def sonarBranchTarget = env.BRANCH_NAME.contains('master') ? '' : env.BRANCH_NAME == 'develop' ? '-Dsonar.branch.target=master' : '-Dsonar.branch.target=develop'

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
        cron(schedule)
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
                    sh 'mvn compile test-compile package -Dmaven.test.skip=true -Dmaven.javadoc.failOnError=false'
                }
            }
        }
        stage('Run Tests') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3') {
                    withSonarQubeEnv('Sonar') {
                        sh 'mvn --activate-profiles test -DgsExec="${gsExec}" -DcompareExec="${compareExec}" -Dmaven.test.skip=false -Dmaven.test.failure.ignore=false -Dmaven.javadoc.skip=true org.jacoco:jacoco-maven-plugin:prepare-agent verify org.jacoco:jacoco-maven-plugin:report sonar:sonar ' + sonarBranchName + ' ' + sonarBranchTarget
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
                archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.jar, **/*.pom', excludes: '**/fb-contrib-*.jar, **/findsecbugs-plugin-*.jar'
            }
        }
    }

    post {
        always {
            echo 'One way or another, I have finished \uD83E\uDD16'
        }
        success {
            echo 'I succeeeded! \u263A'
            cleanWs deleteDirs: true
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
        fixed {
            script {
                if (env.BRANCH_NAME.contains('master') || env.BRANCH_NAME.contains('develop')) {
                    slackNotifier("#ci", currentBuild.currentResult, "${env.BRANCH_NAME} - Back to normal")
                }
            }
        }
        regression {
            script {
                if (env.BRANCH_NAME.contains('master') || env.BRANCH_NAME.contains('develop')) {
                    slackNotifier("#ci", currentBuild.currentResult, "${env.BRANCH_NAME} - First failure")
                }
            }
        }
    }

}
