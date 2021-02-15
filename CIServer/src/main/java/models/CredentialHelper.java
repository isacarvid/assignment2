package models;

import org.json.JSONObject;

/**
 * Handles the server credentials
 * Currently credentials are provided via command line arguments
 * Server email should be authroized to allow App sign-in
 */
public class CredentialHelper {
	private String serverEmail;
	private String serverPassword;

	public String getServerEmail() {
		return serverEmail;
	}

	public void setServerEmail(String serverEmail) {
		this.serverEmail = serverEmail;
	}

	public String getServerPassword() {
		return serverPassword;
	}

	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}

}
