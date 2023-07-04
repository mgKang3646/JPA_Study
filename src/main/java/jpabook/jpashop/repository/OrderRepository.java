package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    @Autowired
    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne (Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){
        String jpql = "select o From Order o join o.member m";

        return em.createQuery(jpql, Order.class)
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    }


    public List<Order> findAllWithMemberDeliver(int offset,int limit) {
        return em.createQuery(
                "SELECT o FROM Order o" +
                        " JOIN FETCH o.member m "+
                        " JOIN FETCH o.delivery d",Order.class
        )
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
    }


    public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

//    DISTINCT는 같은 ID이면 중복으로 판단하여 어플리케이션 내에서 중복제거를 한다.
//    DISTINCT는 DB에 키워드에도 날리지만 ID가 아닌 모든컬럼이 동일해야 제거가 되므로 실제 DB에서는 제거되지 않는다.
//    HIBERNATE6 이후에는 DITINCT를 붙이지 않아도 JPA에서 알아서 중복제거를 한다.
    public List<Order> findAllWithItem() {
        return em.createQuery(" SELECT DISTINCT o FROM Order o"
                + " JOIN FETCH o.member m "
                + " JOIN FETCH o.delivery d"
                + " JOIN FETCH o.orderItems oi"
                + " JOIN FETCH oi.item i", Order.class)
        .getResultList();
    }
}
