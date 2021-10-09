package ru.itmo.soa.servlet;

import ru.itmo.soa.entity.Car;
import ru.itmo.soa.entity.Coordinates;
import ru.itmo.soa.entity.HumanBeing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HumanBeingRequestParams {
    public final String name;
    public final List<String> minutesOfWaiting;
    public final Boolean realHero;
    public final Boolean hasToothpick;
    public final List<String> impactSpeed;
    public final String soundtrackName;
    public final List<String> weaponType;
    public final String carName;
    public final List<String> coordinatesX;
    public final List<String> coordinatesY;
    public final List<String> creationDate;
    public final List<String> sort;
    public final int pageIndex;
    public final int limit;

    private static final String NAME_PARAM = "name";
    private static final String MINUTES_OF_WAITING_PARAM = "minutes-of-waiting";
    private static final String REAL_HERO_PARAM = "real-hero";
    private static final String HAS_TOOTHPICK_PARAM = "has-toothpick";
    private static final String IMPACT_SPEED_PARAM = "impact-speed";
    private static final String SOUNDTRACK_NAME_PARAM = "soundtrack-name";
    private static final String WEAPON_TYPE_PARAM = "weapon-type";
    private static final String CAR_NAME_PARAM = "carname";
    private static final String COORDINATES_X_PARAM = "coordinatesx";
    private static final String COORDINATES_Y_PARAM = "coordinatesy";
    private static final String CREATION_DATE_PARAM = "creation-date";

    private static final String SORTING_PARAM = "sort";
    private static final String PAGE_INDEX = "page-index";
    private static final String PAGE_SIZE_PARAM = "limit";

    HumanBeingRequestParams(MultivaluedMap<String, String> info) {
        this(info.getFirst(NAME_PARAM),
                info.get(MINUTES_OF_WAITING_PARAM),
                info.getFirst(REAL_HERO_PARAM),
                info.getFirst(HAS_TOOTHPICK_PARAM),
                info.get(IMPACT_SPEED_PARAM),
                info.getFirst(SOUNDTRACK_NAME_PARAM),
                info.get(WEAPON_TYPE_PARAM),
                info.getFirst(CAR_NAME_PARAM),
                info.get(COORDINATES_X_PARAM),
                info.get(COORDINATES_Y_PARAM),
                info.get(CREATION_DATE_PARAM),
                info.get(SORTING_PARAM),
                info.getFirst(PAGE_INDEX),
                info.getFirst(PAGE_SIZE_PARAM)
        );
    }

  private   HumanBeingRequestParams(
            String name,
            List<String> minutesOfWaiting,
            String realHero,
            String hasToothpick,
            List<String> impactSpeed,
            String soundtrackName,
            List<String> weaponType,
            String carName,
            List<String> coordinatesX,
            List<String> coordinatesY,
            List<String> creationDate,
            List<String> sort,
            String pageIndex,
            String limit
    ) {
        this.sort = sort;
        this.name = name;
        this.minutesOfWaiting = minutesOfWaiting;
        this.realHero = realHero == null ? null : Boolean.parseBoolean(realHero);
        this.hasToothpick = hasToothpick == null ? null : Boolean.parseBoolean(hasToothpick);
        this.impactSpeed = impactSpeed;
        this.soundtrackName = soundtrackName;
        this.weaponType = weaponType;
        this.carName = carName;
        this.coordinatesX = coordinatesX;
        this.coordinatesY = coordinatesY;
        this.creationDate = creationDate;
        this.pageIndex = pageIndex == null ? 0 : Integer.parseInt(pageIndex);
        this.limit = limit == null ? 5 : Integer.parseInt(limit);
    }

    private String like(String val) {
        return "%" + val + "%";
    }

    boolean isHumanBeingField(String param) {
        if (param==null) return false;
        param=param.toLowerCase();
        return (NAME_PARAM.replaceAll("-","").equals(param)
                || MINUTES_OF_WAITING_PARAM.replaceAll("-","").equals(param)
                || IMPACT_SPEED_PARAM.replaceAll("-","").equals(param)
                || WEAPON_TYPE_PARAM.replaceAll("-","").equals(param)
                || CAR_NAME_PARAM.replaceAll("-","").equals(param)
                || HAS_TOOTHPICK_PARAM.replaceAll("-","").equals(param)
                || REAL_HERO_PARAM.replaceAll("-","").equals(param)
                || SOUNDTRACK_NAME_PARAM.replaceAll("-","").equals(param)
                || COORDINATES_X_PARAM.replaceAll("-","").equals(param)
                || COORDINATES_Y_PARAM.replaceAll("-","").equals(param)
                || CREATION_DATE_PARAM.replaceAll("-","").equals(param));
    }

    public List<javax.persistence.criteria.Predicate> getPredicates(
            CriteriaBuilder cb,
            Root<HumanBeing> root,
            Join<HumanBeing, Car> join,
            Join<HumanBeing, Coordinates> joinCoordinates
    ) throws ParseException {
        List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
        if (name != null)
            predicates.add(cb.like(root.get("name"), like(name)));

        if (minutesOfWaiting != null)
            if (minutesOfWaiting.size() > 1) {
                if (minutesOfWaiting.get(0) != null && !minutesOfWaiting.get(0).isEmpty())
                    predicates.add(cb.ge(root.get("minutesOfWaiting"), Double.parseDouble(minutesOfWaiting.get(0))));
                if (minutesOfWaiting.get(1) != null && !minutesOfWaiting.get(1).isEmpty())
                    predicates.add(cb.le(root.get("minutesOfWaiting"), Double.parseDouble(minutesOfWaiting.get(1))));
            } else if (minutesOfWaiting.get(0) != null && !minutesOfWaiting.get(0).isEmpty())
                predicates.add(cb.equal(root.get("minutesOfWaiting"), Double.parseDouble(minutesOfWaiting.get(0))));

        if (realHero != null)
            predicates.add(cb.equal(root.get("realHero"), realHero));

        if (hasToothpick != null)
            predicates.add(cb.equal(root.get("hasToothpick"), hasToothpick));

        if (impactSpeed != null)
            if (impactSpeed.size() > 1) {
                if (impactSpeed.get(0) != null && !impactSpeed.get(0).isEmpty())
                    predicates.add(cb.ge(root.get("impactSpeed"), Float.parseFloat(impactSpeed.get(0))));
                if (impactSpeed.get(1) != null && !impactSpeed.get(1).isEmpty())
                    predicates.add(cb.le(root.get("impactSpeed"), Float.parseFloat(impactSpeed.get(1))));
            } else if (impactSpeed.get(0) != null && !impactSpeed.get(0).isEmpty())
                predicates.add(cb.equal(root.get("impactSpeed"), Float.parseFloat(impactSpeed.get(0))));

        if (soundtrackName != null)
            predicates.add(cb.like(root.get("soundtrackName"), like(soundtrackName)));

        if (weaponType != null)
            predicates.add(root.get("weaponType").as(String.class).in(weaponType));

        if (carName != null)
            predicates.add(cb.like(join.get("name"), like(carName)));

        if (coordinatesX != null)
            if (coordinatesX.size() > 1) {
                if (coordinatesX.get(0) != null && !coordinatesX.get(0).isEmpty())
                    predicates.add(cb.ge(joinCoordinates.get("x"), Float.parseFloat(coordinatesX.get(0))));
                if (coordinatesX.get(1) != null && !coordinatesX.get(1).isEmpty())
                    predicates.add(cb.le(joinCoordinates.get("x"), Float.parseFloat(coordinatesX.get(1))));
            } else if (coordinatesX.get(0) != null && !coordinatesX.get(0).isEmpty())
                predicates.add(cb.equal(joinCoordinates.get("x"), Float.parseFloat(coordinatesX.get(0))));

        if (coordinatesY != null)
            if (coordinatesY.size() > 1) {
                if (coordinatesY.get(0) != null && !coordinatesY.get(0).isEmpty())
                    predicates.add(cb.ge(joinCoordinates.get("y"), Float.parseFloat(coordinatesY.get(0))));
                if (coordinatesY.get(1) != null && !coordinatesY.get(1).isEmpty())
                    predicates.add(cb.le(joinCoordinates.get("y"), Float.parseFloat(coordinatesY.get(1))));
            } else if (coordinatesY.get(0) != null && !coordinatesY.get(0).isEmpty())
                predicates.add(cb.equal(joinCoordinates.get("y"), Float.parseFloat(coordinatesY.get(0))));
        if (creationDate != null)
            if (creationDate.size() > 1) {
                if (creationDate.get(0) != null && !creationDate.get(0).isEmpty())
                    predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate.get(0))));
                if (creationDate.get(1) != null && !creationDate.get(1).isEmpty())
                    predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate.get(1))));
            } else if (creationDate.get(0) != null && !creationDate.get(0).isEmpty())
                predicates.add(cb.equal(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate.get(0))));

        return predicates;
    }

    public List<Order> getOrders(CriteriaBuilder cb, Root<HumanBeing> root, Join<HumanBeing, Coordinates> joinCoordinates, Join<HumanBeing, Car> joinCar) throws ParseException {
        List<Order> orders = new ArrayList<>();
        if (sort != null)
            for (String s : sort) {
                String[] args = s.split("_", 2);
                if (args.length != 2)
                    throw new ParseException("incorrect sort parameter " + s, 0);
                boolean asc;
                if (args[0].equals("asc"))
                    asc = true;
                else if (args[0].equals("desc"))
                    asc = false;
                else
                    throw new ParseException("incorrect sort parameter " + s, 0);
                String field = args[1];
                if (!isHumanBeingField(field))
                    throw new ParseException("incorrect sort parameter " + s, 0);
                if (field.startsWith("coordinates"))
                    orders.add(asc ? cb.asc(joinCoordinates.get(field.replaceAll("coordinates", "").toLowerCase()))
                            : cb.desc(joinCoordinates.get(field.replaceAll("coordinates", "").toLowerCase())));
                else if (field.startsWith("car"))
                    orders.add(asc ? cb.asc(joinCar.get(field.replaceAll("car", "").toLowerCase()))
                            : cb.desc(joinCar.get(field.replaceAll("car", "").toLowerCase())));
                else
                    orders.add(asc ? cb.asc(root.get(field))
                            : cb.desc(root.get(field)));
            }
        return orders;
    }
}