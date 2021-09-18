package ru.itmo.soa.service;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import ru.itmo.soa.dao.HumanBeingDao;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.validator.HumanValidator;
import ru.itmo.soa.servlet.HumanBeingRequestParams;

import javax.persistence.EntityNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class HumanBeingService {

    private final Gson gson;
    private final HumanBeingDao dao;
    private final HumanValidator humanValidator;

    public HumanBeingService(Gson gson) {
        this.gson =gson;
        humanValidator = new HumanValidator();
        dao = new HumanBeingDao();
    }

    @SneakyThrows
    public void countSoundtrackNameLess(HttpServletResponse response, String soundtrack) {
        Long count = dao.countHumansWeaponTypeLess(soundtrack);
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        writer.write(gson.toJson(count));
    }

    @SneakyThrows
    public void findMinutesOfWaitingLess(HttpServletResponse response, long minutesOfWaiting) {
        List<HumanBeing> list = dao.findHumansMinutesOfWaitingLess(minutesOfWaiting);
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        writer.write(gson.toJson(list));
    }

    @SneakyThrows
    public void deleteAllMinutesOfWaitingEqual(HttpServletResponse response, long minutesOfWaiting) {
        long id = dao.deleteAllHumanMinutesOfWaitingEqual(minutesOfWaiting);
        response.setStatus(id >= 0 ? 200 : 404);
        PrintWriter writer = response.getWriter();
        writer.write("success");
    }

    @SneakyThrows
    public void getHuman(HttpServletResponse response, int id) {
        Optional<HumanBeing> human = dao.getHuman(id);
        if (human.isPresent()) {
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            writer.write(gson.toJson(human.get()));
        } else {
            throw new EntityNotFoundException("Cannot find human with id " + id);
        }
    }

    @SneakyThrows
    public void getAllHumans(HttpServletResponse response, HumanBeingRequestParams params) {
        HumanBeingDao.PaginationResult humans = dao.getAllHumans(params);
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        writer.write(gson.toJson(humans));
    }

    @SneakyThrows
    public void createHuman(HttpServletRequest request, HttpServletResponse response) {
        String requestData = request.getReader().lines().collect(Collectors.joining());
        HumanData humanData = gson.fromJson(requestData, HumanData.class);
        humanValidator.validate(humanData);
        int id = dao.createHuman(humanData.toHumanBeing());
        response.setStatus(201);
        response.getWriter().write(gson.toJson(id));
    }

    @SneakyThrows
    public void updateHuman(HttpServletRequest request, HttpServletResponse response) {
        String requestData = request.getReader().lines().collect(Collectors.joining());
        HumanData humanData = gson.fromJson(requestData, HumanData.class);
        humanValidator.validate(humanData);
        Optional<HumanBeing> human = dao.getHuman(humanData.getId());
        if (human.isPresent()) {
            HumanBeing humanBeing = human.get();
            humanBeing.update(humanData);
            dao.updateHuman(humanBeing);
            response.setStatus(200);
        } else {
            throw new EntityNotFoundException("Cannot update human");
        }
    }

    public void deleteHuman(HttpServletResponse response, int id) {
        if (dao.deleteHuman(id)) {
            response.setStatus(200);
        } else {
            throw new EntityNotFoundException("Cannot find human with id " + id);
        }
    }
}
