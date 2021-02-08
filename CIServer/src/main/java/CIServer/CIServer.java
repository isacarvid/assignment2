package CIServer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/

public class CIServer extends AbstractHandler
{

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
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

        
        
        System.out.println(request + "\n" + baseRequest + "\n" + response);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
		
	}


}