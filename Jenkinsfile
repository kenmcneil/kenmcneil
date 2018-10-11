#!/usr/bin/env groovy
//@formatter:off
def repositoryName = 'product-service'
def gitRepoUrl = "https://github.com/buildcom/${repositoryName}.git"

def commitId = null
String pullRequestId = null

def mavenCommandOptions = "--fail-at-end --batch-mode --quiet --settings settings.xml -Dmaven.repo.local=.localMavenRepo"
def testCompletedSuccessfully = false

// For master branches we want to enforce building sequentially.
if (BRANCH_NAME == 'master')
{
	properties([disableConcurrentBuilds()])
}

ansiColor('gnome-terminal') { timestamps {
node
{
	deleteDir()
	stage('Checkout')
	{
		commitId = checkout([$class: 'GitSCM', branches: scm.branches, extensions: scm.extensions, userRemoteConfigs: scm.userRemoteConfigs]).GIT_COMMIT
		if (commitId != null && !commitId.isEmpty())
		{
			echo "Commit id = ${commitId}"
		}
		else
		{
			echo 'WARNING Unable to retrieve commit id.'
			echo "Commit id = ${commitId}"
		}
		def pullRequestIdResponse = gitPullRequestId(repositoryName, BRANCH_NAME)
		if (pullRequestIdResponse.get("pullRequestId") == null)
		{
			echo 'WARNING Unable to determine pull request id.'
			echo pullRequestIdResponse.get("message")
		}
		else
		{
			echo pullRequestIdResponse.get("message")
			pullRequestId = pullRequestIdResponse.get("pullRequestId")
		}
		pullRequestIdResponse = null
		stage('Stash')
		{
			stash name: "${repositoryName}-${BUILD_NUMBER}", useDefaultExcludes: false
		}
		if(BRANCH_NAME == 'master')
		{
			setJiraLabels(repositoryName)
		}
	}
}
parallel (
	'Build and Test':
	{
		node
		{
			deleteDir()
			unstash "${repositoryName}-${BUILD_NUMBER}"
		}
		testCompletedSuccessfully = true
	},
	'Build and Deploy':
	{
		node
		{
			deleteDir()
			unstash "${repositoryName}-${BUILD_NUMBER}"
		}
	},
	failFast: true
)
}}