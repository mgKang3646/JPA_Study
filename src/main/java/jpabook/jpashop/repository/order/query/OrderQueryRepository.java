package jpabook.jpashop.repository.order.query;


import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDto() {
        List<OrderQueryDto> result = findOrders(); // xToOne 연관관계 조회
        result.forEach((o->{
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); //xToMany 연관관계 조회
            o.setOrderItems(orderItems);
        }));
        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                "SELECT new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)"
                + "FROM OrderItem oi "
                + "JOIN oi.item i "
                + "WHERE oi.order.id = :orderId",OrderItemQueryDto.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery("SELECT new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address) "
                + "FROM Order o "
                + "JOIN o.member m "
                + "JOIN o.delivery d", OrderQueryDto.class).getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));
        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                        "SELECT new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name,oi.orderPrice,oi.count)"
                                + "FROM OrderItem oi "
                                + "JOIN oi.item i "
                                + "WHERE oi.order.id in :orderIds", OrderItemQueryDto.class) // IN절 사용
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 편함을 위해 루프보다는 Map 자료구조를 사용
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                .collect(groupingBy(orderItemQueryDto -> orderItemQueryDto.getId())); // 메모리 Map에 올려놓았다가 찾아가는 원리
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                        .map(OrderQueryDto::getOrderId)
                        .collect(toList());
        return orderIds;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("SELECT new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id,m.name,o.orderDate,o.status,d.address,i.name,oi.orderPrice,oi.count) " +
                                " FROM Order o" +
                                " JOIN o.member m" +
                                " JOIN o.delivery d" +
                                " JOIN o.orderItems oi" +
                                " JOIN oi.item i",OrderFlatDto.class)
                .getResultList();

    }
}
