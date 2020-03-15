
import se.diabol.jenkins.pipeline.lib.Constants

import groovy.json.JsonOutput

/* Create a new deployment into GHE
 * 
 * https://developer.github.com/v3/repos/deployments/#create-a-deployment
 * @params Map containing the parameters
 * @return deployment id (or "null" in fail case)
 */
def call(Map options=[:]) {

  // NOTE: Branch must exist in Github
  String branch = env.CHANGE_BRANCH ?: env.BRANCH_NAME
  options.auto_merge = options?.auto_merge ?: false
  options.description = options?.description ?: "Deploy request from jenkins"
  options.environment = options?.environment ?: "production"
  options.payload = options?.payload ?: '{\\\"metadata\\\": \\\"blah\\\"}'
  options.required_contexts = options?.required_contexts ?: []
  //String p2 = JsonOutput.toJson(options.payload)

  String data = """ {
    \\\"ref\\\": \\\"${branch}\\\",
    \\\"auto_merge\\\": ${options.auto_merge},
    \\\"environment\\\": \\\"${options.environment}\\\",
    \\\"payload\\\": ${options.payload},
    \\\"description\\\": \\\"Deploy request from jenkins\\\",
    \\\"required_contexts\\\": []
  }""".stripIndent()

  String repo="tesuvant/deployments_api_test"
  String url = Constants.GITHUB_API_URL + repo + "/deployments"
  String id

  output = sh(returnStdout: true, script:"""
    curl --retry 3 \
      -d \"$data\" \
      -X POST -H \'Authorization: token $TOKEN\' \
      -H \'accept: application/vnd.github.ant-man-preview+json\' \
      ${url}
  """).stripIndent()

  id = sh(returnStdout: true, script:"echo '${output}' | /var/jenkins_home/jq -r .id")
  if (id.equals("null")) {
    echo output
    echo "WARNING: Failed to create deployment (id=null)"
  }
  return id
}
