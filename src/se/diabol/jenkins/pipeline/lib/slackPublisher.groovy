package se.diabol.jenkins.pipeline.lib

// https://plugins.jenkins.io/build-user-vars-plugin/


def slackPublish(String buildResult) {
  if ( buildResult == "SUCCESS" ) {
    slackSend color: "good",  message: "(<${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>) SUCCESS. (<${env.BUILD_URL}console|view output>. Duration: " + Long.toString(currentBuild.duration)
  }
  else if( buildResult == "FAILURE" ) { 
    slackSend color: "danger", message: "(<${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>) FAILED. (<${env.BUILD_URL}console|view output>"
  } 
  else if( buildResult == "UNSTABLE" ) { 
    slackSend color: "warning", message: "(<${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>) UNSTABLE. (<${env.BUILD_URL}console|view output>"
  }
  else {
    slackSend color: "#439FE0", message: "(<${env.BUILD_URL}|${env.JOB_NAME} ${env.BUILD_NUMBER}>) started by ${env.BUILD_USER_EMAIL}"
  }
}
