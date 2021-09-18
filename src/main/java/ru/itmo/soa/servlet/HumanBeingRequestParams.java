package ru.itmo.soa.servlet;


import ru.itmo.soa.entity.Car;
import ru.itmo.soa.entity.Coordinates;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.model.Vehicle;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HumanBeingRequestParams {
    public final String name;
    public final String[] minutesOfWaiting;
    public final Boolean realHero;
    public final Boolean hasToothpick;
    public final String[] impactSpeed;
    public final String soundtrackName;
    public final String[] weaponType;
    public final String carName;
    public final String[] coordinatesX;
    public final String[] coordinatesY;
    public final String[] creationDate;
    public final String[] sort;
    public final int offset;
    public final int limit;

    HumanBeingRequestParams(
            String name,
            String[] minutesOfWaiting,
            String realHero,
            String hasToothpick,
            String[] impactSpeed,
            String soundtrackName,
            String[] weaponType,
            String carName,
            String[] coordinatesX,
            String[] coordinatesY,
            String[] creationDate,
            String[] sort,
            String offset,
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
        this.offset = offset == null ? 0 : Integer.parseInt(offset);
        this.limit = limit == null ? 5 : Integer.parseInt(limit);
    }

    private String like(String val) {
        return "%" + val + "%";
    }

    public List<javax.persistence.criteria.Predicate> getPredicates(
            CriteriaBuilder cb,
            Root<HumanBeing> root,
            Join<HumanBeing, Car> join,
            Join<HumanBeing, Coordinates> joinCoordinates
    ){
        List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
        if (name != null)
            predicates.add(cb.like(root.get("name"), like(name)));

        if (minutesOfWaiting != null)
            if (minutesOfWaiting.length > 1) {
                predicates.add(cb.ge(root.get("minutesOfWaiting"),Double.parseDouble(minutesOfWaiting[0])));
                predicates.add(cb.le(root.get("minutesOfWaiting"),Double.parseDouble(minutesOfWaiting[1])));
            } else
                predicates.add(cb.equal(root.get("minutesOfWaiting"), Double.parseDouble(minutesOfWaiting[0])));

        if (realHero != null)
            predicates.add(cb.equal(root.get("realHero"), realHero));

        if (hasToothpick != null)
            predicates.add(cb.equal(root.get("hasToothpick"), hasToothpick));

        if (impactSpeed != null)
            if (impactSpeed.length > 1) {
                predicates.add(cb.ge(root.get("impactSpeed"),Float.parseFloat(impactSpeed[0])));
                predicates.add(cb.le(root.get("impactSpeed"),Float.parseFloat(impactSpeed[1])));
            } else
                predicates.add(cb.equal(root.get("impactSpeed"), Float.parseFloat(impactSpeed[0])));

        if (soundtrackName != null)
            predicates.add(cb.like(root.get("soundtrackName"), like(soundtrackName)));

        if (weaponType != null)
            predicates.add(root.get("weaponType").as(String.class).in( weaponType));

        if (carName != null)
            predicates.add(cb.like(join.get("name"), like(carName)));

        if (coordinatesX != null)
            if (coordinatesX.length > 1) {
                predicates.add(cb.ge(joinCoordinates.get("x"),Float.parseFloat(coordinatesX[0])));
                predicates.add(cb.le(joinCoordinates.get("x"),Float.parseFloat(coordinatesX[1])));
            } else
                predicates.add(cb.equal(joinCoordinates.get("x"), Float.parseFloat(coordinatesX[0])));

        if (coordinatesY != null)
            if (coordinatesY.length > 1) {
                predicates.add(cb.ge(joinCoordinates.get("y"),Float.parseFloat(coordinatesY[0])));
                predicates.add(cb.le(joinCoordinates.get("y"),Float.parseFloat(coordinatesY[1])));
            } else
                predicates.add(cb.equal(joinCoordinates.get("y"), Float.parseFloat(coordinatesY[0])));
        if (creationDate != null) {
            try {
                if (creationDate.length > 1) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"),new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[0])));
                    predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[1])));
                } else
                    predicates.add(cb.equal(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[0])));
            }catch (ParseException e){
                System.out.println(e.getMessage());
            }
        }
        return predicates;
    }

    public List<Order> getOrders(CriteriaBuilder cb, Root<HumanBeing> root, Join<HumanBeing, Coordinates> joinCoordinates, Join<HumanBeing, Car> joinCar ) {
        List<Order> orders = new ArrayList<>();
        if (sort != null)
            for (String s : sort) {
                boolean asc = s.charAt(0) == 'a';
                String field = s.substring(1);
                if (field.startsWith("coordinates"))
                    orders.add(asc?cb.asc(joinCoordinates.get(field.replaceAll("coordinates","").toLowerCase()))
                                  :cb.desc(joinCoordinates.get(field.replaceAll("coordinates","").toLowerCase())));
                else if (field.startsWith("car"))
                    orders.add(asc?cb.asc(joinCar.get(field.replaceAll("car","").toLowerCase()))
                            :cb.desc(joinCoordinates.get(field.replaceAll("car","").toLowerCase())));
                else
                    orders.add(asc?cb.asc(root.get(field))
                            :cb.desc(root.get(field)));
            }
        return orders;
    }
}