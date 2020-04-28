pipeline {
  agent none
  options {
    disableConcurrentBuilds()
    parallelsAlwaysFailFast()
  }
  environment {
    repositoryName = 'product-services'
    gitRepoUrl = "https://github.com/buildcom/${repositoryName}.git"
    isRpmReleaseEnabled = true
    isDataFlowPostEnabled = true
    taskRegistrationUrl = 'https://dataflow.build.com'
    dataFlowCredGuid = '94e928b1-16b3-4608-8236-9765fe66069a'
    jarGroupId = 'com.ferguson.cs.product'
    mavenModel = null
    startingVersion = null
    releaseVersion = null
    mavenSettingsConfig = 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig1426267345601'
    mavenCommandOptions = "--fail-at-end --batch-mode --quiet --settings settings.xml -Dmaven.repo.local=.localMavenRepo -Dlogback.configurationFile=logback-test-build.xml"
    testCompletedSuccessfully = false
    skipCI = false
  }
  stages {
    stage ('Parallel Stages') {
      parallel {
        stage ('Build and Test') {
          agent {
            label 'CentOS7'
          }
          tools {
            git 'Latest'
            maven 'MavenAuto'
          }
          stages {
            stage ('Maven Verify & Publish Results') {
              steps {
                withMaven(maven: 'MavenAuto') {
                  // Build and run tests using default presets.
                  sh "mvn ${mavenCommandOptions} verify"
                }
              }
            }
            stage ('Tests Completed') {
              steps {
                script {
                  testCompletedSuccessfully = true
                }
              }
            }
          }
          post {
            always {
              junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            }
            cleanup {
              cleanWs()
            }
          }
        }
        stage ('Build and Deploy') {
          agent {
            label 'CentOS7'
          }
          tools {
            git 'Latest'
            maven 'MavenAuto'
          }
          when {
            branch 'master'
          }
          environment {
            mavenModel = readMavenPom file: ''
            startingVersion = mavenModel.getVersion()
          }
          stages {
            stage ('See If Commit Is Release') {
              steps {
                script {
                  // check commit message for [RELEASE] to avoid duplicate Jenkins runs.
                  result = sh (script: "git log -1 | grep '\\[RELEASE\\]'", returnStatus: true)
                  if (result == 0) {
                    skipCI = true
                  }
                }
              }
            }
            stage ('Continue With Build & Deploy') {
              //Will only run if skipCI is false, otherwise a RELEASE just went out and we don't need to build
              when {
                expression { !skipCI.toBoolean() }
              }
              stages {
                stage ('Jira Labels and Git Tag') {
                  steps {
                    setJiraLabels(repositoryName)
                    gitTag gitRepoUrl, repositoryName
                  }
                }
                stage ('Wait For Tests') {
                  options {
                    timeout(time: 10, unit: 'MINUTES')
                  }
                  steps {
                    // TODO add "quiet: true" when we have workflow-basic-steps-plugin with release v2.20
                    waitUntil (initialRecurrencePeriod: 10000) {
                      script {
                        testCompletedSuccessfully.toBoolean()
                      }
                    }
                  }
                }
                stage ('Release Deploy') {
                  stages {
                    stage ('Maven Deploy & Git Release') {
                      steps {
                        withMaven(maven: 'MavenAuto', mavenSettingsConfig: "${mavenSettingsConfig}") {
                          // Remove SNAPSHOT from the end of version.
                          sh 'mvn versions:set -DremoveSnapshot=true -DgenerateBackupPoms=false'
                          script {
                            // Save pom file's current state for later.
                            mavenModel = readMavenPom file: ''
                            // Save pom file's release state for later.
                            releaseVersion = mavenModel.getVersion()
                          }
                          // Deploy release version to artifactory.
                          // https://maven.apache.org/plugins/maven-deploy-plugin/deploy-mojo.html
                          sh 'mvn deploy -DskipTests'
                          // Clean in prep for commits.
                          sh 'mvn clean'
                          withCredentials([[$class: 'UsernamePasswordBinding', credentialsId: '4be01c7d-4888-411d-a5af-bfaf9270b806', variable: 'GITUSERNAMEPASSWORD']]) {
                            // Create commit version noting the Release Version.
                            sh "git commit --all --message='[RELEASE] Releasing version ${releaseVersion} of ${repositoryName}.'"
                            // Push all commits. This will trigger the RELEASE build.
                            sh "git push ${gitRepoUrl.replaceFirst('github', GITUSERNAMEPASSWORD + '@github')}"
                            // Create lightweight tag denoting the version of release.
                            sh "git tag --annotate ${mavenModel.getVersion()} --message='[RELEASE] Releasing version ${releaseVersion} of ${repositoryName}.'"
                            sh "git push ${gitRepoUrl.replaceFirst('github', GITUSERNAMEPASSWORD + '@github')} ${releaseVersion}"
                          }
                          // Set the version back to the original SNAPSHOT.
                          sh "mvn versions:set -DnewVersion=\"${startingVersion}\" -DgenerateBackupPoms=false"
                          // Increment SNAPSHOT version.
                          sh 'mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false'
                          // Deploy SNAPSHOT version.
                          sh 'mvn deploy -DskipTests'
                          // Clean in prep for more commits.
                          sh 'mvn clean'
                          withCredentials([[$class: 'UsernamePasswordBinding', credentialsId: '4be01c7d-4888-411d-a5af-bfaf9270b806', variable: 'GITUSERNAMEPASSWORD']]) {
                            // Commit version bump.
                            sh "git commit --all --message='[RELEASE] Preparing for the next version of ${repositoryName}.'"
                            // Push all commits. This will trigger the incremented SNAPSHOT build.
                            // NOTE: This will cause another build which can be ignored.
                            sh "git push ${gitRepoUrl.replaceFirst('github', GITUSERNAMEPASSWORD + '@github')}"
                          }
                        }
                      }
                    }
                    stage ('RPM Build and Deploy') {
                      when {
                        expression { isRpmReleaseEnabled.toBoolean() }
                      }
                      environment {
                        workspace = pwd()
                        rpmAppName = 'participation-engine'
                      }
                      steps {
                        sh 'mvn package'
                        withEnv(["HOME=${workspace}"]) {
                          sh "rpmdev-setuptree"
                          sh "mkdir rpmbuild/BUILDROOT"
                          sh "sed -e \"s/0.0.0/${BUILD_NUMBER}/g\" -e \"s/myapp/${rpmAppName}/g\" rpm.spec > ${rpmAppName}.spec"
                          sh "find . -name ${rpmAppName}.jar -exec mv -f {} rpmbuild/SOURCES/ \\;"
                          sh "rpmbuild -bb ${rpmAppName}.spec"
                        }
                        uploadToArtifactory "rpmbuild/RPMS/noarch/*.rpm", 'buildcom-yum'
                      }
                    }
                    stage ('Register Tasks') {
                      when {
                        expression { isDataFlowPostEnabled.toBoolean() }
                      }
                      steps {
                        script {
                          def projects = sh(returnStdout: true, script: 'ls -1 ./build/tasks/').split('\n')

                          // Allocate DataFlow credentials.
                          withCredentials([usernamePassword(credentialsId: "${dataFlowCredGuid}", passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                            // For each project found in the ./build/tasks folder.
                            projects.each {
                              // "it" is the iterator for groovy's each.
                              echo "Found the following Task Definition: ${it}"
                              // Read in the file's content as a string.
                              def taskJson = readFile encoding: 'UTF-8', file: "./build/tasks/${it}"
                              echo "With the following content:\n${taskJson}"
                              // Derive app name.
                              def appName = it.replace('.json','')
                              // Register task version with DataFlow.
                              echo "Beginning POST: ${taskRegistrationUrl}/apps/task/${appName}?uri=maven://${jarGroupId}:${appName}:jar:${releaseVersion}&force=true"
                              def taskResponse = restificationGeneric url: "${taskRegistrationUrl}/apps/task/${appName}?uri=maven://${jarGroupId}:${appName}:jar:${releaseVersion}&force=true",
                                method: 'POST',
                                password: PASSWORD,
                                userName: USERNAME
                              echo taskResponse.toString()
                              // Register task configuration with DataFlow.
                              def registerResponse = restificationGeneric url: "${taskRegistrationUrl}/v1/task-engine/register",
                                method: 'POST',
                                requestContentType: 'application/json',
                                requestBody: taskJson,
                                password: PASSWORD,
                                userName: USERNAME
                              echo registerResponse.toString()
                            }
                          }
                        }
                      }
                    }
                  }
                }
                stage ('Tracker Transitioning') {
                  steps {
                    // Allocate block with credentials to auto-transition with user name stored to USERNAME variable and password stored to PASSWORD variable.
                    withCredentials([usernamePassword(credentialsId: 'cf268355-0109-4eef-86d0-aca8885366f7', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                      echo "Beginning Tracker Transitioning"
                      script {
                        // Http request to auto-transition asking for transition of all trackers in Code Complete to Deployed status.
                        def response = restificationGeneric url: "https://release-tools.impdir.com/transition/${repositoryName}/deploy",
                          method: 'POST',
                          password: PASSWORD,
                          userName: USERNAME,
                          readTimeout: 60
                        echo response.toString()
                      }
                    }
                  }
                }
              }
            }
          }
          post {
            cleanup {
              cleanWs()
            }
          }
        }
      }
    }
  }
}
