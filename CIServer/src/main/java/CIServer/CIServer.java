package CIServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.lang.Object;

import java.util.Properties;
import java.util.Scanner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import models.BuildStatus;
import models.CredentialHelper;
import models.WebhookRequest;

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook See the Jetty
 * documentation for API documentation of those classes.
 */

public class CIServer extends AbstractHandler {
	private ProcessBuilder processBuilder = new ProcessBuilder();
	private CredentialHelper credentialHelper = new CredentialHelper();

	public void startServer(int port, String serverEmail, String serverPassword) throws Exception {
		credentialHelper.setServerEmail(serverEmail);
		credentialHelper.setServerPassword(serverPassword);
		Server server = new Server(port);
		server.setHandler(new CIServer());
		server.start();
		server.join();
	}
	
	/**
	 *  starts server
	 *  */
	@Override
	public void handle(String target, Request baseRequest, jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		WebhookRequest webhookRequest = null;
		if(request.getMethod() == "GET") {
			printBuildHistory(response);
		}
		if (request.getMethod() == "POST") {
		
			String body = getBody(request);
			try {
				webhookRequest = new WebhookRequest(new JSONObject(body));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			var status = compileRepo(webhookRequest);
			
			String emailBody = createBody(webhookRequest.getEmailAddress(), webhookRequest.getBranchName(), webhookRequest.getCommitMessage(),status.isSuccessBuild(), status.isSuccessTest());
			try {
				writeToFile(status, webhookRequest);
				sendEmail(webhookRequest.getEmailAddress(), "test on branch: " + webhookRequest.getBranchName(), emailBody);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			
		}
		// body = parseJSON(body);
		System.out.println("\n" + baseRequest + "\n" + response);
		response.getWriter().println("CI job done");
	}
	
	private void printBuildHistory(jakarta.servlet.http.HttpServletResponse response) throws JSONException, IOException {
		response.getWriter().println("buildHistory");
		JSONObject jsonObject = new JSONObject(readFromFile());
		JSONArray history = jsonObject.getJSONArray("history");
		for(int i = 0; i < history.length(); i++) {
			response.getWriter().println("<h2> ------------Build-----------</h2>");
			response.getWriter().println(history.getJSONObject(i).getString("branch"));
			response.getWriter().println("<p> commit made by: " + history.getJSONObject(i).getString("email") + "</p>");
			response.getWriter().println("<p> commit with message: " + history.getJSONObject(i).getString("message") + "</p>");
			response.getWriter().println("<p>output from build: " + history.getJSONObject(i).getString("buildStatus") + "</p>");
			if(history.getJSONObject(i).getBoolean("successBuild")) {
				response.getWriter().println("<p>  build was: successful" + "<p>");
			} else {
				response.getWriter().println("<p> build was: failed </p>");
			}
			response.getWriter().println("<p>output from test: " + history.getJSONObject(i).getString("testStatus") + "</p>");

			if(history.getJSONObject(i).getBoolean("successTest")) {
				response.getWriter().println("<p>test was: successful</p>");
			} else {
				response.getWriter().println("<p>test was: failed</p>");
			}
		}
		
		
	}

	/**
	 * write json to file
	 * @param information from build
	 * @param information from webhook
	 * */
	public void writeToFile(BuildStatus build, WebhookRequest webhookRequest) {
	    try {
	    	JSONObject json = new JSONObject(build);
	        File myObj = new File("history.json");
	        if (myObj.createNewFile()) {
		        System.out.println("File created: " + myObj.getName());
	        } 
	        FileWriter myWriter = new FileWriter("history.json");
	        JSONObject jsonObject = new JSONObject(readFromFile());
	        json.put("branch", webhookRequest.getBranchName());
	        json.put("message", webhookRequest.getCommitMessage());
	        json.put("email", webhookRequest.getEmailAddress());
	        jsonObject.getJSONArray("history").put(json);
	        myWriter.write(jsonObject.toString(2));
	        myWriter.close();
	        System.out.println("Successfully wrote to the file.");
	      } catch (IOException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }
	}
	
	private String readFromFile() throws IOException {
		String data = "";
	    try {
	        File myObj = new File("history.json");
	        Scanner myReader = new Scanner(myObj);

	        while (myReader.hasNextLine()) {
	          data += myReader.nextLine();
	        }
	        myReader.close();
	        if(data.isBlank()) {
		        FileWriter myWriter = new FileWriter("history.json");
		        myWriter.write("{\"history\":[]}");
		        myWriter.close();
		        return "{\"history\":[]}";
	        }
	      } catch (FileNotFoundException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }
		
		return data;
	}

	/**
	 * clones directory from remote into temp dir.
	 * runs gradle build and test.
	 * saves output from build in buildStatus
	 * @return success state
	 * */
	public BuildStatus compileRepo(WebhookRequest webhook) {
		BuildStatus buildStatus = new BuildStatus();

		processBuilder.directory(new File("../../"));
		processBuilder.command("mkdir", "temp");
		runProcess();
		
		processBuilder.directory(new File("../../temp"));
		processBuilder.command("git", "clone", webhook.getRepoAddress());
		buildStatus.setCloneStatus(runProcess());
		
		processBuilder.directory(new File("../../temp/" + webhook.getRepoName()));
		processBuilder.command("git", "checkout", webhook.getBranchName());
		runProcess();
		
		processBuilder.directory(new File("../../temp/"+webhook.getRepoName()+"/CIServer"));
		processBuilder.command("./gradlew", "build");
		buildStatus.setBuildStatus(runProcess());
		
		processBuilder.directory(new File("../../temp/"+webhook.getRepoName()+"/CIServer"));
		processBuilder.command("./gradlew", "test");
		buildStatus.setTestStatus(runProcess());
		
		processBuilder.directory(new File("../../"));
		processBuilder.command("rm", "-rf", "temp");
		runProcess();
		
		buildStatus.setSuccessBuild();
		buildStatus.setSuccessTest();

		return buildStatus;
	}
	/**
	 * Executes command set in processbuilder
	 * @return Output from process 
	 * */
	private String runProcess() {
	    try {
			Process process = processBuilder.start();
	        StringBuilder output = new StringBuilder();
	        BufferedReader reader = new BufferedReader(
	                new InputStreamReader(process.getInputStream()));

	        String line;
	        while ((line = reader.readLine()) != null) {
	            output.append(line + "\n");
	        }

	        int exitVal = process.waitFor();
	        if (exitVal == 0) {
	            System.out.println(output);
	            process.destroy();
	            return output.toString();
	        } else {
	        	process.destroy();
	            return output.toString();
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
		return "";
	}

	/**
	 * Parses body from http-request
	 * @return the body 
	 * */
	private String getBody(jakarta.servlet.http.HttpServletRequest request) throws IOException {
		String body;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		InputStream inputStream = request.getInputStream();
		if (inputStream != null) {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			char[] charBuffer = new char[128];
			int bytesRead = -1;
			while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
				stringBuilder.append(charBuffer, 0, bytesRead);
			}
		} else {
			stringBuilder.append("");
		}
		bufferedReader.close();
		body = stringBuilder.toString();

		return body;
	}
  
  	/**
     * Creates the body of the email notification.
     * It contains:
     * branch, commit message & version, if the code compiles, if the tests work
     */
    public String createBody(String to, String branch, String commitMessage, boolean compiles, boolean tests) {
		String compileMessage = "";

        if(compiles && tests) {
			compileMessage = "succeeded. The code has compiled and the tests pass.";
        } else if(compiles) {
			compileMessage = "failed. The code compiles but the tests fail.";
        } else if(tests) {
			compileMessage = "failed. The code does not compile.";
        } else {
			compileMessage = "failed. The code does not compile.";
        }

		String body = String.format("Hello %s. Your commit %s on branch %s has %s",
				to, commitMessage, branch, compileMessage);
		return body;
    }

    /**
     * Sends an email notification to the person who committed.
     * @param to : person who committed
     * @param subject : commit
     * @param body : information about the commit
     */
    public void sendEmail(String to, String subject, String body) throws AddressException, MessagingException {
        final String from = credentialHelper.getServerEmail();

        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, credentialHelper.getServerPassword());
                    }
                });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}