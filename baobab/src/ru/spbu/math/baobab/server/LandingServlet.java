package ru.spbu.math.baobab.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bardsoftware.server.AppUrlService;
import com.bardsoftware.server.auth.AuthService;

public class LandingServlet extends HttpServlet {
  private final AppUrlService urlService = new AppUrlService(DevMode.IS_ENABLED);
  private final AuthService authService = new AuthService(urlService.getDomainName());
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    authService.setupUserAndMaintenance(req);
    String[] path = req.getRequestURI().split("/");
    String file = path.length <= 1 ? (DevMode.IS_ENABLED ? "dev_login.jsp" : "index.jsp") : path[1];
    if (file.endsWith(".jsp")) {
      RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/" + file);
      dispatcher.forward(req, resp);
    } else {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
