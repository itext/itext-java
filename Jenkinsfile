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
                    sh 'mvn compile test-compile package -Dmaven.test.skip=true -Dmaven.javadoc.failOnError=false'
                }
            }
        }
        stage('Run Tests') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            environment {
                SONAR_BRANCH_NAME = sh(returnStdout: true, script: '[ $BRANCH_NAME = master ] && echo || [ $BRANCH_NAME = develop ] && echo -Dsonar.branch.name=develop || echo -Dsonar.branch.name=$BRANCH_NAME').trim()
                SONAR_BRANCH_TARGET = sh(returnStdout: true, script: '[ $BRANCH_NAME = master ] && echo || [ $BRANCH_NAME = develop ] && echo -Dsonar.branch.target=master || echo -Dsonar.branch.target=develop').trim()
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3') {
                    withSonarQubeEnv('Sonar') {
                        sh 'mvn --activate-profiles test -DgsExec="${gsExec}" -DcompareExec="${compareExec}" -Dmaven.test.skip=false -Dmaven.test.failure.ignore=false -Dmaven.javadoc.skip=true org.jacoco:jacoco-maven-plugin:prepare-agent verify org.jacoco:jacoco-maven-plugin:report sonar:sonar "${SONAR_BRANCH_NAME}" "${SONAR_BRANCH_TARGET}"'
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
