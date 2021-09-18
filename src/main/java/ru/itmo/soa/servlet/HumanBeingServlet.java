package ru.itmo.soa.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import ru.itmo.soa.service.HumanBeingService;

@WebServlet(name = "humanBeing", value = "/human-being/*")
public class HumanBeingServlet extends HttpServlet {

    private static final String SORTING_PARAM = "sort";
    private static final String OFFSET_PARAM = "offset";
    private static final String PAGE_SIZE_PARAM = "limit";

    private static final String SOUNDTRACK_NAME_LESS_PARAM = "soundtrackNameLess";
    private static final String MINUTES_OF_WAITING_LESS_PARAM = "minutesOfWaitingLess";

    private static final String NAME_PARAM = "name";
    private static final String MINUTES_OF_WAITING_PARAM = "minutesOfWaiting";
    private static final String REAL_HERO_PARAM = "realHero";
    private static final String HAS_TOOTHPICK_PARAM = "hasToothpick";
    private static final String IMPACT_SPEED_PARAM = "impactSpeed";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrackName";
    private static final String WEAPON_TYPE_PARAM = "weaponType";
    private static final String CAR_NAME_PARAM = "carName";
    private static final String COORDINATES_X_PARAM = "coordinatesX";
    private static final String COORDINATES_Y_PARAM = "coordinatesY";
    private static final String CREATION_DATE_PARAM = "date";

    private HumanBeingService service;

    @Override
    public void init() throws ServletException {
        super.init();
        service = new HumanBeingService(new Gson());
    }

    private HumanBeingRequestParams getFilterParams(HttpServletRequest request) {
       return new HumanBeingRequestParams(
               request.getParameter(NAME_PARAM),
               request.getParameterValues(MINUTES_OF_WAITING_PARAM),
               request.getParameter(REAL_HERO_PARAM),
               request.getParameter(HAS_TOOTHPICK_PARAM),
               request.getParameterValues(IMPACT_SPEED_PARAM),
               request.getParameter(SOUNDTRACK_NAME_PARAM),
               request.getParameterValues(WEAPON_TYPE_PARAM),
               request.getParameter(CAR_NAME_PARAM),
               request.getParameterValues(COORDINATES_X_PARAM),
               request.getParameterValues(COORDINATES_Y_PARAM),
               request.getParameterValues(CREATION_DATE_PARAM),
               request.getParameterValues(SORTING_PARAM),
               request.getParameter(OFFSET_PARAM),
               request.getParameter(PAGE_SIZE_PARAM)
        );
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            HumanBeingRequestParams filterParams = getFilterParams(request);
            String minutesOfWaitingLess = request.getParameter(MINUTES_OF_WAITING_LESS_PARAM);
            if (minutesOfWaitingLess != null)
                service.findMinutesOfWaitingLess(response, Long.parseLong(minutesOfWaitingLess));
             else
                service.getAllHumans(response, filterParams);

        } else {
            String[] servletPath = pathInfo.split("/");
            if (servletPath.length > 1) {
                String id = servletPath[1];
                service.getHuman(response, Integer.parseInt(id));
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            service.createHuman(request, response);
        } else {
            String[] servletPath = pathInfo.split("/");
            if (servletPath.length > 1) {
                String soundTrackName = request.getParameter(SOUNDTRACK_NAME_LESS_PARAM);
                service.countSoundtrackNameLess(response, soundTrackName);
            }
        }
    }

    @SneakyThrows
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        service.updateHuman(request, response);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            String minutesOfWaiting = request.getParameter(MINUTES_OF_WAITING_PARAM);
            if (minutesOfWaiting != null) {
                service.deleteAllMinutesOfWaitingEqual(response, Integer.parseInt(minutesOfWaiting));
            }
        } else {
            String[] servletPath = pathInfo.split("/");
            if (servletPath.length > 1) {
                String id = servletPath[1];
                service.deleteHuman(response, Integer.parseInt(id));
            }
        }
    }
}
