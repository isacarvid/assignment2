package CIServer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.lang.Object;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        String body = getBody(request);
        System.out.println(body);
        //body = parseJSON(body);
        System.out.println( "\n" + baseRequest + "\n" + response);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code
        response.getWriter().println("CI job done");

    }


    private String parseJSON(String body) {
        return null;
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

    /**
     * Creates the body of the email notification.
     * It contains:
     * branch, commit message & version, if the code compiles, if the tests work
     */
    public String createBody(String to, String branch, String commitMessage, String version, boolean compiles, boolean tests) {
        String body = "Hello" + " " + to + ". " + "Your commit " + commitMessage +
                " " + version + " on branch " + branch + " has ";
        if(compiles && tests) {
            body += "succeeded. The code has compiled and the tests pass.";
        }
        else if(compiles) {
            body += "failed. The code compiles but the tests fail.";
        }
        else if(tests) {
            body += "failed. The code does not compile.";
        }
        else { // both fail, idk if possible tbh
            body += "failed. The code does not compile.";
        }
        return body; // ":)";
    }

    /**
     * Sends an email notification to the person who committed.
     * @param to : person who committed
     * @param subject : commit
     * @param body : information about the commit
     */
    public void sendEmail(String to, String subject, String body) throws AddressException, MessagingException {
        final String from = "dd2480server@gmail.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, "Travis789!");
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
