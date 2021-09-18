package ru.itmo.soa;

import jakarta.servlet.http.HttpServletResponse;
import ru.itmo.soa.model.Coordinates;
import ru.itmo.soa.model.FuelType;
import ru.itmo.soa.model.Vehicle;
import ru.itmo.soa.model.VehicleType;

import javax.persistence.JoinColumn;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VehicleParams {
    
    private final String name;
    private final String[] creationDate;
    private final String[] enginePower;
    private final String[] capacity;
    private final String[] types;
    private final String[] fuelTypes;
    private final String[] x;
    private final String[] y;
    private final String[] sort;
    private final String limit;
    private final String offset;
    
    public VehicleParams(String name, String[] creationDate, String[] enginePower, String[] capacity, String[] types, String[] fuelTypes, String[] x, String[] y, String[] sort, String limit, String offset) {
        this.name = name;
        this.creationDate = creationDate;
        this.enginePower = enginePower;
        this.capacity = capacity;
        this.types = types;
        this.fuelTypes = fuelTypes;
        this.x = x;
        this.y = y;
        this.sort = sort;
        this.limit = limit;
        this.offset = offset;
    }
    
    public List<Predicate> getPredicates(CriteriaBuilder cb, Root<Vehicle> root, Join<Vehicle, Coordinates> join) throws ParseException{
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (name != null)
            predicates.add(cb.like(root.get("name"), name + "%"));
       
        if (x != null) 
            if (x.length > 1) {
                    predicates.add(cb.ge(join.get("x"),Integer.parseInt(x[0])));
                    predicates.add(cb.le(join.get("x"),Integer.parseInt(x[1])));
            } else
                predicates.add(cb.equal(join.get("x"), Integer.parseInt(x[0])));
        
        if (y != null) 
            if (y.length > 1) {
                    predicates.add(cb.ge(join.get("y"),Long.parseLong(y[0])));
                    predicates.add(cb.le(join.get("y"),Long.parseLong(y[1])));
            } else
                predicates.add(cb.equal(join.get("y"), Long.parseLong(y[0])));
        
        if (enginePower != null) 
            if (enginePower.length > 1) {
                    predicates.add(cb.ge(root.get("enginePower"),Long.parseLong(enginePower[0])));
                    predicates.add(cb.le(root.get("enginePower"), Long.parseLong(enginePower[1])));
            } else
                predicates.add(cb.equal(root.get("enginePower"),  Long.parseLong(enginePower[0])));
        
        if (capacity != null)
            if (capacity.length > 1) {
                    predicates.add(cb.ge(root.get("capacity"), Float.parseFloat(capacity[0])));
                    predicates.add(cb.le(root.get("capacity"),  Float.parseFloat(capacity[1])));
            } else
                predicates.add(cb.equal(root.get("capacity"),  Float.parseFloat(capacity[0])));
        
        if (types != null)
            predicates.add(root.get("type").as(String.class).in(types));
        if (fuelTypes != null)
            predicates.add(root.get("fuelType").as(String.class).in(fuelTypes));

//        if (creationDate != null) {
//                if (creationDate.length > 1) {
//                        predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"),new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[0])));
//                        predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[1])));
//                } else
//                    predicates.add(cb.equal(root.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(creationDate[0])));
//        }

      return   predicates;
        
    }

//    public List<Order> getOrders(CriteriaBuilder cb, Root<Vehicle> root, Join<Vehicle, Coordinates> join) {
//        List<Order> orders = new ArrayList<>();
//        if (sort != null)
//            for (String s : sort) {
//                boolean asc = s.charAt(0) == 'a';
//                String field = s.substring(1);
//                switch (field) {
//                    case "x":
//                    case "y":
//                        orders.add(asc ? cb.asc(join.get(field)) : cb.desc(join.get(field)));
//                        break;
//                    default:
//                        orders.add(asc ? cb.asc(root.get(field)) : cb.desc(root.get(field)));
//                }
//            }
//            return orders;
//    }

    public String getLimit() {
        return limit;
    }

    public String getOffset() {
        return offset;
    }
}
