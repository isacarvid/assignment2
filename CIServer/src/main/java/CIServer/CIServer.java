package CIServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.lang.Object;

import java.util.Properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import models.BuildStatus;
import models.WebhookRequest;

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook See the Jetty
 * documentation for API documentation of those classes.
 */

public class CIServer extends AbstractHandler {
	private ProcessBuilder processBuilder = new ProcessBuilder();

	public void startServer() throws Exception {
		Server server = new Server(8095);
		server.setHandler(new CIServer());
		server.start();
		server.join();
	}

	
	@Override
	public void handle(String target, Request baseRequest, jakarta.servlet.http.HttpServletRequest request,
			jakarta.servlet.http.HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		WebhookRequest webhookRequest = null;
		if (request.getMethod() == "POST") {
			String body = getBody(request);
			try {
				webhookRequest = new WebhookRequest(new JSONObject(body));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			var status = compileRepo(webhookRequest);
			System.out.println(webhookRequest.getEmailAddress());
		}
		
		// body = parseJSON(body);
		System.out.println("\n" + baseRequest + "\n" + response);

		// here you do all the continuous integration tasks
		// for example
		// 1st clone your repository
		// 2nd compile the code
		response.getWriter().println("CI job done");
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

}
