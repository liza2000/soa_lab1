package ru.itmo.soa.service;

import lombok.SneakyThrows;
import ru.itmo.soa.dao.HumanBeingRequestParams;
import ru.itmo.soa.dao.HumanBeingDao;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;
import ru.itmo.soa.entity.data.validator.HumanValidator;

import javax.ejb.Stateless;
import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Stateless
public class HumanBeingService implements HumanBeingServiceI {

    private final HumanBeingDao dao;
    private final HumanValidator humanValidator;

    public HumanBeingService() {
        humanValidator = new HumanValidator();
        dao = new HumanBeingDao();
    }

    @SneakyThrows
    public Long countWeaponTypeLess(String weaponType) {
        return dao.countHumansWeaponTypeLess(weaponType);
    }

    @SneakyThrows
    public List<HumanBeing> findHumansSoundtrackNameStartsWith(String soundtrackName) {
        return dao.findHumansSoundtrackNameStarts(soundtrackName);
    }

    @SneakyThrows
    public int deleteAllMinutesOfWaitingEqual(double minutesOfWaiting) {
        return dao.deleteAllHumanMinutesOfWaitingEqual(minutesOfWaiting);
    }

    @SneakyThrows
    public HumanBeing getHuman(long id) {
        Optional<HumanBeing> human = dao.getHuman(id);
        if (human.isPresent())
            return human.get();
        else
            throw new EntityNotFoundException("Cannot find human with id " + id);
    }

    public PaginationData getAllHumans(HumanBeingRequestParams params) throws ParseException {
        return dao.getAllHumans(params);
    }

    @SneakyThrows
    public HumanBeing createHuman(HumanData humanData) throws ValidationException {
        humanValidator.validate(humanData);
        return dao.createHuman(humanData.toHumanBeing());
    }

    @SneakyThrows
    public void updateHuman(long id, HumanData humanData) throws ValidationException {
        Optional<HumanBeing> human = dao.getHuman(id);
        if (human.isPresent()) {
            HumanBeing humanBeing = human.get();
            humanBeing.update(humanData);
            dao.updateHuman(humanBeing);
        } else
            throw new EntityNotFoundException("Cannot update human with id " + id);

    }

    @SneakyThrows
    public void deleteHuman(Long id) {
        if (!dao.deleteHuman(id)) {
            throw new EntityNotFoundException("Cannot find human with id " + id);
        }
    }
}
