package CIServer;

import org.junit.Test;

import models.WebhookRequest;

import org.json.JSONObject;

import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class TestCI {
    @Before
    public void setUp() {
        System.out.println("setup");
    }
  
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
    public void testSendingEmail() {
        CIServer server = new CIServer();
        try {
            server.sendEmail("eva.despinoy@gmail.com", "TESTsubject!", "TESTbody!");
        }
        catch (Exception e) {
            System.out.println("Error: " + e);
        }
        // assert that it works by checking the email
    }

    @Test
    public void testCreatingEmailBody() {
        CIServer server = new CIServer();

        String returnedBody = server.createBody(
                "eva.despinoy@gmail.com", "branchname", "commitMessage message", "version v1", true, true);
        String body = "Hello" + " " + "eva.despinoy@gmail.com" + ". " + "Your commit " + "commitMessage message" + " " + "version v1" + " on branch " + "branchname" + " has " + "succeeded. The code has compiled and the tests pass.";
        assertTrue(returnedBody.equals(body));

        returnedBody = server.createBody(
                "eva.despinoy@gmail.com", "branchname", "commitMessage message", "version v1", false, false);
        body = "Hello" + " " + "eva.despinoy@gmail.com" + ". " + "Your commit " + "commitMessage message" + " " + "version v1" + " on branch " + "branchname" + " has " + "failed. The code does not compile.";
        assertTrue(returnedBody.equals(body));

        returnedBody = server.createBody(
                "eva.despinoy@gmail.com", "branchname", "commitMessage message", "version v1", false, true);
        body = "Hello" + " " + "eva.despinoy@gmail.com" + ". " + "Your commit " + "commitMessage message" + " " + "version v1" + " on branch " + "branchname" + " has " + "failed. The code does not compile.";
        assertTrue(returnedBody.equals(body));

        returnedBody = server.createBody(
                "eva.despinoy@gmail.com", "branchname", "commitMessage message", "version v1", true, false);
        body = "Hello" + " " + "eva.despinoy@gmail.com" + ". " + "Your commit " + "commitMessage message" + " " + "version v1" + " on branch " + "branchname" + " has " + "failed. The code compiles but the tests fail.";
        assertTrue(returnedBody.equals(body));
    }
}
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
			webhookRequest = new WebhookRequest(new JSONObject("{\"repository\": {\"svn_url\": \"https://github.com/isacarvid/assignment2\"}, \"commits\":[{\"committer\":{\"email\":\"test\"}}], \"ref\":\"/ref/heads/issue/8\"}"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		CIServer server = new CIServer();
		assertTrue(server.compileRepo(webhookRequest));
	}
}