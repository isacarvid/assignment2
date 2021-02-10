package CIServer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.lang.Object;

import java.util.Properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import models.WebhookRequest;

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook See the Jetty
 * documentation for API documentation of those classes.
 */

public class CIServer extends AbstractHandler {
	private ProcessBuilder processBuilder = new ProcessBuilder();

	// used to start the CI server in command line
	public static void main(String[] args) throws Exception {
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
		
		if (request.getMethod() == "POST") {
			String body = getBody(request);
			try {
				WebhookRequest webhookRequest = new WebhookRequest(new JSONObject(body));
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		// body = parseJSON(body);
		System.out.println("\n" + baseRequest + "\n" + response);

		// here you do all the continuous integration tasks
		// for example
		// 1st clone your repository
		// 2nd compile the code
		response.getWriter().println("CI job done");
	}

	public boolean compileRepo() {
		String repo = "https://github.com/isacarvid/assignment2";
		processBuilder.command("mkdir", "../../test");
		//Process process = Runtime.getRuntime().exec({"mkdir","/Users/isacarvidsson/Desktop/kth/proggrund/assignment2/CIServer/test"});
		runProcess();

		
		processBuilder.command("git", "clone", repo, "../../test");
		runProcess();
		
		return false;
	}
	
	private boolean runProcess() {
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
	            return true;
	        } else {
	        	process.destroy();
	            return false;
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
		return false;
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
