package ru.spbu.math.baobab.devtools.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

public class CodeReviewServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger("CodeReview");
  private static final String REVIEW_PREFIX = "review:";
  private static final Properties properties = new Properties();
  static {
    loadProperties(properties, "/app.properties");
  }
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String body = new String(ByteStreams.toByteArray(req.getInputStream()), Charsets.UTF_8);
    try {
      JSONObject jsonBody = new JSONObject(body);
      JSONArray revisions = jsonBody.optJSONArray("revisions");
      if (revisions == null) {
        return;
      }
      for (int i = 0; i < revisions.length(); i++) {
        processRevision(revisions.optJSONObject(i));
      }
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

  private void processRevision(JSONObject jsonRev) {
    String message = jsonRev.optString("message");
    if (message == null) {
      return;
    }
    for (String line : message.split("\\n")) {
      line = line.trim();
      if (line.toLowerCase().startsWith(REVIEW_PREFIX)) {
        List<String> emails = Lists.newArrayList();
        for (String reviewer : line.substring(REVIEW_PREFIX.length()).trim().split(",")) {
          reviewer = reviewer.trim();
          String email = (String) properties.get("codereview.email." + reviewer);
          if (email == null) {
            continue;
          }
          emails.add(email);
        }
        if (!emails.isEmpty()) {
          sendEmails(emails, createText(jsonRev));
        }
      }
    }
  }

  private String createText(JSONObject jsonRev) {
    StringBuilder result = new StringBuilder("Hello, can you please review my code? The changelist is available at this link: to http://code.google.com/p/baobab/source/detail?r=");
    result.append(jsonRev.optString("revision")).append("\n");
    result.append(Strings.repeat("=", 32)).append("\n");
    result.append(jsonRev.optString("message"));
    result.append("\n");
    result.append("-- " + jsonRev.optString("author"));
    return result.toString();
  }

  private void sendEmails(List<String> recipients, String text) {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);
    try {
        Message msg = new MimeMessage(session);
        InternetAddress fromAddress = new InternetAddress("codereview@matmex-baobab.appspotmail.com", "Code Review Bot");
        msg.setFrom(fromAddress);
        for (String email : recipients) {
          msg.addRecipient(RecipientType.TO, new InternetAddress(email));
        }
        msg.setSubject("Code review request");
        msg.setText(text);
        Transport.send(msg);
        LOGGER.info("Sent email to " + recipients);
    } catch (AddressException e) {
      LOGGER.log(Level.SEVERE, "Failed to send out emails", e);
    } catch (MessagingException e) {
      LOGGER.log(Level.SEVERE, "Failed to send out emails", e);
    } catch (UnsupportedEncodingException e) {
      LOGGER.log(Level.SEVERE, "Failed to send out emails", e);
    }
    
  }

  private static void loadProperties(Properties result, String resource) {
    URL url = CodeReviewServlet.class.getResource(resource);
    if (url == null) {
      return;
    }
    try {
      result.load(url.openStream());
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Failed to load properties", e);
    }
  }
}
