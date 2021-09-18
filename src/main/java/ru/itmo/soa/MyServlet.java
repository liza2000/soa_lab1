package ru.itmo.soa;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.deploy.net.HttpRequest;
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

    private  VehicleParams getParams(HttpServletRequest req){
        return new VehicleParams(
                req.getParameter("name"),
                req.getParameterValues("creationDate"),
                req.getParameterValues("enginePower"),
                req.getParameterValues("capacity"),
                req.getParameterValues("types"),
                req.getParameterValues("fuelTypes"),
                req.getParameterValues("x"),
                req.getParameterValues("y"),
                req.getParameterValues("sort"),
                req.getParameter("limit"),
                req.getParameter("offset"));
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

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Vehicle> criteriaQuery = cb.createQuery(Vehicle.class);
        Root<Vehicle> root = criteriaQuery.from(Vehicle.class);
        Join<Vehicle, Coordinates> join = root.join("coordinates");
        List<Predicate> predicates;
        VehicleParams params = getParams(req);
     try {
         predicates = params.getPredicates(cb, root, join);
     }catch (ParseException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
     }

        List<Order> orders = params.getOrders(cb, root, join);

        criteriaQuery.where(cb.and((Predicate[]) predicates.toArray())).orderBy(orders);
        Query<Vehicle> query = session.createQuery(criteriaQuery);
        String offset = params.getOffset();
        String limit = params.getLimit();
        if (offset != null)
            query.setFirstResult(Integer.parseInt(offset));
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
