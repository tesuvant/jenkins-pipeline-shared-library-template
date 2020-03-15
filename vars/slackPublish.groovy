#!/usr/bin/env groovy

import java.text.SimpleDateFormat

/* Send formatted messages to Slack.
   Preconditions:
   - Slack plugin is installed and preconfigured in Jenkins global configuration
   - build-user-vars plugin in installed (https://plugins.jenkins.io/build-user-vars-plugin)
   - scripted pipeline contains "BuildUser" wrapper
*/

def call(org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper currentBuild, String threadId="", String msg="") {

  def String buildColor
    switch(currentBuild?.result) {
        case 'SUCCESS':
            buildColor = 'good'
            break
        case 'UNSTABLE':
            buildColor = 'warning'
            break
        case 'FAILURE':
            buildColor = 'danger'
            break
        case 'ABORTED':
            buildColor = '#D3D3D3'
            break
        default:
            break
    }

    try {
      // Build finished
      if (buildColor?.trim()) {
        String buildDuration = new SimpleDateFormat("HH'h' mm'm' ss's'").format(new Date(currentBuild.duration));
        boolean broadcast = buildColor.equals('danger') ? true : false
        return slackSend(
          replyBroadcast: broadcast,
          channel: threadId,
          color: buildColor,
          message: ("(<${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>) ${currentBuild.result}. "
                         + "<${env.BUILD_URL}console|View output>. Duration: " + buildDuration)
        )
      // Custom message
      } else if (msg?.trim()) {
        return slackSend(channel: threadId, message: msg)
      }
      // Build was started
      else {
        String shortSHA = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
        String gitUrl = sh(returnStdout: true, script: 'git config remote.origin.url').trim() - ".git" + "/commit/" + shortSHA
        return slackSend(message: "(<${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>) started by ${env.BUILD_USER_EMAIL}. <${gitUrl}|${shortSHA}>")
      }
    }
    catch (ex) {
      echo "Unable to publish Slack message!"
      throw ex
    }

}
