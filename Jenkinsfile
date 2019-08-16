#!/usr/bin/env groovy

/* Github info:
*/
def repositoryName = 'product-services'
def gitRepoUrl = "https://github.com/buildcom/${repositoryName}.git"

/* Slack build notification:
 * - Optional slack channel notification for builds
 * - Edit the channel name to the team channel.
 */
def isSlackNotificationEnabled = false;
def slackChannelName = 'cs-release-coord'

/* RPM stage:
 * - The rpm stage releases artifacts to production through harness.
 * - IMPORTANT: An RPM spec file is necessary for this to work before being enabled!
 * - Contact SRE to add an RPM spec file to the repo.
 */
def isRpmReleaseEnabled = false;

/* DataFlow:
 * - The register tasks stage is only necessary if the repo has DataFlow tasks.
 * - The folder ./build/tasks/ must exist in the repo.
 * - The json format may be found in the DataFlow wiki.
 * - Swap Development and Production variables when applicable.
 * - Ensure the jar groupId is correct.
 * - It is important for each task in the repo to be in the same base package space.
 */
def isDataFlowPostEnabled = true;

// Server to register tasks to.
// def taskRegistrationUrl = 'https://dev-dataflow.build.com' // Development.
def taskRegistrationUrl = 'https://dataflow.build.com' // Production.

// Server guid for auth credentials.
// def dataFlowCredGuid = '6e4142ed-9c10-4b4b-b6c9-8da630637582' // Development.
def dataFlowCredGuid = '94e928b1-16b3-4608-8236-9765fe66069a' // Production.

// All tasks within this repo must be in the same groupId.
def jarGroupId = 'com.ferguson.cs.product'

/* Release variables:
*/
// Set based on user input.
def isRelease = false
// Grab version from pom file.
def mavenModel = null
// Current release version.
def releaseVersion = null
// Used to append SNAPSHOT back to the original build version to increment to the next value.
def startingVersion = null

/* Default maven params:
*/
// Assigns credentials, so that maven may do posts.
def mavenSettingsConfig = 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig1426267345601'
def mavenCommandOptions = "--fail-at-end --batch-mode --quiet --settings settings.xml -Dmaven.repo.local=.localMavenRepo"
def testCompletedSuccessfully = false

// For master branches we want to enforce building sequentially to reduce IT stomping and thrashing.
if (BRANCH_NAME == 'master') {
	properties([disableConcurrentBuilds()])
}

// Allocate jenkins agent.
node {
    // Clear agent workspace in case there is reuse.
    deleteDir()

    stage('Checkout') {
        // Checkout and store the commit Hash.
        checkout([$class: 'GitSCM', branches: scm.branches, extensions: scm.extensions, userRemoteConfigs: scm.userRemoteConfigs]).GIT_COMMIT

        stage('Stash') {
            // Stash checked out pristine code for later use. Include everything.
            stash name: "${repositoryName}-${BUILD_NUMBER}", useDefaultExcludes: false
        }

        if (BRANCH_NAME == 'master') {
            // Set build version labels on jira based on ID at the beginning of latest commit.
            setJiraLabels(repositoryName)
        }
    }

    // Clear agent workspace to keep from using up all the agent disk space.
    deleteDir()
}

