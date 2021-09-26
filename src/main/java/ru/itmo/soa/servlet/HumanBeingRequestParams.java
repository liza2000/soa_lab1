package ru.itmo.soa.servlet;



import lombok.SneakyThrows;
import ru.itmo.soa.entity.Car;
import ru.itmo.soa.entity.Coordinates;
import ru.itmo.soa.entity.HumanBeing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
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
    public final int pageIndex;
    public final int limit;

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
    private static final String CREATION_DATE_PARAM = "creationDate";

    private static final String SORTING_PARAM = "sort";
    private static final String PAGE_INDEX = "pageIndex";
    private static final String PAGE_SIZE_PARAM = "limit";

    HumanBeingRequestParams(HttpServletRequest request){
        this(request.getParameter(NAME_PARAM),
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
                request.getParameter(PAGE_INDEX),
                request.getParameter(PAGE_SIZE_PARAM)
        );
    }

  private   HumanBeingRequestParams(
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

   boolean isHumanBeingField(String param){
        return (NAME_PARAM.equals(param)||MINUTES_OF_WAITING_PARAM.equals(param)
        ||IMPACT_SPEED_PARAM.equals(param)||WEAPON_TYPE_PARAM.equals(param)
        ||CAR_NAME_PARAM.equals(param)||HAS_TOOTHPICK_PARAM.equals(param)||REAL_HERO_PARAM.equals(param)
        ||SOUNDTRACK_NAME_PARAM.equals(param)||COORDINATES_X_PARAM.equals(param)||COORDINATES_Y_PARAM.equals(param)
        ||CREATION_DATE_PARAM.equals(param));
   }

    public List<javax.persistence.criteria.Predicate> getPredicates(
            CriteriaBuilder cb,
            Root<HumanBeing> root,
            Join<HumanBeing, Car> join,
            Join<HumanBeing, Coordinates> joinCoordinates
    ) throws ParseException{
        List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
        if (name != null)
            predicates.add(cb.like(root.get("name"), like(name)));

        if (minutesOfWaiting != null)
            if (minutesOfWaiting.length > 1) {
                if (minutesOfWaiting[0]!=null && !minutesOfWaiting[0].isEmpty())
                    predicates.add(cb.ge(root.get("minutesOfWaiting"),Double.parseDouble(minutesOfWaiting[0])));
                if (minutesOfWaiting[1]!=null && !minutesOfWaiting[1].isEmpty())
                    predicates.add(cb.le(root.get("minutesOfWaiting"),Double.parseDouble(minutesOfWaiting[1])));
            } else
                if (minutesOfWaiting[0]!=null && !minutesOfWaiting[0].isEmpty())
                    predicates.add(cb.equal(root.get("minutesOfWaiting"), Double.parseDouble(minutesOfWaiting[0])));

        if (realHero != null)
            predicates.add(cb.equal(root.get("realHero"), realHero));

        if (hasToothpick != null)
            predicates.add(cb.equal(root.get("hasToothpick"), hasToothpick));

        if (impactSpeed != null)
            if (impactSpeed.length > 1) {
                if (impactSpeed[0]!=null && !impactSpeed[0].isEmpty())
                    predicates.add(cb.ge(root.get("impactSpeed"),Float.parseFloat(impactSpeed[0])));
                if (impactSpeed[1]!=null && !impactSpeed[1].isEmpty())
                    predicates.add(cb.le(root.get("impactSpeed"),Float.parseFloat(impactSpeed[1])));
            } else
                if (impactSpeed[0]!=null && !impactSpeed[0].isEmpty())
                    predicates.add(cb.equal(root.get("impactSpeed"), Float.parseFloat(impactSpeed[0])));

        if (soundtrackName != null)
            predicates.add(cb.like(root.get("soundtrackName"), like(soundtrackName)));

        if (weaponType != null)
            predicates.add(root.get("weaponType").as(String.class).in(weaponType));

        if (carName != null)
            predicates.add(cb.like(join.get("name"), like(carName)));

        if (coordinatesX != null)
            if (coordinatesX.length > 1) {
                if (coordinatesX[0]!=null && !coordinatesX[0].isEmpty())
                    predicates.add(cb.ge(joinCoordinates.get("x"),Float.parseFloat(coordinatesX[0])));
                if (coordinatesX[1]!=null && !coordinatesX[1].isEmpty())
                    predicates.add(cb.le(joinCoordinates.get("x"),Float.parseFloat(coordinatesX[1])));
            } else
            if (coordinatesX[0]!=null && !coordinatesX[0].isEmpty())
                    predicates.add(cb.equal(joinCoordinates.get("x"), Float.parseFloat(coordinatesX[0])));

        if (coordinatesY != null)
            if (coordinatesY.length > 1) {
                if (coordinatesY[0]!=null && !coordinatesY[0].isEmpty())
                    predicates.add(cb.ge(joinCoordinates.get("y"),Float.parseFloat(coordinatesY[0])));
                if (coordinatesY[1]!=null && !coordinatesY[1].isEmpty())
                    predicates.add(cb.le(joinCoordinates.get("y"),Float.parseFloat(coordinatesY[1])));
            } else
            if (coordinatesY[0]!=null && !coordinatesY[0].isEmpty())
                    predicates.add(cb.equal(joinCoordinates.get("y"), Float.parseFloat(coordinatesY[0])));
            if (creationDate != null)
                if (creationDate.length > 1) {
                    if (creationDate[0]!=null && !creationDate[0].isEmpty())
                      predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"),new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[0])));
                    if (creationDate[1]!=null && !creationDate[1].isEmpty())
                      predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[1])));
                } else
                    if (creationDate[0]!=null && !creationDate[0].isEmpty())
                        predicates.add(cb.equal(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[0])));

        return predicates;
    }

    public List<Order> getOrders(CriteriaBuilder cb, Root<HumanBeing> root, Join<HumanBeing, Coordinates> joinCoordinates, Join<HumanBeing, Car> joinCar ) throws ParseException{
        List<Order> orders = new ArrayList<>();
        if (sort != null)
            for (String s : sort) {
                String[] args = s.split("_",2);
                if (args.length!=2)
                    throw new ParseException("incorrect sort parameter "+s,0);
                boolean asc;
                if (args[0].equals("asc"))
                    asc=true;
                else if(args[0].equals("desc"))
                    asc = false;
                else
                    throw new ParseException("incorrect sort parameter "+s,0);
                String field = args[1];
                if (!isHumanBeingField(field))
                    throw new ParseException("incorrect sort parameter "+s,0);
                if (field.startsWith("coordinates"))
                    orders.add(asc?cb.asc(joinCoordinates.get(field.replaceAll("coordinates","").toLowerCase()))
                                  :cb.desc(joinCoordinates.get(field.replaceAll("coordinates","").toLowerCase())));
                else if (field.startsWith("car"))
                    orders.add(asc?cb.asc(joinCar.get(field.replaceAll("car","").toLowerCase()))
                                 :cb.desc(joinCar.get(field.replaceAll("car","").toLowerCase())));
                else
                    orders.add(asc?cb.asc(root.get(field))
                            :cb.desc(root.get(field)));
            }
        return orders;
    }
}