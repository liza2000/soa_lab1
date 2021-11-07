package ru.itmo.soa.app;

import com.google.gson.Gson;


import ru.itmo.soa.dao.HumanBeingRequestParams;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;
import ru.itmo.soa.service.HumanBeingServiceI;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.ValidationException;
import java.text.ParseException;
import java.util.Hashtable;

@Path("/human-being")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HumanBeingResource {

    private static final String WEAPON_TYPE_LESS = "weapon-type-less";
    private static final String SOUNDTRACK_NAME_STARTS = "soundtrack-name-starts";
    private static final String MINUTES_OF_WAITING_PARAM = "minutes-of-waiting";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrack-name";
    private static final String WEAPON_TYPE_PARAM = "weapon-type";

    private final HumanBeingServiceI statelessRemoteBean = lookupRemoteStatelessBean();
    private final Gson gson = new Gson();

    @GET
    public Response get(@Context UriInfo ui) {
        MultivaluedMap<String, String> map = ui.getQueryParameters();
        try {
            PaginationData humans = statelessRemoteBean.getAllHumans(new HumanBeingRequestParams(map));
            return Response.ok(gson.toJson(humans)).build();
        } catch (NumberFormatException e) {
            return Response.status(400).entity("Incorrect number " + e.getMessage()).build();
        } catch (ParseException e) {
            return Response.status(400).entity(e.getMessage()).build();
        }
    }


    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") Long id) {
        try {
            HumanBeing human = statelessRemoteBean.getHuman(id);
            return Response.ok(gson.toJson(human)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (NumberFormatException e) {
            return Response.status(400).entity("Incorrect number " + e.getMessage()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path(SOUNDTRACK_NAME_STARTS)
    public Response getSoundtrackNameStarts(@QueryParam(SOUNDTRACK_NAME_PARAM) String soundtrackName) {
        if (soundtrackName != null)
            return Response.ok(gson.toJson(statelessRemoteBean.findHumansSoundtrackNameStartsWith(soundtrackName))).build();
        return Response.status(400).entity("Incorrect parameter " + SOUNDTRACK_NAME_PARAM).build();
    }

    @GET
    @Path(WEAPON_TYPE_LESS)
    public Response getWeaponTypeLess(@QueryParam(WEAPON_TYPE_PARAM) String weaponType) {
        if (weaponType != null)
            return Response.ok(statelessRemoteBean.countWeaponTypeLess(weaponType)).build();
        return Response.status(400).entity("Incorrect parameter " + WEAPON_TYPE_PARAM).build();
    }


    @POST
    public Response doPost(String humanDataS) {
        try {
            HumanData humanData = gson.fromJson(humanDataS, HumanData.class);
            HumanBeing human = statelessRemoteBean.createHuman(humanData);
            return Response.status(201).entity(gson.toJson(human)).build();
        } catch (NumberFormatException e) {
            return Response.status(400).entity("Incorrect number: " + e.getMessage()).build();
        } catch (ValidationException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response doPut(@PathParam("id") Long id, String request) {
        try {
            HumanData humanData = gson.fromJson(request, HumanData.class);
            statelessRemoteBean.updateHuman(id, humanData);
            return Response.ok(gson.toJson("Updated successfully")).build();
        } catch (NumberFormatException e) {
            return Response.status(400).entity("Incorrect number: " + e.getMessage()).build();
        } catch (ValidationException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (EntityNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response doDelete(@PathParam("id") Long id) {
        try {
            statelessRemoteBean.deleteHuman(id);
            return Response.ok(gson.toJson("Deleted successfully")).build();
        } catch (EntityNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    @DELETE
    public Response doDelete(@QueryParam(MINUTES_OF_WAITING_PARAM) Double minutesOfWaiting) {
        if (minutesOfWaiting != null) {
            int count = statelessRemoteBean.deleteAllMinutesOfWaitingEqual(minutesOfWaiting);
            if (count == 0)
                return Response.status(404).entity("No humans with minutes of waiting = " + minutesOfWaiting).build();
            else return Response.ok(gson.toJson("Deleted " + count + " humans")).build();
        }
        return Response.status(400).entity("Incorrect parameter " + MINUTES_OF_WAITING_PARAM).build();
    }

    private static HumanBeingServiceI lookupRemoteStatelessBean() {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(javax.naming.Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        try {
            final javax.naming.Context context = new InitialContext(jndiProperties);
            // The app name is the application name of the deployed EJBs. This is typically the ear name
            // without the .ear suffix. However, the application name could be overridden in the application.xml of the
            // EJB deployment on the server.
            // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
            final String appName = "global";
            // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
            // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
            final String moduleName = "soa_lab1-snapshot";
            // The EJB name which by default is the simple class name of the bean implementation class
            final String beanName = "HumanBeingService";
            // the remote view fully qualified class name
            final String viewClassName = HumanBeingServiceI.class.getName();
            // let's do the lookup
            String lookupName = "java:" + appName + "/" + moduleName + "/" + beanName + "!" + viewClassName;
            return (HumanBeingServiceI) context.lookup(lookupName);
        } catch (NamingException e) {
            System.out.println("не получилось (");
            return null;
        }
    }
}
