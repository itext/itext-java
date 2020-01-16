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
        timeout(time: 2, unit: 'HOURS')
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
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3', mavenLocalRepo: '.repository') {
                    sh 'mvn --threads 2C clean'
                    sh 'mvn dependency:purge-local-repository -Dinclude=com.itextpdf -DresolutionFuzziness=groupId -DreResolve=false'
                }
            }
        }
        stage('Compile') {
            options {
                timeout(time: 10, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3', mavenLocalRepo: '.repository') {
                    sh 'mvn --threads 2C compile test-compile package -Dmaven.test.skip=true -Dmaven.javadoc.failOnError=false'
                }
            }
        }
        stage('Run Tests') {
            options {
                timeout(time: 1, unit: 'HOURS')
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3', mavenLocalRepo: '.repository') {
                    withSonarQubeEnv('Sonar') {
                        sh 'mvn --activate-profiles test -DgsExec="${gsExec}" -DcompareExec="${compareExec}" -Dmaven.test.skip=false -Dmaven.test.failure.ignore=false -Dmaven.javadoc.skip=true org.jacoco:jacoco-maven-plugin:prepare-agent verify org.jacoco:jacoco-maven-plugin:report -Dsonar.java.spotbugs.reportPaths="target/spotbugs.xml" sonar:sonar ' + sonarBranchName + ' ' + sonarBranchTarget
                    }
                }
            }
        }
        stage('Static Code Analysis') {
            options {
                timeout(time: 30, unit: 'MINUTES')
            }
            steps {
                withMaven(jdk: "${JDK_VERSION}", maven: 'M3', mavenLocalRepo: '.repository') {
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
                }
            }
            steps {
                script {
                    def server = Artifactory.server('itext-artifactory')
                    def rtMaven = Artifactory.newMavenBuild()
                    rtMaven.deployer server: server, releaseRepo: 'releases', snapshotRepo: 'snapshot'
                    rtMaven.tool = 'M3'
                    def buildInfo = rtMaven.run pom: 'pom.xml', goals: 'package --threads 2C --offline -Dmaven.main.skip=true -Dmaven.test.skip=true -Dmaven.javadoc.failOnError=false'
                    server.publishBuildInfo buildInfo
                }
            }
        }
        stage('Branch Artifactory Deploy') {
            options {
                timeout(time: 5, unit: 'MINUTES')
            }
            when {
                not {
                    anyOf {
                        branch "master"
                        branch "develop"
                    }
                }
            }
            steps {
                script {
                    getAndConfigureJFrogCLI()
                    if (env.GIT_URL) {
                        repoName = ("${env.GIT_URL}" =~ /(.*\/)(.*)(\.git)/)[ 0 ][ 2 ]
                        findFiles(glob: 'target/*.jar').each { item ->
                            if (!(item ==~ /.*\/fb-contrib-.*?.jar/) && !(item ==~ /.*\/findsecbugs-plugin-.*?.jar/) && !(item ==~ /.*-sources.jar/) && !(item ==~ /.*-javadoc.jar/)) {
                                sh "./jfrog rt u \"${item.path}\" branch-artifacts/${env.BRANCH_NAME}/${repoName}/java/ --recursive=false --build-name ${env.BRANCH_NAME} --build-number ${env.BUILD_NUMBER} --props \"vcs.revision=${env.GIT_COMMIT};repo.name=${repoName}\""
                            }
                        }
                        findFiles(glob: '**/pom.xml').each { item ->
                            def pomPath = item.path.replace('\\','/')
                            if (!(pomPath ==~ /.*target.*/)) {
                                def resPomName = "main.pom"
                                def subDirMatcher = (pomPath =~ /^.*(?<=\/|^)(.*)\/pom\.xml/)
                                if (subDirMatcher.matches()) {
                                    resPomName = "${subDirMatcher[ 0 ][ 1 ]}.pom"
                                }
                                sh "./jfrog rt u \"${item.path}\" branch-artifacts/${env.BRANCH_NAME}/${repoName}/java/${resPomName} --recursive=false --build-name ${env.BRANCH_NAME} --build-number ${env.BUILD_NUMBER} --props \"vcs.revision=${env.GIT_COMMIT};repo.name=${repoName}\""
                            }
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
