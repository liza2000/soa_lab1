package ru.itmo.soa.servlet;

import com.google.gson.Gson;

import ru.itmo.soa.dao.HumanBeingDao;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.service.HumanBeingService;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.ValidationException;
import java.text.ParseException;
import java.util.stream.Collectors;

@Path("/human-being")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HumanBeingServlet extends Application{

//    Удалить все объекты, значение поля minutesOfWaiting которого эквивалентно заданному.
//    Вернуть количество объектов, значение поля weaponType которых меньше заданного.
//    Вернуть массив объектов, значение поля soundtrackName которых начинается с заданной подстроки.

    private static final String WEAPON_TYPE_LESS = "weapon-type-less";
    private static final String SOUNDTRACK_NAME_STARTS = "soundtrack-name-starts";

    private static final String MINUTES_OF_WAITING_PARAM = "minutes-of-waiting";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrack-name";
    private static final String WEAPON_TYPE_PARAM = "weapon-type";

    private final HumanBeingService service;

    Gson gson = new Gson();
    public HumanBeingServlet() {
        service = new HumanBeingService();
    }


    private HumanBeingRequestParams getFilterParams(MultivaluedMap<String, String> map) {
        return new HumanBeingRequestParams(map);
    }

    @GET
    public Response get(@Context UriInfo ui) {
        MultivaluedMap<String, String> map = ui.getQueryParameters();
        try {
            HumanBeingRequestParams filterParams = getFilterParams(map);
             HumanBeingDao.PaginationResult humans = service.getAllHumans(filterParams);
            return Response.ok(gson.toJson(humans)).build();
        } catch (NumberFormatException  e) {
            return Response.status(400 ).entity("Incorrect number " + e.getMessage()).build();
        }catch ( ParseException e) {
            return Response.status(400).entity( e.getMessage()).build();
        }
    }


    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") Long id) {
        try {
            HumanBeing human = service.getHuman(id);
            return Response.ok(gson.toJson(human)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(404).entity( e.getMessage()).build();
        } catch (NumberFormatException e) {
            return Response.status(400).entity("Incorrect number " + e.getMessage()).build();
        }
    }

    @GET
    @Path(SOUNDTRACK_NAME_STARTS)
    public Response getSoundtrackNameStarts(@QueryParam(SOUNDTRACK_NAME_PARAM) String soundtrackName) {
        if (soundtrackName != null)
            return Response.ok(gson.toJson(service.findHumansSoundtrackNameStartsWith(soundtrackName))).build();
        return Response.status(400).entity("Incorrect parameter " + SOUNDTRACK_NAME_PARAM).build();
    }

    @GET
    @Path(WEAPON_TYPE_LESS)
    public Response getWeaponTypeLess(@QueryParam(WEAPON_TYPE_PARAM) String weaponType) {
        if (weaponType != null)
            return Response.ok(service.countWeaponTypeLess(weaponType)).build();
        return Response.status(400).entity("Incorrect parameter " + WEAPON_TYPE_PARAM).build();
    }


    @POST
    public Response doPost(@Context HttpServletRequest request) {
        try {
            String requestData = request.getReader().lines().collect(Collectors.joining());
            HumanData humanData = gson.fromJson(requestData, HumanData.class);
            HumanBeing human = service.createHuman(humanData);
            return Response.status(201).entity(gson.toJson(human)).build();
        } catch (NumberFormatException e) {
            return Response.status(400).entity("Incorrect number: " + e.getMessage()).build();
        } catch (ValidationException e) {
            return Response.status(400).entity(e.getMessage()).build();
        }catch (Exception e){
           return Response.serverError().build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response doPut(@PathParam("id") Long id, @Context HttpServletRequest request) {
        try {
            String requestData = request.getReader().lines().collect(Collectors.joining());
            HumanData humanData = gson.fromJson(requestData, HumanData.class);
            service.updateHuman(id, humanData);
            return Response.ok(gson.toJson("Updated successfully")).build();
        } catch (NumberFormatException e) {
            return Response.status(400).entity("Incorrect number: " + e.getMessage()).build();
        } catch (ValidationException e) {
            return Response.status(400).entity(e.getMessage()).build();
        } catch (EntityNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }catch (Exception e){
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response doDelete(@PathParam("id") Long id) {
        try {
            service.deleteHuman(id);
            return Response.ok(gson.toJson("Deleted successfully")).build();
        } catch (EntityNotFoundException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    @DELETE
    public Response doDelete(@QueryParam(MINUTES_OF_WAITING_PARAM) Double minutesOfWaiting) {
        if (minutesOfWaiting != null) {
            int count = service.deleteAllMinutesOfWaitingEqual(minutesOfWaiting);
            if (count == 0)
                return Response.status(404).entity("No humans with minutes of waiting = " + minutesOfWaiting).build();
            else return Response.ok(gson.toJson("Deleted " + count + " humans")).build();
        }
        return Response.status(400).entity("Incorrect parameter " + MINUTES_OF_WAITING_PARAM).build();
    }
}