parallel (
    'Build and Test': {
        node {
            // Clear agent workspace in case there is reuse.
            deleteDir()

            // Un-stash pristine repo.
            unstash "${repositoryName}-${BUILD_NUMBER}"

            // Provide maven install on the PATH.
            withMaven(maven: 'MavenAuto') {
                try {
                    stage('Maven Verify') {
                        // Build and run tests using default presets.
                        sh "mvn ${mavenCommandOptions} verify"
                    }
                } finally {
                    stage('Publish Tests Results') {
                        // Publish test results to Jenkins page using junit plugin.
                        junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    }
                }
            }

            deleteDir()
        }

        testCompletedSuccessfully = true
    },

    'Build and Deploy': {
        if (BRANCH_NAME == 'master') {
            node {
                // Clear agent workspace in case there is reuse.
                deleteDir()

                // Un-stash pristine Repo.
                unstash "${repositoryName}-${BUILD_NUMBER}"

                // Add tag to github denoting the jenkins build number. Used by auto-transition later.
                gitTag gitRepoUrl, repositoryName

                // Save pom file's current state for later.
                mavenModel = readMavenPom file: ''

                // Get version in the pom file.
                startingVersion = mavenModel.getVersion()

                stage('Wait for user input') {
                    try {
                        // Start a 3 minute time out for whether to release or not.
                        timeout(3) {
                            if (isSlackNotificationEnabled) {
                                slackSend(channel: "${slackChannelName}", color: '#ffff00',
                                    message: "${repositoryName} master #${BUILD_NUMBER} is waiting for input. Please go to ${BUILD_URL}input. This link will expire in 3 minutes.")
                            }

                            isRelease = input message: "Would you like to make a release of ${startingVersion.replace("-SNAPSHOT", "")}?",
                                parameters: [
                                    booleanParam(defaultValue: false, description: '''UNCHECKED - Build and deploy SNAPSHOT.\nCHECKED - Build and deploy RELEASE.''',
                                    name: 'RELEASE')
                                ]
                        }
                    } catch (ignore) {
                        // Treat neglect as a NO.
                        isRelease = false;
                    }
                }

                stage('Wait for tests') {
                    // Start a 5 minute time out for tests to complete successfully.
                    timeout(5) {
                        waitUntil {
                            testCompletedSuccessfully
                        }
                    }
                }

                // Provide maven install on the PATH.
                withMaven(maven: 'MavenAuto', mavenSettingsConfig: "${mavenSettingsConfig}") {
                    // If release requested during earlier input.
                    if (isRelease) {
                        stage('Maven Deploy and Git Release') {

                            // Remove SNAPSHOT from the end of version.
                            sh 'mvn versions:set -DremoveSnapshot=true -DgenerateBackupPoms=false'

                            // Save pom file's current state for later.
                            mavenModel = readMavenPom file: ''

                            // Save pom file's release state for later.
                            releaseVersion = mavenModel.getVersion()

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

                        stage('RPM Build and Deploy') {
                            if (isRpmReleaseEnabled) {

                                // Clear agent workspace in case there is reuse.
                                deleteDir()

                                // Generate a uniqueId for the build.
                                unstash "${repositoryName}-${BUILD_NUMBER}-RPM"

                                def workspace = pwd()

                                withEnv(["HOME=${workspace}"]) {
                                    sh 'rpmdev-setuptree'
                                    sh 'mkdir rpmbuild/BUILDROOT'
                                    sh 'find . \\( -type d -name "rpmbuild" -prune \\) -or \\( -name "*.jar" -or -name "*.conf" \\) -and -type f -exec mv -f {} rpmbuild/SOURCES/ \\;'
                                    sh "ls rpm-specs/ | parallel sed -i \"s/1\\.0\\.0/${releaseVersion}/\" rpm-specs/{}"
                                    sh 'ls rpm-specs/ | parallel --jobs 0 rpmbuild -bb rpm-specs/{}'
                                }

                                // Upload to artifactory.
                                Map<String, String> uploadResult = uploadToArtifactory "rpmbuild/RPMS/noarch/*.rpm", 'buildcom-yum'

                                // Check if upload was successful.
                                if (uploadResult == null || uploadResult.isEmpty()) {
                                    error "Failed to upload RPMs."
                                }
                            } else {
                                echo "isRpmReleaseEnabled is ${isRpmReleaseEnabled}."
                            }
                        }

                        stage('Register Tasks') {
                            if (isDataFlowPostEnabled) {
                                // Get a list of all files in ./build/tasks and store as an array.
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
                            } else {
                                echo "isDataFlowPostEnabled is ${isDataFlowPostEnabled}."
                            }
                        }
                    } else {
                        // No request for release, create and deploy snapshot with latest code.
                        sh 'mvn deploy -DskipTests'
                    }
                }

                stage("Tracker Transitioning") {
                    // Allocate block with credentials to auto-transition with user name stored to USERNAME variable and password stored to PASSWORD variable.
                    withCredentials([usernamePassword(credentialsId: 'cf268355-0109-4eef-86d0-aca8885366f7', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                        echo "Beginning Tracker Transitioning"
                        // Http request to auto-transition asking for transition of all trackers in Code Complete to Deployed status.
                        def response = restificationGeneric url: "https://release-tools.impdir.com/transition/${repositoryName}/deploy",
                                method: 'POST',
                                password: PASSWORD,
                                userName: USERNAME,
                                readTimeout: 60
                        echo response.toString()
                    }
                }

                // End of build, clear agent workspace.
                deleteDir()
            }
        }
    },

    // Trigger aborts in all open threads if exceptions occur in a single thread.
    failFast: true
)
