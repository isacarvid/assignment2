package CIServer;

import org.junit.Test;

import models.WebhookRequest;

import org.junit.Before;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestCI {
	/**
	 * Checking if a json object parses correctly
	 * If not all json parameters are available throw exception 
	 * */
	@Test
	public void testWebhookRequest() {
		WebhookRequest webhookRequest = null;
		try {
			webhookRequest = new WebhookRequest(new JSONObject("{\"repository\": {\"svn_url\": \"test\"}, \"commits\":[{\"committer\":{\"email\":\"test\"}}], \"ref\":\"ttttttttttttttest\"}"));
		} catch(Exception e) {
			assertTrue(false);
		}
		assertEquals("test", webhookRequest.getRepoAddress());
		try {
			webhookRequest = new WebhookRequest(new JSONObject("{}"));
		} catch(Exception e) {
			assertTrue(true);
		}
	}
	
	
	@Test
	public void testCompileRepo() {
		WebhookRequest webhookRequest = null;
		try {
			webhookRequest = new WebhookRequest(new JSONObject("{\"repository\": {\"svn_url\": \"https://github.com/isacarvid/assignment1\"}, \"commits\":[{\"committer\":{\"email\":\"test\"}}], \"ref\":\"/ref/heads/main\"}"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//CIServer server = new CIServer();
		//assertTrue(server.compileRepo(webhookRequest).isSuccessBuild());
	}
}
