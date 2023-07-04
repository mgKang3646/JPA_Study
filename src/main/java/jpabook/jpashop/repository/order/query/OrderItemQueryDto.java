package jpabook.jpashop.repository.order.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderItemQueryDto {

    @JsonIgnore // 화면에서 뿌리려는 DTO이므로 Json에서 조회를 제외한다.
    private Long id;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDto(Long id, String itemName, int orderPrice, int count) {
        this.id = id;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
