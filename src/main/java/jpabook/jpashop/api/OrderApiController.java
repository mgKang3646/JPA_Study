package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;


import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("api/v1/orders") // 엔티티를 노출하는 방식이라 부적절
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for( Order order : all ){
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream()
                    .forEach(o -> o.getItem().getName());
        }
        return all;
    }
    @GetMapping("api/v2/orders")
    public List<OrderDto> ordersV2(){ // DTO를 사용하였으나 N+1 문제 발생
        System.out.println("OrderApiController.ordersV2");
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    @GetMapping("api/v3/orders")
    // 쿼리는 하나가 실행되었으나 1대다 JOIN FETCH는 페이징이 불가능하다.
    //HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
    // DB에서 페이징을 못해서 모든 DB데이터를 모두 메모리로 가져온다음에 페이징을 시도한다. 이는 OOM을 유발한다.
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    @GetMapping("api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value="offset", defaultValue= "0") int offset,
            @RequestParam(value="limit", defaultValue="100") int limit
    ){
        List<Order> orders = orderRepository.findAllWithMemberDeliver(offset,limit); ;//To0NE 관계는 JOIN FETCH로 가져온다.
        return orders.stream()
                .map(OrderDto::new)
                .collect(toList());
    }

    @GetMapping("api/v4/orders") // 컬렉션 DTO 직접조회 ( N+1 문제 발생 )
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDto();
    }

    @GetMapping("api/v5/orders") //IN절 사용하여 처리 ( 쿼리 두번 나감 )
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findAllByDto_optimization();
    }
    @GetMapping("api/v6/orders") //쿼리 한 줄로 처리 , JOIN으로 인해 중복데이터가 발생=> 페이징불가능
    public List<OrderFlatDto> ordersV6(){
        return orderQueryRepository.findAllByDto_flat();// 중복데이터

//        return flats.stream()
//                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
//                                o.getName(), o.getLocalDateTime(), o.getOrderStatus(), o.getAddress()),
//                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
//                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
//                )).entrySet().stream()
//                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
//                        e.getKey().getName(), e.getKey().getLocalDateTime(), e.getKey().getOrderStatus(),
//                        e.getKey().getAddress(), e.getValue()))
//                .collect(toList());
    }

    @Getter
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime localDateTime;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems; // DTO 안에 엔티티가 있으면 엔티티가 외부로 노출된다.OrderItem도 OrderItemDto를 만들어줘야 한다.

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            localDateTime = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(OrderItemDto::new)
                    .collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
