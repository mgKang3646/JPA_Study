package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name ="deliver_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) // EnumType.ORDINAR 은 쓰지말자! ORDINAL은 숫자로 ENUM을 구분하기 때문에 또 다른 상태가 추가되어 숫자가 밀리면 모든 상태가 밀리게 된다. 그러니 무조건 String을 써라
    private DeliveryStatus status;

}
