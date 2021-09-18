package ru.itmo.soa.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.itmo.soa.datasource.HibernateDatasource;
import ru.itmo.soa.entity.Car;
import ru.itmo.soa.entity.Coordinates;
import ru.itmo.soa.entity.HumanBeing;
import ru.itmo.soa.servlet.HumanBeingRequestParams;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HumanBeingDao {

    public HumanBeingDao() {}

    public Long countHumansWeaponTypeLess(String weaponType) {
        long count = 0;
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Long> query = session.createQuery("select count(*) from HumanBeing H where H.weaponType < :weaponType");
            query.setParameter("weaponType", weaponType);
            count = query.getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return count;
    }

    public List<HumanBeing> findHumansMinutesOfWaitingLess(long minutesOfWaiting) {
        List<HumanBeing> list = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<HumanBeing> query = session.createQuery("from HumanBeing H where H.minutesOfWaiting < :minutesOfWaiting");
            query.setParameter("minutesOfWaiting", minutesOfWaiting);
            list = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return list;
    }

    public long deleteAllHumanMinutesOfWaitingEqual(long minutesOfWaiting) {
        long deletedId = -1;
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<HumanBeing> query = session.createQuery("from HumanBeing H where H.minutesOfWaiting = :minutesOfWaiting");
            query.setParameter("minutesOfWaiting", minutesOfWaiting);
            List<HumanBeing> humans = query.getResultList();
            for(HumanBeing human: humans)
                session.delete(humans);
            session.flush();
            transaction.commit();
            if (!humans.isEmpty())
                deletedId = 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return deletedId;
    }

    @Data
    @AllArgsConstructor
    public static class PaginationResult {
        private final long totalItems;
        private final List<HumanBeing> list;
         PaginationResult() {
            totalItems = 0;
            list = new ArrayList<>();
        }
    }


    public PaginationResult getAllHumans(HumanBeingRequestParams params) {
        Transaction transaction = null;
        PaginationResult res = new PaginationResult();
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<HumanBeing> cr = cb.createQuery(HumanBeing.class);
            Root<HumanBeing> root = cr.from(HumanBeing.class);
            Join<HumanBeing, Car> join = root.join("car");
            Join<HumanBeing, Coordinates> joinCoordinates = root.join("coordinates");


            List<Predicate> predicates = params.getPredicates(cb, root, join, joinCoordinates);
            List<Order> orders = params.getOrders(cb,root,joinCoordinates, join);

            CriteriaQuery<HumanBeing> query = cr.select(root).where(predicates.toArray(new Predicate[0])).orderBy(orders);

            Query<HumanBeing> typedQuery = session.createQuery(query);
            typedQuery.setFirstResult(params.offset);
            typedQuery.setMaxResults(params.limit);

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            countQuery.select(cb.count(countQuery.from(HumanBeing.class)));
            Long count = session.createQuery(countQuery).getSingleResult();

            List<HumanBeing> list = typedQuery.getResultList();

            res = new PaginationResult(count, list);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return res;
    }

    public Optional<HumanBeing> getHuman(int id) {
        Transaction transaction = null;
        HumanBeing humanBeing = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            humanBeing = session.find(HumanBeing.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return Optional.ofNullable(humanBeing);
    }

    public boolean deleteHuman(int id) {
        Transaction transaction = null;
        boolean successful = false;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            HumanBeing humanBeing = session.find(HumanBeing.class, id);
            if (humanBeing != null) {
                session.delete(humanBeing);
                session.flush();
                successful = true;
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return successful;
    }

    public int createHuman(HumanBeing human) {
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(human);
            transaction.commit();
            return human.getId();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public void updateHuman(HumanBeing human) {
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(human);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }
}
