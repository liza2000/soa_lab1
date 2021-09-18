package ru.itmo.soa.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import static jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION;

@WebServlet(urlPatterns = "/errorHandler")
public class ErrorHandlerServlet extends HttpServlet {


    private void handle(HttpServletRequest req,
                        HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=utf-8");
        try (PrintWriter writer = resp.getWriter()) {
            Exception exception = (Exception) req.getAttribute(ERROR_EXCEPTION);
            if (exception instanceof EntityNotFoundException) {
                resp.setStatus(404);
            } else {
                resp.setStatus(400);
            }
            writer.write(exception.getMessage());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handle(req, resp);
    }
}
