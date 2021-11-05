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
import ru.itmo.soa.entity.data.PaginationData;

import javax.persistence.criteria.*;
import java.text.ParseException;
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<HumanBeing> countRoot = countQuery.from(HumanBeing.class);
            countQuery = countQuery.where(cb.lessThan(countRoot.get("weaponType").as(String.class), weaponType));
            countQuery.select(cb.count(countRoot));
            count = session.createQuery(countQuery).getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return count;
    }

    public List<HumanBeing> findHumansSoundtrackNameStarts(String soundtrackName) {
        List<HumanBeing> list = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<HumanBeing> query = session.createQuery("from HumanBeing H where H.soundtrackName like :soundtrackName");
            query.setParameter("soundtrackName", soundtrackName+'%');
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

    public int deleteAllHumanMinutesOfWaitingEqual(double minutesOfWaiting) {
        int deletedCount = 0;
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<HumanBeing> query = session.createQuery("delete from HumanBeing H where H.minutesOfWaiting = :minutesOfWaiting");
            query.setParameter("minutesOfWaiting", minutesOfWaiting);
          deletedCount = query.executeUpdate();
            session.flush();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return deletedCount;
    }



    public PaginationData getAllHumans(HumanBeingRequestParams params) throws ParseException{
        Transaction transaction = null;
        PaginationData res = new PaginationData();
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<HumanBeing> cr = cb.createQuery(HumanBeing.class);
            Root<HumanBeing> root = cr.from(HumanBeing.class);
            Join<HumanBeing, Car> join = root.join("car", JoinType.LEFT);
            Join<HumanBeing, Coordinates> joinCoordinates = root.join("coordinates");


            List<Predicate> predicates = params.getPredicates(cb, root, join, joinCoordinates);
            List<Order> orders = params.getOrders(cb,root,joinCoordinates, join);

            CriteriaQuery<HumanBeing> query = cr.select(root).where(predicates.toArray(new Predicate[0])).orderBy(orders);

            Query<HumanBeing> typedQuery = session.createQuery(query);
            typedQuery.setFirstResult(params.pageIndex*params.limit);
            typedQuery.setMaxResults(params.limit);

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<HumanBeing> countRoot = countQuery.from(HumanBeing.class);
            Join<HumanBeing, Coordinates> countCoorJoin = countRoot.join("coordinates");
            Join<HumanBeing, Car> countCarJoin = countRoot.join("car", JoinType.LEFT);

            List<Predicate> countPredicates = params.getPredicates(cb, countRoot, countCarJoin, countCoorJoin);
            countQuery = countQuery.where(countPredicates.toArray(new Predicate[0]));
            countQuery.select(cb.count(countRoot));
            Long count = session.createQuery(countQuery).getSingleResult();

            List<HumanBeing> list = typedQuery.getResultList();

            res = new PaginationData(params.limit, params.pageIndex, count, list);

            transaction.commit();
        } catch (NumberFormatException| ParseException e) {
            throw e;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return res;
    }

    public Optional<HumanBeing> getHuman(long id) {
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

    public boolean deleteHuman(long id) {
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

    public HumanBeing createHuman(HumanBeing human) {
        Transaction transaction = null;
        try (Session session = HibernateDatasource.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(human);
            transaction.commit();
            return human;
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
