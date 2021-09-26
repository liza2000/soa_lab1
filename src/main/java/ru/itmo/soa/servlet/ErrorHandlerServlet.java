package ru.itmo.soa.servlet;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;

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
            if (exception instanceof NumberFormatException)
                writer.write("Incorrect number "+exception.getMessage());
            else
            writer.write(exception.getMessage());
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handle(req, resp);
    }
}
