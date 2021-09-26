package ru.itmo.soa.servlet;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import ru.itmo.soa.service.HumanBeingService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@WebServlet(name = "humanBeing", value = "/human-being/*")
public class HumanBeingServlet extends HttpServlet {


//    Удалить все объекты, значение поля minutesOfWaiting которого эквивалентно заданному.
//    Вернуть количество объектов, значение поля weaponType которых меньше заданного.
//    Вернуть массив объектов, значение поля soundtrackName которых начинается с заданной подстроки.

    private static final String WEAPON_TYPE_LESS = "weaponTypeLess";
    private static final String SOUNDTRACK_NAME_STARTS = "soundtrackNameStarts";

    private static final String MINUTES_OF_WAITING_PARAM = "minutesOfWaiting";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrackName";
    private static final String WEAPON_TYPE_PARAM = "weaponType";

    private HumanBeingService service;

    @Override
    public void init() throws ServletException {
        super.init();
        service = new HumanBeingService(new Gson());
    }

    private HumanBeingRequestParams getFilterParams(HttpServletRequest request) {
       return new HumanBeingRequestParams(request);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        String[] servletPath = pathInfo == null ? null : pathInfo.split("/");

        if (servletPath == null || servletPath.length <= 1) {
            HumanBeingRequestParams filterParams = getFilterParams(request);
            service.getAllHumans(response, filterParams);
            return;
        }

        if (SOUNDTRACK_NAME_STARTS.equals(servletPath[1])) {
            String soundtrackName = request.getParameter(SOUNDTRACK_NAME_PARAM);
            if (soundtrackName != null)
                service.findHumansSoundtrackNameStartsWith(response, soundtrackName);
            return;
        }

        if (WEAPON_TYPE_LESS.equals(servletPath[1])) {
            String weaponType = request.getParameter(WEAPON_TYPE_PARAM);
            if (weaponType != null)
                service.countWeaponTypeLess(response, weaponType);
            return;
        }
        String id = servletPath[1];
        service.getHuman(response, Long.parseLong(id));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
            service.createHuman(request, response);
    }

    @SneakyThrows
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        String[] servletPath = pathInfo == null ? null : pathInfo.split("/");

        if (servletPath != null && servletPath.length > 1) {
            String id = servletPath[1];
            service.updateHuman(Long.parseLong(id), request, response);
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length==0) {
            String minutesOfWaiting = request.getParameter(MINUTES_OF_WAITING_PARAM);
            if (minutesOfWaiting != null)
                service.deleteAllMinutesOfWaitingEqual(response, Double.parseDouble(minutesOfWaiting));
        } else {
            String[] servletPath = pathInfo.split("/");
            if (servletPath.length > 1) {
                String id = servletPath[1];
                service.deleteHuman(response, Integer.parseInt(id));
            }
        }
    }
}
