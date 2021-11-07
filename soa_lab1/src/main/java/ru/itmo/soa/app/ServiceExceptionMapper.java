package ru.itmo.soa.app;

import javax.ejb.EJBException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.ValidationException;
import java.text.ParseException;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception e) {
        if (e instanceof EntityNotFoundException) {
            return handleEntityNotFoundException((EntityNotFoundException) e);
        } else if (e instanceof NumberFormatException) {
            return handleNumberFormatException((NumberFormatException) e);
        } else if (e instanceof ValidationException) {
            return handleValidatorException((ValidationException) e);
        } else if (e instanceof EJBException) {
            return handleEJBException((EJBException) e);
        } else if (e instanceof ParseException) {
            return handleParseException((ParseException) e);
        }
        return handleUnknownException(e);
    }

    private Response handleUnknownException(Exception e) {
        return Response.status(500).entity(e.getMessage()).build();
    }

    private Response handleEJBException(EJBException ejbException) {
        Exception e = ejbException.getCausedByException();
        if (e == null) {
            return handleUnknownException(ejbException);
        }
        if (e instanceof EntityNotFoundException) {
            return handleEntityNotFoundException((EntityNotFoundException) e);
        } else if (e instanceof NumberFormatException) {
            return handleNumberFormatException((NumberFormatException) e);
        } else if (e instanceof ValidationException) {
            return handleValidatorException((ValidationException) e);
        } else if (e instanceof ParseException) {
            return handleParseException((ParseException) e);
        }
        return handleUnknownException(e);
    }

    private Response handleParseException(ParseException e) {
        return Response.status(400).entity(e.getMessage()).build();
    }

    private Response handleEntityNotFoundException(EntityNotFoundException e) {
        return Response.status(404).entity(e.getMessage()).build();
    }

    private Response handleNumberFormatException(NumberFormatException e) {
        return Response.status(400).entity("Incorrect number: " + e.getMessage()).build();
    }

    private Response handleValidatorException(ValidationException e) {
        return Response.status(400).entity(e.getMessage()).build();
    }
}
