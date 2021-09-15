package ru.itmo.soa;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.itmo.soa.model.Coordinates;
import ru.itmo.soa.model.Vehicle;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MyServlet extends HttpServlet {
    private String contextPath;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        contextPath = getServletContext().getContextPath();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getPathInfo();
        Session session = HibernateSessionFactoryUtil.getSessionFactory(contextPath).openSession();
//        if (!id.isEmpty()){
//            Vehicle band = (Vehicle) session.createQuery("from Vehicle where id=:id").setParameter("id",Long.parseLong(id)).list().get(0);
//            session.close();
//            OutputStream os = resp.getOutputStream();
//            os.flush();
//            os.write(gson.toJson(band).getBytes(StandardCharsets.UTF_8));
//            os.close();
//            return;
//        }
        String[] sortData = req.getParameterValues("sort");
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Vehicle> criteriaQuery = cb.createQuery(Vehicle.class);
        Root<Vehicle> vehicle = criteriaQuery.from(Vehicle.class);
        Join<Vehicle, Coordinates> join = vehicle.join("coordinates");
        List<Order> orders = new ArrayList<>();
        if (sortData != null)
            for (String s : sortData) {
                boolean asc = s.charAt(0) == 'a';
                String field = s.substring(1);
                switch (field) {
                    case "x":
                    case "y":
                        orders.add(asc ? cb.asc(join.get(field)) : cb.desc(join.get(field)));
                        break;
                    default:
                        orders.add(asc ? cb.asc(vehicle.get(field)) : cb.desc(vehicle.get(field)));
                }
            }
        String name = req.getParameter("name");
        ArrayList<Predicate> predicates = new ArrayList<>();
        if (name != null)
            predicates.add(cb.like(vehicle.get("name"), name + "%"));
        String[] xs = req.getParameterValues("x");
        if (xs != null) {
            if (xs.length > 1) {
                if (!xs[0].isEmpty())
                    predicates.add(cb.ge(join.get("x"), Integer.parseInt(xs[0])));
                if (!xs[1].isEmpty())
                    predicates.add(cb.le(join.get("x"), Integer.parseInt(xs[1])));
            } else
                predicates.add(cb.equal(join.get("x"), Integer.parseInt(xs[0])));
        }
        String[] ys = req.getParameterValues("y");
        if (ys != null) {
            if (ys.length > 1) {
                if (!ys[0].isEmpty())
                    predicates.add(cb.ge(join.get("y"), Long.parseLong(ys[0])));
                if (!ys[1].isEmpty())
                    predicates.add(cb.le(join.get("y"), Long.parseLong(ys[1])));
            } else
                predicates.add(cb.equal(join.get("y"), Long.parseLong(ys[0])));
        }
        String[] enginePowers = req.getParameterValues("enginePower");
        if (enginePowers != null) {
            if (enginePowers.length > 1) {
                if (!enginePowers[0].isEmpty())
                    predicates.add(cb.ge(vehicle.get("enginePower"), Long.parseLong(enginePowers[0])));
                if (!enginePowers[1].isEmpty())
                    predicates.add(cb.le(vehicle.get("enginePower"), Long.parseLong(enginePowers[1])));
            } else
                predicates.add(cb.equal(vehicle.get("enginePower"), Long.parseLong(enginePowers[0])));
        }
        String[] capacities = req.getParameterValues("capacity");
        if (capacities != null) {
            if (capacities.length > 1) {
                if (!capacities[0].isEmpty())
                    predicates.add(cb.ge(vehicle.get("capacity"), Float.parseFloat(capacities[0])));
                if (!capacities[1].isEmpty())
                    predicates.add(cb.le(vehicle.get("capacity"), Float.parseFloat(capacities[1])));
            } else
                predicates.add(cb.equal(vehicle.get("capacity"), Float.parseFloat(capacities[0])));
        }
        String[] types = req.getParameterValues("type");
        if (types != null)
            predicates.add(vehicle.get("type").as(String.class).in(types));
        String[] fuelTypes = req.getParameterValues("fuelType");
        if (fuelTypes != null)
            predicates.add(vehicle.get("fuelType").as(String.class).in(fuelTypes));

        String[] dates = req.getParameterValues("date");
        if (dates != null) {
            try {
                if (dates.length > 1) {
                    if (!dates[0].isEmpty())
                        predicates.add(cb.greaterThanOrEqualTo(vehicle.get("creationDate"), new SimpleDateFormat("dd.MM.yyyy").parse(dates[0])));
                    if (!dates[1].isEmpty())
                        predicates.add(cb.lessThanOrEqualTo(vehicle.get("creationDate").as(Date.class), new SimpleDateFormat("dd.MM.yyyy").parse(dates[1])));
                } else
                    predicates.add(cb.equal(vehicle.get("creationDate").as(Date.class), new SimpleDateFormat("dd.MM.yyyy").parse(dates[0])));
            } catch (ParseException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        Predicate[] predicatesArray = new Predicate[predicates.size()];
        predicates.toArray(predicatesArray);
        criteriaQuery.where(cb.and(predicatesArray)).orderBy(orders);
        Query<Vehicle> query = session.createQuery(criteriaQuery);
        String offset = req.getParameter("offset");
        if (offset != null)
            query.setFirstResult(Integer.parseInt(offset));
        String limit = req.getParameter("limit");
        if (limit != null)
            query.setMaxResults(Integer.parseInt(limit));
        String len = session.createQuery("select count(*) from Vehicle").list().get(0).toString();
        List<Vehicle> vehicles = query.list();
        session.close();

        OutputStream os = resp.getOutputStream();

        os.flush();
        Resp r = new Resp();
        r.list = vehicles;
        r.length = len;
        os.write(gson.toJson(r).getBytes(StandardCharsets.UTF_8));
        //todo почему не добавляется в виде заголовка
        os.close();
    }

    class Resp implements Serializable {
        List<Vehicle> list;
        String length;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String requestData = req.getReader().lines().collect(Collectors.joining());
            Vehicle band = gson.fromJson(requestData, Vehicle.class);

            Session session = HibernateSessionFactoryUtil.getSessionFactory(contextPath).openSession();
            session.save(band);
            session.close();
            resp.setStatus(HttpServletResponse.SC_CREATED);
            OutputStream os = resp.getOutputStream();
            os.flush();
            os.write(gson.toJson(band).getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        String strId = req.getPathInfo().split("/")[1];
        if (strId == null || !strId.matches("^[1-9]\\d*$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            Long id = Long.parseLong(strId);
            String requestData = req.getReader().lines().collect(Collectors.joining());
            Vehicle vehicle = gson.fromJson(requestData, Vehicle.class);
            vehicle.setId(id);
            Session session = HibernateSessionFactoryUtil.getSessionFactory(contextPath).openSession();
            session.beginTransaction();
            session.update(vehicle);
            session.getTransaction().commit();
            session.close();
            OutputStream os = resp.getOutputStream();
            resp.setStatus(HttpServletResponse.SC_OK);
            os.flush();
            os.write(gson.toJson(vehicle).getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (NumberFormatException | JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String strId = req.getPathInfo().split("/")[1];
        if (!strId.matches("^[1-9]\\d*$")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            long id = Long.parseLong(strId);
            Session session = HibernateSessionFactoryUtil.getSessionFactory(contextPath).openSession();
            session.beginTransaction();
            int count = session.createQuery("delete from Vehicle where id=:id").setParameter("id", id).executeUpdate();
            session.getTransaction().commit();
            session.close();
            resp.setStatus(count == 0 ? HttpServletResponse.SC_NOT_FOUND : HttpServletResponse.SC_NO_CONTENT);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
