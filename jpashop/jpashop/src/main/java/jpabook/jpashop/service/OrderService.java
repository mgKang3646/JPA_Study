package jpabook.jpashop.service;


import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService
{

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     *주문
     */
    @Transactional
    public Long order(Long memberId, Long ItemId,int count){

        //엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(ItemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item,item.getPrice(),count);

        //주문생성
        Order order = Order.createOrder(member,delivery,orderItem);

        //주문 저장 cascade = CascadeType.ALL여서 delivey, orderItem, Order가 persist 될때 deliveryt와OrderItem도 persist 된다???
        //Order만 deliviery와 orderItem만 참조한다. 세개의 라이프 사이클이 동일하기 때문에 Cascade를 사용하면 좋다. 다른 경우 위험하니 사용하면 안 좋다. ..
        orderRepository.save(order);

        return order.getId();
    }


    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.cancel();
        //JPA의 장점 Service로직단에서 Update쿼리문을 가진 메소드를 호출해줘야하는데 JPA가 알아서 쿼리를 날려준다.
    }

//    //검색
//    public List<Order> findOrders(OrderSearch orderSearch){
//        return orderRepository.findAll(orderSearch);
//    }
}
