package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import jpabook.jpashop.service.ItemService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

/*
*     성능최적화는 어떻게 할 것인가?
* Order
* Order -> Member
* Order -> Deliver
* 컬렉션이 아닌 것들.... ManyToOne OneToOne  즉 *ToOne에서의 성능최적화는 어떻게 할 것인가????
* */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    private final ItemService itemService;

    @GetMapping("api/v1/simple-orders")
    public List<Order> orderV1(){
        List<Order> all =  orderRepository.findAll(new OrderSearch()); // Order 엔티티 조회
        for (Order order : all ) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            order.getMember().setName("변경된 이름!"); // Member 엔티티 수정!!
        }
        // Member와 전혀 관련 없는 ITEM 저장 로직 수행
        Item item = new Book();
        item.setName("노인과바다");
        itemService.saveItem(item); // 트랜잭션 생성!!

        return all;
    }

    @GetMapping("api/v2/simple-orders") // 단점! 3개 테이블을 모두 조회해야 한다. ( N+1 문제 발생 )
    public List<SimpleOrderDto> ordersV2(){
        return orderRepository.findAll(new OrderSearch()).stream()
                .map(SimpleOrderDto::new)
                .collect(toList());
    }

    @GetMapping("api/v3/simple-orders") // JOIN FETCH로 N+1 문제 해결 ( 성능 최적화 )
    public List<SimpleOrderDto> ordersV3(){
        System.out.println("OrderSimpleApiController.ordersV3");
        return null;
//        return orderRepository.findAllWithMemberDeliver(offset,).stream()
//                .map(SimpleOrderDto::new)
//                .collect(toList());
    }

    @GetMapping("api/v4/simple-orders") // 화면 요구사항 SPEC에 맞는 필드만 SELECT하여 성능최적화,
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtoes(); // 재사용성이 떨어지고 특수한 화면에만 의존하므로 별도의 Repository에 분리해서 관리한다.
    }

    // v3와 v4은 우열을 가릴 수 없다. ( 트레이드 오프 )
    // v4는 화면스펙에는 최적화되어 있지만 재사용이 안된다. 딱 그 화면만 가능 + 지저분하다.
    // v3는 여러 화면에서 사용할 수 있지만 fit하지 않다.
    // 근데 대부분의 성능은 JOIN에서 발생한다. 필드가 몇개 추가된다고 성능에 영향을 크게 주지 않는다. 그러므로 v3를 추천한다.
    // 근데 20-30개 필드가 엄청 많다면 고민해봐야 한다.


    @Data // DTO를 사용하면 스펙에 맞는 데이터만 딱 출력할 수 있다. 엔티티가 수정되어도 dto가 수정되지 않으면 바뀌지 않는다.
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){ // 단점! 3개 테이블을 모두 조회해야 한다. ( N+1 문제 발생 )
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
