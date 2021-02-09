package CIServer;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestCI {
    @Before
    public void setUp() {
        System.out.println("setup");
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
