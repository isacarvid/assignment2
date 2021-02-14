package models;

import org.json.JSONObject;
/**
 * Model class to describe webhook request form Github
 */
public class WebhookRequest {
	private String emailAddress;
	private String branchName;
	private String repoAddress;
	private String repoName;
	private String commitMessage;
	public String getCommitMessage() {
		return commitMessage;
	}

	public String getRepoName() {
		return repoName;
	}

	/**
	 * parses jsonbody and sets class members
	 * @param json-body
	 */
	public WebhookRequest(JSONObject request) throws Exception {
		if (request.has("repository") && request.has("commits") && request.has("ref")) {
			this.repoAddress = request.getJSONObject("repository").getString("svn_url");
			this.branchName = request.getString("ref").substring(11);
			this.emailAddress = request.getJSONArray("commits").getJSONObject(0).getJSONObject("committer")
					.getString("email");
			this.repoName = getRepoName(repoAddress);
			this.commitMessage = request.getJSONArray("commits").getJSONObject(0).getString("message");
		} else {
			throw new Exception("webhook request is malformed");
		}

	}

	private String getRepoName(String repoAddress2) {
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0 ; i < repoAddress2.length(); i++) {
			stringBuilder.append(repoAddress2.charAt(i));
			if(repoAddress2.charAt(i) == '/') {
				stringBuilder.delete(0, stringBuilder.length()-1);
			}
		}
		return stringBuilder.toString();
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getRepoAddress() {
		return repoAddress;
	}

	public void setRepoAddress(String repoAddress) {
		this.repoAddress = repoAddress;
	}

}
