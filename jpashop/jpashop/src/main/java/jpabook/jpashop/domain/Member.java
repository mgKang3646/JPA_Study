package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue//시퀀스
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded// 내장타입????
    private Address address;

    // Member가 one이고 Order가 Many, Order 테이블에 있는 멤버필드에 의해서 매핑됨을 의미하고 해당 필드는 거울임뿐이다.
    // 해당 필드에 값을 넣어도 변경되지 않는다. Only 읽기만 가능
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();


}
