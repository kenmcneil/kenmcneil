#!/usr/bin/env groovy
//@formatter:off
def repositoryName = "product-services"
def gitRepoUrl = "https://github.com/buildcom/${repositoryName}.git"
//server to register tasks to. use dev-dataflow.build.com on branches if debugging is required.
def taskRegistrationUrl = 'https://dataflow.build.com'

def mavenModel = null
def releaseVersion = null

def isRelease = false

//setup default mvn params.
def mavenCommandOptions = "--fail-at-end --batch-mode --quiet"
def testCompletedSuccessfully = false

// For master branches we want to enforce building sequentially to reduce IT stompage and thrashing
if (BRANCH_NAME == 'master')
{
    properties([disableConcurrentBuilds()])
}

//use gnome-termial coloring and provide time stamps
ansiColor('gnome-terminal') { timestamps {
//allocate jenkins agent
    node
            {
                //clear agent workspace in case there is reuse.
                deleteDir()
                stage('Checkout')
                        {
                            //Checkout and store the commit Hash.
                            checkout([$class: 'GitSCM', branches: scm.branches, extensions: scm.extensions, userRemoteConfigs: scm.userRemoteConfigs]).GIT_COMMIT

                            stage('Stash')
                                    {
                                        //stash checked out pristine code for later use. Include everything.
                                        stash name: "${repositoryName}-${BUILD_NUMBER}", useDefaultExcludes: false
                                    }

                            if(BRANCH_NAME == 'master')
                            {
                                //set build version labels on jira based on ID at the beginning of latest commit
                                setJiraLabels(repositoryName)
                            }
                        }
                //clear agent workspace to keep from using up all the agent disk space
                deleteDir()
            }
    parallel (
            'Build and Test':
                    {
                        node
                                {
                                    deleteDir()
                                    //unstash pristine repo
                                    unstash "${repositoryName}-${BUILD_NUMBER}"

                                    //provide maven install on the PATH
                                    withMaven(maven: 'MavenAuto')
                                            {
                                                try
                                                {
                                                    stage('Maven Verify')
                                                            {
                                                                //build and run tests using default presets
                                                                sh "mvn ${mavenCommandOptions} verify"
                                                            }
                                                }
                                                finally
                                                {
                                                    stage('Publish Tests Results')
                                                            {
                                                                //publish test results to Jenkins page using junit plugin
                                                                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                                                            }
                                                }
                                            }
                                    deleteDir()
                                }
                        testCompletedSuccessfully = true
                    },
            'Build and Deploy':
                    {
                        if(BRANCH_NAME == 'master')
                        {
                            node
                                    {
                                        deleteDir()
                                        //unstash pristine Repo
                                        unstash "${repositoryName}-${BUILD_NUMBER}"

                                        //add tag to github denoting the jenkins build number. used by auto transitioner later
                                        gitTag gitRepoUrl, repositoryName

                                        //save pom file's current state for later
                                        mavenModel = readMavenPom file: ''
                                        //get version in the pom file
                                        def startingVersion = mavenModel.getVersion()

                                        stage('Wait for user input')
                                                {
                                                    try
                                                    {
                                                        //Start a 3 minute time out for whether to release or not
                                                        timeout(3)
                                                                {

                                                                    hipchat "${repositoryName} master #${BUILD_NUMBER} is waiting for input. Please go to <a href=\"${BUILD_URL}input\">this link</a>. This link will expire in 5 minutes.", ['2404465'], 'YELLOW'
                                                                    isRelease = input message: "Would you like to make a release of ${startingVersion.replace("-SNAPSHOT", "")}?", parameters: [booleanParam(defaultValue: false, description: '''UNCHECKED - Build and deploy SNAPSHOT.\nCHECKED - Build and deploy RELEASE.''', name: 'RELEASE')]
                                                                }
                                                    }
                                                    catch(ignore)
                                                    {
                                                        //treat neglect as a NO.
                                                        isRelease = false;
                                                    }
                                                }
                                        stage('Wait for tests')
                                                {
                                                    //Start a 5 minute time out for tests to complete successfully.
                                                    timeout(5)
                                                            {
                                                                waitUntil
                                                                        {
                                                                            testCompletedSuccessfully
                                                                        }
                                                            }
                                                }
                                        withMaven(maven: 'MavenAuto', mavenSettingsConfig: 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig1426267345601')
                                                {
                                                    //if release requested during earlier input
                                                    if (isRelease)
                                                    {
                                                        stage('Maven Deploy and Git Release')
                                                                {
                                                                    //Remove SNAPSHOT from the end of version
                                                                    sh 'mvn versions:set -DremoveSnapshot=true -DgenerateBackupPoms=false'

                                                                    //
                                                                    mavenModel = readMavenPom file: ''
                                                                    releaseVersion = mavenModel.getVersion()
                                                                    //deploy release version
                                                                    sh 'mvn deploy -DskipTests'
                                                                    //clean in prep for commits
                                                                    sh 'mvn clean'

                                                                    //Create commit version noting the Release Version
                                                                    sh "git commit --all --message='[RELEASE] Releasing version ${mavenModel.getVersion()} of ${repositoryName}.'"
                                                                    //Create lightweight tag denoting the version of release.
                                                                    sh "git tag --annotate ${mavenModel.getVersion()} --message='[RELEASE] Releasing version ${mavenModel.getVersion()} of ${repositoryName}.'"

                                                                    //increment patch version and restore SNAPSHOT
                                                                    sh 'mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false'
                                                                    //deploy SNAPSHOT version
                                                                    sh 'mvn deploy -DskipTests'
                                                                    //clean in prep for more commits
                                                                    sh 'mvn clean'

                                                                    //commit version bump
                                                                    sh "git commit --all --message='[RELEASE] Preparing for the next version of ${repositoryName}.'"

                                                                    withCredentials([[$class: 'UsernamePasswordBinding', credentialsId: '4be01c7d-4888-411d-a5af-bfaf9270b806', variable: 'GITUSERNAMEPASSWORD']])
                                                                            {
                                                                                //push all commits NOTE: this will cause another build which can be ignored.
                                                                                sh "git push ${gitRepoUrl.replaceFirst('github', GITUSERNAMEPASSWORD + '@github')}"
                                                                            }
                                                                }
                                                        stage('Register Tasks')
                                                                {
                                                                    //get a list of all files in ./build/tasks and store as an array.
                                                                    def projects = sh(returnStdout: true, script: 'ls -1 ./build/tasks/').split('\n')

                                                                    //allocate data flow credentials. For dev, swap out the guid for: 6e4142ed-9c10-4b4b-b6c9-8da630637582
                                                                    withCredentials([usernamePassword(credentialsId: '94e928b1-16b3-4608-8236-9765fe66069a', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')])
                                                                            {
                                                                                //for each project found in the ./build/tasks folder
                                                                                projects.each
                                                                                        {
                                                                                            //"it" is the iterator for groovy's each
                                                                                            echo "Found the following Task Definition: ${it}"
                                                                                            //read in the file's content as a string.
                                                                                            def taskJson = readFile encoding: 'UTF-8', file: "./build/tasks/${it}"
                                                                                            echo "With the following content:\n${taskJson}"

                                                                                            //derive app name
                                                                                            def appName = it.replace('.json','')

                                                                                            //register task version with dataflow
                                                                                            echo "Beginning POST: ${taskRegistrationUrl}/apps/task/${appName}?uri=maven://com.ferguson.cs.product:${appName}:jar:${releaseVersion}&force=true"
                                                                                            def taskResponse = restificationGeneric url: "${taskRegistrationUrl}/apps/task/${appName}?uri=maven://com.ferguson.cs.product:${appName}:jar:${releaseVersion}&force=true",
                                                                                                    method: 'POST',
                                                                                                    password: PASSWORD,
                                                                                                    userName: USERNAME
                                                                                            echo taskResponse.toString()

                                                                                            //register task configuration with dataflow
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
                                                    else
                                                    {
                                                        //no request for release, create and deploy snapshot with latest code
                                                        sh 'mvn deploy -DskipTests'
                                                    }
                                                }

                                        stage("Tracker Transitioning")
                                                {
                                                    //allocate block with credentials to auto transitioner with user name stored to USERNAME variable and password stored to PASSWORD variable
                                                    withCredentials([usernamePassword(credentialsId: 'cf268355-0109-4eef-86d0-aca8885366f7', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')])
                                                            {
                                                                echo "Beginning Tracker Transitioning"
                                                                //http request to auto transitioner asking for transition of all trackers in Code Complete to Deployed status
                                                                def response = restificationGeneric url: "https://release-tools.impdir.com/transition/${repositoryName}/deploy",
                                                                        method: 'POST',
                                                                        password: PASSWORD,
                                                                        userName: USERNAME,
                                                                        readTimeout: 60
                                                                echo response.toString()
                                                            }
                                                }
                                        deleteDir()
                                    }
                        }
                    },
            //trigger aborts in all open threads if exceptions occur in a single thread.
            failFast: true
    )
}}