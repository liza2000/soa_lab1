package ru.itmo.soa.service;


import lombok.SneakyThrows;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.entity.data.HumanData;
import ru.itmo.soa.entity.data.PaginationData;

import javax.ejb.Remote;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.ValidationException;
import java.text.ParseException;
import java.util.List;

@Remote
public interface RemoteEJBInterface {

    @SneakyThrows
    public Long countWeaponTypeLess(String weaponType);

    @SneakyThrows
    public List<HumanBeing> findHumansSoundtrackNameStartsWith(String soundtrackName);

    @SneakyThrows
    public int deleteAllMinutesOfWaitingEqual(double minutesOfWaiting);

    @SneakyThrows
    public HumanBeing getHuman(long id);

    public PaginationData getAllHumans(MultivaluedMap<String, String> info) throws ParseException;

    @SneakyThrows
    public HumanBeing createHuman(HumanData humanData) throws ValidationException;

    @SneakyThrows
    public void updateHuman(long id, HumanData humanData) throws ValidationException;

    @SneakyThrows
    public void deleteHuman(Long id);
}
