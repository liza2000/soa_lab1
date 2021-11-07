package ru.itmo.soa.service;

import ru.itmo.soa.dao.HumanBeingRequestParams;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;

import javax.ejb.Remote;
import javax.xml.bind.ValidationException;
import java.text.ParseException;
import java.util.List;

@Remote
public interface HumanBeingServiceI {
    Long countWeaponTypeLess(String weaponType);
    List<HumanBeing> findHumansSoundtrackNameStartsWith(String soundtrackName);
    int deleteAllMinutesOfWaitingEqual(double minutesOfWaiting);
    HumanBeing getHuman(long id);
    PaginationData getAllHumans(HumanBeingRequestParams params) throws ParseException;
    HumanBeing createHuman(HumanData humanData) throws ValidationException;
    void updateHuman(long id, HumanData humanData) throws ValidationException;
    void deleteHuman(Long id);
}
