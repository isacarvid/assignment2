package models;

import org.json.JSONObject;

public class WebhookRequest {
	private String emailAddress;
	private String branchName;
	private String repoAddress;

	public WebhookRequest(JSONObject request) throws Exception {
		if (request.has("repository") && request.has("commits") && request.has("ref")) {
			this.repoAddress = request.getJSONObject("repository").getString("svn_url");
			this.branchName = request.getString("ref").substring(11);
			this.emailAddress = request.getJSONArray("commits").getJSONObject(0).getJSONObject("committer")
					.getString("email");
		} else {
			throw new Exception("webhook request is malformed");
		}

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
