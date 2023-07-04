package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    // DTO를 SELECT 하려면 new 연산자를 사용해야 한다.
    // 복잡한 join쿼리를 가지고 있도 특정화면을 의존된거라면 별도의 Repository를 분리해서 사용하는 것을 권장한다.
    // 핵심비지니스로직 라이프 사이클이랑 화면 라이플 사이클이랑 많이 다르므로 분리해주어야 한다. (?)
    public List<OrderSimpleQueryDto> findOrderDtoes() {
        return em.createQuery("SELECT new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id,m.name,o.orderDate,o.status,d.address) "
                + "FROM Order o "
                + "JOIN o.member m "
                + "JOIN o.delivery d ", OrderSimpleQueryDto.class
        ).getResultList();
    }

}
